/**
 *
 * 风险库维护主页面
 * 使用border布局
 *
 *
 *
 * @author
 */
Ext.define('FHD.view.risk.riskstorage.RiskStorageMainPanelNew', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskstoragemainpanelnew',
	header : true,
    layout: {
        type: 'border'
    },
    border : true,
    // 初始化方法
    initComponent: function() {
        var me = this;
        //风险库右侧面板
        me.riskCard = Ext.create('FHD.view.risk.riskstorage.RiskStorageCardNew',{
            border : true,
            autoDestroy: true,
            schm: me.schm,
            region: 'center'
        });
        //风险树TODO1111111111
        me.riskTree = Ext.create('FHD.view.risk.riskstorage.RiskStorageTreePanelNew',{
            typeId: me.typeId,
            region: 'west',
            border: true,
            searchable:true,
			toolbar : true,
			expandable : false,
			hideCollapseTool : true,
            rbs: true,
            typeId: me.schm,
            showLight: true,
            width: 200,
            maxWidth: 350,
            collapsible : false,
            split: true,
            autoDestroy: true
        });

        Ext.applyIf(me, {
            items: [me.riskTree,me.riskCard]
        });

        me.callParent(arguments);
    }

});