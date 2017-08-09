package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;

@Service
public class RiskDataAddFlowBO {

	@Autowired
	private JBPMBO o_jbpmBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	

	/**
	 * 工作流第一步--风险信息添加开启工作流
	 * 由定时任务开启
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Transactional
	public boolean submitSavedRiskToApprover(){
		String entityType = "riskDataAdd";	//流程名称
		
//		//1.从库中查出某部门下新保存的风险列表,最近10分钟修改的风险
//		List<Risk> savedRiskList = new ArrayList<Risk>();
//		Criteria c = o_riskDAO.createCriteria();
//		Calendar calendar = Calendar.getInstance();
//		Date now = calendar.getTime();
//		calendar.add(Calendar.MINUTE, -10);
//		Date ago = calendar.getTime();
//		c.add(Restrictions.between("lastModifyTime", ago, now));
//		savedRiskList = c.list();
		
		//1.从库中查出新保存的风险事件列表,把这些风险事件涉及的部门列出来，然后给这些部门风险管理员发待办任务.
		//  要求风险添加的时候，责任部门必须有，责任部门的风险管理员必须有，这样才能审批。否则审批不了
		List<Risk> savedRiskList = new ArrayList<Risk>();
		Criteria c = o_riskDAO.createCriteria();
		c.add(Restrictions.eq("archiveStatus", Contents.RISK_STATUS_SUBMITED));
		c.add(Restrictions.eq("deleteStatus", "1"));
		c.add(Restrictions.eq("isRiskClass", "re"));
		savedRiskList = c.list();
		
		//2.1将库中的新保存风险状态修改成待审批状态;
		//2.2  查询出新保存风险的部门
		Set<SysOrganization>  orgSet = new HashSet<SysOrganization>();
		for(Risk r : savedRiskList){
			r.setArchiveStatus(Contents.RISK_STATUS_WAITINGAPPROVE);
			o_riskDAO.merge(r);
			//查询添加风险的责任部门，责任部门有2个呢？最后汇总到风险管理员那会有问题；部门领导有2个呢？先暂时都按第1个算
			Set<RiskOrg> riskOrgs = r.getRiskOrgs();
			for(RiskOrg riskOrg : riskOrgs){
				if(riskOrg.getType().equals(Contents.DUTY_DEPARTMENT)){	//责任部门
					SysOrganization org = riskOrg.getSysOrganization();
					orgSet.add(org);
				}
			}
		}
		
		//3.开启工作流
		for(SysOrganization org : orgSet){
			String deptId = org.getId();
			String deptName = org.getOrgname();
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("entityType", entityType);
			variables.put("id", deptId);//部门名称
			variables.put("name", deptName);//部门名称
			//根据部门id找到部门领导
			String roleCode = "DeptRiskManager";	//部门风险管理员角色编号
			SysEmployee emp = o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId, roleCode);
			String deptLeaderId = emp.getId();//"zhangcheng";
			variables.put("RiskAddApproverEmpId", deptLeaderId);//审批部门领导
			String executionId = o_jbpmBO.startProcessInstance(entityType,variables);//开启工作流
		}
		
		return true;
	}
	
	/**
	 * 工作流第二步--部门领导审批风险添加
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 * @param riskIds  审批通过的风险id字符串，例如‘111,222',审批的表单是批量审批
	 */
	@Transactional
	public void approveRisk(String executionId, String businessId, String isPass, String examineApproveIdea, String riskIds){
		if ("no".equals(isPass)) {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("path", isPass);
			variables.put("examineApproveIdea", examineApproveIdea);
			o_jbpmBO.doProcessInstance(executionId, variables);
		}else{
				//审批通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				//更加部门id找到风险管理员
				String roleCode = "RiskManagemer";
				SysEmployee emp = o_riskScoreBO.findEmpsByRoleIdAnddeptId(businessId, roleCode);
				String companyLeaderId = emp.getId();//"zhangcheng";
				variables.put("RiskAddManagerApproverEmpId", companyLeaderId);//风险管理员
				o_jbpmBO.doProcessInstance(executionId, variables);
				
				//修改部门下审批通过风险的状态
				String[] idArr = riskIds.split(",");
				for(int i=0;i<idArr.length;i++){
					Risk r = o_riskBO.findRiskById(idArr[i]);
					r.setArchiveStatus(Contents.RISK_STATUS_WAITINGARCHIVE);
					o_riskDAO.merge(r);
				}
		}
	}
	
	/**
	 * 工作流第三步--管理员归档风险添加,结束工作流
	 * @param executionId
	 * @param businessId
	 * @param isPass
	 * @param examineApproveIdea
	 */
	@Transactional
	public void archiveRisk(String executionId, String businessId, String isPass, String examineApproveIdea,String riskIds){
		Map<String, Object> variables = new HashMap<String, Object>();
		o_jbpmBO.doProcessInstance(executionId, variables);
		
		//修改部门下审批通过风险的状态
		String[] idArr = riskIds.split(",");
		for(int i=0;i<idArr.length;i++){
			Risk r = o_riskBO.findRiskById(idArr[i]);
			r.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
			o_riskDAO.merge(r);
		}
	}
}
