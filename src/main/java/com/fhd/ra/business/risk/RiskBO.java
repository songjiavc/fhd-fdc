/**
 * RiskBO.java
 * com.fhd.risk.business
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/
/**
 * RiskBO.java
 * com.fhd.risk.business
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-11-19        金鹏祥
 *
 * Copyright (c) 2012, FirstHuida All Rights Reserved.
*/


package com.fhd.ra.business.risk;

import static org.hibernate.criterion.Restrictions.in;
import static org.hibernate.criterion.Restrictions.like;
import static org.hibernate.criterion.Restrictions.ne;
import static org.hibernate.criterion.Restrictions.not;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.dao.risk.KpiAdjustHistoryDAO;
import com.fhd.dao.risk.OrgAdjustHistoryDAO;
import com.fhd.dao.risk.ProcessAdjustHistoryDAO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskRelaRiskDAO;
import com.fhd.dao.risk.StrategyAdjustHistoryDAO;
import com.fhd.dao.sys.dataimport.RiskFromExcelDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.OrgAdjustHistory;
import com.fhd.entity.risk.ProcessAdjustHistory;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.RiskRelaRisk;
import com.fhd.entity.risk.StrategyAdjustHistory;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.dic.DictEntryRelation;
import com.fhd.entity.sys.dic.DictEntryRelationType;
import com.fhd.entity.sys.entity.RiskFromExcel;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.MapListUtil;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.ra.interfaces.risk.IKpiRelaRiskBO;
import com.fhd.ra.interfaces.risk.IRiskBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.dic.DictEntryRelationBO;
import com.fhd.sys.business.organization.OrgGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 风险操作业务处理
 *
 * @author   金鹏祥
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-19		上午09:27:46
 *
 * @see 	 
 */
@Service
public class RiskBO implements IRiskBO{
	
	public final static String riskKindDict = "1";		//风险类别
	public final static String relePlateDict = "2";		//设计板块
	public final static String riskTypeDict = "3";		//风险类型
	public final static String innerReasonDict = "4";	//内部动因
	public final static String outterReasonDict = "5";	//外部动因
	public final static String valueChainsDict = "8";	//风险价值链
	public final static String impactTimeDict = "9";    //影响期间
	public final static String responseStrategyDict = "10";	//应对策略
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private RiskRelaRiskDAO o_riskRelaRiskDAO;
	
	@Autowired
	private RiskOrgBO o_riskOrgBO;
	
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	
	//流程
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private ProcessRelaRiskBO o_processRelaRiskBO;
	
	//字典
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private DictEntryRelationBO o_dictEntryRelationBO;
	
	/**
	 * 历史记录
	 */
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	@Autowired
	private OrgAdjustHistoryDAO o_orgAdjustHistoryDAO;
	@Autowired
	private ProcessAdjustHistoryDAO o_processAdjustHistoryDAO;
	@Autowired
	private StrategyAdjustHistoryDAO o_strategyAdjustHistoryDAO;
	@Autowired
	private KpiAdjustHistoryDAO o_kpiAdjustHistoryDAO;
	
	@Autowired
    private OrgGridBO o_orgGridBO;
	
	/**
	 * 指标相关
	 */
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private IKpiRelaRiskBO o_kpiRelaRiskBO;	
	
	@Autowired
	private RiskFromExcelDAO o_riskFromExcelDAO;
	
	public Risk findRiskByCompanyId(String id) {
		Criteria criteria = o_riskDAO.createCriteria();
		if (StringUtils.isNotBlank(id)) {
			criteria.add(Restrictions.eq("company.id", id));
		}
		criteria.add(Restrictions.isNull("parent"));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		return (Risk) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Risk> findRiskBySome(String companyId, String deleteStatus) {
		Criteria criteria = o_riskDAO.createCriteria();
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Risk> findRiskBySome(String companyId,
			String parentId, String deleteStatus, Boolean rbs) {
		Criteria criteria = o_riskDAO.createCriteria();
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		if (StringUtils.isNotBlank(parentId)) {
			if(parentId.equalsIgnoreCase("root")){
				criteria.add(Restrictions.isNull("parent.id"));
			}else{
				criteria.add(Restrictions.eq("parent.id", parentId));
			}
			
		}
		criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
		
		if(rbs){
			criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		}
		//不做left join
		criteria.setFetchMode("createBy", FetchMode.SELECT);
		criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Risk> findRiskMapByCompanyId(String companyId) {
		List<Risk> riskList = null;
		HashMap<String, Risk> riskMap = new HashMap<String, Risk>();

		Criteria criteria = o_riskDAO.createCriteria();
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		
		//不做left join
		criteria.setFetchMode("createBy", FetchMode.SELECT);
		criteria.setFetchMode("lastModifyBy", FetchMode.SELECT);
		
		riskList = criteria.list();

		for (Risk risk : riskList) {
			if (risk.getParent() != null) {
				riskMap.put(risk.getParent().getId(), risk);
			}
		}

		return riskMap;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Risk> findRiskMapFromNotRiskOrgByCompanyId(String companyId) {
		List<Risk> riskList = null;
		HashMap<String, Risk> riskMap = new HashMap<String, Risk>();
		Set<String> otheridSet = o_riskOrgBO.findRiskOrg();
		Criteria criteria = o_riskDAO.createCriteria();
		if (otheridSet.size() > 0) {
			criteria.add(Restrictions.not(Restrictions.in("id", otheridSet)));
		}
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", "1"));

		riskList = criteria.list();

		for (Risk risk : riskList) {
			if (risk.getParent() != null) {
				riskMap.put(risk.getParent().getId(), risk);
			}
		}

		return riskMap;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> findRiskMapFromNotRiskOrgBySome(String id, String query) {
		Set<String> idSet = new HashSet<String>();
		Set<String> otheridSet = o_riskOrgBO.findRiskOrg();
		Criteria criteria = this.o_riskDAO.createCriteria();
		List<Risk> strategyList = null;
		
		
		criteria.add(not(in("id", otheridSet)));
		if (StringUtils.isNotBlank(query)) {
			criteria.add(like("name", query, MatchMode.ANYWHERE)).add(
					ne("deleteStatus", "0"));
		}
		strategyList = criteria.list();
		for (Risk entity : strategyList) {
			String[] idsTemp = entity.getIdSeq().split("\\.");
			idSet.addAll(Arrays.asList(idsTemp));
		}
		
		return idSet;
	}
	
	@SuppressWarnings("unchecked")
	public List<Risk> findNotInOrgStrategyMap(String id, String deleteStatus, Boolean rbs) {
		Set<String> otheridSet =o_riskOrgBO.findRiskOrg();
		Criteria criteria = o_riskDAO.createCriteria();
		
		if (otheridSet.size() > 0) {
			criteria.add(Restrictions.not(Restrictions.in("id", otheridSet)));
		}
		
		criteria.add(Restrictions.eq("parent.id", id));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		
		if(rbs){
			criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		}
		
		List<Risk> riskList = criteria.list();
		
		return riskList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Risk> finRiskBySome(String searchName, String companyId, String deleteStatus){
		Criteria criteria = o_riskDAO.createCriteria();
		
		if (StringUtils.isNotBlank(searchName)) {
			criteria.add(Restrictions.like("name", searchName, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("company.id", companyId));
		List<Risk> riskList = criteria.list();
		
		return riskList;
	}
	
	public Risk findRiskParentIsNull(){
		return (Risk)o_riskDAO.createCriteria().add(Restrictions.isNull("parent")).uniqueResult();
	}

	public List<Risk> findRiskByIdAndCompanyIdAndDeleteStatus(String id,
			String companyId, boolean deleteStatus) {
		List<Risk> list = null;
		List<Risk> tempList = new ArrayList<Risk>();
		
		if(deleteStatus){
			list = this.findRiskBySome(companyId, "1");
		}else{
			list = this.findRiskBySome(companyId, "0");
		}
		
		if(StringUtils.isNotBlank(id)){
			for (Risk risk : list) {
				if(StringUtils.isNotBlank(risk.getIdSeq()))
				if(risk.getIdSeq().contains(id)){
					if(!risk.getId().equalsIgnoreCase(id)){
						tempList.add(risk);
					}
				}
			}
		}else{
			return list;
		}
		
		return tempList;
	}

	
	/**
	 * 根据公司id查询机构
	 * @author zhengjunxiang
	 * @param companyId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public SysOrganization findOrganizationByOrgId(String companyId) {
		Criteria c = o_sysOrgDAO.createCriteria();
		List<SysOrganization> list = null;
		if (StringUtils.isNotBlank(companyId)) {
			c.add(Restrictions.eq("id", companyId));
		} else {
			return null;
		}
		list = c.list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public SysEmployee findEmployeeById(String employeeId) {
		Criteria c = o_sysEmployeeDAO.createCriteria();
		List<SysEmployee> list = null;
		if (StringUtils.isNotBlank(employeeId)) {
			c.add(Restrictions.eq("id", employeeId));
		} else {
			return null;
		}
		list = c.list();
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Transactional
	public void saveRisk(Risk risk){
		o_riskDAO.merge(risk);
	}
	
	@Transactional
	public void addRisk(Risk risk,String respDeptName,String relaDeptName,
			String respPositionName, String relaPositionName, String riskKind,
			String relePlate, String riskKpiName, String influKpiName,
			String controlProcessureName, String influProcessureName,
			String innerReason, String outterReason,
			String riskReason, String riskInfluence,
			String valueChains,String riskType,
			String impactTime,String responseStrategy){
		//创建时间和最后修改时间
		risk.setCreateTime(new Date());
		risk.setLastModifyTime(new Date());
		o_riskDAO.merge(risk);
		
		//添加责任部门/人和相关部门/人
		JSONObject object = new JSONObject();
		RiskOrg riskOrg = null;
		if(null!=respDeptName && !"".equals(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				object = (JSONObject)respDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("M");
				//保存责任部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存责任人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		if(null!=relaDeptName && !"".equals(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				object = (JSONObject)relaDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("A");
				//保存相关部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存相关人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		
		//添加风险指标和影响指标
		KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
		if(null!=riskKpiName && !"".equals(riskKpiName)){
			String[] riskKpiArray = riskKpiName.split(",");
			for(int i=0;i<riskKpiArray.length;i++){
				String id = riskKpiArray[i];
				//保存风险指标
				Kpi kpi = o_kpiBO.findKpiById(id);
				kpiRelaRisk = new KpiRelaRisk();
				kpiRelaRisk.setId(UUID.randomUUID().toString());
				kpiRelaRisk.setRisk(risk);
				kpiRelaRisk.setType("RM");
				kpiRelaRisk.setKpi(kpi);
				o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
			}
		}
		if(null!=influKpiName && !"".equals(influKpiName)){
			String[] influKpiArray = influKpiName.split(",");
			for(int i=0;i<influKpiArray.length;i++){
				String id = influKpiArray[i];
				//保存影响指标
				Kpi kpi = o_kpiBO.findKpiById(id);
				kpiRelaRisk = new KpiRelaRisk();
				kpiRelaRisk.setId(UUID.randomUUID().toString());
				kpiRelaRisk.setRisk(risk);
				kpiRelaRisk.setType("I");
				kpiRelaRisk.setKpi(kpi);
				o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
			}
		}
		
		//添加控制流程和影响流程
		ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
		if(null!=controlProcessureName && !"".equals(controlProcessureName)){
			String[] processArray = controlProcessureName.split(",");
			for(int i=0;i<processArray.length;i++){
				String id = processArray[i];
				//保存控制流程
				Process process = o_processBO.findProcessById(id);
				processRelaRisk = new ProcessRelaRisk();
				processRelaRisk.setId(UUID.randomUUID().toString());
				processRelaRisk.setRisk(risk);
				processRelaRisk.setType("C");
				processRelaRisk.setProcess(process);
				o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
			}
		}
		if(null!=influProcessureName && !"".equals(influProcessureName)){
			String[] processArray = influProcessureName.split(",");
			for(int i=0;i<processArray.length;i++){
				String id = processArray[i];
				//保存影响流程
				Process process = o_processBO.findProcessById(id);
				processRelaRisk = new ProcessRelaRisk();
				processRelaRisk.setId(UUID.randomUUID().toString());
				processRelaRisk.setRisk(risk);
				processRelaRisk.setType("I");
				processRelaRisk.setProcess(process);
				o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
			}
		}
		
		//添加风险类别和涉及版块
		DictEntryRelation dictRelation = new DictEntryRelation();
		if(null!=riskKind && !"".equals(riskKind)){
			String[] dictArray = riskKind.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类别
				DictEntry dictEntry = o_dictBO.findDictEntryById(id);
				DictEntryRelationType relationType = o_dictEntryRelationBO.findDictEntryRelationTypeByTypeId("1");	//1代表风险类别
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		if(null!=relePlate && !"".equals(relePlate)){
			String[] dictArray = relePlate.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存涉及版块
				DictEntry dictEntry = o_dictBO.findDictEntryById(id);
				DictEntryRelationType relationType = o_dictEntryRelationBO.findDictEntryRelationTypeByTypeId("2");	//1代表涉及版块
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加风险类型
		if(null!= riskType && !"".equals(riskType)){
			String[] dictArray = riskType.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类别
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskBO.riskTypeDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加内部动因和外部动因
		if(null!= innerReason && !"".equals(innerReason)){
			String[] dictArray = innerReason.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存内部动因
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskBO.innerReasonDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		if(null!= outterReason && !"".equals(outterReason)){
			String[] dictArray = outterReason.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存内部动因
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskBO.outterReasonDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加风险动因和风险影响
		RiskRelaRisk riskRelaRisk = new RiskRelaRisk();
		if(null!=riskReason && !"".equals(riskReason)){
			JSONArray riskReasonArr = JSONArray.fromObject(riskReason);
			for(int i=0;i<riskReasonArr.size();i++){
				object = (JSONObject)riskReasonArr.get(i);
				//保存责任部门/责任人
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("R");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
		if(null!=riskInfluence && !"".equals(riskInfluence)){
			JSONArray riskInfluenceArr = JSONArray.fromObject(riskInfluence);
			for(int i=0;i<riskInfluenceArr.size();i++){
				object = (JSONObject)riskInfluenceArr.get(i);
				//保存责任部门/责任人
				Risk relaRisk = new Risk();
				relaRisk.setId(object.getString("id"));
				riskRelaRisk = new RiskRelaRisk();
				riskRelaRisk.setId(UUID.randomUUID().toString());
				riskRelaRisk.setRisk(risk);
				riskRelaRisk.setType("I");
				riskRelaRisk.setRelaRisk(relaRisk);
				o_riskRelaRiskDAO.merge(riskRelaRisk);
			}
		}
		
		//添加风险价值链
		if(null!= valueChains && !"".equals(valueChains)){
			String[] dictArray = valueChains.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存内部动因
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskBO.valueChainsDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加影响期间
		if(null!= impactTime && !"".equals(impactTime)){
			String[] dictArray = impactTime.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类别
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskBO.impactTimeDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		
		//添加应对策略
		if(null!= responseStrategy && !"".equals(responseStrategy)){
			String[] dictArray = responseStrategy.split(",");
			for(int i=0;i<dictArray.length;i++){
				String id = dictArray[i];
				//保存风险类别
				DictEntry dictEntry = new DictEntry();
				dictEntry.setId(id);
				DictEntryRelationType relationType = new DictEntryRelationType();
				relationType.setId(RiskBO.responseStrategyDict);
				dictRelation = new DictEntryRelation();
				dictRelation.setId(UUID.randomUUID().toString());
				dictRelation.setBusinessId(risk.getId());	//业务id
				dictRelation.setRelationType(relationType);
				dictRelation.setDictEntry(dictEntry);
				o_dictEntryRelationBO.saveDictEntryRelation(dictRelation);
			}
		}
		
		//更新上级风险，修改叶子节点
		if(risk.getIsRiskClass().equalsIgnoreCase("rbs")){	//添加风险事件不修改叶子节点状态
			Risk parent = risk.getParent();
			if(null!=parent){
				parent.setIsLeaf(false);
				saveRisk(parent);
			}
		}
	}
	
	@Transactional
	public void updateRisk(Risk risk, String respDeptName, String relaDeptName,
			String influKpiName, String influProcessureName){
		//删除责任部门/人和相关部门/人
		Set<RiskOrg> orgs = risk.getRiskOrgs();
		for(RiskOrg org : orgs){
			if(org.getType().equals("M") || org.getType().equals("A")){
				o_riskOrgBO.removeRiskOrgById(org);
			}
		}
		
		//删除影响指标
		Set<KpiRelaRisk> kpis = risk.getKpiRelaRisks();
		for(KpiRelaRisk kpi : kpis){
			if(kpi.getType().equals("I")){
				o_kpiRelaRiskBO.removeKpiRelaRiskById(kpi.getId());
			}
		}
		
		//删除影响流程
		Set<ProcessRelaRisk> processes = risk.getRiskProcessures();
		for(ProcessRelaRisk process : processes){
			if(process.getType().equals("I")){
				o_processRelaRiskBO.removeProcessRelaRiskById(process.getId());
			}
		}
		//最后修改时间
		risk.setLastModifyTime(new Date());
		o_riskDAO.merge(risk);
		
		//添加责任部门/人和相关部门/人
		JSONObject object = new JSONObject();
		RiskOrg riskOrg = new RiskOrg();
		if(null!=respDeptName && !"".equals(respDeptName)){
			JSONArray respDeptArray = JSONArray.fromObject(respDeptName);
			for(int i=0;i<respDeptArray.size();i++){
				object = (JSONObject)respDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("M");
				//保存责任部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存责任人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		if(null!=relaDeptName && !"".equals(relaDeptName)){
			JSONArray relaDeptArray = JSONArray.fromObject(relaDeptName);
			for(int i=0;i<relaDeptArray.size();i++){
				object = (JSONObject)relaDeptArray.get(i);
				riskOrg = new RiskOrg();
				riskOrg.setId(UUID.randomUUID().toString());
				riskOrg.setRisk(risk);
				riskOrg.setType("A");
				//保存相关部门
				String deptidStr = object.get("deptid").toString();
				if(!deptidStr.equals("") && !deptidStr.equals("null")){	//空保存出错，查询的时候报错
					SysOrganization org = new SysOrganization();
					org.setId(deptidStr);
					riskOrg.setSysOrganization(org);
				}
				//保存相关人
				String empidStr = object.get("empid").toString();
				if(!empidStr.equals("") && !empidStr.equals("null")){
					SysEmployee emp = new SysEmployee();
					emp.setId(empidStr);
					riskOrg.setEmp(emp);
				}
				o_riskOrgBO.saveRiskOrg(riskOrg);
			}
		}
		
		//添加影响指标
		KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
		if(null!=influKpiName && !"".equals(influKpiName)){
			String[] influKpiArray = influKpiName.split(",");
			for(int i=0;i<influKpiArray.length;i++){
				String id = influKpiArray[i];
				//保存影响指标
				Kpi kpi = o_kpiBO.findKpiById(id);
				kpiRelaRisk = new KpiRelaRisk();
				kpiRelaRisk.setId(UUID.randomUUID().toString());
				kpiRelaRisk.setRisk(risk);
				kpiRelaRisk.setType("I");
				kpiRelaRisk.setKpi(kpi);
				o_kpiRelaRiskBO.saveKpiRelaRisk(kpiRelaRisk);
			}
		}

		//添加影响流程
		ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
		if(null!=influProcessureName && !"".equals(influProcessureName)){
			String[] processArray = influProcessureName.split(",");
			for(int i=0;i<processArray.length;i++){
				String id = processArray[i];
				//保存影响流程
				Process process = o_processBO.findProcessById(id);
				processRelaRisk = new ProcessRelaRisk();
				processRelaRisk.setId(UUID.randomUUID().toString());
				processRelaRisk.setRisk(risk);
				processRelaRisk.setType("I");
				processRelaRisk.setProcess(process);
				o_processRelaRiskBO.saveProcessRelaRisk(processRelaRisk);
			}
		}
		
	}
	
	@Transactional
	public void removeRiskByIds(String ids){
		String[] idArray = ids.split(",");
		for (String id : idArray) {
			Risk risk = findRiskById(id);
			risk.setDeleteStatus("0");
			o_riskDAO.merge(risk);
			//removeRiskBySome(risk, risk.getChildren());
			
			//如果上级没有孩子，修改is_leaf属性
			Risk parent = risk.getParent();
			Criteria c = o_riskDAO.createCriteria();
			if(parent == null){
				c.add(Restrictions.isNull("parent.id"));
			}else{
				c.add(Restrictions.eq("parent.id", parent.getId()));
			}
			c.add(Restrictions.eq("deleteStatus", "1"));
			c.setProjection(Projections.rowCount());
			Long size = (Long)c.uniqueResult();
			if(size==0){	//risk是没删除前缓存的状态
				parent.setIsLeaf(true);
				o_riskDAO.merge(parent);
			}
		}
	}
	/**
	 * 从数据库删除选中风险及下级风险,丢弃
	 */
	 public void removeRiskBySome(Risk risk, Set<Risk> children) {
		if (children != null) {
		    for (Risk r: children) {
				if (r.getChildren() != null	&& r.getChildren().size() > 0) {
					removeRiskBySome(r, r.getChildren());
				} else {
					removeRiskBySome(r, null);
				}
		    }
		}
		if (risk != null) {
			risk.setDeleteStatus("0");
			o_riskDAO.merge(risk);
		}
    }
	
	public Page<RiskAdjustHistory> findRiskAdjustHistoryBySome(String id,Page<RiskAdjustHistory> page,String sort, String dir,String query){
		
		DetachedCriteria dc = DetachedCriteria.forClass(RiskAdjustHistory.class);
        dc.add(Restrictions.eq("risk.id", id));
        if(StringUtils.isNotBlank(query)){	//按年份查询
        	dc.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
        	dc.createAlias("riskAssessPlan", "riskAssessPlan", CriteriaSpecification.LEFT_JOIN);
        	dc.createAlias("template", "template", CriteriaSpecification.LEFT_JOIN);
			dc.add(
					Restrictions.or(
							Restrictions.like("timePeriod.year",query,MatchMode.ANYWHERE), 
							Restrictions.or(
							Restrictions.like("riskAssessPlan.planName",query,MatchMode.ANYWHERE), 
							Restrictions.or(
							Restrictions.like("template.name", query, MatchMode.ANYWHERE), 
							Restrictions.like("calculateFormula", query, MatchMode.ANYWHERE))))
					);
			
		}
        
        if ("dateRange".equals(sort)) {
        	//按时间升序和降序排列
            dc.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
            String sortstr = "timePeriod.year";
            String sortstr2 = "timePeriod.month";
            if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc(sortstr));
                dc.addOrder(Order.asc(sortstr2));
            }
            else {
                dc.addOrder(Order.desc(sortstr));
                dc.addOrder(Order.desc(sortstr2));
            }
        }else if("assessementStatus".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("assessementStatus"));
            }
            else {
                dc.addOrder(Order.desc("assessementStatus"));
            }
        }else if("etrend".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("etrend"));
            }
            else {
                dc.addOrder(Order.desc("etrend"));
            }
        }else{//发生可能性，影响程度
        	dc.addOrder(Order.desc("adjustTime"));
        }
 
        return o_riskAdjustHistoryDAO.findPage(dc, page, false);
	}
	
	public Page<Risk> findRiskEventBySome(String id,Page<Risk> page,String sort, String dir,String query){
		DetachedCriteria dc = DetachedCriteria.forClass(Risk.class);
		if(id.equalsIgnoreCase("root")){
			dc.add(Restrictions.isNull("parent.id"));
		}else{
			dc.add(Restrictions.eq("parent.id", id));
		}
        dc.add(Restrictions.eq("parent.id", id));
        dc.add(Restrictions.eq("deleteStatus", "1"));
        dc.add(Restrictions.eq("isRiskClass", "re"));
        if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
 
        return o_riskDAO.findPage(dc, page, false);
	}
	
	public RiskAdjustHistory findLatestRiskAdjustHistoryByRiskId(String id){
		
		Criteria criteria = o_riskAdjustHistoryDAO.createCriteria();
		
		criteria.add(Restrictions.eq("risk.id", id));
		//
		criteria.addOrder(Order.desc("adjustTime"));  
		criteria.setMaxResults(1);

		return (RiskAdjustHistory) criteria.uniqueResult();
	}
	/**
	 * 根据ID获得实体
	 * @param id 风险ID
	 * @return Risk
	 * @author 金鹏祥
	 */
	public Risk findRiskById(String id){
		return this.o_riskDAO.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> riskTreeLoader(String id, String query, Boolean rbs, Boolean canChecked, String chooseId){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		
		Criteria criteria = o_riskDAO.createCriteria();
		Set<String> idSet = queryObjectBySearchName(query,rbs);
		if(StringUtils.isNotBlank(query)&&idSet.size()==0){
			return nodes;
		}

		if("root".equals(id)){
			criteria.add(Restrictions.isNull("parent.id"));
		}else{
			criteria.add(Restrictions.eq("parent.id", id));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		if(rbs){
			criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		}
		criteria.addOrder(Order.asc("sort"));
		List<Risk> list = criteria.list();

		for (Risk risk : list) {
			if(!idSet.contains(risk.getId())){
				continue;
			}
			map = new HashMap<String, Object>();
			map.put("id", risk.getId());
			map.put("dbid", risk.getId());
			map.put("text", risk.getName());
			map.put("code", risk.getCode());
			map.put("name", risk.getName());
			map.put("type", "risk");
			//展示出不同的灯图标
			String iconCls = "icon-ibm-symbol-0-sm";	//分为高中低，默认无
			RiskAdjustHistory history = findLatestRiskAdjustHistoryByRiskId(risk.getId());
			if(history!=null){
				iconCls = history.getAssessementStatus();
			}
			map.put("iconCls", iconCls);//icon-org
			map.put("cls", "org");
			map.put("leaf", !riskHasChildren(risk.getId(),rbs));
			if(canChecked) {
				if(StringUtils.isNotEmpty(chooseId)){
					for (String choose : StringUtils.split(chooseId,",")) {
						if(map.get("id").toString().equals(choose)){
							map.put("checked", true);
							break;
						}
						map.put("checked", false);
					}
				}else{
					map.put("checked", false);
				}
			}
			nodes.add(map);
		}
//		long end  = System.currentTimeMillis();
//		System.out.println("树查询时间："+(end-start));
		return nodes;
	}
	
	protected Boolean riskHasChildren(String parentId,Boolean rbs){
		boolean hasChildren = false;
		
		Criteria criteria = o_riskDAO.createCriteria();
		if("root".equals(parentId)){
			criteria.add(Restrictions.isNull("parent.id"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		if(rbs){
			criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		}
		criteria.setProjection(Projections.rowCount());
		int count = Integer.parseInt(criteria.uniqueResult().toString());
		if(count>0){
			hasChildren = true;
		}
		
		return hasChildren;
	}
	@SuppressWarnings("unchecked")
	protected Set<String> queryObjectBySearchName(String query,boolean rbs){
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_riskDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		if(rbs){
			criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		}
		List<Risk> list = criteria.list();
		
		for (Risk entity : list) {
			if(null==entity.getIdSeq()){
				continue;
			}
			String[] idsTemp = entity.getIdSeq().split("\\.");
			idSet.addAll(Arrays.asList(idsTemp));
		}
		return idSet;
	}
	/**
	 * 根据Id的Set集合查询列表 zhengjunxiang
	 */
	public List<HashMap<String, Object>> findRiskByIdSet(Set<String> idSet) {
		Criteria criteria = o_riskDAO.createCriteria();
		if(null != idSet && idSet.size()>0){
			criteria.add(Restrictions.in("id", idSet.toArray()));
		}else{
			criteria.add(Restrictions.isNotNull("id"));
		}
		criteria.addOrder(Order.asc("id"));
		return MapListUtil.toMapList(criteria.list());
	}
	
	/**
	 *   根据ids 查询所有满足的风险列表
	 */
	public List<Map<String,Object>>  findRiskByIds(String[] ids){
		
		List<Map<String,Object>> rtnList = new ArrayList<Map<String,Object>>();
		Map<String,Object> paramMap = null;
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.in("id",ids));
		criteria.setProjection(Projections.projectionList().add(Property.forName("id")).add(Property.forName("code")).add(Property.forName("name"))); //加入Projections 后返回结果是object
		List<Object> tempList = criteria.list();

		for(Object temp : tempList){
			Object[] obj = (Object[]) temp;
			paramMap = new HashMap<String,Object>();
			paramMap.put("id",obj[0].toString());
			paramMap.put("riskCode", obj[1].toString());
			paramMap.put("riskName", obj[2].toString());
			rtnList.add(paramMap);
		}
		return rtnList;
		
	}
		
	@Transactional
	public void enableRisk(String ids,String isUsed){
		String[] idArray = ids.split(",");
		for (String id : idArray) {
			Risk risk = findRiskById(id);
			risk.setIsUse(isUsed);
			o_riskDAO.merge(risk);
		}
	}
	
	public Page<Risk> findRiskEventByStrateMapId(String id,Page<Risk> page,String sort, String dir,String query){
		DetachedCriteria dc = DetachedCriteria.forClass(Risk.class);
		dc = dc.createAlias("kpiRelaRisks", "kpiRelaRisks");
		dc = dc.createAlias("kpiRelaRisks.kpi", "kpi");
		dc = dc.createAlias("kpi.dmRelaKpis", "dmRelaKpis");
		dc.add(Restrictions.eq("dmRelaKpis.strategyMap.id", id));
 
        return o_riskDAO.findPage(dc, page, false);
	}
	
	public Page<StrategyAdjustHistory> findStrategyHistoryByStrateMapId(String id,Page<StrategyAdjustHistory> page,String sort, String dir,String query,String schm){
		DetachedCriteria dc = DetachedCriteria.forClass(StrategyAdjustHistory.class);
		dc.add(Restrictions.eq("strategyMap.id", id));
		//查询目标历史事件，过滤风险分库标识
		if(null != schm && !"".equals(schm)){
			dc.createAlias("riskAssessPlan", "riskAssessPlan", CriteriaSpecification.LEFT_JOIN);
			dc.add(Restrictions.eq("riskAssessPlan.schm", schm));
			if("dept".equals(schm)){
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				dc.add(Restrictions.eq("riskAssessPlan.createOrg", deptId));
			}
		}
        if ("dateRange".equals(sort)) {
        	//按时间升序和降序排列
            dc.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
            String sortstr = "timePeriod.year";
            String sortstr2 = "timePeriod.month";
            if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc(sortstr));
                dc.addOrder(Order.asc(sortstr2));
            }
            else {
                dc.addOrder(Order.desc(sortstr));
                dc.addOrder(Order.desc(sortstr2));
            }
        }else if("assessementStatus".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("assessementStatus"));
            }
            else {
                dc.addOrder(Order.desc("assessementStatus"));
            }
        }else if("etrend".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("etrend"));
            }
            else {
                dc.addOrder(Order.desc("etrend"));
            }
        }else{//发生可能性，影响程度
        	dc.addOrder(Order.desc("adjustTime"));
        }
 
        return o_strategyAdjustHistoryDAO.findPage(dc, page, false);
	}
	
	public Page<KpiAdjustHistory> findKpiHistoryByKpiId(String id,Page<KpiAdjustHistory> page,String sort, String dir,String query,String schm){
		DetachedCriteria dc = DetachedCriteria.forClass(KpiAdjustHistory.class);
		dc.add(Restrictions.eq("kpiId.id", id));
		//查询目标历史事件，过滤风险分库标识
		if(null != schm && !"".equals(schm)){
			dc.createAlias("riskAssessPlan", "riskAssessPlan", CriteriaSpecification.LEFT_JOIN);
			dc.add(Restrictions.eq("riskAssessPlan.schm", schm));
			if("dept".equals(schm)){
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				dc.add(Restrictions.eq("riskAssessPlan.createOrg", deptId));
			}
		}
        if ("dateRange".equals(sort)) {
        	//按时间升序和降序排列
            dc.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
            String sortstr = "timePeriod.year";
            String sortstr2 = "timePeriod.month";
            if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc(sortstr));
                dc.addOrder(Order.asc(sortstr2));
            }
            else {
                dc.addOrder(Order.desc(sortstr));
                dc.addOrder(Order.desc(sortstr2));
            }
        }else if("assessementStatus".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("assessementStatus"));
            }
            else {
                dc.addOrder(Order.desc("assessementStatus"));
            }
        }else if("etrend".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("etrend"));
            }
            else {
                dc.addOrder(Order.desc("etrend"));
            }
        }else{//发生可能性，影响程度
        	dc.addOrder(Order.desc("adjustTime"));
        }
 
        return o_kpiAdjustHistoryDAO.findPage(dc, page, false);
	}
	
	public Page<Risk> findRiskEventByOrgId(String id,Page<Risk> page,String sort, String dir,String query){
		DetachedCriteria dc = DetachedCriteria.forClass(Risk.class);
		dc = dc.createAlias("riskOrgs", "riskOrgs");
		dc = dc.createAlias("riskOrgs.sysOrganization", "sysOrganization");
		dc.add(Restrictions.eq("isRiskClass", "re"));
		dc.add(Restrictions.eq("sysOrganization.id", id));
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
 
        return o_riskDAO.findPage(dc, page, false);
	}
	
	public Page<OrgAdjustHistory> findOrgHistoryByOrgId(String id,Page<OrgAdjustHistory> page,String sort, String dir,String query,String schm){
		DetachedCriteria dc = DetachedCriteria.forClass(OrgAdjustHistory.class);
		dc.add(Restrictions.eq("organization.id", id));
		if(null != schm && !"".equals(schm)){//判定评估计划中的风险分库标识
			dc.createAlias("riskAssessPlan", "riskAssessPlan", CriteriaSpecification.LEFT_JOIN);
			dc.add(Restrictions.eq("riskAssessPlan.schm", schm));
		}

        if ("dateRange".equals(sort)) {
        	//按时间升序和降序排列
            dc.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
            String sortstr = "timePeriod.year";
            String sortstr2 = "timePeriod.month";
            if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc(sortstr));
                dc.addOrder(Order.asc(sortstr2));
            }
            else {
                dc.addOrder(Order.desc(sortstr));
                dc.addOrder(Order.desc(sortstr2));
            }
        }else if("assessementStatus".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("assessementStatus"));
            }
            else {
                dc.addOrder(Order.desc("assessementStatus"));
            }
        }else if("etrend".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("etrend"));
            }
            else {
                dc.addOrder(Order.desc("etrend"));
            }
        }else{//发生可能性，影响程度
        	dc.addOrder(Order.desc("adjustTime"));
        }
		
        return o_orgAdjustHistoryDAO.findPage(dc, page, false);
	}
	
	public Page<Risk> findRiskEventByProcessId(String id,Page<Risk> page,String sort, String dir,String query){
		DetachedCriteria dc = DetachedCriteria.forClass(Risk.class);
		dc = dc.createAlias("riskProcessures", "riskProcessures");
		dc = dc.createAlias("riskProcessures.process", "process");
		dc.add(Restrictions.eq("process.id", id));
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
 
        return o_riskDAO.findPage(dc, page, false);
	}
	
	public Page<ProcessAdjustHistory> findProcessHistoryByProcessId(String id,Page<ProcessAdjustHistory> page,String sort, String dir,String query,String schm){
		DetachedCriteria dc = DetachedCriteria.forClass(ProcessAdjustHistory.class);
		dc.add(Restrictions.eq("process.id", id));
		if(null != schm && !"".equals(schm)){//判定评估计划中的风险分库标识
			dc.createAlias("riskAssessPlan", "riskAssessPlan", CriteriaSpecification.LEFT_JOIN);
			dc.add(Restrictions.eq("riskAssessPlan.schm", schm));
			if("dept".equals(schm)){
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				dc.add(Restrictions.eq("riskAssessPlan.createOrg", deptId));
			}
		}
        if ("dateRange".equals(sort)) {
        	//按时间升序和降序排列
            dc.createAlias("timePeriod", "timePeriod", CriteriaSpecification.LEFT_JOIN);
            String sortstr = "timePeriod.year";
            String sortstr2 = "timePeriod.month";
            if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc(sortstr));
                dc.addOrder(Order.asc(sortstr2));
            }
            else {
                dc.addOrder(Order.desc(sortstr));
                dc.addOrder(Order.desc(sortstr2));
            }
        }else if("assessementStatus".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("assessementStatus"));
            }
            else {
                dc.addOrder(Order.desc("assessementStatus"));
            }
        }else if("etrend".equals(sort)){
        	if ("ASC".equalsIgnoreCase(dir)) {
                dc.addOrder(Order.asc("etrend"));
            }
            else {
                dc.addOrder(Order.desc("etrend"));
            }
        }else{//发生可能性，影响程度
        	dc.addOrder(Order.desc("adjustTime"));
        }
 
        return o_processAdjustHistoryDAO.findPage(dc, page, false);
	}
	
	public Page<Risk> findRiskEventByKpiId(String id,Page<Risk> page,String sort, String dir,String query){
		DetachedCriteria dc = DetachedCriteria.forClass(Risk.class);
		dc = dc.createAlias("kpiRelaRisks", "kpiRelaRisks");
		dc = dc.createAlias("kpiRelaRisks.kpi", "kpi");
		dc.add(Restrictions.eq("kpi.id", id));
		if (StringUtils.isNotBlank(query)) {
			dc.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
 
        return o_riskDAO.findPage(dc, page, false);
	}
	
	/**
	 * 查询该公司下所有风险事件,并已上级风险ID,风险事件实体存储MAP
	 * @param companyId 公司ID
	 * @param isRiskClass 风险类型 风险事件/风险分类
	 * @return HashMap<String, ArrayList<Risk>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Risk>> findParentReRiskAllMap(String companyId, String isRiskClass){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("isRiskClass", isRiskClass));
		List<Risk> riskList = criteria.list();
		HashMap<String, ArrayList<Risk>> riskMap = new HashMap<String, ArrayList<Risk>>();
		
		for (Risk risk : riskList) {
			try {
				if(null != risk.getParent()){
					if(riskMap.get(risk.getParent().getId()) != null){
						riskMap.get(risk.getParent().getId()).add(risk);
					}else{
						ArrayList<Risk> list = new ArrayList<Risk>();
						list.add(risk);
						riskMap.put(risk.getParent().getId(), list);
					}
				}
			} catch (Exception e) {
			}
		}
		
		return riskMap;
	}
	
	/**
	 * 查询风险级别最高级别
	 * @param companyId 公司ID
	 * @return Integer
	 * @author 金鹏祥
	 * */
	public Integer findRiskMaxElevel(String companyId) {
		StringBuffer sql = new StringBuffer();
        sql.append(" select max(elevel),id from t_rm_risks where is_risk_class = 'rbs' and company_Id = :companyId ");
        SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("companyId", companyId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            int maxElevel = 0;
            
            if(null != objects[0]){
            	maxElevel = Integer.parseInt(objects[0].toString());
            }
            
            return maxElevel;
        }
        
        return 0;
	}
	
	/**
	 * 查询所有该公司下的风险,并已MAP方式存储
	 * @param companyId 公司ID
	 * @param maxElevel 风险最高级别
	 * @return HashMap<Integer, ArrayList<Risk>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<Integer, ArrayList<Risk>> findLevelRbsRiskAllMap(String companyId, int maxElevel){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("isRiskClass", "rbs"));
		
		HashMap<Integer, ArrayList<Risk>> riskMap = new HashMap<Integer,ArrayList<Risk>>();
		ArrayList<Risk> list = null;
		List<Risk> riskList = criteria.list();
		for (Risk risk : riskList) {
			
			if(null == risk.getLevel()){
				//根
				if(riskMap.get(0) != null){
					riskMap.get(0).add(risk);
				}else{
					list = new ArrayList<Risk>();
					list.add(risk);
					riskMap.put(0, list);
				}
			}else{
				if(riskMap.get(risk.getLevel()) != null){
					riskMap.get(risk.getLevel()).add(risk);
				}else{
					list = new ArrayList<Risk>();
					list.add(risk);
					riskMap.put(risk.getLevel(), list);
				}
			}
		}
		
		return riskMap;
	}
	
	/**
	 * 删除打分综合结果表
	 * */
	@Transactional
	public boolean delRisk(String riskId){
		boolean isBool = false;
		try {
			SQLQuery sqlQuery = o_riskDAO.createSQLQuery("delete from t_rm_risks where id = ?", riskId);
			sqlQuery.executeUpdate();
			isBool = true;
		} catch (Exception e) {
			System.out.println("==========删除统计打分表t_rm_statistics_result出错:" + e.getMessage());
			return isBool;
		}
		
		return isBool;
	}
	
	/**
	 * 批量修改风险事件状态
	 * */
	@Transactional
    public void updateRiskDelectEstatusByInRiskId(final ArrayList<String> riskIdList) {
        this.o_riskDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement pst = null;
                String sql = "update t_rm_risks set delete_estatus=? where id=?";
                pst = connection.prepareStatement(sql);
                
                for (String riskId : riskIdList) {
                	  pst.setString(1, "1");
                      pst.setString(2, riskId);
                      pst.addBatch();
				}
                pst.executeBatch();
            }
        });
    }
	
	/**
	 * 根据风险Code查询数据
	 * @author 元杰
	 * @param riskCode 风险编号
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, RiskFromExcel> findRiskExcelByCode() {
		Criteria criteria = o_riskFromExcelDAO.createCriteria();
		List<RiskFromExcel> risks = criteria.list();
		
		HashMap<String, RiskFromExcel> map = new HashMap<String, RiskFromExcel>();
		for (RiskFromExcel riskFromExcel : risks) {
			if(riskFromExcel.getCompany() != null){
				map.put(riskFromExcel.getCode() + "--" + riskFromExcel.getCompany().getId(), riskFromExcel);
			}
		}
		return map;
	}
	
	/**
	 * 根据风险Code查询数据
	 * @author 元杰
	 * @param riskCode 风险编号
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, List<RiskFromExcel>> findRiskExcelByCodeList() {
		Criteria criteria = o_riskFromExcelDAO.createCriteria();
		List<RiskFromExcel> risks = criteria.list();
		List<RiskFromExcel> list = null;
		HashMap<String, List<RiskFromExcel>> map = new HashMap<String, List<RiskFromExcel>>();
		for (RiskFromExcel riskFromExcel : risks) {
			
			if(map.get(riskFromExcel.getCode() + "--" + riskFromExcel.getCompany().getId()) != null){
				map.get(riskFromExcel.getCode() + "--" + riskFromExcel.getCompany().getId()).add(riskFromExcel);
			}else{
				list = new ArrayList<RiskFromExcel>();
				list.add(riskFromExcel);
				map.put(riskFromExcel.getCode() + "--" + riskFromExcel.getCompany().getId(), list);
			}
		}
		return map;
	}
	
	/**
	 * 根据风险Code查询数据
	 * @author 元杰
	 * @param riskCode 风险编号
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, RiskFromExcel> findRiskExcelByName() {
		Criteria criteria = o_riskFromExcelDAO.createCriteria();
		List<RiskFromExcel> risks = criteria.list();
		
		HashMap<String, RiskFromExcel> map = new HashMap<String, RiskFromExcel>();
		for (RiskFromExcel riskFromExcel : risks) {
			map.put(riskFromExcel.getName() + "--" + riskFromExcel.getCompany().getId(), riskFromExcel);
		}
		return map;
	}
	
	/**
	 * 根据风险Code查询数据
	 * @author 元杰
	 * @param riskCode 风险编号
	 */
	@SuppressWarnings("unchecked")
	public Risk findRiskByCode(String riskCode,String companyId) {
		Criteria criteria = o_riskDAO.createCriteria();
		if(StringUtils.isNotBlank(riskCode)){
			criteria.add(Restrictions.eq("code", riskCode));
		}
		if(StringUtils.isNotBlank(companyId)){
		    criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		List<Risk> risks = criteria.list();
		if(null != risks && risks.size() > 0){
			return risks.get(0);
		}
		return null;
	}
	
	/**
	 * 查询该公司全部风险事件
	*/
	@SuppressWarnings("unchecked")
	public List<Risk> findRiskListAll() {
		Criteria criteria = o_riskDAO.createCriteria();
		List<Risk> risks = criteria.list();
		return risks;
	}
	/**
	 * 根据公司id查询该公司的所有风险list.
	 * @author 吴德福
	 * @param companyId
	 * @return List<Risk>
	 */
	@SuppressWarnings("unchecked")
	public List<Risk> findRiskListByCompanyId(String companyId) {
		Criteria criteria = o_riskDAO.createCriteria();
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		
		return criteria.list();
	}
	
	/**
	 * 查询全部可用风险(风险编号，公司/集团ID)
	 * @param companyId 公司ID
	 * @param isRiskClass 风险类型 风险事件/风险分类
	 * @return HashMap<String, ArrayList<Risk>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, Risk> findRiskKeyIdAllMap(){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<Risk> riskList = criteria.list();
		HashMap<String, Risk> riskMap = new HashMap<String, Risk>();
		
		for (Risk risk : riskList) {
			if(null != risk.getCompany()){
				riskMap.put(risk.getCode() + "--" + risk.getCompany().getId(), risk);
			}
		}
		
		return riskMap;
	}
	
	/**
	 * 查询全部可用风险(风险编号，公司/集团ID)
	 * @param companyId 公司ID
	 * @param isRiskClass 风险类型 风险事件/风险分类
	 * @return HashMap<String, ArrayList<Risk>>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, Risk> findRiskKeyNameAllMap(){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<Risk> riskList = criteria.list();
		HashMap<String, Risk> riskMap = new HashMap<String, Risk>();
		
		for (Risk risk : riskList) {
			if(null != risk.getCompany()){
				riskMap.put(risk.getName() + "--" + risk.getCompany().getId(), risk);
			}
		}
		
		return riskMap;
	}
}