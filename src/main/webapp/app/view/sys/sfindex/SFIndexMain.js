/**
 * 沈飞首页主面板
 *
 * @author
 */
Ext.define('FHD.view.sys.sfindex.SFIndexMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.sfindexmain',
    requires: [],


    // 初始化方法
    initComponent: function() {
        var me = this;
       
        //左侧树面板
        me.portalTree = Ext.create('FHD.view.sys.sfindex.IndexTreePanel',{
        });

        //portal面板
        me.portalPanel = Ext.create('FHD.view.sys.sfindex.IndexPortalPanel',{
			region: 'center'
        });

        Ext.apply(me, {
            layout: 'border',
            border:false,
            items:[me.portalTree,me.portalPanel]
        });

        me.callParent(arguments);

    }
});