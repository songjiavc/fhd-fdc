Ext.define('FHD.view.kpi.cmp.kpi.KpiCard', {
    extend: 'FHD.ux.CardPanel',
    border: true,
    activeItem: 0,
    backType: '',
    requires: [],

    /**
     * 返回按钮事件
     */
    undo: function () {},

    /**
     * 下一步按钮事件
     */
    last: function () {
        var me = this;
        var activePanel = me.getActiveItem();
        if (activePanel.last) {
            activePanel.last(me);
        }
    },

    /**
     * 完成按钮事件
     */
    finish: function () {
        var me = this;
        me.setBtnState(0);
        var activePanel = me.getActiveItem();
        if (activePanel.last) {
            activePanel.last(me, true);
        }
    },
    /**
     * 点击下一步按钮时要设置导航按钮的选中或不选中状态
     *
     * @param {panel}
     *            cardPanel cardpanel面板
     * @param {panel}
     *            activePanel 激活面板
     */
    lastSetBtnState: function (cardpanel, activePanel) {
        var items = cardpanel.items.items;
        var index = Ext.Array.indexOf(items, activePanel) + 1;
        this.setBtnState(index);
    },

    /**
     * 设置导航按钮的事件函数
     *
     * @param {panel}
     *            cardPanel cardpanel面板
     * @param index
     *            面板索引值
     */
    navBtnHandler: function (cardPanel, index) {
        var me = this;
        me.setActiveItem(index);
        me.navBtnState();

    },

    /**
     * 设置上一步和下一步按钮的状态
     */
    navBtnState: function () {
        var me = this;
        var layout = me.getLayout();
        me.down("[name='kpi_kpi_move-prev']").setDisabled(!layout
            .getPrev());
        me.down("[name='kpi_kpi_move-next']").setDisabled(!layout
            .getNext());
    },

    /**
     * 上一步按钮事件
     */
    back: function () {
        var me = this;
        me.pageMove("prev");
        var activePanel = me.getActiveItem();
        me.navBtnState();
        me.preSetBtnState(me, activePanel);
    },
    /**
     * 点击上一步按钮时要设置导航按钮的选中或不选中状态
     *
     * @param {panel}
     *            cardPanel cardpanel面板
     * @param {panel}
     *            activePanel 激活面板
     */
    preSetBtnState: function (cardpanel, activePanel) {
        var items = cardpanel.items.items;
        var index = Ext.Array.indexOf(items, activePanel);
        this.setBtnState(index);
    },
    /**
     * 设置导航按钮的选中或不选中状态
     *
     * @param index,要激活的面板索引
     */
    setBtnState: function (index) {
        var me = this;
        if (index == 0) {
            me.down("[name='kpi_kpi_details_btn_top']").toggle(true);
            me.down("[name='kpi_kpi_caculate_btn_top']").toggle(false);
            me.down("[name='kpi_kpi_alarmset_btn_top']").toggle(false);
        } else if (index == 1) {
            me.down("[name='kpi_kpi_details_btn_top']").toggle(false);
            me.down("[name='kpi_kpi_caculate_btn_top']").toggle(true);
            me.down("[name='kpi_kpi_alarmset_btn_top']").toggle(false);

        } else if (index == 2) {
            me.down("[name='kpi_kpi_details_btn_top']").toggle(false);
            me.down("[name='kpi_kpi_caculate_btn_top']").toggle(false);
            me.down("[name='kpi_kpi_alarmset_btn_top']").toggle(true);
        }
    },

    /**
     * 设置首项为激活状态
     *
     */
    setFirstItemFoucs: function (disable) {
        var me = this;
        me.setBtnState(0);
        me.navBtnHandler(me, 0);
        me.down("[name='kpi_kpi_caculate_btn_top']").setDisabled(disable);
        me.down("[name='kpi_kpi_alarmset_btn_top']")
            .setDisabled(disable);
    },

    /**
     * 使导航按钮为enable状态
     */
    setNavBtnEnable: function (v, first) {
        var me = this;
        if (first) {
            me.down("[name='kpi_kpi_details_btn_top']").setDisabled(v);
        }
        me.down("[name='kpi_kpi_caculate_btn_top']").setDisabled(v);
        me.down("[name='kpi_kpi_alarmset_btn_top']").setDisabled(v);
    },

    topbarItem: {
        items: [{

                text: FHD.locale.get('fhd.common.details'), // 基本信息导航按钮
                iconCls: 'icon-001',
                name: 'kpi_kpi_details_btn_top',
                handler: function () {
                    var me = this.up('panel');
                    me.setBtnState(0);
                    me.navBtnHandler(me, 0);
                }
            },
            '<img src="' + __ctxPath + '/images/icons/show_right.gif">', {
                text: FHD.locale
                    .get("fhd.kpi.kpi.toolbar.caculate"), // 采集计算报告导航按钮
                iconCls: 'icon-002',
                name: 'kpi_kpi_caculate_btn_top',
                handler: function () {
                    var me = this.up('panel');
                    me.setBtnState(1);
                    me.navBtnHandler(me, 1);
                }
            },
            '<img src="' + __ctxPath + '/images/icons/show_right.gif">', {
                id: '',
                text: FHD.locale
                    .get("fhd.kpi.kpi.toolbar.alarmset"), // 告警设置导航按钮
                iconCls: 'icon-003',
                name: 'kpi_kpi_alarmset_btn_top',
                handler: function () {
                    var me = this.up('panel');
                    me.setBtnState(2);
                    me.navBtnHandler(me, 2);
                }
            }
        ]
    },
    bottomBarItem: {
        items: ['->', {
                name: 'kpi_kpi_move-undo',
                text: FHD.locale
                    .get('fhd.strategymap.strategymapmgr.form.undo'), // 返回按钮
                iconCls: 'icon-control-repeat-blue',
                handler: function () {
                    var me = this.up('panel');
                    me.undo();
                }
            },

            {
                name: 'kpi_kpi_move-prev',
                text: FHD.locale
                    .get("fhd.strategymap.strategymapmgr.form.back"), // 上一步按钮
                iconCls: 'icon-control-rewind-blue',
                handler: function () {
                    var me = this.up('panel');
                    me.back();

                }
            }, {
                name: 'kpi_kpi_move-next',
                text: FHD.locale
                    .get("fhd.strategymap.strategymapmgr.form.last"), // 下一步按钮
                iconCls: 'icon-control-fastforward-blue',
                handler: function () {
                    var me = this.up('panel');
                    me.last();
                }
            }, {
                text: FHD.locale
                    .get("fhd.strategymap.strategymapmgr.form.save"), // 保存按钮
                name: 'kpi_kpi_finish_btn',
                iconCls: 'icon-control-stop-blue',
                handler: function () {
                    var me = this.up('panel');
                    me.finish();

                }
            }
        ]
    },
    // 初始化方法
    initComponent: function () {
        var me = this;
        if (!me.kpibasicform) {
            me.kpibasicform = Ext.create(
                'FHD.view.kpi.cmp.kpi.BasicForm', {
                    paramObj: me.paramObj,
                    pcontainer: me
                });
        }
        if (!me.kpigatherform) {
            me.kpigatherform = Ext.create(
                'FHD.view.kpi.cmp.kpi.GatherForm', {
                    paramObj: me.paramObj,
                    pcontainer: me
                });
        }
        if (!me.kpiwarningset) {
            me.kpiwarningset = Ext.create(
                'FHD.view.kpi.cmp.kpi.WarningSet', {
                    paramObj: me.paramObj,
                    pcontainer: me
                });
        }
        Ext.apply(me, {
            border: me.border,
            items: [me.kpibasicform, me.kpigatherform,
                me.kpiwarningset
            ],
            tbar: me.topbarItem,
            bbar: me.bottomBarItem
        });
        me.addListener('afterrender', function () {
            var me = this;
            if (!me.paramObj.editflag) {
                me.kpibasicform.clearFormData();
                me.kpibasicform.initFormData();
                me.backType = "sm";
                me.kpigatherform.clearFormData();
                me.kpigatherform.initFormData();
                me.kpibasicform.clearParamObj();
                me.kpiwarningset.reLoadGridById("", false);
                me.setFirstItemFoucs(true);
                me.setInitBtnState();
            } else {
                me.setFirstItemFoucs(false);
                me.kpibasicform.formLoad();
                me.kpibasicform.valueToFormulaName();
            }
        });

        me.callParent(arguments);

    },

    setInitBtnState: function () {
        var me = this;
        var kpimainpanel = me.pcontainer;
        if (!kpimainpanel.paramObj.editflag) {
            // 添加指标
            me.setNavBtnEnable(true, false);
        } else {
            // 编辑指标
            me.setNavBtnEnable(false, true);
        }
    }

});