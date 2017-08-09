/**
 * 
 */
package com.fhd.sm.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sm.business.KpiDataImportBO;
import com.fhd.sys.web.form.file.FileForm;

/**
 * @author wangxin
 *
 */
@Controller
public class DatakpiGatherResultImport {
     
	@Autowired
	private KpiDataImportBO o_kpiDataImportBO;
	
	/**导入指标采集数据
	 * @param form fileform对象
	 * @param response http响应对象
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping("/kpi/kpi/uploadKpiResultToDb.f") 
	public void saveKpiGatherResultFromExcel(FileForm form, HttpServletResponse response) throws IOException {
		Boolean result = true;
		boolean uploadResult = true;
		JSONObject resultObj = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
				.readEspecialExcel(form.getFile().getInputStream(), 8);// 读取文件(第五页)
			resultObj = o_kpiDataImportBO.saveKpiGatherDatasFromExcel(excelDatas);
			uploadResult = resultObj.getBoolean("result");
		} catch (Exception e) {
			result = false;
			uploadResult = false;
			e.printStackTrace();
		}
		resultMap.put("success", result);
		resultMap.put("uploadResult", uploadResult);
		resultMap.put("errors", resultObj.getJSONArray("errors"));
		response.getWriter().print(JSONSerializer.toJSON(resultMap));
		response.getWriter().close();
	}
	
}
