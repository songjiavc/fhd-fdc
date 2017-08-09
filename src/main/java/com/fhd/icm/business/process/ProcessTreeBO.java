package com.fhd.icm.business.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.process.ProcessDAO;
import com.fhd.entity.process.Process;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.interfaces.process.IProcessTreeBO;
import com.fhd.ra.business.risk.RiskOutsideBO;


/**
 * 查询流程树
 * @author   李克东
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-1-25		下午2:52:33
 *
 * @see 	 
 */
@Service
@SuppressWarnings("unchecked")
public class ProcessTreeBO implements IProcessTreeBO {
	
	//前台树节点变量,临时变量
	public static final String ROOT = "root";
	
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private RiskOutsideBO o_riskOutsideBO;

	/**
	 * 查询流程树
	 * @author zhengjunxiang
	 * @param id
	 * @param canChecked
	 * @param leafCheck
	 * @param showLight
	 * @param query
	 * @param dealStatus
	 * @return
	 */
	public List<Map<String, Object>> processTreeLoader(String id, Boolean canChecked, Boolean leafCheck, Boolean showLight, String query, String dealStatus) {
		
		if(ProcessTreeBO.ROOT.equals(id)){
			id=null;
		}
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();// 返回前台的nodelist
		//1.查询所有需要的流程，按照id_seq排序
		Criteria criteria = o_processDAO.createCriteria();
		//2.查询所有的流程，按照id_seq排序
		if(StringUtils.isNotBlank(query)){
			//查询条件
			Criteria subCriteria = o_processDAO.createCriteria();
			subCriteria.add(Restrictions.like("name", query,MatchMode.ANYWHERE));	
			subCriteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
			subCriteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
			subCriteria.add(Restrictions.eq("isLeaf", true));
			subCriteria.addOrder(Order.asc("sort"));
			List<Process> processList = subCriteria.list();
			Set<String> ids =  new HashSet<String>();
			for(Process process : processList){
				String[] idSeqArray = process.getIdSeq().split("\\.");
				for (String idSeq : idSeqArray) {
					ids.add(idSeq);
				}
			}
			if(ids.isEmpty()){
				criteria.add(Restrictions.isNull("parent.id"));
			}else{
				criteria.add(Restrictions.in("id", ids));
			}
		}
		if(StringUtils.isNotBlank(id)){
			criteria.add(Restrictions.eq("parent.id",id));
		}else{
			criteria.add(Restrictions.isNull("parent.id"));
		}
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		criteria.addOrder(Order.asc("sort"));
		List<Process> processureList = criteria.list();
		if(!processureList.isEmpty()){
			Map<String,Object> iconMap = new HashMap<String, Object>();
			if(showLight){
				List<String> ids = new ArrayList<String>();
				for(Process tempProcess : processureList){
					ids.add(tempProcess.getId());
				}
				iconMap = o_riskOutsideBO.findRiskAssementStatusById("process",ids);
			}
			for (Process tempProcess : processureList) {
				if(StringUtils.isNotBlank(dealStatus)){
					if(isExistByProcessDealStatus(false, tempProcess, dealStatus)){
						nodes.add(setNodesAttributes(tempProcess, false, canChecked, leafCheck, iconMap));
					}
				}else{
					nodes.add(setNodesAttributes(tempProcess, false, canChecked, leafCheck, iconMap));
				}
			}
		}
		
		return nodes;
	}
	/**
	 * 判断流程及子流程是否存在dealStatus状态.
	 * @author 吴德福
	 * @param flag
	 * @param tempProcess
	 * @param dealStatus
	 * @return boolean
	 */
	public boolean isExistByProcessDealStatus(boolean flag, Process tempProcess, String dealStatus){
		if(tempProcess.getIsLeaf()){
			//末级流程
			if(dealStatus.equals(tempProcess.getDealStatus())){
				flag = true;
			}
		}else{
			//流程分类
			Set<Process> children = tempProcess.getChildren();
			for (Process process : children) {
				if(process.getIsLeaf()){
					//末级流程
					if(dealStatus.equals(process.getDealStatus())){
						flag = true;
					}
				}else{
					flag = isExistByProcessDealStatus(flag, process, dealStatus);
				}
			}
		}
		return flag;
	}
	
	/**
	 * 封装节点属性设置值.
	 * @param process
	 * @param expanded
	 * @param canChecked
	 * @param leafCheck
	 * @param map
	 * @return Map<String, Object>
	 */
	private Map<String, Object> setNodesAttributes(Process process, boolean expanded, boolean canChecked, boolean leafCheck, Map<String,Object> map) {

		Map<String, Object> node = new HashMap<String, Object>();
		if(leafCheck){
			if (canChecked && process.getIsLeaf()) {
				node.put("checked", false);
			}
		}else{
			if (canChecked) {
				node.put("checked", false);
			}
		}
		
		node.put("id", process.getId());
		node.put("dbid", process.getId());
		node.put("text", process.getName());
		node.put("leaf", process.getIsLeaf());
		node.put("expanded", expanded);
		if(null != map){
			node.put("iconCls", map.get(process.getId()));
		}
		return node;
	}
}
