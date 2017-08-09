/**
 * @author zhengjunxiang 2013-11-1
 * 给张雷提供的主对象风险状态灯的接口
 */
package com.fhd.icm.web.controller.process.graph;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.ra.business.risk.graph.ProcessGraphDrawBO;

@Controller
public class ProcessGraphDrawControl {
	@Autowired
	private ProcessGraphDrawBO o_processGraphDrawBO;
	
	/**
	 * 查询风险对象的状态
	 * id 以逗号分隔，表示多个id
	 * @author zhengjunxiang
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "process/graph/showstatus.f")
	public Map<String, Object> showRiskStatus(String id) {
		Map<String, Object> map = o_processGraphDrawBO.showStatus(id);
		map.put("sucess", true);
		return map;
	}
}
