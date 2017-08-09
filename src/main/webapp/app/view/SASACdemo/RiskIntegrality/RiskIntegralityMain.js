Ext.define('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskIntegralityMain',

    requires: [
		
    ],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.riskIntegralityChart = Ext.create('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityChart',{
        	region:'north',
        	height: 300
        });
        
        me.riskIntegralityGrid = Ext.create('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityGrid',{
        	region:'center'
        });
        
        Ext.applyIf(me, {
        	border : false,
        	layout: 'border',
            items: [me.riskIntegralityChart,me.riskIntegralityGrid]
        });

        me.callParent(arguments);
    }

});