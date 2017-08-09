package com.fhd.ra.business.risk.riskcomparequery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.core.dao.Page;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.entity.risk.history.RiskHistory;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.ra.web.form.risk.RiskCompreQueryForm;
/**
 * 风险综合查询BO
 * @author wzr
 *
 */
@Service
public class RiskCompreQueryBO {
	
	@Autowired
	private RiskDAO o_riskDAO;

	/**
	 * 风险综合查询-根据查询条件查询风险库(查询风险库实体，但是用风险版本明细实体展示，因为前台页面公用)
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> queryRisksByQueryForm(RiskCompreQueryForm form,Page<Map> page,String sort, String dir){
		List<RiskHistory> reList = new ArrayList<RiskHistory>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT h.ID,h.RISK_CODE,h.RISK_NAME,h.EDESC,h.PARENT_NAME,  ");
		sql.append("GROUP_CONCAT(CONCAT_WS('/',IFNULL(org1.ORG_NAME,''),IFNULL(e1.EMP_NAME,''))) AS mainOrg, ");
		sql.append("GROUP_CONCAT(CONCAT_WS('/',IFNULL(org2.ORG_NAME,''),IFNULL(e2.EMP_NAME,'')))AS relaOrg,");
		sql.append("a.RISK_STATUS ");
		sql.append("FROM t_rm_risks h  ");
		//责任部门
		sql.append("LEFT JOIN t_rm_risk_org riskorg1 ON h.id = riskorg1.RISK_ID AND riskorg1.ETYPE = 'M' ");
		sql.append("LEFT JOIN t_sys_organization org1 ON org1.id=riskorg1.ORG_ID ");
		sql.append("LEFT JOIN t_sys_employee e1 ON e1.id = riskorg1.EMP_ID ");
		//相关部门
		sql.append("LEFT JOIN t_rm_risk_org riskorg2 ON h.id = riskorg2.RISK_ID AND riskorg2.ETYPE = 'A' ");
		sql.append("LEFT JOIN t_sys_organization org2 ON org2.id=riskorg2.ORG_ID ");
		sql.append("LEFT JOIN t_sys_employee e2 ON e2.id = riskorg2.EMP_ID ");
		//分值
		sql.append("LEFT JOIN t_rm_risk_adjust_history a on a.RISK_ID=h.ID and a.IS_LATEST='1' ");
		sql.append("WHERE h.schm = :schm AND h.IS_RISK_CLASS = 're' AND h.DELETE_ESTATUS = '1' ");
		if(null != form.getRiskCode()){
			sql.append("AND h.RISK_CODE LIKE :riskCode ");
		}
		if(null != form.getRiskName()){
			sql.append("AND h.RISK_NAME LIKE :riskName ");
		}
		if(null != form.getRiskDesc()){
			sql.append("AND h.EDESC LIKE :riskDesc ");
		}
		if(null != form.getParentId()){//上级风险
			sql.append("AND h.PARENT_ID = :parentRiskId ");
		}
		if(null != form.getMainDeptId()){
			sql.append("AND org1.ORG_NAME LIKE :mainDeptName ");
		}
		if(null != form.getRelaDeptId()){
			sql.append("AND org2.ORG_NAME LIKE :relaDeptName ");
		}
		sql.append("GROUP BY h.ID ");
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("schm", form.getSchm().split("risk_schm_")[1]);
		if(null != form.getRiskCode()){
			sqlQuery.setParameter("riskCode", "%"+form.getRiskCode()+"%");
		}
		if(null != form.getRiskName()){
			sqlQuery.setParameter("riskName", "%"+form.getRiskName()+"%");
		}
		if(null != form.getRiskDesc()){
			sqlQuery.setParameter("riskDesc", "%"+form.getRiskDesc()+"%");
		}
		if(null != form.getParentId()){//上级风险
			sqlQuery.setParameter("parentRiskId", IcmStandardUtils.findIdbyJason(form.getParentId(), "id"));
		}
		if(null != form.getMainDeptId()){//责任部门
			sqlQuery.setParameter("mainDeptName", "%"+IcmStandardUtils.findIdbyJason(form.getMainDeptId(), "deptname")+"%");
		}
		if(null != form.getRelaDeptId()){//相关部门
			sqlQuery.setParameter("relaDeptName", "%"+IcmStandardUtils.findIdbyJason(form.getRelaDeptId(), "deptname")+"%");
		}
		//分页实现
		int size = sqlQuery.list().size();
  		int start = (page.getPageNo()-1)*page.getPageSize();
  		int limit = page.getPageSize();
  		sqlQuery.setFirstResult(start);
  		sqlQuery.setMaxResults(limit);
		List<Object[]> list = sqlQuery.list();
		List<Map> objList = new ArrayList<Map>();
		for(Object[] o : list){
  			Map m = new HashMap();
  			m.put("id", o[0].toString());
  			m.put("riskCode", null==o[1]?"":o[1].toString());
  			m.put("riskName", null==o[2]?"":o[2].toString());
  			m.put("riskDesc", null==o[3]?"":o[3].toString());
  			m.put("parentName", null==o[4]?"":o[4].toString());
  			if(null != o[5]){
  				m.put("mainName", "/".equals(o[5].toString())?"":o[5].toString());
  			}else{
  				m.put("mainName", "");
  			}
  			if(null != o[6]){
  				m.put("relaName", "/".equals(o[6].toString())?"":o[6].toString());
  			}else{
  				m.put("relaName", "");
  			}
  			m.put("score", null==o[7]?"":o[7].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		return page;
	}
}
