package com.fhd.ra.business.assess.oper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.risk.TemplateRelaDimensionDAO;
import com.fhd.entity.assess.quaAssess.StatisticsResult;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.fdc.utils.NumberUtil;

@Service
public class TemplateRelaDimensionBO {

	@Autowired
	private TemplateRelaDimensionDAO o_templateRelaDimensionDAO;
	
	/**
	 * 查询模版关联纬度(主维度),并已MAP方式存储(模板关联维度主键ID,模板关联维度实体)
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, TemplateRelaDimension> findTemplateRelaDimensionIsParentIdIsNullAllMap(String companyId){
		HashMap<String, TemplateRelaDimension> map = new HashMap<String, TemplateRelaDimension>();
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		criteria.createAlias("template", "te");
		criteria.createAlias("dimension", "dim");
		criteria.add(Restrictions.isNull("parent.id"));
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.or(Restrictions.eq("te.company.id",companyId), Restrictions.eq("te.company.id", "")));
		}
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		for (TemplateRelaDimension templateRelaDimension : list) {
			map.put(templateRelaDimension.getId(), templateRelaDimension);
		}
		
		return map;
	}
	
	/**
	 * 查询模版关联,主维度下有多少子维度,并已MAP方式存储(模板关联维度ParentID,模板关联维度实体)
	 * @return HashMap<String, ArrayList<TemplateRelaDimension>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<TemplateRelaDimension>> findTemplateRelaDimensionIsParentIdIsNullInfoAllMap(){
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		HashMap<String, ArrayList<TemplateRelaDimension>> map = new HashMap<String, ArrayList<TemplateRelaDimension>>();
		ArrayList<TemplateRelaDimension> arrayList = null;
		
		for (TemplateRelaDimension templateRelaDimension : list) {
			if(null != templateRelaDimension.getParent()){
				if(map.get(templateRelaDimension.getParent().getId()) != null){
					map.get(templateRelaDimension.getParent().getId()).add(templateRelaDimension);
				}else{
					arrayList = new ArrayList<TemplateRelaDimension>();
					arrayList.add(templateRelaDimension);
					map.put(templateRelaDimension.getParent().getId(), arrayList);
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 查询维度对应模板全部信息,并已MAP方式存储(维度ID--模板ID,模板关联维度实体)
	 * @return HashMap<String, TemplateRelaDimension>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, TemplateRelaDimension> findTemplateRelaDimensionByIdAndTemplateIdAllMap(){
		HashMap<String, TemplateRelaDimension> map = new HashMap<String, TemplateRelaDimension>();
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		for (TemplateRelaDimension templateRelaDimension : list) {
			map.put(templateRelaDimension.getDimension().getId() + "--" + templateRelaDimension.getTemplate().getId(), templateRelaDimension);
		}
		
		
		return map;
	}
	
	/**
	 * 查询综合信息
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, TemplateRelaDimension>> findTemplateRelaDimensionMapByDimAll(){
		HashMap<String, HashMap<String, TemplateRelaDimension>> maps = new HashMap<String, HashMap<String,TemplateRelaDimension>>();
		
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		for (TemplateRelaDimension templateRelaDimension : list) {
			
			if(null != maps.get(templateRelaDimension.getTemplate().getId())){
				maps.get(templateRelaDimension.getTemplate().getId()).put(templateRelaDimension.getDimension().getId(), templateRelaDimension);
			}else{
				HashMap<String, TemplateRelaDimension> map = new HashMap<String, TemplateRelaDimension>();
				map.put(templateRelaDimension.getDimension().getId(), templateRelaDimension);
				maps.put(templateRelaDimension.getTemplate().getId(), map);
			}
			
			
		}
		
		return maps;
	}
	
	/**
	 * 查询模版关联维度全部信息,并已MAP方式存储(模板关联维度主键ID,模板关联维度实体)
	 * @return HashMap<String, TemplateRelaDimension>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, TemplateRelaDimension> findTemplateRelaDimensionAllMap(){
		HashMap<String, TemplateRelaDimension> map = new HashMap<String, TemplateRelaDimension>();
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		for (TemplateRelaDimension templateRelaDimension : list) {
			map.put(templateRelaDimension.getId(), templateRelaDimension);
		}
		
		return map;
	}
	
	/**
	 * 查询模版关联维度全部信息,并已MAP方式存储(模板关联维度主键ID,模板关联维度实体)
	 * @return HashMap<String, TemplateRelaDimension>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<TemplateRelaDimension>> findTemplateRelaDimensionByTempIdAllMap(){
		HashMap<String, List<TemplateRelaDimension>> map = new HashMap<String, List<TemplateRelaDimension>>();
		List<TemplateRelaDimension> listT = null;
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		for (TemplateRelaDimension templateRelaDimension : list) {
			if(null != templateRelaDimension.getTemplate()){
				if(null != map.get(templateRelaDimension.getTemplate().getId())){
					map.get(templateRelaDimension.getTemplate().getId()).add(templateRelaDimension);
				}else{
					listT = new ArrayList<TemplateRelaDimension>();
					listT.add(templateRelaDimension);
					map.put(templateRelaDimension.getTemplate().getId(), listT);
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 查询模版关联维度全部信息,并已MAP方式存储
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<TemplateRelaDimension>> findTemplateRelaDimensionParentIdAllMap(){
		HashMap<String, List<TemplateRelaDimension>> map = new HashMap<String, List<TemplateRelaDimension>>();
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		List<TemplateRelaDimension> lists = null;
		for (TemplateRelaDimension templateRelaDimension : list) {
			if(templateRelaDimension.getParent() != null){
				if(map.get(templateRelaDimension.getParent().getId()) != null){
					map.get(templateRelaDimension.getParent().getId()).add(templateRelaDimension);
				}else{
					lists = new ArrayList<TemplateRelaDimension>();
					lists.add(templateRelaDimension);
					map.put(templateRelaDimension.getParent().getId(), lists);
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 查询模版关联维度全部信息
	 * @return List<TemplateRelaDimension>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public List<TemplateRelaDimension> findTemplateRelaDimensionAllList(){
		List<TemplateRelaDimension> arrayList = new ArrayList<TemplateRelaDimension>();
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		criteria.createAlias("dimension", "dimension");
		criteria.addOrder(Order.asc("dimension.sort"));
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		for (TemplateRelaDimension templateRelaDimension : list) {
				arrayList.add(templateRelaDimension);
		}
		
		return arrayList;
	}
	
	/**
	 * 查询所有1及维度
	 * @return HashMap<String, HashMap<String, TemplateRelaDimension>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, TemplateRelaDimension>> findTemplateRelaDimensionParentIdIsNull(){
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		criteria.add(Restrictions.isNull("parent.id"));
		
		HashMap<String, HashMap<String, TemplateRelaDimension>> map = new HashMap<String, HashMap<String,TemplateRelaDimension>>();
		
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		for (TemplateRelaDimension templateRelaDimension : list) {
			if(null != map.get(templateRelaDimension.getTemplate().getId())){
				map.get(templateRelaDimension.getTemplate().getId()).put(templateRelaDimension.getId(), templateRelaDimension);
			}else{
				HashMap<String, TemplateRelaDimension> templateRelaDimensionMap = new HashMap<String, TemplateRelaDimension>();
				templateRelaDimensionMap.put(templateRelaDimension.getId(), templateRelaDimension);
				map.put(templateRelaDimension.getTemplate().getId(), templateRelaDimensionMap);
			}
		}
		
		return map;
	}
	
	/**
	 * 查询模版关联维度全部信息,并已MAP方式存储(模板关联维度主键ID,模板关联维度实体)
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	@SuppressWarnings("unchecked")
	public List<TemplateRelaDimension> findTemplateRelaDimensionAllList(String templateId){
		List<TemplateRelaDimension> list = null;
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		criteria.add(Restrictions.eq("template.id", templateId));
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 查询模版关联维度全部信息,并已MAP方式存储(模板关联维度主键ID,模板关联维度实体)
	 * @author 金鹏祥
	 * @return List<TemplateRelaDimension>
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, TemplateRelaDimension> findTemplateRelaDimensionAllMap(String templateId){
		HashMap<String, TemplateRelaDimension> map = new HashMap<String, TemplateRelaDimension>();
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		criteria.add(Restrictions.eq("template.id", templateId));
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		
		for (TemplateRelaDimension templateRelaDimension : list) {
			map.put(templateRelaDimension.getDimension().getId(), templateRelaDimension);
		}
		
		return map;
	}
	
	/**
	 * 查询所有子维度
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, ArrayList<TemplateRelaDimension>>> findTemplateRelaDimensionParentIdNotNull(){
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
//		criteria.add(Restrictions.eq("template.id", templateId));
		criteria.add(Restrictions.isNotNull("parent.id"));
		ArrayList<TemplateRelaDimension> lists = null;
		
		
		HashMap<String, HashMap<String, ArrayList<TemplateRelaDimension>>> map = new HashMap<String, HashMap<String,ArrayList<TemplateRelaDimension>>>();
		
		HashMap<String, ArrayList<TemplateRelaDimension>> templateRelaDimensionMap = new HashMap<String, ArrayList<TemplateRelaDimension>>();
		List<TemplateRelaDimension> list = null;
		list = criteria.list();
		for (TemplateRelaDimension templateRelaDimension : list) {
			if(templateRelaDimensionMap.get(templateRelaDimension.getParent().getId()) != null){
				templateRelaDimensionMap.get(templateRelaDimension.getParent().getId()).add(templateRelaDimension);
			}else{
				lists = new ArrayList<TemplateRelaDimension>();
				lists.add(templateRelaDimension);
				templateRelaDimensionMap.put(templateRelaDimension.getParent().getId(), lists);
			}
			
			
			map.put(templateRelaDimension.getTemplate().getId(), templateRelaDimensionMap);
			
		}
		
		return map;
	}
	
	/**
	 * 通过关联维度分值查询分值ID
	 * @param assessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> findScoreValueByTemplateRelaDimId(String templateRelaDimId){
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.id,a.score_dic_value from t_dim_score_dic a " +
				" LEFT JOIN t_dim_score_dic_inst b on a.id = b.score_dic_id " +
				" where b.TEMPLATE_rela_dim_id= :templateRelaDimId ");
        
        SQLQuery sqlQuery = o_templateRelaDimensionDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("templateRelaDimId", templateRelaDimId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String scoreId = "";
            String scoreValue = "";
            
            
            if(null != objects[0]){
            	scoreId = objects[0].toString();
            }if(null != objects[1]){
            	scoreValue = objects[1].toString();
            }
            map.put(scoreValue, scoreId);
        }
		
		return  map;
	}
}
