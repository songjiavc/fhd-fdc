package com.fhd.sm.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.SQLQuery;
import org.hibernate.jdbc.Work;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.TimePeriodBO;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.base.SqlBuilder;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;

@Service
public class KpiTargetValueGatherPlanBO {
     
	@Autowired
	private RiskScoreObjectDAO o_riskScoreObjDAO;
	
	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
 	@Autowired	
    private KpiGatherResultInputBO o_kpiGatherResultInputBO;
 	
 	@Autowired
 	private TimePeriodBO o_timePeriodBO;
	
	/**
	 * 
	 * @param id 考核计划id
	 * @return 部门考察目标统计
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findPlanStatisticDataById(String id,String query) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append("select so.ASSESS_PLAN_ID,org.id,org.ORG_NAME,count(*) as kpicount,p.check_begin_date,p.check_type ")
		   .append("from t_rm_risk_score_object so ")
		   .append("     inner join t_kpi_kpi kpi on so.Object_ID = kpi.id ")
		   .append("     inner join t_kpi_kpi_rela_org_emp kpiorg on kpiorg.kpi_id  = kpi.id and kpiorg.ETYPE = 'B'")
		   .append("     inner join t_sys_organization org on kpiorg.org_id = org.id ")
		   .append("     inner join T_RM_RISK_ASSESS_PLAN p on so.ASSESS_PLAN_ID = p.id ")
		   .append("where so.assess_plan_id = ? ");
		if(StringUtils.isNotBlank(query)) {
			   sql.append("and org.ORG_NAME like '%").append(query).append("%' ");	
			}
		sql.append("GROUP BY ORG.ORG_NAME ")
		   .append("order by kpicount ");

	    List<Object[]> list = o_riskScoreObjDAO.createSQLQuery(sql.toString(), id).list();
	    for(Object[] o : list) {
	    	Map<String, Object> map = new HashMap<String, Object>();
	    	map.put("planId", o[0]);
	    	map.put("orgId", o[1]);
	    	map.put("deptName", o[2]);
	    	map.put("kpiCount", o[3]);
	    	map.put("assessDateRange", this.findCheckTimeFullName(o[4], o[5]));
	    	data.add(map);
	    }		
		return data;
	}
	/**
	 * 单个部门在考核计划下考核的指标
	 * @param planId 考核计划Id
	 * @param deptId 部门Id
	 * @return 部门考核概况
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findDeptPlanKpiById(String planId,String deptId,String gatherType) {
		String etype = null;
		if("kpiTargetGather".equalsIgnoreCase(gatherType)) {
			etype = "T";
		} else if("kpiFinishGather".equalsIgnoreCase(gatherType)){
			etype = "G";
		}
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		List<Object[]> list = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select so.ASSESS_PLAN_ID,org.id orgId,kpi.id kpiId,org.ORG_NAME,kpi.KPI_NAME,emp.EMP_NAME  ")
		   .append("from t_rm_risk_score_object so ")
		   .append("inner join t_kpi_kpi kpi on so.object_id = kpi.ID " )
		   .append("inner join t_kpi_kpi_rela_org_emp kpiorg on kpiorg.kpi_id  = kpi.id and kpiorg.ETYPE = 'B' ")
		   .append("inner join t_sys_organization org on kpiorg.org_id = org.id ")
		   .append("left join (t_kpi_kpi_rela_org_emp kpirela ")
		   .append("            inner join t_sys_employee emp on kpirela.EMP_ID =  emp.ID) ")
		   .append("on kpirela.kpi_id  = kpi.id and kpirela.ETYPE = ? ")
		   .append("where so.ASSESS_PLAN_ID = ? ");
		
		if(StringUtils.isNotBlank(deptId)) {
			sql.append(" and org.id = ?");
			sql.append(" order by ORG.id,");
			sql.append(" kpi.KPI_NAME");
			list = o_riskScoreObjDAO.createSQLQuery(sql.toString(),etype, planId ,deptId).list(); 
		} else {
			sql.append(" order by ORG.id,");
			sql.append("  kpi.KPI_NAME");
			list = o_riskScoreObjDAO.createSQLQuery(sql.toString(),etype, planId).list(); 
		}
		
		 
	    for(Object[] o : list) {
	    	Map<String, Object> map = new HashMap<String, Object>();
	    	map.put("planId", o[0]);
	    	map.put("orgId", o[1]);
	    	map.put("kpiId", o[2]);
	    	map.put("deptName", o[3]);
	    	map.put("kpiName", o[4]);
	    	map.put("empName", o[5]);
	    	data.add(map);
	    }	
		return data;
	}
	
	/**
	 * 更新考核计划相关的指标
	 * @param planId 考核计划id
	 * @param kpiList 指标Id集合
	 */
	@Transactional
	public void mergePlanRelaKpi(final String planId,final List<String> kpiList) {
		   String delSql = "delete from t_rm_risk_score_object  " +
	                       "where assess_plan_id = ? " +
	                       "  and object_id in (:kpiList)";
		   o_riskScoreObjDAO.createSQLQuery(delSql).setParameter(0,planId)
		                                           .setParameterList("kpiList", kpiList, new StringType())
		                                           .executeUpdate();
		   // 插入新的指标Id
		   o_riskScoreObjDAO.getSession().doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String updateSql = "insert into " +
						           "    t_rm_risk_score_object " +
						           "    (id," +
						           "     object_id," +
						           "     assess_plan_id" +
						           "     )values" +
						           "    (?," +
						           "     ?," +
						           "     ?" +
						           "    )";
				PreparedStatement pst = connection.prepareStatement(updateSql);
				for(String kpiId : kpiList) {
					pst.setString(1, Identities.uuid());
					pst.setString(2, kpiId);
					pst.setString(3, planId);
					pst.addBatch();
				}
                pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);				
			}			   
		   });		   		   
	}
	
	/**
	 * 删除考核指标
	 * @param planId 考核计划Id
	 * @param ids 删除的指标id拼接字符串
	 */
	@Transactional
	public void removePlanRelaKpiByIds(String planId,String ids) {
		   String[] idArray = ids.split(",");
		   String delSql = "delete from t_rm_risk_score_object  " +
                   "where assess_plan_id = ? " +
                   "  and object_id in (:kpiList)";
		   o_riskScoreObjDAO.createSQLQuery(delSql, planId).setParameterList("kpiList", idArray).executeUpdate();
	}
	
	/**
	 * 或者考核计划的考核时间
	 * @param planId 计划ID
	 * @return 考核时间
	 */
	public Map<String, Object> findplanChecktimeById(String planId) {
		 Map<String, Object> map = new HashMap<String, Object>();
		 RiskAssessPlan plan = o_riskAssessPlanDAO.get(planId);
			//开始时间
			if(null != plan.getCheckBeginDate()){
				map.put("startTime", plan.getCheckBeginDate().toString().split(" ")[0]);
			}else{
				map.put("startTime", "");
			}
			map.put("gatherType", plan.getPlanType());
			map.put("checkType", plan.getCheckType());
			map.put("workState", plan.getStatus());
	     return map;
	}
	/**
	 * 发起考核计划
	 * @param planId 考核计划id
	 * @param startTime 考核时间
	 * @param gatherType 采集类型(目标值或实际值采集)
	 * @throws ParseException
	 */
	@Transactional
	public void startKpiGahterProcess(String planId,String startTime,String checkType,String gatherType) throws ParseException{
		RiskAssessPlan riskAssessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
		Date date = new Date();
		if(StringUtils.isNotBlank(startTime)) {
			date = DateUtils.parseDate(startTime, "yyyy-MM-dd");
			riskAssessPlan.setCheckBeginDate(date);
		} else {
			riskAssessPlan.setCheckBeginDate(null);
		}							
		riskAssessPlan.setDealStatus("H");
		riskAssessPlan.setCheckType(checkType);
		o_riskAssessPlanBO.mergeRiskAssessPlan(riskAssessPlan);
		List<Object[]> list = this.findPlanKpiDetailByPlanId(planId,gatherType,date);
		if(null != list && list.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("planId", planId);
			String planName = riskAssessPlan.getPlanName();
			map.put("planName",planName);
			o_kpiGatherResultInputBO.doFlowProcess(list,map);
		}
	}
	/**
	 * 保存考核时间
	 * @param planId 考核计划id
	 * @param startTime 考核时间
	 * @param checkType 考核类型
	 * @throws ParseException
	 */
	@Transactional
	public void saveplanbycheckTime(String planId,String startTime,String checkType) throws ParseException{
		RiskAssessPlan riskAssessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
		if(StringUtils.isNotBlank(startTime)) {
			riskAssessPlan.setCheckBeginDate(DateUtils.parseDate(startTime, "yyyy-MM-dd"));			
		} else {
			riskAssessPlan.setCheckBeginDate(Calendar.getInstance().getTime());
		}
		riskAssessPlan.setCheckType(checkType);
		o_riskAssessPlanBO.mergeRiskAssessPlan(riskAssessPlan);
	}
	
	/**
	 * 获取各个部门的考核信息
	 * @param planId 考核计划id
	 * @param gatherType 考核类型
	 * @param startTime 考核时间
	 * @return 考核信息
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findPlanKpiDetailByPlanId(String planId,String gatherType,Date startTime) {
    	Map<String,String> model = new HashMap<String, String>();
    	String querySql = SqlBuilder.getSql("findAllPlanKpiInfoById", model);
    	SQLQuery sqlQuery = o_riskAssessPlanDAO.createSQLQuery(querySql);
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	sqlQuery.setString("date", dateFormat.format(startTime));
   	    if("kpiTargetGather".equalsIgnoreCase(gatherType)) {
   	    	sqlQuery.setString("etype", "T");
    	} else {
    		sqlQuery.setString("etype", "G");
    	}
   	    sqlQuery.setString("planId", planId);
    	return sqlQuery.list();
	}
	/**
	 * 改变考核计划处理状态
	 * @param planId 考核计划id
	 * @param type 处理状态
	 */
	@Transactional
	public void changeWorkState(String planId,String type) {
		  if(StringUtils.isNotBlank(planId)) {
			  RiskAssessPlan riskAssessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
			  riskAssessPlan.setDealStatus(type);
			  o_riskAssessPlanBO.mergeRiskAssessPlan(riskAssessPlan);
		  }
	}
	
	/**
	 * 获取考核计划考核时间中文字段
	 * @param t 时间 
	 * @param c 考核性质
	 * @return 考核时间中文字段
	 */
	public String findCheckTimeFullName(Object t,Object c) {
    	if(null == t) {
    		return null;
    	} else {
    		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    		Date date = (Date) t;
    		if("0checktype_y".equalsIgnoreCase((String) c)) {
    			return  o_timePeriodBO.findTimePeriodFullNameBySome(date, "0frequecy_year");
    		} else if("0checktype_m".equalsIgnoreCase((String) c)){
    			return  o_timePeriodBO.findTimePeriodFullNameBySome(date, "0frequecy_month");
    		} else if("0checktype_w".equalsIgnoreCase((String) c)){
    			return  o_timePeriodBO.findTimePeriodFullNameBySome(date, "0frequecy_week");
    		} else if("0checktype_q".equalsIgnoreCase((String) c)){
    			return  o_timePeriodBO.findTimePeriodFullNameBySome(date, "0frequecy_quarter");
    		} else {
    			return dateFormat.format(date);
    		}
    	}
	}
}
