package com.fhd.ra.business.response.major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.response.major.RiskResponseTaskExecutionDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.response.major.RiskResponsePlanRiskRela;
import com.fhd.entity.response.major.RiskResponseTaskExecution;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysOrganization;

@Service
public class RiskResponseTaskExecutionService {
	
	private static final Logger log = LoggerFactory.getLogger(RiskResponseTaskExecutionService.class);
	@Autowired
	private RiskResponseTaskExecutionDAO riskResponseTaskExecutionDao;
	@Autowired
	private RiskResponsePlanRiskRelaService riskResponsePlanRiskRelaService;
	@Transactional
	public void save(RiskResponseTaskExecution entity)throws Exception{
		this.riskResponseTaskExecutionDao.merge(entity);
	}
	@Transactional
	public void saveBatch(final List<RiskResponseTaskExecution> list)throws Exception{
		 this.riskResponseTaskExecutionDao.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_task_execution(id,plan_risk_rela_id,emp_id,emp_type) values(?,?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponseTaskExecution obj : list) {
	                	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getPlanRiskRelaObj().getId());
	                      pst.setString(3, obj.getExecuteEmp().getId());
	                      pst.setString(4, obj.getEmpType());
	                      pst.addBatch();
					}
	                pst.executeBatch();
	            }
	        });
	}
	@Transactional
	public void deleteBatchByList(final List<RiskResponseTaskExecution> list)throws Exception{
		this.riskResponseTaskExecutionDao.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_task_execution where plan_risk_rela_id = ? and emp_id = ? and  emp_type = ? ";
                pst = connection.prepareStatement(sql);
                for (RiskResponseTaskExecution entity: list) {
                	pst.setString(1,entity.getPlanRiskRelaObj().getId());
                	pst.setString(2,entity.getExecuteEmp().getId());
                	pst.setString(3,entity.getEmpType());
                	pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
	}
	@Transactional
	public void update(RiskResponseTaskExecution entity)throws Exception{
		this.riskResponseTaskExecutionDao.merge(entity);
	}
	@Transactional
	public void delete(RiskResponseTaskExecution entity)throws Exception{
		this.riskResponseTaskExecutionDao.delete(entity.getId());
	}
	
	public RiskResponseTaskExecution findObj(RiskResponseTaskExecution entity)throws Exception{
		return this.riskResponseTaskExecutionDao.get(entity.getId());
	}
	
	@SuppressWarnings("unchecked")
	public List<RiskResponseTaskExecution> findList(RiskResponseTaskExecution entity){
		Criteria criteria = this.riskResponseTaskExecutionDao.createCriteria();
		if(entity.getPlanRiskRelaObj()!= null){
			criteria.createAlias("planRiskRelaObj", "planRiskRelaObj");
			criteria.add(Restrictions.eq("planRiskRelaObj.id", entity.getPlanRiskRelaObj().getId()));
		}
		if(entity.getExecuteEmp() != null){
			criteria.createAlias("executeEmp", "executeEmp");
			criteria.add(Restrictions.eq("executeEmp.id", entity.getExecuteEmp().getId()));
		}
		if(entity.getEmpType()!= null && !"".equals(entity.getEmpType())){
			criteria.add(Restrictions.eq("empType", entity.getEmpType()));
		}
		return criteria.list();
	}
	
	/**  
	* @Title: getExecutionObj  
	* @Description: 根据计划id风险id部门id待办人id人员类型获取执行对象
	* @param planId
	* @param riskId
	* @param deptId
	* @param empId
	* @param empType
	* @return RiskResponseTaskExecution
	* @throws  
	*/
	public RiskResponseTaskExecution getExecutionObj(String planId,String riskId,String deptId,String empId,String empType){
		RiskResponsePlanRiskRela param = new RiskResponsePlanRiskRela();
		param.setPlan(new RiskAssessPlan(planId));
		param.setMajorRisk(new Risk(riskId));
		param.setDept(new SysOrganization(deptId));
		RiskResponsePlanRiskRela planRiskRela = this.riskResponsePlanRiskRelaService.findOne(param);
		Criteria criteria = this.riskResponseTaskExecutionDao.createCriteria();
		if(null != planRiskRela){
			criteria.createAlias("planRiskRelaObj", "planRiskRelaObj");
			criteria.add(Restrictions.eq("planRiskRelaObj.id", planRiskRela.getId()));
		}
		if(!"".equals(empId)){
			criteria.createAlias("executeEmp", "executeEmp");
			criteria.add(Restrictions.eq("executeEmp.id", empId));
		}
		if(!"".equals(empType)){
			criteria.add(Restrictions.eq("empType", empType));
		}
		return (RiskResponseTaskExecution) criteria.uniqueResult();
	}
}
