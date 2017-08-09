package com.fhd.sm.web.form;

import java.text.DecimalFormat;

import com.fhd.entity.kpi.RelaAssessResult;

public class RelaAssessResultForm {

    /**
     * 时间段
     */
    private String dateRange;
    /**
     * 对象ID
     */
    private String objectId;


	/**
     * 评估值状态
     */
    private String assessmentStatusStr;

    /**
     * 趋势
     */
    private String directionstr;

    /**
     * 评估值
     */
    private String assessmentValue;

    /**
     * 评分结果表ID
     */
    private String id;
    /**
     * 时间区间维
     */
    private String timePeriod ;

    public RelaAssessResultForm(RelaAssessResult relaAssessResult) {
        if (null != relaAssessResult.getTimePeriod()) {
            this.dateRange = relaAssessResult.getTimePeriod().getTimePeriodFullName();
        }
        if (null != relaAssessResult.getAssessmentStatus()) {
            this.assessmentStatusStr = relaAssessResult.getAssessmentStatus().getValue();
        }
        if(null!=relaAssessResult.getTrend()){
            this.directionstr = relaAssessResult.getTrend().getValue();
        }
        if (null != relaAssessResult.getAssessmentValue()) {
            this.assessmentValue = new DecimalFormat("0.00").format(relaAssessResult.getAssessmentValue());
        }
        this.id = relaAssessResult.getId();
        
        this.objectId = relaAssessResult.getObjectId();
        
        this.timePeriod = relaAssessResult.getTimePeriod().getId();
        
    }
    public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
    
    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getAssessmentValue() {
        return assessmentValue;
    }

    public void setAssessmentValue(String assessmentValue) {
        this.assessmentValue = assessmentValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssessmentStatusStr() {
        return assessmentStatusStr;
    }

    public void setAssessmentStatusStr(String assessmentStatusStr) {
        this.assessmentStatusStr = assessmentStatusStr;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getDirectionstr() {
        return directionstr;
    }

    public void setDirectionstr(String directionstr) {
        this.directionstr = directionstr;
    }

}
