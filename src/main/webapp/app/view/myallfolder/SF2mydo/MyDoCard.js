Ext.define('FHD.view.myallfolder.SF2mydo.MyDoCard',{
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.mydocard',


    initComponent: function () {
        var me = this;
        //我的待办列表
        me.myDoGrid = Ext.create('FHD.view.myallfolder.SF2mydo.MyDoGrid',{
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
            items: [me.myDoGrid]
        });

        me.callParent(arguments);
    }

});