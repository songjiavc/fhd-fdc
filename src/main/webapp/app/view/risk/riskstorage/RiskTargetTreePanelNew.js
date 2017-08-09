/*
 * 风险 -- 目标树
 * 
 * @time 2017年3月23日09:39:27
 */
Ext.define('FHD.view.risk.riskstorage.RiskTargetTreePanelNew', {
    extend: 'FHD.view.kpi.cmp.StrategyMapTree',// 风险-目标 tree
    alias: 'widget.risktargetreepanelnew',


    // 初始化方法
    initComponent: function() {
        var me = this;

        Ext.apply(me, {
            showLight : true,
            listeners: {
                itemclick: function (tablepanel, record, item, index, e, options) {
                   
                   var id = record.data.id;
                   var type = record.data.type;
                   var name = record.data.text;
                   //刷新右侧容器数据
                   me.selectedNode(id, type, name);
                },
            }
        });

        me.callParent(arguments);
    },
    
    /**
     * 树节点被单击处理函数
     */
    selectedNode: function (id, type, name) {
    	//因为父节点type=sm 叶子节点type=kpi 2017年3月23日09:23:26 吉志强
    	type = "sm";
        var me = this;
        //保存当前节点
        me.nodeId = id;
        me.nodeType = type;
        //点击节点切换回grid
        me.up('risktargetmainpanelnew').targetCard.showTargetGrid();
        //刷新右侧数据
        me.up('risktargetmainpanelnew').targetCard.targetGrid.reloadData(id,type);

    },
    //获取当前选中节点
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