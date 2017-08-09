Ext.define('FHD.view.kpi.report.SmKpiReportMainPanel',{
	 extend: 'Ext.panel.Panel',
	 layout: 'fit',
	 initComponent : function() {
		 var me = this;
		 if(!me.gridPanel) {
			 me.gridPanel = Ext.create('FHD.view.kpi.report.SmKpiReport');
		 }
		 if(!me.treePanel) {
			 me.treePanel = Ext.create('FHD.view.kpi.report.SmKpiReportTreePanel',{
				 border: false
			 });
		 }
		 if(!me.groupGridPanel) {
			 me.groupGridPanel = Ext.create('FHD.view.kpi.report.SmKpiReportGroupGridPanel',{
				 border: false
			 });
		 }
		 me.cardPanel = Ext.create('FHD.ux.CardPanel',{
			 items:[me.groupGridPanel,me.gridPanel,me.treePanel],
			 border: false
		 });
			// 添加搜索框
	       me.searchField = Ext.create('Ext.ux.form.SearchField', {
						width : 150,
						paramName: 'query',
						store:me.cardPanel.getActiveItem().getStore(),
						emptyText : FHD.locale.get('searchField.emptyText')
			});
		 Ext.apply(me,{
			 items: [me.cardPanel], 
			 border:false,
			 tbar:[{
	    			text : '导出',
	    			tooltip: '导出',
	    			iconCls : 'icon-ibm-action-export-to-excel',
	    			handler:function(){
	    				 me.exportExcel();
	    			}
				},{
						xtype:'combobox',
						store: Ext.create('Ext.data.ArrayStore',{
							fields: ['reportStype'],
	        				data : [['分组展示'],['树形展示'],['列表展示']]
						}),
						displayField:'reportStype',
				        typeAhead: true,
				        mode: 'local',
				        width:100,
				        forceSelection: true,
				        triggerAction: 'all',
				        value:'分组展示',
				        selectOnFocus:true,
				        listeners:{
				        	select:function(c,r,o){
				        		if(r[0].data.reportStype == '列表展示') {
				        			me.cardPanel.setActiveItem(1);
				        			me.searchField.store = me.gridPanel.store;
				        			me.searchField.url  = me.gridPanel.url;
				        			 me.searchField.setDisabled(false);
				        			me.reLoadData();
				        			 me.searchField.setVisible(false);
				        		} else if(r[0].data.reportStype == '树形展示'){
				        			me.cardPanel.setActiveItem(2);
				        			me.searchField.setDisabled(false);
				        			me.searchField.store = me.treePanel.treegrid.store;
				        			me.searchField.url = me.treePanel.treegrid.url;
				        			me.reLoadData();	
				        			me.searchField.setVisible(true);
				        		} else if(r[0].data.reportStype == '分组展示') {
				        			me.cardPanel.setActiveItem(0);
				        			me.groupGridPanel.store.remoteFilter = true;
				        			me.searchField.setVisible(true);
				        			me.reLoadData();				        			
				        		}
				        	}
				        }
					},
					'->',me.searchField
					
				]
		 });

		 me.cardPanel.setActiveItem(0);
	     me.callParent(arguments);
	 },
	 exportExcel: function() {
	        var me=this;
	        me.gridPanel.exportExcel();
	 },
	 reLoadData:function() {
		 var me = this;
		 me.cardPanel.getActiveItem().reLoadData();
	 }
})