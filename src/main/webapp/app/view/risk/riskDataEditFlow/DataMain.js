/**
 * 
 * 定性评估主面板
 */

Ext.define('FHD.view.risk.riskDataEditFlow.DataMain', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.dataMain',

	requires : [
	        'FHD.view.risk.riskDataEditFlow.RiskEditIdeaApprove',
	        'FHD.ux.icm.common.FlowTaskBar'
	],

	reloadData : function() {

	},

	del : function(){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection()[0];//得到选中的记录
    	var riskId = selection.get('empRelaRiskIdeaId');
    	
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
    },
	
	// 初始化方法
	initComponent : function() {
		var me = this;
		me.id = 'dataMainId';
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
					status : 'done'
				}, {
					index : 3,
					context : '3.归档',
					status : 'current'
				} ],
				margin : '5 5 5 5'
			})
		});
		
		me.riskEditIdeaApprove = Ext.widget('riskEditIdeaApprove',{dataMain : me, isDel : false});
		
		Ext.apply(me, {
			border : false,
			layout: {
    	        type: 'border',
    	        padding: '0 0 5	0'
    	    },
			items : [me.quassessManTitle, me.riskEditIdeaApprove],
			bbar : {
				items : ['->',{
					text : '结束',
					iconCls : 'icon-operator-submit',
					handler : function() {
						Ext.MessageBox.show({
				    		title : '提示',
				    		width : 260,
				    		msg : '确认结束吗？',
				    		buttons : Ext.MessageBox.YESNO,
				    		icon : Ext.MessageBox.QUESTION,
				    		fn : function(btn) {
				    			if (btn == 'yes') {//确认删除
				    				Ext.Ajax.timeout = 1000000;
									me.body.mask("提交中...","x-mask-loading");
									FHD.ajax({
			    			            url: __ctxPath + '/endWork.f',
			    			            params: {
			    			            	executionId : me.executionId,
			    			            	empRelaRiskIdeaIdDatas : Ext.JSON.encode(me.riskEditIdeaApprove.empRelaRiskIdeaIdDatas)
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