package com.fhd.sm.web.controller.graph;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.sm.business.graph.KpiGraphDrawBO;
import com.fhd.sm.business.graph.ScGraphDrawBO;
import com.fhd.sm.business.graph.SmGraphDrawBO;

/**
 * 图形的亮灯状态Controller
 * 
 * @author 郝静
 * @version
 * @since Ver 1.1
 * @Date 2013 2013-10-29 上午11:26:34
 * 
 * @see
 */

@Controller
public class GraphDrawControl {


    @Autowired
    private KpiGraphDrawBO o_kpiGraphDrawBO;
	@Autowired
	private ScGraphDrawBO o_scGraphDrawDAO;
	@Autowired
    private SmGraphDrawBO o_smGraphDrawBO;
	
    /**指标获取graph图形的亮灯状态
     * @param kpiId 战略目标id
     * @return
     */
	@ResponseBody
	@RequestMapping("/kpigraphdraw/graph/showstatus.f")
	public Map<String, Object> kpiShowStatus(String kpiId){
		Map<String, Object> map = o_kpiGraphDrawBO.showStatus(kpiId);
		if(null != map){
			map.put("sucess", true);
		}else{
			map = new HashMap<String, Object>();
			map.put("sucess", false);
		}
		return map;
	}

	
    /**记分卡获取graph图形的亮灯状态
     * @param scId 战略目标id
     * @return
     */
	@ResponseBody
	@RequestMapping("/scgraphdraw/graph/showstatus.f")
	public Map<String, Object> scShowStatus(String scId){
		Map<String, Object> map = o_scGraphDrawDAO.showStatus(scId);
		if(null != map){
			map.put("sucess", true);
		}else{
			map = new HashMap<String, Object>();
			map.put("sucess", false);
		}
		return map;
	}
	
	
    /**目标获取graph图形的亮灯状态
     * @param smId 战略目标id
     * @return
     */
	@ResponseBody
	@RequestMapping("/smgraphdraw/graph/showstatus.f")
	public Map<String, Object> smShowStatus(String smId){
		Map<String, Object> map = o_smGraphDrawBO.showStatus(smId);
		if(null != map){
			map.put("sucess", true);
		}else{
			map = new HashMap<String, Object>();
			map.put("sucess", false);
		}
		return map;
	}
}
