/**
 * 主面板
 * 
 * @author 
 */
Ext.define('FHD.view.risk.managereport.managereportmake.ManageReportPlanMakeMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.managereportplanmakemain',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//右侧面板
    	me.manageReportPlanMakeCard = Ext.create('FHD.view.risk.managereport.managereportmake.ManageReportPlanMakeCard');
    	
    	Ext.apply(me, {
    		layout: 'fit',
            border:false,
     	    items:[me.manageReportPlanMakeCard]
        });
    	
        me.callParent(arguments);
        
    }
});