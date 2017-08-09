package com.fhd.sm.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sm.business.DataSmImportBO;
import com.fhd.sys.web.form.file.FileForm;

/**战略目标导入control
 * @author xiaozhe
 *
 */
@Controller
public class DataSmImportControl {
	/**
	 * log对象
	 */
	private static Log logger = LogFactory.getLog(DataSmImportControl.class);
	
	@Autowired
	private DataSmImportBO o_dataSmImportBO;
	
	
	/**查询所有校验不通过的目标信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sm/import/findinvalidatesmdata.f")
	public Map<String, Object> findInvaliDateSmData(){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = o_dataSmImportBO.findAllInValidateSmTmpList();
		map.put("datas", datas);
		return map;
	}
	
	/**上传战略目标数据导入临时表
	 * @param form 文件form
	 * @param response http响应对象
	 * @throws IOException
	 */
	
	@ResponseBody
	@RequestMapping("/sm/import/uploadsmdatatotmp.f")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void uploadSmDataToTmp(FileForm form,Boolean isCoverage, HttpServletResponse response) throws IOException{
		Boolean result = true;
		Boolean uploadResult = true;
		try {
			int smSheetNo = 0;//战略目标sheet位置.
			int smRelaKpiSheetNo = 4;//战略目标关联的指标sheet位置.
			//读取战略目标数据,第1个sheet页
			List<List<String>> smDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), smSheetNo);
			//读取目标关联的指标数据,第5个sheet页
			List<List<String>> smRelaKpiDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), smRelaKpiSheetNo);
			//导入数据到临时表
			uploadResult = o_dataSmImportBO.importSmDataToTmp(smDatas, smRelaKpiDatas,isCoverage);
			
		} catch (Exception e) {
			result = false;
			uploadResult = false;
			e.printStackTrace();
			logger.error("读取文件失败!-----exception:"+e.toString());
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", result);
		resultMap.put("uploadResult", uploadResult);
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(resultMap));
	}
	
	/**将excel中的目标采集数据导入到数据库
	 * @param form 文件form
	 * @param response http响应对象
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("/sm/import/uploadsmresultdatatodb.f") 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void uploadSmResultDatatoDb(FileForm form, HttpServletResponse response) throws IOException {
		Boolean result = true;
		boolean uploadResult = true;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			int smGatherDataSheetNo = 6;//战略目标采集数据sheet位置.
			List<List<String>> excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(),smGatherDataSheetNo);
			uploadResult = o_dataSmImportBO.importSmGatherDataToDB(excelDatas);
		} catch (Exception e) {
			result = false;
			uploadResult = false;
			logger.error("读取文件失败!-----exception:"+e.toString());
		}
		resultMap.put("success", result);
		resultMap.put("uploadResult", uploadResult);
		response.getWriter().print(JSONSerializer.toJSON(resultMap));
		response.getWriter().close();
	}	
	
	/**将excel中的目标采集数据导入到数据库
     * @param form 文件form
     * @param response http响应对象
     * @throws IOException
     */
	@ResponseBody
	@RequestMapping("/sm/import/uploadsmresultdatatodbs.f") 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void uploadSmResultDatatoDbs(FileForm form, HttpServletResponse response) throws IOException {
	    Boolean result = true;
	    boolean uploadResult = true;
	    JSONObject resultObj = null;
	    Map<String, Object> resultMap = new HashMap<String, Object>();
	    try{
	        int smGatherDataSheetNo = 6;//战略目标采集数据sheet位置.
	        List<List<String>> excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(),smGatherDataSheetNo);
	        resultObj = o_dataSmImportBO.importSmGatherDataToDBS(excelDatas);
	        uploadResult = resultObj.getBoolean("result");
	    } catch (Exception e) {
	        result = false;
	        uploadResult = false;
	        logger.error("读取文件失败!-----exception:"+e.toString());
	    }
	    resultMap.put("success", result);
	    resultMap.put("uploadResult", uploadResult);
	    resultMap.put("errors", resultObj.getJSONArray("errors"));
	    response.getWriter().print(JSONSerializer.toJSON(resultMap));
	    response.getWriter().close();
	}	
	
	
	/**
	 * 将目标临时表中的数据导入实际的目标表
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping("/sm/import/confirmimportsmdata.f")
	public void confirmImportSmData(Boolean isCoverage,HttpServletResponse response) throws IOException{
		Boolean result = true;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			o_dataSmImportBO.confirmImportSmData(isCoverage);
		} catch (Exception e) {
			logger.error("导入失败:exception:"+e.toString());
			result = false;
		}
		resultMap.put("success", result);
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(resultMap));
	}

}
