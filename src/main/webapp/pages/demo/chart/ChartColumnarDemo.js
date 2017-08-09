Ext.define('FHD.demo.chart.ChartColumnarDemo', {
    extend: 'Ext.container.Container',
    alias: 'widget.chartcolumnardemo',
    
    
    initComponent: function() {
    	var me = this;
    	var data = "<chart canvasBorderColor=\"C0C0C0\" canvasBottomMargin=\"70\" plotBorderColor=\"C0C0C0\" showLegend=\"1\" legendShadow=\"0\" legendPosition=\"RIGHT\" legendNumColumns=\"1\" showAlternateHGridColor=\"0\" bgColor=\"FFFFFF\" showLabels=\"1\" showvalues=\"0\" decimals=\"2\" placeValuesInside=\"1\" rotateValues=\"1\"><categories>undefined<category label=\"华东区 流程优化计划完成率\" /><category label=\"华东区 产品开发周期\" /><category label=\"东北区 流程优化计划完成率\" /><category label=\"东北区 产品开发周期\" /><category label=\"西南区 流程优化计划完成率\" /><category label=\"西南区 产品开发周期\" /></categories><dataset seriesName=\"目标值\">undefined<set value=\"100.00\" /><set value=\"133.00\" /><set value=\"98.00\" /><set value=\"138.00\" /><set value=\"88.00\" /><set value=\"149.00\" /></dataset><dataset seriesName=\"实际值\">undefined<set value=\"90.00\" /><set value=\"162.00\" /><set value=\"90.00\" /><set value=\"142.00\" /><set value=\"89.00\" /><set value=\"148.00\" /></dataset><dataset seriesName=\"上期实际值\">undefined<set value=\"79.00\" /><set value=\"190.00\" /><set value=\"81.00\" /><set value=\"103.00\" /><set value=\"74.00\" /><set value=\"190.00\" /></dataset><dataset seriesName=\"去年同期值\">undefined<set value=\"80.00\" /><set value=\"126.00\" /><set value=\"100.00\" /><set value=\"143.00\" /><set value=\"93.00\" /><set value=\"159.00\" /></dataset></chart>";
    	var chart = Ext.create('FHD.ux.FusionChartPanel',{
    			chartType:'MSColumn2D',
    			style:'padding:5px 5px 5px 5px',
    			xmlData:data
    		});
    	Ext.applyIf(me,{
    		layout: {
				type: 'fit'
	        },
	        items : chart
		});
		me.callParent(arguments);
    }
    
})