/**
 * 方案执行上报表单
 * */
Ext.define('FHD.view.response.PreplanTab', {
	extend : 'Ext.tab.Panel',
	alias: 'widget.preplantab',
	requires:[
		'FHD.view.response.PreplanForm'
	],
	plain: true,
	initComponent :function() {
		var me = this;
        //针对风险的预案页签
        me.preplanforriskList = Ext.create('FHD.view.response.PreplanListPanel',{
        	title:'风险预案'
        });
        
        //针对指标的预案页签
        me.preplanfortargetList = Ext.create("FHD.view.response.PreplanForTargetList",{
        	title: "指标预案"
        });
        
        Ext.applyIf(me, {
//        	tabBar:{
//        		style : 'border-right: 1px  #99bce8 solid;'
//        	},
            items: [me.preplanforriskList, me.preplanfortargetList]
        });
        me.callParent(arguments);
//        me.getTabBar().insert(0,{xtype:'tbfill'});
	},
	loadData: function(improveId,preplanId){
		var me = this;
		me.improveId = improveId;
		me.preplanId = preplanId;
		me.reloadData();
	},
	reloadData:function(){
		var me=this;
	}
});

