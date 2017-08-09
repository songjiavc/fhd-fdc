Ext.define('FHD.demo.rectifyplanselector.Rectifyplanselector', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Rectifyplanselector',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var rectifyplanselector = Ext.create(
						'FHD.ux.icm.rectify.ImproveSelector', {
							fieldLabel : '整改计划',
							name : 'rectifyplanId'
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
										title : '整改计划选择',
										items : [rectifyplanselector]
									}]
						});

				Ext.applyIf(me, {
							autoScroll : true,
							border : false,
							items : [form]
						});
				me.callParent(arguments);

			}
		})