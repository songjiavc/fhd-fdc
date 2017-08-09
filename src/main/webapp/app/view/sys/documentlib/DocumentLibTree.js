/**
 * 文档库分类树
 * 
 * @author 王再冉
 */
Ext.define('FHD.view.sys.documentlib.DocumentLibTree',{
	extend: 'FHD.ux.TreePanel',
    alias: 'widget.documentLibTree',
	
    extraParams: {
    	
    },
    
    refreshTree: function(){
		var me = this;
        me.getStore().load();
	},
	//单击树节点
	clickTreeNode: function(nodeId,type){
		var me = this;
		me.nodeId = nodeId;
		me.nodeType = type;
		var docCard = me.up('documentLibMainPanel').documentLibCard;
		docCard.showDocumentLibGrid();//返回列表
		me.docGrid = docCard.documentLibGrid;
		me.docGrid.getSelectionModel().deselectAll();
		me.docGrid.reloadData(nodeId,type);
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
	        border: true,
	        region: 'west',
	        multiSelect: true,
	        rowLines: false,
	        singleExpand: false,
	        checked: false,
		    url: me.url,
		    listeners : {
		 		 'itemclick' : function(view,re){
		 		 		me.clickTreeNode(re.data.id,re.data.type);
  				 },
  				 load: function (store, records) { //默认选择首节点
   					var selectNode;
   					if(records.childNodes[0]){
   						selectNode = records.childNodes[0];
   						me.getSelectionModel().select(selectNode);//默认选择首节点
		            	me.clickTreeNode(selectNode.data.id,selectNode.data.type);
   					}
                }
 			}
        });

        me.callParent(arguments);
    }

});