package com.fhd.ra.web.controller.risk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.entity.risk.RiskAssessReport;
import com.fhd.entity.risk.RiskAssessReportTemplate;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.risk.RiskAssessReportTemplateBO;

/**
 * 报告模板
 * @author 邓广义
 *
 */
@Controller
public class RiskAssessReportTemplateControl {
	
	
		@Autowired
		private RiskAssessReportTemplateBO o_riskAssessReportTemplateBO;
		
		@Autowired
		private JBPMBO o_jbpmBO;
		
		@Autowired
		private RiskAssessPlanBO  o_riskAssessPlanBO;
	/**
	 * 根据数据字典获取报告模板tree
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/reportemplate.f")
	public Map<String, Object> findReportTemplateTree() {
		List<Map<String, Object>> icmList = new ArrayList<Map<String, Object>>();
		Map<String, Object> node = new HashMap<String, Object>();
		Map<String, Object> icm = new HashMap<String, Object>();
		icm.put("text", "内控评价报告");
		icm.put("id", "icmassessreprot");
		icm.put("expanded", false);
		icm.put("leaf", true);
		icmList.add(icm);
		node.put("text", "内控报告");
		node.put("children", icmList);
		node.put("id", "icmreport");
		node.put("expanded", false);
		node.put("leaf", false);
		// List<Map<String , Object>> riskList = new ArrayList<Map<String ,
		// Object>>();
		return node;
	}
	/**
	 * 保存报告模板
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/saveReportTemplate.f")
	public Map<String,Object> saveReportTemplate(String templateId,String templateData,String templateType,String templateCode,String templateName) throws Exception{
		o_riskAssessReportTemplateBO.saveReportTemplate(templateId,templateData,templateType,templateCode,templateName);
		Map<String , Object> map = new  HashMap<String , Object>(0);
		map.put("success", true);
		return map;
	}
	/**
	 * 根据报告模板类型
	 * 获取报告模板列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/findReportTemplateList.f")
	public Map<String,Object> findReportTemplateList(String templateType){
		List<RiskAssessReportTemplate> list = this.o_riskAssessReportTemplateBO.findReportTemplateListByType(templateType);
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		for(RiskAssessReportTemplate rart :list){
			Map<String , Object> result = new  HashMap<String , Object>();
			result.put("id", rart.getId());
			result.put("companyName", rart.getCompany().getOrgname());
			result.put("templateName", rart.getName());
			result.put("templateCode", rart.getCode());
			result.put("templateType", rart.getTemplateType().getName());
			result.put("isDefault", rart.isDefault()?"是":"否");
			datas.add(result);
		}
		Map<String , Object> map = new  HashMap<String , Object>(0);
		map.put("datas", datas);
		return map;
	}
	/**
	 * 设置默认模板
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/setDefaultTemplate.f")
	public Map<String,Object> setDefaultTemplate(String id){
		this.o_riskAssessReportTemplateBO.setDefaultReportTemplate(id);
		Map<String , Object> map = new  HashMap<String , Object>(0);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 查询是否设置了默认模板
	 * @param templateType
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/hasDefautTemplate.f")
	public Map<String,Object> hasDefautTemplate(String templateType){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", o_riskAssessReportTemplateBO.hasDefautTemplate(templateType));
		
		return map;
	}
	/**
	 * 根据ID删除模板
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/delTemplate.f")
	public Map<String,Object> delTemplate(String id){
		this.o_riskAssessReportTemplateBO.delReportTemplate(id);
		Map<String , Object> map = new  HashMap<String , Object>(0);
		map.put("success", true);
		return map;
	}
	/**
	 * 根据ID查询模板
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/findTemplateByid.f")
	public Map<String,Object> findTemplateByid(String id)throws IOException{
		RiskAssessReportTemplate rart = this.o_riskAssessReportTemplateBO.findReportTemplateById(id);
		Map<String , Object> map = new  HashMap<String , Object>();
		if(null!=rart){
			map.put("templateCode", rart.getCode());
			map.put("templateName", rart.getName());
			if(null!=(rart.getTemplateData())){
				map.put("templateDataText",IOUtils.toString(new ByteArrayInputStream(rart.getTemplateData()),"GBK"));
			}
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("data", map);
		result.put("success", true);
		return result;
	}
	/**
	 * 根据评价计划的ID 获得 此评价计划的报告
	 * 如果没有 则从报告模板中获得 此类型的默认模板
	 * @param AssessPlanId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sys/report/findReportByAssessPlanIdOrDefaultTemplate.f")
	public Map<String,Object> findReportByAssessPlanIdOrDefaultTemplate(String AssessPlanId)throws IOException{
		RiskAssessReport rar = this.o_riskAssessReportTemplateBO.findReportByAssessPlanId(AssessPlanId);
		Map<String , Object> map = new  HashMap<String , Object>();
		Map<String,Object> result = new HashMap<String,Object>();
		if(null!=rar){
			if(null!=(rar.getReportData())){
				map.put("templateDataText",IOUtils.toString(new ByteArrayInputStream(rar.getReportData()),"GBK"));
				map.put("reportId", rar.getId());
			}
		}
		result.put("data", map);
		result.put("success", true);
		return result;
	}
	/**
	 * 保存or修改评估报告
	 * @param assessPlanId
	 * @param reportData
	 * @param id
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/sys/report/saveAssessReport.f")
	public Map<String , Object> saveAssessReport(String id,String assessPlanId,String reportData,HttpServletRequest req) throws Exception{
		this.o_riskAssessReportTemplateBO.saveAssessReport(id,assessPlanId,reportData,req);
		Map<String , Object> map = new  HashMap<String , Object>(0);
		map.put("success", true);
		return map;
	}
	/**
	 * 预览报告
	 * 替换“${**}”成对应的表格
	 * @param assessPlanId
	 * @param reportData
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/sys/report/previewAssessReport.f")
	public Map<String , Object> previewAssessReport(String assessPlanId , String reportData,HttpServletRequest req) throws Exception{
		String stream = this.o_riskAssessReportTemplateBO.previewAssessReport(req,assessPlanId,reportData);

		Map<String , Object> map = new  HashMap<String , Object>(0);
		map.put("stream", stream);
		map.put("success", true);
		return map;
	}
	/**
	 * 提交执行工作流 提交给领导判断是否保存
	 */
	@ResponseBody
	@RequestMapping("/sys/report/doAssessReportWorkFlow.f")
	public Map<String , Object> doAssessReportWorkFlow(String executionId,String AssessPlanId,String empId){
		RiskAssessReport rar = this.o_riskAssessReportTemplateBO.findRiskAssessReportByAssessId(AssessPlanId);
		Map<String , Object> result = new  HashMap<String , Object>();
		String approver = "";
		if(null!=rar){
			if(null!=rar.getReportDoc()){
				JSONArray jsonArray = JSONArray.fromObject(empId);
				if (jsonArray.size() > 0){
					 JSONObject jsobj = jsonArray.getJSONObject(0);
					 approver = jsobj.getString("id");//审批人
				}
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("assessReportId", "".equals(approver)?"admin":approver);
				o_jbpmBO.doProcessInstance(executionId, variables);
				result.put("data", true);
			}else{
				result.put("data", false);
			}
		}
		result.put("success", true);
		return result;
	}
	/**
	 * 下载报告.
	 * @author 邓广义
	 * @param id
	 * @param os
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/sys/report/downloadReport.f")
	public void downloadReport(String assessPlanId, OutputStream os, HttpServletResponse response,HttpServletRequest req) throws Exception {
		RiskAssessReport riskAssessReport =  this.o_riskAssessReportTemplateBO.findRiskAssessReportByAssessId(assessPlanId);
		response.setContentType("doc");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ URLEncoder.encode("风险评估计划报告"+".doc", "UTF-8"));
		if(null!=riskAssessReport){
			IOUtils.write(riskAssessReport.getReportDoc(), os);
		}
	}
	/**
	 * 查询风险评估计划下的风险评估报告
	 * @param AssessPlanId
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/sys/report/findRiskAssessReportByAssessId.f")
	public Map<String,Object> findRiskAssessReportByAssessId(String AssessPlanId,HttpServletRequest req) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		RiskAssessReport rar = this.o_riskAssessReportTemplateBO.findRiskAssessReportByAssessId(AssessPlanId);
		String stream = "";
		if(null!=rar){
			 stream = this.o_riskAssessReportTemplateBO.previewAssessReport(req,AssessPlanId,IOUtils.toString(new ByteArrayInputStream(rar.getReportData()),"GBK"));
		}
		result.put("data", stream);
		result.put("success", true);
		return result;
	}
	/**
	 * 结束工作流
	 * @param executionId
	 * @param AssessPlanId
	 * @param response
	 */
	@ResponseBody
	@RequestMapping("/sys/report/doAssessReportWorkFlowEnd.f")
	public void doAssessReportWorkFlowEnd(String executionId,String AssessPlanId, HttpServletResponse response) throws Exception {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
			o_riskAssessPlanBO.mergeAssessPlanStatusByplanId(AssessPlanId);
			out.write("true");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
}
