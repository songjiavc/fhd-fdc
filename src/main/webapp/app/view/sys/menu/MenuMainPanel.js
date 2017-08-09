/**
 * 菜单管理
 */
Ext.define('FHD.view.sys.menu.MenuMainPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.menumainpanel',
    requires: [
       'FHD.view.sys.authority.AuthoritySelectTreePanel',
       'FHD.view.sys.menu.MenuRightPanel'
    ],
    authoritySelectTreePanel:null,
    menuRightPanel:null,
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	me.authoritySelectTreePanel = Ext.widget('authorityselecttreepanel',{
    		width:260,
			region: 'west',
			split: true,
		   	collapsible : true,
			border:true,
			extraParams:{etype:"M"},
			selectNodeCallBack:function(node){
				var nodeId=node.data.id;
				var parentNode=node.parentNode;
				var parentNodeId="";
				var parentNodeText="";
				if(parentNode){
					parentNodeId=parentNode.data.id;
					parentNodeText=parentNode.data.text;
				}
				if(me.menuRightPanel){
					me.menuRightPanel.setAuthorityId(nodeId);
					me.menuRightPanel.setParentAuthorityId(parentNodeId);
					me.menuRightPanel.reloadData();
				}else{
					//右侧面板
			    	me.menuRightPanel = Ext.widget('menurightpanel',{
			    		authorityId:nodeId,
			    		parentAuthorityId:parentNodeId,
			    		baseEditCallBack:function(id){
					    	me.authoritySelectTreePanel.setSelectNodeId(id);
					    	var options={node:me.authoritySelectTreePanel.getRootNode()};
					    	me.authoritySelectTreePanel.reloadData(options);
					    }
			    	});
			    	me.add(me.menuRightPanel);
				}
			},
			addCallBack:function(){
				
			}
    	});
    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     	        type: 'border',
     	        padding: '0 0 5	0'
     	    },
     	    items:[me.authoritySelectTreePanel]
        });
    	
        me.callParent(arguments);
    }
});