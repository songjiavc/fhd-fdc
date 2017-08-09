package com.fhd.icm.business.assess;

import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.assess.AssessGuidelinesDAO;
import com.fhd.dao.icm.assess.AssessGuidelinesPropertyDAO;
import com.fhd.entity.icm.assess.AssessGuidelines;
import com.fhd.entity.icm.assess.AssessGuidelinesProperty;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;

/**
 * 评价标准模板BO
 * @author 邓广义
 */
@Service
@SuppressWarnings("unchecked")
public class AssessGuidelinesBO {
	
	@Autowired
	private AssessGuidelinesDAO o_assessGuidelinesDAO;
	@Autowired
	private AssessGuidelinesPropertyDAO o_assessGuidelinesPropertyDAO;
	
	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public AssessGuidelines findAssessGuidelinesById(String id){
		return o_assessGuidelinesDAO.get(id);
	}
	/**
	 * 获得评价模板列表.
	 * @author 郑广义
	 * @return List<AssessGuidelines>
	 */
	public List<AssessGuidelines> findAssessGuidelinesBySome(){
		return o_assessGuidelinesDAO.createCriteria().add(Restrictions.eq("deleteStatus", "1")).addOrder(Order.asc("sort")).list();
	}
	/**
	 * 根据ID删除实体
	 *  *逻辑删除*
	 * @param id
	 */
	@Transactional
	public boolean delAssessGuidelinesById(String id){
		Assert.hasText(id);
		boolean frag = false;
		String [] strs = id.split("\\,");
		for(int i=0,j=strs.length;i<j;i++){
			AssessGuidelines ag = o_assessGuidelinesDAO.get(strs[i]);
			Set<AssessGuidelinesProperty> setAgp = ag.getAssessGuidelinesProperty();
			for(AssessGuidelinesProperty agp :setAgp){
				this.delAssessGuidelinesPropertyById(agp.getId());
			}
			ag.setDeleteStatus("0");
			o_assessGuidelinesDAO.merge(ag);
			frag = i+1>j;
		}
		return frag;
	}
	/**
	 * 评价标准模板的保存方法
	 * @param form
	 */
	@Transactional
	public boolean saveAssessGuidelines(String modifiedRecords){
		
		boolean frag = false;
		String companyId = UserContext.getUser().getCompanyid();
		JSONArray jsonArray=JSONArray.fromObject(modifiedRecords);
		int j = jsonArray.size();
		for(int i=0;i<j;i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			String id = jsonObj.getString("id");
			String name = jsonObj.getString("name");
			String comment = jsonObj.getString("comment");
			Integer sort = jsonObj.getInt("sort");
			String dictype = jsonObj.getString("dictype");
			AssessGuidelines entity = null;
			if(!StringUtils.isBlank(id)){
				entity = o_assessGuidelinesDAO.get(id);
			}else{
				entity = new AssessGuidelines();
				entity.setId(Identities.uuid());
			}
			SysOrganization sysorg = new SysOrganization(companyId);
			DictEntry dic = new DictEntry(dictype);
			entity.setCompany(sysorg);
			entity.setName(name);
			entity.setComment(comment);
			entity.setDeleteStatus("1");
			entity.setSort(sort);
			entity.setType(dic);
			o_assessGuidelinesDAO.merge(entity);
			frag = i+1>=j;
		}
		return frag;
	}
	/**
	 * 通过评价标准模板ID查询该模板对应的评价标准项.
	 * @author 郑广义
	 * @param AssessGuidelinesId
	 * @return List<AssessGuidelinesProperty>
	 */
	public List<AssessGuidelinesProperty> findAssessGuidelinesPropertiesByAGId(String assessGuidelinesId){
		return o_assessGuidelinesPropertyDAO.createCriteria(Restrictions.eq("assessGuidelines.id", assessGuidelinesId)).addOrder(Order.asc("sort")).list();
	}
	/**
	 * 评价标准项的保存方法
	 * @param modifiedRecords
	 * @return boolean
	 */
	@Transactional
	public boolean saveAssessGuidelinesProperty(String modifiedRecords){
		
		boolean frag = false;
		JSONArray jsonArray=JSONArray.fromObject(modifiedRecords);
		int j = jsonArray.size();
		for(int i=0;i<j;i++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			
			String id = jsonObj.getString("id");
			String parentId = jsonObj.getString("parentId");
			String content = jsonObj.getString("content");
			String dictype = jsonObj.getString("dictype");
			Double minValue = jsonObj.getDouble("minValue");
			Double maxValue = jsonObj.getDouble("maxValue");
			Double judgeValue = jsonObj.getDouble("judgeValue");
			Integer sort = jsonObj.getInt("sort");
			AssessGuidelinesProperty entity = null;
			if(!StringUtils.isBlank(id)){
				entity = o_assessGuidelinesPropertyDAO.get(id);
			}else{
				entity = new AssessGuidelinesProperty(Identities.uuid());
			}
			DictEntry dic = new DictEntry(dictype);
			entity.setAssessGuidelines(o_assessGuidelinesDAO.get(parentId));
			entity.setContent(content);
			entity.setDefectLevel(dic);
			entity.setJudgeValue(judgeValue);
			entity.setMaxValue(maxValue);
			entity.setMinValue(minValue);
			entity.setSort(sort);
			o_assessGuidelinesPropertyDAO.merge(entity);
			frag = i+1>=j;
		}
		return frag;
		
	}
	/**
	 * 根据评价标准项ID删除实体
	 * @param id
	 * @return
	 */
	@Transactional
	public boolean delAssessGuidelinesPropertyById(String ids){
		Assert.hasText(ids);
		boolean frag = false;
		String [] strs = ids.split("\\,");
		for(int i=0,j=strs.length;i<j;i++){
				o_assessGuidelinesPropertyDAO.delete(strs[i]);
			frag = i+1>j;
		}
		return frag;
	}
	
	/**
	 * 根据评价计划id查询对应的评价模板详细.
	 * @param page
	 * @param assessPlanId
	 * @param sort
	 * @param query
	 * @return Page<AssessGuidelinesProperty>
	 */
	public Page<AssessGuidelinesProperty> findGuidelinesPropertyByAssessPlanId(Page<AssessGuidelinesProperty> page, String assessPlanId, String sort, String query){
		DetachedCriteria dc = DetachedCriteria.forClass(AssessGuidelinesProperty.class);
		dc.createAlias("assessGuidelines", "assessGuidelines");
		
		if(StringUtils.isNotBlank(assessPlanId)){
			//根据评价计划id查询评价计划关联的评价标准模板
			//dc.add(Restrictions.in("assessGuidelines.id", new Object[]{}));
		}
		if(StringUtils.isNotBlank(query)){
			dc.add(Restrictions.like("content", query, MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq("assessGuidelines.deleteStatus", "1"));
		
		dc.addOrder(Order.asc("assessGuidelines.name"));
		dc.addOrder(Order.asc("assessGuidelines.sort"));
		dc.addOrder(Order.asc("sort"));
		return o_assessGuidelinesPropertyDAO.findPage(dc, page, false);
	}
}