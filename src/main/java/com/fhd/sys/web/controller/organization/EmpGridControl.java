/**
 * EmpGridControl.java
 * com.fhd.sys.web.controller.organization
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-21 		黄晨曦
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.web.controller.organization;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DigestUtils;
import com.fhd.core.utils.Identities;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.autho.SysoRoleBO;
import com.fhd.sys.business.dic.DictBO;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;
import com.fhd.dao.sys.autho.SysoUserDAO;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.duty.Duty;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.sys.web.form.autho.SysoRoleForm;
import com.fhd.sys.web.form.organization.EmployeeForm;

/**
 * @author   王再冉
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-1-21		上午11:14:17
 *
 * @see 	 
 */
@Controller
public class EmpGridControl {

	@Autowired
	private EmpGridBO o_empGridBO;
	
	@Autowired
	private DictBO o_dictBO;
	
	@Autowired
	private OrgGridBO o_orgGridBO;
	@Autowired
	private SysoRoleBO o_sysoRoleBO;
	@Autowired
	private SysoUserDAO o_sysUserDAO;
	
	/**
	 * 员工列表分页查询
	 * @author 王再冉
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/queryemppage.f")
	public Map<String, Object> queryEmpPage(int start, int limit, String query, String sort,String orgIds,String positionIds) throws Exception {
		String property = "";
		String direction = "";
		Page<SysEmployee> page = new Page<SysEmployee>();
		
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		
		if (StringUtils.isNotBlank(sort)){
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0){
                JSONObject jsobj = jsonArray.getJSONObject(0);
                property = jsobj.getString("property");
                direction = jsobj.getString("direction");
                if(property.equalsIgnoreCase("zq")){
                	property = "isRecycle";
    			}else if(property.equalsIgnoreCase("statusName")){
    				property = "status";
    			}else if(property.equalsIgnoreCase("triggerName")){
    				property = "triggerType";
    			}
            }
        }else{
        	property = "empcode";
        	direction = "ASC";
        }
		
		page = o_empGridBO.findEmpBySome(query, page, property, direction, orgIds, positionIds);
		
		List<SysEmployee> entityList = page.getResult();
		List<SysEmployee> datas = new ArrayList<SysEmployee>();
		for(SysEmployee de : entityList){
			String orgPart = "";
			Set<SysRole> roleList = de.getSysUser().getSysRoles();//员工角色
			Set<SysEmpOrg> empOrgSet = de.getSysEmpOrgs();//员工辅助部门集合
			SysEmpOrg empOrg = o_empGridBO.findEmpOrgByEmpId(de.getId());//员工主部门
			if(null != empOrg){
				orgPart = o_orgGridBO.findOrganizationByOrgId(empOrg.getSysOrganization().getId()).getOrgname();
			}
			datas.add(new EmployeeForm(de,roleList,orgPart,empOrgSet));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 保存员工信息
	 * @author 王再冉
	 * @param orgForm
	 * @param id
	 * @param response
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/saveEmpInfo.f")
	public void saveEmp(EmployeeForm empForm,String id, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		String isSave = "false";
		SysOrganization orgPart = new SysOrganization();//员工所属部门
	
		SysEmployee emp = new SysEmployee(Identities.uuid());
		emp.setEmpcode(empForm.getEmpcode());
		emp.setEmpname(empForm.getEmpname());
		emp.setRealname(empForm.getEmpname());//真实姓名
		emp.setGender(empForm.getGender());
		//生日
		if(StringUtils.isNotBlank(empForm.getBirthDateStr())){
			emp.setBirthdate(DateUtils.parseDate(empForm.getBirthDateStr(), "yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(empForm.getRegdateStr())){
			emp.setRegdate(DateUtils.parseDate(empForm.getRegdateStr(), "yyyy-MM-dd"));
		}
		//根据员工主部门保存员工所属公司
		if(null != empForm.getOrgStrMain()){
			String orgid = "";
			JSONArray orgArray = JSONArray.fromObject(empForm.getOrgStrMain());
			if(null!=orgArray&&orgArray.size()>0){
				orgid = (String)JSONObject.fromObject(orgArray.get(0)).get("id");
			}
			if(null != o_orgGridBO.findOrganizationByOrgId(orgid)){
				//emp.setSysOrganization(o_orgGridBO.findOrganizationByOrgId(orgid));
				orgPart = o_orgGridBO.findOrganizationByOrgId(orgid);
				if(null != orgPart.getCompany()){
					emp.setSysOrganization(o_orgGridBO.findOrganizationByOrgId(orgPart.getCompany().getId()));
				}else if(null != orgPart.getParentOrg()){//该部门没有companyId，则保存他的父机构为公司
					emp.setSysOrganization(o_orgGridBO.findOrganizationByOrgId(orgPart.getParentOrg().getId()));
				}else{//该部门没有companyId和parentId，保存该部门为公司
					emp.setSysOrganization(o_orgGridBO.findOrganizationByOrgId(orgPart.getId()));
				}
				
			}
		}
		//职务
		if(null != o_empGridBO.findDutyEntryBydutyId(empForm.getDutyStr())){
			emp.setDuty(o_empGridBO.findDutyEntryBydutyId(empForm.getDutyStr()));
		}
		emp.setOaddress(empForm.getOaddress());
		emp.setOemail(empForm.getOemail());
		emp.setOtel(empForm.getOtel());
		emp.setOzipcode(empForm.getOzipcode());
		if(null!=empForm.getEmpStatus()){
			emp.setEmpStatus(empForm.getEmpStatus());
		}else{
			emp.setEmpStatus("1");
		}
		
		emp.setFaxno(empForm.getFaxno());
		emp.setCardno(empForm.getCardno());
		emp.setCardtype(empForm.getCardtype());
		emp.setMobikeno(empForm.getMobikeno());
		emp.setMsn(empForm.getMsn());
		emp.setPemail(empForm.getPemail());
		emp.setHaddress(empForm.getHaddress());
		emp.setHtel(empForm.getHtel());
		emp.setHzipcode(empForm.getHzipcode());
		emp.setParty(empForm.getParty());
		emp.setDegree(empForm.getDegree());
		emp.setMajor(empForm.getMajor());
		emp.setSpecialty(empForm.getSpecialty());
		emp.setRemark(empForm.getRemark());
		//用户信息
		emp.setUsername(empForm.getUsername());
		SysUser user = o_empGridBO.findUserByuserName(empForm.getUsername());
		if(null!=user){
			user.setRealname(empForm.getEmpname());
			//用户最后登录时间
			if(StringUtils.isNotBlank(empForm.getUserLastLoginTimeStr())){
				user.setLastLoginTime(DateUtils.parseDate(empForm.getUserLastLoginTimeStr(), "yyyy-MM-dd"));
			}
			//用户密码过期日期
			if(StringUtils.isNotBlank(empForm.getUserCredentialsexpiryDateStr())){
				user.setCredentialsexpiryDate(DateUtils.parseDate(empForm.getUserCredentialsexpiryDateStr(), "yyyy-MM-dd"));
			}
			user.setUsername(empForm.getUsername());
			user.setUserStatus(empForm.getUserState());
			String passwordOld = user.getPassword();
			String passwordNew=empForm.getUserPassword();
			String password="";
			if(passwordOld.equals(passwordNew)){//如果未修改
				password=passwordOld;
			}else{//如果修改了
				password=DigestUtils.md5ToHex(passwordNew);
			}
			user.setPassword(password);
			user.setEnable(empForm.getEnable());
			user.setLockstate(empForm.getLockstate());
		}else{
			user = new SysUser();
			user.setId(Identities.uuid());
			user.setRealname(empForm.getEmpname());
			//用户最后登录时间
			if(StringUtils.isNotBlank(empForm.getUserLastLoginTimeStr())){
				user.setLastLoginTime(DateUtils.parseDate(empForm.getUserLastLoginTimeStr(), "yyyy-MM-dd"));
			}
			//用户密码过期日期
			if(StringUtils.isNotBlank(empForm.getUserCredentialsexpiryDateStr())){
				user.setCredentialsexpiryDate(DateUtils.parseDate(empForm.getUserCredentialsexpiryDateStr(), "yyyy-MM-dd"));
			}
			user.setUsername(empForm.getUsername());
			user.setUserStatus(empForm.getUserState());
			user.setPassword(DigestUtils.md5ToHex(empForm.getUserPassword()));
			user.setEnable(empForm.getEnable());
			user.setLockstate(empForm.getLockstate());
		}
		o_empGridBO.saveUser(user);//保存用户信息
		emp.setUserid(user.getId());
		emp.setSysUser(user);
		try{
			if(StringUtils.isNotBlank(id)){
				//更新
				String ids[] = id.split(",");
				emp.setId(ids[0]);
				SysEmployee findEmp = o_empGridBO.findEmpEntryByEmpId(emp.getId());
				if(null == emp.getSysOrganization()){
					emp.setSysOrganization(findEmp.getSysOrganization());
				}
				if(null == emp.getDuty()){
					emp.setDuty(findEmp.getDuty());//员工职务
				}
				emp.setDeleteStatus(findEmp.getDeleteStatus());
				o_empGridBO.mergeEmp(emp);
				o_empGridBO.saveEmpOrgMain(orgPart, emp);
				isSave = "true";
				
			}else{
				//保存
				emp.setDeleteStatus("1");//删除状态
				o_empGridBO.saveEmp(emp);
				o_empGridBO.saveEmpOrgMain(orgPart, emp);
				isSave = "true";
				id = emp.getId();
			}
			out.write(isSave);
		}finally {
			out.close();
		}
		saveEmpOrgs(empForm, id, response);
	}
	
	/**
	 * 保存用户部门
	 * @param empForm
	 * @param id
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/saveemporgs.f")
	public void saveEmpOrgs(EmployeeForm empForm,String id, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		String isSave = "false";
		String[] orgIdArray = null;//辅助部门id数组
		//员工辅助部门
				if(null != empForm.getOrgStrs()){
					JSONArray orgsArray = JSONArray.fromObject(empForm.getOrgStrs());
					orgIdArray = new String[orgsArray.size()];
					if(null!=orgsArray&&orgsArray.size()>0){
						for( int i = 0 ; i<orgsArray.size() ; i++ ){//得到部门id数组
							orgIdArray[i] = (String)JSONObject.fromObject(orgsArray.get(i)).get("id");
						}
					}
				}
		try{
			//保存
			if(StringUtils.isNotBlank(id)){
				//更新
				String ids[] = id.split(",");
				SysEmployee findEmp = o_empGridBO.findEmpEntryByEmpId(ids[0]);
				o_empGridBO.saveEmpOrgs(findEmp, orgIdArray);
				isSave = "true";
			}
			out.write(isSave);
		}finally {
			out.close();
		}
	}
	
	/**
	 * 删除员工
	 * @author 王再冉
	 * @param request
	 * @param ids	员工id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	 @ResponseBody
	  @RequestMapping(value = "/sys/organization/removeempentrybyid.f")
	  public boolean removeEmpEntryById(HttpServletRequest request, String ids) {
		  if (StringUtils.isNotBlank(ids)) {
			   o_empGridBO.removeEmpEntrys(ids);
			   return true;
		  } else {
			   return true;
		  }
	  }
	 
	/**
	 * 修改：显示所选用户信息
	 * 
	 * @author 王再冉
	 * @param request
	 * @param id	员工id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	 @ResponseBody
		@RequestMapping(value = "/sys/organization/findempbyid.f")
		public Map<String, Object> findEmpById(HttpServletRequest request, String id) {
			SysEmployee sysEmp = new SysEmployee();
			if(StringUtils.isNotBlank(id)){
				String ids[] = id.split(",");
				sysEmp.setId(ids[0]);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			SysEmployee emp = o_empGridBO.findEmpEntryByEmpId(sysEmp.getId());
			Map<String, String> inmap = new HashMap<String, String>();
			inmap.put("empcode", emp.getEmpcode());
			inmap.put("empname", emp.getEmpname());
			inmap.put("realname", emp.getEmpname());
			inmap.put("gender", emp.getGender());
			if(null != emp.getBirthdate()){//生日
				inmap.put("birthDateStr", emp.getBirthdate().toString().split(" ")[0]);
			}
			//所属主部门
			JSONArray emparray = new JSONArray();
			JSONObject empjson = new JSONObject();
			SysEmpOrg sysEmpOrg = o_empGridBO.findEmpOrgByEmpId(emp.getId());
			if(null != sysEmpOrg && sysEmpOrg.getIsmain()){
				empjson.put("id", sysEmpOrg.getSysOrganization().getId());
				empjson.put("deptno", sysEmpOrg.getSysOrganization().getOrgcode());
				empjson.put("deptname", sysEmpOrg.getSysOrganization().getOrgname());
				emparray.add(empjson);
				inmap.put("orgStrMain",emparray.toString() );
			}
			//辅助部门
			JSONArray empOrgsarray = new JSONArray();
			JSONObject empOrgsjson = new JSONObject();
			List<SysEmpOrg> empOrgList = o_empGridBO.findEmpOrgListByEmpId(emp.getId());//员工部门实体 
			if(empOrgList.size()>0){
				for(SysEmpOrg empOrgs : empOrgList){
					if(!empOrgs.getIsmain()){//不是主部门
						empOrgsjson.put("id", empOrgs.getSysOrganization().getId());
						empOrgsarray.add(empOrgsjson);
						inmap.put("orgStrs",empOrgsarray.toString() );
					}
				}
			}
			//职务
			if(null != emp.getDuty()){
				inmap.put("dutyStr", emp.getDuty().getDutyName());
			}else{
				inmap.put("dutyStr", "");
			}
	
			inmap.put("otel", emp.getOtel());
			inmap.put("oaddress", emp.getOaddress());
			inmap.put("ozipcode", emp.getOzipcode());
			inmap.put("oemail", emp.getOemail());
			inmap.put("faxno", emp.getFaxno());
			inmap.put("cardtype", emp.getCardtype());
			inmap.put("cardno", emp.getCardno());
			inmap.put("mobikeno", emp.getMobikeno());
			inmap.put("msn", emp.getMsn());
			inmap.put("htel", emp.getHtel());
			inmap.put("haddress", emp.getHaddress());
			inmap.put("hzipcode", emp.getHzipcode());
			inmap.put("pemail", emp.getPemail());
			inmap.put("party", emp.getParty());
			inmap.put("degree", emp.getDegree());
			inmap.put("empStatus", emp.getEmpStatus());
			inmap.put("major", emp.getMajor());
			inmap.put("specialty", emp.getSpecialty());
			//用户信息
			inmap.put("userid", emp.getUserid());
			inmap.put("username", emp.getUsername());
			inmap.put("userPassword", emp.getSysUser().getPassword());
			
			if(null!=emp.getSysUser().getCredentialsexpiryDate()){//密码过期时间
				inmap.put("userCredentialsexpiryDateStr", emp.getSysUser().getCredentialsexpiryDate()+"");
			}
			if(emp.getSysUser().getLockstate()==false){//用户锁定状态
				inmap.put("lockstate", "0");
			}else{
				inmap.put("lockstate", "1");
			}
			if(emp.getSysUser().getEnable()==false){//是否锁定
				inmap.put("enable", "0");
			}else{
				inmap.put("enable", "1");
			}
			inmap.put("userState", emp.getSysUser().getUserStatus());//用户状态
			if(null!=emp.getSysUser().getLastLoginTime()){//最后登录时间
				inmap.put("userLastLoginTimeStr", emp.getSysUser().getLastLoginTime().toString().split(" ")[0]);
			}
			
			if(null!=emp.getRegdate()){//注册日期
				inmap.put("regdateStr", emp.getRegdate().toString().split(" ")[0]);
			}
			inmap.put("remark", emp.getRemark());
			
			Set<SysRole> roleList = emp.getSysUser().getSysRoles();//员工角色字符串
			StringBuffer roleStr = new StringBuffer();
			
			for(SysRole sr : roleList){
				roleStr.append(sr.getRoleName());
			}
			inmap.put("roleStr", roleStr.toString());
			
			map.put("data", inmap);
			map.put("success", true);
			return map;
	    }
	
	/**
	 * 性别下拉列表
	 * 
	 * @author 王再冉
	 * @param response
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findempgender.f")
	public Map<String, Object> findEmpGender(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0gender");
		for (DictEntry dictEntry : dictEntryList) {
			inmap = new HashMap<String, Object>();
			inmap.put("id", dictEntry.getId());
			inmap.put("text",dictEntry.getName());
			list.add(inmap);
		}
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 政治面貌下拉菜单
	 * 
	 * @author 王再冉
	 * @param response
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findempparty.f")
	public Map<String, Object> findEmpParty(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0party");
		for (DictEntry dictEntry : dictEntryList) {
			inmap = new HashMap<String, Object>();
			inmap.put("id", dictEntry.getId());
			inmap.put("text",dictEntry.getName());
			list.add(inmap);
		}
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 学位下拉菜单
	 * 
	 * @author 王再冉
	 * @param response
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findempdegree.f")
	public Map<String, Object> findEmpDegree(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0degree");
		for (DictEntry dictEntry : dictEntryList) {
			inmap = new HashMap<String, Object>();
			inmap.put("id", dictEntry.getId());
			inmap.put("text",dictEntry.getName());
			list.add(inmap);
		}
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 证件类型下拉列表
	 * @author 王再冉
	 * @param response
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findempcardtype.f")
	public Map<String, Object> findEmpCardtype(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId("0card_type");
		for (DictEntry dictEntry : dictEntryList) {
			inmap = new HashMap<String, Object>();
			inmap.put("id", dictEntry.getId());
			inmap.put("text",dictEntry.getName());
			list.add(inmap);
		}
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 状态/用户状态下拉菜单
	 * @author 王再冉
	 * @param response
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findempstates.f")
	public Map<String, Object> findEmpStates(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		inmap = new HashMap<String, Object>();
		inmap.put("id", "1");
		inmap.put("text", "正常");
		list.add(inmap);
		inmap = new HashMap<String, Object>();
		inmap.put("id", "0");
		inmap.put("text", "注销");
		list.add(inmap);
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 员工所属机构下拉列表
	 * @author 王再冉
	 * @param response
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findemporganization.f")
	public Map<String, Object> findEmpOrganization(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String companyId = UserContext.getUser().getCompanyid();// 所在公司id
		List<SysOrganization> orgEntryList = new ArrayList<SysOrganization>();
		if(StringUtils.isNotBlank(companyId)){
			orgEntryList = o_orgGridBO.findOrganizationByCompanyIds(companyId);
		}else{
			//admin账号登陆
			orgEntryList = o_orgGridBO.findAllOrganizations();
		}
		if(null != orgEntryList && !orgEntryList.isEmpty()){
			for (SysOrganization orgEntry : orgEntryList) {
				inmap = new HashMap<String, Object>();
				inmap.put("id", orgEntry.getId());
				inmap.put("text",orgEntry.getOrgname());
				list.add(inmap);
			}
		}
		map.put("datas", list);
		return map;
	}
	
	
	/**
	 * 根据companyId查询职务下拉列表
	 * @author 王再冉
	 * @param response
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findempduty.f")
	public Map<String, Object> findEmpDuty(HttpServletResponse response) throws Exception {
		Map<String, Object> inmap = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String companyId = UserContext.getUser().getCompanyid();// 所在公司id
		List<Duty> dutyEntryList = o_empGridBO.findDutyByCompanyId(companyId);
		if(dutyEntryList != null){
			for (Duty dutyEntry : dutyEntryList) {
				inmap = new HashMap<String, Object>();
				inmap.put("id", dutyEntry.getId());
				inmap.put("text", dutyEntry.getDutyName());
				list.add(inmap);
			}
		}
		map.put("datas", list);
		return map;
	}
	
	/**
	 * 角色列表查询
	 * @param start
	 * @param limit
	 * @param query
	 * @param sort
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/queryemprolepage.f")
	public Map<String, Object> queryEmpRolePage(int start, int limit, String query, String sort) throws Exception {
		String property = "";
		String direction = "";
		Page<SysRole> page = new Page<SysRole>();
		
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		
		if (StringUtils.isNotBlank(sort)){
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0){
                JSONObject jsobj = jsonArray.getJSONObject(0);
                property = jsobj.getString("property");
                direction = jsobj.getString("direction");
                if(property.equalsIgnoreCase("zq")){
                	property = "isRecycle";
    			}else if(property.equalsIgnoreCase("statusName")){
    				property = "status";
    			}else if(property.equalsIgnoreCase("triggerName")){
    				property = "triggerType";
    			}
            }
        }else{
        	property = "roleCode";
        	direction = "ASC";
        }
		
		page = o_empGridBO.findEmpRoleBySome(query, page, property, direction);
		
		List<SysRole> entityList = page.getResult();
		List<SysRole> datas = new ArrayList<SysRole>();
		for(SysRole de : entityList){
			datas.add(new SysoRoleForm(de));
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		return map;
	}
	
	/**
	 * 保存员工新增角色(分配角色)
	 * @param empIds
	 * @param ids
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/saveemproleentry.f")
	public void saveEmpRole(String empIds,String ids, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		List<SysEmployee> empList = new ArrayList<SysEmployee>();
		Set<SysRole> roleSet = new HashSet<SysRole>(); 
		if(StringUtils.isNotBlank(empIds)){
			String empidArray[] = empIds.split(",");
			for(int i=0;i<empidArray.length;i++){
				String empId = empidArray[i];
				empList.add(o_empGridBO.findEmpEntryByEmpId(empId));
			}
		}
		if(StringUtils.isNotBlank(ids)){
			String roleidArray[] = ids.split(",");
			for(int i=0;i<roleidArray.length;i++){
				String roleId = roleidArray[i];
				roleSet.add(o_sysoRoleBO.queryRoleById(roleId));
			}
		}
		try{
			//保存
			for(SysEmployee emp : empList){
				/*SysUser user = emp.getSysUser();
				user.setSysRoles(roleSet);*/
				o_empGridBO.saveSysUserRole(emp.getUserid(), roleSet);
			}
			out.write("true");
		}finally {
			out.close();
		}
	}
	/**
	 * 用户密码重置
	 * @param userIds	用户id
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/resetuserpassword.f")
	public void resetUsersPassword(String userIds, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		String[] idArray = userIds.split(",");
		try{
			for (String id : idArray) {
				SysUser user = o_sysUserDAO.get(id);//通过用户id查询用户
				user.setPassword(DigestUtils.md5ToHex("111111"));
				o_empGridBO.saveUser(user);
			}
			out.write("true");
		}finally {
			out.close();
		}
	}
	
	/**
	 * 給新增人员默认主责部门
	 * @param orgId		当前树节点选中的机构id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findmainorgbyorgnodeid.f")
	public Map<String, Object> findmainOrgByorgNodeId(String orgId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		JSONArray orgarray = new JSONArray();
		JSONObject orgjson = new JSONObject();
		if(StringUtils.isBlank(orgId)){
			orgId = UserContext.getUser().getCompanyid();
		}
		SysOrganization org = o_orgGridBO.findOrganizationByOrgId(orgId);
		if(null != org){
			orgjson.put("id", org.getId());
			orgjson.put("deptno", org.getOrgcode());
			orgjson.put("deptname", org.getOrgname());
			orgarray.add(orgjson);
			inmap.put("orgStrMain",orgarray.toString() );
		}
		map.put("data", inmap);
		map.put("success", true);
		return map;
    }
	
	
	/**
	   * 判断当前登录人员是否为集团员工
	   * @return
	   */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/findempiscompany.f")
	public Map<String,Boolean> findEmpIsCompany(){
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		Boolean isCompanyEmp = false;	//true表示是集团的员工
	  	String companyId = UserContext.getUser().getCompanyid();
	  	SysOrganization org = o_orgGridBO.findOrganizationByOrgId(companyId);
	  	if(null != org && null == org.getParentOrg()){
	  		isCompanyEmp = true;  //是集团的员工
	  	}
	  	map.put("isCompanyPerson", isCompanyEmp);
	  	return map;
  	}
	
}

