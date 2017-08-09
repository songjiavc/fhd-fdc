Ext.define('FHD.view.sys.role.authority.RoleAuthorityPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.roleAuthorityPanel',
	requires: [
		'FHD.view.sys.authority.AuthorityButtonPanel',
		'FHD.view.sys.role.authority.RoleAuthorityCheckTreePanel'
	],
    autoScroll:true,
    border:false,
    roleId:'',
    noRoleAuthorityTree:null,
    roleAuthorityTree:null,
    roleAuthorityButtonPanel:null,
	saveRoleAuthUrl:__ctxPath +'/sys/auth/role/saveRoleAuth.f',
	saveRoleAllAuthUrl:__ctxPath +'/sys/auth/role/saveRoleAllAuth.f',
	removeRoleAuthUrl:__ctxPath +'/sys/auth/role/removeRoleAuth.f',
	removeRoleAllAuthUrl:__ctxPath +'/sys/auth/role/removeRoleAllAuth.f',
    setRoleId:function(roleId){
		var me=this;
		me.roleId=roleId;
		me.noRoleAuthorityTree.setNoRoleId(me.roleId);
		me.roleAuthorityTree.setRoleId(me.roleId);
	},
	reloadData : function(){
    	var me = this;
    	me.noRoleAuthorityTree.reloadData();
    	me.roleAuthorityTree.reloadData();
    },
 	saveRoleAuth:function(nodeIds){
 		var me = this;
 		if(nodeIds&&nodeIds.length>0){
	 		var authorityIdsStr=nodeIds+"";
 			jQuery.ajax({
				type: "POST",
				url: me.saveRoleAuthUrl,
				data: {
					roleId:me.roleId,
					authorityIdsStr:authorityIdsStr
				},
				success: function(message){
					FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
					me.noRoleAuthorityTree.checkedNodeIds=[];
					me.reloadData();
				},
				error: function(){
					FHD.alert("操作失败！");
				}
			});
 		}
 	},
 	saveRoleAllAuth:function(){
 		var me = this;
		jQuery.ajax({
			type: "POST",
			url: me.saveRoleAllAuthUrl,
			data: {
				roleId:me.roleId
			},
			success: function(message){
				FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
		    	me.roleAuthorityButtonPanel.saveButton.disable();
				me.noRoleAuthorityTree.checkedNodeIds=[];
				me.reloadData();
			},
			error: function(){
				FHD.alert("操作失败！");
			}
		});
 	},
 	removeRoleAuth:function(nodeIds){
 		var me = this;
 		if(nodeIds&&nodeIds.length>0){
	 		var authorityIdsStr=nodeIds+"";
 			jQuery.ajax({
				type: "POST",
				url: me.removeRoleAuthUrl,
				data: {
					roleId:me.roleId,
					authorityIdsStr:authorityIdsStr
				},
				success: function(message){
					FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
					me.roleAuthorityTree.checkedNodeIds=[];
					me.reloadData();
				},
				error: function(){
					FHD.alert("操作失败！");
				}
			});
 		}
 	},
 	removeRoleAllAuth:function(){
 		var me = this;
		jQuery.ajax({
			type: "POST",
			url: me.removeRoleAllAuthUrl,
			data: {
				roleId:me.roleId
			},
			success: function(message){
				FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
		    	me.roleAuthorityButtonPanel.removeButton.disable();
				me.roleAuthorityTree.checkedNodeIds=[];
				me.reloadData();
			},
			error: function(){
				FHD.alert("操作失败！");
			}
		});
 	},
    initComponent: function () {
		var me = this;
		me.roleAuthorityButtonPanel = Ext.create('FHD.view.sys.authority.AuthorityButtonPanel',{
			border:true,
			width:100,
			saveAllCallBack:function(){
				me.saveRoleAllAuth();
			},
			saveCallBack:function(){
		    	me.roleAuthorityButtonPanel.saveButton.disable();
				var checkedNodeIds=me.noRoleAuthorityTree.checkedNodeIds;
				me.saveRoleAuth(checkedNodeIds);
			},
			removeCallBack:function(){
		    	me.roleAuthorityButtonPanel.removeButton.disable();
				var checkedNodeIds=me.roleAuthorityTree.checkedNodeIds;
				me.removeRoleAuth(checkedNodeIds);
			},
			removeAllCallBack:function(){
				me.removeRoleAllAuth();
			}
		});
		me.noRoleAuthorityTree = Ext.create('FHD.view.sys.role.authority.RoleAuthorityCheckTreePanel',{
			border:false,
			noRoleId:me.roleId,
			flex: 0.5,
			checkModel:'cascade',
			myexpand:false,
			rootVisible:false,
			rootCheck:false,
		    checkCallBack:function(flag){
		    	if(flag){	
			    	me.roleAuthorityButtonPanel.saveButton.enable();
		    	}else{
			    	me.roleAuthorityButtonPanel.saveButton.disable();
		    	}
		    },
		    listeners:{
		    	load:function(){
		    		me.roleAuthorityButtonPanel.saveButton.disable();
		    		var flag=me.noRoleAuthorityTree.getRootNode().hasChildNodes();
			    	if(flag){
				    	me.roleAuthorityButtonPanel.saveAllButton.enable();
			    	}else{
				    	me.roleAuthorityButtonPanel.saveAllButton.disable();
			    	}
		    	}
		    }
		});
		me.roleAuthorityTree = Ext.create('FHD.view.sys.role.authority.RoleAuthorityCheckTreePanel',{
			border:false,
			roleId:me.roleId,
			flex: 0.5,
			checkModel:'cascade',
			myexpand:false,
			rootVisible:false,
			rootCheck:false,
		    checkCallBack:function(flag){
		    	if(flag){
			    	me.roleAuthorityButtonPanel.removeButton.enable();
		    	}else{
			    	me.roleAuthorityButtonPanel.removeButton.disable();
		    	}
		    },
		    listeners:{
		    	load:function(){
		    		me.roleAuthorityButtonPanel.removeButton.disable();
		    		var flag=me.roleAuthorityTree.getRootNode().hasChildNodes();
			    	if(flag){
				    	me.roleAuthorityButtonPanel.removeAllButton.enable();
			    	}else{
				    	me.roleAuthorityButtonPanel.removeAllButton.disable();
			    	}
		    	}
		    }
		});
		Ext.apply(me, {
			title:"角色授权",
			layout: {
				align: 'stretch',
				type: 'hbox'
		    },
			items:[me.noRoleAuthorityTree,me.roleAuthorityButtonPanel,me.roleAuthorityTree]
		});
        me.callParent(arguments);
    }
});