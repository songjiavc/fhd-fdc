<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript">
Ext.define('icm_assess_assessPlanPointRelaOrgAndEmpEditView',{
	extend : 'Ext.container.Container',
	layout : 'border',
	defaults : {
		collapsible : true,
		split : true,
		animFloat : false,
		useSplitTips : true,
		collapseMode : 'mini'
	},
	items : new Array(),
	height : FHD.getCenterPanelHeight(),
	autoWidth:true,
	renderTo : "FHD.icm.assessPlanPointRelaOrgAndEmpEditManage${param._dc}",
	gridPanel:null,
	initComponent : function(){
		var me=this;
		me.gridPanel = Ext.create('Ext.container.Container', {
			 region : 'center',
			 height:100,
		     autoLoad : {
			 url : 'pages/icm/assess/assessPlanPointRelaOrgEmpEditList.jsp?assessPlanId=${param.assessPlanId}',
			 scripts : true
		     }
		});
		me.items = [me.gridPanel];
		me.callParent(arguments);
	}
	
});

var assessPlanPointRelaOrgAndEmpEditView;

Ext.onReady(function() {

	assessPlanPointRelaOrgAndEmpEditView = Ext.create('icm_assess_assessPlanPointRelaOrgAndEmpEditView');
	 FHD.componentResize(assessPlanPointRelaOrgAndEmpEditView, 0, 0);

});

</script>


</head>
<body>
	<div id='FHD.icm.assessPlanPointRelaOrgAndEmpEditManage${param._dc}'></div>
</body>

</html>