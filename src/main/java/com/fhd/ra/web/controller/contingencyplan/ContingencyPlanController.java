package com.fhd.ra.web.controller.contingencyplan;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.ra.business.assess.risktidy.ShowRiskTidyBO;
import com.fhd.ra.business.contingencyplan.ContingencyPlanBO;
import com.fhd.sys.business.orgstructure.OrganizationBO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ContingencyPlanController {

	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private ContingencyPlanBO o_contingencyPlanBO;
	@Autowired
	private OrganizationBO o_organizationBO;
	@Autowired
    private ShowRiskTidyBO o_showRiskTidyBO;
	@Autowired
	private EmployeeDAO o_employeeDAO;
	
	/**
     * 预案审批提交
     * */
    @ResponseBody
    @RequestMapping(value = "/contingencyplan/findcontingencyplanlist.f")
    public Map<String, Object> findContingencyPlanList(String executionId, String businessId, String query, String type){
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        List<Object[]> list = o_contingencyPlanBO.findContingencyPlanList(executionId,businessId,query,type);
        if(null != list){
            for(Object[] obs : list){
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("riskid", obs[0]); 
                data.put("riskName", obs[1]);
                data.put("reportid", null == obs[2]?"":obs[2].toString()); 
                data.put("reportName", null == obs[3]?"":obs[3].toString());
                data.put("occuredOrg", null == obs[4]?"":o_organizationBO.findById(obs[4].toString()).getOrgname());
                data.put("status", null == obs[5]?"":obs[5].toString());
                datas.add(data);
            }
        }
        map.put("datas", datas);
        return map;
    }
    
	
	/**
	 * 汇总入库，把计划中的应急预案状态改成已归档状态
	 * 
	 * @author 张健
	 * @param id 计划ID
	 * @return 
	 * @date 2014-3-6
	 * @since Ver 1.1
	 */
	public void saveContingencyPlan(String id) throws Exception{
	    ContingencyPlanBO o_ContingencyPlanBO = ContextLoader.getCurrentWebApplicationContext().getBean(ContingencyPlanBO.class);
	    o_ContingencyPlanBO.updateManageReprotStatus(id);
	}
	
	/**
	 * 应急预案提交
	 * */
	@ResponseBody
	@RequestMapping(value = "/contingencyplan/submitContingencyPlan.f")
	public void submitContingencyPlan(String executionId, String assessPlanId, HttpServletResponse response,HttpServletRequest request) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 预案审批提交
	 * */
	@ResponseBody
	@RequestMapping(value = "approveContingencyPlan.f")
	public void ApproveContingencyPlan(String executionId, String assessPlanId, HttpServletResponse response,HttpServletRequest request) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Map<String, Object> variables = new HashMap<String, Object>();
			o_jbpmBO.doProcessInstance(executionId, variables);
			out.write("true");
		} catch (Exception e) {
			e.printStackTrace();
			out.write("false");
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}
	
	/**
	 * 通过应急预案计划ID查询风险
	 * 
	 * @author 张健
	 * @param assessPlanId 应急预案计划ID
	 * @param typeId 类型
	 * @return 
	 * @date 2014-3-5
	 * @since Ver 1.1
	 */
	@ResponseBody
    @RequestMapping(value="/contingencyplan/findcontingencyplanbyrisk.f")
    public List<Map<String,Object>> findContingencyPlanByRisk(String query, String assessPlanId, String typeId, String type) {
        //风险事件GRID
        if(assessPlanId.indexOf(",") != -1){
            assessPlanId = assessPlanId.split(",")[0];
        }
        List<HashMap<String, String>> listTemp = o_contingencyPlanBO.findContingencyPlanByRisk(query,assessPlanId, typeId, type);
        StringBuffer riskIds = new StringBuffer();
        for(HashMap<String, String> listte : listTemp){
            if(listte.get("riskId") != null){
                riskIds.append(listte.get("riskId"));
                riskIds.append(",");
            }
        }
        if(riskIds.length() > 0){
            riskIds.append(riskIds.substring(0, riskIds.length()-1));
        }
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        List<Object[]> list = o_contingencyPlanBO.findContingencyPlanListBySome(assessPlanId,riskIds.toString());
        if(null != list){
            for(Object[] obs : list){
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("riskid", obs[0]); 
                data.put("riskName", obs[1]);
                data.put("reportid", null == obs[2]?"":obs[2].toString()); 
                data.put("reportName", null == obs[3]?"":obs[3].toString());
                data.put("occuredOrg", null == obs[4]?"":o_organizationBO.findById(obs[4].toString()).getOrgname());
                datas.add(data);
            }
        }
        
        return datas;
    }
}