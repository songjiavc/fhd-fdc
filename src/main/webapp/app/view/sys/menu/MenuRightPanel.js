Ext.define('FHD.view.sys.menu.MenuRightPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.menurightpanel',
    requires: [
			'FHD.view.sys.menu.MenuTabPanel'
    ],
	region:'center',
	layout:'fit',
	menuTabPanel:null,
	authorityId:'',
	parentAuthorityId:'',
	setAuthorityId:function(authorityId){
		var me=this;
		me.authorityId=authorityId;
		me.menuTabPanel.setAuthorityId(me.authorityId);
	},
	setParentAuthorityId:function(parentAuthorityId){
		var me=this;
		me.parentAuthorityId=parentAuthorityId;
		me.menuTabPanel.setParentAuthorityId(me.parentAuthorityId);
	},
	baseEditCallBack:function(){
    },
	reloadData:function(){
		var me=this;
		me.menuTabPanel.reloadData();
	},
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.menuTabPanel = Ext.widget('menutabpanel',{
			authorityId:me.authorityId,
			parentAuthorityId:me.parentAuthorityId,
		    baseEditCallBack:me.baseEditCallBack
    	});
        Ext.apply(me, {
    		items:[me.menuTabPanel]
        });
        me.callParent(arguments);
    }
});