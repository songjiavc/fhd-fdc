/**
 * 结果输出主面板
 * 
 * @author 
 */
Ext.define('FHD.view.riskAnalysisDemo.AnalysisResultMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.analysisResultMainPanel',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//左侧面板
    	me.leftPanel = Ext.create('FHD.view.riskAnalysisDemo.AnalysisResultLeftPanel');
    	//右侧面板
    	//me.rightPanel = Ext.create('FHD.view.risk.analyse.RiskRecurrenceAnalyse');
    	
    	
    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     	        type: 'border',
     	        padding: '0 0 5	0'
     	    },
     	    items:[me.leftPanel]
        });
    	
        me.callParent(arguments);
        
    }
});