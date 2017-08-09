Ext.define('FHD.view.kpi.cmp.kpi.KpiBasicInfoForm', {
    extend: 'Ext.form.Panel',
    border: false,
    waitMsgTarget: true,
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },
    addComponent: function () {
        var me = this;
        var basicfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //基本信息fieldset
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '3 30 3 30',
                height: 24,
                labelWidth: 100
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.common.baseInfo'),
            items: [

            ]
        });

        // 指标名称
        me.kpiName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            //disabled: true,
            name: 'kpiName',
            fieldLabel: '指标名称', //上级维度
            margin: '7 30 3 30',
            maxLength: 200,
            columnWidth: .5,
            allowBlank: false
        });

        basicfieldSet.add(me.kpiName);
        // 指标编号
        me.kpiCode = Ext.widget('displayfield', {
            xtype: 'displayfield',
            //disabled: true,
            name: 'kpiCode',
            fieldLabel: '编号', //上级维度
            margin: '7 30 3 30',
            maxLength: 200,
            columnWidth: .5,
            allowBlank: false
        });

        basicfieldSet.add(me.kpiCode);
        // 指标类型
        me.kpiType = Ext.widget('displayfield', {
            xtype: 'displayfield',
            //disabled: true,
            name: 'kpiType',
            fieldLabel: '指标类型', //上级维度
            margin: '7 30 3 30',
            maxLength: 200,
            columnWidth: .5,
            allowBlank: false
        });
        basicfieldSet.add(me.kpiType);
        // 上级指标
        me.parentKpi = Ext.widget('displayfield', {
            xtype: 'displayfield',
            //disabled: true,
            name: 'parentKpi',
            fieldLabel: '上级指标', //上级维度
            margin: '7 30 3 30',
            maxLength: 200,
            columnWidth: .5,
            allowBlank: false
        });
        basicfieldSet.add(me.parentKpi);
        //所属部门人员
        me.ownDept = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept"), //所属部门人员
            labelAlign: 'left',
            columnWidth: .5,
            name: 'ownDept'
        });
        basicfieldSet.add(me.ownDept);

        //采集部门人员
        me.gatherDept = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.gatherdept'), //采集部门
            labelAlign: 'left',
            columnWidth: .5,
            name: 'gatherDept'

        });
        basicfieldSet.add(me.gatherDept);

        // 目标部门
        me.targetDept = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetdept'),
            labelAlign: 'left',
            columnWidth: .5,
            name: 'targetDept'
        });
        basicfieldSet.add(me.targetDept);

        //报告人
        me.reportDept = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.reportEmp"), //报告人
            labelAlign: 'left',
            columnWidth: .5,
            name: 'reportDept'
        });
        basicfieldSet.add(me.reportDept);

        //实际值公式
        me.resultFormula = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.resultFormula'), //实际值公式
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 5,
            name: 'resultFormula',
            column: 'resultValueFormula',
            labelWidth: 100,
            columnWidth: 0.5
        });
        basicfieldSet.add(me.resultFormula);

        //目标值公式
        me.targetFormula = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetFormula'), //评估值公式
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 5,
            name: 'targetFormula',
            column: 'targetValueFormula',
            labelWidth: 100,
            columnWidth: 0.5
        });
        basicfieldSet.add(me.targetFormula);
        //评估值公式
        me.assessmentFormula = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.assessmentFormula'), //评估值公式
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 5,
            name: 'assessmentFormula',
            column: 'assessmentValueFormula',
            labelWidth: 100,
            columnWidth: 1.0
        });
        basicfieldSet.add(me.assessmentFormula);
        var alarmField = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '告警方案',
            name: 'alarmPlanName',
            labelAlign: 'left',
            columnWidth: .5
        });
        basicfieldSet.add(alarmField);
        me.add(basicfieldSet);
        me.alarmSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //基本信息fieldset
            collapsible: true,
            flex: 1,
            collapsed: false,
            // overflow: 'auto',
            defaults: {
                margin: '7 30 7 30',
                height: 24,
                labelWidth: 100
            },
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            title: '告警信息',
            items: [

            ]
        });
        me.add(me.alarmSet);

    },
    /**
     * 初始化组件方法
     */
    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            autoScroll: true,
            border: me.border,
            layout: 'column',
            width: FHD.getCenterPanelWidth() - 258,
            bodyPadding: "0 3 3 3"
        });

        me.callParent(arguments);

        //向form表单中添加控件
        me.addComponent();

    },
    reloadData: function () {
        var me = this;
        FHD.ajax({
            async: false,
            url: __ctxPath + '/kpi/kpi/loadkpiDetailbyid.f',
            params: {
                id: me.paramObj.kpiid
            },
            callback: function (data) {
                me.form.setValues({
                    kpiName: data.kpiName,
                    kpiCode: data.kpiCode,
                    kpiType: data.kpiType,
                    parentKpi: data.parentKpi,
                    gatherDept: data.gatherDept,
                    ownDept: data.ownDept,
                    targetDept: data.targetDept,
                    reportDept: data.reportDept,
                    resultFormula: data.resultFormula,
                    targetFormula: data.targetFormula,
                    assessmentFormula: data.assessmentFormula,
                    alarmPlanName: data.alarmPlanName

                });
                var url = __ctxPath + "/kpi/alarm/findalarmplanregions.f";
                if (!me.panel) {
                    me.panel = Ext.create('FHD.view.kpi.cmp.sc.scWarningForm', {
                        paramId: data.alarmId,
                        url: url,
                        extraParams: {
                            id: data.alarmId
                        },
                        flex: 2
                    });
                    me.alarmSet.add(me.panel);
                } else {
                    me.panel.store.proxy.url = url;
                    me.panel.store.proxy.extraParams.id = data.alarmId;
                    me.panel.store.load();
                }
                if (!data.parentname) {
                    me.parentKpi.setValue('指标库');
                }

            }
        });
    }
})