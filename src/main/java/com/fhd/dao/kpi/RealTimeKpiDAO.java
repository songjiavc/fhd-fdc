package com.fhd.dao.kpi;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.kpi.RealTimeKpi;

/**
 * 实时指标DAO
 */
@Repository
public class RealTimeKpiDAO extends HibernateDao<RealTimeKpi, String> {

}
