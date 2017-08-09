package com.fhd.sm.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.kpi.KpiDsParameterDAO;
import com.fhd.entity.kpi.KpiDsParameter;

/**
 * 指标采集参数BO ClassName:KpiDsParameterBO
 * 
 * @author 王鑫
 * @version
 * @since Ver 1.0
 * @Date 2013-7-29
 * 
 * @see
 */
@Service
public class KpiDsParameterBO {
	  @Autowired
      private KpiDsParameterDAO o_kpiDsParameterDAO; 
	  
		/**
		 *  根据参数类型和指标ID查找
		 *  @param objectId 指标id
		 *  @param valueType 采集值类型(实际值或目标值)
		 *  @return 数据参数
		 */
		@SuppressWarnings("unchecked")
		public List<Map<String, Object>> findDsParameterBySome(String objectId,String valueType) {
			 Criteria criteria = o_kpiDsParameterDAO.createCriteria();
			  if(StringUtils.isNotBlank(objectId)) {
				  criteria.add(Restrictions.eq("objectId", objectId));
			  }
			  if(StringUtils.isNotBlank(valueType)) {
				  criteria.add(Restrictions.eq("valueType", valueType));
			  }
			      criteria.addOrder(Order.asc("id"));
			  List<Map<String, Object>> jsonArray = new ArrayList<Map<String,Object>>();
			  List<KpiDsParameter> list = criteria.list();
			  for (KpiDsParameter kpi :list) {
				  Map<String, Object> inmap = new HashMap<String, Object>();
				  inmap.put("id", kpi.getId());
				  inmap.put("parameterName", kpi.getParamName());
				  inmap.put("parameterValue",kpi.getParamValue());
				  jsonArray.add(inmap);
			  }
			  return jsonArray;
		}
		
	  /**
	   * 插入实际值采集参数
	   * @param kpi
	   */
	  @Transactional
	  public void mergeResultParameter(KpiDsParameter kpi) {
		  o_kpiDsParameterDAO.merge(kpi);
	  }
	  
	  /**
	   *  根据参数类型和指标ID删除
	   *  @param objectId 指标id
	   *  @param valueType 采集值类型(实际值或目标值)
	   *  @return 数据参数
	   */
	  @SuppressWarnings("unchecked")
	  @Transactional
	  public void deleteDsParameterBySome(String objectId,String valueType) {
		  Criteria criteria = o_kpiDsParameterDAO.createCriteria();
		  if(StringUtils.isNotBlank(objectId)) {
			  criteria.add(Restrictions.eq("objectId", objectId));
		  }
		  if(StringUtils.isNotBlank(valueType)) {
			  criteria.add(Restrictions.eq("valueType", valueType));
		  }
		  List<KpiDsParameter> list = criteria.list();
		  for(KpiDsParameter kpi:list) {
			  o_kpiDsParameterDAO.delete(kpi); 
		  }
	  }
	

}
