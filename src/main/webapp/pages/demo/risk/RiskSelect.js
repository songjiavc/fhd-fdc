Ext.define('FHD.demo.risk.RiskSelect', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.RiskSelect',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var panel = Ext.widget('riskstrategymapselector', {
							name : 'kpiStrategyMapIds',
							single : false,
							height : 100,
							value : '3,4',
							labelText : $locale('riskstrategymapselector.labeltext'),
							labelAlign : 'right',
							labelWidth : 80
						});
				Ext.applyIf(me, {
							autoScroll : true,
							border : false,
							items : [panel]
						});
				me.callParent(arguments);
			}
		})