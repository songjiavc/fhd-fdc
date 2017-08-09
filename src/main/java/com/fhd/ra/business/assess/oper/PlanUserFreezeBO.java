package com.fhd.ra.business.assess.oper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.assess.quaAssess.PlanUserFreezeDAO;
import com.fhd.entity.assess.quaAssess.PlanUserFreeze;
import com.fhd.entity.sys.auth.SysUser;
import com.fhd.entity.sys.st.Plan;

@Service
public class PlanUserFreezeBO {
	
@Autowired
private PlanUserFreezeDAO o_planUserFreezeDao;	
	

/**
 * 保存计划需要冻结的用户
 * @param panlID 计划ID
 * @return Map<String,String> 流程中角色：用戶id
 * @author 郭鹏
 * 20170615
 * */
@Transactional
public void savePlanUserFreeze(String planID,Map<String,String> userRole){
		if (userRole != null) {
			for (Map.Entry<String, String> entry : userRole.entrySet()) {
				PlanUserFreeze planUserFreeze = new PlanUserFreeze();
				planUserFreeze.setId(UUID.randomUUID().toString());
				Plan plan = new Plan();
				plan.setId(planID);
				planUserFreeze.setPlanId(plan);
				planUserFreeze.setRoleName(entry.getKey());
				SysUser user = new SysUser();
				user.setId(entry.getValue());
				planUserFreeze.setUserId(user);
				o_planUserFreezeDao.merge(planUserFreeze);
			}
		}
	}

}
