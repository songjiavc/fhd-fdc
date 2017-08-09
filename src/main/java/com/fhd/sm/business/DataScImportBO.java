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
import com.fhd.comm.business.CategoryBO;
import com.fhd.comm.business.TimePeriodBO;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.comm.CategoryDAO;
import com.fhd.dao.comm.CategoryTmpDAO;
import com.fhd.dao.kpi.KpiTmpDAO;
import com.fhd.entity.comm.Category;
import com.fhd.entity.comm.CategoryTmp;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.KpiTmp;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpOrgBO;

/**记分卡导入业务对象
 * @author xiaozhe
 *
 */
@Service
@SuppressWarnings("unchecked")
public class DataScImportBO {
	
	@Autowired
	private CategoryTmpDAO o_categoryTmpDAO;
	
	@Autowired
	private KpiTmpDAO o_kpiTmpDAO;
	
	@Autowired
	private DataImportCommBO o_dataImportCommBO;
	
	@Autowired
	private KpiBO o_kpiBO;
	
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
    private CategoryBO o_categoryBO;
    
    @Autowired
    private CategoryDAO o_categoryDAO;
    
    private static Log log = LogFactory.getLog(DataScImportBO.class);
    
    
	/** 查询所有没有通过校验的目标信息,按照行号升序排序
	 *  @return 不合法的导入数据列表
	 */
	public List<Map<String, Object>> findAllInValidateSmTmpList() {
		  List<CategoryTmp> cateTmpList = findAllValidateSm(false);
		  List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		  for(CategoryTmp tmp : cateTmpList) {
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
    
	/** 查询公司下所有记分卡id集合
	 * @param companyId公司ID
	 * @return 公司下的积分卡id拼接字符串
	 */
	private String findRemoveScIdsByCompanyId(String companyId){
		String idStr = "";
		List<String> idList = new ArrayList<String>();
		List<Category> scList = o_dataImportCommBO.findCategoryAllByCompanyId(companyId);
		for (Category item : scList) {
			idList.add("'"+item.getId()+"'");
		}
		if(idList.size()>0){
			idStr = StringUtils.join(idList,",");
		}
		return idStr;
	}
	/**临时表中查询所有验证通过的目标
	 * @return
	 */
	private List<CategoryTmp> findAllValidateSm(Boolean validateFlag){
		Criteria criteria = o_categoryTmpDAO.createCriteria();
		criteria.addOrder(Order.desc("validateInfo"));
		criteria.addOrder(Order.asc("rowNo"));
		/*if(validateFlag){
			criteria.add(Restrictions.isNull("validateInfo"));
		}else{
			criteria.add(Restrictions.isNotNull("validateInfo"));
		}*/
		criteria.addOrder(Order.asc("level"));
		return criteria.list();
	}
	
	/**根据名称查找记分卡关联的指标
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
	private Map<String,Map<String,StringBuffer>> findScRelaKpi(List<List<String>> smRelaKpiDatas){
		int rowFirst = 2;//读取起始行
		int scCodeIdx = 2;//记分卡编号所在列
		int kpiCodeIdx = 4;//指标编号所在列
		int kpiNameIdx = 5;//指标名称所在列
		int weightIdx = 6;//权重所在列
		List<Kpi> kpiList = o_kpiBO.findAllKpiListByCompanyId(UserContext.getUser().getCompanyid());
		Map<String,Map<String,StringBuffer>> kpiWeightMap = new HashMap<String, Map<String,StringBuffer>>();
		for (int row = rowFirst; row < smRelaKpiDatas.size(); row++) {
			List<String> smKpiData = smRelaKpiDatas.get(row);
			String name = smKpiData.get(scCodeIdx);
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
	
	/**将目标关联指标sheet页数据封装为map
	 * @param smRelaKpiDatas 目标关联指标sheet页数据
	 * @return
	 */
	private Map<String,Map<String,StringBuffer>> findAllScRelaKpi(List<List<String>> smRelaKpiDatas,Boolean isCoverage){
	    int rowFirst = 2;//读取起始行
	    int scCodeIdx = 2;//记分卡编号所在列
	    int kpiCodeIdx = 4;//目标编号所在列
	    int kpiNameIdx = 5;//指标名称所在列
	    int weightIdx = 6;//权重所在列
	    List<Kpi> appendKpiList = null;
	    List<KpiTmp> kpiList = findAllTmpKpiListByCompanyId(UserContext.getUser().getCompanyid());
	    if(!isCoverage){
	        appendKpiList = o_kpiBO.findAllKpiListByCompanyId(UserContext.getUser().getCompanyid());
	    }
	    Map<String,Map<String,StringBuffer>> kpiWeightMap = new HashMap<String, Map<String,StringBuffer>>();
	    for (int row = rowFirst; row < smRelaKpiDatas.size(); row++) {
	        List<String> smKpiData = smRelaKpiDatas.get(row);
	        String name = smKpiData.get(scCodeIdx);
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
	 * @param categoryTmpList
	 */
	private void findIsLeaf(List<CategoryTmp> categoryTmpList){
		for (CategoryTmp item : categoryTmpList) {
			String isLeaf = "1";
			String id = item.getId();
			for (int j=0;j<categoryTmpList.size();j++) {
				CategoryTmp categoryTmp2 =  categoryTmpList.get(j);
				if(id.equals(categoryTmp2.getParent())){
					isLeaf = "0";
					break;
				}
			}
			item.setIsLeaf(isLeaf);
		}
	}
	
	/**设置parent和idseq
	 * @param tmpList
	 */
	private void findParent(List<CategoryTmp> tmpList,List<Category> categoryList ){
		for (CategoryTmp categoryItem : tmpList) {
			String parentCode = categoryItem.getParentCode();
			if(StringUtils.isBlank(parentCode)){
				categoryItem.setIdSeq("."+categoryItem.getId()+".");
			}else{
				Boolean isExist = false;
				for (int j=0;j<tmpList.size();j++) {
					CategoryTmp categoryTmp2 = tmpList.get(j);
					if(parentCode.equals(categoryTmp2.getCode())){
						categoryItem.setParent(categoryTmp2.getId());
						categoryItem.setIdSeq(categoryTmp2.getIdSeq()+categoryItem.getId()+".");
						isExist = true;
					}
				}
				if(!isExist){
				    for (int j=0;j<categoryList.size();j++) {
	                    Category categoryTmp2 = categoryList.get(j);
	                    if(parentCode.equals(categoryTmp2.getCode())){
	                        categoryItem.setParent(categoryTmp2.getId());
	                        categoryItem.setIdSeq(categoryTmp2.getIdSeq()+categoryItem.getId()+".");
	                        isExist = true;
	                        categoryTmp2.setIsLeaf(false);
	                    }
	                }
				}
				if(!isExist){//上级目标填写错误,默认为根节点下的目标
					String parentError = "上级记分卡错误";
					if(StringUtils.isNotBlank(categoryItem.getValidateInfo())){
						categoryItem.setValidateInfo(categoryItem.getValidateInfo()+parentError);
					}else{
						categoryItem.setValidateInfo("上级记分卡错误");
					}
					categoryItem.setParent(null);
					categoryItem.setIdSeq("."+categoryItem.getId()+".");
				}
			}
		}
	}
	
    /**将excel中的记分卡采集数据导入到数据库
     * @param excelDatas excel中记分卡的采集数据
     * @return
     * @throws Exception 
     */
    @Transactional
    public Boolean importScGatherDataToDB(final List<List<String>> excelDatas) {
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
        	Map<String,String> codeMap = o_categoryBO.findScIdAndCodeMap(companyId);
        	//查询目标关联的告警方案Map
        	final Map<String,Object> alarmMap = o_categoryBO.findScRelaAlarmMap(companyId);
        	
        	String alarmPlanQuery = "select alarm_plan_id,max_value,min_value,is_contain_min,is_contain_max,alarm_icon  from t_com_alarm_region";
        	
        	String resultDataStateQuery = "select s.category_code,t.eyear from t_kpi_sm_assess_result r inner join t_com_category s on r.object_id=s.id , t_com_time_period t where  s.category_code in (:codelist) and t.id = r.time_period_id group by s.category_code,t.eyear";
        	
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
    				  List<Object[]> resultYearState = o_categoryDAO.createSQLQuery(resultDataStateQuery).setParameterList("codelist", codeList,new StringType()).list();
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
    		    	  
    		    	  List<Object[]> alarmRegionList = o_categoryDAO.createSQLQuery(alarmPlanQuery).list();
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
    		    		     if(StringUtils.isBlank(code)){
    		    		         continue;
    		    		     }
    		    		     String name = rowDatas.get(nameIdx);
    		    		     List<String> yearList  = (List<String>) resultYearMap.get(code);
    		    		     if(null==yearList){
    		    		         yearList = new ArrayList<String>();
    		    		     }
    						 if(!yearList.contains(year)) {
    							 yearList.add(year);
    							 String smId =  codeMap.get(code);
    							 if(null==smId){
    							     continue;
    							 }
    							 List<TimePeriod> timeList = o_relaAssessResultBO.findTimePeriodByMonthType(year);
    					         //向评分结果表中插入默认数据;
    				             JSONArray smJsonArray = new JSONArray();
    				             JSONObject smJsonObject = new JSONObject();
    				             smJsonObject.put("objectId", smId);
    				             smJsonObject.put("objectName", name);
    				             smJsonObject.put("companyid", companyId);
    					         smJsonArray.add(smJsonObject);
    					         o_relaAssessResultBO.saveBathResultData(smJsonArray, timeList, "sc",year);
    							 resultYearMap.put(code, yearList);
    						 }
    		    	    }
    		    	  
    		    	  o_categoryDAO.getSession().doWork(new Work() {
    		  			public void execute(Connection connection) throws SQLException {
    		  				connection.setAutoCommit(false);
    		  				PreparedStatement pst = null;
    		  				String companyId = UserContext.getUser().getCompanyid();
    		  				String sql = "update t_kpi_sm_assess_result "
    		  						+ "   set object_name = ? , "
    		  						+ "       assessment_value = ? , "
    		  						+ "       assessment_status = ? "
    		  						+ "   where object_id = ("
    		  						+ "         select id from t_com_category where category_code = ? and company_id = ?)"
    		  						+ "         and time_period_id = ?";
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
    	  									Map<String, String> keyValueMap = (Map<String, String>) entry.getValue();
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
    		log.error("导入记分卡采集数据失败:exception====="+e.toString());
    		result = false;
    		e.printStackTrace();
		}
			 
    	return result;
    }
    /**将excel中的记分卡采集数据导入到数据库
     * @param excelDatas excel中记分卡的采集数据
     * @return
     * @throws Exception 
     */
    @Transactional
    public JSONObject importScGatherDataToDBS(final List<List<String>> excelDatas) {
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
            Map<String,String> codeMap = o_categoryBO.findScIdAndCodeMap(companyId);
            
            Map<String,String> nameMap = o_categoryBO.findScNameAndCodeMap(companyId);
            
            String alarmPlanQuery = "select alarm_plan_id,max_value,min_value,is_contain_min,is_contain_max,alarm_icon  from t_com_alarm_region";
            
            String resultDataStateQuery = "select s.category_code,t.eyear from t_kpi_sm_assess_result r inner join t_com_category s on r.object_id=s.id , t_com_time_period t where  s.category_code in (:codelist) and t.id = r.time_period_id group by s.category_code,t.eyear";
            
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
                final Map<String,Object> alarmMap = o_categoryBO.findScRelaAlarmMap(companyId);
                final Map<String, Object> alarmMapCompare = alarmMap;
                List<Object[]> resultYearState = o_categoryDAO.createSQLQuery(resultDataStateQuery).setParameterList("codelist", codeList,new StringType()).list();
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
                
                List<Object[]> alarmRegionList = o_categoryDAO.createSQLQuery(alarmPlanQuery).list();
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
                    if(StringUtils.isBlank(code)){
                        continue;
                    }
                    String name = rowDatas.get(nameIdx);
                    List<String> yearList  = (List<String>) resultYearMap.get(code);
                    if(null==yearList){
                        yearList = new ArrayList<String>();
                    }
                    if(!yearList.contains(year)) {
                        yearList.add(year);
                        String smId =  codeMap.get(code);
                        if(null==smId){
                            continue;
                        }
                        List<TimePeriod> timeList = o_relaAssessResultBO.findTimePeriodByMonthType(year);
                        //向评分结果表中插入默认数据;
                        JSONArray smJsonArray = new JSONArray();
                        JSONObject smJsonObject = new JSONObject();
                        smJsonObject.put("objectId", smId);
                        smJsonObject.put("objectName", name);
                        smJsonObject.put("companyid", companyId);
                        smJsonArray.add(smJsonObject);
                        o_relaAssessResultBO.saveBathResultData(smJsonArray, timeList, "sc",year);
                        resultYearMap.put(code, yearList);
                    }
                }
                
                o_categoryDAO.getSession().doWork(new Work() {
                    public void execute(Connection connection) throws SQLException {
                        connection.setAutoCommit(false);
                        PreparedStatement pst = null;
                        String companyId = UserContext.getUser().getCompanyid();
                        String sql = "update t_kpi_sm_assess_result "
                                + "   set object_name = ? , "
                                + "       assessment_value = ? , "
                                + "       assessment_status = ? "
                                + "   where object_id = ("
                                + "         select id from t_com_category where category_code = ? and company_id = ?)"
                                + "         and time_period_id = ?";
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
                                        Map<String, String> keyValueMap = (Map<String, String>) entry.getValue();
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
            log.error("导入记分卡采集数据失败:exception====="+e.toString());
            result = false;
            e.printStackTrace();
        }
        
        resultObj.put("errors", gatherDatasErrors);
        return resultObj;
    }
	
	/**将excel中的记分卡和记分卡关联指标数据导入临时表  tmp_imp_category
	 * @param scDatas 从excel中读取的战略目标数据
	 * @param scRelaKpiDatas 从excel中读取的目标关联指标数据
	 * @throws ParseException 
	 */
	@Transactional
	public Boolean importScDataToTmp(List<List<String>> scDatas,List<List<String>> scRelaKpiDatas,Boolean isCoverage) throws ParseException{
		//清空临时表数据
		Boolean result = true;
		String delSql = "delete from tmp_imp_category";
		SQLQuery sqlQuery = o_categoryTmpDAO.createSQLQuery(delSql);
		sqlQuery.executeUpdate();
		String companyId = UserContext.getUser().getCompanyid();
		List<CategoryTmp> tmpList = new ArrayList<CategoryTmp>();
		
		if(null!=scDatas&&scDatas.size()>0){
			//告警方案map
			Map<String, String> alarmFMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_forecast");
			//预警方案map
			Map<String, String> alarmAMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_alarm");
			//目标关联指标map
			Map<String,Map<String,StringBuffer>> scRelaKpiMap = findScRelaKpi(scRelaKpiDatas);
			//是否list
			List<DictEntry> ynList = o_dictBO.findDictEntryByDictTypeId("0yn");
			//图表类型list
			List<DictEntry> chartList = o_dictBO.findDictEntryByDictTypeId("0com_catalog_chart_type");
			
			List<DictEntry> dataList = o_dictBO.findDictEntryByDictTypeId("0category_data_type");
			
			//部门map
			Map<String,String> orgIdMap = o_dataImportCommBO.findOrgIdMap(companyId);
			//人员map
			Map<String,String> empIdMap = o_dataImportCommBO.findEmpIdMap(companyId);
			
			int parentCodeIdx = 0;//上级编号
			int parentName = 1; //上级名称
			int codeIdx = 2;//编号所在列
			int nameIdx = 3;//名称所在列
			int descIdx = 4;//说明
			int ownDeptIdx = 5;//所属部门
			int ownEmpIdx = 6;//所属人员
			int isFocusIdx = 7;//是否关注
			int isCalcIdx = 8;//是否计算
			int isEnableIdx = 9;//是否启用
			int resultfIdx = 10;//评估值公式
			int forcastfIdx = 11;//预警公式
			int warningDateIdx = 12;//方案日期
			int warningIdx = 13;//告警方案
			int forcastIdx = 14;//预警方案
			int dataTypeIdx = 15;//数据类型
			int chartIdx = 16;//图表类型
			int levelIdx = 17;//层级
			int sortIdx = 18;//序号
			
			
			int recordFirstRow = 2;//目标数据起始记录行数
			// 保存已经导入的编号
			List<String> codeList = new ArrayList<String>();
			if(!isCoverage){//增量
				List<Category> scList = o_dataImportCommBO.findCategoryAllByCompanyId(companyId);
				codeList = o_dataImportCommBO.findScCodeList(scList);
			}
			for (int row = recordFirstRow; row < scDatas.size(); row++) {
				CategoryTmp item = new CategoryTmp();
				item.setId(Identities.uuid());
				//取出数据
				List<String> smRowData = scDatas.get(row);
				//设置行号
				item.setRowNo(row+1);
				//设置公司ID
				item.setCompany(companyId);
				//设置上级记分卡
				String parentId = null;
				if(StringUtils.isNotBlank(smRowData.get(parentName))){
					parentId = smRowData.get(parentName);
				}
				
				item.setParent(parentId);
				//设置编号
				item.setCode(smRowData.get(codeIdx));
				//上级编号
				item.setParentCode(smRowData.get(parentCodeIdx));
				//设置目标名称
				item.setName(smRowData.get(nameIdx));
				
				//设置所属部门
				item.setOwnDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownDeptIdx), orgIdMap));
				//设置所属人员
				item.setOwnEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownEmpIdx),empIdMap));
				//设置是否启用
				item.setStatus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isEnableIdx),ynList));
				//设置是否关注
				item.setIsFocus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isFocusIdx),ynList));
				//是否计算
				item.setCalc(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isCalcIdx),ynList));
				//数据类型 
				item.setDateType(o_dataImportCommBO.findDictIdByDictName(smRowData.get(dataTypeIdx),dataList));
				//设置评估值公式
				item.setAssessmentFormula(smRowData.get(resultfIdx));
				//设置预警公式
				item.setForecastFormula(smRowData.get(forcastfIdx));
				//设置描述
				item.setDesc(smRowData.get(descIdx));
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
                
                item.setWarningEffDate(warningDate);
				//设置预警方案
				item.setForeWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(forcastIdx), alarmAMap));
				//设置告警方案
				item.setWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(warningIdx),alarmFMap));
				//设置图表类型
				item.setChartType(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(chartIdx),chartList));
				//设置层级
				if(NumberUtils.isNumber(smRowData.get(levelIdx))){
					item.setLevel(Integer.valueOf(smRowData.get(levelIdx)));
				}
				
				//设置序号
				if(NumberUtils.isNumber(smRowData.get(sortIdx))){
					item.setSort(Integer.valueOf(smRowData.get(sortIdx)));
				}
				
				
				//设置目标关联的指标
				String kpiWeight = findSmRelaKpiByName(smRowData.get(codeIdx),scRelaKpiMap);
				item.setRelaKpi(kpiWeight);
				
				//设置验证信息
				String validateInfo = this.validateSmData(smRowData,codeList,isCoverage,orgIdMap,empIdMap);
				if(null!=scRelaKpiMap.get(smRowData.get(codeIdx))){
					StringBuffer validateSb = scRelaKpiMap.get(smRowData.get(codeIdx)).get("validate");
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
                if(StringUtils.isNotBlank(smRowData.get(isFocusIdx))&&null==item.getIsFocus()){
                    validataInfoBf.append("关注设置错误,");
                }
                
                //校验是否计算
                if(StringUtils.isNotBlank(smRowData.get(isCalcIdx))&&null==item.getCalc()){
                    validataInfoBf.append("计算设置错误,");
                }
                //校验是否启用
                if(StringUtils.isNotBlank(smRowData.get(isEnableIdx))&&null==item.getStatus()){
                    validataInfoBf.append("启用设置错误,");
                }
                //校验数据类型
                if(StringUtils.isNotBlank(smRowData.get(dataTypeIdx))&&null==item.getDateType()){
                    validataInfoBf.append("数据类型设置错误,");
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
				item.setValidateInfo(validataInfoBf.toString());
				
				tmpList.add(item);
			}
			if(tmpList.size()>0){
				//设置上级目标,设置idseq
			    List<Category> categoryList = new ArrayList<Category>();
                if(!isCoverage){
                    categoryList = o_categoryBO.findCategoryAllByTask();
                }
				findParent(tmpList,categoryList);
				//设置isleaf
				findIsLeaf(tmpList);
				//批量插入数据
				batchImportSmTmp(tmpList);
			}
		}
		
		for (CategoryTmp item : tmpList) {
			if(StringUtils.isNotBlank(item.getValidateInfo())){
				result = false; 
			}
		}
		return result;
	}
	/**将excel中的记分卡和记分卡关联指标数据导入临时表  tmp_imp_category
	 * @param scDatas 从excel中读取的战略目标数据
	 * @param scRelaKpiDatas 从excel中读取的目标关联指标数据
	 * @throws ParseException 
	 */
	@Transactional
	public Boolean importAllScDataToTmp(List<List<String>> scDatas,List<List<String>> scRelaKpiDatas,Boolean isCoverage) throws ParseException{
	    //清空临时表数据
	    Boolean result = true;
	    String delSql = "delete from tmp_imp_category";
	    SQLQuery sqlQuery = o_categoryTmpDAO.createSQLQuery(delSql);
	    sqlQuery.executeUpdate();
	    String companyId = UserContext.getUser().getCompanyid();
	    List<CategoryTmp> tmpList = new ArrayList<CategoryTmp>();
	    
	    if(null!=scDatas&&scDatas.size()>0){
	        //告警方案map
	        Map<String, String> alarmFMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_forecast");
	        //预警方案map
	        Map<String, String> alarmAMap = o_alarmPlanBO.findAlarmPlanMap("0alarm_type_kpi_alarm");
	        //目标关联指标map
	        Map<String,Map<String,StringBuffer>> scRelaKpiMap = findAllScRelaKpi(scRelaKpiDatas,isCoverage);
	        //是否list
	        List<DictEntry> ynList = o_dictBO.findDictEntryByDictTypeId("0yn");
	        //图表类型list
	        List<DictEntry> chartList = o_dictBO.findDictEntryByDictTypeId("0com_catalog_chart_type");
	        
	        List<DictEntry> dataList = o_dictBO.findDictEntryByDictTypeId("0category_data_type");
	        
	        //部门map
	        Map<String,String> orgIdMap = o_dataImportCommBO.findOrgIdMap(companyId);
	        //人员map
	        Map<String,String> empIdMap = o_dataImportCommBO.findEmpIdMap(companyId);
	        
	        int parentCodeIdx = 0;//上级编号
	        int parentName = 1; //上级名称
	        int codeIdx = 2;//编号所在列
	        int nameIdx = 3;//名称所在列
	        int descIdx = 4;//说明
	        int ownDeptIdx = 5;//所属部门
	        int ownEmpIdx = 6;//所属人员
	        int isFocusIdx = 7;//是否关注
	        int isCalcIdx = 8;//是否计算
	        int isEnableIdx = 9;//是否启用
	        int resultfIdx = 10;//评估值公式
	        int forcastfIdx = 11;//预警公式
	        int warningDateIdx = 12;//方案日期
	        int warningIdx = 13;//告警方案
	        int forcastIdx = 14;//预警方案
	        int dataTypeIdx = 15;//数据类型
	        int chartIdx = 16;//图表类型
	        int levelIdx = 17;//层级
	        int sortIdx = 18;//序号
	        
	        
	        int recordFirstRow = 2;//目标数据起始记录行数
	        // 保存已经导入的编号
	        List<String> codeList = new ArrayList<String>();
	        if(!isCoverage){//增量
	            List<Category> scList = o_dataImportCommBO.findCategoryAllByCompanyId(companyId);
	            codeList = o_dataImportCommBO.findScCodeList(scList);
	        }
	        for (int row = recordFirstRow; row < scDatas.size(); row++) {
	            CategoryTmp item = new CategoryTmp();
	            item.setId(Identities.uuid());
	            //取出数据
	            List<String> smRowData = scDatas.get(row);
	            //设置行号
	            item.setRowNo(row+1);
	            //设置公司ID
	            item.setCompany(companyId);
	            //设置上级记分卡
	            String parentId = null;
	            if(StringUtils.isNotBlank(smRowData.get(parentName))){
	                parentId = smRowData.get(parentName);
	            }
	            
	            item.setParent(parentId);
	            //设置编号
	            item.setCode(smRowData.get(codeIdx));
	            //上级编号
	            item.setParentCode(smRowData.get(parentCodeIdx));
	            //设置目标名称
	            item.setName(smRowData.get(nameIdx));
	            
	            //设置所属部门
	            item.setOwnDept(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownDeptIdx), orgIdMap));
	            //设置所属人员
	            item.setOwnEmp(o_dataImportCommBO.findEmpOrgIdByName(smRowData.get(ownEmpIdx),empIdMap));
	            //设置是否启用
	            item.setStatus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isEnableIdx),ynList));
	            //设置是否关注
	            item.setIsFocus(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isFocusIdx),ynList));
	            //是否计算
	            item.setCalc(o_dataImportCommBO.findDictIdByDictName(smRowData.get(isCalcIdx),ynList));
	            //数据类型 
	            item.setDateType(o_dataImportCommBO.findDictIdByDictName(smRowData.get(dataTypeIdx),dataList));
	            //设置评估值公式
	            item.setAssessmentFormula(smRowData.get(resultfIdx));
	            //设置预警公式
	            item.setForecastFormula(smRowData.get(forcastfIdx));
	            //设置描述
	            item.setDesc(smRowData.get(descIdx));
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
                
	            item.setWarningEffDate(warningDate);
	            //设置预警方案
	            item.setForeWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(forcastIdx), alarmAMap));
	            //设置告警方案
	            item.setWarningSet(o_dataImportCommBO.findAlarmPlanbyName(smRowData.get(warningIdx),alarmFMap));
	            //设置图表类型
	            item.setChartType(o_dataImportCommBO.findMutilOptionDictIdByName(smRowData.get(chartIdx),chartList));
	            //设置层级
	            if(NumberUtils.isNumber(smRowData.get(levelIdx))){
	                item.setLevel(Integer.valueOf(smRowData.get(levelIdx)));
	            }
	            
	            //设置序号
	            if(NumberUtils.isNumber(smRowData.get(sortIdx))){
	                item.setSort(Integer.valueOf(smRowData.get(sortIdx)));
	            }
	            
	            
	            //设置目标关联的指标
	            String kpiWeight = findSmRelaKpiByName(smRowData.get(codeIdx),scRelaKpiMap);
	            item.setRelaKpi(kpiWeight);
	            
	            //设置验证信息
	            String validateInfo = this.validateSmData(smRowData,codeList,isCoverage,orgIdMap,empIdMap);
	            if(null!=scRelaKpiMap.get(smRowData.get(codeIdx))){
	                StringBuffer validateSb = scRelaKpiMap.get(smRowData.get(codeIdx)).get("validate");
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
                if(StringUtils.isNotBlank(smRowData.get(isFocusIdx))&&null==item.getIsFocus()){
                    validataInfoBf.append("关注设置错误,");
                }
                
                //校验是否计算
                if(StringUtils.isNotBlank(smRowData.get(isCalcIdx))&&null==item.getCalc()){
                    validataInfoBf.append("计算设置错误,");
                }
                //校验是否启用
                if(StringUtils.isNotBlank(smRowData.get(isEnableIdx))&&null==item.getStatus()){
                    validataInfoBf.append("启用设置错误,");
                }
                
                //校验数据类型
                if(StringUtils.isNotBlank(smRowData.get(dataTypeIdx))&&null==item.getDateType()){
                    validataInfoBf.append("数据类型设置错误,");
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
                
	            item.setValidateInfo(validataInfoBf.toString());
	            
	            tmpList.add(item);
	        }
	        if(tmpList.size()>0){
	            //设置上级目标,设置idseq
	            List<Category> categoryList = new ArrayList<Category>();
	            if(!isCoverage){
	                categoryList = o_categoryBO.findCategoryAllByTask();
	            }
	            findParent(tmpList,categoryList);
	            //设置isleaf
	            findIsLeaf(tmpList);
	            //批量插入数据
	            batchImportSmTmp(tmpList);
	        }
	    }
	    
	    for (CategoryTmp item : tmpList) {
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
		List<CategoryTmp> categoryTmpList = findAllValidateSm(true);
		
		if(isCoverage){//是否覆盖
			//删除目标关联表,以及 目标主表
			String companyId = UserContext.getUser().getCompanyid();
			String idStr = findRemoveScIdsByCompanyId(companyId);
			batchRemoveSmAndSmRelaData(idStr);
		}
		if(null!=categoryTmpList&&categoryTmpList.size()>0){
		  //保存目标基本信息表数据
	        batchSaveTmpToRealStrategy(categoryTmpList);
	        String delSql = "delete from tmp_imp_category";
	        SQLQuery sqlQuery = o_categoryTmpDAO.createSQLQuery(delSql);
	        sqlQuery.executeUpdate();
	        
	        //批量生成记分卡历史数据
	        //batchMergeHistoryData(categoryTmpList);
		}
		
	}
	
	/**批量生成记分卡历史数据
	 * @param  categoryTmpList 记分卡临时数据集合
	 */
	@Transactional
	private void batchMergeHistoryData(List<CategoryTmp> categoryTmpList){
		JSONArray dataJsonArray = new JSONArray();
		String year = DateUtils.getYear(new java.util.Date());
		final List<TimePeriod> timeList = o_timePeriodBO.findTimePeriodByMonthType(year);
		for (CategoryTmp categoryTmp : categoryTmpList) {
            JSONObject smJsObj = new JSONObject();
            smJsObj.put("objectId", categoryTmp.getId());
            smJsObj.put("objectName", categoryTmp.getName());
            smJsObj.put("companyid", categoryTmp.getCompany());
            dataJsonArray.add(smJsObj);
        }
		o_relaAssessResultBO.saveBathResultData(dataJsonArray, timeList, "sc",year);
	}
	
	
	/**
	 * 批量删除目标关联表和目标主表
	 */
	@Transactional
	private void batchRemoveSmAndSmRelaData(final String ids){
		o_categoryTmpDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				//基本信息sql
				String basicSql = "delete from t_com_category ";
				if(StringUtils.isNotBlank(ids)){
					basicSql+=" where id in ("+ids+" ) ";
				}
				//告警sql
				String alarmSql = "delete from t_com_category_rela_alarm ";
				if(StringUtils.isNotBlank(ids)){
					alarmSql+=" where category_id in ( "+ids+" )";
				}
				//关联指标sql
				String relaKpiSql = "delete from  t_kpi_kpi_rela_category ";
				if(StringUtils.isNotBlank(ids)){
					relaKpiSql+=" where category_id in ( "+ids+" )";
				}
				//关联部门人员
				String orgSql = "delete from  t_com_category_rela_org_emp ";
				if(StringUtils.isNotBlank(ids)){
					orgSql+=" where category_id in ( "+ids+" )";
				}
				//历史数据
				String historySql = "delete from t_kpi_sm_assess_result ";
				if(StringUtils.isNotBlank(ids)){
					historySql+=" where object_id in ( "+ids+" )";
				}
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement alarmPst = connection.prepareStatement(alarmSql);
				PreparedStatement relaKpiPst = connection.prepareStatement(relaKpiSql);
				PreparedStatement orgPst = connection.prepareStatement(orgSql);
				PreparedStatement historyPst = connection.prepareStatement(historySql);
				
				
				basicPst.addBatch();
				alarmPst.addBatch();
				relaKpiPst.addBatch();
				orgPst.addBatch();
				historyPst.addBatch();
				
				basicPst.executeBatch();
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
	private void batchSaveTmpToRealStrategy(final List<CategoryTmp> categoryTmpList){
		o_categoryTmpDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				//基本信息sql
				String basicSql = "insert into t_com_category (id,CATEGORY_CODE,CATEGORY_NAME,PARENT_ID,ID_SEQ,ELEVEL,IS_LEAF,DELETE_STATUS,ESORT,FORECAST_FORMULA,IS_ENABLED,EDESC,COMPANY_ID,CHART_TYPE,IS_GENERATE_KPI,ASSESSMENT_FORMULA,DATA_TYPE,is_calc,is_focus)" +
						" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//告警sql
				String alarmSql = "insert into t_com_category_rela_alarm(ID,category_id,FC_ALARM_PLAN_ID,R_ALARM_PLAN_ID,START_DATE) values (?,?,?,?,?)";
				//关联指标sql
				String relaKpiSql = "insert into t_kpi_kpi_rela_category(ID,category_id,KPI_ID,EWEIGHT,is_creator) values (?,?,?,?,?) ";
				//关联部门人员
				String orgSql = "insert into t_com_category_rela_org_emp(id,category_id,ETYPE,ORG_ID,EMP_ID) values(?,?,?,?,?)";
				
				
				PreparedStatement basicPst = connection.prepareStatement(basicSql);
				PreparedStatement alarmPst = connection.prepareStatement(alarmSql);
				PreparedStatement relaKpiPst = connection.prepareStatement(relaKpiSql);
				PreparedStatement orgPst = connection.prepareStatement(orgSql);
				
				for (CategoryTmp item : categoryTmpList) {
					//目标基本信息
					basicPst.setString(1, item.getId());
					basicPst.setString(2, item.getCode());
					basicPst.setString(3, item.getName());
					basicPst.setString(4, item.getParent());
					basicPst.setString(5,item.getIdSeq());
					if(null!=item.getLevel()){
						basicPst.setInt(6, item.getLevel());
					}else{
						basicPst.setInt(6, 0);
					}
					basicPst.setString(7, item.getIsLeaf());
					basicPst.setBoolean(8, true);
					if(null!=item.getSort()){
						basicPst.setInt(9, item.getSort());
					}else{
						basicPst.setInt(9, 0);
					}
					basicPst.setString(10, item.getForecastFormula());
					basicPst.setString(11, item.getStatus());
					basicPst.setString(12, item.getDesc());
					basicPst.setString(13, item.getCompany());
					basicPst.setString(14, item.getChartType());
					basicPst.setString(15, Contents.DICT_N);
					basicPst.setString(16, item.getAssessmentFormula());
					basicPst.setString(17, item.getDateType());
					basicPst.setString(18, item.getCalc());
					basicPst.setString(19, item.getIsFocus());
					basicPst.addBatch();
					
					//告警方案
					String warningSet = item.getWarningSet();
					//预警方案
					String forcastSet = item.getForeWarningSet();
					java.util.Date warningDate = item.getWarningEffDate();
					if(StringUtils.isNotBlank(warningSet)||StringUtils.isNotBlank(forcastSet)){
						alarmPst.setString(1, Identities.uuid());
						alarmPst.setString(2, item.getId());
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
					String relaKpis = item.getRelaKpi();
					if(StringUtils.isNotBlank(relaKpis)){
						String[] relaKpiArray = StringUtils.split(relaKpis,";");
						for (String relaKpi : relaKpiArray) {
							if(StringUtils.isNotBlank(relaKpi)){
								String[] weights = StringUtils.split(relaKpi,",");
								relaKpiPst.setString(1, Identities.uuid());
								relaKpiPst.setString(2, item.getId());
								relaKpiPst.setString(3, weights[0]);
								if(weights.length>1){
									if(StringUtils.isNotBlank(weights[1])){
										relaKpiPst.setFloat(4, NumberUtils.toFloat(weights[1]));
									}else{
										relaKpiPst.setFloat(4, 0);
									}
								}
								relaKpiPst.setString(5, "1");
								relaKpiPst.addBatch();
							}
						}
					}
					
					//所属部门和人员
					String ownDept = item.getOwnDept();
					String ownEmp = item.getOwnEmp();
					orgPst.setString(1, Identities.uuid());
					orgPst.setString(2, item.getId());
					orgPst.setString(3, Contents.BELONGDEPARTMENT);
					orgPst.setString(4, ownDept);
					orgPst.setString(5, ownEmp);
					orgPst.addBatch();
				}
				basicPst.executeBatch();
				alarmPst.executeBatch();
				relaKpiPst.executeBatch();
				orgPst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
	
	/**批量插入目标临时表
	 * @param categoryTmpList 目标临时对象集合
	 */
	@Transactional
	private void batchImportSmTmp(final List<CategoryTmp> categoryTmpList){
		o_categoryTmpDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				PreparedStatement pst = null;
                String sql = "insert into tmp_imp_category (id,company_id,PARENT_ID,CATEGORY_NAME,CATEGORY_CODE,ID_SEQ,ELEVEL,IS_LEAF,ESORT," +
                		"OWNER_DEPT,WARNING_EFF_DATE,WARNING_SET,FORE_WARNING_SET,FORECAST_FORMULA,ASSESSMENT_FORMULA,EDESC,CHART_TYPE,DATA_TYPE,is_calc,is_focus,parent_code," +
                		"VALIDATE_INFO,rela_kpi,row_no,OWNER_EMP,is_enabled) " +
                		"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                
                pst = connection.prepareStatement(sql);
                for (CategoryTmp item : categoryTmpList) {
                	pst.setString(1, item.getId());
                	pst.setString(2, item.getCompany());
                	pst.setString(3, item.getParent());
                	pst.setString(4, item.getName());
                	pst.setString(5, item.getCode());
                	pst.setString(6, item.getIdSeq());
                	if(null!=item.getLevel()){
                		pst.setInt(7, item.getLevel());
                	}else{
                		pst.setInt(7, 0);
                	}
                	pst.setString(8, item.getIsLeaf());
                	if(null!=item.getSort()){
                		pst.setInt(9, item.getSort());
                	}else{
                		pst.setInt(9, 0);
                	}
                	pst.setString(10, item.getOwnDept());
                	if(null!=item.getWarningEffDate()){
                	    pst.setDate(11, new Date(item.getWarningEffDate().getTime()));
                	}else{
                	    pst.setDate(11, null);
                	}
                	pst.setString(12, item.getWarningSet());
                	pst.setString(13, item.getForeWarningSet());
                	pst.setString(14, item.getForecastFormula());
                	pst.setString(15, item.getAssessmentFormula());
                	pst.setString(16, item.getDesc());
                	pst.setString(17, item.getChartType());
                	pst.setString(18, item.getDateType());
                	pst.setString(19, item.getCalc());
                	pst.setString(20, item.getIsFocus());
                	pst.setString(21, item.getParentCode());
                	pst.setString(22, item.getValidateInfo());
                	pst.setString(23, item.getRelaKpi());
                	pst.setInt(24, item.getRowNo());
                	pst.setString(25, item.getOwnEmp());
                	pst.setString(26, item.getStatus());
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
	private String validateSmData(List<String> smRowData,List<String> codeList,Boolean isCoverage
			,Map<String,String> orgIdMap,Map<String,String> empIdMap){
		StringBuffer validateSb = new StringBuffer();
		int codeIdx = 2; //编号所在列
		int ownDeptIdx = 5;//所属部门
		int ownEmpIdx = 6;//所属人员
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
		//所属部门人员是否匹配
		if(null!=ownEmp&&null!=ownDept){
			if(!o_empOrgBO.isEmpOrgBySome(ownEmp, ownDept, companyId)){
				validateSb.append("所属部门人员不匹配,");
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
