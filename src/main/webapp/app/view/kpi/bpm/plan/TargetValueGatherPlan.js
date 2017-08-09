Ext.define('FHD.view.kpi.bpm.plan.TargetValueGatherPlan', {
    extend: 'FHD.ux.EditorGridPanel',
    isEdit: true,// 是否只是窗口展示 如果是则不含按钮 
    hasDateRange: true,
    hasUniqueTitle: '采集指标选择',
    submitPlanByParams: function (startTime, checkType) {
        var me = this;
        if (startTime) {
            startTime = Ext.Date.format(new Date(startTime), 'Y-m-d');
        }
        me.body.mask("提交中...", "x-mask-loading");
        FHD.ajax({
            url: __ctxPath + '/kpi/plan/startKpiGahterProcess.f',
            params: {
                planId: me.planId,
                gatherType: me.gatherType,
                startTime: startTime,
                checkType: checkType
            },
            callback: function (data) {
                //						if (data && data.success) {
                //							 me.startTime = data.startTime;
                //							 me.gatherType = data.gatherType;
                //							 me.workState = data.workState;
                //						}
                var prt = me.up('planConformCard');
                if (prt) {
                    prt.planConformGrid.store.load();
                    //取消列表已选中的列，解决提交后未刷新的重复修改问题
                    prt.planConformGrid.getSelectionModel().deselectAll(true);
                    prt.showPlanConformGrid();
                }
            }
        });
    },
    showDeptKpiDetail: function (deptId) {
        var me = this;
        var paramObj = {};
        paramObj.deptId = deptId;
        paramObj.planId = me.planId;
        var checkType = me.up('panel').checkFre.getValue();
        me.deptPlanKpi = Ext.create('FHD.view.kpi.bpm.plan.DeptPlanKpiGridPanel', {
            queryUrl: __ctxPath + '/kpi/plan/findDeptPlanKpiById.f',
            planId: me.planId,
            checkType: checkType
        });
        me.deptPlanKpi.initParam(paramObj);
        me.deptPlanKpi.reloadData();
        me.window = Ext.create('FHD.ux.Window', {
            title: '部门考核指标明细',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true
        });
        me.window.show();
        me.window.on('close', function () {
            me.store.load()
        });
        me.window.add(me.deptPlanKpi);

    },
    addKpi: function () {
        var me = this;
        var gType = null;
        var checkType = me.up('panel').checkFre.getValue();
        // 生成指标频率
        if ('0checktype_y' == checkType) {
            gType = '0frequecy_year';
        } else if ('0checktype_m' == checkType) {
            gType = '0frequecy_month';
        } else if ('0checktype_w' == checkType) {
            gType = '0frequecy_week';
        } else if ('0checktype_q' == checkType) {
            gType = '0frequecy_quarter';
        }
        var selectorWindow = Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow', {
            multiSelect: true,
            gType: gType,
            onSubmit: function (store) {
                var idArray = [];
                var items = store.data.items;
                Ext.Array.each(items, function (item) {
                    idArray.push(item.data.id);
                });
                if (idArray.length > 0) {
                    var paraobj = {
                        planId: me.planId,
                        kpiIds: idArray,
                        gatherType: me.gatherType
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
    },
    //加载列表数据
    loadData: function (planId) {
        var me = this;
        FHD.ajax({
            url: __ctxPath + '/kpi/plan/findplanchecktimebyid.f',
            params: {
                planId: planId
            },
            async: false,
            callback: function (data) {
                if (data && data.success) {
                    me.startTime = data.startTime;
                    me.checkType = data.checkType;
                    me.gatherType = data.gatherType;
                    me.workState = data.workState;
                }
            }
        });
        me.store.proxy.url = __ctxPath + '/kpi/plan/findstatisticdatabyplanid.f';
        me.store.proxy.extraParams.planId = planId;
        me.store.proxy.extraParams.gatherType = me.gatherType;
        me.planId = planId;
        me.store.load();

    },
    initComponent: function () {
        var me = this;
        me.cols = [{
            header: '部门名称',
            dataIndex: 'deptName',
            sortable: true,
            flex: 1
        }, {
            header: '指标数量',
            dataIndex: 'kpiCount',
            sortable: true,
            flex: 1
        },
        /*{
            header: '考核时间',
            dataIndex: 'assessDateRange',
            sortable: false,
            flex: 1
        },
        */{
            dataIndex: 'planId',
            hidden: true
        }, {
            dataIndex: 'orgId',
            hidden: true
        }];
        me.tbar = [];
        if(me.isEdit) {
           me.cols.push({
            header: "操作",
            dataIndex: '',
            sortable: true,
            width: 40,
            flex: 1,
            renderer: function (v, rowIndex, cellIndex) {
                var deptId = cellIndex.data.orgId;
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showDeptKpiDetail('" + deptId + "')\" >" + "查看明细" + "</a>"
            }
        });
           me.tbar = [{
            text: '添加指标',
            iconCls: 'icon-add',
            handler: function () {
                me.addKpi();
            }
        }, {
            text: '按部门添加指标',
            iconCls: 'icon-add',
            handler: function () {
                me.addDeptKpi();
            }
        }, {
            text: '查看全部',
            iconCls: 'icon-scorecards',
            handler: function () {
                me.showAllDeptKpi();
            }
        }];
        }
        Ext.apply(me, {
            cols: me.cols,
            tbarItems: me.tbar,
            border: true,
            columnLines: false,
            checked: false,
            pagable: false,
            searchable: true,
            type: 'editgrid'
        });
        me.callParent(arguments);
    },
    showAllDeptKpi: function () {
        var me = this;
        var paramObj = {};
        paramObj.planId = me.planId;
        var checkType = me.up('panel').checkFre.getValue();
        me.PlanKpi = Ext.create('FHD.view.kpi.bpm.plan.DeptPlanKpiGridPanel', {
            queryUrl: __ctxPath + '/kpi/plan/findDeptPlanKpiById.f',
            planId: me.planId,
            checkType: checkType
        })
        me.planWindow = Ext.create('FHD.ux.Window', {
            title: '部门考核指标明细',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true
        });
        me.planWindow.show();
        me.planWindow.on('close', function () {
            me.store.load()
        });
        me.planWindow.add(me.PlanKpi);
        me.PlanKpi.initParam(paramObj);
        me.PlanKpi.reloadData();
    },
    addDeptKpi: function () {
        var me = this;
        var gType = null;
        var checkType = me.up('panel').checkFre.getValue();
        // 生成指标频率
        if ('0checktype_y' == checkType) {
            gType = '0frequecy_year';
        } else if ('0checktype_m' == checkType) {
            gType = '0frequecy_month';
        } else if ('0checktype_w' == checkType) {
            gType = '0frequecy_week';
        } else if ('0checktype_q' == checkType) {
            gType = '0frequecy_quarter';
        }
        var selectorWindow = Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow', {
            multiSelect: true,
            gType: gType,
            smtreevisable: false,
            categorytreevisable: false,
            kpitypetreevisable: false,
            myfoldertreevisable: false,
            depttreevisable: true,
            onSubmit: function (store) {
                var idArray = [];
                var items = store.data.items;
                Ext.Array.each(items, function (item) {
                    idArray.push(item.data.id);
                });
                if (idArray.length > 0) {
                    var paraobj = {
                        planId: me.planId,
                        kpiIds: idArray,
                        gatherType: me.gatherType
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
})