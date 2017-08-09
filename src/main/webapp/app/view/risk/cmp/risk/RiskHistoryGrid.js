Ext.define('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
    extend: 'FHD.ux.GridPanel', //FHD.ux.GridPanel
    alias: 'widget.riskhistorygrid',
    requires: [
        'FHD.view.risk.assess.utils.GridCells'
    ],
    /**
     * public
     * 接口属性
     */
    type: 'risk', //risk,org,strategy,process
    currentId: '',
    riskHistoryUrl: __ctxPath + '/risk/findRiskAdjustHistoryById.f',
    orgHistoryUrl: __ctxPath + '/risk/findOrgAdjustHistoryByOrgId',
    strategyHistoryUrl: __ctxPath + '/risk/findStrategyAdjustHistoryByStrategyMapId',
    processHistoryUrl: __ctxPath + '/risk/findProcessAdjustHistoryByProcessId',
    //返回方法，用于对历史记录修改后续处理，如刷新左侧树
    historyCallback: function(){},
    showbar : true,

    initComponent: function () {
        var me = this;
        var cols = new Array();
		var colsbegin = new Array();
		colsbegin = [{
                dataIndex: 'id',
                invisible: true
            }, {
                dataIndex: 'year',
                header: '年',
                width: 60,
                align: 'right'
            }, {
                dataIndex: 'month',
                header: '月',
                width: 60,
                align: 'right'
            }, {
                dataIndex: 'adjustType',
                header: '来源',
                sortable: false,
                flex: 2,
                scope: me,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	            	metaData.tdAttr = 'data-qtip="' + value + '"';
	            	return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showAssessHistory()\" >" + value + "</a>";
	            }
            }];
		for(var i = 0 ; i < colsbegin.length; i++){
        	cols.push(colsbegin[i]);
        }
        if(me.type == 'risk' || me.type == 'riskevent'){
        	cols.push({
                dataIndex: 'template',
                header: '评估模板',
                sortable: false,
                flex: 2,
                scope: me,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="' + value + '"';
                	return value;
	            }
            });
        }
		var colsend = new Array();
        colsend = [{
                dataIndex: 'adjustTypeValue',
                invisible: true
            }, {
                dataIndex: 'templateid',
                invisible: true
            }, {
                dataIndex: 'calculateFormula',
                header: '计算公式',
                sortable: false,
                flex: 2,
                scope: me,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	                metaData.tdAttr = 'data-qtip="' + value + '"';
                	return value;
	            }
            }, {
                dataIndex: 'riskStatus',
                header: '风险值',
                width: 60,
                scope: me
            },

            {
                cls: 'grid-icon-column-header grid-statushead-column-header',
                dataIndex: 'assessementStatus',
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
                    } else {
                        v = "icon-ibm-underconstruction-small";
                        display = "无";
                    }
                    return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
                }
            }, {
            	cls: 'grid-icon-column-header grid-trendhead-column-header',
                dataIndex: 'etrend',
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
                    return "<div style='width: 23px; height: 19px; background-repeat: no-repeat;" +
                        "background-position: center top;' data-qtitle='' " +
                        "class='" + v + "'  data-qtip='" + display + "'></div>";
                }
            }
        ];
        for(var i = 0 ; i < colsend.length; i++){
        	cols.push(colsend[i]);
        }
        
        if(me.showbar){
        	me.tbarItems= [{
            	authority:'ROLE_ALL_RISK_HISTORY',
                btype: 'add',
                handler: function () {
                    me.addFun();
                }
            }, {
            	authority:'ROLE_ALL_RISK_HISTORY',
                btype: 'edit',
                name: 'edit_button',
                disabled: true,
                handler: function () {
                    me.editFun();
                }
            }, {
            	authority:'ROLE_ALL_RISK_HISTORY',
                btype: 'delete',
                name: 'delete_button',
                disabled: true,
                handler: function () {
                    me.delFun();
                }
            }];
        
        }

        Ext.apply(me, {
        	cols : cols,
            columnLines: true
        });
        me.on('selectionchange', function () {
            me.setstatus(me)
        });
        me.callParent(arguments);

        me.on('afterlayout', function () {
            Ext.widget('gridCells').mergeCells(me, [2, 3]);
        });

    },
    reloadData: function (searchId) {
        var me = this;
        var url = '';
        if (me.type == 'risk') {
            url = me.riskHistoryUrl;
        } else if (me.type == 'riskevent') {
            url = me.riskHistoryUrl;
        } else if (me.type == 'org') {
            url = me.orgHistoryUrl;
        } else if (me.type == 'sm') {
            url = me.strategyHistoryUrl;
        } else if (me.type == 'process') {
            url = me.processHistoryUrl;
        } else {
            alert('type参数传递出错!');
        }
        if (searchId != null) {
            me.currentId = searchId;
        }
        me.store.proxy.url = url;
        me.store.proxy.extraParams.id = me.currentId;
        me.store.proxy.extraParams.schm = me.schm;      //wzr 风险分库标识
        me.store.load();
    },

    initParams: function () {
        var me = this;
    },

    // 设置按钮可用状态
    setstatus: function (me) {
    	if(me.down("[name='edit_button']")){
	        me.down("[name='edit_button']").setDisabled(me.getSelectionModel().getSelection().length != 1);
            me.down("[name='delete_button']").setDisabled(me.getSelectionModel().getSelection().length != 1);
    	}
    },

    addFun: function () {
        var me = this;
        var riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanel', {
            isEdit: false,
            type: me.type,
            callback: function (data) {
                me.reloadData(me.currentId);
                assesswindow.close();
                if (me.callback) {
                    me.callback(data);
                }
            }
        });
        riskAssessPanel.reloadData(me.currentId);
        var assesswindow = Ext.create('FHD.ux.Window', {
            title: '历史记录新增',
            maximizable: true,
            modal: true,
            width: 600,
            height: 400,
            collapsible: true,
            autoScroll: true,
            items: riskAssessPanel,
            buttons: [{
                text: '保存',
                handler: function () {
                    riskAssessPanel.save();
                }
            }, {
                text: '关闭',
                handler: function () {
                    assesswindow.close();
                }
            }]
        }).show();
    },
    editFun: function () {
        var me = this;
        if (me.getSelectionModel().getSelection().length > 0) {
            var riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanel', {
                isEdit: true,
                type: me.type,
                callback: function (data) {
                    me.reloadData(me.currentId);
                    assesswindow.close();
                    if (me.callback) {
                        me.callback(data);
                    }
                }
            });
            riskAssessPanel.reloadData(me.currentId, me.getSelectionModel().getSelection()[0].data.templateid, me.getSelectionModel().getSelection()[0].data.id,me.getSelectionModel().getSelection()[0].data.adjustTypeValue);
            var assesswindow = Ext.create('FHD.ux.Window', {
                title: '历史记录修改',
                maximizable: true,
                modal: true,
                width: 600,
                height: 550,
                collapsible: true,
                autoScroll: true,
                items: riskAssessPanel,
                buttons: [{
                    text: '保存',
                    handler: function () {
                        riskAssessPanel.save();
                    }
                }, {
                    text: '关闭',
                    handler: function () {
                        assesswindow.close();
                    }
                }]
            }).show();
        }
    },
    showAssessHistory: function(){
    	var me = this;
    	if(me.getSelectionModel().getSelection()[0].data.adjustTypeValue != '3'){
            var riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanel', {
                isEdit: true,
                oprType: 'view',
                type: me.type,
                callback: function (data) {
                    me.reloadData(me.currentId);
                    assesswindow.close();
                    if (me.callback) {
                        me.callback(data);
                    }
                }
            });
    	}else{
    		//公式定义
    		var riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanel', {
                isEdit: true,
                type: 'formula',
                oprType: 'view',
                callback: function (data) {
                    me.reloadData(me.currentId);
                    assesswindow.close();
                    if (me.callback) {
                        me.callback(data);
                    }
                }
            });
    	}
        riskAssessPanel.reloadData(me.currentId, me.getSelectionModel().getSelection()[0].data.templateid, me.getSelectionModel().getSelection()[0].data.id);
        var assesswindow = Ext.create('FHD.ux.Window', {
            title: '评估结果查看',
            maximizable: true,
            modal: true,
            width: 600,
            height: 550,
            collapsible: true,
            autoScroll: true,
            items: riskAssessPanel,
            buttons: [{
                text: '关闭',
                handler: function () {
                    assesswindow.close();
                }
            }]
        }).show();
    },
    delFun: function (hisid) {
        var me = this;
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') { //确认删除
                    FHD.ajax({
                        url: __ctxPath + '/cmp/risk/riskassessdelete.f',
                        params: {
                            hisid: me.getSelectionModel().getSelection()[0].data.id,
                            type: me.type,
                            value: me.currentId
                        },
                        callback: function (data) {
                            FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                            me.reloadData(me.currentId);
                            if (me.callback) {
                                me.historyCallback(data);
                            }
                        }
                    });
                }
            }
        });
    }
});