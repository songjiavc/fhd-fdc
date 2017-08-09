Ext.define('FHD.view.sys.menu.MenuBasePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.menubasepanel',
    requires: [
           'FHD.view.sys.menu.MenuBaseForm',
           'FHD.view.sys.menu.MenuTabEditGrid',
           'FHD.view.sys.menu.MenuButtonEditGrid'
    ],
	layout:'column',
	defaults: {
        columnWidth : 1
    },
    bodyPadding: "5 5 5 5",
    border: false,
    autoScroll:true,
    menuBaseForm:null,
    menuTabEditGrid:null,
    menuButtonEditGrid:null,
	authorityId:'',
	parentAuthorityId:'',
	setAuthorityId:function(authorityId){
		var me=this;
		me.authorityId=authorityId;
		me.menuBaseForm.setAuthorityId(me.authorityId);
		me.menuTabEditGrid.setParentAuthorityId(me.authorityId);
		me.menuButtonEditGrid.setParentAuthorityId(me.authorityId);
	},
	setParentAuthorityId:function(parentAuthorityId){
		var me=this;
		me.parentAuthorityId=parentAuthorityId;
		me.menuBaseForm.setParentAuthorityId(me.parentAuthorityId);
	},
	reloadData:function(){
		var me=this;
		me.menuBaseForm.reloadData();
		me.menuTabEditGrid.reloadData();
		me.menuButtonEditGrid.reloadData();
	},
    editCallBack:function(){
    },
    initComponent: function () {
        var me = this;
		me.menuBaseForm = Ext.widget('menubaseform',{
			authorityId:me.authorityId,
			parentAuthorityId:me.parentAuthorityId,
			editCallBack:me.editCallBack
		});
		me.menuTabEditGrid = Ext.widget('menutabeditgrid',{
			pagable : false,
    		searchable:false,
			parentAuthorityId:me.authorityId
		});
		me.menuButtonEditGrid = Ext.widget('menubuttoneditgrid',{
			pagable : false,
    		searchable:false,
			parentAuthorityId:me.authorityId
		});
        Ext.apply(me, {
            bbar:['->',{
	    		text : "保存",
	    		iconCls: 'icon-save',
	    		handler:function(){
	    			me.menuButtonEditGrid.save();
	    			me.menuTabEditGrid.save();
	    			me.menuBaseForm.edit();
	    		}
			}],
            items: [{
                xtype: 'fieldset',
                collapsible: true,
                title: "菜单信息",
                items:[
                	me.menuBaseForm
				]
            },{
                xtype: 'fieldset',
                collapsible: true,
                title: "功能权限",
                items:[
                   me.menuTabEditGrid
				]
            },{
                xtype: 'fieldset',
                collapsible: true,
                title: "表单字段权限",
                items:[
                   me.menuButtonEditGrid
				]
            }]
        });
        me.callParent(arguments);
    }
});