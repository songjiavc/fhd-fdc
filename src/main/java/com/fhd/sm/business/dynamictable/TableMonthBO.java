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
import com.fhd.sm.web.controller.util.TableMonthUtil;
import com.fhd.sm.web.form.KpiMemoForm;

@Service
public class TableMonthBO {
	
	@Autowired
    private KpiGatherResultBO o_KpiGatherResultBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	 
	@Autowired
	private KpiTableBO o_kpiTableBO;
	
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
	
	/**
     * 得到月动态TABLE
     * 
     * @author 金鹏祥
     * @param year 年
     * @param isEdit 是否编辑
     * @param kpiId 指标ID
     * @return String
     * @since  fhd　Ver 1.1
    */
    public String getMonthHtml(String contextPath, String year, boolean isEdit, String kpiId, String kpiName, String timeId,String containerId){
    	Map<String, KpiGatherResult> map = o_KpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiId);
    	List<TimePeriod> quarterList = o_kpiTableBO.findTimePeriodById(year, "0frequecy_quarter");
	    List<TimePeriod> monthsList = o_kpiTableBO.findTimePeriodById(year, "0frequecy_month");
	    Map<String, DictEntry> dictMap = o_kpiTableBO.findMapDictEntryAll();
	    Kpi kpi = o_kpiBO.findKpiById(kpiId);
	    List<TimePeriod> monthByQuarterList = null;
	    KpiGatherResult kpiGatherResultYear = null;
	    KpiGatherResult kpiGatherResultQuarter = null;
	    KpiGatherResult kpiGatherResultMonth = null;
//		add by haojing  备注信息列
		String memoKpiName = kpiName;
//		add by haojing  备注信息列
	    kpiName = "采集信息";
    	String startTableHtml = TableMonthUtil.getStartTableHtml(isEdit, kpiName,containerId);
		String YearHtml = TableMonthUtil.getYear(year,containerId);
		String quarterHtml = "";
		String monthHtml = "";
		String html = "";
		String alarmStatusImgPath = "";
		String assImgPath = "";
		String imgNull = "icons/underconstruction_small.gif";
		boolean isYearSunInputReality = false;
		boolean isYearSunInputTarget = false;
		boolean isYearSunInputAssess = false;
		boolean isMonthInputReality = false; 
		boolean isMonthInputTarget = false; 
		boolean isMonthInputAssess = false;
		boolean isQuarterInputReality = false; 
		boolean isQuarterInputTarget = false; 
		boolean isQuarterInputAssess = false;
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
			isMonthInputReality = true; 
			isMonthInputTarget = true; 
			isMonthInputAssess = true;
			isQuarterInputReality = true; 
			isQuarterInputTarget = true; 
			isQuarterInputAssess = true;
		}
		
		//不是全部编辑状态、KPI呈现、不呈现---年
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
				assImgPath = dictMap.get(kpiGatherResultYear.getDirection().getId()).getValue();
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
		
		
		String yearSunHtml = TableMonthUtil.getYearSunHtml(contextPath, isEdit, kpiId, year,
//				false, false, false,
				isYearSunInputReality, isYearSunInputTarget, isYearSunInputAssess,
				"realityYearId" + year, "targetYearId" + year, "assessYearId" + year, 
				kpiGatherResultYear == null || kpiGatherResultYear.getFinishValue() == null?"":kpiGatherResultYear.getFinishValue().toString(), 
				kpiGatherResultYear == null || kpiGatherResultYear.getTargetValue() == null?"":kpiGatherResultYear.getTargetValue().toString(), 
				kpiGatherResultYear == null || kpiGatherResultYear.getAssessmentValue() == null?"":kpiGatherResultYear.getAssessmentValue().toString(),
				alarmStatusImgPath,assImgPath,kgrId,imgPath,memoKpiName,containerId,kpi.getScale());
		String endTableHtml = TableMonthUtil.getEndTableHtml();
		String quarterAndMonthHtml = "";
		
		int i = 1;
		int j = 1;
		for (TimePeriod timePeriod : quarterList) {
			kpiGatherResultQuarter = map.get(timePeriod.getId());
			monthByQuarterList = o_kpiTableBO.getMonth(monthsList, String.valueOf(i));
			
			quarterHtml = TableMonthUtil.getQuarterHtml(String.valueOf(i));
			for (TimePeriod monthTimePeriod : monthByQuarterList) {
				kpiGatherResultMonth = map.get(monthTimePeriod.getId());
				
//				//不是全部编辑状态、KPI呈现、不呈现---年
//				if(!isEdit){
//					if(kpi.getIsResultFormula() == null){
//						isMonthInputReality = false;
//					}if(kpi.getIsResultFormula() == null){
//						isMonthInputTarget = false;
//					}if(kpi.getIsResultFormula() == null){
//						isMonthInputAssess = false;
//					}if(kpi.getIsResultFormula() != null){
//						if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_formula")){
////							isMonthInputReality = false;
//							isMonthInputReality = true;
//						}
//					}if(kpi.getIsTargetFormula() != null){
//						if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_formula")){
////							isMonthInputTarget = false;
//							isMonthInputTarget = true;
//						}
//					}if(kpi.getIsAssessmentFormula() != null){
//						if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_formula")){
////							isMonthInputAssess = false;
//							isMonthInputAssess = true;
//						}
//					}if(kpi.getIsResultFormula() != null){
//						if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//							isMonthInputReality = true;
//						}
//					}if(kpi.getIsTargetFormula() != null){
//						if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//							isMonthInputTarget = true;
//						}
//					}if(kpi.getIsAssessmentFormula() != null){
//						if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//							isMonthInputAssess = true;
//						}
//					}
//				}
				
				//不是全部编辑状态、单点
				if(!isEdit){
					if(!timeId.equalsIgnoreCase("")){
						if(timeId.equalsIgnoreCase(monthTimePeriod.getId())){
							isMonthInputReality = true; 
							isMonthInputTarget = true; 
							isMonthInputAssess = true;
						}else{
							isMonthInputReality = false; 
							isMonthInputTarget = false; 
							isMonthInputAssess = false;
						}
					}else{
//						//如果DB月份小于当前月份,不显示input
//						if(Integer.parseInt(year) < o_kpiTableBO.getYear()){
//							isMonthInputReality = false; 
//							isMonthInputTarget = false; 
//							isMonthInputAssess = false;
//						}
//						if(Integer.parseInt(monthTimePeriod.getMonth()) < o_kpiTableBO.getMonth()){
//							isMonthInputReality = false; 
//							isMonthInputTarget = false; 
//							isMonthInputAssess = false;
//						}
					}
				}
				
				//等到告警状态、趋势图片
				alarmStatusImgPath ="";
				assImgPath = "";
				if(kpiGatherResultMonth != null){
					if(kpiGatherResultMonth.getDirection() != null){
						assImgPath= dictMap.get(kpiGatherResultMonth.getDirection().getId()).getValue();
					}if(kpiGatherResultMonth.getAssessmentStatus() != null){
						alarmStatusImgPath = dictMap.get(kpiGatherResultMonth.getAssessmentStatus().getId()).getValue();
					}
				}
				
				/*if(assImgPath.equalsIgnoreCase("")){
					assImgPath = imgNull;
				}*/
				if(StringUtils.isBlank(alarmStatusImgPath)){
					alarmStatusImgPath = imgNull;
				}
				
				
//				add by haojing  备注信息列
				if(kpiGatherResultMonth != null){
					kgrId = kpiGatherResultMonth.getId();
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
//	    		add by haojing  备注信息列
				monthHtml += TableMonthUtil.getMonthHtml(contextPath, year,isEdit, kpiId, monthTimePeriod.getId(), String.valueOf(j), 
						isMonthInputReality, isMonthInputTarget, isMonthInputAssess,
//						false, false, false,
						monthTimePeriod.getId(), monthTimePeriod.getId(),
						kpiGatherResultMonth == null || kpiGatherResultMonth.getFinishValue() == null?"":kpiGatherResultMonth.getFinishValue().toString(), 
						kpiGatherResultMonth == null || kpiGatherResultMonth.getTargetValue() == null?"":kpiGatherResultMonth.getTargetValue().toString(), 
						kpiGatherResultMonth == null || kpiGatherResultMonth.getAssessmentValue() == null?"":kpiGatherResultMonth.getAssessmentValue().toString(),
						alarmStatusImgPath,assImgPath,kgrId,imgPath,memoKpiName,containerId,kpi.getScale());
				j++;
			}
			
//			//不是全部编辑状态、KPI呈现、不呈现---年
//			if(!isEdit){
//				if(kpi.getIsResultFormula() == null){
//					isQuarterInputReality = false;
//				}if(kpi.getIsResultFormula() == null){
//					isQuarterInputTarget = false;
//				}if(kpi.getIsResultFormula() == null){
//					isQuarterInputAssess = false;
//				}if(kpi.getIsResultFormula() != null){
//					if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//						isQuarterInputReality = true;//false;
//					}
//				}if(kpi.getIsTargetFormula() != null){
//					if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//						isQuarterInputTarget = true;//false;
//					}
//				}if(kpi.getIsAssessmentFormula() != null){
//					if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_formula")){
//						isQuarterInputAssess = true;//false;
//					}
//				}if(kpi.getIsResultFormula() != null){
//					if(kpi.getIsResultFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//						isQuarterInputReality = true;
//					}
//				}if(kpi.getIsTargetFormula() != null){
//					if(kpi.getIsTargetFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//						isQuarterInputTarget = true;
//					}
//				}if(kpi.getIsAssessmentFormula() != null){
//					if(kpi.getIsAssessmentFormula().equalsIgnoreCase("0sys_use_formular_manual")){
//						isQuarterInputAssess = true;
//					}
//				}
//			}
			
			//不是全部编辑状态、单点
			if(!isEdit){
				if(!timeId.equalsIgnoreCase("")){
					if(timeId.equalsIgnoreCase(timePeriod.getId())){
						isQuarterInputReality = true; 
						isQuarterInputTarget = true; 
						isQuarterInputAssess = true;
					}else{
						isQuarterInputReality = false; 
						isQuarterInputTarget = false; 
						isQuarterInputAssess = false;
					}
				}
				
//				else{
//					
//					if(Integer.parseInt(year) < o_kpiTableBO.getYear()){
//						isQuarterInputReality = false; 
//						isQuarterInputTarget = false; 
//						isQuarterInputAssess = false;
//					}
//					
//					//如果DB季度小于当前季度,不显示input
//					if(Integer.parseInt(timePeriod.getQuarter()) < o_kpiTableBO.getQuarter(o_kpiTableBO.getMonth())){
//						isQuarterInputReality = false; 
//						isQuarterInputTarget = false; 
//						isQuarterInputAssess = false;
//					}
//				}
			}
			
			//等到告警状态、趋势图片
			alarmStatusImgPath ="";
			assImgPath = "";
			if(kpiGatherResultQuarter != null){
				if(kpiGatherResultQuarter.getDirection() != null){
//					if(iconMap.get(dictMap.get(kpiGatherResultQuarter.getDirection().getId()).getValue()) != null){
						assImgPath= dictMap.get(kpiGatherResultQuarter.getDirection().getId()).getValue();
//					}
				}if(kpiGatherResultQuarter.getAssessmentStatus() != null){
//					if(iconMap.get(dictMap.get(kpiGatherResultQuarter.getAssessmentStatus().getId()).getValue()) != null){
						alarmStatusImgPath = dictMap.get(kpiGatherResultQuarter.getAssessmentStatus().getId()).getValue();
//					}
				}
			}
			
			/*if(assImgPath.equalsIgnoreCase("")){
				assImgPath = imgNull;
			}*/
			if(StringUtils.isBlank(alarmStatusImgPath)){
				alarmStatusImgPath = imgNull;
			}
//			add by haojing  备注信息列
			if(kpiGatherResultQuarter != null){
				kgrId = kpiGatherResultQuarter.getId();
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
//    		add by haojing  备注信息列
			if(i!=1){
			    quarterHtml = "<tr  quarterrowspan='1' onclick=\" Ext.getCmp('" + containerId + "').tablePanel.focusmonth(this,'"+ containerId +"_month_table');\">\n"+quarterHtml;
			}
			quarterAndMonthHtml += quarterHtml + monthHtml + TableMonthUtil.getQuarterSunHtml(contextPath, year,isEdit, kpiId, timePeriod.getId(),
					isQuarterInputReality, isQuarterInputTarget, isQuarterInputAssess,
					timePeriod.getId(), timePeriod.getId() ,
					kpiGatherResultQuarter == null || kpiGatherResultQuarter.getFinishValue() == null?"":kpiGatherResultQuarter.getFinishValue().toString(), 
					kpiGatherResultQuarter == null || kpiGatherResultQuarter.getTargetValue() == null?"":kpiGatherResultQuarter.getTargetValue().toString(), 
					kpiGatherResultQuarter == null || kpiGatherResultQuarter.getAssessmentValue() == null?"":kpiGatherResultQuarter.getAssessmentValue().toString(),
					alarmStatusImgPath, assImgPath, true,kgrId,imgPath,memoKpiName,containerId,kpi.getScale());
			quarterHtml = "";
			monthHtml = "";
			i++;
		}
		
		html = startTableHtml + YearHtml + quarterAndMonthHtml + yearSunHtml + endTableHtml;
		return html;
    }
}