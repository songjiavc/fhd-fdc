
package com.fhd.ra.web.controller.risk.relate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.Identities;
import com.fhd.entity.assess.formulatePlan.RiskOrgRelationTemp;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.assess.quaAssess.EditIdea;
import com.fhd.entity.assess.quaAssess.ResponseIdea;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.response.RiskResponse;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.RiskRelaRisk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.LastLeaderBO;
import com.fhd.ra.business.assess.formulateplan.RiskResponseBO;
import com.fhd.ra.business.assess.kpiset.AssessTaskBO;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.quaassess.SaveAssessBO;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.business.riskidentify.RiskIdentifyBO;
import com.fhd.ra.web.form.risk.RiskForm;
import com.fhd.sys.business.dic.DictBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 风险定义
 * 
 * @author 张健
 * @date 2013-8-20
 * @since Ver 1.1
 */
@Controller
public class RelateRiskControl {
	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
    @Autowired
    private RiskCmpBO o_riskCmpBO;
	@Autowired
	private AssessTaskBO o_assessTaskBO;
    @Autowired
    private DictBO o_dicBO;
    @Autowired
	private RiskIdentifyBO o_riskIdentifyBO;
    @Autowired
	private SaveAssessBO o_saveAssessBO;
    @Autowired
    private LastLeaderBO lastLeaderBO;
    @Autowired
    private AdjustHistoryResultBO o_adjustHistoryResultBO;
    
    @Autowired
    private ScoreObjectBO o_scoreObjectBO;
    
    @Autowired
    private RiskResponseBO o_riskResponseBO;
    /**
     * 保存风险 state=2,专用于风险评估模块风险的添加
     * 
     * @author zj
     * @param riskForm
     * @param id
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/risk/relate/saveRiskInfo")
    public Map<String, Object> saveRiskInfo(
    		RiskForm riskForm,
    		String id,
    		String state,
    		String archiveStatus,
    		String schm,
    		String parentId,
    		String assessPlanId,
    		String executionId,
    		String _type
    		) {
    	//判断添加风险是否为流程中添加还是风险库中添加
    	if(assessPlanId != null){
	        Map<String, Object> map = new HashMap<String, Object>();
	        Risk risk = new Risk();
	        if (state == null || state.equals("")) {
	            risk.setDeleteStatus("1");
	        } else {
	            risk.setDeleteStatus(state); // 将2的状态保存起来
	        }
	        //归档状态
	  		if(archiveStatus==null || archiveStatus.equals("")){
	  			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
	  		}else{
	  			risk.setArchiveStatus(archiveStatus);
	  		}
	        // 添加
	        String makeId = Identities.uuid();
	        risk.setId(makeId);
	        String companyId = UserContext.getUser().getCompanyid();
	        SysOrganization company = new SysOrganization();
	        company.setId(companyId);
	        risk.setCompany(company);
	        risk.setSchm(schm); //  风险分库标识
	        // 上级风险
	        String parentIdStr = riskForm.getParentId();
	        if (parentIdStr != null && !parentIdStr.equals("")) {
	            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
	            String _parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
	            Risk parent = o_riskCmpBO.findRiskById(_parentId);
	            if (null != parent) {
	                risk.setParent(parent);
	                risk.setParentName(parent.getName());
	                risk.setIdSeq(parent.getIdSeq() + makeId + ".");
	                risk.setLevel(parent.getLevel()+1);
	            }
	        } else {
	            risk.setParent(null);
	            risk.setParentName("");
	            risk.setIdSeq("." + makeId + ".");
	            risk.setLevel(1);
	        }
	
	        risk.setIsLeaf(true);
	        risk.setIsRiskClass(riskForm.getIsRiskClass());
	        // 编码，名称，描述
	        risk.setCode(riskForm.getCode());
	        risk.setName(riskForm.getName());
	        risk.setDesc(riskForm.getDesc());
	        //add by songjia 添加
	        
	        //序号
	        if(riskForm.getSort() == null){	//未指定，自动排序
	        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
			}else{
				risk.setSort(riskForm.getSort());
			}
	        // 是否启用
	        risk.setIsUse("0yn_y");
	        // 评估模板
	        if(risk.getParent()!=null){
	        	risk.setIsInherit("0yn_y");
	        	risk.setTemplate(risk.getParent().getTemplate());
	        }
	
	        // 风险动因和风险影响
	//        String riskReason = riskForm.getRiskReason();
	//        String riskInfluence = riskForm.getRiskInfluence();
	
	        // 责任部门，相关部门，风险指标，控制流程，影响指标
	        String respDeptName = riskForm.getRespDeptName();
	        String relaDeptName = riskForm.getRelaDeptName();
	        /*
		        String riskKpiName = riskForm.getRiskKpiName();
		        String controlProcessureName = riskForm.getControlProcessureName();
		        String influKpiName = riskForm.getInfluKpiName();
		        String influProcessureName = riskForm.getInfluProcessureName();
	        */
	      //添加责任部门/人和相关部门/人
	  		JSONObject object = new JSONObject();
	  		//主责部门List
	  		List<RiskOrgTemp> ListZhuzeOrg = new ArrayList<RiskOrgTemp>();
	  		if(null!=respDeptName && !"".equals(respDeptName)){
	  			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
	  			for(int i=0;i<respDeptArray.size();i++){
	  				RiskOrgTemp riskOrgZhuze  = new RiskOrgTemp();
	  				object = (JSONObject)respDeptArray.get(i);
	  				riskOrgZhuze.setId(UUID.randomUUID().toString());
	  				riskOrgZhuze.setType("M");
	  				//保存责任部门
	  				String deptidStr = object.get("deptid").toString();
	  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
	  					SysOrganization org = new SysOrganization();
	  					org.setId(deptidStr);
	  					riskOrgZhuze.setSysOrganization(org);
	  				}
	  				//保存责任人
	  				String empidStr = object.get("empid").toString();
	  				if(!empidStr.equals("") && !empidStr.equals("null")){
	  					SysEmployee emp = new SysEmployee();
	  					emp.setId(empidStr);
	  					riskOrgZhuze.setEmp(emp);
	  				}
	  				ListZhuzeOrg.add(riskOrgZhuze);
	  			}
	  		}
	  		//相关部门list
	  		List<RiskOrgTemp> ListRelaOrg = new ArrayList<RiskOrgTemp>();
	  		if(null!=relaDeptName && !"".equals(relaDeptName)){
	  			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
	  			for(int i=0;i<relaDeptArray.size();i++){
	  				object = (JSONObject)relaDeptArray.get(i);
	  				RiskOrgTemp riskOrgRela = new RiskOrgTemp();
	  				riskOrgRela.setId(UUID.randomUUID().toString());
	  				riskOrgRela.setType("A");
	  				//保存相关部门
	  				String deptidStr = object.get("deptid").toString();
	  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
	  					SysOrganization org = new SysOrganization();
	  					org.setId(deptidStr);
	  					riskOrgRela.setSysOrganization(org);
	  				}
	  				//保存相关人
	  				String empidStr = object.get("empid").toString();
	  				if(!empidStr.equals("") && !empidStr.equals("null")){
	  					SysEmployee emp = new SysEmployee();
	  					emp.setId(empidStr);
	  					riskOrgRela.setEmp(emp);
	  				}
	  				ListRelaOrg.add(riskOrgRela);
	  			}
	  		}
	        // 保存
	        o_riskIdentifyBO.saveTaskBySome(parentId, risk, assessPlanId, executionId, _type,ListZhuzeOrg,ListRelaOrg);
	        map.put("success", true);
	        map.put("id", risk.getId());
	        map.put("name", risk.getName());
	        return map;
    	}else{
    		return saveRiskInfo(riskForm,id,state,archiveStatus,schm);
    	}
    }
    
    
    
   
    /**
     * @desc    保存风险的基本
     * @param riskForm
     * @param id
     * @param state
     * @param archiveStatus
     * @param schm
     * @return
     */
    public Map<String, Object> saveRiskInfo(RiskForm riskForm, String id, String state,String archiveStatus,String schm) {
        Map<String, Object> map = new HashMap<String, Object>();

        Risk risk = new Risk();
        if (state == null || state.equals("")) {
            risk.setDeleteStatus("1");
        } else {
            risk.setDeleteStatus(state); // 将2的状态保存起来
        }
        //归档状态
  		if(archiveStatus==null || archiveStatus.equals("")){
  			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
  		}else{
  			risk.setArchiveStatus(archiveStatus);
  		}
        // 添加
        String makeId = Identities.uuid();
        risk.setId(makeId);
        String companyId = UserContext.getUser().getCompanyid();
        SysOrganization company = new SysOrganization();
        company.setId(companyId);
        risk.setCompany(company);
        risk.setSchm(schm); //  风险分库标识
        // 上级风险
        String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && !parentIdStr.equals("")) {
            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
            String parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
            Risk parent = o_riskCmpBO.findRiskById(parentId);
            if (null != parent) {
                risk.setParent(parent);
                risk.setParentName(parent.getName());
                risk.setIdSeq(parent.getIdSeq() + makeId + ".");
                risk.setLevel(parent.getLevel()+1);
            }
        } else {
            risk.setParent(null);
            risk.setParentName("");
            risk.setIdSeq("." + makeId + ".");
            risk.setLevel(1);
        }
        risk.setIsLeaf(true);
        risk.setIsRiskClass(riskForm.getIsRiskClass());
        // 编码，名称，描述
        risk.setCode(riskForm.getCode());
        risk.setName(riskForm.getName());
        risk.setDesc(riskForm.getDesc());

        //序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
        // 是否启用
        risk.setIsUse("0yn_y");
        // 评估模板
        if(risk.getParent()!=null){
        	risk.setIsInherit("0yn_y");
        	risk.setTemplate(risk.getParent().getTemplate());
        }

        // 风险动因和风险影响
//        String riskReason = riskForm.getRiskReason();
//        String riskInfluence = riskForm.getRiskInfluence();

        // 责任部门，相关部门，风险指标，控制流程，影响指标
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();
        String riskKpiName = riskForm.getRiskKpiName();
        String controlProcessureName = riskForm.getControlProcessureName();
        String influKpiName = riskForm.getInfluKpiName();
        String influProcessureName = riskForm.getInfluProcessureName();

        // 保存
        o_riskCmpBO.saveRisk(risk, respDeptName, relaDeptName, null, null, null, null, riskKpiName, influKpiName, controlProcessureName,
                influProcessureName, null, null, null, null, null, null, null, null, null,null);
      //保密风险保存应对信息
  		if (null != riskForm.getResponseText()) {
  			RiskResponse responseIdeaForRisk = new RiskResponse();
  			responseIdeaForRisk.setId(Identities.uuid());
  			responseIdeaForRisk.setRiskId(risk.getId());
  			responseIdeaForRisk.setEditIdeaContent(riskForm.getResponseText());
  			o_riskCmpBO.mergeEditIdea(responseIdeaForRisk);
  		}
        map.put("success", true);
        map.put("id", risk.getId());
        map.put("name", risk.getName());

        return map;
    }
    
    
    /** 
      * @Description:宋佳流程中保存
      * @author jia.song@pcitc.com
      * @date 2017年5月24日 下午2:38:18 
      * @param riskForm
      * @param id
      * @param state
      * @param archiveStatus
      * @param schm
      * @param parentId
      * @param assessPlanId
      * @param executionId
      * @param _type
      * @param responseText
      * @return 
      */
    @ResponseBody
    @RequestMapping(value = "/risk/relate/saveRiskInfoForSecurity.f")
    public Map<String, Object> saveRiskInfoForSecurity(
    		RiskForm riskForm,
    		String id,
    		String state,
    		String archiveStatus,
    		String schm,
    		String parentId,
    		String assessPlanId,
    		String executionId,
    		String _type,
    		String responseText,
    		String dimList
    		) {
        Map<String, Object> map = new HashMap<String, Object>();

        Risk risk = new Risk();
        if (state == null || state.equals("")) {
            risk.setDeleteStatus("1");
        } else {
            risk.setDeleteStatus(state); // 将2的状态保存起来
        }
        //归档状态
  		if(archiveStatus==null || archiveStatus.equals("")){
  			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
  		}else{
  			risk.setArchiveStatus(archiveStatus);
  		}
        // 添加
        String makeId = Identities.uuid();
        risk.setId(makeId);
        String companyId = UserContext.getUser().getCompanyid();
        SysOrganization company = new SysOrganization();
        company.setId(companyId);
        risk.setCompany(company);
        risk.setSchm(schm); //  风险分库标识
        // 上级风险
        String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && !parentIdStr.equals("")) {
            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
            String _parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
            Risk parent = o_riskCmpBO.findRiskById(_parentId);
            if (null != parent) {
                risk.setParent(parent);
                risk.setParentName(parent.getName());
                risk.setIdSeq(parent.getIdSeq() + makeId + ".");
                risk.setLevel(parent.getLevel()+1);
            }
        } else {
            risk.setParent(null);
            risk.setParentName("");
            risk.setIdSeq("." + makeId + ".");
            risk.setLevel(1);
        }

        risk.setIsLeaf(true);
        risk.setIsRiskClass(riskForm.getIsRiskClass());
        // 编码，名称，描述
        risk.setCode(riskForm.getCode());
        risk.setName(riskForm.getName());
        risk.setDesc(riskForm.getDesc());
        //序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
        // 是否启用
        risk.setIsUse("0yn_y");
        // 评估模板
        if(risk.getParent()!=null){
        	risk.setIsInherit("0yn_y");
        	risk.setTemplate(risk.getParent().getTemplate());
        }

        // 风险动因和风险影响
//        String riskReason = riskForm.getRiskReason();
//        String riskInfluence = riskForm.getRiskInfluence();

        // 责任部门，相关部门，风险指标，控制流程，影响指标
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();
        String riskKpiName = riskForm.getRiskKpiName();
        String controlProcessureName = riskForm.getControlProcessureName();
        String influKpiName = riskForm.getInfluKpiName();
        String influProcessureName = riskForm.getInfluProcessureName();
      //添加责任部门/人和相关部门/人
  		JSONObject object = new JSONObject();
  		//主责部门List
  		List<RiskOrgTemp> ListZhuzeOrg = new ArrayList<RiskOrgTemp>();
  		if(null!=respDeptName && !"".equals(respDeptName)){
  			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
  			for(int i=0;i<respDeptArray.size();i++){
  				RiskOrgTemp riskOrgZhuze  = new RiskOrgTemp();
  				object = (JSONObject)respDeptArray.get(i);
  				riskOrgZhuze.setId(UUID.randomUUID().toString());
  				riskOrgZhuze.setType("M");
  				//保存责任部门
  				String deptidStr = object.get("deptid").toString();
  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
  					SysOrganization org = new SysOrganization();
  					org.setId(deptidStr);
  					riskOrgZhuze.setSysOrganization(org);
  				}
  				//保存责任人
  				String empidStr = object.get("empid").toString();
  				if(!empidStr.equals("") && !empidStr.equals("null")){
  					SysEmployee emp = new SysEmployee();
  					emp.setId(empidStr);
  					riskOrgZhuze.setEmp(emp);
  				}
  				ListZhuzeOrg.add(riskOrgZhuze);
  			}
  		}
  		//相关部门list
  		List<RiskOrgTemp> ListRelaOrg = new ArrayList<RiskOrgTemp>();
  		if(null!=relaDeptName && !"".equals(relaDeptName)){
  			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
  			for(int i=0;i<relaDeptArray.size();i++){
  				object = (JSONObject)relaDeptArray.get(i);
  				RiskOrgTemp riskOrgRela = new RiskOrgTemp();
  				riskOrgRela.setId(UUID.randomUUID().toString());
  				riskOrgRela.setType("A");
  				//保存相关部门
  				String deptidStr = object.get("deptid").toString();
  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
  					SysOrganization org = new SysOrganization();
  					org.setId(deptidStr);
  					riskOrgRela.setSysOrganization(org);
  				}
  				//保存相关人
  				String empidStr = object.get("empid").toString();
  				if(!empidStr.equals("") && !empidStr.equals("null")){
  					SysEmployee emp = new SysEmployee();
  					emp.setId(empidStr);
  					riskOrgRela.setEmp(emp);
  				}
  				ListRelaOrg.add(riskOrgRela);
  			}
  		}
        // 保存
        String objectId = o_riskIdentifyBO.saveTaskBySome(parentId, risk, assessPlanId, executionId, _type,ListZhuzeOrg,ListRelaOrg);
        lastLeaderBO.saveLastLeader(risk.getId(),objectId,responseText);
        //保存风险过程分值结果表
        o_adjustHistoryResultBO.saveTidyAdjustHistory(dimList, objectId, assessPlanId);
        map.put("success", true);
        map.put("id", risk.getId());
        map.put("name", risk.getName());
        return map;
    }
    
    @ResponseBody
    @RequestMapping(value = "/risk/relate/mergeRiskInfoForCleanUp.f")
    public Map<String,Object> mergeRiskInfoForCleanUp(RiskForm riskForm,String schm){
    	Map<String, Object> map = new HashMap<String, Object>();
    	RiskScoreObject scoreObject = o_scoreObjectBO.get(riskForm.getScoreObjectId());
    	scoreObject.setName(riskForm.getName());
    	scoreObject.setCode(riskForm.getCode());
    	scoreObject.setDesc(riskForm.getDesc());
    	scoreObject.setSchm(schm);
	    String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && !parentIdStr.equals("")) {
            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
            String _parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
            Risk parent = o_riskCmpBO.findRiskById(_parentId);
            if (null != parent) {
            	scoreObject.setParent(parent);
            	scoreObject.setParentName(parent.getName());
            	scoreObject.setIdSeq(parent.getIdSeq() + riskForm.getId() + ".");
            	scoreObject.setLevel(parent.getLevel()+1);
            }
        }
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();
        
        //添加责任部门/人和相关部门/人
  		JSONObject object = new JSONObject();
  		//主责部门List
  		List<RiskOrgRelationTemp> mDeptList = new ArrayList<RiskOrgRelationTemp>();
  		if(null!=respDeptName && !"".equals(respDeptName)){
  			RiskOrgRelationTemp riskOrgZhuze = null;
  			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
  			for(int i=0;i<respDeptArray.size();i++){
  				object = (JSONObject)respDeptArray.get(i);
  				riskOrgZhuze  = new RiskOrgRelationTemp();
  				riskOrgZhuze.setId(UUID.randomUUID().toString());
  				riskOrgZhuze.setType("M");
  				riskOrgZhuze.setRiskScoreObject(new RiskScoreObject(riskForm.getScoreObjectId()));
  				//保存责任部门
  				if(object.get("deptid") != null){
	  				String deptidStr = object.get("deptid").toString();
	  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
	  					SysOrganization org = new SysOrganization();
	  					org.setId(deptidStr);
	  					riskOrgZhuze.setSysOrganization(org);
	  				}
	  				//保存责任人
	  				String empidStr = object.get("empid").toString();
	  				if(!empidStr.equals("") && !empidStr.equals("null")){
	  					SysEmployee emp = new SysEmployee();
	  					emp.setId(empidStr);
	  					riskOrgZhuze.setEmp(emp);
	  				}
  				}
  				mDeptList.add(riskOrgZhuze);
  			}
  		}
  		//相关部门list
  		List<RiskOrgRelationTemp> relaOrgList = new ArrayList<RiskOrgRelationTemp>();
  		if(null!=relaDeptName && !"".equals(relaDeptName)){
  			RiskOrgRelationTemp riskRela = null;
  			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
  			for(int i=0;i<relaDeptArray.size();i++){
  				object = (JSONObject)relaDeptArray.get(i);
  				riskRela = new RiskOrgRelationTemp();
  				riskRela.setId(UUID.randomUUID().toString());
  				riskRela.setType("A");
  				riskRela.setRiskScoreObject(new RiskScoreObject(riskForm.getScoreObjectId()));
  				//保存相关部门
  				if(object.get("deptid") != null){
	  				String deptidStr = object.get("deptid").toString();
	  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
	  					SysOrganization org = new SysOrganization();
	  					org.setId(deptidStr);
	  					riskRela.setSysOrganization(org);
	  				}
	  				//保存相关人
	  				String empidStr = object.get("empid").toString();
	  				if(!empidStr.equals("") && !empidStr.equals("null")){
	  					SysEmployee emp = new SysEmployee();
	  					emp.setId(empidStr);
	  					riskRela.setEmp(emp);
	  				}
  				}
  				relaOrgList.add(riskRela);
  			}
  		}
  		
  		o_riskCmpBO.mergeScoreObject(scoreObject,mDeptList,relaOrgList,riskForm.getResponseText());
  		map.put("flag","success");
  		map.put("message", "修改成功!");
    	return map;
    }
    
    /**
     * 修改风险 state=2,专用于风险评估模块风险的添加
     * @author zj
     * @param riskForm
     * @param id
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/risk/relate/mergeRiskInfo")
    public Map<String, Object> mergeRiskInfo(RiskForm riskForm, String id, String state,String assessPlanId,String riskId,String scoreObjectId,String schm) {
       //判断是在风险库中修改还是流程中修改的
    	if(assessPlanId != null ){
	    	Map<String, Object> map = new HashMap<String, Object>();
	        Risk risk = new Risk();
	        if (state == null || state.equals("")) {
	            risk.setDeleteStatus("1");
	        } else {
	            risk.setDeleteStatus(state); // 将2的状态保存起来
	        }
	      //归档状态
	  		if(risk.getArchiveStatus()==null || risk.getArchiveStatus().equals("")){
	  			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
	  		}
	        String makeId = riskForm.getId();
	        risk.setId(makeId);
	        String companyId = UserContext.getUser().getCompanyid();
	        SysOrganization company = new SysOrganization();
	        company.setId(companyId);
	        risk.setCompany(company);
	        risk.setSchm(schm); //  风险分库标识
	        // 上级风险
	        String parentIdStr = riskForm.getParentId();
	        if (parentIdStr != null && !parentIdStr.equals("")) {
	            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
	            String _parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
	            Risk parent = o_riskCmpBO.findRiskById(_parentId);
	            if (null != parent) {
	                risk.setParent(parent);
	                risk.setParentName(parent.getName());
	                risk.setIdSeq(parent.getIdSeq() + makeId + ".");
	                risk.setLevel(parent.getLevel()+1);
	            }
	        } else {
	            risk.setParent(null);
	            risk.setParentName("");
	            risk.setIdSeq("." + makeId + ".");
	            risk.setLevel(1);
	        }
	
	        risk.setIsLeaf(true);
	        risk.setIsRiskClass(riskForm.getIsRiskClass());
	        // 编码，名称，描述
	        risk.setCode(riskForm.getCode());
	        risk.setName(riskForm.getName());
	        risk.setDesc(riskForm.getDesc());
	        
	        //序号
	        if(riskForm.getSort() == null){	//未指定，自动排序
	        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
			}else{
				risk.setSort(riskForm.getSort());
			}
	        // 是否启用
	        risk.setIsUse("0yn_y");
	        // 评估模板
	        if(risk.getParent()!=null){
	        	risk.setIsInherit("0yn_y");
	        	risk.setTemplate(risk.getParent().getTemplate());
	        }
	        
	        String respDeptName = riskForm.getRespDeptName();
	        String relaDeptName = riskForm.getRelaDeptName();
	        
	        //添加责任部门/人和相关部门/人
	  		JSONObject object = new JSONObject();
	  		//主责部门List
	  		List<RiskOrgTemp> ListZhuzeOrg = new ArrayList<RiskOrgTemp>();
	  		if(null!=respDeptName && !"".equals(respDeptName)){
	  			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
	  			for(int i=0;i<respDeptArray.size();i++){
	  				RiskOrgTemp riskOrgZhuze  = new RiskOrgTemp();
	  				object = (JSONObject)respDeptArray.get(i);
	  				riskOrgZhuze.setId(UUID.randomUUID().toString());
	  				riskOrgZhuze.setType("M");
	  				riskOrgZhuze.setRiskScoreObject(new RiskScoreObject(scoreObjectId));
	  				riskOrgZhuze.setPlanId(assessPlanId);
	  				riskOrgZhuze.setStatus("running");
	  				//保存责任部门
	  				String deptidStr = object.get("deptid").toString();
	  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
	  					SysOrganization org = new SysOrganization();
	  					org.setId(deptidStr);
	  					riskOrgZhuze.setSysOrganization(org);
	  				}
	  				//保存责任人
	  				String empidStr = object.get("empid").toString();
	  				if(!empidStr.equals("") && !empidStr.equals("null")){
	  					SysEmployee emp = new SysEmployee();
	  					emp.setId(empidStr);
	  					riskOrgZhuze.setEmp(emp);
	  				}
	  				ListZhuzeOrg.add(riskOrgZhuze);
	  			}
	  		}
	  		//相关部门list
	  		List<RiskOrgTemp> ListRelaOrg = new ArrayList<RiskOrgTemp>();
	  		if(null!=relaDeptName && !"".equals(relaDeptName)){
	  			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
	  			for(int i=0;i<relaDeptArray.size();i++){
	  				object = (JSONObject)relaDeptArray.get(i);
	  				RiskOrgTemp riskOrgRela = new RiskOrgTemp();
	  				riskOrgRela.setId(UUID.randomUUID().toString());
	  				riskOrgRela.setType("A");
	  				riskOrgRela.setRiskScoreObject(new RiskScoreObject(scoreObjectId));
	  				riskOrgRela.setPlanId(assessPlanId);
	  				riskOrgRela.setStatus("running");
	  				
	  				//保存相关部门
	  				String deptidStr = object.get("deptid").toString();
	  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
	  					SysOrganization org = new SysOrganization();
	  					org.setId(deptidStr);
	  					riskOrgRela.setSysOrganization(org);
	  				}
	  				//保存相关人
	  				String empidStr = object.get("empid").toString();
	  				if(!empidStr.equals("") && !empidStr.equals("null")){
	  					SysEmployee emp = new SysEmployee();
	  					emp.setId(empidStr);
	  					riskOrgRela.setEmp(emp);
	  				}
	  				ListRelaOrg.add(riskOrgRela);
	  			}
	  		}
	
	        //风险辨识流程在结果整理的环节修改风险,操作打分对象表,不影响风险库 吉志强
	        o_riskCmpBO.mergeRiskRelate(risk ,ListZhuzeOrg,ListRelaOrg ,assessPlanId,scoreObjectId,riskId,schm);
	        map.put("success", true);
	        map.put("id", riskForm.getId());
	        map.put("name", riskForm.getName());
	
	        return map;
	    	
    	}else{
    		return mergeRiskInfo(riskForm,id,state);
    	}
    }
    
    /** 
      * @Description: 保存保密风险信息
      * @author jia.song@pcitc.com
      * @date 2017年5月24日 下午8:09:42 
      * @param riskForm
      * @param id
      * @param state
      * @param assessPlanId
      * @param riskId
      * @param scoreObjectId
      * @param schm
      * @return 
      */
    @ResponseBody
    @RequestMapping(value = "/risk/relate/mergeSecurityRiskInfo.f")
    public Map<String, Object> mergeSecurityRiskInfo(RiskForm riskForm, String id, String state,String assessPlanId,String riskId,String scoreObjectId,String schm) {
       //判断是在风险库中修改还是流程中修改的
    	Map<String, Object> map = new HashMap<String, Object>();
        Risk risk = new Risk();
        if (state == null || state.equals("")) {
            risk.setDeleteStatus("1");
        } else {
            risk.setDeleteStatus(state); // 将2的状态保存起来
        }
      //归档状态
  		if(risk.getArchiveStatus()==null || risk.getArchiveStatus().equals("")){
  			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
  		}
        String companyId = UserContext.getUser().getCompanyid();
        SysOrganization company = new SysOrganization();
        company.setId(companyId);
        risk.setCompany(company);
        risk.setSchm(schm); //  风险分库标识
        // 上级风险
        String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && !parentIdStr.equals("")) {
            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
            String _parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
            Risk parent = o_riskCmpBO.findRiskById(_parentId);
            if (null != parent) {
                risk.setParent(parent);
                risk.setParentName(parent.getName());
                risk.setIdSeq(parent.getIdSeq() + riskId + ".");
                risk.setLevel(parent.getLevel()+1);
            }
        } else {
            risk.setParent(null);
            risk.setParentName("");
            risk.setIdSeq("." + riskId + ".");
            risk.setLevel(1);
        }

        risk.setIsLeaf(true);
        risk.setIsRiskClass(riskForm.getIsRiskClass());
        // 编码，名称，描述
        risk.setCode(riskForm.getCode());
        risk.setName(riskForm.getName());
        risk.setDesc(riskForm.getDesc());
        //序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
        // 是否启用
        risk.setIsUse("0yn_y");
        // 评估模板
        if(risk.getParent()!=null){
        	risk.setIsInherit("0yn_y");
        	risk.setTemplate(risk.getParent().getTemplate());
        }
        
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();
        
        //添加责任部门/人和相关部门/人
  		JSONObject object = new JSONObject();
  		//主责部门List
  		List<RiskOrgTemp> ListZhuzeOrg = new ArrayList<RiskOrgTemp>();
  		if(null!=respDeptName && !"".equals(respDeptName)){
  			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
  			for(int i=0;i<respDeptArray.size();i++){
  				RiskOrgTemp riskOrgZhuze  = new RiskOrgTemp();
  				object = (JSONObject)respDeptArray.get(i);
  				riskOrgZhuze.setId(UUID.randomUUID().toString());
  				riskOrgZhuze.setType("M");
  				riskOrgZhuze.setRiskScoreObject(new RiskScoreObject(scoreObjectId));
  				riskOrgZhuze.setPlanId(assessPlanId);
  				riskOrgZhuze.setStatus("running");
  				//保存责任部门
  				String deptidStr = object.get("deptid").toString();
  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
  					SysOrganization org = new SysOrganization();
  					org.setId(deptidStr);
  					riskOrgZhuze.setSysOrganization(org);
  				}
  				//保存责任人
  				String empidStr = object.get("empid").toString();
  				if(!empidStr.equals("") && !empidStr.equals("null")){
  					SysEmployee emp = new SysEmployee();
  					emp.setId(empidStr);
  					riskOrgZhuze.setEmp(emp);
  				}
  				ListZhuzeOrg.add(riskOrgZhuze);
  			}
  		}
  		//相关部门list
  		List<RiskOrgTemp> ListRelaOrg = new ArrayList<RiskOrgTemp>();
  		if(null!=relaDeptName && !"".equals(relaDeptName)){
  			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
  			for(int i=0;i<relaDeptArray.size();i++){
  				object = (JSONObject)relaDeptArray.get(i);
  				RiskOrgTemp riskOrgRela = new RiskOrgTemp();
  				riskOrgRela.setId(UUID.randomUUID().toString());
  				riskOrgRela.setType("A");
  				riskOrgRela.setRiskScoreObject(new RiskScoreObject(scoreObjectId));
  				riskOrgRela.setPlanId(assessPlanId);
  				riskOrgRela.setStatus("running");
  				
  				//保存相关部门
  				String deptidStr = object.get("deptid").toString();
  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
  					SysOrganization org = new SysOrganization();
  					org.setId(deptidStr);
  					riskOrgRela.setSysOrganization(org);
  				}
  				//保存相关人
  				String empidStr = object.get("empid").toString();
  				if(!empidStr.equals("") && !empidStr.equals("null")){
  					SysEmployee emp = new SysEmployee();
  					emp.setId(empidStr);
  					riskOrgRela.setEmp(emp);
  				}
  				ListRelaOrg.add(riskOrgRela);
  			}
  		}

        //风险辨识流程在结果整理的环节修改风险,操作打分对象表,不影响风险库 吉志强
        o_riskCmpBO.mergeRiskRelate(risk ,ListZhuzeOrg,ListRelaOrg ,assessPlanId,scoreObjectId,riskId,schm);
        //保存汇总人填写的应对信息
        lastLeaderBO.updateResponseTextByObjectId(scoreObjectId,riskId,riskForm.getResponseText());
        map.put("success", true);
        map.put("id", riskForm.getId());
        map.put("name", riskForm.getName());

        return map;
    }
    
    public Map<String, Object> mergeRiskInfo(RiskForm riskForm, String id, String state) {
        Map<String, Object> map = new HashMap<String, Object>();

        // 修改
        Risk risk = o_riskCmpBO.findRiskById(id);
        // 上级风险
        String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && (!parentIdStr.equals("")) && (!parentIdStr.equals("[]"))) {
            JSONArray arr = JSONArray.fromObject(parentIdStr);
            if (arr != null) {
                JSONObject obj = (JSONObject) arr.get(0);
                Risk parent = o_riskCmpBO.findRiskById(obj.getString("id"));
                risk.setParent(parent);
                risk.setParentName(parent.getName());
                risk.setIdSeq(parent.getIdSeq() + risk.getId() + ".");
            }
        } else {
            risk.setParent(null);
            risk.setParentName("");
            risk.setIdSeq("." + risk.getId() + ".");
        }

        risk.setIsRiskClass(riskForm.getIsRiskClass());
        // 编码，名称，描述
        risk.setCode(riskForm.getCode());
        risk.setName(riskForm.getName());
        risk.setDesc(riskForm.getDesc());
        
        // 是否启用
        risk.setIsUse("0yn_y");

        // 风险动因和风险影响
//        String riskReason = riskForm.getRiskReason();
//        String riskInfluence = riskForm.getRiskInfluence();

        // 责任部门，相关部门，风险指标，控制流程，影响指标
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();
        String riskKpiName = riskForm.getRiskKpiName();
        String controlProcessureName = riskForm.getControlProcessureName();
        String influKpiName = riskForm.getInfluKpiName();
        String influProcessureName = riskForm.getInfluProcessureName();

        // 保存
        o_riskCmpBO.mergeRiskRelate(risk, respDeptName, relaDeptName, riskKpiName, controlProcessureName, null
        		                    , null,influKpiName,influProcessureName);
      //保存应对措施   2017-8-1
        if (null!= riskForm.getResponseText()){
    		RiskResponse response=o_riskResponseBO.getResponseByRiskId(id);
    		if (null!=response) {
    			response.setEditIdeaContent(riskForm.getResponseText());
    		}else 
    		{
    			response=new RiskResponse();
    			response.setId(Identities.uuid());
    			response.setRiskId(id);
    			response.setEditIdeaContent(riskForm.getResponseText());
    		}
    		o_riskCmpBO.mergeEditIdea(response);
		}
        map.put("success", true);
        map.put("id", risk.getId());
        map.put("name", risk.getName());

        return map;
    }
    
    
    
    
    /**
     * 修改风险 state=2,专用于风险评估模块风险的添加
     * 
     * @author 郭鹏
     * @param riskForm
     * @param id
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/risk/relate/mergeRiskInfoForSecurity")
    public Map<String, Object> mergeRiskInfoForSecurity(RiskForm riskForm, String id, String state,String assessPlanId,String riskId,String scoreObjectId,String schm,String rangObjectDeptEmpId,
    		String extendValue,String respondValue,String saveinfo
    		) {
        Map<String, Object> map = new HashMap<String, Object>();
        Risk risk = new Risk();
        if (state == null || state.equals("")) {
            risk.setDeleteStatus("1");
        } else {
            risk.setDeleteStatus(state); // 将2的状态保存起来
        }
        
        String makeId = riskForm.getId();
        risk.setId(makeId);
        String companyId = UserContext.getUser().getCompanyid();
        SysOrganization company = new SysOrganization();
        company.setId(companyId);
        risk.setCompany(company);
        risk.setSchm(schm); //  风险分库标识
        // 上级风险
        String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && !parentIdStr.equals("")) {
            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
            String _parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
            Risk parent = o_riskCmpBO.findRiskById(_parentId);
            if (null != parent) {
                risk.setParent(parent);
                risk.setParentName(parent.getName());
                risk.setIdSeq(parent.getIdSeq() + makeId + ".");
                risk.setLevel(parent.getLevel()+1);
            }
        } else {
            risk.setParent(null);
            risk.setParentName("");
            risk.setIdSeq("." + makeId + ".");
            risk.setLevel(1);
        }

        risk.setIsLeaf(true);
        risk.setIsRiskClass(riskForm.getIsRiskClass());
        // 编码，名称，描述
        risk.setCode(riskForm.getCode());
        risk.setName(riskForm.getName());
        risk.setDesc(riskForm.getDesc());
        //序号
        if(riskForm.getSort() == null){	//未指定，自动排序
        	risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
        // 是否启用
        risk.setIsUse("0yn_y");
        // 评估模板
        if(risk.getParent()!=null){
        	risk.setIsInherit("0yn_y");
        	risk.setTemplate(risk.getParent().getTemplate());
        }
        
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();
        
        //添加责任部门/人和相关部门/人
  		JSONObject object = new JSONObject();
  		//主责部门List
  		List<RiskOrgTemp> ListZhuzeOrg = new ArrayList<RiskOrgTemp>();
  		if(null!=respDeptName && !"".equals(respDeptName)){
  			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
  			for(int i=0;i<respDeptArray.size();i++){
  				RiskOrgTemp riskOrgZhuze  = new RiskOrgTemp();
  				object = (JSONObject)respDeptArray.get(i);
  				riskOrgZhuze.setId(UUID.randomUUID().toString());
  				riskOrgZhuze.setType("M");
  				riskOrgZhuze.setRiskScoreObject(new RiskScoreObject(scoreObjectId));
  				riskOrgZhuze.setPlanId(assessPlanId);
  				riskOrgZhuze.setStatus("running");
  				//保存责任部门
  				String deptidStr = object.get("deptid").toString();
  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
  					SysOrganization org = new SysOrganization();
  					org.setId(deptidStr);
  					riskOrgZhuze.setSysOrganization(org);
  				}
  				//保存责任人
  				String empidStr = object.get("empid").toString();
  				if(!empidStr.equals("") && !empidStr.equals("null")){
  					SysEmployee emp = new SysEmployee();
  					emp.setId(empidStr);
  					riskOrgZhuze.setEmp(emp);
  				}
  				ListZhuzeOrg.add(riskOrgZhuze);
  			}
  		}
  		//相关部门list
  		List<RiskOrgTemp> ListRelaOrg = new ArrayList<RiskOrgTemp>();
  		if(null!=relaDeptName && !"".equals(relaDeptName)){
  			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
  			for(int i=0;i<relaDeptArray.size();i++){
  				object = (JSONObject)relaDeptArray.get(i);
  				RiskOrgTemp riskOrgRela = new RiskOrgTemp();
  				riskOrgRela.setId(UUID.randomUUID().toString());
  				riskOrgRela.setType("A");
  				riskOrgRela.setRiskScoreObject(new RiskScoreObject(scoreObjectId));
  				riskOrgRela.setPlanId(assessPlanId);
  				riskOrgRela.setStatus("running");
  				
  				//保存相关部门
  				String deptidStr = object.get("deptid").toString();
  				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
  					SysOrganization org = new SysOrganization();
  					org.setId(deptidStr);
  					riskOrgRela.setSysOrganization(org);
  				}
  				//保存相关人
  				String empidStr = object.get("empid").toString();
  				if(!empidStr.equals("") && !empidStr.equals("null")){
  					SysEmployee emp = new SysEmployee();
  					emp.setId(empidStr);
  					riskOrgRela.setEmp(emp);
  				}
  				ListRelaOrg.add(riskOrgRela);
  			}
  		}
        // 保存
        //o_riskCmpBO.mergeRiskRelate(risk, respDeptName, relaDeptName, riskKpiName, controlProcessureName, null, null,influKpiName,influProcessureName);
        //风险辨识流程在结果整理的环节修改风险,操作打分对象表,不影响风险库 吉志强
        o_riskCmpBO.mergeRiskRelate(risk ,ListZhuzeOrg,ListRelaOrg ,assessPlanId,scoreObjectId,riskId,schm);
        /**
         *如果为保密风险则存储应对信息，打分信息，及评估意见
         *郭鹏
         *20170518 
         */
        if (null!=schm&&!"".equals(schm)) {
        	 if(schm.equals("security"))
             {
            //储存评价意见
        		 if(null!=extendValue){
        			 EditIdea editIdea = o_saveAssessBO.findEditIdeaByObjectDeptEmpId(rangObjectDeptEmpId);
     				//评估人员意见内容不为空,添加、编辑状态
     				if(null == editIdea){
     					RangObjectDeptEmp rangObjectDeptEmp = o_rangObjectDeptEmpBO.findRangObjectDeptEmpById(rangObjectDeptEmpId);
     					editIdea = new EditIdea();
     					editIdea.setId(Identities.uuid());
     					editIdea.setObjectDeptEmpId(rangObjectDeptEmp);
     					editIdea.setEditIdeaContent(extendValue);
     					
     					rangObjectDeptEmp.setEditIdea(editIdea);
     					
     					o_assessTaskBO.saveObjectDeptEmp(rangObjectDeptEmp);
     					o_saveAssessBO.mergeEditIdea(editIdea);
     				}else{
     					editIdea.setEditIdeaContent(extendValue);
     					o_saveAssessBO.mergeEditIdea(editIdea);
     				}
     			}	  
        		 //储存应对意见
           		 if(null != respondValue){
           			ResponseIdea responseIdea = o_saveAssessBO.findResponseIdeaByObjectDeptEmpId(rangObjectDeptEmpId);
     					RangObjectDeptEmp rangObjectDeptEmp = o_rangObjectDeptEmpBO.findRangObjectDeptEmpById(rangObjectDeptEmpId);
     					if (responseIdea==null) {
     						responseIdea = new ResponseIdea();
         					responseIdea.setId(Identities.uuid());
         					responseIdea.setObjectDeptEmpId(rangObjectDeptEmp);;
         					responseIdea.setEditIdeaContent(respondValue);
         					o_saveAssessBO.mergeEditIdea(responseIdea);
						}else
						{
							responseIdea.setEditIdeaContent(respondValue);
							o_saveAssessBO.mergeEditIdea(responseIdea);
						}
     					
     				}
           		 //储存打分信息
           	String[] infos= saveinfo.split("\\|");
           	for (int i = 0; i < infos.length; i++) {
			String [] info=	infos[i].split("\\*");
			for (int j = 0; j < info.length; j++) {
			Map<String, Object> mapValue = new HashMap<String, Object>();
			mapValue.put("dimId", info[0]);
			mapValue.put("rangObjectDeptEmpId", rangObjectDeptEmpId);
			mapValue.put("dimValue", info[1]);
			JSONArray jsonObject = JSONArray.fromObject(mapValue);
			o_saveAssessBO.assessSaveOper(jsonObject.toString());
				}
				}
     			}	
             }
     
        
        map.put("success", true);
        map.put("id", riskForm.getId());
        map.put("name", riskForm.getName());

        return map;
    }

    /**
     * 查询风险的详细信息 只使用：上级风险名称，风险名称，编号，风险描述，责任部门，相关部门，影响指标，风险指标，影响流程，控制流程
     * 
     * @param riskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/risk/relate/findRiskDetailInfoById")
    public Map<String, Object> findRiskDetailInfoById(String riskId) {
        Risk risk = o_riskCmpBO.findRiskById(riskId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", risk.getCode());
        map.put("name", risk.getName());
        map.put("parentName", risk.getParent() == null ? "无" : risk.getParent().getName());
        map.put("desc", risk.getDesc());

        String mDeptName = "";// 责任部门
        String aDeptName = "";// 相关部门
        String respPositionName = "";// 责任岗位
        String relaPositionName = "";// 相关岗位

        Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
        for (RiskOrg org : riskOrgs) {
            if (org.getType().equals("M")) { // 责任部门和责任人
                String empStr = "";
                if (null != org.getEmp()) {
                    try {
                        empStr = "(" + org.getEmp().getEmpname() + ")";
                    } catch (Exception e) {
                        // hibnate错误
                    }
                }
                if (null != org.getSysOrganization()) {
                    mDeptName += org.getSysOrganization().getOrgname() + empStr + ",";
                }
            }
            if (org.getType().equals("A")) { // 相关部门和相关人
                String empStr = "";
                if (null != org.getEmp()) {
                    try {
                        empStr = "(" + org.getEmp().getEmpname() + ")";
                    } catch (Exception e) {
                        // hibnate错误
                    }
                }
                if (null != org.getSysOrganization()) {
                    aDeptName += org.getSysOrganization().getOrgname() + empStr + ",";
                }
            }
        }
        if (!mDeptName.equals("")) {
            mDeptName = mDeptName.substring(0, mDeptName.length() - 1);
        }
        if (!aDeptName.equals("")) {
            aDeptName = aDeptName.substring(0, aDeptName.length() - 1);
        }
        if (!respPositionName.equals("")) {
            respPositionName = respPositionName.substring(0, respPositionName.length() - 1);
        }
        if (!relaPositionName.equals("")) {
            relaPositionName = relaPositionName.substring(0, relaPositionName.length() - 1);
        }

        map.put("respDeptName", mDeptName);
        map.put("relaDeptName", aDeptName);
        map.put("respPositionName", respPositionName); // 责任岗位
        map.put("relaPositionName", relaPositionName); // 相关岗位

        String riskKpiName = "";// 风险指标
        String influKpiName = "";// 影响指标
        Set<KpiRelaRisk> kpiRelaRisks = risk.getKpiRelaRisks();
        for (KpiRelaRisk kpi : kpiRelaRisks) {
            if (kpi.getType().equals("RM")) { // 风险指标
                riskKpiName += kpi.getKpi().getName() + ",";
            }
            if (kpi.getType().equals("I")) { // 影响指标
                influKpiName += kpi.getKpi().getName() + ",";
            }
        }
        if (!riskKpiName.equals("")) {
            riskKpiName = riskKpiName.substring(0, riskKpiName.length() - 1);
        }
        if (!influKpiName.equals("")) {
            influKpiName = influKpiName.substring(0, influKpiName.length() - 1);
        }
        map.put("riskKpiName", riskKpiName);
        map.put("influKpiName", influKpiName);

        StringBuffer controlProcessureName = new StringBuffer();// 控制流程
        StringBuffer influProcessureName = new StringBuffer();// 影响流程
        Set<ProcessRelaRisk> ProcessRelaRisks = risk.getRiskProcessures();
        for (ProcessRelaRisk processure : ProcessRelaRisks) {
            if (processure.getType().equals("C")) { // 控制流程
                controlProcessureName.append(processure.getProcess().getName());
                controlProcessureName.append(",");
            }
            if (processure.getType().equals("I")) { // 影响指标
                controlProcessureName.append(processure.getProcess().getName());
                controlProcessureName.append(",");
            }
        }
        if (!controlProcessureName.equals("")) {
            controlProcessureName.append(controlProcessureName.substring(0, controlProcessureName.length() - 1));
        }
        if (!influProcessureName.equals("")) {
            influProcessureName.append(influProcessureName.substring(0, influProcessureName.length() - 1));
        }
        map.put("controlProcessureName", controlProcessureName);
        map.put("influProcessureName", influProcessureName);

        // 是否继承上级模板
        if (null == risk.getIsInherit()) {
            map.put("isInherit", "");
        } else {
            map.put("isInherit", o_dicBO.findDictEntryById(risk.getIsInherit()).getName());
        }

        // 评估模板
        map.put("templeteName", risk.getTemplate() == null ? "" : risk.getTemplate().getName());

        // 公式
        map.put("formulaDefine", risk.getFormulaDefine());

        // 告警方案 下拉框
        map.put("alarmScenario", risk.getAlarmScenario() == null ? "" : risk.getAlarmScenario().getName());

        // 是否定量
        if (null == risk.getIsFix()) {
            map.put("isFix", "");
        } else {
            map.put("isFix", o_dicBO.findDictEntryById(risk.getIsFix()).getName());
        }

        // 是否启用
        if (null == risk.getIsUse()) {
            map.put("isUse", "");
        } else {
            map.put("isUse", o_dicBO.findDictEntryById(risk.getIsUse()).getName());
        }

        // 风险动因
        String riskReasonName = "";
        // 风险影响
        String riskInfluenceName = "";
        Set<RiskRelaRisk> riskRelaRisks = risk.getrRisk();
        for (RiskRelaRisk r : riskRelaRisks) {
            riskReasonName = riskReasonName + r.getRelaRisk().getName() + "</br>";
        }
        if (!riskReasonName.equals("")) {
            riskReasonName = riskReasonName.substring(0, riskReasonName.length() - 1);
        }
        map.put("riskReasonName", riskReasonName);

        riskRelaRisks = risk.getiRisk();
        for (RiskRelaRisk r : riskRelaRisks) {
            riskInfluenceName = riskInfluenceName + r.getRelaRisk().getName() + "</br>";
        }
        map.put("riskInfluenceName", riskInfluenceName);

        return map;
    }
}
