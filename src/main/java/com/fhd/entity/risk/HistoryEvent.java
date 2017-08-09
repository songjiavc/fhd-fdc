package com.fhd.entity.risk;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 历史事件实体
 * 
 * @author 张健
 * @date 2013-11-6
 * @since Ver 1.1
 */
@Entity
@Table(name = "T_HISTORY_EVENT") 
public class HistoryEvent extends IdEntity implements Serializable{
    private static final long serialVersionUID = 1831605195472471583L;
    
    /**
     * 名称
     */
    @Column(name = "NAME")
    private String hisname;
    
    /**
     * 编号
     */
    @Column(name = "CODE")
    private String hiscode;
    
    /**
     * 所属公司
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private SysOrganization company;
    
    /**
     * 发生事件
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "OCCUR_DATE")
    private Date occurDate;
    
    
    /**
     * 损失金额
     */
    @Column(name = "LOST_AMOUNT")
    private Double lostAmount;
    
    /**
     * 处理状态
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEAL_STATUS")
    private DictEntry dealStatus;
    
    /**
     * 状态
     */
    @Column(name = "ESTATUS")
    private String status;
    
    /**
     * 删除状态
     */
    @Column(name = "DELETE_STATUS")
    private String deleteStatus;
    
    /**
     * 责任认定
     */
    @Column(name = "EVENT_OCCURED_OBJECT")
    private String eventOccuredObject;
    
    /**
     * 责任部门
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_OCCURED_ORG")
    private SysOrganization eventOccuredOrg;
    
    /**
     * 影响
     */
    @Column(name = "EFFECT")
    private String effect;
    
    /**
     * 发生地点
     */
    @Column(name = "OCCURE_PLACE")
    private String occurePlace;
    
    /**
     * 事件等级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_LEVEL")
    private DictEntry eventLevel;
    
    /**
     * 备注
     */
    @Column(name = "ECOMMENT")
    private String comment;
    
    /**
     * 描述
     */
    @Column(name = "EDESC")
    private String hisdesc;
    
    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATE_BY")
    private SysEmployee createBy;
    
    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;
    
    /**
     * 最后修改时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_MODIFY_TIME")
    private Date lastModifyTime;
    
    /**
     * 分库使用
     */
    @Column(name = "SCHM")
    private String schm;
    
    @Column(name = "create_org")
    private String createOrg;
    
    /**
     * 历史记录关联表
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "historyEvent")
    private Set<HistoryEventRelaOthers> historyEventRelaOthers = new HashSet<HistoryEventRelaOthers>(0);
    
    public SysOrganization getEventOccuredOrg() {
        return eventOccuredOrg;
    }

    public void setEventOccuredOrg(SysOrganization eventOccuredOrg) {
        this.eventOccuredOrg = eventOccuredOrg;
    }

    public SysOrganization getCompany() {
        return company;
    }

    public void setCompany(SysOrganization company) {
        this.company = company;
    }

    public Date getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(Date occurDate) {
        this.occurDate = occurDate;
    }

    public Double getLostAmount() {
        return lostAmount;
    }

    public void setLostAmount(Double lostAmount) {
        this.lostAmount = lostAmount;
    }

    public DictEntry getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(DictEntry dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getEventOccuredObject() {
        return eventOccuredObject;
    }

    public void setEventOccuredObject(String eventOccuredObject) {
        this.eventOccuredObject = eventOccuredObject;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getOccurePlace() {
        return occurePlace;
    }

    public void setOccurePlace(String occurePlace) {
        this.occurePlace = occurePlace;
    }

    public DictEntry getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel(DictEntry eventLevel) {
        this.eventLevel = eventLevel;
    }

    public Set<HistoryEventRelaOthers> getHistoryEventRelaOthers() {
        return historyEventRelaOthers;
    }

    public void setHistoryEventRelaOthers(Set<HistoryEventRelaOthers> historyEventRelaOthers) {
        this.historyEventRelaOthers = historyEventRelaOthers;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHisname() {
        return hisname;
    }

    public void setHisname(String hisname) {
        this.hisname = hisname;
    }

    public String getHiscode() {
        return hiscode;
    }

    public void setHiscode(String hiscode) {
        this.hiscode = hiscode;
    }

    public String getHisdesc() {
        return hisdesc;
    }

    public void setHisdesc(String hisdesc) {
        this.hisdesc = hisdesc;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public SysEmployee getCreateBy() {
        return createBy;
    }

    public void setCreateBy(SysEmployee createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
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
    
    

}

