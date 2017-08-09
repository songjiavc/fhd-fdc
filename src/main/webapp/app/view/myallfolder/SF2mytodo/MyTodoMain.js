Ext.define('FHD.view.myallfolder.SF2mytodo.MyTodoMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.mytodomain',
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : true,
    // 初始化方法
    initComponent: function() {

        var me = this;
        //我的待办面板
        me.mytodoCard = Ext.create('FHD.view.myallfolder.SF2mytodo.MyTodoCard',{
            border : true,
            autoDestroy: true,
            schm: me.typeId,
            region: 'center'
        });
        Ext.applyIf(me, {
            items: [me.mytodoCard]
        });

        me.callParent(arguments);
    }
});