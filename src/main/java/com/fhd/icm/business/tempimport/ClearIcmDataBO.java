package com.fhd.icm.business.tempimport;

import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.comm.graph.GraphRelaProcessDAO;
import com.fhd.dao.icm.assess.AssessPlanRelaOrgEmpDAO;
import com.fhd.dao.icm.assess.AssessPlanRelaPointDAO;
import com.fhd.dao.icm.assess.AssessPlanRelaProcessDAO;
import com.fhd.dao.icm.assess.AssessPointDAO;
import com.fhd.dao.icm.assess.AssessResultDAO;
import com.fhd.dao.icm.assess.AssessSampleDAO;
import com.fhd.dao.icm.assess.AssessSampleRelaFileDAO;
import com.fhd.dao.icm.assess.MeasureDAO;
import com.fhd.dao.icm.assess.MeasureRelaOrgDAO;
import com.fhd.dao.icm.assess.MeasureRelaRiskDAO;
import com.fhd.dao.icm.icsystem.ConstructPlanRelaStandardDAO;
import com.fhd.dao.icm.icsystem.ConstructPlanRelaStandardEmpDAO;
import com.fhd.dao.icm.icsystem.ConstructRelaProcessDAO;
import com.fhd.dao.icm.icsystem.DiagnosesDAO;
import com.fhd.dao.icm.icsystem.DiagnosesRelaDefectDAO;
import com.fhd.dao.icm.standard.StandardDAO;
import com.fhd.dao.icm.standard.StandardRelaFileDAO;
import com.fhd.dao.icm.standard.StandardRelaOrgDAO;
import com.fhd.dao.icm.standard.StandardRelaProcessureDAO;
import com.fhd.dao.icm.standard.StandardRelaRiskDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.process.ProcessGraphDAO;
import com.fhd.dao.process.ProcessPointDAO;
import com.fhd.dao.process.ProcessPointRelaMeasureDAO;
import com.fhd.dao.process.ProcessPointRelaOrgDAO;
import com.fhd.dao.process.ProcessPointRelaPointSelfDAO;
import com.fhd.dao.process.ProcessPointRelaRiskDAO;
import com.fhd.dao.process.ProcessRelaFileDAO;
import com.fhd.dao.process.ProcessRelaMeasureDAO;
import com.fhd.dao.process.ProcessRelaOrgDAO;
import com.fhd.dao.process.ProcessRelaRiskDAO;
import com.fhd.dao.process.ProcessRelaRuleDAO;
import com.fhd.dao.risk.ProcessAdjustHistoryDAO;
import com.fhd.dao.sys.file.FileUploadDAO;

/**
 * 清理流程数据BO.
 * @author 吴德福
 * @Date 2013-1-5 14:10:32
 */
@Service
public class ClearIcmDataBO {

	@Autowired
	private ProcessPointRelaPointSelfDAO o_processPointRelaPointSelfDAO;
	@Autowired
	private ProcessPointRelaRiskDAO o_processPointRelaRiskDAO;
	@Autowired
	private ProcessPointRelaOrgDAO o_processPointRelaOrgDAO;
	@Autowired
	private ProcessPointDAO o_processpointDAO;
	@Autowired
	private ProcessRelaRiskDAO o_processRelaRiskDAO;
	@Autowired
	private AssessPlanRelaProcessDAO o_assessPlanRelaProcessDAO;
	@Autowired
	private AssessPlanRelaOrgEmpDAO o_assessPlanRelaOrgEmpDAO;
	@Autowired
	private ProcessRelaOrgDAO o_processRelaOrgDAO;
	@Autowired
	private ProcessRelaFileDAO o_processRelaFileDAO;
	@Autowired
	private ProcessGraphDAO o_processGraphDAO;
	@Autowired
	private ProcessRelaRuleDAO o_processRelaRuleDAO;
	@Autowired
	private ProcessAdjustHistoryDAO o_processAdjustHistoryDAO;
	@Autowired
	private GraphRelaProcessDAO o_graphRelaProcessDAO;
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private AssessPointDAO o_assessPointDAO;
	@Autowired
	private MeasureRelaRiskDAO o_measureRelaRiskDAO;
	@Autowired
	private ProcessPointRelaMeasureDAO o_processPointRelaMeasureDAO;
	@Autowired
	private ProcessRelaMeasureDAO o_processRelaMeasureDAO;
	@Autowired
	private MeasureRelaOrgDAO o_measureRelaOrgDAO;
	@Autowired
	private AssessPlanRelaPointDAO o_assessPlanRelaPointDAO;
	@Autowired
	private AssessResultDAO o_assessResultDAO;
	@Autowired
	private AssessSampleRelaFileDAO o_assessSampleRelaFileDAO;
	@Autowired
	private AssessSampleDAO o_assessSampleDAO;
	@Autowired
	private FileUploadDAO o_fileUploadDAO;
	@Autowired
	private MeasureDAO o_measureDAO;
	@Autowired
	private StandardRelaRiskDAO o_standardRelaRiskDAO;
	@Autowired
	private StandardRelaProcessureDAO o_standardRelaProcessureDAO;
	@Autowired
	private StandardRelaFileDAO o_standardRelaFileDAO;
	@Autowired
	private StandardRelaOrgDAO o_standardRelaOrgDAO;
	@Autowired
	private DiagnosesRelaDefectDAO o_diagnosesRelaDefectDAO;
	@Autowired
	private DiagnosesDAO o_diagnosesDAO;
	@Autowired
	private ConstructRelaProcessDAO o_constructRelaProcessDAO;
	@Autowired
	private ConstructPlanRelaStandardEmpDAO o_constructPlanRelaStandardEmpDAO;
	@Autowired
	private ConstructPlanRelaStandardDAO o_constructPlanRelaStandardDAO;
	@Autowired
	private StandardDAO o_standardDAO;
	
	/**
	 * 根据公司id流程基础数据.
	 * @author 吴德福
	 * @param companyId
	 */
	@Transactional
	public void removeProcessData(String companyId){
		//根据公司id删除控制标准及相关数据.
		this.removeControlStandardAndRelationData(companyId);
		//根据公司id删除控制措施及相关数据.
		this.removeControlMeasureAndRelationData(companyId);
		//根据公司id删除流程及相关数据.
		this.removeProcessAndRelationData(companyId);
	}
	
	/**
	 * 根据公司id删除控制措施及相关数据(12/2).
	 * @author 吴德福
	 * @param companyId
	 */
	@Transactional
	public void removeControlMeasureAndRelationData(String companyId){
		String sql = "";
		SQLQuery sqlQuery;
		
		//1.抽样测试评价点表
		sql = "delete from t_ca_assessment_point where measure_id in (select id from t_con_control_measure where company_id = :companyId)";
		sqlQuery = o_assessPointDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//2.控制措施相关风险表
		sql = "delete from t_con_measure_rela_risk where control_measure_id in (select id from t_con_control_measure where company_id = :companyId)";
		sqlQuery = o_measureRelaRiskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//3.控制措施相关流程节点表
		sql = "delete from t_ic_measure_rela_control_poin where control_measure_id in (select id from t_con_control_measure where company_id = :companyId)";
		sqlQuery = o_processPointRelaMeasureDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//4.控制措施相关流程表
		sql = "delete from t_ic_measure_rela_processure where control_measure_id in (select id from t_con_control_measure where company_id = :companyId)";
		sqlQuery = o_processRelaMeasureDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//5.控制措施相关部门/人员表
		sql = "delete from t_con_measure_rela_org where control_measure_id in (select id from t_con_control_measure where company_id = :companyId)";
		sqlQuery = o_measureRelaOrgDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//6.缺陷关联评估结果表
    	sql = "delete from t_ca_defect_assessment where measure_id in (select id from t_con_control_measure where company_id = :companyId)";
		sqlQuery = o_measureRelaOrgDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//7.评价样本关联文件表
    	sql = "delete from t_sys_file where id in (select file_id from t_ca_sample_rela_file where sample_id in (select id from t_ca_sample where assessment_point_id in (select id from t_ca_assessment_result where measure_id in (select id from t_con_control_measure where company_id = :companyId))))";
    	sqlQuery = o_fileUploadDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//8.评价样本相关附件表
    	sql = "delete from t_ca_sample_rela_file where sample_id in (select id from t_ca_sample where assessment_point_id in (select id from t_ca_assessment_result where measure_id in (select id from t_con_control_measure where company_id = :companyId)))";
    	sqlQuery = o_assessSampleRelaFileDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
	
    	//9.评价样本表
    	sql = "delete from t_ca_sample where assessment_point_id in (select id from t_ca_assessment_result where measure_id in (select id from t_con_control_measure where company_id = :companyId))";
    	sqlQuery = o_assessSampleDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//10.评价点评价结果表
    	sql = "delete from t_ca_assessment_result where measure_id in (select id from t_con_control_measure where company_id = :companyId)";
    	sqlQuery = o_assessResultDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//11.评价计划评价点范围设置表
    	sql = "delete from t_ca_plan_assessment_point where measure_id in (select id from t_con_control_measure where company_id = :companyId)";
    	sqlQuery = o_assessPlanRelaPointDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//12.控制措施表
		sql = "delete from t_con_control_measure where company_id = :companyId ";
		sqlQuery = o_measureDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	/*
    	 * 数据库还有2张表没有数据，不知道是否使用，若使用，也需要，表名如下：
    	 * t_rm_risk_measure和t_st_deal_measure
    	 */
	}
	
	/**
	 * 根据公司id控制标准及相关数据(10/1).
	 * @author 吴德福
	 * @param companyId
	 */
	@Transactional
	public void removeControlStandardAndRelationData(String companyId){
		String sql = "";
		SQLQuery sqlQuery;
    	
    	//1.体系建设计划关联部门表
    	sql = "delete from t_ca_const_plan_rela_process where plan_rela_standard_id in (select id from t_ca_const_plan_rela_standard where standard_id in (select id from t_ic_control_standard where company_id = :companyId))";
    	sqlQuery = o_constructRelaProcessDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//2.计划标准部门人员
    	sql = "delete from t_ca_const_plan_rela_st_emp where plan_rela_standard_id in (select id from t_ca_const_plan_rela_standard where standard_id in (select id from t_ic_control_standard where company_id = :companyId))";
    	sqlQuery = o_constructPlanRelaStandardEmpDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//3.合规诊断缺陷表T_CA_DIAGNOSES_DEFECT
    	sql = "delete from t_ca_diagnoses_defect where diagnoses_id in (select id from t_ca_compliance_diagnoses where plan_rela_standard_id in (select id from t_ca_const_plan_rela_standard where standard_id in (select id from t_ic_control_standard where company_id = :companyId)))";
    	sqlQuery = o_diagnosesRelaDefectDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//4.合规诊断表
    	sql = "delete from t_ca_compliance_diagnoses where plan_rela_standard_id in (select id from t_ca_const_plan_rela_standard where standard_id in (select id from t_ic_control_standard where company_id = :companyId))";
    	sqlQuery = o_diagnosesDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//5.控制标准相关体系建设计划表
		sql = "delete from t_ca_const_plan_rela_standard where standard_id in (select id from t_ic_control_standard where company_id = :companyId)";
		sqlQuery = o_constructPlanRelaStandardDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//6.控制标准相关文件
		sql = "delete from t_ic_standard_rela_file where standard_id in (select id from t_ic_control_standard where company_id = :companyId)";
		sqlQuery = o_standardRelaFileDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
		//7.控制标准相关风险
		sql = "delete from t_ic_standard_rela_risk where standard_id in (select id from t_ic_control_standard where company_id = :companyId)";
		sqlQuery = o_standardRelaRiskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//8.控制标准相关流程
		sql = "delete from t_ic_standard_rela_processure where control_standard_id in (select id from t_ic_control_standard where company_id = :companyId)";
		sqlQuery = o_standardRelaProcessureDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//9.控制标准相关部门
		sql = "delete from t_ic_standard_rela_org where control_standard_id in (select id from t_ic_control_standard where company_id = :companyId)";
		sqlQuery = o_standardRelaOrgDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//10.控制标准
		sql = "delete from t_ic_control_standard where company_id = :companyId ";
		sqlQuery = o_standardDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	/*
    	 * 数据库还有1张表没有数据，不知道是否使用，若使用，也需要，表名如下：
    	 * t_ic_standard_rela_point
    	 */
	}
	
	/**
	 * 根据公司id流程、流程节点及相关数据(18/5/9).
	 * @author 吴德福
	 * @param companyId
	 */
	@Transactional
	public void removeProcessAndRelationData(String companyId){
		String sql = "";
		SQLQuery sqlQuery;
		
		/***********流程节点*************/
		//1.控制措施相关流程节点
		sql = "delete from t_ic_measure_rela_control_poin where control_point_id in (select id from t_ic_control_point where processure_id in (select id from t_ic_processure where company_id = :companyId))";
		sqlQuery = o_processPointRelaMeasureDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
		
    	//2.流程节点自关联
		sql = "delete from t_ic_control_point_relevance where control_point_id in (select id from t_ic_control_point where processure_id in (select id from t_ic_processure where company_id = :companyId)) or previous_control_point_id in (select id from t_ic_control_point where processure_id in (select id from t_ic_processure where company_id = :companyId))";
		sqlQuery = o_processPointRelaPointSelfDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//3.流程节点相关风险
		sql = "delete from t_ic_control_point_rela_risk where control_point_id in (select id from t_ic_control_point where processure_id in (select id from t_ic_processure where company_id = :companyId))";
		sqlQuery = o_processPointRelaRiskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//4.流程节点相关部门/人员
		sql = "delete from t_ic_control_point_rela_org where control_point_id in (select id from t_ic_control_point where processure_id in (select id from t_ic_processure where company_id = :companyId))";
		sqlQuery = o_processPointRelaOrgDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	    	
    	//5.流程节点
		sql = "delete from t_ic_control_point where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processpointDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	/***********流程*************/
    	
    	//1.评价样本关联文件表
    	sql = "delete from t_sys_file where id in (select file_id from t_ca_sample_rela_file where sample_id in (select id from t_ca_sample where assessment_point_id in (select id from t_ca_assessment_result where processure_id in (select id from t_ic_processure where company_id = :companyId))))";
    	sqlQuery = o_fileUploadDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//2.评价样本相关附件表
    	sql = "delete from t_ca_sample_rela_file where sample_id in (select id from t_ca_sample where assessment_point_id in (select id from t_ca_assessment_result where processure_id in (select id from t_ic_processure where company_id = :companyId)))";
    	sqlQuery = o_assessSampleRelaFileDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
	
    	//3.评价样本表
    	sql = "delete from t_ca_sample where assessment_point_id in (select id from t_ca_assessment_result where processure_id in (select id from t_ic_processure where company_id = :companyId))";
    	sqlQuery = o_assessSampleDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//4.评价点评价结果表
    	sql = "delete from t_ca_assessment_result where processure_id in (select id from t_ic_processure where company_id = :companyId)";
    	sqlQuery = o_assessResultDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//5.评价计划评价点范围设置表
    	sql = "delete from t_ca_plan_assessment_point where processure_id in (select id from t_ic_processure where company_id = :companyId)";
    	sqlQuery = o_assessPlanRelaPointDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//6.评价计划流程范围表
    	sql = "delete from t_ca_plan_processure where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_assessPlanRelaProcessDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//7.流评价计划流程范围涉及部门人员表
    	sql = "delete from t_ca_plan_processure_org_emp where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_assessPlanRelaOrgEmpDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//8.抽样测试评价点和穿行测试评价点
		sql = "delete from t_ca_assessment_point where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_assessPointDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//9.流程相关图表
    	sql = "delete from t_com_graph_rela_process where process_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processGraphDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	    	
    	//10.流程相关控制措施
		sql = "delete from t_ic_measure_rela_processure where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processRelaMeasureDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//11.流程相关文件
    	sql = "delete from t_ic_processure_rela_file where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processRelaFileDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//12.流程节点相关部门/人员
		sql = "delete from t_ic_processure_rela_org where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processRelaOrgDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//13.流程相关规章制度
    	sql = "delete from t_ic_processure_rela_rule where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processRelaRuleDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//14.流程相关控制标准
		sql = "delete from t_ic_standard_rela_processure where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_standardRelaProcessureDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//15.流程相关图例
    	sql = "delete from t_processure_graph where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_graphRelaProcessDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//16.流程相关风险
		sql = "delete from t_processure_risk_processure where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processRelaRiskDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	    	
    	//17.流程角度风险评估结果表
    	sql = "delete from t_rm_processure_adjust_history where processure_id in (select id from t_ic_processure where company_id = :companyId)";
		sqlQuery = o_processAdjustHistoryDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	//18.流程
		sql = "delete from t_ic_processure where company_id = :companyId ";
		sqlQuery = o_processDAO.createSQLQuery(sql);
    	sqlQuery.setString("companyId", companyId);
    	sqlQuery.executeUpdate();
    	
    	/*
    	 * 数据库还有9张表没有数据，不知道是否使用，若使用，也需要，表名如下：
    	 * t_processure_processure、t_processure_org_processure
		 * t_ic_processure_copy、t_processure_operation
		 * t_processure_kpi_processure、t_ca_defect_processure
		 * t_rectify_improve_processure、t_ca_const_plan_rela_process
		 * t_ic_kpi_processure
    	 */
	}
}