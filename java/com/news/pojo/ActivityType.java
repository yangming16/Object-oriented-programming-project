package com.news.pojo;

public enum ActivityType {
	BROWSED_NEWS(1),
	SEARCH_NEWS(2),
	COMMENT_NEWS(3),
	UP_NEWS(4),
	SHARE_NEWS(5),
    LOGIN(6),
    LOGOUT(7),
	REG(8);
	
	private int activity;

    private ActivityType(int activity) {
        this.activity = activity;
    }
    
    public static ActivityType getType(int activity) {
        ActivityType type = null;
        for (ActivityType at : ActivityType.values()) {
            if (activity == at.activity) {
                type = at;
                break;
            }
        }
        return type;
    }
    
    public int getValue() {
        switch (this) {
	        case BROWSED_NEWS:
	            activity = 1;
	            break;
	        case SEARCH_NEWS:
	            activity = 2;
	            break;
	        case COMMENT_NEWS:
	        	activity = 3;
	            break;
	        case UP_NEWS:
	        	activity = 4;
	            break;
	        case SHARE_NEWS:
	        	activity = 5;
	            break;
	        case LOGIN:
	            activity = 6;
	            break;
	        case LOGOUT:
	            activity = 7;
	            break;
	        case REG:
	        	activity = 8;
	        	break;
        }
        return activity;
    }
    
    public static void main(String[] args) {
        ActivityType type = ActivityType.SEARCH_NEWS;
        System.out.println(type.getValue());
    }
}
