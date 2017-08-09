Ext.define('FHD.demo.deptAndEmpSelect.DeptAndEmpSelect', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.Processselect',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		var indexMainPanel = Ext.getCmp('indexMainPanel');;
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
				title : '机构选择',
				items : [Ext.create('FHD.ux.org.CommonSelector', {
					fieldLabel : '机构多选',
					labelAlign : 'right',
					type : 'dept',
					subCompany : true,// 显示子公司
					companyOnly : false,// 显示公司和部门
					rootVisible : true,// 显示根机构
					value : '[{"id":"13c2667cdfe444d99c0625cbec215375"},{"id":"0e5254f249e74d63be576c8b8076c4ca"}]',
					multiSelect : true
				}), Ext.create('FHD.ux.org.CommonSelector', {
							fieldLabel : '机构单选',
							labelAlign : 'right',
							type : 'dept',
							value : '[{"id":"13c2667cdfe444d99c0625cbec215375"}]',
							multiSelect : false
						})]
			}, {
				xtype : 'fieldset',
				defaults : {
					columnWidth : 1 / 2,
					labelWidth : 100,
					margin : '7 10 0 30'
				},// 每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				bodyPadding : 5,
				collapsed : false,
				collapsible : false,
				title : '员工选择',
				items : [Ext.create('FHD.ux.org.CommonSelector', {
									fieldLabel : '员工多选',
									labelAlign : 'right',
									subCompany : true,
									rootVisible : true,// 显示根机构
									type : 'emp',
									multiSelect : true
								}), Ext.create('FHD.ux.org.CommonSelector', {
									fieldLabel : '员工单选',
									labelAlign : 'right',
									type : 'emp',
									multiSelect : false
								})]
			}, {
				xtype : 'fieldset',
				defaults : {
					columnWidth : 1 / 2,
					labelWidth : 100,
					margin : '7 10 0 30'
				},// 每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				bodyPadding : 5,
				collapsed : false,
				collapsible : false,
				title : '员工选择',
				items : [Ext.create('FHD.ux.org.CommonSelector', {
									fieldLabel : '机构员工多选',
									labelAlign : 'right',
									type : 'dept_emp',
									subCompany : true,
									multiSelect : true
								}), Ext.create('FHD.ux.org.CommonSelector', {
									fieldLabel : '机构员工单选',
									labelAlign : 'right',
									type : 'dept_emp',
									multiSelect : false
								})]
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