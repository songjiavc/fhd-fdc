<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript">
Ext.onReady(function(){
	
	var departmentGrid = Ext.create('FHD.view.risk.assess.DepartmentGrid',{
		height: baseConfigMainPanel.getHeight()/3-50,
		width: baseConfigMainPanel.getWidth()-270
	});
	var roleGrid = Ext.create('FHD.view.risk.assess.RoleGrid',{
		height: baseConfigMainPanel.getHeight()/3-50,
		width: baseConfigMainPanel.getWidth()-270
	});
	var weightAlarmPlanPanel = Ext.create('FHD.view.risk.assess.WeightAlarmPlanPanel',{
		height: baseConfigMainPanel.getHeight()/3-100,
		width: baseConfigMainPanel.getWidth()-170
	});
	/* var rightWeightsetPanel = Ext.create('FHD.view.risk.assess.RightWeightsetPanel',{
		height: baseConfigMainPanel.getHeight()-50,
		width: baseConfigMainPanel.getWidth()/2-170
	});
	 */
	var departmentGridFieldSet = {
  			xtype : 'fieldset',
  			title : '部门权重设定方案',
  			margin : '5 5 0 5',
  			//width : 700,
  			items : [ departmentGrid ]
  		};
	var roleGridFieldSet = {
  			xtype : 'fieldset',
  			title : '职务',
  			margin : '5 5 0 5',
  			items : [roleGrid]
  		};
	var alarmPlanFieldSet = {
  			xtype : 'fieldset',
  			title : '告警方案',
  			margin : '5 5 5 5',
  			items : [weightAlarmPlanPanel]
  		};
	var weightMain = Ext.create("Ext.panel.Panel",{
		renderTo: 'weightingsetDIV',
		autoScroll: true,
        border: false,
        bodyPadding: "5 5 0 5",
    	layout: {
    		align: 'stretch',
            type: 'vbox'
        },
        items: [departmentGridFieldSet, roleGridFieldSet, alarmPlanFieldSet]
	}); 
	
	//设置自适应窗口
	baseConfigTree.on('collapse',function(p){
		departmentGrid.setWidth(baseConfigMainPanel.getWidth()-90);
		roleGrid.setWidth(baseConfigMainPanel.getWidth()-90);
		weightAlarmPlanPanel.setWidth(baseConfigMainPanel.getWidth()-90);
	});
	baseConfigTree.on('expand',function(p){
		departmentGrid.setWidth(baseConfigMainPanel.getWidth()-270);
		roleGrid.setWidth(baseConfigMainPanel.getWidth()-270);
		weightAlarmPlanPanel.setWidth(baseConfigMainPanel.getWidth()-270);
	});
	baseConfigMainPanel.on('resize',function(p){
		departmentGrid.setHeight(baseConfigMainPanel.getHeight()/3-50);
		roleGrid.setHeight(baseConfigMainPanel.getHeight()/3-50);
		weightAlarmPlanPanel.setHeight(baseConfigMainPanel.getHeight()/3-100);
		if(baseConfigTree.collapsed){
			departmentGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			roleGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			weightAlarmPlanPanel.setWidth(baseConfigMainPanel.getWidth()-270);
		}else{
			departmentGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			roleGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			weightAlarmPlanPanel.setWidth(baseConfigMainPanel.getWidth()-270);
		}
	});
	baseConfigTree.on('resize',function(p){
		if(p.collapsed){
			departmentGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			roleGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			weightAlarmPlanPanel.setWidth(baseConfigMainPanel.getWidth()-270);
		}else{
			departmentGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			roleGrid.setWidth(baseConfigMainPanel.getWidth()-270);
			weightAlarmPlanPanel.setWidth(baseConfigMainPanel.getWidth()-270);
		}
	});
	
});
/***Ext.onReady end***/
</script>
</head>
<body>
	<div>
	<div id='weightingsetDIV' style="height:50%"></div>
	</div>
</body>
</html>