Ext.define('FHD.view.kpi.cmp.sm.SmBasicInfoForm', {
    extend: 'Ext.form.Panel',
    border: false,
    //waitMsgTarget:true,
    paramObj: {
        smid: '', //目标ID
        parentid: 'sm_root', //父目标ID
        parentname: '目标库', //父目标名称
        editflag: false, //是否是编辑状态
        smname: '' //当前目标名称
    },

    /**
     * form表单中添加控件
     */
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

        //上级目标
        me.parentname = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.parent'), //上级目标
            name: 'parentname',
            margin: '7 30 3 30',
            columnWidth: .5
        });

        basicfieldSet.add(me.parentname);

        //编码
        var code = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.code'), //编码
            margin: '7 30 3 30',
            name: 'code',
            columnWidth: .4
        });
        basicfieldSet.add(code);

        //名称
        var name = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 2,
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.name'), //名称
            margin: '7 30 3 30',
            name: 'name',
            columnWidth: .5
        });
        basicfieldSet.add(name);

        //短名称
        var shortName = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 2,
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.shortName'), //短名称
            margin: '7 30 3 30',
            name: 'shortName',
            columnWidth: .5
        });
        basicfieldSet.add(shortName);
        
        //所属部门人员
        me.ownDept = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept"), //所属部门人员
            labelAlign: 'left',
            columnWidth: .5,
            margin: '7 30 3 30',
            name: 'ownDept'
        });
        basicfieldSet.add(me.ownDept);
        
        //图表类型
        me.chartTypeStr = Ext.widget('displayfield', {
            xtype: 'displayfield',
            labelWidth: 100,
            name: 'chartTypeStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.category.charttype'), //图表类型
             margin: '7 30 3 30',
            columnWidth: .5,
            labelAlign: 'left'
        });

        basicfieldSet.add(me.chartTypeStr);

//        //查看人
//        me.viewDept = Ext.widget('displayfield', {
//            xtype: 'displayfield',
//            fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.viewEmp"), //查看人
//            labelAlign: 'left',
//            columnWidth: .5,
//            name: 'viewDept'
//
//        });
//        basicfieldSet.add(me.viewDept);
//
//        //报告人
//        me.reportDept = Ext.widget('displayfield', {
//            xtype: 'displayfield',
//            fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.reportEmp"), //报告人
//            labelAlign: 'left',
//            columnWidth: .5,
//            name: 'reportDept'
//        });
//        basicfieldSet.add(me.reportDept);




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
            margin: '7 30 3 30',
            labelWidth: 100,
            columnWidth: 1.0
        });
        basicfieldSet.add(me.assessmentFormula);
        
        //预警公式
        me.warningFormula = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.warningFormula'),
            labelAlign: 'left',
            flex: 1.5,
            labelWidth: 100,
            cols: 20,
            rows: 5,
            margin: '7 30 3 30',
            name: 'warningFormula',
            columnWidth: 1.0
        });
        basicfieldSet.add(me.warningFormula);

        //说明
        var desc = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 5,
            labelAlign: 'left',
            name: 'desc',
            margin: '7 30 3 30',
            fieldLabel: FHD.locale.get('fhd.sys.dic.desc'), //说明
            columnWidth: .5
        });
        basicfieldSet.add(desc);
        var alarmField = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '告警方案',
            name: 'alarmPlanName',
            labelAlign: 'left',
            margin: '7 30 3 30',
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

    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            autoScroll: true,
            layout: 'column',
            width: FHD.getCenterPanelWidth() - 258,
            bodyPadding: "0 3 3 3"

        });
        me.callParent(arguments);

        //向form表单中添加控件
        me.addComponent();
    },
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    reloadData: function () {
        var me = this;
        FHD.ajax({
            async: false,
            //waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
            url: __ctxPath + '/kpi/kpistrategymap/loadsmDetailbyid.f',
            params: {
                id: me.paramObj.smid
            },
            callback: function (data) {
                me.form.setValues({
                    parentname: data.parentname,
                    code: data.code,
                    name: data.name,
                    shortName: data.shortName,
                    desc: data.desc,
                    ownDept: data.ownDept,
                    assessmentFormula: data.assessmentFormula,
                    warningFormula: data.warningFormula,
                    chartTypeStr: data.chartTypeStr,
//                    reportDept: data.reportDept,
//                    viewDept: data.viewDept,
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
                    me.parentname.setValue('目标库');
                }
            }
        });


    }

});