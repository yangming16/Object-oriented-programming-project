package com.news.entity;

import com.news.pojo.ActivityType;

public class Activity {
	
	private int id;
	private int userId;
	private int newsId;
	private int genreId;
	private ActivityType activity;
	private String time;
	
	public Activity() {
		
	}
	
	public Activity(int userId, int newsId, int genreId, ActivityType activity, String time) {
		super();
		this.userId = userId;
		this.newsId = newsId;
		this.genreId = genreId;
		this.activity = activity;
		this.time = time;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getNewsId() {
		return newsId;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public int getGenreId() {
		return genreId;
	}
	public void setGenreId(int genreId) {
		this.genreId = genreId;
	}
	public ActivityType getActivity() {
		return activity;
	}
	public void setActivity(ActivityType activity) {
		this.activity = activity;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
