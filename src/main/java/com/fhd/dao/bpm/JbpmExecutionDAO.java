package com.fhd.dao.bpm;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.bpm.JbpmExecution;

@Repository
public class JbpmExecutionDAO extends HibernateDao<JbpmExecution,Long> {
}
