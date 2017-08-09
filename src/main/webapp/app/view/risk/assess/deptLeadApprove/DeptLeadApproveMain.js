
Ext.define('FHD.view.risk.assess.deptLeadApprove.DeptLeadApproveMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.deptLeadApproveMain',
    
    requires: [
               'FHD.view.risk.assess.utils.GridCells'
              ],
    
	reloadData:function(){
		 
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
					Ext.MessageBox.show({
			    		title : '提示',
			    		width : 260,
			    		msg : '确认提交吗？',
			    		buttons : Ext.MessageBox.YESNO,
			    		icon : Ext.MessageBox.QUESTION,
			    		fn : function(btn) {
			    			if (btn == 'yes') {
			    				Ext.Ajax.timeout = 1000000;
								me.body.mask("提交中...","x-mask-loading");
								
								FHD.ajax({
						            url: __ctxPath + '/assess/riskTidy/submitRiskTidySf2.f',
						            params: {
						            	// 宋佳   riskDatas 无用 params : Ext.JSON.encode(me.collectGrid.riskDatas),
						            	executionId : me.executionId,
						            	assessPlanId : me.businessId
						            },
						            callback: function (data) {
						            	me.body.unmask();
						            	if(Ext.getCmp(me.winId) != null){
											Ext.getCmp(me.winId).close();
										}else{
											window.location.reload();
										}
						            }
						        });
			    			}
			    		}
			    	});
	            }
			}
		];
		me.flowtaskbar = Ext.widget('panel',{
			region: 'north',
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
		    		{index: 11, context:'11.风险部门主管审批',status:'done'},
		    		{index: 12, context:'12.风险部门领导审批',status:'current'}
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
	  	
	  	me.downPanel = Ext.create('Ext.form.Panel',{
	  		autoScroll: true,
	  		region: 'center',
        	border:false,
        	flex : 1,
            items : [me.collectGridSet, me.ideaApprovalSet]
	  	});
		
	  	me.collectGrid = Ext.create('FHD.view.risk.assess.newAgainRiskTidy.NewRiskTidyGridForSubmit',{
    		businessId: me.businessId, executionId: me.executionId,flex : 1
    	});//isLeader:多级审批（风险部门主管、领导审批）节点
        var extraParams = {
        		assessPlanId : me.businessId,
        		typeId : "root",
        		type : "risk"
        };
    	me.collectGrid.store.proxy.extraParams = extraParams;
    	me.collectGrid.store.load();
		 Ext.apply(me, {
	        	autoScroll: false,
	        	border:false,
	        	layout:{
	        		align: 'stretch',
	        		type: 'vbox',
	        		padding: .5
	        	},
	            items : [me.flowtaskbar,me.collectGrid,me.downPanel],
	    	
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