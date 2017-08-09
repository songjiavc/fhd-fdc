Ext.define('FHD.view.kpi.realtime.RealTimeKpiMain', {
    extend: 'Ext.container.Container',
    border: false,
    layout:'fit',
    
    
    initComponent: function () {
        var me = this;
        
        me.realTimeKpiGrid = Ext.create("FHD.view.kpi.realtime.RealTimeKpiGrid");
        
        me.realTimeKpiGrid.store.load();
        
        
        
        
        Ext.applyIf(me, {
        	items:[
				me.realTimeKpiGrid
			]
        });
        
        
        me.callParent(arguments);

    }
    



});