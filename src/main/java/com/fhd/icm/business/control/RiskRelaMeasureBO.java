package com.fhd.icm.business.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.icm.assess.MeasureRelaRiskDAO;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.icm.control.MeasureRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.fdc.utils.Contents;
/**
 * 流程节点维护
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-3-11		下午1:17:50
 *
 * @see 	 
 */
@Service
@SuppressWarnings("unchecked")
public class RiskRelaMeasureBO{
	@Autowired
	private MeasureRelaRiskDAO o_measureRelaRiskDAO;
	/**
	 * <pre>
	 *    根据风险id获取该风险下所有的控制措施
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param riskId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<MeasureRelaRisk> findMeasurebyRiskId(String riskId){
		Criteria criteria = o_measureRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "risk");
		criteria.createAlias("controlMeasure", "controlMeasure");
		criteria.setFetchMode("controlMeasure", FetchMode.JOIN);
		criteria.add(Restrictions.eq("risk.id", riskId));
		criteria.add(Restrictions.eq("controlMeasure.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return criteria.list();
	}
	/**
	 * <pre>
	 *    根据控制措施获取该措施下所有的风险
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param measureId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<MeasureRelaRisk> findRiskbyMeasureId(String measureId){
		Criteria criteria = o_measureRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "risk");
		criteria.createAlias("controlMeasure", "controlMeasure");
		criteria.setFetchMode("risk", FetchMode.JOIN);
		criteria.add(Restrictions.eq("controlMeasure.id", measureId));
		criteria.add(Restrictions.eq("risk.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return criteria.list();
	}
	/**
	 * 根据风险list获取风险id和控制措施list的map
	 * @param riskList
	 * @return
	 */
	public Map<String,Object> getRiskMeasureMapByRiskList(List<Risk> riskList){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<String> riskIdList = new ArrayList<String>();
		for(Risk risk : riskList){
			riskIdList.add(risk.getId());
		}
		Criteria criteria = o_measureRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "risk");
		criteria.createAlias("controlMeasure", "controlMeasure");
		criteria.setFetchMode("risk", FetchMode.JOIN);
		criteria.add(Restrictions.in("risk.id", riskIdList));
		List<MeasureRelaRisk> measureRelaRiskList = criteria.list();
		for(Risk risk : riskList){
			List<Measure> measureList = new ArrayList<Measure>();
			for(MeasureRelaRisk measureRelaRisk : measureRelaRiskList){
				if(measureRelaRisk.getRisk().equals(risk)){
					measureList.add(measureRelaRisk.getControlMeasure());
				}
			}
			resultMap.put(risk.getId(), measureList);
		}
		return resultMap;
	}
}

