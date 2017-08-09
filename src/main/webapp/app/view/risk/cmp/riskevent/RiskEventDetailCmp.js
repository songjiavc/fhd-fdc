/*
 * 风险事件查看通用页面
 * ZJ
 * */
Ext.define('FHD.view.risk.cmp.riskevent.RiskEventDetailCmp', {
    extend: 'FHD.view.risk.cmp.RiskDetailForm',
    alias: 'widget.riskeventdetailcmp',
    autoScroll: true,
    border: false,
    initComponent: function () {
        var me = this;
        var returnBtn = Ext.create('Ext.button.Button', {
            text: FHD.locale
                .get('fhd.strategymap.strategymapmgr.form.undo'), // 返回按钮
            iconCls: 'icon-arrow-undo',
            handler: function () {
                me.goback();
            }
        });
        Ext.apply(me, {
            type: 're',
            hiddenSaveBtn: true,
            border: false,
            tbar: ['->', returnBtn]
        });
        me.callParent(arguments);
    },
    goback: Ext.emptyFn()
});