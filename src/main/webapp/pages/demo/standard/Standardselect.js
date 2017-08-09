Ext.define('FHD.demo.standard.Standardselect', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Standardselect',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var selector = Ext.create('FHD.ux.standard.StandardSelector', {
							columnWidth : 1,
							name : 'standardName1',
							multiSelect : true,// 多选，都带复选框，包括，父节点
							myType : 'standard',
							height : 100,
							labelText : $locale('standardSelector.labelText'),
							labelAlign : 'right',
							labelWidth : 80
						});
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
										title : '内控标准选择',
										items : [selector]
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