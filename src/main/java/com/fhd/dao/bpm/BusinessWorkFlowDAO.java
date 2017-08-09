package com.fhd.dao.bpm;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.bpm.BusinessWorkFlow;


@Repository
public class BusinessWorkFlowDAO  extends HibernateDao<BusinessWorkFlow,String>{
}

