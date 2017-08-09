Ext.define('FHD.view.icm.assess.bpm.AssessPlanBpmOne', {
    extend: 'Ext.container.Container',
    alias: 'widget.assessplanbpmone',
    
    requires: [
       'FHD.view.icm.assess.AssessPlanCardPanel',
       'FHD.ux.icm.common.FlowTaskBar'
    ],
	autoScroll:true,
    
    initComponent: function() {
        var me = this;
        
        me.cardpanel = Ext.widget('assessplancardpanel',{
        	flex:1,
        	businessId:me.businessId,
        	executionId:me.executionId,
        	editflag:true
        });

        Ext.applyIf(me, {
        	layout:{
        		align: 'stretch',
    			type: 'vbox'
        	},
    		items:[Ext.widget('panel',{border:false,items:Ext.widget('flowtaskbar',{
        		jsonArray:[
  		    		{index: 1, context:'1.计划制定',status:'current'},
  		    		{index: 2, context:'2.计划审批',status:'undo'},
  		    		{index: 3, context:'3.任务分配',status:'undo'},
  		    		{index: 4, context:'4.任务分配审批',status:'undo'},
  		    		{index: 5, context:'5.计划发布',status:'undo'}  		    	]
  	    	})}),me.cardpanel]
        });
    
        me.callParent(arguments);
    },
    reloadData:function(){
    	var me=this;
    	
    }
});