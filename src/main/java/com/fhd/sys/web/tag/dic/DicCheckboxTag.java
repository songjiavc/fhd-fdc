/**
 * DicCheckboxTag.java
 * com.fhd.sys.web.tag.dic
 *
 * Function : ENDO
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2012-2-21 		胡迪新
 *
 * Copyright (c) 2012, Firsthuida All Rights Reserved.
*/

package com.fhd.sys.web.tag.dic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fhd.core.web.tag.AbstractTagSupport;
import com.fhd.sys.business.dic.OldDictEntryBO;
import com.fhd.entity.sys.dic.DictEntry;

/**
 * ClassName:DicCheckboxTag
 * Function: ADD FUNCTION
 * Reason:	 ENDO ADD REASON
 *
 * @author   胡迪新
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-2-21		下午03:57:40
 *
 * @see 	 
 */
public class DicCheckboxTag extends AbstractTagSupport {

	/**
	 * serialVersionUID:ENDO
	 *
	 * @author 胡迪新
	 * @since  fhd　Ver 1.1
	 */
	private static final long serialVersionUID = 4852069819856293886L;

	
	private String dicTypeId;
	
	private Boolean disabled;
	
	private Boolean filter = Boolean.FALSE;
	
	private String filterOp;
	
	private String filterStr;
	
	private String id;
	
	private String name;
	
	private Integer perrow;
	
	/**
	 * 默认选中
	 */
	private String property;


	
	@Override
	public int doEndTag() throws JspException {
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
		OldDictEntryBO dictEntryBean = (OldDictEntryBO) webApplicationContext.getBean("oldDictEntryBO");
		String[] checkeds = StringUtils.split(property, ",");
		List<DictEntry> dictEntries = dictEntryBean.queryEntryByDictTypeId(dicTypeId, filter, filterOp, filterStr);
		VelocityEngine ve = new VelocityEngine();
		JspWriter out = pageContext.getOut();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
				    ((DicCheckboxTag.class.getResourceAsStream("/component_vm/DicCheckboxTag.vm")))));
			VelocityContext context = new VelocityContext();
			context.put("dictEntries", dictEntries);
			context.put("checkeds", checkeds);
			context.put("id", id);
			context.put("name", name);
			context.put("property", property);
			context.put("disabled", disabled);
			context.put("perrow", perrow);
			context.put("style", getStyle());
			context.put("styleCss", getStyleCss());
			context.put("onblur", getOnblur());
			context.put("onchange", getOnchange());
			context.put("onclick", getOnclick());
			context.put("ondblclick", getOndblclick());
			context.put("onfocus", getOnfocus());
			context.put("onkeydown", getOnkeydown());
			context.put("onkeypress", getOnkeypress());
			context.put("onkeyup", getOnkeyup());
			context.put("onmousedown", getOnmousedown());
			context.put("onmousemove", getOnmousemove());
			context.put("onmouseout", getOnmouseout());
			context.put("onmouseover", getOnmouseover());
			context.put("onmouseup", getOnmouseup());
			context.put("onselect", getOnselect());
			StringWriter writer = new StringWriter();
			ve.evaluate(context, writer, "", br);
			out.write(writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return EVAL_BODY_INCLUDE;
		
	}
	
	
	public String getDicTypeId() {
		return dicTypeId;
	}

	public void setDicTypeId(String dicTypeId) {
		this.dicTypeId = dicTypeId;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getFilter() {
		return filter;
	}

	public void setFilter(Boolean filter) {
		this.filter = filter;
	}

	public String getFilterOp() {
		return filterOp;
	}

	public void setFilterOp(String filterOp) {
		this.filterOp = filterOp;
	}

	public String getFilterStr() {
		return filterStr;
	}

	public void setFilterStr(String filterStr) {
		this.filterStr = filterStr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPerrow() {
		return perrow;
	}

	public void setPerrow(Integer perrow) {
		this.perrow = perrow;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	
	
}

