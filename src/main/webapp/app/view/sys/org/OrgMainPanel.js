Ext.define('FHD.view.sys.org.OrgMainPanel', {
    extend: 'Ext.container.Container',
    alias: 'widget.OrgMainPanel',
    requires:[
    	'FHD.view.sys.organization.import.OrgImpFormPanel',
    	'FHD.view.sys.import.RiskImportFrom'
    ],
    frame: false,
    // 布局
    layout: {
        type: 'border'
    },
    border : false,
    importMenuPanel:null,
    importCenterPanel:null,
    // 初始化方法
    initComponent: function() {
		var me = this;
		me.importMenuPanel = Ext.create('FHD.ux.MenuPanel',{
			region : 'west',
		    split:true,
		    width: 150,
		    collapsible: false,
		    autoScroll:true,
		    items:[{
		        text: '组织机构',
		        iconCls:'icon-btn-execute',
		        scale: 'large',
				iconAlign: 'top',
				cls:'menu-selected-btn menu-btn',
				handler:function(){	
					me.importCenterPanel.removeAll(true);
					var orgImpFormPanel = Ext.widget('orgimpformpanel');
					me.importCenterPanel.add(orgImpFormPanel);
				}
		    },{
		        text: '风险',
		        iconCls:'icon-btn-risk',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					me.importCenterPanel.removeAll(true);
					var riskImportFrom = Ext.widget('riskimportfrom');
					me.importCenterPanel.add(riskImportFrom);
				}
		    },{
		        text: '流程',
		        iconCls:'icon-ibm-icon-diagram-32',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					me.importCenterPanel.removeAll(true);
					var processImportFrom = Ext.create('FHD.view.icm.import.ProcessImportPanel');
					me.importCenterPanel.add(processImportFrom);
				}
		    },{
		        text: '指标',
		        iconCls:'icon-ibm-action-order-20',
		        scale: 'large',
				iconAlign: 'top',
				handler:function(){	
					me.importCenterPanel.removeAll(true);
					var kpiImportFrom = Ext.create('FHD.view.kpi.export.kpitype.KpiTypeImportForm');
					me.importCenterPanel.add(kpiImportFrom);
				}
		    }]
		});
		me.importCenterPanel = Ext.create('Ext.panel.Panel',{
			region : 'center',
			border:false,
		    layout:'fit'
		});
		
		Ext.applyIf(me, {
		    items: [me.importMenuPanel,me.importCenterPanel]
		});
		me.callParent(arguments);
		var orgImpFormPanel = Ext.widget('orgimpformpanel');
		me.importCenterPanel.add(orgImpFormPanel);
    }
});