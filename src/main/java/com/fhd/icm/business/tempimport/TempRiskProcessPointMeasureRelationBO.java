package com.fhd.icm.business.tempimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.tempimport.TempRiskProcessPointMeasureRelationDAO;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.icm.control.MeasureRelaRisk;
import com.fhd.entity.icm.tempimport.TempControlMeasure;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.icm.tempimport.TempProcessPoint;
import com.fhd.entity.icm.tempimport.TempRiskProcessPointMeasureRelation;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessPointRelaMeasure;
import com.fhd.entity.process.ProcessPointRelaRisk;
import com.fhd.entity.process.ProcessRelaMeasure;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.assess.MeasureBO;
import com.fhd.icm.business.assess.MeasureRelaRiskBO;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.process.ProcessPointBO;
import com.fhd.icm.business.process.ProcessPointRelaMeasureBO;
import com.fhd.icm.business.process.ProcessRelaMeasureBO;
import com.fhd.ra.business.risk.ProcessPointRelaRiskBO;
import com.fhd.ra.business.risk.ProcessRelaRiskBO;
import com.fhd.ra.business.risk.RiskBO;

/**
 * 风险--流程--流程节点--控制措施关系导入临时表BO.
 * @author 吴德福
 * @Date 2013-12-12 16:27:32
 */
@Service
@SuppressWarnings("unchecked")
public class TempRiskProcessPointMeasureRelationBO {

	@Autowired
	private TempRiskProcessPointMeasureRelationDAO o_tempRiskProcessPointMeasureRelationDAO;
	@Autowired
	private RiskBO o_riskBO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private TempProcessBO o_tempProcessBO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	@Autowired
	private TempProcessPointBO o_tempProcessPointBO;
	@Autowired
	private MeasureBO o_controlMeasureBO;
	@Autowired
	private TempControlMeasureBO o_tempControlMeasureBO;
	@Autowired
	private ProcessRelaRiskBO o_processRelaRiskBO;
	@Autowired
	private ProcessPointRelaRiskBO o_processPointRelaRiskBO;
	@Autowired
	private MeasureRelaRiskBO o_measureRelaRiskBO;
	@Autowired
	private ProcessRelaMeasureBO o_processRelaMeasureBO;
	@Autowired
	private ProcessPointRelaMeasureBO o_processPointRelaMeasureBO;
	
	/**
	 * 验证风险--流程--流程节点--控制措施关系.
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateRiskProcessPointMeasureRelationData(String fileId){
		Map<String,Object> processPointRelationMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempRiskProcessPointMeasureRelation> tempRiskProcessPointMeasureRelationList = this.findRiskProcessPointMeasureRelationPreviewListBySome("", fileId);

		//真实表中的风险list
		List<Risk> riskList = o_riskBO.findRiskListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> riskCodeList = new ArrayList<String>();
		for (Risk risk : riskList) {
			if(!riskCodeList.contains(risk.getCode())){
				riskCodeList.add(risk.getCode());
			}
		}
		//真实表中的流程list
		List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> processCodeList = new ArrayList<String>();
		for (Process process : processList) {
			if(!processCodeList.contains(process.getCode())){
				processCodeList.add(process.getCode());
			}
		}
		//临时表中流程list
		List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", fileId);
		for (TempProcess tempProcess : tempProcessList) {
			if(!processCodeList.contains(tempProcess.getProcessureCode())){
				processCodeList.add(tempProcess.getProcessureCode());
			}
		}
		//真实表中的流程节点list
		List<ProcessPoint> processPointList = o_processPointBO.findProcessPointByCompanyId(UserContext.getUser().getCompanyid());
		List<String> processPointCodeList = new ArrayList<String>();
		for (ProcessPoint processPoint : processPointList) {
			if(!processPointCodeList.contains(processPoint.getCode())){
				processPointCodeList.add(processPoint.getCode());
			}
		}
		//临时表中流程节点list
		List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", fileId);
		for (TempProcessPoint tempProcessPoint : tempProcessPointList) {
			if(!processPointCodeList.contains(tempProcessPoint.getProcessPointCode())){
				processPointCodeList.add(tempProcessPoint.getProcessPointCode());
			}
		}
		//真实表中的控制措施list
		List<Measure> controlMeasureList = o_controlMeasureBO.findMeasureListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> controlMeasureCodeList = new ArrayList<String>();
		for (Measure controlMeasure : controlMeasureList) {
			if(!controlMeasureCodeList.contains(controlMeasure.getCode())){
				controlMeasureCodeList.add(controlMeasure.getCode());
			}
		}
		//临时表中的控制措施list
		List<TempControlMeasure> tempControlMeasureList = o_tempControlMeasureBO.findControlMeasurePreviewListBySome("", fileId);
		for (TempControlMeasure tempControlMeasure : tempControlMeasureList) {
			if(!controlMeasureCodeList.contains(tempControlMeasure.getControlMeasureCode())){
				controlMeasureCodeList.add(tempControlMeasure.getControlMeasureCode());
			}
		}
		
		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempRiskProcessPointMeasureRelation tempRiskProcessPointMeasureRelation : tempRiskProcessPointMeasureRelationList) {
			StringBuffer errorTip=new StringBuffer();
			
			if(StringUtils.isNotBlank(tempRiskProcessPointMeasureRelation.getRiskCode()) && !riskCodeList.contains(tempRiskProcessPointMeasureRelation.getRiskCode())){
				//验证风险编号是否正确
				errorTip.append("(").append("A").append(i).append(")").append("风险编号不存在!");
			}
			if(StringUtils.isNotBlank(tempRiskProcessPointMeasureRelation.getProcessCode()) && !processCodeList.contains(tempRiskProcessPointMeasureRelation.getProcessCode())){
				//验证流程编号是否正确
				errorTip.append("(").append("C").append(i).append(")").append("流程编号不存在!");
			}
			if(StringUtils.isNotBlank(tempRiskProcessPointMeasureRelation.getProcessPointCode()) && !processPointCodeList.contains(tempRiskProcessPointMeasureRelation.getProcessPointCode())){
				//验证流程节点编号是否正确
				errorTip.append("(").append("E").append(i).append(")").append("流程节点编号不存在!");
			}
			if(StringUtils.isNotBlank(tempRiskProcessPointMeasureRelation.getControlMeasureCode()) && !controlMeasureCodeList.contains(tempRiskProcessPointMeasureRelation.getControlMeasureCode())){
				//验证控制标准(要求)编号是否正确
				errorTip.append("(").append("G").append(i).append(")").append("控制措施编号不存在!");
			}
			
			if(errorTip.length()>0){
				errorCount++;
				tempRiskProcessPointMeasureRelation.setErrorTip(errorTip.toString());
				o_tempRiskProcessPointMeasureRelationDAO.merge(tempRiskProcessPointMeasureRelation);
			}
			i++;
		}
		
		processPointRelationMap.put("allCount", tempRiskProcessPointMeasureRelationList.size());
		processPointRelationMap.put("correctCount", tempRiskProcessPointMeasureRelationList.size()-errorCount);
		processPointRelationMap.put("errorCount", errorCount);
		
		return processPointRelationMap;
	}
	/**
	 * excel表数据存入风险--流程--流程节点--控制措施关系临时表.
	 * @author 吴德福
	 * @param dataListList
	 * @param fileUploadEntity
	 */
	@Transactional
	public void saveTempProcessStandardRiskRelation(List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		List<TempRiskProcessPointMeasureRelation> tempRiskProcessPointMeasureRelationList = new ArrayList<TempRiskProcessPointMeasureRelation>();
		//真实表中的风险list
		List<Risk> riskList = o_riskBO.findRiskListByCompanyId(UserContext.getUser().getCompanyid());
		//真实表中的流程list
		List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		//临时表中流程list
		List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		//真实表中的流程节点list
		List<ProcessPoint> processPointList = o_processPointBO.findProcessPointByCompanyId(UserContext.getUser().getCompanyid());
		//临时表中流程节点list
		List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		//真实表中的控制措施list
		List<Measure> controlMeasureList = o_controlMeasureBO.findMeasureListByCompanyId(UserContext.getUser().getCompanyid());
		//临时表中的控制措施list
		List<TempControlMeasure> tempControlMeasureList = o_tempControlMeasureBO.findControlMeasurePreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempRiskProcessPointMeasureRelation tempRiskProcessPointMeasureRelation = new TempRiskProcessPointMeasureRelation();
			tempRiskProcessPointMeasureRelation.setId(Identities.uuid());
			tempRiskProcessPointMeasureRelation.setIndex(String.valueOf(i+1+START_ROW));
			tempRiskProcessPointMeasureRelation.setRiskCode(dataList.get(j).get(0));
			tempRiskProcessPointMeasureRelation.setRiskName(dataList.get(j).get(1));
			tempRiskProcessPointMeasureRelation.setProcessCode(dataList.get(j).get(2));
			tempRiskProcessPointMeasureRelation.setProcessName(dataList.get(j).get(3));
			tempRiskProcessPointMeasureRelation.setProcessPointCode(dataList.get(j).get(4));
			tempRiskProcessPointMeasureRelation.setProcessPointName(dataList.get(j).get(5));
			tempRiskProcessPointMeasureRelation.setControlMeasureCode(dataList.get(j).get(6));
			tempRiskProcessPointMeasureRelation.setControlMeasureName(dataList.get(j).get(7));
			tempRiskProcessPointMeasureRelation.setFileUploadEntity(fileUploadEntity);
			
			String riskId = "";
			for(Risk risk : riskList){
				if(dataList.get(j).get(0).equals(risk.getCode())){//risk.getCode有可能是空
					//真实表中存在
					riskId = risk.getId();
					break;
				}
			}
			if(!"".equals(riskId)){
				tempRiskProcessPointMeasureRelation.setRiskId(riskId);
			}
			
			String processId = "";
			for(Process process : processList){
				if(process.getCode().equals(dataList.get(j).get(2))){
					//真实表中存在
					processId = process.getId();
					break;
				}
			}
			if("".equals(processId)){
				//真实表中不存在
				for(TempProcess tempProcess : tempProcessList){
					if(tempProcess.getProcessureCode().equals(dataList.get(j).get(2))){
						//临时表中存在
						processId = tempProcess.getId();
						break;
					}
				}
			}
			if(!"".equals(processId)){
				tempRiskProcessPointMeasureRelation.setProcessId(processId);
			}
			
			String processPointId = "";
			for(ProcessPoint processPoint : processPointList){
				if(processPoint.getCode().equals(dataList.get(j).get(4))){
					//真实表中存在
					processPointId = processPoint.getId();
					break;
				}
			}
			if("".equals(processPointId)){
				//真实表中不存在
				for(TempProcessPoint tempProcessPoint : tempProcessPointList){
					if(tempProcessPoint.getProcessPointCode().equals(dataList.get(j).get(4))){
						//临时表中存在
						processPointId = tempProcessPoint.getId();
						break;
					}
				}
			}
			if(!"".equals(processPointId)){
				tempRiskProcessPointMeasureRelation.setProcessPointId(processPointId);
			}
			
			String controlMeasureId = "";
			for(Measure controlMeasure : controlMeasureList){
				if(controlMeasure.getCode().equals(dataList.get(j).get(6))){
					//真实表中存在
					controlMeasureId = controlMeasure.getId();
					break;
				}
			}
			if("".equals(controlMeasureId)){
				//真实表中不存在
				for(TempControlMeasure tempControlMeasure : tempControlMeasureList){
					if(tempControlMeasure.getControlMeasureCode().equals(dataList.get(j).get(6))){
						//临时表中存在
						controlMeasureId = tempControlMeasure.getId();
						break;
					}
				}
			}
			if(!"".equals(controlMeasureId)){
				tempRiskProcessPointMeasureRelation.setControlMeasureId(controlMeasureId);
			}
			
			//o_tempRiskProcessPointMeasureRelationDAO.merge(tempRiskProcessPointMeasureRelation);
			tempRiskProcessPointMeasureRelationList.add(tempRiskProcessPointMeasureRelation);
			i++;
		}
		//批量导入临时表数据
		batchSaveTempData(tempRiskProcessPointMeasureRelationList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempRiskProcessPointMeasureRelation> tempRiskProcessPointMeasureRelationList){
		o_tempRiskProcessPointMeasureRelationDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_RISK_PROCESS_POINT_MEASURE_RELATION(id,E_INDEX,RISK_CODE,RISK_NAME,PROCESS_CODE," +
						"PROCESS_NAME,PROCESS_POINT_CODE,PROCESS_POINT_NAME,CONTROL_MEASURE_CODE,CONTROL_MEASURE_NAME,RISK_ID," +
						"PROCESS_ID,PROCESS_POINT_ID,CONTROL_MEASURE_ID) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempRiskProcessPointMeasureRelation o : tempRiskProcessPointMeasureRelationList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getRiskCode());
					pst.setObject(4, o.getRiskName());
					pst.setObject(5, o.getProcessCode());
					pst.setObject(6, o.getProcessName());
					pst.setObject(7, o.getProcessPointCode());
					pst.setObject(8, o.getProcessPointName());
					pst.setObject(9, o.getControlMeasureCode());
					pst.setObject(10, o.getControlMeasureName());
					pst.setObject(11, o.getRiskId());
					pst.setObject(12, o.getProcessId());
					pst.setObject(13, o.getProcessPointId());
					pst.setObject(14, o.getControlMeasureId());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempRiskProcessPointMeasureRelationDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的风险--流程--流程节点--控制措施关系临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempRiskProcessPointMeasureRelation>
	 */
	public List<TempRiskProcessPointMeasureRelation> findRiskProcessPointMeasureRelationPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempRiskProcessPointMeasureRelationDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.like("riskCode", query, MatchMode.ANYWHERE), Restrictions.like("processCode", query, MatchMode.ANYWHERE)), Restrictions.or(Restrictions.like("processPointCode", query, MatchMode.ANYWHERE), Restrictions.like("controlMeasureCode", query, MatchMode.ANYWHERE))));
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		criteria.addOrder(Order.desc("errorTip"));
		criteria.addOrder(Order.asc("index"));
		return criteria.list();
	}
	/**
	 * 批量删除风险--流程--流程节点--控制措施关系临时表.
	 * @author 吴德福
	 */
	@Transactional
	public void removeBatchTempRiskProcessPointMeasureRelationByHql(){
		String hql="delete from TempRiskProcessPointMeasureRelation";
		o_tempRiskProcessPointMeasureRelationDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempRiskProcessPointMeasureRelationDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempRiskProcessPointMeasureRelation> tempRiskProcessPointMeasureRelationList = (List<TempRiskProcessPointMeasureRelation>) map.get("tempRiskProcessPointMeasureRelationList");

				//风险--流程关系sql--4个字段
				String riskProcessSql = "insert into t_processure_risk_processure(id,processure_id,risk_id,etype) values(?,?,?,?)";
				//风险--流程节点关系sql--4个字段
				String riskProcessPointsql = "insert into t_ic_control_point_rela_risk(id,processure_id,control_point_id,risk_id) values(?,?,?,?)";
				//风险--控制措施关系sql--3个字段
				String riskControlMeasureSql = "insert into t_con_measure_rela_risk(id,control_measure_id,risk_id) values(?,?,?)";
				//流程--控制措施关系sql--3个字段
				String processControlMeasureSql = "insert into t_ic_measure_rela_processure(id,processure_id,control_measure_id) values(?,?,?)";
				//流程节点--控制措施关系sql--4个字段
				String processPointControlMeasureSql = "insert into t_ic_measure_rela_control_poin(id,processure_id,control_measure_id,control_point_id) values(?,?,?,?)";
				
				//验证风险相关流程关系是否存在
				List<ProcessRelaRisk> riskRelaProcessList = o_processRelaRiskBO.findRiskRelaProcessListByCompanyId(UserContext.getUser().getCompanyid());
				//验证风险相关流程节点关系是否存在
				List<ProcessPointRelaRisk> riskRelaProcessPointList = o_processPointRelaRiskBO.findProcessPointRelaRiskListByCompanyId(UserContext.getUser().getCompanyid());
				//验证风险相关控制措施关系是否存在
				List<MeasureRelaRisk> riskRelaMeasureList = o_measureRelaRiskBO.findMeasureRelaRiskListByCompanyId(UserContext.getUser().getCompanyid());
				//验证流程相关控制措施关系是否存在
				List<ProcessRelaMeasure> processRelaMeasureList = o_processRelaMeasureBO.findProcessRelaMeasureListByCompanyId(UserContext.getUser().getCompanyid());
				//验证流程相关控制措施关系是否存在
				List<ProcessPointRelaMeasure> processPointRelaMeasureList = o_processPointRelaMeasureBO.findProcessPointRelaMeasureListByCompanyId(UserContext.getUser().getCompanyid());
				
				//验证风险相关流程关系是否存在
				List<ProcessRelaRisk> tempRiskRelaProcessList = new ArrayList<ProcessRelaRisk>();
				//验证风险相关流程节点关系是否存在
				List<ProcessPointRelaRisk> tempRiskRelaProcessPointList = new ArrayList<ProcessPointRelaRisk>();
				//验证风险相关控制措施关系是否存在
				List<MeasureRelaRisk> tempRiskRelaMeasureList = new ArrayList<MeasureRelaRisk>();
				//验证流程相关控制措施关系是否存在
				List<ProcessRelaMeasure> tempProcessRelaMeasureList = new ArrayList<ProcessRelaMeasure>();
				//验证流程相关控制措施关系是否存在
				List<ProcessPointRelaMeasure> tempProcessPointRelaMeasureList = new ArrayList<ProcessPointRelaMeasure>();
				
				PreparedStatement riskProcessPst = connection.prepareStatement(riskProcessSql);
				PreparedStatement riskProcessPointPst = connection.prepareStatement(riskProcessPointsql);
				PreparedStatement riskControlMeasurePst = connection.prepareStatement(riskControlMeasureSql);
				PreparedStatement processControlMeasurePst = connection.prepareStatement(processControlMeasureSql);
				PreparedStatement processPointControlMeasurePst = connection.prepareStatement(processPointControlMeasureSql);
				
				for (TempRiskProcessPointMeasureRelation tempRiskProcessPointMeasureRelation : tempRiskProcessPointMeasureRelationList) {
					/*
					 * 风险--流程关系信息
					 */
					boolean riskRelaProcessFlag = true;
					if(StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getProcessId()) || StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getRiskId())){
						riskRelaProcessFlag = false;
					}else{
						for (ProcessRelaRisk processRelaRisk : riskRelaProcessList) {
							if(processRelaRisk.getProcess().getId().equals(tempRiskProcessPointMeasureRelation.getProcessId()) && processRelaRisk.getRisk().getId().equals(tempRiskProcessPointMeasureRelation.getRiskId())){
								riskRelaProcessFlag = false;
								break;
							}
						}
						for (ProcessRelaRisk tempProcessRelaRisk : tempRiskRelaProcessList) {
							if(tempProcessRelaRisk.getProcess().getId().equals(tempRiskProcessPointMeasureRelation.getProcessId()) && tempProcessRelaRisk.getRisk().getId().equals(tempRiskProcessPointMeasureRelation.getRiskId())){
								riskRelaProcessFlag = false;
								break;
							}
						}
					}
					if(riskRelaProcessFlag){
						ProcessRelaRisk tempProcessRelaRisk = new ProcessRelaRisk();
						Risk tempRisk = new Risk();
						tempRisk.setId(tempRiskProcessPointMeasureRelation.getRiskId());
						tempProcessRelaRisk.setRisk(tempRisk);
						Process tempProcess = new Process();
						tempProcess.setId(tempRiskProcessPointMeasureRelation.getProcessId());
						tempProcessRelaRisk.setProcess(tempProcess);
						tempRiskRelaProcessList.add(tempProcessRelaRisk);
						
						riskProcessPst.setString(1, Identities.uuid());
						riskProcessPst.setString(2, tempRiskProcessPointMeasureRelation.getProcessId());
						riskProcessPst.setString(3, tempRiskProcessPointMeasureRelation.getRiskId());
						riskProcessPst.setString(4, Contents.INFLUENCE_PROCESS);
						riskProcessPst.addBatch();
					}
					
					/*
					 * 风险--流程节点关系信息
					 */
					boolean riskRelaProcessPointFlag = true;
					if(StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getProcessPointId()) || StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getRiskId())){
						riskRelaProcessPointFlag = false;
					}else{
						for (ProcessPointRelaRisk processPointRelaRisk : riskRelaProcessPointList) {
							if(processPointRelaRisk.getProcessPoint().getId().equals(tempRiskProcessPointMeasureRelation.getProcessPointId()) && processPointRelaRisk.getRisk().getId().equals(tempRiskProcessPointMeasureRelation.getRiskId())){
								riskRelaProcessPointFlag = false;
								break;
							}
						}
						for (ProcessPointRelaRisk tempProcessPointRelaRisk : tempRiskRelaProcessPointList) {
							if(tempProcessPointRelaRisk.getProcessPoint().getId().equals(tempRiskProcessPointMeasureRelation.getProcessPointId()) && tempProcessPointRelaRisk.getRisk().getId().equals(tempRiskProcessPointMeasureRelation.getRiskId())){
								riskRelaProcessPointFlag = false;
								break;
							}
						}
					}
					if(riskRelaProcessPointFlag){
						ProcessPointRelaRisk tempProcessPointRelaRisk = new ProcessPointRelaRisk();
						Risk tempRisk = new Risk();
						tempRisk.setId(tempRiskProcessPointMeasureRelation.getRiskId());
						tempProcessPointRelaRisk.setRisk(tempRisk);
						ProcessPoint tempProcessPoint = new ProcessPoint();
						tempProcessPoint.setId(tempRiskProcessPointMeasureRelation.getProcessPointId());
						tempProcessPointRelaRisk.setProcessPoint(tempProcessPoint);
						tempRiskRelaProcessPointList.add(tempProcessPointRelaRisk);
						
						riskProcessPointPst.setString(1, Identities.uuid());
						riskProcessPointPst.setString(2, tempRiskProcessPointMeasureRelation.getProcessId());
						riskProcessPointPst.setString(3, tempRiskProcessPointMeasureRelation.getProcessPointId());
						riskProcessPointPst.setString(4, tempRiskProcessPointMeasureRelation.getRiskId());
						riskProcessPointPst.addBatch();
					}
					
					/*
					 * 风险--控制措施信息
					 */
					boolean riskRelaMeasureFlag = true;
					if(StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getControlMeasureId()) || StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getRiskId())){
						riskRelaMeasureFlag = false;
					}else{
						for (MeasureRelaRisk measureRelaRisk : riskRelaMeasureList) {
							if(measureRelaRisk.getControlMeasure().getId().equals(tempRiskProcessPointMeasureRelation.getControlMeasureId()) && measureRelaRisk.getRisk().getId().equals(tempRiskProcessPointMeasureRelation.getRiskId())){
								riskRelaMeasureFlag = false;
								break;
							}
						}
						for (MeasureRelaRisk tempMeasureRelaRisk : tempRiskRelaMeasureList) {
							if(tempMeasureRelaRisk.getControlMeasure().getId().equals(tempRiskProcessPointMeasureRelation.getControlMeasureId()) && tempMeasureRelaRisk.getRisk().getId().equals(tempRiskProcessPointMeasureRelation.getRiskId())){
								riskRelaMeasureFlag = false;
								break;
							}
						}
					}
					if(riskRelaMeasureFlag){
						MeasureRelaRisk tempMeasureRelaRisk = new MeasureRelaRisk();
						Risk tempRisk = new Risk();
						tempRisk.setId(tempRiskProcessPointMeasureRelation.getRiskId());
						tempMeasureRelaRisk.setRisk(tempRisk);
						Measure tempControlMeasure = new Measure();
						tempControlMeasure.setId(tempRiskProcessPointMeasureRelation.getControlMeasureId());
						tempMeasureRelaRisk.setControlMeasure(tempControlMeasure);
						tempRiskRelaMeasureList.add(tempMeasureRelaRisk);
						
						riskControlMeasurePst.setString(1, Identities.uuid());
						riskControlMeasurePst.setString(2, tempRiskProcessPointMeasureRelation.getControlMeasureId());
						riskControlMeasurePst.setString(3, tempRiskProcessPointMeasureRelation.getRiskId());
						riskControlMeasurePst.addBatch();
					}
					
					/*
					 * 流程--控制措施关系信息
					 */
					boolean processRelaMeasureFlag = true;
					if(StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getControlMeasureId()) || StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getProcessId())){
						processRelaMeasureFlag = false;
					}else{
						for (ProcessRelaMeasure processRelaMeasure : processRelaMeasureList) {
							if(processRelaMeasure.getControlMeasure().getId().equals(tempRiskProcessPointMeasureRelation.getControlMeasureId()) && processRelaMeasure.getProcess().getId().equals(tempRiskProcessPointMeasureRelation.getProcessId())){
								processRelaMeasureFlag = false;
								break;
							}
						}
						for (ProcessRelaMeasure tempProcessRelaMeasure : tempProcessRelaMeasureList) {
							if(tempProcessRelaMeasure.getControlMeasure().getId().equals(tempRiskProcessPointMeasureRelation.getControlMeasureId()) && tempProcessRelaMeasure.getProcess().getId().equals(tempRiskProcessPointMeasureRelation.getProcessId())){
								processRelaMeasureFlag = false;
								break;
							}
						}
					}
					if(processRelaMeasureFlag){
						ProcessRelaMeasure tempProcessRelaMeasure = new ProcessRelaMeasure();
						Process tempProcess = new Process();
						tempProcess.setId(tempRiskProcessPointMeasureRelation.getProcessId());
						tempProcessRelaMeasure.setProcess(tempProcess);
						Measure tempControlMeasure = new Measure();
						tempControlMeasure.setId(tempRiskProcessPointMeasureRelation.getControlMeasureId());
						tempProcessRelaMeasure.setControlMeasure(tempControlMeasure);
						tempProcessRelaMeasureList.add(tempProcessRelaMeasure);
						
						processControlMeasurePst.setString(1, Identities.uuid());
						processControlMeasurePst.setString(2, tempRiskProcessPointMeasureRelation.getProcessId());
						processControlMeasurePst.setString(3, tempRiskProcessPointMeasureRelation.getControlMeasureId());
						processControlMeasurePst.addBatch();
					}
					/*
					 * 流程节点--控制措施关系信息
					 */
					boolean processPointRelaMeasureFlag = true;
					if(StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getControlMeasureId()) || StringUtils.isBlank(tempRiskProcessPointMeasureRelation.getProcessPointId())){
						processPointRelaMeasureFlag = false;
					}else{
						for (ProcessPointRelaMeasure processPointRelaMeasure : processPointRelaMeasureList) {
							if(processPointRelaMeasure.getControlMeasure().getId().equals(tempRiskProcessPointMeasureRelation.getControlMeasureId()) && processPointRelaMeasure.getProcessPoint().getId().equals(tempRiskProcessPointMeasureRelation.getProcessPointId())){
								processPointRelaMeasureFlag = false;
								break;
							}
						}
						for (ProcessPointRelaMeasure tempProcessPointRelaMeasure : tempProcessPointRelaMeasureList) {
							if(tempProcessPointRelaMeasure.getControlMeasure().getId().equals(tempRiskProcessPointMeasureRelation.getControlMeasureId()) && tempProcessPointRelaMeasure.getProcessPoint().getId().equals(tempRiskProcessPointMeasureRelation.getProcessPointId())){
								processPointRelaMeasureFlag = false;
								break;
							}
						}
					}
					if(processPointRelaMeasureFlag){
						ProcessPointRelaMeasure tempProcessPointRelaMeasure = new ProcessPointRelaMeasure();
						ProcessPoint tempProcessPoint = new ProcessPoint();
						tempProcessPoint.setId(tempRiskProcessPointMeasureRelation.getProcessPointId());
						tempProcessPointRelaMeasure.setProcessPoint(tempProcessPoint);
						Measure tempControlMeasure = new Measure();
						tempControlMeasure.setId(tempRiskProcessPointMeasureRelation.getControlMeasureId());
						tempProcessPointRelaMeasure.setControlMeasure(tempControlMeasure);
						tempProcessPointRelaMeasureList.add(tempProcessPointRelaMeasure);
						
						processPointControlMeasurePst.setString(1, Identities.uuid());
						processPointControlMeasurePst.setString(2, tempRiskProcessPointMeasureRelation.getProcessId());
						processPointControlMeasurePst.setString(3, tempRiskProcessPointMeasureRelation.getControlMeasureId());
						processPointControlMeasurePst.setString(4, tempRiskProcessPointMeasureRelation.getProcessPointId());
						processPointControlMeasurePst.addBatch();
					}
				}
				
				riskProcessPst.executeBatch();
				riskProcessPointPst.executeBatch();
				riskControlMeasurePst.executeBatch();
				processControlMeasurePst.executeBatch();
				processPointControlMeasurePst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
}