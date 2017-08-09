
package com.fhd.comm.web.controller.cmp;

import com.fhd.comm.business.cmp.OrganizationCmpBO;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 新员工选择组件control
 * 
 * @author 张健
 * @date 2013-7-11
 * @since Ver 1.1
 */
@Controller
public class OrganizationCmpController {

    @Autowired
    private OrganizationCmpBO o_organizationCmpBO;

    /**
     * 根据ID串，得到名称，编号，id对应的map
     * 
     * @author 张健
     * @param ids ID串
     * @return map
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/component/org/findEmpByIds.f")
    public Map<String, Object> findEmpByIds(String ids) {
        Set<String> empIdSet = new HashSet<String>();
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (StringUtils.isNotBlank(ids)) {
            // 把传过来的value转化成json对象数组
            JSONArray jsonArray = JSONArray.fromObject(ids);
            if (jsonArray.size() == 0) {
                return null;
            }
            // 把所有传过来的ID防盗empIdSet中
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (StringUtils.isNotBlank(jsonObject.getString("id"))) {
                    empIdSet.add(jsonObject.getString("id"));
                }
            }

            List<SysEmployee> empList = o_organizationCmpBO.findEmpByIdSet(empIdSet);

            // 防止有ID重复的情况，只对value中的机构返回map
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (StringUtils.isNotBlank(jsonObject.getString("id"))) {
                    for (SysEmployee emp : empList) {
                        if (jsonObject.getString("id").equals(emp.getId())) {
                            Map<String, Object> item = new HashMap<String, Object>();
                            item.put("id", emp.getId());
                            item.put("empno", emp.getEmpcode());
                            item.put("empname", emp.getEmpname());
                            list.add(item);
                        }
                    }
                }
            }
            map.put("success", true);
            map.put("data", list);
        }
        return map;
    }

    /**
     * 机构树查询
     * 
     * @author 张健
     * @param subCompany 是否显示子机构
     * @param orgIds 机构数组
     * @param empIds 员工数组
     * @return map
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/component/org/empTreeLoader.f")
    public List<Map<String, Object>> empTreeLoader(String node, Boolean subCompany, String orgIds, String empIds, String query) {
        String[] strings = StringUtils.split(node, "_");
        return o_organizationCmpBO.empTreeLoader(strings[0], subCompany, orgIds, empIds, query);
    }

    /**
     * 角色树查询
     * 
     * @author 张健
     * @param node 当前节点
     * @param query查询条件
     * @param roleIds 角色数组
     * @return
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/component/org/roleTreeLoader.f")
    public List<Map<String, Object>> roleTreeLoader(String node, String query, String roleIds, String empIds) {
        return o_organizationCmpBO.roleTreeLoader(node, query, roleIds, empIds);
    }

    /**
     * 根据组织或岗位id查询员工，会有机构和角色入参的限制条件
     * 
     * @author 张健
     * @param orgId 选中机构号
     * @param empIds 员工数组
     * @param roleIds 角色数组
     * @param employee 模糊查询数据项
     * @return
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/component/org/findEmpsByOrgId.f")
    public Map<String, Object> findEmpsByOrgId(String orgId, String empIds, String roleIds, SysEmployee employee) {
        Set<SysEmployee> empList = o_organizationCmpBO.findEmployeeByOrgId(orgId, empIds, roleIds, employee);
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        for (SysEmployee emp : empList) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("id", emp.getId());
            row.put("empno", emp.getEmpcode());
            row.put("empname", emp.getEmpname());
            datas.add(row);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("datas", datas);
        return map;
    }

    /**
     * 根据角色id查询员工，
     * 
     * @author 张健
     * @param roleId 选中的角色
     * @param empIds 员工数组
     * @param orgIds 机构数组
     * @param employee 模糊查询数据项
     * @return
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/component/org/findEmpsByRoleId.f")
    public Map<String, Object> findEmpsByRoleId(String roleId, String empIds, String orgIds, SysEmployee employee) {
        List<SysEmployee> empList = o_organizationCmpBO.findEmployeeByRoleId(UserContext.getUser().getCompanyid(), roleId, empIds, orgIds, employee);
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

        for (SysEmployee emp : empList) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("id", emp.getId());
            row.put("empno", emp.getEmpcode());
            row.put("empname", emp.getEmpname());
            datas.add(row);
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("datas", datas);

        return map;
    }

}
