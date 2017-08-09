/*
 *北京第一会达风险管理有限公司 版权所有 2012
 *Copyright(C) 2012 Firsthuida Co.,Ltd. All rights reserved. 
 */
package com.fhd.fdc.web.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.RSACoder;

/**
 * 项目的许可管理
 *
 * @author   胡迪新
 * @since    fhd Ver 4.5
 * @Date	 2012-11-14  上午11:33:48
 *
 * @see 	 
 */
@SuppressWarnings("deprecation")
@Controller
public class LicenseControl {

	
	@ResponseBody
	@RequestMapping("/savelicense.f")
	public Map<String, Object> saveLicense(HttpServletRequest request,@RequestParam("licensefile") MultipartFile licensefile) {
		
		String decrypt = "";
		try {
			Map<String, Object> keyMap = RSACoder.genKeyPair("0");
			String publicKey  = RSACoder.getPublicKey(keyMap);
			// 解密
			byte[] decodedData = RSACoder.decryptByPublicKey(licensefile.getBytes(), publicKey);
			decrypt = new String(decodedData);
			if(StringUtils.isEmpty(decrypt)) {
				throw new RuntimeException("许可证信息不正确");
			}
			// 保存许可文件
			FileUtils.writeByteArrayToFile(new File(request.getRealPath("/") + "/license.lic") , licensefile.getBytes());
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> data = reData(decrypt);
		
		return data;
	}
	
	@ResponseBody
	@RequestMapping("/findlicense.f")
	public Map<String, Object> findLicense(HttpServletRequest request) {
		String decrypt = "";
		try {
			Map<String, Object> keyMap = RSACoder.genKeyPair("0");
			String publicKey  = RSACoder.getPublicKey(keyMap);
			byte[] decodedData = RSACoder.decryptByPublicKey(FileUtils.readFileToByteArray(new File(request.getRealPath("/") + "/license.lic")), publicKey);
			decrypt = new String(decodedData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> data = reData(decrypt);
		
		return data;
	}

	private Map<String, Object> reData(String decrypt) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("success", true);
		
		
		String[] decrypts = StringUtils.split(decrypt,",");
		Date today = new Date();
		if("forever".equals(decrypts[1])){
			data.put("msg","永久,N");
		}else{
			Date endDate = DateUtils.parse(decrypts[1]);
			data.put("msg", DateUtils.formatShortDate(endDate) + "," + (DateUtils.dateDiffDay(endDate, today)+1));
		}
		return data;
	}
	
}

