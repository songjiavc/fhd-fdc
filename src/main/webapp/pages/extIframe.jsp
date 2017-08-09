<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>第一会达风险管理平台</title>

		<!-- 主界面CSS -->
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
		<script type="text/javascript" src="<c:url value='/scripts/Highstock-1.3.2/js/highstock.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/Highstock-1.3.2/js/highcharts-more.js'/>"></script>
		
		
		<script type="text/javascript" src="<c:url value='/scripts/xbreadcrumbs.js'/>"></script>
		
		
		<script type="text/javascript" src="<c:url value='/scripts/fhd.js'/>"></script>
		
		<!-- ext4核心JS -->
		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/include-ext.js'/>"></script>
		<!-- <script type="text/javascript" src="<c:url value='/scripts/ext-4.2/options-toolbar.js'/>"></script> -->
		
		<!-- ext4中文支持JS -->
		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/locale/ext-lang-zh_CN.js'/>"></script>
		
		<script type="text/javascript" src="<c:url value='/scripts/kindeditor-4.1.1/kindeditor.js'/>"></script>
		
		<!-- 公用JS -->
		<script type="text/javascript" src="<c:url value='/app/view/kpi/cmp/kpi/result/ResultParam.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/app/view/kpi/kpi.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/commons/dynamic.jsp'/>?"+Math.random();></script>
		<!-- 本地 国际化资源 -->
		<script type="text/javascript">
			${locale}
		</script>
		
		<script type="text/javascript" src="<c:url value='/scripts/locale.js'/>"></script>
		
		<script type="text/javascript" src="<c:url value='/UserAuth'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/authority.js'/>"></script>
		<script type="text/javascript">
		
		Ext.Loader.setConfig({
			enabled : true
		});
		
		Ext.Loader.setPath({
			'Ext.ux' : 'scripts/ext-4.2/ux',
			'Ext.app' : 'scripts/ext-4.2/app',
			'FHD.ux' : 'scripts/component',
			'FHD.view' : 'app/view',
			'FHD.demo' : 'pages/demo'
			
		});
		
		Ext.onReady(function(){
			var url = '${param.url}';
			var typeId = '${param.typeId}';
			if(Ext.isIE6){
				Ext.BLANK_IMAGE_URL = __ctxPath +"/images/s.gif";
			}
	   		Ext.tip.QuickTipManager.init();
	   		Ext.apply(Ext.tip.QuickTipManager.getQuickTip(), {
	   		    showDelay: 500      // Show 50ms after entering target
	   		});
	   		
	   		Ext.create('Ext.container.Viewport',{
	   			id:'ermis-viewport',
	   			layout: 'fit',
	   		    border: false,
	   			items : Ext.create(url,{
	   				itemId:url,
	   				typeId: typeId
	   			})
	   		});
		});
		</script>
</head>
<body>
	
</body>

<script type="text/javascript" src="<c:url value='/scripts/chart/FusionCharts.js'/>" ></script>
	<script type="text/javascript" src="${ctx}/scripts/component/meshStructureChart/meshStructureChart.js"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value='/scripts/component/meshStructureChart/meshStructureChart.css'/>" />
	
	<script type="text/javascript">
  		mxBasePath = '<c:url value='/scripts/mxgraph-1.10/'/>';
 	</script>
	<script type="text/javascript" src="<c:url value='/scripts/mxgraph-1.10/js/mxgraph.js'/>"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value='/scripts/component/treeChar/treeChar.css'/>" />
	
	<script type="text/javascript" src="<c:url value='/scripts/FHDDebugTool.js'/>" ></script>
	<script type="text/javascript" src="<c:url value='/scripts/FHDException.js'/>" ></script>
</html>