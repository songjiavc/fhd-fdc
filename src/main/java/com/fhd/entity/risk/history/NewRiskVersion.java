package com.fhd.entity.risk.history;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.base.IdEntity;
/**
 * (新)风险版本实体类
 * Created by wzr on 2017/4/1.
 */
@Entity
@Table(name = "T_RISK_VERSION")
public class NewRiskVersion extends IdEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 版本名称
     */
    @Column(name = "NAME")
    private String versionName;

    /**
     * 创建版本人员姓名
     */
    @Column(name = "CREATE_BY")
    private String createBy;

    /**
     * 创建版本时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    /**
     * 版本库分库标识
     */
    @Column(name = "SCHM")
    private String schm;

    /**
     * 创建版本的人员部门，部门版本库使用
     */
    @Column(name = "CREATE_ORG")
    private String createOrg;

    /**
     * 版本包含的风险事件总数
     */
    @Column(name = "RISK_COUNT")
    private Integer riskCount;

    /**
     * 较上一版本增加的风险事件数量
     */
    @Column(name = "ADD_RISK")
    private Integer addRisk;

    /**
     * 较上一版本删除的风险事件数量
     */
    @Column(name = "DELETE_RISK")
    private Integer deleteRisk;

    /**
     * 版本描述
     */
    @Column(name = "EDESC")
    private String desc;
    
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSchm() {
        return schm;
    }

    public void setSchm(String schm) {
        this.schm = schm;
    }

    public String getCreateOrg() {
        return createOrg;
    }

    public void setCreateOrg(String createOrg) {
        this.createOrg = createOrg;
    }

    public Integer getRiskCount() {
        return riskCount;
    }

    public void setRiskCount(Integer riskCount) {
        this.riskCount = riskCount;
    }

    public Integer getAddRisk() {
        return addRisk;
    }

    public void setAddRisk(Integer addRisk) {
        this.addRisk = addRisk;
    }

    public Integer getDeleteRisk() {
        return deleteRisk;
    }

    public void setDeleteRisk(Integer deleteRisk) {
        this.deleteRisk = deleteRisk;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}
