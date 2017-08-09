Ext.define('FHD.view.response.major.plan.MajorRiskPlanFormMainPanel',{
 	extend: 'Ext.form.Panel',
 	alias : 'widget.majorriskplanformmainpanel',
 	border:false,
 	layout:{
		align: 'stretch',
		type: 'border'
	},
 	requires: [
	],
	//窗口确认按钮事件
	save: function(form){
		var me = this;
		var approverId = form.items.items[0].value;
		if(!approverId){
			FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
			return ;
		}
		
		var url =  __ctxPath +"/majorResponse/saveRiskAndPlanRela";
    	var values = new Array();
    	var selectedGrid = me.majorRiskPlanFormTwo.riskSelecteGrid.getStore();
    	selectedGrid.each(function(r){
		    values.push(r.data);
		});
	 	FHD.ajax({
			url:url,
            params:{
            	param:Ext.JSON.encode(values),
            	approverId: approverId
            },
            async: false,
            callback: function (data) {
            	//说明有的部门没有风险管理员
            	if(data.success ==false && data.result!= null){
            		FHD.notification("【"+data.result+"】 没有配置部门风险管理员，不能发起计划!",FHD.locale.get('fhd.common.prompt'));
        			return ;
            	}
            	var cardPanel = me.up('majorriskplancard');
            	cardPanel.majorRiskPlanGridPanel.store.load();
	 			cardPanel.changeLayout("grid");
            }
        });
	 	me.subWinhide(form);
	},
	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
    initComponent: function () {
    	var me = this;
    	//提交按钮，流程开始
    	me.btnSubmit = Ext.create('Ext.button.Button',{
            text: '提交',
            disabled: true,
            iconCls: 'icon-operator-submit',
            handler: function () {
            	var values = new Array();
            	var selectedGrid = me.majorRiskPlanFormTwo.riskSelecteGrid.getStore();
            	selectedGrid.each(function(r){
        		    values.push(r.data);
        		});
            	if(values.length == 0){
            		FHD.notification('必须选择重大风险!',FHD.locale.get('fhd.common.prompt'));
            		return;
            	}else{
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
        	   					{ xtype: 'button', text: '确定', handler:function(){me.save(formulateApproverEdit)}},
        	   					{ xtype: 'button', text: '取消', handler:function(){me.subWinhide(formulateApproverEdit)}}
        					  ]
        			}).show();
            	}
           
            }
        });
    	//第一步填写计划信息
    	me.majorRiskPlanFormOne = Ext.create('FHD.view.response.major.plan.MajorRiskPlanFormOne',{
    		 planType:me.planType,//计划类型
    		 schm:me.schm,//分库标识
    		 border: true,
    		 last:function(){
    			 var form = me.majorRiskPlanFormOne.getForm();
				 //判断起止日期
				 var beginTime = me.majorRiskPlanFormOne.assessPlanTimeStart.getValue();
				 var endTime = me.majorRiskPlanFormOne.assessPlanTimeEnd.getValue();
				 if(beginTime && endTime && beginTime >= endTime){
					FHD.notification('结束时间必须大于开始时间!',FHD.locale.get('fhd.common.prompt'));
				    return false;
				 }
				 
				 //前端校验
    			 if(form.isValid()){
    				 //保存应对计划
    				 FHD.submit({
    						form:form,
    						url: __ctxPath + '/majorResponse/savePlan',
    						callback:function(data){
    							//设置提交按钮可用
    							me.btnSubmit.setDisabled(false);
    							var planId = data.data.planId;
    							me.majorRiskPlanFormTwo.planId.setValue(planId);
    							//初始化树和grid
    		    				me.majorRiskPlanFormTwo.initDataForTreeAndGrid(planId);
    		    				me.majorRiskPlanFormTwo.treePanel.planId = planId;
    						}
    					});
    				 
    			 }else{
    				 return false;
    			 }
    			 
    			 
    		 }
 		});
        //第二步填写重大风险信息
        me.majorRiskPlanFormTwo = Ext.create('FHD.view.response.major.plan.MajorRiskPlanFormTwo',{
        	 border: true,
        	 planType:me.planType,//计划类型
    		 schm:me.schm,//分库标识
			 back:function(){
			 	me.btnSubmit.setDisabled(true);
			 }
         });
         //主布局
         me.basicPanel = Ext.create('FHD.ux.layout.StepNavigator',{
		 	region: 'center',
		    hiddenTop: true,	//是否隐藏头部
		    hiddenBottom: false, //是否隐藏底部
		    hiddenUndo: false,	//是否有返回按钮
			btns: [me.btnSubmit],
		 	items: [me.majorRiskPlanFormOne,me.majorRiskPlanFormTwo],
		 	undo : function(){//返回键
		 		var cardPanel = me.up('majorriskplancard');
		 		setTimeout(function(){
		 			cardPanel.majorRiskPlanGridPanel.store.load();
		 			cardPanel.changeLayout("grid");
		 		},200);
		 	}
		});
    	me.callParent(arguments);
    	me.add(me.basicPanel);
    }

});