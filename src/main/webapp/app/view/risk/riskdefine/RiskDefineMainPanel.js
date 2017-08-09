/**
 * 风险定义主面板
 * @author ZJ
 */
Ext.define('FHD.view.risk.riskdefine.RiskDefineMainPanel', {
    extend: 'FHD.ux.layout.treeTabFace.TreeTabFace',
    alias: 'widget.riskdefinemainpanel',

    node:null,		//左侧选择的树节点
    nodeId: null, //左侧选择的树节点id
    nodeType: null, //risk,org,sm,process

    initComponent: function () {
        var me = this;
        
        var treeArr = [];
        //创建风险容器
        me.createRiskTreeContainer();
	    treeArr.push(me.riskTreeContainer);

        //创建左侧折叠树
        var accordionTree = Ext.create("FHD.ux.layout.AccordionTree", {
            title: '风险',
            iconCls: 'icon-ibm-icon-scorecards',
            width: 250,
            treeArr: treeArr
        });

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
                	var firstNodeSelected = false;
                    me.riskTree = Ext.create('FHD.view.risk.riskstorage.RiskStorageTreePanel', {
                        border: false,
                        collapsible : false,
                        rbs: true,
                        showLight: true,
                        typeId : me.typeId,
                        
                        reloadNavigator: function (id, name) {
                            var nid = id.split('_')[0];
                            //选中当前节点
                            me.selectTreeNode(nid, 'risk');
                            //刷新右侧容器数据
                            me.reloadContainerData(nid, 'risk');
                        },
                        showRiskAddForm: function(node,id,isAdd){
                        	me.node = node;
                        	me.nodeId = node.data.id;
							me.nodeType = 'risk';
                        	me.storageRiskContainer.showRiskAddForm(id,isAdd);
                        },
                        riskDeleteCallback: function(parentNode){
                        	me.node = parentNode;
                        	me.nodeId = parentNode.data.id;
							me.nodeType = 'risk';
							me.reloadContainerData(me.nodeId, 'risk');
                        },
                        /**
                         * 树节点被单击处理函数
                         */
                        nodeClick: function (record) {
                        	var id = record.data.id;
                            //风险节点不可查看基本信息
                            if (id == 'root') {
                                return;
                            } else {
                            	//保存当前节点
						        me.nodeId = id;
						        me.node = record;
						        me.nodeType = 'risk';
                                //刷新右侧容器数据
                            	me.reloadContainerData(id, 'risk');
                            	/**
                            	 * 风险树点击事件
                            	 */
                            	if(me.typeId == 'dept'){
                            		me.down('riskstorageform').respDeptName.setReadOnly(true);
                            	}
                            }
                            
	                    },
	                    /**
	                     * 选中首节点//并激活树节点被单击处理函数
	                     */
	                    selectFirstNode:function(){
	                    	//选择默认节点
	                        var selectedNode = null;
	                        var firstNode = me.riskTree.getRootNode().firstChild;
                            if (null != firstNode) {
                            	me.riskTree.getSelectionModel().select(firstNode);//界面有等待
                                selectedNode = firstNode;
                                me.nodeId = selectedNode.data.id;
                                me.nodeType = 'risk';
                                me.node = selectedNode;
                                //标识首节点被选中
                                firstNodeSelected = true;
                            }
                            
							var startDate = new Date();
                            //渲染右侧面板
                            me.storageRiskContainer.navFunId=me.riskTree.id;
                            me.storageRiskContainer.currentId = me.nodeId;
                            me.storageRiskContainer.addCenterPanel();
                    		var endDate = new Date();
                    		//alert((endDate-startDate)/1000+'s');
                            me.storageRiskContainer.riskAddFormContainer.body.unmask();//在riskdefinecontainer加入的等待
                            me.riskTree.nodeClick(me.node);
	                    },
                        itemclick: function (tablepanel, record, item, index, e, options) {
                            me.riskTree.nodeClick(record);
                        },
                        afteritemexpand: function(node,index,item,eOpts){
                        	if(!me.node){//防止节点展开式，重新选中首节点
                        		me.riskTree.selectFirstNode();
                        	}
                        }
                    });
                    this.add(me.riskTree);
                    this.doLayout();
                }
            	if (!me.storageRiskContainer) {
                    me.storageRiskContainer = Ext.create('FHD.view.risk.riskdefine.RiskDefineContainer', {
                    	navFunId: me.riskTree.id,
                        formType : 'define',
                        typeId : me.typeId,
                        showRiskDetailCallback: function(){
                        	me.expendTreeNode(me.nodeId,'risk');
                        },
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
                                me.node = newNode;
                                me.nodeId = newNode.data.id;
                                me.nodeType = 'risk';
                        	} else {
                            	//编辑节点,需要替换节点名称
                        		var nodeData = me.node.data;
                        		nodeData.text = text;
                        		me.node.updateInfo(true, nodeData);
                        	}
                        	
                        	//左侧选中节点
                            me.riskTree.getSelectionModel().select(me.node);
                            
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
        }else {

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
            if(selectedNode != null){
		        var id = selectedNode.data.id;
		
		        me.node = selectedNode;
		        me.nodeId = id;
		        me.nodeType = type;
		        icontainer.chageNavigationBar(me.nodeId, '');
		        icontainer.reloadData(me.nodeId);
		        //改变新增按钮状态
		        if(type == 'risk'){
		        	//叶子节点才可以添加风险事件
					if(selectedNode.data.leaf){
		            	icontainer.changeAddbuttonStatus(false);
		            }else{
		            	icontainer.changeAddbuttonStatus(true);
		            }
		        }
            }
        }
    },
    //展开树节点,递归展开
    expendTreeNode: function(nodeid,type){
    	var me = this;
    	if(nodeid != 'root'){
	        var itree = me.getTreeOrContainer(type, true);
	        var rootNode = itree.getRootNode(); //得到根节点
	        var selectNode = me.findNode(rootNode, nodeid);
	        me.expendTreeChildNode(selectNode,itree,true);
    	}
    },
    //展开树所有子节点
    expendTreeChildNode: function(selectNode,itree,isChild){
    	var me = this;
		var path = selectNode.getPath('id');
    	itree.expandPath(path,'id');
    	if(isChild){
    		var childnodes = selectNode.childNodes;
	        for (var i = 0; i < childnodes.length; i++) {
	            var node = childnodes[i];
	            if (!node.data.leaf) {
		                me.expendTreeChildNode(node,itree,false); //递归调用
	            }
	        }
    	}
    }
});