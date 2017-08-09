package com.fhd.fdc.commons.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解@RecordLog
 * @author 吴德福
 * @since 2013-10-12
 */
@Target({ElementType.TYPE,ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)  
public @interface RecordLog {  
	//注解值--模块名称
	String value();
}