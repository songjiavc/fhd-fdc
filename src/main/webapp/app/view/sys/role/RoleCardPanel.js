/**
 * 角色面板
 */
Ext.define('FHD.view.sys.role.RoleCardPanel', {
    extend: 'FHD.ux.CardPanel',
    alias: 'widget.roleCardPanel',
    requires: [
         'FHD.view.sys.role.RoleTabPanel'
    ],
    roleTabPanel:null,
    roleId:null,
    companyId:null,
    baseEditCallBack:function(){
    },
	setRoleId:function(roleId){
		var me=this;
		me.roleId=roleId;
		me.roleTabPanel.setRoleId(me.roleId);
	},
	reloadData:function(){
		var me=this;
		me.roleTabPanel.reloadData();
	},
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.roleTabPanel = Ext.widget('roleTabPanel',{
			companyId:me.companyId,
			roleId:me.roleId,
		    baseEditCallBack:me.baseEditCallBack
    	});
		me.roleTabPanel.getTabBar().insert(0, {
            xtype: 'tbfill'
       	});
    	Ext.apply(me, {
			xtype: 'cardpanel',
			border:false,
			activeItem : 0,
			items:[
				me.roleTabPanel
			]
        });
        me.callParent(arguments);
    }
});