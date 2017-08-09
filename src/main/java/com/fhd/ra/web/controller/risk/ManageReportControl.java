package com.fhd.ra.web.controller.risk;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.RiskAssessPlanDAO;
import com.fhd.entity.assess.formulatePlan.RiskAssessPlan;
import com.fhd.entity.risk.ManageReport;
import com.fhd.entity.risk.ManageReportRelaFile;
import com.fhd.entity.risk.ManageReportRelaRisk;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.templatemanage.TemplateManage;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.response.ResponseSolutionBO;
import com.fhd.ra.business.risk.ManageReportBO;
import com.fhd.ra.web.form.assess.formulateplan.RiskAssessPlanForm;
import com.fhd.ra.web.form.risk.ManageReportForm;
import com.fhd.sys.business.organization.EmpGridBO;
import com.fhd.sys.business.organization.OrgGridBO;
import com.fhd.sys.business.templatemanage.TemplateManageBO;

/**
 * 应急预案管理
 * 
 * @author 张健
 * @date 2014-1-2
 * @since Ver 1.1
 */
@Controller
public class ManageReportControl {
    @Autowired
    private ManageReportBO o_manageReportBO;
    @Autowired
    private TemplateManageBO o_templateManageBO;
    @Autowired
    private RiskAssessPlanDAO o_riskAssessPlanDAO;
    @Autowired
    private ResponseSolutionBO o_responseSolutionBO;
    @Autowired
    private OrgGridBO o_OrgGridBO;
    @Autowired
    private RiskAssessPlanBO o_riskAssessPlanBO;
    @Autowired
    private EmpGridBO o_empGridBO;
    
    /**
     * 查询应急预案列表
     * 
     * @author 张健
     * @param typeId 报告类型
     * @return Map 查询结果集
     * @date 2014-1-2
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/managereport/findmanagereportlist.f")
    public Map<String, Object> findmanagereportlist(String typeId,int start, int limit, String query, String sort){
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        Page<ManageReport> page = new Page<ManageReport>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        String dir = "desc";
        String sortColumn = "startDate";
        if (StringUtils.isNotBlank(sort)) {
            JSONArray jsonArray = JSONArray.fromObject(sort);
            if (jsonArray.size() > 0) {
                JSONObject jsobj = jsonArray.getJSONObject(0);
                sortColumn = jsobj.getString("property");//按照哪个字段排序
                dir = jsobj.getString("direction");//排序方向
            }
        }
        Object[] typeIds = new Object[1];
        typeIds[0] = typeId;
        page = o_manageReportBO.findmanagereportlist(typeIds,page,query,sortColumn,dir);
        List<ManageReport> list = page.getResult();//查询的应急预案结果集
        if(null != list){
            for(ManageReport mr : list){
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("id", mr.getId());
                data.put("reportName", mr.getReportName());
                Set<ManageReportRelaRisk> mrSet = mr.getManageReportRelaRisk();
                String risk = "";
                for(ManageReportRelaRisk mrrr : mrSet){
                    risk = mrrr.getRisk().getName();
                }
                data.put("risk", risk);
                data.put("createDate", null == mr.getCreateDate()?"":mr.getCreateDate().toString().substring(0, 10));
                data.put("occuredorg", null == mr.getOccuredOrg()?"":mr.getOccuredOrg().getOrgname());
                data.put("employee", null == mr.getEmployee()?"":mr.getEmployee().getEmpname());
                data.put("status", mr.getStatus());
                datas.add(data);
            }
        }
        map.put("datas", datas);
        map.put("totalCount",page.getTotalItems());
        return map;
    }
    
    
    /**
     * 应急预案保存
     * 
     * @author 张健
     * @param reportId 应急预案ID
     * @param typeId 应急预案类型
     * @param comment 应急预案描述
     * @param businessId 计划ID
     * @param archiveStatus 审批状态
     * @return Map 操作结果集
     * @date 2014-1-3
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/managereport/savemanagereport.f")
    public Map<String, Object> savemanagereport(String reportId, String typeId, String comment, ManageReportForm form,String businessId,String archiveStatus) throws Exception{
        o_manageReportBO.savemanagereport(reportId,typeId,comment,form,businessId,archiveStatus);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        return map;
    }
    
    /**
     * 查询应急预案信息
     * 
     * @author 张健
     * @param reportId 应急预案ID
     * @return Map 查询结果集
     * @date 2014-1-3
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/managereport/finemanagereportinfo.f")
    public Map<String, Object> finemanagereportinfo(String reportId) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ManageReport mr = o_manageReportBO.finemanagereportinfo(reportId);//应急预案实体
        data.put("reportName", mr.getReportName());
        data.put("reportCode", mr.getReportCode());
        data.put("sort", mr.getSort());
        data.put("startDateStr", null == mr.getStartDate()?"":mr.getStartDate().toString().substring(0, 10));
        data.put("endDateStr", null == mr.getEndDate()?"":mr.getEndDate().toString().substring(0, 10));
        data.put("content", mr.getContent()==null?"":mr.getContent());
        JSONArray respDeptName = new JSONArray();
        JSONArray relateRisk = new JSONArray();
        String respDeptNameStr = "";
        StringBuffer relateRiskStr = new StringBuffer();
        if(null != mr.getOccuredOrg()){
            JSONObject jsoDept = new JSONObject();
            jsoDept.put("deptid", mr.getOccuredOrg().getId());
            jsoDept.put("deptno", mr.getOccuredOrg().getOrgcode());
            jsoDept.put("deptname", mr.getOccuredOrg().getOrgname());
            respDeptNameStr = respDeptNameStr + mr.getOccuredOrg().getOrgname();
            if(null != mr.getEmployee()){
                jsoDept.put("empid", mr.getEmployee().getId());
                jsoDept.put("empno", mr.getEmployee().getEmpcode());
                jsoDept.put("empname", mr.getEmployee().getEmpname());
                respDeptNameStr = respDeptNameStr + "(" + mr.getEmployee().getEmpname() + ")";
            }else{
                jsoDept.put("empid", "");
            }
            respDeptName.add(jsoDept);
        }
        data.put("respDeptNameStr", respDeptNameStr);//责任部门名称
        data.put("respDeptName", respDeptName.size()==0?"":respDeptName.toString());//责任部门字符串
        if(mr.getManageReportRelaRisk().size() != 0){
            JSONObject jsoRisk = new JSONObject();
            Set<ManageReportRelaRisk> setMr = mr.getManageReportRelaRisk();
            for(ManageReportRelaRisk mrrr : setMr){
                if(null != mrrr.getRisk()){
                    jsoRisk.put("id", mrrr.getRisk().getId());
                    relateRiskStr.append(mrrr.getRisk().getName());
                    relateRiskStr.append(",");
                }else{
                    jsoRisk.put("id", "");
                }
            }
            relateRisk.add(jsoRisk);
        }
        data.put("relateRiskStr", relateRiskStr.length()==0?"":relateRiskStr.substring(0,relateRiskStr.length()-1));//风险ID
        data.put("relateRisk", relateRisk.size()==0?"":relateRisk.toString());//风险名称
        List<ManageReportRelaFile> fileList = o_manageReportBO.findDManageReportRelaFileByReId(reportId);
        String fileIds = "";
        List<Map<String,String>> flList = new ArrayList<Map<String,String>>();
        if(null != fileList && fileList.size() > 0 ){
            Map<String,String> fileMap = null;
            for(ManageReportRelaFile mrrf : fileList){
                fileMap = new HashMap<String,String>();
                fileIds = fileIds + "," + mrrf.getFile().getId();
                fileMap.put("fileId", mrrf.getFile().getId());
                fileMap.put("fileName", mrrf.getFile().getNewFileName());
                flList.add(fileMap);
            }
            JSONArray json = JSONArray.fromObject(flList); 
            data.put("fileId", json.toString());
        }
        data.put("fileIds", fileIds);//文件ID
        
        map.put("data", data);
        map.put("success", true);
        return map;
    }
    
    /**
     * 删除应急预案
     * 
     * @author 张健
     * @param ids 删除ID
     * @return Map 操作结果集
     * @date 2014-1-3
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/managereport/deletemanagereport.f")
    public Map<String, Object> deletemanagereport(String ids){
        Map<String, Object> map = new HashMap<String, Object>();
        o_manageReportBO.deletemanagereport(ids);
        map.put("success", true);
        return map;
    }
    
    /**
     * 查询应急预案模板
     * 
     * @author 张健
     * @param typeId 应急预案类型
     * @param businessId 计划ID
     * @return Map 查询结果集
     * @date 2014-2-12
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping("/managereport/finemanagereporttemplate.f")
    public Map<String, Object> finemanagereporttemplate(String businessId,String typeId){
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        String type = "";
        if(StringUtils.isNotBlank(typeId)){
            //应急预案综合页面，有类型ID
            type = typeId;
        }else{
            RiskAssessPlan riskAssessPlan = o_riskAssessPlanDAO.get(businessId);
            //通过应急预案计划的方式或者类型
            type = riskAssessPlan.getContingencyType();
        }
        String desc = "";
        if(StringUtils.isNotBlank(type)){
            String templateType = "";
            if("preplan_event_risk".equals(type)){
                templateType = "template_manage_preplan_event_risk";
            }else if("preplan_event_emergency".equals(type)){
                templateType = "template_manage_preplan_event_emergency";
            }
            TemplateManage templateManage = o_templateManageBO.findDefaultTemplateByentryId(templateType);//查询模板信息
            if(null != templateManage){
                desc = templateManage.getContent();
            }
        }
        data.put("desc", desc);
        map.put("data", data);
        map.put("success", true);
        return map;
    }
    
    /**
     * 保存应急预案计划
     * 
     * @author 张健
     * @param executionId 流程ID
     * @return Map 操作结果集
     * @date 2014-3-3
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/managereport/savemanagereportplan.f")
    public Map<String, Object> saveResponsePlan(RiskAssessPlanForm planForm,String id,String executionId) throws Exception{
        String companyId = UserContext.getUser().getCompanyid();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> inmap = new HashMap<String, Object>();
        RiskAssessPlan assessPlan = new RiskAssessPlan();
        assessPlan.setId(Identities.uuid());
        assessPlan.setPlanName(planForm.getPlanName());
        assessPlan.setPlanCode(planForm.getPlanCode());
        assessPlan.setCompany(o_OrgGridBO.findOrganizationByOrgId(companyId));//保存当前机构
        assessPlan.setWorkType(planForm.getWorkType());//工作类型
        assessPlan.setRangeReq(planForm.getRangeReq());//范围要求
        assessPlan.setCollectRate(planForm.getCollectRate());//采集频率
        assessPlan.setPlanType("riskContingencyPlanTotal");//计划类型
        assessPlan.setWorkTage(planForm.getWorkTage());//工作目标
        assessPlan.setPlanCreatTime(new Date());//创建时间
        
        assessPlan.setTemplate(null);
        assessPlan.setTemplateType(null);
        if(StringUtils.isNotBlank(planForm.getBeginDataStr())){//开始时间
            assessPlan.setBeginDate(DateUtils.parseDate(planForm.getBeginDataStr(), "yyyy-MM-dd"));
        }
        if(StringUtils.isNotBlank(planForm.getEndDataStr())){//开始时间
            assessPlan.setEndDate(DateUtils.parseDate(planForm.getEndDataStr(), "yyyy-MM-dd"));
        }
        
        if(null != planForm.getContactName()){
            String conEmpId = "";
            JSONArray conArray = JSONArray.fromObject(planForm.getContactName());
            if(null != conArray && conArray.size()>0){
                conEmpId = (String)JSONObject.fromObject(conArray.get(0)).get("id");
            }
            SysEmployee conEmp = o_empGridBO.findEmpEntryByEmpId(conEmpId);
            if(null != conEmp){
                assessPlan.setContactPerson(conEmp);//保存联系人
            }
        }
        if(null != planForm.getResponsName()){
            String respEmpId = "";
            JSONArray respArray = JSONArray.fromObject(planForm.getResponsName());
            if(null != respArray && respArray.size()>0){
                respEmpId = (String)JSONObject.fromObject(respArray.get(0)).get("id");
            }
            SysEmployee respEmp = o_empGridBO.findEmpEntryByEmpId(respEmpId);
            if(null != respEmp){
                assessPlan.setResponsPerson(respEmp);//保存负责人
            }
        }
        assessPlan.setDealStatus(Contents.DEAL_STATUS_NOTSTART);//点保存按钮，处理状态为"未开始"
        assessPlan.setStatus(Contents.STATUS_SAVED);//保存：状态为"已保存"
        assessPlan.setContingencyType(planForm.getContingencyType());
        if(StringUtils.isNotBlank(id)){//修改
            String ids[] = id.split(",");
            assessPlan.setId(ids[0]);
            RiskAssessPlan plan = o_riskAssessPlanDAO.get(assessPlan.getId());
            if(null==assessPlan.getTemplateType()){
                if(null!=plan.getTemplate()){
                    assessPlan.setTemplate(plan.getTemplate());//评估模板
                    assessPlan.setTemplateType(plan.getTemplateType());//模板类型
                }else{
                    assessPlan.setTemplate(null);
                    assessPlan.setTemplateType(null);
                }
            }
            if(StringUtils.isNotBlank(executionId)){
                assessPlan.setDealStatus(plan.getDealStatus());//点保存按钮，处理状态为"未开始"
                assessPlan.setStatus(plan.getStatus());//保存：状态为"已保存"
            }
            o_riskAssessPlanBO.mergeRiskAssessPlan(assessPlan);
        }else{
            o_riskAssessPlanBO.saveRiskAssessPlan(assessPlan);//保存
        }
        inmap.put("planId", assessPlan.getId());
        map.put("data", inmap);
        map.put("success", true);
        return map;
    }
}