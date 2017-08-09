/**
 * 文件夹主面板
 */
Ext.define('FHD.view.myallfolder.MyAllFolderMain', {
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
     * 创建部门文件夹树容器
     */
    createDepartmentTreeContainer: function () {
        var me = this;
        me.departmentTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '部门文件夹',
            treeIconCls: 'icon-strategy',
            layout: 'fit',
            authority: 'ROLE_ALL_WORK_FILE_DEP',
            onClick: function () {
                me.createDepartmentTree(me, this); // 创建左侧树
                me.createDepartmentMainContainer(me); // 创建右侧部门文件夹主容器
            }
        });

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
        var id = selectedNode.data.id;
        var name = selectedNode.data.text;
        me.treeNodeId = id;
        me.treeType = treeType;
        var selectedItem = {
            nodeId: id,
            nodeName: name
        };
        me.myTree.currentNode = selectedNode;
        return selectedItem;
    },

    reloadRightMyTreeMainPanel: function (treeid) {
        var me = this;
        var paramObj = {
            did: treeid
        };
        var containerId = "";
        // 导航条信息
        me.navData = [{
               type: 'myFolder',
               icon:'MYFOLDER',
               id: 'myFolder',
               name: '我的文件夹'
        }];
        if ('allkpi' == treeid) {
            me.myKpiGridContainer.onClick();
            me.myKpiGrid.initParam(paramObj);
            me.myKpiGrid.reLoadData();
            me.reRightLayout(me.myfolderContainer);
        }
        if ('myTodo' == treeid) { //王再冉 添加
            me.myTodoGridContainer.onClick();
            me.myTodoGrid.reloadData();
            me.reRightLayout(me.myTodofolderContainer);
        }
        if ('myDone' == treeid) {
            me.myDoneGridContainer.onClick();
            me.myDoneGrid.store.load();
            me.reRightLayout(me.myDonefolderContainer);
        }
        if ('myrisk' == treeid) {
            me.myRiskContainer.onClick();
            me.myRiskEventGrid.reloadData(__user.empId);
            me.reRightLayout(me.myRiskfolderContainer);
        }
        //刷新右侧部门文件夹主容器
        if ('deptkpi' == treeid) {
            me.reloadDeptRightPanel(treeid);
        } else if ('deptsm' == treeid) {
            me.reloadDeptRightPanel(treeid);
        } else if ('deptsc' == treeid) {
            me.reloadDeptRightPanel(treeid);
        } else if ('deptrisk' == treeid) {
            me.reloadDeptRightPanel(treeid);
        } //部门流程
        else if ('orgProcess' == treeid) {
            me.reloadDeptRightPanel(treeid);
        } /*  add by  宋佳    增加风险应对tab */
        else if ("riskResponse" == treeid) {
            me.reloadDeptRightPanel(treeid);
        } else if ("deptHistory" == treeid) {
            me.reloadDeptRightPanel(treeid);
        }

    },
    /**
     * 更新右侧部门文件夹容器
     */
    reloadDeptRightPanel: function (did) {
        var me = this;
        var containerId = "";
        // 导航条信息
        me.navData = [{
               type: 'deptFolder',
               icon:'MYFOLDER',
               id: 'deptFolder',
               name: '部门文件夹'
        }];
        if ('deptkpi' == did) {
        	me.createDeptKpi();
            me.deptKpiGridContainer.onClick();
            me.cardpanel.add(me.deptKpiTabContainer);
            me.reRightLayout(me.deptKpiTabContainer);
            //重新加载页面
            me.deptKpiGrid.reloadData();
            containerId = me.deptKpiTabContainer.id;
            me.navData.push({
               type: 'deptkpi',
               id: did,
               name: '部门指标',
               containerId: containerId
            }); 
        } else if ('deptsm' == did) {
            me.createDeptSm();
            me.deptSmGridContainer.onClick();
            me.cardpanel.add(me.deptSmTabContainer);
            me.reRightLayout(me.deptSmTabContainer);

            var paramObj = {
                dataType: 'sm',
                navid: "deptsm",
                navtype: "departmentfolder",
                treeid: me.myTree.id
            };
            //初始化页面
            me.deptSmGrid.initParam(paramObj);
            //重新加载页面
            me.deptSmGrid.reloadData();
            containerId = me.deptSmTabContainer.id;
            me.navData.push({
               type: 'deptsm',
               id: did,
               name: '部门目标',
               containerId: containerId
            }); 
        } else if ('deptsc' == did) {
            me.createDeptSc();
            me.deptScGridContainer.onClick();
            me.cardpanel.add(me.deptScTabContainer);
            me.reRightLayout(me.deptScTabContainer);

            var paramObj = {
                dataType: 'sc',
                navid: "deptsc",
                navtype: "departmentfolder",
                treeid: me.myTree.id
            };
            //初始化页面
            me.deptScGrid.initParam(paramObj);
            //重新加载页面
            me.deptScGrid.reloadData();
            containerId = me.deptScTabContainer.id;
            me.navData.push({
               type: 'deptsc',
               id: did,
               name: '部门记分卡',
               containerId: containerId
            }); 
        } else if ('deptrisk' == did) {
            me.createDeptRisk();
            me.myfolderRiskCardPanel.setActiveItem(me.myfolderRiskTabPanel);
            me.cardpanel.add(me.myfolderRiskContainer);
            me.reRightLayout(me.myfolderRiskContainer);
            containerId = me.myfolderRiskContainer.id;
            //判断当前激活了那个tab
            var activeTab = me.myfolderRiskTabPanel.getActiveTab();
            if (activeTab) {
                if (me.riskEventGrid && activeTab.id == me.riskEventGrid.id) {//风险事件
                    me.riskEventGrid.initParams('org');
                    me.riskEventGrid.reloadData(__user.majorDeptId);
                } else if (me.orgRiskHistoryGrid && activeTab.id == me.orgRiskHistoryGrid.id) {//历史记录
                    me.orgRiskHistoryGrid.reloadData(__user.majorDeptId);
                } else if (me.orgassessAnalyseCardPanel && activeTab.id == me.orgassessAnalyseCardPanel.id) {//图标分析
                    me.orgassessAnalyseCardPanel.reloadData(__user.majorDeptId);
                }
            }

        } else if ('orgProcess' == did) { //部门流程
            me.createDeptProcess();
            me.cardpanel.add(me.orgProcessTabContainer);
            me.reRightLayout(me.orgProcessTabContainer);
            me.orgProcessContainer.initParam({
                orgId: __user.majorDeptId
            });
            me.orgProcessContainer.reloadData();
        } else if ('riskResponse' == did) { /*宋佳 添加 部门应对*/
            me.createDeptResponse();
            me.responseGridContainer.onClick();
            me.cardpanel.add(me.deptResponseTabContainer);
            me.reRightLayout(me.deptResponseTabContainer);
        } else if ('deptHistory' == did) { //历史事件
            me.createDeptHistory();
            me.cardpanel.add(me.deptHistoryTabContainer);
            me.reRightLayout(me.deptHistoryTabContainer);
            me.depthistoryEventGrid.initParams('dept');
            me.depthistoryEventGrid.reloadData(__user.majorDeptId);
        }
        
    },
    /**
     *
     * 目标树默认选中首节点事件
     */
    departmentTreeFirstNodeClick: function (tree) {
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
                var did = selectedNode.data.id;
                me.treeNodeId = did;
                me.treeType = "department";
                //刷新右侧部门文件夹主容器
                me.reloadDeptRightPanel(did);
            }
        }

    },

    myTreeFirstNodeClick: function (tree) {
        var me = this;
        var selectedNode;
        var nodeItems = tree.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
        }
        if (selectedNode == null) {
            var firstNode = tree.getRootNode().firstChild.firstChild;
            if (null != firstNode) {
                tree.getSelectionModel().select(firstNode);
                selectedNode = firstNode;
                tree.currentNode = selectedNode;
                var treeid = selectedNode.data.id;
                me.treeNodeId = treeid;
                me.treeType = "mytree";
                //刷新右侧部门文件夹主容器
                me.reloadRightMyTreeMainPanel(treeid);
            }
        }

    },

    /**
     * 部门文件夹树点击事件
     */
    departmentTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "department";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        //部门文件夹ID
        var did = selectedItem.nodeId;
        if (did == 'deptmyfolder_root') {
            return;
        }
        //刷新右侧部门文件夹主容器
        if ('deptkpi' == did) {
            me.reloadDeptRightPanel(did);
        } else if ('deptsm' == did) {
            me.reloadDeptRightPanel(did);
        } else if ('deptsc' == did) {
            me.reloadDeptRightPanel(did);
        } else if ('deptrisk' == did) {
            me.reloadDeptRightPanel(did);
        } //部门流程
        else if ('orgProcess' == did) {
            me.reloadDeptRightPanel(did);
        } /*  add by  宋佳    增加风险应对tab */
        else if ("riskResponse" == did) {
            me.reloadDeptRightPanel(did);
        } else if ("deptHistory" == did) {
            me.reloadDeptRightPanel(did);
        }
    },
    myTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "mytree";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        //部门文件夹ID
        var treeid = selectedItem.nodeId;
        if (treeid == 'myfolder_root' || treeid == 'deptmyfolder_root') {
            return;
        }
        //刷新右侧主容器
        me.reloadRightMyTreeMainPanel(treeid);
    },


    /**
     * 创建部门文件夹树
     */
    createDepartmentTree: function (me, c) {
        if (!me.departmentTree) {
            me.departmentTree = Ext.create('FHD.view.myallfolder.DeptTree', {
                reloadNavigator: function (id) {
                    var selNode = me.departmentTree.selectNode(me.departmentTree.getRootNode(), id);
                    if(selNode){
                    	me.departmentTree.currentNode = selNode;
                    }
                    me.reloadDeptRightPanel(id);
                },
                treeTitle: '部门文件夹',
                onItemClick: function (tree, record, item, index, e, eOpts) {
                    me.departmentTreeItemClick(tree, record, item, index, e, eOpts);
                },
                firstNodeClick: function (store, node, records, successful, eOpts) {
                    me.departmentTreeFirstNodeClick(me.departmentTree);
                }
            });
            c.add(me.departmentTree);
            c.doLayout();
        }
    },

    createDeptKpi: function () {
        var me = this;
        if (!me.deptKpiGridContainer) {
            me.deptKpiGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '指标列表',
                onClick: function () {
                    if (!me.deptKpiGrid) {
                        me.deptKpiGrid = Ext.create('FHD.view.myallfolder.kpifolder.DeptRelaKpiGrid', {
                            autoHeight: true,
                            treeId: me.myTree.id,
                            navData: me.navData,
                            goback: function () {
                                var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reloadDeptRightPanel(dbid);
                            },
                            reRightLayout: function (p) {
                                me.cardpanel.add(p);
                                me.reRightLayout(p);
                            },
                            undo: function () {
                                var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reloadDeptRightPanel(dbid);
                            }
                        });
                    }
                    this.add(me.deptKpiGrid);
                    this.doLayout();
                }
            });
        }
        if (!me.deptKpiTab) {
            me.deptKpiTab = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.deptKpiGridContainer]
            });

            me.deptKpiTabContainer = Ext.create("FHD.ux.layout.treeTabFace.TreeTabContainer", {
                border: false,
                tabpanel: me.deptKpiTab,
                go:function(){
                	var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                    me.reloadDeptRightPanel(dbid);
                }
            });

        }
    },

    createDeptSm: function () {
        var me = this;
        if (!me.deptSmGridContainer) {
            me.deptSmGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '目标列表',
                onClick: function () {
                    if (!me.deptSmGrid) {
                        me.deptSmGrid = Ext.create('FHD.view.myallfolder.kpifolder.DeptRelaSmGrid', {
                            autoHeight: true,
                            treeId: me.myTree.id,
                            navData: me.navData,
                            goback: function () {
                                var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reloadDeptRightPanel(dbid);
                            },
                            reRightLayout: function (p) {
                                me.cardpanel.add(p);
                                me.reRightLayout(p);
                            },
                            removePanel:function(p){
                            	me.cardpanel.remove(p,true);
                            },
                            undo: function () {
                                var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reloadDeptRightPanel(dbid);
                            }
                        });
                    }
                    this.add(me.deptSmGrid);
                }
            });
        }
        if (!me.deptSmTab) {
            me.deptSmTab = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.deptSmGridContainer]
            });
            me.deptSmTabContainer = Ext.create("FHD.ux.layout.treeTabFace.TreeTabContainer", {
                border: false,
                tabpanel: me.deptSmTab,
                go:function(){
                	var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                    me.reloadDeptRightPanel(dbid);
                },
                goback:function(){
                	var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                    me.reloadDeptRightPanel(dbid);
                }
            });
        }
    },

    createDeptSc: function () {
        var me = this;
        if (!me.deptScGridContainer) {
            me.deptScGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '记分卡列表',
                onClick: function () {

                    if (!me.deptScGrid) {
                        me.deptScGrid = Ext.create('FHD.view.myallfolder.kpifolder.DeptRelaScGrid', {
                            autoHeight: true,
                            treeId: me.myTree.id,
                            navData: me.navData,
                            goback: function () {
                                var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reloadDeptRightPanel(dbid);
                            },
                            reRightLayout: function (p) {
                                me.cardpanel.add(p);
                                me.reRightLayout(p);
                            },
                            removePanel:function(p){
                            	me.cardpanel.remove(p,true);
                            },
                            undo: function () {
                                var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reloadDeptRightPanel(dbid);
                            }
                        });
                    }
                    this.add(me.deptScGrid);
                    this.doLayout();
                }
            });
        }
        if (!me.deptScTab) {
            me.deptScTab = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.deptScGridContainer]
            });
            me.deptScTabContainer = Ext.create("FHD.ux.layout.treeTabFace.TreeTabContainer", {
                border: false,
                tabpanel: me.deptScTab,
                go:function(){
                	var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                    me.reloadDeptRightPanel(dbid);
                }
            });
        }
    },

    createDeptRisk: function () {
        var me = this;
        if (!me.myfolderRiskContainer) {
            me.riskEventGridContainer = Ext.create('Ext.container.Container', {
                authority: 'ROLE_ALL_WORK_FILE_DEP_RISK_RISKLIST',
                layout: 'fit',
                title: '风险事件排序',
                listeners: {
                    afterrender: function () {
                        if (!me.riskEventGrid) {
                            me.riskEventGrid = Ext.create('FHD.view.risk.cmp.risk.IRiskEventGrid', {
                                border: false,
                                navData: me.navData,
                                navHeight : 0,
                                type: 'org',
                                showRiskAdd: function (p, parentId, name) {
                                    me.cardpanel.add(p);
                                    me.reRightLayout(p);
                                },
                                showRiskDetail: function (p, parentId, name) {
                                    me.cardpanel.add(p);
                                    me.reRightLayout(p);
                                },
                                goback: function () {
                                    var dbid = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                    me.reloadDeptRightPanel(dbid);
                                    me.riskEventGrid.reloadData();
                                }
                            });
                            this.add(me.riskEventGrid);
                            this.doLayout();
                        }
                        if (__user.majorDeptId != '') {
                            me.riskEventGrid.reloadData(__user.majorDeptId);
                        }
                    }
                }
            });
            me.riskEventGrid = Ext.create('FHD.view.risk.cmp.risk.IRiskEventGrid', {
                border: false,
                navData: me.navData,
                navHeight : 0,
                type: 'org',
                showRiskAdd: function (p, parentId, name) {
                	me.myfolderRiskCardPanel.add(p);
                	me.myfolderRiskCardPanel.setActiveItem(p);
                },
                showRiskDetail: function (p, parentId, name) {
                    me.myfolderRiskCardPanel.add(p);
                	me.myfolderRiskCardPanel.setActiveItem(p);
                },
                goback: function () {
                    me.myfolderRiskCardPanel.setActiveItem(me.myfolderRiskTabPanel);
                    me.riskEventGrid.reloadData();
                
                }
            });
            me.riskEventGridContainer.add(me.riskEventGrid);

            //十大风险，郑军祥添加
            me.top10Grid = Ext.create('FHD.view.report.risk.Top10RiskOrgGrid', {
                authority: 'ROLE_ALL_WORK_FILE_DEP_TENRISK',
                layout: 'fit',
                title: '风险排序',
                showRiskDetail: function (p, parentId, name) {
                    me.myfolderRiskCardPanel.add(p);
                	me.myfolderRiskCardPanel.setActiveItem(p);
                },
                goback: function () {
                    me.myfolderRiskCardPanel.setActiveItem(me.myfolderRiskTabPanel);
                    me.riskEventGrid.reloadData();
                },
                reRightLayout: function (p) {
			        me.myfolderRiskCardPanel.add(p);
                	me.myfolderRiskCardPanel.setActiveItem(p);
			    }
            });


            //历史记录页
            me.orgRiskHistoryGridContainer = Ext.create('Ext.container.Container', {
                authority: 'ROLE_ALL_WORK_FILE_DEP_RISK_HISTORY',
                layout: 'fit',
                title: '历史数据',
                listeners: {
                    afterrender: function () {
                        if (!me.orgRiskHistoryGrid) {
                            me.orgRiskHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
                                face: me,
                                type: 'org',
                                border: false,
                                height: FHD.getCenterPanelHeight() - 47
                            });
                            this.add(me.orgRiskHistoryGrid);
                            this.doLayout();
                        }
                        //根据左侧选中节点，初始化数据
                        if (__user.majorDeptId != '') {
                            me.orgRiskHistoryGrid.reloadData(__user.majorDeptId);
                        }
                    }
                }
            });
            me.orgRiskHistoryGrid = Ext.create('FHD.view.risk.cmp.risk.RiskHistoryGrid', {
                face: me,
                type: 'org',
                border: false,
                height: FHD.getCenterPanelHeight() - 47
            });
            me.orgRiskHistoryGridContainer.add(me.orgRiskHistoryGrid);
            //图表分析
            me.orgassessAnalyseContainer = Ext.create('Ext.container.Container', {
                authority: 'ROLE_ALL_WORK_FILE_DEP_RISK_CHART',
                title: '图表分析',
                layout: 'fit',
                listeners: {
                    afterrender: function () {
                        if (!me.orgassessAnalyseCardPanel) {
                            me.orgassessAnalyseCardPanel = Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel', {
                                face: me,
                                type: 'org',
                                border: false
                            });
                            this.add(me.orgassessAnalyseCardPanel);
                            this.doLayout();
                        }
                        if (__user.majorDeptId != '') {
                            me.orgassessAnalyseCardPanel.reloadData(__user.majorDeptId);
                        }
                    }
                }
            });
            me.orgassessAnalyseCardPanel = Ext.create('FHD.view.risk.analyse.AssessAnalyseCardPanel', {
                face: me,
                type: 'org',
                border: false
            });
            me.orgassessAnalyseContainer.add(me.orgassessAnalyseCardPanel);
            me.orgassessAnalyseContainer.doLayout();

            me.myfolderRiskTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.riskEventGridContainer, me.top10Grid, me.orgassessAnalyseContainer, me.orgRiskHistoryGridContainer],
                listeners: {	
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                    	if(newCard == me.top10Grid){
                    		me.top10Grid.reloadData();
                    	}
                    }
                }
            });
            
            me.myfolderRiskCardPanel = Ext.create('FHD.ux.CardPanel',{
            	border : false,
            	items : me.myfolderRiskTabPanel,
            	flex : 1
            });

            me.myfolderRiskContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                tabpanel: me.myfolderRiskCardPanel,
                go:function(){
                	me.myfolderRiskCardPanel.setActiveItem(me.myfolderRiskTabPanel);
                	
                }
            });

        }
    },

    createDeptProcess: function () {
        var me = this;
        if (!me.orgProcessContainer) {
            me.orgProcessContainer = Ext.create("FHD.view.icm.statics.IcmMyProcessInfo", {
                title: '部门流程',
                layout: 'fit',
                displayChart: false
            });
            me.orgProcessContainer.initParam({
                orgId: __user.majorDeptId
            });
        }
        if (!me.orgProcessTab) {
            me.orgProcessTab = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.orgProcessContainer]
            });

            me.orgProcessTabContainer = Ext.create("FHD.ux.layout.treeTabFace.TreeTabContainer", {
                border: false,
                tabpanel: me.orgProcessTab
            });

        }
    },

    createDeptResponse: function () {
        var me = this;
        if (!me.responseGridContainer) {
            me.responseGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '应对列表',
                onClick: function () {
                    if (!me.deptResponseEditPanel) {
                        me.deptResponseEditPanel = Ext.create('FHD.view.response.new.SolutionEditPanel', {
                        	type : 'dept',
                        	businessType : 'solution',
                        	navData: me.navData,
                            autoHeight: true
                        });
	                    this.add(me.deptResponseEditPanel);
	                    this.doLayout();
                    }
                    me.deptResponseEditPanel.initParam({
		                type: '0',
		                selectId: __user.majorDeptId
		            });
					me.deptResponseEditPanel.reloadData();

                }
            });
        }
        if (!me.measureGridContainer) {
            me.measureGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '控制措施',
                onClick: function () {
                    if (!me.deptMeasureEditPanel) {
                        me.deptMeasureEditPanel = Ext.create('FHD.view.response.new.SolutionEditPanel', {
                        	type : 'dept',
                        	businessType : 'measure',
                        	navData: me.navData,
                            autoHeight: true
                        });
	                    this.add(me.deptMeasureEditPanel);
	                    this.doLayout();
                    }
                    me.deptMeasureEditPanel.initParam({
		                type: '0',
		                selectId: __user.majorDeptId
		            });
					me.deptMeasureEditPanel.reloadData();
                }
            });
        }
        if (!me.deptResponseTab) {
            me.deptResponseTab = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.responseGridContainer,me.measureGridContainer],
                listeners: {
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                    	if(newCard){
							newCard.onClick();
                    	}
                    }
                }
            });

            me.deptResponseTabContainer = Ext.create("FHD.ux.layout.treeTabFace.TreeTabContainer", {
                border: false,
                tabpanel: me.deptResponseTab,
                go:function(){
                	if(me.deptResponseTab.getLayout().getActiveItem().id == me.responseGridContainer.id){
                		me.deptResponseEditPanel.go();
                	}else if(me.deptResponseTab.getLayout().getActiveItem().id == me.measureGridContainer.id){
                		me.deptMeasureEditPanel.go();
                	}
                }
            });

        }
    },

    createDeptHistory: function () {
        var me = this;
        if (!me.depthistoryEventGrid) {
            me.depthistoryEventGrid = Ext.create('FHD.view.risk.hisevent.HistoryEventGridPanel', {
                type : 'dept',
                title: '历史事件',
                border: false
            });
        }
        if (!me.deptHistoryTab) {
            me.deptHistoryTab = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.depthistoryEventGrid]
            });

            me.deptHistoryTabContainer = Ext.create("FHD.ux.layout.treeTabFace.TreeTabContainer", {
                border: false,
                tabpanel: me.deptHistoryTab
            });

        }
    },

    /**
     * 创建部门文件夹右侧主面板
     */
    createDepartmentMainContainer: function (me) {
        //部门指标
        me.createDeptKpi();

        if (me.departmentTree.currentNode) {
            var dbid = me.departmentTree.currentNode.data.id;
            me.reloadDeptRightPanel(dbid);
        }
    },

    /**
     * 我的文件夹树容器
     */
    createMyTreeContainer: function () {
        var me = this;

        me.myTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '个人工作台',
            treeIconCls: 'icon-ibm-new-group-view',
            layout: 'fit',
            authority: 'ROLE_ALL_WORK_FILE_MY',
            onClick: function () {
                me.createMyTree(me, this);
                me.createMyMainContainer(me);
            }
        });
    },

    /**
     * 我的文件夹右侧主容器
     */
    createMyMainContainer: function (me) {
        if (!me.myKpiGridContainer) {
            me.myKpiGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '度量指标',
                onClick: function () {
                    if (!me.myKpiGrid) {
                        me.myKpiGrid = Ext.create('FHD.view.kpi.cmp.myfolder.MyKpiGrid', {
                            autoHeight: true,
                            pContainer: me,
                            navData: me.navData,
                            treeId: me.myTree.id,
                            goback: function () {
                                var did = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reRightLayout(me.myfolderContainer);
                            },
                            reRightLayout: function (p) {
                                me.cardpanel.add(p);
                                me.reRightLayout(p);
                            },
                            undo: function () {
                                var did = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reRightLayout(me.myfolderContainer);
                                me.myKpiGrid.store.load();
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
            me.myfolderContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                tabpanel: me.myfolderTabPanel,
                go:function(){
                    var did = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                    me.reRightLayout(me.myfolderContainer);
                }
            });
            me.cardpanel.add(me.myfolderContainer);
        }

        //我的待办------by wangzairan
        if (!me.myTodoGridContainer) {
            me.myTodoGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '我的待办',
                onClick: function () {
                    if (!me.myTodoGrid) {
                        me.myTodoGrid = Ext.create('FHD.view.bpm.mywork.MyTask', {
                            autoHeight: true,
                            border: false,
                            pContainer: me,
                            treeId: me.myTree.id,
                            goback: function () {
                                var did = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reRightLayout(me.myTodofolderContainer);
                            },
                            reRightLayout: function (p) {
                                me.cardpanel.add(p);
                                me.reRightLayout(p);
                            },
                            undo: function () {
                                var did = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reRightLayout(me.myTodofolderContainer);
                                me.myTodoGrid.store.load();
                            }
                        });
                    }
                    this.add(me.myTodoGrid);
                    this.doLayout();
                }
            });
        }
        if (!me.myTodofolderContainer) {
            me.myTodofolderTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.myTodoGridContainer]
            });
            me.myTodofolderContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                tabpanel: me.myTodofolderTabPanel
            });
            me.cardpanel.add(me.myTodofolderContainer);
        }

        //我的已办---by wangzairan
        if (!me.myDoneGridContainer) {
            me.myDoneGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '我的已办',
                onClick: function () {
                    if (!me.myDoneGrid) {
                        me.myDoneGrid = Ext.create('FHD.view.bpm.processinstance.ProcessInstanceGrid', {
                            autoHeight: true,
                            pContainer: me,
                            border: false,
                            treeId: me.myTree.id,
                            goback: function () {
                                var did = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reRightLayout(me.myDonefolderContainer);
                            },
                            reRightLayout: function (p) {
                                me.cardpanel.add(p);
                                me.reRightLayout(p);
                            },
                            undo: function () {
                                var did = me.getSelectedTreeItem(me.myTree, 'mytree').nodeId;
                                me.reRightLayout(me.myDonefolderContainer);
                                me.myDoneGrid.store.load();
                            }
                        });
                    }
                    this.add(me.myDoneGrid);
                    this.doLayout();
                }
            });
        }
        
        //我的风险
        if (!me.myRiskContainer) {
            me.myRiskContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '风险列表',
                onClick : function(){
                    if (!me.myRiskEventGrid) {
                        me.myRiskEventGrid = Ext.create('FHD.view.risk.cmp.risk.IRiskEventGrid', {
                            border: false,
                            type : 'myfolder',
	                        showRiskAdd: function (p, parentId, name) {
	                            me.cardpanel.add(p);
	                            me.reRightLayout(p);
	                        },
	                        showRiskDetail: function (p, parentId, name) {
	                            me.cardpanel.add(p);
	                            me.reRightLayout(p);
	                        },
	                        goback: function () {
	                            me.reRightLayout(me.myRiskfolderContainer);
	                            me.myRiskEventGrid.reloadData();
	                        }
                        });
                        this.add(me.myRiskEventGrid);
                        this.doLayout();
                    }
                    if (__user.majorDeptId != '') {
                        me.myRiskEventGrid.reloadData(__user.empId);
                    }
                }
            });
        }
        
        if (!me.myRiskfolderContainer) {
            me.myRiskfolderTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.myRiskContainer]
            });
            me.myRiskfolderContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                tabpanel: me.myRiskfolderTabPanel
            });
            me.cardpanel.add(me.myRiskfolderContainer);
        }
        
        if (!me.myDonefolderContainer) {
            me.myDonefolderTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: [me.myDoneGridContainer]
            });
            me.myDonefolderContainer = Ext.create('FHD.ux.layout.treeTabFace.TreeTabContainer', {
                border: false,
                tabpanel: me.myDonefolderTabPanel
            });
            me.cardpanel.add(me.myDonefolderContainer);
        }


        if (me.myTree.currentNode) {
            var treeid = me.myTree.currentNode.data.id;
            me.reloadRightMyTreeMainPanel(treeid);
        }
    },

    /**
     * 我的文件夹树
     */
    createMyTree: function (me, c) {
        if (!me.myTree) {
            me.myTree = Ext.create('FHD.view.kpi.cmp.myfolder.MyTree', {
                treeTitle: '个人工作台',
                reloadNavigator: function (id) {
                    me.myTree.selectNode(me.myTree.getRootNode(), id);
                    me.reloadRightMyTreeMainPanel(id);
                },
                onItemClick: function (tree, record, item, index, e, eOpts) {
                    me.myTreeItemClick(tree, record, item, index, e, eOpts);
                },
                firstNodeClick: function (store, node, records, successful, eOpts) {
                    me.myTreeFirstNodeClick(me.myTree);
                }
            });
            c.add(me.myTree);
            c.doLayout();
        }
    },


    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        
        
        me.createMyTreeContainer();

//        me.createDepartmentTreeContainer();

        var treeArr = new Array();

        if ($ifAnyGranted('ROLE_ALL_WORK_FILE_MY')) {
            treeArr.push(me.myTreeContainer);
        }
//        if ($ifAnyGranted('ROLE_ALL_WORK_FILE_DEP')) {
//            treeArr.push(me.departmentTreeContainer);
//        }

        me.accordionTree = Ext.create('FHD.ux.layout.AccordionTree', {
            title: "",
            iconCls: 'icon-strategy',
            width: 210,
            treeArr: treeArr
        });


        var tabs = [];

        Ext.apply(me, {
            tree: me.accordionTree,
            tabs: tabs,
            listeners: {
                destroy: function (me, eOpts) {
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

        //设置右侧cardpanel显示border
        me.cardpanel.setBorder(1);
        if ($ifAnyGranted('ROLE_ALL_WORK_FILE_MY')) {
            me.accordionTree.setTitle("个人工作台");
            me.myTreeContainer.onClick();
        } else if ($ifAnyGranted('ROLE_ALL_WORK_FILE_DEP')) {
            me.accordionTree.setTitle("部门文件夹");
            me.departmentTreeContainer.onClick();
        }

    }
});