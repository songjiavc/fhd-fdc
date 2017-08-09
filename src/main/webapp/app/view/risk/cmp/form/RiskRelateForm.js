Ext.define('FHD.view.risk.cmp.form.RiskRelateForm', {
	extend : 'FHD.view.risk.cmp.form.RiskBasicForm',
	alias : 'widget.riskrelateform',
	//待添加
	
	/**
	 * 常量
	 */
	saveUrl: '/risk/relate/saveRiskInfo',
	mergeUrl:'/risk/relate/mergeRiskInfo',
	findUrl: '/cmp/risk/findRiskEditInfoById',
	isInherit:'0yn_y',	//是否继承
	
	/**
	 * 变量
	 */
	archiveStatus:'archived',	//归档状态 archived
	showSubmitBtn:false,		//显示提交按钮,只在部门风险添加和修改的时候使用
	
	/**
	 * 变量
	 */
	showbar:false,	//是否显示保存，返回工具条
	
	/**
	 * 方法
	 */
	save : function(callback) {
		var me = this;
		
		if(me.isEdit){
    		return me.merge(me.riskId,callback);
    	}
		
		var form = me.getForm();
		//责任部门
		var respDeptName = me.respDeptName.getValue();
		//相关部门
		var relaDeptName = me.relaDeptName.getValue();
		/*
		// 影响指标
		var influKpiName = me.influKpiName.getFieldValue();
		// 影响流程
		var influProcessureName = me.influProcessureName.getValue();
		*/
		if (form.isValid() && me.customValidate()) {
			FHD.submit({
				form : form,
				url : __ctxPath + me.saveUrl,
				params : {
					isRiskClass : me.type, // 风险还是风险事件
					state:me.state,
					archiveStatus:me.archiveStatus,
					//influKpiName : influKpiName,
					//influProcessureName : influProcessureName,
					schm:me.schm,
					parentId: me.parentRisk.getValue(),
					assessPlanId : me.assessPlanId,
					executionId: me.executionId,
		        	_type: me._type
				},
				callback : function(data) {
					if(callback){
						callback(data,me.isEdit);
					}
				}
			});
		}else{
			me.body.unmask();
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
		/*
		// 影响指标
		var influKpiName = me.influKpiName.getFieldValue();
		// 影响流程
		var influProcessureName = me.influProcessureName.getValue();
		*/
		if (form.isValid() && me.customValidate()) {
			FHD.submit({
				form : form,
				url : __ctxPath + me.mergeUrl,
				params : {
					id:id,
					isRiskClass : me.type, // 风险还是风险事件
					state:me.state,
					archiveStatus:me.archiveStatus,
					//influProcessureName : influProcessureName,
					//influKpiName : influKpiName,
					scoreObjectId :me.scoreObjectId,
					riskId:me.riskId,
					assessPlanId:me.assessPlanId,
					schm:me.schm
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
	resetData: function (type,id,empid) {	//id为树节点id
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
		// 责任部门
		me.respDeptName.clearValues();
		// 相关部门
		me.relaDeptName.clearValues();
		// 影响指标
		me.influKpiName.initGridStore(null);
		// 影响流程
		me.influProcessureName.initValue(null);
		
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
        	obj["empid"] = null;
        	value.push(obj);
        	me.respDeptName.setHiddenValue(value);
			me.respDeptName.initValue(Ext.encode(value));
		}else if(type == 'myfolder'){
			// 责任部门
			var value = [];
        	var obj = {};
        	obj["deptid"] = id;
        	obj["empid"] = empid;
        	value.push(obj);
        	me.respDeptName.setHiddenValue(value);
			me.respDeptName.initValue(Ext.encode(value));
		}else{
			alert('type参数传递错误！');
		}
    },
    reloadData: function (id) {	//id是风险事件id
    	var me = this;
    	me.isEdit = true;
    	
    	//将变灰的数据项恢复过来
    	me.parentId.grid.setDisabled(false);
		me.parentId.button.setDisabled(false);
		
    	FHD.ajax({
   			async:false,
   			params: {
                objectId : id
            },
            url: __ctxPath + me.findUrl,
            callback: function (json) {
            	//赋值
            	me.form.setValues({
        			parentId : json.parentId,
        			code : json.code,
        			name : json.name,
        			desc : json.desc,
//        			respDeptName : json.respDeptName,
        			//relaDeptName : json.relaDeptName,
        			
        			respDeptName : json.respDeptName,
        			relaDeptName : json.relaDeptName,
        			
        			influProcessureName : json.influProcessureName,
        			//controlProcessureName : json.controlProcessureName,
        			templateId : json.templateId
        		});

        		//上级风险
        		me.parentId.initValue();
        		//责任部门，可能没有sethidden值
        		
        		if(json.respDeptName){
        			var value = Ext.JSON.decode(json.respDeptName);
	        		me.respDeptName.setValues(value);
	        		
        		}
        		//相关部门
        		if(json.relaDeptName){
        			var value = Ext.JSON.decode(json.relaDeptName);
	        		me.relaDeptName.setValues(value);
	        		
        		}
        		
//        		me.respDeptName.initValue(json.respDeptName);
//        		
//        		me.relaDeptName.initValue(json.relaDeptName);
        		//影响指标
        		me.influKpiName.initGridStore(json.influKpiName);
        		//影响流程
        		me.influProcessureName.initValue(json.influProcessureName);
            }
        });
		
    },
    
     /**
     * 保存和返回的回调函数
     */
    goback:Ext.emptyFn(),
    
	initComponent : function() {
		var me = this;
		// 基本信息
		var basicfieldSet =  me.addBasicComponent();
		
//		//关联信息
//		var relafieldSet = me.addRelaComponent();
//		me.relafieldSet = relafieldSet;
		
		if(me.showbar){
			me.tbar = [{width:0,height:20}];
			
			var saveBtn = Ext.create('Ext.button.Button',{
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"),//保存按钮
	            iconCls: 'icon-control-stop-blue',
	            handler: function () {
	            	var i = this;
	            	//i.setDisabled(true);
	            	me.body.mask("提交中...","x-mask-loading");
	            	me.save(function(data){
	            		//i.setDisabled(false);
	            		me.body.unmask();
	            		if(me.goback){
	            			me.goback(data);
	            		}
	            	});
	            }
	        });
	        if(me.type=='re'){	//风险事件显示保存和返回按钮
		        var returnBtn = Ext.create('Ext.button.Button',{
		            text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),//返回按钮
		            iconCls: 'icon-arrow-undo',
		            handler: function () {
		            	if(me.goback){
		        			me.goback();
		        		}
		            }
		        });
		        me.bbar = ['->',returnBtn,saveBtn];
		        
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
		        me.bbar = ['->',saveBtn];
			}
		}
		
		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [basicfieldSet]//,relafieldSet
		});

		me.callParent(arguments);
	}
});