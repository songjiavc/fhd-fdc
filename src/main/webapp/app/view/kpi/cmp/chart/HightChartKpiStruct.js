Ext.define('FHD.view.kpi.cmp.chart.HightChartKpiStruct', {
	extend : 'Ext.panel.Panel',
	style : 'padding:5px 5px 0px 5px',
	border : true,
	timeRefresh: true,
	initParam : function(paramObj) {
		var me = this;
		me.paramObj = paramObj;
	},
	initChart : function(data) {
		var me = this;
		if (me.chartPanel) { // 仪表盘
			me.remove(me.chartPanel, true);
		}
		var storeArray = [];
		if (data.totalCount != "0") {
			var datas = data.datas;
			for (var i = 0; i < datas.length; i++) {
				var valueArr = [];
				var v = datas[i];
				if(v.finishValue.length==0){
					continue;
				}else{
					valueArr.push(v.name);
					valueArr.push(v.finishValue);
					storeArray.push(valueArr);
				}
			}
		}

		me.chartPanel = Ext.create('Ext.panel.Panel', {
					border : false,
					html : '<div id="'
							+ me.id
							+ 'DIV" style="height:450px; min-width: 950px"></div>',
					listeners : {
						afterrender : function(c, opts) {
							me.renderChart(storeArray);
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
//		paraobj.dataType = me.paramObj.dataType;
		paraobj.quarterId = FHD.data.quarterId;
		paraobj.weekId = FHD.data.weekId;

		FHD.ajax({
					url : __ctxPath + '/kpi/cmp/findcategoryrelakpistruct.f',
					params : {
						condItem : Ext.JSON.encode(paraobj)
					},
					callback : function(data) {
						if (data && data.success) {
							me.initChart(data);
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
							+ 'DIV" style="height: 450px; min-width: 950px"></div>'
				});

		me.callParent(arguments);

		me.add(me.chartPanel);

	},
	renderChart : function(storeArray) {
		var me = this;
		
		/*Highcharts.getOptions().colors = Highcharts.map(Highcharts.getOptions().colors, function(color) {
	    return {
	        radialGradient: { cx: 0.5, cy: 0.3, r: 0.7 },
	        stops: [
	            [0, color],
	            [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
	        ]
	    };
	   });*/
	  
		var yearId = FHD.data.yearId;
		var chartId = me.id + 'DIV';
		var options = {
			credits : {
				enabled : false
			},
	        chart: {
	        	renderTo : chartId,
	            plotBackgroundColor: null,
	            plotBorderWidth: null,
	            plotShadow: false
	        },
	        title: {
	            text : '指标结构分析',
				style:{
					fontSize: '14px',
					fontWeight:'900'
				}
	        },
	        tooltip: {
	    	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	        },
	        plotOptions: {
	            pie: {
	                allowPointSelect: true,
	                cursor: 'pointer',
	                dataLabels: {
	                    enabled: true,
	                    color: '#000000',
	                    connectorColor: '#000000',
	                    formatter: function() {
	                        return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 1) +' %';
	                    }
	                }
	            }
	        },
	        series: [{
	            type: 'pie',
	            name: '比例',
	            data: storeArray
	        }]
	    };
		me.chartcontainer = new Highcharts.Chart(options);
	}
});