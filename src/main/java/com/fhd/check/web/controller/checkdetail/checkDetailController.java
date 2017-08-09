package com.fhd.check.web.controller.checkdetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.check.business.checkDetail.CheckDetailBO;
import com.fhd.core.dao.Page;
import com.fhd.entity.check.checkdetail.CheckDetail;

/*
 * 查询所有评分细则controller
 * AUTHOR:Perry Guo
 * DATE:2017-07-17
 * PARAM: int start 起页数
 * 		  int limit 每页显示个数
 * 		  String query 查询 
 * 		  String sort  排序 
 * */
@Controller
public class checkDetailController {

	@Autowired
	private CheckDetailBO checkDetailBO;
	
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/check/checkdetail/finCheckDetailAllPage.f")
	public List<Map> finCheckDetailAllPage (int start, int limit, String query, String sort)
	{
		Page<CheckDetail> page = new Page<CheckDetail>();
		page.setPageNo(limit == 0 ? 0 : (start / limit) + 1);
		page.setPageSize(limit);
		page=checkDetailBO.finCheckDetailAllPage(page, query, sort);
		//获取启用状态并判断是否显示灯（0为显示红灯，1为显示绿灯）
		List<Map> stutesList=checkDetailBO.validaCheckDetail();
		String stutes="";
		if(stutesList.size()!=0)
		{
			for (int i = 0; i < stutesList.size(); i++) {
				if(i!=stutesList.size()-1)
				{
					stutes=stutes+stutesList.get(i).get("errorMes")+"<br>";
				}else
				{
					stutes=stutes+stutesList.get(i).get("errorMes");
				}
				
			}
		}else
		{
			stutes="1";
		}
		List<CheckDetail> list=page.getResult();
		List<Map> data=new ArrayList<Map>() ;
		for (int i = 0; i < list.size(); i++) {
		Map<String, Object> map=new HashMap<String,Object>();
		map.put("id", list.get(i).getId());
		map.put("projectId", list.get(i).getCheckComment().getProject().getId());
		map.put("commentId", list.get(i).getCheckComment().getId());
		map.put("projectName", list.get(i).getCheckComment().getProject().getName());
		map.put("commentName", list.get(i).getCheckComment().getName());
		map.put("name", list.get(i).getName());
		map.put("detailStandard", list.get(i).getDetailStandard());
		map.put("detailScore", list.get(i).getDetailScore());
		map.put("detailStutes",stutes);
		data.add(map);
		}
		
		return data;
	}
	
	/*
	 * 保存更新考评细则controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@ResponseBody
	@RequestMapping("/check/checkdetail/savaCheckDetail.s")
	public boolean savaCheckDetail(String data) {
		return checkDetailBO.savaCheckDetail(data);
	}
	/*
	 * 删除考评项目controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-17
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@ResponseBody
	@RequestMapping("/check/checkdetail/deleteCheckDetail.d")
	public boolean deleteCheckDetail(String data) {
		return checkDetailBO.deleteCheckDetail(data);
	}
	
	/*
	 * 启用考评标准controller
	 * AUTHOR:Perry Guo
	 * DATE:2017-07-22
	 * PARAM: JsonString jsonArray  需保存或更新的考评项目
	 * */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/check/checkdetail/validaCheckDetail.f")
	public List<Map> validaCheckDetail() {
	List<Map> list=checkDetailBO.validaCheckDetail();
		
		return list;
	}
}
