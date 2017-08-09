package com.fhd.ra.business.assess.quaassess;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.assess.quaAssess.EditIdeaDAO;
import com.fhd.dao.assess.quaAssess.ResponseIdeaDao;
import com.fhd.dao.assess.quaAssess.ScoreResultDAO;
import com.fhd.dao.risk.ScoreDAO;
import com.fhd.entity.assess.kpiSet.RangObjectDeptEmp;
import com.fhd.entity.assess.quaAssess.EditIdea;
import com.fhd.entity.assess.quaAssess.ResponseIdea;
import com.fhd.entity.assess.quaAssess.ScoreResult;
import com.fhd.entity.risk.Dimension;
import com.fhd.entity.risk.Score;
import com.fhd.ra.business.assess.oper.ScoreResultBO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class SaveAssessBO {

	@Autowired
	private ScoreResultDAO o_scoreResultDAO;
	
	@Autowired
	private ScoreDAO o_scoreDAO;
	
	@Autowired
	private EditIdeaDAO o_editIdeaDAO;
	
	@Autowired
	private ResponseIdeaDao o_responseIdeaDAO;
	@Autowired
	private ScoreResultBO o_scoreResultBO;
	
	/**
	 * 通过维度ID、维度分值查询维度关联分值ID
	 * @author 金鹏祥
	 * @return String
	 * */
	@SuppressWarnings("unchecked")
	public String findScoreBydimensionIdAndValue(String dimId, double value){
		Criteria criteria = o_scoreDAO.createCriteria();
		criteria.add(Restrictions.eq("dimension.id", dimId));
		criteria.add(Restrictions.eq("value", value));
		List<Score> list = null;
		list = criteria.list();
		
		return list.get(0).getId();
	}
	
	/**
	 * 通过维度ID、维度分值查询维度关联分值ID
	 * @author 金鹏祥
	 * @return String
	 * */
	@SuppressWarnings("unchecked")
	public List<ScoreResult> findScoreBydimensionIdAndDicIdAndRangObjectDeptEmpId(String scoreId, String rangObjectDeptEmpId){
		Criteria criteria = o_scoreResultDAO.createCriteria();
		criteria.add(Restrictions.eq("dimension.id", scoreId));
		criteria.add(Restrictions.eq("rangObjectDeptEmpId.id", rangObjectDeptEmpId));
		List<ScoreResult> list = null;
		list = criteria.list();
		
		return list;
	}
	
	/**
	 * 评估保存操作
	 * @param params 风险信息参数
	 * @return boolean
	 * @author 金鹏祥
	 * */
	@Transactional
	public boolean assessSaveOper(String params) {
		JSONArray jsonarr = JSONArray.fromObject(params); //风险信息参数数据
		try {
			for (Object objects : jsonarr) {
				JSONObject jsobjs = (JSONObject) objects;
				String dimId = jsobjs.get("dimId").toString(); //维度ID
				String rangObjectDeptEmpId = jsobjs.get("rangObjectDeptEmpId").toString(); //打分综合ID
				double dimValue = Double.parseDouble(jsobjs.get("dimValue").toString()); //打分值
				RangObjectDeptEmp rangObjectDeptEmp = new RangObjectDeptEmp();
				Dimension dimension = new Dimension();
				Score score = new Score();
				ScoreResult scoreResult = new ScoreResult();
				String scoreId = this.findScoreBydimensionIdAndValue(dimId, 
						dimValue);
				List<ScoreResult> scoreResultList = this.findScoreBydimensionIdAndDicIdAndRangObjectDeptEmpId(dimId, rangObjectDeptEmpId);
				
				if(scoreResultList.size() != 0){
					//修改打分结果
					scoreResult.setId(scoreResultList.get(0).getId());
				}else{
					//添加打分结果
					scoreResult.setId(Identities.uuid());
				}
				
				rangObjectDeptEmp.setId(rangObjectDeptEmpId);
				dimension.setId(dimId);
				score.setId(scoreId);
				
				scoreResult.setDimension(dimension);
				scoreResult.setScore(score);
				scoreResult.setRangObjectDeptEmpId(rangObjectDeptEmp);
				scoreResult.setSubmitTime(new Date());
				scoreResult.setApproval(false);
				scoreResult.setStatus(null);
				
				o_scoreResultBO.mergeScoreResult(scoreResult); //添加/修改打分结果
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 保存意见
	 * @param editIdea 意见实体
	 * @return VOID
	 * @author 金鹏祥
	 * */
	@Transactional
	public void mergeEditIdea(EditIdea editIdea){
		o_editIdeaDAO.merge(editIdea);
	}
	/**
	 * 保存应对意见
	 * @param responseIdea 应对意见实体
	 * @return VOID
	 * @author 郭鹏
	 * */
	@Transactional
	public void mergeEditIdea(ResponseIdea responseIdea){
		o_responseIdeaDAO.merge(responseIdea);
	}	
	
	
	/**
	 * 查询保存意见
	 * @return HashMap<String, EditIdea>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, EditIdea> findEditIdeaByObjectDeptEmpIdMapAll(){
		Criteria criteria = o_editIdeaDAO.createCriteria();
		List<EditIdea> list = null;
		list = criteria.list();
		HashMap<String, EditIdea> map = new HashMap<String, EditIdea>();
		for (EditIdea editIdea : list) {
			map.put(editIdea.getObjectDeptEmpId().getId(), editIdea);
		}
		
		return map;
	}
	
	/**
	 * 查询保存意见按评估人过滤
	 * @param scoreEmpId 人员ID
	 * @return HashMap<String, EditIdea>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public HashMap<String, EditIdea> findEditIdeaByObjectDeptEmpIdMapAll(String scoreEmpId){
		HashMap<String, EditIdea> map = new HashMap<String, EditIdea>();
		StringBuffer sql = new StringBuffer();
		EditIdea editIdea = null;
		RangObjectDeptEmp rangObjectDeptEmp = null;
		sql.append(" select a.* from T_RM_EDIT_IDEA a , t_rm_rang_object_dept_emp b " +
				" where a.object_dept_emp_id = b.id and b.SCORE_EMP_ID =:scoreEmpId");
		SQLQuery sqlQuery = o_editIdeaDAO.createSQLQuery(sql.toString());
		sqlQuery.setParameter("scoreEmpId", scoreEmpId);
		List<Object[]> list = sqlQuery.list();
        for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
        	Object[] objects = (Object[]) iterator.next();
        	
        	String id = "";
        	String rangObjectDeptEmpId = "";
        	String editIdeaContent = "";
        	editIdea = new EditIdea();
        	rangObjectDeptEmp = new RangObjectDeptEmp();
        	
        	if(null != objects[0]){
        		id = objects[0].toString();
            }if(null != objects[1]){
            	rangObjectDeptEmpId = objects[1].toString();
            }if(null != objects[2]){
            	editIdeaContent = objects[2].toString();
            }
            rangObjectDeptEmp.setId(rangObjectDeptEmpId);
            editIdea.setId(id);
            editIdea.setObjectDeptEmpId(rangObjectDeptEmp);
            editIdea.setEditIdeaContent(editIdeaContent);
            
            map.put(editIdea.getObjectDeptEmpId().getId(), editIdea);
        }
		
		return map;
	}
	
	/**
	 * 通过综合ID查询保存意见
	 * @param objectDeptEmpId 打分综合ID
	 * @return EditIdea
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public EditIdea findEditIdeaByObjectDeptEmpId(String objectDeptEmpId){
		Criteria criteria = o_editIdeaDAO.createCriteria();
		criteria.add(Restrictions.eq("objectDeptEmpId.id", objectDeptEmpId));
		List<EditIdea> list = null;
		list = criteria.list();
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	
	
	/**
	 * 通过综合ID查询保存意见
	 * @param objectDeptEmpId 打分综合ID
	 * @return ResponseIdea
	 * @author 郭鹏
	 * */
	@SuppressWarnings("unchecked")
	public ResponseIdea findResponseIdeaByObjectDeptEmpId(String objectDeptEmpId){
		Criteria criteria = o_responseIdeaDAO.createCriteria();
		criteria.add(Restrictions.eq("objectDeptEmpId.id", objectDeptEmpId));
		List<ResponseIdea> list = null;
		list = criteria.list();
		if(list.size() != 0){
			return list.get(0);
		}else{
			return null;
		}
		
	}
}