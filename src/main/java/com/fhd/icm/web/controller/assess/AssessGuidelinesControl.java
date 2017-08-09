package com.fhd.icm.web.controller.assess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.icm.business.assess.AssessGuidelinesBO;
import com.fhd.entity.icm.assess.AssessGuidelines;
import com.fhd.entity.icm.assess.AssessGuidelinesProperty;
/**
 * 评价标准模板control
 * @author 邓广义
 */
@Controller
public class AssessGuidelinesControl {
	
	@Autowired
	private AssessGuidelinesBO o_assessGuidelinesBO;
	
	/**
	 * 通过ID查询实体
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/findAssessGuidelinesById.f")
	public AssessGuidelines findAssessGuidelinesById(String id){
		return o_assessGuidelinesBO.findAssessGuidelinesById(id);
	}
	/**
	 * 获得评价标准模板列表
	 * @author 郑广义
	 * @return List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/findAssessGuidelinesBySome.f")
	public List<Map<String,Object>> findAssessGuidelinesBySome(){
		List<Map<String,Object>> map = new ArrayList<Map<String,Object>>();
		List<AssessGuidelines> list = o_assessGuidelinesBO.findAssessGuidelinesBySome();
		for(AssessGuidelines ag : list){
			Map<String,Object> rowMap = new HashMap<String,Object>();
			rowMap.put("name", ag.getName());
			rowMap.put("comment", ag.getComment());
			rowMap.put("sort", ag.getSort());
			rowMap.put("dictype", ag.getType().getId());
			rowMap.put("id", ag.getId());
			map.add(rowMap);
		}
		return map;
	}
	/**
	 * 根据ID删除实体--逻辑删除
	 * @param id
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/delAssessGuidelinesById.f")
	public Map<String,Object> delAssessGuidelinesById(String id){
		 boolean b = this.o_assessGuidelinesBO.delAssessGuidelinesById(id);
		 Map<String,Object> map = new HashMap<String,Object>(0);
		 map.put("success", b);
		 return map;
	}
	/**
	 * 保存实体的方法
	 * @param modifyRecords
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/saveAssessGuidelines.f")
	public Map<String,Object> saveAssessGuidelines(String modifyRecords){
		boolean b = this.o_assessGuidelinesBO.saveAssessGuidelines(modifyRecords);
		Map<String,Object> map = new HashMap<String,Object>(0);
		map.put("success", b);
		return map;
	}
	/**
	 * 通过评价标准模板ID查询该模板对应的评价标准项.
	 * @author 郑广义
	 * @param AssessGuidelinesId
	 * @return List<AssessGuidelinesProperty>
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/findAssessGuidelinesPropertiesByAGId.f")
	public List<Map<String,Object>> findAssessGuidelinesPropertiesByAGId(String assessGuidelinesId){
		List<Map<String,Object>> map = new ArrayList<Map<String,Object>>();
		List<AssessGuidelinesProperty> list = o_assessGuidelinesBO.findAssessGuidelinesPropertiesByAGId(assessGuidelinesId);
		for(AssessGuidelinesProperty agp : list){
			Map<String,Object> rowMap = new HashMap<String,Object>();
			rowMap.put("minValue", agp.getMinValue());
			rowMap.put("maxValue", agp.getMaxValue());
			rowMap.put("judgeValue", agp.getJudgeValue());
			rowMap.put("sort", agp.getSort());
			rowMap.put("content", agp.getContent());
			rowMap.put("dictype", agp.getDefectLevel().getId());
			rowMap.put("parentName", agp.getAssessGuidelines().getName());
			rowMap.put("parentId", agp.getAssessGuidelines().getId());
			rowMap.put("id", agp.getId());
			map.add(rowMap);
		}
		return map;
	}
	/**
	 * 评价标准项保存实体的方法
	 * @param modifyRecords
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/saveAssessGuidelinesProperty.f")
	public Map<String,Object> saveAssessGuidelinesProperty(String modifyRecords){
		boolean b = this.o_assessGuidelinesBO.saveAssessGuidelinesProperty(modifyRecords);
		Map<String,Object> map = new HashMap<String,Object>(0);
		map.put("success", b);
		return map;
	}
	/**
	 * 根据ID删除实体(评价标准项删除)
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/delAssessGuidelinesPropertyById.f")
	public Map<String,Object> delAssessGuidelinesPropertyById(String ids){
		boolean b = this.o_assessGuidelinesBO.delAssessGuidelinesPropertyById(ids);
		Map<String,Object> map = new HashMap<String,Object>(0);
		map.put("success", b);
		return map;
	}
	/**
	 * 根据评价计划id查询对应的评价模板详细.
	 * @param assessPlanId 评价计划id
	 * @param limit
	 * @param start
	 * @param sort
	 * @param query
	 * @return Map<String, Object>
	 */
	@ResponseBody
	@RequestMapping("/icm/assess/baseset/findGuidelinesPropertyByAssessPlanId.f")
    public Map<String, Object> findGuidelinesPropertyByAssessPlanId(String assessPlanId, int limit, int start, String sort, String query) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

        Page<AssessGuidelinesProperty> page = new Page<AssessGuidelinesProperty>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        page = o_assessGuidelinesBO.findGuidelinesPropertyByAssessPlanId(page, assessPlanId, sort, query);
        List<AssessGuidelinesProperty> assessGuidelinesPropertyList = page.getResult();
        if (!assessGuidelinesPropertyList.isEmpty()) {
            Map<String, Object> row = null;
            for (AssessGuidelinesProperty agp : assessGuidelinesPropertyList) {
                row = new HashMap<String, Object>();
                //模板项id
                row.put("id", agp.getId());
                //模板项描述
                row.put("content", agp.getContent());
                //模板项类型
                if(null != agp.getDefectLevel()){
                	row.put("type", agp.getDefectLevel().getName());
                }else{
                	row.put("type", "");
                }
                AssessGuidelines assessGuidelines = agp.getAssessGuidelines();
                if(null != assessGuidelines){
                	//评价模板id
                	row.put("assessGuidelinesId", assessGuidelines.getId());
                	//评价模板名称
                	row.put("assessGuidelinesName", assessGuidelines.getName());
                	//评价模板类型
                	if(null != assessGuidelines.getType()){
                		row.put("assessGuidelinesType", assessGuidelines.getType().getName());
                	}else{
                		row.put("assessGuidelinesType", "");
                	}
                	//评价模板说明
                	row.put("assessGuidelinesComment", assessGuidelines.getComment());
                }
                row.put("planId", "");
                
                datas.add(row);
            }
            map.put("datas", datas);
            map.put("totalCount", page.getTotalItems());
        }else {
            map.put("datas", new Object[0]);
            map.put("totalCount", "0");
        }
        return map;
    }
}