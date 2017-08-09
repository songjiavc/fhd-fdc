package com.fhd.sys.web.controller.templatemanage;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.sys.business.dic.DictTreeBO;
import com.fhd.sys.business.templatemanage.TemplateManageBO;
import com.fhd.sys.web.form.tamplatemanage.TemplateManageForm;
/**
 * 
 * @功能 : 模板管理control类
 * @author 王再冉
 * @date 2013-12-25
 * @since Ver
 * @copyRight FHD
 */
@Controller
public class TemplateManageControl {
	
	@Autowired
	private TemplateManageBO o_templateManageBO;
	
	@Autowired
	private DictTreeBO o_dictTreeBO;
	
	/**
	 * 保存模板实体
	 * add by 王再冉
	 * 2013-12-25  上午10:06:40
	 * desc :  
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/templatemanage/savetemplatemanagebyentryid.f")
	public Boolean saveTemplateManageByentryId(String contentEdit){
		TemplateManageForm form = new TemplateManageForm();
		if(StringUtils.isNotBlank(contentEdit)){
			JSONObject contentJson = JSONObject.fromObject(contentEdit);
			String content = contentJson.getString("contents");
			form.setName(contentJson.getString("name"));
			form.setDictEntryId(contentJson.getString("dictEntryId"));
			form.setId(contentJson.getString("id"));
			o_templateManageBO.saveTemplateManageByentryId(form,content);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 列表查询，根据数据字典项id查询所有的模板 
	 * add by 王再冉
	 * 2013-12-25  下午1:28:22
	 * desc : 
	 * @param entryId
	 * @return 
	 * Map<String,String>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/templatemanage/findtemplatemanagesbyentryid.f")
	public List<Map<String,String>> findTemplateManagesByentryId(String entryId,String query){
		return o_templateManageBO.findTemplateManagesByentryIdGrid(entryId,query);
	}
	
	/**
	 * 修改模板，显示模板相关内容
	 * add by wangzairan
	 * 2013-12-25  下午4:00:36
	 * desc : 
	 * @param id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/templatemanage/findtemplatemanageformbyid.f")
	public Map<String, Object> findTemplateManageFormById(String id) {
		return o_templateManageBO.findTemplateManageFormById(id);
	}
	
	/**
	 * 根据模板id，逻辑删除模板实体
	 * add by 王再冉
	 * 2013-12-25  下午5:05:42
	 * desc : 
	 * @param ids 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/templatemanage/removetemplatemanagesbyids.f")
	public Boolean removeTemplateManagesByIds(String ids){
		o_templateManageBO.removeTemplateManagesByIds(ids);
		return true;
	}
	
	/**
	 * 设置默认模板
	 * add by 王再冉
	 * 2013-12-26  下午1:51:12
	 * desc : 
	 * @param id
	 * @return 
	 * Boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/templatemanage/setdefaulttemplatebytemplateid.f")
	public Boolean setDefaultTemplateByTemplateId(String id){
		return o_templateManageBO.setDefaultTemplateByTemplateId(id);
	}
	
	/**
	 * 查询数据字典项tree
	 * add by 王再冉
	 * 2013-12-26  下午1:53:35
	 * desc : 
	 * @param query			查询关键字
	 * @param dictEntryId	数据字典id
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/templatemanage/finddictentrytreebysome.f")
	public Map<String, Object> findDictEntryTreeBySome(String query, String dictEntryId) {
		return o_dictTreeBO.findDictEntryTree(query,dictEntryId);
	}

}
