/**
 * 指标监控主面板
 */
Ext.define('FHD.view.kpi.kpimonitor.KpiMonitorMain', {
    extend: 'FHD.ux.layout.treeNavigationCardPanel.TreeNavigationCardPanel',
    alias: 'widget.kpimonitormain',

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
     * 初始化战略目标基本信息页签
     */
    initSmBasicInfo: function (paramObj) {
        var me = this;
        me.strBasicInfoContainer.initSmBasicInfo(paramObj);
    },

    /**
     * 创建战略目标树容器
     */
    createStrTreeContainer: function () {
        var me = this;
        me.strTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '战略目标',
            treeIconCls: 'icon-strategy',
            layout: 'fit',
            onClick: function () {
            	me.createStrTree(me, this); // 创建左侧树
                me.createStrMainContainer(); // 创建右侧战略目标主容器
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
        if(!selectedNode){
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
        if (treeType == "sm") {
            me.strTree.currentNode = selectedNode;
        }else if(treeType=='mytree'){
        	me.myTree.currentNode = selectedNode;
        }
        return selectedItem;
    },
    
    reloadRightMyTreeMainPanel:function(treeid){
    	var me = this;
    	var paramObj = {
    		smid: treeid
    	};

    	me.myKpiGridContainer.onClick();
    	me.myKpiGrid.initParam(paramObj);
	    me.myKpiGrid.reLoadData();
	    me.refreshRightNavigator(treeid, "myfolder");
    },
    /**
     * 更新右侧战略目标容器
     */
    reloadRightStrMainPanel: function (smid) {
        var me = this;
        me.navData = me.getSmNavInfoById(smid);        
        var activeType = me.strMainTabPanel.activeType;
        if (activeType == "strbasicinfo") { //激活的tab为基本信息页签

            var paramObj = {
                smid: smid, //目标ID
                parentid: 'sm_root', //父目标ID
                parentname: '目标库', //父目标名称
                editflag: true //是否是编辑状态
            };
            me.initSmBasicInfo(paramObj);

        } else if (activeType == "strgrid") {
            me.smKpiGrid.setNavData(me.navData);
            var paramObj = {
                smid: smid,//目标ID
                smname: me.getSelectedTreeItem(me.strTree, 'sm').nodeName
            };
            me.smKpiGrid.initParam(paramObj);
            //重新加载度量指标
            me.smKpiGrid.reLoadData();

        } else if (activeType == "strchart") {
            var paramObj = {
                objectId: smid, //目标ID
                dataType:'str'
            };
            //初始化图表分析页面
            me.chartanalysis.initParam(paramObj);
            //重新加载图表分析页面
            me.chartanalysis.reloadData();

        } else if (activeType == "analysis") {
        	me.riskanalysisgridpanel.setNavData(me.navData);
            me.riskanalysisgridpanel.reloadData(smid);
        } else if (activeType == "hisevent") {
            me.historyEventGrid.initParams('sm');
            me.historyEventGrid.reloadData(smid);
        } else if(activeType =="strhistorygrid"){
        	var paramObj = {
                objectId: smid, //目标ID
                type:'str'
            };
            me.strHistoryGrid.initParam(paramObj);
        	me.strHistoryGrid.reloadData();
        }else if(activeType == "riskresponse"){
        	 me.solutioneditpanel.initParam({
                        type: 'sm',
                        selectId: smid
             });
             me.solutioneditpanel.reloadData();
        } else if (activeType =="graphAnalyse"){
        	me.graphAnalysePanel.initParam({
                 strategyMapId:smid        	
        	})
        	me.graphAnalysePanel.reloadData();
        }
       me.navigationBarNew.renderHtml(me.id + 'DIV', me.navData);
    },
    /**
     *
     * 目标树默认选中首节点事件
     */
    strTreeFirstNodeClick: function (tree) {
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
                var smid = selectedNode.data.id;
                me.treeNodeId = smid;
        		me.treeType = "sm";
                //刷新右侧战略目标主容器
                me.reloadRightStrMainPanel(smid);
            }
        }
        if(me.strBasicInfoContainer){
        	me.strBasicInfoContainer.addflag = false;
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
            var firstNode = tree.getRootNode().firstChild;
            if (null != firstNode) {
                tree.getSelectionModel().select(firstNode);
                selectedNode = firstNode;
                tree.currentNode = selectedNode;
                var treeid = selectedNode.data.id;
                me.treeNodeId = treeid;
        		me.treeType = "mytree";
                //刷新右侧战略目标主容器
                me.reloadRightMyTreeMainPanel(treeid);
            }
        }

    },
    
    /**
     * 战略目标树点击事件
     */
    strTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "sm";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        if(!selectedItem){
        	return ;
        }
        //战略目标ID
        var smid = selectedItem.nodeId;
        if(smid=='sm_root'){
        	return;
        }
        
        //刷新右侧战略目标主容器
        me.reloadRightStrMainPanel(smid);
        me.reRightLayout(me.strMainContainer);
        if(me.strBasicInfoContainer){
        	me.strBasicInfoContainer.addflag = false;
        }
    },
    myTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "mytree";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        //战略目标ID
        var treeid = selectedItem.nodeId;
        if(treeid=='myfolder_root'){
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
        if (type == "sm") {
        	var data = me.getSmNavInfoById(id);
            me.navigationBarNew.renderHtml(me.id + 'DIV',data);
        }

    },

    /**
     * 创建战略目标树
     */
    createStrTree: function (me, c) {
        if (!me.strTree) {
            me.strTree = Ext.create('FHD.view.kpi.cmp.sm.SmTree', {
            	reloadNavigator:function(id){            		    
	                me.strTree.selectNode(me.strTree.getRootNode(),id);
	                me.reloadRightStrMainPanel(id);
	                me.reRightLayout(me.strMainContainer);
                },
                onItemClick: function (tree, record, item, index, e, eOpts) {
                    me.strTreeItemClick(tree, record, item, index, e, eOpts);
                },
                firstNodeClick: function (store, node, records, successful, eOpts) {
                    me.strTreeFirstNodeClick(me.strTree);
                },
                smLevelHandler: function (id, name) { //添加下级,同级菜单执行函数
                	if(me.strBasicInfoContainer){
                		me.strBasicInfoContainer.addflag = true;
                	}
                	var addData = me.getSmNavInfoById(id);
                	addData.push({
                	      id: 'newSm',
                	      name:'添加目标',
                	      type:'strategy_map'
                	});
                    me.strMainTabPanel.setActiveTab(me.strBasicInfoContainer);
                    me.cardpanel.setActiveItem(me.strMainContainer);
                    var paramObj = {
                        parentid: id,
                        smid: 'undefined',
                        parentname: name,
                        editflag: false
                    };
                    me.initSmBasicInfo(paramObj);
                    me.strBasicInfoContainer.navToFirst();
                    me.navigationBarNew.renderHtml(me.id + 'DIV', addData);
                },
                deleteLast:function(node){
                	var parentnode = node.parentNode;
                    me.strTree.currentNode.removeChild(node);
                    if (!me.strTree.currentNode.hasChildNodes() && null != me.strTree.currentNode.parentNode) {
                        var oldnode = me.strTree.currentNode;
                        var newnode = me.strTree.currentNode;
                        newnode.data.leaf = true;
                        me.strTree.currentNode.parentNode.replaceChild(newnode, oldnode);
                    }
                    me.strTree.getSelectionModel().select(parentnode);
                    me.strTreeItemClick(me.strTree);
                }
            });
            c.add(me.strTree);
            c.doLayout();
        }
    },

    /**
     * 创建战略目标右侧主面板
     */
    createStrMainContainer: function () {

    	var me = this;
    	if (!me.strMainContainer) {
        	var tabArr = [];
        	if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_KPI')){
        		//度量指标页签
                if (!me.strGridContainer) {
                    me.strGridContainer = Ext.create('Ext.container.Container', {
                        layout: 'fit',
                        title: '度量指标',
                        authority:'ROLE_ALL_MONITOR_KPIMONITOR_KPI',
                        onClick: function () {
                            if (!me.smKpiGrid) {
                                me.smKpiGrid = Ext.create('FHD.view.kpi.cmp.sm.SmKpiGrid', {
                                    autoHeight: true,
                                    navData: me.navData,
                                    goback: function() {
        						        var smid = me.getSelectedTreeItem(me.strTree,'sm').nodeId;
        						        me.strMainTabPanel.activeType = 'strgrid';
        						        me.reRightLayout(me.strMainContainer);
        						        me.reloadRightStrMainPanel(smid);
            						},
            						reRightLayout:function(p){
						                	if(p.needExtraNav){
						                    		me.kpiContainer = Ext.create("Ext.container.Container",{
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
			        				reLayoutNavigationBar: function(data){
					            		me.navigationBarNew.renderHtml(me.id + 'DIV', data);
					            	},
            						undo: function(){
            							var smid = me.getSelectedTreeItem(me.strTree,'sm').nodeId;
        						        me.strMainTabPanel.activeType = 'strgrid';
        						        me.reRightLayout(me.strMainContainer);
        						        me.reloadRightStrMainPanel(smid);
            						}
                                });
                            }
                            this.add(me.smKpiGrid);
                            this.doLayout();
                            me.strMainTabPanel.activeType = "strgrid";
                        }
                    });
                }
        		tabArr.push(me.strGridContainer);
        	}
        	if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_RISK')){
        		if (!me.riskanalysisgridContainer) {
                    me.riskanalysisgridContainer = Ext.create('Ext.container.Container', {
        				title : '风险分析',
        				layout : 'fit',
        				authority:'ROLE_ALL_MONITOR_KPIMONITOR_RISK',
        				border : false,
        				onClick : function() {
        					if (!me.riskanalysisgridpanel) {
        						me.riskanalysisgridpanel = Ext.create('FHD.view.risk.cmp.risk.RiskAnalysisGridPanel',{
        							type : 'sm',
        							navData: me.navData,
        							navHeight: 0,
        							showRiskAdd : function(p,name) {
        								me.cardpanel.add(p);
        								me.reRightLayout(p);
        							},
        							showRiskDetail : function(p,name) {
        								me.cardpanel.add(p);
        								me.reRightLayout(p);
        							},
        							goback : function() {
        								me.reRightLayout(me.strMainContainer);
        								
        							},
        						    reLayoutNavigationBar: function(data){
					            		me.navigationBarNew.renderHtml(me.id + 'DIV', data);
					            	}
        						});
        						this.add(me.riskanalysisgridpanel);
        						this.doLayout();
        					}
        					me.strMainTabPanel.activeType = "analysis";
        				}
        			});
                }
        		tabArr.push(me.riskanalysisgridContainer);
        	}
        	if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_CHART')){
        		//图表分析页签
                if (!me.strChartanalysisContainer) {
                    me.strChartanalysisContainer = Ext.create('Ext.container.Container', {
                        title: '图表分析',
                        border: false,
                        layout:'fit',
                        authority:'ROLE_ALL_MONITOR_KPIMONITOR_CHART',
                        onClick: function () {
                            if (!me.chartanalysis) {
                                me.chartanalysis = Ext.create('FHD.view.kpi.cmp.chart.ChartMainPanel', {
                                    dataType: 'str'
                                });
                                this.add(me.chartanalysis);
                                this.doLayout();
                            }
                            me.strMainTabPanel.activeType = "strchart";
                        }

                    });
                }
				tabArr.push(me.strChartanalysisContainer);
			}

        	if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_GRAPH')){
        		if(!me.graphAnalyseContainer) {
                	me.graphAnalyseContainer = Ext.create('Ext.container.Container',{
                	    title:'图形分析',
                	    layout: 'fit',
                        border: false,
                        authority:'ROLE_ALL_MONITOR_KPIMONITOR_GRAPH',
                        onClick: function () {
                        	if(!me.graphAnalysePanel){
                        		me.graphAnalysePanel = Ext.create('FHD.view.comm.graph.GraphRelaStrategyMapPanel',{
                	                 title:'图形分析',
                	                 extraParams: {
                	                 	strategyMapId: me.treeNodeId
                	                 }
                	            })
                        	}
                        	me.graphAnalysePanel.reloadData();
                            me.strMainTabPanel.activeType = "graphAnalyse";
                            this.add(me.graphAnalysePanel);
                            this.doLayout();
                        }
                	});
                	
                }
        		tabArr.push(me.graphAnalyseContainer);
        	}

        	if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_HISTORY')){
        		if(!me.strHistoryGridContainer){
            		me.strHistoryGridContainer = Ext.create('Ext.container.Container', {
            			title: '历史数据',
                        layout: 'fit',
                        authority:'ROLE_ALL_MONITOR_KPIMONITOR_HISTORY',
                        border: false,
                        onClick: function () {
                        	if(!me.strHistoryGrid){
                        		me.strHistoryGrid = Ext.create('FHD.view.kpi.cmp.sc.ScSmHistoryGrid');
                        		this.add(me.strHistoryGrid);
                            	this.doLayout();
                        	}
                        	me.strMainTabPanel.activeType = "strhistorygrid";
                        }
            		});
            	
            	}
        		tabArr.push(me.strHistoryGridContainer);
        	}
        	if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_BASIC')){
        		//基本信息容器
                if (!me.strBasicInfoContainer) {
                    me.strBasicInfoContainer = Ext.create('FHD.view.kpi.cmp.sm.SmBasicInfoContainer', {
                    	authority:'ROLE_ALL_MONITOR_KPIMONITOR_BASIC',
                        appendTreeNode: function (node) {
                            me.strTree.appendTreeNode(node);
                        },
                        refreshRightNavigator: function (smid) {
                            me.refreshRightNavigator(smid, "sm");
                        },
                        undo: function () {
                            if (me.strMainTabPanel) {
                                me.strMainTabPanel.setActiveTab(0);
                            }
                        },
                        setActiveType: function () {
                            me.strMainTabPanel.activeType = "strbasicinfo";
                        },
                        getCurrentTreeNode: function () {
                            return me.strTree.getCurrentTreeNode();
                        },
                        updateTreeNode: function (data) {
                            me.strTree.updateTreeNode(data);
                        },
                        selectTreeNode: function(data){
                        	me.strTree.reloadNodeInfo(data);
                        	me.strTree.selectNode(me.strTree.getRootNode(),data);
                        }
                        
                    });
                }
        		tabArr.push(me.strBasicInfoContainer);
        	}
            me.strMainTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
            	items:tabArr,
                listeners: {
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                        if (newCard.onClick) {
                            newCard.onClick();
                        }
                        if (!newCard.addflag) {
                            me.strTreeItemClick(me.strTree);
                        }
                        if (tabPanel.activeType == 'strbasicinfo') {
                            me.strBasicInfoContainer.navToFirst();
                        }
                    }
                }
            });


            me.strMainContainer = Ext.create('FHD.ux.layout.treeNavigationCardPanel.TreeNavigationTabContainer', {
                border: false,
                tabpanel: me.strMainTabPanel,
                go:function(param) {
			        me.reRightLayout(me.strMainContainer);
			        me.reloadRightStrMainPanel(param.id);
			        me.strTree.selectNode(me.strTree.getRootNode(),param.id);
                }

            });

            if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_KPI')){
            	me.strGridContainer.onClick();
            	me.strMainTabPanel.activeType = "strgrid";
        	}
        	else if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_RISK')){
        		me.riskanalysisgridContainer.onClick();
        		me.strMainTabPanel.activeType = "analysis";
        	}
            else if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_CHART')){
            	me.strChartanalysisContainer.onClick();
            	me.strMainTabPanel.activeType = "strchart";
			}
            else if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_GRAPH')){
        		me.graphAnalyseContainer.onClick();
        		me.strMainTabPanel.activeType = "graphAnalyse";
        	}

        	//历史事件
        	else if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_HISTORYRISK')){
        		me.historyEventGridContainer.onClick();
        		me.strMainTabPanel.activeType = "hisevent";
        	}
            else if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_RESPONS')){
        		me.riskprocessContainer.onClick();
        		me.strMainTabPanel.activeType = "riskresponse";
        	}
            else if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_HISTORY')){
        		me.strHistoryGridContainer.onClick();
        		me.strMainTabPanel.activeType = "strhistorygrid";
        	}
            else if($ifAnyGranted('ROLE_ALL_MONITOR_KPIMONITOR_BASIC')){
        		me.strBasicInfoContainer.showComponent();
        		me.strMainTabPanel.activeType = "strbasicinfo";
        	}
            
            me.cardpanel.add(me.strMainContainer);
        }
    	
		if(me.strTree){
			if(me.strTree.getCurrentTreeNode()){
                me.refreshRightNavigator(me.strTree.getCurrentTreeNode().data.id, "sm");
			}
        }
        me.reRightLayout(me.strMainContainer);

    },

    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        
        //自定义新的导航条
        me.navigationBarNew = Ext.create('FHD.ux.NavigationBar');

        me.createStrTreeContainer();

        me.accordionTree = Ext.create('FHD.ux.layout.AccordionTree', {
            title: '战略目标',
            iconCls: 'icon-strategy',
            width: 250,
            treeArr: [me.strTreeContainer]
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

        //设置右侧cardpanel显示border
        me.strTreeContainer.onClick();


    },
    getSmNavInfoById: function(smid) {
    	// 根据传入的目标ID 动态生成导航数组
    	var me = this;
    	var navgationData = [{
        	id: 'strategy_map',
        	name: '战略目标',
            icon: 'SM',
            type: 'strategy_map'
        }];
        if('sm_root' == smid) {
        	return navgationData;
        }
        FHD.ajax({async: false,
                  url: __ctxPath + '/kpi/kpistrategymap/findsmnavgationinfo.f',
                  params: {
                     id: smid
                  },
                  callback:function(data) {
                  	  for(i =0;i<data.length;i++) {
                  	  	navgationData.push({
                  	  		id: data[i].smId,
                  	  		name : data[i].smName,
                  	  		type: 'strategy_map',
                  	  		containerId: me.strMainContainer.id                	  		
                  	  	});
                  	  }
                  }
                  });
        return navgationData;           
    }
});