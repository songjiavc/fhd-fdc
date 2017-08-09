package com.fhd.ra.business.assess.email;

/**
 * 风险评估邮件核心内容
 * */

public class HtmlUtil {

	public static String getStyle(){
		StringBuffer style = new StringBuffer();
		style.append("<style type=\"text/css\">");
		style.append("table.gridtable {");
		style.append("font-family: verdana,arial,sans-serif;");
		style.append("font-size:11px;");
		style.append("color:#333333;");
		style.append("border-width: 1px;");
		style.append("border-color: #666666;");
		style.append("border-collapse: collapse;");
		style.append("}");
		style.append("table.gridtable th {");
		style.append("border-width: 1px;");
		style.append("padding: 8px;");
		style.append(" border-style: solid;");
		style.append("border-color: #666666;");
		style.append("background-color: #dedede;");
		style.append("}");
		style.append("table.gridtable td {");
		style.append("border-width: 1px;");
		style.append("padding: 8px;");
		style.append("border-style: solid;");
		style.append("border-color: #666666;");
		style.append("background-color: #ffffff;");
		style.append("}");
		style.append("</style>");
		
		return style.toString();
	}
	
	public static String getStartTable(){
		String startHtml = "<table class=\"gridtable\">";
		return startHtml;
	}
	
	public static String getTh(){
		return "<tr><th>风险事件</th></tr>";
	}
	
	public static String getTwoTh(){
		return "<tr><th>部门名称</th><th>风险事件</th></tr>";
	}
	
	public static String getEndTable(){
		String endHtml = "</table>";
		return endHtml;
	}
}
