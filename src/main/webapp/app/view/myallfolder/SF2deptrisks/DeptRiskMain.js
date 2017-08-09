Ext.define('FHD.view.myallfolder.SF2deptrisks.DeptRiskMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.deptriskmain',
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {

        var me = this;
        //我的已办面板
        me.deptRisksCard = Ext.create('FHD.view.myallfolder.SF2deptrisks.DeptRiskCard',{
            border : true,
            autoDestroy: true,
            schm: me.typeId,
            region: 'center'
        });
        Ext.applyIf(me, {
            items: [me.deptRisksCard]
        });

        me.callParent(arguments);
    }
});