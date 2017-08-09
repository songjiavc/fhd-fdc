package com.fhd.icm.business.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
import com.fhd.dao.icm.assess.AssessPointDAO;
import com.fhd.dao.icm.assess.MeasureDAO;
import com.fhd.dao.icm.assess.MeasureRelaOrgDAO;
import com.fhd.dao.icm.assess.MeasureRelaRiskDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.process.ProcessPointDAO;
import com.fhd.dao.process.ProcessPointRelaMeasureDAO;
import com.fhd.dao.process.ProcessPointRelaRiskDAO;
import com.fhd.dao.process.ProcessRelaMeasureDAO;
import com.fhd.dao.process.ProcessRelaRiskDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.sys.dic.DictEntryDAO;
import com.fhd.dao.sys.organization.SysOrgDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.icm.assess.AssessPoint;
import com.fhd.entity.icm.control.Measure;
import com.fhd.entity.icm.control.MeasureRelaOrg;
import com.fhd.entity.icm.control.MeasureRelaRisk;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessPointRelaMeasure;
import com.fhd.entity.process.ProcessPointRelaRisk;
import com.fhd.entity.process.ProcessRelaMeasure;
import com.fhd.entity.process.ProcessRelaOrg;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.interfaces.process.IProcessRiskBO;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.icm.web.form.process.RiskMeasureForm;
import com.fhd.ra.business.risk.ProcessRelaRiskBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;
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
public class ProcessRiskBO implements IProcessRiskBO{
	
	@Autowired
	private ProcessPointDAO o_processpointDAO;
	@Autowired
	private ProcessRelaRiskDAO o_processRelaRiskDAO;
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private SysOrgDAO o_sysOrgDAO;
	@Autowired
    private OrganizationBO o_organizationBO;
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	@Autowired
	private DictEntryDAO o_dictEntryDAO;
	@Autowired
	private AssessPointDAO o_assessPointDAO;
	@Autowired
	private MeasureRelaRiskDAO o_measureRelaRiskDAO;
	@Autowired
	private ProcessRelaMeasureDAO o_processRelaMeasureDAO;
	@Autowired
	private MeasureRelaOrgDAO o_measureRelaOrgDAO;
	@Autowired
	private ProcessPointRelaMeasureDAO o_processPointRelaMeasureDAO;
	@Autowired
	private RiskDAO o_riskDAO;
	@Autowired
	private MeasureDAO o_measureDAO;   
	@Autowired
	private ProcessRelaRiskBO o_processRelaRiskBO;   
	@Autowired
	private ProcessPointRelaRiskDAO o_processPointRelaRiskDAO;   
	@Autowired
	private ProcessRelaMeasureBO o_processRelaMeasureBO;   
	/**
	 * <pre>
	 *   保存风险信息
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processPointForm
	 * @param parentId
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public Map<String,Object> saveRiskMeasure(RiskMeasureForm riskForm){
		Map<String,Object> map = new HashMap<String,Object>();

		/*判断页面上是否有重复的*/
		JSONArray msForm=JSONArray.fromObject(riskForm.getMeasureFormstr());
		if(msForm==null){
			msForm = new JSONArray();
		}
		for(int i=0;i<msForm.size(); i++){
			for(int j=i+1;j<msForm.size();j++){
				JSONObject jsonObjecti = msForm.getJSONObject(i);
				JSONObject jsonObjectj = msForm.getJSONObject(j);
				if(jsonObjecti.getString("measurecode").equals(jsonObjectj.getString("measurecode"))){
					map.put("success", false);
					map.put("info", "控制措施编号重复");
					return map;
				}
			}
			if(!validMeasureCodeRepeat(riskForm.getCode(),riskForm.getProcessRiskId())){
				map.put("success", false);
				map.put("info", "控制措施编号重复");
				return map;
			}
		}
		
		ProcessRelaRisk proRelaRisk = new ProcessRelaRisk();    // 风险和流程关系实体
		Measure measure = null;
		MeasureRelaRisk measureRelaRisk = null;
		ProcessRelaMeasure processRelaMeasure = null;
		ProcessPointRelaMeasure processPointRelaMeasure = null;
		//获得机构对象
        SysOrganization company = o_organizationBO.get(UserContext.getUser().getCompanyid());
        SysEmployee employee = new SysEmployee();
        employee.setId(UserContext.getUser().getUserid());
        if(StringUtils.isEmpty(riskForm.getProcessRiskId())){    //如果风险Id为空，说明为新增内容
			//保存风险和流程关系信息
        	proRelaRisk.setId(Identities.uuid());
			proRelaRisk.setProcess(o_processDAO.get(riskForm.getProcessId()));
			proRelaRisk.setRisk(new Risk(riskForm.getId()));
			proRelaRisk.setType("I");
			o_processRelaRiskDAO.merge(proRelaRisk);
        }else{
			// 删除风险对应的控制措施
			Criteria riskrelameasure = o_measureRelaRiskDAO.createCriteria();
			riskrelameasure.createAlias("risk", "risk");
			riskrelameasure.add(Restrictions.eq("risk.id", riskForm.getProcessRiskId()));
			List<MeasureRelaRisk> riskRelaRiskList = riskrelameasure.list();
			for(MeasureRelaRisk riskRelaRisk : riskRelaRiskList){
				//删除控制对应的流程节点
				Criteria pointelameasurectr = o_processPointRelaMeasureDAO.createCriteria();
				pointelameasurectr.createAlias("controlMeasure", "controlMeasure");
				pointelameasurectr.add(Restrictions.eq("controlMeasure.id", riskRelaRisk.getControlMeasure().getId()));
				List<ProcessPointRelaMeasure> processpointrelameasureList = pointelameasurectr.list();
				for(ProcessPointRelaMeasure processpointrelameasure : processpointrelameasureList){
					o_processPointRelaMeasureDAO.delete(processpointrelameasure);
				}
				//删除控制和流程对应关系
				Criteria processRelaMeasureCtr = o_processRelaMeasureDAO.createCriteria();
				processRelaMeasureCtr.createAlias("controlMeasure", "controlMeasure");
				processRelaMeasureCtr.add(Restrictions.eq("controlMeasure.id",riskRelaRisk.getControlMeasure().getId()));
				List<ProcessRelaMeasure> processrelameasureList = processRelaMeasureCtr.list();
				for(ProcessRelaMeasure processrelameasure : processrelameasureList){
					o_processRelaMeasureDAO.delete(processrelameasure);
				}
				//删除控制和部门的关系
				Criteria measureRelaOrgCtr = o_measureRelaOrgDAO.createCriteria();
				measureRelaOrgCtr.createAlias("controlMeasure", "controlMeasure");
				measureRelaOrgCtr.add(Restrictions.eq("controlMeasure.id", riskRelaRisk.getControlMeasure().getId()));
				List<MeasureRelaOrg> measurerelaorgList = measureRelaOrgCtr.list();
				for(MeasureRelaOrg measurerelaorg : measurerelaorgList){
					o_measureRelaOrgDAO.delete(measurerelaorg);
				}
				o_measureRelaRiskDAO.delete(riskRelaRisk);
				//逻辑删除控制措施
				removeMeasureByID(riskRelaRisk.getControlMeasure().getId());
				//物理删除评价点内容
				Criteria criteriaAssess = o_assessPointDAO.createCriteria();
				criteriaAssess.add(Restrictions.eq("controlMeasure.id",riskRelaRisk.getControlMeasure().getId()));
				List<AssessPoint> assessPointList = criteriaAssess.list(); 
				for(AssessPoint assesspoint : assessPointList ){
					o_assessPointDAO.delete(assesspoint);
				}
			}
		}
		//保存控制措施信息
		if(msForm != null){   //如果没有控制节点就只保存风险
		for(int i = 0 ;i < msForm.size(); i++){
			JSONObject jsonObject = msForm.getJSONObject(i);
			measure = new Measure();	
			measure.setId(Identities.uuid());
			measure.setCode(jsonObject.getString("measurecode"));
			measure.setCompany(company);
			if(StringUtils.isNotBlank(jsonObject.getString("controlFrequency"))){
				measure.setControlFrequency(o_dictEntryDAO.get(jsonObject.getString("controlFrequency")));
			}
			if(StringUtils.isNotBlank(jsonObject.getString("controlMeasure"))){
				measure.setControlMeasure(o_dictEntryDAO.get(jsonObject.getString("controlMeasure")));
			}
			measure.setControlPoint(jsonObject.getString("controlPoint"));
			measure.setControlTarget(jsonObject.getString("controlTarget"));
			measure.setName(jsonObject.getString("meaSureDesc"));
			measure.setImplementProof(jsonObject.getString("implementProof"));
			measure.setIsKeyControlPoint(jsonObject.getString("isKeyPoint"));
			measure.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			o_measureDAO.merge(measure);
			//保存风险和控制关系信息
			measureRelaRisk = new MeasureRelaRisk();
			measureRelaRisk.setId(Identities.uuid());
			measureRelaRisk.setControlMeasure(measure);
			measureRelaRisk.setRisk(new Risk(riskForm.getId()));
			o_measureRelaRiskDAO.merge(measureRelaRisk);
			//保存控制和流程节点关系,控制节点可以对应多个流程节点
		    String pointNote = jsonObject.getString("pointNote");
			if(!StringUtils.isBlank(pointNote)){              //当流程节点不为空的时候执行
			    String[] pointArray = pointNote.split(",");
				for(int j = 0;j<pointArray.length; j++){
					processPointRelaMeasure = new ProcessPointRelaMeasure();
					processPointRelaMeasure.setId(Identities.uuid());
					processPointRelaMeasure.setProcessPoint(o_processpointDAO.get(pointArray[j].replace("[", "").replace("]", "").replace("\"", "")));
					processPointRelaMeasure.setProcess(o_processDAO.get(riskForm.getProcessId()));
					processPointRelaMeasure.setControlMeasure(measure);
					o_processPointRelaMeasureDAO.merge(processPointRelaMeasure);
				}
			}
			//保存控制和流程关系
			processRelaMeasure = new ProcessRelaMeasure();
			processRelaMeasure.setId(Identities.uuid());
			processRelaMeasure.setControlMeasure(measure);
			processRelaMeasure.setProcess(o_processDAO.get(riskForm.getProcessId()));
			o_processRelaMeasureDAO.merge(processRelaMeasure);
			//保存控制信息和部门关联关系
			MeasureRelaOrg measureRelaOrg = new MeasureRelaOrg();
			if(!StringUtils.isEmpty(jsonObject.getString("meaSureorgId"))){
				measureRelaOrg.setId(Identities.uuid());
				measureRelaOrg.setControlMeasure(measure);
				String orgid=IcmStandardUtils.findIdbyJason(jsonObject.getString("meaSureorgId"), "id");
				measureRelaOrg.setOrg(o_sysOrgDAO.get(orgid));
				measureRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
				o_measureRelaOrgDAO.merge(measureRelaOrg);
			}
			if(!StringUtils.isEmpty(jsonObject.getString("meaSureempId"))){
				measureRelaOrg = new MeasureRelaOrg();
				measureRelaOrg.setControlMeasure(measure);
				measureRelaOrg.setId(Identities.uuid());
				String empid=IcmStandardUtils.findIdbyJason(jsonObject.getString("meaSureempId"), "id");
				measureRelaOrg.setEmp(o_sysEmployeeDAO.get(empid));
				measureRelaOrg.setType(Contents.EMP_RESPONSIBILITY);
				o_measureRelaOrgDAO.merge(measureRelaOrg);
			}
			//保存控制评价点
			this.saveAssessPointEditGrid("",measure.getId(),jsonObject.getString("editGridJson"),Contents.ASSESS_POINT_TYPE_EXECUTE);
		}
	}
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
	public boolean validCodeRepeat(String id,String code,String companyId){
		boolean flag = true;
		if(id == null){  /*新增的判断 */
			Criteria criteria = o_riskDAO.createCriteria();
			criteria.add(Restrictions.eq("code", code));
			criteria.add(Restrictions.eq("company.id", companyId));
			Risk risk = (Risk)criteria.uniqueResult();
			if(risk != null){
				flag = false;
			}
		}else{    /*修改的情况 */
			Criteria criteria = o_riskDAO.createCriteria();
			criteria.add(Restrictions.eq("id", id));
			Risk risk = (Risk)criteria.uniqueResult();
			if(!code.equals(risk.getCode())){
				Criteria cri = o_riskDAO.createCriteria();
				cri.add(Restrictions.eq("code", code));
				cri.add(Restrictions.eq("company.id", companyId));
				Risk pro = (Risk)cri.uniqueResult();
				if(pro != null){
					flag = false;
				}
			}
		}
		return flag;
	}
	/**
	 * 判断是否有重复的编号
	 * add by songjia
	 * @param id
	 * @param code
	 * @param companyId
	 * @return
	 */
	public boolean validMeasureCodeRepeat(String code,String riskId){
		boolean flag = true;
		Criteria criteria = o_measureDAO.createCriteria();
		criteria.createAlias("measureRelaRisks", "measureRelaRisks");
		criteria.createAlias("measureRelaRisks.risk", "risk");
		criteria.add(Restrictions.eq("code", code));
		criteria.add(Restrictions.eq("risk.id", riskId));
		Measure measure = (Measure)criteria.uniqueResult();
		if(measure != null){
			flag = false;
		}
		return flag;
	}
	
	//保存评价点信息
	@Transactional
	public void saveAssessPointEditGrid(String processPointId,String measureId,String modifiedRecord,String type) {
		JSONArray jsonArray=JSONArray.fromObject(modifiedRecord);
		if(!jsonArray.isEmpty()){
			try {
				for(int i=0;i<jsonArray.size();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					String id = jsonObject.getString("id");
					String pointId = processPointId;
					String processId = jsonObject.getString("processId");
					String desc = jsonObject.getString("assessDesc");
					String comment = jsonObject.getString("comment");
					AssessPoint self = new AssessPoint();
					if("".equalsIgnoreCase(id)){
						id = Identities.uuid();
					}
					self.setId(id);
					self.setProcess(new Process(processId));
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
	 *加载流程表单，将数据库中信息写入表单
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public Map<String,Object> initMeasureFormData(String processId){
		Map<String,Object> node = new HashMap<String,Object>();
		Process process = o_processDAO.get(processId);
		Set<ProcessRelaOrg> processRelaOrgSet = process.getProcessRelaOrg();
		for(ProcessRelaOrg processRelaOrg : processRelaOrgSet){
			if(processRelaOrg.getType().equals(Contents.ORG_RESPONSIBILITY)){
				node.put("measureInitOrgId", processRelaOrg.getOrg().getId());
			}else if(processRelaOrg.getType().equals(Contents.EMP_RESPONSIBILITY)){
				node.put("measureInitEmpId", processRelaOrg.getEmp().getId());
			}
		}
		node.put("success", true);
		return node;
	}
	/**
	 * <pre>
	 *   加载控制措施form
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> loadMeasureEditFormData(String measureId){
		Criteria criteria = o_measureDAO.createCriteria();
		criteria.add(Restrictions.eq("id", measureId));
		Criteria orgctr = o_measureRelaOrgDAO.createCriteria();
		orgctr.createAlias("controlMeasure", "controlMeasure").add(Restrictions.eq("controlMeasure.id",measureId));
		//获取控制措施对应节点信息
		Criteria measureRelaPointCtr = o_processPointRelaMeasureDAO.createCriteria();
		measureRelaPointCtr.createAlias("controlMeasure", "controlMeasure");
		measureRelaPointCtr.add(Restrictions.eq("controlMeasure.id", measureId));
		Measure measure=(Measure) criteria.uniqueResult();
		Map<String, Object> formMap = new HashMap<String, Object>();
		formMap.put("measurecode", measure.getCode());
		if(null != measure.getControlFrequency()){
			formMap.put("controlFrequency", measure.getControlFrequency().getId());
		}
		formMap.put("isKeyPoint", measure.getIsKeyControlPoint());
		formMap.put("implementProof", measure.getImplementProof());
		formMap.put("controlPoint", measure.getControlPoint());
		formMap.put("meaSureDesc", measure.getName());
		if(null != measure.getControlMeasure()){
			formMap.put("controlMeasure", measure.getControlMeasure().getId());
		}
		if(null != measure.getControlTarget()){
			formMap.put("controlTarget", measure.getControlTarget());
		}
		
		//获取责任和部门
		List<MeasureRelaOrg> orglist=orgctr.list();
		JSONArray deptArr = new JSONArray();
		JSONArray empArr = new JSONArray();
		for(MeasureRelaOrg measurerelaorg : orglist){
			 if(Contents.ORG_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				 JSONObject obj = new JSONObject();
				 SysOrganization org = measurerelaorg.getOrg();
				 obj.put("id", org.getId());
				 obj.put("deptno", org.getOrgcode());
				 obj.put("deptname", org.getOrgname());
				 deptArr.add(obj);
			 }else if(Contents.EMP_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				 JSONObject obj = new JSONObject();
				 SysEmployee emp = measurerelaorg.getEmp();
				 obj.put("id", emp.getId());
				 obj.put("empno", emp.getEmpcode());
				 obj.put("empname", emp.getEmpname());
				 empArr.add(obj);
			 }
		}
		formMap.put("meaSureorgId",deptArr.toString());
		formMap.put("meaSureempId",empArr.toString());
		
		List<ProcessPointRelaMeasure> measureRelaPointList = measureRelaPointCtr.list();
		String[] array = new String[measureRelaPointList.size()];
		int i = 0;
		for(ProcessPointRelaMeasure pointrelameasure : measureRelaPointList){
			array[i] = pointrelameasure.getProcessPoint().getId();
			i++ ;
		}
		formMap.put("pointNote",array);
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", formMap);
		node.put("success", true);
		return node;
	}
	
	/**
	 * <pre>
	 *   加载控制措施formview
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,Object> loadMeasureEditFormDataForView(String measureId){
		Criteria criteria = o_measureDAO.createCriteria();
		criteria.add(Restrictions.eq("id", measureId));
		Criteria orgctr = o_measureRelaOrgDAO.createCriteria();
		orgctr.createAlias("controlMeasure", "controlMeasure").add(Restrictions.eq("controlMeasure.id",measureId));
		//获取控制措施对应节点信息
		Criteria measureRelaPointCtr = o_processPointRelaMeasureDAO.createCriteria();
		measureRelaPointCtr.createAlias("controlMeasure", "controlMeasure");
		measureRelaPointCtr.add(Restrictions.eq("controlMeasure.id", measureId));
		measureRelaPointCtr.add(Restrictions.eq("controlMeasure.deleteStatus", "1"));
		Measure measure=(Measure) criteria.uniqueResult();
		Map<String, Object> formMap = new HashMap<String, Object>();
		formMap.put("measurecode", measure.getCode());
		if(measure.getControlFrequency()!=null){
			formMap.put("controlFrequency", measure.getControlFrequency().getName());
		}
		if(StringUtils.isNotBlank(measure.getIsKeyControlPoint())){
			formMap.put("isKeyPoint", o_dictEntryDAO.get(measure.getIsKeyControlPoint()).getName());
		}
		if(measure.getImplementProof() != null){
			formMap.put("implementProof", measure.getImplementProof());
		}
		formMap.put("controlPoint", measure.getControlPoint());
		formMap.put("meaSureDesc", measure.getName());
		if(measure.getControlMeasure() != null){
			formMap.put("controlMeasure", measure.getControlMeasure().getName());
		}
		formMap.put("controlTarget", measure.getControlTarget());
		//获取责任和部门
		List<MeasureRelaOrg> orglist=orgctr.list();
		for(MeasureRelaOrg measurerelaorg : orglist){
			if(Contents.ORG_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				formMap.put("orgName",measurerelaorg.getOrg().getOrgname());
			}else if(Contents.EMP_RESPONSIBILITY.equalsIgnoreCase(measurerelaorg.getType())){
				formMap.put("empName",measurerelaorg.getEmp().getEmpname());
			}
		}
		List<ProcessPointRelaMeasure> measureRelaPointList = measureRelaPointCtr.list();
		StringBuffer sb = new StringBuffer();
		for(ProcessPointRelaMeasure pointrelameasure : measureRelaPointList){
			sb.append(pointrelameasure.getProcessPoint().getName());
		}
		formMap.put("pointNote",sb.toString());
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
	public Map<String,Object> findMeasureIdbyRiskId(String processId,String riskId){
		List<ProcessRelaMeasure> processRelaMeasureList = o_processRelaMeasureBO.findProcessRelaMeasureListBySome(null, processId, null);
		Criteria criteria = o_measureRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "risk");
		criteria.createAlias("controlMeasure", "controlMeasure");
		criteria.add(Restrictions.eq("risk.id", riskId));
		criteria.add(Restrictions.eq("controlMeasure.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<MeasureRelaRisk> measureRelaRiskList = criteria.list();
		List<String> measureIdList = new ArrayList<String>();
		String measureId;
		for(MeasureRelaRisk measureRelaRisk : measureRelaRiskList){
			for (ProcessRelaMeasure processRelaMeasure : processRelaMeasureList) {
				measureId = measureRelaRisk.getControlMeasure().getId();
				if(StringUtils.isNotBlank(measureId) && measureId.equals(processRelaMeasure.getControlMeasure().getId())){
					measureIdList.add(measureRelaRisk.getControlMeasure().getId());
				}
			}
		}
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", measureIdList);
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
	public Map<String,Object> findProcessPointCode(String processEditID, String parentId){
		Map<String,Object> processMap =new HashMap<String,Object>();
		processMap.put("code",Identities.uuid());
		Map<String, Object> node=new HashMap<String, Object>();
		node.put("data", processMap);
		node.put("success", true);
		return node;
	}
	/**
	 * 根据流程编号查找流程下所有的风险
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findProcessRiskPageBySome(Page<ProcessRelaRisk> page, String query,String processId,String companyId) {
		DetachedCriteria ctrProcessRelaRisk = DetachedCriteria.forClass(ProcessRelaRisk.class);   //风险流程关联表 
		ctrProcessRelaRisk.createAlias("risk", "risk", CriteriaSpecification.LEFT_JOIN);   //关联风险实体
		ctrProcessRelaRisk.createAlias("process", "process",CriteriaSpecification.LEFT_JOIN).add(Restrictions.eq("process.id",processId));     //关联流程实体
		if(StringUtils.isNotBlank(query)){
			ctrProcessRelaRisk.add(Restrictions.or(Property.forName("risk.code").like(query, MatchMode.ANYWHERE), Property.forName("risk.name").like(query, MatchMode.ANYWHERE)));
		}
		ctrProcessRelaRisk.add(Restrictions.eq("risk.deleteStatus",Contents.DELETE_STATUS_USEFUL));
		if (!StringUtils.isNotEmpty(processId)) {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		List<ProcessRelaRisk> ctrProcessRelaRiskList =o_processRelaRiskDAO.findPage(ctrProcessRelaRisk,page,false).getResult();
		//得到某个流程下的风险的控制措施数量
		Map<String,Object> riskMeasureNumMap = this.findriskMeasureNumMapByProcessId(processId);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (ProcessRelaRisk processrelarisk : ctrProcessRelaRiskList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", processrelarisk.getId());
			map.put("riskId", processrelarisk.getRisk().getId());
			map.put("code", processrelarisk.getRisk().getCode());
			map.put("name", processrelarisk.getRisk().getName());
			map.put("desc", processrelarisk.getRisk().getDesc());
			map.put("type", processrelarisk.getType());
			//流程下风险关联的控制措施的数量
			map.put("measureNum", riskMeasureNumMap.get(processrelarisk.getRisk().getId()));
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", page.getTotalItems());

		return result;
	}
	/**
	 * 根据流程编号查找流程下所有的风险
	 * @autor 宋佳
	 * @param processId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public List<Risk> findRiskListByProcessId(String processId) {
		Criteria criteria = o_processRelaRiskDAO.createCriteria();
		criteria.createAlias("risk", "risk", CriteriaSpecification.LEFT_JOIN);   //关联风险实体
		criteria.createAlias("process", "process",CriteriaSpecification.LEFT_JOIN).add(Restrictions.eq("process.id",processId));     //关联流程实体
		criteria.add(Restrictions.eq("risk.deleteStatus",Contents.DELETE_STATUS_USEFUL));
		return criteria.list();
	}
	public List<DictEntryForm> findAllProcessPointByProcessId(String processId){
		Criteria criteria = o_processpointDAO.createCriteria().setCacheable(true);
		criteria.createAlias("process", "o").add(Restrictions.eq("o.id",processId));
		List<ProcessPoint> list= criteria.list();
		List<DictEntryForm> result=new ArrayList<DictEntryForm>();
		for(ProcessPoint entry:list)
		{
		    DictEntryForm form=new DictEntryForm();
		    form.setId(entry.getId());
		    form.setName(entry.getName());
		    result.add(form);
		}
		return result;
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
	public void removeProcessRiskById(String riskId){
		// 删除风险对应的控制措施
		Criteria riskrelameasure = o_measureRelaRiskDAO.createCriteria();
		riskrelameasure.createAlias("risk", "risk");
		riskrelameasure.add(Restrictions.in("risk.id", riskId.split(",")));
		List<MeasureRelaRisk> riskRelaRiskList = riskrelameasure.list();
		for(MeasureRelaRisk riskRelaRisk : riskRelaRiskList){
			//删除控制对应的流程节点
			Criteria pointelameasurectr = o_processPointRelaMeasureDAO.createCriteria();
			pointelameasurectr.createAlias("controlMeasure", "controlMeasure");
			pointelameasurectr.add(Restrictions.eq("controlMeasure.id", riskRelaRisk.getControlMeasure().getId()));
			List<ProcessPointRelaMeasure> processpointrelameasureList = pointelameasurectr.list();
			for(ProcessPointRelaMeasure processpointrelameasure : processpointrelameasureList){
				o_processPointRelaMeasureDAO.delete(processpointrelameasure);
			}
			//删除控制和流程对应关系
			Criteria processRelaMeasureCtr = o_processRelaMeasureDAO.createCriteria();
			processRelaMeasureCtr.createAlias("controlMeasure", "controlMeasure");
			processRelaMeasureCtr.add(Restrictions.eq("controlMeasure.id",riskRelaRisk.getControlMeasure().getId()));
			List<ProcessRelaMeasure> processrelameasureList = processRelaMeasureCtr.list();
			for(ProcessRelaMeasure processrelameasure : processrelameasureList){
				o_processRelaMeasureDAO.delete(processrelameasure);
			}
			//删除控制和部门的关系
			//删除控制和流程对应关系
			Criteria measureRelaOrgCtr = o_measureRelaOrgDAO.createCriteria();
			measureRelaOrgCtr.createAlias("controlMeasure", "controlMeasure");
			measureRelaOrgCtr.add(Restrictions.eq("controlMeasure.id", riskRelaRisk.getControlMeasure().getId()));
			List<MeasureRelaOrg> measurerelaorgList = measureRelaOrgCtr.list();
			for(MeasureRelaOrg measurerelaorg : measurerelaorgList){
				o_measureRelaOrgDAO.delete(measurerelaorg);
			}
			o_measureRelaRiskDAO.delete(riskRelaRisk);
			removeMeasureByID(riskRelaRisk.getControlMeasure().getId());
			
			//删除控制评价点信息
			Criteria criteriaAssess = o_assessPointDAO.createCriteria();
			criteriaAssess.add(Restrictions.eq("controlMeasure.id",riskRelaRisk.getControlMeasure().getId()));
			List<AssessPoint> assessPointList = criteriaAssess.list(); 
			for(AssessPoint assesspoint : assessPointList ){
				o_assessPointDAO.delete(assesspoint);
			}
		}
		removeRiskByIds(riskId);
	}
	/**
	 * <pre>
	 *		删除流程风险关联关系
	 * </pre>
	 * 
	 * @author 宋佳
	 * @param processID
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeProcessRelaRiskById(String riskId){
		for(String id : riskId.split(",")){
			ProcessRelaRisk processrelarisk = o_processRelaRiskDAO.get(riskId);
			Criteria criteria = o_processPointRelaRiskDAO.createCriteria();
			criteria.add(Restrictions.eq("process.id", processrelarisk.getProcess().getId()));
			criteria.add(Restrictions.eq("risk.id", processrelarisk.getRisk().getId()));
			List<ProcessPointRelaRisk> processPointRelaRiskList = criteria.list();
			for(ProcessPointRelaRisk processPointRelaRisk : processPointRelaRiskList){
				o_processPointRelaRiskDAO.delete(processPointRelaRisk);
			}
			o_processRelaRiskBO.removeProcessRelaRiskById(id);
		}
	}
	@Transactional
	public void removeMeasureByIds(String ids){
		// 删除风险对应的控制措施
		Criteria riskrelameasure = o_measureRelaRiskDAO.createCriteria();
		riskrelameasure.createAlias("controlMeasure", "controlMeasure");
		riskrelameasure.add(Restrictions.in("controlMeasure.id", ids.split(",")));
		List<MeasureRelaRisk> riskRelaRiskList = riskrelameasure.list();
		for(MeasureRelaRisk riskRelaRisk : riskRelaRiskList){
			//删除控制对应的流程节点
			Criteria pointelameasurectr = o_processPointRelaMeasureDAO.createCriteria();
			pointelameasurectr.createAlias("controlMeasure", "controlMeasure");
			pointelameasurectr.add(Restrictions.eq("controlMeasure.id", riskRelaRisk.getControlMeasure().getId()));
			List<ProcessPointRelaMeasure> processpointrelameasureList = pointelameasurectr.list();
			for(ProcessPointRelaMeasure processpointrelameasure : processpointrelameasureList){
				o_processPointRelaMeasureDAO.delete(processpointrelameasure);
			}
			//删除控制和流程对应关系
			Criteria processRelaMeasureCtr = o_processRelaMeasureDAO.createCriteria();
			processRelaMeasureCtr.createAlias("controlMeasure", "controlMeasure");
			processRelaMeasureCtr.add(Restrictions.eq("controlMeasure.id",riskRelaRisk.getControlMeasure().getId()));
			List<ProcessRelaMeasure> processrelameasureList = processRelaMeasureCtr.list();
			for(ProcessRelaMeasure processrelameasure : processrelameasureList){
				o_processRelaMeasureDAO.delete(processrelameasure);
			}
			//删除控制和部门的关系
			//删除控制和流程对应关系
			Criteria measureRelaOrgCtr = o_measureRelaOrgDAO.createCriteria();
			measureRelaOrgCtr.createAlias("controlMeasure", "controlMeasure");
			measureRelaOrgCtr.add(Restrictions.eq("controlMeasure.id", riskRelaRisk.getControlMeasure().getId()));
			List<MeasureRelaOrg> measurerelaorgList = measureRelaOrgCtr.list();
			for(MeasureRelaOrg measurerelaorg : measurerelaorgList){
				o_measureRelaOrgDAO.delete(measurerelaorg);
			}
			removeMeasureByID(riskRelaRisk.getControlMeasure().getId());
			
			//删除控制评价点信息
			Criteria criteriaAssess = o_assessPointDAO.createCriteria();
			criteriaAssess.add(Restrictions.eq("controlMeasure.id",riskRelaRisk.getControlMeasure().getId()));
			List<AssessPoint> assessPointList = criteriaAssess.list(); 
			for(AssessPoint assesspoint : assessPointList ){
				o_assessPointDAO.delete(assesspoint);
			}
		}
	
	}

	/**
	 * <pre>
	 * 删除控制措施
	 * </pre>
	 * @author 李克东
	 * @param processID
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void removeMeasureByID(String measureId){
		Measure measure = o_measureDAO.get(measureId);
		measure.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
		o_measureDAO.merge(measure);
	}
	/**
	 * <pre>
	 * 删除风险
	 * </pre>
	 * @author 李克东
	 * @param processID
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeRiskByIds(String riskIds){
		for(String riskId : riskIds.split(",")){
			Risk risk = o_riskDAO.get(riskId);
			risk.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
			o_riskDAO.merge(risk);
		}
	}
	/**
	 * 给风险导入程序用
	 * add by 宋佳
	 * 2013-12-12  下午1:47:20
	 * desc : 
	 * @param oldRiskId
	 * @param riskId
	 * @param companyId
	 * @return 
	 * boolean
	 */
	@Transactional
	public boolean saveMeasureByRiskId(Map<String,Risk> oldRiskMap,String companyId){
		for(Map.Entry<String, Risk> entry : oldRiskMap.entrySet()){
			String oldRiskId = entry.getKey();
			Risk risk  = entry.getValue();
			Measure measure = null;
			Measure oldMeasure = null;
			Criteria c = o_measureRelaRiskDAO.createCriteria();
			c.createAlias("risk", "risk");
			c.createAlias("controlMeasure", "controlMeasure");
			c.add(Restrictions.eq("risk.id", oldRiskId));
			c.add(Restrictions.eq("controlMeasure.deleteStatus", Contents.DELETE_STATUS_USEFUL));
			List<MeasureRelaRisk> measureRelaRiskList = c.list();
			for(MeasureRelaRisk measureRelaRisk : measureRelaRiskList){
				oldMeasure = measureRelaRisk.getControlMeasure();
				measure = new Measure();
				measure.setId(Identities.uuid());
				measure.setCompany(new SysOrganization(companyId));
				measure.setCode(oldMeasure.getCode());
				measure.setControlFrequency(oldMeasure.getControlFrequency());
				measure.setControlMeasure(oldMeasure.getControlMeasure());
				measure.setControlPoint(oldMeasure.getControlPoint());
				measure.setControlTarget(oldMeasure.getControlTarget());
				measure.setCreateBy(oldMeasure.getCreateBy());
				measure.setCreateTime(oldMeasure.getCreateTime());
				measure.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
				measure.setDesc(oldMeasure.getDesc());
				measure.setImplementProof(oldMeasure.getImplementProof());
				measure.setIsKeyControlPoint(oldMeasure.getIsKeyControlPoint());
				measure.setLastModifyBy(oldMeasure.getLastModifyBy());
				measure.setName(oldMeasure.getName());
				measure.setSort(oldMeasure.getSort());
				o_measureDAO.merge(measure);
				//保存完新的控制措施保存风险控制措施关联关系
				saveMeasureRelaRiskByNewId(risk.getId(),measure.getId());
			}
		}
		return true;
	}
	/**
	 * 保存控制措施和风险的关联关系
	 * add by songjia 
	 * 
	 */
	@Transactional
	private boolean saveMeasureRelaRiskByNewId(String riskId,String measureId){
		MeasureRelaRisk measureRelaRisk = new MeasureRelaRisk();
		measureRelaRisk.setId(Identities.uuid());
		measureRelaRisk.setRisk(new Risk(riskId));
		measureRelaRisk.setControlMeasure(new Measure(measureId));
		o_measureRelaRiskDAO.merge(measureRelaRisk);
		return true;
	}
	
	/**
	 * 得到某个流程下的风险的控制措施数量
	 * @author zhengjunxiang
	 * @return
	 */
	private Map<String,Object> findriskMeasureNumMapByProcessId(String processId){
		StringBuffer sql = new StringBuffer();
		sql.append("select new map(prt.risk.id as riskId,count(rmt.id) as num) ");
		sql.append("from ProcessRelaRisk prt ");
		sql.append("left join prt.risk risk ");
		sql.append("left join risk.measureRelaRisk rmt ");
		sql.append("where prt.process.id=:processId and risk.deleteStatus=").append(Contents.DELETE_STATUS_USEFUL);
		sql.append("group by risk.id");

		Query query = o_measureRelaRiskDAO.getSession().createQuery(sql.toString());
		query.setParameter("processId", processId);
		List<Map<String,Object>> list = query.list();
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		for(Map<String,Object> m : list){
			resultMap.put(m.get("riskId").toString(), m.get("num"));
		}
		return resultMap;
	}
}

