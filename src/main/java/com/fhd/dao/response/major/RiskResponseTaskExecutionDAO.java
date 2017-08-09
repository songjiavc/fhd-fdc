package com.fhd.dao.response.major;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.response.RiskResponse;
import com.fhd.entity.response.major.RiskResponsePlanRiskRela;
import com.fhd.entity.response.major.RiskResponseTaskExecution;

@Repository
public class RiskResponseTaskExecutionDAO  extends HibernateDao<RiskResponseTaskExecution, String>{

}
