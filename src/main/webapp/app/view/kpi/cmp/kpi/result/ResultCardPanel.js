/**
 * 结果图片面板
 *
 * @author 王鑫
 */
Ext.define('FHD.view.kpi.cmp.kpi.result.ResultCardPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.resultCardPanel',

    timeRefresh: true,

    load: function (param) {
        var me = this;
        me.resultParam = param;
        //向cardPanel存放图表、表格组件
        me.addComponents(me);
        return me;
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        Ext.apply(me, {
            xtype: 'cardpanel',
            border: false,
            activeItem: 0,
            items: [],
            listeners: {
                destroy: function (me, eOpts) {
                    me.remove(me.chartContainer,true);
                    me.remove(me.diffchartContainer,true);
                    me = null;
                    if (Ext.isIE) {
                        CollectGarbage();
                    }
                }
            },
            tbar: {
                items: [{
                        name: 'tbarChartsButtonId',
                        text: '趋势图',
                        iconCls: 'icon-chart-trendline',
                        handler: function () {
                            me.charts(me);
                        }
                    }, '-', {
                        name: 'tbardiffChartsButtonId',
                        text: '对比图',
                        iconCls: 'icon-chart-bar',
                        handler: function () {
                            me.diffcharts(me);
                        }
                    },
                    '->', {
                        name: 'gatherresulttableinputsave',
                        text: '保存',
                        hidden: true,
                        iconCls: 'icon-page-save',
                        handler: function () {
                            me.tablePanel.save();
                        }
                    }, {
                        text: '返回',
                        iconCls: 'icon-control-repeat-blue',
                        handler: function () {
                            me.goback();
                        }
                    }
                ]
            }
        });

        me.callParent(arguments);
    },

    addComponents: function (me) {
        if (!me.resultParam.paraobj) {
            me.resultParam.paraobj = {};
        }
        me.resultParam.paraobj.eType = FHD.data.eType;
        me.resultParam.paraobj.kpiname = me.resultParam.kpiname;
        FHD.data.kpiName = me.resultParam.kpiname;
        me.resultParam.paraobj.timeId = me.resultParam.timeId;
        me.resultParam.paraobj.year = FHD.data.yearId;
        me.resultParam.paraobj.isNewValue = FHD.data.isNewValue;
        me.resultParam.paraobj.containerId = me.id;
        FHD.data.edit = false;

        if (me.items.items.length != 0) {
            me.removeAll();
        }
        me.chartContainer = Ext.create('FHD.view.kpi.cmp.kpi.result.ChartContainer', {
            itemobj: me.resultParam.paraobj
        });
        me.diffchartContainer = Ext.create('FHD.view.kpi.cmp.kpi.result.DiffChartContainer', {
            itemobj: me.resultParam.paraobj
        });


        me.add(me.chartContainer);

        me.add(me.diffchartContainer);

        me.doLayout();

        me.charts(me);

    },


    charts: function (me) {
        me.getLayout().setActiveItem(me.items.items[0]);
        me.down("[name='tbarChartsButtonId']").toggle(true);
        me.down("[name='tbardiffChartsButtonId']").toggle(false);
    },
    diffcharts: function (me) {
        me.getLayout().setActiveItem(me.items.items[1]);
        me.down("[name='tbardiffChartsButtonId']").toggle(true);
        me.down("[name='tbarChartsButtonId']").toggle(false);
    },

    list: function (me) {
        me.getLayout().setActiveItem(me.items.items[2]);
        me.down("[name='tbardiffChartsButtonId']").toggle(false);
        me.down("[name='tbarChartsButtonId']").toggle(false);
    }
});