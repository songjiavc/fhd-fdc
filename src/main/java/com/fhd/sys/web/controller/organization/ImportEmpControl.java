package com.fhd.sys.web.controller.organization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fhd.core.utils.encode.JsonBinder;
import com.fhd.entity.sys.entity.RiskFromExcel;
import com.fhd.entity.sys.orgstructure.TempEmpExcelData;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.sys.business.organization.ImportEmpBO;
import com.fhd.sys.web.form.file.FileForm;

/**
 * 人员临时表控制
 * */

@Controller
public class ImportEmpControl {

	@Autowired
	private ImportEmpBO o_importEmpBO;
	
	/**
	 * 上传人员数据模板并将数据写入数据临时表
	 * @author 金鹏祥
	 * @param form
	 * @param result
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/organization/uploadEmpData.f")
	public void uploadEmpData(FileForm form, HttpServletResponse response) throws IOException {
	    Map<String, Object> map = new HashMap<String, Object>();
//		String flag = "false";
		try{
			List<List<String>> excelDatas = (List<List<String>>)new ReadExcel()
				.readEspecialExcel(form.getFile().getInputStream(), 3);// 读取文件(第二页)
			
			//将数据写入数据库临时表
			ArrayList<TempEmpExcelData> tempEmpExcelDataList = o_importEmpBO.getTempEmpExcelDataList(excelDatas);
//			o_dataImportBO.saveRiskFromExcel(riskFromExcelList);
////			flag = "true";
//			o_dataImportBO.updateRiskFromExcelLevel(true,null,null);
//			response.getWriter().print(flag);
//			response.getWriter().close();
			map.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		response.getWriter().write(JsonBinder.buildNonDefaultBinder().toJson(map));
	}
}