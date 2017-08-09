Ext.define('FHD.view.risk.hisevent.HistoryEventAddForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.historyeventaddform',
	requires : [
		'Ext.ux.Toast'
	],
	goback: function(){},
	currentId: '',
	type : 'risk',
	isAdd: true,
	archiveStatus : 'saved',
	saveUrl: '/historyevent/savehistoryinfo.f',
	findUrl: '/historyevent/findhistoryinfo.f',
	
	
	initComponent : function() {
		var me = this;

		// 关联信息
		me.addRelateComponent();
		
		// 基本信息
		me.addBasicComponent();
		
		var saveBtn = Ext.create('Ext.button.Button',{
            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"),//保存按钮
            iconCls: 'icon-control-stop-blue',
            handler: function () {
            	me.save();
            }
        });
        var returnBtn = Ext.create('Ext.button.Button',{
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
            iconCls: 'icon-operator-home',
            handler: function () {
    			me.goback();
            }
        });
        me.bbar = ['->',returnBtn,saveBtn];
		
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [me.relatefieldSet,me.basicfieldSet]
		});

		me.callParent(arguments);
	},
	
	
	save : function() {
		var me = this;
		if(!me.isAdd){
    		me.merge(me.currentId);
    		return;
    	}
		
		var form = me.getForm();

		if (form.isValid()) {
			var relationValue = '';
			if(me.type == 'risk'){
				relationValue = me.relationValue;
			}else if(me.type == 'dept'){
				if(me.relation.getValue() != ''){
					relationValue = Ext.JSON.decode(me.relation.getValue())[0].id;
				}
			}
			FHD.submit({
				form : form,
				url : __ctxPath + me.saveUrl,
				params : {
					type : 'risk',
					relationId: relationValue,
					isAdd : me.isAdd,
					schm:me.typeId,
					archiveStatus : me.archiveStatus
				},
				callback : function(data) {
					me.goback(data);
				}
			});
			return true;
		}else{
			return false;
		}
	},
	merge : function(id) {
		var me = this;
		
		var form = me.getForm();
		
		if (form.isValid()) {
			var relationValue = '';
			if(me.type == 'risk'){
				relationValue = me.relationValue;
			}else if(me.type == 'dept' || me.type == 'all'){
				if(me.relation.getValue() != ''){
					relationValue = Ext.JSON.decode(me.relation.getValue())[0].id;
				}
			}
			FHD.submit({
				form : form,
				url : __ctxPath + me.saveUrl,
				params : {
					id:id,
					type : 'risk',
					schm:me.typeId,
					relationId: relationValue,
					isAdd : me.isAdd
				},
				callback : function(data) {
					me.goback(data);
				}
			});
		}else{
			return false;
		}
	},
	
	//基础信息
	addBasicComponent : function() {
		var me = this;

		// 名称
		var hisname = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '事件名称' + '<font color=red>*</font>',
			margin : '7 30 3 30',
			name : 'hisname',
			maxLength : 255,
			labelWidth : 120,
			columnWidth : .5,
			allowBlank:false
		});
		
		// 编号
		var hiscode = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '编号',
			margin : '7 30 3 30',
			name : 'hiscode',
			maxLength : 255,
			labelWidth : 120,
			columnWidth : .4
		});
		//自动生成机构编号按钮
    	var autoButton = {
            xtype: 'button',
            margin: '7 10 10 10',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.autoCode'),
            handler: function(){
       			FHD.ajax({
	            	url:__ctxPath+'/standard/standardTree/createStandardCode.f',
	            	params: {
	                	nodeId: me.nodeId
                 	},
	                callback: function (data) {
	                 	me.getForm().setValues({'hiscode':data.code});//给code表单赋值
	                }
                });
            },
            columnWidth: .1
        };
		
		//发生日期
		var occurDateStr = Ext.widget('datefield',{
			fieldLabel : '发生日期',
		    name: 'occurDateStr',
		    format: 'Y-m-d',
		    margin : '7 30 3 30',
		    labelWidth : 120,
		    maxValue : new Date,
		    columnWidth : .5
		});
		
		// 损失金额
		var lostAmount = Ext.widget('numberfield', {
			xtype : 'numberfield',
			fieldLabel : '损失金额（万元）',
			name : 'lostAmount',
			labelWidth : 120,
			margin : '7 30 3 30',
			decimalPrecision : 2,
			columnWidth : .5
		});
		
		// 处理状态
		var dealStatusDict = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
            editable: false,
            multiSelect: false,
            name: 'dealStatusDict',
            dictTypeId: '0deal_status',
            fieldLabel: '处理状态', 
            columnWidth: .5,
            labelWidth : 120,
            margin : '7 30 3 30',
            labelAlign: 'left'
        });
		
		// 事件等级
		var eventLevelDict = Ext.create('FHD.ux.dict.DictSelectForEditGrid', {
            editable: false,
            multiSelect: false,
            name: 'eventLevelDict',
            dictTypeId: 'ic_processure_importance',
            fieldLabel: '事件等级', 
            columnWidth: .5,
            margin : '7 30 3 30',
            labelWidth : 120,
            labelAlign: 'left'
        });
        
        // 责任部门
        /*me.eventOccuredOrgStr = Ext.create('FHD.ux.org.CommonSelector', {
			fieldLabel : '责任部门',
			labelAlign : 'left',
			type : 'dept',
			subCompany : true,
			multiSelect : false,
			margin : '7 30 3 30',
			name : 'eventOccuredOrgStr',
			labelWidth : 115,
			columnWidth : .5
		});*/
		
		me.eventOccuredOrgStr = Ext.create('Ext.ux.form.OrgEmpSelect',{
        	multiSelect: false,
			type: 'dept',
			fieldLabel:  '责任部门', // 所属部门人员
			labelAlign: 'left',
			labelWidth: 120,
			columnWidth: .5,
			margin : '7 30 3 30',
			store: [],
			queryMode: 'local',
			forceSelection: false,
			createNewOnEnter: true,
			createNewOnBlur: true,
			filterPickList: true,
			name: 'eventOccuredOrgStr',
			value:''
		});
		
		// 责任认定
		var eventOccuredObject = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '责任认定',
			margin : '7 30 3 30',
			name : 'eventOccuredObject',
			labelWidth : 120,
			height : 80,
			columnWidth : .5
		});
        
		// 影响
		var effect = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '影响',
			margin : '7 30 3 30',
			name : 'effect',
			labelWidth : 120,
			height : 80,
			columnWidth : .5
		});
		
		// 发生地点
		var occurePlace = Ext.widget('textfield', {
			xtype : 'textfield',
			fieldLabel : '发生地点',
			margin : '7 30 3 30',
			name : 'occurePlace',
			labelWidth : 120,
			maxLength : 255,
			columnWidth : .5
		});
		
		//  备注
		var comment = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '备注',
			margin : '7 30 3 30',
			name : 'comment',
			labelWidth : 120,
			height : 80,
			columnWidth : .5
		});
		
		//  描述
		var hisdesc = Ext.widget('textareafield', {
			xtype : 'textareafield',
			rows : 4,
			fieldLabel : '描述',
			margin : '7 30 3 30',
			name : 'hisdesc',
			labelWidth : 120,
			height : 80,
			columnWidth : .5
		});

		me.basicfieldSet = Ext.widget('fieldset', {
			title : '历史事件',
			xtype : 'fieldset', // 基本信息fieldset
			autoHeight : true,
			autoWidth : true,
			width : '99%',
			margin : '10 20 10 10',
			collapsible : true,
			layout : {
				type : 'column'
			},
			items : [hisname,hiscode,autoButton,
						eventLevelDict,dealStatusDict,
						occurDateStr,occurePlace,
						lostAmount,me.eventOccuredOrgStr,
						hisdesc,eventOccuredObject,
						effect,comment
					]
		});
	},
	
	//关联信息
	addRelateComponent : function() {
		var me = this;
		// 风险事件
		if(me.type == 'risk'){
			me.relatefieldSet = Ext.create('FHD.view.risk.cmp.form.RiskFullFormDetail', {
				margin : '0 0 0 0',
	            border: false,
	            showbar: false
	        });
		}else if(me.type == 'dept' || me.type == 'all'){
			me.relation = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelector', {
				fieldLabel : '风险',
				multiSelect: false,
				riskmyfoldertreevisable : true,
				labelAlign : 'left',
				height: 23,
				name : 'relation',
				labelWidth : 120,
				margin : '7 30 3 30',
				typeId:me.typeId,//分库标志
				columnWidth : 1
			});
			me.relatefieldSet = Ext.widget('fieldset', {
				title : '风险信息',
				xtype : 'fieldset', // 基本信息fieldset
				autoHeight : true,
				autoWidth : true,
				width : '99%',
				collapsible : true,
				layout : {
					type : 'fit'
				},
				items : [me.relation
						]
			});
		}
		
		if(me.type == 'risk'){
			me.relatefieldSet.basicfieldSet.collapse();
		}
	},
	
	resetData : function(relationId) {
		var me = this;
		me.currentId = '';
		me.isAdd = true;
		me.eventOccuredOrgStr.clearValues();
		me.getForm().reset();
		// 责任部门
		var valuedept = [];
    	//var objdept = {};
    	//objdept["id"] = __user.majorDeptId;
    	var objdept = {id:__user.majorDeptId,deptno:__user.majorDeptNo,deptname:__user.majorDeptName};
    	valuedept.push(objdept);
    	
    	//me.eventOccuredOrgStr.setHiddenValue(valuedept);
    	me.eventOccuredOrgStr.setHideValue(valuedept);
		//me.eventOccuredOrgStr.initValue(Ext.encode(valuedept));
		me.eventOccuredOrgStr.setValues(valuedept);
		if(me.type == 'risk'){
			// 风险
			me.relatefieldSet.reloadData(relationId);
			me.relationValue = relationId;
		}else if(me.type == 'dept' || me.type == 'all'){
			me.relation.clearValues();
		}
		
	},
    reloadData: function (id) {
    	var me = this;
    	me.isAdd = false;
    	me.currentId = id;
    	
    	FHD.ajax({
   			async:false,
   			params: {
                id: me.currentId
            },
            url: __ctxPath + me.findUrl,
            callback: function (json) {
            	//赋值
            	me.form.setValues({
            		relation: json.data.relation,
            		hisname: json.data.hisname,
            		hiscode: json.data.hiscode,
            		occurDateStr: json.data.occurDateStr,
            		lostAmount: json.data.lostAmount,
            		dealStatusDict: json.data.dealStatusDict,
            		eventLevelDict: json.data.eventLevelDict,
            		eventOccuredOrgStr: json.data.eventOccuredOrgStr,
            		eventOccuredObject: json.data.eventOccuredObject,
            		hisdesc: json.data.hisdesc,
            		effect: json.data.effect,
            		occurePlace: json.data.occurePlace,
            		comment: json.data.comment
            	});
            	//me.eventOccuredOrgStr.initValue(json.data.eventOccuredOrgStr);
            	if(json.data.eventOccuredOrgStr){
            		var value = Ext.JSON.decode(json.data.eventOccuredOrgStr);
            		me.eventOccuredOrgStr.setValues(value);
            	}
            	if(me.type == 'risk'){
            		me.relatefieldSet.reloadData(Ext.JSON.decode(json.data.relation)[0].id);
            	}else if(me.type == 'dept' || me.type == 'all'){
            		if(json.data.relation != '' && json.data.relation != null && json.data.relation != undefined){
		            	me.relation.initValue(json.data.relation);
            		}else{
            			me.relation.clearValues();
            		}
            	}
            }
    	});
    	
    },
    
    initParams: function(){
    	var me = this;
    }
	
});