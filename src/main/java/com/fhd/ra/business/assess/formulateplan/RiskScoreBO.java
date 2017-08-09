package com.fhd.ra.business.assess.formulateplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.formulatePlan.RiskCircuseeDAO;
import com.fhd.dao.assess.formulatePlan.RiskOrgTempDAO;
import com.fhd.dao.assess.formulatePlan.RiskScoreDeptDAO;
import com.fhd.dao.assess.formulatePlan.RiskScoreObjectDAO;
import com.fhd.dao.assess.quaAssess.EditIdeaDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.dao.sys.autho.SysoRoleDAO;
import com.fhd.dao.sys.autho.SysoRoleUsersDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.organization.SysOrgEmpDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.dao.sys.orgstructure.SysOrganizationDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlanTakerObject;
import com.fhd.entity.assess.formulatePlan.RiskCircusee;
import com.fhd.entity.assess.formulatePlan.RiskOrgRelationTemp;
import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.assess.quaAssess.DeptLeadCircusee;
import com.fhd.entity.assess.quaAssess.EditIdea;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.email.AssessEmailBO;
import com.fhd.ra.business.assess.email.TaskDistributionEmailBO;
import com.fhd.ra.business.assess.kpiset.AssessTaskBO;
import com.fhd.ra.business.assess.oper.CircuseeBO;
import com.fhd.ra.business.assess.oper.DeptLeadCircuseeBO;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.oper.RangObjectDeptEmpBO;
import com.fhd.ra.business.assess.oper.ScoreDeptBO;
import com.fhd.ra.business.assess.oper.ScoreObjectBO;
import com.fhd.ra.business.assess.quaassess.SaveAssessBO;
import com.fhd.ra.business.risk.RiskBO;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.business.risk.RiskOrgBO;
import com.fhd.ra.business.riskidentify.RiskIdentifyBO;
import com.fhd.ra.interfaces.assess.formulateplan.IRiskScoreBO;
import com.fhd.sys.business.assess.SendEmailBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 打分对象，打分部门BO类
 * @author 王再冉
 *
 */
@Service
public class RiskScoreBO implements IRiskScoreBO{
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	@Autowired
	private RiskCircuseeDAO o_riskcircuseeDAO;
	@Autowired
	private RiskScoreObjectDAO o_riskScoreObjDAO;
	@Autowired
	private RiskScoreDeptDAO o_riskScoreDeptDAO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private SysOrgEmpDAO o_sysOrgEmpDAO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private JBPMOperate o_jbpmOperate;
	@Autowired
	private RiskBO o_riskBO;
	@Autowired
	private EmpGridBO o_empGridBO;
	@Autowired
	private AssessTaskBO o_assessTaskBO;
	@Autowired
	private SysoRoleDAO o_sysoRoleDAO;
	@Autowired
	private ScoreObjectBO o_scoreObjectBO;
	@Autowired
	private ScoreDeptBO o_scoreDeptBO;
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	@Autowired 
	private CircuseeBO o_circuseeBO;
	@Autowired
	private RiskAssessPlanBO o_planBO;
	@Autowired
	private RangObjectDeptEmpBO o_rangeObjDeptEmpBO;
	@Autowired
	private RiskCmpBO o_riskCmpBO;
	@Autowired
	private SaveAssessBO o_saveAssessBO;
	@Autowired
	private EditIdeaDAO o_editIdeaDAO;
	@Autowired
	private SendEmailBO o_sendEmailBO;
	@Autowired
	private TaskDistributionEmailBO o_taskDistributionEmailBO;
	@Autowired
	private AssessEmailBO o_assessEmailBO;
	@Autowired
	private SysoRoleUsersDAO o_sysRoleUserDAO;
	@Autowired
	private SysOrganizationDAO o_sysOrganizationDAO;
	
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	
	@Autowired
	private OrganizationBO o_organizationBO;
	
	@Autowired
	private RiskOrgBO o_riskOrgBO;
	
	@Autowired
	private DeptLeadCircuseeBO o_deptLeadCircuseeBO;
	
	@Autowired
	private RiskIdentifyBO o_riskIdentifyBO;
	
	@Autowired
	private RiskOrgTempDAO riskOrgTempDAO;
	
	@Autowired
	private RiskOrgTempBO riskOrgTempBO;
	
	
	/**
	 * 根据辨识计划id获取打分对象列表
	 * @author Jzq
	 * @param planId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreObject> getRiskScoreObjectListByPlanId(String planId){
		Criteria criteria = this.o_riskScoreObjDAO.createCriteria();
		criteria.add(Restrictions.eq("assessPlan.id", planId));
		return criteria.list();
	}
	
	
	/**
	 * 根据辨识计划id获取打分部门新增的t_rm_risk_org_temp列表
	 * @param planId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskOrgTemp> getRiskOrgTempListByPlanId(String planId){
		Criteria criteria = this.riskOrgTempDAO.createCriteria();
		criteria.add(Restrictions.eq("planId", planId));
		return criteria.list();
	}
	
	
	
	
	
	
	
	/**根据公司id获取风险打分对象列表
	 * @param companyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskScoreObject> getRiskScoreObjectListByCompanyId(String companyId){
		Criteria criteria = this.o_riskScoreObjDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		return criteria.list();
	}
	@SuppressWarnings("unchecked")
	public List<RiskScoreObject> getRiskScoreObjectById(String riskId){
		Criteria criteria = this.o_riskScoreObjDAO.createCriteria();
		criteria.add(Restrictions.eq("riskId", riskId))
		.add(Restrictions.isNotNull("name"));
		return  criteria.list();
	}
	
	
	
	@SuppressWarnings({ "unchecked" })
	public List<SysEmployee> findEmpDeptBydeptIdList(List<String> deptIds) {
		Criteria c = o_sysOrgEmpDAO.createCriteria();
		List<SysEmpOrg> list = null;
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		if (deptIds.size()>0) {
			c.add(Restrictions.in("sysOrganization.id", deptIds));
		} 
		list = c.list();
		for(SysEmpOrg eo : list){
			if("1".equals(eo.getSysEmployee().getDeleteStatus())){//员工状态为已启用
				empList.add(eo.getSysEmployee());
			}
		}
		return empList;
	}

	
	/**
	 * 查询部门id下所有部门风险实体
	 * @param orgIds 部门ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskOrg> findRiskOrgsById(String orgIds) {
		Criteria c = o_riskOrgDAO.createCriteria();
		List<RiskOrg> list = null;
		List<String> idList = new ArrayList<String>();
		if (StringUtils.isNotBlank(orgIds)) {
			  String[] idArray = orgIds.split(",");
			  for (String id : idArray) {
					  idList.add(id);
			  }
			  c.add(Restrictions.in("sysOrganization.id", idList));
		}
		list = c.list();
		return list;
	}
	/**
	 * 保存打分对象
	 * @param scoreObj
	 */
	@Transactional
	public void saveRiskScoreObject(RiskScoreObject scoreObj) {
		o_riskScoreObjDAO.merge(scoreObj);
	}
	/**
	 * 保存打分部门
	 * @param scoreDept
	 */
	@Transactional
	public void saveRiskScoreDept(RiskScoreDept scoreDept) {
		o_riskScoreDeptDAO.merge(scoreDept);
	}
	/**
	 * 删除打分部门
	 * @param depts
	 */
	@Transactional
	public void removeRiskScoreDeptsByIds(List<String> deptIds) {
		for(String deptId : deptIds){
			o_riskScoreDeptDAO.delete(deptId);
		}
	}
	
	/**
	 * 删除打分对象
	 * @param obj
	 */
	@Transactional
	public void removeRiskScoreObj(RiskScoreObject obj) {
		o_riskScoreObjDAO.delete(obj);
	}
	/**
	 * 删除打分对象By id
	 */
	@Transactional
	public void removeRiskScoreObjById(String objId) {
		o_riskScoreObjDAO.delete(objId);
	}

	/**
	 * 查找部门风险管理员
	 * @param deptId	部门id
	 * @param rodeCode	角色编号
	 */
	public SysEmployee findEmpsByRoleIdAnddeptId(String deptId,String rodeCode){
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		Set<SysUser> userSet = this.fingUsersByRoleCode(rodeCode);//部门风险管理员DeptRiskManager    (公司风险管理员RiskManagemer)
		List<String> userIdList = new ArrayList<String>();
		for(SysUser user : userSet){
			userIdList.add(user.getId());
		}
		//根据用户id集合查询员工集合
		List<SysEmployee> emps = this.findEmpEntryByUserIdList(userIdList);
		List<String> empIdList = new ArrayList<String>();
		for(SysEmployee emp : emps){
			empIdList.add(emp.getId());
		}
		List<SysEmpOrg> empDepts = this.findEmpDeptsByEmpIdList(empIdList);
		for(SysEmpOrg empOrg : empDepts){
			if(deptId.equals(empOrg.getSysOrganization().getId())){
				empList.add(empOrg.getSysEmployee());
			}
		}
		if(empList.size()>0){
			return empList.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 登录人公司的风险管理员(返回人员实体)
	 */
	public SysEmployee findRiskManagerByCompanyId(){
		String companyId = UserContext.getUser().getCompanyid();
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		Set<SysUser> userSet = this.fingUsersByRoleCode("RiskManagemer");//风险管理员
		for(SysUser user : userSet){
			List<SysEmployee> emps = findEmpEntryByUserId(user.getId());
			if(emps.size()>0){
				for(SysEmployee emp : emps){
					if(companyId.equals(emp.getSysOrganization().getId())){
						empList.add(emp);
					}
				}
			}
		}
		if(empList.size()>0){
			return empList.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 查询当前登录人公司的公司风险管理员id
	 * add by 王再冉
	 * 2014-3-3  下午3:39:16
	 * desc : 
	 * @param companyId	当前登录人公司id
	 * @return 
	 * String	返回管理员id
	 */
	public String findRiskManagerIdByCompanyId(String companyId) {
		StringBuffer sql = new StringBuffer();
		String riskManagerStr = "";
		sql.append(" SELECT e.id,e.org_id FROM t_sys_user_role b ");
		sql.append(" LEFT JOIN t_sys_employee e ON e.user_id = b.user_id ");
		sql.append(" WHERE role_id = ( ");
		sql.append(" SELECT ID FROM t_sys_role a WHERE a.ROLE_CODE = 'RiskManagemer' ) ");
        SQLQuery sqlQuery = o_sysRoleUserDAO.createSQLQuery(sql.toString());
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String empId = "";//人员ID
            String orgId = "";
            if(null != objects[0]){
            	empId = objects[0].toString();
            }if(null != objects[1]){
            	orgId = objects[1].toString();
            }
            if(orgId.equals(companyId)){
            	riskManagerStr = empId;
            }
        }
        return riskManagerStr;
	}
	
	
	/**
	 * 根据用户id查员工
	 * @param userId	用户id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysEmployee> findEmpEntryByUserId(String userId) {
		Criteria c = o_sysEmployeeDAO.createCriteria();
		List<SysEmployee> list = null;
		
		if (StringUtils.isNotBlank(userId)) {
			c.add(Restrictions.eq("sysUser.id", userId));
		} else {
			return null;
		}
		
		list = c.list();
		return list;
	}
	/**
	 * 根据用户id集合 查员工
	 * @param userIds	用户id集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysEmployee> findEmpEntryByUserIdList(List<String> userIds) {
		Criteria c = o_sysEmployeeDAO.createCriteria();
		List<SysEmployee> list = new ArrayList<SysEmployee>();
		if(userIds.size()>0){
			c.add(Restrictions.in("sysUser.id", userIds));
			list = c.list();
		}
		return list;
	}
	/**
	 * 根据员工id集合查员工部门实体集合
	 * @param empIdList  员工id集合
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<SysEmpOrg> findEmpDeptsByEmpIdList(List<String> empIdList) {
		Criteria c = o_sysOrgEmpDAO.createCriteria();
		List<SysEmpOrg> list = new ArrayList<SysEmpOrg>();
		if(empIdList.size()>0){
			c.add(Restrictions.in("sysEmployee.id", empIdList));
			list = c.list();
		}
		
		return list;
	}
	/**
	 * 根据员工id 查员工部门实体集合
	 * @param empId 员工id
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<SysEmpOrg> findEmpDeptsByEmpId(String empId) {
		Criteria c = o_sysOrgEmpDAO.createCriteria();
		List<SysEmpOrg> list = null;
		if(StringUtils.isNotBlank(empId)){
			c.add(Restrictions.eq("sysEmployee.id", empId));
		}
		list = c.list();
		return list;
	}
	/**
	 * 通过部门id查询所有员工
	 * @param deptId
	 * @return
	 */
	public List<SysEmployee> findEmpsBydeptId(String deptId){
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		if(null != deptId){
			List<SysEmpOrg> empOrgs = findEmpDeptBydeptId(deptId);
			for(SysEmpOrg eo : empOrgs){
				if("1".equals(eo.getSysEmployee().getDeleteStatus())&&"1".equals(eo.getSysEmployee().getEmpStatus())){//员工状态为已启用
					empList.add(eo.getSysEmployee());
				}
			}
		}
		return empList;
	}
	
	/**
	 * 得到所有此部门下所有部门ID
	 * */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<String> findChildDeptById(String id) {
		Criteria criteria = o_sysOrganizationDAO.createCriteria();
	    criteria.add(Restrictions.eq("parentOrg.id", id));
	    List<SysOrganization> list = criteria.list();
	    if(null != list && list.size() > 0) {
	    	List<String> childDeptList = new ArrayList<String>();
	    	for(SysOrganization childDept: list) {
	    		childDeptList.add(childDept.getId());
	    	}
	    	return childDeptList;
	    }
	    return null;
	}
	
	/**
	 * 通过部门id查询部门以及下属所有子部门的员工(递归调用)
	 * @param deptId 部门id
	 * @return
	 */
	public List<SysEmployee> findAllEmpsBydeptId(String deptId){
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		if(null != deptId){
			List<String> childDepts = this.findChildDeptById(deptId);
			List<SysEmpOrg> empOrgs = findEmpDeptBydeptId(deptId);
			for(SysEmpOrg eo : empOrgs){
				if( "1".equals(eo.getSysEmployee().getDeleteStatus()) && "1".equals(eo.getSysEmployee().getEmpStatus())){//员工状态为已启用
					empList.add(eo.getSysEmployee());
				}
			}
			if(null != childDepts && childDepts.size() > 0) {
				for(String childDeptId: childDepts) {
					empList.addAll(findAllEmpsBydeptId(childDeptId));
				}
			}
		}
		return empList;
	}

	/**
	 * 通过部门id查询部门所有下级部门的id(递归调用)
	 * @param deptId 部门id
	 * @return
	 */
	public List<String> findAllDeptIdsBydeptId(String deptId){
		List<String> deptIdList = new ArrayList<String>();
		
		if(null != deptId){
			List<String> childDepts = this.findChildDeptById(deptId);
			if(null != childDepts){
				deptIdList.addAll(childDepts);
			}
			if(null != childDepts && childDepts.size() > 0) {
				for(String childDeptId: childDepts) {
					deptIdList.addAll(findAllDeptIdsBydeptId(childDeptId));	
				}
			}
		}
		return deptIdList;
	}
	
	/**
	 * 查询全部部门关联人员集合
	 * */
	@SuppressWarnings("unchecked")
	public List<SysEmployee> findSysEmployeeAllList(List<String> orgIdList){
		Criteria c = o_sysOrgEmpDAO.createCriteria();
		c.add(Restrictions.in("sysOrganization.id", orgIdList));
		List<SysEmpOrg> list = null;
		list = c.list();
		List<SysEmployee> lists = new ArrayList<SysEmployee>();
		for (SysEmpOrg sysEmpOrg : list) {
			lists.add(sysEmpOrg.getSysEmployee());
		}
		return lists;
	}
	
	/**
	 * 查询所有机构
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> findAllOrganizationsMap() {
		HashMap<String, ArrayList<SysOrganization>> mapList = new HashMap<String, ArrayList<SysOrganization>>();
		HashMap<String, SysOrganization> map = new HashMap<String, SysOrganization>();
		HashMap<String, Object> maps = new HashMap<String, Object>();
		ArrayList<SysOrganization> orgList = null;
		Criteria c = o_sysOrgDAO.createCriteria();
		c.add(Restrictions.eq("deleteStatus", "1"));
		List<SysOrganization> list = null;
		list = c.list();
		for (SysOrganization sysOrganization : list) {
			if(null != sysOrganization.getParentOrg()){
				map.put(sysOrganization.getId(), sysOrganization);
				if(null != mapList.get(sysOrganization.getParentOrg().getId())){
					mapList.get(sysOrganization.getParentOrg().getId()).add(sysOrganization);
				}else{
					orgList = new ArrayList<SysOrganization>();
					orgList.add(sysOrganization);
					mapList.put(sysOrganization.getParentOrg().getId(), orgList);
				}
			}
		}
		
		maps.put("map", map);
		maps.put("mapList", mapList);
		return maps;
	}
	
	/**
	 * 查询全部部门关联人员集合
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> findSysEmployeeAllMap(){
		HashMap<String, Object> mapObj = new HashMap<String, Object>();
		HashMap<String, SysEmployee> map = new HashMap<String, SysEmployee>();
		HashMap<String, ArrayList<SysEmployee>> mapList = new HashMap<String, ArrayList<SysEmployee>>();
		Criteria c = o_sysOrgEmpDAO.createCriteria();
		List<SysEmpOrg> list = null;
		list = c.list();
		for (SysEmpOrg sysEmpOrg : list) {
			if(null != sysEmpOrg.getSysOrganization()){
				map.put(sysEmpOrg.getSysOrganization().getId(), sysEmpOrg.getSysEmployee());
				
				if(null != mapList.get(sysEmpOrg.getSysOrganization().getId())){
					mapList.get(sysEmpOrg.getSysOrganization().getId()).add(sysEmpOrg.getSysEmployee());
				}else{
					ArrayList<SysEmployee> lists = new ArrayList<SysEmployee>();
					lists.add(sysEmpOrg.getSysEmployee());
					mapList.put(sysEmpOrg.getSysOrganization().getId(), lists);
				}
				
			}
		}
		
		mapObj.put("map", map);
		mapObj.put("mapList", mapList);
		
		return mapObj;
	}
	
	/**
	 * 通过部门id查询所有员工
	 * @param deptId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysEmployee> findEmpsAllBydeptId(String deptId){
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		HashMap<String, Object> mapObj = this.findSysEmployeeAllMap();
		HashMap<String, ArrayList<SysEmployee>> empMapList = (HashMap<String, ArrayList<SysEmployee>>) mapObj.get("mapList");
		HashMap<String, SysEmployee> orgEmpMapAll = (HashMap<String, SysEmployee>) mapObj.get("map");
		HashMap<String, Object> objeMap = this.findAllOrganizationsMap();
//		HashMap<String, SysOrganization> map = (HashMap<String, SysOrganization>) objeMap.get("map");
		HashMap<String, ArrayList<SysOrganization>> orgMapAll = (HashMap<String, ArrayList<SysOrganization>>) objeMap.get("mapList");
		String tempDeptId = "";
		
		if(null != empMapList.get(deptId)){
			for (SysEmployee sysEmployee : empMapList.get(deptId)) {
				empList.add(sysEmployee);
			}
		}
		
		while(true){
			if(null != orgMapAll.get(deptId)){
				ArrayList<SysOrganization> list = orgMapAll.get(deptId);
				for (SysOrganization sysOrganization : list) {
					empList.add(orgEmpMapAll.get(sysOrganization.getId()));
					
					
					while(true){
						tempDeptId = sysOrganization.getId();
						if(null != orgMapAll.get(tempDeptId)){
							ArrayList<SysOrganization> list2 = orgMapAll.get(tempDeptId);
							for (SysOrganization sysOrganization2 : list2) {
								empList.add(orgEmpMapAll.get(sysOrganization2.getId()));
							}
						}
						break;
					}
					
				}
				break;
			}
		}
		return empList;
	}
	
	
	/**
	 * 根据部门id查找部门员工集合
	 * @param deptId
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<SysEmpOrg> findEmpDeptBydeptId(String deptId) {
		Criteria c = o_sysOrgEmpDAO.createCriteria();
		List<SysEmpOrg> list = null;
		c.createAlias("sysEmployee", "emp");
		if (StringUtils.isNotBlank(deptId)) {
			c.add(Restrictions.eq("sysOrganization.id", deptId));
		}
		c.add(Restrictions.eq("emp.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		list = c.list();
		return list;
	}
	
	/**
	 * 工作流第一步--评估计划制定，提交
	 * @param empIds	承办人id
	 * @param approver	审批人
	 * @param businessId	评估计划id
	 */
	@SuppressWarnings("deprecation")
	@Transactional
	@RecordLog("提交计划")
	public void submitAssessRiskPlanToApprover(String empIds, String approverId, String businessId, 
										String executionId, String entityType,String deptEmpId){
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(businessId);
		String makePlanEmpId = UserContext.getUser().getEmpid();//制定计划人员id
		String approver = "";//审批人
		JSONArray jsonArray = JSONArray.fromObject(approverId);
		if (jsonArray.size() > 0){
			 JSONObject jsobj = jsonArray.getJSONObject(0);
			 approver = jsobj.getString("id");//审批人
		}
		String riskManagerId = this.findRiskManagerIdByCompanyId(UserContext.getUser().getCompanyid());//公司风险管理员id
		if(StringUtils.isBlank(riskManagerId)){//如果公司管理员为空，则选用计划制定人
			riskManagerId = makePlanEmpId;
		}
		if(null != assessPlan){
			assessPlan.setStatus(Contents.STATUS_SUBMITTED);//计划状态
			assessPlan.setDealStatus(Contents.DEAL_STATUS_HANDLING);//计划处理状态
			if (!"".equals(approver)) {
				if(StringUtils.isBlank(executionId)){
					//菜单触发提交开启工作流
					Map<String, Object> variables = new HashMap<String, Object>();
					if(!StringUtils.isNotBlank(entityType)){
						entityType = "riskAssessTotal";//流程名称
					}
					variables.put("entityType", entityType);
					variables.put("AssessPlanApproverEmpId", approver);//审批人
					variables.put("id", businessId);
					variables.put("name", assessPlan.getPlanName());//评估计划名称
					variables.put("empIds", empIds);
					variables.put("companyId", UserContext.getUser().getCompanyid());
					variables.put("riskManagemer", riskManagerId);//该公司的风险管理员
					variables.put("makePlanEmpId", makePlanEmpId);//计划制定人
					//启动流程时将schm作为流程变量保存在流程实例中 2017年4月14日14:34:08 吉志强添加
					variables.put("schm", assessPlan.getSchm());//计划制定人
					
					
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
							
							if("complex".equalsIgnoreCase(jbpmType)){//判断工作流是否为多节点
								o_assessEmailBO.sendAssessEmail(businessId, approver, taskId.toString(), "FHD.view.risk.assess.planManagerApprove.PlanManagerApproveMain", "评估计划主管审批");
							}else if("simple".equalsIgnoreCase(jbpmType)){
								o_assessEmailBO.sendAssessEmail(businessId, approver, taskId.toString(), "FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitMain", "评估计划审批");
							}
						}
					}
				}
			}
			//保存审批人承办人
			this.saveRiskCircuseeBysome(deptEmpId, approver, assessPlan, businessId);
		}
	}
	
	/**
	 * 一在发起流程时需要人员：
	 * 1公司保密员
	 * 2保密部部门领导
	 * 二在任务分配时：
	 * 1需要辨识人拥有普通员工或者部门领导角色
	 * 
	*/
	/**
	 * 保密工作流第一步--评估计划制定，提交
	 * @param empIds	承办人id
	 * @param approver	审批人
	 * @param businessId	评估计划id
	 * change by 郭鹏
	 * @return 
	 */
	@SuppressWarnings("deprecation")
	@Transactional
	@RecordLog("提交计划")
	public boolean submitAssessRiskPlanToApproverSecrecy(String empIds, String approverId, String businessId, 
										String executionId, String entityType,String deptEmpId){
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(businessId);
		String makePlanEmpId = UserContext.getUser().getEmpid();//制定计划人员id
		String approver = "";//审批人
		JSONArray jsonArray = JSONArray.fromObject(approverId);
		if (jsonArray.size() > 0){
			 JSONObject jsobj = jsonArray.getJSONObject(0);
			 approver = jsobj.getString("id");//审批人
		}
		SysEmployee emp =this.findEmpsByRoleIdAnddeptId(UserContext.getUser().getMajorDeptId(),"secrecyManager");//查询保密部风险管理员
		SysEmployee secrecyLeader =this.findEmpsByRoleIdAnddeptId(UserContext.getUser().getMajorDeptId(),"DeptLeader");//查询保密部部门领导
		//UNDO 当保密部下面没有部门风险管理员角色时，我们要提示需要配置该角色才能开启流程
		if (emp==null||secrecyLeader==null) {
			return false;
		}else{
		String riskManagerId = emp.getId();//公司风险管理员id
		String secrecyLederId=secrecyLeader.getId(); //保密部部门领导ID
		if(StringUtils.isBlank(riskManagerId)){//如果公司管理员为空，则选用计划制定人
			riskManagerId = makePlanEmpId;
		}
		if(StringUtils.isBlank(riskManagerId)){//如果公司主管领导为空，则选用计划制定人
			secrecyLederId = makePlanEmpId;
		}
		if(null != assessPlan){
			assessPlan.setStatus(Contents.STATUS_SUBMITTED);//计划状态
			assessPlan.setDealStatus(Contents.DEAL_STATUS_HANDLING);//计划处理状态
			if (!"".equals(approver)) {
				if(StringUtils.isBlank(executionId)){
					//菜单触发提交开启工作流
					Map<String, Object> variables = new HashMap<String, Object>();
					if(!StringUtils.isNotBlank(entityType)){
						entityType = "riskAssessAndIdentify";//流程名称
					}
					variables.put("entityType", entityType);
					variables.put("AssessPlanApproverEmpId", approver);//审批人
					variables.put("approveFore", secrecyLederId);//保密部门主管领导
					variables.put("id", businessId);
					variables.put("name", assessPlan.getPlanName());//评估计划名称
					variables.put("empIds", empIds);
					variables.put("companyId", UserContext.getUser().getCompanyid());
					variables.put("riskManagemer", riskManagerId);//该公司的风险管理员
					variables.put("makePlanEmpId", makePlanEmpId);//计划制定人
					//启动流程时将schm作为流程变量保存在流程实例中 2017年4月14日14:34:08 吉志强添加
					variables.put("schm", assessPlan.getSchm());//计划制定人
					
					
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
							
							if("complex".equalsIgnoreCase(jbpmType)){//判断工作流是否为多节点
								o_assessEmailBO.sendAssessEmail(businessId, approver, taskId.toString(), "FHD.view.risk.assess.planManagerApprove.PlanManagerApproveMain", "评估计划主管审批");
							}else if("simple".equalsIgnoreCase(jbpmType)){
								o_assessEmailBO.sendAssessEmail(businessId, approver, taskId.toString(), "FHD.view.risk.assess.formulatePlan.FormulateApproverSubmitMain", "评估计划审批");
							}
						}
					}
				}
			}
			//保存审批人承办人
			this.saveRiskCircuseeBysome(deptEmpId, approver, assessPlan, businessId);
		}
		return true;
		}
	}
	
	/**
	 * 工作流第二步--评估计划审批
	 * @param executionId	流程id
	 * @param businessId	计划id
	 * @param isPass		是否通过
	 * @param examineApproveIdea	审批意见
	 */
	@Transactional
	@RecordLog("审批计划")
	public void mergeRiskAssessPlanApproval(String executionId, String businessId, String isPass, String examineApproveIdea){
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(businessId);//计划实体
		HashMap<String, Long> empIdTaskIdMap = new HashMap<String, Long>();//员工id对应工作流节点id的map
		if(null != assessPlan){
			if ("no".equals(isPass)) {//审批未通过
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
			}else{// 审批通过，工作流提交
				String takers = o_jbpmOperate.getVariable(executionId,"empIds");
				List<RiskAssessPlanTakerObject> assessPlanTakerList = new ArrayList<RiskAssessPlanTakerObject>();
				if (StringUtils.isNotBlank(takers)) {
					String[] idArray = takers.split(",");
					for (String id : idArray) {
						RiskAssessPlanTakerObject assessPlanTaker = new RiskAssessPlanTakerObject();
						assessPlanTaker.setPlanTakerEmpId(id);
						assessPlanTakerList.add(assessPlanTaker);
					}
				}

				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("path", isPass);
				variables.put("assessPlanTakers", assessPlanTakerList);//承办人
				variables.put("assessPlanTakersJoinCount", assessPlanTakerList.size());
				variables.put("examineApproveIdea", examineApproveIdea);
				o_jbpmBO.doProcessInstance(executionId, variables);
				//判断该节点是否发送邮件
				if(o_sendEmailBO.findIsSendValue("send_email_assess_riskplan_all")){
					JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
					List<JbpmHistActinst> jbpmHistActinstList = o_planBO.findJbpmHistActinstsBySome(jbpmhp.getId(),0L);
					for(JbpmHistActinst jbpmHistActinst:jbpmHistActinstList){
						VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
						SysEmployee sysEmployee = jbpmHistTask.getAssignee();
						if(jbpmHistTask!=null && "评估任务分配".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
							Long taskId = jbpmHistTask.getId();
							empIdTaskIdMap.put(sysEmployee.getId(), taskId);
						}
					}
					o_taskDistributionEmailBO.sendTaskDistributionEmail(businessId, empIdTaskIdMap);
				}
			}
		}
	}
	/**
	 * 工作流第三步--任务分配
	 * @param executionId	工作流id
	 * @param businessId	计划id
	 * @param pingguEmpIds	评估人id
	 */
	@Transactional
	@RecordLog("进行计划的任务分配")
	public void mergeRiskTaskDistribute(String executionId, String businessId, String pingguEmpIds){
		Map<String, Object> variables = new HashMap<String, Object>();
		 
		o_jbpmBO.doProcessInstance(executionId, variables);
		o_sendEmailBO.isSendEmailByEmpIds(businessId,pingguEmpIds,"send_email_assess_riskplan_all");//如果设置为“是”，给人员列表中的人员发送email
	}
	
	/**
	 * 批量删除RiskCircusee集合
	 * @param cirList	RiskCircusee实体集合
	 */
	@Transactional
	public void removeRiskCircusees(List<RiskCircusee> cirList) {
		String circuseeIds = "";
		for(RiskCircusee cir : cirList){
			circuseeIds = cir.getId() + "," + circuseeIds;
		}
		if(!"".equals(circuseeIds)){
			o_riskScoreObjDAO.createQuery("delete RiskCircusee where id in (:circuseeIds)")
			.setParameterList("circuseeIds", StringUtils.split(circuseeIds,",")).executeUpdate();
		}
	}
	
	/**
	 * 保存riskCircusee
	 * @param cir riskCircusee实体
	 */
	@Transactional
	public void saveRisCircusee(RiskCircusee cir) {
		o_riskcircuseeDAO.merge(cir);
	}
	
	/**
	 * 查询每个部门的评估风险总数
	 * @param deptId	部门id
	 * @param planId	评估计划id
	 * @return
	 */
	public int findRiskCountsByPlanIdAndDeptID(String deptId, String planId) {
		List<RiskScoreObject> list = new ArrayList<RiskScoreObject>();
		List<RiskScoreObject> scObjList = o_scoreObjectBO.findRiskScoreObjByplanId(planId);
		List<String> objIdList = new ArrayList<String>();
		for(RiskScoreObject obj : scObjList){
			objIdList.add(obj.getId());
		}
		List<Map<String,Object>> deptMapList = o_scoreDeptBO.findRiskDeptByObjIdList(objIdList);
		for(Map<String,Object> deptMap : deptMapList){
			RiskOrgTemp scoreDept = (RiskOrgTemp)deptMap.get("scoreDept");
			if(null != deptMap.get(deptId) && !list.contains(scoreDept.getRiskScoreObject())
					&& null != scoreDept.getRiskScoreObject()){//该打分部门中含有该部门的id//风险辨识有的部门没有关联风险
				list.add(scoreDept.getRiskScoreObject());//将打分部门放到集合中
			}
		}
		return list.size();
	}
	
	/**
	 * 为计划新增打分对象
	 * @param planId 评估计划ID
	 * @param riskId 风险ID
	 * @param empId 人员ID
	 * @param templateId 模板ID
	 * @return  VOID
	 * @author 金鹏祥
	 */
	public void addRiskAndsaveAllBySome(String planId, String riskId, String empId, String templateId) {
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
		Risk risk = o_riskBO.findRiskById(riskId);
		SysOrganization dept = null;
		//打分对象是否存在
		RiskScoreObject exitObj = o_scoreObjectBO.findRiskScoreObjsByPlanAndRisk(planId, riskId);
		if(null == exitObj){//不存在相同的打分对象
			RiskScoreObject nullRiskObj = o_riskIdentifyBO.findnullRiskObjByPlanId(planId);
			//辨识计划可能含有没有风险的打分对象，如果存在，更新打分对象；不存在则新建
			if(null != nullRiskObj){
				List<RiskScoreDept> findScoreDeptList = o_scoreDeptBO.findRiskDeptByObjId(nullRiskObj.getId());
				if(UserContext.getUser().getMajorDeptId().equals(findScoreDeptList.get(0).getOrganization().getId())){
					nullRiskObj.setRisk(risk);
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					nullRiskObj.setCompany(risk.getCompany());
					nullRiskObj.setTemplate(risk.getTemplate());
					nullRiskObj.setCode(risk.getCode());
					nullRiskObj.setName(risk.getName());
					nullRiskObj.setDesc(risk.getDesc());
					nullRiskObj.setParent(risk.getParent());
					nullRiskObj.setParentName(risk.getParentName());
					nullRiskObj.setLevel(risk.getLevel());
					nullRiskObj.setIdSeq(risk.getIdSeq());
					nullRiskObj.setDeleteStatus(risk.getDeleteStatus());
					nullRiskObj.setArchiveStatus(risk.getArchiveStatus());
					nullRiskObj.setSort(risk.getSort());
					nullRiskObj.setCreateTime(risk.getCreateTime());
					nullRiskObj.setCreateBy(risk.getCreateBy());
					nullRiskObj.setCreateOrg(risk.getCreateOrg());
					nullRiskObj.setLastModifyTime(risk.getLastModifyTime());
					nullRiskObj.setResponseStrategys(risk.getResponseStrategys());
					nullRiskObj.setIsRiskClass(risk.getIsRiskClass());
					nullRiskObj.setIsLeaf(risk.getIsLeaf());
					nullRiskObj.setIsInherit(risk.getIsInherit());
					nullRiskObj.setFormulaDefine(risk.getFormulaDefine());
					nullRiskObj.setAlarmScenario(risk.getAlarmScenario());
					nullRiskObj.setGatherFrequence(risk.getGatherFrequence());
					nullRiskObj.setGatherDayFormulr(risk.getGatherDayFormulr());
					nullRiskObj.setGatherDayFormulrShow(risk.getGatherDayFormulrShow());
					nullRiskObj.setResultCollectInterval(risk.getResultCollectInterval());
					nullRiskObj.setIsFix(risk.getIsFix());
					nullRiskObj.setIsUse(risk.getIsUse());
					nullRiskObj.setIsAnswer(risk.getIsAnswer());
					nullRiskObj.setCalc(risk.getCalc());
					nullRiskObj.setSchm(risk.getSchm());
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					
					saveRiskScoreObject(nullRiskObj);//保存打分对象
				}else{
					RiskScoreObject scoreObj = new RiskScoreObject();
					scoreObj.setId(Identities.uuid());
					scoreObj.setRisk(risk);
					scoreObj.setAssessPlan(assessPlan);
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					scoreObj.setCompany(risk.getCompany());
					scoreObj.setTemplate(risk.getTemplate());
					scoreObj.setCode(risk.getCode());
					scoreObj.setName(risk.getName());
					scoreObj.setDesc(risk.getDesc());
					scoreObj.setParent(risk.getParent());
					scoreObj.setParentName(risk.getParentName());
					scoreObj.setLevel(risk.getLevel());
					scoreObj.setIdSeq(risk.getIdSeq());
					scoreObj.setDeleteStatus(risk.getDeleteStatus());
					scoreObj.setArchiveStatus(risk.getArchiveStatus());
					scoreObj.setSort(risk.getSort());
					scoreObj.setCreateTime(risk.getCreateTime());
					scoreObj.setCreateBy(risk.getCreateBy());
					scoreObj.setCreateOrg(risk.getCreateOrg());
					scoreObj.setLastModifyTime(risk.getLastModifyTime());
					scoreObj.setResponseStrategys(risk.getResponseStrategys());
					scoreObj.setIsRiskClass(risk.getIsRiskClass());
					scoreObj.setIsLeaf(risk.getIsLeaf());
					scoreObj.setIsInherit(risk.getIsInherit());
					scoreObj.setFormulaDefine(risk.getFormulaDefine());
					scoreObj.setAlarmScenario(risk.getAlarmScenario());
					scoreObj.setGatherFrequence(risk.getGatherFrequence());
					scoreObj.setGatherDayFormulr(risk.getGatherDayFormulr());
					scoreObj.setGatherDayFormulrShow(risk.getGatherDayFormulrShow());
					scoreObj.setResultCollectInterval(risk.getResultCollectInterval());
					scoreObj.setIsFix(risk.getIsFix());
					scoreObj.setIsUse(risk.getIsUse());
					scoreObj.setIsAnswer(risk.getIsAnswer());
					scoreObj.setCalc(risk.getCalc());
					scoreObj.setSchm(risk.getSchm());
					scoreObj.setName(risk.getName());
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					saveRiskScoreObject(scoreObj);//保存打分对象
					//保存打分部门
					SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(UserContext.getUser().getEmpid());//查询人员主部门
					dept = empOrg.getSysOrganization();//得到当前登录人员的部门
					
					HashMap<String, Risk> orgRiskMap = o_riskOrgBO.findRiskOrgMap();
					String idSeq[] = dept.getOrgseq().replace(".", ",").split(",");
					String orgIds = "";
					for (String orgId : idSeq) {
						if(null != orgRiskMap.get(orgId)){
							orgIds = orgId;
							break;
						}
					}
					
					RiskOrg riskOrg = findRiskOrgTypeByriskIdAndOrgId(risk.getId(),orgIds);
					RiskScoreDept scoreDept = new RiskScoreDept();
					scoreDept.setId(Identities.uuid());
					scoreDept.setScoreObject(scoreObj);
					scoreDept.setOrganization(dept);
					scoreDept.setOrgType(riskOrg.getType());//部门类型
//					List<RiskScoreDept> deptList = o_scoreDeptBO.findRiskScoreDeptIsSave(scoreObj.getId(), riskOrg.getSysOrganization().getId(), riskOrg.getType());
//					if(!(deptList.size()>0)){//不存在，保存
						//saveRiskScoreDept(scoreDept);//保存打分部门
						RiskOrgTemp riskOrgTemp = new RiskOrgTemp();
						riskOrgTemp.setId(scoreDept.getId());
						riskOrgTemp.setEmp(new SysEmployee(UserContext.getUser().getEmpid()));
						riskOrgTemp.setRiskScoreObject(scoreObj);
						riskOrgTemp.setPlanId(planId);
						riskOrgTemp.setStatus("running");
						riskOrgTemp.setSysOrganization(dept);
						riskOrgTemp.setType(riskOrg.getType());
						riskOrgTempDAO.save(riskOrgTemp);
						//保存综合实体
						if(null != empId){
							RangObjectDeptEmp ode = new RangObjectDeptEmp();
							ode.setId(Identities.uuid());
							ode.setRiskOrgTemp(riskOrgTemp);
							ode.setScoreObject(scoreObj);
							SysEmployee scoreEmp = o_empGridBO.findEmpEntryByEmpId(empId);
							ode.setScoreEmp(scoreEmp);
							//更新部门，对象，人员实体
							o_assessTaskBO.saveObjectDeptEmp(ode);
						}
//					}
				}
			}else{
				RiskScoreObject scoreObj = new RiskScoreObject();
				scoreObj.setId(Identities.uuid());
				scoreObj.setRisk(risk);
				scoreObj.setAssessPlan(assessPlan);
				/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
				scoreObj.setCompany(risk.getCompany());
				scoreObj.setTemplate(risk.getTemplate());
				scoreObj.setCode(risk.getCode());
				scoreObj.setName(risk.getName());
				scoreObj.setDesc(risk.getDesc());
				scoreObj.setParent(risk.getParent());
				scoreObj.setParentName(risk.getParentName());
				scoreObj.setLevel(risk.getLevel());
				scoreObj.setIdSeq(risk.getIdSeq());
				scoreObj.setDeleteStatus(risk.getDeleteStatus());
				scoreObj.setArchiveStatus(risk.getArchiveStatus());
				scoreObj.setSort(risk.getSort());
				scoreObj.setCreateTime(risk.getCreateTime());
				scoreObj.setCreateBy(risk.getCreateBy());
				scoreObj.setCreateOrg(risk.getCreateOrg());
				scoreObj.setLastModifyTime(risk.getLastModifyTime());
				scoreObj.setResponseStrategys(risk.getResponseStrategys());
				scoreObj.setIsRiskClass(risk.getIsRiskClass());
				scoreObj.setIsLeaf(risk.getIsLeaf());
				scoreObj.setIsInherit(risk.getIsInherit());
				scoreObj.setFormulaDefine(risk.getFormulaDefine());
				scoreObj.setAlarmScenario(risk.getAlarmScenario());
				scoreObj.setGatherFrequence(risk.getGatherFrequence());
				scoreObj.setGatherDayFormulr(risk.getGatherDayFormulr());
				scoreObj.setGatherDayFormulrShow(risk.getGatherDayFormulrShow());
				scoreObj.setResultCollectInterval(risk.getResultCollectInterval());
				scoreObj.setIsFix(risk.getIsFix());
				scoreObj.setIsUse(risk.getIsUse());
				scoreObj.setIsAnswer(risk.getIsAnswer());
				scoreObj.setCalc(risk.getCalc());
				scoreObj.setSchm(risk.getSchm());
				scoreObj.setName(risk.getName());
				/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
				saveRiskScoreObject(scoreObj);//保存打分对象
				
				
				
				
				
				
				//保存打分部门
				SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(UserContext.getUser().getEmpid());//查询人员主部门
				dept = empOrg.getSysOrganization();//得到当前登录人员的部门
				
				HashMap<String, Risk> orgRiskMap = o_riskOrgBO.findRiskOrgMap();
				String idSeq[] = dept.getOrgseq().replace(".", ",").split(",");
				String orgIds = "";
				for (String orgId : idSeq) {
					if(null != orgRiskMap.get(orgId)){
						orgIds = orgId;
						break;
					}
				}
				
				RiskOrg riskOrg = findRiskOrgTypeByriskIdAndOrgId(risk.getId(),orgIds);
				RiskScoreDept scoreDept = new RiskScoreDept();
				scoreDept.setId(Identities.uuid());
				scoreDept.setScoreObject(scoreObj);
				scoreDept.setOrganization(dept);
				scoreDept.setOrgType(riskOrg.getType());//部门类型
//				List<RiskScoreDept> deptList = o_scoreDeptBO.findRiskScoreDeptIsSave(scoreObj.getId(), riskOrg.getSysOrganization().getId(), riskOrg.getType());
//				if(!(deptList.size()>0)){//不存在，保存
//					saveRiskScoreDept(scoreDept);//保存打分部门
					RiskOrgTemp riskOrgTemp = new RiskOrgTemp();
					riskOrgTemp.setId(scoreDept.getId());
					riskOrgTemp.setEmp(new SysEmployee(UserContext.getUser().getEmpid()));
					riskOrgTemp.setRiskScoreObject(scoreObj);
					riskOrgTemp.setPlanId(planId);
//					riskOrgTemp.setRisk(risk);
					riskOrgTemp.setStatus("running");
					riskOrgTemp.setSysOrganization(dept);
					riskOrgTemp.setType(riskOrg.getType());
					riskOrgTempDAO.save(riskOrgTemp);
					
					//保存综合实体
					if(null != empId){
						RangObjectDeptEmp ode = new RangObjectDeptEmp();
						ode.setId(Identities.uuid());
						ode.setRiskOrgTemp(riskOrgTemp);
						ode.setScoreObject(scoreObj);
						SysEmployee scoreEmp = o_empGridBO.findEmpEntryByEmpId(empId);
						ode.setScoreEmp(scoreEmp);
						//更新部门，对象，人员实体
						o_assessTaskBO.saveObjectDeptEmp(ode);
					}
//				}
			}
		}
		//risk.setAlarmScenario();//告警方案
//		if(StringUtils.isNotBlank(templateId)){
//			Template template = new Template();
//			template.setId(templateId);
//			risk.setTemplate(template);//评估模板
//		}else{
//			risk.setTemplate(null);
//		}
		
		//o_riskBO.saveRisk(risk);
	}
	
	@Transactional
	public String addRiskAndsaveAllBySome(String planId, Risk risk, String empId, String templateId,List<RiskOrgTemp> zhuzeOrgList,List<RiskOrgTemp> relaOrgList) {
		
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
		//Risk risk = o_riskBO.findRiskById(riskId);
		SysOrganization dept = null;
		String rtnObjectId = null;
		
		//打分对象是否存在
		RiskScoreObject exitObj = o_scoreObjectBO.findRiskScoreObjsByPlanAndRisk(planId, risk.getId());
		if(null == exitObj){//不存在相同的打分对象
			RiskScoreObject nullRiskObj = o_riskIdentifyBO.findnullRiskObjByPlanId(planId);
			//辨识计划可能含有没有风险的打分对象，如果存在，更新打分对象；不存在则新建
			if(null != nullRiskObj){
				List<RiskScoreDept> findScoreDeptList = o_scoreDeptBO.findRiskDeptByObjId(nullRiskObj.getId());
				if(UserContext.getUser().getMajorDeptId().equals(findScoreDeptList.get(0).getOrganization().getId())){
					nullRiskObj.setRisk(risk);
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					nullRiskObj.setCompany(risk.getCompany());
					nullRiskObj.setTemplate(risk.getTemplate());
					nullRiskObj.setCode(risk.getCode());
					nullRiskObj.setName(risk.getName());
					nullRiskObj.setDesc(risk.getDesc());
					nullRiskObj.setParent(risk.getParent());
					nullRiskObj.setParentName(risk.getParentName());
					nullRiskObj.setLevel(risk.getLevel());
					nullRiskObj.setIdSeq(risk.getIdSeq());
					nullRiskObj.setDeleteStatus(risk.getDeleteStatus());
					nullRiskObj.setArchiveStatus(risk.getArchiveStatus());
					nullRiskObj.setSort(risk.getSort());
					nullRiskObj.setCreateTime(risk.getCreateTime());
					nullRiskObj.setCreateBy(risk.getCreateBy());
					nullRiskObj.setCreateOrg(risk.getCreateOrg());
					nullRiskObj.setLastModifyTime(risk.getLastModifyTime());
					nullRiskObj.setResponseStrategys(risk.getResponseStrategys());
					nullRiskObj.setIsRiskClass(risk.getIsRiskClass());
					nullRiskObj.setIsLeaf(risk.getIsLeaf());
					nullRiskObj.setIsInherit(risk.getIsInherit());
					nullRiskObj.setFormulaDefine(risk.getFormulaDefine());
					nullRiskObj.setAlarmScenario(risk.getAlarmScenario());
					nullRiskObj.setGatherFrequence(risk.getGatherFrequence());
					nullRiskObj.setGatherDayFormulr(risk.getGatherDayFormulr());
					nullRiskObj.setGatherDayFormulrShow(risk.getGatherDayFormulrShow());
					nullRiskObj.setResultCollectInterval(risk.getResultCollectInterval());
					nullRiskObj.setIsFix(risk.getIsFix());
					nullRiskObj.setIsUse(risk.getIsUse());
					nullRiskObj.setIsAnswer(risk.getIsAnswer());
					nullRiskObj.setCalc(risk.getCalc());
					nullRiskObj.setSchm(risk.getSchm());
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					
					saveRiskScoreObject(nullRiskObj);//保存打分对象
					//添加风险直接
					rtnObjectId = nullRiskObj.getId();
				}else{
					RiskScoreObject scoreObj = new RiskScoreObject();
					scoreObj.setId(Identities.uuid());
					scoreObj.setRisk(risk);
					scoreObj.setAssessPlan(assessPlan);
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					scoreObj.setCompany(risk.getCompany());
					scoreObj.setTemplate(risk.getTemplate());
					scoreObj.setCode(risk.getCode());
					scoreObj.setName(risk.getName());
					scoreObj.setDesc(risk.getDesc());
					scoreObj.setParent(risk.getParent());
					scoreObj.setParentName(risk.getParentName());
					scoreObj.setLevel(risk.getLevel());
					scoreObj.setIdSeq(risk.getIdSeq());
					scoreObj.setDeleteStatus(risk.getDeleteStatus());
					scoreObj.setArchiveStatus(risk.getArchiveStatus());
					scoreObj.setSort(risk.getSort());
					scoreObj.setCreateTime(risk.getCreateTime());
					scoreObj.setCreateBy(risk.getCreateBy());
					scoreObj.setCreateOrg(risk.getCreateOrg());
					scoreObj.setLastModifyTime(risk.getLastModifyTime());
					scoreObj.setResponseStrategys(risk.getResponseStrategys());
					scoreObj.setIsRiskClass(risk.getIsRiskClass());
					scoreObj.setIsLeaf(risk.getIsLeaf());
					scoreObj.setIsInherit(risk.getIsInherit());
					scoreObj.setFormulaDefine(risk.getFormulaDefine());
					scoreObj.setAlarmScenario(risk.getAlarmScenario());
					scoreObj.setGatherFrequence(risk.getGatherFrequence());
					scoreObj.setGatherDayFormulr(risk.getGatherDayFormulr());
					scoreObj.setGatherDayFormulrShow(risk.getGatherDayFormulrShow());
					scoreObj.setResultCollectInterval(risk.getResultCollectInterval());
					scoreObj.setIsFix(risk.getIsFix());
					scoreObj.setIsUse(risk.getIsUse());
					scoreObj.setIsAnswer(risk.getIsAnswer());
					scoreObj.setCalc(risk.getCalc());
					scoreObj.setSchm(risk.getSchm());
					scoreObj.setName(risk.getName());
					/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
					saveRiskScoreObject(scoreObj);//保存打分对象
					rtnObjectId = scoreObj.getId();
					//保存打分部门scoreObj
					SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(UserContext.getUser().getEmpid());//查询人员主部门
					dept = empOrg.getSysOrganization();//得到当前登录人员的部门
					
					HashMap<String, Risk> orgRiskMap = o_riskOrgBO.findRiskOrgMap();
					String idSeq[] = dept.getOrgseq().replace(".", ",").split(",");
					String orgIds = "";
					for (String orgId : idSeq) {
						if(null != orgRiskMap.get(orgId)){
							orgIds = orgId;
							break;
						}
					}
					
					List<RiskOrgRelationTemp> riskOrgRelationTempListMajor = new ArrayList<RiskOrgRelationTemp>();
					List<RiskOrgRelationTemp> riskOrgRelationTempListRela = new ArrayList<RiskOrgRelationTemp>();
					//添加主责部门
					if(zhuzeOrgList != null && zhuzeOrgList.size()>0){
						for(RiskOrgTemp riskOrgTemp :zhuzeOrgList){
							riskOrgTemp.setEmp(new SysEmployee(UserContext.getUser().getEmpid()));
							riskOrgTemp.setRiskScoreObject(scoreObj);
							riskOrgTemp.setPlanId(planId);
							riskOrgTemp.setStatus("running");
							riskOrgTemp.setSysOrganization(dept);
							
							
							RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
							riskOrgRelationTemp.setId(Identities.uuid());
							riskOrgRelationTemp.setRiskScoreObject(scoreObj);
							riskOrgRelationTemp.setRisk(risk);
							riskOrgRelationTemp.setSysOrganization(dept);
							riskOrgRelationTemp.setType(riskOrgTemp.getType());
							riskOrgRelationTempListMajor.add(riskOrgRelationTemp);
							
							
							
							
							
							//保存综合实体
							if(null != empId){
								RangObjectDeptEmp ode = new RangObjectDeptEmp();
								ode.setId(Identities.uuid());
								ode.setRiskOrgTemp(riskOrgTemp);
								ode.setScoreObject(scoreObj);
								SysEmployee scoreEmp = o_empGridBO.findEmpEntryByEmpId(empId);
								ode.setScoreEmp(scoreEmp);
								//更新部门，对象，人员实体
								o_assessTaskBO.saveObjectDeptEmp(ode);
							}
						}
						//保存风险和部门关系
						saveRiskOrgTemp(zhuzeOrgList);
						this.riskOrgTempBO.saveRiskOrgRelationBatch(riskOrgRelationTempListMajor);
					}
					

					//添加相关部门
					if(relaOrgList != null && relaOrgList.size()>0){
						for(RiskOrgTemp riskOrgTemp :relaOrgList){
//							riskOrgTemp.setEmp(new SysEmployee(UserContext.getUser().getEmpid()));
							riskOrgTemp.setRiskScoreObject(scoreObj);
							riskOrgTemp.setPlanId(planId);
							riskOrgTemp.setStatus("running");
//							riskOrgTemp.setSysOrganization(dept);
							
							RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
							riskOrgRelationTemp.setId(Identities.uuid());
							riskOrgRelationTemp.setRiskScoreObject(scoreObj);
							riskOrgRelationTemp.setRisk(risk);
							riskOrgRelationTemp.setSysOrganization(riskOrgTemp.getSysOrganization());
							riskOrgRelationTemp.setType(riskOrgTemp.getType());
							riskOrgRelationTempListRela.add(riskOrgRelationTemp);
							
							
							
							
							//保存综合实体
							/*if(null != empId){
								RangObjectDeptEmp ode = new RangObjectDeptEmp();
								ode.setId(Identities.uuid());
								ode.setRiskOrgTemp(riskOrgTemp);
								ode.setScoreObject(scoreObj);
								SysEmployee scoreEmp = o_empGridBO.findEmpEntryByEmpId(empId);
								ode.setScoreEmp(scoreEmp);
								//更新部门，对象，人员实体
								o_assessTaskBO.saveObjectDeptEmp(ode);
							}*/
						}
						//保存风险和部门关系
						saveRiskOrgTemp(relaOrgList);
						this.riskOrgTempBO.saveRiskOrgRelationBatch(riskOrgRelationTempListRela);
					}
				}
			}else{
				RiskScoreObject scoreObj = new RiskScoreObject();
				scoreObj.setId(Identities.uuid());
				scoreObj.setRisk(risk);
				scoreObj.setAssessPlan(assessPlan);
				/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
				scoreObj.setCompany(risk.getCompany());
				scoreObj.setTemplate(risk.getTemplate());
				scoreObj.setCode(risk.getCode());
				scoreObj.setName(risk.getName());
				scoreObj.setDesc(risk.getDesc());
				scoreObj.setParent(risk.getParent());
				scoreObj.setParentName(risk.getParentName());
				scoreObj.setLevel(risk.getLevel());
				scoreObj.setIdSeq(risk.getIdSeq());
				scoreObj.setDeleteStatus(risk.getDeleteStatus());
				scoreObj.setArchiveStatus(risk.getArchiveStatus());
				scoreObj.setSort(risk.getSort());
				scoreObj.setCreateTime(risk.getCreateTime());
				scoreObj.setCreateBy(risk.getCreateBy());
				scoreObj.setCreateOrg(risk.getCreateOrg());
				scoreObj.setLastModifyTime(risk.getLastModifyTime());
				scoreObj.setResponseStrategys(risk.getResponseStrategys());
				scoreObj.setIsRiskClass(risk.getIsRiskClass());
				scoreObj.setIsLeaf(risk.getIsLeaf());
				scoreObj.setIsInherit(risk.getIsInherit());
				scoreObj.setFormulaDefine(risk.getFormulaDefine());
				scoreObj.setAlarmScenario(risk.getAlarmScenario());
				scoreObj.setGatherFrequence(risk.getGatherFrequence());
				scoreObj.setGatherDayFormulr(risk.getGatherDayFormulr());
				scoreObj.setGatherDayFormulrShow(risk.getGatherDayFormulrShow());
				scoreObj.setResultCollectInterval(risk.getResultCollectInterval());
				scoreObj.setIsFix(risk.getIsFix());
				scoreObj.setIsUse(risk.getIsUse());
				scoreObj.setIsAnswer(risk.getIsAnswer());
				scoreObj.setCalc(risk.getCalc());
				scoreObj.setSchm(risk.getSchm());
				scoreObj.setName(risk.getName());
				/////////////////////将所有字段复制到打分对象表  吉志强///////////////////////
				saveRiskScoreObject(scoreObj);//保存打分对象
				rtnObjectId = scoreObj.getId();
				//保存打分部门
				SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(UserContext.getUser().getEmpid());//查询人员主部门
				dept = empOrg.getSysOrganization();//得到当前登录人员的部门
				
				HashMap<String, Risk> orgRiskMap = o_riskOrgBO.findRiskOrgMap();
				String idSeq[] = dept.getOrgseq().replace(".", ",").split(",");
				String orgIds = "";
				for (String orgId : idSeq) {
					if(null != orgRiskMap.get(orgId)){
						orgIds = orgId;
						break;
					}
				}
				List<RiskOrgRelationTemp> riskOrgRelationTempListMajor = new ArrayList<RiskOrgRelationTemp>();
				List<RiskOrgRelationTemp> riskOrgRelationTempListRela = new ArrayList<RiskOrgRelationTemp>();
				//添加主责部门
				if(zhuzeOrgList != null && zhuzeOrgList.size()>0){
					for(RiskOrgTemp riskOrgTemp :zhuzeOrgList){
						riskOrgTemp.setId(Identities.uuid());
						riskOrgTemp.setEmp(new SysEmployee(UserContext.getUser().getEmpid()));
						riskOrgTemp.setRiskScoreObject(scoreObj);
						riskOrgTemp.setPlanId(planId);
//						riskOrgTemp.setRisk(risk);
						riskOrgTemp.setStatus("running");
						riskOrgTemp.setSysOrganization(dept);
						
						RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
						riskOrgRelationTemp.setId(Identities.uuid());
						riskOrgRelationTemp.setRiskScoreObject(scoreObj);
						riskOrgRelationTemp.setRisk(risk);
						riskOrgRelationTemp.setSysOrganization(dept);
						riskOrgRelationTemp.setType(riskOrgTemp.getType());
						riskOrgRelationTempListMajor.add(riskOrgRelationTemp);
						
						//保存综合实体
						RangObjectDeptEmp ode = new RangObjectDeptEmp();
						ode.setId(Identities.uuid());
						ode.setRiskOrgTemp(riskOrgTemp);
						ode.setScoreObject(scoreObj);
						SysEmployee scoreEmp = o_empGridBO.findEmpEntryByEmpId(empId);
						ode.setScoreEmp(scoreEmp);
						//更新部门，对象，人员实体
						o_assessTaskBO.saveObjectDeptEmp(ode);
					}
					//保存风险和部门关系
					saveRiskOrgTemp(zhuzeOrgList);
					this.riskOrgTempBO.saveRiskOrgRelationBatch(riskOrgRelationTempListMajor);
				}
				

				//添加相关部门
				if(relaOrgList != null && relaOrgList.size()>0){
					for(RiskOrgTemp riskOrgTemp :relaOrgList){
						riskOrgTemp.setId(Identities.uuid());
//						riskOrgTemp.setEmp(new SysEmployee(UserContext.getUser().getEmpid()));
						riskOrgTemp.setRiskScoreObject(scoreObj);
						riskOrgTemp.setPlanId(planId);
//						riskOrgTemp.setRisk(risk);
						riskOrgTemp.setStatus("running");
//						riskOrgTemp.setSysOrganization(dept);
						

						RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
						riskOrgRelationTemp.setId(Identities.uuid());
						riskOrgRelationTemp.setRiskScoreObject(scoreObj);
						riskOrgRelationTemp.setRisk(risk);
						riskOrgRelationTemp.setSysOrganization(riskOrgTemp.getSysOrganization());
						riskOrgRelationTemp.setType(riskOrgTemp.getType());
						riskOrgRelationTempListRela.add(riskOrgRelationTemp);
						
						//保存综合实体
						/*if(null != empId){
							RangObjectDeptEmp ode = new RangObjectDeptEmp();
							ode.setId(Identities.uuid());
							ode.setRiskOrgTemp(riskOrgTemp);
							ode.setScoreObject(scoreObj);
							SysEmployee scoreEmp = o_empGridBO.findEmpEntryByEmpId(empId);
							ode.setScoreEmp(scoreEmp);
							//更新部门，对象，人员实体
							o_assessTaskBO.saveObjectDeptEmp(ode);
						}*/
					}
					//保存风险和部门关系
					saveRiskOrgTemp(relaOrgList);
					this.riskOrgTempBO.saveRiskOrgRelationBatch(riskOrgRelationTempListRela);
				}
				//新添加风险初始化 t_rm_dept_lead_circusee
				DeptLeadCircusee deptLeadCircusee = new DeptLeadCircusee();
				deptLeadCircusee.setId(Identities.uuid());
				//获取主责部门的风险管理员
				SysEmployee emp = findDeptLeaderOrCbr(planId,UserContext.getUser().getMajorDeptId());
				deptLeadCircusee.setDeptLeadEmpId(emp);
				deptLeadCircusee.setRiskAssessPlan(new RiskAssessPlan(planId));
				deptLeadCircusee.setScoreObjectId(new RiskScoreObject(rtnObjectId));
				o_deptLeadCircuseeBO.mergeDeptLeadCircusee(deptLeadCircusee);
			}
		}
		return rtnObjectId;
	}
	/**
	 * deleteRiskAndDeleteAllBySome: 删除风险评估中新增的打分对象
	 * @param planId	计划id
	 * @param riskId	风险id
	 * @param empId		当前登录人员id
	 * @return VOID
	 * @author 王再冉
	 */
	public void deleteRiskAndDeleteAllBySome(String planId, String riskId, String empId) {
		String[] riskIds = riskId.split(",");
		List<RiskScoreDept> scoreDeptList = new ArrayList<RiskScoreDept>();
		//打分对象是否存在
		RiskScoreObject exitObj = o_scoreObjectBO.findRiskScoreObjsByPlanAndRisk(planId, riskId);
		if(null != exitObj){
			scoreDeptList = o_scoreDeptBO.findRiskScoreDeptByScoreObjectId(exitObj.getId());
			List<RangObjectDeptEmp> rangObjDeptEmpList = o_rangeObjDeptEmpBO.findObjDeptEmpsByObjId(exitObj.getId());
			for(RangObjectDeptEmp rode : rangObjDeptEmpList){
				EditIdea editIdea = o_saveAssessBO.findEditIdeaByObjectDeptEmpId(rode.getId());
				if(null != editIdea){
					o_editIdeaDAO.delete(editIdea);
				}
			}
			
			o_rangeObjDeptEmpBO.removeRangObjectDeptEmps(rangObjDeptEmpList);
		}
		o_riskScoreDeptDAO.delete(scoreDeptList.get(0));
		this.removeRiskScoreObj(exitObj);//删除打分对象
		List<DeptLeadCircusee> deptLeadCircuseeList = 
				o_deptLeadCircuseeBO.findDeptLeadCircuseeByAssessPlanIdAndScoreObjectId(planId, exitObj.getId());
		for (DeptLeadCircusee en : deptLeadCircuseeList) {
			o_deptLeadCircuseeBO.removeDeptLeadCircusee(en);
		}
		o_riskCmpBO.deleteRisk(riskIds);
	}

	/**
	 * 根据风险id和机构id查询关联数据
	 * @param riskId	风险id
	 * @param orgId		机构id
	 * @return
	 */
	public RiskOrg findRiskOrgTypeByriskIdAndOrgId(String riskId, String orgId) {
		Criteria c = o_riskOrgDAO.createCriteria();
		c.add(Restrictions.and(Restrictions.eq("risk.id", riskId), Restrictions.eq("sysOrganization.id", orgId)));
		return (RiskOrg) c.list().get(0);
	}
	/**
	 * 保存打分部门和打分对象
	 * @param riskIds	风险id字符串
	 * @param planId	计划id
	 * @param isMain	true：只保存责任部门
	 * @param deptId	部门id
	 */
	@Transactional
	@RecordLog("保存打分部门和打分对象信息")
	public boolean saveScoreObjectsAndScoreDepts(String riskIds, String planId, Boolean isMain, String deptId) {
		List<String> riskIdList = new ArrayList<String>();
		List<RiskScoreObject> saveObjList = new ArrayList<RiskScoreObject>();
		List<RiskScoreDept> saveDeptList = new ArrayList<RiskScoreDept>();
		
		List<RiskOrgTemp> riskOrgTempList = new ArrayList<RiskOrgTemp>();
		List<RiskOrgRelationTemp> riskOrgRelationTempList = new ArrayList<RiskOrgRelationTemp>();
		Map<String,String> savedRiskIdsMap = new HashMap<String, String>();//已经保存的风险（打分对象只保存一次）
		if (StringUtils.isNotBlank(riskIds)) {
			String[] idArray = riskIds.split(",");
			for (String id : idArray) {
				riskIdList.add(id);//风险id集合
			}
		}
		//查询计划下所有打分对象打分部门和风险id
		HashMap<String, ArrayList<String>> savedMap = o_scoreDeptBO.findAllSaveScoresByplanId(planId);
		ArrayList<HashMap<String, String>> riskOrgMapList = this.findRiskOrgsByriskIds(riskIdList, isMain,deptId);
		for(HashMap<String, String> hashMap : riskOrgMapList){
			Boolean saved = true;
			String riskId = hashMap.get("riskId");
			String orgId = hashMap.get("orgId");
			String eType = hashMap.get("eType");
			String empId = hashMap.get("empId");
			if(null == savedMap.get(riskId)){//该风险未保存
				RiskScoreObject scoreObj = new RiskScoreObject();
				scoreObj.setId(Identities.uuid());
				scoreObj.setRisk(new Risk(riskId));
				scoreObj.setAssessPlan(new RiskAssessPlan(planId));
				if(null == savedRiskIdsMap.get(riskId)){//判断之前保存的打分对象中是否有重复的数据
					saveObjList.add(scoreObj);//批量保存的打分对象list
					savedRiskIdsMap.put(riskId, scoreObj.getId());
				}else{
					scoreObj.setId(savedRiskIdsMap.get(riskId));
				}
				//保存打分部门
				RiskScoreDept scoreDept = new RiskScoreDept();
				scoreDept.setId(Identities.uuid());
				scoreDept.setScoreObject(scoreObj);
				scoreDept.setOrganization(new SysOrganization(orgId));
				scoreDept.setOrgType(eType);//部门类型
				saveDeptList.add(scoreDept);
				
			///////  增加riskOrgTemp表为该表赋值    begin
			RiskOrgTemp riskOrgTemp = new RiskOrgTemp();
			riskOrgTemp.setId(scoreDept.getId());//为了不更改原逻辑，为新增的表 t_rm_risk_org_temp的主键与t_rm_risk_score_dept一致
			riskOrgTemp.setRiskScoreObject(scoreObj);
			riskOrgTemp.setPlanId(planId);
//			riskOrgTemp.setRisk(new Risk(riskId));
			riskOrgTemp.setEmp(new SysEmployee(empId));
			//running正在进行辨识的风险
			riskOrgTemp.setStatus("running");
			riskOrgTemp.setSysOrganization(new SysOrganization(orgId));
			riskOrgTemp.setType(eType);
			riskOrgTempList.add(riskOrgTemp);
			///////  增加riskOrgTemp表为该表赋值    end
			
			
			///////  增加riskOrgTemp表为该表赋值    begin
			
			RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
			riskOrgRelationTemp.setId(scoreDept.getId());
			riskOrgRelationTemp.setRiskScoreObject(scoreObj);
			riskOrgRelationTemp.setRisk(new Risk(riskId));
			riskOrgRelationTemp.setSysOrganization(new SysOrganization(orgId));
			riskOrgRelationTemp.setType(eType);
			riskOrgRelationTempList.add(riskOrgRelationTemp);
			///////  增加riskOrgTemp表为该表赋值    end
				
				
				
			}else{
				ArrayList<String> savedStr = savedMap.get(riskId);
				for(String str : savedStr){
					String[] strArray = str.split("--");//0:scoreObjId,1:scoreDeptId,2:orgId,3:orgType
					if(orgId.equals(strArray[2]) && eType.equals(strArray[3])){//部门id和部门类型同时满足，不保存打分部门
						saved = false;
					}
				}
				if(saved){
					String objId = savedStr.get(0).split("--")[0];
					RiskScoreDept scoreDept = new RiskScoreDept();
					scoreDept.setId(Identities.uuid());
					scoreDept.setScoreObject(new RiskScoreObject(objId));
					scoreDept.setOrganization(new SysOrganization(orgId));
					scoreDept.setOrgType(eType);//部门类型
					saveDeptList.add(scoreDept);
					
					///////  增加riskOrgTemp表为该表赋值    begin
					RiskOrgTemp riskOrgTemp = new RiskOrgTemp();
					riskOrgTemp.setId(scoreDept.getId());//为了不更改原逻辑，为新增的表 t_rm_risk_org_temp的主键与t_rm_risk_score_dept一致
					riskOrgTemp.setRiskScoreObject(new RiskScoreObject(objId));
					riskOrgTemp.setPlanId(planId);
//					riskOrgTemp.setRisk(new Risk(riskId));
					riskOrgTemp.setEmp(new SysEmployee(empId));
					//running正在进行辨识的风险
					riskOrgTemp.setStatus("running");
					riskOrgTemp.setSysOrganization(new SysOrganization(orgId));
					riskOrgTemp.setType(eType);
					riskOrgTempList.add(riskOrgTemp);
					///////  增加riskOrgTemp表为该表赋值    end
					
					///////  增加riskOrgTemp表为该表赋值    begin
					RiskOrgRelationTemp riskOrgRelationTemp = new RiskOrgRelationTemp();
					riskOrgRelationTemp.setId(scoreDept.getId());
					riskOrgRelationTemp.setRiskScoreObject(new RiskScoreObject(objId));
					riskOrgRelationTemp.setRisk(new Risk(riskId));
					riskOrgRelationTemp.setSysOrganization(new SysOrganization(orgId));
					riskOrgRelationTemp.setType(eType);
					riskOrgRelationTempList.add(riskOrgRelationTemp);
					///////  增加riskOrgTemp表为该表赋值    end
					
					
				}
			}
		}
		this.saveRiskScoreObjectsSql(saveObjList);//批量保存打分对象
		//this.saveRiskScoreDeptsSql(saveDeptList);
		//增加riskOrgTemp表为该表赋值    
		this.saveRiskOrgTemp(riskOrgTempList);
		this.riskOrgTempBO.saveRiskOrgRelationBatch(riskOrgRelationTempList);
		
		
		if(saveObjList.size() < riskIdList.size()){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 批量保存打分对象
	 */
	@Transactional
    public void saveRiskScoreObjectsSql(final List<RiskScoreObject> objList) {
        this.o_riskScoreObjDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                //String sql = "insert into t_rm_risk_score_object (id,risk_id,assess_plan_id) values(?,?,?)";
                //风险辨识的时候选择风险时将选择的风险复制到打分对象表中  吉志强 2017年4月20日14:31:48
                String sql = "INSERT INTO t_rm_risk_score_object ( ID, RISK_ID, assess_plan_id, COMPANY_ID, TEMPLATE_ID, RISK_CODE, RISK_NAME, EDESC, PARENT_ID, PARENT_NAME, ELEVEL, ID_SEQ, DELETE_ESTATUS, ARCHIVE_STATUS, ESORT, CREATE_TIME, CREATE_BY, CREATE_ORG_ID, LAST_MODIFY_TIME, LAST_MODIFY_BY, ASSESSMENT_MEASURE, RESPONSE_STRATEGY, IS_RISK_CLASS, IS_LEAF, CHART_TYPE, RELATIVE_TO, IS_INHERIT, FORMULA_DEFINE, ALARM_SCENARIO, GATHER_FREQUENCE, GATHER_DAY_FORMULR_SHOW, GATHER_DAY_FORMULR, RESULT_COLLECT_INTERVAL, ALARM_MEASURE, IMPACT_TIME, MONITOR_FREQUENCE, MONITOR_DAY_FORMULR_SHOW, MONITOR_DAY_FORMULR, MONITOR_COLLECT_INTERVAL, EXPAND_DAYS, IS_FIX, IS_USE, IS_ANSWER, IS_CALC, calc_digit, SCHM ) SELECT ?, ?, ?, COMPANY_ID, TEMPLATE_ID, RISK_CODE, RISK_NAME, EDESC, PARENT_ID, PARENT_NAME, ELEVEL, ID_SEQ, DELETE_ESTATUS, ARCHIVE_STATUS, ESORT, CREATE_TIME, CREATE_BY, CREATE_ORG_ID, LAST_MODIFY_TIME, LAST_MODIFY_BY, ASSESSMENT_MEASURE, RESPONSE_STRATEGY, IS_RISK_CLASS, IS_LEAF, CHART_TYPE, RELATIVE_TO, IS_INHERIT, FORMULA_DEFINE, ALARM_SCENARIO, GATHER_FREQUENCE, GATHER_DAY_FORMULR_SHOW, GATHER_DAY_FORMULR, RESULT_COLLECT_INTERVAL, ALARM_MEASURE, IMPACT_TIME, MONITOR_FREQUENCE, MONITOR_DAY_FORMULR_SHOW, MONITOR_DAY_FORMULR, MONITOR_COLLECT_INTERVAL, EXPAND_DAYS, IS_FIX, IS_USE, IS_ANSWER, IS_CALC, calc_digit, SCHM FROM t_rm_risks risks WHERE risks.ID = ? ";
                pst = connection.prepareStatement(sql);
                
                for (RiskScoreObject obj : objList) {
                	pst.setString(1, obj.getId());
                	if(null != obj.getRisk()){
                		pst.setString(2, obj.getRisk().getId());
                		//风险辨识的时候选择风险时将选择的风险复制到打分对象表中  吉志强 2017年4月20日14:31:48
                		pst.setString(4, obj.getRisk().getId());
                	}else{
                		pst.setString(2, null);
                		//风险辨识的时候选择风险时将选择的风险复制到打分对象表中  吉志强 2017年4月20日14:31:48
                		pst.setString(4, null);
                	}
                    pst.setString(3, obj.getAssessPlan().getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	/**
	 * 批量保存打分部门
	 * @param deptList
	 */
	@Transactional
    public void saveRiskScoreDeptsSql(final List<RiskScoreDept> deptList) {
        this.o_riskScoreDeptDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_risk_score_dept (id,score_object_id,org_id,org_type) values(?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (RiskScoreDept dept : deptList) {
                	pst.setString(1, dept.getId());
                    pst.setString(2, dept.getScoreObject().getId());
                    pst.setString(3, dept.getOrganization().getId());
                    pst.setString(4, dept.getOrgType());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	
	
	
	
	///////////////////////  吉志强新增打分对象临时表 begin    /////////////////////////
	
	@Transactional
    public void saveRiskOrgTemp(final List<RiskOrgTemp> list) {
        this.riskOrgTempDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_rm_risk_org_temp (id,etype,org_id,position_id,emp_id,plan_id,`status`,object_id) values(?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (RiskOrgTemp riskOrgTemp : list) {
                	pst.setString(1, riskOrgTemp.getId());
                	pst.setString(2, riskOrgTemp.getType());
                	pst.setString(3, riskOrgTemp.getSysOrganization().getId());
                	pst.setString(4, riskOrgTemp.getSysPosition() == null ? null : riskOrgTemp.getSysPosition().getId());
                	pst.setString(5,  null ==riskOrgTemp.getEmp() ||riskOrgTemp.getEmp().getId()==null||riskOrgTemp.getEmp().getId().equals("") ? null : riskOrgTemp.getEmp().getId());
                	pst.setString(6, riskOrgTemp.getPlanId());
                	pst.setString(7, riskOrgTemp.getStatus());
                	pst.setString(8, riskOrgTemp.getRiskScoreObject().getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	///////////////////////  吉志强新增打分对象临时表 end    //////////////////////////
	
	@Transactional
    public void deleteRiskOrgTempByObjectId(final String objectId) {
        this.riskOrgTempDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_risk_org_temp where object_id=?";
                pst = connection.prepareStatement(sql);
            	pst.setString(1, objectId);
                pst.execute();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	
	@Transactional
    public void deleteRiskOrgTemp(final List<RiskOrgTemp> list) {
        this.riskOrgTempDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_risk_org_temp where plan_id=? and object_id=?";
                pst = connection.prepareStatement(sql);
                
                for (RiskOrgTemp riskOrgTemp : list) {
                	pst.setString(1, riskOrgTemp.getPlanId());
                	pst.setString(2, riskOrgTemp.getRiskScoreObject().getId());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	
	/**
	 * 批量删除打分部门，判断打分对象下是否存在打分部门，不存在就删除
	 * @param ids	打分部门id
	 * @param objIds	打分对象id
	 * @return
	 */
	@Transactional
	@RecordLog("删除计划下打分对象及打分部门")
	public Boolean removeScoreDeptsAndScoreObjBydeptIdsAndPlanId(String ids, String objIds){
		//this.removeScoreDeptsByDeptIds(ids);//批量删除打分部门
		this.removeRiskOrgTempByDeptIds(ids);
		this.riskOrgTempBO.removeRiskOrgRelationTempByDeptIds(ids);
		
		List<String> objIdList = new ArrayList<String>();
		if (StringUtils.isNotBlank(objIds)) {
			  String[] idArray = objIds.split(",");
			  for (String id : idArray) {
				  if(!objIdList.contains(id)){
					  objIdList.add(id);
				  }
			  }
		}
		List<Map<String,Object>> deptMapList = o_scoreDeptBO.findRiskDeptByObjIdList(objIdList);
		for(Map<String,Object> deptMap : deptMapList){
			RiskOrgTemp scoreDept = (RiskOrgTemp)deptMap.get("scoreDept");
			RiskScoreObject scoreObj = scoreDept.getRiskScoreObject();
			if(objIdList.contains(scoreObj.getId())){//如果打分对象下存在打分部门
				objIdList.remove(scoreObj.getId());//从打分对象id集合中删除该记录，剩下的打分对象id集合都删除
			}
		}
		String scoreobjIds = "";
		//删除没有打分部门的打分对象
		for(String objId : objIdList){
			scoreobjIds = objId + "," + scoreobjIds;
		}
		this.removeRiskScoreObjectsByIds(scoreobjIds);//批量删除打分对象
		return true;
	}
	/**
	 * 批量删除打分部门
	 * @param deptids	打分部门id字符串
	 */
	@Transactional
	public void removeScoreDeptsByDeptIds(String deptids){
		if(!"".equals(deptids)){
			o_riskScoreDeptDAO.createQuery("delete RiskScoreDept where id in (:ids)")
			.setParameterList("ids", StringUtils.split(deptids,",")).executeUpdate();
		}
	}
	
	/**
	 * 批量删除流程过程中风险与部门之间的关系表数据
	 * @author Jzq
	 * @time 2017年5月8日13:26:05
	 * @param deptids
	 */
	@Transactional
	public void removeRiskOrgTempByDeptIds(String deptids){
		if(!"".equals(deptids)){
			this.riskOrgTempDAO.createQuery("delete RiskOrgTemp where id in (:ids)")
			.setParameterList("ids", StringUtils.split(deptids,",")).executeUpdate();
		}
	}
	
	
	/**
	 * 批量删除打分对象
	 * @param ids	打分对象id字符串
	 */
	@Transactional
	public void removeRiskScoreObjectsByIds(String ids){
		if(!"".equals(ids)){
			o_riskScoreObjDAO.createQuery("delete RiskScoreObject where id in (:scoreObjIds)")
			.setParameterList("scoreObjIds", StringUtils.split(ids,",")).executeUpdate();
		}
	}
	/**
	 * 根据计划id查询打分范围列表,查询承办人列表  从新增表：t_rm_risk_org_temp表中获取
	 * @author Jzq
	 * @time 2017年5月8日13:44:30
	 * @param query		搜索框关键字
	 * @param planId	计划id
	 * @return 
	 */
	public List<Map<String, String>> findscoreDeptAndEmpGridByPlanIdSQL(String query, String planId) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, String>> mapsList = new ArrayList<Map<String, String>>();
		sql.append(" SELECT o.ID,o.ORG_NAME, COUNT(o.ORG_NAME),c.UNDERTAKER,emp.EMP_NAME,a.id   manager_id,a.emp_name   manager_name,obj.risk_id ");
		sql.append(" FROM t_rm_risk_org_temp sd ");
		sql.append(" LEFT JOIN t_sys_organization o ON o.id  = sd.ORG_ID ");
		sql.append(" LEFT JOIN t_rm_risk_circusee c ON c.ASSESS_PLAN_ID = :planId AND c.ORG_ID = o.ID ");
		sql.append(" LEFT JOIN t_sys_employee emp ON emp.id = c.UNDERTAKER ");
		sql.append(" LEFT JOIN ( ");
		sql.append(" SELECT e.id,e.emp_name,eo.org_id FROM t_sys_user_role ur ");
		sql.append(" LEFT JOIN t_sys_employee e ON e.user_id = ur.user_id  ");
		sql.append(" LEFT JOIN  t_sys_emp_org eo ON eo.emp_id = e.id  ");
		sql.append(" WHERE ur.role_id = ( ");
		sql.append(" SELECT ID FROM t_sys_role WHERE role_code = 'DeptRiskManager') ");
		sql.append(" ) a ON a.org_id = sd.ORG_ID ");
		sql.append(" LEFT JOIN t_rm_risk_score_object obj on sd.object_id = obj.id ");
		sql.append(" WHERE obj.assess_plan_id =:planId ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sql.append(" and (o.ORG_NAME like :likeQuery or emp.EMP_NAME like :likeQuery or a.emp_name like :likeQuery ) ");
			}
		}
		sql.append(" GROUP BY o.ORG_NAME ");
		
		System.out.println("@@@@@@@@@@@@ 根据计划id查询打分范围列表,查询承办人列表sql：="+sql.toString());
		
		SQLQuery sqlQuery = o_riskScoreDeptDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String orgId = "";
			String orgName = "";
			String countStr = "0";
			String empId = "";
			String empName = "";
			String cbrName = "";
            if(null != o[0]){
            	orgId = o[0].toString();
            }if(null != o[1]){
            	orgName = o[1].toString();
            }if(null != o[2] && null != o[7]){
            	countStr = o[2].toString();
            }
            if(null != o[3]){
            	empId = o[3].toString();//保存的承办人id
            	if(null != o[4]){
            		empName = o[4].toString();//保存的承办人名称
            		cbrName = o[4].toString();
            	}
            }else{
            	if(null != o[5]){
            		empId = o[5].toString();//部门风险管理员id
            		if(null != o[6]){
            			empName = o[6].toString();//部门风险管理员名称
                	}
            	}
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", orgId);
            map.put("planId", planId);
			map.put("deptName", orgName);
			map.put("riskCournts", countStr);
			map.put("empId", empId);
			map.put("empName", empName);
			map.put("cbrName", cbrName);
			mapsList.add(map);
		}
        return mapsList;
	}
	
	/*public List<Map<String, String>> findscoreDeptAndEmpGridByPlanIdSQL_bak(String query, String planId) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, String>> mapsList = new ArrayList<Map<String, String>>();
		sql.append(" SELECT o.ID,o.ORG_NAME, COUNT(o.ORG_NAME),c.UNDERTAKER,emp.EMP_NAME,a.id   manager_id,a.emp_name   manager_name,obj.risk_id ");
		sql.append(" FROM t_rm_risk_score_dept sd ");
		sql.append(" LEFT JOIN t_sys_organization o ON o.id  = sd.ORG_ID ");
		sql.append(" LEFT JOIN t_rm_risk_circusee c ON c.ASSESS_PLAN_ID = :planId AND c.ORG_ID = o.ID ");
		sql.append(" LEFT JOIN t_sys_employee emp ON emp.id = c.UNDERTAKER ");
		sql.append(" LEFT JOIN ( ");
		sql.append(" SELECT e.id,e.emp_name,eo.org_id FROM t_sys_user_role ur ");
		sql.append(" LEFT JOIN t_sys_employee e ON e.user_id = ur.user_id  ");
		sql.append(" LEFT JOIN  t_sys_emp_org eo ON eo.emp_id = e.id  ");
		sql.append(" WHERE ur.role_id = ( ");
		sql.append(" SELECT ID FROM t_sys_role WHERE role_code = 'DeptRiskManager') ");
		sql.append(" ) a ON a.org_id = sd.ORG_ID ");
		sql.append(" LEFT JOIN t_rm_risk_score_object obj on sd.score_object_id = obj.id ");
		sql.append(" WHERE obj.assess_plan_id =:planId ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sql.append(" and (o.ORG_NAME like :likeQuery or emp.EMP_NAME like :likeQuery or a.emp_name like :likeQuery ) ");
			}
		}
		sql.append(" GROUP BY o.ORG_NAME ");
		
		System.out.println("@@@@@@@@@@@@ 根据计划id查询打分范围列表,查询承办人列表sql：="+sql.toString());
		
		SQLQuery sqlQuery = o_riskScoreDeptDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String orgId = "";
			String orgName = "";
			String countStr = "0";
			String empId = "";
			String empName = "";
			String cbrName = "";
            if(null != o[0]){
            	orgId = o[0].toString();
            }if(null != o[1]){
            	orgName = o[1].toString();
            }if(null != o[2] && null != o[7]){
            	countStr = o[2].toString();
            }
            if(null != o[3]){
            	empId = o[3].toString();//保存的承办人id
            	if(null != o[4]){
            		empName = o[4].toString();//保存的承办人名称
            		cbrName = o[4].toString();
            	}
            }else{
            	if(null != o[5]){
            		empId = o[5].toString();//部门风险管理员id
            		if(null != o[6]){
            			empName = o[6].toString();//部门风险管理员名称
                	}
            	}
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", orgId);
            map.put("planId", planId);
			map.put("deptName", orgName);
			map.put("riskCournts", countStr);
			map.put("empId", empId);
			map.put("empName", empName);
			map.put("cbrName", cbrName);
			mapsList.add(map);
		}
        return mapsList;
	}*/
	
	/**部门风险评估和风险辨识获取承办人列表只获取主责部门的
	 * @param query
	 * @param planId
	 * @return
	 */
	public List<Map<String, String>> findscoreDeptAndEmpGridByPlanIdSQL_Dept(String query, String planId) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, String>> mapsList = new ArrayList<Map<String, String>>();
		sql.append(" SELECT o.ID,o.ORG_NAME, COUNT(o.ORG_NAME),c.UNDERTAKER,emp.EMP_NAME,a.id   manager_id,a.emp_name   manager_name,obj.risk_id ");
		sql.append(" FROM t_rm_risk_org_temp sd ");
		sql.append(" LEFT JOIN t_sys_organization o ON o.id  = sd.ORG_ID ");
		sql.append(" LEFT JOIN t_rm_risk_circusee c ON c.ASSESS_PLAN_ID = :planId AND c.ORG_ID = o.ID ");
		sql.append(" LEFT JOIN t_sys_employee emp ON emp.id = c.UNDERTAKER ");
		sql.append(" LEFT JOIN ( ");
		sql.append(" SELECT e.id,e.emp_name,eo.org_id FROM t_sys_user_role ur ");
		sql.append(" LEFT JOIN t_sys_employee e ON e.user_id = ur.user_id  ");
		sql.append(" LEFT JOIN  t_sys_emp_org eo ON eo.emp_id = e.id  ");
		sql.append(" WHERE ur.role_id = ( ");
		sql.append(" SELECT ID FROM t_sys_role WHERE role_code = 'DeptRiskManager') ");
		sql.append(" ) a ON a.org_id = sd.ORG_ID ");
		sql.append(" LEFT JOIN t_rm_risk_score_object obj on sd.object_id = obj.id ");
		sql.append(" WHERE obj.assess_plan_id =:planId and sd.ETYPE='M' ");
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sql.append(" and (o.ORG_NAME like :likeQuery or emp.EMP_NAME like :likeQuery or a.emp_name like :likeQuery ) ");
			}
		}
		sql.append(" GROUP BY o.ORG_NAME ");
		
		System.out.println("@@@@@@@@@@@@ 根据计划id查询打分范围列表,查询承办人列表sql：="+sql.toString());
		
		SQLQuery sqlQuery = o_riskScoreDeptDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String orgId = "";
			String orgName = "";
			String countStr = "0";
			String empId = "";
			String empName = "";
			String cbrName = "";
            if(null != o[0]){
            	orgId = o[0].toString();
            }if(null != o[1]){
            	orgName = o[1].toString();
            }if(null != o[2] && null != o[7]){
            	countStr = o[2].toString();
            }
            if(null != o[3]){
            	empId = o[3].toString();//保存的承办人id
            	if(null != o[4]){
            		empName = o[4].toString();//保存的承办人名称
            		cbrName = o[4].toString();
            	}
            }else{
            	if(null != o[5]){
            		empId = o[5].toString();//部门风险管理员id
            		if(null != o[6]){
            			empName = o[6].toString();//部门风险管理员名称
                	}
            	}
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", orgId);
            map.put("planId", planId);
			map.put("deptName", orgName);
			map.put("riskCournts", countStr);
			map.put("empId", empId);
			map.put("empName", empName);
			map.put("cbrName", cbrName);
			mapsList.add(map);
		}
        return mapsList;
	}
	
	
	
	/**
	 * 根据角色编号查用户集合 
	 * @param rolecode	角色编号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<SysUser> fingUsersByRoleCode(String rolecode){
		Set<SysUser> users = new HashSet<SysUser>();
		Criteria c = o_sysoRoleDAO.createCriteria();
		if (StringUtils.isNotBlank(rolecode)) {
			c.add(Restrictions.eq("roleCode", rolecode));
		} 
		List<SysRole> list = c.list();
		if(list.size()>0){
			SysRole role = list.get(0);
			users = role.getSysUsers();
		}
		return users;
	}
	/**
	 * 根据计划id查询部门领导（没有用承办人代替）
	 * @param planId
	 * @return
	 */
	public SysEmployee findDeptLeaderOrCbr(String planId, String deptId) {
		SysEmployee deptLeader = this.findEmpsByRoleIdAnddeptId(deptId,"RiskAssessDeptLeader");
		if(null == deptLeader){
			List<RiskCircusee> cirlist = o_circuseeBO.findRiskCircuseeByplanId(planId);
			for(RiskCircusee circuess : cirlist){
				if(deptId.equals(circuess.getOrganization().getId())){
					deptLeader = circuess.getUnderTaker();//该部门承办人
				}
			}
		}
		return deptLeader;
	}
	
	/**
	 * 更新计划状态
	 * @param planId	计划id
	 */
	public void mergeAssessPlanStatusByPlanId(String planId, String statusStr) {
		RiskAssessPlan assessPlan = o_assessPlanBO.findRiskAssessPlanById(planId);
		if(StringUtils.isNotBlank(statusStr)){
			assessPlan.setDealStatus(statusStr);
		}else{
			assessPlan.setDealStatus(Contents.DEAL_STATUS_FINISHED);//点结束工作流按钮，处理状态为"已完成"
		}
		o_planBO.mergeRiskAssessPlan(assessPlan);
	}
	/**
	 * 保存计划审批人承办人
	 * add by 王再冉
	 * 2014-3-13  下午2:02:42
	 * desc : 
	 * @param deptEmpId	部门人员id
	 * @param approver	审批人id
	 * @param assessPlan计划实体
	 * @param businessId计划id
	 * void
	 */
	public void saveRiskCircuseeBysome(String deptEmpId, String approver, RiskAssessPlan assessPlan, String businessId){
		o_circuseeBO.removeRiskCircuseesByPlanId(assessPlan.getId());//删除计划下所有保存的审批人承办人数据
		List<RiskCircusee> saveRiskCircusees = new ArrayList<RiskCircusee>();
		JSONArray deptempArray=JSONArray.fromObject(deptEmpId);
		int j = deptempArray.size();
		for(int i=0;i<j;i++){
			JSONObject jsonObj = deptempArray.getJSONObject(i);
			String deptId = jsonObj.getString("id");//部门id
			String empId = jsonObj.getString("empId");//承办人id
			
			RiskCircusee circusee = new RiskCircusee();
			circusee.setId(Identities.uuid());
			circusee.setApprover(new SysEmployee(approver));
			circusee.setAssessPlan(assessPlan);
			circusee.setOrganization(new SysOrganization(deptId));
			circusee.setUnderTaker(new SysEmployee(empId));
			saveRiskCircusees.add(circusee);
		}
		o_circuseeBO.saveRiskCircuseesSql(saveRiskCircusees);
	}
	
	/**
	 * 根据风险id集合查询风险关联部门信息，isMain为true：只查询主部门（type为‘M’）
	 * add by 王再冉
	 * 2014-3-13  下午2:04:57
	 * desc : 
	 * @param riskIds	风险id
	 * @param isMain	是否主部门
	 * @param deptId	部门id
	 * @return 
	 * ArrayList<HashMap<String,String>>
	 */
	public ArrayList<HashMap<String, String>> findRiskOrgsByriskIds(List<String> riskIds, Boolean isMain,String deptId) {
		StringBuffer sql = new StringBuffer();
		List<String> savedRiskIdList = new ArrayList<String>();
		ArrayList<HashMap<String, String>> mapsList = new ArrayList<HashMap<String, String>>();
		sql.append(" SELECT distinct ro.ETYPE,ro.ORG_ID,r.ID   RISK_ID, ro.EMP_ID ");
		sql.append(" FROM t_rm_risk_org ro ");
		sql.append(" LEFT JOIN t_rm_risks r ON r.ID = ro.RISK_ID ");
		sql.append(" WHERE ro.RISK_ID IN (:riskIds) AND r.DELETE_ESTATUS = '1' AND r.IS_RISK_CLASS = 're'");
		if(isMain){
			sql.append(" AND ro.ETYPE = 'M' ");
		}
		SQLQuery sqlQuery = o_riskOrgDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameterList("riskIds", riskIds);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String eType = "";
			String orgId = "";
            String riskId = "";
            String empId = "";
            if(null != o[0]){
            	eType = o[0].toString();
            }if(null != o[1]){
            	orgId = o[1].toString();
            }if(null != o[2]){
            	riskId = o[2].toString();
            }
            if(null != o[3]){
            	empId = o[3].toString();
            }
            if(StringUtils.isNotBlank(deptId)){//参与部门
            	eType = "C";
            	orgId = deptId;
            	if(!savedRiskIdList.contains(riskId)){
            		HashMap<String, String> map = new HashMap<String, String>();
        			map.put("riskId", riskId);
        			map.put("eType", eType);
        			map.put("orgId", orgId);
        			map.put("mepId", empId);
        			mapsList.add(map);
        			savedRiskIdList.add(riskId);
            	}
            }else{
            	HashMap<String, String> map = new HashMap<String, String>();
    			map.put("riskId", riskId);
    			map.put("eType", eType);
    			map.put("orgId", orgId);
    			map.put("mepId", empId);
    			mapsList.add(map);
            }
		}
        return mapsList;
	}
	/**
	 * 查询全部/该部门打分对象明细
	 * add by 王再冉
	 * 2014-3-5  下午3:58:44
	 * desc : 
	 * @param query		查询关键字
	 * @param planId	计划id
	 * @param deptId	部门id
	 * @param countMap	部门对应个数map
	 * @return 
	 * List<Map<String,String>>
	 */
	public List<Map<String, String>> findInfoByplanIdAndDeptId(String query, String planId,
												String deptId,Map<String,String> countMap) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, String>> mapsList = new ArrayList<Map<String, String>>();
		sql.append(" SELECT ob.risk_id,ob.risk_name,pr.risk_name  pname,org.org_name,d.etype as org_type,ob.id  objid,d.id  scoredeptid,d.org_id ");
		sql.append(" FROM t_rm_risk_score_object ob ");
		sql.append(" LEFT JOIN t_rm_risk_org_temp d ON d.OBJECT_ID = ob.id ");
//		sql.append(" LEFT JOIN t_rm_risks r ON r.ID = ob.risk_id ");
		sql.append(" LEFT JOIN t_rm_risks pr ON pr.ID = ob.PARENT_ID ");
		sql.append(" LEFT JOIN t_sys_organization org ON org.id = d.org_id ");
		sql.append(" WHERE ob.assess_plan_id = :planId ");
		if(StringUtils.isNotBlank(deptId)){
			sql.append(" AND d.org_id = :deptId ");
		}
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sql.append(" and (ob.risk_name like :likeQuery or pr.risk_name like :likeQuery or org.org_name like :likeQuery ) ");
			}
		}
		sql.append(" ORDER BY d.org_id ");
		SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        if(StringUtils.isNotBlank(deptId)){
        	sqlQuery.setParameter("deptId", deptId);
		}
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String countStr = "0";
			String riskId = "";
			String riskName = "";
			String pRiskName = "";
			String orgName = "";
			String orgType = "";
			String objId = "";
			String scoreDeptId = "";
			String orgId = "";
            if(null != o[0]){
            	riskId = o[0].toString();//风险id
            }if(null != o[1]){
            	riskName = o[1].toString();//风险名称
            }if(null != o[2]){
            	pRiskName = o[2].toString();//上级风险名称
            }if(null != o[3]){
            	orgName = o[3].toString();//部门名称
            }if(null != o[4]){
            	orgType = o[4].toString();//部门类型
            }if(null != o[5]){
            	objId = o[5].toString();//打分部门id
            }if(null != o[6]){
            	scoreDeptId = o[6].toString();//打分部门id
            }if(null != o[7]){
            	orgId = o[7].toString();//部门id
            }
            if(null != countMap.get(orgId) && StringUtils.isNotBlank(riskId)){
            	countStr = countMap.get(orgId);//该部门打分对象个数
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", objId + ";" + scoreDeptId);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("parentRiskName", pRiskName);
			map.put("deptName", orgName + "("+countStr+")");
			map.put("deptType", orgType);
			map.put("scoreObjId", objId);
			map.put("scoreDeptId", scoreDeptId);
			mapsList.add(map);
		}
        return mapsList;
	}
	/**
	 * 查询计划下每个部门打分对象的个数，返回map    吉志强     2017年5月8日14:28:21
	 * add by 王再冉
	 * 2014-3-5  下午3:50:57
	 * desc : 
	 * @param planId	计划id
	 */
	public Map<String, String> getOrgCountByPlanId(String planId) {
		StringBuffer sql = new StringBuffer();
		Map<String, String> map = new HashMap<String, String>();
		sql.append(" SELECT d.org_id,COUNT(*) FROM t_rm_risk_score_object o ");
		sql.append(" LEFT JOIN t_rm_risk_org_temp d ON d.object_id = o.id ");
		sql.append(" WHERE assess_plan_id = :planId ");
		sql.append(" GROUP BY d.org_id ");
		SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String orgId = "";
			String countStr = "";
            if(null != o[0]){
            	orgId = o[0].toString();
            }if(null != o[1]){
            	countStr = o[1].toString();
            }
            map.put(orgId,countStr);
		}
        return map;
	}
	
	
	
	
	
	

	/** 吉志强bak 2017年5月8日14:25:04
	 * @param query
	 * @param planId
	 * @param deptId
	 * @param countMap
	 * @return
	 */
	public List<Map<String, String>> findInfoByplanIdAndDeptId_bak(String query, String planId,
												String deptId,Map<String,String> countMap) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, String>> mapsList = new ArrayList<Map<String, String>>();
		sql.append(" SELECT ob.risk_id,ob.risk_name,pr.risk_name  pname,org.org_name,d.org_type,ob.id  objid,d.id  scoredeptid,d.org_id ");
		sql.append(" FROM t_rm_risk_score_object ob ");
		sql.append(" LEFT JOIN t_rm_risk_score_dept d ON d.SCORE_OBJECT_ID = ob.id ");
		sql.append(" LEFT JOIN t_rm_risks r ON r.ID = ob.risk_id ");
		sql.append(" LEFT JOIN t_rm_risks pr ON pr.ID = r.PARENT_ID ");
		sql.append(" LEFT JOIN t_sys_organization org ON org.id = d.org_id ");
		sql.append(" WHERE assess_plan_id = :planId ");
		if(StringUtils.isNotBlank(deptId)){
			sql.append(" AND d.org_id = :deptId ");
		}
		if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sql.append(" and (r.risk_name like :likeQuery or pr.risk_name like :likeQuery or org.org_name like :likeQuery ) ");
			}
		}
		sql.append(" ORDER BY d.org_id ");
		SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        if(StringUtils.isNotBlank(deptId)){
        	sqlQuery.setParameter("deptId", deptId);
		}
        if(StringUtils.isNotBlank(query)){
			if(!query.matches("\\d*")){//判断查询条件是否为数字
				sqlQuery.setParameter("likeQuery", "%"+query+"%");
			}
		}
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String countStr = "0";
			String riskId = "";
			String riskName = "";
			String pRiskName = "";
			String orgName = "";
			String orgType = "";
			String objId = "";
			String scoreDeptId = "";
			String orgId = "";
            if(null != o[0]){
            	riskId = o[0].toString();//风险id
            }if(null != o[1]){
            	riskName = o[1].toString();//风险名称
            }if(null != o[2]){
            	pRiskName = o[2].toString();//上级风险名称
            }if(null != o[3]){
            	orgName = o[3].toString();//部门名称
            }if(null != o[4]){
            	orgType = o[4].toString();//部门类型
            }if(null != o[5]){
            	objId = o[5].toString();//打分部门id
            }if(null != o[6]){
            	scoreDeptId = o[6].toString();//打分部门id
            }if(null != o[7]){
            	orgId = o[7].toString();//部门id
            }
            if(null != countMap.get(orgId) && StringUtils.isNotBlank(riskId)){
            	countStr = countMap.get(orgId);//该部门打分对象个数
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", objId + ";" + scoreDeptId);
            map.put("riskId", riskId);
            map.put("riskName", riskName);
            map.put("parentRiskName", pRiskName);
			map.put("deptName", orgName + "("+countStr+")");
			map.put("deptType", orgType);
			map.put("scoreObjId", objId);
			map.put("scoreDeptId", scoreDeptId);
			mapsList.add(map);
		}
        return mapsList;
	}
	
	/**吉志强bak 2017年5月8日14:25:04
	 * @param planId
	 * @return
	 */
	public Map<String, String> getOrgCountByPlanId_bak(String planId) {
		StringBuffer sql = new StringBuffer();
		Map<String, String> map = new HashMap<String, String>();
		sql.append(" SELECT d.org_id,COUNT(*) FROM t_rm_risk_score_object o ");
		sql.append(" LEFT JOIN t_rm_risk_score_dept d ON d.score_object_id = o.id ");
		sql.append(" WHERE assess_plan_id = :planId ");
		sql.append(" GROUP BY d.org_id ");
		SQLQuery sqlQuery = o_riskScoreObjDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("planId", planId);
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String orgId = "";
			String countStr = "";
            if(null != o[0]){
            	orgId = o[0].toString();
            }if(null != o[1]){
            	countStr = o[1].toString();
            }
            map.put(orgId,countStr);
		}
        return map;
	}
}