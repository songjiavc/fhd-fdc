Ext.define('FHD.view.risk.managereport.managereportmake.ManageReportPlanMakeEditMain',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.managereportplanmakeeditmain',
 	requires: [
 		'FHD.ux.org.CommonSelector'
	],
 	border:false,
 	layout:{
	    		align: 'stretch',
	    		type: 'border'
        	},
        	
    listeners: {
			beforerender : function () {
				var me = this;
				if(me.businessId){
					FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/findAssessName.f',
			            params: {
			            	assessPlanId : me.businessId
			            },
			            callback: function (data) {
			                if (data && data.success) {
			                	var assessPlanName = data.assessPlanName;
			                	//Ext.getCmp(me.winId).setTitle('计划名称:' + assessPlanName);
			                	me.flowtaskbar.setTitle('计划名称:' + assessPlanName);
			                } 
			            }
			        });
				}
			}
		},
 	//提交方法
	submitWindow:function(me,deptIds){
		me.subWin = Ext.create('FHD.ux.Window', {
			title:'选择审批人',
   		 	height: 200,
    		width: 600,
   			layout: {
     	        type: 'fit'
     	    },
   			buttonAlign: 'center',
   			closeAction: 'hide',
    		items: [me.workPlanApproverEditMain],
   			fbar: [
   					{ xtype: 'button', text: '确定', handler:function(){me.btnConfirm(me.workPlanApproverEditMain);}},
   					{ xtype: 'button', text: '取消', handler:function(){me.subWinhide(me.workPlanApproverEditMain);}}
				  ]
		}).show();
	},
	//窗口确认按钮事件
	btnConfirm: function(form){
		var me = this;
		var approverId = form.approver.value;
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
			    			if (btn == 'yes') {//确认删除
								//新版
								var grid = me.p2.rightgrid;
								
								var empIds = [];
								//var approverId = form.approver.value;
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
							
								me.body.mask("提交中...","x-mask-loading");
								FHD.ajax({//ajax调用
						    				url : __ctxPath + '/access/formulateplan/submitassessriskplan.f',
						    				params : {
						    					empIds: empIds.join(','),
						    					approverId: approverId,
						    					businessId: me.businessId,
						    					deptEmpId: Ext.encode(jsonArray),
						    					executionId: me.executionId,
						    					entityType:'contingencyPlanTotal'
						    				},
						    				callback : function(data){
						    					me.body.unmask();
						    					if(me.winId){
										    		Ext.getCmp(me.winId).close();
										    	}else{
										    		var prt = me.up('managereportplanmakecard');
							    					prt.managereportplanmakegrid.store.load();
							    					//取消列表已选中的列，解决提交后未刷新的重复修改问题
							    					prt.managereportplanmakegrid.getSelectionModel().deselectAll(true);
										    		prt.showWorkPlanMakeGrid();
										    	}
						    				}
						    			});
						    	me.subWin.hide();
						    	form.approver.clearValues();
						    	//form.approver.removeAll(true);
			    			}
			    		}
			    	});
		
	},

	subWinhide: function(form){
		var me = this;
		me.subWin.hide();
		form.approver.clearValues();
    	//form.approver.removeAll(true);//销毁组件
	},
   
    initComponent: function () {
    	var me = this;
    	me.workPlanApproverEditMain = Ext.create('FHD.view.response.workplan.workplanmake.WorkPlanApproverEditMain');
    	
    	var planEdit_saveUrl = __ctxPath + '/managereport/savemanagereportplan.f';//保存计划制定表单url
    	 me.p1 = Ext.create('FHD.view.risk.managereport.managereportmake.ManageReportPlanEditForm',{
    	 		businessId:me.businessId,
    		 	navigatorTitle:'计划制定',
    		 	border: false,
    		 
	    		 last:function(){//新版评估范围查询
    		 		var form = me.p1.getForm();
    		 		var managereportplandepttakergrid = me.down('managereportplandepttakergrid');
    		 		if(form.isValid()){
    		 			if(me.executionId && me.businessId){//工作流回退
    		 				FHD.submit({
	    						form:form,
	    						url:planEdit_saveUrl,
	    						params : {
	    							id : me.businessId,
	    							executionId : me.executionId
	    						},
	    						callback:function(data){
	    							//为风险列表url动态赋值，重新加载数据
				    		 		managereportplandepttakergrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
				    		 		managereportplandepttakergrid.store.proxy.extraParams.planId = me.businessId;
				    		 		managereportplandepttakergrid.store.load();
	    						}
	    					});
    		 			}else{
    		 				var managereportplanmakegrid = me.up('managereportplanmakecard').managereportplanmakegrid;
    		 				if(typeof(managereportplanmakegrid.businessId) == 'undefined'){
	    		 				FHD.submit({//添加评估计划
		    						form:form,
		    						url:planEdit_saveUrl,
		    						callback:function(data){
		    							managereportplanmakegrid.store.load();
		    							me.businessId = data.data.planId;
		    							//为风险列表url动态赋值，重新加载数据
					    		 		managereportplandepttakergrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
					    		 		managereportplandepttakergrid.store.proxy.extraParams.planId = me.businessId;
					    		 		managereportplandepttakergrid.store.load();
		    						}
		    					});
	    		 			}else{//修改
	    		 				FHD.submit({
		    						form:form,
		    						url:planEdit_saveUrl + '?id=' + managereportplanmakegrid.businessId,
		    						callback:function(data){
		    							managereportplanmakegrid.store.load();
		    							me.businessId = data.data.planId;
		    							//为风险列表url动态赋值，重新加载数据
					    		 		managereportplandepttakergrid.store.proxy.url = __ctxPath + '/access/formulateplan/queryscoredeptsandcbrgrid.f';
					    		 		managereportplandepttakergrid.store.proxy.extraParams.planId = me.businessId;
					    		 		managereportplandepttakergrid.store.load();
		    						}
		    					});
	    		 			}
    		 			}
    		 			me.btnSubmit.setDisabled(false);
    		 			//范围选择--计划名称赋值
    		 			me.p2.items.items[0].items.items[0].setValue(me.p1.planName.value);
    		 			//范围选择--起止日期赋值
    		 				//日期控件格式转换
    		 			var beginDate = Ext.util.Format.date(me.p1.assessPlanTime.items.items[1].getValue(), 'Y-m-d');
    		 			var endDate = Ext.util.Format.date(me.p1.assessPlanTime.items.items[3].getValue(), 'Y-m-d');
    		 			me.p2.items.items[0].items.items[1].setValue(beginDate + ' 至 ' + endDate);
    		 			//范围选择--联系人赋值(组件)
    		 			if(me.p1.contactor.valueStore.data.items[0]){
    		 				me.p2.items.items[0].items.items[2].setValue(me.p1.items.items[0].items.items[4].valueStore.data.items[0].data.empname);
    		 			}
    		 			//范围选择--负责人赋值
    		 			if(me.p1.reponser.valueStore.data.items[0]){
    		 				me.p2.items.items[0].items.items[3].setValue(me.p1.items.items[0].items.items[3].valueStore.data.items[0].data.empname);
    		 			}
    		 		}else{
    		 			return false;
    		 		}
	    		 	
	    		 }
    		 });
    		 me.p2 = Ext.create('FHD.view.risk.managereport.managereportmake.ManageReportPlanRange',{
    		 	navigatorTitle:'范围选择',
    		 	border: true,
    		 	last:function(){
    		 		FHD.notification('操作成功',FHD.locale.get('fhd.common.prompt'));
    		 		var prt = me.up('managereportplanmakecard');
    		 		prt.managereportplanmakegrid.store.load();
    		 		prt.showWorkPlanMakeGrid();
    		 	},
    		 	back:function(){//上一步方法，传参
    		 		if(!me.executionId){//工作流回退时不用传参
    		 			var managereportplanmakegrid = me.up('managereportplanmakecard').managereportplanmakegrid;
    		 			managereportplanmakegrid.businessId = me.businessId;
    		 		}
    		 	}
    		 });
    		 
    		 me.flowtaskbar = Ext.widget('panel',{
				region: 'north',
				border: false,
				collapsible : true,
				collapsed:true,
				title: '计划制定',
				items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
		    		jsonArray:[
			    		{index: 1, context:'1.计划制定',status:'current'},
			    		{index: 2, context:'2.计划主管审批',status:'undo'},
			    		{index: 3, context:'3.计划领导审批',status:'undo'},
			    		{index: 4, context:'4.任务分配',status:'undo'},
			    		{index: 5, context:'5.应急预案',status:'undo'},
			    		{index: 6, context:'6.预案审批',status:'undo'},
			    		{index: 7, context:'7.单位主管审批',status:'undo'},
			    		{index: 8, context:'8.单位领导审批',status:'undo'},
			    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
			    		{index: 10, context:'10.预案汇总整理',status:'undo'},
			    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
			    		{index: 12, context:'12.风险部门领导审批',status:'undo'},
			    		{index: 13, context:'13.业务分总审批',status:'undo'},
			    		{index: 14, context:'14.备案',status:'undo'}
				    	],
			    	margin : '5 5 5 5'
	    	})});
    		 
    		me.btnSubmit = Ext.create('Ext.button.Button',{
 	            text: '提交',//提交按钮
 	            disabled: true,
 	            id: 'btn_tijiao_workPlan',
 	            iconCls: 'icon-operator-submit',
 	            handler: function () {
 	            	FHD.ajax({//查询所有打分部门id
	    						url:__ctxPath + '/access/formulateplan/findscoredeptids.f',
	    						params : {
    								planId:me.businessId
    							},
	    						callback:function(data){
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
	    							me.deptIds = data.deptIds;
	    							me.submitWindow(me,me.deptIds);
	    						}
	    					});
 	            }
 	        });
    		 me.basicPanel = Ext.create('FHD.ux.layout.StepNavigator',{
    		 	region: 'center',
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
    		 		var prt = me.up('managereportplanmakecard');
    		 		prt.managereportplanmakegrid.store.load();
    		 		prt.showWorkPlanMakeGrid();
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