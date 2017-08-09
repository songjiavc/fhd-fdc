/**
 * 
 * 部门领导审批意见
 */
Ext.define('FHD.view.risk.assess.utils.DeptLeadIdea', {
	extend : 'Ext.form.Panel',
	alias : 'widget.deptLeadIdea',
	
	load : function(scoreObjectId, assessPlanId, riskId) {
		var me = this;
		FHD.ajax({
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
	        		
	        		me.DeptIds = {
	        				xtype : 'hidden',
		        			rows : 4,
		        			name : 'deptLeadCircuseeId',
		        			value : data.deptLeadCircuseeId,
		        			margin : '5 0 3 20',
		        			columnWidth : 1
	        		},
	        		
	        		me.editIdeaFieldSet = {
	        				xtype : 'fieldset',
	        				collapsible : true,
	        				title : '修改意见',
	        				margin : '10 10 10 10',
	        				layout: {
	        					 type: 'column'
	        				},
	        				items : [ me.editIdea , me.DeptIds]
	        		};
	        		
	        		me.detailAllForm.add(me.editIdeaFieldSet);
	        		
	        		me.responseIdea = Ext.widget('textareafield', {
	        			rows : 4,
	        			name : 'responseIdeaContent',
	        			value : data.responseIdea,
	        			margin : '5 0 3 20',
	        			columnWidth : 1
	        		});
	        		
	        		me.responseIdeaFieldSet = {
	        				xtype : 'fieldset',
	        				collapsible : true,
	        				title : '应对意见',
	        				margin : '10 10 10 10',
	        				layout: {
	        					 type: 'column'
	        				},
	        				items : [ me.responseIdea]
	        		};
	        		
	        		me.detailAllForm.add(me.responseIdeaFieldSet);
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