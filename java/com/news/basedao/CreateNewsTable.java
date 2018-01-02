package com.news.basedao;

import java.io.IOException;

import com.news.util.Constants_News;
import com.news.basedao.HbaseDB;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;

public class CreateNewsTable {

	static Connection conn;

	public static void iniTable(String table, String[] fams) {
		Admin admin;
		conn = HbaseDB.getConn();
		if (conn == null) {
			System.out.println("conn is null!");
		}
		try {
			admin = conn.getAdmin();
			TableName tableName = TableName.valueOf(table);
			if (admin.tableExists(tableName)) {
				admin.disableTable(tableName);
				admin.deleteTable(tableName);
				System.out.println("delete table " + tableName + " successfully");
			}

			HTableDescriptor desc = new HTableDescriptor(tableName);
			HColumnDescriptor coldef;
			for (String fam : fams) {
				coldef = new HColumnDescriptor(fam);
				desc.addFamily(coldef);
			}
			admin.createTable(desc);

			admin.close();
			boolean avail = admin.isTableAvailable(tableName);
			System.out.println("table  " + table + "  " + avail);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		iniTable(Constants_News.HBASE_TABLE_GID, new String[] { Constants_News.HBASE_FAMILY_GID_GID });
		iniTable(Constants_News.HBASE_TABLE_USER, new String[] { Constants_News.HBASE_FAMILY_USER_ID, Constants_News.HBASE_FAMILY_USER_USER, Constants_News.HBASE_FAMILY_USER_INFO });
		iniTable(Constants_News.HBASE_TABLE_GENRE, new String[] { Constants_News.HBASE_FAMILY_GENRE_GENRE, Constants_News.HBASE_FAMILY_GENRE_NEWS });
		iniTable(Constants_News.HBASE_TABLE_NEWS, new String[] { Constants_News.HBASE_FAMILY_NEWS_NEWS, Constants_News.HBASE_FAMILY_NEWS_GENRE, Constants_News.HBASE_FAMILY_NEWS_COMMENT });
		iniTable(Constants_News.HBASE_TABLE_ACTIVITY, new String[] { Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY });
		iniTable(Constants_News.HBASE_TABLE_COMMENT, new String[] { Constants_News.HBASE_FAMILY_COMMENT_COMMENT });
		System.out.println("create over!");
	}

}