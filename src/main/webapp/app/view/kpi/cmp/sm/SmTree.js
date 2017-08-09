Ext.define('FHD.view.kpi.cmp.sm.SmTree', {
    extend: 'FHD.ux.TreePanel',
	requires: [
              ],
    border : false,
    collapsible : false,
    multiSelect: true,
    rowLines: false,
    singleExpand: false,
    checked: false,
    url: __ctxPath + "/kpi/KpiStrategyMapTree/treeloader", //调用后台url
    rootVisible: true,
    root: {
        "id": "sm_root",
        "text": FHD.locale.get('fhd.sm.strategymaps'),
        "dbid": "sm_root",
        "leaf": false,
        "code": "sm",
        "type": "sm",
        "expanded": true,
        'iconCls':'icon-strategy'
    },
    
    /**
     * 添加树节点
     */
    appendTreeNode:function(node){
    	var me = this;
    	if (me.currentNode.isLeaf()) {
            me.currentNode.data.leaf = false;
        }
        me.currentNode.appendChild(node);
        me.currentNode.expand();
        me.getSelectionModel().select(me.currentNode.lastChild);
        me.currentNode = me.currentNode.lastChild;
    },
    selectNode: function(root,id) {
    	var me = this;
    	var navNode;
    	var childnodes = root.childNodes;//获取根节点的子节点
        for(var i=0; i < childnodes.length; i++){
           var node = childnodes[i];
           if(node.data.id == id)
           {
             navNode = node;
           }
           if(node.hasChildNodes()){
        	 me.selectNode(node,id);//递归调用
           }
        };
         me.getSelectionModel().select(navNode)
    },
    /**
     * 更新树节点信息
     */
    updateTreeNode:function(nodeData){
    	var me = this;
    	me.currentNode.updateInfo(true, nodeData);
    },
    
    /**
     * 获得树当前节点
     */
    getCurrentTreeNode:function(){
    	var me = this;
        var selectedNode;
        if(!me.getSelectionModel()){
        	return null;
        }
        var nodeItems = me.getSelectionModel().selected.items;
        if (nodeItems.length > 0) {
            selectedNode = nodeItems[0];
        }
        if (selectedNode == null) {
            var firstNode = me.getRootNode().firstChild;
            if (null != firstNode) {
                me.getSelectionModel().select(firstNode);
                selectedNode = firstNode;
            }
        }
        me.currentNode = selectedNode;
        return me.currentNode;
    },
    
    /**
     * 添加下级
     */
    smLevelHandler: function (id, name) {
        var me = this;
    },
    /**
     *刷新树函数 
     */
    refreshTree: function () {
    	var me = this;
    	me.getStore().load();

    },
     /**
     * 删除节点函数
     */
    smDeleteHandler: function (id, node) { //删除目标函数
    	var me = this;
        Ext.MessageBox.show({
            title: FHD.locale.get('fhd.common.delete'),
            width: 260,
            msg: FHD.locale.get('fhd.common.makeSureDelete'),
            buttons: Ext.MessageBox.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (btn) {
                if (btn == 'yes') {
                    FHD.ajax({
                        params: {
                            "id": id
                        },
                        url: __ctxPath + '/kpi/kpistrategymap/removestrategymap.f',
                        callback: function (ret) {
                            if (ret && !ret.result) {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), '存在下级,不能删除!');
                            } else {
	                            me.deleteLast(node);
                                FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
                            }
                        }
                    });
                }
            }
        });
    },
    enables:function(enable,id,rec){
    	FHD.ajax({
            params: {
                "strategyMapId": id,
                "enable":enable
            },
            url: __ctxPath + '/kpi/kpistrategymap/mergestrategymapenable.f',
            callback: function (ret) {
            	if(ret.success){
            		if(ret.iconCls){
                  	  var data = rec.data;
                  	  data.iconCls = ret.iconCls;
                  	  rec.updateInfo(true,data);
            		}
            		FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
            	}
            }
        });
    },
    
    focus:function(isfocus,id,rec){
    	FHD.ajax({
            params: {
                "strategyMapId": id,
                "focus":isfocus
            },
            url: __ctxPath + '/kpi/kpistrategymap/mergestrategymapfocus.f',
            callback: function (ret) {
            	if(ret.success){
            		FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
            	}
            }
        });
    },
    
    
    /**
     * 添加右键菜单
     */
    contextItemMenuFun: function (view, rec, node, index, e,menu) {
        var me = this;
        var id = rec.data.id;
        var name = rec.data.text;

        /*添加下级*/
        var subLevel = {
            iconCls: 'icon-add',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.subLevel'),
            handler: function () {
            	me.currentNode = rec;
                if (!me.currentNode.isExpanded() && !me.currentNode.isLeaf()) {
                    me.currentNode.expand();
                }
                me.smLevelHandler(id, name); 
            }
        };
        if($ifAnyGranted('ROLE_ALL_TARGET_ADD')) {
        	 menu.add(subLevel);
        }
       
        if (index != 0) {
            /*添加同级*/
            var sameLevel = {
                iconCls: 'icon-add',
                text: FHD.locale.get('fhd.strategymap.strategymapmgr.sameLevel'),
                handler: function () {
                	me.currentNode = rec.parentNode;
                    me.currentNode.expand();
                    me.smLevelHandler(me.currentNode.data.id,me.currentNode.data.text);
                }
            };
            if($ifAnyGranted('ROLE_ALL_TARGET_ADD')) {
                menu.add(sameLevel);
                menu.add('-');
            }

        }
        if (index != 0) {
            /*删除菜单*/
            var delmenu = {
                iconCls: 'icon-delete-icon',
                text: FHD.locale.get('fhd.strategymap.strategymapmgr.delete'),
                handler: function () {
                	me.currentNode = rec.parentNode;
                    me.smDeleteHandler(id, rec); //删除节点函数
                }
            }
            if($ifAnyGranted('ROLE_ALL_TARGET_DELETE')) {
                menu.add(delmenu);
                menu.add('-');
            }

        }
        /* ‘刷新’右键菜单*/
        var refresh = {
            iconCls: 'icon-arrow-refresh',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.refresh'),
            handler: function () {
            	 me.refreshTree(); 
            }
        }
        menu.add(refresh);
        if(index!=0){
        	menu.add('-');
        	//启用停用菜单
        	var enablemenu = {
                    iconCls: 'icon-plan-start',
                    text: FHD.locale.get('fhd.sys.planMan.start'),
                    handler: function () {
                    	me.enables("0yn_y", id,rec);
                    }
                };
        	if($ifAnyGranted('ROLE_ALL_TARGET_ENABLE')) {
        		 menu.add(enablemenu);
        	}
           
            var disablemenu = {
                    iconCls: 'icon-plan-stop',
                    text: FHD.locale.get('fhd.sys.planMan.stop'),
                    handler: function () {
                    	me.enables("0yn_n", id,rec);
                    }
                };
        	if($ifAnyGranted('ROLE_ALL_TARGET_ENABLE')) {
        		 menu.add(disablemenu);
                 menu.add('-');
       	     }  
           
            var focusmenu = {
                    iconCls: 'icon-kpi-heart-add',
                    text: '关注',
                    handler: function () {
                    	me.focus("0yn_y", id,rec);
                    }
                };
            if($ifAnyGranted('ROLE_ALL_TARGET_ATTENTION')) {
            	 menu.add(focusmenu);
            }
           
            var nofocusmenu = {
                    iconCls: 'icon-kpi-heart-delete',
                    text: '取消关注',
                    handler: function () {
                    	me.focus("0yn_n", id,rec);
                    }
                };
            if($ifAnyGranted('ROLE_ALL_TARGET_ATTENTION')) {
            	  menu.add(nofocusmenu);
            }
          
        }
        return menu;
    },
    
    firstNodeClick:function(){
    	var me = this;
    },
    
    onItemClick: function (tree, record, item, index, e, eOpts ) {
    	var me = tree;
    },
    
    
    initComponent: function () {
        var me = this;
        
        Ext.applyIf(me, 
        	{
        	listeners: {
                itemclick: me.onItemClick,
                load: function (store, records) {
                	me.firstNodeClick();
                }

            },
            viewConfig: {
                listeners: {
                    /**
                     * 右键监听事件
                     */
                    itemcontextmenu: function (view, rec, node, index, e) {
                        e.stopEvent();
                        if(!me.menuItems){
                        	me.menuItems = Ext.create('Ext.menu.Menu', {
                                margin: '0 0 10 0',
                                items: []
                            });
                        }else{
                        	me.menuItems.removeAll(true);
                        }
	                    me.menu = me.contextItemMenuFun(view, rec, node, index, e,me.menuItems);
                        if (me.menu) {
                            me.menu.showAt(e.getPoint());
                        }
                        return false;
                    }
                }
            }
        });

        me.callParent(arguments);
    },
    
    onDestroy:function(){
    	if(this.menu){
    		this.menu.destroy();
    	}
    	this.callParent(arguments);
	},
	reloadNodeInfo:function(id) {
		var me = this;
		me.expandAll();
        /*me.getStore().load({
          node:me.getRootNode(),
          callback:function() {
          	me.expandAll();
          }
        });*/
	}

});