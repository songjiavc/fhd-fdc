package com.fhd.ra.web.controller.risk.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.ra.business.risk.graph.RiskGraphDrawBO;

@Controller
public class RiskGraphDrawControl {
	@Autowired
	private RiskGraphDrawBO o_riskGraphDrawBO;
	
	/**
	 * 查询风险对象的状态
	 * id 以逗号分隔，表示多个id
	 * @author zhengjunxiang
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "risk/graph/showstatus.f")
	public Map<String, Object> showRiskStatus(String id) throws Exception {
		Map<String, Object> map = o_riskGraphDrawBO.showStatus(id);
		
		if(null == map){
		    map = new HashMap<String, Object>();
		    map.put("sucess", false);
		}else{
		    map.put("sucess", true);
		}
		
		return map;
	}
	
	/**
	 * 查询风险对象的状态
	 * @param riskIds  以逗号分隔，表示多个id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "risk/graph/showstatusbatch.f")
	public Map<String, Object> showRiskStatusBatch(String riskIds) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isNotBlank(riskIds)){
			String[] riskIdArray = riskIds.split(",");
			List<String> riskIdList = Arrays.asList(riskIdArray);
			resultList = o_riskGraphDrawBO.showStatus(riskIdList);
			map.put("datas", resultList);
			map.put("sucess", true);
		}
		return map;
	}
}
