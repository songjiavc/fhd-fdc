package com.fhd.entity.response.major;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**  
* @ClassName: RiskResponseRiskSchemeRela  
* @Description: 方案表  与应对对象关系表
* @author Jzhq
* @date 2017年8月1日 下午3:15:37  
*    
*/
@Entity
@Table(name="t_rm_response_risk_scheme_rela")
public class RiskResponseRiskSchemeRela extends IdEntity implements Serializable{

	private static final long serialVersionUID = 7062014072455376620L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "TASK_EXECUTION_ID")
	private RiskResponseTaskExecution taskExecutionObj;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "SCHEME_ID")
	private RiskResponseScheme scheme;

	public RiskResponseTaskExecution getTaskExecutionObj() {
		return taskExecutionObj;
	}

	public void setTaskExecutionObj(RiskResponseTaskExecution taskExecutionObj) {
		this.taskExecutionObj = taskExecutionObj;
	}

	public RiskResponseScheme getScheme() {
		return scheme;
	}

	public void setScheme(RiskResponseScheme scheme) {
		this.scheme = scheme;
	}
	
	
	
}
