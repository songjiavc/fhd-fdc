Ext.define('FHD.view.SASACdemo.companyReport.CompanyReportMain',{
 	extend: 'Ext.panel.Panel',
 	alias : 'widget.companyReportMain',
 	requires: [
 	      	],
 	      	
 	border:false,
 	layout:{
	    	align: 'stretch',
	    	type: 'border'
        },
        	
 	
    initComponent: function () {
    	 var me = this;

    	 me.firstPanel = Ext.create('FHD.view.SASACdemo.companyReport.CompanyReportFirstMain',{
    	 	navigatorTitle : '总体情况'
    	 });
    	 
    	 me.nextGrid = Ext.create('FHD.view.SASACdemo.companyReport.CompanyReportNextGrid',{
    	 	navigatorTitle : '10大重大风险的情况'
    	 });
    	 
		 me.basicPanel = Ext.create('FHD.ux.layout.StepNavigator',{
		 	region: 'center',
		 	hiddenUndo: true,
		    hiddenTop:false,	//是否隐藏头部
		    hiddenBottom:false, //是否隐藏底部
		 	items:[me.firstPanel, me.nextGrid],
		 	undo : function(){ 
		 	}
		 });
    	me.callParent(arguments);
    	me.add(me.basicPanel);
    	
    	
    },
    reloadData:function(){
		var me=this;
	}

});