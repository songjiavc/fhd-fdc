package com.fhd.entity.bpm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.file.FileUploadEntity;

/**
 * 
 * ClassName:ProcessDefinitionDeploy:流程定义
 *
 * @author   杨鹏
 * @version  
 * @since    Ver 1.1
 * @Date	 2013	2013-6-26		上午10:46:33
 *
 * @see
 */
@Entity
@Table(name = "T_JBPM_PROCESS_DEFINE_DEPLOY")
public class ProcessDefinitionDeploy extends IdEntity implements Serializable {
    private static final long serialVersionUID = -7130452106622848250L;

    /**
     *  流程定义显示名称
     */
    @Column(name = "DIS_NAME")
    private String disName;

    /**
     *  业务查看路径
     */
    @Column(name = "url")
    private String url;

    /**
     *  流程定义文件
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private FileUploadEntity fileUploadEntity;

    /**
     * 是否可用：1、可用。2、已删除
     */
    @Column(name = "DELETE_STATUS")
    private String deleteStatus;

    /**
     * 发布时间
     */
    @Column(name = "CREATE_DATE")
    private Date createDate;

    /**
     * 默认构造函数
     */
    public ProcessDefinitionDeploy() {
        super();
    }

    /**带参数构造函数
     * @param disName 流程定义显示名称
     * @param url 业务查看路径
     * @param fileUploadEntity 流程定义文件
     * @param deleteStatus 是否可用
     * @param createDate 发布时间
     */
    public ProcessDefinitionDeploy(String disName, String url, FileUploadEntity fileUploadEntity, String deleteStatus, Date createDate) {
        this.disName = disName;
        this.url = url;
        this.fileUploadEntity = fileUploadEntity;
        this.deleteStatus = deleteStatus;
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FileUploadEntity getFileUploadEntity() {
        return fileUploadEntity;
    }

    public void setFileUploadEntity(FileUploadEntity fileUploadEntity) {
        this.fileUploadEntity = fileUploadEntity;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

}
