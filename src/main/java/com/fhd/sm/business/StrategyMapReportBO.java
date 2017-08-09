package com.fhd.sm.business;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.CategoryReportBO;
import com.fhd.dao.kpi.RelaAssessResultDAO;
import com.fhd.dao.kpi.StrategyMapDAO;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.SmRelaKpi;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;

@Service
public class StrategyMapReportBO {
	
    @Autowired
    private StrategyMapBO o_kpiStrategyMapBO;
    
    @Autowired
    private CategoryReportBO o_categoryReportBO;
    
    @Autowired
    private RelaAssessResultDAO o_relaAssessResultDAO; 
    
    @Autowired
    private KpiBO o_kpiBO;
    
    @Autowired
    private StrategyMapDAO o_kpiStrategyMapDAO;
    
    /**
     * 加载目标树
     * @param id 目标id
     * @param query 模糊查询名称
     * @return 下级节点
     */
	public List<Map<String, Object>> treeLoader(String id,String query){
	        Map<String, Object> item = null;
	        List<StrategyMap> smList = null; // 保存根据父节点查出的目标
	        boolean expanded = false;
	        if (StringUtils.isNotBlank(query)) {
	            expanded = true;
	        }
	        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
	        if (StringUtils.isNotBlank(id)) {
	            String companyid = UserContext.getUser().getCompanyid();
	            smList = o_kpiStrategyMapBO.findBySome(null, companyid, id, false, null, true);
	            Set<String> idsSet  = o_kpiStrategyMapBO.findStrategyMapBySearchName(query, null, true, true);
	            // 目标所属部门统计
	            List<String> smIdList = new ArrayList<String>();
	            for (StrategyMap entity : smList) {
	            	boolean isleaf = true;
	                if (idsSet.size() > 0 && idsSet.contains(entity.getId())) {
	                    smIdList.add(entity.getId());
	                    Set<SmRelaKpi> kpiset = entity.getSmRelaKpi();
	                    for (SmRelaKpi smRelaKpi : kpiset) {
	                    	Kpi kpi = smRelaKpi.getKpi();
	                    	if(kpi.getDeleteStatus()) {
	                        	isleaf = false;
	                    	}
	                    }
	                    item = this.wrapStrategyMapNode(entity, isleaf, expanded);

	                    item.put("deptName", findSmBelongDept(entity.getId()));
	                    nodes.add(item);
	                }
	            }
	            nodes = this.wrapIconNode(smIdList, nodes,null);
                if(!"sm_root".equals(id)){             
                    StrategyMap sm = o_kpiStrategyMapBO.findById(id);
                    Set<SmRelaKpi> kpiset = sm.getSmRelaKpi();
                    for (SmRelaKpi smRelaKpi : kpiset) {
                        Kpi kpi = smRelaKpi.getKpi();
                        if(kpi.getDeleteStatus()) {
                        	  //kpiIdList.add(kpi.getId());
                           	Map<String, Object> kpiResultMap = this.findKpiGatherResultDetail(null,kpi.getId());
                              Map<String, Object> node = this.wrapKPINode(kpi, null, true, false,kpiResultMap);
                              node.put("id", id + "_" + kpi.getId());
                              node.put("leaf", true);
                              nodes.add(node);
                        }
                      
                    }
                }
	          

	        }
	        Collections.sort(nodes, new Comparator<Map<String, Object>>() {

	            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
	                String s1 = (String) o1.get("iconCls");
	                String s2 = (String) o2.get("iconCls");
	                if ("icon-status-high".equals(s1)) {
	                    s1 = "high";
	                }
	                if ("icon-status-high".equals(s2)) {
	                    s2 = "high";
	                }
	                if ("icon-status-mid".equals(s1)) {
	                    s1 = "imid";
	                }
	                if ("icon-status-mid".equals(s2)) {
	                    s2 = "imid";
	                }
	                if ("icon-status-low".equals(s1)) {
	                    s1 = "low";
	                }
	                if ("icon-status-low".equals(s2)) {
	                    s2 = "low";
	                }
	                if ("icon-symbol-status-sm".equals(s1)) {
	                    s1 = "up";
	                }
	                if ("icon-symbol-status-sm".equals(s2)) {
	                    s2 = "up";
	                }
	                if ("icon-status-disable".equals(s1)) {
	                    s1 = "nothing";
	                }
	                if ("icon-status-disable".equals(s2)) {
	                    s2 = "nothing";
	                }
	                return (s1).compareTo(s2);
	            }
	        });
	        return nodes;
	}
	
	/**
	 * 加载目标树
	 * @param id 目标id
	 * @param query 名称
	 * @param year 年
	 * @param quarter 季度
	 * @param month 月
	 * @param week 周
	 * @param eType 采集频率
	 * @return
	 */
	public List<Map<String, Object>> treeLoader(String id,String query,String year
			,String quarter,String month,String week,String eType) {
		     String timeId = null;
	        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) && StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
	            timeId = year;
	        }
	        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(month)) {
	        	timeId = quarter;
	        }
	        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
	            timeId = month;
	        }
	        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isNotBlank(week)) {
	           timeId = week;
	        } 
			   Set<String> idsSet = null; // 保存根据查询条件查出来的所有的目标id
		        Map<String, Object> item = null;
		        List<StrategyMap> smList = null; // 保存根据父节点查出的目标
		        boolean expanded = false;
		        if (StringUtils.isNotBlank(query)) {
		            expanded = true;
		        }
		        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		        if (StringUtils.isNotBlank(id)) {
		            String companyid = UserContext.getUser().getCompanyid();
		            smList = o_kpiStrategyMapBO.findBySome(null, companyid, id, false, null, true);
		            idsSet = o_kpiStrategyMapBO.findStrategyMapBySearchName(query, null, true, true);
		            // 目标下指标数量统计
		            Map<String ,Object> map = findSmRelaKpiDeataiCount();
		            // 目标所属部门统计
		            List<String> smIdList = new ArrayList<String>();
		            for (StrategyMap entity : smList) {
		            	Boolean isleaf = true;
		                if (idsSet.size() > 0 && idsSet.contains(entity.getId())) {
		                    smIdList.add(entity.getId());
		                    Set<SmRelaKpi> kpiset = entity.getSmRelaKpi();
		                    for (SmRelaKpi smRelaKpi : kpiset) {
		                    	Kpi kpi = smRelaKpi.getKpi();
		                    	if(kpi.getDeleteStatus()) {
		                        	isleaf = false;
		                    	}
		                    }
		                    item = this.wrapStrategyMapNode(entity, isleaf, expanded);
		                    item.put("kpiCount", map.get(entity.getId()));
		                    item.put("deptName", findSmBelongDept(entity.getId()));
		                    nodes.add(item);
		                }
		            }
		           nodes = this.wrapIconNode(smIdList, nodes,timeId);
	                if(!"sm_root".equals(id)){             
	                    StrategyMap sm = o_kpiStrategyMapBO.findById(id);
	                    Set<SmRelaKpi> kpiset = sm.getSmRelaKpi();
	                    for (SmRelaKpi smRelaKpi : kpiset) {
	                        Kpi kpi = smRelaKpi.getKpi();
	                        if(kpi.getDeleteStatus()) {
	                        	  //kpiIdList.add(kpi.getId());
	                           	Map<String, Object> kpiResultMap = this.findKpiGatherResultDetail(timeId,kpi.getId());
	                              Map<String, Object> node = this.wrapKPINode(kpi, null, true, false,kpiResultMap);
	                              node.put("id", id + "_" + kpi.getId());
	                              node.put("leaf", true);
	                              nodes.add(node);
	                        }	                      
	                    }
	                }

		        }
		        Collections.sort(nodes, new Comparator<Map<String, Object>>() {

		            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		                String s1 = (String) o1.get("iconCls");
		                String s2 = (String) o2.get("iconCls");
		                if ("icon-status-high".equals(s1)) {
		                    s1 = "high";
		                }
		                if ("icon-status-high".equals(s2)) {
		                    s2 = "high";
		                }
		                if ("icon-status-mid".equals(s1)) {
		                    s1 = "imid";
		                }
		                if ("icon-status-mid".equals(s2)) {
		                    s2 = "imid";
		                }
		                if ("icon-status-low".equals(s1)) {
		                    s1 = "low";
		                }
		                if ("icon-status-low".equals(s2)) {
		                    s2 = "low";
		                }
		                if ("icon-symbol-status-sm".equals(s1)) {
		                    s1 = "up";
		                }
		                if ("icon-symbol-status-sm".equals(s2)) {
		                    s2 = "up";
		                }
		                if ("icon-status-disable".equals(s1)) {
		                    s1 = "nothing";
		                }
		                if ("icon-status-disable".equals(s2)) {
		                    s2 = "nothing";
		                }
		                return (s1).compareTo(s2);
		            }
		        });
		        return nodes;
		
	}
	
	/**
	 * 封装目标节点
	 * @param sm 目标
	 * @param isLeaf 是否是叶子
	 * @param expanded 是否展开
	 * @return
	 */
    protected Map<String, Object> wrapStrategyMapNode(StrategyMap sm, Boolean isLeaf, Boolean expanded) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", sm.getId());
        item.put("itemName", sm.getName());
        item.put("type", "sm");
        item.put("deptName", findSmBelongDept(sm.getId()));
        if (null != sm.getParent()){
            item.put("parentid", sm.getParent().getId());
        }
        if (isLeaf) {
            item.put("leaf", sm.getIsLeaf());
        }
        if (!isLeaf) {
            item.put("leaf", false);
        }
        if (expanded) {
            item.put("expanded", true);
        }
        return item;
    }
    
    /**
     * 封装目标节点
     * @param ids id
     * @param nodes 节点
     * @param timeId 时间区间维度
     * @return
     */
    @SuppressWarnings("unchecked")
	private List<Map<String, Object>> wrapIconNode(Collection<String> ids, List<Map<String, Object>> nodes,String timeId) {
            if (null != ids && ids.size() > 0) {
                String smidstr = "";
                StringBuffer smBuf = new StringBuffer();
                for (String id : ids) {
                    smBuf.append("'").append(id).append("'").append(",");
                }
                if (smBuf.length() > 0) {
                    smidstr = smBuf.toString().substring(0, smBuf.length() - 1);
                }
                if (smidstr.length() > 0) {
                	 Map<String, Object> assessResultMap = new HashMap<String,Object>();
                	if (StringUtils.isBlank(timeId)) {
                		assessResultMap = o_categoryReportBO.findAssessmentMaxEntTimeAllNew("str", smidstr);
                	} else {
                		assessResultMap = o_categoryReportBO.findSpecificAssessmentResultAll("str", smidstr, timeId);
                	}
                  
                    for (Map<String, Object> map : nodes) {
                        if ("sm".equals(map.get("type"))) {
                            String css = "icon-flag-white";
                            String smid = (String) map.get("id");
                            StrategyMap strategyMap = o_kpiStrategyMapBO.findById(smid);
                            if(null!=strategyMap.getStatus()){
                                String status = strategyMap.getStatus().getId();
                                if (!Contents.DICT_Y.equals(status)) {
                                    css = "icon-flag-white";
                                }
                                else { 
                                	String cssdict = null;
                                	Map<String, Object> valueMap = (Map<String, Object>) assessResultMap.get(map.get("id"));
                                    if(null != valueMap) {
                                    	cssdict = (String) valueMap.get("status");
                                    	if(null != valueMap.get("value")) {
                                    		map.put("assessmentValue", ((BigDecimal)valueMap.get("value")).doubleValue());
                                    	}
                                    	if(null != valueMap.get("timePeriod")) {
                                    		map.put("timePeriod", (String) valueMap.get("timePeriod"));
                                    	}
                                    }
                                    if ("0alarm_startus_h".equals(cssdict)) {
                                        css = "icon-flag-red";
                                    }
                                    else if ("0alarm_startus_m".equals(cssdict)) {
                                        css = "icon-flag-yellow";
                                    }
                                    else if ("0alarm_startus_l".equals(cssdict)) {
                                        css = "icon-flag-green";
                                    }
                                }
                            }
                            map.put("iconCls", css);
                        }
                    }
                }

            }

        return nodes;
    }
    
    /**
     * 部门目标数统计
     * @return 部门目标数统计
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> findSmRelaKpiDeataiCount() {
    	Map<String, Object> map = new HashMap<String,Object>();
    	String companyId = UserContext.getUser().getCompanyid();
    	String sqlQuery ="  select sm.id,rela.count from t_kpi_strategy_map sm left join" +
    	                 "  (select STRATEGY_MAP_ID,count(*) as count from  t_kpi_sm_rela_kpi" +
    	                 " where kpi_id  in (select id from t_kpi_kpi where delete_status = '1' and COMPANY_ID = ?) " +
    	                 " GROUP BY STRATEGY_MAP_ID) rela " +
    			         "  on sm.id = rela.STRATEGY_MAP_ID " + 
    	                 "  where SM.COMPANY_ID = ? ORDER BY rela.count DESC";
    	List<Object[]> list = o_relaAssessResultDAO.createSQLQuery(sqlQuery, companyId,companyId).list();
        for(Object[] o: list) {
       	 map.put(o[0].toString(), o[1]);
        }
    	return map;
    }
    
    /**
     * 查找目标所属部门
     * @param id 目标id
     * @return
     */
    @SuppressWarnings("unchecked")
	public String findSmBelongDept(String id) {
    	String sqlQuery = "select org.ORG_NAME from t_kpi_sm_rela_org_emp rela inner join t_sys_organization org on rela.ORG_ID = org.id " +
    			          " where rela.ETYPE = 'B' and rela.STRATEGY_MAP_ID = ?";
    	if(StringUtils.isNotBlank(id)) {
    		List<Object> list = o_relaAssessResultDAO.createSQLQuery(sqlQuery, id).list();
    		if(list.size() > 0) {
    			return list.get(0).toString();
    		}
    	}
    	return null;
    }  
    
    /**封装指标节点
     * @param kpi kpi
     * @param canChecked 是否可选
     * @param isLeaf 是否叶子
     * @param expanded 是否展开
     * @return
     */
    @SuppressWarnings({ "unchecked" })
	protected Map<String, Object> wrapKPINode(Kpi kpi, Boolean canChecked, Boolean isLeaf, Boolean expanded,Map<String, Object> map) {
        Map<String, Object> item = new HashMap<String, Object>();
        if (kpi != null) {
             Map<String, Object> resultMap = (Map<String, Object>) map.get(kpi.getId());
            // item.put("id", kpi.getId());
            //item.put("code", kpi.getCode());
            item.put("itemName", kpi.getName());
            item.put("type", "kpi");
            // 报表位数默认2位
            Integer p = Integer.valueOf(2);
            if(null != resultMap.get("escale")) {
            	p = Integer.parseInt(String.valueOf(resultMap.get("escale")));
            }
            String unit = String.valueOf(resultMap.get("units"));
            if(null != resultMap.get("deptName")) {
            	 item.put("deptName", resultMap.get("deptName"));
            }
            if(null != resultMap.get("resultValue")) {
            	item.put("resultValue",  convertValue(Double.parseDouble(String.valueOf(resultMap.get("resultValue"))), p) + unit);
            }
            if(null != resultMap.get("targetValue")) {
            	item.put("targetValue",  convertValue(Double.parseDouble(String.valueOf(resultMap.get("targetValue"))), p) + unit);
            }
            if(null != resultMap.get("assessmentValue")) {
            	item.put("assessmentValue", convertValue(Double.parseDouble(String.valueOf(resultMap.get("assessmentValue"))), p));
            }
            String assessmentStatus = null;
            if(null != resultMap.get("assessmentStatus")) {
            	assessmentStatus = (String) resultMap.get("assessmentStatus");
            }
            item.put("timePeriod", resultMap.get("timePeriod"));
            // 添加指标图标
            if ("0alarm_startus_h".equals(assessmentStatus)) {
                item.put("iconCls", "icon-status-high");
            } else if("0alarm_startus_m".equals(assessmentStatus)){
            	 item.put("iconCls", "icon-status-mid");
            }
            else if("0alarm_startus_l".equals(assessmentStatus)){
           	 item.put("iconCls", "icon-status-low");
            }
            else {
                item.put("iconCls", "icon-status-disable");
            }
            if (isLeaf) {
                item.put("leaf", kpi.getIsLeaf());
            }
            if (!isLeaf) {
                item.put("leaf", false);
            }
            if (null != canChecked && canChecked) {
                item.put("checked", false);
            }
            if (expanded) {
                item.put("expanded", true);
            }
        }
        return item;
    }
    
    /**
     * 查找采集结果详细信息
     * @param timeId 时间区间维度
     * @param id 采集结果Id
     * @return
     */
    @SuppressWarnings("unchecked")
	public Map<String,Object> findKpiGatherResultDetail(String timeId,String id){
    	Map<String, Object> map = new HashMap<String, Object>();
    	String companyId = UserContext.getUser().getCompanyid();
    	StringBuffer sb = new StringBuffer();
    	sb.append("select")
    	  .append("    kpi.id,")
    	  .append("    kpi.ESCALE,")
    	  .append("    unitdict.dict_entry_name,")
    	  .append("    result.ASSESSMENT_VALUE,")
    	  .append("    result.FINISH_VALUE,")
    	  .append("    result.TARGET_VALUE,")
    	  .append("    t.TIME_PERIOD_FULL_NAME,")
    	  .append("    statusdict.id as statusId, ")
    	  .append("    org.ORG_NAME ")
    	  .append("from t_kpi_kpi kpi ")
    	  .append("left join (t_kpi_kpi_rela_org_emp ko  ")
    	  .append("           inner join t_sys_organization  org")
    	  .append("                      on ko.org_id  = org.id )  ")
    	  .append("on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ")
    	  .append("left join (t_kpi_kpi_gather_result result")
    	  .append("           inner join t_com_time_period t")
    	  .append("           on result.TIME_PERIOD_ID = t.id) ");
    	if(StringUtils.isNotBlank(timeId)) {
    		sb.append("on result.TIME_PERIOD_ID = '").append(timeId).append("'")
  		      .append("  and  kpi.GATHER_FREQUENCE = t.ETYPE AND ")
  		      .append("   kpi.ID = result.KPI_ID ");    		
    	} else {
    		sb.append("on kpi.LATEST_TIME_PERIOD_ID = result.TIME_PERIOD_ID and ")
    		  .append("   kpi.GATHER_FREQUENCE = t.ETYPE AND ")
    		  .append("   kpi.ID = result.KPI_ID ");
    	}
    	sb.append("LEFT JOIN t_sys_dict_entry  unitdict")
    	  .append("      on  kpi.units = unitdict.id ")
    	  .append("left outer join t_sys_dict_entry  statusdict")
    	  .append("      on result.assessment_status = statusdict.id ")
    	  .append("where kpi.COMPANY_ID = ? ")
    	  .append("      and kpi.DELETE_STATUS = '1' ")
    	  .append("      and kpi.id = ? ");
    	List<Object[]> list = o_relaAssessResultDAO.createSQLQuery(sb.toString(), companyId,id).list();
    	for(Object[] o : list) {
    		Map<String, Object> resultMap = new HashMap<String, Object>();
    		resultMap.put("escale", o[1]);
    		resultMap.put("units", o[2]);
    		resultMap.put("assessmentValue", o[3]);
    		resultMap.put("resultValue", o[4]);
    		resultMap.put("targetValue", o[5]);
    		resultMap.put("timePeriod", o[6]);
    		resultMap.put("assessmentStatus", o[7]);
    		resultMap.put("deptName", o[8]);
    		map.put(o[0].toString(), resultMap);
    	}
    	return map;
    }
    
    /**
     * 部门目标最新值统计
     * @param map 数据map
     * @param start 分页开始记录index
     * @param limit 分页结束index
     * @param queryName 名称
     * @param sortColumn 排序列
     * @param dir 排序顺序
     * @param year 年
     * @param quarter 季度
     * @param month 月
     * @param week 周
     * @param frequence 采集频率
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> findAllDeptSmKpiDetail(Map<String, Object> map, int start, int limit, String queryName, String sortColumn,
            String dir, String year, String quarter, String month, String week, String frequence) {
    	Map<String,Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap("KPI");
        List<Map<String,Object>> detailList = new ArrayList<Map<String,Object>>();
        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        List<Object> paralist = new ArrayList<Object>();
        StringBuffer cond = new StringBuffer();
        StringBuffer kpicond = new StringBuffer();
        countBuf.append("select count(*) ");
        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) && StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
        	cond.append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(year);
            paralist.add(year);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(month)) {
        	cond.append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(quarter);
            paralist.add(quarter);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
        	cond .append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(month);
            paralist.add(month);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isNotBlank(week)) {
        	cond.append("   AND smresult.TIME_PERIOD_ID =?");
        	kpicond.append(" AND time.id = ?");
            paralist.add(week);
            paralist.add(week);
        }

        selectBuf.append("SELECT")
                 .append("    SM.ID,") //战略目标ID
                 .append("    SM.STRATEGY_MAP_NAME,") //战略目标名称
                 .append("    SMRESULT.ASSESSMENT_VALUE smassess,")//战略目标评估值
                 .append("    SMSTATUS.DICT_ENTRY_VALUE smstaus,")//战略目标亮灯状态
                 .append("    KPI.KPI_NAME,")//关联指标名称
                 .append("    TIME.TIME_PERIOD_FULL_NAME,")//时间维度
                 .append("    SK.EWEIGHT,")//权重
                 .append("    UNITDICT.DICT_ENTRY_NAME unit,")//单位
                 .append("    FREDICT.DICT_ENTRY_NAME fre,")//指标采集频率
                 .append("    RESULT.ASSESSMENT_VALUE kpiassess,")//指标评估值
                 .append("    RESULT.TARGET_VALUE,")// 指标结果值
                 .append("    RESULT.FINISH_VALUE,")// 指标完成值
                 .append("    STATUSDICT.DICT_ENTRY_VALUE kpistatus,")// 指标亮灯状态
                 .append("    SYSORG.ORG_NAME, ")
                 .append("    SYSORG.ID");// 目标所属部门名称
        fromBuf.append(" FROM T_SYS_ORGANIZATION SYSORG ")
               .append("LEFT JOIN ")
               .append("(T_KPI_SM_RELA_ORG_EMP SMDEPT ")
               .append("      INNER JOIN T_KPI_STRATEGY_MAP SM")
               .append("      ON SMDEPT.STRATEGY_MAP_ID = SM.ID ")
               .append(" ) ON SYSORG.ID = SMDEPT.ORG_ID AND SMDEPT.ETYPE = 'B'" )
               .append("      LEFT OUTER JOIN T_KPI_SM_ASSESS_RESULT SMRESULT ")
               .append("      ON SM.ID = SMRESULT.OBJECT_ID  ")
               .append(cond)
               .append("      left outer join t_sys_dict_entry  smstatus")
               .append("      on smresult.assessment_status = smstatus.id ")
               .append("      LEFT outer JOIN (t_kpi_sm_rela_kpi sk ")
               .append("                      inner join t_kpi_kpi kpi")
               .append("                      on sk.KPI_ID = kpi.ID AND ")
               .append("                         kpi.DELETE_STATUS = '1' AND")
               .append("                         kpi.IS_ENABLED = '0yn_y' and")
               .append("                         kpi.is_kpi_category='KPI'")
               .append("                      left outer join (t_kpi_kpi_gather_result result")
               .append("                                       INNER JOIN t_com_time_period time")
               .append("                                       on time.id = result.TIME_PERIOD_ID")
               .append(kpicond)
               .append("                                       )  on kpi.id=result.kpi_id ")
               .append("                                          and  kpi.gather_frequence=time.etype")
               .append("                 )  on sm.id = sk.STRATEGY_MAP_ID ")
               .append("      left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id")
               .append("      left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id")
               .append("      left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
               .append("      left outer join (t_kpi_kpi_rela_org_emp ko ")
               .append("      inner join t_sys_organization  org on ko.org_id  = org.id )")
               .append("      on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
          wherebuf.append(" where SYSORG.ORG_TYPE = '0ORGTYPE_SD' and sysorg.DELETE_STATUS = '1' ");
          // 战略目标模糊查询
          if(StringUtils.isNotBlank(queryName)) {
        	  wherebuf.append(" and (sm.STRATEGY_MAP_NAME like '%").append(queryName).append("%'  or ")
        	          .append("  KPI.KPI_NAME like '%").append(queryName).append("%') "); 
          }
          if (StringUtils.isNotBlank(companyid)) {
              wherebuf.append(" and SYSORG.company_id='").append(companyid).append("' ");
          }
          Object[] paraobjects = new Object[paralist.size()];
          paraobjects = paralist.toArray(paraobjects);
          orderbuf.append("ORDER BY SYSORG.ORG_LEVEL,SYSORG.ID,SM.ELEVEL,SM.ID");
          if (StringUtils.isNotBlank(sortColumn)) {
        	  // 增加额外的排序
          }
          SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromBuf).append(wherebuf).toString(), paraobjects);
          map.put("totalCount", countQuery.uniqueResult());
          SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(
                  selectBuf.append(fromBuf).append(wherebuf).append(orderbuf).toString(), paraobjects);
          if(start >= 0) {
        	  sqlquery.setFirstResult(start);
              sqlquery.setMaxResults(limit); 
          }
         
          list = sqlquery.list();
          if(null != list && list.size() > 0) {
              for(Object[] o: list) {
            	  Map<String, Object> dataMap = new HashMap<String,Object>();
    			  dataMap.put("id", String.valueOf(o[0]));
    			  if(null!= o[1]) {
    				  dataMap.put("smName", String.valueOf(o[1]));  
    			  } else {
    				  dataMap.put("smName", "");  
    			  }			 
    			  if(null != o[2]) {
    				  dataMap.put("smAssessValue",convertValue(Double.parseDouble(String.valueOf(o[2])),2));
    			  } else {
    				  dataMap.put("smAssessValue", o[2]);
    			  }
    			
    			  dataMap.put("smStatus", o[3]);
    			  dataMap.put("kpiName", o[4]);
    			  dataMap.put("timePeriod",o[5]);
    			  if(null != o[6]) {
    				  dataMap.put("kpiWeight",convertValue(Double.parseDouble(String.valueOf(o[6])),2));
    			  } else {
    				  dataMap.put("kpiWeight", o[6]);
    			  }
    			  dataMap.put("units", o[7]);
    			  dataMap.put("kpiFrequency", o[8]);
    			  if(null != o[9]) {
    				   dataMap.put("assessMentValue",convertValue(Double.parseDouble(String.valueOf(o[9])),2));
    			  } else {
    				  dataMap.put("assessMentValue", o[9]);
    			  }			 
    			  dataMap.put("targetValue", o[10]);
    			  dataMap.put("finishValue", o[11]);
    			  dataMap.put("status", o[12]);
    			  dataMap.put("deptName", o[13]);
                  dataMap.put("deptId", o[14]);
            	  // 根据指标的报表小数位置和单位生成目标值和完成值
    			  if(null != o[4]) {
    				  Integer i = null;
    				  if(null != scaleReportMap.get(o[4])) {
    					 i = (Integer) scaleReportMap.get(o[4]);
    				  } else {
    					 i = 2; 
    				  }
    				  if(null != o[10]) {
    					  dataMap.put("targetValue" , convertValue(Double.parseDouble(String.valueOf(o[10])),i) + dataMap.get("units"));
    				  }
    				  if(null != o[11]) {
    					  dataMap.put("finishValue" , convertValue(Double.parseDouble(String.valueOf(o[11])),i) + dataMap.get("units"));
    				  }
    			  }
            	  detailList.add(dataMap);
              }
          }
        return detailList;
    }
    /**
     * 部门目标最新值统计
     * @param map 数据map
     * @param start 分页开始记录index
     * @param limit 分页结束index
     * @param queryName 名称
     * @param sortColumn 排序列
     * @param dir 排序顺序
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> findLastDeptSmResults(Map<String, Object> map, int start, int limit, String queryName, String sortColumn, String dir) {
    	Map<String,Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap("KPI");
        List<Map<String,Object>> detailList = new ArrayList<Map<String,Object>>();
        List<Object[]> list = null;
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        countBuf.append("select count(*) ");
        selectBuf.append("SELECT")
		        .append("    SM.ID,") //战略目标ID
		        .append("    SM.STRATEGY_MAP_NAME,") //战略目标名称
		        .append("    SMRESULT.ASSESSMENT_VALUE smassess,")//战略目标评估值
		        .append("    SMSTATUS.DICT_ENTRY_VALUE smstaus,")//战略目标亮灯状态
		        .append("    KPI.KPI_NAME,")//关联指标名称
		        .append("    TIME.TIME_PERIOD_FULL_NAME,")//时间维度
		        .append("    SK.EWEIGHT,")//权重
		        .append("    UNITDICT.DICT_ENTRY_NAME unit,")//单位
		        .append("    FREDICT.DICT_ENTRY_NAME fre,")//指标采集频率
		        .append("    RESULT.ASSESSMENT_VALUE kpiassess,")//指标评估值
		        .append("    RESULT.TARGET_VALUE,")// 指标结果值
		        .append("    RESULT.FINISH_VALUE,")// 指标完成值
		        .append("    STATUSDICT.DICT_ENTRY_VALUE kpistatus,")// 指标亮灯状态
                .append("    SYSORG.ORG_NAME, ")
                .append("    SYSORG.ID");// 目标所属部门名称
	   fromBuf.append(" FROM T_SYS_ORGANIZATION SYSORG ")
			  .append("LEFT JOIN ")
			  .append("(T_KPI_SM_RELA_ORG_EMP SMDEPT ")
	          .append("      INNER JOIN T_KPI_STRATEGY_MAP SM")
			  .append("      ON SMDEPT.STRATEGY_MAP_ID = SM.ID ")
			  .append(" ) ON SYSORG.ID = SMDEPT.ORG_ID AND SMDEPT.ETYPE = 'B'" )
		      .append("      LEFT OUTER JOIN T_KPI_SM_ASSESS_RESULT SMRESULT ")
		      .append("      ON SM.ID = SMRESULT.OBJECT_ID  ")
		      .append("      AND SM.latest_time_period_id = SMRESULT.time_period_id")
		      .append("      left outer join t_sys_dict_entry  smstatus")
		      .append("      on smresult.assessment_status = smstatus.id ")
		      .append("      LEFT outer JOIN (t_kpi_sm_rela_kpi sk ")
		      .append("                      inner join t_kpi_kpi kpi")
		      .append("                      on sk.KPI_ID = kpi.ID AND ")
		      .append("                         kpi.DELETE_STATUS = '1' AND")
		      .append("                         kpi.IS_ENABLED = '0yn_y' and")
		      .append("                         kpi.is_kpi_category='KPI'")
		      .append("                      left outer join (t_kpi_kpi_gather_result result")
		      .append("                                       INNER JOIN t_com_time_period time")
		      .append("                                       on time.id = result.TIME_PERIOD_ID")
		      .append("                                       )  on  kpi.id=result.kpi_id ")
		      .append("                                          and  kpi.latest_time_period_id=result.time_period_id ")
		      .append("                                          and  kpi.gather_frequence=time.etype")
		      .append("                 )  on sm.id = sk.STRATEGY_MAP_ID ")
		      .append("      left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id")
		      .append("      left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id")
		      .append("      left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
		      .append("      left outer join (t_kpi_kpi_rela_org_emp ko ")
		      .append("      inner join t_sys_organization  org on ko.org_id  = org.id )")
		      .append("      on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
       wherebuf.append(" where SYSORG.ORG_TYPE = '0ORGTYPE_SD' and sysorg.DELETE_STATUS = '1' ");
		 // 战略目标模糊查询
		 if(StringUtils.isNotBlank(queryName)) {
       	      wherebuf.append(" and (sm.STRATEGY_MAP_NAME like '%").append(queryName).append("%'  or ")
                      .append("  KPI.KPI_NAME like '%").append(queryName).append("%') "); 
		 }
		 if (StringUtils.isNotBlank(companyid)) {
		     wherebuf.append(" and sysorg.company_id='").append(companyid).append("' ");
		 }
         orderbuf.append("ORDER BY SYSORG.ORG_LEVEL,SYSORG.ID,SM.ELEVEL,SM.ID");
		 SQLQuery countQuery = o_kpiStrategyMapDAO.createSQLQuery(countBuf.append(fromBuf).append(wherebuf).toString());
		 map.put("totalCount", countQuery.uniqueResult());
		 SQLQuery sqlquery = o_kpiStrategyMapDAO.createSQLQuery(
		         selectBuf.append(fromBuf).append(wherebuf).append(orderbuf).toString());
         if(start >= 0) {
       	  sqlquery.setFirstResult(start);
             sqlquery.setMaxResults(limit); 
         }
		 list = sqlquery.list();
		 if(null != list && list.size() > 0) {
			 for(Object[] o: list) {
				  Map<String, Object> dataMap = new HashMap<String,Object>();
				  dataMap.put("id", String.valueOf(o[0]));
				  if(null!= o[1]) {
					  dataMap.put("smName", String.valueOf(o[1]));  
				  } else {
					  dataMap.put("smName", "");  
				  }	
				  if(null != o[2]) {
					  dataMap.put("smAssessValue",convertValue(Double.parseDouble(String.valueOf(o[2])),2));
				  } else {
					  dataMap.put("smAssessValue", o[2]);
				  }
				
				  dataMap.put("smStatus", o[3]);
				  dataMap.put("kpiName", o[4]);
				  dataMap.put("timePeriod",o[5]);
				  if(null != o[6]) {
					  dataMap.put("kpiWeight",convertValue(Double.parseDouble(String.valueOf(o[6])),2));
				  } else {
					  dataMap.put("kpiWeight", o[6]);
				  }
				  dataMap.put("units", o[7]);
				  dataMap.put("kpiFrequency", o[8]);
				  if(null != o[9]) {
					   dataMap.put("assessMentValue",convertValue(Double.parseDouble(String.valueOf(o[9])),2));
				  } else {
					  dataMap.put("assessMentValue", o[9]);
				  }			 
				  dataMap.put("targetValue", o[10]);
				  dataMap.put("finishValue", o[11]);
				  dataMap.put("status", o[12]);
				  dataMap.put("deptName", o[13]);
				  dataMap.put("deptId", o[14]);

				  // 根据指标的报表小数位置和单位生成目标值和完成值
				  if(null != o[4]) {
					  Integer i = null;
					  if(null != scaleReportMap.get(o[4])) {
						 i = (Integer) scaleReportMap.get(o[4]);
					  } else {
						 i = 2; 
					  }
					  if(null != o[10]) {
						  dataMap.put("targetValue" , convertValue(Double.parseDouble(String.valueOf(o[10])),i) + dataMap.get("units"));
					  }
					  if(null != o[11]) {
						  dataMap.put("finishValue" , convertValue(Double.parseDouble(String.valueOf(o[11])) ,i) + dataMap.get("units"));
					  }
				  }
				  detailList.add(dataMap);
			 }
		 }
		return detailList;
    }
    
    /**
    * 数值格式转换
    * @param valueObj 原始数值
    * @param m 位数
    * @return
    */
   private String convertValue(Double valueObj, Integer m) {
       String valueStr = "";

       if (null == valueObj) {
           valueStr = "";
       } else {
           BigDecimal value = new BigDecimal(valueObj);
           int valueInt = value.intValue();
           float valueFloat = value.floatValue();

           if (valueFloat > valueInt) {
               StringBuffer sb = new StringBuffer("0.");

               for (int i = 1; i < m; i++) {
                   sb.append("0");
               }
               valueStr = new DecimalFormat(sb.toString()).format(value);
           } else {
               valueStr = new DecimalFormat("0").format(value);
           }
       }

       return valueStr;
   }
}
