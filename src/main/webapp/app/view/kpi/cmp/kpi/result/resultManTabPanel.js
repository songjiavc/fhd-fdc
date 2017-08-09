Ext.define('FHD.view.kpi.cmp.kpi.result.resultManTabPanel', {
	extend : 'Ext.tab.Panel',
	alias : 'widget.resultManTabPanel',
	
	// 初始化方法
	initComponent : function() {
		var me = this;
		
		Ext.apply(me, {
			border : false,
			plain: true,
			tabBar: {
                style: 'border-left: 1px  #99bce8 solid;'
            }
		});

		me.callParent(arguments);
		
		me.getTabBar().insert(0, {
            xtype: 'tbfill'
        });
	}
});