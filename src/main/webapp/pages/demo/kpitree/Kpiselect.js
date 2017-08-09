Ext.define('FHD.demo.kpitree.Kpiselect', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Kpiselect',
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
										title : '指标选择',
										items : [{
													labelWidth : 80,
													columnWidth : .5,
													gridHeight : 100,
													btnHeight : 100,
													btnWidth : 10,
													multiSelect : false,
													// fieldValue:'4a3c8b74-d594-43c8-a8b9-e49fdcb70885',
													xtype : 'kpioptselector'
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