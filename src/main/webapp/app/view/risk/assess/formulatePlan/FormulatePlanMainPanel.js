/**
 * 
 * 计划制定入口
 */

Ext.define('FHD.view.risk.assess.formulatePlan.FormulatePlanMainPanel',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.formulateplanmainpanel',
 	border:false,
 	layout:{
	    		align: 'stretch',
	    		type: 'vbox'
        	},
        	
    listeners: {
			beforerender : function () {
				var me = this;
				if(me.businessId){
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
		},
 	requires: [
 		'FHD.view.risk.assess.formulatePlan.FormulateGrid',
 		'FHD.view.risk.assess.formulatePlan.FormulatePlanEdit',
 		'FHD.view.risk.assess.formulatePlan.FormulatePlanRang',
 		'FHD.view.risk.assess.formulatePlan.FormulateSubmitMainPanel',
 		'FHD.ux.org.CommonSelector',
 		'FHD.view.sys.role.RoleMainPanel'
	],
 	//提交方法
	submitWindow:function(me,deptIds){
		var formulateApproverEdit = me.formulateSubmitMainPanel.formulateapproveredit;
		me.subWin_assess = Ext.create('FHD.ux.Window', {
			title:'选择审批人',
   		 	height: 200,
    		width: 600,
    		layout: {
     	        type: 'fit'
     	    },
   			buttonAlign: 'center',
   			closeAction: 'hide',
    		items: [me.formulateSubmitMainPanel],
   			fbar: [
   					{ xtype: 'button', text: '确定', handler:function(){me.btnConfirm(/*formulatePlanSubWindowGrid*/formulateApproverEdit);}},
   					{ xtype: 'button', text: '取消', handler:function(){me.subWinhide(formulateApproverEdit);}}
				  ]
		}).show();
	},
	
	//跳转到角色设置页面
	showRolePanel: function(){
		var me = this;
		var isExist = false;
		var centerPanel = Ext.getCmp('center-panel');
		me.roleMainPanel = Ext.widget('roleMainPanel',{
			closable:true,
			title: '角色管理'
    	});
		if(centerPanel){	//程序入口
			for(var i=0;i<centerPanel.items.length;i++){
				if(centerPanel.items.items[i].id.split('-')[0] == me.roleMainPanel.id.split('-')[0]){
					isExist = true;
					me.roleMainPanel = centerPanel.items.items[i];
				}
			}
			if(isExist){
				centerPanel.setActiveTab(me.roleMainPanel);
			}else{
				me.roleMainPanel = centerPanel.add(me.roleMainPanel);
				centerPanel.setActiveTab(me.roleMainPanel);
			}
		}else{	//邮件入口
			me.roleWin = Ext.create('FHD.ux.Window', {
				title:'角色管理',
	   		 	height: 600,
	    		width: 1000,
	    		maximizable: true,
	   			layout: 'fit',
	    		items: [me.roleMainPanel]
			}).show();
		}
	},
	
	//窗口确认按钮事件
	btnConfirm: function(/*grid, */form){
		var me = this;
		var approverId = form.items.items[0].value;
		if(!approverId){
			FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
			return ;
		}
		Ext.MessageBox.show({
			title : '提示',
			width : 260,
			msg : '确认提交吗？',
			buttons : Ext.MessageBox.YESNO,
			icon : Ext.MessageBox.QUESTION,
			fn : function(btn) {
				//验证用户是否存在多个角色中
				var deptIdArray = [];
				var gridItems = me.p2.rightgrid.store.data.items;
				for(var i=0;i<gridItems.length;i++){
					deptIdArray.push(gridItems[i].data.id);
				}
				FHD.ajax({//ajax调用
					url : __ctxPath + '/sys/assess/finduserrolessamebyroleids.f',
					params : {
						deptIds: deptIdArray.join(',')
					},
					callback : function(data){
						if(data.deptName){
							
							/**
							 * 宋佳修改  如果缺少风险管理员将不再跳转到角色管理页面，直接提示就好
							 */
							FHD.alert(data.deptName + '的部门风险管理员角色下有且只能有一名员工，是否现在进行设置？');
							/*
							Ext.MessageBox.show({
					    		title : '提示',
					    		width : 260,
					    		msg : data.deptName + '的部门风险管理员角色下有且只能有一名员工，是否现在进行设置？',
					    		buttons : Ext.MessageBox.YESNO,
					    		icon : Ext.MessageBox.QUESTION,
					    		fn : function(btn) {
					    			if (btn == 'yes') {//跳转到角色设置页面
					    				me.subWin_assess.close();
					    				me.showRolePanel();
					    			}
					    		}
					    	});
					    	*/
							/*var alertStr = '';
							for(var i=0;i<data.length;i++){
								alertStr = alertStr+data[i].userNames+'存在'+data[i].roleNames+'角色中'+'<br>';
							}
							FHD.alert(alertStr+'<br>'+'角色下员工不允许重复，请在风险评价准则中重新配置！');*/
						}else{
							if (btn == 'yes') {//确认删除
							//新版
							var grid = me.p2.rightgrid;
							
							var empIds = [];
							var items = grid.store.data.items;
								var jsonArray=[];
								Ext.each(items,function(item){
									jsonArray.push(item.data);
								});
							
							for(var k in items){
									if(!items[k].data.empId){
										 empIds = [];
										 break;
									}else{
										empIds.push(grid.store.data.items[k].data.empId);
									}
							}
						
							if(!empIds.length){
								 FHD.notification('承办人不能为空！',FHD.locale.get('fhd.common.prompt'));
								return ;
							}
							me.body.mask("提交中...","x-mask-loading");
							FHD.ajax({//ajax调用
					    				url : __ctxPath + '/access/formulateplan/submitassessriskplan.f',
					    				params : {
					    					empIds: empIds.join(','),
					    					approverId: approverId,
					    					businessId: me.businessId,
					    					deptEmpId: Ext.encode(jsonArray),
					    					executionId: me.executionId
					    				},
					    				callback : function(data){
					    					me.body.unmask();
					    					if(me.winId){
									    		Ext.getCmp(me.winId).close();
									    	}else{
									    		var prt;
									    		if(me.up('formulatePlanCard')){
									    			prt = me.up('formulatePlanCard')
									    		}else if(me.up('formulatePlanCardnew')){
									    			prt = me.up('formulatePlanCardnew')
									    		}
						    					prt.formulateGrid.store.load();
						    					//取消列表已选中的列，解决提交后未刷新的重复修改问题
						    					prt.formulateGrid.getSelectionModel().deselectAll(true);
									    		prt.showFormulateGrid();
									    	}
					    				}
					    			});
					    	me.subWin_assess.hide();
					    	form.approver.clearValues();
		    			}
		    					}
		    				}
		    			});
		    		}
		    	});
	},

	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
    	//form.approver.removeAll(true);//销毁组件
	},
   
    initComponent: function () {
    	var me = this;
    	me.formulateSubmitMainPanel = Ext.widget('formulatesubmitmainPanel');
    	var planEdit_saveUrl = 'access/formulateplan/saveriskplan.f';//保存计划制定表单url
    	//吉志强修改 2017年4月11日11:32:05 计划主管审批退回到计划制定
//    	me.p1 = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanEdit',{
    	 me.p1 = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanEditNew',{
    	 		businessId:me.businessId,
    		 	navigatorTitle:'计划制定',
    		 	border: false,
    		 
	    		 last:function(){//新版评估范围查询
	    		 	if(me.p1.workTage.getValue().length>500){
	    		 		FHD.notification('工作目标输入超过限定长度！',FHD.locale.get('fhd.common.prompt'));
						return false;
	    		 	}else if(me.p1.rangRequire.getValue().length>500){
	    		 		FHD.notification('范围要求输入超过限定长度！',FHD.locale.get('fhd.common.prompt'));
						return false;
	    		 	}
    		 		var form = me.p1.getForm();
    		 		var formulateDeptUndertakerGrid = me.down('formulateDeptUndertakerGrid');
//    		 		formulateDeptUndertakerGrid.schm  = me.typeId;
//    		 		formulateDeptUndertakerGrid.planId  = me.businessId;
//    		 		formulateDeptUndertakerGrid.formulatePlanMainPanel = me;
//    		 		formulateDeptUndertakerGrid.flowType = "company";
    		 		if(form.isValid()){
    		 			if(me.executionId && me.businessId){//工作流回退
    		 				FHD.submit({
	    						form:form,
	    						url:planEdit_saveUrl + '?id=' + me.businessId + '&executionId=' + me.executionId+
								'&schm='+me.typeId,
	    						callback:function(data){
	    							//为风险列表url动态赋值，重新加载数据
				    		 		formulateDeptUndertakerGrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
				    		 		formulateDeptUndertakerGrid.store.proxy.extraParams.planId = me.businessId;
				    		 		formulateDeptUndertakerGrid.store.load();
	    						}
	    					});
    		 			}else{
    		 				var formulateGrid;
    		 				if(me.up('formulatePlanCard')){
    		 					formulateGrid = me.up('formulatePlanCard').formulateGrid;
    		 				}else if(me.up('formulatePlanCardnew')){
    		 					formulateGrid = me.up('formulatePlanCardnew').formulateGrid;
    		 				}
    		 				if(typeof(formulateGrid.businessId) == 'undefined'){
	    		 				FHD.submit({//添加评估计划
		    						form:form,
		    						url:planEdit_saveUrl + '?schm='+me.typeId,
		    						callback:function(data){
		    							formulateGrid.store.load();
		    							me.businessId = data.data.planId;
		    							//为风险列表url动态赋值，重新加载数据
					    		 		formulateDeptUndertakerGrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
					    		 		formulateDeptUndertakerGrid.store.proxy.extraParams.planId = me.businessId;
					    		 		formulateDeptUndertakerGrid.store.load();
		    						}
		    					});
	    		 			}else{//修改
	    		 				FHD.submit({
		    						form:form,
		    						url:planEdit_saveUrl + '?id=' + formulateGrid.businessId + '&schm=' + me.typeId,
		    						callback:function(data){
		    							formulateGrid.store.load();
		    							me.businessId = data.data.planId;
		    							//为风险列表url动态赋值，重新加载数据
					    		 		formulateDeptUndertakerGrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
					    		 		formulateDeptUndertakerGrid.store.proxy.extraParams.planId = me.businessId;
					    		 		formulateDeptUndertakerGrid.store.load();
		    						}
		    					});
	    		 			}
    		 			}
    		 			me.btnSubmit.setDisabled(false);
    		 			var formulatePlanEdit = me.p1;
    		 			var formulatePlanRang = me.p2;
    		 			//范围选择--计划名称赋值
    		 			formulatePlanRang.items.items[0].items.items[0].setValue(me.p1.planName.value);
    		 			//范围选择--起止日期赋值
    		 				//日期控件格式转换
    		 			var beginDate = Ext.util.Format.date(me.p1.assessPlanTime.items.items[1].getValue(), 'Y-m-d');
    		 			var endDate = Ext.util.Format.date(me.p1.assessPlanTime.items.items[3].getValue(), 'Y-m-d');
    		 			formulatePlanRang.items.items[0].items.items[1].setValue(beginDate + ' 至 ' + endDate);
    		 			//范围选择--联系人赋值(组件)
    		 			if(me.p1.contactor.valueStore.data.items[0]){
    		 				formulatePlanRang.items.items[0].items.items[2].setValue(formulatePlanEdit.items.items[0].items.items[4].valueStore.data.items[0].data.field1);
    		 			}
    		 			//范围选择--负责人赋值
    		 			if(me.p1.reponser.valueStore.data.items[0]){
    		 				formulatePlanRang.items.items[0].items.items[3].setValue(formulatePlanEdit.items.items[0].items.items[3].valueStore.data.items[0].data.field1);
    		 			}
    		 		}else{
    		 			return false;
    		 		}
	    		 	
	    		 }
    		 });
    		 me.p2 = Ext.create('FHD.view.risk.assess.formulatePlan.FormulatePlanRang',{
    		 	navigatorTitle:'范围选择',
    		 	border: true,
				typeId: me.typeId,
				//将参数传递过去
				schm : me.typeId,
		 		planId:me.businessId,
		 		formulatePlanMainPanel : me,
		 		flowType : "company",
    		 	last:function(){
    		 		FHD.notification('操作成功',FHD.locale.get('fhd.common.prompt'));
    		 		var prt;
		    		if(me.up('formulatePlanCard')){
		    			prt = me.up('formulatePlanCard')
		    		}else if(me.up('formulatePlanCardnew')){
		    			prt = me.up('formulatePlanCardnew')
		    		}
    		 		prt.formulateGrid.store.load();
    		 		prt.formulateGrid.setstatus(prt.formulateGrid);
    		 		prt.showFormulateGrid();
    		 	},
    		 	back:function(){//上一步方法，传参
    		 		if(!me.executionId){//工作流回退时不用传参
    		 			var formulateGrid;
		 				if(me.up('formulatePlanCard')){
		 					formulateGrid = me.up('formulatePlanCard').formulateGrid;
		 				}else if(me.up('formulatePlanCardnew')){
		 					formulateGrid = me.up('formulatePlanCardnew').formulateGrid;
		 				}
    		 			formulateGrid.businessId = me.businessId;
    		 		}
    		 	}
    		 });
    		 
    		 me.flowtaskbar = Ext.widget('panel',{
				border: false,
				collapsible : true,
				collapsed:true,
				title: '计划制定',
				items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
		    		jsonArray:[
			    		{index: 1, context:'1.计划制定',status:'current'},
//			    		{index: 2, context:'2.计划审批',status:'undo'},
//			    		{index: 3, context:'3.任务分配',status:'undo'},
//			    		{index: 4, context:'4.风险评估',status:'undo'},
//			    		{index: 5, context:'5.评估任务审批',status:'undo'},
//			    		{index: 6, context:'6.评估结果整理',status:'undo'}
			    		//2017年4月1日09:50:21吉志强             评估计划主管审批 退回 到计划定制节点 流程节点跟踪状态
			    		{index: 2, context:'2.计划主管审批',status:'undo'},
			    		{index: 3, context:'3.计划领导审批',status:'undo'},
			    		{index: 4, context:'4.任务分配',status:'undo'},
			    		{index: 5, context:'5.风险评估',status:'undo'},
			    		{index: 6, context:'6.任务审批',status:'undo'},
			    		{index: 7, context:'7.单位主管审批',status:'undo'},
			    		{index: 8, context:'8.单位领导审批',status:'undo'},
			    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
			    		{index: 10, context:'10.结果整理',status:'undo'},
			    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
			    		{index: 12, context:'12.风险部门领导审批',status:'undo'}
			    	],
			    	margin : '5 5 5 5'
	    	})});
    		 
    		me.btnSubmit = Ext.create('Ext.button.Button',{
 	            text: '提交',//提交按钮
 	            disabled: true,
 	            id: 'btn_tijiao',
 	            iconCls: 'icon-operator-submit',
 	            handler: function () {
 	            	me.p2.rightgrid.body.mask("验证中...","x-mask-loading");
 	    			FHD.ajax({//ajax调用
 	    				url : __ctxPath + '/riskFiltering.f',
 	    				params : {
 	    					assessPlanId:me.businessId
 	    				},
 	    				callback : function(data){
 	    					if(data.success){
 	    						me.p2.rightgrid.body.unmask();
 	    						FHD.ajax({//查询所有打分部门id
 		    						url:__ctxPath + '/access/formulateplan/findscoredeptids.f',
 		    						params : {
 	    								planId:me.businessId
 	    							},
 		    						callback:function(data){
 		    							me.deptIds = data.deptIds;
 		    							var grid = me.p2.rightgrid;
 										var empIds = [];
 										var items = grid.store.data.items;
 											var jsonArray=[];
 											Ext.each(items,function(item){
 												jsonArray.push(item.data);
 											});
 										for(var k in items){
 												if(!items[k].data.empId){
 													 empIds = [];
 													 break;
 												}else{
 													empIds.push(grid.store.data.items[k].data.empId);
 												}
 										}
 										if(!empIds.length){
 											FHD.notification('承办人不能为空！',FHD.locale.get('fhd.common.prompt'));
 											return ;
 										}
 		    							me.submitWindow(me,me.deptIds);
 		    						}
 		    					});
 	    					}else{
 	    						me.p2.rightgrid.body.unmask();
 	    						Ext.Msg.alert('验证', data.message);
 	    						return;
 	    					}
 	    				}
 	    			});
 	            }
 	        });
    		 me.basicPanel = Ext.create('FHD.ux.layout.StepNavigator',{
    		 	flex: 1,
 			    hiddenTop:true,	//是否隐藏头部
			    hiddenBottom:false, //是否隐藏底部
			    hiddenUndo:false,	//是否有返回按钮
    			btns:[me.btnSubmit],
    		 	items:[me.p1,me.p2],
    		 	undo : function(){
    		 		//清空范围选择基础信息表单值
    		 		//范围选择--联系人赋值(组件)
    		 		me.p2.items.items[0].items.items[2].setValue("");
    		 		//范围选择--负责人赋值
    		 		me.p2.items.items[0].items.items[3].setValue("");
    		 		var prt;
		    		if(me.up('formulatePlanCard')){
		    			prt = me.up('formulatePlanCard')
		    		}else if(me.up('formulatePlanCardnew')){
		    			prt = me.up('formulatePlanCardnew')
		    		}
    		 		prt.formulateGrid.store.load();
    		 		prt.formulateGrid.setstatus(prt.formulateGrid);
    		 		prt.showFormulateGrid();
    		 	}
    		 });
    	me.callParent(arguments);
    	me.add(me.flowtaskbar);
    	me.add(me.basicPanel);
    	
    	if(me.businessId){
    		me.basicPanel.undoBottom.setDisabled(true);
    		me.basicPanel.finishBottom.setDisabled(true);
    	}
    	if(me.winId){
    		me.on('resize',function(p){
    			me.p1.setHeight(Ext.getCmp(me.winId).getHeight()-59);
    			me.p2.setHeight(Ext.getCmp(me.winId).getHeight()-59);
    		});
    	}
    	
    },
    reloadData:function(){
		var me=this;
	}

});