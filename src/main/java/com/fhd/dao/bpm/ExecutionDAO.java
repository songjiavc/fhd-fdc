package com.fhd.dao.bpm;

import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;

@Repository
public class ExecutionDAO extends HibernateDao<ExecutionImpl,Long> {
}
