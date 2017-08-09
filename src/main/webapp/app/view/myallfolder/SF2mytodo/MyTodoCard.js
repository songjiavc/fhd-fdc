Ext.define('FHD.view.myallfolder.SF2mytodo.MyTodoCard',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.mytodocard',


    initComponent: function () {
        var me = this;
        //我的待办列表
        me.myTodoGrid = Ext.create('FHD.view.myallfolder.SF2mytodo.MyTodoGrid',{
            autoHeight: true,
            border: false,
            pContainer: me,
            face: me,
            autoDestroy: true,
            navHeight: me.navHeight
        });
        Ext.apply(me, {
            border:false,
            activeItem : 0,
            items: [me.myTodoGrid]
        });

        me.callParent(arguments);
    }

});