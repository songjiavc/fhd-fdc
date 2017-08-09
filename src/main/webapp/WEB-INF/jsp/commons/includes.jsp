<%@ page errorPage="/WEB-INF/jsp/commons/errorpage.jsp"  pageEncoding="UTF-8"%>
<%@ page buffer="48kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@ taglib uri="fhd-tag" prefix="fhd" %>
<%@ taglib uri="fhd-tag-core" prefix="fhdcore" %>
<%@ taglib uri="fhd-dic-tag" prefix="d" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" /> 
<c:set var="req" value="${pageContext.request}" /> 

<link rel="stylesheet" id="fhd" type="text/css" href="${ctx}/css/FHDstyle.css" />
<link rel="stylesheet" id="fhd" type="text/css" href="${ctx}/css/icon.css" />


<script type="text/javascript" src="${ctx}/scripts/fhd.js"></script>
<script src="${ctx}/scripts/jquery-1.10.2.min.js" type="text/javascript"></script>
<script src="${ctx}/scripts/jquery.validate.js" type="text/javascript"></script>
<script src="${ctx}/scripts/jquery.validate.method.js" type="text/javascript"></script>
<script src="${ctx}/scripts/jquery.form.js" type="text/javascript"></script>
<script src="${ctx}/scripts/messages_cn.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctx}/scripts/ext-4.2/include-ext.js"></script>


<script>
	var contextPath = "${ctx}";	
	var contextHttp = "${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/";
	
	
	
	/**
	 *	在其他iframe页面中点击隐藏菜单
	 *
	 */
	$(document).click(function(event){
		try{//altered by David on 2012-02-23
			Ext.each(top.menu.items.items,function(m){
				if(m.text){
					m.menu.hide();
				}
			});
		}catch(err){}
	});
	// 初始化提示
	Ext.QuickTips.init();
</script>


<style type="text/css">
.x-grid3-row-over .x-grid3-cell-inner {
    font-weight: bold;
}
.icon-expand-all {
    background-image: url(${ctx}/images/expand-all.gif) !important;
}
.icon-collapse-all {
    background-image: url(${ctx}/images/collapse-all.gif) !important;
}

body {
	overflow-y: auto;
}
</style>
