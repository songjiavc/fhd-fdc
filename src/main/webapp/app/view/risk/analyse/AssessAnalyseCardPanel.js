/**
 * 图表分析card页面
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.analyse.AssessAnalyseCardPanel', {
	extend : 'FHD.ux.CardPanel',
	alias : 'widget.assessanalysecardpanel',
	
	type : '',
	// 初始化方法
	initComponent : function() {
		var me = this;
		
        me.riskHeatMapPanel = Ext.create('FHD.view.risk.cmp.chart.RiskHeatMapPanel',{
        	type : me.type,
        	border:false
        });
        me.riskGroupCountPanel = Ext.create('FHD.view.risk.cmp.chart.RiskGroupCountPanel',{
        	type : me.type,
        	border:false
        });
        me.riskTrendLinePanel = Ext.create('FHD.view.risk.cmp.chart.RiskTrendLinePanel',{
        	type : me.type,
        	border:false
        });
		
		Ext.apply(me, {
			xtype : 'cardpanel',
			border : false,
			activeItem : 0,
			items : [me.riskHeatMapPanel,me.riskGroupCountPanel,me.riskTrendLinePanel],
			tbar : {
				items : [ 
				{
					name : 'heatmapcharts',
					text : '风险图谱',
					iconCls : 'icon-tupu',
					handler : function() {
						me.heatmapcharts();
					}
				},'-', 
				{
					name : 'groupcharts',
					text : '分类分析',
					iconCls : 'icon-chart-pie',
					handler : function() {
						me.groupcharts();
					}
				},'-',
				{
					name : 'trendline',
					text : '历史趋势',
					iconCls : 'icon-chart-trendline',
					handler : function() {
						me.trendline();
					}
				}]
			}
		});
		me.callParent(arguments);
		me.down("[name='heatmapcharts']").toggle(true);
	},
	
    heatmapcharts: function(){
    	var me = this;
    	me.getLayout().setActiveItem(me.items.items[0]);
    	me.down("[name='heatmapcharts']").toggle(true);
    	me.down("[name='groupcharts']").toggle(false);
    	me.down("[name='trendline']").toggle(false);
    },
    
    groupcharts: function(){
    	var me = this;
    	me.getLayout().setActiveItem(me.items.items[1]);
    	me.down("[name='heatmapcharts']").toggle(false);
    	me.down("[name='groupcharts']").toggle(true);
    	me.down("[name='trendline']").toggle(false);
    },
    
    trendline: function(){
    	var me = this;
    	me.getLayout().setActiveItem(me.items.items[2]);
    	me.down("[name='heatmapcharts']").toggle(false);
    	me.down("[name='groupcharts']").toggle(false);
    	me.down("[name='trendline']").toggle(true);
    },
    
    reloadData : function(nodeId){
    	var me = this;
    	me.riskHeatMapPanel.initParams(me.type);
    	me.riskHeatMapPanel.reloadData(nodeId);
    	me.riskGroupCountPanel.initParams(me.type);
    	me.riskGroupCountPanel.reloadData(nodeId);
    	me.riskTrendLinePanel.initParams(me.type);
    	me.riskTrendLinePanel.reloadData(nodeId);
    }
    
});