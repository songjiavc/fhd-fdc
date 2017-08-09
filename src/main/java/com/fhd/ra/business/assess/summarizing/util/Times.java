package com.fhd.ra.business.assess.summarizing.util;

import java.util.Calendar;

/**
 * 时间工具类
 * */

public class Times {

	/**
	 * 得到系统年
	 * @return static String
	 * @author 金鹏祥
	 * */
	public static String getYear() {
		Calendar c = Calendar.getInstance();

		return String.valueOf(c.get(Calendar.YEAR));
	}

	/**
	 * 得到系统月
	 * @return static String
	 * @author 金鹏祥
	 * */
	public static String getMonth() {
		Calendar c = Calendar.getInstance();
		int month = (c.get(Calendar.MONTH) + 1);
		String months = "";
		
		if(month < 10){
			months = "0" + month;
		}else{
			months = String.valueOf(month);
		}
		
		return months;
	}
}