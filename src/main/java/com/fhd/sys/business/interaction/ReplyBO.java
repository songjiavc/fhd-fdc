package com.fhd.sys.business.interaction;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.sys.interaction.ReplyDAO;
import com.fhd.entity.sys.interaction.Reply;

/**
 * 回复业务
 * */

@Service
public class ReplyBO {
	@Autowired
	private ReplyDAO o_replyDAO;
	
	/**
	 * 根据帖子id查询所有回复
	 * add by 王再冉
	 * 2014-3-11  下午3:55:00
	 * desc : 
	 * @param postsId
	 * @return 
	 * List<Reply>
	 */
	@SuppressWarnings("unchecked")
	public List<Reply> findPostsByCompanyId(String postsId) {
		Criteria criteria = o_replyDAO.createCriteria();
		criteria.add(Restrictions.eq("posts.id", postsId));
		criteria.addOrder(Order.asc("createTime"));//按创建时间排序
		List<Reply> list = criteria.list();
		return list;
	}
	/**
	 * 保存回复实体
	 * add by 王再冉
	 * 2014-3-11  下午4:29:43
	 * desc : 
	 * @param reply 
	 * void
	 */
	@Transactional
	public void saveReply(Reply reply) {
		o_replyDAO.merge(reply);
	}
	
}
