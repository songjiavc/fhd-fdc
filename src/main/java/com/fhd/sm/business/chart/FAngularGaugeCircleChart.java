package com.fhd.sm.business.chart;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fhd.entity.comm.AlarmRegion;

public class FAngularGaugeCircleChart {
    public static final String COLOR_R = "FF654F";

    public static final String COLOR_Y = "ffe600";

    public static final String COLOR_G = "8BBA00";

    public static String getXml(List<AlarmRegion> AlarmRegionList, Double assessValue, String name, String id, boolean hasChild, String ctx,String url) {
        StringBuffer xml = new StringBuffer();
        xml.append("<chart baseFontSize='20' manageResize='1' autoScale='1' lowerLimit='0' origW='220' origH='220' upperLimit='100' gaugeScaleAngle='360' minorTMNumber='0' majorTMNumber='0' showBorder='0' ");
        xml.append(" gaugeOuterRadius='80' gaugeInnerRadius='55' gaugeOriginX='130' gaugeOriginY='140'  pivotRadius='1' showGaugeBorder='0' ");
        xml.append(" majorTMColor='ffffff'  placeValuesInside='1' displayValueDistance='999' ");
        xml.append(" basefontColor='000000' toolTipBgColor='FFFFFF'  showShadow='0' bgColor='FFFFFF' ");
        
        if (hasChild) {
           // xml.append(" ClickUrl='").append(ctx).append(url + "'>");
            xml.append(" clickURL='").append("JavaScript: analyseKpi(\""+id+"\"); ").append("'>");
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
        xml.append("<dial showValue='1' value='" + assessValue + "'    rearExtension='10' baseWidth='10'/>");
        xml.append("</dials>");
        xml.append("<annotations>");
        xml.append("<annotationGroup>");
        xml.append("<annotation type='text' x='170' y='30'   label='" + name +"' align='center' bold='1' color='#3d7f9f' size='27'/>");
        xml.append("</annotationGroup>");
        xml.append("</annotations>");
        xml.append("</chart>");

        return xml.toString();
    }
}
