package com.fhd.icm.utils.mxgraph;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;


/**
 * 图形对象与xml的转换类
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-9-11		上午9:30:32
 *
 * @see 	 
 */
public class GraphCellXmlUtils {
/*	public static double YD_X = 10;
	public static double YD_Y = 10;
	public static double YD_WIDTH = 0;
	public static double YD_HEIGHT = 0;
	public static double CELL_X = 30;
	public static double CELL_Y = 30;
	public static double CELL_WIDTH = 80;
	public static double CELL_HEIGHT = 60;*/
	
	/**
	 * 重大风险
	 */
	public static final String HIGHRISK = "shape=mxgraph.flowchart.on-page_reference;fillColor=#FF0000;strokeColor=#000000;strokeWidth=1;gradientColor=#FF0000;labelPosition=right;align=left";
	/**
	 * 关注风险
	 */
	public static final  String MODERATERISK = "shape=mxgraph.flowchart.on-page_reference;fillColor=#FFFF00;strokeColor=#000000;strokeWidth=1;gradientColor=#FFFF00;labelPosition=right;align=left";//
	/**
	 * 一般风险
	 */
	public static final  String LOWRISK = "shape=mxgraph.flowchart.on-page_reference;fillColor=#00FF00;strokeColor=#000000;strokeWidth=1;gradientColor=#00FF00;labelPosition=right;align=left";//

	/**
	 *  未确定风险水平
	 */
	public static final  String NOTEVALUATEDRISK = "shape=mxgraph.flowchart.on-page_reference;fillColor=#CCCCCC;strokeColor=#000000;strokeWidth=1;gradientColor=#CCCCCC;labelPosition=right;align=left";//
	/**
	 * 业务活动
	 */
	public static final  String OPERATIONALACTION = "shape=mxgraph.flowchart.process;";
	/**
	 * 控制点
	 */
	public static final  String CONTROLPOINT = "shape=mxgraph.flowchart.process;strokeWidth=2;strokeColor=#FFFF00";
	/**
	 * 关键控制点
	 */
	public static final  String KEYCONTROLPOINT = "shape=mxgraph.flowchart.process;strokeWidth=2;strokeColor=#FF0000";
	/**
	 * 开始节点
	 */
	public static final  String START = "shape=mxgraph.flowchart.terminator;fillColor=#ffffff;strokeColor=#000000;strokeWidth=1;";
	/**
	 * 结束节点
	 */
	public static final  String END = "shape=mxgraph.flowchart.terminator;fillColor=#ffffff;strokeColor=#000000;strokeWidth=1;";
	/**
	 * 垂直泳道
	 */
	public static final  String CONTAINER = "swimlane";
	/**
	 * 水平泳道
	 */
	public static final  String POOL = "swimlane;horizontal=0";
	/**
	 * 预设流程
	 */
	public static final  String PREFLOW = "shape=mxgraph.flowchart.predefined_process";
	/**
	 * 判断
	 */
	public static final  String JUDGE = "shape=mxgraph.flowchart.decision;";
	/**
	 * 控制
	 */
	public static final  String CONTROLMEASURE = "shape=mxgraph.flowchart.extract_or_measurement;fillColor=#FF8000;strokeColor=#CC6600;strokeWidth=2;gradientColor=#CC6600;labelPosition=left;align=right";
	/**
	 * 线条
	 */
	public static final  String EDGE = "edgeStyle=elbowEdgeStyle;elbow=horizontal";
	
	
	
	/**
	 * <pre>
	 * renderXML:将一个MxCell对象转为xml
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphCell
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public static String renderXML(GraphCell graphCell){
		StringBuffer sb = new StringBuffer();
		sb.append("<mxCell ");
		sb.append("id='").append(graphCell.getId()).append("' ");
		sb.append("value='").append(StringUtils.isNotBlank(graphCell.getValue())?graphCell.getValue().replaceAll("\\n", "&#xa;"):"").append("' ");
		sb.append("vertex='1' ");
		sb.append("parent='").append(graphCell.getParent()).append("' ");
		if(StringUtils.isNotBlank(graphCell.getStyle())){
			sb.append(" style='").append(graphCell.getStyle()).append("' ");
		}
		if(StringUtils.isNotBlank(graphCell.getEdge())){
			sb.append("edge='").append(graphCell.getEdge()).append("' ");
		}
		if(StringUtils.isNotBlank(graphCell.getSource())){
			sb.append("source='").append(graphCell.getSource()).append("' ");
		}
		if(StringUtils.isNotBlank(graphCell.getTarget())){
			sb.append("target='").append(graphCell.getTarget()).append("' ");
		}
		sb.append("><mxGeometry ");
		if(null != graphCell.getX()){
			sb.append("x='").append(graphCell.getX()).append("' ");
		}
		if(null != graphCell.getY()){
			sb.append("y='").append(graphCell.getY()).append("' ");
		}
		if(null != graphCell.getWidth()){
			sb.append("width='").append(graphCell.getWidth()).append("' ");
		}
		if(null != graphCell.getWidth()){
			sb.append("height='").append(graphCell.getHeight()).append("' ");
		}	
		if(StringUtils.isNotBlank(graphCell.getRelative())){
			sb.append("relative='1' ");
		}
		if(graphCell.getGraphCellPoints() != null && graphCell.getGraphCellPoints().size()>0){
			if(StringUtils.isNotBlank(graphCell.getRelative())){
				sb.append("relative='1' as='geometry'>");
			}else{
				sb.append("as='geometry'>");
			}
			for (GraphCellPoint graphCellPoint : graphCell.getGraphCellPoints()) {
				sb.append("<mxPoint x='").append(graphCellPoint.getPx()).append("' y='").append(graphCellPoint.getPy()).append("' as='").append(graphCellPoint.getpAs()).append("'/>");
			}
			sb.append("<Array as='points'>");
			for (GraphCellPoint graphCellPoint : graphCell.getArrayPoints()) {
				sb.append("<mxPoint x='").append(graphCellPoint.getPx()).append("' y='").append(graphCellPoint.getPy()).append("'/>");
			}
			sb.append("</Array>");
			sb.append("</mxGeometry>");
		}else{
			sb.append("as='geometry'/>");
		}
		sb.append("</mxCell>");
		return sb.toString();
	}

	/**
	 * <pre>
	 * renderMxCell:将xml转为一个MxCell构成的List
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param xml
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("rawtypes")
	public static List<GraphCell> renderGraphCell(String xml){
		InputSource in = new InputSource(new StringReader(xml));
		List<GraphCell> graphCellList = new ArrayList<GraphCell>();
		in.setEncoding("GBK");
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(in);
			Element rootElt = document.getRootElement(); // 获取根节点
			Element rootjd = rootElt.element("root");
			Iterator rootiter = rootjd.elementIterator("mxCell"); // 获取根节点下的子节点mxCell
			//封装mxCell
			while (rootiter.hasNext()) {
				GraphCell graphCell = new GraphCell();
				Element cellEle = (Element) rootiter.next();
				String id = cellEle.attributeValue("id");
				if(!id.equals("0") && !id.equals("1")){
					String value = cellEle.attributeValue("value");
					String style = cellEle.attributeValue("style");
					String parent = cellEle.attributeValue("parent");
					String vertex = cellEle.attributeValue("vertex");
					String edge = cellEle.attributeValue("edge");
					String source = cellEle.attributeValue("source");
					String target = cellEle.attributeValue("target");
					graphCell.setId(id);
					graphCell.setValue(StringUtils.isNotBlank(value)?value:"");
					graphCell.setStyle(style);
					graphCell.setParent(parent);
					graphCell.setVertex(vertex);
					graphCell.setEdge(edge);
					graphCell.setSource(source);
					graphCell.setTarget(target);
//					System.out.println("id:"+id);
//					System.out.println("value:"+value);
//					System.out.println("style:"+style);
//					System.out.println("parent:"+parent);
//					System.out.println("vertex:"+vertex);
//					System.out.println("edge:"+edge);
//					System.out.println("source:"+source);
//					System.out.println("target:"+target);
					Iterator celliter = cellEle.elementIterator("mxGeometry");
					while(celliter.hasNext()){
						Element geoEle = (Element) celliter.next();
						Double x = null;
						Double y = null;
						Double width = null;
						Double height = null;
						if(StringUtils.isNotBlank(geoEle.attributeValue("x"))){
							x =  Double.valueOf(geoEle.attributeValue("x"));
						}
						if(StringUtils.isNotBlank(geoEle.attributeValue("y"))){
							y =  Double.valueOf(geoEle.attributeValue("y"));
						}
						if(StringUtils.isNotBlank(geoEle.attributeValue("width"))){
							width =  Double.valueOf(geoEle.attributeValue("width"));
						}
						if(StringUtils.isNotBlank(geoEle.attributeValue("height"))){
							height =  Double.valueOf(geoEle.attributeValue("height"));
						}
						String relative = geoEle.attributeValue("relative");
						if(null != x){
							graphCell.setX(x);
						}
						if(null != y){
							graphCell.setY(y);
						}
						if(null != width){
							graphCell.setWidth(width);
						}
						if(null != height){
							graphCell.setHeight(height);
						}
						graphCell.setRelative(relative);
						Iterator pointiter = geoEle.elementIterator("mxPoint");
						Set<GraphCellPoint> graphCellPoints = new HashSet<GraphCellPoint>();
						while(pointiter.hasNext()){
							Element pointEle = (Element) pointiter.next();
							GraphCellPoint graphCellPoint = new GraphCellPoint();
							Double px = null;
							Double py = null;
							String pAs = null;
							if(StringUtils.isNotBlank(pointEle.attributeValue("x"))){
								px =  Double.valueOf(pointEle.attributeValue("x"));
							}
							if(StringUtils.isNotBlank(pointEle.attributeValue("y"))){
								py =  Double.valueOf(pointEle.attributeValue("y"));
							}
							if(StringUtils.isNotBlank(pointEle.attributeValue("as"))){
								pAs =  String.valueOf(pointEle.attributeValue("as"));
							}
							if(null != px){
								graphCellPoint.setPx(px);
							}
							if(null != py){
								graphCellPoint.setPy(py);
							}
							if(StringUtils.isNotBlank(pAs)){
								graphCellPoint.setpAs(pAs);
							}
							graphCellPoints.add(graphCellPoint);
						}
						Iterator arrayiter = geoEle.elementIterator("Array");
						Set<GraphCellPoint> arrayPoints = new HashSet<GraphCellPoint>();
						while(arrayiter.hasNext()){
							Element arrayEle = (Element) arrayiter.next();
							Iterator pointiter2 = arrayEle.elementIterator("mxPoint");
							while(pointiter2.hasNext()){
								Element pointEle = (Element) pointiter2.next();
								GraphCellPoint graphCellPoint = new GraphCellPoint();
								Double px = null;
								Double py = null;
								if(StringUtils.isNotBlank(pointEle.attributeValue("x"))){
									px =  Double.valueOf(pointEle.attributeValue("x"));
								}
								if(StringUtils.isNotBlank(pointEle.attributeValue("y"))){
									py =  Double.valueOf(pointEle.attributeValue("y"));
								}
								if(null != px){
									graphCellPoint.setPx(px);
								}
								if(null != py){
									graphCellPoint.setPy(py);
								}
								arrayPoints.add(graphCellPoint);
							}
						}
						graphCell.setArrayPoints(arrayPoints);
						graphCell.setGraphCellPoints(graphCellPoints);
//						System.out.println("x:"+x);
//						System.out.println("y:"+y);
//						System.out.println("width:"+width);
//						System.out.println("height:"+height);
//						System.out.println("relative:"+relative);
					}
					graphCellList.add(graphCell);
				}
			}
			return graphCellList;
		} catch (Exception e) {
			e.printStackTrace();
			return graphCellList;
		}
	}
	
	/**
	 * <pre>
	 * renderXML:将一个UserObject对象转为xml
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphCell
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public static String renderXML(GraphUserObject graphUserObject){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<UserObject ");
		sb.append("id='").append(graphUserObject.getId()).append("' ");
		sb.append("label='").append(graphUserObject.getLabel()).append("' ");
		sb.append("link='").append(graphUserObject.getLink()).append("'><mxCell ");
		
		if(StringUtils.isNotBlank(graphUserObject.getStyle())){
			sb.append(" style='").append(graphUserObject.getStyle()).append("' ");
		}
		sb.append("vertex='1' ");
		sb.append("parent='").append(graphUserObject.getParent()).append("' ");
		sb.append("><mxGeometry ");
		if(null != graphUserObject.getX()){
			sb.append("x='").append(graphUserObject.getX()).append("' ");
		}
		if(null != graphUserObject.getY()){
			sb.append("y='").append(graphUserObject.getY()).append("' ");
		}	
		if(null != graphUserObject.getWidth()){
			sb.append("width='").append(graphUserObject.getWidth()).append("' ");
		}		
		if(null != graphUserObject.getHeight()){
			sb.append("height='").append(graphUserObject.getHeight()).append("' ");
		}		
		if(graphUserObject.getGraphCellPoints() != null && graphUserObject.getGraphCellPoints().size()>0){
			if(StringUtils.isNotBlank(graphUserObject.getRelative())){
				sb.append("relative='1' as='geometry'>");
			}else{
				sb.append("as='geometry'>");
			} 
			for (GraphCellPoint graphCellPoint : graphUserObject.getGraphCellPoints()) {
				sb.append("<mxPoint x='").append(graphCellPoint.getPx()).append("' y='").append(graphCellPoint.getPy()).append("' as='").append(graphCellPoint.getpAs()).append("'/>");
			}
			sb.append("<Array as='points'>");
			for (GraphCellPoint graphCellPoint : graphUserObject.getArrayPoints()) {
				sb.append("<mxPoint x='").append(graphCellPoint.getPx()).append("' y='").append(graphCellPoint.getPy()).append("'/>");
			}
			sb.append("</Array>");
			sb.append("</mxGeometry>");
		}else{
			sb.append("as='geometry'/>");
		}
		sb.append("</mxCell></UserObject>");
		return sb.toString();
	} 
	
	/**
	 * <pre>
	 * renderGraphUserObject:将xml转为一个GraphUserObject构成的List
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param xml
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@SuppressWarnings("rawtypes")
	public static List<GraphUserObject> renderGraphUserObject(String xml){
		InputSource in = new InputSource(new StringReader(xml));
		List<GraphUserObject> graphCellList = new ArrayList<GraphUserObject>();
		in.setEncoding("GBK");
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(in);
			Element rootElt = document.getRootElement(); // 获取根节点
			Element rootjd = rootElt.element("root");
			Iterator rootiter = rootjd.elementIterator("UserObject"); // 获取根节点下的子节点UserObject

			//封装UserObject
			while (rootiter.hasNext()) {
				GraphUserObject graphUserObject = new GraphUserObject();
				Element userObjectEle = (Element) rootiter.next();
				String id = userObjectEle.attributeValue("id");
				String label = userObjectEle.attributeValue("label");
				String link = userObjectEle.attributeValue("link");
				graphUserObject.setId(id);
				graphUserObject.setLabel(label);
				graphUserObject.setLink(link);
				Iterator celliter = userObjectEle.elementIterator("mxCell");
				while (celliter.hasNext()) {
					Element cellEle = (Element) celliter.next();
					String style = cellEle.attributeValue("style");
					String parent = cellEle.attributeValue("parent");
					String vertex = cellEle.attributeValue("vertex");
					graphUserObject.setStyle(style);
					graphUserObject.setParent(parent);
					graphUserObject.setVertex(vertex);
					Iterator mxGeometryiter = cellEle.elementIterator("mxGeometry");
					while(mxGeometryiter.hasNext()){
						Element geoEle = (Element) mxGeometryiter.next();
						Double x = null;
						Double y = null;
						Double width = null;
						Double height = null;
						if(StringUtils.isNotBlank(geoEle.attributeValue("x"))){
							x =  Double.valueOf(geoEle.attributeValue("x"));
						}
						if(StringUtils.isNotBlank(geoEle.attributeValue("y"))){
							y =  Double.valueOf(geoEle.attributeValue("y"));
						}
						if(StringUtils.isNotBlank(geoEle.attributeValue("width"))){
							width =  Double.valueOf(geoEle.attributeValue("width"));
						}
						if(StringUtils.isNotBlank(geoEle.attributeValue("height"))){
							height =  Double.valueOf(geoEle.attributeValue("height"));
						}
						if(null != x){
							graphUserObject.setX(x);
						}
						if(null != y){
							graphUserObject.setY(y);
						}
						if(null != width){
							graphUserObject.setWidth(width);
						}
						if(null != height){
							graphUserObject.setHeight(height);
						}
						
						Iterator pointiter = geoEle.elementIterator("mxPoint");
						Set<GraphCellPoint> graphCellPoints = new HashSet<GraphCellPoint>();
						while(pointiter.hasNext()){
							Element pointEle = (Element) pointiter.next();
							GraphCellPoint graphCellPoint = new GraphCellPoint();
							Double px = null;
							Double py = null;
							String pAs = null;
							if(StringUtils.isNotBlank(pointEle.attributeValue("x"))){
								px =  Double.valueOf(pointEle.attributeValue("x"));
							}
							if(StringUtils.isNotBlank(pointEle.attributeValue("y"))){
								py =  Double.valueOf(pointEle.attributeValue("y"));
							}
							if(StringUtils.isNotBlank(pointEle.attributeValue("as"))){
								pAs =  String.valueOf(pointEle.attributeValue("as"));
							}
							if(null != px){
								graphCellPoint.setPx(px);
							}
							if(null != py){
								graphCellPoint.setPy(py);
							}
							if(StringUtils.isNotBlank(pAs)){
								graphCellPoint.setpAs(pAs);
							}
							graphCellPoints.add(graphCellPoint);
						}
						Iterator arrayiter = geoEle.elementIterator("Array");
						Set<GraphCellPoint> arrayPoints = new HashSet<GraphCellPoint>();
						while(arrayiter.hasNext()){
							Element arrayEle = (Element) arrayiter.next();
							Iterator pointiter2 = arrayEle.elementIterator("mxPoint");
							while(pointiter2.hasNext()){
								Element pointEle = (Element) pointiter2.next();
								GraphCellPoint graphCellPoint = new GraphCellPoint();
								Double px = null;
								Double py = null;
								if(StringUtils.isNotBlank(pointEle.attributeValue("x"))){
									px =  Double.valueOf(pointEle.attributeValue("x"));
								}
								if(StringUtils.isNotBlank(pointEle.attributeValue("y"))){
									py =  Double.valueOf(pointEle.attributeValue("y"));
								}
								if(null != px){
									graphCellPoint.setPx(px);
								}
								if(null != py){
									graphCellPoint.setPy(py);
								}
								arrayPoints.add(graphCellPoint);
							}
						}
						graphUserObject.setArrayPoints(arrayPoints);
						graphUserObject.setGraphCellPoints(graphCellPoints);
//						System.out.println("x:"+x);
//						System.out.println("y:"+y);
//						System.out.println("width:"+width);
//						System.out.println("height:"+height);
//						System.out.println("relative:"+relative);
					}
					graphCellList.add(graphUserObject);
				}
			}
			return graphCellList;
		} catch (Exception e) {
			e.printStackTrace();
			return graphCellList;
		}
	}
	/**
	 * <pre>
	 * render:封装graphCell的list为整个图的xml
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param graphCellList
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public static String render(List<GraphCell> graphCellList, List<GraphUserObject> graphUserObjectList){
		StringBuffer xml = new StringBuffer();//xml字符串
		xml.append("<mxGraphModel>");
		xml.append("<root>");
		xml.append("<mxCell id='0'/>");
		xml.append("<mxCell id='1' parent='0'/>");
		for (GraphCell graphCell : graphCellList) {
			xml.append(GraphCellXmlUtils.renderXML(graphCell));
		}
		for (GraphUserObject graphUserObject : graphUserObjectList) {
			xml.append(GraphCellXmlUtils.renderXML(graphUserObject));
		}
		xml.append("</root>");
		xml.append("</mxGraphModel>");
//		System.out.println(xml.toString());
		return xml.toString();
	}
	
	/**
	 * 保存拓扑节点坐标信息
	 * 
	 * @param userInfo
	 * @return
	 */
/*	@SuppressWarnings("finally")
	public static int saveTopoData(String xmldata) {
		int updateResult = 1;
		//Map<String, String> paraMap = new HashMap<String, String>();
		InputSource in = new InputSource(new StringReader(xmldata));
		// in.setEncoding("UTF-8");
		in.setEncoding("GBK");
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(in);
			// ---------------------------方案一-----------------------------------------------------
			Element rootElt = document.getRootElement(); // 获取根节点
			Element rootjd = rootElt.element("root");
			@SuppressWarnings("rawtypes")
			Iterator rootiter = rootjd.elementIterator("mxCell"); // 获取根节点下的子节点mxCell
			while (rootiter.hasNext()) {
				Element cellEle = (Element) rootiter.next();
				String id = cellEle.attributeValue("id");
				String value = cellEle.attributeValue("value");
				String style = cellEle.attributeValue("style");
				String parent = cellEle.attributeValue("parent");
				String vertex = cellEle.attributeValue("vertex");
				String edge = cellEle.attributeValue("edge");
				String source = cellEle.attributeValue("source");
				String target = cellEle.attributeValue("target");
				Double x = Double.valueOf(StringUtils.isNotBlank(cellEle.attributeValue("x"))?cellEle.attributeValue("x"):"0");
				Double y = Double.valueOf(StringUtils.isNotBlank(cellEle.attributeValue("y"))?cellEle.attributeValue("y"):"0");
				Double width = Double.valueOf(StringUtils.isNotBlank(cellEle.attributeValue("width"))?cellEle.attributeValue("width"):"0");
				Double height = Double.valueOf(StringUtils.isNotBlank(cellEle.attributeValue("height"))?cellEle.attributeValue("height"):"0");
				String relative = cellEle.attributeValue("relative");
				System.out.println("id:"+id);
				System.out.println("value:"+value);
				System.out.println("style:"+style);
				System.out.println("parent:"+parent);
				System.out.println("vertex:"+vertex);
				System.out.println("edge:"+edge);
				System.out.println("source:"+source);
				System.out.println("target:"+target);
				System.out.println("x:"+x);
				System.out.println("y:"+y);
				System.out.println("width:"+width);
				System.out.println("height:"+height);
				System.out.println("relative:"+relative);
				
//				if (autoSaveNode != null && !"".equals(autoSaveNode)) {
//					System.out.println("==节点允许保存:" + autoSaveNode);
//					Element xyEle = cellEle.element("mxGeometry");
//					System.out
//							.println("节点id：" + cellEle.attributeValue("id"));
//					System.out.println("x坐标：" + xyEle.attributeValue("x"));
//					System.out.println("y坐标：" + xyEle.attributeValue("y"));
//
//					String zbElementX = xyEle.attributeValue("x") == null ? "0"
//							: xyEle.attributeValue("x");
//					String zbElementY = xyEle.attributeValue("y") == null ? "0"
//							: xyEle.attributeValue("y");
//					if (zbElementX.contains(".")) {
//						zbElementX = zbElementX.substring(0,
//								zbElementX.indexOf("."));
//					}
//
//					if (zbElementY.contains(".")) {
//						zbElementY = zbElementY.substring(0,
//								zbElementY.indexOf("."));
//					}
//
//					// 节点只是移动了位置
//					paraMap.put(
//							"deviceid",
//							Long.parseLong(cellEle.attributeValue("deviceid"))
//									+ "");
//					paraMap.put("xpoint", zbElementX);
//					paraMap.put("ypoint", zbElementY);
//
//				}
//			}
			// -------------------------------------------------------------------------------------
			// ---------------------------方案二-----------------------------------------------------

//			String xpath = "//mxCell[@autoSaveNode]"; List<Element> eList =
//			document.selectNodes(xpath);//获取所有拥有autoSaveNode属性的mxCell节点
//			
//			for (Iterator iterator = eList.iterator(); iterator.hasNext();) {
//			Element element = (Element) iterator.next(); Element zbElement =
//			(Element) element.elements().get(0);//坐标数据节点
//			System.out.println("节点id："+element.attributeValue("id"));
//			System.out.println("x坐标："+zbElement.attributeValue("x"));
//			System.out.println("y坐标："+zbElement.attributeValue("y")); String
//			zbElementX = zbElement.attributeValue("x")==null?"0":zbElement.
//			attributeValue("x"); String zbElementY =
//			zbElement.attributeValue(
//			"y")==null?"0":zbElement.attributeValue("y"); if
//			(zbElementX.contains(".")) { zbElementX =
//			zbElementX.substring(0,zbElementX.indexOf(".")); }
//			
//			if (zbElementY.contains(".")) { zbElementY =
//			zbElementY.substring(0,zbElementY.indexOf(".")); }
//			
//			//节点只是移动了位置
//			paraMap.put("deviceid",Long.parseLong(element.attributeValue
//			("deviceid"))+""); paraMap.put("xpoint",zbElementX);
//			paraMap.put("ypoint",zbElementY);
//			
//			topoDAO.saveTopoData(paraMap);
//			
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			return updateResult;

		}
	}
*/

}

