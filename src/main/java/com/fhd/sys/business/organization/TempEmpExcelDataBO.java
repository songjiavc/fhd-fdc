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
import com.fhd.dao.sys.organization.TempEmpExcelDataDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.entity.sys.orgstructure.TmpSysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sys.business.dataimport.DataImportBO;
import com.fhd.sys.web.form.file.FileForm;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 人员业务
 * */

@Service
public class TempEmpExcelDataBO {

	@Autowired
	private TempEmpExcelDataDAO o_tempEmpExcelDataDAO;
	@Autowired
	private SysUserDAO o_sysUserDAO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private DataImportBO o_dataImportBO;
	
	/**
	 * 批量存储临时表
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveTempEmpExcelData(final List<TempEmpExcelData> list) {
        this.o_tempEmpExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into temp_emp_exceldata " +
                		" (id,emp_code,emp_name,user_name,ex_row,comment,company_id,company_name) value (?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
               for (TempEmpExcelData tempEmpExcelData : list) {
                	  pst.setString(1, tempEmpExcelData.getId());
                      pst.setString(2, tempEmpExcelData.getEmpCode());
                      pst.setString(3, tempEmpExcelData.getEmpName());
                      pst.setString(4, tempEmpExcelData.getUserName());
                      pst.setString(5, tempEmpExcelData.getExRow());
                      pst.setString(6, tempEmpExcelData.getComment());
                      pst.setString(7, tempEmpExcelData.getCompanyId());
                      pst.setString(8, tempEmpExcelData.getCompanyName());
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量存储人员
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveEmp(final ArrayList<SysEmployee> empList) {
        this.o_tempEmpExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into t_sys_employee " +
                		" (id,org_id,emp_code,user_id,user_name,emp_name,real_name,estatus,delete_status) value (?,?,?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (SysEmployee emp : empList) {
                	  pst.setString(1, emp.getId());
                	  if(emp.getSysOrganization() != null){
                		  pst.setString(2, emp.getSysOrganization().getId());
                	  }else{
                		  pst.setString(2, null);
                	  }
                      pst.setString(3, emp.getEmpcode());
                      pst.setString(4, emp.getUserid());
                      pst.setString(5, emp.getUsername());
                      pst.setString(6, emp.getEmpname());
                      pst.setString(7, emp.getRealname());
                      pst.setString(8, emp.getEmpStatus());
                      pst.setString(9, emp.getDeleteStatus());
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量存储用户
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveUser(final ArrayList<SysUser> userList) {
        this.o_tempEmpExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into t_sys_user (id,user_name,real_name,password,estatus,lock_state,is_enable) value (?,?,?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (SysUser user : userList) {
                	  pst.setString(1, user.getId());
                      pst.setString(2, user.getUsername());
                      pst.setString(3, user.getRealname());
                      pst.setString(4, "96e79218965eb72c92a549dd5a330112");
                      pst.setString(5, user.getUserStatus());
                      pst.setString(6, "0");
                      pst.setString(7, "1");
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 查询临时表
	 * */
	@SuppressWarnings("unchecked")
	public List<TempEmpExcelData> findTempEmp(String query){
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			createCriteria.add(Restrictions.like("empName",query,MatchMode.ANYWHERE));
		}
		List<TempEmpExcelData> list = createCriteria.list();
		return list;
	}
	
	/**
	 * 得到用户包装
	 * */
	public ArrayList<SysUser> getAdjustUser(List<TempEmpExcelData> list){
		ArrayList<SysUser> result = new ArrayList<SysUser>();
		SysUser user = null;
		for (TempEmpExcelData tempEmpExcelData : list) {
			user = new SysUser();
			user.setId(tempEmpExcelData.getUserName());
			user.setUsername(tempEmpExcelData.getUserName());
			user.setRealname(tempEmpExcelData.getEmpName());
			user.setUserStatus("1");
			result.add(user);
		}
		
		return result;
	}
	
	/**
	 * 得到人员包装
	 * */
	public ArrayList<SysEmployee> getAdjustEmp(List<TempEmpExcelData> list){
		ArrayList<SysEmployee> result = new ArrayList<SysEmployee>();
		SysEmployee emp = null;
		SysOrganization company = null;
		for (TempEmpExcelData tempEmpExcelData : list) {
			emp = new SysEmployee();
			company = new SysOrganization();
			company.setId(tempEmpExcelData.getCompanyId());
			company.setOrgname(tempEmpExcelData.getCompanyName());
			emp.setId(Identities.uuid2());
			emp.setSysOrganization(company);
			emp.setEmpcode(tempEmpExcelData.getEmpCode());
			emp.setUserid(tempEmpExcelData.getUserName());
			emp.setUsername(tempEmpExcelData.getUserName());
			emp.setEmpname(tempEmpExcelData.getEmpName());
			emp.setRealname(tempEmpExcelData.getEmpName());
			emp.setEmpStatus("1");
			emp.setDeleteStatus("1");
			result.add(emp);
		}
		
		return result;
	}
	
	/**
	 * 得到所有可用用户(用户名称)
	 * */
	public HashMap<String, SysUser> findUserByUserNameAllMap(){
		HashMap<String, SysUser> map = new HashMap<String, SysUser>();
		List<SysUser> list = this.findUserAll();
		for (SysUser sysUser : list) {
			map.put(sysUser.getUsername(), sysUser);
		}
		
		return map;
	}
	
	/**
	 * 得到所有可用人员（人员编号--人员名称--集团ID）
	 * */
	public HashMap<String, SysEmployee> findEmpByEmpCompanyAllMap(){
		HashMap<String, SysEmployee> map = new HashMap<String, SysEmployee>();
		Criteria createCriteria = o_sysEmployeeDAO.createCriteria();
		createCriteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
		@SuppressWarnings("unchecked")
		List<SysEmployee> list = createCriteria.list();
		for (SysEmployee sysEmployee : list) {
			map.put(sysEmployee.getEmpcode() + "--" + sysEmployee.getEmpname() + "--" + sysEmployee.getSysOrganization().getId(), sysEmployee);
		}
		
		return map;
	}
	
	/**
	 * 得到所有可用人员（人员名称--集团ID）
	 * */
	public HashMap<String, SysEmployee> findEmpByEmpCodeNotOrgAllMap(){
		HashMap<String, SysEmployee> map = new HashMap<String, SysEmployee>();
		Criteria createCriteria = o_sysEmployeeDAO.createCriteria();
		createCriteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
		@SuppressWarnings("unchecked")
		List<SysEmployee> list = createCriteria.list();
		for (SysEmployee sysEmployee : list) {
			map.put(sysEmployee.getEmpcode(), sysEmployee);
		}
		
		return map;
	}
	
	/**
	 * 得到所有可用人员（人员名称--集团ID）
	 * */
	public HashMap<String, SysEmployee> findEmpByEmpCodeAllMap(){
		HashMap<String, SysEmployee> map = new HashMap<String, SysEmployee>();
		Criteria createCriteria = o_sysEmployeeDAO.createCriteria();
		createCriteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
		@SuppressWarnings("unchecked")
		List<SysEmployee> list = createCriteria.list();
		for (SysEmployee sysEmployee : list) {
			if(null != sysEmployee.getSysOrganization()){
				map.put(sysEmployee.getEmpcode(), sysEmployee);
			}
		}
		
		return map;
	}
	
	/**
	 * 临时人员数据(人员编号)
	 * */
	public HashMap<String, TempEmpExcelData> findTempEmpCodeAllMap(){
		HashMap<String, TempEmpExcelData> map = new HashMap<String, TempEmpExcelData>();
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<TempEmpExcelData> list = createCriteria.list();
		for (TempEmpExcelData tempEmpExcelData : list) {
			map.put(tempEmpExcelData.getUserName(), tempEmpExcelData);
		}
		
		return map;
	}
	
	/**
	 * 临时人员数据(登录名)
	 * */
	public HashMap<String, TempEmpExcelData> findTempUserNameAllMap(){
		HashMap<String, TempEmpExcelData> map = new HashMap<String, TempEmpExcelData>();
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<TempEmpExcelData> list = createCriteria.list();
		for (TempEmpExcelData tempEmpExcelData : list) {
			map.put(tempEmpExcelData.getUserName(), tempEmpExcelData);
		}
		
		return map;
	}
	
	/**
	 * 临时人员数据(人员名称)
	 * */
	public HashMap<String, TempEmpExcelData> findTempEmpNameAllMap(){
		HashMap<String, TempEmpExcelData> map = new HashMap<String, TempEmpExcelData>();
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<TempEmpExcelData> list = createCriteria.list();
		for (TempEmpExcelData tempEmpExcelData : list) {
			map.put(tempEmpExcelData.getEmpName(), tempEmpExcelData);
		}
		
		return map;
	}
	
	/**
	 * 临时人员数据(人员编号--人员名称)
	 * */
	public HashMap<String, TempEmpExcelData> findTempEmpAllMap(){
		HashMap<String, TempEmpExcelData> map = new HashMap<String, TempEmpExcelData>();
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<TempEmpExcelData> list = createCriteria.list();
		for (TempEmpExcelData tempEmpExcelData : list) {
			map.put(tempEmpExcelData.getEmpCode() + "--" + tempEmpExcelData.getEmpName(), tempEmpExcelData);
		}
		
		return map;
	}
	
	/**
	 * 临时人员数据
	 * */
	public HashMap<String, Object> findTempEmpByObjAllMap(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		HashMap<String, TempEmpExcelData> tempEmpCompanyAllMap = new HashMap<String, TempEmpExcelData>();//临时人员编号--名称--集团
		HashMap<String, TempEmpExcelData> tempEmpCodeAllMap = new HashMap<String, TempEmpExcelData>();//临时人员编号
		HashMap<String, TempEmpExcelData> tempEmpNameAllMap = new HashMap<String, TempEmpExcelData>();//临时人员名称
		
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<TempEmpExcelData> list = createCriteria.list();
		for (TempEmpExcelData tempEmpExcelData : list) {
			
			tempEmpCompanyAllMap.put(tempEmpExcelData.getEmpCode() + "--" + tempEmpExcelData.getEmpName() + "--" + tempEmpExcelData.getCompanyId(), 
					tempEmpExcelData);
			tempEmpCodeAllMap.put(tempEmpExcelData.getEmpCode(), tempEmpExcelData);
			tempEmpNameAllMap.put(tempEmpExcelData.getEmpName(), tempEmpExcelData);
		}
		
		map.put("tempEmpCompanyAllMap", tempEmpCompanyAllMap);
		map.put("tempEmpCodeAllMap", tempEmpCodeAllMap);
		map.put("tempEmpNameAllMap", tempEmpNameAllMap);
		
		return map;
	}
	
	
	/**
	 * 临时人员数据(人员编号--人员名称--集团)
	 * */
	public HashMap<String, TempEmpExcelData> findTempEmpCompanyAllMap(){
		HashMap<String, TempEmpExcelData> map = new HashMap<String, TempEmpExcelData>();
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<TempEmpExcelData> list = createCriteria.list();
		for (TempEmpExcelData tempEmpExcelData : list) {
			map.put(tempEmpExcelData.getEmpCode() + "--" + tempEmpExcelData.getEmpName() + "--" + tempEmpExcelData.getCompanyId(), tempEmpExcelData);
		}
		
		return map;
	}
	
	/**
	 * 得到临时表所有MAP方式(empNameMap,userNameMap)
	 * */
	public HashMap<String, Object> findTempExAllMap(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, List<TempEmpExcelData>> userNameMap = new HashMap<String, List<TempEmpExcelData>>();
		HashMap<String, List<TempEmpExcelData>> empCodeMap = new HashMap<String, List<TempEmpExcelData>>();
		List<TempEmpExcelData> listData = null;
		Criteria createCriteria = o_tempEmpExcelDataDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<TempEmpExcelData> list = createCriteria.list();
		for (TempEmpExcelData tempEmpExcelData : list) {
			if(empCodeMap.get(tempEmpExcelData.getEmpName()) != null){
				empCodeMap.get(tempEmpExcelData.getEmpName()).add(tempEmpExcelData);
			}else{
				listData = new ArrayList<TempEmpExcelData>();
				listData.add(tempEmpExcelData);
				empCodeMap.put(tempEmpExcelData.getEmpName(), listData);
			}
			
			if(userNameMap.get(tempEmpExcelData.getUserName()) != null){
				userNameMap.get(tempEmpExcelData.getUserName()).add(tempEmpExcelData);
			}else{
				listData = new ArrayList<TempEmpExcelData>();
				listData.add(tempEmpExcelData);
				userNameMap.put(tempEmpExcelData.getEmpCode(), listData);
			}
		}
		map.put("empCodeMap", empCodeMap);
		map.put("userNameMap", userNameMap);
		
		return map;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findTempEmpEx(FileForm form, int exPage, HashMap<String, Object> tempOrgByObjAllMap){
		List<TempEmpExcelData> tempEmpExcelDataList = new ArrayList<TempEmpExcelData>();
		List<List<String>> excelDatas = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emp_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, SysOrganization> orgAllMap = o_dataImportBO.findOrgByNameAllMap();
		HashMap<String, SysEmployee> empByEmpCodeAllMap = this.findEmpByEmpCodeAllMap();
		HashMap<String, SysUser> userByUserNameAllMap = this.findUserByUserNameAllMap();
		HashMap<String, String> userNameMap = new HashMap<String, String>();
		HashMap<String, String> empCodeMap = new HashMap<String, String>();
		HashMap<String, TmpSysOrganization> tempOrgCompanyAllMap = 
				(HashMap<String, TmpSysOrganization>) tempOrgByObjAllMap.get("tempOrgAllMap");//临时数据机构
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				StringBuilder sb = new StringBuilder();//记录错误信息
				
				if(rowDatas.size() != 4){
					continue;
				}
				
                String userName = rowDatas.get(0);
                String empCode = rowDatas.get(1);
                String empName = rowDatas.get(2);
                String companyName = rowDatas.get(3);
                String companyId = "";
    			TempEmpExcelData tempEmpExcelData = new TempEmpExcelData();
    			
    			if(orgAllMap.get(companyName) != null){
					companyId = orgAllMap.get(companyName).getId();
				}else{
					if(null != tempOrgCompanyAllMap.get(companyName)){
						companyId = tempOrgCompanyAllMap.get(companyName).getCompanyName();
					}else{
						sb.append("集团/公司不存在.");
					}
				}
    			
    			if(StringUtils.isBlank(userName)){
    				sb.append("登录名不能为空.");
    			}
    			
				if(userNameMap.get(userName) != null){
					sb.append("登录名相同、只能为一.");
				}
					
				if(userByUserNameAllMap.get(userName) != null){
					sb.append("登录名已存在.");
				}
				
				if(StringUtils.isBlank(empCode)){
    				sb.append("员工编号不能为空.");
    			}
				
				if(empCodeMap.get(empCode) != null){
					sb.append("员工编号相同、只能为一.");
				}
				
				if(empByEmpCodeAllMap.get(empCode) != null){
					sb.append("员工编号已存在.");
				}
				
				if(StringUtils.isBlank(empName)){
					sb.append("员工名称不能为空.");
				}
				
				if(StringUtils.isBlank(companyName)){
    				sb.append("集团不能为空.");
    			}
				
				if(!sb.toString().equalsIgnoreCase("")){
					errorInfo = false;
				}
				
				tempEmpExcelData.setId(Identities.uuid());
				tempEmpExcelData.setEmpName(empName);
				tempEmpExcelData.setEmpCode(empCode);
				tempEmpExcelData.setUserName(userName);
				tempEmpExcelData.setCompanyId(companyId);
				tempEmpExcelData.setCompanyName(companyName);
				tempEmpExcelData.setComment(sb.toString());
				tempEmpExcelData.setExRow(String.valueOf(row + 1));
				
				userNameMap.put(userName, userName);
				empCodeMap.put(empCode, empCode);
				
				tempEmpExcelDataList.add(tempEmpExcelData);
			}
		}
		
		this.saveTempEmpExcelData(tempEmpExcelDataList);
		
		Collections.sort(tempEmpExcelDataList, new Comparator<TempEmpExcelData>() {
			@Override
			public int compare(TempEmpExcelData arg0, TempEmpExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
			
		});
		
		resultMap.put("datas", tempEmpExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findEmpEx(FileForm form, int exPage){
		List<TempEmpExcelData> tempEmpExcelDataList = new ArrayList<TempEmpExcelData>();
		List<List<String>> excelDatas = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emp_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, SysOrganization> orgAllMap = o_dataImportBO.findOrgByNameAllMap();
		HashMap<String, SysEmployee> empByEmpCodeAllMap = this.findEmpByEmpCodeAllMap();
		HashMap<String, SysUser> userByUserNameAllMap = this.findUserByUserNameAllMap();
		HashMap<String, String> userNameMap = new HashMap<String, String>();
		HashMap<String, String> empCodeMap = new HashMap<String, String>();
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				StringBuilder sb = new StringBuilder();//记录错误信息
				
				if(rowDatas.size() != 4){
					continue;
				}
				
                String userName = rowDatas.get(0);
                String empCode = rowDatas.get(1);
                String empName = rowDatas.get(2);
                String companyName = rowDatas.get(3);
                String companyId = "";
    			TempEmpExcelData tempEmpExcelData = new TempEmpExcelData();
    			
    			if(orgAllMap.get(companyName) != null){
					companyId = orgAllMap.get(companyName).getId();
				}else{
					sb.append("集团/公司不存在.");
				}
    			
    			if(StringUtils.isBlank(userName)){
    				sb.append("登录名不能为空.");
    			}
    			
				if(userNameMap.get(userName) != null){
					sb.append("登录名相同、只能为一.");
				}
					
				if(userByUserNameAllMap.get(userName) != null){
					sb.append("登录名已存在.");
				}
				
				if(StringUtils.isBlank(empCode)){
    				sb.append("员工编号不能为空.");
    			}
				
				if(empCodeMap.get(empCode) != null){
					sb.append("员工编号相同、只能为一.");
				}
				
				if(empByEmpCodeAllMap.get(empCode) != null){
					sb.append("员工编号已存在.");
				}
				
				if(StringUtils.isBlank(empName)){
					sb.append("员工名称不能为空.");
				}
				
				if(StringUtils.isBlank(companyName)){
    				sb.append("集团不能为空.");
    			}
				
				if(!sb.toString().equalsIgnoreCase("")){
					errorInfo = false;
				}
				
				tempEmpExcelData.setId(Identities.uuid());
				tempEmpExcelData.setEmpName(empName);
				tempEmpExcelData.setEmpCode(empCode);
				tempEmpExcelData.setUserName(userName);
				tempEmpExcelData.setCompanyId(companyId);
				tempEmpExcelData.setCompanyName(companyName);
				tempEmpExcelData.setComment(sb.toString());
				tempEmpExcelData.setExRow(String.valueOf(row + 1));
				
				userNameMap.put(userName, userName);
				empCodeMap.put(empCode, empCode);
				
				tempEmpExcelDataList.add(tempEmpExcelData);
			}
		}
		
		this.saveTempEmpExcelData(tempEmpExcelDataList);
		
		Collections.sort(tempEmpExcelDataList, new Comparator<TempEmpExcelData>() {
			@Override
			public int compare(TempEmpExcelData arg0, TempEmpExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
			
		});
		
		resultMap.put("datas", tempEmpExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	
	/***
	 * 查询所有可用用户
	 * */
	@SuppressWarnings({"unchecked" })
	public List<SysUser> findUserAll() {
		Criteria createCriteria = o_sysUserDAO.createCriteria();
		createCriteria.add(Restrictions.eq("userStatus", Contents.STATUS_NORMAL));
		return createCriteria.list();
	}
	
	/**
	 * 导入员工临时表查看
	 * add by 王再冉
	 * 2014-4-30  上午9:47:24
	 * desc : 
	 * @param query
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findAllTmpEmpExcelDatas(String query){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> empList = new ArrayList<Map<String,Object>>();
		Boolean errorInfo = true;
		List<TempEmpExcelData> tmpEmpList = this.findTempEmp(query);
		for(TempEmpExcelData tmpEmp : tmpEmpList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", tmpEmp.getId());
			map.put("exRow", tmpEmp.getExRow());
			map.put("userName", tmpEmp.getUserName());
			map.put("empCode", tmpEmp.getEmpCode());
			map.put("empName", tmpEmp.getEmpName());
			map.put("companyName", tmpEmp.getCompanyName());
			map.put("comment", tmpEmp.getComment());
			if(StringUtils.isNotBlank(tmpEmp.getComment())){
				errorInfo = false;
			}
			empList.add(map);
		}
		resultMap.put("errorInfo", errorInfo);//错误信息
		resultMap.put("datas", empList);//搜索框结果集（只能显示‘datas中的数据’）
		return resultMap;
	}
	
	/**
	 * 清空人员临时表数据
	 * add by 王再冉
	 * 2014-4-30  下午1:35:31
	 * desc :  
	 * void
	 */
	public void deleteTmpEmpDatas(){
		String sql = "delete from temp_emp_exceldata";
		SQLQuery sqlQuery = o_tempEmpExcelDataDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
	}
}