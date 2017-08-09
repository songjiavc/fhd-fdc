package com.fhd.ra.web.form.risk;

import com.fhd.entity.risk.ManageReport;

/**
 * 报告FORM
 * 
 * @author 张健
 * @date 2014-1-2
 * @since Ver 1.1
 */
public class ManageReportForm extends ManageReport {
    private static final long serialVersionUID = -7566681737906911048L;
    
    /**
     * 责任部门和责任人
     */
    private String respDeptName = "";
    
    /**
     * 关联风险
     */
    private String relateRisk = "";
    
    /**
     * 开始时间
     */
    private String startDateStr = "";
    
    /**
     * 结束时间
     */
    private String endDateStr = "";
    
    /**
     * 附件
     */
    private String fileIds;

    public String getRespDeptName() {
        return respDeptName;
    }

    public void setRespDeptName(String respDeptName) {
        this.respDeptName = respDeptName;
    }

    public String getRelateRisk() {
        return relateRisk;
    }

    public void setRelateRisk(String relateRisk) {
        this.relateRisk = relateRisk;
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }
    
}

