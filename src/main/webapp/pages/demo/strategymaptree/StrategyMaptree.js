Ext.define('FHD.demo.strategymaptree.StrategyMaptree', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.StrategyMaptree',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var panel = Ext.create('FHD.ux.kpi.KpiStrategyMapTree', {
							//id : 'formdemo',
							width : 300,
							height : 500,
							extraParams : {
								canChecked : false
							},
							clickFunction : function(node) {
							},
							orgSmTreeContextMenuFc : function(tree, node) {
								var type = node.data.type;
								var menu;
								if (type === 'sm') {
									menu = Ext.create('Ext.menu.Menu', {
												items : [{
															text : '111'
														}, {
															text : '222'
														}]
											});
								}
								return menu;
							}
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