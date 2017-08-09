package com.fhd.sm.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.dao.Page;
import com.fhd.dao.kpi.KpiMemoDAO;
import com.fhd.entity.kpi.KpiMemo;

/**
 * 
 * 指标备注信息业务逻辑BO
 * 
 * @author 郝静
 * @version
 * @since Ver 1.1
 * @Date 2013 2013-7-18 上午10:45:52
 * 
 * @see
 */
@Service
@SuppressWarnings("unchecked")
public class KpiMemoBO {

    
    @Autowired
    private KpiMemoDAO o_kpiMemoDAO;

    
    /**
     * 根据目标ID查询最新的关联指标的采集结果
     * 
     * @param map
     * @param start
     *            分页起始位置
     * @param limit
     *            显示记录数
     * @param queryName
     *            指标类型名称
     * @param sortColumn
     *            排序字段
     * @param dir
     *            排序方向
     * @param smid
     *            目标ID
     * @return
     */
    public List<Object[]> findMemoByKgrId(String kgrids) {

    	List<Object[]> list = null;
        StringBuffer selectBuf = new StringBuffer();
        StringBuffer fromLeftJoinBuf = new StringBuffer();
        selectBuf.append("select kpi_gatherreslut_id,IMPORTANT,theme,memo,operation_time ");
        fromLeftJoinBuf.append("from t_kpi_rela_memo");
        fromLeftJoinBuf.append(" where 1=1 ");
        if (StringUtils.isNotBlank(kgrids)) {
            fromLeftJoinBuf.append(" and kpi_gatherreslut_id =:gather_id ");
        }
        fromLeftJoinBuf.append("order by IMPORTANT asc , operation_time desc ");
        SQLQuery sqlquery = o_kpiMemoDAO.createSQLQuery(selectBuf.append(fromLeftJoinBuf).toString());
        sqlquery.setParameter("gather_id", kgrids);
        sqlquery.setMaxResults(1);
        list = sqlquery.list();
        return list;
    }
    
    /**根据指标采集结果id集合查询出备注信息
     * @param resultIdList 指标采集结果id集合
     * @return
     */
    public Map<String, KpiMemo> findMemoByKpiGatherIds(List<String> resultIdList){
    	Map<String,KpiMemo> memoMap = new HashMap<String, KpiMemo>();
    	Criteria criteria = o_kpiMemoDAO.createCriteria();
    	criteria.createAlias("kpiGatherResult", "result");
    	criteria.add(Restrictions.in("result.id", resultIdList));
    	criteria.addOrder(Order.asc("result.id"));
    	criteria.addOrder(Order.asc("important"));
    	criteria.addOrder(Order.desc("operTime"));
    	List<KpiMemo> memoList = criteria.list();
    	for (KpiMemo kpiMemo : memoList) {
			String resultId = kpiMemo.getKpiGatherResult().getId();
			if(!memoMap.containsKey(resultId)){
				memoMap.put(resultId, kpiMemo);
			}
		}
    	return memoMap;
    }
    
	/**
	 * 根据采集结果id查找备注信息
	 * @param kgrId 采集结果id
	 * @param pages 分页对象
	 * @return
	 */
    public Page<KpiMemo> findKpiMemoBySomes(String kgrId,Page<KpiMemo> pages) {
		DetachedCriteria dc = DetachedCriteria.forClass(KpiMemo.class);
		dc.add(Restrictions.eq("kpiGatherResult.id", kgrId));
		dc.addOrder(Order.desc("operTime"));
		return o_kpiMemoDAO.findPage(dc, pages, false);
		
	}

    /**
	 * 更新实体
	 * @param 
     */
    @Transactional
    public void mergeKpiMemo(KpiMemo kpiMemo) {
    	o_kpiMemoDAO.merge(kpiMemo);
    }
	

    /**
	 * 删除实体
	 * @param 
     */
	@Transactional
	public void removeKpiMemo(KpiMemo kpiMemo) {
        if (null != kpiMemo) {
        	o_kpiMemoDAO.delete(kpiMemo);
        }
    }
	
	/**
	 * 保存实体
	 * @param 
	 */
	@Transactional
	public void saveKpiMemo(KpiMemo kpiMemo) {
		o_kpiMemoDAO.merge(kpiMemo);
	}
	
    

}
