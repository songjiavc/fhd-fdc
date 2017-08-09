<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title></title>
</head>
<body>
	<%
	    String ip = request.getHeader("x-forwarded-for"); 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("Proxy-Client-IP"); 
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("WL-Proxy-Client-IP"); 
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getRemoteAddr(); 
	    }
	%>
	<!-- <form name="loginForm" action="${ctx}/<c:url value='j_spring_security_check'/>" method="POST">-->
	<form action="${ctx}/j_spring_security_check" method="POST">
		<input type="hidden" name="j_ip" id="ip" value="<%=ip%>" />
		<input type="hidden" value="${username}"  name="j_username" id="username"/>
		<input type="hidden" value="${password}"  name="j_password" id="password"/>
		<input type="hidden" value="sso"  name="j_sso" id="sso"/>
	</form>
	<script type="text/javascript">
		document.forms[0].submit();
	</script>
</body>
</html>