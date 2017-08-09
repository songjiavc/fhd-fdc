package com.fhd.sm.web.form;

import com.fhd.entity.kpi.RealTimeKpi;

public class RealTimeKpiForm {

    private String id;

    private String name;

    private String desc;

    private String status;

    private String code;
    
    private String alarmId;
    
    private String unit;
    

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public RealTimeKpiForm(RealTimeKpi realTimeKpi) {
        this.id = realTimeKpi.getId();
        this.name = realTimeKpi.getName();
        this.desc = realTimeKpi.getDesc() == null ? "" : realTimeKpi.getDesc();
        this.code = realTimeKpi.getCode() == null ? "" : realTimeKpi.getCode();
        this.alarmId = realTimeKpi.getAlarmPlan()==null?"":realTimeKpi.getAlarmPlan().getId();
        this.status = realTimeKpi.getStatus()==null?"":realTimeKpi.getStatus().getValue();
        this.unit = realTimeKpi.getUnits()==null?"":realTimeKpi.getUnits().getName();
    }

    public RealTimeKpiForm() {
    }

}
