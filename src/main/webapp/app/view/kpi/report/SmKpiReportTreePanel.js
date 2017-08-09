Ext.define('FHD.view.kpi.report.SmKpiReportTreePanel', {
    extend: 'Ext.panel.Panel',
    hasQuery: true,
    hasHeader: true,
    root: {
        "id": "sm_root",
        "itemName": FHD.locale.get('fhd.sm.strategymaps'),
        "dbid": "sm_root",
        "leaf": false,
        "code": "sm",
        "type": "sm",
        "expanded": true,
        'iconCls': 'icon-strategy'
    },
    initComponent: function () {
        var me = this;
        var cols = [{
            dataIndex: 'id',
            hidden: true
        }, {
            text: '名称',
            dataIndex: 'itemName',
            flex: 2,
            hideable: false,
            sortable: false,
            xtype: 'treecolumn'
        }, 
        {
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
        },{
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
        }, 
//        {
//            text: '指标查看',
//            dataIndex: 'kpiCheck',
//            flex: 0.5,
//            hideable: false,
//            sortable: false,
//            align: 'center',
//            renderer: function (value, metaData, record, colIndex, store, view) {
//                var id = record.get('id');
//                var href = "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showscrelakpi('" + id + "')\" >";
//                if ('sm_root' != id && record.data.kpiCount) {
//                    return href + "<image src='images/icons/icon_listview.gif'/>" + 　"</a>";
//                }
//            }
//        }
        {
            text: '关联指标数量',
            dataIndex: 'kpiCount',
            hidden: true,
            sortable: false
        },
        {
            text: '类型',
            dataIndex: 'type',
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
            border: false,
            cols: cols,
            root: me.root,
            header: false,
            name: 'reportSmKpi',
            url: __ctxPath + '/kpi/report/smKpiReportTreeLoader.f',
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
                        if (cellIndex == 1 && record.data.id != 'sm_root' && record.data.kpiCount) {

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
        }
        menu.add(nofocusmenu);
        return menu;
    },
    showscrelakpi: function (id) {
        var kpiGridPanel = Ext.create('FHD.view.kpi.report.SmKpiReportGrid', {
            smid: id
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
    },
	reLoadData : function() {
		var me = this;
		me.treegrid.store.load();
	}
})