Ext.define('FHD.demo.strategymaptree.StrategyMapselect', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.StrategyMapselect',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;
		var panel = Ext.create('Ext.form.Panel', {
			bodyPadding : 5,
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
				title : '目标选择',
				items : [{
							columnWidth : 1,
							xtype : 'kpistrategymapselector',
							name : 'kpiStrategyMapIds',
							extraParams : {
								smIconType : 'display',
								canChecked : true
							},
							single : false,
							height : 100,
							// value:'3,4',
							labelText : $locale('kpistrategymapselector.labeltext'),
							labelAlign : 'right',
							labelWidth : 80
						}]
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