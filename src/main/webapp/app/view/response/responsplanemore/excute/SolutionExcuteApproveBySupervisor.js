Ext.define('FHD.view.response.responsplanemore.excute.SolutionExcuteApproveBySupervisor', {
	extend:'Ext.panel.Panel',
	aligs:'widget.solutionExcuteApproveBySupervisor',
	requires: [
    ],
	autoScroll:false,
	border: false,
	initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	//提交方法
	submitWindow: function(){
		var me = this;
		var isPass = me.ideaApproval.isPass;
		if('no'== isPass){
				me.btnConfirm();
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
	   					{ xtype: 'button', text: '确定', handler:function(){me.btnConfirm(formulateApproverEdit);}},
	   					{ xtype: 'button', text: '取消', handler:function(){me.subWinhide(formulateApproverEdit);}}
					  ]
			}).show();
		}
	},
	//提交审批
	btnConfirm:function(form){
		var me=this;
		var isPass = me.ideaApproval.isPass;
		var examineApproveIdea = me.ideaApproval.getValue();
		if('no' == isPass){
			var approverId = '';
		}else{
			var approverId = form.items.items[0].value;
			if(!approverId){
				FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
				return ;
			}
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
						url : __ctxPath+ '/responseplan/workflow/solutionplanapprovebyleader.f',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea,
					    	approverId: approverId
						},
						callback : function(data) {
							me.body.unmask();
							if(me.winId){
								Ext.getCmp(me.winId).close();
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
	
	initComponent : function() {
		var me=this;
		me.bbar={
			items: [
				'->',{
       				text: '提交',
    				iconCls: 'icon-operator-submit',
    				handler: function () {
						me.submitWindow();
    				}
				}
    		]
		};
		
		me.flowtaskbar=Ext.widget('panel',{
            collapsed:true,
            collapsible: true,
            title : '应对执行',
            border: false,
        	items: Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划主管审批',status:'done'},
					    		{index: 3, context:'3.计划领导审批',status:'done'},
					    		{index: 4, context:'4.任务分配',status:'done'},
					    		{index: 5, context:'5.方案制定',status:'done'},
					    		{index: 6, context:'6.方案审批',status:'done'},
					    		{index: 7, context:'7.单位主管审批',status:'done'},
					    		{index: 8, context:'8.单位领导审批',status:'done'},
					    		{index: 9, context:'9.业务分管副总审批',status:'done'},
					    		{index: 10, context:'10.方案执行',status:'done'},
					    		{index: 11, context:'11.风险应对主管审批',status:'current'},
					    		{index: 12, context:'12.风险应对领导审批',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	})
        });
		
		me.solutionformforview = Ext.create('FHD.view.response.new.SolutionFormForView',{ margin : '0 0 0 0'});
		
		me.executionHistory = Ext.create('FHD.view.response.ExecutionHistoryList',{columnWidth:1/1});
		me.historySet = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				collapsed : false,
				collapsible : true,
				title : '历史记录',
				margin: '5 5 0 5',
				items:[me.executionHistory]
	  	});
		
		//审批意见
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			columnWidth:1/1,
			executionId:me.executionId
		});
		me.ideaApprovalSet = Ext.create('Ext.form.FieldSet',{
				layout:{
         	        type: 'column'
         	    },
				title:'审批意见',
				collapsible: true,
				margin: '5 5 0 5',
				items:[me.ideaApproval]
	  	});
	  	
	  	me.downpanel = Ext.create('Ext.panel.Panel',{
	  		flex: 1,
			autoScroll: true,
        	border:false,
			items:[me.solutionformforview,me.historySet,me.ideaApprovalSet]
		});
	  	
		Ext.applyIf(me, {
			layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
			items:[me.flowtaskbar,me.downpanel]
		});
		me.callParent(arguments);
	},
	reloadData:function(){
		var me=this;
		//初始化业务页面的数据
		FHD.ajax({
			url:__ctxPath+'/response/bpm/getbpmvalues.f',
			params: {
				executeId : me.executionId
			},
	     	callback: function (data) {
				if(data.success){
					me.solutionformforview.initParam({
						solutionId : data.data.solutionId,
						riskId : data.data.riskId
					});
					me.solutionformforview.reloadData();
					me.solutionformforview.basicInfoFieldset.collapse();
					me.executionHistory.initParam({
						solutionExecutionId : data.data.solutionExecuteId
					});
					me.executionHistory.reloadData();
				}else{
					Ext.Msg.alert("提示","初始化工作流数据失败！");
					return false;
				}
	     		
	         }
         });
	}
});