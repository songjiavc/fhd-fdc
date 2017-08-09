/**
 * OrgTreeControl.java
 * com.fhd.sys.web.controller.organization
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-1-11 		黄晨曦
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.web.controller.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.TmpImpOrganization;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sys.business.organization.TempEmpExcelDataBO;
import com.fhd.sys.business.organization.TempEmpOrgExcelDataBO;
import com.fhd.sys.business.organization.TempEmpPostExcelDataBO;
import com.fhd.sys.business.organization.TempEmpRoleExcelDataBO;
import com.fhd.sys.business.orgstructure.TmpImpOrganizationBO;
import com.fhd.sys.business.orgstructure.TmpImpPositionBO;
import com.fhd.sys.business.orgstructure.TmpImpRoleBO;
import com.fhd.sys.web.form.file.FileForm;

/**
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-1-11		下午1:04:06
 *
 * @see 	 
 */
@Controller
public class TmpImpOrganizationControl {
	
	@Autowired
	private TmpImpOrganizationBO o_tmpImpOrganizationBO;
	@Autowired
	private TmpImpPositionBO o_tmpImpPositionBO;
	@Autowired
	private TmpImpRoleBO o_tmpImpRoleBO;
	@Autowired
	private TempEmpExcelDataBO o_tempEmpExcelDataBO;
	@Autowired
	private TempEmpOrgExcelDataBO o_tempEmpOrgExcelDataBO;
	@Autowired
	private TempEmpRoleExcelDataBO o_tempEmpRoleExcelDataBO;
	@Autowired
	private TempEmpPostExcelDataBO o_tempEmpPostExcelDataBO;
	
	/**
	 * 
	 * validate:
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/validate.f")
	public String validate(String fileId) throws Exception {
		o_tmpImpOrganizationBO.validate(fileId);
		return fileId;
	}
	/**
	 * 
	 * importData:
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/importData.f")
	public void importData(String fileId) throws Exception {
		o_tmpImpOrganizationBO.importData(fileId);
	}
	/**
	 * 
	 * findPageBySome:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param fileId
	 * @param limit
	 * @param start
	 * @param sort
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/findPageBySome.f")
	public Map<String,Object> findPageBySome(String query,String fileId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<TmpImpOrganization> page=new Page<TmpImpOrganization>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_tmpImpOrganizationBO.findPageBySome(page, query, fileId, sortList);
		List<TmpImpOrganization> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (TmpImpOrganization tmpImpOrganization : list) {
			Map<String, Object> tmpImpOrganizationToMap = this.tmpImpOrganizationToMap(tmpImpOrganization);
			datas.add(tmpImpOrganizationToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	/**
	 * 
	 * tmpImpOrganizationToMap:转为MAP格式
	 * 
	 * @author 杨鹏
	 * @param tmpImpOrganization
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> tmpImpOrganizationToMap(TmpImpOrganization tmpImpOrganization){
		String id = tmpImpOrganization.getId();
		String index = tmpImpOrganization.getIndex();
		String companyCode = tmpImpOrganization.getCompanyCode();
		String companyName = tmpImpOrganization.getCompanyName();
		String organizationCode = tmpImpOrganization.getOrganizationCode();
		String organizationName = tmpImpOrganization.getOrganizationName();
		String positionCode = tmpImpOrganization.getPositionCode();
		String positionName = tmpImpOrganization.getPositionName();
		String userName = tmpImpOrganization.getUserName();
		String empName = tmpImpOrganization.getEmpName();
		String haveRoleCodes = tmpImpOrganization.getHaveRoleCodes();
		String haveRoleNames = tmpImpOrganization.getHaveRoleNames();
		String error = tmpImpOrganization.getError();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("index", index);
		map.put("companyCode", companyCode);
		map.put("companyName", companyName);
		map.put("organizationCode", organizationCode);
		map.put("organizationName", organizationName);
		map.put("positionCode", positionCode);
		map.put("positionName", positionName);
		map.put("userName", userName);
		map.put("empName", empName);
		map.put("haveRoleCodes", haveRoleCodes);
		map.put("haveRoleNames", haveRoleNames);
		map.put("error", error);
		return map;
	}
	
	/**
	 * 导入机构数据
	 * add by 王再冉
	 * 2014-4-21  下午6:00:57
	 * desc : 
	 * @param form
	 * @throws Exception 
	 * void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/importorgdata.f")
	public void importOrgData(FileForm form, HttpServletResponse response) throws Exception {
		List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
		.readEspecialExcel(form.getFile().getInputStream(), 0);// 读取文件(第1页)
		
		Map<String, Object> map = o_tmpImpOrganizationBO.importOrgData(excelDatas);
		
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	/**
	 * 查询机构临时表数据
	 * add by 王再冉
	 * 2014-4-23  上午11:35:12
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/findalltmpsysorganizations.f")
	public Map<String, Object> findAllTmpSysOrganizations(String query) {
		return o_tmpImpOrganizationBO.findAllTmpSysOrganizations(query);
	}
	/**
	 * 导入机构数据
	 * add by 王再冉
	 * 2014-4-23  下午4:10:58
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/importtosysorganizationfromtmp.f")
	public Map<String, Object> importToSysOrganizationFromTmp() {
		Map<String, Object> data = new HashMap<String,Object>();
		o_tmpImpOrganizationBO.saveAllOrgsFromTmpOrg();
		data.put("success", true);
		return data;
	}
	/**
	 * 全部导入
	 * add by 王再冉
	 * 2014-4-25  下午3:38:09
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/importallfromtmp.f")
	public Map<String, Object> importAllFromTmp() {
		Map<String, Object> data = new HashMap<String,Object>();
		o_tmpImpOrganizationBO.importAllFromTmp();
		data.put("success", true);
		o_tempEmpExcelDataBO.deleteTmpEmpDatas();//删除人员临时表
		o_tempEmpOrgExcelDataBO.deleteTmpEmpOrgDatas();//清空人员机构临时表
		o_tempEmpRoleExcelDataBO.deleteEmpRoleDatas();//清空人员角色临时表
		o_tempEmpPostExcelDataBO.deleteTmpEmpPosiDatas();//清空人员岗位临时表
		return data;
	}
	/**
	 * 全部上传
	 * add by 王再冉
	 * 2014-4-25  下午4:25:13
	 * desc : 
	 * @param form
	 * @param response
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/importalldata.f")
	public void importAllData(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String,Map<String,Object>> resuleMapList = new HashMap<String, Map<String,Object>>();
		Boolean flag = true;
		//上传机构数据
		List<List<String>> orgExcelDatas = (List<List<String>>)new ReadExcel()
		.readEspecialExcel(form.getFile().getInputStream(), 0);// 读取文件(第1页)
		Map<String, Object> orgmap = o_tmpImpOrganizationBO.importOrgData(orgExcelDatas);
		resuleMapList.put("orgmap", orgmap);
		//上传角色数据
		List<List<String>> roleExcelDatas = (List<List<String>>)new ReadExcel()
		.readEspecialExcel(form.getFile().getInputStream(), 1);// 读取文件(第2页)
		Map<String, Object> rolemap = o_tmpImpRoleBO.importTmpRoleData(roleExcelDatas);
		resuleMapList.put("rolemap", rolemap);
		//上传岗位数据(验证机构临时表及实体)
		List<List<String>> posiExcelDatas = (List<List<String>>)new ReadExcel()
		.readEspecialExcel(form.getFile().getInputStream(), 2);// 读取文件(第3页)
		Map<String, Object> posimap = o_tmpImpPositionBO.importAllPosiData(posiExcelDatas);
		resuleMapList.put("posimap", posimap);
		
		
		HashMap<String, Object> tempOrgByObjAllMap = o_tempEmpOrgExcelDataBO.findTempOrgByObjAllMap();
		
		//人员
		Map<String, Object> empMap = o_tempEmpExcelDataBO.findTempEmpEx(form, 3, tempOrgByObjAllMap);
		resuleMapList.put("empMap", empMap);
		HashMap<String, Object> tempEmpObjeAllMap = o_tempEmpExcelDataBO.findTempEmpByObjAllMap();
		HashMap<String, SysEmployee> empByEmpCompanyAllMap = o_tempEmpExcelDataBO.findEmpByEmpCompanyAllMap();
		
		//人员机构
		Map<String, Object> empOrgMap = o_tempEmpOrgExcelDataBO.findTempEmpOrgEx(form, 4, tempEmpObjeAllMap, empByEmpCompanyAllMap, tempOrgByObjAllMap);
		resuleMapList.put("empOrgMap", empOrgMap);
		
		//人员角色
		Map<String, Object> empRoleMap = o_tempEmpRoleExcelDataBO.findTempEmpRoleEx(form, 5);
		resuleMapList.put("empRoleMap", empRoleMap);
		
		//人员岗位
		Map<String, Object> empPostMap = o_tempEmpPostExcelDataBO.findTempEmpPostEx(form, 6, tempEmpObjeAllMap, empByEmpCompanyAllMap, tempOrgByObjAllMap);
		resuleMapList.put("empPostMap", empPostMap);
		
		//如果其中一个校验信息为false，则校验信息都为false
		if("false".equals(orgmap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(rolemap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(posimap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empMap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empOrgMap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empRoleMap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empPostMap.get("errorInfo").toString())){
			flag = false;
		}
		
		map.put("datas", resuleMapList);
		map.put("errorInfo", flag);
		map.put("success", true);
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	
	/**
	 * 查看全部数据
	 * add by 王再冉
	 * 2014-4-30  上午11:35:35
	 * desc : 
	 * @param query
	 * @param response
	 * @throws Exception 
	 * void
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/organization/import/findallTmpDatas.f")
	public void findAllTmpDatas(String query, HttpServletResponse response) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String,Map<String,Object>> resuleMapList = new HashMap<String, Map<String,Object>>();
		Boolean flag = true;
		//上传机构数据
		Map<String, Object> orgmap = o_tmpImpOrganizationBO.findAllTmpSysOrganizations(query);
		resuleMapList.put("orgmap", orgmap);
		//上传角色数据
		Map<String, Object> rolemap = o_tmpImpRoleBO.findAllTmpSysRolesGrid(query);
		resuleMapList.put("rolemap", rolemap);
		//上传岗位数据(验证机构临时表及实体)
		Map<String, Object> posimap = o_tmpImpPositionBO.findAllTmpSysPositions(query);
		resuleMapList.put("posimap", posimap);
		//人员
		Map<String, Object> empMap = o_tempEmpExcelDataBO.findAllTmpEmpExcelDatas(query);
		resuleMapList.put("empMap", empMap);
		//人员机构
		Map<String, Object> empOrgMap = o_tempEmpOrgExcelDataBO.findAllTmpEmpExcelDatas(query);
		resuleMapList.put("empOrgMap", empOrgMap);
		//人员角色
		Map<String, Object> empRoleMap = o_tempEmpRoleExcelDataBO.findAllTmpEmpRolesExcelDatas(query);
		resuleMapList.put("empRoleMap", empRoleMap);
		//人员岗位
		Map<String, Object> empPostMap = o_tempEmpPostExcelDataBO.findAllTmpEmpPosisExcelDatas(query);
		resuleMapList.put("empPostMap", empPostMap);
		
		//如果其中一个校验信息为false，则校验信息都为false
		if("false".equals(orgmap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(rolemap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(posimap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empMap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empOrgMap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empRoleMap.get("errorInfo").toString())){
			flag = false;
		}else if("false".equals(empPostMap.get("errorInfo").toString())){
			flag = false;
		}
		map.put("datas", resuleMapList);
		map.put("errorInfo", flag);
		map.put("success", true);
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
}

