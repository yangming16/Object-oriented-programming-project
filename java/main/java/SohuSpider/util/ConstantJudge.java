package main.java.SohuSpider.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConstantJudge {
	
	 String encoding = "UTF-8";  
	
	 public static void main(String[] args) throws Exception {  
		 System.out.println(findStringInFile("556287972"));  
	    } 
			
	public static boolean findStringInFile(String path) throws IOException{  
        File file = new File("queue.txt");  
        InputStreamReader read = new InputStreamReader(new FileInputStream(file),"UTF-8");//考虑到编码格式  
        BufferedReader bufferedReader = new BufferedReader(read);  
        String line = null;  
        while ((line = bufferedReader.readLine()) != null) {  
//            if(line.startsWith("#")){  
//                continue;  
//            }  
            //指定字符串判断处  
            if (line.contains(path)) {  
                System.out.println(path);  
                return true;
            }  
        }  
        return false;
    }  
}
