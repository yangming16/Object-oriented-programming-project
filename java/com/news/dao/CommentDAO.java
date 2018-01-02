package com.news.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import com.news.basedao.HbaseDB;
import com.news.entity.Comment;
import com.news.util.Constants_News;

@Repository("commentDAO")
public class CommentDAO {
	
	/**
	 * 添加评论
	 * @param comment
	 * @param newsId
	 * @param userId
	 */
	public void addComment(Comment comment, int newsId) {
		int rowKey = HbaseDB.incr(Constants_News.HBASE_TABLE_GID, Constants_News.HBASE_ROWKEY_GID_COMMENTID, Constants_News.HBASE_FAMILY_GID_GID, Constants_News.HBASE_COLUMN_GID_COMMENT_ID, 1);
		comment.setId(rowKey);
		HbaseDB.put(Constants_News.HBASE_TABLE_COMMENT, rowKey, Constants_News.HBASE_FAMILY_COMMENT_COMMENT, Constants_News.HBASE_COLUMN_COMMENT_USERID, comment.getUserId());
		HbaseDB.put(Constants_News.HBASE_TABLE_COMMENT, rowKey, Constants_News.HBASE_FAMILY_COMMENT_COMMENT, Constants_News.HBASE_COLUMN_COMMENT_USERNAME, comment.getUsername());
		HbaseDB.put(Constants_News.HBASE_TABLE_COMMENT, rowKey, Constants_News.HBASE_FAMILY_COMMENT_COMMENT, Constants_News.HBASE_COLUMN_COMMENT_CONTENT, comment.getContent());
		HbaseDB.put(Constants_News.HBASE_TABLE_COMMENT, rowKey, Constants_News.HBASE_FAMILY_COMMENT_COMMENT, Constants_News.HBASE_COLUMN_COMMENT_DATE, comment.getDate());
		HbaseDB.put(Constants_News.HBASE_TABLE_NEWS, newsId + "_" + rowKey, Constants_News.HBASE_FAMILY_NEWS_COMMENT, Constants_News.HBASE_COLUMN_NEWS_COMMENT_ID, rowKey);
	}
	
	/**
	 * 获得评论列表
	 * @param newsId
	 * @return
	 */
	public List<Comment> getCommentList(int newsId){
		List<Comment> commentList = new ArrayList<Comment>();
		Filter filter = new PrefixFilter(Bytes.toBytes(newsId + "_"));
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_NEWS, filter, -1, true, Constants_News.HBASE_FAMILY_NEWS_COMMENT);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				int commentId = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_NEWS_COMMENT), Bytes.toBytes(Constants_News.HBASE_COLUMN_NEWS_COMMENT_ID)));
				if (commentId > 0) {
					commentList.add(getById(commentId));
				}
			}
		}
		return commentList;
	}
	
	/**
	 * 获得评论信息
	 * @param id
	 * @return
	 */
	private Comment getById(int id) {
		Comment comment = null;
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_COMMENT, id, Constants_News.HBASE_FAMILY_COMMENT_COMMENT);
		if(!result.isEmpty()) {
			comment = new Comment();
			comment.setId(id);
			comment.setUserId(Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_COMMENT_COMMENT), Bytes.toBytes(Constants_News.HBASE_COLUMN_COMMENT_USERID))));
			comment.setUsername(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_COMMENT_COMMENT), Bytes.toBytes(Constants_News.HBASE_COLUMN_COMMENT_USERNAME))));
			comment.setContent(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_COMMENT_COMMENT), Bytes.toBytes(Constants_News.HBASE_COLUMN_COMMENT_CONTENT))));
			comment.setDate(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_COMMENT_COMMENT), Bytes.toBytes(Constants_News.HBASE_COLUMN_COMMENT_DATE))));
		}
		return comment;
	}
	
}