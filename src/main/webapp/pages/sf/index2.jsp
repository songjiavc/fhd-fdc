<%@ page language="java" pageEncoding="utf-8"%> 
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>
<html>
<head>
<base href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/">
		<title>沈阳飞机工业（集团）有限公司</title>
		<script type="text/javascript">
		
			var panelHeigt = Ext.getCmp('center-panel').body.dom.clientHeight;
			Ext.create('Ext.panel.Panel',{
				layout:'fit',
				height:panelHeigt,
				html : '<iframe width="100%" height="100%" frameborder="0" src="${ctx}/pages/sf/iframe2.jsp"></iframe>',
				renderTo:'sfhome2'
				
			});	
		</script>
	</head>
	<body>
	 <div id="sfhome2"></div>
	</body>
</html>
