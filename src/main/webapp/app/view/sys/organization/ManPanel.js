/**
 * 机构管理主面板
 * 
 * @author 金鹏祥
 */
Ext.define('FHD.view.sys.organization.ManPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.manPanel',
    
    requires: [
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	var variable;
    	//右侧面板
    	me.rightPanel = Ext.create('FHD.view.sys.organization.RightPanel');
    	
    	//机构树
    	me.treePanel = Ext.create('FHD.view.sys.organization.TreePanel',{
    		region: 'west'
    	});
    	
    	
    	Ext.apply(me, {
            border:false,
     		layout: {
     	        type: 'border',
     	        padding: '0 0 5	0'
     	    },
     	    items:[me.treePanel, me.rightPanel]
        });
    	
        me.callParent(arguments);
        
        /*me.rightPanel.on('resize',function(p){
    		me.rightPanel.setHeight(FHD.getCenterPanelHeight() );
    	});*/
    }
});