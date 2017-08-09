Ext.define('FHD.view.kpi.export.kpitype.KpiTypeImportGrid', {
    extend: 'FHD.ux.GridPanel',
    initComponent: function () {
        var me = this;
        var url = __ctxPath + '/kpi/kpi/QuerykpiTypeImportData.f';
        var tbar = [ // 菜单项
            {
                text: "导入数据",
                iconCls: 'icon-save',
                handler: function () {
                    me.importData();
                },
                scope: this
            }
        ];
        me.cols = [{
            header: 'id',
            dataIndex: 'id',
            sortable: true,
            flex: 1,
            hidden: true
        }, {
            header: '行号',
            dataIndex: 'rowNum',
            sortable: true,
            flex: 0.3
        },{
            header: '指标名称',
            dataIndex: 'kpiName',
            sortable: true,
            flex: 1
//            renderer: function (v, rowIndex, cellIndex) {
//                var kpiTmpid = cellIndex.data.id;
//                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').editRecord('" + kpiTmpid + "')\" >" + v + "</a>";
//            }
        }, {
            header: '编号',
            dataIndex: 'code',
            sortable: true,
            flex: 0.5
        }, {
            header: '校验结果',
            dataIndex: 'validateInfo',
            sortable: true,
            flex: 2,
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + (value?value:'') + '"';
                return value;
            }
        }];
        Ext.apply(me, {
            multiSelect: true,
            border: false,
            rowLines: true, // 显示横向表格线
            columnLines: true,
            checked: false, // 复选框
            autoScroll: true,
            cols: me.cols, // cols:为需要显示的列
            extraParams: {
            	type: me.type
            },
            url: url,
            pagable: false,
            //tbarItems: tbar,
            viewConfig: {
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return record.get("validateInfo") ? "row-s" : "";
                }
            }
        });

        me.callParent(arguments);
    },
    editRecord: function (id) {
        var me = this;
        me.window = Ext.create('FHD.ux.Window', {
            title: '指标类型数据',
            items: [],
            closeAction: 'destroy',
            maximizable: true,
            collapsible: true
        });
        me.kpiTmpDetail = Ext.create('FHD.view.kpi.export.kpitype.KpiTypeImportDetailForm', {
            kpiTmpId: id,
            pcontainer: me
        });
        me.kpiTmpDetail.reloadData();
        me.window.on('close', function (panel, eOpts) {
            // me.store.load();
        });
        me.window.add(me.kpiTmpDetail);
        me.window.show();
    },
    reloadData: function () {
        var me = this;
        if (me.window) {
            me.window.close();
        }
        me.store.load();
    },
    importData: function () {
        var me = this;
        var msg;
        if(me.store.getAt(0)&& me.store.getAt(0).get("validateInfo")) {
        	msg = '存在未校验通过的数据，继续导入么？';
        } else {
        	msg = '确认导入列表中的数据么';
        }        
        Ext.Msg.confirm('提示', msg, function (g) {
            if (g == 'yes') {
                FHD.ajax({
                    async: false,
                    //waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
                    url: me.importUrl,
                    addStyle: me.addStyle,
                    callback: function (data) {
                    	if(data.success) {
                    		 FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
                    	} else {
                    		FHD.notification('导入过程出现错误', FHD.locale.get('fhd.common.prompt'));
                    	}
                        me.pcontainer.window.close();
                    }
                });

            } else {

            }
        })
    }

})