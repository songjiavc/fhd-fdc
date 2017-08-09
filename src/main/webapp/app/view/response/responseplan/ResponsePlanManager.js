/**
 * 
 * 工作计划
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.response.responseplan.ResponsePlanManager', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.responseplanmanager',
    requires: [
    	'FHD.view.response.responseplan.ResponsePlanCenterPanel',
    	'FHD.view.response.responseplan.ResponsePlanMenuPanel'
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
        me.responseplanmenupanel = Ext.widget('responseplanmenupanel',{
        	region : 'west',
            split:true,
            width: 150,
            collapsible: false
        });
        me.responseplancenterpanel = Ext.widget('responseplancenterpanel',{
        	region : 'center',
        	border:false
        });
        
        Ext.applyIf(me, {
            items: [me.responseplancenterpanel,me.responseplanmenupanel]
        });

        me.callParent(arguments);
    }
});