package com.fhd.dao.risk;

import com.fhd.core.dao.Page;
import com.fhd.core.dao.hibernate.HibernateDao;
import com.fhd.entity.risk.ManageReport;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * 管理报告数据层
 * 
 * @author 张健
 * @date 2013-12-31
 * @since Ver 1.1dc.addOrder(Order.desc(sort));
 */
@Repository
public class ManageReportDAO  extends HibernateDao<ManageReport, String>{

    /**
     * 根据类型查询报告列表
     * 
     * @author 张健
     * @param 
     * @return 
     * @date 2014-1-3
     * @since Ver 1.1
     */
    public Page<ManageReport> findmanagereportlist(Object[] typeIds,Page<ManageReport> page,String query, String sort, String dir){
        String companyId = UserContext.getUser().getCompanyid();
        DetachedCriteria dc = DetachedCriteria.forClass(ManageReport.class);
        dc.add(Restrictions.in("reportType.id", typeIds));
        dc.add(Restrictions.eq("deleteStatus", Contents.DELETE_STATUS_USEFUL));//启用状态
        dc.add(Restrictions.eq("company.id", companyId));
        
        
        if(StringUtils.isNotBlank(query)){  //按姓名查询
            dc.add(Restrictions.like("reportName", query, MatchMode.ANYWHERE));
        }
        
        if("desc".equals(dir)){
            dc.addOrder(Order.desc(sort));
        }else{
            dc.addOrder(Order.asc(sort));
        }
        
        return this.findPage(dc, page, false);
    }
    
}