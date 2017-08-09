package com.fhd.ra.web.controller.risk.comprequery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.risk.history.NewRiskVersion;
import com.fhd.entity.risk.history.RiskHistory;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.ra.business.risk.history.RiskVersionBO;
import com.fhd.ra.business.risk.riskcomparequery.RiskCompreQueryBO;
import com.fhd.ra.web.form.risk.RiskCompreQueryForm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 风险综合查询controller
 * @author wzr
 *
 */
@Controller
public class RiskCompreQueryController {
	
	@Autowired
	private RiskVersionBO o_riskVersionBO;
	@Autowired
	private RiskCompreQueryBO o_riskCompreQueryBO;

	/**
	 * 查询风险版本下拉框
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/compre/findriskversionbytype.f")
	public Map<String, Object> findRiskVersionByType(String schm,String deptId){
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(null != deptId && !"".equals(deptId)){
			deptId = IcmStandardUtils.findIdbyJason(deptId, "id");
		}
		schm = schm.split("risk_schm_")[1];
		List<NewRiskVersion> versionList = o_riskVersionBO.findRiskVersionsByschm(schm,deptId);
		for (NewRiskVersion version : versionList) {
			inmap = new HashMap<String, Object>();
			inmap.put("type", version.getId());
			inmap.put("name",version.getVersionName());
			list.add(inmap);
		}
		map.put("datas", list);
		return map;
	}
	/**
	 * 风险综合查询-根据查询条件查询风险事件列表
	 * @param verId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/compre/queryriskrbsbyverid.f")
	public Map<String, Object> queryRiskrbsByVerid(RiskCompreQueryForm form,int start, int limit, String query, String sort){
		HashMap<String, Object> map = new HashMap<String, Object>();
		Page<Map> page = new Page<Map>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        String dir = "ASC";
        String sortColumn = null;
		if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
		if(null != form.getRiskVersion()){//查询风险版本库
			page = o_riskVersionBO.queryRisksByQueryForm(form,page,sort,dir);
		}else{//查询现行风险库
			page = o_riskCompreQueryBO.queryRisksByQueryForm(form,page,sort,dir);
		}
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		List<Map> list = page.getResult();
		Map<String, Object> item = null;
		for (Map event: list) {
			item = new HashMap<String, Object>();
			item.put("id",event.get("id").toString());
			item.put("versionId",null==event.get("versionId")?"":event.get("versionId").toString());
			item.put("riskId",null==event.get("riskId")?"":event.get("riskId").toString());
			item.put("riskCode",event.get("riskCode").toString());
			item.put("riskName",event.get("riskName").toString());
			item.put("riskDesc",event.get("riskDesc").toString());
			item.put("parentName",event.get("parentName").toString());
			item.put("mainName",event.get("mainName").toString());
			item.put("relaName",event.get("relaName").toString());
			item.put("score",event.get("score").toString());
			datas.add(item);
		}
		map.put("totalCount",page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 查询一级风险的风险事件明细
	 * @param verId
	 * @param rbsId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/compre/queryrisksbyrbsandverid.f")
	public Map<String, Object> queryRisksByRbsAndVerId(String verId,String rbsId){
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<RiskHistory> rbsList = o_riskVersionBO.queryRisksByRbsAndVerId(verId,rbsId);
		map.put("datas", rbsList);
		return map;
	}
}
