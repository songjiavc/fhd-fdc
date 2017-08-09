package com.fhd.entity.risk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 历史事件关联表实体
 * 
 * @author 张健
 * @date 2013-11-6
 * @since Ver 1.1
 */
@Entity
@Table(name = "T_HISTORY_EVENT_RELA_OTHERS") 
public class HistoryEventRelaOthers extends IdEntity implements Serializable{
    private static final long serialVersionUID = 1831605195472471583L;
    
    /**
     * 关联类型,风险：risk，指标：kpi，记分卡：sc，流程：process
     */
    @Column(name = "ETYPE")
    private String type;
    
    /**
     * 关联历史事件
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private HistoryEvent historyEvent;
    
    /**
     * 关联对象实体ID
     */
    @Column(name = "RELATION_ID")
    private String relation;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HistoryEvent getHistoryEvent() {
        return historyEvent;
    }

    public void setHistoryEvent(HistoryEvent historyEvent) {
        this.historyEvent = historyEvent;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
    
}

