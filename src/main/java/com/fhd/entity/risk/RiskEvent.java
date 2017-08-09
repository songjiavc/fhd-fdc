package com.fhd.entity.risk;

import java.math.BigDecimal;
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

@Entity
@Table(name = "T_RM_EVENT")
public class RiskEvent extends IdEntity implements java.io.Serializable {

	private static final long serialVersionUID = -2715166040530468157L;
	
	/**
	 * 风险
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RISK_ID")
	private Risk risk;
	
	/**
	 * 历史事件编号
	 */
	@Column(name = "EVENT_CODE")
	private String eventCode;
	
	/**
	 * 历史事件类型
	 */
	@Column(name = "ETYPE", length = 100)
	private String etype;
	
	/**
	 * 动因
	 */
	@Column(name = "CAUSE", length = 65535)
	private String cause;
	
	/**
	 * 发生日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "OCCUR_DATE", length = 0)
	private Date occurDate;
	
	/**
	 * 处理状态
	 */
	@Column(name = "DEAL_STATUS", length = 100)
	private String dealStatus;
	
	/**
	 * 状态
	 */
	@Column(name = "ESTATUS", length = 100)
	private String estatus;
	
	/**
	 * 备注
	 */
	@Column(name = "ECOMMENT", length = 65535)
	private String ecomment;
	
	/**
     * 公司ID
     */
    @Column(name = "COMPANY_ID", length = 100)
    private String companyId;
    
    /**
     * 人员伤亡数-死亡
     */
    @Column(name = "EMP_DEAD_NUM")
    private Integer empDeadNum;
    
    /**
     * 人员伤亡数-轻伤
     */
    @Column(name = "EMP_LIGHT_HURT_NUM")
    private Integer empLightHurtNum;
    
    /**
     * 人员伤亡数-重伤
     */
    @Column(name = "EMP_SERIOUSLY_HURT_NUM")
    private Integer empSeriouslyHurtNum;
    
    /**
     * 事件前状态
     */
    @Column(name = "EVENT_OCCURED_BEFORE_STATUS", length = 100)
    private String eventOccuredBeforeStatus;
    
    /**
     * 责任认定
     */
    @Column(name = "EVENT_OCCURED_OBJECT", length = 65535)
    private String eventOccuredObject;
    
    /**
     * 事件发生原因分析
     */
    @Column(name = "EVENT_OCCURED_REASON", length = 65535)
    private String eventOccuredReason;
    
    /**
     * 事件发生过程
     */
    @Column(name = "EVENT_OCCURED_STORY", length = 65535)
    private String eventOccuredStory;
    
    /**
     * 财务损失金额
     */
    @Column(name = "FINANCE_LOST_AMOUNT", precision = 23, scale = 4)
    private BigDecimal financeLostAmount;
    
    /**
     * 财务损失说明
     */
    @Column(name = "FINANCE_LOST_DESC", length = 65535)
    private String financeLostDesc;
    
    /**
     * 运营影响
     */
    @Column(name = "OPERATION_EFFECT", length = 65535)
    private String operationEffect;
    
    /**
     * 设备影响
     */
    @Column(name = "EQUIPMENT_EFFECT", length = 65535)
    private String equipmentEffect;
    
    /**
     * 声誉影响
     */
    @Column(name = "REPUTATION_EFFECT", length = 65535)
    private String reputationEffect;
    
    /**
     * 上报部门
     */
    @Column(name = "REPORT_ORG_ID", length = 100)
    private String reportOrgId;
    
    /**
     * 事件名称
     */
    @Column(name = "EVENT_NAME")
    private String eventName;
    
    /**
     * 事件等级
     */
    @Column(name = "EVENT_LEVEL", length = 100)
    private String eventLevel;
    
    /**
     * 发生地点
     */
    @Column(name = "OCCURE_PLACE", length = 65535)
    private String occurePlace;
    
    /**
     * 责任主体
     */
    @Column(name = "RESPONSE_DETERMIN", length = 65535)
    private String responseDetermin;
    
	public RiskEvent() {
	}

	public RiskEvent(String id) {
		super.setId(id);
	}

	public Risk getRisk() {
		return risk;
	}

	public void setRisk(Risk risk) {
		this.risk = risk;
	}

	
	public String getEventCode() {
		return this.eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}


	public String getEtype() {
		return this.etype;
	}

	public void setEtype(String etype) {
		this.etype = etype;
	}

	public Date getOccurDate() {
		return this.occurDate;
	}

	public void setOccurDate(Date occurDate) {
		this.occurDate = occurDate;
	}

	
	public String getDealStatus() {
		return this.dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}


	public String getEstatus() {
		return this.estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}


	public String getEcomment() {
		return this.ecomment;
	}

	public void setEcomment(String ecomment) {
		this.ecomment = ecomment;
	}

	public String getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


	public Integer getEmpDeadNum() {
		return this.empDeadNum;
	}

	public void setEmpDeadNum(Integer empDeadNum) {
		this.empDeadNum = empDeadNum;
	}


	public Integer getEmpLightHurtNum() {
		return this.empLightHurtNum;
	}

	public void setEmpLightHurtNum(Integer empLightHurtNum) {
		this.empLightHurtNum = empLightHurtNum;
	}


	public Integer getEmpSeriouslyHurtNum() {
		return this.empSeriouslyHurtNum;
	}

	public void setEmpSeriouslyHurtNum(Integer empSeriouslyHurtNum) {
		this.empSeriouslyHurtNum = empSeriouslyHurtNum;
	}


	public String getEventOccuredBeforeStatus() {
		return this.eventOccuredBeforeStatus;
	}

	public void setEventOccuredBeforeStatus(String eventOccuredBeforeStatus) {
		this.eventOccuredBeforeStatus = eventOccuredBeforeStatus;
	}


	public String getEventOccuredObject() {
		return this.eventOccuredObject;
	}

	public void setEventOccuredObject(String eventOccuredObject) {
		this.eventOccuredObject = eventOccuredObject;
	}


	public String getEventOccuredReason() {
		return this.eventOccuredReason;
	}

	public void setEventOccuredReason(String eventOccuredReason) {
		this.eventOccuredReason = eventOccuredReason;
	}


	public String getEventOccuredStory() {
		return this.eventOccuredStory;
	}

	public void setEventOccuredStory(String eventOccuredStory) {
		this.eventOccuredStory = eventOccuredStory;
	}


	public String getEquipmentEffect() {
		return this.equipmentEffect;
	}

	public void setEquipmentEffect(String equipmentEffect) {
		this.equipmentEffect = equipmentEffect;
	}


	public BigDecimal getFinanceLostAmount() {
		return this.financeLostAmount;
	}

	public void setFinanceLostAmount(BigDecimal financeLostAmount) {
		this.financeLostAmount = financeLostAmount;
	}


	public String getFinanceLostDesc() {
		return this.financeLostDesc;
	}

	public void setFinanceLostDesc(String financeLostDesc) {
		this.financeLostDesc = financeLostDesc;
	}


	public String getOperationEffect() {
		return this.operationEffect;
	}

	public void setOperationEffect(String operationEffect) {
		this.operationEffect = operationEffect;
	}

	public String getReportOrgId() {
		return this.reportOrgId;
	}

	public void setReportOrgId(String reportOrgId) {
		this.reportOrgId = reportOrgId;
	}


	public String getEventName() {
		return this.eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	
	public String getEventLevel() {
		return this.eventLevel;
	}

	public void setEventLevel(String eventLevel) {
		this.eventLevel = eventLevel;
	}

	
	public String getOccurePlace() {
		return this.occurePlace;
	}

	public void setOccurePlace(String occurePlace) {
		this.occurePlace = occurePlace;
	}

	public String getResponseDetermin() {
		return this.responseDetermin;
	}

	public void setResponseDetermin(String responseDetermin) {
		this.responseDetermin = responseDetermin;
	}

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getReputationEffect() {
        return reputationEffect;
    }

    public void setReputationEffect(String reputationEffect) {
        this.reputationEffect = reputationEffect;
    }
	
}
