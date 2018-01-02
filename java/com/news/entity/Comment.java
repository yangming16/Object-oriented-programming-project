package com.news.entity;

public class Comment {
	
	private int id;
	private int userId;
	private String username;
	private String content;
	private String date;
	
	public Comment() {
		
	}
	
	public Comment(int userId, String username, String content, String date) {
		super();
		this.userId = userId;
		this.username = username;
		this.content = content;
		this.date = date;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}