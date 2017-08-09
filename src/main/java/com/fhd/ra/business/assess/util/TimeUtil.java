package com.fhd.ra.business.assess.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	/**
	 * 获得到24小时制当前事件
	 * @return static Date
	 * @author 金鹏祥
	 * */
	public static Date getCurrentTime() throws ParseException{
		Date date = new Date();
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
		String LgTime = sdformat.format(date);
		return sdformat.parse(LgTime);
	}
	
	 /**
     * 得到当前时间
     * @return static String
     * */
    public static String getCurrentTimeStr(){
    	Date d = new Date();
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	return sdf.format(d);
    }
    
    /**
     * 得到当前时间
     * @return static String
     * */
    public static String getCurrentTimeStrHMS(){
    	Date d = new Date();
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return sdf.format(d);
    }
    
    /**
     * 通过date获得时间
     * @param date DATE时间类型
     * @return static String
     * */
    public static String getFrequencyTime(Date date){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	return sdf.format(date);
    }
    
    /**
     * 时间转DATE时间
     * @param time 字符串时间类型
     * @return static Date
     * */
    public static Date getTime(String time){
    	Date date=null;
    	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
    	try {
			date = formatter.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return date;
    }
	
	public static void main(String[] args) throws ParseException {
		System.out.println(getCurrentTime());
	}
}
