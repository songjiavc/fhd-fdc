Ext.define('FHD.view.kpi.bpm.plan.DeptPlanKpiGridPanel', {
    extend: 'FHD.ux.GridPanel',
    planId: '',
    queryUrl: '',
    paramObj: {

    },
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },
    reloadData: function () {
        var me = this;
        me.store.proxy.extraParams.planId = me.paramObj.planId;
        me.store.proxy.extraParams.deptId = me.paramObj.deptId;
        me.store.load();
    },
    initComponent: function () {
        var me = this;
        if (me.isTarget) {
            me.valueCol = {
                header: '目标值',
                dataIndex: 'targetValue',
                sortable: true,
                flex: 1,
                menuDisabled: true
            }
        } else {
            me.valueCol = {
                header: '完成值',
                dataIndex: 'finishValue',
                sortable: true,
                flex: 1,
                menuDisabled: true
            }
        }
        me.cols = [{
            header: 'deptId',
            dataIndex: 'deptId',
            sortable: true,
            flex: 1,
            invisible: true           
        }, {
            header: 'kpiId',
            dataIndex: 'kpiId',
            sortable: true,
            flex: 1,
            invisible: true
        }, {
            header: '部门名称',
            dataIndex: 'deptName',
            sortable: false,
            flex: 1,
            menuDisabled: true
        }, {
            header: '指标名称',
            dataIndex: 'kpiName',
            sortable: false,
            flex: 2,
            menuDisabled: true
        }, {
            header: '采集人',
            dataIndex: 'empName',
            sortable: false,
            flex: 1,
            menuDisabled: true
        }, {
            header: '操作',
            dataIndex: 'caozuo',
            hidden: false,
            editor: false,
            align: 'center', //必须有dataIndex，否则不能导出Excel
            xtype: 'actioncolumn',
            items: [{
                icon: __ctxPath + '/images/icons/delete_icon.gif', // Use a URL in the icon config
                tooltip: FHD.locale.get('fhd.common.del'),
                handler: function (grid, rowIndex, colIndex) {
                    grid.getSelectionModel().deselectAll();
                    var rows = [grid.getStore().getAt(rowIndex)];
                    grid.getSelectionModel().select(rows, true);
                    me.delKpis(me);
                }
            }]
        }];
        me.tbar = [{
                btype: 'add',
                handler: function () {
                    me.addKpi()
                }
            }, {
                btype: 'delete',
                name: 'kpi_delete',
                disabled: true,
                handler: function () {
                    me.delKpis(me);
                }
            }
            //        	{
            //            iconCls: 'icon-ibm-action-export-to-excel',
            //            text: '导出',
            //            handler: function () {
            //                me.exportChart();
            //            }
            //        }
        ];
        Ext.apply(me, {
            tbarItems: me.tbar,
            multiSelect: true,
            border: false,
            rowLines: true, // 显示横向表格线
            columnLines: true,
            checked: true, // 复选框
            autoScroll: true,
            cols: me.cols, // cols:为需要显示的列
            extraParams: me.extraParams,
            url: me.queryUrl,
            pagable: false
        });

        me.callParent(arguments);
        me.on('selectionchange', function () {
            me.setstatus(me)
        });
        me.on('afterlayout', function () {
            Ext.create('FHD.view.risk.assess.utils.GridCells').mergeCells(me, [2]);
        });
    },
    //设置按钮可用状态
    setstatus: function (me) {
        me.down("[name='kpi_delete']").setDisabled(me.getSelectionModel().getSelection().length === 0);
    },
    exportChart: function () {

    },
    delKpis: function (me) {
        var selection = me.getSelectionModel().getSelection(); //得到选中的记录
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    var ids = [];
                    var objIds = [];
                    for (var i = 0; i < selection.length; i++) {
                        ids.push(selection[i].get('kpiId'));
                    }
                    me.body.mask("删除中...", "x-mask-loading");
                    FHD.ajax({ //ajax调用
                        url: __ctxPath + '/kpi/plan/removeplanrelakpiByIds.f',
                        params: {
                            ids: ids.join(','),
                            planId: me.planId
                        },
                        callback: function (data) {
                            if (data) { //删除成功！
                                me.body.unmask();
                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
                                me.reloadData();
                            }
                        }
                    });
                }
            }
        });
    },
    addKpi: function () {
        var me = this;
        var gType = null;
        // 生成指标频率
        if ('0checktype_y' == me.checkType) {
            gType = '0frequecy_year';
        } else if ('0checktype_m' == me.checkType) {
            gType = '0frequecy_month';
        } else if ('0checktype_w' == me.checkType) {
            gType = '0frequecy_week';
        } else if ('0checktype_q' == me.checkType) {
            gType = '0frequecy_quarter';
        }
        var selectorWindow = Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow', {
            multiSelect: true,
            gType: gType,
            deptId: me.paramObj.deptId,
            smtreevisable: false,
            categorytreevisable: false,
            kpitypetreevisable: false,
            myfoldertreevisable: false,
            singledepttreevisable: true,
            onSubmit: function (store) {
                var idArray = [];
                var items = store.data.items;
                Ext.Array.each(items, function (item) {
                    idArray.push(item.data.id);
                });
                if (idArray.length > 0) {
                    var paraobj = {
                        planId: me.planId,
                        kpiIds: idArray
                    };
                    FHD.ajax({
                        url: __ctxPath + '/kpi/plan/mergeplanrelakpi.f',
                        params: {
                            param: Ext.JSON.encode(paraobj)
                        },
                        callback: function (data) {
                            if (data && data.success) {
                                me.store.load();
                            }
                        }
                    });
                }

            }
        }).show();
        selectorWindow.addComponent();
    }
});