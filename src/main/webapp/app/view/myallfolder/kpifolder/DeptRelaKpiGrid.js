Ext.define('FHD.view.myallfolder.kpifolder.DeptRelaKpiGrid', {
    extend: 'FHD.view.kpi.cmp.kpi.KpiGridPanel',

    border: false,
    kpiMainContainer: null, // 指标编辑容器
    mainPanel: null, // 历史数据图表面板
    paramObj: {},
    navData: null,
    /**
     * 初始化该类所用到的参数
     */

    initParam: function (paramObj) {
        var me = this;
        paramObj.navId = paramObj.objectId;
        me.paramObj = paramObj;
    },
    /**
     * 删除指标
     */
    kpiDelFun: function () {
        var me = this;
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { // 确认删除
                    var kpiids = [];
                    var selections = me.getSelectionModel()
                        .getSelection();
                    Ext.Array.each(selections, function (item) {
                        kpiids.push(item.get("id"));
                    });

                    FHD.ajax({
                        url: __ctxPath + '/kpi/kpi/removecommonkpibatch.f',
                        params: {
                            kpiItems: Ext.JSON.encode(kpiids)
                        },
                        callback: function (data) {
                            if (data && data.success) {
                                me.store.load();
                            }
                        }
                    });
                }
            }
        });
    },

    initKpiMainContainer: function () {
        var me = this;
        var param = [];
        if(me.editflag) {
    	 param.navId = me.paramObj.objectId;
         param.kpiId = me.kpiId;
         param.kpiname = me.kpiname;
         param.selecttypeflag = '';
         param.backType= 'departmentfolder';
         param.editflag = me.editflag;
     } else {
        param.navId = me.paramObj.objectId;
        param.kpiname = '';
        param.selecttypeflag = '';
        param.backType= 'departmentfolder';
        param.editflag = me.editflag;
        param.empType = 'dept_emp';
     }
        /**
         * 初始化右侧指标容器
         */

        me.kpiMainContainer = Ext.create('FHD.view.myallfolder.kpifolder.KpiEditMainPanel', {
            pcontainer: me,
            paramObj: param,
            undo: me.undo,
            navData : me.navData
        });
        me.reRightLayout(me.kpiMainContainer);
    },
    /**
     * 添加指标
     */
    kpiaddFun: function() {
    	var me = this;
    	var param = {};
        me.editflag = false;
    	me.initKpiMainContainer();
    },
    /**
     * 编辑指标
     */
    kpiEditFun: function () {
        var me = this;
        var selections = me.getSelectionModel().getSelection();
        var length = selections.length;
        if (length > 0) {
            if (length >= 2) {
                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),
                    FHD.locale.get('fhd.kpi.kpi.prompt.editone'));
                return;
            } else {
                var selection = selections[0]; // 得到选中的记录
                var kpiId = selection.get('id'); // 获得指标ID
                var kpiname = selection.get('name');
                me.kpiId = kpiId;
                me.kpiname = kpiname;
                me.editflag = true;
                me.initKpiMainContainer();
            }
        } else {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '请选择一条指标.');
            return;
        }
    },
    addListerner: function () {
        var me = this;
        me.store.on('load',function(){
    		  me.setBtnState();
    	});
        me.on('selectionchange', function () {
        	  me.setBtnState();
        }); // 选择记录发生改变时改变按钮可用状态

    },
    //改变按钮状态
    setBtnState: function(){
    	var me = this;
        var btns = me.getDockedItems('toolbar[dock="top"]');
        if (me.btnEdit) {
            me.btnEdit
                .setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.btnDel) {
            me.btnDel
                .setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='stop']")) {
            me.down("[name='stop']")
                .setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='start']")) {
            me.down("[name='start']")
                .setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='cal']")) {
            me.down("[name='cal']")
                .setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='focus']")) {
            me.down("[name='focus']")
                .setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='noFocus']")) {
            me.down("[name='noFocus']")
                .setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
    	
    },
    
    
    initComponent: function () {
        var me = this;
        me.btnEdit = Ext.create('Ext.Button', {
            tooltip: FHD.locale.get("fhd.kpi.kpi.toolbar.editkpi"),
            iconCls: 'icon-edit',
            authority:'ROLE_ALL_KPI_EDIT',
            disabled: true,
            handler: function () {
                me.kpiEditFun();
            },
            text: FHD.locale.get("fhd.common.edit")
        });
        me.btnDel = Ext.create('Ext.Button', {
            tooltip: FHD.locale.get("fhd.kpi.kpi.toolbar.delkpi"),
            iconCls: 'icon-del',
            authority:'ROLE_ALL_KPI_DELETE',
            disabled: true,
            handler: function () {
                me.kpiDelFun();
            },
            text: FHD.locale.get("fhd.common.delete")
        });
        Ext.apply(me, {
        	isDisplayWeight:false,
            url: __ctxPath + "/myfolder/finddeptrelakpi.f",
            extraParams: {
                id: me.paramObj.objectId,
                year: FHD.data.yearId,
                month: FHD.data.monthId,
                quarter: FHD.data.quarterId,
                week: FHD.data.weekId,
                eType: FHD.data.eType,
                isNewValue: FHD.data.isNewValue
            },
            checked: true,
            type: 'deptrelakpigrid',
            tbarItems: [{
                tooltip: FHD.locale.get('fhd.kpi.kpi.op.addkpi'),
                iconCls: 'icon-add',
                authority:'ROLE_ALL_KPI_ADD',
                name: 'kpiadd',
                handler: function() {
                    me.kpiaddFun();
                },
                text:FHD.locale.get('fhd.strategymap.strategymapmgr.subLevel')
            },
           
            me.btnEdit, me.btnDel,
            		{
                        name: 'start',
                        btype : 'op',
                        tooltip: FHD.locale.get('fhd.sys.planMan.start'),
                        iconCls: 'icon-plan-start',
                        authority:'ROLE_ALL_KPI_ENABLE',
                        handler: function () {
                            me.enables("0yn_y");
                        },
                        disabled: true,
                        text: FHD.locale.get('fhd.sys.planMan.start')
                    }, {
                        name: 'stop',
                        btype:'op',
                        tooltip: FHD.locale.get('fhd.sys.planMan.stop'),
                        authority:'ROLE_ALL_KPI_ENABLE',
                        iconCls: 'icon-plan-stop',
                        handler: function () {
                            me.enables("0yn_n");
                        },
                        disabled: true,
                        text: FHD.locale.get('fhd.sys.planMan.stop')
                    }, {
                        name: 'focus',
                        tooltip: '关注',
                        btype:'op',
                        authority:'ROLE_ALL_KPI_ATTENTION',
                        iconCls: 'icon-kpi-heart-add',
                        handler: function () {
                            me.focus('0yn_y');
                        },
                        disabled: true,
                        text: '关注'
                    },

                    {
                        name: 'noFocus',
                        tooltip: '取消关注',
                        btype:'op',
                        authority:'ROLE_ALL_KPI_ATTENTION',
                        iconCls: 'icon-kpi-heart-delete',
                        handler: function () {
                            me.focus('0yn_n');
                        },
                        disabled: true,
                        text: '取消关注'
                    }, {
                        name: 'cal',
                        btype:'op',
                        authority:'ROLE_ALL_KPI_CALCULATE',
                        tooltip: FHD.locale.get('fhd.formula.calculate'),
                        iconCls: 'icon-calculator',
                        handler: function () {
                            me.recalc();
                        },
                        disabled: true,
                        text: FHD.locale.get('fhd.formula.calculate')

                    }
            
            ]
            
        });

        me.callParent(arguments);

        me.addListerner();
    },
    focus: function (focus) {
        var me = this;
        var paraobj = {};
        paraobj.focus = focus;
        paraobj.kpiids = [];
        var selections = me.getSelectionModel().getSelection();
        Ext.Array.each(selections,
            function (item) {
                paraobj.kpiids.push(item.get("id"));
            });
        if (me.body != undefined) {
            if ('Y' == focus) {
                me.body.mask("关注中...", "x-mask-loading");
            } else {
                me.body.mask("取消关注中...", "x-mask-loading");
            }
        }
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/mergekpifoucs.f',
            params: {
                kpiItems: Ext.JSON.encode(paraobj)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.store.load();
                    if (me.body != undefined) {
                        me.body.unmask();
                    }
                }
            }
        });
    },
    recalc: function () {
        var me = this;
        var objArr = [];
        var selections = me.getSelectionModel().getSelection();
        for (var i = 0; i < selections.length; i++) {
            var obj = {};
            obj.kpiId = selections[i].get('id');
            obj.timeperiod = selections[i].get('timeperiod');
            objArr.push(obj);
        }
        if (me.body != undefined) {
            me.body.mask("计算中...", "x-mask-loading");
        }
        FHD.ajax({
            url: __ctxPath + '/formula/reformulacalculate.f',
            params: {
                kpiItems: Ext.JSON.encode(objArr)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.store.load();
                    if (me.body != undefined) {
                        me.body.unmask();
                    }
                }
            }
        });

    },
    enables: function (enable) {
        var me = this;
        var paraobj = {};
        paraobj.enable = enable;
        paraobj.kpiids = [];
        var selections = me.getSelectionModel().getSelection();
        Ext.Array.each(selections, function (item) {
            paraobj.kpiids.push(item.get("id"));
        });
        if (me.body != undefined) {
            if ('0yn_y' == enable) {
                me.body.mask("启用中...", "x-mask-loading");
            } else {
                me.body.mask("停用中...", "x-mask-loading");
            }
        }
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/mergekpienable.f',
            params: {
                kpiItems: Ext.JSON.encode(paraobj)
            },
            callback: function (data) {
                if (data && data.success) {
                    me.store.load();
                    if (me.body != undefined) {
                        me.body.unmask();
                    }
                }
            }
        });
    },
    /**
     * 重新加载列表数据
     */
    reloadData: function () {
        var me = this;
        if (me.paramObj != undefined) {
            me.store.proxy.extraParams.id = me.paramObj.objectId;
            me.store.load();
        }
    },
    gatherResultFun: function (obj) {
        var me = this;
        var paraobj = obj.split(",");
        var name = paraobj[0];
        var kgrid = paraobj[1];
        var memoTitle = paraobj[2];
        var kpiid = paraobj[3];
        var loadParam = {};
        loadParam.navId = me.paramObj.objectId;
        loadParam.name = name;
        loadParam.kpiname = name;
        loadParam.type = 'departmentfolder';
        loadParam.kgrid = kgrid;
        loadParam.memoTitle = memoTitle;
        loadParam.kpiid = kpiid;
        if (me.historyDataPanel == null) {
            me.historyDataPanel = Ext.create('FHD.view.myallfolder.kpifolder.DeptRelaKpiAnalysePanel', {
                goback: me.goback,
                navData: me.navData,
                go:function() {
                	 me.historyDataPanel.reLoadData();
                	 me.reRightLayout(me.historyDataPanel);
                }                
            });
        }
        me.historyDataPanel.paramObj = loadParam;
        me.historyDataPanel.card.setActiveItem(me.historyDataPanel.mainPanel);
        me.reRightLayout(me.historyDataPanel);
        me.historyDataPanel.reLoadData();

    },
    reRightLayout: function (p) {
        var me = this;
    },
    goback: function () {
        var me = this;
    },
    undo: function () {

    }
});