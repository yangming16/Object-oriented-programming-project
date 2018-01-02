package recommend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.news.basedao.HbaseDB;
import com.news.util.Constants_News;

public class DateTime {
	public static int CalculateTime(String newsDate){
		int time = 1000;
		Date now = new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("MM-dd");	
		try {
			String start = formatter.format(now);
			Date startTime = formatter.parse(start);

			
			Date date =  formatter.parse(newsDate);
			long timediff =  (startTime.getTime() - date.getTime())/(1000*3600*24);
			if(timediff<0){
				timediff+=365;
			}
			time = new Long(timediff).intValue();
			//System.out.println("ʱ����������"+time);
		} catch (ParseException e) {
		//	time=100;
			e.printStackTrace();
		}
		return time;
	}
	public static void main(String[] args) {
		Date now = new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("MM-dd");	
		String newsDate = "11-16";
		try {
			String start = formatter.format(now);
			Date startTime = formatter.parse(start);

			Date date =  formatter.parse(newsDate);
			long timediff =  (startTime.getTime() - date.getTime())/(1000*3600*24);
			if(timediff<0){
				timediff+=365;
			}
			int time = new Long(timediff).intValue();
			//int time2 = new Long(time).intValue();
			System.out.println(time);
			//System.out.println(time2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
}
