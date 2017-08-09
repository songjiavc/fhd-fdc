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
import com.fhd.dao.icm.tempimport.TempProcessPointDAO;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.icm.tempimport.TempProcessPoint;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.process.ProcessPointBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 流程导入临时表BO.
 * @author 吴德福
 * @Date 2013-12-03 14:10:32
 */
@Service
@SuppressWarnings("unchecked")
public class TempProcessPointBO {

	@Autowired
	private TempProcessPointDAO o_tempProcessPointDAO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private TempProcessBO o_tempProcessBO;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	
	/**
	 * 验证流程节点信息.
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateProcessPointData(String fileId){
		Map<String,Object> processPointMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempProcessPoint> tempProcessPointList = this.findProcessPointPreviewListBySome("", fileId);
		//部门list
		List<SysOrganization> organizationList = o_organizationBO.findByCompanyId(UserContext.getUser().getCompanyid());
		List<String> orgNameList = new ArrayList<String>();
		for (SysOrganization sysOrganization : organizationList) {
			if(!orgNameList.contains(sysOrganization.getOrgname())){
				orgNameList.add(sysOrganization.getOrgname());
			}
		}
		//员工list
		List<SysEmployee> employeeList = o_employeeBO.findByCompanyId(UserContext.getUser().getCompanyid());
		List<String> empNameList = new ArrayList<String>();
		for (SysEmployee sysEmployee : employeeList) {
			if(!empNameList.contains(sysEmployee.getEmpname())){
				empNameList.add(sysEmployee.getEmpname());
			}
		}
		//频率数据字典项list
		List<DictEntry> pointTypeList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ca_point_type");
		List<String> pointTypeNameList = new ArrayList<String>();
		for (DictEntry dictEntry : pointTypeList) {
			if(!pointTypeNameList.contains(dictEntry.getName())){
				pointTypeNameList.add(dictEntry.getName());
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

		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempProcessPoint tempProcessPoint : tempProcessPointList) {
			StringBuffer errorTip=new StringBuffer();
			
			if(StringUtils.isBlank(tempProcessPoint.getProcessCode())){
				errorTip.append("(").append("A").append(i).append(")").append("流程编号必填!");
			}else{
				//验证流程编号是否正确
				if(!processCodeList.contains(tempProcessPoint.getProcessCode())){
					errorTip.append("(").append("A").append(i).append(")").append("流程编号不存在!");
				}
			}
			if(StringUtils.isBlank(tempProcessPoint.getProcessName())){
				errorTip.append("(").append("B").append(i).append(")").append("流程名称必填!");
			}
			if(StringUtils.isBlank(tempProcessPoint.getProcessPointCode())){
				errorTip.append("(").append("C").append(i).append(")").append("流程节点编号必填!");
			}else{
				//验证流程节点编号是否正确
				if(processPointCodeList.contains(tempProcessPoint.getProcessPointCode())){
					errorTip.append("(").append("C").append(i).append(")").append("流程节点编号已存在!");
				}
			}
			if(StringUtils.isBlank(tempProcessPoint.getProcessPointName())){
				errorTip.append("(").append("D").append(i).append(")").append("流程节点名称必填!");
			}
			if(StringUtils.isBlank(tempProcessPoint.getType())){
				errorTip.append("(").append("E").append(i).append(")").append("流程节点类型必填!");
			}else{
				//验证发生频率是否正确
				if(!pointTypeNameList.contains(tempProcessPoint.getType())){
					errorTip.append("(").append("E").append(i).append(")").append("流程节点类型不存在!");
				}
			}
			if(StringUtils.isBlank(tempProcessPoint.getResponsibleOrg())){
				errorTip.append("(").append("G").append(i).append(")").append("流程节点责任部门必填!");
			}else{
				//验证责任部门是否正确
				if(!orgNameList.contains(tempProcessPoint.getResponsibleOrg())){
					errorTip.append("(").append("G").append(i).append(")").append("流程节点责任部门不存在!");
				}
			}
			if(StringUtils.isBlank(tempProcessPoint.getResponsibleEmp())){
				errorTip.append("(").append("H").append(i).append(")").append("流程节点责任人必填!");
			}else{
				//验证责任人是否正确
				if(!empNameList.contains(tempProcessPoint.getResponsibleEmp())){
					errorTip.append("(").append("H").append(i).append(")").append("流程节点责任人不存在!");
				}
			}
			if(errorTip.length()>0){
				errorCount++;
				tempProcessPoint.setErrorTip(errorTip.toString());
				o_tempProcessPointDAO.merge(tempProcessPoint);
			}
			i++;
		}
		
		processPointMap.put("allCount", tempProcessPointList.size());
		processPointMap.put("correctCount", tempProcessPointList.size()-errorCount);
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
	public void saveTempProcessPoint(List<List<String>> dataList, FileUploadEntity fileUploadEntity){
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
		List<TempProcessPoint> tempProcessPointList = new ArrayList<TempProcessPoint>();	//临时表数据集合
		//真实表中的流程list
		List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		//临时表中流程list
		List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", "");
		
		//流程code临时字段，用于排序
		String tempProcessureCode = "";
		Integer sort = 1;
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempProcessPoint tempProcessPoint = new TempProcessPoint();
			tempProcessPoint.setId(Identities.uuid());
			tempProcessPoint.setIndex(String.valueOf(i+1+START_ROW));
			tempProcessPoint.setProcessCode(dataList.get(j).get(0));
			tempProcessPoint.setProcessName(dataList.get(j).get(1));
			tempProcessPoint.setProcessPointCode(dataList.get(j).get(2));
			tempProcessPoint.setProcessPointName(dataList.get(j).get(3));
			tempProcessPoint.setType(dataList.get(j).get(4));
			tempProcessPoint.setProcessPointDesc(dataList.get(j).get(5));
			tempProcessPoint.setResponsibleOrg(dataList.get(j).get(6));
			tempProcessPoint.setResponsibleEmp(dataList.get(j).get(7));
			tempProcessPoint.setInputInfo(dataList.get(j).get(8));
			tempProcessPoint.setOutputInfo(dataList.get(j).get(9));
			tempProcessPoint.setFileUploadEntity(fileUploadEntity);
			//删除状态:默认值为--1
			tempProcessPoint.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			//状态:默认值为--0yn_y(数据字典0yn)
			tempProcessPoint.setStatus("0yn_y");
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
			tempProcessPoint.setProcessId(processId);
			//排序
			if("".equals(tempProcessureCode)){
				//当前记录为第一条记录
				tempProcessureCode = dataList.get(j).get(0);
			}else if(tempProcessureCode.equals(dataList.get(j).get(0))){
				//当前记录流程code与上一条记录流程code相同，排序+1
				sort += 1;
			}else{
				//当前记录流程code与上一条记录流程code不同，排序重置为1
				tempProcessureCode = dataList.get(j).get(0);
				sort = 1;
			}
			tempProcessPoint.setSort(sort);
			//o_tempProcessPointDAO.merge(tempProcessPoint);
			i++;
			
			//把新增的临时表数据添加表list中
			tempProcessPointList.add(tempProcessPoint);
		}
		//批量导入临时表数据
		batchSaveTempData(tempProcessPointList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempProcessPoint> tempProcessPointList){
		o_tempProcessPointDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_PROCESS_POINT(id,E_INDEX,PROCESS_CODE,PROCESS_NAME,PROCESS_POINT_CODE," +
						"PROCESS_POINT_NAME,TYPE,PROCESS_POINT_DESC,RESPONSIBLE_ORG,RESPONSIBLE_EMP,INPUT_INFO,OUTPUT_INFO," +
						"DELETE_STATUS,STATUS,PROCESS_ID,ESORT) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempProcessPoint o : tempProcessPointList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getProcessCode());
					pst.setObject(4, o.getProcessName());
					pst.setObject(5, o.getProcessPointCode());
					pst.setObject(6, o.getProcessPointName());
					pst.setObject(7, o.getType());
					pst.setObject(8, o.getProcessPointDesc());
					pst.setObject(9, o.getResponsibleOrg());
					pst.setObject(10, o.getResponsibleEmp());
					pst.setObject(11, o.getInputInfo());
					pst.setObject(12, o.getOutputInfo());
					pst.setObject(13, o.getDeleteStatus());
					pst.setObject(14, o.getStatus());
					pst.setObject(15, o.getProcessId());
					pst.setObject(16, o.getSort());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempProcessPointDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的流程节点临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempProcessPoint>
	 */
	public List<TempProcessPoint> findProcessPointPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempProcessPointDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.like("processPointCode", query, MatchMode.ANYWHERE), Restrictions.like("processPointName", query, MatchMode.ANYWHERE)));
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
	public void removeBatchTempProcessPointByHql(){
		String hql="delete from TempProcessPoint";
		o_tempProcessPointDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempProcessPointDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempProcessPoint> tempProcessPointList = (List<TempProcessPoint>) map.get("tempProcessPointList");
				//按流程code、流程节点code排序
				Collections.sort(tempProcessPointList,new Comparator<TempProcessPoint>(){
					@Override
					public int compare(TempProcessPoint o1, TempProcessPoint o2) {
						String a1 = o1.getProcessCode();
						String a2 = o2.getProcessCode();
						String a3 = o1.getProcessPointCode();
						String a4 = o2.getProcessPointCode();
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
				/*
				 * 转换信息list
				 */
				//部门list
				List<SysOrganization> organizationList = o_organizationBO.findByCompanyId(UserContext.getUser().getCompanyid());
				//员工list
				List<SysEmployee> employeeList = o_employeeBO.findByCompanyId(UserContext.getUser().getCompanyid());
				//节点类型数据字典项list
				List<DictEntry> pointTypeList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ca_point_type");
				
				//流程节点sql--11个字段
				String basicSql = "insert into t_ic_control_point(id,processure_id,control_point_code,control_point_name,edesc,esort,estatus,delete_status,info_input,info_output,point_type) values(?,?,?,?,?,?,?,?,?,?,?)";
				//流程节点部门人员sql--5个字段
				String relateSql = "insert into t_ic_control_point_rela_org(id,control_point_id,org_id,etype,emp_id) values(?,?,?,?,?)";
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement relatePst = connection.prepareStatement(relateSql);
				
				for (TempProcessPoint tempProcessPoint : tempProcessPointList) {
					/*
					 * 流程节点信息
					 */
					basicPst.setString(1, tempProcessPoint.getId());
					basicPst.setString(2, tempProcessPoint.getProcessId());
					basicPst.setString(3, tempProcessPoint.getProcessPointCode());
					basicPst.setString(4, tempProcessPoint.getProcessPointName());
					basicPst.setString(5, tempProcessPoint.getProcessPointDesc());
					basicPst.setInt(6, tempProcessPoint.getSort());
					basicPst.setString(7, tempProcessPoint.getStatus());
					basicPst.setString(8, tempProcessPoint.getDeleteStatus());
					basicPst.setString(9, tempProcessPoint.getInputInfo());
					basicPst.setString(10, tempProcessPoint.getOutputInfo());
					//节点类型转换数据字典id
					for (DictEntry dictEntry : pointTypeList) {
						if(tempProcessPoint.getType().equals(dictEntry.getName())){
							basicPst.setString(11, dictEntry.getId());
							break;
						}
					}
					basicPst.addBatch();
					
					/*
					 * 流程部门/人员
					 */
					//责任部门
					if(StringUtils.isNotBlank(tempProcessPoint.getResponsibleOrg())){
						for (SysOrganization organization : organizationList) {
							if(organization.getOrgname().equals(tempProcessPoint.getResponsibleOrg())){
								relatePst.setString(1, Identities.uuid());
								relatePst.setString(2, tempProcessPoint.getId());
								relatePst.setString(3, organization.getId());
								relatePst.setString(4, Contents.ORG_RESPONSIBILITY);
								relatePst.setString(5, null);
								relatePst.addBatch();
								break;
							}
						}
					}
					//责任人
					if(StringUtils.isNotBlank(tempProcessPoint.getResponsibleEmp())){
						for (SysEmployee emp : employeeList) {
							if(emp.getEmpname().equals(tempProcessPoint.getResponsibleEmp())){
								relatePst.setString(1, Identities.uuid());
								relatePst.setString(2, tempProcessPoint.getId());
								relatePst.setString(3, null);
								relatePst.setString(4, Contents.EMP_RESPONSIBILITY);
								relatePst.setString(5, emp.getId());
								relatePst.addBatch();
								break;
							}
						}
					}
				}
				basicPst.executeBatch();
				relatePst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
}
