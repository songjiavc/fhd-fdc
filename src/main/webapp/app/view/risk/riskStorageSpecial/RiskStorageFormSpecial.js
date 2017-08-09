/**
 * @author 郭鹏 2017-04-25
 * 
 */
Ext.define('FHD.view.risk.riskStorageSpecial.RiskStorageFormSpecial', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskstorageformspecial',

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
	saveUrl: '/cmp/risk/saveRiskStorageForSecurity.f',
	mergeUrl:'/cmp/risk/mergeRiskStorageForSecurity.f',
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
		me.respDeptName = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept_emp',
			fieldLabel: '责任部门/人'+ '<font color=red>*</font>',
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
                fieldLabel: '责任部门'+ '<font color=red>*</font>',
                margin: '7 30 3 30', 
                name: 'respDeptLabel',
                columnWidth: .5,
                value:deptname
            });
    		itemArr.push(me.respDeptLabel);
    	}

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

		me.assessfieldSet = Ext.create("FHD.view.risk.riskStorageSpecial.RiskAssessMakePanelSpecial",{
			title:'评估信息',
			riskId:me.riskId
		});
		return me.assessfieldSet;
	},
	addExtendComponent : function() {
		
		var me = this;
		
		var editIdea = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			name : 'respondComments',
			margin : '5 0 3 20',
			columnWidth : 1
		 });
		
		var respondfieldSet = Ext.widget('fieldset', {
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '100%',
			collapsible : true,
			collapsed : false,
			defaults : {
				margin : '3 30 3 30',
				labelWidth : 100
			},
			layout : {
				type : 'column'
			},
			title : '应对意见',
			items : [editIdea]
		});
		
		return respondfieldSet;
	},
	//返回
    backGrid: function(){
        var me = this;
        me.up('riskstoragecardnew').showRiskGrid();
    },
	/**
	 * 方法
	 */
	save : function(callback) {
		var me = this;
//		var assessff=me.assessfieldSet;
//		var saveinfo=assessff.getValue();
//		assessff.assessValue+
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
		//应对信息
		var respondValue= me.getForm().getValues().respondComments;;
		//评价内容字符串
		var assessValue = me.assessfieldSet.riskResultScore;
		//get templateid
		var templateId = me.assessfieldSet.assesstemplate.getValue();
		if (form.isValid() && me.customValidate()) {
	        
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
					respondValue:respondValue,
					assessValue:Ext.encode(assessValue),
					templateId : templateId,
					schm : "security"
				},
				callback : function(data) {
			 		 if(data.success){
			 		 
                        me.up('riskstoragecardnew').riskGrid.reloadData(null,'risk');
                        me.up('riskstoragecardnew').showRiskGrid();
                    }
                    	me.body.unmask();
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
		//评价内容字符串me.riskResultScore
		var assessValueEn = '';

		if(!me.assessfieldSet.assesstemplate.readOnly){
			var assessValue = me.assessfieldSet.riskResultScore;
			assessValueEn = Ext.encode(assessValue);
		}
		//应对信息
		var respondValue= me.getForm().getValues().respondComments;
		var templateId = me.assessfieldSet.assesstemplate.getValue();
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
					assessValue: assessValueEn,
					respondValue:respondValue,
					templateId : templateId,
					schm : me.typeId
				},
				callback : function(data) {
			  			if(data.success){
                        me.up('riskstoragecardnew').riskGrid.reloadData(null,'risk');
                        me.up('riskstoragecardnew').showRiskGrid();
                    }
				}
			});
		}else{
			return false;
		}
	},
	resetData: function (id) {	//type为左侧不同树的类型，根据类型初始化不同的数据项
        var me = this;
        type="risk";
		me.assessfieldSet.initParams(false);
    	//1.清空组件值
		me.getForm().reset();
		// 上級风险
		me.parentId.clearValues();
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
        			templateId:json.templateId
        		});

        		//上级风险
        		me.parentId.initValue();

        		//责任部门，可能没有sethidden值
        		me.respDeptName.initValue(json.respDeptName);
        		//相关部门
        		me.relaDeptName.initValue(json.relaDeptName);
        		//影响指标
        		me.influKpiName.initGridStore(json.influKpiName);
        		//影响流程
        		me.influProcessureName.initValue(json.influProcessureName);
        		if(null!=json.riskResponse){
        			me.extendfieldSet.down('textareafield').setValue(json.riskResponse.editIdeaContent);
				}else{
					me.extendfieldSet.down('textareafield').setValue('');
				}
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
		var assessfieldSet = me.addAssessComponent();
		
		// 扩展信息
		me.extendfieldSet = me.addExtendComponent();
           
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			dockedItems: [{
			xtype: 'toolbar',
	        dock: 'bottom',
	        ui: 'footer',
	        items: ['->', {
	           text: '返回',
	           iconCls: 'icon-control-repeat',
	           height : 40,
	           handler: function () {
	           me.backGrid();
	                }
	            },{
	                text: '保存',
	                iconCls: 'icon-database-save',
	                height : 40,
	                handler: function () {
	                     me.save();
	            }}]
            }],
			items : [basicfieldSet,assessfieldSet,me.extendfieldSet]
		});

		me.callParent(arguments);

	}
});