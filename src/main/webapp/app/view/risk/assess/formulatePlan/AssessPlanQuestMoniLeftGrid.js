/**
 * 
 * 问卷监控左侧面板
 */

Ext.define('FHD.view.risk.assess.formulatePlan.AssessPlanQuestMoniLeftGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.assessPlanQuestMoniLeftGrid',
 	/*requires: [
 		'FHD.view.risk.assess.formulatePlan.AssessPlanQuestMoniRightGrid'
	],*/
	
	reloadData: function(){
		var me = this;
		me.store.proxy.url =  __ctxPath + '/access/formulateplan/queryreladeptbyplanId.f';
 		me.store.proxy.extraParams.businessId = me.planId;
 		me.store.load();
	},
    //默认选中第一行
    storeSelect: function(){
    	var me = this;
    	var  model = me.getSelectionModel();
		model.select(0);
    },
    //导出excel
    exportChart: function(){
    	var me=this;
    	var assessPlanQuestMonitorMain = me.up('assessPlanQuestMonitorMain');
    	sheetName = 'exportexcel';
    	window.location.href = __ctxPath +"/access/formulateplan/exportquestmonidept.f?businessId="+assessPlanQuestMonitorMain.id+"&exportFileName="+""+
    							"&sheetName="+sheetName;
    },
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
        	{header: "planId", dataIndex:'planId', hidden:true},
			{header: "id", dataIndex:'id', hidden:true},
			{header: "riskworkflowId", dataIndex:'riskworkflowId', hidden:true},
	        {header: "部门名称", dataIndex: 'deptName', sortable: true, width:40, flex:1},
	        {header: "完成情况", dataIndex: 'complateRate', sortable: true, width:40, flex:1},
	        {header: "状态", dataIndex: 'endactivity', sortable: true, width:40, flex:1,
	        	renderer:function(dataIndex) { 
    				  if(dataIndex == "0"){
    					  return '<span style="color:red;">未开始</span>';
    				  }else if(dataIndex == "1"){
    					  return '已完成';
    				  }else {
    					  return '<span style="color:green;">处理中</span>';
    				  }
    			}
	        }
        ];
       
        Ext.apply(me,{
        	cols:cols,
        	storeAutoLoad: false,
        	margin: '1 0 1 0',
        	title: '参与部门',
		    border: true,
		    checked : false,
		    pagable : false,
		    searchable: true,
		    tbarItems:[
		    	{iconCls: 'icon-ibm-action-export-to-excel', text:'导出', handler:function(){me.exportChart();}}
		    ],
		    listeners: {
		    	select:function(){//改变选中行，监听
		    		var selection = me.getSelectionModel().getSelection();
		    		var assessPlanQuestMonitorMain = me.up('assessPlanQuestMonitorMain');
		    		if(selection&&selection.length){
		    			//刷新右侧grid数据
		    			assessPlanQuestMonitorMain.rightGridPanel.reloadData(selection[0].get('planId'),selection[0].get('id'));
		    		}
		    	}
		    }
        });
       
        me.callParent(arguments);
        
    	me.getStore().addListener({
			load:function(store,records,options){
				if(records && records.length){
					me.storeSelect();
				}
		    }
		});
    }

});