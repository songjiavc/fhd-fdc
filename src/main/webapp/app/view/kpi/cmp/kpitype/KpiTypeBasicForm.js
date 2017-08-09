Ext.define('FHD.view.kpi.cmp.kpitype.KpiTypeBasicForm', {
    extend: 'Ext.form.Panel',
    waitMsgTarget:true,
    requires: ['FHD.ux.dict.DictRadio','FHD.ux.dict.DictSelectForEditGrid'],
    border: false,

    createKpiTypeCode: function () {
        var me = this;
        var paraobj = {
            id: me.paramObj.kpitypeid
        };
        var vform = me.getForm();
        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/findkpitypecode.f',
            params: {
                param: Ext.JSON.encode(paraobj)
            },
            callback: function (data) {
                if (data && data.success) {
                    vform.setValues({
                        code: data.code
                    });
                }
            }
        });
    },

    /**
     * 指标类型基本信息提交方法点击下一步提交事件
     */
    last: function () {
        var me = this;
        var form = me.getForm();
        var vobj = form.getValues();
        var paramObj = {};
        paramObj.name = vobj.name;
        paramObj.code = vobj.code;
        paramObj.type = "KC";
        var kpitypeid = me.paramObj.kpitypeid;
        
        if(!me.ownDept.getValue()) {
        	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'请选择所属人员！');
        	return false;
        }
        if (!form.isValid()) {
            return false;
        }
        var validateInfo = true;
        if (form.isValid()) {
            FHD.ajax({
                url: __ctxPath + '/kpi/kpi/validate.f',
                async:false,
                params: {
                    id: kpitypeid,
                    validateItem: Ext.JSON.encode(paramObj)
                },
                callback: function (data) {
                    if (data && data.success) {
                        //提交指标信息
                        var addUrl = __ctxPath + '/kpi/kpi/mergekpitype.f';
                        vobj.id = kpitypeid;
                        FHD.ajax({
		            	async:false,
		                url: addUrl,
		                params: {
		                    items: Ext.JSON.encode(vobj)
		                },
		                callback: function (data) {
		                   if (data) {
                                    me.paramObj.kpitypeid = data.id;
                                    var editflag = me.paramObj.editflag;
                                    me.paramObj.editflag = true;
                                    me.paramObj.kpitypename = vobj.name;
                                    me.submitCallBack(editflag, me.paramObj.kpitypeid, me.paramObj.kpitypename);
                                    FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                           }
		                }
            		});
                        
                    } else {
                        //校验失败信息
                        if (data && data.error == "nameRepeat") {
                        	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get("fhd.kpi.kpi.prompt.namerepeat"));
                        	validateInfo =  false;
                        }
                        if (data && data.error == "codeRepeat") {
                        	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get("fhd.kpi.kpi.prompt.coderepeat"));
                        	validateInfo =  false;
                        }
                    }
                }
            });
        }
        return validateInfo;
    },

    addComponent: function () {
        var me = this;

        me.kpitypeid = Ext.create('Ext.form.field.Hidden', {
            xtype: 'hidden',
            hidden: true,
            name: 'id'
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
        //名称
        var name = Ext.widget('textareafield', {
            xtype: 'textareafield',
            labelAlign: 'left',
            rows: 3,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.name') + '<font color=red>*</font>', //名称
            name: 'name',
            maxLength: 255,
            columnWidth: .5,
            allowBlank: false
        });
        fieldSet.add(name);
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
        var code = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.code'), //编码
            margin: '7 3 3 30',
            name: 'code',
            maxLength: 255,
            columnWidth: .4
        });
        fieldSet.add(code);

        //自动生成按钮
        var codeBtn = Ext.widget('button', {
            xtype: 'button',
            margin: '7 30 3 3',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'), //自动生成按钮
            columnWidth: .1,
            handler: function () {
                me.createKpiTypeCode();
            }
        });
        fieldSet.add(codeBtn);

        //所属部门
        me.ownDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept") + '<font color=red>*</font>', // 所属部门人员
			labelAlign: 'left',
			labelWidth: 100,
			columnWidth: .5,
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'ownDept',
			value:''
		});
        
        
        fieldSet.add(me.ownDept);
        //采集部门
        me.gatherDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel:  FHD.locale.get('fhd.kpi.kpi.form.gatherdept') , //采集部门
			labelAlign: 'left',
			labelWidth: 100,
			columnWidth: .5,
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'gatherDept',
			value:''
		});
		
        fieldSet.add(me.gatherDept);
        //目标部门
        me.targetDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel:  FHD.locale.get('fhd.kpi.kpi.form.targetdept') , //目标部门
			labelAlign: 'left',
			labelWidth: 100,
			columnWidth: .5,
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'targetDept',
			value:''
		});
		
        
        fieldSet.add(me.targetDept);

        //报告部门
        
        me.reportDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			type: 'dept_emp',
			fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.reportdept') , //报告部门
			labelAlign: 'left',
			labelWidth: 100,
			columnWidth: .5,
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'reportDept',
			value:''
		});
        
        
        fieldSet.add(me.reportDept);
        //查看部门
        me.viewDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			type: 'dept_emp',
			fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.viewdept'), //查看部门
			labelAlign: 'left',
			labelWidth: 100,
			columnWidth: .5,
			height:80,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'viewDept',
			value:''
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
        var startDateStr = Ext.widget('datefield', {
            xtype: 'datefield',
            format: 'Y-m-d',
            name: 'startDateStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.startdate') , //开始日期
            columnWidth: .5,
            allowBlank: true
        });
        relaFieldSet.add(startDateStr);
        //单位
        var unitsStr = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            multiSelect: false,
            name: 'unitsStr',
            dictTypeId: '0units',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.unit')+ '<font color=red>*</font>', //单位
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

    },

    // 初始化方法
    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            autoScroll: true,
            border: me.border,
            autoHeight: true,
            layout: 'column',
            width: FHD.getCenterPanelWidth() - 258,
            bodyPadding: "0 3 3 3"
        });

        me.callParent(arguments);

        //向form表单中添加控件
        me.addComponent();

    },

    clearFormData: function () {
        var me = this;
        me.getForm().reset();
        me.ownDept.clearValues();
        me.gatherDept.clearValues();
        me.targetDept.clearValues();
        me.reportDept.clearValues();
        me.viewDept.clearValues();
    },

    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
        me.initFormData();
    },

    submitCallBack: function (editflag, kpitypeid, kpitypename) {
        var me = this;
    },

    /**
     * 初始化默认值
     */
    initFormData: function () {
        var me = this;
        me.getForm().setValues({
            monitorStr: '0yn_y',
            statusStr: '0yn_y',
            isInheritStr: '0yn_y',
            typeStr: 'kpi_etype_positive',
            kpiTypeStr: 'kpi_kpi_type_assessment',
            alarmMeasureStr: 'kpi_alarm_measure_score',
            alarmBasisStr: 'kpi_alarm_basis_forecast'

        });
    },

    reloadData: function () {
        var me = this;
        var kpitypeid = me.paramObj.kpitypeid;
        me.form.setValues({
            id: kpitypeid
        });
        if (me.paramObj.editflag) {
            me.form.load({
                waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
                url: __ctxPath + '/kpi/Kpi/findKpiByIdToJson.f',
                params: {
                    id: kpitypeid
                },
                success: function (form, action) {
                	
                 if(action.result.data.ownDept){
                 	var ownDeptValue = Ext.JSON.decode(action.result.data.ownDept);
		    	 	me.ownDept.setValues(ownDeptValue);
                 }
                 
		    	 if(action.result.data.reportDept){
		    	 	var reportDeptValue = Ext.JSON.decode(action.result.data.reportDept);
		    	 	me.reportDept.setValues(reportDeptValue);
		    	 }
                 
		    	 if(action.result.data.viewDept){
		    	 	var viewDeptValue = Ext.JSON.decode(action.result.data.viewDept);
		    	 	me.viewDept.setValues(viewDeptValue);
		    	 }
                 
		    	 if(action.result.data.gatherDept){
		    	 	var gatherDeptValue = Ext.JSON.decode(action.result.data.gatherDept);
		    	 	me.gatherDept.setValues(gatherDeptValue);
		    	 }
                 
		    	 if(action.result.data.targetDept){
		    	 	var targetDeptValue = Ext.JSON.decode(action.result.data.targetDept);
		    	 	me.targetDept.setValues(targetDeptValue);	
		    	 }
                 
		    	 
		    	 
                    var otherDimArray = action.result.data.otherDimArray;
                    if (otherDimArray) {
                        var arr = Ext.JSON.decode(otherDimArray);
                        //给辅助纬度赋值
                        me.otherDim.setValue(arr);
                    }
                    return true;
                }
            });
        }
    }
});