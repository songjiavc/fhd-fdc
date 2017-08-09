/**
 * 
 * 文档库主页面
 * 使用border布局
 * 
 * 左侧是RuleTree,右侧是RuleEditPanel
 * 
 * @author 王再冉
 */
Ext.define('FHD.view.sys.documentlib.DocumentLibMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.documentLibMainPanel',

    requires: [
    ],
    
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.documentLibCard = Ext.create('FHD.view.sys.documentlib.DocumentLibCard',{
        	border : true,
        	typeId: me.typeId,
            region: 'center'
        });
        
        me.documentLibTree = Ext.create('FHD.view.sys.documentlib.DocumentLibTree',{
        	typeId: me.typeId,
        	collapsible : false,
        	region: 'west'
        });
        
        Ext.applyIf(me, {
            items: [me.documentLibTree,me.documentLibCard]
        });

        me.callParent(arguments);
    }

});