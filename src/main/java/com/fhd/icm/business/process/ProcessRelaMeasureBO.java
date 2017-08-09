package com.fhd.icm.business.process;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.process.ProcessRelaMeasureDAO;
import com.fhd.entity.process.ProcessRelaMeasure;

/**
 * 流程相关控制措施BO.
 * @author 吴德福
 * @Date 2013-12-13 14:58:02
 */
@Service
@SuppressWarnings("unchecked")
public class ProcessRelaMeasureBO {

	@Autowired
	private ProcessRelaMeasureDAO o_processRelaMeasureDAO;
	
	/**
	 * 根据公司id查询流程相关控制措施列表.
	 * @author 吴德福
	 * @param companyId
	 * @return List<ProcessRelaMeasure>
	 */
	public List<ProcessRelaMeasure> findProcessRelaMeasureListByCompanyId(String companyId){
		Criteria criteria = o_processRelaMeasureDAO.createCriteria();
		criteria.createAlias("process", "p");
		criteria.createAlias("p.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
	
	/**
	 * 根据条件查询流程相关的控制措施列表
	 * @author zhanglei
	 * @param companyId 公司ID
	 * @param processId 流程ID
	 * @param measureId 控制措施ID
	 * @return
	 */
	public List<ProcessRelaMeasure> findProcessRelaMeasureListBySome(String companyId, String processId, String measureId){
		Criteria criteria = o_processRelaMeasureDAO.createCriteria();
		criteria.createAlias("process", "p");
		criteria.createAlias("controlMeasure", "m");
		criteria.createAlias("p.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		if(StringUtils.isNotBlank(processId)){
			criteria.add(Restrictions.eq("p.id", processId));
		}
		if(StringUtils.isNotBlank(measureId)){
			criteria.add(Restrictions.eq("m.id", measureId));
		}
		return criteria.list();
	}
}
