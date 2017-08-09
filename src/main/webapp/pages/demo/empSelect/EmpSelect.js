Ext.define('FHD.demo.empSelect.EmpSelect', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.empSelect',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		var empmany = Ext.create('FHD.ux.empSelector.EmpSelector', {
					fieldLabel : '员工多选',
					labelAlign : 'right',
					subCompany : true,// 显示子公司
					multiSelect : true
				});
		var empone = Ext.create('FHD.ux.empSelector.EmpSelector', {
					fieldLabel : '员工单选',
					labelAlign : 'right',
					subCompany : true,// 显示子公司
					multiSelect : false
				});
		var indexMainPanel = Ext.getCmp('indexMainPanel');
		var emppanel = Ext.create('Ext.form.Panel', {
			bodyPadding : '0 3 3 3',
			useArrows : true,
			height : FHD.getCenterPanelHeight() - 5,
			items : [{
				xtype : 'fieldset',
				defaults : {
					columnWidth : 1 / 2,
					labelWidth : 100,
					margin : '7 10 0 30'
				},// 每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				collapsed : false,
				collapsible : false,
				title : '员工选择',
				items : [empmany,empone]
			}]
		});
		indexMainPanel.tree.on('collapse', function(p) {
					emppanel.setWidth(panel.getWidth() - 26 - 5);
				});
		indexMainPanel.tree.on('expand', function(p) {
					emppanel.setWidth(panel.getWidth() - p.getWidth() - 5);
				});
		indexMainPanel.on('resize', function(p) {
					emppanel.setHeight(p.getHeight() - 5);
					if (indexMainPanel.tree.collapsed) {
						emppanel.setWidth(p.getWidth() - 26 - 5);
					} else {
						emppanel.setWidth(p.getWidth()
								- indexMainPanel.tree.getWidth() - 5);
					}
				});
		indexMainPanel.tree.on('resize', function(p) {
			if (p.collapsed) {
				emppanel.setWidth(indexMainPanel.getWidth() - 26 - 5);
			} else {
				emppanel.setWidth(indexMainPanel.getWidth() - p.getWidth() - 5);
			}
		});
		Ext.applyIf(me, {
					autoScroll : true,
					border : false,
					items : [emppanel]
				});
		me.callParent(arguments);
	}
})