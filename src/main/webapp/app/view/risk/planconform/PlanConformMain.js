Ext.define('FHD.view.risk.planconform.PlanConformMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.planConformMain',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//右侧面板
    	me.planConformCard = Ext.create('FHD.view.risk.planconform.PlanConformCard',{
			typeId:me.typeId
		});
    	
    	Ext.apply(me, {
    		layout: 'fit',
			typeId:me.typeId,//分库标志
            border:false,
     	    items:[me.planConformCard]
        });
    	
        me.callParent(arguments);
        
    }
});