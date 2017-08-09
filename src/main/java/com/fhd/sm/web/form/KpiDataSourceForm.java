package com.fhd.sm.web.form;

import com.fhd.entity.kpi.KpiDataSource;

public class KpiDataSourceForm extends KpiDataSource {
	
   private static final long serialVersionUID = 1047671522868944666L;
   public KpiDataSourceForm(){
	  
    }
  public KpiDataSourceForm(KpiDataSource kpiDataSource) {
	  this.setId(kpiDataSource.getId());
	  this.setDriverName(kpiDataSource.getDriverName());
	  this.setIp(kpiDataSource.getIp());
	  this.setPort(kpiDataSource.getPort());
	  this.setDataBaseName(kpiDataSource.getDataBaseName());
	  this.setUserName(kpiDataSource.getUserName());
	  this.setPassWord(kpiDataSource.getPassWord());
	  this.setDbType(kpiDataSource.getDbType());	  
  }
}
