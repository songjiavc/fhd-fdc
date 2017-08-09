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
import com.fhd.dao.sys.organization.TempEmpOrgExcelDataDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.dao.sys.orgstructure.SysEmpOrgDAO;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpOrgExcelData;
import com.fhd.entity.sys.orgstructure.TmpSysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.ra.business.assess.oper.SysEmployeeBO;
import com.fhd.sys.business.dataimport.DataImportBO;
import com.fhd.sys.business.orgstructure.TmpImpOrganizationBO;
import com.fhd.sys.web.form.file.FileForm;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 人员关联机构业务
 * */

@Service
public class TempEmpOrgExcelDataBO {
	@Autowired
	private SysUserDAO o_sysUserDAO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private DataImportBO o_dataImportBO;
	@Autowired
	private TempEmpExcelDataBO o_tempEmpExcelDataBO;
	@Autowired
	private TmpImpOrganizationBO o_tmpImpOrganizationBO;
	@Autowired
	private TempEmpOrgExcelDataDAO o_tempEmpOrgExcelDataDAO;
	@Autowired
	private SysEmployeeBO o_SysEmployeeBO;
	@Autowired
	private SysEmpOrgDAO o_sysEmpOrgDAO;
	
	/**
	 * 批量存储人员机构临时数据
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveTempEmpOrgExcelData(final List<TempEmpOrgExcelData> list) {
        this.o_tempEmpOrgExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into temp_emporg_exceldata " +
                		" (id,emp_code,emp_name,org_id,org_name,ex_row,comment,company_id,company_name) values(?,?,?,?,?,?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (TempEmpOrgExcelData tempEmpOrgExcelData : list) {
                	pst.setString(1, tempEmpOrgExcelData.getId());
                    pst.setString(2, tempEmpOrgExcelData.getEmpCode());
                    pst.setString(3, tempEmpOrgExcelData.getEmpName());
                    pst.setString(4, tempEmpOrgExcelData.getOrgId());
                    pst.setString(5, tempEmpOrgExcelData.getOrgName());
                    pst.setString(6, tempEmpOrgExcelData.getExRow());
                    pst.setString(7, tempEmpOrgExcelData.getComment());
                    pst.setString(8, tempEmpOrgExcelData.getCompanyId());
                    pst.setString(9, tempEmpOrgExcelData.getCompanyName());
                    pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量存储人员机构关联
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveEmpOrg(final ArrayList<SysEmpOrg> empOrgList) {
        this.o_tempEmpOrgExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into t_sys_emp_org (id,emp_id,org_id,ismain) values(?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (SysEmpOrg empOrg : empOrgList) {
                	  pst.setString(1, empOrg.getId());
                      pst.setString(2, empOrg.getSysEmployee().getId());
                      pst.setString(3, empOrg.getSysOrganization().getId());
                      pst.setString(4, "1");
                      pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量编辑人员机构关联
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void editEmpOrg(final ArrayList<SysEmpOrg> empOrgList) {
        this.o_tempEmpOrgExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "update t_sys_emp_org set org_id=? where emp_id=?";
                pst = connection.prepareStatement(sql);
                
                for (SysEmpOrg empOrg : empOrgList) {
                  pst.setString(0, empOrg.getSysOrganization().getId());
                  pst.setString(1, empOrg.getSysEmployee().getId());
                  pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 查询人员机构关联
	 * */
	public HashMap<String, SysEmpOrg> findEmpOrgAllMap(){
		HashMap<String, SysEmpOrg> map = new HashMap<String, SysEmpOrg>();
		@SuppressWarnings("deprecation")
		Criteria createCriteria = o_sysEmpOrgDAO.createCriteria();
		@SuppressWarnings("unchecked")
		List<SysEmpOrg> list = createCriteria.list();
		for (SysEmpOrg sysEmpOrg : list) {
			try {
				if(sysEmpOrg.getSysEmployee() != null && sysEmpOrg.getSysOrganization() != null){
					map.put(sysEmpOrg.getSysEmployee().getEmpcode() + "--" + sysEmpOrg.getSysOrganization().getId(), sysEmpOrg);
				}
			} catch (Exception e) {
			}
		}
		
		return map;
	}
	
	/**
	 * 得到编辑人员机构包装(系统添加)
	 * */
	public ArrayList<SysEmpOrg> getTempAdjustUser(List<TempEmpOrgExcelData> list){
		ArrayList<SysEmpOrg> result = new ArrayList<SysEmpOrg>();
		SysEmpOrg empOrg = null;
		SysEmployee emp = null;
		SysOrganization org = null;
		Map<String, SysEmployee> empMap = o_SysEmployeeBO.findSysEmployeeByCodeMapAll();
		for (TempEmpOrgExcelData tempEmpOrgExcelData : list) {
			empOrg = new SysEmpOrg();
			empOrg.setId(Identities.uuid2());
			emp = new SysEmployee();
			if(empMap.get(tempEmpOrgExcelData.getEmpCode()) != null){
				emp = empMap.get(tempEmpOrgExcelData.getEmpCode());
			}
			empOrg.setSysEmployee(emp);
			
			if(StringUtils.isNotBlank(tempEmpOrgExcelData.getOrgId())){
				org = new SysOrganization();
				org.setId(tempEmpOrgExcelData.getOrgId());
				empOrg.setSysOrganization(org);
			}
			result.add(empOrg);
		}
		
		return result;
	}
	
	/**
	 * 得到添加人员机构包装(临时添加)
	 * */
	public ArrayList<SysEmpOrg> getAdjustUser(List<TempEmpOrgExcelData> list){
		ArrayList<SysEmpOrg> result = new ArrayList<SysEmpOrg>();
		SysEmpOrg empOrg = null;
		SysEmployee emp = null;
		SysOrganization org = null;
		HashMap<String, SysEmployee> empMap = o_tempEmpExcelDataBO.findEmpByEmpCodeNotOrgAllMap();
		for (TempEmpOrgExcelData tempEmpOrgExcelData : list) {
			
			if(tempEmpOrgExcelData.getOrgId().indexOf(",") != -1){
				String strs[] = tempEmpOrgExcelData.getOrgId().split(",");
				for (String string : strs) {
					empOrg = new SysEmpOrg();
					empOrg.setId(Identities.uuid2());
					emp = new SysEmployee();
					if(empMap.get(tempEmpOrgExcelData.getEmpCode()) != null){
						emp = empMap.get(tempEmpOrgExcelData.getEmpCode());
					}
					empOrg.setSysEmployee(emp);
					
					if(StringUtils.isNotBlank(tempEmpOrgExcelData.getOrgId())){
						org = new SysOrganization();
						org.setId(string);
						empOrg.setSysOrganization(org);
					}
					result.add(empOrg);
				}
			}else{
				empOrg = new SysEmpOrg();
				empOrg.setId(Identities.uuid2());
				emp = new SysEmployee();
				if(empMap.get(tempEmpOrgExcelData.getEmpCode()) != null){
					emp = empMap.get(tempEmpOrgExcelData.getEmpCode());
				}
				empOrg.setSysEmployee(emp);
				
				if(StringUtils.isNotBlank(tempEmpOrgExcelData.getOrgId())){
					org = new SysOrganization();
					org.setId(tempEmpOrgExcelData.getOrgId());
					empOrg.setSysOrganization(org);
				}
				result.add(empOrg);
			}
		}
		
		return result;
	}
	
	/**
	 * 查询临时数据
	 * */
	public List<TempEmpOrgExcelData> findTempEmp(String query){
		Criteria createCriteria = o_tempEmpOrgExcelDataDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			createCriteria.add(Restrictions.or(Restrictions.like("empName",query,MatchMode.ANYWHERE),
					Restrictions.like("orgName",query,MatchMode.ANYWHERE)));
		}
		@SuppressWarnings("unchecked")
		List<TempEmpOrgExcelData> list = createCriteria.list();
		return list;
	}
	
	/**
	 * 得到所有可用人员（人员名称--人员名称--集团ID）
	 * */
	public HashMap<String, Object> findEmpByEmpCodeAllMap(){
		HashMap<String, Object> maps = new HashMap<String, Object>();
		HashMap<String, SysEmployee> empCodeMap = new HashMap<String, SysEmployee>();
		HashMap<String, SysEmployee> empNameMap = new HashMap<String, SysEmployee>();
		HashMap<String, SysEmployee> map = new HashMap<String, SysEmployee>();
		
		Criteria createCriteria = o_sysEmployeeDAO.createCriteria();
		createCriteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
		@SuppressWarnings("unchecked")
		List<SysEmployee> list = createCriteria.list();
		for (SysEmployee sysEmployee : list) {
			if(null != sysEmployee.getSysOrganization()){
				map.put(sysEmployee.getEmpcode() + "--" + sysEmployee.getEmpname() + "--" + sysEmployee.getSysOrganization().getId(), sysEmployee);
				
			}else{
				map.put(sysEmployee.getEmpcode() + "--" + sysEmployee.getEmpname() + "--" + "", sysEmployee);
			}
			
			empCodeMap.put(sysEmployee.getEmpcode(), sysEmployee);
			empNameMap.put(sysEmployee.getEmpname(), sysEmployee);
		}
		
		maps.put("empCodeMap", empCodeMap);
		maps.put("empNameMap", empNameMap);
		maps.put("map", map);
		
		return maps;
	}
	
	/**
	 * 集合
	 * */
	public HashMap<String, Object> findTempOrgByObjAllMap(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, TmpSysOrganization> tempOrgCompanyAllMap = new HashMap<String, TmpSysOrganization>();
		HashMap<String, TmpSysOrganization> tempOrgAllMap = new HashMap<String, TmpSysOrganization>();
		
		List<TmpSysOrganization> list = o_tmpImpOrganizationBO.findAllTmpSysOrgs(null);
		for (TmpSysOrganization tmpSysOrganization : list) {
			tempOrgCompanyAllMap.put(tmpSysOrganization.getOrgName() + "--" + tmpSysOrganization.getCompanyName(), tmpSysOrganization);
			tempOrgAllMap.put(tmpSysOrganization.getOrgName(), tmpSysOrganization);
		}
		
		map.put("tempOrgCompanyAllMap", tempOrgCompanyAllMap);
		map.put("tempOrgAllMap", tempOrgAllMap);
		
		return map;
	}
	
//	/**
//	 * 临时机构数据(机构名称)
//	 * */
//	public HashMap<String, TmpSysOrganization> findTempOrgAllMap(){
//		HashMap<String, TmpSysOrganization> map = new HashMap<String, TmpSysOrganization>();
//		
//		List<TmpSysOrganization> list = o_tmpImpOrganizationBO.findAllTmpSysOrgsSQL();
//		for (TmpSysOrganization tmpSysOrganization : list) {
//			map.put(tmpSysOrganization.getOrgName() + "--" + tmpSysOrganization.getCompanyName(), tmpSysOrganization);
//		}
//		
//		return map;
//	}
//	
//	/**
//	 * 临时机构数据(机构名称)
//	 * */
//	public HashMap<String, TmpSysOrganization> findTempOrgCompanyAllMap(){
//		HashMap<String, TmpSysOrganization> map = new HashMap<String, TmpSysOrganization>();
//		
//		List<TmpSysOrganization> list = o_tmpImpOrganizationBO.findAllTmpSysOrgsSQL();
//		for (TmpSysOrganization tmpSysOrganization : list) {
//			map.put(tmpSysOrganization.getOrgName(), tmpSysOrganization);
//		}
//		
//		return map;
//	}
	
	/**
	 * 没写完 需要继续写
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findTempEmpOrgEx(FileForm form, int exPage, HashMap<String, Object> TempEmpObjeAllMap,
			HashMap<String, SysEmployee> empByEmpCompanyAllMap, HashMap<String, Object> tempOrgByObjAllMap){
		List<TempEmpOrgExcelData> tempEmpExcelDataList = new ArrayList<TempEmpOrgExcelData>();
		List<List<String>> excelDatas = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emporg_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, Object> maps = this.findEmpByEmpCodeAllMap();
		HashMap<String, SysEmployee> empCodeMap = (HashMap<String, SysEmployee>) maps.get("empCodeMap");
		HashMap<String, SysEmployee> empNameMap = (HashMap<String, SysEmployee>) maps.get("empNameMap");
		HashMap<String, SysOrganization> orgAllMap = o_dataImportBO.findOrgByNameAllMap();
		HashMap<String, SysOrganization> orgByNameAndCompanyIdAllMap = o_dataImportBO.findOrgByNameAndCompanyIdAllMap();
		HashMap<String, SysEmpOrg> empOrgAllMap = this.findEmpOrgAllMap();
		
		HashMap<String, TmpSysOrganization> tempOrgAllMap = 
				(HashMap<String, TmpSysOrganization>) tempOrgByObjAllMap.get("tempOrgCompanyAllMap");//临时数据机构
		HashMap<String, TmpSysOrganization> tempOrgCompanyAllMap = 
				(HashMap<String, TmpSysOrganization>) tempOrgByObjAllMap.get("tempOrgAllMap");//临时数据机构
		
		//临时人员编号--名称--集团
		HashMap<String, TempEmpExcelData> tempEmpCompanyAllMap = (HashMap<String, TempEmpExcelData>) TempEmpObjeAllMap.get("tempEmpCompanyAllMap");
		//临时人员编号
		HashMap<String, TempEmpExcelData> tempEmpCodeAllMap = (HashMap<String, TempEmpExcelData>) TempEmpObjeAllMap.get("tempEmpCodeAllMap");
		//临时人员名称
		HashMap<String, TempEmpExcelData> tempEmpNameAllMap = (HashMap<String, TempEmpExcelData>) TempEmpObjeAllMap.get("tempEmpNameAllMap");
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				StringBuilder sb = new StringBuilder();//记录错误信息
				
				if(rowDatas.size() != 4){
					continue;
				}
				
                String empCode = rowDatas.get(0);
                String empName = rowDatas.get(1);
                String orgName = rowDatas.get(2);
                String companyName = rowDatas.get(3);
                String orgId = "";
                SysOrganization company = null;
    			TempEmpOrgExcelData tempEmpOrgExcelData = new TempEmpOrgExcelData();
    			
    			if(orgAllMap.get(companyName) != null){
					company = new SysOrganization();
					company = orgAllMap.get(companyName);
				}else{
					if(null != tempOrgCompanyAllMap.get(companyName)){
						company = new SysOrganization();
						company.setId(tempOrgCompanyAllMap.get(companyName).getCompanyName());
					}else{
						sb.append("集团/公司不存在.");
					}
				}
				
				if(StringUtils.isBlank(orgName)){
					sb.append("所属部门不能为空.");
				}else{
					if(orgName.indexOf(",") != -1){
						String strs[] = orgName.split(",");
						for (String string : strs) {
							if(null != company){
								if(null != orgByNameAndCompanyIdAllMap.get(string + "--" + company.getId())){
									orgId += orgByNameAndCompanyIdAllMap.get(string + "--" + company.getId()).getId() + ",";
								}else{
									if(null != tempOrgAllMap.get(string + "--" + company.getId())){
										orgId += tempOrgAllMap.get(string + "--" + company.getId()).getOrgCode() + ",";
									}else{
										sb.append(string + "所属部门不存在.");
									}
								}
							}
						}
					}else{
						if(null != company){
							if(null != orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId())){
								orgId = orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId()).getId();
							}else{
								if(null != tempOrgAllMap.get(orgName + "--" + company.getId())){
									orgId = tempOrgAllMap.get(orgName + "--" + company.getId()).getOrgCode();
								}else{
									sb.append("所属部门不存在.");
								}
							}
						}
					}
				}
    			
				if(StringUtils.isBlank(empCode)){
    				sb.append("员工编号不能为空.");
    			}
				
				if(StringUtils.isBlank(empName)){
					sb.append("员工名称不能为空.");
				}
				
				if(StringUtils.isBlank(companyName)){
    				sb.append("集团不能为空.");
    			}
				
				if(StringUtils.isNotBlank(empCode)){
					if(empCodeMap.get(empCode) == null){
						if(tempEmpCodeAllMap.get(empCode) == null){
							sb.append("员工编号不存在.");
						}
					}
				}
				
				if(StringUtils.isNotBlank(empName)){
					if(empNameMap.get(empName) == null){
						if(tempEmpNameAllMap.get(empName) == null){
							sb.append("员工名称不存在.");
						}
					}
				}
				
				if(orgId.indexOf(",") != -1){
					String strs[] = orgId.split(",");
					for (String string : strs) {
						if(empOrgAllMap.get(empCode + "--" + string) != null){
							sb.append(empOrgAllMap.get(empCode + "--" + string).getSysOrganization().getOrgname() + "此员工已关联机构.");
						}
					}
				}else{
					if(empOrgAllMap.get(empCode + "--" + orgId) != null){
						sb.append("此员工已关联机构.");
					}
				}
				
				if(null != company){
					if(empByEmpCompanyAllMap.get(empCode + "--" + empName + "--" + company.getId()) == null){
						if(tempEmpCompanyAllMap.get(empCode + "--" + empName + "--" + company.getId()) == null){
							sb.append("员工编号、名称、集团不符.");
						}
					}
				}
				
				if(!sb.toString().equalsIgnoreCase("")){
					errorInfo = false;
				}
				
				tempEmpOrgExcelData.setId(Identities.uuid());
				tempEmpOrgExcelData.setOrgName(orgName);
				tempEmpOrgExcelData.setEmpName(empName);
				tempEmpOrgExcelData.setEmpCode(empCode);
				tempEmpOrgExcelData.setOrgId(orgId);
				if(company != null){
					tempEmpOrgExcelData.setCompanyId(company.getId());
				}
				tempEmpOrgExcelData.setCompanyName(companyName);
				tempEmpOrgExcelData.setComment(sb.toString());
				tempEmpOrgExcelData.setExRow(String.valueOf(row + 1));
				tempEmpExcelDataList.add(tempEmpOrgExcelData);
			}
		}
		
		this.saveTempEmpOrgExcelData(tempEmpExcelDataList);
		
		Collections.sort(tempEmpExcelDataList, new Comparator<TempEmpOrgExcelData>() {
			@Override
			public int compare(TempEmpOrgExcelData arg0, TempEmpOrgExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
			
		});
		
		resultMap.put("datas", tempEmpExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findEmpOrgEx(FileForm form, int exPage){
		List<TempEmpOrgExcelData> tempEmpExcelDataList = new ArrayList<TempEmpOrgExcelData>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		List<List<String>> excelDatas = null;
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emporg_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, Object> maps = this.findEmpByEmpCodeAllMap();
		HashMap<String, SysEmployee> empCodeMap = (HashMap<String, SysEmployee>) maps.get("empCodeMap");
		HashMap<String, SysEmployee> empNameMap = (HashMap<String, SysEmployee>) maps.get("empNameMap");
		HashMap<String, SysOrganization> orgAllMap = o_dataImportBO.findOrgByNameAllMap();
		HashMap<String, SysOrganization> orgByNameAndCompanyIdAllMap = o_dataImportBO.findOrgByNameAndCompanyIdAllMap();
		HashMap<String, SysEmpOrg> empOrgAllMap = this.findEmpOrgAllMap();
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				StringBuilder sb = new StringBuilder();//记录错误信息
				
				if(rowDatas.size() != 4){
					continue;
				}
				
                String empCode = rowDatas.get(0);
                String empName = rowDatas.get(1);
                String orgName = rowDatas.get(2);
                String companyName = rowDatas.get(3);
                String orgId = "";
                SysOrganization company = null;
    			TempEmpOrgExcelData tempEmpOrgExcelData = new TempEmpOrgExcelData();
    			
    			if(orgAllMap.get(companyName) != null){
					company = new SysOrganization();
					company = orgAllMap.get(companyName);
				}else{
					sb.append("集团/公司不存在.");
				}
				
				if(StringUtils.isBlank(orgName)){
					sb.append("所属部门不能为空.");
				}else{
					if(orgName.indexOf(",") != -1){
						String strs[] = orgName.split(",");
						for (String string : strs) {
							if(null != company){
								if(null != orgByNameAndCompanyIdAllMap.get(string + "--" + company.getId())){
									orgId += orgByNameAndCompanyIdAllMap.get(string + "--" + company.getId()).getId() + ",";
								}else{
									sb.append(string + "所属部门不存在.");
								}
							}
						}
					}else{
						if(null != company){
							if(null != orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId())){
								orgId = orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId()).getId();
							}else{
								sb.append("所属部门不存在.");
							}
						}
					}
				}
    			
				if(StringUtils.isBlank(empCode)){
    				sb.append("员工编号不能为空.");
    			}
				
				if(StringUtils.isBlank(empName)){
					sb.append("员工名称不能为空.");
				}
				
				if(StringUtils.isBlank(companyName)){
    				sb.append("集团不能为空.");
    			}
				
				if(StringUtils.isNotBlank(empCode)){
					if(empCodeMap.get(empCode) == null){
						sb.append("员工编号不存在.");
					}
				}
				
				if(StringUtils.isNotBlank(empName)){
					if(empNameMap.get(empName) == null){
						sb.append("员工名称不存在.");
					}
				}
				
				if(orgId.indexOf(",") != -1){
					String strs[] = orgId.split(",");
					for (String string : strs) {
						if(empOrgAllMap.get(empCode + "--" + string) != null){
							sb.append(empOrgAllMap.get(empCode + "--" + string).getSysOrganization().getOrgname() + "此员工已关联机构.");
						}
					}
				}else{
					if(empOrgAllMap.get(empCode + "--" + orgId) != null){
						sb.append("此员工已关联机构.");
					}
				}
				
				if(!sb.toString().equalsIgnoreCase("")){
					errorInfo = false;
				}
				
				tempEmpOrgExcelData.setId(Identities.uuid());
				tempEmpOrgExcelData.setOrgName(orgName);
				tempEmpOrgExcelData.setEmpName(empName);
				tempEmpOrgExcelData.setEmpCode(empCode);
				tempEmpOrgExcelData.setOrgId(orgId);
				if(company != null){
					tempEmpOrgExcelData.setCompanyId(company.getId());
				}
				tempEmpOrgExcelData.setCompanyName(companyName);
				tempEmpOrgExcelData.setComment(sb.toString());
				tempEmpOrgExcelData.setExRow(String.valueOf(row + 1));
				tempEmpExcelDataList.add(tempEmpOrgExcelData);
			}
		}
		
		this.saveTempEmpOrgExcelData(tempEmpExcelDataList);
		
		Collections.sort(tempEmpExcelDataList, new Comparator<TempEmpOrgExcelData>() {
			@Override
			public int compare(TempEmpOrgExcelData arg0, TempEmpOrgExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
		});
		
		resultMap.put("datas", tempEmpExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	/**
	 * 查询员工机构关联临时表
	 * add by 王再冉
	 * 2014-4-30  上午10:15:56
	 * desc : 
	 * @param query
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findAllTmpEmpExcelDatas(String query){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> empList = new ArrayList<Map<String,Object>>();
		Boolean errorInfo = true;
		List<TempEmpOrgExcelData> tmpEmporgList = this.findTempEmp(query);
		for(TempEmpOrgExcelData tmpEmporg : tmpEmporgList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", tmpEmporg.getId());
			map.put("exRow", tmpEmporg.getExRow());
			map.put("empCode", tmpEmporg.getEmpCode());
			map.put("empName", tmpEmporg.getEmpName());
			map.put("companyName", tmpEmporg.getCompanyName());
			map.put("orgName", tmpEmporg.getOrgName());
			map.put("comment", tmpEmporg.getComment());
			if(StringUtils.isNotBlank(tmpEmporg.getComment())){
				errorInfo = false;
			}
			empList.add(map);
		}
		resultMap.put("errorInfo", errorInfo);//错误信息
		resultMap.put("datas", empList);//搜索框结果集（只能显示‘datas中的数据’）
		return resultMap;
	}
	/**
	 * 清空人员机构临时表数据
	 * add by 王再冉
	 * 2014-4-30  下午1:36:43
	 * desc :  
	 * void
	 */
	public void deleteTmpEmpOrgDatas(){
		String sql = "delete from temp_emporg_exceldata";
		SQLQuery sqlQuery = o_tempEmpOrgExcelDataDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
	}
}