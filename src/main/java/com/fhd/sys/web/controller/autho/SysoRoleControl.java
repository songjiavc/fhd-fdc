package com.fhd.sys.web.controller.autho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmpPosi;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.sys.business.autho.SysoRoleBO;
import com.fhd.sys.web.form.autho.SysoUserForm;

/**
 * 
 * ClassName:SysoRoleControl 系统角色Control
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-7-29		上午9:45:10
 *
 * @see
 */
@Controller
public class SysoRoleControl {
    @Autowired
    private SysoRoleBO o_sysoRoleBO;

    /**
	 * 初始化Grid(员工列表)
	 * @author 翟辉
	 * @param start 开始页码
	 * @param limit 分页码
	 * @param query 查询条件
	 * @param sort 排序字段
	 * @return Map<String, Object>
	*/
	@ResponseBody
	@RequestMapping(value = "/sys/autho/findrolePage.f")
	public Map<String, Object> findrolePage(int start, int limit, String query, String sort,String roleId){
		Map<String, Object> map = new HashMap<String, Object>();
		List<SysoUserForm> datas = new ArrayList<SysoUserForm>();
		Page<SysEmployee> page = o_sysoRoleBO.findrolePage(start, limit, query, sort, roleId);
		List<SysEmployee> sysEmployeeList = page.getResult();
		for (SysEmployee sysEmployee : sysEmployeeList) {
			String posiName = "";//岗位
			String deptName = "";//部门
			String companyname = "";//公司
			String username = sysEmployee.getUsername();//用户名
			String userId = sysEmployee.getUserid();//用户Id
			String empname  = sysEmployee.getEmpname();//员工姓名
			if(null!=sysEmployee.getSysOrganization()){
				companyname = sysEmployee.getSysOrganization().getOrgname();
				
			}
			
			Set<SysEmpPosi> sysEmpPosis =  sysEmployee.getSysEmpPosis();
			for (SysEmpPosi sysEmpPosi : sysEmpPosis) {
				posiName = sysEmpPosi.getSysPosition().getPosiname();
			}
			Set<SysEmpOrg> sysEmpOrgs = sysEmployee.getSysEmpOrgs();
			for (SysEmpOrg sysEmpOrg : sysEmpOrgs) {
				deptName = sysEmpOrg.getSysOrganization().getOrgname();
			}
			datas.add(new SysoUserForm(posiName,deptName,username,empname,companyname,roleId,userId));
			
		}
		map.put("totalCount", page.getTotalItems());
		map.put("datas", datas);
	
		return map;
	}
	
	/**
	 *删除角色下的人员
	 * @author 翟辉
	 * @param roleItem
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/autho/delUserAndRole.f")
	public Map<String, Object> delUserAndRole(String roleItem) {
		Map<String, Object> map = new HashMap<String, Object>();
		o_sysoRoleBO.deleteRoleUser(roleItem);	
		map.put("success", true);
		return map;
    }
	
	
	/**
	 *	添加角色下的人员
	 * @author 翟辉
	 * @param roleItem
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/autho/addUserAndRole.f")
	public Map<String, Object> addUserAndRole(String id,String roleItem) {
		Map<String, Object> map = new HashMap<String, Object>();
		o_sysoRoleBO.addRoleUser(id,roleItem);
		map.put("success", true);	
		return map;	
    }

}