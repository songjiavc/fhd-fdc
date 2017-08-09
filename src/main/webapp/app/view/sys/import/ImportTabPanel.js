Ext.define('FHD.view.sys.import.ImportTabPanel', {
	extend: 'Ext.tab.Panel',
	alias: 'widget.importtabpanel',
	requires:['FHD.view.sys.organization.import.OrgImpFormPanel'],
	fileId:null,
	orgImpFormPanel:null,
	reloadData:function(){
		var me=this;
		me.orgImpFormPanel.reloadData();
	},
	// 初始化方法
	initComponent: function() {
		var me = this;
		me.orgImpFormPanel = Ext.widget('orgimpformpanel',{
			title:"组织机构导入"
		});
		Ext.apply(me, {
			deferredRender: false,
			activeTab: 0,
			plain: true,
			items: [me.orgImpFormPanel]
	    });
		
	    me.callParent(arguments);
	}
});