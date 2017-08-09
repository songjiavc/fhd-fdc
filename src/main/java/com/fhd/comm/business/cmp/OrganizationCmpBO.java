
package com.fhd.comm.business.cmp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fhd.dao.sys.auth.RoleDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.dao.sys.orgstructure.SysEmpOrgDAO;
import com.fhd.dao.sys.orgstructure.SysEmpPosiDAO;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmpPosi;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;

/**
 * 新员工选择组件BO
 * 
 * @author 张健
 * @date 2013-7-11
 * @since Ver 1.1
 */
@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class OrganizationCmpBO {

    @Autowired
    private EmployeeDAO o_sysEmployeeDAO;

    @Autowired
    private SysOrgDAO o_sysOrgDAO;

    @Autowired
    private RoleDAO o_sysRoleDAO;

    @Autowired
    private SysEmpOrgDAO o_sysEmpOrgDAO;

    @Autowired
    private SysEmpPosiDAO o_sysEmpPosiDAO;

    /**
     * 根据员工的Id的Set集合查询员工列表
     * 
     * @author 张健
     * @param idSet 员工的Id的Set集合
     * @return List<SysEmployee>
     * @since Ver 1.1
     */
    public List<SysEmployee> findEmpByIdSet(Set<String> idSet) {
        Criteria criteria = o_sysEmployeeDAO.createCriteria();
        if (null != idSet && idSet.size() > 0) {
            criteria.add(Restrictions.in("id", idSet.toArray()));
        } else {
            criteria.add(Restrictions.isNull("id"));
        }
        return criteria.list();
    }

    /**
     * 机构树查询
     * 
     * @author 张健
     * @param id 树节点ID
     * @param subCompany 是否显示子机构
     * @param orgIds 机构数组
     * @param empIds 员工数组
     * @param query 查询条件
     * @return
     * @since Ver 1.1
     */
    public List<Map<String, Object>> empTreeLoader(String id, Boolean subCompany, String orgIds, String empIds, String query) {
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        // 按根机构搜所有机构
        SysOrganization org = null;
        Criteria cri = o_sysOrgDAO.createCriteria();
        cri.add(Restrictions.eq("id", id));
        org = (SysOrganization) cri.list().get(0);

        // 使用断言验证组织
        Assert.notNull(org, "组织机构数据未初始化，请与管理员联系");

        // 加载公司和部门
        String[] orgType = new String[] {
                "0orgtype_d", "0orgtype_sd"
        };
        if (null != subCompany && subCompany) {
            orgType = new String[] {
                    "0orgtype_d", "0orgtype_sd", "0orgtype_sc"
            };
        }
        Set<String> idSet = queryOrgBySearchName(query, org.getId(), subCompany, orgType);
        Set<SysOrganization> subOrgs = org.getSubOrg();
        if (StringUtils.isNotBlank(query) && idSet.size() == 0) {
            return nodes;
        }
        // 判断是否有特殊机构或特殊人员要显示
        Boolean rules = false;// 是否进行规则验证
        Set<String> orgSet = new HashSet<String>();// 存放需要显示的机构的set
        if (StringUtils.isNotBlank(orgIds)) {
            // 机构数组不为空，机构数组和员工数组都不为空的时候，以机构数组为显示内容
            rules = true;
            orgSet.addAll(Arrays.asList(orgIds.split(",")));
        } else if (StringUtils.isNotBlank(empIds)) {
            // 员工数组不为空，需要按员工数组反查所在机构
            rules = true;
            orgSet = queryOrgByEmpIds(empIds);// 根据员工数组获得机构set
        }

        for (SysOrganization subOrg : subOrgs) {
            if (!"admin".equals(UserContext.getUsername()) && !idSet.contains(subOrg.getId())) {
                continue;
            }
            if (rules && !orgSet.contains(subOrg.getId())) {
                continue;
            }
            Map<String, Object> node = new HashMap<String, Object>();
            node.put("id", subOrg.getId() + "_" + RandomUtils.nextInt(9999));
            node.put("dbid", subOrg.getId());
            node.put("text", subOrg.getOrgname());
            node.put("leaf", subOrg.getIsLeaf());
            String orgicon = ("0orgtype_c".equals(subOrg.getOrgType()) || "0orgtype_sc".equals(subOrg.getOrgType())) ? "icon-org" : "icon-orgsub";
            node.put("iconCls", orgicon);
            node.put("cls", "org");
            nodes.add(node);
        }
        return nodes;
    }

    /**
     * 模糊匹配机构名称
     * 
     * @author 张健
     * @param searchName query关键字
     * @param parentId 父ID
     * @param subCompany 是否包含子公司
     * @param orgType 机构类型
     * @return
     * @since Ver 1.1
     */
    protected Set<String> queryOrgBySearchName(String searchName, String parentId, Boolean subCompany, String[] orgType) {
        List<SysOrganization> list = new ArrayList<SysOrganization>();
        Set<String> idSet = new HashSet<String>();
        Criteria criteria = o_sysOrgDAO.createCriteria();
        criteria.setCacheable(true);
        if (StringUtils.isNotBlank(searchName)) {
            criteria.add(Restrictions.like("orgname", searchName, MatchMode.ANYWHERE));
        }
        criteria.add(Restrictions.eq("parentOrg.id", parentId));
        if (null != subCompany && !subCompany) {
            criteria.add(Restrictions.in("orgType", orgType));
        }
        list = criteria.list();
        for (SysOrganization org : list) {
            String[] idsTemp = org.getOrgseq().split("\\.");
            idSet.addAll(Arrays.asList(idsTemp));
        }
        return idSet;
    }

    /**
     * 根据员工数组，获得机构数组
     * 
     * @author 张健
     * @param empIds 员工数组
     * @return
     * @since Ver 1.1
     */
    protected Set<String> queryOrgByEmpIds(String empIds) {
        Set<String> orgSet = new HashSet<String>();
        Criteria criteria = o_sysEmployeeDAO.createCriteria();
        criteria.add(Restrictions.in("id", empIds.split(",")));
        List<SysEmployee> list = criteria.list();
        for (SysEmployee sys : list) {
            for (SysEmpOrg seo : sys.getSysEmpOrgs()) {
                orgSet.add(seo.getSysOrganization().getId());
            }
        }
        return orgSet;
    }

    /**
     * 角色树查询，模糊匹配
     * 
     * @author 张健
     * @param node 当前节点
     * @param query查询条件
     * @param roleIds 角色数组
     * @return
     * @since Ver 1.1
     */
    public List<Map<String, Object>> roleTreeLoader(String id, String query, String roleIds, String empIds) {
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        Criteria criteria = o_sysRoleDAO.createCriteria();
        List<SysRole> sysRoles = new ArrayList<SysRole>();
        if (StringUtils.isNotBlank(roleIds)) {
            // 身份数组不为空
            criteria.add(Restrictions.in("id", roleIds.split(",")));
        } else if (StringUtils.isNotBlank(empIds)) {
            // 员工数组不为空，需要按员工反查身份
            Set<String> roleSet = queryRoleByEmpIds(empIds);
            criteria.add(Restrictions.in("id", roleSet));
        }
        sysRoles = criteria.list();

        for (SysRole sysRole : sysRoles) {
            Map<String, Object> node = new HashMap<String, Object>();
            node.put("id", sysRole.getId() + "_" + RandomUtils.nextInt(9999));
            node.put("dbid", sysRole.getId());
            node.put("text", sysRole.getRoleName());
            node.put("leaf", true);
            node.put("iconCls", "");
            node.put("cls", "role");
            if (StringUtils.isNotBlank(query)) {
                if (sysRole.getRoleName().contains(query)) {
                    nodes.add(node);
                }
            } else {
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * 查询当前机构下包括子机构和下属岗位下所有的员工,会有员工数组或角色数组做限制条件
     * 
     * @author 张健
     * @param orgid 选中机构号
     * @param empids 员工数组
     * @param roleids 角色数组
     * @param employee 模糊查询数据项
     * @return
     * @since fhd　Ver 1.1
     */
    public Set<SysEmployee> findEmployeeByOrgId(String orgId, String empIds, String roleIds, SysEmployee employee) {
        Set<SysEmployee> allEmpSet = new HashSet<SysEmployee>();
        Criteria criteria = o_sysEmpOrgDAO.createCriteria();
        criteria.createCriteria("sysOrganization", "o");
        criteria.add(Restrictions.like("o.orgseq", orgId, MatchMode.ANYWHERE));//部门匹配
        criteria.createCriteria("sysEmployee", "see");
        if (StringUtils.isNotBlank(employee.getEmpname())) {
            criteria.add(Restrictions.or(Restrictions.like("see.empcode", employee.getEmpname(), MatchMode.ANYWHERE),
                    Restrictions.like("see.empname", employee.getEmpname(), MatchMode.ANYWHERE)));
        }
        // 入参有员工数组，则进行员工号匹配
        if (null != empIds && StringUtils.isNotBlank(empIds)) {
            criteria.add(Restrictions.in("see.id", empIds.split(",")));
        }
        // 入参有角色数组，进行角色匹配
        if (StringUtils.isNotBlank(roleIds)) {
            criteria.createCriteria("see.sysUser", "u");
            criteria.createCriteria("u.sysRoles", "r");
            criteria.add(Restrictions.in("r.id", roleIds.split(",")));
        }
        List<SysEmpOrg> empSEOList = criteria.list();

        Criteria criteria2 = o_sysEmpPosiDAO.createCriteria();
        criteria2.createCriteria("sysPosition", "sp");
        criteria2.createCriteria("sp.sysOrganization", "o");
        criteria2.add(Restrictions.like("o.orgseq", orgId, MatchMode.ANYWHERE));//部门匹配
        criteria2.createCriteria("sysEmployee", "see");
        if (StringUtils.isNotBlank(employee.getEmpname())) {
            criteria2.add(Restrictions.or(Restrictions.like("see.empcode", employee.getEmpname(), MatchMode.ANYWHERE),
                    Restrictions.like("see.empname", employee.getEmpname(), MatchMode.ANYWHERE)));
        }
        // 入参有员工数组，则进行员工号匹配
        if (null != empIds && StringUtils.isNotBlank(empIds)) {
            criteria2.add(Restrictions.in("see.id", empIds.split(",")));
        }
        // 入参有角色数组，进行角色匹配
        if (StringUtils.isNotBlank(roleIds)) {
            criteria2.createCriteria("see.sysUser", "u");
            criteria2.createCriteria("u.sysRoles", "r");
            criteria2.add(Restrictions.in("r.id", roleIds.split(",")));
        }
        List<SysEmpPosi> empSEPList = criteria2.list();

        List<SysEmployee> seeList = new ArrayList<SysEmployee>();
        for (SysEmpOrg soe : empSEOList) {
            seeList.add(soe.getSysEmployee());
        }
        for (SysEmpPosi sep : empSEPList) {
            seeList.add(sep.getSysEmployee());
        }
        allEmpSet.addAll(seeList);
        return allEmpSet;
    }

    /**
     * 根据角色查询本公司的所有员工
     * 
     * @author 张健
     * @param companyId 当前机构号
     * @param roleId 选中的角色
     * @param empIds 员工数组
     * @param orgIds 机构数组
     * @param employee 模糊查询数据项
     * @return
     * @since Ver 1.1
     */
    public List<SysEmployee> findEmployeeByRoleId(String companyId, String roleId, String empIds, String orgIds, SysEmployee SysEmployee) {
        List<SysEmployee> selectedList = new ArrayList<SysEmployee>();
        Criteria criteria = o_sysEmployeeDAO.createCriteria();
        if (StringUtils.isNotBlank(empIds)) {
            criteria.add(Restrictions.in("id", empIds.split(",")));
        }
        criteria.createCriteria("sysUser", "u");
        criteria.createCriteria("u.sysRoles", "r");
        criteria.add(Restrictions.eq("r.id", roleId));
        criteria.add(Restrictions.eq("sysOrganization.id", companyId));
        if (StringUtils.isNotBlank(SysEmployee.getEmpname())) {
            criteria.add(Restrictions.or(Restrictions.like("empcode", SysEmployee.getEmpname(), MatchMode.ANYWHERE),
                    Restrictions.like("empname", SysEmployee.getEmpname(), MatchMode.ANYWHERE)));
        }
        List<SysEmployee> empList = criteria.list();
        if (StringUtils.isNotBlank(orgIds) && empList != null && empList.size() != 0) {
            Set<String> orgSet = new HashSet<String>();
            orgSet.addAll(Arrays.asList(orgIds.split(",")));
            for (SysEmployee see : empList) {
                for (SysEmpOrg sr : see.getSysEmpOrgs()) {
                    if (orgSet.contains(sr.getSysOrganization().getId())) {
                        selectedList.add(see);
                    }
                }
            }
        } else {
            selectedList.addAll(empList);
        }
        return selectedList;
    }

    /**
     * 根据员工数组，获得角色数组
     * 
     * @author 张健
     * @param empIds 员工数组
     * @return
     * @since Ver 1.1
     */
    protected Set<String> queryRoleByEmpIds(String empIds) {
        Set<SysRole> roleSet = new HashSet<SysRole>();
        Criteria criteria = o_sysEmployeeDAO.createCriteria();
        criteria.add(Restrictions.in("id", empIds.split(",")));
        List<SysEmployee> list = criteria.list();
        for (SysEmployee sys : list) {
            roleSet.addAll(sys.getSysUser().getSysRoles());
        }
        Set<String> roleSetId = new HashSet<String>();
        for (SysRole roleId : roleSet) {
            roleSetId.add(roleId.getId());
        }
        return roleSetId;
    }
}
