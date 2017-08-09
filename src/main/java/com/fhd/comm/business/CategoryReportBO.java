package com.fhd.comm.business;

import static org.hibernate.criterion.Restrictions.eq;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.comm.CategoryDAO;
import com.fhd.dao.kpi.RelaAssessResultDAO;
import com.fhd.entity.comm.Category;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiRelaCategory;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.KpiBO;


@Service
public class CategoryReportBO {
    @Autowired
    private CategoryDAO o_categoryDAO;
    @Autowired
    private CategoryBO o_categoryBO;
    
    @Autowired
    private RelaAssessResultDAO o_relaAssessResultDAO;
    @Autowired
    private KpiBO o_kpiBO;

    /**
     * 记分卡树展开
     * @param id 记分卡id
     * @param query 记分卡名称模糊查询
     * @return 下级节点信息
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> scKpiReportTreeLoader(String id,
        String query) {
        Map<String, Object> node = null;
        boolean expanded = false;

        if (StringUtils.isNotBlank(query)) { // 是否展开节点
            expanded = true;
        }

        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> allNodes = new ArrayList<Map<String, Object>>();
        String companyId = UserContext.getUser().getCompanyid(); // 所在公司id
        Set<String> idSet = new HashSet<String>();
        List<String> categoryIdList = new ArrayList<String>();

        if (StringUtils.isNotBlank(id)) { // 根据父节点查询所有子节点
            Criteria criteria = o_categoryDAO.createCriteria();
            criteria.add(eq("company.id", companyId));

            if ("category_root".equals(id)) {
                criteria.add(Restrictions.isNull("parent"));
            } else {
                criteria.add(Restrictions.eq("parent.id", id));
            }

            criteria.add(Restrictions.eq("deleteStatus", true));
            criteria.addOrder(Order.asc("name"));

            List<Category> parentCategorys = criteria.list();
            Criteria criteriaQuery = this.o_categoryDAO.createCriteria();
            criteriaQuery.add(eq("company.id", companyId));

            if (StringUtils.isNotBlank(query)) {
                criteriaQuery.add(Restrictions.like("name", query,
                        MatchMode.ANYWHERE));
            }

            criteriaQuery.addOrder(Order.asc("name"));

            List<Category> categoryList = criteriaQuery.list();

            for (Category entity : categoryList) {
                String[] idsTemp = entity.getIdSeq().split("\\.");
                idSet.addAll(Arrays.asList(idsTemp));
            }

            for (Category category : parentCategorys) {
            	Boolean isleaf = true;
                if ((idSet.size() > 0) && idSet.contains(category.getId())) {
                    categoryIdList.add(category.getId());
                   
                    Set<KpiRelaCategory> kpiset = category.getKpiRelaCategorys();
                    for(KpiRelaCategory kpiRelaCategory:kpiset){
                    	Kpi kpi = kpiRelaCategory.getKpi();
                    	if(kpi.getDeleteStatus()) {
                        	isleaf = false;
                    	}
                    } 
                    node = wrapCategoryNode(category, false, isleaf, expanded);
                    nodes.add(node);
                }
            }

            String categoryIdstr = "";
            StringBuffer categoryBf = new StringBuffer();

            for (String categoryId : categoryIdList) {
                categoryBf.append("'").append(categoryId).append("'").append(",");
            }

            if (categoryBf.length() > 0) {
                categoryIdstr = categoryBf.toString()
                                          .substring(0, categoryBf.length() -
                        1);
            }

            if (categoryIdstr.length() > 0) {
                Map<String, Object> findAssessmentAllMap = findAssessmentMaxEntTimeAllNew("sc",
                        categoryIdstr);
                Map<String, Object> findScRelaKpiCountDetailMap = findScRelaKpiCountDetail();

                for (int i = 0; i < nodes.size(); i++) {
                    node = nodes.get(i);

                    Map<String, Object> valueMap = (Map<String, Object>) findAssessmentAllMap.get(node.get(
                                "id"));
                    node.put("kpiCount",
                        findScRelaKpiCountDetailMap.get(node.get("id")));

                    String css = null;

                    if (null != valueMap) {
                        css = (String) valueMap.get("status");

                        if (null != valueMap.get("value")) {
                            node.put("assessmentValue",
                                ((BigDecimal) valueMap.get("value")).doubleValue());
                        }

                        if (null != valueMap.get("timePeriod")) {
                            node.put("timePeriod",
                                (String) valueMap.get("timePeriod"));
                        }
                    }

                    String cssImg = "icon-status-disable";

                    if ("0alarm_startus_h".equals(css)) {
                        cssImg = "icon-flag-red";
                    } else if ("0alarm_startus_m".equals(css)) {
                        cssImg = "icon-flag-yellow";
                    } else if ("0alarm_startus_l".equals(css)) {
                        cssImg = "icon-flag-green";
                    } 
//                     else if ("0alarm_startus_safe".equals(css)) {
//                        cssImg = "icon-flag-white";
//                    }
                     else {
                    	cssImg = "icon-flag-white";
                    }

                    node.put("iconCls", cssImg);
                }
            }
            if(!"category_root".equals(id)){             
                Category category = o_categoryDAO.get(id);
                Set<KpiRelaCategory> kpiset = category.getKpiRelaCategorys();
                for (KpiRelaCategory scRelaKpi : kpiset) {
                    Kpi kpi = scRelaKpi.getKpi();
                    if(kpi.getDeleteStatus()) {
                    	  //kpiIdList.add(kpi.getId());
                       	  Map<String, Object> kpiResultMap = this.findKpiGatherResultDetail(null,kpi.getId());
                          Map<String, Object> kpiNode = this.wrapKPINode(kpi, null, true, false,kpiResultMap);
                          kpiNode.put("id", id + "_" + kpi.getId());
                          kpiNode.put("leaf", true);
                          nodes.add(kpiNode);
                    }
                  
                }
            }
        }

        Collections.sort(nodes,
            new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1,
                    Map<String, Object> o2) {
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

                    if ("icon-status-disable".equals(s1)) {
                        s1 = "nothing";
                    }

                    if ("icon-status-disable".equals(s2)) {
                        s2 = "nothing";
                    }

                    return (s1).compareTo(s2);
                }
            });
        allNodes.addAll(nodes);

        return allNodes;
    }

    /**
     * 记分卡报表数据获取
     * @param id id 
     * @param query 名称
     * @param year 年
     * @param quarter 季度
     * @param month 月
     * @param week 周
     * @param eType 采集频率
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> scKpiReportTreeLoader(String id,
        String query, String year, String quarter, String month, String week,
        String eType) {
        String timeId = null;

        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) &&
                StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
            timeId = year;
        }

        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) &&
                StringUtils.isBlank(month)) {
            timeId = quarter;
        }

        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) &&
                StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
            timeId = month;
        }

        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) &&
                StringUtils.isNotBlank(quarter) &&
                StringUtils.isNotBlank(week)) {
            timeId = week;
        }

        Map<String, Object> node = null;
        boolean expanded = false;

        if (StringUtils.isNotBlank(query)) { // 是否展开节点
            expanded = true;
        }

        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> allNodes = new ArrayList<Map<String, Object>>();
        String companyId = UserContext.getUser().getCompanyid(); // 所在公司id
        Set<String> idSet = new HashSet<String>();
        List<String> categoryIdList = new ArrayList<String>();

        if (StringUtils.isNotBlank(id)) { // 根据父节点查询所有子节点

            Criteria criteria = o_categoryDAO.createCriteria();
            criteria.add(eq("company.id", companyId));

            if ("category_root".equals(id)) {
                criteria.add(Restrictions.isNull("parent"));
            } else {
                criteria.add(Restrictions.eq("parent.id", id));
            }

            criteria.add(Restrictions.eq("deleteStatus", true));
            criteria.addOrder(Order.asc("name"));

            List<Category> parentCategorys = criteria.list();
            Criteria criteriaQuery = this.o_categoryDAO.createCriteria();
            criteriaQuery.add(eq("company.id", companyId));

            if (StringUtils.isNotBlank(query)) {
                criteriaQuery.add(Restrictions.like("name", query,
                        MatchMode.ANYWHERE));
            }

            criteriaQuery.addOrder(Order.asc("name"));

            List<Category> categoryList = criteriaQuery.list();

            for (Category entity : categoryList) {
                String[] idsTemp = entity.getIdSeq().split("\\.");
                idSet.addAll(Arrays.asList(idsTemp));
            }

            for (Category category : parentCategorys) {
            	Boolean isleaf = true;
                if ((idSet.size() > 0) && idSet.contains(category.getId())) {
                    categoryIdList.add(category.getId());
                    Set<KpiRelaCategory> kpiset = category.getKpiRelaCategorys();
                    for(KpiRelaCategory kpiRelaCategory:kpiset){
                    	Kpi kpi = kpiRelaCategory.getKpi();
                    	if(kpi.getDeleteStatus()) {
                        	isleaf = false;
                    	}
                    } 
                    node = wrapCategoryNode(category, false, isleaf, expanded);
                    nodes.add(node);
                }
            }

            String categoryIdstr = "";
            StringBuffer categoryBf = new StringBuffer();

            for (String categoryId : categoryIdList) {
                categoryBf.append("'").append(categoryId).append("'").append(",");
            }

            if (categoryBf.length() > 0) {
                categoryIdstr = categoryBf.toString()
                                          .substring(0, categoryBf.length() -
                        1);
            }

            if (categoryIdstr.length() > 0) {
                Map<String, Object> findAssessmentAllMap = findSpecificAssessmentResultAll("sc",
                        categoryIdstr, timeId);
                Map<String, Object> findScRelaKpiCountDetailMap = findScRelaKpiCountDetail();

                for (int i = 0; i < nodes.size(); i++) {
                    node = nodes.get(i);

                    Map<String, Object> valueMap = (Map<String, Object>) findAssessmentAllMap.get(node.get(
                                "id"));
                    node.put("kpiCount",
                        findScRelaKpiCountDetailMap.get(node.get("id")));

                    String css = null;

                    if (null != valueMap) {
                        css = (String) valueMap.get("status");

                        if (null != valueMap.get("value")) {
                            node.put("assessmentValue",
                                ((BigDecimal) valueMap.get("value")).doubleValue());
                        }

                        if (null != valueMap.get("timePeriod")) {
                            node.put("timePeriod",
                                (String) valueMap.get("timePeriod"));
                        }
                    }

                    String cssImg = "icon-status-disable";

                    if ("0alarm_startus_h".equals(css)) {
                        cssImg = "icon-flag-red";
                    } else if ("0alarm_startus_m".equals(css)) {
                        cssImg = "icon-flag-yellow";
                    } else if ("0alarm_startus_l".equals(css)) {
                        cssImg = "icon-flag-green";
                    } else {
                    	cssImg = "icon-flag-white";
                    }

                    node.put("iconCls", cssImg);
                }
            }
            if(!"category_root".equals(id)){             
                Category category = o_categoryDAO.get(id);
                Set<KpiRelaCategory> kpiset = category.getKpiRelaCategorys();
                for (KpiRelaCategory scRelaKpi : kpiset) {
                    Kpi kpi = scRelaKpi.getKpi();
                    if(kpi.getDeleteStatus()) {
                    	  //kpiIdList.add(kpi.getId());
                       	  Map<String, Object> kpiResultMap = this.findKpiGatherResultDetail(timeId,kpi.getId());
                          Map<String, Object> kpiNode = this.wrapKPINode(kpi, null, true, false,kpiResultMap);
                          kpiNode.put("id", id + "_" + kpi.getId());
                          kpiNode.put("leaf", true);
                          nodes.add(kpiNode);
                    }
                  
                }
            }
        }

        Collections.sort(nodes,
            new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1,
                    Map<String, Object> o2) {
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

                    if ("icon-status-disable".equals(s1)) {
                        s1 = "nothing";
                    }

                    if ("icon-status-disable".equals(s2)) {
                        s2 = "nothing";
                    }

                    return (s1).compareTo(s2);
                }
            });
        allNodes.addAll(nodes);

        return allNodes;
    }

    /**
     * <pre>
     * 将维度对象封装为树的node节点
     * </pre>
     *
     * @param category 记分卡对象
     * @param canChecked 是否可以选择
     * @param isLeaf 是否是叶子
     * @param expanded 是否展开
     * @return
     * @since fhd　Ver 1.1
     */
    protected Map<String, Object> wrapCategoryNode(Category category,
        Boolean canChecked, Boolean isLeaf, Boolean expanded) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", category.getId());
        //        item.put("dbid", category.getId());
        //        item.put("code", category.getCode());
        item.put("itemName", category.getName());
        item.put("type", "category");
        item.put("deptName", findCategoryBelongDept(category.getId()));

        if (null != category.getParent()) {
            item.put("parentid", category.getParent().getId());
        }

        if (isLeaf) {
            item.put("leaf", category.getIsLeaf());
        }

        if (!isLeaf) {
            item.put("leaf", false);
        }

        if (canChecked) {
            item.put("checked", false);
        }

        if (expanded) {
            item.put("expanded", true);
        }

        return item;
    }

    /**
     * 找到记分卡所属部门
     * @param id 记分卡ID 
     * @return 所属部门
     */
    @SuppressWarnings("unchecked")
    public String findCategoryBelongDept(String id) {
        String sqlQuery = "SELECT ORG.ORG_NAME FROM t_com_category_rela_org_emp  RELA INNER JOIN t_sys_organization ORG ON RELA.ORG_ID = ORG.ID WHERE RELA.ETYPE = 'B' AND RELA.CATEGORY_ID = ? ";

        if (StringUtils.isNotBlank(id)) {
            List<Object> list = o_relaAssessResultDAO.createSQLQuery(sqlQuery,
                    id).list();

            if (list.size() > 0) {
                return list.get(0).toString();
            } else {
                return null;
            }
        }

        return null;
    }

    /**
     * 查询所有类型最新值
     * @return Map<String, String>
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> findAssessmentMaxEntTimeAllNew(String type,
        String ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        sql.append(
            " select result.object_id,result.assessment_status,result.assessment_value,t.TIME_PERIOD_FULL_NAME from  t_kpi_sm_assess_result result ")
           .append(" left join t_com_time_period t on result.time_period_id = t.id ")
           .append(" where  result.TIME_PERIOD_ID=(select LATEST_TIME_PERIOD_ID from ");

        if ("str".equalsIgnoreCase(type)) {
            sql.append("t_kpi_strategy_map r ");
        } else if ("sc".equalsIgnoreCase(type)) {
            sql.append("t_com_category r ");
        }

        sql.append(" where result.OBJECT_ID=r.id )");
        sql.append(" and result.data_type='").append(type).append("'");
        sql.append(" and result.assessment_value is not null");

        if (StringUtils.isNotBlank(ids)) {
            sql.append(" and object_id in(").append(ids).append(")");
        }

        SQLQuery sqlquery = o_relaAssessResultDAO.createSQLQuery(sql.toString());

        List<Object[]> list = sqlquery.list();

        for (Object[] o : list) {
            Map<String, Object> valueMap = new HashMap<String, Object>();
            valueMap.put("value", o[2]);
            valueMap.put("status", o[1]);
            valueMap.put("timePeriod", o[3]);
            map.put(o[0].toString(), valueMap);
        }

        return map;
    }

    /**
     * 根据查询条件找到记分卡采集结果值
     * @param type 类型
     * @param ids id字符串
     * @param timeId 时间区间Id
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> findSpecificAssessmentResultAll(String type,
        String ids, String timeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        sql.append(
            " select result.object_id,result.assessment_status,result.assessment_value,t.TIME_PERIOD_FULL_NAME from  t_kpi_sm_assess_result result ")
           .append(" left join t_com_time_period t on result.time_period_id = t.id ")
           .append(" where  result.TIME_PERIOD_ID= ? ")
           .append(" and result.data_type='").append(type).append("'");

        if (StringUtils.isNotBlank(ids)) {
            sql.append(" and object_id in(").append(ids).append(")");
        }

        SQLQuery sqlquery = o_relaAssessResultDAO.createSQLQuery(sql.toString(),
                timeId);

        List<Object[]> list = sqlquery.list();

        for (Object[] o : list) {
            Map<String, Object> valueMap = new HashMap<String, Object>();
            valueMap.put("value", o[2]);
            valueMap.put("status", o[1]);
            valueMap.put("timePeriod", o[3]);
            map.put(o[0].toString(), valueMap);
        }

        return map;
    }

    /**
     * 查询记分卡数量
     * @return 
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> findScRelaKpiCountDetail() {
        String companyId = UserContext.getUser().getCompanyid();
        Map<String, Object> map = new HashMap<String, Object>();
        String sqlQuery = "select CATEGORY.id,rela.count from t_com_category  CATEGORY left join  " +
            "(select CATEGORY_ID,count(*) as count from t_kpi_kpi_rela_category GROUP BY CATEGORY_ID) rela  " +
            "on CATEGORY.id  = rela.CATEGORY_ID " +
            "where CATEGORY.COMPANY_ID = ? ORDER BY rela.count DESC";
        List<Object[]> list = o_relaAssessResultDAO.createSQLQuery(sqlQuery,
                companyId).list();

        for (Object[] o : list) {
            map.put(o[0].toString(), o[1]);
        }

        return map;
    }
    
    /**
     * 最新记分卡报表
     * @param map 数据map
     * @param start 分页开始index
     * @param limit 分页结束index
     * @param queryName 模糊查询名字
     * @param sortColumn 排序列
     * @param dir 排序顺序
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findLastScKpiDetailResults(
        Map<String, Object> map, int start, int limit, String queryName,
        String sortColumn, String dir) {
      
        Map<String, Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap(
                "KPI");
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        // 获取公司下记分卡的map
    	Map<String, String> categoryMap = o_categoryBO.findScIdAndNameMap(companyid);
        countBuf.append("select count(*) ");
        selectBuf.append("SELECT").append("    SC.ID,") //战略目标ID
        .append("    SC.category_name,") //战略目标名称
        .append("    SCRESULT.ASSESSMENT_VALUE scassess,") //战略目标评估值
        .append("    SCSTATUS.DICT_ENTRY_VALUE scstaus,") //战略目标亮灯状态
        .append("    KPI.KPI_NAME,") //关联指标名称
        .append("    TIME.TIME_PERIOD_FULL_NAME,") //时间维度
        .append("    SK.EWEIGHT,") //权重
        .append("    UNITDICT.DICT_ENTRY_NAME unit,") //单位
        .append("    FREDICT.DICT_ENTRY_NAME fre,") //指标采集频率
        .append("    RESULT.ASSESSMENT_VALUE kpiassess,") //指标评估值
        .append("    RESULT.TARGET_VALUE,") // 指标结果值
        .append("    RESULT.FINISH_VALUE,") // 指标完成值
        .append("    STATUSDICT.DICT_ENTRY_VALUE kpistatus,") // 指标亮灯状态
        .append("    ORG.ORG_NAME, ")
        .append("    SC.ID_SEQ"); // 指标所属部门名称
        fromBuf.append(" FROM T_COM_CATEGORY SC ")
               .append("      LEFT OUTER JOIN T_KPI_SM_ASSESS_RESULT SCRESULT ")
               .append("      ON SC.ID = SCRESULT.OBJECT_ID  ")
               .append("      AND SC.latest_time_period_id = SCRESULT.time_period_id")
               .append("      left outer join t_sys_dict_entry  scstatus")
               .append("      on scresult.assessment_status = scstatus.id ")
               .append("      LEFT outer JOIN (t_kpi_kpi_rela_category sk ")
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
               .append("                 )  on sc.id = sk.category_id ")
               .append("      left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id")
               .append("      left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id")
               .append("      left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
               .append("      left outer join (t_kpi_kpi_rela_org_emp ko ")
               .append("      inner join t_sys_organization  org on ko.org_id  = org.id )")
               .append("      on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
        wherebuf.append(" where 1 = 1");

        //记分卡模糊查询条件添加
        if (StringUtils.isNotBlank(queryName)) {
            wherebuf.append(" and sc.CATEGORY_NAME like '%").append(queryName)
                    .append("%'   ");
        }

        if (StringUtils.isNotBlank(companyid)) {
            wherebuf.append(" and sc.company_id='").append(companyid)
                    .append("' ");
        }

        orderbuf.append(" order by sc.ELEVEL ASC,sc.id,sk.EWEIGHT DESC");

        SQLQuery countQuery = o_categoryDAO.createSQLQuery(countBuf.append(
                    fromBuf).append(wherebuf).toString());
        map.put("totalCount", countQuery.uniqueResult());

        SQLQuery sqlquery = o_categoryDAO.createSQLQuery(selectBuf.append(
                    fromBuf).append(wherebuf).append(orderbuf).toString());

        if (start >= 0) {
            sqlquery.setFirstResult(start);
            sqlquery.setMaxResults(limit);
        }

        List<Object[]> list =  sqlquery.list();
        for (Object[] o : list) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("id", String.valueOf(o[0]));
            dataMap.put("scName", generateScFullName(String.valueOf(o[14]), categoryMap));

            if (null != o[2]) {
                dataMap.put("scAssessValue",
                    convertValue(Double.parseDouble(String.valueOf(o[2])), 2));
            } else {
                dataMap.put("scAssessValue", o[2]);
            }

            dataMap.put("scStatus", o[3]);
            dataMap.put("kpiName", o[4]);
            dataMap.put("timePeriod", o[5]);

            if (null != o[6]) {
                dataMap.put("kpiWeight",
                    convertValue(Double.parseDouble(String.valueOf(o[6])), 2));
            } else {
                dataMap.put("kpiWeight", o[6]);
            }

            dataMap.put("units", o[7]);
            dataMap.put("kpiFrequency", o[8]);

            if (null != o[9]) {
                dataMap.put("assessMentValue",
                    convertValue(Double.parseDouble(String.valueOf(o[9])), 2));
            } else {
                dataMap.put("assessMentValue", o[9]);
            }

            dataMap.put("targetValue", o[10]);
            dataMap.put("finishValue", o[11]);
            dataMap.put("status", o[12]);
            dataMap.put("deptName", o[13]);

            // 根据指标的报表小数位置和单位生成目标值和完成值
            if (null != o[4]) {
                Integer i = null;

                if (null != scaleReportMap.get(o[4])) {
                    i = (Integer) scaleReportMap.get(o[4]);
                } else {
                    i = 2;
                }

                if (null != o[10]) {
                    dataMap.put("targetValue",
                        convertValue(Double.parseDouble(String.valueOf(o[10])),
                            i) + dataMap.get("units"));
                }

                if (null != o[11]) {
                    dataMap.put("finishValue",
                        convertValue(Double.parseDouble(String.valueOf(o[11])),
                            i) + dataMap.get("units"));
                }
            }

            detailList.add(dataMap);
        }

        return detailList;
    }
    
    /**
     * 指定时间维度记分卡报表
     * @param map 数据Map 
     * @param start 开始index
     * @param limit 分页结束index
     * @param queryName 模糊查询名字
     * @param sortColumn 排序列
     * @param dir 排列顺序
     * @param year 年
     * @param quarter 季度
     * @param month 月
     * @param week 周
     * @param frequence 采集频率
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Map<String, Object>> findSpecificScKpiDetailResults(
        Map<String, Object> map, int start, int limit, String queryName,
        String sortColumn, String dir, String year, String quarter,
        String month, String week, String frequence) {
        Map<String, Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap(
                "KPI");
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
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
    	Map<String, String> categoryMap = o_categoryBO.findScIdAndNameMap(companyid);
        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) &&
                StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
            cond.append("   AND scresult.TIME_PERIOD_ID =?");
            kpicond.append(" AND time.id = ?");
            paralist.add(year);
            paralist.add(year);
        }

        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) &&
                StringUtils.isBlank(month)) {
            cond.append("   AND scresult.TIME_PERIOD_ID =?");
            kpicond.append(" AND time.id = ?");
            paralist.add(quarter);
            paralist.add(quarter);
        }

        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) &&
                StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
            cond.append("   AND scresult.TIME_PERIOD_ID =?");
            kpicond.append(" AND time.id = ?");
            paralist.add(month);
            paralist.add(month);
        }

        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) &&
                StringUtils.isNotBlank(quarter) &&
                StringUtils.isNotBlank(week)) {
            cond.append("   AND scresult.TIME_PERIOD_ID =?");
            kpicond.append(" AND time.id = ?");
            paralist.add(week);
            paralist.add(week);
        }

        selectBuf.append("SELECT")
			        .append("    Sc.ID,") //战略目标ID
			        .append("    SC.category_name,") //战略目标名称
			        .append("    SCRESULT.ASSESSMENT_VALUE scassess,") //战略目标评估值
			        .append("    SCSTATUS.DICT_ENTRY_VALUE scstaus,") //战略目标亮灯状态
			        .append("    KPI.KPI_NAME,") //关联指标名称
			        .append("    TIME.TIME_PERIOD_FULL_NAME,") //时间维度
			        .append("    SK.EWEIGHT,") //权重
			        .append("    UNITDICT.DICT_ENTRY_NAME unit,") //单位
			        .append("    FREDICT.DICT_ENTRY_NAME fre,") //指标采集频率
			        .append("    RESULT.ASSESSMENT_VALUE kpiassess,") //指标评估值
			        .append("    RESULT.TARGET_VALUE,") // 指标结果值
			        .append("    RESULT.FINISH_VALUE,") // 指标完成值
			        .append("    STATUSDICT.DICT_ENTRY_VALUE kpistatus,") // 指标亮灯状态
			        .append("    ORG.ORG_NAME, ")
			        .append("    SC.ID_SEQ"); // 指标所属部门名称
        fromBuf.append(" FROM T_COM_CATEGORY SC ")
               .append("      LEFT OUTER JOIN T_KPI_SM_ASSESS_RESULT SCRESULT ")
               .append("      ON SC.ID = SCRESULT.OBJECT_ID  ").append(cond)
               .append("      left outer join t_sys_dict_entry  scstatus")
               .append("      on scresult.assessment_status = scstatus.id ")
               .append("      LEFT outer JOIN (t_kpi_kpi_rela_category sk ")
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
               .append("                 )  on sc.id = sk.category_id ")
               .append("      left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id")
               .append("      left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id")
               .append("      left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
               .append("      left outer join (t_kpi_kpi_rela_org_emp ko ")
               .append("      inner join t_sys_organization  org on ko.org_id  = org.id )")
               .append("      on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
        wherebuf.append(" where 1 = 1");

        // 战略目标模糊查询
        if (StringUtils.isNotBlank(queryName)) {
            wherebuf.append(" and sc.category_name like '%")
                    .append(queryName).append("%'   ");
        }

        if (StringUtils.isNotBlank(companyid)) {
            wherebuf.append(" and sc.company_id='").append(companyid)
                    .append("' ");
        }

        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);
        orderbuf.append("order by sc.ELEVEL,sc.id,sk.EWEIGHT DESC");

        if (StringUtils.isNotBlank(sortColumn)) {
            // 增加额外的排序
        }

        SQLQuery countQuery = o_categoryDAO.createSQLQuery(countBuf.append(
                    fromBuf).append(wherebuf).toString(), paraobjects);
        map.put("totalCount", countQuery.uniqueResult());

        SQLQuery sqlquery = o_categoryDAO.createSQLQuery(selectBuf.append(
                    fromBuf).append(wherebuf).append(orderbuf).toString(),
                paraobjects);

        if (start >= 0) {
            sqlquery.setFirstResult(start);
            sqlquery.setMaxResults(limit);
        }

        List<Object[]> list = sqlquery.list();

        for (Object[] o : list) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("id", String.valueOf(o[0]));
            dataMap.put("scName", String.valueOf(generateScFullName(String.valueOf(o[14]), categoryMap)));

            if (null != o[2]) {
                dataMap.put("scAssessValue",
                    convertValue(Double.parseDouble(String.valueOf(o[2])), 2));
            } else {
                dataMap.put("scAssessValue", o[2]);
            }

            dataMap.put("scStatus", o[3]);
            dataMap.put("kpiName", o[4]);
            dataMap.put("timePeriod", o[5]);

            if (null != o[6]) {
                dataMap.put("kpiWeight",
                    convertValue(Double.parseDouble(String.valueOf(o[6])), 2));
            } else {
                dataMap.put("kpiWeight", o[6]);
            }

            dataMap.put("units", o[7]);
            dataMap.put("kpiFrequency", o[8]);

            if (null != o[9]) {
                dataMap.put("assessMentValue",
                    convertValue(Double.parseDouble(String.valueOf(o[9])), 2));
            } else {
                dataMap.put("assessMentValue", o[9]);
            }

            dataMap.put("targetValue", o[10]);
            dataMap.put("finishValue", o[11]);
            dataMap.put("status", o[12]);
            dataMap.put("deptName", o[13]);

            // 根据指标的报表小数位置和单位生成目标值和完成值
            // 根据指标的报表小数位置和单位生成目标值和完成值
            if (null != o[4]) {
                Integer i = null;

                if (null != scaleReportMap.get(o[4])) {
                    i = (Integer) scaleReportMap.get(o[4]);
                } else {
                    i = 2;
                }

                if (null != o[10]) {
                    dataMap.put("targetValue",
                        convertValue(Double.parseDouble(String.valueOf(o[10])),
                            i) + dataMap.get("units"));
                }

                if (null != o[11]) {
                    dataMap.put("finishValue",
                        convertValue(Double.parseDouble(String.valueOf(o[11])),
                            i) + dataMap.get("units"));
                }
            }

            detailList.add(dataMap);
        }

        return detailList;
    }
    
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
     * 转换数字格式
     * @param valueObj
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
    
    /**封装指标节点
     * @param kpi 指标实体
     * @param canChecked 是否有复选框
     * @param isLeaf 是否为叶子节点
     * @param expanded 是否展开
     * @return
     */
    @SuppressWarnings({ "unchecked" })
	protected Map<String, Object> wrapKPINode(Kpi kpi, Boolean canChecked, Boolean isLeaf, Boolean expanded,Map<String, Object> map) {
        Map<String, Object> item = new HashMap<String, Object>();
        if (kpi != null) {
             Map<String, Object> resultMap = (Map<String, Object>) map.get(kpi.getId());
            //item.put("id", kpi.getId());
            //item.put("code", kpi.getCode());
            item.put("itemName", kpi.getName());
            item.put("type", "kpi");
            // 报表位数默认2位
            Integer p = Contents.DEFAULT_KPI_DOT_POSITION;
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
     * 获取记分卡层级名字
     * @param id 记分卡id
     * @param map 记分卡Id/名称映射数据集
     * @return
     */
    private String generateScFullName(String id,Map<String, String> map ){
    	StringBuffer sb = new StringBuffer();
    	String[] ids = id.split("\\.");
    	for(String scId: ids) {
    		if(StringUtils.isNotBlank(scId)) {
    			sb.append(map.get(scId));
        		sb.append(" ");
    		}   		
    	}
    	return sb.toString();
    }
}
