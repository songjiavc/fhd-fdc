package com.fhd.entity.response;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.base.AuditableEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;

/**
 * 控制措施
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-9-17		下午2:23:09
 *
 * @see 	 
 */
@Entity
@Table(name="T_RM_EXECUTION")
public class SolutionExecution extends AuditableEntity implements Serializable {
	
	private static final long serialVersionUID = -7714989398654347108L;	
	/**
	 * 应对措施
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOLUTION_ID")
	private Solution solution;
	/**
	 * 实际开始时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="REAL_START_TIME", length = 7)
	private Date realStartTime;
	/**
	 * 实际结束时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="REAL_FINISH_TIME", length = 7)
	private Date realFinishTime;
	/**
	 * 实际成本
	 */
	@Column(name="REAL_COST")
	private String realCost;
	/**
	 * 实际收效
	 */
	@Column(name="REAL_INCOME")
	private String realIncome;
	/**
	 * 执行人
	 */
	@JoinColumn(name="EXECUTOR")
	@ManyToOne(fetch = FetchType.EAGER)
	private SysEmployee respnoseExecutor;
	/**
	 * 执行进度
	 */
	@Column(name="PROGRESS")
	private String progress;
	/**
	 * 执行描述
	 */
	@Column(name="EDESC")
	private String desc;
	/**
	 * 执行状态
	 */
	@Column(name="ESTATUS")
	private String status;
	
	public SolutionExecution() {
	}
	
	public SolutionExecution(String id) {
		 this.setId(id);
	}
	
	public Solution getSolution() {
		return solution;
	}
	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	public Date getRealStartTime() {
		return realStartTime;
	}
	public void setRealStartTime(Date realStartTime) {
		this.realStartTime = realStartTime;
	}
	
	public Date getRealFinishTime() {
		return realFinishTime;
	}

	public void setRealFinishTime(Date realFinishTime) {
		this.realFinishTime = realFinishTime;
	}

	public String getRealCost() {
		return realCost;
	}
	public void setRealCost(String realCost) {
		this.realCost = realCost;
	}
	public String getRealIncome() {
		return realIncome;
	}
	public void setRealIncome(String realIncome) {
		this.realIncome = realIncome;
	}
	
	public SysEmployee getRespnoseExecutor() {
		return respnoseExecutor;
	}

	public void setRespnoseExecutor(SysEmployee respnoseExecutor) {
		this.respnoseExecutor = respnoseExecutor;
	}

	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

