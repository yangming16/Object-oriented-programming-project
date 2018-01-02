package com.news.entity;

public class Genre{

	private int genreId;
	private String genreName;
	
	public Genre(){
		
	}
	
	public Genre(int genreId, String genreName) {
		this.genreId = genreId;
		this.genreName = genreName;
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
		this.genreName = genreName.trim();
	}

	@Override
	public String toString() {
		return "Genre [genre_id=" + genreId + ", genre_name=" + genreName + "]";
	}
	
}