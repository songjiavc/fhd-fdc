package com.fhd.dao.bpm;

import org.jbpm.pvm.internal.history.model.HistoryActivityInstanceImpl;
import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;

@Repository
public class HistoryActivityInstanceDAO extends HibernateDao<HistoryActivityInstanceImpl,Long> {
}