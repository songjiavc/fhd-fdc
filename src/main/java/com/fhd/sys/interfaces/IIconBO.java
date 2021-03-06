/**
 * IIconBO.java
 * com.fhd.sys.interfaces
 *
 * Function : ENDO
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-9-18 		张 雷
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.interfaces;

import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.entity.sys.icon.Icon;

/**
 * ClassName:IDictBO
 * Function: ADD FUNCTION
 * Reason: ADD REASON
 *
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-9-18		下午3:47:27
 *
 * @see 	 
 */
@Service
public interface IIconBO {
	public Page<Icon> findIconByQuery(String query,String sort,Page<Icon> page);
}

