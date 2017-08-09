package com.fhd.sm.web.controller.util;

public class TableQuarterUtil {

	
	
	public static String getStartTableHtml(boolean isEdit, String kpiName,String containerId){
		StringBuffer startTableHtml = new StringBuffer();
		
		startTableHtml.append("<fieldset style='margin-left:10px;margin-right:10px;' class='x-fieldset x-fieldset-with-title x-fieldset-with-legend x-fieldset-default'>\n");
		startTableHtml.append("<legend class='x-fieldset-header x-fieldset-header-default'>" + kpiName + "</legend>\n");
		startTableHtml.append("<table width='100%' height='80%' border='1' cellpadding='0' cellspacing='0' class='fhd_add' id ='"+ containerId +"_quarter_table'>\n");
		startTableHtml.append("<tbody>");
		startTableHtml.append("<tr>\n");
		startTableHtml.append("<td class='cen' width='5%' align='center'>年份</td>\n");
		startTableHtml.append("<td class='cen' width='5%' align='center'>季度</td>\n");
		startTableHtml.append("<td class='cen' width='5%' align='center'>状态</td>\n");
		startTableHtml.append("<td class='cen' width='5%' align='center'>趋势</td>\n");
//		add by haojing  备注信息列
		startTableHtml.append("<td class='cen' width='5%' align='center'>注释</td>\n");
//		add by haojing  备注信息列
		startTableHtml.append("<td hi='1' class='cen' width='20%' align='center'>实际值</td>\n");
		startTableHtml.append("<td hi='1' class='cen' width='20%' align='center'>目标值</td>\n");
		startTableHtml.append("<td hi='1' class='cen' width='20%' align='center'>评估值</td>\n");
		if(!isEdit){
			startTableHtml.append("<td class='cen' width='5%' align='center'>操作</td>\n");
		}
		startTableHtml.append("</tr>\n");
		
		return startTableHtml.toString();
	}
	
	public static String getYear(String year,String containerId){
		StringBuffer yearHtml = new StringBuffer();
		yearHtml.append("<tr rowspaneflag='1' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.focus(this,'"+ containerId +"_quarter_table');\">\n");
		yearHtml.append("<td  align='center' hi='1' rowspan='6'>" + year + "</td>\n");
//		yearHtml.append("</tr>\n");
		
		return yearHtml.toString();
	}
	
	public static String getQuarterHtml(String contextPath, String yearId, boolean isEdit, String kpiId, String timeId,
			String quarter,
			boolean isInputReality, 
			boolean isInputTarget, 
			boolean isInputAssess,
			String inputId, 
			String inputName,
			String realityValue,
			String targetValue,
			String assessValue, String statusImg, String trendImg,String kgrId,String imgPath,String memoKpiName,String containerId,int scale){
		realityValue = Utils.getValue(realityValue,scale);
		targetValue = Utils.getValue(targetValue,scale);
		assessValue = Utils.getValue(assessValue,scale);
		
		StringBuffer quarterHtml = new StringBuffer();
		
		//实际
		String inputReality = "<input type='text' value='" + realityValue + "' name='" + inputName + "reality' id='" +containerId+ inputId + "reality' class='tableTextInputChanged' style='font-size: 100%;'>";
		//目标
		String inputTarget = "<input type='text' value='" + targetValue + "' name='" + inputName + "target' id='" + containerId+ inputId + "target' class='tableTextInputChanged' style='font-size: 100%;'>";
		//评估
		String inpuAssess = "<input type='text' value='" + assessValue + "' name='" + inputName + "assess' id='" + containerId + inputId + "assess' class='tableTextInputChanged' style='font-size: 100%;'>";
		
		if(!isInputReality){
			inputReality =realityValue;
		}
		if(!isInputTarget){
			inputTarget =targetValue;
		}
		
		if(!isInputAssess){
			inpuAssess = assessValue;
		}
		
		String trendimgHtml = "";
		if(trendImg.equalsIgnoreCase("")){
			trendimgHtml = "";
		}else{
			trendimgHtml = "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + trendImg + "'/>";
		}
		if(!"1".equals(quarter)){
		    quarterHtml.append("<tr onclick=\"Ext.getCmp('" + containerId + "').tablePanel.focus(this,'"+ containerId +"_quarter_table');\">\n");
		}
		quarterHtml.append("<td nowrap='true' align='center' hi='1' rowspan='1'>第" + quarter + "季</td>\n");
		quarterHtml.append("<input type='hidden' value='1140' name='time_period_sid_1'>\n");
		quarterHtml.append("<td nowrap='true' align='center' class='normalTabLevel3' hi='3' ><div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + statusImg + "'/></td>\n");
		quarterHtml.append("<td nowrap='true' align='center' class='normalTabLevel3' hi='3'>" + trendimgHtml + "</td>\n");
//		add by haojing  备注信息列
		String memoStr = "注释——"+memoKpiName+"("+yearId+"年第" + quarter + "季度)";
		quarterHtml.append("<td nowrap='true' align='center' hi='3' >" +
				"<input type='image' src='" + contextPath + imgPath+"' title='备注' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.getData('"+ kgrId +"','" + memoStr + "')\"/></td>\n");
//		add by haojing  备注信息列
		quarterHtml.append("<td nowrap='true' align='center' hi='3' >" + inputReality + "</td>\n");
		quarterHtml.append("<td nowrap='true' align='center' hi='3' >" + inputTarget + "</td>\n");
		quarterHtml.append("<td nowrap='true' align='center' hi='3' >" + inpuAssess + "</td>\n");
		
		if(!isEdit){
			if(!isInputReality && !isInputTarget && !isInputAssess){
				quarterHtml.append("<td nowrap='true' align='center' hi='3'  class='cen'>" +
					"<input type='image' src='" + contextPath + "/images/icons/edit.gif' title='编辑' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneInput('" + timeId + "','" + yearId + "');\"/></td>\n");
			}else{
				quarterHtml.append("<td nowrap='true' align='center' hi='3' id='RT_tolerance_1' class='cen'>");
				quarterHtml.append("<input type='image' src='" + contextPath + "/images/icons/save.gif' title='保存' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneSave('" + yearId + "','" + kpiId + "','" + timeId + "',document.getElementById('" + containerId + inputId + "reality').value,document.getElementById('" + containerId + inputId + "target').value,document.getElementById('" + containerId + inputId + "assess').value);\"/>");
				quarterHtml.append("</td>\n");
			}
		}
		
		quarterHtml.append("</tr>\n");
		
		return quarterHtml.toString();
	}
	
	public static String getYearSunHtml(String contextPath, boolean isEdit, String kpiId, String timeId,
			boolean isInputReality, 
			boolean isInputTarget, 
			boolean isInputAssess,
			String realityId,
			String targetId, 
			String assessId, 
			String realityValue,
			String targetValue,
			String assessValue, String statusImg, String trendImg,String kgrId,String imgPath,String memoKpiName,String containerId,int scale){
		realityValue = Utils.getValue(realityValue,scale);
		targetValue = Utils.getValue(targetValue,scale);
		assessValue = Utils.getValue(assessValue,scale);
		
		StringBuffer YearSunHtml = new StringBuffer();
		
		//实际
		String inputReality = "<input type='text' value='" + realityValue + "' name='" + realityId + "reality' id='" + containerId + realityId + "reality' class='tableTextInputChanged' style='font-size: 100%;'>";
		//目标
		String inputTarget = "<input type='text' value='" + targetValue + "' name='" + targetId + "target' id='" + containerId + targetId + "target' class='tableTextInputChanged' style='font-size: 100%;'>";
		//评估
		String inpuAssess = "<input type='text' value='" + assessValue + "' name='" + assessId + "assess' id='" + containerId + assessId + "assess' class='tableTextInputChanged' style='font-size: 100%;'>";
		
		if(!isInputReality){
			inputReality = realityValue;
		}
		if(!isInputTarget){
			inputTarget = targetValue;
		}
		
		if(!isInputAssess){
			inpuAssess = assessValue;
		}
		
		String trendimgHtml = "";
		if(trendImg.equalsIgnoreCase("")){
			trendimgHtml = "";
		}else{
			trendimgHtml = "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + trendImg + "'/>";
		}	
		YearSunHtml.append("<tr onclick=\"Ext.getCmp('" + containerId + "').tablePanel.focus(this,'"+ containerId +"_quarter_table');\">\n");
		YearSunHtml.append("<td nowrap='true' class='cen' hi='1' rowspan='1' align='center'>年汇总</td>\n");
		YearSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen'class='cen'><div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + statusImg + "'/></td>\n");
		YearSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen' class='cen'>" + trendimgHtml + "</td>\n");
//		add by haojing  备注信息列
		String memoStr = "注释——"+memoKpiName+"年汇总";
		YearSunHtml.append("<td nowrap='true' align='center' hi='3'  class='cen'>" +
				"<input type='image' src='" + contextPath + imgPath+"' title='备注' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.getData('"+ kgrId +"','" + memoStr + "');\"/></td>\n");
//		add by haojing  备注信息列
		YearSunHtml.append("<td nowrap='true' align='center' class='cen' hi='3'>" + inputReality + "</td>\n");
		YearSunHtml.append("<td nowrap='true' align='center' class='cen' hi='3'>" + inputTarget + "</td>\n");
		YearSunHtml.append("<td nowrap='true' align='center' class='cen' hi='3'>" + inpuAssess + "</td>\n");
		
		if(!isEdit){
			if(!isInputReality && !isInputTarget && !isInputAssess){
				YearSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>" +
						"<input type='image' src='" + contextPath + "/images/icons/edit.gif' title='编辑' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneInput('yearId','"+ timeId +"');\"/></td>\n");
			}else{
				YearSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>");
				YearSunHtml.append("<input type='image' src='" + contextPath + "/images/icons/save.gif' title='保存' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneSave('" + timeId + "','" + kpiId + "','" + timeId + "',document.getElementById('" + containerId + realityId + "reality').value,document.getElementById('" + containerId +  targetId + "target').value,document.getElementById('" + containerId + assessId + "assess').value);\"/>");
				YearSunHtml.append("</td>\n");
			}
		}
		
		YearSunHtml.append("</tr>\n");
		
		return YearSunHtml.toString();
	}
	
	public static String getEndTableHtml(){
		StringBuffer endTableHtml = new StringBuffer();
		
		endTableHtml.append("</tbody></table></fieldset>\n");
		
		return endTableHtml.toString();
	}
}