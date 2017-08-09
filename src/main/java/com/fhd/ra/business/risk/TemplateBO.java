package com.fhd.ra.business.risk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.risk.RiskRelaTemplateDAO;
import com.fhd.dao.risk.ScoreInstanceDAO;
import com.fhd.dao.risk.TemplateDAO;
import com.fhd.dao.risk.TemplateRelaDimensionDAO;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.risk.RiskRelaTemplate;
import com.fhd.entity.risk.Score;
import com.fhd.entity.risk.ScoreInstance;
import com.fhd.entity.risk.Template;
import com.fhd.entity.risk.TemplateRelaDimension;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.UserContext;
import com.fhd.ra.interfaces.risk.ITemplateBO;
import com.fhd.sys.business.file.FileUploadBO;

/**
 * 模板业务类：模板及模板相关信息的增删改查功能
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-11-13		下午3:48:37
 *
 * @see 	 
 */
@SuppressWarnings("unchecked")
@Service
public class TemplateBO implements ITemplateBO{

	@Autowired
	private TemplateDAO o_templateDAO;
	
	@Autowired
	private TemplateRelaDimensionDAO o_templateRelaDimensionDAO;
	
	@Autowired
	private RiskRelaTemplateDAO o_riskRelaTemplateDAO;
	
	@Autowired
	private ScoreInstanceDAO o_scoreInstanceDAO;
	
	@Autowired
	private DimensionBO o_dimensionBO;
	
	@Autowired
	private RiskBO o_riskBO;
	
	@Autowired
	private FileUploadBO o_fileUploadBO;
	
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#saveTemplateById(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void saveTemplateById(String templateId, String riskId){
		Template templateSource = new Template();
		int sort = 0;
		
		List<Template> templateList = this.findTemplateByQuery(null);
		List<TemplateRelaDimension> templateRelaDimensionList = this.findTemplateRelaDimensionBySome(null, templateId);
		List<ScoreInstance> scoreInstanceList = this.findScoreInstanceBySome(templateId, null);
		for (Template template : templateList) {
			if(templateId.equals(template.getId())){
				templateSource = template;//获得ID为templateId的对象
			}
			if(template.getSort() != null && sort<=template.getSort()){
				sort = template.getSort();//获得排序的最大值
			}
		}
		Template templateTarget = new Template(Identities.uuid());
		//copy集团模板，没有公司关联，保存当前登录人的公司信息,否则报错
		if("dim_template_type_sys".equals(templateSource.getType().getId())){
			templateTarget.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));
		}else{
			templateTarget.setCompany(templateSource.getCompany());
		}
		templateTarget.setDesc(templateSource.getDesc());
		templateTarget.setName(templateSource.getName());
		templateTarget.setType(new DictEntry("dim_template_type_self"));
		templateTarget.setSort(++sort);
		o_templateDAO.merge(templateTarget);
		for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionList) {//保存模板关联维度信息
			if(null == templateRelaDimension.getParent()){
				TemplateRelaDimension templateRelaDimensionTarget = new TemplateRelaDimension();
				BeanUtils.copyProperties(templateRelaDimension, templateRelaDimensionTarget);
				templateRelaDimensionTarget.setId(Identities.uuid());
				templateRelaDimensionTarget.setTemplate(templateTarget);
				o_templateRelaDimensionDAO.merge(templateRelaDimensionTarget);
				this.mergeTemplateRelaDimensionIdSeqAndLevel(templateRelaDimensionTarget);
				for (ScoreInstance scoreInstance : scoreInstanceList) {//保存模板关联维度下的分值信息
					if(scoreInstance.getTemplateRelaDimension().getId().equals(templateRelaDimension.getId())){
						ScoreInstance scoreInstanceTarget = new ScoreInstance();
						BeanUtils.copyProperties(scoreInstance, scoreInstanceTarget);
						scoreInstanceTarget.setId(Identities.uuid());
						scoreInstanceTarget.setTemplateRelaDimension(templateRelaDimensionTarget);
						o_scoreInstanceDAO.merge(scoreInstanceTarget);
					}
				}
				copyTemplateRelaDimensionAndScoreInstance(templateRelaDimension.getChildren(),scoreInstanceList,templateRelaDimensionTarget.getId(), templateTarget);
			}
		}
		if(StringUtils.isNotBlank(riskId)){//拷贝风险关联模板信息
			RiskRelaTemplate riskRelaTemplate = new RiskRelaTemplate(Identities.uuid());
			riskRelaTemplate.setIsCreator(true);
			riskRelaTemplate.setRisk(new Risk(riskId));
			riskRelaTemplate.setTemplate(templateTarget);
			o_riskRelaTemplateDAO.merge(riskRelaTemplate);
			//查询子风险，默认设置关联当前模板。
			List<Risk> riskList = o_riskBO.findRiskByIdAndCompanyIdAndDeleteStatus(riskId, templateSource.getCompany().getId(), true);
			for (Risk risk : riskList) {
				RiskRelaTemplate subRiskRelaTemplate = new RiskRelaTemplate(Identities.uuid());
				subRiskRelaTemplate.setIsCreator(false);//该风险的子风险不是创建者,不可以修改模板
				subRiskRelaTemplate.setRisk(risk);
				subRiskRelaTemplate.setTemplate(templateTarget);
				o_riskRelaTemplateDAO.merge(subRiskRelaTemplate);
			}
		}
	}
	
	
	
	/** 
	  * @Description: 保存模板对应的计算公式
	  * @author jia.song@pcitc.com
	  * @date 2017年4月6日 下午2:47:41 
	  * @param templateId
	  * @param caluFormularContext 
	  */
	@Transactional
	public void saveTemplateRelaCaluFormular(String templateId,String caluFormularContext){
		Template template = o_templateDAO.get(templateId);
		template.setCaluFormula(caluFormularContext);
		o_templateDAO.merge(template);
	}
	
	/**
	 * <pre>
	 * 递归调用拷贝模板关联维度及维度分值
	 * </pre>
	 * 
	 * @author 张 雷
	 * @param templateRelaDimensionSet 模板关联维度的集合
	 * @param scoreInstanceList 该模板下模板关联维度的分值
	 * @param parentId 该模板关联维度集合的新的父级
	 * @param template 模板
	 * @since  fhd　Ver 1.1
	*/
	
	@Transactional
	private void copyTemplateRelaDimensionAndScoreInstance(Set<TemplateRelaDimension> templateRelaDimensionSet,List<ScoreInstance> scoreInstanceList, String parentId, Template template){
		if(null == templateRelaDimensionSet){
			return;
		}
		for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionSet) {
			TemplateRelaDimension templateRelaDimensionTarget = new TemplateRelaDimension();
			BeanUtils.copyProperties(templateRelaDimension, templateRelaDimensionTarget);
			templateRelaDimensionTarget.setId(Identities.uuid());
			templateRelaDimensionTarget.setTemplate(template);
			templateRelaDimensionTarget.setParent(new TemplateRelaDimension(parentId));
			o_templateRelaDimensionDAO.merge(templateRelaDimensionTarget);
			this.mergeTemplateRelaDimensionIdSeqAndLevel(templateRelaDimensionTarget);
			for (ScoreInstance scoreInstance : scoreInstanceList) {//保存模板关联维度下的分值信息
				if(scoreInstance.getTemplateRelaDimension().getId().equals(templateRelaDimension.getId())){
					ScoreInstance scoreInstanceTarget = new ScoreInstance();
					BeanUtils.copyProperties(scoreInstance, scoreInstanceTarget);
					scoreInstanceTarget.setId(Identities.uuid());
					scoreInstanceTarget.setTemplateRelaDimension(templateRelaDimensionTarget);
					o_scoreInstanceDAO.merge(scoreInstanceTarget);
				}
			}
			copyTemplateRelaDimensionAndScoreInstance(templateRelaDimension.getChildren(),scoreInstanceList,templateRelaDimensionTarget.getId(),template);
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#saveTemplateRelaDimension(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void saveTemplateRelaDimension(String parentId, String dimensionId, String templateId){
		Dimension dimension = o_dimensionBO.findDimensionById(dimensionId);
		Template template = o_templateDAO.get(templateId);
		List<Score> scoreList = o_dimensionBO.findScoreBySome(dimensionId, null);
		
		TemplateRelaDimension templateRelaDimension = new TemplateRelaDimension(Identities.uuid());
		templateRelaDimension.setDimension(dimension);
		templateRelaDimension.setTemplate(template);
		if(StringUtils.isNotBlank(parentId)){//如果没有父级Id，则设置为一级，如果有则设置其父级
			templateRelaDimension.setParent(new TemplateRelaDimension(parentId));
		}
		o_templateRelaDimensionDAO.merge(templateRelaDimension);//添加模板关联维度记录
		this.mergeTemplateRelaDimensionIdSeqAndLevel(templateRelaDimension);//递归修改该条记录的IdSeq和level
		
		for (Score score : scoreList) {//将维度下的分值复制一份到ScoreInstance
			ScoreInstance scoreInstance = new ScoreInstance(Identities.uuid());
			scoreInstance.setName(score.getName());//默认使用Score的名称
			scoreInstance.setTemplateRelaDimension(templateRelaDimension);
			scoreInstance.setScore(score);
			scoreInstance.setDesc(score.getDesc());//默认使用Score的描述
			o_scoreInstanceDAO.merge(scoreInstance);
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#mergeTemplate(com.fhd.risk.entity.Template)
	 */
	@Override
	@Transactional
	public void mergeTemplate(Template template) {
		o_templateDAO.merge(template);
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#mergeTemplateBatch(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void mergeTemplateBatch(String jsonString,String companyId, String riskId){
		JSONArray jsonArray=JSONArray.fromObject(jsonString);
		boolean isAdd = false;
		if(jsonArray.size()==0){
			return;
		}
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			String id = jsonObject.getString("id");
			String typeId = jsonObject.getString("type");
			String name = jsonObject.getString("name");
			String desc = jsonObject.getString("desc");
			Integer sort = jsonObject.getInt("sort");
			Template template = null;
			if(StringUtils.isNotBlank(id)){
				template = o_templateDAO.get(id);
			}else{
				template = new Template(Identities.uuid());
				isAdd = true;
			}
			DictEntry type = new DictEntry(typeId);
			template.setName(name);
			template.setType(type);
			template.setSort(sort);
			template.setDesc(desc);
			template.setCompany(new SysOrganization(companyId));
			o_templateDAO.merge(template);
			if(isAdd && StringUtils.isNotBlank(riskId)){//如果是新增，且风险ID不为空，则将该模板关联该风险及以下风险
				RiskRelaTemplate riskRelaTemplate = new RiskRelaTemplate(Identities.uuid());
				riskRelaTemplate.setIsCreator(true);//该风险为创建者，可以修改模板
				riskRelaTemplate.setRisk(new Risk(riskId));
				riskRelaTemplate.setTemplate(template);
				o_riskRelaTemplateDAO.merge(riskRelaTemplate);
				//查询子风险，默认设置关联当前模板。
				List<Risk> riskList = o_riskBO.findRiskByIdAndCompanyIdAndDeleteStatus(riskId, companyId, true);
				for (Risk risk : riskList) {
					RiskRelaTemplate subRiskRelaTemplate = new RiskRelaTemplate(Identities.uuid());
					subRiskRelaTemplate.setIsCreator(false);//该风险的子风险不是创建者,不可以修改模板
					subRiskRelaTemplate.setRisk(risk);
					subRiskRelaTemplate.setTemplate(template);
					o_riskRelaTemplateDAO.merge(subRiskRelaTemplate);
				}
			}
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#mergeTemplateRelaDimensionIdSeqAndLevel(com.fhd.risk.entity.TemplateRelaDimension)
	 */
	@Override
	@Transactional
	public void mergeTemplateRelaDimensionIdSeqAndLevel(TemplateRelaDimension templateRelaDimension){
		if (null != templateRelaDimension.getParent()) {
			TemplateRelaDimension parentTemplateRelaDimension = o_templateRelaDimensionDAO.get(templateRelaDimension.getParent().getId());
			templateRelaDimension.setLevel(parentTemplateRelaDimension.getLevel() + 1);
			templateRelaDimension.setIdSeq(parentTemplateRelaDimension.getIdSeq()+ templateRelaDimension.getId() + ".");
		} else {
			templateRelaDimension.setLevel(1);
			templateRelaDimension.setIdSeq("." + templateRelaDimension.getId() + ".");
			templateRelaDimension.setParent(null);
		}
		o_templateRelaDimensionDAO.merge(templateRelaDimension);
		Iterator<TemplateRelaDimension> it = templateRelaDimension.getChildren().iterator();
		while(it.hasNext()){
			TemplateRelaDimension subTemplateRelaDimension = it.next();
			mergeTemplateRelaDimensionIdSeqAndLevel(subTemplateRelaDimension);
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#mergeTemplateRelaDimension(com.fhd.risk.entity.TemplateRelaDimension)
	 */
	@Override
	@Transactional
	public void mergeTemplateRelaDimension(TemplateRelaDimension templateRelaDimension){
		o_templateRelaDimensionDAO.merge(templateRelaDimension);
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#mergeScoreInstanceBatch(java.lang.String)
	 */
	@Override
	@Transactional
	public void mergeScoreInstanceBatch(String jsonString){
		JSONArray jsonArray=JSONArray.fromObject(jsonString);
		if(jsonArray.size()==0){
			return;
		}
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			String id = jsonObject.getString("id");
			String name = jsonObject.getString("name");
			String desc = jsonObject.getString("desc");
			ScoreInstance scoreInstance = null;
			if(StringUtils.isNotBlank(id)){
				scoreInstance = o_scoreInstanceDAO.get(id);
			}else{
				scoreInstance = new ScoreInstance(Identities.uuid());
			}
			scoreInstance.setName(name);
			scoreInstance.setDesc(desc);
			o_scoreInstanceDAO.merge(scoreInstance);
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#removeScoreInstanceByTemplateRelaDimensionId(java.lang.String)
	 */
	@Override
	@Transactional
	public void removeScoreInstanceByTemplateRelaDimensionId(String templateRelaDimensionId){
		o_scoreInstanceDAO.createQuery("delete ScoreInstance si where si.templateRelaDimension.id=:templateRelaDimensionId")
		.setString("templateRelaDimensionId", templateRelaDimensionId)
		.executeUpdate();
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#removeScoreInstanceByTemplateId(java.lang.String)
	 */
	@Override
	@Transactional
	public void removeScoreInstanceByTemplateId(String templateId){
		o_scoreInstanceDAO.createQuery("delete ScoreInstance si where si.templateRelaDimension.id in (select trd.id from TemplateRelaDimension trd where trd.template.id=:templateId)")
		.setString("templateId", templateId)
		.executeUpdate();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#removeTemplateById(java.lang.String)
	 */
	@Override
	@Transactional
	public void removeTemplateById(String templateId){
		Template template = this.findTemplateById(templateId);
		String fileId = null;
		if(null != template && null != template.getFile()){
			fileId = template.getFile().getId();
		}
		//删除风险关联模板信息
		removeRiskRelaTemplateByTemplateId(templateId);
		//删除模板维度相关信息
		removeTemplateRelaDimensionByTemplateId(templateId);
		//删除该模板的数据
		o_templateDAO.delete(templateId);
		//删除该模板的附件
		if(StringUtils.isNotBlank(fileId)){
			o_fileUploadBO.removeFileById(fileId);
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#removeTemplateRelaDimensionByTemplateId(java.lang.String)
	 */
	@Override
	@Transactional
	public void removeTemplateRelaDimensionByTemplateId(String templateId){
		//删除模板维度分值相关信息
		removeScoreInstanceByTemplateId(templateId);
		//删除模板维度相关信息
		o_templateRelaDimensionDAO.createQuery("delete TemplateRelaDimension trd where trd.template.id=:templateId")
		.setString("templateId", templateId)
		.executeUpdate();
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#removeTemplateRelaDimensionById(java.lang.String)
	 */
	@Override
	@Transactional
	public void removeTemplateRelaDimensionById(String templateRelaDimensionId){
		//删除模板维度分值相关信息
		removeScoreInstanceByTemplateRelaDimensionId(templateRelaDimensionId);
		//删除模板维度相关信息
		o_templateRelaDimensionDAO.delete(templateRelaDimensionId);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#removeRiskRelaTemplateByTemplateId(java.lang.String)
	 */
	@Override
	public void removeRiskRelaTemplateByTemplateId(String templateId){
		o_riskRelaTemplateDAO.createQuery("delete RiskRelaTemplate riskRelaTemplate where riskRelaTemplate.template.id =:templateId)")
		.setString("templateId", templateId)
		.executeUpdate();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#findScoreInstanceBySome(java.lang.String, java.lang.String)
	 */
	@Override
	public List<ScoreInstance> findScoreInstanceBySome(String templateId,String templateRelaDimensionId){
		Criteria criteria = o_scoreInstanceDAO.createCriteria();
		criteria.createAlias("templateRelaDimension", "templateRelaDimension");
		criteria.createAlias("templateRelaDimension.template", "template");
		if(StringUtils.isNotBlank(templateId)){
			criteria.add(Restrictions.eq("template.id", templateId));
		}
		if(StringUtils.isNotBlank(templateRelaDimensionId)){
			criteria.add(Restrictions.eq("templateRelaDimension.id", templateRelaDimensionId));
		}
		criteria.createAlias("score", "score").addOrder(Order.asc("score.sort"));
		return criteria.list();
	}

	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#findTemplateById(java.lang.String)
	 */
	@Override
	public Template findTemplateById(String templateId) {
		return o_templateDAO.get(templateId);
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#findTemplateByQuery(java.lang.String)
	 */
	@Override
	public List<Template> findTemplateByQuery(String query){
		Criteria criteria = o_templateDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Property.forName("name").like(query, MatchMode.ANYWHERE), Property.forName("desc").like(query, MatchMode.ANYWHERE)));
		}
		criteria.addOrder(Order.asc("sort"));
		return criteria.list();
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#findTemplateRelaDimensionById(java.lang.String)
	 */
	@Override
	public TemplateRelaDimension findTemplateRelaDimensionById(String templateRelaDimensionId){
		return o_templateRelaDimensionDAO.get(templateRelaDimensionId);
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#findTemplateRelaDimensionIdSetByQuery(java.lang.String)
	 */
	@Override
	public Set<String> findTemplateRelaDimensionIdSetByQuery(String query){
		Set<String> idSet = new HashSet<String>();
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.createAlias("dimension", "dimension").add(Restrictions.like("dimension.name",query,MatchMode.ANYWHERE));
		}
		List<TemplateRelaDimension> templateRelaDimensionList = criteria.list();
		for (TemplateRelaDimension templateRelaDimension : templateRelaDimensionList) {
			String[] idsTemp = templateRelaDimension.getIdSeq().split("\\.");
			idSet.addAll(Arrays.asList(idsTemp));
		}
		return idSet;
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#findTemplateRelaDimensionBySome(java.lang.String, java.lang.String)
	 */
	@Override
	public List<TemplateRelaDimension> findTemplateRelaDimensionBySome(String query, String templateId){
		Criteria criteria = o_templateRelaDimensionDAO.createCriteria();
		Set<String> idSet = this.findTemplateRelaDimensionIdSetByQuery(query);
		if(idSet.size()>0){
			criteria.add(Restrictions.in("id", idSet));
		}else{
			criteria.add(Restrictions.isNull("id"));
		}
		if(StringUtils.isNotBlank(templateId)){
			criteria.add(Restrictions.eq("template.id", templateId));
		}else{
			criteria.add(Restrictions.isNull("id"));
		}
		criteria.addOrder(Order.asc("sort"));
		return criteria.list();
	}
	/**
	 * (non-Javadoc)
	 * @see com.fhd.ra.interfaces.risk.ITemplateBO#findRiskRelaTemplateBySome(java.lang.String, java.lang.String)
	 */
	@Override
	public List<RiskRelaTemplate> findRiskRelaTemplateBySome(String riskId, String templateId){
		Criteria criteria = o_riskRelaTemplateDAO.createCriteria();
		if(StringUtils.isNotBlank(riskId)){
			criteria.add(Restrictions.eq("risk.id", riskId));
		}
		if(StringUtils.isNotBlank(templateId)){
			criteria.add(Restrictions.eq("template.id", templateId));
		}
		return criteria.list();
	}
	
	/**
	 * 查询模版全部信息,并已MAP方式存储(模板主键ID,模板实体)
	 * @return HashMap<String, Template>
	 * @author 金鹏祥
	 * */
	public HashMap<String, Template> findTemplateAllMap(){
		HashMap<String, Template> map = new HashMap<String, Template>();
		Criteria criteria = o_templateDAO.createCriteria();
		List<Template> list = null;
		list = criteria.list();
		
		for (Template template : list) {
			map.put(template.getId(), template);
		}
		
		return map;
	}
	
	/**
	 * 查询模版全部信息,并已MAP方式存储(模板名称,模板实体)
	 * @return HashMap<String, Template>
	 * @author 金鹏祥
	 * */
	public HashMap<String, Template> findTemplateByNameAllMap(){
		HashMap<String, Template> map = new HashMap<String, Template>();
		Criteria criteria = o_templateDAO.createCriteria();
		List<Template> list = null;
		list = criteria.list();
		
		for (Template template : list) {
			map.put(template.getName(), template);
		}
		
		return map;
	}
	
	/**
	 * 查询登录人所在公司的模板
	 * @author  王再冉
	 * @param query		查询条件字段
	 * @param companyId	登录人公司
	 * @return
	 */
	public List<Template> findTemplateByQueryAndCompanyId(String query,String companyId){
		Criteria criteria = o_templateDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Property.forName("name").like(query, MatchMode.ANYWHERE), Property.forName("desc").like(query, MatchMode.ANYWHERE)));
		}
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		return criteria.list();
	}
	
	/**
	 * 通过类型id查询模板
	 * @author 王再冉
	 * @param typeId 数据字典模板类型
	 * @return
	 */
	public Template findTemplateByType(String typeId){
		Criteria criteria = o_templateDAO.createCriteria();
		List<Template> list = null;
		if(StringUtils.isNotBlank(typeId)){
			criteria.add(Restrictions.eq("type.id", typeId));
		}
		list = criteria.list();
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 根据名称完全匹配进行查询模板
	 * @author  元杰
	 * @param templateName	查询名称
	 * @return Template
	 */
	public Template findTemplateByName(String templateName){
		Template template = null;
		Criteria criteria = o_templateDAO.createCriteria();
		if(StringUtils.isNotBlank(templateName)){
			criteria.add(Restrictions.eq("name", templateName));
		}
		List<Template> templateList = criteria.list();
		if(templateList != null && templateList.size() > 0){
			template = templateList.get(0);
		}
		return template;
	}
}

