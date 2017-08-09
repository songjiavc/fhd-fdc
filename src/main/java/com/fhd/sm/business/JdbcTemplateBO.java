package com.fhd.sm.business;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fhd.dao.kpi.JdbcTemplateDAO;
/**
 * JDBC模板 ClassName:JdbcTemplateBO
 * 
 * @author 王鑫
 * @version
 * @since Ver 1.1
 * @Date 2013 2013-7-26
 * 
 * @see
 */
@Service
public class JdbcTemplateBO  {
	
	@Autowired
	private JdbcTemplateDAO o_jdbcTemplateDao;
	
    private static Log log = LogFactory.getLog(DataScImportBO.class);
	
	public void setDataSource(Map<String, Object> map){
		o_jdbcTemplateDao.setDataSource(map);
	}	
    
	/**
	 * 连接测试
	 * @return 连接测试结果
	 * @throws SQLException 
	 */
	public boolean connectionTest() {
		Connection conn  = null;
         try {
			//dc.setLoginTimeout((Integer) map.get("loginTimeOut"));
			conn = o_jdbcTemplateDao.getConnection();			
		  } catch (SQLException e) {
			  log.error("获取连接失败:exception====="+e.toString());
		  }
         if (null!=conn) {
 			try{
 				conn.close();
 			} catch(SQLException e) {
 			  log.error("关闭连接失败:exception====="+e.toString());
 			}
 			return true;
 		}
         
		return false;
	}
	
	/**
	 * 执行带参数的sql语句
	 * @param sql 执行的sql语句
	 * @param args
	 * @return sql语句采集结果
	 */
	public String executeSql(String sql, Object... args){
		List<Map<String, Object>> list = o_jdbcTemplateDao.queryForList(sql, args);
		if(list.size() >0) {
		   Map<String, Object> map = list.get(0);
		   if(map.values().size() > 0) {
			   Object[] value = map.values().toArray();
			   return  String.valueOf(value[0]);
		   }		   
		}
		return null;
	}
	
	
	/**
	 * 执行带参数的sql语句
	 * @param sql 执行的sql语句
	 * @param args
	 * @return sql语句采集结果
	 */
	public List<Map<String, Object>> executeSqls(String sql, Object... args){
		return  o_jdbcTemplateDao.queryForList(sql, args);
	}
	
	/**
	 * 执行存储过程 
	 * @param formular 
	 * @param index
	 * @return 存储过程返回采集指标值
	 */
	
	public String executeProcedure(String formular,Map<Integer,Object> map,int index) {
		return o_jdbcTemplateDao.executeProcedure(formular,map,index);
		
	}
	
	/**
	 * 返回连接桥属性
	 * @return JdbcTemplate
	 * */
	public JdbcTemplate getJdbcTemplate(){
		return o_jdbcTemplateDao.getO_jdbcTemplate();
	}
}