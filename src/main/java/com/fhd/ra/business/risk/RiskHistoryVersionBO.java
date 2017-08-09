package com.fhd.ra.business.risk;

import com.fhd.dao.assess.riskTidy.AdjustHistoryResultDAO;
import com.fhd.dao.risk.DimensionDAO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskHistoryVersionDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.dao.risk.RiskVersionDAO;
import com.fhd.entity.assess.riskTidy.AdjustHistoryResult;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskHistoryVersion;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.risk.RiskVersion;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.web.form.risk.RiskHistoryVersionForm;
import com.fhd.sys.business.organization.OrgGridBO;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RiskHistoryVersionBO {
	@Autowired
	private RiskVersionDAO o_riskVersionDAO;
	
	@Autowired
	private RiskHistoryVersionDAO o_riskHistoryVersionDAO;
	
	@Autowired
	private RiskCmpBO o_riskCmpBO;
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private DimensionDAO o_dimensionDAO;
	
	//打分表
	@Autowired
	private AdjustHistoryResultDAO o_adjustHistoryResultDAO;
	
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	@Autowired
    private OrgGridBO o_orgGridBO;
	
	/**
	 * 创建公司的风险库历史副本
	 * 创建风险版本，同时将当前风险库中的所有风险和事件保存成到这个新版本中
	 * @author zhengjunxiang 
	 * @param risiVersion
	 * @param companyId 公司id
	 * @param createBy	创建人
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean saveVersion(final RiskVersion riskVersion,String companyId,final String createBy){
		o_riskVersionDAO.merge(riskVersion);
		
		//查询当前风险库下的所有风险和风险事件
		final List<Risk> riskList = this.findRiskAndEventByCompany(companyId);
		
		//获取风险的最新历史记录
		final String[] riskIdArr = new String[riskList.size()];
		for(int i=0;i<riskList.size();i++){
			riskIdArr[i] = riskList.get(i).getId();
		}
  		final Map<String,Object> historyMap = this.findLatestRiskAdjustHistoryByRiskIds(riskIdArr);
  		final Map<String,Object> orgMap = this.findDutyAndRelativeDepartmentByRiskIds(riskIdArr);
		
		o_riskHistoryVersionDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException  {
                connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into T_RM_HISTORYVERSION ( ID , VERSION_ID , RISK_ID , COMPANY_ID , IS_RISK_CLASS , RISK_CODE , RISK_NAME , EDESC , PARENT_ID , PARENT_NAME , ELEVEL , ID_SEQ , IS_LEAF , ESORT , CREATE_TIME , CREATE_BY , DUTY_DEPARTMENT , RELATIVE_DEPARTMENT , DUTY_EMPLOYEE , RELATIVE_EMPLOYEE ," +
                		" INFLUENCE_KPI , RISK_KPI , INFLUENCE_PROCESS , CONTROL_PROCESS , HISTORY_ID , TEMPLATE_ID , PROBABILITY , INFLUENCE_DEGREE , RISK_SCORE , RISK_STATUS , SOURCE , DELETE_ESTATUS , CREATE_ORG_ID , LAST_MODIFY_TIME , RELATIVE_DEPARTMENT_IDS , DUTY_DEPARTMENT_IDS , LAST_MODIFY_BY ) " +
                		"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                for(Risk r : riskList) {
                    pst.setString(1, UUID.randomUUID().toString());
                    pst.setString(2, riskVersion.getId());
                    pst.setString(3, r.getId());
                    if(null != r.getCompany().getCompany()){
                    	pst.setString(4, r.getCompany().getCompany().getId());
                    }else if(null != r.getCompany()){
                    	pst.setString(4, r.getCompany().getId());
                    }else{
                    	pst.setString(4, UserContext.getUser().getCompanyid());
                    }
                    pst.setString(5, r.getIsRiskClass());
                    pst.setString(6, r.getCode());
                    pst.setString(7, r.getName());
                    pst.setString(8, r.getDesc());
                    pst.setString(9, null);
                    pst.setString(10, r.getParent()==null?"":r.getParent().getName());
                    pst.setObject(11, r.getLevel()==null?null:r.getLevel());
                    pst.setString(12, r.getIdSeq());
                    pst.setBoolean(13, r.getIsLeaf());
                    pst.setObject(14, r.getSort());
                    Date date = new Date();
                    pst.setDate(15, new java.sql.Date(date.getTime()));
                    pst.setString(16, createBy);
                    
                    //风险的责任部门和相关部门
                    Map<String,Object> dutyDepartmentMap = (Map<String,Object>)orgMap.get("dutyDepartment");        //责任部门和人
                    Map<String,Object> relativeDepartmentMap = (Map<String,Object>)orgMap.get("relativeDepartment");//相关部门和人
                    StringBuffer dutyDepartmentIds = new StringBuffer();
                    StringBuffer dutyDepartment = new StringBuffer();
                    if(dutyDepartmentMap.get(r.getId())!=null){
                        List<SysOrganization> orgList = (List<SysOrganization>)dutyDepartmentMap.get(r.getId());
                        for(int i = 0;i<orgList.size();i++){
                            SysOrganization org = orgList.get(i);
                            if(i==0){
                                dutyDepartmentIds.append(org.getId());
                                dutyDepartment.append(org.getOrgname());
                            }else{
                                dutyDepartmentIds.append(",");
                                dutyDepartmentIds.append(org.getId());
                                dutyDepartment.append(",");
                                dutyDepartment.append(org.getOrgname());
                            }
                        }
                    }
                    pst.setString(17, dutyDepartment.toString());//责任部门
                    
                    String relativeDepartmentIds = "";
                    String relativeDepartment = "";
                    if(relativeDepartmentMap.get(r.getId())!=null){
                        List<SysOrganization> orgList = (List<SysOrganization>)relativeDepartmentMap.get(r.getId());
                        for(int i = 0;i<orgList.size();i++){
                            SysOrganization org = orgList.get(i);
                            if(i==0){
                                relativeDepartmentIds += org.getId();
                                relativeDepartment += org.getOrgname();
                            }else{
                                relativeDepartmentIds += "," + org.getId();
                                relativeDepartment += "," + org.getOrgname();
                            }
                        }
                    }
                    pst.setString(18, relativeDepartment);//相关部门
                    
                    pst.setString(19, "");//责任人
                    pst.setString(20, "");//相关人
                    pst.setString(21, "");//影响指标
                    pst.setString(22, "");//风险指标
                    pst.setString(23, "");//影响流程
                    pst.setString(24, "");//控制流程
                    
                    //评估属性,获取风险的最新记录
                    RiskAdjustHistory adjustHistory = (RiskAdjustHistory)historyMap.get(r.getId());
                    if(adjustHistory!=null){
                        pst.setString(25, adjustHistory.getId());//历史记录id
                        pst.setDouble(29, adjustHistory.getStatus());//风险水平
                        pst.setString(30, adjustHistory.getAssessementStatus());//风险状态
                        pst.setString(31, adjustHistory.getAdjustType());//数据来源
                    }else{
                        pst.setString(25, null);//历史记录id
                        pst.setString(29, null);//风险水平
                        pst.setString(30, null);//风险状态
                        pst.setString(31, null);//数据来源
                    }
                    pst.setString(26, null);
                    pst.setString(27, null);
                    pst.setString(28, null);
                    pst.setString(32, null);
                    pst.setString(33, null);
                    pst.setString(34, null);
                    pst.setString(35, relativeDepartmentIds);//相关部门ids
                    pst.setString(36, dutyDepartmentIds.toString());//责任部门ids
                    pst.setString(37, null);
                    pst.addBatch();
                }               
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }

        });
		
		
		
		return true;
	}
	
	/**
	 * 查询公司下全部风险和风险事件,归档的和没有删除的
	 * 要级联带出责任部门和相关部门等相关信息
	*/
	@SuppressWarnings("unchecked")
	private List<Risk> findRiskAndEventByCompany(String companyId) {
		Criteria criteria = o_riskDAO.createCriteria();
		//关联上级风险
		criteria.createAlias("parent", "parent",CriteriaSpecification.LEFT_JOIN);
		criteria.setFetchMode("parent", FetchMode.SELECT);
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("archiveStatus", Contents.RISK_STATUS_ARCHIVED));
		List<Risk> risks = criteria.list();
		return risks;
	}
	
	/**
	 * 查找风险的历史记录状态和趋势
	 * Map,1个风险对应1个最新的历史记录
	 * 事件列表查询没有使用，直接通过sql将状态和趋势查出来
	 * @param idList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> findLatestRiskAdjustHistoryByRiskIds(String[] ids){
		Map<String,Object> historyMap = new HashMap<String,Object>();
		if(ids.length==0){	//ids数组为空
			return historyMap;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select h.id,h.assessement_status,dict.dict_entry_value,h.risk_id,h.risk_status")
		.append(" from t_rm_risk_adjust_history h,t_sys_dict_entry dict")
		.append(" where h.etrend = dict.id")
		.append(" and h.is_latest='1'")
		.append(" and h.risk_id in (:ids)");
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameterList("ids", ids);
		List<Object[]> list = sqlQuery.list();
			
		RiskAdjustHistory h = null;
  		for(Object[] o : list){
  			h = new RiskAdjustHistory();
  			h.setId(o[0].toString());
  			h.setAssessementStatus(o[1].toString());
  			h.setEtrend(o[2].toString());
  			h.setStatus(o[4]==null?null:Double.parseDouble(o[4].toString()));
  			String riskId = o[3].toString();
  			historyMap.put(riskId, h);
  		}
  		
		return historyMap;
	}
	
	/**
	 * 查询所有的风险版本
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskVersion> findVersionByAll(String companyId,String schm){
		Criteria c = o_riskVersionDAO.createCriteria();
		c.add(Restrictions.eq("companyId", companyId));
		c.add(Restrictions.eq("schm", schm));//分库标志
		if("dept".equals(schm)){
			SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
			String seq = org.getOrgseq();
			String deptId = seq.split("\\.")[2];//部门编号
			
			c.add(Restrictions.eq("createOrg", deptId));//分库标志
		}
		c.addOrder(Order.desc("createTime"));
		List<RiskVersion> resultList = c.list();
		return resultList;
	}
	
	/**
	 * 查询所有的风险版本
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskVersion> findVersionByName(String name){
		Criteria c = o_riskVersionDAO.createCriteria();
		c.add(Restrictions.eq("name", name));
		c.addOrder(Order.desc("createTime"));
		List<RiskVersion> resultList = c.list();
		return resultList;
	}
	
	/**
	 * 查询某公司下风险历史版本下的所有风险
	 * @author zhengjunxiang
	 * @orgIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<RiskHistoryVersionForm> findHistoryVersionByVersion(String versionId,String level,String orgIds,String query){
		List<RiskHistoryVersionForm> resultList = new ArrayList<RiskHistoryVersionForm>();
		Criteria c = o_riskHistoryVersionDAO.createCriteria();
		c.add(Restrictions.eq("version.id", versionId));
		if(StringUtils.isNotEmpty(level)){
			if(level.equals("0")){	//代表全部
				
			}else if(level.equals("-1")){	//代表末级风险事件
				c.add(Restrictions.eq("isRiskClass", "re"));
			}else{					//代表查询几级风险
				c.add(Restrictions.eq("isRiskClass", "rbs"));
				c.add(Restrictions.eq("level", Integer.parseInt(level)));
			}
		}
		if(StringUtils.isNotEmpty(orgIds)){
			String[] orgIdArr = orgIds.split(",");
			if(orgIdArr.length == 1){
				c.add(Restrictions.like("dutyDepartmentIds", orgIds, MatchMode.ANYWHERE));
			}else{
			    StringBuffer sql = new StringBuffer();
			    sql.append("duty_department_ids like '%");
			    sql.append(orgIdArr[0]);
			    sql.append("%'");
				for(int i=1;i<orgIdArr.length;i++){
				    sql.append(" or duty_department_ids like '%");
				    sql.append(orgIdArr[i]);
				    sql.append("%'");
				} 
				c.add(Restrictions.sqlRestriction("("+sql.toString()+")"));
			}
		}
		if (StringUtils.isNotBlank(query)) {
			c.add(Restrictions.like("name", query,MatchMode.ANYWHERE));
		}
		
		c.addOrder(Order.desc("riskScore"));
		List<RiskHistoryVersion> list = (List<RiskHistoryVersion>)c.list();
		if(list!=null){
			for(RiskHistoryVersion rv : list){
				resultList.add(new RiskHistoryVersionForm(rv));
			}
		}
		
		return resultList;
	}
	
	/**
	 * 按部门查找部门下所有风险
	 * @param version 版本id，null为当前版本
	 * @param level	   风险级别
	 * @param orgId  部门id，null为查询所有部门下的风险
	 * @return
	 */
	public List<RiskHistoryVersionForm> findRiskHistoryVersionBySome(String companyId,String version,String level,String orgId,String query,String schm){
		List<RiskHistoryVersionForm> risks = null;
		if(StringUtils.isNotEmpty(version)){
			risks = this.findHistoryVersionByVersion(version,level,orgId,query);
		}else{
			risks = this.findAllRiskByOrgId(companyId, level,orgId, query,schm);
		}
		return risks;
	}
	
	/**
	 * 查询风险当前版本的所有风险和风险事件
	 * 对于没有最新记录的风险，相关值插入空；对应打分维度不是发生可能性和影响维度的情况，查出来的东西也为空
	 * 直接根据风险的历史记录templateId查询维度分值
	 * @param companyId
	 * @param orgId
	 * @param companyId
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<RiskHistoryVersionForm> findAllRiskByOrgId(String companyId,String level,String orgIds,String query,String schm){
		List<RiskHistoryVersionForm> objList = new ArrayList<RiskHistoryVersionForm>();
		Map<String,String> mapParams = new HashMap<String,String>();
		//1.风险基本信息
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct r.id,r.risk_code,r.risk_name,parent.risk_name parentName,r.elevel,h.assessement_status,h.id history_id,h.template_id,h.risk_status,h.ADJUST_TYPE,r.parent_id parentId")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN t_rm_risks parent on parent.id = r.parent_id")
		.append(" LEFT JOIN t_rm_risk_org riskorg on r.id = riskorg.risk_id")
		.append(" LEFT JOIN t_sys_organization org on org.id=riskorg.org_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
		.append(" WHERE r.company_id=:companyId ")
		.append(" and r.delete_estatus='1'");
		mapParams.put("companyId", companyId);
		sql.append(" and r.archive_status='"+Contents.RISK_STATUS_ARCHIVED+"'");
		if(StringUtils.isNotBlank(level)){
			if(level.equals("0")){	//代表全部
				
			}else if(level.equals("-1")){	//代表末级风险事件
				sql.append(" and r.is_risk_class='re'");
			}else{					//代表查询几级风险
				sql.append(" and r.is_risk_class='rbs' and r.elevel = :level ");//and r.is_risk_class='rbs' 
				mapParams.put("level", level);
			}
			
		}
		if(StringUtils.isNotBlank(orgIds)){
			String[] orgIdArr = orgIds.split(",");
			if(orgIdArr.length == 1){
				sql.append(" and org.id_seq like :orgIds ");
				mapParams.put("orgIds", "%."+orgIds+".%");
			}else{
				String sql1 = "org.id_seq like :orgIdArr ";
				mapParams.put("orgIdArr", "%"+orgIdArr[0]+"%");
				for(int i=1;i<orgIdArr.length;i++){
					sql1 += " or org.id_seq like :orgIdArr"+i;
					mapParams.put("orgIdArr"+i, "%"+orgIdArr[i]+"%");
				} 
				sql.append(" and ("+sql1+")");
			}
		}
		if (StringUtils.isNotBlank(query)) {
			sql.append(" and r.risk_name like '%" + query + "%'");
		}
		//schm区分部门还是公司
		 if(null != schm && !"".equals(schm)){
			 sql.append(" and r.schm=:schm");
				if("dept".equals(schm)){//dept部门库CREATE_ORG_ID
					SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
					String seq = org.getOrgseq();
					String deptId = seq.split("\\.")[2];//部门编号
					
					sql.append(" and r.CREATE_ORG_ID='"+deptId+"'");
				}
				mapParams.put("schm", schm);
			}
		//排序
		sql.append(" order by h.risk_status desc");
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
  		List<Object[]> list = sqlQuery.list();
  		String[] riskIdArr = new String[list.size()];		//风险集合
  		List<String> historyIdList = new ArrayList<String>();	//历史记录集合
  		int i = 0;
  		for(Object[] o : list){
  			RiskHistoryVersionForm v = new RiskHistoryVersionForm();
  			
  			String riskId = o[0].toString();
  			riskIdArr[i] = riskId;
  			String historyId = o[6]==null?"":o[6].toString();
  			if(StringUtils.isNotEmpty(historyId)){
  	  			historyIdList.add(historyId);
  			}
  			
  			v.setNum(++i);
  			v.setRiskId(riskId);
  			v.setName(o[2]==null?"":o[2].toString());
  			v.setParentName(o[3]==null?"":o[3].toString());	
  			v.setLevel(o[4]==null?0:Integer.parseInt(o[4].toString()));
  			v.setRiskStatus(o[5]==null?"":o[5].toString());
  			v.setAdjustHistoryId(historyId);
  			v.setTemplateId(o[7]==null?"":o[7].toString());
  			v.setRiskScore(o[8]==null?null:Double.parseDouble(o[8].toString()));
  			v.setAdjustType(o[9]==null?"":o[9].toString());
  			v.setParentId(o[10]==null?"":o[10].toString());
  			objList.add(v);
  		}

  		//2.风险历史记录的发生可能性分值和影响程度分值
		Map<String,AdjustHistoryResult> proMap= this.findDimensionScoreByDimName(historyIdList,"发生可能性");
		Map<String,AdjustHistoryResult> influMap= this.findDimensionScoreByDimName(historyIdList,"影响程度");
		//3.风险的责任部门和相关部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(riskIdArr);
		Map<String,Object> dutyDepartmentMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relativeDepartmentMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//影响部门

		for(RiskHistoryVersionForm hv : objList){
			String hid = hv.getAdjustHistoryId();
			String rid = hv.getRiskId();
			hv.setProbability(proMap.get(hid)==null?"":((AdjustHistoryResult)proMap.get(hid)).getScore());
			hv.setInfluenceDegree(influMap.get(hid)==null?"":((AdjustHistoryResult)influMap.get(hid)).getScore());
			hv.setDutyDepartment(dutyDepartmentMap.get(rid)==null?"":dutyDepartmentMap.get(rid).toString());
			hv.setRelativeDepartment(relativeDepartmentMap.get(rid)==null?"":relativeDepartmentMap.get(rid).toString());
		}
		
        return objList;
	}
	
	/**
	 * 获取特定维度名称的维度分值
	 * @param idList  风险历史记录id集合
	 * @return Map<历史记录id，打分结果对象>
	 */
	@SuppressWarnings("unchecked")
	private Map<String,AdjustHistoryResult> findDimensionScoreByDimName(List<String> idList,String dimName){
		Map<String,AdjustHistoryResult> resultMap = new HashMap<String,AdjustHistoryResult>();
		if(idList.size()==0){
			return resultMap;
		}
		Criteria c = o_adjustHistoryResultDAO.createCriteria();
		c.createAlias("dimension", "dimension");
		c.add(Restrictions.eq("dimension.name", dimName));
		c.add(Restrictions.in("adjustHistoryId", idList));
		c.addOrder(Order.desc("riskAssessPlan.id"));
		List<AdjustHistoryResult> list = (List<AdjustHistoryResult>)c.list();
		for(AdjustHistoryResult result : list){
			resultMap.put(result.getAdjustHistoryId(), result);
		}
		return resultMap;
	}
	
	/**
	 * 查找风险的历史记录
	 * Map:key:dutyDepartment,relativeDepartment   他们的value是一个map，机构如下 key-riskId  value-list<sysorganization>
	 * 1个风险对应多个责任部门和相关部门
	 * @param idList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> findDutyAndRelativeDepartmentByRiskIds(String[] ids){
		Map<String,Object> orgMap = new HashMap<String,Object>();
		Map<String,Object> dutyDepartment = new HashMap<String,Object>();
		Map<String,Object> relativeDepartment = new HashMap<String,Object>();
		if(ids.length==0){	//ids数组为空
			orgMap.put("dutyDepartment", dutyDepartment);
			orgMap.put("relativeDepartment", relativeDepartment);
			return orgMap;
		}
		
		Criteria criteria = o_riskOrgDAO.createCriteria();
		//自动关联风险
		criteria.createAlias("risk", "risk");
		criteria.setFetchMode("risk", FetchMode.SELECT);
		//自动关联部门
		criteria.createAlias("sysOrganization", "sysOrganization");
		criteria.setFetchMode("sysOrganization", FetchMode.SELECT);
		criteria.add(Restrictions.in("risk.id",ids));
		List<RiskOrg> lists = (List<RiskOrg>)criteria.list();
		for(RiskOrg riskOrg : lists){
			String riskId = riskOrg.getRisk().getId();
			if(riskOrg.getType().equalsIgnoreCase(Contents.DUTY_DEPARTMENT)){
				List<SysOrganization> orgList = new ArrayList<SysOrganization>();
				if(dutyDepartment.containsKey(riskId)){
					orgList = (List<SysOrganization>)dutyDepartment.get(riskId);
					orgList.add(riskOrg.getSysOrganization());
					dutyDepartment.put(riskId, orgList);
				}else{
					orgList.add(riskOrg.getSysOrganization());
					dutyDepartment.put(riskId, orgList);
				}
			}
			if(riskOrg.getType().equalsIgnoreCase(Contents.RELATIVE_DEPARTMENT)){
				List<SysOrganization> orgList = new ArrayList<SysOrganization>();
				if(relativeDepartment.containsKey(riskId)){
					orgList = (List<SysOrganization>)relativeDepartment.get(riskId);
					orgList.add(riskOrg.getSysOrganization());
					relativeDepartment.put(riskId, orgList);
				}else{
					orgList.add(riskOrg.getSysOrganization());
					relativeDepartment.put(riskId, orgList);
				}
			}
		}
		orgMap.put("dutyDepartment", dutyDepartment);
		orgMap.put("relativeDepartment", relativeDepartment);
		
		return orgMap;
	}
	
	/**
     * 获取公司下风险分类的最大层数
     * @param valueObj
     * @return
     */
	public int getRiskCatalogDeep(String companyId){
		Criteria c = o_riskDAO.createCriteria();
		c.add(Restrictions.eq("company.id", companyId));
		c.add(Restrictions.eq("isRiskClass", "rbs"));
		c.setProjection(Projections.max("level"));
		int count = Integer.parseInt(c.uniqueResult().toString());
		return count;
	}
}
