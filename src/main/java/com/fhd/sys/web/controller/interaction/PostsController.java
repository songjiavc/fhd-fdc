package com.fhd.sys.web.controller.interaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.interaction.Posts;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.interaction.PostsBO;


/**
 * 帖子控制
 * */

@Controller
public class PostsController {
	@Autowired
	private PostsBO o_postsBO;
	
	/**
	 * 查询帖子列表
	 * add by 王再冉
	 * 2014-3-11  上午11:21:58
	 * desc : 
	 * @return 
	 * List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/interaction/findinteractionsgrid.f")
	public List<Map<String,Object>> findInteractionsGrid(String query){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		List<Posts> postsList = o_postsBO.findPostsByCompanyId(UserContext.getUser().getCompanyid(),query);//登录人公司帖子
		for(Posts posts : postsList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", posts.getId());
			map.put("title", posts.getTitle());//标题
			map.put("createTime", posts.getCreateTime().toString());//创建时间
			map.put("createEmp", posts.getCreateEmp().getEmpname());//创建人
			listmap.add(map);
		}
		return listmap;
	}
	/**
	 * 保存新帖
	 * add by 王再冉
	 * 2014-3-11  下午1:19:30
	 * desc : 
	 * @param contentEdit	帖子标题和内容
	 * @return 
	 * Boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/interaction/saveinteractioncontents")
	public Boolean saveInteractionContents(String contentEdit){
		Posts posts = new Posts();//帖子实体
		JSONObject contentJson = JSONObject.fromObject(contentEdit);
		posts.setTitle(contentJson.getString("title"));//标题
		posts.setContent(contentJson.getString("contents"));//内容
		if(StringUtils.isNotBlank(contentJson.getString("id"))){//修改
			posts.setId(contentJson.getString("id"));
		}else{//新增
			posts.setId(Identities.uuid());
			posts.setCreateEmp(UserContext.getUser().getEmp());//创建人为当前登录人
			posts.setCreateTime(new Date());//创建时间
			posts.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);//已启用
			posts.setCompany(new SysOrganization(UserContext.getUser().getCompanyid()));//当前登录人的公司
		}
		o_postsBO.savePosts(posts);
		return true;
	}
	
	/**
	 * 
	 * add by 王再冉
	 * 2014-3-11  下午3:06:23
	 * desc : 
	 * @param postsId
	 * @return 
	 * Map<String,Object>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/interaction/findpostsformbyid")
	public Map<String,Object> findPostsFormById(String postsId){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> formMap = new HashMap<String,Object>();
		Posts posts = o_postsBO.findPostsById(postsId);
		formMap.put("countentStr", posts.getContent());
		formMap.put("id", posts.getId());
		map.put("data", formMap);
		map.put("success", true);
		return map;
	}

}
