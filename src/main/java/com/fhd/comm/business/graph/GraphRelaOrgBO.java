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
import com.fhd.dao.comm.graph.GraphRelaOrgDAO;
import com.fhd.entity.comm.graph.GraphRelaOrg;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Service
public class GraphRelaOrgBO {

	@Autowired
	private GraphRelaOrgDAO o_graphRelaOrgDAO;
	
	/**
	 * <pre>
	 * 新增保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaOrg 图形关联组织的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public String saveGraphRelaOrg(GraphRelaOrg graphRelaOrg){
		String id = Identities.uuid();
		graphRelaOrg.setId(id);
		o_graphRelaOrgDAO.merge(graphRelaOrg);
		return id;
	}
	
	/**
	 * <pre>
	 * 更新保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaOrg 图形关联组织的关联关系
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeGraphRelaOrg(GraphRelaOrg graphRelaOrg){
		o_graphRelaOrgDAO.merge(graphRelaOrg);
	}
	
	/**
	 * <pre>
	 * 删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联组织的关联关系的ID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaOrgById(String id){
		o_graphRelaOrgDAO.delete(id);
	}
	
	/**
	 * <pre>
	 * 批量删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 图形关联组织的关联关系的ID的List集合
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaOrgByIdList(List<String> idList){
		if(null !=idList && idList.size()>0){
			StringBuffer hql = new StringBuffer();
			hql.append("delete GraphRelaOrg g where g.id in('").append(StringUtils.join(idList,"','")).append("')");
			o_graphRelaOrgDAO.createQuery(hql.toString()).executeUpdate();
		}
	}
	
	
	/**
	 * <pre>
	 * 查询单个图形关联组织的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联组织的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public GraphRelaOrg findGraphRelaOrgById(String id){
		return o_graphRelaOrgDAO.get(id);
	}
	
	/**
	 * <pre>
	 * 查询图形关联组织的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图形ID
	 * @param orgId 组织ID
	 * @param type 图形允许操作类型
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("unchecked")
	public List<GraphRelaOrg> findGraphRelaOrgListBySome(String graphId, String orgId, String type){
		Criteria criteria = o_graphRelaOrgDAO.createCriteria();
		criteria.createAlias("graph", "graph");
		if(StringUtils.isNotBlank(orgId)){
			criteria.add(Restrictions.eq("org.id", orgId));
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

