package com.fhd.sys.web.controller.dataimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.dao.sys.dataimport.RiskFromExcelDAO;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.sys.entity.RiskFromExcel;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.dataimport.DataImportBO;
import com.fhd.sys.web.form.file.FileForm;

/**
 * @author 元杰
 */
@Controller
public class DataImportControl {
	
	@Autowired
	private DataImportBO o_dataImportBO;
	@Autowired
	private KpiBO o_kpiBO;
	@Autowired
	private RiskFromExcelDAO o_riskFromExcelDAO;

	/**
	 * 上传风险数据模板并将数据写入风险数据临时表
	 * @author 元杰
	 * @param form
	 * @param result
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/dataimport/uploadRiskDataUp.f")
	public void uploadRiskDataUp(FileForm form, HttpServletResponse response) throws IOException {
	    Map<String, Object> map = new HashMap<String, Object>();
		try{
			List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
				.readEspecialExcel(form.getFile().getInputStream(), 0);// 读取文件(第二页)
			//将数据写入数据库临时表
			ArrayList<RiskFromExcel> riskFromExcelList = o_dataImportBO.getRiskDataList(excelDatas);
			o_dataImportBO.saveRiskFromExcel(riskFromExcelList);
			o_dataImportBO.updateRiskFromExcelLevel(true,null,null);
			map.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(map));
	}
	
	/**
	 * 展现临时表中风险数据
	 * @author 元杰
	 * @param form
	 * @param result
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/dataimport/risk/showRiskFromRiskList.f")
	public Map<String, Object> showRiskFromRiskList(String query){
		//查询列表之前，先验证一下数据
		ArrayList<RiskFromExcel> riskFromExcelList = o_dataImportBO.validateRiskData();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		for (RiskFromExcel risk : riskFromExcelList) {
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("id", risk.getId());//id
//			map.put("companyId", risk.getCompany().getId());//companyId
			map.put("riskCode", risk.getCode());//riskCode
			map.put("riskName", risk.getName());//riskName
			map.put("edesc", risk.getDesc());//edesc
			map.put("parentCode", risk.getParentCode());//parentCode
			map.put("parentName", risk.getParentName());//parentName
//			map.put("idSeq", risk.ge);//idSeq
			map.put("esort", risk.getEsort());//esort
			map.put("exRow", risk.getExRow());//esort
//			map.put("isLeaf", risk[9]);//isLeaf
			if(null != risk.getIsInherit()){
				if(risk.getIsInherit().equals("0yn_y")){
					map.put("isInherit", "是");//isInherit
				}else if(risk.getIsInherit().equals("0yn_n")){
					map.put("isInherit", "否");//isInherit
				}
			}
			map.put("probability", risk.getProbability());//probability
			map.put("impact", risk.getImpact());//impact
			map.put("urgency", risk.getUrgency());//urgency
			map.put("riskLevel", risk.getRiskLevel());//riskLevel
			map.put("riskStatus", risk.getRiskStatus());//riskStatus
			map.put("SCHM", risk.getSCHM());//SCHM

			if (null !=risk.getCreateOrg())
			{
				map.put("createOrg", risk.getCreateOrg().getNameSeq());//createOrg
			}
			if(StringUtils.isNotBlank(risk.getRespOrgsName())){
				map.put("respOrgs", risk.getRespOrgsName());//respOrgs
			}if(StringUtils.isNotBlank(risk.getRelaOrgsName())){
				map.put("relaOrgs", risk.getRelaOrgsName());//respOrgs
			}if(StringUtils.isNotBlank(risk.getImpactTargetName())){
				map.put("impactTarget", risk.getImpactTargetName());//respOrgs
			}if(StringUtils.isNotBlank(risk.getImpactProcessName())){
				map.put("impactProcess", risk.getImpactProcessName());//respOrgs
			}if(StringUtils.isNotBlank(risk.getAssessmentTemplateName())){
				map.put("assessmentTemplate", risk.getAssessmentTemplateName());//respOrgs
			}
			map.put("comment", risk.getComment());//assessmentTemplate
			if(StringUtils.isBlank(risk.getComment())){//显示是否通过的标示
				map.put("isPass", "yes");
			}else{
				map.put("isPass", "no");
			}
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", riskFromExcelList.size());
		
		return result;
	}
	
	/**
	 * 根据ID在风险临时表中查询风险数据
	 * @author 元杰
	 * @param form
	 * @param result
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/dataimport/risk/findRiskFromExcelById.f")
	public Map<String, Object> findRiskFromExcelById(String riskFromExcelId){
		List<RiskFromExcel> riskFromExcelList = o_dataImportBO.findRiskFromExcelById(riskFromExcelId);
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> formMap = new HashMap<String, Object>();
		if(riskFromExcelList != null && riskFromExcelList.size() > 0){
			RiskFromExcel risk = riskFromExcelList.get(0);
			formMap.put("id", risk.getId());//id
//			formMap.put("companyId", risk.getCompany().getId());//companyId
			formMap.put("code", risk.getCode());//riskCode
			formMap.put("name", risk.getName());//riskName
			formMap.put("desc", risk.getDesc());//edesc
			formMap.put("parentCode", risk.getParentCode());//parentCode
			formMap.put("parentName", risk.getParentName());//parentName
//			formMap.put("idSeq", risk.);//idSeq
			formMap.put("esort", risk.getEsort());//esort
//			formMap.put("isLeaf", risk.getIsLeaf);//isLeaf
			formMap.put("isInherit", risk.getIsInherit());//isInherit
			formMap.put("probability", risk.getProbability());//probability
			formMap.put("impact", risk.getImpact());//impact
			formMap.put("urgency", risk.getUrgency());//urgency
			formMap.put("riskLevel", risk.getRiskLevel());//riskLevel
			formMap.put("riskStatus", risk.getRiskStatus());//riskStatus
			formMap.put("riskStatus", risk.getRiskStatus());//riskStatus
			formMap.put("SCHM", risk.getSCHM());//SCHM
			if (risk.getCreateOrg()!=null)
			{
				formMap.put("createOrg", risk.getCreateOrg().getNameSeq());//createOrg
			}
			JSONArray orgIds = null;
			JSONObject object = null;
			if(StringUtils.isNotBlank(risk.getRespOrgs())){
				String[] respOrgIds = risk.getRespOrgs().split(",");
				orgIds = new JSONArray();
				object = new JSONObject();
				for(String respOrgId : respOrgIds){
					object.put("id", respOrgId);
					orgIds.add(object);
				}
				formMap.put("respOrgs", orgIds.toString());//respOrgs
			}
			if(null != risk.getRelaOrgs() && StringUtils.isNotBlank(risk.getRelaOrgs())){
				String[] relaOrgIds = risk.getRelaOrgs().split(",");
				orgIds = new JSONArray();
				object = new JSONObject();
				for(String relaOrgId : relaOrgIds){
					object.put("id", relaOrgId);
					orgIds.add(object);
				}
				formMap.put("relaOrgs", orgIds.toString());//relaOrgs
			}
			
			if(null != risk.getImpactTarget()){
				String[] impactTargetIds = risk.getImpactTarget().split(","); 
				StringBuilder sb = new StringBuilder();
				for(String targetId : impactTargetIds){
					Kpi kpi = o_kpiBO.findKpiById(targetId);
					if(null != kpi){
						sb.append(kpi.getId() + ",");
						
					}
				}
				formMap.put("impactTarget", sb.toString());//impactTarget
			}
			
			formMap.put("impactProcess", risk.getImpactProcess());//impactProcess
			formMap.put("assessmentTemplate", risk.getAssessmentTemplate());//assessmentTemplate
		}
		
		result.put("data", formMap);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 更新风险数据
	 * @author 元杰
	 * @param form
	 * @param result
	 * @throws IOException 
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping("/dataimport/risk/updateRiskFromExcel.f")
	public void updateRiskFromExcel(RiskFromExcel form, HttpServletResponse response) throws IOException {
		String flag = "false";
		try{
			RiskFromExcel risk = o_riskFromExcelDAO.get(form.getId());
			
			risk.setCode(form.getCode());
			risk.setName(form.getName());
			risk.setParentCode(form.getParentCode());
			risk.setParentName(form.getParentName());
			risk.setDesc(form.getDesc());
			String respOrgIds = IcmStandardUtils.findIdbyJason(form.getRespOrgs(), "id");
			String relaOrgIds = IcmStandardUtils.findIdbyJason(form.getRelaOrgs(), "id");
			risk.setRespOrgs(respOrgIds);
			risk.setRelaOrgs(relaOrgIds);
			risk.setImpactTarget(form.getImpactTarget());
			risk.setImpactProcess(form.getImpactProcess());
			risk.setIsInherit(form.getIsInherit());
			risk.setAssessmentTemplate(form.getAssessmentTemplate());
			risk.setProbability(form.getProbability());
			risk.setImpact(form.getImpact());
			risk.setUrgency(form.getUrgency());
			risk.setRiskLevel(form.getRiskLevel());
			risk.setRiskStatus(form.getRiskStatus());
			
			o_dataImportBO.saveRiskFromExcel(risk);
			flag = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().print(flag);
		response.getWriter().close();
	}
	
	/**
	 * 将风险临时表数据写入真实风险表中
	 * @author 元杰
	 * @param form
	 * @param result
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/dataimport/risk/importRiskToBD.f")
	public Map<String,Object> importRiskToBD(){
		Map<String, Object> data = new HashMap<String, Object>();
		String flag = "false";
		try{
			o_dataImportBO.importRiskFromExcelToBD();
			flag = "true";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data.put("success", flag);
		}
		
		if(flag.equalsIgnoreCase("true")){
			String sql = "delete from tmp_risks_exceldata";
			SQLQuery sqlQuery = o_riskFromExcelDAO.createSQLQuery(sql);
			sqlQuery.executeUpdate();
		}
		
		return data;
	}
	
	/**
	 * 验证风险临时信息，将错误信息记录
	 * @author 元杰
	 * @param form
	 * @param result
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/dataimport/risk/validateRiskFromExcelData.f")
	public Map<String,Object> validateRiskFromExcelData() {
		Map<String, Object> data = new HashMap<String, Object>();
		String flag = "false";
		try{
			o_dataImportBO.validateRiskData();
			flag = "true";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data.put("success", flag);
		}
		return data;
	}
	
	/**
	 * 根据风险code返回风险名称
	 * @author 元杰
	 * @param form
	 * @param result
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/dataimport/risk/returnRiskNameByCode.f")
	public Map<String,Object> returnRiskNameByCode(String code) {
		Map<String, Object> data = new HashMap<String, Object>();
		String flag = "false";
		try{
			List<RiskFromExcel> riskFromExcelList = o_dataImportBO.findRiskFromExcelByCode(code);
			if(riskFromExcelList != null && riskFromExcelList.size() > 0){
				data.put("parentName", riskFromExcelList.get(0).getName());
			}else{
				data.put("parentName", "");
			}
			flag = "true";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data.put("success", flag);
		}
		return data;
	}
}
