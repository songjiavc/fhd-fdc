Ext.define('FHD.demo.FileUpload.FileUpLoad', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.FileUpLoad',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		var indexMainPanel = Ext.getCmp('indexMainPanel');
		var filepanel = Ext.create('Ext.form.Panel', {
			bodyPadding : 5,
			height : FHD.getCenterPanelHeight(),
			items : [{
				xtype : 'fieldset',
				defaults : {
					columnWidth : 1 / 2,
					margin : '5 50 5 50'
				},// 每行显示一列，可设置多列
				layout : {
					type : 'column'
				},
				bodyPadding : 5,
				collapsed : false,
				collapsible : false,
				title : '多选',
				items : [{
					xtype : 'FileUpload',
					name : 'fileIds1',// 名称
					showModel : 'base',// 显示模式
					value : 'f1545902-6bef-49c3-a15a-751530b7e078,f4b4801e-4081-4f80-a3d2-dbd06d19fa77',// 初始化参数
					labelText : $locale('fileupdate.labeltext'),// 标题名称
					labelAlign : 'right'
					// 标题对齐方式
				}, {
					xtype : 'FileUpload',
					name : 'fileIds2',
					showModel : 'base',
					windowModel : 'selector',
					value : 'f1545902-6bef-49c3-a15a-751530b7e078,f4b4801e-4081-4f80-a3d2-dbd06d19fa77',
					labelAlign : 'right'
				}, {
					xtype : 'FileUpload',
					name : 'fileIds3',
					showModel : 'grid',
					value : 'f1545902-6bef-49c3-a15a-751530b7e078,f4b4801e-4081-4f80-a3d2-dbd06d19fa77',
					labelAlign : 'right'
				}, {
					xtype : 'FileUpload',
					name : 'fileIds4',
					showModel : 'grid',
					windowModel : 'selector',
					value : 'f1545902-6bef-49c3-a15a-751530b7e078,f4b4801e-4081-4f80-a3d2-dbd06d19fa77',
					labelText : $locale('fileupdate.labeltext'),
					labelAlign : 'right'
				}]
			}]
		});
		if (typeof(indexMainPanel.tree) != 'undefined') {
			indexMainPanel.tree.on('collapse', function(p) {
						filepanel.setWidth(indexMainPanel.getWidth() - 26 - 5);
					});
			indexMainPanel.tree.on('expand', function(p) {
				filepanel
						.setWidth(indexMainPanel.getWidth() - p.getWidth() - 5);
			});
			indexMainPanel.on('resize', function(p) {
						filepanel.setHeight(p.getHeight() - 5);
						if (tree.collapsed) {
							filepanel.setWidth(p.getWidth() - 26 - 5);
						} else {
							filepanel.setWidth(p.getWidth()
									- indexMainPanel.tree.getWidth() - 5);
						}
					});
			indexMainPanel.tree.on('resize', function(p) {
						if (p.collapsed) {
							filepanel.setWidth(indexMainPanel.getWidth() - 26
									- 5);
						} else {
							filepanel.setWidth(indexMainPanel.getWidth()
									- p.getWidth() - 5);
						}
					});
		}
		Ext.applyIf(me, {
					autoScroll : true,
					border : false,
					items : [filepanel]
				});
		me.callParent(arguments);
	}

})