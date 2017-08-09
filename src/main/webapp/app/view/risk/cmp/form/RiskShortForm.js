/**
 * @author zhengjunxiang
 * 最简单表单，用于风险定义，同时也提供给内控使用
 * 1. 封装是否显示toobbar
 * 调用地方：
 * 内控体系更新工作流（流程和矩阵编写）reset()传递为null
 */
Ext.define('FHD.view.risk.cmp.form.RiskShortForm', {
	extend : 'FHD.view.risk.cmp.form.RiskBasicForm',
	alias : 'widget.riskshortform',
	
	layout : 'fit',
	/**
	 * 常量
	 */
	saveUrl: '/cmp/risk/saveRiskDefineInfo',
	mergeUrl:'/cmp/risk/mergeRiskInfo',
	findUrl: '/cmp/risk/findRiskEditInfoById',
	isInherit:'0yn_y',	//是否继承

	/**
	 * 变量
	 */
	archiveStatus:'archived',	//归档状态 archived
	
	/**
	 * 变量
	 */
	showbar:false,	//是否显示保存，返回工具条
	
	/**
	 * 方法
	 */
	save : function(callback) {
		var me = this;
		
		if(me.isEdit){	//容错处理，防止别人调用错误
    		return me.merge(me.riskId,callback);
    	}
		
		var form = me.getForm();
		//责任部门
		var respDeptName = me.respDeptName.getValue();
		//相关部门
		var relaDeptName = me.relaDeptName.getValue();

		if (form.isValid()  && me.customValidate()) {
			FHD.submit({	//callback返回json对象
				form : form,
				url : __ctxPath + me.saveUrl,
				params : {
					isRiskClass : me.type, //风险还是风险事件
					state:me.state,		   //状态
					archiveStatus:me.archiveStatus
				},
				callback : function(data) {
					if(callback){
						callback(data);
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
	
		if (form.isValid() && me.customValidate()) {
			FHD.submit({
				form : form,
				url : __ctxPath + me.mergeUrl,
				params : {
					id:id,
					isRiskClass : me.type, // 风险还是风险事件
					state:me.state,
					archiveStatus:me.archiveStatus
				},
				callback : function(data) {
					if(callback){
						callback(data);
					}
				}
			});
		}else{
			return false;
		}
	},
	resetData : function(id) {	//id为树节点id
		var me = this;
        me.isEdit = false;
		
        //1.清空组件值
		me.getForm().reset();
		// 责任部门
		me.respDeptName.clearValues();
		// 相关部门
		me.relaDeptName.clearValues();
		//2.设置上级节点继承的初始值
		if(id){	//做组件必须验证传入参数的合法性
			FHD.ajax({
	   			async:false,
	   			params: {
	                riskId: id
	            },
	            url: __ctxPath + me.findUrl,
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
		}
	},
    reloadData: function (id) {	//id是风险事件id
    	var me = this;
    	me.isEdit = true;
    	me.riskId = id;
    	
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
        			code : json.code,
        			name : json.name,
        			desc : json.desc,
//        			respDeptName : json.respDeptName,
//        			relaDeptName : json.relaDeptName,
        			influKpiName : json.influKpiName,
        			influProcessureName : json.influProcessureName
        		});

        		//上级风险
        		me.parentId.initValue();
        		//责任部门，相关部门
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
    
    /**
     * 设置责任部门的值
     */
    setDutyDepartmentValue:function(deptId,empid){
    	var me =this;

		var value = [];
    	var obj = {};
    	obj["deptid"] = deptId;
    	obj["empid"] = empid;
    	value.push(obj);
    	me.respDeptName.setHiddenValue(value);
		me.respDeptName.initValue(Ext.encode(value));
    },
	
     /**
     * 保存和返回的回调函数
     */
    goback:Ext.emptyFn(),
    
	initComponent : function() {
		var me = this;

		//基本信息
		var basicfieldSet = me.addBasicComponent();
		
		if(me.showbar){
			me.tbar = [{width:0,height:20}];
			
			var saveBtn = Ext.create('Ext.button.Button',{
	            text: FHD.locale.get("fhd.strategymap.strategymapmgr.form.save"),//保存按钮
	            iconCls: 'icon-control-stop-blue',
	            handler: function () {
	            	var i = this;
	            	i.setDisabled(true);
	            	me.body.mask("提交中...","x-mask-loading");
	            	me.save(function(data){
	            		i.setDisabled(false);
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
			}else{
		        me.bbar = ['->',saveBtn];
			}
		}

		Ext.applyIf(me, {
			autoScroll : true,
			border : false,
			items : [basicfieldSet]
		});

		me.callParent(arguments);
	}	
});