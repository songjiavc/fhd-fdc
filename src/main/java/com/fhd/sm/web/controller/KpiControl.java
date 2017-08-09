package com.fhd.sm.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.web.form.AlarmRegionForm;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.comm.AlarmPlan;
import com.fhd.entity.comm.AlarmRegion;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiDataSource;
import com.fhd.entity.kpi.KpiDsParameter;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.KpiMemo;
import com.fhd.entity.kpi.KpiRelaAlarm;
import com.fhd.entity.kpi.KpiRelaDim;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.sm.business.ChartForEmailBO;
import com.fhd.sm.business.KpiBO;
import com.fhd.sm.business.KpiDsParameterBO;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.business.KpiTreeBO;
import com.fhd.sm.business.StrategyMapBO;
import com.fhd.sm.business.dynamictable.TableHalfYearBO;
import com.fhd.sm.business.dynamictable.TableMonthBO;
import com.fhd.sm.business.dynamictable.TableQuarterBO;
import com.fhd.sm.business.dynamictable.TableWeekBO;
import com.fhd.sm.business.dynamictable.TableYearBO;
import com.fhd.sm.web.form.KpiForm;
import com.fhd.sm.web.form.KpiMemoForm;
import com.fhd.sm.web.form.KpiRelaAlarmForm;
import com.fhd.sys.business.dic.DictBO;

/**
 * KPI_指标Controller ClassName:KpiController
 * 
 * @author 张帅
 * @version
 * @since Ver 1.1
 * @Date 2012 2012-9-17 下午13:11:34
 * 
 * @see
 */

@Controller
public class KpiControl {

    @Autowired
    private DictBO o_dictEntryBO;

    @Autowired
    private KpiTreeBO o_kpiTreeBO;

    @Autowired
    private KpiBO o_kpiBO;

    @Autowired
    private TableMonthBO o_tableMonthBO;

    @Autowired
    private TableWeekBO o_tableWeekBO;

    @Autowired
    private TableHalfYearBO o_tableHalfYearBO;

    @Autowired
    private TableQuarterBO o_tableQuarterBO;

    @Autowired
    private TableYearBO o_tableYearBO;

    @Autowired
    private CategoryBO o_categoryBO;

    @Autowired
    private StrategyMapBO o_strategyMapBO;

    @Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
    
    @Autowired
    private KpiMemoBO o_kpiMemoBO;
    @Autowired
    private KpiDsParameterBO o_kpiDsParameterBO;
    
    @Autowired
    private ChartForEmailBO o_chartForEmailBO;
    
    @Autowired
    private RiskCmpBO o_riskCmpBO;
    
    @Autowired
    private AlarmPlanBO o_alarmPlanBO;

    
    /**
     * 指标树 treeLoader:
     * 
     * @author 张帅
     * @param canChecked
     *            是否有复选框
     * @param node
     *            节点id
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiTree/kpitreeloader")
    public List<Map<String, Object>> kpiTreeLoader(String node, Boolean canChecked, String query) {
        return o_kpiTreeBO.kpiTreeLoader(node, canChecked, query, null);
    }
    
    /**关注的指标树
     * @param node 节点id
     * @param canChecked 是否有复选框
     * @param query 查询条件
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiTree/focuskpitreeloader")
    public List<Map<String, Object>> focusKpiTreeLoader(String node, Boolean canChecked, String query) {
    	return o_kpiTreeBO.focusKpiTreeLoader(node, canChecked, query, null);
    }

    /**
     * 我的指标树 treeLoader:
     * 
     * @author 张帅
     * @param canChecked
     *            是否有复选框
     * @param node
     *            节点id
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiTree/minetreeloader")
    public List<Map<String, Object>> mineTreeLoader(String node, Boolean canChecked, String query) {
        return o_kpiTreeBO.kpiTreeLoader(node, canChecked, query, "emp");
    }

    /**
     * 机构指标树 treeLoader:
     * 
     * @author 张帅
     * @param canChecked
     *            是否有复选框
     * @param node
     *            节点id
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiTree/orgtreeloader")
    public List<Map<String, Object>> orgTreeLoader(String node, Boolean canChecked, String query) {
        return o_kpiTreeBO.orgTreeLoader(node, canChecked, query, null);
    }

    /**
     * <pre>
     * 指标类型树查询
     * </pre>
     * 
     * @author 陈晓哲
     * @param node
     *            id标识
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/kpitypetreeloader.f")
    public List<Map<String, Object>> kpiTypeTreeLoader(String node, String query, Boolean canChecked) {
        return o_kpiTreeBO.kpiTypeTreeLoader(node, query, canChecked);
    }

    /**
     * 目标指标树 treeLoader:
     * 
     * @author 张帅
     * @param canChecked
     *            是否有复选框
     * @param node
     *            节点id
     * @param query
     *            查询条件
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiTree/strategyMapTreeLoader")
    public List<Map<String, Object>> strategyMapTreeLoader(String node, Boolean canChecked, String query, String smIconType) {
        return o_kpiTreeBO.strategyMapTreeLoader(node, canChecked, query, null, smIconType);
    }
    
    
    /**查询关注的指标
     * @param start
     * @param limit
     * @param query
     * @param isFocus
     * @param sort
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findfocuskpi.f")
    public Map<String, Object> findFocusKpi(int start, int limit, String query,String sort){
    	 Map<String, Object> map = new HashMap<String, Object>();
    	 List<KpiForm> datas = new ArrayList<KpiForm>();
    	 List<Kpi> kpiList = o_kpiBO.findFocusKpi(query, Contents.DICT_Y);
    	 for (Kpi kpi : kpiList) {
    		KpiForm form = new KpiForm();
			form.setId(kpi.getId());
			form.setTypeStr("kpi");
			form.setName(kpi.getName());
			form.setIsfocustr(kpi.getIsFocus());
			datas.add(form);
		}
    	 map.put("datas", datas);
    	 return map;
    }
    
    /**
     * 
     * 根据当前登陆人所在公司id查询指标id、目标id、机构id
     * 
     * @author 张帅
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/KpiTree/findrootbycompany")
    public List<Map<String, Object>> findRootByCompanyId(String node, Boolean canChecked, String query) {
        return o_kpiTreeBO.findRootByCompanyId();
    }

    /**
     * 根据id查询指标
     * 
     * @author 张帅
     * @param id
     * @return
     * @since fhd　Ver 1.1
     */

    @ResponseBody
    @RequestMapping("/kpi/Kpi/findKpiByid")
    public List<Map<String, Object>> findKpiByid(String[] ids) {
        return o_kpiBO.findKpiByid(ids);
    }

    /**
     * <pre>
     * 根据指标ID查找指标对象
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标Id
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/Kpi/findKpiByIdToJson.f")
    public Map<String, Object> findKpiByIdToJson(String id) throws IllegalAccessException, InvocationTargetException {
        DictEntry entry = null;
        JSONArray otherdimArr = new JSONArray();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(id) && !Contents.ID_UNDEFINED.equals(id)) {
            Kpi kpi = o_kpiBO.findKpiById(id);
            // 基本信息赋值
            if (null != kpi.getStartDate()) {
                jsonMap.put("startDateStr", sf.format(kpi.getStartDate()));
            }
            jsonMap.put("id", kpi.getId());
            jsonMap.put("code", kpi.getCode());
            jsonMap.put("name", kpi.getName());
            jsonMap.put("desc", kpi.getDesc());
            jsonMap.put("targetValueAlias", kpi.getTargetValueAlias());
            jsonMap.put("resultValueAlias", kpi.getResultValueAlias());
            // 默认名称
            if (null != kpi.getIsNameDefault()) {
                jsonMap.put("namedefault", kpi.getIsNameDefault() ? Contents.DICT_Y : Contents.DICT_N);
            }
            if (null != kpi.getIsInherit()) {
                jsonMap.put("isInheritStr", kpi.getIsInherit() ? Contents.DICT_Y : Contents.DICT_N);
            }

            if (null != kpi.getIsMonitor()) {
                jsonMap.put("monitorStr", kpi.getIsMonitor() ? Contents.DICT_Y : Contents.DICT_N);
            }
            if (null != kpi.getUnits()) {
                jsonMap.put("unitsStr", kpi.getUnits().getId());
            }
            if (null != kpi.getStatus()) {
                jsonMap.put("statusStr", kpi.getStatus().getId());
            }
            if (null != kpi.getType()) {
                jsonMap.put("typeStr", kpi.getType().getId());
            }
            if (null != kpi.getDataType()) {
                jsonMap.put("dataTypeStr", kpi.getDataType().getId());
            }
            if (null != kpi.getKpiType()) {
                jsonMap.put("kpiTypeStr", kpi.getKpiType().getId());
            }
            if (null != kpi.getAlarmMeasure()) {
                jsonMap.put("alarmMeasureStr", kpi.getAlarmMeasure().getId());
            }
            if (null != kpi.getRelativeTo()) {
                jsonMap.put("relativeToStr", kpi.getRelativeTo().getId());
            }
            if (null != kpi.getAlarmBasis()) {
                jsonMap.put("alarmBasisStr", kpi.getAlarmBasis().getId());
            }
            if (null != kpi.getGatherFrequence()) {
                jsonMap.put("gatherFrequenceStr", kpi.getGatherFrequence().getId());
            }
            jsonMap.put("targetSetInterval", kpi.getTargetSetInterval());
            jsonMap.put("resultCollectInterval", kpi.getResultCollectInterval());
            jsonMap.put("targetFormula", kpi.getTargetFormula());
            jsonMap.put("resultFormula", kpi.getResultFormula());
            jsonMap.put("assessmentFormula", kpi.getAssessmentFormula());
            jsonMap.put("relationFormula", kpi.getRelationFormula());
            jsonMap.put("forecastFormula", kpi.getForecastFormula());
            jsonMap.put("shortName", kpi.getShortName());
            jsonMap.put("sort", kpi.getSort());

            // 维度信息赋值
            Set<KpiRelaDim> kpiRelaDimSet = kpi.getKpiRelaDims();
            for (KpiRelaDim kpiRelaDim : kpiRelaDimSet) {
                entry = kpiRelaDim.getSmDim();
                if (null != entry) {
                    if (Contents.MAIN.equals(kpiRelaDim.getType())) {
                        jsonMap.put("mainDim", entry.getId());
                    }
                    else {
                        otherdimArr.add(entry.getId());
                    }
                }
            }
            jsonMap.put("otherDimArray", otherdimArr.toString());
            // 部门信息赋值
            JSONObject orgEmpObj = this.o_kpiBO.findKpiRelaOrgEmpBySmToJson(kpi);
           if(((JSONArray)orgEmpObj.get("ownDept")).size() > 0) {
        	   jsonMap.put("ownDept", orgEmpObj.getString("ownDept"));
            } else {
        	   jsonMap.put("ownDept", null);
           }
            
            jsonMap.put("reportDept", orgEmpObj.getString("reportDept"));
            jsonMap.put("viewDept", orgEmpObj.getString("viewDept"));
            jsonMap.put("gatherDept", orgEmpObj.getString("gatherDept"));
            jsonMap.put("targetDept", orgEmpObj.getString("targetDept"));
            if (null != kpi.getParent()) {
                jsonMap.put("parentKpiId", kpi.getParent().getId());
            }else{
                jsonMap.put("parentKpiId", "none");
            }
            // 查询指标所属的类型信息
            Kpi belongKpi = kpi.getBelongKpiCategory();
            if (null != belongKpi) {
                jsonMap.put("kpitypeid", belongKpi.getId());
                jsonMap.put("kpitypename", belongKpi.getName());
            }
            dataMap.put("data", jsonMap);
            dataMap.put("success", true);
        }

        return dataMap;
    }

    /**
     * <pre>
     * 查找指标采集计算信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param id指标ID
     * @return
     * @since fhd　Ver 1.1
     */
	@ResponseBody
    @RequestMapping("/kpi/Kpi/findkpitypecalculatetojson.f")
    public Map<String, Object> findKpiTypeCalculateToJson(String id) {
        DictEntry dict = null;
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        Map<String, Object> othervalue = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(id) && !Contents.ID_UNDEFINED.equals(id)) {
            Kpi kpi = o_kpiBO.findKpiById(id);
            dict = kpi.getResultSumMeasure();
            if (null != dict) {
                jsonMap.put("resultSumMeasureStr", dict.getId());
            }
            dict = kpi.getTargetSumMeasure();
            if (null != dict) {
                jsonMap.put("targetSumMeasureStr", dict.getId());
            }
            dict = kpi.getAssessmentSumMeasure();
            if (null != dict) {
                jsonMap.put("assessmentSumMeasureStr", dict.getId());
            }
            String isResultFormula = kpi.getIsResultFormula();
            String resultFormula = kpi.getResultFormula();
            if (StringUtils.isNotBlank(isResultFormula)) {
                jsonMap.put("isResultFormula", isResultFormula);
            }
            if (null != resultFormula && StringUtils.isNotBlank(resultFormula)) {
                jsonMap.put("resultFormula", resultFormula);
            }
            // Mainsoft
            KpiDataSource kpiResultDs = kpi.getResultDs();
            if(kpiResultDs != null) {
            	jsonMap.put("resultDs", kpiResultDs.getDriverName());
            	jsonMap.put("resultCollectMethod", kpi.getIsResultDsType());
            }
            //本地方法
            if("03".equals(kpi.getIsResultDsType())) {
            	jsonMap.put("resultLocalPath", resultFormula);
            	List<Map<String, Object>> resultList = o_kpiDsParameterBO.findDsParameterBySome(kpi.getId(),"result");
            	
            	jsonMap.put("resultParameterJson", resultList);
            }

            String isTargetFormula = kpi.getIsTargetFormula();
            if (StringUtils.isNotBlank(isTargetFormula)) {
                jsonMap.put("isTargetFormula", isTargetFormula);
            }
            String targetFormula = kpi.getTargetFormula();
            if (null != targetFormula && StringUtils.isNotBlank(targetFormula)) {
                jsonMap.put("targetFormula", targetFormula);
            }
            if("03".equals(kpi.getIsTargetDsType())) {
            	jsonMap.put("targetLocalPath", targetFormula);
            	List<Map<String, Object>> targetList = o_kpiDsParameterBO.findDsParameterBySome(kpi.getId(),"target");
            	
            	jsonMap.put("targetParameterJson", targetList);
            }
            // Mainsoft
            KpiDataSource kpiTargetDs = kpi.getTargetDs();
            if(kpiTargetDs != null) {
            	jsonMap.put("tagertDs", kpiTargetDs.getDriverName());
            	jsonMap.put("targetCollectMethod", kpi.getIsTargetDsType());
            }
            String isAssessmentFormula = kpi.getIsAssessmentFormula();
            if (StringUtils.isNotBlank(isAssessmentFormula)) {
                jsonMap.put("isAssessmentFormula", isAssessmentFormula);
            }
            String assessmentFormula = kpi.getAssessmentFormula();
            if (null != assessmentFormula && StringUtils.isNotBlank(assessmentFormula)) {
                jsonMap.put("assessmentFormula", assessmentFormula);
            }
            // Mainsoft
            KpiDataSource kpiAssessmentDs = kpi.getAsseessmentDs();
            if(kpiAssessmentDs != null) {
            	jsonMap.put("assessDs", kpiAssessmentDs.getDriverName());
            	jsonMap.put("assessCollectMethod", kpi.getIsAssessmentDsType());
            }
            if("03".equals(kpi.getIsAssessmentDsType())) {
            	jsonMap.put("assessLocalPath", assessmentFormula);
            	List<Map<String, Object>> assessList = o_kpiDsParameterBO.findDsParameterBySome(kpi.getId(),"assess");
            	
            	jsonMap.put("assessParameterJson", assessList);
            }
            /*String alarmformula = kpi.getForecastFormula();
            if (null != alarmformula
                    && StringUtils.isNotBlank(alarmformula)) {
                jsonMap.put("forecastFormula", alarmformula);
            }*/
            if (null != kpi.getScale()) {
                jsonMap.put("scale", kpi.getScale());
            }
            if (null != kpi.getResultCollectInterval()) {
                jsonMap.put("resultCollectIntervalStr", kpi.getResultCollectInterval());
            }
            if (null != kpi.getTargetSetInterval()) {
                jsonMap.put("targetSetIntervalStr", kpi.getTargetSetInterval());
            }
            if (null != kpi.getRelativeTo()) {
                jsonMap.put("relativeToStr", kpi.getRelativeTo().getId());
            }
            if(null!=kpi.getCalc()){
                jsonMap.put("calcStr", kpi.getCalc().getId());
            }else{
            	jsonMap.put("calcStr", "");
            }
            String[] values = null;

            String gatherDayFormulr = kpi.getGatherDayFormulrShow();
            String targetSetDayFormular = kpi.getTargetSetDayFormularShow();
            String gatherReportDayFormulr = kpi.getGatherReportDayFormulrShow();
            String targetSetReportDayFormulr = kpi.getTargetReportDayFormulrShow();

            String gatherDayCron = StringUtils.defaultIfEmpty(kpi.getGatherDayFormulr(), "");
            String targetDayCron = StringUtils.defaultIfEmpty(kpi.getTargetSetDayFormular(), "");
            othervalue.put("gatherDayCron", gatherDayCron);
            othervalue.put("targetDayCron", targetDayCron);

            if (StringUtils.isNotBlank(gatherDayFormulr)) {
                values = StringUtils.split(gatherDayFormulr, "@");
                if (null != values && values.length > 0) {
                    jsonMap.put("resultgatherfrequence", values[0]);
                }
                dict = kpi.getGatherFrequence();
                if (null != dict) {
                    othervalue.put("resultgatherfrequenceDictType", dict.getId());
                }
                if (null != values && values.length > 1) {
                    othervalue.put("resultgatherfrequenceRule", values[1]);
                }
            }
            if (StringUtils.isNotBlank(targetSetDayFormular)) {
                values = StringUtils.split(targetSetDayFormular, "@");
                if (null != values && values.length > 0) {
                    jsonMap.put("targetSetFrequence", values[0]);
                    othervalue.put("targetSetFrequence", values[0]);
                }
                dict = kpi.getTargetSetFrequence();
                if (null != dict) {
                    othervalue.put("targetSetFrequenceDictType", dict.getId());
                }
                if (null != values && values.length > 1) {
                    othervalue.put("targetSetFrequenceRule", values[1]);
                }
            }
            if (StringUtils.isNotBlank(gatherReportDayFormulr)) {
                values = StringUtils.split(gatherReportDayFormulr, "@");
                if (null != values && values.length > 0) {
                    jsonMap.put("reportFrequence", values[0]);
                    othervalue.put("reportFrequence", values[0]);
                }
                dict = kpi.getReportFrequence();
                if (null != dict) {
                    othervalue.put("reportFrequenceDictType", dict.getId());
                }
                if (null != values && values.length > 1) {
                    othervalue.put("reportFrequenceRule", values[1]);
                }
            }
            if (StringUtils.isNotBlank(targetSetReportDayFormulr)) {
                values = StringUtils.split(targetSetReportDayFormulr, "@");
                if (null != values && values.length > 0) {
                    jsonMap.put("targetSetReportFrequence", values[0]);
                    othervalue.put("targetSetReportFrequence", values[0]);
                }
                dict = kpi.getTargetSetReportFrequence();
                if (null != dict) {
                    othervalue.put("targetSetReportFrequenceDictType", dict.getId());
                }
                if (null != values && values.length > 1) {
                    othervalue.put("targetSetReportFrequenceRule", values[1]);
                }

            }
            jsonMap.put("modelValue", kpi.getModelValue());
            jsonMap.put("maxValue", kpi.getMaxValue());
            jsonMap.put("minValue", kpi.getMinValue());
            jsonMap.put("gatherDesc", kpi.getGatherDesc());
        }
        dataMap.put("data", jsonMap);
        dataMap.put("othervalue", othervalue);
        dataMap.put("success", true);
        return dataMap;
    }

	 /**根据指标id查找指标对象
	 * @param kpiid指标id
	 * @param response
	 * @throws IOException 
	 */
	@ResponseBody
	 @RequestMapping("/kpi/kpi/findkpiobjectbyid.f")
	 public void findKpiObjectById(String kpiid,HttpServletResponse response) throws IOException{
		 if(StringUtils.isNotBlank(kpiid)){
			 Kpi kpi = o_kpiBO.findKpiById(kpiid);
			 if(null!=kpi){
				 Map<String,Object> result = new HashMap<String, Object>();
				 result.put("kpiname",kpi.getName());
				 response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(result));
			 }
			 
		 }
	 }
    /**
     * <pre>
     * 查找指标关联的预警信息
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
     * @return
     * @throws Exception
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findkpirelaalarmbysome.f")
    public Map<String, Object> findKpiRelaAlarmBySome(int start, int limit, String query, String id, String editflag, String sort) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        id = StringUtils.defaultIfEmpty(id, "");
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
        List<KpiRelaAlarm> entityList = this.o_kpiBO.findKpiRelaAlarmBySome(query, id, editflag, sortColumn, dir);
        List<KpiRelaAlarmForm> datas = new ArrayList<KpiRelaAlarmForm>();
        for (KpiRelaAlarm de : entityList) {
            datas.add(new KpiRelaAlarmForm(de));
        }
        map.put("datas", datas);
        return map;
    }

    /**
     * <pre>
     * 生成指标类型代码
     * </pre>
     * 
     * @author 陈晓哲
     * @param param
     *            指标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findkpitypecode.f")
    public Map<String, Object> findKpiTypeCode(String param) {
        String typeId = "";
        JSONObject jsobj = JSONObject.fromObject(param);
        Map<String, Object> result = new HashMap<String, Object>();
        if(jsobj.containsKey("id")){
            typeId = jsobj.getString("id");
        }
        String code = this.o_kpiBO.findKpiTypeCode(typeId);
        result.put("code", code);
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 根据指标ID查找所属的指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiId
     *            指标ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findkpitypebykid.f")
    public Map<String, Object> findKpiTypeByKid(String kpiId) {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = o_kpiBO.findKpiTypeByKid(kpiId);
        result.put("result", jsobj);
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 查找所有的指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param query
     *            查询条件
     * @param sort
     *            排序字段
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findkpitypeall.f")
    public Map<String, Object> findKpiTypeAll(int start, int limit, String query, String sort) {

        HashMap<String, Object> map = new HashMap<String, Object>();
        String dir = "ASC";
        String sortColumn = "name";
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");// 按照哪个字段排序
                dir = jsobj.getString("direction");// 排序方向
            }
        }
        List<KpiForm> datas = new ArrayList<KpiForm>();
        List<Kpi> list = this.o_kpiBO.findKpiTypeAll(query, sortColumn, dir);
        for (Kpi kpi : list) {
            datas.add(new KpiForm(kpi));
        }
        map.put("datas", datas);
        return map;

    }

    /**
     * <pre>
     * 查询指标类型关联的指标信息
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
     *            指标类型ID
     * @param sort
     *            排序字段
     * @param year
     *            年
     * @param month
     *            月
     * @param quarter
     *            季
     * @param week
     *            周
     * @param eType
     *            采集频率
     * @param isNewValue
     *            是否选择最新值
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findkpityperelaresult.f")
    public Map<String, Object> findKpiTypeRelaResult(int start, int limit, String query, String sort, String id, String year, String quarter,
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
            gatherDatas = o_kpiBO.findLastGatherResultByKpiTypeid(map, start, limit, query, sortColumn, dir, id);
        }
        else {// 具体频率查询
            gatherDatas = o_kpiBO.findSpecificGatherResultByKpiTypeid(map, start, limit, query, sortColumn, dir, id, year, quarter, month, week,
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
        		form = new KpiForm(objects, "kpitype");
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
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param query
     *            查询条件
     * @param sort
     *            排序信息
     * @param type
     *            分类树类型 'kpi_type':指标类型
     * @param id
     *            id标识
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findkpibysome.f")
    public Map<String, Object> findKpiBySome(int start, int limit, String query, String sort, String type, String id,String gType)  {
        String dir = "DESC"; // 默认排序顺序
        List<Kpi> kpiList = new ArrayList<Kpi>();
        String sortColumn = "name";// 默认排序字段
        Page<Kpi> page = new Page<Kpi>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");// 按照哪个字段排序
                dir = jsobj.getString("direction");// 排序方向
            }
        }
        List<KpiForm> datas = new ArrayList<KpiForm>();
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(type) && "kpi_type".equals(type)) {
            // 查询指标类型下关联的指标信息
            page = o_kpiBO.findKpiListById(page, query, "KPI", id, sortColumn, dir,gType);
            kpiList = page.getResult();
        }
        else if (StringUtils.isNotBlank(type) && "sm".equals(type)) {
            page = o_strategyMapBO.findKpiBySmId(page, query, id, sortColumn, dir,gType);
            kpiList = page.getResult();
        }
        else if (StringUtils.isNotBlank(type) && "kpi_category".equals(type)) {
            page = o_categoryBO.findKpiByCategoryId(page, query, id, sortColumn, dir,gType);
            kpiList = page.getResult();
        }
        else if (StringUtils.isNotBlank(type) && "all_metric_kpi".equals(type)) {
            page = o_kpiBO.findAllKpiList(page, query, sortColumn, dir,gType);
            kpiList = page.getResult();
        }
        else if(StringUtils.isNotBlank(type) && "kpi_dept".equals(type)){
        	  page = o_kpiBO.findKpiByDeptId(page, query, sortColumn, dir,gType,id);
              kpiList = page.getResult();
        }
        for (Kpi kpi : kpiList) {
            datas.add(new KpiForm(kpi));
        }
        map.put("totalCount", page.getTotalItems());
        map.put("datas", datas);
        return map;
    }

    /**
     * <pre>
     * 生成指标编码
     * </pre>
     * 
     * @author 陈晓哲
     * @param param
     *            指标ID和父ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findcodebyparentid.f")
    public Map<String, Object> findCodeByParentId(String param) {
        String code = "";
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(param)) {
            JSONObject jsobj = JSONObject.fromObject(param);
            String id = jsobj.getString("id");
            String parentid = jsobj.getString("parentid");
            code = o_kpiBO.findKpiCode(parentid, id);
        }
        result.put("code", code);
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 根据指标名称查询指标ID
     * </pre>
     * 
     * @author 陈晓哲
     * @param name
     *            指标名称
     * @return
     * @throws UnsupportedEncodingException
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/findkpiinfobyname.f")
    public Map<String, Object> findKpiInfoByName(String param) throws UnsupportedEncodingException {
        String frequence = "";
        boolean successFlag = true;
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(param);
        Kpi kpi = this.o_kpiBO.findKpiByName(jsobj.getString("name"));
        String kpiid = kpi.getId();
        DictEntry gatherFrequence = kpi.getGatherFrequence();
        if (null != gatherFrequence) {
            frequence = gatherFrequence.getId();
        }
        result.put("kpiid", kpiid);
        result.put("frequence", frequence);
        result.put("success", successFlag);
        return result;
    }
    
    /**根据指标id获得指标对象详细信息
     * @param id 指标id
     * @return
     */
    @SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("/kpi/kpi/loadkpiDetailbyid.f")
    public Map<String, Object> findKpiDetailById(String id) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	 if (StringUtils.isNotBlank(id) && !Contents.ID_UNDEFINED.equals(id)) {
    		 Kpi kpi = o_kpiBO.findKpiById(id);
    		 if(null != kpi.getBelongKpiCategory()) {
    			 map.put("kpiType", kpi.getBelongKpiCategory().getName());
    		 } else {
    			 map.put("kpiType","");
    		 }
    		    map.put("kpiName", kpi.getName());
    		    map.put("kpiCode", kpi.getCode());
    		if(null != kpi.getParent()) {
    			map.put("parentKpi", kpi.getParent().getName());
    		} else {
    			map.put("parentKpi",null);
    		}
    		// 所属部门、查看人、报告人
            Map<String, Object> empMap = o_kpiBO.findKpiRelaOrgEmpByKpi(kpi);
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
            List<String> gatherDept = (List<String>) empMap.get("gatherDept");
            if(gatherDept.size()>0){
            	StringBuffer sb = new StringBuffer(); 
            	for (String s: gatherDept) {
            		sb.append(s).append(",");
            	}
            	map.put("gatherDept", sb.substring(0, sb.length()-1));
            }
            List<String> targetDept = (List<String>) empMap.get("targetDept");
            if(targetDept.size()>0){
            	StringBuffer sb = new StringBuffer(); 
            	for (String s: targetDept) {
            		sb.append(s).append(",");
            	}
            	map.put("targetDept", sb.substring(0, sb.length()-1));
            }
            map.put("assessmentFormula", kpi.getAssessmentFormula());
            map.put("resultFormula", kpi.getResultFormula());
            map.put("targetFormula",kpi.getTargetFormula());
            Set<KpiRelaAlarm> relaAlarms = kpi.getKpiRelaAlarms();
            String alarmId = null;
        	Date preDate = null;
            //设置预警方案 和告警方案ID、名称
            for(KpiRelaAlarm cra : relaAlarms) {

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
     * 
     * @param list 采集结果List
     * @param map 时间区间map
     * @return json数组
     */
    private String findJsonData(List<KpiGatherResult> list,Map<String, TimePeriod> map) {
    	String name = null;
    	JSONArray jsonArray = new JSONArray();
    	Collections.sort(list, new Comparator<KpiGatherResult>() {

			public int compare(KpiGatherResult o1, KpiGatherResult o2) {
				String s1 = o1.getTimePeriod().getId();
				BigDecimal t1 = new BigDecimal(s1.replace("mm", "").replace("Q", "").replace("w", ""));
				String s2 = o2.getTimePeriod().getId();
				BigDecimal t2 = new BigDecimal(s2.replace("mm", "").replace("Q", "").replace("w", ""));
				return (t1).compareTo(t2);
			}

		});
    	for(KpiGatherResult kpiGatherResult :  list) {
			if(map.get(kpiGatherResult.getTimePeriod().getId()).getTimePeriodFullName().indexOf("季度") != -1){
				name = map.get(kpiGatherResult.getTimePeriod().getId()).getTimePeriodFullName().split("第")[1];
			}else{
				if(map.get(kpiGatherResult.getTimePeriod().getId()).getTimePeriodFullName().indexOf("月") != -1){
					name = map.get(kpiGatherResult.getTimePeriod().getId()).getTimePeriodFullName().split("年")[1];
					if(name.substring(0, 1).equalsIgnoreCase("0")){
						name = name.replace("0", "");
					}
				}else{
					if(map.get(kpiGatherResult.getTimePeriod().getId()).getTimePeriodFullName().indexOf("周") != -1){
						name = map.get(kpiGatherResult.getTimePeriod().getId()).getTimePeriodFullName().split("年")[1];
					}else{
						name = map.get(kpiGatherResult.getTimePeriod().getId()).getTimePeriodFullName();
					}
				}
			}
			
			if(name.indexOf('年') != -1){
				name = name.replace("年", "");
			}
    		JSONObject jsonObject = new JSONObject();
    		jsonObject.put("timePeriod", name);
    		jsonObject.put("targetValue", kpiGatherResult.getTargetValue());
    		jsonObject.put("finishValue", kpiGatherResult.getFinishValue());
    		if(null != kpiGatherResult.getTargetValue() || null !=kpiGatherResult.getFinishValue()) {
    			jsonArray.add(jsonObject);
    		}   		
    	}
    	return jsonArray.toString();
    }

    
    /**过滤指标除年外的采集数据
     * @param kpiGatherResultLists 采集结果集合
     * @param kpiId 指标id
     * @param year 年
     * @return
     */
    private List<KpiGatherResult> findKpiGatherResultListMap(List<KpiGatherResult> kpiGatherResultLists, String kpiId, String year) {
        List<KpiGatherResult> kpiGatherResultList = new ArrayList<KpiGatherResult>();
        for (KpiGatherResult kpiGatherResult : kpiGatherResultLists) {
            if (kpiId.equalsIgnoreCase(kpiGatherResult.getKpi().getId())) {
                if (kpiGatherResult.getKpi().getGatherFrequence().getId().equalsIgnoreCase("0frequecy_year")) {
                    break;
                }
                else {
                    kpiGatherResultList.add(kpiGatherResult);
                }
            }
        }

        return kpiGatherResultList;
    }
    
    /**
     * <pre>
     * 保存指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param form
     *            指标form
     * @return
     * @throws ParseException
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/mergekpitype.f")
    public Map<String, Object> mergeKpiType(String items) throws ParseException, IllegalAccessException, InvocationTargetException {
    	String otherDimStr = "";
    	StringBuffer otherDimBf = new StringBuffer();
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject param = JSONObject.fromObject(items);
        KpiForm form = new KpiForm();
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
        form.setOtherDim(otherDimStr);
        String id = this.o_kpiBO.mergeKpiType(form);
        result.put("id", id);
        result.put("success", true);
        return result;
    }

    /**
     * 设置指标启用或停用
     * 
     * @param enable
     *            启用或停用
     * @param kpiId
     *            指标id
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/mergekpienable.f")
    public Map<String, Object> mergeKpiEnable(String kpiItems) {
        JSONObject jsobj = JSONObject.fromObject(kpiItems);
        String enable = jsobj.getString("enable");
        JSONArray kpiidArr = jsobj.getJSONArray("kpiids");
        String[] kpiids = new String[] {};
        kpiids = (String[]) kpiidArr.toArray(kpiids);
        Map<String, Object> result = new HashMap<String, Object>();
        if (kpiids.length > 0) {
        	DictEntry dictEnable = null;
        	if(Contents.DICT_Y.equals(enable)){
        		dictEnable = o_dictEntryBO.findDictEntryById(Contents.DICT_Y);
        	}else{
        		dictEnable = o_dictEntryBO.findDictEntryById(Contents.DICT_N);
        	}
        	Map<String,Kpi> kpiMap = new HashMap<String, Kpi>();
        	List<String> kpiIdList = new ArrayList<String>();
        	for (String kpiid : kpiids) {
        		kpiIdList.add(kpiid);
        	}
        	List<Kpi> kpiList = o_kpiBO.findKpiListByIds(kpiIdList);
        	for (Kpi kpi : kpiList) {
        		kpiMap.put(kpi.getId(), kpi);
			}
            for (String kpiid : kpiids) {
                Kpi kpi = kpiMap.get(kpiid);
                kpi.setStatus(dictEnable);
                this.o_kpiBO.mergeKpi(kpi);
            }
        }
        result.put("success", true);
        return result;
    }
    
    /**设置指标是否关注
     * @param kpiItems 指标的关心信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/mergekpifoucs.f")
    public Map<String,Object> mergeKpiFocus(String kpiItems){
        JSONObject jsobj = JSONObject.fromObject(kpiItems);
        String focus = jsobj.getString("focus");// Y/N
        JSONArray kpiidArr = jsobj.getJSONArray("kpiids");
        String[] kpiids = new String[] {};
        kpiids = (String[]) kpiidArr.toArray(kpiids);
        Map<String, Object> result = new HashMap<String, Object>();
        if (kpiids.length > 0) {
        	Map<String,Kpi> kpiMap = new HashMap<String, Kpi>();
        	List<String> kpiIdList = new ArrayList<String>();
        	for (String kpiid : kpiids) {
        		kpiIdList.add(kpiid);
        	}
        	List<Kpi> kpiList = o_kpiBO.findKpiListByIds(kpiIdList);
        	for (Kpi kpi : kpiList) {
        		kpiMap.put(kpi.getId(), kpi);
			}
            for (String kpiid : kpiids) {
                Kpi kpi = kpiMap.get(kpiid);
                kpi.setIsFocus(focus);
                this.o_kpiBO.mergeKpi(kpi);
            }
        }
        result.put("success", true);
        return result;
    }
    

    /**
     * <pre>
     * 保存指标信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param form
     *            指标form
     * @return
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/mergekpi.f")
    public Map<String, Object> mergeKpi(KpiForm form, HttpServletRequest request) throws ParseException {
        Map<String, Object> result = new HashMap<String, Object>();
        form.setId(request.getParameter("id"));
        form.setParentKpiId(request.getParameter("parentid"));
        String id = o_kpiBO.saveKpi(form);
        if(StringUtils.isNotBlank(request.getParameter("smid"))) {
        	o_strategyMapBO.saveSingleKpiRela(id,request.getParameter("smid"));
        }
        if(StringUtils.isNotBlank(request.getParameter("riskid"))) {
        	o_riskCmpBO.saveSingleKpiRela(id,request.getParameter("riskid"));
        }
        result.put("id", id);
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 更新指标类型采集计算报告
     * </pre>
     * 
     * @author 陈晓哲
     * @param 采集结果参数
     * @return
     * @throws Exception
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/mergekpitypecal.f")
    public Map<String, Object> mergeKpiTypeCalculate(String param,String resultParameter,String targetParameter,String assessParameter)  {
        Map<String, Object> result = new HashMap<String, Object>();
        KpiForm form = new KpiForm();
        JSONObject jsobj = JSONObject.fromObject(param);
        form.setId(jsobj.getString("id"));
        form.setGatherReportValueCron(jsobj.getString("gatherreportCron"));
        form.setTargetReportCron(jsobj.getString("targetReportCron"));
        form.setGatherValueCron(jsobj.getString("gatherValueCron"));
        form.setTargetValueCron(jsobj.getString("targetValueCron"));
        form.setGatherFrequenceDict(jsobj.getString("gatherfrequence"));
        form.setGatherfrequenceRule(jsobj.getString("gatherfrequenceRule"));
        form.setTargetSetFrequenceDict(jsobj.getString("targetSetFrequenceDictType"));
        form.setTargetSetFrequenceRule(jsobj.getString("targetSetFrequenceRadioType"));
        form.setReportFrequenceDict(jsobj.getString("reportFrequenceDictType"));
        form.setReportFrequenceRule(jsobj.getString("reportFrequenceRadioType"));
        form.setTargetSetReportFrequenceDict(jsobj.getString("targetSetReportFrequenceDictType"));
        form.setTargetSetReportFrequenceRule(jsobj.getString("targetSetReportFrequenceRadioType"));
        form.setIsResultFormula(jsobj.getString("resultformulaDict"));
        form.setIsTargetFormula(jsobj.getString("targetformulaDict"));
        form.setIsAssessmentFormula(jsobj.getString("assessmentformulaDict"));
        if(jsobj.containsKey("gatherDesc")&&StringUtils.isNotBlank(jsobj.getString("gatherDesc"))){
        	form.setGatherDesc(jsobj.getString("gatherDesc"));
        }
        if(jsobj.containsKey("calcValue")&&StringUtils.isNotBlank(jsobj.getString("calcValue"))){
            form.setCalcStr(jsobj.getString("calcValue"));
        }
        if (jsobj.containsKey("resultformula")&&!"null".equals(jsobj.getString("resultformula"))) {
            form.setResultFormula(jsobj.getString("resultformula"));
        }
        if (jsobj.containsKey("targetformula")&&!"null".equals(jsobj.getString("targetformula"))) {
            form.setTargetFormula(jsobj.getString("targetformula"));
        }
        if (jsobj.containsKey("assessmentformula")&&!"null".equals(jsobj.getString("assessmentformula"))) {
            form.setAssessmentFormula(jsobj.getString("assessmentformula"));
        }
        // mainSoft接口
        if (jsobj.containsKey("resultDsName")){
        	form.setResultDsStr(jsobj.getString("resultDsName"));
        	if("null".equals(jsobj.getString("resultDsName"))) {
        		form.setResultDsStr(null);
        	} 
        }
        if (jsobj.containsKey("assessmentDsName")){
        	form.setAsseessmentDsStr(jsobj.getString("assessmentDsName"));
        	if("null".equals(jsobj.getString("assessmentDsName"))) {
        		form.setAsseessmentDsStr(null);
        	}
        }
        if (jsobj.containsKey("targetDsName")){    	
        	form.setTargetDsStr(jsobj.getString("targetDsName"));
        	if("null".equals(jsobj.getString("targetDsName"))) {
        		form.setTargetDsStr(null);
        	}
        }
        if (jsobj.containsKey("resultCollectMethod")){
        	form.setIsResultDsType(jsobj.getString("resultCollectMethod"));
        	if("null".equals(jsobj.getString("resultCollectMethod"))) {
        		form.setIsResultDsType(null);
        	}
        } 
        
        if (jsobj.containsKey("assessmentCollectMethod")){
        	form.setIsAssessmentDsType(jsobj.getString("assessmentCollectMethod"));
        	if("null".equals(jsobj.getString("assessmentCollectMethod"))) {
        		form.setIsAssessmentDsType(null);
        	}
        } 
        
        if (jsobj.containsKey("targetCollectMethod")){
        	form.setIsTargetDsType(jsobj.getString("targetCollectMethod"));
        	if("null".equals(jsobj.getString("targetCollectMethod"))) {
        		form.setIsTargetDsType(null);
        	}
        } 
        // 本地方法
        o_kpiDsParameterBO.deleteDsParameterBySome(jsobj.getString("id"),"result");
        if(jsobj.containsKey("resultLocalmethodPath") &&!"null".equals(jsobj.getString("resultLocalmethodPath"))) {
        	JSONArray rp = JSONArray.fromObject(resultParameter);
        	if(rp.size() > 0) {               	
            	form.setIsResultDsType("03");
            	form.setResultDsStr(null);
            	int j = rp.size();
            	for (int i = 0;i<j;i++){
            		JSONObject resultObj = rp.getJSONObject(i);
            		KpiDsParameter resultDsParemeterDsParameter = new KpiDsParameter();
            		if(resultObj.containsKey("id") && StringUtils.isNotBlank(resultObj.getString("id"))) {
            			resultDsParemeterDsParameter.setId(resultObj.getString("id"));        			
            		} else {
            			StringBuffer paramId = new StringBuffer();
            			paramId.append(jsobj.getString("id"));
            			paramId.append("result");
            			paramId.append(i);
            			resultDsParemeterDsParameter.setId(paramId.toString());
            		}
            		resultDsParemeterDsParameter.setObjectId(jsobj.getString("id"));
            		resultDsParemeterDsParameter.setValueType("result");
            		resultDsParemeterDsParameter.setParamName(resultObj.getString("parameterName"));
            		resultDsParemeterDsParameter.setParamValue(resultObj.getString("parameterValue"));
            		o_kpiDsParameterBO.mergeResultParameter(resultDsParemeterDsParameter);
            	}
        	} 	
        }
        // 本地方法法（目标值）
        o_kpiDsParameterBO.deleteDsParameterBySome(jsobj.getString("id"),"target");
        if(jsobj.containsKey("targetLocalmethodPath") &&!"null".equals(jsobj.getString("targetLocalmethodPath"))) {
        	JSONArray rp = JSONArray.fromObject(targetParameter);
        	if(rp.size() > 0) {
        		
            	form.setIsTargetDsType("03");
            	form.setTargetDsStr(null);
            	int j = rp.size();
            	for (int i = 0;i<j;i++){
            		JSONObject targetObj = rp.getJSONObject(i);
            		KpiDsParameter targetDsParemeter = new KpiDsParameter();
            		if(targetObj.containsKey("id") && StringUtils.isNotBlank(targetObj.getString("id"))) {
            			targetDsParemeter.setId(targetObj.getString("id"));        			
            		} else {
            			StringBuffer paramId = new StringBuffer();
            			paramId.append(jsobj.getString("id"));
            			paramId.append("target");
            			paramId.append(i);
            			targetDsParemeter.setId(paramId.toString());
            		}
            		targetDsParemeter.setObjectId(jsobj.getString("id"));
            		targetDsParemeter.setValueType("target");
            		targetDsParemeter.setParamName(targetObj.getString("parameterName"));
            		targetDsParemeter.setParamValue(targetObj.getString("parameterValue"));
            		o_kpiDsParameterBO.mergeResultParameter(targetDsParemeter);
            	}       	        		
        	}
        	
        }
        // 本地方法 评估值
        o_kpiDsParameterBO.deleteDsParameterBySome(jsobj.getString("id"),"assess");
        if(jsobj.containsKey("assessLocalmethodPath") &&!"null".equals(jsobj.getString("assessLocalmethodPath"))) {
        	JSONArray rp = JSONArray.fromObject(assessParameter);
        	if(rp.size() > 0) {
        		
            	form.setIsAssessmentDsType("03");
            	form.setAsseessmentDsStr(null);
            	int j = rp.size();
            	for (int i = 0;i<j;i++){
            		JSONObject assessObj = rp.getJSONObject(i);
            		KpiDsParameter assessDsParemeter = new KpiDsParameter();
            		if(assessObj.containsKey("id") && StringUtils.isNotBlank(assessObj.getString("id"))) {
            			assessDsParemeter.setId(assessObj.getString("id"));        			
            		} else {
            			StringBuffer paramId = new StringBuffer();
            			paramId.append(jsobj.getString("id"));
            			paramId.append("assess");
            			paramId.append(i);
            			assessDsParemeter.setId(paramId.toString());
            		}
            		assessDsParemeter.setObjectId(jsobj.getString("id"));
            		assessDsParemeter.setValueType("assess");
            		assessDsParemeter.setParamName(assessObj.getString("parameterName"));
            		assessDsParemeter.setParamValue(assessObj.getString("parameterValue"));
            		o_kpiDsParameterBO.mergeResultParameter(assessDsParemeter);
            	}     
        	}	  	
        }
        /*if(!"null".equals(jsobj.getString("alarmformula"))){
            form.setAlarmFormula(jsobj.getString("alarmformula"));
        }*/
        form.setResultSumMeasureStr(jsobj.getString("resultSumMeasureStr"));
        form.setTargetSumMeasureStr(jsobj.getString("targetSumMeasureStr"));
        form.setAssessmentSumMeasureStr(jsobj.getString("assessmentSumMeasureStr"));
        if (StringUtils.isNotBlank(jsobj.getString("scale"))) {
            form.setScale(Integer.parseInt(jsobj.getString("scale")));
        }
        form.setRelativeToStr(jsobj.getString("relativeTo"));
        form.setResultCollectIntervalStr(jsobj.getString("resultCollectInterval"));
        form.setTargetSetIntervalStr(jsobj.getString("targetSetInterval"));
        if (!"null".equals(jsobj.getString("modelValue")) && StringUtils.isNotBlank(jsobj.getString("modelValue"))) {
            form.setModelValue(Double.parseDouble(jsobj.getString("modelValue")));
        }
        if (!"null".equals(jsobj.getString("maxValue")) && StringUtils.isNotBlank(jsobj.getString("maxValue"))) {
            form.setMaxValue(Double.parseDouble(jsobj.getString("maxValue")));
        }
        if (!"null".equals(jsobj.getString("minValue")) && StringUtils.isNotBlank(jsobj.getString("minValue"))) {
            form.setMinValue(Double.parseDouble(jsobj.getString("minValue")));
        }
        form.setTargetSetFrequenceStr(jsobj.getString("targetSetFrequenceStr"));
        form.setReportFrequenceStr(jsobj.getString("reportFrequenceStr"));
        form.setResultgatherfrequence(jsobj.getString("resultgatherfrequence"));
        form.setTargetSetReportFrequenceStr(jsobj.getString("targetSetReportFrequenceStr"));
        this.o_kpiBO.mergeKpiTypeCalculate(form);
        result.put("success", true);
        return result;
    }

    /**
     * <pre>
     * 更新指标和预警关联信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param modifiedRecord
     *            告警信息
     * @param id
     *            指标ID
     * @return
     * @throws ParseException
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/mergekpirelaalarm.f")
    public Map<String, Object> mergeKpiRelaAlarm(String modifiedRecord, String id) throws ParseException {
        Map<String, Object> result = new HashMap<String, Object>();
        this.o_kpiBO.mergeKpiRelaAlarm(modifiedRecord, id);
        result.put("success", true);
        return result;
    }

    
    /**
     * <pre>
     * 保存季度指标采集结果
     * </pre>
     * 
     * @author 金鹏祥
     * @param params
     *            采集结果信息
     * @param request
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/savekpigatherresultquarter.f")
    public Map<String, Object> mergeKpiGatherResultQuarter(String params, HttpServletRequest request) {
        boolean successFlag = true;
        Map<String, Object> result = new HashMap<String, Object>();
        String kpiid = request.getParameter("kpiid");
        this.o_kpiBO.saveKpiGatherResultQuarter(params, kpiid);
        o_kpiBO.saveKpiLastTimePeriod(params, kpiid);
        result.put("success", successFlag);
        if(o_chartForEmailBO.findIsValueChangeSend()){
        	o_chartForEmailBO.sendKpiChangedEmail(kpiid,params);
        }

        return result;
    }
    /**
     * <pre>
     * 逻辑删除指标类型
     * </pre>
     * 
     * @author 陈晓哲
     * @param id
     *            指标类型ID
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/removekpitype.f")
    public Map<String, Object> removeKpiType(String id) {
        boolean result = true;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (this.o_kpiBO.findKpiCountByType(id) > 0) {
            result = false;
        }
        else {
            this.o_kpiBO.removeKpiType(id);
        }
        resultMap.put("result", result);
        return resultMap;
    }

    /**
     * <pre>
     * 批量删除指标信息(记分卡列表中的删除事件使用)
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiItems
     *            kpiid集合
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/removekpibatch.f")
    public Map<String, Object> removeKpiBatch(String kpiItems) {
        boolean result = true;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        o_kpiBO.removeKpiBatch(kpiItems);
        resultMap.put("success", true);
        resultMap.put("result", result);
        return resultMap;
    }
    
    /**删除目标和指标的关联关系,更新指标的删除状态
     * @param kpiItems kpiId集合
     * @param smId 目标id
     * @return
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/removesmrelakpibatch.f")
    public Map<String, Object> removeSmRelaKpiBatch(String kpiItems,String smId) {
        boolean result = true;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        o_kpiBO.removeSmRelaKpiBatch(kpiItems,smId);
        resultMap.put("success", true);
        resultMap.put("result", result);
        return resultMap;
    }

    /**
     * <pre>
     * 批量删除指标信息(我的度量指标, 所有度量指标, 战略目标关联的度量指标, 指标类型关联的度量指标列表中的删除事件使用)
     * </pre>
     * 
     * @author 陈晓哲
     * @param kpiItems
     *            kpiid集合
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/removecommonkpibatch.f")
    public Map<String, Object> removeCommonKpiBatch(String kpiItems) {
        boolean result = true;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        o_kpiBO.removeCommonKpiBatch(kpiItems);
        resultMap.put("success", true);
        resultMap.put("result", result);
        return resultMap;
    }

    /**
     * <pre>
     * 校验指标类型信息
     * </pre>
     * 
     * @author 陈晓哲
     * @param validateItem
     *            指标校验信息
     * @param id
     *            指标id
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/validate.f")
    public Map<String, Object> validate(String validateItem, String id) {
        boolean successFlag = true;
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(validateItem);
        String type = jsobj.getString("type");
        String name = jsobj.getString("name");
        String code = jsobj.getString("code");
        if (o_kpiBO.findKpiCountByName(name, id, type) > 0) {
            successFlag = false;
            result.put("error", "nameRepeat");
            return result;
        }
        if (o_kpiBO.findKpiCountByCode(code, id, type) > 0) {
            successFlag = false;
            result.put("error", "codeRepeat");
        }
        result.put("success", successFlag);
        return result;
    }

    

    /**
     * <pre>
     * 根据指标的采集频率生成采集表单
     * </pre>
     * 
     * @author 金鹏祥
     * @param condItem
     *            指标名称,年份信息
     * @param request
     * @return
     * @since fhd　Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/kpi/kpi/createtable.f")
    public Map<String, Object> createTable(String condItem, HttpServletRequest request) {
        String contextPath = request.getContextPath();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> jsonMap = new HashMap<String, Object>(); 
        Map<String, Object>  maxValueMap = new HashMap<String, Object>();
        Map<String, Object>  minValueMap = new HashMap<String, Object>();
        JSONObject jsobj = JSONObject.fromObject(condItem);
        Boolean isGather = false;
        if (jsobj.get("isGather") != null) {
        	isGather = Boolean.valueOf(jsobj.get("isGather").toString());
        }
        String year = "";
        if (jsobj.get("year") != null) {
            year = jsobj.getString("year");
        }
        String oneEdit = "";
        if (jsobj.get("oneEdit") != null) {
            oneEdit = jsobj.getString("oneEdit");
        }
        if (oneEdit.equalsIgnoreCase("1")) {
            if (jsobj.get("yearId") != null&&null==jsobj.get("year")) {
                year = jsobj.getString("yearId");
            }
        }
        String kpiName = "";
        if (jsobj.get("kpiname") != null) {
            kpiName = jsobj.getString("kpiname");
        }

        String timeId = "";
        if (jsobj.get("timeId") != null) {
            timeId = jsobj.getString("timeId");
        }
        String eType = "";
        if (jsobj.get("eType") != null) {
            eType = jsobj.getString("eType");
        }
        boolean isNewValue = false;
        if (jsobj.get("isNewValue") != null) {
            isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
        }
        String containerId = "";
        if(jsobj.get("containerId") != null){
        	containerId = jsobj.getString("containerId");
        }
        String frequecy = "";
        String kpiId = "";
        String html = "";
        boolean edit = Boolean.valueOf(request.getParameter("edit"));
        List<KpiGatherResult> kpiGatherResultList = null;
        Map<String, TimePeriod> map = o_kpiGatherResultBO.findTimePeriodAllMap();

        if ("0frequecy_all".equalsIgnoreCase(eType)) {
            List<List<KpiGatherResult>> kpiGatherResultLists = new ArrayList<List<KpiGatherResult>>();
            HashMap<String, List<KpiGatherResult>> mapYear = new HashMap<String, List<KpiGatherResult>>();
            if (jsobj.get("kpiId") != null) {
                if (jsobj.get("isNewValue") != null) {
                    isNewValue = Boolean.valueOf(jsobj.get("isNewValue").toString());
                }
                String id = jsobj.getString("kpiId");
                String[] kpiIds = id.split(",");

                String yearsStr = "";
                if (isNewValue) {
                    Map<String, String> kpiGatherResultMapYearNewValue  = o_kpiBO.findKpiAndGatherResultYearNewValue(id);
                    for (String kid : kpiIds) {
                        if (kpiGatherResultMapYearNewValue.get(kid) != null) {
                            // 有最新值并且存在年份-保留
                            String years[] = this.getYears(kpiGatherResultMapYearNewValue.get(kid));
                            yearsStr = StringUtils.join(years, ",");
                            mapYear.put(kid,o_kpiBO.findKpiAndGatherResultYear(kid, yearsStr));
                        }
                    }

                    // 除了年
                    kpiGatherResultLists.add(o_kpiBO.findKpiAndGatherResult(id, false, null));
                }
                else {

                    for (String kid : kpiIds) {
                        // 有最新值并且存在年份-保留
                        String years[] = this.getYears(year);
                        yearsStr = StringUtils.join(years, ",");
                        List<KpiGatherResult> list = o_kpiBO.findKpiAndGatherResultYear(kid, yearsStr);
                        if (list != null) {
                            mapYear.put(kid, list);
                        }
                    }

                    // 除年外
                    kpiGatherResultLists.add(o_kpiBO.findKpiAndGatherResult(id, true, year));
                }

                // 年
                for (String kid : kpiIds) {
                    if (mapYear.get(kid) != null) {
                    	  jsonMap.put(kid, findJsonData(mapYear.get(kid), map));
                    }
                }

                // 除年外
                for (String kid : kpiIds) {
                    for (List<KpiGatherResult> list : kpiGatherResultLists) {
                        kpiGatherResultList = this.findKpiGatherResultListMap(list, kid, year);
                        if (kpiGatherResultList.size() != 0) {
                        	jsonMap.put(kid, findJsonData(kpiGatherResultList, map));
                            BigDecimal maxValue = (BigDecimal) this.findKpiGatherEdgeValue(kpiGatherResultList).get("maxValue");
                            BigDecimal minValue = (BigDecimal) this.findKpiGatherEdgeValue(kpiGatherResultList).get("minValue");
                            maxValueMap.put(kid,maxValue);
                            minValueMap.put(kid,minValue);
                        }
                    }
                }

                result.put("success", true);
                result.put("jsonMap", jsonMap);
                result.put("maxMap", maxValueMap);
                result.put("minMap", minValueMap);
                return result;
            }

        }

        if (StringUtils.isNotBlank(kpiName)) {
            Kpi kpi = this.o_kpiBO.findKpiByName(kpiName);

            if (kpi != null) {
                DictEntry gatherFrequence = kpi.getGatherFrequence();
                if (gatherFrequence != null) {
                    frequecy = gatherFrequence.getId();
                    if (StringUtils.isNotBlank(frequecy)) {
                        kpiId = kpi.getId();
                        if (isNewValue) {

                        	if (kpi.getLastTimePeriod() != null) {
                                year = kpi.getLastTimePeriod().getId().subSequence(0, 4).toString();
                            }
                            else {
                                year = this.getYear();
                            }
                        }
                        // 如果是是待办任务采集页面 则固定展示当前年的数据
                    	if(isGather != null && isGather) {
                    		year = this.getYear();
                    	}

                        if (!"".equalsIgnoreCase(year)) {
                            if ("0frequecy_week".equalsIgnoreCase(frequecy)) {
                                html = o_tableWeekBO.getWeekHtml(contextPath, year, edit, kpiId, kpiName, timeId,containerId);
                            }
                            else if ("0frequecy_month".equalsIgnoreCase(frequecy)) {
                                html = o_tableMonthBO.getMonthHtml(contextPath, year, edit, kpiId, kpiName, timeId,containerId);
                            }
                            else if ("0frequecy_quarter".equalsIgnoreCase(frequecy)) {
                                html = o_tableQuarterBO.getQuarterHtml(contextPath, year, edit, kpiId, kpiName, timeId,containerId);
                            }
                            else if ("0frequecy_halfyear".equalsIgnoreCase(frequecy)) {
                                html = o_tableHalfYearBO.getHalfYearHtml(contextPath, year, edit, kpiId, kpiName, timeId,containerId);
                            }
                            else if ("0frequecy_year".equalsIgnoreCase(frequecy)) {
                                html = o_tableYearBO.getYearHtml(contextPath, year, edit, kpiId, kpiName, timeId,containerId);
                            }
                        }
                        result.put("success", true);
                        result.put("year", year);
                        result.put("tableHtml", html);
                        result.put("jsonMap", "");
                    }
                }
            }
        }
        return result;
    }

   


    /**获得当前年
     * @return
     */
    private String getYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        return String.valueOf(year);
    }

    /**
     * 获取当前年度的前4个年度集合
     * 
     * @param String
     *            time 当前年度
     * @return String[]
     * @author 金鹏祥
     * */
    private String[] getYears(String year) {
        int g = 0;
        String[] str = new String[5];
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                str[i] = String.valueOf(year);
                g = Integer.parseInt(year);
            }
            else {
                g = g - 1;
                str[i] = String.valueOf(g);
            }
        }
        return str;
    }
    
    /**
     * 取得采集结果的最大值和最小值
     * @param list 采集结果List
     * @return
     */
    private Map<String, Object> findKpiGatherEdgeValue(List<KpiGatherResult> list) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	BigDecimal maxValue = new BigDecimal(0);
    	BigDecimal minValue = new BigDecimal(0);
    	for(KpiGatherResult kgr: list) {
    		if(null != kgr.getTargetValue() &&  new BigDecimal(kgr.getTargetValue()).compareTo(maxValue) > 0) {
    			maxValue = new BigDecimal(kgr.getTargetValue());
    		}
    		if(null != kgr.getFinishValue() &&  new BigDecimal(kgr.getFinishValue()).compareTo(maxValue) > 0) {
    			maxValue = new BigDecimal(kgr.getFinishValue());
    		}
    		
    		if(null != kgr.getTargetValue() &&  new BigDecimal(kgr.getTargetValue()).compareTo(minValue) < 0) {
    			minValue = new BigDecimal(kgr.getTargetValue());
    		}
    		if(null != kgr.getFinishValue() &&  new BigDecimal(kgr.getFinishValue()).compareTo(minValue) < 0) {
    			minValue = new BigDecimal(kgr.getFinishValue());
    		}
    	}
    	map.put("maxValue", maxValue);
    	map.put("minValue", minValue);
    	return map;
    }
}
