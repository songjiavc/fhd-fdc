Ext.define('FHD.view.risk.contingencyPlan.ContingencyPlanMain', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.contingencyPlanMain',
	
	reloadData : function() {

	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		
		me.contingencyPlanGridPanel  = Ext.create('FHD.view.risk.contingencyPlan.ContingencyPlanGridPanel',{
			type : 'emp',
//	  		region: 'center',
	  		flex: 1,
			border : false
		});
		
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
		    		{index: 5, context:'5.应急预案',status:'current'},
		    		{index: 6, context:'6.预案审批',status:'undo'},
		    		{index: 7, context:'7.单位主管审批',status:'undo'},
		    		{index: 8, context:'8.单位领导审批',status:'undo'},
		    		{index: 9, context:'9.业务分管副总审批',status:'undo'},
		    		{index: 10, context:'10.预案汇总整理',status:'undo'},
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
			items : [me.flowtaskbar,me.contingencyPlanGridPanel],
			bbar : [
		    			'->',
		    			{
							text : '提交',
							iconCls: 'icon-operator-submit',
							handler : function() {
								Ext.MessageBox.show({
						    		title : '提示',
						    		width : 260,
						    		msg : '确认提交吗？',
						    		buttons : Ext.MessageBox.YESNO,
						    		icon : Ext.MessageBox.QUESTION,
						    		fn : function(btn) {
						    			if (btn == 'yes') {//确认删除
						    				me.body.mask("提交中...","x-mask-loading");
					    					FHD.ajax({
					    			            url: __ctxPath + '/contingencyplan/submitContingencyPlan.f',
					    			            params: {
					    			            	executionId : me.executionId,
					    			            	assessPlanId : me.businessId
					    			            },
					    			            callback: function (data) {
				    			            		me.body.unmask();
				    			            		Ext.getCmp(me.winId).close();
					    			            }
					    			        });
						    			}
						    		}
						    	});
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
		
		me.contingencyPlanGridPanel.reloadData(me.executionId,me.businessId);
	}
});