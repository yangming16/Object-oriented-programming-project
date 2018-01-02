package com.news.controller;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.news.dao.ActivityDAO;
import com.news.dao.UserDAO;
import com.news.entity.Activity;
import com.news.entity.User;
import com.news.pojo.ActivityType;
import com.news.util.Constants;
import com.news.util.DateUtil;
import com.news.util.ResponseUtil;

import net.sf.json.JSONObject;

@Controller
public class UserController {
	
	@Resource
	private UserDAO userDAO;
	
	@Resource
	private ActivityDAO activityDAO;
	
	/**
	 * 登录页面
	 * @return
	 */
	@RequestMapping("/login.do")
	public ModelAndView login() {
		return new ModelAndView("login");
	}
	
	/**
	 * 注册页面
	 * @return
	 */
	@RequestMapping("/reg.do")
	public ModelAndView reg() {
		return new ModelAndView("reg");
	}
	
	/**
	 * 退出登录
	 * @param session
	 * @return
	 */
	@RequestMapping("/logout.do")
	public ModelAndView logout(HttpSession httpSession) {
		User user = (User) httpSession.getAttribute(Constants.currentUserSessionKey);
		Activity activity = new Activity(user.getId(), 0, 0, ActivityType.LOGOUT, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
		activityDAO.addActivity(activity);
		httpSession.removeAttribute(Constants.currentUserSessionKey);
		return new ModelAndView("redirect:/login.do");
	}
	
	/**
	 * 注册用户操作
	 * @param user
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/regUserAction.do")
	public ModelAndView register(User user, HttpServletResponse response) throws IOException{
		JSONObject result = new JSONObject();
		if (!userDAO.checkEmail(user.getEmail())) {
			result.put("errres", false);
			result.put("errno", 1);
			result.put("errmsg", "邮箱已经存在！");
		} else if (!userDAO.checkName(user.getName())) {
			result.put("errres", false);
			result.put("errno", 2);
			result.put("errmsg", "姓名已经存在！");
		}else {
			userDAO.addUser(user);
			Activity activity = new Activity(user.getId(), 0, 0, ActivityType.REG, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
			activityDAO.addActivity(activity);
			result.put("errres", true);
			result.put("errno", 0);
			result.put("errmsg", "注册成功！");
		}
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * 登录
	 * @param user
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/loginUserAction.do")
	public ModelAndView gologin(User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject result = new JSONObject();
		int res = userDAO.login(user);
		if (res == 1) {
			HttpSession session = request.getSession();
			session.setAttribute(Constants.currentUserSessionKey, user);
			Activity activity = new Activity(user.getId(), 0, 0, ActivityType.LOGIN, DateUtil.DateToString("yyyy-MM-dd HH:mm:ss", new Date()));
			activityDAO.addActivity(activity);
			result.put("errres", true);
			result.put("errno", res);
			result.put("errmsg", "登陆成功！");
		}else if(res == 0){
			result.put("errres", false);
			result.put("errno", res);
			result.put("errmsg", "密码不正确！");
		}else if(res == -1){
			result.put("errres", false);
			result.put("errno", res);
			result.put("errmsg", "用户名不正确！");
		}else {
			result.put("errres", false);
			result.put("errno", res);
			result.put("errmsg", "登录失败！");
		}
		ResponseUtil.write(response, result);
		return null;
	}
	
}
