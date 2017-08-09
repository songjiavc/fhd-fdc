package com.fhd.entity.sys.interaction;

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
 * 帖子实体
 * */

@Entity
@Table(name = "t_sys_posts")
public class Posts extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 帖子标题
	 */
	@Column(name = "TITLE")
	private String title;
	
	/**
	 * 帖子内容
	 */
	@Column(name = "CONTENT")
	private String content;
	
	/**
	 * 帖子创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME")
	private Date createTime;
	
	/**
	 * 帖子创建人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATE_EMP")
	private SysEmployee createEmp;
	
	/**
	 * 删除状态
	 */
	@Column(name = "DELETE_STATUS")
	private String deleteStatus;
	
	/**
	 * 公司id
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID")
	private SysOrganization company;
	
	public Posts(){
		
	}
	
	public Posts(String id){
		this.setId(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public SysEmployee getCreateEmp() {
		return createEmp;
	}

	public void setCreateEmp(SysEmployee createEmp) {
		this.createEmp = createEmp;
	}

	public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}
	
}
