/**
 *
 * 组织风险库维护主页面
 * 使用border布局
 *
 *
 *
 * @author
 */
Ext.define('FHD.view.risk.orgstorage.OrgStorageMainPanelNew', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.orgstoragemainpanelnew',
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
        me.riskCard = Ext.create('FHD.view.risk.orgstorage.OrgStorageCardNew',{
            border : true,
            autoDestroy: true,
            schm: me.typeId,
            region: 'center'
        });
        //风险树
        me.orgTree = Ext.create('FHD.view.risk.orgstorage.OrgStorageTreePanel',{
            subCompany: false,
            companyOnly: false,
            checkable: false,
            rootVisible: true,
            typeId: me.typeId,
            collapsible : false,
            schm: me.typeId,
            region: 'west',
            border: true,
            rbs: true,
            showLight: true,
            width: 180,
            maxWidth: 350,
            split: true,
            autoDestroy: true
        });

        Ext.applyIf(me, {
            items: [me.orgTree,me.riskCard]
        });

        me.callParent(arguments);
    }

});