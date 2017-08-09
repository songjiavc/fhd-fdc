package com.fhd.sm.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.sm.business.SmInitBO;

/**
 * 监控预警初始化control
 *
 */
@Controller
public class SmInitControl {

	@Autowired
	private SmInitBO o_smInitBO;
	
	private static Log log = LogFactory.getLog(SmInitControl.class);
	
	/**清除公司下指标及指标相关数据
	 * @param companyId公司ID
	 * @throws IOException 
	 */
	@ResponseBody
    @RequestMapping("/sm/init/removeKpiData.f")
	public void removeKpiData(String companyId,HttpServletResponse response) throws IOException {
		boolean result = true;
		try{
			if(StringUtils.isNotBlank(companyId)){
				o_smInitBO.removeKpiRelaData(companyId);
			}
		}catch (Exception e) {
			result = false;
			e.printStackTrace();
			log.error("清除公司下指标及指标相关数据,exception====="+e.toString());
		}
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
	}
	
	
	/**清除公司下指标类型及指标类型相关数据
	 * @param companyId公司ID
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping("/sm/init/removeKcData.f")
	public void removeKcData(String companyId,HttpServletResponse response) throws IOException {
		boolean result = true;
		try{
			if(StringUtils.isNotBlank(companyId)){
				o_smInitBO.removeKcRelaData(companyId);
			}
		}catch (Exception e) {
			result = false;
			e.printStackTrace();
			log.error("清除公司下指标类型及指标类型相关数据,exception====="+e.toString());
		}
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
	}
	
	/**清除公司下目标及目标相关数据
	 * @param companyId公司ID
	 */
	@ResponseBody
	@RequestMapping("/sm/init/removeSmData.f")
	public void removeSmData(String companyId,HttpServletResponse response) throws IOException {
		boolean result = true;
		try{
			if(StringUtils.isNotBlank(companyId)){
				o_smInitBO.removeStrategyRelaData(companyId);
			}
		}catch (Exception e) {
			result = false;
			e.printStackTrace();
			log.error("清除公司下目标及目标相关数据,exception====="+e.toString());
		}
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
	}
	/**清除公司下记分卡及记分卡相关数据
	 * @param companyId公司ID
	 */
	@ResponseBody
	@RequestMapping("/sm/init/removeScData.f")
	public void removeScData(String companyId,HttpServletResponse response) throws IOException {
		boolean result = true;
		try{
			if(StringUtils.isNotBlank(companyId)){
				o_smInitBO.removeCategoryRelaData(companyId);
			}
		}catch (Exception e) {
			result = false;
			e.printStackTrace();
			log.error("清除公司下记分卡及记分卡相关数据,exception====="+e.toString());
		}
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
	}
	
	
}
