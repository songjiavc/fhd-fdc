package com.fhd.ra.web.controller.risk;

import com.fhd.core.dao.Page;
import com.fhd.entity.risk.HistoryEvent;
import com.fhd.entity.risk.HistoryEventRelaOthers;
import com.fhd.fdc.utils.Contents;
import com.fhd.ra.business.risk.HistoryEventBO;
import com.fhd.ra.business.risk.RiskCmpBO;
import com.fhd.ra.web.form.risk.HistoryEventForm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史事件
 * 
 * @author 张健
 * @date 2014-1-4
 * @since Ver 1.1
 */
@Controller
@SuppressWarnings("unchecked")
public class HistoryEventControl {
    
    @Autowired
    private HistoryEventBO o_historyEventBO;
    @Autowired
    private RiskCmpBO o_riskCmpBO;
    
    /**
     * 历史事件列表查询
     * 
     * @author 张健
     * @param id 关联对象ID
     * @param type 类型
     * @return 
     * @date 2013-11-7
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/historyevent/findhistoryeventbytype.f")
    public Map<String, Object> findHistoryEventByType(String id,String type,int start, int limit, String query, String sort,String schm){
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        Page<HistoryEvent> page = new Page<HistoryEvent>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        page = o_historyEventBO.findHistoryEventByType(id,type,page,query,schm);
        //历史事件的关联信息
        Map<String, HistoryEventRelaOthers> hisRelaMap = o_historyEventBO.getHisRelaByHisId();
        List<HistoryEvent> list = page.getResult();
        if(null != list){
            for(HistoryEvent he : list){
                Map<String, Object> item = new HashMap<String, Object>();
                if(hisRelaMap.get(he.getId()) != null){
                    //有关联风险
                    item.put("id", he.getId()+"_"+(hisRelaMap.get(he.getId()).getId()));//风险ID，历史记录ID
                    item.put("riskname", o_riskCmpBO.findRiskById(hisRelaMap.get(he.getId()).getRelation())==null?"":o_riskCmpBO.findRiskById(hisRelaMap.get(he.getId()).getRelation()).getName());
                }else{
                    //没有关联风险
                    item.put("id", (he.getId())+"_");//风险ID，历史记录ID
                    item.put("riskname", "");
                }
                item.put("hisname", null==he.getHisname()?"":he.getHisname());
                item.put("occurDate", null==he.getOccurDate()?"":he.getOccurDate().toString().substring(0,10));
                item.put("lostAmount", null==he.getLostAmount()?"":he.getLostAmount().toString());
                item.put("createBy", null==he.getCreateBy()?"":he.getCreateBy().getEmpname());
                item.put("dealStatus", null==he.getDealStatus()?"":he.getDealStatus().getId());
                item.put("status", null==he.getStatus()?"":he.getStatus());
                datas.add(item);
            }
        }
        map.put("totalCount",page.getTotalItems());
        map.put("datas", datas);
        map.put("success", true);
        return map;
    }
    
    /**
     * 历史事件新增或修改
     * 
     * @author 张健
     * @param id 历史事件ID
     * @param type 类型，目前只有risk
     * @param relationId 关联对象ID
     * @param isAdd 是否新增
     * @param archiveStatus 审批状态
     * @return 
     * @throws ParseException 
     * @date 2013-11-7
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/historyevent/savehistoryinfo.f")
    public Map<String, Object> saveHistoryInfo(HistoryEventForm form,String id,String type,String relationId,Boolean isAdd,String archiveStatus,String schm) throws ParseException{
        Map<String, Object> map = new HashMap<String, Object>();
        o_historyEventBO.saveHistoryInfo(form,id,type,relationId,isAdd,archiveStatus,schm);
        map.put("success", true);
        return map;
    }
    
    /**
     * 历史事件详细信息
     * 
     * @author 张健
     * @param id 关联对象ID
     * @param type 类型
     * @return 
     * @date 2013-11-7
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/historyevent/findhistoryinfo.f")
    public Map<String, Object> findHistoryInfo(String id,String type){
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        HistoryEvent he = o_historyEventBO.findHistoryInfo(id);
        data.put("id", he.getId());
        data.put("hisname", he.getHisname());
        data.put("hiscode", he.getHiscode());
        data.put("hisdesc", he.getHisdesc());
        data.put("occurDateStr", null == he.getOccurDate()?"":sf.format(he.getOccurDate()));
        data.put("lostAmount", he.getLostAmount().toString());
        data.put("effect", he.getEffect());
        HistoryEventRelaOthers her = o_historyEventBO.findHisRelaByRelation(he.getId());
        if("detail".equals(type)){
            //查看界面
            data.put("dealStatusDict", null == he.getDealStatus()?"":he.getDealStatus().getName());
            data.put("eventLevelDict", null == he.getEventLevel()?"":he.getEventLevel().getName());
            data.put("eventOccuredOrgStr", null == he.getEventOccuredOrg()?"":he.getEventOccuredOrg().getOrgname());
            data.put("relation", null == her?"":o_riskCmpBO.findRiskById(her.getRelation()).getId());
        }else{
            //修改界面
            data.put("dealStatusDict", null == he.getDealStatus()?"":he.getDealStatus().getId());
            data.put("eventLevelDict", null == he.getEventLevel()?"":he.getEventLevel().getId());
            if(null != her){
                JSONArray orgJsonre = new JSONArray();
                JSONObject orgObjectre = new JSONObject();
                orgObjectre.put("id", o_riskCmpBO.findRiskById(her.getRelation()).getId());
                orgJsonre.add(orgObjectre);
                data.put("relation", orgJsonre.toString());
            }else{
                data.put("relation", "");
            }
            if(null != he.getEventOccuredOrg()){
                JSONArray orgJson = new JSONArray();
                JSONObject orgObject = new JSONObject();
                orgObject.put("id", he.getEventOccuredOrg().getId());
                orgObject.put("deptno", he.getEventOccuredOrg().getOrgcode());
                orgObject.put("deptname", he.getEventOccuredOrg().getOrgname());
                orgJson.add(orgObject);
                data.put("eventOccuredOrgStr", orgJson.toString());
            }else{
                data.put("eventOccuredOrgStr", "");
            }
        }
        data.put("eventOccuredObject", he.getEventOccuredObject());
        data.put("occurePlace", he.getOccurePlace());
        data.put("comment", he.getComment());
        map.put("data", data);
        map.put("success", true);
        return map;
    }
    
    /**
     * 历史事件删除
     * 
     * @author 张健
     * @param hisid 历史事件ID
     * @return 
     * @date 2013-11-7
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/historyevent/deletehistoryevent.f")
    public Map<String, Object> deleteHistoryInfo(String hisid){
        String[] hisidArr = hisid.split(",");
        Map<String, Object> map = new HashMap<String, Object>();
        o_historyEventBO.deleteHistoryInfo(hisidArr);
        map.put("success", true);
        return map;
        
        
    }
    
    /**
     * 历史事件综合管理，查询所有
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-1-10
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/historyevent/findallhistoryevent.f")
    public Map<String, Object> findAllHistoryEvent(int start, int limit, String query, String sort){
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
        
        Page<HistoryEvent> page = new Page<HistoryEvent>();
        page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
        page.setPageSize(limit);
        page = o_historyEventBO.findHistoryEventByType(null,null,page,query,null);//null是后添加的shcm
        
        Map<String, HistoryEventRelaOthers> hisRelaMap = o_historyEventBO.getHisRelaByHisId();
        List<HistoryEvent> list = page.getResult();
        if(null != list){
            for(HistoryEvent he : list){
                Map<String, Object> item = new HashMap<String, Object>();
                if(hisRelaMap.get(he.getId()) != null){
                    //有关联风险
                    item.put("id", he.getId()+"_"+(hisRelaMap.get(he.getId()).getId()));//风险ID，历史记录ID
                    item.put("riskname", o_riskCmpBO.findRiskById(hisRelaMap.get(he.getId()).getRelation())==null?"":o_riskCmpBO.findRiskById(hisRelaMap.get(he.getId()).getRelation()).getName());
                }else{
                    //没有关联风险
                    item.put("id", (he.getId())+"_");//风险ID，历史记录ID
                    item.put("riskname", "");
                }
                item.put("hisname", he.getHisname());
                item.put("occurDate", null == he.getOccurDate()?"":sf.format(he.getOccurDate()));
                item.put("lostAmount", null == he.getLostAmount()?"":he.getLostAmount().toString());
                item.put("effect", null == he.getEffect()?"":he.getEffect());
                item.put("dealStatus", null == he.getDealStatus()?"":he.getDealStatus().getId());
                item.put("status", null == he.getStatus()?"":he.getStatus());
                datas.add(item);
            }
        }
        map.put("totalCount",page.getTotalItems());
        map.put("datas", datas);
        map.put("success", true);
        return map;
    }
    
    /**
     * 返回审批状态树的各个状态的数量
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-1-18
     * @since Ver 1.1
     */
    @ResponseBody
    @RequestMapping(value = "/historyevent/gettreehistorycount.f")
    public Map<String, Object> getTreeHistoryCount(String schm){
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapHistoryCount = o_historyEventBO.getTreeHistoryCount(schm);//按组查询所有历史事件审批类型的数量
        Object count = (Object) mapHistoryCount.get("count");
        Map<String, Object> groupMap = new HashMap<String, Object>();
        groupMap = (Map<String, Object>) mapHistoryCount.get("group");
        List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
        
        Map<String, Object> childrenSave = new HashMap<String, Object>();
        childrenSave.put("id", "saved");
        childrenSave.put("text", "<font color=red>"+"待提交("+(groupMap.get(Contents.RISK_STATUS_SAVED) == null?"0":groupMap.get(Contents.RISK_STATUS_SAVED).toString())+")"+"</font>");
        childrenSave.put("dbid", "saved");
        childrenSave.put("leaf", true);
        childrenSave.put("code", "saved");
        childrenSave.put("type", "type");
        childrenList.add(childrenSave);
        Map<String, Object> childrenExamine = new HashMap<String, Object>();
        childrenExamine.put("id", "examine");
        childrenExamine.put("text", "<font color=green>"+"审批中("+(groupMap.get(Contents.RISK_STATUS_EXAMINE) == null?"0":groupMap.get(Contents.RISK_STATUS_EXAMINE).toString())+")"+"</font>");
        childrenExamine.put("dbid", "examine");
        childrenExamine.put("leaf", true);
        childrenExamine.put("code", "examine");
        childrenExamine.put("type", "examine");
        childrenList.add(childrenExamine);
        Map<String, Object> childrenArchived = new HashMap<String, Object>();
        childrenArchived.put("id", "archived");
        childrenArchived.put("text", "已归档("+(groupMap.get(Contents.RISK_STATUS_ARCHIVED) == null?"0":groupMap.get(Contents.RISK_STATUS_ARCHIVED).toString())+")");
        childrenArchived.put("dbid", "archived");
        childrenArchived.put("leaf", true);
        childrenArchived.put("code", "archived");
        childrenArchived.put("type", "archived");
        childrenList.add(childrenArchived);
        
        if(null != groupMap.get("other")){
            Map<String, Object> childrenOther = new HashMap<String, Object>();
            childrenOther.put("id", "other");
            childrenOther.put("text", "未分类("+(groupMap.get("other") == null?"0":groupMap.get("other").toString())+")");
            childrenOther.put("dbid", "other");
            childrenOther.put("leaf", true);
            childrenOther.put("code", "other");
            childrenOther.put("type", "other");
            childrenList.add(childrenOther);
        }
        
        Map<String, Object> allMap = new HashMap<String, Object>();
        allMap.put("id", "all");
        allMap.put("text", "全部("+count+")");
        allMap.put("dbid", "all");
        allMap.put("leaf", false);
        allMap.put("code", "all");
        allMap.put("type", "all");
        allMap.put("expanded", true);
        allMap.put("children", childrenList);
        
        map.put("children", allMap);
        return map;
    }
    
}