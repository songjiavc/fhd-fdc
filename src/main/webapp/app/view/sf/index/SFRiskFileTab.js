/**
 *均衡风险tab页
 */
Ext.define('FHD.view.sf.index.SFRiskFileTab', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.sfriskproducetab',

    // 初始化方法
    initComponent: function () {
        var me = this;

        me.riskFilePanel = Ext.create('FHD.view.sf.index.SFRiskFileMain',
            {title: '<div style="FONT-SIZE: 14px;font-weight: bold;COLOR: #000;height: 30px;border-top: 1px ">风险文档</div>', fn: me.fn});
        me.newsPanel = Ext.create('FHD.view.sf.index.SFNewsMain',
            {title: '<div style="FONT-SIZE: 14px;font-weight: bold;COLOR: #000;height: 30px;border-top: 1px ">新闻公告</div>'});
        me.riskReport = Ext.create('FHD.view.sys.documentlib.DocumentLibGrid',
            {
                height: 200,
                title: '<div style="FONT-SIZE: 14px;font-weight: bold;COLOR: #000;height: 30px;border-top: 1px ">风险简报</div>'

            });
        var typeId = "WDK0001";
        me.riskReport.reloadData(typeId, null);

        Ext.apply(me, {
            border: true,
            activeTab: 0,
            items: [
                me.newsPanel,
                me.riskFilePanel,
                me.riskReport
            ]
//            plain: true
        });

        me.callParent(arguments);
//        me.getTabBar().insert(0,{xtype:'tbfill'});
    }
});