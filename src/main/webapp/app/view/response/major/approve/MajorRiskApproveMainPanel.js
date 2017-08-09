/**
 * 计划主管审批
 */
Ext.define('FHD.view.response.major.approve.MajorRiskApproveMainPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.majorriskapprovemainpanel',
    requires: [
	],
	//获得导航item
	getItems: function(){
		var me = this;
		me.flowtaskbarItem = Ext.create('FHD.ux.icm.common.FlowTaskBar',{
    		jsonArray:[
    			{index: 1, context:'1.计划制定',status:'done'},
	    		{index: 2, context:'2.计划主管审批',status:'current'},
	    		{index: 3, context:'3.计划领导审批',status:'undo'},
	    		{index: 4, context:'4.任务分配',status:'undo'},
	    		{index: 5, context:'5.方案制定',status:'undo'},
	    		{index: 6, context:'6.部门汇总',status:'undo'},
	    		{index: 7, context:'7.部门主管审批',status:'undo'},
	    		{index: 8, context:'8.部门领导审批',status:'undo'},
	    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
	    		{index: 10, context:'10.方案审批(风管办公室)',status:'undo'},
	    		{index: 11, context:'11.结果整理',status:'undo'}
	    	],
	    	margin : '5 5 5 5'
		});
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
		var isPass = me.GridApproveDown.ideaApproval.isPass;
		var examineApproveIdea = me.GridApproveDown.ideaApproval.getValue();
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
    				//提交计划审批，执行流程
    				FHD.ajax({//ajax调用
						url : __ctxPath+ '/majorResponse/majorRiskPlanApprove',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea,
					    	approverId: approverId,
					    	approverKey:"planLeaderApproval"//计划领导审批
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
	reloadData:function(){
		var me=this;
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
	  	me.GridApproveDown = Ext.create('FHD.view.response.major.approve.MajorRiskApproveDownFormPanel',{
	  		flex: 1,
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  		winId : me.winId,
	  		margin : '0 0 0 0'
	  	});
	  	//流程节点导航
		me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items: me.flowtaskbarItem
		});
        Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.flowtaskbar,me.GridApproveDown],
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
        
        /*me.form.load({
	        url: __ctxPath + '/access/formulateplan/queryassessplanbyplanId.f',
	        params:{businessId:me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });*/
    }
});