package com.fhd.sm.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.web.form.AlarmRegionForm;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.AlarmRegion;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiMemo;
import com.fhd.entity.kpi.KpiRelaOrgEmp;
import com.fhd.entity.kpi.SmRelaAlarm;
import com.fhd.entity.kpi.SmRelaDim;
import com.fhd.entity.kpi.SmRelaKpi;
import com.fhd.entity.kpi.SmRelaTheme;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.business.RelaAssessResultBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sm.business.StrategyMapTreeBO;
import com.fhd.sm.web.form.KpiForm;
import com.fhd.sm.web.form.KpiMemoForm;
import com.fhd.sm.web.form.SmRelaAlarmForm;
import com.fhd.sm.web.form.SmRelaKpiForm;
import com.fhd.sm.web.form.StrategyMapForm;
import com.fhd.sys.business.dic.DictBO;

/**
 * KPI_战略目标Controller ClassName:KpiStrategyMapController
 * 
 * @author 杨鹏
 * @version
 * @since Ver 1.1
 * @Date 2012 2012-8-15 下午3:11:34
 * 
 * @see
 */
@Controller
public class StrategyMapControl {
    @Autowired
    private StrategyMapBO o_kpiStrategyMapBO;

    @Autowired
    private StrategyMapTreeBO o_kpiStrategyMapTreeBO;

    @Autowired
    private StrategyMapBO o_strategyMapBO;

    @Autowired
    private DictBO o_dictEntryBO;

    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;
    
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
       
    @Autowired
    private AlarmPlanBO o_alarmPlanBO;
    
    /**
     * 目标树 treeLoader:
     * 
     * @author 王鑫
     * @param id
     *            节点id
     * @param canChecked
     *            是否有复选框
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/StrategyMapTreeSelector/treeloader")
    public List<Map<String, Object>> treeLoader(String node, Boolean canChecked, String query, String smIconType,String invalidSmId,String isExpand) {
        return o_kpiStrategyMapTreeBO.treeLoader(node, canChecked, query, null, smIconType,invalidSmId,"expand");
    }
    
    /**
     * 目标树 treeLoader:
     * 
     * @author 张帅
     * @param id
     *            节点id
     * @param canChecked
     *            是否有复选框
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiStrategyMapTree/treeloader")
    public List<Map<String, Object>> treeLoader(String node, Boolean canChecked, String query, String smIconType) {
        return o_kpiStrategyMapTreeBO.treeLoader(node, canChecked, query, null, smIconType);
    }
    
    /**关注的目标树展示
     * @param node 节点ID
     * @param canChecked  是否有复选框
     * @param query  查询条件
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiStrategyMapTree/focustreeloader")
    public List<Map<String, Object>> focusTreeLoader(String node, Boolean canChecked, String query) {
    	return o_kpiStrategyMapTreeBO.focusTreeLoader(node, canChecked, query, null);
    }

    /**
     * 我的目标树 treeLoader:
     * 
     * @author 张帅
     * @param id
     *            节点id
     * @param canChecked
     *            是否有复选框
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiStrategyMapTree/minetreeloader")
    public List<Map<String, Object>> mineTreeLoader(String node, Boolean canChecked, String query, String smIconType) {
        return o_kpiStrategyMapTreeBO.mineTreeLoader(node, canChecked, query, smIconType);
    }

    /**
     * <pre>
     * 根据战略目标ID查询图表类型
     * </pre>
     * @param response 响应对象
     * @param id 记分卡ID
     * @throws IOException
     */
    @RequestMapping("/kpi/strategyMap/findStrategyMapById.f")
    public void findStrategyMapById(HttpServletResponse response, String id) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        StrategyMap strategyMap = o_strategyMapBO.findById(id);
        StringBuffer chartType = new StringBuffer();
        if (null != strategyMap && StringUtils.isNotBlank(strategyMap.getChartType())) {
            String[] chartIdArray = strategyMap.getChartType().split(",");
            Arrays.sort(chartIdArray);
            int i = 0;
            for (String chartId : chartIdArray) {
                chartType.append(chartId);
                if (i != chartIdArray.length - 1) {
                    chartType.append(",");
                }
                i++;
            }
        }
        result.put("chartType", chartType.toString());
        result.put("success", true);
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
    }

    /**
     * 机构目标树 treeLoader:
     * 
     * @author 张帅
     * @param id
     *            节点id
     * @param canChecked
     *            是否有复选框
     * @param query
     *            查询条件
     * @param smIcon
     *            'manager'表示管理,'display':表示显示
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiStrategyMapTree/orgtreeloader")
    public List<Map<String, Object>> orgTreeLoader(String node, String type, Boolean canChecked, String query, String smIconType) {
        return o_kpiStrategyMapTreeBO.orgTreeLoader(node, type, canChecked, query, smIconType);
    }
    
    /**
     * 根据传入目标ID生成导航信息
     * @param id 目标ID
     * @return 生成的导航数组信息
     */
    @ResponseBody
    @RequestMapping(value = "/kpi/kpistrategymap/findsmnavgationinfo.f")
    public List<Map<String, Object>> findSmNavInfo(String id) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	StrategyMap strategyMap = o_strategyMapBO.findById(id);
    	Map<String, String> smMap = o_strategyMapBO.findSmIdAndNameMap(UserContext.getUser().getCompanyid());
	    String idSeq =  strategyMap.getIdSeq();
	    String[] ids = idSeq.split("\\.");
    	for(String smId: ids) {
    		if(StringUtils.isNotBlank(smId)) {
    			Map<String, Object> map = new HashMap<String,Object>();
    			map.put("smId", smId);
    			map.put("smName", smMap.get(smId));
    			list.add(map);
    		}      		
    	}
    	return list;
    }

    /**
     * 根据id查询目标
     * 
     * @author 张帅
     * @param id
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiStrategyMap/findSmById")
    public List<Map<String, Object>> findSmById(String[] ids) {
        return o_kpiStrategyMapBO.findStrategyById(ids);
    }

    /**
     * <pre>
     * 目标查看 - 编辑菜单
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            目标ID
     * @return 前台接收的json
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findsmbyidtojson.f")
    public Map<String, Object> findSmByIdToJson(String id) {
        DictEntry entry = null;
        StringBuffer smDim = new StringBuffer();
        StringBuffer smTheme = new StringBuffer();
        JSONArray otherdimArr = new JSONArray();
        JSONArray otherThemeArr = new JSONArray();
        Map<String, Object> mutiJson = new HashMap<String, Object>();
        if (StringUtils.contains(id, "_")) {
            id = id.split("_")[1];// 机构目标树,传递过来的id为orgid_smid
        }
        StrategyMap sm = o_kpiStrategyMapBO.findById(id);
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != sm) {
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put("code", sm.getCode());
            jsonMap.put("name", sm.getName());
            jsonMap.put("shortName", sm.getShortName());
            jsonMap.put("warningFormula", sm.getWarningFormula());
            jsonMap.put("assessmentFormula", sm.getAssessmentFormula());
            jsonMap.put("desc", sm.getDesc());
            if (null != sm.getStatus()){
                jsonMap.put("estatus", sm.getStatus().getId());
            }
            if(StringUtils.isNotBlank(sm.getIsFocus())){
                jsonMap.put("isfocustr", sm.getIsFocus());
            }
            jsonMap.put("currentSmId", sm.getId());
            jsonMap.put("id", sm.getId());
            Set<SmRelaDim> smRelaDimSet = sm.getSmRelaDims();
            for (SmRelaDim smRelaDim : smRelaDimSet) {
                entry = smRelaDim.getSmDim();
                if (null != entry) {
                    if (Contents.DUTY_DEPARTMENT.equals(smRelaDim.getType())) {
                        smDim.append(entry.getId()).append(",");
                    }
                    else {
                        otherdimArr.add(entry.getId());
                    }
                }
            }
            if (smDim.length() > 0) {
                jsonMap.put("mainDim", smDim.toString().substring(0, smDim.length() - 1));
            }
            mutiJson.put("otherDim", otherdimArr.toString());
            Set<SmRelaTheme> smRelaThemeSet = sm.getSmRelaThemes();
            for (SmRelaTheme smRelaTheme : smRelaThemeSet) {
                entry = smRelaTheme.getTheme();
                if (null != entry) {
                    if (Contents.DUTY_DEPARTMENT.equals(smRelaTheme.getType())) {
                        smTheme.append(entry.getId()).append(",");
                    }
                    else {
                        otherThemeArr.add(entry.getId());
                    }
                }
            }
            if (smTheme.length() > 0) {
                jsonMap.put("mainTheme", smTheme.toString().substring(0, smTheme.length() - 1));
            }
            mutiJson.put("otherTheme", otherThemeArr.toString());
            if (null != sm.getParent()) {
                jsonMap.put("parentId", sm.getParent().getId());
                jsonMap.put("parentname", sm.getParent().getName());
                
            }

            if (null != sm.getChartType()) {
                jsonMap.put("chartTypeStr", sm.getChartType());
                jsonMap.put("charttypehidden", sm.getChartType());
            }

            /* 部门和人员信息 */
            JSONObject orgEmpObj = this.o_kpiStrategyMapBO.findSmRelaOrgEmpBySmToJson(sm);
            jsonMap.put("ownDept", orgEmpObj.getString("ownDept"));
            jsonMap.put("reportDept", orgEmpObj.getString("reportDept"));
            jsonMap.put("viewDept", orgEmpObj.getString("viewDept"));
            map.put("data", jsonMap);
            map.put("multiSelect", mutiJson);
            map.put("success", true);
        }

        return map;
    }
    /**
     * <pre>
     * 目标查看 - 编辑菜单
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            目标ID
     * @return 前台接收的json
     * @since fhd　Ver 1.1
     */
    @SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("/kpi/kpistrategymap/loadsmDetailbyid.f")
    public Map<String, Object> loadSmDeatilById(String id) {
        DictEntry entry = null;
        StringBuffer smDim = new StringBuffer();
        StringBuffer otherdim = new StringBuffer();
        StringBuffer smTheme = new StringBuffer();
        StringBuffer otherTheme = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		StrategyMap sm = o_kpiStrategyMapBO.findById(id);
		 if (null != sm) {
			 map.put("code", sm.getCode());
			 map.put("name", sm.getName());
			 map.put("shortName", sm.getShortName());
			 map.put("warningFormula", sm.getWarningFormula());
			 map.put("assessmentFormula", sm.getAssessmentFormula());
			 map.put("desc", sm.getDesc());
	         if (null != sm.getStatus()){
	            	map.put("estatus", sm.getStatus().getName());
	            }
	            if(StringUtils.isNotBlank(sm.getIsFocus())){
	            	map.put("isfocustr", sm.getIsFocus());
	            }
	         //主维度和辅助维度
	            Set<SmRelaDim> smRelaDimSet = sm.getSmRelaDims();
	            for (SmRelaDim smRelaDim : smRelaDimSet) {
	                entry = smRelaDim.getSmDim();
	                if (null != entry) {
	                    if (Contents.DUTY_DEPARTMENT.equals(smRelaDim.getType())) {
	                        smDim.append(entry.getName()).append(",");
	                    }
	                    else {
	                    	otherdim.append(entry.getName()).append(",");
	                    }
	                }
	            }
	            if (smDim.length() > 0) {
	                map.put("mainDim", smDim.toString().substring(0, smDim.length() - 1));
	            }
	            if (otherdim.length() > 0) {
	            	 map.put("otherDim", otherdim.toString().substring(0, otherdim.length() - 1));
	            }
	            // 主题与辅助主题
	            Set<SmRelaTheme> smRelaThemeSet = sm.getSmRelaThemes();
	            for (SmRelaTheme smRelaTheme : smRelaThemeSet) {
	                entry = smRelaTheme.getTheme();
	                if (null != entry) {
	                    if (Contents.DUTY_DEPARTMENT.equals(smRelaTheme.getType())) {
	                        smTheme.append(entry.getName()).append(",");
	                    }
	                    else {
	                    	otherTheme.append(entry.getName()).append(",");
	                    }
	                }
	            }
	            if (smTheme.length() > 0) {
	                map.put("mainTheme", smTheme.toString().substring(0, smTheme.length() - 1));
	            }
	            if (otherTheme.length() > 0) {
	            	map.put("otherTheme", otherTheme.toString().substring(0, otherTheme.length() - 1));
	            }
	            //上级目标
	            if (null != sm.getParent()) {
	                map.put("parentId", sm.getParent().getId());
	                map.put("parentname", sm.getParent().getName());
	                
	            }
	            // 图标类型
                if (StringUtils.isNotBlank(sm.getChartType())) {
                	String ids[] = sm.getChartType().split(",");
                	List<DictEntry> chartList = o_dictEntryBO.findDictEntryByIds(ids);
                	StringBuffer chartName = new StringBuffer();
                	for(DictEntry dict: chartList) {
                		chartName.append(dict.getName());
                		chartName.append(",");
                	}
                    if(chartName.length() > 0) {
                       map.put("chartTypeStr", chartName.substring(0, chartName.length()-1));
                    }
                   
                }
                // 所属部门、查看人、报告人
                Map<String, Object> empMap = o_kpiStrategyMapBO.findSmRelaOrgEmpBySm(sm);
                List<String> ownDept = (List<String>) empMap.get("ownDept");
                if(ownDept.size()>0){
                	StringBuffer sb = new StringBuffer(); 
                	for (String s: ownDept) {
                		sb.append(s).append(",");
                	}
                	map.put("ownDept", sb.substring(0, sb.length()-1));
                }
                List<String> reportDept = (List<String>) empMap.get("reportDept");
                if(reportDept.size()>0){
                	StringBuffer sb = new StringBuffer(); 
                	for (String s: reportDept) {
                		sb.append(s).append(",");
                	}
                	map.put("reportDept", sb.substring(0, sb.length()-1));
                }
                List<String> viewDept = (List<String>) empMap.get("viewDept");
                if(viewDept.size()>0){
                	StringBuffer sb = new StringBuffer(); 
                	for (String s: viewDept) {
                		sb.append(s).append(",");
                	}
                	map.put("viewDept", sb.substring(0, sb.length()-1));
                }
                Set<SmRelaAlarm> relaAlarms = sm.getSmRelaAlarms();
                String alarmId = null;
            	Date preDate = null;
                //设置预警方案 和告警方案ID、名称
                for(SmRelaAlarm cra : relaAlarms) {

                	if(preDate == null ||cra.getStartDate().compareTo(preDate)>0) {
                		preDate = cra.getStartDate();
                		//告警
                		if(cra.getrAlarmPlan() != null) {
                			map.put("alarmPlanName", cra.getrAlarmPlan().getName());
                			alarmId =  cra.getrAlarmPlan().getId();
                		}
                		
                	}
                }
                // 告警区间和等级信息
                if(StringUtils.isNotBlank(alarmId)) {
                    List<AlarmRegionForm> alarmRegion = new ArrayList<AlarmRegionForm>();
                    AlarmRegionForm form = null;
                        AlarmPlan alarmPlan = o_alarmPlanBO.findAlarmPlanById(alarmId);
                        Set<AlarmRegion> alarmRegions = alarmPlan.getAlarmRegions();
                        for (AlarmRegion am : alarmRegions)
                        {
                            form = new AlarmRegionForm(am);
                            alarmRegion.add(form);
                        }
                        map.put("alarmId", alarmId);
                }
	            
		 }
		return map;
    }

    /**
     * <pre>
     * 查询目标和kpi指标关联信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param start
     *            起始位置
     * @param limit
     *            返回记录数
     * @param query查询参数
     * @param currentSmId
     *            当前目标ID
     * @return 前台接收的json
     * @throws Exception
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findsmrelakpibysome.f")
    public Map<String, Object> findSmRelaKpiBySome(int start, int limit, String query, String currentSmId, String sort) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        List<SmRelaKpiForm> datas = new ArrayList<SmRelaKpiForm>();
        if (StringUtils.isNotBlank(currentSmId) && !"undefined".equals(currentSmId)) {
            String dir = "ASC";
            String sortColumn = "id";
            
            SmRelaKpiForm form = null;
            if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");
                    dir = jsobj.getString("direction");
                }
            }
            List<SmRelaKpi> entityList = o_kpiStrategyMapBO.findSmRelaKpiBySome(query, currentSmId, sortColumn, dir);
            for (SmRelaKpi de : entityList) {
            	String orgname = "";
            	StringBuffer orgBuf = new StringBuffer();
                Set<KpiRelaOrgEmp> orgSet = de.getKpi().getKpiRelaOrgEmps();
                for (KpiRelaOrgEmp kpiRelaOrgEmp : orgSet) {
                    if (Contents.BELONGDEPARTMENT.equals(kpiRelaOrgEmp.getType())) {
                        orgBuf.append(kpiRelaOrgEmp.getOrg().getOrgname()).append(",");
                    }
                }
                if (orgBuf.length() > 0){
                	orgname = orgBuf.toString().substring(0, orgBuf.length() - 1);
                }
                form = new SmRelaKpiForm(de);
                form.setDept(orgname);
                datas.add(form);
            }
        }
        map.put("datas", datas);
        return map;
    }

    /**
     * <pre>
     * 查询目标和预警关联信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param start
     * @param limit
     * @param query
     * @param currentSmId
     * @return
     * @throws Exception
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findsmrelaalarmbysome.f")
    public Map<String, Object> findSmRelaAlarmBySome(int start, int limit, String query, String currentSmId, String editflag, String sort)
    {

        HashMap<String, Object> map = new HashMap<String, Object>();
        List<SmRelaAlarmForm> datas = new ArrayList<SmRelaAlarmForm>();
        if (StringUtils.isNotBlank(currentSmId)) {
            String dir = "ASC";
            String sortColumn = "id";
            if (StringUtils.isNotBlank(sort)) {
                JSONArray jsonArray = JSONArray.fromObject(sort);
                if (jsonArray.size() > 0) {
                    JSONObject jsobj = jsonArray.getJSONObject(0);
                    sortColumn = jsobj.getString("property");
                    dir = jsobj.getString("direction");
                }
            }
            List<SmRelaAlarm> entityList = o_kpiStrategyMapBO.findSmRelaAlarmBySome(query, currentSmId, editflag, sortColumn, dir);

            for (SmRelaAlarm de : entityList) {
                datas.add(new SmRelaAlarmForm(de));
            }
        }

        map.put("datas", datas);
        return map;
    }

    /**
     * <pre>
     * 查询告警信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param type
     *            告警或预警类型
     * @return 告警或预警列表
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findwarningbytype.f")
    public Map<String, Object> findWarningByType(String type) {
        Map<String, String> map = null;
        List<Map<String, String>> l = new ArrayList<Map<String, String>>();
        Map<String, Object> result = new HashMap<String, Object>();
        List<AlarmPlan> list = o_kpiStrategyMapBO.findAlarmPlanByType(type);
        for (AlarmPlan plan : list) {
            map = new HashMap<String, String>();
            map.put("name", plan.getName());
            map.put("id", plan.getId());
            l.add(map);
        }
        result.put("warninglist", l);
        return result;
    }

    /**
     * <pre>
     * 查找目标父节点id
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            目标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findparentbyid.f")
    public Map<String, Object> findParentByid(String id) {
        String parentid = "";
        String parentname = "";
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.contains(id, "_")) {
            id = id.split("_")[1];// 机构目标树,传递过来的id为orgid_smid
        }
        StrategyMap sm = o_kpiStrategyMapBO.findById(id);
        if (null != sm) {
            StrategyMap psm = sm.getParent();
            if (null != psm) {
                parentid = psm.getId();
                parentname = psm.getName();
            }
            else {
                parentid = "sm_root";
                parentname = "目标库";
            }
            result.put("name", sm.getName());
            result.put("id", sm.getId());
            result.put("chartType", sm.getChartType());
        }
        result.put("parentid", parentid);
        result.put("parentname", parentname);
        
        return result;
    }

    /**
     * <pre>
     * 根据父节点Id生成目标编码
     * </pre>
     * 
     * @author 陈晓哲
     * @param parentid
     *            父目标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findcodebyparentid.f")
    public Map<String, Object> findCodeByParentId(String param) {
        String code = "";
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(param)) {
            JSONObject jsobj = JSONObject.fromObject(param);
            String parentid = jsobj.getString("parentid");
            String smId = jsobj.getString("currentSmId");
            code = o_kpiStrategyMapBO.findCodeBySmParentId(parentid, smId);
        }
        result.put("code", code);
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 根据kpiid查询所属部门
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findkpirelaorgempbyid.f")
    public Map<String, Object> findKpiRelaOrgEmpById(String kpiID) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        JSONObject jsonobj = o_kpiStrategyMapBO.findKpiRelaOrgEmpById(kpiID);
        resultMap.put("success", true);
        resultMap.put("kpiRelOrg", jsonobj.toString());
        return resultMap;
    }

    /**
     * <pre>
     * 查询目标关联的指标信息
     * </pre>
     * 
     * @author 陈晓哲
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
    @RequestMapping("/kpi/kpistrategymap/findsmrelakpiresult.f")
    public Map<String, Object> findSmRelaKpiResult(int start, int limit, String query, String sort, String id, String year, String quarter,
            String month, String week, String eType, String isNewValue) {
        id = ",".equals(id) ? "" : id;
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
        if (null != id && id.contains(",")) {
            id = id.replace(",", "");
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
        if ("true".equals(lastflag)) {// 最新值查询
            gatherDatas = o_kpiStrategyMapBO.findLastSmRelaKpiResults(map, start, limit, query, sortColumn, dir, id);
        }
        else {// 具体频率查询
            gatherDatas = o_kpiStrategyMapBO.findSpecificSmRelaKpiResults(map, start, limit, query, sortColumn, dir, id, year, quarter, month, week,
                    frequence);
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
        		form = new KpiForm(objects, "sm");
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
     * 目标查看 - 详细页查看
     * </pre>
     * 
     * @author zhengjunxiang
     * @param id 目标ID
     * @return 前台接收的json
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findsmbyidtojsonstr.f")
    public Map<String, Object> findSmByIdToJsonStr(String id) {
        DictEntry entry = null;
        StringBuffer smDim = new StringBuffer();
        StringBuffer smTheme = new StringBuffer();
        StringBuffer otherdimStr = new StringBuffer();
        StringBuffer otherThemeStr = new StringBuffer();

        if (StringUtils.contains(id, "_")) {
            id = id.split("_")[1];// 机构目标树,传递过来的id为orgid_smid
        }
        StrategyMap sm = o_kpiStrategyMapBO.findById(id);
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != sm) {
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put("code", sm.getCode());
            jsonMap.put("name", sm.getName());
            jsonMap.put("shortName", sm.getShortName());
            jsonMap.put("warningFormula", sm.getWarningFormula());
            jsonMap.put("assessmentFormula", sm.getAssessmentFormula());
            jsonMap.put("desc", sm.getDesc());
            if (null != sm.getStatus()) {
                jsonMap.put("estatus", sm.getStatus().getName());
            }
            if(StringUtils.isNotBlank(sm.getIsFocus())){
                jsonMap.put("isfocustr", sm.getIsFocus());
            }
            jsonMap.put("currentSmId", sm.getId());
            jsonMap.put("id", sm.getId());
            Set<SmRelaDim> smRelaDimSet = sm.getSmRelaDims();
            for (SmRelaDim smRelaDim : smRelaDimSet) {
                entry = smRelaDim.getSmDim();
                if (null != entry) {
                    if (Contents.DUTY_DEPARTMENT.equals(smRelaDim.getType())) {
                        smDim.append(entry.getName()).append(",");
                    }
                    else {
                        otherdimStr.append(entry.getName()).append(",");
                    }
                }
            }
            if (smDim.length() > 0) {
                jsonMap.put("mainDim", smDim.toString().substring(0, smDim.length() - 1));
            }
            else {
                jsonMap.put("mainDim", "");
            }
            if (otherdimStr.length() > 0) {
                jsonMap.put("otherDim", otherdimStr.toString().substring(0, otherdimStr.length() - 1));
            }
            else {
                jsonMap.put("otherDim", "");
            }

            Set<SmRelaTheme> smRelaThemeSet = sm.getSmRelaThemes();
            for (SmRelaTheme smRelaTheme : smRelaThemeSet) {
                entry = smRelaTheme.getTheme();
                if (null != entry) {
                    if (Contents.DUTY_DEPARTMENT.equals(smRelaTheme.getType())) {
                        smTheme.append(entry.getName()).append(",");
                    }
                    else {
                        otherThemeStr.append(entry.getName()).append(",");
                    }
                }
            }
            if (smTheme.length() > 0) {
                jsonMap.put("mainTheme", smTheme.toString().substring(0, smTheme.length() - 1));
            }
            else {
                jsonMap.put("mainTheme", "");
            }
            if (otherThemeStr.length() > 0) {
                jsonMap.put("otherTheme", otherThemeStr.toString().substring(0, otherThemeStr.length() - 1));
            }
            else {
                jsonMap.put("otherTheme", "");
            }

            if (null != sm.getParent()) {
                jsonMap.put("parentId", sm.getParent().getId());
            }

            if (null != sm.getChartType()) {
                jsonMap.put("chartTypeStr", sm.getChartType());
                jsonMap.put("charttypehidden", sm.getChartType());
            }

            /* 部门和人员信息 */
            JSONObject orgEmpObj = this.o_kpiStrategyMapBO.findSmRelaOrgEmpBySmToJsonStr(sm);
            jsonMap.put("ownDept", orgEmpObj.getString("ownDept"));
            jsonMap.put("reportDept", orgEmpObj.getString("reportDept"));
            jsonMap.put("viewDept", orgEmpObj.getString("viewDept"));
            map.put("data", jsonMap);
            map.put("success", true);
        }

        return map;
    }

    /**
     * 查询目标最新的告警方案
     * @param id 目标id
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findNewestAlarmById.f")
    public Map<String, Object> findNewestAlarmById(String id) {
        Map<String, Object> result = new HashMap<String, Object>();
        StrategyMap sm = o_kpiStrategyMapBO.findById(id);
        if (null != sm) {
            Set<SmRelaAlarm> alarms = sm.getSmRelaAlarms();

            AlarmPlan plan = null; //最近的告警方案
            Date newestDate = null;
            Iterator<SmRelaAlarm> ite = alarms.iterator();
            //循环获取最新的告警方案，以后可以直接通过hql直接查出最新的
            for (int i = 0; ite.hasNext(); i++) {
                SmRelaAlarm alarm = ite.next();
                if (i == 0) {
                    newestDate = alarm.getStartDate();
                    plan = alarm.getrAlarmPlan();
                }
                else {
                    Date currentDate = alarm.getStartDate();
                    if (currentDate.after(newestDate)) {
                        newestDate = currentDate;
                        plan = alarm.getrAlarmPlan();
                    }
                }
            }

            List<AlarmRegionForm> datas = new ArrayList<AlarmRegionForm>();
            if (plan != null) { //有可能没有告警方案
                Set<AlarmRegion> regions = plan.getAlarmRegions();
                AlarmRegionForm form = null;
                for (AlarmRegion region : regions) {
                    form = new AlarmRegionForm(region);
                    datas.add(form);
                }
            }

            result.put("datas", datas);
        }

        return result;
    }
    
    /**查询关注的战略目标
     * @param start
     * @param limit
     * @param query
     * @param isFocus
     * @param sort
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/findfocusstrategymap.f")
    public Map<String, Object> findFocusStrategymap(int start, int limit, String query, String isFocus,String sort){
    	 Map<String, Object> map = new HashMap<String, Object>();
    	 List<StrategyMapForm> datas = new ArrayList<StrategyMapForm>();
    	 List<StrategyMap> smList = o_kpiStrategyMapBO.findFocusStrategymap(query,Contents.DICT_Y);
    	 for (StrategyMap strategyMap : smList) {
			StrategyMapForm form = new StrategyMapForm();
			form.setId(strategyMap.getId());
			form.setName(strategyMap.getName());
			form.setIsfocustr(strategyMap.getIsFocus());
			form.setTypeStr("sm");
			datas.add(form);
		}
    	 map.put("datas", datas);
    	 return map;
    }
    
    /**
     * <pre>
     * 根据记分卡ID查询图表类型
     * </pre>
     * 
     * @param response
     *            响应对象
     * @param id
     *            记分卡ID
     * @throws IOException
     */
    @RequestMapping("/kpi/kpistrategymap/findcharttypebyid.f")
    public void findChartTypeById(HttpServletResponse response, String id) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        StrategyMap sm = o_kpiStrategyMapBO.findById(id);
        StringBuffer chartType = new StringBuffer();
        if (null != sm && StringUtils.isNotBlank(sm.getChartType())) {
            String[] chartIdArray = sm.getChartType().split(",");
            Arrays.sort(chartIdArray,new Comparator<String>() {
				public int compare(String chartType1, String chartType2) {
				    int result = chartType1.compareTo(chartType2);
				    return -result;
				}
			});
            int i = 0;
            for (String chartId : chartIdArray) {
                chartType.append(chartId);
                if (i != chartIdArray.length - 1) {
                   chartType.append(",");
                }
                i++;
            }
        }
        result.put("chartType", chartType.toString());
        result.put("success", true);
        response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
    }
    
    /**
     * <pre>
     * 保存目标信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param form
     *            目标Form
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/mergestrategymap.f")
    public Map<String, Object> mergeStrategyMap(String items) throws UnsupportedEncodingException, IllegalAccessException, InvocationTargetException {
    	String otherDimStr = "";
    	String otherThemeStr = "";
    	StringBuffer otherDimBf = new StringBuffer();
    	StringBuffer otherThemeBf = new StringBuffer();
        JSONObject param = JSONObject.fromObject(items);
        StrategyMapForm form = new StrategyMapForm();
        BeanUtils.populate(form, param);
        if(param.containsKey("otherDim")&&!"".equals(param.get("otherDim"))){
        	JSONArray otherDimArr = param.getJSONArray("otherDim");
        	for (Object otherDim : otherDimArr) {
        		otherDimBf.append(otherDim).append(",");
			}
        }
        if(otherDimBf.length()>0){
        	otherDimStr = otherDimBf.substring(0, otherDimBf.length()-1);
        }
        if(param.containsKey("otherTheme")&&!"".equals(param.get("otherTheme"))){
        	JSONArray otherThemeArr = param.getJSONArray("otherTheme");
        	for (Object otherTheme : otherThemeArr) {
        		otherThemeBf.append(otherTheme).append(",");
			}
        }
        if(otherThemeBf.length()>0){
        	otherThemeStr = otherThemeBf.substring(0, otherThemeBf.length()-1);
        }
        form.setOtherDim(otherDimStr);
        form.setOtherTheme(otherThemeStr);
        form.setParentId(param.getString("parentSmId"));
        String strategyMapId = o_kpiStrategyMapBO.mergeStrategyMapAndUpdateNode(form);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        result.put("smId", strategyMapId);
        return result;
    }

    /**启用和停用战略目标
     * @param enable是否启用
     * @param strategyMapId 战略目标id
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/mergestrategymapenable.f")
    public Map<String, Object> mergeStrategyMapEnable(String enable, String strategyMapId) {
        Map<String, Object> result = new HashMap<String, Object>();
        StrategyMap strategyMap = o_kpiStrategyMapBO.findById(strategyMapId);
        strategyMap.setStatus(o_dictEntryBO.findDictEntryById(enable));
        o_kpiStrategyMapBO.mergeStrategyMap(strategyMap);
        if(Contents.DICT_Y.equals(enable)){
            String smid = strategyMapId;
            Map<String, String> findAssessmentMaxEntTimeAllMap = o_relaAssessResultBO.findAssessmentMaxEntTimeAll("str", smid);
            String iconCls = findAssessmentMaxEntTimeAllMap.get(strategyMapId);
            String css = "icon-status-disable";
            if ("0alarm_startus_h".equals(iconCls)) {
                css = "icon-status-high";
            }
            else if ("0alarm_startus_m".equals(iconCls)) {
                css = "icon-status-mid";
            }
            else if ("0alarm_startus_l".equals(iconCls)) {
                css = "icon-status-low";
            }
            result.put("iconCls", css);
        }else{
            result.put("iconCls", "icon-symbol-status-sm");
        }
        result.put("success", true);
        return result;
    }
    /**启用和停用战略目标（批量操作）
     * @param enable是否启用
     * @param smid 战略目标id
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/mergesmenable.f")
    public Map<String, Object> mergeSmEnable(String smItems) {
    	JSONObject jsobj = JSONObject.fromObject(smItems);
        String enable = jsobj.getString("enable");// Y/N
        DictEntry enableDict = o_dictEntryBO.findDictEntryById(enable);
        JSONArray smidArr = jsobj.getJSONArray("smids");
        String[] smids = new String[] {};
        smids = (String[]) smidArr.toArray(smids);
        Map<String, Object> result = new HashMap<String, Object>();
        
        if (smids.length > 0) {
        	Map<String,StrategyMap> strMap = new HashMap<String, StrategyMap>();
        	List<StrategyMap> straList = o_kpiStrategyMapBO.findByIdList(smids);
        	for (StrategyMap strategyMap : straList) {
        		strMap.put(strategyMap.getId(),strategyMap);
			}
            for (String smid : smids) {
                StrategyMap strategyMap = strMap.get(smid);
                strategyMap.setStatus(enableDict);
                o_kpiStrategyMapBO.mergeStrategyMap(strategyMap);
            }
        }

        result.put("success", true);
        return result;
    }
    
    
    /**是否关注战略目标
     * @param focus 是否关注
     * @param strategyMapId 战略目标ID
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/mergestrategymapfocus.f")
    public Map<String, Object> mergeStrategyMapFocus(String focus, String strategyMapId) {
        Map<String, Object> result = new HashMap<String, Object>();
        StrategyMap strategyMap = o_kpiStrategyMapBO.findById(strategyMapId);
        strategyMap.setIsFocus(focus);
        o_kpiStrategyMapBO.mergeStrategyMap(strategyMap);
        result.put("success", true);
        return result;
    }
    
    /**设置目标是否关注（批量关注）
     * @param smItems 目标的关心信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/mergesmfocus.f")
    public Map<String,Object> mergeSmFocus(String smItems){
        JSONObject jsobj = JSONObject.fromObject(smItems);
        String focus = jsobj.getString("focus");// Y/N
        JSONArray smidArr = jsobj.getJSONArray("smids");
        String[] smids = new String[] {};
        smids = (String[]) smidArr.toArray(smids);
        Map<String, Object> result = new HashMap<String, Object>();
        if (smids.length > 0) {
        	Map<String,StrategyMap> strMap = new HashMap<String, StrategyMap>();
        	List<StrategyMap> straList = o_kpiStrategyMapBO.findByIdList(smids);
        	for (StrategyMap strategyMap : straList) {
        		strMap.put(strategyMap.getId(),strategyMap);
			}
            for (String smid : smids) {
            	 StrategyMap strategyMap = strMap.get(smid);
                 strategyMap.setIsFocus(focus);
                 o_kpiStrategyMapBO.mergeStrategyMap(strategyMap);
            }
        }
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 保存目标和指标关联信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiParam
     *            kpi指标参数
     * @param currentSmId
     *            当前目标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/mergestrategyrelakpi.f")
    public Map<String, Object> mergeStrategyRelaKpi(String kpiParam, String currentSmId) {
        Map<String, Object> result = new HashMap<String, Object>();
        o_kpiStrategyMapBO.mergeStrategyRelaKpi(kpiParam, currentSmId);
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 保存目标和预警关联信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param modifiedRecord
     *            预警或告警信息
     * @param currentSmId
     *            当前目标ID
     * @return
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/mergestrategyrelaalarm.f")
    public Map<String, Object> mergeStrategyRelaAlarm(String modifiedRecord, String currentSmId) throws ParseException {
        Map<String, Object> result = new HashMap<String, Object>();
        o_kpiStrategyMapBO.mergeStrategyRelaAlarm(modifiedRecord, currentSmId);
        result.put("success", true);
        return result;
    }
    
    /**
     * 
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/kpi/kpistrategymap/mergesmrelakpi.f")
    public Map<String, Object> mergeSmRelaKpi(String param) {
    	  Map<String, Object> result = new HashMap<String, Object>();
          if (StringUtils.isNotBlank(param)) {
              JSONObject jsobj = JSONObject.fromObject(param);
              o_strategyMapBO.mergeSmRelaKpi(jsobj);
          }
          result.put("success", true);
          return result;    	
    }

    /**
     * <pre>
     * 逻辑删除目标对象
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            目标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/removestrategymap.f")
    public Map<String, Object> removeStrategyMap(String id) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        boolean result = o_kpiStrategyMapBO.removeStrategyMap(id);
        resultMap.put("result", result);
        return resultMap;
    }
    
    /**
     * <pre>
     * 逻辑删除目标对象（批量）
     * </pre>
     * 
     * @author 郝静
     * @param id
     *            目标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/removestrategymaps.f")
    public Map<String, Object> removeStrategyMaps(String ids) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(ids);
        JSONArray smidArr = jsobj.getJSONArray("ids");
        String[] id = new String[] {};
        id = (String[]) smidArr.toArray(id);
        boolean result = false;
        for(int i = 0; i<id.length;i++){
        	 result = o_kpiStrategyMapBO.removeStrategyMap(id[i]);
        }
        resultMap.put("success", true);
        resultMap.put("result", result);
        return resultMap;
    }

    /**
     * <pre>
     * 逻辑删除目标对象及相关信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            目标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/removerelstrategymap.f")
    public Map<String, Object> removeRelStrategyMap(String id) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        boolean result = o_kpiStrategyMapBO.removeStrategyMapBatch(id);
        resultMap.put("result", result);
        return resultMap;

    }

   

    
    
    /**
     * 解除目标与指标的关联关系
     * @param kpiItems
     * @param smId
     * @return 删除结果
     */
    @ResponseBody
    @RequestMapping(value = "/kpi/kpi/removesmrelakpiByIds.f")
    public Map<String, Object>  removesmrelakpiByIds(String kpiItems,String smId) {
  	  Map<String, Object> result = new HashMap<String, Object>();
      if (StringUtils.isNotBlank(kpiItems) && StringUtils.isNotBlank(smId)) {          
          o_strategyMapBO.removeSmRelaKpiByIds(kpiItems,smId);
      }
      result.put("success", true);
      return result; 
    }
    
    /**
     * <pre>
     * 校验目标名称和编码是否重复
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            目标名称
     * @param code
     *            目标编码
     * @param id
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpistrategymap/validate.f")
    public Map<String, Object> validate(String name, String code, String id) {
        boolean successFlag = true;
        Map<String, Object> result = new HashMap<String, Object>();
        if (o_kpiStrategyMapBO.findStrategyMapCountByName(name, id,UserContext.getUser().getCompanyid()) > 0) {
            successFlag = false;
            result.put("error", "nameRepeat");
        }
        if (o_kpiStrategyMapBO.findStrategyMapCountByCode(code, id, UserContext.getUser().getCompanyid()) > 0) {
            successFlag = false;
            result.put("error", "codeRepeat");
        }
        result.put("success", successFlag);
        return result;
    }
    
    
   
}
