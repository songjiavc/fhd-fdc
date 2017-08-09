Ext.define('FHD.view.sys.documentlib.documentWindow.DocumentTreePanel',{
	extend: 'FHD.ux.TreePanel',
    alias: 'widget.documentTreePanel',
	
    extraParams: {
    	
    },
    
    refreshTree: function(){
		var me = this;
        me.getStore().load();
	},
	
	findRootNode:function(){
    	var me = this;
    	var root;
    	FHD.ajax({
       		async : false,
       		url : __ctxPath + '/access/formulateplan/finddocumenttreeroot.f',
       		params : {
					typeId: me.typeId
				},
       		callback : function(objectMaps){
       			 root =objectMaps.documentRoot;
       		}
    	});
    	return root;
    },
	
    initComponent: function() {
        var me = this;
        me.root = me.findRootNode();
        me.url = __ctxPath + '/sys/document/findtreenoodbydicttypeid.f?typeId='+me.typeId;
        
        Ext.apply(me, {
          	rootVisible: true,
          	root: me.root,
	        width: 240,
	        maxWidth: 350,
	        split: true,
	        collapsible : true,
	        border: true,
	        multiSelect: true,
	        rowLines: false,
	        singleExpand: false,
	        checked: false,
		    url: me.url
        });

        me.callParent(arguments);
    }

});