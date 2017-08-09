package com.fhd.dao.kpi;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;


@Repository
public class JdbcTemplateDAO {

	@Autowired
    private JdbcTemplate o_jdbcTemplate;
    
    private DriverManagerDataSource dc;
    
    public JdbcTemplateDAO() {
    	
    }
    /**
     * 
     * @param map 注入数据源
     */
    
    public void setDataSource(Map<String, Object> map) {
    	dc = new DriverManagerDataSource();
		dc.setDriverClassName((String) map.get("driverClassName"));
        dc.setUrl((String) map.get("url"));
        dc.setUsername((String) map.get("userName"));
        dc.setPassword((String) map.get("passWord"));
        o_jdbcTemplate.setDataSource(dc);
    }
    
    /**
     * 
     * @return 打开连接
     * @throws SQLException
     */
	public Connection getConnection() throws SQLException{
		return this.dc.getConnection();
	}
    /**
     * 
     * @param sql
     * @param args
     * @return
     */
	public List<Map<String, Object>> queryForList(String sql, Object... args){
		return o_jdbcTemplate.queryForList(sql, args);
	}
	
	
	/**获得jdbc模板
	 * @return
	 */
	public JdbcTemplate getO_jdbcTemplate()
    {
        return o_jdbcTemplate;
    }
	
    
	/**
	 * 
	 * @param sql
	 * @param index
	 * @return 存储过程计算所得数值
	 */
    public String executeProcedure(String sql,Map<Integer,Object> map,int index) {
    	try {
    		Connection conn = this.getConnection();
		    CallableStatement caller =  conn.prepareCall(sql);
			//caller.registerOutParameter(index, Types.INTEGER);
			Set<Map.Entry<Integer, Object>> keySet = map.entrySet();
			for(Map.Entry<Integer, Object> i:keySet ) {
				if(null != i.getValue()){
					if(i.getValue() instanceof String) {
						caller.setString(Integer.valueOf(i.getKey()), (String)i.getValue());
					}
					if(i.getValue() instanceof Date) {
						caller.setDate(Integer.valueOf(i.getKey()),(Date) i.getValue());
					}
					if(i.getValue() instanceof BigDecimal) {
						caller.setBigDecimal(Integer.valueOf(i.getKey()), (BigDecimal) i.getValue()); 
					}
				}
			}
			//caller.registerOutParameter(1, Types.INTEGER);
			caller.execute();
			String value = String.valueOf(caller.getBigDecimal(index));
			String errMessage = caller.getString("o_execMessage");
			caller.close();
			// 检查存储过程错误信息
			if(StringUtils.isNotBlank(errMessage)) {
				return errMessage;
			}
			conn.close();
			return value;
		} catch (SQLException e) { 
			return null;
		}
    }
}
