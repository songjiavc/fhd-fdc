package com.fhd.entity.response.major;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;

/**  
* @ClassName: RiskResponseItemCounterRela  
* @Description: 风险事项与应对措施关系表
* @author Jzhq
* @date 2017年8月1日 下午3:15:37  
*    
*/
@Entity
@Table(name="t_rm_response_item_counter_rela")
public class RiskResponseItemCounterRela extends IdEntity implements Serializable{

	private static final long serialVersionUID = 7062014072455376620L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID")
	private RiskResponseItem item;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "COUNTER_ID")
	private RiskResponseCounter counter;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "EMP_ID")
	private SysEmployee executionEmp;
	
	public RiskResponseItem getItem() {
		return item;
	}

	public void setItem(RiskResponseItem item) {
		this.item = item;
	}

	public RiskResponseCounter getCounter() {
		return counter;
	}

	public void setCounter(RiskResponseCounter counter) {
		this.counter = counter;
	}

	public SysEmployee getExecutionEmp() {
		return executionEmp;
	}

	public void setExecutionEmp(SysEmployee executionEmp) {
		this.executionEmp = executionEmp;
	}


	
	
	
	
}
