package com.fhd.ra.business.response.major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.core.utils.Identities;
import com.fhd.dao.response.major.RiskResponseRiskSchemeRelaDAO;
import com.fhd.dao.response.major.RiskResponseSchemeDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.response.major.RiskResponsePlanRiskRela;
import com.fhd.entity.response.major.RiskResponseRiskSchemeRela;
import com.fhd.entity.response.major.RiskResponseScheme;
import com.fhd.entity.response.major.RiskResponseSchemeForm;
import com.fhd.entity.response.major.RiskResponseTaskExecution;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.web.controller.response.major.utils.DateUtil;
import com.fhd.ra.web.controller.response.major.utils.Tasker;

@Service
public class RiskResponseSchemeService {

	private static final Logger log = LoggerFactory.getLogger(RiskResponseSchemeService.class);
	@Autowired
	private RiskResponseSchemeDAO riskResponseSchemeDAO;
	@Autowired
	private RiskResponseRiskSchemeRelaService riskResponseRiskSchemeRelaService;
	@Autowired
	private RiskResponseRiskSchemeRelaDAO RiskResponseRiskSchemeRelaDAO;
	@Autowired
	private RiskResponseTaskExecutionService riskResponseTaskExecutionService;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Transactional
	public void save(RiskResponseScheme entity)throws Exception{
		this.riskResponseSchemeDAO.merge(entity);
	}
	@Transactional
	public void saveBatch(final List<RiskResponseScheme> list)throws Exception{
		 this.riskResponseSchemeDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_scheme(id,description,analysis,target,strategy,start_time,finish_time,type,create_user,create_org) values(?,?,?,?,?,?,?,?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponseScheme obj : list) {
	                	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getDescription());
	                      pst.setString(3, obj.getAnalysis());
	                      pst.setString(4, obj.getTarget()); 
	                      pst.setString(5, obj.getStrategy());
	                      pst.setString(6, DateUtil.format(obj.getStartTime(), "yyyy-mm-dd"));
	                      pst.setString(7, DateUtil.format(obj.getFinishTime(), "yyyy-mm-dd"));
	                      pst.setString(8, obj.getType());
	                      pst.setString(9, obj.getCreateUser().getId());
	                      pst.setString(10, obj.getCreateOrg().getId());
	                     
	                      pst.addBatch();
					}
	                pst.executeBatch();
	            }
	        });
	}
	@Transactional
	public void deleteBatchByList(final List<RiskResponseScheme> list)throws Exception{
		this.riskResponseSchemeDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_scheme where id = ? ";
                pst = connection.prepareStatement(sql);
                for (RiskResponseScheme entity: list) {
                	pst.setString(1,entity.getId());
                	pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
	}
	@Transactional
	public void update(RiskResponseScheme entity)throws Exception{
		this.riskResponseSchemeDAO.merge(entity);
	}
	@Transactional
	public void delete(RiskResponseScheme entity)throws Exception{
		this.riskResponseSchemeDAO.delete(entity.getId());
	}
	
	public RiskResponseScheme findObj(RiskResponseScheme entity){
		return this.riskResponseSchemeDAO.get(entity.getId());
	}
	@Transactional
	public RiskResponseScheme saveScheme(RiskResponseSchemeForm riskResponseSchemeForm,String planId,String riskId,String deptId,String currentEmpId,String empType, RiskResponseTaskExecution executionObj){
		RiskResponseScheme scheme = new RiskResponseScheme();
		String id = "";
		if(riskResponseSchemeForm.getId() ==null ||"".equals(riskResponseSchemeForm.getId()) ){
			id = Identities.uuid();
		}else{
			id = riskResponseSchemeForm.getId();
		}
		Date _startTime = null;
		Date _finishTime = null;
		try {
			 _startTime =riskResponseSchemeForm.getStartTimeStr()!=null ? DateUtil.parse(riskResponseSchemeForm.getStartTimeStr(), "yyyy-dd-mm"):null ;
			 _finishTime =riskResponseSchemeForm.getFinishTimeStr()!= null? DateUtil.parse(riskResponseSchemeForm.getFinishTimeStr(), "yyyy-dd-mm"):null ;
		} catch (ParseException e) {
			log.debug("date parse failed !");
			e.printStackTrace();
		}
		scheme.setId(id);
		scheme.setDescription(riskResponseSchemeForm.getDescription());
		scheme.setAnalysis(riskResponseSchemeForm.getAnalysis());
		scheme.setTarget(riskResponseSchemeForm.getTarget());
		scheme.setStrategy(riskResponseSchemeForm.getStrategy());
		scheme.setStartTime(_startTime);
		scheme.setFinishTime(_finishTime);
		scheme.setType(riskResponseSchemeForm.getType());
		scheme.setCreateUser(riskResponseSchemeForm.getCreateUser());
		scheme.setCreateOrg(riskResponseSchemeForm.getCreateOrg());
		this.riskResponseSchemeDAO.merge(scheme);
		RiskResponseRiskSchemeRela isHas = riskResponseRiskSchemeRelaService.findByTaskExecution(executionObj);
		RiskResponseRiskSchemeRela riskSchemeRela = new RiskResponseRiskSchemeRela();
		String riskSchemeRelaId = "";
		if(isHas!=null){
			riskSchemeRelaId =isHas.getId(); 
		}else{
			riskSchemeRelaId = Identities.uuid();
		}
		riskSchemeRela.setId(riskSchemeRelaId);
		riskSchemeRela.setScheme(scheme);
		riskSchemeRela.setTaskExecutionObj(executionObj);
		//保存关系
		RiskResponseRiskSchemeRelaDAO.merge(riskSchemeRela);
		
		return scheme;
	}
	
	/**  
	* @Title: loadScheme  
	* @Description: 加载方案表单数据根据计划id和风险id部门id以及人员类型
	* @param planId
	* @param riskId
	* @param deptId
	* @param empType 1非汇总 2汇总
	* @return Map<String,Object>
	* @throws  
	*/
	public Map<String,Object> loadScheme(String planId,String riskId,String deptId,String empType){
		Map<String,Object> map = new HashMap<String,Object>();
		String currentUserId = UserContext.getUser().getEmpid();
		RiskResponseTaskExecution executionObj = this.riskResponseTaskExecutionService.getExecutionObj(planId,riskId,deptId,currentUserId,empType);
		RiskResponseRiskSchemeRela riskSchemeRela = this.riskResponseRiskSchemeRelaService.findByTaskExecution(executionObj);
		String id = "";
		String description="";
		String analysis = "";
		String target = "";
		String strategy = "";
		Date startTime = null;
		Date finishTime = null;
		String type = "";
		if(riskSchemeRela != null){
			RiskResponseScheme scheme = riskSchemeRela.getScheme();
			id = scheme.getId();
			description = scheme.getDescription();
			analysis = scheme.getAnalysis();
			strategy = scheme.getStrategy();
			startTime = scheme.getStartTime();
			finishTime = scheme.getFinishTime();
			type = scheme.getType();
			target = scheme.getTarget();
		}
		map.put("id", id);
		map.put("description", description);
		map.put("analysis", analysis);
		map.put("strategy", strategy);
		map.put("startTimeStr", startTime ==null ? "":DateUtil.format(startTime, "yyyy-mm-dd"));
		map.put("finishTimeStr", finishTime ==null ? "":DateUtil.format(finishTime, "yyyy-mm-dd"));
		map.put("type", type);
		map.put("target", target);
		return map;
	}
	
	/**  
	* @Title: schemeApprove  
	* @Description: 部门主管，领导，副总审批流程
	* @param businessId
	* @param executionId
	* @param isPass
	* @param examineApproveIdea
	* @param approverId
	* @param approverKey
	* @return Map<String,Object>
	* @throws  
	*/
	public Map<String,Object> schemeApprove(String businessId,
			 String executionId,
			 String isPass,
			 String examineApproveIdea,
			 String approverId,
			 String approverKey){
		 Map<String,Object> map = new HashMap<String,Object>();
		 boolean flag = false;
		 Map<String, Object> variables = new HashMap<String, Object>();
		 variables.put("path", isPass);
		 variables.put("examineApproveIdea", examineApproveIdea);
		 if("yes".equals(isPass)){
			 if(!approverKey.equals("officeSummary")){//方案审批通过不需要添加该流程变量，在分配或启动流程时已指定
				 variables.put(approverKey, approverId);
			 }else{
				 //TODO 方案审批节点通过即发起方案实施流程
			 }
			 
		 }
		 this.o_jbpmBO.doProcessInstance(executionId, variables);
		 flag = true;
		 map.put("success", flag);
		 return map;
	 }
}
