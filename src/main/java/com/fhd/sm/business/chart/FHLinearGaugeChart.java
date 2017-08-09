package com.fhd.sm.business.chart;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fhd.entity.comm.AlarmRegion;

public class FHLinearGaugeChart {
    public static final String COLOR_R = "FF654F";

    public static final String COLOR_Y = "ffe600";

    public static final String COLOR_G = "8BBA00";

    public static String getXml(List<AlarmRegion> AlarmRegionList, Double assessValue, String name, String id, boolean hasChild, String ctx,String url) {
        StringBuffer chartBuf = new StringBuffer();
        chartBuf.append("<chart   baseFontSize='15'   manageResize='1' autoScale='1' bgColor='ffffff' showBorder='0'  lowerLimit='0' upperLimit='100'  palette='1'  chartRightMargin='20'  chartTopMargin='25'  ");
        if (hasChild) {
            chartBuf.append(" clickURL='").append("JavaScript: analyseScorecard(\""+id+"\"); ").append("'>");
         }
         else {
            chartBuf.append(" >");
         }
        chartBuf.append("<colorRange>");
        for (AlarmRegion alarmRegion : AlarmRegionList) {
            if (StringUtils.isNotBlank(alarmRegion.getAlarmIcon().getId())) {
                if ("0alarm_startus_h".equals(alarmRegion.getAlarmIcon().getId())) {
                    chartBuf.append("<color minValue='" + alarmRegion.getMinValue() + "' maxValue='" + alarmRegion.getMaxValue() + "' code='" + COLOR_R
                            + "'/>");
                }
                else if ("0alarm_startus_m".equals(alarmRegion.getAlarmIcon().getId())) {
                    chartBuf.append("<color minValue='" + alarmRegion.getMinValue() + "' maxValue='" + alarmRegion.getMaxValue() + "' code='" + COLOR_Y
                            + "'/>");
                }
                else if ("0alarm_startus_l".equals(alarmRegion.getAlarmIcon().getId())) {
                    chartBuf.append("<color minValue='" + alarmRegion.getMinValue() + "' maxValue='" + alarmRegion.getMaxValue() + "' code='" + COLOR_G
                            + "'/>");
                }
            }
        }
        chartBuf.append("</colorRange>");
        chartBuf.append("<pointers> ");
        chartBuf.append("<pointer value='").append(assessValue).append("'/>  ");
        chartBuf.append("</pointers>");
        chartBuf.append("<annotations>");
        chartBuf.append("<annotationGroup>");
        chartBuf.append("<annotation type='text' x='200' y='10'   label='" + name +"' align='center' bold='1' color='#3d7f9f' size='17'/>");
        chartBuf.append("</annotationGroup>");
        chartBuf.append("</annotations>");
        chartBuf.append("</chart>");
        return chartBuf.toString();
    }
}
