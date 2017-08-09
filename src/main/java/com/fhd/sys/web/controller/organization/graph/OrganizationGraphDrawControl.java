/**
 * @author zhengjunxiang 2013-11-1
 * 给张雷提供的主对象风险状态灯的接口
 */
package com.fhd.sys.web.controller.organization.graph;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fhd.ra.business.risk.graph.OrganizationGraphDrawBO;

@Controller
public class OrganizationGraphDrawControl {
	@Autowired
	private OrganizationGraphDrawBO o_organizationGraphDrawBO;
	
	/**
	 * 查询风险对象的状态
	 * id 以逗号分隔，表示多个id
	 * @author zhengjunxiang
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "organization/graph/showstatus.f")
	public Map<String, Object> showRiskStatus(String id) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map = o_organizationGraphDrawBO.showStatus(id);
		
		if(null != map){
			map.put("sucess", true);
		}else{
			map = new HashMap<String, Object>();
			map.put("sucess", false);
		}

		return map;
	}
}
