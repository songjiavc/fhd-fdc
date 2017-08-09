package com.fhd.ra.interfaces.assess.formulateplan;

import java.util.List;

import com.fhd.entity.assess.formulatePlan.RiskCircusee;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;

/**
 * RiskScoreBO类接口
 * @author 王再冉
 *
 */
public interface IRiskScoreBO {

	/**
	 * 查询部门id下所有部门风险实体
	 * @param orgIds
	 * @return
	 */
	public List<RiskOrg> findRiskOrgsById(String orgIds);
	
	/**
	 * 保存打分对象
	 * @param scoreObj
	 */
	public void saveRiskScoreObject(RiskScoreObject scoreObj);
	/**
	 * 保存打分部门
	 * @param scoreDept
	 */
	public void saveRiskScoreDept(RiskScoreDept scoreDept);
	
	/**
	 * 删除打分对象
	 * @param obj
	 */
	public void removeRiskScoreObj(RiskScoreObject obj);
	
	/**
	 * 删除打分部门
	 * @param deptIds
	 */
	public void removeRiskScoreDeptsByIds(List<String> deptIds);
	
	/**
	 * 删除打分对象by id
	 * @param objId
	 */
	public void removeRiskScoreObjById(String objId);
	
	/**
	 * 查找部门风险管理员
	 * @param deptId
	 * @return
	 */
	public SysEmployee findEmpsByRoleIdAnddeptId(String deptId,String roleCode);
	
	/**
	 * 登录人公司的风险管理员
	 * @return
	 */
	public SysEmployee findRiskManagerByCompanyId();
	
	/**
	 * 删除RiskCircusee集合
	 * @param cirList
	 */
	public void removeRiskCircusees(List<RiskCircusee> cirList);
	
	/**
	 * 保存riskCircusee
	 * @param cir
	 */
	public void saveRisCircusee(RiskCircusee cir);
	
	/**
	 * 查询每个部门的评估风险总数
	 * @param deptId	部门id
	 * @param planId	评估计划id
	 * @return
	 */
	public int findRiskCountsByPlanIdAndDeptID(String deptId, String planId);
	
	/**
	 * 为计划新增打分对象
	 * @param planId	计划id		
	 * @param riskId	风险id
	 * @param empId		当前登录人员id
	 */
	public void addRiskAndsaveAllBySome(String planId, String riskId, String empId, String templateId);
	
	/**
	 * 根据风险id和机构id查询关联数据
	 * @param riskId
	 * @param orgId
	 * @return
	 */
	public RiskOrg findRiskOrgTypeByriskIdAndOrgId(String riskId, String orgId);
	
	/**
	 * 保存打分部门和打分对象
	 * @param riskIds	添加的风险id
	 * @param planId	评估计划id
	 * @return
	 */
	public boolean saveScoreObjectsAndScoreDepts(String riskIds, String planId, Boolean isMain, String deptId);
	
	/**
	 * 批量保存打分对象
	 */
    public void saveRiskScoreObjectsSql(final List<RiskScoreObject> objList);
    
    /**
	 * 批量保存打分部门
	 * @param deptList
	 */
    public void saveRiskScoreDeptsSql(final List<RiskScoreDept> deptList);
    
    /**
	 * 批量删除打分部门，判断打分对象下是否存在打分部门，不存在就删除
	 * @param ids
	 * @param planId
	 * @return
	 */
	public Boolean removeScoreDeptsAndScoreObjBydeptIdsAndPlanId(String ids, String objIds);
	
	/**
	 * 批量删除打分部门
	 * @param deptids
	 */
	public void removeScoreDeptsByDeptIds(String deptids);
	
	/**
	 * 批量删除打分对象
	 * @param ids	打分对象数组
	 */
	public void removeRiskScoreObjectsByIds(String ids);
	
}
