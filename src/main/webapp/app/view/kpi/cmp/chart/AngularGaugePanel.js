Ext.define('FHD.view.kpi.cmp.chart.AngularGaugePanel', {
    extend: 'Ext.container.Container',
    iconCls: 'icon-ibm-icon-reports',
    border: false,
    timeRefresh: true,
    paramObj: {
        objectId: '' //目标ID
    },

    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },


    initComponent: function () {
        var me = this;

        me.upContainer = Ext.create('Ext.container.Container', {
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch'
            }
        });

        me.downContainer = Ext.create('Ext.container.Container', {
            flex: 1,
            layout:'fit'
        });

        Ext.applyIf(me, {

            items: [me.upContainer, me.downContainer],

            layout: {
                type: 'vbox',
                align: 'stretch'
            },
	        listeners: {
                destroy:function(me, eOpts){
                	//me.destroyFusionChart();
                	if(Ext.isIE){
                        CollectGarbage();
                    }
                }
            }
        });
        me.callParent(arguments);

    },
    
/*    destroyFusionChart:function(){
    	var me = this;
    	if (me.angularGaugeChart&&FusionCharts("chartId-AngularGauge" + me.id) != undefined) {
                FusionCharts("chartId-AngularGauge" + me.id).dispose();
        }
        
        if (me.barChartPanel&&FusionCharts("chartId-bar" + me.id) != undefined) {
                FusionCharts("chartId-bar" + me.id).dispose();
        }
            
		if (me.downpanel&&FusionCharts("chartId-MSLine" + me.id) != undefined) {
                FusionCharts("chartId-MSLine" + me.id).dispose();
        }
    },*/

    initChart: function (datas) {
        var me = this;
        if (me.angularGaugeChart) { //仪表盘

            /*if (FusionCharts("chartId-AngularGauge" + me.id) != undefined) {
                FusionCharts("chartId-AngularGauge" + me.id).dispose();
            }*/

            me.upContainer.remove(me.angularGaugeChart, true);
        }
        
        /*me.angularGaugeChart = Ext.create('Ext.panel.Panel', {
            border: true,
            chartid: "chartId-AngularGauge" + me.id,
            layout: 'fit',
            divid: "divId-AngularGauge" + me.id,
            style: 'padding:5px 5px 0px 5px',
            xmldata: data.xml,
            flex: 0.3,
            listeners: {
                afterrender: function (c, opts) {
                    me.achart = new FusionCharts(__ctxPath + '/images/chart/' + 'AngularGauge' + '.swf', c.chartid, '100%', '100%', "0", "1", "FFFFFF","exactFit");
                    me.achart.setXMLData(c.xmldata);
                    me.achart.render(me.angularGaugeChart.id + '-body');
                }
            }
        });*/
        
        me.angularGaugeChart = Ext.create('FHD.view.kpi.cmp.chart.HightChartAngularGauge',{
        	data:datas.angularMap
        });


        if (me.barChartPanel) {
            /*if (FusionCharts("chartId-bar" + me.id) != undefined) {
                FusionCharts("chartId-bar" + me.id).dispose();
            }*/
            me.upContainer.remove(me.barChartPanel, true);

        }
       /* me.barChartPanel = Ext.create('Ext.panel.Panel', {
            border: true,
            chartid: "chartId-bar" + me.id,
            layout: 'fit',
            divid: "divId-bar" + me.id,
            style: 'padding:5px 5px 0px 5px',
            xmldata: data.relKpiXml,
            flex: 0.7,
            listeners: {
                afterrender: function (c, opts) {
                    me.bchart = new FusionCharts(__ctxPath + '/images/chart/' + 'Bar2D' + '.swf', c.chartid, '100%', '100%', "0", "1", "FFFFFF");
                    me.bchart.setXMLData(c.xmldata);
                    me.bchart.render(c.id + '-body');
                }
            }
        });*/
        
        me.barChartPanel = Ext.create('FHD.view.kpi.cmp.chart.HightChartBar',{
        	data:datas.barMap
        });

        if (me.downpanel) {
            /*if (FusionCharts("chartId-MSLine" + me.id) != undefined) {
                FusionCharts("chartId-MSLine" + me.id).dispose();
            }*/
            me.downContainer.remove(me.downpanel, true);
        }

        me.downpanel = Ext.create('FHD.view.kpi.cmp.chart.HightChartScHistroyTrend',{
        	data:datas.histroyMap
        });
        if(me.upContainer){
        	me.remove(me.upContainer,true);
        }
        if(me.downContainer){
        	me.remove(me.downContainer,true);
        }
        
        me.upContainer = Ext.create('Ext.container.Container', {
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch'
            }
        });
        

        me.downContainer = Ext.create('Ext.container.Container', {
            flex: 1,
            layout:'fit'
        });
        
        me.add(me.upContainer);
        
        me.add(me.downContainer);
        
        
        /*me.downpanel = Ext.create('Ext.panel.Panel', {
            border: true,
            chartid: "chartId-MSLine" + me.id,
            layout: 'fit',
            divid: "divId-MSLine" + me.id,
            style: 'padding:5px 5px 0px 5px',
            xmldata: data.histXml,
            listeners: {
                afterrender: function (c, opts) {
                    me.mchart = new FusionCharts(__ctxPath + '/images/chart/' + 'MSLine' + '.swf', c.chartid, '100%', '100%', "0", "1", "FFFFFF");
                    me.mchart.setXMLData(c.xmldata);
                    me.mchart.render(c.id + '-body');
                }
            }
        });*/


        me.upContainer.add(me.angularGaugeChart);

        me.upContainer.add(me.barChartPanel);

        me.downContainer.add(me.downpanel);

    },

    //获取当前年份
    getYear: function () {
        var myDate = new Date();
        var year = myDate.getFullYear();
        return year;
    },
    
    load:function(){
    	var me = this;
    	me.reloadData();
    },

    //重新加载数据
    reloadData: function () {
        var me = this;

        var paraobj = {};
        paraobj.isNewValue = FHD.data.isNewValue
        if (FHD.data.yearId == '') {
            paraobj.yearId = this.getYear();
        } else {
            paraobj.yearId = FHD.data.yearId;
        }
        paraobj.monthId = FHD.data.monthId;
        paraobj.objectId = me.paramObj.objectId;
        paraobj.dataType = me.paramObj.dataType;
        paraobj.quarterId = FHD.data.quarterId,
        paraobj.weekId = FHD.data.weekId,

        FHD.ajax({
            url: __ctxPath + '/kpi/findchartAngularGaugePanel.f',
            params: {
                condItem: Ext.JSON.encode(paraobj)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.initChart(data);
                }
            }
        });
    }
});