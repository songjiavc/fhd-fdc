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
import com.fhd.dao.comm.graph.GraphRelaStrategyMapDAO;
import com.fhd.entity.comm.graph.GraphRelaStrategyMap;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Service
public class GraphRelaStrategyMapBO {

	@Autowired
	private GraphRelaStrategyMapDAO o_graphRelaStrategyMapDAO;
	
	/**
	 * <pre>
	 * 新增保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaStrategyMap 图形关联目标的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public String saveGraphRelaStrategyMap(GraphRelaStrategyMap graphRelaStrategyMap){
		String id = Identities.uuid();
		graphRelaStrategyMap.setId(id);
		o_graphRelaStrategyMapDAO.merge(graphRelaStrategyMap);
		return id;
	}
	
	/**
	 * <pre>
	 * 更新保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaStrategyMap 图形关联目标的关联关系
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeGraphRelaStrategyMap(GraphRelaStrategyMap graphRelaStrategyMap){
		o_graphRelaStrategyMapDAO.merge(graphRelaStrategyMap);
	}
	
	/**
	 * <pre>
	 * 删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联目标的关联关系的ID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaStrategyMapById(String id){
		o_graphRelaStrategyMapDAO.delete(id);
	}
	
	/**
	 * <pre>
	 * 批量删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 图形关联目标的关联关系的ID的List集合
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaStrategyMapByIdList(List<String> idList){
		if(null !=idList && idList.size()>0){
			StringBuffer hql = new StringBuffer();
			hql.append("delete GraphRelaStrategyMap g where g.id in('").append(StringUtils.join(idList,"','")).append("')");
			o_graphRelaStrategyMapDAO.createQuery(hql.toString()).executeUpdate();
		}
	}
	
	
	/**
	 * <pre>
	 * 查询单个图形关联目标的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联目标的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public GraphRelaStrategyMap findGraphRelaStrategyMapById(String id){
		return o_graphRelaStrategyMapDAO.get(id);
	}
	
	/**
	 * <pre>
	 * 查询图形关联目标的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图形ID
	 * @param strategyMapId 目标ID
	 * @param type 图形允许操作类型
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("unchecked")
	public List<GraphRelaStrategyMap> findGraphRelaStrategyMapListBySome(String graphId, String strategyMapId, String type){
		Criteria criteria = o_graphRelaStrategyMapDAO.createCriteria();
		criteria.createAlias("graph", "graph");
		if(StringUtils.isNotBlank(strategyMapId)){
			criteria.add(Restrictions.eq("strategyMap.id", strategyMapId));
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

