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
import com.fhd.dao.comm.graph.GraphRelaCategoryDAO;
import com.fhd.entity.comm.graph.GraphRelaCategory;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 *
 * @see 	 
 */
@Service
public class GraphRelaCategoryBO {

	@Autowired
	private GraphRelaCategoryDAO o_graphRelaCategoryDAO;
	
	/**
	 * <pre>
	 * 新增保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaCategory 图形关联记分卡的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public String saveGraphRelaCategory(GraphRelaCategory graphRelaCategory){
		String id = Identities.uuid();
		graphRelaCategory.setId(id);
		o_graphRelaCategoryDAO.merge(graphRelaCategory);
		return id;
	}
	
	/**
	 * <pre>
	 * 更新保存
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphRelaCategory 图形关联记分卡的关联关系
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeGraphRelaCategory(GraphRelaCategory graphRelaCategory){
		o_graphRelaCategoryDAO.merge(graphRelaCategory);
	}
	
	/**
	 * <pre>
	 * 删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联记分卡的关联关系的ID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaCategoryById(String id){
		o_graphRelaCategoryDAO.delete(id);
	}
	
	/**
	 * <pre>
	 * 批量删除关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param idList 图形关联记分卡的关联关系的ID的List集合
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeGraphRelaCategoryByIdList(List<String> idList){
		if(null !=idList && idList.size()>0){
			StringBuffer hql = new StringBuffer();
			hql.append("delete GraphRelaCategory g where g.id in('").append(StringUtils.join(idList,"','")).append("')");
			o_graphRelaCategoryDAO.createQuery(hql.toString()).executeUpdate();
		}
	}
	
	
	/**
	 * <pre>
	 * 查询单个图形关联记分卡的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 图形关联记分卡的关联关系
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public GraphRelaCategory findGraphRelaCategoryById(String id){
		return o_graphRelaCategoryDAO.get(id);
	}
	
	/**
	 * <pre>
	 * 查询图形关联记分卡的关联关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphId 图形ID
	 * @param categoryId 记分卡ID
	 * @param type 图形允许操作类型
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("unchecked")
	public List<GraphRelaCategory> findGraphRelaCategoryListBySome(String graphId, String categoryId, String type){
		Criteria criteria = o_graphRelaCategoryDAO.createCriteria();
		criteria.createAlias("graph", "graph");
		if(StringUtils.isNotBlank(categoryId)){
			criteria.add(Restrictions.eq("category.id", categoryId));
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

