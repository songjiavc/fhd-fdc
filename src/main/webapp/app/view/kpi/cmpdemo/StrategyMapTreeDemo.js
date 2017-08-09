Ext.define('FHD.view.kpi.cmpdemo.StrategyMapTreeDemo', {
    extend: 'Ext.container.Container',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        me.treePanel = Ext.create("FHD.view.kpi.cmp.StrategyMapTree",{
        	showLight:true
        });
        
		//me.treePanel.reloadData();
		
        Ext.applyIf(me, {
        	layout:'fit',
            items: [me.treePanel]
        });
        
        me.callParent(arguments);
    }
});