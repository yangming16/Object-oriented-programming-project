package com.news.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Repository;

import com.news.basedao.HbaseDB;
import com.news.entity.Genre;
import com.news.entity.News;
import com.news.util.Constants_News;

@Repository("genreDAO")
public class GenreDAO {
	
	/**
	 * 添加分类
	 * @param genre
	 */
	public void addGenre(Genre genre) {
		HbaseDB.put(Constants_News.HBASE_TABLE_GENRE, genre.getGenreId(), Constants_News.HBASE_FAMILY_GENRE_GENRE, Constants_News.HBASE_COLUMN_GENRE_NAME, genre.getGenreName());
	}
	
	/**
	 * 添加genre表到news表的映射
	 * @param genre
	 * @param news
	 * @param hbaseDB
	 */
	public void addGenreToNews(Genre genre, News news) {
		HbaseDB.put(Constants_News.HBASE_TABLE_GENRE, genre.getGenreId() + "_" + news.getId(), Constants_News.HBASE_FAMILY_GENRE_NEWS, Constants_News.HBASE_COLUMN_GENRE_NEWS_ID, news.getId());
	}
	
	/**
	 * 获得新闻分类名称和分类id
	 * @return
	 */
	public List<Genre> getGenres(int genreNum){
		List<Genre> genres = new ArrayList<Genre>();
		Genre genre = null;
		Filter filter = null;
		if (genreNum != -1) {
			filter = new PageFilter(genreNum);
		}
		ResultScanner resultScanner = HbaseDB.getResultScannerByFilter(Constants_News.HBASE_TABLE_GENRE, filter, -1, false, Constants_News.HBASE_FAMILY_GENRE_GENRE);
		Iterator<Result> iter = resultScanner.iterator();
		while(iter.hasNext()) {
			Result result = iter.next();
			if(!result.isEmpty()) {
				genre = new Genre();
				genre.setGenreId(Bytes.toInt(result.getRow()));
				genre.setGenreName(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_GENRE_GENRE), Bytes.toBytes(Constants_News.HBASE_COLUMN_GENRE_NAME))));
				genres.add(genre);
			}
		}
		return genres;
	}
	
	/**
	 * 根据id获得genre信息
	 * @param genresid
	 * @return
	 */
	public Genre getById(int genreid) {
		Genre genre = null;
		Result result = HbaseDB.getResultByRow(Constants_News.HBASE_TABLE_GENRE, genreid, Constants_News.HBASE_FAMILY_GENRE_GENRE);
		if(!result.isEmpty()) {
			genre = new Genre();
			genre.setGenreId(Integer.parseInt(String.valueOf(genreid)));
			genre.setGenreName(Bytes.toString(result.getValue(Bytes.toBytes(Constants_News.HBASE_FAMILY_GENRE_GENRE), Bytes.toBytes(Constants_News.HBASE_COLUMN_GENRE_NAME))));
		}
		return genre;
	}
	
}