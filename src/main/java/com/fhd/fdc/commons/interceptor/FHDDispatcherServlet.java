/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fhd.fdc.commons.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

/**
 * 记录后台url执行时间.
 * @author 吴德福
 * @Date 2014-1-8 16:56:03
 */
@SuppressWarnings("serial")
public class FHDDispatcherServlet extends DispatcherServlet {
	
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		long startTime = System.currentTimeMillis();
		
		super.doService(request, response);
			
		long endTime = System.currentTimeMillis();
		
		String requestUrl = request.getRequestURL().toString();
		if(this.isMatches(requestUrl)){
			logger.debug("request url '" + requestUrl + "' time is :" + (endTime - startTime) + " ms");
		}
	}

	/**
	 * 正则表达式判断url以.do或.f结尾.
	 * @author 吴德福
	 * @param url
	 * @return boolean
	 */
	private boolean isMatches(String url){
		boolean flag = false;
		if(url.matches("^/(.*\\.do)|(.*\\.f)$")){
			flag = true;
		}
		return flag;
	}
}