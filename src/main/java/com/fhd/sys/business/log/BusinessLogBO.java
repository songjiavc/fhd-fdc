package com.fhd.sys.business.log;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.log.BusinessLogDAO;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.log.BusinessLog;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.auth.SysUserBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

/**
 * 业务日志BO类.
 * @author 吴德福
 * @version V1.0 创建时间：2013-10-12
 * Company FirstHuiDa.
 */
@Service
@SuppressWarnings("unchecked")
public class BusinessLogBO {

	@Autowired
	private BusinessLogDAO o_businessLogDAO;
	@Autowired
	private SysUserBO o_sysUserBO;
	@Autowired
	private EmployeeBO o_employeeBO;
	@Autowired
	private OrganizationBO o_organizationBO;
	
	/**
	 * 新增业务日志.
	 * @author 吴德福
	 * @param businessLog 业务日志.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public void saveBusinessLog(BusinessLog businessLog) {
		o_businessLogDAO.merge(businessLog);
	}
	/**
	 * 删除业务日志.
	 * @author 吴德福
	 * @param id 业务日志id.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public void removeBusinessLog(String id) {
		o_businessLogDAO.delete(id);
	}
	/**
	 * 根据id查询业务日志.
	 * @author 吴德福
	 * @param id 业务日志id.
	 * @return BusinessLog 业务日志.
	 * @since fhd　Ver 1.1
	 */
	public BusinessLog findBusinessLogById(String id) {
		return o_businessLogDAO.get(id);
	}
	/**
	 * 根据查询条件分页查询业务日志.
	 * @param page
	 * @param sort
	 * @param query
	 * @return Page<BusinessLog>
	 */
	public Page<BusinessLog> findBusinessLogByPage(Page<BusinessLog> page, String sort, String query){
		DetachedCriteria dc = DetachedCriteria.forClass(BusinessLog.class);
		dc.createAlias("sysUser", "sysUser", CriteriaSpecification.LEFT_JOIN);
		dc.createAlias("sysOrganization", "sysOrganization", CriteriaSpecification.LEFT_JOIN);
		if(StringUtils.isNotBlank(query)){
			dc.add(Restrictions.or(Restrictions.or(Property.forName("sysOrganization.orgname").like(query, MatchMode.ANYWHERE), Property.forName("moduleName").like(query, MatchMode.ANYWHERE)), Restrictions.or(Property.forName("ip").like(query, MatchMode.ANYWHERE), Property.forName("sysUser.username").like(query, MatchMode.ANYWHERE))));
		}
		dc.addOrder(Order.desc("operateTime"));
		dc.addOrder(Order.asc("moduleName"));
		dc.addOrder(Order.asc("sysUser.id"));
		return o_businessLogDAO.findPage(dc, page, false);
	}
	/**
	 * 根据登录用户查询用户所属公司的业务日志.
	 * @author 吴德福
	 * @param page 分页信息.
	 * @return Page<BusinessLog> 业务日志集合.
	 * @since fhd　Ver 1.1
	 */
	public Page<BusinessLog> findBusinessLogByOrgId(Page<BusinessLog> page, String orgId) {
		DetachedCriteria dc = DetachedCriteria.forClass(BusinessLog.class);
		dc.createAlias("sysOrganization", "o", CriteriaSpecification.LEFT_JOIN);
		dc.add(Restrictions.eq("o.id", orgId));
		dc.addOrder(Order.desc("operateTime"));
		dc.addOrder(Order.asc("moduleName"));
		dc.addOrder(Order.asc("sysUser.id"));
		return o_businessLogDAO.findPage(dc, page, false);
	}
	/**
	 * 新增模块记录日志.
	 * <pre>
	 * 吴德福在2010-8-30新增权限成功。
	 * </pre>
	 * @author 吴德福
	 * @param operateType 操作类型：新增
	 * @param moduleName 模块名称.
	 * @param isSuccess 操作结果.
	 * @return Boolean 记录日志是否成功.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public Boolean saveBusinessLogInterface(String operateType, String moduleName, String isSuccess, String... params) {
		BusinessLog businessLog = new BusinessLog();
		businessLog.setId(Identities.uuid2());
		SysUser sysUser = null;
		SysOrganization sysOrganization = null;
		if(null != UserContext.getUser()){
			sysUser = o_sysUserBO.get(UserContext.getUser().getUserid());
			if(StringUtils.isNotBlank(UserContext.getUser().getCompanyid())){
				sysOrganization = new SysOrganization();
				sysOrganization.setId(UserContext.getUser().getCompanyid());
			}
			businessLog.setIp(UserContext.getUser().getRemoteIp());
		}else{
			businessLog.setIp("127.0.0.1");
			//设置系统用户
			sysUser = o_sysUserBO.getByUsername("admin");
			sysOrganization = o_organizationBO.getRootOrg();
		}
		businessLog.setSysUser(sysUser);
		if(null != sysOrganization){
			businessLog.setSysOrganization(sysOrganization);
		}
		businessLog.setOperateTime(new Date());
		businessLog.setOperateType(operateType);
		businessLog.setModuleName(moduleName);
		businessLog.setIsSuccess(isSuccess);
		StringBuilder record = new StringBuilder();
		if(null != sysUser){
			record.append(sysUser.getRealname());
		}else{
			record.append("admin");
		}
		record.append("在"
			+ DateUtils.formatDate(businessLog.getOperateTime(),"yyyy-MM-dd hh:mm:ss") 
			+ "通过ip为" + businessLog.getIp() + "的主机"
			+ businessLog.getOperateType()
			+ businessLog.getModuleName()
			+ businessLog.getIsSuccess());
		if(null != params && params.length > 0){
			record.append("，参数为：");
			for (int i = 0; i < params.length; i++) {
				record.append(params[i]);
				if (i != params.length - 1) {
					record.append(",");
				}
			}
		}

		businessLog.setOperateRecord(record.toString());
		o_businessLogDAO.merge(businessLog);
		return true;
	}
	/**
	 * 修改模块记录日志.
	 * <pre>
	 * 吴德福在2010-8-30修改权限记录id:'402881b22ac1c54f012ac1c7a47f0001'成功。
	 * </pre>
	 * @author 吴德福
	 * @param operateType 操作类型：修改.
	 * @param moduleName 模块名称.
	 * @param id 修改的记录id.
	 * @param isSuccess 操作结果.
	 * @return boolean 记录日志是否成功.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public Boolean modBusinessLogInterface(String operateType, String moduleName, String isSuccess, String id, String... params) {
		BusinessLog businessLog = new BusinessLog();
		businessLog.setId(Identities.uuid2());
		SysUser sysUser = null;
		SysOrganization sysOrganization = null;
		if(null != UserContext.getUser()){
			sysUser = o_sysUserBO.get(UserContext.getUser().getUserid());
			if(StringUtils.isNotBlank(UserContext.getUser().getCompanyid())){
				sysOrganization = new SysOrganization();
				sysOrganization.setId(UserContext.getUser().getCompanyid());
			}
			businessLog.setIp(UserContext.getUser().getRemoteIp());
		}else{
			businessLog.setIp("127.0.0.1");
			//设置系统用户
			sysUser = o_sysUserBO.getByUsername("admin");
			sysOrganization = o_organizationBO.getRootOrg();
		}
		businessLog.setSysUser(sysUser);
		if(null != sysOrganization){
			businessLog.setSysOrganization(sysOrganization);
		}
		businessLog.setOperateTime(new Date());
		businessLog.setOperateType(operateType);
		businessLog.setModuleName(moduleName);
		businessLog.setIsSuccess(isSuccess);
		StringBuilder record = new StringBuilder();
		if(null != sysUser){
			record.append(sysUser.getRealname());
		}else{
			record.append("admin");
		}
		record.append("在"
			+ DateUtils.formatDate(businessLog.getOperateTime(),"yyyy-MM-dd hh:mm:ss") 
			+ "通过ip为" + businessLog.getIp() + "的主机"
			+ businessLog.getOperateType()
			+ businessLog.getModuleName() 
			+ "记录id:'" + id + "'" + businessLog.getIsSuccess());
		if(null != params && params.length > 0){
			record.append("，参数为：");
			for (int i = 0; i < params.length; i++) {
				record.append(params[i]);
				if (i != params.length - 1) {
					record.append(",");
				}
			}
		}
		businessLog.setOperateRecord(record.toString());
		o_businessLogDAO.merge(businessLog);
		return true;
	}
	/**
	 * 删除模块记录日志.
	 * <pre>
	 * 吴德福在2010-8-30删除权限记录id:'402881b22ac1c54f012ac1c7a47f0001'成功。
	 * 吴德福在2010-8-30删除权限记录id:'402881b22ac1c54f012ac1c7a47f0002'成功。
	 * 吴德福在2010-8-30删除权限记录id:'402881b22ac1c54f012ac1c7a47f0003'成功。
	 * </pre>
	 * @author 吴德福
	 * @param operateType 操作类型：删除.
	 * @param moduleName 模块名称.
	 * @param ids 删除的记录id数组.
	 * @param isSuccess 操作结果.
	 * @return boolean 记录日志是否成功.
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public Boolean delBusinessLogInterface(String operateType, String moduleName, String isSuccess, String id) {
		BusinessLog businessLog = null;
		businessLog = new BusinessLog();
		businessLog.setId(Identities.uuid2());
		SysUser sysUser = null;
		SysOrganization sysOrganization = null;
		if(null != UserContext.getUser()){
			sysUser = o_sysUserBO.get(UserContext.getUser().getUserid());
			if(StringUtils.isNotBlank(UserContext.getUser().getCompanyid())){
				sysOrganization = new SysOrganization();
				sysOrganization.setId(UserContext.getUser().getCompanyid());
			}
			businessLog.setIp(UserContext.getUser().getRemoteIp());
		}else{
			businessLog.setIp("127.0.0.1");
			//设置系统用户
			sysUser = o_sysUserBO.getByUsername("admin");
			sysOrganization = o_organizationBO.getRootOrg();
		}
		businessLog.setSysUser(sysUser);
		if(null != sysOrganization){
			businessLog.setSysOrganization(sysOrganization);
		}
		businessLog.setOperateTime(new Date());
		businessLog.setOperateType(operateType);
		businessLog.setModuleName(moduleName);
		businessLog.setIsSuccess(isSuccess);
		StringBuilder record = new StringBuilder();
		if(null != sysUser){
			record.append(sysUser.getRealname());
		}else{
			record.append("admin");
		}
		record.append("在"+ DateUtils.formatDate(businessLog.getOperateTime(),"yyyy-MM-dd hh:mm:ss") 
			+ "通过ip为" + businessLog.getIp() + "的主机"
			+ businessLog.getOperateType()
			+ businessLog.getModuleName() 
			+ "记录id:'" + id + "'"+ businessLog.getIsSuccess());
		businessLog.setOperateRecord(record.toString());
		o_businessLogDAO.merge(businessLog);
		return true;
	}

	/**
	 * 查询模块记录日志.
	 * <pre>
	 * 吴德福在2010-8-30查询权限成功。
	 * </pre>
	 * @author 吴德福
	 * @param operateType 操作类型：查询.
	 * @param moduleName 模块名称.
	 * @param isSuccess 操作结果.
	 * @return boolean 记录日志是否成功.
	 * @since fhd　Ver 1.1
	 */
	public Boolean findBusinessLogInterface(String operateType, String moduleName, String isSuccess) {
		BusinessLog businessLog = new BusinessLog();
		businessLog.setId(Identities.uuid2());
		SysUser sysUser = null;
		SysOrganization sysOrganization = null;
		if(null != UserContext.getUser()){
			sysUser = o_sysUserBO.get(UserContext.getUser().getUserid());
			if(StringUtils.isNotBlank(UserContext.getUser().getCompanyid())){
				sysOrganization = new SysOrganization();
				sysOrganization.setId(UserContext.getUser().getCompanyid());
			}
			businessLog.setIp(UserContext.getUser().getRemoteIp());
		}else{
			businessLog.setIp("127.0.0.1");
			//设置系统用户
			sysUser = o_sysUserBO.getByUsername("admin");
			sysOrganization = o_organizationBO.getRootOrg();
		}
		businessLog.setSysUser(sysUser);
		if(null != sysOrganization){
			businessLog.setSysOrganization(sysOrganization);
		}
		businessLog.setOperateTime(new Date());
		businessLog.setOperateType(operateType);
		businessLog.setModuleName(moduleName);
		businessLog.setIsSuccess(isSuccess);
		StringBuilder record = new StringBuilder();
		if(null != sysUser){
			record.append(sysUser.getRealname());
		}else{
			record.append("admin");
		}
		record.append("在"
			+ DateUtils.formatDate(businessLog.getOperateTime(),"yyyy-MM-dd hh:mm:ss") 
			+ "通过ip为" + businessLog.getIp() + "的主机"
			+ businessLog.getOperateType()
			+ businessLog.getModuleName()
			+ businessLog.getIsSuccess());
		businessLog.setOperateRecord(record.toString());
		saveBusinessLog(businessLog);
		return true;
	}

	/**
	 * 查询指定用户的操作日志.
	 * <pre>
	 * 吴德福在2010-8-30查询用户成功。
	 * 吴德福在2010-8-30查角色成功。
	 * 吴德福在2010-8-30查询权限成功。
	 * 吴德福在2010-8-30查询模块成功。
	 * </pre>
	 * @author 吴德福
	 * @param userId 用户id.
	 * @since fhd　Ver 1.1
	 */
	public List<BusinessLog> findBusinessLogByUserId(String userId) {
		return o_businessLogDAO.findBy("sysUser.id", userId);
	}

	/**
	 * 查询指定时间段内的操作日志.
	 * <pre>
	 * 吴德福在2010-8-30查询用户成功。
	 * 吴德福在2010-8-30查角色成功。
	 * 吴德福在2010-8-30查询权限成功。
	 * 吴德福在2010-8-30查询模块成功。
	 * </pre>
	 * @author 吴德福
	 * @param userId 用户id.
	 * @since fhd　Ver 1.1
	 */
	public List<BusinessLog> findBushinessLogByOperateTime(Date beginTime, Date endTime) {
		Criteria criteria = o_businessLogDAO.createCriteria();
		criteria.createAlias("sysOrganization", "o", CriteriaSpecification.LEFT_JOIN);
		if(null != beginTime){
			criteria.add(Restrictions.ge("operateTime", beginTime));
		}
		if(null != endTime){
			criteria.add(Restrictions.le("operateTime", endTime));
		}
		criteria.addOrder(Order.desc("operateTime"));
		criteria.addOrder(Order.asc("moduleName"));
		criteria.addOrder(Order.asc("sysUser.id"));
		return criteria.list();
	}

	/**
	 * 添加指定人员的操作日志
	 * @author David
	 * @param operatorId
	 * @param operateType
	 * @param moduleName
	 * @param isSuccess
	 * @param id
	 * @param params
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public Boolean addModBusinessLog(String operatorId,String operateType,
			String moduleName, String isSuccess, String id, String... params) {
		BusinessLog businessLog = new BusinessLog();
		businessLog.setId(Identities.uuid2());
		SysUser sysUser = new SysUser();
		sysUser.setId(operatorId);
		businessLog.setSysUser(sysUser);
		SysEmployee employee = o_employeeBO.getEmployee(sysUser.getId());
		StringBuilder record = new StringBuilder();
		if (null != employee) {
			businessLog.setSysOrganization(employee.getSysOrganization());
			record.append(employee.getEmpname());
		}
		businessLog.setOperateTime(new Date());
		businessLog.setOperateType(operateType);
		businessLog.setModuleName(moduleName);
		businessLog.setIsSuccess(isSuccess);
		record.append("在"
				+ DateUtils.formatDate(businessLog.getOperateTime(),"yyyy-MM-dd hh:mm:ss") 
				+ businessLog.getOperateType()
				+ businessLog.getModuleName() + "记录id:'" + id
				+ "'" + businessLog.getIsSuccess());
		if(null != params && params.length > 0){
			record.append("，参数为：");
			for (int i = 0; i < params.length; i++) {
				record.append(params[i]);
				if (i != params.length - 1) {
					record.append(",");
				}
			}
		}
		businessLog.setOperateRecord(record.toString());
		o_businessLogDAO.merge(businessLog);
		return true;
	}
	/**
	 * 调用日志拦截器记录模块日志.
	 * @author 吴德福
	 * @param operateType 操作类型：新增
	 * @param moduleName 模块名称.
	 * @param isSuccess 操作结果.
	 * @param 类名
	 * @param 方法名
	 * @param params 参数
	 * @return Boolean 记录日志是否成功.
	 * @Date 2013-11-04 10:00:03
	 * @since fhd　Ver 1.1
	 */
	@Transactional
	public Boolean saveBusinessLogByLogAroundInterceptor(String operateType, String moduleName, String isSuccess, String className, String methodName,String params) {
		BusinessLog businessLog = new BusinessLog();
		businessLog.setId(Identities.uuid2());
		SysUser sysUser = null;
		SysOrganization sysOrganization = null;
		if(null != UserContext.getUser()){
			sysUser = o_sysUserBO.get(UserContext.getUser().getUserid());
			if(StringUtils.isNotBlank(UserContext.getUser().getCompanyid())){
				sysOrganization = new SysOrganization();
				sysOrganization.setId(UserContext.getUser().getCompanyid());
			}
			businessLog.setIp(UserContext.getUser().getRemoteIp());
		}else{
			businessLog.setIp("127.0.0.1");
			//设置系统用户
			sysUser = o_sysUserBO.getByUsername("admin");
			sysOrganization = o_organizationBO.getRootOrg();
		}
		businessLog.setSysUser(sysUser);
		if(null != sysOrganization){
			businessLog.setSysOrganization(sysOrganization);
		}
		businessLog.setOperateTime(new Date());
		businessLog.setOperateType(operateType);
		businessLog.setModuleName(moduleName);
		businessLog.setIsSuccess(isSuccess);
		StringBuilder record = new StringBuilder();
		if(null != sysUser){
			record.append(sysUser.getRealname());
		}else{
			record.append("admin");
		}
		record.append("在").append(DateUtils.formatDate(businessLog.getOperateTime(),"yyyy-MM-dd hh:mm:ss"))
			.append("通过ip为").append(businessLog.getIp()).append("的主机").append(businessLog.getOperateType())
			.append(businessLog.getModuleName()).append(businessLog.getIsSuccess()).append(",调用了类'")
			.append(className).append("',执行了'").append(methodName).append("'方法，参数为:").append(params);

		businessLog.setOperateRecord(record.toString());
		o_businessLogDAO.merge(businessLog);
		return true;
	}
}