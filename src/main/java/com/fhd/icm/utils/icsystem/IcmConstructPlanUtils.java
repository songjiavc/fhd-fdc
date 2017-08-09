/**
 * IcmStandardUtils.java
 * com.fhd.icm.utils
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-12-29 		刘中帅
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.icm.utils.icsystem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author   宋佳
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-4-2  
 *
 * @see 	 
 */
public class IcmConstructPlanUtils {
    
	/**
	 *    记录保存和执行状态的代码表
	 */
    public static final Map<String, String> STATUS_UTILS = new HashMap<String, String>();
    static
    {
    	STATUS_UTILS.put("S", "已保存");
    	STATUS_UTILS.put("P", "已提交");
    	STATUS_UTILS.put("N", "未开始");
    	STATUS_UTILS.put("H", "处理中");
    	STATUS_UTILS.put("F", "已完成");
    }
}

