package com.fhd.comm.business.analysis;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.dao.comm.analysis.ThemeAnalysisDAO;
import com.fhd.entity.comm.analysis.ThemeAnalysis;
import com.fhd.fdc.commons.interceptor.RecordLog;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;

/**
 * 主题分析BO.
 * @author 吴德福
 * @since 2013-9-2
 */
@Service
@SuppressWarnings("unchecked")
@RecordLog(value="主题分析")
public class ThemeAnalysisBO {
	
	@Autowired
	private ThemeAnalysisDAO o_themeAnalysisDAO;
	
	/**
	 * 保存主题分析.
	 * @param themeAnalysis
	 */
	@Transactional
	public void saveThemeAnalysis(ThemeAnalysis themeAnalysis){
		o_themeAnalysisDAO.merge(themeAnalysis);
	}
	/**
	 * 修改主题分析.
	 * @param themeAnalysis
	 */
	@Transactional
	public void mergeThemeAnalysis(ThemeAnalysis themeAnalysis){
		o_themeAnalysisDAO.merge(themeAnalysis);
	}
	/**
	 * 根据id删除主题分析.
	 * @param id
	 */
	@Transactional
	public void removeThemeAnalysis(String id){
		o_themeAnalysisDAO.delete(id);
	}
	/**
	 * 根据id集合批量删除主题分析.
	 * @param ids
	 */
	@Transactional
	public void removeThemeAnalysisByIds(String ids){
		//物理删除
		//o_themeAnalysisDAO.createQuery("delete ThemeAnalysis where id in (:ids)").setParameterList("ids", StringUtils.split(ids,",")).executeUpdate();
		//逻辑删除
		Criteria criteria = o_themeAnalysisDAO.createCriteria();
		criteria.add(Restrictions.in("id", StringUtils.split(ids,",")));
		List<ThemeAnalysis> themeAnalysisList = criteria.list();
		for (ThemeAnalysis themeAnalysis : themeAnalysisList) {
			themeAnalysis.setDeleteStatus(Contents.DELETE_STATUS_DELETED);
			o_themeAnalysisDAO.merge(themeAnalysis);
		}
	}
	/**
	 * 根据id查询主题分析.
	 * @param id
	 * @return ThemeAnalysis
	 */
	public ThemeAnalysis findThemeAnalysisById(String id){
		return o_themeAnalysisDAO.get(id);
	}
	/**
	 * 根据查询条件分页查询主题分析.
	 * @param page
	 * @param sort
	 * @param query
	 * @param companyId
	 * @return Page<ThemeAnalysis>
	 */
	public Page<ThemeAnalysis> findThemeAnalysisByPage(Page<ThemeAnalysis> page, String sort, String query, String companyId){
		DetachedCriteria dc = DetachedCriteria.forClass(ThemeAnalysis.class);
		if(StringUtils.isNotBlank(query)){
			dc.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
		dc.add(Restrictions.eq("company.id", companyId));
		dc.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		dc.addOrder(Order.asc("company.id"));
		dc.addOrder(Order.asc("name"));
		dc.addOrder(Order.asc("layoutType"));
		dc.addOrder(Order.asc("attribute"));
		return o_themeAnalysisDAO.findPage(dc, page, false);
	}
	/**
	 * 根据查询条件查询主题分析.
	 * @param name
	 * @return List<ThemeAnalysis>
	 */
	public List<ThemeAnalysis> findThemeAnalysisBySome(String name) {
		Criteria criteria = o_themeAnalysisDAO.createCriteria();
		if(StringUtils.isNotBlank(name)){
			criteria.add(Restrictions.eq("name", name));
		}
		criteria.add(Restrictions.eq("company.id", UserContext.getUser().getCompanyid()));
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
}