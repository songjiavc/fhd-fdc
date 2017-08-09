/**
 * TempControl.java
 * com.fhd.sys.web.controller.st
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-10-11 		金鹏祥
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.web.controller.st;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.st.Temp;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.dic.DictTreeBO;
import com.fhd.sys.business.st.TempBO;
import com.fhd.sys.web.form.st.TempForm;

/**
 * 计划任务模版控制分发
 *
 * @author   金鹏祥
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-10-11		上午09:24:04
 *
 * @see 	 
 */
@Controller
public class TempControl {
	
	@Autowired
    private DictTreeBO o_dictTreeBO;
	@Autowired
	private TempBO o_tempBO;
	@Autowired
	private DictBO o_dictBO;
	
	/**
     * 保存、更新模版.
     * @author 金鹏祥
     * @modify 吴德福
     * @param data form实体
     * @param response
     * @param dictEntryId 字典项ID
     * @param contentEdit 内容
     * @throws Exception
     * @since  fhd　Ver 1.1
    */
	@ResponseBody
    @RequestMapping(value = "/sys/st/saveTemp.f")
	public Map<String,Object> saveTemp(TempForm data,	String dictEntryId, String contentEdit) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		
		Temp entity = null;
		DictEntry dictEntry = o_tempBO.findDictEntryById(dictEntryId);
		if(StringUtils.isNotBlank(data.getId())){
			//更新
			entity = new Temp();
			BeanUtils.copyProperties(data, entity, new String[]{});
			entity.setDictEntry(dictEntry);
			if(StringUtils.isNotBlank(contentEdit)){
				entity.setContent(contentEdit);
			}
			o_tempBO.mergeTemp(entity);
		}else{
			//保存
			entity = new Temp(Identities.uuid());
			BeanUtils.copyProperties(data, entity, new String[]{"id"});
			entity.setDictEntry(dictEntry);
			if(StringUtils.isNotBlank(contentEdit)){
				entity.setContent(contentEdit);
			}
			o_tempBO.saveTemp(entity);
		}
		
		map.put("success", true);
		return map;
	}
	
	/**
     * 
     * 查询字典项
     * 
     * @author 金鹏祥
     * @param request
     * @param dictEntryId 字典项ID
     * @return Map
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/st/findDictEntryBySome.f")
    public Map<String, Object> findDictEntryBySome(HttpServletRequest request, String query, String dictEntryId) {
    	if(dictEntryId.indexOf(',') != -1){
    		dictEntryId = dictEntryId.replace(",", "");
    	}
		return o_dictTreeBO.findDictEntryTree(query, dictEntryId);
    }
    
    /**
     * 字典项对应参数.
     * @author 金鹏祥
     * @modify 吴德福
     * @param request
     * @return Map
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/st/findDictEntryByType.f")
    public Map<String, Object> findDictEntryByType(String dictTypeId) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    	
    	List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId(dictTypeId);
    	if(null != dictEntryList && dictEntryList.size()>0){
    		Map<String,Object> row = null;
    		for (DictEntry dictEntry : dictEntryList) {
    			row = new HashMap<String,Object>();
    			
    			row.put("parameter", dictEntry.getId());
    			row.put("describe", dictEntry.getName());
    			
    			list.add(row);
    		}
    	}
    	
		map.put("totalCount", list.size());
		map.put("datas", list);
		return map;
    }
    /**
     * 查询所有可用的模板列表.
     * @author 吴德福
     * @param dictEntryId
     * @return Map<String,Object>
     */
    @ResponseBody
    @RequestMapping(value = "/sys/st/findTempList.f")
    public Map<String,Object> findTempList(String dictEntryId){
    	Map<String,Object> map = new HashMap<String,Object>();
    	List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
    	
    	List<Temp> tempList = o_tempBO.findTempList(dictEntryId);
    	Map<String,Object> row = null;
    	for (Temp temp : tempList) {
    		row = new HashMap<String,Object>();
    		
    		row.put("id", temp.getId());
    		row.put("tempName", temp.getName());
    		if(null != temp.getDictEntry()){
    			row.put("dictEntryId", temp.getDictEntry().getId());
    			row.put("dictEntryName", temp.getDictEntry().getName());
    		}
    		row.put("tempContent", temp.getContent());

    		datas.add(row);
    	}
    	
    	map.put("datas", datas);
    	map.put("totalCount", datas.size());
    	
    	return map;
    }
    /**
     * 加载模板详细信息.
     * @author 吴德福
     * @param tempId
     * @return Map<String, Object>
     */
    @ResponseBody
    @RequestMapping(value = "/sys/st/findTempByIdForView.f")
    public Map<String, Object> findTempByIdForView(String tempId) {		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> inmap = new HashMap<String, String>();
		
		Temp temp = o_tempBO.findTempById(tempId);
		if(null != temp){
			inmap.put("id", temp.getId());
			inmap.put("name", temp.getName());
			inmap.put("content", temp.getContent());
			if(null != temp.getDictEntry()){
				inmap.put("dictTypeId", temp.getDictEntry().getId());
			}else{
				inmap.put("dictTypeId", "");
			}
		}else{
			inmap.put("id", "");
			inmap.put("name", "");
			inmap.put("content", "");
			inmap.put("dictTypeId", "");
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
    }
    /**
     * 根据id集合删除模板.
     * @author 吴德福
     * @param tempIds
     * @return Map<String,Object>
     */
    @ResponseBody
	@RequestMapping("/sys/st/removeTempByIds.f")
	public Map<String,Object> removeTempByIds(String tempIds) {
			
    	Map<String,Object> map = new HashMap<String,Object>();
    	
		if(StringUtils.isNotBlank(tempIds)){
			List<String> idList = new ArrayList<String>();
			String[] idArray = tempIds.split(",");
			for (String id : idArray) {
				if(!idList.contains(id)){
					idList.add(id);
				}
			}
			o_tempBO.removeTempByIdList(idList);
		}
			
		map.put("success", true);
		return map;
	}
    /**
     * 
     * 字典项对应Temp实体
     * 
     * @author 金鹏祥
     * @param request
     * @param dictEntryId 字典项ID
     * @return Map
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/sys/st/findTempByCategory.f")
    public Map<String, Object> findTempByCategory(HttpServletRequest request, String dictEntryId) {		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> inmap = new HashMap<String, String>();
		
		Temp temp = o_tempBO.findTempByCategory(dictEntryId);
		if(temp!=null){
			inmap.put("id", temp.getId());
			inmap.put("name", temp.getName());
			inmap.put("content", temp.getContent());
			inmap.put("dictEntryId", dictEntryId);
		}else{
			inmap.put("id", "");
			inmap.put("name", "");
			inmap.put("content", "");
			inmap.put("dictEntryId", dictEntryId);
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
    }
}