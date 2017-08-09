package com.fhd.ra.business.response.major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.response.major.RiskResponseCounterDAO;
import com.fhd.entity.response.major.RiskResponseCounter;
import com.fhd.entity.response.major.RiskResponseItem;
import com.fhd.entity.response.major.RiskResponseItemCounterRela;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.ra.web.controller.response.major.utils.DateUtil;

@Service
public class RiskResponseCounterService {

	private static final Logger log = LoggerFactory.getLogger(RiskResponseCounterService.class);
	@Autowired
	private RiskResponseCounterDAO RiskResponseCounterDAO;
	@Autowired
	private RiskResponseItemCounterRelaService riskResponseItemCounterRelaService;
	@Transactional
	public void save(RiskResponseCounter entity) throws Exception{
		this.RiskResponseCounterDAO.merge(entity);
	}
	@Transactional
	public void saveBatch(final List<RiskResponseCounter> list) throws Exception{
		 this.RiskResponseCounterDAO.getSession().doWork(new Work() {
	            public void execute(Connection connection) throws SQLException {
	                PreparedStatement pst = null;
	                String sql = "insert into t_rm_response_counter(id,description,target,complete_sign,start_time,finish_time,type,create_user,create_org) values(?,?,?,?,?,?,?,?,?)";
	                pst = connection.prepareStatement(sql);
	                for (RiskResponseCounter obj : list) {
		               	  pst.setString(1, obj.getId());
	                      pst.setString(2, obj.getDescription());
	                      pst.setString(3, obj.getTarget());
	                      pst.setString(4, obj.getCompleteSign()); 
	                      pst.setString(5, DateUtil.format(obj.getStartTime(), "yyyy-mm-dd"));
	                      pst.setString(6, DateUtil.format(obj.getFinishTime(), "yyyy-mm-dd"));
	                      pst.setString(7, obj.getType());
	                      pst.setString(8, obj.getCreateUser().getId());
	                      pst.setString(9, obj.getCreateOrg().getId());
	                      pst.addBatch();
					}
	                pst.executeBatch();
	            }
	        });
	}
	@Transactional
	public void deleteBatchByList(final List<RiskResponseCounter> list) throws Exception{
		this.RiskResponseCounterDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = "delete from t_rm_response_counter where id = ? ";
                pst = connection.prepareStatement(sql);
                for (RiskResponseCounter entity: list) {
                	pst.setString(1,entity.getId());
                	pst.addBatch();
                }
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
	}
	@Transactional
	public void update(RiskResponseCounter entity) throws Exception{
		this.RiskResponseCounterDAO.merge(entity);
	}
	@Transactional
	public void delete(RiskResponseCounter entity) throws Exception{
		this.RiskResponseCounterDAO.delete(entity.getId());
	}
	
	public RiskResponseCounter findObj(RiskResponseCounter entity)throws Exception{
		return this.RiskResponseCounterDAO.get(entity.getId());
	}
	
	public Map<String,Object> saveCounter(RiskResponseCounter counter,String[] executionEmpIds,String itemId){
		Map<String,Object> map = new HashMap<String,Object>();
		//批量插入。。。
		List<RiskResponseItemCounterRela> itemCounterRelaList = new ArrayList<RiskResponseItemCounterRela>();
		for(String _executionEmpId : executionEmpIds){
			RiskResponseItemCounterRela obj = new RiskResponseItemCounterRela();
			obj.setId(Identities.uuid());
			obj.setCounter(counter);
			obj.setExecutionEmp(new SysEmployee(_executionEmpId));
			RiskResponseItem item =new RiskResponseItem();
			item.setId(itemId);
			obj.setItem(item);
			itemCounterRelaList.add(obj);
		}
		boolean flag = false;
		try {
			save(counter);
			riskResponseItemCounterRelaService.saveBatch(itemCounterRelaList);
			flag = true;
		} catch (Exception e) {
			
		}
		map.put("success", flag);
		map.put("data", true);
		return map;
	}
	
}
