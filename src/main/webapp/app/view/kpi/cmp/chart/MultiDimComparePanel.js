Ext.define('FHD.view.kpi.cmp.chart.MultiDimComparePanel',{
	extend : 'Ext.container.Container',
	
    iconCls:'icon-ibm-icon-reports',
    border: false,

	paramObj: {
		objectId: '' //目标ID
    },
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },
	
    initComponent: function() {
    	var me = this;
    	
		//1.创建gridPanel
		me.grid = Ext.create('FHD.view.kpi.cmp.chart.MultiDimCompareGrid', {
    		searchable:false,
    		checked:false,
    		flex:3,
    		pagable:false,
    		border:true,
    		title:FHD.locale.get('fhd.kpi.kpi.form.kpilist'),
            isDisplayPreResult:true,
		    style:'padding:5px 5px 0px 5px'
        });
        
        me.chartContainer = Ext.create('Ext.container.Container', {
            flex: 4,
            layout:'fit'
        });
        
		me.grid.store.on('load',function(){
    			me.initChart(me.grid);
    	});
    	
    	Ext.applyIf(me,{
    		layout: {
				type: 'vbox',
	        	align:'stretch'
	        },
	        listeners: {
                destroy:function(me, eOpts){
                	me.destroyFusionChart();
                	if(Ext.isIE){
                        CollectGarbage();
                    }
                }
            }
		});
		
		me.callParent(arguments);
		
		me.add(me.grid);
		
		me.add(me.chartContainer);
    },
    
    destroyFusionChart:function(){
    	var me = this;
    	
		if (me.chart&&FusionCharts("chartId-multiDimCompare" + me.id) != undefined) {
            FusionCharts("chartId-multiDimCompare" + me.id).dispose();
        }
    		
    },
    //生成多维对比图表数据
    initChart:function(grid){
    	var me = this;

    	me.categoriesItems='';
		me.finishValueItems='';
		me.targetValueItems='';
		me.preFinishValueItems='';
		me.preYearFinishValueItems='';
		
		grid.store.each(function(record){
			me.categoriesItems += '<category label="'+record.get('name')+'" />';
			if(null != record.get('finishValue')){
				me.finishValueItems += '<set value="'+record.get('finishValue')+'" />';
			}else{
				me.finishValueItems += '<set value="0.0" />';
			}
			if(null != record.get('targetValue')){
				me.targetValueItems += '<set value="'+record.get('targetValue')+'" />';
			}else{
				me.targetValueItems += '<set value="0.0" />';
			}
			if(null != record.get('preFinishValue')){
				me.preFinishValueItems += '<set value="'+record.get('preFinishValue')+'" />';
			}else{
				me.preFinishValueItems += '<set value="0.0" />';
			}
			if(null != record.get('preYearFinishValue')){
				me.preYearFinishValueItems += '<set value="'+record.get('preYearFinishValue')+'" />';
			}else{
				me.preYearFinishValueItems += '<set value="0.0" />';
			}
		});
		
    	var data='<chart canvasBorderColor="C0C0C0" canvasBottomMargin="70" plotBorderColor="C0C0C0" showLegend="1" legendShadow="0" legendPosition="RIGHT" legendNumColumns="1" showAlternateHGridColor="0" bgColor="FFFFFF" showLabels="1" showvalues="0" decimals="2" placeValuesInside="1" rotateValues="1">';

		data += '<categories>';
		data += me.categoriesItems;
		data += '</categories>';
		
		data += '<dataset seriesName="'+FHD.locale.get('fhd.kpi.kpi.form.targetValue')+'">';
		data += me.targetValueItems;
		data += '</dataset>';
		
		data += '<dataset seriesName="'+FHD.locale.get('fhd.kpi.kpi.form.finishValue')+'">';
		data += me.finishValueItems;
		data += '</dataset>';
		
		data += '<dataset seriesName="'+FHD.locale.get('fhd.kpi.kpi.form.prefinishValue')+'">';
		data += me.preFinishValueItems;
		data += '</dataset>';
		
		data += '<dataset seriesName="'+FHD.locale.get('fhd.kpi.kpi.form.preYearfinishValue')+'">';
		data += me.preYearFinishValueItems;
		data += '</dataset>';
		
		data += '</chart>';
		
		if(me.chart){
			
			if (FusionCharts("chartId-multiDimCompare" + me.id) != undefined) {
                FusionCharts("chartId-multiDimCompare" + me.id).dispose();
            }
    		
    		me.chartContainer.remove(me.chart,true);
		}
		
		
		me.chart = Ext.create('Ext.panel.Panel', {
            border: true,
            chartid: "chartId-multiDimCompare" + me.id,
            layout: 'fit',
            divid: "divId-multiDimCompare" + me.id,
            style:'padding:5px 5px 5px 5px',
            xmldata: data,
            listeners: {
                afterrender: function (c, opts) {
                    me.compareChart = new FusionCharts(__ctxPath + '/images/chart/' + 'MSColumn2D' + '.swf', c.chartid, '100%', '100%', "0", "1", "FFFFFF");
                    me.compareChart.setXMLData(c.xmldata);
                    me.compareChart.render(me.chart.id + '-body');
                }
            }
        });
	
		me.chartContainer.add(me.chart);
    },
    
        //获取当前年份
    getYear: function(){
    	var myDate = new Date();
    	var year = myDate.getFullYear();
    	return year;
    },
    
    //重新加载数据
    reloadData: function() {
    	var me = this;
    	me.grid.store.proxy.extraParams.objectId = me.paramObj.objectId;
		me.grid.store.proxy.extraParams.dataType = me.paramObj.dataType;
    	me.grid.store.load();
    }
});