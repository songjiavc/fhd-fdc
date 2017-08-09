package com.fhd.sys.web.controller.dic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.entity.sys.dic.DictType;
import com.fhd.sys.business.dic.DictTypeTreeBO;


/**
 * 
 * ClassName:DictTypeTreeControl
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-8		下午3:46:34
 *
 * @see
 */
@Controller
public class DictTypeTreeControl {
    @Autowired
    private DictTypeTreeBO o_dictTypeTreeBO;
    
	/**
	 * 
	 * getTreeRoot:得到根节点
	 * 
	 * @author 杨鹏
	 * @return
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
    @RequestMapping("/sys/dict/dictType/getTreeRoot.f")
    public Map<String, String> getTreeRoot(){
    	DictType dictType = o_dictTypeTreeBO.getTreeRoot();
        Map<String, String> nodeMap = dictTypeToNodeMap(dictType);
        return nodeMap;
    }
    /**
     * 
     * treeLoader:
     * 
     * @author 杨鹏
     * @param query
     * @param node
     * @return
     * @since  fhd　Ver 1.1
     */
	@ResponseBody
	@RequestMapping("/sys/dict/dictType/treeLoader.f")
	public List<Map<String, String>> treeLoader(String query,String node){
		List<DictType> dictTypes = o_dictTypeTreeBO.treeLoader(query, node, "sort", "ASC");
		List<Map<String,String>> nodes = new ArrayList<Map<String,String>>();
		for (DictType dictType : dictTypes) {
			Map<String, String> map=dictTypeToNodeMap(dictType);
			nodes.add(map);
		}
	    return nodes;
	}
    
    /**
     * 
     * dictToNodeMap:
     * 
     * @author 杨鹏
     * @param dictType
     * @return
     * @since  fhd　Ver 1.1
     */
    private Map<String, String> dictTypeToNodeMap(DictType dictType) {
		Map<String, String> node=new HashMap<String, String>();
		String id = dictType.getId();
		String name = dictType.getName();
		Boolean isLeaf = dictType.getIsLeaf();
        node.put("id",id);
        node.put("text", name);
        if(isLeaf!=null){
        	node.put("leaf", isLeaf.toString());
        }
        return node;
	}
}