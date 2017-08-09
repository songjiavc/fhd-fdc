package com.fhd.dao.sys.sysapp;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.fhd.core.dao.HibernateEntityDao;
import com.fhd.entity.sys.app.Remind;
/**
 * 动态菜单DAO类.
 * @author  wudefu
 * @version V1.0  创建时间：2010-8-9
 * Company FirstHuiDa.
 */
@Repository
@SuppressWarnings({ "unchecked", "deprecation" })
public class RemindDAO extends HibernateEntityDao<Remind>{
	

	public List<Remind> getRemindsByUserid(String userId){
		String hql="select r from Remind r where r.user.id='"+userId+"'";
		Query query = null;
		query = this.getSession().createQuery(hql);
		return query.list();
	}
	public void deleteRemindsByUserid(String userId){
		String hql="delete Remind r where r.user.id='"+userId+"'";
		Query query = null;
		query = this.getSession().createQuery(hql);
		query.executeUpdate();
	}
	public Remind getRemindByUserAndDic(String userId,String dicId){
		String hql="select r from Remind r where r.user.id='"+userId+"' and r.dictEntry.id='"+dicId+"'";
		Query query = null;
		query = this.getSession().createQuery(hql);
		
		return (Remind)query.uniqueResult();
	}	
}