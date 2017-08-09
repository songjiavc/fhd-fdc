/**
 * 
 * 工作计划左侧功能树
 * 
 * @author 胡迪新
 */
Ext.define('FHD.view.icm.standard.StandardPlanCenterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.standardplancenterpanel',
	
    layout:'fit',
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.standardplandashboard = Ext.widget('standardplandashboard',{});
        me.callParent(arguments);
        //驾驶舱
        if($ifAllGranted('ROLE_ALL_ENV_ICSTANDARD_DASHBOARD')){
        	me.add(me.standardplandashboard);
        }
    }
});