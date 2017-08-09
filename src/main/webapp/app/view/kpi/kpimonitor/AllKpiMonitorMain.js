/**
 * 目标管理主面板
 */
Ext.define('FHD.view.kpi.kpimonitor.AllKpiMonitorMain', {
    extend: 'FHD.ux.layout.treeNavigationCardPanel.TreeNavigationCardPanel',
    alias: 'widget.allkpimonitormain',

    /**
     * 树节点ID
     */
    treeNodeId: '',
    /**
     * 树类型
     */
    treeType: '',
    /**
     * 右侧容器重新布局
     */
    reRightLayout: function (c) {
        var me = this;
        me.cardpanel.setActiveItem(c);
        me.cardpanel.doLayout();

    },
    /**
     * 获得选中的树节点信息
     * {nodeId:'节点ID',nodeName:'节点名称'}
     */
    getSelectedTreeItem: function (tree, treeType) {
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
        if (!selectedNode) {
            return;
        }
        var id = selectedNode.data.id;
        var name = selectedNode.data.text;
        me.treeNodeId = id;
        me.treeType = treeType;
        var selectedItem = {
            nodeId: id,
            nodeName: name
        };
        if (treeType == 'all_metric_kpi') {
            me.allKpiTree.currentNode = selectedNode;
        }
        return selectedItem;
    },

    /**
     * 更新右侧所有度量指标容器
     */
    reloadRightMyTreeMainPanel: function (treeid) {

        var me = this;
        me.navData = [];
        me.navData.push({
            type: 'allKpi',
            name: '度量指标',
            id: 'allKpiRoot',
            icon: 'ALLMETRICKPI'
        });
        me.navData.push({
            type: 'allKpi',
            name: '所有度量指标',
            id: treeid,
            containerId: me.myfolderContainer.id
        });
        var paramObj = {
            did: treeid
        };
        me.myKpiGridContainer.onClick();
        me.myKpiGrid.initParam(paramObj);
        me.myKpiGrid.reLoadData();
        me.myKpiGrid.setNavData(me.navData);
        me.navigationBarNew.renderHtml(me.id + 'DIV', me.navData);

    },
    /**
     * 所有度量指标点击节点事件
     */
    allKpiTreeOnClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "all_metric_kpi";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        //所有度量指标节点ID
        var nodeid = selectedItem.nodeId;
        if (nodeid == 'allkpi_root') {
            return;
        }
        //刷新右侧战略目标主容器
        me.reloadRightMyTreeMainPanel(nodeid);
        me.reRightLayout(me.myfolderContainer);
    },
    /**
     *
     * 所有度量指标树默认选中首节点事件
     */
    allKpiTreeFirstNodeClick: function (tree) {
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
                tree.currentNode = selectedNode;
                var treeid = selectedNode.data.id;
                me.treeNodeId = treeid;
                me.treeType = "allKpiTree";
                //刷新右侧部门文件夹主容器
                me.reloadRightMyTreeMainPanel(treeid);
            }
        }

    },
    /**
     * 度量指标树点击事件
     */
    allKpiTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "mytree";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        if (!selectedItem) {
            return;
        }
        //部门文件夹ID
        var treeid = selectedItem.nodeId;
        if (treeid == 'myfolder_root') {
            return;
        }

        //刷新右侧主容器
        me.reloadRightMyTreeMainPanel(treeid);
        me.reRightLayout(me.myfolderContainer);
    },

    /**
     * 刷新右侧战略目标导航
     */
    refreshRightNavigator: function (id, type) {
        var me = this;
        var data = [];
        if (type == "all_metric_kpi") {
            data.push({
                type: 'allKpi',
                name: '度量指标',
                id: 'ALLMETRICKPI',
                icon: 'KPI'
            });
            data.push({
                type: 'allKpi',
                name: '所有度量指标',
                id: id,
                containerId: me.kpiTypeContainer.id
            });
            me.navigationBarNew.renderHtml(me.id + 'DIV', data);
        }
    },

    /**
     * 所有度量指标树容器
     */
    createAllKpiContainer: function () {
        var me = this;

        me.allKpiTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '度量指标',
            authority: 'ROLE_ALL_ENV_TARGET_KPI',
            treeIconCls: 'icon-ibm-icon-summary',
            layout: 'fit',
            onClick: function () {
                me.createAllKpiTree(me, this);
                me.createAllKpiMainContainer(me);
            }
        });
    },

    /**
     * 所有度量指标树
     */
    createAllKpiTree: function (me, c) {
        if (!me.allKpiTree) {
            me.allKpiTree = Ext.create('FHD.view.kpi.cmp.allkpi.AllKpiTree', {
                treeTitle: '度量指标',
                reloadNavigator: function (id) {},
                onItemClick: function (tree, record, item, index, e, eOpts) {
                    me.allKpiTreeOnClick(tree, record, item, index, e, eOpts);
                },
                firstNodeClick: function (store, node, records, successful, eOpts) {
                    me.allKpiTreeFirstNodeClick(me.allKpiTree);
                }
            });
            c.add(me.allKpiTree);
            c.doLayout();
        }
    },
    /**
     * 所有度量指标右侧主容器
     */
    createAllKpiMainContainer: function (me) {
        if (!me.myKpiGridContainer) {
            me.myKpiGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '所有度量指标',
                onClick: function () {
                    if (!me.myKpiGrid) {
                        me.myKpiGrid = Ext.create('FHD.view.kpi.cmp.allkpi.AllKpiGrid', {
                            autoHeight: true,
                            pContainer: me,
                            goback: function () {
                                var did = me.getSelectedTreeItem(me.allKpiTree, 'allKpiTree').nodeId;
                                me.reRightLayout(me.myfolderContainer);
                                me.reloadRightMyTreeMainPanel(did);
                            },
                            reRightLayout: function (p) {
                                if (p.needExtraNav) {
                                    me.kpiContainer = Ext.create("Ext.container.Container", {
                                        layout: {
                                            align: 'stretch',
                                            type: 'vbox'
                                        }
                                    });
                                    me.navKpiObj = {
                                        xtype: 'box',
                                        height: 18,
                                        style: 'border-left: 1px  #99bce8 solid;',
                                        html: '<div id="' + me.kpiContainer.id + 'DIV" class="navigation"></div>'
                                    };
                                    me.kpiContainer.add(me.navKpiObj);
                                    me.kpiContainer.add(p);
                                    me.cardpanel.add(me.kpiContainer);
                                    me.reRightLayout(me.kpiContainer);
                                } else {
                                    me.cardpanel.add(p);
                                    me.reRightLayout(p);
                                }
                            },
                            undo: function () {
                                var did = me.getSelectedTreeItem(me.allKpiTree, 'allKpiTree').nodeId;
                                me.reRightLayout(me.myfolderContainer);
                                me.reloadRightMyTreeMainPanel(did);
                            },
                            reLayoutNavigationBar: function (data) {
                                me.navigationBarNew.renderHtml(me.id + 'DIV', data);
                            }
                        });
                    }
                    this.add(me.myKpiGrid);
                    this.doLayout();
                }
            });
        }
        if (!me.myfolderContainer) {

            me.myfolderTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.myKpiGridContainer]
            });

            me.myfolderContainer = Ext.create('FHD.ux.layout.treeNavigationCardPanel.TreeNavigationTabContainer', {
                border: false,
                tabpanel: me.myfolderTabPanel,
                go: function (param) {
                    me.reRightLayout(me.myfolderContainer);
                    me.reloadRightMyTreeMainPanel(param.id);
                    me.allKpiTree.selectNode(me.allKpiTree.getRootNode(), param.id);
                }
            });

            me.cardpanel.add(me.myfolderContainer);
        }

        me.reRightLayout(me.myfolderContainer);

        if (me.allKpiTree.currentNode) {
            var treeid = me.allKpiTree.currentNode.data.id;
            me.reloadRightMyTreeMainPanel(treeid);
        }
    },


    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        me.navigationBarNew = Ext.create('FHD.ux.NavigationBar');

        me.createAllKpiContainer();

        // 添加了TAB签的权限
        var treeArr = new Array();
        if ($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPI')) {
            treeArr.push(me.allKpiTreeContainer);
        }

        me.accordionTree = Ext.create('FHD.ux.layout.AccordionTree', {
            title: '度量指标',
            iconCls: 'icon-ibm-icon-summary',
            width: 250,
            treeArr: treeArr
        });


        var tabs = [];

        Ext.apply(me, {
            tree: me.accordionTree,
            tabs: tabs,
            listeners: {
                destroy: function (me, eOpts) {
                    me.navigationBarNew.destroy();
                    me.el.remove();
                    me.removeAll(true);
                    me = null;
                    if (Ext.isIE) {
                        CollectGarbage();
                    }
                }
            }
        });


        me.callParent(arguments);

        if ($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPI')) {
            me.allKpiTreeContainer.onClick();
        }

    }

});