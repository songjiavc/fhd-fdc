package com.fhd.comm.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.TimePeriodBO;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.RelaAssessResultBO;

@Controller
public class GatherDataControl {

    @Autowired
    private TimePeriodBO o_timePeriodBO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;

    /**时间区间表中所有年份信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/kpi/data/findyeardata.f")
    public Map<String, Object> findYearData() {
        Map<String, Object> item = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<TimePeriod> timeList = o_timePeriodBO.findAllYearData();
        for (TimePeriod timePeriod : timeList) {
            item = new HashMap<String, Object>();
            item.put("id", timePeriod.getId());
            item.put("text", timePeriod.getTimePeriodFullName());
            list.add(item);
        }
        map.put("datas", list);
        return map;
    }

    /**生成指定年份指标采集数据
     * @param year 年信息
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping(value = "/kpi/data/generationkpigatherdata.f")
    public void generationKpiGatherData(String year, HttpServletResponse response) throws IOException {
        boolean result = true;
        o_kpiGatherResultBO.generationKpiGatherData(year);
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
    }

    /**生成指定年份战略目标采集数据
     * @param year 年信息
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping(value = "/kpi/data/generationsmgatherdata.f")
    public void generationSmGatherData(String year, HttpServletResponse response) throws IOException {
        boolean result = true;
        o_relaAssessResultBO.generationSmGatherData(year);
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
    }

    /**生成指定年份记分卡采集数据
     * @param year 年信息
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping(value = "/kpi/data/generationscgatherdata.f")
    public void generationScGatherData(String year, HttpServletResponse response) throws IOException {
        boolean result = true;
        o_relaAssessResultBO.generationScGatherData(year);
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
    }

}
