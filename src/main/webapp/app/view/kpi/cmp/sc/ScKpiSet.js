Ext.define('FHD.view.kpi.cmp.sc.ScKpiSet', {
    extend: 'FHD.ux.EditorGridPanel',
    border: false,
    pagable: false,
    checked: false,
    requires: [
              ],
              
    url: __ctxPath + '/kpi/category/findkpiRelaCategory.f',
    extraParams: {
    	currentScId: ''
    },
    cols: [{
        header: FHD.locale.get("fhd.strategymap.strategymapmgr.form.kpiname"), //指标名称
        flex: 1,
        dataIndex: 'name',
        sortable: false,
        flex: 1.5,
        renderer: function (value, metaData, record, rowIndex, colIndex, store) {
            return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
        }
    }, {
        header: FHD.locale.get('fhd.strategymap.strategymapmgr.form.Dept'), //所属部门
        dataIndex: 'dept',
        sortable: false,
        flex: 3
    }, 
    {
        header: FHD.locale.get("fhd.strategymap.strategymapmgr.form.weight"), //权重
        renderer: function (value, metaData, record, rowIndex, colIndex, store) {
            return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
        },
        dataIndex: 'weight',
        sortable: false,
        flex: 0.5,
        editor: {
            allowBlank: false,
            xtype: 'numberfield',
            maxValue: 100,
            minValue: 1,
            allowDecimals: true,
            nanText: FHD.locale.get('fhd.strategymap.strategymapmgr.prompt.inputNum'),
            step: 1
        },
        renderer: function (value, metaData, record, rowIndex, colIndex, store) {
           var renderValue = "";
           if(null!=value){
           		renderValue = value;
           }
           return renderValue;
        }
    }, 
    {
        header: FHD.locale.get('fhd.strategymap.strategymapmgr.form.oper'), //操作
        dataIndex: 'oper',
        xtype: 'templatecolumn',
        sortable: false,
        flex: 0.5,
        text: $locale('fhd.common.delete'),
        tpl: '<font class="icon-del-min" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
        listeners: {
            click: {
                fn: function (g, d, i) {
                    g.store.removeAt(i);
                }
            }
        }
    }],
    /**
     * 查找衡量指标列表中的指标和权重信息
     */
    findKpiObjects: function () {
        var me = this;
        var i = 0;
        var selectedvalues = {
            data: {
                items: []
            }
        };
        var kpiWeight = {};
        var items = me.store.data.items;
        Ext.Array.each(items, function (object) {
            var item = object.data;
            var insertobj = {
                data: {
                    id: item.id,
                    name: item.name
                }
            }
            selectedvalues.data.items[i++] = insertobj;
            selectedvalues.data.length = i;
            kpiWeight[item.id] = item.weight;
        });
        return {
            'kpiWeight': kpiWeight,
            'kpiarr': selectedvalues
        };
    },

    selectorWindowonsubmit: function (store) {
        var me = this;
        var list = [];
        var idArray = [];
        var kpiobj = me.findKpiObjects();
        me.store.removeAll();
        var items = store.data.items;
        Ext.Array.each(items, function (obj) {
            var item = obj.data;
            var kpim = new kpiModel({
                id: item.id,
                name: item.name,
                weight: kpiobj.kpiWeight[item.id]
            });
            idArray.push(kpim.data.id);
            list.push(kpim);
        });
        //需要查询所属部门
        FHD.ajax({
        	async:false,
            url: __ctxPath + '/kpi/kpistrategymap/findkpirelaorgempbyid.f',
            params: {
                kpiID: Ext.JSON.encode(idArray)
            },
            callback: function (data) {
                if (data && data.kpiRelOrg) {
                    var vobj = Ext.JSON.decode(data.kpiRelOrg);
                    for (var i = 0; i < list.length; i++) {
                        list[i].set("dept", vobj[list[i].get("id")].orgName);
                        me.store.add(list[i]);
                    }
                }
            }
        });
    },
    /**
     * 指标选择弹出窗口函数
     */
    popKpiSelectorWindow: function () {
        var me = this;
        var kpiobj = me.findKpiObjects();
        me.kpiselectorwindow = Ext.create('FHD.ux.kpi.opt.KpiSelectorWindow', {
            multiSelect: true,
            closeAction: 'destroy',
            selectedvalues: [],
            onSubmit: function (store) {
                me.selectorWindowonsubmit(store);
            }
        });
        //清除弹出窗口中,已选择的指标
        me.kpiselectorwindow.resetSelectGrid();
        me.kpiselectorwindow.setSelectedValue(kpiobj.kpiarr);
        me.kpiselectorwindow.show();
        me.kpiselectorwindow.addComponent();
    },

    /**
     * 点击下一步提交事件
     */
    last: function () {
        var me = this;
        var flag = false;
        var parameter = "";
        var storeItems = me.store.data.items;
        var sumWeight = 0;
        Ext.Array.each(storeItems, function (object) {
            var item = object.data;
            
            if(null!=item.weight){
	            sumWeight+=item.weight;
            }
            
            /*if (!(item.weight >= 1 && item.weight <= 100)) {
                flag = true;
            }*/
            parameter += item.id + "," + (item.weight==null?'':item.weight) + ";";
        });
        if(sumWeight>100){
        	FHD.alert("权重之和不能大于100，请重新设置。");
        	return false;
        }
        /*if (flag) {
            Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get("fhd.strategymap.strategymapmgr.form.weighterror"));
            return;
        }*/
        /* 提交指标数据*/
        if (!flag) {
            FHD.ajax({
            	async:false,
                params: {
                    "kpiParam": parameter,
                    "currentScId": me.paramObj.scid
                },
                url: __ctxPath + '/kpi/category/mergescrelakpi.f',
                callback: function (ret) {
                    if (ret && ret.success) {
                        FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                    }
                }
            });
        }
        me.store.commitChanges();
    },

    initComponent: function () {
        var me = this;

        

        Ext.define('kpiModel', {
            extend: 'Ext.data.Model',
            fields: ['id', 'code', 'text', 'name', 'dbid', 'type', 'sort', 'weight', 'oper', 'dept']
        });

        Ext.apply(me, {
            tbarItems: [{
            	text:FHD.locale.get('fhd.strategymap.strategymapmgr.form.set'),
                tooltip: FHD.locale.get('fhd.strategymap.strategymapmgr.form.set'),
                iconCls: 'icon-cog',
                columnWidth: 0.1,
                scope: this,
                handler: function () {
                    //弹出指标选择按钮
                    me.popKpiSelectorWindow();
                }
            }]
        });

        me.callParent(arguments);
    },

    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    reloadData: function () {
        var me = this;
        me.store.proxy.extraParams.currentScId = me.paramObj.scid;
        me.store.load();
    }

});