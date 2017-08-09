Ext.define('FHD.demo.kpitree.Kpitree', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Kpitree',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var panel = Ext.create('FHD.ux.kpi.KpiTree', {
							//id : 'formdemo',
							width : 300,
							height : 500,
							autoScroll : true,
							single : false,
							extraParams : {
								canChecked : false
							},
							/* 树节点点击事件 */
							clickFunction : function(node) {
							},
							onCheckchange : function(item, check) {
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