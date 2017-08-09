package com.fhd.fdc.commons.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fhd.core.dao.Page;
import com.fhd.sys.business.log.BusinessLogBO;

/**
 * LogAroundInterceptor拦截器.
 * @author 吴德福
 * @since 2013-10-12
 */
@Aspect
@Component
public class LogAroundInterceptor{
	
	@Autowired
	private BusinessLogBO o_businessLogBO;
	
	private final Log logger = LogFactory.getLog(LogAroundInterceptor.class);
	
	@Around(value="execution(* com.fhd..*.*BO.*(..)) and !execution(* com.fhd.sys.business.log.*BO.*(..))")
	public Object invoke(ProceedingJoinPoint jointPoint) throws Throwable {
		String className = jointPoint.getTarget().getClass().getName();
		String methodName = jointPoint.getSignature().getName();
		
		logger.info("类'"+ className +"'执行了'"+methodName+"'方法");
		
		//方法返回值
		Object proceed = null;
		
		//操作结果:成功/失败--根据方法返回结果判断
		String isSuccess = "成功";
		try {
			proceed = jointPoint.proceed();
		} catch (Exception e) {
			isSuccess = "失败";
			throw e;
		}
		
		//若拦截的整个类上有RecordLog注解，则整个类的方法都记录日志
		if(jointPoint.getTarget().getClass().isAnnotationPresent(RecordLog.class)){
			//若存在就获取注解
			RecordLog recordLog = (RecordLog) jointPoint.getTarget().getClass().getAnnotation(RecordLog.class);
			
			Method[] methods = jointPoint.getTarget().getClass().getDeclaredMethods();
			for (Method method : methods) {
				if(method.getName().equals(methodName)) {
					//操作类型
					String operateType = "";
					if (method.getName().startsWith("save")) {
						operateType = "新增";
					} else if(method.getName().startsWith("merge")){
						operateType = "修改";
					}else if(method.getName().startsWith("remove")){
						operateType = "删除";
					}else if(method.getName().startsWith("find")) {
						operateType = "查询";
					}
					
					//根据自定义注解@RecordLog的value属性获取模块名称
					String moduleName = recordLog.value();
					
					//操作记录
					String operateRecord = this.generateOperateRecord(jointPoint, method);
					
					o_businessLogBO.saveBusinessLogByLogAroundInterceptor(operateType, moduleName, isSuccess, className, methodName, operateRecord);
				}
			}
		}else{
			//若拦截的整个类上没有RecordLog注解，则验证类中添加RecordLog注解的方法记录日志
			Method[] methods = jointPoint.getTarget().getClass().getDeclaredMethods();
			for (Method method : methods) {
				//匹配自定义注解，记录日志
				if(method.getName().equals(methodName) && method.isAnnotationPresent(RecordLog.class)){
					//操作类型
					String operateType = "";
					if (method.getName().startsWith("save")) {
						operateType = "新增";
					} else if(method.getName().startsWith("merge")){
						operateType = "修改";
					}else if(method.getName().startsWith("remove")){
						operateType = "删除";
					}else if(method.getName().startsWith("find")) {
						operateType = "查询";
					}
					
					//根据自定义注解@RecordLog的value属性获取模块名称
					RecordLog recordLog = method.getAnnotation(RecordLog.class);
					String moduleName = recordLog.value();
					
					//操作记录
					String operateRecord = this.generateOperateRecord(jointPoint, method);
					
					o_businessLogBO.saveBusinessLogByLogAroundInterceptor(operateType, moduleName, isSuccess, className, methodName, operateRecord);
				}
			}
		}
		return proceed;
	}
	/**
	 * 生成业务日志操作记录字段.
	 * @param jointPoint
	 * @param method
	 * @return String
	 */
	public String generateOperateRecord(ProceedingJoinPoint jointPoint, Method method){
		//操作记录
		StringBuilder sb = new StringBuilder();
		Object[] args = jointPoint.getArgs();
		if(args.length>0){
			sb.append("[");
			int i=0;
			for (Object object : args) {
				if(null != object){
					if (object instanceof Page) {
						//得到泛型化的参数类型，返回的是一个数组，因为一个方法可能有多个泛型化的参数类型
						Type[] genericParameter = method.getGenericParameterTypes();
						for (Type type : genericParameter) {
							if(type instanceof ParameterizedType){
								/*
								 * 这里方法apply只有一个泛型化的参数类型，所以取下标为0，
								 * ParameterizedType代表一个参数化的类型，例如Page<ThemeAnalysis>
								 */
								ParameterizedType parameterizedType = (ParameterizedType) type;
								/*
								 * parameterizedType.getActualTypeArguments()返回的是实际类型的数组，
								 * 因为可能有多个如：Map<k,v>这里只有一个，所以取下标0
								 */
								String genericClassName = parameterizedType.getActualTypeArguments()[0].toString();
								sb.append("entity:'").append(genericClassName.substring(genericClassName.lastIndexOf(" ")+1, genericClassName.length())).append("'");
								break;
							}else{
								Method[] declaredMethods = type.getClass().getDeclaredMethods();
								int k=0;
								for (Method typeMethod : declaredMethods) {
									if("getName".equals(typeMethod.getName())){
										try {
											Object fieldValue = typeMethod.invoke(type);
											if(k!=declaredMethods.length-1){
												sb.append(",");
											}
											if(!object.getClass().isPrimitive()){
												sb.append("entity:'").append(fieldValue).append("'");
											}
										} catch (IllegalAccessException e) {
											e.printStackTrace();
										} catch (IllegalArgumentException e) {
											e.printStackTrace();
										} catch (InvocationTargetException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} else if(object instanceof String){
						sb.append("'").append(object).append("'");
					} else if(object instanceof Integer){
						sb.append("'").append(object).append("'");
					} else if(object instanceof Boolean){
						sb.append("'").append(object).append("'");
					} else {
						Object id = this.findDeclaredMethodValue(object, "getId", String.class);
						if(null != id){
							sb.append("id:'").append(String.valueOf(id)).append("'");
						}
						Method[] declaredMethods = object.getClass().getDeclaredMethods();
						int j=0;
			        	for (Method objectMethod : declaredMethods) {
			        		objectMethod.setAccessible(true);
							if(objectMethod.getName().toLowerCase().startsWith("get") && objectMethod.getName().toLowerCase().endsWith("name")){
								try {
									Object fieldValue = objectMethod.invoke(object);
									if(j!=declaredMethods.length-1){
										sb.append(",");
									}
									sb.append(objectMethod.getName()).append(":'").append(String.valueOf(fieldValue)).append("'");
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
							j++;
						}
					}
					if(i != args.length-1){
						sb.append(",");
					}
				}else{
					sb.append("'").append("null").append("'");
					if(sb.length()>0){
						sb.append(",");
					}
				}
				i++;
			}
			sb.append("]");
		}else{
			sb.append("没有参数!");
		}
		
		return sb.toString();
	}
	/**
	 * 循环向上转型, 获取对象的DeclaredMethod的值
	 * @param object : 子类对象
	 * @param methodName : 子类或父类中的方法名
	 * @param parameterTypes : 子类或父类中的方法参数类型
	 * @return 子类或父类方法执行的返回值.
	 */
	public Object findDeclaredMethodValue(Object object, String methodName, Class<?> ... parameterTypes){
        for(Class<?> clazz = object.getClass();clazz != Object.class;clazz = clazz.getSuperclass()) {
        	Method[] declaredMethods = clazz.getDeclaredMethods();
        	for (Method method : declaredMethods) {
        		method.setAccessible(true);
				if(method.getName().equals(methodName)){
					try {
						Object invoke = method.invoke(object);
						return invoke;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
        }    
        return null;    
	}
}