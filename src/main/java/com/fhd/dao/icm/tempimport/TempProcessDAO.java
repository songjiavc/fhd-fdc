package com.fhd.dao.icm.tempimport;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.icm.tempimport.TempProcess;

/**
 * 流程导入临时表DAO.
 * @author 吴德福
 * @Date 2013-11-26 14:10:32
 */
@Repository
public class TempProcessDAO extends HibernateDao<TempProcess, String>{

}
