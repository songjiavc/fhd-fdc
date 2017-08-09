package com.fhd.risk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.fhd.dao.fdc.NavigationBarsDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.ra.business.risk.PotentialRiskEventBO;
import com.fhd.ra.business.risk.RiskOutsideBO;
import com.fhd.sys.business.dic.DictEntryRelationBO;

/**
* 用于service的单元测试
* @author   郑军祥
* @since    fhd Ver 4.5
* @Date	 2013-6-1
* AbstractTransactionalJUnit4SpringContextTests
* SpringContextTestCase
* @see 	 
*/

@ContextConfiguration(locations = {"/spring/spring-config-business.xml","/spring/spring-config-jbpm.xml","/spring/spring-config-quartz.xml","/spring/spring-config-email.xml"})
public class PotentialRiskEventBOTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private NavigationBarsDAO o_commonDAO;
	
	@Autowired    
    private PotentialRiskEventBO service;
	
	@Autowired    
    private RiskOutsideBO riskService;
	
	@Autowired 
	private DictEntryRelationBO dr;
	
	@Autowired 
	private ProcessBO o_processBO;
	
	@Test
	public void getOrgTreeRecordByEventIdsTest(){
		String ids = "02a02f5f-9d23-4d70-9cd8-256f169ad569,1804d68c-b14c-470b-83fe-f9a550d24273,34a73cdb-45cc-4ce1-a21a-885bae354d68,4c1fed3d-71fa-4746-be77-d4d99fb0b166,8b636bf1-513b-4697-8e0e-33934dcb0c19,a55247be-d747-4eed-9624-066a5cda9c79,fca6018e-65e8-4c61-bc04-e56690eee299,XD00SC010101,XD00SC010301,XD00SC010302,XD00SC020101,XD00SC020201,XD00SC030101,XD00SC030201,XD00SC030202,XD00SC030203,XD00SC040101,XD00SC040102,XD00SC040201,XD00SC040202,XD00SC040203,XD00SC040301,XD00SC040302,XD00SC040303,XD00SC040304,XD00SC040305,XD00SC040401,XD00SC040402,XD00SC040403,XD00SC040404,XD00SC040405,XD00SC040406,XD00SC040501,XD00SC040502,XD00SC040503,XD00SC050101,XD00SC050102,XD00SC050103,XD00SC060101,XD00SC060102,XD00SC060103,XD00SC070101,XD00SC070102,XD00SC070201,";
		String[] eventIds = ids.split(",");
		List<Map<String,Object>> list = service.getOrgTreeRecordByEventIds("10000", null, eventIds,null);
		System.out.println("*****************************");
		System.out.println(list.toString());
	}
	@Test
	public void getRiskTreeRecordByEventIdsTest() {
		String node = "root";
		String query = null;
		String ids = "[{\"id\":\"2e61bc88-c345-4dc2-900d-7b9c20a81299\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"333aa017-287c-43f5-b84c-5edf3ee9e5f6\",\"type\":\"re\",\"icon\":\"icon-ibm-symbol-5-sm\"},{\"id\":\"7b927266-1984-45b6-8cb0-d347ee0c1509\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"88e1ab25-940f-44fe-a192-b4c45ecb90fb\",\"type\":\"re\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW\",\"type\":\"RBS\",\"icon\":\"icon-ibm-symbol-5-sm\"},{\"id\":\"CW01\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW02\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW03\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW04\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW05\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW06\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW07\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW08\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW09\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW10\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"},{\"id\":\"CW11\",\"type\":\"rbs\",\"icon\":\"icon-ibm-symbol-6-sm\"}]";
		JSONArray array = JSONArray.fromObject(ids);
    	Map riskMap = new HashMap();
    	String[] eventIds = new String[array.size()];	//风险事件ID数组
    	for(int i=0;i<array.size();i++){
    		JSONObject obj = (JSONObject)array.get(i);
    		String id = obj.getString("id");
    		eventIds[i] = id;
    		riskMap.put(id, obj.getString("icon"));
    	}
		List<Map<String,Object>> map = service.getRiskTreeRecordByEventIds(node, query, eventIds, riskMap);
		System.out.println(map.toString());
	}
	
	@Test
	public void testQueryStrategyMapObjectBySearchName(){	//单元测试不好使  加riskEventId
		String node = "root";
		String query = null;
		String idStr = "\"88e1ab25-940f-44fe-a192-b4c45ecb90fb\",\"333aa017-287c-43f5-b84c-5edf3ee9e5f6\"";
		String[] eventIds = idStr.split(",");
		List<Map<String,Object>> map = service.getStrategyMapTreeRecordByEventIds(node, query, eventIds,null,null);
		System.out.println(map.toString()+"*&*********");
	}
	
	@Test
	public void testGetProcessTreeRecordByEventIds(){//单元测试不好使
		String node = "root";
		String query = null;
		String idStr = "\"88e1ab25-940f-44fe-a192-b4c45ecb90fb\",\"333aa017-287c-43f5-b84c-5edf3ee9e5f6\"";
		String[] eventIds = idStr.split(",");
		List<Map<String,Object>> map = service.getProcessTreeRecordByEventIds(node, query, eventIds,null);
		System.out.println(map.toString());
	}
	
	@Test
	public void testRisk() {
		Risk risk = new Risk();
		risk.setId("zjxtest24");
		risk.setName("zjxtest24");
		List<String> respOrgIds = new ArrayList<String>();
		respOrgIds.add("10000");
		respOrgIds.add("10001");
		List<String> relaOrgIds = new ArrayList<String>();
		relaOrgIds.add("10002");
		relaOrgIds.add("10003");
		List<String> influenceKpiIds = new ArrayList<String>();
		influenceKpiIds.add("027c6a8b-b764-4ab7-87bf-6f289c3d89db");
		influenceKpiIds.add("027e7318-5935-48e1-a269-4bfd18ee0b5e");
		List<String> influeceProcessIds = new ArrayList<String>();
		influeceProcessIds.add("1001");
		influeceProcessIds.add("1002");StringBuffer sql = new StringBuffer();
		sql.append("select new map(prt.risk.id as riskId,count(prt.id) as num) ");
		sql.append("from ProcessRelaRisk prt ");
		sql.append("left join prt.risk risk ");
		sql.append("left join risk.measureRelaRisk rmt ");
		sql.append("where prt.process.id=:processId ");
		sql.append("group by risk.id");
		sql.append("select new map(prt.risk.id as riskId,count(prt.id) as num) ");
		sql.append("from ProcessRelaRisk prt ");
		sql.append("left join prt.risk risk ");
		sql.append("left join risk.measureRelaRisk rmt ");
		sql.append("where prt.process.id=:processId ");
		sql.append("group by risk.id");
		Boolean success = riskService.saveRisk(risk, respOrgIds, relaOrgIds, influenceKpiIds, influeceProcessIds);
		System.out.println(success);
	}
	
	@Test
	public void testHqlQueryMap() {

		StringBuffer sql = new StringBuffer();
		sql.append("select new map(prt.risk.id as riskId,count(prt.id) as num) ");
		sql.append("from ProcessRelaRisk prt ");
		sql.append("left join prt.risk risk ");
		sql.append("left join risk.measureRelaRisk rmt ");
		sql.append("where prt.process.id=:processId ");
		sql.append("group by risk.id");
		
		//String hql = "select new map(u.username as n,u.password as p) from SysUser u";
		Query query = o_commonDAO.getSession().createQuery(sql.toString());//sql.toString()
		query.setParameter("processId", "03fb06d1-c2c1-44e5-a379-bbf574cf9696");
		List<Map<String,Object>> list = query.list();
		System.out.println(list.toString());
	}
	
	@Test
	public void test(){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from t_sys_organization ");
		sb.append("where org_level = :orgId");
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("orgId", "4 or 1=1");
		int size = o_commonDAO.createSQLQuery(sb.toString(),paramsMap).list().size();
		System.out.println("ok"+size);
	}
}