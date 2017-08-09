Ext.define('FHD.demo.defectselector.Defectselector', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Defectselector',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var defectselector = Ext.create(
						'FHD.ux.icm.defect.DefectSelector', {
							fieldLabel : '缺陷',
							name : 'defectId'
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
										title : '缺陷选择',
										items : [defectselector]
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