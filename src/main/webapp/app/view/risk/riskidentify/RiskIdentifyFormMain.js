Ext.define('FHD.view.risk.riskidentify.RiskIdentifyFormMain',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.riskIdentifyFormMain',
 	requires: [
	],
	//获得导航item
	getItems: function(){
		var me = this;
		FHD.ajax({
            url: __ctxPath + '/access/riskidentify/findriskidentifydescription.f',
            params:{
            	executionId:me.executionId
            },
            async: false,
            callback: function (data) {
            	var items;
                me.description = data;
                if('complex'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'current'},
					    		{index: 2, context:'2.计划主管审批',status:'undo'},
					    		{index: 3, context:'3.计划领导审批',status:'undo'},
					    		{index: 4, context:'4.任务分配',status:'undo'},
					    		{index: 5, context:'5.风险辨识',status:'undo'},
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
					    		{index: 1, context:'1.计划制定',status:'current'},
					    		{index: 2, context:'2.计划审批',status:'undo'},
					    		{index: 3, context:'3.任务分配',status:'undo'},
					    		{index: 4, context:'4.风险辨识',status:'undo'},
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
 	//提交方法
	submitWindow: function(form){
		var me = this;
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
	},
	//确认提交
	btnConfirm:function(form){
		var me = this;
		var approverId = form.items.items[0].value;
		if(!approverId){
			FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
			return ;
		}
		var businessId = me.riskIdentifyEditOne.idField.getValue();
		Ext.MessageBox.show({
    		title : '提示',
    		width : 260,
    		msg : '确认提交吗？',
    		buttons : Ext.MessageBox.YESNO,
    		icon : Ext.MessageBox.QUESTION,
    		fn : function(btn) {
    			if (btn == 'yes') {//确认
    				me.subWin_assess.hide();
					form.approver.clearValues();
    				var grid = me.riskIdentifyEditNext.downPanel;
	    			grid.submitPlanByParams(approverId,businessId,me.valueAll);
    			}
    			
    		}
    	});
	},
	//为计划下一步基本信息表单赋值
	nextFieldSetValue: function(){
		var me = this;
		var contactor = '';
		var responser = '';
		me.riskIdentifyEditNext.down("[name='ri_planName']").setValue(me.riskIdentifyEditOne.planName.getValue());
		var beginDate = Ext.util.Format.date(me.riskIdentifyEditOne.assessPlanTimeStart.getValue(), 'Y-m-d');
    	var endDate = Ext.util.Format.date(me.riskIdentifyEditOne.assessPlanTimeEnd.getValue(), 'Y-m-d');
    	me.riskIdentifyEditNext.down("[name='ri_date']").setValue(beginDate + ' 至 ' + endDate);
    	if(me.riskIdentifyEditOne.contactor.valueStore.data.items[0]){
    		contactor = me.riskIdentifyEditOne.contactor.valueStore.data.items[0].data.empname;
    	}
    	if(me.riskIdentifyEditOne.reponser.valueStore.data.items[0]){
    		responser = me.riskIdentifyEditOne.reponser.valueStore.data.items[0].data.empname;
    	}
    	me.riskIdentifyEditNext.down("[name='ri_contactor']").setValue(contactor);
    	me.riskIdentifyEditNext.down("[name='ri_responser']").setValue(responser);
	},

	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
   
    initComponent: function () {
    	var me = this;
    	me.getItems();
    	me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
    	//计划制定第一步
    	//me.riskIdentifyEditOne = Ext.create('FHD.view.risk.riskidentify.RiskIdentifyEditOneNuew',{
    	me.riskIdentifyEditOne = Ext.create('FHD.view.risk.planconformNew.PlanConformEditOneNew',{
		 	navigatorTitle:'计划制定',
		 	//计划类型           吉志强添加
		 	planType:"riskIdentify",
		 	schm:me.schm,
		 	border: false,
		 	last:function(){//保存计划--风险辨识
		 		var form = me.riskIdentifyEditOne.getForm();
		 		if(me.riskIdentifyEditOne.workTage.getValue().length>500){
    		 		FHD.notification('工作目标输入超过限定长度！',FHD.locale.get('fhd.common.prompt'));
					return false;
    		 	}else if(me.riskIdentifyEditOne.rangRequire.getValue().length>500){
    		 		FHD.notification('范围要求输入超过限定长度！',FHD.locale.get('fhd.common.prompt'));
					return false;
    		 	}
    		 	if(form.isValid()){
    		 		FHD.submit({
						form:form,
						//url: __ctxPath + '/access/riskidentify/saveriskidentifyplan.f',
						url: __ctxPath + '/access/planconform/saveplanbyplantypeNew.f',
						params:{
			            	id: me.businessId
			            },
						callback:function(data){
							me.valueAll = data.data.valueAll;
							me.riskIdentifyEditNext.loadData(data.data.planId);
							me.riskIdentifyEditOne.idField.setValue(data.data.planId);//id赋值
							me.nextFieldSetValue();
							me.btnSubmit.setDisabled(false);
						}
					});
    		 	}else{
		 			return false;
		 		}
		 	}
    	});
    	me.riskIdentifyEditOne.loadData(me.businessId);//加载表单数据
    	me.riskIdentifyEditNext = Ext.create('FHD.view.risk.riskidentify.RiskIdentifyEditNext',{
    	
			 navigatorTitle:'范围选择',
			 border: true,
			 executionId: me.executionId,
			 businessId: me.businessId,
			 //吉志强2017年4月17日10:01:12添加
			 schm:me.schm,
			 _description:me.description,//判断是公司流程还是部门流程，为了显示按部门添加按钮
			 winId: me.winId,
			 last:function(){
			 },
			 back:function(){//上一步方法，传参
			 	me.btnSubmit.setDisabled(true);
			 }
		});
    		 
		me.btnSubmit = Ext.create('Ext.button.Button',{
            text: '提交',//提交按钮
            disabled: true,
            iconCls: 'icon-operator-submit',
            handler: function () {
            	me.submitWindow();
            }
        });
		me.basicPanel = Ext.create('FHD.ux.layout.StepNavigator',{
			flex: 1,
		    hiddenTop: true,	//是否隐藏头部
		    hiddenBottom: false, //是否隐藏底部
		    hiddenUndo: true,	//是否有返回按钮
			btns: [me.btnSubmit],
		 	items: [me.riskIdentifyEditOne,me.riskIdentifyEditNext],
		 	undo : function(){
		 		//清空范围选择基础信息表单值
		 		//范围选择--联系人赋值(组件)
		 		me.riskIdentifyEditNext.items.items[0].items.items[2].setValue("");
		 		//范围选择--负责人赋值
		 		me.riskIdentifyEditNext.items.items[0].items.items[3].setValue("");
		 		
		 	}
		});
		Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.flowtaskbar,me.basicPanel],
		    listeners: {
				beforerender : function () {
					var me = this;
					FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/findAssessName.f',
			            params:{
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
    	//me.add(me.basicPanel);
    	
    },
    reloadData:function(){
		var me=this;
	}

});