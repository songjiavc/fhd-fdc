Ext.define('FHD.demo.stepnavigator.StepNavigator', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.StepNavigator',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var p1 = Ext.create('Ext.panel.Panel', {
							navigatorTitle : '基本信息', // 步骤导航的名称，必须
							border : false,
							html : 'aa',
							last : function() {
								alert('save1');
							}
						});
				var p2 = Ext.create('Ext.panel.Panel', {
							navigatorTitle : '额外信息',
							border : false,
							html : 'bb',
							last : function() {
								alert('save2');
							}
						});
				var p3 = Ext.create('Ext.panel.Panel', {
							navigatorTitle : '更多信息',
							border : false,
							html : 'cc',
							last : function() {
								alert('save3');
							}
						});

				var basicPanel = Ext.create('FHD.ux.layout.StepNavigator', {
							items : [p1, p2, p3],
							undo : function() {
								alert('返回');
							}
						});
				Ext.applyIf(me, {
							autoScroll : true,
							border : false,
							items : [basicPanel]
						});
				me.callParent(arguments);

			}
		})