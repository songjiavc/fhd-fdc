/**
 * 
 * 结果输出左侧面板
 */

Ext.define('FHD.view.riskAnalysisDemo.AnalysisResultLeftPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.analysisResultLeftPanel',
    requires: [
               
	],
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.upEditPanel = Ext.create('FHD.view.riskAnalysisDemo.AnalysisResultFormPanel',{
        	height: 180,
        	width:	870
        });
        me.centergrid = Ext.create('FHD.view.riskAnalysisDemo.AnalysisResultGridPanel',{
        	height:	100,
        	width:	860
        });
        me.fieldSet = {
                xtype:'fieldset',
                title: '结果列表',
                defaultType: 'textfield',
                margin: '0 5 5 5',
                columnWidth: .5,
         	    items : [me.centergrid]
       };
       
       me.downgrid = Ext.create('FHD.view.riskAnalysisDemo.AnalysisResultDowmPanel',{
        	height:	300,
        	width:	870
        });
        
        Ext.apply(me, {
        	autoScroll:true,
        	border:false,
        	layout: {
     			align: 'stretch',
     	        type: 'vbox'
     	    },
            items: [me.upEditPanel, me.fieldSet, me.downgrid]
        });

        me.callParent(arguments);

		me.on('resize',function(p){
    		me.setHeight(570);
    	});
    }

});