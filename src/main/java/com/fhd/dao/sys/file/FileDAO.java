/**
 * FileUploadDAO.java
 * com.fhd.fdc.commons.dao.dic
 *
 * Function : ENDO
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2010-8-14 		吴德福
 *
 * Copyright (c) 2010, Firsthuida All Rights Reserved.
*/

package com.fhd.dao.sys.file;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.sys.file.FileUploadEntity;

@Repository
public class FileDAO extends HibernateDao<FileUploadEntity,String>{
}

