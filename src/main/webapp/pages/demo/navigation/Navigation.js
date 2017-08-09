Ext.define('FHD.demo.navigation.Navigation', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.Navigation',
	/**
	 * 初始化页面组件
	 */
	initComponent : function() {
		var me = this;

		var navigationBar = Ext.create('FHD.ux.NavigationBars');
		var nav = {
			xtype : 'box',
			height : 40,
			style : 'border-left: 1px  #99bce8 solid;',
			html : '<div id="gatherresulttable2NavDiv" class="navigation"></div>',
			listeners : {
				afterrender : function() {
					navigationBar.renderHtml('gatherresulttable2NavDiv',
							'eda8ffeab0da4159be0ff924108e3883MB0011', '', 'sm');
				}
			}
		};
		Ext.applyIf(me, {
					autoScroll : true,
					height : 500,
					border : false,
					items : [nav]
				});
		me.callParent(arguments);
	}
})