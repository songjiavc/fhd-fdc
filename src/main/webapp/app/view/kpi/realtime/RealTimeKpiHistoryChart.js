Ext.define('FHD.view.kpi.realtime.RealTimeKpiHistoryChart', {
	extend : 'Ext.container.Container',
	border : false,
	layout : 'fit',

	initComponent : function() {
		var me = this;

		me.chartPanel = Ext.create('Ext.panel.Panel', {
					border : false,
					html : '<div id="'
							+ me.id
							+ 'DIV" style="height: 430px; min-width: 1000px"></div>',
					listeners : {
						afterrender : function(c, opts) {
							me.renderChart();
						}
					}
				});

		Ext.apply(me, {
					layout : {
						align : 'stretch',
						type : 'vbox'
					},
					items : []
				});

		me.callParent(arguments);

		me.add(me.chartPanel);

	},

	initParam : function(paramObj) {
		var me = this;
		me.paramObj = paramObj;
	},
	reload : function() {
		var me = this;
		me.renderChart();
	},
	renderChart : function() {
		var me = this;
		var chartId = me.id + 'DIV';
		var year = new Date();
		var yearId = year.getFullYear();
		FHD.ajax({
					url : __ctxPath + '/kpi/real/findrealtimekpihistorychart.f',
					params : {
						items : Ext.JSON.encode(me.paramObj),
						year : yearId
					},
					callback : function(result) {
						if (result && result.success) {
							var descMap = result.descMap
							var options = {
								chart : {
									type : 'scatter',
									zoomType : 'xy',
									renderTo : chartId
								},
								title : {
									text : ''
								},
								subtitle : {
									text : ''
								},
								credits : {
									enabled : false
								},
								xAxis : {
									title : {
										enabled : true,
										text : ''
									},
									startOnTick : true,
									endOnTick : true,
									showLastLabel : true,
									type : 'datetime', // 定义x轴上日期的显示格式
									labels : {
										formatter : function() {
											var vDate = new Date(this.value);
											return vDate.getFullYear() + "-"
													+ (vDate.getMonth() + 1)
													+ "-" + vDate.getDate();
										},
										align : 'center'
									}
								},
								yAxis : {
									title : {
										text : ''
									}
								},
								tooltip : {
									shared : false,
									formatter : function() {
										return '时间: '
												+Highcharts.dateFormat(
												'%Y-%m-%d %H:%M:%S',
												this.point.x)
												+ ' , 值: '
												+ this.point.y
												+ ' , 单位: '
												+result.unit
												+ ' , 描述: '
												+ descMap[this.point.x]
									}

								},
								legend : {
									layout : 'vertical',
									align : 'left',
									verticalAlign : 'top',
									x : 100,
									y : 70,
									floating : true,
									backgroundColor : '#FFFFFF',
									borderWidth : 1
								},
								plotOptions : {
									scatter : {
										marker : {
											radius : 5,
											states : {
												hover : {
													enabled : true,
													lineColor : 'rgb(100,100,100)'
												}
											}
										},
										states : {
											hover : {
												marker : {
													enabled : false
												}
											}
										}

									}
								},
								series : [{
											name : result.kpiName,
											color : 'rgba(39, 138, 244, .5)',
											data : result.chartDatas

										}]
							};
							me.chartcontainer = new Highcharts.Chart(options);
						}
					}
				});

	}

});