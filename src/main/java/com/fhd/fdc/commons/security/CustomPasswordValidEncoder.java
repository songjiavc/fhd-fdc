package com.fhd.fdc.commons.security;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.fhd.core.utils.DigestUtils;

/**
 * 自定义密码验证--解决单点登录获取数据库的md5密码直接匹配问题.
 * @author 吴德福
 * @Date 2013-10-31 10:50:20
 */
public class CustomPasswordValidEncoder implements PasswordEncoder {

	@Override
	public String encodePassword(String rawPass, Object salt) throws DataAccessException {
		//md5加密
		return DigestUtils.md5ToHex(rawPass);
	}

	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
		//单点登录
		if(encPass.equals(rawPass)){
			return true;
		}
		//系统登录
		return encPass.equals(encodePassword(rawPass, salt));  
	}
}