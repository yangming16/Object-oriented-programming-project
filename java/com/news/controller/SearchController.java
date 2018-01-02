package com.news.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.news.dao.ActivityDAO;
import com.news.dao.GenreDAO;
import com.news.dao.NewsDAO;
import com.news.dao.SearchDAO;
import com.news.entity.Activity;
import com.news.entity.Genre;
import com.news.entity.News;
import com.news.entity.User;
import com.news.pojo.ActivityType;
import com.news.util.Constants;
import com.news.util.DateUtil;
import com.news.util.PageUtil;
import com.news.util.RandomNum;

@Controller
public class SearchController {
	
	@Resource
	private GenreDAO genreDAO;
	
	@Resource
	private NewsDAO newsDAO;
	
	@Resource
	private SearchDAO searchDAO;
	
	@Resource
	private ActivityDAO activityDAO;
	
	/**
	 * 搜索新闻
	 * @param httpSession
	 * @param keyword
	 * @param pageNum
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search.do")
	public ModelAndView search(HttpSession httpSession, 
			@RequestParam(value = "keyword",required = false) String keyword, 
			@RequestParam(value = "pageNum", defaultValue="1" ) int pageNum) throws Exception {
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		List<Genre> genreList = genreDAO.getGenres(-1);// 全部分类
		List<Genre> genreListPart = genreDAO.getGenres(Constants.partGenreNum);// 部分分类
		PageUtil<Map<String, Object>> searchPages = searchDAO.search(keyword, pageNum, Constants.ESRow);// 搜索返回的结果
		
		Activity activity = new Activity(user.getId(), 0, 0, ActivityType.SEARCH_NEWS, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
		activityDAO.addActivity(activity);
		
		List<News> breakingNewsList = newsDAO.getLatestNews(Constants.breakingNewsNum);// 即时最新新闻数据
		List<News> praiseNewsList = newsDAO.getPraiseNews(Constants.praiseBrowserNewsNum);// 热门点赞新闻数据
		List<News> popularNewsList = newsDAO.getPopularNews(Constants.popularBrowserNewsNum);// 热门浏览新闻数据
		ModelAndView modelAndView = new ModelAndView("/search");
		modelAndView.addObject("genreList", genreList);
		modelAndView.addObject("genreListPart", genreListPart);
		modelAndView.addObject("searchPages", searchPages);
		modelAndView.addObject("breakingNewsList", breakingNewsList);
		modelAndView.addObject("praiseNewsList", praiseNewsList);
		modelAndView.addObject("praiseNewsListIndex", RandomNum.generateRandomNumber(Constants.praiseBrowserNewsNum));
		modelAndView.addObject("popularNewsList", popularNewsList);
		modelAndView.addObject("popularNewsListIndex", RandomNum.generateRandomNumber(Constants.popularBrowserNewsNum));
		modelAndView.addObject("keyword", keyword);
		return modelAndView;
	}
	
}
