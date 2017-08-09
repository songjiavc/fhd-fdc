package com.fhd.ra.web.controller.risk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.util.ExcelUtil;
import com.fhd.ra.business.risk.RiskHistoryVersionBO;
import com.fhd.ra.web.form.risk.RiskHistoryVersionForm;
import com.fhd.sys.business.organization.OrgGridBO;
import com.fhd.core.utils.Identities;
import com.fhd.entity.risk.RiskVersion;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Controller
public class RiskHistoryVersionControl {
	
	@Autowired
	private RiskHistoryVersionBO o_riskHistoryVersionBO;
	@Autowired
    private OrgGridBO o_orgGridBO;

	/**
	 * 创建风险版本
	 * @param dimensionId
	 * @param response
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value="/riskhistoryversion/createRiskVersion")
	public Map<String, Object> createRiskVersion(String companyId,String versionName,String schm) throws IOException{
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<RiskVersion> nameList = o_riskHistoryVersionBO.findVersionByName(versionName);
		if(nameList.size() != 0){
			map.put("success", false);
			return map;
		}
		
		RiskVersion riskVersion = new RiskVersion();
		riskVersion.setId(Identities.uuid());
		riskVersion.setName(versionName);
		riskVersion.setCreateTime(new Date());
		String createBy = UserContext.getUser().getEmpid();
		if(StringUtils.isEmpty(companyId)){
			companyId = UserContext.getUser().getCompanyid();
		}
		String createOrg = "";
		if("dept".equals(schm)){
			SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
			String seq = org.getOrgseq();
			String deptId = seq.split("\\.")[2];//部门编号
			
			createOrg = deptId;
		}
		riskVersion.setCreateBy(createBy);
		riskVersion.setCompanyId(companyId);
		riskVersion.setCreateOrg(createOrg);
		riskVersion.setSchm(schm);
		o_riskHistoryVersionBO.saveVersion(riskVersion, companyId, createBy);
		map.put("success", true);
		
		return map;
	}
	
	/**
	 * 查询风险库，包括当前风险库和历史版本风险库
	 * @param start
	 * @param limit
	 * @param query
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/riskhistoryversion/findCurrentRiskStorage.f")
	public Map<String, Object> findCurrentRiskStorage(int start,int limit,String query,String companyId,String version,String level,String orgIds,String schm) throws Exception {
		//当前版本
		if("0".equals(version) || "当前版本".equals(version)){
			version = null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		//没有传入公司id，默认取当前用户公司id
		if(StringUtils.isEmpty(companyId)){
	        companyId = UserContext.getUser().getCompanyid();
		}
        List<RiskHistoryVersionForm> list = o_riskHistoryVersionBO.findRiskHistoryVersionBySome(companyId, version, level, orgIds, query,schm);
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        int count = start+limit;
        if(count>list.size()){	//最后一页记录不够分页条数
        	count = list.size();
        }
        for(int i=start;i<count;i++){
        	RiskHistoryVersionForm form = list.get(i);
        	Map<String,Object> m = new HashMap<String,Object>();
        	m.put("num", (i+1));
        	m.put("parentName", form.getParentName());
        	m.put("name", form.getName());
        	m.put("riskId", form.getRiskId());
        	m.put("adjustHistoryId", form.getAdjustHistoryId());
        	m.put("templateId", form.getTemplateId());
        	m.put("dutyDepartment", form.getDutyDepartment());
        	m.put("relativeDepartment", form.getRelativeDepartment());
        	m.put("probability", form.getProbability()==null?"":form.getProbability());
        	m.put("influenceDegree", form.getInfluenceDegree()==null?"":form.getInfluenceDegree());
        	m.put("riskScore", form.getRiskScore()==null?"":form.getRiskScore());
        	m.put("riskStatus", form.getRiskStatus());
        	m.put("adjustType", form.getAdjustType());
        	datas.add(m);
        }
		map.put("totalCount", list.size());
		map.put("datas", datas);
		return map;
	}
	

	/**operateitem.dataIndex != ''
	 * 历史版本导出
	 */
	@ResponseBody
	@RequestMapping(value = "/riskhistoryversion/findCurrentRiskStorageExport.f")
	public void findCurrentRiskStorageExport(String headerData,String query,String companyId,String version,String level,String orgIds
											,HttpServletRequest request, HttpServletResponse response) throws Exception {
		//当前版本
		if("0".equals(version)){
			version = null;
		}
		
		/**
         * 格式
         */
		JSONArray headerArray=JSONArray.fromObject(headerData);
        int j = headerArray.size();
        String[] fieldTitle = new String[j];
        for(int i=0;i<j;i++){
            JSONObject jsonObj = headerArray.getJSONObject(i);
            fieldTitle[i] = jsonObj.get("text").toString();
        }
        
        /**
         * 数据
         */
        List<Object[]> datas = new ArrayList<Object[]>();
		//没有传入公司id，默认取当前用户公司id
		if(StringUtils.isEmpty(companyId)){
	        companyId = UserContext.getUser().getCompanyid();
		}
        List<RiskHistoryVersionForm> list = o_riskHistoryVersionBO.findRiskHistoryVersionBySome(companyId, version, level, orgIds, query,null);//schm值为null
        for(int i=0;i<list.size();i++){
        	RiskHistoryVersionForm form = list.get(i);
        	Object[] object = new Object[j];
            object[0] = String.valueOf(i+1);
            object[1] = form.getName();
            object[2] = form.getParentName();
            object[3] = form.getDutyDepartment();
            object[4] = form.getRelativeDepartment();
            object[5] = form.getProbability()==null?"":form.getProbability();
            object[6] = form.getInfluenceDegree()==null?"":form.getInfluenceDegree();
            object[7] = form.getRiskScore()==null?"":form.getRiskScore().toString();
            
            if(form.getRiskStatus().equalsIgnoreCase("icon-ibm-symbol-6-sm")){
            	object[8] = "低";
            }else if(form.getRiskStatus().equalsIgnoreCase("icon-ibm-symbol-5-sm")){
            	object[8] = "中";
            }else{
            	object[8] = "高";
            }
            
        	datas.add(object);
        }
        
		String exportFileName = "风险版本.xls";
		String sheetName = "风险版本";
        //数据，列名称，文件名称，sheet名称，sheet位置
        ExcelUtil.write(datas, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/riskhistoryversion/findAllVersion.f")
	public Map<String, Object> findAllVersiont(String companyId,String schm) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		//没有传入公司id，默认取当前用户公司id
		if(StringUtils.isEmpty(companyId)){
	        companyId = UserContext.getUser().getCompanyid();
		}
		List<RiskVersion> list = o_riskHistoryVersionBO.findVersionByAll(companyId,schm);
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		//添加当前版本数据
		Map<String,Object> current = new HashMap<String,Object>();
		current.put("id", 0);
		current.put("name", "当前版本");
		datas.add(current);
		for(RiskVersion rv : list){
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("id", rv.getId());
			m.put("name", rv.getName());
			datas.add(m);
		}
		map.put("totalCount", datas.size()+1);
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 获取部门的十大风险，仅供部门文件夹，十大风险tab页签使用
	 * @param start
	 * @param limit
	 * @param query
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/riskhistoryversion/findOrgTop10Risk.f")
	public Map<String, Object> findOrgTop10Risk(int start,int limit,String query,String level) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String companyId = UserContext.getUser().getCompanyid();
		String version = "";//最新版本
		if(StringUtils.isBlank(level)){
			level = "2"; //二级风险
		}
		String orgId = UserContext.getUser().getMajorDeptId();
		if(StringUtils.isBlank(query)){
			query = "";
		}
        List<RiskHistoryVersionForm> list = o_riskHistoryVersionBO.findRiskHistoryVersionBySome(companyId, version, level, orgId, query,null);//schm值为null
        //自动分页
        int count = start+limit;
        if(count>list.size()){	//最后一页记录不够分页条数
        	count = list.size();
        }
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        for(int i=start;i<count;i++){
        	RiskHistoryVersionForm form = list.get(i);
        	Map<String,Object> m = new HashMap<String,Object>();
        	m.put("num", (i+1));
        	m.put("parentName", form.getParentName());
        	m.put("parentId", form.getParentId());
        	m.put("name", form.getName());
        	m.put("riskId", form.getRiskId());
        	m.put("adjustHistoryId", form.getAdjustHistoryId());
        	m.put("templateId", form.getTemplateId());
        	m.put("dutyDepartment", form.getDutyDepartment());
        	m.put("relativeDepartment", form.getRelativeDepartment());
        	m.put("probability", form.getProbability()==null?"":form.getProbability());
        	m.put("influenceDegree", form.getInfluenceDegree()==null?"":form.getInfluenceDegree());
        	m.put("riskScore", form.getRiskScore()==null?"":form.getRiskScore());
        	m.put("riskStatus", form.getRiskStatus());
        	datas.add(m);
        }
		map.put("totalCount", list.size());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 风险排序导出
	 */
	@ResponseBody
	@RequestMapping(value = "/riskhistoryversion/findOrgTop10RiskExport.f")
	public void findOrgTop10RiskExport(String headerData,String query,String level
											,HttpServletRequest request, HttpServletResponse response) throws Exception {
		/**
         * 格式
         */
		JSONArray headerArray=JSONArray.fromObject(headerData);
        int j = headerArray.size();
        String[] fieldTitle = new String[j];
        for(int i=0;i<j;i++){
            JSONObject jsonObj = headerArray.getJSONObject(i);
            fieldTitle[i] = jsonObj.get("text").toString();
        }
        
        /**
         * 数据
         */
        String companyId = UserContext.getUser().getCompanyid();
		String version = "";//最新版本
		if(StringUtils.isBlank(level)){
			level = "2"; //二级风险
		}
		String orgId = UserContext.getUser().getMajorDeptId();
		if(StringUtils.isBlank(query)){
			query = "";
		}
        List<Object[]> datas = new ArrayList<Object[]>();
        List<RiskHistoryVersionForm> list = o_riskHistoryVersionBO.findRiskHistoryVersionBySome(companyId, version, level, orgId, query,null);//schm值为null
        
        /**
         * 组装数据
         */
        for(int i=0;i<list.size();i++){
        	RiskHistoryVersionForm form = list.get(i);
        	Object[] object = new Object[j];
            object[0] = String.valueOf(i+1);
            object[1] = form.getName();
            object[2] = form.getParentName();
            object[3] = form.getDutyDepartment();
            object[4] = form.getRelativeDepartment();
            object[5] = form.getProbability()==null?"":form.getProbability();
            object[6] = form.getInfluenceDegree()==null?"":form.getInfluenceDegree();
            object[7] = form.getRiskScore()==null?"":form.getRiskScore().toString();
            Object riskStatus = form.getRiskStatus();
            if("icon-ibm-symbol-4-sm".equals(riskStatus)){
            	object[8] = "color|red";
            }else if("icon-ibm-symbol-6-sm".equals(riskStatus)){
            	object[8] = "color|green";
            }else if("icon-ibm-symbol-5-sm".equals(riskStatus)){
            	object[8] = "color|yellow";
            }else if("icon-ibm-symbol-0-sm".equals(riskStatus)){
            	object[8] = "color|";
            }else{
            	object[8] = riskStatus;
            }
        	datas.add(object);
        }
        
        /**
         * 输出
         */
		String exportFileName = "风险排序.xls";
		String sheetName = "风险排序";
        //数据，列名称，文件名称，sheet名称，sheet位置
        ExcelUtil.write(datas, fieldTitle, exportFileName, sheetName, 0, request, response);
	}
	
	/**
	 * 获取风险分类的所有层次
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/riskhistoryversion/findAllRiskCatalog.f")
	public Map<String, Object> findAllRiskCatalog() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		//获取风险的层次
		String companyId = UserContext.getUser().getCompanyid();
		int deep = o_riskHistoryVersionBO.getRiskCatalogDeep(companyId);
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		//添加当前版本数据
		Map<String,Object> all = new HashMap<String,Object>();
		all.put("id", 0);
		all.put("name", "全部");
		datas.add(all);
		for(int i=1;i<=deep;i++){
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("id", i);
			String level = "";
			switch(i){	//8个足够
				case 1:level = "一";break;
				case 2:level = "二";break;
				case 3:level = "三";break;
				case 4:level = "四";break;
				case 5:level = "五";break;
				case 6:level = "六";break;
				case 7:level = "七";break;
				case 8:level = "八";break;
			}
			m.put("name", level+"级风险");
			datas.add(m);
		}
		Map<String,Object> event = new HashMap<String,Object>();
		event.put("id", -1);
		event.put("name", "风险事件");
		datas.add(event);
		map.put("totalCount", datas.size()+2);
		map.put("datas", datas);
		return map;
	}
}

