package com.fhd.sys.web.controller.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.DateUtils;
import com.fhd.entity.sys.log.BusinessLog;
import com.fhd.sys.business.log.BusinessLogBO;

/**
 * 业务日志control类.
 * @author 吴德福
 * @version V1.0 创建时间：2013--10-12 
 * Company FirstHuiDa.
 */
@Controller
public class BusinessLogControl {

	@Autowired
	private BusinessLogBO o_businessLogBO;

	/**
	 * 分页查询业务日志.
	 * @author 吴德福
	 * @param limit
	 * @param start
	 * @param sort
	 * @param query
	 * @return Map<String, Object>
	 */
    @ResponseBody
    @RequestMapping("/sys/log/findBusinessLogList.f")
    public Map<String, Object> findBusinessLogList(int limit, int start, String sort, String query) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	
	    Page<BusinessLog> page = new Page<BusinessLog>();
	    page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	    page.setPageSize(limit);
	    page = o_businessLogBO.findBusinessLogByPage(page, sort, query);
	    List<BusinessLog> businessLogList = page.getResult();
	    if (null != businessLogList && businessLogList.size() > 0) {
	        Map<String, Object> row = null;
	        for (BusinessLog businessLog : businessLogList) {
	            row = new HashMap<String, Object>();
	            row.put("id", businessLog.getId());
				row.put("username", businessLog.getSysUser().getUsername());
				row.put("ip", businessLog.getIp());
				row.put("operateTime",DateUtils.formatDate(businessLog.getOperateTime(), "yyyy-MM-dd"));
				row.put("operateType", businessLog.getOperateType());
				row.put("moduleName", businessLog.getModuleName());
				row.put("isSuccess", businessLog.getIsSuccess());
				if(StringUtils.isNotBlank(businessLog.getOperateRecord())){
					row.put("operateRecord", businessLog.getOperateRecord());
				}else{
					row.put("operateRecord", "");
				}
				if (null != businessLog.getSysOrganization()) {
					row.put("orgname",businessLog.getSysOrganization().getOrgname());
				}
	            
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
}