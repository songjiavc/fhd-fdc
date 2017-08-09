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
import com.fhd.entity.sys.orgstructure.TmpImpRole;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
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
public class TmpImpRoleControl {
	
	@Autowired
	private TmpImpRoleBO o_tmpImpRoleBO;
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
	@RequestMapping(value = "/sys/role/import/validate.f")
	public String validate(String fileId) throws Exception {
		o_tmpImpRoleBO.validate(fileId);
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
	@RequestMapping(value = "/sys/role/import/importData.f")
	public void importData(String fileId) throws Exception {
		o_tmpImpRoleBO.importData(fileId);
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
	@RequestMapping(value = "/sys/role/import/findPageBySome.f")
	public Map<String,Object> findPageBySome(String query,String fileId,Integer limit, Integer start, String sort) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		JsonBinder binder = JsonBinder.buildNonNullBinder();
		List<Map<String, String>> sortList = new ArrayList<Map<String,String>>();
		if(StringUtils.isNotBlank(sort)){
			sortList = binder.fromJson(sort, (new ArrayList<HashMap<String, String>>()).getClass());
		}
		Page<TmpImpRole> page=new Page<TmpImpRole>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		page = o_tmpImpRoleBO.findPageBySome(page, query, fileId, sortList);
		List<TmpImpRole> list = page.getResult();
		List<Map<String,Object>> datas = new ArrayList<Map<String, Object>>();
		for (TmpImpRole tmpImpRole : list) {
			Map<String, Object> tmpImpRoleToMap = this.tmpImpRoleToMap(tmpImpRole);
			datas.add(tmpImpRoleToMap);
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
		map.put("success", true);
		return map;
	}
	/**
	 * 
	 * tmpImpRoleToMap:转为MAP格式
	 * 
	 * @author 杨鹏
	 * @param tmpImpRole
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> tmpImpRoleToMap(TmpImpRole tmpImpRole){
		String id = tmpImpRole.getId();
		String index = tmpImpRole.getIndex();
		String roleCode = tmpImpRole.getRoleCode();
		String roleName = tmpImpRole.getRoleName();
		String authorityName = tmpImpRole.getAuthorityName();
		String error = tmpImpRole.getError();
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("index", index);
		map.put("roleCode", roleCode);
		map.put("roleName", roleName);
		map.put("authorityName", authorityName);
		map.put("error", error);
		return map;
	}
	
/**************************************新版导入分割线******************************************************/
	/**
	 * 读取角色excel，并保存角色临时表
	 * add by 王再冉
	 * 2014-4-24  上午10:49:26
	 * desc : 
	 * @param form
	 * @param response
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/sys/role/import/importroledata.f")
	public void importRoleData(FileForm form, HttpServletResponse response) throws Exception {
		List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
		.readEspecialExcel(form.getFile().getInputStream(), 1);// 读取文件(第2页)
		
		Map<String, Object> map = o_tmpImpRoleBO.importTmpRoleData(excelDatas);
		
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	/**
	 * 查看角色临时表
	 * add by 王再冉
	 * 2014-4-24  上午11:24:01
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/role/import/findalltmpsysrolesgrid.f")
	public Map<String, Object> findAllTmpSysRolesGrid(String query) {
		return o_tmpImpRoleBO.findAllTmpSysRolesGrid(query);
	}
	/**
	 * 导入角色表
	 * add by 王再冉
	 * 2014-4-24  下午1:42:28
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/role/import/importtosysrolefromtmp.f")
	public Map<String, Object> importToSysRoleFromTmp() {
		Map<String, Object> data = new HashMap<String,Object>();
		o_tmpImpRoleBO.saveAllRolesFromTmpRole();
		data.put("success", true);
		return data;
	}
	
}

