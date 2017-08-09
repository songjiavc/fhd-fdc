package com.fhd.icm.web.controller.standard;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.dao.Page;
import com.fhd.dao.icm.standard.StandardDAO;
import com.fhd.entity.bpm.BusinessWorkFlow;
import com.fhd.entity.icm.standard.Standard;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.bpm.StandardBpmBO;
import com.fhd.icm.business.standard.StandardBO;
import com.fhd.icm.web.form.StandardForm;

/**
 * STANDARD_内控Controller 
 * 
 * @author 元杰
 * @version
 */
@Controller
@SuppressWarnings("deprecation")
public class StandardControl {
	@Autowired
	private StandardBO o_standardBO;
	@Autowired
	private StandardBpmBO o_standardBpmBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	
	/**
	 * <pre>
	 * 工作流调用,内控标准更新汇总,工作流结束自动触发
	 * 内控标准和内控标准下要求改为已完成状态
	 * </pre>
	 * 
	 * @author 元杰
	 * @param standardId
	 * @since  fhd　Ver 1.1
	*/
	public void mergeStandardStatus(String standardId) {
		//将内控标准和内控标准下要求的处理状态置为已完成
		StandardBO standardBO = ContextLoader.getCurrentWebApplicationContext().getBean(StandardBO.class);
		StandardDAO standardDAO = ContextLoader.getCurrentWebApplicationContext().getBean(StandardDAO.class);
		Standard standard = standardBO.findStandardById(standardId);
		standard.setDealStatus(Contents.DEAL_STATUS_FINISHED);
		standard.setStatus(Contents.STATUS_SOLVED);
		standardDAO.merge(standard);
		Set<Standard> standardControlList = standard.getChildren();
		Iterator<Standard> iterator = standardControlList.iterator();
		if(iterator.hasNext()){
			Standard standardControl = iterator.next();
			standardControl.setDealStatus(Contents.DEAL_STATUS_FINISHED);
			standardDAO.merge(standardControl);
		}
	}
	
	/**
	 * 保存内控标准以及下属的内控要求，仅保存
	 * @author 元杰
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@ResponseBody
	@RequestMapping("/icm/standard/saveStandardData.f")
	public Map<String, Object> saveStandardData(StandardForm standardForm, String step) {
		Map<String, Object> result = new HashMap<String, Object>();
		o_standardBO.saveStandardData(standardForm);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 保存内控标准以及下属的内控要求，保存并触发工作流
	 * @author 元杰
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@ResponseBody
	@RequestMapping("/icm/standard/saveStandard.f")
	public Map<String, Object> saveStandard(String executionId, String businessId, StandardForm standardForm, String step) throws IllegalAccessException, InvocationTargetException {
		Map<String, Object> result = new HashMap<String, Object>();
		o_standardBO.saveStandard(executionId, businessId, standardForm, step);//触发JBPM
		result.put("success", true);
		return result;
	}
	
	/**
	 * 保存内控标准和要求的审批意见
	 * @author 元杰
	 */
	@ResponseBody
	@RequestMapping("/icm/standard/saveStandardAdvice.f")
	public Map<String, Object> saveStandardAdvice(String executionId, String businessId, String isPass, String examineApproveIdea, StandardForm standardForm, String step, String isTerminal) {
		Map<String, Object> result = new HashMap<String, Object>();
		o_standardBO.saveStandardAdvice(standardForm);
		
		if(StringUtils.isNotBlank(executionId)){
			if("no".equals(isPass)){
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{
				//jbpm
				if("2".equals(step)){
					Standard standard = o_standardBO.findStandardById(businessId);
					Set<Standard> standardControlList = standard.getChildren();
					String[] standardControlIds = new String[standardControlList.size()];
					int i = 0;
					for(Standard standardControl : standardControlList){//取出内控标准下的要求中的部门集合，去重
						standardControlIds[i] = standardControl.getId();
						++i;
					}
					o_standardBpmBO.startCurCompanyStandardApplyStepTwo(executionId, businessId, standard, standardControlIds, isPass, examineApproveIdea,isTerminal);
				}else if("5".equals(step)){
					o_standardBpmBO.startCurCompanyStandardApplyStepFive(executionId, businessId, isPass, examineApproveIdea);
				}
			}
		}
		result.put("success", true);
		return result;
	}
	
	/**
	 * 根据内控标准ID返回标准Form，如果没有，则新创建标准
	 * @author 元杰
	 * @param standardId 就是businessId
	 * @param executionId
	 */
	@ResponseBody
	@RequestMapping("/icm/standard/findStandardById.f")
	public Map<String, Object> findStandardById(String standardId, String executionId, String step){
		return o_standardBO.findStandardJsonById(standardId, executionId, step);
	}
	
	/**
	 * 返回内控要求form数据
	 * @author 元杰
	 * @param standardControlId
	 */
	@ResponseBody
	@RequestMapping("/icm/standard/findstandardControlById.f")
	public Map<String, Object> findstandardControlById(String standardControlId){
		return o_standardBO.findstandardControlById(standardControlId);
	}
	
	/**
	 * 查询内控标准的列表
	 * @author 元杰
	 * @param query 查询条件
	 * @param dealStatus 状态
	 * @param companyId 公司ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/icm/standard/findStandardByPage.f")
	public Map<String,Object> findStandardByPage(int limit, int start, String query, String dealStatus, String companyId){
		Page<Standard> page = new Page<Standard>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		if(StringUtils.isBlank(companyId)){
			companyId = UserContext.getUser().getCompanyid();
		}
		page=o_standardBO.findStandardBpmListByPage(page, query, dealStatus, companyId);
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<Standard> standardList = page.getResult();
		List<String> idList = new ArrayList<String>();
		for (Standard standard : standardList) {
			idList.add(standard.getId());
		}
		String businessObjectType="icmStandardPlan";
		List<BusinessWorkFlow> businessWorkFlows = o_jbpmBO.findBusinessWorkFlowBySome(null, null,businessObjectType,idList.toArray(new String[idList.size()]));
		Map<String,String> rateMap = new HashMap<String, String>();
		for (BusinessWorkFlow businessWorkFlow : businessWorkFlows) {
			String businessId = businessWorkFlow.getBusinessId();
			String rate = businessWorkFlow.getRate();
			rateMap.put(businessId, rate);
		}
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (Standard standard : standardList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("controlRequirement", standard.getControlRequirement());
			if(standard.getStatus()!=null){
				map.put("status", standard.getStatus());
			}
			if(standard.getDealStatus()!=null){
				map.put("dealstatus", standard.getDealStatus());
			}
			
			//实际进度
			String rate = rateMap.get(standard.getId());
			if(null==rate&&(Contents.DEAL_STATUS_FINISHED.equals(standard.getDealStatus())||Contents.DEAL_STATUS_AFTER_DEADLINE.equals(standard.getDealStatus()))){
				rate="100";
			}else if(null==rate){
				rate="0";
			}
			map.put("actualProgress", rate);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(null != standard.getCreateTime()){
				map.put("createTime", sdf.format(standard.getCreateTime()));
			}else{
				map.put("createTime", "");
			}
			map.put("code", standard.getCode());
			map.put("name", standard.getName());
			map.put("id", standard.getId());
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", page.getTotalItems());

		return result;
	}
}
