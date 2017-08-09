/*
 *
 * 风险-目标 维护主页面
 * 使用border布局
 * @author
 */
Ext.define('FHD.view.risk.riskstorage.RiskTargetMainPanelNew', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.risktargetmainpanelnew',
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {
        var me = this;
        //右侧面板
        me.targetCard = Ext.create('FHD.view.risk.riskstorage.RiskTargetCardNew',{
            border : true,
            autoDestroy: true,
            schm: me.typeId,
            region: 'center'
        });
        //目标树
        me.targetTree = Ext.create('FHD.view.risk.riskstorage.RiskTargetTreePanelNew',{
            typeId: me.typeId,
            region: 'west',
            border: true,
            rbs: true,
            typeId: me.typeId,
            showLight: true,
            width: 240,
            maxWidth: 350,
            collapsible : false,
            split: true,
            autoDestroy: true
        });

        Ext.applyIf(me, {
            items: [me.targetTree,me.targetCard]
        });

        me.callParent(arguments);
    }

});