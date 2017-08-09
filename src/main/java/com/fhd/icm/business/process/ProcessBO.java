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
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.icsystem.ConstructPlanRelaStandardEmpDAO;
import com.fhd.dao.icm.icsystem.ConstructRelaProcessDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.process.ProcessRelaFileDAO;
import com.fhd.dao.process.ProcessRelaMeasureDAO;
import com.fhd.dao.process.ProcessRelaOrgDAO;
import com.fhd.dao.process.ProcessRelaRiskDAO;
import com.fhd.dao.process.ProcessRelaRuleDAO;
import com.fhd.dao.sys.dic.DictEntryDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.entity.icm.icsystem.ConstructPlanRelaStandard;
import com.fhd.entity.icm.icsystem.ConstructPlanRelaStandardEmp;
import com.fhd.entity.icm.icsystem.ConstructRelaProcess;
import com.fhd.entity.icm.standard.StandardRelaProcessure;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessRelaFile;
import com.fhd.entity.process.ProcessRelaMeasure;
import com.fhd.entity.process.ProcessRelaOrg;
import com.fhd.entity.process.ProcessRelaRule;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.bpm.ConstructPlanBpmBO;
import com.fhd.icm.interfaces.process.IProcessBO;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.icm.web.controller.bpm.icsystem.ConstructPlanBpmObject;
import com.fhd.icm.web.form.process.ProcessForm;
/**
 * 流程维护
 * @author   李克东
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-1-25		下午2:55:50
 *
 * @see 	 
 */
@Service
@SuppressWarnings("unchecked")
public class ProcessBO implements IProcessBO{
	
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private ConstructPlanBpmBO o_constructPlanBpmBO;
	@Autowired
	private ProcessRelaOrgDAO o_processRelaOrgDAO;
	@Autowired
	private ProcessRelaRuleDAO o_processRelaRuleDAO;
	@Autowired
	private ProcessRelaRiskDAO o_processRelaRiskDAO;
	@Autowired
	private ProcessRelaFileDAO o_processRelaFileDAO;
	@Autowired
	private DictEntryDAO o_dictEntryDAO;
	@Autowired
	private ConstructPlanRelaStandardEmpDAO o_constructPlanRelaStandardEmpDAO;
	@Autowired
	private ConstructRelaProcessDAO o_constructRelaProcessDAO;
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	@Autowired
	private ProcessRiskBO o_processRiskBO;
	@Autowired
	private ProcessRelaMeasureDAO o_processRelaMeasureDAO;
	
	/**
	 * <pre>
	 * 保存流程
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param process
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void mergeProcess(Process process){
		o_processDAO.merge(process);
	}
	
	/**
	 * 根据id查询流程.
	 * @author 吴德福
	 * @param processId
	 * @return Process
	 */
	public Process findProcessById(String processId) {
		return o_processDAO.get(processId);
	}

	/** (non-Javadoc)
	 * @see com.fhd.icm.interfaces.process.IProcessBO#findChildsProcessById(java.lang.String, boolean)
	 */
	@Override
	public List<Process> findProcessBySome(String processId ,String type,boolean self) {
		List<Process> processList = new ArrayList<Process>();
		if (StringUtils.isNotBlank(processId)) {
			Criteria criteria = this.o_processDAO.createCriteria();
			if (self) {// 包含自己
				criteria.add(Restrictions.or(
						Property.forName("parent.id").eq(processId),
						Property.forName("id").eq(processId)));
			} else {
				criteria.add(Property.forName("parent.id").eq(processId));
				criteria.add(Property.forName("type").eq(type));
			}
			processList = criteria.list();
		}
		return processList;
	}
	
	/**
	 * <pre>
	 * 通过部门Id查找关联的流程实体集合
	 * </pre>
	 * @author 刘中帅
	 * @param orgIdArray：部门Id数组
	 * @return 评价计划与部门关联实体的集合
	 * @since  fhd　Ver 1.1
	 */
	public List<Process> findProcessByOrgIds(String[] orgIdArray, String dealStatus){
		List<Process> processList=new ArrayList<Process>();
		Criteria criteria=o_processRelaOrgDAO.createCriteria();
		criteria.createAlias("process", "p");
		criteria.add(Restrictions.in("org.id", orgIdArray));
		criteria.add(Restrictions.eq("type", Contents.ORG_RESPONSIBILITY));
		//过滤--只查末级流程
		criteria.add(Restrictions.eq("p.isLeaf", true));
		if(StringUtils.isNotBlank(dealStatus)){
			criteria.add(Restrictions.eq("p.dealStatus", dealStatus));
		}
		List<ProcessRelaOrg> processRelaOrgList=criteria.list();
		for(ProcessRelaOrg processRelaOrg:processRelaOrgList){
			if(!processList.contains(processRelaOrg.getProcess())){
				processList.add(processRelaOrg.getProcess());
			}
		}
		return processList;
	}
	
	/**
	 * <pre>
	 * 通过名称查询流程实体集合
	 * </pre>
	 * @author 元杰
	 * @param processName 流程名称
	 * @since  fhd　Ver 1.1
	 */
	public List<Process> findProcessByName(String processName){
		Criteria criteria = o_processDAO.createCriteria();
		criteria.add(Restrictions.eq("name", processName));
		return criteria.list();
	}
	
	/**
	 * 查询流程对应的缺陷数量及缺陷状态
	 * @author 吴德福 
	 * @return List<Object[]>
	 */
	public List<Object[]> findProcessRelaDefectList(){
		StringBuilder sql = new StringBuilder();
		//通用的sql
		sql.append("select p.id,count(d.id),min(d.defect_level) ")
		  	.append("from t_ic_processure p left join t_ca_defect_assessment da on p.id=da.processure_id ")
		  	.append("left join t_ca_defect d on da.defect_id=d.id ")
		  	.append("where p.is_leaf = true ")
		  	.append("and p.company_id = :companyid ")
			.append("group by p.id ");
		
		SQLQuery sqlQuery = o_processDAO.createSQLQuery(sql.toString());
		
		sqlQuery.setParameter("companyid", UserContext.getUser().getCompanyid());
		
		return sqlQuery.list();
	}
	/**
	 * <pre>
	 * 通过流程Id查找相关联的实体集合
	 * </pre>
	 * @author 刘中帅
	 * @param processId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysOrganization> findSysOrganizationByProcessId(String[] processId,String type){
		List<ProcessRelaOrg> processRelaOrgList=null;
		List<SysOrganization> sysOrganizationList=new ArrayList<SysOrganization>();
		Criteria criteria=o_processRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.in("process.id", processId));
		criteria.add(Restrictions.eq("type", type));
		processRelaOrgList=criteria.list();
		for(ProcessRelaOrg processRelaOrg:processRelaOrgList){
			if(!sysOrganizationList.contains(processRelaOrg.getOrg())){
				sysOrganizationList.add(processRelaOrg.getOrg());
			}
		}
		return sysOrganizationList;
	}
	
	/**
	 * <pre>
	 * 通过流程Id查找相关联的实体集合
	 * </pre>
	 * @author 刘中帅
	 * @param processIds:流程Id集合
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<ProcessRelaOrg> findProcessRelaOrgByProcessId(String[] processId){
		Criteria criteria=o_processRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.in("process.id", processId));
		return criteria.list();
	}
	
	/**
	 * <pre>
	 * 通过流程Id查找相关联的实体集合
	 * </pre>
	 * @author 刘中帅
	 * @param processId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<SysEmployee> findSysEmployeeByProcessId(String[] processId,String type){
		List<ProcessRelaOrg> processRelaOrgList=null;
		List<SysEmployee> sysEmployeeList=new ArrayList<SysEmployee>();
		Criteria criteria=o_processRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.in("process.id", processId));
		criteria.add(Restrictions.eq("type", type));
		processRelaOrgList=criteria.list();
		for(ProcessRelaOrg processRelaOrg:processRelaOrgList){
			if(!sysEmployeeList.contains(processRelaOrg.getEmp())){
				sysEmployeeList.add(processRelaOrg.getEmp());
			}
		}
		return sysEmployeeList;
	}
	
	/**
	 * <pre>
	 * 通过流程Id查找相关联的实体集合.
	 * </pre>
	 * @author 吴德福
	 * @param processIds
	 * @return List<ProcessRelaOrg>
	 * @since  fhd　Ver 1.1
	 */
	public List<ProcessRelaOrg> findProcessRelaOrgByProcessIds(String processIds){
		Criteria criteria=o_processRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.in("process.id",processIds.split(",")));
		criteria.add(Restrictions.eq("type", Contents.EMP_RESPONSIBILITY));
		return criteria.list();
	}
	
	/**
	 * <pre>
	 * 通过流程Id查找相关联的实体集合
	 * </pre>
	 * @author 刘中帅
	 * @param processId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public ProcessRelaOrg findSysEmployeeByProcessId(String processId){
		ProcessRelaOrg processRelaOrg=null;
		Criteria criteria=o_processRelaOrgDAO.createCriteria();
		criteria.add(Restrictions.eq("process.id", processId));
		criteria.add(Restrictions.eq("type", Contents.EMP_RESPONSIBILITY));
		processRelaOrg=(ProcessRelaOrg)criteria.uniqueResult();
		return processRelaOrg;
	}
	
	/**
	 * <pre>
	 * 保存流程
	 * </pre>
	 * @author 李克东
	 * @param processForm
	 * @param parentId
	 * modify 宋佳 
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public Map<String,Object> saveProcess(ProcessForm processForm,String parentId,String companyId){
		Map<String,Object> map = new HashMap<String,Object>();
		if(!validCodeRepeat(processForm.getId(),processForm.getCode(),companyId)){
			map.put("success", false);
			map.put("info", "编号重复");
			return map;
		}
		Process process = this.findProcessById(processForm.getId());
		if(Contents.DEAL_STATUS_FINISHED.equals(processForm.getDealStatus())){
			//如果流程下无节点直接跳出程序
			List<ProcessPoint> processPointList = o_processPointBO.findProcessPointListByProcessId(process.getId());
			if(processPointList == null || processPointList.size()==0){
				map.put("pointInfo", "流程：["+process.getName()+"]下没有对应的流程节点,流程的处理状态不能为[已完成]！");
				map.put("success", false);
				return map;
			}
			List<Risk> riskList = o_processRiskBO.findRiskListByProcessId(process.getId());
			if(riskList == null || riskList.size()==0){
				map.put("pointInfo", "流程：["+process.getName()+"]下没有对应的风险,流程的处理状态不能为[已完成]！");
				map.put("success", false);
				return map;
			}
		}
		//主责部门关系保存
        if(StringUtils.isNotBlank(processForm.getOrgId())){
    		//先删除processRelaOrg中此流程对应的数据
    		Criteria criteria = o_processRelaOrgDAO.createCriteria();
    		criteria.add(Restrictions.eq("process.id", processForm.getId()));
    		List<ProcessRelaOrg> processRelaOrg = criteria.list();
    		for(ProcessRelaOrg pro : processRelaOrg){
    			o_processRelaOrgDAO.delete(pro.getId());
    		}
    		//再添加数据
        	ProcessRelaOrg processRealOrg=new ProcessRelaOrg();
            processRealOrg.setId(Identities.uuid());
     		String orgid=IcmStandardUtils.findIdbyJason(processForm.getOrgId(), "id");//将Json转换为需要的字符串
     		processRealOrg.setOrg(o_sysOrgDAO.get(orgid));     		
     		processRealOrg.setEmp(null);
     		processRealOrg.setType(Contents.ORG_RESPONSIBILITY);
     		processRealOrg.setProcess(process);
         	o_processRelaOrgDAO.merge(processRealOrg);
     	}	
        if(!StringUtils.isEmpty(processForm.getRelaOrgId())){
			String crdorgid=IcmStandardUtils.findIdbyJason(processForm.getRelaOrgId(), "id");
			String[] crdorgidArray = crdorgid.split(",");
			for(int i = 0;i < crdorgidArray.length;i++){
				Criteria criteria = o_processRelaOrgDAO.createCriteria();
	    		criteria.add(Restrictions.eq("process.id", processForm.getId()));
				ProcessRelaOrg processRealOrg=new ProcessRelaOrg();
				processRealOrg.setProcess(process);
				processRealOrg.setId(Identities.uuid());
				processRealOrg.setOrg(o_sysOrgDAO.get(crdorgidArray[i]));
				processRealOrg.setType(Contents.ORG_PARTICIPATION);
				o_processRelaOrgDAO.merge(processRealOrg);
			}
		}
        //责任人关系保存
     	if(StringUtils.isNotBlank(processForm.getEmpId())){
     		ProcessRelaOrg processRealOrg=new ProcessRelaOrg();
     		processRealOrg.setId(Identities.uuid());
     		SysEmployee emp=new SysEmployee();
     		processRealOrg.setEmp(emp);
     		String empid=IcmStandardUtils.findIdbyJason(processForm.getEmpId(), "id");
     		emp.setId(empid);
     		processRealOrg.setOrg(null);
     		processRealOrg.setType(Contents.EMP_RESPONSIBILITY);
     		processRealOrg.setProcess(process);
         	o_processRelaOrgDAO.merge(processRealOrg);
        }
     	//先删除processRelaOrg中此流程对应的数据
     	Criteria fileCriteria=o_processRelaFileDAO.createCriteria();
     	fileCriteria.add(Restrictions.eq("process.id", processForm.getId()));
     	List<ProcessRelaFile> fileList=fileCriteria.list();
     	for(ProcessRelaFile prr : fileList){
     		o_processRelaFileDAO.delete(prr.getId());
     	}
		if(StringUtils.isNotBlank(processForm.getFileId())){
			String[] fileIds = processForm.getFileId().split(",");
			String fileId ="";
			FileUploadEntity file = new FileUploadEntity();
			ProcessRelaFile processRelaFile = new ProcessRelaFile();
			
			for(int i=0;i<fileIds.length;i++){
				String ids=Identities.uuid();
				processRelaFile.setId(ids);
				fileId=fileIds[i];
				file.setId(fileId);
				processRelaFile.setFile(file);
				processRelaFile.setProcess(process);
				o_processRelaFileDAO.merge(processRelaFile);
			}
		}
		process.setDealStatus(processForm.getDealStatus());
		process.setCode(processForm.getCode());
		process.setName(processForm.getName());
		process.setControlTarget(processForm.getControlTarget());
		process.setDesc(processForm.getDesc());
		process.setSort(processForm.getSort());
		if(StringUtils.isBlank(processForm.getImportance().getId())){
			process.setImportance(null);
		}else{
			process.setImportance(processForm.getImportance());
		}
		process.setRelaSubject(processForm.getRelaSubject());
		if(processForm.getFrequency() == null){
			process.setFrequency(new DictEntry());
		}else{
			process.setFrequency(processForm.getFrequency());
		}
		process.setLastModifyBy(UserContext.getUser().getEmp());
		process.setLastModifyTime(new Date());
		o_processDAO.merge(process);
		map.put("success", true);
		return map;
	}
	public boolean validCodeRepeat(String id,String code,String companyId){
		boolean flag = true;
		if(id == null){  /*新增的判断 */
			Criteria criteria = o_processDAO.createCriteria();
			criteria.add(Restrictions.eq("code", code));
			criteria.add(Restrictions.eq("company.id", companyId));
			Process process = (Process)criteria.uniqueResult();
			if(process != null){
				flag = false;
			}
		}else{    /*修改的情况 */
			Criteria criteria = o_processDAO.createCriteria();
			criteria.add(Restrictions.eq("id", id));
			criteria.add(Restrictions.eq("company.id", companyId));
			Process process = (Process)criteria.uniqueResult();
			if(!code.equals(process.getCode())){
				Criteria cri = o_processDAO.createCriteria();
				cri.add(Restrictions.eq("code", code));
				cri.add(Restrictions.eq("company.id", companyId));
				Process pro = (Process)cri.uniqueResult();
				if(pro != null){
					flag = false;
				}
			}
		}
		return flag;
	}
	/**
	 * <pre>
	 * 保存流程
	 * </pre>
	 * @author 李克东
	 * @param processForm
	 * @param parentId
	 * modify 宋佳 
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public Map<String,Object> saveProcessFrame(ProcessForm processForm,String parentId,String companyId){
		Map<String,Object> map = new HashMap<String,Object>();
		if(!validCodeRepeat(processForm.getId(),processForm.getCode(),companyId)){
			map.put("success", false);
			map.put("info", "编号重复");
			return map;
		}
		String num = Identities.uuid();
		Process process = new Process();
		if(StringUtils.isNotBlank(processForm.getId())){
			process = this.findProcessById(processForm.getId());
			process.setLastModifyBy(UserContext.getUser().getEmp());
			process.setLastModifyTime(new Date());
		}else{
			process.setId(num);
			process.setCompany(new SysOrganization(companyId));
			process.setCreateTime(new Date());
			process.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			process.setIsLeaf(true);
			process.setDealStatus(Contents.DEAL_STATUS_NOTSTART);
			if(!StringUtils.isBlank(parentId)){
				if("root".equals(parentId))	{
					process.setIdSeq("."+num+".");
					process.setLevel(1);
					process.setParent(null);
				}else{			
					Criteria criteria = o_processDAO.createCriteria();
					criteria.add(Restrictions.eq("id", parentId));//获得父节点		
					Process processParent=(Process)criteria.uniqueResult();
					processParent.setIsLeaf(false);
					process.setIdSeq(processParent.getIdSeq()+num+".");
					if(processParent.getLevel()!=null){
						process.setLevel(processParent.getLevel()+1);
					}else{
						process.setLevel(process.getIdSeq().split(".").length-1);//-2+1
					}

					process.setParent(processParent);
				}
			}
		}
		process.setCode(processForm.getCode());
		process.setName(processForm.getName());
		process.setDesc(processForm.getDesc());
		process.setSort(processForm.getSort());
		o_processDAO.merge(process);
		map.put("success", true);
		return map;
	}
	
	/**
	 * <pre>
	 * 删除流程节点
	 * </pre>
	 * @author 李克东
	 * @param processID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeProcessByID(String processID){
		Process process = o_processDAO.get(processID);
		process.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
		o_processDAO.merge(process);
	}
	
	/**
	 * <pre>
	 * 加载流程表单
	 * </pre>
	 * @author 李克东
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Map<String,Object> findProcessForm(String processEditId){
		Process process = this.findProcessById(processEditId);
		Map<String, Object> formMap = new HashMap<String, Object>();

		if(null != process.getParent()){
			formMap.put("parentid", process.getParent().getId());
			formMap.put("parentprocess", process.getParent().getName());
		}
		formMap.put("name", process.getName());
		formMap.put("controlTarget", process.getControlTarget());
		formMap.put("desc", process.getDesc());
		formMap.put("sort", process.getSort());
		formMap.put("dealStatus", process.getDealStatus());
		Criteria orgcriteria = o_processRelaOrgDAO.createCriteria();
		orgcriteria.add(Restrictions.eq("process.id", processEditId));
		List<ProcessRelaOrg> orglist=orgcriteria.list();
		if(null != orglist && orglist.size()>0){
			JSONArray deptArr = new JSONArray();
			JSONArray deptempArr = new JSONArray();
			JSONArray empArr = new JSONArray();
			for(ProcessRelaOrg pro : orglist){
				if(Contents.ORG_RESPONSIBILITY.equals(pro.getType())){//责任部门
					JSONObject obj = new JSONObject();
					SysOrganization org = pro.getOrg();
					obj.put("id", org.getId());
					obj.put("deptno", org.getOrgcode());
					obj.put("deptname", org.getOrgname());
					deptArr.add(obj);					
				}
				if(Contents.ORG_PARTICIPATION.equals(pro.getType())){//相关部门
					JSONObject obj = new JSONObject();
					SysOrganization org = pro.getOrg();
					obj.put("id", org.getId());
					obj.put("deptno", org.getOrgcode());
					obj.put("deptname", org.getOrgname());
					deptempArr.add(obj);
				}
				if(Contents.EMP_RESPONSIBILITY.equals(pro.getType())){//责任人
					JSONObject obj = new JSONObject();
					SysEmployee emp = pro.getEmp();
					obj.put("id", emp.getId());
					obj.put("empno", emp.getEmpcode());
					obj.put("empname", emp.getEmpname());
					empArr.add(obj);
				}
			}
			formMap.put("orgId",deptArr.toString());
			formMap.put("relaOrgId",deptempArr.toString());
			formMap.put("empId",empArr.toString());
		}else{
			formMap.put("orgId","[]");
			formMap.put("relaOrgId","[]");
			formMap.put("empId","[]");
		}
		formMap.put("id", process.getId());
		if(null != process.getImportance()){
			formMap.put("importance",process.getImportance().getId());	
		}
		if(null != process.getFrequency()){
			formMap.put("frequency",process.getFrequency().getId());	
		}
		if(null != process.getRelaSubject()){
			formMap.put("relaSubject",process.getRelaSubject().split(","));
		}
		//制度
		Criteria ruleCriteria=o_processRelaRuleDAO.createCriteria();
		ruleCriteria.add(Restrictions.eq("process.id", processEditId));
		List<ProcessRelaRule> ruleList=ruleCriteria.list();
		if(ruleList.size()>0){			
			StringBuilder ruleIds=new StringBuilder();
			boolean flag = true;
			for(ProcessRelaRule processRelaRule:ruleList){
				String ruleId=processRelaRule.getRule().getId();
				if(flag){
					ruleIds.append(ruleId);
					flag = false;
				}else{
					ruleIds.append(",").append(ruleId);
				}
			}
			formMap.put("ruleId", ruleIds);
		}
		//文件
		Criteria fileCriteria=o_processRelaFileDAO.createCriteria();
		fileCriteria.add(Restrictions.eq("process.id", processEditId));
		List<ProcessRelaFile> fileList=fileCriteria.list();
		if(!fileList.isEmpty()){			
			StringBuilder fileIds=new StringBuilder();
			int i=0;
			for(ProcessRelaFile processRelaFile:fileList){
				String fileId=processRelaFile.getFile().getId();
				if(i!=fileList.size()-1){
					fileIds.append(fileId);
				}else{
					fileIds.append(",").append(fileId);
				}
				i++;
			}
			formMap.put("fileId", fileIds);
		}
		if(StringUtils.isNotBlank(process.getCode())){			
			formMap.put("code", process.getCode());
		}else{
			List<String> list=o_processDAO.find("select max(id) from Process");
			String maxprocessid=(String)list.get(0);
			int i=Integer.parseInt(maxprocessid);
			String processCode=Integer.toString(i+1);
			formMap.put("code","pr"+processCode);
		}
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
	}
	/**
	 * <pre>
	 * 加载流程表单
	 * </pre>
	 * @author 李克东
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> findConstructProcessFormForView(String processEditId){
		Process process=o_processDAO.get(processEditId);
		Map<String, Object> formMap = new HashMap<String, Object>();

		if(null != process.getParent()){
			formMap.put("parentprocess", process.getParent().getName());
		}
		formMap.put("name", process.getName());
		formMap.put("controlTarget", process.getControlTarget());
		formMap.put("desc", process.getDesc());
		formMap.put("sort", process.getSort());
		StringBuffer sb = new StringBuffer();
		if(null != process.getRelaSubject()){
			for(String subjectId : process.getRelaSubject().split(",")){
				sb.append(o_dictEntryDAO.get(subjectId).getName()).append(",");
			}
			formMap.put("relaSubject", sb.toString().substring(0,sb.toString().length()-1));
		}
		Criteria orgcriteria = o_processRelaOrgDAO.createCriteria();
		orgcriteria.add(Restrictions.eq("process.id", processEditId));
		List<ProcessRelaOrg> orglist=orgcriteria.list();
		if(null != orglist && orglist.size()>0){
			StringBuffer relaBuffer = new StringBuffer();
			for(ProcessRelaOrg pro : orglist){
				if(Contents.ORG_RESPONSIBILITY.equals(pro.getType())){//责任部门
					formMap.put("orgName",pro.getOrg().getOrgname());
				}
				if(Contents.ORG_PARTICIPATION.equals(pro.getType())){//相关部门
					relaBuffer.append(pro.getOrg().getOrgname());
					relaBuffer.append(",");
				}
				if(Contents.EMP_RESPONSIBILITY.equals(pro.getType())){//责任人
					formMap.put("empName",pro.getEmp().getEmpname());
				}
			}
			if(!StringUtils.isBlank(relaBuffer.toString())){
				formMap.put("relaOrgName",relaBuffer.toString().substring(0,relaBuffer.toString().length()-1));
			}
		}
		formMap.put("id", process.getId());
		if(null != process.getImportance()){
			formMap.put("controlLevelId",process.getImportance().getName());	
		}
		if(null != process.getFrequency()){
			formMap.put("frequency",process.getFrequency().getName());	
		}
		//制度
		Criteria ruleCriteria=o_processRelaRuleDAO.createCriteria();
		ruleCriteria.add(Restrictions.eq("process.id", processEditId));
		List<ProcessRelaRule> ruleList=ruleCriteria.list();
		if(ruleList.size()>0){			
			StringBuilder ruleNames=new StringBuilder();
			boolean flag = true;
			for(ProcessRelaRule processRelaRule:ruleList){
				String ruleName=processRelaRule.getRule().getName();
				if(flag){
					ruleNames.append(ruleName);
					flag = false;
				}else{
					ruleNames.append(",").append(ruleName);
				}
			}
			formMap.put("ruleName", ruleNames);
		}
		//文件
		Criteria fileCriteria=o_processRelaFileDAO.createCriteria();
		fileCriteria.add(Restrictions.eq("process.id", processEditId));
		List<ProcessRelaFile> fileList=fileCriteria.list();
		if(fileList.size()>0){			
			StringBuilder fileIds=new StringBuilder();
			for(ProcessRelaFile processRelaFile:fileList){
				String fileId=processRelaFile.getFile().getId();
				fileIds.append("<a href='javascript:void(0)'onclick=\"Ext.getCmp('floweditpanelforview').downloadFile('").append(fileId).append("');\" >").append(processRelaFile.getFile().getNewFileName()).append("</a>").append("||");
			}
			formMap.put("fileId", fileIds);
		}
		if(StringUtils.isNotBlank(process.getCode())){			
			formMap.put("code", process.getCode());
		}else{
			List<String> list=o_processDAO.find("select max(id) from Process");
			String maxprocessid=(String)list.get(0);
			int i=Integer.parseInt(maxprocessid);
			String processCode=Integer.toString(i+1);
			formMap.put("code","pr"+processCode);
		}
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
	}
	
	/**
	 * <pre>
	 * 加载流程选择
	 * </pre>
	 * @author 李克东
	 * @param processIds
	 * @return List<Process>
	 * @since  fhd　Ver 1.1
	*/
	public List<Process> findProcessByIds(String processIds){
		Criteria criteria = o_processDAO.createCriteria();
		criteria.add(Restrictions.in("id",processIds.split(",")));
		return criteria.list();
	}
	
	/**
	 * <pre>
	 * 编号自动生成
	 * </pre>
	 * @author 李克东
	 * @param processEditID
	 * @param parentId
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public Map<String,Object> findProcessCode(String processEditID,String parentId){
		Map<String,Object> processMap =new HashMap<String,Object>();
		if(StringUtils.isNotBlank(processEditID)){
			processMap.put("code","pr"+processEditID);
		}else{
			List<String> list=o_processDAO.find("select max(id) from Process");
			String maxprocessid=(String)list.get(0);
			int i=Integer.parseInt(maxprocessid);
			String processCode=Integer.toString(i+1);
			processMap.put("code","pr"+processCode);
		}
		
		Criteria criteria= o_processDAO.createCriteria();
		criteria.add(Restrictions.eq("id", parentId));
		List<Process> listmap=criteria.list();
		if(null != listmap && listmap.size()>0){
			Process processParent=listmap.get(0);
			processMap.put("Parent", processParent.getName());
		}
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", processMap);
		node.put("success", true);
		return node;
	}
	
	/**
	 * 根据流程id查询相关风险--修改此方法时，列顺序不要修改，要展示新的列直接放在最后面.
	 * @author 吴德福
	 * @param processId 流程id
	 * @return List<Object[]> 流程及相关风险信息
	 */
	public List<Object[]> findRiskStatusByProcessId(String processId){
		StringBuilder sb = new StringBuilder();
		sb.append("select p.id,p.processure_name,r.id,r.risk_name,h.id,h.assessement_status,h.etrend,h.adjust_time,h.impacts,h.probability,h.management_urgency,h.time_period_id ");
        sb.append("from t_ic_processure p, t_processure_risk_processure pr, t_rm_risks r, t_rm_risk_adjust_history h ");
        sb.append("where p.id=pr.processure_id and pr.risk_id=r.id and r.id=h.risk_id and h.is_latest=true and r.delete_estatus='1' ");
        Map<String,String> paramsMap = new HashMap<String,String>();
        if (StringUtils.isNotBlank(processId)){
            sb.append("and p.id=:processId ");
            paramsMap.put("processId", processId);
        }
        sb.append("order by h.assessement_status asc ");
		return o_processDAO.createSQLQuery(sb.toString(),paramsMap).list();
	}
	
	/**
	 * 本方法不再调用，学习使用
	 * 根据流程id查找流程下那所有节点
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findProcessListByPageBak(Page<ConstructPlanRelaStandardEmp> page, String query,String constructPlanId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ConstructPlanRelaStandardEmp.class);
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Property.forName("code").like(query, MatchMode.ANYWHERE), Property.forName("name").like(query, MatchMode.ANYWHERE)));
		}
		if (!StringUtils.isNotEmpty(constructPlanId)) {
			return null;
		}
		criteria.createAlias("constructPlanRelaStandard", "constructPlanRelaStandard",CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("constructPlanRelaOrg", "constructPlanRelaOrg",CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("constructPlanRelaOrg.emp", "emp",CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("constructPlanRelaStandard.constructPlan", "constructPlan",CriteriaSpecification.LEFT_JOIN);
		criteria.add(Restrictions.eq("constructPlan.id",constructPlanId));
		criteria.add(Restrictions.eq("emp.id",UserContext.getUserid()));
		Map<String, Object> result = new HashMap<String, Object>();
		List<ConstructPlanRelaStandardEmp> constructPlanRelaStandardEmpList =o_constructPlanRelaStandardEmpDAO.findPage(criteria,page,false).getResult();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Process process = null;
		for (ConstructPlanRelaStandardEmp constructPlanRelaStandardEmp : constructPlanRelaStandardEmpList) {
			Map<String, Object> map = new HashMap<String, Object>();
			Set<StandardRelaProcessure> standardRelaProcessures = constructPlanRelaStandardEmp.getConstructPlanRelaStandard().getStandard().getStandardRelaProcessure();
			for (StandardRelaProcessure standardRelaProcessure : standardRelaProcessures) {
				process =  standardRelaProcessure.getProcessure();
			}
			map.put("id", process.getId());
			map.put("processCode", process.getCode());
			map.put("processName", process.getName());
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", constructPlanRelaStandardEmpList.size());
		return result;
	}
	/**
	 * 根据流程id查找流程下那所有节点
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
		
	public Map<String, Object> findProcessListByPage(Page<ConstructPlanRelaStandardEmp> page, String query,String constructPlanId,String executionId,String orgScroll) {
		ConstructPlanBpmObject constructPlanBpmObject = o_constructPlanBpmBO.findBpmObjectByExecutionId(executionId,"item");
		String constructPlanRelaProcess = constructPlanBpmObject.getForeachExecutionId();
		String[] constructPlanRelaProcessArray = constructPlanRelaProcess.split(",");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		Process process = null;
		for(String constructPlanRelaProcessId : constructPlanRelaProcessArray){
			ConstructRelaProcess constructRelaProcess = o_constructRelaProcessDAO.get(constructPlanRelaProcessId);
			ConstructPlanRelaStandard constructPlanRelaStandard = constructRelaProcess.getConstructPlanRelaStandard();
			Set<StandardRelaProcessure> standardRelaProcessures = constructPlanRelaStandard.getStandard().getStandardRelaProcessure();
			for (StandardRelaProcessure standardRelaProcessure : standardRelaProcessures) {
				process =  standardRelaProcessure.getProcessure();
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", process.getId());
			map.put("standardName", constructPlanRelaStandard.getStandard().getParent().getName());
			map.put("standardRequir", constructPlanRelaStandard.getStandard().getName());
			map.put("processCode", process.getCode());
			map.put("processName", process.getName());   
			//设置操作的链接路径
			map.put("operate", "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+orgScroll+"').scollWindow('processEdit','"
					+ process.getId()
					+ "');\" >编辑</a>&nbsp;&nbsp;/&nbsp;&nbsp;"
					+	"<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+orgScroll+"').scollWindow('noteEdit','"
					+ process.getId()
					+ "');\" >流程节点维护</a>&nbsp;&nbsp;/&nbsp;&nbsp;"
					+ "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+orgScroll+"').scollWindow('riskEdit', '"
					+ process.getId() + "');\" >风险控制矩阵维护</a>");
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", constructPlanRelaProcessArray.length);
		return result;
	}
	/**
	 * 根据流程id查找流程下那所有节点
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findProcessListApproveByPage(Page<ConstructPlanRelaStandardEmp> page, String query,String constructPlanId,String executionId,String orgScroll) {
		ConstructPlanBpmObject constructPlanBpmObject = o_constructPlanBpmBO.findBpmObjectByExecutionId(executionId,"approveitem");
		String constructPlanRelaProcess = constructPlanBpmObject.getConstructPlanRelaProcessIds();
		String[] constructPlanRelaProcessArray = constructPlanRelaProcess.split(",");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		Process process = null;
		for(String constructPlanRelaProcessId : constructPlanRelaProcessArray){
			ConstructRelaProcess constructRelaProcess = o_constructRelaProcessDAO.get(constructPlanRelaProcessId);
			ConstructPlanRelaStandard constructPlanRelaStandard = constructRelaProcess.getConstructPlanRelaStandard();
			Set<StandardRelaProcessure> standardRelaProcessures = constructPlanRelaStandard.getStandard().getStandardRelaProcessure();
			for (StandardRelaProcessure standardRelaProcessure : standardRelaProcessures) {
				process =  standardRelaProcessure.getProcessure();
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", process.getId());
			map.put("standardName", constructPlanRelaStandard.getStandard().getParent().getName());
			map.put("standardRequir", constructPlanRelaStandard.getStandard().getName());
			map.put("processCode", process.getCode());
			map.put("processName", process.getName());
			//设置操作的链接路径
			map.put("operate", "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+orgScroll+"').scollWindow('processEdit','"
					+ process.getId()
					+ "');\" >查看流程信息</a>");
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", dataList.size());
		return result;
	}
	/**
	 * 流程修复查找待梳理流程列表
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findProcessListCompairByPage(Page<ConstructPlanRelaStandardEmp> page, String query,String constructPlanId,String executionId,String orgScroll) {
		ConstructPlanBpmObject constructPlanBpmObject = o_constructPlanBpmBO.findBpmObjectByExecutionId(executionId,"approveitem");
		String constructPlanRelaProcess = constructPlanBpmObject.getConstructPlanRelaProcessIds();
		String[] constructPlanRelaProcessArray = constructPlanRelaProcess.split(",");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		Process process = null;
		for(String constructPlanRelaProcessId : constructPlanRelaProcessArray){
			ConstructRelaProcess constructRelaProcess = o_constructRelaProcessDAO.get(constructPlanRelaProcessId);
			ConstructPlanRelaStandard constructPlanRelaStandard = constructRelaProcess.getConstructPlanRelaStandard();
			Set<StandardRelaProcessure> standardRelaProcessures = constructPlanRelaStandard.getStandard().getStandardRelaProcessure();
			for (StandardRelaProcessure standardRelaProcessure : standardRelaProcessures) {
				process =  standardRelaProcessure.getProcessure();
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", process.getId());
			map.put("standardName", constructPlanRelaStandard.getStandard().getParent().getName());
			map.put("standardRequir", constructPlanRelaStandard.getStandard().getName());
			map.put("processCode", process.getCode());
			map.put("processName", process.getName());
			//设置操作的链接路径
			map.put("operate", "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+orgScroll+"').scollWindow('processEdit','"
					+ process.getId()
					+ "');\" >编辑</a>&nbsp;&nbsp;/&nbsp;&nbsp;"
					+	"<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+orgScroll+"').scollWindow('noteEdit','"
					+ process.getId()
					+ "');\" >流程节点维护</a>&nbsp;&nbsp;/&nbsp;&nbsp;"
					+ "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+orgScroll+"').scollWindow('riskEdit', '"
					+ process.getId() + "');\" >风险控制矩阵维护</a>");
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", dataList.size());
		return result;
	}
	/**
	 *   获取流程对应的规章制度,流程bo暴漏出的对外接口，所有流程查询方面的内容全部封在这里面
	 *   add by 宋佳
	 */
	public List<ProcessRelaRule> findProcessRelaRules(String processId){
		Criteria processRelaRuleCriteria = o_processRelaRuleDAO.createCriteria();
		processRelaRuleCriteria.add(Restrictions.eq("process.id", processId));
		return processRelaRuleCriteria.list();
	}
	
	/**
	 * 获取流程主责部门和相关部门名称，对外提供接口
	 * @param process
	 * @return
	 */
	public String[] findProcessOrg(Process process){
		String responsibilityOrg = "";
		StringBuffer participationOrg = new StringBuffer();
		Set<ProcessRelaOrg> processRelaOrgs = process.getProcessRelaOrg();
		for (ProcessRelaOrg processRelaOrg : processRelaOrgs) {
			if (Contents.ORG_RESPONSIBILITY.equals(processRelaOrg.getType()) && null != processRelaOrg.getOrg()){ /*流程主责部门*/
				responsibilityOrg = processRelaOrg.getOrg().getOrgname();
			}else if(Contents.ORG_PARTICIPATION.equals(processRelaOrg.getType()) && null != processRelaOrg.getOrg()){
				participationOrg.append(processRelaOrg.getOrg().getOrgname()).append("、");   /*流程配合部门*/
			}
		}
		String orgParticipationStr = "";
		if(participationOrg.length()!=0){
			orgParticipationStr = participationOrg.substring(0, participationOrg.length()-1);
		}else{
			orgParticipationStr = "无";
		}
		
		return new String[]{responsibilityOrg,orgParticipationStr};
	}
	
	/**
	 * 查询子流程
	 * */
	public List<Process> findProcessByParent(String parentId){
		Criteria processRelaRuleCriteria = o_processDAO.createCriteria();
		processRelaRuleCriteria.add(Restrictions.eq("parent.id", parentId));
		return processRelaRuleCriteria.list();
	}
	
	/**
	 * 查询子流程,并已MAP方式存储(parentid,ArrayList(Process))
	 * */
	public Map<String, List<Process>> findProcessByParentMapAll(){
		Criteria processRelaRuleCriteria = o_processDAO.createCriteria();
		List<Process> processList = processRelaRuleCriteria.list();
		Map<String, List<Process>> map = new HashMap<String, List<Process>>();
		ArrayList<Process> lists = null;
		for (Process process : processList) {
			if(process.getParent() != null){
				if(map.get(process.getParent().getId()) != null){
					map.get(process.getParent().getId()).add(process);
				}else{
					lists = new ArrayList<Process>();
					lists.add(process);
					map.put(process.getParent().getId(), lists);
				}
			}
		}
		return map;
	}
	/**
	 *  根据风险id判断是否有关联流程
	 */
	public boolean findHasRelaFromRisk(String riskId,String processId){
		boolean flag = true;
		Criteria cra = o_processRelaRiskDAO.createCriteria();
		cra.add(Restrictions.eq("risk.id", riskId));
		cra.add(Restrictions.eq("process.id", processId));
		if(cra.list() != null && cra.list().size()>0){
			flag = false;
		}
		return flag;
	}
	/**
	 *  根据风险id判断是否有关联流程
	 */
	public List<ProcessRelaMeasure> findProcessFromMeasure(String measureId){
		Criteria cra = o_processRelaMeasureDAO.createCriteria();
		cra.add(Restrictions.eq("controlMeasure.id", measureId));
		cra.createAlias("process", "process");
		cra.setFetchMode("process", FetchMode.JOIN);
		return cra.list();
	}
	
	/**
	 * 根据公司id查询所有的流程集合.
	 * @author 吴德福
	 * @param companyId
	 * @return List<Process>
	 */
	public List<Process> findProcessListByCompanyId(String companyId){
		Criteria criteria = o_processDAO.createCriteria();
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return criteria.list();
	}
	
	/**
	 * 根据公司id查询所有的流程集合.
	 * @author 金鹏祥
	 * @param companyId
	 * @return List<Process>
	 */
	public Map<String, Process> findProcessMapByCompanyId(String companyId){
		Criteria criteria = o_processDAO.createCriteria();
		List<Process> list = null;
		Map<String, Process> map = new HashMap<String, Process>();
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		
		list = criteria.list();
		for (Process process : list) {
			map.put(process.getId(), process);
		}
		
		return map;
	}
	
	/**
	 * 根据公司id查询所有的流程集合.
	 * @author 金鹏祥
	 * @param companyId
	 * @return List<Process>
	 */
	public Map<String, List<Process>> findProcessByNameAllMap(){
		Criteria criteria = o_processDAO.createCriteria();
		List<Process> list = null;
		List<Process> processList = null;
		Map<String, List<Process>> map = new HashMap<String, List<Process>>();		
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		
		list = criteria.list();
		for (Process en : list) {
			if(map.get(en.getName()) != null){
				map.get(en.getName()).add(en);
			}else{
				processList = new ArrayList<Process>();
				processList.add(en);
				map.put(en.getName(), processList);
			}
		}
		
		return map;
	}
	
	/**
	 * 根据公司id查询所有的流程集合.
	 * @author 金鹏祥
	 * @param companyId
	 * @return List<Process>
	 */
	public Map<String, Process> findProcessByNameEAllMap(){
		Criteria criteria = o_processDAO.createCriteria();
		List<Process> list = null;
		Map<String, Process> map = new HashMap<String, Process>();		
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		
		list = criteria.list();
		for (Process en : list) {
			map.put(en.getName(), en);
		}
		
		return map;
	}
}