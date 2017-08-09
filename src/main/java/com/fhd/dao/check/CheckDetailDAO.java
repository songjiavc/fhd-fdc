package com.fhd.dao.check;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.check.checkdetail.CheckDetail;

@Repository
public class CheckDetailDAO extends HibernateDao<CheckDetail,String> {

}
