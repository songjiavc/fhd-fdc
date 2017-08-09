Ext.define('FHD.demo.kpitree.Kpitypeselect', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Kpitypeselect',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var panel = Ext.create('Ext.form.Panel', {
							id : 'formdemo',
							bodyPadding : 25,
							autoScroll : true,
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
										title : '指标类型选择',
										items : [{
													xtype : 'kpitypeselect',
													labelWidth : 70,
													btnHeight : 23
													,
												}]
									}

							]
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