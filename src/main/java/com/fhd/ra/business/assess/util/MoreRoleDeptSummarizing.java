package com.fhd.ra.business.assess.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.fhd.fdc.utils.NumberUtil;
import com.fhd.ra.business.assess.approval.ApplyContainer;

public class MoreRoleDeptSummarizing {
	
	/**
	 * 计算同一风险、根据角色、部门
	 * @param types 计算方式 部门/角色
	 * @param riskId 风险ID
	 * @param tempId 模板ID
	 * @param deptRisksList 参与人列表
	 * @param applyContainer 组织数据初始化实体
	 * @return HashMap<String, Double>
	 * @author 金鹏祥
	 * */
	public static HashMap<String, Double> getDynamicDimValueMap(
			String types,
			String riskId,
			String tempId,
			ArrayList<String> deptRisksList,
			ApplyContainer applyContainer){
		
		HashMap<String, Double> weightSetAllMap = applyContainer.getWeightSetAllMap();
		HashMap<String, ArrayList<String>> userRoleAllMap = applyContainer.getUserRoleAllMap();
		HashMap<String, String> riskOrgAllMap = applyContainer.getRiskOrgAllMap();
		ArrayList<HashMap<String, Object>> mapsList = applyContainer.getMapsList();
		ArrayList<String> dimIdArrayList = applyContainer.getDimIdArrayList();
		HashMap<String, Double> riskAssessDynamicRoleLeaderMap = applyContainer.getRiskAssessDynamicRoleLeaderMap();
    	List<String> dynamicRoleList = applyContainer.getDynamicRoleList();
    	ArrayList<String> rangObjectDeptEmpEmpIdList = applyContainer.getRangObjectDeptEmpEmpIdList();
		
    	int sunPersonDeptZR = 0;//责任部门人数和
    	int sunPersonXG = 0;//相关人数和
    	int sunPersonCY = 0;//参与人数和
    	double ZR = 0l;//责任部门权重
    	double XG = 0l;//相关部门权重
    	double CY = 0l;//参与部门权重
    	double sunRole = 0;//动态角色权重和
    	
    	HashMap<String, HashMap<String, ArrayList<Double>>> riskAssessDynamicRolesMaps = null;//风险评估动态角色汇总维度及分值集合
    	ArrayList<HashMap<String, Double>> riskAssessDynamicRolesMapList = null;
    	HashMap<String, Integer> riskAssessDynamicRolesunPersonMap = null;//风险评估动态角色人数总合
    	
    	HashMap<String, ArrayList<Double>> riskAssessXGMap = null;//相关部门汇总维度及分值
    	HashMap<String, ArrayList<Double>> riskAssessZRMap = null;//责任部门汇总维度及分值
    	HashMap<String, ArrayList<Double>> riskAssessCYMap = null;//责任部门汇总维度及分值
    	HashMap<String, Double> roleLeaderMap = null;
    	
    	if("role".equalsIgnoreCase(types)){
    		riskAssessDynamicRolesMaps = new HashMap<String, HashMap<String, ArrayList<Double>>>();
        	riskAssessDynamicRolesunPersonMap = new HashMap<String, Integer>();
        	riskAssessDynamicRolesMapList = new ArrayList<HashMap<String, Double>>();
        	roleLeaderMap = new HashMap<String, Double>();
        	for (String empId : rangObjectDeptEmpEmpIdList) {
        		ArrayList<String> rileArrayList = userRoleAllMap.get(empId);
        		for (String roleId : rileArrayList) {
        			if(riskAssessDynamicRoleLeaderMap.get(roleId) != null){
        				if(null == roleLeaderMap.get(roleId)){
        					roleLeaderMap.put(roleId, riskAssessDynamicRoleLeaderMap.get(roleId));
        				}
        			}
        		}
			}
        	
			//得到同一用户如果有两种角色或以上,得到最大权重的角色,如果角色权重相等得到其中一项角色及权重
	    	for (String rangObjectDeptEmpIds : deptRisksList) {
	    		String strs[] = rangObjectDeptEmpIds.split("--");
	    		//String rangObjectDeptEmpId = strs[0];
	    		String empIdAndDeptId = strs[1];
	    		ArrayList<Double> assessRoleArrayList = new ArrayList<Double>();
	    		HashMap<Double, String> tempMap = new HashMap<Double, String>();
	    		ArrayList<String> tempArrayList = new ArrayList<String>();
	    		
	    		double maxValue = 0L;
	    		
	    		for (String rile : dynamicRoleList) {
	    			if(userRoleAllMap.get(empIdAndDeptId).contains(rile)){
	    				tempMap.put(riskAssessDynamicRoleLeaderMap.get(rile), rile);
	    				assessRoleArrayList.add(riskAssessDynamicRoleLeaderMap.get(rile));
	    			}
	    		}
	    		
	    		maxValue = CalculateUtil.getMaxValue(assessRoleArrayList);
	    		tempArrayList.add(tempMap.get(maxValue));
	    		userRoleAllMap.put(empIdAndDeptId, tempArrayList);
	    	}
    	}else if("dept".equalsIgnoreCase(types)){
    		riskAssessXGMap = new HashMap<String, ArrayList<Double>>();
    		riskAssessZRMap = new HashMap<String, ArrayList<Double>>();
    		riskAssessCYMap = new HashMap<String, ArrayList<Double>>();
    	}
    	
    	//换算
    	for (String rangObjectDeptEmpIds : deptRisksList) {
    		String strs[] = rangObjectDeptEmpIds.split("--");
    		String rangObjectDeptEmpId = strs[0];
    		String empIdAndDeptId = strs[1];
    		
			if("role".equalsIgnoreCase(types)){
					for (String rile : dynamicRoleList) {
						if(userRoleAllMap.get(empIdAndDeptId).contains(rile)){
							if(riskAssessDynamicRolesMaps.get(rile) == null){
								HashMap<String, ArrayList<Double>> riskAssessDynamicRoleMap = new HashMap<String, ArrayList<Double>>();
								riskAssessDynamicRoleMap = getDimList(
										riskId, 
										tempId, 
										rangObjectDeptEmpId, 
										applyContainer,
										riskAssessDynamicRoleMap);
								riskAssessDynamicRolesMaps.put(rile, riskAssessDynamicRoleMap);
								if(riskAssessDynamicRolesunPersonMap.get(rile) == null){
									riskAssessDynamicRolesunPersonMap.put(rile, 0);
								}
								int dynamicRolesun = riskAssessDynamicRolesunPersonMap.get(rile) + 1;
								riskAssessDynamicRolesunPersonMap.put(rile, dynamicRolesun);
							}else{
								HashMap<String, ArrayList<Double>> riskAssessDynamicRoleMap = riskAssessDynamicRolesMaps.get(rile);
								riskAssessDynamicRoleMap = getDimList(
										riskId, 
										tempId, 
										rangObjectDeptEmpId, 
										applyContainer,
										riskAssessDynamicRoleMap);
								
								riskAssessDynamicRolesMaps.put(rile, riskAssessDynamicRoleMap);
								if(riskAssessDynamicRolesunPersonMap.get(rile) == null){
									riskAssessDynamicRolesunPersonMap.put(rile, 0);
								}
								int dynamicRolesun = riskAssessDynamicRolesunPersonMap.get(rile) + 1;
								riskAssessDynamicRolesunPersonMap.put(rile, dynamicRolesun);
							}
						}
					}
				}else if("dept".equalsIgnoreCase(types)){
				String type = riskOrgAllMap.get(riskId + "--" + empIdAndDeptId);//责任、相关部门
				if("M".equalsIgnoreCase(type)){
					//M:责任部门
					for (HashMap<String, Object> hasmMap : mapsList) {
						if(hasmMap.get("deptId").toString().equalsIgnoreCase(empIdAndDeptId)){
							if(hasmMap.get("riskId").toString().equalsIgnoreCase(riskId)){
								for (String dimId : dimIdArrayList) {
									if(hasmMap.get(dimId) != null){
										if(riskAssessZRMap.get(dimId) != null){
											riskAssessZRMap.get(dimId).add(Double.parseDouble(hasmMap.get(dimId).toString()));
										}else{
											ArrayList<Double> doubles = new ArrayList<Double>();
											doubles.add(Double.parseDouble(hasmMap.get(dimId).toString()));
											riskAssessZRMap.put(dimId, doubles);
										}
									}
								}
							}
						}
					}
					
					sunPersonDeptZR++;
					ZR = weightSetAllMap.get("dutyDeptWeight");
	        	}else if("A".equalsIgnoreCase(type)){
	        		//A:相关部门
	        		for (HashMap<String, Object> hasmMap : mapsList) {
						if(hasmMap.get("deptId").toString().equalsIgnoreCase(empIdAndDeptId)){
							if(hasmMap.get("riskId").toString().equalsIgnoreCase(riskId)){
								for (String dimId : dimIdArrayList) {
									if(hasmMap.get(dimId) != null){
										if(riskAssessXGMap.get(dimId) != null){
											riskAssessXGMap.get(dimId).add(Double.parseDouble(hasmMap.get(dimId).toString()));
										}else{
											ArrayList<Double> doubles = new ArrayList<Double>();
											doubles.add(Double.parseDouble(hasmMap.get(dimId).toString()));
											riskAssessXGMap.put(dimId, doubles);
										}
									}
								}
							}
							
						}
					}
	        		
					sunPersonXG++;
					XG = weightSetAllMap.get("relatedDeptWeight");
	        	}else if("C".equalsIgnoreCase(type)){
	        		//C:参与部门
	        		for (HashMap<String, Object> hasmMap : mapsList) {
						if(hasmMap.get("deptId").toString().equalsIgnoreCase(empIdAndDeptId)){
							if(hasmMap.get("riskId").toString().equalsIgnoreCase(riskId)){
								for (String dimId : dimIdArrayList) {
									if(hasmMap.get(dimId) != null){
										if(riskAssessCYMap.get(dimId) != null){
											riskAssessCYMap.get(dimId).add(Double.parseDouble(hasmMap.get(dimId).toString()));
										}else{
											ArrayList<Double> doubles = new ArrayList<Double>();
											doubles.add(Double.parseDouble(hasmMap.get(dimId).toString()));
											riskAssessCYMap.put(dimId, doubles);
										}
									}
								}
							}
							
						}
					}
	        		
					sunPersonCY++;
					CY = weightSetAllMap.get("assistDeptWeight");
	        	}
			}
		} 
    	if("role".equalsIgnoreCase(types)){
			Set<String> riskAssessDynamicRolesunPersonMapKey = riskAssessDynamicRolesunPersonMap.keySet();
	        for (Iterator<String> it = riskAssessDynamicRolesunPersonMapKey.iterator(); it.hasNext();) {
	        	Object keys = (String) it.next();
	        	if(roleLeaderMap.get(keys) != null){
	        		sunRole += roleLeaderMap.get(keys);
	        	}
	        }
    		
    		int count = 0;
    		Set<String> key = riskAssessDynamicRolesunPersonMap.keySet();
	        for (Iterator<String> it = key.iterator(); it.hasNext();) {
	        	Object keys = (String) it.next();
	    		int value = riskAssessDynamicRolesunPersonMap.get(keys);
	    		if(value != 0){
	    			count++;
	    		}
	        }
    		
    		if(count > 1){
    			key = riskAssessDynamicRolesMaps.keySet();
    	        for (Iterator<String> it = key.iterator(); it.hasNext();) {
    	        	Object keys = (String) it.next();
    	    		HashMap<String, ArrayList<Double>> riskAssessDynamicRoleMap = riskAssessDynamicRolesMaps.get(keys);
    	    		HashMap<String, Double> map = getListDimValueMap(
    	    				riskAssessDynamicRoleMap, 
    	    				riskAssessDynamicRolesunPersonMap.get(keys), 
        					riskAssessDynamicRoleLeaderMap.get(keys), 
        					sunRole,"");
    	    		riskAssessDynamicRolesMapList.add(map);
    	        }
        		return getDeptDimValueMaps(riskAssessDynamicRolesMapList);
    		}else if(count == 1){
    			key = riskAssessDynamicRolesMaps.keySet();
    	        for (Iterator<String> it = key.iterator(); it.hasNext();) {
    	        	Object keys = (String) it.next();
    	    		HashMap<String, ArrayList<Double>> riskAssessDynamicRoleMap = riskAssessDynamicRolesMaps.get(keys);
    	    		return getListDimValueMap(
    	    				riskAssessDynamicRoleMap, 
    	    				riskAssessDynamicRolesunPersonMap.get(keys), 
        					riskAssessDynamicRoleLeaderMap.get(keys), 
        					sunRole,"one");
    	        }
    		}
		}else if("dept".equalsIgnoreCase(types)){
			if(sunPersonXG != 0 && sunPersonDeptZR != 0 && sunPersonCY != 0){
				//三部门汇总
				sunRole = weightSetAllMap.get("dutyDeptWeight") + weightSetAllMap.get("relatedDeptWeight") + weightSetAllMap.get("assistDeptWeight");
				return getThreeDeptRiskListDimValueMap(
						riskAssessXGMap, 
						sunPersonXG, 
						XG, 
						riskAssessZRMap, 
						sunPersonDeptZR, 
						ZR, 
						riskAssessCYMap, 
						sunPersonCY, 
						CY, 
						sunRole);
			}if(sunPersonXG == 0 && sunPersonDeptZR == 0){
				sunRole = weightSetAllMap.get("assistDeptWeight");
				//参与部门汇总
				return getOneDeptRiskListDimValueMap(
						riskAssessCYMap, 
						sunPersonCY, 
						CY, 
						sunRole);
			}if(sunPersonXG == 0 && sunPersonCY == 0){
				sunRole = weightSetAllMap.get("dutyDeptWeight");
				//责任部门汇总
				return getOneDeptRiskListDimValueMap(
						riskAssessZRMap, 
						sunPersonDeptZR, 
						ZR, 
						sunRole);
			}if(sunPersonDeptZR == 0 && sunPersonCY == 0){
				sunRole = weightSetAllMap.get("relatedDeptWeight");
				//相关部门汇总
				return getOneDeptRiskListDimValueMap(
						riskAssessXGMap, 
						sunPersonXG, 
						XG, 
						sunRole);
			}if(sunPersonXG != 0 && sunPersonDeptZR != 0){
				sunRole = weightSetAllMap.get("dutyDeptWeight") + weightSetAllMap.get("relatedDeptWeight");
				//相关部门汇总,责任部门汇总
				return getTwoDeptRiskListDimValueMap(
						riskAssessXGMap, 
						sunPersonXG, 
						XG, 
						riskAssessZRMap, 
						sunPersonDeptZR, 
						ZR, 
						sunRole);
			}if(sunPersonXG != 0 && sunPersonCY != 0){
				sunRole =weightSetAllMap.get("relatedDeptWeight") + weightSetAllMap.get("assistDeptWeight");
				//相关部门汇总,参与部门汇总
				return getTwoDeptRiskListDimValueMap(
						riskAssessXGMap, 
						sunPersonXG, 
						XG, 
						riskAssessCYMap, 
						sunPersonCY, 
						CY, 
						sunRole);
			}if(sunPersonDeptZR != 0 && sunPersonCY != 0){
				sunRole = weightSetAllMap.get("dutyDeptWeight") + weightSetAllMap.get("assistDeptWeight");
				//责任部门汇总,参与部门汇总
				return getTwoDeptRiskListDimValueMap(
						riskAssessZRMap, 
						sunPersonDeptZR, 
						ZR, 
						riskAssessCYMap, 
						sunPersonCY, 
						CY, 
						sunRole);
			}
		}
    	
    	return null;
	}
	
	/**得三部门动态汇总维度分值集合
	 * @param riskAssessDept1Map 责任/相关/参与部门数据集合
	 * @param dept1SunPerson 责任/相关/参与部门人数总合
	 * @param dept1Weight 责任/相关/参与部门 权重
	 * @param riskAssessDept2Map 责任/相关/参与部门数据集合
	 * @param dept2SunPerson 责任/相关/参与部门人数总合
	 * @param dept2Weight 责任/相关/参与部门 权重
	 * @param riskAssessDept3Map 责任/相关/参与部门数据集合
	 * @param dept3SunPerson 责任/相关/参与部门人数总合
	 * @param dept3Weight 责任/相关/参与部门 权重
	 * @param sunRole 责任/相关/参与部门 权重总合
	 * @return static HashMap<String, Double>
	 * @author 金鹏祥
	 * */
	private static HashMap<String, Double> getThreeDeptRiskListDimValueMap(
			HashMap<String, ArrayList<Double>> riskAssessDept1Map,
			int dept1SunPerson,
			double dept1Weight,
			HashMap<String, ArrayList<Double>> riskAssessDept2Map,
			int dept2SunPerson,
			double dept2Weight,
			HashMap<String, ArrayList<Double>> riskAssessDept3Map,
			int dept3SunPerson,
			double dept3Weight,
			double sunRole){
		HashMap<String, Double> dept1Map = new HashMap<String, Double>();
		HashMap<String, Double> dept2Map = new HashMap<String, Double>();
		HashMap<String, Double> dept3Map = new HashMap<String, Double>();
		
		Set<Entry<String, ArrayList<Double>>> key = riskAssessDept1Map.entrySet();
        for (Iterator<Entry<String, ArrayList<Double>>> it = key.iterator(); it.hasNext();) {
            Entry<String, ArrayList<Double>> keys = it.next();
            ArrayList<Double> list = riskAssessDept1Map.get(keys.getKey());
            double dimValues = 0L;
            double dimValueSun = 0L;
            for (Double dimValue : list) {
            	dimValueSun += dimValue;
			}
            
            dimValues = dimValueSun / dept1SunPerson * dept1Weight / sunRole;
            dept1Map.put(keys.getKey(), dimValues);
        }
        
        Set<Entry<String, ArrayList<Double>>> key2 = riskAssessDept2Map.entrySet();
        for (Iterator<Entry<String, ArrayList<Double>>> it2 = key2.iterator(); it2.hasNext();) {
            Entry<String, ArrayList<Double>> keys = it2.next();
            ArrayList<Double> list = riskAssessDept2Map.get(keys.getKey());
            double dimValues = 0L;
            double dimValueSun = 0L;
            for (Double dimValue : list) {
            	dimValueSun += dimValue;
			}
            
            dimValues = dimValueSun / dept2SunPerson * dept2Weight / sunRole;
            dimValues = dimValues + dept1Map.get(keys.getKey());
            dept2Map.put(keys.getKey(), dimValues);
        }
        
        Set<Entry<String, ArrayList<Double>>> key3 = riskAssessDept3Map.entrySet();
        for (Iterator<Entry<String, ArrayList<Double>>> it3 = key3.iterator(); it3.hasNext();) {
            Entry<String, ArrayList<Double>> keys = it3.next();
            ArrayList<Double> list = riskAssessDept3Map.get(keys.getKey());
            double dimValues = 0L;
            double dimValueSun = 0L;
            for (Double dimValue : list) {
            	dimValueSun += dimValue;
			}
            
            dimValues = dimValueSun / dept3SunPerson * dept3Weight / sunRole;
            dimValues = dimValues + dept2Map.get(keys.getKey());
            dept3Map.put(keys.getKey(), dimValues);
        }
        
        return dept3Map;
	}
	
	/**得二部门动态汇总维度分值集合
	 * @param riskAssessDept1Map 责任/相关/参与部门数据集合
	 * @param dept1SunPerson 责任/相关/参与部门人数总合
	 * @param dept1Weight 责任/相关/参与部门 权重
	 * @param riskAssessDept2Map 责任/相关/参与部门数据集合
	 * @param dept2SunPerson 责任/相关/参与部门人数总合
	 * @param dept2Weight 责任/相关/参与部门 权重
	 * @param sunRole 责任/相关/参与部门 权重总合
	 * @return static HashMap<String, Double>
	 * @author 金鹏祥
	 * */
	private static HashMap<String, Double> getTwoDeptRiskListDimValueMap(
			HashMap<String, ArrayList<Double>> riskAssessDept1Map,
			int dept1SunPerson,
			double dept1Weight,
			HashMap<String, ArrayList<Double>> riskAssessDept2Map,
			int dept2SunPerson,
			double dept2Weight,
			double sunRole){
		HashMap<String, Double> dept1Map = new HashMap<String, Double>();
		HashMap<String, Double> dept2Map = new HashMap<String, Double>();
		
		Set<Entry<String, ArrayList<Double>>> key = riskAssessDept1Map.entrySet();
        for (Iterator<Entry<String, ArrayList<Double>>> it = key.iterator(); it.hasNext();) {
            Entry<String, ArrayList<Double>> keys = it.next();
            ArrayList<Double> list = riskAssessDept1Map.get(keys.getKey());
            double dimValues = 0L;
            double dimValueSun = 0L;
            for (Double dimValue : list) {
            	dimValueSun += dimValue;
			}
            
            dimValues = dimValueSun / dept1SunPerson * dept1Weight / sunRole;
            dept1Map.put(keys.getKey(), dimValues);
        }
        
        Set<Entry<String, ArrayList<Double>>> key2 = riskAssessDept2Map.entrySet();
        for (Iterator<Entry<String, ArrayList<Double>>> it2 = key2.iterator(); it2.hasNext();) {
            Entry<String, ArrayList<Double>> keys = it2.next();
            ArrayList<Double> list = riskAssessDept2Map.get(keys.getKey());
            double dimValues = 0L;
            double dimValueSun = 0L;
            for (Double dimValue : list) {
            	dimValueSun += dimValue;
			}
            
            dimValues = dimValueSun / dept2SunPerson * dept2Weight / sunRole;
            dimValues = dimValues + dept1Map.get(keys.getKey());
            dept2Map.put(keys.getKey(), dimValues);
        }
        
        return dept2Map;
	}
	
	/**得二部门动态汇总维度分值集合
	 * @param riskAssessDept1Map 责任/相关/参与部门数据集合
	 * @param dept1SunPerson 责任/相关/参与部门人数总合
	 * @param dept1Weight 责任/相关/参与部门 权重
	 * @return static HashMap<String, Double>
	 * @author 金鹏祥
	 * */
	private static HashMap<String, Double> getOneDeptRiskListDimValueMap(
			HashMap<String, ArrayList<Double>> riskAssessDept1Map,
			int dept1SunPerson,
			double dept1Weight,
			double sunRole){
		HashMap<String, Double> dept1Map = new HashMap<String, Double>();
		
		Set<Entry<String, ArrayList<Double>>> key = riskAssessDept1Map.entrySet();
        for (Iterator<Entry<String, ArrayList<Double>>> it = key.iterator(); it.hasNext();) {
            Entry<String, ArrayList<Double>> keys = it.next();
            ArrayList<Double> list = riskAssessDept1Map.get(keys.getKey());
            double dimValues = 0L;
            double dimValueSun = 0L;
            for (Double dimValue : list) {
            	dimValueSun += dimValue;
			}
            
            dimValues = dimValueSun / dept1SunPerson * dept1Weight / sunRole;
            dept1Map.put(keys.getKey(), dimValues);
        }
        
        return dept1Map;
	}
	
	/**得到此角色人员打分维度值
	 * @param riskAssessDynamicRoleMap 角色打分数据集合
	 * @param sunPersonDeptLeader 角色人数总合
	 * @param deptLeader 角色权重
	 * @param sunRole 权重总合
	 * @param type 只有一个部门角色人打分 one/""
	 * @return static HashMap<String, Double>
	 * @author 金鹏祥
	 * */
	private static HashMap<String, Double> getListDimValueMap(
			HashMap<String, ArrayList<Double>> riskAssessDynamicRoleMap,
			int sunPersonDeptLeader,
			double deptLeader,
			double sunRole,
			String type){
		HashMap<String, Double> map = new HashMap<String, Double>();
		Set<Entry<String, ArrayList<Double>>> key = riskAssessDynamicRoleMap.entrySet();
        for (Iterator<Entry<String, ArrayList<Double>>> it = key.iterator(); it.hasNext();) {
            Entry<String, ArrayList<Double>> keys = it.next();
            ArrayList<Double> list = riskAssessDynamicRoleMap.get(keys.getKey());
            double dimValues = 0L;
            double dimValueSun = 0L;
            for (Double dimValue : list) {
            	dimValueSun += dimValue;
			}
            
            if("one".equalsIgnoreCase(type)){
            	//该部门下一个角色
            	dimValues = dimValueSun / sunPersonDeptLeader * 1;
            }else{
            	//该部门下多个角色
            	dimValues = dimValueSun / sunPersonDeptLeader * deptLeader / sunRole;
            }
            
            map.put(keys.getKey(), dimValues);
        }
        
        return map;
	}
	
	/**同部门打分维度及分值集合相加得维度值
	 * @param riskAssessEmployeeMapList 角色打分集合
	 * @return static HashMap<String, Double>
	 * @author 金鹏祥
	 * */
	private static HashMap<String, Double> getDeptDimValueMaps(
			ArrayList<HashMap<String, Double>> riskAssessEmployeeMapList){
		HashMap<String, Double> doubleMaps = new HashMap<String, Double>();
		for (HashMap<String, Double> hashMap : riskAssessEmployeeMapList) {
			Set<Entry<String, Double>> key = hashMap.entrySet();
	        for (Iterator<Entry<String, Double>> it = key.iterator(); it.hasNext();) {
	            Entry<String, Double> keys = it.next();
	            Double doubles = hashMap.get(keys.getKey());
	            
	            if(doubleMaps.get(keys.getKey()) != null){
	            	doubles += doubleMaps.get(keys.getKey());
	            	doubleMaps.put(keys.getKey(), doubles);
	            }else{
	            	doubleMaps.put(keys.getKey(), doubles);
	            }
	        }
		}
        
        return doubleMaps;
	}
	
	/**
	 * 得人、部门打的维度及分值,并追加到MAP中
	 * @param riskId 风险ID
	 * @param tempId 模板ID
	 * @param rangObjectDeptEmpId 综合打分ID
	 * @param applyContainer 组织数据初始化实体
	 * @param map MAP数据信息键值返回
	 * @return static HashMap<String, ArrayList<Double>>
	 * @author 金鹏祥
	 * */
	private static HashMap<String, ArrayList<Double>> getDimList(
			String riskId,
			String tempId,
			String rangObjectDeptEmpId,
			ApplyContainer applyContainer,
			HashMap<String, ArrayList<Double>> map){
		ArrayList<Double> dimValueList = null;
		HashMap<String, String> mapss = new HashMap<String, String>();
		ArrayList<String> dimList = DynamicDim.getDimValue(
    			riskId, 
    			tempId, 
    			rangObjectDeptEmpId, 
    			applyContainer);
		
		for (String string : dimList) {
			mapss.put(string, string);
		}
		
		dimList = new ArrayList<String>();
		for (String key : mapss.keySet()) {
			   dimList.add(key);
		}
		
		if(dimList != null){
			for (String obj : dimList) {
				String dimId = obj.split("--")[0].toString();
				Double dimValue = NumberUtil.meg(Double.parseDouble(obj.split("--")[1].toString()), 2);
				
				if(map.get(dimId) != null){
					map.get(dimId).add(dimValue);
				}else{
					dimValueList = new ArrayList<Double>();
					dimValueList.add(dimValue);
					map.put(dimId, dimValueList);
				}
			}
		}
		
		return map;
	}
}