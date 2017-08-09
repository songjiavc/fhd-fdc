package com.fhd.sm.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.sm.business.StrategyMapTreeBO;

@Controller
public class StrategyMapTreeControl {
    
    @Autowired
    private StrategyMapTreeBO o_strategyMapTreeBO;
    
    /**目标指标树
     * @param node
     * @param canChecked
     * @param query
     * @param smIconType
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpism/kpismtreeloader")
    public List<Map<String, Object>> kpiSmTreeLoader(String node, Boolean canChecked, String query, String smIconType,boolean showLight) {
        return o_strategyMapTreeBO.kpiSmTreeLoader(node, canChecked, query, null, smIconType,showLight);
    }

}
