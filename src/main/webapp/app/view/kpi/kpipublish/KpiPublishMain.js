/**
 * 考核指标发布主面板
 */
Ext.define('FHD.view.kpi.kpipublish.KpiPublishMain', {
    extend: 'FHD.ux.layout.treeNavigationCardPanel.TreeNavigationCardPanel',
    alias: 'widget.kpipublishmain',

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
     * 初始化记分卡基本信息页签
     */
    initScBasicInfo: function (paramObj) {
        var me = this;
        me.scBasicInfoContainer.initScBasicInfo(paramObj);
    },

    /**
     * 创建记分卡树容器
     */
    createScTreeContainer: function () {
        var me = this;
        me.scTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '记分卡',
            treeIconCls: 'icon-ibm-icon-scorecards',
            layout: 'fit',
            onClick: function () {
                me.createScTree(me, this); // 创建左侧树
                me.createScMainContainer(me); // 创建右侧主容器
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
        if (treeType == "sc") {
            me.scTree.currentNode = selectedNode;
        }
        return selectedItem;
    },
    /**
     * 更新右侧容器
     */
    reloadRightScMainPanel: function (scid) {
        var me = this;
        me.navData = [{
	    	id: 'score_card',
	    	name: '记分卡',
	        icon: 'SC',
	        type: 'score_card'
	    }];
	    if(scid != 'category_root') {
	    	        FHD.ajax({async: false,
                  url: __ctxPath + '/kpi/category/findscnavgationinfo.f',
                  params: {
                     id: scid
                  },
                  callback:function(data) {
                  	  for(i =0;i<data.length;i++) {
                  	  	me.navData.push({
                  	  		id: data[i].scId,
                  	  		name : data[i].scName,
                  	  		type: 'score_card',
                  	  		containerId: me.scMainContainer.id                	  		
                  	  	});
                  	  }
                  }
                  });
	    }


        var activeType = me.scMainTabPanel.activeType;
        if (activeType == "scbasicinfo") { //激活的tab为基本信息页签

            var paramObj = {
            	scid: scid, //目标ID
                parentid: 'category_root', //父目标ID
                parentname: '记分卡', //父目标名称
                editflag: true //是否是编辑状态
            };
            me.initScBasicInfo(paramObj);

        }else if (activeType == "analysis") {
        	me.riskanalysisgridpanel.setNavData(me.navData);
            me.riskanalysisgridpanel.reloadData(scid);
        } else if (activeType == "hisevent") {
            me.historyEventGrid.initParams('sc');
            me.historyEventGrid.reloadData(scid);
        }else if(activeType == "schistorygrid"){
        	var paramObj = {
                objectId: scid, //目标ID
                type:'sc'
            };
            me.scHistoryGrid.initParam(paramObj);
        	me.scHistoryGrid.reloadData();
        }else if (activeType == "scchart") {;
        //重新加载图表分析页面
	        var paramObj = {
                objectId: scid, //目标ID
                dataType:'sc'
            };
            me.chartanalysis.initParam(paramObj);
        	me.chartanalysis.reloadData(scid);

        }else if (activeType == "scgrid") {
        	me.scKpiGrid.setNavData(me.navData);
            var paramObj = {
                	scid: scid, //目标ID
                    parentid: 'category_root', //父目标ID
                    parentname: '记分卡', //父目标名称
                    editflag: true, //是否是编辑状态
                    scname: me.getSelectedTreeItem(me.scTree, 'sc').nodeName
                };
            me.scKpiGrid.initParam(paramObj);
            //重新加载度量指标
            me.scKpiGrid.reLoadData();      	
        }else if(activeType == "riskresponse"){
        	 me.solutioneditpanel.initParam({
                        type: 'sc',
                        selectId: scid
             });
             me.solutioneditpanel.reloadData();
        }else if (activeType =="graphAnalyse"){
        	me.graphAnalysePanel.initParam({
                 categoryId:scid
        	})
        	me.graphAnalysePanel.reloadData();
        }
        //更改右侧导航
        me.navigationBarNew.renderHtml(me.id + 'DIV', me.navData);
    },
    /**
     *
     * 记分卡树默认选中首节点事件
     */
    scTreeFirstNodeClick: function (tree) {
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
                var scid = selectedNode.data.id;
                me.treeNodeId = scid;
        		me.treeType = "sc";
                //刷新右侧战略目标主容器
                me.reloadRightScMainPanel(scid);
            }
        }
        if(me.scBasicInfoContainer){
        	me.scBasicInfoContainer.addflag = false;
        }
    },
    /**
     * 记分卡树点击事件
     */
    scTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "sc";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        if(!selectedItem){
        	return ;
        }
        //记分卡ID
        var scid = selectedItem.nodeId;
        if(scid=='category_root') {
           return ;
        }
        //刷新右侧战略目标主容器
        me.reloadRightScMainPanel(scid);
        me.reRightLayout(me.scMainContainer);
        if(me.scBasicInfoContainer){
        	me.scBasicInfoContainer.addflag = false;
        }
    },

    /**
     * 刷新右侧导航
     */
    refreshRightNavigator: function (id, type) {
        var me = this;
        if (type == "sc") {
            var refreshData = [{
                id: 'score_card',
                name: '记分卡',
                icon: 'SC',
                type: 'score_card'
            }];
            if(id != 'category_root') {
               FHD.ajax({
                async: false,
                url: __ctxPath + '/kpi/category/findscnavgationinfo.f',
                params: {
                    id: id
                },
                callback: function (data) {
                    for (i = 0; i < data.length; i++) {
                        refreshData.push({
                            id: data[i].scId,
                            name: data[i].scName,
                            type: 'score_card',
                            containerId: me.scMainContainer.id
                        });
                    }
                }
            });
            }

            me.navigationBarNew.renderHtml(me.id + 'DIV', refreshData);
        }

    },

    /**
     * 创建记分卡树
     */
    createScTree: function (me, c) {
        if (!me.scTree) {
            me.scTree = Ext.create('FHD.view.kpi.cmp.sc.ScTree', {
            	reloadNavigator:function(id){     
    	                me.scTree.selectNode(me.scTree.getRootNode(),id);
    	                me.reloadRightScMainPanel(id);
    	                me.reRightLayout(me.scMainContainer);
                },
                onItemClick: function (tree, record, item, index, e, eOpts) {
                    me.scTreeItemClick(tree, record, item, index, e, eOpts);
                },
                firstNodeClick: function (store, node, records, successful, eOpts) {
                    me.scTreeFirstNodeClick(me.scTree);
                },
                sameLevelHandler:function(id, name){
                    var addData = [{
                        id: 'score_card',
                        name: '记分卡',
                        icon: 'SC',
                        type: 'score_card'
                    }];
                    if(id != 'category_root') {
                    	                    FHD.ajax({
                        async: false,
                        url: __ctxPath + '/kpi/category/findscnavgationinfo.f',
                        params: {
                            id: id
                        },
                        callback: function (data) {
                            for (i = 0; i < data.length; i++) {
                                addData.push({
                                    id: data[i].scId,
                                    name: data[i].scName,
                                    type: 'score_card',
                                    containerId: me.scMainContainer.id
                                });
                            }
                        }
                    });
                    }

                    addData.push({
                        id: 'newScCard',
                        name: '添加记分卡',
                        type: 'score_card'
                    });
                	if(me.scBasicInfoContainer){
                		me.scBasicInfoContainer.addflag = true;
                	}
                    me.scMainTabPanel.setActiveTab(me.scBasicInfoContainer);
                    me.cardpanel.setActiveItem(me.scMainContainer);
                    var paramObj = {
                        parentid: id,
                        scid: 'undefined',
                        parentname: name,
                        editflag: false
                    };
                    me.initScBasicInfo(paramObj);
                    me.scBasicInfoContainer.navToFirst();
                    me.navigationBarNew.renderHtml(me.id + 'DIV', addData);
                },
                deleteLast:function(node){
                	var parentnode = node.parentNode;
                    me.scTree.currentNode.removeChild(node);
                    if (!me.scTree.currentNode.hasChildNodes() && null != me.scTree.currentNode.parentNode) {
                        var oldnode = me.scTree.currentNode;
                        var newnode = me.scTree.currentNode;
                        newnode.data.leaf = true;
                        me.scTree.currentNode.parentNode.replaceChild(newnode, oldnode);
                    }
                    me.scTree.getSelectionModel().select(parentnode);
                    me.scTreeItemClick(me.scTree);
                }
            });
            c.add(me.scTree);
            c.doLayout();
        }
    },

    /**
     * 创建右侧主面板
     */
    createScMainContainer: function (me) {
    	
    	if (!me.scMainContainer) {
        	var tabArr = new Array();
        	if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_KPI')){
        		//度量指标页签
                if (!me.scGridContainer) {
                    me.scGridContainer = Ext.create('Ext.container.Container', {
                        layout: 'fit',
                        title: '度量指标',
                        navData: me.navData,
                        authority:'ROLE_ALL_REVIEW_RELEASE_KPI',
                        onClick: function () {
                            if (!me.scKpiGrid) {
                                me.scKpiGrid = Ext.create('FHD.view.kpi.cmp.sc.ScKpiGrid', {
                                	autoHeight: true,
                                	goback: function() {                        		                        	
                              		    me.reloadRightScMainPanel(me.getSelectedTreeItem(me.scTree, 'sc').nodeId);
                                		me.reRightLayout(me.scMainContainer);
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
            						undo: function() {
            						    me.reloadRightScMainPanel(me.getSelectedTreeItem(me.scTree, 'sc').nodeId);
                                		me.reRightLayout(me.scMainContainer);	
            						},
            					    reLayoutNavigationBar: function(data){
					            		me.navigationBarNew.renderHtml(me.id + 'DIV', data);
					            	}
                                });
                            }
                            this.add(me.scKpiGrid);
                            this.doLayout();
                            me.scMainTabPanel.activeType = "scgrid";
                        }
                    });
                }
				tabArr.push(me.scGridContainer);
			}
			if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_RISK')){
        		if (!me.riskanalysisgridContainer) {
                    me.riskanalysisgridContainer = Ext.create('Ext.container.Container', {
        				title : '风险分析',
        				layout : 'fit',
        				authority:'ROLE_ALL_REVIEW_RELEASE_RISK',
        				border : false,
        				onClick : function() {
        					if (!me.riskanalysisgridpanel) {
        						me.riskanalysisgridpanel = Ext.create('FHD.view.risk.cmp.risk.RiskAnalysisGridPanel',{
        							type : 'sc',
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
        								me.reRightLayout(me.scMainContainer);
        							},
        							reLayoutNavigationBar: function(data){
					            		me.navigationBarNew.renderHtml(me.id + 'DIV', data);
					            	}
        						});
        						this.add(me.riskanalysisgridpanel);
        						this.doLayout();
        					}
        					me.scMainTabPanel.activeType = "analysis";
        				}
        			});
                }
        		tabArr.push(me.riskanalysisgridContainer);
        	}
        	if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_CHART')){
        		//图表分析页签
                if (!me.strChartanalysisContainer) {
        	    	me.strChartanalysisContainer = Ext.create('Ext.container.Container', {
        	            title: '图表分析',
        	            authority:'ROLE_ALL_REVIEW_RELEASE_CHART',
        	            layout:'fit',
        	            border: false,
        	            onClick: function () {
        	                if (!me.chartanalysis) {
        	                	me.chartanalysis = Ext.create('FHD.view.kpi.cmp.chart.ChartMainPanel',{
        	                		dataType : 'sc',
        	                		typeTitle : '',
        	                		parentname: '目标库',
        	                		showType : ['Trend']
        	                	});
        	                    this.add(me.chartanalysis);
        	                    this.doLayout();
        	                  
        	                }  me.scMainTabPanel.activeType = "scchart";
        	            }
        	            
        	        });
                }
        		tabArr.push(me.strChartanalysisContainer);
        	}
        	if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_GRAPH')){
        		//图形分析
            	if(!me.graphAnalyseContainer) {
                	me.graphAnalyseContainer = Ext.create('Ext.container.Container',{
                	    title:'图形分析',
                	    authority:'ROLE_ALL_REVIEW_RELEASE_GRAPH',
                	    layout: 'fit',
                        border: false,
                        onClick: function () {
                        	if(!me.graphAnalysePanel){
                        		me.graphAnalysePanel = Ext.create('FHD.view.comm.graph.GraphRelaCategoryPanel',{
                	                 title:'图形分析',
                	                 extraParams: {
                	                 	categoryId: me.treeNodeId
                	                 }
                	            })
                        	}
                        	me.graphAnalysePanel.reloadData();
                            me.scMainTabPanel.activeType = "graphAnalyse";
                            this.add(me.graphAnalysePanel);
                              this.doLayout();
                        }
                	});
                	
                }
        		tabArr.push(me.graphAnalyseContainer);
        	}
        	
        	if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_HISTORY')){
        		if(!me.scHistoryGridContainer){
            		me.scHistoryGridContainer = Ext.create('Ext.container.Container', {
            			title: '历史数据',
                        layout: 'fit',
                        authority:'ROLE_ALL_REVIEW_RELEASE_HISTORY',
                        border: false,
                        onClick: function () {
                        	if(!me.scHistoryGrid){
                        		me.scHistoryGrid = Ext.create('FHD.view.kpi.cmp.sc.ScSmHistoryGrid');
                        		this.add(me.scHistoryGrid);
                            	this.doLayout();
                        	}
                        	me.scMainTabPanel.activeType = "schistorygrid";
                        }
            		});
            	
            	}
        		tabArr.push(me.scHistoryGridContainer);
        	}
        	if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_BASIC')){
                //基本信息容器
                if (!me.scBasicInfoContainer) {
                    me.scBasicInfoContainer = Ext.create('FHD.view.kpi.cmp.sc.ScBasicInfoContainer', {
                    	authority:'ROLE_ALL_REVIEW_RELEASE_BASIC',
                        appendTreeNode: function (node) {
                            me.scTree.appendTreeNode(node);
                        },
                        refreshRightNavigator: function (scid) {
                            me.refreshRightNavigator(scid, "sc");
                        },
                        undo: function () {
                            if (me.scMainTabPanel) {
                                me.scMainTabPanel.setActiveTab(0);
                            }
                        },
                        setActiveType: function () {
                            me.scMainTabPanel.activeType = "scbasicinfo";
                        },
                        getCurrentTreeNode: function () {
                            return me.scTree.getCurrentTreeNode();
                        },
                        updateTreeNode: function (data) {
                            me.scTree.updateTreeNode(data);
                        }
                    });
                }
        		tabArr.push(me.scBasicInfoContainer);
        	}
            me.scMainTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: tabArr,
                listeners: {
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                        if (newCard.onClick) {
                            newCard.onClick();
                        }
                        if (!newCard.addflag) {
                            me.scTreeItemClick(me.scTree);
                        }
                        if (tabPanel.activeType == 'scbasicinfo') {
                            me.scBasicInfoContainer.navToFirst();
                        }
                    }
                }
            });


            me.scMainContainer = Ext.create('FHD.ux.layout.treeNavigationCardPanel.TreeNavigationTabContainer', {
                border: false,
                tabpanel: me.scMainTabPanel,
                go:function(param) {
          		    me.reloadRightScMainPanel(param.id);
            		me.reRightLayout(me.scMainContainer);
            		me.scTree.selectNode(me.scTree.getRootNode(),param.id);
                }
            });
            
            if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_KPI')){
				me.scGridContainer.onClick();
				me.scMainTabPanel.activeType = "scgrid";
			}
			else if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_RISK')){
				me.riskanalysisgridContainer.onClick();
				me.scMainTabPanel.activeType = "analysis";
			}
			else if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_CHART')){
				me.strChartanalysisContainer.onClick();
				me.scMainTabPanel.activeType = "scchart";
			}
			else if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_GRAPH')){
				me.graphAnalyseContainer.onClick();
				me.scMainTabPanel.activeType = "graphAnalyse";
			}

			else if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_HISTORYRISK')){
				me.historyEventGridContainer.onClick();
				me.scMainTabPanel.activeType = "hisevent";
				
			}
			else if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_RESPONS')){
				me.riskprocessContainer.onClick();
				me.scMainTabPanel.activeType = "riskresponse";
			}
			else if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_HISTORY')){
				me.scHistoryGridContainer.onClick();
				me.scMainTabPanel.activeType = "schistorygrid";
			}
			else if($ifAnyGranted('ROLE_ALL_REVIEW_RELEASE_BASIC')){
				me.scBasicInfoContainer.showComponent();
				me.scMainTabPanel.activeType = "scbasicinfo";
			}
            
            me.cardpanel.add(me.scMainContainer);

        }

        me.reRightLayout(me.scMainContainer);

    },

    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;
        // 创建新的导航
        me.navigationBarNew = Ext.create('FHD.ux.NavigationBar');
        me.createScTreeContainer();

        me.accordionTree = Ext.create('FHD.ux.layout.AccordionTree', {
            title: '记分卡',
            iconCls: 'icon-ibm-icon-scorecards',
            width: 250,
            treeArr: [me.scTreeContainer]
        });

        Ext.apply(me, {
            tree: me.accordionTree,
            tabs: [],
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


        me.scTreeContainer.onClick();


    }
});