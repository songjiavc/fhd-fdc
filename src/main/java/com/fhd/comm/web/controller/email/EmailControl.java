package com.fhd.comm.web.controller.email;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.sm.business.ChartForEmailBO;

@Controller
public class EmailControl {
	@Autowired
	private ChartForEmailBO o_chartForEmailBO;
	
	/**
	 * 系统运行时发生错误信息，点击发送给管理员触发的事件
	 * @param to 系统管理员emai信息
	 * @param shortContent
	 * @param content
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/email/sendErrorEmail.f")
    public Map<String, Object> sendErrorEmail(String to,String content, String shortContent){
		Map<String, Object> result = new HashMap<String, Object>();
		String title = "用户反馈的系统运行时错误信息";
		//String adminEmailAddress = "491537421@qq.com";	//系统管理员emai信息
		//content = "aaa";
		o_chartForEmailBO.sendEmail(new String[]{to}, null, null, null, title, content, null);
		result.put("success", true);
        return result;
	}
}
