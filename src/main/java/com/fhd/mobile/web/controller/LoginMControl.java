package com.fhd.mobile.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fhd.entity.sys.auth.SysUser;
import com.fhd.sys.business.auth.SysUserBO;

@Controller
public class LoginMControl {

	@Autowired
	private SysUserBO o_sysUserBO;
	
	@RequestMapping("/mobile/login.f")
	public void login(PrintWriter out,HttpServletResponse response,String callback,String username,String password) throws IOException {
		System.out.println(username);
		System.out.println(password);
		String msg = "";
		boolean success = false;
		SysUser sysUser = o_sysUserBO.getByUsername(username);
		if(sysUser != null){
			if(DigestUtils.md5Hex(password).equals(sysUser.getPassword())){
				msg = "登录成功";
				success = true;
			}else {
				msg = "密码不正确";
			}
		}else {
			msg = "用户不存在";
		}
		
		boolean jsonP = false;
		if (callback != null) {
		    jsonP = true;
		    response.setContentType("text/javascript");
		} else {
		    response.setContentType("application/x-json");
		}
		if (jsonP) {
		    out.write(callback + "(");
		}
		out.print("{'data':{'msg':'" + msg + "','success':" + success + "}}");
		if (jsonP) {
		    out.write(");");
		}
		
	}
	
	
}
