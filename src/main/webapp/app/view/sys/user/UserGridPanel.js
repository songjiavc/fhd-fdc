Ext.define('FHD.view.sys.user.UserGridPanel', {
	extend : 'FHD.ux.GridPanel',
	alias : 'widget.userGridPanel',
	requires: [
		'FHD.view.sys.user.UserAuthorityPanel'
    ],
    
	title : "用户管理",
	url : __ctxPath + '/sys/auth/user/findSysUserPageBySomeSf2.f',
	companyId : null,
	tbarItems : null,
	border : true,
	removeUrl : __ctxPath + '/sys/auth/user/remove.f',
	resetPasswordUrl : __ctxPath + '/sys/auth/user/resetPassword.f',

	reloadData : function() {
		var me = this;
		me.store.load();
	},
	save : function() {
		var me = this;
		var UserBaseForm = Ext.create('FHD.view.sys.user.UserBaseForm', {
			editCallBack : function() {
				window.close();
			},
			closeCallBack : function() {
				window.close();
			}
		});
		var window = Ext.create('FHD.ux.Window', {
			title : FHD.locale.get('fhd.common.add'),
			iconCls : 'icon-add',
			maximizable : true,
			listeners : {
				close : function() {
					me.reloadData();
				}
			}
		});
		window.add(UserBaseForm);
		window.show();
	},
	edit : function(id) {
		var me = this;
		var UserBaseForm = Ext.create('FHD.view.sys.user.UserBaseForm', {
			userId:id,
			editCallBack : function() {
				window.close();
			},
			closeCallBack : function() {
				window.close();
			}
		});
		var window = Ext.create('FHD.ux.Window', {
			title : FHD.locale.get('fhd.common.edit'),
			iconCls : 'icon-edit',
			maximizable : true,
			listeners : {
				close : function() {
					me.reloadData();
				}
			}
		});
		window.add(UserBaseForm);
		window.show();
	},
	remove : function(ids) {
		var me = this;
		Ext.MessageBox.confirm('警告', FHD.locale.get('fhd.common.makeSureDelete'), function showResult(btn) {
			if (btn == 'yes') {// 确认删除
				var idsStr = ids + "";
				jQuery.ajax({
					type : "POST",
					url : me.removeUrl,
					data : {
						idsStr : idsStr
					},
					success : function(msg) {
						FHD.notification("提示",FHD.locale.get('fhd.common.operateSuccess'));
						me.store.load();
					},
					error : function() {
						FHD.alert("操作失败！");
					}
				});
			}
		});
	},
	resetPassword : function(ids) {
		var me = this;
		Ext.MessageBox.confirm('警告', "确定重置密码吗？", function showResult(btn) {
			if (btn == 'yes') {// 确认删除
				var idsStr = ids + "";
				jQuery.ajax({
					type : "POST",
					url : me.resetPasswordUrl,
					data : {
						idsStr : idsStr
					},
					success : function(msg) {
						FHD.notification("提示",FHD.locale.get('fhd.common.operateSuccess'));
						me.store.load();
					},
					error : function() {
						FHD.alert("操作失败！");
					}
				});
			}
		});
	},
	editUserRole : function(id) {
		var me = this;
		var userRolePanel = Ext.create('FHD.view.sys.user.UserRoleForm', {
			userId:id
		});
		var window = Ext.create('FHD.ux.Window', {
			title : "赋予角色",
			iconCls : 'icon-edit',
			maximizable : true,
			bbar:{
                items: ['->',
	            {
					text: FHD.locale.get("fhd.common.save"),//保存按钮
					iconCls: 'icon-save',
					handler: function () {
						userRolePanel.edit();
					}
	            },
	            {
					text: FHD.locale.get("fhd.common.close"),
					iconCls: 'icon-ibm-close',
					handler: function () {
						window.close();
					}
	            }]
            }
		});
		userRolePanel.editCallBack=function(){
			window.close();
		};
		window.add(userRolePanel);
		window.show();
	},
	editUserAuthority : function(id) {
		var me = this;
		var userAuthorityPanel = Ext.create('FHD.view.sys.user.UserAuthorityPanel', {
			userId:id
		});
		var window = Ext.create('FHD.ux.Window', {
			title : "赋予权限",
			iconCls : 'icon-edit',
			maximizable : true,
			bbar:{
                items: ['->',
	            {
					text: FHD.locale.get("fhd.common.close"),
					iconCls: 'icon-ibm-close',
					handler: function () {
						window.close();
					}
	            }]
            }
		});
		window.add(userAuthorityPanel);
		window.show();
	},
	initComponent : function() {
		var me = this;
		me.tbarItems = [{
			name : 'save',
			text : FHD.locale.get('fhd.common.add'),
			iconCls : 'icon-add',
			handler : function() {
				me.save();
			}
		}, '-', {
			name : 'edit',
			text : FHD.locale.get('fhd.common.edit'),
			iconCls : 'icon-edit',
			disabled : true,
			handler : function() {
				var selection = me.getSelectionModel().getSelection();
				if (selection.length > 0) {
					var id = selection[0].get("id");
					me.edit(id);
				}
			}
		}, '-', {
			name : 'remove',
			text : FHD.locale.get('fhd.common.delete'),
			iconCls : 'icon-del',
			disabled : true,
			handler : function() {
				var selection = me.getSelectionModel().getSelection();
				var ids = new Array();
				for (var i in selection) {
					ids.push(selection[i].get("id"));
				}
				me.remove(ids);
			}
		}, '-', {
			name : 'resetPassword',
			text : '密码重置',
			iconCls : 'icon-bullet-wrench',
			disabled : true,
			handler : function() {
				var selection = me.getSelectionModel().getSelection();
				var ids = new Array();
				for (var i in selection) {
					ids.push(selection[i].get("id"));
				}
				me.resetPassword(ids);
			}
		}, '-', {
			name : 'editUserRole',
			text : '赋予角色',
			iconCls : 'icon-edit',
			disabled : true,
			handler : function() {
				var selection = me.getSelectionModel().getSelection();
				if (selection.length > 0) {
					var id = selection[0].get("id");
					me.editUserRole(id);
				}
			}
		}, '-', {
			name : 'editUserAuthority',
			text : '赋予权限',
			iconCls : 'icon-edit',
			disabled : true,
			handler : function() {
				var selection = me.getSelectionModel().getSelection();
				if (selection.length > 0) {
					var id = selection[0].get("id");
					me.editUserAuthority(id);
				}
			}
		}];

		Ext.apply(me, {
			extraParams : {
				companyId : me.companyId
			},
			storeSorters : [{
				property : 'username',
				direction : 'asc'
			}],
			cols : [{
				dataIndex : 'id',
				invisible : true
			}, {
				header : FHD.locale.get('fhd.common.username'),
				dataIndex : 'username',
				flex : 1
			}, {
				header : FHD.locale.get('fhd.common.status'),
				dataIndex : 'userStatusStr',
				flex : 1
			}, {
				header : FHD.locale.get('fhd.common.lockState'),
				dataIndex : 'lockstateStr',
				flex : 1
			}, {
				header : FHD.locale.get('fhd.common.enable'),
				dataIndex : 'enableStr',
				flex : 1
			}, {
				header : FHD.locale.get('fhd.common.regdate'),
				dataIndex : 'regdateStr',
				flex : 1
			}, {
				header : FHD.locale.get('fhd.common.abatedate'),
				dataIndex : 'expiryDateStr',
				flex : 1
			}, {
				header : FHD.locale.get('fhd.common.credentialsexpiryDate'),
				dataIndex : 'credentialsexpiryDateStr',
				flex : 1
			}, {
				header : 'mac地址',
				dataIndex : 'mac',
				flex : 1
			}],
			listeners : {
				selectionchange : function() {
					var me = this;
					var selection = me.getSelectionModel().getSelection();
					var selectionLength = selection.length;
					me.down("[name='edit']").setDisabled(selectionLength != 1);
					me.down("[name='remove']").setDisabled(selectionLength <= 0);
					me.down("[name='resetPassword']").setDisabled(selectionLength <= 0);
					me.down("[name='editUserRole']").setDisabled(selectionLength != 1);
					me.down("[name='editUserAuthority']").setDisabled(selectionLength != 1);
				}
			}
		});
		me.callParent(arguments);
	}
});
