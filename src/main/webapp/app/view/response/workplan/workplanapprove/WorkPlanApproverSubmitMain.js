/**
 * 计划审批
 */
Ext.define('FHD.view.response.workplan.workplanapprove.WorkPlanApproverSubmitMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.workPlanApproverSubmitMain',
    requires: [
	],
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
                if('responseMore'==me.description){
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划主管审批',status:'current'},
					    		{index: 3, context:'3.计划领导审批',status:'undo'},
					    		{index: 4, context:'4.任务分配',status:'undo'},
					    		{index: 5, context:'5.方案制定',status:'undo'},
					    		{index: 6, context:'6.方案审批',status:'undo'},
					    		{index: 7, context:'7.单位主管审批',status:'undo'},
					    		{index: 8, context:'8.单位领导审批',status:'undo'},
					    		{index: 9, context:'9.业务分管副总审批',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }else{
                	me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
				    		jsonArray:[
					    		{index: 1, context:'1.计划制定',status:'done'},
					    		{index: 2, context:'2.计划审批',status:'current'},
					    		{index: 3, context:'3.任务分配',status:'undo'},
					    		{index: 4, context:'4.方案制定',status:'undo'},
					    		{index: 5, context:'5.方案审批',status:'undo'},
					    		{index: 6, context:'6.方案执行',status:'undo'}
					    	],
					    	margin : '5 5 5 5'
			    	});
                }
                return items;
            }
        });
	},
	//提交方法
	submitWindow: function(){
		var me = this;
		if('responseMore'==me.description){//多节点
			var isPass = me.workPlanApproverSubmitDown.ideaApproval.isPass;
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
		}else{
			me.btnConfirm();
		}
	},
	//提交审批
	btnConfirm:function(form){
		var me=this;
		var isPass = me.workPlanApproverSubmitDown.ideaApproval.isPass;
		var examineApproveIdea = me.workPlanApproverSubmitDown.ideaApproval.getValue();
		if('responseMore'==me.description){
			if('no' == isPass){
				var approverId = '';
			}else{
				var approverId = form.items.items[0].value;
				if(!approverId){
					FHD.notification('审批人不能为空！',FHD.locale.get('fhd.common.prompt'));
					return ;
				}
			}
		}else{
			var approverId = '';
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
						url : __ctxPath+ '/responseplan/workflow/riskresponseplanapproval.f',
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
   
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.getItems();
	  	me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					//提交工作流
					me.submitWindow();
	            }
			}
		];
	  	me.workPlanApproverSubmitDown = Ext.create('FHD.view.response.workplan.workplanapprove.WorkPlanApproverSubmitDown',{
	  		flex:1,
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  		winId : me.winId,
	  		margin : '0 0 0 0'
	  	});
		me.flowtaskbar = Ext.widget('panel',{
			maxHeight:200,
            split: true,
			border:false,
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
		
        Ext.apply(me, {
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.flowtaskbar,me.workPlanApproverSubmitDown],
    	
	    listeners: {
			beforerender : function () {
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
        
        me.form.load({
	        url: __ctxPath + '/access/formulateplan/queryassessplanbyplanId.f',
	        params:{businessId:me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
    },
	reloadData:function(){
		var me=this;
	}
});