package com.fhd.comm.business.analysis;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.comm.analysis.ThemePanelDAO;
import com.fhd.entity.comm.analysis.ThemePanel;
import com.fhd.fdc.utils.Contents;

/**
 * 主题分析面板BO.
 * @author 吴德福
 * @since 2013-10-8
 */
@Service
@SuppressWarnings("unchecked")
public class ThemePanelBO {

	@Autowired
	private ThemePanelDAO o_themePanelDAO;
	
	/**
	 * 保存主题面板.
	 * @param themePanel
	 */
	@Transactional
	public void saveThemePanel(ThemePanel themePanel){
		o_themePanelDAO.merge(themePanel);
	}
	/**
	 * 修改主题面板.
	 * @param themePanel
	 */
	@Transactional
	public void mergeThemePanel(ThemePanel themePanel){
		o_themePanelDAO.merge(themePanel);
	}
	/**
	 * 根据id删除主题面板.
	 * @param id
	 */
	@Transactional
	public void removeThemePanel(String id){
		o_themePanelDAO.delete(id);
	}
	/**
	 * 根据id集合批量删除主题面板.
	 * @param ids
	 */
	@Transactional
	public void removeThemePanelByIds(String ids){
		//物理删除
		o_themePanelDAO.createQuery("delete ThemePanel where id in (:ids)").setParameterList("ids", StringUtils.split(ids,",")).executeUpdate();
	}
	/**
	 * 根据id查询主题分析面板.
	 * @param id
	 * @return
	 */
	public ThemePanel findThemePanelById(String id){
		return o_themePanelDAO.get(id);
	}
	/**
	 * 根据主题分析id查询对应的布局方式.
	 * @param themeId
	 * @return List<ThemePanel>
	 */
	public List<ThemePanel> findThemeThemePanelListByThemeId(String themeId){
		Criteria criteria =  o_themePanelDAO.createCriteria();
		criteria.createAlias("themeAnalysis", "ta");
		criteria.add(Restrictions.eq("ta.id", themeId));
		criteria.add(Restrictions.eq("ta.deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return criteria.list();
	}
}