Ext.define('FHD.view.SASACdemo.RiskIntegrality.RiskIntegralityChart', {
    extend: 'Ext.container.Container',
    alias: 'widget.riskIntegralityChart',
    
    
    initComponent: function() {
    	var me = this;
    	var data = "<chart canvasBorderColor=\"C0C0C0\" canvasBottomMargin=\"70\" plotBorderColor=\"C0C0C0\" showLegend=\"1\" legendShadow=\"0\" legendPosition=\"RIGHT\" legendNumColumns=\"1\" showAlternateHGridColor=\"0\" bgColor=\"FFFFFF\" showLabels=\"1\" showvalues=\"0\" decimals=\"2\" placeValuesInside=\"1\" rotateValues=\"1\"><categories>undefined<category label=\"投资风险\" /><category label=\"健康安全环保风险\" /><category label=\"现金流风险\" /><category label=\"政策风险\" /><category label=\"竞争风险\" /><category label=\"国际化经营风险\" /></categories><dataset seriesName=\"企业数量\">undefined<set value=\"90.00\" /><set value=\"162.00\" /><set value=\"90.00\" /><set value=\"142.00\" /><set value=\"89.00\" /><set value=\"148.00\" /></dataset><dataset seriesName=\"行业分布\">undefined<set value=\"79.00\" /><set value=\"190.00\" /><set value=\"81.00\" /><set value=\"103.00\" /><set value=\"74.00\" /><set value=\"190.00\" /></dataset><dataset seriesName=\"去年同期值\">undefined<set value=\"80.00\" /><set value=\"126.00\" /><set value=\"100.00\" /><set value=\"143.00\" /><set value=\"93.00\" /><set value=\"159.00\" /></dataset></chart>";
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