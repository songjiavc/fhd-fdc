package com.fhd.sys.business.orgstructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.sys.organization.TmpSysPositionDAO;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.SysPosition;
import com.fhd.entity.sys.orgstructure.TmpSysPosition;
import com.fhd.fdc.utils.Contents;
import com.fhd.sys.business.organization.OrgGridBO;
import com.fhd.sys.business.organization.PositionBO;
/**
 * 岗位导入BO
 * @功能 : 
 * @author 王再冉
 * @date 2014-4-24
 * @since Ver
 * @copyRight FHD
 */
@Service
public class TmpImpPositionBO {
	
	@Autowired
	private PositionBO o_positionBO;
	@Autowired
	private OrgGridBO o_orgGridBO;
	@Autowired
	private TmpSysPositionDAO o_tmpSysPositionDAO;
	@Autowired
	private TmpImpOrganizationBO o_tmpImpOrganizationBO;
	
	/**
	 * 情空岗位临时表数据
	 * add by 王再冉
	 * 2014-4-24  下午3:40:18
	 * desc :  
	 * void
	 */
	public void deleteTmpSysPositionSQL(){
		String sql = " delete from TMP_IMP_SYS_POSITION ";
		SQLQuery sqlQuery = o_tmpSysPositionDAO.createSQLQuery(sql);
		sqlQuery.executeUpdate();
	}
	/**
	 * 查询全部临时表数据
	 * add by 王再冉
	 * 2014-4-24  下午4:16:20
	 * desc : 
	 * @return 
	 * List<TmpSysPosition>
	 */
	@SuppressWarnings("unchecked")
	public List<TmpSysPosition> findAllTmpSysPosisSQL(String query){
		Criteria createCriteria = o_tmpSysPositionDAO.createCriteria();
		if(StringUtils.isNotBlank(query)){
			createCriteria.add(Restrictions.or(Restrictions.like("posiName",query,MatchMode.ANYWHERE),
					Restrictions.like("orgName",query,MatchMode.ANYWHERE)));
		}
		createCriteria.addOrder(Order.asc("rowLine"));
		return createCriteria.list();
	}
	/**
	 * 批量保存岗位临时表
	 * add by 王再冉
	 * 2014-4-24  下午3:47:24
	 * desc : 
	 * @param posiList 
	 * void
	 */
	@Transactional
    public void saveTmpSysPosis(final List<TmpSysPosition> posiList) {
        this.o_tmpSysPositionDAO.getSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
            	connection.setAutoCommit(false);
                PreparedStatement pst = null;
                String sql = " insert into TMP_IMP_SYS_POSITION " +
                		 " (ID,POSI_CODE,POSI_NAME,ORG_ID,VALIDATE_INFO,ROW_LINE,ESORT,ORG_NAME) " + 
                		 " values(?,?,?,?,?,?,?,?) ";
                
                pst = connection.prepareStatement(sql);
                
                for (TmpSysPosition posi : posiList) {
					pst.setString(1, posi.getId());
					pst.setString(2, posi.getPosiCode());
					pst.setString(3, posi.getPosiName());
					if(null != posi.getSysOrganization()){
						pst.setString(4, posi.getSysOrganization().getId());
					}else{
						pst.setString(4, null);
					}
					pst.setString(5, posi.getValidateInfo());
					pst.setInt(6, posi.getRowLine());
					pst.setInt(7, posi.getSn());
					pst.setString(8, posi.getOrgName());
					pst.addBatch();
				}
                pst.executeBatch();
                connection.commit();
                connection.setAutoCommit(true);
            }
        });
    }
	/**
	 * 读取岗位excel，验证并保存临时表
	 * add by 王再冉
	 * 2014-4-24  下午3:40:42
	 * desc : 
	 * @param excelDatas
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> importPosiData(List<List<String>> excelDatas){
		//删除临时表数据
		this.deleteTmpSysPositionSQL();
		Boolean flag = true;
		String posiCode = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<TmpSysPosition> list = new ArrayList<TmpSysPosition>();
		List<String> ExcPosiCodeList = new ArrayList<String>();//excel表中所有角色编号
		List<String> posiCodeList = o_positionBO.findAllPosiIdsAndPosiCodes();
		Map<String,Object> orgMap = this.findAllOrgMap();
		if (excelDatas != null && excelDatas.size() > 0) {
			// 依次读取EXCEL第三行以后的数据（前两行为标题与说明）
			for (int row = 2; row < excelDatas.size(); row++){
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				StringBuffer error=new StringBuffer("");//验证错误信息
				TmpSysPosition tmpPosi = new TmpSysPosition();
				posiCode = rowDatas.get(0);
				tmpPosi.setId(Identities.uuid());
				if(StringUtils.isBlank(posiCode)){
					error.append("岗位编号为空;");
					flag = false;
				}else{
					tmpPosi.setPosiCode(posiCode);//岗位编号
				}
				if(StringUtils.isBlank(rowDatas.get(1))){
					error.append("岗位名称为空;");
					flag = false;
				}else{
					tmpPosi.setPosiName(rowDatas.get(1));//岗位名称
				}
				//所属机构
				if(StringUtils.isNotBlank(rowDatas.get(2))){
					if(null != orgMap.get(rowDatas.get(2))){
						tmpPosi.setSysOrganization(new SysOrganization(rowDatas.get(2)));
						tmpPosi.setOrgName(orgMap.get(rowDatas.get(2)).toString());
					}else{
						error.append("所属机构不存在;");
						flag = false;
					}
				}
				if(rowDatas.size() > 3){
					if(StringUtils.isNotBlank(rowDatas.get(3))){
						tmpPosi.setSn(Integer.parseInt(rowDatas.get(3)));//排序
					}
				}
				tmpPosi.setRowLine(row+1);
				if(ExcPosiCodeList.contains(posiCode) || posiCodeList.contains(posiCode)){//角色编号重复
					error.append("岗位编号重复;");
					flag = false;
				}
				tmpPosi.setValidateInfo(error.toString());
				ExcPosiCodeList.add(posiCode);
				list.add(tmpPosi);
			}
		}
		//保存角色临时表
		this.saveTmpSysPosis(list);
		resultMap.put("datas", list);
		resultMap.put("errorInfo", flag);
		resultMap.put("success", true);
		return resultMap;
	}
	/**
	 * 查询系统中所有机构，验证岗位所属机构是否存在
	 * add by 王再冉
	 * 2014-4-24  下午3:33:22
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String,Object> findAllOrgMap(){
		Map<String,Object> map = new HashMap<String, Object>();
		List<SysOrganization> orgList = o_orgGridBO.findAllOrganizations();
		if(null != orgList){
			for(SysOrganization org : orgList){
				map.put(org.getId(), org.getOrgname());
			}
		}
		return map;
	}
	/**
	 * 查看临时表数据
	 * add by 王再冉
	 * 2014-4-24  下午4:20:54
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> findAllTmpSysPositions(String query){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> orgList = new ArrayList<Map<String,Object>>();
		Boolean errorInfo = true;
		List<TmpSysPosition> tmpPosiList = this.findAllTmpSysPosisSQL(query);
		for(TmpSysPosition tmpPosi : tmpPosiList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", tmpPosi.getId());
			map.put("posiCode", tmpPosi.getPosiCode());
			map.put("posiName", tmpPosi.getPosiName());
			map.put("orgName", tmpPosi.getOrgName());
			map.put("sn", tmpPosi.getSn());
			map.put("rowLine", tmpPosi.getRowLine());
			map.put("validateInfo", tmpPosi.getValidateInfo());
			if(StringUtils.isNotBlank(tmpPosi.getValidateInfo())){
				errorInfo = false;
			}
			orgList.add(map);
		}
		resultMap.put("datas", orgList);
		resultMap.put("errorInfo", errorInfo);//错误信息
		return resultMap;
	}
	/**
	 * 导入岗位数据
	 * add by 王再冉
	 * 2014-4-25  上午9:06:59
	 * desc :  
	 * void
	 */
	public void saveAllPositionsFromTmpPosi(){
		List<TmpSysPosition> tmpPosiList = this.findAllTmpSysPosisSQL(null);
		List<SysPosition> posiList = new ArrayList<SysPosition>();
		for(TmpSysPosition tmpPosi : tmpPosiList){
			SysPosition posi = new SysPosition();
			posi.setId(tmpPosi.getPosiCode());
			posi.setPosicode(tmpPosi.getPosiCode());
			posi.setPosiname(tmpPosi.getPosiName());
			posi.setSn(tmpPosi.getSn());
			posi.setSysOrganization(tmpPosi.getSysOrganization());
			posi.setPosiStatus(Contents.STATUS_NORMAL);
			posi.setDeleteStatus(Contents.STATUS_NORMAL);
			posiList.add(posi);
		}
		//批量保存岗位
		o_positionBO.savePositionsSome(posiList);
		this.deleteTmpSysPositionSQL();
	}
	/**
	 * 导入全部数据时岗位信息的保存，需要验证机构临时表及机构实体表数据
	 * add by 王再冉
	 * 2014-4-25  下午5:12:53
	 * desc : 
	 * @param excelDatas
	 * @return 
	 * Map<String,Object>
	 */
	public Map<String, Object> importAllPosiData(List<List<String>> excelDatas){
		//删除临时表数据
		this.deleteTmpSysPositionSQL();
		Boolean flag = true;
		String posiCode = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<TmpSysPosition> list = new ArrayList<TmpSysPosition>();
		List<String> ExcPosiCodeList = new ArrayList<String>();//excel表中所有角色编号
		List<String> posiCodeList = o_positionBO.findAllPosiIdsAndPosiCodes();
		Map<String,Object> orgMap = this.findAllOrgMap();//机构实体表数据
		Map<String,Object> tmpOrgMap = o_tmpImpOrganizationBO.findAllTmpOrgMap();//临时表机构信息
		if (excelDatas != null && excelDatas.size() > 0) {
			//依次读取EXCEL第三行以后的数据（前两行为标题与说明）
			for(int row = 2; row < excelDatas.size(); row++){
				List<String> rowDatas = (List<String>) excelDatas.get(row);
				StringBuffer error=new StringBuffer("");//验证错误信息
				TmpSysPosition tmpPosi = new TmpSysPosition();
				tmpPosi.setId(Identities.uuid());
				
				if(rowDatas.size() == 0){
					break;
				}
				
				posiCode = rowDatas.get(0);
				
				if(ExcPosiCodeList.contains(posiCode) || posiCodeList.contains(posiCode)){//角色编号重复
					error.append("岗位编号重复;");
					flag = false;
				}
				if(StringUtils.isBlank(posiCode)){
					error.append("岗位编号为空;");
					flag = false;
				}else{
					tmpPosi.setPosiCode(posiCode);//岗位编号
				}
				if(StringUtils.isBlank(rowDatas.get(1))){
					error.append("岗位名称为空;");
					flag = false;
				}else{
					tmpPosi.setPosiName(rowDatas.get(1));//岗位名称
				}
				//所属机构
				if(StringUtils.isNotBlank(rowDatas.get(2))){
					//验证机构实体表和验证机构临时表
					if(null != orgMap.get(rowDatas.get(2)) || null != tmpOrgMap.get(rowDatas.get(2))){
						if(null != orgMap.get(rowDatas.get(2))){//机构实体表存在
							tmpPosi.setSysOrganization(new SysOrganization(rowDatas.get(2)));
							tmpPosi.setOrgName(orgMap.get(rowDatas.get(2)).toString());
						}
						//如果机构实体表存在，临时表也存在，保存临时表中的信息
						if(null != tmpOrgMap.get(rowDatas.get(2))){
							tmpPosi.setSysOrganization(new SysOrganization(rowDatas.get(2)));
							tmpPosi.setOrgName(tmpOrgMap.get(rowDatas.get(2)).toString());
						}
					}else{
						error.append("所属机构不存在;");
						flag = false;
					}
				}else{
					error.append("所属机构不存在;");
					flag = false;
				}
				if(rowDatas.size() > 3){
					if(StringUtils.isNotBlank(rowDatas.get(3))){
						tmpPosi.setSn(Integer.parseInt(rowDatas.get(3)));//排序
					}
				}
				tmpPosi.setRowLine(row+1);
				tmpPosi.setValidateInfo(error.toString());
				ExcPosiCodeList.add(posiCode);
				list.add(tmpPosi);
			}
		}
		//保存角色临时表
		this.saveTmpSysPosis(list);
		resultMap.put("datas", list);
		resultMap.put("errorInfo", flag);
		resultMap.put("success", true);
		return resultMap;
	}
	/**
	 * 查询全部岗位临时表sql
	 * add by 王再冉
	 * 2014-4-29  下午3:12:28
	 * desc : 
	 * @return 
	 * List<Map<String,String>>
	 */
	public List<TmpSysPosition> findAllTmpPositionsSQL() {
		List<TmpSysPosition> tmpPosiList = new ArrayList<TmpSysPosition>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM tmp_imp_sys_position ");
		SQLQuery sqlQuery = o_tmpSysPositionDAO.createSQLQuery(sql.toString());
        @SuppressWarnings("unchecked")
		List<Object[]> list = sqlQuery.list();
		for(Object[] o :list){
			String id = "";
			String posiCode = "";
			String posiName = "";
			String orgId = "";
			String validataInfo = "";
			int rowLine = 0;
			int sn = 0;
			String orgName = "";
            if(null != o[0]){
            	id = o[0].toString();
            }if(null != o[1]){
            	posiCode = o[1].toString();
            }if(null != o[2]){
            	posiName = o[2].toString();
            }if(null != o[3]){
            	orgId = o[3].toString();
            }if(null != o[4]){
            	validataInfo = o[4].toString();
            }if(null != o[5]){
            	rowLine = Integer.parseInt(o[5].toString());
            }if(null != o[6]){
            	sn = Integer.parseInt(o[6].toString());
            }if(null != o[7]){
            	orgName = o[7].toString();
            }
            TmpSysPosition tmpPosi = new TmpSysPosition();
            tmpPosi.setId(id);
            tmpPosi.setPosiCode(posiCode);
            tmpPosi.setPosiName(posiName);
            tmpPosi.setSysOrganization(new SysOrganization(orgId));
            tmpPosi.setRowLine(rowLine);
            tmpPosi.setOrgName(orgName);
            tmpPosi.setValidateInfo(validataInfo);
            tmpPosi.setSn(sn);
            tmpPosiList.add(tmpPosi);
		}
        return tmpPosiList;
	}

}
