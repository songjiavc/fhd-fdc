package com.fhd.ra.web.form.assess.formulateplan;

import java.util.ArrayList;
import java.util.List;

import com.fhd.entity.assess.formulatePlan.RiskOrgTemp;
import com.fhd.entity.assess.formulatePlan.RiskScoreDept;
import com.fhd.entity.assess.formulatePlan.RiskScoreObject;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.sys.orgstructure.SysEmployee;
/**
 * 打分对象Form
 * @author 王再冉
 *
 */
public class RiskScoreObjectForm extends RiskScoreObject{
	private static final long serialVersionUID = 1L;
	private String riskName;//风险名称
	private String riskId;//风险id
	private String parentRiskName;//上级风险
	private String mainOrgName;//责任部门
	private String relaOrgName;//相关部门
	private String joinOrgName;//参与部门
	private String planId;
	private String empId;
	private String deptName;//部门名称
	private String deptType;//部门责任类型
	private String addsid;//复选框值
	private String scoreDeptId;//打分部门id
	private String scoreObjId;//打分对象id
	private String empName;
	
	public RiskScoreObjectForm(){
		
	}
	
	public RiskScoreObjectForm(RiskScoreObject scoreObj, List<RiskOrgTemp> scoreDepts, SysEmployee riskLiaisons){
		this.setId(scoreObj.getId());
		if(null != scoreObj.getRisk()){
			this.setRiskId(scoreObj.getRisk().getId());
			this.setRiskName(scoreObj.getRisk().getName());
			if(null != scoreObj.getRisk().getParent()){//上级风险
				this.setParentRiskName(scoreObj.getRisk().getParent().getName());
			}else{
				this.setParentRiskName("");
			}
		}else{
			this.setRiskId("");
			this.setRiskName("");
			this.setParentRiskName("");
		}
		this.setPlanId(scoreObj.getAssessPlan().getId());
		for(RiskOrgTemp dept : scoreDepts){
			if(null != dept.getType()){
				if(dept.getType().equals("M")){//责任部门
					if(null != this.getMainOrgName()){
						this.setMainOrgName(this.getMainOrgName()+ "," + dept.getSysOrganization().getOrgname());
					}else{
						this.setMainOrgName(dept.getSysOrganization().getOrgname());
					}
				}else if(dept.getType().equals("A")){//相关部门
					if(null != this.getRelaOrgName()){
						this.setRelaOrgName(this.getRelaOrgName() + "," + dept.getSysOrganization().getOrgname());
					}else{
						this.setRelaOrgName(dept.getSysOrganization().getOrgname());
					}
				}
			}
		}
		this.setEmpId(riskLiaisons.getId());
	}
	
	public RiskScoreObjectForm(RiskScoreObject scoreObj, RiskScoreDept scoreDept, int riskCournts){
		this.setId(scoreObj.getId()+";"+ scoreDept.getId());//区分合并单元格
		if(null != scoreObj.getRisk()){
			this.setRiskId(scoreObj.getRisk().getId());
			this.setRiskName(scoreObj.getRisk().getName());
			if(null != scoreObj.getRisk().getParent()){//上级风险
				this.setParentRiskName(scoreObj.getRisk().getParent().getName()+" " + scoreDept.getOrganization().getId());
			}else{
				this.setParentRiskName(""+" " + scoreDept.getOrganization().getId());
			}
		}else{
			this.setRiskId("");
			this.setRiskName("");
			this.setParentRiskName(""+" " + scoreDept.getOrganization().getId());
		}
		this.setPlanId(scoreObj.getAssessPlan().getId());
		this.setDeptName(scoreDept.getOrganization().getOrgname()+ "(" + riskCournts + ")");
		this.setDeptType(scoreDept.getOrgType());
		//this.setAddsid(scoreObj.getId()+";"+ scoreDept.getId());
		this.setScoreDeptId(scoreDept.getId());
		this.setScoreObjId(scoreObj.getId());
	}
	
	public RiskScoreObjectForm(RiskScoreObject scoreObj, List<RiskOrgTemp> scoreDepts, List<RangObjectDeptEmp> rodeList){
		this.setScoreObjId(scoreObj.getId());
		this.setId(scoreObj.getId());
		if(null != scoreObj.getRiskId()){
			try {
				this.setRiskId(scoreObj.getRiskId());
				this.setRiskName(scoreObj.getName());
				if(null != scoreObj.getParent()){//上级风险
					this.setParentRiskName(scoreObj.getParentName());
				}else{
					this.setParentRiskName("");
				}
			} catch (Exception e) {
			}
		}else{
			this.setRiskId("");
			this.setRiskName("");
			this.setParentRiskName("");
		}
		this.setPlanId(scoreObj.getAssessPlan().getId());
		for(RiskOrgTemp dept : scoreDepts){
			if(null != dept.getType()){
				if(dept.getType().equals("M")){//责任部门
					if(null != this.getMainOrgName()){
						this.setMainOrgName(this.getMainOrgName()+ "," + dept.getSysOrganization().getOrgname());
					}else{
						this.setMainOrgName(dept.getSysOrganization().getOrgname());
					}
				}else if(dept.getType().equals("A")){//相关部门
					if(null != this.getRelaOrgName()){
						this.setRelaOrgName(this.getRelaOrgName() + "," + dept.getSysOrganization().getOrgname());
					}else{
						this.setRelaOrgName(dept.getSysOrganization().getOrgname());
					}
				}else if(dept.getType().equals("C")){//参与部门
					if(null != this.getJoinOrgName()){
						this.setJoinOrgName(this.getJoinOrgName() + "," + dept.getSysOrganization().getOrgname());
					}else{
						this.setJoinOrgName(dept.getSysOrganization().getOrgname());
					}
				}
			}else{
				
			}
		}
		List<String> empIdList = new ArrayList<String>();
		for(RangObjectDeptEmp rode : rodeList){
			if(rode.getScoreObject().getId().equals(scoreObj.getId())){
				//this.empId = rode.getScoreEmp().getId();
				if(null != rode.getScoreEmp()){
					if(!empIdList.contains(rode.getScoreEmp().getId())){
						empIdList.add(rode.getScoreEmp().getId());
						if(null != this.getEmpId()){
							this.setEmpId(this.getEmpId() + "," + rode.getScoreEmp().getId());
							this.setEmpName(this.getEmpName() + "," + rode.getScoreEmp().getEmpname());
						}else{
							this.setEmpId(rode.getScoreEmp().getId());
							this.setEmpName(rode.getScoreEmp().getEmpname());
						}
					}
				}
			}
		}
	}

	public String getRiskName() {
		return riskName;
	}

	public void setRiskName(String riskName) {
		this.riskName = riskName;
	}

	public String getParentRiskName() {
		return parentRiskName;
	}

	public void setParentRiskName(String parentRiskName) {
		this.parentRiskName = parentRiskName;
	}

	public String getMainOrgName() {
		return mainOrgName;
	}

	public void setMainOrgName(String mainOrgName) {
		this.mainOrgName = mainOrgName;
	}

	public String getRelaOrgName() {
		return relaOrgName;
	}

	public void setRelaOrgName(String relaOrgName) {
		this.relaOrgName = relaOrgName;
	}

	public String getJoinOrgName() {
		return joinOrgName;
	}

	public void setJoinOrgName(String joinOrgName) {
		this.joinOrgName = joinOrgName;
	}

	public String getRiskId() {
		return riskId;
	}

	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getAddsid() {
		return addsid;
	}

	public void setAddsid(String addsid) {
		this.addsid = addsid;
	}

	public String getScoreDeptId() {
		return scoreDeptId;
	}

	public void setScoreDeptId(String scoreDeptId) {
		this.scoreDeptId = scoreDeptId;
	}

	public String getScoreObjId() {
		return scoreObjId;
	}

	public void setScoreObjId(String scoreObjId) {
		this.scoreObjId = scoreObjId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}
	
	
}
