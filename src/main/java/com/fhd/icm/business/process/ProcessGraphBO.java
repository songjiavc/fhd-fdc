/**
 * GraphBO.java
 * com.fhd.icm.business.process
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-9-11 		张 雷
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.icm.business.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.process.ProcessGraphDAO;
import com.fhd.entity.process.ProcessGraph;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessPointRelaMeasure;
import com.fhd.entity.process.ProcessPointRelaOrg;
import com.fhd.entity.process.ProcessPointRelaPointSelf;
import com.fhd.entity.process.ProcessPointRelaRisk;
import com.fhd.fdc.utils.Contents;
import com.fhd.icm.utils.mxgraph.GraphCell;
import com.fhd.icm.utils.mxgraph.GraphCellXmlUtils;
import com.fhd.icm.utils.mxgraph.GraphUserObject;
import com.fhd.ra.business.risk.RiskOutsideBO;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-9-11		上午9:38:40
 *
 * @see 	 
 */
@Service
public class ProcessGraphBO {
	@Autowired
	private ProcessGraphDAO o_processGraphDAO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	/** 
	* @Title: getProcessGraphByProcessId 
	* @Description: 根据流程ID获得流程设计视图
	* @param @param processId 流程ID
	* @param @return    流程设计视图对象
	* @return ProcessGraph    返回类型 
	* @throws 
	*/ 
	public ProcessGraph getProcessGraphByProcessId(String processId){
		return o_processGraphDAO.findUniqueBy("processId", processId);
	}
	
	/** 
	* @Title: updateProcessGraph 
	* @Description: 更新流程设计视图
	* @param @param flowchart    要更新的流程设计视图
	* @return void    无返回
	* @throws 
	*/ 
	
	@Transactional
	public void updateProcessGraph(ProcessGraph processGraph){
		try {
			ProcessGraph tmp = getProcessGraphByProcessId(processGraph.getProcessId());
			if(tmp!=null && tmp.getId()!=null && tmp.getId().length()>0){
				tmp.setGraphName(processGraph.getGraphName());
				tmp.setGraphContext(processGraph.getGraphContext());
				o_processGraphDAO.merge(tmp);
			}else{
				processGraph.setId(Identities.uuid());
				o_processGraphDAO.merge(processGraph);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>
	 * initXML:初始化该流程的流程图的xml
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param processureId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public String renderXML(String processId,String url){
		List<GraphCell> mxcellList = new ArrayList<GraphCell>();
		
		Map<String,String> orgMap = new LinkedHashMap<String,String>();//存放所有节点的责任部门

		List<ProcessPoint> pcpList = o_processPointBO.findProcessPointListByProcessId(processId);//存放所有节点
		List<ProcessPointRelaPointSelf> cprList = o_processPointBO.findProcessPointRelaPointSelfBySome(processId);//存放所有节点的关系
		List<ProcessPoint> firstList = o_processPointBO.getFirstControlPoint(processId);//存放第一个节点的列表
		List<ProcessPoint> lastList = o_processPointBO.getLastControlPoint(processId);//存放最后一个节点的列表
		
		Double x = Double.valueOf(80);//泳道的初始x坐标
		Double y = Double.valueOf(30);//泳道的初始y坐标
		Double width = Double.valueOf(100);//泳道的初始width
		Double height = Double.valueOf(50);//泳道的初始height
		for (ProcessPoint pcpoint : pcpList) {//遍历所有流程节点
		//	int c_i = 1;//控制措施的序号
			Double controlMeasureX = Double.valueOf(10);//控制措施的x坐标
			Double controlMeasureY = null;//控制措施的y坐标
			Double controlMeasureWidth = Double.valueOf(13);//控制措施的width
			Double controlMeasureHeight = Double.valueOf(13);//控制措施的height
			
			//int r_i = 1;//风险的序号
			Double riskX = controlMeasureX+controlMeasureWidth+width;//风险的x坐标
			Double riskY = null;//风险的y坐标
			Double riskWidth = Double.valueOf(13);//风险的width
			Double riskHeight = Double.valueOf(13);//风险的height
			
			String parent = null;
			for (ProcessPointRelaOrg ppro : pcpoint.getProcessPointRelaOrg()) {
				//如果有责任部门，则将该流程节点的parent设置为该节点的责任部门id
				if(Contents.ORG_RESPONSIBILITY.equals(ppro.getType()) && null != ppro.getOrg().getId()){
					parent = ppro.getOrg().getId();
					if(!orgMap.containsKey(ppro.getOrg().getId())){//将流程节点的责任部门加入到orgMap
						orgMap.put(ppro.getOrg().getId(), ppro.getOrg().getOrgname());
					}
				}
			}
			if(StringUtils.isBlank(parent)){
				parent = "1";
			}
			for (ProcessPoint first : firstList) {//遍历所有第一个节点的集合
				if(pcpoint.getId().equals(first.getId())){
					//开始节点
					String startpointId = "startpoint"+first.getId()+"@startpoint";
					GraphCell graphCell = new GraphCell();
					graphCell.setId(startpointId);
					graphCell.setStyle(GraphCellXmlUtils.START);
					graphCell.setValue("开始");
					graphCell.setParent(parent);
					graphCell.setX(controlMeasureX+controlMeasureWidth);
					graphCell.setY(y);
					graphCell.setWidth(width);
					graphCell.setHeight(30.0);
					mxcellList.add(graphCell);
					
					//开始节点到第一个节点的线
					GraphCell line = new GraphCell();
					line.setId("startline"+first.getId()+"@line");
					line.setEdge("1");
					line.setStyle(GraphCellXmlUtils.EDGE);
					line.setSource(startpointId);
					line.setTarget(pcpoint.getId()+"@processPoint");
					line.setParent("1");
					line.setValue("");
					line.setRelative("1");
					mxcellList.add(line);
				}
			}
			y += height +Double.valueOf(30);
			//拼装节点的xml
			GraphCell graphCell = new GraphCell();
			String id = pcpoint.getId();
			String value = pcpoint.getName();
			String style = null;
			String etype = pcpoint.getPointType().getId();
			if("sub_process".equals(etype)){//如果是子流程
				//id = pcpoint.getRelaProcessure().getId();
				value = pcpoint.getName();
				style = GraphCellXmlUtils.PREFLOW;
			}else if("business".equals(etype)){//如果是业务活动
				style = GraphCellXmlUtils.OPERATIONALACTION;
			}else if("control_point".equals(etype)){//如果是控制点
				style = GraphCellXmlUtils.CONTROLPOINT;
			}else if("key_control_point".equals(etype)){//如果是关键控制点
				style = GraphCellXmlUtils.KEYCONTROLPOINT;
			}else{//如果是判定类型
				style = GraphCellXmlUtils.JUDGE;
			}
			graphCell.setId(id+"@processPoint");
			graphCell.setStyle(style);
			graphCell.setValue(value);
			graphCell.setParent(parent);
			graphCell.setX(controlMeasureX+controlMeasureWidth);
			graphCell.setY(y);
			graphCell.setWidth(width);
			graphCell.setHeight(height);
			mxcellList.add(graphCell);
			controlMeasureY = y;
			for (ProcessPointRelaMeasure pprm : pcpoint.getProcessPointRelaMeasures()) {
				if(null != pprm.getProcessPoint() && pcpoint.getId().equals(pprm.getProcessPoint().getId())){
					//拼装节点相关联的控制措施的xml
					GraphCell mixCellControlMeasure = new GraphCell();
					mixCellControlMeasure.setId(pcpoint.getId()+"^"+pprm.getControlMeasure().getId()+"@controlMeasure");
					mixCellControlMeasure.setValue(pprm.getControlMeasure().getCode());
					mixCellControlMeasure.setParent(parent);
					//mixCellControlMeasure.setStyle("image;image="+url+"/scripts/mxgraph/images/cube_green.png");
					mixCellControlMeasure.setStyle(GraphCellXmlUtils.CONTROLMEASURE);
					mixCellControlMeasure.setX(controlMeasureX);
					mixCellControlMeasure.setY(controlMeasureY);
					mixCellControlMeasure.setWidth(controlMeasureWidth);
					mixCellControlMeasure.setHeight(controlMeasureHeight);
					mxcellList.add(mixCellControlMeasure);
					controlMeasureY += controlMeasureHeight;//控制措施的y坐标下移一个controlMeasureHeight
					//c_i++;
				}
			}
			riskY = y;
			for (ProcessPointRelaRisk pprr : pcpoint.getProcessPointRelaRisks()) {
				if(null != pprr.getProcessPoint() &&pcpoint.getId().equals(pprr.getProcessPoint().getId())){
					//拼装节点关联的风险的xml
					GraphCell mxCellRisk = new GraphCell();
					mxCellRisk.setId(pcpoint.getId()+"^"+pprr.getRisk().getId()+"@risk");
					mxCellRisk.setValue(pprr.getRisk().getCode());
					mxCellRisk.setParent(parent);
					String riskStatus = o_riskOutsideBO.findRiskAssementStatusByRiskId(pprr.getRisk().getId());
					if("icon-ibm-symbol-6-sm".equals(riskStatus)){//低风险	
						mxCellRisk.setStyle(GraphCellXmlUtils.LOWRISK);
					}else if("icon-ibm-symbol-5-sm".equals(riskStatus)){//中风险
						mxCellRisk.setStyle(GraphCellXmlUtils.MODERATERISK);
					}else if("icon-ibm-symbol-4-sm".equals(riskStatus)){//高风险
						mxCellRisk.setStyle(GraphCellXmlUtils.HIGHRISK);
					}else{//未评风险
						mxCellRisk.setStyle(GraphCellXmlUtils.NOTEVALUATEDRISK);
					}
					mxCellRisk.setX(riskX);
					mxCellRisk.setY(riskY);
					mxCellRisk.setWidth(riskWidth);
					mxCellRisk.setHeight(riskHeight);
					mxcellList.add(mxCellRisk);
					riskY += riskHeight;//风险的y坐标下移一个riskHeight
				//	r_i++;
				}
			}
			for (ProcessPoint last : lastList) {
				if(last.getId().equals(pcpoint.getId())){
					y += height + Double.valueOf(30);//y坐标下移(一个节点的高度+30)
					//结束节点
					String endpointId = "endpoint"+last.getId()+"@endpoint";
					GraphCell end = new GraphCell();
					end.setId(endpointId);
					end.setStyle(GraphCellXmlUtils.END);
					end.setValue("结束");
					end.setParent(parent);
					end.setX(controlMeasureX+controlMeasureWidth);
					end.setY(y);
					end.setWidth(width);
					end.setHeight(30.0);
					mxcellList.add(end);
					//开始节点的线
					GraphCell line = new GraphCell();
					line.setId("endline"+last.getId()+"@line");
					line.setEdge("1");
					line.setStyle(GraphCellXmlUtils.EDGE);
					line.setSource(pcpoint.getId()+"@processPoint");
					line.setTarget(endpointId);
					line.setParent("1");
					line.setValue("");
					line.setRelative("1");
					mxcellList.add(line);
				}
			}
		}
		for (ProcessPointRelaPointSelf cpr : cprList) {
			//拼装节点的关系的xml
			GraphCell line = new GraphCell();
			line.setId(cpr.getId()+"@line");
			line.setEdge("1");
			line.setStyle(GraphCellXmlUtils.EDGE);
			line.setSource(cpr.getPreviousProcessPoint().getId()+"@processPoint");
			line.setTarget(cpr.getProcessPoint().getId()+"@processPoint");
			line.setParent("1");
			line.setRelative("1");
			line.setValue(cpr.getDesc()!=null?cpr.getDesc():"");
			mxcellList.add(line);
		}
		Set<Map.Entry<String,String>> entrySet = orgMap.entrySet();
		Iterator<Map.Entry<String,String>> iter = entrySet.iterator(); 
		while(iter.hasNext()){ 
			//拼装泳道的xml
			Map.Entry<String,String> entry = iter.next();
			String key = entry.getKey(); 
			GraphCell graphCell = new GraphCell();
			graphCell.setId(key);
			graphCell.setValue(entry.getValue());
			graphCell.setParent("1");
			graphCell.setStyle(GraphCellXmlUtils.CONTAINER);
			graphCell.setX(x);
			graphCell.setY(Double.valueOf(10));
			graphCell.setWidth(width*2);
			graphCell.setHeight(y+Double.valueOf(30));
			mxcellList.add(graphCell);
			x += width*2;
		} 
		//System.out.println(GraphCellXmlUtils.render(mxcellList));
		return GraphCellXmlUtils.render(mxcellList, new ArrayList<GraphUserObject>());
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessGraph> findProcessGraphListBySome(){
		Criteria criteria = o_processGraphDAO.createCriteria();
		return criteria.list();
	}
	
}

