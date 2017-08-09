package com.fhd.icm.web.controller.assess;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.icm.business.assess.AssessTemplateImportBO;

@Controller
public class AssessTemplateImportControl {
	@Autowired
	private AssessTemplateImportBO o_assessTemplateImportBO;
	/**
	 * 读取excel文件数据存入
	 * @param fileId excel文件ID
	 * @param type 数据类型
	 * @param importStyle 导入方式
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/assess/saveTempAssessTemplateByExcel.f")
	public Map<String,Object> saveTempAssessTemplateByExcel(String fileId, String uploadFiles) throws Exception {
		Map<String,Object> map = new TreeMap<String,Object>();
		//读取excel文件数据存入
		o_assessTemplateImportBO.saveTempAssessTemplateByExcel(fileId,uploadFiles);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 验证数据
	 * @param fileId excel文件ID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/icm/assess/validateData.f")
	public Map<String,Object> validateData(String fileId, String uploadFileNames) throws Exception {
		return o_assessTemplateImportBO.validateData(fileId,uploadFileNames);
	}
}
