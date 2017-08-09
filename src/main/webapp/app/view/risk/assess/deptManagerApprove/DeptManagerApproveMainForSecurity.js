
Ext.define('FHD.view.risk.assess.deptManagerApprove.DeptManagerApproveMainForSecurity', {
    extend: 'Ext.form.Panel',
    alias: 'widget.deptmanagerapprovemainforsecurity',
    
    requires: [
               'FHD.view.risk.assess.utils.GridCells'
              ],
    
	reloadData:function(){
		 
	},
	
	//提交方法
	submitWindow: function(){
		var me = this;
		me.btnConfirm();
	},
	//提交审批
	btnConfirm:function(form){
		var me=this;
		var isPass = me.ideaApproval.isPass;
		var examineApproveIdea = me.ideaApproval.getValue();
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
						url : __ctxPath + '/access/approval/submittidyriskassessriskforsecurity.f',
					    params : {
					    	businessId:me.businessId,
					    	executionId:me.executionId,
					    	isPass:isPass,
					    	examineApproveIdea:examineApproveIdea						},
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
    initComponent: function() {
        var me = this;
	  	me.bbar=[
		    '->',
		    {
				text:'提交',
				iconCls: 'icon-operator-submit',
				handler: function () {
					me.submitWindow();
	            }
			}
		];
		me.flowtaskbar = Ext.widget('panel',{
			border:false,
			collapsible : true,
			collapsed:true,
			items:Ext.create('FHD.ux.icm.common.FlowTaskBar',{
	    		jsonArray:[
		    		{index: 1, context:'1.计划制定',status:'done'},
		    		{index: 2, context:'2.计划主管审批',status:'done'},
		    		{index: 3, context:'3.计划领导审批',status:'done'},
		    		{index: 4, context:'4.任务分配',status:'done'},
		    		{index: 5, context:'5.风险评估',status:'done'},
		    		{index: 6, context:'6.任务审批',status:'done'},
		    		{index: 7, context:'7.单位主管审批',status:'done'},
		    		{index: 8, context:'8.单位领导审批',status:'done'},
		    		{index: 9, context:'9.业务分管副总审批',status:'done'},
		    		{index: 10, context:'10.结果整理',status:'done'},
		    		{index: 11, context:'11.风险部门主管审批',status:'current'}
		    	],
		    	margin : '5 5 5 5'
    	})});
    	
		//审批意见
		me.ideaApproval = Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
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
	  	
	  	me.collectGrid = Ext.create('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyGridForSubmitForSecurity',{
    		businessId: me.businessId, executionId: me.executionId,flex : 1
    	});//isLeader:多级审批（风险部门主管、领导审批）节点
        var extraParams = {
        		assessPlanId : me.businessId,
        		typeId : "root",
        		type : "risk"
        };
    	me.collectGrid.store.proxy.extraParams = extraParams;
    	me.collectGrid.store.load();
	  	
	  	me.downPanel = Ext.create('Ext.form.Panel',{
	  		autoScroll: true,
	  		flex : 1,
        	border:false,
            items : [me.collectGridSet, me.collectGrid,me.ideaApprovalSet]
	  	});
	  	

		Ext.apply(me, {
        	autoScroll: false,
        	border:false,
        	layout:{
        		align: 'stretch',
        		type: 'vbox',
        		padding: .5
        	},
            items : [me.flowtaskbar,me.downPanel],
	    	
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
    }
});