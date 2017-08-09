package com.fhd.sys.business.assess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.bpm.jbpm.JBPMBO;
import com.fhd.dao.sys.dic.DictTypeDAO;
import com.fhd.entity.bpm.JbpmHistActinst;
import com.fhd.entity.bpm.JbpmHistProcinst;
import com.fhd.entity.bpm.view.VJbpmHistTask;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.dic.DictType;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.business.assess.email.AssessEmailBO;
import com.fhd.ra.business.assess.formulateplan.RiskAssessPlanBO;
import com.fhd.ra.business.assess.formulateplan.RiskScoreBO;
import com.fhd.sys.business.dic.DictBO;

@Service
public class SendEmailBO {
	@Autowired
	private DictBO o_dictBO;
	@Autowired
	private AssessEmailBO o_assessEmailBO;
	@Autowired
	private RiskAssessPlanBO o_planBO;
	@Autowired
	private JBPMBO o_jbpmBO;
	@Autowired
	private RiskScoreBO o_riskScoreBO;
	@Autowired
    private DictTypeDAO o_dictTypeDAO;
	
	/**
	 * findisSendEmailCheckBox：是否发送email，动态checkbox数据字典查询
	 * @return
	 */
	public List<Map<String, Object>> findisSendEmailFieldset(String typeId){
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
		List<DictEntry> dictEntryList = o_dictBO.findDictEntryByDictTypeId(typeId);
		if(null != dictEntryList && dictEntryList.size()>0){
			for(DictEntry entry : dictEntryList){
				Map<String, Object> map = new HashMap<String, Object>();
    			map.put("dictentryId", entry.getId());
    			map.put("Name", entry.getName());
    			map.put("value", entry.getValue());
    			/*if("1".equals(entry.getValue())){
    				map.put("value", true);
    			}else{
    				map.put("value", false);
    			}*/
    			mapList.add(map);
			}
		}
		return mapList;
	}
	
	/**
	 * 根据parentId查询数据字典类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DictType> findDictTypeByTypeParentId(String parentId){
		Criteria criteria = o_dictTypeDAO.createCriteria();
		criteria.setCacheable(true);
		criteria.add(Restrictions.eq("parent.id", parentId));
		List<DictType> list = criteria.list();
		if (list.size()>0) {
		    return list;
		} else {
		    return null;
		}
	}
	
	/**
	 * 根据数据字典类型查数据字典项Map集合
	 * @param 
	 * @return
	 */
	public List<Map<String, Object>> findisSendEmailCheckBox(){
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		List<DictType> dictTypeList = this.findDictTypeByTypeParentId("send_email_assess");
		if(null != dictTypeList){
			for(DictType dictType : dictTypeList){
				List<Map<String, Object>> entryList = this.findisSendEmailFieldset(dictType.getId());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dictTypeId", dictType.getId());
				map.put("dictTypeName", dictType.getName());
				map.put("children", entryList);
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	/**
	 * 查询任务分配是否发送email值
	 * @param entryId 数据字典类型
	 * @return Boolean
	 * @author 金鹏祥
	 */
	public Boolean findIsSendValue(String entryId){
		Boolean isSend = false;
		DictEntry entry = o_dictBO.findDictEntryById(entryId);
		if(null != entry){
			if("0yn_y".equals(entry.getValue())){
				isSend = true;
			}
		}
		return isSend;
	}
	
	/**
	 * 修改checkbox值
	 * @param dictEntryId	数据字典id
	 * @param value			值
	 */
	public Boolean mergeValueByDictEntryId(String dictEntryId,String value){
		DictEntry entry = o_dictBO.findDictEntryById(dictEntryId);
		if(null != entry){
			entry.setValue(value);
			o_dictBO.mergeDictEntry(entry);
			return true;
		}
		return false;
	}
	
	/**
	 * 如果设置为“是”，给人员列表中的人员发送email
	 * @param businessId
	 * @param pingguEmpIds
	 * @param request
	 */
	public void isSendEmailByEmpIds(String businessId,String pingguEmpIds,String entryId){
		Boolean isSend = this.findIsSendValue(entryId);
		HashMap<String, Long> empIdTaskIdMap = new HashMap<String, Long>();
		if(isSend){//发送email
			String deptId = UserContext.getUser().getMajorDeptId();//登录用户所在部门
			List<SysEmpOrg> empOrgList = o_riskScoreBO.findEmpDeptBydeptId(deptId);
			List<SysEmployee> empList = new ArrayList<SysEmployee>();
			if(!empOrgList.isEmpty()){
				for(SysEmpOrg empOrg : empOrgList){
					empList.add(empOrg.getSysEmployee());
				}
			}
			JbpmHistProcinst jbpmhp = o_jbpmBO.findJbpmHistProcinstByBusinessId(businessId);
			List<JbpmHistActinst> jbpmHistActinstList = o_planBO.findJbpmHistActinstsBySome(jbpmhp.getId(),0L);
			for(JbpmHistActinst jbpmHistActinst:jbpmHistActinstList){
				VJbpmHistTask jbpmHistTask = jbpmHistActinst.getJbpmHistTask();
				SysEmployee sysEmployee = jbpmHistTask.getAssignee();
				if(empList.contains(sysEmployee)){
					if(jbpmHistTask!=null){
						if("风险评估".equalsIgnoreCase(jbpmHistActinst.getActivityName())){
							Long taskId = jbpmHistTask.getId();
							empIdTaskIdMap.put(sysEmployee.getId(), taskId);
						}
					}
				}
			}
			o_assessEmailBO.sendAssessEmail(businessId, empIdTaskIdMap);	
		}
	}

}
