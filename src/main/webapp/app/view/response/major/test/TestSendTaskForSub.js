Ext.define('FHD.view.response.major.test.TestSendTaskForSub', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.testsendtaskforsub',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.grid = Ext.create('FHD.view.response.major.test.TaskForm',{
    		executionId:me.executionId,
    		businessId:me.businessId
		});
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
     	    items:[me.grid]
        });
    	
        me.callParent(arguments);
        
    },
    reloadData:function(){
		var me=this;
	}
});