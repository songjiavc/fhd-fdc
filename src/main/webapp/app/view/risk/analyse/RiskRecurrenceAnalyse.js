Ext.define('FHD.view.risk.analyse.RiskRecurrenceAnalyse', {
    extend: 'Ext.container.Container',
    alias : 'widget.riskrecurrenceanalyse',
    requires: [
    ],
    
    initComponent: function() {
        var me = this;
        if(Ext.getCmp('highchart_riskrecurrenceanalyse')==null){
        	me.chartPanel = Ext.create('Ext.panel.Panel',{
            	border:false,
            	id:'highchart_riskrecurrenceanalyse',
    	    	html:'<div id="riskRecurrenceAnalysePie" style="height:400px;width:100%"></div>'
            });
        }
        
        Ext.apply(me, {
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
    		items:[]
        });
        
        me.callParent(arguments);
        
        me.createRiskGrid();
        
        me.add(me.chartPanel);
        
        
        
//        me.renderChart();
        
        
        
    },
    createRiskGrid:function(){},
	renderChart:function(){
        $('#riskRecurrenceAnalysePie').highcharts({
            chart: {
            },
            xAxis: {
                min: -0.5,
                max: 5.5
            },
            yAxis: {
                min: 0
            },
            title: {
                text: 'Scatter plot with regression line'
            },
            series: [{
                type: 'line',
                name: 'Regression Line',
                data: [[0, 1.11], [5, 4.51]],
                marker: {
                    enabled: false
                },
                states: {
                    hover: {
                        lineWidth: 0
                    }
                },
                enableMouseTracking: false
            }, {
                type: 'scatter',
                name: 'Observations',
                data: [1, 1.5, 2.8, 3.5, 3.9, 4.2],
                marker: {
                    radius: 4
                }
            }]
        });
	},
	afterRenderChart:function(){
	}
});