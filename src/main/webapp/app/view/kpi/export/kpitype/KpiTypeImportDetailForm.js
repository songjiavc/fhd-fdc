Ext.define('FHD.view.kpi.export.kpitype.KpiTypeImportDetailForm', {
    extend: 'Ext.form.Panel',
    kpiTmpId: '',
    requires: ['FHD.ux.dict.DictRadio','FHD.ux.dict.DictSelectForEditGrid','Ext.ux.Toast','FHD.ux.collection.CollectionSelector','FHD.ux.collection.CollectionWindow'],
    initComponent: function () {
        var me = this;
        var tbar = [// 菜单项
		'->', 
							{
					text : "返回",
					iconCls : 'icon-control-repeat-blue',
					handler : function() {
					      me.goback();
					},
					scope : this
				},{
					text : "保存",
					iconCls : 'icon-save',
					handler : function() {
					      me.last();
					},
					scope : this
				}
];
        Ext.applyIf(me, {
            autoScroll: true,
            border: me.border,
            autoHeight: true,
            layout: 'column',
            //width: FHD.getCenterPanelWidth() - 258,
            bodyPadding: "0 3 3 3",
            tbar:tbar
        });

        me.callParent(arguments);

        //向form表单中添加控件
        me.addComponent();
    },
    addComponent: function () {
        var me = this;

        me.kpitypeid = Ext.create('Ext.form.field.Hidden', {
            xtype: 'hidden',
            hidden: true,
            name: 'id'
        });
        me.validateInfo = Ext.create('Ext.form.field.Hidden', {
            xtype: 'hidden',
            hidden: true,
            name: 'validateInfo'
        });


        var fieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //基本信息fieldset
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 105
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.common.baseInfo'),
            items: [
                me.kpitypeid
            ]
        });

        me.add(fieldSet);
        // 公司名称
        me.companyName = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'companyName',
            fieldLabel: '公司' + '<font color=red>*</font>', //短名称
            //value: '',
            maxLength: 255,
            columnWidth: 1,
            allowBlank: false
        });
        fieldSet.add(me.companyName);
        //名称
        me.kpiname = Ext.widget('textareafield', {
            xtype: 'textareafield',
            labelAlign: 'left',
            rows: 3,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.name') + '<font color=red>*</font>', //名称
            name: 'name',
            maxLength: 255,
            columnWidth: .5,
            allowBlank: false
        });
         fieldSet.add(me.kpiname);
        //说明
        var desc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 3,
            labelAlign: 'left',
            name: 'desc',
            fieldLabel: FHD.locale.get('fhd.sys.dic.desc'), //说明
            maxLength: 2000,
            columnWidth: .5
        });
        fieldSet.add(desc);
        //短名称
        var shortName = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'shortName',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.shortName'), //短名称
            value: '',
            maxLength: 255,
            columnWidth: .5
        });

        fieldSet.add(shortName);

        //序号
        var sort = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 1,
            name: 'sort',
            minValue: 0,
            fieldLabel: FHD.locale.get('fhd.sys.icon.order'), //序号
            value: '',
            maxLength: 255,
            columnWidth: .5
        });
        fieldSet.add(sort);

        //编码
        me.code = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.code'), //编码
            margin: '7 3 3 30',
            name: 'code',
            maxLength: 255,
            columnWidth: .5
        });
        fieldSet.add(me.code);

//        //自动生成按钮
//        var codeBtn = Ext.widget('button', {
//            xtype: 'button',
//            margin: '7 30 3 3',
//            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'), //自动生成按钮
//            columnWidth: .1,
//            handler: function () {
//                me.createKpiTypeCode();
//            }
//        });
//        fieldSet.add(codeBtn);

        //所属部门
        me.ownDept = Ext.create('FHD.ux.org.CommonSelector', {
            fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept") + '<font color=red>*</font>', //所属部门
            labelAlign: 'left',
            columnWidth: .5,
            name: 'ownDept',
            multiSelect: false,
            type: 'dept_emp',
            labelWidth: 95,
            allowBlank: false,
            render:function() {
            	
            }
        });
        fieldSet.add(me.ownDept);
        //采集部门
        me.gatherDept = Ext.create('FHD.ux.org.CommonSelector', {
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.gatherdept'), //采集部门
            labelAlign: 'left',
            columnWidth: .5,
            name: 'gatherDept',
            multiSelect: false,
            type: 'dept_emp',
            labelWidth: 95
        });
        fieldSet.add(me.gatherDept);
        //目标部门
        me.targetDept = Ext.create('FHD.ux.org.CommonSelector', {
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetdept'), //目标部门
            labelAlign: 'left',
            columnWidth: .5,
            name: 'targetDept',
            multiSelect: false,
            type: 'dept_emp',
            labelWidth: 95
        });
        fieldSet.add(me.targetDept);

        //报告部门
        me.reportDept = Ext.create('FHD.ux.org.CommonSelector', {
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.reportdept'), //报告部门
            growMax: 60,
            labelAlign: 'left',
            columnWidth: .5,
            name: 'reportDept',
            multiSelect: true,
            type: 'dept_emp',
            maxHeight: 60,
            labelWidth: 95
        });
        fieldSet.add(me.reportDept);
        //查看部门
        me.viewDept = Ext.create('FHD.ux.org.CommonSelector', {
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.viewdept'), //查看部门
            growMax: 60,
            labelAlign: 'left',
            columnWidth: .5,
            name: 'viewDept',
            multiSelect: true,
            type: 'dept_emp',
            maxHeight: 60,
            labelWidth: 95
        });
        fieldSet.add(me.viewDept);

        //相关信息fieldset
        var relaFieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //相关信息fieldset
            autoHeight: true,
            autoWidth: true,
            collapsible: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 105
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.kpi.kpi.form.assinfo') //相关信息
        });

        me.add(relaFieldSet);

        //是否启用
        var statusStr = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'statusStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '启用' + '<font color=red>*</font>', //是否启用
            defaultValue: '0yn_y',
            labelAlign: 'left',
            allowBlank: false,
            columnWidth: .5
        });
        relaFieldSet.add(statusStr);
        //是否监控
        var monitorStr = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'monitorStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '监控', //是否监控
            defaultValue: '0yn_y',
            labelAlign: 'left',
            columnWidth: .5
        });
        relaFieldSet.add(monitorStr);
        //开始日期
        me.startDateStr = Ext.widget('datefield', {
            xtype: 'datefield',
            format: 'Y-m-d',
            name: 'startDateStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.startdate'), //开始日期
            columnWidth: .5,
            allowBlank: true
        });
        relaFieldSet.add(me.startDateStr);
        //单位
        var unitsStr = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            multiSelect: false,
            name: 'unitsStr',
            dictTypeId: '0units',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.unit') + '<font color=red>*</font>', //单位
            columnWidth: .5,
            labelAlign: 'left',
            labelWidth: 105,
            allowBlank: false
        });
        relaFieldSet.add(unitsStr);
        //指标类型
        var typeStr = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 100,
            multiSelect: false,
            name: 'typeStr',
            dictTypeId: 'kpi_etype',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.etype'), //指标类型
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_etype_positive'
        });
        relaFieldSet.add(typeStr);
        //指标性质
        var kpiTypeStr = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            multiSelect: false,
            name: 'kpiTypeStr',
            dictTypeId: 'kpi_kpi_type',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.type'), //指标性质
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_kpi_type_assessment'
        });
        relaFieldSet.add(kpiTypeStr);
        //亮灯依据
        var alarmMeasureStr = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 100,
            multiSelect: false,
            name: 'alarmMeasureStr',
            dictTypeId: 'kpi_alarm_measure',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.alarmMeasure'), //亮灯依据
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_alarm_measure_score'
        });
        relaFieldSet.add(alarmMeasureStr);
        //预警依据
        var alarmBasisStr = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            multiSelect: false,
            name: 'alarmBasisStr',
            dictTypeId: 'kpi_alarm_basis',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.alarmBasis'), //预警依据
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_alarm_basis_forecast'
        });
        relaFieldSet.add(alarmBasisStr);
        //主纬度
        var mainDim = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 100,
            multiSelect: false,
            name: 'mainDim',
            dictTypeId: 'kpi_dimension',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.mainDim'), //主纬度
            columnWidth: .5,
            labelAlign: 'left'
        });
        relaFieldSet.add(mainDim);
        //辅助纬度
        me.otherDim = Ext.create('FHD.ux.dict.DictSelect', {
            maxHeight: 70,
            labelWidth: 105,
            name: 'otherDim',
            dictTypeId: 'kpi_dimension',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.otherDim'), //辅助纬度
            columnWidth: .5,
            labelAlign: 'left',
            multiSelect: true
        });
        relaFieldSet.add(me.otherDim);
        //目标值别名
        var targetValueAlias = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'targetValueAlias',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetValueAlias'), //目标值别名
            value: '',
            maxLength: 255,
            columnWidth: .5,
            value: FHD.locale.get('fhd.kpi.kpi.form.targetValueAlias')
        });
        relaFieldSet.add(targetValueAlias);
        //实际值别名
        var resultValueAlias = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'resultValueAlias',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.resultValueAlias'), //实际值别名
            value: '',
            maxLength: 255,
            columnWidth: .5,
            labelWidth: 105,
            value: FHD.locale.get('fhd.kpi.kpi.form.resultValueAlias')
        });
        relaFieldSet.add(resultValueAlias);
        var calcfieldset = Ext.widget('fieldset', {
            xtype: 'fieldset', //计算信息fieldset
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '15 30 15 30',
                labelWidth: 105
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.kpi.kpi.form.cainfo')

        });
        //结果值公式
        me.resultFormula = Ext.create('FHD.view.kpi.datasource.kpiFormulaDsSelector', {
            type: 'kpi',
            column: 'resultValueFormula',
            showType: 'all',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.resultFormula'), //结果值公式
            targetId: '',
            targetName: '',
            columnWidth: 0.5,
            formulaTypeName: 'isResultFormula',
            formulaName: 'resultFormula'

        });
        calcfieldset.add(me.resultFormula);
        //目标值公式
        me.targetFormula = Ext.create('FHD.view.kpi.datasource.kpiFormulaDsSelector', {
            type: 'kpi',
            showType: 'all',
            column: 'targetValueFormula',
            targetId: '',
            targetName: '',
            formulaTypeName: 'isTargetFormula',
            formulaName: 'targetFormula',
            columnWidth: .5,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetFormula') //目标值公式
        });

        calcfieldset.add(me.targetFormula);

        //评估值公式
        me.assessmentFormula = Ext.create('FHD.view.kpi.datasource.kpiFormulaDsSelector', {
            type: 'kpi',
            showType: 'all',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.assessmentFormula'), //评估值公式
            column: 'assessmentValueFormula',
            targetId: '',
            targetName: '',
            formulaTypeName: 'isAssessmentFormula',
            formulaName: 'assessmentFormula',
            columnWidth: .5
        });
        calcfieldset.add(me.assessmentFormula);

        var hi = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'null_a',
            value: '',
            maxLength: 100,
            columnWidth: .5,
            hideMode: "visibility",
            hidden: true
        });
        calcfieldset.add(hi);

        //标杆值
        me.modelValue = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 100,
            name: 'modelValue',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.modelValue'), //标杆值
            value: '',
            minValue: 0,
            maxValue: 100000,
            maxLength: 255,
            columnWidth: .5
        });
        calcfieldset.add(me.modelValue);

        var hb = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'null_b',
            value: '',
            maxLength: 100,
            columnWidth: .5,
            hideMode: "visibility",
            hidden: true
        });

        calcfieldset.add(hb);

        //结果值累计计算
        me.resultSumMeasure = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            labelAlign: 'left',
            multiSelect: false,
            name: 'resultSumMeasureStr',
            dictTypeId: 'kpi_sum_measure',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.resultSumMeasure') + '<font color=red>*</font>', //结果值累计计算
            defaultValue: 'kpi_sum_measure_sum',
            allowBlank: false,
            columnWidth: .5
        });
        calcfieldset.add(me.resultSumMeasure);

        //目标值累计计算
        me.targetSumMeasure = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            labelAlign: 'left',
            multiSelect: false,
            name: 'targetSumMeasureStr',
            dictTypeId: 'kpi_sum_measure',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetSumMeasure') + '<font color=red>*</font>', //目标值累计计算
            defaultValue: 'kpi_sum_measure_sum',
            allowBlank: false,
            columnWidth: .5
        });
        calcfieldset.add(me.targetSumMeasure);

        //评估值累计计算
        me.assessmentSumMeasure = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            labelAlign: 'left',
            multiSelect: false,
            name: 'assessmentSumMeasureStr',
            dictTypeId: 'kpi_sum_measure',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.assessmentMeasure') + '<font color=red>*</font>', //评估值累计计算
            defaultValue: 'kpi_sum_measure_last',
            allowBlank: false,
            columnWidth: .5
        });
        calcfieldset.add(me.assessmentSumMeasure);
        //最大值
        me.maxValue = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 5,
            name: 'maxValue',
            minValue: 0,
            fieldLabel: '最大值' /*FHD.locale.get('fhd.kpi.form.maxvalue')*/ , //最大值
            value: '',
            maxLength: 255,
            columnWidth: .5,
            labelWidth: 105
        });
        calcfieldset.add(me.maxValue);
        //最小值
        me.minValue = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 5,
            name: 'minValue',
            fieldLabel: '最小值', //最小值
            value: '',
            maxLength: 255,
            minValue: 0,
            labelWidth: 105,
            columnWidth: .5
        });
        calcfieldset.add(me.minValue);

        //是否计算
        me.iscalcKpi = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'calcStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '计算',
            defaultValue: '0yn_y',
            labelAlign: 'left',
            allowBlank: true,
            columnWidth: .5
        });
        calcfieldset.add(me.iscalcKpi);

        //采集设置fieldset
        var gatherfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //采集设置fieldset
            autoHeight: true,
            autoWidth: true,
            collapsible: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 105
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.kpi.kpi.form.gatherset')

        });

        //结果收集频率
        me.cresultgatherfrequence = Ext.widget('collectionSelector', {
            columnWidth: .5,
            name: 'resultgatherfrequence',
            xtype: 'collectionSelector',
            label: FHD.locale.get('fhd.kpi.kpi.form.gatherfrequence') + '<font color=red>*</font>', //结果收集频率
            valueDictType: '',
            valueRadioType: '',
            single: false,
            value: '',
            labelWidth: 105,
            allowBlank: false,
            columnWidth: .5
        });

        gatherfieldSet.add(me.cresultgatherfrequence);

        //目标收集频率
        me.ctargetSetFrequence = Ext.widget('collectionSelector', {
            columnWidth: .5,
            name: 'targetSetFrequenceStr',
            xtype: 'collectionSelector',
            label: FHD.locale.get('fhd.kpi.kpi.form.targetSetFrequence') + '<font color=red>*</font>', //目标收集频率
            valueDictType: '',
            single: false,
            value: '',
            labelWidth: 105,
            columnWidth: .5,
            allowBlank: false
        });
        gatherfieldSet.add(me.ctargetSetFrequence);

        //结果收集延期天
        me.cresultCollectInterval = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 1,
            maxValue: 5,
            minValue: 1,
            name: 'resultCollectIntervalStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.resultCollectInterval') + '<font color=red>*</font>', //结果收集延期天
            maxLength: 100,
            columnWidth: .5,
            labelWidth: 105,
            allowBlank: false,
            value: '3'

        });

        gatherfieldSet.add(me.cresultCollectInterval);

        //目标收集延期天
        me.ctargetSetInterval = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 1,
            name: 'targetSetIntervalStr',
            maxValue: 5,
            minValue: 1,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetSetInterval'), //目标收集延期天
            value: '3',
            maxLength: 100,
            labelWidth: 105,
            columnWidth: .5
        });
        gatherfieldSet.add(me.ctargetSetInterval);

        //结果收集报告频率
        me.creportFrequence = Ext.widget('collectionSelector', {
            columnWidth: .5,
            name: 'reportFrequenceStr',
            xtype: 'collectionSelector',
            label: FHD.locale.get('fhd.kpi.kpi.form.reportFrequence'), //结果收集报告频率
            valueDictType: '',
            single: false,
            value: '',
            labelWidth: 105
        });
        gatherfieldSet.add(me.creportFrequence);

        //目标收集报告频率
        me.ctargetSetReportFrequence = Ext.widget('collectionSelector', {
            columnWidth: .5,
            name: 'targetSetReportFrequenceStr',
            xtype: 'collectionSelector',
            label: FHD.locale.get('fhd.kpi.kpi.form.targetSetReportFrequence'), //目标收集报告频率
            valueDictType: '',
            single: false,
            value: '',
            labelWidth: 105,
            columnWidth: .5
        });

        gatherfieldSet.add(me.ctargetSetReportFrequence);

        //报告设置fieldset
        var reportfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //报告设置fieldset
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 105
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.kpi.kpi.form.reportset')
        });

        //报表小数点位置
        me.cscale = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 1,
            maxValue: 6,
            minValue: 0,
            name: 'scale',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.scale'), //报表小数点位置
            value: '2',
            columnWidth: .5
        });
        reportfieldSet.add(me.cscale);
        //趋势相对于
        me.crelativeTo = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            multiSelect: false,
            name: 'relativeToStr',
            dictTypeId: 'kpi_relative_to',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.relativeto') + '<font color=red>*</font>', //趋势相对于
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_relative_to_previs',
            allowBlank: false
        });

        reportfieldSet.add(me.crelativeTo);
        //告警信息fieldset
        var warningfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //报告设置fieldset
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 105
            },
            layout: {
                type: 'column'
            },
            title: '告警信息'
        });

        // 预警方案
        Ext.define('alarmModels', {
            extend: 'Ext.data.Model',
            fields: [{
                name: 'id',
                type: 'string'
            }, {
                name: 'name',
                type: 'string'
            }]
        });
        me.warningStore = Ext.create('Ext.data.Store', {
            model: 'alarmModels',
            proxy: {
                type: 'ajax',
                url: __ctxPath + '/kpi/kpistrategymap/findwarningbytype.f?type=warningtype',
                reader: {
                    type: 'json',
                    root: 'warninglist'
                }
            },
            autoLoad: true
        });
        me.warningSet = Ext.create('Ext.form.field.ComboBox', {
            store: me.warningStore,
            fieldLabel: '预警方案',
            emptyText: FHD.locale.get('fhd.common.pleaseSelect'),
            editable: false,
            //queryMode : 'alarmModels',
            name: 'warningPlan',
            displayField: 'name',
            valueField: 'id',
            columnWidth: .5,
            triggerAction: 'all'
        });
        me.alarmStore = Ext.create('Ext.data.Store', {
            model: 'alarmModels',
            proxy: {
                type: 'ajax',
                url: __ctxPath + '/kpi/kpistrategymap/findwarningbytype.f?type=alarmtype',
                reader: {
                    type: 'json',
                    root: 'warninglist'
                }
            },
            autoLoad: true
        });
        me.alarmSet = Ext.create('Ext.form.field.ComboBox', {
            store: me.alarmStore,
            fieldLabel: '告警方案',
            emptyText: FHD.locale.get('fhd.common.pleaseSelect'),
            editable: false,
            //queryMode : 'alarmModels',
            name: 'alarmPlan',
            displayField: 'name',
            valueField: 'id',
            columnWidth: .5,
            triggerAction: 'all'
        });
        // 方案生效日期
        me.warnDataEff = Ext.widget('datefield', {
            xtype: 'datefield',
            format: 'Y-m-d',
            name: 'effDate',
            fieldLabel: '生效日期', //开始日期
            columnWidth: .5,
            allowBlank: true
        });
        warningfieldSet.add(me.warningSet);
        warningfieldSet.add(me.alarmSet);
        warningfieldSet.add(me.warnDataEff);
        me.add(calcfieldset);
        me.add(gatherfieldSet);
        me.add(reportfieldSet);
        me.add(warningfieldSet);
    },
    valueToFormulaName: function (kpitypeid, kpitypename) {
        var me = this;
        var resultformula = me.resultFormula;
        var targetformula = me.targetFormula;
        var assessmentformula = me.assessmentFormula;
        resultformula.setTargetId(kpitypeid);
        resultformula.setTargetName(kpitypename);
        targetformula.setTargetId(kpitypeid);
        targetformula.setTargetName(kpitypename);
        assessmentformula.setTargetId(kpitypeid);
        assessmentformula.setTargetName(kpitypename);
    },
    reloadData: function () {
        var me = this;

        me.form.setValues({
            id: me.kpiTmpId
        });
        me.form.load({
            // waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
            url: __ctxPath + '/kpiTmp/Kpi/findKpiTmpById.f',
            params: {
                id: me.kpiTmpId
            },
            success: function (form, action) {
                var validateInfo = action.result.othervalue.validateInfo;
            	me.initLabel(validateInfo);
                var otherDimArray = action.result.data.otherDimArray;
                if (otherDimArray) {
                    var arr = Ext.JSON.decode(otherDimArray);
                    //给辅助纬度赋值
                    me.otherDim.setValue(arr);
                }
                var othervalue = action.result.othervalue;
                var resultgatherfrequence = me.cresultgatherfrequence;
                var targetSetFrequence = me.ctargetSetFrequence;
                var reportFrequence = me.creportFrequence;
                var targetSetReportFrequence = me.ctargetSetReportFrequence;
                if (othervalue.gatherDayCron) {
                    resultgatherfrequence.valueCron = othervalue.gatherDayCron;
                }
                if (othervalue.targetDayCron) {
                    targetSetFrequence.valueCron = othervalue.targetDayCron;
                }
                if (othervalue.targetSetFrequenceDictType) {
                    targetSetFrequence.valueDictType = othervalue.targetSetFrequenceDictType;
                }
                if (othervalue.targetSetFrequenceRule) {
                    targetSetFrequence.valueRadioType = othervalue.targetSetFrequenceRule;
                }
                if (othervalue.targetSetFrequence) {
                    targetSetFrequence.setValue(othervalue.targetSetFrequence);
                }
                if (othervalue.reportFrequenceDictType) {
                    reportFrequence.valueDictType = othervalue.reportFrequenceDictType;
                }
                if (othervalue.reportFrequenceRule) {
                    reportFrequence.valueRadioType = othervalue.reportFrequenceRule;
                }
                if (othervalue.reportFrequence) {
                    reportFrequence.setValue(othervalue.reportFrequence);
                }
                if (othervalue.targetSetReportFrequenceDictType) {
                    targetSetReportFrequence.valueDictType = othervalue.targetSetReportFrequenceDictType;
                }
                if (othervalue.targetSetReportFrequenceRule) {
                    targetSetReportFrequence.valueRadioType = othervalue.targetSetReportFrequenceRule;
                }
                if (othervalue.targetSetReportFrequence) {
                    targetSetReportFrequence.setValue(othervalue.targetSetReportFrequence);
                }
                if (othervalue.resultgatherfrequenceDictType) {
                    resultgatherfrequence.valueDictType = othervalue.resultgatherfrequenceDictType;
                }
                if (othervalue.resultgatherfrequenceRule) {
                    resultgatherfrequence.valueRadioType = othervalue.resultgatherfrequenceRule;
                }
                me.valueToFormulaName();
                return true;
            }
        });
    },
    last: function() {
    	var me = this;
        var form = me.getForm();
                if(!me.ownDept.field.getValue()) {
        	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'请选择所属人员！');
        }
        if(!form.isValid()) {
        	return false;
        }
        var vobj = form.getValues();
        var resultgatherfrequence = me.cresultgatherfrequence;
        var targetSetFrequence = me.ctargetSetFrequence;
        var reportFrequence =me.creportFrequence;
        var targetSetReportFrequence =me.ctargetSetReportFrequence;
        var valueDictType = resultgatherfrequence.valueDictType;
        var valueRadioType = resultgatherfrequence.valueRadioType;
        var gatherValueCron = resultgatherfrequence.valueCron;
        var targetValueCron = targetSetFrequence.valueCron;
        var gatherreportCron = reportFrequence.valueCron;
        var targetReportCron = targetSetReportFrequence.valueCron;
        var targetSetFrequenceDictType = targetSetFrequence.valueDictType;
        var targetSetFrequenceRadioType = targetSetFrequence.valueRadioType;
        var reportFrequenceDictType = reportFrequence.valueDictType;
        var reportFrequenceRadioType = reportFrequence.valueRadioType;
        var targetSetReportFrequenceDictType = targetSetReportFrequence.valueDictType;
        var targetSetReportFrequenceRadioType = targetSetReportFrequence.valueRadioType;
        var resultformulaDict = me.resultFormula.getRadioValue();
        var resultformula = me.resultFormula.getTriggerValue();
        var targetformulaDict = me.targetFormula.getRadioValue();
        var targetformula = me.targetFormula.getTriggerValue();
        var assessmentformulaDict = me.assessmentFormula.getRadioValue();
        var assessmentformula = me.assessmentFormula.getTriggerValue();
        var resultSumMeasureStr = me.resultSumMeasure.getValue();
        var targetSumMeasureStr = me.targetSumMeasure.getValue();
        var assessmentSumMeasureStr = me.assessmentSumMeasure.getValue();
        var scale = me.cscale.getValue();
        var resultCollectInterval = me.cresultCollectInterval.getValue();
        var targetSetInterval = me.ctargetSetInterval.getValue();
        var relativeTo = me.crelativeTo.getValue();
        var modelValue = me.modelValue.getValue();
        var maxValue = me.maxValue.getValue();
        var minValue = me.minValue.getValue();
        var calcValue = me.iscalcKpi.getValue().calcStr;
        vobj.id = me.kpiTmpId;
        vobj.gatherfrequence = valueDictType;
        vobj.gatherfrequenceRule = valueRadioType;
        vobj.targetSetFrequenceDictType = targetSetFrequenceDictType;
        vobj.targetSetFrequenceRadioType = targetSetFrequenceRadioType;
        vobj.reportFrequenceDictType = reportFrequenceDictType;
        vobj.reportFrequenceRadioType = reportFrequenceRadioType;
        vobj.targetSetReportFrequenceDictType = targetSetReportFrequenceDictType;
        vobj.targetSetReportFrequenceRadioType = targetSetReportFrequenceRadioType;
        vobj.gatherValueCron = gatherValueCron;
        vobj.targetValueCron = targetValueCron;
        vobj.gatherreportCron = gatherreportCron;
        vobj.targetReportCron = targetReportCron;
        vobj.resultformulaDict = resultformulaDict;
        vobj.resultformula = resultformula;
        vobj.targetformulaDict = targetformulaDict;
        vobj.targetformula = targetformula;
        vobj.assessmentformulaDict = assessmentformulaDict;
        vobj.assessmentformula = assessmentformula;
        vobj.resultSumMeasureStr = resultSumMeasureStr;
        vobj.targetSumMeasureStr = targetSumMeasureStr;
        vobj.assessmentSumMeasureStr = assessmentSumMeasureStr;
        vobj.scale = scale;
        vobj.resultCollectInterval = resultCollectInterval;
        vobj.targetSetInterval = targetSetInterval;
        vobj.relativeTo = relativeTo;
        vobj.modelValue = modelValue;
        vobj.maxValue = maxValue;
        vobj.minValue = minValue;
        vobj.calcValue = calcValue;
        vobj.targetSetFrequenceStr = targetSetFrequence.getValue();
        vobj.reportFrequenceStr = reportFrequence.getValue();
        vobj.resultgatherfrequence = resultgatherfrequence.getValue();
        vobj.targetSetReportFrequenceStr = targetSetReportFrequence.getValue();
        vobj.warnstartDate = me.warnDataEff.getValue();
        vobj.warnPlanId = me.warningSet.getValue();
        vobj.alarmPlanId = me.alarmSet.getValue();
        vobj.company = me.companyName.getValue();
        FHD.ajax({
                url: __ctxPath + '/kpi/kpi/mergeKpiTmp.f',
                async: false,
                params: {
                    param: Ext.JSON.encode(vobj)
                },
                callback: function (data) {
                    if (data && data.success) {
                        Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.operateSuccess'));
                    } else {
                        return false;
                    }
                }
            });
        me.pcontainer.reloadData();    
        
    },
    goback: function() {
    	var me = this;
    	me.pcontainer.window.close();
    },
    initLabel:function(s) {
    	var me = this;
        if(s) {
       	if(s.indexOf('指标名称') != -1) {
       		me.kpiname.setFieldLabel('<font color=red>' +FHD.locale.get('fhd.kpi.kpi.form.name') + '*</font>');
       	}
       	 if(s.indexOf('所属部门') != -1) {
       		me.ownDept.label.setText('<font color=red>' + FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept")+ '*:</font>',false);
       	}
       	if(s.indexOf('采集部门') != -1) {
       		me.gatherDept.label.setText('<font color=red>' +FHD.locale.get('fhd.kpi.kpi.form.gatherdept')+ ':</font>',false);
       	}
       	if(s.indexOf('目标部门') != -1) {
       		me.targetDept.label.setText('<font color=red>' +FHD.locale.get('fhd.kpi.kpi.form.targetdept')+ ':</font>',false);
       	}
       	if(s.indexOf('报告部门') != -1) {
       		me.reportDept.label.setText('<font color=red>' +FHD.locale.get('fhd.kpi.kpi.form.reportdept')+ ':</font>',false);
       	}           
        if(s.indexOf('查看部门') != -1) {
        	me.viewDept.label.setText('<font color=red>' +FHD.locale.get('fhd.kpi.kpi.form.viewdept')+ ':</font>',false);
        }
        if(s.indexOf('编号') != -1 ) {
        	me.code.setFieldLabel('<font color=red>' +FHD.locale.get('fhd.strategymap.strategymapmgr.form.code') + '</font>'); 
        }
        if(s.indexOf('开始日期') != -1 ) {
        	me.startDateStr.setFieldLabel('<font color=red>' + FHD.locale.get('fhd.kpi.kpi.form.startdate') + '*</font>'); 
        }
        if(s.indexOf('告警方案') != -1 ) {
        	me.alarmSet.setFieldLabel('<font color=red>' + '告警方案' + '</font>'); 
        }
        if(s.indexOf('预警方案') != -1 ) {
        	me.warningSet.setFieldLabel('<font color=red>' + '预警方案' + '*</font>'); 
        }
       }
    }
    

})