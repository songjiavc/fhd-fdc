package com.fhd.icm.business.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
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
import com.fhd.dao.icm.assess.AssessPointDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.process.ProcessPointDAO;
import com.fhd.dao.process.ProcessPointRelaOrgDAO;
import com.fhd.dao.process.ProcessPointRelaPointSelfDAO;
import com.fhd.dao.process.ProcessPointRelaRiskDAO;
import com.fhd.dao.process.ProcessRelaOrgDAO;
import com.fhd.dao.process.ProcessRelaRiskDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.icm.assess.AssessPoint;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessPointRelaOrg;
import com.fhd.entity.process.ProcessPointRelaPointSelf;
import com.fhd.entity.process.ProcessPointRelaRisk;
import com.fhd.entity.process.ProcessRelaOrg;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.icm.interfaces.process.IProcessPointBO;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.icm.web.form.process.ProcessPointForm;
import com.fhd.sys.web.form.dic.DictEntryForm;
/**
 * 流程节点维护
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-3-11		下午1:17:50
 *
 * @see 	 
 */
@Service
@SuppressWarnings("unchecked")
public class ProcessPointBO implements IProcessPointBO{
	
	@Autowired
	private ProcessPointDAO o_processpointDAO;
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private ProcessPointRelaOrgDAO o_procespointRelaOrgDAO;
	@Autowired
	private ProcessRelaOrgDAO o_procesRelaOrgDAO;
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private ProcessPointRelaPointSelfDAO o_processPointRelaPointSelfDAO;
	@Autowired
	private AssessPointDAO o_assessPointDAO;
	@Autowired
	private ProcessPointRelaRiskDAO o_processPointRelaRiskDAO;   
	@Autowired
	private ProcessRelaRiskDAO o_processRelaRiskDAO;   
	
	@Override
	public ProcessPoint findProcessPointById(String processpointId) {
		Criteria criteria = o_processpointDAO.createCriteria();
		criteria.add(Restrictions.eq("id", processpointId));
		return (ProcessPoint) criteria.uniqueResult();
	}
	/**
	 * <pre>
	 *   保存流程,流程节点直接修改，流程节点关联关系表 先删后加
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processPointForm
	 * @param parentId
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public Map<String,Object> saveProcessPoint(ProcessPointForm processForm){
		ProcessPoint processpoint = new ProcessPoint();
		Map<String,Object> map = new HashMap<String,Object>();
		if(!validCodeRepeat(processForm.getEditProcessPointId(),processForm.getCode(),processForm.getParentid())){
			map.put("success", false);
			map.put("info", "编号重复");
			return map;
		}
		if(StringUtils.isEmpty(processForm.getEditProcessPointId())){
			processpoint.setId(Identities.uuid());
			processpoint.setCode(processForm.getCode());
			processpoint.setName(processForm.getName());
			processpoint.setProcess(o_processDAO.get(processForm.getParentid()));
			processpoint.setDesc(processForm.getDesc());
			processpoint.setSort(processForm.getSort());
			processpoint.setRelaProcess(processForm.getRelaProcess());
			processpoint.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			processpoint.setInfoInput(processForm.getInfoInput());
			processpoint.setInfoOutput(processForm.getInfoOutput());
			if(StringUtils.isNotBlank(processForm.getPointTypeId())){
				//增加流程节点和选择流程两个字段
				processpoint.setPointType(new DictEntry(processForm.getPointTypeId()));
			}else{
				processpoint.setPointType(null);
			}
			o_processpointDAO.merge(processpoint);    //保存到节点表
			//保存到关联的部门表中1 : 增加主管部门；2：增加配合部门；3：增加责任人
			ProcessPointRelaRisk processPointRelaRisk = new ProcessPointRelaRisk();
			ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
			if(StringUtils.isNotBlank(processForm.getRiskId())){
				String riskIds=IcmStandardUtils.findIdbyJason(processForm.getRiskId(), "id");
				String[] riskIdArray = riskIds.split(",");
				for(String riskId : riskIdArray){
					processPointRelaRisk.setId(Identities.uuid());
					processPointRelaRisk.setProcess(new Process(processForm.getParentid()));
					processPointRelaRisk.setProcessPoint(processpoint);
					processPointRelaRisk.setRisk(new Risk(riskId));
					o_processPointRelaRiskDAO.merge(processPointRelaRisk);
					/*判断流程和风险表中是否已经存在关系，如果没有则增加关系*/
					Criteria criteria = o_processRelaRiskDAO.createCriteria();
					criteria.add(Restrictions.eq("process.id", processForm.getParentid()));
					criteria.add(Restrictions.eq("risk.id", riskId));
					List<ProcessPointRelaRisk> processPointRelaRiskList = criteria.list();
					if(processPointRelaRiskList.size()==0){
						processRelaRisk.setId(Identities.uuid());
						processRelaRisk.setProcess(new Process(processForm.getParentid()));
						processRelaRisk.setRisk(new Risk(riskId));
						processRelaRisk.setType(Contents.INFLUENCE_PROCESS);
						o_processRelaRiskDAO.merge(processRelaRisk);
					}
				}
			}
			ProcessPointRelaOrg processpointrelaorg = new ProcessPointRelaOrg();
			if(!StringUtils.isEmpty(processForm.getOrgId())){
				processpointrelaorg.setId(Identities.uuid());
				processpointrelaorg.setProcessPoint(processpoint);
				String orgid=IcmStandardUtils.findIdbyJason(processForm.getOrgId(), "id");
				processpointrelaorg.setOrg(o_sysOrgDAO.get(orgid));
				processpointrelaorg.setType(Contents.ORG_RESPONSIBILITY);
				o_procespointRelaOrgDAO.merge(processpointrelaorg);
			}
			if(!StringUtils.isEmpty(processForm.getCrdorgId())){
				String crdorgid=IcmStandardUtils.findIdbyJason(processForm.getCrdorgId(), "id");
				String[] crdorgidArray = crdorgid.split(",");
				for(int i = 0;i < crdorgidArray.length;i++){
					processpointrelaorg = new ProcessPointRelaOrg();
					processpointrelaorg.setProcessPoint(processpoint);
					processpointrelaorg.setId(Identities.uuid());
					processpointrelaorg.setOrg(o_sysOrgDAO.get(crdorgidArray[i]));
					processpointrelaorg.setType(Contents.ORG_PARTICIPATION);
					o_procespointRelaOrgDAO.merge(processpointrelaorg);
				}
			}
			if(!StringUtils.isEmpty(processForm.getEmpId())){
				processpointrelaorg = new ProcessPointRelaOrg();
				processpointrelaorg.setProcessPoint(processpoint);
				processpointrelaorg.setId(Identities.uuid());
				String empid=IcmStandardUtils.findIdbyJason(processForm.getEmpId(), "id");
				processpointrelaorg.setEmp(o_sysEmployeeDAO.get(empid));
				processpointrelaorg.setType(Contents.EMP_RESPONSIBILITY);
				o_procespointRelaOrgDAO.merge(processpointrelaorg);
			}
		}else{
			//修改流程节点
			processpoint = this.findProcessPointById(processForm.getEditProcessPointId());
			processpoint.setCode(processForm.getCode());
			processpoint.setName(processForm.getName());
			if(StringUtils.isNotBlank(processForm.getPointTypeId())){
				//增加流程节点和选择流程两个字段
				processpoint.setPointType(new DictEntry(processForm.getPointTypeId()));
			}else{
				processpoint.setPointType(null);
			}
			processpoint.setProcess(o_processDAO.get(processForm.getParentid()));
			processpoint.setDesc(processForm.getDesc());
			processpoint.setSort(processForm.getSort());
			processpoint.setInfoInput(processForm.getInfoInput());
			processpoint.setInfoOutput(processForm.getInfoOutput());
			o_processpointDAO.merge(processpoint);    //保存到节点表
			//删除关联关系表
			Criteria orgcriteria = o_procespointRelaOrgDAO.createCriteria();
			orgcriteria.createAlias("processPoint", "o").add(Restrictions.eq("o.id",processForm.getEditProcessPointId()));
			List<ProcessPointRelaOrg> orglist=orgcriteria.list();
			for(ProcessPointRelaOrg processpointrelaorg : orglist){
				o_procespointRelaOrgDAO.delete(processpointrelaorg);
			}
			// 删除节点关联风险时，是否删除其流程对应的风险
//			Set<ProcessRelaRisk> processRelaRiskSet = o_processDAO.get(processForm.getParentid()).getProcessRelaRisks();
//			for(ProcessRelaRisk processRelaRisk : processRelaRiskSet){
//				o_processRelaRiskDAO.delete(processRelaRisk);
//			}
			Set<ProcessPointRelaRisk> processPointRelaRiskSet = processpoint.getProcessPointRelaRisks();
			for(ProcessPointRelaRisk processPointRelaRisk : processPointRelaRiskSet){
				o_processPointRelaRiskDAO.delete(processPointRelaRisk);
			}
			ProcessRelaRisk processRelaRisk = new ProcessRelaRisk();
			ProcessPointRelaRisk processPointRelaRisk = new ProcessPointRelaRisk();
			if(!"[]".equals(processForm.getRiskId())){
				String riskIds=IcmStandardUtils.findIdbyJason(processForm.getRiskId(), "id");
				String[] riskIdArray = riskIds.split(",");
				for(String riskId : riskIdArray){
					processPointRelaRisk.setId(Identities.uuid());
					processPointRelaRisk.setProcess(new Process(processForm.getParentid()));
					processPointRelaRisk.setProcessPoint(processpoint);
					processPointRelaRisk.setRisk(new Risk(riskId));
					o_processPointRelaRiskDAO.merge(processPointRelaRisk);
					/*判断流程和风险表中是否已经存在关系，如果没有则增加关系*/
					Criteria criteria = o_processRelaRiskDAO.createCriteria();
					criteria.add(Restrictions.eq("process.id", processForm.getParentid()));
					criteria.add(Restrictions.eq("risk.id", riskId));
					List<ProcessPointRelaRisk> processPointRelaRiskList = criteria.list();
					if(processPointRelaRiskList.size()==0){
						processRelaRisk.setId(Identities.uuid());
						processRelaRisk.setProcess(new Process(processForm.getParentid()));
						processRelaRisk.setRisk(new Risk(riskId));
						processRelaRisk.setType(Contents.INFLUENCE_PROCESS);
						o_processRelaRiskDAO.merge(processRelaRisk);
					}
				}
			}
			//保存到关联的部门表中1 : 增加主管部门；2：增加配合部门；3：增加责任人
			ProcessPointRelaOrg processpointrelaorg = new ProcessPointRelaOrg();
			if(!StringUtils.isEmpty(processForm.getOrgId())){
				processpointrelaorg.setId(Identities.uuid());
				processpointrelaorg.setProcessPoint(processpoint);
				String orgid=IcmStandardUtils.findIdbyJason(processForm.getOrgId(), "id");
				processpointrelaorg.setOrg(o_sysOrgDAO.get(orgid));
				processpointrelaorg.setType(Contents.ORG_RESPONSIBILITY);
				o_procespointRelaOrgDAO.merge(processpointrelaorg);
			}
			if(!StringUtils.isEmpty(processForm.getCrdorgId())){
				String crdorgid=IcmStandardUtils.findIdbyJason(processForm.getCrdorgId(), "id");
				String[] crdorgidArray = crdorgid.split(",");
				for(int i = 0;i < crdorgidArray.length;i++){
					processpointrelaorg = new ProcessPointRelaOrg();
					processpointrelaorg.setProcessPoint(processpoint);
					processpointrelaorg.setId(Identities.uuid());
					processpointrelaorg.setOrg(o_sysOrgDAO.get(crdorgidArray[i]));
					processpointrelaorg.setType(Contents.ORG_PARTICIPATION);
					o_procespointRelaOrgDAO.merge(processpointrelaorg);
				}
			}
			if(!StringUtils.isEmpty(processForm.getEmpId())){
				processpointrelaorg = new ProcessPointRelaOrg();
				processpointrelaorg.setProcessPoint(processpoint);
				processpointrelaorg.setId(Identities.uuid());
				String empid=IcmStandardUtils.findIdbyJason(processForm.getEmpId(), "id");
				processpointrelaorg.setEmp(o_sysEmployeeDAO.get(empid));
				processpointrelaorg.setType(Contents.EMP_RESPONSIBILITY);
				o_procespointRelaOrgDAO.merge(processpointrelaorg);
			}
			}
		    //保存可编辑列表的内容
			this.saveParentPointEditGrid(processpoint.getId(), processForm.getEditGridJson());
			//保存评价点信息
			this.saveAssessPointEditGrid(processpoint.getId(),"", processForm.getAssessEditGridJson(),Contents.ASSESS_POINT_TYPE_DESIGN);
			map.put("success", true);
			return map;
		}
	/**
	 * 判断是否有重复的编号
	 * add by songjia
	 * @param id
	 * @param code
	 * @param companyId
	 * @return
	 */
	public boolean validCodeRepeat(String id,String code,String processId){
		boolean flag = true;
		if(id == null){  /*新增的判断 */
			Criteria criteria = o_processpointDAO.createCriteria();
			criteria.add(Restrictions.eq("code", code));
			criteria.add(Restrictions.eq("process.id", processId));
			ProcessPoint process = (ProcessPoint)criteria.uniqueResult();
			if(process != null){
				flag = false;
			}
		}else{    /*修改的情况 */
			Criteria criteria = o_processpointDAO.createCriteria();
			criteria.add(Restrictions.eq("id", id));
			ProcessPoint process = (ProcessPoint)criteria.uniqueResult();
			if(!code.equals(process.getCode())){
				Criteria cri = o_processpointDAO.createCriteria();
				cri.add(Restrictions.eq("code", code));
				cri.add(Restrictions.eq("process.id", processId));
				ProcessPoint pro = (ProcessPoint)cri.uniqueResult();
				if(pro != null){
					flag = false;
				}
			}
		}
		return flag;
	}
	/**
	 * <pre>
	 *		删除流程节点
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeProcessPointByID(String processPointID){
		//删除与机关部门关联关系表
		Criteria orgcriteria = o_procespointRelaOrgDAO.createCriteria();
		orgcriteria.createAlias("processPoint", "o").add(Restrictions.in("o.id",processPointID.split(",")));
		List<ProcessPointRelaOrg> orglist=orgcriteria.list();
		for(ProcessPointRelaOrg processpointrelaorg : orglist){
			o_procespointRelaOrgDAO.delete(processpointrelaorg);
		}
		//删除父节点对应关系 
		Criteria parentcriteria = o_processPointRelaPointSelfDAO.createCriteria();
		parentcriteria.createAlias("processPoint", "processPoint");
		parentcriteria.createAlias("previousProcessPoint", "previousProcessPoint");
		parentcriteria.add(Restrictions.or(Restrictions.in("processPoint.id", processPointID.split(",")), Restrictions.in("previousProcessPoint.id",processPointID.split(","))));
		List<ProcessPointRelaPointSelf> parantSelfList=parentcriteria.list();
		for(ProcessPointRelaPointSelf parantSelf : parantSelfList){
			o_processPointRelaPointSelfDAO.delete(parantSelf);
		}
		//删除评价点表
		Criteria criteriaAssess = o_assessPointDAO.createCriteria();
		criteriaAssess.add(Restrictions.in("processPoint.id",processPointID.split(",")));
		List<AssessPoint> assessPointList = criteriaAssess.list(); 
		for(AssessPoint assesspoint : assessPointList ){
			o_assessPointDAO.delete(assesspoint);
		}
		removeProcessPointByIds(processPointID);
//		o_processpointDAO.delete(processpoint);
	}
	/**
	 * <pre>
	 *	    删除对应父亲节点
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processID
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeParentPointByID(String id){
		//删除关联关系表
		Criteria orgcriteria = o_processPointRelaPointSelfDAO.createCriteria();
		ProcessPointRelaPointSelf processpointrelapointself = (ProcessPointRelaPointSelf) orgcriteria.add(Restrictions.eq("id", id)).uniqueResult() ;
		o_processPointRelaPointSelfDAO.delete(processpointrelapointself);
	}
	/**
	 * <pre>
	 *加载流程表单，将数据库中信息写入表单
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Map<String,Object> findProcessPointForm(String processEditId,String processId){
		Map<String, Object> formMap = new HashMap<String, Object>();
		if(!StringUtils.isBlank(processEditId)){
			Criteria criteria = o_processpointDAO.createCriteria();
			criteria.add(Restrictions.eq("id", processEditId));
			ProcessPoint processPoint=(ProcessPoint) criteria.uniqueResult();
			Set<ProcessPointRelaRisk> processPointRelaRiskSet = processPoint.getProcessPointRelaRisks();
			JSONArray ja = new JSONArray();
			for(ProcessPointRelaRisk processPointRelaRisk : processPointRelaRiskSet){
				JSONObject jb = new JSONObject();
				jb.put("id", processPointRelaRisk.getRisk().getId());
				ja.add(jb);
			}
			formMap.put("riskId", ja.toString());
			formMap.put("code", processPoint.getCode());
			formMap.put("name", processPoint.getName());
			formMap.put("desc", processPoint.getDesc());
			formMap.put("sort", processPoint.getSort());
			formMap.put("infoInput", processPoint.getInfoInput());
			formMap.put("infoOutput", processPoint.getInfoOutput());
			if(null!=processPoint.getPointType()){
				formMap.put("pointTypeId",processPoint.getPointType().getId());
			}

			Criteria orgcriteria = o_procespointRelaOrgDAO.createCriteria();
			orgcriteria.createAlias("processPoint", "o").add(Restrictions.eq("o.id",processEditId));
			List<ProcessPointRelaOrg> orglist=orgcriteria.list();
			JSONArray deptArr = new JSONArray();
			JSONArray empArr = new JSONArray();
			JSONArray deptempArray = new JSONArray();
			for(ProcessPointRelaOrg processpointrelaorg : orglist){
				 if(Contents.ORG_RESPONSIBILITY.equalsIgnoreCase(processpointrelaorg.getType())){
					 //formMap.put("orgId","[{\"id\":\""+processpointrelaorg.getOrg().getId()+"\",\"deptno\":\"\",\"deptname\":\"\"}]");
					 JSONObject obj = new JSONObject();
					 SysOrganization org = processpointrelaorg.getOrg();
					 obj.put("id", org.getId());
					 obj.put("deptno", org.getOrgcode());
					 obj.put("deptname", org.getOrgname());
					 deptArr.add(obj);
				 }else if(Contents.EMP_RESPONSIBILITY.equalsIgnoreCase(processpointrelaorg.getType())){
					 //formMap.put("empId","[{\"id\":\""+processpointrelaorg.getEmp().getId()+"\",\"deptno\":\"\",\"deptname\":\"\"}]");
					 JSONObject obj = new JSONObject();
					 SysEmployee emp = processpointrelaorg.getEmp();
					 obj.put("id", emp.getId());
					 obj.put("empno", emp.getEmpcode());
					 obj.put("empname", emp.getEmpname());
					 empArr.add(obj);
				 }else if(Contents.ORG_PARTICIPATION.equalsIgnoreCase(processpointrelaorg.getType())){
					 JSONObject obj = new JSONObject();
					 SysOrganization org = processpointrelaorg.getOrg();
					 obj.put("id", org.getId());
					 obj.put("deptno", org.getOrgcode());
					 obj.put("deptname", org.getOrgname());
					 deptempArray.add(obj);
				 }
			}
			formMap.put("orgId",deptArr.toString());
			formMap.put("empId",empArr.toString());
			formMap.put("CrdorgId",deptempArray.toString());
		}else{
			Criteria orgprocesscriteria = o_procesRelaOrgDAO.createCriteria();
			orgprocesscriteria.add(Restrictions.eq("process.id", processId));
			List<ProcessRelaOrg> orgProcesslist=orgprocesscriteria.list();
			if(null != orgProcesslist && orgProcesslist.size()>0){
				for(ProcessRelaOrg pro : orgProcesslist){
					if(Contents.ORG_RESPONSIBILITY.equals(pro.getType())){//责任部门
						formMap.put("orgId","[{\"id\":\""+pro.getOrg().getId()+"\",\"deptno\":\""+pro.getOrg().getOrgcode()+"\",\"deptname\":\""+pro.getOrg().getOrgname()+"\"}]");
					}
					if(Contents.ORG_PARTICIPATION.equals(pro.getType())){//相关部门
						formMap.put("relaOrgId","[{\"id\":\""+pro.getOrg().getId()+"\",\"deptno\":\""+pro.getOrg().getOrgcode()+"\",\"deptname\":\""+pro.getOrg().getOrgname()+"\"}]");
					}
					if(Contents.EMP_RESPONSIBILITY.equals(pro.getType())){//责任人
						formMap.put("empId","[{\"id\":\""+pro.getEmp().getId()+"\",\"empno\":\""+pro.getEmp().getEmpcode()+"\",\"empname\":\""+pro.getEmp().getEmpname()+"\"}]");
					}
				}
			}
		}
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
		
	}
	/**
	 * <pre>
	 *加载流程表单，将数据库中信息写入表单
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Map<String,Object> findProcessPointFormForView(String processEditId){
		ProcessPoint processPoint = o_processpointDAO.get(processEditId);
		Map<String, Object> formMap = new HashMap<String, Object>();
		formMap.put("code", processPoint.getCode());
		formMap.put("name", processPoint.getName());
		formMap.put("desc", processPoint.getDesc());
		formMap.put("sort", processPoint.getSort());
		formMap.put("infoInput", processPoint.getInfoInput());
		formMap.put("infoOutput", processPoint.getInfoOutput());
		if(!(processPoint.getPointType()==null)){
			formMap.put("pointType",processPoint.getPointType().getName());
		}
//		if(!(processPoint.getRelaProcess() == null )){  
//			formMap.put("relaProcess",processPoint.getRelaProcess().getName());
//		}
		Criteria orgcriteria = o_procespointRelaOrgDAO.createCriteria();
		orgcriteria.createAlias("processPoint", "o").add(Restrictions.eq("o.id",processEditId));
		List<ProcessPointRelaOrg> orglist=orgcriteria.list();
		StringBuffer sb = new StringBuffer();
		for(ProcessPointRelaOrg processpointrelaorg : orglist){
			 if(Contents.ORG_RESPONSIBILITY.equalsIgnoreCase(processpointrelaorg.getType())){
				 formMap.put("orgName",processpointrelaorg.getOrg().getOrgname());
			 }else if(Contents.EMP_RESPONSIBILITY.equalsIgnoreCase(processpointrelaorg.getType())){
				 formMap.put("empName",processpointrelaorg.getEmp().getEmpname()+"("+processpointrelaorg.getEmp().getEmpcode()+")");
			 }else if(Contents.ORG_PARTICIPATION.equalsIgnoreCase(processpointrelaorg.getType())){
				 sb.append(processpointrelaorg.getOrg().getOrgname()).append(",");
			 }
		}
		if(!StringUtils.isBlank(sb.toString())){
			formMap.put("CrdorgName",sb.toString().substring(0,sb.toString().length()-1));
		}else{
			formMap.put("CrdorgName","无");
		}
		Criteria criteria = o_processPointRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "risk");
		criteria.add(Restrictions.eq("risk.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		criteria.setFetchMode("risk", FetchMode.JOIN);
		criteria.add(Restrictions.eq("processPoint.id", processEditId));
		List<ProcessPointRelaRisk> processPointRelaRiskList = criteria.list();
		StringBuffer riskNameSb = new StringBuffer();
		for(ProcessPointRelaRisk processPointRelaRisk : processPointRelaRiskList){
			if(StringUtils.isBlank(riskNameSb.toString())){
				riskNameSb.append(processPointRelaRisk.getRisk().getName()).append("(").append(processPointRelaRisk.getRisk().getCode()).append(")");
			}else{
				riskNameSb.append(";").append(processPointRelaRisk.getRisk().getName()).append("(").append(processPointRelaRisk.getRisk().getCode()).append(")");
			}
		}
		if(!StringUtils.isBlank(riskNameSb.toString())){
			formMap.put("riskId",riskNameSb.toString());
		}else{
			formMap.put("riskId","无");
		}
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
		
	}
	/**
	 * <pre>
	 *加载流程选择
	 * </pre>
	 * 
	 * @author 李克东
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Map<String,Object>> findProcessPointByIds(String processpointIds){
		
		String[] idArray=processpointIds.split(",");
		Criteria criteria = o_processpointDAO.createCriteria();
		criteria.add(Restrictions.in("id",idArray));
		List<Process> processList=criteria.list();
		List<Map<String,Object>> lm=new ArrayList<Map<String,Object>>();
		for(Process process:processList){
			Map<String, Object> mapBean = new HashMap<String, Object>();
			mapBean.put("id", process.getId());
			mapBean.put("dbid", process.getId());
			mapBean.put("code", process.getCode());
			mapBean.put("text", process.getName());
			lm.add(mapBean);
		}
		return lm;
		
	}
	/**
	 * <pre>
	 *编号自动生成
	 * </pre>
	 * 
	 * @author 李克东
	 * @param processEditID
	 * @param parentId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public Map<String,Object> findProcessPointCode(String processEditID,String parentId)
	{
		Map<String,Object> processMap =new HashMap<String,Object>();
		processMap.put("code",DateUtils.formatDate(new Date(), "yyyyMMddhhmmssSSS"));
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", processMap);
		node.put("success", true);
		return node;
	}
	/**
	 * 根据流程id查找流程下那所有节点
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findProcessPointListByPage(Page<ProcessPoint> page, String query,String processId,String companyId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ProcessPoint.class);
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Property.forName("code").like(query, MatchMode.ANYWHERE), Property.forName("name").like(query, MatchMode.ANYWHERE)));
		}
		if (!StringUtils.isNotEmpty(processId)) {
			return null;
		}
		criteria.createAlias("process", "o").add(Restrictions.eq("o.id",processId));
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		criteria.addOrder(Order.asc("code"));
		Map<String, Object> result = new HashMap<String, Object>();
		List<ProcessPoint> processPointList =o_processpointDAO.findPage(criteria,page,false).getResult();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (ProcessPoint processpoint : processPointList) {
			Map<String, Object> map = new HashMap<String, Object>();
			Set<ProcessPointRelaOrg> processpointRelaOrgList = processpoint.getProcessPointRelaOrg();
			for(ProcessPointRelaOrg processpointrelaorg : processpointRelaOrgList){
				 if(Contents.ORG_RESPONSIBILITY.equalsIgnoreCase(processpointrelaorg.getType())){
					 map.put("orgName", processpointrelaorg.getOrg().getOrgname());
				 }else if(Contents.EMP_RESPONSIBILITY.equalsIgnoreCase(processpointrelaorg.getType())){
					 map.put("responsilePersionId", processpointrelaorg.getEmp().getEmpname()+"("+processpointrelaorg.getEmp().getEmpcode()+")");
				 }
			}
			map.put("code", processpoint.getCode());
			map.put("text", processpoint.getName());
			map.put("name", processpoint.getName());
			map.put("id", processpoint.getId());
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", page.getTotalItems());

		return result;
	}
	/**
	 * 根据流程id查找流程下那所有节点 不翻页
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public List<ProcessPoint> findProcessPointListByProcessId(String processId) {
		Criteria pointCtr = o_processpointDAO.createCriteria();
		pointCtr.createAlias("process", "o").add(Restrictions.eq("o.id",processId));
		pointCtr.add(Restrictions.eq("deleteStatus",Contents.DELETE_STATUS_USEFUL));
		pointCtr.addOrder(Order.asc("code"));
		return pointCtr.list();
	}
	/**
	 * 根据节点ID 找到上级节点和节点进入条件
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findParentListByPointId(String processPointId,String companyId) {
		Criteria criteria = o_processPointRelaPointSelfDAO.createCriteria();
		criteria.createAlias("processPoint", "o").add(Restrictions.eq("o.id",processPointId));
		Map<String, Object> result = new HashMap<String, Object>();
		List<ProcessPointRelaPointSelf> processpointrelapointselfList = criteria.list();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for(ProcessPointRelaPointSelf processpointrelapointself : processpointrelapointselfList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", processpointrelapointself.getId());
			map.put("pointId", processpointrelapointself.getProcessPoint().getId());
			map.put("processId", processpointrelapointself.getProcess().getId());
			map.put("pointName", processpointrelapointself.getProcessPoint().getName());
			map.put("pointPreId", processpointrelapointself.getPreviousProcessPoint().getId());
			map.put("pointPreName", processpointrelapointself.getPreviousProcessPoint().getName());
			map.put("contition", processpointrelapointself.getDesc());
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", processpointrelapointselfList.size());
		return result;
	}
	
	public List<DictEntryForm> findAllProcessPointByProcessId(String processId){
		Criteria criteria = o_processpointDAO.createCriteria().setCacheable(true);
		criteria.createAlias("process", "o").add(Restrictions.eq("o.id",processId));
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<ProcessPoint> list= criteria.list();
		List<DictEntryForm> result=new ArrayList<DictEntryForm>();
		for(ProcessPoint entry:list){
		    DictEntryForm form=new DictEntryForm();
		    form.setId(entry.getId());
		    form.setName(entry.getName());
		    result.add(form);
		}
		return result;
	}
	
	@Transactional
	public void saveParentPointEditGrid(String processPointId,String modifiedRecord) {
		JSONArray jsonArray=JSONArray.fromObject(modifiedRecord);
		if(!jsonArray.isEmpty()){
			try {
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					String id = jsonObject.get("id")==null?"":jsonObject.get("id").toString();
					String pointId = processPointId;
					String processId = jsonObject.getString("processId");
					String poiintPreId = jsonObject.getString("pointPreId");
					String contition = jsonObject.getString("contition");
					if(StringUtils.isNotBlank(poiintPreId)){
						ProcessPointRelaPointSelf self = new ProcessPointRelaPointSelf();
						if("".equalsIgnoreCase(id)){
							id = Identities.uuid();
						}
						self.setId(id);
						self.setProcess(o_processDAO.get(processId));
						self.setProcessPoint(o_processpointDAO.get(pointId));
						self.setPreviousProcessPoint(o_processpointDAO.get(poiintPreId));
						self.setDesc(contition);
						o_processPointRelaPointSelfDAO.merge(self);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 保存评价点信息
	 * @author zhengjunxiang
	 * @param processPointId
	 * @param measureId
	 * @param modifiedRecord
	 * @param type
	 */
	@Transactional
	public void saveAssessPointEditGrid(String processPointId,String measureId,String modifiedRecord,String type) {
		JSONArray jsonArray=JSONArray.fromObject(modifiedRecord);
		if(!jsonArray.isEmpty()){
			try {
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					String id = jsonObject.get("id")==null?"":jsonObject.get("id").toString();
					String pointId = processPointId;
					String processId = jsonObject.getString("processId");
					String desc = jsonObject.getString("assessDesc");
					String comment = jsonObject.getString("comment");
					AssessPoint self = new AssessPoint();
					if("".equalsIgnoreCase(id)){
						id = Identities.uuid();
					}
					self.setId(id);
					self.setProcess(o_processDAO.get(processId));
					if(StringUtils.isNotBlank(pointId)){
						self.setProcessPoint(new ProcessPoint(pointId));
					}else{
						self.setProcessPoint(null);
					}
					if(StringUtils.isNotBlank(measureId)){
						self.setControlMeasure(new Measure(measureId));
					}else{
						self.setControlMeasure(null);
					}
					self.setDesc(desc);
					self.setComment(comment);
					self.setType(type);
					o_assessPointDAO.merge(self);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <pre>
	 * 删除流程节点信息
	 * </pre>
	 * @author 李克东
	 * @param processID
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeProcessPointByIds(String processPointIds){
		for(String processPointId : processPointIds.split(",")){
			ProcessPoint processPoint = o_processpointDAO.get(processPointId);
			processPoint.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
			o_processpointDAO.merge(processPoint);
		}
	}
	
	/**
	 * <pre>
	 * 根据一些条件查询流程节点的关系
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param processId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<ProcessPointRelaPointSelf> findProcessPointRelaPointSelfBySome(String processId){
		Criteria criteria = o_processPointRelaPointSelfDAO.createCriteria();
		if(StringUtils.isNotBlank(processId)){
			criteria.add(Restrictions.eq("process.id", processId));
		}else{
			criteria.add(Restrictions.isNull("id"));
		}
		return criteria.list();
	}
	
	/**
	 * <pre>
	 * getFirstControlPoint:获得第一个节点的列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param processureId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<ProcessPoint> getFirstControlPoint(String processId){
		return o_processpointDAO.find("from ProcessPoint pp where pp.process.id=? and pp.id not in (select pprps.processPoint.id from ProcessPointRelaPointSelf pprps) and pp.deleteStatus='"+Contents.DELETE_STATUS_USEFUL+"'", processId);
		
	}
	
	/**
	 * <pre>
	 * getLastControlPoint:获得最后一个节点的列表
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param processureId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<ProcessPoint> getLastControlPoint(String processId){
		return o_processpointDAO.find("from ProcessPoint pp where pp.process.id=? and pp.id not in (select pprps.previousProcessPoint.id from ProcessPointRelaPointSelf pprps) and pp.deleteStatus='"+Contents.DELETE_STATUS_USEFUL+"'", processId);
	}
	/**
	 * 根据公司id查询所有的流程节点集合.
	 * @author 吴德福
	 * @param companyId
	 * @return List<ProcessPoint>
	 */
	public List<ProcessPoint> findProcessPointByCompanyId(String companyId){
		Criteria criteria = o_processpointDAO.createCriteria();
		criteria.createAlias("process", "p");
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		criteria.add(Restrictions.eq("p.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		criteria.add(Restrictions.eq("p.company.id", companyId));
		return criteria.list();
	}
	/**
	 * 根据公司id查询流程节点关系集合.
	 * @author 吴德福
	 * @param companyId
	 * @return List<ProcessPointRelaPointSelf>
	 */
	public List<ProcessPointRelaPointSelf> findProcessPointRelaPointSelfByCompanyId(String companyId){
		Criteria criteria = o_processPointRelaPointSelfDAO.createCriteria();
		criteria.createAlias("process", "p");
		criteria.createAlias("p.company", "c");
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("c.id", companyId));
		}
		return criteria.list();
	}
}