Ext.define('FHD.demo.risk.RiskTree', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.RiskTree',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
				var extraParams = {
					canChecked : false
				};
				var panel = Ext.widget('riskstrategymaptree', {
							width : 220,
							height : 400,
							myRiskTreeVisible : false,
							OrgRiskTreeVisible : false,
							riskTreeVisible : true,
							rbsVisible : false,
							extraParams : extraParams,
							riskClickFunction : function(node) {
								alert(node);
							}
						});

				Ext.applyIf(me, {
							autoScroll : true,
							border : false,
							items : [panel]
						});
				me.callParent(arguments);
			}
		})