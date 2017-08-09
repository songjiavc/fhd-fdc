/**
 * 
 * 保密部门领导审批意见
 * 郭鹏
 * 20170521
 */
Ext.define('FHD.view.risk.assess.utils.DeptLeadIdeaSecrecy', {
	extend : 'Ext.form.Panel',
	alias : 'widget.deptLeadIdeaSecrecy',
	
	load : function(scoreObjectId, assessPlanId, riskId) {
		var me = this;
		FHD.ajax({
			async : false,
            url: __ctxPath + '/assess/quaassess/findDeptLeadIdea.f',
            params: {
            	scoreObjectId : scoreObjectId,
            	assessPlanId : assessPlanId
            },
            callback: function (data) {
            	if(data.success == true){
	            	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
	            		autoScrollas : true,
	        			height : 500,
	        			riskId : riskId,
	        			objectId:scoreObjectId
	        		});
	            	
	        		me.editIdea = Ext.widget('textareafield', {
	        			xtype : 'textareafield',
	        			rows : 4,
	        			name : 'editIdeaContent',
	        			value : data.editIdeaContent,
	        			margin : '5 0 3 20',
	        			columnWidth : 1
	        		});
	        		me.responseIdea = Ext.widget('textareafield', {
	        			rows : 4,
	        			name : 'responseIdea',
	        			value : data.responseIdea,
	        			margin : '5 0 3 20',
	        			columnWidth : 1
	        		});
	        		me.DeptIds = Ext.widget('hidden',{
	        			name : 'deptLeadCircuseeId',
	        			value : data.deptLeadCircuseeId
	        		});
	        		me.editIdeaFieldSet = {
	        				xtype : 'fieldset',
	        				collapsible : true,
	        				title : '修改意见',
	        				margin : '10 10 10 10',
	        				layout: {
	        					 type: 'column'
	        				},
	        				items : [ me.editIdea , me.DeptIds]
	        		},
	        		me.responseIdeaFieldSet = {
	        				xtype : 'fieldset',
	        				collapsible : true,
	        				title : '应对意见',
	        				margin : '10 10 10 10',
	        				layout: {
	        					 type: 'column'
	        				},
	        				items : [ me.responseIdea , me.DeptIds]
	        		};
	        		me.detailAllForm.add(me.editIdeaFieldSet,me.responseIdeaFieldSet);
	        		me.add(me.detailAllForm);
	            }
            }
        });
	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		
		Ext.apply(me, {border : false, autoScroll : true});

		me.callParent(arguments);
	}
});