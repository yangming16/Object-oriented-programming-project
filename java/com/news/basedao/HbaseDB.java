package com.news.basedao;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseDB {
	private Connection conn;
	private Configuration conf;

	// singel module
	private static class SingletonHolder {
		private static final HbaseDB INSTANCE = new HbaseDB();
	}
	
	private HbaseDB() {
		try {
			conf = new Configuration();
			conf.set("hbase.zookeeper.quorum", "oracle");
			conf.set("habse.rootdir", "hdfs://oracle:9000/hbase");
			conn = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConn() {
		return SingletonHolder.INSTANCE.conn;
	}
	
	/**
     * 计数器
     * @param tableName
     * @param rowKey
     * @param family
     * @param column
     * @param range
     * @return int
     * @throws IOException
     */
    public static int incr(String tableName, String rowKey,  String family, String column, int range) {
    	int count = 0;
		try {
			Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
			count = Integer.parseInt(String.valueOf(table.incrementColumnValue(Bytes.toBytes(rowKey), Bytes.toBytes(family), Bytes.toBytes(column), range)));
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
    }
    
    /**
	 * 添加数据
	 * @param tableName
	 * @param rowKey String
	 * @param family
	 * @param qualifier
	 * @param value String
	 */
	public static void put(String tableName, String rowKey, String family, String qualifier, String value) {
		try {
			Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			table.put(put);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加数据
	 * @param tableName
	 * @param rowKey int
	 * @param family
	 * @param qualifier
	 * @param value int
	 */
	public static void put(String tableName, int rowKey, String family, String qualifier, int value) {
		try {
			Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			table.put(put);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加数据
	 * @param tableName
	 * @param rowKey int
	 * @param family
	 * @param qualifier
	 * @param value double
	 */
	public static void put(String tableName, int rowKey, String family, String qualifier, double value) {
		try {
			Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			table.put(put);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加数据
	 * @param tableName
	 * @param rowKey int
	 * @param family
	 * @param qualifier
	 * @param value String
	 */
	public static void put(String tableName, int rowKey, String family, String qualifier, String value) {
		try {
			Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			table.put(put);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加数据
	 * @param tableName
	 * @param rowKey String
	 * @param family
	 * @param qualifier
	 * @param value int
	 */
	public static void put(String tableName, String rowKey, String family, String qualifier, int value) {
		try {
			Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			table.put(put);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 获得一行数据，行健为long类型
     * @category get 'tableName','rowKey'
     * @param tableName
     * @param rowKey
     * @return Result||null
     * @throws IOException
     */
    public static Result getResultByRow(String tableName, int rowKey, String family) {
    	Result result = null;
    	try {
	        Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
	        Get get = new Get(Bytes.toBytes(rowKey));
	        if(family!=null) {
	        	get.addFamily(Bytes.toBytes(family));
	        }
	        result = table.get(get);
	        table.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
    }
    
    /**
     * 获得一行数据，行健为字符串类型
     * @category get 'tableName','rowKey'
     * @param tableName
     * @param rowKey
     * @return Result||null
     * @throws IOException
     */
    public static Result getResultByRow(String tableName, String rowKey, String family) {
    	Result result = null;
    	try {
	        Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
	        Get get = new Get(Bytes.toBytes(rowKey));
	        if(family!=null) {
	        	get.addFamily(Bytes.toBytes(family));
	        }
	        result = table.get(get);
	        table.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
    }
    
    /**
     * 扫描表
     * @param tableName
     * @param family
     * @return
     */
    public static ResultScanner getResultScanner(String tableName, String family) {
    	ResultScanner resultScanner = null;
    	try {
    		Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
    		Scan scan = new Scan();
			if(family != null) {
				scan.addFamily(Bytes.toBytes(family));
			}
    		resultScanner = table.getScanner(scan);
    		table.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	return resultScanner;
    }
    
    /**
     * 按照一定规则扫描表（或者无规则）
     * @param tableName
     * @param filter
     * @param startRow
     * @param isReverse
     * @param family
     * @return
     */
    public static ResultScanner getResultScannerByFilter(String tableName, Filter filter, int startRow, Boolean isReverse, String family) {
    	ResultScanner resultScanner = null;
    	try {
    		Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
    		Scan scan = new Scan();
    		// 设置倒序遍历
    		scan.setReversed(isReverse);
			if (filter != null) {
				scan.setFilter(filter);
			}
			if(family != null) {
				scan.addFamily(Bytes.toBytes(family));
			}
			if(startRow != -1) {
				scan.setStartRow(Bytes.toBytes(startRow));
			}
    		resultScanner = table.getScanner(scan);
    		table.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	return resultScanner;
    }
    
    /**
     * 按照一定规则扫描表（或者无规则）
     * @param tableName
     * @param filterList
     * @param startRow
     * @param isReverse
     * @param family
     * @return
     */
    public static ResultScanner getResultScannerByFilterList(String tableName, FilterList filterList, int startRow, Boolean isReverse, String family) {
    	ResultScanner resultScanner = null;
    	try {
    		Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
    		Scan scan = new Scan();
    		// 设置倒序遍历
    		scan.setReversed(isReverse);
			if (filterList != null) {
				scan.setFilter(filterList);
			}
			if(family != null) {
				scan.addFamily(Bytes.toBytes(family));
			}
			if(startRow != -1) {
				scan.setStartRow(Bytes.toBytes(startRow));
			}
    		resultScanner = table.getScanner(scan);
    		table.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	return resultScanner;
    }
    
    /**
     * 删除某一行的数据，行健为String类型
     * @category deleteall 'tableName','rowKey'
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    public void deleteDataByRow(String tableName, String rowKey) {
    	try {
	        Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
	        Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
	        table.delete(deleteAll);
	        table.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 删除某一行的数据，行健为int类型
     * @category deleteall 'tableName','rowKey'
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    public void deleteDataByRow(String tableName, int rowKey) {
    	try {
	        Table table = HbaseDB.getConn().getTable(TableName.valueOf(tableName));
	        Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
	        table.delete(deleteAll);
	        table.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }

}