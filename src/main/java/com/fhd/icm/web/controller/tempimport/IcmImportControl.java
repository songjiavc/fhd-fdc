package com.fhd.icm.web.controller.tempimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fhd.entity.icm.tempimport.TempControlMeasure;
import com.fhd.entity.icm.tempimport.TempControlStandard;
import com.fhd.entity.icm.tempimport.TempPracticeTestAssessPoint;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.icm.tempimport.TempProcessPoint;
import com.fhd.entity.icm.tempimport.TempProcessPointRelation;
import com.fhd.entity.icm.tempimport.TempProcessStandardRiskRelation;
import com.fhd.entity.icm.tempimport.TempRiskProcessPointMeasureRelation;
import com.fhd.entity.icm.tempimport.TempSamplingTestAssessPoint;
import com.fhd.icm.business.tempimport.IcmImportBO;
import com.fhd.icm.business.tempimport.TempControlMeasureBO;
import com.fhd.icm.business.tempimport.TempControlStandardBO;
import com.fhd.icm.business.tempimport.TempPracticeTestAssessPointBO;
import com.fhd.icm.business.tempimport.TempProcessBO;
import com.fhd.icm.business.tempimport.TempProcessPointBO;
import com.fhd.icm.business.tempimport.TempProcessPointRelationBO;
import com.fhd.icm.business.tempimport.TempProcessStandardRiskRelationBO;
import com.fhd.icm.business.tempimport.TempRiskProcessPointMeasureRelationBO;
import com.fhd.icm.business.tempimport.TempSamplingTestAssessPointBO;
import com.fhd.sys.web.form.file.FileForm;

/**
 * 流程导入临时表Control.
 * @author 吴德福
 * @Date 2013-11-26 14:10:32
 */
@Controller
public class IcmImportControl {
	
	@Autowired
	private IcmImportBO o_icmImportBO;
	@Autowired
	private TempProcessBO o_tempProcessBO;
	@Autowired
	private TempProcessPointBO o_tempProcessPointBO;
	@Autowired
	private TempProcessPointRelationBO o_tempProcessPointRelationBO;
	@Autowired
	private TempControlStandardBO o_tempControlStandardBO;
	@Autowired
	private TempProcessStandardRiskRelationBO o_tempProcessStandardRiskRelationBO;
	@Autowired
	private TempControlMeasureBO o_tempControlMeasureBO;
	@Autowired
	private TempRiskProcessPointMeasureRelationBO o_tempRiskProcessPointMeasureRelationBO;
	@Autowired
	private TempPracticeTestAssessPointBO o_tempPracticeTestAssessPointBO;
	@Autowired
	private TempSamplingTestAssessPointBO o_tempSamplingTestAssessPointBO;
	
	/**
	 * 读取excel文件数据存入临时表并验证数据.
	 * @param fileId
	 * @param type 数据类型
	 * @param importStyle 导入方式
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/saveTempProcessByExcel.f")
	public Map<String,Object> saveTempProcessByExcel(FileForm form, String type, String importStyle) throws Exception {
		Map<String,Object> map = new TreeMap<String,Object>();
		//读取excel文件数据存入临时表
		o_icmImportBO.saveTempProcessByExcel(form.getFile().getBytes(), type, importStyle);//内控之前是使用上传控件来保存excel，现在直接浏览上传

		//验证数据.
		String fileId = null;
		Map<String, Object> validateDataMap = o_icmImportBO.validateData(fileId, type);
		
		map.put("countMap", validateDataMap);
		//错误总数为0，前台数据导入按钮可用
		map.put("errorAllCount", validateDataMap.get("errorAllCount"));
		//正确总数为0，前台数据导入按钮不可用
		map.put("correctAllCount", validateDataMap.get("correctAllCount"));
		
		map.put("success", true);
		return map;
	}
	/**
	 * 导入数据.
	 * @param fileId
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/importData.f")
	public Map<String,Object> importData(String fileId, String type) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			o_icmImportBO.importData(fileId, type);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 流程预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findProcessPreviewListBySome.f")
	public Map<String,Object> findProcessPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> processList = new ArrayList<Map<String,Object>>();
		
		List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome(query, fileId);
		for (TempProcess tempProcess : tempProcessList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempProcess.getId());
			row.put("eindex", tempProcess.getIndex());
			row.put("parentProcessureCode", tempProcess.getParentProcessureCode());
			row.put("parentProcessureName", tempProcess.getParentProcessureName());
			row.put("processureCode", tempProcess.getProcessureCode());
			row.put("processureName", tempProcess.getProcessureName());
			row.put("responsibleOrg", tempProcess.getResponsibleOrg());
			row.put("relateOrg", tempProcess.getRelateOrg());
			row.put("frequency", tempProcess.getFrequency());
			row.put("affectSubjects", tempProcess.getAffectSubjects());
			row.put("responsibleEmp", tempProcess.getResponsibleEmp());
			if(StringUtils.isNotBlank(tempProcess.getErrorTip())){
				row.put("errorTip", tempProcess.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			processList.add(row);
		}
		
		map.put("datas", processList);
		map.put("totalConut", processList.size());
		
		return map;
	}
	/**
	 * 流程节点预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findProcessPointPreviewListBySome.f")
	public Map<String,Object> findProcessPointPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> processPointList = new ArrayList<Map<String,Object>>();
		
		List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome(query, fileId);
		for (TempProcessPoint tempProcessPoint : tempProcessPointList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempProcessPoint.getId());
			row.put("eindex", tempProcessPoint.getIndex());
			row.put("processCode", tempProcessPoint.getProcessCode());
			row.put("processName", tempProcessPoint.getProcessName());
			row.put("processPointCode", tempProcessPoint.getProcessPointCode());
			row.put("processPointName", tempProcessPoint.getProcessPointName());
			row.put("type", tempProcessPoint.getType());
			row.put("processPointDesc", tempProcessPoint.getProcessPointDesc());
			row.put("responsibleOrg", tempProcessPoint.getResponsibleOrg());
			row.put("responsibleEmp", tempProcessPoint.getResponsibleEmp());
			row.put("inputInfo", tempProcessPoint.getInputInfo());
			row.put("outputInfo", tempProcessPoint.getOutputInfo());
			if(StringUtils.isNotBlank(tempProcessPoint.getErrorTip())){
				row.put("errorTip", tempProcessPoint.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			processPointList.add(row);
		}
		
		map.put("datas", processPointList);
		map.put("totalConut", processPointList.size());
		
		return map;
	}
	/**
	 * 流程节点关系预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findProcessPointRelationPreviewListBySome.f")
	public Map<String,Object> findProcessPointRelationPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> processPointRelationList = new ArrayList<Map<String,Object>>();
		
		List<TempProcessPointRelation> tempProcessPointRelationList = o_tempProcessPointRelationBO.findProcessPointRelationPreviewListBySome(query, fileId);
		for (TempProcessPointRelation tempProcessPointRelation : tempProcessPointRelationList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempProcessPointRelation.getId());
			row.put("eindex", tempProcessPointRelation.getIndex());
			row.put("processCode", tempProcessPointRelation.getProcessCode());
			row.put("processName", tempProcessPointRelation.getProcessName());
			row.put("processPointCode", tempProcessPointRelation.getProcessPointCode());
			row.put("processPointName", tempProcessPointRelation.getProcessPointName());
			row.put("parentProcessPointCode", tempProcessPointRelation.getParentProcessPointCode());
			row.put("parentProcessPointName", tempProcessPointRelation.getParentProcessPointName());
			row.put("enterCondition", tempProcessPointRelation.getEnterCondition());
			if(StringUtils.isNotBlank(tempProcessPointRelation.getErrorTip())){
				row.put("errorTip", tempProcessPointRelation.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			processPointRelationList.add(row);
		}
		
		map.put("datas", processPointRelationList);
		map.put("totalConut", processPointRelationList.size());
		
		return map;
	}
	/**
	 * 控制标准(要求)预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findControlStandardPreviewListBySome.f")
	public Map<String,Object> findControlStandardPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> processList = new ArrayList<Map<String,Object>>();
		
		List<TempControlStandard> tempControlStandardList = o_tempControlStandardBO.findControlStandardPreviewListBySome(query, fileId);
		for (TempControlStandard tempControlStandard : tempControlStandardList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempControlStandard.getId());
			row.put("eindex", tempControlStandard.getIndex());
			row.put("parentControlStandardCode", tempControlStandard.getParentControlStandardCode());
			row.put("parentControlStandardName", tempControlStandard.getParentControlStandardName());
			row.put("controlStandardCode", tempControlStandard.getControlStandardCode());
			row.put("controlStandardName", tempControlStandard.getControlStandardName());
			row.put("responsibleOrg", tempControlStandard.getResponsibleOrg());
			row.put("controlLevel", tempControlStandard.getControlLevel());
			row.put("controlElements", tempControlStandard.getControlElements());
			row.put("isClass", tempControlStandard.getIsClass());
			if(StringUtils.isNotBlank(tempControlStandard.getErrorTip())){
				row.put("errorTip", tempControlStandard.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			processList.add(row);
		}
		
		map.put("datas", processList);
		map.put("totalConut", processList.size());
		
		return map;
	}
	/**
	 * 流程--控制标准(要求)--风险关系预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findProcessStandardRiskRelationPreviewListBySome.f")
	public Map<String,Object> findProcessStandardRiskRelationPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> processPointRelationList = new ArrayList<Map<String,Object>>();
		
		List<TempProcessStandardRiskRelation> tempProcessStandardRiskRelationList = o_tempProcessStandardRiskRelationBO.findProcessStandardRiskRelationPreviewListBySome(query, fileId);
		for (TempProcessStandardRiskRelation tempProcessStandardRiskRelation : tempProcessStandardRiskRelationList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempProcessStandardRiskRelation.getId());
			row.put("eindex", tempProcessStandardRiskRelation.getIndex());
			row.put("processCode", tempProcessStandardRiskRelation.getProcessCode());
			row.put("processName", tempProcessStandardRiskRelation.getProcessName());
			row.put("controlStandardCode", tempProcessStandardRiskRelation.getControlStandardCode());
			row.put("controlStandardName", tempProcessStandardRiskRelation.getControlStandardName());
			row.put("riskCode", tempProcessStandardRiskRelation.getRiskCode());
			row.put("riskName", tempProcessStandardRiskRelation.getRiskName());
			if(StringUtils.isNotBlank(tempProcessStandardRiskRelation.getErrorTip())){
				row.put("errorTip", tempProcessStandardRiskRelation.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			processPointRelationList.add(row);
		}
		
		map.put("datas", processPointRelationList);
		map.put("totalConut", processPointRelationList.size());
		
		return map;
	}
	/**
	 * 控制措施预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findControlMeasurePreviewListBySome.f")
	public Map<String,Object> findControlMeasurePreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> controlMeasureList = new ArrayList<Map<String,Object>>();
		
		List<TempControlMeasure> tempControlMeasureList = o_tempControlMeasureBO.findControlMeasurePreviewListBySome(query, fileId);
		for (TempControlMeasure tempControlMeasure : tempControlMeasureList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempControlMeasure.getId());
			row.put("eindex", tempControlMeasure.getIndex());
			row.put("controlMeasureCode", tempControlMeasure.getControlMeasureCode());
			row.put("controlMeasureName", tempControlMeasure.getControlMeasureName());
			row.put("responsibleOrg", tempControlMeasure.getResponsibleOrg());
			row.put("responsibleEmp", tempControlMeasure.getResponsibleEmp());
			row.put("isKeyControlPoint", tempControlMeasure.getIsKeyControlPoint());
			row.put("controlTarget", tempControlMeasure.getControlTarget());
			row.put("implementProof", tempControlMeasure.getImplementProof());
			row.put("controlMode", tempControlMeasure.getControlMode());
			row.put("controlFrequency", tempControlMeasure.getControlFrequency());
			if(StringUtils.isNotBlank(tempControlMeasure.getErrorTip())){
				row.put("errorTip", tempControlMeasure.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			controlMeasureList.add(row);
		}
		
		map.put("datas", controlMeasureList);
		map.put("totalConut", controlMeasureList.size());
		
		return map;
	}
	/**
	 * 风险--流程--流程节点--控制措施关系预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findRiskProcessPointMeasureRelationPreviewListBySome.f")
	public Map<String,Object> findRiskProcessPointMeasureRelationPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> riskProcessPointMeasureRelationList = new ArrayList<Map<String,Object>>();
		
		List<TempRiskProcessPointMeasureRelation> tempRiskProcessPointMeasureRelationList = o_tempRiskProcessPointMeasureRelationBO.findRiskProcessPointMeasureRelationPreviewListBySome("", fileId);
		for (TempRiskProcessPointMeasureRelation tempRiskProcessPointMeasureRelation : tempRiskProcessPointMeasureRelationList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempRiskProcessPointMeasureRelation.getId());
			row.put("eindex", tempRiskProcessPointMeasureRelation.getIndex());
			row.put("riskCode", tempRiskProcessPointMeasureRelation.getRiskCode());
			row.put("riskName", tempRiskProcessPointMeasureRelation.getRiskName());
			row.put("processCode", tempRiskProcessPointMeasureRelation.getProcessCode());
			row.put("processName", tempRiskProcessPointMeasureRelation.getProcessName());
			row.put("processPointCode", tempRiskProcessPointMeasureRelation.getProcessPointCode());
			row.put("processPointName", tempRiskProcessPointMeasureRelation.getProcessPointName());
			row.put("controlMeasureCode", tempRiskProcessPointMeasureRelation.getControlMeasureCode());
			row.put("controlMeasureName", tempRiskProcessPointMeasureRelation.getControlMeasureName());
			if(StringUtils.isNotBlank(tempRiskProcessPointMeasureRelation.getErrorTip())){
				row.put("errorTip", tempRiskProcessPointMeasureRelation.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			riskProcessPointMeasureRelationList.add(row);
		}
		
		map.put("datas", riskProcessPointMeasureRelationList);
		map.put("totalConut", riskProcessPointMeasureRelationList.size());
		
		return map;
	}
	/**
	 * 穿行测试评价点预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findPracticeTestAssessPointPreviewListBySome.f")
	public Map<String,Object> findPracticeTestAssessPointPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> practiceTestAssessPointList = new ArrayList<Map<String,Object>>();
		
		List<TempPracticeTestAssessPoint> tempPracticeTestAssessPointList = o_tempPracticeTestAssessPointBO.findPracticeTestAssessPointPreviewListBySome(query, fileId);
		for (TempPracticeTestAssessPoint tempPracticeTestAssessPoint : tempPracticeTestAssessPointList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempPracticeTestAssessPoint.getId());
			row.put("eindex", tempPracticeTestAssessPoint.getIndex());
			row.put("processCode", tempPracticeTestAssessPoint.getProcessCode());
			row.put("processName", tempPracticeTestAssessPoint.getProcessName());
			row.put("processPointCode", tempPracticeTestAssessPoint.getProcessPointCode());
			row.put("processPointName", tempPracticeTestAssessPoint.getProcessPointName());
			row.put("assessPointCode", tempPracticeTestAssessPoint.getAssessPointCode());
			row.put("assessPointName", tempPracticeTestAssessPoint.getAssessPointName());
			row.put("affectSubjects", tempPracticeTestAssessPoint.getAffectSubjects());
			row.put("implementProof", tempPracticeTestAssessPoint.getImplementProof());
			if(StringUtils.isNotBlank(tempPracticeTestAssessPoint.getErrorTip())){
				row.put("errorTip", tempPracticeTestAssessPoint.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			practiceTestAssessPointList.add(row);
		}
		
		map.put("datas", practiceTestAssessPointList);
		map.put("totalConut", practiceTestAssessPointList.size());
		
		return map;
	}
	/**
	 * 抽样测试评价点预览数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/process/import/findSamplingTestAssessPointPreviewListBySome.f")
	public Map<String,Object> findSamplingTestAssessPointPreviewListBySome(String query, String fileId, String type) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> processPointList = new ArrayList<Map<String,Object>>();
		
		List<TempSamplingTestAssessPoint> tempSamplingTestAssessPointList = o_tempSamplingTestAssessPointBO.findSamplingTestAssessPointPreviewListBySome(query, fileId);
		for (TempSamplingTestAssessPoint tempSamplingTestAssessPoint : tempSamplingTestAssessPointList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", tempSamplingTestAssessPoint.getId());
			row.put("eindex", tempSamplingTestAssessPoint.getIndex());
			row.put("processCode", tempSamplingTestAssessPoint.getProcessCode());
			row.put("processName", tempSamplingTestAssessPoint.getProcessName());
			row.put("controlMeasureCode", tempSamplingTestAssessPoint.getControlMeasureCode());
			row.put("controlMeasureName", tempSamplingTestAssessPoint.getControlMeasureName());
			row.put("assessPointCode", tempSamplingTestAssessPoint.getAssessPointCode());
			row.put("assessPointName", tempSamplingTestAssessPoint.getAssessPointName());
			row.put("affectSubjects", tempSamplingTestAssessPoint.getAffectSubjects());
			row.put("implementProof", tempSamplingTestAssessPoint.getImplementProof());
			if(StringUtils.isNotBlank(tempSamplingTestAssessPoint.getErrorTip())){
				row.put("errorTip", tempSamplingTestAssessPoint.getErrorTip());
			}else{
				row.put("errorTip", "");
			}
			
			processPointList.add(row);
		}
		
		map.put("datas", processPointList);
		map.put("totalConut", processPointList.size());
		
		return map;
	}
}