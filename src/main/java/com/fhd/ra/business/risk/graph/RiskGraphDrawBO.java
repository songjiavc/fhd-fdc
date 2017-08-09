package com.fhd.ra.business.risk.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fhd.comm.interfaces.graph.IGraphDrawBO;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.fdc.utils.Contents;

@Service
public class RiskGraphDrawBO implements IGraphDrawBO {

	/**
	 * 风险历史记录
	 */
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	
	@Override
	public Map<String, Object> showStatus(String id) {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		
		List<String> idList = new ArrayList<String>();
		idList.add(id);
		List<Map<String,Object>> list = this.findLatestRiskAdjustHistoryByRiskIds(idList);
		if(list.size()>0){
			Map<String,Object> map = list.get(0);
			resultMap.put("id", map.get("id"));
			resultMap.put("name", map.get("name"));
			
			//判断level的值
			resultMap.put("riskLevel", map.get("status"));
			if(map.get("status")!=null){
				String riskLevel = "";
				String status = map.get("status").toString();
				if(status.equals(Contents.RISK_LEVEL_HIGH)){
					riskLevel = RiskGraphDrawBO.RISK_LEVEL_RED;
				}else if(status.equals(Contents.RISK_LEVEL_MIDDLE)){
					riskLevel = RiskGraphDrawBO.RISK_LEVEL_YELLOW;
				}else if(status.equals(Contents.RISK_LEVEL_LOW)){
					riskLevel = RiskGraphDrawBO.RISK_LEVEL_GREEN;
				}else{}
				resultMap.put("riskLevel", riskLevel);
			}
			resultMap.put("value", map.get("score"));
		}
		
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> showStatus(List<String> idList) {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list = this.findLatestRiskAdjustHistoryByRiskIds(idList);
		
		//组装成接口格式
		for(Map<String,Object> map : list){
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("id", map.get("id"));
			m.put("name", map.get("name"));
			
			//判断level的值
			m.put("riskLevel", map.get("status"));
			if(map.get("status")!=null){
				String riskLevel = "";
				String status = map.get("status").toString();
				if(status.equals(Contents.RISK_LEVEL_HIGH)){
					riskLevel = RiskGraphDrawBO.RISK_LEVEL_RED;
				}else if(status.equals(Contents.RISK_LEVEL_MIDDLE)){
					riskLevel = RiskGraphDrawBO.RISK_LEVEL_YELLOW;
				}else if(status.equals(Contents.RISK_LEVEL_LOW)){
					riskLevel = RiskGraphDrawBO.RISK_LEVEL_GREEN;
				}else{}
				m.put("riskLevel", riskLevel);
			}
			m.put("value", map.get("score"));
			resultList.add(m);
		}
		
		return resultList;
	}
	
	/**
	 * 查找风险的历史记录状态和趋势和风险水平
	 * Map,1个风险对应1个最新的历史记录
	 * @author zhengjunxiang
	 * @param idList 风险id集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findLatestRiskAdjustHistoryByRiskIds(List<String> idList){
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(idList.size()==0){	//ids数组为空
			return resultList;
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT h.id hid,r.id id,r.risk_name name,h.assessement_status status,dict.dict_entry_value trend,h.risk_status score")
		.append(" FROM t_rm_risks r")
		.append(" LEFT JOIN  t_rm_risk_adjust_history h on h.risk_id = r.id and h.is_latest='1'")
		.append(" LEFT JOIN  t_sys_dict_entry dict on dict.id = h.etrend")
		.append(" WHERE r.id in (:ids)");
		SQLQuery sqlQuery = o_riskAdjustHistoryDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameterList("ids", idList);
		List<Object[]> list = sqlQuery.list();
			
  		for(Object[] o : list){
  			Map<String,Object>  map = new HashMap<String,Object>();
  			map.put("id", o[1]);
  			map.put("name", o[2]);
  			map.put("status", o[3]);
  			map.put("trend", o[4]);
  			map.put("score", o[5]);
  			resultList.add(map);
  		}
  		
		return resultList;
	}

}
