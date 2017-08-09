package com.fhd.icm.web.controller.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessRiskBO;
import com.fhd.icm.web.form.process.RiskMeasureForm;
import com.fhd.ra.web.controller.risk.RiskCmpControl;
import com.fhd.ra.web.controller.risk.define.DefineRiskControl;
import com.fhd.sys.web.form.dic.DictEntryForm;

/**
 * 流程节点维护
 * 
 * @author 宋佳
 * @version
 * @since Ver 1.1
 * @Date 2013 3-13
 */
@Controller
public class ProcessRiskControl {

	@Autowired
	private ProcessRiskBO o_processRiskBO;
	@Autowired
	private DefineRiskControl o_defineRiskControl;
	@Autowired
	private RiskCmpControl o_riskCmpControl;

	/**
	 * <pre>
	 * 加载流程节点表单
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointEditID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/loadriskeditformdata.f")
	public Map<String, Object> loadRiskEditFormData(String processRiskId,String processId) {
		return o_riskCmpControl.findRiskEditInfoByRiskId(processRiskId);
	}
	/**
	 * <pre>
	 * 初始化控制措施表单（新建的时候才调用）
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointEditID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/initmeasureformdata.f")
	public Map<String, Object> initMeasureFormData(String processId) {
		return o_processRiskBO.initMeasureFormData(processId);
	}
	/**
	 * <pre>
	 *     加载控制矩阵
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointEditID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/loadmeasureeditformdata.f")
	public Map<String, Object> loadMeasureEditFormData(String measureId) {
		Map<String, Object> processRiskMap = o_processRiskBO
				.loadMeasureEditFormData(measureId);
		return processRiskMap;
		
	}
	/**
	 * <pre>
	 *     加载控制矩阵
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointEditID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/loadmeasureeditformdataforview.f")
	public Map<String, Object> loadMeasureEditFormDataForView(String measureId) {
		Map<String, Object> processRiskMap = o_processRiskBO
				.loadMeasureEditFormDataForView(measureId);
		return processRiskMap;
		
	}

	/**
	 * <pre>
	 * 根据风险id获取控制id
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointEditID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/findmeasureidbyriskid.f")
	public Map<String, Object> findMeasureIdbyRiskId(String processId,
			String processRiskId) {
		Map<String, Object> processRiskMap = o_processRiskBO
				.findMeasureIdbyRiskId(processId, processRiskId);
		return processRiskMap;

	}

	/**
	 * <pre>
	 * 保存流程节点
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointForm
	 * @param parentId
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/saveriskmeasure.f")
	public Map<String, Object> saveRiskMeasure(RiskMeasureForm riskForm) {
		Map<String, Object> map = null;
		if(StringUtils.isEmpty(riskForm.getProcessRiskId())){
			map = o_defineRiskControl.saveRiskInfo(riskForm, null, null);
		}else{
			map = o_defineRiskControl.mergeRiskInfo(riskForm, riskForm.getProcessRiskId(), null);
		}
		String riskId = (String) map.get("id");
		riskForm.setId(riskId);
		return o_processRiskBO.saveRiskMeasure(riskForm);
	}
	/**
	 * <pre>
	 *  根据控制措施ids 删除控制措施
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointForm
	 * @param parentId
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/removemeasurebyids.f")
	public Map<String, Object> removemeasureidbyids(String measureIds) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(measureIds)){
			o_processRiskBO.removeMeasureByIds(measureIds);
		}
		result.put("success", true);
		return result;
	}

	/**
	 * <pre>
	 * 自动生成编号
	 * </pre>
	 * 
	 * @author 李克东
	 * @param ProcessPointEditID
	 * @param parentId
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/ProcessPointCode.f")
	public Map<String, Object> findProcessPointCode(String processPointId, String processId) {
		return o_processRiskBO.findProcessPointCode(processPointId, processId);
	}

	/**
	 * 根据流程ID 找到流程下所有节点
	 * 
	 * @author 宋佳
	 * @param processId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/processrisk/findProcessRiskListByPage.f")
	public Map<String, Object> findProcessRiskListByPage(int limit, int start,
			String query, String processId) {

		Map<String, Object> resultMap;
		Page<ProcessRelaRisk> page = new Page<ProcessRelaRisk>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		String companyId = UserContext.getUser().getCompanyid();
		resultMap = o_processRiskBO.findProcessRiskPageBySome(page, query,
				processId, companyId);
		return resultMap;

	}
	/**
	 * <pre>
	 *  删除风险
	 * </pre>
	 * 
	 * @author  宋佳
	 * @param ProcessPointID
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/processrisk/removeprocessrisk.f")
	public Map<String,Object> removeProcessRiskById(String riskId) throws IOException{
		if(StringUtils.isNotBlank(riskId)){
			o_processRiskBO.removeProcessRiskById(riskId);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("data", "sucess");
		return result;
	}
	/**
	 * <pre>
	 *  删除风险流程关联关系
	 * </pre>
	 * 
	 * @author  宋佳
	 * @param ProcessPointID
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/removeprocessrelariskbyid.f")
	public Map<String,Object> removeProcessRelaRiskById(String riskId) throws IOException{
		if(StringUtils.isNotBlank(riskId)){
			o_processRiskBO.removeProcessRelaRiskById(riskId);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("data", "sucess");
		return result;
	}
	/**
	 * 查询出流程中所包含所有的节点，作为数据字典展示
	 * 
	 * @param typeId
	 * @author 宋佳
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/processrisk/findAllProcessPointByProcessId.f")
	public List<Map<String, String>> findAllProcessPointByProcessId(String processId) {
		List<DictEntryForm> list = o_processRiskBO.findAllProcessPointByProcessId(processId);
		List<Map<String, String>> pointList = new ArrayList<Map<String, String>>();
		for (DictEntryForm dictEntryForm : list) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", dictEntryForm.getId());
			map.put("name", dictEntryForm.getName());
			pointList.add(map);
		}
		return pointList;
	}
}
