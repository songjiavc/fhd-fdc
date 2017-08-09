package com.fhd.icm.business.standard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.standard.StandardDAO;
import com.fhd.dao.icm.standard.StandardRelaOrgDAO;
import com.fhd.dao.process.ProcessDAO;
import com.fhd.dao.sys.dic.DictEntryDAO;
import com.fhd.dao.sys.orgstructure.SysOrganizationDAO;
import com.fhd.entity.icm.standard.Standard;
import com.fhd.entity.icm.standard.StandardRelaOrg;
import com.fhd.entity.icm.standard.StandardRelaProcessure;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.entity.sys.orgstructure.SysEmpOrg;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.bpm.StandardBpmBO;
import com.fhd.icm.interfaces.standard.IStandardBO;
import com.fhd.icm.utils.IcmStandardUtils;
import com.fhd.icm.web.controller.bpm.StandardBpmObject;
import com.fhd.icm.web.form.StandardForm;
import com.fhd.sys.business.auth.RoleBO;

/**
 * Standard_内控BO ClassName:StandardBO
 * 
 * @author 刘中帅
 * @version
 * @since Ver 1.1
 * @Date 2012 2012-12-11 上午10:21:00
 * 
 * @see
 */
@Service
@SuppressWarnings({"unchecked","deprecation"})
public class StandardBO implements IStandardBO {

	@Autowired
	private StandardDAO o_standardDAO;
	@Autowired
	private StandardRelaOrgBO o_standardRelaOrgBO;
	@Autowired
	private StandardRelaOrgDAO o_standardRelaOrgDAO;
	@Autowired
	private SysOrganizationDAO o_sysOrgnizationDAO;
	@Autowired
	private DictEntryDAO o_dictEntryDAO;
	@Autowired
	private ProcessDAO o_processDAO;
	@Autowired
	private StandardRelaProcessureBO o_standardRelaProcessureBO;
	@Autowired
	private RoleBO o_sysRoleBO;
	@Autowired
	private StandardBpmBO o_standardBpmBO;

	/**
	 * <pre>
	 * 根据内控ID获得指标实体
	 * </pre>
	 * 
	 * @author 刘中帅
	 * @param id
	 * 内控ID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	public Standard findStandardById(String standardId) {
		return o_standardDAO.get(standardId);
	}
	/**
	 * <pre>
	 * 根据内控ID集合获得指标实体
	 * </pre>
	 * 
	 * @author 刘中帅
	 * @param id
	 * 内控ID
	 * @return
	 * @since fhd　Ver 1.1
	 */
	public List<Map<String,Object>> findStandardByIds(String[] standardIds) {
		List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();
		Criteria criteria = o_standardDAO.createCriteria();
		criteria.add(Restrictions.in("id", standardIds));
		List<Standard> standardList =criteria.list();
		for(Standard standard:standardList){
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("id", standard.getId());
			map.put("dbid", standard.getId());
			map.put("code", standard.getCode());
			map.put("text", standard.getName());
			listMap.add(map);
		}
		return listMap;
	}
	/**
	 * 根据传过来的id集合查找其下面的子节点并且etype等于0的
	 * 
	 * @param clickedNodeId
	 *            前台传过来的id
	 * @return map 集合
	 */
	public Map<String, Object> findStandardListByPage(Page<Standard> page, String query,String clickedNodeIds,
			String isLeaf,String companyId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Standard.class);
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.or(Property.forName("code").like(query, MatchMode.ANYWHERE), Property.forName("name").like(query, MatchMode.ANYWHERE)));
		}
		String[] ids;
		if (StringUtils.isNotBlank(clickedNodeIds)) {
			ids = clickedNodeIds.split("\\,");
			criteria.add(Restrictions.eq("type", "0"));
			criteria.add(Restrictions.in("parent.id", ids));
			criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		} else {
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		List<Standard> standardList =o_standardDAO.findPage(criteria, page, false).getResult();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (Standard standard : standardList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("controlRequirement", standard.getControlRequirement());
			if(StringUtils.isNotBlank(standard.getStatus())){
				map.put("status", standard.getStatus());
			}
			if(StringUtils.isNotBlank(standard.getDealStatus())){
				map.put("dealStatus", standard.getDealStatus());
			}
			if(null!=standard.getStandardRelaOrg()&&standard.getStandardRelaOrg().size()>0){
				String orgId="";
				for(StandardRelaOrg standardRelaOrg:standard.getStandardRelaOrg()){
					orgId=standardRelaOrg.getOrg().getOrgname();
				}
				map.put("dept",orgId);
			}
			String controPoint = "";
			if(null!=standard.getControlPoint()){
				 controPoint = o_dictEntryDAO.get(standard.getControlPoint()).getName();
			}
			map.put("idSeq", standard.getIdSeq());
			map.put("code", standard.getCode());
			map.put("text", standard.getName());
			map.put("name", standard.getName());
			map.put("controlPoint", controPoint);
			map.put("id", standard.getId());
			dataList.add(map);
		}
		result.put("datas", dataList);
		result.put("totalCount", standardList.size());

		return result;
	}

	/**
	 * 返回内控标准列表
	 * @author 元杰
	 * @param page
	 * @param query
	 * @param dealStatus
	 * @param companyId
	 * @return page
	 */
	public Page<Standard> findStandardBpmListByPage(Page<Standard> page, String query, String dealStatus, String companyId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Standard.class);
		if(StringUtils.isNotBlank(query)){
			criteria.add(Restrictions.like("name", query, MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(dealStatus)){
			criteria.add(Restrictions.in("dealStatus", dealStatus.split(",")));
		}
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		//过滤内控标准
		criteria.add(Restrictions.eq("type", Contents.STANDARD_TYPE_CLASS));
		criteria.addOrder(Order.desc("createTime"));
		return o_standardDAO.findPage(criteria, page, false);
	}
	
	/**
	 * <pre>
	 * 根据内控标识获取所有下级内控
	 * </pre>
	 * 
	 * @author 刘中帅
	 * @param id内控标识
	 * @param self是否包含自己
	 * @Data 2012 12 11 上午10:15
	 * @return 包含Standard实体的集合
	 * @since fhd　Ver 1.1
	 */
	public List<Standard> findChildsStandardBySome(String id, String type,
			boolean self) {
		List<Standard> standardList = new ArrayList<Standard>();
		if (StringUtils.isNotBlank(id)) {
			Criteria criteria = this.o_standardDAO.createCriteria();
			if (self) {// 包含自己
				criteria.add(Restrictions.or(
						Property.forName("parent.id").eq(id),
						Property.forName("id").eq(id)));
			} else {
				criteria.add(Property.forName("parent.id").eq(id));
				if (StringUtils.isNotEmpty(type)) {
					criteria.add(Property.forName("type").eq(type));
				}
			}
			standardList = criteria.list();
		}
		return standardList;
	}

	/**
	 * <pre>
	 *通过id查询子节点
	 * </pre>
	 * @author 刘中帅
	 * @param id 内控标准id
	 * @param type 标准类型
	 * @return
	 * @since  fhd　Ver 1.2
	 * @editor 增加判断条件  type为1才增加父ID 为null查询的条件
	 */
	public List<Standard> findChildStandardById(String id,String companyId) {
		Criteria criteria = this.o_standardDAO.createCriteria();
		if (StringUtils.isNotEmpty(id)) {
			criteria.add(Restrictions.eq("parent.id", id));
		} else {
				criteria.add(Restrictions.isNull("parent.id"));
		}
		criteria.add(Restrictions.eq("company.id", companyId));
		criteria.add(Restrictions.eq("type", "1")); 
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL)); 
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}

	/**
	 * 通过递归判断当前节点（非子节点）下的叶子节点是否匹配查询条件
	 * 
	 * @param id
	 *            当前选中节点的id
	 * @param query
	 *            查询条件
	 * @author 刘中帅
	 * @return 符合条件的子节点集合
	 */
	public List<Standard> findStandardById(String id, String query,
			List<Standard> standardResult, String companyId,
			List<Standard> standardAllList) {

		List<Standard> childList = new ArrayList<Standard>();
		// 查询子节点
		for (Standard standard : standardAllList) {
			if (null != standard.getParent() && StringUtils.isNotBlank(standard.getParent().getId()) && standard.getParent().getId().equals(id)){
				childList.add(standard);
			}
		}
		for (Standard standard : childList) {
			if (null!=query && standard.getName().indexOf(query) != -1) {
				standardResult.add(standard);
			} else {
				findStandardById(standard.getId(), query, standardResult, companyId, standardAllList);
			}
		}
		return standardResult;
	}
	
	/**
	 * <pre>
	 * 通过部门id查询内控标准
	 * </pre>
	 * @author 元杰
	 * @param deptId 部门ID
	 * @param type 标准类型
	 * @return
	 * @since  fhd　Ver 1.1
	 */
	public List<Standard> findStandardsByDeptId(String deptId, String type, String companyId) {
		Criteria criteria = this.o_standardDAO.createCriteria();
		if (StringUtils.isNotBlank(deptId)){
			criteria.createAlias("standardRelaOrg.org", "sro");
			criteria.add(Restrictions.eq("sro.id", deptId));
		}
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		if(StringUtils.isNotBlank(type)){
			criteria.add(Restrictions.eq("type", type));
		}
		return criteria.list();
	}
	
	/**
	 *   保存内控标准和要求的审批意见
	 * @author 元杰
	*/
	@Transactional
	public void saveStandardAdvice(StandardForm standardForm){
		if(StringUtils.isNotBlank(standardForm.getId())){
			Standard standard = o_standardDAO.get(standardForm.getId());
			standard.setFeedback(standardForm.getFeedback());
			o_standardDAO.merge(standard);
		}
	}
	
	/**
	 *   保存内控标准以及下属的内控要求
	 * @author 元杰
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @param step 五步中的第几部
	*/
	@Transactional
	public Standard saveStandard(String executionId, String businessId, StandardForm standardForm, String step) throws IllegalAccessException, InvocationTargetException{
		Standard standard = null;
		if(StringUtils.isNotBlank(standardForm.getId())){//修改
			standard = o_standardDAO.get(standardForm.getId());
		}else{//新增
			standard = new Standard();
		}
		if("3".equals(step)){//第3步只保存要求对应的意见
			//保存要求信息  
	        JSONArray standardControlForm=JSONArray.fromObject(standardForm.getStandardControlFormsStr());
			if(standardControlForm != null && standardControlForm.size() > 0){   //
				for(int i = 0 ;i < standardControlForm.size(); i++){
					JSONObject jsonObject = standardControlForm.getJSONObject(i);
					//只保存内控要求对应的反馈意见
					if(StringUtils.isNotBlank(jsonObject.getString("cid"))){
						String standardControlId = jsonObject.getString("cid");
						Standard standardControl = o_standardDAO.get(standardControlId);
						String feedback = jsonObject.getString("cfeedback");
						standardControl.setFeedback(feedback);
						o_standardDAO.merge(standardControl);
					}
				}
			}
			o_standardBpmBO.startCurCompanyStandardApplyStepThree(executionId, businessId, standard); //触发本公司流程
		}else if("4".equals(step)){
			//保存要求信息  
	        JSONArray standardControlForm=JSONArray.fromObject(standardForm.getStandardControlFormsStr());
			if(standardControlForm != null && standardControlForm.size() > 0){   //
				for(int i = 0 ;i < standardControlForm.size(); i++){
					JSONObject jsonObject = standardControlForm.getJSONObject(i);
					String isSubCompany = jsonObject.getString("inferior");//是否适用于下级机构
					if(Contents.DICT_Y.equals(isSubCompany)){//是
						jsonObject.getString("csubCompanyId");
					}else if(Contents.DICT_N.equals(isSubCompany)){//否
						Standard standardControl = null;
						String standardControlId = "";
						if(StringUtils.isNotBlank(jsonObject.getString("cid"))){
							standardControlId = jsonObject.getString("cid");
							standardControl = o_standardDAO.get(standardControlId);
						}else{
							standardControl = new Standard();
							standardControl.setId(Identities.uuid2());
						}
						standardControl.setCode(this.findStandardCode());
						standardControl.setName(jsonObject.getString("cname"));
						standardControl.setIsLeaf(true);
						standardControl.setType(Contents.STANDARD_TYPE_REQUIREMENT);
//						standardControl.setCompanyId(UserContext.getUser().getCompanyid());
						standardControl.setParent(standard);
						if(jsonObject.has("cstandardControlPoint")){
							String controlPoint = jsonObject.getString("cstandardControlPoint");
							if(StringUtils.isNotBlank(controlPoint)){
								standardControl.setControlPoint(controlPoint);
							}
						}
						if (jsonObject.has("ccontrolLevelId") && StringUtils.isNotBlank(jsonObject.getString("ccontrolLevelId"))) {
							// 控制层级
							DictEntry dictEntryControl = new DictEntry();
							dictEntryControl.setId(jsonObject.getString("ccontrolLevelId"));
							standardControl.setControlLevel(dictEntryControl);
						}
						if(jsonObject.has("statusId")){
							standardControl.setDealStatus(jsonObject.getString("statusId"));//更新内控要求的处理状态
						}
						o_standardDAO.merge(standardControl);
						//保存要求关联部门
						String deptId = IcmStandardUtils.findIdbyJason(jsonObject.getString("cdeptId"), "id");
						if(StringUtils.isNotBlank(deptId)){
							o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standardControlId);
							StandardRelaOrg standardRelaOrg = new StandardRelaOrg();
							standardRelaOrg.setId(Identities.uuid2());
							standardRelaOrg.setStandard(standardControl);
							standardRelaOrg.setOrg(o_sysOrgnizationDAO.get(deptId));
							standardRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
							o_standardRelaOrgDAO.merge(standardRelaOrg);
						}
						//保存要求相关流程
						o_standardRelaProcessureBO.delStandardRelaProcessureByStandardId(standardControlId);
						if(StringUtils.isNotBlank(jsonObject.getString("cprocessId"))){
							StandardRelaProcessure standardRelaProcessure = new StandardRelaProcessure();
							standardRelaProcessure.setId(Identities.uuid2());
							standardRelaProcessure.setStandard(standardControl);
							standardRelaProcessure.setProcessure(o_processDAO.get(jsonObject.getString("cprocessId")));
							o_standardRelaProcessureBO.saveStandardRelaProcessure(standardRelaProcessure);
						}
					}
				}
			}
			o_standardBpmBO.startCurCompanyStandardApplyStepFour(executionId, businessId, standard); //触发本公司流程
		}else{
//			if(StringUtils.isNotBlank(standardForm.getControlLevel().getId())){
//				standard.setControlLevel(standardForm.getControlLevel());
//			}
			//编号，如果表单中code为空，则自动生成
			if(StringUtils.isNotBlank(standardForm.getCode())){
				standard.setCode(standardForm.getCode());
			}else{
				standard.setCode(this.findStandardCode());
			}
			standard.setName(standardForm.getName());
			standard.setStatus(Contents.STATUS_SUBMITTED);
			standard.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
			standard.setDealStatus(Contents.DEAL_STATUS_HANDLING);
			standard.setUpdateDeadline(standardForm.getUpdateDeadline());
			standard.setCompany(o_sysOrgnizationDAO.get(UserContext.getUser().getCompanyid()));
			standard.setIsLeaf(true);
//			standard.setLevel(1);//20131205bug修改，isLeaf和 elevel
			Standard standardParent = null;
			standard.setType(Contents.STANDARD_TYPE_CLASS);//先不保存，判断如果有本级机构的要求时再保存
			if(null != standardForm.getParent() && StringUtils.isNotBlank(standardForm.getParent().getId())){
				standardParent = o_standardDAO.get(standardForm.getParent().getId());//20131205bug修改，isLeaf和 elevel
				standard.setParent(standardForm.getParent());
				standard.setLevel(standardParent.getLevel() + 1);//20131205bug修改，isLeaf和 elevel
			}
			//20131205bug修改，isLeaf和 elevel
			if(null != standardParent){
				standardParent.setIsLeaf(false);
				o_standardDAO.merge(standardParent);
			}
			//先删除原来保存的要求数据
			Criteria criteria2 = o_standardDAO.createCriteria();
			criteria2.add(Restrictions.eq("parent.id", standard.getId()));
			List<Standard> standardList = criteria2.list();
			for(Standard standard1 : standardList){
				o_standardRelaProcessureBO.delStandardRelaProcessureByStandardId(standard1.getId());
				o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standard1.getId());
				o_standardDAO.delete(standard1);
			}
			
			//保存要求信息  
	        JSONArray standardControlForm=JSONArray.fromObject(standardForm.getStandardControlFormsStr());
	        Set<String> subCompanyIdSet = new HashSet<String>();//用于存储使用与下级机构的要求ID集合
	        boolean hasSubFlag = false; //是否含有适用于下级机构的要求
	        boolean curCompanyFlag = false; //是否含有适用于本公司的要求
	        List<Standard> standardControlList = new ArrayList<Standard>();//存放针对下级机构的内控要求，在saveSubCompanyStandards方法中进行数据的写入
	        
	        //先循环一遍，确定此标准是否是只针对下级公司
			if(standardControlForm != null && standardControlForm.size() > 0){ 
				for(int i = 0 ;i < standardControlForm.size(); i++){
					JSONObject jsonObject = standardControlForm.getJSONObject(i);
					String isSubCompany = jsonObject.getString("inferior");//是否适用于下级机构
					if(Contents.DICT_N.equals(isSubCompany)){//针对本公司
						curCompanyFlag = true;
					}
				}
			}
			
			if(!curCompanyFlag){//此标准下挂要求全都针对下级公司，没有针对本级公司的，此情况不保存本机公司的保准和要求数据
				//保存数据
				if(standardControlForm != null && standardControlForm.size() > 0){ 
					for(int i = 0 ;i < standardControlForm.size(); i++){
						JSONObject jsonObject = standardControlForm.getJSONObject(i);
						String isSubCompany = jsonObject.getString("inferior");//是否适用于下级机构
						if(Contents.DICT_Y.equals(isSubCompany)){//是
							hasSubFlag = true;//是否需要下发下级机构的标示
							
							String subCompanyIdJson = jsonObject.getString("csubCompanyId");
							String subCompanyIdStr = IcmStandardUtils.findIdbyJason(subCompanyIdJson, "id");
							String[] subCompanyIds = subCompanyIdStr.split(",");
							for(String subCompanyId : subCompanyIds){
								subCompanyIdSet.add(subCompanyId);//去重后的下级机构IdSet
								
								//保存针对下级机构的内控要求
								Standard standardControl = new Standard();
								String standardControlId = Identities.uuid2();
								standardControl.setId(standardControlId);
								standardControl.setCode(this.findStandardCode());
								standardControl.setName(jsonObject.getString("cname"));
								standardControl.setIsLeaf(false);
								standardControl.setLevel(2);
								standardControl.setType(Contents.STANDARD_TYPE_REQUIREMENT);
								standardControl.setStatus(Contents.STATUS_SAVED);
								standardControl.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
								standardControl.setCompany(o_sysOrgnizationDAO.get(subCompanyId));
								standardControlList.add(standardControl);
							}
						}else if("adviceOnly".equals(isSubCompany) && "5".equals(step)){
							o_standardBpmBO.startCurCompanyStandardApplyStepFive(executionId, businessId, null, null); //触发本公司流程
						}
					}
				}
				if(hasSubFlag){
					List<String> standardIdList = saveSubCompanyStandards(subCompanyIdSet, standard, standardControlList);
					for(String sid : standardIdList){
						o_standardBpmBO.startSubCompanyStandardApply(sid, subCompanyIdSet);//触发子公司流程
					}
					if(StringUtils.isNotBlank(standard.getId())){//全部针对下级机构，则删除本级
						this.delStandardAndSub(standard);
					}
				}
			}else{//此情况包括全是本级机构要求和本级、下级混合的情况
				String standardId = Identities.uuid2();
				if(StringUtils.isBlank(standard.getId())){
					standard.setId(standardId);
				}else{
					standardId = standard.getId();
				}
				if(null != standardForm.getParent() && StringUtils.isNotBlank(standardForm.getParent().getId())){
					Standard tempStandard = this.findStandardById(standardForm.getParent().getId());//获取选择标准分类实体，用来获得idseq
					standard.setIdSeq(tempStandard.getIdSeq() + standardId + ".");
				}else{
					standard.setIdSeq("." + standardId + ".");
				}
				o_standardDAO.merge(standard);
				
				//保存内控标准关联部门
				if(StringUtils.isNotBlank(UserContext.getUser().getMajorDeptId())){
					o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standard.getId());
					StandardRelaOrg standardRelaOrg = new StandardRelaOrg();
					standardRelaOrg.setId(Identities.uuid2());
					standardRelaOrg.setStandard(standard);
					standardRelaOrg.setOrg(o_sysOrgnizationDAO.get(UserContext.getUser().getMajorDeptId()));
					standardRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
					o_standardRelaOrgDAO.merge(standardRelaOrg);
				}

				//保存数据
				if(standardControlForm != null && standardControlForm.size() > 0){ 
					for(int i = 0 ;i < standardControlForm.size(); i++){
						JSONObject jsonObject = standardControlForm.getJSONObject(i);
						String isSubCompany = jsonObject.getString("inferior");//是否适用于下级机构
						if(Contents.DICT_Y.equals(isSubCompany)){//是
							hasSubFlag = true;//是否需要下发下级机构的标示
							
							String subCompanyIdJson = jsonObject.getString("csubCompanyId");
							String subCompanyIdStr = IcmStandardUtils.findIdbyJason(subCompanyIdJson, "id");
							String[] subCompanyIds = subCompanyIdStr.split(",");
							for(String subCompanyId : subCompanyIds){
								subCompanyIdSet.add(subCompanyId);//去重后的下级机构IdSet
								
								//保存针对下级机构的内控要求
								Standard standardControl = new Standard();
								String standardControlId = Identities.uuid2();
								standardControl.setId(standardControlId);
								standardControl.setCode(this.findStandardCode());
								standardControl.setName(jsonObject.getString("cname"));
								standardControl.setIsLeaf(false);
								standardControl.setLevel(2);
								standardControl.setIdSeq(standard.getIdSeq() + standardControlId +  ".");
								standardControl.setType(Contents.STANDARD_TYPE_REQUIREMENT);
								standardControl.setStatus(Contents.STATUS_SAVED);
								standardControl.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
								standardControl.setCompany(o_sysOrgnizationDAO.get(subCompanyId));
								standardControl.setParent(standard);
								standardControlList.add(standardControl);
							}
						}else if(Contents.DICT_N.equals(isSubCompany)){//否
							curCompanyFlag = true;
							Standard standardControl = null;
							String standardControlId = "";
//							if(StringUtils.isNotBlank(jsonObject.getString("cid"))){
//								standardControlId = jsonObject.getString("cid");
//								standardControl = o_standardDAO.get(standardControlId);
//							}else{
								standardControl = new Standard();
								standardControlId = Identities.uuid2();
								standardControl.setId(standardControlId);
//							}
							standardControl.setCode(this.findStandardCode());
							standardControl.setName(jsonObject.getString("cname"));
							standardControl.setIsLeaf(true);
							standardControl.setLevel(2);
							standardControl.setIdSeq(standard.getIdSeq() + standardControlId +  ".");
							standardControl.setType(Contents.STANDARD_TYPE_REQUIREMENT);
							standardControl.setStatus(Contents.STATUS_SUBMITTED);
							standardControl.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
							standardControl.setCompany(o_sysOrgnizationDAO.get(UserContext.getUser().getCompanyid()));
							standardControl.setParent(standard);
							if(jsonObject.has("cstandardControlPoint")){
								String controlPoint = jsonObject.getString("cstandardControlPoint");
								if(StringUtils.isNotBlank(controlPoint)){
									standardControl.setControlPoint(controlPoint);
								}
							}
							o_standardDAO.merge(standardControl);
							//保存要求关联部门
							String deptId = IcmStandardUtils.findIdbyJason(jsonObject.getString("cdeptId"), "id");
							if(StringUtils.isNotBlank(deptId)){
								o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standardControlId);
								StandardRelaOrg standardRelaOrg = new StandardRelaOrg();
								standardRelaOrg.setId(Identities.uuid2());
								standardRelaOrg.setStandard(standardControl);
								standardRelaOrg.setOrg(o_sysOrgnizationDAO.get(deptId));
								standardRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
								o_standardRelaOrgDAO.merge(standardRelaOrg);
							}
							//保存要求相关流程
							if(StringUtils.isNotBlank(jsonObject.getString("cprocessId"))){
								o_standardRelaProcessureBO.delStandardRelaProcessureByStandardId(standardControlId);
								StandardRelaProcessure standardRelaProcessure = new StandardRelaProcessure();
								standardRelaProcessure.setId(Identities.uuid2());
								standardRelaProcessure.setStandard(standardControl);
								standardRelaProcessure.setProcessure(o_processDAO.get(jsonObject.getString("cprocessId")));
								o_standardRelaProcessureBO.saveStandardRelaProcessure(standardRelaProcessure);
							}
						}else if("adviceOnly".equals(isSubCompany) && "5".equals(step)){
							o_standardBpmBO.startCurCompanyStandardApplyStepFive(executionId, businessId, null, null); //触发本公司流程
						}
					}
				}
				if(hasSubFlag){
					List<String> standardIdList = saveSubCompanyStandards(subCompanyIdSet, standard, standardControlList);
					for(String sid : standardIdList){
						o_standardBpmBO.startSubCompanyStandardApply(sid, subCompanyIdSet);//触发子公司流程
					}
				}
				if(curCompanyFlag){//第一步
					o_standardBpmBO.startCurCompanyStandardApplyStepOne(executionId, businessId, standard); //触发本公司流程
				}
				//删除subCompanyids有数据的，是standard孩子节点的要求
				List<Standard> standardControlDeleteList = new ArrayList<Standard>();
				Criteria criteria = this.o_standardDAO.createCriteria();
				String parentId = standard.getId();
				if (StringUtils.isNotBlank(parentId)) {
					criteria.add(Restrictions.eq("parent.id", parentId));
				}
				criteria.add(Restrictions.isNotNull("subCompanyids"));
				standardControlDeleteList = criteria.list();
				for(Standard standardControl : standardControlDeleteList){
					o_standardDAO.delete(standardControl.getId());
				}
				
			}
			
		}
		return standard;
	}
	/**
	 * 仅保存内控标准，不触发工作流
	 * @author 元杰
	*/
	@Transactional
	public Standard saveStandardData(StandardForm standardForm) {
		Standard standard =  new Standard();
		standard.setId(standardForm.getId());
		//编号，如果表单中code为空，则自动生成
		if(StringUtils.isNotBlank(standardForm.getCode())){
			standard.setCode(standardForm.getCode());
		}else{
			standard.setCode(this.findStandardCode());
		}
		standard.setName(standardForm.getName());
		standard.setStatus(Contents.STATUS_SAVED);
		standard.setDealStatus(Contents.DEAL_STATUS_NOTSTART);
		standard.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
		standard.setUpdateDeadline(standardForm.getUpdateDeadline());
		String standardId = Identities.uuid2();
		if(StringUtils.isBlank(standard.getId())){
			standard.setId(standardId);
		}else{
			standardId = standard.getId();
		}
		standard.setIsLeaf(true);
		Standard standardParent = null;
		if(null != standardForm.getParent() && StringUtils.isNotBlank(standardForm.getParent().getId())){
			standard.setParent(standardForm.getParent());
			standardParent = this.findStandardById(standardForm.getParent().getId());//获取选择标准分类实体，用来获得idseq
			standard.setIdSeq(standardParent.getIdSeq() + standardId + ".");
			standard.setLevel(standardParent.getLevel() + 1);//20131105bug修复
		}else{
			standard.setIdSeq("." + standardId + ".");
			standard.setLevel(1);//20131105bug修复
		}
		if(null != standardParent){//20131105bug修复
			standardParent.setIsLeaf(false);
			o_standardDAO.merge(standardParent);
		}
		standard.setCompany(o_sysOrgnizationDAO.get(UserContext.getUser().getCompanyid()));
		standard.setType(Contents.STANDARD_TYPE_CLASS);
		o_standardDAO.merge(standard);
		//保存内控标准关联部门
		if(StringUtils.isNotBlank(UserContext.getUser().getMajorDeptId())){
			o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standard.getId());
			StandardRelaOrg standardRelaOrg = new StandardRelaOrg();
			standardRelaOrg.setId(Identities.uuid2());
			standardRelaOrg.setStandard(standard);
			standardRelaOrg.setOrg(o_sysOrgnizationDAO.get(UserContext.getUser().getMajorDeptId()));
			standardRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
			o_standardRelaOrgDAO.merge(standardRelaOrg);
		}
		//先删除原来保存的要求数据
		Criteria criteria = o_standardDAO.createCriteria();
		criteria.add(Restrictions.eq("parent.id", standard.getId()));
		List<Standard> standardList = criteria.list();
		for(Standard standard1 : standardList){
			o_standardRelaProcessureBO.delStandardRelaProcessureByStandardId(standard1.getId());
			o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standard1.getId());
			o_standardDAO.delete(standard1);
		}
		//保存要求信息  
        JSONArray standardControlForm=JSONArray.fromObject(standardForm.getStandardControlFormsStr());
		if(standardControlForm != null && standardControlForm.size() > 0){ 
			for(int i = 0 ;i < standardControlForm.size(); i++){
				JSONObject jsonObject = standardControlForm.getJSONObject(i);
				String isSubCompany = jsonObject.getString("inferior");//是否适用于下级机构
				if(Contents.DICT_Y.equals(isSubCompany)){//是
					Standard standardControl = new Standard();
					String standardControlId = Identities.uuid2();
					standardControl.setId(standardControlId);
					String subCompanyIds = IcmStandardUtils.findIdbyJason(jsonObject.getString("csubCompanyId"), "id");
					standardControl.setCode(this.findStandardCode());
					standardControl.setName(jsonObject.getString("cname"));
					standardControl.setIsLeaf(false);
					standardControl.setLevel(2);
					standardControl.setIdSeq(standard.getIdSeq() + standardControlId +  ".");
					standardControl.setType(Contents.STANDARD_TYPE_REQUIREMENT);
					standardControl.setStatus(Contents.STATUS_SAVED);
					standardControl.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
					standardControl.setCompany(o_sysOrgnizationDAO.get(UserContext.getUser().getCompanyid()));
					standardControl.setSubCompanyids(subCompanyIds);
					standardControl.setParent(standard);
					o_standardDAO.merge(standardControl);
				}else if(Contents.DICT_N.equals(isSubCompany)){//否
					Standard standardControl = new Standard();
					String standardControlId = Identities.uuid2();
					standardControl.setId(standardControlId);
					standardControl.setCode(this.findStandardCode());
					standardControl.setName(jsonObject.getString("cname"));
					standardControl.setIsLeaf(true);
					standardControl.setLevel(2);
					standardControl.setIdSeq(standard.getIdSeq() + standardControlId +  ".");
					standardControl.setType(Contents.STANDARD_TYPE_REQUIREMENT);
					standardControl.setStatus(Contents.STATUS_SAVED);
					standardControl.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
					standardControl.setCompany(o_sysOrgnizationDAO.get(UserContext.getUser().getCompanyid()));
					standardControl.setParent(standard);
					if(jsonObject.has("cstandardControlPoint")){
						String controlPoint = jsonObject.getString("cstandardControlPoint");
						if(StringUtils.isNotBlank(controlPoint)){
							standardControl.setControlPoint(controlPoint);
						}
					}
					o_standardDAO.merge(standardControl);
					//保存要求关联部门
					String deptId = IcmStandardUtils.findIdbyJason(jsonObject.getString("cdeptId"), "id");
					if(StringUtils.isNotBlank(deptId)){
						o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standardControlId);
						StandardRelaOrg standardRelaOrg = new StandardRelaOrg();
						standardRelaOrg.setId(Identities.uuid2());
						standardRelaOrg.setStandard(standardControl);
						standardRelaOrg.setOrg(o_sysOrgnizationDAO.get(deptId));
						standardRelaOrg.setType(Contents.ORG_RESPONSIBILITY);
						o_standardRelaOrgDAO.merge(standardRelaOrg);
					}
					//保存要求相关流程
					if(StringUtils.isNotBlank(jsonObject.getString("cprocessId"))){
						o_standardRelaProcessureBO.delStandardRelaProcessureByStandardId(standardControlId);
						StandardRelaProcessure standardRelaProcessure = new StandardRelaProcessure();
						standardRelaProcessure.setId(Identities.uuid2());
						standardRelaProcessure.setStandard(standardControl);
						standardRelaProcessure.setProcessure(o_processDAO.get(jsonObject.getString("cprocessId")));
						o_standardRelaProcessureBO.saveStandardRelaProcessure(standardRelaProcessure);
					}
				}
			}
		}
		return standard;
	}
	/**
	 * 根据传过来的id查询内控标准，id为空则新建
	 * @author 元杰
	 * @return map 集合
	 */
	public Map<String, Object> findStandardJsonById(String standardId, String executionId, String step) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> formMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(standardId)){
			formMap.put("time", DateUtils.formatShortDate(new Date()));
			formMap.put("deptName", UserContext.getUser().getMajorDeptName());
			formMap.put("deptId", UserContext.getUser().getMajorDeptId());
			formMap.put("updateDeadline", DateUtils.formatShortDate(new Date()));
			formMap.put("code", this.findStandardCode());
		}else{
			Standard standard = o_standardDAO.get(standardId);
			formMap.put("controlRequirement", standard.getControlRequirement());
			if(null != standard.getControlLevel()){
				formMap.put("controlLevel", standard.getControlLevel().getId());
				formMap.put("controlLevelName", standard.getControlLevel().getName());
			}
			if(standard.getStatus()!=null){
				formMap.put("status", standard.getStatus());
			}
			if(null != standard.getCreateBy()){//标准的提交部门是指CreateBy这个人所对应的公司
				String deptId="";
				String deptName="";
				Iterator<SysEmpOrg> iterator = standard.getCreateBy().getSysEmpOrgs().iterator();
				if(iterator.hasNext()){
					SysOrganization org = iterator.next().getSysOrganization();
					deptId = org.getId();
					deptName = org.getOrgname();
				}
				formMap.put("deptId",deptId);
				formMap.put("deptName",deptName);
			}
			formMap.put("idSeq", standard.getIdSeq());
			formMap.put("code", standard.getCode());
			formMap.put("feedback", standard.getFeedback());
			if(null != standard.getParent()){
				formMap.put("parent", standard.getParent().getId());
				formMap.put("parentStandardName", standard.getParent().getName());
			}
			if(null != standard.getCreateTime()){
				formMap.put("time", DateUtils.formatShortDate(standard.getCreateTime()));
			}else{
				formMap.put("time", "");
			}
			formMap.put("company", standard.getCompany().getId());
			formMap.put("companyName", standard.getCompany().getOrgname());;
			formMap.put("name", standard.getName());
			formMap.put("id", standard.getId());
			formMap.put("dealStatus", standard.getDealStatus());
			if(null != standard.getUpdateDeadline()){
				formMap.put("updateDeadline", DateUtils.formatShortDate(standard.getUpdateDeadline()));
			}else{
				formMap.put("updateDeadline", "");
			}
			String[] standardControlIds = null; //内控标准对应的要求ID集合
			if("3".equals(step) || "4".equals(step) || "5".equals(step)){//这几步中，要求是针对部门分类并显示的
				StandardBpmObject standardBpmObject = o_standardBpmBO.findStandardBpmObjectByExecutionId(executionId);
				standardControlIds = standardBpmObject.getStandardControlIds();
			}else {
				List<Standard> standardControl = this.findStandardControlBySome(standardId, UserContext.getUser().getCompanyid());
				standardControlIds = new String[standardControl.size()];
				int i = 0;
				for(Standard sc : standardControl){
					standardControlIds[i] =  sc.getId();
					++i;
				}
			}
			formMap.put("standardControlIds", standardControlIds);//返回内控要求的ID集合
		}
		result.put("data", formMap);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 根据id查询内控要求
	 * @author 元杰
	 * @return map 集合
	 */
	public Map<String, Object> findstandardControlById(String standardControlId) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> formMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(standardControlId)){
			Standard standard = o_standardDAO.get(standardControlId);
			if(StringUtils.isNotBlank(standard.getSubCompanyids())){//如果要求的CompanyId不是父级标准的Company，则选中适用下级单位的Radio为“是”
				formMap.put("inferior", "0yn_y");
			}
			formMap.put("ccontrolRequirement", standard.getControlRequirement());
			formMap.put("cstandardControlAdvice", standard.getFeedback());
			if(standard.getStatus()!=null){
				formMap.put("cstatus", standard.getStatus());
			}
			if(standard.getDealStatus()!=null){//处理状态DealStatus
				formMap.put("statusId", standard.getDealStatus());
			}
			if(null!=standard.getStandardRelaOrg()&&standard.getStandardRelaOrg().size()>0){
				String orgId="";
				String orgCode="";
				String orgName="";
				for(StandardRelaOrg standardRelaOrg:standard.getStandardRelaOrg()){
					SysOrganization org = standardRelaOrg.getOrg();
					orgId=org.getId();
					orgCode=org.getOrgcode();
					orgName=org.getOrgname();
				}
				JSONArray arr = new JSONArray();
				JSONObject obj = new JSONObject();
				obj.put("id", orgId);
				obj.put("deptno", orgCode);
				obj.put("deptname", orgName);
				arr.add(obj);
				formMap.put("cdeptId",arr.toString());
				formMap.put("cdeptName",orgName);
			}
			if(null != standard.getStandardRelaProcessure() && standard.getStandardRelaProcessure() .size()>0){
				String processureName = "";
				String processureId = "";
				for(StandardRelaProcessure standardRelaProcessure : standard.getStandardRelaProcessure()){
					processureName = standardRelaProcessure.getProcessure().getName();
					processureId = standardRelaProcessure.getProcessure().getId();
				}
				formMap.put("cprocessName", processureName);
				formMap.put("cprocessId", processureId);
			}
			if(null != standard.getCompany()){
				formMap.put("csubCompanyName", standard.getCompany().getOrgname());
			}
			String subCompanyIdStr = standard.getSubCompanyids();
			if(StringUtils.isNotBlank(subCompanyIdStr)){
				String[] subCompanyIds = subCompanyIdStr.split(",");
				StringBuilder subCompanyNameStrBuilder = new StringBuilder();
				JSONArray arr = new JSONArray();
				for(String subCompanyId : subCompanyIds){
					JSONObject obj = new JSONObject();
					SysOrganization org = o_sysOrgnizationDAO.get(subCompanyId);
					obj.put("id", org.getId());
					obj.put("deptno", org.getOrgcode());
					obj.put("deptname", org.getOrgname());
					arr.add(obj);
					subCompanyNameStrBuilder.append(org.getOrgname() + ".");
				}
				
				formMap.put("csubCompanyName", subCompanyNameStrBuilder.toString());
				formMap.put("csubCompanyId", arr.toString());
			}
			if(standard.getParent() != null){
				formMap.put("cparent", standard.getParent().getName());
			}else{
				formMap.put("cparent", "");
			}
			if(standard.getControlLevel() != null){
				formMap.put("ccontrolLevel", standard.getControlLevel().getName());
			}else{
				formMap.put("ccontrolLevel", "");
			}
			if(standard.getControlLevel() != null){
				formMap.put("ccontrolLevelId", standard.getControlLevel().getId());
			}else{
				formMap.put("ccontrolLevelId", "");
			}
			if(standard.getDealStatus() != null){
				formMap.put("cdealStatus", standard.getDealStatus());
			}else{
				formMap.put("cdealStatus", "");
			}
			if(StringUtils.isNotBlank(standard.getControlPoint())){
				formMap.put("cstandardControlPointHidden", standard.getControlPoint());
				String[] standardPoints = standard.getControlPoint().split(",");
				String standardPointStr = o_dictEntryDAO.get(standardPoints[0]).getName();
				formMap.put("cstandardControlPoint", standardPointStr);
			}else{
				formMap.put("cstandardControlPointHidden", "");
				formMap.put("cstandardControlPoint", "");
			}
			formMap.put("cidSeq", standard.getIdSeq());
			formMap.put("ccode", standard.getCode());
			formMap.put("cname", standard.getName());
			formMap.put("cid", standard.getId());
		}
		result.put("data", formMap);
		result.put("success", true);
		return result;
	}
	
	/**
	 * 保存内控标准集合List
	 * @author 元杰
	 * @param standardList 标准集合
	 */
	@Transactional
	public void mergeStandards(List<Standard> standardList) {
		if(null != standardList && standardList.size() > 0){
			for(Standard standard : standardList){
				o_standardDAO.merge(standard);
			}
		}
	}
	/**
	 * 根据内控标识获取所有下级内控要求
	 * 
	 * @author 元杰
	 * @param parentId内控父级ID
	 * @param companyId公司ID
	 */
	public List<Standard> findStandardControlBySome(String parentId, String companyId){
		Criteria criteria = this.o_standardDAO.createCriteria();
		if (StringUtils.isNotBlank(parentId)) {
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		return criteria.list();
	}
	
	/**
	 * <pre>
	 * 根据内控标准id取得对应角色人
	 * </pre>
	 * 
	 * @author 元杰
	 * @param roleKey
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	public String[] findStandardEmpIdsByRole(String roleKey){
		String[] empIds = null;
		String roleName = ResourceBundle.getBundle("application").getString(roleKey);
		List<SysEmployee> employeeList = o_sysRoleBO.getEmpByCorpAndRole(roleName);
		if(null != employeeList && employeeList.size()>0){
			Integer length=employeeList.size();
			empIds=new String[employeeList.size()];
			for(int i=0;i<length;i++){
				if(null != employeeList.get(i)){
					empIds[i]=employeeList.get(i).getId();
				}
			}
		}
		return empIds;
	}
	
	/**
	 * <pre>
	 * 为下级机构构造同样的标准和要求
	 * </pre>
	 * @param subCompanyIdSet 适用于下级机构的机构ID集合
	 * @param standard 最父级的标准
	 * @return standardList 下级公司内控标准集合
	 * @author 元杰
	 * @since  fhd　Ver 1.1
	*/
	@Transactional
	public List<String> saveSubCompanyStandards(Set<String> subCompanyIdSet, Standard standard, List<Standard> standardControlList){
		List<String> standardIdList = new ArrayList<String>();//所有下级公司的要求ID集合，相同要求每个公司只有一个
		for(String subCompanyId : subCompanyIdSet){
			Standard subCompanyStandard = new Standard();//为下级公司添加相同的标准
			BeanUtils.copyProperties(standard, subCompanyStandard);
			String standardId = Identities.uuid2();
			subCompanyStandard.setId(standardId);
			subCompanyStandard.setCompany(o_sysOrgnizationDAO.get(subCompanyId));
			subCompanyStandard.setIdSeq("." + standardId + ".");
			o_standardDAO.merge(subCompanyStandard);
			standardIdList.add(standardId);
			
			for(Standard standardControl : standardControlList){//复制同样的下挂要求
				if(standardControl.getCompany().getId().equals(subCompanyId)){
					Standard subCompanyStandardControl = new Standard();
					BeanUtils.copyProperties(standardControl, subCompanyStandardControl);
					String subCompanystandardControlId = Identities.uuid2();
					subCompanyStandardControl.setId(subCompanystandardControlId);
					subCompanyStandardControl.setParent(subCompanyStandard);
					subCompanyStandardControl.setIdSeq(subCompanyStandard.getIdSeq() + subCompanystandardControlId + ".");
					o_standardDAO.merge(subCompanyStandardControl);
				}
			}
		}
		return standardIdList;
	}
	/**
	 * <pre>
	 *自动生成内控标准编号
	 * </pre>
	 * 
	 * @author 元杰
	 * @return 返回根据当前时间生成Code
	 * @since  fhd　Ver 1.1
	*/
	public String findStandardCode(){
		String standardCode = DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS");
		Criteria criteria = o_standardDAO.createCriteria();
		List<Standard> standardList = criteria.list();
		for(Standard standard : standardList){
			if(standardCode.equals(standard.getCode())){
				findStandardCode();
			}
		}
		return standardCode;
	}
	
	/**
	 * 根据标准删除标准和其下挂要求
	 * @author 元杰
	 * @param standard 内控标准
	 */
	@Transactional
	public void delStandardAndSub(Standard standard) {
		if(null != standard){
			Set<Standard> subStandards = standard.getChildren();
			for(Standard subStandard : subStandards ){
				o_standardDAO.delete(subStandard);
			}
			o_standardDAO.delete(standard);
		}
	}
	
	/**
	 * 根据ID集合删除标准
	 * @author 元杰
	 * @param standardIds 内控标准ID集合
	 */
	@Transactional
	public void deleteStandardByIds(String standardIds) {
		if(StringUtils.isNotBlank(standardIds)){
			String[] ids = standardIds.split(",");
			if(ids.length > 0){
				Criteria criteria = o_standardDAO.createCriteria();
				criteria.add(Restrictions.in("id", ids));
				List<Standard> standardList = criteria.list();
				for(Standard standard : standardList){
					o_standardRelaProcessureBO.delStandardRelaProcessureByStandardId(standard.getId());
					o_standardRelaOrgBO.delStandardRelaOrgByStandardId(standard.getId());
					o_standardDAO.delete(standard);
				}
			}
		}
	}
	
	/**
	 * 保存内控标准(要求)
	 * @author 吴德福
	 * @param standard
	 */
	@Transactional
	public void mergeStandard(Standard standard){
		o_standardDAO.merge(standard);
	}
	/**
	 * 根据公司id查询所有的流程集合.
	 * @author 吴德福
	 * @param companyId
	 * @return List<Standard>
	 */
	public List<Standard> findControlStandardListByCompanyId(String companyId){
		Criteria criteria = o_standardDAO.createCriteria();
		if (StringUtils.isNotBlank(companyId)) {
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		criteria.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));
		return criteria.list();
	}
}
