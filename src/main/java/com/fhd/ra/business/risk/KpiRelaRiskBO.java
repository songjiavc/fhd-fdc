package com.fhd.ra.business.risk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.risk.KpiRelaRiskDAO;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.ra.interfaces.risk.IKpiRelaRiskBO;


@Service
public class KpiRelaRiskBO implements IKpiRelaRiskBO{
	@Autowired
	private KpiRelaRiskDAO o_kpiRelaRiskDAO;

	public void saveKpiRelaRisk(KpiRelaRisk kpiRelaRisk){
		o_kpiRelaRiskDAO.merge(kpiRelaRisk);
	}
	
	public void removeKpiRelaRiskById(String id){
		o_kpiRelaRiskDAO.delete(id);
	}
}

