Ext.define('FHD.demo.assessplanselector.Assessplanselector', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.assessplanselector',
	
	autoScroll : true,
	border : false,
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		
		var assessplanselector = Ext.create('FHD.ux.icm.assess.AssessPlanSelector', {
			fieldLabel : '评价计划',
			name : 'assessplanId',
			multiSelect : true,
			modal : true,
			onSubmit : function(win) {

			}
		});

		// 表单panel
		var form = Ext.create("Ext.form.Panel", {
			autoScroll : true,
			border : false,
			bodyPadding : "5 5 5 5",
			items : [{
				xtype : 'fieldset',// 基本信息fieldset
				collapsible : false,
				defaults : {
					margin : '7 30 3 30',
					columnWidth : .5
				},
				layout : {
					type : 'column'
				},
				title : '评价计划选择',
				items : [assessplanselector]
			}]
		});
		Ext.applyIf(me, {
			items : [form]
		});
		me.callParent(arguments);
	}
});