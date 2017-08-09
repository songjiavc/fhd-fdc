package com.fhd.ra.business.risk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.dao.sys.orgstructure.SysOrganizationDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.icm.business.process.ProcessRiskBO;
import com.fhd.ra.business.response.ResponseSolutionBO;

@Service
public class ImportRiskBO {
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private RiskCmpBO o_riskCmpBO;
	
	@Autowired
	private	SysOrganizationDAO o_sysOrganizationDAO;
	
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	
	@Autowired
	private ProcessRiskBO o_processRiskBO;
	
	@Autowired
	private ResponseSolutionBO o_responseSolutionBO;
	
	/**
	 * copy一个公司的风险库到另外一个公司
	 * 算法：
	 * 1、新copy的风险，必须重新制定id
	 * 2、按风险层级一层一层copy,这样可以记录上级风险新的id
	 * 3、为能找到之前的风险，利用oldRiskId当做key,值是新copy过来的风险，数据结构：oldRiskMap
	 * @param fromCompanyId
	 * @param toCompanyId
	 * @param createBy
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean saveRiskList(String fromCompanyId,String toCompanyId,String createBy){

		//查询当前风险库下的所有风险和风险事件
		List<Risk> riskList = this.findRiskAndEventByCompany(fromCompanyId);
		//获取风险的最大层数
		int count = 4;
		
		//构建idArr
		String[] riskIdArr = new String[riskList.size()];
		for(int i=0;i<riskList.size();i++){
			riskIdArr[i] = riskList.get(i).getId();
		}
		
		List<Risk> addRiskList = new ArrayList<Risk>();		//新添加的风险列表
		Map<String,Risk> oldRiskMap = new HashMap<String,Risk>();	//通过之前的老riskID,能够定位到新copy的risk
		for(int i=0;i<count;i++){	//一层一层的构建数据，保证id，parentId,idSeq正确性
			List<Risk> list =  this.filterRiskByLevel(riskList,(i+1));
			for(Risk r : list){
				Risk risk = new Risk();
				
				//自身属性
				String id = UUID.randomUUID().toString();
				risk.setId(id);
				if(r.getParent()==null){	//一级节点
					risk.setParent(null);
					risk.setIdSeq("."+id+".");
				}else{
					Risk parent = oldRiskMap.get(r.getParent().getId());
					risk.setParent(parent);
					if(parent!=null){
						risk.setIdSeq(parent.getIdSeq()+id+".");
					}
				}
				SysOrganization company = new SysOrganization();
				company.setId(toCompanyId);
				risk.setCompany(company);
				risk.setCreateBy(createBy);
				risk.setCreateTime(new Date());
				risk.setLastModifyTime(new Date());
				
				//copy过来的属性,不包括扩展信息
				risk.setParentName(r.getParentName());
				risk.setCode(r.getCode());
				risk.setName(r.getName());
				risk.setDesc(r.getDesc());
				risk.setDeleteStatus(r.getDeleteStatus());
				risk.setArchiveStatus(r.getArchiveStatus());
				risk.setLevel(r.getLevel());
				risk.setIsLeaf(r.getIsLeaf());
				risk.setIsRiskClass(r.getIsRiskClass());
				risk.setSort(r.getSort());
				
				//相关部门，责任部门
				
				//指标
				
				//流程
				
				addRiskList.add(risk);
				oldRiskMap.put(r.getId(), risk);
			}
			
		}
		
		//批量保存风险表
		batchUpdateRiskData(addRiskList);
//		for(Risk risk : addRiskList){
//			o_riskDAO.merge(risk);
//		}
		
		//批量保存责任部门和相关部门.注意，改变公司，可能部门的名称不一样，id更不一样，这块用名称匹配一个
		this.saveRiskOrgInfo(oldRiskMap,toCompanyId);
		
		//保存风险关联控制内容和风险控制关联关系   add by songjia
		//o_processRiskBO.saveMeasureByRiskId(oldRiskMap,toCompanyId);
		
		//保存风险关联应对措施信息
		//o_responseSolutionBO.saveSolutionByRiskId(oldRiskMap, toCompanyId);
				
		return true;
	}
	
	@Transactional
	private void batchUpdateRiskData(final List<Risk> riskList){
		o_riskDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String sql = "insert t_rm_risks(id,parent_id,parent_name,id_seq,company_id,create_by,create_time,last_modify_time,risk_code,risk_name,edesc,delete_estatus,archive_status,elevel,is_leaf,is_risk_class,esort) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement pst = connection.prepareStatement(sql);
				for (Risk risk : riskList) {
					pst.setObject(1, risk.getId());
					pst.setObject(2, risk.getParent()==null?null:risk.getParent().getId());
					pst.setObject(3, risk.getParentName());
					pst.setObject(4, risk.getIdSeq());
					pst.setObject(5, risk.getCompany().getId());
					pst.setObject(6, risk.getCreateBy());
					pst.setObject(7, risk.getCreateTime());
					pst.setObject(8, risk.getLastModifyTime());
					pst.setObject(9, risk.getCode());
					pst.setObject(10, risk.getName());
					pst.setObject(11, risk.getDesc());
					pst.setObject(12, risk.getDeleteStatus());
					pst.setObject(13, risk.getArchiveStatus());
					pst.setObject(14, risk.getLevel());
					pst.setObject(15, risk.getIsLeaf());
					pst.setObject(16, risk.getIsRiskClass());
					pst.setObject(17, risk.getSort());
					pst.addBatch();
				}
				pst.executeBatch();
				connection.commit();
				connection.setAutoCommit(true);
				
				
			}
		});
		o_riskDAO.getSession().flush();
	}
	
	/**
	 * 按照level过滤，得到风险列表
	 * @param companyId
	 * @return
	 */
	private List<Risk> filterRiskByLevel(List<Risk> allList,int level) {
		List<Risk> riskList = new ArrayList<Risk>();
		for(Risk r : allList){
			if(r.getLevel()!=null && r.getLevel()==level){
				riskList.add(r);
			}
		}
		return riskList;
	}
	
	/**
	 * 查询公司下全部风险和风险事件,归档的和没有删除的
	 * 要级联带出责任部门和相关部门等相关信息
	*/
	@SuppressWarnings("unchecked")
	private List<Risk> findRiskAndEventByCompany(String companyId) {
		Criteria criteria = o_riskDAO.createCriteria();
		//关联上级风险
		criteria.createAlias("parent", "parent",CriteriaSpecification.LEFT_JOIN);
		criteria.setFetchMode("parent", FetchMode.SELECT);
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("archiveStatus", Contents.RISK_STATUS_ARCHIVED));
		List<Risk> risks = criteria.list();
		return risks;
	}
	
	/**
	 * 保存导入风险的相关部门和责任部门
	 * 批量保存责任部门和相关部门.注意，改变公司，可能部门的名称不一样，id更不一样，这块用名称匹配一个
	 * @return
	 */
	private boolean saveRiskOrgInfo(Map<String,Risk> oldRiskMap,String toCompanyId){
		for(Map.Entry<String, Risk> entry : oldRiskMap.entrySet()){
			String oldRiskId = entry.getKey();
			Risk risk  = entry.getValue();
			
			//查询之前风险关联的部门
			Criteria c = o_riskOrgDAO.createCriteria();
			c.createAlias("sysOrganization", "sysOrganization");
			c.setFetchMode("sysOrganization", FetchMode.SELECT);
			c.add(Restrictions.eq("risk.id", oldRiskId));
			List<RiskOrg> riskOrgList = (List<RiskOrg>)c.list();
			
			for(RiskOrg ro : riskOrgList){
				String orgName = ro.getSysOrganization().getOrgname();
				String orgId = this.findOrgIdByOrgName(toCompanyId, orgName);
				
				if(orgId!=null){
					//保存该公司的责任部门和相关部门
					RiskOrg riskOrg = new RiskOrg();
					riskOrg.setId(UUID.randomUUID().toString());
					riskOrg.setType(ro.getType());
					riskOrg.setRisk(risk);
					SysOrganization sysOrganization = new SysOrganization();
					sysOrganization.setId(orgId);
					riskOrg.setSysOrganization(sysOrganization);
					o_riskOrgDAO.merge(riskOrg);
				}

			}
		}
		
		return true;
	}
	
	/**
	 * 根据部门名称匹配得到本公司对应的公司id
	 * @author zhengjunxiang
	 * @return
	 */
	private String findOrgIdByOrgName(String companyId,String orgName){
		Criteria c = o_sysOrganizationDAO.createCriteria();
		c.add(Restrictions.eq("company.id", companyId));
		c.add(Restrictions.eq("deleteStatus", "1"));
		c.add(Restrictions.like("orgname", orgName, MatchMode.ANYWHERE));
		c.setMaxResults(1);
		List<SysOrganization> list = (List<SysOrganization>)c.list();
		if(list==null || list.size()==0){
			return null;
		}
		return list.get(0).getId();
	}
	
	/**
	 * 构建某公司下的风险树
	 * @author zhengjunxiang
	 * @id 风险id  null表示根节点，其他值表示上级风险id
	 * @companyId 公司id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findRiskTreeRecord(String id,String companyId){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		
		Criteria criteria = o_riskDAO.createCriteria();

		if(id==null){
			criteria.add(Restrictions.isNull("parent.id"));
		}else{
			criteria.add(Restrictions.eq("parent.id", id));
		}
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("archiveStatus", "archived"));
		criteria.addOrder(Order.asc("sort"));
		List<Risk> list = criteria.list();
		String[] riskIdArr = new String[list.size()];
		int i = 0;
		for (Risk risk : list) {
			riskIdArr[i++] = risk.getId();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", risk.getId());
			map.put("text", risk.getName());
			map.put("code", risk.getCode());
			map.put("name", risk.getName());
			//map.put("leaf", risk.getIsLeaf());//要修正数据
			map.put("leaf", !this.riskHasChildren(risk.getId(),companyId));//要修正数据
			
			nodes.add(map);
		}
		
		//风险的责任部门和相关部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(riskIdArr);
		Map<String,Object> dutyDepartmentMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relativeDepartmentMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//影响部门
		for (Map<String, Object> m : nodes) {
			String rid = m.get("id").toString();
			m.put("dutyDepartment",dutyDepartmentMap.get(rid)==null?"":dutyDepartmentMap.get(rid).toString());
			m.put("relativeDepartment",relativeDepartmentMap.get(rid)==null?"":relativeDepartmentMap.get(rid).toString());
		}
		
		return nodes;
	}
	
	@SuppressWarnings("unchecked")
	private Boolean riskHasChildren(String parentId,String companyId){
		boolean hasChildren = false;
		
		Criteria criteria = o_riskDAO.createCriteria();
		criteria.add(Restrictions.eq("parent.id", parentId));
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("deleteStatus", "1"));
		criteria.add(Restrictions.eq("archiveStatus", "archived"));
		criteria.setProjection(Projections.rowCount());
		int count = Integer.parseInt(criteria.uniqueResult().toString());
		if(count>0){
			hasChildren = true;
		}
		
		return hasChildren;
	}
	
	/**
	 * 获取集团下所有子公司，包括集团
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysOrganization> findAllCompanyAndGroup(){
		Criteria c = o_sysOrganizationDAO.createCriteria();
		c.add(Restrictions.in("orgType", new String[]{"0orgtype_c","0orgtype_sc"}));
		return (List<SysOrganization>)c.list();
	}
	
	/**
	 * 获取集团下所有子公司，包括集团,层级结构
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findHierarchyCompany(){
		Criteria c = o_sysOrganizationDAO.createCriteria();
		c.add(Restrictions.eq("orgType", "0orgtype_c"));
		c.setMaxResults(1);
		SysOrganization group = (SysOrganization)c.uniqueResult();
		
		Map<String,Object> map = this.getSubCompanyNode(group.getId());
		map.put("expanded", true);
		return map;
	}
	private Map<String,Object> getSubCompanyNode(String orgId){
		Map<String,Object> map = new HashMap<String,Object>();
		SysOrganization self =o_sysOrganizationDAO.get(orgId);
		map.put("id", orgId);
		map.put("text", self.getOrgname());
		List<SysOrganization> children = getSubCompanyList(orgId);
		if(children!=null && children.size()>0){
			List<Map<String,Object>> childrenNode = new ArrayList<Map<String,Object>>();
			for(SysOrganization org : children){
				Map<String,Object> node = getSubCompanyNode(org.getId());
				childrenNode.add(node);
			}
			map.put("children", childrenNode);
		}else{
			map.put("leaf", true);
		}
		
		return map;
	}
	@SuppressWarnings("unchecked")
	private List<SysOrganization> getSubCompanyList(String parentOrgId){
		Criteria c = o_sysOrganizationDAO.createCriteria();
		c.add(Restrictions.eq("parentOrg.id", parentOrgId));
		c.add(Restrictions.eq("orgType", "0orgtype_sc"));
		return c.list();
	}
}
