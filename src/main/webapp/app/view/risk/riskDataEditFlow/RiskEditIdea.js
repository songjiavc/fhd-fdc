/**
 * 
 * 风险编辑修改意见
 */

Ext.define('FHD.view.risk.riskDataEditFlow.RiskEditIdea', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskEditIdea',

	load : function(riskId, isEdit) {
		var me = this;
		if(isEdit){
			FHD.ajax({
	            url: __ctxPath + '/findEditIdea.f',
	            params: {
	            	riskId : riskId
	            },
	            callback: function (data) {
	            	if(data.success == true){
		            	me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
		        			height : 400,
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
		        		
		        		me.objectDeptEmpIdHide = {
		        				xtype : 'hidden',
			        			rows : 4,
			        			name : 'empRelaRiskIdeaId',
			        			value : data.empRelaRiskIdeaId,
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
		        		
		        		me.add(me.detailAllForm);
		            }
	            }
	        });
		}else{
			me.detailAllForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
				height : 360,
    			riskId : riskId
    		});
			
			me.add(me.detailAllForm);
		}
	},

	// 初始化方法
	initComponent : function() {
		var me = this;
		
		Ext.apply(me, {
//			border : false,
//			autoScroll: true,
//			region : 'center',
//			layout : {
//				align : 'stretch',
//				type : 'vbox'
//			}
		});

		me.callParent(arguments);
	}
});