package com.fhd.icm.web.controller.assess;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.bpm.BusinessWorkFlow;
import com.fhd.entity.icm.assess.AssessPlan;
import com.fhd.entity.icm.assess.AssessPlanProcessRelaOrgEmp;
import com.fhd.entity.icm.assess.AssessPlanRelaOrgEmp;
import com.fhd.entity.icm.assess.AssessPlanRelaProcess;
import com.fhd.entity.icm.assess.AssessResult;
import com.fhd.entity.process.Process;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.Contents;
import com.fhd.icm.business.assess.AssessPlanBO;
import com.fhd.icm.business.assess.AssessPlanRelaProcessBO;
import com.fhd.icm.business.assess.AssessTemplateExportBO;

@Controller
public class AssessTemplateExportControl {

	@Autowired
	private AssessTemplateExportBO o_assessTemplateExportBO;
	@Autowired
	private AssessPlanBO o_assessPlanBO;
	@Autowired
	private AssessPlanRelaProcessBO o_assessPlanRelaProcessBO;
	
	/**
	 * 查询某人的当前要评价执行的流程范围
	 * @param limit
	 * @param start
	 * @param sort
	 * @param query
	 * @param companyId 公司ID
	 * @param empId 员工ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/findAssessPlanProcessRelaOrgEmpPageBySome.f")
	public Map<String, Object> findAssessPlanProcessRelaOrgEmpPageBySome(int limit, int start, String sort, 
			String query, String companyId, String empId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Page<AssessPlanProcessRelaOrgEmp> page = new Page<AssessPlanProcessRelaOrgEmp>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_assessTemplateExportBO.findAssessPlanProcessRelaOrgEmpPageBySome(page, empId, companyId);
		List<AssessPlanProcessRelaOrgEmp> assessPlanProcessRelaOrgEmpList = page.getResult();
		Set<String> planIdSet = new HashSet<String>();
		for (AssessPlanProcessRelaOrgEmp assessPlanProcessRelaOrgEmp : assessPlanProcessRelaOrgEmpList) {
			AssessPlanRelaOrgEmp assessPlanRelaOrgEmp = assessPlanProcessRelaOrgEmp.getAssessPlanRelaOrgEmp();
			AssessPlan assessPlan = assessPlanRelaOrgEmp.getAssessPlan();
			planIdSet.add(assessPlan.getId()); 
		}
		List<BusinessWorkFlow> businessWorkFlowList;
		if(planIdSet.size()>0){
			businessWorkFlowList = o_assessTemplateExportBO.findBusinessWorkFlowByBusinessIds(planIdSet.toArray(new String[planIdSet.size()]));
		}else{
			businessWorkFlowList = new ArrayList<BusinessWorkFlow>();
		}
		Map<String, Object> row = null;
		for (AssessPlanProcessRelaOrgEmp assessPlanProcessRelaOrgEmp : assessPlanProcessRelaOrgEmpList) {
			AssessPlanRelaProcess assessPlanRelaProcess = assessPlanProcessRelaOrgEmp.getAssessPlanRelaProcess();
			AssessPlanRelaOrgEmp assessPlanRelaOrgEmp = assessPlanProcessRelaOrgEmp.getAssessPlanRelaOrgEmp();
			AssessPlan assessPlan = assessPlanRelaOrgEmp.getAssessPlan();
			Process process = assessPlanRelaProcess.getProcess();
			for (BusinessWorkFlow businessWorkFlow : businessWorkFlowList) {
				int rate = 0;
				if(StringUtils.isNotBlank(businessWorkFlow.getRate())){
					rate = Integer.valueOf(businessWorkFlow.getRate());
				}
				if(19<=rate && rate<=78 && businessWorkFlow.getBusinessId().equals(assessPlan.getId())){
					row = new HashMap<String, Object>();
					SysEmployee emp = assessPlanRelaOrgEmp.getEmp();
					row.put("id", assessPlanProcessRelaOrgEmp.getId());
					row.put("planId", assessPlan.getId());
					row.put("planName", assessPlan.getName());
					row.put("assessMeasureName", assessPlan.getAssessMeasure().getName());
					row.put("processId", process.getId());
					row.put("processName", process.getName());
					row.put("empName", emp.getEmpname());
					datas.add(row);
				}
			}
		}
		map.put("datas", datas);
		map.put("totalCount", page.getTotalItems());
		return map;
	}
	
	/**
	 * 导出评价底稿
	 * @param planId
	 * @param processIds
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/exportAssessTemplateBySome.f")
	public Map<String, Object> exportAssessTemplateBySome(String planId, String processIds,
			 HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object[]> list = new ArrayList<Object[]>();
		AssessPlan assessPlan = o_assessPlanBO.findAssessPlanById(planId);
		String testType = null;
		String assessMeasure = assessPlan.getAssessMeasure().getId();
		if(!Contents.ASSESS_MEASURE_ALL_TEST.equals(assessMeasure)){
			testType = assessMeasure;
		}
		
		JSONArray jsonArray = JSONArray.fromObject(processIds);
		String[] processIdStringArray = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			String string = jsonArray.getString(i);
			processIdStringArray[i] = string;
		}
		List<AssessPlanRelaProcess> assessPlanRelaProcessList = o_assessPlanRelaProcessBO.findAssessPlanRelaProcessListByAssessPlanId(planId);
		
		List<AssessResult> assessResultList = o_assessTemplateExportBO.findAssessResultListBySome(testType, planId,processIdStringArray);
		for (AssessResult assessResult : assessResultList) {
			Object[] object= new Object[11];
			object[0] = assessResult.getProcess().getName();
			object[1] = assessResult.getAssessPoint().getDesc();
			object[2] = assessResult.getAssessPoint().getComment();
			for (AssessPlanRelaProcess assessPlanRelaProcess : assessPlanRelaProcessList) {
				if(assessPlanRelaProcess.getProcess().getId().equals(assessResult.getProcess().getId())){
					if(Contents.ASSESS_MEASURE_PRACTICE_TEST.equals(assessResult.getAssessMeasure().getId())){
						object[3] = assessPlanRelaProcess.getPracticeNum();
					}else{
						object[3] = assessPlanRelaProcess.getCoverageRate()*100;
					}
				}
			}
			object[8] = planId;
			object[9] = assessResult.getId();
			object[10] = assessResult.getAssessMeasure().getId();
			list.add(object);
		}
		//数据，sheet1列名称，sheet2列名称，文件名称，sheet名称，sheet位置
        write(list, assessPlan.getName(), response);
        map.put("success", true);
        return map;
	}
	
	/**
	 * 写入Excel
	 * @param list
	 * @param planName
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void write(List<Object[]> list, String planName, HttpServletResponse response) throws Exception {
		
		WritableWorkbook wwbook = null;
		
		/***将临时excel文件响应客户***/
		// 设置头信息
		response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(planName+"_评价底稿.xls", "utf-8")); 
    	try { 
    		// 创建工作区间
    		wwbook = jxl.Workbook.createWorkbook(response.getOutputStream()); 
    		// 表头格式:添加带有字体颜色,带背景颜色 Formatting的对象       
		  	WritableFont wfc = new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLUE);       
    		WritableCellFormat wcfFc = new WritableCellFormat(wfc);
    		//设置水平对齐
    		wcfFc.setAlignment(Alignment.CENTRE);
    		//设置垂直对齐
    		wcfFc.setVerticalAlignment(VerticalAlignment.CENTRE); 
    		
    		// 列头格式:添加带有字体颜色,带背景颜色 Formatting的对象       
    		WritableFont fontColumnHead=new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);       
    		WritableCellFormat formatColumnHead=new WritableCellFormat(fontColumnHead);
    		//设置水平对齐
    		formatColumnHead.setAlignment(Alignment.CENTRE);
    		//设置垂直对齐
    		formatColumnHead.setVerticalAlignment(VerticalAlignment.CENTRE); 
    		//设置背景色
    		formatColumnHead.setBackground(Colour.ICE_BLUE);
    		
    		// 注释格式:添加带有字体颜色,带背景颜色 Formatting的对象       
    		WritableFont fontColumnComment=new WritableFont(WritableFont.ARIAL,10,WritableFont.NO_BOLD,true,UnderlineStyle.NO_UNDERLINE,Colour.GREY_80_PERCENT);       
    		WritableCellFormat formatColumnComment=new WritableCellFormat(fontColumnComment);
    		//设置水平对齐
    		formatColumnComment.setAlignment(Alignment.LEFT);
    		//设置垂直对齐
    		formatColumnComment.setVerticalAlignment(VerticalAlignment.TOP); 
    		formatColumnComment.setWrap(true);//是否自动换行
    		
    		// 创建sheet1
    		WritableSheet wsheet1 = wwbook.createSheet("穿行测试底稿", 0);
    		// 设置每列的宽度
    		wsheet1.setColumnView(0, 23);
    		wsheet1.setColumnView(1, 40);
    		wsheet1.setColumnView(2, 18);
    		wsheet1.setColumnView(3, 15);
    		wsheet1.setColumnView(4, 15);
    		wsheet1.setColumnView(5, 28);
    		wsheet1.setColumnView(6, 20);
    		wsheet1.setColumnView(7, 25);
    		wsheet1.setColumnView(8, 9);
    		wsheet1.setColumnView(9, 9);
    		wsheet1.setColumnView(10, 9);
    		// 设置第一行行高
    		wsheet1.setRowView(0, 600, false); 
    		//设置第二行行高
    		wsheet1.setRowView(1, 400, false); 
    		//设置第三行行高
    		wsheet1.setRowView(2, 800, false); 
    		//列名
    		String[] fieldTitle1 = new String[]{"流程名称",
				"评价点",
				"实施证据",
				"穿行次数",
				"样本编号*",
				"样本名称",
				"是否合格*",
				"说明",
				"标识列1",
				"标识列2",
				"标识列3"};
    		String[] fieldComment1 = new String[]{"查看列",
    			"查看列，根据“穿行次数”判断样本数量，通过复制整行实现多个样本录入，请保证足够的样本量，否则可能导致操作失败。",
    			"查看列",
    			"查看列，表示每个评价点需要抽取的样本个数",
    			"必填。以“_”、数字或字母组合",
    			"抽取的样本的名称，与样本附件的文件名（包含后缀名）保持一致，否则可能导致错误",
    			"必填：合格/不合格；不合格的请准备附件文件以便导入时上传",
    			"补充说明。样本不合格或不适用时必填。",
    			"标识列，不要修改",
    			"标识列，不要修改",
    			"标识列，不要修改"};
    		// 使用合并表头
    		wsheet1.mergeCells(0,0,fieldTitle1.length-1,0);
    		Label cellName1 = new Label(0, 0, planName, wcfFc);
    		// 插入第一行
    		wsheet1.addCell(cellName1);
    		// Label(列号,行号 ,内容 )
    		Label label1 = null;    
    		Label label11 = null;    
    		for (int i=0; i<fieldTitle1.length; i++){
    			//把标题放到第二行
    			label1 = new Label(i, 1, fieldTitle1[i],formatColumnHead);
    			wsheet1.addCell(label1);
    			//把注释放到第三行
    			label11 = new Label(i, 2, fieldComment1[i],formatColumnComment);
    			wsheet1.addCell(label11);
    		}
    		
    		// 创建sheet2
    		WritableSheet wsheet2 = wwbook.createSheet("抽样测试底稿", 1);
    		wsheet2.setColumnView(0, 23);
    		wsheet2.setColumnView(1, 40);
    		wsheet2.setColumnView(2, 18);
    		wsheet2.setColumnView(3, 15);
    		wsheet2.setColumnView(4, 15);
    		wsheet2.setColumnView(5, 28);
    		wsheet2.setColumnView(6, 20);
    		wsheet2.setColumnView(7, 25);
    		wsheet2.setColumnView(8, 9);
    		wsheet2.setColumnView(9, 9);
    		wsheet2.setColumnView(10, 9);
    		// 设置第一行行高
    		wsheet2.setRowView(0, 600, false); 
    		//设置第二行行高
    		wsheet2.setRowView(1, 400, false); 
    		//设置第三行行高
    		wsheet2.setRowView(2, 800, false); 
    		//列名
    		String[] fieldTitle2 = new String[]{"流程名称",
    			"评价点",
    			"实施证据",
    			"抽样比例(%)",
    			"样本编号*",
    			"样本名称",
    			"是否合格*",
    			"说明",
    			"标识列1",
    			"标识列2",
    			"标识列3"};
    		String[] fieldComment2 = new String[]{"查看列",
    			"查看列，根据“抽样比例(%)”判断样本数量，通过复制整行实现多个样本录入，请保证足够的样本量，否则可能导致操作失败。",
    			"查看列",
    			"查看列，表示每个评价点需抽取的样本的比例",
    			"必填。以“_”、数字或字母组合",
    			"抽取的样本的名称，与样本附件的文件名（包含后缀名）保持一致，否则可能导致错误",
    			"必填：合格/不合格；不合格的请准备附件文件以便导入时上传",
    			"补充说明。样本不合格或不适用时必填。",
    			"标识列，不要修改",
    			"标识列，不要修改",
    			"标识列，不要修改"};
    		// 使用合并表头
    		wsheet2.mergeCells(0,0,fieldTitle2.length-1,0);
    		Label cellName2 = new Label(0, 0, planName, wcfFc);
    		wsheet2.addCell(cellName2);
    		
    		
    		// Label(列号,行号 ,内容 )
    		Label label2 = null;     
    		Label label21 = null;   
    		for (int i=0; i<fieldTitle2.length; i++){
    			//把标题放到第二行
    			label2 = new Label(i, 1, fieldTitle2[i],formatColumnHead);
    			wsheet2.addCell(label2);
    			//把注释放到第三行
    			label21 = new Label(i, 2, fieldComment2[i],formatColumnComment);
    			wsheet2.addCell(label21);
    		}
    		// a wsheet1起始插入的行的序号，b wsheet2起始插入的行的序号
    		int a = 3, b = 3;
    		// 写入数据
    		for (int k = 0; k < list.size(); k++) {
    			boolean isAPlus = false;
    			boolean isBPlus = false;
    			Object[] obj = (Object[]) list.get(k);
    			for (int j=0; j<fieldTitle1.length; j++){
    				Label content = null;
    				if(Contents.ASSESS_MEASURE_PRACTICE_TEST.equals(obj[10])){
    					content  = new Label(j, a, String.valueOf(null != obj[j]?obj[j]:""));
						wsheet1.addCell(content);
    					isAPlus = true;
    				}else if(Contents.ASSESS_MEASURE_SAMPLE_TEST.equals(obj[10])){
    					content  = new Label(j, b, String.valueOf(null != obj[j]?obj[j]:""));
    					wsheet2.addCell(content);
    					isBPlus = true;
    				}
    			}
    			if(isAPlus){
    				a++;
    			}
    			if(isBPlus){
    				b++;
    			}
    		}
    		wwbook.write();
    		wwbook.close();
    	}catch (Exception e) { 
    		e.printStackTrace();
    		throw new Exception(e);
    	}finally {
    		response.getOutputStream().close();
    	} 
    }
	
//	/**
//	 * 添加下拉框
//	 * @param contentArray
//	 * @return
//	 */
//	private WritableCellFeatures addColumnList(String[] contentArray) {
//		List<String> contentList = new ArrayList<String>();
//		WritableCellFeatures wcf = new WritableCellFeatures();
//		for (int i = 0; i < contentArray.length; i++) {
//			contentList.add(contentArray[i]);
//		}
//		wcf.setDataValidationList(contentList);
//		contentList.clear();
//		contentList = null;
//		return wcf;
//	}
	
}
