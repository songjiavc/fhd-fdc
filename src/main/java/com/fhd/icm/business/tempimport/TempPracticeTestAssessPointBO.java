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
import com.fhd.dao.icm.tempimport.TempPracticeTestAssessPointDAO;
import com.fhd.entity.icm.assess.AssessPoint;
import com.fhd.entity.icm.tempimport.TempPracticeTestAssessPoint;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.icm.tempimport.TempProcessPoint;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.AssessPointBO;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.process.ProcessPointBO;
import com.fhd.sys.business.dic.DictBO;

/**
 * 穿行测试导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-13 16:44:32
 */
@Service
@SuppressWarnings("unchecked")
public class TempPracticeTestAssessPointBO {

	@Autowired
	private TempPracticeTestAssessPointDAO o_tempPracticeTestAssessPointDAO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private TempProcessBO o_tempProcessBO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	@Autowired
	private TempProcessPointBO o_tempProcessPointBO;
	@Autowired
	private AssessPointBO o_assessPointBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * 验证流程节点信息.
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validatePracticeTestAssessPointData(String fileId){
		Map<String,Object> processPointMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempPracticeTestAssessPoint> tempPracticeTestAssessPointList = this.findPracticeTestAssessPointPreviewListBySome("", fileId);
		
		//财报科目数据字典项list
		List<DictEntry> affectSubjectsList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_rela_subject");
		List<String> affectSubjectsNameList = new ArrayList<String>();
		for (DictEntry dictEntry : affectSubjectsList) {
			if(!affectSubjectsNameList.contains(dictEntry.getName())){
				affectSubjectsNameList.add(dictEntry.getName());
			}
		}
		
		//流程list
		List<String> processCodeList = new ArrayList<String>();
		List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		for (Process process : processList) {
			if(!processCodeList.contains(process.getCode())){
				processCodeList.add(process.getCode());
			}
		}
		List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", fileId);
		for (TempProcess tempProcess : tempProcessList) {
			if(!processCodeList.contains(tempProcess.getProcessureCode())){
				processCodeList.add(tempProcess.getProcessureCode());
			}
		}
		//流程节点list
		List<ProcessPoint> processPointList = o_processPointBO.findProcessPointByCompanyId(UserContext.getUser().getCompanyid());
		List<String> processPointCodeList = new ArrayList<String>();
		for (ProcessPoint processPoint : processPointList) {
			if(!processPointCodeList.contains(processPoint.getCode())){
				processPointCodeList.add(processPoint.getCode());
			}
		}
		List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", fileId);
		for (TempProcessPoint tempProcessPoint : tempProcessPointList) {
			if(!processPointCodeList.contains(tempProcessPoint.getProcessPointCode())){
				processPointCodeList.add(tempProcessPoint.getProcessPointCode());
			}
		}
		//评价点list
		List<AssessPoint> assessPointList = o_assessPointBO.findAssessPointListByCompanyIdAndType(UserContext.getUser().getCompanyid(),Contents.ASSESS_POINT_TYPE_DESIGN);
		List<String> assessPointCodeList = new ArrayList<String>();
		for (AssessPoint assessPoint : assessPointList) {
			if(!assessPointCodeList.contains(assessPoint.getCode())){
				assessPointCodeList.add(assessPoint.getCode());
			}
		}
		
		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempPracticeTestAssessPoint tempPracticeTestAssessPoint : tempPracticeTestAssessPointList) {
			StringBuffer errorTip=new StringBuffer();
			
			if(StringUtils.isBlank(tempPracticeTestAssessPoint.getProcessCode())){
				errorTip.append("(").append("A").append(i).append(")").append("流程编号必填!");
			}else{
				//验证流程编号是否正确
				if(!processCodeList.contains(tempPracticeTestAssessPoint.getProcessCode())){
					errorTip.append("(").append("A").append(i).append(")").append("流程编号不存在!");
				}
			}
			if(StringUtils.isBlank(tempPracticeTestAssessPoint.getProcessPointCode())){
				errorTip.append("(").append("C").append(i).append(")").append("流程节点编号必填!");
			}else{
				//验证流程节点编号是否正确
				if(!processPointCodeList.contains(tempPracticeTestAssessPoint.getProcessPointCode())){
					errorTip.append("(").append("C").append(i).append(")").append("流程节点编号不存在!");
				}
			}
			if(StringUtils.isBlank(tempPracticeTestAssessPoint.getAssessPointCode())){
				errorTip.append("(").append("E").append(i).append(")").append("评价点编号必填!");
			}else{
				//验证评价点编号是否正确
				if(assessPointCodeList.contains(tempPracticeTestAssessPoint.getAssessPointCode())){
					errorTip.append("(").append("E").append(i).append(")").append("评价点编号已存在!");
				}
			}
			if(StringUtils.isBlank(tempPracticeTestAssessPoint.getAssessPointName())){
				errorTip.append("(").append("F").append(i).append(")").append("评价点编号必填!");
			}
			if(StringUtils.isNotBlank(tempPracticeTestAssessPoint.getAffectSubjects())){
				//验证影响的财报科目是否正确
				String[] affectSubjectsNameArray = tempPracticeTestAssessPoint.getAffectSubjects().split(",");
				for (String affectSubjectsName : affectSubjectsNameArray) {
					if(!affectSubjectsNameList.contains(affectSubjectsName)){
						errorTip.append("(").append("G").append(i).append(")").append("穿行测试评价点影响的财报科目'").append(affectSubjectsName).append("'不存在!");
					}
				}
			}
			if(StringUtils.isBlank(tempPracticeTestAssessPoint.getImplementProof())){
				errorTip.append("(").append("H").append(i).append(")").append("实施证据必填!");
			}
			if(errorTip.length()>0){
				errorCount++;
				tempPracticeTestAssessPoint.setErrorTip(errorTip.toString());
				o_tempPracticeTestAssessPointDAO.merge(tempPracticeTestAssessPoint);
			}
			i++;
		}
		
		processPointMap.put("allCount", tempPracticeTestAssessPointList.size());
		processPointMap.put("correctCount", tempPracticeTestAssessPointList.size()-errorCount);
		processPointMap.put("errorCount", errorCount);
		
		return processPointMap;
	}
	
	/**
	 * excel表数据存入流程节点临时表.
	 * @author 吴德福
	 * @param dataListList
	 * @param fileUploadEntity
	 */
	@Transactional
	public void saveTempPracticeTestAssessPoint(List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		List<TempPracticeTestAssessPoint> tempPracticeTestAssessPointList = new ArrayList<TempPracticeTestAssessPoint>();
		//流程list
		List<String> processCodeList = new ArrayList<String>();
		List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		for (Process process : processList) {
			if(!processCodeList.contains(process.getCode())){
				processCodeList.add(process.getCode());
			}
		}
		List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		for (TempProcess tempProcess : tempProcessList) {
			if(!processCodeList.contains(tempProcess.getProcessureCode())){
				processCodeList.add(tempProcess.getProcessureCode());
			}
		}
		//流程节点list
		List<ProcessPoint> processPointList = o_processPointBO.findProcessPointByCompanyId(UserContext.getUser().getCompanyid());
		List<String> processPointCodeList = new ArrayList<String>();
		for (ProcessPoint processPoint : processPointList) {
			if(!processPointCodeList.contains(processPoint.getCode())){
				processPointCodeList.add(processPoint.getCode());
			}
		}
		List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		for (TempProcessPoint tempProcessPoint : tempProcessPointList) {
			if(!processPointCodeList.contains(tempProcessPoint.getProcessPointCode())){
				processPointCodeList.add(tempProcessPoint.getProcessPointCode());
			}
		}
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempPracticeTestAssessPoint tempPracticeTestAssessPoint = new TempPracticeTestAssessPoint();
			tempPracticeTestAssessPoint.setId(Identities.uuid());
			tempPracticeTestAssessPoint.setIndex(String.valueOf(i+1+START_ROW));
			tempPracticeTestAssessPoint.setProcessCode(dataList.get(j).get(0));
			tempPracticeTestAssessPoint.setProcessName(dataList.get(j).get(1));
			tempPracticeTestAssessPoint.setProcessPointCode(dataList.get(j).get(2));
			tempPracticeTestAssessPoint.setProcessPointName(dataList.get(j).get(3));
			tempPracticeTestAssessPoint.setAssessPointCode(dataList.get(j).get(4));
			tempPracticeTestAssessPoint.setAssessPointName(dataList.get(j).get(5));
			tempPracticeTestAssessPoint.setAffectSubjects(dataList.get(j).get(6));
			tempPracticeTestAssessPoint.setImplementProof(dataList.get(j).get(7));
			tempPracticeTestAssessPoint.setFileUploadEntity(fileUploadEntity);
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
			tempPracticeTestAssessPoint.setProcessId(processId);
			
			String processPointId = "";
			for(ProcessPoint processPoint : processPointList){
				if(processPoint.getCode().equals(dataList.get(j).get(2))){
					//真实表中存在
					processPointId = processPoint.getId();
					break;
				}
			}
			if("".equals(processPointId)){
				//真实表中不存在
				for(TempProcessPoint tempProcessPoint : tempProcessPointList){
					if(tempProcessPoint.getProcessPointCode().equals(dataList.get(j).get(2))){
						//临时表中存在
						processPointId = tempProcessPoint.getId();
						break;
					}
				}
			}
			tempPracticeTestAssessPoint.setProcessPointId(processPointId);

			//o_tempPracticeTestAssessPointDAO.merge(tempPracticeTestAssessPoint);
			tempPracticeTestAssessPointList.add(tempPracticeTestAssessPoint);
			i++;
		}
		//批量导入临时表数据
		batchSaveTempData(tempPracticeTestAssessPointList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempPracticeTestAssessPoint> tempPracticeTestAssessPointList){
		o_tempPracticeTestAssessPointDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_PRACTICE_TEST_ASSESS_POINT(id,E_INDEX,PROCESS_CODE,PROCESS_NAME," +
						"PROCESS_POINT_CODE,PROCESS_POINT_NAME,ASSESS_POINT_CODE,ASSESS_POINT_NAME,AFFECT_SUBJECTS," +
						"IMPLEMENT_PROOF,PROCESS_ID,PROCESS_POINT_ID) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempPracticeTestAssessPoint o : tempPracticeTestAssessPointList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getProcessCode());
					pst.setObject(4, o.getProcessName());
					pst.setObject(5, o.getProcessPointCode());
					pst.setObject(6, o.getProcessPointName());
					pst.setObject(7, o.getAssessPointCode());
					pst.setObject(8, o.getAssessPointName());
					pst.setObject(9, o.getAffectSubjects());
					pst.setObject(10, o.getImplementProof());
					pst.setObject(11, o.getProcessId());
					pst.setObject(12, o.getProcessPointId());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempPracticeTestAssessPointDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的流程节点临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempPracticeTestAssessPoint>
	 */
	public List<TempPracticeTestAssessPoint> findPracticeTestAssessPointPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempPracticeTestAssessPointDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.like("assessPointCode", query, MatchMode.ANYWHERE), Restrictions.like("assessPointName", query, MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		criteria.addOrder(Order.desc("errorTip"));
		criteria.addOrder(Order.asc("index"));
		return criteria.list();
	}
	/**
	 * 批量删除流程节点临时表.
	 * @author 吴德福
	 */
	@Transactional
	public void removeBatchTempPracticeTestAssessPointByHql(){
		String hql="delete from TempPracticeTestAssessPoint";
		o_tempPracticeTestAssessPointDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempPracticeTestAssessPointDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempPracticeTestAssessPoint> tempPracticeTestAssessPointList = (List<TempPracticeTestAssessPoint>) map.get("tempPracticeTestAssessPointList");
				/*
				 * 转换信息list
				 */
				//财报科目数据字典项list
				List<DictEntry> affectSubjectList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_rela_subject");
				
				//穿行测试评价点sql--8个字段
				String basicSql = "insert into t_ca_assessment_point(id,processure_id,control_point_id,assessment_point_code,edesc,rela_subject,ecomment,etype) values(?,?,?,?,?,?,?,?)";
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				
				for (TempPracticeTestAssessPoint tempPracticeTestAssessPoint : tempPracticeTestAssessPointList) {
					/*
					 * 穿行测试评价点信息
					 */
					basicPst.setString(1, tempPracticeTestAssessPoint.getId());
					basicPst.setString(2, tempPracticeTestAssessPoint.getProcessId());
					basicPst.setString(3, tempPracticeTestAssessPoint.getProcessPointId());
					basicPst.setString(4, tempPracticeTestAssessPoint.getAssessPointCode());
					basicPst.setString(5, tempPracticeTestAssessPoint.getAssessPointName());
					if(StringUtils.isNotBlank(tempPracticeTestAssessPoint.getAffectSubjects())){
						StringBuilder affectSubjectId = new StringBuilder();
						String[] affectSubjectsArray = tempPracticeTestAssessPoint.getAffectSubjects().split(",");
						//财报科目转换数据字典id
						for (DictEntry dictEntry : affectSubjectList) {
							for (String affectSubject : affectSubjectsArray) {
								if(affectSubject.equals(dictEntry.getName())){
									if(affectSubjectId.length()>0){
										affectSubjectId.append(",");
									}
									affectSubjectId.append(dictEntry.getId());
								}
							}
						}
						basicPst.setString(6, affectSubjectId.toString());
					}else{
						basicPst.setString(6, null);
					}
					basicPst.setString(7, tempPracticeTestAssessPoint.getImplementProof());
					basicPst.setString(8, Contents.ASSESS_POINT_TYPE_DESIGN);
					
					basicPst.addBatch();
					
				}
				basicPst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
}