package com.news.dao;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import com.news.basedao.HbaseDB;
import com.news.entity.User;
import com.news.util.Constants_News;
import com.news.util.MD5Util;

@Repository("userDao")
public class UserDAO {
	
	/**
	 * 注册用户
	 * @param user
	 */
	public void addUser(User user) {
		int rowKey = HbaseDB.incr(Constants_News.HBASE_TABLE_GID, Constants_News.HBASE_ROWKEY_GID_USERID, Constants_News.HBASE_FAMILY_GID_GID, Constants_News.HBASE_COLUMN_GID_USER_ID, 1);
		HbaseDB.put(Constants_News.HBASE_TABLE_USER, rowKey, Constants_News.HBASE_FAMILY_USER_INFO, Constants_News.HBASE_COLUMN_USER_INFO_NAME, user.getName());
		HbaseDB.put(Constants_News.HBASE_TABLE_USER, rowKey, Constants_News.HBASE_FAMILY_USER_INFO, Constants_News.HBASE_COLUMN_USER_INFO_PWD, MD5Util.encodePwd(user.getPwd()));
		HbaseDB.put(Constants_News.HBASE_TABLE_USER, rowKey, Constants_News.HBASE_FAMILY_USER_INFO, Constants_News.HBASE_COLUMN_USER_INFO_EMAIL, user.getEmail());
		HbaseDB.put(Constants_News.HBASE_TABLE_USER, user.getName(), Constants_News.HBASE_FAMILY_USER_ID, Constants_News.HBASE_COLUMN_USER_ID_ID, rowKey);
		HbaseDB.put(Constants_News.HBASE_TABLE_USER, user.getEmail(), Constants_News.HBASE_FAMILY_USER_USER, Constants_News.HBASE_COLUMN_USER_USERID, rowKey);
	}
	
	/**
	 * 检测邮箱是否存在
	 * @param user
	 * @return
	 */
	public boolean checkEmail(String email) {
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_USER, email, Constants_News.HBASE_FAMILY_USER_USER);
		return result.isEmpty();
	}
	
	/**
	 * 检测姓名是否存在
	 * @param user
	 * @return
	 */
	public boolean checkName(String name) {
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_USER, name, Constants_News.HBASE_FAMILY_USER_ID);
		return result.isEmpty();
	}
	
	/**
	 * 用户登录操作
	 * @param user
	 * @return
	 */
	public int login(User user) {
		int id = getIdByName(user.getName());
		if (id > 0) {
			User user2 = getById(id);
			if (user2!=null && MD5Util.isPwdRight(user.getPwd(), user2.getPwd())) {
				user.setId(id);
				user.setEmail(user2.getEmail());
				return 1;
			} else {
				return 0;
			}
		} else {
			return -1;
		}
	}
	
	/**
	 * 根据用户名称找到用户ID
	 * @param user
	 * @return
	 */
	private int getIdByName(String name) {
		int id = 0;
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_USER, name, Constants_News.HBASE_FAMILY_USER_ID);
		if(!result.isEmpty()) {
			id = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_USER_ID), Bytes.toBytes(Constants_News.HBASE_COLUMN_USER_ID_ID)));
		}
		return id;
	}
	
	/**
	 * 根据用户ID找到用户名、密码或邮箱
	 * @param id
	 * @return
	 */
	public User getById(int id) {
		User user = null;
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_USER, id, Constants_News.HBASE_FAMILY_USER_INFO);
		if(!result.isEmpty()) {
			user = new User();
			user.setId(id);
			user.setName(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_USER_INFO), Bytes.toBytes(Constants_News.HBASE_COLUMN_USER_INFO_NAME))));
			user.setPwd(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_USER_INFO), Bytes.toBytes(Constants_News.HBASE_COLUMN_USER_INFO_PWD))));
			user.setEmail(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_USER_INFO), Bytes.toBytes(Constants_News.HBASE_COLUMN_USER_INFO_EMAIL))));
		}
		return user;
	}
	
}
