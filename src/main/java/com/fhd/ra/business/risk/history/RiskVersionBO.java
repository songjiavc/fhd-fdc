package com.fhd.ra.business.risk.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.history.NewRiskVersionDAO;
import com.fhd.dao.risk.history.RiskHistoryDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.history.NewRiskVersion;
import com.fhd.entity.risk.history.RiskHistory;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.ra.web.form.risk.RiskCompreQueryForm;
import com.fhd.ra.web.form.risk.RiskVersionForm;

/**
 * 风险版本BO
 * @author wzr
 *
 */
@Service
public class RiskVersionBO {
	
	@Autowired
	private NewRiskVersionDAO o_newRiskVersionDAO;
	@Autowired
	private RiskDAO o_riskDAO;
	@Autowired
	private RiskHistoryDAO o_riskHistoryDAO;

	/**
	 * 查询风险版本信息
	 * @param query
	 * @param page
	 * @param schm
	 * @return
	 */
	public Page<NewRiskVersion> findversionsPageBySome(String query, Page<NewRiskVersion> page, String schm){
		DetachedCriteria dc = DetachedCriteria.forClass(NewRiskVersion.class);
		if(StringUtils.isNotBlank(schm)){
			dc.add(Restrictions.eq("schm", schm));
		}
		if(StringUtils.isNotBlank(query)){
			dc.add(Property.forName("versionName").like(query,MatchMode.ANYWHERE));
		}
		dc.addOrder(Order.desc("createDate"));
		return o_newRiskVersionDAO.findPage(dc, page, false);
	}	
	
	/**
	 * 保存风险版本
	 * @param versionForm
	 * @param verId
	 * @param schm
	 */
	@Transactional
	public Boolean saveRiskVersion(RiskVersionForm versionForm, String verId, String schm){
		Boolean resultB = true;
		try{
			NewRiskVersion version = new NewRiskVersion();
			if(StringUtils.isNotBlank(verId)){//修改
				NewRiskVersion findVer = o_newRiskVersionDAO.get(verId);
				findVer.setVersionName(versionForm.getVersionName());
				findVer.setDesc(versionForm.getDesc());
				o_newRiskVersionDAO.merge(findVer);
			}else{//新增
				version.setId(Identities.uuid());
				version.setCreateDate(new Date());//创建时间
				version.setCreateBy(UserContext.getUsername());//创建人
				version.setCreateOrg(UserContext.getUser().getMajorDeptId());//创建人的部门id
				version.setSchm(schm);
				version.setRiskCount(this.findRiskCountsByschm(schm));
				//保存新增风险事件数、删除风险事件数和风险事件总数
				NewRiskVersion exitVer = this.findVersionBySchmAndDate(schm);
				if(null != exitVer){
					version.setAddRisk(this.findaddRiskcountsByVersion(exitVer.getId(), schm));
					version.setDeleteRisk(this.finddeleteRiskcountsByVersion(exitVer.getId(), schm));
				}else{
					version.setAddRisk(0);
					version.setDeleteRisk(0);
				}
				version.setVersionName(versionForm.getVersionName());
				version.setDesc(versionForm.getDesc());
				o_newRiskVersionDAO.merge(version);
				this.createVersionRisks(version.getId(),schm);
			}
		}catch (Exception e) {
			e.printStackTrace();
			resultB = false;
		}
		return resultB;
	}
	
	/**
	 * 查询最新日期的风险版本
	 * @param schm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public NewRiskVersion findVersionBySchmAndDate(String schm){
		Criteria criteria = o_newRiskVersionDAO.createCriteria();
		criteria.add(Restrictions.eq("schm", schm));
		criteria.addOrder(Order.desc("createDate"));
		List<NewRiskVersion> versions = criteria.list();
		if(versions.size()>0){
			return versions.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 保存风险版本库中的所有风险
	 * @param verId
	 * @param schm
	 */
	@Transactional
	public void createVersionRisks(String verId, String schm){
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO t_risk_history(ID,VERSION_ID,RISK_ID,RISK_CODE,RISK_NAME,EDESC,PARENT_NAME,ID_SEQ,");
		sql.append("IS_LEAF,IS_RISK_CLASS,PARENT_ID,MAIN_DEPT_NAME,RELA_DEPT_NAME,SCORE,KPI_NAME,PROCESS_NAME) ");
		sql.append("SELECT * FROM ( ");
		sql.append("SELECT UUID(),'" + verId + "',r.ID,r.RISK_CODE,r.RISK_NAME,r.EDESC,r.PARENT_NAME,r.ID_SEQ,r.IS_LEAF,r.IS_RISK_CLASS, r.PARENT_ID,  ");
		sql.append("GROUP_CONCAT(CONCAT_WS('/',IFNULL(org1.ORG_NAME,''),IFNULL(e1.EMP_NAME,''))) AS mainOrg, ");
		sql.append("GROUP_CONCAT(CONCAT_WS('/',IFNULL(org2.ORG_NAME,''),IFNULL(e2.EMP_NAME,'')))AS relaOrg,");
		sql.append("r.RISK_SCORE, kpi.KPI_NAME,p.PROCESSURE_NAME ");
		sql.append("FROM t_rm_risks r ");
		//查询风险责任部门/责任人名称
		sql.append("LEFT JOIN t_rm_risk_org riskorg1 ON r.id = riskorg1.RISK_ID AND riskorg1.ETYPE = 'M' ");
		sql.append("LEFT JOIN t_sys_organization org1 ON org1.id=riskorg1.ORG_ID ");
		sql.append("LEFT JOIN t_sys_employee e1 ON e1.id = riskorg1.EMP_ID ");
		//查询风险相关部门/责任人
		sql.append("LEFT JOIN t_rm_risk_org riskorg2 ON r.id = riskorg2.RISK_ID AND riskorg2.ETYPE = 'A' ");
		sql.append("LEFT JOIN t_sys_organization org2 ON org2.id=riskorg2.ORG_ID ");
		sql.append("LEFT JOIN t_sys_employee e2 ON e2.id = riskorg2.EMP_ID ");
		//风险关联指标
		sql.append("LEFT JOIN T_KPI_KPI_RELA_RISK kpirela ON kpirela.RISK_ID = r.ID ");
		sql.append("LEFT JOIN t_kpi_kpi kpi ON kpi.ID = kpirela.KPI_ID ");
		//风险关联流程
		sql.append("LEFT JOIN T_PROCESSURE_RISK_PROCESSURE prela ON prela.RISK_ID = r.ID ");
		sql.append("LEFT JOIN T_IC_PROCESSURE p ON p.ID = prela.PROCESSURE_ID ");
		//风险分值
		//sql.append("LEFT JOIN t_rm_risk_adjust_history h on h.RISK_ID=r.ID and h.IS_LATEST='1' ");
		sql.append("WHERE r.SCHM = '" + schm + "' AND r.DELETE_ESTATUS = '1' ");
		sql.append("GROUP BY r.ID) AS b ");
		o_newRiskVersionDAO.createSQLQuery(sql.toString()).executeUpdate();
	}
	
	/**
	 * 查询风险总数
	 * @param schm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int findRiskCountsByschm(String schm){
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("schm", schm));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("isRiskClass", "re"));
		List<Risk> risks = criteria.list();
		if(risks.size()>0){
			return risks.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 查询较上一版本新增的风险数量
	 * @param verId	上一版本id
	 * @return
	 */
	public int findaddRiskcountsByVersion(String verId, String schm){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(*) FROM t_rm_risks r ");
		sql.append("WHERE r.schm = '" + schm + "' AND r.delete_estatus = '1' ");
		sql.append("AND r.IS_RISK_CLASS = 're' ");
		sql.append("AND r.id NOT IN ");
		sql.append("(SELECT h.RISK_ID FROM t_risk_history h ");
		sql.append("WHERE h.VERSION_ID = '" + verId + "' AND r.IS_RISK_CLASS = 're' ) ");
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString());
		return Integer.parseInt(sqlQuery.list().get(0).toString());
	}
	
	/**
	 * 查询较上一版本删除的风险数量
	 * @param verId
	 * @param schm
	 * @return
	 */
	public int finddeleteRiskcountsByVersion(String verId, String schm){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(*) FROM t_risk_history h ");
		sql.append("WHERE h.VERSION_ID = '" + verId + "' ");
		sql.append("AND h.IS_RISK_CLASS = 're' ");
		sql.append("AND h.RISK_ID NOT IN ( ");
		sql.append("SELECT r.id FROM t_rm_risks r ");
		sql.append("WHERE r.schm = '" + schm + "' AND r.delete_estatus = '1' AND r.IS_RISK_CLASS = 're' ) ");
		SQLQuery sqlQuery = o_newRiskVersionDAO.createSQLQuery(sql.toString());
		return Integer.parseInt(sqlQuery.list().get(0).toString());
	}
	
	/**
	 * 加载修改表单数据
	 * @param verId
	 * @return
	 */
	public Map<String,Object> findRiskVersionByID(String verId){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		NewRiskVersion riskVersion = o_newRiskVersionDAO.get(verId);
		inmap.put("versionName", riskVersion.getVersionName());
		inmap.put("desc", riskVersion.getDesc());
		map.put("data", inmap);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 查看版本风险明细树列表--一级风险
	 * @param verId
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findRisksDetailByVerId(String verId,String query){
		List<Map<String, Object>> item = new ArrayList<Map<String,Object>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskMap = this.findChildrenRisksRbs(verId);
		//风险事件
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskResMap = this.findChildrenRisksRe(verId);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rh.ID,rh.VERSION_ID,rh.RISK_ID,rh.RISK_NAME,rh.MAIN_DEPT_NAME,rh.RELA_DEPT_NAME,rh.ID_SEQ,rh.SCORE " +
				" FROM t_risk_history rh  " +
				" WHERE rh.VERSION_ID = :verId AND rh.IS_RISK_CLASS = 'rbs' ");
		/*if(StringUtils.isNotBlank(query)){
			sql.append(" and (b.risk_name like :query or a.risk_status like :query) ");
		}*/
        SQLQuery sqlQuery = o_riskHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("verId", verId);
        if(StringUtils.isNotBlank(query)){
        	sqlQuery.setParameter("query", "%"+query+"%");
        }
		List<Object[]> list = sqlQuery.list();
		
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String historyId = "";
            String versionId = "";
            String riskId = "";
            String riskName = "";
            String mainName = "";
            String relaName = "";
            String parentId = "";
            String score = "";
            if(null != objects[0]){
            	historyId = objects[0].toString();
            }if(null != objects[1]){
            	versionId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[3]){
            	riskName = objects[3].toString();
            }if(null != objects[4]){
            	mainName = objects[4].toString();
            }if(null != objects[5]){
            	relaName = objects[5].toString();
            }if(null != objects[6]){
            	String parentA[] = objects[6].toString().split("\\.");
            	if(parentA.length>0){
            		parentId = parentA[parentA.length-2];
            	}
            }if(null != objects[7]){
            	score = objects[7].toString();
            }
            if(null == parentId || "".equals(parentId)){//用IDSEQ过滤一级风险
            	Map<String, Object> riskMap = new HashMap<String, Object>();
            	ArrayList<Map<String, Object>> childrenList = new ArrayList<Map<String,Object>>();//下级风险
                riskMap.put("historyId", historyId);
                riskMap.put("versionId", versionId);
                riskMap.put("riskId", riskId);
                riskMap.put("riskName", riskName);//风险名称
                riskMap.put("relaName", "/".equals(relaName)?"":relaName);//责任部门/人
                riskMap.put("mainName", "/".equals(mainName)?"":mainName);//相关部门/人
                riskMap.put("score", score);
                riskMap.put("linked", true);
                if(null != childrenRiskMap.get(riskId)){//一级风险下的二级风险
                	childrenList = childrenRiskMap.get(riskId);
                }
                if(null != childrenRiskResMap.get(riskId)){//一级风险下的风险事件
                	for(Map<String, Object> m : childrenRiskResMap.get(riskId)){
                		childrenList.add(m);
                	}
            	}
                if(null != childrenList){
                	riskMap.put("children", childrenList);
                	riskMap.put("leaf", false);
                }else{
                	riskMap.put("leaf", true);
                }
    			riskMap.put("linked", true);
                item.add(riskMap);
            }
        }
		return item;
	}
	
	/**
	 * 查询风险分类下二级风险
	 * @param verId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenRisksRbs(String verId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		HashMap<String, ArrayList<Map<String, Object>>> childrenRiskMap = this.findChildrenRisksRe(verId);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rh.ID,rh.VERSION_ID,rh.RISK_ID,rh.RISK_NAME,rh.MAIN_DEPT_NAME,rh.RELA_DEPT_NAME,rh.ID_SEQ,rh.SCORE " +
				" FROM t_risk_history rh  " +
				" WHERE rh.VERSION_ID = :verId AND rh.IS_RISK_CLASS = 'rbs' ");
        SQLQuery sqlQuery = o_riskHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("verId", verId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String historyId = "";
            String versionId = "";
            String riskId = "";
            String riskName = "";
            String mainName = "";
            String relaName = "";
            String parentId = "";
            String score = "";
            if(null != objects[0]){
            	historyId = objects[0].toString();
            }if(null != objects[1]){
            	versionId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[3]){
            	riskName = objects[3].toString();
            }if(null != objects[4]){
            	mainName = objects[4].toString();
            }if(null != objects[5]){
            	relaName = objects[5].toString();
            }if(null != objects[6]){
            	String parentA[] = objects[6].toString().split("\\.");
            	if(parentA.length>0){
            		parentId = parentA[parentA.length-2];
            	}
            }if(null != objects[7]){
            	score = objects[7].toString();
            }
            
            Map<String, Object> riskMap = new HashMap<String, Object>();
            riskMap.put("historyId", historyId);
            riskMap.put("versionId", versionId);
            riskMap.put("riskId", riskId);
            riskMap.put("riskName", riskName);//风险名称
            riskMap.put("relaName", "/".equals(relaName)?"":relaName);//责任部门/人
            riskMap.put("mainName", "/".equals(mainName)?"":mainName);//相关部门/人
            riskMap.put("score", score);
            riskMap.put("linked", true);
            if(null != childrenRiskMap.get(riskId)){
            	riskMap.put("children", childrenRiskMap.get(riskId));//部门下的风险
            	riskMap.put("leaf", false);
            }else{
            	riskMap.put("leaf", true);
            }
            if(childrenMap.get(parentId) != null){
            	childrenMap.get(parentId).add(riskMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(riskMap);
            	childrenMap.put(parentId, arrayList);
            }
        }
		//return childrenMap;
		return findChildrenRisksRbsSelf(childrenMap);
	}
	
	/**
	 * 递归查询风险子分类中是否含有下级子分类
	 * @param childrenMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenRisksRbsSelf(
												HashMap<String, ArrayList<Map<String, Object>>> childrenMap){
		Set<Entry<String, ArrayList<Map<String, Object>>>> parentRiskIds = childrenMap.entrySet();
		for(Entry<String, ArrayList<Map<String, Object>>> parentRiskIdEntry : parentRiskIds){	//parentRiskId 父级风险id
			ArrayList<Map<String, Object>> parentRiskMaps = childrenMap.get(parentRiskIdEntry.getKey());
			for(Map<String, Object> parentRiskMap : parentRiskMaps){
				String riskId = (String)parentRiskMap.get("riskId");
				if(null!=childrenMap.get(riskId)){
					ArrayList<Map<String, Object>> cMap = childrenMap.get(riskId);
					if(parentRiskMap.containsKey("children")){
						List<Map<String, Object>> oldC = (ArrayList<Map<String, Object>>)parentRiskMap.get("children");
						for(Map<String, Object> c : oldC){
							cMap.add(c);
						}
					}
					parentRiskMap.put("children", cMap);
					parentRiskMap.put("leaf", false);
				}

			}
		}
		return childrenMap;
	}
	
	/**
	 * 查询风险分类下风险事件
	 * @param verId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, ArrayList<Map<String, Object>>> findChildrenRisksRe(String verId){
		HashMap<String, ArrayList<Map<String, Object>>> childrenMap = new HashMap<String, ArrayList<Map<String,Object>>>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rh.ID,rh.VERSION_ID,rh.RISK_ID,rh.RISK_NAME,rh.MAIN_DEPT_NAME,rh.RELA_DEPT_NAME,rh.ID_SEQ,rh.SCORE  " +
				" FROM t_risk_history rh " +
				" WHERE rh.VERSION_ID=:verId AND rh.IS_RISK_CLASS = 're' ");
        SQLQuery sqlQuery = o_riskHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("verId", verId);
		List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String historyId = "";
            String versionId = "";
            String riskId = "";
            String riskName = "";
            String mainName = "";
            String relaName = "";
            String parentId = "";
            String score = "";
            if(null != objects[0]){
            	historyId = objects[0].toString();
            }if(null != objects[1]){
            	versionId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[3]){
            	riskName = objects[3].toString();
            }if(null != objects[4]){
            	mainName = objects[4].toString();
            }if(null != objects[5]){
            	relaName = objects[5].toString();
            }if(null != objects[6]){
            	String parentA[] = objects[6].toString().split("\\.");
            	if(parentA.length>0){
            		parentId = parentA[parentA.length-2];
            	}
            }if(null != objects[7]){
            	score = objects[7].toString();
            }
            Map<String, Object> riskMap = new HashMap<String, Object>();
            riskMap.put("historyId", historyId);
            riskMap.put("versionId", versionId);
            riskMap.put("riskId", riskId);
            riskMap.put("riskName", riskName);//风险名称
            riskMap.put("relaName", "/".equals(relaName)?"":relaName);//责任部门/人
            riskMap.put("mainName", "/".equals(mainName)?"":mainName);//相关部门/人
            riskMap.put("score", score);
            riskMap.put("linked", true);
            riskMap.put("leaf", true);
            if(childrenMap.get(parentId) != null){
            	childrenMap.get(parentId).add(riskMap);
            }else{
            	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            	arrayList.add(riskMap);
            	childrenMap.put(parentId, arrayList);
            }
		}
		return childrenMap;
	}
	
	/**
	 * 查询风险基本信息明细
	 * @param verId
	 * @param riskId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findRiskDetailByVerID(String historyId){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> inmap = new HashMap<String, Object>();
		//RiskHistory risk = o_riskHistoryDAO.get(historyId);
		Criteria criteria = o_riskHistoryDAO.createCriteria();
		criteria.add(Restrictions.eq("id", historyId));
		List<RiskHistory> risks = criteria.list();
		if(risks.size()>0){
			RiskHistory risk = risks.get(0);
			inmap.put("riskName", risk.getRiskName());
			inmap.put("riskCode", risk.getRiskCode());
			inmap.put("riskDesc", risk.getRiskDesc());
			inmap.put("parentName", risk.getParentName());
			inmap.put("mainDeptName", risk.getMainDeptName());
			inmap.put("relaDeptName", "/".equals(risk.getRelaDeptName())?"":risk.getRelaDeptName());
			inmap.put("processName", risk.getProcessName());
			inmap.put("kpiName", risk.getKpiName());
			map.put("data", inmap);
			map.put("success", true);
		}else{
			map.put("success", false);
		}
		return map;
	}
	
	/**
	 * 查询某一风险库的风险版本集合
	 * @param schm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NewRiskVersion> findRiskVersionsByschm(String schm,String deptId){
		Criteria criteria = o_newRiskVersionDAO.createCriteria();
		criteria.add(Restrictions.eq("schm", schm));
		if(null != deptId && !"".equals(deptId)){
			criteria.add(Restrictions.eq("createOrg", deptId));
		}
		List<NewRiskVersion> riskVersions = criteria.list();
		return riskVersions;
	}
	
	/**
	 * 根据版本id查询十大风险
	 * @param verId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskHistory> queryRiskrbsByVerid(String verId){
		List<RiskHistory> rbsList = new ArrayList<RiskHistory>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rh.ID,rh.RISK_NAME,rh.MAIN_DEPT_NAME,rh.RELA_DEPT_NAME,rh.ID_SEQ,rh.SCORE,rh.RISK_ID " +
				" FROM t_risk_history rh  " +
				" WHERE rh.VERSION_ID = :verId AND rh.IS_RISK_CLASS = 'rbs' ORDER BY rh.SCORE DESC ");
        SQLQuery sqlQuery = o_riskHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("verId", verId);
        List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String id = "";
            String riskName = "";
            String mainName = "";
            String relaName = "";
            String parentId = "";
            String score = "";
            String riskId = "";
            if(null != objects[0]){
            	id = objects[0].toString();
            }if(null != objects[1]){
            	riskName = objects[1].toString();
            }if(null != objects[2]){
            	mainName = objects[2].toString();
            }if(null != objects[3]){
            	relaName = objects[3].toString();
            }if(null != objects[4]){
            	String parentA[] = objects[4].toString().split("\\.");
            	if(parentA.length>0){
            		parentId = parentA[parentA.length-2];
            	}
            }if(null != objects[5]){
            	score = objects[5].toString();
            }if(null != objects[6]){
            	riskId = objects[6].toString();
            }
            if(null == parentId || "".equals(parentId)){//用IDSEQ过滤一级风险
            	RiskHistory rbs = new RiskHistory();
            	rbs.setId(id);
            	rbs.setRiskName(riskName);
            	rbs.setMainDeptName("/".equals(mainName)?"":mainName);
            	rbs.setRelaDeptName("/".equals(relaName)?"":relaName);
            	//rbs.setScore(Double.valueOf(score));
            	rbs.setRiskId(riskId);
            	rbs.setScoreStr(score);
            	rbs.setVerId(verId);
            	rbsList.add(rbs);
            }
		}
		return rbsList;
	}
	
	/**
	 *	查询风险事件明细
	 * @param verId
	 * @param rbsId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskHistory> queryRisksByRbsAndVerId(String verId, String rbsId){
		List<RiskHistory> rbsList = new ArrayList<RiskHistory>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT rh.ID,rh.RISK_NAME,rh.MAIN_DEPT_NAME,rh.RELA_DEPT_NAME,rh.ID_SEQ,rh.SCORE,rh.RISK_ID,rh.EDESC " +
				" FROM t_risk_history rh  " +
				" WHERE rh.VERSION_ID = :verId AND rh.IS_RISK_CLASS = 're' "
				+ "AND rh.ID_SEQ LIKE '%").append(rbsId).append("%'   ");
        SQLQuery sqlQuery = o_riskHistoryDAO.createSQLQuery(sql.toString());
        sqlQuery.setParameter("verId", verId);
        List<Object[]> list = sqlQuery.list();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String id = "";
            String riskName = "";
            String mainName = "";
            String relaName = "";
            String score = "";
            String riskId = "";
            String edesc = "";
            if(null != objects[0]){
            	id = objects[0].toString();
            }if(null != objects[1]){
            	riskName = objects[1].toString();
            }if(null != objects[2]){
            	mainName = objects[2].toString();
            }if(null != objects[3]){
            	relaName = objects[3].toString();
            }if(null != objects[5]){
            	score = objects[5].toString();
            }if(null != objects[6]){
            	riskId = objects[6].toString();
            }if(null != objects[7]){
            	edesc = objects[7].toString();
            }
            RiskHistory rbs = new RiskHistory();
        	rbs.setId(id);
        	rbs.setRiskName(riskName);
        	rbs.setMainDeptName("/".equals(mainName)?"":mainName);
        	rbs.setRelaDeptName("/".equals(relaName)?"":relaName);
        	rbs.setRiskId(riskId);
        	rbs.setScoreStr(score);
        	rbs.setVerId(verId);
        	rbs.setRiskDesc(edesc);
        	rbsList.add(rbs);
		}
		return rbsList;
	}
	/**
	 * 查询较上一版本新增或删除的风险事件
	 * @param verId
	 * @param isAdd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskHistory> findAddRisksByVerId(String verId,Boolean isAdd){
		List<RiskHistory> rbsList = new ArrayList<RiskHistory>();
		StringBuffer sql = new StringBuffer();
		NewRiskVersion lastVer = this.findLastVersionByVerNow(verId);
		sql.append(" SELECT rh.ID,rh.VERSION_ID,rh.RISK_ID,rh.RISK_CODE,rh.RISK_NAME,rh.EDESC,rh.PARENT_NAME,"
				+ "rh.MAIN_DEPT_NAME,rh.RELA_DEPT_NAME,rh.SCORE "
				+ "FROM t_risk_history rh "
				+ "WHERE rh.VERSION_ID = :verNowId AND rh.IS_RISK_CLASS = 're' AND rh.RISK_ID NOT IN "
				+ "(SELECT h.RISK_ID FROM t_risk_history h "
				+ "WHERE h.VERSION_ID = :lastVerId AND h.IS_RISK_CLASS = 're') ");
		SQLQuery sqlQuery = o_riskHistoryDAO.createSQLQuery(sql.toString());
		if(isAdd){//新增
			sqlQuery.setParameter("verNowId", verId);
	        sqlQuery.setParameter("lastVerId", lastVer.getId());
		}else{//删除
			sqlQuery.setParameter("verNowId", lastVer.getId());
	        sqlQuery.setParameter("lastVerId", verId);
		}
        List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();
            String id = "";
            String versionId = "";
            String riskId = "";
            String riskCode = "";
            String riskName = "";
            String riskDesc = "";
            String parentName = "";
            String mainName = "";
            String relaName = "";
            String score = "";
            
            if(null != objects[0]){
            	id = objects[0].toString();
            }if(null != objects[1]){
            	versionId = objects[1].toString();
            }if(null != objects[2]){
            	riskId = objects[2].toString();
            }if(null != objects[3]){
            	riskCode = objects[3].toString();
            }if(null != objects[4]){
            	riskName = objects[4].toString();
            }if(null != objects[5]){
            	riskDesc = objects[5].toString();
            }if(null != objects[6]){
            	parentName = objects[6].toString();
            }if(null != objects[7]){
            	mainName = objects[7].toString();
            }if(null != objects[8]){
            	relaName = objects[8].toString();
            }if(null != objects[9]){
            	score = objects[9].toString();
            }
            RiskHistory re = new RiskHistory();
            re.setId(id);
            re.setVerId(versionId);
            re.setRiskId(riskId);
            re.setRiskCode(riskCode);
            re.setRiskName(riskName);
            re.setRiskDesc(riskDesc);
            re.setParentName(parentName);
            re.setMainDeptName("/".equals(mainName)?"":mainName);
            re.setRelaDeptName("/".equals(relaName)?"":relaName);
            re.setScoreStr(score);
        	rbsList.add(re);
		}
		return rbsList;
	}
	
	/**
	 * 根据本版本查询上一个版本
	 * @param verNow
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public NewRiskVersion findLastVersionByVerNow(String verId){
		NewRiskVersion nowVer = o_newRiskVersionDAO.get(verId);
		Criteria criteria = o_newRiskVersionDAO.createCriteria();
		criteria.add(Restrictions.lt("createDate", nowVer.getCreateDate()));
		criteria.addOrder(Order.desc("createDate"));
		List<NewRiskVersion> versions = criteria.list();
		if(versions.size()>0){
			return versions.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 风险综合查询-根据查询条件查询风险事件列表
	 * @param form
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page<Map> queryRisksByQueryForm(RiskCompreQueryForm form,Page<Map> page,String sort, String dir){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT h.ID,h.VERSION_ID,h.RISK_ID,h.RISK_CODE,h.RISK_NAME,h.EDESC,h.PARENT_NAME, ");
		sql.append("h.MAIN_DEPT_NAME,h.RELA_DEPT_NAME,h.SCORE ");
		sql.append("FROM t_risk_history h ");
		sql.append("WHERE h.VERSION_ID = :verId AND h.IS_RISK_CLASS = 're' ");
		if(null != form.getRiskCode()){
			sql.append("AND h.RISK_CODE LIKE :riskCode ");
		}
		if(null != form.getRiskName()){
			sql.append("AND h.RISK_NAME = :riskName ");
		}
		if(null != form.getRiskDesc()){
			sql.append("AND h.EDESC = :riskDesc ");
		}
		if(null != form.getParentId()){//上级风险
			sql.append("AND h.PARENT_ID = :parentRiskId ");
		}
		if(null != form.getMainDeptId()){
			sql.append("AND h.MAIN_DEPT_NAME LIKE :mainDeptName ");
		}
		if(null != form.getRelaDeptId()){
			sql.append("AND h.RELA_DEPT_NAME LIKE :relaDeptName ");
		}
		SQLQuery sqlQuery = o_riskHistoryDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("verId", form.getRiskVersion());
  		
		if(null != form.getRiskCode()){
			sqlQuery.setParameter("riskCode", "%"+form.getRiskCode()+"%");
		}
		if(null != form.getRiskName()){
			sqlQuery.setParameter("riskName", form.getRiskName());
		}
		if(null != form.getRiskDesc()){
			sqlQuery.setParameter("riskDesc", form.getRiskDesc());
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
  			m.put("versionId", o[1].toString());
  			m.put("riskId", o[2].toString());
  			m.put("riskCode", o[3].toString());
  			m.put("riskName", o[4].toString());
  			m.put("riskDesc", o[5]==null?"":o[5].toString());
  			m.put("parentName", o[6]==null?"":o[6].toString());
  			m.put("mainName", "/".equals(o[7].toString())?"":o[7].toString());
  			m.put("relaName", "/".equals(o[8].toString())?"":o[8].toString());
  			m.put("score", null==o[9]?"":o[9].toString());
  			objList.add(m);
  		}
  		page.setResult(objList);
  		page.setTotalItems(size);
  		return page;
	}
}
