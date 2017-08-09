Ext.define('FHD.view.kpi.cmp.kpitype.KpiTypeTree', {
    extend: 'FHD.ux.TreePanel',

	requires: [
              ],
    multiSelect: true,
    rowLines: false,
    singleExpand: false,
    checked: false,
    url: __ctxPath + "/kpi/kpi/kpitypetreeloader.f", //调用后台url
    rootVisible: true,
    border: false,
    root: {
        "id": "type_root",
        "text": FHD.locale.get('fhd.kpi.kpi.form.etype'),
        "dbid": "type_root",
        "leaf": false,
        "code": "zblx",
        "type": "kpi_type",
        "expanded": true,
        'iconCls': 'icon-ibm-icon-metrictypes'
    },

    /**
     * 删除事件
     */
   deleteHandler: function (id,node) {
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
                        url: __ctxPath + '/kpi/kpi/removekpitype.f',
                        callback: function (ret) {
                            if (ret && !ret.result) {
                                Ext.MessageBox.alert(FHD.locale.get('fhd.common.prompt'), FHD.locale.get("fhd.kpi.kpi.prompt.kpitypecasdes"));
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
	/**
	 * 添加同级和下级
	 */
    levelHandler: function () {
        var me = this;
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
	 * 树节点点击事件
	 */
    onItemClick: function (tablepanel, record, item, index, e, options) {
        var me = this;
    },
    firstNodeClick:function(){
    	var me = this;
    },
    /**
     * 添加树节点
     */
    appendTreeNode: function (node) {
        var me = this;
        if (me.currentNode.isLeaf()) {
            me.currentNode.data.leaf = false;
        }
        me.currentNode.appendChild(node);
        me.currentNode.expand();
        me.getSelectionModel().select(me.currentNode.lastChild);
        me.currentNode = me.currentNode.lastChild;
    },
    /**
     * 更新树节点信息
     */
    updateTreeNode: function (nodeData) {
        var me = this;
        me.currentNode.updateInfo(true, nodeData);
    },

    /**
     * 获得树当前节点
     */
    getCurrentTreeNode: function () {
        var me = this;
        var selectedNode;
        if(!me.getSelectionModel()) {
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



    contextItemMenuFun: function (view, rec, node, index, e,menu) {
        var me = this;
        var id = rec.data.id;
        var name = rec.data.text;
        if (index == 0) {
            var subLevel = {
                iconCls: 'icon-add',
                text: FHD.locale.get('fhd.strategymap.strategymapmgr.subLevel'),
                handler: function () {
                    me.currentNode = rec;
                    if (!me.currentNode.isExpanded() && !me.currentNode.isLeaf()) {
                        me.currentNode.expand();
                    }

                    me.levelHandler(id); //添加下级菜单
                }
            };
            if($ifAnyGranted('ROLE_KPITYPE_EDIT')) {
            	 menu.add(subLevel);
            }
           
        }
        if (index != 0) {
            /*添加同级*/
            var sameLevel = {
                iconCls: 'icon-add',
                text: FHD.locale.get('fhd.strategymap.strategymapmgr.sameLevel'),
                handler: function () {
                    me.currentNode = rec.parentNode;
                    me.currentNode.expand();
                    me.levelHandler(id); //添加同级菜单
                }
            };
            if($ifAnyGranted('ROLE_KPITYPE_EDIT')) {
            	  menu.add(sameLevel);
            }
          
        }
        menu.add('-');
        if (index != 0) {
            /*删除菜单*/
            var delmenu = {
                iconCls: 'icon-delete-icon',
                text: FHD.locale.get('fhd.strategymap.strategymapmgr.delete'),
                handler: function () {
                    me.currentNode = rec.parentNode;
                    me.deleteHandler(id, rec); //删除菜单
                }
            }
            if($ifAnyGranted('ROLE_KPITYPE_DELETE')) {
            	menu.add(delmenu);
                menu.add('-');	
            }
            
        }
        /* ‘刷新’右键菜单*/
        var refresh = {
            iconCls: 'icon-arrow-refresh',
            text: FHD.locale.get('fhd.strategymap.strategymapmgr.refresh'),
            handler: function () {
                me.refreshTree(); //刷新菜单
            }
        }
        menu.add(refresh);
        return menu;
    },

    /**
     *刷新树函数
     */
    refreshTree: function (obj) {
        var me = this;
        me.getStore().load();
    },


    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            listeners: {
                itemclick: me.onItemClick,
                load: function (store, records) {
                    me.firstNodeClick();
                }

            },
            viewConfig: {
                listeners: {
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
	}
});