package com.fhd.sm.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.dao.base.SqlBuilder;
import com.fhd.dao.comm.CategoryDAO;
import com.fhd.dao.kpi.KpiDAO;
import com.fhd.dao.kpi.StrategyMapDAO;
import com.fhd.fdc.utils.Contents;

/**
 * 监控预警初始化BO
 *
 */
@Service
public class SmInitBO {
	
	@Autowired
	private KpiDAO o_kpiDAO;
	
	@Autowired
	private StrategyMapDAO o_strategyMapDAO;
	
	@Autowired
	private CategoryDAO o_categoryDAO;
	
	
	/**删除公司下指标及指标相关联数据
	 * @param companyId 公司ID
	 */
	@Transactional
	public void removeKpiRelaData(final String companyId){
		o_kpiDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String type = Contents.KPI_TYPE;
				
				Map<String,String> model = new HashMap<String, String>();
				
				//删除指标关联部门人员
				
				String deleteKpiRelaOrgByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaOrgByCompanyId", model);
				PreparedStatement deleteKpiRelaOrgByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaOrgByCompanyIdSql);
				deleteKpiRelaOrgByCompanyIdSqlPst.setString(1, companyId);
				deleteKpiRelaOrgByCompanyIdSqlPst.setString(2, type);
				deleteKpiRelaOrgByCompanyIdSqlPst.addBatch();
				
				//删除指标关联的记分卡
				String deleteKpiRelaCategoryByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaCategoryByCompanyId", model);
				PreparedStatement deleteKpiRelaCategoryByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaCategoryByCompanyIdSql);
				deleteKpiRelaCategoryByCompanyIdSqlPst.setString(1, companyId);
				deleteKpiRelaCategoryByCompanyIdSqlPst.setString(2, type);
				deleteKpiRelaCategoryByCompanyIdSqlPst.addBatch();
				
				//删除指标关联的目标
				String deleteKpiRelaStrategyByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaStrategyByCompanyId", model);
				PreparedStatement deleteKpiRelaStrategyByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaStrategyByCompanyIdSql);
				deleteKpiRelaStrategyByCompanyIdSqlPst.setString(1, companyId);
				deleteKpiRelaStrategyByCompanyIdSqlPst.setString(2, type);
				deleteKpiRelaStrategyByCompanyIdSqlPst.addBatch();
				
				//删除指标关联的风险
				String deleteKpiRelaRiskByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaRiskByCompanyId", model);
				PreparedStatement deleteKpiRelaRiskByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaRiskByCompanyIdSql);
				deleteKpiRelaRiskByCompanyIdSqlPst.setString(1, companyId);
				deleteKpiRelaRiskByCompanyIdSqlPst.setString(2, type);
				deleteKpiRelaRiskByCompanyIdSqlPst.addBatch();
				
				//删除指标关联的告警
				String deleteKpiRelaAlarmByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaAlarmByCompanyId", model);
				PreparedStatement deleteKpiRelaAlarmByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaAlarmByCompanyIdSql);
				deleteKpiRelaAlarmByCompanyIdSqlPst.setString(1, companyId);
				deleteKpiRelaAlarmByCompanyIdSqlPst.setString(2, type);
				deleteKpiRelaAlarmByCompanyIdSqlPst.addBatch();
				
				//删除指标对象
		    	String deleteKpiByCompanyIdSql = SqlBuilder.getSql("deleteKpiByCompanyId", model);
		    	PreparedStatement deleteKpiByCompanyIdPst = connection.prepareStatement(deleteKpiByCompanyIdSql);
		    	deleteKpiByCompanyIdPst.setString(1, companyId);
		    	deleteKpiByCompanyIdPst.setString(2, type);
		    	deleteKpiByCompanyIdPst.addBatch();
		    	
		    	//删除指标采集数据关联的备注
		    	String deleteKpiRelaMemoByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaMemoByCompanyId", model);
		    	PreparedStatement deleteKpiRelaMemoByCompanyIdPst = connection.prepareStatement(deleteKpiRelaMemoByCompanyIdSql);
		    	deleteKpiRelaMemoByCompanyIdPst.setString(1, companyId);
		    	deleteKpiRelaMemoByCompanyIdPst.setString(2, type);
		    	deleteKpiRelaMemoByCompanyIdPst.addBatch();
		    	
		    	
		    	
		    	//删除指标关联的采集结果
		    	String deleteKpiRelaGatherResultByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaGatherResultByCompanyId", model);
		    	PreparedStatement deleteKpiRelaGatherResultByCompanyIdPst = connection.prepareStatement(deleteKpiRelaGatherResultByCompanyIdSql);
		    	deleteKpiRelaGatherResultByCompanyIdPst.setString(1, companyId);
		    	deleteKpiRelaGatherResultByCompanyIdPst.setString(2, type);
		    	deleteKpiRelaGatherResultByCompanyIdPst.addBatch();
		    	//关联纬度
		    	String deleteKpiRelaDimByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaDimByCompanyId", model);
		    	PreparedStatement deleteKpiRelaDimByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaDimByCompanyIdSql);
		    	deleteKpiRelaDimByCompanyIdSqlPst.setString(1, companyId);
		    	deleteKpiRelaDimByCompanyIdSqlPst.setString(2, type);
		    	deleteKpiRelaDimByCompanyIdSqlPst.addBatch();
		    	
		    	
		    	
		    	
		    	deleteKpiRelaOrgByCompanyIdSqlPst.executeBatch();
		    	deleteKpiRelaCategoryByCompanyIdSqlPst.executeBatch();
		    	deleteKpiRelaStrategyByCompanyIdSqlPst.executeBatch();
		    	deleteKpiRelaRiskByCompanyIdSqlPst.executeBatch();
		    	deleteKpiRelaAlarmByCompanyIdSqlPst.executeBatch();
		    	deleteKpiRelaMemoByCompanyIdPst.executeBatch();
		    	deleteKpiRelaGatherResultByCompanyIdPst.executeBatch();
		    	deleteKpiRelaDimByCompanyIdSqlPst.executeBatch();
		    	
		    	deleteKpiByCompanyIdPst.executeBatch();
		    	
		    	
		    	
				connection.commit();
                connection.setAutoCommit(true);
				
			}
		});
		
	}
	/**删除公司下指标类型及指标类型相关联数据
	 * @param companyId 公司ID
	 */
	@Transactional
	public void removeKcRelaData(final String companyId){
		o_kpiDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				String type = Contents.KC_TYPE;
				
				Map<String,String> model = new HashMap<String, String>();
				
				//删除指标类型关联部门人员
				
				String deleteKpiRelaOrgByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaOrgByCompanyId", model);
				PreparedStatement deleteKpiRelaOrgByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaOrgByCompanyIdSql);
				deleteKpiRelaOrgByCompanyIdSqlPst.setString(1, companyId);
				deleteKpiRelaOrgByCompanyIdSqlPst.setString(2, type);
				deleteKpiRelaOrgByCompanyIdSqlPst.addBatch();
				
				
				
				//删除指标类型关联的告警
				String deleteKpiRelaAlarmByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaAlarmByCompanyId", model);
				PreparedStatement deleteKpiRelaAlarmByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaAlarmByCompanyIdSql);
				deleteKpiRelaAlarmByCompanyIdSqlPst.setString(1, companyId);
				deleteKpiRelaAlarmByCompanyIdSqlPst.setString(2, type);
				deleteKpiRelaAlarmByCompanyIdSqlPst.addBatch();
				
				//删除指标类型对象
				String deleteKpiByCompanyIdSql = SqlBuilder.getSql("deleteKpiByCompanyId", model);
				PreparedStatement deleteKpiByCompanyIdPst = connection.prepareStatement(deleteKpiByCompanyIdSql);
				deleteKpiByCompanyIdPst.setString(1, companyId);
				deleteKpiByCompanyIdPst.setString(2, type);
				deleteKpiByCompanyIdPst.addBatch();
				
				
				
				
				//更新指标的所属指标类型列为null
				String updateKpiBelongKcByCompanyIdSql = SqlBuilder.getSql("updateKpiBelongKcByCompanyId", model);
				PreparedStatement updateKpiBelongKcByCompanyIdPst = connection.prepareStatement(updateKpiBelongKcByCompanyIdSql);
				updateKpiBelongKcByCompanyIdPst.setString(1, companyId);
				updateKpiBelongKcByCompanyIdPst.addBatch();
				
				//关联纬度
                String deleteKpiRelaDimByCompanyIdSql = SqlBuilder.getSql("deleteKpiRelaDimByCompanyId", model);
                PreparedStatement deleteKpiRelaDimByCompanyIdSqlPst = connection.prepareStatement(deleteKpiRelaDimByCompanyIdSql);
                deleteKpiRelaDimByCompanyIdSqlPst.setString(1, companyId);
                deleteKpiRelaDimByCompanyIdSqlPst.setString(2, type);
                deleteKpiRelaDimByCompanyIdSqlPst.addBatch();
				
				
				deleteKpiRelaOrgByCompanyIdSqlPst.executeBatch();
				deleteKpiRelaAlarmByCompanyIdSqlPst.executeBatch();
				updateKpiBelongKcByCompanyIdPst.executeBatch();
				deleteKpiRelaDimByCompanyIdSqlPst.executeBatch();
				
				deleteKpiByCompanyIdPst.executeBatch();
				
				
				
				connection.commit();
				connection.setAutoCommit(true);
				
			}
		});
		
	}
	
	/**删除公司下目标及目标相关联数据
	 * @param companyId 公司ID
	 */
	@Transactional
	public void removeStrategyRelaData(final String companyId){
		o_strategyMapDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				Map<String,String> model = new HashMap<String, String>();
				//目标
				String deleteSmByCompanyIdSql = SqlBuilder.getSql("deleteSmByCompanyId", model);
				PreparedStatement deleteSmByCompanyIdPst = connection.prepareStatement(deleteSmByCompanyIdSql);
				deleteSmByCompanyIdPst.setString(1, companyId);
				deleteSmByCompanyIdPst.addBatch();
				//告警
				String deleteSmRelaAlarmByCompanyIdSql = SqlBuilder.getSql("deleteSmRelaAlarmByCompanyId", model);
				PreparedStatement deleteSmRelaAlarmByCompanyIdPst = connection.prepareStatement(deleteSmRelaAlarmByCompanyIdSql);
				deleteSmRelaAlarmByCompanyIdPst.setString(1, companyId);
				deleteSmRelaAlarmByCompanyIdPst.addBatch();
				//机构
				String deleteSmRelaOrgByCompanyIdSql = SqlBuilder.getSql("deleteSmRelaOrgByCompanyId", model);
				PreparedStatement deleteSmRelaOrgByCompanyIdPst = connection.prepareStatement(deleteSmRelaOrgByCompanyIdSql);
				deleteSmRelaOrgByCompanyIdPst.setString(1, companyId);
				deleteSmRelaOrgByCompanyIdPst.addBatch();
				//主题
				String deleteSmRelaThemeByCompanyIdSql = SqlBuilder.getSql("deleteSmRelaThemeByCompanyId", model);
				PreparedStatement deleteSmRelaThemeByCompanyIdPst = connection.prepareStatement(deleteSmRelaThemeByCompanyIdSql);
				deleteSmRelaThemeByCompanyIdPst.setString(1, companyId);
				deleteSmRelaThemeByCompanyIdPst.addBatch();
				//维度
				String deleteSmRelaDimByCompanyIdSql = SqlBuilder.getSql("deleteSmRelaDimByCompanyId", model);
				PreparedStatement deleteSmRelaDimByCompanyIdPst = connection.prepareStatement(deleteSmRelaDimByCompanyIdSql);
				deleteSmRelaDimByCompanyIdPst.setString(1, companyId);
				deleteSmRelaDimByCompanyIdPst.addBatch();
				//指标
				String deleteSmRelaKpiByCompanyIdSql = SqlBuilder.getSql("deleteSmRelaKpiByCompanyId", model);
				PreparedStatement deleteSmRelaKpiByCompanyIdPst = connection.prepareStatement(deleteSmRelaKpiByCompanyIdSql);
				deleteSmRelaKpiByCompanyIdPst.setString(1, companyId);
				deleteSmRelaKpiByCompanyIdPst.addBatch();
				//采集结果
				String deleteSmRelaResultByCompanyIdSql = SqlBuilder.getSql("deleteSmRelaResultByCompanyId", model);
				PreparedStatement deleteSmRelaResultByCompanyIdPst = connection.prepareStatement(deleteSmRelaResultByCompanyIdSql);
				deleteSmRelaResultByCompanyIdPst.setString(1, companyId);
				deleteSmRelaResultByCompanyIdPst.addBatch();
				
				deleteSmRelaAlarmByCompanyIdPst.executeBatch();
				deleteSmRelaOrgByCompanyIdPst.executeBatch();
				deleteSmRelaThemeByCompanyIdPst.executeBatch();
				deleteSmRelaDimByCompanyIdPst.executeBatch();
				deleteSmRelaKpiByCompanyIdPst.executeBatch();
				deleteSmRelaResultByCompanyIdPst.executeBatch();
				deleteSmByCompanyIdPst.executeBatch();
				
				
				connection.commit();
                connection.setAutoCommit(true);
				
			}
		});
	}
	/**删除公司下记分卡及记分卡相关联数据
	 * @param companyId 公司ID
	 */
	@Transactional
	public void removeCategoryRelaData(final String companyId){
		o_categoryDAO.getSession().doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				Map<String,String> model = new HashMap<String, String>();
				//记分卡
				String deleteScByCompanyIdSql = SqlBuilder.getSql("deleteScByCompanyId", model);
				PreparedStatement deleteScByCompanyIdPst = connection.prepareStatement(deleteScByCompanyIdSql);
				deleteScByCompanyIdPst.setString(1, companyId);
				deleteScByCompanyIdPst.addBatch();
				//告警
				String deleteSmRelaAlarmByCompanyIdSql = SqlBuilder.getSql("deleteScRelaAlarmByCompanyId", model);
				PreparedStatement deleteSmRelaAlarmByCompanyIdPst = connection.prepareStatement(deleteSmRelaAlarmByCompanyIdSql);
				deleteSmRelaAlarmByCompanyIdPst.setString(1, companyId);
				deleteSmRelaAlarmByCompanyIdPst.addBatch();
				//机构
				String deleteSmRelaOrgByCompanyIdSql = SqlBuilder.getSql("deleteScRelaOrgByCompanyId", model);
				PreparedStatement deleteSmRelaOrgByCompanyIdPst = connection.prepareStatement(deleteSmRelaOrgByCompanyIdSql);
				deleteSmRelaOrgByCompanyIdPst.setString(1, companyId);
				deleteSmRelaOrgByCompanyIdPst.addBatch();
				//指标
				String deleteSmRelaKpiByCompanyIdSql = SqlBuilder.getSql("deleteScRelaKpiByCompanyId", model);
				PreparedStatement deleteSmRelaKpiByCompanyIdPst = connection.prepareStatement(deleteSmRelaKpiByCompanyIdSql);
				deleteSmRelaKpiByCompanyIdPst.setString(1, companyId);
				deleteSmRelaKpiByCompanyIdPst.addBatch();
				//采集结果
				String deleteSmRelaResultByCompanyIdSql = SqlBuilder.getSql("deleteScRelaResultByCompanyId", model);
				PreparedStatement deleteSmRelaResultByCompanyIdPst = connection.prepareStatement(deleteSmRelaResultByCompanyIdSql);
				deleteSmRelaResultByCompanyIdPst.setString(1, companyId);
				deleteSmRelaResultByCompanyIdPst.addBatch();
				
				deleteSmRelaAlarmByCompanyIdPst.executeBatch();
				deleteSmRelaOrgByCompanyIdPst.executeBatch();
				deleteSmRelaKpiByCompanyIdPst.executeBatch();
				deleteSmRelaResultByCompanyIdPst.executeBatch();
				deleteScByCompanyIdPst.executeBatch();
				
				
				connection.commit();
				connection.setAutoCommit(true);
				
			}
		});
	}

}
