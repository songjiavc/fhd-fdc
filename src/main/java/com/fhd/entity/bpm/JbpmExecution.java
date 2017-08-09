package com.fhd.entity.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fhd.entity.bpm.view.VJbpmDeployment;

@Entity
@Table(name="JBPM4_EXECUTION")
public class JbpmExecution implements Serializable {

	/**
	 *
	 * @author 杨鹏
	 * @since  fhd　Ver 1.1
	 */
	private static final long serialVersionUID = -447795629506871836L;

	@Id
	@Column(name = "DBID_")
	private String id;
	
	@Column(name = "CLASS_")
	private String class_;
	
	@Column(name = "DBVERSION_")
	private Integer dbversion;
	
	@Column(name = "ACTIVITYNAME_")
	private String activityname;
	
	@Column(name = "HASVARS_")
	private Integer hasvars;
	
	@Column(name = "NAME_")
	private String name;
	
	@Column(name = "KEY_")
	private String key;
	
	@Column(name = "ID_")
	private String id_;
	
	@Column(name = "STATE_")
	private String state;
	
	@Column(name = "SUSPHISTSTATE_")
	private String susphiststate;
	
	@Column(name = "PRIORITY_")
	private Integer priority;
	
	@Column(name = "HISACTINST_")
	private Integer hisactinst;
	
	@Column(name = "PARENT_IDX_")
	private Long parentIDX;
	
	@Column(name = "INSTANCE_")
	private Long instance;
	
	@Column(name = "PROCDEFID_")
	private String procdefid;
	
	
	@Column(name = "SUPEREXEC_")
	private Long superexec;
	
	@Column(name = "SUBPROCINST_")
	private Long subprocinst;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_")
	private JbpmExecution jbpmExecution;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INSTANCE_",insertable=false,updatable=false)
	private JbpmHistProcinst jbpmHistProcinst;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROCDEFID_",referencedColumnName="pdid",insertable=false,updatable=false)
	private VJbpmDeployment vJbpmDeployment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBPROCINST_",insertable=false,updatable=false)
	private JbpmHistProcinst jbpmHistSubProcinst;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "jbpmExecution")
	private List<JbpmHistActinst> JbpmHistActinsts = new ArrayList<JbpmHistActinst>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClass_() {
		return class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public Integer getDbversion() {
		return dbversion;
	}

	public void setDbversion(Integer dbversion) {
		this.dbversion = dbversion;
	}

	public String getActivityname() {
		return activityname;
	}

	public void setActivityname(String activityname) {
		this.activityname = activityname;
	}

	public String getProcdefid() {
		return procdefid;
	}

	public void setProcdefid(String procdefid) {
		this.procdefid = procdefid;
	}

	public Integer getHasvars() {
		return hasvars;
	}

	public void setHasvars(Integer hasvars) {
		this.hasvars = hasvars;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getId_() {
		return id_;
	}

	public void setId_(String id_) {
		this.id_ = id_;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSusphiststate() {
		return susphiststate;
	}

	public void setSusphiststate(String susphiststate) {
		this.susphiststate = susphiststate;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getHisactinst() {
		return hisactinst;
	}

	public void setHisactinst(Integer hisactinst) {
		this.hisactinst = hisactinst;
	}

	public JbpmExecution getJbpmExecution() {
		return jbpmExecution;
	}

	public void setJbpmExecution(JbpmExecution jbpmExecution) {
		this.jbpmExecution = jbpmExecution;
	}

	public Long getInstance() {
		return instance;
	}

	public void setInstance(Long instance) {
		this.instance = instance;
	}

	public JbpmHistProcinst getJbpmHistProcinst() {
		return jbpmHistProcinst;
	}

	public void setJbpmHistProcinst(JbpmHistProcinst jbpmHistProcinst) {
		this.jbpmHistProcinst = jbpmHistProcinst;
	}

	public Long getSuperexec() {
		return superexec;
	}

	public void setSuperexec(Long superexec) {
		this.superexec = superexec;
	}

	public Long getSubprocinst() {
		return subprocinst;
	}

	public void setSubprocinst(Long subprocinst) {
		this.subprocinst = subprocinst;
	}

	public Long getParentIDX() {
		return parentIDX;
	}

	public void setParentIDX(Long parentIDX) {
		this.parentIDX = parentIDX;
	}

	public VJbpmDeployment getvJbpmDeployment() {
		return vJbpmDeployment;
	}

	public void setvJbpmDeployment(VJbpmDeployment vJbpmDeployment) {
		this.vJbpmDeployment = vJbpmDeployment;
	}

	public JbpmHistProcinst getJbpmHistSubProcinst() {
		return jbpmHistSubProcinst;
	}

	public void setJbpmHistSubProcinst(JbpmHistProcinst jbpmHistSubProcinst) {
		this.jbpmHistSubProcinst = jbpmHistSubProcinst;
	}

	public List<JbpmHistActinst> getJbpmHistActinsts() {
		return JbpmHistActinsts;
	}

	public void setJbpmHistActinsts(List<JbpmHistActinst> jbpmHistActinsts) {
		JbpmHistActinsts = jbpmHistActinsts;
	}

}
