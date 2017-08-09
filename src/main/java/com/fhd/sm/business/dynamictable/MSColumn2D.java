package com.fhd.sm.business.dynamictable;

import java.util.List;

import com.fhd.sm.web.form.KpiForm;

/**
 * FUNSIONCHARTS 
 *
 * @author   郝静
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-9-10		下午7:15:15
 *
 * @see 	 
 */
public class MSColumn2D {

	/**
	 * 得到XML
	 * 
	 * @author 郝静
	 * @param list 指标结果实体集合
	 * @return String
	*/
	public static String getXml(List<KpiForm> list) {
		StringBuffer xml = new StringBuffer();
		xml.append("<chart canvasBorderColor='C0C0C0' canvasBottomMargin='70' plotBorderColor='C0C0C0' showLegend='1' legendShadow='0' legendPosition='RIGHT' legendNumColumns='1' showAlternateHGridColor='0' bgColor='FFFFFF' showLabels='1' showvalues='0' decimals='2' placeValuesInside='1' rotateValues='1'>");
	
		for (KpiForm kpi : list) {
			if(kpi.getAssessmentValue()!=null&&!"".equals(kpi.getAssessmentValue())){
				xml.append("<dataset seriesName='目标值"+kpi.getTargetValue()+"'/></dataset>");
				xml.append("<dataset seriesName='实际值"+kpi.getFinishValue()+"'/></dataset>");
				xml.append("<dataset seriesName='上期实际值"+kpi.getPreFinishValue()+"'/></dataset>");
				xml.append("<dataset seriesName='去年同期值"+kpi.getPreYearFinishValue()+"'/></dataset>");
			}
		}
		xml.append("</chart>");
		
		return xml.toString();
	}
}