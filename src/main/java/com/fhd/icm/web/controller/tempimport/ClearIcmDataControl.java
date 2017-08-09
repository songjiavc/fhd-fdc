package com.fhd.icm.web.controller.tempimport;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.icm.business.tempimport.ClearIcmDataBO;

/**
 * 清理流程数据Control.
 * @author 吴德福
 * @Date 2013-1-5 14:10:32
 */
@Controller
public class ClearIcmDataControl {
	
	@Autowired
	private ClearIcmDataBO o_clearIcmDataBO;

	@ResponseBody
	@RequestMapping(value = "/icm/process/init/removeProcessData.f")
	public Map<String, Object> removeProcessData(String companyId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			o_clearIcmDataBO.removeProcessData(companyId);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
		}
		return map;
	}
}