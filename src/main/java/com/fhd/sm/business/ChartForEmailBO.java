package com.fhd.sm.business;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.fhd.comm.business.TimePeriodBO;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.templatemanage.TemplateManage;
import com.fhd.fdc.commons.email.SimpleMailService;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.web.controller.util.Utils;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.templatemanage.TemplateManageBO;

@Service
public class ChartForEmailBO {

    @Autowired
    private KpiBO o_kpiBO;
    
	@Autowired
	private KpiTableBO o_kpiTableBO;
	
	
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	
    @Autowired
    private EmpGridBO o_empGridBO;
    
    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
    
    
    @Autowired
    private DictBO o_dictBO;
    
    @Autowired
    private TemplateManageBO  o_templateManageBO;
    

    private static final String  IS_EMAIL = "send_email_assess_kpi_gather";
    
    private static final String IS_VALUE_CHANGE_EMAIL = "send_email_assess_kpi_change";
    
    private static final Log logger = LogFactory.getLog(ChartForEmailBO.class);
	
    
	/**获得文件路径
	 * @param kpiid 指标id
	 * @return
	 */
	public String getFilePath(String kpiid) {
		
		String frequecy = "";
		String year = "";
		Kpi kpi = o_kpiBO.findKpiById(kpiid);
		String filePath = "";

        if (null!=kpi) {
        	String kpiname = kpi.getName();
            DictEntry gatherFrequence = kpi.getGatherFrequence();
            if (gatherFrequence != null) {
                frequecy = gatherFrequence.getId();
                if (StringUtils.isNotBlank(frequecy)) {
                    if (kpi.getLastTimePeriod() != null) {
                        year = kpi.getLastTimePeriod().getId().subSequence(0, 4).toString();
                    }
                    else {
                        year = this.getYear();
                    }
                    
                    try {
                        if (!"".equalsIgnoreCase(year)) {
                            if ("0frequecy_week".equalsIgnoreCase(frequecy)) {
                            	filePath = getWeekChart(kpiid,kpiname,year);
                            }
                            else if ("0frequecy_month".equalsIgnoreCase(frequecy)) {
                            	filePath = getMonthChart(kpiid,kpiname,year);
                            }
                            else if ("0frequecy_quarter".equalsIgnoreCase(frequecy)) {
                            	filePath = getQuarterChart(kpiid,kpiname,year);
                            }
                            else if ("0frequecy_halfyear".equalsIgnoreCase(frequecy)) {
                            	filePath = getHalfYearChart(kpiid,kpiname,year);
                            }
                            else if ("0frequecy_year".equalsIgnoreCase(frequecy)) {
                            	filePath = getYearChart(kpiid,kpiname,year);
                            }
                        }
					} catch (IOException e) {
						logger.error("获得文件路径异常:["+e.toString()+"]");
						e.printStackTrace();
					}
                }
            }
	
        }	return filePath;
	}
	
    /**获得当前年
     * @return
     */
    private String getYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return String.valueOf(year);
    }
    
    
    /**获得月度指标图片path
     * @param kpiid 指标id
     * @param kpiname 指标名称
     * @param year 年
     * @return
     * @throws IOException
     */
    public String getMonthChart(String kpiid,String kpiname,String year) throws IOException{
    	String filePath = "";
    	Map<String, KpiGatherResult> map = o_kpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiid);
    	List<TimePeriod> monthsList = o_kpiTableBO.findTimePeriodById(year, "0frequecy_month");
	    KpiGatherResult kpiGatherResultMonth = null;
	    
	  	TimeSeries timeSeries = new TimeSeries(year);
    	TimeSeriesCollection lineDataset = new TimeSeriesCollection();
		int y = Integer.parseInt(year);
	    for (TimePeriod monthTimePeriod : monthsList) {
			kpiGatherResultMonth = map.get(monthTimePeriod.getId());
			
			if(null!=kpiGatherResultMonth){
				Double finishValue = kpiGatherResultMonth.getFinishValue();
				int m = Integer.parseInt(monthTimePeriod.getMonth());
				timeSeries.add(new Month(m, y), finishValue);
			}
		}
    	timeSeries.add(new Month(1, y+1), null);
	    lineDataset.addSeries(timeSeries);
   	 	Properties props=System.getProperties(); //系统属性
	   	JFreeChart chart = ChartFactory.createTimeSeriesChart(kpiname,"", "实际值", lineDataset, true, true, true);
	   	chart.getLegend().setItemFont(new Font("宋体",Font.PLAIN,15));
	   	chart.getTitle().setFont(new Font("黑体", Font.PLAIN, 20));
	   	XYPlot plot = chart.getXYPlot(); 
	   	XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
	   	
	   	//设置网格背景颜色
	
	   	plot.setBackgroundPaint(Color.white);
	
	   	//设置网格竖线颜色
	
	   	plot.setDomainGridlinePaint(Color.pink);
	
	   	//设置网格横线颜色
	
	   	plot.setRangeGridlinePaint(Color.pink);
	
	   	//设置曲线图与xy轴的距离
	
	   	plot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 0D));
	   	
	   	
	   	//设置曲线是否显示数据点
	
	   	xylineandshaperenderer.setBaseShapesVisible(true);
	
	   	//设置曲线显示各数据点的值
	
	   	XYItemRenderer xyitem = plot.getRenderer();   
	
	   	xyitem.setBaseItemLabelsVisible(true);   
	
	   	xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	
	   	xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
	
	   	xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));
	
	   	plot.setRenderer(xyitem);
	   	
	   	ValueAxis rAxis = plot.getRangeAxis(); 
	   	ValueAxis domainAxis = plot.getDomainAxis();
	   	domainAxis.setVerticalTickLabels(true);
	   	domainAxis.setVisible(true);
	   	domainAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));      
	   	domainAxis.setLabelFont(new Font("宋体",Font.PLAIN,15));  
	   	rAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));       
	   	rAxis.setLabelFont(new Font("黑体",Font.PLAIN,15)); 
	   	domainAxis.setVisible(true); 
	   	plot.setDomainAxis(domainAxis);
	   	String filename = ServletUtilities.saveChartAsJPEG(chart, 700, 600, null, null);
	   	String tempPath = props.getProperty("java.io.tmpdir");
	   	if(!tempPath.endsWith("\\")){
	   		tempPath += "\\";
	   	}
		filePath =tempPath+filename;
    	
    	return filePath;
    }
    /**获得年度指标图片path
     * @param kpiid 指标id
     * @param kpiname 指标名称
     * @param year 年
     * @return
     * @throws IOException
     */
    public String getYearChart(String kpiid,String kpiname,String year) throws IOException{
    	String filePath = "";
    	Map<String, KpiGatherResult> map = o_kpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiid);
    	KpiGatherResult kpiGatherResultYear = null;
    	KpiGatherResult kpiGatherResultYearL = null;
    	kpiGatherResultYear = map.get(year);
    	int y = Integer.parseInt(year);
    	int yearL = Integer.parseInt(year)-1;
    	kpiGatherResultYearL = map.get(String.valueOf(yearL));
    	
	  	TimeSeries timeSeries = new TimeSeries(year);
    	TimeSeriesCollection lineDataset = new TimeSeriesCollection();
    	
    	
    	if(null!=kpiGatherResultYearL){
    		Double finishValueL = kpiGatherResultYearL.getFinishValue();
        	if(null!=finishValueL){
            	timeSeries.add(new Year(yearL),finishValueL);
        	}
    	}
    	
    	
    	Double finishValue = kpiGatherResultYear.getFinishValue();
		if(null!=finishValue){
			String value = finishValue.toString().split("\\.")[0];
	    	timeSeries.add(new Year(y), Integer.parseInt(value)); 		
		}
    	
    	timeSeries.add(new Year(y+1), null);
    	
	    lineDataset.addSeries(timeSeries);
   	 	Properties props=System.getProperties(); //系统属性
	   	JFreeChart chart = ChartFactory.createTimeSeriesChart(kpiname,"", "实际值", lineDataset, true, true, true);
	   	chart.getLegend().setItemFont(new Font("宋体",Font.PLAIN,15));
	   	chart.getTitle().setFont(new Font("黑体", Font.PLAIN, 20));
	   	XYPlot plot = chart.getXYPlot(); 
	   	XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
	   	
	   	//设置网格背景颜色
	
	   	plot.setBackgroundPaint(Color.white);
	
	   	//设置网格竖线颜色
	
	   	plot.setDomainGridlinePaint(Color.pink);
	
	   	//设置网格横线颜色
	
	   	plot.setRangeGridlinePaint(Color.pink);
	
	   	//设置曲线图与xy轴的距离
	
	   	plot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 0D));
	   	
	   	
	   	//设置曲线是否显示数据点
	
	   	xylineandshaperenderer.setBaseShapesVisible(true);
	
	   	//设置曲线显示各数据点的值
	
	   	XYItemRenderer xyitem = plot.getRenderer();   
	
	   	xyitem.setBaseItemLabelsVisible(true);   
	
	   	xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	
	   	xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
	
	   	xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));
	
	   	plot.setRenderer(xyitem);
	   	
	   	ValueAxis rAxis = plot.getRangeAxis(); 
	   	ValueAxis domainAxis = plot.getDomainAxis();
	   	domainAxis.setVerticalTickLabels(true);
	   	domainAxis.setVisible(true);
	   	domainAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));      
	   	domainAxis.setLabelFont(new Font("宋体",Font.PLAIN,15));  
	   	rAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));       
	   	rAxis.setLabelFont(new Font("黑体",Font.PLAIN,15)); 
	   	domainAxis.setVisible(true); 
	   	plot.setDomainAxis(domainAxis);
	   	String filename = ServletUtilities.saveChartAsJPEG(chart, 700, 600, null, null);
	   	String tempPath = props.getProperty("java.io.tmpdir");
	   	if(!tempPath.endsWith("\\")){
	   		tempPath += "\\";
	   	}
		filePath =tempPath+filename;
    	
    	return filePath;
    }
    /**获得周指标图片path
     * @param kpiid 指标id
     * @param kpiname 指标名称
     * @param year 年
     * @return
     * @throws IOException
     */
    public String getWeekChart(String kpiid,String kpiname,String year) throws IOException{
    	String filePath = "";
    	Map<String, KpiGatherResult> map = o_kpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiid);
	    List<TimePeriod> weekList = o_kpiTableBO.findTimePeriodByEType("0frequecy_week");
		KpiGatherResult kpiGatherResultWeek = null;
	    
	  	TimeSeries timeSeries = new TimeSeries(year);
    	TimeSeriesCollection lineDataset = new TimeSeriesCollection();
    	
	    for (TimePeriod timePeriod : weekList) {
	    	kpiGatherResultWeek = map.get(timePeriod.getId());
			
			if(null!=kpiGatherResultWeek){
				Double finishValue = kpiGatherResultWeek.getFinishValue();
				if(null!=finishValue){
					int y = Integer.parseInt(year);
					Integer week = timePeriod.getWeekNofYear();
					timeSeries.add(new Week(week, y), finishValue);
				}
			}
		}
	    lineDataset.addSeries(timeSeries);
   	 	Properties props=System.getProperties(); //系统属性
	   	JFreeChart chart = ChartFactory.createTimeSeriesChart(kpiname,"", "实际值", lineDataset, true, true, true);
	   	chart.getLegend().setItemFont(new Font("宋体",Font.PLAIN,15));
	   	chart.getTitle().setFont(new Font("黑体", Font.PLAIN, 20));
	   	XYPlot plot = chart.getXYPlot(); 
	   	XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
	   	
	   	//设置网格背景颜色
	
	   	plot.setBackgroundPaint(Color.white);
	
	   	//设置网格竖线颜色
	
	   	plot.setDomainGridlinePaint(Color.pink);
	
	   	//设置网格横线颜色
	
	   	plot.setRangeGridlinePaint(Color.pink);
	
	   	//设置曲线图与xy轴的距离
	
	   	plot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 0D));
	   	
	   	
	   	//设置曲线是否显示数据点
	
	   	xylineandshaperenderer.setBaseShapesVisible(true);
	
	   	//设置曲线显示各数据点的值
	
	   	XYItemRenderer xyitem = plot.getRenderer();   
	
	   	xyitem.setBaseItemLabelsVisible(true);   
	
	   	xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	
	   	xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
	
	   	xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));
	
	   	plot.setRenderer(xyitem);
	   	
	   	ValueAxis rAxis = plot.getRangeAxis(); 
	   	ValueAxis domainAxis = plot.getDomainAxis();
	   	domainAxis.setVerticalTickLabels(true);
	   	domainAxis.setVisible(true);
	   	domainAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));      
	   	domainAxis.setLabelFont(new Font("宋体",Font.PLAIN,15));  
	   	rAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));       
	   	rAxis.setLabelFont(new Font("黑体",Font.PLAIN,15)); 
	   	domainAxis.setVisible(true); 
	   	plot.setDomainAxis(domainAxis);
	   	String filename = ServletUtilities.saveChartAsJPEG(chart, 700, 600, null, null);
	   	String tempPath = props.getProperty("java.io.tmpdir");
	   	if(!tempPath.endsWith("\\")){
	   		tempPath += "\\";
	   	}
		filePath =tempPath+filename;
    	
    	return filePath;
    }
    
    /**获得季度指标图片path
     * @param kpiid 指标id
     * @param kpiname 指标名称
     * @param year 年
     * @return
     * @throws IOException
     */
    public String getQuarterChart(String kpiid,String kpiname,String year) throws IOException{
    	String filePath = "";
    	Map<String, KpiGatherResult> map = o_kpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiid);
    	List<TimePeriod> quarterList = o_kpiTableBO.findTimePeriodById(year, "0frequecy_quarter");
	    KpiGatherResult kpiGatherResultQuarter = null;
	    
	  	TimeSeries timeSeries = new TimeSeries(year);
    	TimeSeriesCollection lineDataset = new TimeSeriesCollection();
    	int y = Integer.parseInt(year);
	    for (TimePeriod timePeriod : quarterList) {
	    	kpiGatherResultQuarter = map.get(timePeriod.getId());
			
			if(null!=kpiGatherResultQuarter){
				Double finishValue = kpiGatherResultQuarter.getFinishValue();
				if(null!=finishValue){
					int m = Integer.parseInt(timePeriod.getQuarter());
					timeSeries.add(new Quarter(m, y), finishValue);
				}
			}
		}
		timeSeries.add(new Quarter(1, y+1), null);
	    lineDataset.addSeries(timeSeries);
   	 	Properties props=System.getProperties(); //系统属性
	   	JFreeChart chart = ChartFactory.createTimeSeriesChart(kpiname,"", "实际值", lineDataset, true, true, true);
	   	chart.getLegend().setItemFont(new Font("宋体",Font.PLAIN,15));
	   	chart.getTitle().setFont(new Font("黑体", Font.PLAIN, 20));
	   	XYPlot plot = chart.getXYPlot(); 
	   	XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
	   	
	   	//设置网格背景颜色
	
	   	plot.setBackgroundPaint(Color.white);
	
	   	//设置网格竖线颜色
	
	   	plot.setDomainGridlinePaint(Color.pink);
	
	   	//设置网格横线颜色
	
	   	plot.setRangeGridlinePaint(Color.pink);
	
	   	//设置曲线图与xy轴的距离
	
	   	plot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 0D));
	   	
	   	
	   	//设置曲线是否显示数据点
	
	   	xylineandshaperenderer.setBaseShapesVisible(true);
	
	   	//设置曲线显示各数据点的值
	
	   	XYItemRenderer xyitem = plot.getRenderer();   
	
	   	xyitem.setBaseItemLabelsVisible(true);   
	
	   	xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	
	   	xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
	
	   	xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));
	
	   	plot.setRenderer(xyitem);
	   	
	   	ValueAxis rAxis = plot.getRangeAxis(); 
	   	ValueAxis domainAxis = plot.getDomainAxis();
	   	domainAxis.setVerticalTickLabels(true);
	   	domainAxis.setVisible(true);
	   	domainAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));      
	   	domainAxis.setLabelFont(new Font("宋体",Font.PLAIN,15));  
	   	rAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));       
	   	rAxis.setLabelFont(new Font("黑体",Font.PLAIN,15)); 
	   	domainAxis.setVisible(true); 
	   	plot.setDomainAxis(domainAxis);
	   	String filename = ServletUtilities.saveChartAsJPEG(chart, 700, 600, null, null);
	   	String tempPath = props.getProperty("java.io.tmpdir");
	   	if(!tempPath.endsWith("\\")){
	   		tempPath += "\\";
	   	}
		filePath =tempPath+filename;
    	
    	return filePath;
    }
    
    /**获得半年指标图片path
     * @param kpiid 指标id
     * @param kpiname 指标名称
     * @param year 年
     * @return
     * @throws IOException
     */
    public String getHalfYearChart(String kpiid,String kpiname,String year) throws IOException{
    	String filePath = "";
    	Map<String, KpiGatherResult> map = o_kpiGatherResultBO.findMapKpiGatherResultListByKpiId(kpiid);
    	List<TimePeriod> halfYearList = o_kpiTableBO.findTimePeriodById(year, "0frequecy_halfyear");
	    KpiGatherResult kpiGatherResultHalfYear = null;
	    
	  	TimeSeries timeSeries = new TimeSeries(year);
    	TimeSeriesCollection lineDataset = new TimeSeriesCollection();
    	
	    for (TimePeriod timePeriod : halfYearList) {
	    	kpiGatherResultHalfYear = map.get(timePeriod.getId());
			
			if(null!=kpiGatherResultHalfYear){
				Double finishValue = kpiGatherResultHalfYear.getFinishValue();
				if(null!=finishValue){
					int y = Integer.parseInt(year);
					Calendar cal = Calendar.getInstance();  
					cal.setTime(timePeriod.getEndTime());  
					int m = cal.get(Calendar.MONTH)+1;
					//int m = timePeriod.getEndTime().getMonth()+1;
					timeSeries.add(new Month(m, y), finishValue);
				}
			}
		}
	    lineDataset.addSeries(timeSeries);
   	 	Properties props=System.getProperties(); //系统属性
	   	JFreeChart chart = ChartFactory.createTimeSeriesChart(kpiname,"", "实际值", lineDataset, true, true, true);
	   	chart.getLegend().setItemFont(new Font("宋体",Font.PLAIN,15));
	   	chart.getTitle().setFont(new Font("黑体", Font.PLAIN, 20));
	   	XYPlot plot = chart.getXYPlot(); 
	   	XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
	   	
	   	//设置网格背景颜色
	
	   	plot.setBackgroundPaint(Color.white);
	
	   	//设置网格竖线颜色
	
	   	plot.setDomainGridlinePaint(Color.pink);
	
	   	//设置网格横线颜色
	
	   	plot.setRangeGridlinePaint(Color.pink);
	
	   	//设置曲线图与xy轴的距离
	
	   	plot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 0D));
	   	
	   	
	   	//设置曲线是否显示数据点
	
	   	xylineandshaperenderer.setBaseShapesVisible(true);
	
	   	//设置曲线显示各数据点的值
	
	   	XYItemRenderer xyitem = plot.getRenderer();   
	
	   	xyitem.setBaseItemLabelsVisible(true);   
	
	   	xyitem.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	
	   	xyitem.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
	
	   	xyitem.setBaseItemLabelFont(new Font("Dialog", 1, 14));
	
	   	plot.setRenderer(xyitem);
	   	
	   	ValueAxis rAxis = plot.getRangeAxis(); 
	   	ValueAxis domainAxis = plot.getDomainAxis();
	   	domainAxis.setVerticalTickLabels(true);
	   	domainAxis.setVisible(true);
	   	domainAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));      
	   	domainAxis.setLabelFont(new Font("宋体",Font.PLAIN,15));  
	   	rAxis.setTickLabelFont(new Font("宋体",Font.PLAIN,15));       
	   	rAxis.setLabelFont(new Font("黑体",Font.PLAIN,15)); 
	   	domainAxis.setVisible(true); 
	   	plot.setDomainAxis(domainAxis);
	   	String filename = ServletUtilities.saveChartAsJPEG(chart, 700, 600, null, null);
	   	String tempPath = props.getProperty("java.io.tmpdir");
	   	if(!tempPath.endsWith("\\")){
	   		tempPath += "\\";
	   	}
		filePath =tempPath+filename;
    	
    	return filePath;
    }
    
    
	/**
	 * 根据Id查找TimePeriod实体
	 * @param id 时间区间纬度id
	 * @return
	 */
	public TimePeriod findTimePeriodById(String id) {
		return o_timePeriodBO.findTimePeriodById(id);
    }
	
    /**
     * 邮件发送--编辑列表时候发送邮件
     * @param kpiid 指标id
     * @author 郝静
     * * */
    
    public void sendKpiChangedEmail(String kpiid,String params){
    	
    	Kpi kpi = o_kpiBO.findKpiById(kpiid);
    	JSONObject orgEmpObj = this.o_kpiBO.findKpiRelaOrgEmpBySmToJson(kpi);
    	TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_kpichange");
    	if(null!=temp){
    		String str = temp.getContent();//获得邮件模板
        	
        	JSONArray ownDept = orgEmpObj.getJSONArray("ownDept");//所属部门
        	if(null!=ownDept&&ownDept.size()>0){
            	JSONObject obj = (JSONObject) ownDept.get(0);
            	String empid = obj.getString("empid");
            	String emailAddresses = "";
            	if(null!=empid&&!"".equals(empid)){
                	SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empid);
                	emailAddresses = emp.getOemail();
                	String title = kpi.getName()+"——变更邮件";
                	String[] emails = {emailAddresses};//主送
                	
                	JSONArray reportDept = orgEmpObj.getJSONArray("reportDept");//报告部门
                	String cemailAddresses = "";
                	if(null!=reportDept&&reportDept.size()>0){
                    	JSONObject robj = (JSONObject) reportDept.get(0);
                    	String rempid = robj.getString("empid");
                    	
                    	if(null!=rempid&&!"".equals(rempid)){
                    		SysEmployee remp = o_empGridBO.findEmpEntryByEmpId(rempid);
                        	cemailAddresses = remp.getOemail();
                    	}
                	}

                	String[] cemails = {cemailAddresses};//抄送
                	
                	JSONArray jsonarr = JSONArray.fromObject(params);
                	JSONObject objs = (JSONObject) jsonarr.get(0);
                	String time = o_kpiBO.findKpiNewValueNes(params);
                	String values = objs.getString(time);
                	String[] valarr = values.split(",");
                	String finishValue = "";//实际值
                	String targetValue = "";//目标值
                	String assessmentValue = "";//评估值
                	
                	if(valarr.length>0){
                		if(valarr.length==1){
                			finishValue = valarr[0];	
                		}else if(valarr.length==2){
                			finishValue = valarr[0];
                			targetValue = valarr[1];
                		}else if(valarr.length==3){
                			finishValue = valarr[0];	
                			targetValue = valarr[1];		
                        	assessmentValue = valarr[2];		
                		}
                	}
                	finishValue = Utils.getValue(finishValue,kpi.getScale());
                	targetValue = Utils.getValue(targetValue,kpi.getScale());
                	assessmentValue = Utils.getValue(assessmentValue,kpi.getScale());
                
                	TimePeriod tP = this.findTimePeriodById(time);//时间区间
                	
                	KpiGatherResult kpiGatherResult = o_kpiGatherResultBO.findKpiGatherResultByIdAndTimeperiod(kpiid, tP);
                	String status = "";
                	if(null!=kpiGatherResult.getAssessmentStatus()){
                		status = kpiGatherResult.getAssessmentStatus().getId();
                	}
                	String statusTag = "";
                	if(status.equals("0alarm_startus_l")){
                		statusTag = "<div style='width: 15px; height: 15px; background-color:#33CC00'></div>";
                	}else if(status.endsWith("0alarm_startus_m")){
                		statusTag = "<div style='width: 15px; height: 15px; background-color:#FFFF00'></div>";
                	}else if(status.endsWith("0alarm_startus_h")){
                		statusTag = "<div style='width: 15px; height: 15px; background-color:#FF0000'></div>";
                	}
                	
                	String timePeriod = tP.getTimePeriodFullName();
                	String path = this.getFilePath(kpiid);//#TODO测试在服务器上发不了邮件
                	
                	str = StringUtils.replace(str, "$userName", emp.getEmpname());
                	str = StringUtils.replace(str, "$kpiName", kpi.getName());
                	str = StringUtils.replace(str, "$unit", kpi.getUnits().getName());
                	str = StringUtils.replace(str, "$finishValue",finishValue);
                	str = StringUtils.replace(str, "$targetValue",targetValue);
                	str = StringUtils.replace(str, "$assessmentValue",assessmentValue);
                	str = StringUtils.replace(str, "$timePeriod",timePeriod);
                	str = StringUtils.replace(str, "$status",statusTag);
                	str = StringUtils.replace(str, "$img", "<img src='cid:img' />");//#TODO测试在服务器上发不了邮件
                	logger.info("发送邮件开始");
                	this.sendEmail(emails, cemails, null, null, title, str, path);
                	logger.info("发送邮件结束");
            	
            	}

        	}
    	}

    }
    
    /**
     * 邮件发送--公式计算时发送邮件
     * @param kpiid 指标id
     * @param timePeriodId 时间区间纬度id
     * @author 郝静
     * */
    
    public void sendFormulaCalculateEmail(String kpiid,String timePeriodId){
		Kpi kpi = o_kpiBO.findKpiById(kpiid);
    	TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_kpichange");
    	if(null!=temp){
    		String str = temp.getContent();//获得邮件模板
    		
    		JSONObject orgEmpObj = this.o_kpiBO.findKpiRelaOrgEmpBySmToJson(kpi);
        	JSONArray ownDept = orgEmpObj.getJSONArray("ownDept");
        	
        	if(null!=ownDept&&ownDept.size()>0){
        		JSONObject obj = (JSONObject) ownDept.get(0);
            	String empid = obj.getString("empid");
            	String emailAddresses = "";
            	
            	if(null!=empid&&!"".equals(empid)){
                	SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empid);
                	emailAddresses = emp.getOemail();
                	String title = kpi.getName()+"——变更邮件";
                	
                	String[] emails = {emailAddresses};
                	
                	JSONArray reportDept = orgEmpObj.getJSONArray("reportDept");//报告部门
                	String cemailAddresses = "";
                	if(null!=reportDept&&reportDept.size()>0){
                    	JSONObject robj = (JSONObject) reportDept.get(0);
                    	String rempid = robj.getString("empid");
                    	
                    	if(null!=rempid&&!"".equals(rempid)){
                    		SysEmployee remp = o_empGridBO.findEmpEntryByEmpId(rempid);
                        	cemailAddresses = remp.getOemail();
                    	}
                	}

                	String[] cemails = {cemailAddresses};//抄送
                	
                	TimePeriod tP = this.findTimePeriodById(timePeriodId);
                	KpiGatherResult kpiGatherResult = o_kpiGatherResultBO.findKpiGatherResultByIdAndTimeperiod(kpiid, tP);
                	Double fValue = kpiGatherResult.getFinishValue();		//实际值
                	String finishValue = "";
                	if(null!=fValue){
                		finishValue = fValue.toString();
                		finishValue = Utils.getValue(finishValue,kpi.getScale());
                	}
                	Double tValue = kpiGatherResult.getTargetValue();		//目标值
                	String targetValue = "";
                	if(null!=tValue){
                		targetValue = tValue.toString();
                		targetValue = Utils.getValue(targetValue,kpi.getScale());
                	}
                	Double aValue = kpiGatherResult.getAssessmentValue();		//评估值
                	String assessmentValue = "";
                	if(null!=aValue){
                		assessmentValue = aValue.toString();
                		assessmentValue = Utils.getValue(assessmentValue,kpi.getScale());
                	}
                	
                	DictEntry astatus = kpiGatherResult.getAssessmentStatus();
                	String status = "";
            		String statusTag = "";
                	if(null!=astatus&&!"".equals(astatus.getId())){
                		status = astatus.getId();

                    	if(status.equals("0alarm_startus_l")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#33CC00'></div>";
                    	}else if(status.endsWith("0alarm_startus_m")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#FFFF00'></div>";
                    	}else if(status.endsWith("0alarm_startus_h")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#FF0000'></div>";
                    	}
                	}
                	
                	String timePeriod = tP.getTimePeriodFullName();
                	final String path = this.getFilePath(kpiid);
                	
                	str = StringUtils.replace(str, "$userName", emp.getEmpname());
                	str = StringUtils.replace(str, "$kpiName", kpi.getName());
                	str = StringUtils.replace(str, "$unit", kpi.getUnits().getName());
                	str = StringUtils.replace(str, "$finishValue",finishValue);
                	str = StringUtils.replace(str, "$targetValue",targetValue);
                	str = StringUtils.replace(str, "$assessmentValue",assessmentValue);
                	str = StringUtils.replace(str, "$timePeriod",timePeriod);
                	str = StringUtils.replace(str, "$status",statusTag);
                	str = StringUtils.replace(str, "$img", "<img src='cid:img' />");
                	
                	this.sendEmail(emails, cemails, null, null, title, str, path);

            	}

        	}
    	}
    	
	
	}
    
    /** 邮件发送--指标采集提醒邮件
     * @param empId 员工id
     * @param kgrIdList 采集结果集合
     * @param taskName 节点任务名称
     * @param url web采集url
     * @param suggestion 审批意见
     * @author 郝静
     */
    public void sendKpiGatherEmail(String empId,List<String> kgrIdList,String taskName,String url,String suggestion){
    	TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_kpigatherdata");
    	if(null!=temp){
    		String str = temp.getContent();//获得邮件模板
    		String title = taskName;
    		SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empId);//获得机构员工实体
    		String[] emails = {emp.getOemail()};//邮箱地址
    		String kpiName = "";//指标名称
    		Double targetValue = null;//目标值
    		Double finishValue = null;//实际值
    		Double finishValuePre = null;//上期完成值
    		Double targetValuePre = null;//上期目标值
    		StringBuffer dynamicStr = new StringBuffer();;//动态表格
    		Integer resultCollectInterval = null;//延期天数 
    		DictEntry astatus = null;//状态灯
    		String unit = "";//单位
    		
    		for(String kgrId:kgrIdList){
    			KpiGatherResult kgr = o_kpiGatherResultBO.findKpiGatherResultById(kgrId);//当期采集结果
    			KpiGatherResult kgrPre = o_kpiGatherResultBO.findPreGatherResultByCurrent(kgr);//上期采集结果
    			Kpi kpi = kgr.getKpi();
    			kpiName = kpi.getName();
    			unit = kpi.getUnits().getName();
    			resultCollectInterval = kpi.getResultCollectInterval();
    			targetValue = kgr.getTargetValue();
    			String tValue = "";
            	if(null!=targetValue){		//目标值空判断
            		tValue = targetValue.toString();
            		tValue = Utils.getValue(tValue);
            	}
    			finishValue = kgr.getFinishValue();
    			String fValue = "";
            	if(null!=finishValue){		//完成值空判断
            		fValue = finishValue.toString();
            		fValue = Utils.getValue(fValue);
            	}
            	
            	String tPValue = "";
            	String fPValue = "";
    			if(null!=kgrPre){
        			targetValuePre = kgrPre.getTargetValue();
        			
                	if(null!=targetValuePre){		//上期目标值空判断
                		tPValue = targetValuePre.toString();
                		tPValue = Utils.getValue(tPValue);
                	}
        			finishValuePre = kgrPre.getFinishValue();
        			
        			if(null!=finishValuePre){		//上期完成值空判断
                		fPValue = finishValuePre.toString();
                		fPValue = Utils.getValue(fPValue);
                	}
        			
        			astatus = kgrPre.getAssessmentStatus();//状态灯
    			}

            	String status = "";
        		String statusTag = "";
            	if(null!=astatus&&!"".equals(astatus.getId())){
            		status = astatus.getId();

                	if(status.equals("0alarm_startus_l")){
                		statusTag = "<div style='width: 15px; height: 15px; background-color:#33CC00'></div>";
                	}else if(status.endsWith("0alarm_startus_m")){
                		statusTag = "<div style='width: 15px; height: 15px; background-color:#FFFF00'></div>";
                	}else if(status.endsWith("0alarm_startus_h")){
                		statusTag = "<div style='width: 15px; height: 15px; background-color:#FF0000'></div>";
                	}
            	}
            	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
            	Calendar   c   =   Calendar.getInstance();    
            	String sDateTime = formatter.format(c.getTime());//开始日期
            	c.add(Calendar.DAY_OF_MONTH, resultCollectInterval); 
            	String eDateTime = formatter.format(c.getTime());//截止日期
            	String timeStr = sDateTime+"~"+eDateTime;
            	
    			
    			dynamicStr.append("<tr>");
    			dynamicStr.append("<td>").append(kpiName).append("</td>");
    			dynamicStr.append("<td>").append(unit).append("</td>");
    			dynamicStr.append("<td>").append(tValue).append("</td>");
    			dynamicStr.append("<td>").append(fValue).append("</td>");
    			dynamicStr.append("<td>").append(tPValue).append("</td>");
    			dynamicStr.append("<td>").append(fPValue).append("</td>");
    			dynamicStr.append("<td>").append(statusTag).append("</td>");
    			dynamicStr.append("<td>").append(timeStr).append("</td>");
    			dynamicStr.append("</tr>");
    			
    		}
    		str = str.replace("$userName",emp.getEmpname());
    		str = str.replace("$dynamicTable", dynamicStr);
    		StringBuffer linkBuf = new StringBuffer();
    		if(StringUtils.isNotBlank(suggestion)){
    			linkBuf.append("<div>").append("审批意见:").append(suggestion).append("</div><br>");
    		}
    		linkBuf.append("<a href='").append(url).append("' target='_blank' >请及时采集</a>");
        	str = str+linkBuf;
        	this.sendEmail(emails, null, null, null, title, str,null);
    	}
	}
    
    /**指标采集结果审批发送邮件
     * @param gatherType采集列席
     * @param executionId 执行id
     * @param taskId 任务id
     * @param empId 员工id
     */
    public void sendApprovalEmail(String gatherType,String executionId,String taskId,String empId,Map<String,KpiGatherResult> resultMap,List<String> resultIdList){
    	String jsUrl = "";
    	String title = "";
    	String emailHtml = "";
    	SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empId);//获得机构员工实体
		String[] emailAddress = {emp.getOemail()};//邮箱地址
    	if("target".equals(gatherType)){
    		title = "目标值采集审批";
    		jsUrl = "FHD.view.kpi.bpm.targetgather.KpiExamineRecorded";
    	}else if("finish".equals(gatherType)){
    		title = "实际值采集审批";
    		jsUrl = "FHD.view.kpi.bpm.finishgather.KpiExamineRecorded";
    	}
    	String weburl = this.findWebUrl(gatherType, executionId, taskId, empId,jsUrl);
    	emailHtml = findApprovalEmailHtml(gatherType, weburl);
    	//添加采集数据内容行
    	StringBuffer contentHtml = new StringBuffer();
    	if("finish".equals(gatherType)){
    		for(int i=0;i< resultIdList.size();i++) {
        		KpiGatherResult kgr =  resultMap.get(resultIdList.get(i));
        		if(kgr != null) {
        			StringBuffer tr = new StringBuffer();
        			tr.append("<tr>");
        			Kpi kpi = kgr.getKpi();
        			KpiGatherResult preKpiGatherResult = o_kpiGatherResultBO.findPreGatherResultByCurrent(kgr);
        			//指标名称
        			tr.append("<td>").append(kpi.getName()).append("</td>");
        			// 目标值
        			Double targetValue = null;
        			if(null!=kpi.getModelValue()){
        				if("0".equals(kgr.getTargetSetStatus())){
        					targetValue =  kgr.getTempTargetValue();
        				}else if("1".equals(kgr.getTargetSetStatus())){
        					targetValue= kgr.getTargetValue();
        				}else{
        					targetValue =  kpi.getModelValue();
        				}
        			}else{
        				if("0".equals(kgr.getTargetSetStatus())){
        					targetValue = kgr.getTempTargetValue();
        				}else{
        					targetValue = kgr.getTargetValue();
        				}
        			}
        			if(null!=targetValue){
        				tr.append("<td>").append(targetValue).append("</td>");
        			}else{
        				tr.append("<td>").append("").append("</td>");
        			}
        			//实际值
        			Double finishValue = null;
        			if("0".equals(kgr.getFinishSetStatus())){
        				finishValue = kgr.getTempFinishValue();
        			} else {
        				finishValue = kgr.getFinishValue();
        			}
        			if(null!=finishValue){
        				tr.append("<td>").append(finishValue).append("</td>");
        			}else{
        				tr.append("<td>").append("").append("</td>");
        			}
        			// 单位
        			String units = "";
        			if(kpi.getUnits() != null){
        				units = kpi.getUnits().getName();
        			} 
        			tr.append("<td>").append(units).append("</td>");
        			//时间区间
        			if(kgr.getTimePeriod() != null){
        				tr.append("<td>").append( kgr.getTimePeriod().getTimePeriodFullName()).append("</td>");
        			}
        			// 上期状态
        			String statusTag = "";
        			String assessmentStatus = "";
        			if(preKpiGatherResult!= null && preKpiGatherResult.getAssessmentStatus() != null){
        				assessmentStatus =  preKpiGatherResult.getAssessmentStatus().getValue();
        				if(assessmentStatus.equals("icon-ibm-symbol-6-sm")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#33CC00'></div>";
                    	}else if(assessmentStatus.endsWith("icon-ibm-symbol-5-sm")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#FFFF00'></div>";
                    	}else if(assessmentStatus.endsWith("icon-ibm-symbol-4-sm")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#FF0000'></div>";
                    	}
        			} 
        			tr.append("<td>").append(statusTag).append("</td>");
        			tr.append("</tr>");
        			contentHtml.append(tr);
        		}
        	}
    	}else if("target".equals(gatherType)){
    		for(int i=0;i<resultIdList.size();i++){
        		KpiGatherResult kgr =resultMap.get(resultIdList.get(i));
	    		 if(kgr != null) {
	    			StringBuffer tr = new StringBuffer();
	    			tr.append("<tr>");
	    			Kpi kpi = kgr.getKpi();
	    			//指标名称
	    			tr.append("<td>").append(kpi.getName()).append("</td>");
	    			//上期目标值
	    			KpiGatherResult preKpiGatherResult = o_kpiGatherResultBO.findPreGatherResultByCurrent(kgr);
	    			if(null!=preKpiGatherResult){
	    				//上期目标值
	    				tr.append("<td>").append(preKpiGatherResult.getTargetValue()==null?"":preKpiGatherResult.getTargetValue()).append("</td>");
	    			}
	    			else{
	    				tr.append("<td>").append("").append("</td>");
	    			}
	    			//当期目标值
	    			Double target = null;
	    			if("0".equals(kgr.getTargetSetStatus())){
	    				target = kgr.getTempTargetValue();
	    			} else {
	    				if(null!=kpi.getModelValue()){
	    					target = kpi.getModelValue();
	    				}else{
	    					target = kgr.getTargetValue();
	    				}
	    			}
	    			if(null!=target){
	    				tr.append("<td>").append(target).append("</td>");
	    			}else{
	    				tr.append("<td>").append("").append("</td>");
	    			}
	    			//上期完成值
	    			if(null!=preKpiGatherResult){
	    				tr.append("<td>").append(preKpiGatherResult.getFinishValue()==null?"":preKpiGatherResult.getFinishValue()).append("</td>");
	    			}else{
	    				tr.append("<td>").append("").append("</td>");
	    			}
	    			// 单位
        			String units = "";
        			if(kpi.getUnits() != null){
        				units = kpi.getUnits().getName();
        			} 
        			tr.append("<td>").append(units).append("</td>");
        			
        			//时间区间
        			if(kgr.getTimePeriod() != null){
        				tr.append("<td>").append( kgr.getTimePeriod().getTimePeriodFullName()).append("</td>");
        			}
        			// 上期状态
        			String statusTag = "";
        			String assessmentStatus = "";
        			if(preKpiGatherResult!= null && preKpiGatherResult.getAssessmentStatus() != null){
        				assessmentStatus =  preKpiGatherResult.getAssessmentStatus().getValue();
        				if(assessmentStatus.equals("icon-ibm-symbol-6-sm")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#33CC00'></div>";
                    	}else if(assessmentStatus.endsWith("icon-ibm-symbol-5-sm")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#FFFF00'></div>";
                    	}else if(assessmentStatus.endsWith("icon-ibm-symbol-4-sm")){
                    		statusTag = "<div style='width: 15px; height: 15px; background-color:#FF0000'></div>";
                    	}
        			} 
        			tr.append("<td>").append(statusTag).append("</td>");
        			tr.append("</tr>");
        			contentHtml.append(tr);
	    		}
        	}
    	}
    	String html = emailHtml.replace("$dynamicTable", contentHtml);
    	html = html.replace("$userName", emp.getEmpname());
    	this.sendEmail(emailAddress, null, null, null, title, html,null);
    	
    }
    
    /**根据采集类型(finish:实际值采集,target:目标值采集)查询审批邮件html格式
     * @param gatherType 采集类型
     * @param url js链接地址
     * @return
     */
    public String findApprovalEmailHtml(String gatherType,String url){
    	StringBuffer emailHtml = new StringBuffer();
    	if("finish".equals(gatherType)){//实际值审批
    		emailHtml.append(findApprovalFinishTemplate());
    	}else if("target".equals(gatherType)){
    		emailHtml.append(findApprovalTargetTemplate());
    	}
    	emailHtml.append("<a href='").append(url).append("' target='_blank' >请及时审批</a>");
    	return emailHtml.toString();
    }
    
    /**查找实际值审批邮件模版
     * @return
     */
    public String findApprovalFinishTemplate(){
    	String html = "";
    	TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_kpidata_finish");
    	if(null!=temp){
    		html = temp.getContent();
    	}
    	return html;
    }
    /**查找目标值审批邮件模版
     * @return
     */
    public String findApprovalTargetTemplate(){
    	String html = "";
    	TemplateManage temp = o_templateManageBO.findDefaultTemplateByentryId("template_manage_kpidata_target");
    	if(null!=temp){
    		html = temp.getContent();
    	}
    	return html;
    }
    
    /**
	 * 是否发送email值
	 * @return
	 */
	public Boolean findIsSend(){
		Boolean isSend = false;
		DictEntry entry = o_dictBO.findDictEntryById(IS_EMAIL);
		if(null != entry){
			if(Contents.DICT_Y.equals(entry.getValue())){
				isSend = true;
			}
		}
		return isSend;
	}
	/**采集结果数据发送变化时，是否发送邮件
	 * @return
	 */
	public Boolean findIsValueChangeSend(){
		Boolean isSend = false;
		DictEntry entry = o_dictBO.findDictEntryById(IS_VALUE_CHANGE_EMAIL);
		if(null != entry){
			if(Contents.DICT_Y.equals(entry.getValue())){
				isSend = true;
			}
		}
		return isSend;
	}
    /**
     * 邮件发送
     * @author 郝静
     * */
    
    public void sendEmail(String[] emails,String[] cemails,String[] bemails,String femails,String title,
    		String tempStr,String path){
    	
    	final String[] em = emails;
    	final String[] cm = cemails;
    	final String[] bm = bemails;
    	final String fm = femails;
    	final String t = title;
    	final String p = path;
    	final String html = tempStr;
    	
        	Thread emailThread = new Thread() {

				public void run() {
        			try {
        					SimpleMailService mailService =  new SimpleMailService();
        			        boolean isSendMail = false;
        			        isSendMail = mailService.htmlSendMail(em, cm, bm, fm, t, html,p);
        			        if(StringUtils.isNotBlank(p)){
        			        	deleteFile(p);
        			        }
        			        logger.info("发送邮件结果:["+isSendMail+"]");
        			} catch (Exception e) {
        				logger.error("发送邮件异常:异常信息-----"+e.toString());
        				e.printStackTrace();
        			}
        		}
        	};
        	emailThread.start();
    }
    
    /**删除图片
     * @param sPath 文件路径
     * @return
     */
    public void deleteFile(String sPath) {  
        File file = new File(sPath);  
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {  
            file.delete();  
        }  
    }  
    
    /**获得单点登录,采集url
     * @param valueType finish/target 目标值或评估值
     * @param executionId 工作流流程id
     * @param taskId 任务id
     * @param empId 人员id
     * @return
     */
    public String findWebUrl(String valueType,String executionId, String taskId,String empId,String jsUrl){
    	String userName = "";
    	if(StringUtils.isNotBlank(empId)){
    		SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(empId);
            userName = emp.getUsername();
    	}
    	WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        String contextPath = applicationContext.getServletContext().getContextPath();
        String ipPort = applicationContext.getServletContext().getInitParameter("webUrl");
    	StringBuffer url = new StringBuffer();
    	url.append(ipPort).append(contextPath).append("/sso/jbpmTask.do?");
        url.append("businessId=").append("").append("&taskId=")
        .append(taskId).append("&url=").append(jsUrl).append("&loginType=email").append("&username=").append(userName);
    	return url.toString();
    }
    
    
}