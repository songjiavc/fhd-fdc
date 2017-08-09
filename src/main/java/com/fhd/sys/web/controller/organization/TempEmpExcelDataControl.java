package com.fhd.sys.web.controller.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmpPosi;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpOrgExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpPostExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpRoleExcelData;
import com.fhd.sys.business.organization.TempEmpExcelDataBO;
import com.fhd.sys.business.organization.TempEmpOrgExcelDataBO;
import com.fhd.sys.business.organization.TempEmpPostExcelDataBO;
import com.fhd.sys.business.organization.TempEmpRoleExcelDataBO;
import com.fhd.sys.web.form.file.FileForm;

/**
 * 人员控制
 * */

@Controller
public class TempEmpExcelDataControl {

	@Autowired
	private TempEmpExcelDataBO o_tempEmpExcelDataBO;
	@Autowired
	private TempEmpOrgExcelDataBO o_tempEmpOrgExcelDataBO;
	@Autowired
	private TempEmpRoleExcelDataBO o_tempEmpRoleExcelDataBO;
	@Autowired
	private TempEmpPostExcelDataBO o_tempEmpPostExcelDataBO;
	
	/**
	 * 员工导入
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmp.f")
	public void importEmp(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> map = o_tempEmpExcelDataBO.findEmpEx(form, 3);
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	
	/**
	 * 员工机构导入
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmpOrg.f")
	public void importEmpOrg(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> map = o_tempEmpOrgExcelDataBO.findEmpOrgEx(form, 4);
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	
	/**
	 * 员工角色导入
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmpRole.f")
	public void importEmpRole(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> map = o_tempEmpRoleExcelDataBO.findEmpRoleEx(form, 5);
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	
	/**
	 * 员工岗位导入
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmpPost.f")
	public void importEmpPosi(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> map = o_tempEmpPostExcelDataBO.findEmpPostEx(form, 6);
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	
	/**
	 * 员工导入
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmpSave.f")
	public Map<String, Object> importEmpSave(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> data = new HashMap<String,Object>();
		List<TempEmpExcelData> list = o_tempEmpExcelDataBO.findTempEmp(null);
		ArrayList<SysUser> userList = o_tempEmpExcelDataBO.getAdjustUser(list);
		ArrayList<SysEmployee> empList = o_tempEmpExcelDataBO.getAdjustEmp(list);
		o_tempEmpExcelDataBO.saveUser(userList);
		o_tempEmpExcelDataBO.saveEmp(empList);
		data.put("success", true);
		//清空临时表数据
		o_tempEmpExcelDataBO.deleteTmpEmpDatas();
		return data;
	}
	
	
	/**
	 * 员工机构导入数据
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmpOrgSave.f")
	public Map<String, Object> importEmpOrgSave(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> data = new HashMap<String,Object>();
		List<TempEmpOrgExcelData> list = o_tempEmpOrgExcelDataBO.findTempEmp(null);
		ArrayList<SysEmpOrg> userList = o_tempEmpOrgExcelDataBO.getAdjustUser(list);
		o_tempEmpOrgExcelDataBO.saveEmpOrg(userList);
		data.put("success", true);
		//清空临时表数据
		o_tempEmpOrgExcelDataBO.deleteTmpEmpOrgDatas();
		return data;
	}
	
	/**
	 * 员工角色导入数据
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmpRoleSave.f")
	public Map<String, Object> importEmpRoleSave(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> data = new HashMap<String,Object>();
		List<TempEmpRoleExcelData> list = o_tempEmpRoleExcelDataBO.findTempEmp(null);
		List<String> roleList = o_tempEmpRoleExcelDataBO.getEmpRole(list);
		o_tempEmpRoleExcelDataBO.saveEmpOrg(roleList);
		data.put("success", true);
		//清空临时表数据
		o_tempEmpRoleExcelDataBO.deleteEmpRoleDatas();
		return data;
	}
	
	/**
	 * 员工岗位导入数据
	 * */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/importEmpPostSave.f")
	public Map<String, Object> importEmpPostSave(FileForm form, HttpServletResponse response) throws Exception {
		Map<String, Object> data = new HashMap<String,Object>();
		List<TempEmpPostExcelData> list = o_tempEmpPostExcelDataBO.findTempEmp(null);
		List<SysEmpPosi> empPosiList = o_tempEmpPostExcelDataBO.getEmpPosi(list);
		o_tempEmpPostExcelDataBO.saveEmpPost(empPosiList);
		data.put("success", true);
		//清空临时表数据
		o_tempEmpPostExcelDataBO.deleteTmpEmpPosiDatas();
		return data;
	}
	/**
	 * 查询员工导入临时表
	 * add by 王再冉
	 * 2014-4-30  上午9:48:52
	 * desc : 
	 * @param query	搜索关键字
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/findalltmpsysemps.f")
	public Map<String, Object> findAllTmpSysEmps(String query) {
		return o_tempEmpExcelDataBO.findAllTmpEmpExcelDatas(query);
	}
	/**
	 * 查询员工机构临时表
	 * add by 王再冉
	 * 2014-4-30  上午10:08:37
	 * desc : 
	 * @param query	搜索关键字
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/findallemporgs.f")
	public Map<String, Object> findAllTmpEmpOrgs(String query) {
		return o_tempEmpOrgExcelDataBO.findAllTmpEmpExcelDatas(query);
	}
	/**
	 * 查询全部员工角色临时表
	 * add by 王再冉
	 * 2014-4-30  上午10:24:57
	 * desc : 
	 * @param query 搜索关键字
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/findallemproles.f")
	public Map<String, Object> findAllTmpEmpRoels(String query) {
		return o_tempEmpRoleExcelDataBO.findAllTmpEmpRolesExcelDatas(query);
	}
	/**
	 * 查询全部员工岗位临时表
	 * add by 王再冉
	 * 2014-4-30  上午10:34:32
	 * desc : 
	 * @param query
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/org/import/findallempPosis.f")
	public Map<String, Object> findAllTmpEmpPosis(String query) {
		return o_tempEmpPostExcelDataBO.findAllTmpEmpPosisExcelDatas(query);
	}
}