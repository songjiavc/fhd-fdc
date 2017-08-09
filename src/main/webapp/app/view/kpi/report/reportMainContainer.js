Ext.define('FHD.view.kpi.report.reportMainContainer', {
	extend : 'Ext.container.Container',

	requires : [],

	border : false,

	title : '报表设计',


	layout : {
		align : 'stretch',
		type : 'vbox'
	},

	initComponent : function() {
		var me = this;


		me.reportView = Ext.create('FHD.view.kpi.report.reportView', {
			border : false,
			reportMainContainer : me,
			flex : 3
		});

		me.reportGrid = Ext.create('FHD.view.kpi.report.reportGrid', {
			flex : 2,
			hidden : true
		});

		Ext.apply(me, {
			title : me.title,
			height : FHD.getCenterPanelHeight(),
			width : FHD.getCenterPanelWidth(),
			items : [ me.reportView, me.reportGrid

			]
		});
		me.callParent(arguments);

	},
	/**
	 * 重新加载数据
	 */
	reloadData : function(record) {
		var me = this;
	}

});