Ext.define('FHD.view.response.major.SchemeMakeMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.schememakeMainPanel',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//右侧面板
    	me.card = Ext.create('FHD.view.response.major.SchemeMakeCard',{
		});
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
     	    items:[me.card]
        });
    	
        me.callParent(arguments);
        
    }
});