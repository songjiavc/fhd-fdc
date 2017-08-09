Ext.define('FHD.demo.chart.ChartPieDemo', {
    extend: 'Ext.container.Container',
    alias: 'widget.chartpiedemo',
    
    
    initComponent: function() {
    	var me = this;
    	var data = "";
    	data = data + "<chart legendShadow=\'0\' chartBottomMargin=\'100\' bgAlpha=\'30,100\' bgAngle=\'45\' pieYScale=\'50\' startingAngle=\'175\'  smartLineColor=\'7D8892\' smartLineThickness=\'2\' baseFontSize=\'12\' showLegend=\'1\' legendShadow=\'0\' showPlotBorder=\'1\' >";
	    	data = data + "<set label=\"西南区 流程优化计划完成率\" value=\"89\" />";
	    	data = data + "<set label=\"西南区 产品开发周期\" value=\"148\" />";
	    	data = data + "<set label=\"华东区 流程优化计划完成率\" value=\"90\" />";
	    	data = data + "<set label=\"华东区 产品开发周期\" value=\"162\" />";
	    	data = data + "<set label=\"东北区 流程优化计划完成率\" value=\"90\" />";
	    	data = data + "<set label=\"东北区 产品开发周期\" value=\"142\" />";
	    	
	    	data = data + "<styles>";
		    	data = data + "<definition>";
		    		data = data + "<style name=\'CaptionFont\' type=\'FONT\' face=\'Verdana\' size=\'11\' color=\'7D8892\' bold=\'1\' /><style name=\'LabelFont\' type=\'FONT\' color=\'7D8892\' bold=\'1\'/>";
		    	data = data + "</definition>";
		    	data = data + "<application>";
		    		data = data + "<apply toObject=\'DATALABELS\' styles=\'LabelFont\' /><apply toObject=\'CAPTION\' styles=\'CaptionFont\' />";
		    	data = data + "</application>";
	    	data = data + "</styles>";
    	data = data + "</chart>";
    	var chart = Ext.create('FHD.ux.FusionChartPanel',{
    		chartType:'Doughnut3D',
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