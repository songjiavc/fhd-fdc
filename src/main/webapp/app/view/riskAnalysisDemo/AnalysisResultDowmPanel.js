/**
 * 
 */
Ext.define('FHD.view.riskAnalysisDemo.AnalysisResultDowmPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.analysisResultDowmPanel',
    
    requires: [
    	'FHD.view.risk.analyse.RiskRecurrenceAnalyse'
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//右侧面板
    	me.resultLabel = {
                xtype:'fieldset',
                title: '数量关系',
                defaultType: 'textfield',
                margin: '5 5 5 5',
         	    items : [{xtype:'displayfield', name:'MultipleR', value:'Y（偏差金额）=-140.27+0.0287*X（物料需求数量）'}]
       };
    	//图
    	me.pictPanel = Ext.create('FHD.view.risk.analyse.RiskRecurrenceAnalyse',{
    		height: 400,
    		width: 500
    	});
    	me.pictPanel.renderChart;
    	//me.pictPanel = Ext.widget('riskrecurrenceanalyse');
    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     			align: 'stretch',
                type: 'vbox'
     	    },
     	    items:[me.resultLabel,me.pictPanel]
        });
        me.callParent(arguments);
        
    }
});