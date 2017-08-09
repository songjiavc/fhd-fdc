package com.fhd.sm.web.controller.util;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.fhd.fdc.utils.Contents;

public class Utils {
    public static String getValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = new DecimalFormat("0.00").format(Double.parseDouble(value));
        }
        return value;
    }
    
    /**根据小数点位数格式化数据
     * @param position 小数点位数
     * @param value 要格式化的值
     * @return
     */
    public static String getValue(Object value ,Integer position) {
    	if(value==null||"".equals(value)){
    		return "";
    	}else{
    		if(null==position){
    			position = Contents.DEFAULT_KPI_DOT_POSITION;
    		}
    		DecimalFormat df = new DecimalFormat("0");
    		df.setMaximumFractionDigits(position);
    		return df.format(Double.valueOf(String.valueOf(value)));
    	}
    	
    }

    /**判断是否为数字字符串
     * @param str 数字字符串
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]*.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    

	/** 
	  * @Description: 计算给定的数学表达式
	  * @author jia.song@pcitc.com
	  * @date 2017年4月24日 上午10:45:45 
	  * @param str
	  * @return 
	  */
	public static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
	
	/** 
	  * @Description: 将double 转为保留两位小数
	  * @author jia.song@pcitc.com
	  * @date 2017年4月25日 下午1:44:33 
	  * @param m
	  * @return 
	  */
	public static String retain2Decimals(double m){
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(m);
	}
	
	/**
	 * @author replace Chinese to id 
	 * @param caluFormula
	 * @param paramMap
	 * @return
	 */
	public static String replaceWeightToNum(String caluFormula,Map<String,String> paramMap){
	    String rtnStr = caluFormula;
	    if(paramMap.size() > 0 && paramMap != null){
			for(String key : paramMap.keySet()) {
			    rtnStr = rtnStr.replaceAll(key,paramMap.get(key));
			}
	    }
	    return rtnStr;
	}
	
	
	/*
	
	public static void main(String[] args){
	    
	    System.out.println(eval("sqrt(3.3*3.3+4.2*4.2) / 2.1")); 
	}
	*/    
}