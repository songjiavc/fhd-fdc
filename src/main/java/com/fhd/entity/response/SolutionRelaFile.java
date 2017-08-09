package com.fhd.entity.response;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.file.FileUploadEntity;

/**
 * 流程关联附件实体
 * @author   张  雷
 * @version  
 * @since    Ver 1.1
 * @Date	 2012-12-19		上午11:44:27
 *
 * @see 	 
 */
@Entity
@Table(name="T_RM_SOLUTION_RELA_FILE")
public class SolutionRelaFile extends IdEntity implements Serializable {
	private static final long serialVersionUID = 8646266612406363553L;

	/**
	 * 应对措施
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOLUTION_ID")
	private Solution solution;
	
	/**
	 * 附件
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FILE_ID")
	private FileUploadEntity file;
	
	
	public SolutionRelaFile(){
		
	}
	
	public SolutionRelaFile(String id){
		super.setId(id);
	}
	
	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	public FileUploadEntity getFile() {
		return file;
	}

	public void setFile(FileUploadEntity file) {
		this.file = file;
	}
	
	
}


