package com.fhd.ra.business.assess.quaassess;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.assess.quaAssess.ScoreResultDAO;
import com.fhd.entity.assess.quaAssess.ScoreResult;


@Service
public class SubmitAssessBO {
	
	@Autowired
	private ScoreResultDAO o_scoreResultDAO;
	
	/**
	 * 通过综合ID查询打分结果
	 * @param rangObjectDeptEmpId 综合ID
	 * @return List<ScoreResult>
	 * @author 金鹏祥
	 * */
	@SuppressWarnings("unchecked")
	public List<ScoreResult> findScoreResultByrangObjectDeptEmpId(String rangObjectDeptEmpId[]) {
		Criteria criteria = o_scoreResultDAO.createCriteria();
		criteria.add(Restrictions.in("rangObjectDeptEmpId.id", rangObjectDeptEmpId));
		List<ScoreResult> list = null;
		list = criteria.list();
		
		return list;
	}
}