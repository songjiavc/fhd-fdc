package com.fhd.dao.sys.auth;



import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.sys.auth.SysUser;


@Repository
public class SysUserDAO extends HibernateDao<SysUser, String> {
	
}

