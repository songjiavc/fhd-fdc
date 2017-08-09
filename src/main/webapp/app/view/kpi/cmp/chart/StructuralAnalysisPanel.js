Ext.define('FHD.view.kpi.cmp.chart.StructuralAnalysisPanel', {
    extend: 'Ext.container.Container',

    iconCls: 'icon-ibm-icon-reports',
    border: false,
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    initComponent: function () {
        var me = this;
        //1.创建gridPanel
        me.grid = Ext.create('FHD.view.kpi.cmp.chart.StructuralAnalysisGrid', {
            searchable: false,
            flex: 2,
            pagable: false,
            border: true,
            title: FHD.locale.get('fhd.kpi.kpi.form.kpilist'),
            style: 'padding:5px 0px 5px 5px'
        });

        me.chartContainer = Ext.create('Ext.container.Container', {
            flex: 3,
            layout:'fit'
        });

        me.grid.store.on('load', function () {
            me.initChart(me.grid);
        });

        Ext.applyIf(me, {
            layout: {
                type: 'hbox',
                align: 'stretch'
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
        if (me.chart&&FusionCharts("chartId-structure" + me.id) != undefined) {
            FusionCharts("chartId-structure" + me.id).dispose();
        }
    },
    //生成饼图图表数据
    initChart: function (grid) {
        var me = this;

        var data = "<chart legendShadow='0' chartBottomMargin='100' bgAlpha='30,100' bgAngle='45' pieYScale='50' startingAngle='175'  smartLineColor='7D8892' smartLineThickness='2' baseFontSize='12' showLegend='1' legendShadow='0' showPlotBorder='1' >";
        grid.store.each(function (record) {
            if (null != record.get('finishValue')) {
                data += '<set label="' + record.get("name") + '" value="' + record.get('finishValue') + '" />';
            } else {
                data += '<set label="' + record.get("name") + '" value="0.0" />';
            }
        });
        data += "<styles>";
        data += "<definition>";
        data += "<style name='CaptionFont' type='FONT' face='Verdana' size='11' color='7D8892' bold='1' />";
        data += "<style name='LabelFont' type='FONT' color='7D8892' bold='1'/>";
        data += "</definition>";
        data += "<application>";
        data += "<apply toObject='DATALABELS' styles='LabelFont' />";
        data += "<apply toObject='CAPTION' styles='CaptionFont' />";
        data += "</application>";
        data += "</styles>";
        data += "</chart>";


        if (me.chart) {
            if (FusionCharts("chartId-structure" + me.id) != undefined) {
                FusionCharts("chartId-structure" + me.id).dispose();
            }

            me.chartContainer.remove(me.chart, true);
        }

        me.chart = Ext.create('Ext.panel.Panel', {
            border: true,
            chartid: "chartId-structure" + me.id,
            layout: 'fit',
            divid: "divId-structure" + me.id,
            style: 'padding:5px 5px 5px 5px',
            xmldata: data,
            listeners: {
                afterrender: function (c, opts) {
                    me.structureChart = new FusionCharts(__ctxPath + '/images/chart/' + 'Doughnut3D' + '.swf', c.chartid, '100%', '100%', "0", "1", "FFFFFF");
                    me.structureChart.setXMLData(c.xmldata);
                    me.structureChart.render(me.chart.id + '-body');
                }
            }
        });

        me.chartContainer.add(me.chart);
    },
    //重新加载数据
    reloadData: function () {
        var me = this;
        me.grid.store.proxy.extraParams.objectId = me.paramObj.objectId;
        me.grid.store.proxy.extraParams.dataType = me.paramObj.dataType;
        me.grid.store.load();
    }
});