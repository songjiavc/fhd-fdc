package com.fhd.sm.web.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sm.business.DataScImportBO;
import com.fhd.sm.business.DataSmImportBO;
import com.fhd.sm.business.KpiDataImportBO;
import com.fhd.sm.business.KpiTypeDataImportBO;
import com.fhd.sys.web.form.file.FileForm;

@Controller
public class DataKpiImportControl {
    
	@Autowired
	private KpiDataImportBO o_kpiDataImportBO;
	
	@Autowired
    private KpiTypeDataImportBO o_kpiTypeDataImportBO; 
	
	@Autowired
	private DataSmImportBO o_dataSmImportBO;
	
	@Autowired
	private DataScImportBO o_dataScImportBO;
	
	/**
     * log对象
     */
    private static Log log= LogFactory.getLog(DataKpiImportControl.class);
    
    
    
    /**导入监控预警所有数据(从临时表到真实表)
     * @param isCoverage 增量导入还是覆盖导入
     * @param response http响应对象
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/kpi/import/confirmimportalldata.f")
    public void confirmimportalldata(Boolean isCoverage,HttpServletResponse response) throws IOException{
        Boolean result = true;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            //指标类型
            o_kpiTypeDataImportBO.mergeKpiTmpKpiType(isCoverage);
            //指标
            o_kpiDataImportBO.mergeKpiTmpKpi(isCoverage,Contents.KPI_TYPE);
            //目标
            o_dataSmImportBO.confirmImportSmData(isCoverage);
            //记分卡
            o_dataScImportBO.confirmImportSmData(isCoverage);
        }
        catch (ParseException e) {
            e.printStackTrace();
            log.error("导入失败:exception:"+e.toString());
            result = false;
        }
        resultMap.put("success", result);
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(resultMap));
    }
	
	/**上传指标类型,指标,记分卡,目标等所有监控预警数据导入临时表
	 * @param form 文件form
	 * @param isCoverage 是否覆盖
	 * @param response http响应对象
	 * @throws Exception 
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @ResponseBody
    @RequestMapping("/kpi/import/uploadkpialldatatotmp.f")
	public void uploadKpiAllDataToTmp(FileForm form,Boolean isCoverage, HttpServletResponse response) throws IOException {
	    Boolean resultAll = true;
	    Boolean uploadResult = true;
	    int smSheetNo = 0;//战略目标sheet位置.
	    int smRelaKpiSheetNo = 4;//战略目标关联的指标sheet位置.
	    int scSheetNo = 1;//记分卡sheet位置.
        int scRelaKpiSheetNo = 5;//记分卡关联的指标sheet位置.
	    int kpiTypeSheetNo = 2;//指标类型所在sheetno
	    int kpiSheetNo = 3;//指标所在sheetno
	    Map<String, Object> resultMap = new HashMap<String, Object>();
	    JSONObject resultItem = new JSONObject();
	    try {
	        //指标类型
	        List<List<String>> kpiTypeExcelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), kpiTypeSheetNo);
	        boolean kpiTypeResult = o_kpiTypeDataImportBO.saveKpiDataFromExcel(kpiTypeExcelDatas, "KC",isCoverage);
	        
	        //指标
	        List<List<String>> kpiExcelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), kpiSheetNo);
	        boolean kpiResult = o_kpiDataImportBO.saveAllKpiDataFromExcel(kpiExcelDatas, "KPI", isCoverage);
	        
	        //目标和关联的衡量指标
            List<List<String>> smDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), smSheetNo);
            List<List<String>> smRelaKpiDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), smRelaKpiSheetNo);
            boolean smResult = o_dataSmImportBO.importAllSmDataToTmp(smDatas, smRelaKpiDatas,isCoverage);
            
            //记分卡和关联的衡量指标
            List<List<String>> scDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), scSheetNo);
            List<List<String>> scRelaKpiDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), scRelaKpiSheetNo);
            boolean scResult = o_dataScImportBO.importAllScDataToTmp(scDatas, scRelaKpiDatas,isCoverage);
            
	        if(kpiTypeResult&&kpiResult&&smResult&&scResult){
	            uploadResult = true;
	        }else{
	            uploadResult = false;
	        }
	        resultItem.put("kpiTypeResult",kpiTypeResult);
	        resultItem.put("kpiResult",kpiResult);
	        resultItem.put("smResult",smResult);
	        resultItem.put("scResult",scResult);
	        
        }
        catch (Exception e) {
            e.printStackTrace();
            resultAll = false;
            uploadResult = false;
            log.error("导入全部监控预警数据异常:["+e.toString()+"]");
        }
	    resultMap.put("success", resultAll);
        resultMap.put("resultItem", resultItem);
        resultMap.put("uploadResult", uploadResult);
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(resultMap));
	}
	
	/**
	 * 上传数据到临时表
	 * @param form 
	 * @param response
	 * @param isCoverage 导入方式
	 * @throws IOException
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/kpi/kpi/uploadKpiTmp.f")
	public void uploadKpiDataToTmp(FileForm form, HttpServletResponse response,Boolean isCoverage) throws IOException{
		Boolean flag = false;
		Boolean result = true;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
				.readEspecialExcel(form.getFile().getInputStream(), 3);// 读取文件(第六页)
			
			//将数据写入数据库临时表
			flag = o_kpiDataImportBO.saveKpiDataFromExcel(excelDatas, "KPI", isCoverage);
			resultMap.put("uploadResult", flag);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		resultMap.put("success", result);
		response.getWriter().print(JSONSerializer.toJSON(resultMap));
		response.getWriter().close();
	}
	/**
	 * 将临时表中的指标导入实际表
	 * @param isCoverage 是否覆盖
	 * @return 导入结果
	 * @throws ParseException
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	@ResponseBody
	@RequestMapping("/kpi/kpiTmp/mergeKpiTmpKpi.f")
	public Map<String, Object> mergeKpiTmpKpi(Boolean isCoverage)  {
		Map<String, Object> data = new HashMap<String,Object>();
		o_kpiDataImportBO.mergeKpiTmpKpi(isCoverage,Contents.KPI_TYPE);
		data.put("success", true);
		return data;
	   }
	}
