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

/**
 * 回复实体
 * */
@Entity
@Table(name = "t_sys_reply")
public class Reply extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 帖子实体
	 * */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POSTS_ID")
	private Posts posts;
	
	/**
	 * 回复内容
	 */
	@Column(name = "REPLY_CONTENT")
	private String content;
	
	/**
	 *回复时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REPLY_TIME")
	private Date createTime;
	
	/**
	 * 回复人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPLY_EMP")
	private SysEmployee createEmp;

	public Posts getPosts() {
		return posts;
	}

	public void setPosts(Posts posts) {
		this.posts = posts;
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
}