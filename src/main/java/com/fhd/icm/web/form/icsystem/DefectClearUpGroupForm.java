
package com.fhd.icm.web.form.icsystem;

/**
 * @author  宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-3-17		下午2:40:43
 *     汇总form
 * @see 	 
 */
public  class DefectClearUpGroupForm{

	/**
	 * 建设计划Id
	 */
	private String constructPlanId;
	/**
	 *  缺陷form信息json
	 */
	private String defectClearUpFormstr;
	public String getConstructPlanId() {
		return constructPlanId;
	}
	public void setConstructPlanId(String constructPlanId) {
		this.constructPlanId = constructPlanId;
	}
	public String getDefectClearUpFormstr() {
		return defectClearUpFormstr;
	}
	public void setDefectClearUpFormstr(String defectClearUpFormstr) {
		this.defectClearUpFormstr = defectClearUpFormstr;
	}
   
}

