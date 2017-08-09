Ext.define('FHD.view.kpi.cmp.chart.HightChartAngularGauge', {
	extend : 'Ext.panel.Panel',
	style : 'padding:5px 5px 0px 5px',
	border : true,
	initParam : function(paramObj) {
		var me = this;
		me.paramObj = paramObj;
	},

	reloadData : function() {
		var me = this;

	},
	onDestroy : function() {
		if (this.chartcontainer) {
			this.chartcontainer.destroy();
		}
		this.removeAll(true);
		if (Ext.isIE) {
			CollectGarbage();
		}
		this.callParent(arguments);
	},

	initComponent : function() {
		var me = this;
		me.chartPanel = Ext.create('Ext.panel.Panel', {
					border : false,
					html : '<div id="'
							+ me.id
							+ 'DIV" style="height: 280px; min-width: 400px"></div>',
					listeners : {
						afterrender : function(c, opts) {
							me.renderChart(me.data);
						}
					}
				});

		me.callParent(arguments);

		me.add(me.chartPanel);

	},
	renderChart : function(data) {
		var me = this;
		var yearId = FHD.data.yearId;
		var chartId = me.id + 'DIV';
		var options = {
			credits : {
				enabled : false
			},
			chart : {
				renderTo : chartId,
				type : 'gauge',
				plotBackgroundColor : null,
				plotBackgroundImage : null,
				plotBorderWidth : 0,
				plotShadow : false
			},

			title : {
				text : data.name,
				style:{
					fontSize: '14px',
					fontWeight:'900'
				}
			},

			pane : {
				center : ['50%', '45%'],
				startAngle : -150,
				endAngle : 150
				// ,background: null

			},

			// the value axis
			yAxis : {
				min : 0,
				max : 100,

				minorTickInterval : 'auto',
				minorTickWidth : 1,
				minorTickLength : 10,
				minorTickPosition : 'inside',
				minorTickColor : '#666',

				tickPixelInterval : 30,
				tickWidth : 2,
				tickPosition : 'inside',
				tickLength : 10,
				tickColor : '#666',
				labels : {
					step : 2,
					rotation : 'auto'
				},
				title : {
					text : ''
				},
				plotBands : [{
							from : data.alarmRegion.greenMinValue,
							to : data.alarmRegion.greenMaxValue,
							color : '#52DD28', // green
							innerRadius : '100%',
							outerRadius : '65%'
						}, {
							from : data.alarmRegion.yellowMinValue,
							to : data.alarmRegion.yellowMaxValue,
							color : '#FFFA01', // yellow
							innerRadius : '100%',
							outerRadius : '65%'
						}, {
							from : data.alarmRegion.redMinValue,
							to : data.alarmRegion.redMaxValue,
							color : '#FF0101', // red
							innerRadius : '100%',
							outerRadius : '65%'
						}]
			},

			series : [{
						name : '评估值',
						data : [data.value],
						tooltip : {
							valueSuffix : ''
						}
					}]

		};
		me.chartcontainer = new Highcharts.Chart(options);
	}
});