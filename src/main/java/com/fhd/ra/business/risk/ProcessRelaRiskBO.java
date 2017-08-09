package com.fhd.ra.business.risk;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.process.ProcessRelaRiskDAO;
import com.fhd.entity.process.ProcessRelaRisk;

/**
 * 流程相关风险BO.
 * @author 吴德福
 * @Date 2013-12-13 14:58:02
 */
@Service
@SuppressWarnings("unchecked")
public class ProcessRelaRiskBO {
	
	@Autowired
	private ProcessRelaRiskDAO  o_processRelaRiskDAO;

	/**
	 * 保存流程相关风险.
	 * @param processRelaRisk
	 */
	@Transactional
	public void saveProcessRelaRisk(ProcessRelaRisk processRelaRisk){
		o_processRelaRiskDAO.merge(processRelaRisk);
	}
	/**
	 * 根据id删除流程相关风险.
	 * @param id
	 */
	@Transactional
	public void removeProcessRelaRiskById(String id){
		o_processRelaRiskDAO.delete(id);
	}
	/**
	 * 根据公司id查询流程相关风险列表.
	 * @author 吴德福
	 * @param companyId
	 * @return List<ProcessRelaRisk>
	 */
	public List<ProcessRelaRisk> findRiskRelaProcessListByCompanyId(String companyId){
		Criteria criteria = o_processRelaRiskDAO.createCriteria();
		criteria.createAlias("process", "p");
		criteria.createAlias("p.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
}