/**
 * 
 * 模板管理主页面
 * 使用border布局
 * 
 * 左侧是Tree,右侧是grid
 * 
 * @author 王再冉
 */
Ext.define('FHD.view.sys.template.TemplateManageMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.templateManageMain',

    requires: [
    ],
    
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    // 初始化方法de 
    initComponent: function() {
        var me = this;
        
        me.templateManageCard = Ext.create('FHD.view.sys.template.TemplateManageCard',{
        	border : true,
            region: 'center'
        });
        
        me.templateManageTree = Ext.create('FHD.view.sys.template.TemplateManageTree',{
        	region: 'west'
        });
        
        Ext.applyIf(me, {
            items: [me.templateManageTree,me.templateManageCard]
        });

        me.callParent(arguments);
    }

});