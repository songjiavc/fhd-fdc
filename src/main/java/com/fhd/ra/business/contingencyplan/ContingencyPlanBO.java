package com.fhd.ra.business.contingencyplan;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.email.AssessEmailBO;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.sys.business.assess.SendEmailBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class ContingencyPlanBO {

	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	
	@Autowired
	private JBPMBO o_jbpmBO;
	
	@Autowired
	private JBPMOperate o_jbpmOperate;
	
	@Autowired
    private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
    private SendEmailBO o_sendEmailBO;
	
	@Autowired
    private RiskAssessPlanBO o_planBO;
	
	@Autowired
    private AssessEmailBO o_assessEmailBO;
	
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	
	@Autowired
    private ShowRiskTidyBO o_showRiskTidyBO;
	
	@Autowired
    private JBPMOperate o_jBPMOperate;
	
	/**
     * 工作流第一步--评估计划制定，提交
     * @param empIds    承办人id
     * @param approver  审批人
     * @param businessId    评估计划id
     */
    @SuppressWarnings("deprecation")
    @Transactional
    @RecordLog("提交计划")
    public void submitAssessRiskPlanToApprover(String empIds, String approverId, String businessId, 
                                        String executionId, String entityType,String deptEmpId){
        RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(businessId);
        String makePlanEmpId = UserContext.getUser().getEmpid();//制定计划人员id
        String approver = "";
        JSONArray jsonArray = JSONArray.fromObject(approverId);
        if (jsonArray.size() > 0){
             JSONObject jsobj = jsonArray.getJSONObject(0);
             approver = jsobj.getString("id");//审批人
        }
        SysEmployee riskManagemer = o_riskScoreBO.findRiskManagerByCompanyId();
        if(null != assessPlan){
            assessPlan.setStatus(Contents.STATUS_SUBMITTED);
            assessPlan.setDealStatus(Contents.DEAL_STATUS_HANDLING);
            if (!"".equals(approver)) {
                if(StringUtils.isBlank(executionId)){
                    //菜单触发提交开启工作流
                    Map<String, Object> variables = new HashMap<String, Object>();
                    if(!StringUtils.isNotBlank(entityType)){
                        entityType = "contingencyPlanTotal";//流程名称
                    }
                    
                    entityType = "contingencyPlanTotal";
                    
                    variables.put("entityType", entityType);
                    variables.put("AssessPlanApproverEmpId", approver);//审批人
                    variables.put("id", businessId);
                    variables.put("name", assessPlan.getPlanName());//评估计划名称
                    variables.put("empIds", empIds);
                    variables.put("companyId", UserContext.getUser().getCompanyid());
                    variables.put("riskManagemer", null!=riskManagemer?riskManagemer.getId():"");//该公司的风险管理员
                    variables.put("makePlanEmpId", makePlanEmpId);
                    executionId = o_jbpmBO.startProcessInstance(entityType,variables);//开启工作流
                    //工作流提交
                    Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
                    variablesBpmTwo.put("AssessPlanApproverEmpId", approver);
                    o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
                }else{
                    //工作流提交
                    Map<String, Object> variablesBpmTwo = new HashMap<String, Object>();
                    variablesBpmTwo.put("AssessPlanApproverEmpId", approver);
                    variablesBpmTwo.put("empIds", empIds);//可能重新分配承办人，更新
                    o_jbpmBO.doProcessInstance(executionId, variablesBpmTwo);
                }
                if(o_sendEmailBO.findIsSendValue("send_email_assess_riskplan_all")){//查询计划审批节点是否发送email
                    JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
                    List<JbpmHistActinst> jbpmHistActinstList = o_planBO.findJbpmHistActinstsBySome(jbpmhp.getId(),0L);
                    if(jbpmHistActinstList.size()>0){
                        JbpmHistActinst jbpmHistActinst = jbpmHistActinstList.get(0);
                        VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
                        if(jbpmHistTask!=null){
                            Long taskId = jbpmHistTask.getId();
                            String jbpmType = o_jbpmBO.findPDDByExecutionId(executionId);
                            
                            if("complex".equalsIgnoreCase(jbpmType)){
                                o_assessEmailBO.sendAssessEmail(businessId, approver, taskId.toString(), "FHD.view.risk.assess.planManagerApprove.PlanManagerApproveMain", "评估计划主管审批");
                            }else if("simple".equalsIgnoreCase(jbpmType)){
                                o_assessEmailBO.sendAssessEmail(businessId, approver, taskId.toString(), "FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitMain", "评估计划审批");
                            }
                        }
                    }
                }
            }
            //保存审批人承办人
            o_riskScoreBO.saveRiskCircuseeBysome(deptEmpId, approver, assessPlan, businessId);
        }
    }
	
	
	/**
	 * 根据计划查询应急预案信息
	 * 
	 * @author 张健
	 * @param executionId 流程ID
	 * @param businessId 计划ID
	 * @param type 查询列表类型,emp按人员查，dept按部门（相当于按所有被分配的人查询）
	 * @return List<Object[]> 结果集列表
	 * @date 2014-1-21
	 * @since Ver 1.1
	 */
	public List<Object[]> findContingencyPlanList(String executionId,String businessId, String query, String type){
	    Map<String,Object> map = new HashMap<String,Object>();
	    String sql = "";
	    List<RiskAssessPlanTakerObject> raptoList = null;
	    List<String> empSet = null;
	    sql = sql + " select DISTINCT riskobj.risk_id,risk.RISK_NAME,reportlist.RID,reportlist.ENAME,reportlist.RORG,reportlist.STATUS  ";
	    if("emp".equals(type) || "dept".equals(type)){
	        sql = sql + " from t_rm_rang_object_dept_emp objdept ";
	        sql = sql + " left JOIN t_rm_risk_score_object riskobj on objdept.SCORE_OBJECT_ID = riskobj.ID ";
	    }else{
	        sql = sql + " from t_rm_risk_score_object riskobj ";
	    }
	    sql = sql + " LEFT JOIN t_rm_risks risk on riskobj.RISK_ID = risk.ID ";
	    sql = sql + " left join ( ";
	    sql = sql + " select report.id RID,report.ENAME ENAME,report.EVENT_OCCURED_ORG RORG,reportrisk.risk_id RISK,report.estatus STATUS from T_MANAGE_REPORT report LEFT JOIN T_MANAGE_REPORT_rela_risk reportrisk on reportrisk.report_id = report.id where report.assess_plan_id=:businessId ";
	    if("emp".equals(type)){
            sql = sql + " and CREATE_BY=:createBy  ";
        }else if("dept".equals(type)){
            raptoList = (List<RiskAssessPlanTakerObject>) o_jbpmOperate.getVariableObj(executionId, "riskTaskEvaluators");
            empSet = new ArrayList<String>();
            for(RiskAssessPlanTakerObject ra : raptoList){
                empSet.add(ra.getRiskTaskEvaluatorId());
            }
            sql = sql + " and CREATE_BY in (:scoreDeptId) ";
            map.put("scoreDeptId", empSet);
        }
	    sql = sql + " ) reportlist on reportlist.RISK = risk.ID ";
	    sql = sql + " where riskobj.assess_plan_id = :businessId ";
	    if("emp".equals(type)){
	        sql = sql + " and objdept.SCORE_EMP_ID = :createBy";
	        map.put("createBy", UserContext.getUser().getEmpid());
	    }else if("dept".equals(type)){
	        sql = sql + " and objdept.SCORE_EMP_ID in (:scoreDeptId) ";
	    }
	    if(StringUtils.isNotBlank(query)){
	        sql = sql + " and (risk.RISK_NAME like :query or reportlist.ENAME like :query ) ";
	        map.put("query", "%"+query+"%");
	    }
	    sql = sql + " order by risk.RISK_NAME ";
	    map.put("businessId", businessId);
	    List<Object[]> list = o_riskAssessPlanDAO.createSQLQuery(sql, map).list();
	    return list;
	}
	
	/**
	 * 通过风险ID查询应急预案
	 * 
	 * @author 张健
	 * @param businessId 计划ID
	 * @param riskIds 风险ID集合
	 * @return List<Object[]> 结果集列表
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public List<Object[]> findContingencyPlanListBySome(String businessId,String riskIds){
        String sql = "";
        sql = sql + " select DISTINCT riskobj.risk_id,risk.RISK_NAME,reportlist.RID,reportlist.ENAME,reportlist.RORG  ";
        sql = sql + " from t_rm_risk_score_object riskobj  ";
        sql = sql + " LEFT JOIN t_rm_risks risk on riskobj.RISK_ID = risk.ID ";
        sql = sql + " left join ( ";
        sql = sql + " select report.id RID,report.ENAME ENAME,report.EVENT_OCCURED_ORG RORG,reportrisk.risk_id RISK from T_MANAGE_REPORT report LEFT JOIN T_MANAGE_REPORT_rela_risk reportrisk on reportrisk.report_id = report.id where report.assess_plan_id= :businessId ";
        sql = sql + " ) reportlist on reportlist.RISK = risk.ID ";
        sql = sql + " where riskobj.assess_plan_id= :businessId ";
        if(StringUtils.isNotBlank(riskIds)){
            sql = sql + " and riskobj.risk_id in (:riskIds) ";
        }else{
            sql = sql + " and 1!=1 ";
        }
        sql = sql + " order by risk.RISK_NAME ";
        SQLQuery SQLQuery = o_riskAssessPlanDAO.createSQLQuery(sql);
        SQLQuery.setParameter("businessId", businessId);
        if(StringUtils.isNotBlank(riskIds)){
            SQLQuery.setParameterList("riskIds", riskIds.split(","));
        }
        List<Object[]> list = SQLQuery.list();
        return list;
    }
	
	/**
	 * 把计划添加的预案改成已归档状态
	 * 
	 * @author 张健
	 * @param businessId 计划ID
	 * @return VOID
	 * @date 2014-1-21
	 * @since Ver 1.1
	 */
	public void updateManageReprotStatus(String businessId){
	    String sql = " update T_MANAGE_REPORT set ESTATUS=:status where assess_plan_id=:assess_plan_id ";
	    Map<String,Object> map = new HashMap<String,Object>();
	    map.put("status",Contents.RISK_STATUS_ARCHIVED);
	    map.put("assess_plan_id",businessId);
	    o_riskAssessPlanDAO.createSQLQuery(sql,map).executeUpdate();
	}
	
	/**
	 * 通用计划
	 */
	@Transactional
	@RecordLog("风险辨识计划，计划主管审批")
	public void submiApprovalBySupervisor(String executionId, String businessId, String isPass, 
																	String examineApproveIdea, String approverId, String approverKey,String approverKeyType,String totalExecutionId){
		RiskAssessPlan assessPlan = o_riskAssessPlanDAO.get(businessId);
		if(null != assessPlan){
			if(!approverKey.equalsIgnoreCase("over")){
				if ("no".equals(isPass)) {
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put("path", isPass);
					variables.put("examineApproveIdea", examineApproveIdea);
					o_jbpmBO.doProcessInstance(executionId, variables);
				}else{
					// 审批通过
					String planDeptLeaderId = "";
					Map<String, Object> variables = new HashMap<String, Object>();
					if(StringUtils.isNotBlank(approverId)){
						JSONArray jsonArray = JSONArray.fromObject(approverId);
						if (jsonArray.size() > 0){
							 JSONObject jsobj = jsonArray.getJSONObject(0);
							 planDeptLeaderId = jsobj.getString("id");//审批人
						}
					}
					variables.put("path", isPass);
					variables.put("examineApproveIdea", examineApproveIdea);
					variables.put(approverKey, planDeptLeaderId);//部门领导
					o_jbpmBO.doProcessInstance(executionId, variables);
				}
			}else{
			    //计划改成已执行的状态
			    String hqlSql = "update RiskAssessPlan ra set ra.dealStatus = :dealStatus where ra.id = :id";
		        Query query = o_riskAssessPlanDAO.createQuery(hqlSql);
		        query.setParameter("dealStatus", Contents.DEAL_STATUS_FINISHED);
		        query.setParameter("id", businessId);
		        query.executeUpdate();
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}
		}
	}
	
    @RecordLog("查询风险整理风险、组织、目标、流程信息")
    public ArrayList<HashMap<String, String>> findContingencyPlanByRisk(String query, String assessPlanId, String typeId, String type){
        StringBuffer sql = new StringBuffer();
        ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
        
        Map<String,String> assessEmpMap = o_showRiskTidyBO.getAssessEmpCount(assessPlanId);
        
        if("risk".equalsIgnoreCase(type)){
            sql.append(" select ");
            sql.append(" a.id, ");
            sql.append(" risk.parent_id, ");
            sql.append(" c.risk_name, ");
            sql.append(" risk.id  risk_id, ");
            sql.append(" risk.risk_name  name, ");
            sql.append(" risk.template_id, ");
            sql.append(" d.temp_id, ");
            sql.append(" risk.DELETE_ESTATUS , ");
            sql.append(" edit.edit_idea_id,z.id  score_object_id ");
            sql.append(" from ");
            sql.append(" t_rm_risk_score_object  a ");
            sql.append(" LEFT JOIN  t_rm_risks risk on a.RISK_ID=risk.id  ");
            sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = risk.id ");
            sql.append(" LEFT JOIN t_rm_risks c on risk.parent_id=c.id   ");
            sql.append(" left JOIN t_rm_risk_assess_plan d on d.id = a.assess_plan_id ");
            sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
            sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
            sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
            sql.append("GROUP BY b.risk_id) edit on a.RISK_ID = edit.risk_id ");
            sql.append(" where ");
            sql.append(" a.assess_plan_id=:assessPlanId ");
            if(StringUtils.isNotBlank(typeId)){
                if(!"root".equalsIgnoreCase(typeId)){
                    //如果节点不为root 则是树节点下的过滤查询
                    sql.append(" and risk.ID_SEQ LIKE :likeTypeId "); 
                }
            }
            if(StringUtils.isNotBlank(query)){
                if(!query.matches("\\d*")){//判断查询条件是否为数字
                    sql.append(" and (c.risk_name like :likeQuery or risk.risk_name like :likeQuery) ");
                }
            }
            sql.append(" and risk.IS_RISK_CLASS ='re' ");
            sql.append(" ORDER BY ");
            sql.append(" risk.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
            sql.append(" c.risk_name ");
        }else if("dept".equalsIgnoreCase(type)){
            sql.append(" select ");
            sql.append(" a.id, ");
            sql.append(" b.PARENT_ID, ");
            sql.append(" e.risk_name  parent_name, ");
            sql.append(" a.RISK_ID, ");
            sql.append(" b.RISK_NAME, ");
            sql.append(" b.TEMPLATE_ID, ");
            sql.append(" c.TEMP_ID , ");
            sql.append(" b.DELETE_ESTATUS, edit.edit_idea_id,z.id  score_object_id ");
            sql.append(" from ");
            sql.append(" t_rm_risk_score_object d ");
            sql.append(" LEFT JOIN t_rm_risk_assess_plan c on d.ASSESS_PLAN_ID = c.id ");
            sql.append(" LEFT JOIN t_rm_risk_org a on a.RISK_ID = d.risk_id ");
            sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = a.RISK_ID ");
            sql.append(" LEFT JOIN t_rm_risks b on d.RISK_ID = b.id   ");
            sql.append(" LEFT JOIN t_sys_organization o on o.ID = a.ORG_ID  ");
            sql.append(" LEFT JOIN t_rm_risks e  on  e.id= b.PARENT_ID ");
            sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
            sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
            sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
            sql.append("GROUP BY b.risk_id) edit on d.RISK_ID = edit.risk_id ");
            sql.append(" where ");
            if(!"root".equalsIgnoreCase(typeId)){
                //如果节点不为root 则是树节点下的过滤查询
                sql.append(" o.ID_SEQ like :likeTypeId and ");
            }
            sql.append(" d.assess_plan_id=:assessPlanId ");
            sql.append(" and b.is_risk_class = 're' ");
            if(StringUtils.isNotBlank(query)){
                if(!query.matches("\\d*")){
                    sql.append(" and (e.risk_name like :likeQuery or b.RISK_NAME like :likeQuery) ");
                }
            }
            sql.append(" GROUP BY ");
            sql.append(" a.RISK_ID  ");
            sql.append(" ORDER BY ");
            sql.append(" b.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
            sql.append(" parent_name ");
        }else if("strategyMap".equalsIgnoreCase(type)){
            sql.append(" select * from ");
            sql.append(" (select ");
            sql.append(" a.id, ");
            sql.append(" b.PARENT_ID, ");
            sql.append(" f.risk_name  PARENT_NAME, ");
            sql.append(" a.RISK_ID, ");
            sql.append(" b.RISK_NAME, ");
            sql.append(" b.TEMPLATE_ID, ");
            sql.append(" c.TEMP_ID , ");
            sql.append(" b.DELETE_ESTATUS,edit.edit_idea_id, e.strategy_map_id,z.id  score_object_id ");
            sql.append(" from ");
            sql.append(" t_rm_risk_score_object d ");
            sql.append(" LEFT JOIN t_rm_risk_assess_plan c on c.ID = d.ASSESS_PLAN_ID ");
            sql.append(" LEFT JOIN t_kpi_kpi_rela_risk a on a.RISK_ID = d.RISK_ID ");
            sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = a.RISK_ID ");
            sql.append(" LEFT JOIN t_rm_risks b on a.RISK_ID = b.id  ");
            sql.append(" LEFT JOIN t_kpi_sm_rela_kpi e on e.kpi_id = a.kpi_id   ");
            sql.append(" LEFT JOIN t_rm_risks f on f.id=b.parent_id ");
            sql.append(" LEFT JOIN t_rm_rang_object_dept_emp object on d.id = object.score_object_id ");
            sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
            sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
            sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
            sql.append("GROUP BY b.risk_id) edit on d.RISK_ID = edit.risk_id ");
            sql.append(" where ");
            sql.append(" a.etype='i'");
            sql.append(" and d.assess_plan_id=:assessPlanId ");
            if(!"root".equalsIgnoreCase(typeId)){
                //如果节点不为root 则是树节点下的过滤查询
                if(StringUtils.isNotBlank(typeId)){
                    String ids [] = typeId.split("\\_");
                    if(ids.length==1){
                        sql.append(" and e.strategy_map_id = :typeId");
                    }else{
                        sql.append(" and e.kpi_id = :ids");
                    }
                }
            }
            sql.append(" and b.IS_RISK_CLASS = 're' ");
            if(StringUtils.isNotBlank(query)){
                if(!query.matches("\\d*")){
                    sql.append(" and (f.risk_name like :likeQuery or b.RISK_NAME like :likeQuery) ");
                }
            }
            sql.append(" GROUP BY ");
            sql.append(" a.risk_id  ");
            sql.append(" order by ");
            sql.append(" b.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
            sql.append(" PARENT_NAME ) a where a.strategy_map_id is not null ");
        }else if("processure".equalsIgnoreCase(type) ){
            sql.append("select ");
            sql.append("a.id, ");
            sql.append("b.PARENT_ID, ");
            sql.append("e.risk_name  parent_name, ");
            sql.append("a.RISK_ID, ");
            sql.append("b.RISK_NAME, ");
            sql.append("b.TEMPLATE_ID, ");
            sql.append("c.TEMP_ID , ");
            sql.append("b.DELETE_ESTATUS,edit.edit_idea_id,z.id  score_object_id ");
            sql.append("from ");
            sql.append("t_rm_risk_score_object d ");
            sql.append("LEFT JOIN t_rm_risk_assess_plan c on c.ID = d.ASSESS_PLAN_ID ");
            sql.append("LEFT JOIN t_rm_risks b on b.id = d.risk_id ");
            sql.append("LEFT JOIN t_processure_risk_processure a on a.RISK_ID = b.id   ");
            sql.append(" LEFT JOIN (select * from t_rm_risk_score_object where assess_plan_id=:assessPlanId) z on z.risk_id = a.RISK_ID ");
            sql.append("LEFT JOIN t_ic_processure p on a.PROCESSURE_ID = p.ID  ");
            sql.append("LEFT JOIN t_rm_risks e on e.id=b.PARENT_ID   ");
            sql.append("LEFT JOIN t_rm_rang_object_dept_emp object on d.id = object.score_object_id ");
            sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
            sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
            sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
            sql.append("GROUP BY b.risk_id) edit on d.RISK_ID = edit.risk_id ");
            sql.append("where ");
            sql.append("a.etype='i' ");
            sql.append("and d.assess_plan_id=:assessPlanId ");
            if(!"root".equalsIgnoreCase(typeId)){
                //如果节点不为root 则是树节点下的过滤查询
                sql.append("and p.ID_SEQ like :likeTypeId ");
            }
            if(StringUtils.isNotBlank(query)){
                if(!query.matches("\\d*")){
                    sql.append(" and (e.risk_name like :likeQuery or b.RISK_NAME like :likeQuery) ");
                }
            }
            sql.append(" and b.is_risk_class = 're' ");
            sql.append("GROUP BY ");
            sql.append("a.RISK_ID  ");
            sql.append("order by ");
            sql.append("b.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
            sql.append("parent_name ");
        }else if(null == type){
            //默认加载此次评估下所有的风险事件
            sql.append("SELECT ");
            sql.append("r.id id, ");
            sql.append("r.PARENT_ID, ");
            sql.append("rp.RISK_NAME parent_name , ");
            sql.append("r.id risk_id , ");
            sql.append("r.RISK_NAME, ");
            sql.append("r.TEMPLATE_ID, ");
            sql.append("plan.TEMP_ID, ");
            sql.append("r.DELETE_ESTATUS,edit.edit_idea_id ");
            sql.append("FROM ");
            sql.append("t_rm_risk_score_object ob ");
            sql.append("LEFT JOIN ");
            sql.append("t_rm_risks r ");
            sql.append("on ob.RISK_ID = r.id ");
            sql.append("LEFT JOIN ");
            sql.append("t_rm_risks rp ");
            sql.append("on rp.id = r.PARENT_ID ");
            sql.append("LEFT JOIN ");
            sql.append("t_rm_risk_assess_plan plan ");
            sql.append("on plan.ID = ob.assess_plan_id ");
            sql.append("LEFT JOIN (select b.risk_id,r.edit_idea_id from t_rm_rang_object_dept_emp r ");
            sql.append("LEFT JOIN t_rm_risk_score_object b on b.id = r.score_object_id ");
            sql.append("where b.assess_plan_id=:assessPlanId and r.edit_idea_id is not null ");
            sql.append("GROUP BY b.risk_id) edit on ob.RISK_ID = edit.risk_id ");
            sql.append("WHERE ");
            sql.append("ob.assess_plan_id = :assessPlanId ");
            sql.append("and r.is_risk_class = 're'");
            if(StringUtils.isNotBlank(query)){
                if(!query.matches("\\d*")){
                    sql.append(" and (rp.RISK_NAME like :likeQuery or r.RISK_NAME like :likeQuery) ");
                }
            }
            sql.append("ORDER BY ");
            sql.append("r.DELETE_ESTATUS DESC, edit.edit_idea_id DESC, ");
            sql.append("parent_name "); 
        }
        
        if(StringUtils.isNotBlank(sql)){
            SQLQuery sqlQuery = o_riskAssessPlanDAO.createSQLQuery(sql.toString());
            sqlQuery.setParameter("assessPlanId", assessPlanId);
            
            if(StringUtils.isNotBlank(query)){
                if(!query.matches("\\d*")){//判断查询条件是否为数字
                    sqlQuery.setParameter("likeQuery", "%"+query+"%");
                }
            }
            
            if("strategyMap".equalsIgnoreCase(type)){
                if(StringUtils.isNotBlank(typeId)){
                    if(!"root".equalsIgnoreCase(typeId)){
                        String ids [] = typeId.split("\\_");
                        if(ids.length==1){
                            sqlQuery.setParameter("typeId", typeId);
                        }else{
                            sqlQuery.setParameter("ids", ids[1]);
                        }
                    }
                }
            }else{
                if(StringUtils.isNotBlank(typeId)){
                    if(!"root".equalsIgnoreCase(typeId)){
                        sqlQuery.setParameter("likeTypeId", "%." + typeId + ".%");
                    }
                }
            }
            
            List<Object[]> list = sqlQuery.list();
            for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
                Object[] objects = (Object[]) iterator.next();
                
                String id = "";
                String riskParentId = "";
                String riskParentName = "";
                String riskId = "";
                String riskName = "";
                String templateId = "";
                String tempId = "";
                String assessEmpSum = "";
                String riskStatusIcon = "";
                String editIdeaId = "";
                String scoreObjectId = "";
                
                if(null != objects[0]){
                    id = objects[0].toString();
                }if(null != objects[1]){
                    riskParentId = objects[1].toString();
                }if(null != objects[2]){
                    riskParentName = objects[2].toString();
                }if(null != objects[3]){
                    riskId = objects[3].toString();
                }if(null != objects[4]){
                    riskName = objects[4].toString();
                }if(null != objects[5]){
                    templateId = objects[5].toString();
                }if(null != objects[6]){
                    tempId = objects[6].toString();
                }if(null != objects[7]){
                    riskStatusIcon = objects[7].toString();
                }if(null != objects[8]){
                    editIdeaId = objects[8].toString();
                }if(objects.length > 9){
                    if(null != objects[9]){
                        scoreObjectId = objects[9].toString();
                    }
                }
                
                assessEmpSum = assessEmpMap.get(riskId);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", id);
                map.put("riskParentId", riskParentId);
                map.put("riskParentName", riskParentName);
                map.put("riskId", riskId);
                map.put("riskName", riskName);
                map.put("assessEmpSum", assessEmpSum);
                map.put("assessPlanId", assessPlanId);
                map.put("scoreObjectId", scoreObjectId);
                
                if(riskStatusIcon.equalsIgnoreCase("1")){
                    map.put("riskStatus", "icon-status-assess_mr");
                }else if(riskStatusIcon.equalsIgnoreCase("2")){
                    map.put("riskStatus", "icon-status-assess_new");
                }
                
                if(!riskStatusIcon.equalsIgnoreCase("2")){
                    if(!editIdeaId.equalsIgnoreCase("")){
                        map.put("riskStatus", "icon-status-assess_edit");
                    }
                }
                
                if(org.apache.commons.lang.StringUtils.isBlank(tempId)){
                    map.put("templateId", templateId);
                }else{
                    map.put("templateId", tempId);
                }
                if(StringUtils.isNotBlank(query)&&query.matches("\\d*")){
                    if(assessEmpSum.contains(query)){
                        maps.add(map);
                    }
                }else{
                    maps.add(map);
                }
            }
        }
        
        return maps;
    }
}