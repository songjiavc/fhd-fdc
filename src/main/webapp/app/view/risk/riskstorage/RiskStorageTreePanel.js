/**
 * 风险树，带右键菜单
 * 
 * @author zhengjunxiang
 */
Ext.define('FHD.view.risk.riskstorage.RiskStorageTreePanel', {
    extend: 'FHD.view.risk.cmp.RiskTreePanel',
    alias: 'widget.riskstoragetreepanel',

    //切换风险基本信息表单
    showRiskAddForm: function(node,id,isAdd){},
    
    //树节点删除风险分类后续操作
    riskDeleteCallback: function(parentNode){},
    
    //风险树初始选中第一个节点
    firstNodeClick: function(type){},
    
    //风险树点击方法
    itemclick: function(tablepanel, record, item, index, e, options){},
    
    afteritemexpand: function(node,index,item,eOpts){},
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	Ext.apply(me, {
//    		showLight : true,
   			listeners: {
//   				load: function () {//afterrender
//                    me.firstNodeClick('risk');
//                },
                itemclick: function (tablepanel, record, item, index, e, options) {
                	me.itemclick(tablepanel, record, item, index, e, options);
                },
                /**
                 * 右键监听事件
                 */
                itemcontextmenu: function (view, rec, node, index, e) {
                    e.stopEvent();
                    var menu = me.contextItemMenuFun(view, rec, node, index, e);
                    if (menu) {
                        menu.showAt(e.getPoint());
                    }
                    return false;
                },
                afteritemexpand: function(node,index,item,eOpts){
                	me.afteritemexpand(node,index,item,eOpts);
                }
            }
        });
    	
        me.callParent(arguments);
    },
    
    /**
     * 添加右键菜单
     */
    contextItemMenuFun: function (view, rec, node, index, e) {
        var me = this;
        var id = rec.data.id;
        
        var menu = Ext.create('FHD.ux.Menu', {
            margin: '0 0 10 0',
            items: []
        });

        /*添加*/
        var add = {
        	authority:'ROLE_ALL_RISK_ADD',
            iconCls: 'icon-add',
            text: "添加下级",
            handler: function () { 
                me.currentNode = rec;
                if (!me.currentNode.isExpanded() && !me.currentNode.isLeaf()) {
                    me.currentNode.expand();
                }
				me.showRiskAddForm(me.currentNode,id,true,me.typeId);
            }
        };
                
        /*添加同级*/
        var addSibling = {
        	authority:'ROLE_ALL_RISK_ADD',
            iconCls: 'icon-add',
            text: "添加同级",
            handler: function () { 
                me.currentNode = rec;
                var id = me.currentNode.parentNode.data.id;
				me.showRiskAddForm(me.currentNode.parentNode,id,true);
            }
        };
                
        /*编辑*/
        var edit = {
        	authority:'ROLE_ALL_RISK_EDIT',
            iconCls: 'icon-edit',
            text: "编辑",
            handler: function () { 
                me.currentNode = rec;
                if (!me.currentNode.isExpanded() && !me.currentNode.isLeaf()) {
                    me.currentNode.expand();
                }
                me.showRiskAddForm(me.currentNode,id,false);
            }
        };        
        
        /*删除*/
        var del = {
        	authority:'ROLE_ALL_RISK_DELETE',
            iconCls: 'icon-del',
            text: "删除",
            handler: function () {
            	var deleteCheckUrl = '/risk/risk/findRiskCanBeRemoved.f';
            	var delUrl = '/risk/risk/removeRiskById.f';
            	var selection = me.getSelectionModel().getSelection()[0];
 
            	//1.判断是否可以删除风险，如果有叶子节点和风险已经别打分，将不能进行删除
            	var canBeRemoved = false;
            	FHD.ajax({
            		async:false,
					url : __ctxPath + deleteCheckUrl + "?id=" + selection.data.id,
					callback : function(data) {
						if(data.success) {//删除成功！
							canBeRemoved = true;
						}else{
							if(data.type == 'hasChildren'){	//分类下有子风险
								Ext.MessageBox.show({
			            			title:'操作错误',
			            			msg:'该风险下有下级风险，不允许删除!'
			            		});
							}else if(data.type == 'hasRef'){	//hasRef 在其他模块被引用了
								Ext.MessageBox.show({
			            			title:'操作错误',
			            			msg:'该风险已经被使用，不允许删除!'
			            		});
							}else{//风险管理删除不判断是否是自己创建的
								canBeRemoved = true;
							}
						}
					}
				});

            	//2.开始删除
            	if(canBeRemoved){
                    Ext.MessageBox.show({
            			title : '删除',
            			width : 260,                     
            			buttons : Ext.MessageBox.YESNO,
            			icon : Ext.MessageBox.QUESTION,
            			msg : FHD.locale.get('fhd.common.makeSureDelete'),
            			fn : function(btn) {
            				if (btn == 'yes') {//确认删除
            					FHD.ajax({//ajax调用
            						url : __ctxPath + delUrl + "?ids=" + selection.data.id,
            						callback : function(data) {
            							if (data) {//删除成功！
            								 FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),
            										          FHD.locale.get('fhd.common.prompt'));
            								 var parentnode = rec.parentNode;
            								 
        									 //更改左侧节点，删除左侧树节点
            								 parentnode.removeChild(rec);
        				                     if (null != parentnode && !parentnode.hasChildNodes()) {
        				                        var nodeData = parentnode.data;
        				                        nodeData.leaf = true;
        				                        parentnode.updateInfo(true, nodeData);
        				                     }
        				                     me.getSelectionModel().select(parentnode);
        				                     //防止上级节点时根节点,编辑查询报错
        				                     if(parentnode.data.id!='root'){
        				                    	 me.riskDeleteCallback(parentnode); 
        				                     }
            							}
            						}
            					});
            				}
            			}
                    })
            	}
            }
        };        
        
//        /*启用*/
//        var start = {
//        	authority:'ROLE_ALL_RISK_ENABLE',
//            iconCls: 'icon-plan-start',
//            text: "启用",
//            handler: function () {
//            	var startUrl = "/risk/enableRisk";
//            	var selection = me.getSelectionModel().getSelection()[0];
//            	FHD.ajax({//ajax调用
//					url : __ctxPath + startUrl,
//					params:{ids:selection.data.id,isUsed:'0yn_y'},
//					callback : function(data) {
//						if (data) {
//							 me.store.load();
//							 Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '启用成功!');
//						}
//					}
//				});
//            }
//        };
//        
//        /*停用*/
//        var stop = {
//            iconCls: 'icon-plan-stop',
//            text: "停用",
//            handler: function () {
//            	var stopUrl = "/risk/enableRisk";
//            	var selection = me.getSelectionModel().getSelection()[0];
//            	FHD.ajax({//ajax调用
//					url : __ctxPath + stopUrl,
//					params:{ids:selection.data.id,isUsed:'0yn_n'},
//					callback : function(data) {
//						if (data) {
//							 me.store.load();
//							 Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '停用成功!');
//						}
//					}
//				});
//            }
//        };        
        
        /*刷新*/
        var refresh = {
            iconCls: 'icon-arrow-refresh-small',
            text: "刷新",
            handler: function () { 
            	me.store.load();
            }
        };
        
//        /*查看全部*/
//        var showAll = {
//            iconCls: 'icon-arrow-refresh-small',
//            text: "查看全部",
//            handler: function () { 
//            	me.store.proxy.extraParams.showAllUsed = true;
//            	me.store.load();
//            }
//        };
        
        /*查看全部启用*/
//        var showUsed = {
//            iconCls: 'icon-arrow-refresh-small',
//            text: "查看全部启用",
//            handler: function () { 
//            	me.store.proxy.extraParams.showAllUsed = false;
//            	me.store.load();
//            }
//        };
        
        //根节点只有添加和刷新操作
        if(id=="root"){
        	menu.add(add);
        	menu.add(refresh);
        	//menu.add(showAll);
        	//menu.add(showUsed);
        }else{
        	menu.add(add);
        	menu.add(addSibling);
        	menu.add(edit);
        	menu.add(del);
//        	menu.add(start);
//        	menu.add(stop);
        	menu.add(refresh);
//        	menu.add(showAll);
//        	menu.add(showUsed);
        }
        
        return menu;
    }
});