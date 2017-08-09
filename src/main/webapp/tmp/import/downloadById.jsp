<%@page import="javax.servlet.ServletOutputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="com.fhd.entity.sys.file.FileUploadEntity"%>
<%@page import="org.springframework.util.FileCopyUtils"%>
<%@page import="com.fhd.sys.business.file.FileUploadBO"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.ContextLoader"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%

	WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
	FileUploadBO o_fileUploadBO = applicationContext.getBean(FileUploadBO.class);
	String id = request.getParameter("id");
    ServletOutputStream outputStream = null;
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    try {
        FileUploadEntity fileUpload = o_fileUploadBO.queryFileById(id);
        outputStream = response.getOutputStream();
        response.setContentType(fileUpload.getFileType().getName());
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUpload.getOldFileName(), "UTF-8"));
        out.clear();  
        out = pageContext.pushBody(); 
        if (!"".equals(fileUpload.getFileAddress()) && null != fileUpload.getFileAddress()) {
            String path = fileUpload.getFileAddress();
            // 获取欲下载的文件输入流
            fis = new FileInputStream(path);
            bis = new BufferedInputStream(fis);
            FileCopyUtils.copy(bis, outputStream);
        }
        else {
            // 获取数据库中blob数据
            byte[] bytes = fileUpload.getContents();
            // 获取欲下载的文件输入流
            FileCopyUtils.copy(bytes, outputStream);
        }
        fileUpload.setCountNum(fileUpload.getCountNum() + 1);
        o_fileUploadBO.updateFile(fileUpload);
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    finally {
        if (null != bis) {
            bis.close();
        }
        if (null != fis) {
            fis.close();
        }
        if (null != outputStream) {
            outputStream.flush();
            outputStream.close();
        }
    }
%>
