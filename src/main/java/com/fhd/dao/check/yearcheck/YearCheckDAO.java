package com.fhd.dao.check.yearcheck;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlan;
@Repository
public class YearCheckDAO extends HibernateDao<YearCheckPlan, String>{

}
