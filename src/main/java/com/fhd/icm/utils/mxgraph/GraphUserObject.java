/**
 * GraphUserObject.java
 * com.fhd.icm.utils.mxgraph
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013年12月17日 		张 雷
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.icm.utils.mxgraph;
/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013年12月17日		下午2:41:45
 *
 * @see 	 
 */
public class GraphUserObject extends GraphCell {
	/**
	 * 标签
	 */
	private String label;
	/**
	 * url连接
	 */
	private String link;
	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	
}

