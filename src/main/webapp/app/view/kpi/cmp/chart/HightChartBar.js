Ext.define('FHD.view.kpi.cmp.chart.HightChartBar', {
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
							+ 'DIV" style="height: 280px; min-width: 618px"></div>',
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
		var me = this
		var yearId = FHD.data.yearId;
		var chartId = me.id + 'DIV';
		var values = [];
		if(data&&data.datas.length>0){
			var datas = data.datas;
			for(var i=0;i<data.datas.length;i++){
				var v = 0;
				if(datas[i]){
					v = parseFloat(datas[i]);
				}
				values.push(v);
			}
		}
		var options = {
			credits : {
				enabled : false
			},
			chart : {
				renderTo : chartId,
				type : 'bar'
			},
			title : {
				text : '指标评估值',
				style:{
					fontSize: '14px',
					fontWeight:'900'
				}
			},
			subtitle : {
				text : ''
			},
			xAxis : {
				categories : data.categorys,
				title : {
					text : null
				}
			},
			yAxis : {
				min : 0,
				title : {
					text : '',
					align : 'high'
				},
				labels : {
					overflow : 'justify'
				}
			},
			tooltip : {
				valueSuffix : ' '
			},
			plotOptions : {
				bar : {
					dataLabels : {
						enabled : true
					}
				}
			},
			legend : {
				enabled:false,
				layout : 'vertical',
				align : 'left',
				verticalAlign : 'top',
				x : 0,
				y : 0,
				floating : true,
				borderWidth : 1,
				backgroundColor : '#FFFFFF',
				shadow : true
			},
			credits : {
				enabled : false
			},
			series : [{
						name : data.year+"年(评估值)",
						data : values
					}]
		};
		me.chartcontainer = new Highcharts.Chart(options);
	}
});