/**
 * @author zhengjunxiang 2013-10-30
 * 风险事件添加表单的基类，下面有风险最全表单，风险评估表单，风险简单表单3个子类
 * 它只是一个接口类，没有任何界面，只有一些公关的属性和方法
 * 支持功能包括：
 * 1. 风险事件的添加
 * 2. 风险事件的修改
 * 3. 风险分类的添加
 * 4. 风险分类的修改
 * 5. 提供保存方法接口和修改接口save
 * 7. 提供用户验证的接口，没有通过验证不进行提交
 * 8. 提供责任部门只读显示接口
 * 9. 提供保存状态的接口，用于满足区分风险评估模块添加的情况
 */
Ext.define('FHD.view.risk.cmp.form.RiskStorageForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskstorageform',

	/**
	 * 接口属性
	 */
	type : 're',   		//风险还是风险事件 rbs:风险 re：风险事件
	setLoginDept:false,	//true 设置责任部门为当前登录人的部门，false 不进行设置
    isEdit:false,	//是否处于编辑状态
	riskId:null,	//编辑的id
	state:1,		//默认是1，但是风险评估模块添加的状态是2
	
	/**
	 * 变量
	 */
	archiveStatus:'archived',	//归档状态
	
	/**
	 * 常量
	 */
	saveUrl: '/cmp/risk/saveRiskStorage.f',
	mergeUrl:'/cmp/risk/mergeRiskStorage.f',
	findUrl: '/cmp/risk/findRiskEditInfoById',
	findTemplateUrl: '/access/formulateplan/findTemplatesrisk.f',
	isInherit:'0yn_y',	//是否继承
	
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
		}else
			{
			FHD.notification('责任部门不能为空','操作提示');
					orgValid = false;
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
		if(me.type=='re'){	//根据是风险添加还是风险事件添加，风险事件添加，只能选择叶子节点
			me.parentId = Ext.create('FHD.view.risk.cmp.RiskSelector', {
				onlyLeaf: true,
				allowBlanks:false,
				title : '请您选择风险',
				fieldLabel : '上级风险' + '<font color=red>*</font>',
				name : 'parentId',
				multiSelect: false,
				columnWidth : .5,
				afterEnter:function(){
					me.setRiskCode();
				}
			});
			itemArr.push(me.parentId);
		}else{
			me.parentId = Ext.create('FHD.view.risk.cmp.RiskSelector', {
				allowBlanks:true,
				title : '请您选择风险',
				fieldLabel : '上级风险',
				name : 'parentId',
				multiSelect: false,
				schm : me.typeId,
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
			height : 60,
			columnWidth : .5
		});*/
		
		me.respDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel: '责任部门/人'+'<font color=red>*</font>',
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
			name: 'respDeptName',
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
	   				employeeId:__user.empId
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
        	me.respDeptName.setHiddenValue(value);
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
			subCompany : false,
			multiSelect : true,
			margin : '7 30 3 30',
			name : 'relaDeptName',
			allowBlank : true,
			height : 60,
			columnWidth : .5
		});*/
		me.relaDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: true,
			type: 'dept_emp',
			fieldLabel: '相关部门/人',
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
			height : 60,
			columnWidth : .5
		});
		itemArr.push(me.influKpiName);
		
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
		itemArr.push(me.influProcessureName);
		
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
	
	addAssessComponent : function() {
		var me = this;

		me.assessfieldSet = Ext.create("FHD.view.risk.cmp.risk.RiskAssessMakePanel",{
			title:'评价信息'
		});
		
		return me.assessfieldSet;
	},
	addExtendComponent : function() {
		var me = this;

		// 是否合规
		var riskKind = Ext.create('FHD.ux.dict.DictCheckbox', {// DictCheckbox
			name : 'riskKind',
			dictTypeId : 'rm_class',
			labelAlign : 'left',
			fieldLabel : '是否合规',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		// 可控性
		var riskType = Ext.create('FHD.ux.dict.DictRadio', {
			name : 'riskType',
			dictTypeId : 'rm_type',
			labelAlign : 'left',
			fieldLabel : '可控性',
			margin : '7 30 3 30',
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

		// 序号
		var sort = Ext.widget('numberfield', {
			xtype : 'numberfield',
			fieldLabel : '序号',
			name : 'sort',
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

		//风险分类
		var hidden = true;
		if(me.type=='re'){
			hidden = false;
		}
		me.riskStructure = Ext.create('FHD.view.risk.cmp.RiskSelector', {
			onlyLeaf: true,
			title : '请您选择风险分类',
			fieldLabel : '风险分类',
			name : 'riskStructure',
			margin : '7 30 3 30',
			height : 40,
			multiSelect: true,
			hidden:hidden,
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
		
		//风险模板
		me.templateIdStore = Ext.create('Ext.data.Store', {
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
			},
			autoLoad:true
		});
		me.templateId = Ext.create('Ext.form.ComboBox', {
			name:'templateId',
			store : me.templateIdStore,
			displayField : 'name',
			valueField : 'type',
			labelAlign : 'left',
			fieldLabel : '风险模板',
			multiSelect : false,
			triggerAction : 'all',
			margin : '7 30 3 30',
			columnWidth : .5
		});

		var extendfieldSet = Ext.widget('fieldset', {
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			collapsible : true,
			collapsed:true,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '扩展信息',
			items : [riskKind, riskType, 
					sort,isUse,
					innerReason, outterReason, 
					responseStrategy, impactTime,
					me.riskStructure,fileUpload,me.templateId]
		});
		return extendfieldSet;
	},
	
	/**
	 * 方法
	 */
	save : function(callback) {
		var me = this;
		
		if(me.isEdit){	//统一保存入口，用于保存后再次点击保存修改
    		return me.merge(me.riskId,callback);
    	}
    	
		var form = me.getForm();
		//责任部门
		var respDeptName = me.respDeptName.getValue();
		//相关部门
		var relaDeptName = me.relaDeptName.getValue();
		// 影响指标
		var influKpiName = me.influKpiName.getFieldValue();
		// 影响流程
		var influProcessureName = me.influProcessureName.getValue();
		
		//评价内容字符串
		var assessValue = me.assessfieldSet.getValue();

		if (form.isValid() && me.customValidate()) {
	        me.body.mask("提交中...","x-mask-loading");
	        
			FHD.submit({
				form : form,
				url : __ctxPath + me.saveUrl,
				params : {
					isRiskClass : me.type, // 风险还是风险事件
					influKpiName : influKpiName,
					influProcessureName : influProcessureName,
					state:me.state,
					id : me.riskStorageId,
					archiveStatus:me.archiveStatus,
					assessValue:assessValue,
					schm : me.typeId
				},
				callback : function(data) {
	            	me.body.unmask();
					if(callback){
						callback(data,me.isEdit);
						me.riskStorageId = data.id;
					}
				}
			});
		}else{
			return false;
		}
	},
	merge : function(id,callback) {
		var me = this;
		
		var form = me.getForm();
		//责任部门
		var respDeptName = me.respDeptName.getValue();
		//相关部门
		var relaDeptName = me.relaDeptName.getValue();
		// 影响指标
		var influKpiName = me.influKpiName.getFieldValue();
		// 影响流程
		var influProcessureName = me.influProcessureName.getValue();
		//评价内容字符串
		var assessValue = me.assessfieldSet.getValue();
		
		if (form.isValid() && me.customValidate()) {
			FHD.submit({
				form : form,
				url : __ctxPath + me.mergeUrl,
				params : {
					id:id,
					influKpiName : influKpiName,
					influProcessureName : influProcessureName,
					state:me.state,
					archiveStatus:me.archiveStatus,
					assessValue:assessValue,
					schm : me.typeId
				},
				callback : function(data) {
					if(callback){
						callback(data,me.isEdit);
					}
				}
			});
		}else{
			return false;
		}
	},
	resetData: function (type,id,otherid) {	//type为左侧不同树的类型，根据类型初始化不同的数据项
        var me = this;
        me.isEdit = false;

		me.assessfieldSet.initParams(false);
    	//1.清空组件值
		me.getForm().reset();
		// 上級风险
		me.parentId.clearValues();
		if(id == 'root'){	//上级节点是根元素，文本框和按钮变灰
			me.parentId.grid.setDisabled(true);
			me.parentId.button.setDisabled(true);
		}else{
			me.parentId.grid.setDisabled(false);
			me.parentId.button.setDisabled(false);
		}
		//风险分类
		if(me.type=='re'){
    		me.riskStructure.clearValues();
		}
		// 责任部门
		me.respDeptName.clearValues();
		// 相关部门
		me.relaDeptName.clearValues();
		// 影响指标
		me.influKpiName.initGridStore(null);
		// 影响流程
		me.influProcessureName.initValue(null);

		//2.设置初始值
		me.getForm().setValues({
			isUse : '0yn_y'
		});

		//3.根据不同的类型，设置不同的初始值
		if(type == 'risk'){
			if(id=='root'){	//根节点不做赋值处理
				return;
			}
        	FHD.ajax({
       			async:false,
       			params: {
                    riskId: id
                },
                url: __ctxPath + '/risk/findRiskEditInfoById.f',
                callback: function (ret) {
                 	//上级风险
                	var parentId = [];
                	var obj = {};
                	obj["id"] = id;
                	parentId.push(obj);
            		me.parentId.setHiddenValue(parentId);
            		me.parentId.initValue();
            		//必须延迟一会，否则得到的store为空
            		setTimeout(function() {
                    	me.setRiskCode();//风险编号联动
                    },500);
                }
            });
		}else if(type == 'org'){
			// 责任部门
			var value = [];
        	var obj = {};
        	obj["deptid"] = id;
        	if(otherid != null && otherid != undefined && otherid != ''){
	        	obj["empid"] = otherid;
        	}else{
        		obj["empid"] = null;
        	}
        	value.push(obj);
        	me.respDeptName.setHiddenValue(value);
			me.respDeptName.initValue(Ext.encode(value));
		}else if(type == 'sm'){
			// 影响指标
		    var kpiArr = id.split('_');
		    if(kpiArr.length>1){
		    	var kpiId = kpiArr[1];
				me.influKpiName.initGridStore(kpiId);
		    }
		}else if(type == 'process'){
			// 影响流程
			me.influProcessureName.setValue(new Array(id));
		}else{
			alert('type参数传递错误！');
		}
    },
    reloadData: function (id) {	//id是风险事件id
    	var me = this;
    	me.isEdit = true;
    	me.riskId = id;
    	
    	//将变灰的数据项恢复过来
    	me.parentId.grid.setDisabled(false);
		me.parentId.button.setDisabled(false);
		
		//初始化评价信息
		me.assessfieldSet.initParams(true);
		me.assessfieldSet.reloadData(id);
		
		//初始化扩展信息
    	FHD.ajax({
   			async:false,
   			params: {
                riskId: id
            },
            cache: false,
            url: __ctxPath + me.findUrl,
            callback: function (json) {
            	//赋值
            	me.form.setValues({
        			parentId : json.parentId,
        			code : json.code,
        			name : json.name,
        			desc : json.desc,
//        			respDeptName : json.respDeptName,
//        			relaDeptName : json.relaDeptName,
        			influKpiName : json.influKpiName,
        			influProcessureName : json.influProcessureName,
        			riskKind : json.riskKind,
        			riskType : json.riskType,
        			isUse : json.isUse,
        			sort : json.sort,
        			innerReason : json.innerReason.split(','),
        			outterReason : json.outterReason.split(','),
        			impactTime : json.impactTime.split(','),
        			responseStrategy : json.responseStrategy.split(','),
        			riskStructure: json.riskStructure,
        			templateId:json.templateId
        		});

        		//上级风险
        		me.parentId.initValue();
        		//风险分类
        		if(me.type=='re'){
            		me.riskStructure.initValue();
        		}

        		//责任部门，可能没有sethidden值
        		me.respDeptName.initValue(json.respDeptName);
        		//相关部门
        		me.relaDeptName.initValue(json.relaDeptName);
        		//影响指标
        		me.influKpiName.initGridStore(json.influKpiName);
        		//影响流程
        		me.influProcessureName.initValue(json.influProcessureName);

                if(json.respDeptName){
                    var values = Ext.JSON.decode(json.respDeptName);
                    me.respDeptName.setValues(values);
                }
                if(json.relaDeptName){
                    var values = Ext.JSON.decode(json.relaDeptName);
                    me.relaDeptName.setValues(values);
                }

            }
        });
    },
	
	initComponent : function() {
		var me = this;

		// 基本信息
		var basicfieldSet = me.addBasicComponent();

		
		//评价信息
		me.assessfieldSet = me.addAssessComponent();
		
		// 扩展信息
		var extendfieldSet = me.addExtendComponent();
        
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [basicfieldSet,me.assessfieldSet,extendfieldSet]
		});

		me.callParent(arguments);

	}
});