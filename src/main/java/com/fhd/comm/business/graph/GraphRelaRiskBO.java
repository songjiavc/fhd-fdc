package com.fhd.comm.business.graph;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.comm.graph.GraphRelaRiskDAO;
import com.fhd.entity.comm.graph.GraphRelaRisk;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Service
public class GraphRelaRiskBO {

	@Autowired
	private GraphRelaRiskDAO o_graphRelaRiskDAO;
	
	/**
	 * <pre>
	 * 新增保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaRisk 图形关联风险的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public String saveGraphRelaRisk(GraphRelaRisk graphRelaRisk){
		String id = Identities.uuid();
		graphRelaRisk.setId(id);
		o_graphRelaRiskDAO.merge(graphRelaRisk);
		return id;
	}
	
	/**
	 * <pre>
	 * 更新保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaRisk 图形关联风险的关联关系
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeGraphRelaRisk(GraphRelaRisk graphRelaRisk){
		o_graphRelaRiskDAO.merge(graphRelaRisk);
	}
	
	/**
	 * <pre>
	 * 删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联风险的关联关系的ID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaRiskById(String id){
		o_graphRelaRiskDAO.delete(id);
	}
	
	/**
	 * <pre>
	 * 批量删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 图形关联风险的关联关系的ID的List集合
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaRiskByIdList(List<String> idList){
		if(null !=idList && idList.size()>0){
			StringBuffer hql = new StringBuffer();
			hql.append("delete GraphRelaRisk g where g.id in('").append(StringUtils.join(idList,"','")).append("')");
			o_graphRelaRiskDAO.createQuery(hql.toString()).executeUpdate();
		}
	}
	
	
	/**
	 * <pre>
	 * 查询单个图形关联风险的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联风险的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public GraphRelaRisk findGraphRelaRiskById(String id){
		return o_graphRelaRiskDAO.get(id);
	}
	
	/**
	 * <pre>
	 * 查询图形关联风险的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图形ID
	 * @param riskId 风险ID
	 * @param type 图形允许操作类型
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("unchecked")
	public List<GraphRelaRisk> findGraphRelaRiskListBySome(String graphId, String riskId, String type){
		Criteria criteria = o_graphRelaRiskDAO.createCriteria();
		criteria.createAlias("graph", "graph");
		if(StringUtils.isNotBlank(riskId)){
			criteria.add(Restrictions.eq("risk.id", riskId));
		}
		if(StringUtils.isNotBlank(graphId)){
			criteria.add(Restrictions.eq("graph.id", graphId));
		}
		if(StringUtils.isNotBlank(type)){
			criteria.add(Restrictions.eq("type", type));
		}
		criteria.addOrder(Order.asc("type"));
		criteria.addOrder(Order.desc("graph.lastModifyTime"));
		criteria.addOrder(Order.desc("graph.createTime"));
		return criteria.list();
	}
}

