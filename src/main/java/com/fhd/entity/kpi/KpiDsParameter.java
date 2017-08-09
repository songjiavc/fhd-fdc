package com.fhd.entity.kpi;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
/**
 * 本地方法参数信息
 *
 * @author   王鑫
 * @since    fhd Ver 4.5
 * @Date	 2013-7-29 
 *
 * @see 	 
 */
@Entity
@Table(name = "T_KPI_DS_PARAMETER")
public class KpiDsParameter extends IdEntity implements Serializable{

	/**
	 *  自动生成序列号
	 */
	private static final long serialVersionUID = -1046493042227715493L;
	


	/**
	 *  对象ID
	 */
	@Column(name = "OBJECT_ID")
    private String objectId;
	
	/**
	 *  值类型（实际值、目标值、评估值）
	 */
	@Column(name = "VALUE_TYPE")
	private String valueType;
	
	/**
	 *  参数名称
	 */
	@Column(name = "PARAM_NAME")
	private String paramName;
	
	/**
	 *  参数值
	 */
	@Column(name ="PARAM_VALUE")
	private String paramValue;
	
	public KpiDsParameter() {
		
	}
	
	public KpiDsParameter(String id) {
		setId(id);
	}
	
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
}
