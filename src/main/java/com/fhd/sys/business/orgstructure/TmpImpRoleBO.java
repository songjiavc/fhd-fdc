package com.fhd.sys.business.orgstructure;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.organization.TmpImpRoleDAO;
import com.fhd.dao.sys.organization.TmpSysRoleDAO;
import com.fhd.entity.sys.auth.SysAuthority;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.TmpImpRole;
import com.fhd.entity.sys.orgstructure.TmpSysRole;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sys.business.auth.AuthorityBO;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.file.FileUploadBO;
/**
 * 
 * ClassName:TmpImpRoleBO 角色导入BO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-30		上午11:33:56
 *
 * @see
 */
@Service
public class TmpImpRoleBO{
	@Autowired
	private TmpImpRoleDAO o_tmpImpRoleDAO;
	@Autowired
	private FileUploadBO o_fileUploadBO;
	@Autowired
	private RoleBO o_roleBO;
	@Autowired
	private AuthorityBO o_authorityBO;
	@Autowired
	private TmpSysRoleDAO o_tmpSysRoleDAO;
	
	/**
	 * 
	 * validate:验证
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void validate(String fileId) throws Exception{
		this.mergeByFileId(fileId);
		List<TmpImpRole> tmpImpRoles = this.findByFileId(fileId);
		for (TmpImpRole tmpImpRole : tmpImpRoles) {
			StringBuffer error=new StringBuffer("");
			String roleCode = tmpImpRole.getRoleCode();
			String roleName = tmpImpRole.getRoleName();
			String authorityName = tmpImpRole.getAuthorityName();
			if(StringUtils.isBlank(roleCode)&&StringUtils.isBlank(roleName)){
				error.append("未指定角色。");
			}
			if(StringUtils.isBlank(authorityName)){
				error.append("未指定权限。");
			}else{
				List<SysAuthority> authoritys = o_authorityBO.findByName(authorityName);
				if(authoritys.size()==0){
					error.append("未找到该权限。");
				}
				
			}
			tmpImpRole.setError(error.toString());
			o_tmpImpRoleDAO.merge(tmpImpRole);
		}
	}
	/**
	 * 
	 * mergeByFileId:根据文件ID保存
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
		List<List<String>> dataListList = new ReadExcel<String>().readEspecialExcel(byteArrayInputStream, "角色权限");
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
						if("角色编号".equals(data)){
							headMap.put(j,"roleCode");
						}else if("角色名称".equals(data)){
							headMap.put(j,"roleName");
						}else if("权限名称".equals(data)){
							headMap.put(j,"authorityName");
						}
					}
				}
			}else{
				TmpImpRole tmpImpRole = new TmpImpRole();
				String roleCode="";
				String roleName="";
				String authorityName="";
				String error="";
				for (String data : dataList) {
					j++;
					String head = headMap.get(j);
					if(StringUtils.isNotBlank(data)&&StringUtils.isNotBlank(head)){
						data=data.trim();
						if("roleCode".equals(head)){
							roleCode=data;
						}else if("roleName".equals(head)){
							roleName=data;
						}else if("authorityName".equals(head)){
							authorityName=data;
						}
					}
				}
				tmpImpRole.setIndex(String.valueOf(i));
				tmpImpRole.setId(Identities.uuid());
				tmpImpRole.setRoleCode(roleCode);
				tmpImpRole.setRoleName(roleName);
				tmpImpRole.setAuthorityName(authorityName);
				tmpImpRole.setFileUploadEntity(fileUploadEntity);
				tmpImpRole.setError(error);
				o_tmpImpRoleDAO.merge(tmpImpRole);
			}
		}
	}
	/**
	 * 
	 * removeByFileId:根据文件ID删除
	 * 
	 * @author 杨鹏
	 * @param fileId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeByFileId(String fileId){
		String hql="delete from TmpImpRole where fileUploadEntity.id=?";
		o_tmpImpRoleDAO.createQuery(hql, fileId).executeUpdate();
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
	public List<TmpImpRole> findByFileId(String fileId){
		Criteria createCriteria = o_tmpImpRoleDAO.createCriteria();
		if(StringUtils.isNotBlank(fileId)){
			createCriteria.add(Restrictions.eq("fileUploadEntity.id", fileId));
		}
		return createCriteria.list();
	}
	/**
	 * 
	 * findPageBySome:分页查询
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param fileId
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<TmpImpRole> findPageBySome(Page<TmpImpRole> page,String query,String fileId,List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(TmpImpRole.class);
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("roleName", query,MatchMode.ANYWHERE));
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
		
		return o_tmpImpRoleDAO.findPage(criteria, page, false);
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
		List<TmpImpRole> list = this.findByFileId(fileId);
		Integer roleCount = o_roleBO.findCountByAll();
		for (TmpImpRole tmpImpRole : list) {
			String roleCode = tmpImpRole.getRoleCode();
			String roleName = tmpImpRole.getRoleName();
			String authorityName = tmpImpRole.getAuthorityName();
			String error = tmpImpRole.getError();
			/**
			 * 读取角色
			 */
			SysRole role=null;
			if(StringUtils.isBlank(error)){
				if(StringUtils.isNotBlank(roleCode)){
					List<SysRole> roles = o_roleBO.findByRoleCode(roleCode);
					if(roles.size()>0){
						role = roles.get(0);
					}else{
						role= new SysRole();
						role.setId(Identities.uuid());
						role.setRoleCode(roleCode);
						role.setRoleName(roleName);
						role.setSort(roleCount++);
						o_roleBO.save(role);
					}
				}
				
				if(StringUtils.isNotBlank(authorityName)){
					List<SysAuthority> authoritys = o_authorityBO.findByName(authorityName);
					if(authoritys.size()>0){
						SysAuthority authority = authoritys.get(0);
						Set<SysAuthority> authorities = role.getSysAuthorities();
						authorities.add(authority);
						role.setSysAuthorities(authorities);
						o_roleBO.merge(role);
					}
				}
			}
		}
	}
	
/*******************************************新版导入分割线*******************************************************/
	/**
	 * 删除角色临时表全部数据
	 * add by 王再冉
	 * 2014-4-24  上午10:21:37
	 * desc :  
	 * void
	 */
	public void deleteTmpSysRoleSQL(){
		String sql = " delete from TMP_IMP_SYS_ROLE ";
		SQLQuery sqlQuery = o_tmpSysRoleDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
	}
	/**
	 * 保存角色临时表信息
	 * add by 王再冉
	 * 2014-4-24  上午10:45:10
	 * desc : 
	 * @param roleList 
	 * void
	 */
	@Transactional
    public void saveTmpSysRoles(final List<TmpSysRole> roleList) {
        this.o_tmpSysRoleDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into TMP_IMP_SYS_ROLE " +
                		 " (ID,ROLE_CODE,ROLE_NAME,VALIDATE_INFO,ROW_LINE) " + 
                		 " values(?,?,?,?,?) ";
                
                pst = connection.prepareStatement(sql);
                
                for (TmpSysRole role : roleList) {
					pst.setString(1, role.getId());
					pst.setString(2, role.getRoleCode());
					pst.setString(3, role.getRoleName());
					pst.setString(4, role.getValidateInfo());
					pst.setInt(5, role.getRowLine());
					pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	/**
	 * 查询角色临时表全部数据
	 * add by 王再冉
	 * 2014-4-24  上午11:16:47
	 * desc : 
	 * @return 
	 * List<TmpSysRole>
	 */
	@SuppressWarnings("unchecked")
	public List<TmpSysRole> findAllTmpSysRolesSQL(String query){
		Criteria createCriteria = o_tmpSysRoleDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			createCriteria.add(Restrictions.or(Restrictions.like("roleCode",query,MatchMode.ANYWHERE),
					Restrictions.like("roleName",query,MatchMode.ANYWHERE)));
		}
		createCriteria.addOrder(Order.asc("rowLine"));
		return createCriteria.list();
	}
	/**
	 * 读取excel数据，保存角色临时表并验证
	 * add by 王再冉
	 * 2014-4-24  上午10:24:11
	 * desc : 
	 * @param excelDatas
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> importTmpRoleData(List<List<String>> excelDatas){
		//删除临时表数据
		this.deleteTmpSysRoleSQL();
		Boolean flag = true;
		String roleCode = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<TmpSysRole> list = new ArrayList<TmpSysRole>();
		List<String> ExcRoleCodeList = new ArrayList<String>();//excel表中所有角色编号
		List<String> roleIdsAll = o_roleBO.findAllRoleIds();//查询角色表中所有角色id
		if (excelDatas != null && excelDatas.size() > 0) {
			// 依次读取EXCEL第三行以后的数据（前两行为标题与说明）
			for (int row = 2; row < excelDatas.size(); row++){
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				StringBuffer errorInfo = new StringBuffer("");//验证错误信息
				TmpSysRole tmpRole = new TmpSysRole();
				roleCode = rowDatas.get(0);
				tmpRole.setRowLine(row+1);
				tmpRole.setId(Identities.uuid());
				if(StringUtils.isBlank(roleCode)){
					errorInfo.append("角色编号为空;");
					flag = false;
				}else{
					tmpRole.setRoleCode(roleCode);
				}
				if(StringUtils.isBlank(rowDatas.get(1))){
					errorInfo.append("角色名称为空;");
					flag = false;
				}else{
					tmpRole.setRoleName(rowDatas.get(1));
				}
				if(ExcRoleCodeList.contains(roleCode) || roleIdsAll.contains(roleCode)){//角色编号重复
					errorInfo.append("角色编号重复;");
					flag = false;
				}
				tmpRole.setValidateInfo(errorInfo.toString());
				ExcRoleCodeList.add(roleCode);
				list.add(tmpRole);
			}
		}
		//保存角色临时表
		this.saveTmpSysRoles(list);
		resultMap.put("datas", list);
		resultMap.put("errorInfo", flag);
		resultMap.put("success", true);
		return resultMap;
	}
	/**
	 * 查看角色临时表
	 * add by 王再冉
	 * 2014-4-24  上午11:22:49
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findAllTmpSysRolesGrid(String query){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> roleList = new ArrayList<Map<String,Object>>();
		Boolean errorInfo = true;
		List<TmpSysRole> tmpRoleList = this.findAllTmpSysRolesSQL(query);
		for(TmpSysRole tmpRole : tmpRoleList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", tmpRole.getId());
			map.put("roleCode", tmpRole.getRoleCode());
			map.put("roleName", tmpRole.getRoleName());
			map.put("validateInfo", tmpRole.getValidateInfo());
			map.put("rowLine", tmpRole.getRowLine());
			if(StringUtils.isNotBlank(tmpRole.getValidateInfo())){
				errorInfo = false;
			}
			roleList.add(map);
		}
		resultMap.put("errorInfo", errorInfo);//错误信息
		resultMap.put("datas", roleList);//搜索框结果集
		return resultMap;
	}
	/**
	 * 导入角色数据
	 * add by 王再冉
	 * 2014-4-24  下午1:46:38
	 * desc :  
	 * void
	 */
	public void saveAllRolesFromTmpRole(){
		List<TmpSysRole> tmpRoles = this.findAllTmpSysRolesSQL(null);
		List<SysRole> saveRoleList = new ArrayList<SysRole>();//需要导入的机构实体
		for(TmpSysRole tmpRole : tmpRoles){
			SysRole role = new SysRole();
			role.setId(tmpRole.getRoleCode());
			role.setRoleCode(tmpRole.getRoleCode());
			role.setRoleName(tmpRole.getRoleName());
			saveRoleList.add(role);
		}
		o_roleBO.saveRoles(saveRoleList);
		//删除临时表数据
		this.deleteTmpSysRoleSQL();
	}
}
