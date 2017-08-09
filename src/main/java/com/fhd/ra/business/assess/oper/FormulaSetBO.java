package com.fhd.ra.business.assess.oper;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.quaAssess.FormulaSetDAO;
import com.fhd.entity.sys.assess.FormulaSet;

@Service
public class FormulaSetBO {

	@Autowired
	private FormulaSetDAO o_formulaSetDAO;

	/**
	 * 修改实体
	 * */
	@Transactional
	public void mergeFormlaSet(FormulaSet formlaSet){
		o_formulaSetDAO.merge(formlaSet);
	}
	
	/**
	 * 查询公式计算配置实体
	 * @param companyId 公司ID
	 * @return FormulaSet
	 * @author 金鹏祥
	 */
	@SuppressWarnings("unchecked")
	public FormulaSet findFormulaSet(String companyId) {
		Criteria criteria = o_formulaSetDAO.createCriteria();
		criteria.add(Restrictions.eq("company.id", companyId));
		List<FormulaSet> list = criteria.list();
		if(null!=list&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
}
