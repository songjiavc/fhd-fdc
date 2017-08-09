package com.fhd.fdc.commons.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.log.BusinessLog;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.log.BusinessLogBO;

/**
 * 过滤器.
 * @author 吴德福
 * @since 2013-10-11
 */
public class BusinessFilter implements Filter {

	private Logger logger = Logger.getLogger(BusinessFilter.class);
	
	@Override
	public void destroy() {
		logger.info("----过滤器消毁----");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		logger.info("-----过滤器执行了-----");

		String ip = request.getRemoteHost();

		Map<String, String[]> parameterMap = request.getParameterMap();

		// request.getServletContext().getContextPath();

		ServletContext servletContext = request.getServletContext();
		servletContext.setAttribute("parameterMap", parameterMap);
		servletContext.setAttribute("ip", ip);

		// request.getSession().getServletContext.getAttribute("m1");

		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		BusinessLogBO o_businessLogBO = (BusinessLogBO) ctx.getBean("businessLogBO");


		String userId = UserContext.getUser().getUserid();
		String companyId = UserContext.getUser().getCompanyid();
		
		BusinessLog businessLog = new BusinessLog();
		businessLog.setId(Identities.uuid());
		businessLog.setIp(request.getRemoteHost());
		
		businessLog.setIsSuccess("");
		businessLog.setModuleName("");
		businessLog.setOperateType("");
		
		businessLog.setOperateRecord("");
		businessLog.setOperateTime(new Date());
		SysOrganization org = new SysOrganization();
		org.setId(companyId);
		businessLog.setSysOrganization(org);
		SysUser sysUser = new SysUser();
		sysUser.setId(userId);
		businessLog.setSysUser(sysUser);
		
		o_businessLogBO.saveBusinessLog(businessLog);

		// 执行过滤
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		logger.info("----过滤器初始化----");
	}
}