package com.fhd.fdc.utils;
/**
 * 报告标签常量类
 * 报告中内嵌表格的一些header（表头）
 * @author 邓广义
 *
 */
public class ReportTagConstant {

	public ReportTagConstant() {
		
	}
	//公用的表头 列头 表结束 列尾
	public static String TABLE_START = "<table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" border=\"1\" bordercolor=\"#000000\"><tbody>";
	public static String TABLE_END = "</tbody></table>";
	public static String TR_START = "<tr>";
	public static String TR_END = "</tr>";
	public static String HEADER_TD_START = "<td style=\"background:#ffffff;\" valign=\"top\"><p><span style=\"font-family:宋体;background:#ffffff;font-size:12pt;\">";
	public static String HEADER_TD_END = "</span></p></td>";
	public static String BODY_TD_START = "<td valign=\"top\"><p><span style=\"font-family:宋体;font-size:12pt;\">";
	public static String BODY_TD_END = "</span></p></td>";
	//新高风险列表
	public static String TABLE_NAME_RISK_ASSESS_RESULT = "<tr><td colspan=\"8\"><p style=\"text-align:center;\" align=\"center\"><b><span style=\"font-size:12pt;font-family:宋体;color:black;\">新高风险列表</span></b></p></td></tr>";
	//风险事件排序
	public static String TABLE_NAME_RISK_EVENT_ORDER = "<tr><td colspan=\"8\"><p style=\"text-align:center;\" align=\"center\"><b><span style=\"font-size:12pt;font-family:宋体;color:black;\">风险事件列表</span></b></p></td></tr>";
	
	public static String TABLE_NAME_RISK_ORDER = "<tr><td colspan=\"8\"><p style=\"text-align:center;\" align=\"center\"><b><span style=\"font-size:12pt;font-family:宋体;color:black;\">风险列表</span></b></p></td></tr>";
	
}
