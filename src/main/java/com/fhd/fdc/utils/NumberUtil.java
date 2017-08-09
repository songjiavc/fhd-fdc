package com.fhd.fdc.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;


public class NumberUtil {
	
	private static  Logger LOGGER = Logger.getLogger(NumberUtil.class);
	
	public static String getDoubleTostring(Double dv){
		DecimalFormat df=new DecimalFormat("#.#");
		try{
			return df.format(dv);
		}catch(Exception e){
			return df.format(0);
		}
	}
	public static Double getStringToDouble(String str){
		try{
			return Double.valueOf(str.replace(",", ""));
		}catch(Exception e){
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception{
		LOGGER.info(getDoubleTostringByData(new Double(1)));
	}
	
	public static String getDoubleTostringByData(Double dv){
		DecimalFormat df = new DecimalFormat( "#,###,###.##"); 
		try{
			 double d = BigDecimal.valueOf(dv).doubleValue();
			 return df.format(d); 
		}catch(Exception e){
			return "";
		}
	}
	public static int getStringToInt(String str){
		try{
			return Integer.parseInt(str);
		}catch(Exception e){
			return 0;
		}
	}
	/**
	 * <pre>
	 * meg:四舍五入，保留一位小数
	 * </pre>
	 * 
	 * @author David
	 * @param input
	 * @param length 小数点位数
	 * @param x 想保留的小数点位数 
	 * @return
	 * @since  fhd　Ver 1.1
	 * @updateTime 2013-08-06 邓广义
	 */
	public static Double meg(Double input,Integer x) {
		if(input==null){
			return 0d;
		}
		double n = 100.0;
		if(x>=0){
			n = Math.pow(10, x);
		}
		int b = (int) Math.round(input * n); // 小数点后两位前移，并四舍五入
		return ((double) b / n); // 还原小数点后两位
	}
}
