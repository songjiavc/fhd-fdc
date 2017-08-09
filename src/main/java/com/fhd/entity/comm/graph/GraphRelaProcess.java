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

import com.fhd.entity.process.Process;

import com.fhd.entity.base.IdEntity;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-10-28		上午11:10:24
 *
 * @see 	 
 */
@Entity
@Table(name = "T_COM_GRAPH_RELA_PROCESS")
public class GraphRelaProcess extends IdEntity {
	
	private static final long serialVersionUID = 8638651085497195808L;

	/**
     * 图形
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRAPH_ID")
	private Graph graph;
    
    /**
	 * 流程
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PROCESS_ID")
	private Process process;
    
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

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
}

