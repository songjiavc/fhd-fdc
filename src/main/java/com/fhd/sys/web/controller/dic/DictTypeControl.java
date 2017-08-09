package com.fhd.sys.web.controller.dic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.sys.business.dic.DictTypeBO;


/**
 * 
 * ClassName:DictTypeControl
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-10-9		上午9:45:12
 *
 * @see
 */
@Controller
public class DictTypeControl {
    @Autowired
    private DictTypeBO o_dictTypeBO;
    
	/**
	 * 
	 * removeById:根据ID删除
	 * 
	 * @author 杨鹏
	 * @param id
	 * @since  fhd　Ver 1.1
	 */
    @ResponseBody
	@RequestMapping(value = "/sys/dict/dictType/removeById.f")
	public void removeById(String id){
    	o_dictTypeBO.removeById(id);
	}
}