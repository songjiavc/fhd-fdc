package com.fhd.sm.web.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.kpi.GatherData;
import com.fhd.entity.kpi.RealTimeKpi;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.web.form.risk.RiskEventForm;
import com.fhd.sm.business.RealTimeKpiBO;
import com.fhd.sm.web.form.RealTimeKpiForm;

/**
 * 实时指标control
 *
 */
@Controller
public class RealTimeKpiControl {

    private static Log loger = LogFactory.getLog(RealTimeKpiControl.class);

    @Autowired
    private RealTimeKpiBO o_realTimeKpiBO;

    /**根据指标id和code判断是否重复
     * @param items 指标id和指标code信息
     * @param response 响应对象
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/kpi/real/findrealtimekpibycode.f")
    public void findRealTimeKpiByCode(String items, HttpServletResponse response) throws IOException {
        boolean result = true;
        if (StringUtils.isNotBlank(items)) {
            String code = "";
            String kpiId = "";
            String companyId = UserContext.getUser().getCompanyid();
            JSONObject jsobj = JSONObject.fromObject(items);
            if (jsobj.containsKey("code")) {
                code = jsobj.getString("code");
            }
            if (jsobj.containsKey("kpiId")) {
                kpiId = jsobj.getString("kpiId");
            }
            RealTimeKpi realTimeKpi = o_realTimeKpiBO.findRealTimeKpiByCode(code, companyId, kpiId);
            if (null != realTimeKpi) {
                result = false;
            }
        }
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
    }
    
    /**根据指标id和name判断是否重复
     * @param items 指标id和指标name信息
     * @param response 响应对象
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/kpi/real/findrealtimekpibyname.f")
    public void findRealTimeKpiByName(String items, HttpServletResponse response) throws IOException {
        boolean result = true;
        if (StringUtils.isNotBlank(items)) {
            String name = "";
            String kpiId = "";
            String companyId = UserContext.getUser().getCompanyid();
            JSONObject jsobj = JSONObject.fromObject(items);
            if (jsobj.containsKey("name")) {
                name = jsobj.getString("name");
            }
            if (jsobj.containsKey("kpiId")) {
                kpiId = jsobj.getString("kpiId");
            }
            RealTimeKpi realTimeKpi = o_realTimeKpiBO.findRealTimeKpiByName(name, companyId, kpiId);
            if (null != realTimeKpi) {
                result = false;
            }
        }
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
    }

    /**查询实时指标图形
     * @param items 参数信息,如实时指标id
     * @param year 年信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/real/findrealtimekpihistorychart.f")
    public Map<String, Object> findRealTimeKpiHistoryChart(String items, String year) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("kpiName", "");
        result.put("unit", "");
        JSONArray resultArray = new JSONArray();
        Map<Long,String> descMap = new HashMap<Long, String>();
        if (StringUtils.isNotBlank(items)) {
            JSONObject jsobj = JSONObject.fromObject(items);
            if (jsobj.containsKey("kpiId")) {
                String kpiId = jsobj.getString("kpiId");
                RealTimeKpi realTimeKpi = o_realTimeKpiBO.findRealTimeKpiById(kpiId);
                String unit = "";
                if(null!=realTimeKpi.getUnits()){
                    unit = realTimeKpi.getUnits().getName();
                }
                result.put("unit",unit);
                result.put("kpiName", realTimeKpi.getName());
                List<GatherData> gatherList = o_realTimeKpiBO.findRealTimeKpiHistoryChart(kpiId, year);
                for (GatherData gatherData : gatherList) {
                    JSONArray item = new JSONArray();
                    Date times = gatherData.getTimes();
                    if (null != gatherData.getValue()) {
                        long time = DateUtils.toCalendar(times).getTimeInMillis() + 28800000;//加8小时
                        descMap.put(time, gatherData.getDesc());
                        item.add(time);
                        item.add(gatherData.getValue());
                        resultArray.add(item);
                    }
                }
            }
        }
        result.put("chartDatas", resultArray);
        result.put("descMap", descMap);
        result.put("success", true);
        return result;
    }

    /**查询实时指标列表
     * @param start 起始位置
     * @param limit 分页大小
     * @param query 查询条件
     * @param sort 排序信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/real/findrealtimekpibysome.f")
    public Map<String, Object> findRealTimeKpiBysome(int start, int limit, String query, String sort) {

        String dir = "ASC";
        String sortColumn = "id";
        RealTimeKpiForm realTimeKpiForm = null;
        if (StringUtils.isBlank(query)) {
            query = "";
        }
        List<RealTimeKpiForm> datas = new ArrayList<RealTimeKpiForm>();
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("query", query);
        paraMap.put("sort", sortColumn);
        paraMap.put("dir", dir);
        paraMap.put("companyId", UserContext.getUser().getCompanyid());
        Page<RealTimeKpi> page = new Page<RealTimeKpi>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        Map<String, Object> map = new HashMap<String, Object>();
        page = o_realTimeKpiBO.findRealTimeBySome(page, paraMap);
        List<RealTimeKpi> realTimeKpiList = page.getResult();
        for (RealTimeKpi realTimeKpi : realTimeKpiList) {
            realTimeKpiForm = new RealTimeKpiForm(realTimeKpi);
            datas.add(realTimeKpiForm);
        }
        map.put("totalCount", page.getTotalItems());
        map.put("datas", datas);
        return map;
    }

    /**根据实时指标id查询所有的历史数据
     * @param start 起始位置
     * @param limit 分页大小
     * @param query 查询条件
     * @param sort 排序信息
     * @param Items 前台传入参数信息 kpiId
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/real/findrealtimekpihistorydata.f")
    public Map<String, Object> findRealTimeKpiHistoryDataBysome(int start, int limit, String query, String sort, String kpiId) {
        String dir = "DESC";
        String sortColumn = "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isBlank(query)) {
            query = "";
        }
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("query", query);
        paraMap.put("sort", sortColumn);
        paraMap.put("dir", dir);
        paraMap.put("kpiId", kpiId);
        Page<GatherData> page = new Page<GatherData>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        Map<String, Object> map = new HashMap<String, Object>();
        page = o_realTimeKpiBO.findRealTimeHistoryBySome(page, paraMap);
        List<GatherData> list = page.getResult();
        List<RiskEventForm> datas = new ArrayList<RiskEventForm>();
        for (GatherData gatherData : list) {
            RiskEventForm form = new RiskEventForm();
            form.setId(gatherData.getId());
            if (null != gatherData.getDesc()) {
                form.setDesc(gatherData.getDesc());
            }
            else {
                form.setDesc("");
            }
            form.setDate(sf.format(gatherData.getTimes()));
            form.setValue(String.valueOf(gatherData.getValue()));
            if (null != gatherData.getStatus()) {
                form.setStatus(gatherData.getStatus().getValue());
            }
            else {
                form.setStatus("");
            }

            datas.add(form);
        }
        map.put("totalCount", page.getTotalItems());
        map.put("datas", datas);
        return map;
    }

    /**查询告警方案下拉框值
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/kpi/real/findalarmcomboxvalue.f")
    public Map<String, Object> findAlarmComboxValue() {
        Map<String, Object> item = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String type = "0alarm_type_kpi_forecast";
        List<AlarmPlan> alarmPlanList = o_realTimeKpiBO.findAlarmComboxValue(type);
        for (AlarmPlan alarmPlan : alarmPlanList) {
            item = new HashMap<String, Object>();
            item.put("id", alarmPlan.getId());
            item.put("text", alarmPlan.getName());
            list.add(item);
        }
        map.put("datas", list);
        return map;
    }

    /**根据id查询指标
     * @param kpiId 实时指标id
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/real/findrealtimekpi.f")
    public Map<String, Object> findRealTimeKpi(String kpiId) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(kpiId) && !Contents.ID_UNDEFINED.equals(kpiId)) {
            RealTimeKpi kpi = o_realTimeKpiBO.findRealTimeKpiById(kpiId);
            jsonMap.put("id", kpi.getId());
            jsonMap.put("name", kpi.getName());
            jsonMap.put("desc", kpi.getDesc());
            jsonMap.put("code", kpi.getCode());
            if(null!=kpi.getUnits()){
                jsonMap.put("unit", kpi.getUnits().getId());
            }else{
                jsonMap.put("unit", "");
            }
            if (null != kpi.getAlarmPlan()) {
                jsonMap.put("alarmId", kpi.getAlarmPlan().getId());
            }
            else {
                jsonMap.put("alarmId", "");
            }
            dataMap.put("data", jsonMap);
            dataMap.put("success", true);

        }
        return dataMap;
    }

    /**添加和修改实时指标信息
     * @param items 实时指标基本信息
     * @throws IOException 
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @ResponseBody
    @RequestMapping("/kpi/real/mergerealtimekpi.f")
    public void mergeRealTimeKpi(String items, HttpServletResponse response) throws IOException {
        boolean result = true;
        if (StringUtils.isNotBlank(items)) {
            try {
                JSONObject param = JSONObject.fromObject(items);
                RealTimeKpiForm realTimeKpiForm = new RealTimeKpiForm();
                BeanUtils.populate(realTimeKpiForm, param);
                String companyId = UserContext.getUser().getCompanyid();
                o_realTimeKpiBO.mergeRealTimeKpi(realTimeKpiForm, companyId);
            }
            catch (Exception e) {
                result = false;
                loger.error("保存实时指标失败,异常信息[" + e.toString() + "]");
                e.printStackTrace();
            }
            response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
        }
    }

    /**修改实时指标历史数据
     * @param items 前台修改的内容信息
     * @param response 响应对象
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping("/kpi/real/mergerealtimekpihistorydata.f")
    public void mergeRealTimeKpiHistoryData(String items, HttpServletResponse response) throws IOException {
        boolean result = true;
        if (StringUtils.isNotBlank(items)) {
            try {
                JSONObject param = JSONObject.fromObject(items);
                String kpiId = param.getString("kpiId");
                JSONArray datas = param.getJSONArray("datas");
                List<RiskEventForm> formList = new ArrayList<RiskEventForm>();
                for (Object object : datas) {
                    RiskEventForm form = new RiskEventForm();
                    JSONObject item = JSONObject.fromObject(object);
                    BeanUtils.populate(form, item);
                    formList.add(form);
                }
                o_realTimeKpiBO.mergeRealTimeKpiHistoryData(formList, kpiId);
            }
            catch (Exception e) {
                result = false;
                loger.error("保存实时指标历史数据失败,异常信息[" + e.toString() + "]");
                e.printStackTrace();
            }
            response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
        }
    }

    /**批量修改指标删除状态
     * @param items 要删除的指标id集合
     * @param response
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping("/kpi/real/moverealtimekpi.f")
    public void removeRealTimeKpi(String items, HttpServletResponse response) throws IOException {
        boolean result = true;
        if (StringUtils.isNotBlank(items)) {
            try {
                List<String> idList = new ArrayList<String>();
                JSONArray jsonArray = JSONArray.fromObject(items);
                for (Object kpiId : jsonArray) {
                    idList.add(String.valueOf(kpiId));
                }
                if (idList.size() > 0) {
                    o_realTimeKpiBO.removeRealTimeKpi(idList);
                }
            }
            catch (Exception e) {
                result = false;
                loger.error("更新实时指标删除状态,异常信息[" + e.toString() + "]");
                e.printStackTrace();
            }
            response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
        }
    }

    /**批量删除实时指标历史数据
     * @param items 要删除的指标采集id集合
     * @param response
     * @throws IOException 
     */
    @ResponseBody
    @RequestMapping("/kpi/real/moverealtimekpihistorydata.f")
    public void removeRealTimeKpiHistoryData(String items, HttpServletResponse response) throws IOException {
        boolean result = true;
        if (StringUtils.isNotBlank(items)) {
            try {
                List<String> idList = new ArrayList<String>();
                JSONObject jsobj = JSONObject.fromObject(items);
                String kpiId = jsobj.getString("kpiId");
                JSONArray jsonArray = jsobj.getJSONArray("datas");
                for (Object gatherId : jsonArray) {
                    idList.add(String.valueOf(gatherId));
                }
                if (idList.size() > 0) {
                    o_realTimeKpiBO.removeRealTimeKpiHistoryData(idList);

                    o_realTimeKpiBO.mergeRealKpiStatus(kpiId);
                }
            }
            catch (Exception e) {
                result = false;
                loger.error("删除实时指标采集数据,异常信息[" + e.toString() + "]");
                e.printStackTrace();
            }
            response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
        }
    }

}
