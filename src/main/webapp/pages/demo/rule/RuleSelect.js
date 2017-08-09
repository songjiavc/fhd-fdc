Ext.define('FHD.demo.rule.RuleSelect', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.RuleSelect',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		var panel = Ext.create('Ext.form.Panel', {
			id : 'formdemo',
			bodyPadding : 25,
			autoScroll : true,
			// layout:'fit',
			height : FHD.getCenterPanelHeight(),
			items : [{
				xtype : 'fieldset',
				defaults : {
					columnWidth : 1 / 1
				},// 每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				bodyPadding : 5,
				collapsed : false,
				collapsible : false,
				title : $locale('fhd.icm.rule.ruleSelectorLabelText'),
				items : [{
							columnWidth : 1,
							xtype : 'ruleselector',
							name : 'kpiIds',
							value : '10,11',
							height : 100,
							multiSelect : true,
							labelText : $locale('fhd.icm.rule.ruleSelectorLabelText'),
							labelAlign : 'right',
							labelWidth : 80
						}, {
							columnWidth : 1,
							xtype : 'ruleselector',
							name : 'kpiIds',
							multiSelect : false,
							value : '10,11',
							height : 100,
							labelText : $locale('fhd.icm.rule.ruleSelectorLabelText'),
							labelAlign : 'right',
							labelWidth : 80
						}

				]
			}]
		});
		FHD.componentResize(panel, 200, 0);
		Ext.applyIf(me, {
					autoScroll : true,
					border : false,
					items : [panel]
				});
		me.callParent(arguments);

	}
})