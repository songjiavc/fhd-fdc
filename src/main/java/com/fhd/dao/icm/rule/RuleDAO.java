package com.fhd.dao.icm.rule;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.icm.rule.Rule;

@Repository
public class RuleDAO extends HibernateDao<Rule, String> {

}
