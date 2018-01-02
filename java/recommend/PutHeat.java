package recommend;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.news.basedao.HbaseDB;
import com.news.util.Constants_News;

public class PutHeat {
	/*
	 * P = 文章获得的热度(由总分计算)
	 * T = 从文章提交至今的时间(天)
	 * G = 比重，缺省值是1.8
	 * r=(P – 1) / (t + 2)^1.8
	 * 
	 * */
	
	/*
	 * 浏览1分，点赞2分，分享3分，评论4分
	 * */
	public static int getP(int newsId){
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_NEWS, newsId, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		int heat=0;
		if(!result.isEmpty()){
			int browse,up,share,comment;
			browse = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_POPULARITY)));
			up = 2*Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_UPNUM)));
			share = 3*Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_SHARENUM)));
			comment = 4*Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_COMNUM)));
			heat = browse+up+share+comment;
		}
		return heat;
	}
	/*
	 * ���·��������ڵ�����
	 * 
	 * */
	public static int getT(int newsId){
		int time=1000;
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_NEWS, newsId, Constants_News.HBASE_FAMILY_NEWS_NEWS);
		if(!result.isEmpty()){
			String newsDate = Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_DATE)));
			time = DateTime.CalculateTime(newsDate);
		}
		return time;
	}
	public static void main(String[] args) {
		double G = 1.8;
		double r =  0;
		int rowkey = HbaseDB.incr(Constants_News.HBASE_TABLE_GID, Constants_News.HBASE_ROWKEY_GID_NEWSID, Constants_News.HBASE_FAMILY_GID_GID, Constants_News.HBASE_COLUMN_GID_NEWS_ID, 0);
		System.out.println(rowkey);
		for(int newsId = rowkey;newsId>0;newsId--){
			int P = getP(newsId)-1;
			int t = getT(newsId)+2;
			double under = Math.pow(t, G);
			r = P/under;
			System.out.println(newsId+":"+r);
			try {
				Table table = HbaseDB.getConn().getTable(TableName.valueOf(Constants_News.HBASE_TABLE_NEWS));
				Put put = new Put(Bytes.toBytes(newsId));
				put.addColumn(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_HEAT), Bytes.toBytes(r));
				table.put(put);
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void Gheat(int newsId){
		double G = 1.8;
		double r =  0;
		//int newsId = HbaseDB.incr(Constants_News.HBASE_TABLE_GID, Constants_News.HBASE_ROWKEY_GID_NEWSID, Constants_News.HBASE_FAMILY_GID_GID, Constants_News.HBASE_COLUMN_GID_NEWS_ID, 0);
	    //for(int newsId = rowkey;newsId>0;newsId--){
			int P = getP(newsId)-1;
			int t = getT(newsId)+2;
			double under = Math.pow(t, G);
			r = P/under;
			try {
				Table table = HbaseDB.getConn().getTable(TableName.valueOf(Constants_News.HBASE_TABLE_NEWS));
				Put put = new Put(Bytes.toBytes(newsId));
				put.addColumn(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_NEWS), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_HEAT), Bytes.toBytes(r));
				table.put(put);
				table.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}
	}
}
