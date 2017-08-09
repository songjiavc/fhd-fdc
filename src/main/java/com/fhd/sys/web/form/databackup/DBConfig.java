package com.fhd.sys.web.form.databackup;

public class DBConfig {
	private String dataBackupFolder;
	private String mysqlPath;
	private String username; 
	private String password; 
	private String host; 
	private String port; 
	private String dbname;
	
	public String getDataBackupFolder() {
		return dataBackupFolder;
	}
	public void setDataBackupFolder(String dataBackupFolder) {
		this.dataBackupFolder = dataBackupFolder;
	}
	public String getMysqlPath() {
		return mysqlPath;
	}
	public void setMysqlPath(String mysqlPath) {
		this.mysqlPath = mysqlPath;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
}
