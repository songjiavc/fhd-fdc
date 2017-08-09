package com.fhd.dao.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.response.SolutionExecutionHistory;

/**
 * 控制措施
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-9-17		下午2:23:09
 *
 * @see 	 
 */
@Repository
public class SolutionExecutionHistoryDAO extends HibernateDao<SolutionExecutionHistory, String> {
	public Map<String,Object> getParameterMap(){
		return new HashMap<String,Object>();
	}
}