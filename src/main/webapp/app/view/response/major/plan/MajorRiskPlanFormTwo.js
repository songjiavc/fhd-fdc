Ext.define('FHD.view.response.major.plan.MajorRiskPlanFormTwo',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.majorriskplanformtwo',
 	border:false,
 	layout:{
		align: 'stretch',
		type: 'border'
	},
 	requires: [
	],
	//初始化grid和tree
	initDataForTreeAndGrid:function(planId){
		var me = this;
		//初始化grid
		var gridStore = me.riskSelecteGrid.loadData(planId);
		//tree加载时node里属性checked = true即可
		me.treePanel.setCheck(gridStore.data.items);
	},
    initComponent: function () {
    	var me = this;
    	me.planId=Ext.widget('hiddenfield',{
            name:"planId",
            value:''
        });
    	me.treePanel = Ext.create('FHD.view.response.major.plan.MajorRiskTreePanel',{
    		planType:me.planType,//计划类型
   		 	schm:me.schm,//分库标识
    		split : true,
        	region:'west',
        	checkable:true,
		});
    	me.riskSelecteGrid = Ext.create('FHD.view.response.major.plan.MajorRiskGridSelected',{
    		region: 'center',
    		planType:me.planType,//计划类型
   		    schm:me.schm,//分库标识
		});
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [me.treePanel,me.riskSelecteGrid],
			buttons: []
		});
    	me.callParent(arguments);
    	
    }

});