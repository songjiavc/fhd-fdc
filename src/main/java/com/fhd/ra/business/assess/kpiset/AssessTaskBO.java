package com.fhd.ra.business.assess.kpiset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.assess.kpiSet.ScoreObjectDeptEmpDAO;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.web.form.assess.formulateplan.RiskScoreObjectForm;
import com.fhd.sys.business.assess.WeightSetBO;
import com.fhd.sys.business.organization.EmpGridBO;
/**
 * 任务分配BO
 * @author 王再冉
 *
 */
@Service
public class AssessTaskBO {
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
	private RiskScoreObjectDAO o_riskScoreObjDAO;
	@Autowired
	private ScoreObjectDeptEmpDAO o_objDeptEmpDAO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private RangObjectDeptEmpBO o_rangObjectDeptEmpBO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private WeightSetBO o_weightSetBO;
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	@Autowired
	private EmpGridBO o_emoGridBO;
	
	/**
	 * 根据评估计划ID 查找当前登录用户所在部门的所有打分对象id
	 * @param empId 登录人员id
	 * @return
	 */
	public List<String> findUserDeptScoreObjIdsByplanIdAndempId(String empId, String businessId) {
		//查评估计划下的打分对象集合
		List<RiskScoreObject> scoreObjList = o_scoreObjectBO.findRiskScoreObjByplanId(businessId);
		List<String> objIdList = new ArrayList<String>();//打分对象id
		//员工部门（主，辅）
		List<SysEmpOrg> empdepts = o_riskScoreBO.findEmpDeptsByEmpId(empId);
		for(RiskScoreObject scoreObj : scoreObjList){
			objIdList.add(scoreObj.getId());
		}
		List<Map<String,Object>> sdeptMapList = o_scoreDeptBO.findRiskDeptByObjIdList(objIdList);
		List<String> findobjIdList = new ArrayList<String>();//打分对象id
		for(Map<String,Object> map : sdeptMapList){
			RiskOrgTemp dept = (RiskOrgTemp)map.get("scoreDept");
			String deptId = dept.getSysOrganization().getId();
			for(SysEmpOrg empDept : empdepts){
				if(empDept.getSysOrganization().getId().equals(deptId)){
					findobjIdList.add(dept.getRiskScoreObject().getId());
				}
			}
		}
		return findobjIdList;
	}
	/**
	 * 任务分配列表查询
	 * add by 王再冉
	 * @param query	搜索关键字
	 * @param page	分页实体
	 * @param sort	排序关键字
	 * @param dir	排序列
	 * @param ObjIds当前登陆者所属部门的打分部门id集合
	 * @param planId计划id
	 * @return 
	 * Page<RiskScoreObject>
	 */
	@RecordLog("查询任务分配列表")
	public Page<RiskScoreObject> findScoreObjsPageByPlanIdAndObjids(String query, Page<RiskScoreObject> page, String sort, String dir, List<String> ObjIds, String planId) {
		DetachedCriteria dc = DetachedCriteria.forClass(RiskScoreObject.class);
		
		if(null != ObjIds && StringUtils.isNotBlank(planId)){
			dc.add(Restrictions.and(Restrictions.in("id", ObjIds), Restrictions.eq("assessPlan.id", planId)));
		}
		/*if(StringUtils.isNotBlank(query)){
		 * dc.createCriteria("risk", "r");
		dc.createCriteria("risk.parent", "rp");
			dc.add(Restrictions.or(Restrictions.like("r.name", query, MatchMode.ANYWHERE), Restrictions.like("rp.name", query, MatchMode.ANYWHERE)));	
		}*/
		
		if("ASC".equalsIgnoreCase(dir)) {
			dc.addOrder(Order.asc(sort));
		} else {
			dc.addOrder(Order.desc(sort));
		}
		return o_riskScoreObjDAO.findPage(dc, page, false);
	}
	/**
	 * 保存综合表（数据库）
	 * @param ode 打分综合对象实体
	 * @return VOID
	 * @author 金鹏祥
	 */
	@Transactional
	public void saveObjectDeptEmp(RangObjectDeptEmp ode) {
		o_objDeptEmpDAO.merge(ode);
	}
	
	/**
	 * 删除对象-部门-人员实体
	 * @param ode	RangObjectDeptEmp实体
	 */
	@Transactional
	@RecordLog("删除打分对象，部门，人员信息")
	public void deleteObjectDeptEmp(RangObjectDeptEmp ode) {
		o_objDeptEmpDAO.delete(ode);
	}
	
	/**
	 * 查询该人员评价的风险数量
	 * @param empId		人员id
	 * @param planId	计划id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RangObjectDeptEmp> findObjDeptEmpListByempIdAndPlanId(String empId, String planId) {
		Criteria c = o_objDeptEmpDAO.createCriteria();
		List<RangObjectDeptEmp> rodeList = new ArrayList<RangObjectDeptEmp>();
		List<RangObjectDeptEmp> list = null;
		c.add(Restrictions.eq("scoreEmp.id", empId));
		list = c.list();
		for(RangObjectDeptEmp rode : list){
			if(planId.equals(rode.getScoreObject().getAssessPlan().getId())){
				rodeList.add(rode);
			}
		}
		return rodeList;
	}
	/**
	 * 查询任务分配列表
	 * @param entityList	打分对象实体集合
	 * @param empId			人员id
	 * @return
	 */
	public List<RiskScoreObjectForm> queryAssessTasksPage(List<RiskScoreObject> entityList,String empId) {
		List<RiskScoreObjectForm> objForms = new ArrayList<RiskScoreObjectForm>();
		List<String> objIdList = new ArrayList<String>();
		String deptId = UserContext.getUser().getMajorDeptId();
		List<String> roleCodes = o_weightSetBO.findRoleCodes();
		SysEmployee riskManager  = o_riskScoreBO.findEmpsByRoleIdAnddeptId(deptId,"DeptRiskManager");
		
		List<RiskScoreObject> scoreObjectList = new ArrayList<RiskScoreObject>();
		List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
		for(RiskScoreObject obj : entityList){
			Map<String, Object> objMap = new HashMap<String, Object>();
			objMap.put("scoreObj", obj);
			mapList.add(objMap);
		}
		for(Map<String,Object> map : mapList){
			objIdList.add(((RiskScoreObject)map.get("scoreObj")).getId());
			scoreObjectList.add((RiskScoreObject)map.get("scoreObj"));
		}
		List<Map<String,Object>> scoreDeptlist = o_scoreDeptBO.findRiskDeptByObjIdList(objIdList);//打分部门集合
		List<Map<String,Object>> rodeMapList = o_rangObjectDeptEmpBO.findobjDeptEmpListByScoreObjIdsMap(objIdList);//评估计划下所有综合实体Map
		
		List<Map<String,Object>> rodeMapListMe = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> rodeMapme : rodeMapList){//登录人部门的综合实体
			RiskOrgTemp rodeScoreDeptMe = (RiskOrgTemp)rodeMapme.get("scoreDept");
			if(deptId.equals(rodeScoreDeptMe.getSysOrganization().getId())){
				rodeMapListMe.add(rodeMapme);
			}
		}
		
		//要保存的综合表
		List<RangObjectDeptEmp> saveRodeList = new ArrayList<RangObjectDeptEmp>();
		for(RiskScoreObject scObj : scoreObjectList){
			List<RiskOrgTemp> scoreDeptList = new ArrayList<RiskOrgTemp>();
			List<RangObjectDeptEmp> rodeList = new ArrayList<RangObjectDeptEmp>();
			for(Map<String,Object> deptmap : scoreDeptlist){
				RiskOrgTemp dept = (RiskOrgTemp)deptmap.get("scoreDept");
				RiskScoreObject obj = dept.getRiskScoreObject();//打分对象
				if(scObj.equals(obj)){
					if(deptId.equals(dept.getSysOrganization().getId())){//判断是否为当前登陆者部门
						scoreDeptList.add(dept);//打分对象相同的打分部门
						if(rodeMapListMe.size()>0){//该部门存在综合实体
							for(Map<String,Object> rodeMap : rodeMapListMe){
								RiskOrgTemp rodeScoreDept = (RiskOrgTemp)rodeMap.get("scoreDept");
								RiskScoreObject rodeScoreObj = (RiskScoreObject)rodeMap.get("scoreObj");
								if(dept.equals(rodeScoreDept)&&obj.equals(rodeScoreObj)){//存在综合实体
									RangObjectDeptEmp rode = (RangObjectDeptEmp)rodeMap.get("rode");
									rodeList.add(rode);
								}
							}
						}else{
							if(null != riskManager){
								RangObjectDeptEmp newrode = new RangObjectDeptEmp();
								newrode.setId(Identities.uuid());
								newrode.setRiskOrgTemp(dept);
								newrode.setScoreObject(obj);
								if(roleCodes.contains("DeptRiskManager")){
									newrode.setScoreEmp(riskManager);
								}else{
									newrode.setScoreEmp(null);
								}
								rodeList.add(newrode);
								saveRodeList.add(newrode);//要保存综合表
							}
						}
					}
				}
			}
			objForms.add(new RiskScoreObjectForm(scObj,scoreDeptList,rodeList));
		}
		//批量保存综合表
		if(saveRodeList.size()>0){
			this.saveRangObjDeptEmpsSql(saveRodeList);
		}
		return objForms;
	}
	
	/**
	 * 批量保存综合表
	 * @param rodeList	综合实体集合
	 */
	@Transactional
	@RecordLog("批量保存打分对象，部门，人员信息")
    public void saveRangObjDeptEmpsSql(final List<RangObjectDeptEmp> rodeList) {
        this.o_objDeptEmpDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement pst = null;
                String sql = "insert into t_rm_rang_object_dept_emp (id,score_object_id,score_dept_id,score_emp_id) values(?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (RangObjectDeptEmp rode : rodeList) {
                	pst.setString(1, rode.getId());
                    pst.setString(2, rode.getScoreObject().getId());
                    pst.setString(3, rode.getRiskOrgTemp().getId());
                    if(null != rode.getScoreEmp()){
                    	pst.setString(4, rode.getScoreEmp().getId());
                    }else{
                    	pst.setString(4, null);
                    }
                    pst.addBatch();
				}
                
                pst.executeBatch();
            }
        });
    }
	/**
	 * 批量删除综合表
	 * @param rodeIds	综合数据id
	 */
	@Transactional
	@RecordLog("批量删除打分对象，部门，人员综合信息")
	public void removeObjectDeptEmpsByids(String rodeIds){
		if(!"".equals(rodeIds)){
			o_objDeptEmpDAO.createQuery("delete RangObjectDeptEmp where id in (:ids)")
			.setParameterList("ids", StringUtils.split(rodeIds,",")).executeUpdate();
		}
	}
	
	/**
	 * findIsSaveRolesByEmpIds: 根据人员id验证该员工角色是否已在准则中设置
	 * @param empIds	人员id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findIsSaveRolesByEmpIds(String empIds){
		Map<String,Object> map = new HashMap<String, Object>();
		String empNames = "";
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		List<String> userIdList = new ArrayList<String>();
		List<String> saveRoleCodeList = o_weightSetBO.findRoleCodes();
		if (StringUtils.isNotBlank(empIds)) {
			  String[] idArray = empIds.split(",");
			  for(String id : idArray) {
				  SysEmployee emp = o_emoGridBO.findEmpEntryByEmpId(id);
				  if(!empList.contains(emp)){
					  empList.add(emp);
					  if(!userIdList.contains(emp.getUserid())){
						  userIdList.add(emp.getUserid());
					  }
				  }
			  }
		}
		List<Map<String, Object>> userRoelesMapList = this.findUserRolesMapByuserIdList(userIdList);
		for(SysEmployee employee : empList){
			for(Map<String, Object> userRoles : userRoelesMapList){
				if(employee.getId().equals(userRoles.get("userId"))){
					boolean isExit = false;//是否存在
					Set<SysRole> roleSet = (Set<SysRole>)userRoles.get("roles");
					if(roleSet.size()>0){
						for(SysRole role : roleSet){
							if(saveRoleCodeList.contains(role.getRoleCode())){//准则中包含员工角色
								isExit = true;
							}
						}
						if(!isExit){
							empNames = employee.getEmpname() + "," + empNames;
						}
					}else{
						empNames = employee.getEmpname() + "," + empNames;
					}
				}
				
			}
		}
		//验证权重角色重复
		List<Map<String, Object>> userRoles = o_weightSetBO.findUserRolesSameByRoleIds(null,userIdList);
		//List<SysEmployee> deptRiskMan = this.findEmpsByRoleIdAnddeptId(UserContext.getUser().getMajorDeptId(),"DeptRiskManager");//查找部门风险管理员
		map.put("empNames", empNames);
		if(null != userRoles && userRoles.size()>0){
			map.put("userRoles", userRoles);
		}
		/*if(deptRiskMan.size()!=1){
			map.put("DeptRiskManagerCount", deptRiskMan.size() + "");
		}else{
			map.put("DeptRiskManagerCount", "");
		}*/
		return map;
	}
	
	/**
	 * 通过用户id集合查询员工，用户，角色Map
	 * @param userIdList	用户id集合
	 * @return
	 */
	public List<Map<String, Object>> findUserRolesMapByuserIdList(List<String> userIdList){
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
		List<SysUser> userList = o_riskAssessPlanBO.findUsersByuserIdList(userIdList);
		for(SysUser user : userList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", user.getId());
			map.put("roles", user.getSysRoles());
			mapList.add(map);
		}
		return mapList;
	}
	
	/**
	 * 查询deptId部门下角色rodeCode数量
	 * @param deptId	部门id
	 * @param rodeCode	角色编号
	 * @return
	 */
	public List<SysEmployee> findEmpsByRoleIdAnddeptId(String deptId,String rodeCode){
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		Set<SysUser> userSet = o_riskScoreBO.fingUsersByRoleCode(rodeCode);//部门风险管理员DeptRiskManager    (公司风险管理员RiskManagemer)
		List<String> userIdList = new ArrayList<String>();
		for(SysUser user : userSet){
			userIdList.add(user.getId());
		}
		//根据用户id集合查询员工集合
		List<SysEmployee> emps = o_riskScoreBO.findEmpEntryByUserIdList(userIdList);
		List<String> empIdList = new ArrayList<String>();
		for(SysEmployee emp : emps){
			empIdList.add(emp.getId());
		}
		List<SysEmpOrg> empDepts = o_riskScoreBO.findEmpDeptsByEmpIdList(empIdList);
		for(SysEmpOrg empOrg : empDepts){
			if(deptId.equals(empOrg.getSysOrganization().getId())){
				empList.add(empOrg.getSysEmployee());
			}
		}
		return empList;
	}
	/**
	 * 根据计划id和部门id得到综合表实体，key是parentRiskId
	 * add by 王再冉
	 * 2014-3-6  上午11:50:30
	 * desc : 用于按风险分配列表
	 * @param planId	计划id
	 * @param orgId		部门id
	 * @return 
	 * Map<String,ArrayList<RangObjectDeptEmp>>
	 */
	public Map<String, ArrayList<RangObjectDeptEmp>> getRangObjectDeptEmpsIdByPlanIdAndOrgID(
																		String planId,String orgId) {
		StringBuffer sql = new StringBuffer();
		Map<String, ArrayList<RangObjectDeptEmp>> map = new HashMap<String, ArrayList<RangObjectDeptEmp>>();
		ArrayList<RangObjectDeptEmp> arrayList = null;
		sql.append(" SELECT rode.ID,rode.SCORE_OBJECT_ID,rode.SCORE_DEPT_ID,r.parent_id ");
		sql.append(" FROM t_rm_rang_object_dept_emp rode ");
		sql.append(" LEFT JOIN t_rm_risk_org_temp sd ON sd.id = rode.SCORE_DEPT_ID ");
		sql.append(" LEFT JOIN t_rm_risk_score_object ob ON ob.id = rode.SCORE_OBJECT_ID ");
		sql.append(" LEFT JOIN t_rm_risks r ON r.id = ob.risk_id ");
		sql.append(" WHERE ob.assess_plan_id = :planId ");
		sql.append(" AND sd.org_id = :orgId ");
		SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        sqlQuery.setParameter("orgId", orgId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String rodeId = "";			//综合id
			String objectId = "";		//打分对象id
			String scoreDeptId = "";	//打分部门id
			String parentRiskId = "";	//上级风险id
            if(null != o[0]){
            	rodeId = o[0].toString();
            }if(null != o[1]){
            	objectId = o[1].toString();
            }if(null != o[2]){
            	scoreDeptId = o[2].toString();
            }if(null != o[3]){
            	parentRiskId = o[3].toString();
            }
            RangObjectDeptEmp rode = new RangObjectDeptEmp();
            rode.setId(rodeId);
            rode.setScoreObject(new RiskScoreObject(objectId));
            rode.setRiskOrgTemp(new RiskOrgTemp(scoreDeptId));
            
            if(null != map.get(parentRiskId)){
            	map.get(parentRiskId).add(rode);
            }else{
            	arrayList = new ArrayList<RangObjectDeptEmp>();
            	arrayList.add(rode);
            	map.put(parentRiskId, arrayList);
            }
		}
        return map;
	}
	/**
	 * 根据风险id和计划id查询综合实体数据
	 * add by 王再冉
	 * 2014-3-6  下午3:02:43
	 * desc : 
	 * @param planId	计划id
	 * @param riskId	风险id
	 * @return 
	 * ArrayList<RangObjectDeptEmp>	综合实体集合
	 */
	public ArrayList<RangObjectDeptEmp> getRangObjectDeptEmpsIdByPlanIdAndRiskID(
																	String planId,String riskId) {
		StringBuffer sql = new StringBuffer();
		ArrayList<RangObjectDeptEmp> arrayList = new ArrayList<RangObjectDeptEmp>();
		sql.append(" SELECT rode.id,rode.SCORE_OBJECT_ID,rode.SCORE_DEPT_ID ");
		sql.append(" FROM t_rm_rang_object_dept_emp rode ");
		sql.append(" LEFT JOIN t_rm_risk_score_dept sd ON sd.id = rode.SCORE_DEPT_ID ");
		sql.append(" LEFT JOIN t_rm_risk_score_object ob ON ob.id = rode.SCORE_OBJECT_ID ");
		sql.append(" WHERE ob.assess_plan_id = :planId ");
		sql.append(" AND ob.risk_id = :riskId ");
		SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("planId", planId);
		sqlQuery.setParameter("riskId", riskId);
		@SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String rodeId = "";			//综合id
			String objectId = "";		//打分对象id
			String scoreDeptId = "";	//打分部门id
			if(null != o[0]){
				rodeId = o[0].toString();
			}if(null != o[1]){
				objectId = o[1].toString();
			}if(null != o[2]){
				scoreDeptId = o[2].toString();
			}
			RangObjectDeptEmp rode = new RangObjectDeptEmp();
			rode.setId(rodeId);
			rode.setScoreObject(new RiskScoreObject(objectId));
			rode.setRiskOrgTemp(new RiskOrgTemp(scoreDeptId));
			arrayList.add(rode);
		}
		return arrayList;
	}
	
}
