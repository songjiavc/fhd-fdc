/**
 * 评估计划主面板
 * 
 * @author 
 */
Ext.define('FHD.view.risk.assess.formulatePlan.FormulateMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.formulateMainPanel',
    
    requires: [
               'FHD.view.risk.assess.formulatePlan.FormulatePlanCard'
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//右侧面板
    	me.formulatePlanCard = Ext.widget('formulatePlanCard');
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
     	    items:[me.formulatePlanCard]
        });
    	
        me.callParent(arguments);
        
    }
});