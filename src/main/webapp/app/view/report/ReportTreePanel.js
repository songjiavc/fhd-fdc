Ext.define('FHD.view.report.ReportTreePanel', {
    extend: 'FHD.ux.TreePanel',
	alias: 'widget.reporttreepanel',

    autoScroll: true,
    animate: false,
    rootVisible: false,
    collapsible: false,
    border: false,
    multiSelect: true,
    rowLines: false,
    singleExpand: false,
    checked: false,
    width : 245,

    url: 'app/view/report/ReportTreeData.json', //调用后台url
    initComponent: function () {
        var me = this;

        Ext.applyIf(me, {
            listeners: {
                itemclick: me.onItemClick,
                load: function (store, records) {
                	me.firstNodeClick();
                }

            }
        });

        me.callParent(arguments);
    },
    /**
     * 获得树当前节点
     */
    getCurrentTreeNode:function(){
    	var me = this;
        var selectedNode;
        if(!me.getSelectionModel()){
        	return null;
        }
        var nodeItems = me.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
        }
        if (selectedNode == null) {
            var firstNode = me.getRootNode().firstChild;
            if (null != firstNode) {
                me.getSelectionModel().select(firstNode);
                selectedNode = firstNode;
            }
        }
        me.currentNode = selectedNode;
        return me.currentNode;
    },
    selectNode: function(root,id) {
    	var me = this;
    	var navNode;
    	var childnodes = root.childNodes;//获取根节点的子节点
        for(var i=0; i < childnodes.length; i++){
           var node = childnodes[i];
           if(node.data.id == id)
           {
             navNode = node;
           }
           if(node.hasChildNodes()){
        	 me.selectNode(node,id);//递归调用
           }
        };
         me.getSelectionModel().select(navNode)
    },
    firstNodeClick:function(){
    	var me = this;
    },
    
    onItemClick: function (tree, record, item, index, e, eOpts ) {
    	var me = tree;
    }

});