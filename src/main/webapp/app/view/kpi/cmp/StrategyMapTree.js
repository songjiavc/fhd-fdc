Ext.define('FHD.view.kpi.cmp.StrategyMapTree', {
	extend : 'FHD.ux.TreePanel',
	url:__ctxPath + "/kpi/kpism/kpismtreeloader",
	root : {
		"id" : "sm_root",
		"text" : FHD.locale.get('fhd.sm.strategymaps'),
		"dbid" : "sm_root",
		"leaf" : false,
		"code" : "sm",
		"type" : "sm",
		"expanded" : true,
		'iconCls' : 'icon-strategy'
	},
	
	showLight:false,
	
	reloadData:function(){
		var me = this;
		me.store.proxy.url = me.queryUrl;
		me.store.load();
	},
	
	// 初始化方法
	initComponent : function() {
		var me = this;
		if(undefined!=me.showLight){
			me.extraParams.showLight = me.showLight;
		}

		Ext.applyIf(me, {
			rootVisible : true,
			width : 260,
			split : true,
			collapsible : true,
			border : true,
			region : 'west',
			multiSelect : true,
			rowLines : false,
			singleExpand : false,
			checked : false,
			url : me.url,
			root : me.root,
			extraParams:me.extraParams
			
		});

		me.callParent(arguments);
	}

});