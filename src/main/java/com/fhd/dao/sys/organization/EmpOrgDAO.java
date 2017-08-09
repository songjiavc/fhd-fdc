package com.fhd.dao.sys.organization;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;

@Repository
public class EmpOrgDAO extends HibernateDao<SysEmpOrg,String> {
}