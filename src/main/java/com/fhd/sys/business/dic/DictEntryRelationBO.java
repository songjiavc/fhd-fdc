package com.fhd.sys.business.dic;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fhd.dao.sys.dic.DictEntryRelationDAO;
import com.fhd.dao.sys.dic.DictEntryRelationTypeDAO;
import com.fhd.entity.sys.dic.DictEntryRelation;
import com.fhd.entity.sys.dic.DictEntryRelationType;
import com.fhd.sys.interfaces.IDictRelationBO;

/**
 * 数据字典业务表关联类
 * @author 郑军祥
 * @Date 2013-6-4
 * 
 * @see
 */
@Service
public class DictEntryRelationBO implements IDictRelationBO {
	@Autowired
	private DictEntryRelationTypeDAO o_dictEntryRelationTypeDao;
	
    @Autowired
    private DictEntryRelationDAO o_dictEntryRelationDao;

    @Transactional
    public void saveDictEntryRelation(DictEntryRelation dictEntryRelation) {
		o_dictEntryRelationDao.merge(dictEntryRelation);
    }
    
    @Transactional
    public void removeDictEntryRelation(String id) {
    	o_dictEntryRelationDao.delete(id);
    }
    
    @Transactional
    public void removeDictEntryRelationByBusinessId(String typeId,String businessId) {
    	String hqlDelete = "delete DictEntryRelation where relationType.id = :typeId and businessId =:businessId";
		int deletedEntities = o_dictEntryRelationDao.createQuery(hqlDelete)
		        .setString( "typeId", typeId).setString( "businessId", businessId)
		        .executeUpdate();
    }
    
    public DictEntryRelationType findDictEntryRelationTypeByTypeId(String typeId){
    	Criteria criteria = o_dictEntryRelationTypeDao.createCriteria();
    	criteria.add(Restrictions.eq("id", typeId));
    	
    	return (DictEntryRelationType)criteria.uniqueResult();
    }
}