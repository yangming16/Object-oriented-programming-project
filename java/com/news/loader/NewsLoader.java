package com.news.loader;

import java.util.Scanner;

import com.news.dao.NewsDAO;
import com.news.entity.News;
import com.news.util.Constants;
import com.news.util.Esutil;

public class NewsLoader {
	
	public static void loadNews(News news) {
		NewsDAO newsDAO = new NewsDAO();
		newsDAO.addNews(news);
		Esutil.addIndex(Constants.ESIndex, Constants.ESType, news);
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String title = scanner.nextLine();
		String source = scanner.nextLine();
		String content = scanner.nextLine();
		String contentHTML = scanner.nextLine();
		String url = scanner.nextLine();
		String pic = scanner.nextLine();
		String date = scanner.nextLine();
		int comNum = scanner.nextInt();
		int popularity = scanner.nextInt();
		int upNum = scanner.nextInt();
		int shareNum = scanner.nextInt();
		int heat = scanner.nextInt();
		int genreId = scanner.nextInt();
		String genreName = scanner.next();
		scanner.close();
		News news = new News(title, source, content, contentHTML, url, pic, date, comNum, popularity, upNum, shareNum,heat, genreId, genreName);
		loadNews(news);
	}
	
}
