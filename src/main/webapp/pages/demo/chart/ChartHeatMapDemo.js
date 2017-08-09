Ext.define('FHD.demo.chart.ChartHeatMapDemo', {
    extend: 'Ext.container.Container',
    alias: 'widget.chartheatmapdemo',
    
    
    initComponent: function() {
    	var me = this;
var data = '<chart Caption="风险图谱" bgColor="D0AC41, FFFFFF" bgAngle="90" baseFontSize="15">\n\
        <rows>\n\
			<row id="5"/>\n\
            <row id="4.5"/>\n\
			<row id="4"/>\n\
            <row id="3.5"/>\n\
            <row id="3"/>\n\
            <row id="2.5"/>\n\
            <row id="2"/>\n\
            <row id="1.5"/>\n\
            <row id="1"/>\n\
        </rows>\n\
        <columns>\n\
               <column id="1" />\n\
               <column id="1.5" />\n\
               <column id="2" />\n\
               <column id="2.5" />\n\
               <column id="3" />\n\
               <column id="3.5" />\n\
               <column id="4" />\n\
			   <column id="4.5" />\n\
			   <column id="5" />\n\
        </columns>\n\
    <dataset>\n\
                <set rowId="1" columnId="1" value="1"/>\n\
                <set rowId="1" columnId="1.5" value="2"/>\n\
                <set rowId="1" columnId="2" value="2"/>\n\
                <set rowId="1" columnId="2.5" value="3"/>\n\
                <set rowId="1" columnId="3" value="3"/>\n\
                <set rowId="1" columnId="3.5" value="15"/>\n\
                <set rowId="1" columnId="4" value="15"/>\n\
				<set rowId="1" columnId="4.5" value="20"/>\n\
				<set rowId="1" columnId="5" value="20"/>\n\
				<set rowId="1.5" columnId="1" value="5"/>\n\
				<set rowId="2" columnId="1" value="5"/>\n\
                <set rowId="1.5" columnId="1.5" value="6"/>\n\
                <set rowId="2" columnId="2" value="6"/>\n\
                <set rowId="1.5" columnId="2" value="6"/>\n\
                <set rowId="2" columnId="1.5" value="6"/>\n\
                <set rowId="1.5" columnId="2.5" value="11"/>\n\
                <set rowId="2" columnId="3" value="11"/>\n\
                <set rowId="1.5" columnId="3" value="11"/>\n\
                <set rowId="2" columnId="2.5" value="11"/>\n\
                <set rowId="1.5" columnId="3.5" value="19"/>\n\
                <set rowId="2" columnId="4" value="19"/>\n\
                <set rowId="1.5" columnId="4" value="19"/>\n\
                <set rowId="2" columnId="3.5" value="19"/>\n\
                <set rowId="1.5" columnId="4.5" value="18"/>\n\
                <set rowId="2" columnId="4.5" value="39"/>\n\
                <set rowId="1.5" columnId="5" value="39"/>\n\
                <set rowId="2" columnId="5" value="39"/>\n\
                <set rowId="2.5" columnId="1" value="9"/>\n\
                <set rowId="3" columnId="1" value="9"/>\n\
                <set rowId="2.5" columnId="1.5" value="21"/>\n\
                <set rowId="3" columnId="2" value="21"/>\n\
                <set rowId="3" columnId="1.5" value="21"/>\n\
                <set rowId="2.5" columnId="2" value="21"/>\n\
                <set rowId="2.5" columnId="2.5" value="23"/>\n\
                <set rowId="3" columnId="3" value="23"/>\n\
                <set rowId="3" columnId="2.5" value="23"/>\n\
                <set rowId="2.5" columnId="3" value="23"/>\n\
                <set rowId="2.5" columnId="3.5" value="30"/>\n\
                <set rowId="3" columnId="4" value="30"/>\n\
                <set rowId="3" columnId="3.5" value="30"/>\n\
                <set rowId="2.5" columnId="4" value="30"/>\n\
                <set rowId="2.5" columnId="4.5" value="39"/>\n\
                <set rowId="3" columnId="4.5" value="39"/>\n\
                <set rowId="2.5" columnId="5" value="39"/>\n\
                <set rowId="3" columnId="5" value="39"/>\n\
                <set rowId="3.5" columnId="1" value="15"/>\n\
                <set rowId="4" columnId="1" value="15"/>\n\
                <set rowId="3.5" columnId="1.5" value="18"/>\n\
                <set rowId="4" columnId="2" value="18"/>\n\
                <set rowId="4" columnId="1.5" value="18"/>\n\
                <set rowId="3.5" columnId="2" value="18"/>\n\
                <set rowId="3.5" columnId="2.5" value="30"/>\n\
                <set rowId="4" columnId="3" value="30"/>\n\
                <set rowId="4" columnId="2.5" value="30"/>\n\
                <set rowId="3.5" columnId="3" value="30"/>\n\
                <set rowId="3.5" columnId="3.5" value="40"/>\n\
                <set rowId="4" columnId="4" value="40"/>\n\
                <set rowId="4" columnId="3.5" value="40"/>\n\
                <set rowId="3.5" columnId="4" value="40"/>\n\
                <set rowId="3.5" columnId="4.5" value="39"/>\n\
                <set rowId="4" columnId="4.5" value="39"/>\n\
                <set rowId="3.5" columnId="5" value="39"/>\n\
                <set rowId="4" columnId="5" value="39"/>\n\
                <set rowId="4.5" columnId="1" value="14"/>\n\
                <set rowId="4.5" columnId="1.5" value="18"/>\n\
                <set rowId="4.5" columnId="2" value="33"/>\n\
                <set rowId="4.5" columnId="2.5" value="44"/>\n\
                <set rowId="4.5" columnId="3" value="44"/>\n\
                <set rowId="4.5" columnId="3.5" value="49"/>\n\
                <set rowId="4.5" columnId="4" value="49"/>\n\
                <set rowId="4.5" columnId="4.5" value="28"/>\n\
                <set rowId="4.5" columnId="5" value="28"/>\n\
                <set rowId="5" columnId="1" value="14"/>\n\
                <set rowId="5" columnId="1.5" value="39"/>\n\
                <set rowId="5" columnId="2" value="33"/>\n\
                <set rowId="5" columnId="2.5" value="44"/>\n\
                <set rowId="5" columnId="3" value="44"/>\n\
                <set rowId="5" columnId="3.5" value="49"/>\n\
                <set rowId="5" columnId="4" value="49"/>\n\
                <set rowId="5" columnId="4.5" value="28"/>\n\
                <set rowId="5" columnId="5" value="28"/>\n\
   </dataset>\n\
        <colorRange gradient="0">\n\
        <color minValue="0" maxValue="10" code="8EA715" label="低"/>\n\
        <color minValue="10" maxValue="25" code="EDC738" label="中"/>\n\
        <color minValue="25" maxValue="50" code="BB1610" label="高 "/>\n\
     </colorRange>\n\
        <styles>\n\
            <definition>\n\
                <style name="DataValueStyle" type="font" color="FFFFFF" bold="1"/>\n\
            </definition>\n\
            <application>\n\
                <apply toObject="DataValues" styles="DataValueStyle" />\n\
            </application>\n\
        </styles>\n\
</chart>';
        
        me.annualRiskAssessmentChart = Ext.create('FHD.ux.FusionChartPanel',{
    		chartType:'HeatMap',
			flex:4,
			width:120,
			height:150,
			border:false,
			//title:'计划完成率',
    		xmlData:data
		});
    	Ext.applyIf(me,{
    		layout: {
				type: 'fit'
	        },
	        items : me.annualRiskAssessmentChart
		});
		me.callParent(arguments);
    }
    
})