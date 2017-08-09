package com.fhd.entity.check;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

@Table(name="t_rm_check_plan_type")
@Entity
public class YearCheckPlanType extends IdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="TYPE_VALUE")
	private String typeValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	
}
