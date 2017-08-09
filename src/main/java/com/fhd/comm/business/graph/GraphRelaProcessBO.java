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
import com.fhd.dao.comm.graph.GraphRelaProcessDAO;
import com.fhd.entity.comm.graph.GraphRelaProcess;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Service
public class GraphRelaProcessBO {

	@Autowired
	private GraphRelaProcessDAO o_graphRelaProcessDAO;
	
	/**
	 * <pre>
	 * 新增保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaProcess 图形关联流程的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public String saveGraphRelaProcess(GraphRelaProcess graphRelaProcess){
		String id = Identities.uuid();
		graphRelaProcess.setId(id);
		o_graphRelaProcessDAO.merge(graphRelaProcess);
		return id;
	}
	
	/**
	 * <pre>
	 * 更新保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaProcess 图形关联流程的关联关系
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeGraphRelaProcess(GraphRelaProcess graphRelaProcess){
		o_graphRelaProcessDAO.merge(graphRelaProcess);
	}
	
	/**
	 * <pre>
	 * 删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联流程的关联关系的ID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaProcessById(String id){
		o_graphRelaProcessDAO.delete(id);
	}
	
	/**
	 * <pre>
	 * 批量删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 图形关联流程的关联关系的ID的List集合
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaProcessByIdList(List<String> idList){
		if(null !=idList && idList.size()>0){
			StringBuffer hql = new StringBuffer();
			hql.append("delete GraphRelaProcess g where g.id in('").append(StringUtils.join(idList,"','")).append("')");
			o_graphRelaProcessDAO.createQuery(hql.toString()).executeUpdate();
		}
	}
	
	
	/**
	 * <pre>
	 * 查询单个图形关联流程的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联流程的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public GraphRelaProcess findGraphRelaProcessById(String id){
		return o_graphRelaProcessDAO.get(id);
	}
	
	/**
	 * <pre>
	 * 查询图形关联流程的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图形ID
	 * @param processId 流程ID
	 * @param type 图形允许操作类型
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("unchecked")
	public List<GraphRelaProcess> findGraphRelaProcessListBySome(String graphId, String processId, String type){
		Criteria criteria = o_graphRelaProcessDAO.createCriteria();
		criteria.createAlias("graph", "graph");
		if(StringUtils.isNotBlank(processId)){
			criteria.add(Restrictions.eq("process.id", processId));
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

