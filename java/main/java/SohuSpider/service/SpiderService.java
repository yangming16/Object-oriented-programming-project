package main.java.SohuSpider.service;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.news.basedao.HbaseDB;
import com.news.dao.NewsDAO;
import com.news.entity.*;
import com.news.util.Constants;
import com.news.util.Constants_News;
import com.news.util.Esutil;

import main.java.SohuSpider.filter.BloomFilter;
import main.java.SohuSpider.util.ConstantJudge;
import main.java.SohuSpider.util.DBStatement;
import recommend.PutHeat;

import static main.java.SohuSpider.util.XmlUtils.getAllChannels;
import static main.java.SohuSpider.util.JSoupUtils.getDocument;
import static main.java.SohuSpider.util.JsonUtils.parseRestContent;
import static main.java.SohuSpider.util.XmlUtils.writeEntryUrls;
import static main.java.SohuSpider.util.XmlUtils.loadEntryUrls;

public class SpiderService implements Serializable {

	// 使用BloomFilter算法去重
	static BloomFilter filter = new BloomFilter();

	// url阻塞队列
	BlockingQueue<String> urlQueue = null;

	// 数据库连接
	static Connection con = DBStatement.getCon();

	static Statement stmt = DBStatement.getInstance();

	static PreparedStatement ps = null;

	// 线程池
	static Executor executor = Executors.newFixedThreadPool(20);

	static String urlHost = "http://m.sohu.com";
	
	static String urlRecord = "";

	// 导航页面url
	static String urlNavigation = "https://m.sohu.com/c/395/?_once_=000025_zhitongche_daohang";

	// 爬取深度
	static int DEFAULT_DEPTH = 10;

	static int DEFAULT_THREAD_NUM = 10;

	// 浏览量随机数生成
	int randomPopMax = 100000;
	int randomPopMin = 1000;

	// 点赞量随机生成
	int randomUpMax = 5000;
	int randomUpMin = 100;

	// 分享量随机生成
	int randomShareMax = 1000;
	int randomShareMin = 10;
	
	static PutHeat putheat;

	// 上传新闻到hbase数据库中
	public static void loadNews(News news) {
		NewsDAO newsDAO = new NewsDAO();
		newsDAO.addNews(news);
		Esutil.addIndex(Constants.ESIndex, Constants.ESType, news);
		int newsId = HbaseDB.incr(Constants_News.HBASE_TABLE_GID, Constants_News.HBASE_ROWKEY_GID_NEWSID, Constants_News.HBASE_FAMILY_GID_GID, Constants_News.HBASE_COLUMN_GID_NEWS_ID, 0);
		//putheat.Gheat(newsId);
	}

	public void start() throws InterruptedException {

		File urlsSer = new File("urlQueue.ser");
		if (urlsSer.exists()) {

			try {
				// 对象反序列化
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(urlsSer));
				urlQueue = (BlockingQueue<String>) ois.readObject();

				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 创建阻塞队列
			urlQueue = new LinkedBlockingQueue<String>();

			// 获取入口Url
			List<String> urlChannels = genEntryChannel(urlNavigation);

			for (String url : urlChannels) {
				urlQueue.add(url);
				
			}
		}

		// 添加程序监听结束,程序结束时候应序列化两个重要对象--urlQueue和filter
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				//System.out.println(urlQueue.isEmpty());
				try {
					if (urlQueue.isEmpty() == false) {
						// 序列化urlQueue
						File file = new File("urlQueue.ser");
						file.createNewFile();
						ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("urlQueue.ser"));
						os.writeObject(urlQueue);
						os.close();
					}

					// 序列化bits
					File file = new File("bits.ser");
					file.createNewFile();
					
					//System.out.println("1");
					ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("bits.ser"));
					//System.out.println("2");
					os.writeObject(filter.getBitset());
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}));

		for (int i = 0; i < DEFAULT_THREAD_NUM; i++) {
			Thread a = new Thread(new Runnable() {

				public void run() {
					while (true) {
						String url = getAUrl();
						if (!filter.contains(url)) {
							filter.add(url);
							System.out.println(Thread.currentThread().getName() + "正在爬取url:" + url);
							if (url != null) {
								crawler(url);
							}
						} else {
							System.out.println("此url存在，不爬了." + url);
						}
					}

				}

			});
			executor.execute(a);
		}

		// 线程池监视线程
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						if (((ThreadPoolExecutor) executor).getActiveCount() < 10) {
							Thread a = new Thread(new Runnable() {
								public void run() {
									while (true) {
										String url = getAUrl();
										if (!filter.contains(url)) {
											filter.add(url);
											System.out.println(Thread.currentThread().getName() + "正在爬取url:" + url);
											if (url != null) {
												crawler(url);
											}
										} else {
											//System.out.println("此url存在， 不爬了." + url);
										}
									}
								}
							});
							executor.execute(a);
							if (urlQueue.size() == 0) {
								System.out.println("队列为0了！！！！！！！");
							}
						}
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		}).start();

	}

	/* 从导航页解析入口新闻url */
	public static List<String> genEntryChannel(String startUrl) {

		List<String> urlArray = new ArrayList<String>();
		// 小说类别的url不需要，其url特征是含有单词read
		String pattern = "^/c.*";

		Document doc = getDocument(startUrl);
		Elements Urls = doc.select("a.h3Sub");
		for (Element url : Urls) {
			String link = url.attr("href");
			if (Pattern.matches(pattern, link) == true) {
				urlArray.add(urlHost + link);
			}
		}

		writeEntryUrls(urlArray);
		return urlArray;
	}

	/* 爬取新闻网页 */
	public void crawler(String url) {

		Document doc = getDocument(url); // 返回的Document对象一定是正确的

		String pattern = ".*/n/[0-9]+/.*";
		System.out.println(Pattern.matches(pattern, url));
		if (Pattern.matches(pattern, url)) {

			String title = "";
			String category = "";
			String cateId = "";
			String sourceFrom = "";
			String date = "";
			String content = "";
			String picURL = "";
			// String editor = null;
			String contentHTML = "";
			
			
			//当前日期
			int date0=0;
			int date1=0;
			
			Calendar now = Calendar.getInstance(); 

			// 产生浏览量、点赞数、分享量、评论数
			Random random = new Random();
			int popularity = random.nextInt(randomPopMax) % (randomPopMax - randomPopMin + 1) + randomPopMin;
			int upNum = random.nextInt(randomUpMax) % (randomUpMax - randomUpMin + 1) + randomUpMin;
			int shareNum = random.nextInt(randomShareMax) % (randomShareMax - randomShareMin + 1) + randomShareMin;
			int comNum = 0;
			int heat = 5;

			News news = new News();
			news.toString();
			news.setUrl(url);
			
			String urlTemp = url;
		    urlTemp = urlTemp.replace("http://m.sohu.com/n/", "");
		    urlTemp = urlTemp.replace("/", "");
		    ConstantJudge cj = new ConstantJudge();
		    try {
				if(cj.findStringInFile(urlTemp) == true){
					//System.out.println(urlTemp);
					//System.out.println("找到重复路径!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					return;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			news.setPopularity(popularity);
			news.setUpNum(upNum);
			news.setShareNum(shareNum);
			news.setComNum(comNum);
			news.setHeat(heat);

			try {
				/**
				 * 新闻标题格式 题目-类别-手机搜狐 但是有些题目中本身就含有 "-"
				 */
				String[] temp = doc.title().trim().split("-");
				// System.out.println("题目格式"+temp);
				category = temp[temp.length - 2].substring(0, 2);
				for (int i = 0; i < temp.length - 2; i++) {
					title += temp[i];
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				// e.printStackTrace();
				return;
			}

			news.setGenreName(category);
			news.setTitle(title);
			
//			String urlTemp = title;
////			urlTemp = urlTemp.replace("http://m.sohu.com/n/", "");
////			urlTemp = urlTemp.replace("/", "");
//			System.out.println("文章url"+urlRecord);
//			if (urlRecord.indexOf(urlTemp) != -1){
//				System.out.println("爬取文章错误，自动忽略!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//				return;
//			}
//			urlRecord = urlRecord + urlTemp;
			

			Elements cateidInfo = doc.body().select("header a.title");
			if (cateidInfo.isEmpty() == false) {
				String temp = cateidInfo.attr("href");
				temp = temp.replace("/c/", "");
				cateId = temp.substring(0, temp.indexOf("/"));
				//System.out.println(cateId);
				int i = Integer.valueOf(cateId).intValue();
				if(i==20)
				{
					return;
				}
				news.setGenreId(i);
			}

			Elements articleInfo = doc.body().select("div.article-info");
			if ( articleInfo.isEmpty() == false) {
				try{
					String[] temp = articleInfo.first().text().split(" ");
					sourceFrom = temp[0];
					date = temp[1];
					
					String temp0="";
					
					temp0 = date.substring(0, 2);
					date0 = Integer.parseInt(temp0);
					
					temp0 = date.substring(3,5);
					date1 = Integer.parseInt(temp0);
//					System.out.println("11111111111111111111111111111111111111111111111"+temp0);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					return ;
				}
			}
			news.setSource(sourceFrom);
			news.setDate(date);
			
			if (date.indexOf(":") != -1){
				System.out.println("爬取文章错误，自动忽略");
				return;
			}
			if (date == ""){
				System.out.println("爬取文章错误，自动忽略");
				return;
			}

			Elements paras = doc.body().select("article p");
			if (paras.isEmpty() == false) {
				for (Element e : paras) {
					content += e.text();
					content += "\n";
				}
			}
			news.setContent(content);
			if (content.length() > 8000) {
				return;
			}

			// Elements img = doc.body().select("div.media-wrapper");
			// if ( img.isEmpty() == false) {
			// for (Element e : img) {
			// contentHTML += e;
			// contentHTML += "<br/>";
			// }
			// }
			// if(doc.body().select("h1").remove("h1")){
			// System.out.println("删除标题成功");
			// }
			// if(doc.body().select("article").remove("div.article-info")){
			// System.out.println("删除文章信息成功");
			// }

			Elements pic = doc.body().select("article img");
			if (pic.isEmpty() == false) {
				for (Element e : pic) {
					picURL = e.attr("src");
					picURL = picURL.replace("//", "http://");
					// System.out.println("picURL:"+picURL);
					break;
				}
			}
			news.setPic(picURL);

			Elements cTHML = doc.body().select("article");
			if (cTHML.isEmpty() == false) {
				for (Element e : cTHML) {
					contentHTML += e;
					contentHTML += "<br/>";
				}
			}

			// 对新闻的HTML进行修改，使网页更加具有可读性
			contentHTML = contentHTML.replace("class=\"h1\"", "class=\"h1\" hidden");
			contentHTML = contentHTML.replace("class=\"article-info clearfix\"",
					"class=\"article-info clearfix\" hidden");
			contentHTML = contentHTML.replace("para", "artext");
			contentHTML = contentHTML.replace("href=\"javascript:;\"", "href=\"javascript:;\" hidden");
			contentHTML = contentHTML.replace("display: none", "display: inline");
			contentHTML = contentHTML.replace("class=\"image\"", "class=\"image\" align=\"center\"");
			contentHTML = contentHTML.replace("a href", "a hidden href");
			contentHTML = contentHTML.replace("class=\"media-info\"", "class=\"media-info\" hidden");

			if (contentHTML.indexOf("img src=\"//") != -1) {
				contentHTML = contentHTML.replace("img src=\"//", "img src=\"http://");
			}

			news.setContentHTML(contentHTML);

			if (contentHTML.length() > 400000) {
				return;
			}

			// Elements divEditor = doc.body().select("div.editor");
			// if (divEditor.isEmpty() == false) {
			// editor = divEditor.first().text();
			// }
			// news.setEditor(editor);

			// 打印用户信息
			System.out.println("爬取成功：" + news);

			//if(now.get(Calendar.MONTH) - 1 <= date0 && date0 <= now.get(Calendar.MONTH) + 1 && date1 <= now.get(Calendar.DAY_OF_MONTH)){
        	//if(date0 == now.get(Calendar.MONTH) + 1 && date1 == now.get(Calendar.DAY_OF_MONTH)){
				// 向mysql数据库存放信息
//			{
//				loadNews(news);
//				
				
				
				String sql = "insert into news_info "
						+ "(title,url,cate,cateId,date,srcFrom,content,contentHTML,popularity,upNum,shareNum,comNum,heat) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
				try {
					ps = con.prepareStatement(sql, Statement.SUCCESS_NO_INFO);
					ps.setString(1, news.getTitle());
					ps.setString(2, news.getUrl());
					ps.setString(3, news.getGenreName());
					ps.setInt(4, news.getGenreId());
					ps.setString(5, news.getDate());
					ps.setString(6, news.getSource());
					ps.setString(7, news.getContent());
					// ps.setString(8, news.getPic());
					ps.setString(8, news.getContentHTML());
					ps.setInt(9, news.getPopularity());
					ps.setInt(10, news.getUpNum());
					ps.setInt(11, news.getShareNum());
					ps.setInt(12, news.getComNum());
					ps.setInt(13, news.getHeat());
					// ps.setString(10, news.getEditor());

					// 存储news
					ps.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
//			}

		}

		// 新闻正文url的特征 https://m.sohu.com/n/488483157/
		Elements urlCandidates = doc.body().select("a[href~=(.*/n/[0-9]+/)|(.*/c.*)]");
		for (Element e : urlCandidates) {
			url = urlHost + e.attr("href");
			try {
				urlQueue.put(url);
			} catch (InterruptedException e1) {

				e1.printStackTrace();
			}
		}

	}

	public String getAUrl() {
		String tmpAUrl;
		try {
			tmpAUrl = urlQueue.take();
			return tmpAUrl;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
