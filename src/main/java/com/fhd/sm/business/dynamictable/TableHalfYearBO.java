package com.fhd.sm.business.dynamictable;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.business.KpiTableBO;
import com.fhd.sm.web.controller.util.TableHalfYearUtil;
import com.fhd.sm.web.form.KpiMemoForm;

@Service
public class TableHalfYearBO {
	@Autowired
    private KpiGatherResultBO o_KpiGatherResultBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	 
	@Autowired
	private KpiTableBO o_kpiTableBO;
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
	
	/**
     * 得到半年动态TABLE
     * 
     * @author 金鹏祥
     * @param year 年
     * @param isEdit 是否编辑
     * @param kpiId 指标ID
     * @return String
     * @since  fhd　Ver 1.1
    */
    public String getHalfYearHtml(String contextPath, String year, boolean isEdit, String kpiId, String kpiName, String timeId,String containerId){
    	Map<String, KpiGatherResult> map = o_KpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiId);
    	List<TimePeriod> halfYearList = o_kpiTableBO.findTimePeriodById(year, "0frequecy_halfyear");
	    Kpi kpi = o_kpiBO.findKpiById(kpiId);
	    Map<String, DictEntry> dictMap = o_kpiTableBO.findMapDictEntryAll();
    	KpiGatherResult kpiGatherResultYear = null;
	    KpiGatherResult kpiGatherResultHalfYear = null;
//		add by haojing  备注信息列
		String memoKpiName = kpiName;
//		add by haojing  备注信息列
	    kpiName = "采集信息";
    	String startTableHtml = TableHalfYearUtil.getStartTableHtml(isEdit, kpiName);
		String YearHtml = TableHalfYearUtil.getYear(year);
		String halfYearHtml = "";
		String endTableHtml = TableHalfYearUtil.getEndTableHtml();
		String html = "";
		String alarmStatusImgPath = "";
		String assImgPath = "";
		String imgNull = "icons/underconstruction_small.gif";
		boolean isYearSunInputReality = false;
		boolean isYearSunInputTarget = false;
		boolean isYearSunInputAssess = false;
		
		boolean isHalfYearInputReality = false; 
		boolean isHalfYearInputTarget = false; 
		boolean isHalfYearInputAssess = false;
//		add by haojing  备注信息列
		List<Object[]> objList = null;
		KpiMemoForm memoForm = null;
		String icon = "";
		String imgPath = "";
		String kgrId = "";
//		add by haojing  备注信息列
		
		if(isEdit){
			isYearSunInputReality = true;
			isYearSunInputTarget = true;
			isYearSunInputAssess = true;
			
			isHalfYearInputReality = true;
			isHalfYearInputTarget = true;
			isHalfYearInputAssess = true;
		}
		
		for (TimePeriod timePeriod : halfYearList) {
			kpiGatherResultHalfYear = map.get(timePeriod.getId());
//			//不是全部编辑状态、KPI呈现、不呈现---年
//			if(!isEdit){
//				if(kpi.getIsResultFormula() == null){
//					isHalfYearInputReality = false;
//				}if(kpi.getIsResultFormula() == null){
//					isHalfYearInputTarget = false;
//				}if(kpi.getIsResultFormula() == null){
//					isHalfYearInputAssess = false;
//				}if(kpi.getIsResultFormula() != null){
//					if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//						isHalfYearInputReality = true;//false;
//					}
//				}if(kpi.getIsTargetFormula() != null){
//					if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//						isHalfYearInputTarget = true;//false;
//					}
//				}if(kpi.getIsAssessmentFormula() != null){
//					if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//						isHalfYearInputAssess = true;//false;
//					}
//				}if(kpi.getIsResultFormula() != null){
//					if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//						isHalfYearInputReality = true;
//					}
//				}if(kpi.getIsTargetFormula() != null){
//					if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//						isHalfYearInputTarget = true;
//					}
//				}if(kpi.getIsAssessmentFormula() != null){
//					if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//						isHalfYearInputAssess = true;
//					}
//				}
//			}
			
			//不是全部编辑状态、单点
			if(!isEdit){
				if(!timeId.equalsIgnoreCase("")){
					if(timeId.equalsIgnoreCase(timePeriod.getId())){
						isHalfYearInputReality = true; 
						isHalfYearInputTarget = true; 
						isHalfYearInputAssess = true;
					}else{
						isHalfYearInputReality = false; 
						isHalfYearInputTarget = false; 
						isHalfYearInputAssess = false;
					}
				}
//				else{
//					//如果DB上、下半年不是当前上、下半年,不显示input
//					if(Integer.parseInt(year) < o_kpiTableBO.getYear()){
//						isHalfYearInputReality = false; 
//						isHalfYearInputTarget = false; 
//						isHalfYearInputAssess = false;
//					}
//					
//					if(Integer.parseInt(timePeriod.getHalfYear()) != o_kpiTableBO.getHalfYear(o_kpiTableBO.getMonth())){
//						isHalfYearInputReality = false; 
//						isHalfYearInputTarget = false; 
//						isHalfYearInputAssess = false;
//					}
//				}
			}
			
			//等到告警状态、趋势图片
			alarmStatusImgPath ="";
			assImgPath = "";
			if(kpiGatherResultHalfYear != null){
				if(kpiGatherResultHalfYear.getDirection() != null){
					assImgPath= dictMap.get(kpiGatherResultHalfYear.getDirection().getId()).getValue();
				}if(kpiGatherResultHalfYear.getAssessmentStatus() != null){
					alarmStatusImgPath = dictMap.get(kpiGatherResultHalfYear.getAssessmentStatus().getId()).getValue();
				}
			}
			
			/*if(assImgPath.equalsIgnoreCase("")){
				assImgPath = imgNull;
			}*/
			if(StringUtils.isBlank(alarmStatusImgPath)){
				alarmStatusImgPath = imgNull;
			}
			
//			add by haojing  备注信息列
			if(kpiGatherResultHalfYear != null){
				kgrId = kpiGatherResultHalfYear.getId();
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
//			add by haojing  备注信息列
			
			halfYearHtml += TableHalfYearUtil.getHalfYearHtml(contextPath, year, isEdit, kpiId, timePeriod.getId(), timePeriod.getTimePeriodName(),
					isHalfYearInputReality, isHalfYearInputTarget, isHalfYearInputAssess,
					timePeriod.getId(), timePeriod.getId() ,
					kpiGatherResultHalfYear == null || kpiGatherResultHalfYear.getFinishValue() == null?"":kpiGatherResultHalfYear.getFinishValue().toString(), 
					kpiGatherResultHalfYear == null || kpiGatherResultHalfYear.getTargetValue() == null?"":kpiGatherResultHalfYear.getTargetValue().toString(), 
					kpiGatherResultHalfYear == null || kpiGatherResultHalfYear.getAssessmentValue() == null?"":kpiGatherResultHalfYear.getAssessmentValue().toString(),
					alarmStatusImgPath, assImgPath,kgrId,imgPath,memoKpiName,containerId,kpi.getScale());
		}
		
//		//不是全部编辑状态、KPI呈现、不呈现---年
//		if(!isEdit){
//			if(kpi.getIsResultFormula() == null){
//				isYearSunInputReality = false;
//			}if(kpi.getIsResultFormula() == null){
//				isYearSunInputTarget = false;
//			}if(kpi.getIsResultFormula() == null){
//				isYearSunInputAssess = false;
//			}if(kpi.getIsResultFormula() != null){
//				if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//					isYearSunInputReality = true;//false;
//				}
//			}if(kpi.getIsTargetFormula() != null){
//				if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//					isYearSunInputTarget = true;//false;
//				}
//			}if(kpi.getIsAssessmentFormula() != null){
//				if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//					isYearSunInputAssess = true;//false;
//				}
//			}if(kpi.getIsResultFormula() != null){
//				if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//					isYearSunInputReality = true;
//				}
//			}if(kpi.getIsTargetFormula() != null){
//				if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//					isYearSunInputTarget = true;
//				}
//			}if(kpi.getIsAssessmentFormula() != null){
//				if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//					isYearSunInputAssess = true;
//				}
//			}
//		}
		
		
		//不是全部编辑状态、单点
		if(!isEdit){
			if(!timeId.equalsIgnoreCase("")){
				if(timeId.equalsIgnoreCase("yearId")){
					isYearSunInputReality = true;
					isYearSunInputTarget = true;
					isYearSunInputAssess = true;
				}else{
					isYearSunInputReality = false;
					isYearSunInputTarget = false;
					isYearSunInputAssess = false;
				}
			}
			
//			else{
//				if(Integer.parseInt(year) < o_kpiTableBO.getYear()){
//					isYearSunInputReality = false;
//					isYearSunInputTarget = false;
//					isYearSunInputAssess = false;
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
		
		String yearSunHtml = TableHalfYearUtil.getYearSunHtml(contextPath, isEdit, kpiId, year, isYearSunInputReality, isYearSunInputTarget, isYearSunInputAssess,
				"realityYearId" + year, "targetYearId" + year, "assessYearId" + year, 
				kpiGatherResultYear == null || kpiGatherResultYear.getFinishValue() == null?"":kpiGatherResultYear.getFinishValue().toString(), 
				kpiGatherResultYear == null || kpiGatherResultYear.getTargetValue() == null?"":kpiGatherResultYear.getTargetValue().toString(), 
				kpiGatherResultYear == null || kpiGatherResultYear.getAssessmentValue() == null?"":kpiGatherResultYear.getAssessmentValue().toString(),
				alarmStatusImgPath, assImgPath,kgrId,imgPath,memoKpiName,containerId,kpi.getScale());
		
		html = startTableHtml + YearHtml + halfYearHtml + yearSunHtml + endTableHtml;
		return html;
    }
}
