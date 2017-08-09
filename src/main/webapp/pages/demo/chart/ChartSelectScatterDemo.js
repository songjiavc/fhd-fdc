Ext.define('FHD.demo.chart.ChartSelectScatterDemo', {
    extend: 'Ext.container.Container',
    alias: 'widget.chartselectscatterdemo',
    
    initComponent: function() {
    	var me = this;
    	me.riskDistributionXml='<chart palette="3" caption="风险图谱分布" yAxisName="" xAxisName="" showLegend="0" showNames="1" xAxisMaxValue="9" xAxisMinValue="0" submitDataAsXML="0" showFormBtn="0">\n\
        	<categories>\n\
	        	<category name="1" x="1" showVerticalLine="1"/>\n\
	        	<category name="2" x="2" showVerticalLine="1"/>\n\
	        	<category name="3" x="3" showVerticalLine="1"/>\n\
	        	<category name="4" x="4" showVerticalLine="1"/>\n\
	        	<category name="5" x="5" showVerticalLine="1"/>\n\
	        	<category name="6" x="6" showVerticalLine="1"/>\n\
	        	<category name="7" x="7" showVerticalLine="1"/>\n\
	        	<category name="8" x="8" showVerticalLine="1"/>\n\
	        	<category name="9" x="9" showVerticalLine="1"/>\n\
        	</categories>\n\
        	<dataSet id="DS1" seriesName="风险" color="0372AB" plotBorderThickness="0" showPlotBorder="1" anchorSides="3">\n\
	        	<set id="INVEQ324_1" x="4.2" y="3.2" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_2" x="2.8" y="3.6" tooltext="发生可能性：2.8,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_3" x="6.2" y="4.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_4" x="1" y="4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_5" x="1.2" y="3.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_6" x="4.4" y="4.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_7" x="8.5" y="3" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_8" x="6.9" y="1.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_9" x="8.9" y="4.1" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_10" x="0.9" y="3" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_11" x="8.8" y="4.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_12" x="3.2" y="2.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_13" x="1.1" y="3.6" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_14" x="4.8" y="2.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_15" x="5.8" y="7.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_16" x="3.5" y="2.5" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_17" x="2.9" y="3.1" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_18" x="0.8" y="1.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_19" x="8.9" y="4.7" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_20" x="0.9" y="2" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_21" x="5.3" y="4.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_22" x="1.4" y="2.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_23" x="8.1" y="3.7" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_24" x="7.8" y="4.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_25" x="8.8" y="4.4" tooltext="发生可能性：4.2,影响程度：3.24%"/>\n\
        	</dataSet>\n\
        	<vTrendLines>\n\
	        	<line startValue="0" endValue="3" displayValue="安全" isTrendZone="1" color="006600" />\n\
	        	<line startValue="3" endValue="6" displayValue="关注" isTrendZone="1" color="cc9900" />\n\
	        	<line startValue="6" endValue="9" displayValue="预警" isTrendZone="1" color="990000" />\n\
        	</vTrendLines>\n\
        	<styles>\n\
        		<definition>\n\
        			<style name="myCaptionFont" type="font" font="Arial" size="14" bold="1" underline="1"/>\n\
        		</definition>\n\
	        	<application>\n\
	        		<apply toObject="Caption" styles="myCaptionFont"/>\n\
	        	</application>\n\
        	</styles>\n\
        	</chart>';
        me.riskDistributionChart = Ext.create('FHD.ux.FusionChartPanel',{
    		chartType:'SelectScatter',
			flex:4,
			border:false,
    		xmlData:me.riskDistributionXml
		});
    	Ext.applyIf(me,{
    		layout: {
				type: 'fit'
	        },
	        items : me.riskDistributionChart
		});
		me.callParent(arguments);
    }
    
})