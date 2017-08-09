/**
 * 
 * 定性评估主面板
 */

Ext.define('FHD.view.risk.riskDataEditFlow.ApproveMain', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.approveMain',

	requires : [ 
	        'FHD.view.risk.riskDataEditFlow.RiskEditIdeaApprove',
	        'FHD.ux.icm.common.FlowTaskBar'
	],

	reloadData : function() {

	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		me.id = 'approveMainId';
		me.quassessManTitle = Ext.widget('panel', {
			border : false,
			collapsible : true,
			region : 'north',
			collapsed : true,
			items : Ext.widget('flowtaskbar', {
				jsonArray : [ 
				{
					index : 1,
					context : '1.修改意见',
					status : 'done'
				}, {
					index : 2,
					context : '2.部门审批',
					status : 'current'
				}, {
					index : 3,
					context : '3.归档',
					status : 'undo'
				} ],
				margin : '5 5 5 5'
			})
		});
		
		me.riskEditIdeaApprove = Ext.widget('riskEditIdeaApprove',{approveMan : me, isDel : true});
		
		me.ideaApproval=Ext.create('FHD.view.comm.bpm.ApprovalIdea',{
			region : 'south',
			executionId:me.executionId
		});
		
		Ext.apply(me, {
			border : false,
			layout: {
    	        type: 'border',
    	        padding: '0 0 5	0'
    	    },
			items : [me.quassessManTitle, me.riskEditIdeaApprove, me.ideaApproval],
			bbar : {
				items : ['->',{
					text : '提交',
					iconCls : 'icon-operator-submit',
					handler : function() {
						Ext.MessageBox.show({
				    		title : '提示',
				    		width : 260,
				    		msg : '确认提交吗？',
				    		buttons : Ext.MessageBox.YESNO,
				    		icon : Ext.MessageBox.QUESTION,
				    		fn : function(btn) {
				    			if (btn == 'yes') {//确认删除
				    				Ext.Ajax.timeout = 1000000;
									me.body.mask("提交中...","x-mask-loading");
									FHD.ajax({
			    			            url: __ctxPath + '/riskTowWork.f',
			    			            params: {
			    			            	executionId : me.executionId,
			    			            	examineApproveIdea : me.ideaApproval.getValue(),
			    			            	isPass : me.ideaApproval.isPass
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
				}]
			}
		});

		me.callParent(arguments);
	}

});