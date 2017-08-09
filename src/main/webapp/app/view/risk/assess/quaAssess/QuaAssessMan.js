/**
 * 
 * 定性评估主面板
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessMan', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.quaAssessMan',

	requires : [ 
	        'FHD.view.risk.assess.quaAssess.QuaAssessPanel',
			'FHD.view.risk.assess.utils.GridCells',
			'FHD.ux.icm.common.FlowTaskBar'
	],

	reloadData : function() {

	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		me.id = 'QuaAssessManId';
		
		Ext.apply(me, {
			border : false,
			margin : '0 0 0 0'
		});

		me.callParent(arguments);
		
		FHD.ajax({
            url: __ctxPath + '/access/approval/findStrApproval.f',
            params: {
            	executionId : me.executionId
            },
            callback: function (data) {
                if (data && data.success) {
                		me.quassessManTitle = Ext.widget('panel', {
                			border : false,
                			collapsible : true,
                			collapsed : true,
                			items : Ext.widget('flowtaskbar', {
                				jsonArray : [ {
                					index : 1,
                					context : '1.计划制定',
                					status : 'done'
                				}, {
                					index : 2,
                					context : '2.计划审批',
                					status : 'done'
                				}, {
                					index : 3,
                					context : '3.任务分配',
                					status : 'done'
                				}, {
                					index : 4,
                					context : '4.风险评估',
                					status : 'current'
                				}, {
                					index : 5,
                					context : '5.任务审批',
                					status : 'undo'
                				}, {
                					index : 6,
                					context : '6.结果整理',
                					status : 'undo'
                				} ],
                				margin : '5 5 5 5'
                			})
                		});
                	
                	
                	me.add(me.quassessManTitle);
                	
                	FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/findAssessName.f',
			            params: {
			            	assessPlanId : me.businessId
			            },
			            callback: function (data) {
			                if (data && data.success) {
			                	var assessPlanName = data.assessPlanName;
			                	me.quassessManTitle.setTitle('计划名称:' + assessPlanName);
			                } 
			            }
			        });
                	
                	me.quaAssessPanel = Ext.widget('quaAssessPanel', {
            			executionId : me.executionId,
            			businessId : me.businessId,
            			quaAssessMan : me,
            			winId : me.winId
            		});
                	me.add(me.quaAssessPanel);
                } 
            }
        });
	}
});