/**
 *  
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.web.controller.response;

import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.icm.control.MeasureRelaOrg;
import com.fhd.entity.response.SolutionRelaOrg;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.response.ResponseListBO;
import com.fhd.ra.business.response.ResponseSolutionBO;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 
 */
@Controller
@SuppressWarnings("unchecked")
public class ResponseListControl{
	@Autowired
	private ResponseListBO o_responseListBO;
	@Autowired
	private ResponseSolutionBO o_responseSolutionBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	/**
	 * 查询应对措施和控制措施列表
	 * @author 宋佳
	 * @param limit
	 * @param start
	 * @param sort
	 * @param dir
	 * @param query 查询条件
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/response/findresponselistbypage.f")
	@RecordLog("查询应对计划列表")
	public Map<String, Object> findResponseListByPage(int limit, int start, String sort, String dir, String query,String selectId,String type,String businessType,String queryJson) {
		Map<String,Object> resultMap=new HashMap<String,Object>();
		Map<String,Object> objectMap= null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Object[]> responseList = null;
		Integer count = null;
		//应对措施和控制措施，总数
		Map<String,Object> listData = o_responseListBO.findResponseListByType(limit, start, sort, dir, selectId, type,businessType,queryJson,query);
		responseList = (List<Object[]>) listData.get("list");
		count = Integer.valueOf(listData.get("count").toString());
		/*获取控制措施对应所有的机构和人员*/
		Map<String,MeasureRelaOrg> mapMeaRelaOrg = new HashMap<String,MeasureRelaOrg>();
		Map<String,MeasureRelaOrg> mapMeaRelaEmp = new HashMap<String,MeasureRelaOrg>();
		List<MeasureRelaOrg> measureRelaOrgList = o_responseListBO.findOrgAndEmpByMeasureId("");
		for(MeasureRelaOrg mro : measureRelaOrgList){
		    if(mro.getType().equals(Contents.ORG_RESPONSIBILITY)){
		        mapMeaRelaOrg.put(mro.getControlMeasure().getId(), mro);
		    }else{
		        mapMeaRelaEmp.put(mro.getControlMeasure().getId(), mro);
		    }
		}
		/*获取应对措施对应的机构和人员*/
		Map<String,SolutionRelaOrg> mapSoRelaOrg = new HashMap<String,SolutionRelaOrg>();
		Map<String,SolutionRelaOrg> mapSoRelaEmp = new HashMap<String,SolutionRelaOrg>();
		List<SolutionRelaOrg> solutionRelaOrgList = o_responseSolutionBO.findOrgAndEmpFromSolutionById("");
		for(SolutionRelaOrg sro : solutionRelaOrgList){
		    if(sro.getType().equals(Contents.ORG_RESPONSIBILITY)){
		        mapSoRelaOrg.put(sro.getSolution().getId(), sro);
		    }else{
		        mapSoRelaEmp.put(sro.getSolution().getId(), sro);
		    }
        }
		for (Iterator<Object[]> iterator = responseList.iterator(); iterator.hasNext();) {
			objectMap = new HashMap<String,Object>();
			Object[] objects = (Object[]) iterator.next();
			objectMap.put("riskParentId", objects[0]);
			objectMap.put("riskId", null == objects[1]?"":objects[1].toString());
			objectMap.put("riskName", null == objects[2]?"":objects[2].toString());
			objectMap.put("measureId", objects[3]);
			objectMap.put("measureName", objects[4]);
			objectMap.put("type", objects[5]);
			if("measure".equals(objects[5])){
			    objectMap.put("orgName", null == mapMeaRelaOrg.get(objects[3].toString())?"":null == mapMeaRelaOrg.get(objects[3].toString()).getOrg()?"":mapMeaRelaOrg.get(objects[3].toString()).getOrg().getOrgname());
			    objectMap.put("empName", null == mapMeaRelaEmp.get(objects[3].toString())?"":null == mapMeaRelaEmp.get(objects[3].toString()).getEmp()?"":mapMeaRelaEmp.get(objects[3].toString()).getEmp().getEmpname());
			    objectMap.put("empId", null == mapMeaRelaEmp.get(objects[3].toString())?"":null == mapMeaRelaEmp.get(objects[3].toString()).getEmp()?"":mapMeaRelaEmp.get(objects[3].toString()).getEmp().getId());
			}else if("solution".equals(objects[5])){
			    objectMap.put("orgName", null == mapSoRelaOrg.get(objects[3].toString())?"":null == mapSoRelaOrg.get(objects[3].toString()).getOrg()?"":mapSoRelaOrg.get(objects[3].toString()).getOrg().getOrgname());
                objectMap.put("empName", null == mapSoRelaEmp.get(objects[3].toString())?"":null == mapSoRelaEmp.get(objects[3].toString()).getEmp()?"":mapSoRelaEmp.get(objects[3].toString()).getEmp().getEmpname());
                objectMap.put("empId", null == mapSoRelaEmp.get(objects[3].toString())?"":null == mapSoRelaEmp.get(objects[3].toString()).getEmp()?"":mapSoRelaEmp.get(objects[3].toString()).getEmp().getId());
			}
			objectMap.put("status", objects[6]);
			objectMap.put("archiveStatus", objects[7]);
			resultList.add(objectMap);
		}
		resultMap.put("datas", resultList);
		resultMap.put("totalCount", count);
		
		return resultMap;
	}
	
	/**
	 * 建设计划列表.
	 * @author 宋佳
	 * @param businessId 计划ID
	 * @param executionId 流程ID
	 * @return Map<String, Object>
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/responseplan/findresponselistbybusinessid.f")
	public Map<String, Object> findResponseListByBusinessId(String businessId,String executionId) {
		Map<String,Object> resultMap=new HashMap<String,Object>();
		Map<String,Object> objectMap= null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Object[]> responseList = null;
		List<String> riskIds = null;
		//获取评估人
		RiskAssessPlanTakerObject riskAssessPlanTakerObject = (RiskAssessPlanTakerObject)o_jbpmOperate.getVariableObj(executionId,"riskTaskEvaluator");
		if(null == riskAssessPlanTakerObject){
			ArrayList<RiskAssessPlanTakerObject> list = (ArrayList<RiskAssessPlanTakerObject>)o_jbpmOperate.getVariableObj(executionId,"riskTaskEvaluators");
			List<String> empList = new ArrayList<String>();
			for(RiskAssessPlanTakerObject taker : list){
				String empId = taker.getRiskTaskEvaluatorId();
				empList.add(empId);
			}
			riskIds = o_responseListBO.getRiskIdsByEmpIdListAndBusinessId(businessId,empList);
			responseList = o_responseListBO.findSolutionListByRiskIdsAndEmpIds(businessId,riskIds,empList);
		}else{
			//评估人
			String empId = riskAssessPlanTakerObject.getRiskTaskEvaluatorId();
			riskIds = o_responseListBO.getRiskIdsByEmpIds(businessId,empId);
			responseList = o_responseListBO.findSolutionListByRiskIds(businessId,riskIds,empId);
		}
		/*获取应对措施对应的机构和人员*/
		List<SolutionRelaOrg> solutionRelaOrgList = o_responseSolutionBO.findOrgAndEmpFromSolutionById("");
		if(null != responseList){
			for (Iterator<Object[]> iterator = responseList.iterator(); iterator.hasNext();) {
				objectMap = new HashMap<String,Object>();
				Object[] objects = (Object[]) iterator.next();
				objectMap.put("riskParentId", objects[0]);
				objectMap.put("riskId", objects[1]);
				objectMap.put("riskName", objects[2]);
				objectMap.put("measureId", objects[3]);
				objectMap.put("measureName", objects[4]);
				objectMap.put("type", objects[5]);
				if("E".equals(objects[5])){
					for(SolutionRelaOrg solutionRelaOrg : solutionRelaOrgList){
						if(solutionRelaOrg.getSolution().getId().equals(objects[3])){
							if(Contents.ORG_RESPONSIBILITY.equals(solutionRelaOrg.getType())){
								String orgName = solutionRelaOrg.getOrg().getOrgname();
								objectMap.put("orgName", orgName);
							}else if(Contents.EMP_RESPONSIBILITY.equals(solutionRelaOrg.getType())){
								String empName = solutionRelaOrg.getEmp().getEmpname();
								objectMap.put("empName", empName);
							}else{
								
							}
						}
					}
				}
				resultList.add(objectMap);
			}
		}
		resultMap.put("datas", resultList);
		return resultMap;
	}
	/**
	 * 风险应对删除功能
	 * @author 宋佳
	 * @param measureIds
	 * @param response
	 * @throws IOException
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/response/removeresponselistbyids.f")
	public Map<String, Object> removeResponseListByIds(String ids){
		int delStatus = 0;
		Map<String, Object> returnMp = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(ids)){
			delStatus = o_responseListBO.deleteAllRelaFromSolutionBySolutionId(ids);
		}
		if(delStatus!=0){
			returnMp.put("success", true);
		}else{
			returnMp.put("failure", false);
		}
		return returnMp;
	}
	/**
	 * 风险应对删除功能
	 * @author 宋佳
	 * @param measureIds
	 * @param response
	 * @throws IOException
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/response/removeresponsemeasurelistbyids.f")
	public Map<String, Object> removeResponseMeasureListByIds(String ids){
		int delStatus = 0;
		Map<String, Object> returnMp = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(ids)){
			delStatus = o_responseListBO.deleteAllRelaFromMeasureByMeasureId(ids);
		}
		if(delStatus!=0){
			returnMp.put("success", true);
		}else{
			returnMp.put("failure", false);
		}
		return returnMp;
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
	@ResponseBody
    @RequestMapping(value = "/response/gettreeresponsecount.f")
    public Map<String, Object> getTreeResponseCount(String query){
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapHistoryCount = o_responseSolutionBO.getTreeResponseCount();
        Object scount = (Object) mapHistoryCount.get("scount");
        Object mcount = (Object) mapHistoryCount.get("mcount");
        Map<String, Object> sgroupMap = (Map<String, Object>) mapHistoryCount.get("sgroup");
        Map<String, Object> mgroupMap = (Map<String, Object>) mapHistoryCount.get("mgroup");
        Map<String, Object> othersgroupMap = (Map<String, Object>) mapHistoryCount.get("othersgroupMap");
        Map<String, Object> othermgroupMap = (Map<String, Object>) mapHistoryCount.get("othermgroupMap");
        List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
        
        Map<String, Object> childrenSave = new HashMap<String, Object>();
        childrenSave.put("id", "saved");
        childrenSave.put("text", "<font color=red>"+"待提交("+(sgroupMap.get(Contents.RISK_STATUS_SAVED) == null?"0":sgroupMap.get(Contents.RISK_STATUS_SAVED).toString())+","+(mgroupMap.get(Contents.RISK_STATUS_SAVED) == null?"0":mgroupMap.get(Contents.RISK_STATUS_SAVED).toString())+")"+"</font>");
        childrenSave.put("dbid", "saved");
        childrenSave.put("leaf", true);
        childrenSave.put("code", "saved");
        childrenSave.put("type", "type");
        if(StringUtils.isNotBlank(query)){
        	if("待提交".contains(query)){
        		childrenList.add(childrenSave);
        	}
        }else{
        	childrenList.add(childrenSave);
        }
        Map<String, Object> childrenExamine = new HashMap<String, Object>();
        childrenExamine.put("id", "examine");
        childrenExamine.put("text", "<font color=green>"+"审批中("+(sgroupMap.get(Contents.RISK_STATUS_EXAMINE) == null?"0":sgroupMap.get(Contents.RISK_STATUS_EXAMINE).toString())+","+(mgroupMap.get(Contents.RISK_STATUS_EXAMINE) == null?"0":mgroupMap.get(Contents.RISK_STATUS_EXAMINE).toString())+")"+"</font>");
        childrenExamine.put("dbid", "examine");
        childrenExamine.put("leaf", true);
        childrenExamine.put("code", "examine");
        childrenExamine.put("type", "examine");
        if(StringUtils.isNotBlank(query)){
        	if("审批中".contains(query)){
        		childrenList.add(childrenExamine);
        	}
        }else{
        	childrenList.add(childrenExamine);
        }
        Map<String, Object> childrenArchived = new HashMap<String, Object>();
        childrenArchived.put("id", "archived");
        childrenArchived.put("text", "已归档("+(sgroupMap.get(Contents.RISK_STATUS_ARCHIVED) == null?"0":sgroupMap.get(Contents.RISK_STATUS_ARCHIVED).toString())+","+(mgroupMap.get(Contents.RISK_STATUS_ARCHIVED) == null?"0":mgroupMap.get(Contents.RISK_STATUS_ARCHIVED).toString())+")");
        childrenArchived.put("dbid", "archived");
        childrenArchived.put("leaf", true);
        childrenArchived.put("code", "archived");
        childrenArchived.put("type", "archived");
        if(StringUtils.isNotBlank(query)){
        	if("已归档".contains(query)){
        		childrenList.add(childrenArchived);
        	}
        }else{
        	childrenList.add(childrenArchived);
        }
        
        if(null != othersgroupMap.get("other") || null != othermgroupMap.get("other")){
            Map<String, Object> childrenOther = new HashMap<String, Object>();
            childrenOther.put("id", "other");
            childrenOther.put("text", "未分类("+(othersgroupMap.get("other") == null?"0":othersgroupMap.get("other").toString())+","+(othermgroupMap.get("other") == null?"0":othermgroupMap.get("other").toString())+")");
            childrenOther.put("dbid", "other");
            childrenOther.put("leaf", true);
            childrenOther.put("code", "other");
            childrenOther.put("type", "other");
            if(StringUtils.isNotBlank(query)){
            	if("未分类".contains(query)){
            		childrenList.add(childrenOther);
            	}
            }else{
            	childrenList.add(childrenOther);
            }
        }
        
        Map<String, Object> allMap = new HashMap<String, Object>();
        allMap.put("id", "all");
        allMap.put("text", "全部("+scount+","+mcount+")");
        allMap.put("dbid", "all");
        allMap.put("leaf", false);
        allMap.put("code", "all");
        allMap.put("type", "all");
        allMap.put("expanded", true);
        allMap.put("children", childrenList);
        
        map.put("children", allMap);
        return map;
    }
	
}
