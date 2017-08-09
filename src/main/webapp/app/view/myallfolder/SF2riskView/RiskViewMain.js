Ext.define('FHD.view.myallfolder.SF2riskView.RiskViewMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskviewmain',
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {

        var me = this;
        //风险图谱
        me.deptRisksCard = Ext.create('FHD.view.myallfolder.SF2riskView.RiskViewCard',{
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