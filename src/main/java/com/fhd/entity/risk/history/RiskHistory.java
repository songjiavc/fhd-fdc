package com.fhd.entity.risk.history;

import com.fhd.entity.base.IdEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 风险版本库的风险实体
 * Created by wzr on 2017/4/1.
 */
@Entity
@Table(name = "T_RISK_HISTORY")
public class RiskHistory extends IdEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 风险所属版本
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERSION_ID")
    private NewRiskVersion versionId;

    /**
     * 风险id
     */
    @Column(name = "RISK_ID")
    private String riskId;

    /**
     * 风险编号
     */
    @Column(name = "RISK_CODE")
    private String riskCode;

    /**
     * 风险名称
     */
    @Column(name = "RISK_NAME")
    private String riskName;

    /**
     * 风险描述
     */
    @Column(name = "EDESC")
    private String riskDesc;

    /**
     * 上级风险名称
     */
    @Column(name = "PARENT_NAME")
    private String parentName;

    /**
     * id序列
     */
    @Column(name = "ID_SEQ")
    private String idSeq;

    /**
     * 责任部门/人名称
     */
    @Column(name = "MAIN_DEPT_NAME")
    private String mainDeptName;

    /**
     * 相关部门/人名称
     */
    @Column(name = "RELA_DEPT_NAME")
    private String relaDeptName;

    /**
     * 影响流程名称
     */
    @Column(name = "PROCESS_NAME")
    private String processName;

    /**
     * 影响指标
     */
    @Column(name = "KPI_NAME")
    private String kpiName;

    /**
     * 风险值
     */
    @Column(name = "SCORE")
    private Double score;

    /**
     * 是否叶子节点
     */
    @Column(name = "IS_LEAF")
    private Integer isLeaf;
    
    /**
     * 风险类型
     */
    @Column(name = "IS_RISK_CLASS")
    private String isRiskClass;
    
    /**
     * 上级风险
     */
    @Column(name = "PARENT_ID")
    private String parentId;
    
	@Transient
    private String scoreStr;
    
    @Transient
    private String verId;
    
    public NewRiskVersion getVersionId() {
        return versionId;
    }

    public void setVersionId(NewRiskVersion versionId) {
        this.versionId = versionId;
    }

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getRiskCode() {
        return riskCode;
    }

    public void setRiskCode(String riskCode) {
        this.riskCode = riskCode;
    }

    public String getRiskName() {
        return riskName;
    }

    public void setRiskName(String riskName) {
        this.riskName = riskName;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getIdSeq() {
        return idSeq;
    }

    public void setIdSeq(String idSeq) {
        this.idSeq = idSeq;
    }

    public String getMainDeptName() {
        return mainDeptName;
    }

    public void setMainDeptName(String mainDeptName) {
        this.mainDeptName = mainDeptName;
    }

    public String getRelaDeptName() {
        return relaDeptName;
    }

    public void setRelaDeptName(String relaDeptName) {
        this.relaDeptName = relaDeptName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }

	public String getIsRiskClass() {
		return isRiskClass;
	}

	public void setIsRiskClass(String isRiskClass) {
		this.isRiskClass = isRiskClass;
	}

	public String getScoreStr() {
		return scoreStr;
	}

	public void setScoreStr(String scoreStr) {
		this.scoreStr = scoreStr;
	}

	public String getVerId() {
		return verId;
	}

	public void setVerId(String verId) {
		this.verId = verId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}



}
