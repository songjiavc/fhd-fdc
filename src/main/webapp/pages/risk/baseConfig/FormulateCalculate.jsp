<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript">
Ext.onReady(function(){
	var formulateCalculateEdit = Ext.create('FHD.view.risk.assess.quaAssess.FormulateCalculateEdit',{
		height: baseConfigMainPanel.getHeight(),
		width: baseConfigMainPanel.getWidth()-230
	});
	
	var calculateMain = Ext.create("Ext.panel.Panel",{
		renderTo: 'calculateDIV',
		autoScroll: true,
        border: false,
        bodyPadding: "5 5 5 5",
    	layout: {
    		align: 'stretch',
            type: 'hbox'
        },
        items: [formulateCalculateEdit]
	}); 
	
	//设置自适应窗口
	baseConfigTree.on('collapse',function(p){
		formulateCalculateEdit.setWidth(baseConfigMainPanel.getWidth()-50);
	});
	baseConfigTree.on('expand',function(p){
		formulateCalculateEdit.setWidth((baseConfigMainPanel.getWidth())-230);
	});
	baseConfigMainPanel.on('resize',function(p){
		formulateCalculateEdit.setHeight(baseConfigMainPanel.getHeight());
		if(baseConfigTree.collapsed){
			formulateCalculateEdit.setWidth(baseConfigMainPanel.getWidth()-200);
		}else{
			formulateCalculateEdit.setWidth(baseConfigMainPanel.getWidth()-230);
		}
	});
	baseConfigTree.on('resize',function(p){
		if(p.collapsed){
			formulateCalculateEdit.setWidth((baseConfigMainPanel.getWidth()-26)-5);
		}else{
			formulateCalculateEdit.setWidth((baseConfigMainPanel.getWidth()-p.getWidth())-5);
		}
	}); 
	
});
/***Ext.onReady end***/
</script>
</head>
<body>
	<div>
	<div id='calculateDIV' style="height:50%"></div>
	</div>
</body>
</html>