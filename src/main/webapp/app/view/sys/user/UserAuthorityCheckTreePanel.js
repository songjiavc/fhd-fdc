Ext.define('FHD.view.sys.user.UserAuthorityCheckTreePanel', {
    extend: 'FHD.view.sys.authority.AuthorityTreePanel',
    alias: 'widget.userauthoritychecktreepanel',
    url:__ctxPath +"/sys/auth/auth/checkTreeLoaderAllByUser.f",
	userId:null,
	noUserId:null,
	checkedNodeIds:[],
	etypes:['M','G','T'],
    setUserId:function(userId){
		var me=this;
		me.userId=userId;
		me.extraParams.userId=me.userId;
	},
    setNoUserId:function(noUserId){
		var me=this;
		me.noUserId=noUserId;
		me.extraParams.noUserId=me.noUserId;
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
		me.extraParams.userId=me.userId;
		me.extraParams.noUserId=me.noUserId;
		me.extraParams.etypes=me.etypes;
    	me.store.load();
    },
    initComponent: function() {
		var me = this;
		Ext.apply(me, {
			extraParams:{
				userId:me.userId,
				noUserId:me.noUserId,
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
