
package com.fhd.ra.web.controller.risk.define;

import com.fhd.core.utils.Identities;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.web.form.risk.RiskForm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 风险定义
 * 
 * @author 张健
 * @date 2013-8-20
 * @since Ver 1.1
 */
@Controller
public class DefineRiskControl {

    @Autowired
    private RiskCmpBO o_riskCmpBO;

    /**
     * 保存风险 state=2,专用于风险评估模块风险的添加
     * 
     * @author zj
     * @param riskForm
     * @param id
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/risk/define/saveRiskInfo")
    public Map<String, Object> saveRiskInfo(RiskForm riskForm, String id, String state) {
        Map<String, Object> map = new HashMap<String, Object>();

        Risk risk = new Risk();
        if (state == null || state.equals("")) {
            risk.setDeleteStatus("1");
        } else {
            risk.setDeleteStatus(state); // 将2的状态保存起来
        }
        // 添加
        String makeId = Identities.uuid();
        risk.setId(makeId);
        String companyId = UserContext.getUser().getCompanyid();
        SysOrganization company = new SysOrganization();
        company.setId(companyId);
        risk.setCompany(company);
        // 上级风险
        String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && !parentIdStr.equals("")) {
            JSONArray parentIdArr = JSONArray.fromObject(parentIdStr);
            String parentId = ((JSONObject) parentIdArr.get(0)).get("id").toString();
            Risk parent = o_riskCmpBO.findRiskById(parentId);
            if (null != parent) {
                risk.setParent(parent);
                risk.setParentName(parent.getName());
                risk.setIdSeq(parent.getIdSeq() + makeId + ".");
                if(parent.getLevel()!=null){
                	risk.setLevel(parent.getLevel()+1);
                }
            }
        } else {
            risk.setParent(null);
            risk.setParentName("");
            risk.setIdSeq("." + makeId + ".");
            risk.setLevel(1);
        }

        risk.setIsLeaf(true);
        risk.setIsRiskClass(riskForm.getIsRiskClass());
        // 编码，名称，描述
        risk.setCode(riskForm.getCode());
        risk.setName(riskForm.getName());
        risk.setDesc(riskForm.getDesc());
        //序号
        if(riskForm.getSort() == null){	//未指定，自动排序
			risk.setSort(o_riskCmpBO.getSiblingRiskNum(risk.getParent())+1);
		}else{
			risk.setSort(riskForm.getSort());
		}
        // 是否启用
        risk.setIsUse("0yn_y");

        // 评估模板
        if(risk.getParent()!=null){
        	risk.setIsInherit("0yn_y");
        	risk.setTemplate(risk.getParent().getTemplate());
        }
        // 责任部门，相关部门
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();

        // 保存
        o_riskCmpBO.saveRisk(risk, respDeptName, relaDeptName, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null,null);

        map.put("success", true);
        map.put("id", risk.getId());
        map.put("name", risk.getName());
        return map;
    }

    /**
     * 修改风险 state=2,专用于风险评估模块风险的添加
     * 
     * @author zj
     * @param riskForm
     * @param id
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/risk/define/mergeRiskInfo")
    public Map<String, Object> mergeRiskInfo(RiskForm riskForm, String id, String state) {
        Map<String, Object> map = new HashMap<String, Object>();

        // 修改
        Risk risk = o_riskCmpBO.findRiskById(id);
        // 上级风险
        String parentIdStr = riskForm.getParentId();
        if (parentIdStr != null && (!parentIdStr.equals("")) && (!parentIdStr.equals("[]"))) {
            JSONArray arr = JSONArray.fromObject(parentIdStr);
            if (arr != null) {
                JSONObject obj = (JSONObject) arr.get(0);
                Risk parent = o_riskCmpBO.findRiskById(obj.getString("id"));
                risk.setParent(parent);
                risk.setParentName(parent.getName());
                risk.setIdSeq(parent.getIdSeq() + risk.getId() + ".");
            }
        } else {
            risk.setParent(null);
            risk.setParentName("");
            risk.setIdSeq("." + risk.getId() + ".");
        }

        risk.setIsRiskClass(riskForm.getIsRiskClass());
        // 编码，名称，描述
        risk.setCode(riskForm.getCode());
        risk.setName(riskForm.getName());
        risk.setDesc(riskForm.getDesc());
        // 是否启用
        risk.setIsUse("0yn_y");
        // 评估模板
        if(risk.getParent()!=null){
        	risk.setTemplate(risk.getParent().getTemplate());
        }

        // 责任部门，相关部门
        String respDeptName = riskForm.getRespDeptName();
        String relaDeptName = riskForm.getRelaDeptName();

        // 保存
        o_riskCmpBO.mergeRiskDefine(risk, respDeptName, relaDeptName);

        map.put("success", true);
        map.put("id", risk.getId());
        map.put("name", risk.getName());

        return map;
    }

}
