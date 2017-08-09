Ext.define('FHD.view.kpi.cmp.chart.TrendPanel', {
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
        me.grid = Ext.create('FHD.view.kpi.cmp.chart.TrendGrid', {
            searchable: false,
            flex: 3,
            pagable: false,
            border: true,
            title: FHD.locale.get('fhd.kpi.kpi.form.kpilist'),
            style: 'padding:5px 5px 0px 5px'
        });

        me.chartContainer = Ext.create('Ext.container.Container', {
            flex: 4,
            layout:'fit'
        });


        me.grid.store.on('load', function () {
            me.initChart(me.grid);
        });

        Ext.applyIf(me, {
            layout: {
                type: 'vbox',
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
        if (me.chart&&FusionCharts("chartId-trendAnalysisChart" + me.id) != undefined) {
            FusionCharts("chartId-trendAnalysisChart" + me.id).dispose();
        }
    },
    //生成趋势图表数据
    initChart: function (grid) {
        var me = this;

        //不带滚动条
        var data = '<chart canvasBorderColor="C0C0C0" showLegend="1" legendShadow="0" legendPosition="RIGHT" legendNumColumns="1" bgColor="FFFFFF" xAxisName="' + FHD.locale.get('fhd.timestampWindow.month') + '" yAxisName="' + FHD.locale.get('fhd.kpi.kpi.form.finishValue') + '" numdivlines="4" vDivLineAlpha ="0" showValues="0" decimals="2" numVDivLines="22" anchorRadius="2" labelDisplay="rotate" slantLabels="1" lineThickness="2" xtLabelManagement="0" showAlternateHGridColor="0">';

        data += '<categories>';
        data += '<category label="1' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="2' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="3' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="4' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="5' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="6' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="7' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="8' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="9' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="10' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="11' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '<category label="12' + FHD.locale.get('fhd.sys.planEdit.month') + '" />';
        data += '</categories>';


        grid.store.each(function (record) {
            data += '<dataset seriesName="' + record.get('name') + '">';
            if (null != record.get('januaryValue')) {
                data += '<set value="' + record.get('januaryValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('februaryValue')) {
                data += '<set value="' + record.get('februaryValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('marchValue')) {
                data += '<set value="' + record.get('marchValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('aprilValue')) {
                data += '<set value="' + record.get('aprilValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('mayValue')) {
                data += '<set value="' + record.get('mayValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('juneValue')) {
                data += '<set value="' + record.get('juneValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('julyValue')) {
                data += '<set value="' + record.get('julyValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('aguestValue')) {
                data += '<set value="' + record.get('aguestValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('septemberValue')) {
                data += '<set value="' + record.get('septemberValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('octoberValue')) {
                data += '<set value="' + record.get('octoberValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('novemberValue')) {
                data += '<set value="' + record.get('novemberValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            if (null != record.get('decemberValue')) {
                data += '<set value="' + record.get('decemberValue') + '" />';
            } else {
                data += '<set value="0.0" />';
            }
            data += '</dataset>';
        });

        data += '</chart>';

        if (me.chart) {
            if (FusionCharts("chartId-trendAnalysisChart" + me.id) != undefined) {
                FusionCharts("chartId-trendAnalysisChart" + me.id).dispose();
            }

            me.chartContainer.remove(me.chart, true);
        }

        me.chart = Ext.create('Ext.panel.Panel', {
            border: true,
            chartid: "chartId-trendAnalysisChart" + me.id,
            layout: 'fit',
            divid: "divId-trendAnalysisChart" + me.id,
            style: 'padding:5px 5px 5px 5px',
            xmldata: data,
            listeners: {
                afterrender: function (c, opts) {
                    me.trendAnalysisChart = new FusionCharts(__ctxPath + '/images/chart/' + 'MSLine' + '.swf', c.chartid, '100%', '100%', "0", "1", "FFFFFF");
                    me.trendAnalysisChart.setXMLData(c.xmldata);
                    me.trendAnalysisChart.render(me.chart.id + '-body');
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