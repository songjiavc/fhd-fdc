Ext.define('FHD.view.kpi.report.ScKpiReportTreePanel', {
    extend: 'Ext.panel.Panel',
    root: {
        "id": "category_root",
        "leaf": false,
        "expanded": true,
        'iconCls': 'icon-ibm-icon-scorecards',
        "itemName": '记分卡',
        "score": '',
        "scStatus": ''
    },
    initComponent: function () {
        var me = this;
        var cols = [{
            dataIndex: 'id',
            hidden: true
        }, {
            text: '记分卡',
            dataIndex: 'itemName',
            flex: 2,
            hideable: false,
            sortable: false,
            xtype: 'treecolumn'
        },  {
            text: '完成值',
            dataIndex: 'resultValue',
            flex: 1,
            hideable: false,
            sortable: false
        },
        {
            text: '目标值',
            dataIndex: 'targetValue',
            flex: 1,
            hideable: false,
            sortable: false
        }, {
            text: '评估值',
            dataIndex: 'assessmentValue',
            flex: 1,
            hideable: false,
            sortable: false
        }, {
            text: '所属部门',
            dataIndex: 'deptName',
            flex: 1,
            hideable: false,
            sortable: false
        }, {
            text: '时间维度',
            dataIndex: 'timePeriod',
            flex: 1,
            hideable: false,
            sortable: false
        }, {
            text: '关联指标数量',
            dataIndex: 'kpiCount',
            hidden: true,
            sortable: false
        }];
        me.treegrid = Ext.create('FHD.ux.TreeGridPanel', {
            useArrows: true,
            rootVisible: true,
            multiSelect: false,
            border: false,
            rowLines: true,
            checked: false,
            autoScroll: true,
            searchable: false,
            header: false,
            cols: cols,
            root: me.root,
            name: 'reportKpi',
            url:  __ctxPath + '/kpi/report/scKpiReportTreeLoader.f',
            extraParams: {
                year: FHD.data.yearId,
                month: FHD.data.monthId,
                quarter: FHD.data.quarterId,
                week: FHD.data.weekId,
                isNewValue: FHD.data.isNewValue,
                eType: FHD.data.eType
            },
            viewConfig: {
                listeners: {
                    cellcontextmenu: function (view, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                        e.stopEvent();
                        if (cellIndex == 1 && record.data.id != 'category_root' && record.data.kpiCount) {

                            var menu = me.contextItemMenuFun(view, record, null, null, e);
                            menu.showAt(e.getPoint());
                        }

                    }
                }
            }

        });
        Ext.applyIf(me, {
            layout: 'fit',
            border: false,
            items: me.treegrid
        });

        me.callParent(arguments);
    },
    reLoadData: function () {
        var me = this;
//        me.treegrid.url = __ctxPath + '/kpi/report/scKpiReportTreeLoader.f';
//        me.treegrid.store.proxy.url = __ctxPath + '/kpi/report/scKpiReportTreeLoader.f';
        me.treegrid.store.load();
    },
    contextItemMenuFun: function (view, rec, node, index, e) {
        var me = this;
        var menu = Ext.create('Ext.menu.Menu', {
            margin: '0 0 10 0',
            items: []
        });
        var nofocusmenu = {
            iconCls: 'icon-ibm-icon-views',
            text: '查看指标',
            handler: function () {
                me.showscrelakpi(rec.data.id);
            }
        };
        menu.add(nofocusmenu);
        return menu;

    },
    showscrelakpi: function (id) {
        var kpiGridPanel = Ext.create('FHD.view.kpi.report.ScKpiReportGrid', {
            scid: id
        });
        kpiGridPanel.reLoadData();
        var window = Ext.create('FHD.ux.Window', {
            title: name,
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true
        });
        window.add(kpiGridPanel);
        window.show();
    }
})