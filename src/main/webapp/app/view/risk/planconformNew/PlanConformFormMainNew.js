/**
 * 
 * 整合计划制定入口
 */
Ext.define('FHD.view.risk.planconformNew.PlanConformFormMainNew',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.planConformFormMainNew',
 	border:false,
 	layout:{
		align: 'stretch',
		type: 'border'
	},
 	requires: [
 		'FHD.ux.org.CommonSelector',
 		'FHD.view.sys.role.RoleMainPanel'
	],
 	//提交方法
	submitWindow:function(){
		var me = this;
		if(me.planConformEditOne.riskWorkSelect.getValue() == 'kpiTargetGather'
			|| me.planConformEditOne.riskWorkSelect.getValue() == 'kpiFinishGather') {
			Ext.MessageBox.show({
	    		title : '提示',
	    		width : 260,
	    		msg : '确认提交吗？',
	    		buttons : Ext.MessageBox.YESNO,
	    		icon : Ext.MessageBox.QUESTION,
	    		fn : function(btn) {
	    			if (btn == 'yes') {//确认
	    				if(me.planConformEditNext.validateDate()) {
	    					var grid = me.planConformEditNext.downPanel;
	    					var startTime = me.planConformEditNext.assessKpiTimeStart.getValue();
	    					var checkType = me.planConformEditNext.checkFre.getValue();
 			    			grid.submitPlanByParams(startTime,checkType);	
	    				} 			
	    			}
	    		}
	    	});
		} else {
			me.formulateSubmitMainPanel = Ext.create('FHD.view.risk.assess.formulatePlan.FormulateSubmitMainPanel');
			var formulateApproverEdit = me.formulateSubmitMainPanel.formulateapproveredit;//审批人页面
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
	   					{ xtype: 'button', text: '确定', handler:function(){me.btnConfirm(formulateApproverEdit);}},
	   					{ xtype: 'button', text: '取消', handler:function(){me.subWinhide(formulateApproverEdit);}}
					  ]
			}).show();
		}

	},
	
	//窗口确认按钮事件
	btnConfirm: function(form){
		var me = this;
		var approverId = form.items.items[0].value;
		if(!approverId){
			FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
			return ;
		}
		//验证是否有过期的人员
		var grid = me.planConformEditNext.downPanel;
		var businessId = me.planConformEditOne.idField.getValue();
		var empIds = [];
		var items = grid.store.data.items;
		for(var k in items){
			if(!items[k].data.empId){
				 empIds = [];
				 break;
			}else{
				empIds.push(items[k].data.empId);
			}
		}
		FHD.ajax({//ajax调用
			url : __ctxPath + '/access/planconform/findempdatebyempids.f',
			params : {
				empIds: empIds,
				approverId: approverId
			},
			callback : function(data){
				if(data.length>0){//删除成功！
					FHD.notification(data+'密码已过期，请重新分配 ！',FHD.locale.get('fhd.common.prompt'));
					me.subWin_assess.hide();
					form.approver.clearValues();
					return false;
				}else{
					Ext.MessageBox.show({
			    		title : '提示',
			    		width : 260,
			    		msg : '确认提交吗？',
			    		buttons : Ext.MessageBox.YESNO,
			    		icon : Ext.MessageBox.QUESTION,
			    		fn : function(btn) {
			    			if (btn == 'yes') {//确认
			    				var grid = me.planConformEditNext.downPanel;
				    			grid.submitPlanByParams(approverId,businessId,me.valueAll);
								if(form){
									me.subWin_assess.hide();
									form.approver.clearValues();
								}
			    			}
			    		}
			    	});
				}
			}
		});
	},
	//为计划下一步基本信息表单赋值
	nextFieldSetValue: function(){
		var me = this;
		var contactor = '';
		var responser = '';
		me.planConformEditNext.down("[name='pc_planName']").setValue(me.planConformEditOne.planName.getValue());
		var beginDate = Ext.util.Format.date(me.planConformEditOne.assessPlanTimeStart.getValue(), 'Y-m-d');
    	var endDate = Ext.util.Format.date(me.planConformEditOne.assessPlanTimeEnd.getValue(), 'Y-m-d');
    	me.planConformEditNext.down("[name='pc_date']").setValue(beginDate + ' 至 ' + endDate);
    	if(me.planConformEditOne.contactor.valueStore.data.items[0]){
    		contactor = me.planConformEditOne.contactor.valueStore.data.items[0].data.field1;
    	}
    	if(me.planConformEditOne.reponser.valueStore.data.items[0]){
    		responser = me.planConformEditOne.reponser.valueStore.data.items[0].data.field1;
    	}
    	me.planConformEditNext.down("[name='pc_contactor']").setValue(contactor);
    	me.planConformEditNext.down("[name='pc_responser']").setValue(responser);
	},

	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
   
    initComponent: function () {
    	var me = this;
    	//计划制定第一步
    	me.planConformEditOne = Ext.create('FHD.view.risk.planconformNew.PlanConformEditOneNew',{
		 	navigatorTitle:'计划制定',
		 	//计划类型           吉志强添加
		 	planType:me.planType,
		 	border: false,
		 	//2017年3月28日11:03:51 吉志强添加风险分库标识
		 	schm :me.typeId,
		 	last:function(){//保存计划
		 		var form = me.planConformEditOne.getForm();
		 		if(me.planConformEditOne.workTage.getValue().length>500){
    		 		FHD.notification('工作目标输入超过限定长度！',FHD.locale.get('fhd.common.prompt'));
					return false;
    		 	}else if(me.planConformEditOne.rangRequire.getValue().length>500){
    		 		FHD.notification('范围要求输入超过限定长度！',FHD.locale.get('fhd.common.prompt'));
					return false;
    		 	}
    		 	//判断起止日期
    		 	var beginTime = me.planConformEditOne.assessPlanTimeStart.getValue();
    		 	var endTime = me.planConformEditOne.assessPlanTimeEnd.getValue();
    		 	if(beginTime && endTime && beginTime >= endTime){
    		 		FHD.notification('结束时间必须大于开始时间!',FHD.locale.get('fhd.common.prompt'));
					return false;
    		 	}
    		 	if(form.isValid()){
    		 		FHD.submit({
						form:form,
						url: __ctxPath + '/access/planconform/saveplanbyplantypeNew.f',
						callback:function(data){
							me.valueAll = data.data.valueAll;
							me.planConformEditNext.loadData(data.data.planId,data.data.dictValue);
							me.planConformEditOne.idField.setValue(data.data.planId);//id赋值
							me.nextFieldSetValue();
							me.btnSubmit.setDisabled(false);
						}
					});
    		 	}else{
		 			return false;
		 		}
		 	}
    	});
		me.planConformEditNext = Ext.create('FHD.view.risk.planconformNew.PlanConformEditNextNew',{
			 navigatorTitle:'范围选择',
			//2017年3月28日11:03:51 吉志强添加风险分库标识
			 schm :me.typeId,
			 planType:me.planType,
			 border: true,
			 back:function(){//上一步方法，传参
			 	me.btnSubmit.setDisabled(true);
			 }
		});
    		 
		me.btnSubmit = Ext.create('Ext.button.Button',{
            text: '提交',//提交按钮
            disabled: true,
            iconCls: 'icon-operator-submit',
            handler: function () {
            	
            	var items = me.planConformEditNext.downPanel.store.data.items;
            	if(items.length == 0){
            		FHD.notification('请选择风险事件！',FHD.locale.get('fhd.common.prompt'));
					return 
            	}
            	//验证承办人
            	for(var k in items){
					if(!items[k].data.empId){
						FHD.notification('承办人不能为空！',FHD.locale.get('fhd.common.prompt'));
						return ;
					}
				}
            	me.submitWindow();
            }
        });
		me.basicPanel = Ext.create('FHD.ux.layout.StepNavigator',{
		 	region: 'center',
		    hiddenTop: true,	//是否隐藏头部
		    hiddenBottom: false, //是否隐藏底部
		    hiddenUndo: false,	//是否有返回按钮
			btns: [me.btnSubmit],
		 	items: [me.planConformEditOne,me.planConformEditNext],
		 	undo : function(){
		 		//清空范围选择基础信息表单值
		 		//范围选择--联系人赋值(组件)
		 		me.planConformEditNext.items.items[0].items.items[2].setValue("");
		 		//范围选择--负责人赋值
		 		me.planConformEditNext.items.items[0].items.items[3].setValue("");
		 		var cardPanel = me.up('planConformCardNew');
		 		setTimeout(function(){
		 			cardPanel.planConformGrid.store.load();
		 			cardPanel.showPlanConformGrid();
		 		},200);
		 		
		 	}
		});
    	me.callParent(arguments);
    	me.add(me.basicPanel);
    	
    },
    reloadData:function(){
		var me=this;
	}

});