Ext.define('FHD.view.sys.authority.AuthorityCheckTreePanel', {
    extend: 'FHD.view.sys.authority.AuthorityTreePanel',
    alias: 'widget.authoritychecktreepanel',
	nodeIds:null,
	checkedNodeIds:[],
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
		me.extraParams.nodeIds=me.nodeIds;
    	me.store.load();
    },
    initComponent: function() {
		var me = this;
		Ext.apply(me, {
			extraParams:{
				nodeIds:me.nodeIds
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
