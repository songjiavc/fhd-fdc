package com.fhd.sys.web.controller.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.document.DocumentLibrary;
import com.fhd.entity.sys.document.DocumentRelaFile;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.document.DicumentBO;
import com.fhd.sys.web.form.document.DocumentLibraryForm;
/**
 * 文档库分类树查询Control类
 * @author 王再冉
 *
 */
@Controller
public class DocumentControl {
	@Autowired
	private DicumentBO o_documentBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * findTreeNodeByDictTypeId: 查询树节点
	 * @param typeId	数据字典id	
	 * @param query
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/sys/document/findtreenoodbydicttypeid.f")
    public Map<String, Object> findTreeNodeByDictTypeId(String typeId, String query){ 
        return o_documentBO.findTreeNodeByDictTypeId(typeId,query);
        
    }
	
	/**
	 * saveDocument：保存文档库
	 * @param documentForm
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/document/savedocument.f")
	public Map<String,Object> saveDocument(DocumentLibraryForm documentForm, String docId){
		Map<String,Object> result=new HashMap<String,Object>();
		o_documentBO.saveDocument(documentForm,docId);
		result.put("success", true);
		return result;
	}
	
	/**
	 * findDocumentLabrarysPage: 查询文档库列表
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @param typeId	数据字典id
	 * @param type		是否根节点
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/document/finddocumentlabraryspage.f")
	public Map<String, Object> findDocumentLabrarysPage(int start, int limit, String query, String sort, String typeId,String type){
		String property = "";
		String direction = "";
		Page<DocumentLibrary> page = new Page<DocumentLibrary>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		property = "creatTime";
    	direction = "DESC";
		
		String companyId = UserContext.getUser().getCompanyid();//当前登录者的公司id
		if(StringUtils.isNotBlank(type)){//是根节点
			List<String> typeIdList = new ArrayList<String>();
			List<DictEntry> entryList = o_dictBO.findDictEntryByDictTypeId(typeId);
			if(null != entryList && entryList.size()>0){
				for(DictEntry entry : entryList){
					typeIdList.add(entry.getId());
				}
			}
			page = o_documentBO.findDocumentLabrarysPageByIdList(query, page, property, direction, companyId,typeIdList);
		}else{
			page = o_documentBO.findDocumentLabrarysPage(query, page, property, direction, companyId,typeId);
		}
		
		List<DocumentLibrary> entityList = new ArrayList<DocumentLibrary>();
		if(null != page){
			entityList = page.getResult();
		}
		List<DocumentLibrary> datas = new ArrayList<DocumentLibrary>();
		for(DocumentLibrary de : entityList){
			List<DocumentRelaFile> docrelaFiles = o_documentBO.findDocumentRelaFilesBydoc(de);
			if(docrelaFiles.size()>0){
				datas.add(new DocumentLibraryForm(de,docrelaFiles.get(0)));
			}else{
				datas.add(new DocumentLibraryForm(de,null));
			}
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(null != page){
			map.put("totalCount", page.getTotalItems());
		}else{
			map.put("totalCount", "0");
		}
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * findDocumentLibByID: 修改表单加载数据
	 * @param docId	文档id
	 * @return	
	 */
	@ResponseBody
	@RequestMapping("/sys/document/finddocumentlibbyid.f")
	public Map<String, Object> findDocumentLibByID(String docId){
		Map<String, Object> ruleMap=o_documentBO.findDocumentLibFormByID(docId);
		return ruleMap;
	}
	
	/**
	 * removeDocumentLibraryByIds: 逻辑删除文档，删除文档与文件，部门的关联
	 * @param docIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/document/deletedocumentlibrarybyids.f")
	public boolean removeDocumentLibraryByIds(String docIds){
		return o_documentBO.removeDocumentLibraryAll(docIds);
	}
	
	/**
	 * findPreviewPanelBydocId：根据文档id查看预览信息
	 * 
	 * @param docId	文档id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/access/formulateplan/findpreviewpanelbydocid.f")
	public Map<String,Object> findPreviewPanelBydocId(String docId){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> inmap = o_documentBO.findPreviewPanelBydocId(docId);
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 查询文档库的根节点
	 * @param typeId
	 * @param query
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/access/formulateplan/finddocumenttreeroot.f")
    public Map<String, Object> findDocumentTreeRoot(String typeId, String query){ 
        return o_documentBO.findDocumentTreeRoot(typeId, query);
    }
}
