package com.fhd.sm.business.dynamictable;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.business.KpiTableBO;
import com.fhd.sm.web.controller.util.TableYearUtil;
import com.fhd.sm.web.form.KpiMemoForm;

@Service
public class TableYearBO {

	@Autowired
    private KpiGatherResultBO o_KpiGatherResultBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	 
	@Autowired
	private KpiTableBO o_kpiTableBO;
	
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
	
	/**
     * 得到年动态TABLE
     * 
     * @author 金鹏祥
     * @param year 年
     * @param isEdit 是否编辑
     * @param kpiId 指标ID
     * @return String
     * @since  fhd　Ver 1.1
    */
    public String getYearHtml(String contextPath, String year, boolean isEdit, String kpiId, String kpiName, String timeId,String containerId){
    	Map<String, KpiGatherResult> map = o_KpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiId);
	    Kpi kpi = o_kpiBO.findKpiById(kpiId);
	    Map<String, DictEntry> dictMap = o_kpiTableBO.findMapDictEntryAll();
//		add by haojing  备注信息列
		String memoKpiName = kpiName;
//		add by haojing  备注信息列
	    kpiName = "采集信息";
    	String startTableHtml = TableYearUtil.getStartTableHtml(isEdit, kpiName);
    	String endTableHtml = TableYearUtil.getEndTableHtml();
		String halfYearHtml = "";
		String html = "";
		String alarmStatusImgPath = "";
		String assImgPath = "";
		String imgNull = "icons/underconstruction_small.gif";
		KpiGatherResult kpiGatherResultYear = null;
		boolean isYearInputReality = false; 
		boolean isYearInputTarget = false; 
		boolean isYearInputAssess = false;
//		add by haojing  备注信息列
		List<Object[]> objList = null;
		KpiMemoForm memoForm = null;
		String icon = "";
		String imgPath = "";
		String kgrId = "";
//		add by haojing  备注信息列
		
		if(isEdit){
			isYearInputReality = true;
			isYearInputTarget = true;
			isYearInputAssess = true;
		}
		
//		//不是全部编辑状态、KPI呈现、不呈现---年
//		if(!isEdit){
//			if(kpi.getIsResultFormula() == null){
//				isYearInputReality = false;
//			}if(kpi.getIsResultFormula() == null){
//				isYearInputTarget = false;
//			}if(kpi.getIsResultFormula() == null){
//				isYearInputAssess = false;
//			}if(kpi.getIsResultFormula() != null){
//				if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//					isYearInputReality = true;//false;
//				}
//			}if(kpi.getIsTargetFormula() != null){
//				if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//					isYearInputTarget = true;//false;
//				}
//			}if(kpi.getIsAssessmentFormula() != null){
//				if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//					isYearInputAssess = true;//false;
//				}
//			}if(kpi.getIsResultFormula() != null){
//				if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//					isYearInputReality = true;
//				}
//			}if(kpi.getIsTargetFormula() != null){
//				if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//					isYearInputTarget = true;
//				}
//			}if(kpi.getIsAssessmentFormula() != null){
//				if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//					isYearInputAssess = true;
//				}
//			}
//		}
		
		
		
		//不是全部编辑状态、单点
		if(!isEdit){
			if(!timeId.equalsIgnoreCase("")){
				if(timeId.equalsIgnoreCase("yearId")){
					isYearInputReality = true;
					isYearInputTarget = true;
					isYearInputAssess = true;
				}else{
					isYearInputReality = false;
					isYearInputTarget = false;
					isYearInputAssess = false;
				}
			}
			
//			else{
//				if(Integer.parseInt(year) < o_kpiTableBO.getYear()){
//					isYearInputReality = false;
//					isYearInputTarget = false;
//					isYearInputAssess = false;
//				}
//			}
		}
		
		kpiGatherResultYear = map.get(year);
		//等到告警状态、趋势图片
		alarmStatusImgPath ="";
		assImgPath = "";
		if(kpiGatherResultYear != null){
			if(kpiGatherResultYear.getDirection() != null){
				assImgPath= dictMap.get(kpiGatherResultYear.getDirection().getId()).getValue();
			}if(kpiGatherResultYear.getAssessmentStatus() != null){
				alarmStatusImgPath = dictMap.get(kpiGatherResultYear.getAssessmentStatus().getId()).getValue();
			}
		}
		
		/*if(assImgPath.equalsIgnoreCase("")){
			assImgPath = imgNull;
		}*/
		if(StringUtils.isBlank(alarmStatusImgPath)){
			alarmStatusImgPath = imgNull;
		}
		
//		add by haojing  备注信息列
		if(kpiGatherResultYear != null){
			kgrId = kpiGatherResultYear.getId();
			objList = o_kpiMemoBO.findMemoByKgrId(kgrId);
	 		memoForm = new KpiMemoForm(objList);
	 		icon = memoForm.getImportant();
	 		if(icon!=null&&icon.equals("0alarm_startus_h")){
	 			imgPath = "/images/icons/icon_comment_importance_high.gif";
	 		}else if(icon!=null&&icon.equals("0alarm_startus_l")){
	 			imgPath = "/images/icons/icon_comment_importance_low.gif";
	 		}else if(icon!=null&&icon.equals("0alarm_startus_n")){
	 			imgPath = "/images/icons/icon_note.gif";
	 		}else {
	 			imgPath = "/images/icons/icon_noreport_properties.gif";
	 		}
		}
		else {
 			imgPath = "/images/icons/icon_noreport_properties.gif";
 		}
//		add by haojing  备注信息列
		
		halfYearHtml += TableYearUtil.getYearHtml(contextPath, isEdit, kpiId, year, year, isYearInputReality, isYearInputTarget, isYearInputAssess,
				"realityYearId" + year, "targetYearId" + year, "assessYearId" + year, 
				kpiGatherResultYear == null || kpiGatherResultYear.getFinishValue() == null?"":kpiGatherResultYear.getFinishValue().toString(), 
				kpiGatherResultYear == null || kpiGatherResultYear.getTargetValue() == null?"":kpiGatherResultYear.getTargetValue().toString(), 
				kpiGatherResultYear == null || kpiGatherResultYear.getAssessmentValue() == null?"":kpiGatherResultYear.getAssessmentValue().toString(),
				alarmStatusImgPath, assImgPath,kgrId,imgPath,memoKpiName,containerId,kpi.getScale());
		
		html = startTableHtml + halfYearHtml + endTableHtml;
		
		return html;
    }
}
