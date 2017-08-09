Ext.define('FHD.view.kpi.cmp.kpi.result.ChartContainer', {
    extend: 'Ext.container.Container',
    
    onDestroy: function () {
    	if(this.chartcontainer){
    		this.chartcontainer.destroy();
    	}
        this.removeAll(true);
        if (Ext.isIE) {
            CollectGarbage();
        }
        this.callParent(arguments);
    },
    
    initComponent: function() {
        var me = this;
        me.chartPanel = Ext.create('Ext.panel.Panel',{
            	border:false,
    	    	html:'<div id="'+me.id+'DIV" style="height: 490px; min-width: 600px"></div>',
    	    	listeners: {                    
    	    		afterrender: function (c, opts) {
    	    			me.renderChart(me.itemobj);
                    }
                }
            });
        
        Ext.apply(me, {
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
    		items:[]
        });
        
        me.callParent(arguments);
        
        me.add(me.chartPanel);
        
    },
	renderChart:function(itemobj){
		var me = this;
		var yearId = FHD.data.yearId;
		var chartId =  me.id + 'DIV';
		FHD.ajax({
			url : __ctxPath + '/kpi/findkpihistoryresults.f' ,
			params : {
				items : Ext.JSON.encode(itemobj),
				yearId:yearId
			},
			callback : function(result) {
				if (result && result.success) {
					var options = {
					    chart: {
					    	renderTo:chartId
					    },
					    credits:{
			            	enabled:false
			            },
					    xAxis: {
							type: 'datetime', //定义x轴上日期的显示格式
							labels: {
								formatter: function() {
									var vDate=new Date(this.value-86400000);
									return vDate.getFullYear()+"-"+(vDate.getMonth()+1)+"-"+vDate.getDate();
								},
								align: 'center'
							}
					    },
					    tooltip: {
				            xDateFormat: '%Y-%m-%d, %A'//鼠标移动到趋势线上时显示的日期格式
				        },
					    rangeSelector: {
					        selected: 4,
					        enabled:true
					    },

					    title: {
					        text: ''
					    },
					    series: [{
					        name: '实际值',
					        data: result.chartDatas,
					        type: 'spline',
					        marker : {
								enabled : true,
								radius : 5
							},
							shadow : true,
					        tooltip: {
					        	valueDecimals: 2
					        }
					    }]
					};
					me.chartcontainer =  new Highcharts.StockChart(options);
				}
			}
		});
	}
});