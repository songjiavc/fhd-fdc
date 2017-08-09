package com.fhd.entity.sys.templatemanage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.dic.DictEntry;
/**
 * 
 * @功能 : 模板管理实体
 * @author 王再冉
 * @date 2013-12-24
 * @since Ver
 * @copyRight FHD
 */
@Entity
@Table(name = "T_SYS_TEMPLATE_MANAGE")
public class TemplateManage extends IdEntity implements Serializable{
	private static final long serialVersionUID = -2755889997216612327L;

	/**
	 * 模版类型
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DICT_ENTRY_ID")
	private DictEntry dictEntry;
	
	/**
	 * 
	 */
	@Column(name = "TEMPLATE_NAME")
	private String name;
	
	/**
	 * 模版内容
	 */
	@Column(name = "TEMPLATE_CONTENT")
	private String content;
	
	/**
	 * 模版删除状态
	 */
	@Column(name = "DELETE_STATUS")
	private String status;

	public DictEntry getDictEntry() {
		return dictEntry;
	}

	public void setDictEntry(DictEntry dictEntry) {
		this.dictEntry = dictEntry;
	}
	
	/**
	 * 是否默认：1，默认 0：非默认
	 */
	@Column(name = "IS_DEFAULT")
	private String isDefault;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	
	
	
}
