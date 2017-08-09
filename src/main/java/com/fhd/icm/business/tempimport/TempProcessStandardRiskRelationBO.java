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
import com.fhd.dao.icm.tempimport.TempProcessStandardRiskRelationDAO;
import com.fhd.entity.icm.standard.Standard;
import com.fhd.entity.icm.standard.StandardRelaProcessure;
import com.fhd.entity.icm.standard.StandardRelaRisk;
import com.fhd.entity.icm.tempimport.TempControlStandard;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.icm.tempimport.TempProcessStandardRiskRelation;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.standard.StandardBO;
import com.fhd.icm.business.standard.StandardRelaProcessureBO;
import com.fhd.icm.business.standard.StandardRelaRiskBO;
import com.fhd.ra.business.risk.ProcessRelaRiskBO;
import com.fhd.ra.business.risk.RiskBO;

/**
 * 流程--控制标准(要求)--风险关系导入临时表BO.
 * @author 吴德福
 * @Date 2013-12-12 10:40:32
 */
@Service
@SuppressWarnings("unchecked")
public class TempProcessStandardRiskRelationBO {

	@Autowired
	private TempProcessStandardRiskRelationDAO o_tempProcessStandardRiskRelationDAO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private TempProcessBO o_tempProcessBO;
	@Autowired
	private StandardBO o_controlStandardBO;
	@Autowired
	private TempControlStandardBO o_tempControlStandardBO;
	@Autowired
	private RiskBO o_riskBO;
	@Autowired
	private ProcessRelaRiskBO o_processRelaRiskBO;
	@Autowired
	private StandardRelaRiskBO o_standardRelaRiskBO;
	@Autowired
	private StandardRelaProcessureBO o_standardRelaProcessureBO;
	
	/**
	 * 验证流程--控制标准(要求)--风险关系.
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateProcessStandardRiskRelationData(String fileId){
		Map<String,Object> processPointRelationMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempProcessStandardRiskRelation> tempProcessStandardRiskRelationList = this.findProcessStandardRiskRelationPreviewListBySome("", fileId);

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
		//真实表中的流程list
		List<Standard> controlStandardList = o_controlStandardBO.findControlStandardListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> controlStandardCodeList = new ArrayList<String>();
		for (Standard controlStandard : controlStandardList) {
			if(!controlStandardCodeList.contains(controlStandard.getCode())){
				controlStandardCodeList.add(controlStandard.getCode());
			}
		}
		//临时表中流程list
		List<TempControlStandard> tempControlStandardList = o_tempControlStandardBO.findControlStandardPreviewListBySome("", fileId);
		for (TempControlStandard tempControlStandard : tempControlStandardList) {
			if(!controlStandardCodeList.contains(tempControlStandard.getControlStandardCode())){
				controlStandardCodeList.add(tempControlStandard.getControlStandardCode());
			}
		}
		//真实表中的风险list
		List<Risk> riskList = o_riskBO.findRiskListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> riskCodeList = new ArrayList<String>();
		for (Risk risk : riskList) {
			if(!riskCodeList.contains(risk.getCode())){
				riskCodeList.add(risk.getCode());
			}
		}
		
		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempProcessStandardRiskRelation tempProcessStandardRiskRelation : tempProcessStandardRiskRelationList) {
			StringBuffer errorTip=new StringBuffer();
			
			if(StringUtils.isNotBlank(tempProcessStandardRiskRelation.getProcessCode()) && !processCodeList.contains(tempProcessStandardRiskRelation.getProcessCode())){
				//验证流程编号是否正确
				errorTip.append("(").append("A").append(i).append(")").append("流程编号不存在!");
			}
			if(StringUtils.isNotBlank(tempProcessStandardRiskRelation.getControlStandardCode()) && !controlStandardCodeList.contains(tempProcessStandardRiskRelation.getControlStandardCode())){
				//验证控制标准(要求)编号是否正确
				errorTip.append("(").append("C").append(i).append(")").append("控制标准(要求)编号不存在!");
			}
			if(StringUtils.isNotBlank(tempProcessStandardRiskRelation.getRiskCode()) && !riskCodeList.contains(tempProcessStandardRiskRelation.getRiskCode())){
				//验证风险编号是否正确
				errorTip.append("(").append("E").append(i).append(")").append("风险编号不存在!");
			}
			
			if(errorTip.length()>0){
				errorCount++;
				tempProcessStandardRiskRelation.setErrorTip(errorTip.toString());
				o_tempProcessStandardRiskRelationDAO.merge(tempProcessStandardRiskRelation);
			}
			i++;
		}
		
		processPointRelationMap.put("allCount", tempProcessStandardRiskRelationList.size());
		processPointRelationMap.put("correctCount", tempProcessStandardRiskRelationList.size()-errorCount);
		processPointRelationMap.put("errorCount", errorCount);
		
		return processPointRelationMap;
	}
	/**
	 * excel表数据存入流程--控制标准(要求)--风险关系临时表.
	 * @author 吴德福
	 * @param dataListList
	 * @param fileUploadEntity
	 */
	@Transactional
	public void saveTempProcessStandardRiskRelation(List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		List<TempProcessStandardRiskRelation> tempProcessStandardRiskRelationList = new ArrayList<TempProcessStandardRiskRelation>();
		//真实表中的流程list
		List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		//临时表中流程list
		List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		//真实表中的流程list
		List<Standard> controlStandardList = o_controlStandardBO.findControlStandardListByCompanyId(UserContext.getUser().getCompanyid());
		//临时表中流程list
		List<TempControlStandard> tempControlStandardList = o_tempControlStandardBO.findControlStandardPreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		//真实表中的风险list
		List<Risk> riskList = o_riskBO.findRiskListByCompanyId(UserContext.getUser().getCompanyid());
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempProcessStandardRiskRelation tempProcessStandardRiskRelation = new TempProcessStandardRiskRelation();
			tempProcessStandardRiskRelation.setId(Identities.uuid());
			tempProcessStandardRiskRelation.setIndex(String.valueOf(i+1+START_ROW));
			tempProcessStandardRiskRelation.setProcessCode(dataList.get(j).get(0));
			tempProcessStandardRiskRelation.setProcessName(dataList.get(j).get(1));
			tempProcessStandardRiskRelation.setControlStandardCode(dataList.get(j).get(2));
			tempProcessStandardRiskRelation.setControlStandardName(dataList.get(j).get(3));
			tempProcessStandardRiskRelation.setRiskCode(dataList.get(j).get(4));
			tempProcessStandardRiskRelation.setRiskName(dataList.get(j).get(5));
			tempProcessStandardRiskRelation.setFileUploadEntity(fileUploadEntity);
			
			String processId = "";
			for(Process process : processList){
				if(process.getCode().equals(dataList.get(j).get(0))){
					//真实表中存在
					processId = process.getId();
					break;
				}
			}
			if("".equals(processId)){
				//真实表中不存在
				for(TempProcess tempProcess : tempProcessList){
					if(tempProcess.getProcessureCode().equals(dataList.get(j).get(0))){
						//临时表中存在
						processId = tempProcess.getId();
						break;
					}
				}
			}
			if(!"".equals(processId)){
				tempProcessStandardRiskRelation.setProcessId(processId);
			}
			
			String controlStandardId = "";
			for(Standard controlStandard : controlStandardList){
				if(controlStandard.getCode().equals(dataList.get(j).get(2))){
					//真实表中存在
					controlStandardId = controlStandard.getId();
					break;
				}
			}
			if("".equals(controlStandardId)){
				//真实表中不存在
				for(TempControlStandard tempControlStandard : tempControlStandardList){
					if(tempControlStandard.getControlStandardCode().equals(dataList.get(j).get(2))){
						//临时表中存在
						controlStandardId = tempControlStandard.getId();
						break;
					}
				}
			}
			if(!"".equals(controlStandardId)){
				tempProcessStandardRiskRelation.setControlStandardId(controlStandardId);
			}
			
			String riskId = "";
			for(Risk risk : riskList){
				if(dataList.get(j).get(4).equals(risk.getCode())){	//risk.getCode有可能是空
					//真实表中存在
					riskId = risk.getId();
					break;
				}
			}
			if(!"".equals(riskId)){
				tempProcessStandardRiskRelation.setRiskId(riskId);
			}
			
			//o_tempProcessStandardRiskRelationDAO.merge(tempProcessStandardRiskRelation);
			tempProcessStandardRiskRelationList.add(tempProcessStandardRiskRelation);
			i++;
		}
		//批量导入临时表数据
		batchSaveTempData(tempProcessStandardRiskRelationList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempProcessStandardRiskRelation> tempProcessStandardRiskRelationList){
		o_tempProcessStandardRiskRelationDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_PROCESS_STANDARD_RISK_RELATION(id,E_INDEX,PROCESS_CODE,PROCESS_NAME," +
						"CONTROL_STANDARD_CODE,CONTROL_STANDARD_NAME,RISK_CODE,RISK_NAME,PROCESS_ID,CONTROL_STANDARD_ID,RISK_ID) " +
						"values(?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempProcessStandardRiskRelation o : tempProcessStandardRiskRelationList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getProcessCode());
					pst.setObject(4, o.getProcessName());
					pst.setObject(5, o.getControlStandardCode());
					pst.setObject(6, o.getControlStandardName());
					pst.setObject(7, o.getRiskCode());
					pst.setObject(8, o.getRiskName());
					pst.setObject(9, o.getProcessId());
					pst.setObject(10, o.getControlStandardId());
					pst.setObject(11, o.getRiskId());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempProcessStandardRiskRelationDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的流程--控制标准(要求)--风险关系临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempProcessStandardRiskRelation>
	 */
	public List<TempProcessStandardRiskRelation> findProcessStandardRiskRelationPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempProcessStandardRiskRelationDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.like("processCode", query, MatchMode.ANYWHERE), Restrictions.like("controlStandardCode", query, MatchMode.ANYWHERE)), Restrictions.or(Restrictions.like("riskCode", query, MatchMode.ANYWHERE), Restrictions.like("controlStandardName", query, MatchMode.ANYWHERE))));
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		criteria.addOrder(Order.desc("errorTip"));
		criteria.addOrder(Order.asc("index"));
		return criteria.list();
	}
	/**
	 * 批量删除流程--控制标准(要求)--风险关系临时表.
	 * @author 吴德福
	 */
	@Transactional
	public void removeBatchTempProcessStandardRiskRelationByHql(){
		String hql="delete from TempProcessStandardRiskRelation";
		o_tempProcessStandardRiskRelationDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempProcessStandardRiskRelationDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempProcessStandardRiskRelation> tempProcessStandardRiskRelationList = (List<TempProcessStandardRiskRelation>) map.get("tempProcessStandardRiskRelationList");

				//流程--控制标准关系sql--3个字段
				String processStandardSql = "insert into t_ic_standard_rela_processure(id,control_standard_id,processure_id) values(?,?,?)";
				//控制标准(要求)--风险关系sql--3个字段
				String standardRiskSql = "insert into t_ic_standard_rela_risk(id,standard_id,risk_id) values(?,?,?)";
				//流程--风险关系sql--4个字段
				String processRiskSql = "insert into t_processure_risk_processure(id,processure_id,risk_id,etype) values(?,?,?,?)";
				
				//验证风险相关流程关系是否存在
				List<ProcessRelaRisk> riskRelaProcessList = o_processRelaRiskBO.findRiskRelaProcessListByCompanyId(UserContext.getUser().getCompanyid());
				//验证风险相关控制标准(要求)关系是否存在
				List<StandardRelaRisk> riskRleaControlStandardList = o_standardRelaRiskBO.findStandardRelaRiskByCompanyId(UserContext.getUser().getCompanyid());
				//验证流程相关控制标准(要求)关系是否存在
				List<StandardRelaProcessure> processRelaControlStandardList = o_standardRelaProcessureBO.findStandardRelaRiskByCompanyId(UserContext.getUser().getCompanyid());
				
				//验证风险相关流程关系是否存在
				List<ProcessRelaRisk> tempRiskRelaProcessList = new ArrayList<ProcessRelaRisk>();
				//验证风险相关控制标准(要求)关系是否存在
				List<StandardRelaRisk> tempRiskRleaControlStandardList = new ArrayList<StandardRelaRisk>();
				//验证流程相关控制标准(要求)关系是否存在
				List<StandardRelaProcessure> tempProcessRelaControlStandardList = new ArrayList<StandardRelaProcessure>();
				
				PreparedStatement processStandardPst = connection.prepareStatement(processStandardSql);
				PreparedStatement standardRiskPst = connection.prepareStatement(standardRiskSql);
				PreparedStatement processRiskPst = connection.prepareStatement(processRiskSql);
				
				for (TempProcessStandardRiskRelation tempProcessStandardRiskRelation : tempProcessStandardRiskRelationList) {
					/*
					 * 流程--控制标准(要求)关系信息
					 */
					boolean processRelaControlStandardFlag = true;
					if(StringUtils.isBlank(tempProcessStandardRiskRelation.getProcessId()) || StringUtils.isBlank(tempProcessStandardRiskRelation.getControlStandardId())){
						processRelaControlStandardFlag = false;
					}else{
						for (StandardRelaProcessure standardRelaProcessure : processRelaControlStandardList) {
							if(standardRelaProcessure.getProcessure().getId().equals(tempProcessStandardRiskRelation.getProcessId()) && standardRelaProcessure.getStandard().getId().equals(tempProcessStandardRiskRelation.getControlStandardId())){
								processRelaControlStandardFlag = false;
								break;
							}
						}
						for (StandardRelaProcessure tempStandardRelaProcessure : tempProcessRelaControlStandardList) {
							if(tempStandardRelaProcessure.getProcessure().getId().equals(tempProcessStandardRiskRelation.getProcessId()) && tempStandardRelaProcessure.getStandard().getId().equals(tempProcessStandardRiskRelation.getControlStandardId())){
								processRelaControlStandardFlag = false;
								break;
							}
						}
					}
					if(processRelaControlStandardFlag){
						StandardRelaProcessure tempStandardRelaProcessure = new StandardRelaProcessure();
						Process tempProcess = new Process();
						tempProcess.setId(tempProcessStandardRiskRelation.getProcessId());
						tempStandardRelaProcessure.setProcessure(tempProcess);
						Standard tempStandard = new Standard();
						tempStandard.setId(tempProcessStandardRiskRelation.getControlStandardId());
						tempStandardRelaProcessure.setStandard(tempStandard);
						tempProcessRelaControlStandardList.add(tempStandardRelaProcessure);
						
						processStandardPst.setString(1, Identities.uuid());
						processStandardPst.setString(2, tempProcessStandardRiskRelation.getControlStandardId());
						processStandardPst.setString(3, tempProcessStandardRiskRelation.getProcessId());
						processStandardPst.addBatch();
					}
					
					/*
					 * 控制标准(要求)--风险关系信息
					 */
					boolean riskRleaControlStandardFlag = true;
					if(StringUtils.isBlank(tempProcessStandardRiskRelation.getRiskId()) || StringUtils.isBlank(tempProcessStandardRiskRelation.getControlStandardId())){
						riskRleaControlStandardFlag = false;
					}else{
						for (StandardRelaRisk riskRleaControlStandard : riskRleaControlStandardList) {
							if(riskRleaControlStandard.getRisk().getId().equals(tempProcessStandardRiskRelation.getRiskId()) && riskRleaControlStandard.getStandard().getId().equals(tempProcessStandardRiskRelation.getControlStandardId())){
								riskRleaControlStandardFlag = false;
								break;
							}
						}
						for (StandardRelaRisk tempStandardRelaRisk : tempRiskRleaControlStandardList) {
							if(tempStandardRelaRisk.getRisk().getId().equals(tempProcessStandardRiskRelation.getRiskId()) && tempStandardRelaRisk.getStandard().getId().equals(tempProcessStandardRiskRelation.getControlStandardId())){
								riskRleaControlStandardFlag = false;
								break;
							}
						}
					}
					if(riskRleaControlStandardFlag){
						StandardRelaRisk tempStandardRelaRisk = new StandardRelaRisk();
						Risk tempRisk = new Risk();
						tempRisk.setId(tempProcessStandardRiskRelation.getRiskId());
						tempStandardRelaRisk.setRisk(tempRisk);
						Standard tempStandard = new Standard();
						tempStandard.setId(tempProcessStandardRiskRelation.getControlStandardId());
						tempStandardRelaRisk.setStandard(tempStandard);
						tempRiskRleaControlStandardList.add(tempStandardRelaRisk);
						
						standardRiskPst.setString(1, Identities.uuid());
						standardRiskPst.setString(2, tempProcessStandardRiskRelation.getControlStandardId());
						standardRiskPst.setString(3, tempProcessStandardRiskRelation.getRiskId());
						standardRiskPst.addBatch();
					}
					/*
					 * 流程--风险关系信息
					 */
					boolean riskRelaProcessFlag = true;
					if(StringUtils.isBlank(tempProcessStandardRiskRelation.getProcessId()) || StringUtils.isBlank(tempProcessStandardRiskRelation.getRiskId())){
						riskRelaProcessFlag = false;
					}else{
						for (ProcessRelaRisk processRelaRisk : riskRelaProcessList) {
							if(processRelaRisk.getProcess().getId().equals(tempProcessStandardRiskRelation.getProcessId()) && processRelaRisk.getRisk().getId().equals(tempProcessStandardRiskRelation.getRiskId())){
								riskRelaProcessFlag = false;
								break;
							}
						}
						for (ProcessRelaRisk tempProcessRelaRisk : riskRelaProcessList) {
							if(tempProcessRelaRisk.getProcess().getId().equals(tempProcessStandardRiskRelation.getProcessId()) && tempProcessRelaRisk.getRisk().getId().equals(tempProcessStandardRiskRelation.getRiskId())){
								riskRelaProcessFlag = false;
								break;
							}
						}
					}
					if(riskRelaProcessFlag){
						ProcessRelaRisk tempProcessRelaRisk = new ProcessRelaRisk();
						Risk tempRisk = new Risk();
						tempRisk.setId(tempProcessStandardRiskRelation.getRiskId());
						tempProcessRelaRisk.setRisk(tempRisk);
						Process tempProcess = new Process();
						tempProcess.setId(tempProcessStandardRiskRelation.getProcessId());
						tempProcessRelaRisk.setProcess(tempProcess);
						tempRiskRelaProcessList.add(tempProcessRelaRisk);
						
						processRiskPst.setString(1, Identities.uuid());
						processRiskPst.setString(2, tempProcessStandardRiskRelation.getProcessId());
						processRiskPst.setString(3, tempProcessStandardRiskRelation.getRiskId());
						processRiskPst.setString(4, Contents.INFLUENCE_PROCESS);
						processRiskPst.addBatch();
					}
				}
				processStandardPst.executeBatch();
				standardRiskPst.executeBatch();
				processRiskPst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
}