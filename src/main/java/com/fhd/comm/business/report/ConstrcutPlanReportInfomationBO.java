package com.fhd.comm.business.report;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import sun.misc.BASE64Encoder;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.comm.utils.FreeMarkerXml;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.comm.report.ReportInfomationDAO;
import com.fhd.dao.icm.assess.MeasureRelaRiskDAO;
import com.fhd.dao.icm.icsystem.ConstructRelaProcessDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.process.ProcessPointDAO;
import com.fhd.dao.process.ProcessRelaFileDAO;
import com.fhd.dao.process.ProcessRelaRiskDAO;
import com.fhd.entity.comm.report.ReportInfomation;
import com.fhd.entity.icm.control.MeasureRelaRisk;
import com.fhd.entity.process.Process;
import com.fhd.entity.process.ProcessGraph;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.ProcessPointRelaOrg;
import com.fhd.entity.process.ProcessRelaRisk;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.assess.AssessRelaDefectBO;
import com.fhd.icm.business.assess.AssessResultBO;
import com.fhd.icm.business.bpm.AssessPlanBpmBO;
import com.fhd.icm.business.icsystem.ConstructPlanBO;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.process.ProcessGraphBO;
import com.fhd.icm.business.process.ProcessPointBO;
import com.fhd.icm.business.rectify.ImprovePlanRelaDefectBO;
import com.fhd.icm.utils.mxgraph.Constants;
import com.fhd.sys.business.file.FileUploadBO;
import com.fhd.sys.business.organization.EmployeeBO;
import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.canvas.mxICanvas2D;
import com.mxgraph.reader.mxSaxOutputHandler;
import com.mxgraph.util.mxUtils;

/**
 * 体系建设报告BO.
 * @author 宋佳
 * @since 2013-06-06 pm 13:40
 */
@Service
@SuppressWarnings({"unchecked","unused","deprecation", "restriction"})
public class ConstrcutPlanReportInfomationBO {
	
	@Autowired
	private ReportInfomationDAO o_reportInfomationDAO;
	@Autowired
	private ReportRelaConstructPlanBO o_reportRelaConstructPlanBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private ConstructPlanBO o_constructPlanBO;
	@Autowired
	private AssessResultBO o_assessResultBO;
	@Autowired
	private AssessRelaDefectBO o_assessRelaDefectBO;
	@Autowired
	private AssessPlanBpmBO o_assessPlanBpmBO;
	@Autowired
	private EmployeeBO o_empolyeeBO;
	@Autowired
	private ImprovePlanRelaDefectBO o_improvePlanRelaDefectBO;
	@Autowired
	private ConstructRelaProcessDAO o_constructRelaProcessDAO;
	@Autowired
	private ProcessRelaFileDAO o_processRelaFileDAO;
	@Autowired
	private MeasureRelaRiskDAO o_measureRelaRiskDAO;
	@Autowired
	private ProcessRelaRiskDAO o_processRelaRiskDAO;
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private ProcessPointBO o_processPointBO;
	@Autowired
	private ProcessPointDAO o_processPointDAO;
	@Autowired
	private ProcessGraphBO o_processGraphBO;
	@Autowired
	private FileUploadBO o_fileUploadBO;
	
	private transient SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	
	/**
	 * Cache for all images.
	 */
	private transient Map<String, Image> imageCache = new HashMap<String, Image>();
	

	/**
	 * 保存内控手册.
	 * @author 吴德福
	 * @param report
	 * @param request
	 * @throws Exception 
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public void saveInternalControlManual(ReportInfomation report, HttpServletRequest request) throws Exception {
		String templatePath = "/com/fhd/comm/business/report";
		String templateName = "InternalControlManual.ftl";
		Map<String,Object> dataMap = this.findInternalControlManualMap();
		byte[] contents = FreeMarkerXml.createDoc(templatePath, templateName, dataMap);
		report.setReportData(contents);
		
		//report.setReportDoc(contents);
		FileUploadEntity file = this.packageFileByContent(report.getReportName(), contents);
		o_fileUploadBO.saveFileUpload(file);
		report.setFile(file);
		
        report.setStatus(Contents.STATUS_SAVED);
        report.setExecuteStatus(Contents.DEAL_STATUS_NOTSTART);
        report.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
		
		o_reportInfomationDAO.merge(report);
		
		//o_reportRelaConstructPlanBO.removeReportRelaConstructPlanByReportId(report.getId());
	}
	
	/**
	 * 保存生成的报告到文件中.
	 * @author 吴德福
	 * @param fileName
	 * @param contents
	 * @return FileUploadEntity
	 * @throws Exception 
	 */
	public FileUploadEntity packageFileByContent(String fileName, byte[] contents) throws Exception{
		FileUploadEntity file = new FileUploadEntity();
		file.setId(Identities.uuid());
		file.setOldFileName(fileName);
		file.setNewFileName(fileName);
		DictEntry fileType = new DictEntry();
		fileType.setId("0file_type_xls");
		file.setFileType(fileType);
		file.setContents(contents);
		file.setUploadTime(new Date());
		file.setFileSize("0");
		file.setCountNum(0);
		return file;
	}
	
	/**
	 * 内控手册替换数据map.
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	public Map<String,Object> findInternalControlManualMap() throws Exception{
		Map<String,Object> dataMap = new HashMap<String,Object>();
		//1.基本信息
		String orgName = "";
		String empId = o_assessPlanBpmBO.findAssessPlanEmpIdByRole("ICDepartmentStaff");
		SysOrganization department = o_empolyeeBO.getDepartmentByEmpId(empId);
		if(null != department){
			orgName = department.getOrgname();
		}
		dataMap.put("公司名称", UserContext.getUser().getCompanyName());
		dataMap.put("内控主责部门", orgName);
		dataMap.put("内控归口部门", orgName);
		Date date = new Date();
		dataMap.put("发布日期", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		dataMap.put("年", String.valueOf(date.getYear()+1900));
		dataMap.put("月", String.valueOf(date.getMonth()+1));
		dataMap.put("日", String.valueOf(date.getDay()));
		
		
		//4.3文件控制
		StringBuilder builder = new StringBuilder();
		//所有流程
		List<Process> processList= o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
		//一级流程
		List<Process> firstProcessList = this.findParentProcessResult();
		
		Criteria criteria = o_measureRelaRiskDAO.createCriteria();
		criteria.createAlias("controlMeasure", "controlMeasure");
		criteria.add(Restrictions.eq("controlMeasure.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<MeasureRelaRisk> measureRelaRiskList = criteria.list();
		
		Criteria criRroRisk = o_processRelaRiskDAO.createCriteria();
		criRroRisk.createAlias("risk", "risk");
		criRroRisk.add(Restrictions.eq("risk.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<ProcessRelaRisk> processRelaRiskList = criRroRisk.list();
		
		Criteria criProcessPoint = o_processPointDAO.createCriteria();
		criProcessPoint.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		List<ProcessPoint> processPointList = criProcessPoint.list();
		List<ProcessGraph> processGraphList = o_processGraphBO.findProcessGraphListBySome();
		String parentCode = "4.3.";
		int i = 1;
		for(Process process : firstProcessList){
			parentCode = "4.3."+i;
			builder.append("<wx:sub-section>")
					.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\">")
						.append("<w:pPr>")
							.append("<w:pStyle w:val=\"").append(process.getLevel()+2).append("\"/>")
						.append("</w:pPr>")
						.append("<w:r>")
							.append("<w:rPr>")
								.append("<w:rFonts w:hint=\"fareast\"/>")
							.append("</w:rPr>")
							.append("<w:t>").append(parentCode).append(" ").append(process.getName()).append("</w:t>")
						.append("</w:r>")
					.append("</w:p>");
			
			builder = findResultByQuery(process,processList,processRelaRiskList,measureRelaRiskList,processPointList,processGraphList,builder,parentCode);
			
			builder.append("</wx:sub-section>");
			i++;
		}
		dataMap.put("文件控制描述", builder.toString());
		
		return dataMap;
	}
	
	/**
	 * <pre>
	 * 	查询内控所有流程和流程节点和
	 * </pre>
	 * @author 宋佳
	 * @param id
	 * @param query
	 * @param processResult
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public StringBuilder findResultByQuery(Process process,List<Process> processList,List<ProcessRelaRisk> processRelaRiskList,List<MeasureRelaRisk> measureRelaRiskList,List<ProcessPoint> processPointList,
			List<ProcessGraph> processGraphList, StringBuilder sb, String parentCode){
		int i=1;
		for(Process processVar:processList ){
			if(processVar.getParent() == process){
				if(!processVar.getIsLeaf()){
					sb.append("<wx:sub-section>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"").append(process.getLevel()+2+1).append("\"/>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:t>").append(parentCode).append(".").append(i).append(" ").append(processVar.getName()).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>");
					
					findResultByQuery(processVar,processList,processRelaRiskList,measureRelaRiskList,processPointList,processGraphList,sb,parentCode+"."+i);
					i++;
					sb.append("</wx:sub-section>");
				}else{
					String controlTarget = "";
					if(StringUtils.isNotBlank(processVar.getControlTarget())){
						controlTarget = processVar.getControlTarget();
					}
					//获取流程的责任部门和相关部门
					String[] orgs = o_processBO.findProcessOrg(processVar);
					sb.append("<wx:sub-section>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"").append(process.getLevel()+2+1).append("\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(parentCode).append(".").append(i).append(" ").append(processVar.getName()).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>");
					
					sb.append(this.appendEndProcessure("1、流程目标:", controlTarget));
					sb.append(this.appendEndProcessure("2、流程管理部门:", orgs[0]));
					sb.append(this.appendEndProcessure("3、流程涉及部门及领导:", orgs[1]));
					sb.append(this.appendEndProcessure("4、规章制度及管理标准:", ""));
					sb.append(this.appendProcessPoint(processVar,processPointList).toString());
					sb.append(this.appendProcessRelaRiskList(processVar,processRelaRiskList,measureRelaRiskList).toString());
					sb.append(this.appendEndProcessure("7、流程图:", this.appendProcessFlowchart(processVar).toString()));
					i++;
					sb.append("</wx:sub-section>");
				}
			}
		}
		return sb;
	}
	
	public StringBuilder appendEndProcessure(String title, String content){
		StringBuilder sb = new StringBuilder();
		
		sb.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\">")
			.append("<w:pPr>")
				.append("<w:pStyle w:val=\"a5\"/>")
				.append("<w:rPr>")
					.append("<w:rFonts w:hint=\"fareast\"/>")
				.append("</w:rPr>")
			.append("</w:pPr>")
			.append("<w:r>")
				.append("<w:rPr>")
					.append("<w:rFonts w:ascii=\"Arial\" w:h-ansi=\"Arial\" w:cs=\"Arial\"/>")
					.append("<wx:font wx:val=\"Arial\"/>")
					.append("<w:b/>")
					.append("<w:b-cs/>")
				.append("</w:rPr>")
				.append("<w:t>").append(title).append("</w:t>")
			.append("</w:r>")
		.append("</w:p>")
		.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\">")
			.append("<w:pPr>")
				.append("<w:pStyle w:val=\"a5\"/>")
				.append("<w:ind w:first-line=\"480\"/>")
				.append("<w:rPr>")
					.append("<w:rFonts w:hint=\"fareast\"/>")
				.append("</w:rPr>")
			.append("</w:pPr>")
			.append("<w:r>")
				.append("<w:rPr>")
					.append("<w:rFonts w:hint=\"fareast\"/>")
				.append("</w:rPr>")
				.append("<w:t>").append(content).append("</w:t>")
			.append("</w:r>")
		.append("</w:p>");
		
		return sb;
	}
	/**
	 * 根据流程id加入流程节点列表
	 */
	public StringBuilder appendProcessPoint(Process process,List<ProcessPoint> processPointList){
		StringBuilder sb = new StringBuilder();
		
		sb.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\">")
				.append("<w:pPr>")
					.append("<w:pStyle w:val=\"a5\"/>")
					.append("<w:rPr>")
						.append("<w:rFonts w:hint=\"fareast\"/>")
					.append("</w:rPr>")
				.append("</w:pPr>")
				.append("<w:r>")
					.append("<w:rPr>")
						.append("<w:rFonts w:ascii=\"Arial\" w:h-ansi=\"Arial\" w:cs=\"Arial\"/>")
						.append("<wx:font wx:val=\"Arial\"/>")
						.append("<w:b/>")
						.append("<w:b-cs/>")
					.append("</w:rPr>")
					.append("<w:t>5、流程描述:</w:t>")
				.append("</w:r>")
			.append("</w:p>")
			.append("<w:tbl>")
				.append("<w:tblPr>")
					.append("<w:tblW w:w=\"0\" w:type=\"auto\"/>")
					.append("<w:tblBorders>")
						.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:insideH w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:insideV w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
					.append("</w:tblBorders>")
					.append("<w:tblLook w:val=\"04A0\"/>")
				.append("</w:tblPr>")
				.append("<w:tblGrid>")
					.append("<w:gridCol w:w=\"2840\"/>")
					.append("<w:gridCol w:w=\"2841\"/>")
					.append("<w:gridCol w:w=\"2842\"/>")
				.append("</w:tblGrid>")
				.append("<w:tr wsp:rsidR=\"00000000\" wsp:rsidRPr=\"000C154C\" wsp:rsidTr=\"000C154C\">")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2840\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>编号</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2841\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>责任部门</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2842\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>流程步骤描述</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
				.append("</w:tr>");
		
		if(null != processPointList && processPointList.size()>0){
			
			for(ProcessPoint processPoint : processPointList){
				if(processPoint.getProcess().equals(process)){
					sb.append("<w:tr wsp:rsidR=\"00000000\" wsp:rsidRPr=\"000C154C\" wsp:rsidTr=\"000C154C\">");
					
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2840\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(processPoint.getCode()).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					String orgName = "";
					Set<ProcessPointRelaOrg> processPointRelaOrgs = processPoint.getProcessPointRelaOrg();
					for (ProcessPointRelaOrg processPointRelaOrg : processPointRelaOrgs) {
						if (Contents.ORG_RESPONSIBILITY.equals(processPointRelaOrg.getType()) && null != processPointRelaOrg.getOrg()){
							orgName = processPointRelaOrg.getOrg().getOrgname();
						}
					}
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2841\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(orgName).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2842\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(processPoint.getName()).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					sb.append("</w:tr>");
				}
			}
		}
		
		sb.append("</w:tbl>");
		return sb;
	}
	
	/**
	 * @param process
	 * @param processRelaRiskList
	 * @param measureRelaRiskList
	 * @return
	 */
	private StringBuilder appendProcessRelaRiskList(Process process,List<ProcessRelaRisk> processRelaRiskList,List<MeasureRelaRisk> measureRelaRiskList) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\">")
				.append("<w:pPr>")
					.append("<w:pStyle w:val=\"a5\"/>")
					.append("<w:rPr>")
						.append("<w:rFonts w:hint=\"fareast\"/>")
					.append("</w:rPr>")
				.append("</w:pPr>")
				.append("<w:r>")
					.append("<w:rPr>")
						.append("<w:rFonts w:ascii=\"Arial\" w:h-ansi=\"Arial\" w:cs=\"Arial\"/>")
						.append("<wx:font wx:val=\"Arial\"/>")
						.append("<w:b/>")
						.append("<w:b-cs/>")
					.append("</w:rPr>")
					.append("<w:t>6、风险描述:</w:t>")
				.append("</w:r>")
			.append("</w:p>")
			.append("<w:tbl>")
				.append("<w:tblPr>")
					.append("<w:tblW w:w=\"8895\" w:type=\"dxa\"/>")
					.append("<w:tblBorders>")
						.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:insideH w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
						.append("<w:insideV w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
					.append("</w:tblBorders>")
					.append("<w:tblLayout w:type=\"Fixed\"/>")
					.append("<w:tblLook w:val=\"04A0\"/>")
				.append("</w:tblPr>")
				.append("<w:tblGrid>")
					.append("<w:gridCol w:w=\"1525\"/>")
					.append("<w:gridCol w:w=\"2267\"/>")
					.append("<w:gridCol w:w=\"1417\"/>")
					.append("<w:gridCol w:w=\"1560\"/>")
					.append("<w:gridCol w:w=\"708\"/>")
					.append("<w:gridCol w:w=\"709\"/>")
					.append("<w:gridCol w:w=\"709\"/>")
				.append("</w:tblGrid>")
				.append("<w:tr wsp:rsidR=\"00000000\" wsp:rsidRPr=\"000C154C\" wsp:rsidTr=\"000C154C\">")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"1526\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>风险点编号</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2267\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>风险点名称</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"1417\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>控制点编号</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"1560\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>控制措施</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"708\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>控制方法</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"709\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>控制频率</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
					.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"709\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"pct-10\" w:color=\"auto\" w:fill=\"auto\" wx:bgcolor=\"E5E5E5\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>实施证据</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>")
				.append("</w:tr>");
		
		if(null != processRelaRiskList && processRelaRiskList.size()>0){
			for (ProcessRelaRisk processRelaRisk : processRelaRiskList) {
				if(processRelaRisk.getProcess().equals(process)){
					//风险点编号
					String riskCode = "";
					//风险点
					String riskName = "";
					//控制点编号
					String measureCode = "";
					//控制措施
					String measureName = "";
					//控制方法
					String measureMethod = "";
					//控制频率
					String controlFrequency = "";
					//实施证据
					String implementProof = "";
					//制度索引
					String ruleName = "";
					
					sb.append("<w:tr wsp:rsidR=\"00000000\" wsp:rsidRPr=\"000C154C\" wsp:rsidTr=\"000C154C\">");
					
					//风险编号
					if(StringUtils.isNotBlank(processRelaRisk.getRisk().getCode())){
						riskCode = processRelaRisk.getRisk().getCode();
					}
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"1526\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(riskCode).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					//风险名称
					if(StringUtils.isNotBlank(processRelaRisk.getRisk().getName())){
						riskName = processRelaRisk.getRisk().getName();
					}
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"2267\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(riskName).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");

					for(MeasureRelaRisk measureRelaRisk : measureRelaRiskList){
						if(measureRelaRisk.getRisk().equals(processRelaRisk.getRisk())){
							//控制措施编号
							if(StringUtils.isNotBlank(measureRelaRisk.getControlMeasure().getCode())){
								measureCode = measureRelaRisk.getControlMeasure().getCode();
							}
							
							//控制措施名称
							if(StringUtils.isNotBlank(measureRelaRisk.getControlMeasure().getName())){
								measureName = measureRelaRisk.getControlMeasure().getName();
							}
						
							//控制方法
							if(null != measureRelaRisk.getControlMeasure().getControlMeasure()){
								measureMethod = measureRelaRisk.getControlMeasure().getControlMeasure().getName();
							}
							
							//控制频率
							if(null != measureRelaRisk.getControlMeasure().getControlFrequency()){
								controlFrequency = measureRelaRisk.getControlMeasure().getControlFrequency().getName();
							}
							
							//实施证据
							if(null != measureRelaRisk.getControlMeasure().getImplementProof()){
								implementProof = measureRelaRisk.getControlMeasure().getImplementProof();
							}
						}
					}
					
					//控制措施编号
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"1417\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(measureCode).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					//控制措施名称
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"1560\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(measureName).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
				
					//控制方法
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"708\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(measureMethod).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					//控制频率
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"709\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(controlFrequency).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					//实施证据
					sb.append("<w:tc>")
						.append("<w:tcPr>")
							.append("<w:tcW w:w=\"709\" w:type=\"dxa\"/>")
							.append("<w:tcBorders>")
								.append("<w:top w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:left w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:bottom w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
								.append("<w:right w:val=\"single\" w:sz=\"4\" wx:bdrwidth=\"10\" w:space=\"0\" w:color=\"auto\"/>")
							.append("</w:tcBorders>")
							.append("<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"auto\"/>")
						.append("</w:tcPr>")
						.append("<w:p wsp:rsidR=\"00000000\" wsp:rsidRDefault=\"006F7B41\" wsp:rsidP=\"000C154C\">")
							.append("<w:pPr>")
								.append("<w:pStyle w:val=\"a5\"/>")
							.append("</w:pPr>")
							.append("<w:r>")
								.append("<w:rPr>")
									.append("<w:rFonts w:hint=\"fareast\"/>")
								.append("</w:rPr>")
								.append("<w:t>").append(implementProof).append("</w:t>")
							.append("</w:r>")
						.append("</w:p>")
					.append("</w:tc>");
					
					sb.append("</w:tr>");
				}
			}
		}
		
		sb.append("</w:tbl>");
		return sb;
	}
	/**
	 * <pre>
	 * 插入流程图
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param process
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	private StringBuilder appendProcessFlowchart(Process process) {
		StringBuilder sb = new StringBuilder();
		if(null != process.getFlowXML()){
			sb.append("<w:pict>");
				sb.append("<w:binData w:name=\"wordml://"+process.getId()+".png\" xml:space=\"preserve\">");
				sb.append(this.getImageStr(process.getFlowXML(),process.getFlowbg(),process.getFlowFormat(),Integer.valueOf(process.getFlowHeight()),Integer.valueOf(process.getFlowWidth())));
				sb.append("</w:binData>");
				sb.append("<v:shape id=\"_x0000_i1029\" type=\"#_x0000_t75\" style=\"width:");
					sb.append(Integer.valueOf(process.getFlowWidth())>381.75?381.75:Integer.valueOf(process.getFlowWidth()));
					sb.append("pt;height:");
					sb.append(Integer.valueOf(process.getFlowHeight())>679.5?679.5:Integer.valueOf(process.getFlowHeight()));
					sb.append("pt\">");
					sb.append("<v:imagedata src=\"wordml://"+process.getId()+".png\" o:title=\"icon_trend_neutral_null\"/>");
				sb.append("</v:shape>");
			sb.append("</w:pict>");
		}
		
		return sb;
	}
	
	/**
	 * <pre>
	 *  查询所有一级流程
	 * </pre>
	 * @author 宋佳
	 * @param id
	 * @param query
	 * @param processResult
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public List<Process> findParentProcessResult(){
		Criteria criteria = o_processDAO.createCriteria();
		criteria.add(Restrictions.isNull("parent.id"));   //  只允许使用isnull  使用isempty报错
		criteria.add(Restrictions.eq("deleteStatus",Contents.DELETE_STATUS_USEFUL));
		criteria.add(Restrictions.eq("company.id",UserContext.getUser().getCompanyid()));
		return criteria.list();
	}
	/**
	 * 根据id集合批量删除评价报告.
	 * @param ids
	 */
	@Transactional
	public void removeReportByIds(String ids){
		//删除评价报告表
		o_reportInfomationDAO.createQuery("delete ReportInfomation where id in (:ids)")
			.setParameterList("ids", StringUtils.split(ids,",")).executeUpdate();
		//删除评价报告关联计划表
		o_reportRelaConstructPlanBO.removeReportRelaConstructByReportIds(ids);
	}
	/**
	 * 根据id查询评价报告.
	 * @param id
	 * @return
	 */
	public ReportInfomation findReportById(String id){
		return o_reportInfomationDAO.get(id);
	}
	
	/**
	 * 根据查询条件分页查询评价报告.
	 * @param page
	 * @param sort
	 * @param dir, 
	 * @param query
	 * @return Page<AssessmentReport>
	 */
	public Page<ReportInfomation> findReportListBySome(Page<ReportInfomation> page, 
			String sort, String query, String reportType){
		DetachedCriteria dc = DetachedCriteria.forClass(ReportInfomation.class);
		dc.add(Restrictions.eq("reportType.id", reportType));
		if(StringUtils.isNotBlank(query)){
			dc.add(Restrictions.like("reportName", query, MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		dc.addOrder(Order.asc("company.id"));
		dc.addOrder(Order.asc("reportType.id"));
		dc.addOrder(Order.asc("reportName"));
		dc.addOrder(Order.asc("reportCode"));
		return o_reportInfomationDAO.findPage(dc, page, false);
	}
	
	/**
	 * 根据计划id查询报告的版本号
	 * @param planId
	 * @return double
	 */
	public String findVersionCodeByPlanId(String reportType){
		String ret = null;
		
		/*
		select i.report_code 
		from t_report_information i left join t_report_rela_assessment a on i.id=a.report_id 
		where a.assessment_plan_id='' and i.report_type='' 
		*/
		
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append("select i.report_code ")
			.append("from t_report_information i ")
			.append("where 1=1 ");
		if(StringUtils.isNotBlank(reportType)){
			sqlBuffer.append("and i.report_type=:reportType ");
		}
		
		SQLQuery sqlQuery = o_reportInfomationDAO.createSQLQuery(sqlBuffer.toString());
		if(StringUtils.isNotBlank(reportType)){
			sqlQuery.setString("reportType", reportType);
		}
		List<String> list = sqlQuery.list();
		for (String versionCode : list) {
			if(StringUtils.isNotBlank(versionCode)){
				ret = versionCode;
			}
		}
		return ret;
	}
    
    /**
     * <pre>
     * 获取图片的字符串
     * </pre>
     * 
     * @author 张 雷
     * @param imageXML
     * @param bg
     * @param format
     * @param h
     * @param w
     * @return
     * @since  fhd　Ver 1.1
    */
	private String getImageStr(String imageXML,String bg,String format, Integer h, Integer w) {
    	String tmpdir=System.getProperty("java.io.tmpdir"); 
    	File f = new File(tmpdir); 
        InputStream in = null;
        byte[] data = null;
        File fTemp = null;
        String dataStr = null;
        Color bgColor = (bg != null) ? mxUtils.parseColor(bg) : Color.WHITE;
        BufferedImage image = mxUtils.createBufferedImage(w.intValue(), h.intValue(), bgColor);
        Graphics2D g2 = image.createGraphics();
		mxUtils.setAntiAlias(g2, true, true);
		BASE64Encoder encoder = new BASE64Encoder();
        try {
        	fTemp = File.createTempFile("tmp", ".png", f); 
        	renderXml(imageXML, createCanvas(tmpdir, g2));
    		ImageIO.write(image, format, fTemp);
            in = new FileInputStream(fTemp);
            data = new byte[in.available()];
            in.read(data);
            in.close();
            dataStr = encoder.encode(data);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	if(null != fTemp){
        		fTemp.delete();
        	}
        }
        return dataStr;
    }
    
	/**
	 * Renders the XML to the given canvas.
	 */
	protected void renderXml(String xml, mxICanvas2D canvas)
			throws SAXException, ParserConfigurationException, IOException
	{
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		reader.setContentHandler(new mxSaxOutputHandler(canvas));
		reader.parse(new InputSource(new StringReader(xml)));
	}

	/**
	 * Creates a graphics canvas with an image cache.
	 */
	protected mxGraphicsCanvas2D createCanvas(String url, Graphics2D g2)
	{
		// Caches custom images for the time of the request
		final Map<String, Image> shortCache = new HashMap<String, Image>();
		final String domain = url;

		mxGraphicsCanvas2D g2c = new mxGraphicsCanvas2D(g2)
		{
			public Image loadImage(String src)
			{
				// Uses local image cache by default
				Map<String, Image> cache = shortCache;

				// Uses global image cache for local images
				if (src.startsWith(domain))
				{
					cache = imageCache;
				}

				Image image = cache.get(src);

				if (image == null)
				{
					image = super.loadImage(src);

					if (image != null)
					{
						cache.put(src, image);
					}
					else
					{
						cache.put(src, Constants.EMPTY_IMAGE);
					}
				}
				else if (image == Constants.EMPTY_IMAGE)
				{
					image = null;
				}

				return image;
			}
		};

		g2c.setAutoAntiAlias(true);
		
		return g2c;
	}

}
