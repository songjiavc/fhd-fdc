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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.assess.MeasureDAO;
import com.fhd.dao.icm.assess.MeasureRelaOrgDAO;
import com.fhd.dao.icm.assess.MeasureRelaRiskDAO;
import com.fhd.dao.process.ProcessPointRelaMeasureDAO;
import com.fhd.dao.process.ProcessRelaMeasureDAO;
import com.fhd.dao.sys.dic.DictEntryDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.icm.control.MeasureRelaOrg;
import com.fhd.entity.icm.control.MeasureRelaRisk;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessPointRelaMeasure;
import com.fhd.entity.process.ProcessRelaMeasure;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.control.RiskRelaMeasureBO;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.process.ProcessRiskBO;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.icm.web.form.process.MeasureForm;
import com.fhd.ra.interfaces.response.IMeasureOutSiteBO;


/**
 * @author 宋佳
 * @version 尝试性基类
 * @since Ver 1.1
 * @Date 2013-1-6 下午7:28:25
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class ResponseMeasureBO implements IMeasureOutSiteBO{
	@Autowired
	private MeasureDAO o_measureDAO;
	@Autowired
	private MeasureRelaRiskDAO o_measureRelaRiskDAO;
	@Autowired
	private MeasureRelaOrgDAO o_measureRelaOrgDAO;
	@Autowired
	private ProcessPointRelaMeasureDAO o_processPointRelaMeasureDAO;
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private DictEntryDAO o_dictEntryDAO;
	@Autowired
	private ProcessRiskBO o_processRiskBO;
	@Autowired
	private ProcessRelaMeasureDAO o_processRelaMeasureDAO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private RiskRelaMeasureBO o_riskRelaMeasureBO;
	
	/**
	 *  根据id找实体
	 *  add by 宋佳
	 */
	private Measure findMeasureByMeausreId(String id){
		return o_measureDAO.get(id);
	}
	/**
	 * 删除应对关联的机构和人员
	 * add by 宋佳
	 */
	public int removeOrgAndEmpFromMeasureByMeasureId(String measureId){
		String hql = "delete from MeasureRelaOrg reOrg where reOrg.controlMeasure.id = :measureId";
		Query hqlQuery = o_measureRelaOrgDAO.createQuery(hql);
		hqlQuery.setParameter("measureId", measureId);
		return hqlQuery.executeUpdate();
	}
	/**
	 * 删除应对关联的机构和人员
	 * add by 宋佳
	 */
	public int removeOrgAndEmpFromMeasureByMeasureId(List<String> idList){
		String hql = "delete from MeasureRelaOrg reOrg where reOrg.controlMeasure.id in (:measureIds)";
		Query hqlQuery = o_measureRelaOrgDAO.createQuery(hql);
		hqlQuery.setParameterList("measureIds", idList);
		return hqlQuery.executeUpdate();
	}
	/**
	 * 删除应对对应
	 * add by 宋佳
	 */
	@RecordLog("删除控制措施")
	public int removeMeasuresByMeasureId(List<String> idList){
		String hql = "update Measure st set st.deleteStatus = :deleteStatus where st.id in (:measureIds)";
		Query hqlQuery = o_measureDAO.createQuery(hql);
		hqlQuery.setParameter("deleteStatus", Contents.DELETE_STATUS_USEFUL);
		hqlQuery.setParameterList("measureIds", idList);
		return hqlQuery.executeUpdate();
	}
	
	
	
	/**
	 * 删除所有文件
	 * add by 宋佳
	 * 
	 */
	public int removeRisksFromMeasureByMeasureId(List<String> idList){
		String hql = "delete from MeasureRelaRisk reRisk where reRisk.controlMeasure.id in (:measureIds)";
		Query hqlQuery = o_measureRelaRiskDAO.createQuery(hql);
		hqlQuery.setParameterList("measureIds", idList);
		return hqlQuery.executeUpdate();
	}
	/**
	 *  add  by 宋佳
	 *  2013-9-16
	 *  保存控制措施form
	 * @throws CloneNotSupportedException 
	 */
	@Transactional
	@RecordLog("保存控制措施")
	public Boolean saveMeasureForm(MeasureForm measureForm){
		boolean flag = true;
		Measure measure = null;
		MeasureRelaRisk measureRelaRisk = null;
		JSONArray msForm=JSONArray.fromObject(measureForm.getProcessAndPoint());
		JSONObject jsonObject=msForm.getJSONObject(0);
		String processId = jsonObject.getString("processId");
		String pointId = jsonObject.getString("pointId");
		if(StringUtils.isBlank(measureForm.getId())){
			measure = new Measure();	
			measure.setId(Identities.uuid());
			measure.setCode(measureForm.getMeacode());
			measure.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
			if(StringUtils.isNotBlank(measureForm.getControlFrequency().getId())){
				measure.setControlFrequency(measureForm.getControlFrequency());
			}
			if(StringUtils.isNotBlank(measureForm.getControlMeasure().getId())){
				measure.setControlMeasure(measureForm.getControlMeasure());
			}
			measure.setControlPoint(measureForm.getControlPoint());
			measure.setControlTarget(measureForm.getControlTarget());
			measure.setCreateBy(new SysEmployee(UserContext.getUser().getEmpid()));
			measure.setCreateTime(new Date());
			measure.setName(measureForm.getMeadesc());
			measure.setImplementProof(measureForm.getImplementProof());
			measure.setIsKeyControlPoint(measureForm.getIsKeyControlPoint());
			measure.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			if("saved".equals(measureForm.getArchiveStatus())){
			    measure.setArchiveStatus(Contents.RISK_STATUS_SAVED);
			}else if("archived".equals(measureForm.getArchiveStatus())){
			    measure.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
			}
			o_measureDAO.merge(measure);
		}else{
			//删除控制和部门的关系
			measure = this.findMeasureByMeausreId(measureForm.getId());
			if(StringUtils.isBlank(pointId)){
				Criteria processelameasurectr = o_processRelaMeasureDAO.createCriteria();
				processelameasurectr.createAlias("controlMeasure", "controlMeasure");
				processelameasurectr.add(Restrictions.eq("controlMeasure.id", measureForm.getId()));
				List<ProcessRelaMeasure> processrelameasureList = processelameasurectr.list();
				for(ProcessRelaMeasure processrelameasure : processrelameasureList){
					o_processRelaMeasureDAO.delete(processrelameasure);
				}
			}else{
				//删除控制对应的流程节点
				Criteria pointelameasurectr = o_processPointRelaMeasureDAO.createCriteria();
				pointelameasurectr.createAlias("controlMeasure", "controlMeasure");
				pointelameasurectr.add(Restrictions.eq("controlMeasure.id", measureForm.getId()));
				List<ProcessPointRelaMeasure> processpointrelameasureList = pointelameasurectr.list();
				for(ProcessPointRelaMeasure processpointrelameasure : processpointrelameasureList){
					o_processPointRelaMeasureDAO.delete(processpointrelameasure);
				}
			}
			List<String> midList = new ArrayList<String>();
			midList.add(measure.getId());
			this.removeRisksFromMeasureByMeasureId(midList);
			Criteria measureRelaOrgCtr = o_measureRelaOrgDAO.createCriteria();
			measureRelaOrgCtr.createAlias("controlMeasure", "controlMeasure");
			measureRelaOrgCtr.add(Restrictions.eq("controlMeasure.id", measureForm.getId()));
			List<MeasureRelaOrg> measurerelaorgList = measureRelaOrgCtr.list();
			for(MeasureRelaOrg measurerelaorg : measurerelaorgList){
				o_measureRelaOrgDAO.delete(measurerelaorg);
			}
			measure.setCode(measureForm.getMeacode());
			measure.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
			if(StringUtils.isNotBlank(measureForm.getControlFrequency().getId())){
				measure.setControlFrequency(measureForm.getControlFrequency());
			}
			if(StringUtils.isNotBlank(measureForm.getControlMeasure().getId())){
				measure.setControlMeasure(measureForm.getControlMeasure());
			}
			measure.setControlPoint(measureForm.getControlPoint());
			measure.setControlTarget(measureForm.getControlTarget());
			measure.setLastModifyBy(new SysEmployee(UserContext.getUser().getEmpid()));
			measure.setLastModifyTime(new Date());
			measure.setName(measureForm.getMeadesc());
			measure.setImplementProof(measureForm.getImplementProof());
			measure.setIsKeyControlPoint(measureForm.getIsKeyControlPoint());
			measure.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			o_measureDAO.merge(measure);
		}
		MeasureRelaOrg measureRelaOrg = new MeasureRelaOrg();
		if(!StringUtils.isEmpty(measureForm.getMeaSureorgId())){
			measureRelaOrg.setId(Identities.uuid());
			measureRelaOrg.setControlMeasure(measure);
			String orgid=IcmStandardUtils.findIdbyJason(measureForm.getMeaSureorgId(), "id");
			measureRelaOrg.setOrg(o_sysOrgDAO.get(orgid));
			measureRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
			o_measureRelaOrgDAO.merge(measureRelaOrg);
		}
		if(!StringUtils.isEmpty(measureForm.getMeaSureempId())){
			measureRelaOrg = new MeasureRelaOrg();
			measureRelaOrg.setControlMeasure(measure);
			measureRelaOrg.setId(Identities.uuid());
			String empid=IcmStandardUtils.findIdbyJason(measureForm.getMeaSureempId(), "id");
			measureRelaOrg.setEmp(o_sysEmployeeDAO.get(empid));
			measureRelaOrg.setType(Contents.EMP_RESPONSIBILITY);
			o_measureRelaOrgDAO.merge(measureRelaOrg);
		}
		if(StringUtils.isNotBlank(pointId)){
			
			ProcessPointRelaMeasure processPointRelaMeasure = new ProcessPointRelaMeasure();
			processPointRelaMeasure.setId(Identities.uuid());
			processPointRelaMeasure.setProcessPoint(new ProcessPoint(pointId));
			processPointRelaMeasure.setProcess(new Process(processId));
			processPointRelaMeasure.setControlMeasure(measure);
			o_processPointRelaMeasureDAO.merge(processPointRelaMeasure);
		}else{
			ProcessRelaMeasure processRelaMeasure = new ProcessRelaMeasure();
			processRelaMeasure.setId(Identities.uuid());
			processRelaMeasure.setProcess(new Process(processId));
			processRelaMeasure.setControlMeasure(measure);
			o_processRelaMeasureDAO.merge(processRelaMeasure);
		}
		if(StringUtils.isNotBlank(measureForm.getRiskId())){
		    //保存风险和控制关系信息
		    measureRelaRisk = new MeasureRelaRisk();
		    measureRelaRisk.setId(Identities.uuid());
		    measureRelaRisk.setControlMeasure(measure);
		    measureRelaRisk.setRisk(new Risk(measureForm.getRiskId()));
		    o_measureRelaRiskDAO.merge(measureRelaRisk);
		}
		//保存控制评价点
		o_processRiskBO.saveAssessPointEditGrid("",measure.getId(),measureForm.getEditGridJson(),"E");
		return flag;
	}
	/**
	 * <pre>
	 *   加载应对中的控制措施
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> loadMeasureEditItemFormData(String measureId){
		Criteria criteria = o_measureDAO.createCriteria();
		criteria.add(Restrictions.eq("id", measureId));
		Criteria orgctr = o_measureRelaOrgDAO.createCriteria();
		orgctr.createAlias("controlMeasure", "controlMeasure").add(Restrictions.eq("controlMeasure.id",measureId));
		Measure measure=(Measure) criteria.uniqueResult();
		Map<String, Object> formMap = new HashMap<String, Object>();
		formMap.put("meacode", measure.getCode());
		if(measure.getControlFrequency()!=null){
			formMap.put("controlFrequency", measure.getControlFrequency().getId());
		}
		formMap.put("isKeyControlPoint", measure.getIsKeyControlPoint());
		formMap.put("implementProof", measure.getImplementProof());
		formMap.put("controlPoint", measure.getControlPoint());
		formMap.put("meadesc", measure.getName());
		if(null != measure.getControlMeasure()){
			formMap.put("controlMeasure", measure.getControlMeasure().getId());
		}
		if(null != measure.getControlTarget()){
			formMap.put("controlTarget", measure.getControlTarget());
		}
//		formMap.put("relaSubject", measure.getRelaSubject());
		//formMap.put("pointNote", measure.getp());
		
		//获取责任和部门
		List<MeasureRelaOrg> orglist=orgctr.list();
		for(MeasureRelaOrg measurerelaorg : orglist){
			if(Contents.ORG_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				formMap.put("meaSureorgId","[{\"id\":\""+measurerelaorg.getOrg().getId()+"\",\"deptno\":\"" + measurerelaorg.getOrg().getOrgcode()+ "\",\"deptname\":\""+ measurerelaorg.getOrg().getOrgname() +"\"}]");
			}else if(Contents.EMP_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				formMap.put("meaSureempId","[{\"id\":\""+measurerelaorg.getEmp().getId()+"\",\"empno\":\"" + measurerelaorg.getEmp().getEmpcode()+ "\",\"empname\":\""+ measurerelaorg.getEmp().getEmpname() +"\"}]");
			}else{
			}
		}
		//获取控制措施对应节点信息
		Criteria measureRelaPointCtr = o_processPointRelaMeasureDAO.createCriteria();
		measureRelaPointCtr.createAlias("controlMeasure", "controlMeasure");
		measureRelaPointCtr.add(Restrictions.eq("controlMeasure.id", measureId));
		List<ProcessPointRelaMeasure> measureRelaPointList = measureRelaPointCtr.list();
		JSONArray ja = new JSONArray();
		JSONObject jb = null;
		if(measureRelaPointList != null && measureRelaPointList.size()>0){
			for(ProcessPointRelaMeasure pointrelameasure : measureRelaPointList){
				jb = new JSONObject();
				jb.put("pointId", pointrelameasure.getProcessPoint().getId());
				jb.put("processId", pointrelameasure.getProcess().getId());
				ja.add(jb);
			}
		}else{
			List<ProcessRelaMeasure> processRelaMeasureList = o_processBO.findProcessFromMeasure(measureId);
			for(ProcessRelaMeasure processrelameasure : processRelaMeasureList){
				jb = new JSONObject();
				jb.put("pointId", "");
				jb.put("processId", processrelameasure.getProcess().getId());
				ja.add(jb);
			}
		}
		formMap.put("processPoint",ja);
		List<MeasureRelaRisk> measureRelaRiskList = o_riskRelaMeasureBO.findRiskbyMeasureId(measureId);
        if(measureRelaRiskList.size() == 0){
            formMap.put("riskId","");
        }else{
            JSONArray riskJson = new JSONArray();
            JSONObject riskObject = new JSONObject();
            if(null != measureRelaRiskList.get(0).getRisk()){
                riskObject.put("id", measureRelaRiskList.get(0).getRisk().getId());
                riskJson.add(riskObject);
            }
            formMap.put("riskId",riskJson.size() == 0 ?"":riskJson.toString());
        }
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
	}
	/**
	 * <pre>
	 *   加载应对中的控制措施
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> loadMeasureEditItemFormDataForView(String measureId){
		Criteria orgctr = o_measureRelaOrgDAO.createCriteria();
		orgctr.createAlias("controlMeasure", "controlMeasure").add(Restrictions.eq("controlMeasure.id",measureId));
		//获取控制措施对应节点信息
		Criteria measureRelaPointCtr = o_processPointRelaMeasureDAO.createCriteria();
		measureRelaPointCtr.createAlias("controlMeasure", "controlMeasure");
		measureRelaPointCtr.add(Restrictions.eq("controlMeasure.id", measureId));
		Measure measure=(Measure) this.findMeasureByMeausreId(measureId);
		Map<String, Object> formMap = new HashMap<String, Object>();
		//formMap.put("code", process.getCode());
		formMap.put("measurecode", measure.getCode());
//		formMap.put("meaSureDesc", measure.getDesc());
		if(measure.getControlFrequency()!=null){
			formMap.put("controlFrequency", measure.getControlFrequency().getName());
		}
		if(StringUtils.isNotBlank(measure.getIsKeyControlPoint())){
			formMap.put("isKeyControlPoint", o_dictEntryDAO.get(measure.getIsKeyControlPoint()).getName());
		}
		if(measure.getImplementProof() != null){
			formMap.put("implementProof", measure.getImplementProof());
		}
		formMap.put("controlPoint", measure.getControlPoint());
		formMap.put("meaSureDesc", measure.getName());
		if(measure.getControlMeasure() != null){
			formMap.put("controlMeasure", measure.getControlMeasure().getName());
		}
		formMap.put("controlTarget", measure.getControlTarget());
		//获取责任和部门
		List<MeasureRelaOrg> orglist=orgctr.list();
		for(MeasureRelaOrg measurerelaorg : orglist){
			if(Contents.ORG_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				formMap.put("orgName",measurerelaorg.getOrg().getOrgname());
			}else if(Contents.EMP_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				formMap.put("empName",measurerelaorg.getEmp().getEmpname());
			}else{
			}
		}
		List<ProcessPointRelaMeasure> measureRelaPointList = measureRelaPointCtr.list();
		String sb = "";
		if(measureRelaPointList != null && measureRelaPointList.size()>0){
			for(ProcessPointRelaMeasure pointrelameasure : measureRelaPointList){
				sb =  pointrelameasure.getProcessPoint().getName();
				formMap.put("processPoint","流程:"+ pointrelameasure.getProcess().getName() +";" + "节点:" + sb);
			}
		}else{
			List<ProcessRelaMeasure> processRelaMeasureList = o_processBO.findProcessFromMeasure(measureId);
			for(ProcessRelaMeasure processrelameasure : processRelaMeasureList){
				formMap.put("processPoint","流程:"+ processrelameasure.getProcess().getName() +";" + "节点:" + "无");
			}
		}
		List<MeasureRelaRisk> measureRelaRiskList = o_riskRelaMeasureBO.findRiskbyMeasureId(measureId);
		if(measureRelaRiskList.size() == 0){
		    formMap.put("riskId","");
		}else{
		    formMap.put("riskId",null == measureRelaRiskList.get(0).getRisk()?"":measureRelaRiskList.get(0).getRisk().getId());
		}
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
	}
}
