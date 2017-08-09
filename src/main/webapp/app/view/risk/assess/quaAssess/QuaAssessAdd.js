/**
 * 
 * 风险整理表单
 */

Ext.define('FHD.view.risk.assess.quaAssess.QuaAssessAdd', {
	extend : 'Ext.form.Panel',
	alias : 'widget.quaAssessAdd',

	load : function() {
		var me = this;
		me.addShortForm = Ext.create('FHD.view.risk.cmp.RiskAddForm', {
			type:'re',	//如果是re,上级风险只能选择叶子节点
			border:false,
			state : '2',			
			hiddenSaveBtn:true,
			callback:function(data){
				if(data.id != null){
					FHD.ajax({
			            url: __ctxPath + '/assess/quaassess/saveTaskByEmpId.f',
			            params: {
			            	assessPlanId : Ext.getCmp('QuaAssessManId').businessId,
			            	riskId : data.id
			            },
			            callback: function (data) {
			            	Ext.MessageBox.alert('修改信息','修改成功');
			            	me.form.close();
			            	me.grids.store.load();
			            	me.grids.riskDatas = [];
			            }
			        });
				}
			}
		});
		
		me.objectDeptEmpIdHide = {
				xtype : 'hidden',
    			rows : 4,
    			name : 'objectDeptEmpId',
    			//value : objectDeptEmpId,
    			margin : '5 0 3 20',
    			columnWidth : 1
		},

		me.add(me.addShortForm);
	},
	
	getFieldSetAssess : function(result, type){
		var me = this;
		
		me.assessFChart = Ext.widget('assessFChart');
		me.assessFChart.load(result, type);
		
		me.fieldSetAssess = {
				xtype : 'fieldset',
				title : '定性评估',
				margin : '5 5 5 5',
				items : [ me.assessFChart ]
		};
		
		return me.fieldSetAssess;
	},

	// 初始化方法
	initComponent : function() {
		var me = this;

		Ext.apply(me, {
			border : false,
			region : 'center',
			layout : {
				align : 'stretch',
				type : 'vbox'
			},
			collapsible:true
		});

		me.callParent(arguments);
	}
});