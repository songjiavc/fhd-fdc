package com.fhd.entity.kpi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import com.fhd.entity.base.IdEntity;
/**
 * 本地方法参数信息
 *
 * @author   王鑫
 * @since    fhd Ver 4.5
 * @Date	 2013-7-24
 *
 * @see 	 
 */
@Entity
@Table(name = "T_KPI_DATASOURCE")
public class KpiDataSource extends IdEntity implements Serializable {
	
    private static final long serialVersionUID = 2756564806248230798L;
    /**
     *  驱动名称
     */
  	@Column(name = "DRIVER_NAME")
  	private String driverName;
  	
	/**
  	 *  IP
  	 */
	@Column(name = "IP")
	private String ip;
	
	/**
	 *  端口
	 */
	@Column(name = "PORT")
	private String port;
	
	/**
	 *  数据库名称
	 */
	@Column(name = "DATABASE_NAME")
	private String dataBaseName;
	/**
	 * 用户名
	 */
	@Column(name = "USER_NAME")
	private String userName;
	
	/**
	 *  登陆密码
	 */
	@Column(name = "PWD")
	private String passWord;
	

	
	/**
	 * 数据库类型
	 */
	@Column(name = "DB_TYPE")
	private String dbType;
      
      public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
 	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dabaseName) {
		this.dataBaseName = dabaseName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public Map<String, Object> createConnectionMap() {
		Map<String, Object> dsMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(this.dbType) && "mySQL".equalsIgnoreCase(this.dbType)) {
			dsMap.put("driverClassName", "com.mysql.jdbc.Driver");
			dsMap.put("userName",this.getUserName());
			dsMap.put("passWord", this.getPassWord());
			StringBuffer dbUrl = new StringBuffer("jdbc:mysql://");
			dbUrl.append(this.getIp());
			dbUrl.append(":");
			dbUrl.append(this.getPort());
			dbUrl.append("/");
			dbUrl.append(this.getDataBaseName());
			dsMap.put("url", dbUrl.toString());
		} else if(StringUtils.isNotBlank(this.dbType) && "oracle".equalsIgnoreCase(this.dbType)) {
			dsMap.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
			dsMap.put("userName",this.getUserName());
			dsMap.put("passWord", this.getPassWord());
			StringBuffer dbUrl = new StringBuffer("jdbc:oracle:thin:@");
			dbUrl.append(this.getIp());
			dbUrl.append(":");
			dbUrl.append(this.getPort());
			dbUrl.append(":").append(this.getDataBaseName());
			dsMap.put("url", dbUrl.toString());
		}
		return dsMap;
	}
	
	public KpiDataSource(){
		
	}
	
	public KpiDataSource(String id){
		setId(id);
	}
	 
}
