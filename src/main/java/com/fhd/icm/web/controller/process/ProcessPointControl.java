package com.fhd.icm.web.controller.process;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.entity.process.ProcessPoint;
import com.fhd.entity.process.Process;
import com.fhd.fdc.utils.UserContext;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.process.ProcessPointBO;
import com.fhd.icm.web.form.process.ProcessPointForm;
import com.fhd.sys.web.form.dic.DictEntryForm;


/**
 *  流程节点维护
 * 
 * @author 宋佳
 * @version
 * @since Ver 1.1
 * @Date 2013  3-13
 */
@Controller
public class ProcessPointControl {

	
	@Autowired
	private ProcessPointBO o_processPointBO;
	@Autowired
	private ProcessBO o_processBO;
	/**
	 * 
	 * @author 宋佳 
	 * @param  processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/ProcessPoint/ProcessPoint/editProcessPoint.f")
	public Map<String,Object> findProcessPointForm(String processEditID,String processId){
		return o_processPointBO.findProcessPointForm(processEditID,processId);
		
	}
	/**
	 * 
	 * @author 宋佳 
	 * @param  processEditID
	 * @return
	 * @since  fhd　Ver 1.1
	*/
	@ResponseBody
	@RequestMapping("/ProcessPoint/ProcessPoint/editProcessPointforview.f")
	public Map<String,Object> findProcessPointFormForView(String processEditID){
		return o_processPointBO.findProcessPointFormForView(processEditID);
		
	}
/**
 *  
 *   删除流程节点
 * 
 * 
 * @author  宋佳
 * @param ProcessPointID
 * @since  fhd　Ver 1.1
*/
@ResponseBody
@RequestMapping("/ProcessPoint/ProcessPoint/removeProcessPoint.f")
public void removeProcessPointByID(String processPointID,HttpServletResponse response) throws IOException{
	PrintWriter out = response.getWriter();
	try {
		if(StringUtils.isNotBlank(processPointID)){
			o_processPointBO.removeProcessPointByID(processPointID);
		}
		out.print("true");
	} catch (Exception e) {
		e.printStackTrace();
		out.print("false");
	} finally{
		if(null != out){
			out.close();
		}
	}
	
}
/**
 *  
 *    删除父亲节点
 *  
 * 
 * @author  宋佳
 * @param ProcessPointID
 * @since  fhd　Ver 1.1
 */
@ResponseBody
@RequestMapping("/processpoint/removeparentpointbyid.f")
public void removeParentPointById(String ids,HttpServletResponse response) throws IOException{
	PrintWriter out = response.getWriter();
	try {
		if(StringUtils.isNotBlank(ids)){
			o_processPointBO.removeParentPointByID(ids);
		}
		out.print("true");
	} catch (Exception e) {
		e.printStackTrace();
		out.print("false");
	} finally{
		if(null != out){
			out.close();
		}
	}
}
/**
 *  
 *    保存流程节点
 *  
 * @author 宋佳
 * @param ProcessPointForm
 * @param parentId
 * @return
 * @since  fhd　Ver 1.1
*/
@ResponseBody
@RequestMapping("/processpoint/processpoint/saveprocesspoint.f")
public Map<String,Object> saveProcessPoint(ProcessPointForm processPointForm){
		return o_processPointBO.saveProcessPoint(processPointForm);
    }
/**
 * 
 *自动生成编号
 *  
 * 
 * @author 李克东
 * @param ProcessPointEditID
 * @param parentId
 * @return
 * @since  fhd　Ver 1.1
*/
@ResponseBody
@RequestMapping("/ProcessPoint/ProcessPoint/ProcessPointCode.f")
public Map<String,Object> findProcessPointCode(String processPointId,String processId){
	return o_processPointBO.findProcessPointCode(processPointId, processId);
}
/**
 * 根据流程ID 找到流程下所有节点
 * @author 宋佳
 * @param processId
 * @return
 */
@ResponseBody
@RequestMapping("/process/findprocesspointlistbypage.f")
public Map<String,Object> findProcessPointListByPage(int limit, int start, String query,String processId){
	
	Map<String,Object> resultMap;
	Page<ProcessPoint> page = new Page<ProcessPoint>();
	page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	page.setPageSize(limit);
	String companyId = UserContext.getUser().getCompanyid();
	resultMap=o_processPointBO.findProcessPointListByPage(page, query, processId,companyId);
	return resultMap;
	
	
}
/**
 * 根据节点ID 找到上级节点和节点进入条件
 * @author 宋佳
 * @param processId
 * @return
 */
@ResponseBody
@RequestMapping("/process/findParentListByPointId.f")
public Map<String,Object> findParentListByPointId(String processPointId){
	Map<String,Object> resultMap;
	String companyId = UserContext.getUser().getCompanyid();
	resultMap=o_processPointBO.findParentListByPointId(processPointId,companyId);
	return resultMap;
}
/**
 * 查询出流程中所包含所有的节点，作为数据字典展示
 * @param typeId
 * @author 宋佳
 * @return
 */
@ResponseBody
@RequestMapping("/processpoint/findallprocesspointbyprocessid.f")
public List<Map<String,String>> findAllProcessPointByProcessId(String processId,String processPointId){
	List<DictEntryForm> list=o_processPointBO.findAllProcessPointByProcessId(processId);
	List<Map<String, String>> l=new ArrayList<Map<String, String>>();
	for(DictEntryForm dictEntryForm:list)
	{
		Map<String,String> map=new HashMap<String,String>();
	    if(!dictEntryForm.getId().equals(processPointId))
		{	
	    	map.put("id", dictEntryForm.getId());
	    	map.put("name",dictEntryForm.getName());
	    	l.add(map);
		}
	}
	return l;
}
/**
 * 流程节点查询组建中用到的返回值类型
 * @param typeId
 * @author 宋佳
 * @return
 */
@ResponseBody
@RequestMapping("/processpoint/findprocesspointbyprocessidforcommon.f/{processId}")
public Map<String,Object> findProcessPointByProcessIdForCommon(@PathVariable("processId") String processId){
	List<DictEntryForm> list=o_processPointBO.findAllProcessPointByProcessId(processId);
	List<Map<String, String>> l=new ArrayList<Map<String, String>>();
	Map<String,Object> returnMap = new HashMap<String,Object>();
	for(DictEntryForm dictEntryForm:list)
	{
		Map<String,String> map=new HashMap<String,String>();
    	map.put("id", dictEntryForm.getId());
    	map.put("name",dictEntryForm.getName());
    	l.add(map);
	}
	returnMap.put("datas",l);
	returnMap.put("totalCount", l.size());
	return returnMap;
}
	/**
	 * 获取初始化中的流程和流程节点的值
	 * add by 宋佳
	 * 
	 */
	@ResponseBody
	@RequestMapping("/processpoint/findinitvalueforprocessandpoint.f")
	public Map<String,Object> findInitValueForProcessAndPoint(String processId,String pointId){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(pointId)){
			Process process = o_processBO.findProcessById(processId);
			resultMap.put("code", "");
			resultMap.put("dbid", process.getId());
			resultMap.put("id", process.getId());
			resultMap.put("pointId", "");
			resultMap.put("pointName", "");
			resultMap.put("text", process.getName());
		}else{
			ProcessPoint processPoint =o_processPointBO.findProcessPointById(pointId);
			resultMap.put("code", "");
			resultMap.put("dbid", processPoint.getProcess().getId());
			resultMap.put("id", processPoint.getProcess().getId());
			resultMap.put("pointId", processPoint.getId());
			resultMap.put("pointName", processPoint.getName());
			resultMap.put("text", processPoint.getProcess().getName());
		}
		return resultMap;
	}

}
