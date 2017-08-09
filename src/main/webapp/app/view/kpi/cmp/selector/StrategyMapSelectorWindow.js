Ext.define('FHD.view.kpi.cmp.selector.StrategyMapSelectorWindow', {
	 extend: 'Ext.window.Window',
	    alias: 'widget.strategymapSelectorwindow',
	    requires: ['FHD.ux.kpi.KpiStrategyMapTree'],
	    constrain: true,
	    width: 800,
	    height: 600,
	    modal: true,
	    collapsible: true,
	    maximizable: true,
	    layout: {
	        type: 'border'
	    },

	    title: $locale('kpistrategymapselectorwindow.title'),

	    // 单选部门
	    single: true,
	    checkModel: 'multiple',

	    values: new Array(),
	    tree: null,
	    grid: null,
	    buttons: null,
	    extraParams: {},

	    //是否显示机构目标树
	    OrgSmTreeVisible: false,
	    //是否显示目标树
	    smTreeVisible: true,
	    //是否显示我的目标树
	    mineSmTreeVisible: false,
	    //设置机构目标树图标
	    orgSmTreeIcon: 'icon-org',
	    //设置目标树图标
	    smTreeIcon: 'icon-flag-red',
	    //设置我的目标树图标
	    mineSmTreeIcon: 'icon-orgsub',

	    //赋值给grid,参数类型为store
	    setValue: function (selecteds) {
	        var me = this;
	        var value = new Array();
	        if (me.grid) {
	            if (Ext.typeOf(selecteds) == 'array') {
	                Ext.Array.each(selecteds, function (selected) {
	                    me.grid.store.insert(me.grid.store.count(), selected);
	                    value.push(selected.dbid);
	                });
	            }
	            if (Ext.typeOf(selecteds) == 'object') {
	                selecteds.each(function (selected) {
	                    me.grid.store.insert(me.grid.store.count(), selected);
	                    value.push(selected.data.dbid);
	                });
	            }
	        }
	        me.values = value;
	        this.setTreeValue(value);
	    },
	    setTreeValue: function (values) {
	        var me = this;
	        if (me.tree) {
	            me.tree.values = values;
	            me.tree.setTreeValues(true);
	        }
	    },
	    initComponent: function () {
	        var me = this;

	        me.valueTypeStore = Ext.create('Ext.data.Store', {
	            fields: ['id', 'name'],
	            data: [{
	                'id': '评估值',
	                'name': '评估值'
	            }]
	        });

	        if (me.single) {
	            me.checkModel = 'single';
	        }
	        me.tree = Ext.create('FHD.view.kpi.cmp.selector.StrategyMapTreeSelector', {
	            split: true,
	            region: 'west',
	            width: 220,
	            maxWidth: 300,
//	            checkModel: me.checkModel,
//	            OrgSmTreeVisible: me.OrgSmTreeVisible,
	            smTreeVisible: me.smTreeVisible,
//	            mineSmTreeVisible: me.mineSmTreeVisible,
	            canChecked: true,
	            single: me.single,
	            //设置机构目标树图标
//	            orgSmTreeIcon: me.orgSmTreeIcon,
	            //设置目标树图标
	            smTreeIcon: me.smTreeIcon,
	            //设置我的目标树图标
//	            mineSmTreeIcon: me.mineSmTreeIcon,
	            extraParams: me.extraParams,
	            smClickFunction: function (node) {
//	                if (!node.data.root) {
//	                    if (!me.grid.store.getById(node.data.id)) {
//	                        if (!me.multiSelect) {
//	                            me.grid.store.removeAll();
//	                        }
//	                        me.grid.store.insert(me.grid.store.count(), node);
//	                    }
//	                }
	                if (!me.grid.store.getById(node.data.id)) {
                        if (!me.multiSelect) {
                            me.grid.store.removeAll();
                        }
                        me.grid.store.insert(me.grid.store.count(), node);
                    }
	            },
//	            onCheckchange: function (node, checked) {
//	                var value = {
//	                    id: node.data.dbid,
//	                    dbid: node.data.dbid,
//	                    code: node.data.code,
//	                    text: node.data.text
//	                };
//	                if (checked) {
//	                    if (me.single) {
//	                        me.grid.store.removeAll();
//	                    }
//	                    if (!me.grid.store.getById(node.data.id)) {
//	                        me.grid.store.insert(me.grid.store.count(), value);
//	                    }
//	                } else {
//	                    me.grid.store.remove(me.grid.store.getById(node.data.dbid));
//	                }
//	                me.tree.values = new Array();
//	                me.grid.store.each(function (value) {
//	                    me.tree.values.push(value.data.id);
//	                })
//	                me.tree.setTreeValues(checked);
//	            }
	        });
	        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
	            clicksToEdit: 1
	        });

	        me.grid = Ext.create('Ext.grid.Panel', {
	            region: 'center',
	            tbar: new Ext.Toolbar({
	                height: 25,
	                items: [{
	                    xtype: "tbtext",
	                    text: $locale('kpistrategymapselectorwindow.selectgrid.title')
	                }, '-']
	            }),
	            plugins: [cellEditing],
	            store: Ext.create('Ext.data.Store', {
	                idProperty: 'id',
	                fields: ['id', 'code', 'name', 'valueType']
	            }),
	            columns: [{
	                xtype: 'gridcolumn',
	                hidden: true,
	                dataIndex: 'id'
	            }, {
	                xtype: 'gridcolumn',
	                hidden: true,
	                dataIndex: 'code'
	            }, {
	                xtype: 'gridcolumn',
	                dataIndex: 'text',
	                flex: 1,
	                text: $locale("fhd.pages.test.field.name"),
	                renderer: function (value, metaData, record, rowIndex,
	                    colIndex, store) {
	                    return "<div data-qtitle='' data-qtip='" + value + "'>" + value + "</div>";
	                }
	            }, {
	                xtype: 'templatecolumn',
	                text: $locale('fhd.common.delete'),
	                align: 'center',
	                tpl: '<font class="icon-del-min" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
	                listeners: {
	                    click: {
	                        fn: function (grid, d, i) {
	                            Ext.Array.remove(me.tree.values, grid.store.getAt(i).data.dbid);
	                            if (me.multiSelect) {
	                                me.tree.setTreeValues(false);
	                            }
	                            grid.store.removeAt(i);
	                        }
	                    }
	                }
	            }]
	        });

	        me.buttons = [{
	            xtype: 'button',
	            text: $locale('fhd.common.confirm'),
	            handler: function () {
	                me.onSubmit(me.grid.store);
	                me.close();
	            }
	        }, {
	            xtype: 'button',
	            text: $locale('fhd.common.close'),
	            style: {
	                marginLeft: '10px'
	            },
	            handler: function () {
	                me.close();
	            }
	        }];

	        Ext.applyIf(me, {
	            items: [me.tree, me.grid]
	        });

	        me.callParent(arguments);
	        me.setValue(me.values);
	    }

})