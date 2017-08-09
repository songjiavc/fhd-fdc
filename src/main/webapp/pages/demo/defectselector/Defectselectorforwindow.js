Ext.define('FHD.demo.defectselector.Defectselectorforwindow', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Defectselectorforwindow',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var button = Ext.create('Ext.Button', {
							text : '缺陷弹窗',
							handler : function() {
								var defectforwindowselector = Ext
										.create(
												'FHD.ux.icm.defect.DefectSelectorWindow',
												{
													multiSelect : true,
													modal : true,
													onSubmit : function(win) {

													}
												}).show();
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
											columnWidth : .2
										},
										layout : {
											type : 'column'
										},
										title : '缺陷弹窗选择',
										items : [button]
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