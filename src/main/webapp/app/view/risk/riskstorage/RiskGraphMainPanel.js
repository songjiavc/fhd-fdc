/**
 * 风险库主面板
 * @author ZJ
 */
Ext.define('FHD.view.risk.riskstorage.RiskGraphMainPanel', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.riskgraphmainpanel',

    node:null,		//左侧选择的树节点
    nodeId: null, //左侧选择的树节点id
    nodeType: null, //risk,org,sm,process

    initComponent: function () {
        var me = this;
        //创建风险容器
        var treeArr = [];
        //创建风险容器
        if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_REASONQUERY_RISK')){
            me.createRiskTreeContainer();
            treeArr.push(me.riskTreeContainer);
		}
//        //创建组织容器
//        me.createOrgTreeContainer();
//        //创建目标容器
//        me.createSmTreeContainer();
//        //创建流程容器
//        me.createProcessTreeContainer();

        //创建左侧折叠树
        var accordionTree = undefined;
	    if(treeArr.length>0){
	    	accordionTree = Ext.create("FHD.ux.layout.AccordionTree", {
	            title: '风险',
	            iconCls: 'icon-ibm-icon-scorecards',
	            width: 250,
	            treeArr: treeArr
	        });
	    }

        Ext.apply(me, {
            tree: accordionTree
        });

        me.callParent(arguments);

        //激活第一个面板
        if(treeArr.length>0){
        	treeArr[0].onClick();
        }

    },
    /**
     * 创建风险容器
     * */
    createRiskTreeContainer: function () {
        var me = this;
        me.riskTreeContainer = Ext.create('Ext.container.Container', {
        	//authority:'ROLE_ALL_ASSESS_ANALYSIS_REASONQUERY_RISK_RISKLIST',
            treeTitle: '风险',
            treeIconCls: 'icon-ibm-icon-scorecards',
            layout: 'fit',
            onClick: function () {
                if (!me.riskTree) {
                	me.riskTree = Ext.create('FHD.view.risk.cmp.RiskTreePanel', {
                        border: false,
                        rbs: true,
                        showLight: true,
                        reloadNavigator: function (id, name) {
                            var nid = id.split('_')[0];
                            //选中当前节点
                            me.selectTreeNode(nid, 'risk');
                            //刷新右侧容器数据
                            me.reloadContainerData(nid, 'risk');
                        },
                        listeners: {
                            itemclick: function (tablepanel, record, item, index, e, options) {
                                var id = record.data.id;
	                            //风险节点不可查看基本信息
	                            if (id == 'root') {
	                                me.storageRiskContainer.riskFormViewDisable(true);
	                            } else {
	                                me.storageRiskContainer.riskFormViewDisable(false);
	                            }
	                            //刷新右侧容器数据
	                            me.reloadContainerData(id, 'risk');
                            },
                            load: function () {
                                me.firstNodeClick('risk');
                            }
                        }
                    });
                    this.add(me.riskTree);
                    this.doLayout();
                }

                if (!me.storageRiskContainer) {
                    me.storageRiskContainer = Ext.create('FHD.view.risk.cmp.container.storage.RiskGraphContainer', {
                        navFunId: me.riskTree.id,
                        addFormCallback: function (data,editflag) {
                        	var id = data.id;
        	        		var text = data.name;
        	        		//刷新树节点,如果是添加则添加树节点;如果是修改则修改节点名称
                        	if (!editflag) {
                        		//添加节点
                                var node = {
                                    iconCls: 'icon-ibm-symbol-0-sm',	//风险水平无对应的显示灯图标
                                    id: id,
                                    text: text,
                                    dbid: id,
                                    leaf: true,
                                    type: 'risk'
                                };
                                if (me.node.isLeaf()) {
                                    me.node.data.leaf = false;
                                }
                                me.node.appendChild(node);
                                me.node.expand();
                                var newNode = me.node.lastChild;
                                //选中新添加的节点
                                me.riskTree.getSelectionModel().select(newNode);
                                me.node = newNode;
                                me.nodeId = newNode.data.id;
                                me.nodeType = 'risk';
                        	} else {
                            	//编辑节点,需要替换节点名称
                        		var nodeData = me.node.data;
                        		nodeData.text = text;
                        		me.node.updateInfo(true, nodeData);
                        	}
                        	me.storageRiskContainer.chageNavigationBar(id, '');
                    	}
                    });
                    me.cardpanel.add(me.storageRiskContainer);
                }
                me.cardpanel.setActiveItem(me.storageRiskContainer);
            }
        });
    },

    /**
     * 创建组织容器
     * */
    createOrgTreeContainer: function () {
        var me = this;
        //组织树
        me.orgTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '组织',
            treeIconCls: 'icon-ibm-new-group-view',
            layout: 'fit',
            onClick: function () {
                if (!me.orgTree) {
                    me.orgTree = Ext.create('FHD.ux.org.DeptTree', {
                        showLight: true,
                        subCompany: false,
                        companyOnly: false,
                        checkable: false,
                        border: false,
                        rootVisible: true,
                        reloadNavigator: function (id, name) {
                            var nid = id.split('_')[0];
                            //选中当前节点
                            me.selectTreeNode(nid, 'org');
                            //刷新右侧容器数据
                            me.reloadContainerData(nid, 'org');
                        },
                        listeners: {
                            afteritemexpand: function () {
                                me.firstNodeClick('org');
                            },
                            itemclick: function (tablepanel, record, item, index, e, options) {
                                var id = record.data.id;
                                //刷新右侧容器数据
                                me.reloadContainerData(id, 'org');
                            }

                        }
                    });
                    this.add(me.orgTree);
                    this.doLayout();
                }

                if (!me.storageOrgContainer) {
                    me.storageOrgContainer = Ext.create('FHD.view.risk.cmp.container.storage.StorageOrgContainer', {
                        navFunId: me.orgTree.id,
                        hisCallback: function () {
                            me.orgTree.store.load();
                        }
                    });
                    me.cardpanel.add(me.storageOrgContainer);
                }

                me.cardpanel.setActiveItem(me.storageOrgContainer);

            }
        });
    },

    /**
     * 创建目标容器
     * */
    createSmTreeContainer: function () {
        var me = this;
        //目标树
        me.smTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '目标',
            treeIconCls: 'icon-strategy',
            layout: 'fit',
            onClick: function () {
                //1.加载右侧card
                if (!me.smTree) {
                    me.smTree = Ext.create('FHD.view.kpi.cmp.StrategyMapTree', {
                        rootVisible: true,
                        collapsible: false,
                        showLight: true,
                        border: false,
                        reloadNavigator: function (id, name) {
                            var nid = id.split('_')[0];
                            //选中当前节点
                            me.selectTreeNode(nid, 'sm');
                            //刷新右侧容器数据
                            me.reloadContainerData(nid, 'sm');
                        },
                        listeners: {
                            afteritemexpand: function () {
                                me.firstNodeClick('sm');
                            },
                            itemclick: function (tablepanel, record, item, index, e, options) {
                                var id = record.data.id;
                                //刷新右侧容器数据
                                me.reloadContainerData(id, 'sm', record.data.text);
                            }
                        }
                    });
                    this.add(me.smTree);
                    this.doLayout();
                }

                if (!me.storageSmContainer) {
                    me.storageSmContainer = Ext.create('FHD.view.risk.cmp.container.storage.StorageSmContainer', {
                        navFunId: me.smTree.id,
                        hisCallback: function () {
                            me.smTree.store.load();
                        }
                    });
                    me.cardpanel.add(me.storageSmContainer);
                }

                me.cardpanel.setActiveItem(me.storageSmContainer);
            }
        });
    },

    /**
     * 创建流程容器
     * */
    createProcessTreeContainer: function () {
        var me = this;
        //流程树
        me.processTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '流程',
            treeIconCls: 'icon-ibm-icon-metrictypes',
            layout: 'fit',
            onClick: function () {
                if (!me.processTree) {
                    me.processTree = Ext.create('FHD.view.process.ProcessTree', {
                        border: false,
                        face: me,
                        extraParams: {
                            showLight: true
                        },
                        collapsible: false,
                        reloadNavigator: function (id, name) {
                            var nid = id.split('_')[0];
                            //选中当前节点
                            me.selectTreeNode(nid, 'process');
                            //刷新右侧容器数据
                            me.reloadContainerData(nid, 'process');
                        },
                        listeners: {
                            afteritemexpand: function () {
                                me.firstNodeClick('process');
                            },
                            itemclick: function (tablepanel, record, item, index, e, options) {
                                var id = record.data.id;
                                //刷新右侧容器数据
                                me.reloadContainerData(id, 'process');
                            }
                        }
                    });
                    this.add(me.processTree);
                    this.doLayout();
                }

                if (!me.storageProcessContainer) {
                    me.storageProcessContainer = Ext.create('FHD.view.risk.cmp.container.storage.StorageProcessContainer', {
                        navFunId: me.processTree.id,
                        hisCallback: function () {
                            me.processTree.store.load();
                        }
                    });
                    me.cardpanel.add(me.storageProcessContainer);
                }

                me.cardpanel.setActiveItem(me.storageProcessContainer);
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
        me.node = selectNode;
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
		
        //更改导航
        if (type == 'sm') {
            //目标树特殊处理
            if (id.indexOf('_') != -1) {
                var strategyId = id.split('_')[0];
                me.storageSmContainer.chageNavigationBar(strategyId, name);
            } else {
                me.storageSmContainer.chageNavigationBar(id, '');
            }
        } else {
            icontainer.chageNavigationBar(id, '');
        }

    },


    /**
     * 根据type返回tree或container
     * */
    getTreeOrContainer: function (type, isTree) {
        var me = this;
        var itree = me.riskTree; //树实例
        var icontainer = me.storageRiskContainer; //导航container实例
        if (type == 'org') {
            itree = me.orgTree;
            icontainer = me.storageOrgContainer;
        } else if (type == 'sm') {
            itree = me.smTree;
            icontainer = me.storageSmContainer;
        } else if (type == 'process') {
            itree = me.processTree;
            icontainer = me.storageProcessContainer;
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
        var selectedNode = null;
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
        }
        var id = selectedNode.data.id;

        me.node = selectedNode;
        me.nodeId = id;
        me.nodeType = type;

        icontainer.chageNavigationBar(me.nodeId, '');
        icontainer.reloadData(me.nodeId);
    }
});