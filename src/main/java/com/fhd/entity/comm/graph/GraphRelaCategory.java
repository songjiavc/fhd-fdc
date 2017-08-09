/**
 * GraphRelaRisk.java
 * com.fhd.entity.comm.graph
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-10-28 		张 雷
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.entity.comm.graph;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.comm.Category;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-10-28		上午11:10:24
 *
 * @see 	 
 */
@Entity
@Table(name = "T_COM_GRAPH_RELA_CATEGORY")
public class GraphRelaCategory extends IdEntity {
	private static final long serialVersionUID = 4708325000271169104L;

	/**
     * 图形
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRAPH_ID")
	private Graph graph;
    
    /**
	 * 记分卡
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;
    
    /**
     * 类型：创建，调用
     */
    @Column(name = "ETYPE", length = 100)
    private String type;

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
}

