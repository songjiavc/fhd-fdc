package com.fhd.ra.business.assess.oper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.assess.formulatePlan.RiskOrgTempDAO;
import com.fhd.dao.assess.formulatePlan.RiskScoreDeptDAO;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Service
public class ScoreDeptBO {

	@Autowired
	private RiskScoreDeptDAO o_riskScoreDeptDAO;
	
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	
	@Autowired
	private RiskOrgTempDAO o_riskScoreDeptTempDAO;
	
	/**
	 * 通过风险ID,得到同一部门打分的风险
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<String> getDeptRisks(ArrayList<ArrayList<String>> finDeptByRiskAllList, String riskId){
		ArrayList<String> arrayList = new ArrayList<String>();
		for (ArrayList<String> arrayList2 : finDeptByRiskAllList) {
			for (String string : arrayList2) {
				if(riskId.equalsIgnoreCase(string)){
					arrayList.add(arrayList2.get(1) + "--" + arrayList2.get(2));//存放综合ID
				}
			}
		}
		return arrayList;
	}
	
	/**
	 * 通过风险ID,得到同一部门打分的风险(汇总风险事件而用)
	 * @return ArrayList<HashMap<String, String>>
	 * @author 金鹏祥
	 * */
	public ArrayList<String> getDeptRisks(ArrayList<ArrayList<String>> finDeptByRiskAllList, String riskId, String orgId){
		ArrayList<String> arrayList = new ArrayList<String>();
		for (ArrayList<String> arrayList2 : finDeptByRiskAllList) {
			for (String string : arrayList2) {
				if(string.indexOf("--") != -1){
					String strs[] = string.split("--");
					String strRiskId = strs[0];
					String strOrgId = strs[1];
					if(riskId.equalsIgnoreCase(strRiskId)){
						if(orgId.equalsIgnoreCase(strOrgId)){
							arrayList.add(arrayList2.get(1) + "--" + arrayList2.get(2));//存放综合ID
						}
					}
				}
			}
		}
		return arrayList;
	}
	
	/**
	 * 通过打分对象ID得到打分部门
	 * */
	@SuppressWarnings("unchecked")
	public List<RiskScoreDept> findRiskScoreDeptByScoreObjectId(String scoreObjectId){
		Criteria criteria = o_riskScoreDeptDAO.createCriteria();
		criteria.add(Restrictions.eq("scoreObject.id", scoreObjectId));
		
		List<RiskScoreDept> list = criteria.list();
		
		return list;
	}
	
	/**
	 * 根据打分对象id查询打分部门实体
	 * @param ObjId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreDept> findRiskDeptByObjId(String objId) {
		Criteria c = o_riskScoreDeptDAO.createCriteria();
		List<RiskScoreDept> list = null;
		if (StringUtils.isNotBlank(objId)) {
			c.add(Restrictions.eq("scoreObject.id", objId));
		} else {
			return null;
		}
		c.addOrder(Order.asc("organization"));
		list = c.list();
		return list;
	}
	
	/**
	 * 判断打分部门中是否存在该部门
	 * @param ObjId	打分对象id
	 * @param orgId	部门id
	 * @param type	打分部门类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreDept> findRiskScoreDeptIsSave(String objId, String orgId, String type) {
		Criteria c = o_riskScoreDeptDAO.createCriteria();
		List<RiskScoreDept> list = null;
		c.add(Restrictions.and(Restrictions.eq("scoreObject.id", objId), 
			Restrictions.and(Restrictions.eq("organization.id", orgId),Restrictions.eq("orgType", type))));
		list = c.list();
		return list;
	}
	
	@Autowired
	private RiskOrgTempDAO riskOrgTempDAO;
	/**
	 * 通过计划id查询所有打分部门的部门id
	 * @param planId 计划id
	 * @return
	 */
	public List<String> findScoreDeptIdsByPlanId(String planId){
		List<String> deptIdlist = new ArrayList<String>();//打分部门id集合
		List<RiskScoreObject> entityList = o_scoreObjectBO.findRiskScoreObjByplanId(planId);
		List<String> objIdlist = new ArrayList<String>();//打分对象id集合
		for(RiskScoreObject scoreObj : entityList){
			objIdlist.add(scoreObj.getId());
		}
		//打分对象map集合
		List<Map<String,Object>> deptMapList = this.findRiskDeptByObjIdList(objIdlist);
		for(Map<String,Object> deptMap : deptMapList){
			RiskOrgTemp scoreDept = (RiskOrgTemp)deptMap.get("scoreDept");
			if(!deptIdlist.contains(scoreDept.getSysOrganization().getId())){
				deptIdlist.add(scoreDept.getSysOrganization().getId());
			}
		}
		return deptIdlist;
	}
	
	/**
	 * 根据计划id查打分部门集合
	 * @param planId 计划id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findScoreDeptsByPlanId(String planId){
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<RiskOrgTemp> listMap = new ArrayList<RiskOrgTemp>();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.ID,b.RISK_ID,a.ETYPE,a.ORG_ID,a.PLAN_ID,a.`STATUS`,a.OBJECT_ID from t_rm_risk_org_temp a LEFT JOIN t_rm_risk_score_object b ON a.OBJECT_ID= b.ID WHERE a.PLAN_ID=:planId");
		sql.append(" and  (a.ETYPE = 'M' OR (a.etype = 'A' AND b.DELETE_ESTATUS != '2'))");
		SQLQuery sqlQuery = this.riskOrgTempDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("planId", planId);
		
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
            String id = "";
            String riskId = "";
            String eType = "";
            String orgId = "";
            String status = "";
            String objectId = "";
            
            if(null != o[0]){
            	id = o[0].toString();
            }if(null != o[1]){
            	riskId = o[1].toString();
            }if(null != o[2]){
            	eType = o[2].toString();
            }if(null != o[3]){
            	orgId = o[3].toString();
            }if(null != o[4]){
            	planId = o[4].toString();
            }if(null != o[5]){
            	status = o[5].toString();
            }if(null != o[6]){
            	objectId = o[6].toString();
            }
            RiskOrgTemp riskOrgTemp = new RiskOrgTemp();
            riskOrgTemp.setId(id);
            riskOrgTemp.setType(eType);
            riskOrgTemp.setPlanId(planId);
            riskOrgTemp.setRiskScoreObject(new RiskScoreObject(objectId));
            riskOrgTemp.setSysOrganization(new SysOrganization(orgId));
            listMap.add(riskOrgTemp);
		}
		map.put("scoreDept", listMap);
		return map;
	}
	
	/**
	 * 吉志强备份原逻辑代码 根据计划id查打分部门集合
	 * 2017年5月13日06:24:49
	 * @param planId
	 * @return
	 */
	public List<Map<String,Object>> findScoreDeptsByPlanId_bak(String planId){
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		List<String> objIdlist = new ArrayList<String>();
		List<RiskScoreObject> entityList = o_scoreObjectBO.findRiskScoreObjByplanId(planId);
		
		for(RiskScoreObject scoreObj : entityList){
			objIdlist.add(scoreObj.getId());
		}
		//打分对象map集合
		List<Map<String,Object>> deptMapList = this.findRiskDeptByObjIdList(objIdlist);
		for(Map<String,Object> deptMap : deptMapList){
			RiskScoreDept scoreDept = (RiskScoreDept)deptMap.get("scoredept");
			HashMap<String,Object> map = new HashMap<String, Object>();
			map.put("scoreDept", scoreDept);
			listMap.add(map);
		}
		return listMap;
	}
	
	
	
	/**
	 * 根据打分对象id集合查打分部门
	 * @param ObjIds	打分对象id集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findRiskDeptByObjIdList(List<String> ObjIds) {
		Criteria c = o_riskScoreDeptTempDAO.createCriteria();
		List<RiskOrgTemp> list = null;
		if(ObjIds.size()>0){
			c.add(Restrictions.in("riskScoreObject.id", ObjIds));
			c.addOrder(Order.asc("sysOrganization"));
			list = c.list();
		}
		
		List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
		if(null!= list){
			for(RiskOrgTemp scoreDept : list){
				Map<String, Object> exitObjMap = new HashMap<String, Object>();
				exitObjMap.put("scoreDept", scoreDept);
				exitObjMap.put(scoreDept.getSysOrganization().getId(), scoreDept.getSysOrganization().getId());//部门id
				mapList.add(exitObjMap);
			}
		}
		return mapList;
	}
	
	/**
	 * 根据部门id查打分部门实体
	 * @param deptId 部门id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreDept> findRiskDeptsBydeptId(String deptId) {
		Criteria c = o_riskScoreDeptDAO.createCriteria();
		List<RiskScoreDept> list = null;
		if (StringUtils.isNotBlank(deptId)) {
			c.add(Restrictions.eq("organization.id", deptId));
		} 
		list = c.list();
		return list;
	}
	
	/**
	 * 通过打分对象查询主责、相关部门(scoreObjectId,orgType--orgName)
	 * @author 金鹏祥
	 * @return String
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<String>> findOrgByScoreObjectIdMapAll(String planIdQuery){
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		StringBuffer sql = new StringBuffer();
		ArrayList<String> arrayList = null;
		sql.append(" select a.score_object_id, b.org_name,a.org_type,b.id from t_rm_risk_score_dept a ");
		sql.append(" LEFT JOIN t_sys_organization b on a.org_id = b.id ");
		sql.append(" LEFT JOIN t_rm_risk_score_object c on c.id = a.score_object_id ");
		sql.append(" where c.assess_plan_id=:planIdQuery ");
		
		SQLQuery sqlQuery = o_riskScoreDeptDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("planIdQuery", planIdQuery);
		List<Object[]> list = sqlQuery.list();
		
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String scoreObjectId = "";
        	String orgName = "";
        	String orgType = "";
        	String orgId = "";
        	
        	if(null != objects[0]){
        		scoreObjectId = objects[0].toString();
            }if(null != objects[1]){
            	orgName = objects[1].toString();
            }if(null != objects[2]){
            	orgType = objects[2].toString();
            }if(null != objects[3]){
            	orgId = objects[3].toString();
            }
            
        	if(map.get(scoreObjectId) != null){
        		map.get(scoreObjectId).add(orgType + "--" + orgName + "--" + orgId);
        	}else{
        		arrayList = new ArrayList<String>();
        		arrayList.add(orgType + "--" + orgName + "--" + orgId);
        		map.put(scoreObjectId, arrayList);
        	}
        }
        
		return map;
	}
	/**
	 * 查询计划下所有打分对象打分部门和风险id
	 * add by 王再冉
	 * 2014-2-19  下午5:09:47
	 * desc : 
	 * @param planId 计划id
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	public HashMap<String, ArrayList<String>> findAllSaveScoresByplanId(String planId) {
		StringBuffer sql = new StringBuffer();
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> arrayList = null;
		sql.append(" SELECT so.id,so.risk_id,sd.id  scoredeptid,sd.org_id,sd.org_type ");
		sql.append(" FROM t_rm_risk_score_dept sd ");
		sql.append(" LEFT JOIN t_rm_risk_score_object so ON so.id = sd.score_object_id ");
		sql.append(" WHERE so.assess_plan_id = :planId AND so.risk_id is not NULL ORDER BY so.risk_id");
		SQLQuery sqlQuery = o_riskScoreDeptDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String objId = "";
			String riskId = "";
			String scoreDeptId = "";
			String orgId = "";
			String orgType = "";
            
            if(null != o[0]){
            	objId = o[0].toString();
            }if(null != o[1]){
            	riskId = o[1].toString();
            }if(null != o[2]){
            	scoreDeptId = o[2].toString();
            }if(null != o[3]){
            	orgId = o[3].toString();
            }if(null != o[4]){
            	orgType = o[4].toString();
            }
            
            if(map.get(riskId) != null){
        		map.get(riskId).add(objId + "--" + scoreDeptId + "--" + orgId + "--" + orgType);
        	}else{
        		arrayList = new ArrayList<String>();
        		arrayList.add(objId + "--" + scoreDeptId + "--" + orgId + "--" + orgType);
        		map.put(riskId, arrayList);
        	}
		}
        return map;
	}
}
