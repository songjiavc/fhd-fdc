Ext.define('FHD.view.sys.user.UserAuthorityPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.userAuthorityPanel',
	requires: [
		'FHD.view.sys.authority.AuthorityButtonPanel',
		'FHD.view.sys.user.UserAuthorityCheckTreePanel'
	],
	autoScroll:true,
	border:false,
	userId:'',
	noUserAuthorityTree:null,
	userAuthorityTree:null,
	authorityButtonPanel:null,
	saveUserAuthUrl:__ctxPath +'/sys/auth/user/saveUserAuth.f',
	saveUserAllAuthUrl:__ctxPath +'/sys/auth/user/saveUserAllAuth.f',
	removeUserAuthUrl:__ctxPath +'/sys/auth/user/removeUserAuth.f',
	removeUserAllAuthUrl:__ctxPath +'/sys/auth/user/removeUserAllAuth.f',
	setUserId:function(userId){
		var me=this;
		me.userId=userId;
		me.noUserAuthorityTree.setNoUserId(me.userId);
		me.userAuthorityTree.setUserId(me.userId);
	},
	reloadData : function(){
		var me = this;
		me.noUserAuthorityTree.reloadData();
		me.userAuthorityTree.reloadData();
	},
	saveUserAuth:function(nodeIds){
		var me = this;
		if(nodeIds&&nodeIds.length>0){
	 		var authorityIdsStr=nodeIds+"";
			jQuery.ajax({
				type: "POST",
				url: me.saveUserAuthUrl,
				data: {
					userId:me.userId,
					authorityIdsStr:authorityIdsStr
				},
				success: function(message){
					FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
					me.noUserAuthorityTree.checkedNodeIds=[];
					me.reloadData();
				},
				error: function(){
					FHD.alert("操作失败！");
				}
			});
		}
	},
	saveUserAllAuth:function(){
		var me = this;
		jQuery.ajax({
			type: "POST",
			url: me.saveUserAllAuthUrl,
			data: {
				userId:me.userId
			},
			success: function(message){
				FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
		    	me.userAuthorityButtonPanel.saveButton.disable();
				me.noUserAuthorityTree.checkedNodeIds=[];
				me.reloadData();
			},
			error: function(){
				FHD.alert("操作失败！");
			}
		});
	},
	removeUserAuth:function(nodeIds){
		var me = this;
		if(nodeIds&&nodeIds.length>0){
	 		var authorityIdsStr=nodeIds+"";
			jQuery.ajax({
				type: "POST",
				url: me.removeUserAuthUrl,
				data: {
					userId:me.userId,
					authorityIdsStr:authorityIdsStr
				},
				success: function(message){
					FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
					me.userAuthorityTree.checkedNodeIds=[];
					me.reloadData();
				},
				error: function(){
					FHD.alert("操作失败！");
				}
			});
		}
	},
	removeUserAllAuth:function(){
		var me = this;
		jQuery.ajax({
			type: "POST",
			url: me.removeUserAllAuthUrl,
			data: {
				userId:me.userId
			},
			success: function(message){
				FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
		    	me.userAuthorityButtonPanel.removeButton.disable();
				me.userAuthorityTree.checkedNodeIds=[];
				me.reloadData();
			},
			error: function(){
				FHD.alert("操作失败！");
			}
		});
	},
	initComponent: function () {
		var me = this;
		me.userAuthorityButtonPanel = Ext.create('FHD.view.sys.authority.AuthorityButtonPanel',{
			border:true,
			width:100,
			saveAllCallBack:function(){
				me.saveUserAllAuth();
			},
			saveCallBack:function(){
		    	me.userAuthorityButtonPanel.saveButton.disable();
				var checkedNodeIds=me.noUserAuthorityTree.checkedNodeIds;
				me.saveUserAuth(checkedNodeIds);
			},
			removeCallBack:function(){
		    	me.userAuthorityButtonPanel.removeButton.disable();
				var checkedNodeIds=me.userAuthorityTree.checkedNodeIds;
				me.removeUserAuth(checkedNodeIds);
			},
			removeAllCallBack:function(){
				me.removeUserAllAuth();
			}
		});
		me.noUserAuthorityTree = Ext.create('FHD.view.sys.user.UserAuthorityCheckTreePanel',{
			border:false,
			noUserId:me.userId,
			flex: 0.5,
			checkModel:'cascade',
			myexpand:false,
			rootVisible:false,
			rootCheck:false,
		    checkCallBack:function(flag){
		    	if(flag){	
			    	me.userAuthorityButtonPanel.saveButton.enable();
		    	}else{
			    	me.userAuthorityButtonPanel.saveButton.disable();
		    	}
		    },
		    listeners:{
		    	load:function(){
		    		me.userAuthorityButtonPanel.saveButton.disable();
		    		var flag=me.noUserAuthorityTree.getRootNode().hasChildNodes();
			    	if(flag){
				    	me.userAuthorityButtonPanel.saveAllButton.enable();
			    	}else{
				    	me.userAuthorityButtonPanel.saveAllButton.disable();
			    	}
		    	}
		    }
		});
		me.userAuthorityTree = Ext.create('FHD.view.sys.user.UserAuthorityCheckTreePanel',{
			border:false,
			userId:me.userId,
			flex: 0.5,
			checkModel:'cascade',
			myexpand:false,
			rootVisible:false,
			rootCheck:false,
		    checkCallBack:function(flag){
		    	if(flag){
			    	me.userAuthorityButtonPanel.removeButton.enable();
		    	}else{
			    	me.userAuthorityButtonPanel.removeButton.disable();
		    	}
		    },
		    listeners:{
		    	load:function(){
		    		me.userAuthorityButtonPanel.removeButton.disable();
		    		var flag=me.userAuthorityTree.getRootNode().hasChildNodes();
			    	if(flag){
				    	me.userAuthorityButtonPanel.removeAllButton.enable();
			    	}else{
				    	me.userAuthorityButtonPanel.removeAllButton.disable();
			    	}
		    	}
		    }
		});
		Ext.apply(me, {
			layout: {
				align: 'stretch',
				type: 'hbox'
		    },
			items:[me.noUserAuthorityTree,me.userAuthorityButtonPanel,me.userAuthorityTree]
		});
	    me.callParent(arguments);
	}
});