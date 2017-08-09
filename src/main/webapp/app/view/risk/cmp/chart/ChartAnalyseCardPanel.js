/**
 * 图表分析card页面
 * 
 * @author 张健
 */
Ext.define('FHD.view.risk.cmp.chart.ChartAnalyseCardPanel', {
	extend : 'FHD.ux.CardPanel',
	alias : 'widget.chartanalysecardpanel',
	
	type : '',
	// 初始化方法
	initComponent : function() {
		var me = this;
		
        me.riskHeatMapPanel = Ext.create('FHD.view.risk.cmp.chart.RiskHeatMapPanel',{
        	//authority:'ALL_ASSESS_ANALYSIS_RISKQUERY_RISKCHART',
        	type : me.type,
			schm : me.schm,//风险分库标识
        	border:false
        });
        me.riskGroupCountPanel = Ext.create('FHD.view.risk.cmp.chart.RiskGroupCountPanel',{
        	//authority:'ALL_ASSESS_ANALYSIS_RISKQUERY_RISKANALYSIS',
        	type : me.type,
        	border:false
        });
        me.riskTrendLinePanel = Ext.create('FHD.view.risk.cmp.chart.RiskTrendLinePanel',{
        	//authority:'ALL_ASSESS_ANALYSIS_RISKQUERY_RISKHISTORYTREND',
        	type : me.type,
        	border:false
        });
        
        //控制权限
        var arr = [];
        arr.push(me.riskHeatMapPanel);
        if($ifAnyGranted('ALL_ASSESS_ANALYSIS_RISKQUERY_RISKANALYSIS')){
        	arr.push(me.riskGroupCountPanel);
        }
        if($ifAnyGranted('ALL_ASSESS_ANALYSIS_RISKQUERY_RISKHISTORYTREND')){
        	arr.push(me.riskTrendLinePanel);
        }
        var itemArr = [];
        itemArr.push({
			name : 'heatmapcharts',
			text : '风险图谱',
			iconCls : 'icon-tupu',
			handler : function() {
				me.heatmapcharts();
			}
		});
        if($ifAnyGranted('ALL_ASSESS_ANALYSIS_RISKQUERY_RISKANALYSIS')){
        	itemArr.push({
				name : 'groupcharts',
				text : '分类分析',
				iconCls : 'icon-chart-pie',
				handler : function() {
					me.groupcharts();
				}
			});
        }
        if($ifAnyGranted('ALL_ASSESS_ANALYSIS_RISKQUERY_RISKHISTORYTREND')){
        	itemArr.push({
				name : 'trendline',
				text : '历史趋势',
				iconCls : 'icon-chart-trendline',
				handler : function() {
					me.trendline();
				}
			});
        }
        
		//items的添加
		Ext.apply(me, {
			xtype : 'cardpanel',
			border : false,
			activeItem : 0,
			items : arr,//[me.riskHeatMapPanel,me.riskGroupCountPanel,me.riskTrendLinePanel],
			tbar : {
				items : itemArr
//					[ 
//				{
//					name : 'heatmapcharts',
//					text : '风险图谱',
//					iconCls : 'icon-tupu',
//					handler : function() {
//						me.heatmapcharts();
//					}
//				},'-', 
//				{
//					name : 'groupcharts',
//					text : '分类分析',
//					iconCls : 'icon-chart-pie',
//					handler : function() {
//						me.groupcharts();
//					}
//				},'-',
//				{
//					name : 'trendline',
//					text : '历史趋势',
//					iconCls : 'icon-chart-trendline',
//					handler : function() {
//						me.trendline();
//					}
//				}]
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