package com.fhd.sys.business.databackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fhd.core.utils.Identities;
import com.fhd.sys.web.form.databackup.DBConfig;
import com.fhd.sys.web.form.databackup.DataBackup;

/**
 * 数据库的备份和还原。
 * 需配置application.properties的配置项
 * dataBackupFolder=D\:\\upload\\
 * mysqlPath = C:\\Program Files\\MySQL\\MySQL Server 5.5\\bin\\
 * @author zhanglei
 *
 */
@Service
public class DataBackupBO {
	private static DBConfig dBConfig;
	private static final Logger log = Logger.getLogger(DataBackupBO.class);
	/**
	 * 读取数据库配置信息
	 * @return
	 */
	static { 
		dBConfig = new DBConfig();
		String dataBackupFolder=ResourceBundle.getBundle("application").getString("dataBackupFolder");
		String mysqlPath = ResourceBundle.getBundle("application").getString("mysqlPath");
		String username = ResourceBundle.getBundle("application").getString("jdbc.username");
		String password = ResourceBundle.getBundle("application").getString("jdbc.password");
		String url = ResourceBundle.getBundle("application").getString("jdbc.url");
		dBConfig.setDataBackupFolder(dataBackupFolder);
		dBConfig.setMysqlPath(mysqlPath);
		dBConfig.setUsername(username); 
		dBConfig.setPassword(password); 
		url = url.substring(13, url.length()); 
		String[] temp = url.split("/"); 
		String[] temp1 = temp[0].split(":"); 
		dBConfig.setHost(temp1[0]); 
		dBConfig.setPort(temp1[1]); 
		for (int i = 0; i < temp[1].length(); i++) { 
			String temp2 = temp[1].charAt(i)+""; 
			if(temp2.equals("?")){ 
				dBConfig.setDbname(temp[1].substring(0,temp[1].indexOf('?'))); 
			} 
		} 
		log.info("数据库配置信息加载完成！");
	} 
	
	/**
	 * 查询备份文件列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DataBackup> findDataBackupList(){
		String dataBackupFolder = dBConfig.getDataBackupFolder();
		Collection<File> listFiles = FileUtils.listFiles(new File(dataBackupFolder), FileFilterUtils.fileFileFilter(), FileFilterUtils.falseFileFilter());
		List<DataBackup> dataBackupList = new ArrayList<DataBackup>();
		for (File file : listFiles) {
			DataBackup dataBackup = new DataBackup();
			dataBackup.setId(Identities.uuid());
			dataBackup.setFileName(file.getName());
			dataBackup.setFilePath(file.getPath());
			dataBackup.setFileSize(String.valueOf(file.length()));
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒  E"); 
			//前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型 
			java.util.Date dt = new Date(file.lastModified());   
			String sDateTime = sdf.format(dt);//得到精确到秒的表示：2013年12月30日 11:11:11
			dataBackup.setFileDate(sDateTime);
			//dataBackup.setDbName(dBConfig.getDbname());
			//dataBackup.setHost(dBConfig.getHost());
			dataBackupList.add(dataBackup);
		}
		Collections.sort(dataBackupList);
		return dataBackupList;
	}
	
	/**
	 * 备份
	 * @return
	 */
	public boolean backup(){
		boolean flag =false;
		File backupath = new File(dBConfig.getDataBackupFolder());
		if (!backupath.exists()) {
			backupath.mkdir();
		}
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmssSSS");
		StringBuffer sb = new StringBuffer();
		sb.append(dBConfig.getMysqlPath());
		sb.append("mysqldump ");
		sb.append("--opt ");
		sb.append("-h ");
		sb.append(dBConfig.getHost());
		sb.append(" ");
		sb.append("--user=");
		sb.append(dBConfig.getUsername());
		sb.append(" ");
		sb.append("--password=");
		sb.append(dBConfig.getPassword());
		sb.append(" ");
		sb.append("--hex-blob --lock-all-tables=true ");
		sb.append(" --default-character-set=utf8 ");
		sb.append(dBConfig.getDbname());
		try {
            Runtime rt = Runtime.getRuntime();
            // 调用 mysql 的 cmd:
            Process child = rt.exec(sb.toString());// 设置导出编码为utf8。这里必须是utf8
            // 把进程执行中的控制台输出信息写入.sql文件，即生成了备份文件。注：如果不对控制台信息进行读出，则会导致进程堵塞无法运行
            InputStream in = child.getInputStream();// 控制台的输出信息作为输入流
            InputStreamReader inr = new InputStreamReader(in, "UTF-8");// 设置输出流编码为utf8。这里必须是utf8，否则从流中读入的是乱码
            String inStr;
            StringBuffer sbtmp = new StringBuffer();
            String outStr;
            // 组合控制台输出信息字符串
            BufferedReader br = new BufferedReader(inr);
            while ((inStr = br.readLine()) != null) {
            	sbtmp.append(inStr).append( "\r\n"); 
            }
            outStr = sbtmp.toString();
            //System.out.println(outStr);
            // 要用来做导入用的sql目标文件：
            StringBuffer filePath = new StringBuffer();
            filePath.append(dBConfig.getDataBackupFolder());
            filePath.append(dBConfig.getDbname());
            filePath.append("_");
            filePath.append(sdf.format(System.currentTimeMillis()));
            filePath.append(".sql");
            FileOutputStream fout = new FileOutputStream(filePath.toString());
            OutputStreamWriter writer = new OutputStreamWriter(fout, "UTF-8");
            writer.write(outStr);
            // 注：这里如果用缓冲方式写入文件的话，会导致中文乱码，用flush()方法则可以避免
            writer.flush();
            // 别忘记关闭输入输出流
            in.close();
            inr.close();
            br.close();
            writer.close();
            fout.close();
            log.info("数据库备份完成！");
            flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 恢复
	 * @param fileName 数据库备份文件
	 * @return
	 */
	public boolean load(String fileName){ 
		boolean flag =false;
		String sqlPath = null;
		if(StringUtils.isNotBlank(fileName)){
			String dataBackupFolder = dBConfig.getDataBackupFolder();
			sqlPath = dataBackupFolder+fileName;
		}
		
		StringBuffer stmt = new StringBuffer();
		stmt.append(dBConfig.getMysqlPath());
		stmt.append("mysql ");
		stmt.append("-h ");
		stmt.append(dBConfig.getHost());
		stmt.append(" ");
		stmt.append(" -u  ");
		stmt.append(dBConfig.getUsername());
		stmt.append(" -p");
		stmt.append(dBConfig.getPassword());
		stmt.append(" --default-character-set=utf8 ");
		stmt.append(dBConfig.getDbname());
		try {
			Runtime rt = Runtime.getRuntime(); 
			// 调用 mysql 的 cmd: 
			Process child = rt.exec(stmt.toString()); 
			OutputStream out = child.getOutputStream();// 控制台的输入信息作为输出流 
			String inStr; 
			StringBuffer sb = new StringBuffer(""); 
			String outStr; 
			BufferedReader br = new BufferedReader(new InputStreamReader( 
			new FileInputStream(sqlPath), "UTF-8")); 
			while ((inStr = br.readLine()) != null) { 
			sb.append(inStr).append( "\r\n"); 
			} 
			outStr = sb.toString(); 
			OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8"); 
			writer.write(outStr); 
			// 注：这里如果用缓冲方式写入文件的话，会导致中文乱码，用flush()方法则可以避免 
			writer.flush(); 
			// 别忘记关闭输入输出流 
			out.close(); 
			br.close(); 
			writer.close(); 
			log.info("数据库恢复完成！"); 
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
}
