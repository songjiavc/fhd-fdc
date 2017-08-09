Ext.define('FHD.view.kpi.realtime.RealTimeKpiHistoryGrid', {
    extend: 'FHD.ux.EditorGridPanel',

    border: false,
    layout:'fit',
    addEvent: function () {
        var me = this;
        var r = Ext.create('eventModel');
        me.store.insert(0, r);
        me.editingPlugin.startEditByPosition({
            row: 0,
            column: 0
        });
        me.doComponentLayout();

    },
	
    updateRealTimeLastStatus:function(){
    	var me = this;
    	if(me.paramObj.realTimeKpiGrid){
    		me.paramObj.realTimeKpiGrid.store.load();
    	}
    },
    
	setstatus: function () {
        var me = this;
        var length = me.getSelectionModel().getSelection().length;
        if (me.down("[name='history_del']")) {
            me.down("[name='history_del']").setDisabled(length === 0);
        }
    },
    remove: function (me) {
        var rows = me.getSelectionModel().getSelection();
        if (rows.length > 0) {
            Ext.MessageBox.show({
                title: FHD.locale.get('fhd.common.delete'),
                width: 260,
                msg: FHD.locale.get('fhd.common.makeSureDelete'),
                buttons: Ext.MessageBox.YESNO,
                icon: Ext.MessageBox.QUESTION,
                fn: function (btn) {
                    var jsobj = {};
                    jsobj.datas = [];
                    Ext.each(rows, function (item) {
                        jsobj.datas.push(item.data.id);
                    });
                    if (jsobj.datas.length > 0) {
                    	jsobj.kpiId = me.paramObj.kpiId;
                        FHD.ajax({
                            url: __ctxPath + "/kpi/real/moverealtimekpihistorydata.f",
                            params: {
                                items: Ext.encode(jsobj)
                            },
                            callback: function (data) {
                                if (data) {
                                    FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
                                    me.store.load();
                                    me.updateRealTimeLastStatus();
                                }
                            }
                        });
                    }
                    me.store.commitChanges();
                }
            });
        }

    },
    save: function (me) {
        var rows = me.store.getModifiedRecords();
        var jsobj = {};
        jsobj.datas = [];
        Ext.each(rows, function (item) {
            if (item.data.value == "") {
                FHD.notification( "实际值不能为空.",FHD.locale.get('fhd.common.prompt'));
                return;
            }
            if (item.data.date == "") {
                FHD.notification( "发生时间不能为空.",FHD.locale.get('fhd.common.prompt'));
                return;
            }
            if (item.data.date instanceof Date) {
                item.data.date = Ext.Date.format(new Date(item.data.date), 'Y-m-d H:i:s');
            }
            jsobj.datas.push(item.data);
        });
        if (jsobj.datas.length > 0) {
        	jsobj.kpiId = me.paramObj.kpiId;
            FHD.ajax({
                url: __ctxPath + "/kpi/real/mergerealtimekpihistorydata.f",
                params: {
                    items: Ext.encode(jsobj)
                },
                callback: function (data) {
                    if (data) {
                        FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
                        me.store.load();
                        me.updateRealTimeLastStatus();
                    }
                }
            });
        }
        me.store.commitChanges();
    },
    /**
     * 初始化组件
     */
    initComponent: function () {
        var me = this;


        Ext.define('eventModel', {
            extend: 'Ext.data.Model',
            fields: ['id', 'status', 'value', 'desc', 'date']
        });
        
        var ccols = [];
        
        ccols.push({
            dataIndex: 'id',
            width: 0
        });
        
        ccols.push({
            cls: 'grid-icon-column-header grid-statushead-column-header',
            header: "<span data-qtitle='' data-qtip='" + FHD.locale.get("fhd.sys.planEdit.status") + "'>&nbsp&nbsp&nbsp&nbsp" + "</span>",
            dataIndex: 'status',
            sortable: true,
            width: 40,
            renderer: function (v) {
                var display = "";
                if (v == "icon-ibm-symbol-4-sm") {
                    display = FHD.locale.get("fhd.alarmplan.form.hight");
                } else if (v == "icon-ibm-symbol-6-sm") {
                    display = FHD.locale.get("fhd.alarmplan.form.low");
                } else if (v == "icon-ibm-symbol-5-sm") {
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
        });

        ccols.push({
            header: '实际值' + '<font color=red>*</font>',
            dataIndex: 'value',
            sortable: false,
            flex: 0.3,
            align: 'right',
            editor: {
                xtype: 'numberfield',
                allowDecimals: true
            }
        });

        ccols.push({
            header: '采集说明',
            dataIndex: 'desc',
            sortable: false,
            align: 'right',
            flex: 2,
            editor: {
                xtype: 'textareafield',
                height: 100
            },
            renderer: function (value, metaData, record, colIndex, store, view) {
                return "<div style=' background-repeat: no-repeat;" +
                    "background-position: center top;' data-qtitle='' " +
                    "class='" + "" + "' data-qwidth='100' data-qalign='tl-br' data-qtip='" + value + "'>" + value + "</div>";
            }
        });
        ccols.push({
            header: '发生时间' + '<font color=red>*</font>',
            dataIndex: 'date',
            align: 'right',
            sortable: false,
            flex: 0.4,
            renderer: function (value) {
                if (value instanceof Date) {
                    return Ext.Date.format(value, 'Y-m-d H:i:s');
                } else {
                    return value;
                }
            },
            editor: new Ext.form.DateField({
                //在编辑器里面显示的格式,这里为10/20/09的格式  
                format: 'Y-m-d H:i:s' //默认配置
            })
        });



        Ext.apply(me, {
            pagable: true,
            tbarItems: [{
                    iconCls: 'icon-add',
                    name:'history_add',
                    handler: function () {
                        me.addEvent(); //添加记录
                    },
                    scope: this
                }, {
                    iconCls: 'icon-del',
                    name:'history_del',
                    handler: function () {
                        me.remove(me); //删除记录
                    },
                    scope: this
                }, {
                    text: FHD.locale.get('fhd.common.save'),
                    name:'history_save',
                    iconCls: 'icon-save',
                    handler: function () {
                        me.save(me)
                    },
                    disabled: false,
                    scope: this
                }

            ],
            cols: ccols
        });


        me.callParent(arguments);
        
        me.store.on('load', function () {
            me.setstatus();
        });
        
        me.on('selectionchange', function (model, selected, eOpts) {
            me.setstatus();
        });
    },
    
    reload:function(){
    	var me = this;
    	me.store.proxy.url = __ctxPath + "/kpi/real/findrealtimekpihistorydata.f";
    	me.store.proxy.extraParams = {
            kpiId: me.paramObj.kpiId
        };
        me.store.load();
    },
    
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    }


});