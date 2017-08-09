package com.fhd.entity.risk;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.file.FileUploadEntity;

/**
 * 报告关联附件
 * 
 * @author 张健
 * @date 2014-1-3
 * @since Ver 1.1
 */
@Entity
@Table(name = "T_MANAGE_REPORT_RELA_FILE")
public class ManageReportRelaFile extends IdEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 文档库
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORT_ID")
    private ManageReport report;
    
    /**
     * 附件
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileUploadEntity file;
    
    public ManageReport getReport() {
        return report;
    }

    public void setReport(ManageReport report) {
        this.report = report;
    }

    public FileUploadEntity getFile() {
        return file;
    }

    public void setFile(FileUploadEntity file) {
        this.file = file;
    }
    
}
