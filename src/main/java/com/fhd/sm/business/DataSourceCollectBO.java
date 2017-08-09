/**
 * 
 */
package com.fhd.sm.business;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.TimePeriodBO;
import com.fhd.entity.kpi.KpiDataSource;
import com.fhd.sm.business.JdbcTemplateBO;
import com.fhd.sm.business.KpiDataSourceBO;

/**
 * @author wangxin
 *
 */
@Service
public class DataSourceCollectBO {
	  @Autowired
      private JdbcTemplateBO o_jdbcTemplateBO;
	  
	  @Autowired
	  private KpiDataSourceBO o_kpiDataSourceBO;
	  
	  @Autowired
	  private TimePeriodBO o_timePeriodBO;
	  
	  @Autowired
	  private KpiDsParameterBO o_kpiDsParameterBO;
	  
	  private static Log logger = LogFactory.getLog(DataSourceCollectBO.class);
	  
	  
	  /**
	   * 
	   * @param kpiId
	   * @param kpiName
	   * @param kpiDsType
	   * @param kpiDsName
	   * @param formula
	   * @param timePeriodId
	   * @return 公式计算结果
	   */
	  @SuppressWarnings("rawtypes")
	public String executeConfigDetails(String kpiId,String kpiName,String kpiDsType,String kpiDsName,String formula,String timePeriodId,String valueType){
		  if("03".equals(kpiDsType)) {
			  int i = formula.lastIndexOf('.');
			  String method = formula.substring(i+1);
			  String classNameString = formula.substring(0,i);
			  List<Map<String, Object>> list = o_kpiDsParameterBO.findDsParameterBySome(kpiId, valueType);
			  try {
				Class c = Class.forName(classNameString);
				Method[] methods = c.getDeclaredMethods();
				for(int j= 0;j< methods.length;j++) {
					if(methods[j].getName().equalsIgnoreCase(method)){
					   Class<?>[] parameterTypes = methods[j].getParameterTypes();
					   if(list.size() != parameterTypes.length) {
						   logger.error("参数个数不匹配");
						   return "参数个数不匹配";
					   }
					   if(list.size() > 0) {						   
						   Object[] obj = new Object[list.size()]; 
						   int k = 0;
					       for(Map<String, Object> map: list) {
					    	  obj[k] = map.get("parameterValue");
					    	  k++;
					       }						   
				       return  (String) methods[j].invoke(c.newInstance(), obj);						   
					   } else {
						   return (String) methods[j].invoke(c.newInstance());
					   }
                      
					}
				}
				logger.error("方法未找到");
				return "方法未找到";
			} catch (Exception e) {
				logger.error("本地方法调用错误,异常信息["+e.toString()+"]");
				logger.error("本地方法入参类型为String或Object,方法返回类型为String");
				return "本地方法调用错误";
			}
			  //本地方法
		  } else {
			  if(StringUtils.isBlank(kpiDsName) || StringUtils.isBlank(formula)) {
				  logger.error("数据源参数缺失");
				  return "数据源参数缺失";
			  } else {
				if("01".equals(kpiDsType)) {
					return executeSqlInRemoteDataBase(kpiDsName,formula,timePeriodId);
				} else {
					return executeProcedureInRemoteDataBase(kpiDsName,formula,timePeriodId);
				}
			  }
		  }
	  }
	  
	  /**
	   * 
	   * @param kpiDsName 数据源名称
	   * @param formula sql语句
	   * @return sql语句执行结果
	   */
	@SuppressWarnings("deprecation")
	public String executeSqlInRemoteDataBase(String kpiDsName,String formula,String timeperiodId) {
		try {
			  KpiDataSource kpiDs = o_kpiDataSourceBO.findDataSourceById(kpiDsName);
			  Map<String, Object> map = kpiDs.createConnectionMap();
			  o_jdbcTemplateBO.setDataSource(map);
			  if(formula.lastIndexOf("采集时间") != -1) {
				  formula = formula.replace("采集时间", "?");
				  formula = formula.replace("&nbsp;", " ");
				  Date gatherTimeDate =  o_timePeriodBO.findTimePeriodById(timeperiodId).getStartTime();
				  java.sql.Date inputParameter = new java.sql.Date(gatherTimeDate.getYear(),gatherTimeDate.getMonth(),gatherTimeDate.getDate());
				  return o_jdbcTemplateBO.executeSql(formula,inputParameter,inputParameter);	
			  } else {
				  return o_jdbcTemplateBO.executeSql(formula);
			  }
		} catch (Exception e) {
			String errMsg = "执行远程sql异常:异常信息["+e.toString()+"]";
			logger.error(errMsg);
			return errMsg; 
		}
	  }
	  
	  /**
	   * 
	   * @param kpiDsName
	   * @param formula
	   * @return 存储过程执行结果
	   */
	@SuppressWarnings("deprecation")
	public String executeProcedureInRemoteDataBase(String kpiDsName,String formula,String timePeriodId) {
		  KpiDataSource kpiDs = o_kpiDataSourceBO.findDataSourceById(kpiDsName);
		  Map<String, Object> map = kpiDs.createConnectionMap();
		  o_jdbcTemplateBO.setDataSource(map);
		  int indexStart = formula.indexOf('(')+1;
		  int indexEnd = formula.indexOf(')');
		  String parameters = formula.substring(indexStart, indexEnd);
		  String[] parameter = parameters.split(",");
		  Map<Integer,Object> paramMap = new HashMap<Integer, Object>();
		  int count = 1;
		  for(String param: parameter) {
			  Object inputParameter = null;
			  formula = formula.replace(param, "?");
              if(param.equals("采集时间")) {
				  java.util.Date gatherTimeDate =  o_timePeriodBO.findTimePeriodById(timePeriodId).getStartTime();
				  inputParameter = new java.sql.Date(gatherTimeDate.getYear(),gatherTimeDate.getMonth(),gatherTimeDate.getDate());
				  paramMap.put(count, inputParameter);
			  } else {
				  paramMap.put(count,param);
			  }			 
			  count++;
		  }
		  formula = formula.replace("&nbsp;", " ");
		  int index = parameter.length;
		  return o_jdbcTemplateBO.executeProcedure(formula,paramMap,index);
	  }
	
	    /**
	     * java正则表达式判断字符串是否是double类型.
	     * @param str
	     * @return boolean
	     */
	    public boolean isDoubleOrInteger(String str) {
	        return NumberUtils.isNumber(str);
	    }
}
