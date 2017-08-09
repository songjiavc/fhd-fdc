Ext.define('FHD.view.response.responseplan.ResponsePlanPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.responseplanpanel',
    layout:{
		align: 'stretch',
		type: 'vbox'
    },
    requires: [
       'FHD.ux.icm.common.FlowTaskBar'
    ],
	autoScroll:true,
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	border:false,
    initComponent: function() {
        var me = this;
		me.flowtaskbar=Ext.widget('panel',{
        	title: "风险应对方案-审批",
            region:'north',
            collapsed:true,
            collapsible: true,
            maxHeight:200,
            split: true,
            border: false,
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

        
        me.cardpanel = Ext.create('FHD.view.response.responseplan.ResponsePlanCardPanel',{
        	flex : 1,
        	businessId:me.businessId,
        	executionId:me.executionId,
        	editflag:me.editflag,
        	border:false
        });
       	Ext.applyIf(me, {
        	items:[me.cardpanel]
        });
    
        me.callParent(arguments);
    },
    reloadData:function(){
//    	var me=this;
//    	var constructplaneditpanel = me.up('constructplaneditpanel');
//    	if(constructplaneditpanel){
//    		me.cardpanel.initParam(me.paramObj);
//        	me.cardpanel.reloadData();
//    	}
    }
});