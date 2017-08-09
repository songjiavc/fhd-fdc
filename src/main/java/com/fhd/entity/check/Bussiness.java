package com.fhd.entity.check;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 针对于业务的人员机关管理关系表
 * @author 宋佳
 */
@Entity
@Table(name = "T_RM_CHECK_BUSSINESS") 
public class Bussiness extends IdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;	
	
	/** default constructor */
	public Bussiness() {
	}
	
	 /** minimal constructor */
    public Bussiness(String id)
    {
        setId(id);
    }
	
	@Column(name = "NAME")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
