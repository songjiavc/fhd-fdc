package com.fhd.sm.ws;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public class GatherDataServicesClient {
	public static void main(String[] args) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IGatherDataServices.class);
		factory.setAddress("http://127.0.0.1:8081/fdc/cxfservices/igatherservice");
		IGatherDataServices service = (IGatherDataServices) factory.create();
		GatherDataDto dto = new GatherDataDto();
		dto.setName("任务完成率12");
		dto.setStatus("低");
		dto.setTimes("2013-12-15 12:12:12");
		dto.setType("评估值");
		dto.setValue("70.20");
		dto.setDesc("任务完成率12");
		dto.setCode("110");
		service.saveKpiGatherData(dto);
	}
}
