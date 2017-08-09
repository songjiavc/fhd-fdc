package com.fhd.dao.check;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.check.checkcomment.CheckComment;
import com.fhd.entity.check.checkproject.CheckProject;
@Repository
public class CheckCommentDAO extends HibernateDao<CheckComment,String>{

}
