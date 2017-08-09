package com.fhd.comm.web.form.analysis;

import com.fhd.entity.comm.analysis.ThemeAnalysis;

/**
 * 主题分析form.
 * 主题分析布局方式：2*2，2*3，3*3
 * @author 吴德福
 * @since 2013-9-2
 */
public class ThemeAnalysisForm extends ThemeAnalysis{

	private static final long serialVersionUID = 1964317478298611948L;

	/*
	 * 布局中面板之间的宽度比和高度比--最多4*4
	 */
	//第一行宽度比
	private String oneWidthRatio;
	//第二行宽度比
	private String twoWidthRatio;
	//第三行宽度比
	private String threeWidthRatio;
	//第四行宽度比
	private String fourWidthRatio;
	//宽度比--只有layout0只有一个面板时使用.
	private String widthRatio;
	//高度比
	private String heightRatio;
	/*
	 * 布局中面板的宽度和高度--最多4*4项
	 */
	//第一行宽度和高度
	private String oneHeight;
	private String oneWidth;
	//第二行宽度和高度
	private String twoHeight;
	private String twoWidth;
	//第三行宽度和高度
	private String threeHeight;
	private String threeWidth;
	//第四行宽度和高度
	private String fourHeight;
	private String fourWidth;
	//第五行宽度和高度
	private String fiveHeight;
	private String fiveWidth;
	//第六行宽度和高度
	private String sixHeight;
	private String sixWidth;
	//第七行宽度和高度
	private String sevenHeight;
	private String sevenWidth;
	//第八行宽度和高度
	private String eightHeight;
	private String eightWidth;
	//第九行宽度和高度
	private String nineHeight;
	private String nineWidth;
	//第十行宽度和高度
	private String tenHeight;
	private String tenWidth;
	//第十一行宽度和高度
	private String elevenHeight;
	private String elevenWidth;
	//第十二行宽度和高度
	private String twelveHeight;
	private String twelveWidth;
	//第十三行宽度和高度
	private String thirteenHeight;
	private String thirteenWidth;
	//第十四行宽度和高度
	private String fourteenHeight;
	private String fourteenWidth;
	//第十五行宽度和高度
	private String fifteenHeight;
	private String fifteenWidth;
	//第十六行宽度和高度
	private String sixteenHeight;
	private String sixteenWidth;
	/*
	 * 布局中面板的id和name--最多4*4项
	 */
	//第一个面板的id
	private String onePanelId;
	private String onePanelName;
	//第二个面板的id
	private String twoPanelId;
	private String twoPanelName;
	//第三个面板的id
	private String threePanelId;
	private String threePanelName;
	//第四个面板的id
	private String fourPanelId;
	private String fourPanelName;
	//第五个面板的id
	private String fivePanelId;
	private String fivePanelName;
	//第六个面板的id
	private String sixPanelId;
	private String sixPanelName;
	//第七个面板的id
	private String sevenPanelId;
	private String sevenPanelName;
	//第八个面板的id
	private String eightPanelId;
	private String eightPanelName;
	//第九个面板的id
	private String ninePanelId;
	private String ninePanelName;
	//第十个面板的id
	private String tenPanelId;
	private String tenPanelName;
	//第十一个面板的id
	private String elevenPanelId;
	private String elevenPanelName;
	//第十二个面板的id
	private String twelvePanelId;
	private String twelvePanelName;
	//第十三个面板的id
	private String thirteenPanelId;
	private String thirteenPanelName;
	//第十四个面板的id
	private String fourteenPanelId;
	private String fourteenPanelName;
	//第十五个面板的id
	private String fifteenPanelId;
	private String fifteenPanelName;
	//第十六个面板的id
	private String sixteenPanelId;
	private String sixteenPanelName;
	
	public String getOneWidthRatio() {
		return oneWidthRatio;
	}
	public void setOneWidthRatio(String oneWidthRatio) {
		this.oneWidthRatio = oneWidthRatio;
	}
	public String getTwoWidthRatio() {
		return twoWidthRatio;
	}
	public void setTwoWidthRatio(String twoWidthRatio) {
		this.twoWidthRatio = twoWidthRatio;
	}
	public String getThreeWidthRatio() {
		return threeWidthRatio;
	}
	public void setThreeWidthRatio(String threeWidthRatio) {
		this.threeWidthRatio = threeWidthRatio;
	}
	public String getFourWidthRatio() {
		return fourWidthRatio;
	}
	public void setFourWidthRatio(String fourWidthRatio) {
		this.fourWidthRatio = fourWidthRatio;
	}
	public String getWidthRatio() {
		return widthRatio;
	}
	public void setWidthRatio(String widthRatio) {
		this.widthRatio = widthRatio;
	}
	public String getHeightRatio() {
		return heightRatio;
	}
	public void setHeightRatio(String heightRatio) {
		this.heightRatio = heightRatio;
	}
	public String getOneHeight() {
		return oneHeight;
	}
	public void setOneHeight(String oneHeight) {
		this.oneHeight = oneHeight;
	}
	public String getOneWidth() {
		return oneWidth;
	}
	public void setOneWidth(String oneWidth) {
		this.oneWidth = oneWidth;
	}
	public String getTwoHeight() {
		return twoHeight;
	}
	public void setTwoHeight(String twoHeight) {
		this.twoHeight = twoHeight;
	}
	public String getTwoWidth() {
		return twoWidth;
	}
	public void setTwoWidth(String twoWidth) {
		this.twoWidth = twoWidth;
	}
	public String getThreeHeight() {
		return threeHeight;
	}
	public void setThreeHeight(String threeHeight) {
		this.threeHeight = threeHeight;
	}
	public String getThreeWidth() {
		return threeWidth;
	}
	public void setThreeWidth(String threeWidth) {
		this.threeWidth = threeWidth;
	}
	public String getFourHeight() {
		return fourHeight;
	}
	public void setFourHeight(String fourHeight) {
		this.fourHeight = fourHeight;
	}
	public String getFourWidth() {
		return fourWidth;
	}
	public void setFourWidth(String fourWidth) {
		this.fourWidth = fourWidth;
	}
	public String getFiveHeight() {
		return fiveHeight;
	}
	public void setFiveHeight(String fiveHeight) {
		this.fiveHeight = fiveHeight;
	}
	public String getFiveWidth() {
		return fiveWidth;
	}
	public void setFiveWidth(String fiveWidth) {
		this.fiveWidth = fiveWidth;
	}
	public String getSixHeight() {
		return sixHeight;
	}
	public void setSixHeight(String sixHeight) {
		this.sixHeight = sixHeight;
	}
	public String getSixWidth() {
		return sixWidth;
	}
	public void setSixWidth(String sixWidth) {
		this.sixWidth = sixWidth;
	}
	public String getSevenHeight() {
		return sevenHeight;
	}
	public void setSevenHeight(String sevenHeight) {
		this.sevenHeight = sevenHeight;
	}
	public String getSevenWidth() {
		return sevenWidth;
	}
	public void setSevenWidth(String sevenWidth) {
		this.sevenWidth = sevenWidth;
	}
	public String getEightHeight() {
		return eightHeight;
	}
	public void setEightHeight(String eightHeight) {
		this.eightHeight = eightHeight;
	}
	public String getEightWidth() {
		return eightWidth;
	}
	public void setEightWidth(String eightWidth) {
		this.eightWidth = eightWidth;
	}
	public String getNineHeight() {
		return nineHeight;
	}
	public void setNineHeight(String nineHeight) {
		this.nineHeight = nineHeight;
	}
	public String getNineWidth() {
		return nineWidth;
	}
	public void setNineWidth(String nineWidth) {
		this.nineWidth = nineWidth;
	}
	public String getTenHeight() {
		return tenHeight;
	}
	public void setTenHeight(String tenHeight) {
		this.tenHeight = tenHeight;
	}
	public String getTenWidth() {
		return tenWidth;
	}
	public void setTenWidth(String tenWidth) {
		this.tenWidth = tenWidth;
	}
	public String getElevenHeight() {
		return elevenHeight;
	}
	public void setElevenHeight(String elevenHeight) {
		this.elevenHeight = elevenHeight;
	}
	public String getElevenWidth() {
		return elevenWidth;
	}
	public void setElevenWidth(String elevenWidth) {
		this.elevenWidth = elevenWidth;
	}
	public String getTwelveHeight() {
		return twelveHeight;
	}
	public void setTwelveHeight(String twelveHeight) {
		this.twelveHeight = twelveHeight;
	}
	public String getTwelveWidth() {
		return twelveWidth;
	}
	public void setTwelveWidth(String twelveWidth) {
		this.twelveWidth = twelveWidth;
	}
	public String getThirteenHeight() {
		return thirteenHeight;
	}
	public void setThirteenHeight(String thirteenHeight) {
		this.thirteenHeight = thirteenHeight;
	}
	public String getThirteenWidth() {
		return thirteenWidth;
	}
	public void setThirteenWidth(String thirteenWidth) {
		this.thirteenWidth = thirteenWidth;
	}
	public String getFourteenHeight() {
		return fourteenHeight;
	}
	public void setFourteenHeight(String fourteenHeight) {
		this.fourteenHeight = fourteenHeight;
	}
	public String getFourteenWidth() {
		return fourteenWidth;
	}
	public void setFourteenWidth(String fourteenWidth) {
		this.fourteenWidth = fourteenWidth;
	}
	public String getFifteenHeight() {
		return fifteenHeight;
	}
	public void setFifteenHeight(String fifteenHeight) {
		this.fifteenHeight = fifteenHeight;
	}
	public String getFifteenWidth() {
		return fifteenWidth;
	}
	public void setFifteenWidth(String fifteenWidth) {
		this.fifteenWidth = fifteenWidth;
	}
	public String getSixteenHeight() {
		return sixteenHeight;
	}
	public void setSixteenHeight(String sixteenHeight) {
		this.sixteenHeight = sixteenHeight;
	}
	public String getSixteenWidth() {
		return sixteenWidth;
	}
	public void setSixteenWidth(String sixteenWidth) {
		this.sixteenWidth = sixteenWidth;
	}
	public String getOnePanelId() {
		return onePanelId;
	}
	public void setOnePanelId(String onePanelId) {
		this.onePanelId = onePanelId;
	}
	public String getOnePanelName() {
		return onePanelName;
	}
	public void setOnePanelName(String onePanelName) {
		this.onePanelName = onePanelName;
	}
	public String getTwoPanelId() {
		return twoPanelId;
	}
	public void setTwoPanelId(String twoPanelId) {
		this.twoPanelId = twoPanelId;
	}
	public String getTwoPanelName() {
		return twoPanelName;
	}
	public void setTwoPanelName(String twoPanelName) {
		this.twoPanelName = twoPanelName;
	}
	public String getThreePanelId() {
		return threePanelId;
	}
	public void setThreePanelId(String threePanelId) {
		this.threePanelId = threePanelId;
	}
	public String getThreePanelName() {
		return threePanelName;
	}
	public void setThreePanelName(String threePanelName) {
		this.threePanelName = threePanelName;
	}
	public String getFourPanelId() {
		return fourPanelId;
	}
	public void setFourPanelId(String fourPanelId) {
		this.fourPanelId = fourPanelId;
	}
	public String getFourPanelName() {
		return fourPanelName;
	}
	public void setFourPanelName(String fourPanelName) {
		this.fourPanelName = fourPanelName;
	}
	public String getFivePanelId() {
		return fivePanelId;
	}
	public void setFivePanelId(String fivePanelId) {
		this.fivePanelId = fivePanelId;
	}
	public String getFivePanelName() {
		return fivePanelName;
	}
	public void setFivePanelName(String fivePanelName) {
		this.fivePanelName = fivePanelName;
	}
	public String getSixPanelId() {
		return sixPanelId;
	}
	public void setSixPanelId(String sixPanelId) {
		this.sixPanelId = sixPanelId;
	}
	public String getSixPanelName() {
		return sixPanelName;
	}
	public void setSixPanelName(String sixPanelName) {
		this.sixPanelName = sixPanelName;
	}
	public String getSevenPanelId() {
		return sevenPanelId;
	}
	public void setSevenPanelId(String sevenPanelId) {
		this.sevenPanelId = sevenPanelId;
	}
	public String getSevenPanelName() {
		return sevenPanelName;
	}
	public void setSevenPanelName(String sevenPanelName) {
		this.sevenPanelName = sevenPanelName;
	}
	public String getEightPanelId() {
		return eightPanelId;
	}
	public void setEightPanelId(String eightPanelId) {
		this.eightPanelId = eightPanelId;
	}
	public String getEightPanelName() {
		return eightPanelName;
	}
	public void setEightPanelName(String eightPanelName) {
		this.eightPanelName = eightPanelName;
	}
	public String getNinePanelId() {
		return ninePanelId;
	}
	public void setNinePanelId(String ninePanelId) {
		this.ninePanelId = ninePanelId;
	}
	public String getNinePanelName() {
		return ninePanelName;
	}
	public void setNinePanelName(String ninePanelName) {
		this.ninePanelName = ninePanelName;
	}
	public String getTenPanelId() {
		return tenPanelId;
	}
	public void setTenPanelId(String tenPanelId) {
		this.tenPanelId = tenPanelId;
	}
	public String getTenPanelName() {
		return tenPanelName;
	}
	public void setTenPanelName(String tenPanelName) {
		this.tenPanelName = tenPanelName;
	}
	public String getElevenPanelId() {
		return elevenPanelId;
	}
	public void setElevenPanelId(String elevenPanelId) {
		this.elevenPanelId = elevenPanelId;
	}
	public String getElevenPanelName() {
		return elevenPanelName;
	}
	public void setElevenPanelName(String elevenPanelName) {
		this.elevenPanelName = elevenPanelName;
	}
	public String getTwelvePanelId() {
		return twelvePanelId;
	}
	public void setTwelvePanelId(String twelvePanelId) {
		this.twelvePanelId = twelvePanelId;
	}
	public String getTwelvePanelName() {
		return twelvePanelName;
	}
	public void setTwelvePanelName(String twelvePanelName) {
		this.twelvePanelName = twelvePanelName;
	}
	public String getThirteenPanelId() {
		return thirteenPanelId;
	}
	public void setThirteenPanelId(String thirteenPanelId) {
		this.thirteenPanelId = thirteenPanelId;
	}
	public String getThirteenPanelName() {
		return thirteenPanelName;
	}
	public void setThirteenPanelName(String thirteenPanelName) {
		this.thirteenPanelName = thirteenPanelName;
	}
	public String getFourteenPanelId() {
		return fourteenPanelId;
	}
	public void setFourteenPanelId(String fourteenPanelId) {
		this.fourteenPanelId = fourteenPanelId;
	}
	public String getFourteenPanelName() {
		return fourteenPanelName;
	}
	public void setFourteenPanelName(String fourteenPanelName) {
		this.fourteenPanelName = fourteenPanelName;
	}
	public String getFifteenPanelId() {
		return fifteenPanelId;
	}
	public void setFifteenPanelId(String fifteenPanelId) {
		this.fifteenPanelId = fifteenPanelId;
	}
	public String getFifteenPanelName() {
		return fifteenPanelName;
	}
	public void setFifteenPanelName(String fifteenPanelName) {
		this.fifteenPanelName = fifteenPanelName;
	}
	public String getSixteenPanelId() {
		return sixteenPanelId;
	}
	public void setSixteenPanelId(String sixteenPanelId) {
		this.sixteenPanelId = sixteenPanelId;
	}
	public String getSixteenPanelName() {
		return sixteenPanelName;
	}
	public void setSixteenPanelName(String sixteenPanelName) {
		this.sixteenPanelName = sixteenPanelName;
	}
}