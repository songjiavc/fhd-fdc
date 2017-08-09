/*
 * 风险事件新增页面通用组件
 * ZJ
 * */
Ext.define('FHD.view.risk.cmp.riskevent.RiskEventAddFormCmp', {
    extend: 'FHD.view.risk.cmp.RiskAddForm',
    alias: 'widget.riskeventaddformcmp',
    autoScroll: true,
    border: false,
    initComponent: function () {
        var me = this;
        var saveBtn = Ext.create('Ext.button.Button', {
            text: FHD.locale
                .get("fhd.strategymap.strategymapmgr.form.save"), // 保存按钮
            iconCls: 'icon-control-stop-blue',
            handler: function () {
                me.save();
            }
        });
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
            callback : function(){
            	me.goback();
            },
            border: false,
            tbar: [{
                width: 0,
                height: 20
            }],
            bbar: ['->', returnBtn, saveBtn]
        });
        me.callParent(arguments);
    },
    goback : Ext.emptyFn()//返回方法
});