/**
 * GraphRelaStrategyMap.java
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
import com.fhd.entity.kpi.StrategyMap;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-10-28		上午11:46:38
 *
 * @see 	 
 */
@Entity
@Table(name = "T_COM_GRAPH_RELA_STRATEGY_MAP")
public class GraphRelaStrategyMap extends IdEntity {

	private static final long serialVersionUID = -2598544323234456454L;

	/**
     * 图形
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRAPH_ID")
	private Graph graph;
    
    /**
     * 目标
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STRATEGY_MAP_ID")
	private StrategyMap strategyMap;
    
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

	public StrategyMap getStrategyMap() {
		return strategyMap;
	}

	public void setStrategyMap(StrategyMap strategyMap) {
		this.strategyMap = strategyMap;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
    

}

