Ext.define('FHD.view.kpi.cmp.sm.SmGrid', {
    extend: 'FHD.ux.GridPanel',
    extraParams: {},
    layout: 'fit',
    border: false,
	requires:['FHD.ux.TipColumn'],
    enables: function (value) {

    },
    focus: function (value) {

    },
    initComponent: function () {
        var me = this;
        me.queryUrl = __ctxPath + '';
        var focuspanel = Ext.widget('container');
        var statuspanel = Ext.widget('container');
        // 显示列
        me.cols = [{
                cls: 'grid-icon-column-header grid-statushead-column-header',
                header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
                dataIndex: 'assessmentStatus',
                sortable: true,
                width: 40,
                renderer: function (v) {
                    var color = "";
                    var display = "";
                    if (v == "icon-ibm-symbol-4-sm") {
                        color = "symbol_4_sm";
                        display = FHD.locale.get("fhd.alarmplan.form.hight");
                    } else if (v == "icon-ibm-symbol-6-sm") {
                        color = "symbol_6_sm";
                        display = FHD.locale.get("fhd.alarmplan.form.low");
                    } else if (v == "icon-ibm-symbol-5-sm") {
                        color = "symbol_5_sm";
                        display = FHD.locale.get("fhd.alarmplan.form.min");
                    } else if (v == "icon-ibm-symbol-safe-sm") {
                        display = "安全";
                    } else {
                        v = "icon-ibm-underconstruction-small";
                        display = "无";
                    }
                    return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                        "background-position: center top;' data-qtitle='' " +
                        "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
                }
            }, {
                cls: 'grid-icon-column-header grid-trendhead-column-header',
                header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
                dataIndex: 'directionstr',
                sortable: true,
                width: 40,
                renderer: function (v) {
                    var color = "";
                    var display = "";
                    if (v == "icon-ibm-icon-trend-rising-positive") {
                        color = "icon_trend_rising_positive";
                        display = FHD.locale.get("fhd.kpi.kpi.prompt.positiv");
                    } else if (v == "icon-ibm-icon-trend-neutral-null") {
                        color = "icon_trend_neutral_null";
                        display = FHD.locale.get("fhd.kpi.kpi.prompt.flat");
                    } else if (v == "icon-ibm-icon-trend-falling-negative") {
                        color = "icon_trend_falling_negative";
                        display = FHD.locale.get("fhd.kpi.kpi.prompt.negative");
                    }
                    return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
                        "background-position: center top;' data-qtitle='' " +
                        "class='" + v + "'  data-qtip='" + display + "'></div>";
                }
            }, {
                header: FHD.locale.get('fhd.kpi.kpi.form.name'),
                dataIndex: 'name',
                sortable: true,
                flex: 3,
                renderer: function (v, rowIndex, cellIndex) {
                    return "<div style='width: 100px; height: 19px; background-repeat: no-repeat;" +
                        "background-position: center top;' data-qtitle=''   data-qtip='" + v + "'></div>";
                }
            }, {
                header: FHD.locale.get('fhd.kpi.kpi.form.assessmentValue'),
                dataIndex: 'assessmentValue',
                sortable: true,
                flex: 1.1,
                align: 'right'
            }, {
                header: FHD.locale.get('fhd.kpi.kpi.form.dateRange'),
                dataIndex: 'dateRange',
                sortable: true,
                flex: 1,
                renderer: function (v) {
                    return "<div data-qtitle='' data-qtip='" + v + "'>" + v + "</div>";
                }
            }, {
                header: FHD.locale.get('fhd.sys.planMan.start'),
                xtype: 'tipcolumn',
                tips: {
                    items: [statuspanel],
                    renderer: function (cellIndex, rowIndex, tooltip) {
                        var data = me.items.items[0].store.data.items[rowIndex].data.kpistatus;
                        tooltip.setWidth(75);
                        tooltip.setHeight(30);
                        var htmlstr = "启用";
                        if (data == "0yn_y") {
                            htmlstr = "停用";
                        }
                        var p = statuspanel.items.items[0];
                        statuspanel.remove(p);
                        statuspanel.add({
                            border: false,
                            html: htmlstr
                        });
                    }
                },
                dataIndex: 'statusStr',
                sortable: false,
                flex: 0.5,
                renderer: function (v) {
                    var type = me.type;
                    if ("0yn_y" == v) {
                        return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').enables('" + '0yn_n' + "')\" >" + "<image src='images/icons/state_ok.gif'/>" + "</a>";
                    }
                    if ("0yn_n" == v || "" == v) {
                        return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').enables('" + '0yn_y' + "')\" >" + "<image src='images/icons/state_error.gif'/>" + "</a>";
                    }
                }
            }, {
                header: '关注',
                xtype: 'tipcolumn',
                tips: {
                    items: [focuspanel],
                    renderer: function (cellIndex, rowIndex, tooltip) {
                        var data = me.items.items[0].store.data.items[rowIndex].data.kpifocus;
                        tooltip.setWidth(75);
                        tooltip.setHeight(30);
                        var htmlstr = "关注";
                        if (data == "0yn_y") {
                            htmlstr = "取消关注";
                        }
                        var p = focuspanel.items.items[0];
                        focuspanel.remove(p);
                        focuspanel.add({
                            border: false,
                            html: htmlstr
                        });
                    }
                },
                dataIndex: 'focusStr',
                sortable: false,
                flex: 0.5,
                renderer: function (v) {
                    var type = me.type;
                    if ("0yn_y" == v) {
                        return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').focus('" + '0yn_n' + "')\" >" + "<image src='images/icons/kpi_heart.png' />" + "</a>";
                    }
                    if ("0yn_n" == v || "" == v) {
                        return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').focus('" + '0yn_y' + "')\" >" + "<image src='images/icons/kpi_heart_add.png' />" + "</a>";
                    }
                }

            }

        ];
        Ext.apply(me, {
            multiSelect: true,
            border: false,
            rowLines: true, // 显示横向表格线
            columnLines: true,
            checked: false, // 复选框
            autoScroll: true,
            cols: me.cols, // cols:为需要显示的列
            extraParams: me.extraParams,
            url: me.queryUrl,
            pagable: false
        });

        me.callParent(arguments);
    }
})