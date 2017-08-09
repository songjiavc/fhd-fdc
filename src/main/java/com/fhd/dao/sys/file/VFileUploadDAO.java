/**
 * FileUploadDAO.java
 * com.fhd.fdc.commons.dao.dic
 *
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
import com.fhd.entity.sys.file.VFileUploadEntity;

/**
 * 
 * ClassName:VFileUploadDAO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-9-10		下午6:21:56
 *
 * @see
 */
@Repository
public class VFileUploadDAO extends HibernateDao<VFileUploadEntity,String>{
}

