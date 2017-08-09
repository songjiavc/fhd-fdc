/**
 * 添加记分卡,基本信息面板
 * 继承于Ext.form.Panel
 *
 */
Ext.define('FHD.view.kpi.cmp.sc.ScBasicInfoForm', {
    extend: 'Ext.form.Panel',
    border: false,
    waitMsgTarget: true,
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
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
        //上级维度
        me.parentCategory = Ext.widget('displayfield', {
            xtype: 'displayfield',
            //disabled: true,
            name: 'parentStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.parentCategory'), //上级维度
            margin: '7 30 3 30',
            maxLength: 200,
            columnWidth: .5,
            allowBlank: false
        });

        basicfieldSet.add(me.parentCategory);

        //编码
        var code = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.code'), //编码
            margin: '7 30 3 30',
            name: 'code',
            maxLength: 255,
            columnWidth: .4
        });
        basicfieldSet.add(code);



        //名称
        var name = Ext.widget('displayfield', {
            xtype: 'displayfield',
            name: 'name',
            rows: 3,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.name'), //名称
            value: '',
            margin: '7 30 3 30',
            columnWidth: .5,
            allowBlank: false
        });
        basicfieldSet.add(name);
        //说明
        var desc = Ext.widget('displayfield', {
            xtype: 'displayfield',
            rows: 3,
            labelAlign: 'left',
            name: 'desc',
            fieldLabel: FHD.locale.get('fhd.sys.dic.desc'), //说明
            margin: '7 30 3 30',
            columnWidth: .5
        });
        basicfieldSet.add(desc);

        //所属部门人员
        me.owndepts = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept"), //所属部门人员
            labelAlign: 'left',
            columnWidth: .5,
            name: 'ownDept',
            multiSelect: false,
            margin: '7 30 3 30',
            allowBlank: false
        });
        basicfieldSet.add(me.owndepts);

        //是否启用
        me.statu = Ext.widget('displayfield', {
            xtype: 'displayfield',
            margin: '7 30 3 30',
            name: 'statusStr',
            columns: 4,
            fieldLabel: FHD.locale.get('fhd.common.enable'), //是否启用
            labelAlign: 'left',
            allowBlank: false,
            columnWidth: .5
        });
        basicfieldSet.add(me.statu);
        //评估值公式
        me.assessmentFormula = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.assessmentFormula'), //评估值公式
            hideLabel: false,
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 3,
            name: 'assessmentFormula',
            margin: '7 30 3 30',
            columnWidth: .5
        });
        basicfieldSet.add(me.assessmentFormula);

        //预警公式
        me.forecastFormula = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.warningFormula'),
            hideLabel: false,
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 3,
            name: 'forecastFormula',
            margin: '7 30 3 30',
            columnWidth: .5
        });
        basicfieldSet.add(me.forecastFormula);
        //图表类型
        me.chartype = Ext.widget('displayfield', {
            xtype: 'displayfield',
            labelWidth: 100,
            name: 'chartTypeStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.category.charttype'), //图表类型
            columnWidth: .5,
            labelAlign: 'left',
            margin: '7 30 3 30'
        });
        basicfieldSet.add(me.chartype);
        //数据类型
        me.datatype = Ext.widget('displayfield', {
            xtype: 'displayfield',
            labelWidth: 100,
            name: 'dataTypeStr',
            fieldLabel: '数据类型', //数据类型
            columnWidth: .5,
            labelAlign: 'left',
            margin: '7 30 3 30'
        });
        basicfieldSet.add(me.datatype);
        //是否生成度量指标
        me.iscreateKpi = Ext.widget('displayfield', {
            xtype: 'displayfield',
            name: 'createKpiStr',
            columns: 4,
            fieldLabel: '生成度量指标', //是否生成度量指标
            labelAlign: 'left',
            allowBlank: false,
            columnWidth: .5

        });
        basicfieldSet.add(me.iscreateKpi);
        //是否计算
        me.iscalcKpi = Ext.widget('displayfield', {
            xtype: 'displayfield',
            name: 'calcStr',
            columns: 4,
            fieldLabel: '是否计算',
            labelAlign: 'left',
            allowBlank: true,
            columnWidth: .5,
            margin: '7 30 3 30'
        });
        basicfieldSet.add(me.iscalcKpi);

        me.isfocus = Ext.widget('displayfield', {
            xtype: 'displayfield',
            margin: '7 30 3 30',
            name: 'isfocustr',
            columns: 4,
            fieldLabel: '是否关注', //是否关注
            labelAlign: 'left',
            columnWidth: .5
        });
        var alarmField = Ext.widget('displayfield', {
            xtype: 'displayfield',
            fieldLabel: '告警方案',
            name: 'alarmPlanName',
            labelAlign: 'left',
            columnWidth: .5
        });
        basicfieldSet.add(me.isfocus);
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
            url: __ctxPath + '/kpi/category/loadCategoryDetailById.f',
            params: {
                id: me.paramObj.scid
            },
            callback: function (data) {

                me.form.setValues({
                    parentStr: data.parentStr,
                    code: data.code,
                    name: data.name,
                    desc: data.desc,
                    ownDept: data.ownDept,
                    statusStr: data.statusStr,
                    assessmentFormula: data.assessmentFormula,
                    forecastFormula: data.forecastFormula,
                    chartTypeStr: data.chartTypeStr,
                    dataTypeStr: data.dataTypeStr,
                    createKpiStr: data.createKpiStr,
                    calcStr: data.calcStr,
                   // warningPlanName:data.warningPlanName,
                    alarmPlanName: data.alarmPlanName
                });
             	
                	var url = __ctxPath + "/kpi/alarm/findalarmplanregions.f";
                //debugger;
                	if(!me.panel) {               		
                       me.panel = Ext.create('FHD.view.kpi.cmp.sc.scWarningForm',{
                         paramId: data.alarmId,
                         url : url,
                         extraParams: {
                         	id: data.alarmId
                         },
                         flex:2
                    });
                    me.alarmSet.add(me.panel);
                	}
                	else {
                		me.panel.store.proxy.url = url;
                		me.panel.store.proxy.extraParams.id = data.alarmId;
                		me.panel.store.load();
                	}
                
                
                if ('0yn_y' == data.isfocustr) {
                    me.isfocus.setValue('是');
                } else {
                    me.isfocus.setValue('');
                }
            }
        });
    }

});