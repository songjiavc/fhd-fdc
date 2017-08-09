package com.fhd.comm.web.controller.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fhd.comm.business.bpm.jbpm.JbpmHistTaskBO;
import com.fhd.core.utils.DigestUtils;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.sys.business.auth.SysUserBO;

/**
 * 单点登录处理类.
 * @author 吴德福
 * @Date 2013-10-29 17:08:54
 */
@Controller
public class SSOController {
	
	@Autowired
	private SysUserBO o_sysUserBO;
	@Autowired
	private JbpmHistTaskBO o_jbpmHistTaskBO;
	
	private static Logger LOGGER = Logger.getLogger(SSOController.class);
	
	/**
	 * 待办详细模拟自动登录验证.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
	@RequestMapping(value = "/sso/ssoLogin.do")
	public String ssoLogin(HttpServletRequest request, HttpServletResponse response){
    	String userNumber = "";
    	
		String loginType = request.getParameter("loginType");
		if(StringUtils.isNotBlank(loginType)){
			if("email".equals(loginType)){
				//email推送待办
				userNumber = request.getParameter("username");
			}else if("sso".equals(loginType)){
				//门户推送待办--正式系统获取胸卡号
				userNumber = request.getHeader("iv-user");
			}
		}
    	
    	//进行验证
		String errorMsg = "";
		if(StringUtils.isBlank(userNumber)){
			errorMsg = "胸卡号不能为空!";
		}else{
			//如果有加密，此处需要解密
			/***解密开始****解密中****解密完毕***/
			
			SysUser sysUser = o_sysUserBO.getByUsername(userNumber);
			if(null == sysUser){
				errorMsg = "风险管理信息系统中用户'"+userNumber+"'不存在!";
			}
		}
		
		if(StringUtils.isNotBlank(errorMsg)){
			//登录失败，验证不通过返回错误信息
			request.setAttribute("SSO_LOGIN_MESSAGE", errorMsg);
    		//指向登录页面
			//response.sendRedirect(request.getContextPath() + "/login.do");
    		return "redirect:/login.do";
		}else{
			request.setAttribute("username", userNumber);
		}
		
		if(null == loginType || "null".equals(loginType) || StringUtils.isBlank(loginType)){
			//非单点登录
			return "redirect:/login.do";
		}
		return "ssoLogin";
	}
	/**
	 * 单点待办执行跳转页面.
	 * @author 吴德福
	 * @param request
	 * @param response
	 * @return String
	 */
	@RequestMapping(value = "/sso/jbpmTask.do")
	public String jbpmTask(HttpServletRequest request, HttpServletResponse response){
		String taskId = request.getParameter("taskId");
		String businessId = request.getParameter("businessId");
		String url = request.getParameter("url");

		//工作流taskId转executionId
		VJbpmHistTask vJbpmHistTask = o_jbpmHistTaskBO.findById(Long.valueOf(taskId));
		if(null != vJbpmHistTask){
			if(0 == vJbpmHistTask.getDbversion()){
				//未执行
				String executionId = vJbpmHistTask.getJbpmExecution().getId_();
				
				//正式系统使用
				request.setAttribute("executionId", executionId);
				request.setAttribute("businessId", businessId);
				request.setAttribute("url", url);
			}else{
				//已执行错误提示
				request.setAttribute("errorMsg", "该流程节点已经执行，请不要重复操作!");
				return "jbpmTaskError";
			}
		}
		return "jbpmTask";
	}
	/**
	 * main方法测试.
	 */
	public static void main(String[] args) {
		//md5加密
		LOGGER.info("wudefu:"+DigestUtils.md5ToHex("wudefu"));
		//字符串拆分
		LOGGER.info("123".split("&")[0]);
	}
}