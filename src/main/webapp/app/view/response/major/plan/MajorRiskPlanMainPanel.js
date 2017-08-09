/*
 * 重大风险应对计划制定主面板
 * 吉志强
 */
Ext.define('FHD.view.response.major.plan.MajorRiskPlanMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.majorriskplanmainpanel',
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	var planType = "majorRiskResponse";
    	var schm = "company";
    	
    	//右侧面板
    	me.card = Ext.create('FHD.view.response.major.plan.MajorRiskPlanCard',{
    		planType : planType,
    		schm :schm
		});
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
     	    items:[me.card]
        });
    	
        me.callParent(arguments);
        
    }
});