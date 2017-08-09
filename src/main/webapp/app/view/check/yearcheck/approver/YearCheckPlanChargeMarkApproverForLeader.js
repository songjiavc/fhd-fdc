/**
 * 
 * 审计处领导审批(审批)
 */

Ext.define('FHD.view.check.yearcheck.approver.YearCheckPlanChargeMarkApproverForLeader', {
    extend: 'Ext.form.Panel',
    alias: 'widget.yearCheckPlanChargeMarkApproverForLeader',
   
    // 初始化方法
    initComponent: function() {
        var me = this;
        var businessId =me.businessId;
	  	var	executionId= me.executionId;
	  	me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					me.submit(me.formulateApproverSubmitDownMain.ideaApproval.isPass,
			    							me.formulateApproverSubmitDownMain.ideaApproval.getValue());
	            }
			}
		];
	  	
	  	me.formulateApproverSubmitDownMain = Ext.create('FHD.view.check.yearcheck.approver.YearCheckPlanChargeMarkDown',{
	  		flex: 1,
	  		businessId : me.businessId,
	  		executionId: me.executionId,
	  		winId : me.winId,
	  		margin : '0 0 0 0'
	  	});
	  	
		me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
	    		jsonArray:[
		    		{index: 1, context:'1.考核计划制定',status:'done'},
		    		{index: 2, context:'2.主管领导审批',status:'done'},
		    		{index: 3, context:'3.负责人审批',status:'done'},
		    		{index: 4, context:'4.考评打分',status:'current'},
		    		{index: 5, context:'5.风险办汇总',status:'undo'},
		    		{index: 6, context:'6.主管领导汇总审批',status:'undo'},
		    		{index: 7, context:'7.负责人汇总审批',status:'undo'},
		    		{index: 8, context:'8.集团副总审批',status:'undo'}
		    	],
		    	margin : '5 5 5 5'
    	})});
		
        Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox'
        	},
            items : [me.flowtaskbar,me.formulateApproverSubmitDownMain]
        });

        
        me.callParent(arguments);
        
        me.form.load({
	        url: __ctxPath + '/check/yearcheck/findYearCheckPlanById.f',
	        params:{id:me.businessId},
	        failure:function(form,action) {
	            alert("err 155");
	        },
	        success:function(form,action){
	        }
	    });
    },
//提交选择审批人
    submit:function(isPass,examineApproveIdea){
    	
    			var me = this;
		me.formulateSubmitMainPanel = Ext
				.create('FHD.view.risk.assess.formulatePlan.FormulateSubmitMainPanel');
		var formulateApproverEdit = me.formulateSubmitMainPanel.formulateapproveredit;// 审批人页面
		me.subWin_assess = Ext.create('FHD.ux.Window', {
					title : '选择审批人',
					height : 200,
					width : 600,
					layout : {
						type : 'fit'
					},
					buttonAlign : 'center',
					closeAction : 'hide',
					items : [me.formulateSubmitMainPanel],
					fbar : [{
								xtype : 'button',
								text : '确定',
								handler : function() {
									me.submitForm(formulateApproverEdit,isPass,examineApproveIdea);
								}
							}, {
								xtype : 'button',
								text : '取消',
								handler : function() {
									me.subWinhide(formulateApproverEdit);
								}
							}]
				}).show();
		
	},
	subWinhide: function(form){
		var me = this;
		me.subWin_assess.hide();
		form.approver.clearValues();
	},
	submitForm:function (formulateApproverEdit,isPass,examineApproveIdea){
	var me=this;
	var approverId = formulateApproverEdit.items.items[0].value;
	    me.subWinhide(formulateApproverEdit);
		me.body.mask("提交中...","x-mask-loading");
		FHD.ajax({//ajax调用
			url : __ctxPath+ '/check/yearcheck/submAuditMarkForLeader.s',
		    params : {
		    	businessId:me.businessId,
		    	executionId:me.executionId,
		    	isPass:isPass,
		    	approverId:approverId,
		    	examineApproveIdea:examineApproveIdea
			},
			callback : function(data) {
				me.body.unmask();
				if(me.winId){
					me.subWin_assess.hide();
					Ext.getCmp(me.winId).close();
				}else{
					window.location.reload();
				}
			}
		});
	},
	
	 reloadData:function(){
		var me=this;
	}
    
   
});