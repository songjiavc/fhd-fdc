Ext.define('FHD.view.interaction.interactionMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.interactionmain',
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//Card面板
    	me.interactionCard = Ext.create('FHD.view.interaction.InteractionCard');
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
     	    items:[me.interactionCard]
        });
    	
        me.callParent(arguments);
        
    }
});