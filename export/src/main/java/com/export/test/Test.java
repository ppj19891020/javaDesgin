package com.export.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();  
        
        /* HOUR_OF_DAY 指示一天中的小时 */  
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 45);  
          
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
          
        System.out.println("45分钟后的时间：" + df.format(calendar.getTime())+"   :"+calendar.getTimeInMillis());  
        System.out.println("当前的时间：" + df.format(new Date())+"   :"+System.currentTimeMillis()); 
        
        String ids = "1";
        String[] values = ids.split(",");
        List<String> list = Arrays.asList(values);
        if(list.contains("1")){
            System.out.println("true");
        }else {
            System.out.println("false");
        }
        
	}

}
