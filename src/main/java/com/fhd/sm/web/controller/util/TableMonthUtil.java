package com.fhd.sm.web.controller.util;

public class TableMonthUtil {

    public static String getStartTableHtml(boolean isEdit, String kpiName,String containerId) {
        StringBuffer startTableHtml = new StringBuffer();
        startTableHtml
                .append("<fieldset style='margin-left:10px;margin-right:10px;' class='x-fieldset x-fieldset-with-title x-fieldset-with-legend x-fieldset-default'>\n");
        startTableHtml.append("<legend class='x-fieldset-header x-fieldset-header-default'>" + kpiName + "</legend>\n");
        startTableHtml.append("<table id='"+ containerId +"_month_table' width='100%' height='80%' border='1' cellpadding='0' cellspacing='0' class='fhd_add'>\n");
        startTableHtml.append("<tbody></tr><tr><td class='cen' width='5%' align='center'>年份</td>\n");
        startTableHtml.append("<td width='5%' class='cen' align='center'>季度</td><td class='cen' width='5%'>月份</td>\n");
        startTableHtml.append("<td width='5%' class='cen' align='center'>状态</td>\n");
        startTableHtml.append("<td width='5%' class='cen' align='center'>趋势</td>\n");
//		add by haojing  备注信息列
		startTableHtml.append("<td class='cen' width='5%' align='center'>注释</td>\n");
//		add by haojing  备注信息列
        startTableHtml.append("<td class='cen'width='20%' align='center'>实际值</td>\n");
        startTableHtml.append("<td class='cen' width='20%' align='center'>目标值</td>\n");
        startTableHtml.append("<td class='cen' width='20%' align='center'>评估值</td>");
        if (!isEdit) {
            startTableHtml.append("<td class='cen' width='5%' align='center'>操作</td>\n");
        }
        startTableHtml.append("</tr>\n");

        return startTableHtml.toString();
    }

    public static String getYear(String year,String containerId) {
        StringBuffer yearHtml = new StringBuffer();
        yearHtml.append("<tr yearrowspan='1' onclick=\" Ext.getCmp('" + containerId + "').tablePanel.focusmonth(this,'"+ containerId +"_month_table');\">\n");
        yearHtml.append("<td nowrap='true'  hi='1' rowspan='22'>" + year + "</td>\n");
        //yearHtml.append("</tr>\n");

        return yearHtml.toString();
    }

    public static String getQuarterHtml(String quarter) {
        StringBuffer quarterHtml = new StringBuffer();

        quarterHtml.append("<td nowrap='true'  hi='1' rowspan='3' align='center'>第" + quarter + "季</td>\n");

        return quarterHtml.toString();
    }

    public static String getMonthHtml(String contextPath, String yearId, boolean isEdit, String kpiId, String timeId, String month,
            boolean isInputReality, boolean isInputTarget, boolean isInputAssess, String inputId, String inputName, String realityValue,
            String targetValue, String assessValue, String statusImg, String trendImg,String kgrId,String imgPath,String memoKpiName,String containerId,int scale) {
        realityValue = Utils.getValue(realityValue,scale);
        targetValue = Utils.getValue(targetValue,scale);
        assessValue = Utils.getValue(assessValue,scale);

        StringBuffer monthHtml = new StringBuffer();
        //实际
        String inputReality = "<input type='text' value='" + realityValue + "' name='" + inputName + "reality' id='" + containerId + inputId
                + "reality' style='font-size: 100%;'>";
        //目标
        String inputTarget = "<input type='text' value='" + targetValue + "' name='" + inputName + "target' id='"+ containerId + inputId
                + "target' style='font-size: 100%;'>";
        //评估
        String inpuAssess = "<input type='text' value='" + assessValue + "' name='" + inputName + "assess' id='"+ containerId + inputId
                + "assess'  style='font-size: 100%;'>";

        if (!isInputReality) {
            inputReality = realityValue;
        }
        if (!isInputTarget) {
            inputTarget = targetValue;
        }

        if (!isInputAssess) {
            inpuAssess = assessValue;
        }

        String trendimgHtml = "";
        if (trendImg.equalsIgnoreCase("")) {
            trendimgHtml = "";
        }
        else {
            trendimgHtml = "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + trendImg
                    + "'/>";
        }
        if(!"1".equals(month)&&!"4".equals(month)&&!"7".equals(month)&&!"10".equals(month)){
          monthHtml.append("<tr onclick=\" Ext.getCmp('" + containerId + "').tablePanel.focusmonth(this,'"+ containerId +"_month_table');\">\n");
        }
         
        monthHtml.append("<td nowrap='true' align='center'>" + month + "</td>\n");
        monthHtml
                .append("<td nowrap='true' align='center'><div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='"
                        + statusImg + "'/></td>\n");
        monthHtml.append("<td nowrap='true' align='center' class='normalTabLevel3' hi='3'>" + trendimgHtml + "</td>\n");
//		add by haojing  备注信息列
		String memoStr = "注释——"+memoKpiName+"("+yearId+"年" + month + "月)";
		monthHtml.append("<td nowrap='true' align='center' hi='3'>" +
				"<input type='image' src='" + contextPath + imgPath+"' title='备注' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.getData('"+ kgrId +"','" + memoStr + "')\"/></td>\n");
//		add by haojing  备注信息列
        monthHtml.append("<td nowrap='true' align='center' hi='3'>" + inputReality + "</td>\n");
        monthHtml.append("<td nowrap='true' align='center' hi='3'>" + inputTarget + "</td>\n");
        monthHtml.append("<td nowrap='true' align='center' hi='3'>" + inpuAssess + "</td>\n");
        if (!isEdit) {
            if (!isInputReality && !isInputTarget && !isInputAssess) {
                monthHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>" + "<input type='image' src='"
                        + contextPath + "/images/icons/edit.gif' title='编辑' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneInput('" + timeId + "','" + yearId
                        + "');\"/></td>\n");
            }
            else {
                monthHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>");
                monthHtml.append("<input type='image' src='" + contextPath
                        + "/images/icons/save.gif' title='保存' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneSave('" + yearId + "','" + kpiId + "','" + timeId
                        + "',document.getElementById('"+ containerId + inputId + "reality').value,document.getElementById('" + containerId + inputId
                        + "target').value,document.getElementById('" + containerId + inputId + "assess').value);\"/>");
                monthHtml.append("</td>\n");
            }
        }
        monthHtml.append("</tr>\n");

        return monthHtml.toString();
    }

    public static String getQuarterSunHtml(String contextPath, String yearId, boolean isEdit, String kpiId, String timeId, boolean isInputReality,
            boolean isInputTarget, boolean isInputAssess, String inputId, String inputName, String realityValue, String targetValue,
            String assessValue, String statusImg, String trendImg, boolean isShow,String kgrId,String imgPath,String memoKpiName,String containerId,int scale) {
        realityValue = Utils.getValue(realityValue,scale);
        targetValue = Utils.getValue(targetValue,scale);
        assessValue = Utils.getValue(assessValue,scale);

        StringBuffer quarterSunHtml = new StringBuffer();

        //实际
        String inputReality = "<input type='text' value='" + realityValue + "' name='" + inputName + "reality' id='" + containerId + inputId
                + "reality' style='font-size: 100%;'>";
        //目标
        String inputTarget = "<input type='text' value='" + targetValue + "' name='" + inputName + "target' id='" + containerId + inputId
                + "target' style='font-size: 100%;'>";
        //评估
        String inpuAssess = "<input type='text' value='" + assessValue + "' name='" + inputName + "assess' id='" + containerId + inputId
                + "assess' style='font-size: 100%;'>";

        if (!isInputReality) {
            inputReality = realityValue;
        }
        if (!isInputTarget) {
            inputTarget = targetValue;
        }

        if (!isInputAssess) {
            inpuAssess = assessValue;
        }

        String trendimgHtml = "";
        if (trendImg.equalsIgnoreCase("")) {
            trendimgHtml = "";
        }
        else {
            trendimgHtml = "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + trendImg
                    + "'/>";
        }

        quarterSunHtml.append("<tr>\n");
        quarterSunHtml.append("<td nowrap='true' hi='1' colspan='2' class='cen' align='center'>季汇总</td>\n");
        quarterSunHtml
                .append("<td nowrap='true' align='center' class='cen'><div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='"
                        + statusImg + "'/></td>\n");
        quarterSunHtml.append("<td nowrap='true' align='center' class='cen' hi='3'>" + trendimgHtml + "</td>\n");
//		add by haojing  备注信息列
		String memoStr = "注释——"+memoKpiName+"季汇总";
		quarterSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>" +
				"<input type='image' src='" + contextPath + imgPath+"' title='备注' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.getData('"+ kgrId +"','" + memoStr + "');\"/></td>\n");
//		add by haojing  备注信息列
        quarterSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen'>" + inputReality + "</td>\n");
        quarterSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen'>" + inputTarget + "</td>\n");
        quarterSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen'>" + inpuAssess + "</td>\n");

        if(isShow){
	        if (!isEdit) {
	            if (!isInputReality && !isInputTarget && !isInputAssess) {
	                quarterSunHtml.append("<td nowrap='true' align='center' hi='3'  class='cen'>" + "<input type='image' src='"
	                        + contextPath + "/images/icons/edit.gif' title='编辑' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneInput('" + timeId + "','" + yearId
	                        + "');\"/></td>\n");
	            }
	            else {
	                quarterSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>");
	                quarterSunHtml.append("<input type='image' src='" + contextPath
	                        + "/images/icons/save.gif' title='保存' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneSave('" + yearId + "','" + kpiId + "','" + timeId
	                        + "',document.getElementById('" + containerId + inputId + "reality').value,document.getElementById('"+ containerId + inputId
	                        + "target').value,document.getElementById('"+ containerId+ inputId + "assess').value);\"/>");
	                quarterSunHtml.append("</td>\n");
	            }
	        }
        }else{
        	quarterSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>" + "&nbsp;</td>\n");
        }

        quarterSunHtml.append("</tr>\n");

        return quarterSunHtml.toString();
    }

    public static String getYearSunHtml(String contextPath, boolean isEdit, String kpiId, String timeId, boolean isInputReality,
            boolean isInputTarget, boolean isInputAssess, String realityId, String targetId, String assessId, String realityValue,
            String targetValue, String assessValue, String statusImg, String trendImg,String kgrId,String imgPath,String memoKpiName,String containerId,int scale) {

        realityValue = Utils.getValue(realityValue,scale);
        targetValue = Utils.getValue(targetValue,scale);
        assessValue = Utils.getValue(assessValue,scale);

        StringBuffer YearSunHtml = new StringBuffer();

        //实际
        String inputReality = "<input type='text' value='" + realityValue + "' name='" + realityId + "reality' id='" + containerId + realityId
                + "reality'style='size: 100%;'>";
        //目标
        String inputTarget = "<input type='text' value='" + targetValue + "' name='" + targetId + "target' id='" + containerId+ targetId
                + "target' style='font-size: 100%;'>";
        //评估
        String inpuAssess = "<input type='text' value='" + assessValue + "' name='" + assessId + "assess' id='"+ containerId + assessId
                + "assess' style='font-size: 100%;'>";

        if (!isInputReality) {
            inputReality = realityValue;
        }
        if (!isInputTarget) {
            inputTarget = targetValue;
        }

        if (!isInputAssess) {
            inpuAssess = assessValue;
        }

        String trendimgHtml = "";
        if (trendImg.equalsIgnoreCase("")) {
            trendimgHtml = "";
        }
        else {
            trendimgHtml = "<div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='" + trendImg
                    + "'/>";
        }

        YearSunHtml.append("<tr>\n");
        YearSunHtml.append("<td nowrap='true' hi='1' colspan='2' class='cen' align='center'>年汇总</td>\n");
        YearSunHtml
                .append("<td nowrap='true' align='center' class='cen'><div style='width: 32px; height: 19px; background-repeat: no-repeat;background-position: center top;' class='"
                        + statusImg + "'/></td>\n");
        YearSunHtml.append("<td nowrap='true' align='center' class='cen' hi='3'>" + trendimgHtml + "</td>\n");
//		add by haojing  备注信息列
		String memoStr = "注释——"+memoKpiName+"年汇总";
		YearSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>" +
				"<input type='image' src='" + contextPath + imgPath+"' title='备注' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.getData('"+ kgrId +"','" + memoStr + "');\"/></td>\n");
//		add by haojing  备注信息列
        YearSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen'>" + inputReality + "</td>\n");
        YearSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen'>" + inputTarget + "</td>\n");
        YearSunHtml.append("<td nowrap='true' align='center' hi='2'  class='cen'>" + inpuAssess + "</td>\n");

        if (!isEdit) {
            if (!isInputReality && !isInputTarget && !isInputAssess) {
                YearSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>" + "<input type='image' src='"
                        + contextPath + "/images/icons/edit.gif' title='编辑' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneInput('yearId','" + timeId
                        + "');\"/></td>\n");
            }
            else {
                YearSunHtml.append("<td nowrap='true' align='center' hi='3' class='cen'>");
                YearSunHtml.append("<input type='image' src='" + contextPath
                        + "/images/icons/save.gif' title='保存' onclick=\"Ext.getCmp('" + containerId + "').tablePanel.oneSave('" + timeId + "','" + kpiId + "','" + timeId
                        + "',document.getElementById('" + containerId + realityId + "reality').value,document.getElementById('" + containerId+ targetId
                        + "target').value,document.getElementById('" + containerId + assessId + "assess').value);\"/>");
                YearSunHtml.append("</td>\n");
            }
        }

        YearSunHtml.append("</tr>");

        return YearSunHtml.toString();
    }

    public static String getEndTableHtml() {
        StringBuffer endTableHtml = new StringBuffer();

        endTableHtml.append("</tbody></table></fieldset>\n");

        return endTableHtml.toString();
    }
}