package com.fhd.icm.business.tempimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.tempimport.TempProcessDAO;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.process.Process;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 流程导入临时表BO.
 * @author 吴德福
 * @Date 2013-11-26 14:10:32
 */
@Service
@SuppressWarnings({"unchecked"})
public class TempProcessBO {

	@Autowired
	private TempProcessDAO o_tempProcessDAO;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private ProcessBO o_processBO;
	
	/**
	 * 验证流程信息.
	 * @author 吴德福
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateProcessData(String fileId){
		Map<String,Object> processMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempProcess> tempProcessList = this.findProcessPreviewListBySome("", fileId);
		//流程分类list
		List<TempProcess> tempProcessClassList = new ArrayList<TempProcess>();
		for (TempProcess tempProcess : tempProcessList) {
			if(StringUtils.isNotBlank(tempProcess.getParentProcessureCode())){
				tempProcessClassList.add(tempProcess);
			}
		}
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
		List<DictEntry> frequencyList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_frequency");
		List<String> frequencyNameList = new ArrayList<String>();
		for (DictEntry dictEntry : frequencyList) {
			if(!frequencyNameList.contains(dictEntry.getName())){
				frequencyNameList.add(dictEntry.getName());
			}
		}
		//财报科目数据字典项list
		List<DictEntry> affectSubjectsList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_rela_subject");
		List<String> affectSubjectsNameList = new ArrayList<String>();
		for (DictEntry dictEntry : affectSubjectsList) {
			if(!affectSubjectsNameList.contains(dictEntry.getName())){
				affectSubjectsNameList.add(dictEntry.getName());
			}
		}
		//流程list
		//流程编号验证list
		List<String> processCodeList = new ArrayList<String>();
		//上级流程编号验证list
		List<String> parentProcessCodeList = new ArrayList<String>();
		List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		for (Process process : processList) {
			if(!processCodeList.contains(process.getCode())){
				processCodeList.add(process.getCode());
			}
			if(!parentProcessCodeList.contains(process.getCode())){
				parentProcessCodeList.add(process.getCode());
			}
		}
		for (TempProcess tempProcess : tempProcessList) {
			if(StringUtils.isNotBlank(tempProcess.getProcessureCode()) && !parentProcessCodeList.contains(tempProcess.getProcessureCode())){
				parentProcessCodeList.add(tempProcess.getProcessureCode());
			}
		}

		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempProcess tempProcess : tempProcessList) {
			StringBuffer errorTip=new StringBuffer();
			if(tempProcessClassList.contains(tempProcess) || StringUtils.isBlank(tempProcess.getParentProcessureCode())){
				//流程分类验证
				if(StringUtils.isNotBlank(tempProcess.getParentProcessureCode()) && !parentProcessCodeList.contains(tempProcess.getParentProcessureCode())){
					//验证流程编号是否正确
					errorTip.append("(").append("A").append(i).append(")").append("流程编号不存在!");
				}
				if(StringUtils.isBlank(tempProcess.getProcessureCode())){
					errorTip.append("(").append("C").append(i).append(")").append("流程编号必填!");
				}else{
					//验证流程编号是否正确
					if(processCodeList.contains(tempProcess.getProcessureCode())){
						errorTip.append("(").append("C").append(i).append(")").append("流程编号已存在!");
					}
				}
				if(StringUtils.isBlank(tempProcess.getProcessureName())){
					errorTip.append("(").append("D").append(i).append(")").append("流程名称必填!");
				}
				if(StringUtils.isNotBlank(tempProcess.getResponsibleOrg()) && !orgNameList.contains(tempProcess.getResponsibleOrg())){
					//验证责任部门是否正确
					errorTip.append("(").append("E").append(i).append(")").append("流程责任部门不存在!");
				}
				if(StringUtils.isNotBlank(tempProcess.getFrequency()) && !frequencyNameList.contains(tempProcess.getFrequency())){
					//验证发生频率是否正确
					errorTip.append("(").append("G").append(i).append(")").append("流程发生频率不存在!");
				}
				if(StringUtils.isNotBlank(tempProcess.getResponsibleEmp()) && !empNameList.contains(tempProcess.getResponsibleEmp())){
					//验证责任人是否正确
					errorTip.append("(").append("I").append(i).append(")").append("流程责任人不存在!");
				}
				if(StringUtils.isNotBlank(tempProcess.getRelateOrg())){
					//验证相关部门是否正确
					String[] relateOrgNameArray = tempProcess.getRelateOrg().split(",");
					for (String relateOrgName : relateOrgNameArray) {
						if(!orgNameList.contains(relateOrgName)){
							errorTip.append("(").append("F").append(i).append(")").append("流程相关部门'").append(relateOrgName).append("'不存在!");
						}
					}
				}
				if(StringUtils.isNotBlank(tempProcess.getAffectSubjects())){
					//验证影响的财报科目是否正确
					String[] affectSubjectsNameArray = tempProcess.getAffectSubjects().split(",");
					for (String affectSubjectsName : affectSubjectsNameArray) {
						if(!affectSubjectsNameList.contains(affectSubjectsName)){
							errorTip.append("(").append("H").append(i).append(")").append("流程影响的财报科目'").append(affectSubjectsName).append("'不存在!");
						}
					}
				}
			}else{
				//末级流程验证
				if(StringUtils.isNotBlank(tempProcess.getParentProcessureCode()) && !parentProcessCodeList.contains(tempProcess.getParentProcessureCode())){
					//验证流程编号是否正确
					errorTip.append("(").append("A").append(i).append(")").append("流程编号不存在!");
				}
				if(StringUtils.isBlank(tempProcess.getProcessureCode())){
					errorTip.append("(").append("C").append(i).append(")").append("流程编号必填!");
				}else{
					//验证流程编号是否正确
					if(processCodeList.contains(tempProcess.getProcessureCode())){
						errorTip.append("(").append("C").append(i).append(")").append("流程编号已存在!");
					}
				}
				if(StringUtils.isBlank(tempProcess.getProcessureName())){
					errorTip.append("(").append("D").append(i).append(")").append("流程名称必填!");
				}
				if(StringUtils.isBlank(tempProcess.getResponsibleOrg())){
					errorTip.append("(").append("E").append(i).append(")").append("流程责任部门必填!");
				}else{
					//验证责任部门是否正确
					if(!orgNameList.contains(tempProcess.getResponsibleOrg())){
						errorTip.append("(").append("E").append(i).append(")").append("流程责任部门不存在!");
					}
				}
				if(StringUtils.isBlank(tempProcess.getFrequency())){
					errorTip.append("(").append("G").append(i).append(")").append("流程发生频率必填!");
				}else{
					//验证发生频率是否正确
					if(!frequencyNameList.contains(tempProcess.getFrequency())){
						errorTip.append("(").append("G").append(i).append(")").append("流程发生频率不存在!");
					}
				}
				if(StringUtils.isBlank(tempProcess.getResponsibleEmp())){
					errorTip.append("(").append("I").append(i).append(")").append("流程责任人必填!");
				}else{
					//验证责任人是否正确
					if(!empNameList.contains(tempProcess.getResponsibleEmp())){
						errorTip.append("(").append("I").append(i).append(")").append("流程责任人不存在!");
					}
				}
				if(StringUtils.isNotBlank(tempProcess.getRelateOrg())){
					//验证相关部门是否正确
					String[] relateOrgNameArray = tempProcess.getRelateOrg().split(",");
					for (String relateOrgName : relateOrgNameArray) {
						if(!orgNameList.contains(relateOrgName)){
							errorTip.append("(").append("F").append(i).append(")").append("流程相关部门'").append(relateOrgName).append("'不存在!");
						}
					}
				}
				if(StringUtils.isNotBlank(tempProcess.getAffectSubjects())){
					//验证影响的财报科目是否正确
					String[] affectSubjectsNameArray = tempProcess.getAffectSubjects().split(",");
					for (String affectSubjectsName : affectSubjectsNameArray) {
						if(!affectSubjectsNameList.contains(affectSubjectsName)){
							errorTip.append("(").append("H").append(i).append(")").append("流程影响的财报科目'").append(affectSubjectsName).append("'不存在!");
						}
					}
				}
			}
			if(errorTip.length()>0){
				errorCount++;
				tempProcess.setErrorTip(errorTip.toString());
				o_tempProcessDAO.merge(tempProcess);
			}
			i++;
		}
		
		processMap.put("allCount", tempProcessList.size());
		processMap.put("correctCount", tempProcessList.size()-errorCount);
		processMap.put("errorCount", errorCount);
		
		return processMap;
	}
	/**
	 * 根据类型判断导入的临时数据存入对应的临时表.
	 * id,parentId,idSeq,level,leaf5大字段，sort没必要导入
	 * @author 吴德福
	 * @param processList
	 * @param dataList
	 * @param fileUploadEntity
	 */
	@Transactional
	public void saveTempDataByDataListAndType(List<Process> processList, List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		List<TempProcess> tempProcessListBatch = new ArrayList<TempProcess>();
		List<TempProcess> tempProcessList = new ArrayList<TempProcess>();
		
		//流程code临时字段，用于排序。记录上一条流程的code
		String tempProcessureCode = "";
		Integer sort = 1;
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempProcess tempProcess = new TempProcess();
			String id = Identities.uuid();
			tempProcess.setId(id);
			tempProcess.setIndex(String.valueOf(i+1+START_ROW));
			tempProcess.setParentProcessureCode(dataList.get(j).get(0));
			tempProcess.setParentProcessureName(dataList.get(j).get(1));
			tempProcess.setProcessureCode(dataList.get(j).get(2));
			tempProcess.setProcessureName(dataList.get(j).get(3));
			tempProcess.setResponsibleOrg(dataList.get(j).get(4));
			tempProcess.setRelateOrg(dataList.get(j).get(5));
			tempProcess.setFrequency(dataList.get(j).get(6));
			tempProcess.setAffectSubjects(dataList.get(j).get(7));
			tempProcess.setResponsibleEmp(dataList.get(j).get(8));
			tempProcess.setFileUploadEntity(fileUploadEntity);

			boolean isExist = false;
			if("".equals(dataList.get(j).get(0))){
				//上级流程编号为空
				tempProcess.setParentId(null);
				tempProcess.setIdSeq("."+id+".");
				tempProcess.setLevel(1);
				tempProcessureCode = "";
				for (Process process : processList) {
					if(null == process.getParent()){
						sort += 1;
					}
				}
				isExist = true;
			}else{
				//导入的上级流程编号在数据库真实表中
				for (Process process : processList) {
					if(process.getCode().equals(dataList.get(j).get(0))){
						tempProcess.setParentId(process.getId());
						tempProcess.setIdSeq(process.getIdSeq()+id+".");
						tempProcess.setLevel(process.getLevel()+1);
						tempProcessureCode = process.getCode();
						Set<Process> processSet = process.getChildren();
						sort += processSet.size();
						if(process.getIsLeaf()){
							process.setIsLeaf(false);
							o_processBO.mergeProcess(process);
						}
						isExist = true;
						break;
					}
				}
			}
			if(!isExist){
				//导入的上级流程编号在excel表中
				for(TempProcess tp : tempProcessList){
					if(tp.getProcessureCode().equals(dataList.get(j).get(0))){
						tempProcess.setParentId(tp.getId());
						tempProcess.setIdSeq(tp.getIdSeq()+id+".");
						tempProcess.setLevel(tp.getLevel()+1);
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
						//更新上级流程的叶子结点
						if(tp.getIsLeaf()){
							tp.setIsLeaf(false);
							//o_tempProcessDAO.merge(tp);
						}
					}
				}
			}
			tempProcess.setSort(sort);
			
			tempProcess.setIsLeaf(true);
			tempProcess.setCompanyId(UserContext.getUser().getCompanyid());
			tempProcess.setCreateBy(UserContext.getUser().getEmpid());
			tempProcess.setDealStatus(Contents.DEAL_STATUS_FINISHED);
			tempProcess.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			tempProcess.setCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			//保存当前流程
			//o_tempProcessDAO.merge(tempProcess);
			tempProcessListBatch.add(tempProcess);
			
			//把新增的临时表数据添加表list中，给层级、主键序列、上级等使用
			if(!tempProcessList.contains(tempProcess)){
				tempProcessList.add(tempProcess);
			}
			
			i++;
		}
		
		//批量导入流程数据
		batchSaveTempData(tempProcessListBatch);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempProcess> tempProcessList){
		o_tempProcessDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_PROCESS(id,E_INDEX,PARENT_PROCESS_CODE,PARENT_PROCESS_NAME,PROCESS_CODE," +
						"PROCESS_NAME,RESPONSIBLE_ORG,RELATE_ORG,FREQUENCY,AFFECT_SUBJECTS,RESPONSIBLE_EMP,ESORT,IS_LEAF," +
						"COMPANY_ID,CREATE_BY,DEAL_STATUS,DELETE_STATUS,CREATE_TIME,PARENT_ID,ID_SEQ,ELEVEL) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempProcess o : tempProcessList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getParentProcessureCode());
					pst.setObject(4, o.getParentProcessureName());
					pst.setObject(5, o.getProcessureCode());
					pst.setObject(6, o.getProcessureName());
					pst.setObject(7, o.getResponsibleOrg());
					pst.setObject(8, o.getRelateOrg());
					pst.setObject(9, o.getFrequency());
					pst.setObject(10, o.getAffectSubjects());
					pst.setObject(11, o.getResponsibleEmp());
					pst.setObject(12, o.getSort());
					pst.setObject(13, o.getIsLeaf());
					pst.setObject(14, o.getCompanyId());
					pst.setObject(15, o.getCreateBy());
					pst.setObject(16, o.getDealStatus());
					pst.setObject(17, o.getDeleteStatus());
					pst.setObject(18, o.getCreateTime());
					pst.setObject(19, o.getParentId());
					pst.setObject(20, o.getIdSeq());
					pst.setObject(21, o.getLevel());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempProcessDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的流程临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempProcess>
	 */
	public List<TempProcess> findProcessPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempProcessDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.like("processureCode", query, MatchMode.ANYWHERE), Restrictions.like("processureName", query, MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		criteria.addOrder(Order.desc("errorTip"));
		criteria.addOrder(Order.asc("index"));
		return criteria.list();
	}
	/**
	 * 批量删除流程临时表.
	 * @author 吴德福
	 */
	@Transactional
	public void removeBatchTempProcessByHql(){
		String hql="delete from TempProcess";
		o_tempProcessDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempProcessDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempProcess> tempProcessList = (List<TempProcess>) map.get("tempProcessList");
				//按上级流程code、流程code排序
				Collections.sort(tempProcessList,new Comparator<TempProcess>(){
					@Override
					public int compare(TempProcess o1, TempProcess o2) {
						String a1 = o1.getProcessureCode();
						String a2 = o2.getProcessureCode();
						if(a1.compareToIgnoreCase(a2)<0){
							return -1;
						}else if(a1.compareToIgnoreCase(a2)>0){
							return 1;
						}else{
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
				//频率数据字典项list
				List<DictEntry> frequencyList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_frequency");
				//财报科目数据字典项list
				List<DictEntry> affectSubjectsList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_rela_subject");
				
				//流程sql--15个字段
				String basicSql = "insert into t_ic_processure(id,processure_code,processure_name,parent_id,id_seq,elevel,esort,is_leaf,deal_status,delete_status,create_time,create_by,company_id,process_class,erange) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//流程部门人员sql--5个字段
				String relateSql = "insert into t_ic_processure_rela_org(id,processure_id,org_id,etype,emp_id) values(?,?,?,?,?)";
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement relatePst = connection.prepareStatement(relateSql);
				
				for (TempProcess tempProcess : tempProcessList) {
					/*
					 * 流程信息
					 */
					basicPst.setString(1, tempProcess.getId());
					basicPst.setString(2, tempProcess.getProcessureCode());
					basicPst.setString(3, tempProcess.getProcessureName());
					basicPst.setString(4, tempProcess.getParentId());
					basicPst.setString(5, tempProcess.getIdSeq());
					basicPst.setInt(6, tempProcess.getLevel());
					basicPst.setInt(7, tempProcess.getSort());
					basicPst.setBoolean(8, tempProcess.getIsLeaf());
					basicPst.setString(9, tempProcess.getDealStatus());
					basicPst.setString(10, tempProcess.getDeleteStatus());
					basicPst.setString(11, tempProcess.getCreateTime());
					basicPst.setString(12, tempProcess.getCreateBy());
					basicPst.setString(13, tempProcess.getCompanyId());
					if(StringUtils.isNotBlank(tempProcess.getFrequency())){
						//发生频率转换数据字典id
						for (DictEntry dictEntry : frequencyList) {
							if(tempProcess.getFrequency().equals(dictEntry.getName())){
								basicPst.setString(14, dictEntry.getId());
								break;
							}
						}
					}else{
						basicPst.setString(14, null);
					}
					if(StringUtils.isNotBlank(tempProcess.getAffectSubjects())){
						StringBuilder affectSubjectId = new StringBuilder();
						String[] affectSubjectsArray = tempProcess.getAffectSubjects().split(",");
						//财报科目转换数据字典id
						for (DictEntry dictEntry : affectSubjectsList) {
							for (String affectSubject : affectSubjectsArray) {
								if(affectSubject.equals(dictEntry.getName())){
									if(affectSubjectId.length()>0){
										affectSubjectId.append(",");
									}
									affectSubjectId.append(dictEntry.getId());
								}
							}
						}
						basicPst.setString(15, affectSubjectId.toString());
					}else{
						basicPst.setString(15, null);
					}
					
					basicPst.addBatch();
					
					/*
					 * 流程部门/人员
					 */
					//责任部门
					if(StringUtils.isNotBlank(tempProcess.getResponsibleOrg())){
						for (SysOrganization organization : organizationList) {
							if(organization.getOrgname().equals(tempProcess.getResponsibleOrg())){
								relatePst.setString(1, Identities.uuid());
								relatePst.setString(2, tempProcess.getId());
								relatePst.setString(3, organization.getId());
								relatePst.setString(4, Contents.ORG_RESPONSIBILITY);
								relatePst.setString(5, null);
								relatePst.addBatch();
								break;
							}
						}
					}
					//相关部门
					if(StringUtils.isNotBlank(tempProcess.getRelateOrg())){
						String[] relateOrgNameArray = StringUtils.split(tempProcess.getRelateOrg(), ",");
						for(String relateOrgName : relateOrgNameArray){
							for (SysOrganization organization : organizationList) {
								if(organization.getOrgname().equals(relateOrgName)){
									relatePst.setString(1, Identities.uuid());
									relatePst.setString(2, tempProcess.getId());
									relatePst.setString(3, organization.getId());
									relatePst.setString(4, Contents.ORG_PARTICIPATION);
									relatePst.setString(5, null);
									relatePst.addBatch();
									break;
								}
							}
						}
					}
					//责任人
					if(StringUtils.isNotBlank(tempProcess.getResponsibleEmp())){
						for (SysEmployee emp : employeeList) {
							if(emp.getEmpname().equals(tempProcess.getResponsibleEmp())){
								relatePst.setString(1, Identities.uuid());
								relatePst.setString(2, tempProcess.getId());
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