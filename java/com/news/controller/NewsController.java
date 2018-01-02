package com.news.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.news.dao.ActivityDAO;
import com.news.dao.CommentDAO;
import com.news.dao.GenreDAO;
import com.news.dao.NewsDAO;
import com.news.dao.RecommendDAO;
import com.news.entity.Activity;
import com.news.entity.Comment;
import com.news.entity.Genre;
import com.news.entity.News;
import com.news.entity.User;
import com.news.pojo.ActivityType;
import com.news.util.Constants;
import com.news.util.DateUtil;
import com.news.util.PageUtil;
import com.news.util.RandomNum;
import com.news.util.ResponseUtil;

import net.sf.json.JSONObject;

@Controller
public class NewsController {
	
	@Resource
	private NewsDAO newsDAO;
	
	@Resource
	private GenreDAO genreDAO;
	
	@Resource
	private ActivityDAO activityDAO;
	
	@Resource
	private CommentDAO commentDAO;
	
	@Resource
	private RecommendDAO recommendDAO;
	
	/**
	 * 新闻首页
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/index.do")
	public ModelAndView index(HttpSession httpSession, 
			@RequestParam(value = "pageNum", defaultValue="1" ) int pageNum) throws Exception{
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		List<Genre> genreList = genreDAO.getGenres(-1);// 全部分类
		List<Genre> genreListPart = genreDAO.getGenres(Constants.partGenreNum);// 部分分类
		PageUtil<Map<String, Object>> newsPage = newsDAO.getNewsList(pageNum, Constants.rowNum);// 分页新闻数据
		List<News> breakingNewsList = newsDAO.getLatestNews(Constants.breakingNewsNum);// 即时最新新闻数据
		List<News> browserNewsList = activityDAO.getUserBrowseRecordList(user.getId());// 最近浏览新闻数据
		List<News> praiseNewsList = newsDAO.getPraiseNews(Constants.praiseBrowserNewsNum);// 热门点赞新闻数据
		List<News> popularNewsList = newsDAO.getPopularNews(Constants.popularBrowserNewsNum);// 热门浏览新闻数据
		List<News> heatNewsList = newsDAO.getHeatNews(Constants.heatBrowserNewsNum);// 热门新闻推荐数据
		List<News> shareNewsList = newsDAO.getShareNews(Constants.shareBrowserNewsNum);// 热门分享新闻数据
		ModelAndView modelAndView = new ModelAndView("/index");
		modelAndView.addObject("genreList", genreList);
		modelAndView.addObject("genreListPart", genreListPart);
		modelAndView.addObject("newsPage", newsPage);
		modelAndView.addObject("newsPageIndex", RandomNum.generateRandomNumber(Constants.rowNum));
		modelAndView.addObject("breakingNewsList", breakingNewsList);
		modelAndView.addObject("browserNewsList", browserNewsList);
		modelAndView.addObject("browserNewsListIndex", RandomNum.generateRandomNumber(Constants.recentBrowserNewsNum));
		modelAndView.addObject("praiseNewsList", praiseNewsList);
		modelAndView.addObject("praiseNewsListIndex", RandomNum.generateRandomNumber(Constants.praiseBrowserNewsNum));
		modelAndView.addObject("popularNewsList", popularNewsList);
		modelAndView.addObject("popularNewsListIndex", RandomNum.generateRandomNumber(Constants.popularBrowserNewsNum));
		modelAndView.addObject("heatNewsList", heatNewsList);
		modelAndView.addObject("heatNewsListIndex", RandomNum.generateRandomNumber(Constants.heatBrowserNewsNum));
		modelAndView.addObject("shareNewsList", shareNewsList);
		modelAndView.addObject("shareNewsListIndex", RandomNum.generateRandomNumber(Constants.shareBrowserNewsNum));
		modelAndView.addObject("genreId", -1);
		return modelAndView;
	}
	
	/**
	 * 新闻分类
	 * @param httpSession
	 * @param genreid
	 * @return
	 */
	@RequestMapping("/cat.do")
	public ModelAndView cat(HttpSession httpSession, 
			@RequestParam(value = "genreId") int genreId, 
			@RequestParam(value = "pageNum", defaultValue="1" ) int pageNum) {
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		List<Genre> genreList = genreDAO.getGenres(-1);
		List<Genre> genreListPart = genreDAO.getGenres(Constants.partGenreNum);
		PageUtil<Map<String, Object>> newsPage = newsDAO.getNewsListByGenreId(genreId, pageNum, Constants.rowNum);
		List<News> breakingNewsList = newsDAO.getLatestNews(Constants.breakingNewsNum);
		List<News> browserNewsList = activityDAO.getUserBrowseRecordList(user.getId());
		List<News> praiseNewsList = newsDAO.getPraiseNews(Constants.praiseBrowserNewsNum);
		List<News> popularNewsList = newsDAO.getPopularNews(Constants.popularBrowserNewsNum);
		Genre genre = genreDAO.getById(genreId);
		ModelAndView modelAndView = new ModelAndView("/cat");
		modelAndView.addObject("genreList", genreList);
		modelAndView.addObject("genreListPart", genreListPart);
		modelAndView.addObject("newsPage", newsPage);
		modelAndView.addObject("newsPageIndex", RandomNum.generateRandomNumber(Constants.rowNum));
		modelAndView.addObject("breakingNewsList", breakingNewsList);
		modelAndView.addObject("browserNewsList", browserNewsList);
		modelAndView.addObject("browserNewsListIndex", RandomNum.generateRandomNumber(Constants.recentBrowserNewsNum));
		modelAndView.addObject("praiseNewsList", praiseNewsList);
		modelAndView.addObject("praiseNewsListIndex", RandomNum.generateRandomNumber(Constants.praiseBrowserNewsNum));
		modelAndView.addObject("popularNewsList", popularNewsList);
		modelAndView.addObject("popularNewsListIndex", RandomNum.generateRandomNumber(Constants.popularBrowserNewsNum));
		modelAndView.addObject("genreId", genreId);
		modelAndView.addObject("genreName", genre.getGenreName());
		return modelAndView;
	}
	
	/**
	 * 新闻详情页
	 * @param httpSession
	 * @param id
	 * @return
	 */
	@RequestMapping("/detail.do")
	public ModelAndView detail(HttpSession httpSession,
			@RequestParam(value="newsid") int newsid) {
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		newsDAO.incrPopularity(newsid);
		List<Genre> genreList = genreDAO.getGenres(-1);
		List<Genre> genreListPart = genreDAO.getGenres(Constants.partGenreNum);
		List<News> breakingNewsList = newsDAO.getLatestNews(Constants.breakingNewsNum);
		News news = newsDAO.getNewsById(newsid);
		List<News> praiseNewsList = newsDAO.getPraiseNews(Constants.praiseBrowserNewsNum);// 热门点赞新闻数据
		List<News> popularNewsList = newsDAO.getPopularNews(Constants.popularBrowserNewsNum);// 热门浏览新闻数据
		
		int recoNum = 4;   //推荐新闻条数
		List<News> newsList = recommendDAO.getRecomList(user.getId(), recoNum);
//		List<News> newsList = new ArrayList<>();
	//	RecommendDAO reco = new RecommendDAO();
	//	List<News> newsList = reco.getRecomList(user.getId(), recoNum);
		
		Activity activity = new Activity(user.getId(), newsid, news.getGenreId(), ActivityType.BROWSED_NEWS, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
		activityDAO.addActivity(activity);
		List<News> browserNewsList = activityDAO.getUserBrowseRecordList(user.getId());
		
		List<Comment> commentList = commentDAO.getCommentList(newsid);
		
		
		ModelAndView modelAndView = new ModelAndView("/detail");
		modelAndView.addObject("newsList", newsList);
		modelAndView.addObject("genreList", genreList);
		modelAndView.addObject("genreListPart", genreListPart);
		modelAndView.addObject("breakingNewsList", breakingNewsList);
		modelAndView.addObject("browserNewsList", browserNewsList);
		modelAndView.addObject("browserNewsListIndex", RandomNum.generateRandomNumber(Constants.recentBrowserNewsNum));
		modelAndView.addObject("praiseNewsList", praiseNewsList);
		modelAndView.addObject("praiseNewsListIndex", RandomNum.generateRandomNumber(Constants.praiseBrowserNewsNum));
		modelAndView.addObject("popularNewsList", popularNewsList);
		modelAndView.addObject("popularNewsListIndex", RandomNum.generateRandomNumber(Constants.popularBrowserNewsNum));
		modelAndView.addObject("news", news);
		modelAndView.addObject("commentList", commentList);
		return modelAndView;
	}
	
	/**
	 * 点赞
	 * @param response
	 * @param httpSession
	 * @param newsId
	 * @param genreId
	 * @return
	 */
	@RequestMapping("/praise.do")
	public ModelAndView praise(HttpServletResponse response, HttpSession httpSession, 
			@RequestParam(value="newsId") int newsId, 
			@RequestParam(value="genreId") int genreId) {
		JSONObject result = new JSONObject();
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		newsDAO.incrPraiseNum(newsId);
		Activity activity = new Activity(user.getId(), newsId, genreId, ActivityType.UP_NEWS, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
		activityDAO.addActivity(activity);
		result.put("errres", true);
		result.put("errmsg", "成功！");
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * 分享
	 * @param response
	 * @param httpSession
	 * @param newsId
	 * @param genreId
	 * @return
	 */
	@RequestMapping("/share.do")
	public ModelAndView share(HttpServletResponse response, HttpSession httpSession, 
			@RequestParam(value="newsId") int newsId, 
			@RequestParam(value="genreId") int genreId) {
		JSONObject result = new JSONObject();
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		newsDAO.incrShareNum(newsId);
		Activity activity = new Activity(user.getId(), newsId, genreId, ActivityType.SHARE_NEWS, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
		activityDAO.addActivity(activity);
		result.put("errres", true);
		result.put("errmsg", "成功！");
		ResponseUtil.write(response, result);
		return null;
	}
	
}