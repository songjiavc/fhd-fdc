package com.fhd.check.business.quarterlycheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.check.quarterlycheck.QuarterlyCheckPlanDAO;
import com.fhd.entity.check.quarterlycheck.QuarterlyCheck;
import com.fhd.entity.check.yearcheck.plan.YearCheckPlan;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.organization.EmpGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class QuarterlyCheckPlanBO {
	@Autowired
	private QuarterlyCheckPlanDAO quarterCheckPlanDAO;
	@Autowired
	private EmpGridBO o_empGridBO;
	public Page<QuarterlyCheck> findAllQuarterlyCheckPlan(String query, Page<QuarterlyCheck> page, String status) {
		DetachedCriteria dc = DetachedCriteria.forClass(QuarterlyCheck.class);

		if(StringUtils.isNotBlank(status)){//处理状态
		dc.add(Restrictions.eq("dealStatus", status));
		}
		if(StringUtils.isNotBlank(query)){
		dc.add(Restrictions.like("planName", query, MatchMode.ANYWHERE));
		}
		
		return quarterCheckPlanDAO.findPage(dc, page, false);
	}

	public Map<String, Object> findQuarterlyCheckPlanById(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		QuarterlyCheck plan = quarterCheckPlanDAO.get(id);
		if(null != plan){
			inmap.put("id", id);
			inmap.put("name", plan.getName());
			inmap.put("planCode", plan.getPlanCode());
			inmap.put("rangeRequire", plan.getRangeRequire());
			//联系人
			JSONArray conarray = new JSONArray();
			JSONObject conjson = new JSONObject();
			if(null != plan.getContactPerson()){
				conjson.put("id", plan.getContactPerson().getId());
				conjson.put("empno", plan.getContactPerson().getEmpcode());
				conjson.put("empname", plan.getContactPerson().getEmpname());
				conarray.add(conjson);
				inmap.put("contactName",conarray.toString());
				inmap.put("cName",plan.getContactPerson().getEmpname());
			}else{
				//conjson.put("id", "");
				//conarray.add(conjson);
				//inmap.put("contactName",conarray.toString() );
			}
			//负责人
			JSONArray resparray = new JSONArray();
			JSONObject respjson = new JSONObject();
			if(null != plan.getResponsiblePerson()){
				respjson.put("id", plan.getResponsiblePerson().getId());
				respjson.put("empno", plan.getResponsiblePerson().getEmpcode());
				respjson.put("empname", plan.getResponsiblePerson().getEmpname());
				resparray.add(respjson);
				inmap.put("responsName",resparray.toString() );
				inmap.put("rName",plan.getResponsiblePerson().getEmpname() );
			}else{
				//respjson.put("id", "");
				//resparray.add(respjson);
				//inmap.put("responsName",resparray.toString() );
			}
			inmap.put("checkContent", plan.getCheckContent());
			//开始时间
			if(null != plan.getBeginDate()){
				inmap.put("beginDataStr", plan.getBeginDate().toString().split(" ")[0]);
			}else{
				inmap.put("beginDataStr", "");
			}
			//结束时间
			if(null != plan.getEndDate()){
				inmap.put("endDataStr", plan.getEndDate().toString().split(" ")[0]);
			}else{
				inmap.put("endDataStr", "");
			}
			if(null != plan.getEndDate()&&null != plan.getBeginDate()){
				inmap.put("beginendDateStr", plan.getBeginDate().toString().split(" ")[0]+"-"+plan.getEndDate().toString().split(" ")[0]);
			}
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}

	@Transient
	public Map<String, Object> savaQuarterlyCheck(QuarterlyCheck quarterlyCheck) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		if(StringUtils.isBlank(quarterlyCheck.getId())) {
			quarterlyCheck.setId(Identities.uuid());
		} 
	if(null != quarterlyCheck.getContactName()){
			String conEmpId = "";
			JSONArray conArray = JSONArray.fromObject(quarterlyCheck.getContactName());
			if(null != conArray && conArray.size()>0){
				conEmpId = (String)JSONObject.fromObject(conArray.get(0)).get("id");
			}
			SysEmployee conEmp = o_empGridBO.findEmpEntryByEmpId(conEmpId);
			if(null != conEmp){
				quarterlyCheck.setContactPerson(conEmp);//保存联系人
			}
		}
		if(null != quarterlyCheck.getResponsName()){
			String respEmpId = "";
			JSONArray respArray = JSONArray.fromObject(quarterlyCheck.getResponsName());
			if(null != respArray && respArray.size()>0){
				respEmpId = (String)JSONObject.fromObject(respArray.get(0)).get("id");
			}
			SysEmployee respEmp = o_empGridBO.findEmpEntryByEmpId(respEmpId);
			if(null != respEmp){
				quarterlyCheck.setResponsiblePerson(respEmp);//保存负责人
			}
		}
		if(StringUtils.isNotBlank(quarterlyCheck.getBeginDateStr())){//开始时间
			quarterlyCheck.setBeginDate(DateUtils.parseDate(quarterlyCheck.getBeginDateStr(), "yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(quarterlyCheck.getEndDateStr())){//开始时间
			quarterlyCheck.setEndDate(DateUtils.parseDate(quarterlyCheck.getEndDateStr(), "yyyy-MM-dd"));
		}
		quarterlyCheck.setCreateEmp(UserContext.getUser().getEmp());//计划创建人
		quarterlyCheck.setDealStatus(Contents.DEAL_STATUS_NOTSTART);//点保存按钮，处理状态为"未开始"
		quarterlyCheck.setStatus(Contents.DEAL_STATUS_SAVED);//保存：状态为"已保存"

		quarterCheckPlanDAO.merge(quarterlyCheck);;
		inmap.put("planId", quarterlyCheck.getId());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}

	@Transient
	public Map<String, Object> deleteQuarterlyPlan(String id) {
		Map<String, Object> data=new HashMap<String, Object>();
		boolean t=true;
		try {
			String ids[]=id.split(",");
			for (int i = 0; i < ids.length; i++) {
				QuarterlyCheck quarterlyCheck=new QuarterlyCheck();
				quarterlyCheck.setId(ids[i]);
				quarterCheckPlanDAO.delete(quarterlyCheck);
			}
		} catch (Exception e) {
			t=false;
			e.printStackTrace();
		   
		}
		data.put("data", t);
		
		return data;
	}


}
