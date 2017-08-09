/**
 * 角色管理右面板
 * 
 */
Ext.define('FHD.view.sys.role.RoleRightPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.roleRightPanel', 
    requires: [
		'FHD.view.sys.role.RoleCardPanel'
    ],
    roleId:'',
    companyId:'',
	navigationBar:null,
	roleCardPanel:null,
    baseEditCallBack:function(){
    },
	setRoleId:function(roleId){
		var me=this;
		me.roleId=roleId;
		me.roleCardPanel.setRoleId(me.roleId);
	},
	reloadData:function(){
		var me=this;
		me.navigationBar.renderHtml('rolerightPanelNavDiv',me.roleId, '', 'role');
		me.roleCardPanel.reloadData();
	},
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	//图片面板
    	me.navigationBar = Ext.create('FHD.ux.NavigationBars',{
        	type: 'role'
        });
    	me.roleCardPanel = Ext.widget('roleCardPanel',{
    		roleId:me.roleId,
    		companyId:me.companyId,
		    baseEditCallBack:me.baseEditCallBack
    	});
        Ext.apply(me, {
        	region:'center',
        	border:false,
        	layout:{
                align: 'stretch',
                type: 'vbox'
    		},
    		items:[
    		    {
	    			xtype:'box',
	    			height:20,
	    			style : 'border-left: 1px  #99bce8 solid;',
	    			html:'<div id="rolerightPanelNavDiv"  class="navigation"></div>'
		            
    		    },me.roleCardPanel
			]
        });
    	me.on('resize',function( me, width, height, oldWidth, oldHeight, eOpts ){
    		if(Ext.getCmp('center-panel')){
    			me.roleCardPanel.setHeight(Ext.getCmp('center-panel').getHeight()-50);
    		}else{		/*王再冉添加，风险评估任务分配email入口无法得到centerPanel*/
    			me.roleCardPanel.setHeight(520);
    		}
    	});
    	me.on('render',function(me, eOpts ){
	    	me.navigationBar.renderHtml('rolerightPanelNavDiv',me.roleId, '', 'role');
    	});
        me.callParent(arguments);
    }
});