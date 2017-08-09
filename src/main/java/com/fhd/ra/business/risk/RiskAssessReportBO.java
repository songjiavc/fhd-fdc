package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.JoinType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.report.ReportInfomationBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.RiskAdjustHistoryDAO;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.dao.risk.RiskDAO;
import com.fhd.dao.risk.RiskOrgDAO;
import com.fhd.entity.comm.report.ReportInfomation;
import com.fhd.entity.response.Solution;
import com.fhd.entity.response.SolutionRelaOrg;
import com.fhd.entity.response.SolutionRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskAdjustHistory;
import com.fhd.entity.risk.RiskOrg;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.jfreechar.FHDJfreeChar;
import com.fhd.fdc.utils.jfreechar.JFCBaseProper;
import com.fhd.fdc.utils.jfreechar.TestImageBinary;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.risk.report.DocumentHandler;
import com.fhd.ra.interfaces.response.IResponseOutSiteBO;
import com.fhd.sys.business.file.FileUploadBO;

/**
 * @decs 风险评估报告数据提供类
 * @author 郑军祥
 *
 */
@Service
public class RiskAssessReportBO {
	
	@Autowired
	private RiskDAO o_riskDAO;
	
	@Autowired
	private RiskAdjustHistoryDAO o_riskAdjustHistoryDAO;
	
	@Autowired
	private RiskOrgDAO o_riskOrgDAO;
	
	@Autowired
	private RiskAssessPlanDAO o_riskAssessPlanDAO;
	
	@Autowired
	private DocumentHandler o_documentHandler;
	
	@Autowired
	private RiskCmpBO o_riskCmpBO;
	
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;
	
	@Autowired
	private IResponseOutSiteBO o_responseListBO;
	
	//报告
	@Autowired
	private ReportInfomationBO o_reportInfomationBO;
	
	@Autowired
	private FileUploadBO o_fileUploadBO;
	
	/**
	 * 上传日常风险报告
	 * @param id		附件id
	 * @param disName   报告名称
	 * @param type		报告类型
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public void uploadRiskReport(String id,String disName,String type) throws Exception{
		String companyId = UserContext.getUser().getCompanyid();
		String empId = UserContext.getUser().getEmpid();
		
		//2.将生成的内容插入到报告表中
		ReportInfomation report = new ReportInfomation();
		String makeId = Identities.uuid();
		report.setId(makeId);
		report.setReportCode(makeId);
		report.setReportName(disName);
		//报告类型
		DictEntry reportType = new DictEntry();
		reportType.setId(type);
		report.setReportType(reportType);
		//所在公司
		SysOrganization company = new SysOrganization();
		company.setId(companyId);
		report.setCompany(company);
		//报告附件
		FileUploadEntity file = new FileUploadEntity();
		file.setId(id);
		report.setFile(file);
		report.setCreateTime(new Date());
		//创建人
		SysEmployee creator = new SysEmployee();
		creator.setId(empId);
		report.setCreateBy(creator);
		o_reportInfomationBO.saveRiskAssessReport(report);
	}
	
	/**
	 * 获取风险评估报告的数据
	 * @author zhengjunxiang
	 * @param month    读取风险报告的月份
	 * @param realName 报告创建人
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public byte[] generateRiskReport(String companyId,int month,String empId) throws Exception{
		String templatePath = "/com/fhd/ra/business/risk/report";
		String templateName = "report1.ftl";
		Map<String,Object> dataMap = this.getData(companyId,month);
		byte[] contents = o_documentHandler.createDoc(templatePath, templateName, dataMap);
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String reportName = year+"年"+month+"月"+day + "日风险管理报告";
		
		//1.将生成的内容插入到文件表中
		String fileId = Identities.uuid();
		FileUploadEntity fileUpload  = new FileUploadEntity();
		fileUpload.setId(fileId);
		fileUpload.setContents(contents);
		fileUpload.setFileSize("0");
		fileUpload.setNewFileName(reportName);
		fileUpload.setOldFileName(reportName);
		fileUpload.setUploadTime(new Date());
		o_fileUploadBO.updateFile(fileUpload);
		
		//2.将生成的内容插入到报告表中
		ReportInfomation report = new ReportInfomation();
		String makeId = Identities.uuid();
		report.setId(makeId);
		report.setReportCode(makeId);
		report.setReportName(reportName);
		//报告类型
		DictEntry reportType = new DictEntry();
		reportType.setId(Contents.REPORT_RISK_MANAGE);
		report.setReportType(reportType);
		//所在公司
		SysOrganization company = new SysOrganization();
		company.setId(companyId);
		report.setCompany(company);
		//报告附件
		FileUploadEntity file = new FileUploadEntity();
		file.setId(fileId);
		report.setFile(file);
		report.setCreateTime(new Date());
		//创建人
		SysEmployee creator = new SysEmployee();
		creator.setId(empId);
		report.setCreateBy(creator);
		o_reportInfomationBO.saveRiskAssessReport(report);
		
		return contents;
	}
	
	/**
	 * 获取风险评估报告的数据
	 * @author zhengjunxiang
	 * @param month    读取风险报告的月份
	 * @param realName 报告创建人
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public byte[] generateRiskAssessReport(String companyId, int month, String empId, String assessPlanId) throws Exception{
		String templatePath = "/com/fhd/ra/business/risk/report";
		String templateName = "assessreport.ftl";
		Map<String,Object> dataMap = this.getRiskAssessData(companyId,month,assessPlanId);
		byte[] contents = o_documentHandler.createDoc(templatePath, templateName, dataMap);

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String planName = o_riskAssessPlanDAO.get(assessPlanId).getPlanName();
		String reportName = year+"年"+month+"月"+day +"日" + "风险评估报告("+planName+")";
		
		//1.将生成的内容插入到文件表中
		String fileId = Identities.uuid();
		FileUploadEntity fileUpload  = new FileUploadEntity();
		fileUpload.setId(fileId);
		fileUpload.setContents(contents);
		fileUpload.setFileSize("0");
		fileUpload.setNewFileName(reportName);
		fileUpload.setOldFileName(reportName);
		fileUpload.setUploadTime(new Date());
		o_fileUploadBO.updateFile(fileUpload);
		
		//2.将生成的内容插入到报告表中
		ReportInfomation report = new ReportInfomation();
		String makeId = Identities.uuid();
		report.setId(makeId);
		report.setReportCode(makeId);
		report.setReportName(reportName);
		//报告类型
		DictEntry reportType = new DictEntry();
		reportType.setId(Contents.REPORT_RISK_ASSESS);
		report.setReportType(reportType);
		//所在公司
		SysOrganization company = new SysOrganization();
		company.setId(companyId);
		report.setCompany(company);
		//报告附件
		FileUploadEntity file = new FileUploadEntity();
		file.setId(fileId);
		report.setFile(file);
		report.setCreateTime(new Date());
		//创建人
		SysEmployee creator = new SysEmployee();
		creator.setId(empId);
		report.setCreateBy(creator);
		o_reportInfomationBO.saveRiskAssessReport(report);
		
		return contents;
	}
	
	/**
	 * 获取风险评估报告的数据
	 * @author zhengjunxiang
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getData(String companyId,int month) throws Exception{
//		  String companyId = "xd00";
//		  int month = 9;
		  Map<String,Object> dataMap = new HashMap<String,Object>();
		  //1.2公司十大风险
		  List<Map<String,Object>> riskCatalogTable = this.findTop10RiskTable(companyId);
		  dataMap.put("riskCatalogTable", riskCatalogTable);
		  
		  //1.3新增风险事件列表
		  List<Map<String,Object>> newAddRiskTable = this.findLatestRisk(companyId,month);
		  dataMap.put("newAddRiskTable", newAddRiskTable);
		  
		  //1.4公司风险清单
		  List<Map<String,Object>> riskCheckListTable = this.findRiskEventByCompanyId(companyId);
		  dataMap.put("riskCheckListTable", riskCheckListTable);
		  
		  //1.1风险概况
		  dataMap.put("allRiskNum", riskCheckListTable.size());
		  dataMap.put("newAddRiskNum", newAddRiskTable.size());
		  //dataMap.put("stateChangeRiskNum", "3");
		  
		  //1.5风险分类图
//		  DefaultPieDataset pieDataset = new DefaultPieDataset();	
//		  pieDataset.setValue((Comparable)"战略风险",(Number)3);
//		  pieDataset.setValue((Comparable)"财务风险",(Number)4);
//		  pieDataset.setValue((Comparable)"法律风险",(Number)5);
//		  JFCBaseProper jfcbp = new JFCBaseProper();
//		  jfcbp.setTitle("风险分类图");
//		  Map<String,Object> riskCatalogChartMap = FHDJfreeChar.createPieChar(jfcbp, pieDataset);
//		  TestImageBinary image = new TestImageBinary();
//		  byte[] data = (byte[])riskCatalogChartMap.get("image");
//		  Base64 encoder = new Base64();
//		  String imageStr = encoder.encodeBase64String(data);//返回Base64编码过的字节数组字符串 
//		  dataMap.put("riskCatalogChart", imageStr);
		  
		  Map deparetRiskListMap = this.findDepartRiskList(companyId);
		  //2.1部门风险概况
		  List<Map<String,Object>> departmentTable = (List<Map<String,Object>>)deparetRiskListMap.get("orgList");
		  dataMap.put("departmentTable", departmentTable);
		  
		  
		  //2.2部门风险列表
		  List<Map<String,Object>> riskEventTable = (List<Map<String,Object>>)deparetRiskListMap.get("orgRiskList");
		  dataMap.put("riskEventTable", riskEventTable);
		  
		  //3.1风险应对措施
//		  List<Map<String,Object>> riskAnswerTable=new ArrayList<Map<String,Object>>();
//		  for(int i=0;i<25;i++)
//		  {
//			  Map<String,Object> map = new HashMap<String,Object>();
//			  //序号
//			  map.put("num", i+1);
//			  //风险分类
//			  map.put("catalog", "采购计划制定风险"+(i+1));
//			  //风险名称
//			  map.put("name", "由于一些原材料及零件的检测设备不足和基础条件较差，验收可能出现差错，影响产品质量.");
//			  //应对措施
//			  map.put("answer", "找其他供应商"+(i+1));
//			  //责任部门
//			  map.put("respDept", "采购部"+(i+1));
//			  //责任人
//			  map.put("respEmp", "张红"+(i+1));
//			  //完成时间
//			  map.put("finishDate", "2013-10-01");
//			  riskAnswerTable.add(map);
//		  }
		  List<Map<String,Object>> riskAnswerTable = this.findRiskSolutions(companyId);
		  dataMap.put("riskAnswerTable", riskAnswerTable);
		  
		  return dataMap;
	  }
	
	/**
	 * 查询公司10大风险
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Risk> findTop10RiskCatalog(String companyId){
  		List<Risk> list = new ArrayList<Risk>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT parent.*")
		.append(" FROM t_rm_risks r")
		.append(" INNER JOIN t_rm_risks parent on parent.id = r.parent_id")
		.append(" LEFT JOIN t_rm_risk_adjust_history h on h.risk_id  = parent.id and h.is_latest = '1'")
		.append(" WHERE r.is_risk_class = 're' and r.company_id = :companyId and r.delete_estatus = '1'")
		.append(" GROUP BY r.parent_id order by h.RISK_STATUS desc");
		
		Session session = o_riskDAO.getSessionFactory().openSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql.toString()).addEntity(Risk.class);
		sqlQuery.setParameter("companyId", companyId);
// 		sqlQuery.setFirstResult(0);
// 		sqlQuery.setMaxResults(10);
 		list = sqlQuery.list();
  		
        return list;
	}
	
	/**
	 * 查询公司10大风险,为模板提供数据
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findTop10RiskTable(String companyId){
		List<Map<String,Object>> riskCatalogTable=new ArrayList<Map<String,Object>>();
		
		//获取十大直接关联的风险分类
		List<Risk> riskList = this.findTop10RiskCatalog(companyId);
		String[] ids = new String[riskList.size()];	//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
		for(int i=0;i<riskList.size();i++){
			String id  = riskList.get(i).getId();
			ids[i] = id;
			idList.add(id);
		}
		
		//获取风险分类的责任主体和辅助部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
		Map<String,Object> respDeptMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relaDeptMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//相关部门
		
		//获取风险状态和趋势
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		Map<String,Object> trendMap = (Map<String,Object>)historyMap.get("trendMap");	//趋势
		
		//组装显示列表
		for(int i=0;i<riskList.size();i++){
			Risk r  = riskList.get(i);
			String id = r.getId();
			Map<String,Object> riskMap = new HashMap<String,Object>();
			  //序号
			  riskMap.put("num", i+1);
			  //风险大类
			  riskMap.put("name", r.getName());
			  //责任主体
			  riskMap.put("respDept", respDeptMap.get(id)==null?"":respDeptMap.get(id).toString());
			  //辅助部门
			  riskMap.put("relaDept", relaDeptMap.get(id)==null?"":relaDeptMap.get(id).toString());
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
			  //趋势
			  String trend = "";
			  if(trendMap!=null && trendMap.get(id)!=null){
				  trend = trendMap.get(id).toString();
			  }
			  if(trend.equals("icon-ibm-icon-trend-rising-positive")){
				  trend = "up";
			  }else if(trend.equals("icon-ibm-icon-trend-neutral-null")){
				  trend = "flat";
			  }else if(trend.equals("icon-ibm-icon-trend-falling-negative")){
				  trend = "down";
			  }else{
				  trend = "";
			  }
			  riskMap.put("trend", trend);
			  riskCatalogTable.add(riskMap);
		}
		
		return riskCatalogTable;
	}
	
	/**
	 * 查询最新风险,为模板提供数据
	 * 这块使用了数据库的时间函数，导致sql不能跨数据库
	 * @author zhengjunxiang
	 * @month  几月份
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findLatestRisk(String companyId,int month){
  		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		StringBuffer sql = new StringBuffer();
		//得到最近30天的开始时间和结束时间
		Calendar calendar = Calendar.getInstance();
		Date endTime = calendar.getTime();
		String endTimeStr = DateUtils.formatDate(endTime, "yyyy-MM-dd HH:mm:ss");
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		Date startTime = calendar.getTime();
		String startTimeStr = DateUtils.formatDate(startTime, "yyyy-MM-dd HH:mm:ss");
		sql.append("SELECT r.id,parent.risk_name,r.risk_name pName")
		.append(" FROM t_rm_risks r,t_rm_risks parent")
		.append(" WHERE r.parent_id = parent.id and r.is_risk_class = 're' and r.company_id = :companyId and r.delete_estatus = '1'")
		//.append(" and MONTH(r.last_modify_time)="+month);
		.append(" and (r.last_modify_time between :startTimeStr and :endTimeStr)");
		Session session = o_riskDAO.getSessionFactory().openSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
		sqlQuery.setParameter("companyId", companyId);
		sqlQuery.setParameter("startTimeStr", startTimeStr);
		sqlQuery.setParameter("endTimeStr", endTimeStr);
		List<Object[]> objList = sqlQuery.list();
		String[] ids = new String[objList.size()];		//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
		for(int i=0;i<objList.size();i++){
			Object[] o = objList.get(i);
  			String id = o[0].toString();
  			ids[i] = id;
  			idList.add(id);
			Map m = new HashMap();
			m.put("id", id);
  			m.put("belongRisk", o[1]==null?"":o[1].toString());
  			m.put("name", o[2]==null?"":o[2].toString());
  			list.add(m);
  		}
		
		//获取风险分类的责任主体和辅助部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
		Map<String,Object> respDeptMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relaDeptMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//相关部门
		
		//获取风险状态
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		
		//组装显示列表
		for(int i=0;i<list.size();i++){
			  Map riskMap  = list.get(i);
			  String id = riskMap.get("id").toString();
			  //序号
			  riskMap.put("num", i+1);
			  //责任主体
			  riskMap.put("respDept", respDeptMap.get(id)==null?"":respDeptMap.get(id).toString());
			  //辅助部门
			  riskMap.put("relaDept", relaDeptMap.get(id)==null?"":relaDeptMap.get(id).toString());
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
		}
		
        return list;
	}
	
	/**
	 * 查询部门风险,所有风险事件按照部门分组,为模板提供数据
	 * list<部门map>,部门map<部门id，部门下风险事件列表Map>
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findDepartRiskList(String companyId){
  		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT o.id,o.org_name,r.id riskId,r.risk_name")
		.append(" FROM t_rm_risk_org ro,t_rm_risks r,t_sys_organization o")
		.append(" WHERE ro.risk_id = r.id and ro.org_id = o.id")
		.append(" and r.company_id = :companyId and o.company_id=:companyId")
		.append(" ORDER BY o.id_seq");	//按照一个部门（子部门）这样顺序查询
		Session session = o_riskDAO.getSessionFactory().openSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
		sqlQuery.setParameter("companyId", companyId);
		List<Object[]> objList = sqlQuery.list();
		String[] ids = new String[objList.size()];		//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
		for(int i=0;i<objList.size();i++){
			Object[] o = objList.get(i);
			String deptId = o[0].toString();
  			String riskId = o[2].toString();
  			ids[i] = riskId;
  			idList.add(riskId);
			Map m = new HashMap();
			m.put("deptId", deptId);
  			m.put("department", o[1]==null?"":o[1].toString());
  			m.put("riskId", riskId);
  			m.put("name", o[3]==null?"":o[3].toString());
  			list.add(m);
  		}
		
		//获取风险状态和趋势
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		Map<String,Object> trendMap = (Map<String,Object>)historyMap.get("trendMap");	//趋势
		
		//1.组装显示列表,同时也构建部门风险概况
		Map<String,Object> orgMap = new HashMap<String,Object>();
		List<Map<String,Object>> orgList = new ArrayList<Map<String,Object>>();	//部门列表
		for(int i=0;i<list.size();i++){
			  Map riskMap  = list.get(i);
			  String id = riskMap.get("riskId").toString();
			  String deptId = riskMap.get("deptId").toString();
			  //序号
			  riskMap.put("num", i+1);
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
			  //趋势
			  String trend = "";
			  if(trendMap!=null && trendMap.get(id)!=null){
				  trend = trendMap.get(id).toString();
			  }
			  if(trend.equals("icon-ibm-icon-trend-rising-positive")){
				  trend = "up";
			  }else if(trend.equals("icon-ibm-icon-trend-neutral-null")){
				  trend = "flat";
			  }else if(trend.equals("icon-ibm-icon-trend-falling-negative")){
				  trend = "down";
			  }else{
				  trend = "";
			  }
			  riskMap.put("trend", trend);
			  
			  //构建部门分析概况
			  if(orgMap.containsKey(deptId)){
				  List<Map<String,Object>> riskList = (List<Map<String,Object>>)orgMap.get(deptId);
				  riskList.add(riskMap);
				  orgMap.put(deptId, riskList);
			  }else{
				  List<Map<String,Object>> riskList = new ArrayList<Map<String,Object>>();
				  riskList.add(riskMap);
				  orgMap.put(deptId, riskList);
				  //部门列表
				  Map<String,Object> org = new HashMap<String,Object>();
				  org.put("id", deptId);
				  org.put("name", riskMap.get("department")==null?"":riskMap.get("department").toString());//记录部门的名称
				  orgList.add(org);
			  }
		}
		
		//2.组装部门风险概况显示列表,map是无序的,所以不能顺序遍历map,而是List
		int orgNum = 1;
		for(Map org : orgList){ 
		    String orgId = org.get("id").toString(); 
		    List<Map<String,Object>> subList = (List<Map<String,Object>>)orgMap.get(orgId);
		    //重要风险
		    int importRiskNum = 0;
			//关注风险
			int focusRiskNum = 0;
			//安全风险
			int safeRiskNum = 0;
		    for(Map<String,Object> r : subList){
		    	String status = r.get("status").toString();
		    	if(status.equals("red")){
		    		importRiskNum++;
		    	}else if(status.equals("yellow")){
		    		focusRiskNum++;
		    	}else if(status.equals("green")){
		    		safeRiskNum++;
		    	}else{}
		    }
		    //序号
		    org.put("num", orgNum++);
		    org.put("importRiskNum", importRiskNum);
		    org.put("focusRiskNum", focusRiskNum);
		    org.put("safeRiskNum", safeRiskNum);
		} 
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orgList", orgList);
		map.put("orgRiskList", list);
        return map;
	}
	
	/**
	 * 查询风险应对方案,所有应对方案按照风险事件分组,为模板提供数据
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findRiskSolutions(String companyId){
  		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
  		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r.id,r.risk_name,parent.risk_name")
		.append(" FROM t_rm_risks r,t_rm_risks parent")
		.append(" WHERE r.parent_id = parent.id and r.is_risk_class = 're' and r.company_id = :companyId and r.delete_estatus = '1'")
		.append(" ORDER BY r.parent_id");
		
		Session session = o_riskDAO.getSessionFactory().openSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
		sqlQuery.setParameter("companyId", companyId);
 		List<Object[]> objList = sqlQuery.list();
 		List<String> idList = new ArrayList<String>();
 		for(int i=0;i<objList.size();i++){
 			Object[] o = objList.get(i);
 			idList.add(o[0].toString());
 		}
// 		idList.clear();
// 		idList.add("XD00ZL010101");
// 		idList.add("SYLCR37");
 		//得到风险事件的应对方案
		List<SolutionRelaRisk> solutionList = o_responseListBO.findSolutionsByRisk(idList);
 		int num = 1;
 		for(SolutionRelaRisk sr : solutionList){
 			Risk r = sr.getRisk();
 			Solution s = sr.getSolution();
			Map<String,Object> m = new HashMap<String,Object>();
			//序号
		    m.put("num", num++);
			m.put("id", sr.getId());
  			m.put("catalog", r.getParent()==null?"":r.getParent().getName());
  			m.put("name", r.getName());
  		    m.put("answer", s.getSolutionName());
  		    Set<SolutionRelaOrg> solorg = s.getSolutionRelaOrg();//嵌套查询,拼接语句
  		    String respDept = ""; //责任部门
  		    String respEmp = ""; //责任人
  		    for(SolutionRelaOrg so : solorg){
  		    	if(so.getType().equals(Contents.ORG_RESPONSIBILITY)){
  		    		respDept += respDept + so.getOrg().getOrgname() + ",";
  		    	}
  		    	if(so.getType().equals(Contents.EMP_RESPONSIBILITY)){
  		    		respEmp += respEmp + so.getEmp().getEmpname() + ",";
  		    	}
  		    }
  		    if(!respDept.equals("")){
  		    	respDept = respDept.substring(0,respDept.length()-1);
  		    }
  		    if(!respEmp.equals("")){
  		    	respEmp = respEmp.substring(0,respEmp.length()-1);
		    }
  		    m.put("respDept", respDept); //责任部门
  		    m.put("respEmp", respEmp);	//责任人
  		    m.put("finishDate", s.getExpectEndTime().toLocaleString());
  			list.add(m);
			}
  		return list;
	}
	
	/**
	 * 查询公司下风险事件，为模板提供数据
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findRiskEventByCompanyId(String companyId){
  		List<Map<String,Object>> riskList = new ArrayList<Map<String,Object>>();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT r.id,r.risk_name,parent.risk_name pName")
		.append(" FROM t_rm_risks r,t_rm_risks parent")
		.append(" WHERE r.parent_id = parent.id and r.is_risk_class = 're' and r.company_id = :companyId and r.delete_estatus = '1'")
		.append(" ORDER BY r.parent_id,r.last_modify_time desc");
		
		Session session = o_riskDAO.getSessionFactory().openSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
		sqlQuery.setParameter("companyId", companyId);
 		List<Object[]> objList = sqlQuery.list();
 		
 		String[] ids = new String[objList.size()];		//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
 		for(int i=0;i<objList.size();i++){
			Object[] o = objList.get(i);
  			String id = o[0].toString();
  			ids[i]=id;
  			idList.add(id);
			Map m = new HashMap();
			//序号
		    m.put("num", i+1);
			m.put("id", id);
  			m.put("catalog", o[2]==null?"":o[2].toString());
  			m.put("name", o[1]==null?"":o[1].toString());
  			riskList.add(m);
  		}
 		
 		//获取风险分类的责任主体和辅助部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
		Map<String,Object> respDeptMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relaDeptMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//相关部门
		
		//获取风险状态和趋势
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		Map<String,Object> trendMap = (Map<String,Object>)historyMap.get("trendMap");	//趋势
		
		//组装显示列表
		for(int i=0;i<riskList.size();i++){
			  Map riskMap  = riskList.get(i);
			  String id = riskMap.get("id").toString();
			  //责任主体
			  riskMap.put("respDept", respDeptMap.get(id)==null?"":respDeptMap.get(id).toString());
			  //辅助部门
			  riskMap.put("relaDept", relaDeptMap.get(id)==null?"":relaDeptMap.get(id).toString());
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
			  //趋势
			  String trend = "";
			  if(trendMap!=null && trendMap.get(id)!=null){
				  trend = trendMap.get(id).toString();
			  }
			  if(trend.equals("icon-ibm-icon-trend-rising-positive")){
				  trend = "up";
			  }else if(trend.equals("icon-ibm-icon-trend-neutral-null")){
				  trend = "flat";
			  }else if(trend.equals("icon-ibm-icon-trend-falling-negative")){
				  trend = "down";
			  }else{
				  trend = "";
			  }
			  riskMap.put("trend", trend);
		}
		
        return riskList;
	}
	
	/**
	 * 获取风险评估报告的数据
	 * @author zhengjunxiang
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getRiskAssessData(String companyId, int month, String assessPlanId) throws Exception{
		  Map<String,Object> dataMap = new HashMap<String,Object>();
		  //1.2公司十大风险
		  List<Map<String,Object>> riskCatalogTable = this.findTop10RiskTableByPlanId(assessPlanId);
		  dataMap.put("riskCatalogTable", riskCatalogTable);
		  
		  //1.3新增风险事件列表
		  List<Map<String,Object>> newAddRiskTable = this.findLatestRiskByPlanId(companyId,month,assessPlanId);
		  dataMap.put("newAddRiskTable", newAddRiskTable);
		  
		  //1.4公司风险清单
		  List<Map<String,Object>> riskCheckListTable = this.findRiskEventByPlanId(companyId,assessPlanId);
		  dataMap.put("riskCheckListTable", riskCheckListTable);
		  
		  //1.1风险概况
		  dataMap.put("allRiskNum", riskCheckListTable.size());
		  dataMap.put("newAddRiskNum", newAddRiskTable.size());

		  Map deparetRiskListMap = this.findDepartRiskListByPlanId(companyId,assessPlanId);
		  //2.1部门风险概况
		  List<Map<String,Object>> departmentTable = (List<Map<String,Object>>)deparetRiskListMap.get("orgList");
		  dataMap.put("departmentTable", departmentTable);
		  
		  
		  //2.2部门风险列表
		  List<Map<String,Object>> riskEventTable = (List<Map<String,Object>>)deparetRiskListMap.get("orgRiskList");
		  dataMap.put("riskEventTable", riskEventTable);

		  List<Map<String,Object>> riskAnswerTable = this.findRiskSolutionsByPlanId(companyId,assessPlanId);
		  dataMap.put("riskAnswerTable", riskAnswerTable);
		  
		  return dataMap;
	  }
	
	/**
	 * 按评估计划	查询公司10大风险,为模板提供数据
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findTop10RiskTableByPlanId(String assessPlanId){
		List<Map<String,Object>> riskCatalogTable=new ArrayList<Map<String,Object>>();
		
		//子查询
		DetachedCriteria dc= DetachedCriteria.forClass(Risk.class,"re");
		dc.add(Restrictions.eqProperty("r.id", "re.parent.id"));
		dc.add(Restrictions.eq("re.isRiskClass", "re"));
		
		Criteria c = o_riskDAO.getSession().createCriteria(Risk.class,"r");
		c.createAlias("adjustHistory", "h", CriteriaSpecification.INNER_JOIN);
		c.add(Restrictions.eq("h.riskAssessPlan.id", assessPlanId));
		c.add(Restrictions.eq("r.isRiskClass", "rbs"));
		//必须加setProjection，否则报错  郑军祥
		c.add(Subqueries.exists(dc.setProjection(Projections.property("re.id"))));
		c.addOrder(Order.desc("r.lastModifyTime"));
		List<Risk> riskList = c.list();
		String[] ids = new String[riskList.size()];	//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
		for(int i=0;i<riskList.size();i++){
			String id  = riskList.get(i).getId();
			ids[i] = id;
			idList.add(id);
		}
		
		//获取风险分类的责任主体和辅助部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
		Map<String,Object> respDeptMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relaDeptMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//相关部门
		
		//获取风险状态和趋势
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		Map<String,Object> trendMap = (Map<String,Object>)historyMap.get("trendMap");	//趋势
		
		//组装显示列表
		for(int i=0;i<riskList.size();i++){
			Risk r  = riskList.get(i);
			String id = r.getId();
			Map<String,Object> riskMap = new HashMap<String,Object>();
			  riskMap.put("id", id);
			  //序号
			  riskMap.put("num", i+1);
			  //风险大类
			  riskMap.put("name", r.getName());
			  //责任主体
			  riskMap.put("respDept", respDeptMap.get(id)==null?"":respDeptMap.get(id).toString());
			  //辅助部门
			  riskMap.put("relaDept", relaDeptMap.get(id)==null?"":relaDeptMap.get(id).toString());
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
			  //趋势
			  String trend = "";
			  if(trendMap!=null && trendMap.get(id)!=null){
				  trend = trendMap.get(id).toString();
			  }
			  if(trend.equals("icon-ibm-icon-trend-rising-positive")){
				  trend = "up";
			  }else if(trend.equals("icon-ibm-icon-trend-neutral-null")){
				  trend = "flat";
			  }else if(trend.equals("icon-ibm-icon-trend-falling-negative")){
				  trend = "down";
			  }else{
				  trend = "";
			  }
			  riskMap.put("trend", trend);
			  riskCatalogTable.add(riskMap);
		}
		
		return riskCatalogTable;
	}
	
	/**
	 * 按评估计划	查询最新风险,为模板提供数据
	 * 这块使用了数据库的时间函数，导致sql不能跨数据库
	 * @author zhengjunxiang
	 * @month  几月份
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findLatestRiskByPlanId(String companyId,int month,String assessPlanId){
  		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
  		Criteria c = o_riskDAO.createCriteria();
  		c.createAlias("adjustHistory", "h", CriteriaSpecification.INNER_JOIN);
  		c.add(Restrictions.eq("h.riskAssessPlan.id", assessPlanId));
		c.add(Restrictions.eq("company.id", companyId));
		c.add(Restrictions.eq("isRiskClass", "re"));
		c.add(Restrictions.eq("deleteStatus", "1"));
		//mysql自己脚本
		//c.add(Restrictions.sqlRestriction("MONTH(last_modify_time)="+month));
		//得到最近30天的开始时间和结束时间
		Calendar calendar = Calendar.getInstance();
		Date endTime = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		Date startTime = calendar.getTime();
		c.add(Restrictions.ge("lastModifyTime", startTime));
		c.add(Restrictions.le("lastModifyTime", endTime));
		c.addOrder(Order.desc("lastModifyTime"));
		List<Risk> riskList = c.list();
		
		String[] ids = new String[riskList.size()];		//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
		for(int i=0;i<riskList.size();i++){
			Risk r = riskList.get(i);
  			String id = r.getId();
  			ids[i] = id;
  			idList.add(id);
			Map m = new HashMap();
			m.put("id", id);
  			m.put("belongRisk", r.getParentName());
  			m.put("name", r.getName());
  			list.add(m);
  		}
		
		//获取风险分类的责任主体和辅助部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
		Map<String,Object> respDeptMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relaDeptMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//相关部门
		
		//获取风险状态
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		
		//组装显示列表
		for(int i=0;i<list.size();i++){
			  Map riskMap  = list.get(i);
			  String id = riskMap.get("id").toString();
			  //序号
			  riskMap.put("num", i+1);
			  //责任主体
			  riskMap.put("respDept", respDeptMap.get(id)==null?"":respDeptMap.get(id).toString());
			  //辅助部门
			  riskMap.put("relaDept", relaDeptMap.get(id)==null?"":relaDeptMap.get(id).toString());
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
		}
		
        return list;
	}
	
	/**
	 * 按评估计划	查询公司下风险事件，为模板提供数据
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findRiskEventByPlanId(String companyId,String assessPlanId){
  		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
  		Criteria c = o_riskDAO.createCriteria();
  		c.createAlias("adjustHistory", "h", CriteriaSpecification.INNER_JOIN);
  		c.createAlias("parent", "parent", CriteriaSpecification.LEFT_JOIN);
  		c.add(Restrictions.eq("h.riskAssessPlan.id", assessPlanId));
		c.add(Restrictions.eq("company.id", companyId));
		c.add(Restrictions.eq("isRiskClass", "re"));
		c.add(Restrictions.eq("deleteStatus", "1"));
		c.addOrder(Order.desc("lastModifyTime"));
		List<Risk> riskList = c.list();
		
		String[] ids = new String[riskList.size()];		//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
		for(int i=0;i<riskList.size();i++){
			Risk r = riskList.get(i);
  			String id = r.getId();
  			ids[i] = id;
  			idList.add(id);
			Map m = new HashMap();
			m.put("id", id);
			//序号
		    m.put("num", i+1);
  			m.put("catalog", r.getParent().getName());
  			m.put("name", r.getName());
  			list.add(m);
  		}
 		
 		//获取风险分类的责任主体和辅助部门
		Map<String,Object> orgMap = o_riskCmpBO.findOrgNameByRiskIds(ids);
		Map<String,Object> respDeptMap = (Map<String,Object>)orgMap.get("respDeptMap");	//责任部门
		Map<String,Object> relaDeptMap = (Map<String,Object>)orgMap.get("relaDeptMap");	//相关部门
		
		//获取风险状态和趋势
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		Map<String,Object> trendMap = (Map<String,Object>)historyMap.get("trendMap");	//趋势
		
		//组装显示列表
		for(int i=0;i<riskList.size();i++){
			  Map riskMap  = list.get(i);
			  String id = riskMap.get("id").toString();
			  //责任主体
			  riskMap.put("respDept", respDeptMap.get(id)==null?"":respDeptMap.get(id).toString());
			  //辅助部门
			  riskMap.put("relaDept", relaDeptMap.get(id)==null?"":relaDeptMap.get(id).toString());
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
			  //趋势
			  String trend = "";
			  if(trendMap!=null && trendMap.get(id)!=null){
				  trend = trendMap.get(id).toString();
			  }
			  if(trend.equals("icon-ibm-icon-trend-rising-positive")){
				  trend = "up";
			  }else if(trend.equals("icon-ibm-icon-trend-neutral-null")){
				  trend = "flat";
			  }else if(trend.equals("icon-ibm-icon-trend-falling-negative")){
				  trend = "down";
			  }else{
				  trend = "";
			  }
			  riskMap.put("trend", trend);
		}
		
        return list;
	}
	
	/**
	 * 按评估计划 查询部门风险,所有风险事件按照部门分组,为模板提供数据
	 * list<部门map>,部门map<部门id，部门下风险事件列表Map>
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findDepartRiskListByPlanId(String companyId, String assessPlanId){
  		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
  		Criteria c = o_riskOrgDAO.createCriteria();
  		c.createAlias("risk", "r", CriteriaSpecification.LEFT_JOIN);
  		c.createAlias("r.adjustHistory", "h", CriteriaSpecification.INNER_JOIN);
  		c.add(Restrictions.eq("h.riskAssessPlan.id", assessPlanId));
  		c.createAlias("sysOrganization", "o", CriteriaSpecification.LEFT_JOIN);
		c.add(Restrictions.eq("r.company.id", companyId));
		c.add(Restrictions.eq("r.isRiskClass", "re"));
		c.add(Restrictions.eq("r.deleteStatus", "1"));
		c.addOrder(Order.asc("o.orgseq"));	//按照一个部门（子部门）这样顺序查询
		List<RiskOrg> riskOrgList = c.list();

		String[] ids = new String[riskOrgList.size()];		//风险id数组
		List<String> idList = new ArrayList<String>();	//风险idList
		for(int i=0;i<riskOrgList.size();i++){
			RiskOrg ro = riskOrgList.get(i);
			SysOrganization sysOrganization = ro.getSysOrganization();
  			Risk risk = ro.getRisk();
  			String riskId = risk.getId();
  			ids[i] = riskId;
  			idList.add(riskId);
			Map m = new HashMap();
			m.put("deptId", sysOrganization.getId());
  			m.put("department", sysOrganization.getOrgname());
  			m.put("riskId", riskId);
  			m.put("name", risk.getName());
  			list.add(m);
  		}
		
		//获取风险状态和趋势
		Map<String,Object> historyMap = o_riskOutsideBO.findRiskStatusAndTrendByRiskIds(idList);
		Map<String,Object> statusMap = (Map<String,Object>)historyMap.get("statusMap");	//状态
		Map<String,Object> trendMap = (Map<String,Object>)historyMap.get("trendMap");	//趋势
		
		//1.组装显示列表,同时也构建部门风险概况
		Map<String,Object> orgMap = new HashMap<String,Object>();
		List<Map<String,Object>> orgList = new ArrayList<Map<String,Object>>();	//部门列表
		for(int i=0;i<list.size();i++){
			  Map riskMap  = list.get(i);
			  String id = riskMap.get("riskId").toString();
			  String deptId = riskMap.get("deptId").toString();
			  //序号
			  riskMap.put("num", i+1);
			  //状态
			  String status = "";
			  if(statusMap!=null && statusMap.get(id)!=null){
				  status = statusMap.get(id).toString();
			  }
			  if(status.equals("icon-ibm-symbol-4-sm")){
				  status = "red";
			  }else if(status.equals("icon-ibm-symbol-5-sm")){
				  status = "yellow";
			  }else if(status.equals("icon-ibm-symbol-6-sm")){
				  status = "green";
			  }else{
				  status = "";
			  }
			  riskMap.put("status", status);
			  //趋势
			  String trend = "";
			  if(trendMap!=null && trendMap.get(id)!=null){
				  trend = trendMap.get(id).toString();
			  }
			  if(trend.equals("icon-ibm-icon-trend-rising-positive")){
				  trend = "up";
			  }else if(trend.equals("icon-ibm-icon-trend-neutral-null")){
				  trend = "flat";
			  }else if(trend.equals("icon-ibm-icon-trend-falling-negative")){
				  trend = "down";
			  }else{
				  trend = "";
			  }
			  riskMap.put("trend", trend);
			  
			  //构建部门分析概况
			  if(orgMap.containsKey(deptId)){
				  List<Map<String,Object>> riskList = (List<Map<String,Object>>)orgMap.get(deptId);
				  riskList.add(riskMap);
				  orgMap.put(deptId, riskList);
			  }else{
				  List<Map<String,Object>> riskList = new ArrayList<Map<String,Object>>();
				  riskList.add(riskMap);
				  orgMap.put(deptId, riskList);
				  //部门列表
				  Map<String,Object> org = new HashMap<String,Object>();
				  org.put("id", deptId);
				  org.put("name", riskMap.get("department")==null?"":riskMap.get("department").toString());//记录部门的名称
				  orgList.add(org);
			  }
		}
		
		//2.组装部门风险概况显示列表,map是无序的,所以不能顺序遍历map,而是List
		int orgNum = 1;
		for(Map org : orgList){ 
		    String orgId = org.get("id").toString(); 
		    List<Map<String,Object>> subList = (List<Map<String,Object>>)orgMap.get(orgId);
		    //重要风险
		    int importRiskNum = 0;
			//关注风险
			int focusRiskNum = 0;
			//安全风险
			int safeRiskNum = 0;
		    for(Map<String,Object> r : subList){
		    	String status = r.get("status").toString();
		    	if(status.equals("red")){
		    		importRiskNum++;
		    	}else if(status.equals("yellow")){
		    		focusRiskNum++;
		    	}else if(status.equals("green")){
		    		safeRiskNum++;
		    	}else{}
		    }
		    //序号
		    org.put("num", orgNum++);
		    org.put("importRiskNum", importRiskNum);
		    org.put("focusRiskNum", focusRiskNum);
		    org.put("safeRiskNum", safeRiskNum);
		} 
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orgList", orgList);
		map.put("orgRiskList", list);
        return map;
	}
	
	/**
	 * 按评估计划查询 查询风险应对方案,所有应对方案按照风险事件分组,为模板提供数据
	 * @author zhengjunxiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> findRiskSolutionsByPlanId(String companyId,String assessPlanId){
  		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
  		
  		Criteria c = o_riskDAO.createCriteria();
  		//自动关联上级风险
  		c.createAlias("parent", "parent",CriteriaSpecification.LEFT_JOIN);
  		c.setFetchMode("parent", FetchMode.SELECT);
  		c.createAlias("adjustHistory", "h", CriteriaSpecification.INNER_JOIN);
  		c.add(Restrictions.eq("h.riskAssessPlan.id", assessPlanId));
		c.add(Restrictions.eq("company.id", companyId));
		c.add(Restrictions.eq("isRiskClass", "re"));
		c.add(Restrictions.eq("deleteStatus", "1"));
		c.addOrder(Order.asc("parent.id"));
		c.addOrder(Order.desc("lastModifyTime"));
		List<Risk> riskList = c.list();
 		List<String> idList = new ArrayList<String>();
 		for(int i=0;i<riskList.size();i++){
 			Risk r = riskList.get(i);
 			idList.add(r.getId());
 		}
 		//得到风险事件的应对方案
		List<SolutionRelaRisk> solutionList = o_responseListBO.findSolutionsByRisk(idList);
 		int num = 1;
 		for(SolutionRelaRisk sr : solutionList){
 			Risk r = sr.getRisk();
 			Solution s = sr.getSolution();
			Map<String,Object> m = new HashMap<String,Object>();
			//序号
		    m.put("num", num++);
			m.put("id", sr.getId());
  			m.put("catalog", r.getParent()==null?"":r.getParent().getName());
  			m.put("name", r.getName());
  		    m.put("answer", s.getSolutionName());
  		    Set<SolutionRelaOrg> solorg = s.getSolutionRelaOrg();//嵌套查询,拼接语句
  		    String respDept = ""; //责任部门
  		    String respEmp = ""; //责任人
  		    for(SolutionRelaOrg so : solorg){
  		    	if(so.getType().equals(Contents.ORG_RESPONSIBILITY)){
  		    		respDept += respDept + so.getOrg().getOrgname() + ",";
  		    	}
  		    	if(so.getType().equals(Contents.EMP_RESPONSIBILITY)){
  		    		respEmp += respEmp + so.getEmp().getEmpname() + ",";
  		    	}
  		    }
  		    if(!respDept.equals("")){
  		    	respDept = respDept.substring(0,respDept.length()-1);
  		    }
  		    if(!respEmp.equals("")){
  		    	respEmp = respEmp.substring(0,respEmp.length()-1);
		    }
  		    m.put("respDept", respDept); //责任部门
  		    m.put("respEmp", respEmp);	//责任人
  		    m.put("finishDate", s.getExpectEndTime().toLocaleString());
  			list.add(m);
			}
  		return list;
	}
	
	/**
	 * 查询部门下所有一级子部门的风险事件（包括风险水平），并且按照部门统计重大风险个数，关注风险个数，安全风险个数。没有风险事件的一级子部门，不进行查出
	 * @param orgId		 部门id,支持集团id和公司id,子部门id，总部门id 因为整个集团树是一个大树
	 * @param onlyCurrent  true:只取当前节点部门节点   false:取当前节点下级部门节点
	 * @return Map<部门id，Strign[4]{总风险个数，重大风险个数，关注风险个数，安全风险个数}>
	 * 不使用了，因为查询逻辑太复杂，而且也需要递归调用
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> findDeptRiskEventSummary(String orgId,Boolean onlyCurrent,Boolean isGroup){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlMid = new StringBuffer();
		Map<String,String> mapParams = new HashMap<String,String>();
		sqlMid.append(" FROM t_rm_risk_org riskorg")
        .append(" INNER JOIN t_rm_risks r on riskorg.risk_id = r.id")
        .append(" INNER JOIN t_sys_organization org on riskorg.org_id = org.id")
        .append(" LEFT  JOIN t_rm_risk_adjust_history h on h.risk_id=r.id and h.is_latest='1'")
        .append(" WHERE 1=1 ");
		//是否是集团,是否是单一节点
		if(isGroup){
            sql.append("SELECT org.parent_id as orgId,r.id as riskId,r.risk_name as name,h.assessement_status as status");
            sql.append(sqlMid);
            sql.append(" and org.parent_id in (select id from t_sys_organization where PARENT_ID= :orgId or PARENT_ID is null )");
            mapParams.put("orgId", orgId);
        }else{
            if(onlyCurrent){
                sql.append("SELECT org.parent_id as orgId,r.id as riskId,r.risk_name as name,h.assessement_status as status");
                sql.append(sqlMid);
                sql.append(" and org.id_seq like '%." + orgId + ".%'");
            }else{
                sql.append("SELECT org.id as orgId,r.id as riskId,r.risk_name as name,h.assessement_status as status");
                sql.append(sqlMid);
                sql.append(" and org.id_seq like '%." + orgId + ".%'");
            }
            mapParams.put("orgId", "%."+orgId+".%");
        }
		
		SQLQuery sqlQuery = o_riskDAO.createSQLQuery(sql.toString(),mapParams);
  		List<Object[]> list = sqlQuery.list();
  		for(Object[] o : list){
  			String id  = o[0].toString();
  			String status = o[3]==null?"":o[3].toString();
  			
  			Integer[] summary = new Integer[]{0,0,0,0};	//统计的集合
  			if(resultMap.containsKey(id)){
  				summary = (Integer[])resultMap.get(id);
  			}
  			
  			//计算风险红黄绿灯的个数
  			if(Contents.RISK_LEVEL_HIGH.equals(status)){
				summary[1] = summary[1] + 1;
			}else if(Contents.RISK_LEVEL_MIDDLE.equals(status)){
				summary[2] = summary[2] + 1;
			}else if(Contents.RISK_LEVEL_LOW.equals(status)){
				summary[3] = summary[3] + 1;
			}else{}
  			summary[0] = summary[0] + 1;	//风险事件的总数也包括没有灯的事件个数
			resultMap.put(id, summary);
  		}
  		
        return resultMap;
	}
}
