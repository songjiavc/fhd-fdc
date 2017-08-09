/**
 * 流程树
 * 
 * @author 元杰
 */
Ext.define('FHD.view.process.ProcessTree',{
	extend: 'FHD.ux.TreePanel',
    alias: 'widget.processtree',
	url : __ctxPath + '/process/processTree/findrootProcessTreeLoader.f',
    
	root: {
        text: '流程库',
        dbid: 'process_root',
        leaf: false,
        code: 'category',
        type: '',
        expanded: false
    },
	autoScroll:true,
    animate: false,
    rootVisible: true,
    collapsible: true,
    border: false,
    showLight : false,
    multiSelect: true,
    rowLines: false,
    singleExpand: false,
    checked: false,
    contextItemMenuFun: function (view, rec, node, index, e) {
        var me = this;
        var id = rec.data.id;
        var name = rec.data.text;
        var menu = Ext.create('Ext.menu.Menu', {
            margin: '0 0 10 0',
            items: []
        });
        //添加下级菜单
        var subLevel = {
            iconCls: 'icon-add',
            text : '添加下级',
            handler: function () {
            	me.currentNode = rec;
            	if(!me.currentNode.isExpanded()&&!me.currentNode.isLeaf()){
	            	me.currentNode.expand();
            	}
                me.addNextLevel(id, name);//添加下级菜单
            }
	    };
	    if($ifAllGranted('ROLE_ALL_PROCESS_ADD')){
			menu.add(subLevel);
		}
	    if(rec.data.id != 'root'){
		    //添加同级
		    var addNowLevel = {
				iconCls : 'icon-add',
				text : '添加同级',
				handler : function() {
					//流程添加当前级（选中节点的同级）
					me.addSameLevelHandler(rec);
				}
			};
			if($ifAllGranted('ROLE_ALL_PROCESS_ADD')){
				menu.add(addNowLevel);
			}
	    }
		
	    //非根节点才有删除菜单
	    if(rec.data.id != 'root'){
		    /*删除*/
			var deleteRela = {
				iconCls : 'icon-delete-icon',
				text : '删除',
				handler : function() {
			        var deleId = rec.data.id;
					Ext.MessageBox.show({
						title : FHD.locale.get('fhd.common.delete'),
						width : 260,
						msg : '您确定要删除吗？',
						buttons : Ext.MessageBox.YESNO,
						icon : Ext.MessageBox.QUESTION,
						fn : function(btn) {
							if (btn == 'yes') {
							   	FHD.ajax({
									   params : {
									   		"processID" : deleId
									   }, 
									  url : __ctxPath+ '/process/process/removeProcess.f',
									  callback : function(ret) {
										  me.refreshTree();//刷新
									  }
						 		});
							   }
							}
					});
				}
			}
			if($ifAllGranted('ROLE_ALL_PROCESS_DELETE')){
				menu.add(deleteRela);
			}
	    }
	    
	    
	    /* ‘刷新’右键菜单*/
		var refresh = {
			iconCls : 'icon-arrow-refresh',
			text :'刷新',
			handler : function() {
				 me.refreshTree();
			}
		};
	    menu.add(refresh);
	    return menu;
    },
    addNextLevel: function(id, name) {//添加下级
    	var me = this;
    	me.up('panel').processeditpanel.clearFormData();//删除
//		me.up('panel').processeditpanel.clearFormData();
    	me.up('panel').processeditpanel.parentprocessId.setValue(id);
    	me.up('panel').processeditpanel.parentprocess.setValue(name);
	},
	addSameLevelHandler: function(node){//添加同级
		var me = this;
		me.up('panel').processeditpanel.clearFormData();//删除
    	var parentNode = node.parentNode;
    	me.up('panel').processeditpanel.parentprocessId.setValue(parentNode.data.id);
    	me.up('panel').processeditpanel.parentprocess.setValue(parentNode.data.text);
	},
	onTreepanelItemClick: function(store, records){
		var me = this;
		me.up('panel').processeditpanel.clearFormData();//删除
		var parentNode = records.parentNode;
		me.up('panel').processeditpanel.reLoadData(store, records, parentNode);
	},
	refreshTree: function(){
		var me = this;
        me.getStore().load();
	},
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            
            listeners: {
                itemclick: me.onTreepanelItemClick,
                load: function (store, records) { //默认选择首节点
                    /*var rootNode = me.getRootNode();
                    if (rootNode.childNodes.length > 0) {
                        me.getSelectionModel().select(rootNode.firstChild);//默认选择首节点
                        Ext.getCmp('scorecardmainpanel').reLoadData(rootNode.firstChild);//加载首节点数据
                    }*/
                }
            },
            viewConfig: {
                listeners: {
                    itemcontextmenu: function (view, rec, node, index, e) {
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