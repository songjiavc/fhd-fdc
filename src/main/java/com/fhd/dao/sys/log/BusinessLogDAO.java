package com.fhd.dao.sys.log;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.sys.log.BusinessLog;

/**
 * 业务日志DAO类.
 * @author  吴德福
 * @version V1.0  创建时间：2013-10-12
 * Company FirstHuiDa.
 */
@Repository
public class BusinessLogDAO extends HibernateDao<BusinessLog, String>{

}

