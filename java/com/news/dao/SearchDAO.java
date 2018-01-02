package com.news.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.news.entity.News;
import com.news.util.Constants;
import com.news.util.Esutil;
import com.news.util.PageUtil;

@Repository("searchDAO")
public class SearchDAO {
	
	/**
	 * 搜索新闻
	 * @param keyword
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<Map<String, Object>> search(String keyword, int pageNum, int pageSize) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = Esutil.search(keyword, Constants.ESIndex, Constants.ESType, (pageNum - 1) * pageSize, pageSize);
		} catch (Exception e) {
			System.out.println("查询索引错误!{}");
			e.printStackTrace();
		}
		int searchCount = Integer.parseInt(((Long) map.get("count")).toString());
		List<Map<String, Object>> source = (List<Map<String,Object>>) map.get("dataList");
		
		List<News> searchList = new ArrayList<News>();
		for (Map<String, Object> map2 : source) {
			News news = new News();
			news.setId(Integer.valueOf(map2.get("id") + ""));
			news.setTitle(map2.get("title") + "");
			news.setSource(map2.get("source") + "");
			news.setContent(map2.get("content") + "");
			news.setContentHTML(map2.get("contentHTML") + "");
			news.setUrl(map2.get("url") + "");
			news.setPic(map2.get("pic") + "");
			news.setDate(map2.get("date") + "");
			news.setComNum(Integer.valueOf(map2.get("comNum") + ""));
			news.setPopularity(Integer.valueOf(map2.get("popularity") + ""));
			news.setUpNum(Integer.valueOf(map2.get("upNum") + ""));
			news.setShareNum(Integer.valueOf(map2.get("shareNum") + ""));
			news.setGenreId(Integer.valueOf(map2.get("genreId") + ""));
			news.setGenreName(map2.get("genreName") + "");
			searchList.add(news);
		}
		
		PageUtil<Map<String, Object>> page = new PageUtil<Map<String, Object>>(String.valueOf(pageNum), String.valueOf(pageSize), searchCount);
		page.setList(searchList);
		return page;
	}
	
	
}