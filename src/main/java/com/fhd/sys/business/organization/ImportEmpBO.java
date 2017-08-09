package com.fhd.sys.business.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.organization.TempEmpExcelDataDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.process.Process;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.Template;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.entity.RiskFromExcel;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.SysPosition;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.assess.oper.SysEmployeeBO;
import com.fhd.sys.business.auth.RoleBO;
import com.fhd.sys.business.auth.SysUserBO;

/**
 * 人员临时表业务
 * */

@Service
public class ImportEmpBO {

	@Autowired
	private TempEmpExcelDataDAO o_tempEmpExcelDataDAO;
	
	@Autowired
	private OrgGridBO o_orgGridBO;
	
	@Autowired
	private PositionBO o_positionBO;
	
	@Autowired
	private SysEmployeeBO o_employeeBO;
	
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	
	@Autowired
	private RoleBO o_roleBO;
	
	@Autowired
	private SysUserBO o_userBO;
	
	/**
	 * 机构所有数据(机构编号--机构名称)
	 * */
	public HashMap<String, SysOrganization> findOrgAllMap(){
		HashMap<String, SysOrganization> map = new HashMap<String, SysOrganization>();
		List<SysOrganization> list = o_orgGridBO.findAllOrganizationsByDeleteStatusFalse();
		for (SysOrganization en : list) {
			map.put(en.getOrgcode() + "--" + en.getOrgname(), en);
		}
		
		return map;
	}
	
	/**
	 * 岗位所有数据(机构ID--岗位名称)
	 * */
	public HashMap<String, SysPosition> findPosiAllMap(){
		HashMap<String, SysPosition> map = new HashMap<String, SysPosition>();
		List<SysPosition> list = o_positionBO.findByAll();
		for (SysPosition en : list) {
			map.put(en.getSysOrganization().getId() + "--" + en.getPosiname(), en);
		}
		
		return map;
	}
	
	/**
	 * 人员所有数据(机构ID--人员名称)
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, SysEmployee> findEmpAllMap(){
		HashMap<String, SysEmployee> map = new HashMap<String, SysEmployee>();
		Criteria c = o_sysEmployeeDAO.createCriteria();
		c.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<SysEmployee> list = c.list();
		for (SysEmployee sysEmployee : list) {
			map.put(sysEmployee.getSysOrganization().getId() + "--" + sysEmployee.getEmpname(), sysEmployee);
		}
		
		return map;
	}
	
	/**
	 * 用户所有数据(用户名)
	 * */
	public HashMap<String, SysUser> findUserAllMap(){
		HashMap<String, SysUser> map = new HashMap<String, SysUser>();
		List<SysUser> list = o_userBO.findUserAll();
		for (SysUser en : list) {
			map.put(en.getUsername(), en);
		}
		
		return map;
	}
	
	/**
	 * 角色所有数据(角色名称)
	 * */
	public HashMap<String, SysRole> findRoleAllMap(){
		HashMap<String, SysRole> map = new HashMap<String, SysRole>();
		List<SysRole> list = o_roleBO.findByAll();
		for (SysRole en : list) {
			map.put(en.getRoleName(), en);
		}
		
		return map;
	}
	
	/**
	 * 将风险数据导入到数据库
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	 */
	public ArrayList<TempEmpExcelData> getTempEmpExcelDataList(final List<List<String>> excelDatas){
		//清空临时表数据
		String deleteSql = "delete from temp_emp_exceldata";
		SQLQuery sqlQuery = o_tempEmpExcelDataDAO.createSQLQuery(deleteSql);
		sqlQuery.executeUpdate();
		
		ArrayList<TempEmpExcelData> tempEmpExcelDataList = new ArrayList<TempEmpExcelData>();
		HashMap<String, SysOrganization> sysOrganizationAllMap = this.findOrgAllMap();
		HashMap<String, SysPosition> sysPositionAllMap = this.findPosiAllMap();
		HashMap<String, SysEmployee> SysEmployeeAllMap = this.findEmpAllMap();
		HashMap<String, SysUser> sysUserAllMap = this.findUserAllMap();
		HashMap<String, SysRole> sysRoleAllMap = this.findRoleAllMap();
		
		if (excelDatas != null && excelDatas.size() > 0) {
			for (int row = 2; row < excelDatas.size(); row++) {// 读取数据行
				StringBuilder sb = new StringBuilder();
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				
				//用户名
                String exUserName = rowDatas.get(0);
                if(StringUtils.isBlank(exUserName)){
                	sb.append("用户名不能为空.");
                }
                
                //员工编号
                String exEmpDept = rowDatas.get(1);
                if(StringUtils.isBlank(exEmpDept)){
                	sb.append("员工编号不能为空.");
                }
                	
                //员工名称
                String exEmpName = rowDatas.get(2);
                if(StringUtils.isBlank(exEmpName)){
                	sb.append("员工名称不能为空.");
                }
                
                //部门所在公司集团
                String exCompanyId = rowDatas.get(3);
                if(StringUtils.isBlank(exCompanyId)){
                	sb.append("部门所在公司集团.");
                }
    			
                //所属部门
                String exDeptName = rowDatas.get(4);
                if(StringUtils.isBlank(exDeptName)){
                	sb.append("所属部门不能为空.");
                }
    			
    			//所属角色
    			String exRoleName = rowDatas.get(5);
    			if(StringUtils.isBlank(exRoleName)){
                	sb.append("所属角色不能为空.");
                }
    			
    			//所属岗位
    			String exPosiName = rowDatas.get(6);
    			if(StringUtils.isBlank(exPosiName)){
                	sb.append("所属岗位不能为空.");
                }
    			
//    			RiskFromExcel riskFromExcel = new RiskFromExcel();
//				riskFromExcel.setId(Identities.uuid());
//				List<SysOrganization> orgList = o_organizationBO.findNextSysOrgBySome(null, null, rowDatas.get(18));
//				if(orgList != null && orgList.size() > 0){
//					riskFromExcel.setCompany(orgList.get(0));
//				}else{
//					riskFromExcel.setCompany(null);
//				}
//				riskFromExcel.setParentCode(rowDatas.get(0));
//				riskFromExcel.setParentName(rowDatas.get(1));
//				riskFromExcel.setCode(rowDatas.get(2));
//				riskFromExcel.setName(rowDatas.get(3));
//				riskFromExcel.setDesc(rowDatas.get(4));
//				riskFromExcel.setRespOrgsName(row5);
//				riskFromExcel.setRelaOrgsName(row6);
//				riskFromExcel.setRespOrgs(sb5.toString());
//				riskFromExcel.setRelaOrgs(sb6.toString());
//				riskFromExcel.setImpactTargetName(row7);
//				riskFromExcel.setImpactProcessName(row8);
//				riskFromExcel.setImpactTarget(sb7.toString());
//				riskFromExcel.setImpactProcess(sb8.toString());
//				riskFromExcel.setEsort(rowDatas.get(9));
//				if(null == rowDatas.get(10) || rowDatas.get(10).equals("是")){
//					riskFromExcel.setIsInherit("0yn_y");//isInherit
//				}else if(rowDatas.get(10).equals("否")){
//					riskFromExcel.setIsInherit("0yn_ny");//isInherit
//				}
//				riskFromExcel.setAssessmentTemplateName(rowDatas.get(11));
//				String s11 = null;
//				if(StringUtils.isNotBlank(rowDatas.get(11))){
//					String[] tempNames = rowDatas.get(11).split(",");
//					if(tempNames.length > 0){
//						Template template = o_templateBO.findTemplateByName(tempNames[0]);
//						if(null != template){
//							s11 = template.getId();
//						}else{
//							//错误：没有找到对应的template
//    						sb.append("没有此评估模板.");
//						}
//					}
//				}
//				riskFromExcel.setAssessmentTemplate(s11);
//				riskFromExcel.setProbability(rowDatas.get(12));
//				riskFromExcel.setImpact(rowDatas.get(13));
//				riskFromExcel.setUrgency(rowDatas.get(14));
//				riskFromExcel.setRiskLevel(rowDatas.get(15));
//				riskFromExcel.setRiskStatus(rowDatas.get(16));
//				riskFromExcel.setIsRiskClass(rowDatas.get(17));
//				riskFromExcel.setComment(sb.toString());
//				
//				if(StringUtils.isBlank(riskFromExcel.getComment())){
//					if(!riskFromExcel.getProbability().equalsIgnoreCase("") || !riskFromExcel.getImpact().equalsIgnoreCase("") || 
//							!riskFromExcel.getUrgency().equalsIgnoreCase("")){
//						if(null == riskFromExcel.getAssessmentTemplate()){
//							sb.append("存在分值情况下,必须有评估模板.");
//						}
//						riskFromExcel.setComment(sb.toString());
//					}
//				}
//				
//				riskFromExcel.setExRow(String.valueOf(row + 1));
//				RiskFromExcelList.add(riskFromExcel);
//				o_riskFromExcelDAO.save(riskFromExcel);
			}
		}
		
		return null;
	}
}
