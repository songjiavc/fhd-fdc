package com.fhd.sm.business.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.interfaces.graph.IGraphDrawBO;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.kpi.KpiGatherResultDAO;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.sm.web.controller.util.Utils;

/**
 * 
 * 获取graph图形的亮灯状态BO
 * 
 * @author 郝静
 * @version
 * @since Ver 1.1
 * @Date 2013 2013-10-29 上午10:45:52
 * 
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class KpiGraphDrawBO implements IGraphDrawBO{

	@Autowired
    private KpiDAO o_kpiDAO;
	
    @Autowired
    private KpiGatherResultDAO o_kpiGatherResultDAO;

	/**
	 * <pre>
	 *	通过对象的ID获得其的最新的风险状态
	 *	map格式：{
	 *				id:'',//主对象的ID
	 *				riskLevel:RISK_LEVEL_YELLOW,//主对象的风险状态
	 *				value:3.41,//主对象的风险水平
	 *				name:''//主对象的名称
	 *			}
	 * </pre>
	 * 
	 * @author 郝静
	 * @param id 对象的ID
	 * @return Map<String,Object> 
	 * @since  fhd　Ver 1.1
	*/
	@Override
	public Map<String, Object> showStatus(String id) {

		Kpi kpi = null;
		KpiGatherResult kgr = null;
		Map<String,Object> map = new HashMap<String, Object>();
		String riskLevel = "";
		
        Criteria criteria = this.o_kpiDAO.createCriteria();
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.eq("deleteStatus", true));
        List<Kpi> kpilist = criteria.list();
        
        if (kpilist.size() > 0) {
            kpi = kpilist.get(0);
            Criteria criteriaR = this.o_kpiGatherResultDAO.createCriteria();
            criteriaR.add(Restrictions.eq("kpi.id",id));
            if(null!=kpi.getLastTimePeriod()){
            	criteriaR.add(Restrictions.eq("timePeriod.id",kpi.getLastTimePeriod().getId()));
                
                List<KpiGatherResult> kgrlist = criteriaR.list();
                if(kgrlist.size()>0){
                	kgr = kgrlist.get(0);
                	if(null!=kgr.getAssessmentStatus()){//判断风险状态是否空
                		riskLevel = kgr.getAssessmentStatus().getId();
    		           	if ("0alarm_startus_l".equals(riskLevel)) {
    		           		 riskLevel = "green";
    		                }
    	                if ("0alarm_startus_m".equals(riskLevel)) {
    	               	 riskLevel = "yellow";
    	                }
    	                if ("0alarm_startus_h".equals(riskLevel)) {
    	               	 riskLevel = "red";
    	                }
    	                map.put("riskLevel", riskLevel);//主风险水平
                	}
                	if(null!=kgr.getAssessmentValue()){//判断风险水平是否为空
                		map.put("value", Utils.getValue(kgr.getAssessmentValue().toString()));//主对象风险水平，保留小数点后两位
                	}
                }
            }
        	map.put("id", id);//主对象id
        	map.put("name", kpi.getName());//主对象名称
        }
        return map;
	}
	/**
	 * <pre>
	 * 批量通过对象的ID获得其的最新的风险状态
	 *	map格式：{
	 *				id:'',//主对象的ID
	 *				riskLevel:RISK_LEVEL_YELLOW,//主对象的风险状态
	 *				value:3.41,//主对象的风险水平
	 *				name:''//主对象的名称
	 *			}
	 * </pre>
	 * 
	 * @author 郝静
	 * @param idList 对象的idList
	 * @return List<Map<String,String>> 
	 * @since  fhd　Ver 1.1
	*/
	@Override
	public List<Map<String, Object>> showStatus(List<String> idList) {

		String id = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String riskLevel = "";
		for(int i = 0 ; i < idList.size() ; i++){
			id = idList.get(i);
			Kpi kpi = null;
			KpiGatherResult kgr = null;
			Map<String,Object> map = new HashMap<String, Object>();
	        Criteria criteria = this.o_kpiDAO.createCriteria();
	        criteria.add(Restrictions.eq("id", id));
	        criteria.add(Restrictions.eq("deleteStatus", true));
	        List<Kpi> kpilist = criteria.list();
	        
	        if (kpilist.size() > 0) {
	            kpi = kpilist.get(0);
	            Criteria criteriaR = this.o_kpiGatherResultDAO.createCriteria();
	            criteriaR.add(Restrictions.eq("kpi.id",id));
	            if(null!=kpi.getLastTimePeriod()){
	            	criteriaR.add(Restrictions.eq("timePeriod.id",kpi.getLastTimePeriod().getId()));
		            
		            List<KpiGatherResult> kgrlist = criteriaR.list();
		            if(kgrlist.size()>0){
		            	kgr = kgrlist.get(0);
		            	if(null!=kgr.getAssessmentStatus()){//判断风险状态是否空
		            		riskLevel = kgr.getAssessmentStatus().getId();
				           	if ("0alarm_startus_l".equals(riskLevel)) {
				           		 riskLevel = "green";
				                }
			                if ("0alarm_startus_m".equals(riskLevel)) {
			               	 riskLevel = "yellow";
			                }
			                if ("0alarm_startus_h".equals(riskLevel)) {
			               	 riskLevel = "red";
			                }
			                map.put("riskLevel", riskLevel);//主风险水平
		            	}
		            	if(null!=kgr.getAssessmentValue()){//判断风险水平是否为空
		            		map.put("value", Utils.getValue(kgr.getAssessmentValue().toString()));//主对象风险水平，保留小数点后两位
		            	}
		            }
	            }
	            map.put("id", id);//主对象id
	        	map.put("name", kpi.getName());//主对象名称
	        }
	        list.add(map);
		}
		return list;
	}

}
