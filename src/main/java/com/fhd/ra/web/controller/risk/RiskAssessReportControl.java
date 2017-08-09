package com.fhd.ra.web.controller.risk;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.comm.report.ReportInfomation;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.risk.RiskAssessReportBO;


/**
 * 风险评估报告模板
 * @author zhengjunxiang
 */
@Controller
public class RiskAssessReportControl {

	@Autowired
	private RiskAssessReportBO o_riskAssessReportBO;
	
	@ResponseBody
	@RequestMapping("/report/generateRiskReport")
	public Map<String,Object> generateRiskReport(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		String companyId = UserContext.getUser().getCompanyid();
		String empId = UserContext.getUser().getEmpid();
		Calendar cal = Calendar.getInstance();
		int month = (cal.get(Calendar.MONTH))+1;
		byte[] contents = o_riskAssessReportBO.generateRiskReport(companyId,month,empId);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("success", true);
		return resultMap;
//		/**
//		 * 设置文件ContentType类型
//		 * multipart/form-data 自动判断下载文件类型
//		 * application/ms-word
//		 * application/msexcel
//		 */
//        response.setContentType("application/ms-word;charset=utf-8"); 
//        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)  
//        String exportFileName = "风险报告.xml";        
//        response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(exportFileName, "utf-8"));
//        //3.通过response获取ServletOutputStream对象(out)
//        ServletOutputStream out = response.getOutputStream();
//        //4.写到输出流(out)中
//        out.write(contents);
//        out.flush();
//        out.close();
	}

	/**
	 * @param id 评估计划id
	 */
	@ResponseBody
	@RequestMapping("/report/generateRiskAssessReport")
	public Map<String,Object> generateRiskAssessReport(HttpServletRequest request,HttpServletResponse response,String id) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		String companyId = UserContext.getUser().getCompanyid();
		String empId = UserContext.getUser().getEmpid();
		Calendar cal = Calendar.getInstance();
		int month = (cal.get(Calendar.MONTH))+1;
		byte[] contents = o_riskAssessReportBO.generateRiskAssessReport(companyId,month,empId,id);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("success", true);
		return resultMap;
	}
	
	/**
	 * 
	 * @param fileId	附加id
	 * @param disName   报告名称
	 * @param type		报告类型
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/report/saveUploadReport.f")
	public Map<String,Object> saveUploadReport(String fileId, String disName,String type) throws Exception{
		o_riskAssessReportBO.uploadRiskReport(fileId, disName, type);	
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", true);
		
		return map;
	}
}
