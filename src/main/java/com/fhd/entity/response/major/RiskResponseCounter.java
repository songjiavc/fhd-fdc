package com.fhd.entity.response.major;

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
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**  
* @ClassName: RiskResponseCounter 
* @Description: 应对措施  
* @author Jzhq
* @date 2017年8月1日 下午3:15:37  
*    
*/
@Entity
@Table(name="t_rm_response_counter")
public class RiskResponseCounter extends IdEntity implements Serializable{

	private static final long serialVersionUID = -8049493991422828278L;

	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="TARGET")
	private String target;
	
	@Column(name="COMPLETE_SIGN")
	private String completeSign;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_TIME")
	private Date startTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FINISH_TIME")
	private Date finishTime;
	
	@Column(name = "TYPE")
	private String type;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CREATE_USER")
	private SysEmployee createUser;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CREATE_ORG")
	private SysOrganization createOrg;
	
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getCompleteSign() {
		return completeSign;
	}

	public void setCompleteSign(String completeSign) {
		this.completeSign = completeSign;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public SysEmployee getCreateUser() {
		return createUser;
	}

	public void setCreateUser(SysEmployee createUser) {
		this.createUser = createUser;
	}

	public SysOrganization getCreateOrg() {
		return createOrg;
	}

	public void setCreateOrg(SysOrganization createOrg) {
		this.createOrg = createOrg;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	
	
	
	
}
