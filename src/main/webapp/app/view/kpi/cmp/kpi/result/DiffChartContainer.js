Ext.define('FHD.view.kpi.cmp.kpi.result.DiffChartContainer', {
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
            var width = 350;
        	me.chartPanel = Ext.create('Ext.panel.Panel',{
            	border:false,
    	    	html:'<div id="'+me.id+'DIV" style="height: 450px; min-width: '+width+'px"></div>',
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
			url : __ctxPath + '/kpi/findkpidiffhistoryresults.f' ,
			params : {
				items : Ext.JSON.encode(itemobj),
				yearId:yearId
			},
			callback : function(result) {
				if (result && result.success) {
					var options = {
			            chart: {
			                zoomType: 'xy',
			                renderTo:chartId
			            },
			            credits:{
			            	enabled:false
			            },
			            title: {
			                text: ''
			            },
			            subtitle: {
			                text: ''
			            },
			            xAxis: [{
			                categories: result.categorys
			            }],
			            yAxis: [{ // Primary yAxis
			                labels: {
			                    formatter: function() {
			                        return this.value +result.unit;
			                    },
			                    style: {
			                        color: '#89A54E'
			                    }
			                },
			                title: {
			                    text:'',
			                    style: {
			                        color: '#89A54E'
			                    }
			                },
			                opposite: true
			    
			            }, { // Secondary yAxis
			                gridLineWidth: 0,
			                title: {
			                    text: '',
			                    style: {
			                        color: '#4572A7'
			                    }
			                },
			                labels: {
			                    formatter: function() {
			                        return this.value +result.unit;
			                    },
			                    style: {
			                        color: '#4572A7'
			                    }
			                }
			    
			            }, { // Tertiary yAxis
			                gridLineWidth: 0,
			                title: {
			                    text: '',
			                    style: {
			                        color: '#AA4643'
			                    }
			                },
			                labels: {
			                    formatter: function() {
			                        return this.value +result.unit;
			                    },
			                    style: {
			                        color: '#AA4643'
			                    }
			                },
			                opposite: true
			            }],
			            tooltip: {
			                shared: true
			            },
			            legend: {
			                layout: 'horizontal',
			                align: 'left',
			                x: 0,
			                verticalAlign: 'left',
			                y: 0,
			                floating: true,
			                backgroundColor: '#FFFFFF'
			            },
			            series: [{
			                name: result.legendTargetAlias,
			                color: '#4572A7',
			                type: 'column',
			                yAxis: 1,
			                data: result.targetValues,
			                tooltip: {
			                    valueSuffix: result.unit
			                }
			    
			            }, 
			            {
			                name: result.legendFinishAlias,
			                color: '#89A54E',
			                type: 'spline',
			                data: result.finishValues,
			                tooltip: {
			                    valueSuffix: result.unit
			                }
			            },
			            {
			                name: '差值',
			                type: 'spline',
			                color: '#AA4643',
			                yAxis: 2,
			                data: result.diffValues,
			                marker: {
			                    enabled: true
			                },
			                dashStyle: 'shortdot',
			                tooltip: {
			                    valueSuffix: result.unit
			                }
			    
			            }]
			        };
					me.chartcontainer =  new Highcharts.Chart(options);
				}
			}
		});
	}
});