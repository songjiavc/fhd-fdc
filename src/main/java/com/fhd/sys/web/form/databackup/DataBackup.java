package com.fhd.sys.web.form.databackup;


public class DataBackup  implements Comparable<DataBackup> {
	private String id;//ID
	private String fileName;//备份文件的名称
	private String fileDate;//备份文件的日期
	private String filePath;//备份文件的地址
	private String fileSize;//备份文件的大小
	private String dbName;//数据库名称
	private String host;//host
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDate() {
		return fileDate;
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	@Override
	public int compareTo(DataBackup o) {
		if (null == o) {
			return 1;  
		} else {  
            return o.fileDate.compareTo(this.fileDate);  
        }  
	}
	
}
