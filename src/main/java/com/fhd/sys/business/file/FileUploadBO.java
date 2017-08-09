package com.fhd.sys.business.file;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.file.FileUploadDAO;
import com.fhd.dao.sys.file.VFileUploadDAO;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.file.VFileUploadEntity;
import com.fhd.fdc.utils.Contents;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.log.BusinessLogBO;
import com.fhd.sys.web.form.file.FileUploadForm;

/**
 * 文件BO类.
 * @author   wudefu
 * @version V1.0  创建时间：2010-9-8 
 * @since    Ver 1.1
 * @Date	 2010-9-8		下午12:45:33
 * Company FirstHuiDa.
 * @see 	 
 */

@Service
@SuppressWarnings("unchecked")
public class FileUploadBO {
	
	@Autowired
	private VFileUploadDAO o_vFileUploadDAO;
	@Autowired
	private FileUploadDAO o_fileUploadDAO;
	@Autowired
	private BusinessLogBO o_businessLogBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * 
	 * findById:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public FileUploadEntity findById(String id){
		return o_fileUploadDAO.get(id);
	}
	
	/**
	 * 
	 * findPageBySome:
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param fileTypeId
	 * @param oldFileName
	 * @param sort
	 * @param dir
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<VFileUploadEntity> findPageBySome(Page<VFileUploadEntity> page,String fileTypeId,String oldFileName, List<Map<String, String>> sortList){
		DetachedCriteria criteria=DetachedCriteria.forClass(VFileUploadEntity.class);
		criteria.createAlias("fileType", "fileType");
		if(StringUtils.isNotBlank(oldFileName)){
			criteria.add(Restrictions.like("oldFileName", oldFileName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(fileTypeId)){
			DetachedCriteria fileTypeCriteria=DetachedCriteria.forClass(DictEntry.class);
			fileTypeCriteria.add(Restrictions.like("idSeq", "."+fileTypeId+".",MatchMode.ANYWHERE));
			fileTypeCriteria.setProjection(Property.forName("id"));
			criteria.add(Property.forName("fileType.id").in(fileTypeCriteria));
		}
		if(null!=sortList){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("fileTypeDictEntryName".equalsIgnoreCase(property)){
						property="fileType.sort";
					}
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}else{
			criteria.addOrder(Order.desc("uploadTime"));
		}
		return o_vFileUploadDAO.findPage(criteria, page, false);
	}
	
	public List<VFileUploadEntity> findBySome(String fileType,String oldFileName,String[] ids){
		Criteria criteria = o_vFileUploadDAO.createCriteria();
		if(ids!=null){
			if(ids.length>0){
				criteria.add(Restrictions.in("id", ids));
			}else{
				criteria.add(Restrictions.isNull("id"));
			}
		}
		if(StringUtils.isNotBlank(oldFileName)){
			criteria.add(Restrictions.like("oldFileName", oldFileName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(fileType)){
			criteria.add(Restrictions.eq("fileType", fileType));
		}
		return criteria.list();
	}
	/**
	 * 保存文件.
	 * @author 吴德福
	 * @param fileUploadForm 文件Form.
	 * @param request
	 * @param response
	 * @return boolean 是否上传成功.
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void saveFileUpload(FileUploadEntity fileUpload) throws Exception{
		try {
			o_fileUploadDAO.merge(fileUpload);
			o_businessLogBO.saveBusinessLogInterface("新增", "文件上传", "成功", fileUpload.getOldFileName());
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.saveBusinessLogInterface("新增", "文件上传", "失败", fileUpload.getOldFileName());
		}
	}
	
	/**
	 * 修改文件.
	 * @author 吴德福
	 * @param file 上传文件.
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void updateFile(FileUploadEntity fileUpload){
		try {
			o_fileUploadDAO.merge(fileUpload);
			o_businessLogBO.modBusinessLogInterface("修改", "文件上传", "成功", fileUpload.getId());
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.modBusinessLogInterface("修改", "文件上传", "失败", fileUpload.getId());
		}
	}
	
	/**
	 * 删除文件.
	 * @author 吴德福
	 * @param id 文件id.
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeFileById(String id){
		try {
			o_fileUploadDAO.delete(id);
			o_businessLogBO.delBusinessLogInterface("删除", "文件上传", "成功", id);
		} catch (Exception e) {
			e.printStackTrace();
			o_businessLogBO.delBusinessLogInterface("删除", "文件上传", "失败", id);
		}
	}
	
	/**
	 * 根据id集合批量删除文件.
	 * @author 吴德福
	 * @param ids 文件id集合.
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeFilesByIds(String ids){
		o_fileUploadDAO.createQuery("delete FileUploadEntity where id in (:ids)")
			.setParameterList("ids", StringUtils.split(ids,",")).executeUpdate();
	}
	
	/**
	 * 根据id查询上传文件.
	 * @author 吴德福
	 * @param id 文件id.
	 * @return FileUpload 文件.
	 * @since  fhd　Ver 1.1
	 */
	public FileUploadEntity queryFileById(String id){
		return o_fileUploadDAO.get(id);
	}
	
	/**
	 * 查询所有的上传文件.
	 * @author 吴德福
	 * @return List<FileUpload> 上传文件集合.
	 * @since  fhd　Ver 1.1
	 */
	public Page<FileUploadEntity> queryAllFileUpload(Page<FileUploadEntity> page){
		return o_fileUploadDAO.findPage(DetachedCriteria.forClass(FileUploadEntity.class), page,false);
	}
	
	/**
	 * 根据查询条件查询文件信息.
	 * @author 吴德福
	 * @param fileUploadForm.
	 * @return List<FileUpload> 文件信息集合.
	 * @since  fhd　Ver 1.1
	 */
	public List<FileUploadEntity> fineFileUploadEntityBySome(FileUploadForm fileUploadForm){
		StringBuilder hql = new StringBuilder();
		hql.append("From FileUpload Where 1=1 ");
		String username = "";
		if(fileUploadForm != null && !"".equals(fileUploadForm.getUsername()) && fileUploadForm.getUsername() != null){
			username = fileUploadForm.getUsername();
			hql.append(" and sysUser.username like '%"+username+"%'");
		}
		if(fileUploadForm != null && !"".equals(fileUploadForm.getOldFileName()) && fileUploadForm.getOldFileName() != null){
			hql.append(" and oldFileName like '%"+fileUploadForm.getOldFileName()+"%'");
		}
		if(fileUploadForm != null && fileUploadForm.getNewFileName() != null && fileUploadForm.getNewFileName() != null){
			hql.append(" and newFileName like '%"+fileUploadForm.getNewFileName()+"%'");
		}
		return o_fileUploadDAO.find(hql.toString());
	}
	/**
	 *  根据查询条件查询文件信息
	 * @param page
	 * @param fileUploadEntity
	 * @return
	 */
	public Page<FileUploadEntity> queryAllFileByCondandPage(Page<FileUploadEntity> page,FileUploadEntity fileUploadEntity,String sort,String dir){
		DetachedCriteria dc = DetachedCriteria.forClass(FileUploadEntity.class);
		dc.createCriteria("sysUser","user");
		if(StringUtils.isNotBlank(fileUploadEntity.getOldFileName())){
			dc.add(Restrictions.like("oldFileName", fileUploadEntity.getOldFileName(), MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(fileUploadEntity.getNewFileName())){
			dc.add(Restrictions.like("newFileName", fileUploadEntity.getNewFileName(), MatchMode.ANYWHERE));
		}
		//用户名
		if(StringUtils.isNotBlank(fileUploadEntity.getNotes())){
			dc.add(Restrictions.like("user.username", fileUploadEntity.getNotes(), MatchMode.ANYWHERE));
		}
		
		if("ASC".equalsIgnoreCase(dir)) {
			if(!"userName".equals(sort) && !"id".equals(sort)){
				dc.addOrder(Order.asc(sort));
			}else if("id".equals(sort)){
				dc.addOrder(Order.asc("uploadTime"));
			}else{
				dc.addOrder(Order.asc("user.username"));
			}
		}else {
			if(!"userName".equals(sort) && !"id".equals(sort)){
				dc.addOrder(Order.desc(sort));
			}else if("id".equals(sort)){
				dc.addOrder(Order.desc("uploadTime"));
			}else{
				dc.addOrder(Order.desc("user.username"));
			}
		}
		return o_fileUploadDAO.findPage(dc, page,false);
	}
	/**
	 * 
	 * uploadFile:
	 * 
	 * @author 杨鹏
	 * @param chooseWay
	 * @param oldFileName
	 * @param file
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public String uploadFile(String chooseWay, String oldFileName,MultipartFile file) throws Exception {
		String flag="";
		/**
		 * 初始化参数
		 */
		if(Contents.FILE_TYPE_MAP.keySet().size()==0){
			List<DictEntry> fileTypeList = o_dictBO.findBySome(null, "0file_type", true);
			for (DictEntry dictEntry : fileTypeList) {
				String value = dictEntry.getValue();
				if(StringUtils.isNotBlank(value)){
					Contents.FILE_TYPE_MAP.put(value.toUpperCase(), true);
				}
			}
		}
		String fileUploadMaxSizeStr=ResourceBundle.getBundle("application").getString("fileUploadMaxSize");
		if(StringUtils.isBlank(fileUploadMaxSizeStr)){
			fileUploadMaxSizeStr="1048576";
		}
		/**
		 * 判断文件类型权限
		 */
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
		String prefix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
		if(Contents.FILE_TYPE_MAP.get(prefix)==null){
			flag=prefix+"_fileTypeout";
		}else{
			/**
			 * 判断文件大小
			 */
			Long size = file.getSize();
			if(size>Long.valueOf(fileUploadMaxSizeStr)){
				flag="big";
			}else{
				FileUploadEntity fileUpload = new FileUploadEntity();
				if ("fileDir".equals(chooseWay)) {//保存文件到硬盘中
					/**
					 * 读取硬盘保存路径
					 */
					String fileUploadpath = ResourceBundle.getBundle("application").getString("fileUploadPath");
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
					FileOutputStream fos = new FileOutputStream(new File(fileUploadpath + newFilename));
					IOUtils.copy(file.getInputStream(), fos);
					fileUpload.setFileAddress(fileUploadpath + newFilename);
				}else{// 保存文件到数据库中
					fileUpload.setNewFileName(fileName.substring(0, fileName.length()- prefix.length() - 1)+ "." + prefix);
					fileUpload.setContents(file.getBytes());
				}
				fileUpload.setId(Identities.uuid());
				if(StringUtils.isNotBlank(oldFileName)){
					fileUpload.setOldFileName(oldFileName+"." + prefix);
				}else{
					fileUpload.setOldFileName(fileName);
				}
				fileUpload.setFileSize(String.valueOf(size));
				DictEntry fileType = o_dictBO.getBySome(prefix, "0file_type");
				fileUpload.setFileType(fileType);
				fileUpload.setUploadTime(new Date());
				fileUpload.setCountNum(0);
				saveFileUpload(fileUpload);
				flag = fileUpload.getId();
			}
		}
		return flag;

	}

}

