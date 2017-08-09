Ext.define('FHD.view.echart.echartMain', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.echartmain',

    requires: [
    ],

    // 初始化方法
    initComponent: function() {
        var me = this;

        me.columnPie = Ext.create('Ext.panel.Panel',{
            layout:'fit',
            title: '时间轴柱形图',
            border: false,
            html : '<iframe width="100%" height="100%" frameborder="0" src="app/view/echart/echartcolumnpie.html"></iframe>'
        });

        me.pie7 = Ext.create('Ext.panel.Panel',{
            layout:'fit',
            border: false,
            title: '时间轴饼图',
            html : '<iframe width="100%" height="100%" frameborder="0" src="app/view/echart/echartpie.html"></iframe>'
        });

        Ext.apply(me, {
            activeTab: 0,
            layout:'fit',
            border: false,
            items: [me.columnPie,me.pie7],
            plain: true
        });

        me.doLayout();
        me.callParent(arguments);
    }
});