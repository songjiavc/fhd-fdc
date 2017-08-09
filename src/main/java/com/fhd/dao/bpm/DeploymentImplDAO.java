package com.fhd.dao.bpm;

import org.jbpm.pvm.internal.repository.DeploymentImpl;
import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;

@Repository
public class DeploymentImplDAO extends HibernateDao<DeploymentImpl,Long> {
}
