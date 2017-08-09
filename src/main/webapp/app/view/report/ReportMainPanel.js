/*
 * 部门综合报表主面板
 * 
 * ZJ
 * */

Ext.define('FHD.view.report.ReportMainPanel', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
	alias: 'widget.reportmainpanel',
	
	treeNodeId: '',
	treeType: '',
	type : '',
	
	initComponent: function () {
        var me = this;
        
        
        me.reportRiskViewGridContainer = Ext.create('Ext.container.Container', {
            layout: 'fit',
            title: '部门风险概况'
        });
        me.reportRiskViewGrid = Ext.create('FHD.view.report.risk.ReportRiskViewGrid', {
        	type : me.type,
            autoHeight: true
        });
        me.reportRiskViewGridContainer.add(me.reportRiskViewGrid);
        me.reportRiskViewGridContainer.doLayout();

        me.reportTree = Ext.create('FHD.view.report.ReportTreePanel',{
        	title : '综合报表',
        	onItemClick: function (tree, record, item, index, e, eOpts) {
                me.onItemClick(tree, record, item, index, e, eOpts);
            },
            firstNodeClick: function (store, node, records, successful, eOpts) {
                me.firstNodeClick(me.reportTree);
            }
        });
        var tabs = [];
        Ext.apply(me, {
            tree: me.reportTree,
            tabs: tabs
        });

        me.callParent(arguments);

        me.reRightLayout(me.reportRiskViewGridContainer);
    },
	
    reRightLayout: function (c) {
        var me = this;
        me.cardpanel.setActiveItem(c);
        me.cardpanel.doLayout();
    },
    
    getSelectedTreeItem: function (tree) {
        var me = this;
        var selectedNode;
        var nodeItems = tree.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
        }
        if (selectedNode == null) {
            var firstNode = tree.getRootNode().firstChild;
            if (null != firstNode) {
                tree.getSelectionModel().select(firstNode);
                selectedNode = firstNode;
            }
        }
        var id = selectedNode.data.id;
        var name = selectedNode.data.text;
        var treeType = selectedNode.data.type;
        me.treeNodeId = id;
        me.treeType = treeType;
        var selectedItem = {
            nodeId: id,
            nodeName: name
        };
        return selectedItem;
    },
    
    firstNodeClick: function (tree) {
        var me = this;
        var selectedNode;
        var nodeItems = tree.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
        }
        if (selectedNode == null) {
            var firstNode = tree.getRootNode().firstChild;
            firstNode = firstNode.firstChild;
            if (null != firstNode) {
                tree.getSelectionModel().select(firstNode);
                selectedNode = firstNode;
                tree.currentNode = selectedNode;
                var treeid = selectedNode.data.id;
                var treetype = selectedNode.data.type;
                me.treeNodeId = treeid;
                me.treeType = treetype;
                //刷新右侧部门文件夹主容器
                me.reloadRightMainPanel(treeid);
            }
        }
    },
    
    reloadRightMainPanel : function(treeid){
    	var me = this;
    	if('riskview' == treeid){
    		me.reportRiskViewGrid.reloadData();
    	}
    },
    
    onItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var selectedItem = me.getSelectedTreeItem(tree);
        //部门文件夹ID
        var treeid = selectedItem.nodeId;
        if (treeid == 'root') {
            return;
        }
        //刷新右侧主容器
        if('riskview' == treeid){
	        me.reRightLayout(me.reportRiskViewGridContainer);
        }
        me.reloadRightMainPanel(treeid);
        
    }
});