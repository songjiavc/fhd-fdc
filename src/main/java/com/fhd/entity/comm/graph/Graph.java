/**
 * Graph.java
 * com.fhd.entity.comm.graph
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2013-10-28 		张 雷
 *
 * Copyright (c) 2013, Firsthuida All Rights Reserved.
*/

package com.fhd.entity.comm.graph;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fhd.entity.base.AuditableEntity;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * @author   张 雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2013-10-28		上午10:48:41
 *
 * @see 	 
 */
@Entity
@Table(name = "T_COM_GRAPH")
public class Graph extends AuditableEntity{

	private static final long serialVersionUID = 2397429815610260942L;
    
	 /**
     * 公司编号
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private SysOrganization company;
    
	/**
     * 名称
     */
    @Column(name = "NAME")
    private String name;
    
    /** 
	* 内容
	*/ 
	@Lob
	@Column(name = "CONTENT" , nullable = true)
	private byte[] content;
	
	@OneToMany(mappedBy = "graph",orphanRemoval=true) 
	private Set<GraphRelaProcess> graphRelaProcesses;


	public Graph() {
	}
	
	public Graph(String id) {
		super.setId(id);
	}

	public SysOrganization getCompany() {
		return company;
	}

	public void setCompany(SysOrganization company) {
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "CONTENT", columnDefinition = "BLOB",nullable=true)
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public Set<GraphRelaProcess> getGraphRelaProcesses() {
		return graphRelaProcesses;
	}

	public void setGraphRelaProcesses(Set<GraphRelaProcess> graphRelaProcesses) {
		this.graphRelaProcesses = graphRelaProcesses;
	}
	
}

