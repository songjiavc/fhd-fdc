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
import com.fhd.dao.sys.organization.SysPosiDAO;
import com.fhd.dao.sys.organization.TempEmpPostExcelDataDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.dao.sys.orgstructure.SysEmpPosiDAO;
import com.fhd.entity.sys.orgstructure.SysEmpPosi;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.SysPosition;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpPostExcelData;
import com.fhd.entity.sys.orgstructure.TmpSysOrganization;
import com.fhd.entity.sys.orgstructure.TmpSysPosition;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.ra.business.assess.oper.SysEmployeeBO;
import com.fhd.sys.business.dataimport.DataImportBO;
import com.fhd.sys.business.orgstructure.TmpImpPositionBO;
import com.fhd.sys.web.form.file.FileForm;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 人员关联岗位业务
 * */


@Service
public class TempEmpPostExcelDataBO {
	@Autowired
	private SysUserDAO o_sysUserDAO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private DataImportBO o_dataImportBO;
	@Autowired
	private TmpImpPositionBO o_tmpImpPositionBO;
	@Autowired
	private SysPosiDAO o_posiDAO;
	@Autowired
	private TempEmpOrgExcelDataBO o_tempEmpOrgExcelDataBO;
	@Autowired
	private TempEmpPostExcelDataDAO o_tempEmpPostExcelDataDAO;
	@Autowired
	private SysEmpPosiDAO o_sysEmpPosiDAO;
	@Autowired
	private SysEmployeeBO o_SysEmployeeBO;
	
	/**
	 * 批量存储岗位关联
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveEmpPost(final List<SysEmpPosi> list) {
        this.o_tempEmpPostExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into t_sys_emp_posi (id,emp_id,posi_id) " +
                		" values(?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (SysEmpPosi sysEmpPosi : list) {
                	pst.setString(1, sysEmpPosi.getId());
                	pst.setString(2, sysEmpPosi.getSysEmployee().getId());
                	pst.setString(3, sysEmpPosi.getSysPosition().getId());
                	pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 批量存储人员岗位临时数据
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
    public void saveTempEmpPostExcelData(final List<TempEmpPostExcelData> list) {
        this.o_tempEmpPostExcelDataDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into temp_emppost_exceldata (id,emp_code,emp_name,post_id,post_name,ex_row,org_name,company_id,comment) " +
                		" values(?,?,?,?,?,?,?,?,?) ";
                pst = connection.prepareStatement(sql);
                
                for (TempEmpPostExcelData tempEmpPostExcelData : list) {
                	pst.setString(1, tempEmpPostExcelData.getId());
                	pst.setString(2, tempEmpPostExcelData.getEmpCode());
                	pst.setString(3, tempEmpPostExcelData.getEmpName());
                	pst.setString(4, tempEmpPostExcelData.getPostId());
                	pst.setString(5, tempEmpPostExcelData.getPostName());
                	pst.setString(6, tempEmpPostExcelData.getExRow());
                	pst.setString(7, tempEmpPostExcelData.getOrgName());
                	pst.setString(8, tempEmpPostExcelData.getCompanyId());
                	pst.setString(9, tempEmpPostExcelData.getComment());
                	pst.addBatch();
				}
                
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	
	/**
	 * 查询临时数据
	 * */
	public List<TempEmpPostExcelData> findTempEmp(String query){
		Criteria createCriteria = o_tempEmpPostExcelDataDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			createCriteria.add(Restrictions.or(Restrictions.like("empName",query,MatchMode.ANYWHERE),
					Restrictions.like("postName",query,MatchMode.ANYWHERE)));
		}
		@SuppressWarnings("unchecked")
		List<TempEmpPostExcelData> list = createCriteria.list();
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
				map.put(sysEmployee.getEmpcode() + "--"  + sysEmployee.getEmpname() + "--" + sysEmployee.getSysOrganization().getId(), sysEmployee);
			}
		}
		
		return map;
	}
	
	/**
	 * 包装岗位数据
	 * */
	public List<SysEmpPosi> getEmpPosi(List<TempEmpPostExcelData> list){
		List<SysEmpPosi> lists = new ArrayList<SysEmpPosi>();
		SysEmpPosi sysEmpPosi = null;
		SysEmployee emp = null;
		SysPosition sysPosition = null;
		
		Map<String, SysEmployee> empMap = o_SysEmployeeBO.findSysEmployeeByCodeMapAll();
		for (TempEmpPostExcelData tempEmpPostExcelData : list) {
			if(tempEmpPostExcelData.getPostId().indexOf(",") != -1){
				String strs[] = tempEmpPostExcelData.getPostId().split(",");
				for (String string : strs) {
					sysEmpPosi = new SysEmpPosi();
					sysEmpPosi.setId(Identities.uuid2());
					sysPosition = new SysPosition();
					emp = new SysEmployee();
					sysPosition.setId(string);
					if(empMap.get(tempEmpPostExcelData.getEmpCode()) != null){
						emp = empMap.get(tempEmpPostExcelData.getEmpCode());
					}
					sysEmpPosi.setSysEmployee(emp);
					sysEmpPosi.setSysPosition(sysPosition);
					lists.add(sysEmpPosi);
				}
			}else{
				sysEmpPosi = new SysEmpPosi();
				sysEmpPosi.setId(Identities.uuid2());
				sysPosition = new SysPosition();
				emp = new SysEmployee();
				sysPosition.setId(tempEmpPostExcelData.getPostId());
				if(empMap.get(tempEmpPostExcelData.getEmpCode()) != null){
					emp = empMap.get(tempEmpPostExcelData.getEmpCode());
				}
				sysEmpPosi.setSysEmployee(emp);
				sysEmpPosi.setSysPosition(sysPosition);
				lists.add(sysEmpPosi);
			}
		}
		
		return lists;
	}
	
//	/**
//	 * 临时机构数据(机构名称)
//	 * */
//	public HashMap<String, TmpSysOrganization> findTempOrgAllMap(){
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
	 * 系统岗位数据(人员ID--岗位ID)
	 * */
	public HashMap<String, SysEmpPosi> findEmpPostAllMap(){
		HashMap<String, SysEmpPosi> map = new HashMap<String, SysEmpPosi>();
		@SuppressWarnings("deprecation")
		Criteria createCriteria = o_sysEmpPosiDAO.createCriteria();
//		createCriteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
		@SuppressWarnings("unchecked")
		List<SysEmpPosi> list = createCriteria.list();
		for (SysEmpPosi sysEmpPosi : list) {
			try {
				map.put(sysEmpPosi.getSysEmployee().getEmpcode() + "--" + sysEmpPosi.getSysPosition().getId(), sysEmpPosi);
			} catch (Exception e) {
				// ENDO: handle exception
			}
		}
		
		return map;
	}
	
	/**
	 * 系统岗位数据(岗位名称--机构名称)
	 * */
	public HashMap<String, SysPosition> findPostAllMap(){
		HashMap<String, SysPosition> map = new HashMap<String, SysPosition>();
		Criteria createCriteria = o_posiDAO.createCriteria();
//		createCriteria.add(Restrictions.eq("deleteStatus", Contents.STATUS_NORMAL));
		@SuppressWarnings("unchecked")
		List<SysPosition> list = createCriteria.list();
		for (SysPosition sysPosition : list) {
			if(null != sysPosition.getSysOrganization()){
				map.put(sysPosition.getPosiname() + "--" + sysPosition.getSysOrganization().getId(), sysPosition);
			}
		}
		
		return map;
	}
	
	/**
	 * 临时岗位数据
	 * */
	public HashMap<String, TmpSysPosition> findTempPostAllMap(){
		HashMap<String, TmpSysPosition> map = new HashMap<String, TmpSysPosition>();
		List<TmpSysPosition> list = o_tmpImpPositionBO.findAllTmpPositionsSQL();
		for (TmpSysPosition tmpSysPosition : list) {
			map.put(tmpSysPosition.getPosiName() + "--" + tmpSysPosition.getSysOrganization().getId(), tmpSysPosition);
		}
		
		return map;
	}
	
	/**
	 * 没写完 需要继续写
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findTempEmpPostEx(FileForm form, int exPage, HashMap<String, Object> TempEmpObjeAllMap,
			HashMap<String, SysEmployee> empByEmpCompanyAllMap, HashMap<String, Object> tempOrgByObjAllMap){
		List<TempEmpPostExcelData> tempEmpPostExcelDataList = new ArrayList<TempEmpPostExcelData>();
		List<List<String>> excelDatas = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emppost_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, SysOrganization> orgAllMap = o_dataImportBO.findOrgByNameAllMap();
		HashMap<String, SysOrganization> orgByNameAndCompanyIdAllMap = o_dataImportBO.findOrgByNameAndCompanyIdAllMap();
		HashMap<String, SysPosition> postAllMap = this.findPostAllMap();
		HashMap<String, TmpSysPosition> tempPostAllMap = this.findTempPostAllMap();
		HashMap<String, Object> maps = o_tempEmpOrgExcelDataBO.findEmpByEmpCodeAllMap();
		HashMap<String, SysEmployee> empCodeMap = (HashMap<String, SysEmployee>) maps.get("empCodeMap");
		HashMap<String, SysEmployee> empNameMap = (HashMap<String, SysEmployee>) maps.get("empNameMap");
		HashMap<String, SysEmpPosi> empPostMap =  this.findEmpPostAllMap(); 
		
		
		//临时人员编号--名称--集团
		HashMap<String, TempEmpExcelData> tempEmpCompanyAllMap = (HashMap<String, TempEmpExcelData>) TempEmpObjeAllMap.get("tempEmpCompanyAllMap");
		//临时人员编号
		HashMap<String, TempEmpExcelData> tempEmpCodeAllMap = (HashMap<String, TempEmpExcelData>) TempEmpObjeAllMap.get("tempEmpCodeAllMap");
		//临时人员名称
		HashMap<String, TempEmpExcelData> tempEmpNameAllMap = (HashMap<String, TempEmpExcelData>) TempEmpObjeAllMap.get("tempEmpNameAllMap");
		
		HashMap<String, TmpSysOrganization> tempOrgCompanyAllMap = 
				(HashMap<String, TmpSysOrganization>) tempOrgByObjAllMap.get("tempOrgAllMap");//临时数据机构
		HashMap<String, TmpSysOrganization> tempOrgAllMap = 
				(HashMap<String, TmpSysOrganization>) tempOrgByObjAllMap.get("tempOrgCompanyAllMap");//临时数据机构
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				
				if(rowDatas.size() != 5){
					continue;
				}
				
				StringBuilder sb = new StringBuilder();//记录错误信息
				
                String empCode = rowDatas.get(0);
                String empName = rowDatas.get(1);
                String orgName = rowDatas.get(2);
                String postName = rowDatas.get(3);
                String companyName = rowDatas.get(4);
                String orgId = "";
                SysOrganization company = null;
                SysOrganization org = null;
                TempEmpPostExcelData tempEmpPostExcelData = new TempEmpPostExcelData();
                String postId = "";
    			
    			if(orgAllMap.get(companyName) != null){
					company = new SysOrganization();
					company.setId(orgAllMap.get(companyName).getId());
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
					if(null != company){
						if(null != orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId())){
							org = new SysOrganization();
							orgId = orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId()).getId();
							org.setId(orgId);
						}else{
							if(null != tempOrgAllMap.get(orgName + "--" + company.getId())){
								org = new SysOrganization();
								orgId = tempOrgAllMap.get(orgName + "--" + company.getId()).getId();
								org.setId(orgId);
								org.setOrgcode(tempOrgAllMap.get(orgName + "--" + company.getId()).getOrgCode());
							}else{
								sb.append("所属部门不存在.");
							}
						}
					}
				}
				
				if(StringUtils.isBlank(postName)){
					sb.append("所属岗位不能为空.");
				}
				
				if(StringUtils.isNotBlank(postName)){
					if(postName.indexOf(",") != -1){
						String str[] = postName.split(",");
						for (String string : str) {
							if(null != org){
								if(postAllMap.get(string + "--" + org.getId()) == null){
									if(tempPostAllMap.get(string + "--" + org.getId()) == null && tempPostAllMap.get(string + "--" + org.getOrgcode()) == null){
										sb.append(string + "所属岗位不存在.");
									}else{
										postId += tempPostAllMap.get(string + "--" + org.getOrgcode()).getPosiCode() + ",";
									}
								}else{
									postId += postAllMap.get(string + "--" + org.getId()).getId() + ",";
								}
							}
						}
					}else{
						if(null != org){
							if(postAllMap.get(postName + "--" + org.getId()) == null){
								if(tempPostAllMap.get(postName + "--" + org.getOrgcode()) == null && tempPostAllMap.get(postName + "--" + org.getOrgcode()) == null){
									sb.append("所属岗位不存在.");
								}else{
									postId = tempPostAllMap.get(postName + "--" + org.getOrgcode()).getPosiCode() + ",";
								}
							}else{
								postId = postAllMap.get(postName + "--" + org.getId()).getId();
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
				
				if(StringUtils.isNotBlank(postId)){
					if(postId.indexOf(",") != -1){
						String str[] = postId.split(",");
						for (String string : str) {
							if(empPostMap.get(empCode + "--" + string) != null){
								sb.append(empPostMap.get(empCode + "--" + string).getSysPosition().getPosiname() + "岗位关联已存在.");
							}
						}
					}else{
						if(empPostMap.get(empCode + "--" + postId) != null){
							sb.append("员工岗位关联已存在.");
						}
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
				
				tempEmpPostExcelData.setId(Identities.uuid());
				tempEmpPostExcelData.setEmpCode(empCode);
				tempEmpPostExcelData.setEmpName(empName);
				tempEmpPostExcelData.setOrgName(orgName);
				tempEmpPostExcelData.setPostName(postName);
				tempEmpPostExcelData.setPostId(postId);
				if(company != null){
					tempEmpPostExcelData.setCompanyId(company.getId());
				}
				tempEmpPostExcelData.setCompanyName(companyName);
				tempEmpPostExcelData.setComment(sb.toString());
				tempEmpPostExcelData.setExRow(String.valueOf(row + 1));
				tempEmpPostExcelDataList.add(tempEmpPostExcelData);
			}
		}
		
		this.saveTempEmpPostExcelData(tempEmpPostExcelDataList);
		
		Collections.sort(tempEmpPostExcelDataList, new Comparator<TempEmpPostExcelData>() {
			@Override
			public int compare(TempEmpPostExcelData arg0, TempEmpPostExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
			
		});
		
		resultMap.put("datas", tempEmpPostExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findEmpPostEx(FileForm form, int exPage){
		List<TempEmpPostExcelData> tempEmpPostExcelDataList = new ArrayList<TempEmpPostExcelData>();
		List<List<String>> excelDatas = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean errorInfo = true;
		
		try {
			excelDatas = (List<List<String>>)new ReadExcel().readEspecialExcel(form.getFile().getInputStream(), exPage);// 读取文件(第二页)
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//清空临时表数据
		String sql = "delete from temp_emppost_exceldata";
		SQLQuery sqlQuery = o_sysUserDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
		
		HashMap<String, SysOrganization> orgAllMap = o_dataImportBO.findOrgByNameAllMap();
		HashMap<String, SysOrganization> orgByNameAndCompanyIdAllMap = o_dataImportBO.findOrgByNameAndCompanyIdAllMap();
		HashMap<String, SysPosition> postAllMap = this.findPostAllMap();
		HashMap<String, Object> maps = o_tempEmpOrgExcelDataBO.findEmpByEmpCodeAllMap();
		HashMap<String, SysEmployee> empCodeMap = (HashMap<String, SysEmployee>) maps.get("empCodeMap");
		HashMap<String, SysEmployee> empNameMap = (HashMap<String, SysEmployee>) maps.get("empNameMap");
		HashMap<String, SysEmpPosi> empPostMap =  this.findEmpPostAllMap(); 
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				
				if(rowDatas.size() != 5){
					continue;
				}
				
				StringBuilder sb = new StringBuilder();//记录错误信息
				
                String empCode = rowDatas.get(0);
                String empName = rowDatas.get(1);
                String orgName = rowDatas.get(2);
                String postName = rowDatas.get(3);
                String companyName = rowDatas.get(4);
                String orgId = "";
                SysOrganization company = null;
                SysOrganization org = null;
                TempEmpPostExcelData tempEmpPostExcelData = new TempEmpPostExcelData();
                String postId = "";
    			
    			if(orgAllMap.get(companyName) != null){
					company = new SysOrganization();
					company.setId(orgAllMap.get(companyName).getId());
				}else{
					sb.append("集团/公司不存在.");
				}
				
				if(StringUtils.isBlank(orgName)){
					sb.append("所属部门不能为空.");
				}else{
					if(null != company){
						if(null != orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId())){
							org = new SysOrganization();
							orgId = orgByNameAndCompanyIdAllMap.get(orgName + "--" + company.getId()).getId();
							org.setId(orgId);
						}else{
							sb.append("所属部门不存在.");
						}
					}
				}
				
				if(StringUtils.isBlank(postName)){
					sb.append("所属岗位不能为空.");
				}
				
				if(StringUtils.isNotBlank(postName)){
					if(postName.indexOf(",") != -1){
						String str[] = postName.split(",");
						for (String string : str) {
							if(null != org){
								if(postAllMap.get(string + "--" + org.getId()) == null){
									sb.append(string + "所属岗位不存在.");
								}else{
									postId += postAllMap.get(string + "--" + org.getId()).getId() + ",";
								}
							}
						}
					}else{
						if(null != org){
							if(postAllMap.get(postName + "--" + org.getId()) == null){
								sb.append("所属岗位不存在.");
							}else{
								postId = postAllMap.get(postName + "--" + org.getId()).getId();
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
				
				if(StringUtils.isNotBlank(postId)){
					if(postId.indexOf(",") != -1){
						String str[] = postId.split(",");
						for (String string : str) {
							if(empPostMap.get(empCode + "--" + string) != null){
								sb.append(empPostMap.get(empCode + "--" + string).getSysPosition().getPosiname() + "岗位关联已存在.");
							}
						}
					}else{
						if(empPostMap.get(empCode + "--" + postId) != null){
							sb.append("员工岗位关联已存在.");
						}
					}
				}
				
				if(!sb.toString().equalsIgnoreCase("")){
					errorInfo = false;
				}
				
				tempEmpPostExcelData.setId(Identities.uuid());
				tempEmpPostExcelData.setEmpCode(empCode);
				tempEmpPostExcelData.setEmpName(empName);
				tempEmpPostExcelData.setOrgName(orgName);
				tempEmpPostExcelData.setPostName(postName);
				tempEmpPostExcelData.setPostId(postId);
				if(company != null){
					tempEmpPostExcelData.setCompanyId(company.getId());
				}
				tempEmpPostExcelData.setCompanyName(companyName);
				tempEmpPostExcelData.setComment(sb.toString());
				tempEmpPostExcelData.setExRow(String.valueOf(row + 1));
				tempEmpPostExcelDataList.add(tempEmpPostExcelData);
			}
		}
		
		this.saveTempEmpPostExcelData(tempEmpPostExcelDataList);
		
		Collections.sort(tempEmpPostExcelDataList, new Comparator<TempEmpPostExcelData>() {
			@Override
			public int compare(TempEmpPostExcelData arg0, TempEmpPostExcelData arg1) {
				return -arg0.getComment().compareTo(arg1.getComment());
			}
			
		});
		
		resultMap.put("datas", tempEmpPostExcelDataList);
		resultMap.put("errorInfo", errorInfo);
		resultMap.put("success", true);
		
		return resultMap;
	}
	/**
	 * 查询全部员工岗位临时表
	 * add by 王再冉
	 * 2014-4-30  上午10:41:03
	 * desc : 
	 * @param query
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findAllTmpEmpPosisExcelDatas(String query){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> empList = new ArrayList<Map<String,Object>>();
		Boolean errorInfo = true;
		List<TempEmpPostExcelData> tmpEmpPosiList = this.findTempEmp(query);
		for(TempEmpPostExcelData tmpEmpPosi : tmpEmpPosiList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", tmpEmpPosi.getId());
			map.put("exRow", tmpEmpPosi.getExRow());
			map.put("empCode", tmpEmpPosi.getEmpCode());
			map.put("empName", tmpEmpPosi.getEmpName());
			map.put("orgName", tmpEmpPosi.getOrgName());
			map.put("postName", tmpEmpPosi.getPostName());
			map.put("companyName", tmpEmpPosi.getCompanyName());
			map.put("comment", tmpEmpPosi.getComment());
			if(StringUtils.isNotBlank(tmpEmpPosi.getComment())){
				errorInfo = false;
			}
			empList.add(map);
		}
		resultMap.put("errorInfo", errorInfo);//错误信息
		resultMap.put("datas", empList);//搜索框结果集（只能显示‘datas中的数据’）
		return resultMap;
	}
	/**
	 * 清空人员岗位临时表数据
	 * add by 王再冉
	 * 2014-4-30  下午1:38:52
	 * desc :  
	 * void
	 */
	public void deleteTmpEmpPosiDatas(){
		String sql = "delete from temp_emppost_exceldata";
		SQLQuery sqlQuery = o_tempEmpPostExcelDataDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
	}
}