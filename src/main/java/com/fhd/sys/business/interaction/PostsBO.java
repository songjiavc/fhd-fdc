package com.fhd.sys.business.interaction;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.sys.interaction.PostsDAO;
import com.fhd.entity.sys.interaction.Posts;

/**
 * 帖子业务
 * */

@Service
public class PostsBO {
	@Autowired
	private PostsDAO o_postsDAO;
	
	/**
	 * 查询该公司下所有的帖子
	 * add by 王再冉
	 * 2014-3-11  上午11:38:02
	 * desc : 
	 * @param companyId	公司id
	 * @return 
	 * List<Posts>
	 */
	@SuppressWarnings("unchecked")
	public List<Posts> findPostsByCompanyId(String companyId,String query) {
		Criteria criteria = o_postsDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));//未删除
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("title", query, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.desc("createTime"));//按创建时间排序
		List<Posts> list = criteria.list();
		return list;
	}
	
	/**
	 * 保存帖子实体
	 * add by 王再冉
	 * 2014-3-11  下午1:50:05
	 * desc : 
	 * @param posts 
	 * void
	 */
	@Transactional
	public void savePosts(Posts posts) {
		o_postsDAO.merge(posts);
	}
	/**
	 * 根据id查询帖子实体
	 * add by 王再冉
	 * 2014-3-11  下午3:03:55
	 * desc : 
	 * @param id	帖子id
	 * @return 
	 * Posts
	 */
	public Posts findPostsById(String id){
		return o_postsDAO.get(id);
	}

}
