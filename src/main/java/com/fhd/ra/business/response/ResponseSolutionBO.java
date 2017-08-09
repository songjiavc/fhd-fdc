/**
 * AssessPlanBO.java
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		刘中帅
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.business.response;

import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.assess.MeasureRelaOrgDAO;
import com.fhd.dao.response.SolutionDAO;
import com.fhd.dao.response.SolutionRelaFileDAO;
import com.fhd.dao.response.SolutionRelaOrgDAO;
import com.fhd.dao.response.SolutionRelaRiskDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.icm.control.MeasureRelaOrg;
import com.fhd.entity.response.Solution;
import com.fhd.entity.response.SolutionRelaFile;
import com.fhd.entity.response.SolutionRelaOrg;
import com.fhd.entity.response.SolutionRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.ra.interfaces.response.ISolutionOutSiteBO;
import com.fhd.ra.web.form.response.solution.SolutionForm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应对措施，控制措施列表查询
 * 
 * @author 张健
 * @date 2014-3-6
 * @since Ver 1.1
 */
@Service
@SuppressWarnings("unchecked")
public class ResponseSolutionBO implements ISolutionOutSiteBO{
	@Autowired
	private MeasureRelaOrgDAO o_measureRelaOrgDAO;
	@Autowired
	private SolutionDAO o_solutionDAO;
	@Autowired
	private SolutionRelaRiskDAO o_solutionRelaRiskDAO;
	@Autowired
	private SolutionRelaOrgDAO o_solutionRelaOrgDAO;
	@Autowired
	private SolutionRelaFileDAO o_solutionRelaFileDAO;
	
	/**
	 * 查询控制措施关联的机构和人员
	 * 
	 * @author 张健
	 * @param measureId 控制措施ID
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public List<MeasureRelaOrg> findOrgAndEmpByMeasureId(String measureId){
		Criteria cra = o_measureRelaOrgDAO.createCriteria();
		if(StringUtils.isNotBlank(measureId)){
			cra.add(Restrictions.eq("controlMeasure.id", measureId));
		}
		return cra.list();
	}
	
	/**
	 * 保存应对措施
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	@Transactional
	@RecordLog("保存应对措施内容")
	public Map<String,Object> saveSolutionForm(SolutionForm solutionForm) throws UnsupportedEncodingException{
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Solution solution = null;
		if(StringUtils.isBlank(solutionForm.getId())){
			solution = new Solution();
			solution.setId(Identities.uuid());
			solution.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
			solution.setCreateBy(new SysEmployee(UserContext.getUser().getEmpid()));
			solution.setCreateTime(new Date());
			solution.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			solution.setCompleteIndicator(solutionForm.getCompleteIndicator());
			solution.setSolutionCode(solutionForm.getSolutionCode());
			solution.setCost(solutionForm.getCost());
			solution.setSolutionDesc(solutionForm.getDescHtml());
			solution.setExpectEndTime(solutionForm.getExpectEndTime());
			solution.setExpectStartTime(solutionForm.getExpectStartTime());
			solution.setIncome(solutionForm.getIncome());
			solution.setSolutionName(solutionForm.getSolutionName());
			solution.setType(solutionForm.getType());
			if("saved".equals(solutionForm.getArchiveStatus())){
			    solution.setArchiveStatus(Contents.RISK_STATUS_SAVED);//审核状态
			}else if("archived".equals(solutionForm.getArchiveStatus())){
			    solution.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);//审核状态
			}else if("examine".equals(solutionForm.getArchiveStatus())){
			    solution.setArchiveStatus(Contents.RISK_STATUS_EXAMINE);//审核状态
			}
			if(Contents.SOLUTION_PLAN.equals(solutionForm.getType())){
				solution.setRiskAssessPlan(new RiskAssessPlan(solutionForm.getRiskAssessPlanId()));
			}
			solution.setStatus(Contents.DEAL_STATUS_NOTSTART);
			if(StringUtils.isNotBlank(solutionForm.getStategy().getId())){
				solution.setStategy(solutionForm.getStategy());
			}else{
				solution.setStategy(null);
			}
			solution.setIsAddPresolution(null);//界面已去掉
			o_solutionDAO.merge(solution);
		}else{
			solution = this.findSolutionBySolutionId(solutionForm.getId());
			this.deleteOrgAndEmpFromSolutionBySolutionId(solutionForm.getId());
			this.deleteFilesFromSolutionBySolutionId(solutionForm.getId());
			solution.setCompleteIndicator(solutionForm.getCompleteIndicator());
			solution.setSolutionCode(solutionForm.getSolutionCode());
			solution.setCost(solutionForm.getCost());
			solution.setSolutionDesc(solutionForm.getDescHtml());
			solution.setExpectEndTime(solutionForm.getExpectEndTime());
			solution.setExpectStartTime(solutionForm.getExpectStartTime());
			solution.setIncome(solutionForm.getIncome());
			solution.setSolutionName(solutionForm.getSolutionName());
			if(StringUtils.isNotBlank(solutionForm.getStategy().getId())){
				solution.setStategy(solutionForm.getStategy());
			}else{
				solution.setStategy(null);
			}
			solution.setIsAddPresolution(solutionForm.getIsAddPresolution());
			o_solutionDAO.merge(solution);
		}
		List<String> paralist = new ArrayList<String>();
		paralist.add(solution.getId());
		this.deleteRisksFromSolutionBySolutionId(paralist);
		if(null != solutionForm.getRiskId()){
		    SolutionRelaRisk solutionRelaRisk = new SolutionRelaRisk();
		    solutionRelaRisk.setId(Identities.uuid());
		    solutionRelaRisk.setRisk(new Risk(solutionForm.getRiskId()));
		    solutionRelaRisk.setSolution(new Solution(solution.getId()));
		    o_solutionRelaRiskDAO.merge(solutionRelaRisk);
		}
		/*保存 部门和人员*/
		this.saveOrgAndEmpToSolution(solutionForm, solution.getId());
		//保存附件
		this.saveFilesToSolution(solutionForm, solution.getId());
		returnMap.put("success", true);
		return returnMap;
	}
	/**
	 *  根据id找实体
	 *  add by 宋佳
	 */
	private Solution findSolutionBySolutionId(String id){
		return o_solutionDAO.get(id);
	}
	
	/**
	 * 查询出所有应对措施关联的机构和人员
	 * 
	 * @author 张健
	 * @param solutionId 应对措施ID
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public List<SolutionRelaOrg> findOrgAndEmpFromSolutionById(String solutionId){
		Criteria cra = o_solutionRelaOrgDAO.createCriteria();
		cra.createAlias("solution", "solution");
		cra.createAlias("org", "org",CriteriaSpecification.LEFT_JOIN);
		cra.createAlias("emp", "emp",CriteriaSpecification.LEFT_JOIN);
		if(StringUtils.isNotBlank(solutionId)){
			cra.add(Restrictions.eq("solution.id", solutionId));
		}
		cra.add(Restrictions.or(Restrictions.eq("org.deleteStatus", Contents.DELETE_STATUS_USEFUL),Restrictions.eq("emp.deleteStatus", Contents.DELETE_STATUS_USEFUL)));
		cra.add(Restrictions.eq("solution.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return cra.list();
	}
	
	/**
	 * 删除应对关联的机构和人员
	 * 
	 * @author 张健
	 * @param solutionId 应对措施ID
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public int deleteOrgAndEmpFromSolutionBySolutionId(String solutionId){
		String hql = "delete from SolutionRelaOrg reOrg where reOrg.solution.id = :solutionId";
		Query hqlQuery = o_solutionRelaOrgDAO.createQuery(hql);
		hqlQuery.setParameter("solutionId", solutionId);
		return hqlQuery.executeUpdate();
	}
	
    /**
     * 批量删除应对关联的机构和人员
     * 
     * @author 张健
     * @param solutionId 应对措施ID
     * @return 
     * @date 2014-3-6
     * @since Ver 1.1
     */
	public int deleteOrgAndEmpFromSolutionBySolutionId(List<String> idList){
		String hql = "delete from SolutionRelaOrg reOrg where reOrg.solution.id in (:solutionIds)";
		Query hqlQuery = o_solutionRelaOrgDAO.createQuery(hql);
		hqlQuery.setParameterList("solutionIds", idList);
		return hqlQuery.executeUpdate();
	}
	/**
	 * 删除应对对应
	 * add by 宋佳
	 */
	@RecordLog("删除应对措施")
	public int deleteSolutionsBySolutionId(List<String> idList){
		String hql = "update Solution st set st.deleteStatus = :deleteStatus where st.id in (:solutionIds)";
		Query hqlQuery = o_solutionRelaOrgDAO.createQuery(hql);
		hqlQuery.setParameter("deleteStatus", Contents.DELETE_STATUS_DELETED);
		hqlQuery.setParameterList("solutionIds", idList);
		return hqlQuery.executeUpdate();
	}
	
	/**
	 * 保存应对对应的机构和人员
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public void saveOrgAndEmpToSolution(SolutionForm solutionForm,String solutionId){
		SolutionRelaOrg solutionRelaOrg = null;
		
		//增加机关 
		solutionRelaOrg=new SolutionRelaOrg();
		solutionRelaOrg.setId(Identities.uuid());
 		String orgid=IcmStandardUtils.findIdbyJason(solutionForm.getOrgId(), "deptid");//将Json转换为需要的字符串
 		if(StringUtils.isNotBlank(orgid)){
 		    solutionRelaOrg.setOrg(new SysOrganization(orgid));     		
 		    solutionRelaOrg.setEmp(null);
 		    solutionRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
 		    solutionRelaOrg.setSolution(new Solution(solutionId));
 		    o_solutionRelaOrgDAO.merge(solutionRelaOrg);
 		}
     	//增加人员 
     	solutionRelaOrg=new SolutionRelaOrg();
		solutionRelaOrg.setId(Identities.uuid());
 		String empid=IcmStandardUtils.findIdbyJason(solutionForm.getOrgId(), "empid");//将Json转换为需要的字符串
 		if(StringUtils.isNotBlank(empid)){
 		    solutionRelaOrg.setEmp(new SysEmployee(empid));     		
 		    solutionRelaOrg.setOrg(null);
 		    solutionRelaOrg.setType(Contents.EMP_RESPONSIBILITY);
 		    solutionRelaOrg.setSolution(new Solution(solutionId));
 		    o_solutionRelaOrgDAO.merge(solutionRelaOrg);
 		}
	}
	/**
	 * 加载应对措施的数据
	 * @throws IOException 
	 */
	public Map<String,Object> getSolutionFormBySolutionId(String solutionId) throws IOException{
		Map<String,Object> formMap = new HashMap<String,Object>();
		Solution solution = this.findSolutionBySolutionId(solutionId);
		formMap.put("id", solution.getId());
		formMap.put("solutionName", solution.getSolutionName());
		formMap.put("solutionCode", solution.getSolutionCode());
		formMap.put("completeIndicator",solution.getCompleteIndicator());
		formMap.put("cost",solution.getCost());
		formMap.put("solutionDesc",solution.getSolutionDesc());
		formMap.put("expectEndTime",solution.getExpectEndTime());
		formMap.put("expectStartTime",solution.getExpectStartTime());
		formMap.put("income",solution.getIncome());
		JSONArray orgJsonre = new JSONArray();
	    JSONObject orgObjectre = new JSONObject();
	    if(null != this.findRiskBySolutionId(solution.getId())){
	        orgObjectre.put("id", null == this.findRiskBySolutionId(solution.getId())?"":this.findRiskBySolutionId(solution.getId()).getId());
	        orgJsonre.add(orgObjectre);
	    }
	    formMap.put("riskId", orgJsonre.size() == 0?"":orgJsonre.toString());
	    formMap.put("riskSelect", orgJsonre.size() == 0?"":orgJsonre.toString());
		if(solution.getStategy() != null )
		{
			formMap.put("stategy",solution.getStategy().getId());
		}
		if(solution.getIsAddPresolution()!=null){
			formMap.put("isAddPresolution", solution.getIsAddPresolution().getId());
		}
		/*加载机构和人员*/
		List<SolutionRelaOrg> solutionRelaOrgList = this.findOrgAndEmpFromSolutionById(solutionId);
		if(null != solutionRelaOrgList && solutionRelaOrgList.size()>0){
		    JSONArray orgJson = new JSONArray();
	        JSONObject orgObject = new JSONObject();
			for(SolutionRelaOrg soRelaOrg : solutionRelaOrgList){
				if(Contents.ORG_RESPONSIBILITY.equals(soRelaOrg.getType())){//责任部门
				    orgObject.put("deptid", soRelaOrg.getOrg().getId());
				    orgObject.put("deptno", soRelaOrg.getOrg().getOrgcode());
				    orgObject.put("deptname", soRelaOrg.getOrg().getOrgname());
				}
				if(Contents.EMP_RESPONSIBILITY.equals(soRelaOrg.getType())){//责任人
				    orgObject.put("empid", soRelaOrg.getEmp().getId());
				    orgObject.put("empno", soRelaOrg.getEmp().getEmpcode());
				    orgObject.put("empname", soRelaOrg.getEmp().getEmpname());
				}
			}
			if(orgObject.get("empid") == null){
			    orgObject.put("empid", "");
			}
			orgJson.add(orgObject);
			formMap.put("orgId", null == orgObject.get("deptid")?"":orgJson.toString());
		}
		/*加载应对对应的文件*/
		List<SolutionRelaFile> solutionRelaFileList = this.findFilesFromSolution(solutionId);
		if(solutionRelaFileList.size()>0){			
			StringBuffer fileIds = new StringBuffer();
			for(SolutionRelaFile solutionRelaFile:solutionRelaFileList){
				String fileId=solutionRelaFile.getFile().getId();
				fileIds.append(",");
				fileIds.append(fileId);
			}
			formMap.put("fileId", fileIds.substring(1));
		}
		Map<String,Object> node = new HashMap<String,Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
	}
	
	/**
	 * 加载应对措施的数据
	 * @throws IOException 
	 */
	public Map<String,Object> getSolutionFormBySolutionIdForView(String solutionId) throws IOException{
		Map<String,Object> formMap = new HashMap<String,Object>();
		Solution solution = this.findSolutionBySolutionId(solutionId);
		formMap.put("solutionName", solution.getSolutionName());
		formMap.put("solutionCode", solution.getSolutionCode());
		formMap.put("completeIndicator",solution.getCompleteIndicator());
		formMap.put("cost",solution.getCost());
		formMap.put("expectEndTime",solution.getExpectEndTime());
		formMap.put("expectStartTime",solution.getExpectStartTime());
		formMap.put("income",solution.getIncome());
		formMap.put("solutionDesc", solution.getSolutionDesc());
		if(solution.getStategy() != null )
		{
			formMap.put("stategy",solution.getStategy().getName());
		}
		if(solution.getIsAddPresolution()!=null){
			formMap.put("isAddPresolution", solution.getIsAddPresolution().getName());
		}
		/*加载机构和人员*/
		List<SolutionRelaOrg> solutionRelaOrgList = this.findOrgAndEmpFromSolutionById(solutionId);
		if(null != solutionRelaOrgList && solutionRelaOrgList.size()>0){
		    StringBuffer org = new StringBuffer();
            for(SolutionRelaOrg soRelaOrg : solutionRelaOrgList){
                if(Contents.ORG_RESPONSIBILITY.equals(soRelaOrg.getType())){//责任部门
                    org.append(soRelaOrg.getOrg().getOrgname());
                }
                if(Contents.EMP_RESPONSIBILITY.equals(soRelaOrg.getType())){//责任人
                    org.append("(");
                    org.append(soRelaOrg.getEmp().getEmpname());
                    org.append(")");
                }
            }
            formMap.put("orgId", org);
		}
		/*加载应对对应的文件*/
		List<SolutionRelaFile> solutionRelaFileList = this.findFilesFromSolution(solutionId);
		if(solutionRelaFileList.size()>0){			
			List<Map<String,String>> flList = new ArrayList<Map<String,String>>();
			Map<String,String> fileMap = null;
			for(SolutionRelaFile solutionRelaFile:solutionRelaFileList){
				fileMap = new HashMap<String,String>();
				String fileId=solutionRelaFile.getFile().getId();
				String fileName = solutionRelaFile.getFile().getNewFileName();
				fileMap.put("fileId", fileId);
				fileMap.put("fileName", fileName);
				flList.add(fileMap);
			}
			JSONArray json = JSONArray.fromObject(flList); 
			formMap.put("fileId", json.toString());
		}
        if(null != this.findRiskBySolutionId(solution.getId())){
            formMap.put("riskId", this.findRiskBySolutionId(solution.getId()).getId());
        }else{
            formMap.put("riskId", "");
        }
		Map<String,Object> node = new HashMap<String,Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
	}
	/**
	 * 查询所有应对措施关联的文件
	 */
	public List<SolutionRelaFile> findFilesFromSolution(String solutionId){
		Criteria cra = o_solutionRelaFileDAO.createCriteria();
		cra.add(Restrictions.eq("solution.id", solutionId));
		return cra.list();
	}
	/**
	 * 保存solution下面所有的文件
	 */
	public void saveFilesToSolution(SolutionForm solutionForm,String solutionId){
		SolutionRelaFile solutionRelaFile = null;
		//增加文件
		if(StringUtils.isNotBlank(solutionForm.getFileId())){
			String[] fileIds = solutionForm.getFileId().split(",");
			String fileId ="";
			FileUploadEntity file = new FileUploadEntity();
			solutionRelaFile = new SolutionRelaFile();
			for(int i=0;i<fileIds.length;i++){
				String ids=Identities.uuid();
				solutionRelaFile.setId(ids);
				fileId=fileIds[i];
				file.setId(fileId);
				solutionRelaFile.setFile(file);
				solutionRelaFile.setSolution(new Solution(solutionId));
				o_solutionRelaFileDAO.merge(solutionRelaFile);
			}
		}
	}
	/**
	 * 删除所有文件
	 * add by 宋佳
	 * 
	 */
	public int deleteFilesFromSolutionBySolutionId(String solutionId){
		String hql = "delete from SolutionRelaFile reOrg where reOrg.solution.id = :solutionId";
		Query hqlQuery = o_solutionRelaFileDAO.createQuery(hql);
		hqlQuery.setParameter("solutionId", solutionId);
		return hqlQuery.executeUpdate();
	}
	/**
	 * 删除应对关联所有文件
	 * add by 宋佳
	 * 
	 */
	public int deleteFilesFromSolutionBySolutionId(List<String> idList){
		String hql = "delete from SolutionRelaFile reOrg where reOrg.solution.id in (:solutionIds)";
		Query hqlQuery = o_solutionRelaFileDAO.createQuery(hql);
		hqlQuery.setParameterList("solutionIds", idList);
		return hqlQuery.executeUpdate();
	}
	
	public int deleteRisksFromSolutionBySolutionId(List<String> idList){
		String hql = "delete from SolutionRelaRisk reRisk where reRisk.solution.id in (:solutionIds)";
		Query hqlQuery = o_solutionRelaFileDAO.createQuery(hql);
		hqlQuery.setParameterList("solutionIds", idList);
		return hqlQuery.executeUpdate();
	}
	/**
	 * 根据solutionId找到risk
	 */
	public Risk findRiskBySolutionId(String solutionId){
		String hqlSql = "select risk from SolutionRelaRisk reRisk where reRisk.solution.id = :solutionId";
		Query query = o_solutionRelaRiskDAO.createQuery(hqlSql);
		query.setString("solutionId", solutionId);
		if(query.list().size() == 0){
		    return null;
		}else{
		    return (Risk) query.list().get(0);
		}
	}
	/**
	 * 更改应对措施的状态
	 * add by 宋佳
	 * 2013-10-11
	 */
	@Transactional
	public int updateStatusFromSolutionById(String solutionId,String status){
		String hqlSql = "update Solution st set st.status=:status where st.id = :solutionId";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("status", status);
		paramMap.put("solutionId", solutionId);
		int upCount = o_solutionDAO.createQuery(hqlSql, paramMap).executeUpdate();
		return upCount;
	}
	
	/**
     * 更改应对措施状态
     * add by 宋佳
     * 2013-10-11
     */
    @Transactional
    public int updateSolutionArchiveStatusById(String businessId,String archiveStatus){
        String sql = " update t_rm_solution set ARCHIVE_STATUS = :archiveStatus where RESPONSE_PLAN_ID = :responsePlanId ";
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("archiveStatus", archiveStatus);
        paramMap.put("responsePlanId", businessId);
        int upCount = o_solutionDAO.createSQLQuery(sql, paramMap).executeUpdate();
        return upCount;
    }
	
	/**
	 *  根据定时任务查找风险列表
	 */
	public List<Solution> findSolutionsByAutoWorkFlowTime(){
		Criteria cra = o_solutionDAO.createCriteria();
		cra.add(Restrictions.le("expectStartTime", new Date()));
		cra.add(Restrictions.eq("status", Contents.DEAL_STATUS_NOTSTART));
		cra.add(Restrictions.eq("archiveStatus", Contents.RISK_STATUS_ARCHIVED));
		return cra.list();
	}
	
	/**
	 * 根据风险id插入风险id所对应的的应对信息
	 * @param oldRiskMap
	 * @param companyId
	 * @return
	 */
	@Transactional
	public boolean saveSolutionByRiskId(Map<String,Risk> oldRiskMap,String companyId){
		for(Map.Entry<String, Risk> entry : oldRiskMap.entrySet()){
			String oldRiskId = entry.getKey();
			Risk risk  = entry.getValue();
			Solution solution = null;
			Solution oldSolution = null;
			Criteria c = o_solutionRelaRiskDAO.createCriteria();
			c.createAlias("risk", "risk");
			c.createAlias("solution", "solution");
			c.add(Restrictions.eq("risk.id", oldRiskId));
			c.add(Restrictions.eq("solution.deleteStatus", Contents.DELETE_STATUS_USEFUL));
			List<SolutionRelaRisk> solutionRelaRiskList = c.list();
			for(SolutionRelaRisk solutionRelaRisk : solutionRelaRiskList){
				oldSolution = solutionRelaRisk.getSolution();
				solution = new Solution();
				solution.setId(Identities.uuid());
				solution.setCompany(new SysOrganization(companyId));
				solution.setSolutionCode(oldSolution.getSolutionCode());
				solution.setCreateBy(oldSolution.getCreateBy());
				solution.setCreateTime(oldSolution.getCreateTime());
				solution.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
				solution.setSolutionDesc(oldSolution.getSolutionDesc());
				solution.setLastModifyBy(oldSolution.getLastModifyBy());
	//			measure.setMeasureRelaRisks(measureRelaRisks)
				solution.setSolutionName(oldSolution.getSolutionName());
				o_solutionDAO.merge(solution);
				//保存完新的控制措施保存风险控制措施关联关系
				saveSolutionRelaRiskByNewId(risk.getId(),solution.getId());
			}
		}
		return true;
	}
	/**
	 * 保存控制措施和风险的关联关系
	 * add by songjia 
	 * 
	 */
	@Transactional
	private boolean saveSolutionRelaRiskByNewId(String riskId,String measureId){
		SolutionRelaRisk solutionRelaRisk = new SolutionRelaRisk();
		solutionRelaRisk.setId(Identities.uuid());
		solutionRelaRisk.setRisk(new Risk(riskId));
		solutionRelaRisk.setSolution(new Solution(measureId));
		o_solutionRelaRiskDAO.merge(solutionRelaRisk);
		return true;
	}
	
	/**
	 * 按分类查询所有审批状态的数量
	 * 
	 * @author 张健
	 * @param 
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public Map<String, Object> getTreeResponseCount(){
        Map<String, Object> map = new HashMap<String, Object>();
        String scountSql = " select count(1) cc from t_rm_solution where DELETE_STATUS = '1' ";
        String mcountSql = " select count(1) cc from T_CON_CONTROL_MEASURE where DELETE_ESTATUS = '1' ";
        
        String sgroupSql = " select ARCHIVE_STATUS,count(1) from t_rm_solution where DELETE_STATUS = '1' group by ARCHIVE_STATUS ";
        String mgroupSql = " select ARCHIVE_STATUS,count(1) from T_CON_CONTROL_MEASURE where DELETE_ESTATUS = '1' group by ARCHIVE_STATUS ";
        List<Object[]> scountList = o_solutionDAO.createSQLQuery(scountSql).list();
        List<Object[]> mcountList = o_solutionDAO.createSQLQuery(mcountSql).list();
        List<Object[]> sgroupList = o_solutionDAO.createSQLQuery(sgroupSql).list();
        List<Object[]> mgroupList = o_solutionDAO.createSQLQuery(mgroupSql).list();
        
        Map<String, Object> sgroupMap = new HashMap<String, Object>();
        Map<String, Object> mgroupMap = new HashMap<String, Object>();
        Map<String, Object> othersgroupMap = new HashMap<String, Object>();
        Map<String, Object> othermgroupMap = new HashMap<String, Object>();
        for(Object[] obs : sgroupList){
            if(obs[0] != null && !"null".equals(obs[0].toString())){
                sgroupMap.put(obs[0].toString(), obs[1].toString());
            }else{
                othersgroupMap.put("other", obs[1].toString());
            }
        }
        for(Object[] obs : mgroupList){
            if(obs[0] != null && !"null".equals(obs[0].toString())){
                mgroupMap.put(obs[0].toString(), obs[1].toString());
            }else{
                othermgroupMap.put("other", obs[1].toString());
            }
        }
        
        map.put("scount", scountList.get(0));//总数
        map.put("mcount", mcountList.get(0));//总数
        map.put("sgroup", sgroupMap);//分组数量
        map.put("mgroup", mgroupMap);//分组数量
        map.put("othersgroupMap", othersgroupMap);//分组数量
        map.put("othermgroupMap", othermgroupMap);//分组数量
        return map;
    }
}
