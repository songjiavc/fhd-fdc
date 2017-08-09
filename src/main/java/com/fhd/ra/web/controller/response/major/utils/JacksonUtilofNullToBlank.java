package com.fhd.ra.web.controller.response.major.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
   
 
   
   
/**
* @ClassName: JacksonUtils
* @Description: jackson工具类
* @author:Jzq
* @date: 2016-1-12 上午9:55:21
*/ 
public class JacksonUtilofNullToBlank {  
	 private static final Logger logger = LoggerFactory.getLogger(JacksonUtilofNullToBlank.class);
	 private static final ObjectMapper objectMapper;  
	    static {  
	        objectMapper = new ObjectMapper();  
	        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
				@Override
				public void serialize(Object arg0, JsonGenerator arg1,
						SerializerProvider arg2) throws IOException,
						JsonProcessingException {
						arg1.writeString("");
				}
			});
	        //去掉默认的时间戳格式  
	        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  
	        //设置为中国上海时区  
	        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));  
	        //序列化时，日期的统一格式  
	        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));  
	        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);  
	        //单引号处理  
	        objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);  
	    }  
	  
	    /** 
	    * @Title: toObject
	    * @Description: json转对象
	    * @param json
	    * @param clazz
	    * @return T
	    * @throws
	    * @date : 2016-1-12 上午9:54:11
	    */ 
	    public static <T> T toObject(String json, Class<T> clazz) {  
	        try {  
	            return objectMapper.readValue(json, clazz);  
	        } catch (JsonParseException e) {  
	            logger.error(e.getMessage(), e);  
	        } catch (JsonMappingException e) {  
	            logger.error(e.getMessage(), e);  
	        } catch (IOException e) {  
	            logger.error(e.getMessage(), e);  
	        }  
	        return null;  
	    }  
	  
	    /** 
	    * @Title: toJson
	    * @Description: 对象转json
	    * @param entity
	    * @return String
	    * @throws
	    * @date : 2016-1-12 上午9:53:29
	    */ 
	    public static <T> String toJson(T entity) {  
	        try {  
	            return objectMapper.writeValueAsString(entity);  
	        } catch (JsonGenerationException e) {  
	            logger.error(e.getMessage(), e);  
	        } catch (JsonMappingException e) {  
	            logger.error(e.getMessage(), e);  
	        } catch (IOException e) {  
	            logger.error(e.getMessage(), e);  
	        }  
	        return null;  
	    }  
	    /** 
	    * @Title: toCollection
	    * @Description: JSON 转集合
	    * @param json
	    * @param typeReference   TypeReference<Map<String,Object>>() {} 或者new TypeReference<List<Object>>() {}
	    * @return T
	    * @throws
	    * @date : 2016-1-12 上午9:52:34
	    */ 
	    public static <T> T toCollection(String json, TypeReference<T> typeReference) {  
	        try {  
	            return objectMapper.readValue(json, typeReference);  
	        } catch (JsonParseException e) {  
	            logger.error(e.getMessage(), e);  
	        } catch (JsonMappingException e) {  
	            logger.error(e.getMessage(), e);  
	        } catch (IOException e) {  
	            logger.error(e.getMessage(), e);  
	        }  
	        return null;  
	    }  
	  
   
}