package com.fhd.icm.business.statics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.icm.statics.IcmMyDatasDAO;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.IsChineseOrNotUtil;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.orgstructure.OrganizationBO;



/**
 * 内控我的数据(包括 我的流程 我的风险 我的制度 我的标准 我的控制 我的任务)
 * @author 邓广义
 * @date 2013-5-15
 * @since  fhd　Ver 1.1
 */
@Service
@SuppressWarnings({"unchecked","deprecation"})
public class IcmMyDatasBO {
	
	@Autowired
	private IcmMyDatasDAO o_icmMyDatasDAO;
	
	@Autowired
	private OrganizationBO o_organizationBO;
	
	private String findDialect(){
		String dialect = ResourceBundle.getBundle("application").getString(
				"hibernate.dialect");
				dialect = StringUtils.lowerCase(StringUtils.substringAfterLast(dialect,
						"."));
		if (StringUtils.indexOf(dialect, "oracle") > -1) {
			dialect = "oracle";
		} else if (StringUtils.indexOf(dialect, "sqlserver") > -1) {
			dialect = "sqlserver";
		} else if (StringUtils.indexOf(dialect, "mysql") > -1) {
			dialect = "mysql";
		} else if (StringUtils.indexOf(dialect, "db2") > -1) {
			dialect = "db2";
		} else if (StringUtils.indexOf(dialect, "h2") > -1) {
			dialect = "h2";
		} else if (StringUtils.indexOf(dialect, "hsql") > -1) {
			dialect = "hsql";
		} else if (StringUtils.indexOf(dialect, "sapdb") > -1) {
			dialect = "sapdb";
		} else if (StringUtils.indexOf(dialect, "sysbase") > -1) {
			dialect = "sysbase";
		}
		return dialect;
	}
	/**
	 * <pre>
	 * 查询末级流程
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param result 保存总数的map
	 * @param orgId 机构ID：公司或部门
	 * @param query 查询条件：查询流程编号或流程名称
	 * @param start 
	 * @param limit
	 * @param sort
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findProcessBySome(Map<String,Object> result, String orgId, String query, String start, String limit, String sort){
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT DISTINCT ");
		sql.append("pro1.ORG_ID, ");//0机构ID
		sql.append("o.ORG_NAME, ");//1机构名称
		sql.append("pro2.EMP_ID, ");//2员工ID
		sql.append("e.EMP_NAME, ");//3员工姓名
		sql.append("pp.PROCESSURE_NAME  PARENT_NAME, ");//4流程分类名称
		sql.append("p.PROCESS_CLASS, ");//5发生频率
		sql.append("p.PROCESSURE_CODE, ");//6流程编号
		sql.append("p.PROCESSURE_NAME, ");//7流程名称
		sql.append("p.ID, ");//8流程ID
		sql.append("p.CREATE_TIME, ");//9创建日期
		sql.append("p.LAST_MODIFY_TIME, ");//10更新日期
		sql.append("e1.DICT_ENTRY_NAME ");//11发生频率文字
		sql.append("FROM T_IC_PROCESSURE p ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.ID=p.PROCESS_CLASS ");
		sql.append("LEFT JOIN T_IC_PROCESSURE_RELA_ORG pro1 ON p.ID=pro1.PROCESSURE_ID AND pro1.ETYPE='OR' ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON pro1.ORG_ID=o.ID ");
		sql.append("LEFT JOIN T_IC_PROCESSURE_RELA_ORG pro2 ON p.ID=pro2.PROCESSURE_ID AND pro2.ETYPE='ER' ");
		sql.append("LEFT JOIN T_SYS_EMPLOYEE e ON pro2.EMP_ID=e.ID ");
		sql.append("LEFT JOIN T_IC_PROCESSURE pp ON pp.ID=p.PARENT_ID ");
		sql.append("WHERE  p.IS_LEAF='1' AND p.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" AND p.COMPANY_ID=?  ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append("AND p.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append("AND pro1.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{//非内控部门 默认取值
			sql.append("AND pro1.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(query)){
			sql.append("AND (p.PROCESSURE_CODE LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR o.ORG_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR e.EMP_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR pp.PROCESSURE_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR e1.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			if(!IsChineseOrNotUtil.isChinese(query)){
				sql.append("OR p.LAST_MODIFY_TIME LIKE ").append("'%").append(query).append("%' ");
				sql.append("OR p.CREATE_TIME LIKE ").append("'%").append(query).append("%' ");
			}
			sql.append("OR p.PROCESSURE_NAME LIKE ").append("'%").append(query).append("%') ");
		}
		sql.append("ORDER BY ");
		if(StringUtils.isNotBlank(sort)){
			sql.append(sort);
		}else{
			sql.append("p.LAST_MODIFY_TIME DESC, ");
			sql.append("p.CREATE_TIME DESC ");
		}
		
		SQLQuery sqlQuery = null;
        if(StringUtils.isNotBlank(param)){
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
        }else{
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString());
        }
		result.put("totalCount", sqlQuery.list().size());
		sqlQuery.setFirstResult(Integer.valueOf(start));
		sqlQuery.setMaxResults(Integer.valueOf(limit));
		return sqlQuery.list();
	}
	/**
	 * <pre>
	 * 查询控制措施
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param result 保存总数的map
	 * @param orgId 机构ID：公司或部门
	 * @param query 查询条件
	 * @param start 开始
	 * @param limit 限制
	 * @param sort 排序
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findControlMeasureBySome(Map<String, Object> result,String orgId,String query,String start,String limit,String sort) {
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT DISTINCT ");
		sql.append("m.ID, ");//0控制ID
		sql.append("m.MEASURE_CODE, ");//1控制编号
		sql.append("m.MEASURE_NAME, ");//2控制名称
		sql.append("o.ID org_ID, ");//3机构ID
		sql.append("o.ORG_NAME, ");//4机构名称
		sql.append("m.CONTROL_MEASURE, ");//5控制方式
		sql.append("e1.DICT_ENTRY_NAME control_measure_name, ");//6控制方式名称
		sql.append("m.implement_proof, ");//7实施证据
		sql.append("m.is_key_control_point, ");//8是否关键控制点
		sql.append("e2.dict_entry_name is_key_control_point_name, ");//9是否关键控制点
		sql.append("m.CREATE_TIME, ");//10创建日期
		sql.append("m.LAST_MODIFY_TIME ");//11最后修改日期
		sql.append("FROM T_CON_CONTROL_MEASURE m ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.id=m.CONTROL_MEASURE ");
		sql.append("LEFT JOIN t_sys_dict_entry e2 ON e2.id=m.is_key_control_point ");
		sql.append("LEFT JOIN T_CON_MEASURE_RELA_ORG mro ON m.ID=mro.CONTROL_MEASURE_ID AND mro.etype='OR' ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON o.ID=mro.org_id ");
		sql.append("WHERE m.DELETE_ESTATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append("AND m.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append("AND m.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append("AND mro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append("AND mro.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(query)){
			sql.append("AND (m.MEASURE_CODE LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR m.MEASURE_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR o.ORG_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR m.IMPLEMENT_PROOF LIKE ").append("'%").append(query).append("%' ");
			if(!IsChineseOrNotUtil.isChinese(query)){
				sql.append("OR m.LAST_MODIFY_TIME LIKE ").append("'%").append(query).append("%' ");
				sql.append("OR m.CREATE_TIME LIKE ").append("'%").append(query).append("%' ");
			}
			sql.append("OR e1.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR e2.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%') ");
		}
		sql.append("ORDER BY ");
		if(StringUtils.isNotBlank(sort)){
			sql.append(sort);
		}else{
			sql.append("m.LAST_MODIFY_TIME DESC, ");
			sql.append("m.CREATE_TIME DESC ");
		}
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		result.put("totalCount", sqlQuery.list().size());
		sqlQuery.setFirstResult(Integer.valueOf(start));
		sqlQuery.setMaxResults(Integer.valueOf(limit));
		return sqlQuery.list();
	}
	/**
	 * 判断当前登录人是否为内控部门
	 * @return boolean
	 */
	public boolean judgeIfIcmDept(){
		Set<SysRole> roles = UserContext.getUser().getSysRoles();
		boolean flag = false;
		for(SysRole role :roles){
			String code = role.getRoleCode();
			if(Contents.IC_DEPARTMENT_LEADER.equals(code)||Contents.IC_DEPARTMENT_MINISTER.equals(code)||Contents.IC_DEPARTMENT_STAFF.equals(code)){
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * <pre>
	 * 查询相关风险信息
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param result 保存总数的map
	 * @param orgId 机构ID：公司或部门
	 * @param processId 流程ID
	 * @param query 查询条件
	 * @param start 开始
	 * @param limit 限制
	 * @param sort 排序
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findRiskControlMatrixBySome(Map<String, Object> result,String orgId,String processId, String query,String start,String limit,String sort) {
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT DISTINCT ");
		sql.append("r.ID RISK_ID, ");//0风险ID
		sql.append("pr.RISK_NAME PARENT_NAME, ");//1风险分类
		sql.append("r.RISK_NAME, ");//2风险名称
		sql.append("p.ID PROCESSURE_ID, ");//3流程ID
		sql.append("p.PROCESSURE_NAME, ");//4流程名称
		sql.append("icp.CONTROL_POINT_NAME, ");//5流程节点
		sql.append("ccm.MEASURE_NAME, ");//6控制措施
		sql.append("e1.DICT_ENTRY_NAME CONTROL_METHOD, ");//7控制方式
		sql.append("e2.DICT_ENTRY_NAME CONTROL_FREQUENCY, ");//8控制频率
		sql.append("org.ORG_NAME, ");//9责任部门,这块取的是风险的部门，郑军祥觉得应该是控制措施的部门
		sql.append("emp.EMP_NAME, ");//10责任人
		sql.append("r.CREATE_TIME, ");//11创建日期
		sql.append("r.LAST_MODIFY_TIME, ");//12更新日期
		sql.append("ccm.ID MEASURE_ID ");//13控制措施ID
		sql.append("FROM ");
		sql.append("T_RM_RISKS r ");
		sql.append("LEFT JOIN T_RM_RISKS pr ON pr.ID = r.PARENT_ID ");
		sql.append("LEFT JOIN t_rm_risk_org rro ON r.ID=rro.RISK_ID ");
		sql.append("LEFT JOIN t_sys_organization org ON org.ID=rro.ORG_ID ");
		sql.append("LEFT JOIN T_PROCESSURE_RISK_PROCESSURE prp ON r.ID=prp.RISK_ID ");
		sql.append("LEFT JOIN T_IC_PROCESSURE p ON p.ID=prp.PROCESSURE_ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_POINT_RELA_RISK cprs ON cprs.RISK_ID=r.ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_POINT icp ON cprs.CONTROL_POINT_ID=icp.ID ");
		sql.append("LEFT JOIN T_CON_MEASURE_RELA_RISK mrr ON mrr.RISK_ID=r.ID ");
		sql.append("LEFT JOIN T_CON_CONTROL_MEASURE ccm ON ccm.ID=mrr.CONTROL_MEASURE_ID ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.id = ccm.CONTROL_MEASURE ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e2 ON e2.id = ccm.CONTROL_FREQUENCY ");
		sql.append("LEFT JOIN T_CON_MEASURE_RELA_ORG cmro ON cmro.CONTROL_MEASURE_ID=ccm.ID ");
		sql.append("LEFT JOIN T_SYS_EMPLOYEE emp ON emp.ID = cmro.EMP_ID ");
		sql.append("WHERE r.DELETE_ESTATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' ");
		sql.append("AND cmro.ETYPE='").append(Contents.EMP_RESPONSIBILITY).append("' ");
		sql.append("AND rro.ETYPE='").append(Contents.MAIN).append("' ");
		
		sql.append("AND prp.ETYPE='I' AND ccm.ID is not NULL ");
		
		// 主责部门由风险部门变为控制措施的主责部门
		//sql.append("AND cmro.ETYPE='").append(Contents.ORG_RESPONSIBILITY).append("' ");//添加,数据库是相关部门Contents.EMP_RESPONSIBILITY
		//sql.append("LEFT JOIN t_sys_organization org ON org.ID=cmro.ORG_ID ");//添加
		
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append("AND r.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append("AND r.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append("AND rro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append("AND rro.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(processId)){
			sql.append(" and p.ID = '").append(processId).append("' ");
		}
		if(StringUtils.isNotBlank(query)){
			sql.append("AND (pr.RISK_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR r.RISK_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR p.PROCESSURE_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR icp.CONTROL_POINT_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR ccm.MEASURE_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR p.PROCESSURE_NAME LIKE ").append("'%").append(query).append("%' ");
			
			if(!IsChineseOrNotUtil.isChinese(query)){
				sql.append("OR r.LAST_MODIFY_TIME LIKE ").append("'%").append(query).append("%' ");
				sql.append("OR r.CREATE_TIME LIKE ").append("'%").append(query).append("%' ");
			}
			sql.append("OR emp.EMP_NAME LIKE ").append("'%").append(query).append("%') ");
		}
		sql.append("ORDER BY ");
		if(StringUtils.isNotBlank(sort)){
			sql.append(sort);
		}else{
			sql.append("r.LAST_MODIFY_TIME DESC, ");
			sql.append("r.CREATE_TIME DESC ");
		}
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		result.put("totalCount", sqlQuery.list().size());
		sqlQuery.setFirstResult(Integer.valueOf(start));
		sqlQuery.setMaxResults(Integer.valueOf(limit));
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 查询内控标准的数据
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param result 保存总数的map
	 * @param orgId 机构ID：公司或部门
	 * @param query 查询条件
	 * @param start 起始数
	 * @param limit 限制数
	 * @param sort 排序
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findStandardBySome(Map<String, Object> result, String orgId, String query, String start, String limit, String sort) {
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT DISTINCT ");
		sql.append("s.ID, ");//0内控要求ID
		sql.append("s.STANDARD_CODE, ");//1内控要求编号
		sql.append("s.STANDARD_NAME, ");//2内控要求名称
		sql.append("s.CONTROL_LEVEL, ");//3控制层级
		sql.append("e1.DICT_ENTRY_NAME CONTROL_LEVEL_NAME, ");//4控制层级中文
		sql.append("s.CONTROL_POINT, ");//5控制要素
		sql.append("e2.DICT_ENTRY_NAME CONTROL_POINT_NAME, ");//6控制要素中文
		sql.append("s.DEAL_STATUS, ");//7处理状态
		sql.append("e3.DICT_ENTRY_NAME DEAL_STATUS_NAME, ");//8处理状态中文
		sql.append("ps.ID PARENT_ID, ");//9内控标准ID
		sql.append("ps.STANDARD_NAME PARENT_NAME, ");//10内控标准名称
		sql.append("o.ID ORG_ID, ");//11机构ID
		sql.append("o.ORG_NAME, ");//12机构名称
		sql.append("s.CREATE_TIME, ");//13创建日期
		sql.append("s.LAST_MODIFY_TIME ");//14更新日期
		sql.append("FROM T_IC_CONTROL_STANDARD s ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON s.CONTROL_LEVEL=e1.ID ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e2 ON s.CONTROL_POINT=e2.ID ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e3 ON s.DEAL_STATUS=e3.ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_STANDARD ps ON ps.ID=s.PARENT_ID ");
		sql.append("LEFT JOIN T_IC_STANDARD_RELA_ORG sro ON sro.CONTROL_STANDARD_ID=s.ID AND sro.ETYPE='OR' ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON sro.ORG_ID=o.ID ");
		sql.append("WHERE s.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND s.ETYPE='0' ");
		
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append("AND s.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append("AND s.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append("AND sro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append("AND sro.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(query)){
			sql.append("AND (s.STANDARD_CODE LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR s.STANDARD_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR e1.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR e2.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR e3.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR ps.STANDARD_NAME LIKE ").append("'%").append(query).append("%' ");
			if(!IsChineseOrNotUtil.isChinese(query)){
				sql.append("OR s.LAST_MODIFY_TIME LIKE ").append("'%").append(query).append("%' ");
				sql.append("OR s.CREATE_TIME LIKE ").append("'%").append(query).append("%' ");
			}
			sql.append("OR o.ORG_NAME LIKE ").append("'%").append(query).append("%') ");
		}
		sql.append("ORDER BY ");
		if(StringUtils.isNotBlank(sort)){
			sql.append(sort);
		}else{
			sql.append("s.LAST_MODIFY_TIME DESC, ");
			sql.append("s.CREATE_TIME DESC ");
		}
		SQLQuery sqlQuery = null;
        if(StringUtils.isNotBlank(param)){
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
        }else{
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString());
        }
		result.put("totalCount", sqlQuery.list().size());
		sqlQuery.setFirstResult(Integer.valueOf(start));
		sqlQuery.setMaxResults(Integer.valueOf(limit));
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计内控标准的数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID:公司或部门
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findStandardCountBySome(String orgId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		sql.append("o.ID ORG_ID, ");//0机构ID
		sql.append("o.ORG_NAME, ");//1机构名称
		sql.append("ps.ID PARENT_ID, ");//2内控标准ID
		sql.append("ps.STANDARD_NAME PARENT_NAME, ");//3内控标准名称
		sql.append("s.DEAL_STATUS, ");//4处理状态
		sql.append("e3.DICT_ENTRY_NAME DEAL_STATUS_NAME, ");//5处理状态中文
		sql.append("s.CONTROL_POINT, ");//6控制要素
		sql.append("e2.DICT_ENTRY_NAME CONTROL_POINT_NAME, ");//7控制要素中文
		sql.append("s.CONTROL_LEVEL, ");//8控制层级
		sql.append("e1.DICT_ENTRY_NAME CONTROL_LEVEL_NAME, ");//9控制层级中文
		if("mysql".equals(dialect)){
			sql.append("YEAR(s.CREATE_TIME), ");//10创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(s.CREATE_TIME, 'yyyy'), ");//10创建年份
		}
		sql.append("COUNT(s.ID) STANDARD_COUNT ");//11数量
		sql.append("FROM T_IC_CONTROL_STANDARD s ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON s.CONTROL_LEVEL=e1.ID ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e2 ON s.CONTROL_POINT=e2.ID ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e3 ON s.DEAL_STATUS=e3.ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_STANDARD ps ON ps.ID=s.PARENT_ID ");
		sql.append("LEFT JOIN T_IC_STANDARD_RELA_ORG sro ON sro.CONTROL_STANDARD_ID=s.ID AND sro.ETYPE='OR' ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON sro.ORG_ID=o.ID ");
		sql.append("WHERE s.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND s.ETYPE='0' ");
		
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append("AND s.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append("AND s.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append("AND sro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append("AND sro.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		sql.append("GROUP BY "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(s.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(s.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("o.ID, ");
		sql.append("ps.ID, ");
		sql.append("s.DEAL_STATUS, ");
		sql.append("s.CONTROL_POINT, ");
		sql.append("s.CONTROL_LEVEL ");
		sql.append("ORDER BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(s.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(s.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("o.ID, ");
		sql.append("ps.ID, ");
		sql.append("s.DEAL_STATUS, ");
		sql.append("s.CONTROL_POINT, ");
		sql.append("s.CONTROL_LEVEL ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	
	
	/**
	 * 我的制度 ：分类，制度编号，制度名称，责任部门
	 * @param map
	 * @param orgid
	 * @param limit
	 * @param query
	 * @param start
	 * @return List<Object[]>
	 */
	public List<Object[]> findMyInstitutionDatasByOrgId(Map<String, Object> map,String orgid,String limit,String query,String start) {
		StringBuffer sql = new StringBuffer();
		StringBuffer countBuf = new StringBuffer();
		StringBuffer querySql = new StringBuffer();
		List<Object> paralist = new ArrayList<Object>();
		String param = "";
		querySql.append(" SELECT ir.ID,");//id 0 
		querySql.append(" ir.PARENT_ID,");//制度分类1
		querySql.append(" ir.RULE_CODE,");//制度编号2
		querySql.append(" ir.RULE_NAME,");//制度名称3
		querySql.append(" iro.ORG_ID");//责任部门4
		querySql.append(" FROM T_IC_RULE ir ");
		querySql.append(" LEFT JOIN T_IC_RULE_RELA_ORG iro on ir.id=iro.RULE_ID ");
		
		countBuf.append(" SELECT COUNT(*) ");
		countBuf.append(" FROM T_IC_RULE ir ");
		countBuf.append(" LEFT JOIN T_IC_RULE_RELA_ORG iro on ir.id=iro.RULE_ID ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgid)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" WHERE ir.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				
				SysOrganization org = o_organizationBO.get(orgid);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" WHERE ir.COMPANY_ID=? ");
						param = orgid;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" LEFT JOIN T_SYS_ORGANIZATION o on iro.ORG_ID=o.id");
						sql.append(" WHERE iro.ORG_ID=? ");
						param = orgid;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" LEFT JOIN T_SYS_ORGANIZATION o on iro.ORG_ID=o.id");
			sql.append(" WHERE iro.ORG_ID=?");
			param = UserContext.getUser().getMajorDeptId();
		}
		sql.append(" AND ir.IS_LEAF='1'");
		sql.append(" AND iro.ETYPE = 'OR'");
		if(StringUtils.isNotBlank(query)){
			sql.append(" AND ir.RULE_NAME LIKE "+"'%"+query+"%'");
			sql.append(" OR  ir.RULE_CODE LIKE "+"'%"+query+"%'");
		}
		paralist.add(param);
		Object[] paraobjects = new Object[paralist.size()];
		paraobjects = paralist.toArray(paraobjects);
		SQLQuery countQuery = o_icmMyDatasDAO.createSQLQuery(countBuf.append(sql).toString(), paraobjects);
		map.put("totalCount", countQuery.uniqueResult());
		sql.append(" ORDER BY ir.ID ");
		if(!StringUtils.isBlank(limit)){
			//sql.append(" LIMIT 0," + limit);//ENDO limit 为关键字mysql
		}
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(querySql.append(sql).toString(), param);
		sqlQuery.setFirstResult(Integer.valueOf(start));
		sqlQuery.setMaxResults(Integer.valueOf(limit));
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计缺陷数量的情况
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param result 保存总数的map
	 * @param orgId 机构ID
	 * @param planId 评价计划ID
	 * @param iPlanId 整改计划ID
	 * @param processId 流程ID
	 * @param processPointId 流程节点ID
	 * @param measureId 控制措施ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findDefectCountBySome(String orgId, String planId,String iPlanId, String processId){
		
		String dialect = findDialect();
		
		
		StringBuffer sql = new StringBuffer();
		judgeIfIcmDept();
		String param = "";
		sql.append("SELECT "); 
		sql.append("o.ID ORG_ID, ");//0机构ID
		sql.append("o.ORG_NAME, ");//1机构名称
		sql.append("plan.ID PLAN_ID, ");//2评价计划ID
		sql.append("plan.PLAN_NAME, ");//3评价计划名称
		sql.append("p.ID PROCESSURE_ID, ");//4流程ID
		sql.append("p.PROCESSURE_NAME, ");//5流程名称
		sql.append("CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_FINISHED).append("' THEN '已完成' ELSE  ");
		sql.append("(CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_HANDLING).append("' THEN '处理中' ELSE ");
		sql.append("(CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_NOTSTART).append("' THEN '未开始' ELSE ");
		sql.append("'' ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END DEAL_STATUS_NAME, ");//6缺陷整改状态
		sql.append("d.DEFECT_LEVEL, ");//7缺陷等级
		sql.append("d.DEFECT_TYPE, ");//8缺陷类型
		sql.append("COUNT(DISTINCT d.ID) DEFECT_COUNT, ");//9缺陷数量
		sql.append("e1.dict_entry_name DEFECT_LEVEL_NAME, ");//10缺陷等级中文
		sql.append("e2.dict_entry_name DEFECT_TYPE_NAME, ");//11缺陷类型中文	 
		if("mysql".equals(dialect)){//12创建年份
			sql.append("YEAR(d.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(d.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("iplan.ID IPLAN_ID, ");//13整改计划ID
		sql.append("iplan.IMPROVEMENT_NAME ");//14整改计划名称
		sql.append("FROM T_CA_DEFECT d ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.ID=d.DEFECT_LEVEL ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e2 ON e2.ID=d.DEFECT_TYPE ");
		sql.append("LEFT JOIN T_CA_DEFECT_RELA_ORG dro ON d.ID=dro.DEFECT_ID ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON o.id=dro.ORG_ID "); 
		sql.append("LEFT JOIN T_CA_DEFECT_ASSESSMENT a ON d.ID=a.DEFECT_ID "); 
		sql.append("LEFT JOIN T_IC_PROCESSURE p ON p.ID=a.PROCESSURE_ID "); 
		sql.append("LEFT JOIN T_CA_ASSESSMENT_PLAN plan ON plan.ID=a.PLAN_ID "); 
		sql.append("LEFT JOIN T_RECTIFY_DEFECT_IMPROVE_PLAN diplan ON diplan.DEFECT_ID=d.ID "); 
		sql.append("LEFT JOIN T_RECTIFY_IMPROVE iplan ON iplan.ID=diplan.IMPROVE_PLAN_ID "); 
		sql.append("WHERE d.DELETE_STATUS = '").append(Contents.DELETE_STATUS_USEFUL).append("' and dro.ETYPE='OR' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and d.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and d.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and dro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and dro.ORG_ID=?");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(planId)){
			sql.append(" and plan.ID = '").append(planId).append("'");
		}
		if(StringUtils.isNotBlank(iPlanId)){
			sql.append(" and iplan.ID = '").append(iPlanId).append("'");
		}
		
		if(StringUtils.isNotBlank(processId)){
			sql.append(" and p.ID = '").append(processId).append("'");
		}
		
		sql.append(" GROUP BY "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(d.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(d.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("iplan.ID, ");
		sql.append("o.ID,");
		sql.append("plan.ID,");
		sql.append("p.ID,");
		sql.append("d.DEAL_STATUS,");
		sql.append("d.DEFECT_LEVEL,");
		sql.append("d.DEFECT_TYPE ");
		sql.append("ORDER BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(d.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(d.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("iplan.ID, ");
		sql.append("o.ORG_NAME,");
		sql.append("plan.PLAN_NAME,");
		sql.append("p.PROCESSURE_NAME,");
		sql.append("d.DEAL_STATUS,");
		sql.append("d.DEFECT_LEVEL,");
		sql.append("d.DEFECT_TYPE ");
		//SQLQuery query = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		SQLQuery query = null;
		if(StringUtils.isNotBlank(param)){
		    query = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		}else{
		    query = o_icmMyDatasDAO.createSQLQuery(sql.toString());
		}
		return query.list();
	}
	
	/**
	 * <pre>
	 * 统计缺陷的情况
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID
	 * @param query 查询条件
	 * @param planId 评价计划ID
	 * @param processId 流程ID
	 * @param processPointId 流程节点ID
	 * @param measureId 控制措施ID
	 * @param start 起始序号
	 * @param limit 最大序号
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findDefectBySome(Map<String,Object> result, String orgId, String query, String planId, String processId, 
			 String start, String limit, String sort){
		StringBuffer sql = new StringBuffer();
		judgeIfIcmDept();
		String param = "";
		sql.append("SELECT DISTINCT "); 
		sql.append("o.ID ORG_ID,");//0机构ID
		sql.append("o.ORG_NAME,");//1机构名称
		sql.append("plan.ID PLAN_ID, ");//2评价计划ID
		sql.append("plan.PLAN_NAME, ");//3评价计划名称
		sql.append("p.ID PROCESSURE_ID, ");//4流程ID
		sql.append("p.PROCESSURE_NAME, ");//5流程名称
		sql.append("ap.ID ASSESSMENT_POINT_ID, ");//6评价点ID
		sql.append("ap.EDESC ASSESSMENT_POINT_EDESC, ");//7评价点描述
		sql.append("CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_FINISHED).append("' THEN '已完成' ELSE  ");
		sql.append("(CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_HANDLING).append("' THEN '处理中' ELSE ");
		sql.append("(CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_NOTSTART).append("' THEN '未开始' ELSE ");
		sql.append("'' ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END DEAL_STATUS_NAME, ");//8缺陷整改状态
		sql.append("d.DEFECT_LEVEL, ");//9缺陷等级
		sql.append("d.DEFECT_TYPE, ");//10缺陷类型
		sql.append("d.ID, ");//11缺陷ID
		sql.append("d.EDESC, ");//12缺陷描述
		sql.append("e1.DICT_ENTRY_NAME DEFECT_LEVEL_NAME, ");//13缺陷等级中文
		sql.append("e2.DICT_ENTRY_NAME DEFECT_TYPE_NAME, ");//14缺陷类型中文
		sql.append("d.CREATE_TIME, ");//15创建日期
		sql.append("d.LAST_MODIFY_TIME ");//16更新日期
		sql.append("FROM T_CA_DEFECT d ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.ID=d.DEFECT_LEVEL ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e2 ON e2.ID=d.DEFECT_TYPE ");
		sql.append("LEFT JOIN T_CA_DEFECT_RELA_ORG dro ON d.ID=dro.DEFECT_ID ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON o.id=dro.ORG_ID "); 
		sql.append("LEFT JOIN T_CA_DEFECT_ASSESSMENT a ON d.ID=a.DEFECT_ID "); 
		sql.append("LEFT JOIN T_CA_ASSESSMENT_POINT ap ON ap.ID=a.POINT_ID "); 
		sql.append("LEFT JOIN T_IC_PROCESSURE p ON p.ID=a.PROCESSURE_ID "); 
		sql.append("LEFT JOIN T_CA_ASSESSMENT_PLAN plan ON plan.ID=a.PLAN_ID "); 
		sql.append("WHERE d.DELETE_STATUS = '").append(Contents.DELETE_STATUS_USEFUL).append("' and dro.ETYPE='OR' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and d.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and d.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and dro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and dro.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		
		if(StringUtils.isNotBlank(query)){
			sql.append(" AND (o.ORG_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("or CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_FINISHED).append("' THEN '已完成' ELSE  ");
			sql.append("(CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_HANDLING).append("' THEN '处理中' ELSE ");
			sql.append("(CASE WHEN d.DEAL_STATUS='").append(Contents.DEAL_STATUS_NOTSTART).append("' THEN '未开始' ELSE ");
			sql.append("'' ");
			sql.append("END) ");
			sql.append("END) ");
			sql.append("END like ").append("'%").append(query).append("%' ");
			sql.append("OR e1.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR e2.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			if(!IsChineseOrNotUtil.isChinese(query)){
				sql.append("OR d.LAST_MODIFY_TIME LIKE ").append("'%").append(query).append("%' ");
				sql.append("OR d.CREATE_TIME LIKE ").append("'%").append(query).append("%' ");
			}
			
			sql.append("OR d.EDESC LIKE ").append("'%").append(query).append("%') ");
		}
		
		if(StringUtils.isNotBlank(planId)){
			sql.append(" and plan.ID = '").append(planId).append("'");
		}
		
		if(StringUtils.isNotBlank(processId)){
			sql.append(" and p.ID = '").append(processId).append("'");
		}
		
		sql.append("ORDER BY ");
		if(StringUtils.isNotBlank(sort)){
			sql.append(sort);
		}else{
			sql.append("d.LAST_MODIFY_TIME DESC, ");
			sql.append("d.CREATE_TIME DESC ");
		}
		SQLQuery sqlQuery = null;
        if(StringUtils.isNotBlank(param)){
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
        }else{
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString());
        }
		result.put("totalCount", sqlQuery.list().size());
		sqlQuery.setFirstResult(Integer.valueOf(start));
		sqlQuery.setMaxResults(Integer.valueOf(limit));
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 查询评价结果的数量统计
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID
	 * @param query 查询条件
	 * @param planId 评价计划ID
	 * @param processId 流程ID
	 * @param processPointId 流程节点ID
	 * @param measureId 控制措施ID
	 * @param assessPointId 评价点ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findAssessResultCountBySome(String orgId, String planId, String empId, 
			String processId, String processPointId, String measureId, String assessPointId){
		StringBuffer sql = new StringBuffer();
		judgeIfIcmDept();
		String param = "";
		sql.append("SELECT ");
		sql.append("plan.ID PLAN_ID, ");//0评价计划ID
		sql.append("plan.PLAN_NAME, ");//1评价计划名称
		sql.append("e.ID EMP_ID, ");//2员工ID
		sql.append("e.EMP_NAME, ");//3员工姓名
		sql.append("p.ID PROCESS_ID, ");//4流程ID
		sql.append("p.PROCESSURE_NAME, ");//5流程名称
		/*sql.append("c.ID CONTROL_POINT_ID, ");//6流程节点ID
		sql.append("c.CONTROL_POINT_NAME, ");//7流程节点名称
		sql.append("m.ID MEASURE_ID, ");//8控制措施ID
		sql.append("m.MEASURE_NAME, ");//9控制措施名称
		sql.append("ap.ID ASSESSMENT_POINT_ID, ");//10评价点ID
		sql.append("ap.EDESC ASSESSMENT_POINT_EDESC, ");//11评价点名称
*/		sql.append("r.ASSESSMENT_MEASURE, ");//6评价方式
		sql.append("dicte.dict_entry_name, ");//7评价方式中文
		sql.append("CASE WHEN  ");
		sql.append("r.HAS_DEFECT_ADJUST IS NULL ");
		sql.append("THEN ");
		sql.append("(CASE WHEN r.HAS_DEFECT IS NULL THEN NULL ELSE (CASE WHEN r.HAS_DEFECT='0' THEN '无效' ELSE '有效' END)  END)  ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN r.HAS_DEFECT_ADJUST='0' THEN '无效' ELSE '有效' END) ");
		sql.append("END IS_QUALIFIED, ");//8是否有效
		sql.append("CASE WHEN  ");
		sql.append("r.has_defect IS NOT NULL ");
		sql.append("THEN ");
		sql.append("'已完成' ");
		sql.append("ELSE ");
		sql.append("'未完成' ");
		sql.append("END IS_DONE, ");//9是否已完成	
		sql.append("COUNT(DISTINCT r.ID) RESULT_COUNT ");//10评价结果数量
		sql.append("FROM T_CA_ASSESSMENT_RESULT r ");
		sql.append("LEFT JOIN t_sys_dict_entry dicte ON dicte.ID=r.ASSESSMENT_MEASURE ");
		sql.append("LEFT JOIN T_CA_ASSESSMENT_PLAN plan ON plan.ID=r.PLAN_ID ");
		sql.append("LEFT JOIN T_CA_ASSESSMENT_ASSESSOR a ON a.ID=r.ASSESSOR_ID ");
		sql.append("LEFT JOIN T_SYS_EMPLOYEE e ON e.ID=a.OPERATORID ");
		sql.append("LEFT JOIN T_SYS_EMP_ORG eo ON eo.EMP_ID=e.ID ");
		sql.append("LEFT JOIN T_IC_PROCESSURE p ON p.ID=r.PROCESSURE_ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_POINT c ON c.ID=r.CONTROL_POINT_ID ");
		sql.append("LEFT JOIN T_CON_CONTROL_MEASURE m ON m.ID=r.MEASURE_ID ");
		sql.append("LEFT JOIN T_CA_ASSESSMENT_POINT ap ON ap.ID=r.POINT_ID ");
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND plan.ESTATUS='").append(Contents.STATUS_SUBMITTED).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and plan.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and plan.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and eo.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and eo.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		
		if(StringUtils.isNotBlank(planId)){
			sql.append(" and plan.ID = '").append(planId).append("'");
		}
		
		if(StringUtils.isNotBlank(empId)){
			sql.append(" and e.ID = '").append(empId).append("'");
		}
		
		if(StringUtils.isNotBlank(processId)){
			sql.append(" and p.ID = '").append(processId).append("'");
		}
		
		/*if(StringUtils.isNotBlank(processPointId)){
			sql.append(" and c.ID = '").append(processPointId).append("'");
		}
		
		if(StringUtils.isNotBlank(measureId)){
			sql.append(" and m.ID = '").append(measureId).append("'");
		}
		
		if(StringUtils.isNotBlank(assessPointId)){
			sql.append(" and ap.ID = '").append(assessPointId).append("'");
		}*/
		
		sql.append("GROUP BY ");
		sql.append("plan.ID, ");
		sql.append("e.ID, ");
		sql.append("p.ID, ");
		/*sql.append("c.ID, ");
		sql.append("m.ID, ");
		sql.append("ap.ID, ");*/
		sql.append("r.ASSESSMENT_MEASURE, ");
		sql.append("CASE WHEN  ");
		sql.append("r.HAS_DEFECT_ADJUST IS NULL ");
		sql.append("THEN ");
		sql.append("(CASE WHEN r.HAS_DEFECT IS NULL THEN NULL ELSE (CASE WHEN r.HAS_DEFECT='0' THEN '无效' ELSE '有效' END)  END)  ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN r.HAS_DEFECT_ADJUST='0' THEN '无效' ELSE '有效' END) ");
		sql.append("END, ");
		sql.append("CASE WHEN  ");
		sql.append("r.has_defect IS NOT NULL ");
		sql.append("THEN ");
		sql.append("'").append(Contents.DEAL_STATUS_FINISHED).append("' ");
		sql.append("ELSE ");
		sql.append("'未完成' ");
		sql.append("END ");
		sql.append("ORDER BY ");
		sql.append("plan.PLAN_NAME, ");
		sql.append("e.EMP_NAME, ");
		sql.append("p.PROCESSURE_NAME, ");
/*		sql.append("c.CONTROL_POINT_NAME, ");
		sql.append("m.MEASURE_NAME, ");
		sql.append("ap.EDESC, ");*/
		sql.append("r.ASSESSMENT_MEASURE ");
		//SQLQuery query = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		SQLQuery query = null;
		if(StringUtils.isNotBlank(param)){
		    query = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		}else{
		    query = o_icmMyDatasDAO.createSQLQuery(sql.toString());
		}
		return query.list();
	}
	
	/**
	 * <pre>
	 * 查询评价结果的情况
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID
	 * @param query 查询条件
	 * @param planId 评价计划ID
	 * @param processId 流程ID
	 * @param processPointId 流程节点ID
	 * @param measureId 控制措施ID
	 * @param assessPointId 评价点ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findAssessResultBySome(Map<String,Object> result,String orgId, String planId, String empId, 
			String processId, String processPointId, String measureId, String assessPointId, String query, String start, String limit, String sort){
		StringBuffer sql = new StringBuffer();
		judgeIfIcmDept();
		String param = "";
		sql.append("SELECT DISTINCT ");
		sql.append("plan.ID PLAN_ID, ");//0评价计划ID
		sql.append("plan.PLAN_NAME, ");//1评价计划名称
		sql.append("e.ID EMP_ID, ");//2员工ID
		sql.append("e.EMP_NAME, ");//3员工姓名
		sql.append("p.ID PROCESS_ID, ");//4流程ID
		sql.append("p.PROCESSURE_NAME, ");//5流程名称
		sql.append("c.ID CONTROL_POINT_ID, ");//6流程节点ID
		sql.append("c.CONTROL_POINT_NAME, ");//7流程节点名称
		sql.append("m.ID MEASURE_ID, ");//8控制措施ID
		sql.append("m.MEASURE_NAME, ");//9控制措施名称
		sql.append("ap.ID ASSESSMENT_POINT_ID, ");//10评价点ID
		sql.append("ap.EDESC ASSESSMENT_POINT_EDESC, ");//11评价点名称
		sql.append("r.ASSESSMENT_MEASURE, ");//12评价方式
		sql.append("dicte.dict_entry_name ASSESSMENT_MEASURE_NAME, ");//13评价方式中文
		sql.append("CASE WHEN  ");
		sql.append("r.HAS_DEFECT_ADJUST IS NULL ");
		sql.append("THEN ");
		sql.append("(CASE WHEN r.HAS_DEFECT IS NULL THEN NULL ELSE (CASE WHEN r.HAS_DEFECT='0' THEN '无效' ELSE '有效' END)  END)  ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN r.HAS_DEFECT_ADJUST='0' THEN '无效' ELSE '有效' END) ");
		sql.append("END IS_QUALIFIED, ");//14是否有效
		sql.append("CASE WHEN  ");
		sql.append("r.HAS_DEFECT IS NOT NULL ");
		sql.append("THEN ");
		sql.append("'已完成' ");
		sql.append("ELSE ");
		sql.append("'未完成' ");
		sql.append("END IS_DONE, ");//15是否已完成		
		sql.append("plan.CREATE_TIME, ");//16创建日期
		sql.append("plan.LAST_MODIFY_TIME, ");//17更新日期
		sql.append("(SELECT COUNT(DISTINCT s.ID) from T_CA_SAMPLE s where s.ASSESSMENT_POINT_ID=r.ID AND plan.ID=s.PLAN_ID) SAMPLE_COUNT, ");//18样本数量
		sql.append("r.ID ");//19评价结果ID
		sql.append("FROM T_CA_ASSESSMENT_RESULT r ");
		sql.append("LEFT JOIN t_sys_dict_entry dicte ON dicte.ID=r.ASSESSMENT_MEASURE ");
		sql.append("LEFT JOIN T_CA_ASSESSMENT_PLAN plan ON plan.ID=r.PLAN_ID ");
		sql.append("LEFT JOIN t_ca_assessment_assessor a ON a.ID=r.ASSESSOR_ID ");
		sql.append("LEFT JOIN t_sys_employee e ON e.ID=a.OPERATORID ");
		sql.append("LEFT JOIN t_sys_emp_org eo ON eo.EMP_ID=e.ID ");
		sql.append("LEFT JOIN T_IC_PROCESSURE p ON p.ID=r.PROCESSURE_ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_POINT c ON c.ID=r.CONTROL_POINT_ID ");
		sql.append("LEFT JOIN T_CON_CONTROL_MEASURE m ON m.ID=r.MEASURE_ID ");
		sql.append("LEFT JOIN T_CA_ASSESSMENT_POINT ap ON ap.ID=r.POINT_ID ");
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND plan.ESTATUS='").append(Contents.STATUS_SUBMITTED).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and plan.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and plan.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and eo.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and eo.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(query)){
			sql.append(" AND (plan.PLAN_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR p.PROCESSURE_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR c.CONTROL_POINT_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR m.MEASURE_NAME LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR ap.EDESC LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR dicte.DICT_ENTRY_NAME LIKE ").append("'%").append(query).append("%' ");
			if(!IsChineseOrNotUtil.isChinese(query)){
				sql.append("OR plan.LAST_MODIFY_TIME LIKE ").append("'%").append(query).append("%' ");
				sql.append("OR plan.CREATE_TIME LIKE ").append("'%").append(query).append("%' ");
			}
			sql.append("OR CASE WHEN  ");
			sql.append("r.HAS_DEFECT_ADJUST IS NULL ");
			sql.append("THEN ");
			sql.append("(CASE WHEN r.HAS_DEFECT IS NULL THEN NULL ELSE (CASE WHEN r.HAS_DEFECT='0' THEN '无效' ELSE '有效' END)  END)  ");
			sql.append("ELSE ");
			sql.append("(CASE WHEN r.HAS_DEFECT_ADJUST='0' THEN '无效' ELSE '有效' END) ");
			sql.append("END LIKE ").append("'%").append(query).append("%' ");
			sql.append("OR CASE WHEN  ");
			sql.append("r.has_defect IS NOT NULL ");
			sql.append("THEN ");
			sql.append("'").append(Contents.DEAL_STATUS_FINISHED).append("' ");
			sql.append("ELSE ");
			sql.append("'未完成' ");
			sql.append("END LIKE ").append("'%").append(query).append("%') ");
		}
		if(StringUtils.isNotBlank(planId)){
			sql.append(" and plan.ID = '").append(planId).append("'");
		}
		
		if(StringUtils.isNotBlank(empId)){
			sql.append(" and e.ID = '").append(empId).append("'");
		}
		
		if(StringUtils.isNotBlank(processId)){
			sql.append(" and p.ID = '").append(processId).append("'");
		}
		
		if(StringUtils.isNotBlank(processPointId)){
			sql.append(" and c.ID = '").append(processPointId).append("'");
		}
		
		if(StringUtils.isNotBlank(measureId)){
			sql.append(" and m.ID = '").append(measureId).append("'");
		}
		
		if(StringUtils.isNotBlank(assessPointId)){
			sql.append(" and ap.ID = '").append(assessPointId).append("'");
		}
		
		sql.append("ORDER BY ");
		if(StringUtils.isNotBlank(sort)){
			sql.append(sort);
		}else{
			sql.append("plan.LAST_MODIFY_TIME DESC, ");
			sql.append("plan.CREATE_TIME DESC ");
		}
		SQLQuery sqlQuery = null;
        if(StringUtils.isNotBlank(param)){
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
        }else{
            sqlQuery = o_icmMyDatasDAO.createSQLQuery(sql.toString());
        }
		result.put("totalCount", sqlQuery.list().size());
		sqlQuery.setFirstResult(Integer.valueOf(start));
		sqlQuery.setMaxResults(Integer.valueOf(limit));
		return sqlQuery.list();
	}

	/**
	 * <pre>
	 * 统计评价计划的数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param companyId 机构ID:公司
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findAssessPlanCountBySome(String companyId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.ETYPE, ");//1评价类型
		sql.append("e1.DICT_ENTRY_NAME ETYPE_NAME, ");//2评价类型中文
		sql.append("CASE WHEN plan.EXECUTE_STATUS IS NOT NULL THEN ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_NOTSTART).append("' THEN '未开始' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_AFTER_DEADLINE).append("' THEN '逾期' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_HANDLING).append("' THEN '处理中' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_FINISHED).append("' THEN '已完成' ");
		sql.append("ELSE ");
		sql.append("NULL ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END DEAL_STATUS, ");//3处理状态
		sql.append("plan.ASSESSMENT_MEASURE, ");//4评价方式
		sql.append("e2.DICT_ENTRY_NAME ASSESSMENT_MEASURE_NAME, ");//5评价方式中文
		sql.append("count(DISTINCT plan.ID) PLAN_COUNT ");//6评价计划数量
		sql.append("FROM T_CA_ASSESSMENT_PLAN plan ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.ID=plan.ETYPE ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e2 ON e2.ID=plan.ASSESSMENT_MEASURE ");
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND plan.ESTATUS='").append(Contents.STATUS_SUBMITTED).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			sql.append("AND plan.COMPANY_ID=? ");
			if(StringUtils.isBlank(companyId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				param = companyId;
			}
		}
		sql.append("GROUP BY "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.ETYPE, ");
		sql.append("plan.EXECUTE_STATUS, ");
		sql.append("plan.ASSESSMENT_MEASURE ");
		sql.append("ORDER BY  "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.ETYPE, ");
		sql.append("plan.EXECUTE_STATUS, ");
		sql.append("plan.ASSESSMENT_MEASURE ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计整改计划的数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param companyId 机构ID:公司
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findImproveCountBySome(String companyId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("CASE WHEN plan.EXECUTE_STATUS IS NOT NULL THEN ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_NOTSTART).append("' THEN '未开始' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_AFTER_DEADLINE).append("' THEN '逾期' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_HANDLING).append("' THEN '处理中' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.EXECUTE_STATUS='").append(Contents.DEAL_STATUS_FINISHED).append("' THEN '已完成' ");
		sql.append("ELSE ");
		sql.append("NULL ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END DEAL_STATUS, ");//1处理状态
		sql.append("COUNT(DISTINCT plan.ID) PLAN_COUNT ");//2整改计划数量
		sql.append("FROM T_RECTIFY_IMPROVE plan ");
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND plan.ESTATUS='").append(Contents.STATUS_SUBMITTED).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			sql.append("AND plan.COMPANY_ID=? ");
			if(StringUtils.isBlank(companyId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				param = companyId;
			}
		}
		sql.append("GROUP BY "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.EXECUTE_STATUS ");
		sql.append("ORDER BY  "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.EXECUTE_STATUS ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计体系建设计划的数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param companyId 公司ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findConstructPlanCountBySome(String companyId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.ETYPE, ");//1工作内容
		sql.append("e1.DICT_ENTRY_NAME ETYPE_NAME, ");//2工作内容中文
		sql.append("CASE WHEN plan.DEAL_STATUS IS NOT NULL THEN ");
		sql.append("(CASE WHEN plan.DEAL_STATUS='").append(Contents.DEAL_STATUS_NOTSTART).append("' THEN '未开始' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.DEAL_STATUS='").append(Contents.DEAL_STATUS_AFTER_DEADLINE).append("' THEN '逾期' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.DEAL_STATUS='").append(Contents.DEAL_STATUS_HANDLING).append("' THEN '处理中' ");
		sql.append("ELSE ");
		sql.append("(CASE WHEN plan.DEAL_STATUS='").append(Contents.DEAL_STATUS_FINISHED).append("' THEN '已完成' ");
		sql.append("ELSE ");
		sql.append("NULL ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END) ");
		sql.append("END DEAL_STATUS_NAME, ");//3处理状态
		sql.append("count(DISTINCT plan.ID) PLAN_COUNT ");//4体系建设计划数量
		sql.append("FROM T_CA_SYSTEM_CONSTRUCTION_PLAN plan ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.ID=plan.ETYPE ");
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND plan.ESTATUS<>'").append(Contents.STATUS_SAVED).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			sql.append("AND plan.COMPANY_ID=? ");
			if(StringUtils.isBlank(companyId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				param = companyId;
			}
		}
		sql.append("GROUP BY "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.ETYPE, ");
		sql.append("plan.DEAL_STATUS ");
		sql.append("ORDER BY  "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("plan.ETYPE, ");
		sql.append("plan.DEAL_STATUS ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计整改方案的数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param companyId 公司ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findImprovePlanCountBySome(String companyId,String improveId, String defectId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("i.ID IMPROVEMENT_ID, ");//1整改计划ID
		sql.append("i.IMPROVEMENT_NAME, ");//2整改计划名称
		sql.append("d.ID DEFECT_ID, ");//3缺陷ID
		sql.append("d.EDESC DEFECT_EDESC, ");//4缺陷描述
		sql.append("COUNT(DISTINCT plan.ID) PLAN_COUNT ");//5计划数量
		sql.append("FROM T_RECTIFY_IMPROVE_PLAN plan ");
		sql.append("LEFT JOIN T_RECTIFY_IMPROVE_RELA_PLAN rirplan ON rirplan.IMPROVE_PLAN_ID=plan.ID ");
		sql.append("LEFT JOIN T_RECTIFY_IMPROVE i ON i.ID=rirplan.IMPROVEMENT_ID ");
		sql.append("LEFT JOIN T_RECTIFY_DEFECT_IMPROVE di ON di.IMPROVEMENT_ID=plan.ID ");
		sql.append("LEFT JOIN T_CA_DEFECT d ON d.ID=di.DEFECT_ID ");
		
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			sql.append("AND plan.COMPANY_ID=? ");
			if(StringUtils.isBlank(companyId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				param = companyId;
			}
		}

		if(StringUtils.isNotBlank(improveId)){
			sql.append(" and i.ID = '").append(improveId).append("'");
		}

		if(StringUtils.isNotBlank(defectId)){
			sql.append(" and d.ID = '").append(defectId).append("'");
		}
		
		sql.append("GROUP BY "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("i.ID, ");
		sql.append("d.ID ");
		sql.append("ORDER BY  "); 
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("i.ID, ");
		sql.append("d.ID ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计合规诊断的数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID：公司或部门
	 * @param standardId 标准ID：内控标准
	 * @param planId 体系建设计划ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findDiagnosesCountBySome(String orgId, String standardId, String planId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("pics.ID, ");//1内控标准ID
		sql.append("pics.STANDARD_NAME, ");//2内控标准
		sql.append("o.ID ORG_ID, ");//3机构ID
		sql.append("o.ORG_NAME, ");//4机构名称
		sql.append("plan.ID, ");//5体系建设计划ID
		sql.append("plan.PLAN_NAME, ");//6体系建设计划名称
		sql.append("e.ID EMP_ID, ");//7人员ID
		sql.append("e.EMP_NAME, ");//8人员姓名
		sql.append("CASE WHEN ccd.DIAGNOSIS IS NOT NULL ");
		sql.append("THEN ");
		sql.append("(CASE WHEN ccd.DIAGNOSIS='1' THEN '合格' ELSE '不合格' END) ");
		sql.append("ELSE ");
		sql.append("NULL ");
		sql.append("END IS_QUALIFIED, ");//9诊断结果
		sql.append("COUNT(DISTINCT ccd.ID) DIAGNOSES_COUNT ");//10合规诊断数量
		sql.append("FROM T_CA_COMPLIANCE_DIAGNOSES ccd ");
		sql.append("LEFT JOIN T_CA_SYSTEM_CONSTRUCTION_PLAN plan ON plan.id=ccd.PLAN_ID ");
		sql.append("LEFT JOIN T_CA_CONST_PLAN_RELA_STANDARD ccprs ON ccprs.id=ccd.PLAN_RELA_STANDARD_ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_STANDARD ics ON ics.id=ccprs.STANDARD_ID ");
		sql.append("LEFT JOIN T_IC_CONTROL_STANDARD pics ON pics.id=ics.parent_id ");
		sql.append("LEFT JOIN T_IC_STANDARD_RELA_ORG sro ON sro.CONTROL_STANDARD_ID=ics.ID AND sro.ETYPE='OR' ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON o.ID=sro.ORG_ID ");
		sql.append("LEFT JOIN T_CA_CONST_PLAN_RELA_ST_EMP ccprse ON ccprs.ID=ccprse.PLAN_RELA_STANDARD_ID ");
		sql.append("LEFT JOIN T_CA_CONSTRUCTION_PLAN_ORG_EMP ccpoe1 ON ccpoe1.ID=ccprse.PLAN_RELA_ORG_ID AND ccpoe1.ETYPE='EH' ");
		sql.append("LEFT JOIN T_SYS_EMPLOYEE e ON e.id=ccpoe1.EMP_ID ");
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND plan.ESTATUS<>'").append(Contents.STATUS_SAVED).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and plan.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and plan.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and sro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and sro.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(standardId)){
			sql.append(" and pics.ID = '").append(standardId).append("'");
		}

		if(StringUtils.isNotBlank(planId)){
			sql.append(" and plan.ID = '").append(planId).append("'");
		}
		sql.append("GROUP BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("pics.ID, ");
		sql.append("o.ID, ");
		sql.append("plan.ID, ");
		sql.append("e.ID, ");
		sql.append("ccd.DIAGNOSIS ");
		sql.append("ORDER BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("pics.ID, ");
		sql.append("o.ID, ");
		sql.append("plan.ID, ");
		sql.append("e.ID, ");
		sql.append("ccd.DIAGNOSIS ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计体系建设的流程梳理数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID：公司或部门
	 * @param processId 流程分类ID
	 * @param planId 体系建设计划ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findConstructProcessCountBySome(String orgId, String processId, String planId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("pp.ID PARENT_ID, ");//1流程分类ID
		sql.append("pp.PROCESSURE_NAME PARENT_NAME, ");//2流程分类
		sql.append("o.ID ORG_ID, ");//3机构ID                    
		sql.append("o.ORG_NAME, ");//4机构名称                   
		sql.append("plan.ID, ");//5体系建设计划ID                
		sql.append("plan.PLAN_NAME, ");//6体系建设计划名称       
		sql.append("e.id EMP_ID, ");//7人员ID                    
		sql.append("e.EMP_NAME, ");//8人员姓名                   
		sql.append("COUNT(DISTINCT ccprp.ID) PROCESS_COUNT ");//9建设流程数量
		sql.append("FROM T_CA_CONST_PLAN_RELA_PROCESS ccprp ");
		sql.append("LEFT JOIN T_CA_SYSTEM_CONSTRUCTION_PLAN plan ON plan.id=ccprp.PLAN_ID ");
		sql.append("LEFT JOIN T_CA_CONST_PLAN_RELA_STANDARD ccprs ON ccprs.id=ccprp.PLAN_RELA_STANDARD_ID ");
		sql.append("LEFT JOIN T_CA_CONST_PLAN_RELA_ST_EMP ccprse ON ccprs.ID=ccprse.PLAN_RELA_STANDARD_ID ");
		sql.append("LEFT JOIN T_CA_CONSTRUCTION_PLAN_ORG_EMP ccpoe1 ON ccpoe1.ID=ccprse.PLAN_RELA_ORG_ID AND ccpoe1.etype='EH' ");
		sql.append("LEFT JOIN t_sys_employee e ON e.id=ccpoe1.emp_id ");
		sql.append("LEFT JOIN T_IC_CONTROL_STANDARD ics ON ics.id=ccprs.STANDARD_ID ");
		sql.append("LEFT JOIN t_ic_standard_rela_processure srp ON srp.CONTROL_STANDARD_ID=ics.ID ");
		sql.append("LEFT JOIN t_ic_processure p ON srp.PROCESSURE_ID=p.ID ");
		sql.append("LEFT JOIN t_ic_processure pp ON p.PARENT_ID=pp.ID ");
		sql.append("LEFT JOIN t_ic_processure_rela_org prorg ON p.ID=prorg.processure_id AND prorg.ETYPE='OR' ");
		sql.append("LEFT JOIN t_sys_organization o ON prorg.ORG_ID=o.ID ");
		sql.append("WHERE plan.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("' AND plan.ESTATUS<>'").append(Contents.STATUS_SAVED).append("' ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and plan.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and plan.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and prorg.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and prorg.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		if(StringUtils.isNotBlank(processId)){
			sql.append(" and pp.ID = '").append(processId).append("'");
		}

		if(StringUtils.isNotBlank(planId)){
			sql.append(" and plan.ID = '").append(planId).append("'");
		}
		sql.append("GROUP BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("pp.ID, ");
		sql.append("o.ID, ");
		sql.append("plan.ID, ");
		sql.append("e.ID ");
		sql.append("ORDER BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(plan.CREATE_TIME), ");//创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(plan.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("pp.ID, ");
		sql.append("o.ID, ");
		sql.append("plan.ID, ");
		sql.append("e.ID ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	
	/**
	 * <pre>
	 * 统计流程的数量
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID：公司或部门
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findProcessCountBySome(String orgId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(P.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(P.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("ppp.PROCESSURE_NAME LEVEL_ONE, ");//1一级分类
		sql.append("pp.PROCESSURE_NAME LEVEL_TWO, ");//2二级分类
		sql.append("o.ID ORG_ID, ");//3机构ID             
		sql.append("o.ORG_NAME ORG_NAME, ");//4机构名称                      
		sql.append("e1.DICT_ENTRY_NAME PROCESS_CLASS, ");//5发生频率
		sql.append("COUNT(DISTINCT P.ID) PROCESS_COUNT ");//6流程数量           
		sql.append("FROM t_ic_processure p ");
		sql.append("LEFT JOIN t_sys_dict_entry e1 ON e1.ID=p.PROCESS_CLASS ");                  
		sql.append("LEFT JOIN t_ic_processure_rela_org pro1 ON p.ID=pro1.PROCESSURE_ID AND pro1.ETYPE='OR' "); 
		sql.append("LEFT JOIN t_sys_organization o ON pro1.ORG_ID=o.ID ");
		sql.append("LEFT JOIN t_ic_processure_rela_org pro2 ON p.ID=pro2.PROCESSURE_ID AND pro2.ETYPE='ER' ");
		sql.append("LEFT JOIN t_sys_employee e ON pro2.EMP_ID=e.ID ");
		sql.append("LEFT JOIN t_ic_processure pp ON pp.ID=p.PARENT_ID ");
		sql.append("LEFT JOIN t_ic_processure ppp ON ppp.ID=pp.PARENT_ID ");
		sql.append("where  p.IS_LEAF='1' AND p.DELETE_STATUS='").append(Contents.DELETE_STATUS_USEFUL).append("'  ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and p.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and p.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and pro1.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and pro1.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		
		sql.append("GROUP BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(p.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(p.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("ppp.PROCESSURE_NAME, ");
		sql.append("pp.PROCESSURE_NAME, ");
		sql.append("o.ORG_NAME, ");
		sql.append("e1.DICT_ENTRY_NAME ");
		sql.append("ORDER BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(p.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(p.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("ppp.PROCESSURE_NAME, ");
		sql.append("pp.PROCESSURE_NAME, ");
		sql.append("o.ORG_NAME, ");
		sql.append("e1.DICT_ENTRY_NAME ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
	/**
	 * <pre>
	 * 统计控制措施的情况
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param orgId 机构ID：公司或部门
	 * @param processId 流程ID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Object[]> findControlMeasureCountBySome(String orgId,String processId) {
		String dialect = findDialect();
		StringBuffer sql = new StringBuffer();
		String param = "";
		sql.append("SELECT ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(m.CREATE_TIME), ");//0创建年份
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(m.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("o.id org_ID, ");//1机构ID
		sql.append("o.org_name, ");//2机构名称
		sql.append("p.ID PROCESSURE_ID, ");//3流程ID
		sql.append("p.PROCESSURE_NAME, ");//4流程名称
		sql.append("e1.DICT_ENTRY_NAME CONTROL_MEASURE_NAME, ");//5控制方式
		sql.append("e2.DICT_ENTRY_NAME IS_KEY_CONTROL_POINT_NAME, ");//6是否关键控制点
		sql.append("COUNT(DISTINCT m.ID) MEASURE_COUNT ");//7控制措施数量
		sql.append("FROM T_CON_CONTROL_MEASURE m ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e1 ON e1.ID=m.CONTROL_MEASURE ");
		sql.append("LEFT JOIN T_SYS_DICT_ENTRY e2 ON e2.ID=m.IS_KEY_CONTROL_POINT ");
		sql.append("LEFT JOIN T_CON_MEASURE_RELA_ORG mro ON m.ID=mro.CONTROL_MEASURE_ID AND mro.ETYPE='OR' ");
		sql.append("LEFT JOIN T_IC_MEASURE_RELA_PROCESSURE mrp ON mrp.CONTROL_MEASURE_ID=m.ID ");
		sql.append("LEFT JOIN T_IC_PROCESSURE p ON mrp.PROCESSURE_ID=p.ID ");
		sql.append("LEFT JOIN T_SYS_ORGANIZATION o ON o.ID=mro.ORG_ID ");
		sql.append("WHERE  m.DELETE_ESTATUS='").append(Contents.DELETE_STATUS_USEFUL).append("'  ");
		if(judgeIfIcmDept()){//是内控部门
			if(StringUtils.isBlank(orgId)){// orgid为null说明第一次访问 默认显示 按照companyId获取数据
				sql.append(" and m.COMPANY_ID=? ");
				param = UserContext.getUser().getCompanyid();
			}else{//内控部门切换公司部门维度的时候
				SysOrganization org = o_organizationBO.get(orgId);
				if(null!=org){
					String orgtype = org.getOrgType();
					if("0orgtype_c".equals(orgtype)||"0orgtype_sc".equals(orgtype)){		//如果是公司
						sql.append(" and m.COMPANY_ID=? ");
						param = orgId;
					}else if("0orgtype_d".equals(orgtype)||"0orgtype_sd".equals(orgtype)){		//如果是部门
						sql.append(" and mro.ORG_ID=? ");
						param = orgId;
					}
				}
			}
		}else{			//非内控部门 默认取值
			sql.append(" and mro.ORG_ID=? ");
			param = UserContext.getUser().getMajorDeptId();
		}
		
		if(StringUtils.isNotBlank(processId)){
			sql.append(" and p.ID = '").append(processId).append("'");
		}
		
		sql.append("GROUP BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(m.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(m.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("o.org_name, ");
		sql.append("p.ID, ");
		sql.append("m.CONTROL_MEASURE, ");
		sql.append("m.IS_KEY_CONTROL_POINT ");
		sql.append("ORDER BY ");
		if("mysql".equals(dialect)){
			sql.append("YEAR(m.CREATE_TIME), ");
		} else if("sqlserver".equals(dialect)){
    		//ENDO sqlserver 截取年份
		} else if("oracle".equals(dialect)){
			sql.append("TO_CHAR(m.CREATE_TIME, 'yyyy'), ");
		}
		sql.append("o.ORG_NAME, ");
		sql.append("p.ID, ");
		sql.append("m.CONTROL_MEASURE, ");
		sql.append("m.IS_KEY_CONTROL_POINT ");
		SQLQuery sqlQuery = this.o_icmMyDatasDAO.createSQLQuery(sql.toString(), param);
		return sqlQuery.list();
	}
}
