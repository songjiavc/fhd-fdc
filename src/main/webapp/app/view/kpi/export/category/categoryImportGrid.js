Ext.define('FHD.view.kpi.export.category.categoryImportGrid', {
    extend: 'FHD.ux.GridPanel',
    initComponent: function () {
        var me = this;
        var url = __ctxPath + '/sc/import/findinvalidatescdata.f';
        var tbar = [
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
            sortable: false,
            flex: 0.3
        },{
            header: '记分卡名称',
            dataIndex: 'name',
            sortable: false,
            flex: 1
        }, {
            header: '记分卡编号',
            dataIndex: 'code',
            sortable: false,
            flex: 0.5
        }, {
            header: '校验结果',
            dataIndex: 'validateInfo',
            sortable: false,
            flex: 2,
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
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
            extraParams: me.extraParams,
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
    reloadData: function () {
        var me = this;
        if (me.window) {
            me.window.close();
        }
        me.store.load();
    },
    importData: function () {
        var me = this;
        var msg = '存在未校验通过的数据，继续导入么？';
        Ext.Msg.confirm('提示', msg, function (g) {
            if (g == 'yes') {
                FHD.ajax({
                    async: false,
                    url: __ctxPath + '/sc/import/confirmimportscdata.f?isCoverage='+me.isCoverage,
                    addStyle: me.addStyle,
                    callback: function (data) {
        	  			if (data) {
                            FHD.notification(FHD.locale.get('fhd.common.operateSuccess'), FHD.locale.get('fhd.common.prompt'));
                        } else {
                            FHD.notification('导入过程出现错误', FHD.locale.get('fhd.common.prompt'));
                        }
                        me.pcontainer.window.close();
                    }
                });

            } 
        })
    }

})