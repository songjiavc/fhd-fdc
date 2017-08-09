package com.fhd.sys.web.controller.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fhd.core.dao.Page;
import com.fhd.entity.sys.log.SystemLog;
import com.fhd.sys.business.log.SystemLogBO;
import com.fhd.sys.business.log.SystemLogBO.LogFile;
import com.fhd.sys.web.form.log.SystemLogForm;

/**
 * 系统日志control类.
 * @author  吴德福
 * @version V1.0  创建时间：2013-10-12
 * Company FirstHuiDa.
 */
@Controller
@SessionAttributes(types =SystemLogForm.class)
public class SystemLogControl {
	
	@Autowired
	private SystemLogBO o_systemLogBO;
	
	/**
	 * 分页查询系统日志.
	 * @author 吴德福
	 * @param limit
	 * @param start
	 * @param sort
	 * @param query
	 * @return Map<String, Object>
	 */
    @ResponseBody
    @RequestMapping("/sys/log/findSystemLogList.f")
    public Map<String, Object> findSystemLogList(int limit, int start, String sort, String query) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	
	    Page<SystemLog> page = new Page<SystemLog>();
	    page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	    page.setPageSize(limit);
	    page = o_systemLogBO.findSystemLogByPage(page, sort, query);
	    List<SystemLog> systemLogList = page.getResult();
	    if (null != systemLogList && systemLogList.size() > 0) {
	        Map<String, Object> row = null;
	        for (SystemLog systemLog : systemLogList) {
	            row = new HashMap<String, Object>();
	            row.put("logDate", systemLog.getLogDate());
				row.put("logLevel", systemLog.getLogLevel());
				row.put("location", systemLog.getLocation());
				row.put("message", systemLog.getMessage());
	            
	            datas.add(row);
	        }
	        map.put("datas", datas);
	        map.put("totalCount", page.getTotalItems());
	    }else {
	        map.put("datas", new Object[0]);
	        map.put("totalCount", "0");
	    }
	    return map;
	}
    
    @ResponseBody
    @RequestMapping("/sys/log/findlogfilelist.f")
    public Map<String, Object> findLogFileList() throws Exception{
    	 Map<String, Object> map = new HashMap<String, Object>();
    	 List<LogFile> logFileList = o_systemLogBO.findLogFile();
    	 map.put("datas", logFileList);
    	 map.put("totalCount", logFileList.size());
    	 return map;
    }
    
    
}