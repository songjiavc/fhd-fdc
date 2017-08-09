package com.fhd.sm.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.dao.kpi.KpiMemoDAO;
import com.fhd.entity.kpi.KpiGatherResult;
import com.fhd.entity.kpi.KpiMemo;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sm.business.KpiGatherResultBO;
import com.fhd.sm.business.KpiMemoBO;
import com.fhd.sm.web.form.KpiMemoForm;

/**
 * 指标备注Controller
 * 
 * @author 郝静
 * @version
 * @since Ver 1.1
 * @Date 2013 2013-7-18 上午11:26:34
 * 
 * @see
 */

@Controller
public class KpiMemoControl {


    @Autowired
    private KpiMemoBO o_kpiMemoBO;
	@Autowired
	private KpiMemoDAO o_kpiMemoDAO;
	@Autowired
    private KpiGatherResultBO o_kpiGatherResultBO;
	
	
	/**
	 * 修改备注信息
	 * @param request
	 * @param id  节点id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/kpimemo/findkpimemobyid.f")
	public Map<String, Object> findKpiMemoById(HttpServletRequest request, String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		KpiMemo kpiMemo = o_kpiMemoDAO.get(id);		//根据id查询实体
		Map<String, Object> inmap = new HashMap<String, Object>();
		inmap.put("theme", kpiMemo.getTheme());
		inmap.put("important", kpiMemo.getImportant());
		inmap.put("memo", kpiMemo.getMemo());
		map.put("data", inmap);
		map.put("success", true);
		return map;
    }
	
	/**
	 * 备注信息列表加载
	 * @param query	主题名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/kpi/kpimemo/kpimemolistloader.f")
	public Map<String, Object> findMemoListLoader(String kgrid,int start, int limit){
		Page<KpiMemo> page = new Page<KpiMemo>();
		page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
		page.setPageSize(limit);
		Map<String, Object> map = new HashMap<String, Object>();
		if(kgrid!=null){
			KpiGatherResult kgr = o_kpiGatherResultBO.findKpiGatherResultById(kgrid);
			page = o_kpiMemoBO.findKpiMemoBySomes(kgr.getId(),page);
			
			List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
			List<KpiMemo> infoList = page.getResult();
			for(KpiMemo info : infoList){
				Map<String,Object> m = new HashMap<String,Object>();
				m.put("id", info.getId());
				m.put("theme", info.getTheme());
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sf.format(info.getOperTime());
				m.put("operTime", date);
				m.put("important", info.getImportant());
				datas.add(m);
			}
			map.put("totalCount", page.getTotalItems());
			map.put("datas", datas);
		}
		return map;
	}
	/**
	 * 保存指标备注信息
	 * @param kpiMemoForm 指标备注对象
	 * @param id 备注信息id	
	 * @param response
	 * @throws IOException 
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/kpi/kpimemo/savekpimemo.f")
	public void saveAnalysisInfo(KpiMemoForm kpiMemoForm,String id,String kgrid, HttpServletResponse response) throws IOException  {
		PrintWriter out = response.getWriter();
		String isSave = "false";
		KpiMemo kpiMemo = new KpiMemo();
		try{
			if(StringUtils.isNotBlank(id)){
				//更新
				String ids[] = id.split(",");
				kpiMemo.setId(ids[0]);
				kpiMemo.setTheme(kpiMemoForm.getTheme());
				kpiMemo.setImportant(kpiMemoForm.getImportant());
				kpiMemo.setMemo(kpiMemoForm.getMemo());
				KpiMemo km = o_kpiMemoDAO.get(id);		//根据id查询实体
				kpiMemo.setOperTime(km.getOperTime());
				String operBy = UserContext.getUser().getUsername();
				kpiMemo.setOperBy(operBy);
				if(kgrid!=null){
					KpiGatherResult kgr = o_kpiGatherResultBO.findKpiGatherResultById(kgrid);
					kpiMemo.setKpiGatherResult(kgr);
					
//					改变t_kpi_kpi_gather_result是否有备注的标识
					kgr.setIsMemo("1");
					o_kpiGatherResultBO.saveKpiGatherResult(kgr);
				}

				o_kpiMemoBO.mergeKpiMemo(kpiMemo);
				isSave = "true";
			}else{
				//保存
				kpiMemo.setId(Identities.uuid());
				kpiMemo.setTheme(kpiMemoForm.getTheme());
				kpiMemo.setImportant(kpiMemoForm.getImportant());
				kpiMemo.setMemo(kpiMemoForm.getMemo());
				kpiMemo.setOperTime(new Date());
				String operBy = UserContext.getUser().getUsername();
				kpiMemo.setOperBy(operBy);
				
				if(kgrid!=null){
					KpiGatherResult kgr = o_kpiGatherResultBO.findKpiGatherResultById(kgrid);
					kpiMemo.setKpiGatherResult(kgr);
	//				改变t_kpi_kpi_gather_result是否有备注的标识
					kgr.setIsMemo("1");
					o_kpiGatherResultBO.saveKpiGatherResult(kgr);
				}
				o_kpiMemoBO.saveKpiMemo(kpiMemo);
				isSave = "true";
			}
			out.write(isSave);
		}finally {
			out.close();
		}
	}
	
	/**
	 * 删除节点(逻辑删除)
	 * @param request
	 * @param ids 备注信息id
	 * @return
	 */
	 @ResponseBody
	 @RequestMapping(value = "/kpi/kpimemo/removeMemobyId.f")
	  public boolean removeAnalysisEntryById(HttpServletRequest request, String ids) {
		 if(StringUtils.isNotBlank(ids)){
			 String[] idArray = ids.split(",");
			 for (String id : idArray) {
				 KpiMemo kpiMemo = o_kpiMemoDAO.get(id);
				 if(null != kpiMemo){
//						改变t_kpi_kpi_gather_result是否有备注的标识
					 KpiGatherResult kgr = o_kpiGatherResultBO.findKpiGatherResultById(kpiMemo.getKpiGatherResult().getId());
					 o_kpiMemoBO.removeKpiMemo(kpiMemo);
					 List<Object[]> objList = o_kpiMemoBO.findMemoByKgrId(kgr.getId());
					 if(objList.size() > 0){
						 kgr.setIsMemo("1");
					 }else{
						 kgr.setIsMemo("");
					 }
					 
					 o_kpiGatherResultBO.saveKpiGatherResult(kgr);
				 }
			 }
			 return true;
		 }else{
			 return false;
		 }
	  }
	 
}
