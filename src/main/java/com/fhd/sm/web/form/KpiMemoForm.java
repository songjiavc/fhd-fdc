package com.fhd.sm.web.form;

import java.util.List;

import com.fhd.entity.kpi.KpiMemo;

/**
 * 指标备注信息
 *
 * @author   郝静
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-7-18		上午11:16:27
 *
 * @see 	 
 */
public class KpiMemoForm extends KpiMemo
{
    
    private static final long serialVersionUID = 1L;
    
    private String id ;
    
    private String important;
    
    private String theme;
    
    private String memo;
    
    private String memoStr;
    

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImportant() {
		return important;
	}
	public void setImportant(String important) {
		this.important = important;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
	public String getMemoStr() {
		return memoStr;
	}
	public void setMemoStr(String memoStr) {
		this.memoStr = memoStr;
	}
	public KpiMemoForm()    {
    }
	
	public KpiMemoForm(KpiMemo kpiMemo){
		this.id = kpiMemo.getKpiGatherResult().getId();
		this.important = kpiMemo.getImportant();
		this.theme = kpiMemo.getTheme();
		this.memo = null==kpiMemo.getMemo()?"":kpiMemo.getMemo();
		this.memoStr = this.theme+"!@#$"+this.important+"!@#$"+this.memo;
	}
	
    public KpiMemoForm( List<Object[]> objs)    {
    
    	if(objs.size()>0){
	       	 Object[] objects = (Object[]) objs.get(0);
	       	 this.id = (String) objects[0];//指标采集结果ID
	       	 this.important = (String) objects[1];//重要性
	         this.theme = (String) objects[2];//指标名称
	         this.memo = null == objects[3] ? "" : (String) objects[3];//时间段 
	         this.memoStr = this.theme+"!@#$"+this.important+"!@#$"+this.memo;
    	}
    }
   
}