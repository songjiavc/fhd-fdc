Ext.define('FHD.view.kpi.cmp.sc.ScSmHistoryGrid', {
    extend: 'FHD.ux.EditorGridPanel',
    url: '',
    // kpi列表Url地址
    cols: [],
    // kpi列表显示的列
    tbarItems: [],
    // kpi列表上方工具条
    border: false,
    // 默认不显示border
    checked: true,
    
    paramObj:{
    	objectId:'',
    	type:''
    },
    
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    recalc: function () {
        var me = this;
        var objArr = [];
        var selections = me.getSelectionModel().getSelection();
        for (var i = 0; i < selections.length; i++) {
            var obj = {};
            obj.objectId = selections[i].get('id');
            obj.timeperiod = selections[i].get('timePeriod');
            objArr.push(obj);
        }
        if (me.body != undefined) {
            me.body.mask("计算中...", "x-mask-loading");
        }
        FHD.ajax({
            url: __ctxPath + '/formula/recategoryformulacalculate.f',
            params: {
                items: Ext.JSON.encode(objArr),
                type: me.paramObj.type,
                objectId: me.paramObj.objectId
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
    
    onchange: function (me) {
    	me.savebtn.setDisabled(me.getSelectionModel().getSelection().length === 0);
        me.calcbtn.setDisabled(me.getSelectionModel().getSelection().length === 0);
    },
    reloadData: function () {
        var me = this;
        me.store.proxy.extraParams.objectId = me.paramObj.objectId;
        me.store.proxy.extraParams.type = me.paramObj.type;
        me.store.load();
    },
    save: function (me) {
        var rows = me.store.getModifiedRecords();
        var jsobj = {
            type: me.paramObj.type,
            objectId: me.paramObj.objectId
        };
        jsobj.datas = [];
        Ext.each(rows, function (item) {
            jsobj.datas.push(item.data);
        });
        if (jsobj.datas.length > 0) {
            if (me.body != undefined) {
                me.body.mask("保存中...", "x-mask-loading");
            }
            FHD.ajax({
                url: __ctxPath + "/category/modifiedrelaassessresult.f",
                params: {
                    modifiedRecord: Ext.encode(jsobj)
                },
                callback: function (data) {
                    if (data) {
                        me.store.load();
                        if (me.body != undefined) {
                            me.body.unmask();
                        }
                    }
                }
            });
        }
        me.store.commitChanges();
    },
    
    // 是否可以选中
    initComponent: function () {
        var me = this;
        me.savebtn = Ext.create('Ext.button.Button',{
                text: FHD.locale.get('fhd.common.save'),
                iconCls: 'icon-save',
                /*authority:{
        			type:'any',
        			name:'ROLE_ALL_MONITOR_KPIMONITOR_SAVEHISTORY,ROLE_ALL_REVIEW_RELEASE_SAVEHISTORY'
    			},*/
                handler: function () {
                    me.save(me)
                },
                disabled: true,
                scope: this
            });
        
        me.calcbtn = Ext.create('Ext.button.Button',{
            tooltip: '计算',
            /*authority:{
    			type:'any',
    			name:'ROLE_ALL_MONITOR_KPIMONITOR_CALCULATEHISTORY,ROLE_ALL_REVIEW_RELEASE_CALCULATEHISTORY'
			},*/
            iconCls: 'icon-calculator',
            handler: function () {
                me.recalc();
            },
            disabled: true,
            text: '计算'
        });
        Ext.apply(me, {
        	searchable:false,
            url: __ctxPath + "/category/findrelaassessresultsbysome.f",
            extraParams: {
                objectId: me.paramObj.objectId,
                type: me.paramObj.type
            },
            tbarItems: [me.savebtn,'-',me.calcbtn ]
        });
        me.cols = [{
                dataIndex: 'id',
                hidden: true
            },

            {
                cls: 'grid-icon-column-header grid-statushead-column-header',
                header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
                dataIndex: 'assessmentStatusStr',
                menuDisabled:true,
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
                    return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
                }
            }, {
                cls: 'grid-icon-column-header grid-trendhead-column-header',
                header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.kpi.kpi.form.directionto") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
                dataIndex: 'directionstr',
                menuDisabled:true,
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
                header: FHD.locale.get('fhd.kpi.kpi.form.assessmentValue'),
                dataIndex: 'assessmentValue',
                sortable: true,
                flex: 1,
                align: 'right',
                editor: {
                    xtype: 'numberfield',
                    maxValue: 100,
                    allowDecimals: true,
                    nanText: FHD.locale.get('fhd.strategymap.strategymapmgr.prompt.inputNum'),
                    step: 0.5
                },
                renderer: function (value, metaData, record, colIndex, store, view) {
                    metaData.tdAttr = 'style="background-color:#FFFBE6"';
                    return value;
                }
            }, {
                header: FHD.locale.get('fhd.kpi.kpi.form.dateRange'),
                dataIndex: 'dateRange',
                sortable: true,
                align: 'right',
                flex: 1,
                renderer: function (v) {
                    return "<div data-qtitle='' data-qtip='" + v + "'>" + v + "</div>";
                }
            }, {
                dataIndex: 'timePeriod',
                hidden: true
            	},
            	{
            		dataIndex:'objectId',
            		hidden:true
            	}
        ];
        me.callParent(arguments);
        me.store.on('load',function(){
  		  	me.onchange(me);
        });
        me.on('selectionchange', function () {
            me.onchange(me)
        }); //选择记录发生改变时改变按钮可用状态
    }
});