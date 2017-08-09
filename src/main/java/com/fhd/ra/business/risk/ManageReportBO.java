package com.fhd.ra.business.risk;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.ManageReportDAO;
import com.fhd.dao.risk.ManageReportRelaFileDAO;
import com.fhd.dao.risk.ManageReportRelaRiskDAO;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.dao.sys.dic.DictTypeDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.risk.ManageReport;
import com.fhd.entity.risk.ManageReportRelaFile;
import com.fhd.entity.risk.ManageReportRelaRisk;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.web.form.risk.ManageReportForm;
import com.fhd.sys.business.dic.DictBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 应急预案管理BO
 * 
 * @author 张健
 * @date 2014-1-4
 * @since Ver 1.1
 */
@Service
public class ManageReportBO {

    @Autowired
    private DictTypeDAO o_dictTypeDAO;
    @Autowired
    private ManageReportDAO o_manageReportDAO;
    @Autowired
    private DictBO o_dictBO;
    @Autowired
    private EmployeeDAO o_employeeDAO;
    @Autowired
    private ManageReportRelaRiskDAO o_manageReportRelaRiskDAO;
    @Autowired
    private ManageReportRelaFileDAO o_manageReportRelaFileDAO;
    @Autowired
    private RiskAssessPlanDAO o_riskAssessPlanDAO;
    
    /**
     * 查询应急预案列表
     * 
     * @author 张健
     * @param typeId 应急预案类型
     * @return Page<ManageReport> 应急预案PAGE对象
     * @date 2014-1-3
     * @since Ver 1.1
     */
    public Page<ManageReport> findmanagereportlist(Object[] typeIds,Page<ManageReport> page,String query, String sort, String dir){
        return o_manageReportDAO.findmanagereportlist(typeIds,page,query,sort,dir);
    }
    
    /**
     * 应急预案保存
     * 
     * @author 张健
     * @param reportId 应急预案ID
     * @param typeId 应急预案类型
     * @param content 描述
     * @param businessId 计划ID
     * @param archiveStatus 审批状态
     * @return VOID
     * @throws UnsupportedEncodingException 
     * @date 2014-1-3
     * @since Ver 1.1
     */
    @Transactional
    public void savemanagereport(String reportId, String typeId, String content, ManageReportForm form,String businessId,String archiveStatus) throws ParseException, UnsupportedEncodingException{
        String empId = UserContext.getUser().getEmpid();
        String companyId = UserContext.getUser().getCompanyid();
        ManageReport manageReport = new ManageReport();
        if(StringUtils.isBlank(reportId)){
            //新增
            manageReport.setId(UUID.randomUUID().toString());
            manageReport.setCreateBy(o_employeeDAO.get(empId));
            manageReport.setCreateDate(new Date());
            if(StringUtils.isBlank(businessId)){
                manageReport.setReportType(o_dictBO.findDictEntryById(typeId));
            }else{
                RiskAssessPlan riskAssessPlan = o_riskAssessPlanDAO.get(businessId);
                manageReport.setReportType(o_dictBO.findDictEntryById(riskAssessPlan.getContingencyType()));//取计划中存的应急元类型
                manageReport.setRiskAssessPlan(riskAssessPlan);
            }
        }else{
            //修改
            manageReport = o_manageReportDAO.get(reportId);
        }
        manageReport.setReportName(form.getReportName());
        manageReport.setReportCode(form.getReportCode());
        
        //责任部门
        if(StringUtils.isNotBlank(form.getRespDeptName())){
            JSONArray jsonArray =JSONArray.fromObject(form.getRespDeptName());
            String jsonOrg = ((JSONObject)jsonArray.get(0)).get("deptid").toString();
            SysOrganization orgtemp = new SysOrganization();
            orgtemp.setId(jsonOrg);
            manageReport.setOccuredOrg(orgtemp);
            String jsonEmp = ((JSONObject)jsonArray.get(0)).get("empid").toString();
            if(StringUtils.isNotBlank(jsonEmp)){
                SysEmployee emptemp = new SysEmployee();
                emptemp.setId(jsonEmp);
                manageReport.setEmployee(emptemp);
            }
        }
        //应急预案描述
        if(StringUtils.isNotBlank(content)){
            manageReport.setContent(content);
        }
        manageReport.setLastModifyTime(new Date());
        
        if("saved".equals(archiveStatus)){
            manageReport.setStatus(Contents.RISK_STATUS_SAVED);//待提交
        }else if("examine".equals(archiveStatus)){
            manageReport.setStatus(Contents.RISK_STATUS_EXAMINE);//审批中
        }else if("archived".equals(archiveStatus)){
            manageReport.setStatus(Contents.RISK_STATUS_ARCHIVED);//已归档
        }
        manageReport.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
        //开始，结束时间
        if(StringUtils.isNotBlank(form.getStartDateStr())){
            manageReport.setStartDate(DateUtils.parseDate(form.getStartDateStr(), "yyyy-MM-dd"));
        }
        if(StringUtils.isNotBlank(form.getEndDateStr())){
            manageReport.setEndDate(DateUtils.parseDate(form.getEndDateStr(), "yyyy-MM-dd"));
        }
        manageReport.setSort(form.getSort());
        SysOrganization org = new SysOrganization();
        org.setId(companyId);
        manageReport.setCompany(org);
        o_manageReportDAO.merge(manageReport);
        //先删除关联的风险，再保存关联风险
        o_manageReportRelaRiskDAO.createSQLQuery(" delete from T_MANAGE_REPORT_RELA_RISK where REPORT_ID = ? ", reportId).executeUpdate();
        if(StringUtils.isNotBlank(form.getRelateRisk())){
            JSONArray jsonArray =JSONArray.fromObject(form.getRelateRisk());
            String jsonRisk = ((JSONObject)jsonArray.get(0)).get("id").toString();
            Risk risk = new Risk();
            risk.setId(jsonRisk);
            ManageReportRelaRisk manageReportRelaRisk = new ManageReportRelaRisk();
            manageReportRelaRisk.setId(UUID.randomUUID().toString());
            manageReportRelaRisk.setManageReport(manageReport);
            manageReportRelaRisk.setRisk(risk);
            o_manageReportRelaRiskDAO.merge(manageReportRelaRisk);
        }
        //保存关联的文件
        this.saveReportRelaFiles(manageReport, form);
    }
    
    /**
     * 查询应急预案信息
     * 
     * @author 张健
     * @param reportId 应急预案ID
     * @return ManageReport 应急预案实体
     * @date 2014-1-3
     * @since Ver 1.1
     */
    public ManageReport finemanagereportinfo(String reportId) throws Exception{
        ManageReport manageReport = o_manageReportDAO.get(reportId);
        return manageReport;
    }
    
    /**
     * 应急预案删除
     * 
     * @author 张健
     * @param ids 应急预案ID
     * @return VOID
     * @date 2014-1-3
     * @since Ver 1.1
     */
    @Transactional
    public void deletemanagereport(String ids){
        String[] idArray = ids.split(",");
        List<String> idList = new ArrayList<String>();
        for(String id : idArray){
            idList.add(id);
        }
        //删除应急预案关联的风险
        SQLQuery query = o_manageReportRelaRiskDAO.createSQLQuery(" delete from T_MANAGE_REPORT_RELA_RISK where REPORT_ID in (:idList) ");
        query.setParameterList("idList", idList);
        query.executeUpdate();
        //删除应急预案
        SQLQuery query2 = o_manageReportDAO.createSQLQuery(" delete from T_MANAGE_REPORT where ID in (:idList) ");
        query2.setParameterList("idList", idList);
        query2.executeUpdate();
    }
    
    /**
     * 重新保存应急预案关联的文件
     * 
     * @author 张健
     * @param manageReport 应急预案实体
     * @param form 获取文件ID
     * @return VOID
     * @date 2014-3-5
     * @since Ver 1.1
     */
    @Transactional
    public void saveReportRelaFiles(ManageReport manageReport, ManageReportForm form){
        o_manageReportRelaFileDAO.createSQLQuery(" delete from t_manage_report_rela_file where report_id = ? ", form.getId()).executeUpdate();
        if(null != form.getFileIds()){
            String[] IdsList = form.getFileIds().split(",");
            FileUploadEntity fileEntity=new FileUploadEntity();
            for(int i=0;i<IdsList.length;i++){
                if(StringUtils.isNotBlank(IdsList[i])){
                    ManageReportRelaFile mrRelaReport=new ManageReportRelaFile();
                    fileEntity.setId(IdsList[i]);
                    mrRelaReport.setFile(fileEntity);
                    mrRelaReport.setId(Identities.uuid());
                    mrRelaReport.setReport(manageReport);
                    o_manageReportRelaFileDAO.merge(mrRelaReport);
                }
            }
        }
    }
    
    /**
     * 查询应急预案关联的文件
     * 
     * @author 张健
     * @param reportId 应急预案ID
     * @return List<ManageReportRelaFile> 应急预案关联文件列表
     * @date 2014-3-5
     * @since Ver 1.1
     */
    @SuppressWarnings("unchecked")
    public List<ManageReportRelaFile> findDManageReportRelaFileByReId(String reportId) {
        Criteria criteria = o_manageReportRelaFileDAO.createCriteria();
        criteria.add(Restrictions.eq("report.id", reportId));
        return criteria.list();
    }
}