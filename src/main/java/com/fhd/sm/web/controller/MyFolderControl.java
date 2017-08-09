package com.fhd.sm.web.controller;

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

import com.fhd.core.dao.Page;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiMemo;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.web.form.KpiForm;
import com.fhd.sm.web.form.KpiMemoForm;

/**
 * 我的文件夹控制类
 * ClassName:MyFolderControl
 * @author   陈晓哲
 * @version  
 * @since    Ver 1.1
 * @Date     2012   2012-12-26       下午12:50:34
 *
 * @see
 */
@Controller
public class MyFolderControl {
    @Autowired
    private KpiBO o_kpiBO;
    @Autowired
    private KpiMemoBO o_kpiMemoBO;

    /**<pre>
     * 我的文件夹树展现
     * </pre>
     * 
     * @author 陈晓哲
     * @param node 节点信息
     * @param query 查询条件
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/myfolder/myfoldertreeloader.f")
    public Map<String, Object> myFolderTreeLoader(String node, String query,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();//返回前台的nodelist
        List<Map<String, Object>> myFolderChild = new ArrayList<Map<String, Object>>();//返回前台的nodelist
        /**
         * 王再冉添加我的待办，我的已办节点
         */
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_MY_TASK")){
        	Map<String, Object> myTodoNode = new HashMap<String, Object>();
            myTodoNode.put("id", "myTodo");
            myTodoNode.put("text", "我的待办");
            myTodoNode.put("type", "myfolder");
            myTodoNode.put("leaf", true);
            myTodoNode.put("iconCls", "icon-nstep");
            myFolderChild.add(myTodoNode);
        }
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_MY_DONETASK")){
        	Map<String, Object> myDoneNode = new HashMap<String, Object>();
            myDoneNode.put("id", "myDone");
            myDoneNode.put("text", "我的已办");
            myDoneNode.put("type", "myfolder");
            myDoneNode.put("leaf", true);
            myDoneNode.put("iconCls", "icon-nstep");
            myFolderChild.add(myDoneNode);
        }
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_MY_KPI")){
        	Map<String, Object> allKpiNode = new HashMap<String, Object>();
            allKpiNode.put("id", "allkpi");
            allKpiNode.put("text", "我的指标");
            allKpiNode.put("type", "myfolder");
            allKpiNode.put("leaf", true);
            allKpiNode.put("iconCls", "icon-nstep");
            myFolderChild.add(allKpiNode);
        }
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_MY_KPI")){
            Map<String, Object> allRiskNode = new HashMap<String, Object>();
            allRiskNode.put("id", "myrisk");
            allRiskNode.put("text", "我的风险");
            allRiskNode.put("type", "myfolder");
            allRiskNode.put("leaf", true);
            allRiskNode.put("iconCls", "icon-nstep");
            myFolderChild.add(allRiskNode);
        }
        
        Map<String, Object> myFolderNode = new HashMap<String, Object>();
        myFolderNode.put("id", "myfolder_root");
        myFolderNode.put("text", "我的文件夹");
        myFolderNode.put("type", "myfolder_root");
        myFolderNode.put("leaf", false);
        myFolderNode.put("expanded", true);
        myFolderNode.put("type", "myfolder");
        myFolderNode.put("children", myFolderChild);
        nodes.add(myFolderNode);

        List<Map<String, Object>> deptChild = new ArrayList<Map<String, Object>>();//返回前台的nodelist
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_KPI")){
            Map<String, Object> allKpiNode = new HashMap<String, Object>();
            allKpiNode.put("dbid", "deptkpi");
            allKpiNode.put("id", "deptkpi");
            allKpiNode.put("text", "部门指标");
            allKpiNode.put("type", "deptkpi");
            allKpiNode.put("leaf", true);
            allKpiNode.put("iconCls", "icon-nstep");
            deptChild.add(allKpiNode);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_TARGET")){
            Map<String, Object> smNode = new HashMap<String, Object>();
            smNode.put("id", "deptsm");
            smNode.put("dbid", "deptsm");
            smNode.put("text", "部门目标");
            smNode.put("type", "deptsm");
            smNode.put("leaf", true);
            smNode.put("iconCls", "icon-nstep");
            deptChild.add(smNode);
        }
        /*
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_CATEGORY")){
            Map<String, Object> scNode = new HashMap<String, Object>();
            scNode.put("id", "deptsc");
            scNode.put("dbid", "deptsc");
            scNode.put("text", "部门记分卡");
            scNode.put("type", "deptsc");
            scNode.put("leaf", true);
            scNode.put("iconCls", "icon-nstep");
            deptChild.add(scNode);
        }
        */
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_RISK")){
            Map<String, Object> allRiskNode = new HashMap<String, Object>();
            allRiskNode.put("dbid", "deptrisk");
            allRiskNode.put("id", "deptrisk");
            allRiskNode.put("text", "部门风险");
            allRiskNode.put("type", "deptrisk");
            allRiskNode.put("leaf", true);
            allRiskNode.put("iconCls", "icon-nstep");
            deptChild.add(allRiskNode);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_PROCESS")){
            Map<String, Object> orgProcess = new HashMap<String, Object>();
            orgProcess.put("dbid", "orgProcess");
            orgProcess.put("id", "orgProcess");
            orgProcess.put("text", "部门流程");
            orgProcess.put("type", "orgProcess");
            orgProcess.put("leaf", true);
            orgProcess.put("iconCls", "icon-nstep");
            deptChild.add(orgProcess);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_RESPONS")){
            Map<String, Object> riskResponse = new HashMap<String, Object>();
            riskResponse.put("dbid", "riskResponse");
            riskResponse.put("id", "riskResponse");
            riskResponse.put("text", "部门应对");
            riskResponse.put("type", "riskResponse");
            riskResponse.put("leaf", true);
            riskResponse.put("iconCls", "icon-nstep");
            deptChild.add(riskResponse);
        }
        
        if(request.isUserInRole("ROLE_ALL_WORK_FILE_DEP_HISTORYEVENT")){
            Map<String, Object> orgHistory = new HashMap<String, Object>();
            orgHistory.put("dbid", "deptHistory");
            orgHistory.put("id", "deptHistory");
            orgHistory.put("text", "部门历史事件");
            orgHistory.put("type", "orgHistory");
            orgHistory.put("leaf", true);
            orgHistory.put("iconCls", "icon-nstep");
            deptChild.add(orgHistory);
        }
        
        Map<String, Object> deptNode = new HashMap<String, Object>();
        deptNode.put("id", "deptmyfolder_root");
        deptNode.put("text", "部门文件夹");
        deptNode.put("type", "deptmyfolder_root");
        deptNode.put("leaf", false);
        deptNode.put("expanded", true);
        deptNode.put("type", "department");
        deptNode.put("children", deptChild);
        nodes.add(deptNode);
        
        map.put("children", nodes);
        return map;

    }
    /**<pre>
     * 所有度量指标树展现
     * </pre>
     * 
     * @author 郝静
     * @param node 节点信息
     * @param query 查询条件
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/myfolder/allkpitreeloader.f")
    public List<Map<String, Object>> allKpiTreeLoader(String node, String query) {

        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();//返回前台的nodelist
        Map<String, Object> allKpiNode = new HashMap<String, Object>();
        allKpiNode.put("id", "all_metric_kpi");
        allKpiNode.put("text", "所有度量指标");
        allKpiNode.put("type", "all_metric_kpi");
        allKpiNode.put("leaf", true);
        allKpiNode.put("iconCls", "icon-ibm-icon-owner");
        nodes.add(allKpiNode);
        return nodes;

    }

    /**
     * <pre>
     * 查询所有指标关联的采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param start 分页起始位置
     * @param limit 显示记录数
     * @param query 查询条件
     * @param id 指标ID
     * @param sort 排序字段
     * @return
     * @since  fhd　Ver 1.1
    */
    @ResponseBody
    @RequestMapping("/kpi/myfolder/findallkpirelaresult.f")
    public Map<String, Object> findAllkpiRelaResult(int start, int limit, String query, String sort, String year, String quarter, String month,
            String week, String eType, String isNewValue) {
        String lastflag = isNewValue;
        String frequence = eType;
        String dir = "ASC";
        String sortColumn = "assessmentStatus";//默认排序
        KpiForm form = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        Page<Kpi> page = new Page<Kpi>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
        List<Object[]> gatherDatas = null;
        if ("true".equals(lastflag)) {//最新值查询
            gatherDatas = o_kpiBO.findLastGatherResults(map, start, limit, query, sortColumn, dir, false);
        }
        else {//具体频率查询
            gatherDatas = o_kpiBO.findSpecificGatherResults(map, start, limit, query, sortColumn, dir, year, quarter, month, week, frequence, false);
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
     * 查询我的指标关联的采集结果
     * </pre>
     * 
     * @author 陈晓哲
     * @param start 分页起始位置
     * @param limit 显示记录数
     * @param query 查询条件
     * @param id 指标ID
     * @param sort 排序字段
     * @return
     * @since  fhd　Ver 1.1
    */
    @ResponseBody
    @RequestMapping("/kpi/myfolder/findmykpirelaresult.f")
    public Map<String, Object> findMykpiRelaResult(int start, int limit, String query, String sort, String year, String quarter, String month,
            String week, String eType, String isNewValue) {
        String lastflag = isNewValue;
        String frequence = eType;
        String dir = "ASC";
        String sortColumn = "assessmentStatus";//默认排序
        KpiForm form = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<KpiForm> datas = new ArrayList<KpiForm>();
        Page<Kpi> page = new Page<Kpi>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
        List<Object[]> gatherDatas = null;
        if ("true".equals(lastflag)) {//最新值查询
            gatherDatas = o_kpiBO.findLastGatherResults(map, start, limit, query, sortColumn, dir, true);
        }
        else {//具体频率查询
            gatherDatas = o_kpiBO.findSpecificGatherResults(map, start, limit, query, sortColumn, dir, year, quarter, month, week, frequence, true);
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
        		form = new KpiForm(objects, "mykpi");
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
}
