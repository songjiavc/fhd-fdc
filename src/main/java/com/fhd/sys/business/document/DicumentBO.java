package com.fhd.sys.business.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.dic.DictTypeDAO;
import com.fhd.dao.sys.document.DocumentLibraryDAO;
import com.fhd.dao.sys.document.DocumentRelaFileDAO;
import com.fhd.dao.sys.document.DocumentRelaOrgDAO;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.dic.DictType;
import com.fhd.entity.sys.document.DocumentLibrary;
import com.fhd.entity.sys.document.DocumentRelaFile;
import com.fhd.entity.sys.document.DocumentRelaOrg;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.sys.business.dic.DictTreeBO;
import com.fhd.sys.web.form.document.DocumentLibraryForm;

/**
 * 文档库分类树查询BO
 * @author 王再冉
 *
 */
@Service
public class DicumentBO {
	
	@Autowired
	private DictTypeDAO o_dictTypeDAO;
	@Autowired
	private DictTreeBO o_dictTreeBO;
	@Autowired
	private DocumentLibraryDAO o_documentLibDAO;
	@Autowired
	private DocumentRelaOrgDAO o_documentRelaOrgDAO;
	@Autowired
	private DocumentRelaFileDAO o_documentRelaFileDAO;
	
	/**
	 * findTreeRootByDictTypeId: 根据数据字典类型id查询根节点
	 * @param typeId	数据字典类型id
	 * @param query		查询条件
	 * @return
	 */
	public Map<String, Object> findTreeNodeByDictTypeId(String typeId, String query){ 
		Map<String, Object> rootNode = new HashMap<String, Object>();//数据字典类型节点
		Map<String, Object> dictEntryNode = o_dictTreeBO.findDictEntryTree(query, typeId);//数据字典项
		rootNode.putAll(dictEntryNode);
		return rootNode;
	}
	
	/**
	 * saveDocument： 保存文档库
	 * @param documentForm	包含文档库实体，文档关联部门实体和文档关联文件实体
	 */
	@Transactional
	public void saveDocument(DocumentLibraryForm documentForm, String docId){
		DocumentLibrary document = new DocumentLibrary();
		String companyId = UserContext.getUser().getCompanyid();//获得公司ID
		String orgId=IcmStandardUtils.findIdbyJason(documentForm.getOrgId(), "id");//将Json转换为需要的字符串
		if(StringUtils.isNotBlank(docId)){//修改
			DocumentLibrary findDoc = o_documentLibDAO.get(docId);
			document.setId(docId);
			document.setLastModifyTime(new Date());//最后修改时间
			document.setCreatTime(findDoc.getCreatTime());
		}else{//新增
			document.setId(Identities.uuid());
			document.setCreatTime(new Date());//创建时间
		}
		document.setDocumentCode(documentForm.getDocumentCode());
		document.setDocumentName(documentForm.getDocumentName());
		document.setCompany(new SysOrganization(companyId));
		DictEntry dictEntry = new DictEntry();
		dictEntry.setId(documentForm.getDictEntryId());
		document.setDictEntry(dictEntry);//保存数据字典类型
		document.setDesc(documentForm.getDesc());
		document.setSort(documentForm.getSort());
		document.setDeleteStatus(true);//删除状态
		o_documentLibDAO.merge(document);
		
		this.saveDocumentRelaOrg(document, orgId);//保存文档关联部门实体
		this.saveDocumentRelaFiles(document,documentForm);//保存文档关联文件实体
	}
	
	/**
	 * saveDocumentRelaOrg: 保存文档关联部门实体
	 * @param document	文档库实体
	 * @param orgId		部门id
	 */
	@Transactional
	public void saveDocumentRelaOrg(DocumentLibrary document, String orgId){
		DocumentRelaOrg docRelaOrg = new DocumentRelaOrg();
		DocumentRelaOrg finddocRelaOrg = this.findDocumentRelaOrgBydocId(document);
		if(null != finddocRelaOrg){//存在关联，先删除
			this.deleteDocumentRelaOrg(finddocRelaOrg);
		}
		//保存文档关联部门实体
		if(StringUtils.isNotBlank(orgId)){
			docRelaOrg.setId(Identities.uuid());
			docRelaOrg.setDocument(document);
			docRelaOrg.setOrg(new SysOrganization(orgId));
			o_documentRelaOrgDAO.merge(docRelaOrg);
		}
	}
	
	/**
	 * findDocumentRelaOrgBydocId: 根据文档id查文档关联部门实体
	 * @param document	文档类
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public DocumentRelaOrg findDocumentRelaOrgBydocId(DocumentLibrary document) {
		Criteria criteria = o_documentRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.eq("document.id", document.getId()));
		List<DocumentRelaOrg> docRelaOrg = criteria.list();
		if(docRelaOrg.size()>0){
			return docRelaOrg.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 批量删除文档关联部门实体
	 * @param list
	 */
	@Transactional
	public void deleteDocumentRelaOrgs(List<DocumentRelaOrg> list){
		StringBuffer ids = new StringBuffer();
		for(DocumentRelaOrg temp : list){
			//ids = ids + "," + temp.getId();
			ids = ids.append("," + temp.getId());
		}
		o_documentRelaOrgDAO.createQuery("delete DocumentRelaOrg where id in (:ids)")
		.setParameterList("ids", StringUtils.split(ids.toString(),",")).executeUpdate();
	}
	
	/**
	 * 删除文档关联机构实体
	 * @param docRelaOrg
	 */
	@Transactional
	public void deleteDocumentRelaOrg(DocumentRelaOrg docRelaOrg){
		o_documentRelaOrgDAO.delete(docRelaOrg);
	}
	
	/**
	 * saveDocumentRelaFiles: 保存文档关联文件实体
	 * @param document	文档类
	 * @param docForm	文档表单类
	 */
	@Transactional
	public void saveDocumentRelaFiles(DocumentLibrary document, DocumentLibraryForm docForm){
		List<DocumentRelaFile> docRelaFileList = this.findDocumentRelaFilesBydoc(document);
		if(docRelaFileList.size()>0){//存在关联，先删除
			this.deleteDocumentRelaFiles(docRelaFileList);
		}
		DocumentRelaFile docRealFile=new DocumentRelaFile();
		docRealFile.setDocument(document);
		if(null != docForm.getFileIds()){
			String[] IdsList = docForm.getFileIds().split(",");
			FileUploadEntity fileEntity=new FileUploadEntity();
			for(int i=0;i<IdsList.length;i++){
				if(StringUtils.isNotBlank(IdsList[i])){
					fileEntity.setId(IdsList[i]);
					docRealFile.setFile(fileEntity);
					docRealFile.setId(Identities.uuid());
					o_documentRelaFileDAO.merge(docRealFile);
				}
			}
		}
	}
	
	/**
	 * findDocumentRelaFilesBydocId: 通过文档查找文档关联文件类实体集合
	 * @param document
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<DocumentRelaFile> findDocumentRelaFilesBydoc(DocumentLibrary document) {
		Criteria criteria = o_documentRelaFileDAO.createCriteria();
		criteria.add(Restrictions.eq("document.id", document.getId()));
		return criteria.list();
	}
	
	/**
	 * 批量删除文档关联文件
	 * @param list
	 */
	@Transactional
	public void deleteDocumentRelaFiles(List<DocumentRelaFile> list){
		StringBuffer ids = new StringBuffer();
		for(DocumentRelaFile temp : list){
			//ids = ids + "," + temp.getId();
			ids = ids.append( "," + temp.getId());
		}
		o_documentRelaFileDAO.createQuery("delete DocumentRelaFile where id in (:ids)")
		.setParameterList("ids", StringUtils.split(ids.toString(),",")).executeUpdate();
	}
	
	/**
	 * findDocumentLabrarysPage：查询文档库列表
	 * @param query		查询条件
	 * @param page		分页
	 * @param sort		排序
	 * @param dir	
	 * @param companyId	公司id
	 * @param typeId	数据字典id
	 * @return
	 */
	public Page<DocumentLibrary> findDocumentLabrarysPage(String query, Page<DocumentLibrary> page, String sort, String dir,
						String companyId, String typeId){
		DetachedCriteria dc = DetachedCriteria.forClass(DocumentLibrary.class);
		if(StringUtils.isNotBlank(companyId)){
			dc.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(typeId)){
			dc.add(Restrictions.eq("dictEntry.id", typeId));
		}
		if(StringUtils.isNotBlank(query)){
			dc.add(Property.forName("documentName").like(query,MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq("deleteStatus", true));//删除状态为“1”
		
		if("ASC".equalsIgnoreCase(dir)) {
			dc.addOrder(Order.asc(sort));
		}else {
			dc.addOrder(Order.desc(sort));
		}
		return o_documentLibDAO.findPage(dc, page, false);
	}
	
	/**
	 * findDocumentLabrarysPageByIdList:通过数据字典id集合查询所有文档库
	 * @param query
	 * @param page
	 * @param sort
	 * @param dir
	 * @param companyId
	 * @param typeIdList
	 * @return
	 */
	public Page<DocumentLibrary> findDocumentLabrarysPageByIdList(String query, Page<DocumentLibrary> page, String sort, String dir,
			String companyId, List<String> typeIdList){
		DetachedCriteria dc = DetachedCriteria.forClass(DocumentLibrary.class);
		if(StringUtils.isNotBlank(companyId)){
			dc.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(query)){
			dc.add(Property.forName("documentName").like(query,MatchMode.ANYWHERE));
		}
		if(typeIdList.size()>0){
			dc.add(Restrictions.in("dictEntry.id", typeIdList));
			dc.add(Restrictions.eq("deleteStatus", true));//删除状态为“1”
		}else{
			return null;
		}
		if("ASC".equalsIgnoreCase(dir)) {
			dc.addOrder(Order.asc(sort));
		}else {
			dc.addOrder(Order.desc(sort));
		}
		return o_documentLibDAO.findPage(dc, page, false);
	}
	
	/**
	 * 查询文档表单
	 * @param docId
	 * @return
	 */
	public Map<String,Object> findDocumentLibFormByID(String docId){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		DocumentLibrary document = o_documentLibDAO.get(docId);
		DocumentRelaOrg docRelaOrg = this.findDocumentRelaOrgBydocId(document);
		List<DocumentRelaFile> docRelaFilelist = this.findDocumentRelaFilesBydoc(document);
		inmap.put("documentName", document.getDocumentName());
		inmap.put("documentCode", document.getDocumentCode());
		inmap.put("desc", document.getDesc());
		inmap.put("sort", document.getSort());
		inmap.put("dictEntryId", document.getDictEntry().getId());
		if(null != docRelaOrg){
			//inmap.put("orgId","[{\"id\":\""+docRelaOrg.getOrg().getId()+"\",\"deptno\":\"\",\"deptname\":\"\"}]");
			JSONArray arr = new JSONArray();
			JSONObject obj = new JSONObject();
			SysOrganization org = docRelaOrg.getOrg();
			obj.put("id", org.getId());
			obj.put("deptno", org.getOrgcode());
			obj.put("deptname", org.getOrgname());
			arr.add(obj);
			inmap.put("orgId",arr.toString());
		}
		if(null != docRelaFilelist && docRelaFilelist.size()>0){
			StringBuffer fileIds = new StringBuffer();
			for(DocumentRelaFile docRalaFile:docRelaFilelist){
				String fileId=docRalaFile.getFile().getId();
				//fileIds=fileIds+","+fileId;
				fileIds = fileIds.append(","+fileId);
			}
			inmap.put("fileIds", fileIds.toString());
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 删除文档
	 * @param docIds
	 */
	@Transactional
	public Boolean removeDocumentLibraryAll(String docIds){
		List<String> idList = new ArrayList<String>();
		if (StringUtils.isNotBlank(docIds)) {
			String[] idArray = docIds.split(",");
			for (String id : idArray) {
				idList.add(id);
			}
		}
		List<DocumentRelaFile> docRelaFileList = this.findDocumentRelaFilesBydocIds(idList);
		List<DocumentRelaOrg> docRelaOrgList = this.findDocumentRelaOrgsBydocIds(idList);
		if(null != docRelaOrgList && docRelaOrgList.size()>0){
			this.deleteDocumentRelaOrgs(docRelaOrgList);//批量删除文档关联部门
		}
		if(null != docRelaFileList && docRelaFileList.size()>0){
			this.deleteDocumentRelaFiles(docRelaFileList);
		}
		this.removeDocumentLibraryBydocIds(idList);
		return true;
	}
	
	/**
	 * 根据文档id集合查询所有上传文件
	 * @param docIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<DocumentRelaFile> findDocumentRelaFilesBydocIds(List<String> docIds) {
		Criteria criteria = o_documentRelaFileDAO.createCriteria();
		criteria.add(Restrictions.in("document.id", docIds));
		return criteria.list();
	}
	
	/**
	 * 根据文档id集合查询所有文档关联部门实体s
	 * @param docIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<DocumentRelaOrg> findDocumentRelaOrgsBydocIds(List<String> docIds) {
		Criteria criteria = o_documentRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.in("document.id", docIds));
		return criteria.list();
	}
	
	/**
	 * 根据id集合查找文档实体集合
	 * @param docIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<DocumentLibrary> findDocumentLibraryBydocIds(List<String> docIds) {
		Criteria criteria = o_documentLibDAO.createCriteria();
		criteria.add(Restrictions.in("id", docIds));
		return criteria.list();
	}
	
	/**
	 * 逻辑删除文档实体
	 * @param docList
	 */
	public void removeDocumentLibraryBydocIds(List<String> docIds){
		List<DocumentLibrary> docLibList = this.findDocumentLibraryBydocIds(docIds);
		for(DocumentLibrary doc : docLibList){
			doc.setDeleteStatus(false);
			o_documentLibDAO.merge(doc);
		}
	}
	
	/**
	 * findPreviewPanelBydocId:根据文档id查看预览信息
	 * 
	 * @param docId 文档id
	 * @return
	 */
	public Map<String,Object> findPreviewPanelBydocId(String docId){
		Map<String, Object> inmap = new HashMap<String, Object>();
		String fileNames = "";
		String fileIds = "";
		DocumentLibrary document = this.findDocumentLibraryById(docId);
		DocumentRelaOrg docRelaOrg = this.findDocumentRelaOrgBydocId(document);
		List<DocumentRelaFile> docRelaFiles = this.findDocumentRelaFilesBydoc(document);
		if(null != document){
			inmap.put("documentName", document.getDocumentName());
			inmap.put("documentCode", document.getDocumentCode());
			inmap.put("dictEntryName", document.getDictEntry().getName());
			inmap.put("sort", document.getSort());
			inmap.put("desc", document.getDesc());
			if(null != docRelaOrg){
				inmap.put("orgName", null!=docRelaOrg.getOrg()?docRelaOrg.getOrg().getOrgname():"");
			}
			if(docRelaFiles.size()>0){
				for(DocumentRelaFile temp : docRelaFiles){
					fileIds = temp.getFile().getId() + "," + fileIds;
					fileNames = temp.getFile().getNewFileName() + "," + fileNames;
				}
			}
			inmap.put("fileName", fileNames);
			inmap.put("fileIds", fileIds);
		}
		return inmap;
	}
	
	/**
	 * 根据文档id查文档实体
	 * @param id
	 * @return
	 */
	@Transactional
	public DocumentLibrary findDocumentLibraryById(String id){
		return o_documentLibDAO.get(id);
	}
	
	/**
	 * 查询文档库的根节点
	 * @param typeId
	 * @param query
	 * @return
	 */
	public Map<String, Object> findDocumentTreeRoot(String typeId, String query){ 
        Map<String, Object> rootNode = new HashMap<String, Object>();
        DictType root = new DictType();
        Map<String, Object> items = new HashMap<String, Object>(); // 存放
        if(StringUtils.isNotBlank(typeId)){
        	root = o_dictTypeDAO.get(typeId);
        }
        if(null != root){
        	rootNode.put("id",root.getId());
            rootNode.put("text", root.getName());
            rootNode.put("leaf", false);
            rootNode.put("expanded", true);
            rootNode.put("type", "root");
            items.put("documentRoot", rootNode);
        }
        return items;
    }
	
}
