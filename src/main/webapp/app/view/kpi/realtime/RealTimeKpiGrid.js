Ext.define('FHD.view.kpi.realtime.RealTimeKpiGrid', {
    extend: 'FHD.ux.GridPanel',


    initComponent: function () {
        var me = this;

        var tbar = [ //菜单项
            {
                text: "添加",
                iconCls: 'icon-add',
                name: 'realtimekpigrid_add',
                handler: function () {
                    me.addKpi();
                }
            }, {
                text: "编辑",
                name: 'realtimekpigrid_edit',
                iconCls: 'icon-edit',
                disabled: true,
                handler: function () {
                    me.editKpi();
                }
            }, {
                text: "删除",
                iconCls: 'icon-del',
                name: 'realtimekpigrid_del',
                handler: function () {
                    me.removeKpi();
                },
                disabled: true,
                scope: this
            }, {
                text: "历史数据回放",
                iconCls: 'icon-chart-trendline',
                name: 'realtimekpigrid_history',
                handler: function () {
                	me.historyinput();
                },
                disabled: true,
                scope: this
            }
        ];

        var gridColums = [];


        var idCol = {
            dataIndex: 'id',
            invisible: true
        };

        gridColums.push(idCol);

        var statusCol = {
            cls: 'grid-icon-column-header grid-statushead-column-header',
            header: "<span data-qtitle='' data-qtip='" + "状态" + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'status',
            sortable: true,
            menuDisabled: true,
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
        };

        gridColums.push(statusCol);


        var nameCol = {
            header: "名称",
            dataIndex: 'name',
            sortable: true,
            flex: 0.8,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        };

        gridColums.push(nameCol);


        var codeCol = {
            header: "编号",
            dataIndex: 'code',
            sortable: true,
            flex: 0.8,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        };


        gridColums.push(codeCol);
        
        
        var unitCol = {
            header: "单位",
            dataIndex: 'unit',
            sortable: true,
            flex: 0.8,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        };


        gridColums.push(unitCol);
        

        var descCol = {
            header: FHD.locale.get('fhd.alarmplan.form.desc'),
            dataIndex: 'desc',
            sortable: true,
            flex: 3.6,
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
            }
        };

        gridColums.push(descCol);

        Ext.apply(me, {
            url: __ctxPath + "/kpi/real/findrealtimekpibysome.f",
            storeAutoLoad: false,
            cols: gridColums, //cols:为需要显示的列
            tbarItems: tbar
        });


        me.callParent(arguments);


        me.store.on('load', function () {
            me.setstatus();
        });
        me.on('selectionchange', function (model, selected, eOpts) {
            me.setstatus();
        });



    },
    setstatus: function () {
        var me = this;
        var length = me.getSelectionModel().getSelection().length;
        if (me.down("[name='realtimekpigrid_del']")) {
            me.down("[name='realtimekpigrid_del']").setDisabled(length === 0);
        }
        if (me.down("[name='realtimekpigrid_edit']")) {
            me.down("[name='realtimekpigrid_edit']").setDisabled(length === 0);
        }
        if (me.down("[name='realtimekpigrid_history']")) {
            me.down("[name='realtimekpigrid_history']").setDisabled(length === 0);
        }
    },
    
    historyinput:function(){
    	var me = this;
    	var selections = me.getSelectionModel().getSelection();
        var length = selections.length;
        if (length >= 2) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "只能选择一条指标录入数据!");
            return;
        }
        var kpiId = selections[0].get('id');
        
        var formwindow = new Ext.Window({
            title: '历史数据查看',
            constrain: true,
            layout: 'fit',
            iconCls: 'icon-edit', //标题前的图片
            modal: true, //是否模态窗口
            collapsible: true,
            scroll: 'auto',
            closeAction: 'destroy',
            width: 1000,
            height: 500,
            maximizable: true //（是否增加最大化，默认没有）
        });
        
        var param = {
            kpiId: kpiId,
            realTimeKpiGrid:me
        };
        
        var historyGrid = Ext.create('FHD.view.kpi.realtime.RealTimeKpiHistoryGrid',{
        	title:'历史数据'
        });
        
        historyGrid.initParam(param);
        historyGrid.reload();
        
        var chartParam = {
        	kpiId: kpiId
        }
        var chartContainer = Ext.create('FHD.view.kpi.realtime.RealTimeKpiHistoryChart',{
        	title:'图表分析'
        });
        chartContainer.initParam(chartParam);
        
        var historyTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
        		position:'left',
        		listeners: {
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                        if (newCard.reload) {
                            newCard.reload();
                        }
                    }
                },
                items: [
                		historyGrid,
                		chartContainer
                	   ]
            });
            
        formwindow.show();
        formwindow.add(historyTabPanel);
    },

    editKpi: function () {
        var me = this;
        var selections = me.getSelectionModel().getSelection();
        var length = selections.length;
        if (length >= 2) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), "只能编辑一条指标数据!");
            return;
        }
        var kpiId = selections[0].get('id');
        var formwindow = new Ext.Window({
            title: '编辑',
            constrain: true,
            layout: 'fit',
            iconCls: 'icon-edit', //标题前的图片
            modal: true, //是否模态窗口
            collapsible: true,
            scroll: 'auto',
            closeAction: 'destroy',
            width: 800,
            height: 300,
            maximizable: true //（是否增加最大化，默认没有）
        });
        var param = {
            kpiId: kpiId,
            editflag: true,
            formwindow: formwindow
        };
        var addWindow = Ext.create('FHD.view.kpi.realtime.RealTimeKpiEdit', {
            refresh: function () {
                me.store.load();
            }
        });
        addWindow.initParam(param);
        addWindow.reloadData();
        formwindow.show();
        formwindow.add(addWindow);
    },

    addKpi: function () {
        var me = this;
        var formwindow = new Ext.Window({
            title: '添加',
            constrain: true,
            layout: 'fit',
            iconCls: 'icon-edit', //标题前的图片
            modal: true, //是否模态窗口
            collapsible: true,
            scroll: 'auto',
            closeAction: 'destroy',
            width: 800,
            height: 300,
            maximizable: true //（是否增加最大化，默认没有）
        });
        var param = {
            editflag: false,
            formwindow: formwindow
        };
        var addWindow = Ext.create('FHD.view.kpi.realtime.RealTimeKpiEdit', {
            refresh: function () {
                me.store.load();
            }
        });
        addWindow.initParam(param);
        formwindow.show();
        formwindow.add(addWindow);

    },

    removeKpi: function () {
        var me = this;

        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    var idArray = [];
                    var selections = me.getSelectionModel().getSelection();
                    if (selections.length > 0) {
                        for (var i = 0; i < selections.length; i++) {
                            idArray.push(selections[i].get('id'));
                        }
                        FHD.ajax({
                            async: false,
                            url: __ctxPath + '/kpi/real/moverealtimekpi.f',
                            params: {
                                items: Ext.JSON.encode(idArray)
                            },
                            callback: function (data) {
                                if (data) {
                                    FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
                                    me.store.load();
                                }
                            }
                        });
                    }
                }
            }
        });


    }




});