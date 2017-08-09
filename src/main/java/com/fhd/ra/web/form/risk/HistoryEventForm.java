package com.fhd.ra.web.form.risk;

import com.fhd.entity.risk.HistoryEvent;

/**
 * 历史事件FORM
 * 
 * @author 张健
 * @date 2013-11-12
 * @since Ver 1.1
 */
public class HistoryEventForm extends HistoryEvent {
    private static final long serialVersionUID = -7566681737906911048L;
    
    private String occurDateStr = "";
    
    private String dealStatusDict = "";
    
    private String eventLevelDict = "";
    
    private String eventOccuredOrgStr = "";

    public String getOccurDateStr() {
        return occurDateStr;
    }

    public void setOccurDateStr(String occurDateStr) {
        this.occurDateStr = occurDateStr;
    }

    public String getEventOccuredOrgStr() {
        return eventOccuredOrgStr;
    }

    public void setEventOccuredOrgStr(String eventOccuredOrgStr) {
        this.eventOccuredOrgStr = eventOccuredOrgStr;
    }

    public String getDealStatusDict() {
        return dealStatusDict;
    }

    public void setDealStatusDict(String dealStatusDict) {
        this.dealStatusDict = dealStatusDict;
    }

    public String getEventLevelDict() {
        return eventLevelDict;
    }

    public void setEventLevelDict(String eventLevelDict) {
        this.eventLevelDict = eventLevelDict;
    }
    
    
}

