/**
 * 菜单管理TAB面板
 * 
 * @author 邓广义
 */
Ext.define('FHD.view.sys.menu.MenuTabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.menutabpanel',
    
    requires: [
       	'FHD.view.sys.menu.MenuBasePanel'
    ],
	border:false,
    plain: true,
    activeTab: 0,
    menuBasePanel:null,
	authorityId:'',
	parentAuthorityId:'',
	setAuthorityId:function(authorityId){
		var me=this;
		me.authorityId=authorityId;
		me.menuBasePanel.setAuthorityId(me.authorityId);
	},
	setParentAuthorityId:function(parentAuthorityId){
		var me=this;
		me.parentAuthorityId=parentAuthorityId;
		me.menuBasePanel.setParentAuthorityId(me.parentAuthorityId);
	},
	reloadData:function(){
		var me=this;
		me.menuBasePanel.reloadData();
	},
	baseEditCallBack:function(){
    },
    initComponent: function() {
    	var me = this;
    	me.menuBasePanel = Ext.widget('menubasepanel',{
    		title:'基本信息',
    		authorityId:me.authorityId,
			parentAuthorityId:me.parentAuthorityId,
    		editCallBack:me.baseEditCallBack
    	});
    	Ext.apply(me, {
            items: [me.menuBasePanel]
        });
        me.callParent(arguments);
    	me.getTabBar().insert(0,{xtype:'tbfill'});
    }
});