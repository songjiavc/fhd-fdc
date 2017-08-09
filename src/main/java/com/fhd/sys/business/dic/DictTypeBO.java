package com.fhd.sys.business.dic;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.sys.dic.DictTypeDAO;
import com.fhd.entity.sys.dic.DictType;

/**
 * 
 * ClassName:DictTypeBO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-8		下午3:49:54
 *
 * @see
 */
@Service
public class DictTypeBO{
    @Autowired
    private DictTypeDAO o_dictTypeDAO;
    /**
     * 
     * findBySome:
     * 
     * @author 杨鹏
     * @param name
     * @param parentId
     * @param property
     * @param direction
     * @return
     * @since  fhd　Ver 1.1
     */
    @SuppressWarnings("unchecked")
	public List<DictType> findBySome(String name,String parentId,String property, String direction){
		Criteria criteria = o_dictTypeDAO.createCriteria();
		if(StringUtils.isNotBlank(name)){
			criteria.add(Restrictions.eq("name", name));
		}
		if(StringUtils.isNotBlank(parentId)&&!"null".equals(parentId)){
			criteria.add(Restrictions.eq("parent.id", parentId));
		}else if("null".equals(parentId)){
			criteria.add(Restrictions.isNull("parent"));
		}
		if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
			if("desc".equalsIgnoreCase(direction)){
				criteria.addOrder(Order.desc(property));
			}else{
				criteria.addOrder(Order.asc(property));
			}
		}
		return criteria.list();
	}
    
	/**
	 * 
	 * removeById:根据ID删除
	 * 
	 * @author 杨鹏
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeById(String id) {
		o_dictTypeDAO.delete(id);
	}
    
}