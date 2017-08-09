package com.fhd.icm.web.controller.rectify;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.entity.bpm.BusinessWorkFlow;
import com.fhd.entity.icm.rectify.Improve;
import com.fhd.entity.icm.rectify.ImprovePlan;
import com.fhd.entity.icm.rectify.ImproveTrace;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.chart.AngularGauge;
import com.fhd.icm.business.chart.ChartColumn2D;
import com.fhd.icm.business.defect.DefectBO;
import com.fhd.icm.business.rectify.ImproveBO;
import com.fhd.icm.business.rectify.ImprovePlanBO;
import com.fhd.icm.utils.DaysUtils;
import com.fhd.icm.web.form.ImproveForm;
import com.fhd.sys.business.dic.DictBO;
/**
 * 整改计划控制类，整改计划相关的控制跳转等
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-5-6		下午2:06:09
 *
 * @see 	 
 */
@Controller
@SuppressWarnings("deprecation")
public class ImproveControl{
	@Autowired
	private ImproveBO o_improveBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private ImprovePlanBO o_improvePlanBO;
	@Autowired
	private DefectBO o_defectBO;
	
	/**
	 * <pre>
	 * 保存整改计划
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param improveForm 整改计划的Form
	 * @param result
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/improve/mergeImprove.f")
	public Map<String, Object> mergeImprove(ImproveForm improveForm,BindingResult result) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Improve improve = new Improve();
		if(improveForm.getId()!=null){
			improve.setId(improveForm.getId());
		}else{
			improve.setId(Identities.uuid());	
		}	
		String companyId = UserContext.getUser().getCompanyid();
		improve.setCompany(new SysOrganization(companyId));
		improve.setCode(improveForm.getCode());
		improve.setName(improveForm.getName());
		improve.setPlanStartDate(improveForm.getPlanStartDate());
		improve.setImprovementSource(improveForm.getImprovementSource());
		improve.setPlanEndDate(improveForm.getPlanEndDate());
		improve.setImprovementTarget(improveForm.getImprovementTarget());
		improve.setReasonDetail(improveForm.getReasonDetail());
		improve.setDealStatus(Contents.DEAL_STATUS_NOTSTART);
		improve.setStatus(Contents.STATUS_SAVED);
		improve.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
		o_improveBO.mergeImprove(improve);
		returnMap.put("id", improve.getId());
		returnMap.put("success", true);
		return returnMap;
	}
	
	
	/**
	 * <pre>
	 *批量设置整改方案实际开始结束日期
	 * </pre>
	 * 
	 * @author 李克东
	 * @param improvePlanId
	 * @param modifiedRecord
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/improvePlan/mergeimprovePlan.f")
	public Map<String, Object> mergeImprovePlan(String improvePlanId, String modifiedRecord) {
		Map<String, Object> improvePlanMap = new HashMap<String, Object>();
		o_improveBO.mergeImprovePlan(improvePlanId, modifiedRecord);
		improvePlanMap.put("success", true);
		return improvePlanMap;
	}
	/**
	 * <pre>
	 *保存整改方案附件关联
	 * </pre>
	 * 
	 * @author 李克东
	 * @param fileId
	 * @param improvePlanId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/rectify/mergeimprovePlanFile.f")
	public Boolean mergeImprovePlanFile(String fileId, String improvePlanId){
		o_improveBO.mergeImprovePlanFile(fileId, improvePlanId);
		return true;
	}
	

	/**
	 * <pre>
	 * 批量删除整改计划
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param improveIds 待删除的整改计划的列表
	 * @param response
	 * @throws IOException 
	 * @throws  
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping(value = "/icm/improve/removeImproveByIdBatch.f")
	public void removeImproveByIdBatch(String improveIds,HttpServletResponse response) throws IOException {
		PrintWriter out = null;
		String flag = "false";
		try{
			out = response.getWriter();
			o_improveBO.removeImproveByIdBatch(improveIds);
			flag = "true";
		} finally {
			out.write(flag);
			out.close();
		}
	}
	
	
	/**
	 * <pre>
	 *删除整改方案附件关联
	 * </pre>
	 * 
	 * @author 李克东
	 * @param improvePlanId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/rectify/removeimprovePlanFile.f")
	public Boolean removeAssessSampleRelaFile(String improvePlanId){
		o_improveBO.removeImprovePlanFile(improvePlanId);
		return true;
	}
	/**
	 * <pre>
	 *分页查询整改计划列表
	 * </pre>
	 * 
	 * @author 李克东
	 * @param limit
	 * @param start
	 * @param query
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/improve/findImproveListBypage.f")
	public Map<String,Object> findImproveListByPage(int limit, int start, String companyId, String dealStatus, String query){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Page<Improve> page = new Page<Improve>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_improveBO.findImproveListbyPage(page, query, companyId, dealStatus);
		List<Improve> improveList = page.getResult();
		List<String> idList = new ArrayList<String>();
		if(improveList==null){
			improveList = new ArrayList<Improve>();
		}
		for (Improve improve : improveList) {
			idList.add(improve.getId());
		}
		final String businessObjectType="icmRectifyPlan";
		List<BusinessWorkFlow> businessWorkFlows = o_jbpmBO.findBusinessWorkFlowBySome(null, null,businessObjectType,idList.toArray(new String[idList.size()]));
		Map<String,String> rateMap = new HashMap<String, String>();
		for (BusinessWorkFlow businessWorkFlow : businessWorkFlows) {
			String businessId = businessWorkFlow.getBusinessId();
			String rate = businessWorkFlow.getRate();
			rateMap.put(businessId, rate);
		}
		if(null != improveList && improveList.size()>0){
			Map<String, Object> improveMap = null;
			for(Improve improve : improveList){
				improveMap = new HashMap<String, Object>();
				improveMap.put("id", improve.getId());
				improveMap.put("companyId", null != improve.getCompany()?improve.getCompany().getId():"");
				improveMap.put("companyName", null != improve.getCompany()?improve.getCompany().getOrgname():"");
				improveMap.put("code", improve.getCode());
				improveMap.put("name", improve.getName());
				//计划进度
				if(null != improve.getPlanStartDate() && null != improve.getPlanEndDate()){
					improveMap.put("schedule", DaysUtils.calculateRate(improve.getPlanStartDate(), new Date(), improve.getPlanEndDate()));
				}else{
					improveMap.put("schedule", "");
				}
				//实际进度
				String rate = rateMap.get(improve.getId());
				//计划逾期也算完成100%
				if(null==rate&&(Contents.DEAL_STATUS_FINISHED.equals(improve.getDealStatus())||Contents.DEAL_STATUS_AFTER_DEADLINE.equals(improve.getDealStatus()))){
					rate="100";
				}else if(null==rate){
					rate="0";
				}
				improveMap.put("actualProgress", rate);
				improveMap.put("planStartDate", DateUtils.formatShortDate(improve.getPlanStartDate()));
				improveMap.put("planEndDate",DateUtils.formatShortDate(improve.getPlanEndDate()));
				improveMap.put("dealStatus",improve.getDealStatus());
				improveMap.put("status",improve.getStatus());
				improveMap.put("createTime",DateUtils.formatShortDate(improve.getCreateTime()));
				datas.add(improveMap);
			}
			map.put("datas", datas);
			map.put("totalCount", page.getTotalItems());
		}else{
			map.put("datas", new Object[0]);
			map.put("totalCount", "0");
		}
		return map;
	}
	
	/**
	 * <pre>
	 *整改计划跟踪分页查询
	 * </pre>
	 * 
	 * @author 李克东
	 * @param limit
	 * @param start
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/improve/findImproveTraceListBypage.f")
	public Map<String,Object> findImproveTraceListByPage(int limit, int start){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Page<ImproveTrace> page = new Page<ImproveTrace>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_improveBO.findImproveTraceListbyPage(page);
		List<ImproveTrace> improveTraceList = page.getResult();
		if(null != improveTraceList && improveTraceList.size()>0){
			Map<String, Object> improveTraceMap = null;
			for(ImproveTrace improveTrace:improveTraceList ){
				improveTraceMap = new HashMap<String,Object>();
				improveTraceMap.put("actualStartDate", improveTrace.getActualStartDate());
				improveTraceMap.put("actualEndDate",improveTrace.getActualEndDate());
				improveTraceMap.put("finishRate",improveTrace.getFinishRate());
				improveTraceMap.put("improveResult",improveTrace.getImproveResult());
				improveTraceMap.put("comment",improveTrace.getComment());
				datas.add(improveTraceMap);
			}
			map.put("datas", datas);
			map.put("totalCount", page.getTotalItems());
			
		}else{
			map.put("datas", new Object[0]);
			map.put("totalCount", "0");
		}
		
		return map;
	}
	
	
	/**
	 * <pre>
	 *加载整改计划跟踪
	 * </pre>
	 * 
	 * @author 李克东
	 * @param improveId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/improve/findImproveAdviceForview.f")
	public Map<String,Object> findImproveAdviceForview(String improveId){
		return o_improveBO.findImproveForview(improveId);
	}
	/**
	 * <pre>
	 *整改计划加载
	 * </pre>
	 * 
	 * @author 李克东
	 * @param improveId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/improve/findImproveAdviceForForm.f")
	public Map<String,Object> findImproveAdviceForForm(String improveId){
		Map<String,Object> improveMap = new HashMap<String,Object>();
		Map<String,Object> map= new HashMap<String, Object>();
	Improve	improve = null;
	String companyId = UserContext.getUser().getCompanyid();
	if(StringUtils.isNotBlank(improveId)){
		improve = o_improveBO.findImproveById(improveId);
	}else{
		improveId = Identities.uuid2();
		improve = new Improve(improveId);
		improve.setCompany(new SysOrganization(companyId));
	}
	improveMap.put("id", improve.getId());
	improveMap.put("code", improve.getCode());
	improveMap.put("name", improve.getName());
	/*if(improve.getCompany()!=null){
		improveMap.put("companyId", improve.getCompany().getId());	
	}	
	List<ImproveRelaOrg> improveRelaOrg = o_improveBO.findImproveRelaOrgById(improveId);
	for(int i=0;i<improveRelaOrg.size();i++){
		improveMap.put("orgId","[{\"id\":\""+improveRelaOrg.get(0).getOrg().getId()+"\",\"deptno\":\"\",\"deptname\":\"\"}]");
		if(improveRelaOrg.get(i).getType().equals(Contents.EMP_RESPONSIBILITY)){
			improveMap.put("empERId","[{\"id\":\""+improveRelaOrg.get(i).getEmp().getId()+"\",\"empno\":\"\",\"empname\":\"\"}]");
		}
		if(improveRelaOrg.get(i).getType().equals(Contents.EMP_REVIEW_PERSON)){
			improveMap.put("empERPId","[{\"id\":\""+improveRelaOrg.get(i).getEmp().getId()+"\",\"empno\":\"\",\"empname\":\"\"}]");
		}
	}
	List<DefectRelaImprove> defectRelaImproveList = o_improveBO.findDefectRelaImprove(improveId);
    if(defectRelaImproveList != null && defectRelaImproveList.size()>0){
    		improveMap.put("defectId","id:4be2204c-3ced-4ef9-8cef-28b6148fa1dc");	 
    }
	List<ImproveRelaProcess> improveRelaProcessList = o_improveBO.findImproveRelaProcessById(improveId);
	if(improveRelaProcessList != null && improveRelaProcessList.size()>0){
		String improveProcessId = new String();
		for(ImproveRelaProcess improveRelaProcess:improveRelaProcessList){
			String processId=improveRelaProcess.getProcess().getId();
			improveProcessId = processId+","+improveProcessId;
		}
		improveMap.put("processId", "[id:"+improveProcessId+"]");
	}*/
	improveMap.put("planStartDate", improve.getPlanStartDate());
	improveMap.put("planEndDate", improve.getPlanEndDate());
	if(improve.getImprovementSource() != null){
		improveMap.put("improvementSource", improve.getImprovementSource());
	}
	improveMap.put("improvementTarget", improve.getImprovementTarget());
	improveMap.put("reasonDetail", improve.getReasonDetail());
	map.put("data", improveMap);
	map.put("success", true);
	return map;
	
	}
	
	
	/**
	 * <pre>
	 *整改计划预览加载
	 * </pre>
	 * 
	 * @author 郑军祥
	 * @param improveId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/improve/findImproveAdviceview.f")
	public Map<String,Object> findImproveAdviceview(String improveId){
		Map<String,Object> improveMap = new HashMap<String,Object>();
		Map<String,Object> map= new HashMap<String, Object>();
	Improve	improve = o_improveBO.findImproveById(improveId);
	improveMap.put("id", improve.getId());
	improveMap.put("code", improve.getCode());
	improveMap.put("name", improve.getName());
	if(improve.getCompany()!=null){
	improveMap.put("companyName", improve.getCompany().getOrgname());	
	}	
	improveMap.put("planStartDate", improve.getPlanStartDate());
	improveMap.put("planEndDate", improve.getPlanEndDate());
	if(StringUtils.isNotBlank(improve.getImprovementSource())){
		String[] improvementSourceArray = improve.getImprovementSource().split(",");
		Set<String> dictEntryNameSet = new HashSet<String>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("ref_improvement_source");
		for (int i = 0; i < improvementSourceArray.length; i++) {
			for (DictEntry dictEntry : dictEntryList) {
				if(improvementSourceArray[i].equals(dictEntry.getId())){
					dictEntryNameSet.add(dictEntry.getName());
				}
			}
		}
		improveMap.put("improvementSourceName", StringUtils.join(dictEntryNameSet, ","));
	}
	
	StringBuffer empName = new StringBuffer();
	if(null != improve.getLastModifyBy()){
		empName.append(improve.getLastModifyBy().getEmpname()).append(" ( ").append(improve.getLastModifyBy().getEmpcode()).append(" ) "); 
	}else{
		empName.append(improve.getCreateBy().getEmpname()).append(" ( ").append(improve.getCreateBy().getEmpcode()).append(" ) "); 
	}
	improveMap.put("createBy", empName.toString());
	improveMap.put("createTime", (null != improve.getLastModifyTime())?DateUtils.formatShortDate(improve.getLastModifyTime()):(DateUtils.formatShortDate(improve.getCreateTime())));
	
	
	improveMap.put("improvementTarget", improve.getImprovementTarget());
	improveMap.put("reasonDetail", improve.getReasonDetail());
	map.put("data", improveMap);
	map.put("success", true);
	return map;
	
	}

	
	
	
	
	
	/**
	 * <pre>
	 *创建整改计划编号
	 * </pre>
	 * 
	 * @author 李克东
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/rectify/rectify/createRectifyCode.f")
	public Map<String, Object> createRectifyCode() {
		/*String standardCode = "ZG-01";
		String queryHql = "select code from Improve";
		List<String> resultList = null;
		List<Integer> resultList1=new ArrayList<Integer>();
		resultList = o_improveDAO.find(queryHql);
		for(String value:resultList){
			Integer lastValue=Integer.parseInt(value.substring(value.indexOf("-")+1));
			resultList1.add(lastValue);
		}
		if (resultList1.size() > 0 && null != resultList1.get(0)) {
			Integer newCodeNumber = 0;
			String code = resultList.get(0);
			String codeChar = code.substring(0, code.indexOf("-") + 1);
			newCodeNumber =(Integer) Collections.max(resultList1) + 1;
			standardCode = codeChar + newCodeNumber;
		}*/
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS"));
		resultMap.put("success", true);
		return resultMap;
	}
	
	/**
	 * <pre>
	 * 根据整改计划ID查询监控指标的xml
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param improveId 整改计划ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/rectify/findImproveChartXmlByImproveId.f")
	public Map<String, Object> findImproveChartXmlByImproveId(String improveId) {
		Map<String, Object> map = new HashMap<String, Object>();
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String finishRate = "0.00";
		Improve improve = o_improveBO.findImproveById(improveId);
		if(null != improve){
			if(Contents.DEAL_STATUS_FINISHED.equals(improve.getDealStatus())){
				finishRate = "100";
			}else{
				List<ImprovePlan> improvePlanList = o_improvePlanBO.findImprovePlanListBySome(null,improveId);
				Double finishRateTemp = 0d;
				Integer count = 0;
				for (ImprovePlan improvePlan : improvePlanList) {
					List<ImproveTrace> improveTraceList = o_improvePlanBO.findImproveTraceBySome(improvePlan.getId());//找到最后一次保存的进度
					if(null != improveTraceList && improveTraceList.size()>0){
						count++;
						finishRateTemp += improveTraceList.get(0).getFinishRate();
					}
				}
				if(count > 0){
					finishRate = decimalFormat.format(((double)finishRateTemp/count));
				}
			}
		}
		map.put("finishRateXml", AngularGauge.getXml(finishRate, "","0"));
		map.put("finishRate", finishRate);
		return map;
	}
	
	/**
	 * <pre>
	 * 根据公司ID查询监控指标的xml
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param companyId 公司ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/rectify/findImproveChartXmlByComanyId.f")
	public Map<String, Object> findImproveChartXmlByComanyId(String companyId) {
		Map<String, Object> map = new HashMap<String, Object>();
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String finishRate = "0.00";
		List<ImprovePlan> improvePlanList = o_improvePlanBO.findImprovePlanListBySome(companyId,null);
		Double finishRateTemp = 0d;
		Integer count = 0;
		for (ImprovePlan improvePlan : improvePlanList) {
			List<ImproveTrace> improveTraceList = o_improvePlanBO.findImproveTraceBySome(improvePlan.getId());//找到最后一次保存的进度
			if(null != improveTraceList && improveTraceList.size()>0){
				count++;
				finishRateTemp += improveTraceList.get(0).getFinishRate();
			}
		}
		if(count > 0){
			finishRate = decimalFormat.format(((double)finishRateTemp/count));
		}
		//缺陷级别
		List<Object[]> defectLevelInfoList = o_defectBO.findDefectLevelListByCompanyId(companyId,"improve");
		//部门缺陷
		List<Object[]> orgDefectInfoList = o_defectBO.findOrgDefectListByCompanyId(companyId,"improve");
				
		map.put("finishRateXml", AngularGauge.getXml(finishRate, "","0"));
		map.put("finishRate", finishRate);
		map.put("defectLevelXml", ChartColumn2D.getXml("各个等级的缺陷数量统计图", "", "", "", defectLevelInfoList));
		map.put("orgDefectXml", ChartColumn2D.getXml("各个部门的缺陷数量统计图", "", "", "", orgDefectInfoList));
		return map;
	}
	
	/**
	 * <pre>
	 * 验证数据有效性
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param id 待验证的整改计划ID
	 * @param code 编号
	 * @param response 
	 * @return
	 * @throws IOException
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/icm/rectify/validateImprove.f")
	public Map<String, Object> validateImprove(String id, String code,
			HttpServletResponse response) throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		String companyId = UserContext.getUser().getCompanyid();
		String flagString = o_improveBO.validateImprove(id,companyId, code);
		result.put("success", true);
		result.put("flagStr", flagString);
		return result;
	}
	
}