package com.fhd.ra.web.controller.risk.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiRelaCategory;
import com.fhd.entity.kpi.SmRelaKpi;
import com.fhd.entity.risk.KpiRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.business.risk.analysis.RiskAnalysisBO;
import com.fhd.sm.business.StrategyMapBO;


@Controller
public class RiskAnalysisControl {

    @Autowired
    private RiskAnalysisBO o_riskAnalysisBO;
    
    @Autowired
    private RiskCmpBO o_riskCmpBO;
    
    @Autowired
    private StrategyMapBO o_strategyMapBO;
    
    /**
     * 风险分析显示grid
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-8-27
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value="risk/analysis/findriskanalysis.f")
    public Map<String,Object> findRiskAnalysis(int start, int limit, String query, String sort, String smid){
        String property = "";
        String direction = "";
        Page<SmRelaKpi> page = new Page<SmRelaKpi>();

        page.setPageNo(1);
        page.setPageSize(10000);

        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                property = jsobj.getString("property");
                direction = jsobj.getString("direction");
            }
        } else {
            property = "kpi.name";
            direction = "ASC";
        }
        page = o_riskAnalysisBO.findRiskAnalysis(query, page, property, direction, smid);
        List<SmRelaKpi> srelaList = page.getResult();
        Set<Kpi> kpiSet = new HashSet<Kpi>();
        for(SmRelaKpi srk : srelaList){
            kpiSet.add(srk.getKpi());
        }
        Map<String,Set<Risk>> riskSetByKpiMap = new HashMap<String,Set<Risk>>();
        Set<String> riskAllSet = new HashSet<String>();//所有风险，用于查询风险状态
        for(Kpi kpi : kpiSet){
            Set<KpiRelaRisk> krelarSet = kpi.getKpiRelaRisks();
            Set<Risk> riskSet = new HashSet<Risk>();
            for(KpiRelaRisk krelar : krelarSet ){
                Risk risk = krelar.getRisk();
                if(risk != null && risk.getIsRiskClass().equals("re")){
                    riskSet.add(krelar.getRisk());
                    riskAllSet.add(krelar.getRisk().getId());
                }
            }
            riskSetByKpiMap.put(kpi.getId(), riskSet);
        }
        String[] ids = new String[riskAllSet.size()];
        int riskNum = 0;
        for(String riskid : riskAllSet){
            ids[riskNum] = riskid;
            riskNum++;
        }
        //获取风险事件的最新历史记录
        Map<String,Object> historyMap = o_riskCmpBO.findLatestRiskAdjustHistoryByRiskIds(ids);
        
        List<Map<String, Object>> alldatas = new ArrayList<Map<String, Object>>();
        
        for(Kpi kpi : kpiSet){
            Set<Risk> riskSet = riskSetByKpiMap.get(kpi.getId());
            if(riskSet != null && riskSet.size() != 0){
                for(Risk risk : riskSet){
                    Map<String,Object> data = new HashMap<String,Object>();
                    Set<KpiRelaRisk> krelarSet = risk.getKpiRelaRisks();
                    StringBuffer kpiname = new StringBuffer();//风险指标
                    data.put("id", kpi.getId()+"_"+risk.getId());
                    data.put("aimTarget", kpi.getName());
                    data.put("riskname", risk.getName());
                    for(KpiRelaRisk krelar : krelarSet){
                        if(krelar.getType().equals("RM")){
                            kpiname.append(krelar.getKpi().getName());
                            kpiname.append(",");
                        }
                    }
                    data.put("riskMeasure", StringUtils.isBlank(kpiname.toString())?"":kpiname.toString().substring(0, kpiname.toString().length()-1));
                    
                    //状态和趋势
                    RiskAdjustHistory history = (RiskAdjustHistory)historyMap.get(risk.getId());
                    if(history==null){
                        data.put("assessementStatus","");
                    }else{
                        data.put("assessementStatus",history.getAssessementStatus());
                    }
                    alldatas.add(data);
                }
            }else{//非影响指标
                Map<String,Object> data = new HashMap<String,Object>();
                data.put("id", kpi.getId()+"_");
                data.put("aimTarget", kpi.getName());
                data.put("riskname", "");
                data.put("riskMeasure", "");
                data.put("assessementStatus","");
                alldatas.add(data);
            }
        }
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        int pageNo = limit == 0 ? 1 : start / limit+1;
        int pageSize = limit;
        int allNum = alldatas.size();
        int allPage = allNum%pageSize==0?allNum/pageSize:allNum/pageSize+1;
        if(pageNo<allPage){
            //不是最后一页
            for(int i=0;i<pageSize;i++){
                datas.add(alldatas.get((pageNo-1)*limit + i));
            }
        }else{
            pageSize = allNum%pageSize;
            for(int i=0;i<pageSize;i++){
                datas.add(alldatas.get((pageNo-1)*limit + i));
            }
        }
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("totalCount", alldatas.size());
        map.put("datas", datas);
        return map;
    }
    
    /**
     * 根据影响指标查询所关联的风险
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-8-27
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value="/risk/analysis/findinflukpi.f")
    public Map<String,Object> findInfluKpi(String kpiid,String smid){
        List<KpiRelaRisk> krealrList = o_riskAnalysisBO.findInfluKpi(kpiid,smid);
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        for(KpiRelaRisk krealr : krealrList){
            Risk risk = krealr.getRisk();
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("id", risk.getId());
            data.put("code", risk.getCode());
            data.put("name", risk.getName());
            datas.add(data);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("datas", datas);
        return map;
    }
    
    /**
     * 风险关联方法
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-8-28
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value="/risk/analysis/savekpirelarisk.f")
    public Map<String,Object> saveKpiRelaRisk(String kpiid,String riskids,String smid){
        o_riskAnalysisBO.saveKpiRelaRisk(kpiid,riskids,smid);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return map;
    }
    
    /**
     * 风险分析显示grid
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-8-27
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value="risk/analysis/findscriskanalysis.f")
    public Map<String,Object> findScRiskAnalysis(int start, int limit, String query, String sort, String categoryid){
        String property = "";
        String direction = "";
        Page<KpiRelaCategory> page = new Page<KpiRelaCategory>();

        page.setPageNo(1);
        page.setPageSize(10000);

        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                property = jsobj.getString("property");
                direction = jsobj.getString("direction");
            }
        } else {
            property = "kpi.name";
            direction = "ASC";
        }
        page = o_riskAnalysisBO.findScRiskAnalysis(query, page, property, direction, categoryid);
        List<KpiRelaCategory> krelacList = page.getResult();
        Set<Kpi> kpiSet = new HashSet<Kpi>();
        for(KpiRelaCategory krc : krelacList){
            kpiSet.add(krc.getKpi());
        }
        Map<String,Set<Risk>> riskSetByKpiMap = new HashMap<String,Set<Risk>>();
        Set<String> riskAllSet = new HashSet<String>();//所有风险，用于查询风险状态
        for(Kpi kpi : kpiSet){
            Set<KpiRelaRisk> krelarSet = kpi.getKpiRelaRisks();
            Set<Risk> riskSet = new HashSet<Risk>();
            for(KpiRelaRisk krelar : krelarSet ){
                Risk risk = krelar.getRisk();
                if(risk != null && risk.getIsRiskClass().equals("re")){
                    riskSet.add(krelar.getRisk());
                    riskAllSet.add(krelar.getRisk().getId());
                }
            }
            riskSetByKpiMap.put(kpi.getId(), riskSet);
        }
        String[] ids = new String[riskAllSet.size()];
        int riskNum = 0;
        for(String riskid : riskAllSet){
            ids[riskNum] = riskid;
            riskNum++;
        }
        //获取风险事件的最新历史记录
        Map<String,Object> historyMap = o_riskCmpBO.findLatestRiskAdjustHistoryByRiskIds(ids);
        
        List<Map<String, Object>> alldatas = new ArrayList<Map<String, Object>>();
        
        for(Kpi kpi : kpiSet){
            Set<Risk> riskSet = riskSetByKpiMap.get(kpi.getId());
            if(riskSet != null && riskSet.size() != 0){
                for(Risk risk : riskSet){
                    Map<String,Object> data = new HashMap<String,Object>();
                    Set<KpiRelaRisk> krelarSet = risk.getKpiRelaRisks();
                    StringBuffer kpiname = new StringBuffer();//风险指标
                    data.put("id", kpi.getId()+"_"+risk.getId());
                    data.put("aimTarget", kpi.getName());
                    data.put("riskname", risk.getName());
                    for(KpiRelaRisk krelar : krelarSet){
                        if(krelar.getType().equals("RM")){
                            kpiname.append(krelar.getKpi().getName());
                            kpiname.append(",");
                        }
                    }
                    data.put("riskMeasure", StringUtils.isBlank(kpiname.toString())?"":kpiname.toString().substring(0, kpiname.toString().length()-1));
                    
                    //状态和趋势
                    RiskAdjustHistory history = (RiskAdjustHistory)historyMap.get(risk.getId());
                    if(history==null){
                        data.put("assessementStatus","");
                    }else{
                        data.put("assessementStatus",history.getAssessementStatus());
                    }
                    alldatas.add(data);
                }
            }else{//非影响指标
                Map<String,Object> data = new HashMap<String,Object>();
                data.put("id", kpi.getId()+"_");
                data.put("aimTarget", kpi.getName());
                data.put("riskname", "");
                data.put("riskMeasure", "");
                data.put("assessementStatus","");
                alldatas.add(data);
            }
        }
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        int pageNo = limit == 0 ? 1 : start / limit+1;
        int pageSize = limit;
        int allNum = alldatas.size();
        int allPage = allNum%pageSize==0?allNum/pageSize:allNum/pageSize+1;
        if(pageNo<allPage){
            //不是最后一页
            for(int i=0;i<pageSize;i++){
                datas.add(alldatas.get((pageNo-1)*limit + i));
            }
        }else{
            pageSize = allNum%pageSize;
            for(int i=0;i<pageSize;i++){
                datas.add(alldatas.get((pageNo-1)*limit + i));
            }
        }
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("totalCount", alldatas.size());
        map.put("datas", datas);
        return map;
    }
    
    /**
     * 记分卡根据影响指标查询所关联的风险
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-8-27
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value="/risk/analysis/findscinflukpi.f")
    public Map<String,Object> findScInfluKpi(String kpiid,String categoryid){
        List<KpiRelaRisk> krealrList = o_riskAnalysisBO.findScInfluKpi(kpiid,categoryid);
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        for(KpiRelaRisk krealr : krealrList){
            Risk risk = krealr.getRisk();
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("id", risk.getId());
            data.put("code", risk.getCode());
            data.put("name", risk.getName());
            datas.add(data);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("datas", datas);
        return map;
    }
    
    /**
     * 记分卡风险关联方法
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-8-28
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value="/risk/analysis/savesckpirelarisk.f")
    public Map<String,Object> savescKpiRelaRisk(String kpiid,String riskids,String categoryid){
        o_riskAnalysisBO.saveScKpiRelaRisk(kpiid,riskids,categoryid);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return map;
    }
    
    
    
}