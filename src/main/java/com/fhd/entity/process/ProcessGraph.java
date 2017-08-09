/**   
* @Title: Flowchart.java 
* @Package com.fhd.fdc.commons.entity.processure 
* @Description: (用一句话描述该文件做什么) 
* @author 张雷 
* @date 2011-4-12 下午04:19:28 
* @version v1.0 
*/ 
package com.fhd.entity.process;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;

/**
 * 流程图
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-9-11		上午9:40:27
 *
 * @see 	 
 */
@Entity
@Table(name="T_PROCESSURE_GRAPH")
public class ProcessGraph extends IdEntity implements Serializable{

	
	private static final long serialVersionUID = -3159487291801525252L;

	/** 
	* @Fields processId : 流程ID
	*/ 
	@Column(name="PROCESSURE_ID")
	private String processId;
	
	/** 
	* @Fields flowchartName : 流程设计图的名字
	*/ 
	@Column(name="GRAPH_NAME")
	private String graphName;
	
	
	/** 
	* @Fields flowchartContext : 流程设计视图的内容
	*/ 
	@Lob
	@Column(name = "GRAPH_CONTEXT" , nullable = true)
	private byte[] graphContext;


	public String getProcessId() {
		return processId;
	}


	public void setProcessId(String processId) {
		this.processId = processId;
	}


	public String getGraphName() {
		return graphName;
	}


	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "GRAPH_CONTEXT", columnDefinition = "BLOB",nullable=true)
	public byte[] getGraphContext() {
		return graphContext;
	}

	
	public void setGraphContext(byte[] graphContext) {
		this.graphContext = graphContext;
	}

	
}
