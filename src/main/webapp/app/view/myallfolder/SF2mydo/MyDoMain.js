Ext.define('FHD.view.myallfolder.SF2mydo.MyDoMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.mydomain',
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : true,
    // 初始化方法
    initComponent: function() {

        var me = this;
        //我的已办面板
        me.myDoCard = Ext.create('FHD.view.myallfolder.SF2mydo.MyDoCard',{
            border : true,
            autoDestroy: true,
            schm: me.typeId,
            region: 'center'
        });
        Ext.applyIf(me, {
            items: [me.myDoCard]
        });

        me.callParent(arguments);
    }
});