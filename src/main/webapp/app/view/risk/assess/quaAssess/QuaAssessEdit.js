/**
 * 
 * 风险整理表单
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessEdit', {
	extend : 'Ext.form.Panel',
	alias : 'widget.quaAssessEdit',

//	requires : [ 'FHD.view.risk.assess.utils.AssessFChart' ],
	
	load : function(riskId, objectDeptEmpId) {
		var me = this;
		if(me.isEditIdea){
			FHD.ajax({
	            url: __ctxPath + '/assess/quaassess/findEditIdeaByObjectDeptEmpId.f',
	            params: {
	            	objectDeptEmpId : objectDeptEmpId
	            },
	            callback: function (data) {
	            	if(data.success == true){
		            	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
		        			objectId:me.objectId,
		        			riskId : riskId
		        		});
		
		        		me.editIdea = Ext.widget('textareafield', {
		        			xtype : 'textareafield',
		        			rows : 4,
		        			name : 'editIdeaContent',
		        			//fieldLabel : '',
		        			value : data.editIdeaContent,
		        			margin : '5 0 3 20',
		        			columnWidth : 1
		        		});
		        		
		        		me.responseText = Ext.widget('textareafield', {
		        			rows : 4,
		        			name : 'responseText',
		        			value :  data.responseText,
		        			margin : '5 0 3 20',
		        			columnWidth : 1
		        		});
		        		
		        		me.objectDeptEmpIdHide = {
		        				xtype : 'hidden',
			        			rows : 4,
			        			name : 'objectDeptEmpId',
			        			value : data.objectDeptEmpId,
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
		        				items : [ me.editIdea , me.objectDeptEmpIdHide]
		        		};
		        		
		        		me.detailAllForm.add(me.editIdeaFieldSet);
		        		
		        		me.responseFieldSet = {
		        				xtype : 'fieldset',
		        				collapsible : true,
		        				title : '应对措施',
		        				margin : '10 10 10 10',
		        				layout: {
		        					 type: 'column'
		        				},
		        				items : [me.responseText]
		        		};
		        		me.detailAllForm.add(me.responseFieldSet);
		        		
		        		me.add(me.detailAllForm);
		            }
	            }
	        });
		}else{
			me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
				height : 360,
    			riskId : riskId,
    			objectId:me.objectId
    		});
			
			me.add(me.detailAllForm);
		}
	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		
		//me.map = new me.Map();
		Ext.apply(me, {
			border : false,
			autoScroll: true,
			region : 'center',
			layout : {
				align : 'stretch',
				type : 'vbox'
			}
		});

		me.callParent(arguments);
	}
});