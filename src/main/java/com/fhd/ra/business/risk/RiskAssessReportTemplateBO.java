package com.fhd.ra.business.risk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.RiskAssessReportDAO;
import com.fhd.dao.risk.RiskAssessReportTemplateDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.risk.RiskAssessReport;
import com.fhd.entity.risk.RiskAssessReportTemplate;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.ReportTagConstant;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.jfreechar.FHDJfreeChar;
import com.fhd.fdc.utils.jfreechar.JFCBaseProper;
import com.fhd.ra.business.assess.oper.RAssessPlanBO;
import com.fhd.ra.business.assess.quaassess.CountAssessBO;
import com.fhd.ra.business.assess.risktidy.AssessReportBO;

/**
 * @decs 风险评价报考模板核心业务类
 * @author 邓广义
 *
 */
@Service
public class RiskAssessReportTemplateBO {
	
	@Autowired
	private RiskAssessReportTemplateDAO o_riskAssessReportTemplateDAO;
	
	@Autowired
	private RiskAssessReportDAO o_riskAssessReportDAO;
	
	@Autowired
	private RAssessPlanBO o_assessPlanBO;
	
	@Autowired
	private CountAssessBO o_countAssessBO;
	
	@Autowired
	private AssessReportBO o_assessReportBO;
	/**
	 * 根据数据字典获取模板分类
	 * 作为tree的数据源
	 * @return
	 */
	
	public Map<String,Object> findReportTemplateTree(){
		// 根据数据字典获取报告分类信息 现为写死的json
		return null;
	}
	/**
	 * 保存模板实体
	 * @param templateData
	 * @param tempName 
	 * @param tempCode 
	 * @throws Exception
	 */
	@Transactional
	public void saveReportTemplate(String id,String templateData,String type, String tempCode, String tempName)throws Exception{
		
       if(StringUtils.isNotBlank(type)){
    	   RiskAssessReportTemplate rart = null;
    	   if(StringUtils.isNotBlank(id)){
    		   //更新
    		   rart = this.o_riskAssessReportTemplateDAO.get(id);
    	   }else{
    		   //保存
    		   rart = new RiskAssessReportTemplate();
    		   rart.setId(Identities.uuid());
    		   rart.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
    		   rart.setTemplateType(new DictEntry(type));
    	   }
    	   rart.setCode(tempCode);
    	   rart.setName(tempName);
    	   if(StringUtils.isNotBlank(templateData)){
    		   rart.setTemplateData(templateData.getBytes("GBK"));
    	   }
    	   this.o_riskAssessReportTemplateDAO.merge(rart);
        }
        


		
	}
	/**
	 * 修改报告模板实体
	 * @param id
	 * @param templateData
	 * @throws UnsupportedEncodingException
	 */
	@Transactional
	public void updateReportTemplate(String id,String templateData) throws UnsupportedEncodingException{
		if( !StringUtils.isBlank(id)){
			RiskAssessReportTemplate rart =  this.o_riskAssessReportTemplateDAO.get(id);
			if( !StringUtils.isBlank(templateData)){
				rart.setTemplateData(templateData.getBytes("GBK"));
				this.o_riskAssessReportTemplateDAO.merge(rart);
			}
			
		}
	}
	/**
	 * 根据报告类型
	 * 获取报告模板列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskAssessReportTemplate> findReportTemplateListByType(String templateType){
		Criteria criteria = this.o_riskAssessReportTemplateDAO.createCriteria();
		criteria.add(Restrictions.eq("templateType.id", templateType));
		List<RiskAssessReportTemplate> list = criteria.list();
		return list;
	}
	/**
	 * 设置默认模板
	 * 一个类型只能有一个默认模板
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void setDefaultReportTemplate(String id) {
		if(StringUtils.isNotBlank(id)){
			RiskAssessReportTemplate rart = (RiskAssessReportTemplate) this.o_riskAssessReportTemplateDAO.get(id);
			String type = rart.getTemplateType().getId();
			//把原来的设置为非默认
			Criteria criteria = this.o_riskAssessReportTemplateDAO.createCriteria();
			criteria.add(Restrictions.eq("templateType.id", type));
			criteria.add(Restrictions.eq("isDefault", true));
			List<RiskAssessReportTemplate> list = criteria.list();
			if(null!=list && list.size()==1){
				RiskAssessReportTemplate riskAssessReportTemplate = list.get(0);
				riskAssessReportTemplate.setDefault(false);
				this.o_riskAssessReportTemplateDAO.merge(riskAssessReportTemplate);
			}
			//把要设置的设置为默认
			rart.setDefault(true);
			this.o_riskAssessReportTemplateDAO.merge(rart);
		}
		
	}
	/**
	 * 删除模板
	 * @param id
	 * @return
	 */
	@Transactional
	public void delReportTemplate(String id) {
		if(StringUtils.isNotBlank(id)){
			String [] ids = id.split("\\,");
			for(String i : ids){
				this.o_riskAssessReportTemplateDAO.delete(i);
			}
		}
	}
	/**
	 * 根据Id查询实体
	 * @param id
	 * @return
	 */
	public RiskAssessReportTemplate findReportTemplateById(String id){
		if(StringUtils.isNotBlank(id)){
			return (RiskAssessReportTemplate) this.o_riskAssessReportTemplateDAO.get(id);
		}
		return null;
	}
	/**
	 * 获得某报告模板类型下
	 * 的默认模板
	 * @param reportType
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public RiskAssessReportTemplate findReportDefaultTemplateByType(String reportType) {
		if(StringUtils.isNotBlank(reportType)){
			Criteria criterion = this.o_riskAssessReportTemplateDAO.createCriteria();
			criterion.add(Restrictions.eq("templateType.id", reportType));
			criterion.add(Restrictions.eq("isDefault", true));
			List<RiskAssessReportTemplate> list = criterion.list();
			if(null!=list&&list.size()>0){
				return list.get(0);
			}
		}
		return null;
	}
	/**
	 * 根据评价计划的ID 获得 此评价计划的报告
	 * 如果没有 则从报告模板中获得 此类型的默认模板并保存到业务表中
	 * @param AssessPlanId
	 * @return
	 */
	@Transactional
	public RiskAssessReport findReportByAssessPlanId(String AssessPlanId){
		RiskAssessReport rar = null;
		if(StringUtils.isNotBlank(AssessPlanId)){
			rar = this.findRiskAssessReportByAssessId(AssessPlanId);
			if(null!=rar){
				return rar;
			}else{
				RiskAssessReportTemplate rart = this.findReportDefaultTemplateByType("risk_assess_report_template");
				if(null!=rart){
					rar = new RiskAssessReport();
					rar.setReportData(rart.getTemplateData());
					rar.setCreate_date(new Date());
					String id = Identities.uuid();
					rar.setId(id);
					RiskAssessPlan rap = o_assessPlanBO.findRiskAssessPlanById(AssessPlanId);
					rar.setRiskAssessPlan(rap);
					rar.setStatus("onSave");
					this.o_riskAssessReportDAO.merge(rar);
				return this.o_riskAssessReportDAO.get(id);
				}
			}
		}
		return null;
	}
	/**
	 * 保存or修改评估报告
	 * @param assessPlanId
	 * @param reportData
	 * @param id
	 */
	@Transactional
	public void saveAssessReport(String id, String assessPlanId, String reportData,HttpServletRequest req) throws Exception{
		RiskAssessReport rar = null;
		if(StringUtils.isNotBlank(id)){
			//更新
			 rar = (RiskAssessReport) this.o_riskAssessReportDAO.get(id);
		}else{
			//保存
			rar = new RiskAssessReport();
			rar.setId(Identities.uuid());
			if(StringUtils.isNotBlank(assessPlanId)){
				RiskAssessPlan rap = o_assessPlanBO.findRiskAssessPlanById(assessPlanId);
				if(null!=rap){
					rar.setRiskAssessPlan(rap);
				}
			}
			rar.setCreate_date(new Date());
		}
		if(StringUtils.isNotBlank(reportData)){
			 rar.setReportData(reportData.getBytes("GBK"));
		}
		String stream = previewAssessReport(req,assessPlanId,reportData);
		String content = "<html>" + stream + "</html>"; 
		
        byte b[] = content.getBytes("GBK");  
        ByteArrayInputStream bais = new ByteArrayInputStream(b);  
        POIFSFileSystem poifs = new POIFSFileSystem();  
        DirectoryEntry directory = poifs.getRoot();  
        directory.createDocument("WordDocument", bais);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        poifs.writeFilesystem(outputStream);
        rar.setReportDoc(outputStream.toByteArray());
		this.o_riskAssessReportDAO.merge(rar);
	}
	/**
	 * 预览报告 替换${***}
	 * @param assessPlanId
	 * @param reportData
	 * @throws Exception 
	 */
	public String previewAssessReport(HttpServletRequest req,String assessPlanId, String reportData) throws Exception {
		String stream = "";
		stream = this.replaceCustomTagToHtmlTag(assessPlanId,reportData);//替换${}为html标签
		stream = this.replaceCustomTagToJPG(req,assessPlanId,stream);//替换${}为图片 试用 jfreeChar
		return stream;
	}
	/**
	 * 替换${}为图片 试用 jfreeChar
	 * @param stream
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private String replaceCustomTagToJPG(HttpServletRequest req,String assessPlanId,String stream) throws Exception {
		if(StringUtils.isNotBlank(assessPlanId)){
			double[][] data = new double[][] {
					{7, 4, 1, 2,5,4},		
					{23, 6, 9, 10,12,1},		
					{4, 7, 1, 7,8,9},		
					};
			
			String[] rowKeys = { "a","b", "c"  };		
			String[] columnKeys = {"1","2","3","4","5","6"};
			CategoryDataset processRiskDataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data);
			//获得流程风险分析柱状图
			String processRisk = (String) FHDJfreeChar.createJFreeChar(req,new JFCBaseProper(),JFCBaseProper.HIS,processRiskDataset).get("url");
			//获得风险分类饼状图
			DefaultPieDataset pieDataset = new DefaultPieDataset();	
			List<Object[]> list = o_countAssessBO.getRiskCategoryNum();
			for(Object[] obj : list){
				if(null!=obj[0]){
					pieDataset.setValue((Comparable) obj[0],(Number)obj[1]);		
				}
			}
			String riskCagetory = (String) FHDJfreeChar.createJFreeChar(req,new JFCBaseProper(),JFCBaseProper.PIE,pieDataset).get("url");
			
			double [][] targetData = new double[][]{
					{16},{5},{83},{22},{49},{35}	
			};
			String[] targetRowKeys = {"SaftyWork","empCash","control","total","cashTotal","xxx"};
			String [] targetColumnKeys = {""};
			CategoryDataset targetRiskDataset = DatasetUtilities.createCategoryDataset(targetRowKeys, targetColumnKeys, targetData);
			String targetRisk = (String) FHDJfreeChar.createJFreeChar(req,new JFCBaseProper(),JFCBaseProper.HIS,targetRiskDataset).get("url");
			String newData = stream ;
			newData = StringUtils.replace(newData, "${流程风险分析}", "<img src=\""+processRisk+"\">");
			newData = StringUtils.replace(newData, "${目标风险分析}", "<img src=\""+targetRisk+"\">");
			newData = StringUtils.replace(newData, "${风险分类分析}", "<img src=\""+riskCagetory+"\">");
			return newData;
		}
		return "";
	}
	/**
	 * 替换${}成html标签
	 * @param reportData 
	 * @param assessPlanId 
	 */
	public String replaceCustomTagToHtmlTag(String assessPlanId, String reportData){
		if(StringUtils.isNotBlank(assessPlanId)){
			//获得一级风险数量
			Map<String,Object> map = this.o_countAssessBO.getRiskNumByAssessPlanIdAndType(assessPlanId, 1);
			String newHighRisk = ((BigInteger) map.get("totalCount")).toString();
			//获得二级风险数量
			String secLevelRiskNum = ((BigInteger)this.o_countAssessBO.getRiskNumByAssessPlanIdAndType(assessPlanId, 2).get("totalCount")).toString();
			//获得三级风险数量
			String thdLevelRiskNum = ((BigInteger)this.o_countAssessBO.getRiskNumByAssessPlanIdAndType(assessPlanId, 3).get("totalCount")).toString();
			//获得风险事件数量
			String riskEventNum = ((BigInteger)this.o_countAssessBO.getRiskNumByAssessPlanIdAndType(assessPlanId, 4).get("totalCount")).toString();
			//新高风险列表
			String companyid = UserContext.getUser().getCompanyid();
			ArrayList<HashMap<String, String>> newHighRisks = o_assessReportBO.findRbsOrReList(true, "rbs",assessPlanId, companyid);//新高风险列表
//			
			ArrayList<HashMap<String, String>> riskEvents = o_assessReportBO.findRbsOrReList(false, "re",assessPlanId, companyid);//风险事件列表
//			
			ArrayList<HashMap<String, String>> riskLists = o_assessReportBO.findRbsOrReList(false, "rbs",assessPlanId, companyid);//风险列表
			
			/**
			 * 新高风险列表 start
			 */
			int newHighRowNum = 1;
			StringBuilder newHighbuilder = new StringBuilder();
			newHighbuilder.append(ReportTagConstant.TABLE_START)
			.append(ReportTagConstant.TABLE_NAME_RISK_ASSESS_RESULT)
			.append(ReportTagConstant.TR_START)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("序号")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("新高风险")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("责任部门")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("相关部门")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("发生可能性")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("影响程度")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("风险水平")
				.append(ReportTagConstant.HEADER_TD_END)
			.append(ReportTagConstant.TR_END);
			for(HashMap<String, String> hashmap :newHighRisks){
				String riskName = hashmap.get("riskName");//风险名称
				String responsDept = hashmap.get("zr");//责任部门
				String relaDept = hashmap.get("xg"); //相关部门
				String happenPossible = hashmap.get("fs"); //发生可能性
				String effect = hashmap.get("yx");//影响程度
				String riskLevel = hashmap.get("level");; //风险水平
				newHighbuilder.append(ReportTagConstant.TR_START)
				
					.append(ReportTagConstant.BODY_TD_START)
					.append(newHighRowNum)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskName)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(responsDept)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(relaDept)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(happenPossible)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(effect)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskLevel)
					.append(ReportTagConstant.BODY_TD_END)
				.append(ReportTagConstant.TR_END);
				
				newHighRowNum++;
			}
			newHighbuilder.append(ReportTagConstant.TABLE_END);
			/**
			 * 新高风险列表 end
			 */
			
			/**
			 * 风险事件列表  start
			 */
			int riskEventRowNum = 1;
			StringBuilder riskEventbuilder = new StringBuilder();
			riskEventbuilder.append(ReportTagConstant.TABLE_START)
			.append(ReportTagConstant.TABLE_NAME_RISK_EVENT_ORDER)
			.append(ReportTagConstant.TR_START)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("序号")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("风险事件")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("责任部门")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("相关部门")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("发生可能性")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("影响程度")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("风险水平")
				.append(ReportTagConstant.HEADER_TD_END)
			.append(ReportTagConstant.TR_END);
			for(HashMap<String, String> hashmap :riskEvents){
				String riskName = hashmap.get("riskName");//风险名称
				String responsDept = hashmap.get("zr");//责任部门
				String relaDept = hashmap.get("xg"); //相关部门
				String happenPossible = hashmap.get("fs"); //发生可能性
				String effect = hashmap.get("yx");//影响程度
				String riskLevel = hashmap.get("level");; //风险水平
				riskEventbuilder.append(ReportTagConstant.TR_START)
				
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskEventRowNum)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskName)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(responsDept)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(relaDept)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(happenPossible)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(effect)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskLevel)
					.append(ReportTagConstant.BODY_TD_END)
				.append(ReportTagConstant.TR_END);
				
				riskEventRowNum++;
			}
			riskEventbuilder.append(ReportTagConstant.TABLE_END);
			/**
			 * 新高风险列表 end
			 */
			/**
			 * 风险列表  start
			 */
			int riskRowNum = 1;
			StringBuilder riskbuilder = new StringBuilder();
			riskbuilder.append(ReportTagConstant.TABLE_START)
			.append(ReportTagConstant.TABLE_NAME_RISK_ORDER)
			.append(ReportTagConstant.TR_START)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("序号")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("风险名称")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("责任部门")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("相关部门")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("发生可能性")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("影响程度")
				.append(ReportTagConstant.HEADER_TD_END)
				.append(ReportTagConstant.HEADER_TD_START)
				.append("风险水平")
				.append(ReportTagConstant.HEADER_TD_END)
			.append(ReportTagConstant.TR_END);
			for(HashMap<String, String> hashmap :riskLists){
				String riskName = hashmap.get("riskName");//风险名称
				String responsDept = hashmap.get("zr");//责任部门
				String relaDept = hashmap.get("xg"); //相关部门
				String happenPossible = hashmap.get("fs"); //发生可能性
				String effect = hashmap.get("yx");//影响程度
				String riskLevel = hashmap.get("level");; //风险水平
				riskbuilder.append(ReportTagConstant.TR_START)
				
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskRowNum)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskName)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(responsDept)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(relaDept)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(happenPossible)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(effect)
					.append(ReportTagConstant.BODY_TD_END)
					
					.append(ReportTagConstant.BODY_TD_START)
					.append(riskLevel)
					.append(ReportTagConstant.BODY_TD_END)
				.append(ReportTagConstant.TR_END);
				
				riskRowNum++;
			}
			riskbuilder.append(ReportTagConstant.TABLE_END);
			/**
			 * 风险列表 end
			 */
			String newData = reportData;
			newData = StringUtils.replace(newData, "${一级风险数量}", newHighRisk);
			newData = StringUtils.replace(newData, "${二级风险数量}", secLevelRiskNum);
			newData = StringUtils.replace(newData, "${三级风险数量}", thdLevelRiskNum);
			newData = StringUtils.replace(newData, "${风险事件数量}", riskEventNum);
			newData = StringUtils.replace(newData, "${新高风险列表}", newHighbuilder.toString());
			newData = StringUtils.replace(newData, "${风险事件列表}", riskEventbuilder.toString());
			newData = StringUtils.replace(newData, "${风险列表}", riskbuilder.toString());
			return newData;
		}
		return "";
	}
	/**
	 * 根据Id查询实体
	 * @param id
	 * @return
	 */
	public RiskAssessReport findAssessReportById(String id){
		if(StringUtils.isNotBlank(id)){
			return this.o_riskAssessReportDAO.get(id);
		}
		return null;
	}
	/**
	 * 查询风险评估计划下的风险评估报告
	 * @param AssessPlanId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RiskAssessReport findRiskAssessReportByAssessId(String AssessPlanId){
		if(StringUtils.isNotBlank(AssessPlanId)){
			Criteria criterion = this.o_riskAssessReportDAO.createCriteria();
			criterion.add(Restrictions.eq("riskAssessPlan.id", AssessPlanId));
			List<RiskAssessReport> list = criterion.list();
			if(null!=list&&list.size()>0){
				return list.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 设置默认模板
	 * 一个类型只能有一个默认模板
	 * @param type 模板类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean hasDefautTemplate(String type) {
		Criteria criteria = this.o_riskAssessReportTemplateDAO.createCriteria();
		criteria.add(Restrictions.eq("templateType.id", type));
		criteria.add(Restrictions.eq("isDefault", true));
		List<RiskAssessReportTemplate> list = criteria.list();
		if(list.size()>0){
			return true;
		}else{
			return false;
		}
	}
}
