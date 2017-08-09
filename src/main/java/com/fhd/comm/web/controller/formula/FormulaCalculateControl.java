package com.fhd.comm.web.controller.formula;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.comm.business.formula.FormulaCalculateBO;
import com.fhd.comm.business.formula.FormulaObjectRelationBO;
import com.fhd.comm.business.formula.FormulaReCalculateBO;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.comm.formula.FormulaObjectRelation;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.RelaAssessResult;
import com.fhd.entity.kpi.StrategyMap;

/**
 * 公式计算Controller.
 * @author   吴德福
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-28		上午10:51:15
 * @see 	 
 */
@Controller
public class FormulaCalculateControl {
	
	@Autowired
	private FormulaCalculateBO o_formulaCalculateBO;
	@Autowired
	private FormulaReCalculateBO o_formulaReCalculateBO;
	@Autowired
	private KpiGatherResultBO o_kpiGatherResultBO;
	@Autowired
	private FormulaObjectRelationBO o_formulaObjectRelationBO;
	@Autowired
	private KpiBO o_kpiBO;
	@Autowired
	private CategoryBO o_categroyBO;
	@Autowired
	private StrategyMapBO o_strategyMapBO;
	@Autowired
	private TimePeriodBO o_timePeriodBO;
	@Autowired
	private RelaAssessResultBO o_relaAssessResultBO;
	
	/**
	 * 验证公式.
	 * @param id 要计算的指标或风险id
	 * @param type kpi或risk
	 * @param objectColumn 对象所属列
	 * @param formula 公式
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("/formula/validateFormula.f")
	public void validateFormula(String id, String type, String objectColumn, String formula, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		if(StringUtils.isBlank(formula)){
		    try{
		        out.print("true");
		    }catch (Exception e) {
	            e.printStackTrace();
	            out.print("false");
	        }finally{
	            if(null != out){
	                out.close();
	            }
	        }
		}
		String relaObjectId = "";
		String relaObjectType = "";
		String relaObjectColumn = "";
		
		try {
			String targetName = "";
			if("kpi".equals(type)){
				Kpi kpi = o_kpiBO.findKpiById(id);
				if(null != kpi){
					targetName = kpi.getName();
				}
			}else if("category".equals(type)){
				//记分卡
				Category category = o_categroyBO.findCategoryById(id);
				if(null != category){
					targetName = category.getName();
				}
			}else if("strategy".equals(type)){
				//战略目标
				StrategyMap strategyMap = o_strategyMapBO.findById(id);
				if(null != strategyMap){
					targetName = strategyMap.getName();
				}
			}else if("risk".equals(type)){
				//风险
				
			}
			
			if("kpi".equals(type)){
				//指标
				/**
				 * 后台验证循环嵌套
				 * Map<String, String> map格式：{name:'',type:'',originalName:''}
				 */
				List<String> nameList = new ArrayList<String>();
				List<Map<String, String>> result = o_formulaCalculateBO.decompositionFormulaString(targetName, formula);
				for(Map<String, String> m : result){
					String name = m.get("name");
					if(!nameList.contains(name)){
						nameList.add(name);
					}
				}
				/*
				 * 根据指标名称批量查询对应的指标采集结果list
				 * @param nameList
				 */
				List<KpiGatherResult> kpiGatherResultList = o_kpiGatherResultBO.findKpiGatherResultListByKpiNames(nameList, "");
				for(Map<String, String> map : result){
					for(KpiGatherResult kpiGatherResult : kpiGatherResultList){
						//指标id
						if(null != kpiGatherResult.getKpi()){
							relaObjectId = kpiGatherResult.getKpi().getId();
						}
						relaObjectType = "kpi";
						relaObjectColumn = "";
						if(Contents.TARGET_VALUE.equals(map.get("type"))){
							relaObjectColumn = "targetValueFormula";
						}else if(Contents.RESULT_VALUE.equals(map.get("type"))){
							relaObjectColumn = "resultValueFormula";
						}else if(Contents.ASSEMENT_VALUE.equals(map.get("type"))){
							relaObjectColumn = "assessmentValueFormula";
						}
						
						/*
						 * 判断是否存在循环嵌套,存在循环嵌套返回错误提示
						 * 例如:
						 * a).若kpiA%结果值 = kpiB + kpiC, kpiC = kpiA%结果值 - 50, 则存在循环调用；
						 * b).若kpiA%结果值  = kpiA%结果值*80%，则存在循环调用
						 * 说明：若kpiA%结果值  = kpiA%目标值*80%，则不存在循环调用
						 */
						//a情况验证
						List<FormulaObjectRelation> formulaObjectRelationList = o_formulaObjectRelationBO.findFormulaObjectRelationListBySome(relaObjectId, relaObjectType, relaObjectColumn, id, type, objectColumn);
						if(null != formulaObjectRelationList && formulaObjectRelationList.size()>0){
							out.print("false");
							return;
						}
						//b情况验证
						if("id".equals(relaObjectId) && "type".equals(relaObjectType) && "objectColumn".equals(relaObjectColumn)){
							out.print("false");
							return;
						}
					}
				}
			}else if("category".equals(type)){
				//记分卡
				
			}else if("strategy".equals(type)){
				//战略目标
				
			}else if("risk".equals(type)){
				//风险
				
			}
			
			out.print("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.print("false");
		}finally{
			if(null != out){
				out.close();
			}
		}
	}
	@ResponseBody
	@RequestMapping("/formula/reformulacalculate.f")
	public  Map<String, Object> formulaReCalculate(String kpiItems) throws ParseException{
	    Map<String, Object> result = new HashMap<String, Object>();
	    if(StringUtils.isNotBlank(kpiItems)){
	        Map<String,String> map = new HashMap<String, String>();
	        JSONArray items = JSONArray.fromObject(kpiItems);
	        for (Object obj : items) {
                JSONObject jsobj = JSONObject.fromObject(obj);
                //if(StringUtils.isNotBlank(jsobj.getString("timeperiod"))){
                    map.put(jsobj.getString("kpiId"), jsobj.getString("timeperiod"));
                //}
            }
	        o_formulaReCalculateBO.formulaCalculate(map);
	    }
	    result.put("success", true);
	    return result;
	}
	/**记分卡和战略目标历史数据计算
	 * @param items
	 * @param type
	 * @param objectId
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/formula/recategoryformulacalculate.f")
	public Map<String, Object> categoryFormulaReCalculate(String items,String type,String objectId){
	    Map<String, Object> result = new HashMap<String, Object>();
	    if("sc".equals(type)){
	        type = "category";
	    }else{
	    	type="strategy";
	    }
        if(StringUtils.isNotBlank(items)){
        	String lastTimePeriodId = "";
        	TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome("");
        	String timePeriodId = timePeriod.getId();
            Map<String,String> map = new HashMap<String, String>();
            JSONArray gatherItems = JSONArray.fromObject(items);
            for (Object obj : gatherItems) {
                JSONObject jsobj = JSONObject.fromObject(obj);
                String tmpTimePeriod = jsobj.getString("timeperiod");
                if(tmpTimePeriod.equals(timePeriodId)){
                	lastTimePeriodId = tmpTimePeriod;
                }
                if(StringUtils.isNotBlank(jsobj.getString("timeperiod"))){
                    map.put(jsobj.getString("objectId"), jsobj.getString("timeperiod"));
                }
            }
            o_formulaReCalculateBO.categoryformulaCalculate(objectId,type,map,lastTimePeriodId);
        }
        result.put("success", true);
        return result;
	}
	/**部门记分卡和战略目标重新计算
	 * @param items
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/formula/deptcategoryformulacalculate.f")
	public Map<String, Object> deptCategoryFormulaCalculate(String items){
		Map<String, Object> result = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(items)){
			TimePeriod timePeriod = o_timePeriodBO.findTimePeriodBySome("");
        	String timePeriodId = timePeriod.getId();
			JSONArray gatherItems = JSONArray.fromObject(items);
			if(gatherItems.size()>0){
				for (Object obj : gatherItems) {
					String type="";
					String objectId = "";
					String gatherResultId = "";
					String lastTimePeriodId = "";
					Map<String,String> map = new HashMap<String, String>();
					JSONObject jsobj = JSONObject.fromObject(obj);
					if(StringUtils.isBlank(jsobj.getString("timeperiod"))){
						lastTimePeriodId = timePeriodId;
					}else{
						if(jsobj.getString("timeperiod").equals(timePeriodId)){
							lastTimePeriodId = jsobj.getString("timeperiod");
						}
					}
					type = jsobj.getString("type");
					objectId = jsobj.getString("id");
					gatherResultId = jsobj.getString("gatherResultId");
					if(StringUtils.isBlank(gatherResultId)){
						RelaAssessResult assessResult = o_relaAssessResultBO.findRelaAssessResultByIdAndTimePeriod(objectId,lastTimePeriodId);
						if(null!=assessResult){
							gatherResultId = assessResult.getId();
						}
					
					}
					if(StringUtils.isNotBlank(gatherResultId)){
						map.put(gatherResultId, lastTimePeriodId);
						o_formulaReCalculateBO.categoryformulaCalculate(objectId,type,map,lastTimePeriodId);
					}
				}
			}
			
		}
		result.put("success", true);
		return result;
	}
	/**
	 * 计算公式.
	 * @param id 要计算的指标或风险id
	 * @param type kpi或risk
	 * @param formula 公式
	 * @param timePeriodId 时间区间维id
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("/formula/calculateFormula.f")
	public void calculateFormula(String id, String type, String formula, String timePeriodId, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		
		String targetName = "";
		if("kpi".equals(type)){
			Kpi kpi = o_kpiBO.findKpiById(id);
			if(null != kpi){
				targetName = kpi.getName();
				try {
	                String ret = o_formulaCalculateBO.calculate(id,targetName, type, formula, timePeriodId,kpi.getCompany().getId());
	                
	                out.print(ret);
	            } catch (Exception e) {
	                e.printStackTrace();
	                out.print("计算失败");
	            }finally{
	                if(null != out){
	                    out.close();
	                }
	            }
			}
			
		}else if("category".equals(type)){
			//记分卡
			
		}else if("strategy".equals(type)){
			//战略目标
			
		}else if("risk".equals(type)){
			//风险
			
		}
		
	}
}
