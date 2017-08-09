Ext.define('FHD.view.kpi.cmp.kpi.BasicForm', {
    extend: 'Ext.form.Panel',
    waitMsgTarget:true,//设置等待图标
    requires: ['FHD.ux.dict.DictRadio','FHD.ux.dict.DictSelectForEditGrid'],
    border: false,
    
    paramObj:{kpiId:'',selecttypeflag:'',kpitypeid:'',kpitypename:'',scid:'',scname:'',empType:'',smid:'',smName:'',riskId:'',riskName:''},
    
    clearParamObj:function(){
    	var me = this;
    	me.paramObj.kpiId = '';
    	me.paramObj.selecttypeflag = '';
    	me.paramObj.kpitypeid = '';
    	me.paramObj.kpiId = '';
    },
    
    clearFormData:function(){
    	var me = this;
    	me.getForm().reset();
    	// 设置的默认的部门和人员
    	if(me.owndept){
    		me.owndept.clearValues();
    	}
    	if(me.paramObj.empType) {
    		me.initCommonSelector();
    	}
    	if(me.gatherDept){
    		me.gatherDept.clearValues();
    	}
    	if(me.targetdept){
    		me.targetdept.clearValues();
    	}
    	if(me.reportdept){
    		me.reportdept.clearValues();
    	}
    	if(me.viewdept){
    		me.viewdept.clearValues();
    	}
    },
    
    /**
     * 点击下一步提交事件
     * @param {panel} cardPanel cardpanel面板
     * @param {boolean} finishflag 是否完成标示,true代表点击了'完成按钮'
     */
    last: function (cardPanel, finishflag) {
        var me = this;
        var form = me.getForm();
        me.defaultname.setDisabled(false);
        var kpimainpanel = me.pcontainer.pcontainer;
        var categoryname = undefined;
        var categoryId = undefined;
        if(me.paramObj.scid){
        	categoryname = me.paramObj.scname;
        	categoryId = me.paramObj.scid;
        }
        
        var vobj = form.getValues();
        var paramObj = {};
        paramObj.name = vobj.name;
        paramObj.code = vobj.code;
        paramObj.categoryname = categoryname;
        paramObj.kpitypename = me.paramObj.kpitypename;
        paramObj.type = "KPI";
        pKpiId = me.kpioptselector.getFieldValue();
        form.setValues({
        	opflag:kpimainpanel.paramObj.editflag==true?'edit':'add',
        	categoryId:categoryId
        });
        if(!me.owndept.getValue()) {
        	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),'请选择所属部门人员！');
        	return false;
        }
        if(!form.isValid()){
        	return false;
        }

        FHD.ajax({
            url: __ctxPath + '/kpi/kpi/validate.f',//校验信息,名称和编码是否重复
            params: {
                id: me.paramObj.kpiId,
                validateItem: Ext.JSON.encode(paramObj)
            },
            callback: function (data) {
                if (data && data.success ) {
                	var addUrl;
                	if(me.paramObj.smid) {
                		 var addUrl = __ctxPath + '/kpi/kpi/mergekpi.f?id=' + me.paramObj.kpiId+"&parentid="+pKpiId+"&smid="+me.paramObj.smid;
                	} else if(me.paramObj.riskId){
                		 var addUrl = __ctxPath + '/kpi/kpi/mergekpi.f?id=' + me.paramObj.kpiId+"&parentid="+pKpiId+"&riskid="+me.paramObj.riskId;
                	}
                	else {
                		 var addUrl = __ctxPath + '/kpi/kpi/mergekpi.f?id=' + me.paramObj.kpiId+"&parentid="+pKpiId;
                	}
          
                    /*
                     * 保存指标信息
                     */
                    FHD.submit({
                        form: form,
                        url: addUrl,
                        callback: function (data) {
                            if (data) {
                            	var kpicardpanel =  me.pcontainer;
                                me.paramObj.kpiId = data.id;
                                me.paramObj.kpiname = vobj.name;
                                me.pcontainer.kpigatherform.paramObj.kpiId = data.id;
                                if (!finishflag) {//如果点击的不是完成按钮,需要移动到下一个面板
                                	cardPanel.lastSetBtnState(cardPanel,cardPanel.getActiveItem());
                                    cardPanel.pageMove("next");
                                    me.pcontainer.down("[name='kpi_kpi_caculate_btn_top']").setDisabled(false);
                                    me.pcontainer.navBtnState();
                                    //给公式赋值,公式编辑器需要设置它的targetid和targetnametargetid为指标ID,targetname为指标名称
                                    me.valueToFormulaName();
                                }else{
                                	kpicardpanel.undo();
                                }
                                
                            }
                        }
                    });
                } else {
                    //校验失败信息
                    if (data && data.error == "nameRepeat") {//名称重复
                    	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get("fhd.kpi.kpi.prompt.namerepeat"));
                        return;
                    }
                    if (data && data.error == "codeRepeat") {//编码重复
                    	Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'),FHD.locale.get("fhd.kpi.kpi.prompt.coderepeat"));
                        return;
                    }
                }
            }
        });
    },
    

    valueToFormulaName:function(){
    	var me = this;
    	var resultformula = me.pcontainer.kpigatherform.resultFormula;
        var targetformula = me.pcontainer.kpigatherform.targetFormula;
        var assessmentformula = me.pcontainer.kpigatherform.assessmentFormula;
        
        var kpiid = me.paramObj.kpiId;
        var kpiname = me.paramObj.kpiname;
        resultformula.setTargetId(kpiid);
        resultformula.setTargetName(kpiname);
        targetformula.setTargetId(kpiid);
        targetformula.setTargetName(kpiname);
        assessmentformula.setTargetId(kpiid);
        assessmentformula.setTargetName(kpiname);
    },
    
    /**
     * 选择指标类型的按钮事件
     */
    selectKpiType: function () {
        var me = this;
        
        me.selectKpiTypeSelector = Ext.create('FHD.view.kpi.cmp.kpi.TypeSelector',{
        	gridHeight:480,
        	kpibasicform: me
        });
        
        me.formwindow = new Ext.Window({
            constrain: true,
            layout: 'fit',
            iconCls: 'icon-edit', //标题前的图片
            modal: true, //是否模态窗口
            collapsible: true,
            scroll: 'auto',
            closeAction:'destroy',
            width: 420,
            height: 550,
            title:'指标类型选择',
            maximizable: true, //是否增加最大化，默认没有
            items: [
                   ]
        });
        
        me.selectKpiTypeSelector.reload();
        me.formwindow.show();
        me.formwindow.add(me.selectKpiTypeSelector);

    },
    /**
     * 编码自动生产按钮事件
     */
    createCode: function () {
    	pKpiId = "kpi_root";
    	if(pKpiId){
    		var me = this;
            var vform = me.getForm();
            var vobj = vform.getValues();
            var paraobj = {};
            paraobj.id = me.paramObj.kpiId;
            paraobj.parentid = pKpiId;
            FHD.ajax({
                url: __ctxPath + '/kpi/kpi/findcodebyparentid.f',
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
        var basicfieldSet = Ext.create('Ext.form.FieldSet', {
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
            title: FHD.locale.get('fhd.kpi.kpi.form.basicinfo'),
            items: [

            {
                xtype: 'hidden',
                hidden: true,
                name: 'kpitypeid'
            }, {
                xtype: 'hidden',
                hidden: true,
                name: 'opflag'
               ,value: ''//提交时需要赋值
            }, {
                xtype: 'hidden',
                hidden: true,
                name: 'id'
            }, {
                xtype: 'hidden',
                hidden: true,
                name: 'categoryId'
               ,value: ''//提交时需要赋值
            }]
        });
        
        //指标类型
        var kpitype = Ext.widget('textfield', {
            xtype: 'textfield',
            disabled: true,
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.etype'), //指标类型
            margin: '7 3 3 30',
            name: 'kpitypename',
            maxLength: 255,
            columnWidth: .4
        });
        basicfieldSet.add(kpitype);

        //指标类型选择按钮
        me.selectBtn = Ext.widget('button', {
            xtype: 'button',
            margin: '7 30 3 3',
            text: FHD.locale.get('fhd.common.select'), //指标类型选择按钮
            handler: function () {
                me.selectKpiType();//指标按钮事件
            },
            columnWidth: .1
        });
        basicfieldSet.add(me.selectBtn);
        


        //是否继承
        me.isHerit = Ext.widget('dictradio', {
            //id: 'isherit',
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'isInheritStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.herit'), //是否继承
            defaultValue: '0yn_y',
            labelAlign: 'left',
            columnWidth: .5,
            listeners: {
                click: {
                    element: 'el',
                    fn: function () {
                        
                        for (var i = 0; i < me.isHerit.items.length; i++) {
                            if (me.isHerit.items.items[i].checked) {
                                if (me.isHerit.items.items[i].inputValue == '0yn_n') {
                                       me.selectBtn.setDisabled(true);  
                                } else if (me.isHerit.items.items[i].inputValue == '0yn_y') {
                                       me.selectBtn.setDisabled(false);
                                }
                            }
                        }

                    }
                }
            }
        });

        basicfieldSet.add(me.isHerit);

        //默认名称
        me.defaultname = Ext.create('FHD.view.kpi.cmp.kpi.KpiNameSelector', {
            fieldLabel: '指标名称' + '<font color=red>*</font>', //默认名称
            textfieldname: 'name',
            is_default: 'namedefault',
            columnWidth: .5
        });

        basicfieldSet.add(me.defaultname);

        //描述
        var desc = Ext.widget('textareafield', {
            xtype: 'textareafield',
            rows: 3,
            labelAlign: 'left',
            name: 'desc',
            fieldLabel: FHD.locale.get('fhd.sys.dic.desc'), //描述
            maxLength: 2000,
            columnWidth: .5
        });
        basicfieldSet.add(desc);
        
        //编码
        var code = Ext.widget('textfield', {
            xtype: 'textfield',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.code'), //编码
            margin: '7 3 3 30',
            name: 'code',
            maxLength: 255,
            columnWidth: .4
        });
        basicfieldSet.add(code);

        //自动生成按钮
        var codeBtn = Ext.widget('button', {
            xtype: 'button',
            margin: '7 30 3 3',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'), //自动生成按钮
            handler: function () {
                me.createCode();//自动生成按钮事件
            },
            columnWidth: .1
        });
        basicfieldSet.add(codeBtn);
        
        
        //短名称
        var shortname = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'shortName',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.shortName'), //短名称
            value: '',
            maxLength: 255,
            columnWidth: .5
        });
        basicfieldSet.add(shortname);
        
        //上级指标
       me.kpioptselector = Ext.create('FHD.ux.kpi.opt.KpiSelector', {
            name: 'parentKpiId',
            multiSelect: false,
            columnWidth: .5,
            gridHeight: 25,
            btnHeight: 25,
            labelText: FHD.locale.get('fhd.kpi.kpi.form.parentKpi'), //上级指标
            labelAlign: 'left',
            labelWidth: 101
        });

        basicfieldSet.add(me.kpioptselector);
        //所属部门
        
        me.owndept = Ext.create('Ext.ux.form.OrgEmpSelect',{
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
        
        basicfieldSet.add(me.owndept);

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
        
        basicfieldSet.add(me.gatherDept);
        //目标部门
        me.targetdept = Ext.create('Ext.ux.form.OrgEmpSelect',{
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
        
        
        basicfieldSet.add(me.targetdept);

        //报告部门
        me.reportdept =  Ext.create('Ext.ux.form.OrgEmpSelect',{
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
        
        basicfieldSet.add(me.reportdept);
        //查看部门
        me.viewdept = Ext.create('Ext.ux.form.OrgEmpSelect',{
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
        
        basicfieldSet.add(me.viewdept);

        me.add(basicfieldSet);

        var relafieldSet = Ext.widget('fieldset', {
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
        //是否启用
        var status = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'statusStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel: '启用',
            defaultValue: '0yn_y',
            labelAlign: 'left',
            allowBlank: false,
            columnWidth: .5
        });
        relafieldSet.add(status);

        var monitor = Ext.widget('dictradio', {
            xtype: 'dictradio',
            labelWidth: 105,
            name: 'monitorStr',
            columns: 4,
            dictTypeId: '0yn',
            fieldLabel:'监控',
            defaultValue: '0yn_y',
            labelAlign: 'left',
            columnWidth: .5
        });


        relafieldSet.add(monitor);
        //单位
        var units = Ext.create('widget.dictselectforeditgrid', {
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
        relafieldSet.add(units);
        //开始日期
        var startDate = Ext.widget('datefield', {
            xtype: 'datefield',
            format: 'Y-m-d',
            name: 'startDateStr',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.startdate'), //开始日期
            columnWidth: .5,
            allowBlank: true            
        });

        relafieldSet.add(startDate);

        //指标类型
        var type = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            multiSelect: false,
            name: 'typeStr',
            dictTypeId: 'kpi_etype',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.etype'), //指标类型
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_etype_positive'
        });
        relafieldSet.add(type);
        //指标性质
        var kpiType = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 100,
            multiSelect: false,
            name: 'kpiTypeStr',
            dictTypeId: 'kpi_kpi_type',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.type'), //指标性质
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_kpi_type_assessment'
        });
        relafieldSet.add(kpiType);

        //亮灯依据
        var alarmMeasure = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            multiSelect: false,
            name: 'alarmMeasureStr',
            dictTypeId: 'kpi_alarm_measure',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.alarmMeasure'), //亮灯依据
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_alarm_measure_score'
        });
        relafieldSet.add(alarmMeasure);
        var extfieldSet = Ext.create('Ext.form.FieldSet', {
            xtype: 'fieldset', //相关信息fieldset
            autoHeight: true,
            autoWidth: true,
            collapsible: true,
            collapsed: true,
            defaults: {
                margin: '7 30 3 30',
                labelWidth: 105
            },
            layout: {
                type: 'column'
            },
            title: '扩展信息'
        });
        //预警依据
        var alarmBasis = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 100,
            multiSelect: false,
            name: 'alarmBasisStr',
            dictTypeId: 'kpi_alarm_basis',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.alarmBasis'), //预警依据
            columnWidth: .5,
            labelAlign: 'left',
            defaultValue: 'kpi_alarm_basis_forecast'
        });
        extfieldSet.add(alarmBasis);
        //主纬度
        me.mainDim = Ext.create('widget.dictselectforeditgrid', {
            editable: false,
            labelWidth: 105,
            multiSelect: false,
            name: 'mainDim',
            dictTypeId: 'kpi_dimension',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.mainDim'), //主纬度
            columnWidth: .5,
            labelAlign: 'left'
        });
        extfieldSet.add(me.mainDim);
        //辅助纬度
        me.otherDim = Ext.create('FHD.ux.dict.DictSelect', {
            maxHeight: 60,
            labelWidth: 100,
            name: 'otherDim',
            dictTypeId: 'kpi_dimension',
            fieldLabel: FHD.locale.get('fhd.strategymap.strategymapmgr.form.otherDim'), //辅助纬度
            columnWidth: .5,
            labelAlign: 'left',
            multiSelect: true
        });
        extfieldSet.add(me.otherDim);

        //目标值别名
        var targetValueAlias = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'targetValueAlias',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.targetValueAlias'), //目标值别名
            maxLength: 255,
            columnWidth: .5,
            value: FHD.locale.get('fhd.kpi.kpi.form.targetValueAlias'),
            labelWidth: 105
        });
        extfieldSet.add(targetValueAlias);
        //实际值别名
        var resultValueAlias = Ext.widget('textfield', {
            xtype: 'textfield',
            name: 'resultValueAlias',
            fieldLabel: FHD.locale.get('fhd.kpi.kpi.form.resultValueAlias'), //实际值别名
            maxLength: 255,
            columnWidth: .5,
            value: FHD.locale.get('fhd.kpi.kpi.form.resultValueAlias')
        });
        extfieldSet.add(resultValueAlias);
        me.add(relafieldSet);
        me.add(extfieldSet);

    },
    
    typeWindowClose:function(){
    	var me = this;
    	me.formwindow.close();
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
        
        Ext.applyIf(me, {
            autoScroll: true,
            border: me.border,
            autoHeight: true,
            layout: 'column',
            height: FHD.getCenterPanelHeight() - 75,
            width: FHD.getCenterPanelWidth() - 258,
            bodyPadding: "0 3 3 3"
        });
        
        
        me.callParent(arguments);

        //向form表单中添加控件
        me.addComponent();
        
    },
    /**
     * 初始化默认值
     */
    initFormData:function(){
    	var me = this;
    	me.getForm().setValues(
    			{
    				monitorStr:'0yn_y',
    				statusStr:'0yn_y',
    				isInheritStr:'0yn_y',
    				typeStr:'kpi_etype_positive',
    				kpiTypeStr:'kpi_kpi_type_assessment',
    				alarmMeasureStr:'kpi_alarm_measure_score',
    				alarmBasisStr:'kpi_alarm_basis_forecast'
    				
    			});
    	me.kpioptselector.initGridStore("kpi_root");
    },
    
    /**
     * 加载form数据
     */
    formLoad: function () {
        var me = this;
        //清除数据
        //基本信息面板
        me.clearFormData();
        //采集结果面板
        me.pcontainer.kpigatherform.clearFormData();
        var vform = me.getForm();
        var id = me.paramObj.kpiId;
        if (me.paramObj.selecttypeflag) {
            //说明是选择指标类型后,的form加载
            id = me.paramObj.kpitypeid; //指标类型ID
            vform.setValues({
                kpitypename: me.paramObj.kpitypename,//指标类型名称
                kpitypeid: id,
                isInheritStr:'0yn_y'
            }); 
        }
        //根据指标类型ID加载kpibasicform数据
        me.form.load({
            waitMsg: FHD.locale.get('fhd.kpi.kpi.prompt.waiting'),
            url: __ctxPath + '/kpi/Kpi/findKpiByIdToJson.f',
            params: {
                id: id
            },
            /**
             * form加载数据成功后回调函数
             */
            success: function (form, action) {
                pKpiId = action.result.data.parentKpiId;
                if(action.result.data.isInheritStr&&action.result.data.isInheritStr=='0yn_n'){
                	me.selectBtn.setDisabled(true); 
                }
                //如果指标的parentId为空时,说明是第一级节点,赋值为默认根节点
                if (pKpiId == "none") {
                    pKpiId = "kpi_root";
                }
                
                if(action.result.data.ownDept){
                	var ownDeptValue = Ext.JSON.decode(action.result.data.ownDept);
		    	 	me.owndept.setValues(ownDeptValue);
                }
                 
		    	if(action.result.data.reportDept){
		    		var reportDeptValue = Ext.JSON.decode(action.result.data.reportDept);
		    	 	me.reportdept.setValues(reportDeptValue);
		    	}
                 
		    	if(action.result.data.viewDept){
		    		var viewDeptValue = Ext.JSON.decode(action.result.data.viewDept);
		    	 	me.viewdept.setValues(viewDeptValue);
		    	}
                 
		    	if(action.result.data.gatherDept){
		    		var gatherDeptValue = Ext.JSON.decode(action.result.data.gatherDept);
		    	 	me.gatherDept.setValues(gatherDeptValue);
		    	}
                
		    	if(action.result.data.targetDept){
		    		var targetDeptValue = Ext.JSON.decode(action.result.data.targetDept);
		    	 	me.targetdept.setValues(targetDeptValue);
		    	}
		    	 
                 
                
                
                //给赋值纬度赋值
                var otherDimMultiCombo = me.otherDim;
                var otherDimArray = action.result.data.otherDimArray;
                if (otherDimArray) {
                    var arr = Ext.JSON.decode(otherDimArray);
                    otherDimMultiCombo.setValue(arr);
                }
                //给指标选择控件赋值
                me.kpioptselector.initGridStore(pKpiId);
                if (me.paramObj.selecttypeflag) {
                	var vform = me.getForm();
                	var defaultName;
                	if(me.paramObj.scname) {
                		defaultName = me.paramObj.scname + " " + me.paramObj.kpitypename//选择指标类型后,名称显示为记分卡名称+' '+指标类型名称
                	} else if(me.paramObj.smname) {
                		defaultName = me.paramObj.smname + " " + me.paramObj.kpitypename//选择指标类型后,名称显示为战略目标名称+' '+指标类型名称
                	} else if(me.paramObj.riskName) {
                		defaultName = me.paramObj.riskName + " " + me.paramObj.kpitypename//选择指标类型后,名称显示为风险名称+' '+指标类型名称
                	}else {
                		defaultName = me.paramObj.kpitypename;
                	}
 
                    vform.setValues({
                    	code:'',
                    	name: defaultName
                    	//name: me.paramObj.kpitypename
                    }); 
                }
                if(me.paramObj.empType) {
                	me.initCommonSelector();
                }
                return true;
            }
        });
        
        //根据指标类型ID加载kpigatherform
        me.pcontainer.kpigatherform.loadFormById(id);
        //根据指标类型ID加载kiwarningset列表数据
        me.pcontainer.kpiwarningset.reLoadGridById(id,true);
    },
    // 设置所属人员部门控件默认值为本部门或当前登录人
    initCommonSelector: function(){
    	var me = this;
    	var dataArray = [];
    	if(me.paramObj.empType == 'dept') {   		
			dataArray.push({id:__user.majorDeptId,deptno:__user.majorDeptNo,deptname:__user.majorDeptName});
			me.owndept.type = 'dept';
    	} else if(me.paramObj.empType == 'dept_emp') {
    		dataArray.push({empid:__user.empId,empno:__user.empNo,empname:__user.empName,deptid: __user.majorDeptId,deptno:__user.majorDeptNo,deptname:__user.majorDeptName});
    	}
    	me.owndept.setValues(dataArray);
    	/*me.owndept.type = 'dept_emp';
    	if(me.paramObj.empType == 'dept') {
    		dataArray = [{empid:'',deptid: __user.majorDeptId}];
    	} else if(me.paramObj.empType == 'dept_emp') {
    		dataArray = [{empid:__user.empId,deptid: __user.majorDeptId}];
    	}
    	me.owndept.setHiddenValue(dataArray);*/
    }
    	
});