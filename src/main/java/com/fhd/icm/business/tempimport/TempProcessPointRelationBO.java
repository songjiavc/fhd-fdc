package com.fhd.icm.business.tempimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.fhd.dao.icm.tempimport.TempProcessPointRelationDAO;
import com.fhd.entity.icm.tempimport.TempProcessPoint;
import com.fhd.entity.icm.tempimport.TempProcessPointRelation;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessPointRelaPointSelf;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessPointBO;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 流程节点关系导入临时表BO.
 * @author 吴德福
 * @Date 2013-12-06 17:40:32
 */
@Service
@SuppressWarnings("unchecked")
public class TempProcessPointRelationBO {

	@Autowired
	private TempProcessPointBO o_tempProcessPointBO;
	@Autowired
	private TempProcessPointRelationDAO o_tempProcessPointRelationDAO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	
	/**
	 * 验证流程节点关系.
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateProcessPointRelationData(String fileId){
		Map<String,Object> processPointRelationMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempProcessPointRelation> tempProcessPointRelationList = this.findProcessPointRelationPreviewListBySome("", fileId);

		//流程节点list
		List<ProcessPoint> processPointList = o_processPointBO.findProcessPointByCompanyId(UserContext.getUser().getCompanyid());
		List<String> processPointCodeList = new ArrayList<String>();
		for (ProcessPoint processPoint : processPointList) {
			if(!processPointCodeList.contains(processPoint.getCode())){
				processPointCodeList.add(processPoint.getCode());
			}
		}
		//流程节点临时表list
		List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", fileId);
		for (TempProcessPoint tempProcessPoint : tempProcessPointList) {
			if(!processPointCodeList.contains(tempProcessPoint.getProcessPointCode())){
				processPointCodeList.add(tempProcessPoint.getProcessPointCode());
			}
		}
		
		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempProcessPointRelation tempProcessPointRelation : tempProcessPointRelationList) {
			StringBuffer errorTip=new StringBuffer();
			
			if(StringUtils.isBlank(tempProcessPointRelation.getProcessPointCode())){
				errorTip.append("(").append("C").append(i).append(")").append("流程节点编号必填!");
			}else{
				//验证流程节点编号是否正确
				if(!processPointCodeList.contains(tempProcessPointRelation.getProcessPointCode())){
					errorTip.append("(").append("C").append(i).append(")").append("流程节点编号不存在!");
				}
			}
			if(StringUtils.isBlank(tempProcessPointRelation.getProcessPointName())){
				errorTip.append("(").append("D").append(i).append(")").append("流程节点名称必填!");
			}
			if(StringUtils.isBlank(tempProcessPointRelation.getParentProcessPointCode())){
				errorTip.append("(").append("E").append(i).append(")").append("上一步流程节点编号必填!");
			}else{
				//验证上级流程节点编号是否正确
				if(!processPointCodeList.contains(tempProcessPointRelation.getParentProcessPointCode())){
					errorTip.append("(").append("E").append(i).append(")").append("上一步流程节点编号不存在!");
				}
			}
			if(StringUtils.isBlank(tempProcessPointRelation.getParentProcessPointName())){
				errorTip.append("(").append("F").append(i).append(")").append("上一步流程节点名称必填!");
			}
			
			if(errorTip.length()>0){
				errorCount++;
				tempProcessPointRelation.setErrorTip(errorTip.toString());
				o_tempProcessPointRelationDAO.merge(tempProcessPointRelation);
			}
			i++;
		}
		
		processPointRelationMap.put("allCount", tempProcessPointRelationList.size());
		processPointRelationMap.put("correctCount", tempProcessPointRelationList.size()-errorCount);
		processPointRelationMap.put("errorCount", errorCount);
		
		return processPointRelationMap;
	}
	/**
	 * excel表数据存入流程节点关系临时表.
	 * @author 吴德福
	 * @param dataListList
	 * @param fileUploadEntity
	 */
	@Transactional
	public void saveTempProcessPointRelation(List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		//按流程code排序
//		Collections.sort(dataList,new Comparator<List<String>>(){
//			@Override
//			public int compare(List<String> o1, List<String> o2) {
//				String a1 = o1.get(0);
//				String a2 = o2.get(0);
//				if(a1.compareToIgnoreCase(a2)<0){
//					return -1;
//				}else if(a1.compareToIgnoreCase(a2)>0){
//					return 1;
//				}else{
//					return 0;
//				}
//			}			
//		});
		List<TempProcessPointRelation> tempProcessPointRelationList = new ArrayList<TempProcessPointRelation>();
		//真实表中的流程节点list
		List<ProcessPoint> processPointList = o_processPointBO.findProcessPointByCompanyId(UserContext.getUser().getCompanyid());
		//临时表中流程节点list
		List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", "");
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempProcessPointRelation tempProcessPointRelation = new TempProcessPointRelation();
			tempProcessPointRelation.setId(Identities.uuid());
			tempProcessPointRelation.setIndex(String.valueOf(i+1+START_ROW));
			tempProcessPointRelation.setProcessCode(dataList.get(j).get(0));
			tempProcessPointRelation.setProcessName(dataList.get(j).get(1));
			tempProcessPointRelation.setProcessPointCode(dataList.get(j).get(2));
			tempProcessPointRelation.setProcessPointName(dataList.get(j).get(3));
			tempProcessPointRelation.setParentProcessPointCode(dataList.get(j).get(4));
			tempProcessPointRelation.setParentProcessPointName(dataList.get(j).get(5));
			tempProcessPointRelation.setEnterCondition(dataList.get(j).get(6));
			tempProcessPointRelation.setFileUploadEntity(fileUploadEntity);
			
			String processId = "";
			String processPointId = "";
			for(ProcessPoint processPoint : processPointList){
				if(processPoint.getCode().equals(dataList.get(j).get(2))){
					//真实表中存在
					processId = processPoint.getProcess().getId();
					processPointId = processPoint.getId();
					break;
				}
			}
			if("".equals(processPointId)){
				//真实表中不存在
				for(TempProcessPoint tempProcessPoint : tempProcessPointList){
					if(tempProcessPoint.getProcessPointCode().equals(dataList.get(j).get(2))){
						//临时表中存在
						processId = tempProcessPoint.getProcessId();
						processPointId = tempProcessPoint.getId();
						break;
					}
				}
			}
			if(!"".equals(processId)){
				tempProcessPointRelation.setProcessId(processId);
			}
			if(!"".equals(processPointId)){
				tempProcessPointRelation.setProcessPointId(processPointId);
			}
			
			String parentProcessPointId = "";
			for(ProcessPoint processPoint : processPointList){
				if(processPoint.getCode().equals(dataList.get(j).get(4))){
					//真实表中存在
					parentProcessPointId = processPoint.getId();
					break;
				}
			}
			if("".equals(parentProcessPointId)){
				//真实表中不存在
				for(TempProcessPoint tempProcessPoint : tempProcessPointList){
					if(tempProcessPoint.getProcessPointCode().equals(dataList.get(j).get(4))){
						//临时表中存在
						parentProcessPointId = tempProcessPoint.getId();
						break;
					}
				}
			}
			if(!"".equals(parentProcessPointId)){
				tempProcessPointRelation.setParentProcessPointId(parentProcessPointId);
			}
			
			//o_tempProcessPointRelationDAO.merge(tempProcessPointRelation);
			tempProcessPointRelationList.add(tempProcessPointRelation);
			i++;
		}
		
		//批量导入临时表数据
		batchSaveTempData(tempProcessPointRelationList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempProcessPointRelation> tempProcessPointRelationList){
		o_tempProcessPointRelationDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_PROCESS_POINT_RELATION(id,E_INDEX,PROCESS_CODE,PROCESS_NAME," +
						"PROCESS_POINT_CODE,PROCESS_POINT_NAME,PARENT_PROCESS_POINT_CODE,PARENT_PROCESS_POINT_NAME," +
						"ENTER_CONDITION,PROCESS_ID,PROCESS_POINT_ID,PARENT_PROCESS_POINT_ID) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempProcessPointRelation o : tempProcessPointRelationList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getProcessCode());
					pst.setObject(4, o.getProcessName());
					pst.setObject(5, o.getProcessPointCode());
					pst.setObject(6, o.getProcessPointName());
					pst.setObject(7, o.getParentProcessPointCode());
					pst.setObject(8, o.getParentProcessPointName());
					pst.setObject(9, o.getEnterCondition());
					pst.setObject(10, o.getProcessId());
					pst.setObject(11, o.getProcessPointId());
					pst.setObject(12, o.getParentProcessPointId());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempProcessPointRelationDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的流程节点关系临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempProcessPointRelation>
	 */
	public List<TempProcessPointRelation> findProcessPointRelationPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempProcessPointRelationDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.like("processPointCode", query, MatchMode.ANYWHERE), Restrictions.like("processPointName", query, MatchMode.ANYWHERE)), Restrictions.or(Restrictions.like("parentProcessPointCode", query, MatchMode.ANYWHERE), Restrictions.like("parentProcessPointName", query, MatchMode.ANYWHERE))));
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		criteria.addOrder(Order.desc("errorTip"));
		criteria.addOrder(Order.asc("index"));
		return criteria.list();
	}
	/**
	 * 批量删除流程节点关系临时表.
	 * @author 吴德福
	 */
	@Transactional
	public void removeBatchTempProcessPointRelationByHql(){
		String hql="delete from TempProcessPointRelation";
		o_tempProcessPointRelationDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempProcessPointRelationDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempProcessPointRelation> tempProcessPointRelationList = (List<TempProcessPointRelation>) map.get("tempProcessPointRelationList");
				//按流程code、流程节点code排序
				Collections.sort(tempProcessPointRelationList,new Comparator<TempProcessPointRelation>(){
					@Override
					public int compare(TempProcessPointRelation o1, TempProcessPointRelation o2) {
						String a1 = o1.getProcessCode();
						String a2 = o2.getProcessCode();
						String a3 = o1.getParentProcessPointCode();
						String a4 = o2.getParentProcessPointCode();
						if(a1.compareToIgnoreCase(a2)<0){
							return -1;
						}else if(a1.compareToIgnoreCase(a2)>0){
							return 1;
						}else{
							if(a3.compareToIgnoreCase(a4)<0){
								return -1;
							}else if(a3.compareToIgnoreCase(a4)>0){
								return 1;
							}
							return 0;
						}
					}			
				});

				//流程节点关系sql--5个字段
				String basicSql = "insert into t_ic_control_point_relevance(id,processure_id,control_point_id,previous_control_point_id,enter_condition) values(?,?,?,?,?)";
				
				//验证风险相关流程关系是否存在
				List<ProcessPointRelaPointSelf> processPointRelationList = o_processPointBO.findProcessPointRelaPointSelfByCompanyId(UserContext.getUser().getCompanyid());
				//验证风险相关流程关系是否存在
				List<ProcessPointRelaPointSelf> tempRelationList = new ArrayList<ProcessPointRelaPointSelf>();
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				
				for (TempProcessPointRelation tempProcessPointRelation : tempProcessPointRelationList) {
					/*
					 * 流程节点关系信息
					 */
					boolean processPointRelationFlag = true;
					if(StringUtils.isBlank(tempProcessPointRelation.getProcessPointId()) || StringUtils.isBlank(tempProcessPointRelation.getParentProcessPointId())){
						processPointRelationFlag = false;
					}else {
						for (ProcessPointRelaPointSelf processPointRelaPointSelf : processPointRelationList) {
							if(processPointRelaPointSelf.getProcessPoint().getId().equals(tempProcessPointRelation.getProcessPointId()) && processPointRelaPointSelf.getPreviousProcessPoint().getId().equals(tempProcessPointRelation.getParentProcessPointId())){
								processPointRelationFlag = false;
								break;
							}
						}
						for (ProcessPointRelaPointSelf tempProcessPointRelaPointSelf : tempRelationList) {
							if(tempProcessPointRelaPointSelf.getProcessPoint().getId().equals(tempProcessPointRelation.getProcessPointId()) && tempProcessPointRelaPointSelf.getPreviousProcessPoint().getId().equals(tempProcessPointRelation.getParentProcessPointId())){
								processPointRelationFlag = false;
								break;
							}
						}
					}
					if(processPointRelationFlag){
						ProcessPointRelaPointSelf tempProcessPointRelaPointSelf = new ProcessPointRelaPointSelf();
						ProcessPoint tempProcessPoint = new ProcessPoint();
						tempProcessPoint.setId(tempProcessPointRelation.getProcessPointId());
						tempProcessPointRelaPointSelf.setProcessPoint(tempProcessPoint);
						ProcessPoint tempPreviousProcessPoint = new ProcessPoint();
						tempPreviousProcessPoint.setId(tempProcessPointRelation.getParentProcessPointId());
						tempProcessPointRelaPointSelf.setPreviousProcessPoint(tempPreviousProcessPoint);
						tempRelationList.add(tempProcessPointRelaPointSelf);
						
						basicPst.setString(1, tempProcessPointRelation.getId());
						basicPst.setString(2, tempProcessPointRelation.getProcessId());
						basicPst.setString(3, tempProcessPointRelation.getProcessPointId());
						basicPst.setString(4, tempProcessPointRelation.getParentProcessPointId());
						basicPst.setString(5, tempProcessPointRelation.getEnterCondition());
						basicPst.addBatch();
					}
				}
				basicPst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
}