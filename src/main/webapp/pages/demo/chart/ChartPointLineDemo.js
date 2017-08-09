Ext.define('FHD.demo.chart.ChartPointLineDemo', {
    extend: 'Ext.container.Container',
    alias: 'widget.chartpointlinedemo',
    
    
    initComponent: function() {
    	var me = this;
    	var data = "";
    	data = "<chart canvasBorderColor=\"C0C0C0\" showBorder='0' yAxisMinValue=\"5000\" yAxisMaxValue=\"10000\" showLegend=\"1\" legendShadow=\"0\" legendPosition=\"RIGHT\" legendNumColumns=\"1\" bgColor=\"FFFFFF\" xAxisName=\"月份\" yAxisName=\"实际值\" numdivlines=\"4\" vDivLineAlpha =\"0\" showValues=\"0\" decimals=\"2\" numVDivLines=\"22\" anchorRadius=\"2\" labelDisplay=\"rotate\" slantLabels=\"1\" lineThickness=\"2\" xtLabelManagement=\"0\" showAlternateHGridColor=\"0\">";
    		data = data + "<categories><category label=\"1月\" /><category label=\"2月\" /><category label=\"3月\" /><category label=\"4月\" /><category label=\"5月\" /><category label=\"6月\" /><category label=\"7月\" /><category label=\"8月\" /><category label=\"9月\" /><category label=\"10月\" /><category label=\"11月\" /><category label=\"12月\" /></categories>";
	    	data = data + "<dataset seriesName=\"现金营运指数\"><set value=\"4123\" /><set value=\"0.54\" /><set value=\"0.46\" /><set value=\"0.66\" /><set value=\"0.65\" /><set value=\"0.4\" /><set value=\"0.38\" /><set value=\"0.57\" /><set value=\"0.36\" /><set value=\"0.67\" /><set value=\"0.47\" /><set value=\"0.44\" /></dataset>";
	    	data = data + "<dataset seriesName=\"支出变化率\"><set value=\"1234\" /><set value=\"0.34\" /><set value=\"0.1\" /><set value=\"0.28\" /><set value=\"0.03\" /><set value=\"0.19\" /><set value=\"0.11\" /><set value=\"0.12\" /><set value=\"0.18\" /><set value=\"0.25\" /><set value=\"0.23\" /><set value=\"0.09\" /></dataset>";
	    	data = data + "<dataset seriesName=\"现金债务总额比\"><set value=\"5342\" /><set value=\"26.29\" /><set value=\"26.11\" /><set value=\"32.08\" /><set value=\"36.91\" /><set value=\"22.6\" /><set value=\"23.21\" /><set value=\"29.58\" /><set value=\"20.07\" /><set value=\"21.99\" /><set value=\"34.94\" /><set value=\"21.48\" /></dataset>";
	    	data = data + "<dataset seriesName=\"营业利润\"><set value=\"34500\" /><set value=\"36113\" /><set value=\"34070\" /><set value=\"36205\" /><set value=\"37518\" /><set value=\"34313\" /><set value=\"34654\" /><set value=\"37472\" /><set value=\"36220\" /><set value=\"36685\" /><set value=\"36476\" /><set value=\"34341\" /></dataset>";
	    	data = data + "<dataset seriesName=\"营运资本\"><set value=\"57151\" /><set value=\"76733\" /><set value=\"82762\" /><set value=\"65624\" /><set value=\"81196\" /><set value=\"90155\" /><set value=\"150000\" /><set value=\"53951\" /><set value=\"81912\" /><set value=\"81798\" /><set value=\"60535\" /><set value=\"64470\" /></dataset>";
		data = data + '<trendlines><line startvalue="5000"  endValue="8000" displayValue="Warning" color="BC9F3F" isTrendZone="1" showOnTop="0" alpha="25" valueOnRight="1"/><line startvalue="8000"  endValue="100000" displayValue="Critical" color="894D1B" isTrendZone="1" showOnTop="0" alpha="25" valueOnRight="1"/>\n\ </trendlines>';
    	data = data + "</chart>";
    	
    	var chart = Ext.create('FHD.ux.FusionChartPanel',{
			style:'padding:5px 5px 5px 5px',
			chartType:'MSLine',//点线图类型
			xmlData:data//String字符串
			//调用MSLine.getXml(List<RelaAssessResult> relaAssessResultByDataTypeList)方法拼装

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