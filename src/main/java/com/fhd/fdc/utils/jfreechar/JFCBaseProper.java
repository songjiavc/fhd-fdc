package com.fhd.fdc.utils.jfreechar;

import java.awt.Color;
import java.awt.Paint;

/**
 * jfc图形实体
 * 封装一些通用的图属性
 * @author 邓广义
 *
 */
public class JFCBaseProper {
	//饼图
	public static final String PIE = "pie";
	//柱状图
	public static final String HIS = "his";
	//折线图
	public static final String TS = "ts";
	
	
	public JFCBaseProper(){
		
		this.title = "图表";
		this.subTitle = "";
		this.miniGraph = true;
		this.XName = "X";
		this.YName = "Y";
		this.XPosition = "bottom";
		this.YPosition = "left";
		this.backgroundColor = Color.white;
		
	}
	//图形标题
	private String title;
	//图形子标题
	private String subTitle;
	//Y轴名称
	//default:Y
	private String YName;
	//X轴名称
	//default:X
	private String XName;
	//是否显示图片下方的小图例
	//default:true
	private boolean miniGraph;
	//Y轴在左还是右（"left","right"）
	//default:left
	private String YPosition;
	//X轴在上还是下（"top","bottom"）
	//default:bottom
	private String XPosition;
	//设置背景颜色默认
	private Paint backgroundColor;
	
	private float orchily;
	
	
	//getter、setter
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getYName() {
		return YName;
	}
	public void setYName(String yName) {
		YName = yName;
	}
	public String getXName() {
		return XName;
	}
	public void setXName(String xName) {
		XName = xName;
	}
	public boolean isMiniGraph() {
		return miniGraph;
	}
	public void setMiniGraph(boolean miniGraph) {
		this.miniGraph = miniGraph;
	}
	public String getYPosition() {
		return YPosition;
	}
	public void setYPosition(String yPosition) {
		YPosition = yPosition;
	}
	public String getXPosition() {
		return XPosition;
	}
	public void setXPosition(String xPosition) {
		XPosition = xPosition;
	}
	public Paint getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(Paint backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	
	
}
