/**
 * AssessPlanBO.java
 * com.fhd.icm.business.assess
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-6 		刘中帅
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
 */

package com.fhd.ra.web.controller.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.icm.web.form.process.MeasureForm;
import com.fhd.ra.business.response.ResponseMeasureBO;


/**
 * 
 */
@Controller
@SuppressWarnings("deprecation")
public class ResponseMeasureControl{
	@Autowired
	private ResponseMeasureBO o_responseMeasureBO;
	
	/**
	 * <pre>
	 * 保存控制措施
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param MeasureForm
	 * @return Map
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/savemeasure.f")
	public Map<String, Object> saveMeasure(MeasureForm measureForm) {
		Map<String, Object> map = new HashMap<String,Object>();
		Boolean returnFlag = o_responseMeasureBO.saveMeasureForm(measureForm);
		map.put("success", returnFlag);
		return map;
	}
	/**
	 * <pre>
	 *     应多中控制措施的load
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointEditID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/processrisk/loadmeasureedititemformdata.f")
	public Map<String, Object> loadMeasureEditItemFormData(String measureId) {
		Map<String, Object> processRiskMap = o_responseMeasureBO.loadMeasureEditItemFormData(measureId);
		return processRiskMap;
		
	}
	/**
	 * <pre>
	 *   加载风险应对控制措施
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param ProcessPointEditID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping("/response/loadmeasureedititemformdataforview.f")
	public Map<String, Object> loadMeasureEditFormDataForView(String measureId) {
		Map<String, Object> processRiskMap = o_responseMeasureBO.loadMeasureEditItemFormDataForView(measureId);
		return processRiskMap;
		
	}
}
