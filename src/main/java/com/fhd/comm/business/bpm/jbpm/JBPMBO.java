package com.fhd.comm.business.bpm.jbpm;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.jbpm.api.Execution;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.history.HistoryProcessInstance;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.history.model.HistoryProcessInstanceImpl;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.model.TransitionImpl;
import org.jbpm.pvm.internal.repository.DeploymentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.bpm.BusinessWorkFlowDAO;
import com.fhd.dao.bpm.DeploymentImplDAO;
import com.fhd.dao.bpm.ExamineApproveIdeaDAO;
import com.fhd.dao.bpm.JbpmExecutionDAO;
import com.fhd.dao.bpm.JbpmHistActinstDAO;
import com.fhd.dao.bpm.ProcessDefinitionDeployDAO;
import com.fhd.dao.bpm.VJbpm4TaskDAO;
import com.fhd.dao.bpm.VJbpmDeploymentDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.bpm.BusinessWorkFlow;
import com.fhd.entity.bpm.ExamineApproveIdea;
import com.fhd.entity.bpm.JbpmExecution;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.ProcessDefinitionDeploy;
import com.fhd.entity.bpm.view.VJbpm4Task;
import com.fhd.entity.bpm.view.VJbpmDeployment;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.file.FileUploadBO;
/**
 * 
 * ClassName:JBPMBO
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-1-4		下午4:23:47
 *
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class JBPMBO {
	@Autowired
	protected ProcessEngine o_processEngine;
	//jbpm操作对象
	@Autowired
	private JBPMOperate o_jBPMOperate;
	@Autowired
	private JbpmHistProcinstBO o_jbpmHistProcinstBO;
	//审批意见Dao
	@Autowired
	private ExamineApproveIdeaDAO o_examineApproveIdeaDAO;
	//员工Dao
	@Autowired
	private EmployeeDAO o_sysEmployeeDAO;
	//工作流业务关系表DAO
	@Autowired
	private BusinessWorkFlowDAO o_businessWorkFlowDAO;
	@Autowired
	private VJbpmDeploymentDAO o_vJbpmDeploymentDAO;
	//流程定义部署DAO
	@Autowired
	private ProcessDefinitionDeployDAO o_processDefinitionDeployDAO;
	@Autowired
	private JbpmHistActinstDAO o_jbpmHistActinstDAO;
	@Autowired
	private JbpmExecutionDAO o_jbpmExecutionDAO;
	@Autowired
	private VJbpm4TaskDAO o_vJbpm4TaskDAO;
	@Autowired
	private DeploymentImplDAO o_deploymentImplDAO;
	@Autowired
	private FileUploadBO o_fileUploadBO;
	@Autowired
	private HistoryProcessInstanceBO o_historyProcessInstanceBO;
	@Autowired
	private JbpmHistActinstBO o_jbpmHistActinstBO;
	/**
	 * startProcessInstance:开启流程
	 * @author 杨鹏
	 * @param entityType 流程名称
	 * @param variables 流程变量
	 */
	@Transactional
	public String startProcessInstance(String entityType,Map<String,Object> variables) {
		// 开启工作流流程并得到流程实例id
		variables.put("weigh", 1);
		ProcessInstance processInstance = o_jBPMOperate.startPocessInstance(entityType, variables);
		String processInstanceId = processInstance.getId();
		/**
		 * 保存业务关联表
		 */
		String id = Identities.uuid();
		BusinessWorkFlow businessWorkFlow = new BusinessWorkFlow();
		businessWorkFlow.setId(id);
		businessWorkFlow.setBusinessId(String.valueOf(variables.get("id")));
		businessWorkFlow.setBusinessName(String.valueOf(variables.get("name")));
		businessWorkFlow.setProcessInstanceId(processInstanceId);
		businessWorkFlow.setBusinessObjectType(entityType);
		businessWorkFlow.setState("1");// 开始正常状态
		businessWorkFlow.setXbState("1");// 协办正常状态
		if(null != UserContext.getUser()) {
			variables.put("startUserId", UserContext.getUser().getEmpid());
			businessWorkFlow.setCreateBy(UserContext.getUser().getEmpid());
		} else {
			variables.put("startUserId", Contents.SYSTEM);
			businessWorkFlow.setCreateBy(Contents.SYSTEM);
		}
		
		businessWorkFlow.setCreateTime(new Date());
		
		List<JbpmHistProcinst> jbpmHistProcinsts = o_jbpmHistProcinstBO.findJbpmHistProcinstBySome(processInstanceId);
		JbpmHistProcinst jbpmHistProcinst = jbpmHistProcinsts.get(0);
		jbpmHistProcinst.setKey("."+jbpmHistProcinst.getId()+".");
		o_jbpmHistProcinstBO.merge(jbpmHistProcinst);
		String url = jbpmHistProcinst.getvJbpmDeployment().getProcessDefinitionDeploy().getUrl();
		businessWorkFlow.setUrl(url);
		/**
		 * 计算第一个节点的流程百分比
		 */
		String taskRateStr="0";
		Integer taskRate=0;
		Double rateWeigh=1d;
		Map<String, String> rateMap = this.getRateByExecutionId(processInstanceId, 1);
		if(rateMap!=null){
			taskRateStr = rateMap.get("taskRate");
			if(StringUtils.isNotBlank(taskRateStr)){
				taskRate=Integer.valueOf(taskRateStr);
			}
		}
		
		businessWorkFlow.setRate(taskRateStr);
		businessWorkFlow.setRateSum(taskRate*rateWeigh);
		businessWorkFlow.setRateWeigh(rateWeigh);
		o_businessWorkFlowDAO.merge(businessWorkFlow);
		return processInstanceId;  
	}
	
	/**
	 * 
	 * doProcessInstance:执行流程
	 * 
	 * @author 杨鹏
	 * @param processInstanceId 流程实例ID
	 * @param variables 流程变量
	 */
	@Transactional
	public ProcessInstance doProcessInstance(String executionId,Map<String,Object> variables) {
		/**
		 * 获取执行
		 */
		Execution execution = o_jBPMOperate.getExecutionService().findExecutionById(executionId);
		/**
		 * 获取流程实例ID
		 */
		String processInstanceId = execution.getProcessInstance().getId();
		/**
		 * 获取TASK
		 */
		List<Task> list = o_jBPMOperate.getTaskService().createTaskQuery().executionId(executionId).list();
		Task task = list.get(0);
		/**
		 * 获取businessWorkFlow
		 */
		BusinessWorkFlow businessWorkFlow=null;
		String businessId=null;
		String mainExecutionId = getExecutionIdBySubExecutionId(executionId);
		List<BusinessWorkFlow> businessWorkFlows = this.findBusinessWorkFlowBySome(null, mainExecutionId);
		if(businessWorkFlows!=null&&businessWorkFlows.size()>0){
			businessWorkFlow = businessWorkFlows.get(0);
			if(businessWorkFlow!=null){
				businessId=businessWorkFlow.getBusinessId();
			}
		}
		/**
		 * 保存意见
		 */
		String showIdea = String.valueOf(variables.get("showIdea"));
		if("true".equals(showIdea)||null!=variables.get("examineApproveIdea")){
			String path = String.valueOf(variables.get("path"));
			String examineApproveIdea = String.valueOf(variables.get("examineApproveIdea"));
			String taskId = task.getId();
			this.createExamineApproveIdea(processInstanceId,businessId,path,examineApproveIdea,UserContext.getUser().getEmpid(),Long.valueOf(taskId));
		}
		/**
		 * 保存流程变量
		 */
		o_jBPMOperate.saveVariables(executionId, variables);
		/**
		 * 获得执行权重
		 */
		String executionWeighStr = o_jBPMOperate.getVariable(executionId, "weigh");
		Integer executionWeigh=1;
		if(StringUtils.isNotBlank(executionWeighStr)){
			executionWeigh=Integer.valueOf(executionWeighStr);
		}
		/**
		 * 获得旧节点完成比例
		 */
		Map<String, String> oldRateMap=new HashMap<String, String>();
		oldRateMap.put("taskRate", "0");
		oldRateMap.put("finishRate", "0");
		Map<String, String> oldDescriptionMap = this.getDescriptionByTask(task);
		if(oldDescriptionMap!=null){
			String oldRateMapStr = oldDescriptionMap.get("rate");
			oldRateMap = this.getRateMapByRateStr(oldRateMapStr);
		}
		/**
		 * 执行流程
		 */
		ProcessInstance pocessInstance = o_jBPMOperate.singelPocessInstance(executionId, variables);
		/**
		 * 处理旧的百分比
		 */
		String oldTaskRateStr = oldRateMap.get("taskRate");
		Integer oldTaskRate=Integer.valueOf(oldTaskRateStr);
		String oldFinishRateStr = oldRateMap.get("finishRate");
		Integer oldFinishRate=100;
		if(StringUtils.isNotBlank(oldFinishRateStr)){
			oldFinishRate=Integer.valueOf(oldFinishRateStr);
		}
		/**
		 * 获取新节点执行比例
		 */
		Integer taskRate=oldFinishRate;
		Map<String, String> rateMap = this.getRateByExecutionId(executionId, executionWeigh);
		if(rateMap!=null){
			String taskRateStr = rateMap.get("taskRate");
			if(StringUtils.isNotBlank(taskRateStr)){
				taskRate=Integer.valueOf(taskRateStr);
			}
		}
		/**
		 * 计算百分比:流程百分比=(旧流程百分比*执行权重-旧执行百分比*执行权重+新执行百分比*执行权重)/执行权重
		 */
		Double rateSum = businessWorkFlow.getRateSum();
		Double rateWeigh = businessWorkFlow.getRateWeigh();
		rateSum=rateSum*executionWeigh-oldTaskRate*rateWeigh+taskRate*rateWeigh;
		rateWeigh*=executionWeigh;
		Double rate=rateSum/rateWeigh;
		Integer rateInt = rate.intValue();
		/**
		 * 保存百分比
		 */
		businessWorkFlow.setRate(rateInt.toString());
		businessWorkFlow.setRateSum(rateSum);
		businessWorkFlow.setRateWeigh(rateWeigh);
		o_businessWorkFlowDAO.merge(businessWorkFlow);
		/**
		 * 判断是否产生子流程，如产生则赋予子关系
		 */
		o_jbpmHistProcinstBO.editSubJbpmHistProcinst(executionId);
		return pocessInstance;
	}

	/**
	 * 
	 * getExecutionIdBySubExecutionId:获得顶级流程分支执行ID
	 * 
	 * @author 杨鹏
	 * @param subExecutionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public String getExecutionIdBySubExecutionId(String subExecutionId){
		Criteria createCriteria = o_jbpmExecutionDAO.createCriteria();
		createCriteria.add(Restrictions.eq("id_", subExecutionId));
		JbpmExecution jbpmExecution = (JbpmExecution)createCriteria.uniqueResult();
		JbpmHistProcinst jbpmHistProcinst = new JbpmHistProcinst();
		while(jbpmExecution!=null){
			jbpmHistProcinst=jbpmExecution.getJbpmHistProcinst();
			Long jbpmHistProcinstId = jbpmHistProcinst.getId();
			createCriteria = o_jbpmExecutionDAO.createCriteria();
			createCriteria.add(Restrictions.eq("subprocinst", jbpmHistProcinstId));
			jbpmExecution = (JbpmExecution)createCriteria.uniqueResult();
		}
		return jbpmHistProcinst.getId_();
	}
	/**
	 * 
	 * getDescriptionByTask:获取task描述
	 * 
	 * @author 杨鹏
	 * @param task
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,String> getDescriptionByTask(Task task){
		Map<String,String> descriptionMap = new HashMap<String, String>();
		String description = task.getDescription();
		if(StringUtils.isNotBlank(description)){
			/**
			 * 因以前版本中有非json描述的所以如此判断。待全部改为json后可删除else部分
			 */
			if("{".equals(description.substring(0, 1))){
				JSONObject fromObject = JSONObject.fromObject(description);
				Set<String> keySet = fromObject.keySet();
				for (String key : keySet) {
					String string = String.valueOf(fromObject.get(key));
					descriptionMap.put(key, string);
				}
			}else{
				descriptionMap.put("rate", description);
			}
		}
		return descriptionMap;
	}
	
	/**
	 * 
	 * getDescriptionByExecutionId:获取task描述
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,String> getDescriptionByExecutionId(String executionId){
		Map<String,String> descriptionMap = new HashMap<String, String>();
		List<Task> list = o_jBPMOperate.getTaskService().createTaskQuery().executionId(executionId).list();
		if(list.size()>0){
			Task task = list.get(0);
			descriptionMap = this.getDescriptionByTask(task);
		}
		return descriptionMap;
	}
	/**
	 * 
	 * getRateMapByRateStr:
	 * 
	 * @author 杨鹏
	 * @param rateMapStr
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,String> getRateMapByRateStr(String rateMapStr){
		HashMap<String, String> rateMap = new HashMap<String, String>();
		if(StringUtils.isNotBlank(rateMapStr)){
			/**
			 * 因以前版本中有非json描述的所以如此判断。待全部改为json后可删除else部分
			 */
			if("{".equals(rateMapStr.substring(0, 1))){
				JSONObject fromObject = JSONObject.fromObject(rateMapStr);
				Set<String> keySet = fromObject.keySet();
				for (String key : keySet) {
					String string = String.valueOf(fromObject.get(key));
					rateMap.put(key, string);
				}
			}else{
				String[] rates = StringUtils.split(rateMapStr,",");
				if(rates.length>0){
					rateMap.put("taskRate", rates[0]);
				}
				if(rates.length>1){
					rateMap.put("finishRate", rates[1]);
				}
			}
		}
		return rateMap;
	}
	/**
	 * 
	 * getRateByExecutionId:获取指定执行下的执行百分比并赋予执行权重
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @param weigh
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,String> getRateByExecutionId(String executionId,Integer weigh){
		Map<String,String> rateMap=new HashMap<String, String>();
		Map<String, Integer> variables=new HashMap<String, Integer>();
		Execution execution = o_jBPMOperate.getExecutionService().findExecutionById(executionId);
		if(execution==null){
			rateMap=null;
		}else{
			variables.put("weigh", weigh);
			o_jBPMOperate.saveVariables(executionId, variables);
			Collection<? extends Execution> executions = execution.getExecutions();
			Execution subProcessInstance = execution.getSubProcessInstance();
			if(subProcessInstance!=null){
				rateMap=this.getRateByExecutionId(subProcessInstance.getId(),weigh);
			}else if(executions!=null&&executions.size()>0){
				Integer sum=0;
				Integer finishRate=0;
				Integer parentFinishRate=0;
				Integer finishCount=0;
				for (Execution executionTemp : executions) {
					Integer weighTemp=weigh*executions.size();
					Map<String, String> rateMapTemp = this.getRateByExecutionId(executionTemp.getId(),weighTemp);
					if(rateMapTemp!=null){
						if(rateMapTemp.keySet().size()!=0){
							String taskRateStr = rateMapTemp.get("taskRate");
							String finishRateStr = rateMapTemp.get("finishRate");
							String parentFinishRateStr = rateMapTemp.get("parentFinishRate");
							if(StringUtils.isNotBlank(parentFinishRateStr)){
								parentFinishRate=Integer.valueOf(parentFinishRateStr);
							}
							if(StringUtils.isNotBlank(finishRateStr)){
								finishRate=Integer.valueOf(finishRateStr);
							}
							if(StringUtils.isNotBlank(taskRateStr)){
								sum+=Integer.valueOf(taskRateStr);
							}
						}else{
							finishCount++;
						}
					}
				}
				sum+=finishCount*finishRate;
				sum/=executions.size();
				rateMap.put("taskRate", String.valueOf(sum));
				rateMap.put("finishRate", String.valueOf(parentFinishRate));
			}else{
				Map<String, String> description = this.getDescriptionByExecutionId(executionId);
				if(description!=null){
					String rateStr = description.get("rate");
					if(StringUtils.isNotBlank(rateStr)){
						/**
						 * 因以前版本中有非json描述的所以如此判断。待全部改为json后可删除else部分
						 */
						if("{".equals(rateStr.substring(0, 1))){
							JSONObject fromObject = JSONObject.fromObject(rateStr);
							Set<String> keySet = fromObject.keySet();
							for (String key : keySet) {
								String string = String.valueOf(fromObject.get(key));
								rateMap.put(key, string);
							}
						}else{
							String[] rates = StringUtils.split(rateStr,",");
							if(rates.length>0){
								rateMap.put("taskRate", rates[0]);
							}
							if(rates.length>1){
								rateMap.put("finishRate", rates[1]);
							}
						}
					}
				}
			}
		}
		return rateMap;
	}
	
	/**
	 * 
	 * getRateByExecutionId:递归计算Execution完成比率
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Map<String,String> getRateByExecutionId(String executionId){
		Map<String,String> rateMap=new HashMap<String, String>();
		Execution execution = o_jBPMOperate.getExecutionService().findExecutionById(executionId);
		if(execution==null){
			rateMap.put("taskRate", "100");
		}else{
			Collection<? extends Execution> executions = execution.getExecutions();
			Execution subProcessInstance = execution.getSubProcessInstance();
			if(subProcessInstance!=null){
				executionId=subProcessInstance.getId();
				executions = subProcessInstance.getExecutions();
			}
			if(executions!=null&&executions.size()>0){
				Integer sum=0;
				Integer finishRate=0;
				Integer parentFinishRate=0;
				Integer finishCount=0;
				for (Execution executionTemp : executions) {
					Map<String, String> rateMapTemp = this.getRateByExecutionId(executionTemp.getId());
					if(rateMapTemp!=null){
						if(rateMapTemp.keySet().size()!=0){
							String taskRateStr = rateMapTemp.get("taskRate");
							String finishRateStr = rateMapTemp.get("finishRate");
							String parentFinishRateStr = rateMapTemp.get("parentFinishRate");
							if(StringUtils.isNotBlank(parentFinishRateStr)){
								parentFinishRate=Integer.valueOf(parentFinishRateStr);
							}
							if(StringUtils.isNotBlank(finishRateStr)){
								finishRate=Integer.valueOf(finishRateStr);
							}
							if(StringUtils.isNotBlank(taskRateStr)){
								sum+=Integer.valueOf(taskRateStr);
							}
						}else{
							finishCount++;
						}
					}
				}
				sum+=finishCount*finishRate;
				sum/=executions.size();
				rateMap.put("taskRate", String.valueOf(sum));
				rateMap.put("finishRate", String.valueOf(parentFinishRate));
			}else{
				Map<String, String> description = this.getDescriptionByExecutionId(executionId);
				if(description!=null){
					String rateStr = description.get("rate");
					if(StringUtils.isNotBlank(rateStr)){
						/**
						 * 因以前版本中有非json描述的所以如此判断。待全部改为json后可删除else部分
						 */
						if("{".equals(rateStr.substring(0, 1))){
							JSONObject fromObject = JSONObject.fromObject(rateStr);
							Set<String> keySet = fromObject.keySet();
							for (String key : keySet) {
								String string = String.valueOf(fromObject.get(key));
								rateMap.put(key, string);
							}
						}else{
							String[] rates = StringUtils.split(rateStr,",");
							if(rates.length>0){
								rateMap.put("taskRate", rates[0]);
							}
							if(rates.length>1){
								rateMap.put("finishRate", rates[1]);
							}
						}
					}
				}
			}
		}
		return rateMap;
	}
	
	/**
	 * 
	 * saveRateByExecutionId:保存流程百分比
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public String saveRateByExecutionId(String executionId){
		String rate = "0";
		List<BusinessWorkFlow> businessWorkFlows = this.findBusinessWorkFlowBySome(null, executionId);
		if(businessWorkFlows!=null&&businessWorkFlows.size()>0){
			BusinessWorkFlow businessWorkFlow = businessWorkFlows.get(0);
			if(businessWorkFlow!=null){
				/**
				 * 获取当前百分比
				 */
				Map<String, String> rateMap = this.getRateByExecutionId(executionId);
				rate = rateMap.get("taskRate");
				if(StringUtils.isNotBlank(rate)){
					businessWorkFlow.setRate(rate);
				}
				o_businessWorkFlowDAO.merge(businessWorkFlow);
			}
		}
		return rate;
	}
	
	/**
	 * 
	 * <pre>
	 * 获取流程当前的进度
	 * </pre>
	 * 
	 * @author 胡迪新
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	@Deprecated
	public Integer processRateOfProgress(String executionId) {
		String rate="100";
		Execution execution = o_jBPMOperate.getExecutionService().findExecutionById(executionId);
		if(execution!=null){
			String processInstanceId = execution.getProcessInstance().getId();
			List<BusinessWorkFlow> businessWorkFlows = this.findBusinessWorkFlowBySome(null, processInstanceId);
			if(businessWorkFlows!=null&&businessWorkFlows.size()>0){
				BusinessWorkFlow businessWorkFlow = businessWorkFlows.get(0);
				rate=businessWorkFlow.getRate();
				if(rate==null){
					String rootExecutionId = getExecutionIdBySubExecutionId(executionId);
					Map<String, String> rateMap = this.getRateByExecutionId(rootExecutionId);
					rate = rateMap.get("taskRate");
					businessWorkFlow.setRate(rate);
					o_businessWorkFlowDAO.merge(businessWorkFlow);
				}
			}
		}
		return null == rate ? 0:Integer.valueOf(rate);
	}
	/**
	 * 
	 * getExamineApproveIdeaAuthoritysByExecutionId:获得意见权限
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<String> getExamineApproveIdeaAuthoritysByExecutionId(String executionId){
		Map<String, String> description = this.getDescriptionByExecutionId(executionId);
		List<String> examineApproveIdeaAuthoritys=null;
		if(description!=null){
			String examineApproveIdeaAuthoritysStr = description.get("examineApproveIdeaAuthoritys");
			if(StringUtils.isNotBlank(examineApproveIdeaAuthoritysStr)){
				examineApproveIdeaAuthoritys=new ArrayList<String>();
				JSONArray fromObject = JSONArray.fromObject(examineApproveIdeaAuthoritysStr);
				for (Object object : fromObject) {
					examineApproveIdeaAuthoritys.add(String.valueOf(object));
				}
			}
		}
		return examineApproveIdeaAuthoritys;
	}
	
	/**
	 * 添加审批意见
	 * @param businessId 业务id
	 * @param processInstanceId 流程id
	 * @param operate 审批操作
	 * @param content 审批意见
	 * @param processPointName 流程节点名称
	 * @param empId 审批人
	 */
	@Transactional
	public void createExamineApproveIdea(String businessId,String executionId,String operate,String content, String empId,Long taskId) {
		ExamineApproveIdea eai = new ExamineApproveIdea();
		eai.setId(Identities.uuid());
		eai.setBusinessId(businessId);
		eai.setProcessInstanceId(executionId);
		eai.setEa_Operate(operate);
		eai.setEa_Content(content);
		eai.setEa_Date(new Date());
		eai.setGatherBy(o_sysEmployeeDAO.get(empId));
		VJbpmHistTask jbpmHistTask=new VJbpmHistTask();
		jbpmHistTask.setId(taskId);
		eai.setJbpmHistTask(jbpmHistTask);
		o_examineApproveIdeaDAO.merge(eai);
	}
	/**
	 * 
	 * mergeTaskAssignee:更改task处理人
	 * 
	 * @author 杨鹏
	 * @param taskId
	 * @param empId
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void mergeTaskAssignee(String taskId, String empId){
		o_jBPMOperate.getTaskService().assignTask(taskId, empId);
	}
	
	/**
	 * 
	 * mergeDeployProcessDefinitionDeploy:发布流程
	 * 
	 * @author 杨鹏
	 * @param processDefinitionDeploy
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void mergeDeployProcessDefinitionDeploy(ProcessDefinitionDeploy processDefinitionDeploy)throws Exception{
		o_processDefinitionDeployDAO.merge(processDefinitionDeploy);
		
		Criteria criteria = o_deploymentImplDAO.createCriteria();
		List<DeploymentImpl> list = criteria.list();
		List<Long> idList = new ArrayList<Long>();
		for (DeploymentImpl deploymentImpl : list) {
			idList.add(Long.valueOf(deploymentImpl.getId()));
		}
		
		FileUploadEntity fileUploadEntity = o_fileUploadBO.findById(processDefinitionDeploy.getFileUploadEntity().getId());
		ByteArrayInputStream byteArrayInputStream=null;
		byteArrayInputStream = new ByteArrayInputStream(fileUploadEntity.getContents());
		o_jBPMOperate.deploy(byteArrayInputStream);
		
		criteria.add(Restrictions.not(Restrictions.in("id", idList.toArray())));
		list = criteria.list();
		DeploymentImpl deploymentImpl = list.get(0);
		deploymentImpl.setName(processDefinitionDeploy.getId());
		o_deploymentImplDAO.merge(deploymentImpl);
		
	}
	
	/**
	 * 
	 * updateProcessDefinitionDeploy:更改发布流程
	 * 
	 * @author 杨鹏
	 * @param processDefinitionDeploy
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void mergeProcessDefinitionDeploy(ProcessDefinitionDeploy processDefinitionDeploy)throws Exception{
		o_processDefinitionDeployDAO.merge(processDefinitionDeploy);
	}
	
	/**
	 * 
	 * removeDeployment:删除发布流程
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeDeployment(String...ids) throws Exception{
		List<VJbpmDeployment> vJbpmDeployments = this.findVJbpmDeploymentBySome(ids);
		for (VJbpmDeployment vJbpmDeployment : vJbpmDeployments) {
			ProcessDefinitionDeploy processDefinitionDeploy = vJbpmDeployment.getProcessDefinitionDeploy();
			processDefinitionDeploy.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
			this.mergeProcessDefinitionDeploy(processDefinitionDeploy);
			FileUploadEntity fileUploadEntity = processDefinitionDeploy.getFileUploadEntity();
			if(fileUploadEntity!=null){
				o_fileUploadBO.removeFileById(fileUploadEntity.getId());
			}
			o_jBPMOperate.deletePorcessDefinition(vJbpmDeployment.getId());
		}
	}
	
	/**
	 * 
	 * findByPage:条件查询流程定义
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param sort
	 * @param dir
	 * @param sortList 
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<ProcessDefinitionDeploy> findProcessDefinitionDeployPageBySome(Page<ProcessDefinitionDeploy> page,String query,String deleteStatus, List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(ProcessDefinitionDeploy.class);
		criteria.setFetchMode("fileUploadEntity", FetchMode.JOIN);
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("disName", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		if(StringUtils.isNotBlank(deleteStatus)){
			criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
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
		
		return o_processDefinitionDeployDAO.findPage(criteria, page, false);
	}
	/**
	 * 
	 * findById:根据ID查询流程定义
	 * 
	 * @author 杨鹏
	 * @param id
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public ProcessDefinitionDeploy findProcessDefinitionDeployById(String id){
		ProcessDefinitionDeploy processDefinitionDeploy=null;
		Criteria criteria = o_processDefinitionDeployDAO.createCriteria();
		criteria.add(Restrictions.idEq(id));
		List<ProcessDefinitionDeploy> list = criteria.list();
		if(list!=null&&list.size()>0){
			processDefinitionDeploy = list.get(0);
		}
		return processDefinitionDeploy;
	}
	/**
	 * 
	 * findProcessDefinitionDeployByIds:
	 * 
	 * @author 杨鹏
	 * @param ids
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<ProcessDefinitionDeploy> findProcessDefinitionDeployByIds(String[] ids){
		Criteria criteria = o_processDefinitionDeployDAO.createCriteria();
		criteria.add(Restrictions.in("id", ids));
		return criteria.list();
	}
	
	/**
	 * 
	 * findVJbpmDeploymentBySome:查询所有发布流程
	 * 
	 * @author 杨鹏
	 * @param vJbpmDeploymentIdS
	 * @return
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	public List<VJbpmDeployment> findVJbpmDeploymentBySome(String...vJbpmDeploymentIdS)throws Exception{
		Criteria criteria = o_vJbpmDeploymentDAO.createCriteria();
		criteria.setFetchMode("processDefinitionDeploy", FetchMode.JOIN);
		criteria.add(Restrictions.in("id", vJbpmDeploymentIdS));
		return criteria.list();
	}
	
	/**
	 * 
	 * findProcessDefinitionDeployByPage:分页查询流程
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param sort
	 * @param dir
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<VJbpmDeployment> findVJbpmDeploymentPageBySome(Page<VJbpmDeployment> page,String query,List<Map<String, String>> sortList){
		DetachedCriteria criteria = DetachedCriteria.forClass(VJbpmDeployment.class);
		criteria.createAlias("processDefinitionDeploy", "processDefinitionDeploy");
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("processDefinitionDeploy.disName", query,MatchMode.ANYWHERE));
			disjunction.add(Restrictions.like("pdid", query,MatchMode.ANYWHERE));
			criteria.add(disjunction);
		}
		criteria.add(Restrictions.eq("processDefinitionDeploy.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if("disName".equalsIgnoreCase(property)){
					property="processDefinitionDeploy.disName";
				}
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						criteria.addOrder(Order.desc(property));
					}else{
						criteria.addOrder(Order.asc(property));
					}
				}
			}
		}
		return o_vJbpmDeploymentDAO.findPage(criteria, page,false);
	}
	
	/**
	 * 
	 * removePorcessInstance:删除流程实例
	 * 
	 * @author 杨鹏
	 * @param processInstanceIds
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removePorcessInstance(String...processInstanceIds) {
		for (String processInstanceId : processInstanceIds) {
			o_jBPMOperate.deletePorcessInstance(processInstanceId);
			List<HistoryProcessInstanceImpl> historyProcessInstanceImpls = o_historyProcessInstanceBO.findByProcessInstanceId(processInstanceId);
			HistoryProcessInstanceImpl historyProcessInstanceImpl = historyProcessInstanceImpls.get(0);
			historyProcessInstanceImpl.setEndActivityName("remove1");
			o_historyProcessInstanceBO.update(historyProcessInstanceImpl);
		}
	}
	
	/**
	 * 
	 * 暂停流程实例
	 * 
	 */
	@Transactional
	public void pausePorcessInstance(String id) {
		this.operateWorkFlow(id, JBPMConstant.JBPM_WORKFLOW_PAUSE);
	}

	/**
	 * 
	 * 启动流程实例
	 * 
	 */
	@Transactional
	public void startPorcessInstance(String id) {
		this.operateWorkFlow(id, JBPMConstant.JBPM_WORKFLOW_RUN);
	}

	/**
	 * 
	 * 操作
	 * 
	 */
	public void operateWorkFlow(String id, String state) {
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("state", state);
		// 如果不是子流程就改变该流程下的所有流程实例的状态
		if (!o_jBPMOperate.isSubPorcess(id)) {
			o_jBPMOperate.saveVariables(id,variables);
			//修改其下所有子流程的状态
			List<String> subIds = o_jBPMOperate.findSubPorcessInstanceIds(id, null);
			for(String subId : subIds){
				o_jBPMOperate.saveVariables(subId,variables);
			}			
		} else // 如果是子流程就改变该流程实例的状态
		{
			String parentId = o_jBPMOperate.findParentExcetionId(id);
			// 如果开子流程，也要把父流程开起来
			if (state.equals(JBPMConstant.JBPM_WORKFLOW_RUN)) {				
				o_jBPMOperate.saveVariables(id,variables);								
				if(parentId != id)
					o_jBPMOperate.saveVariables(parentId,variables);			
			}
			// 如果关子流程，要看其他的子流程是否全关了，来决定父流程的开启和关闭
			else {				
				o_jBPMOperate.saveVariables(id,variables);				
				Collection<ExecutionImpl> subInsts = o_jBPMOperate.findSubPorcessInstance(parentId);
				boolean flag = true;
				for(ExecutionImpl subInst:subInsts){
					state = (String) subInst.getVariable("state");
					if(JBPMConstant.JBPM_WORKFLOW_RUN.equals(state)){
						flag = false;
					}
				}
				if(flag){
					o_jBPMOperate.saveVariables(parentId,variables);
				}
			}
		}
	}

	/**
	 * 
	 * deploy:发布流程定义
	 * 
	 * @author 杨鹏
	 * @param processDefinitionDeployId
	 * @throws Exception
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void deploy(String processDefinitionDeployId) throws Exception{
		Criteria criteria = o_deploymentImplDAO.createCriteria();
		List<DeploymentImpl> list = criteria.list();
		List<Long> idList = new ArrayList<Long>();
		for (DeploymentImpl deploymentImpl : list) {
			idList.add(Long.valueOf(deploymentImpl.getId()));
		}
		
		ProcessDefinitionDeploy processDefinitionDeploy = this.findProcessDefinitionDeployById(processDefinitionDeployId);
		FileUploadEntity fileUploadEntity = processDefinitionDeploy.getFileUploadEntity();
		ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(fileUploadEntity.getContents());
		o_jBPMOperate.deploy(byteArrayInputStream);
		
		criteria.add(Restrictions.not(Restrictions.in("id", idList.toArray())));
		list = criteria.list();
		DeploymentImpl deploymentImpl = list.get(0);
		deploymentImpl.setName(processDefinitionDeployId);
		o_deploymentImplDAO.merge(deploymentImpl);
	}

	/**
	 * 
	 * findVJbpm4TaskCountBySome:
	 * 
	 * @author 杨鹏
	 * @param assigneeId
	 * @param endactivity
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Integer findVJbpm4TaskCountBySome(String assigneeId,String endactivity){
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer sql = new StringBuffer("");
		sql.append("	SELECT");
		sql.append("		COUNT(*)");
		sql.append("	FROM");
		sql.append("		jbpm4_task");
		sql.append("	LEFT JOIN jbpm4_execution ON jbpm4_execution.DBID_ = jbpm4_task.EXECUTION_");
		sql.append("	LEFT JOIN jbpm4_execution jbpm4_execution1 ON jbpm4_execution1.SUBPROCINST_ = jbpm4_execution.INSTANCE_");
		sql.append("	LEFT JOIN jbpm4_hist_procinst ON jbpm4_hist_procinst.DBID_=jbpm4_execution1.INSTANCE_ OR jbpm4_execution1.INSTANCE_ IS NULL AND jbpm4_hist_procinst.DBID_ = jbpm4_task.PROCINST_");
		sql.append("	LEFT JOIN t_jbpm_business_workflow ON t_jbpm_business_workflow.PROCESS_INSTANCE_ID=jbpm4_hist_procinst.ID_");
		sql.append("	LEFT JOIN jbpm4_deployprop ON jbpm4_deployprop.STRINGVAL_=jbpm4_hist_procinst.PROCDEFID_ AND jbpm4_deployprop.KEY_='PDID'");
		sql.append("	LEFT JOIN jbpm4_deployment ON jbpm4_deployment.DBID_=jbpm4_deployprop.DEPLOYMENT_");
		sql.append("	LEFT JOIN t_jbpm_process_define_deploy ON t_jbpm_process_define_deploy.ID=jbpm4_deployment.NAME_");
		sql.append("	WHERE");
		sql.append("		1=1");
		if(StringUtils.isNotBlank(assigneeId)){
			sql.append("	AND jbpm4_task.ASSIGNEE_ = :assigneeId");
			values.put("assigneeId",assigneeId);
		}
		if(StringUtils.isNotBlank(endactivity)){
			if("execut1".equals(endactivity)){
				sql.append("	AND jbpm4_hist_procinst.ENDACTIVITY_ IS NULL");
			}else{
				sql.append("	AND jbpm4_hist_procinst.ENDACTIVITY_ = :endactivity");
				values.put("endactivity",endactivity);
			}
		}
		SQLQuery sqlQuery = o_vJbpm4TaskDAO.createSQLQuery(sql.toString(), values);
		List<Object> list = sqlQuery.list();
		Integer count = Integer.valueOf(list.get(0).toString());
		return count;
	}
	/**
	 * 
	 * findDoneVJbpm4TaskCountByEmpId:
	 * 
	 * @author 杨鹏
	 * @param empId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Integer findDoneVJbpm4TaskCountByEmpId(String empId){
		return this.findVJbpm4TaskCountBySome(empId, "execut1");
	}
	
	/**
	 * 
	 * findJbpmHistActinstPageBySome:分页条件查询历史节点
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param query
	 * @param assigneeId
	 * @param endactivity
	 * @param dbversion
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<JbpmHistActinst> findJbpmHistActinstPageBySome(Page<JbpmHistActinst> page,String query,String assigneeId,String endactivity,Long dbversion, List<Map<String, String>> sortList){
		DetachedCriteria dc=DetachedCriteria.forClass(JbpmHistActinst.class);
		dc.createAlias("jbpmHistTask", "jbpmHistTask");
		dc.createAlias("jbpmHistProcinst", "jbpmHistProcinst");
		dc.createAlias("jbpmHistProcinst.vJbpmDeployment", "vJbpmDeployment");
		dc.createAlias("jbpmHistProcinst.vJbpmDeployment.processDefinitionDeploy", "processDefinitionDeploy");
		dc.createAlias("jbpmExecution", "jbpmExecution");
		dc.setFetchMode("jbpmHistTask.assignee", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistTask.assignee.sysOrganization", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistTask.examineApproveIdea", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistProcinst.businessWorkFlow", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistProcinst.businessWorkFlow.createByEmp", FetchMode.JOIN);
		
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("activityName", query,MatchMode.ANYWHERE));
			if("未处理".equals(query)){
				disjunction.add(Restrictions.eq("dbversion", 0));
			}else if("已处理".equals(query)){
				disjunction.add(Restrictions.eq("dbversion", 1));
			}
			dc.add(disjunction);
		}
		
		if(StringUtils.isNotBlank(assigneeId)){
			dc.add(Restrictions.eq("jbpmHistTask.assignee.id", assigneeId));
		}
		if(StringUtils.isNotBlank(endactivity)){
			if("execut1".equals(endactivity)){
				dc.add(Restrictions.isNull("jbpmHistProcinst.endactivity"));
			}else{
				dc.add(Restrictions.eq("jbpmHistProcinst.endactivity", endactivity));
			}
		}
		if(dbversion!=null){
			dc.add(Restrictions.eq("dbversion", dbversion));
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if("disName".equals(property)){
					property="processDefinitionDeploy.disName";
				}else if("dbversionStr".equals(property)){
					property="dbversion";
				}else if("startStr".equals(property)){
					property="start";
				}else if("startDate".equals(property)){
					property="start";
				}else if("endStr".equals(property)){
					property="jbpmHistTask.end";
				}
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						dc.addOrder(Order.desc(property));
					}else{
						dc.addOrder(Order.asc(property));
					}
				}
			}
		}
		return o_jbpmHistActinstDAO.findPage(dc, page,false);
	}
	
	/**
	 * 
	 * findJbpmHistActinstPageBySome:分页条件查询历史节点
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param jbpmHistProcinstId
	 * @param dbversion
	 * @param sort
	 * @param dir
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Page<JbpmHistActinst> findJbpmHistActinstPageBySome(Page<JbpmHistActinst> page,String query,String executionId,String processInstanceId,
			String assigneeId,Long jbpmHistProcinstId,String endactivity,Long dbversion, List<Map<String, String>> sortList){
		DetachedCriteria dc=DetachedCriteria.forClass(JbpmHistActinst.class);
		dc.createAlias("jbpmHistTask", "jbpmHistTask");
		dc.createAlias("jbpmHistProcinst", "jbpmHistProcinst");
		dc.createAlias("jbpmHistProcinst.vJbpmDeployment", "vJbpmDeployment");
		dc.createAlias("jbpmHistProcinst.vJbpmDeployment.processDefinitionDeploy", "processDefinitionDeploy");
		dc.setFetchMode("jbpmExecution", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistTask.assignee", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistTask.assignee.sysOrganization", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistTask.examineApproveIdea", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistProcinst.businessWorkFlow", FetchMode.JOIN);
		dc.setFetchMode("jbpmHistProcinst.businessWorkFlow.createByEmp", FetchMode.JOIN);
		
		if(StringUtils.isNotBlank(query)){
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.like("activityName", query,MatchMode.ANYWHERE));
			if("未处理".equals(query)){
				disjunction.add(Restrictions.eq("dbversion", 0));
			}else if("已处理".equals(query)){
				disjunction.add(Restrictions.eq("dbversion", 1));
			}
			dc.add(disjunction);
		}
		
		if(StringUtils.isNotBlank(executionId)){
			dc.add(Restrictions.eq("execution_", executionId));
			List<String> examineApproveIdeaAuthoritys = this.getExamineApproveIdeaAuthoritysByExecutionId(executionId);
			if(examineApproveIdeaAuthoritys!=null){
				dc.add(Restrictions.in("activityName", examineApproveIdeaAuthoritys.toArray(new String[examineApproveIdeaAuthoritys.size()])));
			}
		}
		if(StringUtils.isNotBlank(processInstanceId)){
			dc.add(Restrictions.eq("jbpmHistProcinst.id_", processInstanceId));
		}
		if(StringUtils.isNotBlank(assigneeId)){
			dc.add(Restrictions.eq("jbpmHistTask.assignee.id", assigneeId));
		}
		if(jbpmHistProcinstId!=null){
			DetachedCriteria subProcinstCriteria=DetachedCriteria.forClass(JbpmHistActinst.class);
			subProcinstCriteria.createAlias("jbpmHistProcinst", "jbpmHistProcinst");
			subProcinstCriteria.createAlias("jbpmExecution", "jbpmExecution");
			subProcinstCriteria.createAlias("jbpmExecution.jbpmHistSubProcinst", "jbpmHistSubProcinst");
			subProcinstCriteria.add(Restrictions.eq("jbpmHistProcinst.id", jbpmHistProcinstId));
			subProcinstCriteria.setProjection(Property.forName("jbpmHistSubProcinst.id"));
			dc.add(Restrictions.or(Restrictions.eq("jbpmHistProcinst.id", jbpmHistProcinstId),Property.forName("jbpmHistProcinst.id").in(subProcinstCriteria)));
		}
		if(StringUtils.isNotBlank(endactivity)){
			if("execut1".equals(endactivity)){
				dc.add(Restrictions.isNull("jbpmHistProcinst.endactivity"));
			}else{
				dc.add(Restrictions.eq("jbpmHistProcinst.endactivity", endactivity));
			}
		}
		if(dbversion!=null){
			dc.add(Restrictions.eq("dbversion", dbversion));
		}
		if(sortList!=null&&sortList.size()>0){
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if("disName".equals(property)){
					property="processDefinitionDeploy.disName";
				}else if("dbversionStr".equals(property)){
					property="dbversion";
				}else if("startStr".equals(property)){
					property="start";
				}else if("startDate".equals(property)){
					property="start";
				}else if("endStr".equals(property)){
					property="jbpmHistTask.end";
				}
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						dc.addOrder(Order.desc(property));
					}else{
						dc.addOrder(Order.asc(property));
					}
				}
			}
		}
		return o_jbpmHistActinstDAO.findPage(dc, page,false);
	}
	/**
	 * 
	 * findVJbpm4TaskBySome:
	 * 
	 * @author 杨鹏
	 * @param query
	 * @param assigneeId
	 * @param endactivity
	 * @param sortList
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<VJbpm4Task> findVJbpm4TaskBySome(String query,String assigneeId,String endactivity, List<Map<String, String>> sortList){
		List<VJbpm4Task> vJbpm4TaskList=new ArrayList<VJbpm4Task>();
		Map<String,Object> values = new HashMap<String,Object>();
		StringBuffer sql = new StringBuffer("");
		sql.append("	SELECT");
		sql.append("		jbpm4_task.DBID_,jbpm4_hist_procinst.DBID_ JBPM4HISTPROCINSTID,jbpm4_hist_procinst.ENDACTIVITY_,	t_jbpm_process_define_deploy.DIS_NAME,	t_jbpm_business_workflow.PROCESS_INSTANCE_ID,	jbpm4_task.ACTIVITY_NAME_,	t_jbpm_business_workflow.BUSINESS_ID,	t_jbpm_business_workflow.BUSINESS_NAME,	jbpm4_task.CREATE_,	jbpm4_task.FORM_,	t_jbpm_business_workflow.RATE,jbpm4_task.EXECUTION_ID_,jbpm4_task.ASSIGNEE_ ASSIGNEEID,plan.schm");
		sql.append("	FROM");
		sql.append("		jbpm4_task");
		sql.append("	LEFT JOIN jbpm4_execution ON jbpm4_execution.DBID_ = jbpm4_task.EXECUTION_");
		sql.append("	LEFT JOIN jbpm4_execution jbpm4_execution1 ON jbpm4_execution1.SUBPROCINST_ = jbpm4_execution.INSTANCE_");
		sql.append("	LEFT JOIN jbpm4_hist_procinst ON jbpm4_hist_procinst.DBID_=jbpm4_execution1.INSTANCE_ OR jbpm4_execution1.INSTANCE_ IS NULL AND jbpm4_hist_procinst.DBID_ = jbpm4_task.PROCINST_");
		sql.append("	LEFT JOIN t_jbpm_business_workflow ON t_jbpm_business_workflow.PROCESS_INSTANCE_ID=jbpm4_hist_procinst.ID_");
		sql.append("	LEFT JOIN jbpm4_deployprop ON jbpm4_deployprop.STRINGVAL_=jbpm4_hist_procinst.PROCDEFID_ AND jbpm4_deployprop.KEY_='PDID'");
		sql.append("	LEFT JOIN jbpm4_deployment ON jbpm4_deployment.DBID_=jbpm4_deployprop.DEPLOYMENT_");
		sql.append("	LEFT JOIN t_jbpm_process_define_deploy ON t_jbpm_process_define_deploy.ID=jbpm4_deployment.NAME_");
		//吉志强      待办执行过程中返回每个待办任务对应的流程实例所对应的计划的schm
		sql.append("	left join t_rm_risk_assess_plan plan on t_jbpm_business_workflow.BUSINESS_ID = plan.ID");
		sql.append("	WHERE");
		sql.append("		1=1");
		if(StringUtils.isNotBlank(query)){
			sql.append("	AND t_jbpm_business_workflow.BUSINESS_NAME LIKE CONCAT('%',CONCAT(:query,'%'))");
			values.put("query",query);
		}
		if(StringUtils.isNotBlank(assigneeId)){
			sql.append("	AND jbpm4_task.ASSIGNEE_ = :assigneeId");
			values.put("assigneeId",assigneeId);
		}
		if(StringUtils.isNotBlank(endactivity)){
			if("execut1".equals(endactivity)){
				sql.append("	AND jbpm4_hist_procinst.ENDACTIVITY_ IS NULL");
			}else{
				sql.append("	AND jbpm4_hist_procinst.ENDACTIVITY_ = :endactivity");
				values.put("endactivity",endactivity);
			}
		}
		if(sortList!=null&&sortList.size()>0){
			sql.append("	ORDER BY");
			List<String> sortSqlList = new ArrayList<String>();
			for (Map<String, String> sort : sortList) {
				String property=sort.get("property");
				String direction = sort.get("direction");
				if("createStr".equals(property)){
					property="jbpm4_task.CREATE_";
				}else if("createDate".equals(property)){
					property="jbpm4_task.CREATE_";
				}else if("disName".equals(property)){
					property="t_jbpm_process_define_deploy.DIS_NAME";
				}
				if(StringUtils.isNotBlank(property) && StringUtils.isNotBlank(direction)){
					if("desc".equalsIgnoreCase(direction)){
						sortSqlList.add("	"+property+" DESC");
					}else{
						sortSqlList.add("	"+property+" ASC");
					}
				}
			}
			sql.append(StringUtils.join(sortSqlList.toArray(new String[sortSqlList.size()]),","));
		}
		SQLQuery sqlQuery = o_vJbpm4TaskDAO.createSQLQuery(sql.toString(), values);
		List<Object[]> list = sqlQuery.list();
		for (Object[] objects : list) {
			VJbpm4Task vJbpm4Task = new VJbpm4Task();
			int i=0;
			Object idObj = objects[i++];
			if(idObj!=null){
				vJbpm4Task.setId(Long.valueOf(idObj.toString()));
			}
			Object jbpm4HistProcinstIdObj = objects[i++];
			if(jbpm4HistProcinstIdObj!=null){
				vJbpm4Task.setJbpm4HistProcinstId(Long.valueOf(jbpm4HistProcinstIdObj.toString()));
			}
			Object endactivityObj = objects[i++];
			if(endactivityObj!=null){
				vJbpm4Task.setEndactivity(endactivityObj.toString());
			}
			Object disNameObj = objects[i++];
			if(disNameObj!=null){
				vJbpm4Task.setDisName(disNameObj.toString());
			}
			i++;
			Object activityNameObj = objects[i++];
			if(activityNameObj!=null){
				vJbpm4Task.setActivityName(activityNameObj.toString());
			}
			Object businessIdObj = objects[i++];
			if(businessIdObj!=null){
				vJbpm4Task.setBusinessId(businessIdObj.toString());
			}
			Object businessNameObj = objects[i++];
			if(businessNameObj!=null){
				vJbpm4Task.setBusinessName(businessNameObj.toString());
			}
			Object createObj = objects[i++];
			if(createObj!=null){
				vJbpm4Task.setCreate((Date)createObj);
			}
			Object formObj = objects[i++];
			if(formObj!=null){
				vJbpm4Task.setForm(formObj.toString());
			}
			Object rateObj = objects[i++];
			if(rateObj!=null){
				vJbpm4Task.setRate(rateObj.toString());
			}
			Object executionIdObj = objects[i++];
			if(executionIdObj!=null){
				vJbpm4Task.setExecutionId(executionIdObj.toString());
			}
			Object assigneeIdObj = objects[i++];
			if(executionIdObj!=null){
				vJbpm4Task.setAssigneeId(assigneeIdObj.toString());
			}
			Object schm = objects[i++];
			if(schm!=null){
				vJbpm4Task.setSchm(schm.toString());
			}
			
			vJbpm4TaskList.add(vJbpm4Task);
		}
		return vJbpm4TaskList;
	}

	
	
	/**
	 * 
	 * findJbpmHistActinstPageBySome:分页条件查询历史节点
	 * 
	 * @author 杨鹏
	 * @param page
	 * @param jbpmHistProcinstId
	 * @param dbversion
	 * @param jbpmExecutionId 
	 * @param assigneeId :执行人ID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<JbpmHistActinst> findJbpmHistActinstBySome(Long jbpmHistProcinstId,Long dbversion, String jbpmExecutionId, String assigneeId){
		Criteria criteria = o_jbpmHistActinstDAO.createCriteria();
		criteria.createAlias("jbpmHistTask", "jbpmHistTask");
		criteria.createAlias("jbpmExecution", "jbpmExecution");
		if(jbpmHistProcinstId!=null){
			criteria.add(Restrictions.eq("jbpmHistProcinst.id", jbpmHistProcinstId));
		}
		if(dbversion!=null){
			criteria.add(Restrictions.eq("dbversion", dbversion));
		}
		if(StringUtils.isNotBlank(jbpmExecutionId)){
			criteria.add(Restrictions.eq("jbpmExecution.id_", jbpmExecutionId));
		}
		if(StringUtils.isNotBlank(assigneeId)){
			criteria.add(Restrictions.eq("jbpmHistTask.assignee.id", assigneeId));
		}
		return criteria.list();
	}
	
	public List<Object[]> findJbpmHistActinstBySome(String executionId){
		String sql = "select jha.DBID_,jha.ACTIVITY_NAME_,jha.DURATION_," +
			"(select jhk.ASSIGNEE_ from jbpm4_hist_task jhk " +
			"where jhk.DBID_ = jha.HTASK_) ASSIGNEE_," +
			"(select e.EMP_NAME from t_sys_employee e INNER JOIN jbpm4_hist_task jhk2 " +
				"on jhk2.ASSIGNEE_ = e.id where jhk2.DBID_ = jha.HTASK_ ) empname," +
			"(select o.ORG_NAME from t_sys_employee e INNER JOIN t_sys_organization o " +
				"on e.ORG_ID = o.ID INNER JOIN jbpm4_hist_task jhk3 " +
				"on jhk3.ASSIGNEE_ = e.id where jhk3.DBID_ = jha.HTASK_ ) orgname," +
			"(select i.EA_CONTENT from  t_jbpm_examine_approve_idea i  where i.TASK_ID like CONCAT(jha.HTASK_,'.0000'))," +
			"jha.START_,jha.END_,jha.EXECUTION_,jha.HTASK_ " +
			"from jbpm4_hist_actinst jha " +
			"where jha.HPROCI_ = ? and jha.TYPE_ = ?  ORDER BY DBID_ DESC";
		
		SQLQuery query = o_jbpmHistActinstDAO.createSQLQuery(sql, executionId, "task");
		return query.list();
		
		
	}
	
	
	
	
	
	/**
	 * 
	 * findBusinessWorkFlowBySome:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @param processInstanceId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<BusinessWorkFlow> findBusinessWorkFlowBySome(String id,String processInstanceId){
		Criteria criteria = o_businessWorkFlowDAO.createCriteria();
		if(StringUtils.isNotBlank(id)){
			criteria.add(Restrictions.idEq(id));
		}
		if(StringUtils.isNotBlank(processInstanceId)){
			criteria.add(Restrictions.eq("processInstanceId", processInstanceId));
		}
		return criteria.list();
	}
	
	/**
	 * 
	 * findBusinessWorkFlowBySome:查询流程、业务实体关系
	 * 
	 * @author 杨鹏
	 * @param id
	 * @param processInstanceId 流程实例ID
	 * @param businessId 业务ID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<BusinessWorkFlow> findBusinessWorkFlowBySome(String id,String processInstanceId,String businessId){
		Criteria criteria = o_businessWorkFlowDAO.createCriteria();
		if(StringUtils.isNotBlank(id)){
			criteria.add(Restrictions.idEq(id));
		}
		if(StringUtils.isNotBlank(processInstanceId)){
			criteria.add(Restrictions.eq("processInstanceId", processInstanceId));
		}
		if(StringUtils.isNotBlank(businessId)){
			criteria.add(Restrictions.eq("businessId", businessId));
		}
		return criteria.list();
	}
	/**
	 * 
	 * findBusinessWorkFlowBySome:
	 * 
	 * @author 杨鹏
	 * @param id
	 * @param processInstanceId
	 * @param businessIds
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<BusinessWorkFlow> findBusinessWorkFlowBySome(String id,String processInstanceId,String businessObjectType,String...businessIds){
		Criteria criteria = o_businessWorkFlowDAO.createCriteria();
		if(StringUtils.isNotBlank(id)){
			criteria.add(Restrictions.idEq(id));
		}
		if(StringUtils.isNotBlank(processInstanceId)){
			criteria.add(Restrictions.eq("processInstanceId", processInstanceId));
		}
		if(StringUtils.isNotBlank(businessObjectType)){
			criteria.add(Restrictions.eq("businessObjectType", businessObjectType));
		}
		if(businessIds!=null&&businessIds.length>0){
			criteria.add(Restrictions.in("businessId", businessIds));
		}
		return criteria.list();
	}
	/**
	 * 根据业务实体ID 查询JbpmHistProcinst实体
	 * @author 邓广义
	 * @param businessId
	 * @return
	 */
	public JbpmHistProcinst findJbpmHistProcinstByBusinessId(String businessId){
		if(StringUtils.isNotBlank(businessId)){
			List<BusinessWorkFlow> bwf = this.findBusinessWorkFlowBySome(null,null,businessId);
			if(null!=bwf && bwf.size()==1){
				List<JbpmHistProcinst> list = o_jbpmHistProcinstBO.findJbpmHistProcinstByProcessInstanceId(bwf.get(0).getProcessInstanceId());
				if(null!=list && list.size()==1){
					return list.get(0);
				}
			}
		}
		return null;
	}
	/**
	 * 
	 * findTaskByExecutionId:根据执行ID获得代办
	 * 
	 * @author 杨鹏
	 * @param executionId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public Task findTaskByExecutionId(String executionId){
		List<Task> list = o_jBPMOperate.getTaskService().createTaskQuery().executionId(executionId).list();
		return list.get(0);
	}
	
	/**
	 * 
	 * findTaskByProcessInstanceId:根据流程实例ID查询所有代办
	 * 
	 * @author 杨鹏
	 * @param processInstanceId
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<Task> findTaskByProcessInstanceId(String processInstanceId){
		List<Task> list = o_jBPMOperate.getTaskService().createTaskQuery().processInstanceId(processInstanceId).list();
		return list;
	}
	/**
	 * 
	 * UntreadToActinst:自由流退回
	 * 
	 * @author 杨鹏
	 * @param executionId:执行ID
	 * @param processInstanceId:流程实例ID
	 * @param untreadToActinst:退回节点名称
	 * @since  fhd　Ver 1.1
	 */
	public void UntreadToActinst(String executionId,String processInstanceId,String untreadToActinst) {
		Task task = o_jBPMOperate.getTaskService().createTaskQuery().executionId(executionId).uniqueResult();
		String activityName = task.getActivityName();
		/**
		 * 获得流程定义
		 */
		HistoryProcessInstance historyProcessInstance = o_jBPMOperate.getHistoryService().createHistoryProcessInstanceQuery().processInstanceId(processInstanceId).uniqueResult();
		String processDefinitionId = historyProcessInstance.getProcessDefinitionId();
		ProcessDefinition processDefinition = o_jBPMOperate.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processDefinitionId).uniqueResult();
		ProcessDefinitionImpl processDefinitionImpl = (ProcessDefinitionImpl)processDefinition;
		/**
		 * 新建自由流 
		 */
		ActivityImpl sourceActivity = processDefinitionImpl.findActivity(activityName);
		ActivityImpl destActivity = processDefinitionImpl.findActivity(untreadToActinst);
		TransitionImpl transition = sourceActivity.createOutgoingTransition();
		transition.setName(Contents.JBPM_ACTINST_UNTREAD);  
		transition.setDestination(destActivity);
		sourceActivity.addOutgoingTransition(transition);
		/**
		 * 执行自由流
		 */
		o_jBPMOperate.getTaskService().completeTask(task.getId(),transition.getName(), new HashMap<String, Object>());
		/**
		 * 删除自由流
		 */
		sourceActivity.removeOutgoingTransition(transition);
	}
	/**
	 * 
	 * removeTask:删除节点
	 * 
	 * @author 杨鹏
	 * @param taskIds
	 * @since  fhd　Ver 1.1
	 */
	@Transactional
	public void removeTask(String[] taskIds){
		for (String taskId : taskIds) {
			Task task = o_jBPMOperate.getTaskService().getTask(taskId);
			Execution execution = o_jBPMOperate.getExecutionService().findExecutionById(task.getExecutionId());
			if(execution.getIsProcessInstance()){
				this.removePorcessInstance(execution.getProcessInstance().getId());
			}else{
				o_jBPMOperate.getExecutionService().signalExecutionById(execution.getId());
			}
			JbpmHistActinst jbpmHistActinst = o_jbpmHistActinstBO.findByTaskId(Long.valueOf(taskId));
			jbpmHistActinst.setTransition(Contents.JBPM_ACTINST_REMOVE);
			jbpmHistActinst.setDbversion(1L);
			o_jbpmHistActinstBO.merge(jbpmHistActinst);
		}
	}
	/**
	 * 
	 * findPDDByExecutionId:获得流程定义描述
	 * 
	 * @author 杨鹏
	 * @param executionId执行ID
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public String findPDDByExecutionId(String executionId){
		Execution execution = o_jBPMOperate.getExecutionService().findExecutionById(executionId);
		ProcessDefinition processDefinition = o_jBPMOperate.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(execution.getProcessDefinitionId()).uniqueResult();
		return processDefinition.getDescription();
	}
	/**
	 * 
	 * findPDDByPDK:获得流程定义描述
	 * 
	 * @author 杨鹏
	 * @param processDefinitionKey流程定义类型
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public String findPDDByPDK(String processDefinitionKey){
		ProcessDefinition processDefinition = o_jBPMOperate.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).uniqueResult();
		return processDefinition.getDescription();
	}
	/**  
	* @Title: getVariable  
	* @Description: 获取流程标量通过执行id和key
	* @param key
	* @param executionId
	* @return String
	* @throws  
	* @author Jzq
	*/
	public String getVariable(String key,String executionId){
		return this.o_jBPMOperate.getVariable(executionId, key);
	}
	public Object getVariableObj(String key,String executionId){
		return this.o_jBPMOperate.getVariableObj(executionId, key);
	}
}