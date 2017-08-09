package com.fhd.sys.business.templatemanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.templatemanage.TemplateManageDAO;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.templatemanage.TemplateManage;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.web.form.tamplatemanage.TemplateManageForm;
/**
 * 模板管理BO类
 * @功能 : 
 * @author 王再冉
 * @date 2013-12-25
 * @since Ver
 * @copyRight FHD
 */
@Service
public class TemplateManageBO {
	
	@Autowired
	private TemplateManageDAO o_templateManageDAO;
	
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * 保存模板实体
	 * add by 王再冉
	 * 2013-12-25  上午10:52:05
	 * desc : 
	 * @param template 
	 * void
	 */
	@Transactional
	public void mergeTemplateManage(TemplateManage template){
		o_templateManageDAO.merge(template);
	}
	
	/**
	 * 根据模板id查找模板
	 * add by 王再冉
	 * 2013-12-25  下午2:52:18
	 * desc : 
	 * @param id
	 * @return 
	 * TemplateManage
	 */
	@Transactional
	public TemplateManage findTemplateManageById(String id){
		return o_templateManageDAO.get(id);
	}
	
	/**
	 * 保存/修改模板
	 * add by 王再冉
	 * 2013-12-25  上午10:57:16
	 * desc : 
	 * @param form			表单参数
	 * @param contentEdit 	模板内容
	 * void
	 */
	@Transactional
	public void saveTemplateManageByentryId(TemplateManageForm form, String contentEdit){
		TemplateManage template = new TemplateManage();
		DictEntry entry = o_dictBO.findDictEntryById(form.getDictEntryId());
		template.setName(form.getName());
		template.setContent(contentEdit);
		//template.setDictEntry(new DictEntry(form.getDictEntryId()));//保存模板关联数据字典实体
		template.setDictEntry(entry);
		template.setStatus("1");//删除状态
		if(StringUtils.isNotBlank(form.getId())){//修改
			template.setId(form.getId());
			TemplateManage oldTemp = this.findTemplateManageById(form.getId());
			if(StringUtils.isNotBlank(oldTemp.getIsDefault())){
				template.setIsDefault(oldTemp.getIsDefault());
			}else{
				template.setIsDefault("0");
			}
			this.mergeTemplateManage(template);
		}else{//新增
			template.setId(Identities.uuid());
			if(null != findDefaultTemplateByentryId(form.getDictEntryId())){//存在默认模板
				template.setIsDefault("0");
			}else{
				template.setIsDefault("1");
			}
			this.mergeTemplateManage(template);
		}
	}
	
	/**
	 * 列表查询，根据数据字典项id查询所有的模板 
	 * add by 王再冉
	 * 2013-12-25  下午1:31:23
	 * desc : 
	 * @param entryId
	 * @return 
	 * List<Map<String,String>>
	 */
	public List<Map<String,String>> findTemplateManagesByentryIdGrid(String entryId,String query){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		List<String> entryIdList = new ArrayList<String>();
		entryIdList.add(entryId);
		List<DictEntry> entryList = o_dictBO.findChildrenDictEntrysById(entryId);
		if(null != entryList){
			for(DictEntry entry : entryList){
				entryIdList.add(entry.getId());
			}
		}
		List<TemplateManage> templateList = this.findTemplateManagesByentryIdList(entryIdList,query);
		for(TemplateManage temp : templateList){
			Map<String,String> map = new HashMap<String, String>();
			map.put("id", temp.getId());
			map.put("name", temp.getName());
			map.put("dictName", temp.getDictEntry().getName());
			map.put("isDefault", temp.getIsDefault());
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 根据数据字典项id查询所有的模板，(删除状态为“1”的)
	 * add by wangzairan
	 * 2013-12-25  下午1:38:21
	 * desc : 
	 * @param entryId
	 * @return 
	 * List<TemplateManage>
	 */
	@SuppressWarnings("unchecked")
	public TemplateManage findDefaultTemplateEntityByentryId(String entryId){
		Criteria criteria = o_templateManageDAO.createCriteria();
		List<TemplateManage> list = null;
		if(StringUtils.isNotBlank(entryId)){
			criteria.createAlias("dictEntry", "dictEntry", CriteriaSpecification.LEFT_JOIN, Restrictions.eq("dictEntry.id", entryId));
			criteria.add(Restrictions.and(Restrictions.and(Restrictions.eq("status", "1"), Restrictions.eq("isDefault", "1"))
					, Restrictions.eq("dictEntry.id", entryId)));
			list = criteria.list();
		}
		if(null != list && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	
	/**
	 * 根据数据字典id集合查询所有模板
	 * add by 王再冉
	 * 2013-12-26  上午11:06:20
	 * desc : 
	 * @param idList
	 * @return 
	 * List<TemplateManage>
	 */
	@SuppressWarnings("unchecked")
	public List<TemplateManage> findTemplateManagesByentryIdList(List<String> idList,String query){
		Criteria criteria = o_templateManageDAO.createCriteria();
		List<TemplateManage> list = new ArrayList<TemplateManage>();
		if(idList.size()>0){
			//criteria.add(Restrictions.eq("status", "1"));
			criteria.createAlias("dictEntry", "dictEntry", CriteriaSpecification.LEFT_JOIN, 
					Restrictions.in("dictEntry.id", idList));
			criteria.add(Restrictions.and(Restrictions.eq("status", "1"), Restrictions.in("dictEntry.id", idList)));
			if(StringUtils.isNotBlank(query)){
				criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
			}
			list = criteria.list();
		}
		return list;
	}
	
	/**
	 * 查询数据字典下的默认模板，没有返回null
	 * add by 王再冉
	 * 2013-12-25  下午2:56:03
	 * desc : 
	 * @param entryId	数据字典项id
	 * @return 
	 * TemplateManage
	 */
	public TemplateManage findDefaultTemplateByentryId(String entryId){
		TemplateManage template = null;
		TemplateManage tempDefault = this.findDefaultTemplateEntityByentryId(entryId);
		if(null != tempDefault){
			template = tempDefault;
		}
		return template;
	}
	
	/**
	 * 修改模板，显示模板相关内容
	 * add by 王再冉
	 * 2013-12-25  下午4:02:37
	 * desc : 
	 * @param id
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findTemplateManageFormById(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		TemplateManage template = this.findTemplateManageById(id);
		if(null != template){
			inmap.put("id", template.getId());
			inmap.put("dictEntryId", template.getDictEntry().getId());
			inmap.put("name", template.getName());
			inmap.put("content", template.getContent());
			map.put("success", true);
		}else{
			map.put("success", false);
		}
		map.put("data", inmap);
		return map;
	}
	
	/**
	 * 根据id删除模板实体
	 * add by 王再冉
	 * 2013-12-25  下午4:50:00
	 * desc : 
	 * @param ids
	 * @return 
	 * boolean
	 */
	@Transactional
	public void removeTemplateManagesByIds(String ids){
		o_templateManageDAO.createSQLQuery(" update t_sys_template_manage set delete_status='0' " +
				"where id in (:ids)").setParameterList("ids", StringUtils.split(ids,",")).executeUpdate();
	}
	
	/**
	 * 根据模板id设置默认模板，每个数据字典项下只能有一个默认模板
	 * add by 王再冉
	 * 2013-12-26  上午9:52:56
	 * desc : 
	 * @param id	设置为默认的模板id
	 * @return 
	 * Boolean
	 */
	@Transactional
	public Boolean setDefaultTemplateByTemplateId(String id){
		TemplateManage template = this.findTemplateManageById(id);
		if(null != template){
			TemplateManage oldDefault = this.findDefaultTemplateByentryId(template.getDictEntry().getId());//原来的默认模板
			if(null != oldDefault){
				oldDefault.setIsDefault("0");
				this.mergeTemplateManage(oldDefault);
			}
			template.setIsDefault("1");
			this.mergeTemplateManage(template);
			return true;
		}else{
			return false;
		}
	}

}
