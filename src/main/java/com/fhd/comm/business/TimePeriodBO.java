package com.fhd.comm.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.dao.comm.TimePeriodDAO;
import com.fhd.entity.comm.TimePeriod;
import com.fhd.fdc.utils.Contents;

/**
 * 时间区间维度BO.
 * @author  吴德福
 * @version  
 * @since    Ver 1.1
 * @Date     2013-1-7     下午04:24:20
 * @see      
 */
@Service
@SuppressWarnings("unchecked")
public class TimePeriodBO {

	@Autowired
	private TimePeriodDAO o_timePeriodDAO;
	
	/**
	 * 根据id查询时间区间维度.
	 * @param id
	 * @return TimePeriod
	 */
	public TimePeriod findTimePeriodById(String id){
		return o_timePeriodDAO.get(id);
	}
	
	/**
	 * 根据id查询时间区间维度.
	 * @param id
	 * @return TimePeriod
	 */
	public List<TimePeriod> findTimePeriodByParentId(String parentId){
		Criteria criteria = o_timePeriodDAO.createCriteria();
		criteria.add(Restrictions.eq("parent.id", parentId));
		return criteria.list();
	}
	
	/**
	 * 根据查询条件查询时间区间维度.
	 * @param year 年
	 * @param month 月
	 * @param type 频率
	 * @return TimePeriod
	 */
	public TimePeriod findTimePeriodByFre(String year, String month, String type){
		TimePeriod timePeriod = null;
		Criteria criteria = o_timePeriodDAO.createCriteria();
		
		criteria.add(Restrictions.eq("year", year));
		criteria.add(Restrictions.eq("month", month));
		criteria.add(Restrictions.eq("type", type));
		List<TimePeriod> list = criteria.list();
		if(null != list && list.size()>0){
			timePeriod = list.get(0);
		}
		return timePeriod;
	}
	/**
	 * 根据当前时间查询当前时间所在'月'的时间区间维度.
	 * @param type 时间区间维频率
	 * @return TimePeriod
	 */
	public TimePeriod findTimePeriodBySome(String type){
		TimePeriod timePeriod = null;
		Criteria criteria = o_timePeriodDAO.createCriteria();
		if(StringUtils.isNotBlank(type)){
			criteria.add(Restrictions.eq("type", type));
		}else{
			criteria.add(Restrictions.eq("type", "0frequecy_month"));
		}
		Date currentDate = new Date();
		criteria.add(Restrictions.le("startTime", currentDate));
		criteria.add(Restrictions.ge("endTime", currentDate));
		List<TimePeriod> list = criteria.list();
		if(null != list && list.size()>0){
			timePeriod = list.get(0);
		}
		return timePeriod;
	}
	
	/**
	 * 查询全部数据并已MAP方式存储
	 * @return HashMap<String, TimePeriod>
	 * @author 金鹏祥
	 */
	public HashMap<String, TimePeriod> findTimePeriodMapAll(){
		Criteria criteria = o_timePeriodDAO.createCriteria();
		criteria.add(Restrictions.eq("type", "0frequecy_month"));
		
		List<TimePeriod> list = criteria.list();
		HashMap<String, TimePeriod> map = new HashMap<String, TimePeriod>();
		
		for (TimePeriod timePeriod : list) {
			map.put(timePeriod.getId(), timePeriod);
		}
		return map;
	}
	
	/**
	 * 查询全部数据并已MAP方式存储
	 * @param o_timePeriodDAO 区间维度数据层
	 * @return HashMap<String, TimePeriod>
	 * @author 金鹏祥
	 */
	public HashMap<String, TimePeriod> findTimePeriodMapAll(TimePeriodDAO o_timePeriodDAO){
		Criteria criteria = o_timePeriodDAO.createCriteria();
		criteria.add(Restrictions.eq("type", "0frequecy_month"));
		
		List<TimePeriod> list = criteria.list();
		HashMap<String, TimePeriod> map = new HashMap<String, TimePeriod>();
		
		for (TimePeriod timePeriod : list) {
			map.put(timePeriod.getId(), timePeriod);
		}
		return map;
	}
	
	/**
	 * 得到区间维度集合
	*/
	public HashMap<String, ArrayList<String>> findTimePeriodByCurrentTimeMapAll() {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		Criteria c = o_timePeriodDAO.createCriteria();
        List<TimePeriod> list = c.list();
		for (TimePeriod timePeriod : list) {
			if(map.get(timePeriod.getType()) != null){
				map.get(timePeriod.getType()).add(timePeriod.getId() + "--" + timePeriod.getStartTime() + "--" + timePeriod.getEndTime());
			}else{
				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList.add(timePeriod.getId() + "--" + timePeriod.getStartTime() + "--" + timePeriod.getEndTime());
				map.put(timePeriod.getType(), arrayList);
			}
		}
		
		return map;
	}
	
	/**
     * 查询时间纬度表,类型为月.
     * @param year 年份信息
     */
    public List<TimePeriod> findTimePeriodByMonthType(String year) {
        Criteria timeCriteria = this.o_timePeriodDAO.createCriteria();
        timeCriteria.setCacheable(true);// 缓存时间纬度提高效率
        timeCriteria.add(Restrictions.eq("year", String.valueOf(year)));
        timeCriteria.add(Restrictions.eq("type", "0frequecy_month"));
        timeCriteria.addOrder(Order.asc("endTime"));
        timeCriteria.addOrder(Order.desc("startTime"));
        return timeCriteria.list();
    }
    
    /**
     * 根据类型 时间 取得特定时间的时间维度名称
     * @param 时间
     * @type 格式
     * @return 时间区间维度中文名
     */
    public String findTimePeriodFullNameBySome(Date date,String type) {
        Criteria timeCriteria = this.o_timePeriodDAO.createCriteria();
        timeCriteria.setCacheable(true);// 缓存时间纬度提高效率
        timeCriteria.add(Restrictions.or(Restrictions.lt("startTime", date), Restrictions.eq("startTime", date)));
        timeCriteria.add(Restrictions.or(Restrictions.gt("endTime", date), Restrictions.eq("endTime", date)));
        timeCriteria.add(Restrictions.eq("type", type));
        List<TimePeriod> list = timeCriteria.list();
        if(null != list && list.size()>0){
        	return list.get(0).getTimePeriodFullName();
        }
        return null;
    }
    
    /**时间区间表中所有年份信息
     * @return
     */
    public List<TimePeriod> findAllYearData(){
        Criteria timeCriteria = this.o_timePeriodDAO.createCriteria();
        timeCriteria.add(Restrictions.eq("type", Contents.FREQUECY_YEAR));
        return timeCriteria.list();
    }
 }