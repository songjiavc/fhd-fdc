Ext.define('FHD.view.kpi.cmp.sm.SmBasicForm', {
    extend: 'Ext.form.Panel',
    border: false,
    waitMsgTarget:true,
    
    requires: ['FHD.ux.dict.DictRadio','FHD.ux.dict.DictSelectForEditGrid'],
    paramObj: {
        smid: '', //目标ID
        parentid: 'sm_root', //父目标ID
        parentname: '目标库', //父目标名称
        editflag: false, //是否是编辑状态
        smname: '' //当前目标名称
    },
    /**
     * 提交后要对外部对象操作的回调函数
     */
    submitCallBack:function(editflag,smid,smname){
    	var me = this;
    },

    last: function () {
        var me = this;
        var parentSmId  = me.smSelector.getValue();
        var form = me.getForm();
        var vobj = form.getValues();
        if (!form.isValid()) {
            return false;
        }
        var validateInfo = true;
        FHD.ajax({
        	async:false,
            url: __ctxPath + '/kpi/kpistrategymap/validate.f',
            params: {
                name: vobj.name,
                id: me.paramObj.smid,
                code: vobj.code
            },
            callback: function (data) {
                if (data && data.success) {
                    //取预警公式值
                    var warningFormulaValue = me.warningFormula.getValue();
                    //评估值公式
                    var assessmentFormulaValue = me.assessmentFormula.getValue();
                    assessmentFormulaValue = assessmentFormulaValue == null ? "" : assessmentFormulaValue;
					vobj.currentSmId = me.paramObj.smid;
					vobj.warningFormula = warningFormulaValue == undefined ? "" : warningFormulaValue;
					vobj.assessmentFormula = assessmentFormulaValue;
					vobj.parentSmId = parentSmId;
					vobj.chartTypeStr = vobj.chartTypeStr.join(',');
                    var addUrl = __ctxPath + '/kpi/kpistrategymap/mergestrategymap.f' ;
					FHD.ajax({
		            	async:false,
		                url: addUrl,
		                params: {
		                    items: Ext.JSON.encode(vobj)
		                },
		                callback: function (data) {
		                    if (data) {
		                    	FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                                me.paramObj.smname = vobj.name;
                                me.paramObj.smid = data.smId;
                                var smname = me.paramObj.smname;
                                var smid = me.paramObj.smid;
                                var editflag = me.paramObj.editflag;
                                me.paramObj.editflag = true;
                                me.submitCallBack(editflag,smid,smname);
                            }
		                }
            		});
            
                } else {
                    if (data && data.error == "nameRepeat") {
                        Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.strategymap.strategymapmgr.prompt.nameRepeat'));
                    	validateInfo =  false;
                    }
                    if (data && data.error == "codeRepeat") {
                        Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.strategymap.strategymapmgr.prompt.codeRepeat'));
                    	validateInfo =  false;
                    }
                    return false;
                }
            }
        });
        return validateInfo;
    },


    /**
     * 生成编码函数
     */
    createCode:function(){
    	var me = this;
    	var vform = me.getForm();
        var vobj = vform.getValues();
        var paraobj = {};
        paraobj.currentSmId = me.paramObj.smid;
        if (vobj.parentId != "") {
            paraobj.parentid = vobj.parentId;
            FHD.ajax({
                url: __ctxPath + '/kpi/kpistrategymap/findcodebyparentid.f',
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

        var currentSmId = {
            xtype: 'hidden',
            hidden: true,
            name: 'currentSmId'
        };

        var parentId = {
            xtype: 'hidden',
            hidden: true,
            name: 'parentSmId'
        };

        var charttypehidden = {
            xtype: 'hidden',
            hidden: true,
            name: 'charttypehidden'
        };

        me.smSelector =  Ext.create('FHD.view.kpi.cmp.selector.StrategyMapSelector', {
            name: 'parentId',
            multiSelect: false,
            columnWidth: .5,
            gridHeight: 25,
            btnHeight: 25,
            labelText: '上级目标', //上级目标
            labelAlign: 'left',
            labelWidth: 101
        });

        var basicfieldSet = Ext.widget('fieldset', {
            xtype: 'fieldset', //基本信息fieldset
            autoHeight: true,
            autoWidth: true,
            collapsible: true,
            defaults: {
                margin: '3 30 3 30',
                labelWidth: 100
            },
            layout: {
                type: 'column'
            },
            title: FHD.locale.get('fhd.common.baseInfo'),
            items: [currentSmId, charttypehidden]
        });

        //上级目标
//        var parentname = Ext.widget('textfield', {
//            xtype: 'textfield',
//            disabled: true,
//            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.parent') + '<font color=red>*</font>', //上级目标
//            name: 'parentname',
//            maxLength: 200,
//            columnWidth: .5,
//            allowBlank: false
//        });

//        basicfieldSet.add(parentname);
        basicfieldSet.add(me.smSelector);

        //编码
        var code = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.code') + '<font color=red>*</font>', //编码
            margin: '3 3 3 30',
            name: 'code',
            maxLength: 255,
            columnWidth: .4,
            allowBlank: false
        });
        basicfieldSet.add(code);

        //自动生成编码按钮
        var codebtn = Ext.widget('button', {
            xtype: 'button',
            margin: '3 30 3 3',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'), //自动生成编码按钮
            handler: function () {
                me.createCode();
            },
            columnWidth: .1
        });

        basicfieldSet.add(codebtn);

        //名称
        var name = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 2,
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.name') + '<font color=red>*</font>', //名称
            margin: '7 30 3 30',
            name: 'name',
            maxLength: 255,
            columnWidth: .5,
            allowBlank: false
        });
        basicfieldSet.add(name);

        //短名称
        var shortName = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 2,
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.shortName'), //短名称
            margin: '7 30 3 30',
            name: 'shortName',
            columnWidth: .5,
            allowBlank: true,
            maxLength: 255
        });
        basicfieldSet.add(shortName);

        //主维度
        var mainDim = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            multiSelect: false,
            margin: '7 30 3 30',
            name: 'mainDim',
            labelWidth: 100,
            dictTypeId: 'kpi_dimension',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.mainDim'), //主维度
            columnWidth: .5,
            labelAlign: 'left',
            columns: 5
        });

        basicfieldSet.add(mainDim);

        //战略主题
        var mainTheme = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            multiSelect: false,
            margin: '7 30 3 30',
            name: 'mainTheme',
            labelWidth: 100,
            dictTypeId: 'kpi_theme',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.mainTheme'), //战略主题
            columnWidth: .5,
            labelAlign: 'left',
            columns: 5
        });
        basicfieldSet.add(mainTheme);

        //辅助纬度
        me.otherDim = Ext.create('FHD.ux.dict.DictSelect', {
            margin: '7 30 3 30',
            name: 'otherDim',
            labelWidth: 100,
            dictTypeId: 'kpi_dimension',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.otherDim'), //辅助纬度
            columnWidth: .5,
            labelAlign: 'left',
            columns: 5,
            multiSelect: true
        });
        basicfieldSet.add(me.otherDim);
        //辅助战略主题
        me.otherTheme = Ext.create('FHD.ux.dict.DictSelect', {
            margin: '7 30 3 30',
            name: 'otherTheme',
            labelWidth: 100,
            dictTypeId: 'kpi_theme',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.otherTheme'), //辅助战略主题
            columnWidth: .5,
            labelAlign: 'left',
            columns: 5,
            multiSelect: true
        });
        basicfieldSet.add(me.otherTheme);

        var isfocus = Ext.widget('dictradio', {
            xtype: 'dictradio',
            margin: '7 30 3 30',
            name: 'isfocustr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '关注', //是否关注
            defaultValue: '0yn_n',
            labelAlign: 'left',
            allowBlank: true,
            columnWidth: .5
        });
        basicfieldSet.add(isfocus);



        //是否启用
        me.estatus = Ext.widget('dictradio', {
            xtype: 'dictradio',
            margin: '7 30 3 30',
            name: 'estatus',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '启用', //是否启用
            defaultValue: '0yn_y',
            labelAlign: 'left',
            allowBlank: false,
            columnWidth: .5
        });
        basicfieldSet.add(me.estatus);

        //所属部门人员
        me.ownDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.owndept"), // 所属部门人员
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
        basicfieldSet.add(me.ownDept);

        //查看人
        me.viewDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			type: 'dept_emp',
			fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.viewEmp"), // 所属部门人员
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
        
        basicfieldSet.add(me.viewDept);

        //报告人
        me.reportDept = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			type: 'dept_emp',
			fieldLabel: FHD.locale.get("fhd.strategymap.strategymapmgr.form.reportEmp"), // 所属部门人员
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
        
        basicfieldSet.add(me.reportDept);
 		//评估值公式
        me.assessmentFormula = Ext.create('FHD.ux.kpi.FormulaTrigger', {
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.assessmentFormula'), //评估值公式
            hideLabel: false,
            emptyText: '',
            labelAlign: 'left',
            flex: 1.5,
            cols: 20,
            rows: 5,
            name: 'assessmentFormula',
            type: 'strategy',
            showType: 'strategyType',
            column: 'assessmentValueFormula',
            labelWidth: 100,
            columnWidth: .5
        });
        basicfieldSet.add(me.assessmentFormula);
        
        //预警公式
        me.warningFormula = Ext.create('FHD.ux.kpi.FormulaTrigger', {
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.warningFormula'),
            hideLabel: false,
            disabled: true,
            emptyText: '',
            labelAlign: 'left',
            flex: 1.5,
            labelWidth: 100,
            cols: 20,
            margin: '7 30 3 30',
            rows: 5,
            name: 'warningFormula',
            type: 'kpi',
            showType: 'all',
            column: 'assessmentValueFormula',
            columnWidth: .5
        });
        basicfieldSet.add(me.warningFormula);


       
        //图表类型
        me.chartTypeStr = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
            editable: false,
            labelWidth: 100,
            margin: '7 30 3 30',
            multiSelect: true,
            name: 'chartTypeStr',
            dictTypeId: 'strategy_map_chart_type',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.category.charttype') + '<font color=red>*</font>', //图表类型
            columnWidth: .5,
            labelAlign: 'left',
            allowBlank: false,
            maxHeight: 70,
            defaultValue:'strategy_map_chart_type_2'
        });
        
        basicfieldSet.add(me.chartTypeStr);
        //说明
        var desc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 5,
            margin: '7 30 3 30',
            labelAlign: 'left',
            name: 'desc',
            fieldLabel: FHD.locale.get('fhd.sys.dic.desc'), //说明
            maxLength: 4000,
            columnWidth: .5
        });
        basicfieldSet.add(desc);

        me.add(basicfieldSet);


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
    /**
     * 清除form数据
     */
    clearFormData: function () {
        var me = this;
        me.getForm().reset();
        me.ownDept.clearValues();
        me.initCommonSelector();
        me.viewDept.clearValues();
        me.reportDept.clearValues();
        if('sm_root' == me.paramObj.parentid) {
        	 me.smSelector.initValue('');
        } else {
        	 me.smSelector.initValue(me.paramObj.parentid);
        }      
    },

    initParam: function (paramObj) {
        var me = this;
        me.paramObj = paramObj;
    },

    reloadData: function () {
        var me = this;
        me.form.setValues({
            currentSmId: me.paramObj.smid,
            parentId: me.paramObj.parentid,
            parentname: me.paramObj.parentname
        });
        if (me.paramObj.editflag) {
            me.load({
                waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
                url: __ctxPath + '/kpi/kpistrategymap/findsmbyidtojson.f',
                params: {
                    id: me.paramObj.smid
                },
                success: function (form, action) {
                	// 初始化上级目标选择组件
                	var pSmId = action.result.data.parentId;
                    me.smSelector.initValue(pSmId,me.paramObj.smid);
                    
                    if(action.result.data.ownDept){
                    	var owndeptsValue = Ext.JSON.decode(action.result.data.ownDept);
			    		me.ownDept.setValues(owndeptsValue);
                    }
                    
			    	if(action.result.data.viewDept){
			    		var viewDeptValue = Ext.JSON.decode(action.result.data.viewDept);
			    		me.viewDept.setValues(viewDeptValue);
			    	}
                    
			    	if(action.result.data.reportDept){
			    		var reportDeptValue = Ext.JSON.decode(action.result.data.reportDept);
			    		me.reportDept.setValues(reportDeptValue);
			    	}
                    
                    var multiSelect = action.result.multiSelect;
                    if (multiSelect.otherDim) {
                        //给辅助纬度赋值
                        var arr = Ext.JSON.decode(multiSelect.otherDim);
                        me.otherDim.setValue(arr);
                    }
                    if (multiSelect.otherTheme) {
                        //给辅助战略主题赋值
                        var arr = Ext.JSON.decode(multiSelect.otherTheme);
                        me.otherTheme.setValue(arr);
                    }

                    var vobj = form.getValues();
                    if (vobj.charttypehidden != "") {
                        var charttypearr = vobj.charttypehidden.split(',');
                        //给图表类型赋值
                        me.chartTypeStr.setValue(charttypearr);
                    }
                    return true;

                }
            });
        }
        if (!me.paramObj.editflag) {
            //赋默认值
            me.estatus.setValue('0yn_y');
            me.chartTypeStr.setValue(['strategy_map_chart_type_2']);
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
			me.ownDept.type = 'dept';
    	} else if(me.paramObj.empType == 'dept_emp') {
    		dataArray.push({empid:__user.empId,empno:__user.empNo,empname:__user.empName,deptid: __user.majorDeptId,deptno:__user.majorDeptNo,deptname:__user.majorDeptName});
    	}
    	me.ownDept.setValues(dataArray);
    	/*me.ownDept.type = 'dept_emp';
    	if(me.paramObj.empType == 'dept') {
    		dataArray = [{empid:'',deptid: __user.majorDeptId}];
    	} else if(me.paramObj.empType == 'dept_emp') {
    		dataArray = [{empid:__user.empId,deptid: __user.majorDeptId}];
    	}
    	me.ownDept.setHiddenValue(dataArray);*/
    }

});