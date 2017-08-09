package com.fhd.sm.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.kpi.KpiDataSourceDAO;
import com.fhd.entity.kpi.KpiDataSource;
import com.fhd.sm.interfaces.IKpiDataSource;
@Service
public class KpiDataSourceBO implements IKpiDataSource {
	
	@Autowired
	private KpiDataSourceDAO o_kpiDataSourceDao;
	
	/**
	 * 根据id查找数据源
	 * @param id 数据源id
	 * @return 数据源
	 */
	@SuppressWarnings("unchecked")
	public KpiDataSource findDataSourceById (String id) {
		Criteria criteria = o_kpiDataSourceDao.createCriteria();
		List<KpiDataSource> list;
		if(StringUtils.isNotBlank(id)) {
			criteria.add(Restrictions.eq("id", id));
			list = 	criteria.list();		
		} else {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 根据名称查找数据源
	 * @param  数据源名称
	 * @return 检索出的数据源
	 */
	@SuppressWarnings("unchecked")
	public KpiDataSource findDataSourcebyName(String name){
		Criteria criteria = o_kpiDataSourceDao.createCriteria();
		List<KpiDataSource> list;
		if(StringUtils.isNotBlank(name)) {
			criteria.add(Restrictions.eq("driverName", name));
			list = 	criteria.list();		
		} else {
			return null;
		}
		return list.get(0);
	}
    
    /**
     * 根据id删除数据源
     */
	@Override
	@Transactional
	public void deleteById(String id) {
		o_kpiDataSourceDao.delete(id);
	}
	/**
	 * 删除数据源
	 */
	@Override
	@Transactional
	public void delete(KpiDataSource kpiDataSource) {
		o_kpiDataSourceDao.delete(kpiDataSource);
	}
    
	/**
	 * 保存数据源
	 */
	@Override
	@Transactional
	public void save(KpiDataSource kpiDataSource) {
		o_kpiDataSourceDao.merge(kpiDataSource);
	}
	/**
	 * 加载数据源
	 * @param 数据源名称
	 * @return 外部数据源节点列表
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> loadDataSourceNode(String query){
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		Criteria criteria = o_kpiDataSourceDao.createCriteria();
		if(StringUtils.isNotBlank(query)) {
			criteria.add(Restrictions.like("driverName", query, MatchMode.ANYWHERE));
		}
		List<KpiDataSource> list = criteria.list();
		for (KpiDataSource ds: list) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", ds.getId());
			item.put("text", ds.getDriverName());
			item.put("leaf", true);
			item.put("expanded", false);
			nodes.add(item);	
		}
		return nodes;		
	}
	
}
