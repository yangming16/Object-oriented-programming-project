package com.news.entity;

public class News {
	
	private int id;
	private String title;
	private String source;
	private String content;
	private String contentHTML;
	private String url;
	private String pic;
	private String date;
	private int comNum;// 评论量
	private int popularity;// 浏览量
	private int upNum;// 点赞量
	private int shareNum;// 分享量
	private int heat;// 热度
	
	private int genreId;
	private String genreName;
	
	public News() {
		
	}
	
	public News(String title, String source, String content, String contentHTML, String url, String pic, String date, int comNum, int popularity, int upNum, int shareNum,int heat, int genreId, String genreName) {
		super();
		this.title = title;
		this.source = source;
		this.content = content;
		this.contentHTML = contentHTML;
		this.url = url;
		this.pic = pic;
		this.date = date;
		this.comNum = comNum;
		this.popularity = popularity;
		this.upNum = upNum;
		this.shareNum = shareNum;
		this.heat = heat;
		this.genreId = genreId;
		this.genreName = genreName;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContentHTML() {
		return contentHTML;
	}
	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getComNum() {
		return comNum;
	}
	public void setComNum(int comNum) {
		this.comNum = comNum;
	}
	public int getPopularity() {
		return popularity;
	}
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	public int getUpNum() {
		return upNum;
	}
	public void setUpNum(int upNum) {
		this.upNum = upNum;
	}
	public int getShareNum() {
		return shareNum;
	}
	public void setShareNum(int shareNum) {
		this.shareNum = shareNum;
	}
	
	public int getHeat() {
		return heat;
	}
	public void setHeat(int heat) {
		this.heat = heat;
	}

	public int getGenreId() {
		return genreId;
	}
	public void setGenreId(int genreId) {
		this.genreId = genreId;
	}
	public String getGenreName() {
		return genreName;
	}
	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}
	
}