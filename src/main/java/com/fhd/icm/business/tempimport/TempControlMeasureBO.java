package com.fhd.icm.business.tempimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.tempimport.TempControlMeasureDAO;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.icm.tempimport.TempControlMeasure;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.assess.MeasureBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

/**
 * 控制措施导入临时表BO.
 * @author 吴德福
 * @Date 2013-12-12 13:40:32
 */
@Service
@SuppressWarnings("unchecked")
public class TempControlMeasureBO {

	@Autowired
	private TempControlMeasureDAO o_tempControlMeasureDAO;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private MeasureBO o_measureBO;
	
	/**
	 * 验证控制措施信息.
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateControlMeasureData(String fileId){
		Map<String,Object> processPointMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempControlMeasure> tempControlMeasureList = this.findControlMeasurePreviewListBySome("", fileId);
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
		//控制方式数据字典项list
		List<DictEntry> controlModeList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_measure");
		List<String> controlModeNameList = new ArrayList<String>();
		for (DictEntry dictEntry : controlModeList) {
			if(!controlModeNameList.contains(dictEntry.getName())){
				controlModeNameList.add(dictEntry.getName());
			}
		}
		//控制频率数据字典项list
		List<DictEntry> controlFrequencyList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_frequency");
		List<String> controlFrequencyNameList = new ArrayList<String>();
		for (DictEntry dictEntry : controlFrequencyList) {
			if(!controlFrequencyNameList.contains(dictEntry.getName())){
				controlFrequencyNameList.add(dictEntry.getName());
			}
		}
		//控制措施list
		List<Measure> measureList = o_measureBO.findMeasureListByCompanyId(UserContext.getUser().getCompanyid());
		List<String> controlMeasureCodeList = new ArrayList<String>();
		for (Measure measure : measureList) {
			if(!controlMeasureCodeList.contains(measure.getCode())){
				controlMeasureCodeList.add(measure.getCode());
			}
		}

		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempControlMeasure tempControlMeasure : tempControlMeasureList) {
			StringBuffer errorTip=new StringBuffer();
			
			if(StringUtils.isBlank(tempControlMeasure.getControlMeasureCode())){
				errorTip.append("(").append("A").append(i).append(")").append("控制措施编号必填!");
			}else{
				//验证控制措施编号是否正确
				if(controlMeasureCodeList.contains(tempControlMeasure.getControlMeasureCode())){
					errorTip.append("(").append("A").append(i).append(")").append("控制措施编号已存在!");
				}
			}
			if(StringUtils.isBlank(tempControlMeasure.getControlMeasureName())){
				errorTip.append("(").append("B").append(i).append(")").append("控制措施名称必填!");
			}
			if(StringUtils.isBlank(tempControlMeasure.getResponsibleOrg())){
				errorTip.append("(").append("C").append(i).append(")").append("控制措施责任部门必填!");
			}else{
				//验证责任部门是否正确
				if(!orgNameList.contains(tempControlMeasure.getResponsibleOrg())){
					errorTip.append("(").append("C").append(i).append(")").append("控制措施责任部门不存在!");
				}
			}
			if(StringUtils.isBlank(tempControlMeasure.getResponsibleEmp())){
				errorTip.append("(").append("D").append(i).append(")").append("控制措施责任人必填!");
			}else{
				//验证责任人是否正确
				if(!empNameList.contains(tempControlMeasure.getResponsibleEmp())){
					errorTip.append("(").append("D").append(i).append(")").append("控制措施责任人不存在!");
				}
			}
			if(StringUtils.isBlank(tempControlMeasure.getIsKeyControlPoint())){
				errorTip.append("(").append("E").append(i).append(")").append("控制措施是否关键控制点必填!");
			}
			if(StringUtils.isNotBlank(tempControlMeasure.getControlMode()) && !controlModeNameList.contains(tempControlMeasure.getControlMode())){
				//验证控制方式是否正确
				errorTip.append("(").append("H").append(i).append(")").append("控制措施控制方式不存在!");
			}
			if(StringUtils.isBlank(tempControlMeasure.getControlFrequency())){
				errorTip.append("(").append("I").append(i).append(")").append("控制措施责任部门必填!");
			}else{
				//验证控制频率是否正确
				if(!controlFrequencyNameList.contains(tempControlMeasure.getControlFrequency())){
					errorTip.append("(").append("I").append(i).append(")").append("控制措施控制频率不存在!");
				}
			}
			if(errorTip.length()>0){
				errorCount++;
				tempControlMeasure.setErrorTip(errorTip.toString());
				o_tempControlMeasureDAO.merge(tempControlMeasure);
			}
			i++;
		}
		
		processPointMap.put("allCount", tempControlMeasureList.size());
		processPointMap.put("correctCount", tempControlMeasureList.size()-errorCount);
		processPointMap.put("errorCount", errorCount);
		
		return processPointMap;
	}
	/**
	 * excel表数据存入控制措施临时表.
	 * @author 吴德福
	 * @param dataListList
	 * @param fileUploadEntity
	 */
	@Transactional
	public void saveTempControlMeasure(List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		List<TempControlMeasure> tempControlMeasureList = new ArrayList<TempControlMeasure>();
		//排序
		Integer sort = 1;
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempControlMeasure tempControlMeasure = new TempControlMeasure();
			tempControlMeasure.setId(Identities.uuid());
			tempControlMeasure.setIndex(String.valueOf(i+1+START_ROW));
			tempControlMeasure.setControlMeasureCode(dataList.get(j).get(0));
			tempControlMeasure.setControlMeasureName(dataList.get(j).get(1));
			tempControlMeasure.setResponsibleOrg(dataList.get(j).get(2));
			tempControlMeasure.setResponsibleEmp(dataList.get(j).get(3));
			tempControlMeasure.setIsKeyControlPoint(dataList.get(j).get(4));
			tempControlMeasure.setControlTarget(dataList.get(j).get(5));
			if(StringUtils.isNotBlank(dataList.get(j).get(6))){
				tempControlMeasure.setImplementProof(dataList.get(j).get(6));
			}else{
				tempControlMeasure.setImplementProof(null);
			}
			tempControlMeasure.setControlMode(dataList.get(j).get(7));
			tempControlMeasure.setControlFrequency(dataList.get(j).get(8));
			tempControlMeasure.setFileUploadEntity(fileUploadEntity);
			tempControlMeasure.setSort(sort);
			
			//删除状态:默认值为--1
			tempControlMeasure.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			//状态:默认值为--0yn_y(数据字典0yn)
			tempControlMeasure.setStatus("0yn_y");
			tempControlMeasure.setCompanyId(UserContext.getUser().getCompanyid());
			tempControlMeasure.setCreateBy(UserContext.getUser().getEmpid());
			tempControlMeasure.setCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			//o_tempControlMeasureDAO.merge(tempControlMeasure);
			tempControlMeasureList.add(tempControlMeasure);
			i++;
		}
		//批量导入临时表数据
		batchSaveTempData(tempControlMeasureList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempControlMeasure> tempControlMeasureList){
		o_tempControlMeasureDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_CONTROL_MEASURE(id,E_INDEX,CONTROL_MEASURE_CODE,CONTROL_MEASURE_NAME," +
						"RESPONSIBLE_ORG,RESPONSIBLE_EMP,IS_KEY_CONTROL_POINT,CONTROL_TARGET,IMPLEMENT_PROOF,CONTROL_MODE," +
						"CONTROL_FREQUENCY,ESORT,DELETE_STATUS,ESTATUS,COMPANY_ID,CREATE_BY,CREATE_TIME) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempControlMeasure o : tempControlMeasureList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getControlMeasureCode());
					pst.setObject(4, o.getControlMeasureName());
					pst.setObject(5, o.getResponsibleOrg());
					pst.setObject(6, o.getResponsibleEmp());
					pst.setObject(7, o.getIsKeyControlPoint());
					pst.setObject(8, o.getControlTarget());
					pst.setObject(9, o.getImplementProof());
					pst.setObject(10, o.getControlMode());
					pst.setObject(11, o.getControlFrequency());
					pst.setObject(12, o.getSort());
					pst.setObject(13, o.getDeleteStatus());
					pst.setObject(14, o.getStatus());
					pst.setObject(15, o.getCompanyId());
					pst.setObject(16, o.getCreateBy());
					pst.setObject(17, o.getCreateTime());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempControlMeasureDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的控制措施临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempControlMeasure>
	 */
	public List<TempControlMeasure> findControlMeasurePreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempControlMeasureDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.like("controlMeasureCode", query, MatchMode.ANYWHERE), Restrictions.like("controlMeasureName", query, MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		criteria.addOrder(Order.desc("errorTip"));
		criteria.addOrder(Order.asc("index"));
		return criteria.list();
	}
	/**
	 * 批量删除控制措施临时表.
	 * @author 吴德福
	 */
	@Transactional
	public void removeBatchTempControlMeasureByHql(){
		String hql="delete from TempControlMeasure";
		o_tempControlMeasureDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempControlMeasureDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempControlMeasure> tempControlMeasureList = (List<TempControlMeasure>) map.get("tempControlMeasureList");
				/*
				 * 转换信息list
				 */
				//部门list
				List<SysOrganization> organizationList = o_organizationBO.findByCompanyId(UserContext.getUser().getCompanyid());
				//员工list
				List<SysEmployee> employeeList = o_employeeBO.findByCompanyId(UserContext.getUser().getCompanyid());
				//是否关键控制点数据字典项list
				List<DictEntry> isKeyControlPointList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("0yn");
				//控制频率数据字典项list
				List<DictEntry> controlFrequencyList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_frequency");
				//控制方式数据字典项list
				List<DictEntry> controlModeList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_measure");
				
				//控制措施sql--14个字段
				String basicSql = "insert into t_con_control_measure(id,measure_code,measure_name,is_key_control_point,control_target,implement_proof,control_measure,control_frequency,esort,estatus,delete_estatus,create_time,create_by,company_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//控制措施部门人员sql--5个字段
				String relateSql = "insert into t_con_measure_rela_org(id,control_measure_id,org_id,etype,emp_id) values(?,?,?,?,?)";
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement relatePst = connection.prepareStatement(relateSql);
				
				for (TempControlMeasure tempControlMeasure : tempControlMeasureList) {
					/*
					 * 控制措施信息
					 */
					basicPst.setString(1, tempControlMeasure.getId());
					basicPst.setString(2, tempControlMeasure.getControlMeasureCode());
					basicPst.setString(3, tempControlMeasure.getControlMeasureName());
					//是否关键控制点转换数据字典id
					if(StringUtils.isNotBlank(tempControlMeasure.getIsKeyControlPoint())){
						for (DictEntry dictEntry : isKeyControlPointList) {
							if(tempControlMeasure.getIsKeyControlPoint().equals(dictEntry.getName())){
								basicPst.setString(4, dictEntry.getId());
								break;
							}
						}
					}else{
						basicPst.setString(4, null);
					}
					basicPst.setString(5, tempControlMeasure.getControlTarget());
					basicPst.setString(6, tempControlMeasure.getImplementProof());
					//控制方式转换数据字典id
					if(StringUtils.isNotBlank(tempControlMeasure.getControlMode())){
						for (DictEntry dictEntry : controlModeList) {
							if(tempControlMeasure.getControlMode().equals(dictEntry.getName())){
								basicPst.setString(7, dictEntry.getId());
								break;
							}
						}
					}else{
						basicPst.setString(7, null);
					}
					//控制频率转换数据字典id
					if(StringUtils.isNotBlank(tempControlMeasure.getControlFrequency())){
						for (DictEntry dictEntry : controlFrequencyList) {
							if(tempControlMeasure.getControlFrequency().equals(dictEntry.getName())){
								basicPst.setString(8, dictEntry.getId());
								break;
							}
						}
					}else{
						basicPst.setString(8, null);
					}
					basicPst.setInt(9, tempControlMeasure.getSort());
					basicPst.setString(10, tempControlMeasure.getStatus());
					basicPst.setString(11, tempControlMeasure.getDeleteStatus());
					basicPst.setString(12, tempControlMeasure.getCreateTime());
					basicPst.setString(13, tempControlMeasure.getCreateBy());
					basicPst.setString(14, tempControlMeasure.getCompanyId());
					basicPst.addBatch();
					
					/*
					 * 控制措施部门/人员
					 */
					//责任部门
					if(StringUtils.isNotBlank(tempControlMeasure.getResponsibleOrg())){
						for (SysOrganization organization : organizationList) {
							if(organization.getOrgname().equals(tempControlMeasure.getResponsibleOrg())){
								relatePst.setString(1, Identities.uuid());
								relatePst.setString(2, tempControlMeasure.getId());
								relatePst.setString(3, organization.getId());
								relatePst.setString(4, Contents.ORG_RESPONSIBILITY);
								relatePst.setString(5, null);
								relatePst.addBatch();
								break;
							}
						}
					}
					//责任人
					if(StringUtils.isNotBlank(tempControlMeasure.getResponsibleEmp())){
						for (SysEmployee emp : employeeList) {
							if(emp.getEmpname().equals(tempControlMeasure.getResponsibleEmp())){
								relatePst.setString(1, Identities.uuid());
								relatePst.setString(2, tempControlMeasure.getId());
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
