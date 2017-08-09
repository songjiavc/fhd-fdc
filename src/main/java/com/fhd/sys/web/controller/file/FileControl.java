package com.fhd.sys.web.controller.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.sys.business.file.FileBO;
import com.fhd.sys.web.form.file.FileUploadForm;


@Controller
@SessionAttributes(types = FileUploadForm.class)
public class FileControl {
	@Autowired
	private FileBO o_fileBO;
	
	@RequestMapping("/file/uploadSwf.f")
	public void uploadSwf(String chooseWay,MultipartHttpServletRequest request,HttpServletResponse response)throws Exception {
		Map<String, MultipartFile> fileMap = request.getFileMap();
		Set<String> keySet = fileMap.keySet();
		String id = null;
		for (String key : keySet) {
			MultipartFile multipartFile = fileMap.get(key);
			FileUploadEntity fileUploadEntity = o_fileBO.uploadFile(chooseWay, multipartFile);
			id=fileUploadEntity.getId();
		}
		response.getWriter().print("{success:true,id:'"+id+"'}");
	}
	
	@ResponseBody
	@RequestMapping("/file/listMap.f")
	public List<Map<String, Object>> listMap(String fileType,String oldFileName, String[] ids){
		return o_fileBO.listMap(fileType, oldFileName, ids);
	}
	@ResponseBody
	@RequestMapping("/file/remove.f")
	public Map<String, Object> remove(String id) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("success", false);
		try {
			o_fileBO.remove(id);
			map.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
