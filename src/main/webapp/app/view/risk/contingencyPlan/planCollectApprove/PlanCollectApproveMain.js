Ext.define('FHD.view.risk.contingencyPlan.planCollectApprove.PlanCollectApproveMain', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.planCollectApproveMain',
	
	reloadData : function() {

	},

	//提交方法
	submitWindow: function(){
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
		//提交审批
	btnConfirm:function(form){
		var me=this;
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
    			if (btn == 'yes') {//确认
    				if(me.subWin_assess){
    					me.subWin_assess.hide();
						form.approver.clearValues();
    				}
    				me.body.mask("提交中...","x-mask-loading");
					FHD.ajax({//ajax调用
						url : __ctxPath+ '/contingencyPlan/approval/submitriskidentifyapprovalbysupervisor.f',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	approverId: approverId,
					    	isPass:'yes',
					    	examineApproveIdea:'',
					    	approverKey : 'approveFive'
						},
						callback : function(data) {
							me.body.unmask();
							if(me.winId != null){
								Ext.getCmp(me.winId).close();
							}else{
								window.location.reload();
							}
						}
					});
    			}
    		}
    	});
	},
	
	//关闭提交窗口
	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},  
	
	// 初始化方法
	initComponent : function() {
		var me = this;
	  	
	  	me.flowtaskbar = Ext.widget('panel',{
//			region: 'north',
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
	    		jsonArray:[
		    		{index: 1, context:'1.计划制定',status:'done'},
		    		{index: 2, context:'2.计划主管审批',status:'done'},
		    		{index: 3, context:'3.计划领导审批',status:'done'},
		    		{index: 4, context:'4.任务分配',status:'done'},
		    		{index: 5, context:'5.应急预案',status:'done'},
		    		{index: 6, context:'6.预案审批',status:'done'},
		    		{index: 7, context:'7.单位主管审批',status:'done'},
		    		{index: 8, context:'8.单位领导审批',status:'done'},
		    		{index: 9, context:'9.业务分管副总审批',status:'done'},
		    		{index: 10, context:'10.预案汇总整理',status:'current'},
		    		{index: 11, context:'11.风险部门主管审批',status:'undo'},
		    		{index: 12, context:'12.风险部门领导审批',status:'undo'},
		    		{index: 13, context:'13.业务分总审批',status:'undo'},
		    		{index: 14, context:'14.备案',status:'undo'}
		    	],
		    	margin : '5 5 5 5'
    	})});
		
		Ext.apply(me, {
			border : false,
			margin : '0 0 0 0',
			layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
			items : [me.flowtaskbar],
			bbar : [
		    			'->',
		    			{
							text : '提交',
							iconCls: 'icon-operator-submit',
							handler : function() {
								me.submitWindow();
							}
						}
		    			
		    		],
		    		listeners: {
						beforerender : function () {
							var me = this;
							FHD.ajax({
					            url: __ctxPath + '/assess/quaassess/findAssessName.f?assessPlanId=' + me.businessId,
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
		
		FHD.ajax({
	        url: __ctxPath + '/assess/riskTidy/riskRbsOrRe.f?assessPlanId=' + me.businessId,
	        callback: function (data) {
	            if (data && data.success) {
	            	var riskRbsOrReListMap = {ids:data.riskRbsOrReListMap};
	            	var ids = {ids:data.ids};
	            	me.riskTidyTab = Ext.create('FHD.view.risk.contingencyPlan.planCollectApprove.PlanCollectTidyGridPanel',{flex:4,border:false,riskTidyMan : me,assessPlanId : me.businessId});
					me.assessTree = Ext.create("FHD.view.risk.contingencyPlan.planCollectApprove.PlanCollectTidyTreePanel",{flex:1.2,border:false,extraRiskParams : riskRbsOrReListMap, region:'west',
				    		extraParams : ids, businessId : me.businessId, riskTidyMan : me,
				    		assessPlanId : me.businessId, collapseds : false});
		    		me.panel = Ext.widget('panel',{
		    			border:false,
		    			flex : 1,
		    			layout:{
			        		align: 'stretch',
			        		type: 'hbox'
			        	},
			        	items : [me.assessTree,me.riskTidyTab]
		    		});
					me.add(me.panel);
	            }
	        }
    	});
	}
});