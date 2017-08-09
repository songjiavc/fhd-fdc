package com.fhd.sys.web.controller.organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sys.business.orgstructure.TmpImpPositionBO;
import com.fhd.sys.web.form.file.FileForm;
/**
 * 岗位导入control
 * @功能 : 
 * @author 王再冉
 * @date 2014-4-24
 * @since Ver
 * @copyRight FHD
 */
@Controller
public class TmpImpPositionControl {
	@Autowired
	private TmpImpPositionBO o_tmpImpPositionBO;
	
	/**
	 * 上传数据到岗位临时表
	 * add by 王再冉
	 * 2014-4-24  下午3:48:04
	 * desc : 
	 * @param form
	 * @param response
	 * @return
	 * @throws Exception 
	 * Map<String,Object>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@RequestMapping(value = "/sys/position/import/importposidata.f")
	public void importPosiData(FileForm form, HttpServletResponse response) throws Exception {
		List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
		.readEspecialExcel(form.getFile().getInputStream(), 2);// 读取文件(第1页)
		
		Map<String, Object> map = o_tmpImpPositionBO.importPosiData(excelDatas);
		
		response.getWriter().print(JSONSerializer.toJSON(map));
		response.getWriter().close();
	}
	
	/**
	 * 查看岗位临时表数据
	 * add by 王再冉
	 * 2014-4-24  下午4:12:08
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/position/import/findalltmpsyspositions.f")
	public Map<String, Object> findAllTmpSysPositions(String query) {
		return o_tmpImpPositionBO.findAllTmpSysPositions(query);
	}
	/**
	 * 岗位数据导入
	 * add by 王再冉
	 * 2014-4-25  上午9:03:22
	 * desc : 
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/position/import/importtosyspositionfromtmp.f")
	public Map<String, Object> importToSysPositionFromTmp() {
		Map<String, Object> data = new HashMap<String,Object>();
		o_tmpImpPositionBO.saveAllPositionsFromTmpPosi();
		data.put("success", true);
		return data;
	}

}
