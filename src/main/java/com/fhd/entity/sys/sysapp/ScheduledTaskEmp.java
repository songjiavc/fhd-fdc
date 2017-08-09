package com.fhd.entity.sys.sysapp;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;

/**
 * 计划任务--涉及人员.
 * @author 吴德福
 * @version V1.0  创建时间：2012-5-15
 * Company FirstHuiDa.
 */
@Entity
@Table(name = "T_ST_PLAN_EMP")
public class ScheduledTaskEmp extends IdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	//计划任务
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	private ScheduledTask task;
	//员工
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	private SysEmployee emp;
	
	public ScheduledTaskEmp() {
	}

	public ScheduledTaskEmp(ScheduledTask task, SysEmployee emp) {
		super();
		this.task = task;
		this.emp = emp;
	}

	public ScheduledTask getTask() {
		return task;
	}

	public void setTask(ScheduledTask task) {
		this.task = task;
	}

	public SysEmployee getEmp() {
		return emp;
	}

	public void setEmp(SysEmployee emp) {
		this.emp = emp;
	}
}
