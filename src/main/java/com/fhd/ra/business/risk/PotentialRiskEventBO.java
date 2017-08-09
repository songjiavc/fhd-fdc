package com.fhd.ra.business.risk;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.kpi.StrategyMapDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.process.Process;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;

@Service
@SuppressWarnings("unchecked")
public class PotentialRiskEventBO {
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	
	@Autowired
	private StrategyMapDAO o_strategyMapDAO;
	
	@Autowired
	private ProcessDAO o_processDAO;
	
	/**
	 * 根据风险事件构建风险树
	 * 因为传入的数组包括风险分类，根据风险分类显示灯图标，这样会存在传入的风险分类数据不正确导入灯显示错误的问题。可能以后灯显示放入前台判断
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param eventIds		不能为空
	 * @return
	 */
	public List<Map<String, Object>> getRiskIdentifyTidyTreeRecordByEventIds(String node, String query, String[] eventIds, Map<String,String> nodeMap){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		
		Set<String> idSet = queryObjectBySearchName(query,eventIds);
		if(StringUtils.isNotBlank(query)&&idSet.size()==0){
			return nodes;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();

		Criteria criteria = o_riskDAO.createCriteria();
		if (StringUtils.isNotBlank(node)) {
			if(node.equalsIgnoreCase("root")){
				criteria.add(Restrictions.isNull("parent.id"));
			}else{
				criteria.add(Restrictions.eq("parent.id", node));
			}
			
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
			map.put("type", "risk");
			map.put("iconCls", nodeMap.get(risk.getId()));
			map.put("cls", "org");
			//查询该节点是否是子节点：该节点下直接挂传入的风险事件
			List<Risk> children = getRiskChildrenById(risk.getId());
			boolean isLeaf = false;
			for(Risk r : children){
				if(r.getIsRiskClass().equalsIgnoreCase("re")){
					isLeaf = true;
					break;
				}
			}
			map.put("leaf",isLeaf);
			nodes.add(map);
		}
		return nodes;
	}
	
	/**
	 * 根据风险事件构建风险树
	 * 因为传入的数组包括风险分类，根据风险分类显示灯图标，这样会存在传入的风险分类数据不正确导入灯显示错误的问题。可能以后灯显示放入前台判断
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param eventIds		不能为空
	 * @return
	 */
	public List<Map<String, Object>> getRiskTreeRecordByEventIds(String node, String query, String[] eventIds, Map<String,String> nodeMap){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		
		Set<String> idSet = queryObjectBySearchName(query,eventIds);
		if(StringUtils.isNotBlank(query)&&idSet.size()==0){
			return nodes;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();

		Criteria criteria = o_riskDAO.createCriteria();
		if (StringUtils.isNotBlank(node)) {
			if(node.equalsIgnoreCase("root")){
				criteria.add(Restrictions.isNull("parent.id"));
			}else{
				criteria.add(Restrictions.eq("parent.id", node));
			}
			
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
			map.put("type", "risk");
			map.put("iconCls", nodeMap.get(risk.getId()));
			map.put("cls", "org");
			//查询该节点是否是子节点：该节点下直接挂传入的风险事件
			List<Risk> children = getRiskChildrenById(risk.getId());
			boolean isLeaf = false;
			for(Risk r : children){
				if(r.getIsRiskClass().equalsIgnoreCase("re") && nodeMap.get(r.getId())!=null){
					isLeaf = true;
					break;
				}
			}
			map.put("leaf",isLeaf);
			nodes.add(map);
		}
		return nodes;
	}
	
	/**
	 * eventIds可能也包含了风险分类，但是不影响
	 * @param query
	 * @param eventIds
	 * @return
	 */
	protected Set<String> queryObjectBySearchName(String query,String[] eventIds){
		List<Risk> list = new ArrayList<Risk>();
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_riskDAO.createCriteria();
		//关联风险事件
		criteria.createAlias("children", "riskEvent",CriteriaSpecification.INNER_JOIN);
		criteria.add(Restrictions.in("riskEvent.id", eventIds));
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
		}
		list = criteria.list();
		
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
	 * 获取风险下的儿子节点
	 * @param id
	 * @return
	 */
	private List<Risk> getRiskChildrenById(String id){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("parent.id", id));
		return criteria.list();
	}
	/**
	 * 根据风险事件构建组织树
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param eventIds		不能为空
	 * @param nodeMap  key:id  value:评估灯的值
	 * @return
	 */
	public List<Map<String, Object>> getOrgTreeRecordByEventIds(String node, String query, String[] eventIds,Map<String,String> nodeMap){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		
		Set<String> idSet = queryOrgObjectBySearchName(query,eventIds);
		if(StringUtils.isNotBlank(query)&&idSet.size()==0){
			return nodes;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = o_sysOrgDAO.createCriteria();
		if (StringUtils.isNotBlank(node)) {
			if(node.equalsIgnoreCase("root")){
				//criteria.add(Restrictions.isNull("parentOrg.id"));
				//不显示集团，显示这个人所在公司
				String companyId = UserContext.getUser().getCompanyid();
				criteria.add(Restrictions.eq("parentOrg.id", companyId));
			}else{
				criteria.add(Restrictions.eq("parentOrg.id", node));
			}
			
		}
		criteria.addOrder(Order.asc("sn"));
		
		List<SysOrganization> list = criteria.list();

		for (SysOrganization org : list) {
			if(!idSet.contains(org.getId())){
				continue;
			}
			map = new HashMap<String, Object>();
			map.put("id", org.getId());
			map.put("text", org.getOrgname());
			//查询该节点是否是子节点：该节点和风险事件是否有关联关系
			boolean isLeaf = isOrgLeafNode(org.getId(),eventIds);
			map.put("leaf",isLeaf);
			map.put("iconCls", nodeMap.get(org.getId()));
			nodes.add(map);
		}
		return nodes;
	}
	protected Set<String> queryOrgObjectBySearchName(String query,String[] eventIds){
		List<SysOrganization> list = new ArrayList<SysOrganization>();
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_sysOrgDAO.createCriteria();
		//关联风险事件
		criteria.createAlias("orgRisks", "orgRisks");
		criteria.createAlias("orgRisks.risk", "riskEvent");
		String[] orgType = new String[]{"0orgtype_d","0orgtype_sd"};
		criteria.add(Restrictions.in("orgType", orgType));
		criteria.add(Restrictions.in("riskEvent.id", eventIds));
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
		}
		list = criteria.list();
		
		for (SysOrganization entity : list) {
			if(null==entity.getOrgseq()){
				continue;
			}
			String[] idsTemp = entity.getOrgseq().split("\\.");
			idSet.addAll(Arrays.asList(idsTemp));
		}
		return idSet;
	}
	/**
	 * 判断组织节点是否是叶子节点
	 * @return
	 */
	private boolean isOrgLeafNode(String orgId,String[] eventIds){
		String idStr = "";
		for(int i=0;i<eventIds.length;i++){
			idStr += "'"+eventIds[i]+"'" + ",";
		}
		if(idStr.length()>0){
			idStr = idStr.substring(0,idStr.length()-1);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct o.id")
		.append(" FROM t_sys_organization o,t_rm_risk_org orgrisk,t_rm_risks r")
		.append(" where orgrisk.risk_id = r.id and orgrisk.org_id = o.id")
		.append(" and o.id_seq like '%." + orgId + ".%' and o.id != '"+orgId+"'")
		.append(" and r.id in (" + idStr + ")");
		
		SQLQuery sqlQuery = o_sysOrgDAO.createSQLQuery(sql.toString());
		int size = sqlQuery.list().size();
		
		return size<1;
	}
	
	/**
	 * 根据风险事件构建目标树
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param eventIds		不能为空
	 * @return
	 */
	public List<Map<String, Object>> getStrategyMapTreeRecordByEventIds(String node, String query, String[] eventIds,Map<String,String> nodeMap,Map<String,String> kpiMap){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		
		Set<String> idSet = queryStrategyMapObjectBySearchName(query,eventIds);
		if(StringUtils.isNotBlank(query)&&idSet.size()==0){
			return nodes;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = o_strategyMapDAO.createCriteria();
		if (StringUtils.isNotBlank(node)) {
			if(node.equalsIgnoreCase("root")){
				criteria.add(Restrictions.isNull("parent.id"));
			}else{
				criteria.add(Restrictions.eq("parent.id", node));
			}
			
		}
		criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		criteria.addOrder(Order.asc("sort"));
		
		List<StrategyMap> list = criteria.list();

		for (StrategyMap sm : list) {
			if(!idSet.contains(sm.getId())){
				continue;
			}
			map = new HashMap<String, Object>();
			map.put("id", sm.getId());
			map.put("text", sm.getName());

            int size = findFiltedKpiBySmId(sm.getId(),eventIds).size();
            if(size>0){
            	map.put("leaf",false);
            }else{
            	map.put("leaf",true);
            }
            map.put("iconCls", nodeMap.get(sm.getId()));
			nodes.add(map);
		}
		
		//查询指标节点
		if(!"root".equals(node)){
            List<Object[]> kpiList = findFiltedKpiBySmId(node,eventIds);
            for (Object[] objs : kpiList) {
                map = new HashMap<String,Object>();
                String id = node + "_" + objs[0];
                map.put("id", id);
                map.put("text", objs[1]);
                map.put("leaf", true);
                map.put("iconCls", kpiMap.get(objs[0]));
                nodes.add(map);
            }
        }
		
		return nodes;
	}
	protected Set<String> queryStrategyMapObjectBySearchName(String query,String[] eventIds){
		List<StrategyMap> list = new ArrayList<StrategyMap>();
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_strategyMapDAO.createCriteria();
		//关联风险事件 查询的有重复的记录，但是通过idSet，就去除的重复的id值
		criteria.createAlias("smRelaKpi", "smRelaKpi",CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("smRelaKpi.kpi", "kpi",CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("kpi.kpiRelaRisks", "kpiRelaRisks",CriteriaSpecification.LEFT_JOIN,Restrictions.eq("type", "I"));	//只关联影响指标
		criteria.createAlias("kpiRelaRisks.risk", "risk",CriteriaSpecification.LEFT_JOIN);
		criteria.add(Restrictions.in("risk.id", eventIds));
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
		}
		list = criteria.list();
		
		for (StrategyMap entity : list) {
			if(null==entity.getIdSeq()){
				continue;
			}
			String[] idsTemp = entity.getIdSeq().split("\\.");
			idSet.addAll(Arrays.asList(idsTemp));
		}
		return idSet;
	}
	/**
	 * 根据过滤条件，查询目标节点下的指标节点
	 * @return
	 */
	private List<Object[]> findFiltedKpiBySmId(String stragetyId,String[] eventIds){

		String idStr = "";
		for(int i=0;i<eventIds.length;i++){
			idStr += "'"+eventIds[i]+"'" + ",";
		}
		if(idStr.length()>0){
			idStr = idStr.substring(0,idStr.length()-1);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct k.id,k.kpi_name")
		.append(" from t_kpi_kpi k,t_kpi_sm_rela_kpi sk,T_KPI_KPI_RELA_RISK kr,t_rm_risks r")
		.append(" where sk.kpi_id = k.id and kr.risk_id =  r.id and kr.kpi_id = k.id")
		.append(" and sk.strategy_map_id = '" + stragetyId + "'")
		.append(" and kr.etype='I'")	//只查询影响指标
		.append(" and r.id in (" + idStr + ")");
		
		SQLQuery sqlQuery = o_processDAO.createSQLQuery(sql.toString());
		List<Object[]> list = sqlQuery.list();
		
		return list;
	}
	
	/**
	 * 根据风险事件构建流程树
	 * @author 郑军祥
	 * @param node			nodeId
	 * @param query		  	查询条件
	 * @param eventIds		不能为空
	 * @return
	 */
	public List<Map<String, Object>> getProcessTreeRecordByEventIds(String node, String query, String[] eventIds,Map<String,String> nodeMap){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		
		Set<String> idSet = queryProcessObjectBySearchName(query,eventIds);
		if(StringUtils.isNotBlank(query)&&idSet.size()==0){
			return nodes;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		Criteria criteria = o_processDAO.createCriteria();
		if (StringUtils.isNotBlank(node)) {
			if(node.equalsIgnoreCase("root")){
				criteria.add(Restrictions.isNull("parent.id"));
			}else{
				criteria.add(Restrictions.eq("parent.id", node));
			}
			
		}
		criteria.addOrder(Order.asc("sort"));
		
		List<Process> list = criteria.list();

		for (Process ps : list) {
			if(!idSet.contains(ps.getId())){
				continue;
			}
			map = new HashMap<String, Object>();
			map.put("id", ps.getId());
			map.put("text", ps.getName());
			//查询该节点是否是子节点：该节点和风险事件是否有关联关系
			boolean isLeaf = isProcessLeafNode(ps.getId(),eventIds);
			map.put("leaf",isLeaf);
			map.put("iconCls", nodeMap.get(ps.getId()));
			nodes.add(map);
		}
		return nodes;
	}
	protected Set<String> queryProcessObjectBySearchName(String query,String[] eventIds){
		List<Process> list = new ArrayList<Process>();
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_processDAO.createCriteria();
		//关联风险事件
		criteria.createAlias("processRelaRisks", "processRelaRisks",CriteriaSpecification.INNER_JOIN,Restrictions.eq("type", "I"));	//只关联影响流程
		criteria.createAlias("processRelaRisks.risk", "risk");
		criteria.add(Restrictions.in("risk.id", eventIds));
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name",query,MatchMode.ANYWHERE));
		}
		list = criteria.list();
		
		for (Process entity : list) {
			if(null==entity.getIdSeq()){
				continue;
			}
			String[] idsTemp = entity.getIdSeq().split("\\.");
			idSet.addAll(Arrays.asList(idsTemp));
		}
		return idSet;
	}
	/**
	 * 判断流程节点是否是叶子节点
	 * @return
	 */
	private boolean isProcessLeafNode(String processId,String[] eventIds){
		
		String idStr = "";
		for(int i=0;i<eventIds.length;i++){
			idStr += "'"+eventIds[i]+"'" + ",";
		}
		if(idStr.length()>0){
			idStr = idStr.substring(0,idStr.length()-1);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct p.id")
		.append(" from t_ic_processure p,t_processure_risk_processure pr,t_rm_risks r")
		.append(" where pr.risk_id = r.id and pr.processure_id = p.id")
		.append(" and p.id_seq like '%." + processId + ".%' and p.id != '"+processId+"'")
		.append(" and r.id in (" + idStr + ")");
		
		SQLQuery sqlQuery = o_processDAO.createSQLQuery(sql.toString());
		int size = sqlQuery.list().size();
		
		return size<1;
	}
}
