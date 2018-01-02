package com.news.util;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.news.basedao.HbaseDB;

public class GetRecommends {
	
	public static long[][]  getRandomArray(int row,int col,int max) {
		long arry[][] = new long[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				arry[i][j] = (long) (Math.random()*max);
			}
		}
		return arry;
		
	}


	public static long[][] getScoreArray() {
		//
		int max = 1000;
		int userId,newsId,u_score;
		int maxUserId = 0,maxNewsId = 0;
		long score[][] = new long[max][max] ;
		ResultScanner resultScanner = HbaseDB.getResultScanner(Constants_News.HBASE_TABLE_ACTIVITY, Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY);
		Iterator<Result> iter = resultScanner.iterator();
		while (iter.hasNext()) {
			Result result = iter.next();
			if (!result.isEmpty()) {
				 userId = Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_USER_ID)));
				 newsId =  Bytes.toInt(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_ACTIVITY_ACTIVITY), Bytes.toBytes(Constants_News.HBASE_COLUMN_ACTIVITY_NEWS_ID)));	
				
				 u_score =(int) (Math.random() * 7);
				 
				 if (userId > maxUserId) {
					maxUserId = userId;
				}
				 if (newsId > maxNewsId) {
					maxNewsId = newsId;
				}
				score[userId][newsId] = u_score;
			}
		}
		long[][] realScore = new long[maxUserId+1][maxNewsId+1]; 
		for (int i = 0; i < realScore.length; i++) {
			for (int j = 0; j < realScore[0].length; j++) {
			//	realScore[i][j] = score[i][j];
				realScore[i][j] = (long) (Math.random() * 7);
			}
		}
		return realScore;
	}
	
	// 计算 A,B的pearson相关系数
	public static double getPear(int userA, int userB, long[][] score) {
		
		int nItem = score[0].length;
		
		int sumXY = 0;
		int sumX = 0;
		int sumY = 0;
		int sumX2 = 0;
		int sumY2 = 0;
		for (long js : score[userA]) {
			sumX += js;
			sumX2 += js * js;
		}
		for (long js : score[userB]) {
			sumY += js;
			sumY2 += js * js;
		}
		for (int i = 0; i < nItem; i++) {
			sumXY += score[userA][i] * score[userB][i];
		}

		double pearson;
		double A = sumXY - sumX * sumY / nItem;
		double B = (sumX2 - sumX * sumX / nItem) * (sumY2 - sumY * sumY / nItem);
		double C = Math.sqrt(B);
		if (C == 0) {
			//System.out.println("无法计算pearson相关系数，因其分母为0");
			pearson = -1;
			return 0;
		}
		return pearson = A / C;

	}

	// 按项目的相似性大小,冒泡排序，获取相似列表
	public static int[] bestMatch(double[] coArry) {
		double[] arry = coArry;
		int matcher[] = new int[coArry.length];
		for (int i = 0; i < matcher.length; i++) {
			matcher[i] = i;
		}
		for (int i = 1; i < arry.length; i++) {
			for (int j = i; j > 0; j--) {
				if (arry[j] > arry[j - 1]) {

					double temp = arry[j - 1];
					arry[j - 1] = arry[j];
					arry[j] = temp;
					int tempIndex = matcher[j];
					matcher[j] = matcher[j - 1];
					matcher[j - 1] = tempIndex;

				}
			}
		}
		return matcher;
	}

	public static int[] bestItem(int uid, long[][] score, double[][] pearsons) {
		// 没有看过的新闻 评分*相似度 累加
		int nItem = score[0].length;
		double sumScore = 0;
		double[] itemRank = new double[nItem];
		for (int i = 0; i < score[uid].length; i++) {
			long sc = (long) score[uid][i];
			sumScore = 0;
			if (sc == 0) {
				for (int j = 0; j < pearsons.length; j++) {
					sumScore += pearsons[uid][j] * score[j][i];
				}
				if (sumScore > 0) {
					itemRank[i] = sumScore;
				}
			}
		}

		return bestMatch(itemRank);
	}

	public static int[] get(int userId,int recoNum) {
	//	long[][] score = getRandomArray(100, 380, 7);
		long[][] score = getScoreArray();
		
		int nUsers = score.length;
		double[][] pearsons = new double[nUsers][nUsers];
		
		for (int i = 0; i < nUsers; i++) {
			pearsons[i][i] = -1;
			for (int j = i + 1; j < nUsers; j++) {
				pearsons[i][j] = getPear(i, j,score);
				pearsons[j][i] = pearsons[i][j];
			}
		}
		int[] recoList = bestItem(userId,score,pearsons);
		int[] news = new int[recoNum];
		for (int i = 0; i < recoNum; i++) {
			news[i] = recoList[i];
		}
		
		return news;
	}
	public static void main(String[] args) throws IOException {
//		int[] a = get(3,4);
//		for (int i : a) {
//			System.out.println(i);
//		}
		
		

		
		
	}

}
