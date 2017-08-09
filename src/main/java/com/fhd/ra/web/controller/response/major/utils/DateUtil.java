package com.fhd.ra.web.controller.response.major.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * @author zhenwei.liu created on 2013 13-8-29 下午5:35
 * @version $Id$
 */
public class DateUtil extends DateUtils{

    /** 锁对象 */
    private static final Object lockObj = new Object();

    /** 存放不同的日期模板格式的sdf的Map */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * 
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    System.out.println("put new sdf of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {

                        @Override
                        protected SimpleDateFormat initialValue() {
                            System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }
    
    
    
    
    
    
    
    
    
    
    
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static String[] parsePatterns = { "yyyy-MM-dd", 
      "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", 
      "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" ,"yyyyMMdd"};
    
    public static String getMonthFirstDay(Date dealDate)
    {
      Calendar calendar = Calendar.getInstance();
      if (dealDate != null) {
        calendar.setTime(dealDate);
      }
      calendar.set(5, 1);
      return df.format(calendar.getTime());
    }
    
    public static String getMonthLastDay(Date dealDate)
    {
      Calendar calendar = Calendar.getInstance();
      if (dealDate != null) {
        calendar.setTime(dealDate);
      }
      calendar.set(5, 
        calendar.getActualMaximum(5));
      return df.format(calendar.getTime());
    }
    
    public static String getNextDealDate(Date lastDealDate, int intervalMonths)
    {
      Calendar cal = Calendar.getInstance();
      cal.setTime(lastDealDate);
      cal.add(2, intervalMonths);
      return df.format(cal.getTime());
    }
    
    public static String getFutureDate(Date curentDate, int intervalDays)
      throws ParseException
    {
      Calendar cal = Calendar.getInstance();
      cal.setTime(curentDate);
      cal.add(5, intervalDays);
      return df.format(cal.getTime());
    }
    
    public static Date toDate(String strDate)
      throws Exception
    {
      if (StringUtils.isEmpty(strDate)) {
        throw new Exception("格式化日期出错!");
      }
      return df.parse(strDate);
    }
    
    public static String getToday()
      throws Exception
    {
      return df.format(new Date());
    }
    
    public static String dateToString(Date date)
      throws Exception
    {
      return df.format(date);
    }
    
    public static void main(String[] args)
    {
      try
      {
        System.out.println(getFutureDate(new Date(), 5));
      }
      catch (ParseException e)
      {
        e.printStackTrace();
      }
    }
    
    public static String getDate()
    {
      return getDate("yyyy-MM-dd");
    }
    
    public static String getDate(String pattern)
    {
      return DateFormatUtils.format(new Date(), pattern);
    }
    
    public static String formatDate(Date date, Object... pattern)
    {
      String formatDate = null;
      if ((pattern != null) && (pattern.length > 0)) {
        formatDate = DateFormatUtils.format(date, pattern[0].toString());
      } else {
        formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
      }
      return formatDate;
    }
    
    public static String getNoSepStrDate()
    {
      String tempDate = formatDate(new Date(), new Object[] { "yyyy-MM-dd HH:mm:ss" });
      return tempDate.replace("-", "").replace(":", "").replace(" ", "");
    }
    
    public static String getTime()
    {
      return formatDate(new Date(), new Object[] { "HH:mm:ss" });
    }
    
    public static String getDateTime()
    {
      return formatDate(new Date(), new Object[] { "yyyy-MM-dd HH:mm:ss" });
    }
    
    public static String getYear()
    {
      return formatDate(new Date(), new Object[] { "yyyy" });
    }
    
    public static String getMonth()
    {
      return formatDate(new Date(), new Object[] { "MM" });
    }
    
    public static String getDay()
    {
      return formatDate(new Date(), new Object[] { "dd" });
    }
    
    public static String getWeek()
    {
      return formatDate(new Date(), new Object[] { "E" });
    }
    
    public static Date parseDate(Object str)
    {
      if (str == null) {
        return null;
      }
      try
      {
        return parseDate(str.toString(), parsePatterns);
      }
      catch (ParseException e) {}
      return null;
    }
    
    public static long pastDays(Date date)
    {
      long t = new Date().getTime() - date.getTime();
      return t / 86400000L;
    }

}