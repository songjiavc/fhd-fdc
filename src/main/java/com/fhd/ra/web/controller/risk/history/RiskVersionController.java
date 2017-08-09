package com.fhd.ra.web.controller.risk.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.risk.history.NewRiskVersion;
import com.fhd.entity.risk.history.RiskHistory;
import com.fhd.ra.business.risk.history.RiskVersionBO;
import com.fhd.ra.web.form.risk.RiskVersionForm;
/**
 * 风险版本controller
 * @author wzr
 *
 */
@Controller
public class RiskVersionController {

	@Autowired
	private RiskVersionBO o_riskVersionBO;
	
	/**
	 * 查询风险版本列表
	 * @param start
	 * @param limit
	 * @param query	模糊查询版本名称
	 * @param schm	风险版本分库标识
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/riskhistory/queryriskversionspage.f")
	public Map<String, Object> queryRiskVersionsPage(int start, int limit, String sort, String query, String schm){
		Page<NewRiskVersion> page = new Page<NewRiskVersion>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_riskVersionBO.findversionsPageBySome(query, page, schm);
		List<NewRiskVersion> entityList = page.getResult();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", entityList);
		return map;
	}
	
	/**
	 * 保存风险版本
	 * @param versionForm
	 * @param verId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/risk/riskhistory/saveriskversion.f")
	public Map<String,Object> saveRiskVersion(RiskVersionForm versionForm, String verId, String schm){
		Map<String,Object> result=new HashMap<String,Object>();
		Boolean resultB = o_riskVersionBO.saveRiskVersion(versionForm,verId,schm);
		result.put("success", resultB);
		return result;
	}
	
	/**
	 * 修改  查询表单
	 * @param verId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/risk/riskhistory/findriskversionbyid.f")
	public Map<String, Object> findRiskVersionByID(String verId){
		Map<String, Object> map=o_riskVersionBO.findRiskVersionByID(verId);
		return map;
	}
	
	/**
	 * 查看版本风险明细树列表
	 * @param verId
	 * @param query
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/risk/riskhistory/findrisksdetailbyverid.f")
	public Map<String,Object> findRisksDetailByVerId(String verId,String query){
		Map<String,Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> riskList = o_riskVersionBO.findRisksDetailByVerId(verId,query);
		if(riskList.size()>0){
			map.put("children", riskList);
			map.put("linked", true);
		}
		return map;
	}
	/**
	 * 查看风险基本信息明细
	 * @param verId
	 * @param riskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/risk/riskhistory/findriskdetailbyverid.f")
	public Map<String, Object> findRiskDetailByverID(String historyId){
		Map<String, Object> map=o_riskVersionBO.findRiskDetailByVerID(historyId);
		return map;
	}
	
	/**
	 * 查询本版本较上一版本新增的风险事件明细
	 * @param verId
	 * @param isAdd	是新增的还是删除的风险事件，true:新增
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/riskhistory/findaddrisksbyverid.f")
	public Map<String, Object> findAddRisksByVerId(String verId, Boolean isAdd){
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<RiskHistory> riskList = o_riskVersionBO.findAddRisksByVerId(verId,isAdd);
		map.put("datas", riskList);
		return map;
	}
	
}
