/**
 * 
 * 风险辨识主面板
 */
Ext.define('FHD.view.risk.riskidentify.identify.IdentifyMain', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.identifyMain',
	requires : [ 
	],

	reloadData : function() {

	},
	//获得导航item
	getItems: function(){
		var me = this;
		FHD.ajax({
            url: __ctxPath + '/access/riskidentify/findriskidentifydescription.f',
            params : {
            	executionId: me.executionId
            },
            async: false,
            callback: function (data) {
            	var items;
                me.description = data;
                if('complex'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划主管审批',status:'done'},
					    		{index: 3, context:'3.计划领导审批',status:'done'},
					    		{index: 4, context:'4.任务分配',status:'done'},
					    		{index: 5, context:'5.风险辨识',status:'current'},
					    		{index: 6, context:'6.辨识汇总',status:'undo'},
					    		{index: 7, context:'7.单位主管审批',status:'undo'},
					    		{index: 8, context:'8.单位领导审批',status:'undo'},
					    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
					    		{index: 10, context:'10.结果整理',status:'undo'},
					    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
					    		{index: 12, context:'12.风险部门领导审批',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }else{
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划审批',status:'done'},
					    		{index: 3, context:'3.任务分配',status:'done'},
					    		{index: 4, context:'4.风险辨识',status:'current'},
					    		{index: 5, context:'5.辨识汇总',status:'undo'},
					    		{index: 6, context:'6.结果整理',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }
                return items;
            }
        });
	},
	//添加风险
	addRisk: function(){
		var me = this;
		me.addAllShortForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateForm', {
			type:'re',	//如果是re,上级风险只能选择叶子节点
			schm:me.schm,
			border:false,
			state : '2',
			setLoginDept : true,
			executionId : me.executionId,
			assessPlanId : me.businessId,
			hiddenSaveBtn:true,
			userValidate : function(){
				return true;
			},
			callback:function(data){
					if(data){
	            		me.addAllShortForm.body.unmask();
		            	Ext.MessageBox.alert('添加信息','添加成功');
		            	me.formwindow.close();
		            	me.identifyGrid.reloadData(me.executionId);
	            	}
				/*if(data.id != null){
					var parentId =  
						me.addAllShortForm.parentId.getValue().split(':')[1].replace('}]', "").replace('"', '').replace('"', '')
					FHD.ajax({
			            url: __ctxPath + '/access/riskidentify/savetaskbysome.f',
			            params: {
			            	parentId : parentId,
			            	assessPlanId : me.businessId,
			            	riskId : data.id,
			            	executionId: me.executionId
			            },
			            callback: function (data) {
			            	if(data){
			            		me.addAllShortForm.body.unmask();
				            	Ext.MessageBox.alert('添加信息','添加成功');
				            	me.formwindow.close();
				            	me.identifyGrid.reloadData(me.executionId);
			            	}
			            }
			        });
				}*/
			}
		});
		//设置应对信息不可编辑
		me.addAllShortForm.getForm().findField('responseText').setDisabled(true);
		
    	me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			width:900,
			height:400,
			title : '风险信息添加',
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : [me.addAllShortForm],
			buttons: [
				{
					text: '保存',
					handler:function(){
						var isAdd = me.addAllShortForm.save(me.addAllShortForm.callback);
						if(isAdd){
							me.addAllShortForm.body.mask("保存中...","x-mask-loading");
						}
					}
				},
    			{
    				text: '关闭',
    				handler:function(){
    					me.formwindow.close();
    				}
    			}
    		]
		});
		me.formwindow.show();
	},
    //提交
	submit: function(){
		var me = this;
		Ext.MessageBox.show({
    		title : '提示',
    		width : 260,
    		msg : '确认提交吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认删除
					me.body.mask("提交中...","x-mask-loading");
					FHD.ajax({
			            url: __ctxPath + '/access/riskidentify/submitidentitiassess.f',
			            params: {
			            	executionId : me.executionId,
			            	assessPlanId : me.businessId
			            },
			            callback: function (data) {
		            		me.body.unmask();
		            		Ext.getCmp(me.winId).close();
			            }
			        });
    			}
    		}
    	});
	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		me.getItems();
		me.flowtaskbar = Ext.widget('panel',{
			border:false,
			//region: 'north',
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
    	
    	me.identifyGrid = Ext.create('FHD.view.risk.riskidentify.identify.IdentifyGrid', {
    		//region: 'center',
    		flex: 1,
			executionId : me.executionId,
			businessId : me.businessId
		});
		me.identifyGrid.reloadData(me.executionId);
		
		Ext.apply(me, {
			border : false,
			margin : '0 0 0 0',
			layout:{
				align: 'stretch',
        		type: 'vbox'
        	},
			items : [me.flowtaskbar, me.identifyGrid],
			bbar : {
				items : [
					'->',{
						text : '添加风险',
						iconCls : 'icon-add',
						handler : function() {
							me.addRisk();
						}
					},
					{
						text : '提交',
						iconCls : 'icon-operator-submit',
						handler : function() {
							me.submit();
						}
					}
				]
			},
			listeners : {
				beforerender : function() {
					var me = this;
					FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/findAssessName.f',
			            params : {
			            	assessPlanId: me.businessId
			            },
			            callback: function (data) {
			                if (data && data.success) {
			                	var assessPlanName = data.assessPlanName;
			                	me.flowtaskbar.setTitle('计划名称:' + assessPlanName);
			                } 
			            }
			        });
				}
			}
		});

		me.callParent(arguments);
	}
});