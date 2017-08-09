Ext.define('FHD.view.report.ReportMain', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.myallfoldermain',
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
     * 刷新
     * @param {} treeid
     */
    reloadRightMonitorMainPanel: function (treeid) {
        var me = this;
        if ('smKpi' == treeid) {
            me.mySmKpiContainer.onClick();
            me.mySmKpiGrid.reLoadData();
            me.reRightLayout(me.smKpiReportContainer);
        } 
        else if ('scKpi' == treeid) {
            me.scKpiContainer.onClick();
            me.scKpiGrid.reLoadData();
            me.reRightLayout(me.scKpiReportContainer);	
        } else if ('riskKpi' == treeid) {
            me.riskKpiContainer.onClick();
            me.riskKpiGrid.reLoadData();
            me.reRightLayout(me.riskKpiReportContainer);	
        }
    },
    refreshRightNavigator: function (id, type) {
    	var me = this;
    	var containerid = '';
    	if (type == "monitorReport") {
    		if(id == 'smKpi'){
    			containerid = me.smKpiReportContainer.id;
    		}
    	}
    	 me.navigationBar.renderHtml(containerid + 'DIV', id, '', type, me.monitorReportTree.id);
    },
    monitorTreeFirstNodeClick: function (tree) {
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
                me.treeType = "monitor";
                //刷新右侧部门文件夹主容器
                me.reloadRightMonitorMainPanel(treeid);
            }
        }
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
        if (treeType == "monitor") {
            me.monitorReportTree.currentNode = selectedNode;
        }
        return selectedItem;
    },
    createMonitorReportTreeContainer: function () {
        var me = this;
        me.monitorReportTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '监控预警',
            treeIconCls: 'icon-ibm-icon-report-sub',
            layout: 'fit',
            onClick: function () {
                me.createMonitorTree(me, this); // 创建左侧树
                me.createMonitorMainContainer(me); // 创建右侧战略目标主容器
            }
        });
    },
    // 创建监控预警左侧报表目录树
    createMonitorTree: function (me, c) {
        if (!me.monitorReportTree) {
            me.monitorReportTree = Ext.create('FHD.view.report.monitor.MonitorReportTree', {
                treeTitle: '监控预警',
                reloadNavigator: function (id) {
                    me.monitorReportTree.selectNode(me.monitorReportTree.getRootNode(), id);
                    me.reloadRightMyTreeMainPanel(id);
                },
                onItemClick: function (tree, record, item, index, e, eOpts) {
                    me.monitorTreeItemClick(tree, record, item, index, e, eOpts);
                },
                firstNodeClick: function (store, node, records, successful, eOpts) {
                    me.monitorTreeFirstNodeClick(me.monitorReportTree);
                }
            });
            c.add(me.monitorReportTree);
            c.doLayout();
        }
    },
    /**
     * 监控预警报表树点击事件
     */
    monitorTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "monitor";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        
        var treeid = selectedItem.nodeId;
        if (treeid == 'monitor_root') {
            return;
        }
        
        //刷新右侧主容器
        me.reloadRightMonitorMainPanel(treeid);
    },
    createMonitorMainContainer: function (me) {
        if (!me.mySmKpiContainer) {
            me.mySmKpiContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                //title: '战略目标',
                onClick: function () {
                    if (!me.mySmKpiGrid) {
                        me.mySmKpiGrid = Ext.create('FHD.view.kpi.report.SmKpiReportMainPanel', {
                            autoHeight: true,
                            pContainer: me,
                            treeId: me.monitorReportTree.id
                        });
                    }
                    this.add(me.mySmKpiGrid);
                    this.doLayout();
                }
            });
        }
        if (!me.smKpiReportContainer) {
        	me.smKpiReportContainer = Ext.create('Ext.container.Container',{
        		layout:'fit',
        		items: [me.mySmKpiContainer]
        	});
            me.cardpanel.add(me.smKpiReportContainer);
        }
        
         if (!me.scKpiContainer) {
            me.scKpiContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                //title: '战略目标',
                onClick: function () {
                    if (!me.scKpiGrid) {
                        me.scKpiGrid = Ext.create('FHD.view.kpi.report.ScKpiReportMainPanel', {
                            autoHeight: true,
                            pContainer: me
                            //treeId: me.monitorReportTree.id
                        });
                    }
                    this.add(me.scKpiGrid);
                    this.doLayout();
                }
            });
        }
        
       if (!me.scKpiReportContainer) {

        	me.scKpiReportContainer = Ext.create('Ext.container.Container',{
        		layout:'fit',
        		items: [me.scKpiContainer]
        	});
            me.cardpanel.add(me.scKpiReportContainer);
        }
        
        if(!me.riskKpiContainer) {
        	me.riskKpiContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                onClick: function () {
                    if (!me.riskKpiGrid) {
                        me.riskKpiGrid = Ext.create('FHD.view.kpi.report.risk.RiskKpiReportMainPanel', {
                            autoHeight: true,
                            pContainer: me
                        });
                    }
                    this.add(me.riskKpiGrid);
                    this.doLayout();
                }
            });
        }
        
        if(!me.riskKpiReportContainer){
        	me.riskKpiReportContainer = Ext.create('Ext.container.Container',{
        		layout:'fit',
        		items: [me.riskKpiContainer]
        	});
            me.cardpanel.add(me.riskKpiReportContainer);
        }
    },
    initComponent: function () {
        var me = this;
        // 创建监控预警报表树容器
        me.createMonitorReportTreeContainer();
        me.accordionTree = Ext.create('FHD.ux.layout.AccordionTree', {
            title: '监控预警',
            iconCls: 'icon-strategy',
            width: 210,
            treeArr: [me.monitorReportTreeContainer]
        });


        var tabs = [];

        Ext.apply(me, {
            tree: me.accordionTree,
            tabs: tabs
        });


        me.callParent(arguments);

        //设置右侧cardpanel显示border
        me.cardpanel.setBorder(1);
        me.monitorReportTreeContainer.onClick();
    }
})