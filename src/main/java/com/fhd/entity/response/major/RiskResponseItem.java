package com.fhd.entity.response.major;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**  
* @ClassName: RiskResponseItem  
* @Description: 风险事项表  
* @author Jzhq
* @date 2017年8月1日 下午3:15:37  
*    
*/
@Entity
@Table(name="t_rm_response_item")
public class RiskResponseItem extends IdEntity implements Serializable{

	private static final long serialVersionUID = -8049493991422828278L;

	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="FLOW")
	private String flow;
	@Column(name="REASON")
	private String reason;
	@Column(name="TYPE")
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

	public String getFlow() {
		return flow;
	}

	public void setFlow(String flow) {
		this.flow = flow;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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
