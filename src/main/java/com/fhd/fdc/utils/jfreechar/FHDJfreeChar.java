package com.fhd.fdc.utils.jfreechar;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Rotation;

/**
 * JFREECHAR类
 * 封装一些调用状图、柱状图的方法
 * @author 邓广义
 */
public class FHDJfreeChar {

	/**
	 * 创建JFreeChar，返回图片的二进制码
	 * @param req
	 * @param jfcbp
	 * @param type
	 * @param dataset
	 * @return
	 */
	public static Map<String, Object> createPieChar(JFCBaseProper jfcbp,Object dataset) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);

		// 创建饼状图
		JFreeChart chart = ChartFactory.createPieChart3D(jfcbp.getTitle(),(DefaultPieDataset) dataset, true, true, false);
		// 获得3D的水晶饼图对象
		PiePlot3D pieplot3d = (PiePlot3D) chart.getPlot();
		// 设置开始角度
		pieplot3d.setStartAngle(150D);
		// 设置方向为”顺时针方向“
		pieplot3d.setDirection(Rotation.CLOCKWISE);
		// 设置透明度，0.5F为半透明，1为不透明，0为全透明
		pieplot3d.setForegroundAlpha(1F);

		pieplot3d.setBackgroundPaint(jfcbp.getBackgroundColor());
		pieplot3d.setNoDataMessage("no data");
		
		pieplot3d.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}"));
		 // 图例显示百分比:自定义方式， {0} 表示选项， {1} 表示数值， {2} 表示所占比例
//	     plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
//	       "{0}={1}({2})"));
		//pieplot3d.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}"));
		OutputStream outputStream = new FileOutputStream("PieChart1.png");

		ChartUtilities.writeChartAsPNG(outputStream, chart, 500, 270);

		BufferedImage image = chart.createBufferedImage(500, 270);
		byte[] bytes = ChartUtilities.encodeAsPNG(image);
		map.put("image", bytes);

		return map;
	}
	
	/**
	 * 创建JFreeChar
	 * 并返回此图片的URL
	 * @param req
	 * @param jfcbp
	 * @param type
	 * @param dataset
	 * @return
	 */
	public static Map<String,Object> createJFreeChar(HttpServletRequest req ,JFCBaseProper jfcbp , String type,Object dataset) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String url = "";
		if(StringUtils.isNotBlank(type) && null != jfcbp && null!=dataset){
		       //创建主题样式  
		       StandardChartTheme standardChartTheme=new StandardChartTheme("CN");  
		       //设置标题字体  
		       standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));  
		       //设置图例的字体  
		       standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));  
		       //设置轴向的字体  
		       standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));  
		       //应用主题样式  
		       ChartFactory.setChartTheme(standardChartTheme);
		       
			if(JFCBaseProper.HIS.equals(type)){
				//柱状图
				JFreeChart chart = ChartFactory.createBarChart3D(
						jfcbp.getTitle(),		
						jfcbp.getXName(),		
						jfcbp.getYName(),		
						(CategoryDataset)dataset,		
						PlotOrientation.VERTICAL,		
						jfcbp.isMiniGraph(),		
						true,		
						false);
				CategoryPlot plot = chart.getCategoryPlot();		
				//设置网格背景颜色		
				plot.setBackgroundPaint(jfcbp.getBackgroundColor());		
				//设置网格竖线颜色		
				plot.setDomainGridlinePaint(Color.pink);		
				//设置网格横线颜色		
				plot.setRangeGridlinePaint(Color.pink);
				//显示每个柱的数值，并修改该数值的字体属性		
				BarRenderer3D renderer = new BarRenderer3D();		
				renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());		
				renderer.setBaseItemLabelsVisible(true);		
				//默认的数字显示在柱子中，通过如下两句可调整数字的显示		
				//注意：此句很关键，若无此句，那数字的显示会被覆盖，给人数字没有显示出来的问题		
				renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, 		
				TextAnchor.BASELINE_LEFT));		
				renderer.setItemLabelAnchorOffset(10D);
				//设置每个地区所包含的平行柱的之间距离		
				renderer.setItemMargin(0.2);		
				plot.setRenderer(renderer);		
				//默认将下方的“X”放到上方		
				plot.setDomainAxisLocation("top".equals(jfcbp.getXPosition())?AxisLocation.TOP_OR_RIGHT:AxisLocation.BOTTOM_OR_LEFT);		
				//将默认放在左边的“Y”放到左方		
				plot.setRangeAxisLocation("left".equals(jfcbp.getYPosition())?AxisLocation.BOTTOM_OR_LEFT:AxisLocation.BOTTOM_OR_RIGHT);
				
//				plot.setBackgroundAlpha(0.5f);
				
				String fileName = ServletUtilities.saveChartAsPNG(chart, 700, 400, null, req.getSession());
				ChartRenderingInfo info = null;
				url = req.getContextPath() + "/DisplayChart.f?filename=" + fileName;
				map.put("url",url);
				return map;
			}else if(JFCBaseProper.PIE.equals(type)){
				//饼状图
				JFreeChart chart = ChartFactory.createPieChart3D(jfcbp.getTitle(), (DefaultPieDataset)dataset, true, true, false);
				//获得3D的水晶饼图对象		
				PiePlot3D pieplot3d = (PiePlot3D) chart.getPlot();		
				//设置开始角度		
				pieplot3d.setStartAngle(150D);		
				//设置方向为”顺时针方向“		
				pieplot3d.setDirection(Rotation.CLOCKWISE);		
				//设置透明度，0.5F为半透明，1为不透明，0为全透明		
				pieplot3d.setForegroundAlpha(1F);
				
				pieplot3d.setBackgroundPaint(jfcbp.getBackgroundColor());
				pieplot3d.setNoDataMessage("no data");	
				ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
				String fileName = ServletUtilities.saveChartAsPNG(chart, 700, 400, info, req.getSession());
//				ServletUtilities.getTempOneTimeFilePrefix()
//				BufferedImage image = 
//				ChartUtilities.
				req.getContextPath();
				url = req.getContextPath() + "/DisplayChart.f?filename=" + fileName;
				
				OutputStream outputStream=new FileOutputStream("PieChart1.png");
				
				ChartUtilities.writeChartAsPNG(outputStream, chart, 500, 270); 
				
				
				
				
				BufferedImage image = chart.createBufferedImage(500, 270);
				byte [] bytes = ChartUtilities.encodeAsPNG(image);
				
				map.put("url",url);
				map.put("image",bytes);
				return map;
			}else if(JFCBaseProper.TS.equals(type)){
				//折线图
			}
		}
		return map;
	}
	
	public static void main(String[] args) throws Exception{
		DefaultPieDataset pieDataset = new DefaultPieDataset();	
		pieDataset.setValue((Comparable)"战略风险",(Number)3);
		pieDataset.setValue((Comparable)"财务风险",(Number)4);
		pieDataset.setValue((Comparable)"法律风险",(Number)5);
		JFCBaseProper jfcbp = new JFCBaseProper();
		Map<String,Object> map = FHDJfreeChar.createPieChar(jfcbp, pieDataset);
		TestImageBinary image = new TestImageBinary();
		byte[] data = (byte[])map.get("image");
		Base64 encoder = new Base64();
		String imageStr = encoder.encodeBase64String(data);//返回Base64编码过的字节数组字符串 
		System.out.println(imageStr);
		image.base64StringToImage(imageStr);
		System.out.println("ok");
	}
}
