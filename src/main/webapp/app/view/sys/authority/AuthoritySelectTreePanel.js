Ext.define('FHD.view.sys.authority.AuthoritySelectTreePanel', {
	extend: 'FHD.view.sys.authority.AuthorityTreePanel',
	alias: 'widget.authorityselecttreepanel',
	removeUrl:__ctxPath +'/sys/auth/auth/removeById.f',
	nodeMenu:null,
	selectNode:null,
	selectNodeId:null,
	rootVisible : true,
	afterrenderCallBack:function(me){
		
	},
	selectNodeCallBack:function(node){
		
	},
	addCallBack:function(){
		
	},
	editCallBack:function(node){
		
	},
	setSelectNodeId:function(nodeId){
		var me = this;
		me.selectNodeId=nodeId;
	},
	selectedNode: function(node) {//选中树节点
		var me = this;
		var rootNode = me.getRootNode();
		me.selectNode=node;
		if(!node){
	    	me.selectNode=rootNode;
		}
		if(me.selectNode){
			me.getSelectionModel().select(me.selectNode);
			me.setSelectNodeId(me.selectNode.data.id);
		}
		me.selectNodeCallBack(me.selectNode);
	},
	initComponent: function() {
		var me = this;
		me.nodeMenu = new Ext.menu.Menu({
			floating:true
		});
		Ext.apply(me, {
			listeners : {
				load: function (store, records) { //默认选择首节点
					var selectNode=me.getStore().getById(me.selectNodeId);
	                me.selectedNode(selectNode);
	            },itemclick :function(ele,record, item, index, e){
	            	if(me.selectNode.data.id==""&&record!=me.selectNode){
	            		Ext.MessageBox.confirm('警告', "新增菜单未保存，是否要放弃？", function showResult(btn){
		               		if (btn == 'yes') {//确认删除
		                		me.selectNode.remove();
								me.selectedNode(record);
	       					}else{
								me.selectedNode(me.selectNode);
	       					}
		               	});
	            	}else{
						me.selectedNode(record);
	            	}
				},itemcontextmenu:function(menutree, record, item, index, e){ //右键菜单
					e.preventDefault();
	           		e.stopEvent();
					me.nodeMenu.removeAll(); 
					me.nodeMenu.add({
						text : '新增下级', 
						iconCls : 'icon-add', 
						handler:function(){
							var node={
								id:"",
								iconCls:"icon-menu",
								text:"新菜单*",
								leaf:true
							}
							var parentNode=me.selectNode;
							node=parentNode.appendChild(node);
							me.selectNode.data.leaf=false;
							parentNode.expand();
							me.selectedNode(node);
							me.addCallBack(node,parentNode);
							
						}
					});
					if(!record.isRoot()){
						me.nodeMenu.add({
							text : '新增同级', 
							iconCls : 'icon-add', 
							handler:function(){
								var node={
									id:"",
									iconCls:"icon-menu",
									text:"新菜单*",
									leaf:true
								}
								var parentNode=me.selectNode.parentNode;
								node=parentNode.appendChild(node);
								parentNode.expand();
								me.selectedNode(node);
								me.addCallBack(node,parentNode);
								
							}
						});
						me.nodeMenu.add({
							text:"删除",
							iconCls : 'icon-del',
							handler:function(){
								var node=me.getSelectionModel().getSelection()[0];
								var parentNode=node.parentNode;
					           	var nodeId = me.getSelectionModel().getSelection()[0].data.id;
					           	Ext.MessageBox.confirm('警告', FHD.locale.get('fhd.common.makeSureDelete'), function showResult(btn){
					           		if (btn == 'yes') {//确认删除
										jQuery.ajax({
											type: "POST",
											url: me.removeUrl,
											data: {id:nodeId},
											success: function(msg){
												FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
												var options={node:parentNode};
												me.reloadData(options);
											},
											error: function(){
												FHD.alert("操作失败！");
											}
										});
									}
					           	});
							}
						});
						
					}
					me.nodeMenu.add({
						text:"刷新",
						iconCls : 'icon-arrow-refresh-blue',
						handler:function(){
							var node=me.getSelectionModel().getSelection()[0];
							var options={node:node};
							me.reloadData(options);
						}
					});
	            	if(me.selectNode.data.id==""&&record!=me.selectNode){
	            		Ext.MessageBox.confirm('警告', "新增菜单未保存，是否要放弃？", function showResult(btn){
		               		if (btn == 'yes') {//确认删除
		                		me.selectNode.remove();
								me.selectedNode(record);
								me.nodeMenu.showAt(e.getXY());
								record.expand();
	       					}else{
								me.selectedNode(me.selectNode);
	       					}
		               	});
	            	}else if(me.selectNode.data.id!=""){
						me.selectedNode(record);
						record.expand();
						me.nodeMenu.showAt(e.getXY());
	            	}
				},afterrender:function(me,e){
					me.afterrenderCallBack(me);
				}
			}
		});
		me.callParent(arguments);
	}
});
