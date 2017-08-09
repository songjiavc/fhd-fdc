package com.fhd.sys.business.dataimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.response.RiskResponseDAO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.sys.dataimport.RiskFromExcelDAO;
import com.fhd.entity.assess.riskTidy.AdjustHistoryResult;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.response.RiskResponse;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.RiskRelaTemplate;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.entity.RiskFromExcel;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.TemplateBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sys.business.organization.OrgGridBO;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 数据导入BO类
 * @author   元杰
 */

@Service
public class DataImportBO {

	@Autowired
	private RiskBO o_riskBO;
	@Autowired
	private RiskDAO o_riskDAO;
	@Autowired
	private KpiBO o_kpiBO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private RiskFromExcelDAO o_riskFromExcelDAO;
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	@Autowired
	private TemplateBO o_templateBO;
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	@Autowired
	private OrgGridBO o_orgGridBO;
	
	/**
	 * 机构所有数据(机构名称)
	 * */
	public HashMap<String, List<SysOrganization>> findOrgAllMap(){
		HashMap<String,  List<SysOrganization>> map = new HashMap<String,  List<SysOrganization>>();
		List<SysOrganization> orgList = null;
		List<SysOrganization> list = o_orgGridBO.findAllOrganizationsByDeleteStatusFalse();
		for (SysOrganization en : list) {
			if(map.get(en.getOrgname()) != null){
				map.get(en.getOrgname()).add(en);
			}else{
				orgList = new ArrayList<SysOrganization>();
				orgList.add(en);
				map.put(en.getOrgname(), orgList);
			}
		}
		
		return map;
	}
	
	/**
	 * 机构所有数据(机构名称)
	 * */
	public HashMap<String, SysOrganization> findOrgByNameAllMap(){
		HashMap<String, SysOrganization> map = new HashMap<String, SysOrganization>();
		List<SysOrganization> list = o_orgGridBO.findAllOrganizationsByDeleteStatusFalse();
		if(list != null){
			for (SysOrganization en : list) {
				map.put(en.getOrgname(), en);
			}
		}
		
		return map;
	}
	
	/**
	 * 机构所有数据(机构名称)
	 * */
	public HashMap<String, SysOrganization> findOrgByNameAndCompanyIdAllMap(){
		HashMap<String, SysOrganization> map = new HashMap<String, SysOrganization>();
		List<SysOrganization> list = o_orgGridBO.findAllOrganizationsByDeleteStatusFalse();
		if(list != null){
			for (SysOrganization en : list) {
				if(en.getCompany() != null){
					map.put(en.getOrgname() + "--" + en.getCompany().getId(), en);
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 将风险数据导入到数据库
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	public ArrayList<RiskFromExcel> getRiskDataList(final List<List<String>> excelDatas){
		//清空临时表数据
		String sql2 = "delete from tmp_risks_exceldata";
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql2);
		sqlQuery.executeUpdate();
		ArrayList<RiskFromExcel> RiskFromExcelList = new ArrayList<RiskFromExcel>();
		HashMap<String, SysOrganization> orgAllMap = this.findOrgByNameAllMap();
		Map<String, Kpi> kpiAllMap = o_kpiBO.findKpiAllMap();
		Map<String, Process> processAllMap = o_processBO.findProcessByNameEAllMap();
		HashMap<String, Template> templateAllMap = o_templateBO.findTemplateByNameAllMap();
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				
                String parentId = rowDatas.get(0);
                String parentName = rowDatas.get(1);
                String riskCode = rowDatas.get(2);
                String riskName = rowDatas.get(3);
                String riskMs = rowDatas.get(4);
                String zrName = rowDatas.get(5);
    			String xgName = rowDatas.get(6);
    			String yxKpi = rowDatas.get(7);
    			String yxProcess = rowDatas.get(8);
    			String xh = rowDatas.get(9);
    			String templateName = rowDatas.get(10);
    			String fs = rowDatas.get(11);
    			String yx = rowDatas.get(12);
    			String gl = rowDatas.get(13);
    			String riskLevel = rowDatas.get(14);
    			String riskStatus = rowDatas.get(15);
    			String riskType = rowDatas.get(16);
    			String companyName = rowDatas.get(17);
				String SCHM = rowDatas.get(18);
				String createOrg = rowDatas.get(19);
				String responseStr = rowDatas.get(20);
    			RiskFromExcel riskFromExcel = new RiskFromExcel();
				riskFromExcel.setId(Identities.uuid());
				
				if(orgAllMap.get(companyName) != null){
					SysOrganization sys = new SysOrganization();
					sys.setId(orgAllMap.get(companyName).getId());
					riskFromExcel.setCompany(sys);
				}
				
				if(StringUtils.isNotBlank(zrName)){
					if(zrName.indexOf(",") != -1){
						String str[] = zrName.split(",");
						String tempStr = "";
						for (String string : str) {
							if(orgAllMap.get(string) != null){
								tempStr += orgAllMap.get(string).getId() + ",";
							}
						}
						riskFromExcel.setRespOrgs(tempStr);
					}else{
						if(orgAllMap.get(zrName) != null){
							riskFromExcel.setRespOrgs(orgAllMap.get(zrName).getId());
						}
					}
					
				}
				
				if(StringUtils.isNotBlank(xgName)){
					if(xgName.indexOf(",") != -1){
						String str[] = xgName.split(",");
						String tempStr = "";
						for (String string : str) {
							if(orgAllMap.get(string) != null){
								tempStr += orgAllMap.get(string).getId() + ",";
							}
						}
						riskFromExcel.setRelaOrgs(tempStr);
					}else{
						if(orgAllMap.get(xgName) != null){
							riskFromExcel.setRelaOrgs(orgAllMap.get(xgName).getId());
						}
					}
				}
				
				if(StringUtils.isNotBlank(yxKpi)){
					if(yxKpi.indexOf(",") != -1){
						String str[] = yxKpi.split(",");
						String tempStr = "";
						for (String string : str) {
							if(kpiAllMap.get(string) != null){
								tempStr += kpiAllMap.get(string).getId() + ",";;
							}
						}
						riskFromExcel.setImpactTarget(tempStr);
					}else{
						if(kpiAllMap.get(yxKpi) != null){
							riskFromExcel.setImpactTarget(kpiAllMap.get(yxKpi).getId());
						}
					}
				}
				
				if(StringUtils.isNotBlank(yxProcess)){
					if(yxProcess.indexOf(",") != -1){
						String str[] = yxProcess.split(",");
						String tempStr = "";
						for (String string : str) {
							if(processAllMap.get(string) != null){
								tempStr += processAllMap.get(string).getId() + ",";
							}
						}
						riskFromExcel.setImpactProcess(tempStr);
					}else{
						if(processAllMap.get(yxProcess) != null){
							riskFromExcel.setImpactProcess(processAllMap.get(yxProcess).getId());
						}
					}
				}
				
				
				if(StringUtils.isNotBlank(templateName)){
					if(templateAllMap.get(templateName) != null){
						riskFromExcel.setAssessmentTemplate(templateAllMap.get(templateName).getId());
					}
				}
				
				riskFromExcel.setParentCode(parentId);
				riskFromExcel.setParentName(parentName);
				riskFromExcel.setCode(riskCode);
				riskFromExcel.setName(riskName);
				riskFromExcel.setDesc(riskMs);
				riskFromExcel.setRespOrgsName(zrName);
				riskFromExcel.setRelaOrgsName(xgName);
				riskFromExcel.setImpactTargetName(yxKpi);
				riskFromExcel.setImpactProcessName(yxProcess);
				riskFromExcel.setEsort(xh);
				riskFromExcel.setAssessmentTemplateName(templateName);
				riskFromExcel.setProbability(fs);
				riskFromExcel.setImpact(yx);
				riskFromExcel.setUrgency(gl);
				riskFromExcel.setRiskLevel(riskLevel);
				riskFromExcel.setRiskStatus(riskStatus);
				riskFromExcel.setIsRiskClass(riskType);
				riskFromExcel.setExRow(String.valueOf(row + 1));
				riskFromExcel.setSCHM(SCHM);
				if(orgAllMap.get(createOrg) != null){
					SysOrganization sys = new SysOrganization();
					sys.setId(orgAllMap.get(createOrg).getId());
					riskFromExcel.setCreateOrg(sys);
				}
				//增加风险应对--wzr
				if(null != responseStr){
					riskFromExcel.setResponseStr(responseStr);
				}

				RiskFromExcelList.add(riskFromExcel);
			}
		}
		
		return RiskFromExcelList;
	}
	
	/**
	 * 批量添加风险记录
	 * */
	@Transactional
    public void saveRiskFromExcel(final ArrayList<RiskFromExcel> RiskFromExcelList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " INSERT INTO tmp_risks_exceldata " +
                	" (ID, COMPANY_ID, RISK_CODE, RISK_NAME, EDESC, PARENT_CODE, PARENT_NAME, ESORT, IS_RISK_CLASS, IS_INHERIT, " +
                	" PROBABILITY, IMPACT, URGENCY, RISK_LEVEL, RISK_STATUS, RESP_ORGS, RELA_ORGS, RESP_ORGS_NAME, RELA_ORGS_NAME, IMPACT_TARGET_NAME, " + 
                	" IMPACT_PROCESS_NAME, IMPACT_TARGET, IMPACT_PROCESS, ASSESSMENT_TEMPLATE_NAME, ASSESSMENT_TEMPLATE, COMMENT, EX_ROW, SCHM, CREATE_ORG,RISK_RESPONSE) values " +
                	" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (RiskFromExcel riskFromExcel : RiskFromExcelList) {
                	if(StringUtils.isNotBlank(riskFromExcel.getId())){
                		pst.setString(1, riskFromExcel.getId());
                	}else{
                		pst.setString(1, null);
                	}
                	
                	if(null != riskFromExcel.getCompany()){
                		if(StringUtils.isNotBlank(riskFromExcel.getCompany().getId())){
                    		pst.setString(2, riskFromExcel.getCompany().getId());
                    	}
                	}else{
                		pst.setString(2, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getCode())){
                		pst.setString(3, riskFromExcel.getCode());
                	}else{
                		pst.setString(3, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getName())){
                		pst.setString(4, riskFromExcel.getName());
                	}else{
                		pst.setString(4, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getDesc())){
                		pst.setString(5, riskFromExcel.getDesc());
                	}else{
                		pst.setString(5, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getParentCode())){
                		pst.setString(6, riskFromExcel.getParentCode());
                	}else{
                		pst.setString(6, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getParentName())){
                		pst.setString(7, riskFromExcel.getParentName());
                	}else{
                		pst.setString(7, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getEsort())){
                		pst.setString(8, riskFromExcel.getEsort());
                	}else{
                		pst.setString(8, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getIsRiskClass())){
                		pst.setString(9, riskFromExcel.getIsRiskClass());
                	}else{
                		pst.setString(9, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getIsInherit())){
                		pst.setString(10, riskFromExcel.getIsInherit());
                	}else{
                		pst.setString(10, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getProbability())){
                		pst.setString(11, riskFromExcel.getProbability());
                	}else{
                		pst.setString(11, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getImpact())){
                		pst.setString(12, riskFromExcel.getImpact());
                	}else{
                		pst.setString(12, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getUrgency())){
                		pst.setString(13, riskFromExcel.getUrgency());
                	}else{
                		pst.setString(13, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getRiskLevel())){
                		pst.setString(14, riskFromExcel.getRiskLevel());
                	}else{
                		pst.setString(14, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getRiskStatus())){
                		pst.setString(15, riskFromExcel.getRiskStatus());
                	}else{
                		pst.setString(15, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getRespOrgs())){
                		pst.setString(16, riskFromExcel.getRespOrgs());
                	}else{
                		pst.setString(16, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getRelaOrgs())){
                		pst.setString(17, riskFromExcel.getRelaOrgs());
                	}else{
                		pst.setString(17, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getRespOrgsName())){
                		pst.setString(18, riskFromExcel.getRespOrgsName());
                	}else{
                		pst.setString(18, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getRelaOrgsName())){
                		pst.setString(19, riskFromExcel.getRelaOrgsName());
                	}else{
                		pst.setString(19, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getImpactTargetName())){
                		pst.setString(20, riskFromExcel.getImpactTargetName());
                	}else{
                		pst.setString(20, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getImpactProcessName())){
                		pst.setString(21, riskFromExcel.getImpactProcessName());
                	}else{
                		pst.setString(21, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getImpactTarget())){
                		pst.setString(22, riskFromExcel.getImpactTarget());
                	}else{
                		pst.setString(22, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getImpactProcess())){
                		pst.setString(23, riskFromExcel.getImpactProcess());
                	}else{
                		pst.setString(23, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getAssessmentTemplateName())){
                		pst.setString(24, riskFromExcel.getAssessmentTemplateName());
                	}else{
                		pst.setString(24, null);
                	}
                	
                	if(StringUtils.isNotBlank(riskFromExcel.getAssessmentTemplate())){
                		pst.setString(25, riskFromExcel.getAssessmentTemplate());
                	}else{
                		pst.setString(25, null);
                	}

					if(StringUtils.isNotBlank(riskFromExcel.getExRow())){
						pst.setString(26, riskFromExcel.getExRow());
					}else{
						pst.setString(26, null);
					}

                	
                	if(StringUtils.isNotBlank(riskFromExcel.getComment())){
                		pst.setString(27, riskFromExcel.getComment());
                	}else{
                		pst.setString(27, null);
                	}

					if(StringUtils.isNotBlank(riskFromExcel.getSCHM())){
						pst.setString(28, riskFromExcel.getSCHM());
					}else{
						pst.setString(28, null);
					}
					if(null!=riskFromExcel.getCreateOrg()){
						pst.setString(29, riskFromExcel.getCreateOrg().getId());
					}else{
						pst.setString(29, null);
					}
					if(null!=riskFromExcel.getResponseStr()){
						pst.setString(30, riskFromExcel.getResponseStr());
					}else{
						pst.setString(30, null);
					}
                    pst.addBatch();
				}
				System.out.println("sql:"+pst.getParameterMetaData());
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	
	@SuppressWarnings("unchecked")
	public void updateRiskFromExcelLevel(boolean isFirst,List<Object[]> list,Integer level){
	    if(isFirst){
	        String sql = " select RISK_CODE,RISK_NAME from TMP_RISKS_EXCELDATA where PARENT_CODE = '' ";
	        List<Object[]> listsub = o_riskDAO.createSQLQuery(sql).list();
	        if(!listsub.isEmpty()){
	            String sql2 = " update TMP_RISKS_EXCELDATA set ELEVEL = '1' where PARENT_CODE = '' ";
	            SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql2);
	            sqlQuery.executeUpdate();
	            this.updateRiskFromExcelLevel(false,listsub,1);
	        }
	    }else{
	        for(Object[] obj : list){
	            String riskcode = obj[0].toString();
	            String sql = " select RISK_CODE,RISK_NAME from TMP_RISKS_EXCELDATA where PARENT_CODE = ? ";
	            List<Object[]> listsub = new ArrayList<Object[]>();
	            listsub = o_riskDAO.createSQLQuery(sql,riskcode).list();
	            if(!list.isEmpty()){
	                String sql2 = " update TMP_RISKS_EXCELDATA set ELEVEL = ? where PARENT_CODE = ? ";
	                SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql2,level+1,riskcode);
	                sqlQuery.executeUpdate();
	                this.updateRiskFromExcelLevel(false,listsub,level+1);
	            }
	        }
	    }
	}
	
	/**
	 * 查询风险临时表数据
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<RiskFromExcel> findRiskFromExcelById(String id){
		Criteria criteria = o_riskFromExcelDAO.createCriteria();
		if(StringUtils.isNotBlank(id)){
			criteria.add(Restrictions.eq("id", id));
		}
		criteria.addOrder(Order.asc("esort"));
		return criteria.list();
	}
	
	/**
	 * 查询风险临时表数据code
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<RiskFromExcel> findRiskFromExcelByCode(String code){
		Criteria criteria = o_riskFromExcelDAO.createCriteria();
		if(StringUtils.isNotBlank(code)){
			criteria.add(Restrictions.eq("code", code));
		}
		criteria.addOrder(Order.asc("esort"));
		return criteria.list();
	}
	
	/**
	 * 查询风险临时表数据
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<RiskFromExcel> findRiskFromExcelBySome(String query){
		Criteria criteria = o_riskFromExcelDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Restrictions.like("name", query, MatchMode.ANYWHERE), Restrictions.like("code", query, MatchMode.ANYWHERE)));
		}
		criteria.addOrder(Order.desc("comment"));
		criteria.addOrder(Order.asc("esort"));
		return criteria.list();
	}
	
	/**
	 * 将风险数据更新到数据库
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void saveRiskFromExcel(RiskFromExcel riskFromExcel){
		o_riskFromExcelDAO.merge(riskFromExcel);
		this.validateRiskData();
	}
	
	/**
	 * 将风险数据真实写入系统
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void importRiskFromExcelToBD(){
		List<RiskFromExcel> riskFormExcelList = this.findRiskFromExcelById(null);
		HashMap<String, Risk> riskAllMap = o_riskBO.findRiskKeyIdAllMap();
		
		ArrayList<Risk> riskSaveList = new ArrayList<Risk>();
		ArrayList<Risk> riskEditList = new ArrayList<Risk>();
		ArrayList<RiskOrg> riskOrgSaveList = new ArrayList<RiskOrg>();
		ArrayList<RiskAdjustHistory> riskAdjustHistorySaveList = new ArrayList<RiskAdjustHistory>();
		List<Object> adjustHistoryResultSaveList = new ArrayList<Object>();
		ArrayList<RiskRelaTemplate> riskRelaTemplateSaveList = new ArrayList<RiskRelaTemplate>();
		ArrayList<KpiRelaRisk> KpiRelaRiskSaveList = new ArrayList<KpiRelaRisk>();
		ArrayList<ProcessRelaRisk> processRelaRiskSaveList = new ArrayList<ProcessRelaRisk>();
		ArrayList<RiskResponse> saveReponseList = new ArrayList<RiskResponse>();
		
		HashMap<String, List<TemplateRelaDimension>> TemplateRelaDimensionListAllMap = 
				o_templateRelaDimensionBO.findTemplateRelaDimensionByTempIdAllMap();
		HashMap<String, RiskFromExcel> riskExcelAllMap = o_riskBO.findRiskExcelByCode();
		for(RiskFromExcel riskFromExcel : riskFormExcelList){
			Risk risk = new Risk();
			BeanUtils.copyProperties(riskFromExcel, risk);
			
			risk.setId(riskFromExcel.getId());
			
			risk.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			risk.setCreateTime(new Date());
			risk.setLastModifyTime(new Date());
			risk.setSchm(riskFromExcel.getSCHM());
			risk.setCreateOrg(riskFromExcel.getCreateOrg());
			String idSeq = "";
			
			if(riskAllMap.size() != 0){
				idSeq = this.generateRiskIdSeq(riskFromExcel, riskAllMap);
				
				if(riskFromExcel.getParentCode() != null){
					Risk parentRisk = new Risk();
					parentRisk.setCode(riskAllMap.get(riskFromExcel.getParentCode() + "--" + riskFromExcel.getCompany().getId()).getCode());
					parentRisk.setId(riskAllMap.get(riskFromExcel.getParentCode() + "--" + riskFromExcel.getCompany().getId()).getId());
					risk.setParent(parentRisk);
				}
			}else{
				idSeq = this.generateIdSeq(riskFromExcel, riskExcelAllMap);
				
				if(riskFromExcel.getParentCode() != null){
					Risk parentRisk = new Risk();
					parentRisk.setCode(riskExcelAllMap.get(riskFromExcel.getParentCode() + "--" + riskFromExcel.getCompany().getId()).getCode());
					parentRisk.setId(riskExcelAllMap.get(riskFromExcel.getParentCode() + "--" + riskFromExcel.getCompany().getId()).getId());
					risk.setParent(parentRisk);
				}
			}
			
//			String idSeq = this.generateIdSeq(risk);
			risk.setIdSeq(idSeq);
			//层级取法有问题--吴德福修改2014-1-22
			String[] idSplit = idSeq.substring(1, idSeq.length()).split("\\.");
			
			if(idSplit.length != 1){
				
			}
			
			risk.setLevel(idSplit.length);
			if("re".equals(riskFromExcel.getIsRiskClass())){
				risk.setIsLeaf(true);
			}else{
				risk.setIsLeaf(false);
			}
			if(StringUtils.isNotBlank(riskFromExcel.getEsort())){
				risk.setSort(Integer.parseInt(riskFromExcel.getEsort()));
			}else{
				risk.setSort(0);
			}
			if(StringUtils.isNotBlank(riskFromExcel.getAssessmentTemplate())){
				Template template = new Template();
				template.setId(riskFromExcel.getAssessmentTemplate());
				risk.setTemplate(template);
			}
			risk.setIsUse(Contents.DICT_Y);
			//将archiveStatus默认设置成RISK_STATUS_ARCHIVED 郑军祥
			risk.setArchiveStatus(Contents.RISK_STATUS_ARCHIVED);
			
//			if(riskEdit == 1){
//				riskEditList.add(risk);
//			}else{
				riskSaveList.add(risk);
				if(riskAllMap.get(risk.getCode() + "--" + risk.getCompany().getId()) == null){
					riskAllMap.put(risk.getCode() + "--" + risk.getCompany().getId(), risk);
				}
//			}
//			riskEdit = 0;
//			o_riskDAO.merge(risk);
				
			//保存风险关联风险应对（保密风险）--wzr
			if(null != riskFromExcel.getResponseStr()){
				RiskResponse response = new RiskResponse();
				response.setId(Identities.uuid2());
				response.setRiskId(risk.getId());
				response.setEditIdeaContent(riskFromExcel.getResponseStr());
				saveReponseList.add(response);
			}
			
			if(StringUtils.isNotBlank(riskFromExcel.getRespOrgs())){
				String[] respOrgIds = riskFromExcel.getRespOrgs().split(",");
				for(String respOrgId : respOrgIds){
					RiskOrg riskOrg = new RiskOrg();
					riskOrg.setId(Identities.uuid2());
					riskOrg.setRisk(risk);
					SysOrganization org = new SysOrganization();
					org.setId(respOrgId);
					riskOrg.setSysOrganization(org);
					riskOrg.setType(Contents.MAIN);
					
					riskOrgSaveList.add(riskOrg);
					//o_riskOrgDAO.merge(riskOrg);
				}
			}
			
			if(StringUtils.isNotBlank(riskFromExcel.getRelaOrgs())){
				String[] relaOrgIds = riskFromExcel.getRelaOrgs().split(",");
				for(String relaOrgId : relaOrgIds){
					RiskOrg riskOrg = new RiskOrg();
					riskOrg.setId(Identities.uuid2());
					riskOrg.setRisk(risk);
					SysOrganization org = new SysOrganization();
					org.setId(relaOrgId);
					riskOrg.setSysOrganization(org);
					riskOrg.setType(Contents.ASSISTANT);
					
					riskOrgSaveList.add(riskOrg);
					//o_riskOrgDAO.merge(riskOrg);
				}
			}	
			if(StringUtils.isNotBlank(riskFromExcel.getRiskLevel())){
			    //保存风险评估分数
			    RiskAdjustHistory riskAdjustHistory = new RiskAdjustHistory();
			    riskAdjustHistory.setId(Identities.uuid2());
			    riskAdjustHistory.setRisk(risk);
			    try{
//			        riskAdjustHistory.setProbability(Double.parseDouble(riskFromExcel.getProbability()));
//			        riskAdjustHistory.setImpacts(Double.parseDouble(riskFromExcel.getImpact()));
//			        riskAdjustHistory.setManagementUrgency(Double.parseDouble(riskFromExcel.getUrgency()));
			        riskAdjustHistory.setStatus(Double.parseDouble(riskFromExcel.getRiskLevel()==null?"0":riskFromExcel.getRiskLevel()));
			    }catch(Exception e){
			        e.printStackTrace();
			    }
			    riskAdjustHistory.setAdjustType("1");
			    riskAdjustHistory.setIsLatest("1");
			    riskAdjustHistory.setCompany(risk.getCompany());
			    riskAdjustHistory.setAdjustTime(new Date());
			    if("红".equals(riskFromExcel.getRiskStatus())){
			        riskAdjustHistory.setAssessementStatus(Contents.RISK_LEVEL_HIGH);
			    }else if("黄".equals(riskFromExcel.getRiskStatus())){
			        riskAdjustHistory.setAssessementStatus(Contents.RISK_LEVEL_MIDDLE);
			    }else if("绿".equals(riskFromExcel.getRiskStatus())){
			        riskAdjustHistory.setAssessementStatus(Contents.RISK_LEVEL_LOW);
			    }
			    Template template = new Template();
			    template.setId(riskFromExcel.getAssessmentTemplate());
			    riskAdjustHistory.setTemplate(template);
			    
//			    List<TemplateRelaDimension> temlateDim = 
//			    		o_templateRelaDimensionBO.findTemplateRelaDimensionAllList(riskFromExcel.getAssessmentTemplate());
			    
			    List<TemplateRelaDimension> temlateDim = TemplateRelaDimensionListAllMap.get(riskFromExcel.getAssessmentTemplate());
			    if(null != temlateDim){
			    	for (TemplateRelaDimension templateRelaDimension : temlateDim) {
				    	Dimension dimension = new Dimension();
				    	String scoreValue = "";
				    	if(templateRelaDimension.getDimension().getName().equalsIgnoreCase("发生可能性")){
				    		if(null != riskFromExcel.getProbability()){
				    			scoreValue = riskFromExcel.getProbability();
				    		}
				    	}if(templateRelaDimension.getDimension().getName().equalsIgnoreCase("影响程度")){
				    		if(null != riskFromExcel.getImpact()){
				    			scoreValue = riskFromExcel.getImpact();
				    		}
				    	}if(templateRelaDimension.getDimension().getName().equalsIgnoreCase("管理紧迫性")){
				    		if(null != riskFromExcel.getUrgency()){
				    			scoreValue = riskFromExcel.getUrgency();
				    		}
				    	}
				    	
				    	AdjustHistoryResult adjustHistoryResult = new AdjustHistoryResult();
					    adjustHistoryResult.setId(Identities.uuid2());
					    adjustHistoryResult.setAdjustHistoryId(riskAdjustHistory.getId());
				    	dimension.setId(templateRelaDimension.getDimension().getId());
				    	adjustHistoryResult.setDimension(dimension);
				    	adjustHistoryResult.setScore(scoreValue);
				    	
				    	adjustHistoryResultSaveList.add(adjustHistoryResult);
				    	//o_adjustHistoryResultBO.mergeAdjustHistoryResult(adjustHistoryResult);
					}
			    }
			    
			    
			    riskAdjustHistorySaveList.add(riskAdjustHistory);
//			    o_riskAdjustHistoryDAO.merge(riskAdjustHistory);
			}
			
			//保存风险关联模板
			if(StringUtils.isNotBlank(riskFromExcel.getAssessmentTemplate())){
				RiskRelaTemplate riskRelaTemplate = new RiskRelaTemplate();
				riskRelaTemplate.setId(Identities.uuid2());
				riskRelaTemplate.setIsCreator(true);
				riskRelaTemplate.setRisk(risk);
				Template template = new Template();
				template.setId(riskFromExcel.getAssessmentTemplate());
				riskRelaTemplate.setTemplate(template);
				
				riskRelaTemplateSaveList.add(riskRelaTemplate);
//				o_riskRelaTemplateDAO.merge(riskRelaTemplate);
			}
			
			//保存风险关联指标
			if(StringUtils.isNotBlank(riskFromExcel.getImpactTarget())){
				String[] kpiIds = riskFromExcel.getImpactTarget().split(",");
				for(String kpiId : kpiIds){
					KpiRelaRisk kpiRelaRisk = new KpiRelaRisk();
					kpiRelaRisk.setId(Identities.uuid2());
					Kpi kpi = new Kpi();
					kpi.setId(kpiId);
					kpiRelaRisk.setKpi(kpi);
					kpiRelaRisk.setRisk(risk);
					kpiRelaRisk.setType("I");
					
					KpiRelaRiskSaveList.add(kpiRelaRisk);
//					o_kpiRelaRiskDAO.merge(kpiRelaRisk);
				}
			}
			
			//保存风险关联流程
			if(StringUtils.isNotBlank(riskFromExcel.getImpactProcess())){
				String[] processIds = riskFromExcel.getImpactProcess().split(",");
				for(String processId : processIds){
					ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
					processRelaRisk.setId(Identities.uuid2());
					Process process = new Process();
					process.setId(processId);
					processRelaRisk.setProcess(process);
					processRelaRisk.setRisk(risk);
					processRelaRisk.setType("I");
					
					processRelaRiskSaveList.add(processRelaRisk);
//					o_processRelaRiskDAO.merge(processRelaRisk);
				}
			}
		}
		
		if(riskSaveList.size() != 0){
			//保存风险
			this.saveRisk(riskSaveList);
		}if(riskEditList.size() != 0){
			//编辑风险
			this.editRisk(riskSaveList);
		}
		
		if(riskOrgSaveList.size() != 0){
			//保存风险部门关联
			this.saveRiskOrg(riskOrgSaveList);
		}if(riskAdjustHistorySaveList.size() != 0){
			//保存风险历史记录
			this.saveRiskAdjustHistory(riskAdjustHistorySaveList);
		}if(adjustHistoryResultSaveList.size() != 0){
			//保存分值历史记录
			this.saveAdjustHistorySql(adjustHistoryResultSaveList);
		}if(riskRelaTemplateSaveList.size() != 0){
			//保存风险模板关联
			this.saveRiskRelaTemplate(riskRelaTemplateSaveList);
		}if(KpiRelaRiskSaveList.size() != 0){
			//保存风险指标关联
			this.saveKpiRelaRisk(KpiRelaRiskSaveList);
		}if(processRelaRiskSaveList.size() != 0){
			//保存风险流程关联
			this.saveProcessRelaRisk(processRelaRiskSaveList);
		}
		if(saveReponseList.size() != 0){
			this.saveRiskResponse(saveReponseList);
		}
	}
	
	/**
	 * 递归生成风险IDSEQ
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	public String generateIdSeq(RiskFromExcel risk, HashMap<String, RiskFromExcel> map){
		String idSeq = "";
		String parentCode = "";
		
		if(risk.getParentCode() != null){
			parentCode = risk.getParentCode();
			while (true){
				if(map.get(parentCode + "--" + risk.getCompany().getId()) != null){
					idSeq += "." + map.get(parentCode + "--" + risk.getCompany().getId()).getId();
					if(map.get(parentCode + "--" + risk.getCompany().getId()).getParentCode() != null){
						parentCode = map.get(parentCode + "--" + risk.getCompany().getId()).getParentCode();
					}else{
						break;
					}
				}
			}
			String str[] = idSeq.split("\\.");
			idSeq = "";
			for (int i = str.length - 1; i > 0; i--) {
				idSeq += "." + str[i];
			}
			
			idSeq += "." + risk.getId() + ".";
		}else{
			idSeq = "." + risk.getId() + ".";
		}
		return idSeq;
	}
	
	/**
	 * 递归生成风险IDSEQ
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	public String generateRiskIdSeq(RiskFromExcel risk, HashMap<String, Risk> map){
		String idSeq = "";
		String parentCode = "";
		
		if(risk.getParentCode() != null){
			parentCode = risk.getParentCode();
			while (true){
				if(null  != map.get(parentCode + "--" + risk.getCompany().getId())){
					idSeq += "." + map.get(parentCode + "--" + risk.getCompany().getId()).getId();
					if(map.get(parentCode + "--" + risk.getCompany().getId()).getParent() != null){
						parentCode = map.get(parentCode + "--" + risk.getCompany().getId()).getParent().getCode();
					}else{
						break;
					}
				}
			}
			String str[] = idSeq.split("\\.");
			idSeq = "";
			for (int i = str.length - 1; i > 0; i--) {
				idSeq += "." + str[i];
			}
			
			idSeq += "." + risk.getId() + ".";
		}else{
			idSeq = "." + risk.getId() + ".";
		}
		return idSeq;
	}
	
	/**
	 * 验证临时表中Risk 数据
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	public ArrayList<RiskFromExcel> validateRiskData(){
		ArrayList<RiskFromExcel> RiskFromExcelList = new ArrayList<RiskFromExcel>();
		Map<String, Kpi> kpiAllMap = o_kpiBO.findKpiAllMap();
		HashMap<String, List<SysOrganization>> orgAllMap = this.findOrgAllMap();
		HashMap<String, Template> templateAllMap = o_templateBO.findTemplateByNameAllMap();
		Map<String, Process> processAllMap = o_processBO.findProcessByNameEAllMap();
		List<RiskFromExcel> riskFromExcels = this.findRiskFromExcelById(null);
		HashMap<String, Risk> riskAllMap = o_riskBO.findRiskKeyIdAllMap();
		HashMap<String, RiskFromExcel> riskExcelAllMap = o_riskBO.findRiskExcelByCode();
		
		HashMap<String, List<RiskFromExcel>> riskExcelByCodeList = o_riskBO.findRiskExcelByCodeList();
		
		HashMap<String, Risk> riskNameAllMap = o_riskBO.findRiskKeyNameAllMap();
		HashMap<String, RiskFromExcel> riskNameExcelAllMap = o_riskBO.findRiskExcelByName();
		for (RiskFromExcel riskFromExcel : riskFromExcels) {// 读取数据行
			StringBuilder sb = new StringBuilder();//记录错误信息

			String companyId = "";
			String riskCode = riskFromExcel.getCode();
			String riskName = riskFromExcel.getName();
			String parentCode = riskFromExcel.getParentCode();
			String parentName = riskFromExcel.getParentName();
			String xgName = riskFromExcel.getRelaOrgsName();
			String zrName = riskFromExcel.getRespOrgsName();
			String yxKpi = riskFromExcel.getImpactTargetName();
			String yxProcess = riskFromExcel.getImpactProcessName();
			String templateName = riskFromExcel.getAssessmentTemplateName();
			String fs = riskFromExcel.getProbability();
			String yx = riskFromExcel.getImpact();
			String gl = riskFromExcel.getUrgency();
			String isRiskClass = riskFromExcel.getIsRiskClass();
			String SCHM=riskFromExcel.getSCHM();
			String createOrg="";
			
			//验证集团
			if(null != riskFromExcel.getCompany()){
				companyId = riskFromExcel.getCompany().getId();
			}else{
				sb.append("集团名称不存在.");
			}
			
			//验证上级风险编号
			if(StringUtils.isBlank(parentCode) && StringUtils.isBlank(parentName)){
				//根级风险
				if(StringUtils.isNotBlank(riskCode)){
					if(riskAllMap.size() != 0){
						//风险库中有数据
						if(riskAllMap.get(riskCode + "--" + companyId) == null){
							if(riskExcelAllMap.get(riskCode + "--" + companyId) == null){
								sb.append("风险编号不存在.");
							}
						}else{
							sb.append("风险编号已存在.");
						}
					}else{
						if(riskExcelAllMap.get(riskCode + "--" + companyId) == null){
							sb.append("风险编号不存在.");
						}else{
							if(riskExcelByCodeList.get(riskCode + "--" + companyId).size() > 1){
								sb.append("风险编号相同,只能唯一.");
							}
						}
					}
				}else{
					sb.append("风险编号不能为空.");
				}
			}else{
				if(StringUtils.isBlank(riskCode)){
					sb.append("风险编号不能为空.");
				}
				
				if(StringUtils.isBlank(riskName)){
					sb.append("风险名称不能为空.");
				}
				
				if(StringUtils.isBlank(parentCode)){
					sb.append("父级风险编号不能为空.");
				}
				
				if(StringUtils.isBlank(parentName)){
					sb.append("父级风险名称不能为空.");
				}
				if (StringUtils.isNotBlank(SCHM)) {
					if (SCHM.equals("dept")) {
						if (null==riskFromExcel.getCreateOrg()) {
							sb.append("所属部门不能为空");
						}
					}
				}
				
				
				if(StringUtils.isNotBlank(riskCode)){
					if(riskAllMap.size() != 0){
						//风险库中有数据
						if(riskAllMap.get(riskCode + "--" + companyId) == null){
							if(riskExcelAllMap.get(riskCode + "--" + companyId) == null){
								sb.append("风险编号不存在.");
							}
						}else{
							sb.append("风险编号已存在.");
						}
					}else{
						if(riskExcelAllMap.get(riskCode + "--" + companyId) == null){
							sb.append("风险编号不存在.");
						}else{
							if(riskExcelByCodeList.get(riskCode + "--" + companyId).size() > 1){
								sb.append("风险编号相同,只能唯一.");
							}
						}
					}
				}
				
				
				if(StringUtils.isNotBlank(riskName)){
					if(riskNameAllMap.size() != 0){
						//风险库中有数据
						if(riskNameAllMap.get(riskName + "--" + companyId) == null){
							if(riskNameExcelAllMap.get(riskName + "--" + companyId) == null){
								sb.append("风险名称不存在.");
							}
						}
					}else{
						if(riskNameExcelAllMap.get(riskName + "--" + companyId) == null){
							sb.append("风险名称不存在.");
						}
					}
				}
			}
			
			if(StringUtils.isNotBlank(parentCode)){
				if(riskAllMap.size() != 0){
					//风险库中有数据
					if(riskAllMap.get(parentCode + "--" + companyId) == null){
						if(riskExcelAllMap.get(parentCode + "--" + companyId) == null){
							sb.append("父级风险编号不存在.");
						}
					}
				}else{
					if(riskExcelAllMap.get(parentCode + "--" + companyId) == null){
						sb.append("父级风险编号不存在.");
					}
				}
			}
			
			
			if(StringUtils.isNotBlank(parentName)){
				if(riskNameAllMap.size() != 0){
					//风险库中有数据
					if(riskNameAllMap.get(parentName + "--" + companyId) == null){
						if(riskNameExcelAllMap.get(parentName + "--" + companyId) == null){
							sb.append("父级风险名称不存在.");
						}
					}
				}else{
					if(riskNameExcelAllMap.get(parentName + "--" + companyId) == null){
						sb.append("父级风险名称不存在.");
					}
				}
			}
			
			if(StringUtils.isBlank(isRiskClass)){
				sb.append("风险类型不能为空,应为re,rbs系列.");
			}else{
				if(isRiskClass.equalsIgnoreCase("re")){
					if(StringUtils.isBlank(zrName)){
						sb.append("责任部门不能为空.");
					}else{
						if(zrName.indexOf(",") != -1){
							String str[] = zrName.split(",");
							for (String string : str) {
								if(orgAllMap.get(string) == null){
									sb.append(string + "责任部门不存在.");
								}
							}
						}else{
							if(orgAllMap.get(zrName) == null){
								sb.append("责任部门不存在.");
							}
						}
					}
					
					if(StringUtils.isNotBlank(xgName)){
						if(xgName.indexOf(",") != -1){
							String str[] = xgName.split(",");
							for (String string : str) {
								if(orgAllMap.get(string) == null){
									sb.append(string + "相关部门不存在.");
								}
							}
						}else{
							if(orgAllMap.get(xgName) == null){
								sb.append("相关部门不存在.");
							}
						}
					}
					
					if(StringUtils.isNotBlank(yxKpi)){
						if(yxKpi.indexOf(",") != -1){
							String str[] = yxKpi.split(",");
							for (String string : str) {
								if(kpiAllMap.get(string) == null){
									sb.append(string + "影响指标不存在.");
								}
							}
						}else{
							if(kpiAllMap.get(yxKpi) == null){
								sb.append("影响指标不存在.");
							}
						}
					}

					if(StringUtils.isNotBlank(yxProcess)){
						if(yxProcess.indexOf(",") != -1){
							String str[] = yxProcess.split(",");
							for (String string : str) {
								if(processAllMap.get(string) == null){
									sb.append(string + "影响流程不存在.");
								}
							}
						}else{
							if(processAllMap.get(yxProcess) == null){
								sb.append("影响流程不存在.");
							}
						}
					}
					
					if(StringUtils.isNotBlank(fs) || StringUtils.isNotBlank(yx) || StringUtils.isNotBlank(gl)){
						if(StringUtils.isBlank(templateName)){
							sb.append("维度存在情况下,模板不能为空.");
						}else{
							if(templateAllMap.get(templateName) == null){
								sb.append("模板不存在.");
							}
						}
					}
					
					if(StringUtils.isBlank(fs) && StringUtils.isBlank(yx) && StringUtils.isBlank(gl)){
						if(StringUtils.isNotBlank(templateName)){
							sb.append("必须指定维度分值");
						}
					}


				}
			}
			
			
			riskFromExcel.setComment(sb.toString());
			RiskFromExcelList.add(riskFromExcel);
		}
		
		Collections.sort(RiskFromExcelList, new Comparator<RiskFromExcel>() {
			@Override
			public int compare(RiskFromExcel arg0, RiskFromExcel arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
		});
		
		return RiskFromExcelList;
	}
	
	/**
	 * 批量添加风险模板关联
	 * */
	@Transactional
    public void saveRiskRelaTemplate(final ArrayList<RiskRelaTemplate> riskRelaTemplateSaveList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into T_RM_RISK_RELA_TEMPLATE (id, template_id, risk_id, template_resource) value (?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (RiskRelaTemplate riskRelaTemplate : riskRelaTemplateSaveList) {
                	  pst.setString(1, riskRelaTemplate.getId());
                      pst.setString(2, riskRelaTemplate.getTemplate().getId());
                      pst.setString(3, riskRelaTemplate.getRisk().getId());
                      pst.setString(4, "1");
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量添加风险流程关联
	 * */
	@Transactional
    public void saveProcessRelaRisk(final ArrayList<ProcessRelaRisk> processRelaRiskSaveList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into T_PROCESSURE_RISK_PROCESSURE (id, processure_id, risk_id, etype) value (?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (ProcessRelaRisk processRelaRisk : processRelaRiskSaveList) {
                	  pst.setString(1, processRelaRisk.getId());
                      pst.setString(2, processRelaRisk.getProcess().getId());
                      pst.setString(3, processRelaRisk.getRisk().getId());
                      pst.setString(4, "I");
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量添加风险指标关联
	 * */
	@Transactional
    public void saveKpiRelaRisk(final ArrayList<KpiRelaRisk> KpiRelaRiskSaveList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into T_KPI_KPI_RELA_RISK (id, risk_id, kpi_id, etype) value (?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (KpiRelaRisk kpiRelaRisk : KpiRelaRiskSaveList) {
                	  pst.setString(1, kpiRelaRisk.getId());
                      pst.setString(2, kpiRelaRisk.getRisk().getId());
                      pst.setString(3, kpiRelaRisk.getKpi().getId());
                      pst.setString(4, "I");
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量添加风险机构关联
	 * */
	@Transactional
    public void saveRiskOrg(final ArrayList<RiskOrg> riskOrgSaveList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into t_rm_risk_org (id, risk_id, etype, org_id) value (?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (RiskOrg riskOrg : riskOrgSaveList) {
                	  pst.setString(1, riskOrg.getId());
                      pst.setString(2, riskOrg.getRisk().getId());
                      pst.setString(3, riskOrg.getType());
                      pst.setString(4, riskOrg.getSysOrganization().getId());
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量编辑风险
	 * */
	@Transactional
    public void editRisk(final ArrayList<Risk> riskSaveList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " update t_rm_risks set DELETE_ESTATUS=?, CREATE_TIME=?, LAST_MODIFY_TIME=?, PARENT_ID=?, " +
                		" ID_SEQ=?, ELEVEL=?, IS_LEAF=?, ESORT=?, " +
                		" TEMPLATE_ID=?, is_Use=?, ARCHIVE_STATUS=?, IS_RISK_CLASS=?, risk_name=?, risk_code=?,company_id where id = ? ";
                pst = connection.prepareStatement(sql);
                
                for (Risk risk : riskSaveList) {
                	  pst.setString(1, risk.getDeleteStatus());
                      pst.setTimestamp(2, new java.sql.Timestamp(risk.getCreateTime().getTime()));
                      pst.setTimestamp(3, new java.sql.Timestamp(risk.getLastModifyTime().getTime()));
                      if(null != risk.getParent()){
                    	  pst.setString(4, risk.getParent().getId());
                      }else{
                    	  pst.setString(4, null);
                      }
                      pst.setString(5, risk.getIdSeq());
                      pst.setInt(6, risk.getLevel());
                      if(risk.getIsLeaf()){
                    	  pst.setString(7, "1");
                      }else{
                    	  pst.setString(7, "0");
                      }
                      pst.setInt(8, risk.getSort());
                      if(null != risk.getTemplate()){
                    	  pst.setString(9, risk.getTemplate().getId());
                      }else{
                    	  pst.setString(9, null);
                      }
                      pst.setString(10, risk.getIsUse());
                      pst.setString(11, risk.getArchiveStatus());
                      pst.setString(12, risk.getIsRiskClass());
                      pst.setString(13, risk.getName());
                      pst.setString(14, risk.getCode());
                      pst.setString(15, risk.getCompany().getId());
                      pst.setString(16, risk.getId());
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量添加风险
	 * */
	@Transactional
    public void saveRisk(final ArrayList<Risk> riskSaveList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into t_rm_risks (DELETE_ESTATUS, CREATE_TIME, LAST_MODIFY_TIME, PARENT_ID, ID_SEQ, ELEVEL, IS_LEAF, ESORT, " +
                		" TEMPLATE_ID, is_Use, ARCHIVE_STATUS, IS_RISK_CLASS, id, risk_name, risk_code, company_id,SCHM,CREATE_ORG_ID) " +
                		"value (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (Risk risk : riskSaveList) {
                	  pst.setString(1, risk.getDeleteStatus());
                      pst.setTimestamp(2, new java.sql.Timestamp(risk.getCreateTime().getTime()));
                      pst.setTimestamp(3, new java.sql.Timestamp(risk.getLastModifyTime().getTime()));
                      if(null != risk.getParent()){
                    	  pst.setString(4, risk.getParent().getId());
                      }else{
                    	  pst.setString(4, null);
                      }
                      pst.setString(5, risk.getIdSeq());
                      pst.setInt(6, risk.getLevel());
                      if(risk.getIsLeaf()){
                    	  pst.setString(7, "1");
                      }else{
                    	  pst.setString(7, "0");
                      }
                      pst.setInt(8, risk.getSort());
                      if(null != risk.getTemplate()){
                    	  pst.setString(9, risk.getTemplate().getId());
                      }else{
                    	  pst.setString(9, null);
                      }
                      pst.setString(10, risk.getIsUse());
                      pst.setString(11, risk.getArchiveStatus());
                      pst.setString(12, risk.getIsRiskClass());
                      pst.setString(13, risk.getId());
                      pst.setString(14, risk.getName());
                      pst.setString(15, risk.getCode());
                      pst.setString(16, risk.getCompany().getId());
					 pst.setString(17, risk.getSchm());
					if(risk.getCreateOrg()!=null){
						pst.setString(18,risk.getCreateOrg().getId());
					}else{
						pst.setString(18, "");
					}
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量添加风险记录
	 * */
	@Transactional
    public void saveRiskAdjustHistory(final ArrayList<RiskAdjustHistory> riskAdjustHistoryList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_risk_adjust_history " +
                		"(id,risk_id,risk_status,adjust_type,adjust_time,is_latest," +
                		"etrend,time_period_id,assessement_status,company_id,calculate_formula) value(?,?,?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (RiskAdjustHistory riskAdjustHistory : riskAdjustHistoryList) {
                	  pst.setString(1, riskAdjustHistory.getId());
                      pst.setString(2, riskAdjustHistory.getRisk().getId());
                      pst.setDouble(3, riskAdjustHistory.getStatus());
                      pst.setString(4, riskAdjustHistory.getAdjustType());
                      pst.setTimestamp(5, new java.sql.Timestamp(riskAdjustHistory.getAdjustTime().getTime()));
                      pst.setString(6, riskAdjustHistory.getIsLatest());
                      pst.setString(7, riskAdjustHistory.getEtrend());
                      pst.setString(8, null);//riskAdjustHistory.getTimePeriod().getId());
                      pst.setString(9, riskAdjustHistory.getAssessementStatus());
                      pst.setString(10, riskAdjustHistory.getCompany().getId());
                      pst.setString(11, riskAdjustHistory.getCalculateFormula());
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量保存定性评估结果
	 * @param adjustHistoryResultArrayList 风险记录打分实体集合
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveAdjustHistorySql(final List<Object> adjustHistoryResultArrayList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "INSERT INTO t_rm_adjust_history_result (ID, ADJUST_HISTORY_ID, SCORE, ASSESS_PLAN_ID, SCORE_DIM_ID) " +
                		"VALUES (?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (Object object : adjustHistoryResultArrayList) {
                	AdjustHistoryResult  adjustHistoryResult = (AdjustHistoryResult)object;
                	pst.setString(1, adjustHistoryResult.getId());
                	pst.setString(2, adjustHistoryResult.getAdjustHistoryId());
                	pst.setString(3, adjustHistoryResult.getScore());
                	pst.setString(4, null);
                	pst.setString(5, adjustHistoryResult.getDimension().getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量保存风险应对措施
	 * @param responseSaveList
	 */
	@Transactional
    public void saveRiskResponse(final ArrayList<RiskResponse> responseSaveList) {
        this.o_riskAdjustHistoryDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into t_rm_risk_response (id, RISK_ID, EDIT_IDEA_CONTENT) " +
                		"value (?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (RiskResponse response : responseSaveList) {
                	  pst.setString(1, response.getId());
                	  pst.setString(2, response.getRiskId());
                	  pst.setString(3, response.getEditIdeaContent());
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
}