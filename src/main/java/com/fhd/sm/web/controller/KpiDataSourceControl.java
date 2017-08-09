package com.fhd.sm.web.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.Identities;
import com.fhd.entity.kpi.KpiDataSource;
import com.fhd.sm.business.JdbcTemplateBO;
import com.fhd.sm.business.KpiDataSourceBO;
import com.fhd.sm.web.form.KpiDataSourceForm;


@Controller
public class KpiDataSourceControl {
	@Autowired
	private KpiDataSourceBO o_kpiDataSourceBO;
	@Autowired
	private JdbcTemplateBO o_jdbcTmeplateBO;
	
	private static Log logger = LogFactory.getLog(KpiDataSourceControl.class);
	
	/**
	 * 加载数据源节点
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/dataSource/findKpiDataSource.f")
	public List<Map<String,Object>> loadDataSourceNode(String query){
		return o_kpiDataSourceBO.loadDataSourceNode(query);
	}
	
	/**
	 * 
	 * @param id 数据源id
	 * @return 对应的数据源信息
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/dataSource/findDataSourcebyid.f")
	public Map<String, Object> findDataSourcebyid (String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		KpiDataSource ds = o_kpiDataSourceBO.findDataSourceById(id);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("driverName", ds.getDriverName());
		dataMap.put("ip", ds.getIp());
		dataMap.put("dataBaseName",ds.getDataBaseName());
		dataMap.put("port", ds.getPort());
		dataMap.put("userName", ds.getUserName());
		dataMap.put("passWord", ds.getPassWord());
		dataMap.put("dbType", ds.getDbType());
		map.put("data", dataMap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 
	 * @param form 表单信息
	 * @param id 数据源id
	 * @return 保存结果
	 */
	@ResponseBody
	@RequestMapping(value ="/kpi/dataSource/saveDataSource.f")
	public Map<String, Object> saveDataSource(KpiDataSourceForm form,String id){
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		KpiDataSource ds = new KpiDataSource(Identities.uuid());
		ds.setDriverName(form.getDriverName());
		ds.setIp(form.getIp());
		ds.setPort(form.getPort());
		ds.setDataBaseName(form.getDataBaseName());
		ds.setUserName(form.getUserName());
		ds.setPassWord(form.getPassWord());
		ds.setDbType(form.getDbType());
		try {
			if(StringUtils.isNotBlank(id)){
				ds.setId(id);
				o_kpiDataSourceBO.save(ds);
				inmap.put("isSave", "true");
				inmap.put("id", ds.getId());
				inmap.put("text", ds.getDriverName());
				inmap.put("leaf", true);
				inmap.put("expanded", false);
			
			} else {		
					o_kpiDataSourceBO.save(ds);
				    inmap.put("isSave", "true");
					inmap.put("id", ds.getId());
					inmap.put("text", ds.getDriverName());
					inmap.put("leaf", true);
					inmap.put("expanded", false);			
			}			
		} catch(Exception e) {			
			logger.error("error:["+e.toString()+"]");
			e.printStackTrace();
		}
		map.put("data",inmap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 
	 * @param 数据源ID
	 * @return 删除结果（是否成功）
	 */
	@ResponseBody
	@RequestMapping(value ="/kpi/dataSource/removeDataSourceById.f")
	public Boolean removeDataSourceById(String id) {
		if(StringUtils.isNotBlank(id)){			
			o_kpiDataSourceBO.deleteById(id);
			return true;
		} else {
			return false;
		}
		
	}
	/**
	 *  查找所有已配置数据源
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/dataSource/findAllDataSource.f") 
	public Map<String, Object> findAllDataSource() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = o_kpiDataSourceBO.loadDataSourceNode(null);
		map.put("datas", list);
		return map;

	}
	
	/**
	 *  数据库连接测试
	 */
	@ResponseBody
	@RequestMapping(value = "kpi/dataSource/dataBaseConnectionTest.f")
	public Boolean  dataBaseConnectionTest(String values) {
		Map<String, Object> dsMap = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(values)){
			JSONObject jsonObject = JSONObject.fromObject(values);
			if ("mySQL".equalsIgnoreCase((String)jsonObject.get("dbType"))) {
				dsMap.put("driverClassName", "com.mysql.jdbc.Driver");
				dsMap.put("userName", (String)jsonObject.get("userName"));
				dsMap.put("passWord", (String)jsonObject.get("passWord"));
				StringBuffer dbUrl = new StringBuffer("jdbc:mysql://");
				dbUrl.append((String)jsonObject.get("ip"));
				dbUrl.append(":");
				dbUrl.append((String)jsonObject.get("port"));
				dbUrl.append("/");
				dbUrl.append((String)jsonObject.get("dataBaseName"));
				dsMap.put("url", dbUrl.toString());
				o_jdbcTmeplateBO.setDataSource(dsMap);
				return o_jdbcTmeplateBO.connectionTest();
			} else if("oracle".equalsIgnoreCase((String)jsonObject.get("dbType"))) {
				dsMap.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
				dsMap.put("userName",(String)jsonObject.get("userName"));
				dsMap.put("passWord", (String)jsonObject.get("passWord"));
				StringBuffer dbUrl = new StringBuffer("jdbc:oracle:thin:@");
				dbUrl.append((String)jsonObject.get("ip"));
				dbUrl.append(":");
				dbUrl.append((String)jsonObject.get("port"));
				dbUrl.append(":").append((String)jsonObject.get("dataBaseName"));
				dsMap.put("url", dbUrl.toString());
				o_jdbcTmeplateBO.setDataSource(dsMap);
				return o_jdbcTmeplateBO.connectionTest();
			}
		}
		    return false;
	}
	
	/**
	 * 本地方法验证
	 * @param methodPath
	 * @return 验证结果
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value ="kpi/dataSource/validateMethodPath.f")
	public boolean validatePath(String methodPath,int paramSize) {
		  int i = methodPath.lastIndexOf('.');
		  String method = methodPath.substring(i+1);
		  String classNameString = methodPath.substring(0,i);
		  try{
			  Class c = Class.forName(classNameString);
			  Method[] methods = c.getDeclaredMethods();
			  for(int k=0;k< method.length();k++) {
				  if(methods[k].getName().equalsIgnoreCase(method)){
					  Class<?>[] parameterTypes = methods[k].getParameterTypes();
					  if(parameterTypes.length == paramSize){
						  logger.error("验证类信息:成功!");
						  return true;
					  }					  
				  }
			  }
		  } catch(Exception e) {
			   logger.error("验证类异常信息:["+e.toString()+"]");
			   return false;
		  }
		return false;
	}
	
}
