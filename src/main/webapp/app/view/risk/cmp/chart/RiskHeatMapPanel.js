/**
 * 风险图谱
 *
 * @author ZJ
 */
Ext.define('FHD.view.risk.cmp.chart.RiskHeatMapPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.riskheatmappanel',

    autoScroll: true,
    type: '',
    //评估计划id，适应评估计划的列表
    assessPlanId: '',
    currentId: '',
    //是否显示集团，还是当天用户的公司
    showgroup: false,
    //点击数量是否显示风险列表
    showgrid: true,
    //是否直接只是显示当前登录人员所在公司
    currentCompany : false,
    
    initComponent: function () {
        var me = this;
        var comStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: __ctxPath + '/cmp/risk/finddimensiondimlist.f',
                reader: {
                    type: 'json',
                    root: 'datas',
                    totalProperty: 'totalCount'
                }
            }

        });
        me.XAxisCom = Ext.create('Ext.form.ComboBox', {
            name: 'xAxisCom',
            store: comStore,
            displayField: 'name',
            valueField: 'id',
            labelAlign: 'left',
            fieldLabel: '横坐标',
            multiSelect: false,
            margin: '7 30 5 30', // emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示
            triggerAction: 'all',
            editable: false,
            columnWidth: .5,
            listeners: {
                change: function (field, newValue, oldValue, eOpts) {
                    if (newValue != null && newValue != '') {
                        if (me.currentId != '' && me.YAxisCom.getValue() != '' && me.YAxisCom.getValue() != null) {
                            me.reloadChart(me.currentId, me.type, newValue, me.YAxisCom.getValue(), me.showgroup);
                        }
                    }
                }
            }
        });

        me.YAxisCom = Ext.create('Ext.form.ComboBox', {
            name: 'yAxisCom',
            store: comStore,
            displayField: 'name',
            valueField: 'id',
            labelAlign: 'left',
            fieldLabel: '纵坐标',
            multiSelect: false,
            margin: '7 30 5 30', // emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示
            triggerAction: 'all',
            editable: false,
            columnWidth: .5,
            listeners: {
                change: function (field, newValue, oldValue, eOpts) {
                    if (newValue != null && newValue != '') {
                        if (me.currentId != '' && me.XAxisCom.getValue() != '' && me.XAxisCom.getValue() != null) {
                            me.reloadChart(me.currentId, me.type, me.XAxisCom.getValue(), newValue, me.showgroup);
                        }
                    }
                }
            }
        });
        FHD.ajax({
            url: __ctxPath + '/cmp/risk/finddimensiondimlist.f',
            params: {},
            callback: function (result) {
                if (result && result.success) {
                    Ext.each(result.datas, function (item) {
                        if (item.name == '影响程度') {
                            me.XAxisCom.setValue(item.id);
                        }
                        if (item.name == '发生可能性') {
                            me.YAxisCom.setValue(item.id);
                        }
                    });
                }
            }
        });

        var data = '';
        me.chartcontainer = Ext.create('FHD.ux.FusionChartPanel', {
            chartType: 'HeatMap',
            columnWidth: .1,
            border: false,
            flex: 9,
            xmlData: data
        });

        me.comfieldSet = Ext.create('Ext.form.FieldContainer', {
            autoScroll: true,
            layout: 'column',
            margin: '0 0 0 0',
            flex: 1,
            items: [me.YAxisCom, me.XAxisCom]
        });

        me.containerfieldSet = Ext.create('Ext.form.FieldContainer', {
            autoScroll: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [me.comfieldSet, me.chartcontainer]
        });

        Ext.applyIf(me, {
            layout: 'fit',
            items: [me.containerfieldSet],
            listeners : {
            	afterlayout : function(){
            		var me = this;
            		comStore.load();
            	}
            }
        });
        me.callParent(arguments);
    },

    reloadData: function (id) {
    	debugger;
        var me = this;
        if(me.currentCompany){
        	id = __user.majorDeptId;
        }
        if (id == null || id == '') {
            if (me.XAxisCom.getValue() != '' && me.XAxisCom.getValue() != null && me.YAxisCom.getValue() != '' && me.YAxisCom.getValue() != null) {
                me.reloadChart(me.currentId, me.type, me.XAxisCom.getValue(), me.YAxisCom.getValue(), me.showgroup);
            }
        } else {
            me.currentId = id;
            if (me.XAxisCom.getValue() != '' && me.XAxisCom.getValue() != null && me.YAxisCom.getValue() != '' && me.YAxisCom.getValue() != null) {
                me.reloadChart(me.currentId, me.type, me.XAxisCom.getValue(), me.YAxisCom.getValue(), me.showgroup);
            }
        }
    },

    reloadChart: function (id, type, xvalue, yvalue, showgroup) {
    	debugger;
        var me = this;
        FHD.ajax({
            url: __ctxPath + '/cmp/risk/getheatmap.f',
            params: {
                id: id,
                type: type,
                xvalue: xvalue,
                yvalue: yvalue,
                showgroup: showgroup,
                assessPlanId: me.assessPlanId,
                meid: me.id,
                schm: me.schm
            },
            callback: function (result) {
                if (result && result.success) {
                    me.chartcontainer.loadXMLData(result.data);
                }
            }
        })
    },
    showHeatRiskGridPanel: function (id, type, xvalue, yvalue, showgroup, assessPlanId, xscore, yscore) {
        var me = this;
        if (me.showgrid) {
            var colsArray = new Array();
            colsArray.push({
                header: me.YAxisCom.getRawValue(),
                dataIndex: 'yAxisCom',
                sortable: false,
                width: 100
            });
            colsArray.push({
                header: me.XAxisCom.getRawValue(),
                dataIndex: 'xAxisCom',
                sortable: false,
                width: 100
            });
            var riskHeatRiskGridPanel = Ext.create('FHD.view.risk.cmp.chart.RiskHeatMapGridPanel', {
                colsArray: colsArray,
                showRiskDetail: function (p) {
                    me.showRiskDetail(p);
                },
                goback: function () {
                    me.goback();
                }
            });
            var window = Ext.create('FHD.ux.Window', {
                title: '风险图谱详情',
                maximizable: true,
                modal: true,
                width: 800,
                height: 500,
                collapsible: true,
                autoScroll: true,
                items: riskHeatRiskGridPanel
            }).show();
            riskHeatRiskGridPanel.initParam({
                id: id,
                type: type,
                xvalue: xvalue,
                yvalue: yvalue,
                showgroup: showgroup,
                assessPlanId: assessPlanId,
                xscore: xscore,
                yscore: yscore
            });
            riskHeatRiskGridPanel.reloadData();
        }
    },

    initParams: function (type, id) {
        var me = this;
        if (id != null && id != '') {
            me.currentId = id;
        }
        me.type = type;
    }

})