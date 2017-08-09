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
import com.fhd.dao.comm.graph.GraphRelaKpiDAO;
import com.fhd.entity.comm.graph.GraphRelaKpi;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Service
public class GraphRelaKpiBO {

	@Autowired
	private GraphRelaKpiDAO o_graphRelaKpiDAO;
	
	/**
	 * <pre>
	 * 新增保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaKpi 图形关联指标的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public String saveGraphRelaKpi(GraphRelaKpi graphRelaKpi){
		String id = Identities.uuid();
		graphRelaKpi.setId(id);
		o_graphRelaKpiDAO.merge(graphRelaKpi);
		return id;
	}
	
	/**
	 * <pre>
	 * 更新保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaKpi 图形关联指标的关联关系
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeGraphRelaKpi(GraphRelaKpi graphRelaKpi){
		o_graphRelaKpiDAO.merge(graphRelaKpi);
	}
	
	/**
	 * <pre>
	 * 删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联指标的关联关系的ID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaKpiById(String id){
		o_graphRelaKpiDAO.delete(id);
	}
	
	/**
	 * <pre>
	 * 批量删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 图形关联指标的关联关系的ID的List集合
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaKpiByIdList(List<String> idList){
		if(null !=idList && idList.size()>0){
			StringBuffer hql = new StringBuffer();
			hql.append("delete GraphRelaKpi g where g.id in('").append(StringUtils.join(idList,"','")).append("')");
			o_graphRelaKpiDAO.createQuery(hql.toString()).executeUpdate();
		}
	}
	
	
	/**
	 * <pre>
	 * 查询单个图形关联指标的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联指标的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public GraphRelaKpi findGraphRelaKpiById(String id){
		return o_graphRelaKpiDAO.get(id);
	}
	
	/**
	 * <pre>
	 * 查询图形关联指标的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图形ID
	 * @param kpiId 指标ID
	 * @param type 图形允许操作类型
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("unchecked")
	public List<GraphRelaKpi> findGraphRelaKpiListBySome(String graphId, String kpiId, String type){
		Criteria criteria = o_graphRelaKpiDAO.createCriteria();
		criteria.createAlias("graph", "graph");
		if(StringUtils.isNotBlank(kpiId)){
			criteria.add(Restrictions.eq("kpi.id", kpiId));
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

