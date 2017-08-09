Ext.define('FHD.view.risk.analyse.RiskTolerance', {
    extend: 'Ext.container.Container',
    alias : 'widget.risktolerance',
    requires: [
    ],
    
    initComponent: function() {
        var me = this;
        if(Ext.getCmp('highchart_TolerancePieChart')==null){
        	me.chartPanel = Ext.create('Ext.panel.Panel',{
            	border:false,
            	id:'highchart_TolerancePieChart',
    	    	html:'<div id="riskTolerancePie" style="height:400px;width:100%"></div>'
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
    createRiskGrid:function(){
    	var me = this;
        var cols = [
          			 {
          				header : "计算需求数量",
          				dataIndex : 'jsxq',
          				sortable : true,
          				flex : 1,
          				editor:true
          			},
          			{
          				header:'保证生产的程度',
          				dataIndex:'bzsc',
          				flex : 1,
          				editor:true
          			},
          			{
          				header:'断供发生频率（月/次',
          				dataIndex:'dgfs',
          				flex : 1,
          				editor:true
          			},
          			{
          				header:'偏差金额（万元）',
          				dataIndex:'pcje',
          				flex : 1,
          				editor:true
          			},
          			{
          				header:'偏差率',
          				dataIndex:'pcl',
          				flex : 1,
          				editor:true
          			}
                         ];
        var riskCategoryGrid = Ext.create('FHD.view.component.GridPanel',{
//        	extraParams : me.extraParams,
        	url:__ctxPath+'/app/view/risk/analyse/grid.json',
        	cols:cols,
        	autoScroll:true,
        	border: true,
		    checked: false,
		    pagable : false,
		    searchable : false,
		    columnLines: false,
		    isNotAutoload : false
        });
        me.add(riskCategoryGrid);
    },
	renderChart:function(){
		var data = [
		            [Date.UTC(2003,8,24),10000],
		            [Date.UTC(2003,8,25),12000],
		            [Date.UTC(2003,8,26),11000],
		            [Date.UTC(2003,8,29),11050],
		            [Date.UTC(2003,8,30),15000],
		            [Date.UTC(2003,9,1),17000],
		            [Date.UTC(2003,9,2),10000],
		            [Date.UTC(2003,9,3),18000],
		            [Date.UTC(2003,9,6),17500],
		            [Date.UTC(2003,9,7),16000],
		            [Date.UTC(2003,9,8),13000],
		            [Date.UTC(2003,9,9),14000],
		            [Date.UTC(2003,9,10),14500],
		            [Date.UTC(2003,9,13),15000],
		            [Date.UTC(2003,9,14),15500],
		            [Date.UTC(2003,9,15),14560],
		            [Date.UTC(2003,9,16),17560],
		            [Date.UTC(2003,9,17),12600],
		            [Date.UTC(2003,9,20),15000],
		            [Date.UTC(2003,9,21),15620],
		            [Date.UTC(2003,9,22),14560],
		            [Date.UTC(2003,9,23),15600],
		            [Date.UTC(2003,9,24),17500],
		            [Date.UTC(2003,9,27),12350],
		            [Date.UTC(2003,9,28),11000]
		            ];
//		$.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=usdeur.json&callback=?', function(data) {
			$('#riskTolerancePie').highcharts('StockChart', {
								
								rangeSelector : {
									selected : 1
								},
					
								title : {
								},
								credits:{
					            	enabled:false
					            },
								yAxis : {
									title : {
										text : '需求数量'
									},
									plotLines : [{
										value : 9900,
										color : 'green',
										dashStyle : 'shortdash',
										width : 2,
										label : {
											text : '<strong>下限</strong>'
										}
									}, {
										value : 16500,
										color : 'red',
										dashStyle : 'shortdash',
										width : 2,
										label : {
											text : '<strong>上限</strong>'
										}
									}]
								},
					
								series : [{
									name : 'USD to EUR',
									data : data,
									tooltip : {
										valueDecimals : 4
									}
								}]
							});							
//		});
	},
	afterRenderChart:function(){
	}
});