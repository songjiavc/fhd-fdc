Ext.define('FHD.view.risk.cmpdemo.RiskGridPanelDemo', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskgridpaneldemo',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.grid = Ext.create('FHD.view.risk.cmp.RiskGridPanel', {
        	type:'risk',
        	searchId:'CW0103'
		});

        Ext.apply(me, {
            autoScroll: true,	//不显示滚动条
            border: false,
            //bodyPadding: "5 5 5 5",
            layout:'fit',
            items: [me.grid]
        });
        
        me.callParent(arguments);
      
    }
});