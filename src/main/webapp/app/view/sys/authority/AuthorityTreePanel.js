Ext.define('FHD.view.sys.authority.AuthorityTreePanel', {
    extend: 'FHD.ux.TreePanel',
    alias: 'widget.authorityTreePanel',
    
    url:__ctxPath +"/sys/auth/auth/treeLoader.f",
    treeRootUrl:__ctxPath +"/sys/auth/auth/getTreeRoot.f",
	treeRoot:null,
	reloadDataCallBack:function(){
    },
	/**
	 * 旧版tree无此方法，如用改用新版请删除
	 */
    reloadData : function(options){
    	var me = this;
    	me.reloadDataCallBack();
    	me.store.reload(options);
    },
    getTreeRoot:function(){
    	var me=this;
		jQuery.ajax({
			type: "POST",
			async:false,
			url: me.treeRootUrl,
			success: function(treeRoot){
				me.treeRoot=treeRoot;
			},
			error: function(){
				FHD.alert("操作失败！");
			}
		});
		return me.treeRoot;
    },
    initComponent: function() {
		var me = this;
		Ext.apply(me, {
			root:me.getTreeRoot()
		});
		me.callParent(arguments);
    }
});
