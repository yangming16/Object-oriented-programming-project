package main.java.SohuSpider.util;

public class Constants_News {

	// table gid
	public static final String HBASE_TABLE_GID = "news_gid";

	public static final String HBASE_FAMILY_GID_GID = "gid";
	public static final String HBASE_ROWKEY_GID_ACTIVITYID = "activityId";
	public static final String HBASE_COLUMN_GID_ACTIVITY_ID = "activity_id";
	public static final String HBASE_ROWKEY_GID_NEWSID = "newsId";
	public static final String HBASE_COLUMN_GID_NEWS_ID = "news_id";

	// table user
	public static final String HBASE_TABLE_USER = "news_user";

	public static final String HBASE_FAMILY_USER_ID = "id";
	public static final String HBASE_ROWKEY_USER_USERNAME = "username";
	public static final String HBASE_COLUMN_USER_ID = "id";

	public static final String HBASE_FAMILY_USER_INFO = "info";
	public static final String HBASE_ROWKEY_USER_USERID = "userId";
	public static final String HBASE_COLUMN_USER_USERNAME = "userName";
	public static final String HBASE_COLUMN_USER_NAME = "name";
	public static final String HBASE_COLUMN_USER_PASSWORD = "password";
	public static final String HBASE_COLUMN_USER_EMAIL = "email";

	// table genre
	public static final String HBASE_TABLE_GENRE = "news_genre";

	public static final String HBASE_FAMILY_GENRE_GENRE = "genre";
	public static final String HBASE_ROWKEY_GENRE_GENREID = "genreId";
	public static final String HBASE_COLUMN_GENRE_NAME = "genre_name";

	public static final String HBASE_FAMILY_GENRE_NEWS = "news";
	public static final String HBASE_ROWKEY_GENRE_GENREID_NEWSID = "genreId_newsId";
	public static final String HBASE_COLUMN_GENRE_NEWS_ID = "news_id";

	// table news
	public static final String HBASE_TABLE_NEWS = "news";

	public static final String HBASE_FAMILY_NEWS_NEWS = "news";
	public static final String HBASE_ROWKEY_NEWS_NEWSID = "newsId";
	public static final String HBASE_COLUMN_NEWS_TITLE = "title";
	public static final String HBASE_COLUMN_NEWS_SOURCE = "source";
	public static final String HBASE_COLUMN_NEWS_CONTENT = "content";
	public static final String HBASE_COLUMN_NEWS_CONTENT_HTML = "contentHTML";
	public static final String HBASE_COLUMN_NEWS_URL = "url";
	public static final String HBASE_COLUMN_NEWS_PIC = "pic";
	public static final String HBASE_COLUMN_NEWS_DATE = "date";
	public static final String HBASE_COLUMN_NEWS_POPULARITY = "popularity";

	public static final String HBASE_FAMILY_NEWS_GENRE = "genre";
	public static final String HBASE_ROWKEY_NEWS_NEWSID_GENREID = "newsId_genreId";
	public static final String HBASE_COLUMN_NEWS_GENRE_ID = "genre_id";
	public static final String HBASE_COLUMN_NEWS_GENRE_NAME = "genre_name";

	// table activity
	public static final String HBASE_TABLE_ACTIVITY = "news_activity";
	public static final String HBASE_FAMILY_ACTIVITY_ACTIVITY = "activity";
	public static final String HBASE_ROWKEY_ACTIVITY_ACTIVITY_ID = "activity_id";

	public static final String HBASE_COLUMN_ACTIVITY_USER_ID = "user_id";
	public static final String HBASE_COLUMN_ACTIVITY_NEWS_ID = "news_id";
	public static final String HBASE_COLUMN_ACTIVITY_GENRE_ID = "genre_id";
	public static final String HBASE_COLUMN_ACTIVITY_ACTIVITY = "activity";
	public static final String HBASE_COLUMN_ACTIVITY_TIME = "time";

	// table commment
	public static final String HBASE_TABLE_COMMENT = "news_comment";
	public static final String HBASE_FAMILY_COMMENT_COMMENT = "comment";
	public static final String HBASE_ROWKEY_COMMENT_NEWSID_USERID = "newsId_userId";
	public static final String HBASE_ROWKEY_COMMENT_COMMENT_ID = "comment_id";

	public static final String HBASE_COLUMN_COMMENT_COMMENT_ID = "comment_id";
	public static final String HBASE_COLUMN_COMMENT_DATE = "date";
	public static final String HBASE_COLUMN_COMMENT_CONTENT = "content";

}