/**
 * 添加记分卡,基本信息面板 继承于Ext.form.Panel
 * 
 */
Ext.define('FHD.view.kpi.cmp.sc.ScBasicForm', {
    extend: 'Ext.form.Panel',
    border: false,
    requires: ['FHD.ux.dict.DictRadio','FHD.ux.dict.DictSelectForEditGrid'],
    waitMsgTarget:true,
    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },
    
    submitCallBack:function(editflag,scid,scname){
    	var me = this;
    },

    /**
	 * 点击下一步提交事件
	 */
    last: function () {
        var me = this;
        var form = me.getForm();
        var vobj = form.getValues();
        var paramObj = {};
        paramObj.name = vobj.name;
        paramObj.code = vobj.code;
        paramObj.scid = me.paramObj.scid;
        if (!form.isValid()) {
            return false;
        }
        var validateInfo = true;
       
        FHD.ajax({
        	async:false,
            url: __ctxPath + '/kpi/category/validate.f',
            params: {
                id: me.paramObj.scid,
                validateItem: Ext.JSON.encode(paramObj)
            },
            callback: function (data) {
                if (data && data.success) {
                    var forecastFormulaValue = me.forecastFormula.getValue();
                    var assessmentFormulaValue = me.assessmentFormula.getValue();
                    forecastFormulaValue = forecastFormulaValue == null ? "" : forecastFormulaValue;
                    assessmentFormulaValue = assessmentFormulaValue == null ? "" : assessmentFormulaValue;
                    vobj.id =  me.paramObj.scid;
                    vobj.forecastFormula = forecastFormulaValue;
                    vobj.assessmentFormula = assessmentFormulaValue;
                    vobj.chartTypeStr = vobj.chartTypeStr.join(',');
                    var addUrl = __ctxPath + '/kpi/category/mergecategory.f';
                    if (me.body != undefined) {
                        me.body.mask("创建中...", "x-mask-loading");
                    }
                    FHD.ajax({
		            	async:false,
		                url: addUrl,
		                params: {
		                    items: Ext.JSON.encode(vobj)
		                },
		                callback: function (data) {
		                   if (data) {
		                	    if(me.body != undefined) {
	                                me.body.unmask();
	                            }
		                	   	FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                                var editflag = me.paramObj.editflag;
                                me.paramObj.editflag = true;
                                me.paramObj.scid = data.id;
                                me.paramObj.scname = vobj.name;
                                me.paramObj.chartIds = me.chartype.getValue().join(',');
                                me.submitCallBack(editflag,me.paramObj.scid,me.paramObj.scname);
                            }
		                }
            		});
                } else {
                    // 校验失败信息
                    if (data && data.error == "codeRepeat") {
                    	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get("fhd.kpi.kpi.prompt.coderepeat"));
                     	validateInfo =  false;
                    }
                    return false;
                }
            }
        });
        return validateInfo;
    },

    /**
	 * 编码生成函数
	 */
    createCode: function () {
        var me = this;
        var vform = me.getForm();
        var vobj = vform.getValues();
        var paraobj = {};
        paraobj.id = me.paramObj.scid;
        if (vobj.parentid == "") {
            vobj.parentStr = "category_root";
        }
        if (vobj.parentid != "") {
            paraobj.parentid = vobj.parentid;
            FHD.ajax({
                url: __ctxPath + '/kpi/category/findcodebyparentid.f',
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
        }

    },


    /**
	 * form表单中添加控件
	 */
    addComponent: function () {
        var me = this;
        me.categoryId = Ext.create('Ext.form.field.Hidden',{
            xtype: 'hidden',
            hidden: true,
            name: 'id'
        });
        me.categoryParentId = Ext.create('Ext.form.field.Hidden',{
            xtype: 'hidden',
            hidden: true,
            name: 'parentid'
        });
        me.charttypehidden = Ext.create('Ext.form.field.Hidden',{
            xtype: 'hidden',
            hidden: true,
            name: 'charttypehidden'
        });

        var basicfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', // 基本信息fieldset
            collapsible: true,
            autoHeight: true,
            autoWidth: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 95
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.common.baseInfo'),
            items: [
                me.categoryId,
                me.categoryParentId,
                me.charttypehidden
            ]
        });
        // 上级维度
        me.parentCategory = Ext.widget('textfield', {
            xtype: 'textfield',
            readOnly: true,
            disabled: true,
            name: 'parentStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.parentCategory'), // 上级维度
            value: '',
            maxLength: 200,
            columnWidth: .5,
            allowBlank: false
        });

        basicfieldSet.add(me.parentCategory);

        // 编码
        var code = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.code')+ '<font color=red>*</font>', // 编码
            margin: '7 3 3 30',
            name: 'code',
            maxLength: 255,
            allowBlank: false,
            columnWidth: .4
        });

        basicfieldSet.add(code);

        // 自动生成编码按钮
        var codeBtn = Ext.widget('button', {
            xtype: 'button',
            margin: '7 30 3 3',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'), // 自动生成编码按钮
            columnWidth: .1,
            handler: function () {
                me.createCode();
            }
        });

        basicfieldSet.add(codeBtn);

        // 名称
        var name = Ext.widget('textareafield', {
            xtype: 'textareafield',
            name: 'name',
            rows: 3,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.name') + '<font color=red>*</font>', // 名称
            value: '',
            maxLength: 255,
            columnWidth: .5,
            allowBlank: false
        });
        basicfieldSet.add(name);
        // 说明
        var desc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 3,
            labelAlign: 'left',
            name: 'desc',
            fieldLabel: FHD.locale.get('fhd.sys.dic.desc'), // 说明
            maxLength: 4000,
            columnWidth: .5
        });
        basicfieldSet.add(desc);

        // 所属部门人员
        
        me.owndepts = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept"), // 所属部门人员
			labelAlign: 'left',
			labelWidth: 100,
			columnWidth: .5,
			height:85,
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'ownDept',
			value:''
		});
		
        basicfieldSet.add(me.owndepts);

        // 是否启用
        me.statu = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'statusStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '启用' + '<font color=red>*</font>', // 是否启用
            defaultValue: '0yn_y',
            labelAlign: 'left',
            allowBlank: false,
            columnWidth: .5
        });
        basicfieldSet.add(me.statu);
        // 评估值公式
        me.assessmentFormula = Ext.create('FHD.ux.kpi.FormulaTrigger', {
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.assessmentFormula'), // 评估值公式
            hideLabel: false,
            emptyText: '',
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 3,
            name: 'assessmentFormula',
            type: 'category',
            showType: 'categoryType',
            column: 'assessmentValueFormula',
            labelWidth: 100,
            columnWidth: .5
        });
        basicfieldSet.add(me.assessmentFormula);

        // 预警公式
        me.forecastFormula = Ext.create('FHD.ux.kpi.FormulaTrigger', {
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.warningFormula'),
            hideLabel: false,
            disabled: true,
            emptyText: '',
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 3,
            name: 'forecastFormula',
            type: 'kpi',
            showType: 'kpiType',
            column: 'assessmentValueFormula',
            labelWidth: 100,
            columnWidth: .5
        });
        basicfieldSet.add(me.forecastFormula);
        // 图表类型
        me.chartype = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
            editable: false,
            labelWidth: 100,
            multiSelect: true,
            name: 'chartTypeStr',
            dictTypeId: '0com_catalog_chart_type',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.category.charttype') + '<font color=red>*</font>', // 图表类型
            columnWidth: .5,
            labelAlign: 'left',
            allowBlank: false,
            maxHeight: 60
        });
        basicfieldSet.add(me.chartype);
        // 数据类型
        me.datatype = Ext.create('FHD.ux.dict.DictSelect', {
            editable: false,
            labelWidth: 100,
            multiSelect: false,
            name: 'dataTypeStr',
            dictTypeId: '0category_data_type',
            fieldLabel: '数据类型', // 数据类型
            columnWidth: .5,
            labelAlign: 'left',
            maxHeight: 60
        });
        basicfieldSet.add(me.datatype);
        // 是否生成度量指标
        me.iscreateKpi = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'createKpiStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '生成度量指标', // 是否生成度量指标
            defaultValue: '0yn_y',
            labelAlign: 'left',
            allowBlank: true,
            columnWidth: .5
        });
        basicfieldSet.add(me.iscreateKpi);
       

        var isfocus = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'isfocustr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '关注', // 是否关注
            defaultValue: '0yn_n',
            labelAlign: 'left',
            allowBlank: true,
            columnWidth: .5
        });

        basicfieldSet.add(isfocus);
        
        // 是否计算
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
        basicfieldSet.add(me.iscalcKpi);
        

        me.add(basicfieldSet);

    },

    /**
	 * 清除form数据
	 */
    clearFormData: function () {
        var me = this;
        me.getForm().reset();
        if (me.owndepts) {
            me.owndepts.clearValues();
        }
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

        // 向form表单中添加控件
        me.addComponent();

    },

    reloadData: function () {
        var me = this;
        me.categoryParentId.setValue(me.paramObj.parentid);
        me.parentCategory.setValue(me.paramObj.parentname);
        if (me.paramObj.editflag) {
            me.load({
                waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
                url: __ctxPath + '/kpi/category/findcategoryByIdToJson.f',
                params: {
                    id: me.paramObj.scid
                },
                success: function (form, action) {
                	if(action.result.data.ownDept){
                		var owndeptsValue = Ext.JSON.decode(action.result.data.ownDept);
			    		me.owndepts.setValues(owndeptsValue);	
                	}
                    var vobj = form.getValues();
                    if (vobj.charttypehidden != "") {
                        var charttypearr = vobj.charttypehidden.split(',');
                        // 给图表类型赋值
                        me.chartype.setValue(charttypearr);
                    }
                    return true;
                }
            });
        }
        else  {
            // 赋默认值
        	me.categoryParentId.setValue(me.paramObj.parentid);
        	me.parentCategory.setValue(me.paramObj.parentname);
            me.iscreateKpi.setValue('0yn_y');
            me.iscalcKpi.setValue('0yn_y');
            me.statu.setValue('0yn_y');
            // 赋值数据类型字段
            FHD.ajax({
                url: __ctxPath + '/kpi/category/findparentcategorydatetypebyid.f',
                async: false,
                params: {
                    parentid: me.paramObj.parentid
                },
                callback: function (data) {
                    if (data && data.success) {
                        var vform = me.getForm();
                        vform.setValues({
                            dataTypeStr: data.dataTypeStr
                        });
                    }
                }
            });
        }
        if(me.paramObj.empType) {
        		me.initCommonSelector();
        }

    },
    // 设置所属人员部门控件默认值为本部门或当前登录人
    initCommonSelector: function(){
    	var me = this;
    	var dataArray = [];
    	if(me.paramObj.empType == 'dept') {   		
			dataArray.push({id:__user.majorDeptId,deptno:__user.majorDeptNo,deptname:__user.majorDeptName});
			me.owndepts.type = 'dept';
    	} else if(me.paramObj.empType == 'dept_emp') {
    		dataArray.push({empid:__user.empId,empno:__user.empNo,empname:__user.empName,deptid: __user.majorDeptId,deptno:__user.majorDeptNo,deptname:__user.majorDeptName});
    	}
    	me.owndepts.setValues(dataArray);
    	/*me.owndepts.type = 'dept_emp';
    	if(me.paramObj.empType == 'dept') {
    		dataArray = [{empid:'',deptid: __user.majorDeptId}];
    	} else if(me.paramObj.empType == 'dept_emp') {
    		dataArray = [{empid:__user.empId,deptid: __user.majorDeptId}];
    	}
    	me.owndepts.setHiddenValue(dataArray);*/
    }

});