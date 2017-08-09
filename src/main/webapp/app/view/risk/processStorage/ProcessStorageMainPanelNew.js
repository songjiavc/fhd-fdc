/**
 *
 * 流程风险库维护主页面
 * 使用border布局
 *
 *
 *
 * @author
 */
Ext.define('FHD.view.risk.processStorage.ProcessStorageMainPanelNew', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processstoragemainpanelnew',
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {

        var me = this;
        //风险库右侧面板
        me.processCard = Ext.create('FHD.view.risk.processStorage.ProcessStorageCardNew',{
            border : true,
            autoDestroy: true,
            schm: me.typeId,
            region: 'center'
        });
        //风险树
        me.processTree = Ext.create('FHD.view.risk.processStorage.ProcessStorageTreePanel',{
            rowLines: false,
            face: me,
            extraParams: {
            showLight: true
            },
            collapsible: false,
            typeId: me.typeId,
            schm: me.typeId,
            region: 'west',
            border: true,
            rbs: true,
            width: 180,
            maxWidth: 350,
            split: true,
            autoDestroy: true
        });

        Ext.applyIf(me, {
            items: [me.processTree,me.processCard]
        });

        me.callParent(arguments);
    }

});