package com.news.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import com.news.basedao.HbaseDB;
import com.news.entity.Genre;
import com.news.entity.News;
import com.news.util.Constants_News;
import com.news.util.MapSortUtil;
import com.news.util.PageUtil;

@Repository("newsDAO")
public class NewsDAO {
	
	/**
	 * 添加新闻
	 * @param news
	 * @param genre
	 */
	public void addNews(News news) {
		int rowKey = HbaseDB.incr(Constants_News.HBASE_TABLE_GID, Constants_News.HBASE_ROWKEY_GID_NEWSID, Constants_News.HBASE_FAMILY_GID_GID, Constants_News.HBASE_COLUMN_GID_NEWS_ID, 1);
		news.setId(rowKey);
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_TITLE, news.getTitle());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_SOURCE, news.getSource());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_CONTENT, news.getContent());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_CONTENT_HTML, news.getContentHTML());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_URL, news.getUrl());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_PIC, news.getPic());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_DATE, news.getDate());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_COMNUM, news.getComNum());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_POPULARITY, news.getPopularity());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_UPNUM, news.getUpNum());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_SHARENUM, news.getShareNum());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId(), Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_HEAT, news.getHeat());
		Genre genre = new Genre(news.getGenreId(), news.getGenreName());
		addNewsToGenre(news, genre);

		GenreDAO genreDAO = new GenreDAO();
		genreDAO.addGenre(genre);
		genreDAO.addGenreToNews(genre, news);
	}
	
	/**
	 * 添加news表到genre表的映射
	 * @param news
	 * @param genre
	 */
	private void addNewsToGenre(News news, Genre genre) {
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId() + "_" + genre.getGenreId(), Constants_News.HBASE_FAMILY_NEWS_GENRE, Constants_News.HBASE_COLUMN_NEWS_GENRE_ID, genre.getGenreId());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, news.getId() + "_" + genre.getGenreId(), Constants_News.HBASE_FAMILY_NEWS_GENRE, Constants_News.HBASE_COLUMN_NEWS_GENRE_NAME, genre.getGenreName());
	}
	
	/**
	 * 获得新闻news信息
	 * @param id
	 * @return
	 */
	public News getNewsById(int id) {
		News news = getById(id);
		Genre genre = getGenreByNewsid(id);
		news.setGenreId(genre.getGenreId());
		news.setGenreName(genre.getGenreName());
		return news;
	}
	
	/**
	 * 根据newsid获得genre信息
	 * @param id
	 * @return
	 */
	private Genre getGenreByNewsid(int id) {
		Genre genre = null;
		Filter filter = new PrefixFilter(Bytes.toBytes(id + "_"));
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_NEWS, filter, -1, false, Constants_News.HBASE_FAMILY_NEWS_GENRE);
		Iterator<Result> iter = resultScanner.iterator();
		if(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				genre = new Genre();
				genre.setGenreId(Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_GENRE), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_GENRE_ID))));
				genre.setGenreName(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_GENRE), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_GENRE_NAME))));
			}
		}
		return genre;
	}
	
	/**
	 * 根据id获得新闻news信息
	 * @param id
	 * @return
	 */
	private News getById(int id) {
		News news = null;
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_NEWS, id, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		if(!result.isEmpty()) {
			news = new News();
			news.setId(id);
			news.setTitle(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_TITLE))));
			news.setSource(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_SOURCE))));
			news.setContent(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_CONTENT))));
			news.setContentHTML(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_CONTENT_HTML))));
			news.setUrl(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_URL))));
			news.setPic(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_PIC))));
			news.setDate(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_DATE))));
			news.setComNum(Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_COMNUM))));
			news.setPopularity(Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_POPULARITY))));
			news.setUpNum(Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_UPNUM))));
			news.setShareNum(Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_SHARENUM))));
			news.setHeat(Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_HEAT))));
		}
		return news;
	}
	

	/**
	 * 根据分类id获得新闻信息
	 * @param genreId  分类id
	 * @param pageNum  当前页码或者要转向的页码
	 * @param pageSize  每页多少条数据
	 * @return
	 */
	public PageUtil<Map<String, Object>> getNewsListByGenreId(int genreId, int pageNum, int pageSize) {
		int newsCount = getNewsCount(genreId);
		PageUtil<Map<String, Object>> page = new PageUtil<Map<String, Object>>(String.valueOf(pageNum), String.valueOf(pageSize), newsCount);
		List<News> newsListAll = new ArrayList<News>();
		List<News> newsList = new ArrayList<News>();
		Filter filter = new PrefixFilter(Bytes.toBytes(genreId + "_"));
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_GENRE, filter, -1, true, Constants_News.HBASE_FAMILY_GENRE_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsid = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_GENRE_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_GENRE_NEWS_ID)));
				if (newsid > 0) {
					newsListAll.add(getNewsById(newsid));
				}
			}
		}
		for (int i = (pageNum - 1) * pageSize; i < pageNum * pageSize; i++) {
			if (i < newsListAll.size()) {
				newsList.add(newsListAll.get(i));
			}
		}
		page.setList(newsList);
		return page;
	}
	
	/**
	 * 获得新闻最新信息
	 * @param pageNum  当前页码或者要转向的页码
	 * @param pageSize  每页多少条数据
	 * @return
	 */
	public PageUtil<Map<String, Object>> getNewsList(int pageNum, int pageSize) {
		int newsCount = getNewsCount(-1);
		PageUtil<Map<String, Object>> page = new PageUtil<Map<String, Object>>(String.valueOf(pageNum), String.valueOf(pageSize), newsCount);
		List<News> newsList = new ArrayList<News>();
		Filter filter = new PageFilter(pageSize);
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_NEWS, filter, newsCount - (pageNum - 1) * pageSize, true, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsid = Bytes.toInt(result.getRow());
				if (newsid > 0) {
					newsList.add(getNewsById(newsid));
				}
			}
		}
		page.setList(newsList);
		return page;
	}
	
	/**
	 * 根据分类genreid获得新闻总条数，当genreid为0时，获得所有新闻。
	 * @param genreId
	 * @return
	 */
	private int getNewsCount(int genreId) {
		int newsCount = 0;
		Filter filter = null;
		if (genreId != -1) {
			filter = new PrefixFilter(Bytes.toBytes(genreId + "_"));
		}
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_GENRE, filter, -1, false, Constants_News.HBASE_FAMILY_GENRE_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				newsCount++;
			}
		}
		return newsCount;
	}
	
	/**
	 * 获得最新的几条新闻breakingNews
	 * @param newsNum
	 * @return
	 */
	public List<News> getLatestNews(int newsNum){
		List<News> breakingNewsList = new ArrayList<News>();
		Filter filter = new PageFilter(newsNum);
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_NEWS, filter, -1, true, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsid = Bytes.toInt(result.getRow());
				if (newsid > 0) {
					breakingNewsList.add(getNewsById(newsid));
				}
			}
		}
		return breakingNewsList;
	}
	
	/**
	 * 每访问一次新闻浏览量增加1
	 * @param newsId
	 */
	public void incrPopularity(int newsId) {
		News news = getById(newsId);
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, newsId, Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_POPULARITY, news.getPopularity() + 1);
	}
	
	/**
	 * 每评论一次新闻评论量增加1
	 * @param newsId
	 */
	public void incrCommentNum(int newsId) {
		News news = getById(newsId);
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, newsId, Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_COMNUM, news.getComNum() + 1);
	}
	
	/**
	 * 每点赞一次新闻点赞量增加1
	 * @param newsId
	 */
	public void incrPraiseNum(int newsId) {
		News news = getById(newsId);
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, newsId, Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_UPNUM, news.getUpNum() + 1);
	}
	
	/**
	 * 每分享一次新闻分享量增加1
	 * @param newsId
	 */
	public void incrShareNum(int newsId) {
		News news = getById(newsId);
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, newsId, Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_COLUMN_NEWS_SHARENUM, news.getShareNum() + 1);
	}
	
	/**
	 * 获得点赞量最多的新闻
	 * @param newsNum
	 * @return
	 */
	public List<News> getPraiseNews(int newsNum){
		List<News> praiseNewsList = new ArrayList<News>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		ResultScanner resultScanner = HbaseDB.getResultScanner(Constants_News.HBASE_TABLE_NEWS, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsid = Bytes.toInt(result.getRow());
				if (newsid > 0) {
					int praiseNum = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_UPNUM)));
					map.put(newsid, praiseNum);
				}
			}
		}
		List<Integer> newsIds = MapSortUtil.mapSortByValue(map, newsNum);
		for (Integer newsId : newsIds) {
			praiseNewsList.add(getNewsById(newsId));
		}
		return praiseNewsList;
	}
	
	/**
	 * 获取热门浏览新闻
	 * @param newsNum
	 * @return
	 */
	public List<News> getPopularNews(int newsNum){
		List<News> popularNewsList = new ArrayList<News>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		ResultScanner resultScanner = HbaseDB.getResultScanner(Constants_News.HBASE_TABLE_NEWS, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsid = Bytes.toInt(result.getRow());
				if (newsid > 0) {
					int popularity = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_POPULARITY)));
					map.put(newsid, popularity);
				}
			}
		}
		List<Integer> newsIds = MapSortUtil.mapSortByValue(map, newsNum);
		for (Integer newsId : newsIds) {
			popularNewsList.add(getNewsById(newsId));
		}
		return popularNewsList;
	}
	
	/**
	 * 获取分享量最多的新闻
	 * @param newsNum
	 * @return
	 */
	public List<News> getShareNews(int newsNum){
		List<News> shareNewsList = new ArrayList<News>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		ResultScanner resultScanner = HbaseDB.getResultScanner(Constants_News.HBASE_TABLE_NEWS, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsid = Bytes.toInt(result.getRow());
				if (newsid > 0) {
					int shareNum = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_SHARENUM)));
					map.put(newsid, shareNum);
				}
			}
		}
		List<Integer> newsIds = MapSortUtil.mapSortByValue(map, newsNum);
		for (Integer newsId : newsIds) {
			shareNewsList.add(getNewsById(newsId));
		}
		return shareNewsList;
	}
	/*
	 * 根据热度和时间的新闻推荐
	 * 
	 * */
	public List<News> getHeatNews(int newsNum){
		List<News> heatNewsList = new ArrayList<News>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		ResultScanner resultScanner = HbaseDB.getResultScanner(Constants_News.HBASE_TABLE_NEWS, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsid = Bytes.toInt(result.getRow());
				if (newsid > 0) {
					int heat = (int)(Bytes.toDouble(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_HEAT)))*100);//保留小数点后两位
					map.put(newsid, heat);
					System.out.println(newsid+":"+heat);
				}
			}
		}
		List<Integer> newsIds = MapSortUtil.mapSortByValue(map, newsNum);
		for (Integer newsId : newsIds) {
			heatNewsList.add(getNewsById(newsId));
		}
		return heatNewsList;
	}
	
}