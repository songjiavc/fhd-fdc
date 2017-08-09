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
import com.fhd.dao.comm.CategoryDAO;
import com.fhd.dao.kpi.RelaAssessResultDAO;
import com.fhd.entity.comm.Category;
import com.fhd.entity.kpi.RelaAssessResult;
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
public class ScGraphDrawBO implements IGraphDrawBO{

	@Autowired
    private CategoryDAO o_categoryDAO;
	
    @Autowired
    private RelaAssessResultDAO o_relaAssessResultDAO;

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
	 * @return Map<String, Object> 
	 * @since  fhd　Ver 1.1
	*/
	@Override
	public Map<String, Object> showStatus(String id) {

		Category sc = null;
		RelaAssessResult scgr = null;
		Map<String, Object> map = new HashMap<String, Object>();
		String riskLevel = "";
		
        Criteria criteria = this.o_categoryDAO.createCriteria();
        criteria.add(Restrictions.eq("id", id));
        criteria.add(Restrictions.eq("deleteStatus", true));
        List<Category> sclist = criteria.list();
        
        if (sclist.size() > 0) {
            sc = sclist.get(0);
            Criteria criteriaR = this.o_relaAssessResultDAO.createCriteria();
            criteriaR.add(Restrictions.eq("objectId",id));
            criteriaR.add(Restrictions.eq("dataType","sc"));
            if(null!=sc.getTimePeriod()){
            	 criteriaR.add(Restrictions.eq("timePeriod.id",sc.getTimePeriod().getId()));
                 
                 List<RelaAssessResult> scgrlist = criteriaR.list();
                 if(scgrlist.size()>0){
                 	scgr = scgrlist.get(0);
                 	if(null!=scgr.getAssessmentStatus()){//判断风险状态是否空
                 		riskLevel = scgr.getAssessmentStatus().getId();
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
                 	if(null!=scgr.getAssessmentValue()){//判断风险水平是否为空
                 		map.put("value", Utils.getValue(scgr.getAssessmentValue().toString()));//主对象风险水平，保留小数点后两位
                 	}
                 }
            }
            map.put("id", id);//主对象id
        	map.put("name", sc.getName());//主对象名称
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
	 * @return List<Map<String, Object>> 
	 * @since  fhd　Ver 1.1
	*/
	@Override
	public List<Map<String, Object>> showStatus(List<String> idList) {

		String id = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String riskLevel = "";
		for(int i = 0 ; i < idList.size() ; i++){
			id = idList.get(i);
			Category sc = null;
			RelaAssessResult scgr = null;
			Map<String, Object> map = new HashMap<String, Object>();
			
	        Criteria criteria = this.o_categoryDAO.createCriteria();
	        criteria.add(Restrictions.eq("id", id));
	        criteria.add(Restrictions.eq("deleteStatus", true));
	        List<Category> sclist = criteria.list();
	        
	        if (sclist.size() > 0) {
	            sc = sclist.get(0);
	            Criteria criteriaR = this.o_relaAssessResultDAO.createCriteria();
	            criteriaR.add(Restrictions.eq("objectId",id));
	            criteriaR.add(Restrictions.eq("dataType","sc"));
	            if(null!=sc.getTimePeriod()){
	            	criteriaR.add(Restrictions.eq("timePeriod.id",sc.getTimePeriod().getId()));
		            
		            List<RelaAssessResult> scgrlist = criteriaR.list();
		            if(scgrlist.size()>0){
		            	scgr = scgrlist.get(0);
		            	if(null!=scgr.getAssessmentStatus()){//判断风险状态是否空
		            		riskLevel = scgr.getAssessmentStatus().getId();
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
		            	if(null!=scgr.getAssessmentValue()){//判断风险水平是否为空
		            		map.put("value", Utils.getValue(scgr.getAssessmentValue().toString()));//主对象风险水平，保留小数点后两位
		            	}
		            }
	            }
	            map.put("id", id);//主对象id
	        	map.put("name", sc.getName());//主对象名称
	        }
	        list.add(map);
		}
		return list;
	}

}
