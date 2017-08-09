package com.fhd.sm.business.chart;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fhd.entity.comm.AlarmRegion;

public class FAngularGaugeChart {
    public static final String COLOR_R = "FF654F";

    public static final String COLOR_Y = "ffe600";

    public static final String COLOR_G = "8BBA00";

    public static String getXml(List<AlarmRegion> AlarmRegionList, Double assessValue, String name, String id, boolean hasChild, String ctx,String url) {
        StringBuffer xml = new StringBuffer();
        xml.append("<chart baseFontSize='20' gaugeOriginX='200' gaugeOriginY='250' manageResize='1' autoScale='1' bgColor='ffffff' lowerLimit='0' upperLimit='100'  showBorder='0'   gaugeFillMix='{dark-10},FFFFFF,{dark-10}' gaugeFillRatio='3' ");
        if (hasChild) {
//            xml.append(" clickUrl='").append(ctx).append(url + "'>");
            xml.append(" clickURL='").append("JavaScript: analyseStrategyMap(\""+id+"\"); ").append("'>");
        }
        else {
            xml.append(" >");
        }

        xml.append("<colorRange>");
        for (AlarmRegion alarmRegion : AlarmRegionList) {
            if (StringUtils.isNotBlank(alarmRegion.getAlarmIcon().getId())) {
                if ("0alarm_startus_h".equals(alarmRegion.getAlarmIcon().getId())) {
                    xml.append("<color minValue='" + alarmRegion.getMinValue() + "' maxValue='" + alarmRegion.getMaxValue() + "' code='" + COLOR_R
                            + "'/>");
                }
                else if ("0alarm_startus_m".equals(alarmRegion.getAlarmIcon().getId())) {
                    xml.append("<color minValue='" + alarmRegion.getMinValue() + "' maxValue='" + alarmRegion.getMaxValue() + "' code='" + COLOR_Y
                            + "'/>");
                }
                else if ("0alarm_startus_l".equals(alarmRegion.getAlarmIcon().getId())) {
                    xml.append("<color minValue='" + alarmRegion.getMinValue() + "' maxValue='" + alarmRegion.getMaxValue() + "' code='" + COLOR_G
                            + "'/>");
                }
            }
        }
        xml.append("</colorRange>");
        xml.append("<dials>");
        xml.append("<dial value='" + assessValue + "'    rearExtension='10'/>");
        xml.append("</dials>");
        xml.append("<annotations>");
        xml.append("<annotationGroup>");
        xml.append("<annotation type='text' x='200' y='40'   label='" + name +"' align='center' bold='1' color='#3d7f9f' size='27'/>");
        xml.append("</annotationGroup>");
        xml.append("</annotations>");
        xml.append("</chart>");

        return xml.toString();
    }
}
