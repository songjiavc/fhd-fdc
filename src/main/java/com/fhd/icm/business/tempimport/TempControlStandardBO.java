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
import com.fhd.dao.icm.tempimport.TempControlStandardDAO;
import com.fhd.entity.icm.standard.Standard;
import com.fhd.entity.icm.tempimport.TempControlStandard;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.standard.StandardBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 控制标准(要求)导入临时表BO.
 * @author 吴德福
 * @Date 2013-12-11 15:25:32
 */
@Service
@SuppressWarnings({"unchecked"})
public class TempControlStandardBO {

	@Autowired
	private TempControlStandardDAO o_tempControlStandardDAO;
	@Autowired
	private StandardBO o_controlStandardBO;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * 验证控制标准(要求)信息.
	 * @author 吴德福
	 * @param fileId
	 * @return Map<String,Object>
	 */
	public Map<String,Object> validateControlStandardData(String fileId){
		Map<String,Object> controlStandardMap = new HashMap<String,Object>();
		
		//临时表list
		List<TempControlStandard> tempControlStandardList = this.findControlStandardPreviewListBySome("", fileId);
		//控制标准(要求)分类list
		List<TempControlStandard> tempControlStandardClassList = new ArrayList<TempControlStandard>();
		for (TempControlStandard tempControlStandard : tempControlStandardClassList) {
			if(StringUtils.isNotBlank(tempControlStandard.getParentControlStandardCode())){
				tempControlStandardClassList.add(tempControlStandard);
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
		//控制层级数据字典项list
		List<DictEntry> controlLevelList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_level");
		List<String> controlLevelNameList = new ArrayList<String>();
		for (DictEntry dictEntry : controlLevelList) {
			if(!controlLevelNameList.contains(dictEntry.getName())){
				controlLevelNameList.add(dictEntry.getName());
			}
		}
		//内控要素数据字典项list
		List<DictEntry> controlElementsList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_point");
		List<String> controlElementsNameList = new ArrayList<String>();
		for (DictEntry dictEntry : controlElementsList) {
			if(!controlElementsNameList.contains(dictEntry.getName())){
				controlElementsNameList.add(dictEntry.getName());
			}
		}
		
		//控制标准(要求)编号验证list
		List<String> controlStandardCodeList = new ArrayList<String>();
		//上级控制标准(要求)编号验证list
		List<String> parentControlStandardCodeList = new ArrayList<String>();
		
		List<Standard> controlStandardList = o_controlStandardBO.findControlStandardListByCompanyId(UserContext.getUser().getCompanyid());
		for (Standard controlStandard : controlStandardList) {
			if(!controlStandardCodeList.contains(controlStandard.getCode())){
				controlStandardCodeList.add(controlStandard.getCode());
			}
			if(!parentControlStandardCodeList.contains(controlStandard.getCode())){
				parentControlStandardCodeList.add(controlStandard.getCode());
			}
		}
		for (TempControlStandard tempControlStandard : tempControlStandardList) {
			if(StringUtils.isNotBlank(tempControlStandard.getParentControlStandardCode()) && !parentControlStandardCodeList.contains(tempControlStandard.getParentControlStandardCode())){
				parentControlStandardCodeList.add(tempControlStandard.getParentControlStandardCode());
			}
		}

		int errorCount = 0 ;
		//验证
		int i=3;
		for (TempControlStandard tempControlStandard : tempControlStandardList) {
			StringBuffer errorTip=new StringBuffer();
			if(tempControlStandardClassList.contains(tempControlStandard) || StringUtils.isBlank(tempControlStandard.getParentControlStandardCode())){
				//控制标准(要求)分类验证
				if(StringUtils.isNotBlank(tempControlStandard.getParentControlStandardCode()) && !parentControlStandardCodeList.contains(tempControlStandard.getParentControlStandardCode())){
					//验证控制标准(要求)编号是否正确
					errorTip.append("(").append("A").append(i).append(")").append("控制标准(要求)编号不存在!");
				}
				if(StringUtils.isBlank(tempControlStandard.getControlStandardCode())){
					errorTip.append("(").append("C").append(i).append(")").append("控制标准(要求)编号必填!");
				}else{
					//验证控制标准(要求)编号是否正确
					if(controlStandardCodeList.contains(tempControlStandard.getControlStandardCode())){
						errorTip.append("(").append("C").append(i).append(")").append("控制标准(要求)编号已存在!");
					}
				}
				if(StringUtils.isBlank(tempControlStandard.getControlStandardName())){
					errorTip.append("(").append("D").append(i).append(")").append("控制标准(要求)名称必填!");
				}
				if(StringUtils.isNotBlank(tempControlStandard.getResponsibleOrg()) && !orgNameList.contains(tempControlStandard.getResponsibleOrg())){
					//验证责任部门是否正确
					errorTip.append("(").append("E").append(i).append(")").append("控制标准(要求)责任部门不存在!");
				}
				if(StringUtils.isNotBlank(tempControlStandard.getControlLevel()) && !controlLevelNameList.contains(tempControlStandard.getControlLevel())){
					//验证内控层级是否正确
					errorTip.append("(").append("F").append(i).append(")").append("控制标准(要求)内控层级不存在!");
				}
				if(StringUtils.isNotBlank(tempControlStandard.getControlElements()) && !controlElementsNameList.contains(tempControlStandard.getControlElements())){
					//验证内控要素是否正确
					errorTip.append("(").append("G").append(i).append(")").append("控制标准(要求)内控要素不存在!");
				}
				if(StringUtils.isBlank(tempControlStandard.getIsClass())){
					errorTip.append("(").append("H").append(i).append(")").append("控制标准(要求)是否分类必填!");
				}
			}else{
				//末级控制标准(要求)验证
				if(StringUtils.isNotBlank(tempControlStandard.getParentControlStandardCode()) && !parentControlStandardCodeList.contains(tempControlStandard.getParentControlStandardCode())){
					//验证控制标准(要求)编号是否正确
					errorTip.append("(").append("A").append(i).append(")").append("控制标准(要求)编号不存在!");
				}
				if(StringUtils.isBlank(tempControlStandard.getControlStandardCode())){
					errorTip.append("(").append("C").append(i).append(")").append("控制标准(要求)编号必填!");
				}else{
					//验证控制标准(要求)编号是否正确
					if(controlStandardCodeList.contains(tempControlStandard.getControlStandardCode())){
						errorTip.append("(").append("C").append(i).append(")").append("控制标准(要求)编号已存在!");
					}
				}
				if(StringUtils.isBlank(tempControlStandard.getControlStandardName())){
					errorTip.append("(").append("D").append(i).append(")").append("控制标准(要求)名称必填!");
				}
				if(StringUtils.isBlank(tempControlStandard.getResponsibleOrg())){
					errorTip.append("(").append("E").append(i).append(")").append("控制标准(要求)责任部门必填!");
				}else{
					//验证责任部门是否正确
					if(!orgNameList.contains(tempControlStandard.getResponsibleOrg())){
						errorTip.append("(").append("E").append(i).append(")").append("控制标准(要求)责任部门不存在!");
					}
				}
				if(StringUtils.isBlank(tempControlStandard.getControlLevel())){
					errorTip.append("(").append("F").append(i).append(")").append("控制标准(要求)发生频率必填!");
				}else{
					//验证内控层级是否正确
					if(!controlLevelNameList.contains(tempControlStandard.getControlLevel())){
						errorTip.append("(").append("F").append(i).append(")").append("控制标准(要求)发生频率不存在!");
					}
				}
				if(StringUtils.isBlank(tempControlStandard.getControlElements())){
					errorTip.append("(").append("G").append(i).append(")").append("控制标准(要求)内控要素必填!");
				}else{
					//验证内控要素是否正确
					if(!controlElementsNameList.contains(tempControlStandard.getControlElements())){
						errorTip.append("(").append("G").append(i).append(")").append("控制标准(要求)内控要素不存在!");
					}
				}
				if(StringUtils.isBlank(tempControlStandard.getIsClass())){
					errorTip.append("(").append("H").append(i).append(")").append("控制标准(要求)是否分类必填!");
				}
			}
			if(errorTip.length()>0){
				errorCount++;
				tempControlStandard.setErrorTip(errorTip.toString());
				o_tempControlStandardDAO.merge(tempControlStandard);
			}
			i++;
		}
		
		controlStandardMap.put("allCount", tempControlStandardList.size());
		controlStandardMap.put("correctCount", tempControlStandardList.size()-errorCount);
		controlStandardMap.put("errorCount", errorCount);
		
		return controlStandardMap;
	}
	/**
	 * 根据类型判断导入的临时数据存入对应的临时表.
	 * @author 吴德福
	 * @param controlStandardList
	 * @param dataList
	 * @param fileUploadEntity
	 */
	@Transactional
	public void saveTempDataByDataListAndType(List<Standard> controlStandardList, List<List<String>> dataList, FileUploadEntity fileUploadEntity){
		List<TempControlStandard> tempControlStandardList = new ArrayList<TempControlStandard>();
		
		//控制标准(要求)code临时字段，用于排序
		String tempControlStandardCode = "";
		Integer sort = 1;
		
		final int START_ROW = 2;//excel开始的行号
		int i=0;
		for (int j = 0; j < dataList.size(); j++) {
			TempControlStandard tempControlStandard = new TempControlStandard();
			String id = Identities.uuid();
			tempControlStandard.setId(id);
			tempControlStandard.setIndex(String.valueOf(i+1+START_ROW));
			tempControlStandard.setParentControlStandardCode(dataList.get(j).get(0));
			tempControlStandard.setParentControlStandardName(dataList.get(j).get(1));
			tempControlStandard.setControlStandardCode(dataList.get(j).get(2));
			tempControlStandard.setControlStandardName(dataList.get(j).get(3));
			tempControlStandard.setResponsibleOrg(dataList.get(j).get(4));
			tempControlStandard.setControlLevel(dataList.get(j).get(5));
			tempControlStandard.setControlElements(dataList.get(j).get(6));
			tempControlStandard.setIsClass(dataList.get(j).get(7));
			tempControlStandard.setFileUploadEntity(fileUploadEntity);
			
			boolean isExist = false;
			if("".equals(dataList.get(j).get(0))){
				//上级控制标准(要求)编号为空
				tempControlStandard.setParentId(null);
				tempControlStandard.setIdSeq("."+id+".");
				tempControlStandard.setLevel(1);
				tempControlStandardCode = "";
				for (Standard controlStandard : controlStandardList) {
					if(null == controlStandard.getParent()){
						sort += 1;
					}
				}
				isExist = true;
			}else{
				//导入的上级控制标准(要求)编号在数据库真实表中
				for (Standard controlStandard : controlStandardList) {
					if(controlStandard.getCode().equals(dataList.get(j).get(0))){
						tempControlStandard.setParentId(controlStandard.getId());
						tempControlStandard.setIdSeq(controlStandard.getIdSeq()+id+".");
						tempControlStandard.setLevel(controlStandard.getLevel()+1);
						tempControlStandardCode = controlStandard.getCode();
						Set<Standard> controlStandardSet = controlStandard.getChildren();
						sort += controlStandardSet.size();
						if(controlStandard.getIsLeaf()){
							controlStandard.setIsLeaf(false);
							o_controlStandardBO.mergeStandard(controlStandard);
						}
						isExist = true;
						break;
					}
				}
			}
			if(!isExist){
				//导入的上级控制标准(要求)编号在excel表中
				for(TempControlStandard tcs : tempControlStandardList){
					if(tcs.getControlStandardCode().equals(dataList.get(j).get(0))){
						tempControlStandard.setParentId(tcs.getId());
						tempControlStandard.setIdSeq(tcs.getIdSeq()+id+".");
						tempControlStandard.setLevel(tcs.getLevel()+1);
						//排序
						if("".equals(tempControlStandardCode)){
							//当前记录为第一条记录
							tempControlStandardCode = dataList.get(j).get(0);
						}else if(tempControlStandardCode.equals(dataList.get(j).get(0))){
							//当前记录控制标准(要求)code与上一条记录控制标准(要求)code相同，排序+1
							sort += 1;
						}else{
							//当前记录控制标准(要求)code与上一条记录控制标准(要求)code不同，排序重置为1
							tempControlStandardCode = dataList.get(j).get(0);
							sort = 1;
						}
					}
				}
			}
			tempControlStandard.setSort(sort);
			
			tempControlStandard.setIsLeaf(true);
			tempControlStandard.setCompanyId(UserContext.getUser().getCompanyid());
			tempControlStandard.setCreateBy(UserContext.getUser().getEmpid());
			tempControlStandard.setDealStatus(Contents.DEAL_STATUS_OPERATION);
			tempControlStandard.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			tempControlStandard.setCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			//保存当前控制标准(要求)
			//o_tempControlStandardDAO.merge(tempControlStandard);
			
			//把新增的临时表数据添加表list中，给层级、主键序列、上级等使用
			if(!tempControlStandardList.contains(tempControlStandard)){
				tempControlStandardList.add(tempControlStandard);
			}
			
			i++;
		}
		//批量导入流程数据
		batchSaveTempData(tempControlStandardList);
	}
	
	/**
	 * 批量导入临时表数据
	 */
	@Transactional
	private void batchSaveTempData(final List<TempControlStandard> tempControlStandardList){
		o_tempControlStandardDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert into TEMP_IMP_CONTROL_STANDARD(id,E_INDEX,PARENT_CONTROL_STANDARD_CODE,PARENT_CONTROL_STANDARD_NAME," +
						"CONTROL_STANDARD_CODE,CONTROL_STANDARD_NAME,RESPONSIBLE_ORG,CONTROL_LEVEL,CONTROL_ELEMENTS,IS_CLASS,ESORT," +
						"IS_LEAF,COMPANY_ID,CREATE_BY,DEAL_STATUS,DELETE_STATUS,CREATE_TIME,PARENT_ID,ID_SEQ,ELEVEL) " +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (TempControlStandard o : tempControlStandardList) {
					pst.setObject(1, o.getId());
					pst.setObject(2, o.getIndex());
					pst.setObject(3, o.getParentControlStandardCode());
					pst.setObject(4, o.getParentControlStandardName());
					pst.setObject(5, o.getControlStandardCode());
					pst.setObject(6, o.getControlStandardName());
					pst.setObject(7, o.getResponsibleOrg());
					pst.setObject(8, o.getControlLevel());
					pst.setObject(9, o.getControlElements());
					pst.setObject(10, o.getIsClass());
					pst.setObject(11, o.getSort());
					pst.setObject(12, o.getIsLeaf());
					pst.setObject(13, o.getCompanyId());
					pst.setObject(14, o.getCreateBy());
					pst.setObject(15, o.getDealStatus());
					pst.setObject(16, o.getDeleteStatus());
					pst.setObject(17, o.getCreateTime());
					pst.setObject(18, o.getParentId());
					pst.setObject(19, o.getIdSeq());
					pst.setObject(20, o.getLevel());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_tempControlStandardDAO.getSession().flush();
	}
	
	/**
	 * 根据查询条件查询对应的控制标准(要求)临时表数据.
	 * @author 吴德福
	 * @param query
	 * @param fileId
	 * @return List<TempControlStandard>
	 */
	public List<TempControlStandard> findControlStandardPreviewListBySome(String query, String fileId){
		Criteria criteria = o_tempControlStandardDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.like("controlStandardCode", query, MatchMode.ANYWHERE), Restrictions.like("controlStandardName", query, MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		criteria.addOrder(Order.desc("errorTip"));
		criteria.addOrder(Order.asc("index"));
		return criteria.list();
	}
	/**
	 * 批量删除控制标准(要求)临时表.
	 * @author 吴德福
	 */
	@Transactional
	public void removeBatchTempControlStandardByHql(){
		String hql="delete from TempControlStandard";
		o_tempControlStandardDAO.createQuery(hql).executeUpdate();
	}
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final Map<String,Object> map){
		o_tempControlStandardDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				List<TempControlStandard> tempControlStandardList = (List<TempControlStandard>) map.get("tempControlStandardList");
				//按上级控制标准(要求)code、控制标准(要求)code排序
				Collections.sort(tempControlStandardList, new Comparator<TempControlStandard>(){
					@Override
					public int compare(TempControlStandard o1, TempControlStandard o2) {
						String a1 = o1.getControlStandardCode();
						String a2 = o2.getControlStandardCode();
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
				//内控层级数据字典项list
				List<DictEntry> frequencyList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_level");
				//内控要素数据字典项list
				List<DictEntry> affectSubjectsList = o_dictBO.findDictEntryByDictTypeIdAndEStatus("ic_control_point");
				
				//控制标准(要求)sql--15个字段
				String basicSql = "insert into t_ic_control_standard(id,standard_code,standard_name,parent_id,id_seq,elevel,esort,is_leaf,deal_status,delete_status,create_time,create_by,company_id,control_level,control_point,etype) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//控制标准(要求)部门人员sql--5个字段
				String relateSql = "insert into t_ic_standard_rela_org(id,control_standard_id,org_id,etype) values(?,?,?,?)";
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement relatePst = connection.prepareStatement(relateSql);
				
				for (TempControlStandard tempControlStandard : tempControlStandardList) {
					/*
					 * 控制标准(要求)信息
					 */
					basicPst.setString(1, tempControlStandard.getId());
					basicPst.setString(2, tempControlStandard.getControlStandardCode());
					basicPst.setString(3, tempControlStandard.getControlStandardName());
					basicPst.setString(4, tempControlStandard.getParentId());
					basicPst.setString(5, tempControlStandard.getIdSeq());
					basicPst.setInt(6, tempControlStandard.getLevel());
					basicPst.setInt(7, tempControlStandard.getSort());
					basicPst.setBoolean(8, tempControlStandard.getIsLeaf());
					basicPst.setString(9, tempControlStandard.getDealStatus());
					basicPst.setString(10, tempControlStandard.getDeleteStatus());
					basicPst.setString(11, tempControlStandard.getCreateTime());
					basicPst.setString(12, tempControlStandard.getCreateBy());
					basicPst.setString(13, tempControlStandard.getCompanyId());
					if(StringUtils.isNotBlank(tempControlStandard.getControlLevel())){
						//内控层级转换数据字典id
						for (DictEntry dictEntry : frequencyList) {
							if(tempControlStandard.getControlLevel().equals(dictEntry.getName())){
								basicPst.setString(14, dictEntry.getId());
								break;
							}
						}
					}else{
						basicPst.setString(14, null);
					}
					if(StringUtils.isNotBlank(tempControlStandard.getControlElements())){
						//内控要素转换数据字典id
						for (DictEntry dictEntry : affectSubjectsList) {
							if(tempControlStandard.getControlElements().equals(dictEntry.getName())){
								basicPst.setString(15, dictEntry.getId());
								break;
							}
						}
					}else{
						basicPst.setString(15, null);
					}
					String isClass = "1";
					if(StringUtils.isNotBlank(tempControlStandard.getIsClass()) && "否".equals(tempControlStandard.getIsClass())){
						isClass = "0";
					}
					basicPst.setString(16, isClass);
					basicPst.addBatch();
					
					//控制标准(要求)责任部门
					if(StringUtils.isNotBlank(tempControlStandard.getResponsibleOrg())){
						for (SysOrganization organization : organizationList) {
							if(organization.getOrgname().equals(tempControlStandard.getResponsibleOrg())){
								relatePst.setString(1, Identities.uuid());
								relatePst.setString(2, tempControlStandard.getId());
								relatePst.setString(3, organization.getId());
								relatePst.setString(4, Contents.ORG_RESPONSIBILITY);
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
