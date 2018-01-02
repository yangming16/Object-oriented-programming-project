package com.news.controller;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.news.dao.ActivityDAO;
import com.news.dao.CommentDAO;
import com.news.dao.NewsDAO;
import com.news.entity.Activity;
import com.news.entity.Comment;
import com.news.entity.User;
import com.news.pojo.ActivityType;
import com.news.util.Constants;
import com.news.util.DateUtil;
import com.news.util.ResponseUtil;

import net.sf.json.JSONObject;

@Controller
public class CommentController {
	
	@Resource
	private NewsDAO newsDAO;
	
	@Resource
	private CommentDAO commentDAO;
	
	@Resource
	private ActivityDAO activityDAO;
	
	/**
	 * 发布评论
	 * @param response
	 * @param httpSession
	 * @param content
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/addComment.do")
	public ModelAndView addComment(HttpServletResponse response, HttpSession httpSession,
			@RequestParam(value="content") String content, 
			@RequestParam(value="newsId") int newsId,
			@RequestParam(value="genreId") int genreId) throws IOException{
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		JSONObject result = new JSONObject();
		Comment comment = new Comment(user.getId(), user.getName(), content, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
		commentDAO.addComment(comment, newsId);
		
		newsDAO.incrCommentNum(newsId);
		
		Activity activity = new Activity(user.getId(), newsId, genreId, ActivityType.COMMENT_NEWS, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
		activityDAO.addActivity(activity);
		result.put("errres", true);
		result.put("errmsg", "评论成功！");
		ResponseUtil.write(response, result);
		return null;
	}
	
}
