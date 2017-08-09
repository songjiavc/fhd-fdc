package com.fhd.sm.business.dynamictable;

import java.util.List;

import com.fhd.sm.web.form.KpiForm;

/**
 * FUNSIONCHARTS 
 *
 * @author   郝静
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-9-4		上午10:18:15
 *
 * @see 	 
 */
public class Bar2D {

	/**
	 * 得到XML
	 * 
	 * @author 郝静
	 * @param title 主标题
	 * @param timeTitle 副标题 
	 * @param list 指标结果实体集合
	 * @return String
	*/
	public static String getXml(List<KpiForm> list) {
		StringBuffer xml = new StringBuffer();
		xml.append("<chart canvasRightMargin='30' canvasBottomMargin='60' plotBorderColor='C0C0C0' showBorder='0' canvasBorderColor='C0C0C0' showAlternateHGridColor='0' showAlternateVGridColor='0' bgColor='FFFFFF' showValues='0'>");
	    
		if(null != list && list.size() > 0) {
			for (KpiForm kpi : list) {
				if(kpi.getAssessmentValue()!=null&&!"".equals(kpi.getAssessmentValue())&&!"0.00".equals(kpi.getAssessmentValue())){
					xml.append("<set label='"+kpi.getName()+"' value='"+kpi.getAssessmentValue()+"' toolText='评估值："+kpi.getAssessmentValue()+" 指标名称:"+kpi.getName()+"'/>");
				}else{
					xml.append("<set label='"+kpi.getName()+"' value='' />");
				}
				
			}
		}
		xml.append("</chart>");
		
		return xml.toString();
	}
}