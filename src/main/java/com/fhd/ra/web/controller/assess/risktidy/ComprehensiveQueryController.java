package com.fhd.ra.web.controller.assess.risktidy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.oper.RiskAdjustHistoryBO;
import com.fhd.sys.business.dic.DictBO;

@Controller
public class ComprehensiveQueryController {

	@Autowired
	private RiskAdjustHistoryBO o_riskAdjustHistoryBO;
	
	@Autowired
	private RiskAssessPlanBO o_riskAssessPlanBO;
	
	@Autowired
	private DictBO o_dictBO;
	
	/**
	 * 大综合查询GRID
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/riskTidy/findRiskAdjustHistoryInfos.f")
	public Map<String, Object> findRiskAdjustHistoryInfos(int start, int limit, String query, String datas,
			HttpServletRequest request, HttpServletResponse response) {
		String companyId = UserContext.getUser().getCompanyid();
		HashMap<String, Object> map = new HashMap<String, Object>();
		int pageSize = ((limit == 0 ? 0 : start / limit) + 1) -1;
		String planIdQuery = "";
		String assementStatusQuery = "";
		String riskStatusQuery = "";
		String orgMQuery = "";
		String orgAQuery = "";
		String assessEmpQuery = "";
		String riskNameQuery = "";
		
		if(StringUtils.isBlank(datas)){
			return map;
		}
		
		JSONArray headerArray=JSONArray.fromObject(datas);
		for(int i = 0; i < headerArray.size(); i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			planIdQuery = jsonObj.get("planIdQuery").toString();
			if(!"null".equalsIgnoreCase(jsonObj.get("riskStatusQuery").toString())){
				riskStatusQuery = jsonObj.get("riskStatusQuery").toString();
			}if(!"null".equalsIgnoreCase(jsonObj.get("assementStatusQuery").toString())){
				assementStatusQuery = jsonObj.get("assementStatusQuery").toString();
			}if(jsonObj.containsKey("orgMQuery")&&!"null".equalsIgnoreCase(jsonObj.get("orgMQuery").toString())){
				orgMQuery = jsonObj.get("orgMQuery").toString();
			}if(jsonObj.containsKey("orgAQuery")&&!"null".equalsIgnoreCase(jsonObj.get("orgAQuery").toString())){
				orgAQuery = jsonObj.get("orgAQuery").toString();
			}if(jsonObj.containsKey("assessEmpQuery")&&!"null".equalsIgnoreCase(jsonObj.get("assessEmpQuery").toString())){
				assessEmpQuery = jsonObj.get("assessEmpQuery").toString();
			}if(!"null".equalsIgnoreCase(jsonObj.get("riskNameQuery").toString())){
				riskNameQuery = jsonObj.get("riskNameQuery").toString();
			}
		}
		
		if(StringUtils.isNotBlank(query)){
			riskNameQuery = query;
		}
		
		ArrayList<HashMap<String, Object>> list = o_riskAdjustHistoryBO.findRiskAdjustHistoryInfos(start, limit, companyId,
				planIdQuery, assementStatusQuery, riskStatusQuery, orgMQuery, orgAQuery, assessEmpQuery, riskNameQuery);
		List<List<HashMap<String, Object>>> ss = this.splitList(list, limit);
		
		if(list.size() != 0){
			map.put("datas", ss.get(pageSize));
			map.put("totalCount", list.size());
		}
		return map;
	}
	
	public <T> List<List<T>> splitList(List<T> list, int pageSize)
    {

        int listSize = list.size();
        int page = (listSize + (pageSize - 1)) / pageSize;

        List<List<T>> listArray = new ArrayList<List<T>>();
        for (int i = 0; i < page; i++)
        {
            List<T> subList = new ArrayList<T>();
            for (int j = 0; j < listSize; j++)
            {
                int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize;
                if (pageIndex == (i + 1))
                {
                    subList.add(list.get(j));
                }
                if ((j + 1) == ((j + 1) * pageSize))
                {
                    break;
                }
            }
            listArray.add(subList);
        }
        return listArray;
    }

	
	/**
	 * 评估计划下拉菜单
	 * @author 金鹏祥
	*/
	@ResponseBody
	@RequestMapping(value = "/assess/riskTidy/findPlanComplete.f")
	public Map<String, Object> findPlanComplete(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		List<RiskAssessPlan> riskAssessPlanList = o_riskAssessPlanBO.findByDealStatusOrderTime("F");
		for (RiskAssessPlan riskAssessPlan : riskAssessPlanList) {
			inmap = new HashMap<String, Object>();
			inmap.put("id", riskAssessPlan.getId());
			inmap.put("text",riskAssessPlan.getPlanName());
			list.add(inmap);
		}
		
		map.put("datas", list);
		
		return map;
	}
	
	/**
	 * 风险级别(汇总)
	 * @author 金鹏祥
	*/
	@ResponseBody
	@RequestMapping(value = "/assess/riskTidy/findRiskLevel.f")
	public Map<String, Object> findRiskLevel(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = 0;
		
		List<DictEntry> dictList = o_dictBO.findDictEntryByDictTypeId("0alarm_startus");
		for (DictEntry dictEntry : dictList) {
			inmap = new HashMap<String, Object>();
			if(count == 0){
				HashMap<String, Object> inmaps = new HashMap<String, Object>();
				inmaps.put("id", "");
				inmaps.put("text", "全部");
				list.add(inmaps);
			}
			inmap.put("id", dictEntry.getValue());
			inmap.put("text", dictEntry.getName());
			list.add(inmap);
			count++;
		}
		
		map.put("datas", list);
		
		return map;
	}
	
	/**
	 * 大综合查询GRID
	 * */
	@ResponseBody
	@RequestMapping(value = "/assess/riskTidy/exportRiskAdjustHistoryInfos.f")
	public void exportRiskAdjustHistoryInfos(int start, int limit, String planIdQuery, String query,
			String assementStatusQuery, String riskStatusQuery, String orgMQuery, String orgAQuery, String assessEmpQuery, String riskNameQuery,
			String headerData, HttpServletRequest request, HttpServletResponse response) {
		String companyId = UserContext.getUser().getCompanyid();
		
		if(StringUtils.isNotBlank(query)){
			riskNameQuery = query;
		}
		
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] objects = null;
		String[] fieldTitle = null;
		List<String> indexList = new ArrayList<String>();
		JSONArray headerArray=JSONArray.fromObject(headerData);
		
		int j = headerArray.size();
		fieldTitle = new String[j];
		
		if("null".equalsIgnoreCase(assementStatusQuery)){
			assementStatusQuery = null;
		}if("null".equalsIgnoreCase(riskStatusQuery)){
			riskStatusQuery = null;
		}
//		int pageSize = ((limit == 0 ? 0 : start / limit) + 1) -1;
		ArrayList<HashMap<String, Object>> mapList = o_riskAdjustHistoryBO.findRiskAdjustHistoryInfos(start, limit, companyId,
				planIdQuery, assementStatusQuery, riskStatusQuery, orgMQuery, orgAQuery, assessEmpQuery, riskNameQuery);
		
//		List<List<HashMap<String, Object>>> ss = this.splitList(mapList, limit);
//		List<HashMap<String, Object>> aa = ss.get(pageSize);
		
		for(int i=0;i<j;i++){
			JSONObject jsonObj = headerArray.getJSONObject(i);
			fieldTitle[i] = jsonObj.get("text").toString();
			indexList.add(jsonObj.get("dataIndex").toString());
		}
		
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0alarm_startus");//风险水平
		List<Map<String,String>> dictRiskLevelMapList = new ArrayList<Map<String,String>>();
		for(DictEntry dict : dictEntryList){
			Map<String,String> dictRiskLevelMap = new HashMap<String, String>();
			dictRiskLevelMap.put(dict.getValue(), dict.getName());
			dictRiskLevelMapList.add(dictRiskLevelMap);
		}
		
		for(HashMap<String, Object> map : mapList){
			objects = new Object[j];
			for(int m=0;m<indexList.size();m++){
				if(null!=map.get(indexList.get(m))){
					if("assessementStatus".equals(indexList.get(m)) || "riskIcon".equals(indexList.get(m))){//风险水平
						for(Map<String,String> dictMap : dictRiskLevelMapList){
							String mkey = map.get(indexList.get(m)).toString();
							if(null != dictMap.get(mkey)){
								objects[m] = dictMap.get(mkey);
							}
						}
					}else{
						objects[m] = map.get(indexList.get(m)).toString();
					}
				}
			}
			list.add(objects);
		}
		String exportFileName = "综合查询数据.xls";
		String sheetName = "全面风险管理信息系统";
		try {
			ExcelUtil.write(list, fieldTitle, exportFileName, sheetName, 0, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
