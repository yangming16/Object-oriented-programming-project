package com.news.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import com.news.basedao.HbaseDB;
import com.news.entity.Activity;
import com.news.entity.News;
import com.news.entity.User;
import com.news.pojo.ActivityType;
import com.news.util.Constants;
import com.news.util.Constants_News;

@Repository("activityDAO")
public class ActivityDAO {
	
	/**
	 * 添加用户操作记录
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		int rowKey = HbaseDB.incr(Constants_News.HBASE_TABLE_GID, Constants_News.HBASE_ROWKEY_GID_ACTIVITYID, Constants_News.HBASE_FAMILY_GID_GID, Constants_News.HBASE_COLUMN_GID_ACTIVITY_ID, 1);
		HbaseDB.put(Constants_News.HBASE_TABLE_ACTIVITY, rowKey, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY, Constants_News.HBASE_COLUMN_ACTIVITY_USER_ID, activity.getUserId());
		HbaseDB.put(Constants_News.HBASE_TABLE_ACTIVITY, rowKey, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY, Constants_News.HBASE_COLUMN_ACTIVITY_NEWS_ID, activity.getNewsId());
		HbaseDB.put(Constants_News.HBASE_TABLE_ACTIVITY, rowKey, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY, Constants_News.HBASE_COLUMN_ACTIVITY_GENRE_ID, activity.getGenreId());
		HbaseDB.put(Constants_News.HBASE_TABLE_ACTIVITY, rowKey, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY, Constants_News.HBASE_COLUMN_ACTIVITY_ACTIVITY, activity.getActivity().getValue());
		HbaseDB.put(Constants_News.HBASE_TABLE_ACTIVITY, rowKey, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY, Constants_News.HBASE_COLUMN_ACTIVITY_TIME, activity.getTime());
	}
	
	/**
	 * 获得用户最近浏览记录
	 * @param userId
	 * @return
	 */
	public List<News> getUserBrowseRecordList(int userId){
		List<Integer> newsIdList = new ArrayList<Integer>();
		NewsDAO newsDAO = new NewsDAO();
		List<News> browserNewsList = new ArrayList<News>();
		// 第一个过滤是过滤用户
		Filter filter1 = new SingleColumnValueFilter(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_USER_ID), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(userId));
		// 第二个过滤是过滤activity的类型
		Filter filter2 = new SingleColumnValueFilter(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_ACTIVITY), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(ActivityType.BROWSED_NEWS.getValue()));
		// 第三个过滤是过滤数据条数
		Filter filter3 = new PageFilter(Constants.recentBrowserNewsNum);
		FilterList filterList = new FilterList(filter1, filter2, filter3);
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilterList(Constants_News.HBASE_TABLE_ACTIVITY, filterList, -1, true, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsId = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_NEWS_ID)));
				if ((newsId > 0)&&(!newsIdList.contains(newsId))) {
					newsIdList.add(newsId);
					browserNewsList.add(newsDAO.getNewsById(newsId));
				}
			}
		}
		return browserNewsList;
	}
	
	/**
	 * 获得热门搜索记录
	 * @return
	 */
	public List<News> getActiveSearchRecordList(){
		List<Integer> newsIdList = new ArrayList<Integer>();
		NewsDAO newsDAO = new NewsDAO();
		List<News> searchNewsList = new ArrayList<News>();
		// 第一个过滤是过滤activity的类型
		Filter filter1 = new SingleColumnValueFilter(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_ACTIVITY), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(ActivityType.SEARCH_NEWS.getValue()));
		// 第二个过滤是过滤数据条数
		Filter filter2 = new PageFilter(Constants.activeSearchNewsNum);
		FilterList filterList = new FilterList(filter1, filter2);
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilterList(Constants_News.HBASE_TABLE_ACTIVITY, filterList, -1, true, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int newsId = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_NEWS_ID)));
				if ((newsId > 0)&&(!newsIdList.contains(newsId))) {
					newsIdList.add(newsId);
					searchNewsList.add(newsDAO.getNewsById(newsId));
				}
			}
		}
		return searchNewsList;
	}
	
	/**
	 * 获得用户注册记录
	 * @return
	 */
	public List<User> getRegUserRecordList(){
		UserDAO userDAO = new UserDAO();
		List<User> regUserList = new ArrayList<User>();
		// 第一个过滤是过滤activity的类型
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_ACTIVITY), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(ActivityType.REG.getValue()));
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_ACTIVITY, filter, -1, true, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int userId = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_USER_ID)));
				if (userId > 0) {
					regUserList.add(userDAO.getById(userId));
				}
			}
		}
		return regUserList;
	}
	
	/**
	 * 获得用户登录记录
	 * @return
	 */
	public List<User> getLoginUserRecordList(){
		UserDAO userDAO = new UserDAO();
		List<User> loginUserList = new ArrayList<User>();
		// 第一个过滤是过滤activity的类型
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_ACTIVITY), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(ActivityType.LOGIN.getValue()));
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_ACTIVITY, filter, -1, true, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int userId = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_USER_ID)));
				if (userId > 0) {
					loginUserList.add(userDAO.getById(userId));
				}
			}
		}
		return loginUserList;
	}
	
	/**
	 * 获得用户登出记录
	 * @return
	 */
	public List<User> getLogoutUserRecordList(){
		UserDAO userDAO = new UserDAO();
		List<User> logoutUserList = new ArrayList<User>();
		// 第一个过滤是过滤activity的类型
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_ACTIVITY), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(ActivityType.LOGOUT.getValue()));
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_ACTIVITY, filter, -1, true, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int userId = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_USER_ID)));
				if (userId > 0) {
					logoutUserList.add(userDAO.getById(userId));
				}
			}
		}
		return logoutUserList;
	}
	
}