/**
 * 评估计划xin主面板
 * 
 * @author 
 */
Ext.define('FHD.view.risk.assess.formulatePlan.FormulatePlanMainnew', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.formulatePlanMainnew',
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//右侧面板
    	me.mc = Ext.create('Ext.container.Container');
    	me.formulatePlanCard = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanCardnew',{
			typeId: me.typeId	//菜单配置-分库标识
		});
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
            closeAction : 'destroy',
			typeId: me.typeId,	//菜单配置-分库标识
     	    items:[me.formulatePlanCard]
        });
    	
        me.callParent(arguments);
    }
});