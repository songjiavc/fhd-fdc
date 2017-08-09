package com.fhd.icm.business.process;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.process.ProcessPointRelaMeasureDAO;
import com.fhd.entity.process.ProcessPointRelaMeasure;

/**
 * 流程节点相关控制措施BO.
 * @author 吴德福
 * @Date 2013-12-13 14:58:02
 */
@Service
@SuppressWarnings("unchecked")
public class ProcessPointRelaMeasureBO {

	@Autowired
	private ProcessPointRelaMeasureDAO o_processPointRelaMeasureDAO;
	
	/**
	 * 根据公司id查询流程节点相关控制措施列表.
	 * @author 吴德福
	 * @param companyId
	 * @return List<ProcessPointRelaMeasure>
	 */
	public List<ProcessPointRelaMeasure> findProcessPointRelaMeasureListByCompanyId(String companyId){
		Criteria criteria = o_processPointRelaMeasureDAO.createCriteria();
		criteria.createAlias("process", "p");
		criteria.createAlias("p.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
}
