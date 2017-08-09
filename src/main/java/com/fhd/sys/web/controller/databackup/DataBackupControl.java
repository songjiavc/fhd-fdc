package com.fhd.sys.web.controller.databackup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.sys.business.databackup.DataBackupBO;

@Controller
public class DataBackupControl {

	@Autowired
	private DataBackupBO o_dataBackupBO;
	/**
	 * 查询数据库备份文件列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/databackup/findDataBackupList.f")
	public Map<String,Object> findDataBackupList(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("datas", o_dataBackupBO.findDataBackupList());
		return map;
	}
	/**
	 * 备份 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/databackup/backup.f")
	public Map<String,Object> backup(){ 
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", o_dataBackupBO.backup());
		return map;
	} 
	
	/**
	 * 恢复
	 * @param fileName 文件名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/databackup/load.f")
	public Map<String,Object> load(String fileName){ 
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", o_dataBackupBO.load(fileName));
		return map; 
	} 
	
	/**
	 * 删除
	 * @param filePaths 文件路径
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/databackup/remove.f")
	public Map<String,Object> remove(String filePaths) throws Exception{ 
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(filePaths)){
			String[] filePathArray = filePaths.split(",");
			for (String filePath : filePathArray) {
				File file = new File(filePath); 
				file.delete();
			}
			map.put("success", true);
		}
		return map; 
	} 
	
	/**
	 * 下载
	 * @param fileName
	 * @param filePath
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/sys/databackup/download.f")
	public void downloadFile(String fileName, String filePath, HttpServletResponse response)
			throws Exception {
		fileName = java.net.URLDecoder.decode(fileName,"UTF-8");
		filePath = java.net.URLDecoder.decode(filePath,"UTF-8");
		response.setContentType("sql");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ URLEncoder.encode(fileName, "UTF-8"));
		// 获取欲下载的文件输入流
		FileInputStream fis = new FileInputStream(filePath);
		BufferedInputStream bis = new BufferedInputStream(fis);
		FileCopyUtils.copy(bis, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
	
}
