/**
 * 
 */
package com.fhd.fdc.commons.listener;

import javax.servlet.ServletRequestEvent;

import org.springframework.web.context.request.RequestContextListener;

/**
 * @author vincent
 *
 */
public class ErmisRequestContextListener extends RequestContextListener {

	
	@Override
	public void requestInitialized(ServletRequestEvent requestEvent) {
		super.requestInitialized(requestEvent);
		
		// 检查许可证
		//verifyLicense(requestEvent);
				
	}
	
	
	
	
	
}
