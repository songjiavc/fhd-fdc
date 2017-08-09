package com.fhd.ra.business.risk;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.comm.business.CategoryBO;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.HistoryEventDAO;
import com.fhd.dao.risk.HistoryEventRelaOthersDAO;
import com.fhd.dao.sys.orgstructure.EmployeeDAO;
import com.fhd.entity.risk.HistoryEvent;
import com.fhd.entity.risk.HistoryEventRelaOthers;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.web.form.risk.HistoryEventForm;
import com.fhd.sys.business.organization.OrgGridBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 历史事件BO
 * 
 * @author 张健
 * @date 2014-1-4
 * @since Ver 1.1
 */
@Service
@SuppressWarnings("unchecked")
public class HistoryEventBO {
    
    /**
     * 历史事件
     */
    @Autowired
    private HistoryEventDAO o_historyEventDAO;
    @Autowired
    private HistoryEventRelaOthersDAO o_historyEventRelaOthersDAO;
    @Autowired
    private RiskOutsideBO o_riskOutsideBO;
    @Autowired
    private CategoryBO o_categoryBO;
    @Autowired
    private EmployeeDAO o_employeeDAO;
    @Autowired
    private OrgGridBO o_orgGridBO;
    
    /**
     * 历史记录查询
     * 
     * @author 张健
     * @param id 关联对象ID
     * @param type 类型
     * @return 
     * @date 2013-11-6
     * @since Ver 1.1
     */
    public Page<HistoryEvent> findHistoryEventByType(String id,String type,Page<HistoryEvent> page,String query,String schm){
        String companyId = UserContext.getUser().getCompanyid();
        DetachedCriteria dc = DetachedCriteria.forClass(HistoryEvent.class);
        dc.add(Restrictions.eq("company.id", companyId));
        dc.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));//启用状态
        if("risk".equals(type)){
            dc.createCriteria("historyEventRelaOthers", "hero");
            dc.add(Restrictions.eq("hero.relation", id));
        }else if("dept".equals(type)){
            dc.add(Restrictions.eq("eventOccuredOrg.id", id));
        }else if("all".equals(type)){
            if(StringUtils.isNotBlank(id) && !"all".equals(id) && !"other".equals(id)){
                //按状态查询
                dc.add(Restrictions.eq("status", id));
             
                
            }else if("other".equals(id)){
                dc.add(Restrictions.isNull("status"));
            }else{
            	
            }
            //添加风险库分库查询标识
            if(null!=schm && !"".equals(schm)){
            	dc.add(Restrictions.eq("schm", schm));
        		if("dept".equals(schm)){
        			SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
    				String seq = org.getOrgseq();
    				String deptId = seq.split("\\.")[2];//部门编号
    				
    				dc.add(Restrictions.eq("createOrg", deptId));
        		}
            }
        }
        
        if(StringUtils.isNotBlank(query)){  //按姓名查询
            dc.add(Restrictions.like("hisname", query, MatchMode.ANYWHERE));
        }
        
        dc.addOrder(Order.desc("status"));
 
        return o_historyEventDAO.findPage(dc, page, false);
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
    @Transactional
    public void saveHistoryInfo(HistoryEventForm form,String id,String type,String relationId,Boolean isAdd,String archiveStatus,String schm) throws ParseException{
        String empId = UserContext.getUser().getEmpid();
        HistoryEvent historyEvent = new HistoryEvent();
        if(isAdd){
            //新增
            historyEvent.setId(Identities.uuid());
            historyEvent.setCreateBy(o_employeeDAO.get(empId));
            historyEvent.setCreateDate(new Date());
            if("dept".equals(schm)){
            	SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
                historyEvent.setCreateOrg(deptId);
            }
        }else{
            //修改
            historyEvent = o_historyEventDAO.get(id);
        }
        historyEvent.setLastModifyTime(new Date());
        historyEvent.setHiscode(form.getHiscode());
        historyEvent.setHisname(form.getHisname());
        SysOrganization org = new SysOrganization();
        org.setId(UserContext.getUser().getCompanyid());
        historyEvent.setCompany(org);
        historyEvent.setEffect(form.getEffect());
        historyEvent.setSchm(schm);
       
        //责任部门
        if(StringUtils.isNotBlank(form.getEventOccuredOrgStr())){
            SysOrganization orgtemp = new SysOrganization();
            JSONArray jsonArray =JSONArray.fromObject(form.getEventOccuredOrgStr());
            String json = ((JSONObject)jsonArray.get(0)).get("id").toString();
            orgtemp.setId(json);
            historyEvent.setEventOccuredOrg(orgtemp);
        }
        historyEvent.setEventOccuredObject(form.getEventOccuredObject());
        historyEvent.setLostAmount(form.getLostAmount());
        historyEvent.setOccurePlace(form.getOccurePlace());
        historyEvent.setComment(form.getComment());
        historyEvent.setHisdesc(form.getHisdesc());
        if(StringUtils.isNotBlank(form.getOccurDateStr())){
            historyEvent.setOccurDate(DateUtils.parseDate(form.getOccurDateStr(), "yyyy-MM-dd"));
        }
        //事件等级
        if(StringUtils.isNotBlank(form.getEventLevelDict())){
            DictEntry dict = new DictEntry();
            dict.setId(form.getEventLevelDict());
            historyEvent.setEventLevel(dict);
        }
        //处理状态
        if(StringUtils.isNotBlank(form.getDealStatusDict())){
            DictEntry dict = new DictEntry();
            dict.setId(form.getDealStatusDict());
            historyEvent.setDealStatus(dict);
            
        }
        if("saved".equals(archiveStatus)){
            historyEvent.setStatus(Contents.RISK_STATUS_SAVED);
        }else if("archived".equals(archiveStatus)){
            //已归档
            historyEvent.setStatus(Contents.RISK_STATUS_ARCHIVED);
        }
        
        //删除状态状态
        historyEvent.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
        
        o_historyEventDAO.merge(historyEvent);
        if(isAdd){
            //新增保存关联表
            if(StringUtils.isNotBlank(relationId)){
                HistoryEventRelaOthers her = new HistoryEventRelaOthers();
                her.setId(Identities.uuid());
                her.setType("risk");
                her.setHistoryEvent(historyEvent);
                her.setRelation(relationId);
                o_historyEventRelaOthersDAO.merge(her);
            }
        }else{
            //修改
            this.deleteHisRelaByRelation(id);//删除所有关联信息
            if(StringUtils.isNotBlank(relationId)){
                HistoryEventRelaOthers her = new HistoryEventRelaOthers();
                her.setId(Identities.uuid());
                her.setType("risk");
                her.setHistoryEvent(historyEvent);
                her.setRelation(relationId);
                o_historyEventRelaOthersDAO.merge(her);
            }
        }
    }
    
    /**
     * 删除与原历史事件的关联信息
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-1-10
     * @since Ver 1.1
     */
    public void deleteHisRelaByRelation(String id){
        o_historyEventRelaOthersDAO.createSQLQuery(" delete from T_HISTORY_EVENT_RELA_OTHERS where EVENT_ID = ? ", id).executeUpdate();
    }
    
    /**
     * 查找历史事件的关联信息
     * 
     * @author 张健
     * @param id 历史事件ID
     * @return 
     * @date 2014-1-10
     * @since Ver 1.1
     */
    public HistoryEventRelaOthers findHisRelaByRelation(String id){
        Criteria criteria = o_historyEventRelaOthersDAO.createCriteria();
        criteria.add(Restrictions.eq("historyEvent.id", id));
        return (HistoryEventRelaOthers)criteria.uniqueResult();
    }
    
    /**
     * 查询所有历史事件的关联信息
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-3-6
     * @since Ver 1.1
     */
    public Map<String,HistoryEventRelaOthers> getHisRelaByHisId(){
        Map<String,HistoryEventRelaOthers> map = new HashMap<String,HistoryEventRelaOthers>();
        Criteria criteria = o_historyEventRelaOthersDAO.createCriteria();
        criteria.createCriteria("historyEvent", "he");
        criteria.add(Restrictions.eq("he.deleteStatus", Contents.DELETE_STATUS_USEFUL));
        List<HistoryEventRelaOthers> list = criteria.list();
        for(HistoryEventRelaOthers hero : list){
            map.put(hero.getHistoryEvent().getId(), hero);
        }
        return map;
    }
    
    /**
     * 历史事件详细信息
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2013-11-7
     * @since Ver 1.1
     */
    public HistoryEvent findHistoryInfo(String id){
        Criteria criteria = o_historyEventDAO.createCriteria();
        criteria.add(Restrictions.eq("id", id));
        HistoryEvent he = (HistoryEvent) criteria.list().get(0);
        return he;
    }
    
    /**
     * 历史事件删除
     * 
     * @author 张健
     * @param id 历史事件ID
     * @return 
     * @date 2013-11-7
     * @since Ver 1.1
     */
    @Transactional
    public void deleteHistoryInfo(String[] hisidArr){
        for(int i=0;i<hisidArr.length;i++){
            HistoryEvent he = this.findHistoryInfo(hisidArr[i]);
            he.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
            o_historyEventDAO.merge(he);
        }
    }
    
    /**
     * 查询历史事件总数量，所有状态数量
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-1-17
     * @since Ver 1.1
     */
    public Map<String, Object> getTreeHistoryCount(String schm){
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String,String> mapParams = new HashMap<String,String>();
        StringBuffer countSql = new StringBuffer();
        StringBuffer groupSql = new StringBuffer();
        countSql.append(" select count(1) from t_history_event where delete_status = '1' ");
        groupSql.append(" select estatus , count(1) from t_history_event where delete_status = '1' ");
        if(null != schm && !"".equals(schm)){
        	countSql.append(" and schm=:schm");
        	groupSql.append(" and schm=:schm");
			if("dept".equals(schm)){//如果是部门风险库，过滤风险所属部门数据
				SysOrganization org = o_orgGridBO.findOrganizationByOrgId(UserContext.getUser().getMajorDeptId());
				String seq = org.getOrgseq();
				String deptId = seq.split("\\.")[2];//部门编号
				
				countSql.append(" and CREATE_ORG='"+deptId+"'");
				groupSql.append(" and CREATE_ORG='"+deptId+"'");
			}
			mapParams.put("schm", schm);
		}
        groupSql.append(" group by estatus ");
        
        List<Object[]> countList = o_historyEventDAO.createSQLQuery(countSql.toString(),mapParams).list();
        List<Object[]> groupList = o_historyEventDAO.createSQLQuery(groupSql.toString(),mapParams).list();
        Map<String, Object> groupMap = new HashMap<String, Object>();
        if(null != groupList){
            for(Object[] obs : groupList){
                if(null != obs[0]){
                    groupMap.put(obs[0].toString(), obs[1].toString());
                }else{
                    groupMap.put("other", obs[1].toString());
                }
            }
        }
        map.put("count", null==countList?"0":countList.get(0));//总数
        map.put("group", groupMap);//分组数量
        return map;
    }

}