Ext.define('FHD.view.report.monitor.MonitorReportTree',{
    extend: 'FHD.ux.TreePanel',
    border : false,
    multiSelect: true,
    rowLines: false,
    singleExpand: false,
    checked: false,
    rootVisible: true,
    searchable: true,
    url: __ctxPath + "/kpi/monitorReport/monitorreporttreeloader.f", //调用后台url
    root: {
        id: "monitor_root",
        text: '监控预警',
        dbid: "monitor_root",
        leaf: false,
        type: "monitor",
        expanded: true,
        iconCls:'icon-ibm-new-group-view'
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
    // 点击首节点
    firstNodeClick:function(){
    	var me = this;
    },
    // 点击树节点
    onItemClick: function (tree, record, item, index, e, eOpts ) {
    	var me = tree;
    },
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
    }
})