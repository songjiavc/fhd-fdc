/**
 *
 * 历史事件主面板
 */
Ext.define('FHD.view.risk.hisevent.HistoryEventMainPanel', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.historyeventmainpanel',

    nodeId: null, //左侧选择的树节点id
    nodeType: null, //risk,org,strategy,process

    initComponent: function () {
        var me = this;
        
        var treeArr = [];
        //创建风险容器
        me.createStatusTreeContainer();
        treeArr.push(me.statusTreeContainer);
        
        //创建左侧折叠树
	    var accordionTree = undefined;
	    if(treeArr.length>0){
	    	accordionTree = Ext.create("FHD.ux.layout.AccordionTree", {
	            title: treeArr[0]['treeTitle'],
	            iconCls: treeArr[0]['treeIconCls'],
	            width: 200,
	            treeArr: treeArr
	        });
	    }
        Ext.apply(me, {
            tree: accordionTree,
            typeId: me.typeId	//菜单配置-分库标识
        });

        me.callParent(arguments);

        //激活第一个面板
        if(treeArr.length>0){
        	treeArr[0].onClick();
        }

    },

    /**
     * 创建状态树
     * */
    createStatusTreeContainer: function () {
        var me = this;
        //组织树
        me.statusTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '历史事件',
            layout: 'fit',
            onClick: function () {
                if (!me.statusTree) {
                    me.statusTree = Ext.create('FHD.view.risk.StatusTree', {
                        border: false,
                        type : 'history',
						typeId: me.typeId,
                        firstNodeClick:function(){
                        	me.firstNodeClick('history');
                        },  
                        itemclick: function (tablepanel, record, item, index, e, options) {
                                var id = record.data.id;
                                //刷新右侧容器数据
                                me.reloadContainerData(id, 'history');
                        }
                    });
                    this.add(me.statusTree);
                    this.doLayout();
                }

                if (!me.historyEventGridPanel) {
                    me.historyEventGridPanel = Ext.create('FHD.view.risk.hisevent.HistoryEventGridPanel', {
                    	archiveStatus : "archived",
                    	type : 'all',
						typeId: me.typeId
                    });
                    me.cardpanel.add(me.historyEventGridPanel);
                }

                me.cardpanel.setActiveItem(me.historyEventGridPanel);

            }
        });
    },

    /**
     * 按id在树上查找节点
     */
    findNode: function (root, nodeid) {
        var me = this;
        var navNode = null;
        var childnodes = root.childNodes;
        for (var i = 0; i < childnodes.length; i++) {
            var node = childnodes[i];
            if (node.data.id == nodeid) {
                navNode = node;
                break;
            }
            if (node.hasChildNodes()) {
                navNode = me.findNode(node, nodeid); //递归调用
                if (navNode != null) {
                    break;
                }
            }
        }
        return navNode;
    },

    /**
     * 选中当前节点
     * */
    selectTreeNode: function (id, type) {
        var me = this;
        var itree = me.getTreeOrContainer(type, true);
        var rootNode = itree.getRootNode(); //得到根节点
        var selectNode = me.findNode(rootNode, id);
        itree.getSelectionModel().select(selectNode);
    },

    /**
     * 刷新右侧容器数据
     * */
    reloadContainerData: function (id, type, name) {
        var me = this;
        //保存当前节点
        me.nodeId = id;
        me.nodeType = type;
        //刷新右侧内容
        var icontainer = me.getTreeOrContainer(type, false);
        me.cardpanel.setActiveItem(icontainer);
        //刷新右侧数据
        icontainer.reloadData(id);
		
    },


    /**
     * 根据type返回tree或container
     * */
    getTreeOrContainer: function (type, isTree) {
        var me = this;
        var itree = me.riskTree; //树实例
        var icontainer = me.analyseRiskContainer; //导航container实例
        if (type == 'history') {
            itree = me.statusTree;
            icontainer = me.historyEventGridPanel;
        } else {

        }
        if (isTree) {
            return itree;
        } else {
            return icontainer;
        }
    },

    /**
     * 初次加载树选中第一个子节点
     */
    firstNodeClick: function (type) {
        var me = this;
        var itree = me.getTreeOrContainer(type, true);
        var icontainer = me.getTreeOrContainer(type, false);

        //1.选择组织树默认节点
        var selectedNode;
        var nodeItems = itree.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
        }
        //没有选中节点，默认选中第一个节点
        if (selectedNode == null) {
            var firstNode = itree.getRootNode().firstChild;
            if (null != firstNode) {
                itree.getSelectionModel().select(firstNode);
                selectedNode = firstNode;
            }
            if (selectedNode != null) {
		        var id = selectedNode.data.id;
		
		        me.nodeId = id;
		        me.nodeType = type;
		
		        icontainer.reloadData(me.nodeId);
            }
        }
    }
});