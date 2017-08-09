/**
 * 评估结果分析主面板,新改版的
 * @author ZJ
 */
Ext.define('FHD.view.risk.analyse.AnalyseMainPanel', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.analysemainpanel',

    nodeId: null, //左侧选择的树节点id
    nodeType: null, //risk,org,strategy,process

    initComponent: function () {
        var me = this;
        
        var treeArr = [];
        //创建风险容器
        if($ifAnyGranted('ROLE_ALL_ASSESS_ANALYSIS_RISKQUERY_RISK')){
            me.createRiskTreeContainer();
            treeArr.push(me.riskTreeContainer);
		}
        
        //创建左侧折叠树
	    var accordionTree = undefined;
	    if(treeArr.length>0){
	    	accordionTree = Ext.create("FHD.ux.layout.AccordionTree", {
	            title: treeArr[0]['treeTitle'],
	            iconCls: treeArr[0]['treeIconCls'],
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
            treeTitle: '风险',
            treeIconCls: 'icon-ibm-icon-scorecards',
            layout: 'fit',
            onClick: function () {
                if (!me.riskTree) {
                    me.riskTree = Ext.create('FHD.view.risk.cmp.RiskTreePanel', {
                        border: false,
                        rbs: true,
                        showLight: true,
                        typeId: me.typeId,
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
                                    me.analyseRiskContainer.riskFormViewDisable(true);
                                } else {
                                    me.analyseRiskContainer.riskFormViewDisable(false);
                                }
                                //叶子节点才可以添加风险事件
	                            if(record.data.leaf){
	                            	me.analyseRiskContainer.changeAddbuttonStatus(false);
	                            }else{
	                            	me.analyseRiskContainer.changeAddbuttonStatus(true);
	                            }
                                //刷新右侧容器数据
                                me.reloadContainerData(id, 'risk');
                            },
                            load: function () {
                                me.firstNodeClick('risk');
                            },
	                        afteritemexpand: function(node,index,item,eOpts){
	                        	me.expendTreeNode(node.data.id, 'risk');
	                        }
                        }
                    });
                    this.add(me.riskTree);
                    this.doLayout();
                }

                if (!me.analyseRiskContainer) {
                    me.analyseRiskContainer = Ext.create('FHD.view.risk.cmp.container.analyse.AnalyseRiskContainer', {
                        navFunId: me.riskTree.id,
                        showRiskDetailCallback: function(){
                        	me.expendTreeNode(me.nodeId,'risk');
                        },
                        historyCallback: function () {
                            me.riskTree.store.load();
                        }
                    });
                    me.cardpanel.add(me.analyseRiskContainer);
                }
                me.cardpanel.setActiveItem(me.analyseRiskContainer);
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
                        schm: me.typeId,
                        reloadNavigator: function (id, name) {
                            var nid = id.split('_')[0];
                            //选中当前节点
                            me.selectTreeNode(nid, 'org');
                            //刷新右侧容器数据
                            me.reloadContainerData(nid, 'org');
                        },
                        listeners: {
                            load: function () {
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

                if (!me.analyseOrgContainer) {
                    me.analyseOrgContainer = Ext.create('FHD.view.risk.cmp.container.analyse.AnalyseOrgContainer', {
                        navFunId: me.orgTree.id,
                        schm: me.typeId,
                        hisCallback: function () {
                            me.orgTree.store.load();
                        }
                    });
                    me.cardpanel.add(me.analyseOrgContainer);
                }

                me.cardpanel.setActiveItem(me.analyseOrgContainer);

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
                            load: function () {
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

                if (!me.analyseSmContainer) {
                    me.analyseSmContainer = Ext.create('FHD.view.risk.cmp.container.analyse.AnalyseSmContainer', {
                        navFunId: me.smTree.id,
                        schm: me.typeId,
                        hisCallback: function () {
                            me.smTree.store.load();
                        }
                    });
                    me.cardpanel.add(me.analyseSmContainer);
                }

                me.cardpanel.setActiveItem(me.analyseSmContainer);
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
                            load: function () {
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

                if (!me.analyseProcessContainer) {
                    me.analyseProcessContainer = Ext.create('FHD.view.risk.cmp.container.analyse.AnalyseProcessContainer', {
                        navFunId: me.processTree.id,
                        schm: me.typeId,
                        hisCallback: function () {
                            me.processTree.store.load();
                        }
                    });
                    me.cardpanel.add(me.analyseProcessContainer);
                }

                me.cardpanel.setActiveItem(me.analyseProcessContainer);
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
		
        //更改导航
        if (type == 'sm') {
            //目标树特殊处理
            if (id.indexOf('_') != -1) {
                var strategyId = id.split('_')[0];
                me.analyseSmContainer.chageNavigationBar(strategyId, name);
            } else {
                me.analyseSmContainer.chageNavigationBar(id, '');
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
        var icontainer = me.analyseRiskContainer; //导航container实例
        if (type == 'org') {
            itree = me.orgTree;
            icontainer = me.analyseOrgContainer;
        } else if (type == 'sm') {
            itree = me.smTree;
            icontainer = me.analyseSmContainer;
        } else if (type == 'process') {
            itree = me.processTree;
            icontainer = me.analyseProcessContainer;
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
		
		        icontainer.chageNavigationBar(me.nodeId, '');
		        icontainer.reloadData(me.nodeId);
		        //叶子节点才可以添加风险事件
		        if(type == 'risk'){
		            if(selectedNode.data.leaf){
		            	icontainer.changeAddbuttonStatus(false);
		            }else{
		            	icontainer.changeAddbuttonStatus(true);
		            }
		        }
            }
        }
    },
    //展开树节点
    expendTreeNode: function(nodeid,type){
    	var me = this;
        var itree = me.getTreeOrContainer(type, true);
    	if(nodeid != 'root'){
	        var rootNode = itree.getRootNode(); //得到根节点
	        var selectNode = me.findNode(rootNode, nodeid);
	        me.expendTreeChildNode(selectNode,true);
    	}
    },
    //展开树所有子节点
    expendTreeChildNode: function(selectNode,isChild){
    	var me = this;
    	if (!selectNode.isExpanded() && !selectNode.isLeaf()) {
        	selectNode.expand(true);
        }
    	if(isChild){
    		var childnodes = selectNode.childNodes;
	        for (var i = 0; i < childnodes.length; i++) {
	            var node = childnodes[i];
	            if (!node.data.leaf) {
		                me.expendTreeChildNode(node,false); //递归调用
	            }
	        }
    	}
    }
});