Ext.define('FHD.view.kpi.cmp.kpi.GatherForm', {
    extend: 'Ext.form.Panel',
	requires: ['FHD.ux.collection.CollectionSelector','FHD.ux.collection.CollectionWindow',
			   'FHD.ux.dict.DictRadio','FHD.ux.dict.DictSelectForEditGrid'],
    border: false,
    /**
     * 清除form数据
     */
    clearFormData: function () {
        var me = this;
        me.getForm().reset();

        me.c_resultgatherfrequence.reset();
        me.c_targetSetFrequence.reset();
        me.c_reportFrequence.reset();
        me.c_targetSetReportFrequence.reset();
    },
    /**
     * 初始化form默认值
     */
    initFormData: function () {
        var me = this;
        //添加时,需要添加评估值的默认公式
        me.assessmentFormula.setTriggerValue("IF(#[myself~实际值]>=#[myself~目标值],100,#[myself~实际值]/#[myself~目标值]*100)");
        me.assessmentFormula.setRadioValue("0sys_use_formular_formula");
        me.targetFormula.setRadioValue("0sys_use_formular_manual");
        me.resultFormula.setRadioValue("0sys_use_formular_manual");
        //设置采集频率默认值为按月采集       
        me.c_resultgatherfrequence.valueDictType = "0frequecy_month";
        me.c_resultgatherfrequence.valueRadioType = "2,";
        me.c_resultgatherfrequence.setValue("每月,期间末日");
        me.c_resultgatherfrequence.valueCron = "0 0 0 L * ?";
        me.c_targetSetFrequence.valueDictType = "0frequecy_month";
        me.c_targetSetFrequence.valueRadioType = "1,";
        me.c_targetSetFrequence.setValue("每月,期间首日");
        me.c_targetSetFrequence.valueCron = "0 0 0 1 * ?";
        me.iscalcKpi.setValue('0yn_y');
        me.getForm().setValues({
            resultSumMeasureStr: 'kpi_sum_measure_sum',
            targetSumMeasureStr: 'kpi_sum_measure_sum',
            assessmentSumMeasureStr: 'kpi_sum_measure_avg',
            relativeToStr: 'kpi_relative_to_previs'
        });

    },
    /**
     * 点击下一步提交事件
     * @param {panel} cardPanel cardpanel面板
     * @param {boolean} finishflag 是否完成标示,true代表点击了'完成按钮'
     */
    last: function (cardPanel, finishflag) {
        var me = this;
        var jsobj = {};
        var form = me.getForm();
        if (!form.isValid()) {
            return false;
        }
        var resultgatherfrequence = me.c_resultgatherfrequence;
        var targetSetFrequence = me.c_targetSetFrequence;
        var reportFrequence = me.c_reportFrequence;
        var targetSetReportFrequence = me.c_targetSetReportFrequence;
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
        //mainSoft接口 新增
        var resultDsName = me.resultFormula.getDataSource();
        var resultCollectMethod = me.resultFormula.getCollectMethod();
        var targetformulaDict = me.targetFormula.getRadioValue();
        var targetformula = me.targetFormula.getTriggerValue();
        //mainSoft接口 新增
        var targetDsName = me.targetFormula.getDataSource();
        var targetCollectMethod = me.targetFormula.getCollectMethod();
        var assessmentformulaDict = me.assessmentFormula.getRadioValue();
        var assessmentformula = me.assessmentFormula.getTriggerValue();
        //mainSoft接口 新增
        var assessmentDsName = me.assessmentFormula.getDataSource();
        var assessmentCollectMethod = me.assessmentFormula.getCollectMethod();
        var resultSumMeasureStr = me.resultSumMeasure.getValue();
        var targetSumMeasureStr = me.targetSumMeasure.getValue();
        var assessmentSumMeasureStr = me.assessmentSumMeasure.getValue();
        var scale = me.c_scale.getValue();
        var resultCollectInterval = me.c_resultCollectInterval.getValue();
        var targetSetInterval = me.c_targetSetInterval.getValue();
        var relativeTo = me.c_relativeTo.getValue();
        var modelValue = me.modelValue.getValue();
        var maxValue = me.maxValue.getValue();
        var minValue = me.minValue.getValue();
        var calcValue = me.iscalcKpi.getValue().calcStr;
        var gatherDesc = me.kpiGatherDesc.getValue();
        jsobj.id = me.paramObj.kpiId;
        jsobj.gatherfrequence = valueDictType;
        jsobj.gatherfrequenceRule = valueRadioType;
        jsobj.targetSetFrequenceDictType = targetSetFrequenceDictType;
        jsobj.targetSetFrequenceRadioType = targetSetFrequenceRadioType;
        jsobj.reportFrequenceDictType = reportFrequenceDictType;
        jsobj.reportFrequenceRadioType = reportFrequenceRadioType;
        jsobj.targetSetReportFrequenceDictType = targetSetReportFrequenceDictType;
        jsobj.targetSetReportFrequenceRadioType = targetSetReportFrequenceRadioType;
        jsobj.gatherValueCron = gatherValueCron;
        jsobj.targetValueCron = targetValueCron;
        jsobj.gatherreportCron = gatherreportCron;
        jsobj.targetReportCron = targetReportCron;
        jsobj.resultformulaDict = resultformulaDict;
        jsobj.resultformula = resultformula;
        jsobj.targetformulaDict = targetformulaDict;
        jsobj.targetformula = targetformula;
        jsobj.assessmentformulaDict = assessmentformulaDict;
        jsobj.assessmentformula = assessmentformula;
        jsobj.resultSumMeasureStr = resultSumMeasureStr;
        jsobj.targetSumMeasureStr = targetSumMeasureStr;
        jsobj.assessmentSumMeasureStr = assessmentSumMeasureStr;
        jsobj.scale = scale;
        jsobj.resultCollectInterval = resultCollectInterval;
        jsobj.targetSetInterval = targetSetInterval;
        jsobj.relativeTo = relativeTo;
        jsobj.modelValue = modelValue;
        jsobj.maxValue = maxValue;
        jsobj.minValue = minValue;
        jsobj.calcValue = calcValue;
        jsobj.gatherDesc = gatherDesc;
        jsobj.targetSetFrequenceStr = targetSetFrequence.getValue();
        jsobj.reportFrequenceStr = reportFrequence.getValue();
        jsobj.resultgatherfrequence = resultgatherfrequence.getValue();
        jsobj.targetSetReportFrequenceStr = targetSetReportFrequence.getValue();
        // mainsoft接口
        jsobj.targetDsName = targetDsName;
        jsobj.targetCollectMethod = targetCollectMethod;
        jsobj.assessmentDsName = assessmentDsName;
        jsobj.assessmentCollectMethod = assessmentCollectMethod;
        jsobj.resultDsName = resultDsName;
        jsobj.resultCollectMethod = resultCollectMethod;
        // 本地方法
        var resultLocalmethodPath = me.resultFormula.getLocalMethodPath();
        var resultParameter = me.resultFormula.getParameterJson();
        jsobj.resultLocalmethodPath = resultLocalmethodPath;
        var targetLocalmethodPath = me.targetFormula.getLocalMethodPath();
        var targetParameter = me.targetFormula.getParameterJson();
        jsobj.targetLocalmethodPath = targetLocalmethodPath;
        var assessLocalmethodPath = me.assessmentFormula.getLocalMethodPath();
        var assessParameter = me.assessmentFormula.getParameterJson();
        jsobj.assessLocalmethodPath = assessLocalmethodPath;
        if (form.isValid()) {
            FHD.ajax({
                url: __ctxPath + '/kpi/kpi/mergekpitypecal.f',
                params: {
                    param: Ext.JSON.encode(jsobj),
                    resultParameter: Ext.JSON.encode(resultParameter),
                    targetParameter: Ext.JSON.encode(targetParameter),
                    assessParameter: Ext.JSON.encode(assessParameter)
                },
                callback: function (data) {
                    if (data && data.success) {
                        var kpicardpanel = me.pcontainer;
                        FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                        if (!finishflag) { //如果点击的不是完成按钮,需要移动到下一个面板
                            cardPanel.lastSetBtnState(cardPanel, cardPanel.getActiveItem());
                            cardPanel.pageMove("next");
                            kpicardpanel.navBtnState(cardPanel); //设置导航按钮状态,如果是首个面板则上一步按钮为置灰状态,如果是最后一个面板则下一步按钮为置灰状态
                            /*同时将告警设置按钮为可用状态*/
                            me.pcontainer.down("[name='kpi_kpi_alarmset_btn_top']").setDisabled(false);
                        } else {
                            kpicardpanel.undo();
                        }

                    }
                }
            });
        }

    },

    addComponent: function () {
        var me = this;
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
            showType: 'kpiType',
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
            showType: 'kpiType',
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
            showType: 'kpiType',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.assessmentFormula'), //评估值公式
            column: 'assessmentValueFormula',
            targetId: '',
            targetName: '',
            formulaTypeName: 'isAssessmentFormula',
            formulaName: 'assessmentFormula',
            columnWidth: .5
        });
        calcfieldset.add(me.assessmentFormula);

        me.hi = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'null_a',
            value: '',
            maxLength: 100,
            columnWidth: .5,
            hideMode: "visibility",
            hidden: true
        });
        calcfieldset.add(me.hi);

        //标杆值
        me.modelValue = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 100,
            name: 'modelValue',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.modelValue'), //标杆值
            value: '',
            maxLength: 255,
            minValue: 0,
            maxValue:100000,
            columnWidth: .5
        });
        calcfieldset.add(me.modelValue);

        me.hb = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'null_b',
            value: '',
            maxLength: 100,
            columnWidth: .5,
            hideMode: "visibility",
            hidden: true
        });

        calcfieldset.add(me.hb);

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
        
        
        //最大值
        me.maxValue = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 5,
            name: 'maxValue',
            fieldLabel: '最大值', //最大值
            value: '',
            maxLength: 255,
            minValue: 0,
            maxValue:10000000,
            columnWidth: .5,
            labelWidth: 105
        });
        calcfieldset.add(me.maxValue);
        //最小值
        me.minValue = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 5,
            name: 'minValue',
            minValue: 0,
            maxValue:10000000,
            fieldLabel: '最小值', //最小值
            value: '',
            maxLength: 255,
            labelWidth: 105,
            columnWidth: .5
        });
        calcfieldset.add(me.minValue);
        
        //采集说明
        me.kpiGatherDesc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 4,
            labelWidth: 105,
            name: 'gatherDesc',
            fieldLabel: "采集说明", 
            maxLength: 2000,
            columnWidth: .5
        });
        calcfieldset.add(me.kpiGatherDesc);

        

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
        me.c_resultgatherfrequence = Ext.widget('collectionSelector', {
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

        gatherfieldSet.add(me.c_resultgatherfrequence);

        //目标收集频率
        me.c_targetSetFrequence = Ext.widget('collectionSelector', {
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
        gatherfieldSet.add(me.c_targetSetFrequence);

        //结果收集延期天
        me.c_resultCollectInterval = Ext.widget('numberfield', {
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

        gatherfieldSet.add(me.c_resultCollectInterval);

        //目标收集延期天
        me.c_targetSetInterval = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 1,
            //id: 'kpi_kpi_targetSetInterval',
            name: 'targetSetIntervalStr',
            maxValue: 5,
            minValue: 1,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetSetInterval'), //目标收集延期天
            value: '3',
            maxLength: 100,
            labelWidth: 105,
            columnWidth: .5
        });
        gatherfieldSet.add(me.c_targetSetInterval);

        //结果收集报告频率
        me.c_reportFrequence = Ext.widget('collectionSelector', {
            columnWidth: .5,
            name: 'reportFrequenceStr',
            xtype: 'collectionSelector',
            label: FHD.locale.get('fhd.kpi.kpi.form.reportFrequence'), //结果收集报告频率
            valueDictType: '',
            single: false,
            value: '',
            labelWidth: 105
        });
        gatherfieldSet.add(me.c_reportFrequence);

        //目标收集报告频率
        me.c_targetSetReportFrequence = Ext.widget('collectionSelector', {
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

        gatherfieldSet.add(me.c_targetSetReportFrequence);

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
        me.c_scale = Ext.widget('numberfield', {
            xtype: 'numberfield',
            step: 1,
            maxValue: 6,
            minValue: 0,
            name: 'scale',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.scale'), //报表小数点位置
            value: '2',
            columnWidth: .5
        });
        reportfieldSet.add(me.c_scale);
        //趋势相对于
        me.c_relativeTo = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
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

        reportfieldSet.add(me.c_relativeTo);


        me.add(calcfieldset);
        me.add(gatherfieldSet);
        me.add(reportfieldSet);

    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            autoRender: false,
            autoScroll: true,
            border: me.border,
            layout: 'column',
            width: FHD.getCenterPanelWidth() - 258,
            height: FHD.getCenterPanelHeight() - 75,
            bodyPadding: "0 3 3 3"
        });

        me.callParent(arguments);

        //向form表单中添加控件
        me.addComponent();

    },

    /**
     * form加载数据方法
     */
    loadFormById: function (id) {
        var me = this;
        if (!me.paramObj.editflag) {
            me.iscalcKpi.setValue('0yn_y');
        }
        me.load({
            url: __ctxPath + '/kpi/Kpi/findkpitypecalculatetojson.f',
            params: {
                id: id
            },
            success: function (form, action) {
                var othervalue = action.result.othervalue;
                var resultgatherfrequence = me.c_resultgatherfrequence;
                var targetSetFrequence = me.c_targetSetFrequence;
                var reportFrequence = me.c_reportFrequence;
                var targetSetReportFrequence = me.c_targetSetReportFrequence;
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
                // mainSoft
                var resultformula = me.resultFormula;
                var targetformula = me.targetFormula;
                var assessmentformula = me.assessmentFormula;
                var data = action.result.data;
                resultformula.setDataSource(data.resultDs);
                resultformula.setCollectMethod(data.resultCollectMethod);
                resultformula.getLocalMethodPath(data.getLocalMethodPath);
                targetformula.setDataSource(data.tagertDs);
                targetformula.setCollectMethod(data.targetCollectMethod);
                assessmentformula.setDataSource(data.assessDs);
                assessmentformula.setCollectMethod(data.assessCollectMethod);
                resultformula.setLocalMehtodPath(data.resultLocalPath);
                resultformula.setParameterJson(data.resultParameterJson);
                targetformula.setLocalMehtodPath(data.targetLocalPath);
                targetformula.setParameterJson(data.targetParameterJson);
                assessmentformula.setLocalMehtodPath(data.assessLocalPath);
                assessmentformula.setParameterJson(data.assessParameterJson);
                return true;
            }
        });
    }

});