package com.fhd.sys.business.assess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.quaAssess.FormulaSetDAO;
import com.fhd.dao.comm.AlarmPlanDAO;
import com.fhd.dao.risk.DimensionDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.sys.assess.WeightSetDAO;
import com.fhd.dao.sys.autho.SysoRoleDAO;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.assess.FormulaSet;
import com.fhd.entity.sys.assess.WeightSet;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.oper.FormulaSetBO;
import com.fhd.ra.business.assess.oper.TemplateRelaDimensionBO;
import com.fhd.ra.business.risk.DimensionBO;

@Service
public class WeightSetBO {

	@Autowired
	private WeightSetDAO o_weightSetDAO;
	@Autowired
	private FormulaSetDAO o_formulaSetDAO;
	@Autowired
	private AlarmPlanDAO o_alarmPlanDAO;
	@Autowired
	private RiskDAO o_riskDAO;
	@Autowired
	private DimensionDAO o_dimensionDAO;
	@Autowired
	private TemplateRelaDimensionBO o_templateRelaDimensionBO;
	
	@Autowired
	private FormulaSetBO o_formulaSetBO;
	@Autowired
	private SysoRoleDAO o_sysoRoleDAO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	/**汇总保存*/
	public String updateValue(String summarizings){
		String summarizingsStrs[] = summarizings.split(",");
		String summarizing = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid()).getSummarizing();//risk:true--org:true--strategy:true--process:true
		String strs[] = summarizing.split("--");
		String temp = "";
		for (String str : summarizingsStrs) {
			for (String types : strs) {
				if(types.indexOf(str) != -1){
					for (String s : types.toString().split(",")) {
						if(s.indexOf(str) != -1){
							temp = s;
							s = s.replace("0", "1");
							s = s.replace("1", "1");
							summarizing = summarizing.replace(temp, s);
						}else{
							temp = s;
							s = s.replace("0", "0");
							s = s.replace("1", "0");
							summarizing = summarizing.replace(temp, s);
						}
					}
				}
			}
		}
		System.out.println(summarizing + "aa");
		return summarizing;
	}
	
	/**汇总初始化(得到组织、目标、流程、指标) 是否汇总MAP
	 * @param summarizings 初始化汇总字符串
	 * @return HashMap<String, String>
	 * @author 金鹏祥
	 * */
	public HashMap<String, String> findInitValue(String summarizings){
		String summarizingsStrs[] = summarizings.split(",");
		HashMap<String, String> summarizingMap = new HashMap<String, String>();
		FormulaSet formulateSet = o_formulaSetBO.findFormulaSet(UserContext.getUser().getCompanyid());
		//if(null != formulateSet){
			String summarizing = formulateSet.getSummarizing();//risk:true--org:true--strategy:true--process:true
			String strs[] = summarizing.split("--");
			for (String str : summarizingsStrs) {
				for (String types : strs) {
					if(types.indexOf(str) != -1){
						for (String s : types.toString().split(",")) {
							if(s.indexOf("1") != -1){
								summarizingMap.put(str, s.split(":")[0]);
								break;
							}
						}
					}
				}
			}
		//}
		return summarizingMap;
	}
	
	/**
	 * 查询权重设置所有数据
	 * @param companyId 公司ID
	 * @return HashMap<String,Double>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String,Double> findWeightSetAllMap(String companyId){
		Criteria criteria = o_weightSetDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		List<WeightSet> weightSetList = criteria.list();
		double totleRoleWeight = 0;
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (WeightSet weightSet : weightSetList) {
			map.put("dutyDeptWeight", Double.parseDouble(weightSet.getObjectKey()));//责任部门权重
			map.put("relatedDeptWeight", Double.parseDouble(weightSet.getRelatedDeptWeight()));//相关部门权重
			// 去掉参与部门
			map.put("totleDeptWeight", Double.parseDouble(weightSet.getObjectKey()) + Double.parseDouble(weightSet.getRelatedDeptWeight()));
			// 协助部门不用 map.put("assistDeptWeight", Double.parseDouble(weightSet.getAssistDeptWeight()));//辅助(参与)部门权重
			// 领导权重不用 map.put("leadWeight", Double.parseDouble(weightSet.getLeadWeight()));//领导权重
			// 员工权重不用 map.put("seaffWeight", Double.parseDouble(weightSet.getStaffWeight()));//员工权重
			String roleWeight = weightSet.getRoleWeight();   //
			if(StringUtils.isNotBlank(roleWeight)){
				String[] roles = roleWeight.split(";");
				for(String role : roles){
					String[] roleWeights = role.split(",");
					map.put(roleWeights[0], Double.parseDouble(roleWeights[1]));
					totleRoleWeight = totleRoleWeight + Double.parseDouble(roleWeights[1]);
				}
			}
		}
		map.put("totleRoleWeight", totleRoleWeight);
		return map;
	}
	
	/**
	 * 查询权重配置实体
	 * @param companyId 公司ID
	 * @return WeightSet
	 * @author 金鹏祥
	 */
	@SuppressWarnings("unchecked")
	public WeightSet findWeightSetAll(String companyId) {
		Criteria criteria = o_weightSetDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		List<WeightSet> list = criteria.list();
		if(null!=list&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 保存部门权重
	 * @param weiSet
	 */
	@Transactional
	public void saveWeightSetDept(WeightSet weiSet) {
		o_weightSetDAO.merge(weiSet);
	}
	
	
	/**
	 * 根据id查询权重实体
	 * @param weiSet
	 */
	@Transactional
	public WeightSet findWeightSetById(String id) {
		return o_weightSetDAO.get(id);
	}
	
	/**
	 * 保存公式计算实体
	 * @param formula
	 */
	@Transactional
	public void saveFormulaSet(FormulaSet formula) {
		o_formulaSetDAO.merge(formula);
	}
	
	/**
	 * 通过预警方案类型查找预警方案
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AlarmPlan> findAlarmPlanByAlarmType(String type){
		Criteria criteria = o_alarmPlanDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		if("0alarm_type_rm".equals(type)){
			criteria.add(Restrictions.eq("type.id", type));
		}
		List<AlarmPlan> list = criteria.list();
		if(null!=list&&list.size()>0){
			return list;
		}
		return null;
	}
	/**
	 * 查询登录人所在公司下的风险
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Risk> findRisksByAlarmType(){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		List<Risk> list = criteria.list();
		if(null!=list&&list.size()>0){
			return list;
		}
		return null;
	}
	
	/**
	 * 公式编辑树加载
	 * @param id
	 * @param query
	 * @return
	 */
	public List<Map<String, Object>> treeLoader(String sqrt, String jtId) {
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		/*String companyId = UserContext.getUser().getCompanyid();
		if(StringUtils.isNotBlank(companyId)){
			dimenList = this.findDimensionsBycompanyId(companyId);
		}*/
		List<Dimension> dimenList = o_dimensionBO.findDimensionsByCompanyId(UserContext.getUser().getCompanyid(), jtId);
		for(Dimension dimen : dimenList){
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", dimen.getId());
			if(StringUtils.isNotBlank(sqrt)){
				item.put("text", "SQRT(<" + dimen.getName() + ">)");
			}else{
				item.put("text", dimen.getName());
			}
			item.put("leaf", true);
			nodes.add(item);
		}
		return nodes;
	}
	
	/**
	 * 根据维度id查维度
	 * @param dimensionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Dimension findDimensionBydimId(String dimensionId){
		Criteria criteria = o_dimensionDAO.createCriteria();
		criteria.add(Restrictions.eq("id", dimensionId));
		List<Dimension> list = criteria.list();
		if(null!=list&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 根据companyId查维度
	 * @param companyId	登录人的公司id
	 * @return	
	 */
	@SuppressWarnings("unchecked")
	public List<Dimension> findDimensionsBycompanyId(String companyId){
		Criteria criteria = o_dimensionDAO.createCriteria();
		List<Dimension> list = null;
		criteria.add(Restrictions.eq("company.id", companyId));
		list = criteria.list();
		return list;
	}
	/**
	 * 查询所有维度
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Dimension> findDimensionsAll(){
		Criteria criteria = o_dimensionDAO.createCriteria();
		List<Dimension> list = null;
		list = criteria.list();
		return list;
	}
	
	/**
	 * 查询主维度集合
	 * @param 
	 * @return
	 */
	public List<Map<String, Object>> findDimensionIsParentIdIsNullAllMapObj(){
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
		List<Dimension> dimensionList = new ArrayList<Dimension>();//主维度
		String companyId = UserContext.getUser().getCompanyid();
		//查询主维度
		HashMap<String, TemplateRelaDimension> tempRelaDimMap = 
								o_templateRelaDimensionBO.findTemplateRelaDimensionIsParentIdIsNullAllMap(companyId);
		Set<Entry<String, TemplateRelaDimension>> tempRelaDimKey = tempRelaDimMap.entrySet();
        for (Iterator<Entry<String, TemplateRelaDimension>> it = tempRelaDimKey.iterator(); it.hasNext();) {
            String mapKey = it.next().getKey();
            if(!dimensionList.contains(tempRelaDimMap.get(mapKey).getDimension())){
            	dimensionList.add(tempRelaDimMap.get(mapKey).getDimension());
            	Map<String, Object> map = new HashMap<String, Object>();
    			map.put("dimId", tempRelaDimMap.get(mapKey).getDimension().getId());
    			map.put("dimName", tempRelaDimMap.get(mapKey).getDimension().getName());
    			mapList.add(map);
            }
        }
		return mapList;
	}
	
	/**
	 * 查询所有保存的角色编号集合
	 * @return List<String>
	 * @author 金鹏祥
	 */
	public List<String> findRoleCodes(){
		List<String> roleCodeList = new ArrayList<String>();
		String companyId = UserContext.getUser().getCompanyid();
		WeightSet weigSet = this.findWeightSetAll(companyId);
		if(null != weigSet && StringUtils.isNotBlank(weigSet.getRoleWeight())){
			String roleWeight = weigSet.getRoleWeight();
			String idArray[] = roleWeight.split(";");
			for(String roWeightId : idArray){
				String[] roleIdAndWeiId = roWeightId.split(",");
				roleCodeList.add(roleIdAndWeiId[0]);
			}
		}
		return roleCodeList;
	}
	
	/**
	 * 查询角色编号权重Map
	 * @return HashMap<String, Double>
	 * @author 金鹏祥
	 */
	public HashMap<String, Double> findRoleWeight(){
		HashMap<String, Double> map = new HashMap<String, Double>();
		String roleWeight = "";
		String companyId = UserContext.getUser().getCompanyid();
		WeightSet weight = this.findWeightSetAll(companyId);
		if(null != weight && StringUtils.isNotBlank(weight.getRoleWeight())){
			roleWeight = weight.getRoleWeight();
			String idArray[] = roleWeight.split(";");
			for (String roWeightId : idArray) {
				  String[] roleIdAndWeiId = roWeightId.split(",");//用分号拼接的打分对象和打分部门id，第一个是打分对象id
				  if(roleIdAndWeiId.length>1){
					  map.put(roleIdAndWeiId[0], Double.valueOf(roleIdAndWeiId[1]));
				  }
			  }
		}
		return map;
	}
	
	/**
	 * 根据角色编号集合查询角色
	 * @param roleCodes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findSysRoleByRoleCodes(Set<String> roleCodes){
		Map<String,Object> roleMap = new HashMap<String, Object>();
		List<SysRole> roleList = new ArrayList<SysRole>();
		Criteria criteria = o_sysoRoleDAO.createCriteria();
		if(roleCodes.size()>0){
			criteria.add(Restrictions.in("roleCode", roleCodes));
			roleList = criteria.list();
		}
		for(SysRole role : roleList){
			roleMap.put(role.getRoleCode(), role);
		}
		return roleMap;
	}
	/**
	 * 根据角色id查询角色，返回map
	 * @param roleIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findSysRoleByRoleIds(List<String> roleIds){
		Map<String,Object> roleMap = new HashMap<String, Object>();
		List<SysRole> roleList = new ArrayList<SysRole>();
		Criteria criteria = o_sysoRoleDAO.createCriteria();
		if(roleIds.size()>0){
			criteria.add(Restrictions.in("id", roleIds));
			roleList = criteria.list();
		}
		for(SysRole role : roleList){
			roleMap.put(role.getRoleCode(), role);
		}
		return roleMap;
	}
	/**
	 * 根据角色id集合，初始化职务权重，初始权重为“1”
	 * @param roleIds
	 * @return
	 */
	public String setRoleWeightString(String roleIds, WeightSet weightSet){
		String allWeightStr = "";
		StringBuffer roleWeightStr = new StringBuffer();
		List<String> roleCodeList = new ArrayList<String>();
		String idArray[] = roleIds.split(",");
		for (String roleId : idArray) {
			roleCodeList.add(roleId);
		}
		Map<String,Object> roleMap = this.findSysRoleByRoleIds(roleCodeList);
		Set<String> keySet = roleMap.keySet();
		for(String key : keySet){
			roleWeightStr = roleWeightStr.append(key + "," + "1" + ";");
			//roleWeightStr = roleWeightStr + key + "," + "1" + ";";
		}
		if(StringUtils.isNotBlank(weightSet.getRoleWeight())){
			allWeightStr = weightSet.getRoleWeight()+roleWeightStr.toString();
		}else{
			allWeightStr = roleWeightStr.toString();
		}
		return allWeightStr;
	}
	
	/**
	 * 验证用户是否存在多个配置的角色
	 * @param roleIds
	 * @return
	 */
	@Transactional
	public List<Map<String, Object>> findUserRolesSameByRoleIds(String roleIds,List<String> userIds){
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
		List<SysUser> sameUser = new ArrayList<SysUser>();
		List<String> saveRoleCodes = this.findRoleCodes();//已保存的角色编号
		if(StringUtils.isNotBlank(roleIds)){
			String roleCodesArray[] = roleIds.split(",");//新增的角色编号
			for(String roleCodeStr : roleCodesArray){
				saveRoleCodes.add(roleCodeStr);
			}
		}
		List<SysRole> roles = this.findRolesByRoleCodes(saveRoleCodes);//角色集合
		for(SysRole sysRole : roles){
			Set<SysUser> sysUsers = sysRole.getSysUsers();
			for(SysRole role : roles){
				if(!role.equals(sysRole)){//不相同的角色
					Set<SysUser> users = role.getSysUsers();
					for(SysUser temp : users){
						Map<String, Object> map = new HashMap<String, Object>();
						String userNames = "";
						String roleNames = "";
						if(sysUsers.contains(temp)){//存在相同的用户
							if(userIds.contains(temp.getId())){//只验证任务分配选择的评估人
								if(!sameUser.contains(temp)){
									sameUser.add(temp);//相同用户
									userNames = temp.getRealname();
									roleNames = role.getRoleName()+","+sysRole.getRoleName();
									map.put("userNames", userNames);
									map.put("roleNames", roleNames);
									mapList.add(map);
								}
							}
						}
					}
					/*if(StringUtils.isNotBlank(roleNames)&&StringUtils.isNotBlank(userNames)){
						map.put("userNames", userNames);
						map.put("roleNames", roleNames);
						mapList.add(map);
					}*/
				}
			}
		}
		return mapList;
	}
	
	
	/**
	 * 根据角色编号集合查询角色集合
	 * @param roleIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysRole> findRolesByRoleCodes(List<String> roleCodes){
		List<SysRole> roleList = new ArrayList<SysRole>();
		Criteria criteria = o_sysoRoleDAO.createCriteria();
		if(roleCodes.size()>0){
			criteria.add(Restrictions.in("roleCode", roleCodes));
			roleList = criteria.list();
		}
		return roleList;
	}

}








