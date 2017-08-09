package com.fhd.ra.web.controller.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.risk.ImportRiskBO;

@Controller
public class RiskStorageImportControl {
	
	@Autowired
	private ImportRiskBO o_importRiskBO;
	
	/**
	 * 导入风险
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "risk/importRiskStorage.f")
	public Map<String, Object> importRiskStorage(String companyId) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String fromCompanyId = companyId;
		String toCompanyId = UserContext.getUser().getCompanyid();
		if(fromCompanyId.equals(toCompanyId)){
			resultMap.put("success", false);
			return resultMap;
		}
		String createBy = UserContext.getUserid();
		o_importRiskBO.saveRiskList(fromCompanyId, toCompanyId, createBy);
		
		resultMap.put("success", true);
		return resultMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "risk/findRiskStorage.f")
	public List<Map<String, Object>> findRiskStorage(String node,String companyId) throws Exception {
		List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
		if("root".equals(node)){
			node = null;
		}
		if(StringUtils.isBlank(companyId)){
			companyId = UserContext.getUser().getCompanyid();
		}
		resultMapList = o_importRiskBO.findRiskTreeRecord(node, companyId);

		return resultMapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "risk/findAllCompany.f")
	public Map<String, Object> findAllCompany(){
		Map<String, Object> resultMap = new HashMap<String,Object>();
		List<SysOrganization> companyList = o_importRiskBO.findAllCompanyAndGroup();
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		for(SysOrganization org : companyList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", org.getId());
			map.put("name", org.getOrgname());
			datas.add(map);
		}
		
		resultMap.put("totalCount", companyList.size());
		resultMap.put("datas", datas);
		return resultMap;
	}
	
	/**
	 * 子公司下拉树返回的内容
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "risk/findHierarchyCompany.f")
	public Map<String, Object> findHierarchyCompany(){
		return o_importRiskBO.findHierarchyCompany();
	}
}
