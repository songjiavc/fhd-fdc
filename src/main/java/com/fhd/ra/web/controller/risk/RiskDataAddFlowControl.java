package com.fhd.ra.web.controller.risk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.risk.Risk;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.business.risk.RiskDataAddFlowBO;

@Controller
public class RiskDataAddFlowControl {
	
	@Autowired
	private RiskDataAddFlowBO o_riskDataAddFlowBO;
	
	@Autowired
	private RiskCmpBO o_riskCmpBO;
	
	/**
	 * 激活工作流
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/flow/saveRiskApply")
	public Map<String, Object> saveRiskApply(){
		Map<String, Object> map = new HashMap<String, Object>();
		o_riskDataAddFlowBO.submitSavedRiskToApprover();
		map.put("success", true);
		return map;
	}
	
	/**
	 * 领导审批
	 * @param ids 审批列表中风险事件的id字符串，例如：“111,222”
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/flow/saveLeaderApproval")
	public Map<String, Object> saveLeaderApproval(String executionId,String businessId,String isPass,String examineApproveIdea,String ids){
		Map<String, Object> map = new HashMap<String, Object>();
		o_riskDataAddFlowBO.approveRisk(executionId, businessId, isPass, examineApproveIdea,ids);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 公司风险管理员审批
	 * @param ids 审批列表中风险事件的id字符串，例如：“111,222”
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/flow/saveManagerApproval")
	public Map<String, Object> saveManagerApproval(String executionId,String businessId,String isPass,String examineApproveIdea,String ids){
		Map<String, Object> map = new HashMap<String, Object>();
		o_riskDataAddFlowBO.archiveRisk(executionId, businessId, isPass, examineApproveIdea,ids);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 修改风险事件的处理状态，主要用于删除
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/flow/mergeRiskEventArchiveState")
	public Map<String, Object> mergeRiskEventArchiveState(String ids,String state){
		Map<String, Object> map = new HashMap<String, Object>();
		if(ids.split(",").length>1){
			String[] idArr = ids.split(",");
			for(int i=0;i<idArr.length;i++){
				String id = idArr[i];
				Risk risk = o_riskCmpBO.findRiskById(id);
				risk.setArchiveStatus(state);
				o_riskCmpBO.mergeRisk(risk);
			}
		}else{
			Risk risk = o_riskCmpBO.findRiskById(ids);
			risk.setArchiveStatus(state);
			o_riskCmpBO.mergeRisk(risk);
		}
		map.put("success", true);
		return map;
	}
	
	/**
	 * 查找待审批或者待归档的风险列表
	 * @author zhengjunxiang 2013-11-19
	 * @state  可以参考Contents类
	 */
	@ResponseBody
	@RequestMapping(value = "/risk/flow/findRiskEventByArchiveState")
	public Map<String, Object> findRiskEventByArchiveState(String state,int start, int limit, String query, String sort) {
		Map<String, Object> map = new HashMap<String, Object>();

		Page<Map> page = new Page<Map>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		String dir = "ASC";
		String sortColumn = null;
		if (StringUtils.isNotBlank(sort)) {
			JSONArray jsonArray = JSONArray.fromObject(sort);
			if (jsonArray.size() > 0) {
				JSONObject jsobj = jsonArray.getJSONObject(0);
				sortColumn = jsobj.getString("property");// 按照哪个字段排序
				dir = jsobj.getString("direction");// 排序方向
			}
		}
		String companyId = UserContext.getUser().getCompanyid();
		if(state.equals(Contents.RISK_STATUS_WAITINGAPPROVE)){
			String deptId = UserContext.getUser().getMajorDeptId();
			page = o_riskCmpBO.findRiskEventForOrgManager(companyId, deptId, page, sortColumn, dir, query);
		}else if(state.equals(Contents.RISK_STATUS_WAITINGARCHIVE)){
			page = o_riskCmpBO.findRiskEventForCompanyManager(companyId, page, sortColumn, dir, query);
		}

		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		List<Map> list = page.getResult();

		String[] ids = new String[list.size()];
		Set<String> idSet = new HashSet<String>();
		for (int i = 0; i < list.size(); i++) {
			Map r = (Map) list.get(i);
			ids[i] = r.get("id").toString();
			// 把上级节点id都存起来，去掉重复的
			if (r.get("idSeq") != null) {
				String[] idsTemp = r.get("idSeq").toString().split("\\.");
				idSet.addAll(Arrays.asList(idsTemp));
			}
		}
		Object[] idSeqArr = idSet.toArray();
		String[] idSeqs = new String[idSeqArr.length];
		for (int i = 0; i < idSeqs.length; i++) {
			idSeqs[i] = idSeqArr[i].toString();
		}

		// 1.获取风险事件的责任部门名称：‘内控部，生成部’
		Map<String, Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
		// 2.获取风险的所有上级节点
		Map<String, Object> riskMap = o_riskCmpBO
				.findParentRiskNameByRiskIds(idSeqs);

		Map<String, Object> item = null;
		for (Map event : list) {
			String eventId = event.get("id").toString();
			item = new HashMap<String, Object>();
			item.put("id", event.get("id").toString());
			item.put("name", event.get("name").toString());
			item.put("etrend", event.get("etrend").toString());
			item.put("assessementStatus", event.get("assessementStatus")
					.toString());

			// 风险的责任部门
			Object respDeptName = ((Map) orgMap.get("respDeptMap"))
					.get(eventId);
			if (respDeptName == null) {
				item.put("respDeptName", "");
			} else {
				item.put("respDeptName", respDeptName.toString());
			}
			// 风险的相关部门
			Object relaDeptName = ((Map) orgMap.get("relaDeptMap"))
					.get(eventId);
			if (relaDeptName == null) {
				item.put("relaDeptName", "");
			} else {
				item.put("relaDeptName", relaDeptName.toString());
			}

			// 所属风险 获取风险事件的所以上级风险名称：‘战略风险>战略管理风险’
			String belongRisk = "";
			if (event.get("idSeq") != null) {
				String idSeqStr = event.get("idSeq").toString()
						.replace(".", ",");
				String[] idSeq = idSeqStr.split(",");
				for (int i = 0; i < idSeq.length; i++) {
					if (!idSeq[i].equals("") && !idSeq[i].equals(eventId)) {
						belongRisk += ((Risk) riskMap.get(idSeq[i])).getName()
								+ ">";
					}
				}
				belongRisk = belongRisk.substring(0, belongRisk.length() - 1);
			}
			item.put("belongRisk", belongRisk);

			// 上级风险
			int pos = belongRisk.lastIndexOf(">") + 1;
			if (pos != -1) {
				item.put("parentName", belongRisk.substring(pos));
			} else {
				item.put("parentName", "");
			}
			item.put("parentId", event.get("parentId").toString());

			// 是否是最新风险事件.规则:最近30天内的都是最新事件
			if (event.get("lastModifyTime") != null) {
				Long t = ((Date) event.get("lastModifyTime")).getTime();
				Long curentTime = new Date().getTime();
				Long diff = 30 * 24 * 60 * 60 * 1000l;
				if ((curentTime - t) < diff) {
					item.put("isNew", true);
				} else {
					item.put("isNew", false);
				}
			} else {
				item.put("isNew", false);
			}

			datas.add(item);
		}

		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);

		return map;
	}
}
