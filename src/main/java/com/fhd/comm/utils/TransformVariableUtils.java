package com.fhd.comm.utils;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fhd.core.utils.SpringContextHolder;
import com.fhd.sys.business.st.task.TriggerEmailBO;

/**
 * 模板变量替换类.
 * @author 吴德福
 * @Date 2014-1-13 22:30:24
 */
public class TransformVariableUtils {

	private static final Logger logger = Logger.getLogger(TriggerEmailBO.class);
	
	/**
	 * 模板变量替换.
	 * @author 吴德福
	 * @param template 模板内容
	 * @param map 变量key-value集合
	 * @return String
	 */
	public String transformVariables(String template, Map<String,String> map){
		Set<Map.Entry<String, String>> set = map.entrySet();
		for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
			logger.info(entry.getKey() + "--->" + entry.getValue());
			if(template.contains(entry.getKey())){
				template = StringUtils.replace(template, entry.getKey(), entry.getValue());
			}
		}
		return template;
	}
	/**
	 * 根据类名和方法名执行类中的对应方法，返回方法执行结果.
	 * @author 吴德福
	 * @param className
	 * @param methodName
	 * @return String 方法执行结果
	 */
	public String invokeMethodBy(String className, String methodName) {
		String ret = "";
		try {
			//取类
			Class<?> addressesClass = Class.forName(className);
			Object bean = SpringContextHolder.getBean(addressesClass);
			//取类方法
			Method method = addressesClass.getMethod(methodName);
			//取结果
			ret = (String) method.invoke(bean);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		} 
		return ret;
	}
}