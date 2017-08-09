

Ext.define('FHD.view.risk.processStorage.ProcessStorageTreePanel', {
    extend: 'FHD.view.process.ProcessTree',
    alias: 'widget.processstoragetreepanel',
   
nodeClick: function (record) {
        var me = this;
        var id = record.data.id;
        //保存当前节点
        me.nodeId = id;
        me.node = record;
        me.nodeType = 'process';
        //刷新右侧容器数据
        me.up('processstoragemainpanelnew').processCard.showRiskGrid();
        me.up('processstoragemainpanelnew').processCard.processGrid.reloadData(id, 'process');
    },
    /**
     * 选中首节点//并激活树节点被单击处理函数
     */
    selectFirstNode:function(){
        //选择默认节点
        var me = this;
        var selectedNode = null;
        var firstNode = me.riskTree.getRootNode().firstChild;
        if (null != firstNode) {
            me.riskTree.getSelectionModel().select(firstNode);
            selectedNode = firstNode;
            me.nodeId = selectedNode.data.id;
            me.nodeType = 'process';
            me.node = selectedNode;
            //标识首节点被选中
            firstNodeSelected = true;
        }

    },

    // 初始化方法
    initComponent: function() {
        var me = this;

        Ext.apply(me, {
            showLight : true,
            listeners: {
                itemclick: function (tablepanel, record, item, index, e, options) {
                    me.nodeClick(record);
                }
            }
        });

        me.callParent(arguments);
    },
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
            
                selectedNode = '';
        }
        me.currentNode = selectedNode;
        return me.currentNode;
    }
});
    
