package com.fhd.sm.business;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.AlarmPlanBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.kpi.KpiTmpDAO;
import com.fhd.dao.kpi.StrategyMapDAO;
import com.fhd.dao.kpi.StrategyMapTmpDAO;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiTmp;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.entity.kpi.StrategyMapTmp;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpOrgBO;

/**战略目标导入业务对象
 * @author xiaozhe
 *
 */
@Service
@SuppressWarnings("unchecked")
public class DataSmImportBO {
	
	/**
	 * 战略目标数据层对象
	 */
	@Autowired
	private StrategyMapTmpDAO o_strategyMapTmpDAO;
	
	@Autowired
	private DataImportCommBO o_dataImportCommBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
	@Autowired
	private KpiTmpDAO o_kpiTmpDAO;
	
	@Autowired
	private AlarmPlanBO o_alarmPlanBO;
	
	@Autowired
	private DictBO o_dictBO;
	
	@Autowired
	private EmpOrgBO o_empOrgBO;
	
    @Autowired
    private TimePeriodBO o_timePeriodBO;
    
    @Autowired
    private RelaAssessResultBO o_relaAssessResultBO;
    
    @Autowired
    private StrategyMapBO o_strategyMapBO;
    
    @Autowired
    private StrategyMapDAO o_strategyMapDAO;
    
    private static Log log = LogFactory.getLog(DataSmImportBO.class);
    
	/**查询公司下要清除的目标ID
	 * @param companyId公司ID
	 * @return
	 */
	private String findRemoveSmIdsByCompanyId(String companyId){
		String idStr = "";
		List<String> idList = new ArrayList<String>();
		List<StrategyMap> smList = o_dataImportCommBO.findStrategyMapAllByCompanyId(companyId);
		for (StrategyMap strategyMap : smList) {
			idList.add("'"+strategyMap.getId()+"'");
		}
		if(idList.size()>0){
			idStr = StringUtils.join(idList,",");
		}
		return idStr;
	}
	
	/**临时表中查询所有验证通过的目标
	 * @return
	 */
	private List<StrategyMapTmp> findAllValidateSm(Boolean validateFlag){
		Criteria criteria = o_strategyMapTmpDAO.createCriteria();
		criteria.addOrder(Order.desc("validateInfo"));
		criteria.addOrder(Order.asc("rowNo"));
		/*if(validateFlag){
			criteria.add(Restrictions.isNull("validateInfo"));
		}else{
			criteria.add(Restrictions.isNotNull("validateInfo"));
		}*/
		criteria.addOrder(Order.asc("level"));
		return  criteria.list();
	}
	
	/**根据名称查询目标所关联的指标
	 * @param smName 目标名称
	 * @param smRelaKpiDatas 目标关联指标sheet页数据
	 * @return
	 */
	private String findSmRelaKpiByName(String smName,Map<String,Map<String,StringBuffer>> smRelaKpiMap){
		String kpiWeight = "";
		if(null!=smRelaKpiMap.get(smName)){
			StringBuffer smRelaKpi = smRelaKpiMap.get(smName).get("kpis");
			if(null!=smRelaKpi){
				kpiWeight = smRelaKpi.toString();
			}
		}
		
		return kpiWeight;
	}
	
	
	/**将目标关联指标sheet页数据封装为map
	 * @param smRelaKpiDatas 目标关联指标sheet页数据
	 * @return
	 */
	private Map<String,Map<String,StringBuffer>> findSmRelaKpi(List<List<String>> smRelaKpiDatas){
		int rowFirst = 2;//读取起始行
		int smCodeIdx = 2;//目标编号所在列
		int kpiCodeIdx = 4;//指标编号所在列
		int kpiNameIdx = 5;//指标名称所在列
		int weightIdx = 6;//权重所在列
		List<Kpi> kpiList = o_kpiBO.findAllKpiListByCompanyId(UserContext.getUser().getCompanyid());
		Map<String,Map<String,StringBuffer>> kpiWeightMap = new HashMap<String,Map<String,StringBuffer>>();
		for (int row = rowFirst; row < smRelaKpiDatas.size(); row++) {
			List<String> smKpiData = smRelaKpiDatas.get(row);
			String name = smKpiData.get(smCodeIdx);
			if(StringUtils.isNotBlank(name)){
				String kpiId = o_dataImportCommBO.findKpiIdByName(smKpiData.get(kpiNameIdx), kpiList);
				String kpiIdCode = o_dataImportCommBO.findKpiIdByCode(smKpiData.get(kpiCodeIdx), kpiList);
				if(StringUtils.isBlank(kpiId)||StringUtils.isBlank(kpiIdCode)){
					kpiId = "null";
				}
				String weight = smKpiData.get(weightIdx);
				if(kpiWeightMap.containsKey(name)){
					kpiWeightMap.get(name).get("kpis").append(kpiId).append(",").append(weight).append(";");
				}else{
					Map<String,StringBuffer> resultMap = new HashMap<String, StringBuffer>();
					StringBuffer weightSb = new StringBuffer();
					weightSb.append(kpiId).append(",").append(weight).append(";");
					resultMap.put("kpis", weightSb);
					resultMap.put("validate", new StringBuffer());
					kpiWeightMap.put(name, resultMap);
				}
				if("null".equals(kpiId)){
					kpiWeightMap.get(name).get("validate").append("指标[").append(smKpiData.get(kpiNameIdx)).append("] 不存在,");
				}
			}
			
		}
		return kpiWeightMap;
	}
	
	 /**查询临时表中所有的指标
	 * @param companyId公司id
	 * @return
	 */
	public List<KpiTmp> findAllTmpKpiListByCompanyId(String companyId){
	        Criteria criteria = this.o_kpiTmpDAO.createCriteria();
	        criteria.add(Restrictions.eq("company",companyId));
	        criteria.add(Restrictions.eq("isKpiCategory", Contents.KPI_TYPE));
	        return criteria.list();
	    }
	 
	
	/**
	 * @param smRelaKpiDatas
	 * @return
	 */
	private Map<String,Map<String,StringBuffer>> findAllSmRelaKpi(List<List<String>> smRelaKpiDatas,Boolean isCoverage){
	    int rowFirst = 2;//读取起始行
	    int smCodeIdx = 2;//目标编号所在列
	    int kpiCodeIdx = 4;//目标编号所在列
	    int kpiNameIdx = 5;//指标名称所在列
	    int weightIdx = 6;//权重所在列
	    List<Kpi> appendKpiList = null;
	    List<KpiTmp> kpiList = findAllTmpKpiListByCompanyId(UserContext.getUser().getCompanyid());
	    if(!isCoverage){//增量
	        appendKpiList = o_kpiBO.findAllKpiListByCompanyId(UserContext.getUser().getCompanyid());
	    }
	    Map<String,Map<String,StringBuffer>> kpiWeightMap = new HashMap<String,Map<String,StringBuffer>>();
	    for (int row = rowFirst; row < smRelaKpiDatas.size(); row++) {
	        List<String> smKpiData = smRelaKpiDatas.get(row);
	        String name = smKpiData.get(smCodeIdx);
	        if(StringUtils.isNotBlank(name)){
	            String kpiId = o_dataImportCommBO.findTmpKpiIdByName(smKpiData.get(kpiNameIdx), kpiList);
	            String kpiIdCode = o_dataImportCommBO.findTmpKpiIdByCode(smKpiData.get(kpiCodeIdx), kpiList);
	            if(isCoverage){
	                if(StringUtils.isBlank(kpiId)||StringUtils.isBlank(kpiIdCode)){
	                    kpiId = "null";
	                }
	            }else{
	                if(null!=appendKpiList){
	                    String appendKpiId = o_dataImportCommBO.findKpiIdByName(smKpiData.get(kpiNameIdx), appendKpiList);
	                    String appendKpiIdCode = o_dataImportCommBO.findKpiIdByCode(smKpiData.get(kpiCodeIdx), appendKpiList);
	                    if(StringUtils.isBlank(kpiId)||StringUtils.isBlank(kpiIdCode)){
	                        if(StringUtils.isBlank(appendKpiId)||StringUtils.isBlank(appendKpiIdCode)){
	                            kpiId = "null";
	                        }else{
	                            kpiId = appendKpiId;
	                        }
	                    }
	                }
	                
	            }
	            
	            String weight = smKpiData.get(weightIdx);
	            if(kpiWeightMap.containsKey(name)){
	                kpiWeightMap.get(name).get("kpis").append(kpiId).append(",").append(weight).append(";");
	            }else{
	                Map<String,StringBuffer> resultMap = new HashMap<String, StringBuffer>();
	                StringBuffer weightSb = new StringBuffer();
	                weightSb.append(kpiId).append(",").append(weight).append(";");
	                resultMap.put("kpis", weightSb);
	                resultMap.put("validate", new StringBuffer());
	                kpiWeightMap.put(name, resultMap);
	            }
	            if("null".equals(kpiId)){
	                kpiWeightMap.get(name).get("validate").append("指标[").append(smKpiData.get(kpiNameIdx)).append("] 不存在,");
	            }
	        }
	        
	    }
	    return kpiWeightMap;
	}
	
	
	
	/**设置是否是叶子节点
	 * @param strategyMapTmpList
	 */
	private void findIsLeaf(List<StrategyMapTmp> strategyMapTmpList){
		for (StrategyMapTmp strategyMapTmp : strategyMapTmpList) {
			boolean isLeaf = true;
			String id = strategyMapTmp.getId();
			for (int j=0;j<strategyMapTmpList.size();j++) {
				StrategyMapTmp strategyMapTmp2 =  strategyMapTmpList.get(j);
				if(id.equals(strategyMapTmp2.getParent())){
					isLeaf = false;
					break;
				}
			}
			strategyMapTmp.setIsLeaf(isLeaf);
		}
	}
	
	/**设置parent和idseq
	 * @param strategyMapTmpList
	 */
	private void findParent(List<StrategyMapTmp> strategyMapTmpList ,List<StrategyMap> strategyMapList ){
		for (StrategyMapTmp strategyMapTmp : strategyMapTmpList) {
			String parent = strategyMapTmp.getParent();
			if(StringUtils.isBlank(parent)){
				strategyMapTmp.setIdSeq("."+strategyMapTmp.getId()+".");
			}else{
				Boolean isExist = false;
				for (int j=0;j<strategyMapTmpList.size();j++) {
					StrategyMapTmp strategyMapTmp2 = strategyMapTmpList.get(j);
					if(parent.equals(strategyMapTmp2.getName())){
						strategyMapTmp.setParent(strategyMapTmp2.getId());
						strategyMapTmp.setIdSeq(strategyMapTmp2.getIdSeq()+strategyMapTmp.getId()+".");
						isExist = true;
					}
				}
				if(!isExist){
				    for (int j=0;j<strategyMapList.size();j++) {
	                    StrategyMap strategyMapTmp2 = strategyMapList.get(j);
	                    if(parent.equals(strategyMapTmp2.getName())){
	                        strategyMapTmp.setParent(strategyMapTmp2.getId());
	                        strategyMapTmp.setIdSeq(strategyMapTmp2.getIdSeq()+strategyMapTmp.getId()+".");
	                        isExist = true;
	                        strategyMapTmp2.setIsLeaf(false);
	                    }
	                }
				}
				if(!isExist){//上级目标填写错误,默认为根节点下的目标
					String parentError = "上级目标错误";
					if(StringUtils.isNotBlank(strategyMapTmp.getValidateInfo())){
						strategyMapTmp.setValidateInfo(strategyMapTmp.getValidateInfo()+parentError);
					}else{
						strategyMapTmp.setValidateInfo("上级目标错误");
					}
					strategyMapTmp.setParent(null);
					strategyMapTmp.setIdSeq("."+strategyMapTmp.getId()+".");
				}
			}
		}
	}
	
	/**查询所有没有通过校验的目标信息,按照行号升序排序
	 * @return
	 */
	public List<Map<String, Object>> findAllInValidateSmTmpList() {
		  List<StrategyMapTmp> strategyMapTmpList = findAllValidateSm(false);
		  List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		  for(StrategyMapTmp tmp : strategyMapTmpList) {
			  Map<String, Object> map = new HashMap<String, Object>();
			  map.put("id", tmp.getId());
			  map.put("rowNum", tmp.getRowNo());
			  map.put("name", tmp.getName());
			  map.put("code", tmp.getCode());
			  map.put("validateInfo", tmp.getValidateInfo());
			  list.add(map);
		  }
		  return list;
	}
	
    /**将excel中的目标采集数据导入到数据库
     * @param excelDatas excel中战略目标的采集数据
     * @return
     * @throws Exception 
     */
    @Transactional
    public Boolean importSmGatherDataToDB(final List<List<String>> excelDatas) {
    	Boolean result = true;
    	//code
    	final int codeIdx = 0;
    	//目标名称
    	final int nameIdx = 1;
    	//频率
    	//final int frequenceIdx = 2;
    	//日期
    	final int dataIdx = 3;
    	//评估值
    	final int valueIdx = 4;
    	//时间区间
    	final int tperiodIdx = 5;
    	//年份
    	final int yearIdx = 6;
    	try{
    		//查询目标编码和ID的map
        	String companyId = UserContext.getUser().getCompanyid();
        	Map<String,String> codeMap = o_strategyMapBO.findSmIdAndCodeMap(companyId);
        	//查询目标关联的告警方案Map
        	final Map<String,Object> alarmMap = o_strategyMapBO.findSmRelaAlarmMap(companyId);
        	
        	String alarmPlanQuery = "select alarm_plan_id,max_value,min_value,is_contain_min,is_contain_max,alarm_icon  from t_com_alarm_region";
        	
        	String resultDataStateQuery = "select s.strategy_map_code,t.eyear from t_kpi_sm_assess_result r inner join t_kpi_strategy_map s on r.object_id=s.id , t_com_time_period t where  s.strategy_map_code in (:codelist) and t.id = r.time_period_id group by s.strategy_map_code,t.eyear";
        	
        	final Map<String, Object> alarmMapCompare = alarmMap;
    		 
    		 List<String> codeList = new ArrayList<String>();
    		 Boolean validateFlag = true;
    		 if (excelDatas != null && excelDatas.size() > 0) {
    			 for (int row = 2; row < excelDatas.size(); row++) {
    				 List<String> rowDatas = (List<String>) excelDatas.get(row);
    				 String code = rowDatas.get(codeIdx);
    				 // 拼接查询in字符串
    				 if(!codeList.contains(code)) {
    					 codeList.add(code);
    				 }
    				 // 验证数据合法性
    				 if(!NumberUtils.isNumber(rowDatas.get(valueIdx))&&!"".equals(rowDatas.get(valueIdx).trim())){				
    					 validateFlag = false;
    					 break;
    				 }
    			 }
    			 if(validateFlag){
    				  List<Object[]> resultYearState = o_strategyMapDAO.createSQLQuery(resultDataStateQuery).setParameterList("codelist", codeList,new StringType()).list();
    				  final Map<String,Object> resultYearMap = new HashMap<String,Object>();
    		    	  for(Object[] o : resultYearState) {
    		    		  String code = (String) o[0];
    		    		  String year = String.valueOf(o[1]) ;
    		    		  if(resultYearMap.containsKey(code)) {
    		    			  List<String> yearList = (List<String>) resultYearMap.get(code);
    		    			  yearList.add(year);
    		    			  resultYearMap.put(code, yearList);
    		    		  } else {
    		    			  List<String> newYearList  = new ArrayList<String>();
    		    			  newYearList.add(year);
    		    			  resultYearMap.put(code, newYearList);
    		    		  }
    		    	  }
    		    	  
    		    	  List<Object[]> alarmRegionList = o_strategyMapDAO.createSQLQuery(alarmPlanQuery).list();
    		    	  for(Object[] o: alarmRegionList) {
    		    		  String alarmPlanId = (String) o[0]; 
    		    		  String maxValue = (String) o[1];
    		    		  String minValue = (String) o[2];
    		    		  String isContainMin = ((String) o[3]).substring(16);
    		    		  String isContainMax = ((String) o[4]).substring(16);
    		    		  String icon  = (String) o[5];
    		    		  Map<String,String> iconMap = new HashMap<String,String>();
    		    		  iconMap.put("maxValue", maxValue);
    		    		  iconMap.put("minValue", minValue);
    		    		  iconMap.put("isContainMin",isContainMin);
    		    		  iconMap.put("isContainMax", isContainMax);
    		    		  if(alarmMapCompare.containsKey(alarmPlanId)) {
    		    			  Map<String, Object> regionMap = (Map<String, Object>) alarmMapCompare.get(alarmPlanId);
    		    			  regionMap.put(icon, iconMap);
    		    			  alarmMapCompare.put(alarmPlanId, regionMap);
    		    		  } else {
    		    			  Map<String,Object> regionMap = new HashMap<String,Object>();
    		    			  regionMap.put(icon, iconMap);
    		    			  alarmMapCompare.put(alarmPlanId, regionMap);
    		    		  }
    		    	  }
    		    	  
    		    	  for (int row = 2; row < excelDatas.size(); row++) {
    		    		     List<String> rowDatas = excelDatas.get(row);
    		    		     String year = rowDatas.get(yearIdx);
    		    		     String code = rowDatas.get(codeIdx);
    		    		     String name = rowDatas.get(nameIdx);
    		    		     List<String> yearList  = (List<String>) resultYearMap.get(code);
    		    		     if(null==yearList){
    		    		         yearList = new ArrayList<String>();
    		    		     }
    						 if(!yearList.contains(year)) {
    							 yearList.add(year);
    							 String smId =  codeMap.get(code);
    							 if(null!=smId){
    							     List<TimePeriod> timeList = o_relaAssessResultBO.findTimePeriodByMonthType(year);
                                     //向评分结果表中插入默认数据;
                                     JSONArray smJsonArray = new JSONArray();
                                     JSONObject smJsonObject = new JSONObject();
                                     smJsonObject.put("objectId", smId);
                                     smJsonObject.put("objectName", name);
                                     smJsonObject.put("companyid", companyId);
                                     smJsonArray.add(smJsonObject);
                                     o_relaAssessResultBO.saveBathResultData(smJsonArray, timeList, "str",year);
                                     resultYearMap.put(code, yearList);
    							 }
    							 
    						 }
    		    	    }
    		    	  
    		    	  o_strategyMapDAO.getSession().doWork(new Work() {
    		  			public void execute(Connection connection) throws SQLException {
    		  				connection.setAutoCommit(false);
    		  				PreparedStatement pst = null;
    		  				String companyId = UserContext.getUser().getCompanyid();
    		  				String sql = "update t_kpi_sm_assess_result "
    		  						+ " set object_name = ? , "
    		  						+ "    assessment_value = ? , "
    		  						+ "    assessment_status = ? "
    		  						+ " where object_id = ("
    		  						+ " select id from t_kpi_strategy_map where strategy_map_code = ? and company_id = ?)"
    		  						+ " and time_period_id = ?";
    		  				pst = connection.prepareStatement(sql);
    		  				for (int row = 2; row < excelDatas.size(); row++) {
    		  					List<String> rowDatas = (List<String>) excelDatas.get(row);
    		  					String code = rowDatas.get(codeIdx);
    		  					String tperiod = rowDatas.get(tperiodIdx);
    		  					String name = rowDatas.get(nameIdx);
    		  					pst.setString(1,name);
    		  					if(StringUtils.isNotBlank(rowDatas.get(valueIdx))){
    		  					    pst.setDouble(2,Double.parseDouble(rowDatas.get(valueIdx)));
    		  					}else{
    		  					  pst.setObject(2,null);
    		  					}
    		  					String alarmPlanId = (String) alarmMap.get(code);
    		  					if (StringUtils.isNotBlank(alarmPlanId)) {
    		  						Map<String, Object> alarmRegion = (Map<String, Object>) alarmMapCompare.get(alarmPlanId);
    		  						if (StringUtils.isNotBlank(rowDatas.get(valueIdx))) {
    	  								Boolean isGetValue = false;
    	  								for(Entry<String, Object> entry : alarmRegion.entrySet()) {
    	  									Map<String, String> keyValueMap = (Map<String, String>)entry.getValue();
    	  									if (validateRegionInfo(keyValueMap,rowDatas.get(valueIdx))) {
    	  										pst.setString(3, entry.getKey());
    	  										isGetValue = true;
    	  									}
    	  								}
    	  								if (!isGetValue) {
    	  									pst.setString(3, null);
    	  								}
    	  							}else{
    	  							  pst.setString(3, null);
    	  							}

    		  					}else{
    		  					  pst.setString(3, null);
    		  					}
    		  					pst.setString(4, code);
    		  					pst.setString(5, companyId);
    		  					pst.setString(6, tperiod);
    		  					pst.addBatch();
    		  				}
    		  				pst.executeBatch();
    		  				connection.commit();
    		  				connection.setAutoCommit(true);
    		  			}

    		  		});
    			 }
    			 
    		 }
    	}catch (Exception e) {
    		log.error("导入目标采集数据失败:exception====="+e.toString());
    		result = false;
    		e.printStackTrace();
		}
			 
    	return result;
    }
    @Transactional
    public JSONObject importSmGatherDataToDBS(final List<List<String>> excelDatas) {
        Boolean result = true;
        //code
        final int codeIdx = 0;
        //目标名称
        final int nameIdx = 1;
        //频率
        final int frequenceIdx = 2;
        //日期
        final int dataIdx = 3;
        //评估值
        final int valueIdx = 4;
        //时间区间
        final int tperiodIdx = 5;
        //年份
        final int yearIdx = 6;
        JSONObject resultObj = new JSONObject();
        JSONArray gatherDatasErrors = new JSONArray();
        resultObj.put("result", result);
        try{
            //查询目标编码和ID的map
            String companyId = UserContext.getUser().getCompanyid();
            Map<String,String> codeMap = o_strategyMapBO.findSmIdAndCodeMap(companyId);
            Map<String,String> nameMap = o_strategyMapBO.findSmNameAndCodeMap(companyId);
            
            String alarmPlanQuery = "select alarm_plan_id,max_value,min_value,is_contain_min,is_contain_max,alarm_icon  from t_com_alarm_region";
            
            String resultDataStateQuery = "select s.strategy_map_code,t.eyear from t_kpi_sm_assess_result r inner join t_kpi_strategy_map s on r.object_id=s.id , t_com_time_period t where  s.strategy_map_code in (:codelist) and t.id = r.time_period_id group by s.strategy_map_code,t.eyear";
            
            List<String> codeList = new ArrayList<String>();
            Boolean validateFlag = true;
            if (excelDatas != null && excelDatas.size() > 0) {
                for (int row = 2; row < excelDatas.size(); row++) {
                    boolean rowValidateFlag = true;
                    StringBuffer msgBuf = new StringBuffer();
                    
                    List<String> rowDatas = (List<String>) excelDatas.get(row);
                    String code = rowDatas.get(codeIdx);
                    String name = rowDatas.get(nameIdx);
                    String frequence = rowDatas.get(frequenceIdx);
                    String date = rowDatas.get(dataIdx);
                    String value = rowDatas.get(valueIdx);
                    String year = rowDatas.get(yearIdx);
                    String timeperiod = rowDatas.get(tperiodIdx);
                    
                    // 拼接查询in字符串
                    if(StringUtils.isBlank(code)||!codeMap.containsKey(code)){
                        rowValidateFlag = false;
                        validateFlag = false;
                        msgBuf.append("编号错误,");
                    }else{
                        if(!codeList.contains(code)) {
                            codeList.add(code);
                        }
                    }
                    if(StringUtils.isBlank(name)||!nameMap.containsKey(name)){
                        rowValidateFlag = false;
                        validateFlag = false;
                        msgBuf.append("名称错误,");
                    }
                    
                    // 验证数据合法性
                    if(!NumberUtils.isNumber(rowDatas.get(valueIdx))&&!"".equals(rowDatas.get(valueIdx).trim())){				
                        validateFlag = false;
                        rowValidateFlag = false;
                        msgBuf.append("评估值错误,");
                    }
                    if(!"月".equals(frequence)){
                        validateFlag = false;
                        rowValidateFlag = false;
                        msgBuf.append("采集频率错误,");
                    }
                    
                    if(!rowValidateFlag){
                        JSONObject rowObj = new JSONObject();
                        rowObj.put("rownum", row+1);
                        rowObj.put("validateMsg", msgBuf.toString());
                        rowObj.put("code", code);
                        rowObj.put("name", name);
                        rowObj.put("frequence", frequence);
                        rowObj.put("gatherDate", date);
                        rowObj.put("value", value);
                        rowObj.put("eyear", year);
                        rowObj.put("timeperiod", timeperiod);
                        gatherDatasErrors.add(rowObj);
                    }
                }
            }
            if(validateFlag){
              //查询目标关联的告警方案Map
                final Map<String,Object> alarmMap = o_strategyMapBO.findSmRelaAlarmMap(companyId);
                final Map<String, Object> alarmMapCompare = alarmMap;
                List<Object[]> resultYearState = o_strategyMapDAO.createSQLQuery(resultDataStateQuery).setParameterList("codelist", codeList,new StringType()).list();
                final Map<String,Object> resultYearMap = new HashMap<String,Object>();
                for(Object[] o : resultYearState) {
                    String code = (String) o[0];
                    String year = String.valueOf(o[1]) ;
                    if(resultYearMap.containsKey(code)) {
                        List<String> yearList = (List<String>) resultYearMap.get(code);
                        yearList.add(year);
                        resultYearMap.put(code, yearList);
                    } else {
                        List<String> newYearList  = new ArrayList<String>();
                        newYearList.add(year);
                        resultYearMap.put(code, newYearList);
                    }
                }
                
                List<Object[]> alarmRegionList = o_strategyMapDAO.createSQLQuery(alarmPlanQuery).list();
                for(Object[] o: alarmRegionList) {
                    String alarmPlanId = (String) o[0]; 
                    String maxValue = (String) o[1];
                    String minValue = (String) o[2];
                    String isContainMin = ((String) o[3]).substring(16);
                    String isContainMax = ((String) o[4]).substring(16);
                    String icon  = (String) o[5];
                    Map<String,String> iconMap = new HashMap<String,String>();
                    iconMap.put("maxValue", maxValue);
                    iconMap.put("minValue", minValue);
                    iconMap.put("isContainMin",isContainMin);
                    iconMap.put("isContainMax", isContainMax);
                    if(alarmMapCompare.containsKey(alarmPlanId)) {
                        Map<String, Object> regionMap = (Map<String, Object>) alarmMapCompare.get(alarmPlanId);
                        regionMap.put(icon, iconMap);
                        alarmMapCompare.put(alarmPlanId, regionMap);
                    } else {
                        Map<String,Object> regionMap = new HashMap<String,Object>();
                        regionMap.put(icon, iconMap);
                        alarmMapCompare.put(alarmPlanId, regionMap);
                    }
                }
                
                for (int row = 2; row < excelDatas.size(); row++) {
                    List<String> rowDatas = excelDatas.get(row);
                    String year = rowDatas.get(yearIdx);
                    String code = rowDatas.get(codeIdx);
                    String name = rowDatas.get(nameIdx);
                    List<String> yearList  = (List<String>) resultYearMap.get(code);
                    if(null==yearList){
                        yearList = new ArrayList<String>();
                    }
                    if(!yearList.contains(year)) {
                        yearList.add(year);
                        String smId =  codeMap.get(code);
                        if(null!=smId){
                            List<TimePeriod> timeList = o_relaAssessResultBO.findTimePeriodByMonthType(year);
                            //向评分结果表中插入默认数据;
                            JSONArray smJsonArray = new JSONArray();
                            JSONObject smJsonObject = new JSONObject();
                            smJsonObject.put("objectId", smId);
                            smJsonObject.put("objectName", name);
                            smJsonObject.put("companyid", companyId);
                            smJsonArray.add(smJsonObject);
                            o_relaAssessResultBO.saveBathResultData(smJsonArray, timeList, "str",year);
                            resultYearMap.put(code, yearList);
                        }
                        
                    }
                }
                
                o_strategyMapDAO.getSession().doWork(new Work() {
                    public void execute(Connection connection) throws SQLException {
                        connection.setAutoCommit(false);
                        PreparedStatement pst = null;
                        String companyId = UserContext.getUser().getCompanyid();
                        String sql = "update t_kpi_sm_assess_result "
                                + " set object_name = ? , "
                                + "    assessment_value = ? , "
                                + "    assessment_status = ? "
                                + " where object_id = ("
                                + " select id from t_kpi_strategy_map where strategy_map_code = ? and company_id = ?)"
                                + " and time_period_id = ?";
                        pst = connection.prepareStatement(sql);
                        for (int row = 2; row < excelDatas.size(); row++) {
                            List<String> rowDatas = (List<String>) excelDatas.get(row);
                            String code = rowDatas.get(codeIdx);
                            String tperiod = rowDatas.get(tperiodIdx);
                            String name = rowDatas.get(nameIdx);
                            pst.setString(1,name);
                            if(StringUtils.isNotBlank(rowDatas.get(valueIdx))){
                                pst.setDouble(2,Double.parseDouble(rowDatas.get(valueIdx)));
                            }else{
                                pst.setObject(2,null);
                            }
                            String alarmPlanId = (String) alarmMap.get(code);
                            if (StringUtils.isNotBlank(alarmPlanId)) {
                                Map<String, Object> alarmRegion = (Map<String, Object>) alarmMapCompare.get(alarmPlanId);
                                if (StringUtils.isNotBlank(rowDatas.get(valueIdx))) {
                                    Boolean isGetValue = false;
                                    for(Entry<String, Object> entry : alarmRegion.entrySet()) {
                                        Map<String, String> keyValueMap = (Map<String, String>)entry.getValue();
                                        if (validateRegionInfo(keyValueMap,rowDatas.get(valueIdx))) {
                                            pst.setString(3, entry.getKey());
                                            isGetValue = true;
                                        }
                                    }
                                    if (!isGetValue) {
                                        pst.setString(3, null);
                                    }
                                }else{
                                    pst.setString(3, null);
                                }
                                
                            }else{
                                pst.setString(3, null);
                            }
                            pst.setString(4, code);
                            pst.setString(5, companyId);
                            pst.setString(6, tperiod);
                            pst.addBatch();
                        }
                        pst.executeBatch();
                        connection.commit();
                        connection.setAutoCommit(true);
                    }
                    
                });
            }else{
                resultObj.put("result", false);
                resultObj.put("errors", gatherDatasErrors);
            }
        }catch (Exception e) {
            log.error("导入目标采集数据失败:exception====="+e.toString());
            result = false;
            e.printStackTrace();
        }
       
        resultObj.put("errors", gatherDatasErrors);
        return resultObj;
    }
    
 	
    /**将excel中的目标和目标关联指标数据导入临时表  tmp_imp_strategy_map
     * @param smDatas 从excel中读取的战略目标数据
     * @param smRelaKpiDatas 从excel中读取的目标关联指标数据
     * @throws ParseException 
     */
    @Transactional
    public Boolean importAllSmDataToTmp(List<List<String>> smDatas,List<List<String>> smRelaKpiDatas,Boolean isCoverage) throws ParseException{
        //清空临时表数据
        Boolean result = true;
        String delSql = "delete from tmp_imp_strategy_map";
        SQLQuery sqlQuery = o_strategyMapTmpDAO.createSQLQuery(delSql);
        sqlQuery.executeUpdate();
        String companyId = UserContext.getUser().getCompanyid();
        List<StrategyMapTmp> strategyMapTmpList = new ArrayList<StrategyMapTmp>();
        
        if(null!=smDatas&&smDatas.size()>0){
            //告警方案map
            Map<String, String> alarmFMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_forecast");
            //预警方案map
            Map<String, String> alarmAMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_alarm");
            //目标关联指标map
            Map<String,Map<String,StringBuffer>> smRelaKpiMap = findAllSmRelaKpi(smRelaKpiDatas,isCoverage);
            //主纬度,辅助纬度List
            List<DictEntry> mainOtherDimList = o_dictBO.findDictEntryByDictTypeId("kpi_dimension");
            //主题和辅助主题list
            List<DictEntry> mainOtherThemeList = o_dictBO.findDictEntryByDictTypeId("kpi_theme");
            //是否list
            List<DictEntry> ynList = o_dictBO.findDictEntryByDictTypeId("0yn");
            //图表类型list
            List<DictEntry> chartList = o_dictBO.findDictEntryByDictTypeId("strategy_map_chart_type");
            
            //部门map
            Map<String,String> orgIdMap = o_dataImportCommBO.findOrgIdMap(companyId);
            //人员map
            Map<String,String> empIdMap = o_dataImportCommBO.findEmpIdMap(companyId);
            
            int parentIdx = 0;//上级目标所在列
            int codeIdx = 1; //编号所在列
            int nameIdx = 2;//目标名称所在列
            int shortNameIdx = 3;//短名称所在列
            int mainDimIdx = 4;//主纬度所在列
            int otherDimIdx = 5;//辅助纬度所在列
            int mainThemeIdx = 6;//战略主题
            int otherThemeIdx = 7;//辅助战略主题
            int ownDeptIdx = 8;//所属部门
            int ownEmpIdx = 9;//所属人员
            int reportDeptIdx = 10;//报告部门
            int reportEmpIdx = 11;//报告人员
            int viewDeptIdx = 12;//查看部门
            int viewEmpIdx = 13;//查看人员
            int isEnableIdx = 14;//是否启用
            int isFocusIdx = 15;//是否关注
            int warningfIdx = 16;//评估值公式
            int forcastfIdx = 17;//预警公式
            int descIdx = 18;//说明
            int warningDateIdx = 19;//方案日期
            int warningIdx = 20;//告警方案
            int forcastIdx = 21;//预警方案
            int chartIdx = 22;//图表类型
            int levelIdx = 23;//层级
            int sortIdx = 24;//序号
            
            
            int recordFirstRow = 2;//目标数据起始记录行数
            // 保存已经导入的编号
            List<String> codeList = new ArrayList<String>();
            // 保存已经导入的名称
            List<String> nameList = new ArrayList<String>();
            if(!isCoverage){//增量
                List<StrategyMap> smList = o_dataImportCommBO.findStrategyMapAllByCompanyId(companyId);
                codeList = o_dataImportCommBO.findSmCodeList(smList);
                nameList = o_dataImportCommBO.findSmNameList(smList);
            }
            for (int row = recordFirstRow; row < smDatas.size(); row++) {
                StrategyMapTmp strategyMapTmp = new StrategyMapTmp();
                strategyMapTmp.setId(Identities.uuid());
                //取出目标数据
                List<String> smRowData = smDatas.get(row);
                //设置行号
                strategyMapTmp.setRowNo(row+1);
                //设置公司ID
                strategyMapTmp.setCompanyId(companyId);
                //设置上级目标
                String parentId = null;
                if(StringUtils.isNotBlank(smRowData.get(parentIdx))){
                    parentId = smRowData.get(parentIdx);
                }
                strategyMapTmp.setParent(parentId);
                //设置编号
                strategyMapTmp.setCode(smRowData.get(codeIdx));
                //设置目标名称
                strategyMapTmp.setName(smRowData.get(nameIdx));
                //设置短名称
                strategyMapTmp.setShortName(smRowData.get(shortNameIdx));
                //设置主纬度
                strategyMapTmp.setMainDim(o_dataImportCommBO.findDictIdByDictName(smRowData.get(mainDimIdx), mainOtherDimList));
                //设置辅助纬度
                strategyMapTmp.setSupportDim(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(otherDimIdx),mainOtherDimList));
                //设置战略主题
                strategyMapTmp.setMainTheme(o_dataImportCommBO.findDictIdByDictName(smRowData.get(mainThemeIdx),mainOtherThemeList));
                //设置辅助战略主题
                strategyMapTmp.setOtherTheme(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(otherThemeIdx),mainOtherThemeList));
                //设置所属部门
                strategyMapTmp.setOwnerDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownDeptIdx), orgIdMap));
                //设置所属人员
                strategyMapTmp.setOwnerEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownEmpIdx),empIdMap));
                //设置报告部门人员
                strategyMapTmp.setCheckDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(viewDeptIdx), orgIdMap));
                
                strategyMapTmp.setCheckEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(viewEmpIdx),empIdMap));
                //设置查看部门人员
                strategyMapTmp.setReportDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(reportDeptIdx), orgIdMap));
                
                strategyMapTmp.setReportEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(reportEmpIdx),empIdMap));
                //设置是否启用
                strategyMapTmp.setStatus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isEnableIdx),ynList));
                //设置是否关注
                strategyMapTmp.setIsFocus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isFocusIdx),ynList));
                //设置评估值公式
                strategyMapTmp.setAssessmentFormula(smRowData.get(warningfIdx));
                //设置预警公式
                strategyMapTmp.setForeWarningSet(smRowData.get(forcastfIdx));
                //设置描述
                strategyMapTmp.setDesc(smRowData.get(descIdx));
                //设置方案日期
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                
                java.util.Date warningDate = null;
                Boolean dateFlag = true;
                try {
                    warningDate = dateformat.parse(smRowData.get(warningDateIdx));
                }
                catch (Exception e) {
                    dateFlag = false;
                }
                
                strategyMapTmp.setWarningEffDate(warningDate);
                
                //设置预警方案
                strategyMapTmp.setForeWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(forcastIdx), alarmAMap));
                //设置告警方案
                strategyMapTmp.setWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(warningIdx),alarmFMap));
                //设置图表类型
                strategyMapTmp.setChartType(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(chartIdx),chartList));
                //设置层级
                strategyMapTmp.setLevel(Integer.valueOf(smRowData.get(levelIdx)));
                
                //设置序号
                if(NumberUtils.isNumber(smRowData.get(sortIdx))){
                    strategyMapTmp.setSort(Integer.valueOf(smRowData.get(sortIdx)));
                }
                
                
                //设置目标关联的指标
                String kpiWeight = findSmRelaKpiByName(smRowData.get(codeIdx),smRelaKpiMap);
                strategyMapTmp.setRelaKpi(kpiWeight);
                
                //设置验证信息
                String validateInfo = this.validateSmData(smRowData,codeList,nameList,isCoverage,orgIdMap,empIdMap);
                if(null!=smRelaKpiMap.get(smRowData.get(codeIdx))){
                    StringBuffer validateSb = smRelaKpiMap.get(smRowData.get(codeIdx)).get("validate");
                    if(validateSb.length()>0){
                        if(validateInfo==null){
                            validateInfo = validateSb.toString();
                        }else{
                            validateInfo += validateSb;
                        }
                    }
                }
                
                if(null==validateInfo){
                    validateInfo = "";
                }
                
                StringBuffer validataInfoBf = new StringBuffer(validateInfo);
                
                //校验方案日期
                if(!dateFlag){
                    validataInfoBf.append("方案日期设置错误,");
                }
                
                //校验是否关注
                if(StringUtils.isNotBlank(smRowData.get(isFocusIdx))&&null==strategyMapTmp.getIsFocus()){
                    validataInfoBf.append("关注设置错误,");
                }
                //校验是否启用
                if(StringUtils.isNotBlank(smRowData.get(isEnableIdx))&&null==strategyMapTmp.getStatus()){
                    validataInfoBf.append("启用设置错误,");
                }
                //校验主纬度
                if(StringUtils.isNotBlank(smRowData.get(mainDimIdx))&&null==strategyMapTmp.getMainDim()){
                    validataInfoBf.append("主纬度设置错误,");
                }
                
                //校验辅助纬度
                if(StringUtils.isNotBlank(smRowData.get(otherDimIdx))){
                    String[] chartTypes = StringUtils.split(smRowData.get(otherDimIdx),",");
                    for (String ctype : chartTypes) {
                        String ctypeId = o_dataImportCommBO.findDictIdByDictName(ctype, mainOtherDimList);
                        if(null==ctypeId){
                            validataInfoBf.append("辅助纬度设置错误,");
                            break;
                        }
                    }
                }
                //校验主题
                if(StringUtils.isNotBlank(smRowData.get(mainThemeIdx))&&null==strategyMapTmp.getMainTheme()){
                    validataInfoBf.append("主题设置错误,");
                }
                
                //校验辅助主题
                if(StringUtils.isNotBlank(smRowData.get(otherThemeIdx))){
                    String[] chartTypes = StringUtils.split(smRowData.get(otherThemeIdx),",");
                    for (String ctype : chartTypes) {
                        String ctypeId = o_dataImportCommBO.findDictIdByDictName(ctype, mainOtherThemeList);
                        if(null==ctypeId){
                            validataInfoBf.append("辅助主题设置错误,");
                            break;
                        }
                    }
                }
                
                
                //校验图表类型
                if(StringUtils.isNotBlank(smRowData.get(chartIdx))){
                    String[] chartTypes = StringUtils.split(smRowData.get(chartIdx),",");
                    for (String ctype : chartTypes) {
                        String ctypeId = o_dataImportCommBO.findDictIdByDictName(ctype, chartList);
                        if(null==ctypeId){
                            validataInfoBf.append("图表类型设置错误,");
                            break;
                        }
                    }
                }
                
                
                //校验告警方案
                if(!alarmFMap.containsKey(smRowData.get(warningIdx))){
                    validataInfoBf.append("告警方案不存在,");
                }
                if(!alarmAMap.containsKey(smRowData.get(forcastIdx))){
                    validataInfoBf.append("预警方案不存在,");
                }
                
                
                strategyMapTmp.setValidateInfo(validataInfoBf.toString());
                
                strategyMapTmpList.add(strategyMapTmp);
            }
            if(strategyMapTmpList.size()>0){
                //设置上级目标,设置idseq
                List<StrategyMap> strategyMapList = new ArrayList<StrategyMap>();
                if(!isCoverage){
                     strategyMapList = o_strategyMapBO.findStrategyMapAll();
                }
                findParent(strategyMapTmpList,strategyMapList);
                //设置isleaf
                findIsLeaf(strategyMapTmpList);
                //批量插入数据
                batchImportSmTmp(strategyMapTmpList);
            }
        }
        
        for (StrategyMapTmp item : strategyMapTmpList) {
            if(StringUtils.isNotBlank(item.getValidateInfo())){
                result = false; 
            }
        }
        return result;
    }
	/**将excel中的目标和目标关联指标数据导入临时表  tmp_imp_strategy_map
	 * @param smDatas 从excel中读取的战略目标数据
	 * @param smRelaKpiDatas 从excel中读取的目标关联指标数据
	 * @throws ParseException 
	 */
	@Transactional
	public Boolean importSmDataToTmp(List<List<String>> smDatas,List<List<String>> smRelaKpiDatas,Boolean isCoverage) throws ParseException{
		//清空临时表数据
		Boolean result = true;
		String delSql = "delete from tmp_imp_strategy_map";
		SQLQuery sqlQuery = o_strategyMapTmpDAO.createSQLQuery(delSql);
		sqlQuery.executeUpdate();
		String companyId = UserContext.getUser().getCompanyid();
		List<StrategyMapTmp> strategyMapTmpList = new ArrayList<StrategyMapTmp>();
		
		if(null!=smDatas&&smDatas.size()>0){
			//告警方案map
			Map<String, String> alarmFMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_forecast");
			//预警方案map
			Map<String, String> alarmAMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_alarm");
			//目标关联指标map
			Map<String,Map<String,StringBuffer>> smRelaKpiMap = findSmRelaKpi(smRelaKpiDatas);
			//主纬度,辅助纬度List
			List<DictEntry> mainOtherDimList = o_dictBO.findDictEntryByDictTypeId("kpi_dimension");
			//主题和辅助主题list
			List<DictEntry> mainOtherThemeList = o_dictBO.findDictEntryByDictTypeId("kpi_theme");
			//是否list
			List<DictEntry> ynList = o_dictBO.findDictEntryByDictTypeId("0yn");
			//图表类型list
			List<DictEntry> chartList = o_dictBO.findDictEntryByDictTypeId("strategy_map_chart_type");
			
			//部门map
			Map<String,String> orgIdMap = o_dataImportCommBO.findOrgIdMap(companyId);
			//人员map
			Map<String,String> empIdMap = o_dataImportCommBO.findEmpIdMap(companyId);
			
			int parentIdx = 0;//上级目标所在列
			int codeIdx = 1; //编号所在列
			int nameIdx = 2;//目标名称所在列
			int shortNameIdx = 3;//短名称所在列
			int mainDimIdx = 4;//主纬度所在列
			int otherDimIdx = 5;//辅助纬度所在列
			int mainThemeIdx = 6;//战略主题
			int otherThemeIdx = 7;//辅助战略主题
			int ownDeptIdx = 8;//所属部门
			int ownEmpIdx = 9;//所属人员
			int reportDeptIdx = 10;//报告部门
			int reportEmpIdx = 11;//报告人员
			int viewDeptIdx = 12;//查看部门
			int viewEmpIdx = 13;//查看人员
			int isEnableIdx = 14;//是否启用
			int isFocusIdx = 15;//是否关注
			int warningfIdx = 16;//评估值公式
			int forcastfIdx = 17;//预警公式
			int descIdx = 18;//说明
			int warningDateIdx = 19;//方案日期
			int warningIdx = 20;//告警方案
			int forcastIdx = 21;//预警方案
			int chartIdx = 22;//图表类型
			int levelIdx = 23;//层级
			int sortIdx = 24;//序号
			
			
			int recordFirstRow = 2;//目标数据起始记录行数
			// 保存已经导入的编号
			List<String> codeList = new ArrayList<String>();
			// 保存已经导入的名称
			List<String> nameList = new ArrayList<String>();
			if(!isCoverage){//增量
				List<StrategyMap> smList = o_dataImportCommBO.findStrategyMapAllByCompanyId(companyId);
				codeList = o_dataImportCommBO.findSmCodeList(smList);
				nameList = o_dataImportCommBO.findSmNameList(smList);
			}
			for (int row = recordFirstRow; row < smDatas.size(); row++) {
				StrategyMapTmp strategyMapTmp = new StrategyMapTmp();
				strategyMapTmp.setId(Identities.uuid());
				//取出目标数据
				List<String> smRowData = smDatas.get(row);
				//设置行号
				strategyMapTmp.setRowNo(row+1);
				//设置公司ID
				strategyMapTmp.setCompanyId(companyId);
				//设置上级目标
				String parentId = null;
				if(StringUtils.isNotBlank(smRowData.get(parentIdx))){
					parentId = smRowData.get(parentIdx);
				}
				strategyMapTmp.setParent(parentId);
				//设置编号
				strategyMapTmp.setCode(smRowData.get(codeIdx));
				//设置目标名称
				strategyMapTmp.setName(smRowData.get(nameIdx));
				//设置短名称
				strategyMapTmp.setShortName(smRowData.get(shortNameIdx));
				//设置主纬度
				strategyMapTmp.setMainDim(o_dataImportCommBO.findDictIdByDictName(smRowData.get(mainDimIdx), mainOtherDimList));
				//设置辅助纬度
				strategyMapTmp.setSupportDim(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(otherDimIdx),mainOtherDimList));
				//设置战略主题
				strategyMapTmp.setMainTheme(o_dataImportCommBO.findDictIdByDictName(smRowData.get(mainThemeIdx),mainOtherThemeList));
				//设置辅助战略主题
				strategyMapTmp.setOtherTheme(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(otherThemeIdx),mainOtherThemeList));
				//设置所属部门
				strategyMapTmp.setOwnerDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownDeptIdx), orgIdMap));
				//设置所属人员
				strategyMapTmp.setOwnerEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownEmpIdx),empIdMap));
				//设置报告部门人员
				strategyMapTmp.setCheckDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(viewDeptIdx), orgIdMap));
				
				strategyMapTmp.setCheckEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(viewEmpIdx),empIdMap));
				//设置查看部门人员
				strategyMapTmp.setReportDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(reportDeptIdx), orgIdMap));
				
				strategyMapTmp.setReportEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(reportEmpIdx),empIdMap));
				//设置是否启用
				strategyMapTmp.setStatus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isEnableIdx),ynList));
				//设置是否关注
				strategyMapTmp.setIsFocus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isFocusIdx),ynList));
				//设置评估值公式
				strategyMapTmp.setAssessmentFormula(smRowData.get(warningfIdx));
				//设置预警公式
				strategyMapTmp.setForeWarningSet(smRowData.get(forcastfIdx));
				//设置描述
				strategyMapTmp.setDesc(smRowData.get(descIdx));
				//设置方案日期
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date warningDate = null;
				Boolean dateFlag = true;
				try {
				    warningDate = dateformat.parse(smRowData.get(warningDateIdx));
                }
                catch (Exception e) {
                    dateFlag = false;
                }
				
				strategyMapTmp.setWarningEffDate(warningDate);
				//设置预警方案
				strategyMapTmp.setForeWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(forcastIdx), alarmAMap));
				//设置告警方案
				strategyMapTmp.setWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(warningIdx),alarmFMap));
				//设置图表类型
				strategyMapTmp.setChartType(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(chartIdx),chartList));
				//设置层级
				strategyMapTmp.setLevel(Integer.valueOf(smRowData.get(levelIdx)));
				
				//设置序号
				if(NumberUtils.isNumber(smRowData.get(sortIdx))){
					strategyMapTmp.setSort(Integer.valueOf(smRowData.get(sortIdx)));
				}
				
				
				//设置目标关联的指标
				String kpiWeight = findSmRelaKpiByName(smRowData.get(codeIdx),smRelaKpiMap);
				strategyMapTmp.setRelaKpi(kpiWeight);
				
				//设置验证信息
				String validateInfo = this.validateSmData(smRowData,codeList,nameList,isCoverage,orgIdMap,empIdMap);
				if(null!=smRelaKpiMap.get(smRowData.get(codeIdx))){
					StringBuffer validateSb = smRelaKpiMap.get(smRowData.get(codeIdx)).get("validate");
					if(validateSb.length()>0){
						if(validateInfo==null){
							validateInfo = validateSb.toString();
						}else{
							validateInfo += validateSb;
						}
					}
				}
				
				if(null==validateInfo){
                    validateInfo = "";
                }
				StringBuffer validataInfoBf = new StringBuffer(validateInfo);
				//校验方案日期
				if(!dateFlag){
				    validataInfoBf.append("方案日期设置错误,");
                }
                //校验是否关注
                if(StringUtils.isNotBlank(smRowData.get(isFocusIdx))&&null==strategyMapTmp.getIsFocus()){
                    validataInfoBf.append("关注设置错误,");
                }
                
              //校验是否启用
                if(StringUtils.isNotBlank(smRowData.get(isEnableIdx))&&null==strategyMapTmp.getStatus()){
                    validataInfoBf.append("启用设置错误,");
                }
                
                //校验图表类型
                if(StringUtils.isNotBlank(smRowData.get(chartIdx))){
                    String[] chartTypes = StringUtils.split(smRowData.get(chartIdx),",");
                    for (String ctype : chartTypes) {
                        String ctypeId = o_dataImportCommBO.findDictIdByDictName(ctype, chartList);
                        if(null==ctypeId){
                            validataInfoBf.append("图表类型设置错误,");
                            break;
                        }
                    }
                }
                
              //校验主纬度
                if(StringUtils.isNotBlank(smRowData.get(mainDimIdx))&&null==strategyMapTmp.getMainDim()){
                    validataInfoBf.append("主纬度设置错误,");
                }
                
                //校验辅助纬度
                if(StringUtils.isNotBlank(smRowData.get(otherDimIdx))){
                    String[] chartTypes = StringUtils.split(smRowData.get(otherDimIdx),",");
                    for (String ctype : chartTypes) {
                        String ctypeId = o_dataImportCommBO.findDictIdByDictName(ctype, mainOtherDimList);
                        if(null==ctypeId){
                            validataInfoBf.append("辅助纬度设置错误,");
                            break;
                        }
                    }
                }
                //校验主题
                if(StringUtils.isNotBlank(smRowData.get(mainThemeIdx))&&null==strategyMapTmp.getMainTheme()){
                    validataInfoBf.append("主题设置错误,");
                }
                
                //校验辅助主题
                if(StringUtils.isNotBlank(smRowData.get(otherThemeIdx))){
                    String[] chartTypes = StringUtils.split(smRowData.get(otherThemeIdx),",");
                    for (String ctype : chartTypes) {
                        String ctypeId = o_dataImportCommBO.findDictIdByDictName(ctype, mainOtherThemeList);
                        if(null==ctypeId){
                            validataInfoBf.append("辅助主题设置错误,");
                            break;
                        }
                    }
                }
                
                //校验告警方案
                if(!alarmFMap.containsKey(smRowData.get(warningIdx))){
                    validataInfoBf.append("告警方案不存在,");
                }
                if(!alarmAMap.containsKey(smRowData.get(forcastIdx))){
                    validataInfoBf.append("预警方案不存在,");
                }
                
				
				strategyMapTmp.setValidateInfo(validataInfoBf.toString());
				
				strategyMapTmpList.add(strategyMapTmp);
			}
			if(strategyMapTmpList.size()>0){
				//设置上级目标,设置idseq
			    List<StrategyMap> strategyMapList = new ArrayList<StrategyMap>();
			    if(!isCoverage){
			        strategyMapList = o_strategyMapBO.findStrategyMapAll();
			    }
				findParent(strategyMapTmpList,strategyMapList);
				//设置isleaf
				findIsLeaf(strategyMapTmpList);
				//批量插入数据
				batchImportSmTmp(strategyMapTmpList);
			}
		}
		
		for (StrategyMapTmp item : strategyMapTmpList) {
			if(StringUtils.isNotBlank(item.getValidateInfo())){
				result = false; 
			}
		}
		return result;
	}
	
	/**
	 * 将目标临时表中的数据导入实际的目标表
	 */
	@Transactional
	public void confirmImportSmData(Boolean isCoverage){
		//临时表中查询所有验证通过的目标
		List<StrategyMapTmp> strategyMapTmpList = findAllValidateSm(true);
		
		if(isCoverage){//是否覆盖
			//删除目标关联表,以及 目标主表
			String companyId = UserContext.getUser().getCompanyid();
			String idStr = findRemoveSmIdsByCompanyId(companyId);
			batchRemoveSmAndSmRelaData(idStr);
		}
		//保存目标基本信息表数据
		if(null!=strategyMapTmpList&&strategyMapTmpList.size()>0){
		    batchSaveTmpToRealStrategy(strategyMapTmpList);
		    String delSql = "delete from tmp_imp_strategy_map";
	        SQLQuery sqlQuery = o_strategyMapTmpDAO.createSQLQuery(delSql);
	        sqlQuery.executeUpdate();
	        //导入目标采集结果数据
	        //batchMergeHistoryData(strategyMapTmpList);
		}
		
		
	}
	
	/**批量插入目标历史数据
	 * @param  strategyMapTmpList 目标临时数据集合
	 */
	@Transactional
	private void batchMergeHistoryData(List<StrategyMapTmp> strategyMapTmpList){
		JSONArray dataJsonArray = new JSONArray();
		String year = DateUtils.getYear(new java.util.Date());
		final List<TimePeriod> timeList = o_timePeriodBO.findTimePeriodByMonthType(year);
		for (StrategyMapTmp strategyMapTmp : strategyMapTmpList) {
            JSONObject smJsObj = new JSONObject();
            smJsObj.put("objectId", strategyMapTmp.getId());
            smJsObj.put("objectName", strategyMapTmp.getName());
            smJsObj.put("companyid", strategyMapTmp.getCompanyId());
            dataJsonArray.add(smJsObj);
        }
		o_relaAssessResultBO.saveBathResultData(dataJsonArray, timeList, "str",year);
	}
	
	/**
	 * 批量删除目标关联表和目标主表
	 */
	@Transactional
	private void batchRemoveSmAndSmRelaData(final String ids){
		o_strategyMapTmpDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				//基本信息sql
				String basicSql = "delete from t_kpi_strategy_map ";
				if(StringUtils.isNotBlank(ids)){
					basicSql += " where id in ( "+ids+" )";
				}
				//维度sql
				String dimSql = "delete from  t_kpi_sm_rela_dim ";
				if(StringUtils.isNotBlank(ids)){
					dimSql+=" where strategy_map_id in ( "+ids+" )";
				}
				//主题sql
				String themeSql = "delete from  t_kpi_sm_rela_theme ";
				if(StringUtils.isNotBlank(ids)){
					themeSql+=" where strategy_map_id in ( "+ids+" )";
				}
				//告警sql
				String alarmSql = "delete from   t_kpi_sm_rela_alarm ";
				if(StringUtils.isNotBlank(ids)){
					alarmSql+=" where strategy_map_id in ( "+ids+" )";
				}
				//关联指标sql
				String relaKpiSql = "delete from  t_kpi_sm_rela_kpi ";
				if(StringUtils.isNotBlank(ids)){
					relaKpiSql+=" where strategy_map_id in ( "+ids+" )";
				}
				//关联部门人员
				String orgSql = "delete from  t_kpi_sm_rela_org_emp ";
				if(StringUtils.isNotBlank(ids)){
					orgSql+=" where strategy_map_id in ( "+ids+" )";
				}
				
				//历史数据
				String historySql = "delete from t_kpi_sm_assess_result ";
				if(StringUtils.isNotBlank(ids)){
					historySql+=" where object_id in ( "+ids+" )";
				}
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement dimPst = connection.prepareStatement(dimSql);
				PreparedStatement themePst = connection.prepareStatement(themeSql);
				PreparedStatement alarmPst = connection.prepareStatement(alarmSql);
				PreparedStatement relaKpiPst = connection.prepareStatement(relaKpiSql);
				PreparedStatement orgPst = connection.prepareStatement(orgSql);
				PreparedStatement historyPst = connection.prepareStatement(historySql);
				
				
				basicPst.addBatch();
				dimPst.addBatch();
				themePst.addBatch();
				alarmPst.addBatch();
				relaKpiPst.addBatch();
				orgPst.addBatch();
				historyPst.addBatch();
				
				basicPst.executeBatch();
				dimPst.executeBatch();
				themePst.executeBatch();
				alarmPst.executeBatch();
				relaKpiPst.executeBatch();
				orgPst.executeBatch();
				historyPst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
	
	/**将目标临时表信息导入真实目标表
	 * @param smList 目标临时对象集合
	 */
	@Transactional
	private void batchSaveTmpToRealStrategy(final List<StrategyMapTmp> strategyMapTmpList){
		o_strategyMapTmpDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				//基本信息sql
				String basicSql = "insert into t_kpi_strategy_map (id,company_id,PARENT_ID,STRATEGY_MAP_NAME,STRATEGY_MAP_CODE,SHORT_NAME,FORECAST_FORMULA,ASSESSMENT_FORMULA,ID_SEQ,ESORT,IS_LEAF,ESTATUS,DELETE_STATUS,EDESC,CHART_TYPE,is_focus,elevel)" +
						" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//维度sql
				String dimSql = "insert into t_kpi_sm_rela_dim(id,sm_dim_id,strategy_map_id,etype) values(?,?,?,?)";
				//主题sql
				String themeSql = "insert into t_kpi_sm_rela_theme(ID,THEME_ID,STRATEGY_MAP_ID,ETYPE) values (?,?,?,?)";
				//告警sql
				String alarmSql = "insert into t_kpi_sm_rela_alarm(ID,STRATEGY_MAP_ID,FC_ALARM_PLAN_ID,R_ALARM_PLAN_ID,START_DATE) values (?,?,?,?,?)";
				//关联指标sql
				String relaKpiSql = "insert into t_kpi_sm_rela_kpi(ID,STRATEGY_MAP_ID,KPI_ID,EWEIGHT) values (?,?,?,?) ";
				//关联部门人员
				String orgSql = "insert into t_kpi_sm_rela_org_emp(id,STRATEGY_MAP_ID,ETYPE,ORG_ID,EMP_ID) values(?,?,?,?,?)";
				
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement dimPst = connection.prepareStatement(dimSql);
				PreparedStatement themePst = connection.prepareStatement(themeSql);
				PreparedStatement alarmPst = connection.prepareStatement(alarmSql);
				PreparedStatement relaKpiPst = connection.prepareStatement(relaKpiSql);
				PreparedStatement orgPst = connection.prepareStatement(orgSql);
				
				for (StrategyMapTmp strategyMapTmp : strategyMapTmpList) {
					//目标基本信息
					basicPst.setString(1, strategyMapTmp.getId());
					basicPst.setString(2, strategyMapTmp.getCompanyId());
					basicPst.setString(3, strategyMapTmp.getParent());
					basicPst.setString(4, strategyMapTmp.getName());
					basicPst.setString(5,strategyMapTmp.getCode());
					basicPst.setString(6, strategyMapTmp.getShortName());
					basicPst.setString(7, strategyMapTmp.getWarningFormula());
					basicPst.setString(8, strategyMapTmp.getAssessmentFormula());
					basicPst.setString(9, strategyMapTmp.getIdSeq());
					if(null!=strategyMapTmp.getSort()){
						basicPst.setInt(10, strategyMapTmp.getSort());
					}else{
						basicPst.setInt(10, 0);
					}
					basicPst.setBoolean(11, strategyMapTmp.getIsLeaf());
					basicPst.setString(12, strategyMapTmp.getStatus());
					basicPst.setBoolean(13, true);
					basicPst.setString(14, strategyMapTmp.getDesc());
					basicPst.setString(15, strategyMapTmp.getChartType());
					basicPst.setString(16, strategyMapTmp.getIsFocus());
					if(null==strategyMapTmp.getLevel()){
						basicPst.setInt(17,0);
					}else{
						basicPst.setInt(17, strategyMapTmp.getLevel());
					}
					basicPst.addBatch();
					//主维度
					String mainDim = strategyMapTmp.getMainDim();
					if(StringUtils.isNotBlank(mainDim)){
						dimPst.setString(1, Identities.uuid());
						dimPst.setString(2, mainDim);
						dimPst.setString(3,strategyMapTmp.getId());
						dimPst.setString(4, Contents.DUTY_DEPARTMENT);
						dimPst.addBatch();
					}
					//辅助纬度
					String otherDims = strategyMapTmp.getSupportDim();
					if(StringUtils.isNotBlank(otherDims)){
						String[] otherDimArray = StringUtils.split(otherDims, ",");
						for(String otherDim : otherDimArray){
							dimPst.setString(1, Identities.uuid());
							dimPst.setString(2, otherDim);
							dimPst.setString(3,strategyMapTmp.getId());
							dimPst.setString(4, Contents.RELATIVE_DEPARTMENT);
							dimPst.addBatch();
						}
					}
					
					//主题
					String mainTheme = strategyMapTmp.getMainTheme();
					if(StringUtils.isNotBlank(mainTheme)){
						themePst.setString(1, Identities.uuid());
						themePst.setString(2, mainTheme);
						themePst.setString(3,strategyMapTmp.getId());
						themePst.setString(4, Contents.DUTY_DEPARTMENT);
						themePst.addBatch();
					}
					
					String otherThemes = strategyMapTmp.getOtherTheme();
					if(StringUtils.isNotBlank(otherThemes)){
						String[] otherThemArray = StringUtils.split(otherThemes, ",");
						for(String otherTheme: otherThemArray){
							themePst.setString(1, Identities.uuid());
							themePst.setString(2, otherTheme);
							themePst.setString(3,strategyMapTmp.getId());
							themePst.setString(4, Contents.RELATIVE_DEPARTMENT);
							themePst.addBatch();
						}
					}
					
					//告警方案
					String warningSet = strategyMapTmp.getWarningSet();
					//预警方案
					String forcastSet = strategyMapTmp.getForeWarningSet();
					java.util.Date warningDate = strategyMapTmp.getWarningEffDate();
					if(StringUtils.isNotBlank(warningSet)||StringUtils.isNotBlank(forcastSet)){
						alarmPst.setString(1, Identities.uuid());
						alarmPst.setString(2, strategyMapTmp.getId());
						alarmPst.setString(3, forcastSet);
						alarmPst.setString(4, warningSet);
						if(null!=warningDate){
							alarmPst.setDate(5,new Date(warningDate.getTime()));
						}else{
							alarmPst.setDate(5,null);
						}
						alarmPst.addBatch();
					}
					//关联的指标
					String relaKpis = strategyMapTmp.getRelaKpi();
					if(StringUtils.isNotBlank(relaKpis)){
						String[] relaKpiArray = StringUtils.split(relaKpis,";");
						for (String relaKpi : relaKpiArray) {
							if(StringUtils.isNotBlank(relaKpi)){
								String[] weights = StringUtils.split(relaKpi,",");
								relaKpiPst.setString(1, Identities.uuid());
								relaKpiPst.setString(2, strategyMapTmp.getId());
								relaKpiPst.setString(3, weights[0]);
								if(weights.length>1){
									if(StringUtils.isNotBlank(weights[1])){
										relaKpiPst.setFloat(4, NumberUtils.toFloat(weights[1]));
									}else{
										relaKpiPst.setFloat(4, 0);
									}
								}
								relaKpiPst.addBatch();
							}
						}
					}
					
					//所属部门和人员
					String ownDept = strategyMapTmp.getOwnerDept();
					String ownEmp = strategyMapTmp.getOwnerEmp();
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, strategyMapTmp.getId());
					orgPst.setString(3, Contents.BELONGDEPARTMENT);
					orgPst.setString(4, ownDept);
					orgPst.setString(5, ownEmp);
					orgPst.addBatch();
					//报告部门人员
					String reportDept = strategyMapTmp.getReportDept();
					String reportEmp = strategyMapTmp.getReportEmp();
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, strategyMapTmp.getId());
					orgPst.setString(3, Contents.REPORTDEPARTMENT);
					orgPst.setString(4, reportDept);
					orgPst.setString(5, reportEmp);
					orgPst.addBatch();
					//查看部门人员
					String viewDept = strategyMapTmp.getCheckDept();
					String viewEmp = strategyMapTmp.getCheckEmp();
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, strategyMapTmp.getId());
					orgPst.setString(3, Contents.VIEWDEPARTMENT);
					orgPst.setString(4, viewDept);
					orgPst.setString(5, viewEmp);
					orgPst.addBatch();
				}
				basicPst.executeBatch();
				dimPst.executeBatch();
				themePst.executeBatch();
				alarmPst.executeBatch();
				relaKpiPst.executeBatch();
				orgPst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
	
	/**批量插入目标临时表
	 * @param strategyMapTmpList 目标临时对象集合
	 */
	@Transactional
	private void batchImportSmTmp(final List<StrategyMapTmp> strategyMapTmpList){
		o_strategyMapTmpDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				PreparedStatement pst = null;
                String sql = "insert into tmp_imp_strategy_map (id,company_id,PARENT_ID,STRATEGY_MAP_NAME,STRATEGY_MAP_CODE,SHORT_NAME,FORECAST_FORMULA,ASSESSMENT_FORMULA," +
                		"ID_SEQ,ESORT,IS_LEAF,ESTATUS,EDESC,CHART_TYPE,is_focus,CHECK_DEPT,REPORT_DEPT,OWNER_DEPT,WARNING_EFF_DATE,WARNING_SET,FORE_WARNING_SET,MAIN_DIM,SUPPORT_DIM," +
                		"main_theme,other_theme,VALIDATE_INFO,rela_kpi,row_no,ELEVEL,CHECK_EMP,REPORT_EMP,OWNER_EMP) " +
                		"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                
                pst = connection.prepareStatement(sql);
                for (StrategyMapTmp strategyMapTmp : strategyMapTmpList) {
                	pst.setString(1, strategyMapTmp.getId());
                	pst.setString(2, strategyMapTmp.getCompanyId());
                	pst.setString(3, strategyMapTmp.getParent());
                	pst.setString(4, strategyMapTmp.getName());
                	pst.setString(5, strategyMapTmp.getCode());
                	pst.setString(6, strategyMapTmp.getShortName());
                	pst.setString(7, strategyMapTmp.getWarningFormula());
                	pst.setString(8, strategyMapTmp.getAssessmentFormula());
                	pst.setString(9, strategyMapTmp.getIdSeq());
                	if(null!=strategyMapTmp.getSort()){
                		pst.setInt(10, strategyMapTmp.getSort());
                	}else{
                		pst.setInt(10, 0);
                	}
                	pst.setBoolean(11, strategyMapTmp.getIsLeaf());
                	pst.setString(12, strategyMapTmp.getStatus());
                	pst.setString(13, strategyMapTmp.getDesc());
                	pst.setString(14, strategyMapTmp.getChartType());
                	pst.setString(15, strategyMapTmp.getIsFocus());
                	pst.setString(16, strategyMapTmp.getCheckDept());
                	pst.setString(17, strategyMapTmp.getReportDept());
                	pst.setString(18, strategyMapTmp.getOwnerDept());
                	if(null!=strategyMapTmp.getWarningEffDate()){
                	    pst.setDate(19, new Date(strategyMapTmp.getWarningEffDate().getTime()));
                	}else{
                	    pst.setDate(19,null);
                	}
                	pst.setString(20, strategyMapTmp.getWarningSet());
                	pst.setString(21, strategyMapTmp.getForeWarningSet());
                	pst.setString(22, strategyMapTmp.getMainDim());
                	pst.setString(23, strategyMapTmp.getSupportDim());
                	pst.setString(24, strategyMapTmp.getMainTheme());
                	pst.setString(25, strategyMapTmp.getOtherTheme());
                	pst.setString(26, strategyMapTmp.getValidateInfo());
                	pst.setString(27, strategyMapTmp.getRelaKpi());
                	pst.setInt(28, strategyMapTmp.getRowNo());
                	pst.setInt(29, strategyMapTmp.getLevel());
                	pst.setString(30, strategyMapTmp.getCheckEmp());
                	pst.setString(31, strategyMapTmp.getReportEmp());
                	pst.setString(32, strategyMapTmp.getOwnerEmp());
                	
                	pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
			}
		});
	}
	
	

	
	/**校验目标行数据合法性
	 * @param smRowData 行数据
	 * @return
	 */
	private String validateSmData(List<String> smRowData,List<String> codeList,List<String> nameList,Boolean isCoverage
			,Map<String,String> orgIdMap,Map<String,String> empIdMap){
		StringBuffer validateSb = new StringBuffer();
		int codeIdx = 1; //编号所在列
		int nameIdx = 2;//目标名称所在列
		int ownDeptIdx = 8;//所属部门
		int ownEmpIdx = 9;//所属人员
		int reportDeptIdx = 10;//报告部门
		int reportEmpIdx = 11;//报告人员
		int viewDeptIdx = 12;//查看部门
		int viewEmpIdx = 13;//查看人员
		String companyId = UserContext.getUser().getCompanyid();
		//验证编号
		String code = smRowData.get(codeIdx);
		if(StringUtils.isBlank(code)){
			validateSb.append("编号为空,");
		}else{
			if(this.validateSome(code, codeList)){
				validateSb.append("编号重复,");
			}else{
				codeList.add(code);
			}
		}
		//验证名称
		String name = smRowData.get(nameIdx);
		if(StringUtils.isBlank(name)){
			validateSb.append("名称为空,");
		}else{
			if(this.validateSome(name, nameList)){
				validateSb.append("名称重复,");
			}else{
				nameList.add(name);	
			}
		}
		//所属部门不能为空
		String ownDept = smRowData.get(ownDeptIdx);
		if(StringUtils.isBlank(ownDept)){
			validateSb.append("所属部门为空,");
		}else{
			if(o_dataImportCommBO.findEmpOrgIdByName(ownDept, orgIdMap)==null){
				validateSb.append("所属部门不存在,");
			}
		}
		//所属人员
		String ownEmp = smRowData.get(ownEmpIdx);
		if(StringUtils.isNotBlank(ownEmp)&&o_dataImportCommBO.findEmpOrgIdByName(ownEmp, empIdMap)==null){
			validateSb.append("所属人员不存在,");
		}
		//查看部门
		String checkDept = smRowData.get(viewDeptIdx);
		if(StringUtils.isNotBlank(checkDept)&&o_dataImportCommBO.findEmpOrgIdByName(checkDept, orgIdMap)==null){
			validateSb.append("查看部门不存在,");
		}
		//查看人员
		String viewEmp = smRowData.get(viewEmpIdx);
		if(StringUtils.isNotBlank(viewEmp)&&o_dataImportCommBO.findEmpOrgIdByName(viewEmp, empIdMap)==null){
			validateSb.append("查看人员不存在,");
		}
		//报告部门
		String reportDept = smRowData.get(reportDeptIdx);
		if(StringUtils.isNotBlank(reportDept)&&o_dataImportCommBO.findEmpOrgIdByName(reportDept, orgIdMap)==null){
			validateSb.append("报告部门不存在,");
		}
		//报告人员
		String reportEmp = smRowData.get(reportEmpIdx);
		if(StringUtils.isNotBlank(reportEmp)&&o_dataImportCommBO.findEmpOrgIdByName(reportEmp, empIdMap)==null){
			validateSb.append("报告人员不存在,");
		}
		//所属部门人员是否匹配
		if(null!=ownEmp&&null!=ownDept){
			if(!o_empOrgBO.isEmpOrgBySome(ownEmp, ownDept, companyId)){
				validateSb.append("所属部门人员不匹配,");
			}
		}
		//查看部门人员是否匹配
		if(null!=checkDept&&null!=viewEmp){
			if(!o_empOrgBO.isEmpOrgBySome(viewEmp, checkDept, companyId)){
				validateSb.append("查看部门人员不匹配,");
			}
		}
		//报告部门人员是否匹配
		if(null!=reportDept&&null!=reportEmp){
			if(!o_empOrgBO.isEmpOrgBySome(reportEmp, reportDept, companyId)){
				validateSb.append("报告部门人员不匹配,");
			}
		}
		if(validateSb.length()==0){
			return null;
		}else{
			validateSb = new StringBuffer(validateSb.substring(0, validateSb.length()-1));
		}
		return validateSb.toString();
	}
	
	/**校验,编号或名称是否在集合中存在
	 * @param some 目标编号或名称
	 * @param somelist
	 * @return
	 */
	private Boolean validateSome(String some,List<String> somelist){
		Boolean result = false;
		for (String item : somelist) {
			if(item.equals(some)){
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**验证评估值是否在告警区间中
	 * @param map 告警区间值map
	 * @param value 评估值
	 * @return
	 */
	public Boolean validateRegionInfo(Map<String, String> map,String value) {
		boolean validateResult = false;
		String maxValueStr = map.get("maxValue");
        String minValueStr = map.get("minValue");
        String maxSign = map.get("isContainMax");
        String minSign = map.get("isContainMin");
        Double finishValue =  Double.valueOf(value);
        if (!Contents.POSITIVE_INFINITY.equals(maxValueStr) && !Contents.NEGATIVE_INFINITY.equals(minValueStr)) {
            Double maxValue = Double.valueOf(maxValueStr);
            Double minValue = Double.valueOf(minValueStr);
            if("<=".equals(minSign)&&"<".equals(maxSign)){
                if (finishValue >= minValue && finishValue < maxValue) {
                	validateResult = true;
                }
            }else if("<=".equals(minSign)&&"<=".equals(maxSign)){
                if (finishValue >= minValue && finishValue <= maxValue) {
                	validateResult = true;
                }
            }else if("<".equals(minSign)&&"<=".equals(maxSign)){
                if (finishValue > minValue && finishValue <= maxValue) {
                	validateResult = true;
                }
            }else if("<".equals(minSign)&&"<".equals(maxSign)){
                if (finishValue > minValue && finishValue < maxValue) {
                	validateResult = true;
                }
            }
            
        }
        else {
            if (Contents.POSITIVE_INFINITY.equals(maxValueStr)) {
                Double minValue = Double.valueOf(minValueStr);
                if("<".equals(minSign)){
                	if (finishValue > Double.valueOf(minValue)) {
                		validateResult = true;
                    }
                }else if("<=".equals(minSign)){
                	if (finishValue >= Double.valueOf(minValue)) {
                		validateResult = true;
                    }
                }
                
            }
            else if (Contents.NEGATIVE_INFINITY.equals(minValueStr)) {
                Double maxValue = Double.valueOf(maxValueStr);
                if("<=".equals(maxSign)){
                	if (finishValue <= maxValue) {
                		validateResult = true;
                    }
                }else if("<".equals(maxSign)){
                	if (finishValue < maxValue) {
                		validateResult = true;
                    }
                }
            }
        }
        return validateResult;
    }
	
}
