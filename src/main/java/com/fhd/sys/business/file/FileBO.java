package com.fhd.sys.business.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.file.FileDAO;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.file.VFileUploadEntity;
import com.fhd.sys.business.dic.DictBO;

/**
 * 
 * ClassName:FileBO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-9-10		上午11:39:00
 *
 * @see
 */
@Service
public class FileBO {
	
	@Autowired
	private FileDAO o_fileDAO;
	@Autowired
	private FileUploadBO o_fileUploadBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * 
	 * listMap:获得文件列表
	 * 
	 * @author 杨鹏
	 * @param fileType
	 * @param oldFileName
	 * @param ids
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<Map<String, Object>> listMap(String fileType,String oldFileName, String[] ids){
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		List<VFileUploadEntity> list = o_fileUploadBO.findBySome(fileType, oldFileName, ids);
		for (VFileUploadEntity vFileUploadEntity : list) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", vFileUploadEntity.getId());
			data.put("dbid", vFileUploadEntity.getId());
			data.put("name", vFileUploadEntity.getOldFileName());
			data.put("oldFileName", vFileUploadEntity.getOldFileName());
			data.put("uploadTime", DateUtils.formatDate(vFileUploadEntity.getUploadTime(),"yyyy-MM-dd HH:mm:ss"));
			data.put("fileType", vFileUploadEntity.getFileType().getName());
			data.put("countNum", vFileUploadEntity.getCountNum());
			datas.add(data);
		}
		return datas;
	}
	/**
	 * 
	 * uploadFile:上传文件
	 * 
	 * @author 杨鹏
	 * @param chooseWay
	 * @param file
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public FileUploadEntity uploadFile(String chooseWay, MultipartFile file) throws Exception {
		FileUploadEntity fileUpload=null;
		InputStream inputStream=null;
		FileOutputStream fos=null;
		try {
			String fileUploadMaxSizeStr = ResourceBundle.getBundle(
					"application").getString("fileUploadMaxSize");
			/**
			 * 得到文件的完整路径
			 */
			String path = file.getOriginalFilename();
			/**
			 * 得到文件名
			 */
			String fileName = path.substring(path.lastIndexOf("\\") + 1);
			/**
			 * 得到文件的扩展名(无扩展名时将得到全名)
			 */
			String prefix = fileName.substring(fileName.lastIndexOf(".") + 1)
					.toUpperCase();
			/**
			 * 判断文件大小
			 */
			Long size = file.getSize();
			if (StringUtils.isNotBlank(fileUploadMaxSizeStr)) {
				if (size > Long.valueOf(fileUploadMaxSizeStr)) {
					chooseWay = "fileDir";
				}
			}
			fileUpload = new FileUploadEntity();
			inputStream = file.getInputStream();
			if ("fileDir".equals(chooseWay)) {//保存文件到硬盘中
				/**
				 * 读取硬盘保存路径
				 */
				String fileUploadpath = ResourceBundle.getBundle("application")
						.getString("fileUploadPath");
				/**
				 * 构建硬盘保存路径
				 */
				File dirPath = new File(fileUploadpath);
				if (!dirPath.exists()) {
					dirPath.mkdirs();
				}
				/**
				 * 根据系统时间生成上传后保存的文件名
				 */
				Long now = System.currentTimeMillis();
				String newFilename = String.valueOf(now) + "." + prefix;
				fos = new FileOutputStream(new File(fileUploadpath + newFilename));
				IOUtils.copy(inputStream, fos);
				fileUpload.setFileAddress(fileUploadpath + newFilename);
			} else {// 保存文件到数据库中
				fileUpload.setNewFileName(fileName.substring(0,fileName.length() - prefix.length() - 1)+ "." + prefix);
				fileUpload.setContents(IOUtils.toByteArray(inputStream));
			}
			fileUpload.setId(Identities.uuid());
			fileUpload.setOldFileName(fileName);
			fileUpload.setFileSize(String.valueOf(size));
			DictEntry fileType = o_dictBO.getBySome(prefix, "0file_type");
			fileUpload.setFileType(fileType);
			fileUpload.setUploadTime(new Date());
			fileUpload.setCountNum(0);
			this.save(fileUpload);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(inputStream!=null){
				inputStream.close();
			}
			if(fos!=null){
				fos.close();
			}
		}
		return fileUpload;
	}
	/**
	 * 
	 * save:保存文件
	 * 
	 * @author 杨鹏
	 * @param fileUpload
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void save(FileUploadEntity fileUpload) throws Exception{
		o_fileDAO.merge(fileUpload);
	}
	/**
	 * 
	 * remove:删除
	 * 
	 * @author 杨鹏
	 * @param id
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void remove(String id) throws Exception{
		o_fileDAO.delete(id);
	}
}