package com.fhd.sys.business.dic;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.sys.dic.DictTypeDAO;
import com.fhd.entity.sys.dic.DictType;

@Service
public class DictTypeTreeBO {
    @Autowired
    private DictTypeBO o_dictTypeBO;
    @Autowired
    private DictTypeDAO o_dictTypeDAO;
    
    /**
     * 
     * getTreeRoot:得到根节点
     * 
     * @author 杨鹏
     * @return
     * @since  fhd　Ver 1.1
     */
	public DictType getTreeRoot() {
		Criteria createCriteria = o_dictTypeDAO.createCriteria();
		createCriteria.add(Restrictions.isNull("parent"));
		return (DictType)createCriteria.uniqueResult();
	}
    
	/**
	 * 
	 * treeLoader:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param nodeId
	 * @param property
	 * @param direction
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<DictType> treeLoader(String query,String nodeId,String property, String direction) {
		return o_dictTypeBO.findBySome(query, nodeId, property, direction);
	}
    
}
