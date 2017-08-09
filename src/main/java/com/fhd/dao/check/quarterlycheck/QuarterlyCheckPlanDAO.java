package com.fhd.dao.check.quarterlycheck;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.check.quarterlycheck.QuarterlyCheck;

@Repository
public class QuarterlyCheckPlanDAO extends HibernateDao<QuarterlyCheck, String> {

}
