package com.fhd.dao.risk;

import org.springframework.stereotype.Repository;

import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.risk.HistoryEvent;

/**
 * 历史事件DAO
 * 
 * @author 张健
 * @date 2013-11-6
 * @since Ver 1.1
 */
@Repository
public class HistoryEventDAO  extends HibernateDao<HistoryEvent, String>{

}