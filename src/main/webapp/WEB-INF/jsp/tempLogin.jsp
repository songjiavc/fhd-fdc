<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title> </title>
</head>
<body onload="login()">
	<script type="text/javascript">
	function login(){
		form.submit();
	}
	</script>
	<form action="<c:url value='j_spring_security_check'/>" method="POST"  id="form">
		<input type="hidden" name="j_username" id="u" value="${userName}" />
		<input type="hidden" name="j_password" id="p" value="${passWord}" />
<!-- 		<input type="submit" name="button" value="&nbsp;&nbsp;登&nbsp;录" /> -->
	</form>
</body>
</html>