package com.fhd.entity.response;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.risk.Risk;

/**
 * 控制措施
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-9-17		下午2:23:09
 *
 * @see 	 
 */
@Entity
@Table(name="T_RM_SOLUTION_RELA_RISK")
public class SolutionRelaRisk extends IdEntity implements Serializable {
	
	private static final long serialVersionUID = -7714989398654347108L;

	/**
	 * 应对措施
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOLUTION_ID")
	private Solution solution;
	/**
	 * 应对措施
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RISK_ID")
	private Risk risk;
	public Solution getSolution() {
		return solution;
	}
	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	public Risk getRisk() {
		return risk;
	}
	public void setRisk(Risk risk) {
		this.risk = risk;
	}
	
	
	
}

