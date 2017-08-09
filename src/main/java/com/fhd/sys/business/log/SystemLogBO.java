package com.fhd.sys.business.log;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.dao.sys.log.SystemLogDAO;
import com.fhd.entity.sys.log.SystemLog;

/**
 * 系统日志BO类.
 * @author  吴德福
 * @version V1.0  创建时间：2013-10-12
 * Company FirstHuiDa.
 */
@SuppressWarnings("unchecked")
@Service
public class SystemLogBO {
	
	@Autowired
	private SystemLogDAO o_systemLogDAO;
	
	private static final Logger LOGGER = Logger.getLogger(SystemLogBO.class);
	
	/**
	 * 根据查询条件分页查询系统日志.
	 * @param page
	 * @param sort
	 * @param query
	 * @return Page<SystemLog>
	 */
	public Page<SystemLog> findSystemLogByPage(Page<SystemLog> page, String sort, String query){
		DetachedCriteria dc = DetachedCriteria.forClass(SystemLog.class);
		if(StringUtils.isNotBlank(query)){
			dc.add(Restrictions.or(Restrictions.or(Property.forName("logDate").like(query, MatchMode.ANYWHERE), Property.forName("logLevel").like(query, MatchMode.ANYWHERE)), Restrictions.or(Property.forName("location").like(query, MatchMode.ANYWHERE), Property.forName("message").like(query, MatchMode.ANYWHERE))));
		}
		dc.addOrder(Order.desc("logDate"));
		dc.addOrder(Order.desc("logLevel"));
		dc.addOrder(Order.asc("location"));
		return o_systemLogDAO.findPage(dc, page, false);
	}
	 
	
	/**
	 * 查找文件列表
	 * @param realPath 真实路径
	 * @return
	 * @throws IOException 
	 */
	public List<LogFile> findLogFile() throws Exception{
		List<LogFile> list = new ArrayList<LogFile>();
		SAXReader saxReader = new SAXReader();
		
		Document document = saxReader.read(SystemLogBO.class.getClassLoader().getResourceAsStream("logback.xml"));
		Element element = (Element) document.selectSingleNode("//property[@name='log.dir']");
		Collection<File> listFiles = FileUtils.listFiles(new File(element.attributeValue("value")), FileFilterUtils.fileFileFilter(), FileFilterUtils.falseFileFilter());
		
		for (File file : listFiles) {
			LogFile logFile = new LogFile();
			logFile.setFileName(file.getName());
			logFile.setLastUpdateTime(DateUtils.formatLongDate(new Date(file.lastModified())));
			logFile.setFilePath(file.getAbsolutePath());
			logFile.setSize(formetFileSize(file.length()));
			list.add(logFile);
		}
		Collections.sort(list);
		
		return list;
	} 
	
	private String formetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
	
	public static void main(String[] args) throws Exception {
		
		SAXReader saxReader = new SAXReader();
		//System.out.println(IOUtils.toString(ClassLoader.getSystemResourceAsStream("logback.xml")));
		Document document = saxReader.read(ClassLoader.getSystemResourceAsStream("logback.xml"));
		Element element = (Element) document.selectSingleNode("//property[@name='log.dir']");
		LOGGER.info(element.attributeValue("value"));
		SystemLogBO bo = new SystemLogBO();
		LOGGER.info(new File(System.getProperty("user.dir")+"/logs").isDirectory());
		bo.findLogFile();
	}
	
	
	/**
	 * 
	 * 内部类：日志文件信息
	 * 
	 * @author vincent
	 *
	 */
	public class LogFile implements Comparable<LogFile> {

		private String fileName;
		
		private String lastUpdateTime;
		
		private String filePath;
		
		private String size;
		

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getLastUpdateTime() {
			return lastUpdateTime;
		}

		public void setLastUpdateTime(String lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
		
		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}
		
		
		@Override
		public int compareTo(LogFile o) {
			if (null == o) {
				return 1;  
			} else {  
	            return o.lastUpdateTime.compareTo(this.lastUpdateTime);  
	        }  
		}
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
		}
		
		
	}
}