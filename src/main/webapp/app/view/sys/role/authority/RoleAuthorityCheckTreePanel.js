Ext.define('FHD.view.sys.role.authority.RoleAuthorityCheckTreePanel', {
    extend: 'FHD.view.sys.authority.AuthorityTreePanel',
    alias: 'widget.roleauthoritychecktreepanel',
    url:__ctxPath +"/sys/auth/auth/checkTreeLoaderAllByRole.f",
	etypes:['M','G','T'],
	roleId:null,
	noRoleId:null,
	checkedNodeIds:[],
    setRoleId:function(roleId){
		var me=this;
		me.roleId=roleId;
		me.extraParams.roleId=me.roleId;
	},
    setNoRoleId:function(noRoleId){
		var me=this;
		me.noRoleId=noRoleId;
		me.extraParams.noRoleId=me.noRoleId;
	},
    reloadDataCallBack:function(){
    	
    },
    checkCallBack:function(flag){
    	
    },
	/**
	 * 旧版tree无此方法，如用改用新版请删除
	 */
    reloadData : function(){
    	var me = this;
    	me.reloadDataCallBack();
    	me.checkedNodeIds=[];
		me.extraParams.roleId=me.roleId;
		me.extraParams.noRoleId=me.noRoleId;
		me.extraParams.etypes=me.etypes;
    	me.store.load();
    },
    initComponent: function() {
		var me = this;
		Ext.apply(me, {
			extraParams:{
				roleId:me.roleId,
				noRoleId:me.noRoleId,
				etypes:me.etypes
			},
			check: function(me,node,checked){
				if(checked){
					var flag=true;
					for (var i in me.checkedNodeIds) {
						if(me.checkedNodeIds[i]==node.data.id){
							flag=false;
						}
					}
					if(flag){
						me.checkedNodeIds.push(node.data.id);
					}
				}else{
					var tempI=null;
					for (var i in me.checkedNodeIds) {
						if(me.checkedNodeIds[i]==node.data.id){
							tempI=i;
						}
					}
					if(tempI){
						me.checkedNodeIds.splice(tempI,1);
					}
				}
				me.checkCallBack(me.checkedNodeIds.length>0);
			}
		});
		me.callParent(arguments);
    }
});
