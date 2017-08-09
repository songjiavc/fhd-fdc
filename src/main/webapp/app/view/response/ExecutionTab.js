/**
 * 方案执行上报表单
 * */
Ext.define('FHD.view.response.ExecutionTab', {
	extend : 'Ext.tab.Panel',
	alias: 'widget.executiontab',
	requires:[
		'FHD.view.response.ExecutionForm'
	],
	plain: true,
	initComponent :function() {
		var me = this;
        //方案执行页签
        me.executionform = Ext.widget('executionform',{title:'方案执行'});
        
        //工作流日志页签
        me.processInstanceInfo=Ext.create("FHD.view.bpm.processinstance.ProcessInstanceTabInfo",{
        	title: "工作流日志",
        	jbpmHistProcinstId:'icmStandardPlan',
        	model:''
        });
        //工作流状态
        me.flowChartPanel=Ext.create("FHD.view.bpm.FlowChartPanel",{
        	title: "工作流状态",
        	jbpmHistProcinstId:'icmStandardPlan',
        	processInstanceId:''
        });

        me.flowtaskbar=Ext.widget('panel',{
        	title: "工作流查看",
        	items:[
	        	Ext.widget('flowtaskbar',{
	    		jsonArray:[
		    		{index: 1, context:'1.指定方案制定人',status:'done'},
		    		{index: 2, context:'2.方案制定',status:'done'},
		    		{index: 3, context:'3.方案审批',status:'current'},
		    		{index: 4, context:'4.方案执行',status:'undo'}
		    	]
	    		})
        	]
        });
        
        Ext.applyIf(me, {
        	tabBar:{
        		style : 'border-right: 1px  #99bce8 solid;'
        	},
            items: [me.executionform, me.flowtaskbar,me.flowChartPanel]
        });
        me.callParent(arguments);
        me.getTabBar().insert(0,{xtype:'tbfill'});
	},
	loadData: function(improveId,executionId){
		var me = this;
		me.improveId = improveId;
		me.executionId = executionId;
		me.reloadData();
	},
	reloadData:function(){
		var me=this;
	}
});

