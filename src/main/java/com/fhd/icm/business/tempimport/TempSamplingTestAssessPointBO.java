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
import com.fhd.dao.icm.tempimport.TempSamplingTestAssessPointDAO;
import com.fhd.entity.icm.assess.AssessPoint;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.icm.tempimport.TempControlMeasure;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.icm.tempimport.TempSamplingTestAssessPoint;
import com.fhd.entity.process.Process;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.assess.MeasureBO;
import com.fhd.icm.business.process.AssessPointBO;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.sys.business.dic.DictBO;

/**
 * 抽样测试导入临时表实体.
 * @author 吴德福
 * @Date 2013-12-13 16:44:32
 */
@Service
@SuppressWarnings("unchecked")
public class TempSamplingTestAssessPointBO {

	@Autowired
	private TempSamplingTestAssessPointDAO o_tempSamplingTestAssessPointDAO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private TempProcessBO o_tempProcessBO;
	@Autowired
	private MeasureBO o_controlMeasureBO;
	@Autowired
	private TempControlMeasureBO o_tempControlMeasureBO;
	@Autowired
	private AssessPointBO o_assessPointBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * 验证流程节点信息.
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateSamplingTestAssessPointData(String fileId){
		Map<String,Object> processPointMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempSamplingTestAssessPoint> tempSamplingTestAssessPointList = this.findSamplingTestAssessPointPreviewListBySome("", fileId);
		
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
		//控制措施list
		List<Measure> controlMeasureList = o_controlMeasureBO.findMeasureListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> controlMeasureCodeList = new ArrayList<String>();
		for (Measure controlMeasure : controlMeasureList) {
			if(!controlMeasureCodeList.contains(controlMeasure.getCode())){
				controlMeasureCodeList.add(controlMeasure.getCode());
			}
		}
		List<TempControlMeasure> tempControlMeasureList = o_tempControlMeasureBO.findControlMeasurePreviewListBySome("", fileId);
		for (TempControlMeasure tempControlMeasure : tempControlMeasureList) {
			if(!controlMeasureCodeList.contains(tempControlMeasure.getControlMeasureCode())){
				controlMeasureCodeList.add(tempControlMeasure.getControlMeasureCode());
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
		for (TempSamplingTestAssessPoint tempSamplingTestAssessPoint : tempSamplingTestAssessPointList) {
			StringBuffer errorTip=new StringBuffer();
			
			if(StringUtils.isBlank(tempSamplingTestAssessPoint.getProcessCode())){
				errorTip.append("(").append("A").append(i).append(")").append("流程编号必填!");
			}else{
				//验证流程编号是否正确
				if(!processCodeList.contains(tempSamplingTestAssessPoint.getProcessCode())){
					errorTip.append("(").append("A").append(i).append(")").append("流程编号不存在!");
				}
			}
			if(StringUtils.isBlank(tempSamplingTestAssessPoint.getControlMeasureCode())){
				errorTip.append("(").append("C").append(i).append(")").append("控制措施编号必填!");
			}else{
				//验证控制措施编号是否正确
				if(!controlMeasureCodeList.contains(tempSamplingTestAssessPoint.getControlMeasureCode())){
					errorTip.append("(").append("C").append(i).append(")").append("控制措施编号不存在!");
				}
			}
			if(StringUtils.isBlank(tempSamplingTestAssessPoint.getAssessPointCode())){
				errorTip.append("(").append("E").append(i).append(")").append("评价点编号必填!");
			}else{
				//验证评价点编号是否正确
				if(assessPointCodeList.contains(tempSamplingTestAssessPoint.getAssessPointCode())){
					errorTip.append("(").append("E").append(i).append(")").append("评价点编号已存在!");
				}
			}
			if(StringUtils.isBlank(tempSamplingTestAssessPoint.getAssessPointName())){
				errorTip.append("(").append("F").append(i).append(")").append("评价点编号必填!");
			}
			if(StringUtils.isNotBlank(tempSamplingTestAssessPoint.getAffectSubjects())){
				//验证影响的财报科目是否正确
				String[] affectSubjectsNameArray = tempSamplingTestAssessPoint.getAffectSubjects().split(",");
				for (String affectSubjectsName : affectSubjectsNameArray) {
					if(!affectSubjectsNameList.contains(affectSubjectsName)){
						errorTip.append("(").append("G").append(i).append(")").append("穿行测试评价点影响的财报科目'").append(affectSubjectsName).append("'不存在!");
					}
				}
			}
			if(StringUtils.isBlank(tempSamplingTestAssessPoint.getImplementProof())){
				errorTip.append("(").append("H").append(i).append(")").append("实施证据必填!");
			}
			if(errorTip.length()>0){
				errorCount++;
				tempSamplingTestAssessPoint.setErrorTip(errorTip.toString());
				o_tempSamplingTestAssessPointDAO.merge(tempSamplingTestAssessPoint);
			}
			i++;
		}
		
		processPointMap.put("allCount", tempSamplingTestAssessPointList.size());
		processPointMap.put("correctCount", tempSamplingTestAssessPointList.size()-errorCount);
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
	public void saveTempSamplingTestAssessPoint(List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		List<TempSamplingTestAssessPoint> tempSamplingTestAssessPointList = new ArrayList<TempSamplingTestAssessPoint>();
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
		//控制措施list
		List<Measure> controlMeasureList = o_controlMeasureBO.findMeasureListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> controlMeasureCodeList = new ArrayList<String>();
		for (Measure controlMeasure : controlMeasureList) {
			if(!controlMeasureCodeList.contains(controlMeasure.getCode())){
				controlMeasureCodeList.add(controlMeasure.getCode());
			}
		}
		List<TempControlMeasure> tempControlMeasureList = o_tempControlMeasureBO.findControlMeasurePreviewListBySome("", fileUploadEntity==null?null:fileUploadEntity.getId());
		for (TempControlMeasure tempControlMeasure : tempControlMeasureList) {
			if(!controlMeasureCodeList.contains(tempControlMeasure.getControlMeasureCode())){
				controlMeasureCodeList.add(tempControlMeasure.getControlMeasureCode());
			}
		}
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempSamplingTestAssessPoint tempSamplingTestAssessPoint = new TempSamplingTestAssessPoint();
			tempSamplingTestAssessPoint.setId(Identities.uuid());
			tempSamplingTestAssessPoint.setIndex(String.valueOf(i+1+START_ROW));
			tempSamplingTestAssessPoint.setProcessCode(dataList.get(j).get(0));
			tempSamplingTestAssessPoint.setProcessName(dataList.get(j).get(1));
			tempSamplingTestAssessPoint.setControlMeasureCode(dataList.get(j).get(2));
			tempSamplingTestAssessPoint.setControlMeasureName(dataList.get(j).get(3));
			tempSamplingTestAssessPoint.setAssessPointCode(dataList.get(j).get(4));
			tempSamplingTestAssessPoint.setAssessPointName(dataList.get(j).get(5));
			tempSamplingTestAssessPoint.setAffectSubjects(dataList.get(j).get(6));
			tempSamplingTestAssessPoint.setImplementProof(dataList.get(j).get(7));
			tempSamplingTestAssessPoint.setFileUploadEntity(fileUploadEntity);
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
			tempSamplingTestAssessPoint.setProcessId(processId);
			
			String processPointId = "";
			for(Measure controlMeasure : controlMeasureList){
				if(controlMeasure.getCode().equals(dataList.get(j).get(2))){
					//真实表中存在
					processPointId = controlMeasure.getId();
					break;
				}
			}
			if("".equals(processPointId)){
				//真实表中不存在
				for(TempControlMeasure tempControlMeasure : tempControlMeasureList){
					if(tempControlMeasure.getControlMeasureCode().equals(dataList.get(j).get(2))){
						//临时表中存在
						processPointId = tempControlMeasure.getId();
						break;
					}
				}
			}
			tempSamplingTestAssessPoint.setControlMeasureId(processPointId);

			//o_tempSamplingTestAssessPointDAO.merge(tempSamplingTestAssessPoint);
			tempSamplingTestAssessPointList.add(tempSamplingTestAssessPoint);
			i++;
		}
		//批量导入临时表数据
		batchSaveTempData(tempSamplingTestAssessPointList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempSamplingTestAssessPoint> tempSamplingTestAssessPointList){
		o_tempSamplingTestAssessPointDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_SAMPLING_TEST_ASSESS_POINT(id,E_INDEX,PROCESS_CODE,PROCESS_NAME,CONTROL_MEASURE_CODE," +
						"CONTROL_MEASURE_NAME,ASSESS_POINT_CODE,ASSESS_POINT_NAME,AFFECT_SUBJECTS,IMPLEMENT_PROOF,PROCESS_ID,CONTROL_MEASURE_ID) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempSamplingTestAssessPoint o : tempSamplingTestAssessPointList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getProcessCode());
					pst.setObject(4, o.getProcessName());
					pst.setObject(5, o.getControlMeasureCode());
					pst.setObject(6, o.getControlMeasureName());
					pst.setObject(7, o.getAssessPointCode());
					pst.setObject(8, o.getAssessPointName());
					pst.setObject(9, o.getAffectSubjects());
					pst.setObject(10, o.getImplementProof());
					pst.setObject(11, o.getProcessId());
					pst.setObject(12, o.getControlMeasureId());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempSamplingTestAssessPointDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的流程节点临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempSamplingTestAssessPoint>
	 */
	public List<TempSamplingTestAssessPoint> findSamplingTestAssessPointPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempSamplingTestAssessPointDAO.createCriteria();
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
	public void removeBatchTempSamplingTestAssessPointByHql(){
		String hql="delete from TempSamplingTestAssessPoint";
		o_tempSamplingTestAssessPointDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempSamplingTestAssessPointDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempSamplingTestAssessPoint> tempSamplingTestAssessPointList = (List<TempSamplingTestAssessPoint>) map.get("tempSamplingTestAssessPointList");
				/*
				 * 转换信息list
				 */
				//财报科目数据字典项list
				List<DictEntry> affectSubjectList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_rela_subject");
				
				//穿行测试评价点sql--8个字段
				String basicSql = "insert into t_ca_assessment_point(id,processure_id,measure_id,assessment_point_code,edesc,rela_subject,ecomment,etype) values(?,?,?,?,?,?,?,?)";
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				
				for (TempSamplingTestAssessPoint tempSamplingTestAssessPoint : tempSamplingTestAssessPointList) {
					/*
					 * 穿行测试评价点信息
					 */
					basicPst.setString(1, tempSamplingTestAssessPoint.getId());
					basicPst.setString(2, tempSamplingTestAssessPoint.getProcessId());
					basicPst.setString(3, tempSamplingTestAssessPoint.getControlMeasureId());
					basicPst.setString(4, tempSamplingTestAssessPoint.getAssessPointCode());
					basicPst.setString(5, tempSamplingTestAssessPoint.getAssessPointName());
					if(StringUtils.isNotBlank(tempSamplingTestAssessPoint.getAffectSubjects())){
						StringBuilder affectSubjectId = new StringBuilder();
						String[] affectSubjectsArray = tempSamplingTestAssessPoint.getAffectSubjects().split(",");
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
					basicPst.setString(7, tempSamplingTestAssessPoint.getImplementProof());
					basicPst.setString(8, Contents.ASSESS_POINT_TYPE_EXECUTE);
					
					basicPst.addBatch();
					
				}
				basicPst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
}