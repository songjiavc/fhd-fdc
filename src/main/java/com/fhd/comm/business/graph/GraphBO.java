package com.fhd.comm.business.graph;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.comm.graph.GraphDAO;
import com.fhd.entity.comm.graph.Graph;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Service
public class GraphBO {
	
	@Autowired
	private GraphDAO o_graphDAO;
	
	/**
	 * <pre>
	 * 新增保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graph 图形
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public String saveGraph(Graph graph){
		String id = Identities.uuid();
		graph.setId(id);
		o_graphDAO.merge(graph);
		return id;
	}
	
	/**
	 * <pre>
	 * 更新保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graph 图形
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeGraph(Graph graph){
		o_graphDAO.merge(graph);
	}
	
	/**
	 * <pre>
	 * 删除
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形ID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphById(String id){
		o_graphDAO.delete(id);
	}
	
	/**
	 * <pre>
	 * 批量删除
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 图形ID的List集合
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphByIdList(List<String> idList){
		if(null !=idList && idList.size()>0){
			StringBuffer hql = new StringBuffer();
			hql.append("delete Graph g where g.id in('").append(StringUtils.join(idList,"','")).append("')");
			o_graphDAO.createQuery(hql.toString()).executeUpdate();
		}
	}
	
	/**
	 * <pre>
	 * 查询单个图形
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Graph findGraphById(String id){
		return o_graphDAO.get(id);
	}
	
	/**
	 * <pre>
	 * 分页查询图形列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param page
	 * @param query
	 * @param comanyId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Page<Graph> findGraphPageBySome(Page<Graph> page, String query, String companyId){
		DetachedCriteria criteria = DetachedCriteria.forClass(Graph.class);
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(query)){//模糊匹配缺陷的编号和名称
			criteria.add(Property.forName("name").like(query, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.desc("lastModifyTime"));
		criteria.addOrder(Order.desc("createTime"));
		return o_graphDAO.findPage(criteria, page, false);
	}
	
}

