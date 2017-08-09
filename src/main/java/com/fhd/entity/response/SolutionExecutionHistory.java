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

import com.fhd.entity.base.IdEntity;

/**
 * 应对措施执行
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-9-17		下午2:23:09
 *
 * @see 	 
 */
@Entity
@Table(name="T_RM_EXECUTION_HISTORY")
public class SolutionExecutionHistory extends IdEntity implements Serializable {
	
	private static final long serialVersionUID = -7714989398654347108L;	
	/**
	 * 应对措施
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXECUTION_ID")
	private SolutionExecution solutionExecution;
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
	
	public SolutionExecutionHistory() {
	}
	public SolutionExecutionHistory(SolutionExecutionHistory solutionExecutionHistory) {
		
	}
	
	public SolutionExecutionHistory(String id) {
		 this.setId(id);
	}
	
	public SolutionExecution getSolutionExecution() {
		return solutionExecution;
	}

	public void setSolutionExecution(SolutionExecution solutionExecution) {
		this.solutionExecution = solutionExecution;
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

