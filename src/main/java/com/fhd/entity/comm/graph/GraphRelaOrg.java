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
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-10-28		上午11:10:24
 *
 * @see 	 
 */
@Entity
@Table(name = "T_COM_GRAPH_RELA_ORG")
public class GraphRelaOrg extends IdEntity {
	
	private static final long serialVersionUID = 7399438698802570600L;

	/**
     * 图形
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRAPH_ID")
	private Graph graph;
    
    /**
     * 部门
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    private SysOrganization org;
	
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

	public SysOrganization getOrg() {
		return org;
	}

	public void setOrg(SysOrganization org) {
		this.org = org;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
}

