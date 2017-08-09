package com.fhd.sm.business.dynamictable;

import java.util.Calendar;
import java.util.List;

import com.fhd.entity.kpi.KpiGatherResult;

/**
 * FUNSIONCHARTS 饼图
 * 
 * @author haojing
 * @version
 * @since Ver 1.1
 * 
 * @see
 */
public class Doughnut3D {

    
	/**
	 * 得到XML
	 * 
	 * @return String
	 */
	public static String getXml(String name,List<KpiGatherResult> list) {

		StringBuffer xml = new StringBuffer();

		for(int i = 0 ; i<list.size() ; i++){
			KpiGatherResult kpiGatherResult = list.get(i);
			if(i==list.size()-1){
				if("0frequecy_week".equalsIgnoreCase(kpiGatherResult.getKpi().getGatherFrequence().getId())){
					if(getMonth().equalsIgnoreCase(kpiGatherResult.getTimePeriod().getMonth())){
						xml.append("<set label='"+name +"'value='" + kpiGatherResult.getFinishValue() + "' />");
					}
				}else{
					xml.append("<set label='"+name +"'value='" + kpiGatherResult.getFinishValue() + "' />");
				}
			}
		}
		return xml.toString();
	}
	
	/**
	 * 得到XML
	 * 
	 * @return String
	 */
	public static String getAllXml(String xmls) {
		StringBuffer xml = new StringBuffer();
		xml.append("<chart legendShadow='0' chartBottomMargin='50' bgAlpha='30,100' ");
		xml.append("bgAngle='45' pieYScale='50' startingAngle='175'  smartLineColor='7D8892' legendBorderColor='7D8892'");
		xml.append("smartLineThickness='2' baseFontSize='12' manageLabelOverflow='1' legendPosition='RIGHT' showLegend='1' legendShadow='0' showPlotBorder='1' >");
		xml.append(xmls);
		xml.append("<styles> ");
		xml.append("<definition>");
		xml.append("<style name='CaptionFont' type='FONT' face='Verdana' size='11' color='7D8892' bold='0' />");
		xml.append("<style name='LabelFont' type='FONT' bold='0'/>");
		xml.append("</definition>");
		xml.append("<application>");
		xml.append("<apply toObject='DATALABELS' styles='LabelFont' />");
		xml.append("<apply toObject='CAPTION' styles='CaptionFont' />");
		xml.append("</application>");
		xml.append("</styles>");
		xml.append("</chart>");
		
		return xml.toString();
	}
	
	private static String getMonth(){
		Calendar cal = Calendar.getInstance();
	    int month = cal.get(Calendar.MONTH) + 1;
	    return String.valueOf(month);
	}
}