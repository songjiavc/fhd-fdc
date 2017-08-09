Ext.define('FHD.view.kpi.datasource.kpiDataSourceMainPanel', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.kpiMainPanel',
			requires : ['FHD.view.kpi.datasource.kpiDataSourceFormPanel',
					'FHD.view.kpi.datasource.kpiDataSourceTreePanel'],
			initComponent : function() {
				var me = this;
				me.treePanel = Ext.widget("treePanel");
				me.editPanel = Ext.widget("formEditPanel");
				Ext.apply(me, {
							border : false,
							layout : {
								type : 'border',
								padding : '0 0 5 0'
							},
							items : [me.treePanel, me.editPanel]
						})
				me.callParent(arguments);
				me.editPanel.on('resize', function(p) {
							me.editPanel.setHeight(FHD.getCenterPanelHeight());
						});
			}
		})