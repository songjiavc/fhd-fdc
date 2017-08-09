Ext.define('FHD.demo.process.Processselect', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Processselect',
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
								title : $locale('fhd.process.processselector.title'),
								items : [
										Ext
												.create(
														'FHD.ux.process.ProcessSelector',
														{
															columnWidth : 1,
															name : 'kpiIds',
															single : false,
															parent : false,
															height : 100,
															fieldLabel : '流程选择',
															labelAlign : 'left',
															labelWidth : 80
														}),
										Ext
												.create(
														'FHD.ux.process.processSelector',
														{
															columnWidth : 1,
															name : 'kpiIds',
															multiSelect : false,
															height : 30,
															fieldLabel : '流程选择',
															labelAlign : 'left',
															labelWidth : 80
														})]
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