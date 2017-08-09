<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript">

/***Ext.onReady start***/
Ext.onReady(function(){
	/***dimensionGrid start***/
	holddefectGrid = Ext.create('pages.risk.baseConfig.DefectChangeRiskList',{//创建可编辑的grid列表，注释同不可编辑的gird
		renderTo: 'holddefectDIV${param._dc}'
	});
	
});
/***Ext.onReady end***/
</script>
</head>
<body>
<div>
	<div id='holddefectDIV${param._dc}'</div>
	</div>
</body>
</html>