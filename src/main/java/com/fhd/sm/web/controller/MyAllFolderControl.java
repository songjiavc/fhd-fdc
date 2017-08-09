package com.fhd.sm.web.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.CategoryBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiMemo;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sm.web.form.KpiForm;
import com.fhd.sm.web.form.KpiMemoForm;

/**
 * 我的文件夹控制类
 * ClassName:MyAllFolderControl
 * @author   郝静
 * @version  
 * @since    Ver 1.1
 * @Date     2013   2013-9-22       下午13:50:34
 *
 * @see
 */
@Controller
public class MyAllFolderControl {
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
    @Autowired
    private StrategyMapBO o_kpiStrategyMapBO;
    @Autowired
    private CategoryBO o_kpiCategoryMapBO;

    /**<pre>
     * 我的文件夹树展现
     * </pre>
     * 
     * @param node 节点信息
     * @param query 查询条件
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/deptfolder/deptfoldertreeloader.f")
    public List<Map<String, Object>> myFolderTreeLoader(String node, String query,HttpServletRequest request) {

        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();//返回前台的nodelist
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_KPI")){
        	Map<String, Object> allKpiNode = new HashMap<String, Object>();
            allKpiNode.put("dbid", "deptkpi");
            allKpiNode.put("id", "deptkpi");
            allKpiNode.put("text", "部门指标");
            allKpiNode.put("type", "deptkpi");
            allKpiNode.put("leaf", true);
            allKpiNode.put("iconCls", "icon-ibm-icon-owner");
            nodes.add(allKpiNode);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_TARGET")){
        	Map<String, Object> smNode = new HashMap<String, Object>();
            smNode.put("id", "deptsm");
            smNode.put("dbid", "deptsm");
            smNode.put("text", "部门目标");
            smNode.put("type", "deptsm");
            smNode.put("leaf", true);
            smNode.put("iconCls", "icon-strategy");
            nodes.add(smNode);
        }
        /**
         * @date 	2017-3-20 
         * @author 	宋佳
         * @desc 	去掉部门积分卡功能
         */
        /*
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_CATEGORY")){
        	Map<String, Object> scNode = new HashMap<String, Object>();
            scNode.put("id", "deptsc");
            scNode.put("dbid", "deptsc");
            scNode.put("text", "部门记分卡");
            scNode.put("type", "deptsc");
            scNode.put("leaf", true);
            scNode.put("iconCls", "icon-ibm-icon-scorecards");
            nodes.add(scNode);
        }
        */
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_RISK")){
        	Map<String, Object> allRiskNode = new HashMap<String, Object>();
            allRiskNode.put("dbid", "deptrisk");
            allRiskNode.put("id", "deptrisk");
            allRiskNode.put("text", "部门风险");
            allRiskNode.put("type", "deptrisk");
            allRiskNode.put("leaf", true);
            allRiskNode.put("iconCls", "icon-ibm-icon-metrics");
            nodes.add(allRiskNode);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_PROCESS")){
        	Map<String, Object> orgProcess = new HashMap<String, Object>();
            orgProcess.put("dbid", "orgProcess");
            orgProcess.put("id", "orgProcess");
            orgProcess.put("text", "部门流程");
            orgProcess.put("type", "orgProcess");
            orgProcess.put("leaf", true);
            orgProcess.put("iconCls", "icon-application");
            nodes.add(orgProcess);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_RESPONS")){
        	Map<String, Object> riskResponse = new HashMap<String, Object>();
            riskResponse.put("dbid", "riskResponse");
            riskResponse.put("id", "riskResponse");
            riskResponse.put("text", "部门应对");
            riskResponse.put("type", "riskResponse");
            riskResponse.put("leaf", true);
            riskResponse.put("iconCls", "icon-table");
            nodes.add(riskResponse);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_HISTORYEVENT")){
        	Map<String, Object> orgHistory = new HashMap<String, Object>();
            orgHistory.put("dbid", "deptHistory");
            orgHistory.put("id", "deptHistory");
            orgHistory.put("text", "部门历史事件");
            orgHistory.put("type", "orgHistory");
            orgHistory.put("leaf", true);
            orgHistory.put("iconCls", "icon-report");
            nodes.add(orgHistory);
        }
        
        
        return nodes;

    }
    
    /**
     * <pre>
     * 查询部门关联的指标信息
     * </pre>
     * 
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param query
     *            查询条件
     * @param id
     *            指标ID
     * @param sort
     *            排序字段
     * @param year
     *            年
     * @param quarter
     *            季度
     * @param month
     *            月
     * @param week
     *            周
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/myfolder/finddeptrelakpi.f")
    public Map<String, Object> findDeptRelaKpiResult(int start, int limit, String query, String sort, String id, String year, String quarter,
            String month, String week, String eType, String isNewValue) {
        year = ",".equals(year) ? "" : year;
        quarter = ",".equals(quarter) ? "" : quarter;
        month = ",".equals(month) ? "" : month;
        week = ",".equals(week) ? "" : week;
        eType = ",".equals(eType) ? "" : eType;
        isNewValue = ",".equals(isNewValue) ? "" : isNewValue;
        if (null != year && year.contains(",")) {
            year = year.replace(",", "");
        }
        if (null != quarter && quarter.contains(",")) {
            quarter = quarter.replace(",", "");
        }
        if (null != month && month.contains(",")) {
            month = month.replace(",", "");
        }
        if (null != week && week.contains(",")) {
            week = week.replace(",", "");
        }
        if (null != eType && eType.contains(",")) {
            eType = eType.replace(",", "");
        }
        if (null != isNewValue && isNewValue.contains(",")) {
            isNewValue = isNewValue.replace(",", "");
        }
        String lastflag = isNewValue;
        String frequence = eType;
        String dir = "ASC";
        String sortColumn = "assessmentStatus";// 默认排序
        KpiForm form = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        Page<Kpi> page = new Page<Kpi>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        List<Object[]> gatherDatas = null;
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");// 按照哪个字段排序
                dir = jsobj.getString("direction");// 排序方向
            }
        }
    	String deptId = UserContext.getUser().getMajorDeptId();
        if ("true".equals(lastflag)) {// 最新值查询
            gatherDatas = o_kpiGatherResultBO.findLastKpiGatherByBelongDeptId(map, deptId,"B",start, limit, query, sortColumn, dir);
        }
        else {// 具体频率查询
            gatherDatas = o_kpiGatherResultBO.findSpecificKpiGatherByBelongDeptId(map, deptId, "B", start, limit, query, sortColumn, dir, year, quarter, month, week, frequence);
        }
        
        Map<String,KpiMemo> memoMap = new HashMap<String, KpiMemo>();
        List<String> gatherResultIdList = new ArrayList<String>();
        if(null != gatherDatas){
        	for (Object[] objects : gatherDatas) {
        		if(null!=objects[11]){
        			gatherResultIdList.add(String.valueOf(objects[11]));
        		}
        	}
        }
        
        if(gatherResultIdList.size()>0){
        	memoMap = o_kpiMemoBO.findMemoByKpiGatherIds(gatherResultIdList);
        }
        
        if (null != gatherDatas) {
            for (Object[] objects : gatherDatas) {
            	String kgrid = String.valueOf(objects[11]);
        		form = new KpiForm(objects, "allkpi");
        		if(memoMap.containsKey(kgrid)){
        			KpiMemo kpiMemo = memoMap.get(kgrid);
        			KpiMemoForm kpiMemoForm = new KpiMemoForm(kpiMemo);
        			form.setMemoStr(kpiMemoForm.getMemoStr());
        		}
        		datas.add(form);
            }
        }
        
        
        map.put("datas", datas);
        return map;
    }
    
    /**
     * <pre>
     * 查询部门关联的目标、记分卡
     * </pre>
     * @author 郝静
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param query
     *            查询条件
     * @param id
     *            指标ID
     * @param sort
     *            排序字段
     * @param year
     *            年
     * @param quarter
     *            季度
     * @param month
     *            月
     * @param week
     *            周
     * @return
     */
    @ResponseBody
    @RequestMapping("/myfolder/finddeptrelasmsc.f")
    public Map<String, Object> findDeptRelaSmScResult(int start, int limit, String query, String sort, String id, String year, String quarter,
            String month, String week, String eType, String isNewValue,String dataType) {
        year = ",".equals(year) ? "" : year;
        quarter = ",".equals(quarter) ? "" : quarter;
        month = ",".equals(month) ? "" : month;
        week = ",".equals(week) ? "" : week;
        eType = ",".equals(eType) ? "" : eType;
        isNewValue = ",".equals(isNewValue) ? "" : isNewValue;
        dataType = ",".equals(dataType) ? "" : dataType;
        if (null != year && year.contains(",")) {
            year = year.replace(",", "");
        }
        if (null != quarter && quarter.contains(",")) {
            quarter = quarter.replace(",", "");
        }
        if (null != month && month.contains(",")) {
            month = month.replace(",", "");
        }
        if (null != week && week.contains(",")) {
            week = week.replace(",", "");
        }
        if (null != eType && eType.contains(",")) {
            eType = eType.replace(",", "");
        }
        if (null != isNewValue && isNewValue.contains(",")) {
            isNewValue = isNewValue.replace(",", "");
        }
        if (null != dataType && dataType.contains(",")) {
        	dataType = dataType.replace(",", "");
        }
        String lastflag = isNewValue;
        String frequence = eType;
        String dir = "ASC";
        //String sortColumn = "parentName";// 默认排序
        String sortColumn = "assessmentStatus";// 默认排序
        KpiForm form = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        Page<Kpi> page = new Page<Kpi>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        List<Object[]> gatherDatas = null;
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");// 按照哪个字段排序
                dir = jsobj.getString("direction");// 排序方向
            }
        }
    	String deptId = UserContext.getUser().getMajorDeptId();
    	
    	if("sm".equalsIgnoreCase(dataType)){
            if ("true".equals(lastflag)) {// 最新值查询
                gatherDatas = o_kpiStrategyMapBO.findLastStrategyMapBelongDeptId(map, deptId,"B",start, limit, query, sortColumn, dir);
            }
            else {// 具体频率查询
                gatherDatas = o_kpiStrategyMapBO.findSpecificStrategyMapBelongDeptId(map, deptId, "B", start, limit, query, sortColumn, dir, year, quarter, month, week, frequence);
            }
            if (null != gatherDatas) {
                for (Object[] objs : gatherDatas) {
                	form = new KpiForm();
                	form.setId((String) objs[0]);
                	form.setName((String) objs[1]);
                   	if(null!=objs[2]){
	            		form.setDateRange((String) objs[2]);
	            	}
                	if(null!=objs[3]){
                		form.setAssessmentValue(new DecimalFormat("0.00").format((BigDecimal) objs[3]));
                	}
                	form.setAssessmentStatus((String) objs[4]);
                	form.setDirectionstr((String) objs[5]);
                	form.setStatusStr((String) objs[6]);
                	form.setIsFocus((String) objs[7]);
	            	if(null!=objs[9]){
	            		form.setOwerName((String) objs[9]);
	            	}
	            	if(null!=objs[10]){
	            		form.setParentName((String) objs[10]);
	            	}
	            	if(null!=objs[11]){
	            		form.setParentKpiId(String.valueOf(objs[11]));
	            	}
	            	if(null!=objs[12]){
	            		form.setTimeperiod(String.valueOf(objs[12]));
	            	}
	            	if(null!=objs[13]){
	            		form.setKgrId(String.valueOf(objs[13]));
	            	}
                    datas.add(form);
                }
            }
    		
    	}else if("sc".equalsIgnoreCase(dataType)){            
    		if ("true".equals(lastflag)) {// 最新值查询
	            gatherDatas = o_kpiCategoryMapBO.findLastCategoryBelongDeptId(map, deptId,"B",start, limit, query, sortColumn, dir);
	        }
	        else {// 具体频率查询
	            gatherDatas = o_kpiCategoryMapBO.findSpecificCategoryBelongDeptId(map, deptId, "B", start, limit, query, sortColumn, dir, year, quarter, month, week, frequence);
	        }
	        if (null != gatherDatas) {
	            for (Object[] objs : gatherDatas) {
	            	form = new KpiForm();
	            	form.setId((String) objs[0]);
	            	form.setName((String) objs[1]);
	            	if(null!=objs[2]){
	            		form.setDateRange((String) objs[2]);
	            	}
	            	if(null!=objs[3]){
	            		form.setAssessmentValue(new DecimalFormat("0.00").format((BigDecimal) objs[3]));
	            	}
	            	form.setAssessmentStatus((String) objs[4]);
	            	form.setDirectionstr((String) objs[5]);
	            	form.setStatusStr((String) objs[6]);
	            	form.setIsFocus((String) objs[7]);
	            	if(null!=objs[9]){
	            		form.setOwerName((String) objs[9]);
	            	}
	            	if(null!=objs[10]){
	            		form.setParentName((String) objs[10]);
	            	}
	            	if(null!=objs[11]){
	            		form.setParentKpiId(String.valueOf(objs[11]));
	            	}
	            	if(null!=objs[12]){
	            		form.setTimeperiod(String.valueOf(objs[12]));
	            	}
	            	if(null!=objs[13]){
	            		form.setKgrId(String.valueOf(objs[13]));
	            	}
	                datas.add(form);
	            }
	        }
    	}
    	
        map.put("datas", datas);
        return map;
    }

}
