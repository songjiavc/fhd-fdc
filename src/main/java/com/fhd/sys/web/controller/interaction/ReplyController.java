package com.fhd.sys.web.controller.interaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.core.utils.Identities;
import com.fhd.entity.sys.interaction.Posts;
import com.fhd.entity.sys.interaction.Reply;
import com.fhd.fdc.utils.UserContext;
import com.fhd.sys.business.interaction.ReplyBO;

/**
 * 回复控制
 * */

@Controller
public class ReplyController {
	@Autowired
	private ReplyBO o_replyBO;
	
	/**
	 * 根据帖子id查询回复列表
	 * add by 王再冉
	 * 2014-3-11  下午4:03:30
	 * desc : 
	 * @param postsId	帖子id
	 * @return 
	 * List<Map<String,Object>>
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/interaction/findreplysgridbypostsid.f")
	public List<Map<String,Object>> findReplysGridByPostsId(String postsId){
		List<Map<String,Object>> listmap = new ArrayList<Map<String,Object>>();
		List<Reply> replyList = o_replyBO.findPostsByCompanyId(postsId);
		for(Reply reply : replyList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("replyId", reply.getId());
			map.put("createEmpName", reply.getCreateEmp().getEmpname());//回复人
			map.put("content", reply.getContent());//回复内容
			map.put("createTime", reply.getCreateTime().toString());//回复时间
			listmap.add(map);
		}
		return listmap;
	}
	
	/**
	 * 保存回复
	 * add by 王再冉
	 * 2014-3-11  下午4:24:29
	 * desc : 
	 * @param postsId		帖子id
	 * @param countentText	回复内容
	 * @return 
	 * Boolean
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/interaction/savereplybypostsid")
	public Boolean saveReplyByPostsId(String postsId, String countentText){
		if(StringUtils.isNotBlank(postsId)){
			Reply reply = new Reply();
			reply.setId(Identities.uuid());
			reply.setPosts(new Posts(postsId));//回复对应的帖子
			reply.setContent(countentText);//回复内容
			reply.setCreateTime(new Date());//回复时间
			reply.setCreateEmp(UserContext.getUser().getEmp());//回复人
			o_replyBO.saveReply(reply);
			return true;
		}else{
			return false;
		}
	}

}
