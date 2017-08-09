/**
 * @author zhengjunxiang 2013-10-30
 * 风险事件添加表单的基类，下面有风险最全表单，风险评估表单，风险简单表单3个子类
 * 它只是一个接口类，没有任何界面，只有一些公关的属性和方法
 * 支持功能包括：
 * 1. 风险事件的添加
 * 2. 风险事件的修改
 * 3. 风险分类的添加
 * 4. 风险分类的修改
 * 5. 提供保存方法接口和修改接口
 * 6. 界面提供好保存按钮，可以设置是否显示	？？？后面没有实现
 * 7. 提供用户验证的接口，没有通过验证不进行提交
 * 8. 提供责任部门只读显示接口
 * 9. 提供保存状态的接口，用于满足区分风险评估模块添加的情况
 */
Ext.define('FHD.view.risk.cmp.form.RiskBasicForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskbasicform',
 	requires:[
    	'FHD.view.compoments.commonselect.CommonSelectInput'
    ],
	/**
	 * 接口属性
	 */
	type : 're',   		//风险还是风险事件 rbs:风险 re：风险事件
	setLoginDept:false,	//true 设置责任部门为当前登录人的部门，false 不进行设置
    isEdit:false,	//是否处于编辑状态
	riskId:null,	//编辑的id
	state:1,		//默认是1，但是风险评估模块添加的状态是2
	/**
	 * 用户自定义验证
	 */
	userValidate:Ext.emptyFn(),	
	
	/**
	 * 保存风险
	 */
	save : Ext.emptyFn(),
	
	/**
	 * 修改风险
	 */
	merge : Ext.emptyFn(),
	
	/**
	 * 组件内部验证,调用用户自己的验证
	 */
	customValidate : function(){
		var me = this;
		
		//调用用户自定义验证
		if(me.userValidate){
			return me.userValidate();//调用用户验证
		}
		
		/**
		 * 组件内部验证
		 */
		//上级风险验证
		var parentRiskValid = true;
		var value=me.parentId.getValue();	
		if(!me.parentId.allowBlanks){
			if(value && value !='[]'){
				me.parentId.grid.setBodyStyle('background','#FFFFFF');
			}else{
				me.parentId.grid.setBodyStyle('background:#FFEDE9;border-color:red');
				parentRiskValid = false;
			}
		}
		
		//责任部门和相关部门重名验证
		var orgValid = true;
		var emparr = [];
		var deptarr = [];
		var v1 = [];
		var vv1 = me.respDeptName.getValue();
		if(vv1!=""&&vv1!=undefined){
			v1 = Ext.JSON.decode(vv1);
		}
		for(var i=0;i<v1.length;i++){
			var empid = v1[i].empid;
			var deptid = v1[i].deptid;
			deptarr.push(deptid);
			if(empid!=null && empid!=''){
				emparr.push(empid);
			}
		}
		
		var v2 = [];
		var vv2 = me.relaDeptName.getValue();
		if(vv2!=""&&vv2!=undefined){
			v2 = Ext.JSON.decode(vv2);
		}
		for(var i=0;i<v2.length;i++){
			var empid = v2[i].empid;
			var deptid = v2[i].deptid;
			if(empid!=null && empid!=''){
				//判断人员是否重复
				var repeat = false;
				for(var j=0;j<emparr.length;j++){
					if(emparr[j]==empid){
						repeat = true;
						break;
					}
				}
				if(repeat){
					FHD.notification('人员重复','操作提示');
					orgValid = false;
				}
			}else{
				//判断部门是否重复
				var repeat = false;
				for(var j=0;j<deptarr.length;j++){
					if(deptarr[j]==deptid){
						repeat = true;
						break;
					}
				}
				if(repeat){
					FHD.notification('部门重复','操作提示');
					orgValid = false;
				}
			}
		}
		
		return parentRiskValid && orgValid;
	},
	
	/**
	 * 根据上级编码生成下级编码
	 */
	setRiskCode : function(){
		var me = this;
		var values = [];
		var store = me.parentId.getGridStore();
		store.each(function(r){
    		values.push(r.data.id);
    	});
		var id = values[0];
		var code = me.getRiskCode(id);//编号自动生成
		
		me.getForm().setValues({
			code : code
    	});
	},
	getRiskCode : function(parentRiskId) {
		var code = "";
		FHD.ajax({
	   			async:false,
	   			params: {
	                parentId: parentRiskId
	            },
	            url: __ctxPath + '/cmp/risk/getRiskCode.f',
	            callback: function (ret) {
	             	code = ret.code;
	            }
	    });
	    return code;
	},
	
	/**
	 * 构建基本信息
	 */
	addBasicComponent : function() {
		var me = this;
		var itemArr = [];	//显示的数据项
		//上级风险
		if(me.type == 're'){	//根据是风险添加还是风险事件添加，风险事件添加，只能选择叶子节点
			/* 宋佳重写了风险选择组件*/
	        me.parentRisk = Ext.widget('commonselectinput',{
	        	columnWidth : .5,
	            labelWidth : 105,
	            schm : me.schm,
	            multiSelect : false,
	            type : me.type,
	            fieldLabel : '上级风险' + '<font color=red>*</font>:'
	        });
	        itemArr.push(me.parentRisk);
		}else{
			me.parentId = Ext.create('FHD.view.risk.cmp.RiskSelector', {
				allowBlanks:true,
				title : '请您选择风险',
				fieldLabel : '上级风险',
				name : 'parentId',
				//2017年4月20日15:12:05吉志强添加         风险辨识流程到达风险辨识环节添加风险时加上分库标示
				schm: me.schm,
				multiSelect: false,
				columnWidth : .5,
				afterEnter:function(){
					me.setRiskCode();
				}
			});
			itemArr.push(me.parentId);
		}

		//编码
		var code = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '风险编号' + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'code',
			maxLength : 255,
			columnWidth : .5,
			allowBlank:false
		});
		itemArr.push(code);

		//风险名称
		var name = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 2,
			fieldLabel : '风险名称' + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'name',
			allowBlank : false,
			height : 40,
			columnWidth : .5
		});
		itemArr.push(name);
		
		//风险描述
		var desc = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 2,
			fieldLabel : '风险描述',
			margin : '7 30 3 30',
			name : 'desc',
			allowBlank : true,
			height : 40,
			columnWidth : .5
		});
		itemArr.push(desc);
		
		var responseText = Ext.widget('textareafield',{
			rows : 2,
			fieldLabel : '应对措施',
			margin : '7 30 3 30',
			name : 'responseText',
			allowBlank : true,
			height : 40,
			columnWidth : 1
		});
		itemArr.push(responseText);
		
		//责任部门/人
		/*me.respDeptName = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '责任部门/人',// + '<font color=red>*</font>',
			labelAlign : 'left',
			type : 'dept_emp',
			subCompany : false,
			multiSelect : true,
			margin : '7 30 3 30',
			name : 'respDeptName',
			allowBlank : true,
			height : 120,
			columnWidth : .5
		});*/
		me.respDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect:true,
			type: 'dept_emp',
			fieldLabel: '责任部门/人' + '<font color=red>*</font>', // 所属部门人员
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
			name : 'respDeptName',
			value:''
		});
		itemArr.push(me.respDeptName);
		//默认填入登录人所在的部门,以文本框的形式显示出来
    	if(me.setLoginDept){
    		
    		var deptid = '';
    		var deptname = '';
    		FHD.ajax({
	   			async:false,
	   			params: {
	   				employeeId:__user.empId,
	   				executionId : me.executionId
	            },
	            url: __ctxPath + '/cmp/risk/getLoginDept.f',
	            callback: function (ret) {
	            	deptid = ret.deptid;
	            	deptname = ret.deptname;
	            }
	        });
    		/*
    		 * 设置责任部门为不可见状态（reload，reset还可以操作），添加新的文本和隐藏域
    		 */
    		var value = [];
        	var obj = {};
        	obj["deptid"] = deptid;
        	obj["empid"] = '';
        	value.push(obj);
        	me.respDeptName.setHideValue(value);
			me.respDeptName.initValue(Ext.encode(value));
    		me.respDeptName.setVisible(false);
    		
    		me.respDeptLabel = Ext.widget('displayfield', {
                xtype: 'displayfield',
                rows: 2,
                fieldLabel: '责任部门',
                margin: '7 30 3 30', 
                name: 'respDeptLabel',
                columnWidth: .5,
                value:deptname
            });
    		itemArr.push(me.respDeptLabel);
    	}

		//相关部门/人
		/*me.relaDeptName = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '相关部门/人',// + '<font color=red>*</font>',
			labelAlign : 'left',
			type : 'dept_emp',
			subCompany : true,
			multiSelect : true,
			margin : '7 30 3 30',
			name : 'relaDeptName',
			allowBlank : true,
			height : 120,
			columnWidth : .5
		});*/
		
		me.relaDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect:true,
			type: 'dept_emp',
			fieldLabel: '相关部门/人', // 所属部门人员
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
			name: 'relaDeptName',
			value:''
		});
		
		
		itemArr.push(me.relaDeptName);

		
		
		var basicfieldSet = Ext.widget('fieldset', {
			title : FHD.locale.get('fhd.common.baseInfo'),
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				height : 24,
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			items : itemArr
		});
		
		return basicfieldSet;
	},
	
	addRelaComponentTest : function() {
		var me = this;

		// 影响指标
		me.influKpiName = Ext.create('FHD.ux.kpi.opt.KpiSelector', {
			labelWidth : 100,
			gridHeight : 40,
			btnHeight : 25,
			btnWidth : 22,
			multiSelect : true,
			labelAlign : 'left',
			labelText : '影响指标',// + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'influKpiName',
			allowBlank : true,
			height : 40,
			columnWidth : .5
		});

		// 风险指标
		me.riskKpiName = Ext.create('FHD.ux.kpi.opt.KpiSelector', {
			labelWidth : 100,
			gridHeight : 40,
			btnHeight : 25,
			btnWidth : 22,
			multiSelect : true,
			labelText : '风险指标',
			labelAlign : 'left',
			margin : '7 30 3 30',
			name : 'riskKpiName',
			height : 40,
			columnWidth : .5
		});
		
		// 影响流程
		me.influProcessureName = Ext.create('FHD.ux.process.ProcessSelector', {
			labelWidth : 95,
			gridHeight : 25,
			btnHeight : 25,
			btnWidth : 22,
			single : false,
			fieldLabel : '影响流程',// + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'influProcessureName',
			allowBlank : true,
			multiSelect : true,
			height : 40,
			columnWidth : .5
		});
	
		// 控制流程
		me.controlProcessureName = Ext.create('FHD.ux.process.ProcessSelector',
				{
					labelWidth : 95,
					columnWidth : .5,
					gridHeight : 25,
					btnHeight : 25,
					single : false,
					fieldLabel : '控制流程',
					margin : '7 30 3 30',
					name : 'controlProcessureName',
					multiSelect : true,
					height : 40,
					columnWidth : .5
				});

		// 风险动因
		me.riskReason = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
			title : '请您选择风险动因',
			fieldLabel : '风险动因',
			multiSelect: true,
			height:40,
			labelAlign : 'left',
			name : 'riskReason',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 风险影响
		me.riskInfluence = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
			title : '请您选择风险影响',
			fieldLabel : '风险影响',
			multiSelect : true,
			height : 40,
			labelAlign : 'left',
			name : 'riskInfluence',
			margin : '7 30 3 30',
			columnWidth : .5
		});
		
		var relafieldSet = Ext.widget('fieldset', {
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '相关信息',
			items : [me.influKpiName, me.riskKpiName, me.influProcessureName,  me.controlProcessureName] //没有关联功能，去掉动因2个数据项目, me.riskReason, me.riskInfluence
		});
		
		return relafieldSet;
	},
	addExtendComponentTest : function() {
		var me = this;

		// 风险类别
		var riskKind = Ext.create('FHD.ux.dict.DictCheckbox', {// DictCheckbox
			name : 'riskKind',
			dictTypeId : 'rm_class',
			//defaultValue : 'company',
			labelAlign : 'left',
			fieldLabel : '是否合规',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 风险类型
		var riskType = Ext.create('FHD.ux.dict.DictRadio', {
			name : 'riskType',
			dictTypeId : 'rm_type',
			//defaultValue : 'threat',
			labelAlign : 'left',
			fieldLabel : '可控性',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 是否定量
		var isFix = Ext.create('FHD.ux.dict.DictRadio', {
			name : 'isFix',
			dictTypeId : '0yn',
			defaultValue : '0yn_n',
			labelAlign : 'left',
			fieldLabel : '是否定量',
			multiSelect : false,
			margin : '7 30 3 30',
			hidden:true,
			columnWidth : .5
		});

		// 是否应对
		var isAnswer = Ext.create('FHD.ux.dict.DictRadio', {
			xtype : 'dictradio',
			name : 'isAnswer',
			dictTypeId : '0yn',
			defaultValue : '0yn_y',
			labelAlign : 'left',
			fieldLabel : '是否应对',
			multiSelect : false,
			margin : '7 30 3 30',
			hidden:true,
			columnWidth : .5
		});

		// 是否启用
		var isUse = Ext.create('FHD.ux.dict.DictRadio', {
			xtype : 'dictradio',
			name : 'isUse',
			dictTypeId : '0yn',
			defaultValue : '0yn_y',
			labelAlign : 'left',
			fieldLabel : '是否启用',
			multiSelect : false,
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 是否继承上级模板
		var isInherit = Ext.create('FHD.ux.dict.DictRadio', {
			xtype : 'dictradio',
			name : 'isInherit',
			dictTypeId : '0yn',
			defaultValue : '0yn_y',
			labelAlign : 'left',
			fieldLabel : '是否继承',// 上级模板
			margin : '7 30 3 30',
			columnWidth : .5
		});
		
		// 是否计算
        var calcStr = Ext.create('FHD.ux.dict.DictRadio', {
            xtype: 'dictradio',
            labelWidth: 100,
            margin : '7 30 3 30',
            name: 'calcStr',
            dictTypeId: '0yn',
            fieldLabel: '是否计算',
            labelAlign: 'left',
            allowBlank: true,
            columnWidth: .5
        });
        
        // 计算公式
		me.formulaDefine = Ext.create('FHD.ux.kpi.FormulaTrigger', {
			fieldLabel : "计算公式",
			hideLabel : false,
			//height : 40,
			emptyText : '',
			labelAlign : 'left',
			flex : 1.5,
			labelWidth : 100,
			cols : 20,
			margin : '7 30 3 30',
			rows : 3,
			name : 'formulaDefine',
			type : 'kpi',
			showType : 'all',
			column : 'assessmentValueFormula',
			columnWidth : .5
		});
		
		//结果收集频率
        me.gatherfrequenceDict = Ext.create('FHD.ux.collection.CollectionSelector', {
            name: 'gatherfrequence',
            xtype: 'collectionSelector',
            label: '采集频率', //结果收集频率
            valueDictType: '',
            valueRadioType: '',
            single: false,
            value: '',
            labelWidth: 100,
            margin : '7 30 3 30',
            columnWidth: .5
        });
		
		// 延期天数
		var resultCollectInterval = Ext.widget('numberfield', {
			xtype : 'numberfield',
			fieldLabel : '采集延期天数',
			name : 'resultCollectInterval',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 序号
		var sort = Ext.widget('numberfield', {
			xtype : 'numberfield',
			fieldLabel : '序号',
			name : 'sort',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 评估模板
		var templateNameStore = Ext.create('Ext.data.Store', {

			fields : [ 'type', 'name' ],
			remoteSort : true,
			proxy : {
				type : 'ajax',
				url : __ctxPath + me.findTemplateUrl,
				reader : {
					type : 'json',
					root : 'datas',
					totalProperty : 'totalCount'
				}
			}
		});
		templateNameStore.load();

		me.templateName = Ext.create('Ext.form.ComboBox', {
			name : 'templateId',
			store : templateNameStore,
			displayField : 'name',
			valueField : 'type',
			labelAlign : 'left',
			fieldLabel : '评估模板',
			multiSelect : false,
			triggerAction : 'all',
			flex : 10
		});
		
		me.templatebutton = Ext.create('Ext.button.Button',{
            iconCls:'icon-magnifier',
            height: 22,
            width: 22,
            flex : 0.5,
            handler:function(){
            	if(me.templateName.getValue() != null && me.templateName.getValue() != ''){
            		me.riskAssessOpe = Ext.create('FHD.view.risk.assess.quaAssess.commAssess.RiskAssessOpe');
					me.riskAssessOpe.loadInit(me.templateName.getValue());
					var closebutton = Ext.create('Ext.button.Button',{
						text : $locale('fhd.common.close'),
						width : 100,
						handler : function() {
							templatewindow.close();
						}
					});
            		var templatewindow = Ext.create('FHD.ux.Window',{
						title:'模板维度打分查看',
						maximizable: true,
						modal:true,
						width:800,
						height: 400,
						collapsible:true,
						autoScroll : true,
						items : me.riskAssessOpe,
						buttons:closebutton
					}).show();
            	}
		    }
    	});
    	
    	me.templatacontainer = Ext.create('Ext.form.FieldContainer',{
    		margin : '7 30 5 30', // emptyText:FHD.locale.get('fhd.common.pleaseSelect'),//默认为空时的提示
    		columnWidth : .5,
    		layout : 'hbox',
    		items : [
    			me.templateName,me.templatebutton
    		]
    	});

		// 涉及板块
		var relePlate = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'relePlate',
			multiSelect : true,
			dictTypeId : '0forum',
			labelAlign : 'left',
			fieldLabel : '涉及板块',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 应对策略
		var responseStrategy = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'responseStrategy',
			multiSelect : true,
			dictTypeId : 'rm_response_strategy',
			labelAlign : 'left',
			fieldLabel : '应对策略',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 内部动因
		var innerReason = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'innerReason',
			multiSelect : true,
			dictTypeId : 'rm_internal_cause',
			labelAlign : 'left',
			fieldLabel : '内部动因',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 外部动因
		var outterReason = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'outterReason',
			multiSelect : true,
			dictTypeId : 'rm_external cause',
			labelAlign : 'left',
			fieldLabel : '外部动因',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 风险价值链
		var valueChain = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'valueChain',
			multiSelect : true,
			dictTypeId : 'value_chain',
			labelAlign : 'left',
			fieldLabel : '风险价值链',
			margin : '7 30 3 30',
			hidden:true,
			columnWidth : .5
		});

		// 影响期间
		var impactTime = Ext.create('FHD.ux.dict.DictSelect', {
			name : 'impactTime',
			multiSelect : true,
			dictTypeId : 'influence_period',
			labelAlign : 'left',
			fieldLabel : '影响期间',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 附件上传
		var fileUpload = Ext.create('FHD.ux.fileupload.FileUpload', {
			xtype : 'FileUpload',
			name : 'fileUpload',// 名称
			showModel : 'base',// 显示模式
			height : 40,
			labelWidth : 100,
			labelText : $locale('fileupdate.labeltext'),
			labelAlign : 'left',
			fieldLabel : '是否启用',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		var extendfieldSet = Ext.widget('fieldset', {
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			collapsible : true,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '扩展信息',
			items : [riskKind, riskType, 
					isFix, isAnswer, 
					isInherit,isUse,
					me.templatacontainer,calcStr,
					me.formulaDefine, sort, 
					me.gatherfrequenceDict,resultCollectInterval,
					relePlate, responseStrategy,
					innerReason, outterReason, 
					valueChain, impactTime,
					fileUpload]
		});
		return extendfieldSet;
	},
	
	/**
	 * 清空表单数据
	 */
	resetData : function(id) {
		var me = this;
        me.isEdit = false;
        
        //清空组件值
		me.getForm().reset();
	},
	
	/**
	 * 加载表单数据
	 */
    reloadData: function (id) {
    	var me = this;
    	me.isEdit = true;
    	me.riskId = id;
    },
	
	initComponent : function() {
		var me = this;
		me.callParent(arguments);
	}
});