Ext.define('FHD.view.risk.comprehensivequery.RiskCompreQueryPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskcomprequerypanel',

    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        //查询条件form
        me.riskQuaryForm = Ext.create('FHD.view.risk.comprehensivequery.RiskCompreQueryForm',{
        	region: 'north'
        });
        
        //十大风险列表
        me.riskQuaryGrid = Ext.create('FHD.view.risk.comprehensivequery.RiskCompreQueryGrid',{
        	border : true,
            region: 'center'
        });
        
        Ext.applyIf(me, {
        	autoDestroy: true,
            items: [me.riskQuaryForm,me.riskQuaryGrid]
        });

        me.callParent(arguments);
    }

});