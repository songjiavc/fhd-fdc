package com.fhd.icm.web.controller.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.icm.control.MeasureRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.icm.business.control.RiskRelaMeasureBO;

/**
 * 风险关联控制措施control.
 * @author 吴德福
 */
@Controller
@SuppressWarnings("unchecked")
public class RiskRelaMeasureControl {

	@Autowired
	private RiskRelaMeasureBO o_riskRelaMeasureBO;
	
	/**
	 * 根据控制措施id查询关联的风险列表及风险对应的控制措施.
	 * @param measureId 控制措施id
	 * @return Map<String, Object>
	 */
	@ResponseBody
	@RequestMapping("/icm/control/findRiskRelaMeasureListByMeasureId.f")
	public Map<String, Object> findAssessPlanListBySome(String measureId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		
		List<Risk> riskList = new ArrayList<Risk>();
		List<MeasureRelaRisk> measureRelaRiskList = o_riskRelaMeasureBO.findRiskbyMeasureId(measureId);
		for (MeasureRelaRisk measureRelaRisk : measureRelaRiskList) {
			riskList.add(measureRelaRisk.getRisk());
		}
		if(null != riskList && riskList.size()>0){
			Map<String, Object> riskMeasureMap = o_riskRelaMeasureBO.getRiskMeasureMapByRiskList(riskList);
			Map<String, Object> row = null;
			for (Risk risk : riskList) {
				row = new HashMap<String, Object>();
				row.put("id", risk.getId());
				//风险名称
				row.put("name", risk.getName());
				if(null != risk.getParent()){
					//上级风险
					row.put("parentName", risk.getParent().getName());
				}
				
				Set<RiskOrg> riskOrgs = risk.getRiskOrgs();
				StringBuilder respDeptName = new StringBuilder();
				int j=0;
				for (RiskOrg riskOrg : riskOrgs) {
					if("M".equals(riskOrg.getType())){
						respDeptName.append(riskOrg.getSysOrganization().getOrgname());
						if(j!=riskOrgs.size()-1){
							respDeptName.append(",");
						}
					}
					j++;
				}
				//责任部门
				row.put("respDeptName", respDeptName.toString());
				
				Set<RiskAdjustHistory> adjustHistorySet = risk.getAdjustHistory();
				for (RiskAdjustHistory riskAdjustHistory : adjustHistorySet) {
					if("1".equals(riskAdjustHistory.getIsLatest())){
						//评估状态
						row.put("assessementStatus", riskAdjustHistory.getAssessementStatus());
						//趋势
						row.put("etrend", riskAdjustHistory.getEtrend());
						break;
					}
				}
				//风险对应的控制措施
				StringBuffer measureStr = new StringBuffer();
				List<Measure> measureList = (List<Measure>) riskMeasureMap.get(risk.getId());
				for (int i = 0; i < measureList.size(); i++) {
					//measureStr += (i+1) + "." + measureList.get(i).getName()+"<br/>";
					measureStr.append((i+1) + "." + measureList.get(i).getName()+"<br/>");
				}
				row.put("measureStr", measureStr.toString());
				
				datas.add(row);
			}
			map.put("datas", datas);
			map.put("totalCount", riskList.size());
		}else{
			map.put("datas", new Object[0]);
			map.put("totalCount", "0");
		}
		
		return map;
	}
}