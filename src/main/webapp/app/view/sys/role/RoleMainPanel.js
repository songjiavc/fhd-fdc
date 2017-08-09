/**
 * 角色管理主面板
 * 
 * @author 翟辉
 */
Ext.define('FHD.view.sys.role.RoleMainPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.roleMainPanel',
    requires: [
		'FHD.view.sys.role.RoleTreePanel',
		'FHD.view.sys.role.RoleRightPanel'
    ],
	companyId:null,
	roleTreePanel:null,
	roleRightPanel:null,
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	/**
    	 * __user.companyId为字符串null所以加判断
    	 */
    	if(__user.companyId!="null"){
    		me.companyId=__user.companyId;
    	}
    	//角色树
    	me.roleTreePanel = Ext.widget('roleTreePanel',{
			width:260,
			region: 'west',
			split: true,
		   	collapsible : true,
			border:true,
		  	singleExpand: false,
			selectNodeCallBack:function(node){
				if(!me.roleRightPanel){
					//右侧面板
			    	me.roleRightPanel = Ext.widget('roleRightPanel',{
			    		layout:'fit',
			    		companyId:me.companyId,
			    		roleId:me.roleTreePanel.selectNodeId,
					    baseEditCallBack:function(id){
					    	me.roleTreePanel.setSelectNodeId(id);
					    	me.roleTreePanel.reloadData();
					    }
			    	});
			    	me.add(me.roleRightPanel);
				}else{
					me.roleRightPanel.setRoleId(node.data.id);
					me.roleRightPanel.reloadData();
				}
			},addCallBack:function(){
				me.roleRightPanel.roleCardPanel.roleTabPanel.setActiveTab(me.roleRightPanel.roleCardPanel.roleTabPanel.roleBasePanel);
   			},editCallBack:function(node){
				me.roleRightPanel.roleCardPanel.roleTabPanel.setActiveTab(me.roleRightPanel.roleCardPanel.roleTabPanel.roleBasePanel);
   			},afterrenderCallBack:function(){
				
   			}
    	});
    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     	        type: 'border',
     	        padding: '0 0 5	0'
     	    },
     	    items:[
     	    	me.roleTreePanel
     	    ]
        });
    	
        me.callParent(arguments);
    }
});