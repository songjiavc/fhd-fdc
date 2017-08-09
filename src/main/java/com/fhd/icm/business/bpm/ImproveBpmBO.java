package com.fhd.icm.business.bpm;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

import com.fhd.comm.business.bpm.jbpm.JBPMOperate;
import com.fhd.fdc.utils.Contents;
import com.fhd.icm.business.defect.DefectBO;
import com.fhd.icm.business.defect.DefectRelaImproveBO;
import com.fhd.icm.business.rectify.ImproveBO;
import com.fhd.icm.business.rectify.ImprovePlanBO;
import com.fhd.entity.icm.defect.Defect;
import com.fhd.entity.icm.defect.DefectRelaImprove;
import com.fhd.entity.icm.rectify.Improve;
import com.fhd.entity.icm.rectify.ImprovePlan;
import com.fhd.entity.icm.rectify.ImprovePlanRelaDefect;
import com.fhd.entity.icm.rectify.ImprovePlanRelaOrg;
import com.fhd.entity.icm.rectify.ImproveRelaPlan;
import com.fhd.icm.web.controller.bpm.ImproveBpmObject;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.entity.sys.orgstructure.SysEmployee;

/**
 * 内控整改计划DAO.
 * @author 吴德福
 */
@Service
public class ImproveBpmBO {

	@Autowired
	private RoleBO o_sysRoleBO;
	
	@Autowired
	private DefectRelaImproveBO o_defectRelaImproveBO;
	
	@Autowired
	private ImprovePlanBO o_improvePlanBO;
	
	@Autowired
	private JBPMOperate o_jBPMOperate;
	
	/**
	 * 根据整改计划id查询整改计划制定审批人.
	 * @return String
	 */
	public String findRectifyEmpIdByRole(String roleKey){
		String empId = "";
		
		//配置文件读取文件路径：rectifyDraftRoleName=内控部门部长
		String roleName = ResourceBundle.getBundle("application").getString(roleKey);
		List<SysEmployee> employeeList = o_sysRoleBO.getEmpByCorpAndRole(roleName);
		if(null != employeeList && employeeList.size()>0){
			SysEmployee sysEmployee = employeeList.get(0);
			if(null != sysEmployee){
				empId = sysEmployee.getId();
			}
		}
		return empId;
	}
	
	
	/**
	 * <pre>
	 *根据整改计划id进得分发
	 * </pre>
	 * 
	 * @author 李克东
	 * @param roleKey
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public String[] findRectifyEmpIdsByRole(String roleKey){
		String[] empIds = null;
		String roleName = ResourceBundle.getBundle("application").getString(roleKey);
		List<SysEmployee> employeeList = o_sysRoleBO.getEmpByCorpAndRole(roleName);
		if(null != employeeList && employeeList.size()>0){
			Integer length=employeeList.size();
			empIds=new String[employeeList.size()];
			for(int i=0;i<length;i++){
				if(null != employeeList.get(i)){
					empIds[i]=employeeList.get(i).getId();
				}
			}
				
		}
		return empIds;
	}
	
	
	/**
	 * <pre>
	 *整改计划汇总
	 * </pre>
	 * 
	 * @author 李克东
	 * @param roleKey
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Integer findRectifyEmpIdNumByRole(String roleKey){
		String roleName = ResourceBundle.getBundle("application").getString(roleKey);
		List<SysEmployee> employeeList = o_sysRoleBO.getEmpByCorpAndRole(roleName);
		return employeeList.size();
	}
	
	/**
	 * <pre>
	 * 整改方案指定完复核人分发的流程参数中涉及的人和任务的参数
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param improveId 整改计划ID
	 * @param iCByTestingDepartmentStaffRoleKey 被测试部门人员的角色编号
	 * @param iCDepartmentMinisterRoleKey 内控部门部长的角色编号
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<ImproveBpmObject> findDistributeParameterForBpmTask(String improveId,String iCByTestingDepartmentMinisterKey, String iCDepartmentMinisterRoleKey){
		String iCDepartmentMinisterRoleKeyEmpId = findRectifyEmpIdByRole(iCDepartmentMinisterRoleKey);//内控部门部长
		String[] iCByTestingDepartmentStaffEmpIds = findRectifyEmpIdsByRole(iCByTestingDepartmentMinisterKey);//被测试部门部长
		List<ImproveBpmObject> bpmObjectList = new ArrayList<ImproveBpmObject>();
		List<DefectRelaImprove> defectRelaImproveList = o_defectRelaImproveBO.findDefectRelaImproveListBySome(null, improveId);
		List<ImproveRelaPlan> improveRelaPlanList = o_improvePlanBO.findImproveRelaPlanListBySome(improveId,null);
		if(StringUtils.isNotBlank(iCDepartmentMinisterRoleKeyEmpId) && null != iCByTestingDepartmentStaffEmpIds 
				&& iCByTestingDepartmentStaffEmpIds.length>0){
			for (DefectRelaImprove defectRelaImprove : defectRelaImproveList) {
				Defect defect = defectRelaImprove.getDefect();
				Set<ImprovePlanRelaDefect> improvePlanRelaDefectSet = defect.getImprovePlanRelaDefect();
				for (ImprovePlanRelaDefect improvePlanRelaDefect : improvePlanRelaDefectSet) {
					ImprovePlan improvePlan = improvePlanRelaDefect.getImprovePlan();
					for (ImproveRelaPlan improveRelaPlan : improveRelaPlanList) {
						if(improveRelaPlan.getImprovePlan().getId().equals(improvePlan.getId())){
							ImproveBpmObject obj = new ImproveBpmObject();
							obj.setImprovePlanRelaDefectId(improvePlanRelaDefect.getId());	//设置方案和缺陷的关联id
							obj.setApproverEmpId(iCDepartmentMinisterRoleKeyEmpId);			//是否更新手册的审核人
							Set<ImprovePlanRelaOrg> improvePlanRelaOrgSet = improvePlan.getImprovePlanRelaOrg();
							for (ImprovePlanRelaOrg improvePlanRelaOrg : improvePlanRelaOrgSet) {
								if(Contents.EMP_REVIEW_PERSON.equals(improvePlanRelaOrg.getType())){//方案复核人
									obj.setReviewerEmpId(improvePlanRelaOrg.getEmp().getId());
								}
								//方案进度填写人为最后修改方案的人
								obj.setReportorEmpId(improvePlanRelaOrg.getImprovePlan().getLastModifyBy().getId());//方案修改人
							}
							bpmObjectList.add(obj);
						}
					}
				}
			}
		}
		return bpmObjectList;
	}
	
	/**
	 * <pre>
	 * 根据流程实例ID获得类型为BpmObject的流程变量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param executionId 流程实例ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public ImproveBpmObject findBpmObjectByExecutionId(String executionId){
		return (ImproveBpmObject)o_jBPMOperate.getVariableObj(executionId, "item");
	}
	
	
	/**
	 * <pre>
	 * 工作流调用
	 * 整改计划改为已完成状态
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param improveId
	 * @since  fhd　Ver 1.1
	*/
	public void mergeImprovePlanStatus(String improveId) {
		//将整改计划的处理状态置位已完成
		ImproveBO improveBO = ContextLoader.getCurrentWebApplicationContext().getBean(ImproveBO.class);
		DefectRelaImproveBO defectRelaImproveBO = ContextLoader.getCurrentWebApplicationContext().getBean(DefectRelaImproveBO.class);
		DefectBO defectBO = ContextLoader.getCurrentWebApplicationContext().getBean(DefectBO.class);
		Improve improve = improveBO.findImproveById(improveId);
		improve.setDealStatus(Contents.DEAL_STATUS_FINISHED);
		improveBO.mergeImprove(improve);
		List<DefectRelaImprove> defectRelaImproveList = defectRelaImproveBO.findDefectRelaImproveListBySome(null, improveId);
		for (DefectRelaImprove defectRelaImprove : defectRelaImproveList) {
			Defect defect = defectRelaImprove.getDefect();
			defect.setDealStatus(Contents.DEAL_STATUS_FINISHED);
			defectBO.mergeDefect(defect);
		}
	}
	
}
