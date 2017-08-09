
package com.fhd.dao.sys.auth;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.sys.auth.SysAuthority;

@Repository
public class AuthorityDAO extends HibernateDao<SysAuthority, String> {

}

