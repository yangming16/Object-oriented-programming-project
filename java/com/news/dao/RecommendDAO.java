package com.news.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.news.entity.News;
import com.news.util.GetRecommends;

@Repository("recommendDAO")
public class RecommendDAO {

	/**
	 * 获得推荐新闻列表
	 * @param userId
	 * @return
	 */

	public List<News> getRecomList(int userId, int recoNum) {
		List<News> newsList = new ArrayList<News>();
		int[] list = GetRecommends.get(userId, recoNum);

		for (int i = 0; i < list.length; i++) {
			// System.out.println(">>>>>>>>>>>>> newid:"+list[i]);
			if (list[i] == 0) {
				list[i] = (int) (Math.random() * 26 + 1);
				// System.out.println(">>>>>>>>>>>>> changeUnewid:"+list[i]);
			}
		}
		NewsDAO nDao = new NewsDAO();
		for (int newsid : list) {
			newsList.add(nDao.getNewsById(newsid));
		}
		return newsList;
	}
	
	public static void main(String[] args) {
		//List<News> lid = getRecomList(3, 4);
//		for (News news : lid) {
//			System.out.println(news.getTitle());
//		}

	}
	
}
