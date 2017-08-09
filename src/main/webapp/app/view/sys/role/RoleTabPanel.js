/**
 * 角色TAB面板
 * 
 * @author 翟辉
 */
Ext.define('FHD.view.sys.role.RoleTabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.roleTabPanel',
    
    requires: [
       'FHD.view.sys.role.UserRoleGridPanel',
       'FHD.view.sys.role.authority.RoleAuthorityPanel',
       'FHD.view.sys.role.RoleBasePanel'
    ],
    
    roleId:null,
    companyId:null,
    userRoleGridPanel:null,
    roleBasePanel:null,
    roleAuthorityPanel:null,
	baseEditCallBack:function(){
    },
	setRoleId:function(roleId){
		var me=this;
		me.roleId=roleId;
		me.userRoleGridPanel.setRoleId(me.roleId);
		me.roleAuthorityPanel.setRoleId(me.roleId);
		me.functionAuthorityPanel.setRoleId(me.roleId);
		me.formAuthorityPanel.setRoleId(me.roleId);
		me.roleBasePanel.setRoleId(me.roleId);
	},
	reloadData:function(){
		var me=this;
		if(""==me.roleId){
			me.userRoleGridPanel.disable();
			me.roleAuthorityPanel.disable();
			me.functionAuthorityPanel.disable();
			me.formAuthorityPanel.disable();
		}else{
			me.userRoleGridPanel.setDisabled(false);
			me.roleAuthorityPanel.setDisabled(false);
			me.functionAuthorityPanel.setDisabled(false);
			me.formAuthorityPanel.setDisabled(false);
		}
		me.userRoleGridPanel.reloadData();
		me.roleAuthorityPanel.reloadData();
		me.functionAuthorityPanel.reloadData();
		me.formAuthorityPanel.reloadData();
		me.roleBasePanel.reloadData();
	},
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//角色下人员GRID
    	me.userRoleGridPanel = Ext.widget('userRoleGridPanel',{
    		roleId:me.roleId,
    		companyId:me.companyId
    	});
    	//角色授权
    	me.roleAuthorityPanel = Ext.widget('roleAuthorityPanel',{
    		roleId:me.roleId
    	});
    	//功能授权
    	me.functionAuthorityPanel = Ext.create('FHD.view.sys.role.authority.FunctionAuthorityPanel',{
    		roleId:me.roleId,
    		title:'功能授权',
    		etype:'B' //权限类型：B->按钮权限；F->字段权限；
    	});
    	//表单授权
    	me.formAuthorityPanel = Ext.create('FHD.view.sys.role.authority.FunctionAuthorityPanel',{
    		roleId:me.roleId,
    		title:'表单授权',
    		etype:'F' //权限类型：B->按钮权限；F->字段权限；
    	});
    	//角色基本信息
    	me.roleBasePanel = Ext.widget('roleBasePanel',{
    		roleId:me.roleId,
    		editCallBack:me.baseEditCallBack
    	});
    	
    	Ext.apply(me, {
			deferredRender: false,
			activeTab: 0,
			plain: true,
			items: [me.userRoleGridPanel,me.roleAuthorityPanel,me.functionAuthorityPanel,me.formAuthorityPanel,me.roleBasePanel]
        });
    	
        me.callParent(arguments);
    }
});