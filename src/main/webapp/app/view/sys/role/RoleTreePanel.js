/**
 * 角色树
 */
Ext.define('FHD.view.sys.role.RoleTreePanel', {
    extend: 'FHD.ux.TreePanel',
    alias: 'widget.roleTreePanel',
    /**
     * 查询用url
     * @type String
     */
	url:__ctxPath +'/sys/auth/role/treeLoader.f',
	removeUrl:__ctxPath +'/sys/auth/role/removeById.f',
	nodeMenu:null,
	selectNode:null,
	selectNodeId:null,
   	expandable:false,
   	afterrenderCallBack:function(me){
   		
   	},
   	selectNodeCallBack:function(node){
   		
   	},
	addCallBack:function(){
   		
   	},
   	editCallBack:function(node){
   		
   	},
	/**
	 * 旧版tree无此方法，如用改用新版请删除
	 */
   	reloadData:function(){
   		var me=this;
   		me.store.load();
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
            if (rootNode.childNodes.length > 0) {
            	me.selectNode=rootNode.firstChild;
            }
    	}
		me.getSelectionModel().select(me.selectNode);
		me.setSelectNodeId(me.selectNode.data.id);
		me.selectNodeCallBack(me.selectNode);
    },
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	me.nodeMenu = new Ext.menu.Menu({
			floating:true,
			items:[{
				text : '新增', 
				iconCls : 'icon-add', 
				handler:function(){
					var node={
						id:"",
						text:"新角色*",
						leaf:true
					}
					node=me.getRootNode().appendChild(node);
					me.selectedNode(node);
					me.addCallBack();
				}
   			},{
				text:"修改",
				iconCls : 'icon-edit', 
				handler:function(){
					var selection=me.getSelectionModel().getSelection();
					if(selection.length>0){
						var node=selection[0];
						me.setSelectNodeId(node.data.Id);
						me.editCallBack(node);
					}
				}
	   		},{
				text:"删除",
				iconCls : 'icon-del',
				handler:function(){
	               	var nodeId = me.getSelectionModel().getSelection()[0].data.id;
	               	Ext.MessageBox.confirm('警告', FHD.locale.get('fhd.common.makeSureDelete'), function showResult(btn){
	               		if (btn == 'yes') {//确认删除
       						jQuery.ajax({
								type: "POST",
								url: me.removeUrl,
								data: {roleId:nodeId},
								success: function(msg){
									FHD.notification("提示", FHD.locale.get('fhd.common.operateSuccess'));
									me.reloadData();
								},
								error: function(){
									FHD.alert("操作失败！");
								}
							});
       					}
	               	});
				}
			}]
		});
    	Ext.apply(me, {
   			listeners : {
   				load: function (store, records) { //默认选择首节点
   					var selectNode=me.getStore().getById(me.selectNodeId);
                    me.selectedNode(selectNode);
                },itemclick :function(ele,record, item, index, e){
                	if(me.selectNode.data.id==""&&record!=me.selectNode){
                		Ext.MessageBox.confirm('警告', "新增角色未保存，是否要放弃？", function showResult(btn){
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
                	if(me.selectNode.data.id==""&&record!=me.selectNode){
                		Ext.MessageBox.confirm('警告', "新增角色未保存，是否要放弃？", function showResult(btn){
		               		if (btn == 'yes') {//确认删除
		                		me.selectNode.remove();
								me.selectedNode(record);
								me.nodeMenu.showAt(e.getXY());
	       					}else{
								me.selectedNode(me.selectNode);
	       					}
		               	});
                	}else if(me.selectNode.data.id!=""){
						me.selectedNode(record);
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