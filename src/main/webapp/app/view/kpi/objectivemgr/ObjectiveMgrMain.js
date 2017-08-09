/**
 * 目标管理主面板
 */
Ext.define('FHD.view.kpi.objectivemgr.ObjectiveMgrMain', {
    extend: 'FHD.ux.layout.treeNavigationCardPanel.TreeNavigationCardPanel',
    alias: 'widget.objectivemgrmain',

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
    
    initKpiTypeBasicInfo: function (paramObj) {
        var me = this;
        me.kpiTypeBasicInfoContainer.initKpiTypeBasicInfo(paramObj);
    },

    /**
     * 创建战略目标树容器
     */
    createStrTreeContainer: function () {
        var me = this;
        me.strTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '战略目标',
            treeIconCls: 'icon-strategy',
            authority:'ROLE_ALL_ENV_TARGET_TARGET',
            layout: 'fit',
            onClick: function () {
            	me.createStrTree(me, this); // 创建左侧树
                me.createStrMainContainer(me); // 创建右侧战略目标主容器


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
        }else if(treeType=='kpitype'){
        	me.kpiTypeTree.currentNode = selectedNode;
        }else if(treeType=='all_metric_kpi'){
        	me.allKpiTree.currentNode = selectedNode;
        }
        return selectedItem;
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
                smname: me.getSelectedTreeItem(me.strTree,'sm').nodeName
            };
            me.smKpiGrid.initParam(paramObj);
            //重新加载度量指标
            me.smKpiGrid.reLoadData();

        } else if (activeType == "strchart") {
            var paramObj = {
                smid: smid //目标ID
               
            };
            //初始化图表分析页面
            me.chartanalysis.initParam(paramObj);
            //重新加载图表分析页面
            me.chartanalysis.reloadData();

        } else if (activeType == "analysis") {
            me.riskanalysisgridpanel.reLoadData(smid);

        }

        //更改右侧导航
        me.navigationBarNew.renderHtml(me.id + 'DIV', me.navData);
    },
    
    reloadRightKpiTypeMainPanel: function (kpitypeid) {
        var me = this;
        var activeType = me.kpiTypeTabPanel.activeType;
        me.navData = [{
                		type:'kpiType',
                		name: '指标类型',
                		id: 'kpiTypeRoot',
                		icon: 'KPI'
                	}];
        me.navData.push({
                		type:'kpiType',
                		name: me.getSelectedTreeItem(me.kpiTypeTree,'kpitype').nodeName,
                		id: kpitypeid,
                		containerId: me.kpiTypeContainer.id
        });
        if (activeType == "kpitypebasicinfo") { //激活的tab为基本信息页签

            var paramObj = {
                kpitypeid: kpitypeid, //类型ID
                parentid: 'type_root', //父类型ID
                parentname: '指标类型', //父类型名称
                editflag: true //是否是编辑状态
            };
            me.initKpiTypeBasicInfo(paramObj);

        }  else if (activeType == "kpitypegrid"){
        	 me.kpiTypeGrid.setNavData(me.navData);
        	 var paramObj = {
                     kpitypeid: kpitypeid, //类型ID
                     parentid: 'type_root', //父类型ID
                     parentname: '指标类型', //父类型名称
                     editflag: true //是否是编辑状态
                 };
        	 me.kpiTypeGrid.initParam(paramObj);
        	 me.kpiTypeGrid.reLoadData();
        }
        //更改右侧导航
         me.navigationBarNew.renderHtml(me.id + 'DIV', me.navData);
    },
   /**
     * 更新右侧所有度量指标容器
     */
    reloadRightMyTreeMainPanel: function (treeid) {
    	
        var me = this;
        me.navData = [];
        me.navData.push({type:'allKpi',
                		name: '度量指标',
                		id: 'allKpiRoot',
                		icon: 'ALLMETRICKPI'});
            me.navData.push({
                		type:'allKpi',
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

        me.strBasicInfoContainer.addflag = false;
    },
    kpiTypeTreeFirstNodeClick: function (tree) {
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
                var kpitypeid = selectedNode.data.id;
                me.treeNodeId = kpitypeid;
        		me.treeType = "kpitype";
                //刷新右侧战略目标主容器
                me.reloadRightKpiTypeMainPanel(kpitypeid);
            }
        }
        me.kpiTypeBasicInfoContainer.addflag = false;
    },
    /**
     * 所有度量指标点击节点事件
     */
    allKpiTreeOnClick:function(tree, record, item, index, e, eOpts){
    	var me = this;
        var treeType = "all_metric_kpi";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        //所有度量指标节点ID
        var nodeid = selectedItem.nodeId;
        if(nodeid=='allkpi_root'){
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

        me.strBasicInfoContainer.addflag = false;
    },
    /**
     * 指标类型树点击事件
     */
    kpiTypeTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "kpitype";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        if(!selectedItem){
        	return ;
        }
        //指标类型ID
        var kpitypeid = selectedItem.nodeId;
        if(kpitypeid=='type_root'){
        	return;
        }
        //刷新右侧战略目标主容器
        me.reloadRightKpiTypeMainPanel(kpitypeid);
        me.reRightLayout(me.kpiTypeContainer);
        me.kpiTypeBasicInfoContainer.addflag = false;
    },
    /**
     * 度量指标树点击事件
     */
    allKpiTreeItemClick: function (tree, record, item, index, e, eOpts) {
        var me = this;
        var treeType = "mytree";
        var selectedItem = me.getSelectedTreeItem(tree, treeType);
        if(!selectedItem){
        	return ;
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
        var data =  [];
        if (type == "sm") {
        	data = me.getSmNavInfoById(id);
            me.navigationBarNew.renderHtml(me.id + 'DIV', data);
        }else if(type=="kpitype"){
        	data.push({type:'kpiType',
                		name: '指标类型',
                		id: 'kpiTypeRoot',
                		icon: 'KPI'});
            data.push({
                		type:'kpiType',
                		name: me.getSelectedTreeItem(me.kpiTypeTree,'kpitype').nodeName,
                		id: id,
                		containerId: me.kpiTypeContainer.id
        });
        	me.navigationBarNew.renderHtml(me.id + 'DIV', data);
        }else if(type=="all_metric_kpi"){
        	data.push({type:'allKpi',
                		name: '度量指标',
                		id: 'ALLMETRICKPI',
                		icon: 'KPI'});
            data.push({
                		type:'allKpi',
                		name: '所有度量指标',
                		id: id,
                		containerId: me.kpiTypeContainer.id
            });
        	me.navigationBarNew.renderHtml(me.id + 'DIV', data);
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
                    me.strBasicInfoContainer.addflag = true;
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
    createStrMainContainer: function (me) {

        //度量指标页签
        if (!me.strGridContainer) {
            me.strGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                title: '度量指标',
               // authority:'ROLE_ALL_ENV_TARGET_TARGET_KPI',
                onClick: function () {
                    if (!me.smKpiGrid) {
                        me.smKpiGrid = Ext.create('FHD.view.kpi.cmp.sm.SmKpiGrid', {
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
    						undo: function(){
    							var smid = me.getSelectedTreeItem(me.strTree,'sm').nodeId;
						        me.strMainTabPanel.activeType = 'strgrid';
						        me.reRightLayout(me.strMainContainer);
						        me.reloadRightStrMainPanel(smid);
    						},
    						reLayoutNavigationBar: function(data){
					            		me.navigationBarNew.renderHtml(me.id + 'DIV', data);
					            	}
                        });
                    }
                    this.add(me.smKpiGrid);
                    this.doLayout();
                    me.strMainTabPanel.activeType = "strgrid";
                }
            });
        }

        //基本信息容器
        if (!me.strBasicInfoContainer) {	
            me.strBasicInfoContainer = Ext.create('FHD.view.kpi.cmp.sm.SmBasicInfoContainer', {
            	//authority:'ROLE_ALL_ENV_TARGET_TARGET_BASIC',
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

        

        if (!me.strMainContainer) {
			var tabArr = new Array();
			//if($ifAnyGranted('ROLE_ALL_ENV_TARGET_TARGET_KPI')){
				tabArr.push(me.strGridContainer);
			//}
			//if($ifAnyGranted('ROLE_ALL_ENV_TARGET_TARGET_BASIC')){
				tabArr.push(me.strBasicInfoContainer);
			//}
            me.strMainTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: tabArr,
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
//			if($ifAnyGranted('ROLE_ALL_ENV_TARGET_TARGET_KPI')){
				me.strGridContainer.onClick();
				me.strMainTabPanel.activeType = "strgrid";
//			}
//			else if($ifAnyGranted('ROLE_ALL_ENV_TARGET_TARGET_BASIC')){
//				me.strBasicInfoContainer.showComponent();
//				me.strMainTabPanel.activeType = "strbasicinfo";
//			}

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
     * 我的文件夹树容器
     */
    createKpiTypeTreeContainer: function () {
        var me = this;
        me.kpiTypeTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '指标类型',
            authority:'ROLE_ALL_ENV_TARGET_KPITYPE',
            treeIconCls: 'icon-ibm-icon-metrictypes',
            layout: 'fit',
            onClick: function () {
            	me.createKpiTypeTree(me, this);
                me.createKpiTypeContainer(me);

              
            }
        });
    },

    /**
     * 指标类型右侧主容器
     */
    createKpiTypeContainer: function (me) {
    	//度量指标页签
        if (!me.kpiTypeGridContainer) {
            me.kpiTypeGridContainer = Ext.create('Ext.container.Container', {
                layout: 'fit',
                //authority:'ROLE_ALL_ENV_TARGET_KPITYPE_KPI',
                title: '度量指标',
                onClick: function () {
                    if (!me.kpiTypeGrid) {
                        me.kpiTypeGrid = Ext.create('FHD.view.kpi.cmp.kpitype.KpiTypeGrid', {
                            autoHeight: true,
                            goback: function() {
						        var kpiid = me.getSelectedTreeItem(me.kpiTypeTree,'kpitype').nodeId;
						        me.kpiTypeTabPanel.activeType = 'kpitypegrid';
						        me.reRightLayout(me.kpiTypeContainer);
						        me.reloadRightKpiTypeMainPanel(kpiid);
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
    						undo: function(){
                                var kpiid = me.getSelectedTreeItem(me.kpiTypeTree,'kpitype').nodeId;
						        me.kpiTypeTabPanel.activeType = 'kpitypegrid';
						        me.reRightLayout(me.kpiTypeContainer);
						        me.reloadRightKpiTypeMainPanel(kpiid);
    						},
    				        reLayoutNavigationBar: function(data){
					            me.navigationBarNew.renderHtml(me.id + 'DIV', data);
					        }
                        });
                    }
                    this.add(me.kpiTypeGrid);
                    this.doLayout();
                    me.kpiTypeTabPanel.activeType = "kpitypegrid";
                }
            });
        }

        //基本信息容器
        if (!me.kpiTypeBasicInfoContainer) {	
            me.kpiTypeBasicInfoContainer = Ext.create('FHD.view.kpi.cmp.kpitype.KpiTypeInfoContainer', {
            	//authority:'ROLE_ALL_ENV_TARGET_KPITYPE_BASIC',
                appendTreeNode: function (node) {
                    me.kpiTypeTree.appendTreeNode(node);
                },
                refreshRightNavigator: function (kpitypeid) {
                    me.refreshRightNavigator(kpitypeid, "kpitype");
                },
                undo: function () {
                    if (me.kpiTypeTabPanel) {
                        me.kpiTypeTabPanel.setActiveTab(0);
                    }
                },
                setActiveType: function () {
                    me.kpiTypeTabPanel.activeType = "kpitypebasicinfo";
                },
                getCurrentTreeNode: function () {
                    return me.kpiTypeTree.getCurrentTreeNode();
                },
                updateTreeNode: function (data) {
                    me.kpiTypeTree.updateTreeNode(data);
                }
            });
        }
        
        if (!me.kpiTypeContainer) {
			var tabArr = new Array();
//			if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPITYPE_KPI')){
				tabArr.push(me.kpiTypeGridContainer);
//			}
//			if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPITYPE_BASIC')){
				tabArr.push(me.kpiTypeBasicInfoContainer);
//			}
            me.kpiTypeTabPanel = Ext.create("FHD.ux.layout.treeTabFace.TreeTabTab", {
                items: tabArr,
                listeners: {
                    tabchange: function (tabPanel, newCard, oldCard, eOpts) {
                        if (newCard.onClick) {
                            newCard.onClick();
                        }
                        if (!newCard.addflag) {
                            me.kpiTypeTreeItemClick(me.kpiTypeTree);
                        }
                        if (tabPanel.activeType == 'kpitypebasicinfo') {
                            me.kpiTypeBasicInfoContainer.navToFirst();
                        }
                    }
                }
            });

            me.kpiTypeContainer = Ext.create('FHD.ux.layout.treeNavigationCardPanel.TreeNavigationTabContainer', {
                border: false,
                tabpanel: me.kpiTypeTabPanel,
                go:function(param) {
                	 me.reRightLayout(me.kpiTypeContainer);
                	 me.reloadRightKpiTypeMainPanel(param.id);
                	 me.kpiTypeTree.selectNode(me.kpiTypeTree.getRootNode(),param.id);
                }
            });
//            if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPITYPE_KPI')){
				me.kpiTypeGridContainer.onClick();
				me.kpiTypeTabPanel.activeType = 'kpitypegrid';
//			}
//			else if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPITYPE_BASIC')){
//				me.kpiTypeBasicInfoContainer.showComponent();
//				me.kpiTypeTabPanel.activeType = 'kpitypebasicinfo';
//			}
//            

            me.cardpanel.add(me.kpiTypeContainer);
        }
        if(me.kpiTypeTree){
        	if (me.kpiTypeTree.getCurrentTreeNode()) {
        		me.refreshRightNavigator(me.kpiTypeTree.getCurrentTreeNode().data.id, "kpitype");
        	}	        
        }
        me.reRightLayout(me.kpiTypeContainer);
    },

    /**
     * 指标类型树
     */
    createKpiTypeTree: function (me, c) {
        if (!me.kpiTypeTree) {
            me.kpiTypeTree = Ext.create('FHD.view.kpi.cmp.kpitype.KpiTypeTree', {            	
            	reloadNavigator:function(id){            		    
    	                me.kpiTypeTree.selectNode(me.kpiTypeTree.getRootNode(),id);
    	                me.reloadRightKpiTypeMainPanel(id);
    	                me.reRightLayout(me.kpiTypeContainer);
                },
                treeTitle: '指标类型',
                onItemClick: function (tree, record, item, index, e, eOpts) {
                    me.kpiTypeTreeItemClick(tree, record, item, index, e, eOpts);
                },
                firstNodeClick: function (store, node, records, successful, eOpts) {
                    me.kpiTypeTreeFirstNodeClick(me.kpiTypeTree);
                },
                levelHandler:function(){
                	var addData = [{
                		type:'kpiType',
                		name: '指标类型',
                		id: 'kpiTypeRoot',
                		icon: 'KPI'
                	},{
                		type:'kpiType',
                		name: '添加指标类型',
                		id: 'newKpiType'
                	}];
                	me.kpiTypeBasicInfoContainer.addflag = true;
                    me.kpiTypeTabPanel.setActiveTab(me.kpiTypeBasicInfoContainer);
                    var paramObj = {
                        parentid: '',
                        kpitypeid: 'undefined',
                        parentname: '指标类型',
                        editflag: false
                    };
                    me.initKpiTypeBasicInfo(paramObj);
                    me.kpiTypeBasicInfoContainer.navToFirst();
                    me.navigationBarNew.renderHtml(me.id + 'DIV', addData);
                },
                deleteLast:function(node){
                	var rootnode = me.kpiTypeTree.getRootNode();
                	rootnode.removeChild(node);
                	var firstnode = rootnode.firstChild;
                	if(rootnode){
                		me.kpiTypeTree.getSelectionModel().select(firstnode);
                    	me.kpiTypeTreeItemClick(me.kpiTypeTree);
                	}
                }
            });
            c.add(me.kpiTypeTree);
            c.doLayout();
        }
    },
    
        /**
     * 所有度量指标树容器
     */
    createAllKpiContainer: function () {
        var me = this;

        me.allKpiTreeContainer = Ext.create('Ext.container.Container', {
            treeTitle: '度量指标',
            authority:'ROLE_ALL_ENV_TARGET_KPI',
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
                reloadNavigator: function (id) {
                },
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
                            undo: function () {
                                var did = me.getSelectedTreeItem(me.allKpiTree, 'allKpiTree').nodeId;
                                me.reRightLayout(me.myfolderContainer);
                                me.reloadRightMyTreeMainPanel(did);
                            },
                            reLayoutNavigationBar: function(data){
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
                go:function(param){
                	 me.reRightLayout(me.myfolderContainer);
                	 me.reloadRightMyTreeMainPanel(param.id);
                	 me.allKpiTree.selectNode(me.allKpiTree.getRootNode(),param.id);
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
                
        me.createKpiTypeTreeContainer();

        me.createStrTreeContainer();
        
        me.createAllKpiContainer();
        
        // 添加了TAB签的权限
        var treeArr = new Array();
        if($ifAnyGranted('ROLE_ALL_ENV_TARGET_TARGET')){
        	treeArr.push(me.strTreeContainer);
        }
        if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPITYPE')){
        	treeArr.push(me.kpiTypeTreeContainer);
        }
        if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPI')){
        	treeArr.push(me.allKpiTreeContainer);
        }
        
        me.accordionTree = Ext.create('FHD.ux.layout.AccordionTree', {
            title: '战略目标',
            iconCls: 'icon-strategy',
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

        if($ifAnyGranted('ROLE_ALL_ENV_TARGET_TARGET')){
//        	me.accordionTree.setTitle("战略目标");
        	me.strTreeContainer.onClick();
        }
        else if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPITYPE')){
//        	me.accordionTree.setTitle("指标类型");
        	me.kpiTypeTreeContainer.onClick();
        }
        else if($ifAnyGranted('ROLE_ALL_ENV_TARGET_KPI')){
//        	me.accordionTree.setTitle("度量指标");
        	me.allKpiTreeContainer.onClick();
        }

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