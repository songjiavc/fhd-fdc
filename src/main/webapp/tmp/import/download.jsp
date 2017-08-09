<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="org.apache.commons.io.IOUtils"%>
<%@page import="java.io.File"%>
<%@page import="java.io.OutputStream"%>
<%@ page language="java" contentType="text/plain; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

	String path = request.getParameter("path");
	String fullpath = request.getParameter("fullpath");
	
	
	File file = null;
	if(StringUtils.isNotEmpty(path)) {
		file = new File(request.getRealPath(path));
		response.setHeader("Content-Disposition","attachment;filename = " + URLEncoder.encode(StringUtils.substringAfterLast(path, "/"), "UTF-8") );
	} else if(StringUtils.isNotEmpty(fullpath)) {
		file = new File(fullpath);
		response.setHeader("Content-Disposition","attachment;filename = " + URLEncoder.encode(StringUtils.substringAfterLast(fullpath, "/"), "UTF-8") );
	}
	out.clear();
	out = pageContext.pushBody();
	OutputStream os = response.getOutputStream();
	IOUtils.write(FileUtils.readFileToByteArray(file), os);
	os.flush();
	os.close();  
%>
