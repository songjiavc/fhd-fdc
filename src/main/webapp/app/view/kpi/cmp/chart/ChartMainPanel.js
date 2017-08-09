Ext.define('FHD.view.kpi.cmp.chart.ChartMainPanel', {
    extend: 'FHD.ux.CardPanel',
    activeItem: 0,

    border: false,

    paramObj: {
        objectId: '', //目标ID
        parentid: '', //父目标ID
        parentname: '', //父目标名称
        dataType: '',
        showType: []
    },
    tbar: {},

    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    initComponent: function () {
        var me = this;

        var paramObj = {};
        paramObj.dataType = me.dataType;
        paramObj.parentName = me.parentname;

        me.initParam(paramObj);

        me.angularGaugePanel = Ext.create('FHD.view.kpi.cmp.chart.AngularGaugePanel');
//        me.multiDimComparePanel = Ext.create('FHD.view.kpi.cmp.chart.MultiDimComparePanel');
        me.multiDimComparePanel = Ext.create('FHD.view.kpi.cmp.chart.HightChartMultiDimCompare');
//        me.trendPanel = Ext.create('FHD.view.kpi.cmp.chart.TrendPanel');
        me.trendPanel = Ext.create('FHD.view.kpi.cmp.chart.HightChartKpiTrend');
//        me.structuralAnalysisPanel = Ext.create('FHD.view.kpi.cmp.chart.StructuralAnalysisPanel');
        me.structuralAnalysisPanel = Ext.create('FHD.view.kpi.cmp.chart.HightChartKpiStruct');

        Ext.applyIf(me, {
            items: [
                me.angularGaugePanel, me.multiDimComparePanel, me.trendPanel, me.structuralAnalysisPanel
            ],
            listeners: {
                destroy: function (me, eOpts) {
                    me.removeAll(true);
                    me = null;
                    if (Ext.isIE) {
                        CollectGarbage();
                    }
                }
            }

        });


        me.callParent(arguments);
    },

    getChartTbar: function () {
        var me = this;

        if ('str' == me.dataType) {
            FHD.ajax({
                async: false,
                url: __ctxPath + '/kpi/kpistrategymap/findcharttypebyid.f',
                params: {
                    id: me.paramObj.objectId
                },
                callback: function (data) {
                    if (data && data.success && data.chartType != "") {
                        var showType = data.chartType.split(',');
                        if (showType.length > 0) {
                            me.showType = showType;
                            me.setStrChartTbar(me.showType);
                        }
                    } else {
                        me.setStrChartTbar(null);
                    }
                }
            });
        }
        if ('sc' == me.dataType) {
            FHD.ajax({
                async: false,
                url: __ctxPath + '/kpi/category/findcharttypebyid.f',
                params: {
                    id: me.paramObj.objectId
                },
                callback: function (data) {
                    if (data && data.success && data.chartType != "") {
                        var showType = data.chartType.split(',');
                        if (showType.length > 0) {
                            me.showType = showType;
                            me.setScChartTbar(me.showType);
                        }
                    } else {
                        me.setScChartTbar(null);
                    }
                }
            });
        }

    },

    //设置目标图表按钮tbar
    setStrChartTbar: function (showType) {
        var me = this;
        me.topbar = me.getDockedComponent(0);
        var btnLength = me.topbar.items.length;
        for (var j = 0; j < btnLength; j++) {
            me.topbar.remove(0);
        }
        var chartIdArray = showType;
        if (undefined != chartIdArray) {
            if (chartIdArray.length > 0) {
                //根据图表数据字典id设置显示的图表
                for (var i = 0; i < chartIdArray.length; i++) {
                    //中间加分隔符
                    if (i != 0) {
                        me.topbar.add('-');
                    }

                    if ('strategy_map_chart_type_2' == chartIdArray[i]) {
                        var augalarGaugeButton = {
                            name: 'strategy_map_chart_type_2',
                            text: FHD.locale.get('fhd.kpi.chart.angularGauge'),
                            icon: __ctxPath + '/images/icons/rainbow.png',
                            handler: function () {
                                me.setBtnState('strategy_map_chart_type_2');
                                me.setActiveItem(me.angularGaugePanel);
                            }
                        };
                        //if($ifAllGranted('ROLE_ALL_MONITOR_KPIMONITOR_PANEL')){
        					 me.topbar.add(augalarGaugeButton);
        				//}
                    } else if ('strategy_map_chart_type_1' == chartIdArray[i]) {
                        var multiDimCompareButton = {
                            name: 'strategy_map_chart_type_1',
                            text: FHD.locale.get('fhd.kpi.chart.trendAnalysis'),
                            icon: __ctxPath + '/images/icons/chart_bar.png',
                            handler: function () {
                                me.setBtnState('strategy_map_chart_type_1');
                                me.setActiveItem(me.trendPanel);
                            }
                        };
                        //if($ifAllGranted('ROLE_ALL_MONITOR_KPIMONITOR_TREND')){
	                        me.topbar.add(multiDimCompareButton);
        				//}
                    }
                }

                if ('strategy_map_chart_type_2' == chartIdArray[0]) {
                    me.setBtnState('strategy_map_chart_type_2');
                    me.setActiveItem(me.angularGaugePanel);
                } else if ('strategy_map_chart_type_1' == chartIdArray[0]) {
                    me.setBtnState('strategy_map_chart_type_1');
                    me.setActiveItem(me.trendPanel);
                }
            }

        } else {
            me.setBtnState('strategy_map_chart_type_2');
            var augalarGaugeButton = {
                name: 'strategy_map_chart_type_2',
                text: FHD.locale.get('fhd.kpi.chart.angularGauge'),
                icon: __ctxPath + '/images/icons/rainbow.png',
                handler: function () {
                    me.setBtnState('strategy_map_chart_type_2');
                    me.setActiveItem(me.angularGaugePanel)
                }
            };
            
            //if($ifAllGranted('ROLE_ALL_MONITOR_KPIMONITOR_PANEL')){
        		me.topbar.add(augalarGaugeButton);
        	//}
            
            me.setActiveItem(me.angularGaugePanel);
            //如果没有按钮，默认添加仪表板
            me.setBtnState('strategy_map_chart_type_2');
        }

    },

    //设置记分卡图表按钮tbar
    setScChartTbar: function (showType) {
        var me = this;
        me.topbar = me.getDockedComponent(0);
        var btnLength = me.topbar.items.length;
        for (var j = 0; j < btnLength; j++) {
            me.topbar.remove(0);
        }
        var chartIdArray = showType;
        if (undefined != chartIdArray) {
            if (chartIdArray.length > 0) {
                //根据图表数据字典id设置显示的图表
                for (var i = 0; i < chartIdArray.length; i++) {
                    //中间加分隔符
                    if (i != 0) {
                        me.topbar.add('-');
                    }

                    if ('0com_catalog_chart_type_1' == chartIdArray[i]) {
                        var augalarGaugeButton = {
                            name: '0com_catalog_chart_type_1',
                            text: FHD.locale.get('fhd.kpi.chart.angularGauge'),
                            icon: __ctxPath + '/images/icons/rainbow.png',
                            handler: function () {
                                me.setBtnState('0com_catalog_chart_type_1');
                                me.setActiveItem(me.angularGaugePanel);
                            }
                        };
                        //if($ifAllGranted('ROLE_ALL_REVIEW_RELEASE_PANEL')){
                        	me.topbar.add(augalarGaugeButton);
                        //}
                        
                    } else if ('0com_catalog_chart_type_3' == chartIdArray[i]) {
                        var multiDimCompareButton = {
                            name: '0com_catalog_chart_type_3',
                            text: FHD.locale.get('fhd.kpi.chart.multiDimCompare'),
                            icon: __ctxPath + '/images/icons/chart_bar.png',
                            handler: function () {
                                me.setBtnState('0com_catalog_chart_type_3');
                                me.setActiveItem(me.multiDimComparePanel);
                            }
                        };
                        //if($ifAllGranted('ROLE_ALL_REVIEW_RELEASE_CONTRAST')){
                			me.topbar.add(multiDimCompareButton);
           				//}
                        
                    } else if ('0com_catalog_chart_type_4' == chartIdArray[i]) {
                        var trendButton = {
                            name: '0com_catalog_chart_type_4',
                            text: FHD.locale.get('fhd.kpi.chart.trendAnalysis'),
                            icon: __ctxPath + '/images/icons/chart_trend.png',
                            handler: function () {
                                me.setBtnState('0com_catalog_chart_type_4');
                                me.setActiveItem(me.trendPanel);
                            }
                        };
                        //if($ifAllGranted('ROLE_ALL_REVIEW_RELEASE_TREND')){
                			me.topbar.add(trendButton);
           				//}
                        
                    } else if ('0com_catalog_chart_type_5' == chartIdArray[i]) {
                        var structuralAnalysisButton = {
                            name: '0com_catalog_chart_type_5',
                            text: FHD.locale.get('fhd.kpi.chart.structuralAnalysis'),
                            icon: __ctxPath + '/images/icons/chart_pie.png',
                            handler: function () {
                                me.setBtnState('0com_catalog_chart_type_5');
                                me.setActiveItem(me.structuralAnalysisPanel);
                            }
                        };
                        //if($ifAllGranted('ROLE_ALL_REVIEW_RELEASE_STRUCTURE')){
                			me.topbar.add(structuralAnalysisButton);
           				//}
                        
                    }
                }

                if (chartIdArray.length > 0) {
                    if ('0com_catalog_chart_type_1' == chartIdArray[0]) {
                        me.setBtnState('0com_catalog_chart_type_1');
                        me.setActiveItem(me.angularGaugePanel);
                    } else if ('0com_catalog_chart_type_3' == chartIdArray[0]) {
                        me.setBtnState('0com_catalog_chart_type_3');
                        me.setActiveItem(me.multiDimComparePanel);
                    } else if ('0com_catalog_chart_type_4' == chartIdArray[0]) {
                        me.setBtnState('0com_catalog_chart_type_4');
                        me.setActiveItem(me.trendPanel);
                    } else if ('0com_catalog_chart_type_5' == chartIdArray[0]) {
                        me.setBtnState('0com_catalog_chart_type_5');
                        me.setActiveItem(me.structuralAnalysisPanel);
                    }
                }
            }
        } else {
            me.setBtnState('0com_catalog_chart_type_1');
            var augalarGaugeButton = {
                name: '0com_catalog_chart_type_1',
                text: FHD.locale.get('fhd.kpi.chart.angularGauge'),
                icon: __ctxPath + '/images/icons/rainbow.png',
                handler: function () {
                    me.setBtnState('0com_catalog_chart_type_1');
                    me.setActiveItem(me.angularGaugePanel);
                }
            };
            
            //if($ifAllGranted('ROLE_ALL_REVIEW_RELEASE_PANEL')){
                 me.topbar.add(augalarGaugeButton);
            //}
                        
            me.setActiveItem(me.angularGaugePanel);
            
            me.setBtnState('0com_catalog_chart_type_1');
        }

    },
    //设置按钮状态
    setBtnState: function (bname) {
        var me = this;
        var k = 0;
        var topbar = me.topbar;
        var btns = topbar.items.items;
        for (var i = 0; i < btns.length; i++) {
            var item = btns[i];
            if (item.pressed != undefined) {
                if (item.name == bname) {
                    item.toggle(true);
                } else {
                    item.toggle(false);
                }
                k++;
            }
        }
    },
    //cardpanel切换
    /*navBtnHandler: function (index) {
        var me = this;
        me.setActiveItem(index);
        if (0 == index) {
            //重新加载仪表板图表
            me.angularGaugePanel.initParam(me.paramObj);
            me.angularGaugePanel.reloadData();
        } else if (1 == index) {
            //重新加载多维对比分析图表
            me.multiDimComparePanel.initParam(me.paramObj);
            me.multiDimComparePanel.reloadData();
        } else if (2 == index) {
            //重新加载趋势分析图表
            me.trendPanel.initParam(me.paramObj);
            me.trendPanel.reloadData();
        } else if (3 == index) {
            //重新加载趋势分析图表
            me.structuralAnalysisPanel.initParam(me.paramObj);
            me.structuralAnalysisPanel.reloadData();
        }
    },*/

    //重新加载数据
    reloadData: function () {
        var me = this;

        //设置图表分析tbar按钮
        me.getChartTbar();
        var activeItem = me.getActiveItem();
        me.activeid = activeItem.id;
        me.angularGaugePanel.initParam(me.paramObj);
        //重新加载仪表板图表
        me.angularGaugePanel.reloadData();

        me.multiDimComparePanel.initParam(me.paramObj);
        //重新加载多维对比分析图表
        me.multiDimComparePanel.reloadData();

        me.trendPanel.initParam(me.paramObj);
        //重新加载多维对比分析图表
        me.trendPanel.reloadData();

        me.structuralAnalysisPanel.initParam(me.paramObj);
        //重新加载多维对比分析图表
        me.structuralAnalysisPanel.reloadData();


    }
});