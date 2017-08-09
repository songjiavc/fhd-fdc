Ext.define('FHD.view.sys.sfindex.RiskKpiReportMainPanel', {
    extend: 'Ext.panel.Panel',
    layout: 'fit',
    initComponent: function () {
        var me = this;
        me.cardPanel = Ext.create('FHD.ux.CardPanel', {
            items: [],
            border: false
        });
        if (!me.groupGridPanel) {
            me.groupGridPanel = Ext.create('FHD.view.sys.sfindex.RiskReportGroupGridPanel');
        }
        me.cardPanel.add(me.groupGridPanel);
        // 添加搜索框
        me.searchField = Ext.create('Ext.ux.form.SearchField', {
            width: 150,
            paramName: 'query',
            store: me.cardPanel.getActiveItem().getStore(),
            emptyText: FHD.locale.get('searchField.emptyText')
        });
        Ext.apply(me, {
            items: [me.cardPanel],
            border: false,
            tbar: [{
                    text: '导出',
                    tooltip: '导出',
                    iconCls: 'icon-ibm-action-export-to-excel',
                    handler: function () {
                        me.exportExcel();
                    }
                }, {
                    xtype: 'combobox',
                    store: Ext.create('Ext.data.ArrayStore', {
                        fields: ['reportStype'],
                        data: 
                        	[['分组展示'],['列表展示']]
                        
                    }),
                    displayField: 'reportStype',
                    typeAhead: true,
                    mode: 'local',
                    width: 100,
                    forceSelection: true,
                    triggerAction: 'all',
                    //emptyText:$locale('FHDGrid.paging.comb.emptyText'),
                    value: '分组展示',
                    selectOnFocus: true,
			        listeners:{
			        	select:function(c,r,o){
			        		if(r[0].data.reportStype == '列表展示') {
			        			 if(!me.gridPanel) {
			        				 me.gridPanel = Ext.create('FHD.view.sys.sfindex.RiskReportGridPanel');
			        				 me.cardPanel.setActiveItem(me.gridPanel);	
			        			 } 
			        			 me.cardPanel.setActiveItem(me.gridPanel);
			        			 me.reLoadData();
			        			 me.searchField.setVisible(false);
			        		} else if(r[0].data.reportStype == '分组展示') {
			        			me.searchField.setVisible(true);
			        			me.cardPanel.setActiveItem(0);
			        			me.reLoadData();
			        			me.groupGridPanel.store.remoteFilter = true;
			        			me.searchField.store = me.groupGridPanel.store;
			        			me.searchField.url  = me.groupGridPanel.url;				        			
			        		}
			        	}
			        }
                    
                },
                '->', me.searchField
            ]
        });

        me.callParent(arguments);
    },
    reLoadData:function() {
		 var me = this;
		 me.cardPanel.getActiveItem().reLoadData();
	 },
	exportExcel: function() {
        var me=this;
        if(!me.gridPanel) {
        	 me.gridPanel = Ext.create('FHD.view.kpi.report.risk.RiskReportGridPanel');
        }
        me.gridPanel.exportExcel();
	 },
});