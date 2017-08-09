package com.fhd.sys.business.organization;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.auth.SysUserDAO;
import com.fhd.dao.sys.autho.SysoRoleDAO;
import com.fhd.dao.sys.organization.TempEmpRoleExcelDataDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpRoleExcelData;
import com.fhd.entity.sys.orgstructure.TmpSysRole;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sys.business.orgstructure.TmpImpRoleBO;
import com.fhd.sys.web.form.file.FileForm;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 人员关联角色业务
 * */

@Service
public class TempEmpRoleExcelDataBO {
	@Autowired
	private SysUserDAO o_sysUserDAO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private TempEmpExcelDataBO o_tempEmpExcelDataBO;
	@Autowired
	private SysoRoleDAO o_roleDAO;
	@Autowired
	private TmpImpRoleBO o_tmpImpRoleBO;
	@Autowired
	private TempEmpRoleExcelDataDAO o_tempEmpRoleExcelDataDAO;
	
	/**
	 * 批量存储人角色构临时数据
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveTempEmpRoleExcelData(final List<TempEmpRoleExcelData> list) {
        this.o_tempEmpRoleExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into temp_emprole_exceldata (id,role_id,role_name,ex_row,comment,user_Id) " +
                		" values(?,?,?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (TempEmpRoleExcelData tempEmpRoleExcelData : list) {
                	pst.setString(1, tempEmpRoleExcelData.getId());
                	pst.setString(2, tempEmpRoleExcelData.getRoleId());
                	pst.setString(3, tempEmpRoleExcelData.getRoleName());
                	pst.setString(4, tempEmpRoleExcelData.getExRow());
                	pst.setString(5, tempEmpRoleExcelData.getComment());
                	pst.setString(6, tempEmpRoleExcelData.getUserid());
                	pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量存储人角色构关联
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveEmpOrg(final List<String> strList) {
        this.o_tempEmpRoleExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_sys_user_role (user_id, role_id) values(?,?)";
                pst = connection.prepareStatement(sql);
                
                for (String string : strList) {
                	 String strs[] = string.split("--");
                	 pst.setString(1, strs[0]);
                     pst.setString(2, strs[1]);
                     pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 包装人员角色关联
	 * */
	public List<String> getEmpRole(List<TempEmpRoleExcelData> list){
		List<String> lists = new ArrayList<String>();
		for (TempEmpRoleExcelData tempEmpRoleExcelData : list) {
			if(tempEmpRoleExcelData.getRoleId().indexOf(",") != -1){
				String str[] = tempEmpRoleExcelData.getRoleId().split(",");
				for (String string : str) {
					lists.add(tempEmpRoleExcelData.getUserid() + "--" + string);
				}
			}else{
				lists.add(tempEmpRoleExcelData.getUserid() + "--" + tempEmpRoleExcelData.getRoleId());
			}
		}
		
		return lists;
	}
	
	/**
	 * 查询临时数据
	 * */
	public List<TempEmpRoleExcelData> findTempEmp(String query){
		Criteria createCriteria = o_tempEmpRoleExcelDataDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			createCriteria.add(Restrictions.or(Restrictions.like("roleName",query,MatchMode.ANYWHERE),
					Restrictions.like("userid",query,MatchMode.ANYWHERE)));
		}
		@SuppressWarnings("unchecked")
		List<TempEmpRoleExcelData> list = createCriteria.list();
		return list;
	}
	
	/**
	 * 得到所有可用人员（人员名称--人员名称--集团ID）
	 * */
	public HashMap<String, SysEmployee> findEmpByEmpCodeAllMap(){
		HashMap<String, SysEmployee> map = new HashMap<String, SysEmployee>();
		Criteria createCriteria = o_sysEmployeeDAO.createCriteria();
		createCriteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
		@SuppressWarnings("unchecked")
		List<SysEmployee> list = createCriteria.list();
		for (SysEmployee sysEmployee : list) {
			if(null != sysEmployee.getSysOrganization()){
				map.put(sysEmployee.getEmpcode() + "--" + "--" + sysEmployee.getEmpname(), sysEmployee);
			}
		}
		
		return map;
	}
	
	/**
	 * 查询系统中角色数据(角色名称)
	 * */
	public HashMap<String, SysRole> findRoleAllMap(){
		HashMap<String, SysRole> map = new HashMap<String, SysRole>();
		
		Criteria createCriteria = o_roleDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<SysRole> list = createCriteria.list();
		for (SysRole sysRole : list) {
			map.put(sysRole.getRoleName(), sysRole);
		}
		
		return map;
	}
	
	/**
	 * 查询临时角色数据(角色名称)
	 * */
	public HashMap<String, TmpSysRole> findTempRoleAllMap(){
		HashMap<String, TmpSysRole> map = new HashMap<String, TmpSysRole>();
		List<TmpSysRole> list = o_tmpImpRoleBO.findAllTmpSysRolesSQL(null);
		for (TmpSysRole tmpSysRole : list) {
			map.put(tmpSysRole.getRoleName(), tmpSysRole);
		}
		
		return map;
	}
	
	/**
	 * 全部
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findTempEmpRoleEx(FileForm form, int exPage){
		List<TempEmpRoleExcelData> tempEmpRoleExcelDataList = new ArrayList<TempEmpRoleExcelData>();
		List<List<String>> excelDatas = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emprole_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, SysRole> roleAllMap = this.findRoleAllMap();
		HashMap<String, TmpSysRole> tempRoleAllMap = this.findTempRoleAllMap();
		HashMap<String, SysUser> userByUserNameAllMap = o_tempEmpExcelDataBO.findUserByUserNameAllMap();
		HashMap<String, TempEmpExcelData> tempUserNameAllMap = o_tempEmpExcelDataBO.findTempEmpCodeAllMap();
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				
				if(rowDatas.size() != 2){
					continue;
				}
				
				StringBuilder sb = new StringBuilder();//记录错误信息
				
                String userId = rowDatas.get(0);
                String roleName = rowDatas.get(1);
                String roleId = "";
    			TempEmpRoleExcelData tempEmpRoleExcelData = new TempEmpRoleExcelData();
    			
				if(StringUtils.isBlank(roleName)){
					sb.append("所属角色不能为空.");
				}else{
					if(roleName.indexOf(",") != -1){
						String strs[] = roleName.split(",");
						for (String string : strs) {
							if(roleAllMap.get(string) == null){
								if(tempRoleAllMap.get(string) == null){
									sb.append(string + "所属角色不存在.");
								}else{
									roleId += tempRoleAllMap.get(string).getRoleCode() + ",";
								}
							}else{
								roleId += roleAllMap.get(string).getId() + ",";
							}
						}
					}else{
						if(roleAllMap.get(roleName) == null){
							if(tempRoleAllMap.get(roleName) == null){
								sb.append("所属角色不存在.");
							}else{
								roleId = tempRoleAllMap.get(roleName).getRoleCode();
							}
						}else{
							roleId = roleAllMap.get(roleName).getId();
						}
					}
				}
    			
				if(StringUtils.isBlank(userId)){
    				sb.append("登录名不能为空.");
    			}
				
				if(StringUtils.isNotBlank(userId)){
					if(userByUserNameAllMap.get(userId) == null){
						if(tempUserNameAllMap.get(userId) == null){
							sb.append("登录名不存在.");
						}
					}
				}
				
				if(!sb.toString().equalsIgnoreCase("")){
					errorInfo = false;
				}
				
				if(userByUserNameAllMap.get(userId) != null){
					for (SysRole string : userByUserNameAllMap.get(userId).getSysRoles()) {
						if(StringUtils.isNotBlank(roleId)){
							if(roleId.indexOf(string.getId()) != -1){
								sb.append(string.getRoleName() + "关联已存在.");
							}
						}
					}
				}
				
				tempEmpRoleExcelData.setId(Identities.uuid());
				tempEmpRoleExcelData.setUserid(userId);
				tempEmpRoleExcelData.setRoleName(roleName);
				tempEmpRoleExcelData.setRoleId(roleId);
				tempEmpRoleExcelData.setComment(sb.toString());
				tempEmpRoleExcelData.setExRow(String.valueOf(row + 1));
				tempEmpRoleExcelDataList.add(tempEmpRoleExcelData);
			}
		}
		
		this.saveTempEmpRoleExcelData(tempEmpRoleExcelDataList);
		
		Collections.sort(tempEmpRoleExcelDataList, new Comparator<TempEmpRoleExcelData>() {
			@Override
			public int compare(TempEmpRoleExcelData arg0, TempEmpRoleExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
			
		});
		
		resultMap.put("datas", tempEmpRoleExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findEmpRoleEx(FileForm form, int exPage){
		List<TempEmpRoleExcelData> tempEmpRoleExcelDataList = new ArrayList<TempEmpRoleExcelData>();
		List<List<String>> excelDatas = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emprole_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, SysRole> roleAllMap = this.findRoleAllMap();
		HashMap<String, SysUser> userByUserNameAllMap = o_tempEmpExcelDataBO.findUserByUserNameAllMap();
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				
				if(rowDatas.size() != 2){
					continue;
				}
				
				StringBuilder sb = new StringBuilder();//记录错误信息
				
                String userId = rowDatas.get(0);
                String roleName = rowDatas.get(1);
                String roleId = "";
    			TempEmpRoleExcelData tempEmpRoleExcelData = new TempEmpRoleExcelData();
    			
				if(StringUtils.isBlank(roleName)){
					sb.append("所属角色不能为空.");
				}else{
					if(roleName.indexOf(",") != -1){
						String strs[] = roleName.split(",");
						for (String string : strs) {
							if(roleAllMap.get(string) == null){
								sb.append(string + "所属角色不存在.");
							}else{
								roleId += roleAllMap.get(string).getId() + ",";
							}
						}
					}else{
						if(roleAllMap.get(roleName) == null){
							sb.append("所属角色不存在.");
						}else{
							roleId = roleAllMap.get(roleName).getId();
						}
					}
				}
    			
				if(StringUtils.isBlank(userId)){
    				sb.append("登录名不能为空.");
    			}
				
				if(StringUtils.isNotBlank(userId)){
					if(userByUserNameAllMap.get(userId) == null){
						sb.append("登录名不存在.");
					}
				}
				
				if(!sb.toString().equalsIgnoreCase("")){
					errorInfo = false;
				}
				
				if(userByUserNameAllMap.get(userId) != null){
					for (SysRole string : userByUserNameAllMap.get(userId).getSysRoles()) {
						if(StringUtils.isNotBlank(roleId)){
							if(roleId.indexOf(string.getId()) != -1){
								sb.append(string.getRoleName() + "关联已存在.");
							}
						}
					}
				}
				
				tempEmpRoleExcelData.setId(Identities.uuid());
				tempEmpRoleExcelData.setUserid(userId);
				tempEmpRoleExcelData.setRoleName(roleName);
				tempEmpRoleExcelData.setRoleId(roleId);
				tempEmpRoleExcelData.setComment(sb.toString());
				tempEmpRoleExcelData.setExRow(String.valueOf(row + 1));
				tempEmpRoleExcelDataList.add(tempEmpRoleExcelData);
			}
		}
		
		this.saveTempEmpRoleExcelData(tempEmpRoleExcelDataList);
		
		Collections.sort(tempEmpRoleExcelDataList, new Comparator<TempEmpRoleExcelData>() {
			@Override
			public int compare(TempEmpRoleExcelData arg0, TempEmpRoleExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
			
		});
		
		resultMap.put("datas", tempEmpRoleExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	/**
	 * 查询人员角色关联临时表
	 * add by 王再冉
	 * 2014-4-30  上午10:31:40
	 * desc : 
	 * @param query
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findAllTmpEmpRolesExcelDatas(String query){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> empList = new ArrayList<Map<String,Object>>();
		Boolean errorInfo = true;
		List<TempEmpRoleExcelData> tmpEmpRoleList = this.findTempEmp(query);
		for(TempEmpRoleExcelData tmpEmpRole : tmpEmpRoleList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", tmpEmpRole.getId());
			map.put("exRow", tmpEmpRole.getExRow());
			map.put("userid", tmpEmpRole.getUserid());
			map.put("roleName", tmpEmpRole.getRoleName());
			map.put("comment", tmpEmpRole.getComment());
			if(StringUtils.isNotBlank(tmpEmpRole.getComment())){
				errorInfo = false;
			}
			empList.add(map);
		}
		resultMap.put("errorInfo", errorInfo);//错误信息
		resultMap.put("datas", empList);//搜索框结果集（只能显示‘datas中的数据’）
		return resultMap;
	}
	/**
	 * 清空人员角色临时表数据
	 * add by 王再冉
	 * 2014-4-30  下午1:37:47
	 * desc :  
	 * void
	 */
	public void deleteEmpRoleDatas(){
		String sql = "delete from temp_emprole_exceldata";
		SQLQuery sqlQuery = o_tempEmpRoleExcelDataDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
	}
}