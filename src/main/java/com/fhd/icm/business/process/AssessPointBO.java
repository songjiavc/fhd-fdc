package com.fhd.icm.business.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.icm.assess.AssessPointDAO;
import com.fhd.dao.sys.dic.DictEntryDAO;
import com.fhd.entity.icm.assess.AssessPoint;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.sys.web.form.dic.DictEntryForm;
/**
 * 流程节点维护
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-3-11		下午1:17:50
 *
 * @see 	 
 */
@Service
@SuppressWarnings("unchecked")
public class AssessPointBO {
	
	@Autowired
	private AssessPointDAO o_assessPointDAO;
	@Autowired
	private DictEntryDAO o_dictEntryDAO;
	
	/**
	 * 根据节点ID 找到上级节点和节点进入条件
	 * @autor 宋佳
	 * @param processId 前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findAssessPointListBySome(String processPointId,String measureId,String type,String processId,String companyId) {
		Criteria criteria = o_assessPointDAO.createCriteria();
		if(StringUtils.isNotBlank(processId)){
			criteria.createAlias("process", "process");
			criteria.add(Restrictions.eq("process.id",processId));
		}
		if(StringUtils.isNotEmpty(processPointId)){
			criteria.createAlias("processPoint", "processPoint");
			criteria.add(Restrictions.eq("processPoint.id",processPointId));
		}else if(StringUtils.isNotEmpty(measureId)){
			criteria.createAlias("controlMeasure", "controlMeasure");
			criteria.add(Restrictions.eq("controlMeasure.id",measureId));
		}else{
			criteria.add(Restrictions.eq("type", ""));
		}
		Map<String, Object> result = new HashMap<String, Object>();
		List<AssessPoint> assessPointList = criteria.list();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for(AssessPoint assessPoint : assessPointList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", assessPoint.getId());
			if(StringUtils.isNotEmpty(processPointId)){
				map.put("pointId", assessPoint.getProcessPoint().getId());
			}
			map.put("processId", assessPoint.getProcess().getId());
			map.put("type", assessPoint.getType());
			map.put("assessDesc", assessPoint.getDesc());
			map.put("comment", assessPoint.getComment());
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", assessPointList.size());
		return result;
	}
	
	/**
     * findDictEntryByTypeId:根据typeid取字典列表
     * @author 宋佳
     * @param typeId 字典类型ID
     * @return List<DictEntryForm>
     * @since fhd　Ver 1.1
     */
	public List<DictEntryForm> findDictEntryByTypeId(String typeId) {
		Criteria criteria = o_dictEntryDAO.createCriteria();
		criteria.setCacheable(true);
		if (StringUtils.isNotBlank(typeId)) {
			criteria.add(Restrictions.eq("dictType.id", typeId));
		} else {
		    return null;
		}
		criteria.add(Restrictions.eq("status", Contents.STATUS_NORMAL));
		criteria.addOrder(Order.asc("idSeq"));
		List<DictEntry> list= criteria.list();
		List<DictEntryForm> result=new ArrayList<DictEntryForm>();
		for(DictEntry entry:list){
		    DictEntryForm form=new DictEntryForm();
		    form.setId(entry.getId());
		    form.setName(entry.getName());
		    form.setLevel(entry.getLevel());
		    result.add(form);
		}
		return result;
    }
    @Transactional
	public void removeAssessPointByID(String ids){
		//删除关联关系表
		Criteria orgcriteria = o_assessPointDAO.createCriteria();
		List<AssessPoint> asscesspointList = orgcriteria.add(Restrictions.in("id",StringUtils.split(ids,","))).list();
		for(AssessPoint assessPoint : asscesspointList){
			o_assessPointDAO.delete(assessPoint);
		}
	}
    /**
     * 根据公司id和类型type查询评价结点集合.
     * @author 吴德福
     * @param companyId
     * @param type
     * @return List<AssessPoint>
     */
    public List<AssessPoint> findAssessPointListByCompanyIdAndType(String companyId, String type){
    	Criteria criteria = o_assessPointDAO.createCriteria();
    	criteria.createAlias("process", "p");
		criteria.createAlias("p.company", "c");
		if(StringUtils.isNotBlank(type)){
    		criteria.add(Restrictions.eq("type", type));
    	}
    	if(StringUtils.isNotBlank(companyId)){
    		criteria.add(Restrictions.eq("c.id", companyId));
    	}
    	return criteria.list();
    }
}