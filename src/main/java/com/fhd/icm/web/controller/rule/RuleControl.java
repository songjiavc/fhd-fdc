package com.fhd.icm.web.controller.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.icm.business.rule.RuleBO;
import com.fhd.icm.business.rule.RuleTreeBO;
import com.fhd.icm.web.form.RuleForm;


/**
 * @author   黄晨曦
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-12-19		上午10:09:18
 *
 * @see 	 
 */
@Controller
public class RuleControl {
	@Autowired
	private RuleTreeBO o_ruleTreeBO;
	
	@Autowired
	private RuleBO o_ruleBO;
	
	/**
	 * <pre>
	 *读取制度树数据
	 * </pre>
	 * 
	 * @author 黄晨曦
	 * @param node
	 * @param canChecked
	 * @param query
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/rule/ruleTree/ruletreeloader.f")
	public List<Map<String, Object>> findRuleTree(String node, Boolean canChecked, String query){
        return o_ruleTreeBO.findRuleTree(node, canChecked, query, null);
    }
	
	/**
	 * <pre>
	 *删除制度
	 * </pre>
	 * 
	 * @author 黄晨曦
	 * @param ruleID 需要删除的制度Id
	 * @since  fhd　Ver 1.1
	*/
	@RequestMapping("/icm/rule/removeRule.f")
	public void removeRuleByID(String ruleID){
		o_ruleBO.removeRuleByID(ruleID);
	} 
	
	@ResponseBody
	@RequestMapping("/icm/rule/saveRule.f")
	public Map<String,Object> saveRule(RuleForm ruleForm){
		Map<String,Object> result=new HashMap<String,Object>();
		o_ruleBO.saveRule(ruleForm);
		result.put("success", true);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/icm/rule/editRule.f")
	public Map<String, Object> findRuleByID(String ruleid,String ruleparentid){
		return o_ruleBO.findRuleByID(ruleid,ruleparentid);
	}
	
	@ResponseBody
	@RequestMapping("/icm/rule/loadruleforview.f")
	public Map<String, Object> loadRuleById(String ruleId,String ruleParentId){
		return o_ruleBO.findRuleByIdForView(ruleId,ruleParentId);
	}
	
	@ResponseBody
	@RequestMapping("/icm/rule/findRuleByIds.f")
	public List<Map<String, Object>> findRuleByIds(String[] ids){
		return o_ruleBO.findRuleByIds(ids);
	}
	
	@ResponseBody
	@RequestMapping("/icm/rule/findRuleCode.f")
	public Map<String, Object> findRuleCode(String ruleid,String ruleparentid){
		return o_ruleBO.findRuleCode(ruleid,ruleparentid);
	}
}