Ext.define('FHD.view.kpi.cmp.chart.HightChartScHistroyTrend', {
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
							+ 'DIV" style="height: 240px; min-width: 1000px"></div>',
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
		var datas = data.datas;
		var status = data.status;
		var months = data.months;
		var values = [];
		for(var i=0;i<datas.length;i++){
			var img = "";
			var v = datas[i];
			if(v==""){
				v = null;
			}
			if(status[i]&&status[i]!=""){
				if(status[i]=="0alarm_startus_h"){
					img = "url(images/icons/symbol_high_sm.gif)";
				}else if(status[i]=="0alarm_startus_m"){
					img = "url(images/icons/symbol_mid_sm.gif)";
				}else if(status[i]=="0alarm_startus_l"){
					img = "url(images/icons/symbol_low_sm.gif)";
				}
				var obj = {};
				obj.y = v;
				obj.marker = {symbol:img};
				values.push(obj);
			}else{
				values.push(v);
			}
			
		}
		
		var yearId = FHD.data.yearId;
		var chartId = me.id + 'DIV';
		var options = {
			credits : {
				enabled : false
			},
			chart : {
				//type: 'spline',
				renderTo : chartId
			},
			title : {
				x : -20,
				// center
				text : '历史趋势分析',
				style:{
					fontSize: '14px',
					fontWeight:'900'
				}
			},
			subtitle : {
				text : '',
				x : -20
			},
			xAxis : {
				categories : months
			},
			yAxis : {
				title : {
					text : ''
				},
				plotLines : [{
							value : 0,
							width : 1,
							color : '#808080'
						}]
			},
			tooltip : {
				valueSuffix : ''
			},
			legend : {
				enabled:false,
				layout : 'vertical',
				align : 'right',
				verticalAlign : 'middle',
				borderWidth : 0
			},
			series : [{
				name : data.name,
				data : values
			}]
		};
		me.chartcontainer = new Highcharts.Chart(options);
	}
});