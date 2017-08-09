Ext.define('FHD.view.kpi.cmp.chart.HightChartMultiDimCompare', {
	extend : 'Ext.panel.Panel',
	style : 'padding:5px 5px 0px 5px',
	border : true,
	timeRefresh: true,
	initParam : function(paramObj) {
		var me = this;
		me.paramObj = paramObj;
	},

	initChart : function(datas) {
		var me = this;
		if (me.chartPanel) { // 仪表盘
			me.remove(me.chartPanel, true);
		}

		me.chartPanel = Ext.create('Ext.panel.Panel', {
					border : false,
					html : '<div id="'
							+ me.id
							+ 'DIV" style="height: 500px; min-width: 1000px"></div>',
					listeners : {
						afterrender : function(c, opts) {
							me.renderChart(datas);
						}
					}
				});

		me.add(me.chartPanel);

	},
	
	load:function(){
    	var me = this;
    	me.reloadData();
    },

	reloadData : function() {
		var me = this;
		var paraobj = {};
		paraobj.isNewValue = FHD.data.isNewValue
		if (FHD.data.yearId == '') {
			paraobj.yearId = new Date().getFullYear();
		} else {
			paraobj.yearId = FHD.data.yearId;
		}
		paraobj.monthId = FHD.data.monthId;
		paraobj.objectId = me.paramObj.objectId;
		paraobj.dataType = me.paramObj.dataType;
		paraobj.quarterId = FHD.data.quarterId;
		paraobj.weekId = FHD.data.weekId;

		FHD.ajax({
					url : __ctxPath + '/kpi/cmp/findkpimultidimchart.f',
					params : {
						condItem : Ext.JSON.encode(paraobj)
					},
					callback : function(data) {
						if (data && data.success) {
							me.initChart(data.datas);
						}
					}
				});

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
							+ 'DIV" style="height: 500px; min-width: 1000px"></div>'
				});

		me.callParent(arguments);

		me.add(me.chartPanel);

	},
	renderChart : function(datas) {
		var me = this;
		var yearId = FHD.data.yearId;
		var chartId = me.id + 'DIV';
		// var chartId = me.id;
		var options = {
			credits : {
				enabled : false
			},
			chart : {
				renderTo : chartId,
				type : 'column'
			},
			title : {
				text : '指标多维对比分析',
				style:{
					fontSize: '14px',
					fontWeight:'900'
				}
			},
			subtitle : {
				text : ''
			},
			xAxis : {
				categories : ['目标值', '实际值', '上期实际值', '去年同期值']
			},
			yAxis : {
				min : 0,
				title : {
					text : ''
				}
			},
			tooltip : {
				headerFormat : '<span style="font-size:10px">{point.key}</span><table>',
				pointFormat : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>'
						+ '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
				footerFormat : '</table>',
				shared : true,
				useHTML : true
			},
			plotOptions : {
				column : {
					pointPadding : 0.2,
					borderWidth : 0
				}
			},
			series : datas
		};
		me.chartcontainer = new Highcharts.Chart(options);
	}
});