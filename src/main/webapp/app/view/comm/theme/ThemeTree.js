/*
 * File: app/view/CategoryTree.js
 *
 * This file was generated by Sencha Architect version 2.1.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.1.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.1.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('FHD.view.comm.theme.ThemeTree', {
    extend: 'FHD.ux.TreePanel',
    alias: 'widget.themetree',
    currentNode:null,
    root: {
        "id": "Analysis_root",
        "text": '主题分析',
        "leaf": false,
        "type": "",
        "expanded": true,
        'iconCls':'icon-view-analysis'
    },
    /**
	 * 添加同级函数
	 */	    
   sameLevelHandler: function (node) {
        var me = this;
        var themetab = Ext.getCmp('themetab');
        var themebasicform = Ext.getCmp('themebasicform');//基本信息表单
        if('addAnaly'==me.flag){	//新增
        	themetab.setActiveTab(1);
        	themebasicform.getForm().reset();
        	me.isAdd = true;
        }else{						//修改
        	themebasicform.analytreeId = node.data.id;
        	themetab.setActiveTab(1);
        	themebasicform.load();
        	me.isAdd = false;
        }
    },

    /**
     * 左键单击树节点
     */
    onTreepanelItemClick: function(tablepanel, record, item, index, e, options) {
    	var me = this;
    	var themetab = Ext.getCmp('themetab');
    	var themerecordgrid = Ext.getCmp('themerecordgrid');
    	var themebasicform = Ext.getCmp('themebasicform');//基本信息表单
    	var analysislayoutform = Ext.getCmp('analysislayoutform');
    	//themetab.setActiveTab(0);
    	me.currentNode = record;
    	if(me.getRootNode()!=record){//判断是否为根节点
    		themebasicform.analytreeId = record.data.id;//将节点id传给表单
    		themerecordgrid.treeRecordId = record.data.id;//节点id传给列表
    		analysislayoutform.treeRecordId = record.data.id;//节点id传给列表
    		themebasicform.load();
    		if(themetab.getActiveTab()==themetab.items.items[0]){
    			themerecordgrid.store.proxy.url = themerecordgrid.queryUrl;//动态赋给机构列表url
    	  		themerecordgrid.store.proxy.extraParams.themeId = me.currentNode.data.id;
    	  		themerecordgrid.store.load();
    		}
    	}else{
    		if(themetab.getActiveTab()==themetab.items.items[0]){
    			themerecordgrid.store.proxy.url = themerecordgrid.queryUrl;//动态赋给机构列表url
    	  		themerecordgrid.store.proxy.extraParams.themeId = null;
    	  		themerecordgrid.store.load();
    		}else{
    			themebasicform.getForm().reset();
    		}
    	}
    	
    },
    /**
     * 右键菜单
     */
   contextItemMenuFun: function (view, rec, node, index, e) {
        var me = this;
        me.currentNode = rec;
        var id = rec.data.id;
        var name = rec.data.text;
        var menu = Ext.create('Ext.menu.Menu', {
            margin: '0 0 10 0',
            items: []
        });
      //添加菜单
        var sameLevel = {
                iconCls: 'icon-add',
                text: FHD.locale.get('fhd.strategymap.strategymapmgr.subLevel'),
                handler: function () {
                    //me.currentNode = rec.parentNode;
                   // me.currentNode.expand();
                	me.flag = 'addAnaly';
                    me.sameLevelHandler(rec);
                    
                }
            };
            menu.add(sameLevel);
	    menu.add('-');
	    //修改菜单
	    var modifymenu = {
                iconCls: 'icon-edit',
                text: FHD.locale.get('fhd.common.modify'),
                handler: function () {
                	me.flag = 'editAnaly';
                    me.sameLevelHandler(rec);
                }
            };
            menu.add(modifymenu);
	    menu.add('-');
	    //删除菜单
        var delmenu = {
                iconCls: 'icon-delete-icon',
                text: FHD.locale.get('fhd.common.delete'),
                handler: function () {
                	//me.currentNode = rec.parentNode;
                    me.deleteHandler(id,rec);//删除菜单
                }
            }
            menu.add(delmenu);
            menu.add('-');
	    //刷新菜单
	    var refresh = {
	            iconCls: 'icon-arrow-refresh',
	            text: FHD.locale.get('fhd.strategymap.strategymapmgr.refresh'),
	            handler: function () {
	                me.refreshTree();//刷新菜单
	            }
	     }
	    menu.add(refresh);
	    return menu;
    },
	
    /**
     * 刷新函数
     */
   refreshTree: function () {
        var me = this;
        me.getStore().load();
    },
    /**
     * 删除函数
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
                        url: me.delUrl,
                        callback: function (data) {
                        	if(data){//删除成功！
    							Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'),FHD.locale.get('fhd.common.operateSuccess'));
    							me.store.load();
    						}
                        }
                    });
                }
            }
        });
    },

    initComponent: function() {
        var me = this;
        me.id = 'themetree';
        me.queryUrl = __ctxPath+'ic/theme/analysistreeloader.f';//树查询url
        me.delUrl = __ctxPath+'ic/theme/removeanalysisentrybyId.f';//删除url
       // me.navNode = {};
        Ext.apply(me, {
        	rootVisible: true,
    		split: true,
           	border:false,
           	region: 'west',
           	multiSelect: true,
           	rowLines:false,
          	singleExpand: false,
           	checked: false,
   			url: me.queryUrl,//调用后台url
            listeners: {
            	load: function (store, records) { //默认选择首节点
   					var selectedNode;
   					
   					var nodeItems = me.getSelectionModel().selected.items;
   			        if (nodeItems.length > 1) {
   			        	selectedNode = nodeItems[0];
   			            me.currentNode = selectedNode;
   			        }else{
   			        	selectedNode = me.getRootNode().firstChild;
   			        	me.currentNode = me.getRootNode().firstChild;
   			        }
   			        if(selectedNode!=null){
   			          me.getSelectionModel().select(selectedNode);//默认选择首节点
		              me.onTreepanelItemClick(store,selectedNode);
   			        }
                },
                itemclick: me.onTreepanelItemClick//左键单击
            },
            viewConfig: {
                listeners: {
                    itemcontextmenu: function (view, rec, node, index, e) {//右键
                        e.stopEvent();
                        var menu = me.contextItemMenuFun(view, rec, node, index, e);
                        if (menu) {
                            menu.showAt(e.getPoint());
                        }
                        return false;
                    }
                }
            }

        });

        me.callParent(arguments);
    }
    

});