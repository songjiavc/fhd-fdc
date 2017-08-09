package com.fhd.sys.web.controller.assess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.sys.business.assess.SendEmailBO;
import com.fhd.sys.business.dic.DictBO;

/**
 * 是否发送email控制类
 * @author 王再冉
 *
 */
@Controller
public class SendEmailControl {

	@Autowired
	private SendEmailBO o_sendEmailBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * findIsSendEmailCheckBox：任务分配是否发送email，动态fieldset数据字典查询
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/findissendemailfieldset.f")
	public Map<String, Object> findIsSendEmailFieldset(){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> mapList = o_sendEmailBO.findisSendEmailFieldset("send_email_assess");
		map.put("dictEntryList", mapList);
		return map;
	}
	
	/**
	 * 根据数据字典类型查数据字典项Map集合
	 * add by 王再冉
	 * 2013-12-31  上午11:10:28
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/findissendemailfieldsetnew.f")
	public Map<String, Object> findIsSendEmailFieldsetNew(){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> mapList = o_sendEmailBO.findisSendEmailCheckBox();
		map.put("allList", mapList);
		return map;
	}
	
	/**
	 * 修改checkbox值
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/mergevaluebydictentryid.f")
	public Boolean mergeValueByDictEntryId(String dictEntryId,String value){
		return o_sendEmailBO.mergeValueByDictEntryId(dictEntryId,value);
	}
	
	/**
	 * 查询下拉框值
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/assess/findcomboxvalue.f")
	public Map<String, Object> findComboxValue(){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0yn");
		for (DictEntry dictEntry : dictEntryList) {
			inmap = new HashMap<String, Object>();
			inmap.put("id", dictEntry.getId());
			inmap.put("text",dictEntry.getName());
			list.add(inmap);
		}
		map.put("datas", list);
		return map;
	}
}
