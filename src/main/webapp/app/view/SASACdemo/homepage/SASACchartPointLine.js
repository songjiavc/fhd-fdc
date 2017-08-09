Ext.define('FHD.view.SASACdemo.homepage.SASACchartPointLine', {
    extend: 'Ext.container.Container',
    alias: 'widget.sasacchartPointLine',
    
    
    initComponent: function() {
    	var me = this;
    	var data = "";
    	data = "<chart canvasBorderColor=\"C0C0C0\" showBorder='0' yAxisMinValue=\"5000\" yAxisMaxValue=\"10000\" showLegend=\"1\" legendShadow=\"0\" legendPosition=\"RIGHT\" legendNumColumns=\"1\" bgColor=\"FFFFFF\" xAxisName=\"月份\" yAxisName=\"实际值\" numdivlines=\"4\" vDivLineAlpha =\"0\" showValues=\"0\" decimals=\"2\" numVDivLines=\"22\" anchorRadius=\"2\" labelDisplay=\"rotate\" slantLabels=\"1\" lineThickness=\"2\" xtLabelManagement=\"0\" showAlternateHGridColor=\"0\">";
    		data = data + "<categories><category label=\"1月\" /><category label=\"2月\" /><category label=\"3月\" /><category label=\"4月\" /><category label=\"5月\" /><category label=\"6月\" /><category label=\"7月\" /><category label=\"8月\" /><category label=\"9月\" /><category label=\"10月\" /><category label=\"11月\" /><category label=\"12月\" /></categories>";
	    	data = data + "<dataset seriesName=\"最小值\"><set value=\"6342\" /><set value=\"22056\" /><set value=\"40201\" /><set value=\"57566\" /><set value=\"65876\" /><set value=\"89999\" /><set value=\"115732\" /><set value=\"127893\" /><set value=\"146543\" /><set value=\"176783\" /><set value=\"193324\" /><set value=\"216789\" /></dataset>";
	    	data = data + "<dataset seriesName=\"利润\"><set value=\"2962\" /><set value=\"36113\" /><set value=\"64070\" /><set value=\"46205\" /><set value=\"57518\" /><set value=\"84313\" /><set value=\"114654\" /><set value=\"157472\" /><set value=\"136220\" /><set value=\"186685\" /><set value=\"216476\" /><set value=\"254341\" /></dataset>";
	    	data = data + "<dataset seriesName=\"最大值\"><set value=\"12042\" /><set value=\"46733\" /><set value=\"72762\" /><set value=\"85624\" /><set value=\"101196\" /><set value=\"130155\" /><set value=\"150000\" /><set value=\"173951\" /><set value=\"201912\" /><set value=\"231798\" /><set value=\"250535\" /><set value=\"274470\" /></dataset>";
    	data = data + "</chart>";
    	
    	me.chart = Ext.create('FHD.ux.FusionChartPanel',{
			style:'padding:5px 5px 5px 5px',
			chartType:'MSLine',//点线图类型
			xmlData:data//String字符串
			//调用MSLine.getXml(List<RelaAssessResult> relaAssessResultByDataTypeList)方法拼装

		});
    	Ext.applyIf(me,{
    		layout: {
				type: 'fit'
	        },
	        items : me.chart
		});
		me.callParent(arguments);
    }
    
})