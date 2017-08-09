package com.fhd.entity.response.major;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**  
* @ClassName: RiskResponseSchemeItemRela  
* @Description: 方案与风险事项关系表
* @author Jzhq
* @date 2017年8月1日 下午3:15:37  
*    
*/
@Entity
@Table(name="t_rm_response_scheme_item_rela")
public class RiskResponseSchemeItemRela extends IdEntity implements Serializable{

	private static final long serialVersionUID = 7062014072455376620L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID")
	private RiskResponseItem item;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "SCHEME_ID")
	private RiskResponseScheme scheme;

	public RiskResponseItem getItem() {
		return item;
	}

	public void setItem(RiskResponseItem item) {
		this.item = item;
	}

	public RiskResponseScheme getScheme() {
		return scheme;
	}

	public void setScheme(RiskResponseScheme scheme) {
		this.scheme = scheme;
	}

	
	
	
	
}
