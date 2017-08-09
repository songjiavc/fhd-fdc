package com.fhd.sys.business.orgstructure;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DigestUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.organization.TmpImpOrganizationDAO;
import com.fhd.dao.sys.organization.TmpSysOrganizationDAO;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmpPosi;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.SysPosition;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpOrgExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpPostExcelData;
import com.fhd.entity.sys.orgstructure.TempEmpRoleExcelData;
import com.fhd.entity.sys.orgstructure.TmpImpOrganization;
import com.fhd.entity.sys.orgstructure.TmpSysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.auth.SysUserBO;
import com.fhd.sys.business.file.FileUploadBO;
import com.fhd.sys.business.organization.EmpOrgBO;
import com.fhd.sys.business.organization.EmpPosBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.business.organization.OrgGridBO;
import com.fhd.sys.business.organization.PositionBO;
import com.fhd.sys.business.organization.TempEmpExcelDataBO;
import com.fhd.sys.business.organization.TempEmpOrgExcelDataBO;
import com.fhd.sys.business.organization.TempEmpPostExcelDataBO;
import com.fhd.sys.business.organization.TempEmpRoleExcelDataBO;

/**
 * 
 * ClassName:TmpImpOrganizationBO 组织机构导入BO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-30		上午11:33:40
 *
 * @see
 */
@Service
public class TmpImpOrganizationBO{
	@Autowired
	private TmpImpOrganizationDAO o_tmpImpOrganizationDAO;
	@Autowired
	private FileUploadBO o_fileUploadBO;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
	private PositionBO o_positionBO;
	@Autowired
	private SysUserBO o_userBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private EmpOrgBO o_empOrgBO;
	@Autowired
	private EmpPosBO o_empPosBO;
	@Autowired
	private RoleBO o_roleBO;
	@Autowired
	private OrgGridBO o_orgGridBO;
	@Autowired
	private TmpSysOrganizationDAO o_tmpSysOrganizationDAO;
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
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	
	/**
	 * 
	 * validate:
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void validate(String fileId) throws Exception{
		this.mergeByFileId(fileId);
		List<TmpImpOrganization> list = this.findByFileId(fileId);
		for (TmpImpOrganization tmpImpOrganization : list) {
			StringBuffer error=new StringBuffer("");
			String companyName = tmpImpOrganization.getCompanyName();
			String organizationName = tmpImpOrganization.getOrganizationName();
			String userName = tmpImpOrganization.getUserName();
			String empName = tmpImpOrganization.getEmpName();
			String haveRoleNames = tmpImpOrganization.getHaveRoleNames();
			if(StringUtils.isBlank(companyName)){
				error.append("未指定所属公司。");
			}
			if(StringUtils.isNotBlank(haveRoleNames)){
				if(StringUtils.isBlank(empName)){
					error.append("未指定员工。");
				}
				if(StringUtils.isBlank(userName)){
					error.append("未指定用户。");
				}
				if(StringUtils.isBlank(organizationName)){
					error.append("未指定部门。");
				}
				String[] roleNames = StringUtils.split(haveRoleNames,",");
				for (String roleName : roleNames) {
					List<SysRole> roles = o_roleBO.findByRoleName(roleName);
					if(roles.size()==0){
						error.append("包含未指定角色。");
					}
				}
			}
			if(StringUtils.isNotBlank(empName)||StringUtils.isNotBlank(userName)){
				if(StringUtils.isBlank(empName)){
					error.append("未指定员工。");
				}
				if(StringUtils.isBlank(userName)){
					error.append("未指定用户。");
				}
				if(StringUtils.isBlank(organizationName)){
					error.append("未指定部门。");
				}
			}
			tmpImpOrganization.setError(error.toString());
			o_tmpImpOrganizationDAO.merge(tmpImpOrganization);
		}
	}
	/**
	 * 
	 * mergeByFileId:
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void mergeByFileId(String fileId) throws Exception{
		FileUploadEntity fileUploadEntity = o_fileUploadBO.findById(fileId);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileUploadEntity.getContents());
		List<List<String>> dataListList = new ReadExcel<String>().readEspecialExcel(byteArrayInputStream, "组织机构");
		this.removeByFileId(fileId);
		Map<Integer,String> headMap = new HashMap<Integer, String>();
		int i=0;
		for (List<String> dataList : dataListList) {
			i++;
			int j=0;
			if(headMap.keySet().isEmpty()){
				for (String data : dataList) {
					j++;
					if(StringUtils.isNotBlank(data)){
						data=data.replaceAll(" ", "");
						data=data.replaceAll("\n", "");
						data=data.replaceAll("\t", "");
						if("所属公司编号".equals(data)){
							headMap.put(j,"companyCode");
						}else if("所属公司".equals(data)){
							headMap.put(j,"companyName");
						}else if("所属部门编号".equals(data)){
							headMap.put(j,"organizationCode");
						}else if("所属部门".equals(data)){
							headMap.put(j,"organizationName");
						}else if("所属岗位编号".equals(data)){
							headMap.put(j,"positionCode");
						}else if("所属岗位".equals(data)){
							headMap.put(j,"positionName");
						}else if("登录名".equals(data)){
							headMap.put(j,"userName");
						}else if("员工名称".equals(data)){
							headMap.put(j,"empName");
						}else if("拥有角色编号".equals(data)){
							headMap.put(j,"haveRoleCodes");
						}else if("拥有角色".equals(data)){
							headMap.put(j,"haveRoleNames");
						}
					}
				}
			}else{
				TmpImpOrganization tmpImpOrganization=new TmpImpOrganization();
				String companyCode="";
				String companyName="";
				String organizationCode="";
				String organizationName="";
				String positionCode="";
				String positionName="";
				String userName="";
				String empName="";
				String haveRoleCodes="";
				String haveRoleNames="";
				String error="";
				for (String data : dataList) {
					j++;
					String head = headMap.get(j);
					if(StringUtils.isNotBlank(data)&&StringUtils.isNotBlank(head)){
						data=data.trim();
						if("companyCode".equals(head)){
							companyCode=data;
						}else if("companyName".equals(head)){
							companyName=data;
						}else if("organizationCode".equals(head)){
							organizationCode=data;
						}else if("organizationName".equals(head)){
							organizationName=data;
						}else if("positionCode".equals(head)){
							positionCode=data;
						}else if("positionName".equals(head)){
							positionName=data;
						}else if("userName".equals(head)){
							userName=data;
						}else if("empName".equals(head)){
							empName=data;
						}else if("haveRoleCodes".equals(head)){
							haveRoleCodes=data;
						}else if("haveRoleNames".equals(head)){
							haveRoleNames=data;
						}
					}
				}
				tmpImpOrganization.setIndex(String.valueOf(i));
				tmpImpOrganization.setId(Identities.uuid());
				tmpImpOrganization.setCompanyCode(companyCode);
				tmpImpOrganization.setCompanyName(companyName);
				tmpImpOrganization.setOrganizationCode(organizationCode);
				tmpImpOrganization.setOrganizationName(organizationName);
				tmpImpOrganization.setPositionCode(positionCode);
				tmpImpOrganization.setPositionName(positionName);
				tmpImpOrganization.setUserName(userName);
				tmpImpOrganization.setEmpName(empName);
				tmpImpOrganization.setHaveRoleCodes(haveRoleCodes);
				tmpImpOrganization.setHaveRoleNames(haveRoleNames);
				tmpImpOrganization.setFileUploadEntity(fileUploadEntity);
				tmpImpOrganization.setError(error);
				o_tmpImpOrganizationDAO.merge(tmpImpOrganization);
			}
		}
	}
	/**
	 * 
	 * removeByFileId:
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeByFileId(String fileId){
		String hql="delete from TmpImpOrganization where fileUploadEntity.id=?";
		o_tmpImpOrganizationDAO.createQuery(hql, fileId).executeUpdate();
	}
	/**
	 * 
	 * findByFileId:
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@SuppressWarnings("unchecked")
	public List<TmpImpOrganization> findByFileId(String fileId){
		Criteria createCriteria = o_tmpImpOrganizationDAO.createCriteria();
		if(StringUtils.isNotBlank(fileId)){
			createCriteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		return createCriteria.list();
	}
	/**
	 * 
	 * findPageBySome:
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param fileId
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<TmpImpOrganization> findPageBySome(Page<TmpImpOrganization> page,String query,String fileId,List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(TmpImpOrganization.class);
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("userName", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		if(StringUtils.isNotBlank(fileId)){
			criteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}
		
		return o_tmpImpOrganizationDAO.findPage(criteria, page, false);
	}
	/**
	 * 
	 * importData:
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void importData(String fileId) {
		List<TmpImpOrganization> list = this.findByFileId(fileId);
		List<SysOrganization> rootOrgs = o_organizationBO.findRootOrgByAll();
		SysOrganization rootOrg =null;
		if(rootOrgs.size()>0){
			rootOrg = rootOrgs.get(0);
		}
		for (TmpImpOrganization tmpImpOrganization : list) {
			String companyName = tmpImpOrganization.getCompanyName();
			String organizationName = tmpImpOrganization.getOrganizationName();
			String positionName = tmpImpOrganization.getPositionName();
			String userName = tmpImpOrganization.getUserName();
			String empName = tmpImpOrganization.getEmpName();
			String haveRoleNames = tmpImpOrganization.getHaveRoleNames();
			String error = tmpImpOrganization.getError();
			/**
			 * 读取公司
			 */
			SysOrganization company =null;
			if(StringUtils.isBlank(error)){
				if(StringUtils.isNotBlank(companyName)){
					List<SysOrganization> companys = o_organizationBO.findByName(companyName);
					if(companys.size()>0){
						company = companys.get(0);
					}else{
						company = new SysOrganization();
						company.setId(Identities.uuid());
						company.setOrgname(companyName);
						company.setCompany(company);
						company.setIsLeaf(true);
						company.setOrgStatus(Contents.STATUS_NORMAL);
						company.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
						if(rootOrg==null){
							company.setOrgType("0orgtype_c");
							company.setSn(1);
							rootOrg=company;
						}else{
							company.setOrgType("0orgtype_sc");
							company.setParentOrg(rootOrg);
							Integer sn=1;
							if(rootOrg.getChildrenOrg()!=null){
								sn=rootOrg.getChildrenOrg().size()+1;
							}
							company.setSn(sn);
						}
						o_organizationBO.save(company);
					}
				}
				/**
				 * 读取部门
				 */
				SysOrganization organization=null;
				if(StringUtils.isNotBlank(organizationName)){
					List<SysOrganization> organizations = o_organizationBO.findBySome(organizationName,company.getId());
					if(organizations.size()>0){
						organization = organizations.get(0);
					}else{
						organization = new SysOrganization();
						organization.setId(Identities.uuid());
						organization.setOrgname(organizationName);
						organization.setIsLeaf(true);
						organization.setParentOrg(company);
						organization.setCompany(company);
						Integer sn=1;
						if(company.getChildrenOrg()!=null){
							sn=company.getChildrenOrg().size()+1;
						}
						organization.setSn(sn);
						organization.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
						organization.setOrgStatus(Contents.STATUS_NORMAL);
						String orgType = company.getOrgType();
						if("0orgtype_c".equals(orgType)){
							organization.setOrgType("0orgtype_d");
						}else if("0orgtype_sc".equals(orgType)){
							organization.setOrgType("0orgtype_sd");
						}
						o_organizationBO.save(organization);
					}
				}
				/**
				 * 读取岗位
				 */
				SysPosition position=null;
				if(StringUtils.isNotBlank(positionName)){
					List<SysPosition> positions = o_positionBO.findBySome(positionName,organization.getId());
					if(positions.size()>0){
						position = positions.get(0);
					}else{
						position = new SysPosition();
						position.setId(Identities.uuid());
						position.setPosiname(positionName);
						position.setPosiStatus(Contents.STATUS_NORMAL);
						position.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
						position.setSysOrganization(organization);
						o_positionBO.save(position);
					}
				}
				/**
				 * 读取用户
				 */
				SysUser user=null;
				String userId=null;
				if(StringUtils.isNotBlank(userName)){
					List<SysUser> users = o_userBO.findByUsername(userName);
					if(users.size()>0){
						user = users.get(0);
						userId=user.getId();
					}else{
						user=new SysUser();
						user.setId(Identities.uuid());
						user.setUsername(userName);
						user.setRealname(empName);
						user.setPassword(DigestUtils.md5ToHex("111111"));
						user.setLockstate(false);
						user.setUserStatus(Contents.STATUS_NORMAL);
						user.setEnable(true);
						o_userBO.save(user);
					}
				}
				/**
				 * 读取员工
				 */
				SysEmployee employee=null;
				if(StringUtils.isNotBlank(userId)){
					List<SysEmployee> employees = o_employeeBO.findByUserId(userId);
					if(employees.size()>0){
						employee = employees.get(0);
					}else{
						employee=new SysEmployee();
						employee.setId(Identities.uuid());
					}
					employee.setEmpname(empName);
					employee.setUsername(userName);
					employee.setRealname(empName);
					employee.setSysOrganization(company);
					employee.setEmpStatus(Contents.STATUS_NORMAL);
					employee.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
					employee.setUserid(user.getId());
					o_employeeBO.save(employee);
					
					List<SysEmpOrg> empOrgs = o_empOrgBO.findBySome(employee.getId(), organization.getId());
					if(empOrgs.size()==0){
						SysEmpOrg empOrg=new SysEmpOrg();
						empOrg.setId(Identities.uuid());
						empOrg.setSysOrganization(organization);
						empOrg.setSysEmployee(employee);
						empOrg.setIsmain(true);
						o_empOrgBO.save(empOrg);
					}
					if(position!=null){
						List<SysEmpPosi> empPoss = o_empPosBO.findBySome(employee.getId(), position.getId());
						if(empPoss.size()==0){
							SysEmpPosi empPos=new SysEmpPosi();
							empPos.setId(Identities.uuid());
							empPos.setSysPosition(position);
							empPos.setSysEmployee(employee);
							empPos.setIsmain(true);
							o_empPosBO.save(empPos);
						}
					}
					
					/**
					 * 角色赋予
					 */
					if(StringUtils.isNotBlank(haveRoleNames)){
						String[] roleNames = StringUtils.split(haveRoleNames,",");
						List<SysRole> roles = o_roleBO.findByRoleNames(roleNames);
						user.setSysRoles(new HashSet<SysRole>(roles));
						o_userBO.merge(user);
					}
				}
			}
		}
	}
/*********************************新版组织导入分割线**********************************************************/
	/**
	 * 保存机构临时表数据
	 * add by 王再冉
	 * 2014-4-22  下午3:03:33
	 * desc : 
	 * @param orgList 
	 * void
	 */
	@Transactional
    public void saveTmpOrgs(final List<TmpSysOrganization> orgList) {
        this.o_tmpSysOrganizationDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String isLeaf = "";
                String sql = " insert into TMP_IMP_SYS_ORGANIZATION " +
                		 " (ID,PARENT_ORG_CODE,ORG_CODE,ORG_NAME,ORG_LEVEL,ESORT,ID_SEQ,IS_LEAF,COMPANY_ID," +
                		 "NAME_SEQ,ERROR,ORG_TYPE,PARENT_ORG_NAME,COMPANY_NAME,ROW_LINE) " + 
                		 " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                
                pst = connection.prepareStatement(sql);
                
                for (TmpSysOrganization org : orgList) {
                	if(org.getIsLeaf()){
                		isLeaf = "1";
                	}else{
                		isLeaf = "0";
                	}
					pst.setString(1, org.getId());
					if(null == org.getParentOrg()){
						pst.setString(2, null);
					}else{
						pst.setString(2, org.getParentOrg().getId());
					}
					pst.setString(3, org.getOrgCode());
					pst.setString(4, org.getOrgName());
					pst.setInt(5, org.getOrgLevel());
					pst.setInt(6, org.getSn());
					pst.setString(7, org.getIdSeq());
					pst.setString(8, isLeaf);
					pst.setString(9, org.getCompany().getId());
					pst.setString(10, org.getNameSeq());
					pst.setString(11, org.getError());
					pst.setString(12, org.getOrgType());
					pst.setString(13, org.getParentName());
					pst.setString(14, org.getCompanyName());
					pst.setInt(15, org.getRowLine());
					pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	/**
	 * 清空机构临时表数据
	 * add by 王再冉
	 * 2014-4-23  下午5:26:37
	 * desc :  
	 * void
	 */
	public void deleteTmpSysOrganizationsSQL(){
		String sql = " delete from TMP_IMP_SYS_ORGANIZATION ";
		SQLQuery sqlQuery = o_tmpSysOrganizationDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
	}
	/**
	 * 验证需要导入的数据
	 * add by 王再冉
	 * 2014-4-22  上午9:57:01
	 * desc : 
	 * @param excelDatas
	 * @return 
	 * Boolean
	 */
	public Map<String, Object> importOrgData(List<List<String>> excelDatas){
		//删除临时表数据
		this.deleteTmpSysOrganizationsSQL();
		Boolean flag = true;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		ArrayList<Map<String, Object>> vdeptMappList = new ArrayList<Map<String, Object>>();
		HashMap<String, Map<String, Object>> vdeptMapp = new HashMap<String, Map<String,Object>>();
		HashMap<String, Map<String, Object>> vdeptParentMapp = new HashMap<String, Map<String,Object>>();
		List<TmpSysOrganization> orgList = new ArrayList<TmpSysOrganization>();
		List<String> excOrgList = new ArrayList<String>();//excel中机构编号
		List<String> orgCodeList = o_orgGridBO.findAllOrgIds();//机构id（编号）集合
		Map<String,String> orgCodeIdMap = this.findAllOrgCodeAndIdMap();//机构编号和id map
		if (excelDatas != null && excelDatas.size() > 0) {
			// 依次读取EXCEL第三行以后的数据（前两行为标题与说明）
			for (int row = 2; row < excelDatas.size(); row++){
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("DEPTCODE", rowDatas.get(0));//部门编号
				map.put("DEPTNAME", rowDatas.get(1));
				if(StringUtils.isNotBlank(rowDatas.get(2))){
					if(StringUtils.isNotBlank(orgCodeIdMap.get(rowDatas.get(2)))){
						map.put("PARENTDEPTCODE", orgCodeIdMap.get(rowDatas.get(2)));
					}else{
						map.put("PARENTDEPTCODE", null);
					}
//					map.put("PARENTDEPTCODE", rowDatas.get(2));
				}else{
					map.put("PARENTDEPTCODE", null);
				}
				map.put("ORDERBY", rowDatas.get(3));
				map.put("ORGTYPE", rowDatas.get(4));//机构类型
				map.put("ROWLINE", row+1);//行号
				list.add(map);
			}
			for (Map<String, Object> vDeptMappAllDept : list) {
				if(null == vDeptMappAllDept.get("DEPTCODE")){
					continue;
				}if(null == vDeptMappAllDept.get("DEPTNAME")){
					continue;
				}if(null == vDeptMappAllDept.get("ORDERBY")){
					continue;
				}
				vdeptMappList.add(vDeptMappAllDept);
				vdeptMapp.put(vDeptMappAllDept.get("DEPTCODE").toString(), vDeptMappAllDept);
				if(null !=  vDeptMappAllDept.get("PARENTDEPTCODE")){
					vdeptParentMapp.put(vDeptMappAllDept.get("PARENTDEPTCODE").toString(), vDeptMappAllDept);
				}
			}
			
			for (Map<String, Object> deptMap : vdeptMappList) {
				TmpSysOrganization org = this.getOrgEntity(deptMap, vdeptMapp, vdeptParentMapp);
				if(null != org){
					//验证
					StringBuffer error=new StringBuffer("");//验证错误信息
					if(orgCodeList.contains(org.getOrgCode()) || excOrgList.contains(org.getOrgCode())){
						error.append("机构编号重复;");
					}
					if(null == org.getCompany()){
						error.append("未指定所属公司;");
					}
					if(StringUtils.isBlank(org.getOrgType())){
						error.append("未指定机构类型;");
					} if(StringUtils.isBlank(org.getOrgCode())){
						error.append("机构编号为空;");
					} if(StringUtils.isBlank(org.getOrgName())){
						error.append("机构名称为空;");
					} if(null == org.getParentOrg()){
						error.append("上级机构不存在;");
					}
					if(error.length()>0){
						org.setError(error.toString());//保存错误信息
						flag = false;
					}
					orgList.add(org);
					excOrgList.add(org.getOrgCode());
				}
			}
		}
		this.saveTmpOrgs(orgList);
		resultMap.put("datas", orgList);
		resultMap.put("errorInfo", flag);
		resultMap.put("success", true);
		return resultMap;
	}
	
	/**
	 * 组织全部部门,子公司数据
	 * */
	public TmpSysOrganization getOrgEntity(Map<String, Object> deptMap,
			HashMap<String, Map<String, Object>> vdeptMapp, HashMap<String, Map<String, Object>> vdeptParentMapp){
		//全部部门集合
		TmpSysOrganization organization = null;
		ArrayList<String> idSeqArrayList = null;
		ArrayList<String> nameSeqArrayList = null;
		int companyCount = 0;
		String companyId = "";
		String deptCode = "";
		String deptName = "";
		String deptType = "";
		String parentDeptCode = "";
		int order = 0;
		int level = 1;
		int rowLine = 0;
		String idSeq = "";
		String nameSeq = "";
		String parentIdTemp = "";
		boolean isLeaf = false;
		Boolean noExitParent = false;//上级机构不存在
		
		deptCode = deptMap.get("DEPTCODE").toString();
		deptName = deptMap.get("DEPTNAME").toString();
		deptType = deptMap.get("ORGTYPE").toString();//机构类型
		rowLine = Integer.parseInt(deptMap.get("ROWLINE").toString());
		if(null != deptMap.get("PARENTDEPTCODE")){
			parentDeptCode = deptMap.get("PARENTDEPTCODE").toString();
		}else{
			parentDeptCode = null;
		}
		
		if(null != parentDeptCode){
			if(null == vdeptMapp.get(parentDeptCode)){//excel找不到上级部门
				SysOrganization parentorg = o_orgGridBO.findOrganizationByOrgId(parentDeptCode);
				if(null == parentorg){//从机构表找上级
					noExitParent = true;
				}else{
					Map<String, Object> innerMap = new HashMap<String, Object>();
					innerMap.put("DEPTCODE", parentorg.getId());
					innerMap.put("DEPTNAME", parentorg.getOrgname());
					innerMap.put("ORGTYPE", parentorg.getOrgType());
					innerMap.put("PARENTDEPTCODE", null!=parentorg.getParentOrg()?parentorg.getParentOrg().getOrgname():null);
					vdeptMapp.put(parentorg.getId(), innerMap);
				}
			}
		}
		
		idSeqArrayList = new ArrayList<String>();
		nameSeqArrayList = new ArrayList<String>();
		if(StringUtils.isNotBlank(deptMap.get("ORDERBY").toString())){
			order = Integer.parseInt(deptMap.get("ORDERBY").toString());
		}
		organization = new TmpSysOrganization();

		if(StringUtils.isNotBlank(parentDeptCode) && !noExitParent){
			if(null == isParent(vdeptMapp, parentDeptCode).get("PARENTDEPTCODE")){
				//上级是集团
				idSeqArrayList.add(deptCode);
				idSeqArrayList.add(isParent(vdeptMapp, parentDeptCode).get("DEPTCODE").toString());
				nameSeqArrayList.add(deptName);
				nameSeqArrayList.add(isParent(vdeptMapp, parentDeptCode).get("DEPTNAME").toString());
				level++;
			}else{
				while(true){
					if(StringUtils.isBlank(parentIdTemp)){
						idSeqArrayList.add(deptCode);
						idSeqArrayList.add(isParent(vdeptMapp, parentDeptCode).get("DEPTCODE").toString());
						
						nameSeqArrayList.add(deptName);
						nameSeqArrayList.add(isParent(vdeptMapp, parentDeptCode).get("DEPTNAME").toString());
						parentIdTemp = parentDeptCode;
					}else{
						if(null == isParent(vdeptMapp, parentIdTemp).get("PARENTDEPTCODE")){
							break;
						}else{
							parentIdTemp = isParent(vdeptMapp, parentIdTemp).get("PARENTDEPTCODE").toString();
							if(null != isParent(vdeptMapp, parentIdTemp)){
								idSeqArrayList.add(isParent(vdeptMapp, parentIdTemp).get("DEPTCODE").toString());
								nameSeqArrayList.add(isParent(vdeptMapp, parentIdTemp).get("DEPTNAME").toString());
							}else{
								break;
							}
						}
					}
					if(null != parentIdTemp){
						level++;
					}else{
						break;
					}
				}
			}
		}else{
			idSeqArrayList.add(deptCode);
			nameSeqArrayList.add(deptName);
			companyId = deptCode;
		}
		
		if(nameSeqArrayList.size() != 1){
			for (int i = nameSeqArrayList.size(); i > 0; i--) {
				nameSeq += nameSeqArrayList.get(i-1) + ">";
				if(nameSeqArrayList.get(i-1).indexOf("有限公司") != -1){
					companyCount = i;
				}if(nameSeqArrayList.get(i-1).indexOf("工贸公司") != -1){
					companyCount = i;
				}if(nameSeqArrayList.get(i-1).indexOf("工业公司") != -1){
					companyCount = i;
				}if(nameSeqArrayList.get(i-1).indexOf("科贸分公司") != -1){
					companyCount = i;
				}if(nameSeqArrayList.get(i-1).indexOf("有限责任公司") != -1){
					companyCount = i;
				}if(nameSeqArrayList.get(i-1).indexOf("军代表") != -1){
					companyCount = i;
				}if(nameSeqArrayList.get(i-1).indexOf("黎天木业") != -1){
					companyCount = i;
				}if(nameSeqArrayList.get(i-1).indexOf("临时部门") != -1){
					companyCount = i;
				}
			}
		}else{
			for (int i = nameSeqArrayList.size(); i > 0; i--) {
				nameSeq += nameSeqArrayList.get(i-1);
			}
		}
		
		for (int i = idSeqArrayList.size(); i > 0; i--) {
			idSeq += "." + idSeqArrayList.get(i-1);
			if(companyCount != 0){
				if(companyCount == i){
					companyId = idSeqArrayList.get(i-1);
				}
			}else{
				if(i == idSeqArrayList.size()){
					companyId = idSeqArrayList.get(i-1);
				}
			}
		}
		
		idSeq += ".";
		if(nameSeq.indexOf(">") != -1){
			nameSeq += ",";
			nameSeq = nameSeq.replace(">,", "");
		}
		if(StringUtils.isNotBlank(deptMap.get("ORDERBY").toString())){
			order = Integer.parseInt(deptMap.get("ORDERBY").toString());
		}
		if(null == vdeptParentMapp.get(deptCode)){
			isLeaf = true;
		}else{
			isLeaf = false;
		}
		
		organization.setId(Identities.uuid());//临时表id随机
		organization.setOrgType(deptType);
		if(!noExitParent){
			TmpSysOrganization parentOrg = new TmpSysOrganization();
			parentOrg.setId(parentDeptCode);
			organization.setParentOrg(parentOrg);
		}else{
			organization.setParentOrg(null);
		}
		//如果只导入部分机构（其中上级机构已经存在机构实体表中），保存上级id字符串，防止报错
		organization.setParentName(parentDeptCode);
		organization.setOrgCode(deptCode);
		organization.setOrgName(deptName);
		organization.setOrgLevel(level);
		organization.setSn(order);
		organization.setIdSeq(idSeq);
		organization.setNameSeq(nameSeq);
		organization.setIsLeaf(isLeaf);
		TmpSysOrganization company = new TmpSysOrganization();
		company.setId(companyId);
		organization.setCompany(company);
		organization.setCompanyName(companyId);
		organization.setRowLine(rowLine);
		
		return organization;
	}
	
	/**
	 * 得到当前对象
	 * */
	private static Map<String, Object> isParent(HashMap<String, Map<String, Object>> vdeptMapp, String parentDeptCode){
		if(null != vdeptMapp.get(parentDeptCode)){
			return vdeptMapp.get(parentDeptCode);
		}else{
			return null;
		}
	}
	/**
	 * 校验信息查询
	 * add by 王再冉
	 * 2014-4-23  下午1:25:10
	 * desc : 
	 * @return 
	 * List<Map<String,Object>>
	 */
	public Map<String, Object> findAllTmpSysOrganizations(String query){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> orgList = new ArrayList<Map<String,Object>>();
		Boolean errorInfo = true;
		List<TmpSysOrganization> tmpOrgList = this.findAllTmpSysOrgs(query);
		for(TmpSysOrganization tmpOrg : tmpOrgList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", tmpOrg.getOrgCode());
			map.put("orgName", tmpOrg.getOrgName());
			map.put("orgType", tmpOrg.getOrgType());
			map.put("orgLevel", tmpOrg.getOrgLevel());
			map.put("sn", tmpOrg.getSn());
			map.put("rowLine", tmpOrg.getRowLine());
			map.put("error", tmpOrg.getError());
			if(StringUtils.isNotBlank(tmpOrg.getError())){
				errorInfo = false;
			}
			orgList.add(map);
		}
		resultMap.put("errorInfo", errorInfo);//错误信息
		resultMap.put("datas", orgList);//搜索框结果集（只能显示‘datas中的数据’）
		return resultMap;
	}
	
	/**
	 * 查询全部机构临时数据
	 * add by 王再冉
	 * 2014-4-23  下午4:13:20
	 * desc : 
	 * @return 
	 * List<TmpImpOrganization>
	 */
	@SuppressWarnings("unchecked")
	public List<TmpSysOrganization> findAllTmpSysOrgs(String query){
		Criteria createCriteria = o_tmpSysOrganizationDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			createCriteria.add(Restrictions.like("orgName",query,MatchMode.ANYWHERE));
		}
		createCriteria.addOrder(Order.asc("rowLine"));
		return createCriteria.list();
	}
	/**
	 * 查询所有机构临时表，验证岗位所属机构是否存在
	 * add by 王再冉
	 * 2014-4-25  下午5:18:43
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String,Object> findAllTmpOrgMap(){
		Map<String,Object> map = new HashMap<String, Object>();
		List<TmpSysOrganization> tmporgList = this.findAllTmpSysOrgs(null);
		for(TmpSysOrganization org : tmporgList){
			map.put(org.getOrgCode(), org.getOrgName());//临时表中orgCode为最终orgID
		}
		return map;
	}
	/**
	 * 导入机构实体表
	 * add by 王再冉
	 * 2014-4-23  下午4:25:04
	 * desc :  
	 * void
	 */
	public void saveAllOrgsFromTmpOrg(){
		List<TmpSysOrganization> tmpOrgs = this.findAllTmpSysOrgs(null);
		List<SysOrganization> saveOrgList = new ArrayList<SysOrganization>();//需要导入的机构实体
		List<String> parentIds = new ArrayList<String>();//上级机构id，需要更新叶子节点信息
		for(TmpSysOrganization tmporg : tmpOrgs){
			SysOrganization org = new SysOrganization();
			org.setId(tmporg.getOrgCode());
			org.setOrgcode(tmporg.getOrgCode());
			org.setOrgname(tmporg.getOrgName());
			org.setOrgLevel(tmporg.getOrgLevel());
			org.setOrgType(tmporg.getOrgType());
			if(null != tmporg.getParentName()){
				org.setParentOrg(new SysOrganization(tmporg.getParentName()));
				parentIds.add(tmporg.getParentName());
			}else{
				org.setParentOrg(null);
			}
			org.setSn(tmporg.getSn());
			org.setOrgseq(tmporg.getIdSeq());
			org.setIsLeaf(tmporg.getIsLeaf());
			org.setNameSeq(tmporg.getNameSeq());
			if(null != tmporg.getCompanyName()){
				org.setCompany(new SysOrganization(tmporg.getCompanyName()));
			}
			org.setDeleteStatus(Contents.STATUS_NORMAL);
			org.setOrgStatus(Contents.STATUS_NORMAL);
			saveOrgList.add(org);
		}
		o_orgGridBO.saveOrgsSome(saveOrgList);
		this.updateParentOrgIsLeaf(parentIds);
		//删除临时表数据
		this.deleteTmpSysOrganizationsSQL();
	}
	/**
	 * 全部导入
	 * add by 王再冉
	 * 2014-4-25  下午3:42:21
	 * desc :  
	 * void
	 */
	public void importAllFromTmp(){
		this.saveAllOrgsFromTmpOrg();//导入机构
		o_tmpImpRoleBO.saveAllRolesFromTmpRole();//导入角色数据
		o_tmpImpPositionBO.saveAllPositionsFromTmpPosi();//导入岗位
		
		//员工
		List<TempEmpExcelData> list = o_tempEmpExcelDataBO.findTempEmp(null);
		ArrayList<SysUser> userList = o_tempEmpExcelDataBO.getAdjustUser(list);
		ArrayList<SysEmployee> empList = o_tempEmpExcelDataBO.getAdjustEmp(list);
		
		o_tempEmpExcelDataBO.saveUser(userList);
		o_tempEmpExcelDataBO.saveEmp(empList);
		//员工机构
		List<TempEmpOrgExcelData> orgList = o_tempEmpOrgExcelDataBO.findTempEmp(null);
		ArrayList<SysEmpOrg> userOrgList = o_tempEmpOrgExcelDataBO.getAdjustUser(orgList);
		o_tempEmpOrgExcelDataBO.saveEmpOrg(userOrgList);
		
		
		//员工角色
		List<TempEmpRoleExcelData> roleList = o_tempEmpRoleExcelDataBO.findTempEmp(null);
		List<String> roleLists = o_tempEmpRoleExcelDataBO.getEmpRole(roleList);
		o_tempEmpRoleExcelDataBO.saveEmpOrg(roleLists);
		
		
		//员工岗位
		List<TempEmpPostExcelData> postList = o_tempEmpPostExcelDataBO.findTempEmp(null);
		List<SysEmpPosi> empPosiList = o_tempEmpPostExcelDataBO.getEmpPosi(postList);
		o_tempEmpPostExcelDataBO.saveEmpPost(empPosiList);
	}
	/**
	 * 查询所有机构id和编号对应map
	 * add by 王再冉
	 * 2014-5-23  下午3:38:06
	 * desc : 
	 * @return 
	 * Map<String,String>
	 */
	public Map<String,String> findAllOrgCodeAndIdMap(){
		Map<String,String> map = new HashMap<String, String>();
		List<SysOrganization> orgList = o_orgGridBO.findAllOrganizations();
		if(null != orgList){
			for(SysOrganization org : orgList){
				map.put(org.getOrgcode(), org.getId());
			}
		}
		return map;
	}
	/**
	 * 更新上级机构叶子节点信息
	 * add by 王再冉
	 * 2014-5-23  下午5:02:08
	 * desc : 
	 * @param idsArrayList 
	 * void
	 */
	@Transactional
	public void updateParentOrgIsLeaf(List<String> idsArrayList){
		SQLQuery sqlQuery = o_sysOrgDAO.createSQLQuery(" update t_sys_organization set IS_LEAF='0' where ID in (:ids) ");
		sqlQuery.setParameterList("ids", idsArrayList);
		sqlQuery.executeUpdate();
	}

}
