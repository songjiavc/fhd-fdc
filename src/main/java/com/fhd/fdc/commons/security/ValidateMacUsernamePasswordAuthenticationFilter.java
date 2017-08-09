/**
 * ValidateMacUsernamePasswordAuthenticationFilter.java
 * com.fhd.fdc.commons.security
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2011-7-28 		胡迪新
 *
 * Copyright (c) 2011, Firsthuida All Rights Reserved.
*/
package com.fhd.fdc.commons.security;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.DigestUtils;
import com.fhd.core.utils.PropertyUtils;
import com.fhd.core.utils.RSACoder;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.sys.business.auth.SysUserBO;

/**
 * ValidateMacUsernamePasswordAuthenticationFilter
 * @author 胡迪新
 * @modify 吴德福 2013-10-30 11:13:00
 * @version  
 * @since    Ver 1.1
 * @Date	 2011-7-28		下午03:35:46
 * @see 	 
 */
@SuppressWarnings("deprecation")
public class ValidateMacUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(ValidateMacUsernamePasswordAuthenticationFilter.class);
	//ip
	private static final String IP_PARAM = "j_ip";
	//单点登录标识
	private static final String SSO_PARAM = "j_sso";
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		// 验证许可证
//		verifyLicense(request);
		
		
		if (!request.getMethod().equals("POST")) {  
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());  
        }
		
		//单点登录：临时密码
		String tempPassword = "";
		
		//登录验证
		String username = obtainUsername(request);
        String password = obtainPassword(request);
		String sso = obtainSSO(request);
				
		if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();

        ServletContext servletContext = request.getSession().getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		SysUserBO o_sysUserBO =  (SysUserBO) ctx.getBean("sysUserBO");
		
        List<Object[]> userInfoList = o_sysUserBO.findUserInfoListByUsername(username);
        if(null != userInfoList && userInfoList.size()>0){
        	for (Object[] objects : userInfoList) {
    			username = String.valueOf(objects[0]);
    			tempPassword = String.valueOf(objects[1]);
    		}
        }else{
    		throw new AuthenticationServiceException("用户不存在!");   
    	}
    	if(StringUtils.isNotBlank(sso) && "sso".equals(sso)){
        	//单点登录不验证密码，把数据库中的密码直接赋值，供spring security验证使用:sso--表示单点登录
        	password = tempPassword;
        }else{
        	//正常登录系统验证密码
        	if(!tempPassword.equals(DigestUtils.md5ToHex(password))){
        		throw new AuthenticationServiceException("密码错误!");
        	}
        }
		
		//验证mac地址
		Properties properties = null;
		try {
			properties = PropertyUtils.loadProperties("application.properties");
		} catch (IOException e) {
			logger.error("attemptAuthentication(HttpServletRequest, HttpServletResponse)", e); //$NON-NLS-1$
		}
		Boolean macCheck = Boolean.valueOf(properties.getProperty("mac.check"));
		if(macCheck) {
			if(!checkValidateMac(request)) {
				throw new AuthenticationServiceException("MAC地址不匹配");
			}
		}
		
        //UsernamePasswordAuthenticationToken实现 Authentication
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        // Place the last username attempted into HttpSession for views
        HttpSession session = request.getSession(false);
        if (session != null || getAllowSessionCreation()) {
            request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(username));
        }
        
        // 允许子类设置详细属性
        setDetails(request, authRequest);
          
        // 运行UserDetailsService的loadUserByUsername 再次封装Authentication
        return this.getAuthenticationManager().authenticate(authRequest);
	}
	/**
	 * 验证mac地址是否与用户定义的mac地址一致.
	 * @param request
	 * @return Boolean
	 */
	public Boolean checkValidateMac(HttpServletRequest request) {
		// 验证mac地址是否相同标志位
		Boolean flag = Boolean.FALSE;
		
		// 获得远程mac地址
		String username = obtainUsername(request);
		String ip = obtainIp(request);
		String remoteMac = getMAC(ip);
		
		// 获得登录人员mac地址
		SysUserBO sysUserBO = ContextLoader.getCurrentWebApplicationContext().getBean(SysUserBO.class);
		SysUser sysUser = sysUserBO.getByUsername(username);
		if(null != sysUser){
			if(StringUtils.isNotBlank(sysUser.getMac())){
				String userMac = sysUser.getMac();
				if (remoteMac.equalsIgnoreCase(userMac)) {
					flag = Boolean.TRUE;
				}
			}
		}
		return flag;
	}
	/**
	 * 从request对象中获取ip.
	 * @param request
	 * @return String
	 */
	public String obtainIp(HttpServletRequest request) {
		return request.getParameter(IP_PARAM);
	}
	/**
	 * 从request对象中获取sso标识.
	 * @param request
	 * @return String
	 */
	public String obtainSSO(HttpServletRequest request) {
		return request.getParameter(SSO_PARAM);
	}
	/**
	 * 执行脚本程序 取得客户端的mac地址.
	 * @param ip
	 * @return String
	 */
	public String getMAC(String ip) {
		String str = "";
		String macAddress = "";
		try {
			Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
			InputStreamReader ir = new InputStreamReader(p.getInputStream(),"GBK");
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					if (str.indexOf("MAC") > 1) {
						macAddress = str.substring(str.length() - 17, str.length());
						break;
					}
				}
			}
		} catch (IOException e) {
			logger.error("getMAC(String)", e); //$NON-NLS-1$
		}
		return macAddress;
	}
	
	
	/**
	 * 许可证检查
	 * 
	 * @param event
	 * @author vincent
	 */
	private void verifyLicense(HttpServletRequest request) {
		
		// 公钥
		String publicKey = "";
		// 解密后的明文
		String decrypt = "";
		
		try {
			Map<String, Object> keyMap = RSACoder.genKeyPair("0");
			publicKey = RSACoder.getPublicKey(keyMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			File licenseFile = new File(request.getRealPath("/") + "/license.lic");
			// 解密
			decrypt = new String(RSACoder.decryptByPublicKey(FileUtils.readFileToByteArray(licenseFile), publicKey));
			if(StringUtils.isEmpty(decrypt)) {
				throw new RuntimeException("许可证信息不正确");
			}
		} catch (Exception e) {
			throw new RuntimeException("许可证文件未找到或信息不正确",e);
			
		}
		// 检查license
		String[] decrypts = StringUtils.split(decrypt,",");
		if(!"forever".equals(decrypts[1])){
			Date today = new Date();
			Date startDate = DateUtils.parse(decrypts[0]);
			Date endDate = DateUtils.parse(decrypts[1]);
			if(today.before(startDate)) {
				throw new RuntimeException("许可证已过期");
			}
			
			if(today.after(endDate)) {
				throw new RuntimeException("许可证已过期");
			}
		}
		
		
	}
	
}