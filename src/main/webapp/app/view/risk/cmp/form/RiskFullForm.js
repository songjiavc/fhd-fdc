/**
 * @author zhengjunxiang
 * 风险维护的表单，字段最全
 * 表单应用于风险分类导航，组织导航，目标导航，流程导航；所以reset方法传递了type区分
 * 将保存和返回按钮封装进去，供风险维护使用，默认有
 * 归档状态：风险库添加直接是archived,部门风险和指标风险添加的saved
 */
Ext.define('FHD.view.risk.cmp.form.RiskFullForm', {
	extend : 'FHD.view.risk.cmp.form.RiskBasicForm',
	alias : 'widget.riskfullform',

	/**
	 * 常量
	 */
	saveUrl: '/cmp/risk/saveRiskInfo',
	mergeUrl:'/cmp/risk/mergeRiskInfo',
	findUrl: '/cmp/risk/findRiskEditInfoById',
	findTemplateUrl: '/access/formulateplan/findTemplatesrisk.f',
	isInherit:'0yn_y',	//是否继承
	
	/**
	 * 变量
	 */
	riskId:undefined,			//编辑时风险id
	archiveStatus:'archived',	//归档状态
	showSubmitBtn:false,		//显示提交按钮,只在部门风险添加和修改的时候使用
	
	
	showbar:true,	//是否显示保存，返回工具条
	
	/**
     * 返回按钮的操作函数,添加完成后自动调用这个方法。
     */
    goback:Ext.emptyFn(),
    
	/**
	 * 方法
	 */
	save : function(btn,callback) {
		var me = this;
		
		if(me.isEdit){	//统一保存入口，用于保存后再次点击保存修改
    		return me.merge(me.riskId,callback);
    	}
    	
		var form = me.getForm();
		//责任部门
		var respDeptName = me.respDeptName.getValue();
		//相关部门
		var relaDeptName = me.relaDeptName.getValue();
		//风险指标
		var riskKpiName = me.riskKpiName.getFieldValue();
		// 影响指标
		var influKpiName = me.influKpiName.getFieldValue();
		// 控制流程
		var controlProcessureName = me.controlProcessureName.getValue();
		// 影响流程
		var influProcessureName = me.influProcessureName.getValue();
		// 公式定义
		var formulaDefine = me.formulaDefine.getValue();
		//采集频率相关字符串
		var valueDictType = me.gatherfrequenceDict.valueDictType;//采集频率
        var gatherValueCron = me.gatherfrequenceDict.valueCron;//时间采集公式
		var valueDictValue = me.gatherfrequenceDict.getValue();;//采集频率显示内容
        var valueRadioType = me.gatherfrequenceDict.valueRadioType;//显示内容后缀
        var gatherfrequenceDict = '';
        if(valueDictValue != ''){
	        gatherfrequenceDict = valueDictType + '|' + gatherValueCron + '|' + valueDictValue + '|' +  valueRadioType;
        }

		if (form.isValid() && me.customValidate()) {
			btn.setDisabled(true);
	        me.body.mask("提交中...","x-mask-loading");
	        
			FHD.submit({
				form : form,
				url : __ctxPath + me.saveUrl,
				params : {
					isRiskClass : me.type, // 风险还是风险事件
					riskKpiName : riskKpiName,
					influKpiName : influKpiName,
					controlProcessureName : controlProcessureName,
					influProcessureName : influProcessureName,
					formulaDefine : me.formulaDefine.getValue(),
					gatherfrequenceDict : gatherfrequenceDict,
					state:me.state,
					archiveStatus:me.archiveStatus
				},
				callback : function(data) {
					btn.setDisabled(false);
	            	me.body.unmask();
					if(callback){
						callback(data,me.isEdit);
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
		//风险指标
		var riskKpiName = me.riskKpiName.getFieldValue();
		// 影响指标
		var influKpiName = me.influKpiName.getFieldValue();
		// 控制流程
		var controlProcessureName = me.controlProcessureName.getValue();
		// 影响流程
		var influProcessureName = me.influProcessureName.getValue();
		// 公式定义
		var formulaDefine = me.formulaDefine.getValue();
		
		//采集频率相关字符串
		var valueDictType = me.gatherfrequenceDict.valueDictType;//采集频率
        var gatherValueCron = me.gatherfrequenceDict.valueCron;//时间采集公式
		var valueDictValue = me.gatherfrequenceDict.getValue();;//采集频率显示内容
        var valueRadioType = me.gatherfrequenceDict.valueRadioType;//显示内容后缀
        var gatherfrequenceDict = '';
        if(valueDictValue != ''){
	        gatherfrequenceDict = valueDictType + '|' + gatherValueCron + '|' + valueDictValue + '|' +  valueRadioType;
        }

		if (form.isValid() && me.customValidate()) {
			FHD.submit({
				form : form,
				url : __ctxPath + me.mergeUrl,
				params : {
					id:id,
					isRiskClass : me.type, // 风险还是风险事件
					riskKpiName : riskKpiName,
					influKpiName : influKpiName,
					controlProcessureName : controlProcessureName,
					influProcessureName : influProcessureName,
					formulaDefine : me.formulaDefine.getValue(),
					gatherfrequenceDict : gatherfrequenceDict,
					state:me.state,
					archiveStatus:me.archiveStatus
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
	resetData: function (type,id) {	//type为左侧不同树的类型，根据类型初始化不同的数据项
        var me = this;
        me.isEdit = false;
 
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
		// 风险指标
		me.riskKpiName.initGridStore(null);
		// 影响流程
		me.controlProcessureName.initValue(null);
		// 控制流程
		me.influProcessureName.initValue(null);
		// 风险动因
		me.riskReason.clearValues();
		// 风险影响
		me.riskInfluence.clearValues();
		// 采集频率
		me.gatherfrequenceDict.reset();
		me.gatherfrequenceDict.setValue('');

		//2.设置初始值
		me.getForm().setValues({
			riskKind : 'company',	//最好利用监听器默认选择第一个，不要写死key值
			riskType : 'threat',
			isFix : '0yn_n',
			isUse : '0yn_y',
			calcStr : '0yn_n',
			isAnswer : '0yn_y'
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
            		
            		//继承上级节点的值
            		me.getForm().setValues({
            			isInherit : me.isInherit,
    					templateId : ret.templateId,
    					formulaDefine : ret.formulaDefine
            		});
                }
            });
		}else if(type == 'org'){
			// 责任部门
			var value = [];
        	var obj = {};
        	obj["deptid"] = id;
        	obj["empid"] = null;
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
		
    	FHD.ajax({
   			async:false,
   			params: {
                riskId: id
            },
            url: __ctxPath + me.findUrl,
            callback: function (json) {
            	//赋值
            	me.form.setValues({
        			parentId : json.parentId,
        			riskStructure: json.riskStructure,
        			riskReason : json.riskReason,
        			riskInfluence : json.riskInfluence,
        			innerReason : json.innerReason.split(','),
        			outterReason : json.outterReason.split(','),
        			code : json.code,
        			name : json.name,
        			desc : json.desc,
        			riskKind : json.riskKind,
        			riskType : json.riskType,
        			isFix : json.isFix,
        			isUse : json.isUse,
        			isAnswer : json.isAnswer,
        			sort : json.sort,
        			respDeptName : json.respDeptName,
        			relaDeptName : json.relaDeptName,
        			controlProcessureName : json.controlProcessureName,
        			influProcessureName : json.influProcessureName,
        			isInherit : json.isInherit,
        			templateId : json.templateId,
        			formulaDefine : json.formulaDefine, //计算公式
        			relePlate : json.relePlate.split(','),
        			valueChain : json.valueChain.split(','),
        			impactTime : json.impactTime.split(','),
        			responseStrategy : json.responseStrategy.split(','),
        			resultCollectInterval:json.resultCollectInterval,	//采集频率
        			calcStr:json.calcStr	//是否计算
        		});

        		//上级风险
        		me.parentId.initValue();
        		//风险分类
        		if(me.type=='re'){
            		me.riskStructure.initValue();
        		}
        		//风险动因和风险影响
        		me.riskReason.initValue();
        		me.riskInfluence.initValue();

        		//责任部门，可能没有sethidden值
        		me.respDeptName.initValue(json.respDeptName);
        		//相关部门
        		me.relaDeptName.initValue(json.relaDeptName);
        		//影响指标
        		me.influKpiName.initGridStore(json.influKpiName);
        		//风险指标
        		me.riskKpiName.initGridStore(json.riskKpiName);
        		//影响流程
        		me.controlProcessureName.initValue(json.controlProcessureName);
        		//控制流程
        		me.influProcessureName.initValue(json.influProcessureName);
        		
        		if (json.gatherfrequenceDictType) {
                    me.gatherfrequenceDict.valueDictType = json.gatherfrequenceDictType;
                }else{
                	me.gatherfrequenceDict.valueDictType = '';
                }
        		if (json.gatherfrequenceCron) {
                    me.gatherfrequenceDict.valueCron = json.gatherfrequenceCron;
                }else{
                	me.gatherfrequenceDict.valueCron = '';
                }
                if (json.gatherfrequence) {
                    me.gatherfrequenceDict.setValue(json.gatherfrequence);
                }else{
                	me.gatherfrequenceDict.setValue('');
                }
                if (json.gatherfrequenceRule) {
                    me.gatherfrequenceDict.valueRadioType = json.gatherfrequenceRule;
                }else{
                	me.gatherfrequenceDict.valueRadioType = '';
                }
            }
        });
    },

	initComponent : function() {
		var me = this;

		// 基本信息
		var basicfieldSet = me.addBasicComponent();
		if(me.type=='re'){	//根据是风险添加还是风险事件添加，风险事件添加，只能选择叶子节点
			//风险分类
			me.riskStructure = Ext.create('FHD.view.risk.cmp.RiskSelector', {
				onlyLeaf: true,
				title : '请您选择风险分类',
				fieldLabel : '风险分类',
				name : 'riskStructure',
				height : 40,
				multiSelect: true,
				columnWidth : .5
			});
			basicfieldSet.add(me.riskStructure);
		}
		
		//关联信息
		var relafieldSet = me.addRelaComponent();
		// 扩展信息
		var extendfieldSet = me.addExtendComponent();

		if(me.showbar){
			if(me.type=='re'){	//风险事件显示保存和返回按钮
				var saveBtn = Ext.create('Ext.button.Button',{
		            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"),//保存按钮
		            iconCls: 'icon-control-stop-blue',
		            handler: function () {
		            	me.save(this,function(data,editFlag){
		            		if(me.goback){
		            			me.goback(data,editFlag);
		            		}
		            	});
		            }
		        });
		        var returnBtn = Ext.create('Ext.button.Button',{
		            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
		            iconCls: 'icon-operator-home',
		            handler: function () {
		            	if(me.goback){
		        			me.goback();
		        		}
		            }
		        });
		        me.tbar = [{width:0,height:20}];
		        //编辑权限，暂时不考虑添加权限
		        if($ifAnyGranted('ROLE_ALL_RISK_EDIT')){
		        	me.bbar = ['->',returnBtn,saveBtn];
		        }else{
		        	me.bbar = ['->',returnBtn];
		        }
		        
		        if(me.showSubmitBtn){
		        	var submitBtn = Ext.create('Ext.button.Button',{
		        		text: '提交',//提交按钮
			            iconCls: 'icon-control-stop-blue',
			            handler: function () {
			            	me.archiveStatus = 'submited';
			            	me.save(this,function(data,editFlag){
			            		if(me.goback){
			            			me.goback(data,editFlag);
			            		}
			            	});
			            }
			        });
		        	me.bbar.push(submitBtn);
		        }
			}else{
				var saveBtn = Ext.create('Ext.button.Button',{
		            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"),//保存按钮
		            iconCls: 'icon-control-stop-blue',
		            handler: function () {
		            	me.save(this,function(data){
		            		if(me.goback){
		            			me.goback(data,me.isEdit);
		            		}
		            	});
		            }
		        });
		        me.tbar = [{width:0,height:20}];
		        //编辑权限，暂时不考虑添加权限
		        if($ifAnyGranted('ROLE_ALL_RISK_EDIT')){
		        	me.bbar = ['->',saveBtn];
		        }else{
		        	me.bbar = [];
		        }
			}
		
		}
		

        
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [basicfieldSet,relafieldSet,extendfieldSet]
		});

		me.callParent(arguments);

	}
});