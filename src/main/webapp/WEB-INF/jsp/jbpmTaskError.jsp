<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<title></title>
	<link rel="icon" href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/favicon.ico" type="image/x-icon" />  
	
	<!-- 主界面CSS -->
<%--<link rel="stylesheet" type="text/css" href="<c:url value='/scripts/ext-4.2/resources/css/ext-all-neptune.css'/>"/> --%>
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/FHDstyle.css'/>"/>
	<!-- Shared -->
	<!-- ext4默认样式CSS-->
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/icon.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/w.css'/>" />
	
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/xbreadcrumbs.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/monitor.css'/>" />
	 
	<link rel="shortcut icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
	<link rel="icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
	
	<script type="text/javascript" src="<c:url value='/scripts/jquery-1.10.2.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/scripts/fhd.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/include-ext.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/scripts/commons/dynamic.jsp'/>"></script>
	
	<script type="text/javascript" src="<c:url value='/i18n'/>"></script>
	<script type="text/javascript" src="<c:url value='/scripts/locale.js'/>"></script>
	
	<script type="text/javascript" src="<c:url value='/UserAuth'/>"></script>
	<script type="text/javascript" src="<c:url value='/scripts/authority.js'/>"></script>
	
    <script type="text/javascript">
	    Ext.onReady(function () {
	    	Ext.QuickTips.init();
	    	
	    	Ext.Loader.setConfig({
	    		enabled : true
	    	});
	    	Ext.Loader.setPath({
	    		'Ext.ux' : '${ctx}/scripts/ext-4.2/ux',
	    		'Ext.app' : '${ctx}/scripts/ext-4.2/app',
	    		'FHD.ux' : '${ctx}/scripts/component',
	    		'FHD.view' : '${ctx}/app/view'
	    	});
	    	
	    	var errorMsg = '${errorMsg}';
	    	
	    	var errorTipPanel = Ext.create('Ext.panel.Panel',{
	    		region : 'center',
				html: errorMsg
			}); 
	    	
	    	Ext.create('Ext.container.Viewport', {
	    		layout: {
	    	        type: 'border',
	    	        padding: '3 3 3 3'
	    	    },
	    	    border:true,
	            items:[errorTipPanel]
	        });
       	});
   	</script>
</head>
<body>
</body>
</html>