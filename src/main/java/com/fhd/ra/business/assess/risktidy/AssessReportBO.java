package com.fhd.ra.business.assess.risktidy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.dao.assess.quaAssess.StatisticsResultDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.ra.business.assess.quaassess.ShowAssessBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.sm.business.KpiBO;

@Service
public class AssessReportBO {

	@Autowired
	private StatisticsResultDAO o_statStatisticsResultDAO;
	
	@Autowired
	private ShowAssessBO o_showAssessBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private RiskOutsideBO o_risRiskService;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	/***
	 * @param isAlarm 是否告警
	 * @param riskRbsOrRe 风险类型 风险分类/风险事件
	 * @param assessPlanId 评估计划ID
	 * @param companyId 公司ID
	 * @return ArrayList<HashMap<String, String>>
	 * */
	public  ArrayList<HashMap<String, String>>
	findRbsOrReList(boolean isAlarm, String riskRbsOrRe, String assessPlanId, String companyId){
		StringBuffer sql = new StringBuffer();
		HashMap<String, HashMap<String, String>> maps = new HashMap<String, HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayaList = new ArrayList<HashMap<String,String>>();
        Map<String, Risk> riskAllMap = o_risRiskService.getAllRiskInfo(companyId);
        HashMap<String, AlarmPlan> alarmPlanAllMap = o_alarmPlanBO.findAlarmPlanAllMap(companyId);
		sql.append(" select a.score_object_id,etype,score_dim_id,score, d.org_name ");
		sql.append(" from t_rm_statistics_result a, t_rm_risks  b, t_rm_risk_org c, ");
		sql.append(" t_sys_organization d ");
		sql.append(" where c.risk_id = b.id  ");
		sql.append(" and  a.score_object_id = b.id ");
		sql.append(" and d.id=c.org_id ");
		sql.append(" and b.is_risk_class = :riskRbsOrRe ");
		sql.append(" and a.assess_plan_id =:assessPlanId ORDER BY etype,score_dim_id,score desc ");
        SQLQuery sqlQuery = o_statStatisticsResultDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("riskRbsOrRe", riskRbsOrRe);
        sqlQuery.setParameter("assessPlanId", assessPlanId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            
            String scoreObjectId = "";//对象
            String etype = "";//责任相关
            String dimId = "";//发生可能性,影响程度
            String score = "";//分值
            String orgName = "";//部门名称
            
            if(null != objects[0]){
            	scoreObjectId = objects[0].toString();
            }if(null != objects[1]){
            	etype = objects[1].toString();
            }if(null != objects[2]){
            	dimId = objects[2].toString();
            }if(null != objects[3]){
            	score = objects[3].toString();
            }if(null != objects[4]){
            	orgName = objects[4].toString();
            }
            
            HashMap<String, String> map = new HashMap<String, String>();
            
            if(maps.get(scoreObjectId) != null){
            	
            	maps.get(scoreObjectId).put(etype + "--" + orgName, etype + "--" + orgName);
            	if(dimId.equalsIgnoreCase("46e75071-9946-4b37-b407-aff729219a7b")){
            		//影响程度
            		maps.get(scoreObjectId).put("yx", score);
            	}else if (dimId.equalsIgnoreCase("707752d7-70bb-48d2-af1d-8270e6e62a1d")){
            		//发生可能性
            		maps.get(scoreObjectId).put("fs", score);
            	}
            }else{
            	
            	
            	map.put(etype + "--" + orgName, etype + "--" + orgName);
            	if(dimId.equalsIgnoreCase("46e75071-9946-4b37-b407-aff729219a7b")){
            		//影响程度
            		map.put("yx", score);
            	}else if (dimId.equalsIgnoreCase("707752d7-70bb-48d2-af1d-8270e6e62a1d")){
            		//发生可能性
            		map.put("fs", score);
            	}
            	
            	maps.put(scoreObjectId, map);
            }
        }
        
        Set<Entry<String, HashMap<String, String>>> key = maps.entrySet();
        for (Iterator<Entry<String, HashMap<String, String>>> it = key.iterator(); it.hasNext();) {
            Entry<String, HashMap<String, String>> s = it.next();
            HashMap<String, String> map = maps.get(s.getKey());
            String zr = "";
            String xg = "";
            HashMap<String, String> mapZ = new HashMap<String, String>();
            
            Set<Entry<String, String>> key2 = map.entrySet();
            for (Iterator<Entry<String, String>> it2 = key2.iterator(); it2.hasNext();) {
                Entry<String, String> s2 = it2.next();
                String str = map.get(s2.getKey());
                if(s2.getKey().indexOf("M") != -1){
                	//M:责任部门
                	String strs[] = s2.getKey().split("--");
                	zr += strs[1] + ",";
                }else if(s2.getKey().indexOf("A") != -1){
                	//A:相关部门
                	String strs[] = s2.getKey().split("--");
                	xg += strs[1] + ",";
                }else{
                	if(s2.getKey().equalsIgnoreCase("yx")){
                		//影响程度
                		mapZ.put("yx", str);
                	}else if(s2.getKey().equalsIgnoreCase("fs")){
                		//发生可能性
                		mapZ.put("fs", str);
                	}
                }
            }
            
            zr += ",";
            xg += ",";
            
            mapZ.put("riskName", riskAllMap.get(s.getKey()).getName());
            mapZ.put("zr", zr.replace(",,", ""));
            mapZ.put("xg", xg.replace(",,", ""));
            
            
            double riskLevel = o_showAssessBO.getRiskScore(Double.parseDouble(mapZ.get("yx").toString()), Double.parseDouble(mapZ.get("fs").toString()));
            
            mapZ.put("level", String.valueOf(riskLevel));
            String riskIcon =""; 
            	DictEntry dict =  o_kpiBO.findKpiAlarmStatusByValues(alarmPlanAllMap.get(riskAllMap.get(s.getKey()).getAlarmScenario().getId()), riskLevel);
            	if(null!= dict){
            		riskIcon = dict.getValue();
            	}
            mapZ.put("riskIcon", riskIcon);
            
            
            if(isAlarm){
	            if(riskIcon.equalsIgnoreCase("icon-ibm-symbol-4-sm")){
	            	//高
	            	arrayaList.add(mapZ);
	            }
            }else{
            	arrayaList.add(mapZ);
            }
        }
        
        return arrayaList;
	}
	
}
