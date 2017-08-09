package com.fhd.ra.web.controller.risk;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fhd.ra.business.risk.ClearRiskDataBO;

@Controller
public class ClearRiskDataControl {
	
	@Autowired
	private ClearRiskDataBO o_clearRiskDataBO;
	
	/**
	 * 清空风险基础数据
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "risk/removeRiskData.f")
	public Map<String, Object> removeRiskData(String companyId) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		o_clearRiskDataBO.removeRiskData(companyId);
		resultMap.put("success", true);
		return resultMap;
	}
	
	/**
	 * 清空风险评估数据
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "risk/removeRiskAssessData.f")
	public Map<String, Object> removeRiskAssessData(String companyId) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		o_clearRiskDataBO.removeRiskAssessData(companyId);
		resultMap.put("success", true);
		return resultMap;
	}
	
}
