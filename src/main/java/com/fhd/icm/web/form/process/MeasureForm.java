
package com.fhd.icm.web.form.process;
import com.fhd.entity.icm.control.Measure;
/**
 * @author  宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-3-17		下午2:40:43
 *
 * @see 	 
 */
public  class MeasureForm extends Measure{
	private static final long serialVersionUID = 657313864694079311L;
	/**
	 * 风险Id
	 */
	private String riskId;
	/**
	 * 控制关联节点
	 */
	private String pointNote;
	/**
	 * 人员Id
	 */
	private String meaSureempId; 
	/**
	 * 组织机构Id
	 */
	private String meaSureorgId; 
	/**
	 * 流程节点
	 */
	private String processAndPoint; 
	
	private String meadesc;
	
	private String meacode;
	/**
	 * 评价点内容
	 * @return
	 */
	private String editGridJson;
	
	public String getPointNote() {
		return pointNote;
	}
	public void setPointNote(String pointNote) {
		this.pointNote = pointNote;
	}
	public String getMeaSureempId() {
		return meaSureempId;
	}
	public void setMeaSureempId(String meaSureempId) {
		this.meaSureempId = meaSureempId;
	}
	public String getMeaSureorgId() {
		return meaSureorgId;
	}
	public void setMeaSureorgId(String meaSureorgId) {
		this.meaSureorgId = meaSureorgId;
	}
	public String getEditGridJson() {
		return editGridJson;
	}
	public void setEditGridJson(String editGridJson) {
		this.editGridJson = editGridJson;
	}
	public String getRiskId() {
		return riskId;
	}
	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}
	public String getProcessAndPoint() {
		return processAndPoint;
	}
	public void setProcessAndPoint(String processAndPoint) {
		this.processAndPoint = processAndPoint;
	}
    public String getMeadesc() {
        return meadesc;
    }
    public void setMeadesc(String meadesc) {
        this.meadesc = meadesc;
    }
    public String getMeacode() {
        return meacode;
    }
    public void setMeacode(String meacode) {
        this.meacode = meacode;
    }
	
}

