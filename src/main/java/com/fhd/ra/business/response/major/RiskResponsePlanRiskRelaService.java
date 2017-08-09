package com.fhd.ra.business.response.major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Organization;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.dao.assess.riskTidy.AdjustHistoryResultDAO;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.response.RiskResponseDAO;
import com.fhd.dao.response.major.RiskResponsePlanRiskRelaDAO;
import com.fhd.dao.risk.DimensionDAO;
import com.fhd.dao.risk.HistoryEventDAO;
import com.fhd.dao.risk.HistoryEventRelaOthersDAO;
import com.fhd.dao.risk.KpiAdjustHistoryDAO;
import com.fhd.dao.risk.KpiRelaRiskDAO;
import com.fhd.dao.risk.OrgAdjustHistoryDAO;
import com.fhd.dao.risk.ProcessAdjustHistoryDAO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.dao.risk.RiskRelaRiskDAO;
import com.fhd.dao.risk.StrategyAdjustHistoryDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.response.major.RiskResponsePlanRiskRela;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.TempSyncRiskrbsScore;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.AdjustHistoryResultBO;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.business.assess.oper.KpiAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.OrgAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.StrategyAdjustHistoryBO;
import com.fhd.ra.business.assess.oper.TempSyncRiskRbsBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.assess.quaassess.QuaAssessNextBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.assess.summarizing.util.KpiOperBO;
import com.fhd.ra.business.assess.summarizing.util.OrgOperBO;
import com.fhd.ra.business.assess.summarizing.util.ProcessureOperBO;
import com.fhd.ra.business.assess.summarizing.util.RiskRbsOperBO;
import com.fhd.ra.business.assess.summarizing.util.StrategyMapOperBO;
import com.fhd.ra.business.assess.util.RiskLevelBO;
import com.fhd.ra.business.risk.DimensionBO;
import com.fhd.ra.business.risk.KpiRelaRiskBO;
import com.fhd.ra.business.risk.ProcessAdjustHistoryBO;
import com.fhd.ra.business.risk.ProcessRelaRiskBO;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.ra.web.controller.response.major.utils.JacksonUtilofNullToBlank;
import com.fhd.ra.web.controller.response.major.utils.Tasker;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sys.business.assess.WeightSetBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.dic.DictEntryRelationBO;
import com.fhd.sys.business.organization.OrgGridBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

/**
 * 应对计划、重大风险、部门、创建人表service
 * @author Jzq
 *
 */
@Service
public class RiskResponsePlanRiskRelaService {

	private static final Logger log = LoggerFactory.getLogger(RiskResponsePlanRiskRelaService.class);
	@Autowired
	private RiskResponsePlanRiskRelaDAO riskResponsePlanRiskRelaDAO;
	@Autowired
	private RiskDAO o_riskDAO;
	@Autowired
	private TempSyncRiskRbsBO tempSyncRiskRbsBO;
	
	@Autowired
	private ShowRiskTidyBO showRiskTidyBO;
	
    @Autowired
    private OrgGridBO o_orgGridBO;
    @Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
    @Autowired
	private JBPMBO o_jbpmBO;
    @Autowired
    private RiskScoreBO o_riskScoreBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired
	private OrganizationBO orgService;
	private static final String flowKey = "majorRiskResponseMoreMain";
	/**
	 * 插入
	 * @param riskResponsePlanRiskRela
	 */
	@Transactional
	public void save(RiskResponsePlanRiskRela riskResponsePlanRiskRela)throws Exception{
		this.riskResponsePlanRiskRelaDAO.merge(riskResponsePlanRiskRela);
	}
	/**
	 * 批量插入
	 * @param list
	 */
	public void saveBatch(final List<RiskResponsePlanRiskRela> list)throws Exception{
		 this.riskResponsePlanRiskRelaDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_plan_risk_rela(id,plan_id,risk_id,dept_id,dept_type,update_user) values(?,?,?,?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponsePlanRiskRela obj : list) {
	                	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getPlan().getId());
	                      pst.setString(3, obj.getMajorRisk().getId());
	                      pst.setString(4, obj.getDept().getId());
	                      pst.setString(5, obj.getDeptType());
	                      pst.setString(6, obj.getUpdateUser().getId());
	                      pst.addBatch();
					}
	                pst.executeBatch();
	            }
	        });
	}
	/**
	 * 更新
	 * @param riskResponsePlanRiskRela
	 */
	@Transactional
	public void update(RiskResponsePlanRiskRela riskResponsePlanRiskRela)throws Exception{
		this.riskResponsePlanRiskRelaDAO.merge(riskResponsePlanRiskRela);
	}
	/**
	 * 删除
	 * @param riskResponsePlanRiskRela
	 */
	@Transactional
	public void delete(RiskResponsePlanRiskRela riskResponsePlanRiskRela)throws Exception{
		this.riskResponsePlanRiskRelaDAO.delete(riskResponsePlanRiskRela);
	}
	@Transactional
	public void deleteByPlanId(final List<String> list)throws Exception{
		this.riskResponsePlanRiskRelaDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_plan_risk_rela where plan_id = ?";
                pst = connection.prepareStatement(sql);
                for (String planId : list) {
                	pst.setString(1,planId);
                	pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
	}
	
	/**
	 * 获取全部列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskResponsePlanRiskRela> findList(){
		Criteria criteria = this.riskResponsePlanRiskRelaDAO.createCriteria();
		return criteria.list();
	
	}
	@SuppressWarnings("unchecked")
	public List<RiskResponsePlanRiskRela> findListByPlanId(String planId){
		Criteria criteria = this.riskResponsePlanRiskRelaDAO.createCriteria();
		criteria.createAlias("plan", "plan");
		criteria.add(Restrictions.eq("plan.id", planId));
		return criteria.list();
	
	}
	
	public RiskResponsePlanRiskRela findOne(RiskResponsePlanRiskRela entity){
		Criteria criteria = this.riskResponsePlanRiskRelaDAO.createCriteria();
		if(entity.getPlan() != null){
			criteria.createAlias("plan", "plan");
			criteria.add(Restrictions.eq("plan.id", entity.getPlan().getId()));
		}
		if(entity.getDept() != null){
			criteria.createAlias("dept", "dept");
			criteria.add(Restrictions.eq("dept.id", entity.getDept().getId()));
		}
		if(entity.getMajorRisk() != null){
			criteria.createAlias("majorRisk", "majorRisk");
			criteria.add(Restrictions.eq("majorRisk.id", entity.getMajorRisk().getId()));
		}
		if(entity.getDeptType() != null && !"".equals(entity.getDeptType())){
			criteria.add(Restrictions.eq("deptType", entity.getDeptType()));
		}
		return (RiskResponsePlanRiskRela)criteria.uniqueResult();
	}
	
	/**  
	* @Title: getRiskTreeRecord  
	* @Description: 获取重大风险tree
	* @param id
	* @param query
	* @param subCompany
	* @param showLight
	* @param checkable
	* @param checkedIds
	* @param onlyLeaf
	* @param showAllUsed
	* @param schm
	* @return List<Map<String,Object>>
	* @throws  
	*/
	public List<Map<String, Object>> getRiskTreeRecord(String id, String query, Boolean subCompany, 
			Boolean showLight, Boolean checkable, String checkedIds,Boolean onlyLeaf,Boolean showAllUsed,String schm,Boolean leaf){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		
		Criteria criteria = o_riskDAO.createCriteria();
		Set<String> idSet = queryObjectBySearchName(query);
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
		criteria.add(Restrictions.eq("isRiskClass", "RBS"));
		criteria.add(Restrictions.eq("schm", schm));
		if("dept".equals(schm)){
			SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
			String seq = org.getOrgseq();
			String deptId = seq.split("\\.")[2];//部门编号
			
			criteria.add(Restrictions.eq("createOrg.id", deptId));
		}
		criteria.addOrder(Order.asc("sort"));
		List<Risk> list = criteria.list();

		List<String> ids = new ArrayList<String>();	//保存显示的节点id数组
		for (Risk risk : list) {
			if(!idSet.contains(risk.getId())){
				continue;
			}
			ids.add(risk.getId());
			map = new HashMap<String, Object>();
			map.put("id", risk.getId());
			map.put("text", risk.getName());
			map.put("code", risk.getCode());
			map.put("name", risk.getName());
			//展示出不同的灯图标
			if(showLight){
				String iconCls = "icon-ibm-symbol-0-sm";	//分为高中低，默认中
				String assessPlanId = null;
				RiskAssessPlan riskAssessPlan = risk.getRiskAssessPlan();
				if(riskAssessPlan != null){
					 assessPlanId = risk.getRiskAssessPlan().getId();
				}
				TempSyncRiskrbsScore riskScore = tempSyncRiskRbsBO.getRiskScoreByRiskIdAndAssessPlanId(risk.getId(), assessPlanId);
				if(riskScore != null){
					map.put("iconCls", showRiskTidyBO.getRiskIconByRiskScore(riskScore.getRiskScore(),companyId));
				}else{
					map.put("iconCls", iconCls);
				}
			}
			nodes.add(map);
		}
		
		//添加是否为叶子节点(这块避免单个取节点造成的嵌套查询)
		Map<String,Integer> riskMap = getRiskNumInRisk(ids);
		for(Map node : nodes){
			//设置叶子节点
			Boolean isLeaf = leaf;
			Integer count = riskMap.get(node.get("id"));
			if(count!=null && count>0 && leaf == false){
				isLeaf = false;
			}
			node.put("leaf", isLeaf);
			//设置复选框
			if(checkable) {
				if(onlyLeaf && !isLeaf){	//只有叶子节点才加复选框
					
				}else{
					if(StringUtils.isNotEmpty(checkedIds)){
						for (String choose : StringUtils.split(checkedIds,",")) {
							if(node.get("id").toString().equals(choose)){
								node.put("checked", true);
								break;
							}
							node.put("checked", false);
						}
					}else{
						node.put("checked", false);
					}
				}
			}
		}
		
		return nodes;
	}
	/**
	 * 获取风险分类下风险的个数
	 * 有可能传入的ids有4个，传出的map只有3个key.因为这个id并没有子节点
	 * @author zhengjunxiang
	 * @return
	 */
	private Map<String,Integer> getRiskNumInRisk(List<String> ids){
		Map<String,Integer> riskMap = new HashMap<String,Integer>();
		if(ids.size()==0){	//数组为空
			return riskMap;
		}
		
		List<String> id = new ArrayList<String>();
		for(int i=0;i<ids.size();i++){
			id.add(ids.get(i).toString());
		}
		
		StringBuffer sql = new StringBuffer();
		String companyId = UserContext.getUser().getCompanyid();
		Map<String,Object> mapParams = new HashMap<String,Object>();
		sql.append("SELECT parent_id,count(1)")
		.append(" FROM t_rm_risks r")
		.append(" WHERE r.is_risk_class = 'rbs' and r.delete_estatus = '1' and r.company_id = :companyId and r.parent_id in(:id)")
		.append(" GROUP BY r.parent_id");
		mapParams.put("companyId", companyId);
		mapParams.put("id", id);
		List<Object[]> list = o_riskDAO.createSQLQuery(sql.toString(),mapParams).list();
		
  		for(Object[] o : list){
  			riskMap.put(o[0].toString(), Integer.parseInt(o[1].toString()));
  		}
  		
  		return riskMap;
	}
	@SuppressWarnings("unchecked")
	protected Set<String> queryObjectBySearchName(String query){
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_riskDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
		}
		String companyId = UserContext.getUser().getCompanyid();
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("isRiskClass", "RBS"));
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
	* @Title: compareTreeAndGridData  
	* @Description:  比较重大风险树与计划下风险，包含在RiskResponsePlanRiskRela对应的表中的，树应该被选中 checked = true
	* @param list
	* @param planId
	* @return List<Map<String,Object>>
	* @throws  
	*/
	 public List<Map<String,Object>> compareTreeAndGridData(List<Map<String,Object>> list){
		List<RiskResponsePlanRiskRela> gridList = this.findList();
		if(gridList != null && gridList.size()>0){
			for(Map<String,Object> map :list){
				String riskId = map.get("id").toString();
				for(RiskResponsePlanRiskRela relaObj :gridList){
					String majorRiskId = relaObj.getMajorRisk().getId();
					if(riskId.equals(majorRiskId) ){
						map.put("checked", true);
						break;
					}
				}
			}
		}
		return list;
	}	
	 
	/**  
	* @Title: saveRiskAndPlanRela  
	* @Description: 保存风险计划部门表，并且启动流程
	* @param param [{planId:"",riskId:"",detp:"",deptType:""}]
	* @param approverId [{id:""}]  审批人
	* @return Map<String,Object>
	* @throws  
	*/
	 public Map<String,Object> saveRiskAndPlanRela(String param,String approverId){
		 log.debug("################  approverId:"+approverId);
			Map<String,Object> retMap = new HashMap<String,Object>();
			String executionId = "";
			List<Map<String,Object>> approverIdList = JacksonUtilofNullToBlank.toCollection(approverId, new TypeReference<List<Map<String,Object>>>() {});
			//审批人
			String _appriverId = approverIdList.get(0).get("id").toString();
			//计划id，作为工作流的业务id
			String businessId = "";
			List<Map<String,Object>> list = JacksonUtilofNullToBlank.toCollection(param, new TypeReference<List<Map<String,Object>>>() {});
			List<RiskResponsePlanRiskRela> paraList = new ArrayList<RiskResponsePlanRiskRela>();
			List<String> planIdList = new ArrayList<String>();
			List<String> deptIdList = new ArrayList<String>();
			for(Map<String,Object> map :list){
				String id = Identities.uuid().replaceAll("-", "");
				String planId = map.get("planId").toString();
				businessId = planId;
				if(!planIdList.contains(planId)){
					planIdList.add(planId);
				}
				String riskId = map.get("riskId").toString();
				String deptId = map.get("deptId").toString();
				String deptType = map.get("deptType").toString();
				String currentEmpId = UserContext.getUser().getEmpid();
				
				if(!deptIdList.contains(deptId)){
					deptIdList.add(deptId);
				}
				RiskResponsePlanRiskRela  riskResponsePlanRiskRela = new RiskResponsePlanRiskRela();
				RiskAssessPlan planObj = new RiskAssessPlan(planId);
				riskResponsePlanRiskRela.setId(id);
				riskResponsePlanRiskRela.setPlan(planObj);
				riskResponsePlanRiskRela.setMajorRisk(new Risk(riskId));
				riskResponsePlanRiskRela.setDept(new SysOrganization(deptId));
				riskResponsePlanRiskRela.setDeptType(deptType);
				riskResponsePlanRiskRela.setUpdateUser(new SysEmployee(currentEmpId));
				paraList.add(riskResponsePlanRiskRela);
				RiskResponsePlanRiskRela deleteObj = new RiskResponsePlanRiskRela();
				deleteObj.setPlan(planObj);
			}
			boolean flag = false;
			try {
				this.deleteByPlanId(planIdList);
				this.saveBatch(paraList);
				RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(businessId);
				if(null != assessPlan){
					assessPlan.setStatus(Contents.STATUS_SUBMITTED);//计划状态
					assessPlan.setDealStatus(Contents.DEAL_STATUS_HANDLING);//计划处理状态
					// 启动工作流
					Map<String,Object> variables = new HashMap<String,Object>();
					variables.put("planDirectorApproval", _appriverId);
					variables.put("businessId", businessId);
					variables.put("entityType", flowKey);
					variables.put("AssessPlanApproverEmpId", _appriverId);
					variables.put("id", businessId);
					variables.put("name", assessPlan.getPlanName());
					variables.put("companyId", UserContext.getUser().getCompanyid());
					variables.put("planMaker", UserContext.getUser().getEmpid());
					
					String rodeCode = "DeptRiskManager";//部门风险管理员角色编号
					StringBuffer noRiskManagerDeptNames = new StringBuffer();
					for(String _deptId :deptIdList){
						SysEmployee deptRiskManager = o_riskScoreBO.findEmpsByRoleIdAnddeptId(_deptId, rodeCode);//部门风险管理人员
						if(null == deptRiskManager){
							SysOrganization org = this.orgService.findById(_deptId);
							noRiskManagerDeptNames.append(org.getOrgname());
							noRiskManagerDeptNames.append(",");
						}
					}
					if(noRiskManagerDeptNames.length()>0){
						flag = false;
						noRiskManagerDeptNames.setLength(noRiskManagerDeptNames.length()-1);
						retMap.put("result", noRiskManagerDeptNames.toString());
					}else{
						//计划制定节点任务执行
						executionId = o_jbpmBO.startProcessInstance(flowKey,variables);
						o_jbpmBO.doProcessInstance(executionId, variables);
						flag = true;
					}
					
				}
				
			} catch (Exception e) {
				flag = false;
			}
			retMap.put("success", flag);
			return retMap;
	 }
	 /**  
	* @Title: majorRiskPlanApprove  
	* @Description: 重大风险应对计划 主管、领导审批
	* @param businessId 计划id
	* @param executionId 流程执行id
	* @param isPass  yes or no
	* @param examineApproveIdea   审批处理意见
	* @param approverId 审批人
	* @param approverKey 审批流程变量key 区别主管与领导
	* @return Map<String,Object>
	* @throws  
	*/
	public Map<String,Object> majorRiskPlanApprove(String businessId,
			 String executionId,
			 String isPass,
			 String examineApproveIdea,
			 String approverId,
			 String approverKey){
		 Map<String,Object> map = new HashMap<String,Object>();
		 boolean flag = false;
		 RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		 if(assessPlan != null){
			 Map<String, Object> variables = new HashMap<String, Object>();
			 variables.put("path", isPass);
			 variables.put("examineApproveIdea", examineApproveIdea);
			 if("yes".equals(isPass)){
				 variables.put(approverKey, approverId);
				 //部门领导审批通过，则到任务分配阶段
				 if("deptRiskManager".equals(approverKey)){
					 String subKeyOne = "startuserid";
					 //任务分配阶段办理人就是部门风险管理员
					 variables.put(subKeyOne, approverId);
					 List<RiskResponsePlanRiskRela> list = this.findListByPlanId(businessId);
					 List<Tasker> taskers = new ArrayList<Tasker>();
					 if(list != null && list.size()>0){
						 for(RiskResponsePlanRiskRela obj :list){
							 String deptId = obj.getDept().getId();
							 String riskId = obj.getMajorRisk().getId();
							 String deptType = obj.getDeptType();
							 String rodeCode = "DeptRiskManager";//部门风险管理员角色编号
							 SysEmployee deptRiskManager = o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId, rodeCode);//部门风险管理人员
							 /*每个部门必须有风险管理员，否则不能发起计划，则此处不需要判断
							  * if(null == deptRiskManager){
								 deptRiskManager = UserContext.getUser().getEmp();
							 }*/
							 Tasker tasker = new Tasker();
							 tasker.setBusinessId(businessId);
							 tasker.setDeptRiskManager(deptRiskManager.getId());
							 tasker.setName(assessPlan.getPlanName());
							 tasker.setDeptId(deptId);
							 tasker.setDeptType(deptType);
							 tasker.setRiskId(riskId);
							 taskers.add(tasker);
						 }
						 variables.put("taskers", taskers);
						 variables.put("joinCount", taskers.size());
						 //结果汇总代办人就是当前给各个部门的风险管理员下发任务的人
						 variables.put("officeSummary", approverId);
					 }
				 }
			 }
			 this.o_jbpmBO.doProcessInstance(executionId, variables);
			 flag = true;
		 }
		 
		 map.put("success", flag);
		 return map;
	 }
	
	/**  
	* @Title: findPlanRiskRelaListByPlanIdAndDeptId  
	* @Description: 通过计划id和部门id获取计划与风险的关系表进而获取执行对象
	* @param planId
	* @param deptId
	* @return List<RiskResponsePlanRiskRela>
	* @throws  
	*/
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findPlanRiskRelaListByPlanIdAndDeptId(String planId,String deptId,String empType,String _riskId){
		StringBuffer sql = new StringBuffer();
        sql.append(" SELECT a.RISK_ID, risk.RISK_NAME, d.id AS schemeId, d.DESCRIPTION, org.ID AS orgId, b.EMP_ID, emp.EMP_NAME, org.ORG_NAME,b.ID as taskExecutionId,a.PLAN_ID as planId FROM t_rm_response_plan_risk_rela a LEFT JOIN t_rm_response_task_execution b ON a.ID = b.PLAN_RISK_RELA_ID LEFT JOIN t_rm_response_risk_scheme_rela c ON c.TASK_EXECUTION_ID = b.ID LEFT JOIN t_rm_response_scheme d ON c.SCHEME_ID = d.ID LEFT JOIN t_sys_employee emp ON emp.ID = b.EMP_ID LEFT JOIN t_sys_organization org ON org.ID = a.DEPT_ID LEFT JOIN t_rm_risks risk ON risk.ID = a.RISK_ID WHERE a.PLAN_ID = :planId AND a.DEPT_ID = :deptId AND b.EMP_TYPE = :empType AND c.SCHEME_ID !='' and a.RISK_ID = :riskId ");
        SQLQuery sqlQuery = this.riskResponsePlanRiskRelaDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        sqlQuery.setParameter("deptId", deptId);
        sqlQuery.setParameter("empType", empType);
        sqlQuery.setParameter("riskId", _riskId);
		List<Object[]> list = sqlQuery.list();
		List<Map<String,Object>> retList = new ArrayList<Map<String,Object>>();
		for(Object[] o : list){
			Map<String,Object> map = new HashMap<String,Object>();
			String riskId = o[0].toString();
			String riskName = o[1].toString();
			String schemeId = o[2].toString();
			String description = o[3].toString();
			String orgId = o[4].toString();
			String empId = o[5].toString();
			String empName = o[6].toString();
			String orgName = o[7].toString();
			String taskExecutionId = o[8].toString();
			String _planId = o[9].toString();
			map.put("riskId", riskId);
			map.put("riskName", riskName);
			map.put("schemeId", schemeId);
			map.put("description", description);
			map.put("orgId", orgId);
			map.put("empId", empId);
			map.put("empName", empName);
			map.put("orgName", orgName);
			map.put("taskExecutionId", taskExecutionId);
			map.put("planId", _planId);
			retList.add(map);
		}
		return retList;
	
	}
	/**  
	* @Title: findPlanRiskRelaListByPlanIdAndDeptId  
	* @Description: 加载该计划下所有部门汇总的方案
	* @param planId
	* @param empType
	* @return List<Map<String,Object>>
	* @throws  
	*/
	public List<Map<String,Object>> findPlanRiskRelaListByPlanIdAndDeptId(String planId,String empType){
		StringBuffer sql = new StringBuffer();
        sql.append(" SELECT a.RISK_ID, risk.RISK_NAME, d.id AS schemeId, d.DESCRIPTION, org.ID AS orgId, b.EMP_ID, emp.EMP_NAME, org.ORG_NAME,b.ID as taskExecutionId,a.PLAN_ID as planId FROM t_rm_response_plan_risk_rela a LEFT JOIN t_rm_response_task_execution b ON a.ID = b.PLAN_RISK_RELA_ID LEFT JOIN t_rm_response_risk_scheme_rela c ON c.TASK_EXECUTION_ID = b.ID LEFT JOIN t_rm_response_scheme d ON c.SCHEME_ID = d.ID LEFT JOIN t_sys_employee emp ON emp.ID = b.EMP_ID LEFT JOIN t_sys_organization org ON org.ID = a.DEPT_ID LEFT JOIN t_rm_risks risk ON risk.ID = a.RISK_ID WHERE a.PLAN_ID = :planId AND b.EMP_TYPE = :empType AND c.SCHEME_ID !='' ");
        SQLQuery sqlQuery = this.riskResponsePlanRiskRelaDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        sqlQuery.setParameter("empType", empType);
		List<Object[]> list = sqlQuery.list();
		List<Map<String,Object>> retList = new ArrayList<Map<String,Object>>();
		for(Object[] o : list){
			Map<String,Object> map = new HashMap<String,Object>();
			String riskId = o[0].toString();
			String riskName = o[1].toString();
			String schemeId = o[2].toString();
			String description = o[3].toString();
			String orgId = o[4].toString();
			String empId = o[5].toString();
			String empName = o[6].toString();
			String orgName = o[7].toString();
			String taskExecutionId = o[8].toString();
			String _planId = o[9].toString();
			map.put("riskId", riskId);
			map.put("riskName", riskName);
			map.put("schemeId", schemeId);
			map.put("description", description);
			map.put("orgId", orgId);
			map.put("empId", empId);
			map.put("empName", empName);
			map.put("orgName", orgName);
			map.put("taskExecutionId", taskExecutionId);
			map.put("planId", _planId);
			retList.add(map);
		}
		return retList;
	
	}
}
