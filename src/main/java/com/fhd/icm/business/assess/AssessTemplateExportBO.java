package com.fhd.icm.business.assess;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.dao.bpm.BusinessWorkFlowDAO;
import com.fhd.dao.icm.assess.AssessPlanProcessRelaOrgEmpDAO;
import com.fhd.dao.icm.assess.AssessResultDAO;
import com.fhd.entity.bpm.BusinessWorkFlow;
import com.fhd.entity.icm.assess.AssessPlanProcessRelaOrgEmp;
import com.fhd.entity.icm.assess.AssessResult;
import com.fhd.fdc.utils.Contents;

@Service
public class AssessTemplateExportBO {

	@Autowired
	private AssessPlanProcessRelaOrgEmpDAO o_assessPlanProcessRelaOrgEmpDAO;
	@Autowired
	private AssessResultDAO o_assessResultDAO;
	@Autowired
	private BusinessWorkFlowDAO o_businessWorkFlowDAO;
	
	/**
	 * 查询某人的当前要评价执行的流程范围
	 * @param page 
	 * @param empId 员工ID
	 * @param companyId 公司ID
	 * @return
	 */
	public Page<AssessPlanProcessRelaOrgEmp> findAssessPlanProcessRelaOrgEmpPageBySome(Page<AssessPlanProcessRelaOrgEmp> page, 
			String empId, String companyId){
		DetachedCriteria dc= DetachedCriteria.forClass(AssessPlanProcessRelaOrgEmp.class);
		dc.createAlias("assessPlanRelaOrgEmp", "assessPlanRelaOrgEmp");
		dc.createAlias("assessPlanRelaOrgEmp.assessPlan", "assessPlan");
		dc.createAlias("assessPlanRelaProcess", "assessPlanRelaProcess");
		dc.createAlias("assessPlanRelaProcess.process", "process");
		dc.createAlias("assessPlan.company", "company");
		dc.createAlias("assessPlanRelaOrgEmp.emp", "emp");
		dc.add(Restrictions.eq("company.id", companyId));
		if(StringUtils.isNotBlank(empId)){
			dc.add(Restrictions.eq("emp.id", empId));
		}
		dc.add(Restrictions.eq("type", Contents.EMP_HANDLER));
		return o_assessPlanProcessRelaOrgEmpDAO.findPage(dc, page, false);
	}
	/**
	 * 
	 * findBusinessWorkFlowByBusinessIds:
	 * 
	 * @author 杨鹏
	 * @param businessIds
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<BusinessWorkFlow> findBusinessWorkFlowByBusinessIds(String...businessIds){
		Criteria criteria = o_businessWorkFlowDAO.createCriteria();
		criteria.add(Restrictions.in("businessId", businessIds));
		return criteria.list();
	}

	
	/**
	 * 查询评价结果
	 * @author zhanglei
	 * @param testType
	 * @param assessPlanId
	 * @param processIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AssessResult> findAssessResultListBySome(String testType, String assessPlanId, String...processIds){
		Criteria criteria = o_assessResultDAO.createCriteria();
		criteria.add(Restrictions.in("process.id", processIds));
		criteria.add(Restrictions.eq("assessPlan.id", assessPlanId));
		criteria.createAlias("process", "process",CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("assessPoint", "assessPoint",CriteriaSpecification.LEFT_JOIN);
		if(Contents.ASSESS_MEASURE_PRACTICE_TEST.equals(testType)){
			criteria.add(Restrictions.eq("assessPoint.type", Contents.ASSESS_POINT_TYPE_DESIGN));
		}else if(Contents.ASSESS_MEASURE_SAMPLE_TEST.equals(testType)){
			criteria.add(Restrictions.eq("assessPoint.type", Contents.ASSESS_POINT_TYPE_EXECUTE));
		}
		criteria.addOrder(Order.asc("process.id"));
		criteria.addOrder(Order.asc("assessPoint.type"));
		return criteria.list();
	}
}
