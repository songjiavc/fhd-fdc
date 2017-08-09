/**
 * 
 * 
 * @author	王再冉
 */
Ext.define('FHD.view.risk.assess.RightWeightsetPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.rightWeightsetPanel',
    
    requires: [
               'FHD.view.risk.assess.RoleGrid',
               'FHD.view.risk.assess.WeightAlarmPlanPanel'
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//列表
    	me.roleGrid = Ext.widget('roleGrid',{
    		//height:350
    	});
    	
    	//告警方案
    	me.weightAlarmPlanPanel = Ext.widget('weightAlarmPlanPanel');
    	
    	 me.roleGridFieldSet = {
       			xtype : 'fieldset',
       			title : '职务',
//       			collapsible : true,
       			margin : '5 5 0 5',
       			layout: {
             		  align: 'stretch',
                     type: 'vbox'
                 },
                 //width : 475,
       			items : [ me.roleGrid]
       		};
    	 
    	 me.alarmPlanFieldSet = {
        			xtype : 'fieldset',
        			title : '告警方案',
//        			collapsible : true,
        			margin : '5 5 0 5',
        			layout: {
              		  align: 'stretch',
                      type: 'vbox'
                  },
                 // width : 475,
        		items : [ me.weightAlarmPlanPanel]
        		};
    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     			align: 'stretch',
     	        type: 'vbox'
     	    },
     	    items:[me.roleGridFieldSet, me.alarmPlanFieldSet]
        });
    	
        me.callParent(arguments);
        
        me.on('resize',function(p){
			me.weightAlarmPlanPanel.setHeight(me.getHeight()/2);
			me.roleGrid.setHeight(me.getHeight()/2);
	});
        
    }
});