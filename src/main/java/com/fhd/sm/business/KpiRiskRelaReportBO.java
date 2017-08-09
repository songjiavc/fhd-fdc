package com.fhd.sm.business;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.risk.RiskDAO;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;

@Service
public class KpiRiskRelaReportBO {
	
	@Autowired
	private RiskDAO o_risKDAO;
	
	@Autowired
	private KpiBO o_kpiBO;
    
	/**
	 * 风险报表数据查询
	 * @param map 数据Map
	 * @param start 开始记录数
	 * @param limit 每页记录数
	 * @param queryName 模糊查询名称
	 * @param sortColumn 排序列
	 * @param dir 排序方向
	 * @return 查询结果集
	 */
	@SuppressWarnings("unchecked")
	public  List<Map<String, Object>> findLastKpiRiskResults(Map<String, Object> map, int start, int limit, String queryName, String sortColumn, String dir,Boolean isGroup){
		Map<String,Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap("KPI");
		// 最后的数据列表(后台分页)
        List<Map<String,Object>> detailList = new ArrayList<Map<String,Object>>();
        // 公司id
        String companyid = UserContext.getUser().getCompanyid();
        // sql文select 部分
        StringBuffer selectBuf = new StringBuffer();
        // sql文from 部分
        StringBuffer fromBuf = new StringBuffer();
        // sql文where 部分
        StringBuffer wherebuf = new StringBuffer();
        // sql文count 部分
        StringBuffer countBuf = new StringBuffer();
        // sql文排序 部分
        StringBuffer orderbuf = new StringBuffer();
        
        selectBuf.append("SELECT ")
                 .append("RISK.ID, ")                            //风险分类Id
                 .append("RISK.RISK_NAME, ")                     //风险事件名称
                 .append("TIME.TIME_PERIOD_FULL_NAME, ")         //事件区间中文名称
                 .append("RISKRELA.EWEIGHT, ")                   //风险指标权重
                 .append("UNITDICT.DICT_ENTRY_NAME UNITS, ")     //指标单位名称
                 .append("FREDICT.DICT_ENTRY_NAME, ")            //指标采集频率中文名
                 .append("RESULT.ASSESSMENT_VALUE, ")            //指标评估值
                 .append("RESULT.TARGET_VALUE, ")                //指标目标值
                 .append("RESULT.FINISH_VALUE, ")                //指标实际值
                 .append("STATUSDICT.DICT_ENTRY_VALUE ESTATUS, ") //指标状态
                 .append("ORG.ORG_NAME, ")                      //指标所属部门名称
                 .append("KPI.KPI_NAME, ")                      //指标名称
                 .append("riskHis.RISK_STATUS,")                // 最新分值
                 .append("riskHis.ASSESSEMENT_STATUS rsAssessment ");// 风险状态
        fromBuf.append("from t_rm_risks risk ")
               .append("left join t_rm_risk_adjust_history riskHis on riskHis.RISK_ID = risk.id and riskHis.IS_LATEST = 1 ")
               .append("inner join  (t_kpi_kpi_rela_risk riskrela ")
               .append("                  inner join t_kpi_kpi kpi on riskrela.KPI_ID  = kpi.id AND kpi.DELETE_STATUS = 1  and kpi.IS_ENABLED = '0yn_y' and kpi.is_kpi_category='KPI' ")
               .append("                  left outer join (t_kpi_kpi_gather_result result ")
               .append("                                  INNER JOIN t_com_time_period time on time.id = result.TIME_PERIOD_ID) ")
               .append("                  on kpi.id=result.kpi_id  and  kpi.latest_time_period_id=result.time_period_id  and  kpi.gather_frequence=time.etype) ")
               .append("on risk.id = riskrela.RISK_ID and riskrela.ETYPE = 'RM' ")
               .append("left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ")
               .append("left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id ")
               .append("left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
               .append("left join (t_kpi_kpi_rela_org_emp ko ")
               .append("           inner join t_sys_organization  org on ko.org_id  = org.id ) ")
               .append("on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
        
         wherebuf.append("where risk.DELETE_ESTATUS = '1' and risk.is_risk_class = 'rbs' ");        
         // 模糊查询
		 if(StringUtils.isNotBlank(queryName)) {
			  wherebuf.append(" and risk.risk_name like '%").append(queryName).append("%'   "); 
		 }
		 // 公司条件过滤
		 if (StringUtils.isNotBlank(companyid)) {
		     wherebuf.append(" and risk.company_id=:companyid ");
		 }
		 // 如果为分组展示则为固定排序
		 if(isGroup) {
			 orderbuf.append(" order by risk.ELEVEL,risk.id,riskrela.eweight DESC,STATUSDICT.DICT_ENTRY_VALUE DESC,org.org_name ");
		 } else {
			 if(StringUtils.isNotBlank(sortColumn)) {
				 orderbuf.append(" order by risk.ELEVEL,risk.id");
				 if("kpiName".equals(sortColumn)) {
					 orderbuf.append(",KPI.KPI_NAME ").append(dir); 
				 }
				 if("kpiWeight".equals(sortColumn)) {
					 orderbuf.append(", RISKRELA.EWEIGHT ").append(dir);
				 }
				 if("assessMentValue".equals(sortColumn)){
					 orderbuf.append(", RESULT.ASSESSMENT_VALUE ").append(dir);
				 }
				 if("targetValue".equals(sortColumn)){
					 orderbuf.append(", RESULT.TARGET_VALUE ").append(dir);
				 }
				 if("finishValue".equals(sortColumn)){
					 orderbuf.append(", RESULT.FINISH_VALUE ").append(dir);
				 }
				 if("deptName".equals(sortColumn)){
					 orderbuf.append(", org.org_name ").append(dir);
				 }
				 if("status".equals(sortColumn)){
					 orderbuf.append(", STATUSDICT.DICT_ENTRY_VALUE ").append(dir);
				 }
				 } 
		 }
		

         countBuf.append("select count(*) ");
		 SQLQuery countQuery = o_risKDAO.createSQLQuery(countBuf.append(fromBuf).append(wherebuf).toString());
		 if(StringUtils.isNotBlank(companyid)) {
			 countQuery.setParameter("companyid",companyid);
		 }
		 // 总共记录数查询
		 map.put("totalCount", countQuery.uniqueResult());
		 SQLQuery sqlquery = o_risKDAO.createSQLQuery(selectBuf.append(fromBuf).append(wherebuf).append(orderbuf).toString());
		 if(StringUtils.isNotBlank(companyid)) {
			 sqlquery.setParameter("companyid",companyid);
		 }
         if(start >= 0) {
       	  sqlquery.setFirstResult(start);
             sqlquery.setMaxResults(limit); 
         }
         List<Object[]> list = sqlquery.list();
         // 格式化查询数据
         for(Object[] o: list) {
        	 Map<String, Object> dataMap = new HashMap<String,Object>();
        	 // 风险分类id
        	 dataMap.put("id", String.valueOf(o[0]));
        	 // 风险分类名称
        	 dataMap.put("riskName", String.valueOf(o[1]));
        	 // 时间区间
			 dataMap.put("timePeriod",o[2]);
			 //权重
			  if(null != o[3]) {
				  dataMap.put("kpiWeight",convertValue(Double.parseDouble(String.valueOf(o[3])),Contents.DEFAULT_KPI_DOT_POSITION));
			  } else {
				  dataMap.put("kpiWeight", o[3]);
			  }
			  // 单位
			  dataMap.put("units", o[4]);
			  // 频率
			  dataMap.put("kpiFrequency", o[5]);
			  if(null != o[6]) {
				   dataMap.put("assessMentValue",convertValue(Double.parseDouble(String.valueOf(o[6])),Contents.DEFAULT_KPI_DOT_POSITION));
			  } else {
				  dataMap.put("assessMentValue", o[6]);
			  }			 
			  dataMap.put("targetValue", o[7]);
			  dataMap.put("finishValue", o[8]);
			  dataMap.put("status", o[9]);
			  dataMap.put("deptName", o[10]);
              dataMap.put("kpiName", o[11]);
              dataMap.put("riskScore",o[12]);
              dataMap.put("riskStatus",o[13]);
			  // 根据指标的报表小数位置和单位生成目标值和完成值
			  if(null != o[11]) {
				  Integer i = null;
				  if(null != scaleReportMap.get(o[11])) {
					 i = (Integer) scaleReportMap.get(o[11]);
				  } else {
					 i = Contents.DEFAULT_KPI_DOT_POSITION; 
				  }
				  if(null != o[7]) {
					  dataMap.put("targetValue" , convertValue(Double.parseDouble(String.valueOf(o[7])),i) + dataMap.get("units"));
				  }
				  if(null != o[8]) {
					  dataMap.put("finishValue" , convertValue(Double.parseDouble(String.valueOf(o[8])) ,i) + dataMap.get("units"));
				  }
			  }
        	 detailList.add(dataMap);
         }		 
		return detailList;
	}
	
	/**
	 * 指定时间维度风险指标查询
	 * @param map 检索map
	 * @param queryName
	 * @param sortColumn 排序列
	 * @param dir 顺序
	 * @param isGroup 是否分组
	 * @return 查询结果集
	 */
	@SuppressWarnings("unchecked")
	public  List<Map<String, Object>> findKpiRiskResultsByTimeInfo(Map<String, Object> map, String queryName, String sortColumn, String dir,Boolean isGroup){
		Map<String,Object> scaleReportMap = o_kpiBO.findAllKpiReportScaleMap("KPI");
		// 最后的数据列表(后台分页)
        List<Map<String,Object>> detailList = new ArrayList<Map<String,Object>>();
        String year = null;
        String quarter = null;
        String month = null;
        String week = null;
//        String eType = null;
        if(map.containsKey("year") && null != map.get("year")){
        	year = (String) map.get("year");
        }
        if(map.containsKey("quarter") && null != map.get("quarter") ){
        	quarter = (String) map.get("quarter");
        }
        if(map.containsKey("month") && null != map.get("month")){
        	month = (String) map.get("month");
        }
        if(map.containsKey("week") && null != map.get("week")){
        	week = (String) map.get("week");
        }
//        if(map.containsKey("eType")){
//        	eType = (String) map.get("eType");
//        }
        String companyid = UserContext.getUser().getCompanyid();
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromBuf = new StringBuffer();
        StringBuffer wherebuf = new StringBuffer();
        StringBuffer countBuf = new StringBuffer();
        StringBuffer orderbuf = new StringBuffer();
        List<Object> paralist = new ArrayList<Object>();
        StringBuffer cond = new StringBuffer();
        StringBuffer kpicond = new StringBuffer();
        String queryTp = null;
        countBuf.append("select count(*) ");
        if (StringUtils.isNotBlank(year) && StringUtils.isBlank(quarter) && StringUtils.isBlank(month) && StringUtils.isBlank(week)) {
        	cond.append("AND time_PERIOD_ID = :queryTp ");
        	queryTp = year;
//        	paralist.add(year);
        	kpicond.append(" AND time.id = ?");
            paralist.add(year);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(month)) {
        	cond.append("AND time_PERIOD_ID = :queryTp ");
        	queryTp = quarter;
        	kpicond.append(" AND time.id = ?");
            paralist.add(quarter);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isBlank(week)) {
        	cond.append("AND time_PERIOD_ID = :queryTp ");
        	queryTp = month;
        	kpicond.append(" AND time.id = ?");
            paralist.add(month);
        }
        if (StringUtils.isNotBlank(year) && StringUtils.isNotBlank(month) && StringUtils.isNotBlank(quarter) && StringUtils.isNotBlank(week)) {
        	cond.append("AND time_PERIOD_ID = :queryTp ");
        	queryTp = week;
        	kpicond.append(" AND time.id = ?");
            paralist.add(week);
        }
        selectBuf.append("SELECT ")
		        .append("RISK.ID, ")                            //风险分类Id
		        .append("RISK.RISK_NAME, ")                     //风险事件名称
		        .append("TIME.TIME_PERIOD_FULL_NAME, ")         //事件区间中文名称
		        .append("RISKRELA.EWEIGHT, ")                   //风险指标权重
		        .append("UNITDICT.DICT_ENTRY_NAME UNITS, ")     //指标单位名称
		        .append("FREDICT.DICT_ENTRY_NAME, ")            //指标采集频率中文名
		        .append("RESULT.ASSESSMENT_VALUE, ")            //指标评估值
		        .append("RESULT.TARGET_VALUE, ")                //指标目标值
		        .append("RESULT.FINISH_VALUE, ")                //指标实际值
		        .append("STATUSDICT.DICT_ENTRY_VALUE ESTATUS, ") //指标状态
		        .append("ORG.ORG_NAME, ")                       //指标所属部门名称
		        .append("KPI.KPI_NAME ");//                      //指标名称
//               .append("riskHis.RISK_STATUS,")                // 最新分值
//               .append("riskHis.ASSESSEMENT_STATUS rsAssessment ");// 风险状态
		fromBuf.append("from t_rm_risks risk ")
		      .append("inner join  (t_kpi_kpi_rela_risk riskrela ")		      
		      .append("                  inner join t_kpi_kpi kpi on riskrela.KPI_ID  = kpi.id AND kpi.DELETE_STATUS = 1  and kpi.IS_ENABLED = '0yn_y' and kpi.is_kpi_category='KPI' ")
		      .append("                  left outer join (t_kpi_kpi_gather_result result ")
		      .append("                                  INNER JOIN t_com_time_period time on time.id = result.TIME_PERIOD_ID  ")
		      .append(kpicond)
		      .append("                                                                                                       )")
		      .append("                  on kpi.id=result.kpi_id  and  kpi.gather_frequence=time.etype) ")
		      .append("on risk.id = riskrela.RISK_ID and riskrela.ETYPE = 'RM' ")
		      .append("left outer join t_sys_dict_entry  unitdict on kpi.units = unitdict.id ")
		      .append("left outer join t_sys_dict_entry  fredict on kpi.gather_frequence  = fredict.id ")
		      .append("left outer join t_sys_dict_entry  statusdict on result.assessment_status = statusdict.id ")
		      .append("left join (t_kpi_kpi_rela_org_emp ko ")
		      .append("           inner join t_sys_organization  org on ko.org_id  = org.id ) ")
		      .append("on ko.KPI_ID = kpi.id and ko.ETYPE = 'B' ");
		
		wherebuf.append("where risk.DELETE_ESTATUS = '1' and risk.is_risk_class = 'rbs' ");        
		// 模糊查询
		if(StringUtils.isNotBlank(queryName)) {
			  wherebuf.append(" and risk.risk_name like '%").append(queryName).append("%'   "); 
		}
		// 公司条件过滤
		if (StringUtils.isNotBlank(companyid)) {
		    wherebuf.append(" and risk.company_id=:companyid ");
		}
		 // 如果为分组展示则为固定排序
		 if(isGroup) {
			 orderbuf.append(" order by risk.ELEVEL,risk.id,riskrela.eweight DESC,STATUSDICT.DICT_ENTRY_VALUE DESC,org.org_name ");
		 } else {
			 orderbuf.append(" order by risk.ELEVEL,risk.id");
			 if(StringUtils.isNotBlank(sortColumn)) {
					//orderbuf.append(",riskrela.eweight DESC,org.org_name"); 
					 if("kpiName".equals(sortColumn)) {
						 orderbuf.append(",KPI.KPI_NAME ").append(dir); 
					 }
					 if("kpiWeight".equals(sortColumn)) {
						 orderbuf.append(", RISKRELA.EWEIGHT ").append(dir);
					 }
					 if("assessMentValue".equals(sortColumn)){
						 orderbuf.append(", RESULT.ASSESSMENT_VALUE ").append(dir);
					 }
					 if("targetValue".equals(sortColumn)){
						 orderbuf.append(", RESULT.TARGET_VALUE ").append(dir);
					 }
					 if("finishValue".equals(sortColumn)){
						 orderbuf.append(", RESULT.FINISH_VALUE ").append(dir);
					 }
					 if("deptName".equals(sortColumn)){
						 orderbuf.append(", org.org_name ").append(dir);
					 }
					 if("status".equals(sortColumn)){
						 orderbuf.append(", STATUSDICT.DICT_ENTRY_VALUE ").append(dir);
					 }
				 } 
		 }
        Object[] paraobjects = new Object[paralist.size()];
        paraobjects = paralist.toArray(paraobjects);
        SQLQuery countQuery = o_risKDAO.createSQLQuery(countBuf.append(fromBuf).append(wherebuf).toString(), paraobjects);
		 if(StringUtils.isNotBlank(companyid)) {
			 countQuery.setParameter("companyid",companyid);
		 }
        map.put("totalCount", countQuery.uniqueResult());
        SQLQuery sqlquery = o_risKDAO.createSQLQuery(
                selectBuf.append(fromBuf).append(wherebuf).append(orderbuf).toString(), paraobjects);
		 if(StringUtils.isNotBlank(companyid)) {
			 sqlquery.setParameter("companyid",companyid);
		 }
        if(map.containsKey("start")) {
         	  sqlquery.setFirstResult((Integer) map.get("start"));
              sqlquery.setMaxResults((Integer) map.get("limit")); 
          }
        List<Object[]> list = sqlquery.list();
        StringBuffer riskSql = new StringBuffer();
        riskSql.append("SELECT risk_status,ASSESSEMENT_STATUS from t_rm_risk_adjust_history  where risk_id = :riskId and time_period_id = :queryTp order by ADJUST_time desc,risk_status desc limit 1 ");
        // 格式化查询数据
        for(Object[] o: list) {
       	 Map<String, Object> dataMap = new HashMap<String,Object>();
       	 // 风险分类id
       	 dataMap.put("id", String.valueOf(o[0]));
       	 Map<String, Object> queryMap = new HashMap<String, Object>();
       	 queryMap.put("riskId", String.valueOf(o[0]));
       	 queryMap.put("queryTp", queryTp);
       	 List<Object[]> riskReusltlist = o_risKDAO.createSQLQuery(riskSql.toString(), queryMap).list();
       	 // 风险分类名称
       	 dataMap.put("riskName", String.valueOf(o[1]));
       	 // 时间区间
			 dataMap.put("timePeriod",o[2]);
			 //权重
			  if(null != o[3]) {
				  dataMap.put("kpiWeight",convertValue(Double.parseDouble(String.valueOf(o[3])),Contents.DEFAULT_KPI_DOT_POSITION));
			  } else {
				  dataMap.put("kpiWeight", o[3]);
			  }
			  // 单位
			  dataMap.put("units", o[4]);
			  // 频率
			  dataMap.put("kpiFrequency", o[5]);
			  if(null != o[6]) {
				   dataMap.put("assessMentValue",convertValue(Double.parseDouble(String.valueOf(o[6])),Contents.DEFAULT_KPI_DOT_POSITION));
			  } else {
				  dataMap.put("assessMentValue", o[6]);
			  }			 
			  dataMap.put("targetValue", o[7]);
			  dataMap.put("finishValue", o[8]);
			  dataMap.put("status", o[9]);
			  dataMap.put("deptName", o[10]);
              dataMap.put("kpiName", o[11]);
              if(riskReusltlist.size() > 0) {
            	  Object[] or = riskReusltlist.get(0);
            	  dataMap.put("riskScore",or[0]);
                  dataMap.put("riskStatus",or[1]);
              }
             
			  // 根据指标的报表小数位置和单位生成目标值和完成值
			  if(null != o[11]) {
				  Integer i = null;
				  if(null != scaleReportMap.get(o[11])) {
					 i = (Integer) scaleReportMap.get(o[11]);
				  } else {
					 i = Contents.DEFAULT_KPI_DOT_POSITION; 
				  }
				  if(null != o[7]) {
					  dataMap.put("targetValue" , convertValue(Double.parseDouble(String.valueOf(o[7])),i) + dataMap.get("units"));
				  }
				  if(null != o[8]) {
					  dataMap.put("finishValue" , convertValue(Double.parseDouble(String.valueOf(o[8])) ,i) + dataMap.get("units"));
				  }
			  }
       	 detailList.add(dataMap);
        }		
		return detailList;
	}
	
    /**
     * 根据传入的位数格式化数字
     * @param valueObj
     * @param m 截取的字符串位数
     * @return 转换成的字符串
     */
	private String convertValue(Double valueObj,Integer m){
		String valueStr = "";
		if(null == valueObj){
			valueStr = "";
    	}else{
    		BigDecimal value = new BigDecimal(valueObj);
        	int valueInt = value.intValue();
        	float valueFloat = value.floatValue();
        	if(valueFloat>valueInt){
        		StringBuffer sb = new StringBuffer("0.");
        		for(int i = 1;i<m;i++){
        			sb.append("0");
        		}
        		valueStr = new DecimalFormat(sb.toString()).format(value);
        	}else{
        		valueStr = new DecimalFormat("0").format(value);
        	}
    	}
		return valueStr;
	}
	
	
}
