package com.fhd.ra.business.assess.oper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.formulatePlan.RiskCircuseeDAO;
import com.fhd.entity.assess.formulatePlan.RiskCircusee;

@Service
public class CircuseeBO {

	@Autowired
	private RiskCircuseeDAO o_riskcircuseeDAO;
	
	/**
	 * 根据计划id和部门id查RiskCircusee
	 * @param planId	计划id
	 * @param deptId	部门id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskCircusee> findRiskCircuseeByplanIdAnddeptId(String planId, String deptId) {
		Criteria c = o_riskcircuseeDAO.createCriteria();
		List<RiskCircusee> list = null;
		if (StringUtils.isNotBlank(planId)&& StringUtils.isNotBlank(deptId)) {
			c.add(Restrictions.and(Restrictions.eq("assessPlan.id", planId), Restrictions.eq("organization.id", deptId)));
		} else {
			return null;
		}
		list = c.list();
		return list;
	}
	
	/**
	 * 根据计划id查承办人审批人表
	 * @param planId	计划id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskCircusee> findRiskCircuseeByplanId(String planId) {
		Criteria c = o_riskcircuseeDAO.createCriteria();
		List<RiskCircusee> list = null;
		if (StringUtils.isNotBlank(planId)) {
			c.add(Restrictions.eq("assessPlan.id", planId));
		} else {
			return null;
		}
		list = c.list();
		return list;
	}
	
	/**
	 * 根据计划id集合查审批人承办人表
	 * @param planIds	计划id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RiskCircusee> findRiskCircuseesByplanIdList(List<String> planIds) {
		Criteria c = o_riskcircuseeDAO.createCriteria();
		List<RiskCircusee> list = null;
		c.add(Restrictions.in("assessPlan.id", planIds));
		list = c.list();
		return list;
	}
	/**
	 * 根据计划id删除审批人承办人列表
	 * add by 王再冉
	 * 2014-3-3  下午4:10:06
	 * desc : 
	 * @param planId 
	 * void
	 */
	@Transactional
	public void removeRiskCircuseesByPlanId(String planId) {
		if(StringUtils.isNotBlank(planId)){
			o_riskcircuseeDAO.createQuery("delete RiskCircusee where ASSESS_PLAN_ID = (:planId)")
			.setParameter("planId", planId).executeUpdate();
		}
	}
	/**
	 * 批量保存审批人承办人表
	 * add by 王再冉
	 * 2014-3-3  下午4:20:07
	 * desc : 
	 * @param objList 
	 * void
	 */
	@Transactional
    public void saveRiskCircuseesSql(final List<RiskCircusee> objList) {
        this.o_riskcircuseeDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "insert into T_RM_RISK_CIRCUSEE (id,assess_plan_id,org_id,UNDERTAKER,approver) values(?,?,?,?,?)";
                pst = connection.prepareStatement(sql);
                
                for (RiskCircusee obj : objList) {
                	pst.setString(1, obj.getId());
                	pst.setString(2, obj.getAssessPlan().getId());
                    pst.setString(3, obj.getOrganization().getId());
                    pst.setString(4, obj.getUnderTaker().getId());
                    pst.setString(5, obj.getApprover().getId());
                    pst.addBatch();
				}
                	
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
}
